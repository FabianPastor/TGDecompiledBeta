package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
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
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName;
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
import org.telegram.tgnet.TLRPC$TL_messageEntityBold;
import org.telegram.tgnet.TLRPC$TL_messageEntityCode;
import org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC$TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_messageEntityPre;
import org.telegram.tgnet.TLRPC$TL_messageEntityStrike;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageEntityUnderline;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.VideoTimelineView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupStickersActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.StickersActivity;

public class ChatActivityEnterView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StickersAlert.StickersAlertDelegate {
    /* access modifiers changed from: private */
    public AccountInstance accountInstance;
    private boolean allowGifs;
    private boolean allowShowTopView;
    private boolean allowStickers;
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
    /* access modifiers changed from: private */
    public MessageObject botButtonsMessageObject;
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
    public boolean closeAnimationInProgress;
    /* access modifiers changed from: private */
    public boolean configAnimationsEnabled;
    /* access modifiers changed from: private */
    public int currentAccount;
    private int currentEmojiIcon;
    /* access modifiers changed from: private */
    public int currentPopupContentType;
    /* access modifiers changed from: private */
    public AnimatorSet currentTopViewAnimation;
    /* access modifiers changed from: private */
    public ChatActivityEnterViewDelegate delegate;
    /* access modifiers changed from: private */
    public boolean destroyed;
    /* access modifiers changed from: private */
    public long dialog_id;
    private float distCanMove;
    /* access modifiers changed from: private */
    public AnimatorSet doneButtonAnimation;
    private FrameLayout doneButtonContainer;
    /* access modifiers changed from: private */
    public ImageView doneButtonImage;
    /* access modifiers changed from: private */
    public ContextProgressView doneButtonProgress;
    /* access modifiers changed from: private */
    public Paint dotPaint;
    private boolean editingCaption;
    /* access modifiers changed from: private */
    public MessageObject editingMessageObject;
    private int editingMessageReqId;
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
    private Runnable focusRunnable;
    private boolean forceShowSendButton;
    private boolean hasBotCommands;
    /* access modifiers changed from: private */
    public boolean hasRecordVideo;
    /* access modifiers changed from: private */
    public boolean ignoreTextChange;
    /* access modifiers changed from: private */
    public Drawable inactinveSendButtonDrawable;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    /* access modifiers changed from: private */
    public int innerTextChange;
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
    private View.AccessibilityDelegate mediaMessageButtonsDelegate = new View.AccessibilityDelegate(this) {
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.widget.ImageButton");
            accessibilityNodeInfo.setClickable(true);
            accessibilityNodeInfo.setLongClickable(true);
        }
    };
    /* access modifiers changed from: private */
    public EditTextCaption messageEditText;
    private TLRPC$WebPage messageWebPage;
    /* access modifiers changed from: private */
    public boolean messageWebPageSearch;
    /* access modifiers changed from: private */
    public Drawable micDrawable;
    /* access modifiers changed from: private */
    public Drawable micOutline;
    private boolean needShowTopView;
    /* access modifiers changed from: private */
    public ImageView notifyButton;
    /* access modifiers changed from: private */
    public Runnable onFinishInitCameraRunnable;
    /* access modifiers changed from: private */
    public Runnable openKeyboardRunnable;
    private int originalViewHeight;
    /* access modifiers changed from: private */
    public Paint paint;
    /* access modifiers changed from: private */
    public Paint paintRecordWaveBig;
    /* access modifiers changed from: private */
    public Paint paintRecordWaveTin;
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
    private MediaActionDrawable playPauseDrawable;
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
    private boolean showKeyboardOnResume;
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
    /* access modifiers changed from: private */
    public View topLineView;
    /* access modifiers changed from: private */
    public View topView;
    private boolean topViewShowed;
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

            public static void $default$openScheduledMessages(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
            }

            public static void $default$scrollToSendingMessage(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
            }
        }

        void bottomPanelTranslationYChanged(float f);

        void didPressedAttachButton();

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

        void onUpdateSlowModeButton(View view, boolean z, CharSequence charSequence);

        void onWindowSizeChanged(int i);

        void openScheduledMessages();

        void scrollToSendingMessage();
    }

    static /* synthetic */ boolean lambda$new$7(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean hasOverlappingRendering() {
        return false;
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
            ChatActivityEnterView.this.seekBarWaveform.draw(canvas);
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
            this.drawable.setClearOldFrames(true);
            updateColors();
        }

        public void updateColors() {
            int color = Theme.getColor("chat_recordedVoiceDot");
            ChatActivityEnterView.this.redDotPaint.setColor(color);
            this.drawable.setLayerColor("Cup Red.**", color);
            this.drawable.setLayerColor("Box.**", color);
            int color2 = Theme.getColor("chat_messagePanelBackground");
            this.drawable.setLayerColor("Line 1.**", color2);
            this.drawable.setLayerColor("Line 2.**", color2);
            this.drawable.setLayerColor("Line 3.**", color2);
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
            } else {
                ChatActivityEnterView.this.redDotPaint.setAlpha((int) (this.alpha * 255.0f));
            }
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
            } else {
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

    private class RecordCircle extends View {
        private float amplitude;
        private float animateAmplitudeDiff;
        private float animateToAmplitude;
        float animationSpeed = 0.35000002f;
        float animationSpeedCircle = 0.55f;
        float animationSpeedTiny = 0.55f;
        public WaveDrawable bigWaveDrawable;
        private boolean canceledByGesture;
        /* access modifiers changed from: private */
        public float circleRadius = AndroidUtilities.dpf2(41.0f);
        private float circleRadiusAmplitude = ((float) AndroidUtilities.dp(30.0f));
        private float exitTransition;
        float idleProgress;
        boolean incIdle;
        private float lastMovingX;
        private float lastMovingY;
        /* access modifiers changed from: private */
        public long lastUpdateTime;
        /* access modifiers changed from: private */
        public Interpolator linearInterpolator = new LinearInterpolator();
        private float lockAnimatedTranslation;
        Paint lockBackgroundPaint = new Paint(1);
        Paint lockOutlinePaint = new Paint(1);
        Paint lockPaint = new Paint(1);
        private Drawable lockShadowDrawable;
        private int paintAlpha;
        Path path = new Path();
        private boolean pressed;
        private float progressToSeekbarStep3;
        private float progressToSendButton;
        RectF rectF = new RectF();
        private float scale;
        private boolean sendButtonVisible;
        private boolean showTooltip;
        private long showTooltipStartTime;
        private boolean showWaves = true;
        private int slideDelta;
        private float slideToCancelLockProgress;
        private float slideToCancelProgress;
        private float snapAnimationProgress;
        /* access modifiers changed from: private */
        public float startTranslation;
        public WaveDrawable tinyWaveDrawable;
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
            WaveDrawable waveDrawable = new WaveDrawable(12, 0.03f, (float) AndroidUtilities.dp(40.0f), true);
            this.bigWaveDrawable = waveDrawable;
            waveDrawable.rotation = 30.0f;
            this.tinyWaveDrawable = new WaveDrawable(12, 0.03f, (float) AndroidUtilities.dp(35.0f), false);
            float unused6 = this.bigWaveDrawable.amplitudeWaveDif = 0.0162f;
            float unused7 = this.tinyWaveDrawable.amplitudeWaveDif = 0.021060001f;
            float unused8 = this.tinyWaveDrawable.amplitudeRadius = ((float) AndroidUtilities.dp(20.0f)) + (((float) AndroidUtilities.dp(20.0f)) * 0.55f);
            float unused9 = this.tinyWaveDrawable.maxScale = 0.120000005f;
            WaveDrawable waveDrawable2 = this.tinyWaveDrawable;
            waveDrawable2.scaleSpeed = 6.0E-4f;
            waveDrawable2.fling = 0.5f;
            this.lockOutlinePaint.setStyle(Paint.Style.STROKE);
            this.lockOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
            this.lockOutlinePaint.setStrokeWidth(AndroidUtilities.dpf2(1.7f));
            Drawable drawable = getResources().getDrawable(NUM);
            this.lockShadowDrawable = drawable;
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("key_chat_messagePanelVoiceLockShadow"), PorterDuff.Mode.MULTIPLY));
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor("chat_gifSaveHintBackground"));
            this.tooltipPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            this.tooltipBackgroundArrow = ContextCompat.getDrawable(context, NUM);
            this.tooltipMessage = LocaleController.getString("SlideUpToLock", NUM);
            float scaledTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
            this.touchSlop = scaledTouchSlop;
            this.touchSlop = scaledTouchSlop * scaledTouchSlop;
            if (Build.VERSION.SDK_INT >= 26) {
                ChatActivityEnterView.this.paintRecordWaveBig.setAntiAlias(true);
                ChatActivityEnterView.this.paintRecordWaveTin.setAntiAlias(true);
            }
            updateColors();
        }

        public void setAmplitude(double d) {
            this.bigWaveDrawable.setValue((float) (Math.min(1800.0d, d) / 1800.0d));
            this.tinyWaveDrawable.setValue((float) (Math.min(1800.0d, d) / 1800.0d));
            float min = (float) (Math.min(1800.0d, d) / 1800.0d);
            this.animateToAmplitude = min;
            this.animateAmplitudeDiff = (min - this.amplitude) / ((this.animationSpeedCircle * 500.0f) + 100.0f);
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v16, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v17, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v39, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v4, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v0, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v52, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v53, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v54, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v4, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v5, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v54, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v67, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v65, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v75, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v33, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v8, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v9, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v10, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v11, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v4, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v4, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v5, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v6, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v19, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v37, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v5, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v6, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v43, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v31, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v1, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v44, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v2, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v46, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v35, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v47, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v3, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v50, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v4, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v56, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v170, resolved type: float} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v174, resolved type: float} */
        /* JADX WARNING: type inference failed for: r8v21, types: [android.view.ViewParent] */
        /* access modifiers changed from: protected */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x02f6  */
        /* JADX WARNING: Removed duplicated region for block: B:106:0x030c  */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x030f  */
        /* JADX WARNING: Removed duplicated region for block: B:118:0x032c  */
        /* JADX WARNING: Removed duplicated region for block: B:126:0x0376  */
        /* JADX WARNING: Removed duplicated region for block: B:129:0x0391  */
        /* JADX WARNING: Removed duplicated region for block: B:145:0x04ba  */
        /* JADX WARNING: Removed duplicated region for block: B:148:0x04ce  */
        /* JADX WARNING: Removed duplicated region for block: B:153:0x0535  */
        /* JADX WARNING: Removed duplicated region for block: B:158:0x05c0  */
        /* JADX WARNING: Removed duplicated region for block: B:159:0x05c2  */
        /* JADX WARNING: Removed duplicated region for block: B:172:0x05e9  */
        /* JADX WARNING: Removed duplicated region for block: B:177:0x0607  */
        /* JADX WARNING: Removed duplicated region for block: B:184:0x077a  */
        /* JADX WARNING: Removed duplicated region for block: B:185:0x077f  */
        /* JADX WARNING: Removed duplicated region for block: B:196:0x0799  */
        /* JADX WARNING: Removed duplicated region for block: B:201:0x07ae  */
        /* JADX WARNING: Removed duplicated region for block: B:208:0x07e5  */
        /* JADX WARNING: Removed duplicated region for block: B:211:0x08d4  */
        /* JADX WARNING: Removed duplicated region for block: B:214:0x08e3  */
        /* JADX WARNING: Removed duplicated region for block: B:221:0x09bb  */
        /* JADX WARNING: Removed duplicated region for block: B:228:? A[RETURN, SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x01b7  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x01cf  */
        /* JADX WARNING: Removed duplicated region for block: B:68:0x01eb  */
        /* JADX WARNING: Removed duplicated region for block: B:71:0x0203  */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x0241  */
        /* JADX WARNING: Removed duplicated region for block: B:92:0x0298  */
        /* JADX WARNING: Removed duplicated region for block: B:95:0x02ca  */
        /* JADX WARNING: Removed duplicated region for block: B:98:0x02dc  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r36) {
            /*
                r35 = this;
                r6 = r35
                r7 = r36
                android.text.StaticLayout r0 = r6.tooltipLayout
                int r0 = r0.getLineCount()
                r8 = 1
                r9 = 0
                r10 = 0
                if (r0 <= r8) goto L_0x001f
                android.text.StaticLayout r0 = r6.tooltipLayout
                int r0 = r0.getHeight()
                android.text.StaticLayout r1 = r6.tooltipLayout
                int r1 = r1.getLineBottom(r9)
                int r0 = r0 - r1
                float r0 = (float) r0
                r11 = r0
                goto L_0x0020
            L_0x001f:
                r11 = 0
            L_0x0020:
                int r0 = r35.getMeasuredWidth()
                r1 = 1104150528(0x41d00000, float:26.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp2(r1)
                int r12 = r0 - r1
                r0 = 1126825984(0x432a0000, float:170.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                float r0 = r0 + r11
                int r13 = (int) r0
                float r0 = r6.lockAnimatedTranslation
                r1 = 1176256512(0x461CLASSNAME, float:10000.0)
                r2 = 1113849856(0x42640000, float:57.0)
                int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r1 == 0) goto L_0x0059
                float r1 = r6.startTranslation
                float r1 = r1 - r0
                int r0 = (int) r1
                int r0 = java.lang.Math.max(r9, r0)
                float r0 = (float) r0
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r1 = (float) r1
                int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r1 <= 0) goto L_0x0057
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r0 = (float) r0
            L_0x0057:
                r14 = r0
                goto L_0x005a
            L_0x0059:
                r14 = 0
            L_0x005a:
                float r0 = r6.scale
                r1 = 1048576000(0x3e800000, float:0.25)
                r3 = 1056964608(0x3var_, float:0.5)
                r15 = 1065353216(0x3var_, float:1.0)
                int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r4 > 0) goto L_0x006a
                float r0 = r0 / r3
            L_0x0067:
                r16 = r0
                goto L_0x0088
            L_0x006a:
                r4 = 1061158912(0x3var_, float:0.75)
                int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r4 > 0) goto L_0x007a
                float r0 = r0 - r3
                float r0 = r0 / r1
                r3 = 1036831949(0x3dcccccd, float:0.1)
                float r0 = r0 * r3
                float r0 = r15 - r0
                goto L_0x0067
            L_0x007a:
                r3 = 1063675494(0x3var_, float:0.9)
                r4 = 1061158912(0x3var_, float:0.75)
                float r0 = r0 - r4
                float r0 = r0 / r1
                r4 = 1036831949(0x3dcccccd, float:0.1)
                float r0 = r0 * r4
                float r0 = r0 + r3
                goto L_0x0067
            L_0x0088:
                long r3 = java.lang.System.currentTimeMillis()
                long r8 = r6.lastUpdateTime
                long r8 = r3 - r8
                float r0 = r6.animateToAmplitude
                float r3 = r6.amplitude
                int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r4 == 0) goto L_0x00b4
                float r4 = r6.animateAmplitudeDiff
                float r5 = (float) r8
                float r5 = r5 * r4
                float r3 = r3 + r5
                r6.amplitude = r3
                int r4 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
                if (r4 <= 0) goto L_0x00ab
                int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
                if (r3 <= 0) goto L_0x00b1
                r6.amplitude = r0
                goto L_0x00b1
            L_0x00ab:
                int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
                if (r3 >= 0) goto L_0x00b1
                r6.amplitude = r0
            L_0x00b1:
                r35.invalidate()
            L_0x00b4:
                boolean r0 = r6.canceledByGesture
                r17 = 1060320051(0x3var_, float:0.7)
                if (r0 == 0) goto L_0x00c8
                org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
                float r3 = r6.slideToCancelProgress
                float r3 = r15 - r3
                float r0 = r0.getInterpolation(r3)
                float r0 = r0 * r17
                goto L_0x00d1
            L_0x00c8:
                float r0 = r6.slideToCancelProgress
                r3 = 1050253722(0x3e99999a, float:0.3)
                float r0 = r0 * r3
                float r0 = r0 + r17
            L_0x00d1:
                float r3 = r6.circleRadius
                float r4 = r6.circleRadiusAmplitude
                float r5 = r6.amplitude
                float r4 = r4 * r5
                float r3 = r3 + r4
                float r3 = r3 * r16
                float r3 = r3 * r0
                r6.progressToSeekbarStep3 = r10
                float r0 = r6.transformToSeekbar
                r18 = 1098907648(0x41800000, float:16.0)
                r19 = 1053609165(0x3ecccccd, float:0.4)
                r20 = 1073741824(0x40000000, float:2.0)
                int r4 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
                if (r4 == 0) goto L_0x0152
                r4 = 1052938076(0x3eCLASSNAMEf5c, float:0.38)
                r5 = 1052602532(0x3ebd70a4, float:0.37)
                int r21 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r21 <= 0) goto L_0x00fa
                r0 = 1065353216(0x3var_, float:1.0)
                goto L_0x00fb
            L_0x00fa:
                float r0 = r0 / r4
            L_0x00fb:
                float r2 = r6.transformToSeekbar
                r22 = 1059145646(0x3var_ae, float:0.63)
                int r22 = (r2 > r22 ? 1 : (r2 == r22 ? 0 : -1))
                if (r22 <= 0) goto L_0x0107
                r2 = 1065353216(0x3var_, float:1.0)
                goto L_0x010d
            L_0x0107:
                float r2 = r2 - r4
                float r2 = r2 / r1
                float r2 = java.lang.Math.max(r10, r2)
            L_0x010d:
                float r15 = r6.transformToSeekbar
                float r15 = r15 - r4
                float r15 = r15 - r1
                float r15 = r15 / r5
                float r1 = java.lang.Math.max(r10, r15)
                r6.progressToSeekbarStep3 = r1
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r0 = r1.getInterpolation(r0)
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r1 = r1.getInterpolation(r2)
                org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r4 = r6.progressToSeekbarStep3
                float r2 = r2.getInterpolation(r4)
                r6.progressToSeekbarStep3 = r2
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
                float r2 = (float) r2
                float r2 = r2 * r0
                float r3 = r3 + r2
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.view.View r2 = r2.recordedAudioBackground
                int r2 = r2.getMeasuredHeight()
                float r2 = (float) r2
                float r2 = r2 / r20
                float r3 = r3 - r2
                r4 = 1065353216(0x3var_, float:1.0)
                float r15 = r4 - r1
                float r3 = r3 * r15
                float r3 = r3 + r2
                r15 = r0
                r23 = r1
                r5 = r3
                r1 = 1065353216(0x3var_, float:1.0)
                goto L_0x01b1
            L_0x0152:
                float r0 = r6.exitTransition
                int r1 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
                if (r1 == 0) goto L_0x01ab
                r1 = 1058642330(0x3var_a, float:0.6)
                int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r2 <= 0) goto L_0x0162
                r0 = 1065353216(0x3var_, float:1.0)
                goto L_0x0163
            L_0x0162:
                float r0 = r0 / r1
            L_0x0163:
                float r2 = r6.exitTransition
                float r2 = r2 - r1
                float r2 = r2 / r19
                float r2 = java.lang.Math.max(r10, r2)
                org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r0 = r4.getInterpolation(r0)
                org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float r2 = r4.getInterpolation(r2)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
                float r4 = (float) r4
                float r4 = r4 * r0
                float r3 = r3 + r4
                r4 = 1065353216(0x3var_, float:1.0)
                float r15 = r4 - r2
                float r3 = r3 * r15
                org.telegram.ui.Components.ChatActivityEnterView r5 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r5 = r5.configAnimationsEnabled
                if (r5 == 0) goto L_0x01a2
                float r5 = r6.exitTransition
                int r15 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
                if (r15 <= 0) goto L_0x01a2
                float r5 = r5 - r1
                float r5 = r5 / r19
                float r15 = r4 - r5
                float r1 = java.lang.Math.max(r10, r15)
                r15 = r0
                r24 = r2
                r5 = r3
                goto L_0x01a8
            L_0x01a2:
                r15 = r0
                r24 = r2
                r5 = r3
                r1 = 1065353216(0x3var_, float:1.0)
            L_0x01a8:
                r23 = 0
                goto L_0x01b3
            L_0x01ab:
                r5 = r3
                r1 = 1065353216(0x3var_, float:1.0)
                r15 = 0
                r23 = 0
            L_0x01b1:
                r24 = 0
            L_0x01b3:
                boolean r0 = r6.canceledByGesture
                if (r0 == 0) goto L_0x01c9
                float r0 = r6.slideToCancelProgress
                int r2 = (r0 > r17 ? 1 : (r0 == r17 ? 0 : -1))
                if (r2 <= 0) goto L_0x01c9
                float r0 = r0 - r17
                r2 = 1050253722(0x3e99999a, float:0.3)
                float r0 = r0 / r2
                r2 = 1065353216(0x3var_, float:1.0)
                float r0 = r2 - r0
                float r1 = r1 * r0
            L_0x01c9:
                float r0 = r6.progressToSeekbarStep3
                int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
                if (r0 <= 0) goto L_0x01eb
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r0 = r0.paint
                java.lang.String r2 = "chat_messagePanelVoiceBackground"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                java.lang.String r3 = "chat_recordedVoiceBackground"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                float r4 = r6.progressToSeekbarStep3
                int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r3, r4)
                r0.setColor(r2)
                goto L_0x01fa
            L_0x01eb:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r0 = r0.paint
                java.lang.String r2 = "chat_messagePanelVoiceBackground"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setColor(r2)
            L_0x01fa:
                r0 = 0
                boolean r2 = r35.isSendButtonVisible()
                r25 = 1125515264(0x43160000, float:150.0)
                if (r2 == 0) goto L_0x0241
                float r2 = r6.progressToSendButton
                r3 = 1065353216(0x3var_, float:1.0)
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 == 0) goto L_0x0238
                float r0 = (float) r8
                float r0 = r0 / r25
                float r2 = r2 + r0
                r6.progressToSendButton = r2
                int r0 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r0 <= 0) goto L_0x0217
                r6.progressToSendButton = r3
            L_0x0217:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r0 = r0.videoSendButton
                if (r0 == 0) goto L_0x0232
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r0 = r0.videoSendButton
                java.lang.Object r0 = r0.getTag()
                if (r0 == 0) goto L_0x0232
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r0 = r0.cameraDrawable
                goto L_0x0238
            L_0x0232:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r0 = r0.micDrawable
            L_0x0238:
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r2 = r2.sendDrawable
            L_0x023e:
                r3 = r0
                r4 = r2
                goto L_0x0263
            L_0x0241:
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r2 = r2.videoSendButton
                if (r2 == 0) goto L_0x025c
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.ImageView r2 = r2.videoSendButton
                java.lang.Object r2 = r2.getTag()
                if (r2 == 0) goto L_0x025c
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r2 = r2.cameraDrawable
                goto L_0x023e
            L_0x025c:
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r2 = r2.micDrawable
                goto L_0x023e
            L_0x0263:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Rect r0 = r0.sendRect
                int r2 = r4.getIntrinsicWidth()
                int r2 = r2 / 2
                int r2 = r12 - r2
                int r26 = r4.getIntrinsicHeight()
                int r26 = r26 / 2
                int r10 = r13 - r26
                int r26 = r4.getIntrinsicWidth()
                int r26 = r26 / 2
                r28 = r8
                int r8 = r12 + r26
                int r9 = r4.getIntrinsicHeight()
                int r9 = r9 / 2
                int r9 = r9 + r13
                r0.set(r2, r10, r8, r9)
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Rect r0 = r0.sendRect
                r4.setBounds(r0)
                if (r3 == 0) goto L_0x02b9
                int r0 = r3.getIntrinsicWidth()
                int r0 = r0 / 2
                int r0 = r12 - r0
                int r2 = r3.getIntrinsicHeight()
                int r2 = r2 / 2
                int r2 = r13 - r2
                int r8 = r3.getIntrinsicWidth()
                int r8 = r8 / 2
                int r8 = r8 + r12
                int r9 = r3.getIntrinsicHeight()
                int r9 = r9 / 2
                int r9 = r9 + r13
                r3.setBounds(r0, r2, r8, r9)
            L_0x02b9:
                r0 = 1113849856(0x42640000, float:57.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                float r0 = r14 / r0
                r2 = 1065353216(0x3var_, float:1.0)
                float r8 = r2 - r0
                boolean r0 = r6.incIdle
                if (r0 == 0) goto L_0x02dc
                float r0 = r6.idleProgress
                r9 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
                float r0 = r0 + r9
                r6.idleProgress = r0
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 <= 0) goto L_0x02ee
                r0 = 0
                r6.incIdle = r0
                r6.idleProgress = r2
                goto L_0x02ee
            L_0x02dc:
                float r0 = r6.idleProgress
                r2 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
                float r0 = r0 - r2
                r6.idleProgress = r0
                r2 = 0
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 >= 0) goto L_0x02ee
                r0 = 1
                r6.incIdle = r0
                r6.idleProgress = r2
            L_0x02ee:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r0 = r0.configAnimationsEnabled
                if (r0 == 0) goto L_0x0300
                org.telegram.ui.Components.ChatActivityEnterView$RecordCircle$WaveDrawable r0 = r6.bigWaveDrawable
                r0.tick(r5)
                org.telegram.ui.Components.ChatActivityEnterView$RecordCircle$WaveDrawable r0 = r6.tinyWaveDrawable
                r0.tick(r5)
            L_0x0300:
                long r9 = java.lang.System.currentTimeMillis()
                r6.lastUpdateTime = r9
                float r0 = r6.slideToCancelProgress
                int r2 = (r0 > r17 ? 1 : (r0 == r17 ? 0 : -1))
                if (r2 <= 0) goto L_0x030f
                r0 = 1065353216(0x3var_, float:1.0)
                goto L_0x0311
            L_0x030f:
                float r0 = r0 / r17
            L_0x0311:
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r2 = r2.configAnimationsEnabled
                if (r2 == 0) goto L_0x0376
                r2 = 1065353216(0x3var_, float:1.0)
                int r9 = (r23 > r2 ? 1 : (r23 == r2 ? 0 : -1))
                if (r9 == 0) goto L_0x0376
                int r9 = (r24 > r19 ? 1 : (r24 == r19 ? 0 : -1))
                if (r9 >= 0) goto L_0x0376
                r9 = 0
                int r10 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
                if (r10 <= 0) goto L_0x0376
                boolean r9 = r6.canceledByGesture
                if (r9 != 0) goto L_0x0376
                boolean r9 = r6.showWaves
                if (r9 == 0) goto L_0x0342
                float r9 = r6.wavesEnterAnimation
                int r10 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
                if (r10 == 0) goto L_0x0342
                r10 = 1025758986(0x3d23d70a, float:0.04)
                float r9 = r9 + r10
                r6.wavesEnterAnimation = r9
                int r9 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
                if (r9 <= 0) goto L_0x0342
                r6.wavesEnterAnimation = r2
            L_0x0342:
                org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
                float r9 = r6.wavesEnterAnimation
                float r2 = r2.getInterpolation(r9)
                org.telegram.ui.Components.ChatActivityEnterView$RecordCircle$WaveDrawable r9 = r6.bigWaveDrawable
                int r10 = r6.slideDelta
                int r10 = r10 + r12
                float r10 = (float) r10
                r21 = r3
                float r3 = (float) r13
                r26 = r8
                float r8 = r6.scale
                r22 = 1065353216(0x3var_, float:1.0)
                float r30 = r22 - r15
                float r8 = r8 * r30
                float r8 = r8 * r0
                float r8 = r8 * r2
                r9.draw(r10, r3, r8, r7)
                org.telegram.ui.Components.ChatActivityEnterView$RecordCircle$WaveDrawable r8 = r6.tinyWaveDrawable
                int r9 = r6.slideDelta
                int r9 = r9 + r12
                float r9 = (float) r9
                float r10 = r6.scale
                float r10 = r10 * r30
                float r10 = r10 * r0
                float r10 = r10 * r2
                r8.draw(r9, r3, r10, r7)
                goto L_0x037a
            L_0x0376:
                r21 = r3
                r26 = r8
            L_0x037a:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r0 = r0.paint
                int r2 = r6.paintAlpha
                float r2 = (float) r2
                float r2 = r2 * r1
                int r1 = (int) r2
                r0.setAlpha(r1)
                float r0 = r6.scale
                r1 = 1065353216(0x3var_, float:1.0)
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 != 0) goto L_0x04ba
                float r0 = r6.transformToSeekbar
                r1 = 0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 == 0) goto L_0x0468
                float r0 = r6.progressToSeekbarStep3
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x0453
                float r0 = (float) r13
                float r1 = r0 + r5
                float r0 = r0 - r5
                int r2 = r6.slideDelta
                int r3 = r12 + r2
                float r3 = (float) r3
                float r3 = r3 + r5
                int r2 = r2 + r12
                float r2 = (float) r2
                float r2 = r2 - r5
                org.telegram.ui.Components.ChatActivityEnterView r9 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.view.View r9 = r9.recordedAudioBackground
                android.view.ViewParent r10 = r9.getParent()
                android.view.View r10 = (android.view.View) r10
                r30 = 0
                r31 = 0
            L_0x03bb:
                android.view.ViewParent r8 = r35.getParent()
                if (r10 == r8) goto L_0x03d5
                int r8 = r10.getTop()
                int r30 = r30 + r8
                int r8 = r10.getLeft()
                int r31 = r31 + r8
                android.view.ViewParent r8 = r10.getParent()
                r10 = r8
                android.view.View r10 = (android.view.View) r10
                goto L_0x03bb
            L_0x03d5:
                int r8 = r9.getTop()
                int r8 = r8 + r30
                int r10 = r35.getTop()
                int r8 = r8 - r10
                int r10 = r9.getBottom()
                int r10 = r10 + r30
                int r30 = r35.getTop()
                int r10 = r10 - r30
                int r30 = r9.getRight()
                int r30 = r30 + r31
                int r32 = r35.getLeft()
                r33 = r15
                int r15 = r30 - r32
                int r30 = r9.getLeft()
                int r30 = r30 + r31
                int r31 = r35.getLeft()
                r32 = r14
                int r14 = r30 - r31
                r30 = r11
                org.telegram.ui.Components.ChatActivityEnterView r11 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r11 = r11.isInVideoMode()
                if (r11 == 0) goto L_0x0414
                r9 = 0
                goto L_0x041b
            L_0x0414:
                int r9 = r9.getMeasuredHeight()
                float r9 = (float) r9
                float r9 = r9 / r20
            L_0x041b:
                float r8 = (float) r8
                float r0 = r0 - r8
                float r11 = r6.progressToSeekbarStep3
                r22 = 1065353216(0x3var_, float:1.0)
                float r31 = r22 - r11
                float r0 = r0 * r31
                float r8 = r8 + r0
                float r0 = (float) r10
                float r1 = r1 - r0
                float r10 = r22 - r11
                float r1 = r1 * r10
                float r0 = r0 + r1
                float r1 = (float) r14
                float r2 = r2 - r1
                float r10 = r22 - r11
                float r2 = r2 * r10
                float r1 = r1 + r2
                float r2 = (float) r15
                float r3 = r3 - r2
                float r15 = r22 - r11
                float r3 = r3 * r15
                float r2 = r2 + r3
                float r3 = r5 - r9
                float r15 = r22 - r11
                float r3 = r3 * r15
                float r9 = r9 + r3
                android.graphics.RectF r3 = r6.rectF
                r3.set(r1, r8, r2, r0)
                android.graphics.RectF r0 = r6.rectF
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r1 = r1.paint
                r7.drawRoundRect(r0, r9, r9, r1)
                goto L_0x047c
            L_0x0453:
                r30 = r11
                r32 = r14
                r33 = r15
                int r0 = r6.slideDelta
                int r0 = r0 + r12
                float r0 = (float) r0
                float r1 = (float) r13
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r2 = r2.paint
                r7.drawCircle(r0, r1, r5, r2)
                goto L_0x047c
            L_0x0468:
                r30 = r11
                r32 = r14
                r33 = r15
                int r0 = r6.slideDelta
                int r0 = r0 + r12
                float r0 = (float) r0
                float r1 = (float) r13
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r2 = r2.paint
                r7.drawCircle(r0, r1, r5, r2)
            L_0x047c:
                r36.save()
                r0 = 1065353216(0x3var_, float:1.0)
                float r15 = r0 - r23
                float r1 = r0 - r24
                float r15 = r15 * r1
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Rect r0 = r0.sendRect
                int r0 = r0.centerX()
                float r0 = (float) r0
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Rect r1 = r1.sendRect
                int r1 = r1.centerY()
                float r1 = (float) r1
                r7.scale(r15, r15, r0, r1)
                float r8 = r6.progressToSendButton
                r0 = 1132396544(0x437var_, float:255.0)
                float r15 = r15 * r0
                int r9 = (int) r15
                r0 = r35
                r1 = r36
                r2 = r4
                r10 = r21
                r3 = r10
                r11 = r4
                r4 = r8
                r8 = r5
                r5 = r9
                r0.drawIcon(r1, r2, r3, r4, r5)
                r36.restore()
                goto L_0x04c4
            L_0x04ba:
                r8 = r5
                r30 = r11
                r32 = r14
                r33 = r15
                r10 = r21
                r11 = r4
            L_0x04c4:
                boolean r0 = r35.isSendButtonVisible()
                r1 = 1108344832(0x42100000, float:36.0)
                r2 = 1090519040(0x41000000, float:8.0)
                if (r0 == 0) goto L_0x0535
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r0 = (float) r0
                r3 = 1114636288(0x42700000, float:60.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r3 = r3 + r30
                r4 = 1106247680(0x41var_, float:30.0)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                r5 = 1065353216(0x3var_, float:1.0)
                float r15 = r5 - r16
                float r4 = r4 * r15
                float r3 = r3 + r4
                float r3 = r3 - r32
                r4 = 1096810496(0x41600000, float:14.0)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                float r4 = r4 * r26
                float r3 = r3 + r4
                float r4 = r0 / r20
                float r4 = r4 + r3
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
                float r5 = r4 - r5
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r5 = r5 + r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                float r4 = r4 - r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r4 = r4 + r9
                int r9 = (r26 > r19 ? 1 : (r26 == r19 ? 0 : -1))
                if (r9 <= 0) goto L_0x0513
                r9 = 1065353216(0x3var_, float:1.0)
                goto L_0x0515
            L_0x0513:
                float r9 = r26 / r19
            L_0x0515:
                r14 = 1091567616(0x41100000, float:9.0)
                r15 = 1065353216(0x3var_, float:1.0)
                float r16 = r15 - r26
                float r16 = r16 * r14
                float r14 = r6.snapAnimationProgress
                float r19 = r15 - r14
                float r16 = r16 * r19
                r19 = 1097859072(0x41700000, float:15.0)
                float r14 = r14 * r19
                float r9 = r15 - r9
                float r14 = r14 * r9
                float r16 = r16 - r14
                r9 = r5
                r14 = r16
                r5 = r4
                r4 = r3
                r3 = r26
                goto L_0x05a7
            L_0x0535:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r3 = 1096810496(0x41600000, float:14.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r3 = r3 * r26
                int r3 = (int) r3
                int r0 = r0 + r3
                float r0 = (float) r0
                r3 = 1114636288(0x42700000, float:60.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r3 = r3 + r30
                r4 = 1106247680(0x41var_, float:30.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r4
                r5 = 1065353216(0x3var_, float:1.0)
                float r15 = r5 - r16
                float r4 = r4 * r15
                int r4 = (int) r4
                float r4 = (float) r4
                float r3 = r3 + r4
                r4 = r32
                int r4 = (int) r4
                float r4 = (float) r4
                float r3 = r3 - r4
                float r4 = r6.idleProgress
                float r4 = r4 * r26
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r5 = -r5
                float r5 = (float) r5
                float r4 = r4 * r5
                float r3 = r3 + r4
                float r4 = r0 / r20
                float r4 = r4 + r3
                float r5 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
                float r5 = r4 - r5
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r5 = r5 + r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r9 = r9 * r26
                float r5 = r5 + r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                float r4 = r4 - r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r4 = r4 + r9
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r9 = r9 * r26
                float r4 = r4 + r9
                r9 = 1091567616(0x41100000, float:9.0)
                r14 = 1065353216(0x3var_, float:1.0)
                float r15 = r14 - r26
                float r16 = r15 * r9
                r9 = 0
                r6.snapAnimationProgress = r9
                r9 = r5
                r14 = r16
                r5 = r4
                r4 = r3
                r3 = 0
            L_0x05a7:
                boolean r15 = r6.showTooltip
                r16 = 1101004800(0x41a00000, float:20.0)
                r19 = 1077936128(0x40400000, float:3.0)
                r21 = 1082130432(0x40800000, float:4.0)
                if (r15 == 0) goto L_0x05c2
                long r30 = java.lang.System.currentTimeMillis()
                long r1 = r6.showTooltipStartTime
                long r30 = r30 - r1
                r1 = 200(0xc8, double:9.9E-322)
                int r34 = (r30 > r1 ? 1 : (r30 == r1 ? 0 : -1))
                if (r34 > 0) goto L_0x05c0
                goto L_0x05c2
            L_0x05c0:
                r2 = 0
                goto L_0x05c9
            L_0x05c2:
                float r1 = r6.tooltipAlpha
                r2 = 0
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x074f
            L_0x05c9:
                r1 = 1061997773(0x3f4ccccd, float:0.8)
                int r1 = (r26 > r1 ? 1 : (r26 == r1 ? 0 : -1))
                if (r1 < 0) goto L_0x05e2
                boolean r1 = r35.isSendButtonVisible()
                if (r1 != 0) goto L_0x05e2
                float r1 = r6.exitTransition
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 != 0) goto L_0x05e2
                float r1 = r6.transformToSeekbar
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x05e5
            L_0x05e2:
                r1 = 0
                r6.showTooltip = r1
            L_0x05e5:
                boolean r1 = r6.showTooltip
                if (r1 == 0) goto L_0x0607
                float r1 = r6.tooltipAlpha
                r2 = 1065353216(0x3var_, float:1.0)
                int r22 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                r30 = r3
                if (r22 == 0) goto L_0x061a
                r2 = r28
                float r2 = (float) r2
                float r2 = r2 / r25
                float r1 = r1 + r2
                r6.tooltipAlpha = r1
                r2 = 1065353216(0x3var_, float:1.0)
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 < 0) goto L_0x061a
                r6.tooltipAlpha = r2
                org.telegram.messenger.SharedConfig.increaseLockRecordAudioVideoHintShowed()
                goto L_0x061a
            L_0x0607:
                r30 = r3
                r2 = r28
                float r1 = r6.tooltipAlpha
                float r2 = (float) r2
                float r2 = r2 / r25
                float r1 = r1 - r2
                r6.tooltipAlpha = r1
                r2 = 0
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 >= 0) goto L_0x061a
                r6.tooltipAlpha = r2
            L_0x061a:
                float r1 = r6.tooltipAlpha
                r2 = 1132396544(0x437var_, float:255.0)
                float r1 = r1 * r2
                int r1 = (int) r1
                android.graphics.drawable.Drawable r2 = r6.tooltipBackground
                r2.setAlpha(r1)
                android.graphics.drawable.Drawable r2 = r6.tooltipBackgroundArrow
                r2.setAlpha(r1)
                android.text.TextPaint r2 = r6.tooltipPaint
                r2.setAlpha(r1)
                r36.save()
                android.graphics.RectF r2 = r6.rectF
                int r3 = r35.getMeasuredWidth()
                float r3 = (float) r3
                int r15 = r35.getMeasuredHeight()
                float r15 = (float) r15
                r28 = r10
                r10 = 0
                r2.set(r10, r10, r3, r15)
                int r2 = r35.getMeasuredWidth()
                float r2 = (float) r2
                float r3 = r6.tooltipWidth
                float r2 = r2 - r3
                r3 = 1110441984(0x42300000, float:44.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r2 = r2 - r3
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                r7.translate(r2, r3)
                android.graphics.drawable.Drawable r2 = r6.tooltipBackground
                r3 = 1090519040(0x41000000, float:8.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = -r10
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r20)
                int r10 = -r10
                float r15 = r6.tooltipWidth
                r25 = r11
                r18 = 1108344832(0x42100000, float:36.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)
                float r11 = (float) r11
                float r15 = r15 + r11
                int r11 = (int) r15
                android.text.StaticLayout r15 = r6.tooltipLayout
                int r15 = r15.getHeight()
                float r15 = (float) r15
                float r18 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                float r15 = r15 + r18
                int r15 = (int) r15
                r2.setBounds(r3, r10, r11, r15)
                android.graphics.drawable.Drawable r2 = r6.tooltipBackground
                r2.draw(r7)
                android.text.StaticLayout r2 = r6.tooltipLayout
                r2.draw(r7)
                r36.restore()
                r36.save()
                float r2 = (float) r12
                r3 = 1099431936(0x41880000, float:17.0)
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
                android.text.StaticLayout r10 = r6.tooltipLayout
                int r10 = r10.getHeight()
                float r10 = (float) r10
                float r10 = r10 / r20
                float r3 = r3 + r10
                float r10 = r6.idleProgress
                float r11 = org.telegram.messenger.AndroidUtilities.dpf2(r19)
                float r10 = r10 * r11
                float r3 = r3 - r10
                r7.translate(r2, r3)
                android.graphics.Path r2 = r6.path
                r2.reset()
                android.graphics.Path r2 = r6.path
                r3 = 1084227584(0x40a00000, float:5.0)
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
                float r3 = -r3
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                r2.setLastPoint(r3, r10)
                android.graphics.Path r2 = r6.path
                r3 = 0
                r2.lineTo(r3, r3)
                android.graphics.Path r2 = r6.path
                r3 = 1084227584(0x40a00000, float:5.0)
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                r2.lineTo(r3, r10)
                android.graphics.Paint r2 = new android.graphics.Paint
                r3 = 1
                r2.<init>(r3)
                r3 = -1
                r2.setColor(r3)
                r2.setAlpha(r1)
                android.graphics.Paint$Style r1 = android.graphics.Paint.Style.STROKE
                r2.setStyle(r1)
                android.graphics.Paint$Cap r1 = android.graphics.Paint.Cap.ROUND
                r2.setStrokeCap(r1)
                android.graphics.Paint$Join r1 = android.graphics.Paint.Join.ROUND
                r2.setStrokeJoin(r1)
                r1 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
                r2.setStrokeWidth(r1)
                android.graphics.Path r1 = r6.path
                r7.drawPath(r1, r2)
                r36.restore()
                r36.save()
                android.graphics.drawable.Drawable r1 = r6.tooltipBackgroundArrow
                int r2 = r1.getIntrinsicWidth()
                int r2 = r2 / 2
                int r2 = r12 - r2
                android.text.StaticLayout r3 = r6.tooltipLayout
                int r3 = r3.getHeight()
                float r3 = (float) r3
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r16)
                float r3 = r3 + r10
                int r3 = (int) r3
                android.graphics.drawable.Drawable r10 = r6.tooltipBackgroundArrow
                int r10 = r10.getIntrinsicWidth()
                int r10 = r10 / 2
                int r10 = r10 + r12
                android.text.StaticLayout r11 = r6.tooltipLayout
                int r11 = r11.getHeight()
                float r11 = (float) r11
                float r15 = org.telegram.messenger.AndroidUtilities.dpf2(r16)
                float r11 = r11 + r15
                int r11 = (int) r11
                android.graphics.drawable.Drawable r15 = r6.tooltipBackgroundArrow
                int r15 = r15.getIntrinsicHeight()
                int r11 = r11 + r15
                r1.setBounds(r2, r3, r10, r11)
                android.graphics.drawable.Drawable r1 = r6.tooltipBackgroundArrow
                r1.draw(r7)
                r36.restore()
                goto L_0x0755
            L_0x074f:
                r30 = r3
                r28 = r10
                r25 = r11
            L_0x0755:
                r36.save()
                int r1 = r35.getMeasuredWidth()
                int r2 = r35.getMeasuredHeight()
                org.telegram.ui.Components.ChatActivityEnterView r3 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.LinearLayout r3 = r3.textFieldContainer
                int r3 = r3.getMeasuredHeight()
                int r2 = r2 - r3
                r3 = 0
                r7.clipRect(r3, r3, r1, r2)
                float r1 = r6.scale
                r2 = 1065353216(0x3var_, float:1.0)
                float r15 = r2 - r1
                r3 = 0
                int r10 = (r15 > r3 ? 1 : (r15 == r3 ? 0 : -1))
                if (r10 == 0) goto L_0x077f
                float r27 = r2 - r1
                r1 = r27
                goto L_0x078e
            L_0x077f:
                int r1 = (r23 > r3 ? 1 : (r23 == r3 ? 0 : -1))
                if (r1 == 0) goto L_0x0786
                r1 = r23
                goto L_0x078e
            L_0x0786:
                int r1 = (r24 > r3 ? 1 : (r24 == r3 ? 0 : -1))
                if (r1 == 0) goto L_0x078d
                r1 = r24
                goto L_0x078e
            L_0x078d:
                r1 = 0
            L_0x078e:
                float r2 = r6.slideToCancelProgress
                int r2 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
                if (r2 < 0) goto L_0x07ae
                boolean r2 = r6.canceledByGesture
                if (r2 == 0) goto L_0x0799
                goto L_0x07ae
            L_0x0799:
                float r2 = r6.slideToCancelLockProgress
                r3 = 1065353216(0x3var_, float:1.0)
                int r10 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r10 == 0) goto L_0x07c4
                r10 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
                float r2 = r2 + r10
                r6.slideToCancelLockProgress = r2
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 <= 0) goto L_0x07c4
                r6.slideToCancelLockProgress = r3
                goto L_0x07c4
            L_0x07ae:
                r2 = 0
                r6.showTooltip = r2
                float r2 = r6.slideToCancelLockProgress
                r3 = 0
                int r10 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r10 == 0) goto L_0x07c4
                r10 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
                float r2 = r2 - r10
                r6.slideToCancelLockProgress = r2
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 >= 0) goto L_0x07c4
                r6.slideToCancelLockProgress = r3
            L_0x07c4:
                r2 = 1116733440(0x42900000, float:72.0)
                float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
                float r3 = r2 * r1
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r16)
                float r10 = r10 * r33
                r11 = 1065353216(0x3var_, float:1.0)
                float r15 = r11 - r1
                float r10 = r10 * r15
                float r3 = r3 - r10
                float r1 = r6.slideToCancelLockProgress
                float r15 = r11 - r1
                float r15 = r15 * r2
                float r3 = r3 + r15
                int r1 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
                if (r1 <= 0) goto L_0x07e5
                goto L_0x07e6
            L_0x07e5:
                r2 = r3
            L_0x07e6:
                r1 = 0
                r7.translate(r1, r2)
                float r1 = r6.scale
                float r15 = r11 - r23
                float r1 = r1 * r15
                float r15 = r11 - r24
                float r1 = r1 * r15
                float r3 = r6.slideToCancelLockProgress
                float r1 = r1 * r3
                float r3 = (float) r12
                r7.scale(r1, r1, r3, r9)
                android.graphics.RectF r1 = r6.rectF
                r10 = 1099956224(0x41900000, float:18.0)
                float r11 = org.telegram.messenger.AndroidUtilities.dpf2(r10)
                float r11 = r3 - r11
                float r15 = org.telegram.messenger.AndroidUtilities.dpf2(r10)
                float r15 = r15 + r3
                float r0 = r0 + r4
                r1.set(r11, r4, r15, r0)
                android.graphics.drawable.Drawable r0 = r6.lockShadowDrawable
                android.graphics.RectF r1 = r6.rectF
                float r1 = r1.left
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r19)
                float r1 = r1 - r4
                int r1 = (int) r1
                android.graphics.RectF r4 = r6.rectF
                float r4 = r4.top
                float r11 = org.telegram.messenger.AndroidUtilities.dpf2(r19)
                float r4 = r4 - r11
                int r4 = (int) r4
                android.graphics.RectF r11 = r6.rectF
                float r11 = r11.right
                float r15 = org.telegram.messenger.AndroidUtilities.dpf2(r19)
                float r11 = r11 + r15
                int r11 = (int) r11
                android.graphics.RectF r15 = r6.rectF
                float r15 = r15.bottom
                float r16 = org.telegram.messenger.AndroidUtilities.dpf2(r19)
                float r15 = r15 + r16
                int r15 = (int) r15
                r0.setBounds(r1, r4, r11, r15)
                android.graphics.drawable.Drawable r0 = r6.lockShadowDrawable
                r0.draw(r7)
                android.graphics.RectF r0 = r6.rectF
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r10)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r10)
                android.graphics.Paint r10 = r6.lockBackgroundPaint
                r7.drawRoundRect(r0, r1, r4, r10)
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.RectF r0 = r0.pauseRect
                android.graphics.RectF r1 = r6.rectF
                r0.set(r1)
                android.graphics.RectF r0 = r6.rectF
                r1 = 1086324736(0x40CLASSNAME, float:6.0)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
                float r4 = r3 - r4
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                r11 = 1065353216(0x3var_, float:1.0)
                float r15 = r11 - r30
                float r10 = r10 * r15
                float r4 = r4 - r10
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r10 = r10 * r15
                float r10 = r9 - r10
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r11 = r11 + r12
                float r11 = (float) r11
                float r16 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r16 = r16 * r15
                float r11 = r11 + r16
                r16 = 1094713344(0x41400000, float:12.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
                float r1 = (float) r1
                float r9 = r9 + r1
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r1 = r1 * r15
                float r9 = r9 + r1
                r0.set(r4, r10, r11, r9)
                android.graphics.RectF r0 = r6.rectF
                float r1 = r0.bottom
                float r0 = r0.centerX()
                android.graphics.RectF r4 = r6.rectF
                float r4 = r4.centerY()
                r36.save()
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                r10 = 1065353216(0x3var_, float:1.0)
                float r11 = r10 - r26
                float r9 = r9 * r11
                r10 = 0
                r7.translate(r10, r9)
                r7.rotate(r14, r0, r4)
                android.graphics.RectF r9 = r6.rectF
                float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r19)
                r16 = r8
                float r8 = org.telegram.messenger.AndroidUtilities.dpf2(r19)
                r17 = r13
                android.graphics.Paint r13 = r6.lockPaint
                r7.drawRoundRect(r9, r10, r8, r13)
                r8 = 1065353216(0x3var_, float:1.0)
                int r9 = (r30 > r8 ? 1 : (r30 == r8 ? 0 : -1))
                if (r9 == 0) goto L_0x08df
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r9 = r9 * r15
                android.graphics.Paint r10 = r6.lockBackgroundPaint
                r7.drawCircle(r0, r4, r9, r10)
            L_0x08df:
                int r0 = (r30 > r8 ? 1 : (r30 == r8 ? 0 : -1))
                if (r0 == 0) goto L_0x09ad
                android.graphics.RectF r0 = r6.rectF
                r4 = 1090519040(0x41000000, float:8.0)
                float r8 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
                r4 = 0
                r0.set(r4, r4, r8, r9)
                r36.save()
                int r0 = r35.getMeasuredWidth()
                float r0 = (float) r0
                float r2 = r2 + r1
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r1 = r1 * r11
                float r2 = r2 + r1
                r7.clipRect(r4, r4, r0, r2)
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                float r3 = r3 - r0
                r0 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r1 = r6.idleProgress
                r2 = 1065353216(0x3var_, float:1.0)
                float r1 = r2 - r1
                float r0 = r0 * r1
                float r0 = r0 * r26
                float r5 = r5 - r0
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r0 = r0 * r11
                float r5 = r5 - r0
                r0 = 1094713344(0x41400000, float:12.0)
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r0 = r0 * r30
                float r5 = r5 + r0
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r20)
                float r1 = r6.snapAnimationProgress
                float r0 = r0 * r1
                float r5 = r5 + r0
                r7.translate(r3, r5)
                r0 = 0
                int r0 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
                if (r0 <= 0) goto L_0x094d
                r0 = 1090519040(0x41000000, float:8.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r1 = (float) r1
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r2 = (float) r2
                r7.rotate(r14, r1, r2)
                goto L_0x094f
            L_0x094d:
                r0 = 1090519040(0x41000000, float:8.0)
            L_0x094f:
                float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                r0 = 1086324736(0x40CLASSNAME, float:6.0)
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r0)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                float r4 = r4 * r15
                float r4 = r4 + r0
                android.graphics.Paint r5 = r6.lockOutlinePaint
                r0 = r36
                r0.drawLine(r1, r2, r3, r4, r5)
                android.graphics.RectF r1 = r6.rectF
                r2 = 0
                r3 = -1020002304(0xffffffffCLASSNAME, float:-180.0)
                r4 = 0
                android.graphics.Paint r5 = r6.lockOutlinePaint
                r0.drawArc(r1, r2, r3, r4, r5)
                r1 = 0
                float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                r3 = 0
                float r0 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                float r5 = r6.idleProgress
                float r4 = r4 * r5
                float r4 = r4 * r26
                boolean r5 = r35.isSendButtonVisible()
                r8 = 1
                r5 = r5 ^ r8
                float r5 = (float) r5
                float r4 = r4 * r5
                float r0 = r0 + r4
                float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r21)
                float r5 = r6.snapAnimationProgress
                float r4 = r4 * r5
                float r4 = r4 * r11
                float r4 = r4 + r0
                android.graphics.Paint r5 = r6.lockOutlinePaint
                r0 = r36
                r0.drawLine(r1, r2, r3, r4, r5)
                r36.restore()
            L_0x09ad:
                r36.restore()
                r36.restore()
                float r0 = r6.scale
                r1 = 1065353216(0x3var_, float:1.0)
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 == 0) goto L_0x09ee
                int r0 = r6.slideDelta
                int r12 = r12 + r0
                float r0 = (float) r12
                r1 = r17
                float r1 = (float) r1
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r2 = r2.paint
                r3 = r16
                r7.drawCircle(r0, r1, r3, r2)
                boolean r0 = r6.canceledByGesture
                if (r0 == 0) goto L_0x09d8
                float r0 = r6.slideToCancelProgress
                r1 = 1065353216(0x3var_, float:1.0)
                float r15 = r1 - r0
                goto L_0x09dc
            L_0x09d8:
                r1 = 1065353216(0x3var_, float:1.0)
                r15 = 1065353216(0x3var_, float:1.0)
            L_0x09dc:
                float r4 = r6.progressToSendButton
                r0 = 1132396544(0x437var_, float:255.0)
                float r15 = r15 * r0
                int r5 = (int) r15
                r0 = r35
                r1 = r36
                r2 = r25
                r3 = r28
                r0.drawIcon(r1, r2, r3, r4, r5)
            L_0x09ee:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.onDraw(android.graphics.Canvas):void");
        }

        private void drawIcon(Canvas canvas, Drawable drawable, Drawable drawable2, float f, int i) {
            canvas.save();
            float f2 = 0.0f;
            canvas.translate((float) this.slideDelta, 0.0f);
            if (f != 0.0f && f != 1.0f && drawable2 != null) {
                canvas.save();
                canvas.scale(f, f, (float) drawable.getBounds().centerX(), (float) drawable.getBounds().centerY());
                float f3 = (float) i;
                drawable.setAlpha((int) (f3 * f));
                drawable.draw(canvas);
                canvas.restore();
                canvas.save();
                float f4 = 1.0f - f;
                canvas.scale(f4, f4, (float) drawable.getBounds().centerX(), (float) drawable.getBounds().centerY());
                drawable2.setAlpha((int) (f3 * f4));
                drawable2.draw(canvas);
                canvas.restore();
            } else if (this.canceledByGesture && this.slideToCancelProgress == 1.0f) {
                (ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.videoSendButton : ChatActivityEnterView.this.audioSendButton).setAlpha(1.0f);
                setVisibility(8);
                return;
            } else if (this.canceledByGesture && this.slideToCancelProgress < 1.0f) {
                Drawable access$3800 = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.cameraOutline : ChatActivityEnterView.this.micOutline;
                access$3800.setBounds(drawable.getBounds());
                float f5 = this.slideToCancelProgress;
                if (f5 >= 0.93f) {
                    f2 = ((f5 - 0.93f) / 0.07f) * 255.0f;
                }
                int i2 = (int) f2;
                access$3800.setAlpha(i2);
                access$3800.draw(canvas);
                access$3800.setAlpha(255);
                drawable.setAlpha(255 - i2);
                drawable.draw(canvas);
            } else if (!this.canceledByGesture) {
                drawable.setAlpha(i);
                drawable.draw(canvas);
            }
            canvas.restore();
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
            ChatActivityEnterView.this.paintRecordWaveBig.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
            ChatActivityEnterView.this.paintRecordWaveTin.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
            ChatActivityEnterView.this.paintRecordWaveBig.setAlpha(76);
            ChatActivityEnterView.this.paintRecordWaveTin.setAlpha(38);
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

        private class WaveDrawable {
            private float amplitude;
            /* access modifiers changed from: private */
            public float amplitudeRadius;
            /* access modifiers changed from: private */
            public float amplitudeWaveDif;
            private float animateAmplitudeDiff;
            private float animateAmplitudeSlowDiff;
            private float animateToAmplitude;
            private ValueAnimator animator;
            private final CircleBezierDrawable circleBezierDrawable;
            private boolean expandIdleRadius;
            private boolean expandScale;
            public float fling;
            private Animator flingAnimator;
            private float flingRadius;
            private final ValueAnimator.AnimatorUpdateListener flingUpdateListener = ;
            private float idleGlobalRadius = (((float) AndroidUtilities.dp(10.0f)) * 0.56f);
            private float idleRadius = 0.0f;
            private float idleRadiusK = 0.075f;
            float idleRotation;
            private boolean incRandomAdditionals;
            private boolean isBig;
            private boolean isIdle = true;
            float lastRadius;
            /* access modifiers changed from: private */
            public float maxScale;
            float radiusDiff;
            float randomAdditions = (((float) AndroidUtilities.dp(8.0f)) * 0.3f);
            float rotation;
            private float scaleDif;
            private float scaleIdleDif;
            public float scaleSpeed = 8.0E-5f;
            public float scaleSpeedIdle = 6.0000002E-5f;
            private float sineAngleMax;
            private float slowAmplitude;
            boolean wasFling;
            double waveAngle;
            float waveDif;

            public /* synthetic */ void lambda$new$0$ChatActivityEnterView$RecordCircle$WaveDrawable(ValueAnimator valueAnimator) {
                this.flingRadius = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            }

            public WaveDrawable(int i, float f, float f2, boolean z) {
                this.circleBezierDrawable = new CircleBezierDrawable(i);
                this.amplitudeRadius = f2;
                this.isBig = z;
                this.expandIdleRadius = z;
                this.radiusDiff = ((float) AndroidUtilities.dp(34.0f)) * 0.0012f;
            }

            public void setValue(float f) {
                ValueAnimator valueAnimator;
                this.animateToAmplitude = f;
                if (this.isBig) {
                    float f2 = this.amplitude;
                    if (f > f2) {
                        float f3 = RecordCircle.this.animationSpeed;
                        this.animateAmplitudeDiff = (f - f2) / ((300.0f * f3) + 100.0f);
                        this.animateAmplitudeSlowDiff = (f - this.slowAmplitude) / ((f3 * 500.0f) + 100.0f);
                    } else {
                        float f4 = RecordCircle.this.animationSpeed;
                        this.animateAmplitudeDiff = (f - f2) / ((f4 * 500.0f) + 100.0f);
                        this.animateAmplitudeSlowDiff = (f - this.slowAmplitude) / ((f4 * 500.0f) + 100.0f);
                    }
                } else {
                    float f5 = this.amplitude;
                    if (f > f5) {
                        float f6 = RecordCircle.this.animationSpeedTiny;
                        this.animateAmplitudeDiff = (f - f5) / ((400.0f * f6) + 100.0f);
                        this.animateAmplitudeSlowDiff = (f - this.slowAmplitude) / ((f6 * 500.0f) + 100.0f);
                    } else {
                        float f7 = RecordCircle.this.animationSpeedTiny;
                        this.animateAmplitudeDiff = (f - f5) / ((f7 * 500.0f) + 100.0f);
                        this.animateAmplitudeSlowDiff = (f - this.slowAmplitude) / ((f7 * 500.0f) + 100.0f);
                    }
                }
                boolean z = f < 0.1f;
                if (this.isIdle != z && z && this.isBig) {
                    float f8 = this.rotation;
                    float f9 = (float) 60;
                    float round = (float) ((Math.round(f8 / f9) * 60) + 30);
                    float var_ = RecordCircle.this.tinyWaveDrawable.rotation;
                    float var_ = this.waveDif;
                    float var_ = RecordCircle.this.tinyWaveDrawable.waveDif;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                    this.animator = ofFloat;
                    ofFloat.addUpdateListener(
                    /*  JADX ERROR: Method code generation error
                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00c3: INVOKE  
                          (r0v9 'ofFloat' android.animation.ValueAnimator)
                          (wrap: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$RecordCircle$WaveDrawable$_b_L9t3XmBBWpeIIUnLUt9qikrE : 0x00c0: CONSTRUCTOR  (r1v5 org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$RecordCircle$WaveDrawable$_b_L9t3XmBBWpeIIUnLUt9qikrE) = 
                          (r10v0 'this' org.telegram.ui.Components.ChatActivityEnterView$RecordCircle$WaveDrawable A[THIS])
                          (r3v0 'round' float)
                          (r4v0 'f8' float)
                          (wrap: float : 0x00a7: CAST  (r5v0 float) = (float) (wrap: int : 0x00a5: ARITH  (r1v4 int) = (wrap: int : 0x00a1: INVOKE  (r1v3 int) = 
                          (wrap: float : 0x009f: ARITH  (r1v2 float) = (r6v0 'var_' float) / (r1v1 'f9' float))
                         java.lang.Math.round(float):int type: STATIC) * (60 int)))
                          (r6v0 'var_' float)
                          (r7v0 'var_' float)
                          (r8v0 'var_' float)
                         call: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$RecordCircle$WaveDrawable$_b_L9t3XmBBWpeIIUnLUt9qikrE.<init>(org.telegram.ui.Components.ChatActivityEnterView$RecordCircle$WaveDrawable, float, float, float, float, float, float):void type: CONSTRUCTOR)
                         android.animation.ValueAnimator.addUpdateListener(android.animation.ValueAnimator$AnimatorUpdateListener):void type: VIRTUAL in method: org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.WaveDrawable.setValue(float):void, dex: classes.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00c0: CONSTRUCTOR  (r1v5 org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$RecordCircle$WaveDrawable$_b_L9t3XmBBWpeIIUnLUt9qikrE) = 
                          (r10v0 'this' org.telegram.ui.Components.ChatActivityEnterView$RecordCircle$WaveDrawable A[THIS])
                          (r3v0 'round' float)
                          (r4v0 'f8' float)
                          (wrap: float : 0x00a7: CAST  (r5v0 float) = (float) (wrap: int : 0x00a5: ARITH  (r1v4 int) = (wrap: int : 0x00a1: INVOKE  (r1v3 int) = 
                          (wrap: float : 0x009f: ARITH  (r1v2 float) = (r6v0 'var_' float) / (r1v1 'f9' float))
                         java.lang.Math.round(float):int type: STATIC) * (60 int)))
                          (r6v0 'var_' float)
                          (r7v0 'var_' float)
                          (r8v0 'var_' float)
                         call: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$RecordCircle$WaveDrawable$_b_L9t3XmBBWpeIIUnLUt9qikrE.<init>(org.telegram.ui.Components.ChatActivityEnterView$RecordCircle$WaveDrawable, float, float, float, float, float, float):void type: CONSTRUCTOR in method: org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.WaveDrawable.setValue(float):void, dex: classes.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	... 64 more
                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$RecordCircle$WaveDrawable$_b_L9t3XmBBWpeIIUnLUt9qikrE, state: NOT_LOADED
                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	... 70 more
                        */
                    /*
                        this = this;
                        r10.animateToAmplitude = r11
                        boolean r0 = r10.isBig
                        r1 = 1140457472(0x43fa0000, float:500.0)
                        r2 = 1120403456(0x42CLASSNAME, float:100.0)
                        if (r0 == 0) goto L_0x0040
                        float r0 = r10.amplitude
                        int r3 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
                        if (r3 <= 0) goto L_0x0029
                        float r0 = r11 - r0
                        r3 = 1133903872(0x43960000, float:300.0)
                        org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r4 = org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.this
                        float r4 = r4.animationSpeed
                        float r3 = r3 * r4
                        float r3 = r3 + r2
                        float r0 = r0 / r3
                        r10.animateAmplitudeDiff = r0
                        float r0 = r10.slowAmplitude
                        float r0 = r11 - r0
                        float r4 = r4 * r1
                        float r4 = r4 + r2
                        float r0 = r0 / r4
                        r10.animateAmplitudeSlowDiff = r0
                        goto L_0x0075
                    L_0x0029:
                        float r0 = r11 - r0
                        org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r3 = org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.this
                        float r3 = r3.animationSpeed
                        float r4 = r3 * r1
                        float r4 = r4 + r2
                        float r0 = r0 / r4
                        r10.animateAmplitudeDiff = r0
                        float r0 = r10.slowAmplitude
                        float r0 = r11 - r0
                        float r3 = r3 * r1
                        float r3 = r3 + r2
                        float r0 = r0 / r3
                        r10.animateAmplitudeSlowDiff = r0
                        goto L_0x0075
                    L_0x0040:
                        float r0 = r10.amplitude
                        int r3 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
                        if (r3 <= 0) goto L_0x005f
                        float r0 = r11 - r0
                        r3 = 1137180672(0x43CLASSNAME, float:400.0)
                        org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r4 = org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.this
                        float r4 = r4.animationSpeedTiny
                        float r3 = r3 * r4
                        float r3 = r3 + r2
                        float r0 = r0 / r3
                        r10.animateAmplitudeDiff = r0
                        float r0 = r10.slowAmplitude
                        float r0 = r11 - r0
                        float r4 = r4 * r1
                        float r4 = r4 + r2
                        float r0 = r0 / r4
                        r10.animateAmplitudeSlowDiff = r0
                        goto L_0x0075
                    L_0x005f:
                        float r0 = r11 - r0
                        org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r3 = org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.this
                        float r3 = r3.animationSpeedTiny
                        float r4 = r3 * r1
                        float r4 = r4 + r2
                        float r0 = r0 / r4
                        r10.animateAmplitudeDiff = r0
                        float r0 = r10.slowAmplitude
                        float r0 = r11 - r0
                        float r3 = r3 * r1
                        float r3 = r3 + r2
                        float r0 = r0 / r3
                        r10.animateAmplitudeSlowDiff = r0
                    L_0x0075:
                        r0 = 1036831949(0x3dcccccd, float:0.1)
                        int r11 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
                        if (r11 >= 0) goto L_0x007e
                        r11 = 1
                        goto L_0x007f
                    L_0x007e:
                        r11 = 0
                    L_0x007f:
                        boolean r0 = r10.isIdle
                        if (r0 == r11) goto L_0x00d2
                        if (r11 == 0) goto L_0x00d2
                        boolean r0 = r10.isBig
                        if (r0 == 0) goto L_0x00d2
                        float r4 = r10.rotation
                        r0 = 60
                        float r1 = (float) r0
                        float r2 = r4 / r1
                        int r2 = java.lang.Math.round(r2)
                        int r2 = r2 * 60
                        int r2 = r2 + 30
                        float r3 = (float) r2
                        org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r2 = org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.this
                        org.telegram.ui.Components.ChatActivityEnterView$RecordCircle$WaveDrawable r2 = r2.tinyWaveDrawable
                        float r6 = r2.rotation
                        float r1 = r6 / r1
                        int r1 = java.lang.Math.round(r1)
                        int r1 = r1 * 60
                        float r5 = (float) r1
                        float r7 = r10.waveDif
                        org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.this
                        org.telegram.ui.Components.ChatActivityEnterView$RecordCircle$WaveDrawable r0 = r0.tinyWaveDrawable
                        float r8 = r0.waveDif
                        r0 = 2
                        float[] r0 = new float[r0]
                        r0 = {NUM, 0} // fill-array
                        android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
                        r10.animator = r0
                        org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$RecordCircle$WaveDrawable$_b_L9t3XmBBWpeIIUnLUt9qikrE r9 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$RecordCircle$WaveDrawable$_b_L9t3XmBBWpeIIUnLUt9qikrE
                        r1 = r9
                        r2 = r10
                        r1.<init>(r2, r3, r4, r5, r6, r7, r8)
                        r0.addUpdateListener(r9)
                        android.animation.ValueAnimator r0 = r10.animator
                        r1 = 1200(0x4b0, double:5.93E-321)
                        r0.setDuration(r1)
                        android.animation.ValueAnimator r0 = r10.animator
                        r0.start()
                    L_0x00d2:
                        r10.isIdle = r11
                        if (r11 != 0) goto L_0x00e0
                        android.animation.ValueAnimator r11 = r10.animator
                        if (r11 == 0) goto L_0x00e0
                        r11.cancel()
                        r11 = 0
                        r10.animator = r11
                    L_0x00e0:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.WaveDrawable.setValue(float):void");
                }

                public /* synthetic */ void lambda$setValue$1$ChatActivityEnterView$RecordCircle$WaveDrawable(float f, float f2, float f3, float f4, float f5, float f6, ValueAnimator valueAnimator) {
                    float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    this.rotation = f + ((f2 - f) * floatValue);
                    WaveDrawable waveDrawable = RecordCircle.this.tinyWaveDrawable;
                    waveDrawable.rotation = f3 + ((f4 - f3) * floatValue);
                    this.waveDif = ((f5 - 1.0f) * floatValue) + 1.0f;
                    waveDrawable.waveDif = ((f6 - 1.0f) * floatValue) + 1.0f;
                    this.waveAngle = (double) ((float) Math.acos((double) this.waveDif));
                    WaveDrawable waveDrawable2 = RecordCircle.this.tinyWaveDrawable;
                    waveDrawable2.waveAngle = (double) ((float) Math.acos((double) (-waveDrawable2.waveDif)));
                }

                private void startFling(float f) {
                    Animator animator2 = this.flingAnimator;
                    if (animator2 != null) {
                        animator2.cancel();
                    }
                    float f2 = this.fling * 2.0f;
                    float f3 = f * this.amplitudeRadius * ((float) (this.isBig ? 8 : 20)) * 16.0f * f2;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.flingRadius, f3});
                    ofFloat.addUpdateListener(this.flingUpdateListener);
                    ofFloat.setDuration((long) (((float) (this.isBig ? 200 : 350)) * f2));
                    ofFloat.setInterpolator(RecordCircle.this.linearInterpolator);
                    ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{f3, 0.0f});
                    ofFloat2.addUpdateListener(this.flingUpdateListener);
                    ofFloat2.setInterpolator(RecordCircle.this.linearInterpolator);
                    ofFloat2.setDuration((long) (((float) (this.isBig ? 220 : 380)) * f2));
                    AnimatorSet animatorSet = new AnimatorSet();
                    this.flingAnimator = animatorSet;
                    animatorSet.playSequentially(new Animator[]{ofFloat, ofFloat2});
                    animatorSet.start();
                }

                public void tick(float f) {
                    long currentTimeMillis = System.currentTimeMillis() - RecordCircle.this.lastUpdateTime;
                    float f2 = this.animateToAmplitude;
                    float f3 = this.amplitude;
                    if (f2 != f3) {
                        float f4 = this.animateAmplitudeDiff;
                        float f5 = f3 + (((float) currentTimeMillis) * f4);
                        this.amplitude = f5;
                        if (f4 > 0.0f) {
                            if (f5 > f2) {
                                this.amplitude = f2;
                            }
                        } else if (f5 < f2) {
                            this.amplitude = f2;
                        }
                        if (Math.abs(this.amplitude - this.animateToAmplitude) * this.amplitudeRadius >= ((float) AndroidUtilities.dp(4.0f))) {
                            this.wasFling = false;
                        } else if (!this.wasFling) {
                            startFling(this.animateAmplitudeDiff);
                            this.wasFling = true;
                        }
                    }
                    float f6 = this.animateToAmplitude;
                    float f7 = this.slowAmplitude;
                    if (f6 != f7) {
                        float f8 = f7 + (this.animateAmplitudeSlowDiff * ((float) currentTimeMillis));
                        this.slowAmplitude = f8;
                        float abs = Math.abs(f8 - this.amplitude);
                        float f9 = 0.2f;
                        if (abs > 0.2f) {
                            float var_ = this.amplitude;
                            if (this.slowAmplitude <= var_) {
                                f9 = -0.2f;
                            }
                            this.slowAmplitude = var_ + f9;
                        }
                        if (this.animateAmplitudeSlowDiff > 0.0f) {
                            float var_ = this.slowAmplitude;
                            float var_ = this.animateToAmplitude;
                            if (var_ > var_) {
                                this.slowAmplitude = var_;
                            }
                        } else {
                            float var_ = this.slowAmplitude;
                            float var_ = this.animateToAmplitude;
                            if (var_ < var_) {
                                this.slowAmplitude = var_;
                            }
                        }
                    }
                    this.idleRadius = this.idleRadiusK * f;
                    if (this.expandIdleRadius) {
                        float var_ = this.scaleIdleDif + (this.scaleSpeedIdle * ((float) currentTimeMillis));
                        this.scaleIdleDif = var_;
                        if (var_ >= 0.05f) {
                            this.scaleIdleDif = 0.05f;
                            this.expandIdleRadius = false;
                        }
                    } else {
                        float var_ = this.scaleIdleDif - (this.scaleSpeedIdle * ((float) currentTimeMillis));
                        this.scaleIdleDif = var_;
                        if (var_ < 0.0f) {
                            this.scaleIdleDif = 0.0f;
                            this.expandIdleRadius = true;
                        }
                    }
                    float var_ = this.maxScale;
                    if (var_ > 0.0f) {
                        if (this.expandScale) {
                            float var_ = this.scaleDif + (this.scaleSpeed * ((float) currentTimeMillis));
                            this.scaleDif = var_;
                            if (var_ >= var_) {
                                this.scaleDif = var_;
                                this.expandScale = false;
                            }
                        } else {
                            float var_ = this.scaleDif - (this.scaleSpeed * ((float) currentTimeMillis));
                            this.scaleDif = var_;
                            if (var_ < 0.0f) {
                                this.scaleDif = 0.0f;
                                this.expandScale = true;
                            }
                        }
                    }
                    float var_ = this.sineAngleMax;
                    float var_ = this.animateToAmplitude;
                    if (var_ > var_) {
                        float var_ = var_ - 0.25f;
                        this.sineAngleMax = var_;
                        if (var_ < var_) {
                            this.sineAngleMax = var_;
                        }
                    } else if (var_ < var_) {
                        float var_ = var_ + 0.25f;
                        this.sineAngleMax = var_;
                        if (var_ > var_) {
                            this.sineAngleMax = var_;
                        }
                    }
                    if (!this.isIdle) {
                        float var_ = this.rotation;
                        float var_ = this.amplitude;
                        float var_ = var_ + ((((var_ > 0.5f ? 1.0f : var_ / 0.5f) * 0.14400001f) + 0.018000001f) * ((float) currentTimeMillis));
                        this.rotation = var_;
                        if (var_ > 360.0f) {
                            this.rotation = var_ % 360.0f;
                        }
                    } else {
                        float var_ = this.idleRotation + (((float) currentTimeMillis) * 0.020000001f);
                        this.idleRotation = var_;
                        if (var_ > 360.0f) {
                            this.idleRotation = var_ % 360.0f;
                        }
                    }
                    float var_ = this.lastRadius;
                    if (var_ < f) {
                        this.lastRadius = f;
                    } else {
                        float var_ = var_ - (this.radiusDiff * ((float) currentTimeMillis));
                        this.lastRadius = var_;
                        if (var_ < f) {
                            this.lastRadius = f;
                        }
                    }
                    this.lastRadius = f;
                    if (!this.isIdle) {
                        double d = this.waveAngle;
                        double d2 = (double) (this.amplitudeWaveDif * this.sineAngleMax * ((float) currentTimeMillis));
                        Double.isNaN(d2);
                        double d3 = d + d2;
                        this.waveAngle = d3;
                        if (this.isBig) {
                            this.waveDif = (float) Math.cos(d3);
                        } else {
                            this.waveDif = -((float) Math.cos(d3));
                        }
                        if (this.waveDif > 0.0f && this.incRandomAdditionals) {
                            this.circleBezierDrawable.calculateRandomAdditionals();
                            this.incRandomAdditionals = false;
                        } else if (this.waveDif < 0.0f && !this.incRandomAdditionals) {
                            this.circleBezierDrawable.calculateRandomAdditionals();
                            this.incRandomAdditionals = true;
                        }
                    }
                    RecordCircle.this.invalidate();
                }

                public void draw(float f, float f2, float f3, Canvas canvas) {
                    float f4 = this.amplitude;
                    float f5 = f4 < 0.3f ? f4 / 0.3f : 1.0f;
                    float dp = ((float) AndroidUtilities.dp(10.0f)) + (((float) AndroidUtilities.dp(50.0f)) * 0.03f * this.animateToAmplitude);
                    CircleBezierDrawable circleBezierDrawable2 = this.circleBezierDrawable;
                    float f6 = 1.0f - f5;
                    circleBezierDrawable2.idleStateDiff = this.idleRadius * f6;
                    float f7 = 0.35f * f5 * this.waveDif;
                    circleBezierDrawable2.radiusDiff = dp * f7;
                    circleBezierDrawable2.cubicBezierK = (Math.abs(f7) * f5) + 1.0f + (this.idleRadiusK * f6);
                    CircleBezierDrawable circleBezierDrawable3 = this.circleBezierDrawable;
                    float f8 = this.lastRadius + (this.amplitudeRadius * this.amplitude) + this.idleGlobalRadius + (this.flingRadius * f5);
                    circleBezierDrawable3.radius = f8;
                    if (f8 + circleBezierDrawable3.radiusDiff < RecordCircle.this.circleRadius) {
                        this.circleBezierDrawable.radiusDiff = RecordCircle.this.circleRadius - this.circleBezierDrawable.radius;
                    }
                    if (this.isBig) {
                        this.circleBezierDrawable.globalRotate = this.rotation + this.idleRotation;
                    } else {
                        this.circleBezierDrawable.globalRotate = (-this.rotation) + this.idleRotation;
                    }
                    canvas.save();
                    float f9 = f3 + (this.scaleIdleDif * f6) + (this.scaleDif * f5);
                    canvas.scale(f9, f9, f, f2);
                    this.circleBezierDrawable.setRandomAdditions(f5 * this.waveDif * this.randomAdditions);
                    this.circleBezierDrawable.draw(f, f2, canvas, this.isBig ? ChatActivityEnterView.this.paintRecordWaveBig : ChatActivityEnterView.this.paintRecordWaveTin);
                    canvas.restore();
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
            Activity activity2 = activity;
            SizeNotifierFrameLayout sizeNotifierFrameLayout2 = sizeNotifierFrameLayout;
            ChatActivity chatActivity2 = chatActivity;
            int i2 = UserConfig.selectedAccount;
            this.currentAccount = i2;
            this.accountInstance = AccountInstance.getInstance(i2);
            this.emojiButton = new ImageView[2];
            this.currentPopupContentType = -1;
            this.currentEmojiIcon = -1;
            this.isPaused = true;
            this.startedDraggingX = -1.0f;
            this.distCanMove = (float) AndroidUtilities.dp(80.0f);
            this.messageWebPageSearch = true;
            this.openKeyboardRunnable = new Runnable() {
                public void run() {
                    if (!ChatActivityEnterView.this.destroyed && ChatActivityEnterView.this.messageEditText != null && ChatActivityEnterView.this.waitingForKeyboardOpen && !ChatActivityEnterView.this.keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow) {
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                        AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
                        AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
                        AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100);
                    }
                }
            };
            this.updateExpandabilityRunnable = new Runnable() {
                private int lastKnownPage = -1;

                public void run() {
                    int currentPage;
                    if (ChatActivityEnterView.this.emojiView != null && (currentPage = ChatActivityEnterView.this.emojiView.getCurrentPage()) != this.lastKnownPage) {
                        this.lastKnownPage = currentPage;
                        boolean access$800 = ChatActivityEnterView.this.stickersTabOpen;
                        int i = 2;
                        boolean unused = ChatActivityEnterView.this.stickersTabOpen = currentPage == 1 || currentPage == 2;
                        boolean access$900 = ChatActivityEnterView.this.emojiTabOpen;
                        boolean unused2 = ChatActivityEnterView.this.emojiTabOpen = currentPage == 0;
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (!ChatActivityEnterView.this.stickersTabOpen && ChatActivityEnterView.this.searchingType == 0) {
                                if (ChatActivityEnterView.this.searchingType != 0) {
                                    int unused3 = ChatActivityEnterView.this.searchingType = 0;
                                    ChatActivityEnterView.this.emojiView.closeSearch(true);
                                    ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                                }
                                ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                            } else if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                                if (currentPage != 0) {
                                    i = 1;
                                }
                                int unused4 = chatActivityEnterView.searchingType = i;
                                ChatActivityEnterView.this.checkStickresExpandHeight();
                            }
                        }
                        if (access$800 != ChatActivityEnterView.this.stickersTabOpen || access$900 != ChatActivityEnterView.this.emojiTabOpen) {
                            ChatActivityEnterView.this.checkSendButton(true);
                        }
                    }
                }
            };
            this.roundedTranslationYProperty = new Property<View, Integer>(this, Integer.class, "translationY") {
                public Integer get(View view) {
                    return Integer.valueOf(Math.round(view.getTranslationY()));
                }

                public void set(View view, Integer num) {
                    view.setTranslationY((float) num.intValue());
                }
            };
            this.recordCircleScale = new Property<RecordCircle, Float>(this, Float.class, "scale") {
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
                        ChatActivityEnterView.this.recordCircle.setLockTranslation(10000.0f);
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
                            MediaController.getInstance().startRecording(ChatActivityEnterView.this.currentAccount, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.recordingGuid);
                            boolean unused5 = ChatActivityEnterView.this.recordingAudioVideo = true;
                            ChatActivityEnterView.this.updateRecordIntefrace(0);
                            ChatActivityEnterView.this.recordTimerView.start();
                            ChatActivityEnterView.this.audioVideoButtonContainer.getParent().requestDisallowInterceptTouchEvent(true);
                            ChatActivityEnterView.this.recordCircle.showWaves(true, false);
                        } else {
                            ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
                        }
                    }
                }
            };
            this.paint = new Paint(1);
            this.paintRecordWaveBig = new Paint();
            this.paintRecordWaveTin = new Paint();
            this.pauseRect = new RectF();
            this.sendRect = new Rect();
            this.rect = new Rect();
            this.smoothKeyboard = z && SharedConfig.smoothKeyboard;
            Paint paint2 = new Paint(1);
            this.dotPaint = paint2;
            paint2.setColor(Theme.getColor("chat_emojiPanelNewTrending"));
            setFocusable(true);
            setFocusableInTouchMode(true);
            setWillNotDraw(false);
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
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
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
                        ChatActivityEnterView.this.lambda$new$0$ChatActivityEnterView(view);
                    }
                });
                this.emojiButton[i3].setContentDescription(LocaleController.getString("AccDescrEmojiButton", NUM));
                if (i3 == 1) {
                    this.emojiButton[i3].setVisibility(4);
                    this.emojiButton[i3].setAlpha(0.0f);
                    this.emojiButton[i3].setScaleX(0.1f);
                    this.emojiButton[i3].setScaleY(0.1f);
                    ImageView imageView = this.emojiButton[i3];
                } else {
                    ImageView imageView2 = this.emojiButton[i3];
                }
                i3++;
            }
            setEmojiButtonImage(false, false);
            AnonymousClass10 r4 = new EditTextCaption(activity2) {
                /* access modifiers changed from: private */
                /* renamed from: send */
                public void lambda$null$0$ChatActivityEnterView$10(InputContentInfoCompat inputContentInfoCompat, boolean z, int i) {
                    if (inputContentInfoCompat.getDescription().hasMimeType("image/gif")) {
                        SendMessagesHelper.prepareSendingDocument(ChatActivityEnterView.this.accountInstance, (String) null, (String) null, inputContentInfoCompat.getContentUri(), (String) null, "image/gif", ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, inputContentInfoCompat, (MessageObject) null, z, 0);
                    } else {
                        SendMessagesHelper.prepareSendingPhoto(ChatActivityEnterView.this.accountInstance, (String) null, inputContentInfoCompat.getContentUri(), ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, (CharSequence) null, (ArrayList<TLRPC$MessageEntity>) null, (ArrayList<TLRPC$InputDocument>) null, inputContentInfoCompat, 0, (MessageObject) null, z, 0);
                    }
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onMessageSend((CharSequence) null, true, i);
                    }
                }

                public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                    InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
                    try {
                        EditorInfoCompat.setContentMimeTypes(editorInfo, new String[]{"image/gif", "image/*", "image/jpg", "image/png"});
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
                            private final /* synthetic */ InputContentInfoCompat f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void didSelectDate(boolean z, int i) {
                                ChatActivityEnterView.AnonymousClass10.this.lambda$null$0$ChatActivityEnterView$10(this.f$1, z, i);
                            }
                        });
                    } else {
                        lambda$null$0$ChatActivityEnterView$10(inputContentInfoCompat, true, 0);
                    }
                    return true;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (ChatActivityEnterView.this.isPopupShowing() && motionEvent.getAction() == 0) {
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            int unused = ChatActivityEnterView.this.searchingType = 0;
                            ChatActivityEnterView.this.emojiView.closeSearch(false);
                        }
                        ChatActivityEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 0);
                        ChatActivityEnterView.this.openKeyboardInternal();
                    }
                    try {
                        return super.onTouchEvent(motionEvent);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return false;
                    }
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
            };
            this.messageEditText = r4;
            r4.setDelegate(new EditTextCaption.EditTextCaptionDelegate() {
                public final void onSpansChanged() {
                    ChatActivityEnterView.this.lambda$new$1$ChatActivityEnterView();
                }
            });
            this.messageEditText.setWindowView(this.parentActivity.getWindow().getDecorView());
            ChatActivity chatActivity3 = this.parentFragment;
            TLRPC$EncryptedChat currentEncryptedChat = chatActivity3 != null ? chatActivity3.getCurrentEncryptedChat() : null;
            this.messageEditText.setAllowTextEntitiesIntersection(supportsSendingNewEntities());
            updateFieldHint();
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
                                ChatActivityEnterView.this.emojiView.closeSearch(true);
                                ChatActivityEnterView.this.messageEditText.requestFocus();
                            } else {
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
                    }
                    boolean z = false;
                    if (keyEvent != null && i == 0) {
                        if ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0 && ChatActivityEnterView.this.editingMessageObject == null) {
                            ChatActivityEnterView.this.sendMessage();
                            return true;
                        } else if (i == 113 || i == 114) {
                            if (keyEvent.getAction() == 0) {
                                z = true;
                            }
                            this.ctrlPressed = z;
                            return true;
                        }
                    }
                    return false;
                }
            });
            this.messageEditText.addTextChangedListener(new TextWatcher() {
                boolean processChange = false;

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (ChatActivityEnterView.this.innerTextChange != 1) {
                        ChatActivityEnterView.this.checkSendButton(true);
                        CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence.toString());
                        if (ChatActivityEnterView.this.delegate != null && !ChatActivityEnterView.this.ignoreTextChange) {
                            if (i3 > 2 || charSequence == null || charSequence.length() == 0) {
                                boolean unused = ChatActivityEnterView.this.messageWebPageSearch = true;
                            }
                            ChatActivityEnterView.this.delegate.onTextChanged(charSequence, i2 > i3 + 1 || i3 - i2 > 2);
                        }
                        if (ChatActivityEnterView.this.innerTextChange != 2 && i3 - i2 > 1) {
                            this.processChange = true;
                        }
                        if (ChatActivityEnterView.this.editingMessageObject == null && !ChatActivityEnterView.this.canWriteToChannel && trimmedString.length() != 0 && ChatActivityEnterView.this.lastTypingTimeSend < System.currentTimeMillis() - 5000 && !ChatActivityEnterView.this.ignoreTextChange) {
                            int currentTime = ConnectionsManager.getInstance(ChatActivityEnterView.this.currentAccount).getCurrentTime();
                            TLRPC$User tLRPC$User = null;
                            if (((int) ChatActivityEnterView.this.dialog_id) > 0) {
                                tLRPC$User = ChatActivityEnterView.this.accountInstance.getMessagesController().getUser(Integer.valueOf((int) ChatActivityEnterView.this.dialog_id));
                            }
                            if (tLRPC$User != null) {
                                if (tLRPC$User.id != UserConfig.getInstance(ChatActivityEnterView.this.currentAccount).getClientUserId()) {
                                    TLRPC$UserStatus tLRPC$UserStatus = tLRPC$User.status;
                                    if (tLRPC$UserStatus != null && tLRPC$UserStatus.expires < currentTime && !ChatActivityEnterView.this.accountInstance.getMessagesController().onlinePrivacy.containsKey(Integer.valueOf(tLRPC$User.id))) {
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            }
                            long unused2 = ChatActivityEnterView.this.lastTypingTimeSend = System.currentTimeMillis();
                            if (ChatActivityEnterView.this.delegate != null) {
                                ChatActivityEnterView.this.delegate.needSendTyping();
                            }
                        }
                    }
                }

                public void afterTextChanged(Editable editable) {
                    if (ChatActivityEnterView.this.innerTextChange == 0) {
                        if (ChatActivityEnterView.this.sendByEnter && editable.length() > 0 && editable.charAt(editable.length() - 1) == 10 && ChatActivityEnterView.this.editingMessageObject == null) {
                            ChatActivityEnterView.this.sendMessage();
                        }
                        if (this.processChange) {
                            ImageSpan[] imageSpanArr = (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class);
                            for (ImageSpan removeSpan : imageSpanArr) {
                                editable.removeSpan(removeSpan);
                            }
                            Emoji.replaceEmoji(editable, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                            this.processChange = false;
                        }
                    }
                }
            });
            if (z) {
                if (this.parentFragment != null) {
                    Drawable mutate = activity.getResources().getDrawable(NUM).mutate();
                    Drawable mutate2 = activity.getResources().getDrawable(NUM).mutate();
                    mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
                    mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_recordedVoiceDot"), PorterDuff.Mode.MULTIPLY));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, mutate2);
                    ImageView imageView3 = new ImageView(activity2);
                    this.scheduledButton = imageView3;
                    imageView3.setImageDrawable(combinedDrawable);
                    this.scheduledButton.setVisibility(8);
                    this.scheduledButton.setContentDescription(LocaleController.getString("ScheduledMessages", NUM));
                    this.scheduledButton.setScaleType(ImageView.ScaleType.CENTER);
                    if (Build.VERSION.SDK_INT >= 21) {
                        this.scheduledButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
                    }
                    r2.addView(this.scheduledButton, LayoutHelper.createFrame(48, 48, 85));
                    this.scheduledButton.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            ChatActivityEnterView.this.lambda$new$2$ChatActivityEnterView(view);
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
                ImageView imageView4 = new ImageView(activity2);
                this.botButton = imageView4;
                imageView4.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
                this.botButton.setImageResource(NUM);
                this.botButton.setScaleType(ImageView.ScaleType.CENTER);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.botButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
                }
                this.botButton.setVisibility(8);
                this.attachLayout.addView(this.botButton, LayoutHelper.createLinear(48, 48));
                this.botButton.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ChatActivityEnterView.this.lambda$new$3$ChatActivityEnterView(view);
                    }
                });
                ImageView imageView5 = new ImageView(activity2);
                this.notifyButton = imageView5;
                imageView5.setImageResource(this.silent ? NUM : NUM);
                ImageView imageView6 = this.notifyButton;
                if (this.silent) {
                    i = NUM;
                    str = "AccDescrChanSilentOn";
                } else {
                    i = NUM;
                    str = "AccDescrChanSilentOff";
                }
                imageView6.setContentDescription(LocaleController.getString(str, i));
                this.notifyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
                this.notifyButton.setScaleType(ImageView.ScaleType.CENTER);
                if (Build.VERSION.SDK_INT >= 21) {
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
                        ChatActivityEnterView.this.notifyButton.setImageResource(ChatActivityEnterView.this.silent ? NUM : NUM);
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ChatActivityEnterView.this.currentAccount).edit();
                        edit.putBoolean("silent_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.silent).commit();
                        NotificationsController.getInstance(ChatActivityEnterView.this.currentAccount).updateServerNotificationsSettings(ChatActivityEnterView.this.dialog_id);
                        try {
                            if (this.visibleToast != null) {
                                this.visibleToast.cancel();
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        if (ChatActivityEnterView.this.silent) {
                            Toast makeText = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOff", NUM), 0);
                            this.visibleToast = makeText;
                            makeText.show();
                        } else {
                            Toast makeText2 = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOn", NUM), 0);
                            this.visibleToast = makeText2;
                            makeText2.show();
                        }
                        ImageView access$7400 = ChatActivityEnterView.this.notifyButton;
                        if (ChatActivityEnterView.this.silent) {
                            i = NUM;
                            str = "AccDescrChanSilentOn";
                        } else {
                            i = NUM;
                            str = "AccDescrChanSilentOff";
                        }
                        access$7400.setContentDescription(LocaleController.getString(str, i));
                        ChatActivityEnterView.this.updateFieldHint();
                    }
                });
                ImageView imageView7 = new ImageView(activity2);
                this.attachButton = imageView7;
                imageView7.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
                this.attachButton.setImageResource(NUM);
                this.attachButton.setScaleType(ImageView.ScaleType.CENTER);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.attachButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
                }
                this.attachLayout.addView(this.attachButton, LayoutHelper.createLinear(48, 48));
                this.attachButton.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ChatActivityEnterView.this.lambda$new$4$ChatActivityEnterView(view);
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
            updateRecordedDeleteIconColors();
            this.recordDeleteImageView.setContentDescription(LocaleController.getString("Delete", NUM));
            if (Build.VERSION.SDK_INT >= 21) {
                this.recordDeleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            }
            this.recordedAudioPanel.addView(this.recordDeleteImageView, LayoutHelper.createFrame(48, 48.0f));
            this.recordDeleteImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$5$ChatActivityEnterView(view);
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
                    ChatActivityEnterView.this.lambda$new$6$ChatActivityEnterView(view);
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
            this.recordPanel.setOnTouchListener($$Lambda$ChatActivityEnterView$Uo3sK08hLv7NAPRSM5RagKUbmWg.INSTANCE);
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
                    return ChatActivityEnterView.this.lambda$new$12$ChatActivityEnterView(view, motionEvent);
                }
            });
            ImageView imageView8 = new ImageView(activity2);
            this.audioSendButton = imageView8;
            imageView8.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            this.audioSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            this.audioSendButton.setImageResource(NUM);
            this.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
            this.audioSendButton.setContentDescription(LocaleController.getString("AccDescrVoiceMessage", NUM));
            this.audioSendButton.setFocusable(true);
            this.audioSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
            this.audioVideoButtonContainer.addView(this.audioSendButton, LayoutHelper.createFrame(48, 48.0f));
            if (z) {
                ImageView imageView9 = new ImageView(activity2);
                this.videoSendButton = imageView9;
                imageView9.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
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
            ImageView imageView10 = new ImageView(activity2);
            this.cancelBotButton = imageView10;
            imageView10.setVisibility(4);
            this.cancelBotButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ImageView imageView11 = this.cancelBotButton;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView11.setImageDrawable(closeProgressDrawable2);
            this.cancelBotButton.setContentDescription(LocaleController.getString("Cancel", NUM));
            this.progressDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelCancelInlineBot"), PorterDuff.Mode.MULTIPLY));
            this.cancelBotButton.setSoundEffectsEnabled(false);
            this.cancelBotButton.setScaleX(0.1f);
            this.cancelBotButton.setScaleY(0.1f);
            this.cancelBotButton.setAlpha(0.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                this.cancelBotButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            }
            this.sendButtonContainer.addView(this.cancelBotButton, LayoutHelper.createFrame(48, 48.0f));
            this.cancelBotButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$13$ChatActivityEnterView(view);
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
            AnonymousClass16 r22 = new View(activity2) {
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
                        } else if (z) {
                            this.animationProgress = 0.0f;
                            this.animationDuration = 200.0f;
                        } else {
                            this.animationProgress = 0.0f;
                            this.animationDuration = 120.0f;
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
            };
            this.sendButton = r22;
            r22.setVisibility(4);
            int color = Theme.getColor("chat_messagePanelSend");
            this.sendButton.setContentDescription(LocaleController.getString("Send", NUM));
            this.sendButton.setSoundEffectsEnabled(false);
            this.sendButton.setScaleX(0.1f);
            this.sendButton.setScaleY(0.1f);
            this.sendButton.setAlpha(0.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                this.sendButton.setBackgroundDrawable(Theme.createSelectorDrawable(Color.argb(24, Color.red(color), Color.green(color), Color.blue(color)), 1));
            }
            this.sendButtonContainer.addView(this.sendButton, LayoutHelper.createFrame(48, 48.0f));
            this.sendButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$14$ChatActivityEnterView(view);
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
                    ChatActivityEnterView.this.lambda$new$15$ChatActivityEnterView(view);
                }
            });
            this.slowModeButton.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return ChatActivityEnterView.this.lambda$new$16$ChatActivityEnterView(view);
                }
            });
            ImageView imageView12 = new ImageView(activity2);
            this.expandStickersButton = imageView12;
            imageView12.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView13 = this.expandStickersButton;
            AnimatedArrowDrawable animatedArrowDrawable = new AnimatedArrowDrawable(Theme.getColor("chat_messagePanelIcons"), false);
            this.stickersArrow = animatedArrowDrawable;
            imageView13.setImageDrawable(animatedArrowDrawable);
            this.expandStickersButton.setVisibility(8);
            this.expandStickersButton.setScaleX(0.1f);
            this.expandStickersButton.setScaleY(0.1f);
            this.expandStickersButton.setAlpha(0.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                this.expandStickersButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            }
            this.sendButtonContainer.addView(this.expandStickersButton, LayoutHelper.createFrame(48, 48.0f));
            this.expandStickersButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$17$ChatActivityEnterView(view);
                }
            });
            this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", NUM));
            FrameLayout frameLayout5 = new FrameLayout(activity2);
            this.doneButtonContainer = frameLayout5;
            frameLayout5.setVisibility(8);
            this.textFieldContainer.addView(this.doneButtonContainer, LayoutHelper.createLinear(48, 48, 80));
            this.doneButtonContainer.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$18$ChatActivityEnterView(view);
                }
            });
            ShapeDrawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(16.0f), Theme.getColor("chat_messagePanelSend"));
            Drawable mutate3 = activity.getResources().getDrawable(NUM).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(createCircleDrawable, mutate3, 0, AndroidUtilities.dp(1.0f));
            combinedDrawable2.setCustomSize(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
            ImageView imageView14 = new ImageView(activity2);
            this.doneButtonImage = imageView14;
            imageView14.setScaleType(ImageView.ScaleType.CENTER);
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

        public /* synthetic */ void lambda$new$0$ChatActivityEnterView(View view) {
            boolean z = false;
            if (!isPopupShowing() || this.currentPopupContentType != 0) {
                showPopup(1, 0);
                EmojiView emojiView2 = this.emojiView;
                if (this.messageEditText.length() > 0) {
                    z = true;
                }
                emojiView2.onOpen(z);
                return;
            }
            if (this.searchingType != 0) {
                this.searchingType = 0;
                this.emojiView.closeSearch(false);
                this.messageEditText.requestFocus();
            }
            openKeyboardInternal();
        }

        public /* synthetic */ void lambda$new$1$ChatActivityEnterView() {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onTextSpansChanged(this.messageEditText.getText());
            }
        }

        public /* synthetic */ void lambda$new$2$ChatActivityEnterView(View view) {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.openScheduledMessages();
            }
        }

        public /* synthetic */ void lambda$new$3$ChatActivityEnterView(View view) {
            int i;
            if (this.searchingType != 0) {
                this.searchingType = 0;
                this.emojiView.closeSearch(false);
                this.messageEditText.requestFocus();
            }
            if (this.botReplyMarkup != null) {
                if (!isPopupShowing() || (i = this.currentPopupContentType) != 1) {
                    showPopup(1, 1);
                    SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
                    edit.remove("hidekeyboard_" + this.dialog_id).commit();
                } else {
                    if (i == 1 && this.botButtonsMessageObject != null) {
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

        public /* synthetic */ void lambda$new$4$ChatActivityEnterView(View view) {
            this.delegate.didPressedAttachButton();
        }

        public /* synthetic */ void lambda$new$5$ChatActivityEnterView(View view) {
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

        public /* synthetic */ void lambda$new$6$ChatActivityEnterView(View view) {
            if (this.audioToSend != null) {
                if (!MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject) || MediaController.getInstance().isMessagePaused()) {
                    this.playPauseDrawable.setIcon(1, true);
                    MediaController.getInstance().playMessage(this.audioToSendMessageObject);
                    this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
                    return;
                }
                MediaController.getInstance().lambda$startAudioAgain$7$MediaController(this.audioToSendMessageObject);
                this.playPauseDrawable.setIcon(0, true);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            }
        }

        public /* synthetic */ boolean lambda$new$12$ChatActivityEnterView(View view, MotionEvent motionEvent) {
            TLRPC$Chat currentChat;
            int i = 3;
            boolean z = false;
            if (motionEvent.getAction() != 0) {
                float f = 1.0f;
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    if (motionEvent.getAction() == 3 && this.recordingAudioVideo) {
                        startLockTransition();
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
                    } else if (!this.hasRecordVideo || this.calledRecordRunnable) {
                        this.startedDraggingX = -1.0f;
                        if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                            if (this.recordingAudioVideo && isInScheduleMode()) {
                                AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), $$Lambda$ChatActivityEnterView$DbhNVr7aGBPkzi2ZSveTFbR_EbU.INSTANCE, $$Lambda$ChatActivityEnterView$HfVlrs8yNKiGpBRjGRr9aZOie5g.INSTANCE);
                            }
                            this.delegate.needStartRecordAudio(0);
                            MediaController.getInstance().stopRecording(isInScheduleMode() ? 3 : 1, true, 0);
                        } else {
                            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                            this.delegate.needStartRecordVideo(1, true, 0);
                        }
                        this.recordingAudioVideo = false;
                        if (!isInScheduleMode()) {
                            i = 1;
                        }
                        updateRecordIntefrace(i);
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
                if (!this.hasRecordVideo || this.calledRecordRunnable) {
                    this.startedDraggingX = -1.0f;
                    if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                        if (this.recordingAudioVideo && isInScheduleMode()) {
                            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), $$Lambda$ChatActivityEnterView$ntnzswOfvdfLiJpE9RVLQ3qgvgc.INSTANCE, $$Lambda$ChatActivityEnterView$ig8uuPdwp02PNB5dFwZQd6fT5I.INSTANCE);
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
                    updateRecordIntefrace(1);
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

        public /* synthetic */ void lambda$new$13$ChatActivityEnterView(View view) {
            String obj = this.messageEditText.getText().toString();
            int indexOf = obj.indexOf(32);
            if (indexOf == -1 || indexOf == obj.length() - 1) {
                setFieldText("");
            } else {
                setFieldText(obj.substring(0, indexOf + 1));
            }
        }

        public /* synthetic */ void lambda$new$14$ChatActivityEnterView(View view) {
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
                AnimatorSet animatorSet = this.runningAnimationAudio;
                if (animatorSet == null || !animatorSet.isRunning()) {
                    sendMessage();
                }
            }
        }

        public /* synthetic */ void lambda$new$15$ChatActivityEnterView(View view) {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                SimpleTextView simpleTextView = this.slowModeButton;
                chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
            }
        }

        public /* synthetic */ boolean lambda$new$16$ChatActivityEnterView(View view) {
            if (this.messageEditText.length() == 0) {
                return false;
            }
            return onSendLongClick(view);
        }

        public /* synthetic */ void lambda$new$17$ChatActivityEnterView(View view) {
            EmojiView emojiView2;
            if (this.expandStickersButton.getVisibility() == 0 && this.expandStickersButton.getAlpha() == 1.0f) {
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

        public /* synthetic */ void lambda$new$18$ChatActivityEnterView(View view) {
            doneEditingMessage();
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

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view == this.topView) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), view.getLayoutParams().height + AndroidUtilities.dp(2.0f));
            }
            boolean drawChild = super.drawChild(canvas, view, j);
            if (view == this.topView) {
                canvas.restore();
            }
            return drawChild;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            View view = this.topView;
            int translationY = (view == null || view.getVisibility() != 0) ? 0 : (int) this.topView.getTranslationY();
            int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + translationY;
            Theme.chat_composeShadowDrawable.setBounds(0, translationY, getMeasuredWidth(), intrinsicHeight);
            Theme.chat_composeShadowDrawable.draw(canvas);
            canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getWidth(), (float) getHeight(), Theme.chat_composeBackgroundPaint);
        }

        /* access modifiers changed from: private */
        public boolean onSendLongClick(View view) {
            int i;
            View view2 = view;
            if (this.parentFragment != null && !isInScheduleMode() && this.parentFragment.getCurrentEncryptedChat() == null) {
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
                            ChatActivityEnterView.this.lambda$onSendLongClick$19$ChatActivityEnterView(keyEvent);
                        }
                    });
                    this.sendPopupLayout.setShowedFromBotton(false);
                    for (int i2 = 0; i2 < 2; i2++) {
                        if (i2 != 1 || (!UserObject.isUserSelf(currentUser) && (this.slowModeTimer <= 0 || isInScheduleMode()))) {
                            ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext());
                            if (i2 == 0) {
                                if (UserObject.isUserSelf(currentUser)) {
                                    actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                                } else {
                                    actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                                }
                            } else if (i2 == 1) {
                                actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                            }
                            actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
                            this.sendPopupLayout.addView(actionBarMenuSubItem, LayoutHelper.createFrame(-1, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i2 * 48), 0.0f, 0.0f));
                            actionBarMenuSubItem.setOnClickListener(new View.OnClickListener(i2) {
                                private final /* synthetic */ int f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onClick(View view) {
                                    ChatActivityEnterView.this.lambda$onSendLongClick$20$ChatActivityEnterView(this.f$1, view);
                                }
                            });
                        }
                    }
                    AnonymousClass18 r2 = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2) {
                        public void dismiss() {
                            super.dismiss();
                            ChatActivityEnterView.this.sendButton.invalidate();
                        }
                    };
                    this.sendPopupWindow = r2;
                    r2.setAnimationEnabled(false);
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
                int[] iArr = new int[2];
                view2.getLocationInWindow(iArr);
                if (this.keyboardVisible) {
                    int measuredHeight = getMeasuredHeight();
                    View view3 = this.topView;
                    if (measuredHeight > AndroidUtilities.dp((view3 == null || view3.getVisibility() != 0) ? 58.0f : 106.0f)) {
                        i = iArr[1] + view.getMeasuredHeight();
                        this.sendPopupWindow.showAtLocation(view2, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), i);
                        this.sendPopupWindow.dimBehind();
                        this.sendButton.invalidate();
                        view2.performHapticFeedback(3, 2);
                    }
                }
                i = (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f);
                this.sendPopupWindow.showAtLocation(view2, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), i);
                this.sendPopupWindow.dimBehind();
                this.sendButton.invalidate();
                view2.performHapticFeedback(3, 2);
            }
            return false;
        }

        public /* synthetic */ void lambda$onSendLongClick$19$ChatActivityEnterView(KeyEvent keyEvent) {
            ActionBarPopupWindow actionBarPopupWindow;
            if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupWindow.dismiss();
            }
        }

        public /* synthetic */ void lambda$onSendLongClick$20$ChatActivityEnterView(int i, View view) {
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
            } else if (i == 1) {
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
                org.telegram.ui.Components.ChatActivityEnterView$19 r13 = new org.telegram.ui.Components.ChatActivityEnterView$19
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
            if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
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
                org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$X9J0gVteYqpfLVW9jaFB4fp2jQc r0 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$X9J0gVteYqpfLVW9jaFB4fp2jQc
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
            if (!((this.allowStickers == z && this.allowGifs == z2) || this.emojiView == null)) {
                if (this.emojiViewVisible) {
                    hidePopup(false);
                }
                this.sizeNotifierLayout.removeView(this.emojiView);
                this.emojiView = null;
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

        public void showTopView(boolean z, boolean z2) {
            if (this.topView != null && !this.topViewShowed && getVisibility() == 0) {
                this.needShowTopView = true;
                this.topViewShowed = true;
                if (this.allowShowTopView) {
                    this.topView.setVisibility(0);
                    this.topLineView.setVisibility(0);
                    AnimatorSet animatorSet = this.currentTopViewAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.currentTopViewAnimation = null;
                    }
                    resizeForTopView(true);
                    if (z) {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        this.currentTopViewAnimation = animatorSet2;
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.topView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.topLineView, View.ALPHA, new float[]{1.0f})});
                        this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator)) {
                                    AnimatorSet unused = ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                            }
                        });
                        this.currentTopViewAnimation.setDuration(250);
                        this.currentTopViewAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.currentTopViewAnimation.start();
                    } else {
                        this.topView.setTranslationY(0.0f);
                        this.topLineView.setAlpha(1.0f);
                    }
                    if (this.recordedAudioPanel.getVisibility() == 0) {
                        return;
                    }
                    if (!this.forceShowSendButton || z2) {
                        this.messageEditText.requestFocus();
                        openKeyboard();
                    }
                }
            } else if (this.recordedAudioPanel.getVisibility() == 0) {
            } else {
                if (!this.forceShowSendButton || z2) {
                    openKeyboard();
                }
            }
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
                this.topViewShowed = false;
                this.needShowTopView = false;
                if (this.allowShowTopView) {
                    AnimatorSet animatorSet = this.currentTopViewAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.currentTopViewAnimation = null;
                    }
                    if (z) {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        this.currentTopViewAnimation = animatorSet2;
                        View view = this.topView;
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{(float) view.getLayoutParams().height}), ObjectAnimator.ofFloat(this.topLineView, View.ALPHA, new float[]{0.0f})});
                        this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator)) {
                                    ChatActivityEnterView.this.topView.setVisibility(8);
                                    ChatActivityEnterView.this.topLineView.setVisibility(8);
                                    ChatActivityEnterView.this.resizeForTopView(false);
                                    AnimatorSet unused = ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator)) {
                                    AnimatorSet unused = ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                            }
                        });
                        this.currentTopViewAnimation.setDuration(200);
                        this.currentTopViewAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.currentTopViewAnimation.start();
                        return;
                    }
                    this.topView.setVisibility(8);
                    this.topLineView.setVisibility(8);
                    this.topLineView.setAlpha(0.0f);
                    resizeForTopView(false);
                    View view2 = this.topView;
                    view2.setTranslationY((float) view2.getLayoutParams().height);
                }
            }
        }

        public boolean isTopViewVisible() {
            View view = this.topView;
            return view != null && view.getVisibility() == 0;
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
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
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
            closeKeyboard();
        }

        public void onResume() {
            this.isPaused = false;
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
                NotificationCenter.getInstance(i2).removeObserver(this, NotificationCenter.recordStarted);
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
                this.currentAccount = i;
                this.accountInstance = AccountInstance.getInstance(i);
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
            updateFieldHint();
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
                } else if (AndroidUtilities.getPeerLayerVersion(this.accountInstance.getMessagesController().getEncryptedChat(Integer.valueOf(i2)).layer) >= 66) {
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
            return this.videoSendButton.getTag() != null;
        }

        public boolean hasRecordVideo() {
            return this.hasRecordVideo;
        }

        /* access modifiers changed from: private */
        public void updateFieldHint() {
            boolean z = false;
            if (((int) this.dialog_id) < 0) {
                TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
                if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    z = true;
                }
            }
            if (this.editingMessageObject != null) {
                this.messageEditText.setHintText(this.editingCaption ? LocaleController.getString("Caption", NUM) : LocaleController.getString("TypeMessage", NUM));
            } else if (!z) {
                this.messageEditText.setHintText(LocaleController.getString("TypeMessage", NUM));
            } else if (this.silent) {
                this.messageEditText.setHintText(LocaleController.getString("ChannelSilentBroadcast", NUM));
            } else {
                this.messageEditText.setHintText(LocaleController.getString("ChannelBroadcast", NUM));
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
            } else if (messageObject == null && this.replyingMessageObject == this.botButtonsMessageObject) {
                this.replyingMessageObject = null;
                setButtons(this.botMessageObject, false);
                this.botMessageObject = null;
            } else {
                this.replyingMessageObject = messageObject;
            }
            MediaController.getInstance().setReplyingMessage(messageObject);
        }

        public void setWebPage(TLRPC$WebPage tLRPC$WebPage, boolean z) {
            this.messageWebPage = tLRPC$WebPage;
            this.messageWebPageSearch = z;
        }

        public boolean isMessageWebPageSearchEnabled() {
            return this.messageWebPageSearch;
        }

        private void hideRecordedAudioPanel(boolean z) {
            AnimatorSet animatorSet = this.recordPannelAnimation;
            if (animatorSet == null || !animatorSet.isRunning()) {
                this.audioToSendPath = null;
                this.audioToSend = null;
                this.audioToSendMessageObject = null;
                this.videoToSendMessageObject = null;
                this.videoTimelineView.destroy();
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
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.recordPannelAnimation = animatorSet2;
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{0.0f})});
                    this.recordPannelAnimation.setDuration(150);
                    this.recordPannelAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
                            ChatActivityEnterView.this.messageEditText.requestFocus();
                        }
                    });
                    this.recordPannelAnimation.start();
                    return;
                }
                this.recordDeleteImageView.playAnimation();
                AnimatorSet animatorSet3 = new AnimatorSet();
                if (isInVideoMode()) {
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.videoTimelineView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.videoTimelineView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{0.0f})});
                } else {
                    this.messageEditText.setAlpha(1.0f);
                    this.messageEditText.setTranslationX(0.0f);
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordedAudioSeekBar, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioPlayButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioBackground, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioSeekBar, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.recordedAudioPlayButton, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.recordedAudioBackground, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))})});
                }
                animatorSet3.setDuration(200);
                this.attachButton.setAlpha(0.0f);
                this.emojiButton[0].setAlpha(0.0f);
                this.emojiButton[1].setAlpha(0.0f);
                this.attachButton.setScaleX(0.0f);
                this.emojiButton[0].setScaleX(0.0f);
                this.emojiButton[1].setScaleX(0.0f);
                this.attachButton.setScaleY(0.0f);
                this.emojiButton[0].setScaleY(0.0f);
                this.emojiButton[1].setScaleY(0.0f);
                AnimatorSet animatorSet4 = new AnimatorSet();
                animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, new float[]{1.0f})});
                animatorSet4.setDuration(150);
                AnimatorSet animatorSet5 = new AnimatorSet();
                animatorSet5.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f})});
                animatorSet5.setDuration(150);
                animatorSet5.setStartDelay(600);
                AnimatorSet animatorSet6 = new AnimatorSet();
                this.recordPannelAnimation = animatorSet6;
                animatorSet6.playTogether(new Animator[]{animatorSet3, animatorSet4, animatorSet5});
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
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.audioToSend, (VideoEditedInfo) null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, (Object) null);
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
                this.delegate.onMessageEditEnd(true);
                showEditDoneProgress(true, true);
                CharSequence[] charSequenceArr = {this.messageEditText.getText()};
                ArrayList<TLRPC$MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities());
                SendMessagesHelper instance = SendMessagesHelper.getInstance(this.currentAccount);
                MessageObject messageObject = this.editingMessageObject;
                String charSequence = charSequenceArr[0].toString();
                boolean z = this.messageWebPageSearch;
                ChatActivity chatActivity = this.parentFragment;
                MessageObject messageObject2 = this.editingMessageObject;
                this.editingMessageReqId = instance.editMessage(messageObject, charSequence, z, chatActivity, entities, messageObject2.scheduled ? messageObject2.messageOwner.date : 0, new Runnable() {
                    public final void run() {
                        ChatActivityEnterView.this.lambda$doneEditingMessage$21$ChatActivityEnterView();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$doneEditingMessage$21$ChatActivityEnterView() {
            this.editingMessageReqId = 0;
            setEditingMessageObject((MessageObject) null, false);
        }

        public boolean processSendingText(CharSequence charSequence, boolean z, int i) {
            CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence);
            boolean supportsSendingNewEntities = supportsSendingNewEntities();
            int i2 = this.accountInstance.getMessagesController().maxMessageLength;
            if (trimmedString.length() == 0) {
                return false;
            }
            int ceil = (int) Math.ceil((double) (((float) trimmedString.length()) / ((float) i2)));
            int i3 = 0;
            while (i3 < ceil) {
                int i4 = i3 * i2;
                i3++;
                CharSequence[] charSequenceArr = {trimmedString.subSequence(i4, Math.min(i3 * i2, trimmedString.length()))};
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(charSequenceArr[0].toString(), this.dialog_id, this.replyingMessageObject, this.messageWebPage, this.messageWebPageSearch, MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities), (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i);
            }
            return true;
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
                                linearLayout2.setVisibility(0);
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
                                        ChatActivityEnterView.this.sendButton.setVisibility(8);
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                        ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                        ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
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
                        if (this.attachLayout != null || this.recordInterfaceState == 0) {
                            this.attachLayout.setVisibility(0);
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
                        this.runningAnimation.setDuration(150);
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
                        LinearLayout linearLayout3 = this.attachLayout;
                        if (linearLayout3 != null) {
                            linearLayout3.setVisibility(8);
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
            ImageView imageView4;
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null && this.editingMessageObject == null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) editTextCaption.getLayoutParams();
                int i2 = layoutParams.rightMargin;
                if (i == 1) {
                    ImageView imageView5 = this.botButton;
                    if ((imageView5 == null || imageView5.getVisibility() != 0) && (((imageView3 = this.notifyButton) == null || imageView3.getVisibility() != 0) && ((imageView4 = this.scheduledButton) == null || imageView4.getTag() == null))) {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    } else {
                        layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                    }
                } else if (i != 2) {
                    ImageView imageView6 = this.scheduledButton;
                    if (imageView6 == null || imageView6.getTag() == null) {
                        layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
                    } else {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    }
                } else if (i2 != AndroidUtilities.dp(2.0f)) {
                    ImageView imageView7 = this.botButton;
                    if ((imageView7 == null || imageView7.getVisibility() != 0) && (((imageView = this.notifyButton) == null || imageView.getVisibility() != 0) && ((imageView2 = this.scheduledButton) == null || imageView2.getTag() == null))) {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    } else {
                        layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                    }
                }
                if (i2 != layoutParams.rightMargin) {
                    this.messageEditText.setLayoutParams(layoutParams);
                }
            }
        }

        /* access modifiers changed from: private */
        public void updateRecordIntefrace(int i) {
            boolean z;
            long j;
            final ViewGroup.LayoutParams layoutParams;
            final ViewGroup viewGroup;
            final int i2 = i;
            if (!this.recordingAudioVideo) {
                PowerManager.WakeLock wakeLock2 = this.wakeLock;
                if (wakeLock2 != null) {
                    try {
                        wakeLock2.release();
                        this.wakeLock = null;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                AndroidUtilities.unlockOrientation(this.parentActivity);
                this.wasSendTyping = false;
                if (this.recordInterfaceState != 0) {
                    this.accountInstance.getMessagesController().sendTyping(this.dialog_id, 2, 0);
                    this.recordInterfaceState = 0;
                    EmojiView emojiView2 = this.emojiView;
                    if (emojiView2 != null) {
                        emojiView2.setEnabled(true);
                    }
                    AnimatorSet animatorSet = this.runningAnimationAudio;
                    if (animatorSet != null) {
                        z = animatorSet.isRunning();
                        this.runningAnimationAudio.removeAllListeners();
                        this.runningAnimationAudio.cancel();
                    } else {
                        z = false;
                    }
                    AnimatorSet animatorSet2 = this.recordPannelAnimation;
                    if (animatorSet2 != null) {
                        animatorSet2.cancel();
                    }
                    this.messageEditText.setVisibility(0);
                    this.runningAnimationAudio = new AnimatorSet();
                    if (z || i2 == 4) {
                        AnimatorSet animatorSet3 = this.runningAnimationAudio;
                        Animator[] animatorArr = new Animator[20];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f});
                        animatorArr[3] = ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f});
                        animatorArr[4] = ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f});
                        animatorArr[5] = ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f});
                        animatorArr[6] = ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, new float[]{0.0f});
                        animatorArr[7] = ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, new float[]{0.0f});
                        animatorArr[8] = ObjectAnimator.ofFloat(this.attachLayout, View.TRANSLATION_X, new float[]{0.0f});
                        animatorArr[9] = ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f});
                        animatorArr[10] = ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{0.0f});
                        animatorArr[11] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f});
                        animatorArr[12] = ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, new float[]{0.0f});
                        animatorArr[13] = ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{0.0f});
                        animatorArr[14] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f});
                        animatorArr[15] = ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f});
                        animatorArr[16] = ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{0.0f});
                        ImageView imageView = this.audioSendButton;
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = isInVideoMode() ? 0.0f : 1.0f;
                        animatorArr[17] = ObjectAnimator.ofFloat(imageView, property, fArr);
                        ImageView imageView2 = this.videoSendButton;
                        Property property2 = View.ALPHA;
                        float[] fArr2 = new float[1];
                        fArr2[0] = isInVideoMode() ? 1.0f : 0.0f;
                        animatorArr[18] = ObjectAnimator.ofFloat(imageView2, property2, fArr2);
                        animatorArr[19] = ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", new float[]{1.0f});
                        animatorSet3.playTogether(animatorArr);
                        ImageView imageView3 = this.scheduledButton;
                        if (imageView3 != null) {
                            this.runningAnimationAudio.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView3, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f})});
                        }
                        this.runningAnimationAudio.setDuration(150);
                    } else if (i2 == 3) {
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
                        this.recordDeleteImageView.setProgress(0.0f);
                        this.recordDeleteImageView.stopAnimation();
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ChatActivityEnterView.this.lambda$updateRecordIntefrace$22$ChatActivityEnterView(valueAnimator);
                            }
                        });
                        if (!isInVideoMode()) {
                            viewGroup = (ViewGroup) this.recordedAudioPanel.getParent();
                            layoutParams = this.recordedAudioPanel.getLayoutParams();
                            viewGroup.removeView(this.recordedAudioPanel);
                            FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(viewGroup.getMeasuredWidth(), AndroidUtilities.dp(48.0f));
                            layoutParams2.gravity = 80;
                            this.sizeNotifierLayout.addView(this.recordedAudioPanel, layoutParams2);
                            this.videoTimelineView.setVisibility(8);
                        } else {
                            this.videoTimelineView.setVisibility(0);
                            viewGroup = null;
                            layoutParams = null;
                        }
                        this.recordDeleteImageView.setAlpha(0.0f);
                        this.recordDeleteImageView.setScaleX(0.0f);
                        this.recordDeleteImageView.setScaleY(0.0f);
                        AnimatorSet animatorSet4 = new AnimatorSet();
                        Animator[] animatorArr2 = new Animator[17];
                        animatorArr2[0] = ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, new float[]{0.0f});
                        animatorArr2[1] = ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, new float[]{0.0f});
                        animatorArr2[2] = ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, new float[]{0.0f});
                        animatorArr2[3] = ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))});
                        animatorArr2[4] = ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{0.0f});
                        animatorArr2[5] = ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{1.0f});
                        animatorArr2[6] = ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, new float[]{1.0f});
                        animatorArr2[7] = ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, new float[]{1.0f});
                        animatorArr2[8] = ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{0.0f});
                        animatorArr2[9] = ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{0.0f});
                        animatorArr2[10] = ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{0.0f});
                        animatorArr2[11] = ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{0.0f});
                        animatorArr2[12] = ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{0.0f});
                        animatorArr2[13] = ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{0.0f});
                        animatorArr2[14] = ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{0.0f});
                        ImageView imageView4 = this.audioSendButton;
                        Property property3 = View.ALPHA;
                        float[] fArr3 = new float[1];
                        fArr3[0] = isInVideoMode() ? 0.0f : 1.0f;
                        animatorArr2[15] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
                        ImageView imageView5 = this.videoSendButton;
                        Property property4 = View.ALPHA;
                        float[] fArr4 = new float[1];
                        fArr4[0] = isInVideoMode() ? 1.0f : 0.0f;
                        animatorArr2[16] = ObjectAnimator.ofFloat(imageView5, property4, fArr4);
                        animatorSet4.playTogether(animatorArr2);
                        animatorSet4.setDuration(150);
                        animatorSet4.setStartDelay(150);
                        AnimatorSet animatorSet5 = new AnimatorSet();
                        if (isInVideoMode()) {
                            this.recordedAudioTimeTextView.setAlpha(0.0f);
                            this.videoTimelineView.setAlpha(0.0f);
                            animatorSet5.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.videoTimelineView, View.ALPHA, new float[]{1.0f})});
                            animatorSet5.setDuration(150);
                            animatorSet5.setStartDelay(430);
                        }
                        ofFloat.setDuration(isInVideoMode() ? 490 : 580);
                        this.runningAnimationAudio.playTogether(new Animator[]{animatorSet4, ofFloat, animatorSet5});
                        this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (viewGroup != null) {
                                    ChatActivityEnterView.this.sizeNotifierLayout.removeView(ChatActivityEnterView.this.recordedAudioPanel);
                                    viewGroup.addView(ChatActivityEnterView.this.recordedAudioPanel, layoutParams);
                                }
                                ChatActivityEnterView.this.recordedAudioPanel.setAlpha(1.0f);
                                ChatActivityEnterView.this.recordedAudioBackground.setAlpha(1.0f);
                                ChatActivityEnterView.this.recordedAudioTimeTextView.setAlpha(1.0f);
                                ChatActivityEnterView.this.recordedAudioPlayButton.setAlpha(1.0f);
                                ChatActivityEnterView.this.recordedAudioSeekBar.setAlpha(1.0f);
                            }
                        });
                    } else if (i2 == 2 || i2 == 5) {
                        AnimatorSet animatorSet6 = new AnimatorSet();
                        animatorSet6.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, new float[]{0.0f})});
                        AnimatorSet animatorSet7 = new AnimatorSet();
                        animatorSet7.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))}), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(20.0f))})});
                        if (i2 != 5) {
                            this.audioVideoButtonContainer.setScaleX(0.0f);
                            this.audioVideoButtonContainer.setScaleY(0.0f);
                            if (this.attachButton.getVisibility() == 0) {
                                this.attachButton.setScaleX(0.0f);
                                this.attachButton.setScaleY(0.0f);
                            }
                            if (this.botButton.getVisibility() == 0) {
                                this.attachButton.setScaleX(0.0f);
                                this.attachButton.setScaleY(0.0f);
                            }
                            Animator[] animatorArr3 = new Animator[12];
                            animatorArr3[0] = ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", new float[]{1.0f});
                            animatorArr3[1] = ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f});
                            animatorArr3[2] = ObjectAnimator.ofFloat(this.attachLayout, View.TRANSLATION_X, new float[]{0.0f});
                            animatorArr3[3] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{1.0f});
                            animatorArr3[4] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{1.0f});
                            animatorArr3[5] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f});
                            animatorArr3[6] = ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, new float[]{1.0f});
                            animatorArr3[7] = ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, new float[]{1.0f});
                            animatorArr3[8] = ObjectAnimator.ofFloat(this.botButton, View.SCALE_X, new float[]{1.0f});
                            animatorArr3[9] = ObjectAnimator.ofFloat(this.botButton, View.SCALE_Y, new float[]{1.0f});
                            ImageView imageView6 = this.audioSendButton;
                            Property property5 = View.ALPHA;
                            float[] fArr5 = new float[1];
                            fArr5[0] = isInVideoMode() ? 0.0f : 1.0f;
                            animatorArr3[10] = ObjectAnimator.ofFloat(imageView6, property5, fArr5);
                            ImageView imageView7 = this.videoSendButton;
                            Property property6 = View.ALPHA;
                            float[] fArr6 = new float[1];
                            fArr6[0] = isInVideoMode() ? 1.0f : 0.0f;
                            animatorArr3[11] = ObjectAnimator.ofFloat(imageView7, property6, fArr6);
                            animatorSet6.playTogether(animatorArr3);
                            ImageView imageView8 = this.scheduledButton;
                            if (imageView8 != null) {
                                animatorSet6.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView8, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f})});
                            }
                            j = 150;
                        } else {
                            AnimatorSet animatorSet8 = new AnimatorSet();
                            animatorSet8.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.attachLayout, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f})});
                            ImageView imageView9 = this.scheduledButton;
                            if (imageView9 != null) {
                                animatorSet8.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView9, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f})});
                            }
                            j = 150;
                            animatorSet8.setDuration(150);
                            animatorSet8.setStartDelay(110);
                            animatorSet8.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    super.onAnimationEnd(animator);
                                    float f = 0.0f;
                                    ChatActivityEnterView.this.audioSendButton.setAlpha(ChatActivityEnterView.this.isInVideoMode() ? 0.0f : 1.0f);
                                    ImageView access$2100 = ChatActivityEnterView.this.videoSendButton;
                                    if (ChatActivityEnterView.this.isInVideoMode()) {
                                        f = 1.0f;
                                    }
                                    access$2100.setAlpha(f);
                                }
                            });
                            this.runningAnimationAudio.playTogether(new Animator[]{animatorSet8});
                        }
                        animatorSet6.setDuration(j);
                        animatorSet6.setStartDelay(700);
                        animatorSet7.setDuration(200);
                        animatorSet7.setStartDelay(200);
                        this.messageEditText.setTranslationX(0.0f);
                        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f});
                        ofFloat2.setStartDelay(300);
                        ofFloat2.setDuration(200);
                        AnimatorSet animatorSet9 = this.runningAnimationAudio;
                        RecordCircle recordCircle2 = this.recordCircle;
                        animatorSet9.playTogether(new Animator[]{animatorSet6, animatorSet7, ofFloat2, ObjectAnimator.ofFloat(recordCircle2, "lockAnimatedTranslation", new float[]{recordCircle2.startTranslation}).setDuration(200)});
                        if (i2 == 5) {
                            this.recordCircle.canceledByGesture();
                            ObjectAnimator duration = ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", new float[]{1.0f}).setDuration(200);
                            duration.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                            this.runningAnimationAudio.playTogether(new Animator[]{duration});
                        } else {
                            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.recordCircle, "exitTransition", new float[]{1.0f});
                            ofFloat3.setDuration(360);
                            ofFloat3.setStartDelay(490);
                            this.runningAnimationAudio.playTogether(new Animator[]{ofFloat3});
                        }
                        this.recordDot.playDeleteAnimation();
                    } else {
                        AnimatorSet animatorSet10 = new AnimatorSet();
                        Animator[] animatorArr4 = new Animator[13];
                        animatorArr4[0] = ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{1.0f});
                        animatorArr4[1] = ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{1.0f});
                        animatorArr4[2] = ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{1.0f});
                        animatorArr4[3] = ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f});
                        animatorArr4[4] = ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f});
                        animatorArr4[5] = ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f});
                        animatorArr4[6] = ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, new float[]{0.0f});
                        animatorArr4[7] = ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, new float[]{0.0f});
                        animatorArr4[8] = ObjectAnimator.ofFloat(this.attachLayout, View.TRANSLATION_X, new float[]{0.0f});
                        animatorArr4[9] = ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f});
                        animatorArr4[10] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f});
                        ImageView imageView10 = this.audioSendButton;
                        Property property7 = View.ALPHA;
                        float[] fArr7 = new float[1];
                        fArr7[0] = isInVideoMode() ? 0.0f : 1.0f;
                        animatorArr4[11] = ObjectAnimator.ofFloat(imageView10, property7, fArr7);
                        ImageView imageView11 = this.videoSendButton;
                        Property property8 = View.ALPHA;
                        float[] fArr8 = new float[1];
                        fArr8[0] = isInVideoMode() ? 1.0f : 0.0f;
                        animatorArr4[12] = ObjectAnimator.ofFloat(imageView11, property8, fArr8);
                        animatorSet10.playTogether(animatorArr4);
                        ImageView imageView12 = this.scheduledButton;
                        if (imageView12 != null) {
                            animatorSet10.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView12, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f})});
                        }
                        animatorSet10.setDuration(150);
                        animatorSet10.setStartDelay(200);
                        AnimatorSet animatorSet11 = new AnimatorSet();
                        animatorSet11.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(40.0f)}), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(40.0f)})});
                        animatorSet11.setDuration(150);
                        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.recordCircle, "exitTransition", new float[]{1.0f});
                        ofFloat4.setDuration(360);
                        this.messageEditText.setTranslationX(0.0f);
                        ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f});
                        ofFloat5.setStartDelay(150);
                        ofFloat5.setDuration(200);
                        this.runningAnimationAudio.playTogether(new Animator[]{animatorSet10, animatorSet11, ofFloat5, ofFloat4});
                    }
                    this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
                                ChatActivityEnterView.this.recordPanel.setVisibility(8);
                                ChatActivityEnterView.this.recordCircle.setVisibility(8);
                                ChatActivityEnterView.this.recordCircle.setSendButtonInvisible();
                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimationAudio = null;
                                if (i2 != 3) {
                                    ChatActivityEnterView.this.messageEditText.requestFocus();
                                }
                                ChatActivityEnterView.this.recordedAudioBackground.setAlpha(1.0f);
                                ChatActivityEnterView.this.attachLayout.setTranslationX(0.0f);
                                ChatActivityEnterView.this.slideText.setCancelToProgress(0.0f);
                                ChatActivityEnterView.this.delegate.onAudioVideoInterfaceUpdated();
                            }
                        }
                    });
                    this.runningAnimationAudio.start();
                    this.recordTimerView.stop();
                } else {
                    return;
                }
            } else if (this.recordInterfaceState != 1) {
                this.recordInterfaceState = 1;
                EmojiView emojiView3 = this.emojiView;
                if (emojiView3 != null) {
                    emojiView3.setEnabled(false);
                }
                try {
                    if (this.wakeLock == null) {
                        PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(NUM, "telegram:audio_record_lock");
                        this.wakeLock = newWakeLock;
                        newWakeLock.acquire();
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                AndroidUtilities.lockOrientation(this.parentActivity);
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    chatActivityEnterViewDelegate.needStartRecordAudio(0);
                }
                AnimatorSet animatorSet12 = this.runningAnimationAudio;
                if (animatorSet12 != null) {
                    animatorSet12.cancel();
                }
                AnimatorSet animatorSet13 = this.recordPannelAnimation;
                if (animatorSet13 != null) {
                    animatorSet13.cancel();
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
                AnimatorSet animatorSet14 = new AnimatorSet();
                animatorSet14.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.audioSendButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.videoSendButton, View.ALPHA, new float[]{0.0f})});
                animatorSet14.setStartDelay(150);
                AnimatorSet animatorSet15 = new AnimatorSet();
                animatorSet15.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(20.0f)}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.attachLayout, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(30.0f)}), ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, new float[]{1.0f})});
                ImageView imageView13 = this.scheduledButton;
                if (imageView13 != null) {
                    animatorSet15.playTogether(new Animator[]{ObjectAnimator.ofFloat(imageView13, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(30.0f)}), ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{0.0f})});
                }
                this.runningAnimationAudio.playTogether(new Animator[]{animatorSet14.setDuration(150), animatorSet15.setDuration(150), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{1.0f}).setDuration(300)});
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimationAudio = null;
                        }
                        boolean unused2 = ChatActivityEnterView.this.recordDot.enterAnimation = false;
                        ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                        ChatActivityEnterView.this.slideText.setTranslationX(0.0f);
                        ChatActivityEnterView.this.recordCircle.showTooltipIfNeed();
                        ChatActivityEnterView.this.messageEditText.setVisibility(8);
                    }
                });
                this.runningAnimationAudio.setInterpolator(new DecelerateInterpolator());
                this.runningAnimationAudio.start();
                this.recordTimerView.start();
            } else {
                return;
            }
            this.delegate.onAudioVideoInterfaceUpdated();
        }

        public /* synthetic */ void lambda$updateRecordIntefrace$22$ChatActivityEnterView(ValueAnimator valueAnimator) {
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
                        tLRPC$User = this.accountInstance.getMessagesController().getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
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
                        tLRPC$User = this.accountInstance.getMessagesController().getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                    }
                    if ((this.botCount != 1 || z2) && tLRPC$User != null && tLRPC$User.bot && !str3.contains("@")) {
                        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(String.format(Locale.US, "%s@%s", new Object[]{str3, tLRPC$User.username}), this.dialog_id, this.replyingMessageObject, (TLRPC$WebPage) null, false, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                        return;
                    }
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(str, this.dialog_id, this.replyingMessageObject, (TLRPC$WebPage) null, false, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else {
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                    if (chatActivityEnterViewDelegate2 != null) {
                        SimpleTextView simpleTextView = this.slowModeButton;
                        chatActivityEnterViewDelegate2.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
                    }
                }
            }
        }

        public void setEditingMessageObject(MessageObject messageObject, boolean z) {
            CharSequence charSequence;
            if (this.audioToSend == null && this.videoToSendMessageObject == null && this.editingMessageObject != messageObject) {
                if (this.editingMessageReqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.editingMessageReqId, true);
                    this.editingMessageReqId = 0;
                }
                this.editingMessageObject = messageObject;
                this.editingCaption = z;
                if (messageObject != null) {
                    AnimatorSet animatorSet = this.doneButtonAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.doneButtonAnimation = null;
                    }
                    this.doneButtonContainer.setVisibility(0);
                    showEditDoneProgress(true, false);
                    InputFilter[] inputFilterArr = new InputFilter[1];
                    if (z) {
                        inputFilterArr[0] = new InputFilter.LengthFilter(this.accountInstance.getMessagesController().maxCaptionLength);
                        charSequence = this.editingMessageObject.caption;
                    } else {
                        inputFilterArr[0] = new InputFilter.LengthFilter(this.accountInstance.getMessagesController().maxMessageLength);
                        charSequence = this.editingMessageObject.messageText;
                    }
                    if (charSequence != null) {
                        ArrayList<TLRPC$MessageEntity> arrayList = this.editingMessageObject.messageOwner.entities;
                        MediaDataController.sortEntities(arrayList);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
                        Object[] spans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), Object.class);
                        if (spans != null && spans.length > 0) {
                            for (Object removeSpan : spans) {
                                spannableStringBuilder.removeSpan(removeSpan);
                            }
                        }
                        if (arrayList != null) {
                            int i = 0;
                            while (i < arrayList.size()) {
                                try {
                                    TLRPC$MessageEntity tLRPC$MessageEntity = arrayList.get(i);
                                    if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length <= spannableStringBuilder.length()) {
                                        if (tLRPC$MessageEntity instanceof TLRPC$TL_inputMessageEntityMentionName) {
                                            if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length < spannableStringBuilder.length() && spannableStringBuilder.charAt(tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length) == ' ') {
                                                tLRPC$MessageEntity.length++;
                                            }
                                            spannableStringBuilder.setSpan(new URLSpanUserMention("" + ((TLRPC$TL_inputMessageEntityMentionName) tLRPC$MessageEntity).user_id.user_id, 3), tLRPC$MessageEntity.offset, tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length, 33);
                                        } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityMentionName) {
                                            if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length < spannableStringBuilder.length() && spannableStringBuilder.charAt(tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length) == ' ') {
                                                tLRPC$MessageEntity.length++;
                                            }
                                            spannableStringBuilder.setSpan(new URLSpanUserMention("" + ((TLRPC$TL_messageEntityMentionName) tLRPC$MessageEntity).user_id, 3), tLRPC$MessageEntity.offset, tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length, 33);
                                        } else {
                                            if (!(tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCode)) {
                                                if (!(tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityPre)) {
                                                    if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityBold) {
                                                        TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                                                        textStyleRun.flags |= 1;
                                                        MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun), tLRPC$MessageEntity.offset, tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length, spannableStringBuilder, true);
                                                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityItalic) {
                                                        TextStyleSpan.TextStyleRun textStyleRun2 = new TextStyleSpan.TextStyleRun();
                                                        textStyleRun2.flags |= 2;
                                                        MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun2), tLRPC$MessageEntity.offset, tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length, spannableStringBuilder, true);
                                                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityStrike) {
                                                        TextStyleSpan.TextStyleRun textStyleRun3 = new TextStyleSpan.TextStyleRun();
                                                        textStyleRun3.flags |= 8;
                                                        MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun3), tLRPC$MessageEntity.offset, tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length, spannableStringBuilder, true);
                                                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityUnderline) {
                                                        TextStyleSpan.TextStyleRun textStyleRun4 = new TextStyleSpan.TextStyleRun();
                                                        textStyleRun4.flags |= 16;
                                                        MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun4), tLRPC$MessageEntity.offset, tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length, spannableStringBuilder, true);
                                                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityTextUrl) {
                                                        spannableStringBuilder.setSpan(new URLSpanReplacement(tLRPC$MessageEntity.url), tLRPC$MessageEntity.offset, tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length, 33);
                                                    }
                                                }
                                            }
                                            TextStyleSpan.TextStyleRun textStyleRun5 = new TextStyleSpan.TextStyleRun();
                                            textStyleRun5.flags |= 4;
                                            MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun5), tLRPC$MessageEntity.offset, tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length, spannableStringBuilder, true);
                                        }
                                    }
                                    i++;
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            }
                        }
                        setFieldText(Emoji.replaceEmoji(new SpannableStringBuilder(spannableStringBuilder), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
                    } else {
                        setFieldText("");
                    }
                    this.messageEditText.setFilters(inputFilterArr);
                    openKeyboard();
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.messageEditText.getLayoutParams();
                    layoutParams.rightMargin = AndroidUtilities.dp(4.0f);
                    this.messageEditText.setLayoutParams(layoutParams);
                    this.sendButton.setVisibility(8);
                    setSlowModeButtonVisible(false);
                    this.cancelBotButton.setVisibility(8);
                    this.audioVideoButtonContainer.setVisibility(8);
                    this.attachLayout.setVisibility(8);
                    this.sendButtonContainer.setVisibility(8);
                    ImageView imageView = this.scheduledButton;
                    if (imageView != null) {
                        imageView.setVisibility(8);
                    }
                } else {
                    this.doneButtonContainer.setVisibility(8);
                    this.messageEditText.setFilters(new InputFilter[0]);
                    this.delegate.onMessageEditEnd(false);
                    this.sendButtonContainer.setVisibility(0);
                    this.cancelBotButton.setScaleX(0.1f);
                    this.cancelBotButton.setScaleY(0.1f);
                    this.cancelBotButton.setAlpha(0.0f);
                    this.cancelBotButton.setVisibility(8);
                    if (this.slowModeTimer <= 0 || isInScheduleMode()) {
                        this.sendButton.setScaleX(0.1f);
                        this.sendButton.setScaleY(0.1f);
                        this.sendButton.setAlpha(0.0f);
                        this.sendButton.setVisibility(8);
                        this.slowModeButton.setScaleX(0.1f);
                        this.slowModeButton.setScaleY(0.1f);
                        this.slowModeButton.setAlpha(0.0f);
                        setSlowModeButtonVisible(false);
                        this.attachLayout.setScaleX(1.0f);
                        this.attachLayout.setAlpha(1.0f);
                        this.attachLayout.setVisibility(0);
                        this.audioVideoButtonContainer.setScaleX(1.0f);
                        this.audioVideoButtonContainer.setScaleY(1.0f);
                        this.audioVideoButtonContainer.setAlpha(1.0f);
                        this.audioVideoButtonContainer.setVisibility(0);
                    } else {
                        if (this.slowModeTimer == Integer.MAX_VALUE) {
                            this.sendButton.setScaleX(1.0f);
                            this.sendButton.setScaleY(1.0f);
                            this.sendButton.setAlpha(1.0f);
                            this.sendButton.setVisibility(0);
                            this.slowModeButton.setScaleX(0.1f);
                            this.slowModeButton.setScaleY(0.1f);
                            this.slowModeButton.setAlpha(0.0f);
                            setSlowModeButtonVisible(false);
                        } else {
                            this.sendButton.setScaleX(0.1f);
                            this.sendButton.setScaleY(0.1f);
                            this.sendButton.setAlpha(0.0f);
                            this.sendButton.setVisibility(8);
                            this.slowModeButton.setScaleX(1.0f);
                            this.slowModeButton.setScaleY(1.0f);
                            this.slowModeButton.setAlpha(1.0f);
                            setSlowModeButtonVisible(true);
                        }
                        this.attachLayout.setScaleX(0.01f);
                        this.attachLayout.setAlpha(0.0f);
                        this.attachLayout.setVisibility(8);
                        this.audioVideoButtonContainer.setScaleX(0.1f);
                        this.audioVideoButtonContainer.setScaleY(0.1f);
                        this.audioVideoButtonContainer.setAlpha(0.0f);
                        this.audioVideoButtonContainer.setVisibility(8);
                    }
                    if (this.scheduledButton.getTag() != null) {
                        this.scheduledButton.setScaleX(1.0f);
                        this.scheduledButton.setScaleY(1.0f);
                        this.scheduledButton.setAlpha(1.0f);
                        this.scheduledButton.setVisibility(0);
                    }
                    this.messageEditText.setText("");
                    if (getVisibility() == 0) {
                        this.delegate.onAttachButtonShow();
                    }
                    updateFieldRight(1);
                }
                updateFieldHint();
            }
        }

        public ImageView getAttachButton() {
            return this.attachButton;
        }

        public View getSendButton() {
            return this.sendButton.getVisibility() == 0 ? this.sendButton : this.audioVideoButtonContainer;
        }

        public EmojiView getEmojiView() {
            return this.emojiView;
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
                    if (editTextCaption != null && editTextCaption.isFocused() && !this.keyboardVisible) {
                        this.messageEditText.clearFocus();
                    }
                } else if (this.searchingType == 0 && !this.messageEditText.isFocused()) {
                    $$Lambda$ChatActivityEnterView$05hH6FPF8sAzjObBU2KakU_7gx0 r3 = new Runnable() {
                        public final void run() {
                            ChatActivityEnterView.this.lambda$setFieldFocused$23$ChatActivityEnterView();
                        }
                    };
                    this.focusRunnable = r3;
                    AndroidUtilities.runOnUIThread(r3, 600);
                }
            }
        }

        public /* synthetic */ void lambda$setFieldFocused$23$ChatActivityEnterView() {
            EditTextCaption editTextCaption;
            LaunchActivity launchActivity;
            ActionBarLayout layersActionBarLayout;
            this.focusRunnable = null;
            boolean z = true;
            if (AndroidUtilities.isTablet()) {
                Activity activity = this.parentActivity;
                if ((activity instanceof LaunchActivity) && (launchActivity = (LaunchActivity) activity) != null && (layersActionBarLayout = launchActivity.getLayersActionBarLayout()) != null && layersActionBarLayout.getVisibility() == 0) {
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
                if (r1 >= 0) goto L_0x0078
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
                if (r3 == 0) goto L_0x0066
                boolean r4 = r13.silent
                if (r4 == 0) goto L_0x005f
                r4 = 2131165512(0x7var_, float:1.7945243E38)
                goto L_0x0062
            L_0x005f:
                r4 = 2131165513(0x7var_, float:1.7945245E38)
            L_0x0062:
                r3.setImageResource(r4)
                goto L_0x0067
            L_0x0066:
                r1 = 0
            L_0x0067:
                android.widget.LinearLayout r3 = r13.attachLayout
                if (r3 == 0) goto L_0x0079
                int r3 = r3.getVisibility()
                if (r3 != 0) goto L_0x0073
                r3 = 1
                goto L_0x0074
            L_0x0073:
                r3 = 0
            L_0x0074:
                r13.updateFieldRight(r3)
                goto L_0x0079
            L_0x0078:
                r1 = 0
            L_0x0079:
                org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r3 = r13.delegate
                if (r3 == 0) goto L_0x008d
                boolean r3 = r13.isInScheduleMode()
                if (r3 != 0) goto L_0x008d
                org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r3 = r13.delegate
                boolean r3 = r3.hasScheduledMessages()
                if (r3 == 0) goto L_0x008d
                r3 = 1
                goto L_0x008e
            L_0x008d:
                r3 = 0
            L_0x008e:
                if (r3 == 0) goto L_0x0096
                boolean r4 = r13.scheduleButtonHidden
                if (r4 != 0) goto L_0x0096
                r4 = 1
                goto L_0x0097
            L_0x0096:
                r4 = 0
            L_0x0097:
                android.widget.ImageView r5 = r13.scheduledButton
                r6 = 1119879168(0x42CLASSNAME, float:96.0)
                r7 = 1111490560(0x42400000, float:48.0)
                r8 = 0
                r9 = 8
                if (r5 == 0) goto L_0x0104
                java.lang.Object r5 = r5.getTag()
                if (r5 == 0) goto L_0x00aa
                if (r4 != 0) goto L_0x00b4
            L_0x00aa:
                android.widget.ImageView r5 = r13.scheduledButton
                java.lang.Object r5 = r5.getTag()
                if (r5 != 0) goto L_0x00f7
                if (r4 != 0) goto L_0x00f7
            L_0x00b4:
                android.widget.ImageView r14 = r13.notifyButton
                if (r14 == 0) goto L_0x00f6
                if (r3 != 0) goto L_0x00c5
                if (r1 == 0) goto L_0x00c5
                android.widget.ImageView r14 = r13.scheduledButton
                int r14 = r14.getVisibility()
                if (r14 == 0) goto L_0x00c5
                goto L_0x00c7
            L_0x00c5:
                r2 = 8
            L_0x00c7:
                android.widget.ImageView r14 = r13.notifyButton
                int r14 = r14.getVisibility()
                if (r2 == r14) goto L_0x00f6
                android.widget.ImageView r14 = r13.notifyButton
                r14.setVisibility(r2)
                android.widget.LinearLayout r14 = r13.attachLayout
                if (r14 == 0) goto L_0x00f6
                android.widget.ImageView r0 = r13.botButton
                if (r0 == 0) goto L_0x00e2
                int r0 = r0.getVisibility()
                if (r0 != r9) goto L_0x00ee
            L_0x00e2:
                android.widget.ImageView r0 = r13.notifyButton
                if (r0 == 0) goto L_0x00ec
                int r0 = r0.getVisibility()
                if (r0 != r9) goto L_0x00ee
            L_0x00ec:
                r6 = 1111490560(0x42400000, float:48.0)
            L_0x00ee:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r0 = (float) r0
                r14.setPivotX(r0)
            L_0x00f6:
                return
            L_0x00f7:
                android.widget.ImageView r3 = r13.scheduledButton
                if (r4 == 0) goto L_0x0100
                java.lang.Integer r5 = java.lang.Integer.valueOf(r0)
                goto L_0x0101
            L_0x0100:
                r5 = r8
            L_0x0101:
                r3.setTag(r5)
            L_0x0104:
                android.animation.AnimatorSet r3 = r13.scheduledButtonAnimation
                if (r3 == 0) goto L_0x010d
                r3.cancel()
                r13.scheduledButtonAnimation = r8
            L_0x010d:
                r3 = 0
                r5 = 1036831949(0x3dcccccd, float:0.1)
                r8 = 1065353216(0x3var_, float:1.0)
                if (r14 == 0) goto L_0x018a
                if (r1 == 0) goto L_0x0118
                goto L_0x018a
            L_0x0118:
                if (r4 == 0) goto L_0x011f
                android.widget.ImageView r14 = r13.scheduledButton
                r14.setVisibility(r2)
            L_0x011f:
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
                if (r4 == 0) goto L_0x013f
                r3 = 1065353216(0x3var_, float:1.0)
            L_0x013f:
                r12[r2] = r3
                android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
                r1[r2] = r3
                android.widget.ImageView r3 = r13.scheduledButton
                android.util.Property r10 = android.view.View.SCALE_X
                float[] r11 = new float[r0]
                if (r4 == 0) goto L_0x0152
                r12 = 1065353216(0x3var_, float:1.0)
                goto L_0x0155
            L_0x0152:
                r12 = 1036831949(0x3dcccccd, float:0.1)
            L_0x0155:
                r11[r2] = r12
                android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
                r1[r0] = r3
                r3 = 2
                android.widget.ImageView r10 = r13.scheduledButton
                android.util.Property r11 = android.view.View.SCALE_Y
                float[] r0 = new float[r0]
                if (r4 == 0) goto L_0x0168
                r5 = 1065353216(0x3var_, float:1.0)
            L_0x0168:
                r0[r2] = r5
                android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r10, r11, r0)
                r1[r3] = r0
                r14.playTogether(r1)
                android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
                r0 = 180(0xb4, double:8.9E-322)
                r14.setDuration(r0)
                android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
                org.telegram.ui.Components.ChatActivityEnterView$37 r0 = new org.telegram.ui.Components.ChatActivityEnterView$37
                r0.<init>(r4)
                r14.addListener(r0)
                android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
                r14.start()
                goto L_0x01ca
            L_0x018a:
                android.widget.ImageView r14 = r13.scheduledButton
                if (r14 == 0) goto L_0x01b6
                if (r4 == 0) goto L_0x0192
                r0 = 0
                goto L_0x0194
            L_0x0192:
                r0 = 8
            L_0x0194:
                r14.setVisibility(r0)
                android.widget.ImageView r14 = r13.scheduledButton
                if (r4 == 0) goto L_0x019d
                r3 = 1065353216(0x3var_, float:1.0)
            L_0x019d:
                r14.setAlpha(r3)
                android.widget.ImageView r14 = r13.scheduledButton
                if (r4 == 0) goto L_0x01a7
                r0 = 1065353216(0x3var_, float:1.0)
                goto L_0x01aa
            L_0x01a7:
                r0 = 1036831949(0x3dcccccd, float:0.1)
            L_0x01aa:
                r14.setScaleX(r0)
                android.widget.ImageView r14 = r13.scheduledButton
                if (r4 == 0) goto L_0x01b3
                r5 = 1065353216(0x3var_, float:1.0)
            L_0x01b3:
                r14.setScaleY(r5)
            L_0x01b6:
                android.widget.ImageView r14 = r13.notifyButton
                if (r14 == 0) goto L_0x01ca
                if (r1 == 0) goto L_0x01c5
                android.widget.ImageView r0 = r13.scheduledButton
                int r0 = r0.getVisibility()
                if (r0 == 0) goto L_0x01c5
                goto L_0x01c7
            L_0x01c5:
                r2 = 8
            L_0x01c7:
                r14.setVisibility(r2)
            L_0x01ca:
                android.widget.LinearLayout r14 = r13.attachLayout
                if (r14 == 0) goto L_0x01ec
                android.widget.ImageView r0 = r13.botButton
                if (r0 == 0) goto L_0x01d8
                int r0 = r0.getVisibility()
                if (r0 != r9) goto L_0x01e4
            L_0x01d8:
                android.widget.ImageView r0 = r13.notifyButton
                if (r0 == 0) goto L_0x01e2
                int r0 = r0.getVisibility()
                if (r0 != r9) goto L_0x01e4
            L_0x01e2:
                r6 = 1111490560(0x42400000, float:48.0)
            L_0x01e4:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r0 = (float) r0
                r14.setPivotX(r0)
            L_0x01ec:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.updateScheduleButton(boolean):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:24:0x0085, code lost:
            r1 = r4.notifyButton;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void updateBotButton() {
            /*
                r4 = this;
                android.widget.ImageView r0 = r4.botButton
                if (r0 != 0) goto L_0x0005
                return
            L_0x0005:
                boolean r1 = r4.hasBotCommands
                r2 = 8
                if (r1 != 0) goto L_0x0014
                org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r1 = r4.botReplyMarkup
                if (r1 == 0) goto L_0x0010
                goto L_0x0014
            L_0x0010:
                r0.setVisibility(r2)
                goto L_0x0075
            L_0x0014:
                android.widget.ImageView r0 = r4.botButton
                int r0 = r0.getVisibility()
                if (r0 == 0) goto L_0x0022
                android.widget.ImageView r0 = r4.botButton
                r1 = 0
                r0.setVisibility(r1)
            L_0x0022:
                org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = r4.botReplyMarkup
                if (r0 == 0) goto L_0x005f
                boolean r0 = r4.isPopupShowing()
                if (r0 == 0) goto L_0x0048
                int r0 = r4.currentPopupContentType
                r1 = 1
                if (r0 != r1) goto L_0x0048
                android.widget.ImageView r0 = r4.botButton
                r1 = 2131165509(0x7var_, float:1.7945237E38)
                r0.setImageResource(r1)
                android.widget.ImageView r0 = r4.botButton
                r1 = 2131624010(0x7f0e004a, float:1.8875188E38)
                java.lang.String r3 = "AccDescrShowKeyboard"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r0.setContentDescription(r1)
                goto L_0x0075
            L_0x0048:
                android.widget.ImageView r0 = r4.botButton
                r1 = 2131165502(0x7var_e, float:1.7945223E38)
                r0.setImageResource(r1)
                android.widget.ImageView r0 = r4.botButton
                r1 = 2131623953(0x7f0e0011, float:1.8875072E38)
                java.lang.String r3 = "AccDescrBotKeyboard"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r0.setContentDescription(r1)
                goto L_0x0075
            L_0x005f:
                android.widget.ImageView r0 = r4.botButton
                r1 = 2131165501(0x7var_d, float:1.794522E38)
                r0.setImageResource(r1)
                android.widget.ImageView r0 = r4.botButton
                r1 = 2131623952(0x7f0e0010, float:1.887507E38)
                java.lang.String r3 = "AccDescrBotCommands"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r0.setContentDescription(r1)
            L_0x0075:
                r0 = 2
                r4.updateFieldRight(r0)
                android.widget.LinearLayout r0 = r4.attachLayout
                android.widget.ImageView r1 = r4.botButton
                if (r1 == 0) goto L_0x0085
                int r1 = r1.getVisibility()
                if (r1 != r2) goto L_0x0090
            L_0x0085:
                android.widget.ImageView r1 = r4.notifyButton
                if (r1 == 0) goto L_0x0093
                int r1 = r1.getVisibility()
                if (r1 != r2) goto L_0x0090
                goto L_0x0093
            L_0x0090:
                r1 = 1119879168(0x42CLASSNAME, float:96.0)
                goto L_0x0095
            L_0x0093:
                r1 = 1111490560(0x42400000, float:48.0)
            L_0x0095:
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                r0.setPivotX(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.updateBotButton():void");
        }

        public boolean isRtlText() {
            try {
                return this.messageEditText.getLayout().getParagraphDirection(0) == -1;
            } catch (Throwable unused) {
                return false;
            }
        }

        public void setBotsCount(int i, boolean z) {
            this.botCount = i;
            if (this.hasBotCommands != z) {
                this.hasBotCommands = z;
                updateBotButton();
            }
        }

        public void setButtons(MessageObject messageObject) {
            setButtons(messageObject, true);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:39:0x00c7, code lost:
            if (r8.getInt("answered_" + r6.dialog_id, 0) == r7.getId()) goto L_0x00cb;
         */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0067  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x006a  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x007a  */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00e1  */
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
                org.telegram.messenger.MessageObject r0 = r6.botButtonsMessageObject
                if (r0 != 0) goto L_0x001f
                if (r7 != 0) goto L_0x001f
                goto L_0x00f7
            L_0x001f:
                org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
                r1 = 0
                r2 = 1
                if (r0 != 0) goto L_0x004b
                org.telegram.ui.Components.ChatActivityEnterView$38 r0 = new org.telegram.ui.Components.ChatActivityEnterView$38
                android.app.Activity r3 = r6.parentActivity
                r0.<init>(r3)
                r6.botKeyboardView = r0
                r3 = 8
                r0.setVisibility(r3)
                r6.botKeyboardViewVisible = r1
                org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
                org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$A8Bbipu3sbu9rMKqbtJB0m3cjA0 r3 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$A8Bbipu3sbu9rMKqbtJB0m3cjA0
                r3.<init>()
                r0.setDelegate(r3)
                org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r6.sizeNotifierLayout
                org.telegram.ui.Components.BotKeyboardView r3 = r6.botKeyboardView
                int r4 = r0.getChildCount()
                int r4 = r4 - r2
                r0.addView(r3, r4)
            L_0x004b:
                r6.botButtonsMessageObject = r7
                if (r7 == 0) goto L_0x005a
                org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
                org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup
                if (r3 == 0) goto L_0x005a
                org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = (org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup) r0
                goto L_0x005b
            L_0x005a:
                r0 = 0
            L_0x005b:
                r6.botReplyMarkup = r0
                org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
                android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
                int r4 = r3.x
                int r3 = r3.y
                if (r4 <= r3) goto L_0x006a
                int r3 = r6.keyboardHeightLand
                goto L_0x006c
            L_0x006a:
                int r3 = r6.keyboardHeight
            L_0x006c:
                r0.setPanelHeight(r3)
                org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
                org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r3 = r6.botReplyMarkup
                r0.setButtons(r3)
                org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = r6.botReplyMarkup
                if (r0 == 0) goto L_0x00e1
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
                if (r0 != r3) goto L_0x009f
                r0 = 1
                goto L_0x00a0
            L_0x009f:
                r0 = 0
            L_0x00a0:
                org.telegram.messenger.MessageObject r3 = r6.botButtonsMessageObject
                org.telegram.messenger.MessageObject r4 = r6.replyingMessageObject
                if (r3 == r4) goto L_0x00ca
                org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r3 = r6.botReplyMarkup
                boolean r3 = r3.single_use
                if (r3 == 0) goto L_0x00ca
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "answered_"
                r3.append(r4)
                long r4 = r6.dialog_id
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                int r8 = r8.getInt(r3, r1)
                int r7 = r7.getId()
                if (r8 != r7) goto L_0x00ca
                goto L_0x00cb
            L_0x00ca:
                r1 = 1
            L_0x00cb:
                if (r1 == 0) goto L_0x00f4
                if (r0 != 0) goto L_0x00f4
                org.telegram.ui.Components.EditTextCaption r7 = r6.messageEditText
                int r7 = r7.length()
                if (r7 != 0) goto L_0x00f4
                boolean r7 = r6.isPopupShowing()
                if (r7 != 0) goto L_0x00f4
                r6.showPopup(r2, r2)
                goto L_0x00f4
            L_0x00e1:
                boolean r7 = r6.isPopupShowing()
                if (r7 == 0) goto L_0x00f4
                int r7 = r6.currentPopupContentType
                if (r7 != r2) goto L_0x00f4
                if (r8 == 0) goto L_0x00f1
                r6.openKeyboardInternal()
                goto L_0x00f4
            L_0x00f1:
                r6.showPopup(r1, r2)
            L_0x00f4:
                r6.updateBotButton()
            L_0x00f7:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setButtons(org.telegram.messenger.MessageObject, boolean):void");
        }

        public /* synthetic */ void lambda$setButtons$24$ChatActivityEnterView(TLRPC$KeyboardButton tLRPC$KeyboardButton) {
            MessageObject messageObject = this.replyingMessageObject;
            if (messageObject == null) {
                messageObject = ((int) this.dialog_id) < 0 ? this.botButtonsMessageObject : null;
            }
            MessageObject messageObject2 = this.replyingMessageObject;
            if (messageObject2 == null) {
                messageObject2 = this.botButtonsMessageObject;
            }
            boolean didPressedBotButton = didPressedBotButton(tLRPC$KeyboardButton, messageObject, messageObject2);
            if (this.replyingMessageObject != null) {
                openKeyboardInternal();
                setButtons(this.botMessageObject, false);
            } else if (this.botButtonsMessageObject.messageOwner.reply_markup.single_use) {
                if (didPressedBotButton) {
                    openKeyboardInternal();
                } else {
                    showPopup(0, 0);
                }
                SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
                edit.putInt("answered_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
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
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(tLRPC$KeyboardButton2.text, this.dialog_id, messageObject, (TLRPC$WebPage) null, false, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            } else if (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonUrl) {
                this.parentFragment.showOpenUrlAlert(tLRPC$KeyboardButton2.url, true);
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
                        private final /* synthetic */ MessageObject f$1;
                        private final /* synthetic */ TLRPC$KeyboardButton f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatActivityEnterView.this.lambda$didPressedBotButton$25$ChatActivityEnterView(this.f$1, this.f$2, dialogInterface, i);
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
                        int i = tLRPC$Message.from_id;
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
                            private final /* synthetic */ MessageObject f$1;
                            private final /* synthetic */ TLRPC$KeyboardButton f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                                ChatActivityEnterView.this.lambda$didPressedBotButton$26$ChatActivityEnterView(this.f$1, this.f$2, dialogsActivity, arrayList, charSequence, z);
                            }
                        });
                        this.parentFragment.presentFragment(dialogsActivity);
                    }
                }
            }
            return true;
        }

        public /* synthetic */ void lambda$didPressedBotButton$25$ChatActivityEnterView(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, DialogInterface dialogInterface, int i) {
            if (Build.VERSION.SDK_INT < 23 || this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(messageObject, tLRPC$KeyboardButton);
                return;
            }
            this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            this.pendingMessageObject = messageObject;
            this.pendingLocationButton = tLRPC$KeyboardButton;
        }

        public /* synthetic */ void lambda$didPressedBotButton$26$ChatActivityEnterView(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            int i = tLRPC$Message.from_id;
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
            instance.saveDraft(longValue, "@" + user.username + " " + tLRPC$KeyboardButton.query, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$Message) null, true);
            if (longValue != this.dialog_id) {
                int i3 = (int) longValue;
                if (i3 != 0) {
                    Bundle bundle = new Bundle();
                    if (i3 > 0) {
                        bundle.putInt("user_id", i3);
                    } else if (i3 < 0) {
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
                AnonymousClass39 r1 = new EmojiView(this.allowStickers, this.allowGifs, this.parentActivity, true, this.info) {
                    public void setTranslationY(float f) {
                        super.setTranslationY(f);
                        if (ChatActivityEnterView.this.panelAnimation != null) {
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
                            ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(selectionEnd, replaceEmoji));
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

                    public void onStickerSelected(View view, TLRPC$Document tLRPC$Document, Object obj, boolean z, int i) {
                        if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                            if (ChatActivityEnterView.this.stickersExpanded) {
                                if (ChatActivityEnterView.this.searchingType != 0) {
                                    int unused = ChatActivityEnterView.this.searchingType = 0;
                                    ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(tLRPC$Document));
                                    ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                                }
                                ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                            }
                            ChatActivityEnterView.this.lambda$onStickerSelected$27$ChatActivityEnterView(tLRPC$Document, obj, false, z, i);
                            if (((int) ChatActivityEnterView.this.dialog_id) == 0 && MessageObject.isGifDocument(tLRPC$Document)) {
                                ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj, tLRPC$Document);
                            }
                        } else if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterViewDelegate access$1500 = ChatActivityEnterView.this.delegate;
                            if (view == null) {
                                view = ChatActivityEnterView.this.slowModeButton;
                            }
                            access$1500.onUpdateSlowModeButton(view, true, ChatActivityEnterView.this.slowModeButton.getText());
                        }
                    }

                    public void onStickersSettingsClick() {
                        if (ChatActivityEnterView.this.parentFragment != null) {
                            ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0));
                        }
                    }

                    /* renamed from: onGifSelected */
                    public void lambda$onGifSelected$0$ChatActivityEnterView$40(View view, Object obj, Object obj2, boolean z, int i) {
                        View view2 = view;
                        Object obj3 = obj;
                        Object obj4 = obj2;
                        int i2 = i;
                        if (isInScheduleMode() && i2 == 0) {
                            AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(view2, obj3, obj4) {
                                private final /* synthetic */ View f$1;
                                private final /* synthetic */ Object f$2;
                                private final /* synthetic */ Object f$3;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                }

                                public final void didSelectDate(boolean z, int i) {
                                    ChatActivityEnterView.AnonymousClass40.this.lambda$onGifSelected$0$ChatActivityEnterView$40(this.f$1, this.f$2, this.f$3, z, i);
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
                                SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(tLRPC$Document, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, obj2, z, i);
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
                                SendMessagesHelper.prepareSendingBotContextResult(ChatActivityEnterView.this.accountInstance, tLRPC$BotInlineResult, hashMap, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, z, i);
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
                            ChatActivityEnterViewDelegate access$1500 = ChatActivityEnterView.this.delegate;
                            if (view2 == null) {
                                view2 = ChatActivityEnterView.this.slowModeButton;
                            }
                            access$1500.onUpdateSlowModeButton(view2, true, ChatActivityEnterView.this.slowModeButton.getText());
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
                                    ChatActivityEnterView.AnonymousClass40.this.lambda$onClearEmojiRecent$1$ChatActivityEnterView$40(dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                        }
                    }

                    public /* synthetic */ void lambda$onClearEmojiRecent$1$ChatActivityEnterView$40(DialogInterface dialogInterface, int i) {
                        ChatActivityEnterView.this.emojiView.clearRecentEmoji();
                    }

                    public void onShowStickerSet(TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet) {
                        if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
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
                        ChatActivityEnterView.this.setStickersExpanded(i != 0, false, false);
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
                                int access$11500 = chatActivityEnterView2.stickersExpandedHeight;
                                int dp = AndroidUtilities.dp(120.0f);
                                Point point = AndroidUtilities.displaySize;
                                int unused4 = chatActivityEnterView2.stickersExpandedHeight = Math.min(access$11500, dp + (point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight));
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
                            int access$11600 = point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                            float max = (float) Math.max(Math.min(i + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - access$11600));
                            ChatActivityEnterView.this.emojiView.setTranslationY(max);
                            ChatActivityEnterView.this.setTranslationY(max);
                            ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                            float unused = chatActivityEnterView.stickersExpansionProgress = max / ((float) (-(chatActivityEnterView.stickersExpandedHeight - access$11600)));
                            ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                        }
                    }

                    private boolean allowDragging() {
                        return ChatActivityEnterView.this.stickersTabOpen && (ChatActivityEnterView.this.stickersExpanded || ChatActivityEnterView.this.messageEditText.length() <= 0) && ChatActivityEnterView.this.emojiView.areThereAnyStickers();
                    }
                });
                SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 1);
                checkChannelRights();
            }
        }

        /* renamed from: onStickerSelected */
        public void lambda$onStickerSelected$27$ChatActivityEnterView(TLRPC$Document tLRPC$Document, Object obj, boolean z, boolean z2, int i) {
            if (isInScheduleMode() && i == 0) {
                AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(tLRPC$Document, obj, z) {
                    private final /* synthetic */ TLRPC$Document f$1;
                    private final /* synthetic */ Object f$2;
                    private final /* synthetic */ boolean f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void didSelectDate(boolean z, int i) {
                        ChatActivityEnterView.this.lambda$onStickerSelected$27$ChatActivityEnterView(this.f$1, this.f$2, this.f$3, z, i);
                    }
                });
            } else if (this.slowModeTimer <= 0 || isInScheduleMode()) {
                if (this.searchingType != 0) {
                    this.searchingType = 0;
                    this.emojiView.closeSearch(true);
                    this.emojiView.hideSearchKeyboard();
                }
                setStickersExpanded(false, true, false);
                SendMessagesHelper.getInstance(this.currentAccount).sendSticker(tLRPC$Document, this.dialog_id, this.replyingMessageObject, obj, z2, i);
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, true, i);
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

        public void hideEmojiView() {
            EmojiView emojiView2;
            if (!this.emojiViewVisible && (emojiView2 = this.emojiView) != null && emojiView2.getVisibility() != 8) {
                this.sizeNotifierLayout.removeView(this.emojiView);
                this.emojiView.setVisibility(8);
            }
        }

        public void showEmojiView() {
            showPopup(1, 0);
        }

        /* access modifiers changed from: private */
        public void showPopup(final int i, int i2) {
            boolean z;
            boolean z2;
            if (i == 1) {
                if (i2 == 0 && this.emojiView == null) {
                    if (this.parentActivity != null) {
                        createEmojiView();
                    } else {
                        return;
                    }
                }
                View view = null;
                if (i2 == 0) {
                    if (this.emojiView.getParent() == null) {
                        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                        sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 1);
                    }
                    this.emojiView.setVisibility(0);
                    this.emojiViewVisible = true;
                    BotKeyboardView botKeyboardView2 = this.botKeyboardView;
                    if (botKeyboardView2 == null || botKeyboardView2.getVisibility() == 8) {
                        z = false;
                    } else {
                        this.botKeyboardView.setVisibility(8);
                        this.botKeyboardViewVisible = false;
                        z = true;
                    }
                    view = this.emojiView;
                } else if (i2 == 1) {
                    this.botKeyboardViewVisible = true;
                    EmojiView emojiView2 = this.emojiView;
                    if (emojiView2 == null || emojiView2.getVisibility() == 8) {
                        z2 = false;
                    } else {
                        this.sizeNotifierLayout.removeView(this.emojiView);
                        this.emojiView.setVisibility(8);
                        this.emojiViewVisible = false;
                        z2 = true;
                    }
                    this.botKeyboardView.setVisibility(0);
                    view = this.botKeyboardView;
                } else {
                    z = false;
                }
                this.currentPopupContentType = i2;
                if (this.keyboardHeight <= 0) {
                    this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
                }
                if (this.keyboardHeightLand <= 0) {
                    this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
                }
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
                if (i2 == 1) {
                    i3 = Math.min(this.botKeyboardView.getKeyboardHeight(), i3);
                }
                BotKeyboardView botKeyboardView3 = this.botKeyboardView;
                if (botKeyboardView3 != null) {
                    botKeyboardView3.setPanelHeight(i3);
                }
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams.height = i3;
                view.setLayoutParams(layoutParams);
                if (!AndroidUtilities.isInMultiwindow) {
                    AndroidUtilities.hideKeyboard(this.messageEditText);
                }
                SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
                if (sizeNotifierFrameLayout2 != null) {
                    this.emojiPadding = i3;
                    sizeNotifierFrameLayout2.requestLayout();
                    setEmojiButtonImage(true, true);
                    updateBotButton();
                    onWindowSizeChanged();
                    if (this.smoothKeyboard && !this.keyboardVisible && !z) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        this.panelAnimation = animatorSet;
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{(float) i3, 0.0f})});
                        this.panelAnimation.setInterpolator(Easings.easeOutQuad);
                        this.panelAnimation.setDuration(180);
                        this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = ChatActivityEnterView.this.panelAnimation = null;
                            }
                        });
                        this.panelAnimation.start();
                    }
                }
            } else {
                if (this.emojiButton != null) {
                    setEmojiButtonImage(false, true);
                }
                this.currentPopupContentType = -1;
                if (this.emojiView != null) {
                    this.emojiViewVisible = false;
                    if (i != 2 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                        if (this.smoothKeyboard) {
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            this.panelAnimation = animatorSet2;
                            EmojiView emojiView3 = this.emojiView;
                            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(emojiView3, View.TRANSLATION_Y, new float[]{(float) emojiView3.getMeasuredHeight()})});
                            this.panelAnimation.setInterpolator(Easings.easeOutQuad);
                            this.panelAnimation.setDuration(180);
                            this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (i == 0) {
                                        int unused = ChatActivityEnterView.this.emojiPadding = 0;
                                    }
                                    if (ChatActivityEnterView.this.emojiView != null) {
                                        ChatActivityEnterView.this.emojiView.setTranslationY(0.0f);
                                        ChatActivityEnterView.this.emojiView.setVisibility(8);
                                        ChatActivityEnterView.this.sizeNotifierLayout.removeView(ChatActivityEnterView.this.emojiView);
                                    }
                                    AnimatorSet unused2 = ChatActivityEnterView.this.panelAnimation = null;
                                }
                            });
                            this.panelAnimation.start();
                        } else {
                            this.sizeNotifierLayout.removeView(this.emojiView);
                            this.emojiView.setVisibility(8);
                        }
                    }
                }
                if (this.botKeyboardView != null) {
                    this.botKeyboardViewVisible = false;
                    if (i != 2 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                        if (this.smoothKeyboard) {
                            AnimatorSet animatorSet3 = new AnimatorSet();
                            this.panelAnimation = animatorSet3;
                            BotKeyboardView botKeyboardView4 = this.botKeyboardView;
                            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(botKeyboardView4, View.TRANSLATION_Y, new float[]{(float) botKeyboardView4.getMeasuredHeight()})});
                            this.panelAnimation.setInterpolator(Easings.easeOutQuad);
                            this.panelAnimation.setDuration(180);
                            this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (i == 0) {
                                        int unused = ChatActivityEnterView.this.emojiPadding = 0;
                                    }
                                    ChatActivityEnterView.this.botKeyboardView.setTranslationY(0.0f);
                                    ChatActivityEnterView.this.botKeyboardView.setVisibility(8);
                                    AnimatorSet unused2 = ChatActivityEnterView.this.panelAnimation = null;
                                }
                            });
                            this.panelAnimation.start();
                        } else {
                            this.botKeyboardView.setVisibility(8);
                        }
                    }
                }
                SizeNotifierFrameLayout sizeNotifierFrameLayout3 = this.sizeNotifierLayout;
                if (sizeNotifierFrameLayout3 != null && !this.smoothKeyboard && i == 0) {
                    this.emojiPadding = 0;
                    sizeNotifierFrameLayout3.requestLayout();
                    onWindowSizeChanged();
                }
                updateBotButton();
            }
            if (this.stickersTabOpen || this.emojiTabOpen) {
                checkSendButton(true);
            }
            if (this.stickersExpanded && i != 1) {
                setStickersExpanded(false, false, false);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:5:0x000a, code lost:
            if (r8.recordInterfaceState == 0) goto L_0x000c;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void setEmojiButtonImage(boolean r9, boolean r10) {
            /*
                r8 = this;
                r0 = 0
                if (r10 == 0) goto L_0x0008
                int r1 = r8.currentEmojiIcon
                r2 = -1
                if (r1 == r2) goto L_0x000c
            L_0x0008:
                int r1 = r8.recordInterfaceState
                if (r1 != 0) goto L_0x000d
            L_0x000c:
                r10 = 0
            L_0x000d:
                r1 = 3
                r2 = 2
                r3 = 1
                if (r9 == 0) goto L_0x0018
                int r9 = r8.currentPopupContentType
                if (r9 != 0) goto L_0x0018
                r9 = 0
                goto L_0x003d
            L_0x0018:
                org.telegram.ui.Components.EmojiView r9 = r8.emojiView
                if (r9 != 0) goto L_0x0027
                android.content.SharedPreferences r9 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
                java.lang.String r4 = "selected_page"
                int r9 = r9.getInt(r4, r0)
                goto L_0x002b
            L_0x0027:
                int r9 = r9.getCurrentPage()
            L_0x002b:
                if (r9 == 0) goto L_0x003c
                boolean r4 = r8.allowStickers
                if (r4 != 0) goto L_0x0036
                boolean r4 = r8.allowGifs
                if (r4 != 0) goto L_0x0036
                goto L_0x003c
            L_0x0036:
                if (r9 != r3) goto L_0x003a
                r9 = 2
                goto L_0x003d
            L_0x003a:
                r9 = 3
                goto L_0x003d
            L_0x003c:
                r9 = 1
            L_0x003d:
                int r4 = r8.currentEmojiIcon
                if (r4 != r9) goto L_0x0042
                return
            L_0x0042:
                android.animation.AnimatorSet r4 = r8.emojiButtonAnimation
                r5 = 0
                if (r4 == 0) goto L_0x004c
                r4.cancel()
                r8.emojiButtonAnimation = r5
            L_0x004c:
                if (r9 != 0) goto L_0x0059
                android.widget.ImageView[] r4 = r8.emojiButton
                r4 = r4[r10]
                r6 = 2131165509(0x7var_, float:1.7945237E38)
                r4.setImageResource(r6)
                goto L_0x007f
            L_0x0059:
                if (r9 != r3) goto L_0x0066
                android.widget.ImageView[] r4 = r8.emojiButton
                r4 = r4[r10]
                r6 = 2131165516(0x7var_c, float:1.7945251E38)
                r4.setImageResource(r6)
                goto L_0x007f
            L_0x0066:
                if (r9 != r2) goto L_0x0073
                android.widget.ImageView[] r4 = r8.emojiButton
                r4 = r4[r10]
                r6 = 2131165517(0x7var_d, float:1.7945253E38)
                r4.setImageResource(r6)
                goto L_0x007f
            L_0x0073:
                if (r9 != r1) goto L_0x007f
                android.widget.ImageView[] r4 = r8.emojiButton
                r4 = r4[r10]
                r6 = 2131165508(0x7var_, float:1.7945235E38)
                r4.setImageResource(r6)
            L_0x007f:
                android.widget.ImageView[] r4 = r8.emojiButton
                r4 = r4[r10]
                if (r9 != r2) goto L_0x0089
                java.lang.Integer r5 = java.lang.Integer.valueOf(r3)
            L_0x0089:
                r4.setTag(r5)
                r8.currentEmojiIcon = r9
                if (r10 == 0) goto L_0x0122
                android.widget.ImageView[] r9 = r8.emojiButton
                r9 = r9[r3]
                r9.setVisibility(r0)
                android.animation.AnimatorSet r9 = new android.animation.AnimatorSet
                r9.<init>()
                r8.emojiButtonAnimation = r9
                r10 = 6
                android.animation.Animator[] r10 = new android.animation.Animator[r10]
                android.widget.ImageView[] r4 = r8.emojiButton
                r4 = r4[r0]
                android.util.Property r5 = android.view.View.SCALE_X
                float[] r6 = new float[r3]
                r7 = 1036831949(0x3dcccccd, float:0.1)
                r6[r0] = r7
                android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
                r10[r0] = r4
                android.widget.ImageView[] r4 = r8.emojiButton
                r4 = r4[r0]
                android.util.Property r5 = android.view.View.SCALE_Y
                float[] r6 = new float[r3]
                r6[r0] = r7
                android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
                r10[r3] = r4
                android.widget.ImageView[] r4 = r8.emojiButton
                r4 = r4[r0]
                android.util.Property r5 = android.view.View.ALPHA
                float[] r6 = new float[r3]
                r7 = 0
                r6[r0] = r7
                android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
                r10[r2] = r4
                android.widget.ImageView[] r2 = r8.emojiButton
                r2 = r2[r3]
                android.util.Property r4 = android.view.View.SCALE_X
                float[] r5 = new float[r3]
                r6 = 1065353216(0x3var_, float:1.0)
                r5[r0] = r6
                android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r4, r5)
                r10[r1] = r2
                r1 = 4
                android.widget.ImageView[] r2 = r8.emojiButton
                r2 = r2[r3]
                android.util.Property r4 = android.view.View.SCALE_Y
                float[] r5 = new float[r3]
                r5[r0] = r6
                android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r4, r5)
                r10[r1] = r2
                r1 = 5
                android.widget.ImageView[] r2 = r8.emojiButton
                r2 = r2[r3]
                android.util.Property r4 = android.view.View.ALPHA
                float[] r3 = new float[r3]
                r3[r0] = r6
                android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r2, r4, r3)
                r10[r1] = r0
                r9.playTogether(r10)
                android.animation.AnimatorSet r9 = r8.emojiButtonAnimation
                org.telegram.ui.Components.ChatActivityEnterView$45 r10 = new org.telegram.ui.Components.ChatActivityEnterView$45
                r10.<init>()
                r9.addListener(r10)
                android.animation.AnimatorSet r9 = r8.emojiButtonAnimation
                r0 = 150(0x96, double:7.4E-322)
                r9.setDuration(r0)
                android.animation.AnimatorSet r9 = r8.emojiButtonAnimation
                r9.start()
            L_0x0122:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setEmojiButtonImage(boolean, boolean):void");
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
                this.emojiView.closeSearch(true);
                this.messageEditText.requestFocus();
                setStickersExpanded(false, true, false);
                if (this.emojiTabOpen) {
                    checkSendButton(true);
                }
            }
        }

        /* access modifiers changed from: private */
        public void openKeyboardInternal() {
            showPopup((AndroidUtilities.usingHardwareInput || this.isPaused) ? 0 : 2, 0);
            this.messageEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.messageEditText);
            if (this.isPaused) {
                this.showKeyboardOnResume = true;
            } else if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
                this.waitingForKeyboardOpen = true;
                AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
            }
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
            AndroidUtilities.showKeyboard(this.messageEditText);
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
            boolean z3 = true;
            if (this.searchingType != 0) {
                this.lastSizeChangeValue1 = i;
                this.lastSizeChangeValue2 = z;
                if (i <= 0) {
                    z3 = false;
                }
                this.keyboardVisible = z3;
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
            if (isPopupShowing()) {
                int i2 = z ? this.keyboardHeightLand : this.keyboardHeight;
                if (this.currentPopupContentType == 1 && !this.botKeyboardView.isFullSize()) {
                    i2 = Math.min(this.botKeyboardView.getKeyboardHeight(), i2);
                }
                View view = null;
                int i3 = this.currentPopupContentType;
                if (i3 == 0) {
                    view = this.emojiView;
                } else if (i3 == 1) {
                    view = this.botKeyboardView;
                }
                BotKeyboardView botKeyboardView2 = this.botKeyboardView;
                if (botKeyboardView2 != null) {
                    botKeyboardView2.setPanelHeight(i2);
                }
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                if (!this.closeAnimationInProgress && (!(layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == i2) && !this.stickersExpanded)) {
                    layoutParams.width = AndroidUtilities.displaySize.x;
                    layoutParams.height = i2;
                    view.setLayoutParams(layoutParams);
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                    if (sizeNotifierFrameLayout != null) {
                        this.emojiPadding = layoutParams.height;
                        sizeNotifierFrameLayout.requestLayout();
                        onWindowSizeChanged();
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
            if (z3 && isPopupShowing()) {
                showPopup(0, this.currentPopupContentType);
            }
            if (this.emojiPadding != 0 && !(z2 = this.keyboardVisible) && z2 != z4 && !isPopupShowing()) {
                this.emojiPadding = 0;
                this.sizeNotifierLayout.requestLayout();
            }
            if (this.keyboardVisible && this.waitingForKeyboardOpen) {
                this.waitingForKeyboardOpen = false;
                AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
            }
            onWindowSizeChanged();
        }

        public int getEmojiPadding() {
            return this.emojiPadding;
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            TLRPC$ChatFull tLRPC$ChatFull;
            TLRPC$Chat chat;
            int i3;
            if (i == NotificationCenter.emojiDidLoad) {
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
                        ImageView imageView = this.videoSendButton;
                        messagesController.sendTyping(j, (imageView == null || imageView.getTag() == null) ? 1 : 7, 0);
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
                int i5 = 3;
                if (i == NotificationCenter.recordStartError || i == NotificationCenter.recordStopped) {
                    if (objArr[0].intValue() == this.recordingGuid) {
                        if (this.recordingAudioVideo) {
                            this.recordingAudioVideo = false;
                            if (i == NotificationCenter.recordStopped) {
                                Integer num = objArr[1];
                                if (num.intValue() == 4) {
                                    i5 = 4;
                                } else if (isInVideoMode() && num.intValue() == 5) {
                                    i5 = 1;
                                } else if (num.intValue() == 0) {
                                    i5 = 5;
                                } else if (num.intValue() == 6) {
                                    i5 = 2;
                                }
                                updateRecordIntefrace(i5);
                            } else {
                                updateRecordIntefrace(2);
                            }
                        }
                        if (i == NotificationCenter.recordStopped) {
                            Integer num2 = objArr[1];
                            if (num2.intValue() == 2) {
                                this.videoTimelineView.setVisibility(0);
                                this.recordedAudioBackground.setVisibility(8);
                                this.recordedAudioTimeTextView.setVisibility(8);
                                this.recordedAudioPlayButton.setVisibility(8);
                                this.recordedAudioSeekBar.setVisibility(8);
                                this.recordedAudioPanel.setAlpha(1.0f);
                                this.recordedAudioPanel.setVisibility(0);
                                this.recordDeleteImageView.setProgress(0.0f);
                                this.recordDeleteImageView.stopAnimation();
                                return;
                            }
                            num2.intValue();
                        }
                    }
                } else if (i == NotificationCenter.recordStarted) {
                    if (objArr[0].intValue() == this.recordingGuid) {
                        this.recordCircle.showWaves(true, true);
                        this.recordTimerView.start();
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
                            this.recordedAudioBackground.setVisibility(8);
                            this.recordedAudioTimeTextView.setVisibility(8);
                            this.recordedAudioPlayButton.setVisibility(8);
                            this.recordedAudioSeekBar.setVisibility(8);
                            this.recordedAudioPanel.setAlpha(1.0f);
                            this.recordedAudioPanel.setVisibility(0);
                            this.recordDeleteImageView.setProgress(0.0f);
                            this.recordDeleteImageView.stopAnimation();
                            closeKeyboard();
                            hidePopup(false);
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
                            this.videoTimelineView.setVisibility(8);
                            this.recordedAudioBackground.setVisibility(0);
                            this.recordedAudioTimeTextView.setVisibility(0);
                            this.recordedAudioPlayButton.setVisibility(0);
                            this.recordedAudioSeekBar.setVisibility(0);
                            TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                            tLRPC$TL_message.out = true;
                            tLRPC$TL_message.id = 0;
                            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                            tLRPC$TL_message.to_id = tLRPC$TL_peerUser;
                            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                            tLRPC$TL_message.from_id = clientUserId;
                            tLRPC$TL_peerUser.user_id = clientUserId;
                            tLRPC$TL_message.date = (int) (System.currentTimeMillis() / 1000);
                            tLRPC$TL_message.message = "";
                            tLRPC$TL_message.attachPath = this.audioToSendPath;
                            TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument = new TLRPC$TL_messageMediaDocument();
                            tLRPC$TL_message.media = tLRPC$TL_messageMediaDocument;
                            tLRPC$TL_messageMediaDocument.flags |= 3;
                            tLRPC$TL_messageMediaDocument.document = this.audioToSend;
                            tLRPC$TL_message.flags |= 768;
                            this.audioToSendMessageObject = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, false);
                            this.recordedAudioPanel.setAlpha(1.0f);
                            this.recordedAudioPanel.setVisibility(0);
                            this.recordDeleteImageView.setProgress(0.0f);
                            this.recordDeleteImageView.stopAnimation();
                            int i6 = 0;
                            while (true) {
                                if (i6 >= this.audioToSend.attributes.size()) {
                                    i3 = 0;
                                    break;
                                }
                                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.audioToSend.attributes.get(i6);
                                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                                    i3 = tLRPC$DocumentAttribute.duration;
                                    break;
                                }
                                i6++;
                            }
                            int i7 = 0;
                            while (true) {
                                if (i7 >= this.audioToSend.attributes.size()) {
                                    break;
                                }
                                TLRPC$DocumentAttribute tLRPC$DocumentAttribute2 = this.audioToSend.attributes.get(i7);
                                if (tLRPC$DocumentAttribute2 instanceof TLRPC$TL_documentAttributeAudio) {
                                    byte[] bArr = tLRPC$DocumentAttribute2.waveform;
                                    if (bArr == null || bArr.length == 0) {
                                        tLRPC$DocumentAttribute2.waveform = MediaController.getInstance().getWaveform(this.audioToSendPath);
                                    }
                                    this.recordedAudioSeekBar.setWaveform(tLRPC$DocumentAttribute2.waveform);
                                } else {
                                    i7++;
                                }
                            }
                            this.recordedAudioTimeTextView.setText(AndroidUtilities.formatShortDuration(i3));
                            closeKeyboard();
                            hidePopup(false);
                            checkSendButton(false);
                        }
                    }
                } else if (i == NotificationCenter.audioRouteChanged) {
                    if (this.parentActivity != null) {
                        boolean booleanValue = objArr[0].booleanValue();
                        Activity activity = this.parentActivity;
                        if (!booleanValue) {
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
                    Integer num3 = objArr[0];
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
                } else if (i == NotificationCenter.sendingMessagesChanged && this.info != null) {
                    updateSlowModeText();
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
                            ChatActivityEnterView.this.lambda$checkStickresExpandHeight$28$ChatActivityEnterView(valueAnimator);
                        }
                    });
                    animatorSet.setDuration(400);
                    animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
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
                        ChatActivityEnterView.this.lambda$checkStickresExpandHeight$29$ChatActivityEnterView(valueAnimator);
                    }
                });
                animatorSet2.setDuration(400);
                animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
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

        public /* synthetic */ void lambda$checkStickresExpandHeight$28$ChatActivityEnterView(ValueAnimator valueAnimator) {
            this.sizeNotifierLayout.invalidate();
        }

        public /* synthetic */ void lambda$checkStickresExpandHeight$29$ChatActivityEnterView(ValueAnimator valueAnimator) {
            this.sizeNotifierLayout.invalidate();
        }

        /* access modifiers changed from: private */
        public void setStickersExpanded(boolean z, boolean z2, boolean z3) {
            boolean z4 = z;
            if (this.emojiView == null) {
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
                        animatorSet.setDuration(400);
                        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                        ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener(i) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ChatActivityEnterView.this.lambda$setStickersExpanded$30$ChatActivityEnterView(this.f$1, valueAnimator);
                            }
                        });
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                Animator unused = ChatActivityEnterView.this.stickersExpansionAnim = null;
                                ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                            }
                        });
                        this.stickersExpansionAnim = animatorSet;
                        this.emojiView.setLayerType(2, (Paint) null);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
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
                        animatorSet2.setDuration(400);
                        animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                        ((ObjectAnimator) animatorSet2.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener(i) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ChatActivityEnterView.this.lambda$setStickersExpanded$31$ChatActivityEnterView(this.f$1, valueAnimator);
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
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                            }
                        });
                        this.stickersExpansionAnim = animatorSet2;
                        this.emojiView.setLayerType(2, (Paint) null);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
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

        public /* synthetic */ void lambda$setStickersExpanded$30$ChatActivityEnterView(int i, ValueAnimator valueAnimator) {
            this.stickersExpansionProgress = getTranslationY() / ((float) (-(this.stickersExpandedHeight - i)));
            this.sizeNotifierLayout.invalidate();
        }

        public /* synthetic */ void lambda$setStickersExpanded$31$ChatActivityEnterView(int i, ValueAnimator valueAnimator) {
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
                if (this.cancelToProgress == 0.0f) {
                    return false;
                }
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                    setPressed(false);
                }
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
                if (this.slideToLayout != null && (staticLayout = this.cancelLayout) != null) {
                    int width = staticLayout.getWidth() + AndroidUtilities.dp(16.0f);
                    this.selectableBackground.setBounds((getMeasuredWidth() / 2) - width, (getMeasuredHeight() / 2) - width, (getMeasuredWidth() / 2) + width, (getMeasuredHeight() / 2) + width);
                    this.selectableBackground.draw(canvas);
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
                        canvas.clipRect(ChatActivityEnterView.this.recordTimerView.getLeftProperty() + ((float) AndroidUtilities.dp(4.0f)), 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                        canvas.save();
                        int i2 = (int) dp3;
                        canvas.translate((float) ((i2 - AndroidUtilities.dp(this.smallSize ? 7.0f : 10.0f)) + i), f);
                        canvas.drawPath(this.arrowPath, this.arrowPaint);
                        canvas.restore();
                        canvas.save();
                        canvas.translate((float) (i2 + i), (((float) (getMeasuredHeight() - this.slideToLayout.getHeight())) / 2.0f) + f);
                        this.slideToLayout.draw(canvas);
                        canvas.restore();
                        canvas.restore();
                    }
                    if (this.cancelToProgress > 0.0f) {
                        float measuredHeight = ((float) (getMeasuredHeight() - this.cancelLayout.getHeight())) / 2.0f;
                        if (!z) {
                            measuredHeight -= ((float) AndroidUtilities.dp(12.0f)) - f;
                        }
                        float f5 = z ? dp3 + primaryHorizontal : (float) measuredWidth2;
                        canvas.save();
                        canvas.translate(f5, measuredHeight);
                        this.cancelRect.set((int) f5, (int) measuredHeight, (int) (f5 + ((float) this.cancelLayout.getWidth())), (int) (measuredHeight + ((float) this.cancelLayout.getHeight())));
                        this.cancelRect.inset(-AndroidUtilities.dp(16.0f), -AndroidUtilities.dp(16.0f));
                        this.cancelLayout.draw(canvas);
                        canvas.restore();
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
            float left;
            String oldString;
            StaticLayout outLayout;
            final float replaceDistance = ((float) AndroidUtilities.dp(15.0f));
            SpannableStringBuilder replaceIn = new SpannableStringBuilder();
            SpannableStringBuilder replaceOut = new SpannableStringBuilder();
            SpannableStringBuilder replaceStable = new SpannableStringBuilder();
            float replaceTransition;
            long startTime;
            long stopTime;
            final TextPaint textPaint = new TextPaint(1);

            public TimerView(Context context) {
                super(context);
                this.textPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
                this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                updateColors();
            }

            public void start() {
                this.isRunning = true;
                this.startTime = System.currentTimeMillis();
                invalidate();
            }

            public void stop() {
                this.isRunning = false;
                if (this.startTime > 0) {
                    this.stopTime = System.currentTimeMillis();
                }
                invalidate();
            }

            /* access modifiers changed from: protected */
            @SuppressLint({"DrawAllocation"})
            public void onDraw(Canvas canvas) {
                String str;
                String str2;
                Canvas canvas2 = canvas;
                long currentTimeMillis = (this.isRunning ? System.currentTimeMillis() : this.stopTime) - this.startTime;
                long j = currentTimeMillis / 1000;
                int i = ((int) (currentTimeMillis % 1000)) / 10;
                if (currentTimeMillis >= 59500) {
                    float unused = ChatActivityEnterView.this.startedDraggingX = -1.0f;
                    ChatActivityEnterView.this.delegate.needStartRecordVideo(3, true, 0);
                }
                long j2 = j / 60;
                if (j2 >= 60) {
                    str = String.format(Locale.US, "%01d:%02d:%02d,%d", new Object[]{Long.valueOf(j2 / 60), Long.valueOf(j2 % 60), Long.valueOf(j % 60), Integer.valueOf(i / 10)});
                } else {
                    str = String.format(Locale.US, "%01d:%02d,%d", new Object[]{Long.valueOf(j2), Long.valueOf(j % 60), Integer.valueOf(i / 10)});
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
            }
        }

        private static class EmptyStubSpan extends ReplacementSpan {
            public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            }

            private EmptyStubSpan() {
            }

            public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                return (int) paint.measureText(charSequence, i, i2);
            }
        }
    }
