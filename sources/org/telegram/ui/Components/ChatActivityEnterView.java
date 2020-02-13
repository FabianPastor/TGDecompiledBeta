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
import android.graphics.Paint;
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
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
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
import org.telegram.tgnet.TLRPC;
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
    private TLRPC.TL_document audioToSend;
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
    private BotKeyboardView botKeyboardView;
    private boolean botKeyboardViewVisible;
    private MessageObject botMessageObject;
    private TLRPC.TL_replyKeyboardMarkup botReplyMarkup;
    /* access modifiers changed from: private */
    public boolean calledRecordRunnable;
    /* access modifiers changed from: private */
    public Drawable cameraDrawable;
    /* access modifiers changed from: private */
    public boolean canWriteToChannel;
    /* access modifiers changed from: private */
    public ImageView cancelBotButton;
    /* access modifiers changed from: private */
    public boolean closeAnimationInProgress;
    /* access modifiers changed from: private */
    public int currentAccount;
    private int currentEmojiIcon = -1;
    /* access modifiers changed from: private */
    public int currentPopupContentType = -1;
    private Animator currentResizeAnimation;
    /* access modifiers changed from: private */
    public AnimatorSet currentTopViewAnimation;
    /* access modifiers changed from: private */
    public ChatActivityEnterViewDelegate delegate;
    /* access modifiers changed from: private */
    public boolean destroyed;
    /* access modifiers changed from: private */
    public long dialog_id;
    private float distCanMove = ((float) AndroidUtilities.dp(80.0f));
    /* access modifiers changed from: private */
    public AnimatorSet doneButtonAnimation;
    private FrameLayout doneButtonContainer;
    /* access modifiers changed from: private */
    public ImageView doneButtonImage;
    /* access modifiers changed from: private */
    public ContextProgressView doneButtonProgress;
    /* access modifiers changed from: private */
    public Paint dotPaint = new Paint(1);
    private boolean editingCaption;
    /* access modifiers changed from: private */
    public MessageObject editingMessageObject;
    private int editingMessageReqId;
    /* access modifiers changed from: private */
    public ImageView[] emojiButton = new ImageView[2];
    private ImageView emojiButton1;
    private ImageView emojiButton2;
    /* access modifiers changed from: private */
    public AnimatorSet emojiButtonAnimation;
    private int emojiPadding;
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
    private boolean gifsTabOpen;
    private boolean hasBotCommands;
    private boolean hasRecordVideo;
    /* access modifiers changed from: private */
    public boolean ignoreTextChange;
    /* access modifiers changed from: private */
    public Drawable inactinveSendButtonDrawable;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public int innerTextChange;
    private boolean isPaused = true;
    /* access modifiers changed from: private */
    public int keyboardHeight;
    /* access modifiers changed from: private */
    public int keyboardHeightLand;
    /* access modifiers changed from: private */
    public boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private String lastTimeString;
    private long lastTypingSendTime;
    /* access modifiers changed from: private */
    public long lastTypingTimeSend;
    /* access modifiers changed from: private */
    public Drawable lockArrowDrawable;
    /* access modifiers changed from: private */
    public Drawable lockBackgroundDrawable;
    /* access modifiers changed from: private */
    public Drawable lockDrawable;
    /* access modifiers changed from: private */
    public Drawable lockShadowDrawable;
    /* access modifiers changed from: private */
    public Drawable lockTopDrawable;
    private View.AccessibilityDelegate mediaMessageButtonsDelegate = new View.AccessibilityDelegate() {
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.widget.ImageButton");
            accessibilityNodeInfo.setClickable(true);
            accessibilityNodeInfo.setLongClickable(true);
        }
    };
    /* access modifiers changed from: private */
    public EditTextCaption messageEditText;
    private TLRPC.WebPage messageWebPage;
    /* access modifiers changed from: private */
    public boolean messageWebPageSearch = true;
    /* access modifiers changed from: private */
    public Drawable micDrawable;
    private boolean needShowTopView;
    /* access modifiers changed from: private */
    public ImageView notifyButton;
    /* access modifiers changed from: private */
    public Runnable onFinishInitCameraRunnable = new Runnable() {
        public void run() {
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.needStartRecordVideo(0, true, 0);
            }
        }
    };
    /* access modifiers changed from: private */
    public Runnable openKeyboardRunnable = new Runnable() {
        public void run() {
            if (!ChatActivityEnterView.this.destroyed && ChatActivityEnterView.this.messageEditText != null && ChatActivityEnterView.this.waitingForKeyboardOpen && !ChatActivityEnterView.this.keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow) {
                ChatActivityEnterView.this.messageEditText.requestFocus();
                AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
                AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100);
            }
        }
    };
    private int originalViewHeight;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public Paint paintRecord = new Paint(1);
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public ChatActivity parentFragment;
    private Drawable pauseDrawable;
    private TLRPC.KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    private Drawable playDrawable;
    private CloseProgressDrawable2 progressDrawable;
    private Runnable recordAudioVideoRunnable = new Runnable() {
        public void run() {
            if (ChatActivityEnterView.this.delegate != null && ChatActivityEnterView.this.parentActivity != null) {
                ChatActivityEnterView.this.delegate.onPreAudioVideoRecord();
                boolean unused = ChatActivityEnterView.this.calledRecordRunnable = true;
                boolean unused2 = ChatActivityEnterView.this.recordAudioVideoRunnableStarted = false;
                ChatActivityEnterView.this.recordCircle.setLockTranslation(10000.0f);
                ChatActivityEnterView.this.recordSendText.setAlpha(0.0f);
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
                } else if (ChatActivityEnterView.this.parentFragment == null || Build.VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                    ChatActivityEnterView.this.delegate.needStartRecordAudio(1);
                    float unused3 = ChatActivityEnterView.this.startedDraggingX = -1.0f;
                    MediaController.getInstance().startRecording(ChatActivityEnterView.this.currentAccount, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.recordingGuid);
                    ChatActivityEnterView.this.updateRecordIntefrace();
                    ChatActivityEnterView.this.audioVideoButtonContainer.getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean recordAudioVideoRunnableStarted;
    private ImageView recordCancelImage;
    private TextView recordCancelText;
    /* access modifiers changed from: private */
    public RecordCircle recordCircle;
    private Property<RecordCircle, Float> recordCircleScale = new Property<RecordCircle, Float>(Float.class, "scale") {
        public Float get(RecordCircle recordCircle) {
            return Float.valueOf(recordCircle.getScale());
        }

        public void set(RecordCircle recordCircle, Float f) {
            recordCircle.setScale(f.floatValue());
        }
    };
    private ImageView recordDeleteImageView;
    private RecordDot recordDot;
    private int recordInterfaceState;
    /* access modifiers changed from: private */
    public FrameLayout recordPanel;
    /* access modifiers changed from: private */
    public TextView recordSendText;
    private LinearLayout recordTimeContainer;
    private TextView recordTimeText;
    private View recordedAudioBackground;
    /* access modifiers changed from: private */
    public FrameLayout recordedAudioPanel;
    private ImageView recordedAudioPlayButton;
    private SeekBarWaveformView recordedAudioSeekBar;
    private TextView recordedAudioTimeTextView;
    private boolean recordingAudioVideo;
    /* access modifiers changed from: private */
    public int recordingGuid;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public Paint redDotPaint = new Paint(1);
    /* access modifiers changed from: private */
    public MessageObject replyingMessageObject;
    private Property<View, Integer> roundedTranslationYProperty = new Property<View, Integer>(Integer.class, "translationY") {
        public Integer get(View view) {
            return Integer.valueOf(Math.round(view.getTranslationY()));
        }

        public void set(View view, Integer num) {
            view.setTranslationY((float) num.intValue());
        }
    };
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
    private boolean showKeyboardOnResume;
    /* access modifiers changed from: private */
    public boolean silent;
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout sizeNotifierLayout;
    /* access modifiers changed from: private */
    public LinearLayout slideText;
    /* access modifiers changed from: private */
    public SimpleTextView slowModeButton;
    /* access modifiers changed from: private */
    public int slowModeTimer;
    /* access modifiers changed from: private */
    public float startedDraggingX = -1.0f;
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
    private LinearLayout textFieldContainer;
    /* access modifiers changed from: private */
    public View topLineView;
    /* access modifiers changed from: private */
    public View topView;
    private boolean topViewShowed;
    /* access modifiers changed from: private */
    public Runnable updateExpandabilityRunnable = new Runnable() {
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
    private Runnable updateSlowModeRunnable;
    /* access modifiers changed from: private */
    public ImageView videoSendButton;
    private VideoTimelineView videoTimelineView;
    /* access modifiers changed from: private */
    public VideoEditedInfo videoToSendMessageObject;
    /* access modifiers changed from: private */
    public boolean waitingForKeyboardOpen;
    private PowerManager.WakeLock wakeLock;

    public interface ChatActivityEnterViewDelegate {

        /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$hasScheduledMessages(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
                return true;
            }

            public static void $default$openScheduledMessages(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
            }

            public static void $default$scrollToSendingMessage(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
            }
        }

        void didPressedAttachButton();

        boolean hasScheduledMessages();

        void needChangeVideoPreviewState(int i, float f);

        void needSendTyping();

        void needShowMediaBanHint();

        void needStartRecordAudio(int i);

        void needStartRecordVideo(int i, boolean z, int i2);

        void onAttachButtonHidden();

        void onAttachButtonShow();

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
        private boolean isIncr;
        private long lastUpdateTime;

        public RecordDot(Context context) {
            super(context);
            ChatActivityEnterView.this.redDotPaint.setColor(Theme.getColor("chat_recordedVoiceDot"));
        }

        public void resetAlpha() {
            this.alpha = 1.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            this.isIncr = false;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            ChatActivityEnterView.this.redDotPaint.setAlpha((int) (this.alpha * 255.0f));
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            if (!this.isIncr) {
                this.alpha -= ((float) currentTimeMillis) / 400.0f;
                if (this.alpha <= 0.0f) {
                    this.alpha = 0.0f;
                    this.isIncr = true;
                }
            } else {
                this.alpha += ((float) currentTimeMillis) / 400.0f;
                if (this.alpha >= 1.0f) {
                    this.alpha = 1.0f;
                    this.isIncr = false;
                }
            }
            this.lastUpdateTime = System.currentTimeMillis();
            canvas.drawCircle((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.redDotPaint);
            invalidate();
        }
    }

    private class RecordCircle extends View {
        private float amplitude;
        private float animateAmplitudeDiff;
        private float animateToAmplitude;
        private long lastUpdateTime;
        private float lockAnimatedTranslation;
        private boolean pressed;
        private float scale;
        private boolean sendButtonVisible;
        /* access modifiers changed from: private */
        public float startTranslation;
        private VirtualViewHelper virtualViewHelper = new VirtualViewHelper(this);

        public RecordCircle(Context context) {
            super(context);
            ChatActivityEnterView.this.paint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
            ChatActivityEnterView.this.paintRecord.setColor(Theme.getColor("chat_messagePanelVoiceShadow"));
            Drawable unused = ChatActivityEnterView.this.lockDrawable = getResources().getDrawable(NUM);
            ChatActivityEnterView.this.lockDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("key_chat_messagePanelVoiceLock"), PorterDuff.Mode.MULTIPLY));
            Drawable unused2 = ChatActivityEnterView.this.lockTopDrawable = getResources().getDrawable(NUM);
            ChatActivityEnterView.this.lockTopDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("key_chat_messagePanelVoiceLock"), PorterDuff.Mode.MULTIPLY));
            Drawable unused3 = ChatActivityEnterView.this.lockArrowDrawable = getResources().getDrawable(NUM);
            ChatActivityEnterView.this.lockArrowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("key_chat_messagePanelVoiceLock"), PorterDuff.Mode.MULTIPLY));
            Drawable unused4 = ChatActivityEnterView.this.lockBackgroundDrawable = getResources().getDrawable(NUM);
            ChatActivityEnterView.this.lockBackgroundDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("key_chat_messagePanelVoiceLockBackground"), PorterDuff.Mode.MULTIPLY));
            Drawable unused5 = ChatActivityEnterView.this.lockShadowDrawable = getResources().getDrawable(NUM);
            ChatActivityEnterView.this.lockShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("key_chat_messagePanelVoiceLockShadow"), PorterDuff.Mode.MULTIPLY));
            Drawable unused6 = ChatActivityEnterView.this.micDrawable = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.micDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            Drawable unused7 = ChatActivityEnterView.this.cameraDrawable = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.cameraDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            Drawable unused8 = ChatActivityEnterView.this.sendDrawable = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.sendDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            ViewCompat.setAccessibilityDelegate(this, this.virtualViewHelper);
        }

        public void setAmplitude(double d) {
            this.animateToAmplitude = ((float) Math.min(100.0d, d)) / 100.0f;
            this.animateAmplitudeDiff = (this.animateToAmplitude - this.amplitude) / 150.0f;
            this.lastUpdateTime = System.currentTimeMillis();
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
                return 0;
            } else if (this.sendButtonVisible) {
                return 2;
            } else {
                if (this.lockAnimatedTranslation == -1.0f) {
                    this.startTranslation = f;
                }
                this.lockAnimatedTranslation = f;
                invalidate();
                if (this.startTranslation - this.lockAnimatedTranslation < ((float) AndroidUtilities.dp(57.0f))) {
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
                    boolean contains = ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains(x, y);
                    this.pressed = contains;
                    return contains;
                } else if (this.pressed) {
                    if (motionEvent.getAction() == 1 && ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains(x, y)) {
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
        public void onDraw(Canvas canvas) {
            float f;
            float f2;
            float f3;
            Drawable drawable;
            int i;
            int i2;
            int i3;
            int i4;
            int i5;
            Canvas canvas2 = canvas;
            int measuredWidth = getMeasuredWidth() / 2;
            int dp = AndroidUtilities.dp(170.0f);
            float f4 = this.lockAnimatedTranslation;
            if (f4 != 10000.0f) {
                f = (float) Math.max(0, (int) (this.startTranslation - f4));
                if (f > ((float) AndroidUtilities.dp(57.0f))) {
                    f = (float) AndroidUtilities.dp(57.0f);
                }
            } else {
                f = 0.0f;
            }
            int i6 = (int) (((float) dp) - f);
            float f5 = this.scale;
            if (f5 <= 0.5f) {
                f3 = f5 / 0.5f;
                f2 = f3;
            } else {
                f3 = f5 <= 0.75f ? 1.0f - (((f5 - 0.5f) / 0.25f) * 0.1f) : (((f5 - 0.75f) / 0.25f) * 0.1f) + 0.9f;
                f2 = 1.0f;
            }
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            float f6 = this.animateToAmplitude;
            float f7 = this.amplitude;
            if (f6 != f7) {
                float f8 = this.animateAmplitudeDiff;
                this.amplitude = f7 + (((float) currentTimeMillis) * f8);
                if (f8 > 0.0f) {
                    if (this.amplitude > f6) {
                        this.amplitude = f6;
                    }
                } else if (this.amplitude < f6) {
                    this.amplitude = f6;
                }
                invalidate();
            }
            this.lastUpdateTime = System.currentTimeMillis();
            if (this.amplitude != 0.0f) {
                canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) i6, (((float) AndroidUtilities.dp(42.0f)) + (((float) AndroidUtilities.dp(20.0f)) * this.amplitude)) * this.scale, ChatActivityEnterView.this.paintRecord);
            }
            canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) i6, ((float) AndroidUtilities.dp(42.0f)) * f3, ChatActivityEnterView.this.paint);
            if (isSendButtonVisible()) {
                drawable = ChatActivityEnterView.this.sendDrawable;
            } else {
                drawable = (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
            }
            drawable.setBounds(measuredWidth - (drawable.getIntrinsicWidth() / 2), i6 - (drawable.getIntrinsicHeight() / 2), (drawable.getIntrinsicWidth() / 2) + measuredWidth, i6 + (drawable.getIntrinsicHeight() / 2));
            int i7 = (int) (f2 * 255.0f);
            drawable.setAlpha(i7);
            drawable.draw(canvas2);
            float dp2 = 1.0f - (f / ((float) AndroidUtilities.dp(57.0f)));
            float max = Math.max(0.0f, 1.0f - ((f / ((float) AndroidUtilities.dp(57.0f))) * 2.0f));
            if (isSendButtonVisible()) {
                i3 = AndroidUtilities.dp(31.0f);
                i = AndroidUtilities.dp(57.0f) + ((int) (((((float) AndroidUtilities.dp(30.0f)) * (1.0f - f3)) - f) + (((float) AndroidUtilities.dp(20.0f)) * dp2)));
                i4 = AndroidUtilities.dp(11.0f) + i;
                i2 = AndroidUtilities.dp(25.0f) + i;
                int dp3 = (int) (((float) i7) * (f / ((float) AndroidUtilities.dp(57.0f))));
                ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(255);
                ChatActivityEnterView.this.lockShadowDrawable.setAlpha(255);
                ChatActivityEnterView.this.lockTopDrawable.setAlpha(dp3);
                ChatActivityEnterView.this.lockDrawable.setAlpha(dp3);
                ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) dp3) * max));
                i5 = AndroidUtilities.dp(5.0f) + i;
            } else {
                i3 = AndroidUtilities.dp(31.0f) + ((int) (((float) AndroidUtilities.dp(29.0f)) * dp2));
                i = (AndroidUtilities.dp(57.0f) + ((int) (((float) AndroidUtilities.dp(30.0f)) * (1.0f - f3)))) - ((int) f);
                i5 = AndroidUtilities.dp(5.0f) + i + ((int) (((float) AndroidUtilities.dp(4.0f)) * dp2));
                i4 = ((int) (((float) AndroidUtilities.dp(10.0f)) * dp2)) + AndroidUtilities.dp(11.0f) + i;
                i2 = AndroidUtilities.dp(25.0f) + i + ((int) (((float) AndroidUtilities.dp(16.0f)) * dp2));
                ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(i7);
                ChatActivityEnterView.this.lockShadowDrawable.setAlpha(i7);
                ChatActivityEnterView.this.lockTopDrawable.setAlpha(i7);
                ChatActivityEnterView.this.lockDrawable.setAlpha(i7);
                ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) i7) * max));
            }
            int i8 = i3 + i;
            ChatActivityEnterView.this.lockBackgroundDrawable.setBounds(measuredWidth - AndroidUtilities.dp(15.0f), i, AndroidUtilities.dp(15.0f) + measuredWidth, i8);
            ChatActivityEnterView.this.lockBackgroundDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockShadowDrawable.setBounds(measuredWidth - AndroidUtilities.dp(16.0f), i - AndroidUtilities.dp(1.0f), AndroidUtilities.dp(16.0f) + measuredWidth, i8 + AndroidUtilities.dp(1.0f));
            ChatActivityEnterView.this.lockShadowDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockTopDrawable.setBounds(measuredWidth - AndroidUtilities.dp(6.0f), i5, AndroidUtilities.dp(6.0f) + measuredWidth, AndroidUtilities.dp(14.0f) + i5);
            ChatActivityEnterView.this.lockTopDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockDrawable.setBounds(measuredWidth - AndroidUtilities.dp(7.0f), i4, AndroidUtilities.dp(7.0f) + measuredWidth, AndroidUtilities.dp(12.0f) + i4);
            ChatActivityEnterView.this.lockDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockArrowDrawable.setBounds(measuredWidth - AndroidUtilities.dp(7.5f), i2, AndroidUtilities.dp(7.5f) + measuredWidth, AndroidUtilities.dp(9.0f) + i2);
            ChatActivityEnterView.this.lockArrowDrawable.draw(canvas2);
            if (isSendButtonVisible()) {
                ChatActivityEnterView.this.redDotPaint.setAlpha(255);
                ChatActivityEnterView.this.rect.set((float) (measuredWidth - AndroidUtilities.dp2(6.5f)), (float) (AndroidUtilities.dp(9.0f) + i), (float) (measuredWidth + AndroidUtilities.dp(6.5f)), (float) (i + AndroidUtilities.dp(22.0f)));
                canvas2.drawRoundRect(ChatActivityEnterView.this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ChatActivityEnterView.this.redDotPaint);
            }
        }

        /* access modifiers changed from: protected */
        public boolean dispatchHoverEvent(MotionEvent motionEvent) {
            return super.dispatchHoverEvent(motionEvent) || this.virtualViewHelper.dispatchHoverEvent(motionEvent);
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
                int i = (int) f;
                int i2 = (int) f2;
                if (ChatActivityEnterView.this.sendDrawable.getBounds().contains(i, i2)) {
                    return 1;
                }
                return ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains(i, i2) ? 2 : -1;
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
                    accessibilityNodeInfoCompat.setBoundsInParent(ChatActivityEnterView.this.sendDrawable.getBounds());
                    accessibilityNodeInfoCompat.setText(LocaleController.getString("Send", NUM));
                } else if (i == 2) {
                    accessibilityNodeInfoCompat.setBoundsInParent(ChatActivityEnterView.this.lockBackgroundDrawable.getBounds());
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
        Activity activity2 = activity;
        ChatActivity chatActivity2 = chatActivity;
        int i2 = UserConfig.selectedAccount;
        this.currentAccount = i2;
        this.accountInstance = AccountInstance.getInstance(i2);
        this.dotPaint.setColor(Theme.getColor("chat_emojiPanelNewTrending"));
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
            this.recordingGuid = this.parentFragment.getClassGuid();
        }
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        this.sizeNotifierLayout.setDelegate(this);
        this.sendByEnter = MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false);
        this.textFieldContainer = new LinearLayout(activity2);
        this.textFieldContainer.setOrientation(0);
        this.textFieldContainer.setClipChildren(false);
        this.textFieldContainer.setClipToPadding(false);
        addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 2.0f, 0.0f, 0.0f));
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
        this.textFieldContainer.addView(r2, LayoutHelper.createLinear(0, -2, 1.0f, 80));
        for (int i3 = 0; i3 < 2; i3++) {
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
                this.emojiButton2 = this.emojiButton[i3];
            } else {
                this.emojiButton1 = this.emojiButton[i3];
            }
        }
        setEmojiButtonImage(false, false);
        this.messageEditText = new EditTextCaption(activity2) {
            /* access modifiers changed from: private */
            /* renamed from: send */
            public void lambda$null$0$ChatActivityEnterView$10(InputContentInfoCompat inputContentInfoCompat, boolean z, int i) {
                if (inputContentInfoCompat.getDescription().hasMimeType("image/gif")) {
                    SendMessagesHelper.prepareSendingDocument(ChatActivityEnterView.this.accountInstance, (String) null, (String) null, inputContentInfoCompat.getContentUri(), (String) null, "image/gif", ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, inputContentInfoCompat, (MessageObject) null, z, 0);
                } else {
                    SendMessagesHelper.prepareSendingPhoto(ChatActivityEnterView.this.accountInstance, (String) null, inputContentInfoCompat.getContentUri(), ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, (CharSequence) null, (ArrayList<TLRPC.MessageEntity>) null, (ArrayList<TLRPC.InputDocument>) null, inputContentInfoCompat, 0, (MessageObject) null, z, 0);
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
        this.messageEditText.setDelegate(new EditTextCaption.EditTextCaptionDelegate() {
            public final void onSpansChanged() {
                ChatActivityEnterView.this.lambda$new$1$ChatActivityEnterView();
            }
        });
        this.messageEditText.setWindowView(this.parentActivity.getWindow().getDecorView());
        ChatActivity chatActivity3 = this.parentFragment;
        TLRPC.EncryptedChat currentEncryptedChat = chatActivity3 != null ? chatActivity3.getCurrentEncryptedChat() : null;
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
                        TLRPC.User user = null;
                        if (((int) ChatActivityEnterView.this.dialog_id) > 0) {
                            user = ChatActivityEnterView.this.accountInstance.getMessagesController().getUser(Integer.valueOf((int) ChatActivityEnterView.this.dialog_id));
                        }
                        if (user != null) {
                            if (user.id != UserConfig.getInstance(ChatActivityEnterView.this.currentAccount).getClientUserId()) {
                                TLRPC.UserStatus userStatus = user.status;
                                if (userStatus != null && userStatus.expires < currentTime && !ChatActivityEnterView.this.accountInstance.getMessagesController().onlinePrivacy.containsKey(Integer.valueOf(user.id))) {
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
                this.scheduledButton = new ImageView(activity2);
                this.scheduledButton.setImageDrawable(combinedDrawable);
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
            this.attachLayout = new LinearLayout(activity2);
            this.attachLayout.setOrientation(0);
            this.attachLayout.setEnabled(false);
            this.attachLayout.setPivotX((float) AndroidUtilities.dp(48.0f));
            r2.addView(this.attachLayout, LayoutHelper.createFrame(-2, 48, 85));
            this.botButton = new ImageView(activity2);
            this.botButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
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
            this.notifyButton = new ImageView(activity2);
            this.notifyButton.setImageResource(this.silent ? NUM : NUM);
            ImageView imageView = this.notifyButton;
            if (this.silent) {
                i = NUM;
                str = "AccDescrChanSilentOn";
            } else {
                i = NUM;
                str = "AccDescrChanSilentOff";
            }
            imageView.setContentDescription(LocaleController.getString(str, i));
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
                        this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOff", NUM), 0);
                        this.visibleToast.show();
                    } else {
                        this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOn", NUM), 0);
                        this.visibleToast.show();
                    }
                    ImageView access$6300 = ChatActivityEnterView.this.notifyButton;
                    if (ChatActivityEnterView.this.silent) {
                        i = NUM;
                        str = "AccDescrChanSilentOn";
                    } else {
                        i = NUM;
                        str = "AccDescrChanSilentOff";
                    }
                    access$6300.setContentDescription(LocaleController.getString(str, i));
                    ChatActivityEnterView.this.updateFieldHint();
                }
            });
            this.attachButton = new ImageView(activity2);
            this.attachButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
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
        this.recordedAudioPanel = new FrameLayout(activity2);
        this.recordedAudioPanel.setVisibility(this.audioToSend == null ? 8 : 0);
        this.recordedAudioPanel.setBackgroundColor(Theme.getColor("chat_messagePanelBackground"));
        this.recordedAudioPanel.setFocusable(true);
        this.recordedAudioPanel.setFocusableInTouchMode(true);
        this.recordedAudioPanel.setClickable(true);
        r2.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        this.recordDeleteImageView = new ImageView(activity2);
        this.recordDeleteImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.recordDeleteImageView.setImageResource(NUM);
        this.recordDeleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoiceDelete"), PorterDuff.Mode.MULTIPLY));
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
        this.videoTimelineView = new VideoTimelineView(activity2);
        this.videoTimelineView.setColor(Theme.getColor("chat_messagePanelVideoFrame"));
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
        this.recordedAudioPanel.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 32.0f, 19, 40.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioBackground = new View(activity2);
        this.recordedAudioBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("chat_recordedVoiceBackground")));
        this.recordedAudioPanel.addView(this.recordedAudioBackground, LayoutHelper.createFrame(-1, 36.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioSeekBar = new SeekBarWaveformView(activity2);
        this.recordedAudioPanel.addView(this.recordedAudioSeekBar, LayoutHelper.createFrame(-1, 32.0f, 19, 92.0f, 0.0f, 52.0f, 0.0f));
        this.playDrawable = Theme.createSimpleSelectorDrawable(activity2, NUM, Theme.getColor("chat_recordedVoicePlayPause"), Theme.getColor("chat_recordedVoicePlayPausePressed"));
        this.pauseDrawable = Theme.createSimpleSelectorDrawable(activity2, NUM, Theme.getColor("chat_recordedVoicePlayPause"), Theme.getColor("chat_recordedVoicePlayPausePressed"));
        this.recordedAudioPlayButton = new ImageView(activity2);
        this.recordedAudioPlayButton.setImageDrawable(this.playDrawable);
        this.recordedAudioPlayButton.setScaleType(ImageView.ScaleType.CENTER);
        this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
        this.recordedAudioPanel.addView(this.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0f, 83, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioPlayButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$6$ChatActivityEnterView(view);
            }
        });
        this.recordedAudioTimeTextView = new TextView(activity2);
        this.recordedAudioTimeTextView.setTextColor(Theme.getColor("chat_messagePanelVoiceDuration"));
        this.recordedAudioTimeTextView.setTextSize(1, 13.0f);
        this.recordedAudioPanel.addView(this.recordedAudioTimeTextView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 13.0f, 0.0f));
        this.recordPanel = new FrameLayout(activity2);
        this.recordPanel.setVisibility(8);
        this.recordPanel.setBackgroundColor(Theme.getColor("chat_messagePanelBackground"));
        r2.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48, 80));
        this.recordPanel.setOnTouchListener($$Lambda$ChatActivityEnterView$Uo3sK08hLv7NAPRSM5RagKUbmWg.INSTANCE);
        this.slideText = new LinearLayout(activity2);
        this.slideText.setOrientation(0);
        this.recordPanel.addView(this.slideText, LayoutHelper.createFrame(-2, -2.0f, 17, 30.0f, 0.0f, 0.0f, 0.0f));
        this.recordCancelImage = new ImageView(activity2);
        this.recordCancelImage.setImageResource(NUM);
        this.recordCancelImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_recordVoiceCancel"), PorterDuff.Mode.MULTIPLY));
        this.slideText.addView(this.recordCancelImage, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 0, 0));
        this.recordCancelText = new TextView(activity2);
        this.recordCancelText.setText(LocaleController.getString("SlideToCancel", NUM));
        this.recordCancelText.setTextColor(Theme.getColor("chat_recordVoiceCancel"));
        this.recordCancelText.setTextSize(1, 12.0f);
        this.slideText.addView(this.recordCancelText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        this.recordSendText = new TextView(activity2);
        this.recordSendText.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
        this.recordSendText.setTextColor(Theme.getColor("chat_fieldOverlayText"));
        this.recordSendText.setTextSize(1, 16.0f);
        this.recordSendText.setGravity(17);
        this.recordSendText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.recordSendText.setAlpha(0.0f);
        this.recordSendText.setPadding(AndroidUtilities.dp(36.0f), 0, 0, 0);
        this.recordSendText.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$8$ChatActivityEnterView(view);
            }
        });
        this.recordPanel.addView(this.recordSendText, LayoutHelper.createFrame(-2, -1.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
        this.recordTimeContainer = new LinearLayout(activity2);
        this.recordTimeContainer.setOrientation(0);
        this.recordTimeContainer.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        this.recordTimeContainer.setBackgroundColor(Theme.getColor("chat_messagePanelBackground"));
        this.recordPanel.addView(this.recordTimeContainer, LayoutHelper.createFrame(-2, -2, 16));
        this.recordDot = new RecordDot(activity2);
        this.recordTimeContainer.addView(this.recordDot, LayoutHelper.createLinear(11, 11, 16, 0, 1, 0, 0));
        this.recordTimeText = new TextView(activity2);
        this.recordTimeText.setTextColor(Theme.getColor("chat_recordTime"));
        this.recordTimeText.setTextSize(1, 16.0f);
        this.recordTimeContainer.addView(this.recordTimeText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        this.sendButtonContainer = new FrameLayout(activity2);
        this.sendButtonContainer.setClipChildren(false);
        this.sendButtonContainer.setClipToPadding(false);
        this.textFieldContainer.addView(this.sendButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        this.audioVideoButtonContainer = new FrameLayout(activity2);
        this.audioVideoButtonContainer.setBackgroundColor(Theme.getColor("chat_messagePanelBackground"));
        this.audioVideoButtonContainer.setSoundEffectsEnabled(false);
        this.sendButtonContainer.addView(this.audioVideoButtonContainer, LayoutHelper.createFrame(48, 48.0f));
        this.audioVideoButtonContainer.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return ChatActivityEnterView.this.lambda$new$13$ChatActivityEnterView(view, motionEvent);
            }
        });
        this.audioSendButton = new ImageView(activity2);
        this.audioSendButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.audioSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
        this.audioSendButton.setImageResource(NUM);
        this.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.audioSendButton.setContentDescription(LocaleController.getString("AccDescrVoiceMessage", NUM));
        this.audioSendButton.setFocusable(true);
        this.audioSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
        this.audioVideoButtonContainer.addView(this.audioSendButton, LayoutHelper.createFrame(48, 48.0f));
        if (z) {
            this.videoSendButton = new ImageView(activity2);
            this.videoSendButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            this.videoSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            this.videoSendButton.setImageResource(NUM);
            this.videoSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
            this.videoSendButton.setContentDescription(LocaleController.getString("AccDescrVideoMessage", NUM));
            this.videoSendButton.setFocusable(true);
            this.videoSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
            this.audioVideoButtonContainer.addView(this.videoSendButton, LayoutHelper.createFrame(48, 48.0f));
        }
        this.recordCircle = new RecordCircle(activity2);
        this.recordCircle.setVisibility(8);
        this.sizeNotifierLayout.addView(this.recordCircle, LayoutHelper.createFrame(124, 194.0f, 85, 0.0f, 0.0f, -36.0f, 0.0f));
        this.cancelBotButton = new ImageView(activity2);
        this.cancelBotButton.setVisibility(4);
        this.cancelBotButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageView imageView2 = this.cancelBotButton;
        CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
        this.progressDrawable = closeProgressDrawable2;
        imageView2.setImageDrawable(closeProgressDrawable2);
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
                ChatActivityEnterView.this.lambda$new$14$ChatActivityEnterView(view);
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
        this.sendButton = new View(activity2) {
            private float animateBounce;
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
                    this.animationProgress += ((float) (elapsedRealtime - this.lastAnimationTime)) / this.animationDuration;
                    if (this.animationProgress > 1.0f) {
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
                        float f2 = this.animationProgress;
                        if (f2 <= 0.25f) {
                            f = (float) dp3;
                            dp2 = ((float) AndroidUtilities.dp(2.0f)) * CubicBezierInterpolator.EASE_IN.getInterpolation(f2 / 0.25f);
                        } else {
                            float f3 = f2 - 0.25f;
                            if (f3 <= 0.5f) {
                                f = (float) dp3;
                                dp2 = ((float) AndroidUtilities.dp(2.0f)) - (((float) AndroidUtilities.dp(3.0f)) * CubicBezierInterpolator.EASE_IN.getInterpolation(f3 / 0.5f));
                            } else {
                                dp = (int) (((float) dp3) + ((float) (-AndroidUtilities.dp(1.0f))) + (((float) AndroidUtilities.dp(1.0f)) * CubicBezierInterpolator.EASE_IN.getInterpolation((f3 - 0.5f) / 0.25f)));
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
        this.sendButton.setVisibility(4);
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
                ChatActivityEnterView.this.lambda$new$15$ChatActivityEnterView(view);
            }
        });
        this.sendButton.setOnLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return ChatActivityEnterView.this.onSendLongClick(view);
            }
        });
        this.slowModeButton = new SimpleTextView(activity2);
        this.slowModeButton.setTextSize(18);
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
                ChatActivityEnterView.this.lambda$new$16$ChatActivityEnterView(view);
            }
        });
        this.slowModeButton.setOnLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return ChatActivityEnterView.this.lambda$new$17$ChatActivityEnterView(view);
            }
        });
        this.expandStickersButton = new ImageView(activity2);
        this.expandStickersButton.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView3 = this.expandStickersButton;
        AnimatedArrowDrawable animatedArrowDrawable = new AnimatedArrowDrawable(Theme.getColor("chat_messagePanelIcons"), false);
        this.stickersArrow = animatedArrowDrawable;
        imageView3.setImageDrawable(animatedArrowDrawable);
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
                ChatActivityEnterView.this.lambda$new$18$ChatActivityEnterView(view);
            }
        });
        this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", NUM));
        this.doneButtonContainer = new FrameLayout(activity2);
        this.doneButtonContainer.setVisibility(8);
        this.textFieldContainer.addView(this.doneButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        this.doneButtonContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$19$ChatActivityEnterView(view);
            }
        });
        ShapeDrawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(16.0f), Theme.getColor("chat_messagePanelSend"));
        Drawable mutate3 = activity.getResources().getDrawable(NUM).mutate();
        mutate3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable2 = new CombinedDrawable(createCircleDrawable, mutate3, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable2.setCustomSize(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        this.doneButtonImage = new ImageView(activity2);
        this.doneButtonImage.setScaleType(ImageView.ScaleType.CENTER);
        this.doneButtonImage.setImageDrawable(combinedDrawable2);
        this.doneButtonImage.setContentDescription(LocaleController.getString("Done", NUM));
        this.doneButtonContainer.addView(this.doneButtonImage, LayoutHelper.createFrame(48, 48.0f));
        this.doneButtonProgress = new ContextProgressView(activity2, 0);
        this.doneButtonProgress.setVisibility(4);
        this.doneButtonContainer.addView(this.doneButtonProgress, LayoutHelper.createFrame(-1, -1.0f));
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        this.keyboardHeight = globalEmojiSettings.getInt("kbd_height", AndroidUtilities.dp(200.0f));
        this.keyboardHeightLand = globalEmojiSettings.getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
        setRecordVideoButtonVisible(false, false);
        checkSendButton(false);
        checkChannelRights();
    }

    public /* synthetic */ void lambda$new$0$ChatActivityEnterView(View view) {
        if (!isPopupShowing() || this.currentPopupContentType != 0) {
            boolean z = true;
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
        if (this.videoToSendMessageObject != null) {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(2, true, 0);
        } else {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null && playingMessageObject == this.audioToSendMessageObject) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
        }
        String str = this.audioToSendPath;
        if (str != null) {
            new File(str).delete();
        }
        hideRecordedAudioPanel();
        checkSendButton(true);
    }

    public /* synthetic */ void lambda$new$6$ChatActivityEnterView(View view) {
        if (this.audioToSend != null) {
            if (!MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject) || MediaController.getInstance().isMessagePaused()) {
                this.recordedAudioPlayButton.setImageDrawable(this.pauseDrawable);
                MediaController.getInstance().playMessage(this.audioToSendMessageObject);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
                return;
            }
            MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.audioToSendMessageObject);
            this.recordedAudioPlayButton.setImageDrawable(this.playDrawable);
            this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
        }
    }

    public /* synthetic */ void lambda$new$8$ChatActivityEnterView(View view) {
        if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
            this.delegate.needStartRecordAudio(0);
            MediaController.getInstance().stopRecording(0, false, 0);
        } else {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(2, true, 0);
        }
        this.recordingAudioVideo = false;
        updateRecordIntefrace();
    }

    public /* synthetic */ boolean lambda$new$13$ChatActivityEnterView(View view, MotionEvent motionEvent) {
        TLRPC.Chat currentChat;
        int i = 3;
        boolean z = false;
        if (motionEvent.getAction() == 0) {
            if (this.recordCircle.isSendButtonVisible()) {
                if (!this.hasRecordVideo || this.calledRecordRunnable) {
                    this.startedDraggingX = -1.0f;
                    if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                        if (this.recordingAudioVideo && isInScheduleMode()) {
                            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), $$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY.INSTANCE, $$Lambda$ChatActivityEnterView$_c9ye99difEKRRO05psDolLw5uU.INSTANCE);
                        }
                        this.delegate.needStartRecordAudio(0);
                        MediaController instance = MediaController.getInstance();
                        if (!isInScheduleMode()) {
                            i = 1;
                        }
                        instance.stopRecording(i, true, 0);
                    } else {
                        this.delegate.needStartRecordVideo(1, true, 0);
                    }
                    this.recordingAudioVideo = false;
                    updateRecordIntefrace();
                }
                return false;
            }
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
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.recordCircle.isSendButtonVisible() || this.recordedAudioPanel.getVisibility() == 0) {
                return false;
            }
            if (this.recordAudioVideoRunnableStarted) {
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
                        AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), $$Lambda$ChatActivityEnterView$bbN64eZurKUvgp0IAMBkXVy3foY.INSTANCE, $$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS0MhNsaHk.INSTANCE);
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
                updateRecordIntefrace();
            }
        } else if (motionEvent.getAction() == 2 && this.recordingAudioVideo) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (this.recordCircle.isSendButtonVisible()) {
                return false;
            }
            if (this.recordCircle.setLockTranslation(y) == 2) {
                AnimatorSet animatorSet = new AnimatorSet();
                RecordCircle recordCircle2 = this.recordCircle;
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(recordCircle2, "lockAnimatedTranslation", new float[]{recordCircle2.startTranslation}), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f)}), ObjectAnimator.ofFloat(this.recordSendText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordSendText, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(20.0f)), 0.0f})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(150);
                animatorSet.start();
                return false;
            }
            if (x < (-this.distCanMove)) {
                if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                    this.delegate.needStartRecordAudio(0);
                    MediaController.getInstance().stopRecording(0, false, 0);
                } else {
                    CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                    this.delegate.needStartRecordVideo(2, true, 0);
                }
                this.recordingAudioVideo = false;
                updateRecordIntefrace();
            }
            float x2 = x + this.audioVideoButtonContainer.getX();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.slideText.getLayoutParams();
            float f = this.startedDraggingX;
            if (f != -1.0f) {
                float f2 = x2 - f;
                layoutParams.leftMargin = AndroidUtilities.dp(30.0f) + ((int) f2);
                this.slideText.setLayoutParams(layoutParams);
                float f3 = (f2 / this.distCanMove) + 1.0f;
                if (f3 > 1.0f) {
                    f3 = 1.0f;
                } else if (f3 < 0.0f) {
                    f3 = 0.0f;
                }
                this.slideText.setAlpha(f3);
            }
            if (x2 <= this.slideText.getX() + ((float) this.slideText.getWidth()) + ((float) AndroidUtilities.dp(30.0f)) && this.startedDraggingX == -1.0f) {
                this.startedDraggingX = x2;
                this.distCanMove = ((float) ((this.recordPanel.getMeasuredWidth() - this.slideText.getMeasuredWidth()) - AndroidUtilities.dp(48.0f))) / 2.0f;
                float f4 = this.distCanMove;
                if (f4 <= 0.0f) {
                    this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                } else if (f4 > ((float) AndroidUtilities.dp(80.0f))) {
                    this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                }
            }
            if (layoutParams.leftMargin > AndroidUtilities.dp(30.0f)) {
                layoutParams.leftMargin = AndroidUtilities.dp(30.0f);
                this.slideText.setLayoutParams(layoutParams);
                this.slideText.setAlpha(1.0f);
                this.startedDraggingX = -1.0f;
            }
        }
        view.onTouchEvent(motionEvent);
        return true;
    }

    public /* synthetic */ void lambda$new$14$ChatActivityEnterView(View view) {
        String obj = this.messageEditText.getText().toString();
        int indexOf = obj.indexOf(32);
        if (indexOf == -1 || indexOf == obj.length() - 1) {
            setFieldText("");
        } else {
            setFieldText(obj.substring(0, indexOf + 1));
        }
    }

    public /* synthetic */ void lambda$new$15$ChatActivityEnterView(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            sendMessage();
        }
    }

    public /* synthetic */ void lambda$new$16$ChatActivityEnterView(View view) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            SimpleTextView simpleTextView = this.slowModeButton;
            chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
        }
    }

    public /* synthetic */ boolean lambda$new$17$ChatActivityEnterView(View view) {
        if (this.messageEditText.length() == 0) {
            return false;
        }
        return onSendLongClick(view);
    }

    public /* synthetic */ void lambda$new$18$ChatActivityEnterView(View view) {
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

    public /* synthetic */ void lambda$new$19$ChatActivityEnterView(View view) {
        doneEditingMessage();
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
            TLRPC.User currentUser = this.parentFragment.getCurrentUser();
            if (this.sendPopupLayout == null) {
                this.sendPopupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity);
                this.sendPopupLayout.setAnimationEnabled(false);
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
                        ChatActivityEnterView.this.lambda$onSendLongClick$20$ChatActivityEnterView(keyEvent);
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
                                ChatActivityEnterView.this.lambda$onSendLongClick$21$ChatActivityEnterView(this.f$1, view);
                            }
                        });
                    }
                }
                this.sendPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2) {
                    public void dismiss() {
                        super.dismiss();
                        ChatActivityEnterView.this.sendButton.invalidate();
                    }
                };
                this.sendPopupWindow.setAnimationEnabled(false);
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

    public /* synthetic */ void lambda$onSendLongClick$20$ChatActivityEnterView(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$onSendLongClick$21$ChatActivityEnterView(int i, View view) {
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
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00e0  */
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
            if (r13 == 0) goto L_0x010e
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
            android.animation.AnimatorSet r13 = r11.audioVideoButtonAnimation
            r5 = 6
            android.animation.Animator[] r5 = new android.animation.Animator[r5]
            android.widget.ImageView r6 = r11.videoSendButton
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r8 = new float[r2]
            if (r12 == 0) goto L_0x0075
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x0078
        L_0x0075:
            r9 = 1036831949(0x3dcccccd, float:0.1)
        L_0x0078:
            r8[r4] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r5[r4] = r6
            android.widget.ImageView r6 = r11.videoSendButton
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r8 = new float[r2]
            if (r12 == 0) goto L_0x008b
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x008e
        L_0x008b:
            r9 = 1036831949(0x3dcccccd, float:0.1)
        L_0x008e:
            r8[r4] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r5[r2] = r6
            r6 = 2
            android.widget.ImageView r7 = r11.videoSendButton
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r2]
            if (r12 == 0) goto L_0x00a2
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x00a3
        L_0x00a2:
            r10 = 0
        L_0x00a3:
            r9[r4] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5[r6] = r7
            r6 = 3
            android.widget.ImageView r7 = r11.audioSendButton
            android.util.Property r8 = android.view.View.SCALE_X
            float[] r9 = new float[r2]
            if (r12 == 0) goto L_0x00b8
            r10 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x00ba
        L_0x00b8:
            r10 = 1065353216(0x3var_, float:1.0)
        L_0x00ba:
            r9[r4] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5[r6] = r7
            r6 = 4
            android.widget.ImageView r7 = r11.audioSendButton
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r9 = new float[r2]
            if (r12 == 0) goto L_0x00cc
            goto L_0x00ce
        L_0x00cc:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x00ce:
            r9[r4] = r1
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5[r6] = r1
            r1 = 5
            android.widget.ImageView r6 = r11.audioSendButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r2 = new float[r2]
            if (r12 == 0) goto L_0x00e0
            goto L_0x00e2
        L_0x00e0:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x00e2:
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
            goto L_0x0154
        L_0x010e:
            android.widget.ImageView r13 = r11.videoSendButton
            if (r12 == 0) goto L_0x0115
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0118
        L_0x0115:
            r2 = 1036831949(0x3dcccccd, float:0.1)
        L_0x0118:
            r13.setScaleX(r2)
            android.widget.ImageView r13 = r11.videoSendButton
            if (r12 == 0) goto L_0x0122
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0125
        L_0x0122:
            r2 = 1036831949(0x3dcccccd, float:0.1)
        L_0x0125:
            r13.setScaleY(r2)
            android.widget.ImageView r13 = r11.videoSendButton
            if (r12 == 0) goto L_0x012f
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0130
        L_0x012f:
            r2 = 0
        L_0x0130:
            r13.setAlpha(r2)
            android.widget.ImageView r13 = r11.audioSendButton
            if (r12 == 0) goto L_0x013b
            r2 = 1036831949(0x3dcccccd, float:0.1)
            goto L_0x013d
        L_0x013b:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x013d:
            r13.setScaleX(r2)
            android.widget.ImageView r13 = r11.audioSendButton
            if (r12 == 0) goto L_0x0145
            goto L_0x0147
        L_0x0145:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0147:
            r13.setScaleY(r1)
            android.widget.ImageView r13 = r11.audioSendButton
            if (r12 == 0) goto L_0x014f
            goto L_0x0151
        L_0x014f:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x0151:
            r13.setAlpha(r0)
        L_0x0154:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setRecordVideoButtonVisible(boolean, boolean):void");
    }

    public boolean isRecordingAudioVideo() {
        return this.recordingAudioVideo;
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
            this.delegate.needStartRecordVideo(2, true, 0);
        }
        this.recordingAudioVideo = false;
        updateRecordIntefrace();
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
            this.topLineView.setVisibility(8);
            this.topLineView.setAlpha(0.0f);
            addView(this.topLineView, LayoutHelper.createFrame(-1, 1.0f, 51, 0.0f, (float) (i + 1), 0.0f, 0.0f));
            this.topView = view;
            this.topView.setVisibility(8);
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
                    this.currentTopViewAnimation = new AnimatorSet();
                    this.currentTopViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.topView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.topLineView, View.ALPHA, new float[]{1.0f})});
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
                    this.currentTopViewAnimation = new AnimatorSet();
                    AnimatorSet animatorSet2 = this.currentTopViewAnimation;
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
        TLRPC.Chat currentChat;
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
            this.accountInstance = AccountInstance.getInstance(this.currentAccount);
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

    public void setChatInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.setChatInfo(this.info);
        }
        setSlowModeTimer(chatFull.slowmode_next_send_date);
    }

    public void checkRoundVideo() {
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
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
                TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    z = false;
                }
                if (z && !chat.creator && ((tL_chatAdminRights = chat.admin_rights) == null || !tL_chatAdminRights.post_messages)) {
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
            TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
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
            setButtons(this.replyingMessageObject, true);
        } else if (messageObject == null && this.replyingMessageObject == this.botButtonsMessageObject) {
            this.replyingMessageObject = null;
            setButtons(this.botMessageObject, false);
            this.botMessageObject = null;
        } else {
            this.replyingMessageObject = messageObject;
        }
        MediaController.getInstance().setReplyingMessage(messageObject);
    }

    public void setWebPage(TLRPC.WebPage webPage, boolean z) {
        this.messageWebPage = webPage;
        this.messageWebPageSearch = z;
    }

    public boolean isMessageWebPageSearchEnabled() {
        return this.messageWebPageSearch;
    }

    private void hideRecordedAudioPanel() {
        this.audioToSendPath = null;
        this.audioToSend = null;
        this.audioToSendMessageObject = null;
        this.videoToSendMessageObject = null;
        this.videoTimelineView.destroy();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, new float[]{0.0f})});
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
            }
        });
        animatorSet.start();
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
        TLRPC.Chat currentChat;
        boolean z2 = z;
        int i2 = i;
        if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null) {
                TLRPC.Chat currentChat2 = chatActivity.getCurrentChat();
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
                hideRecordedAudioPanel();
                checkSendButton(true);
            } else if (this.audioToSend != null) {
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject != null && playingMessageObject == this.audioToSendMessageObject) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.audioToSend, (VideoEditedInfo) null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, (String) null, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, (Object) null);
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                if (chatActivityEnterViewDelegate2 != null) {
                    chatActivityEnterViewDelegate2.onMessageSend((CharSequence) null, z, i);
                }
                hideRecordedAudioPanel();
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
            ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities());
            SendMessagesHelper instance = SendMessagesHelper.getInstance(this.currentAccount);
            MessageObject messageObject = this.editingMessageObject;
            String charSequence = charSequenceArr[0].toString();
            boolean z = this.messageWebPageSearch;
            ChatActivity chatActivity = this.parentFragment;
            MessageObject messageObject2 = this.editingMessageObject;
            this.editingMessageReqId = instance.editMessage(messageObject, charSequence, z, chatActivity, entities, messageObject2.scheduled ? messageObject2.messageOwner.date : 0, new Runnable() {
                public final void run() {
                    ChatActivityEnterView.this.lambda$doneEditingMessage$22$ChatActivityEnterView();
                }
            });
        }
    }

    public /* synthetic */ void lambda$doneEditingMessage$22$ChatActivityEnterView() {
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
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(charSequenceArr[0].toString(), this.dialog_id, this.replyingMessageObject, this.messageWebPage, this.messageWebPageSearch, MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities), (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i);
        }
        return true;
    }

    private boolean supportsSendingNewEntities() {
        ChatActivity chatActivity = this.parentFragment;
        TLRPC.EncryptedChat currentEncryptedChat = chatActivity != null ? chatActivity.getCurrentEncryptedChat() : null;
        return currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(currentEncryptedChat.layer) >= 101;
    }

    /* access modifiers changed from: private */
    public void checkSendButton(boolean z) {
        int i;
        int i2;
        if (this.editingMessageObject == null) {
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
                    LinearLayout linearLayout3 = this.attachLayout;
                    if (linearLayout3 != null) {
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
            } else if (layoutParams.rightMargin != AndroidUtilities.dp(2.0f)) {
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
    public void updateRecordIntefrace() {
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
            if (this.recordInterfaceState != 0) {
                this.recordInterfaceState = 0;
                AnimatorSet animatorSet = this.runningAnimationAudio;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                this.runningAnimationAudio = new AnimatorSet();
                this.runningAnimationAudio.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordPanel, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.displaySize.x}), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{0.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f})});
                this.runningAnimationAudio.setDuration(300);
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ChatActivityEnterView.this.slideText.getLayoutParams();
                            layoutParams.leftMargin = AndroidUtilities.dp(30.0f);
                            ChatActivityEnterView.this.slideText.setLayoutParams(layoutParams);
                            ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordPanel.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setSendButtonInvisible();
                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimationAudio = null;
                        }
                    }
                });
                this.runningAnimationAudio.setInterpolator(new AccelerateInterpolator());
                this.runningAnimationAudio.start();
            }
        } else if (this.recordInterfaceState != 1) {
            this.recordInterfaceState = 1;
            try {
                if (this.wakeLock == null) {
                    this.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(NUM, "telegram:audio_record_lock");
                    this.wakeLock.acquire();
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            AndroidUtilities.lockOrientation(this.parentActivity);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.needStartRecordAudio(0);
            }
            this.recordPanel.setVisibility(0);
            this.recordCircle.setVisibility(0);
            this.recordCircle.setAmplitude(0.0d);
            this.recordTimeText.setText(String.format("%02d:%02d.%02d", new Object[]{0, 0, 0}));
            this.recordDot.resetAlpha();
            this.lastTimeString = null;
            this.lastTypingSendTime = -1;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.slideText.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(30.0f);
            this.slideText.setLayoutParams(layoutParams);
            this.slideText.setAlpha(1.0f);
            this.recordPanel.setX((float) AndroidUtilities.displaySize.x);
            AnimatorSet animatorSet2 = this.runningAnimationAudio;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            this.runningAnimationAudio = new AnimatorSet();
            this.runningAnimationAudio.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordPanel, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{1.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f})});
            this.runningAnimationAudio.setDuration(300);
            this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
                        ChatActivityEnterView.this.recordPanel.setX(0.0f);
                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimationAudio = null;
                    }
                }
            });
            this.runningAnimationAudio.setInterpolator(new DecelerateInterpolator());
            this.runningAnimationAudio.start();
        }
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
            TLRPC.User user = null;
            if (z) {
                String obj = this.messageEditText.getText().toString();
                if (messageObject2 != null && ((int) this.dialog_id) < 0) {
                    user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                }
                if ((this.botCount != 1 || z2) && user != null && user.bot && !str3.contains("@")) {
                    str2 = String.format(Locale.US, "%s@%s", new Object[]{str3, user.username}) + " " + obj.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
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
                    user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                }
                if ((this.botCount != 1 || z2) && user != null && user.bot && !str3.contains("@")) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(String.format(Locale.US, "%s@%s", new Object[]{str3, user.username}), this.dialog_id, this.replyingMessageObject, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                    return;
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(str, this.dialog_id, this.replyingMessageObject, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
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
            if (this.editingMessageObject != null) {
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
                    ArrayList<TLRPC.MessageEntity> arrayList = this.editingMessageObject.messageOwner.entities;
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
                                TLRPC.MessageEntity messageEntity = arrayList.get(i);
                                if (messageEntity.offset + messageEntity.length <= spannableStringBuilder.length()) {
                                    if (messageEntity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                                        if (messageEntity.offset + messageEntity.length < spannableStringBuilder.length() && spannableStringBuilder.charAt(messageEntity.offset + messageEntity.length) == ' ') {
                                            messageEntity.length++;
                                        }
                                        spannableStringBuilder.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_inputMessageEntityMentionName) messageEntity).user_id.user_id, 3), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                    } else if (messageEntity instanceof TLRPC.TL_messageEntityMentionName) {
                                        if (messageEntity.offset + messageEntity.length < spannableStringBuilder.length() && spannableStringBuilder.charAt(messageEntity.offset + messageEntity.length) == ' ') {
                                            messageEntity.length++;
                                        }
                                        spannableStringBuilder.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_messageEntityMentionName) messageEntity).user_id, 3), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                    } else {
                                        if (!(messageEntity instanceof TLRPC.TL_messageEntityCode)) {
                                            if (!(messageEntity instanceof TLRPC.TL_messageEntityPre)) {
                                                if (messageEntity instanceof TLRPC.TL_messageEntityBold) {
                                                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                                                    textStyleRun.flags |= 1;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun), messageEntity.offset, messageEntity.offset + messageEntity.length, spannableStringBuilder, true);
                                                } else if (messageEntity instanceof TLRPC.TL_messageEntityItalic) {
                                                    TextStyleSpan.TextStyleRun textStyleRun2 = new TextStyleSpan.TextStyleRun();
                                                    textStyleRun2.flags |= 2;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun2), messageEntity.offset, messageEntity.offset + messageEntity.length, spannableStringBuilder, true);
                                                } else if (messageEntity instanceof TLRPC.TL_messageEntityStrike) {
                                                    TextStyleSpan.TextStyleRun textStyleRun3 = new TextStyleSpan.TextStyleRun();
                                                    textStyleRun3.flags |= 8;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun3), messageEntity.offset, messageEntity.offset + messageEntity.length, spannableStringBuilder, true);
                                                } else if (messageEntity instanceof TLRPC.TL_messageEntityUnderline) {
                                                    TextStyleSpan.TextStyleRun textStyleRun4 = new TextStyleSpan.TextStyleRun();
                                                    textStyleRun4.flags |= 16;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun4), messageEntity.offset, messageEntity.offset + messageEntity.length, spannableStringBuilder, true);
                                                } else if (messageEntity instanceof TLRPC.TL_messageEntityTextUrl) {
                                                    spannableStringBuilder.setSpan(new URLSpanReplacement(messageEntity.url), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                                }
                                            }
                                        }
                                        TextStyleSpan.TextStyleRun textStyleRun5 = new TextStyleSpan.TextStyleRun();
                                        textStyleRun5.flags |= 4;
                                        MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun5), messageEntity.offset, messageEntity.offset + messageEntity.length, spannableStringBuilder, true);
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
            if (r1 >= 0) goto L_0x007a
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
            android.widget.ImageView r1 = r13.notifyButton
            if (r1 == 0) goto L_0x0068
            boolean r3 = r13.canWriteToChannel
            boolean r4 = r13.silent
            if (r4 == 0) goto L_0x0061
            r4 = 2131165503(0x7var_f, float:1.7945225E38)
            goto L_0x0064
        L_0x0061:
            r4 = 2131165504(0x7var_, float:1.7945227E38)
        L_0x0064:
            r1.setImageResource(r4)
            goto L_0x0069
        L_0x0068:
            r3 = 0
        L_0x0069:
            android.widget.LinearLayout r1 = r13.attachLayout
            if (r1 == 0) goto L_0x007b
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0075
            r1 = 1
            goto L_0x0076
        L_0x0075:
            r1 = 0
        L_0x0076:
            r13.updateFieldRight(r1)
            goto L_0x007b
        L_0x007a:
            r3 = 0
        L_0x007b:
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r1 = r13.delegate
            if (r1 == 0) goto L_0x008f
            boolean r1 = r13.isInScheduleMode()
            if (r1 != 0) goto L_0x008f
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r1 = r13.delegate
            boolean r1 = r1.hasScheduledMessages()
            if (r1 == 0) goto L_0x008f
            r1 = 1
            goto L_0x0090
        L_0x008f:
            r1 = 0
        L_0x0090:
            if (r1 == 0) goto L_0x0098
            boolean r4 = r13.scheduleButtonHidden
            if (r4 != 0) goto L_0x0098
            r4 = 1
            goto L_0x0099
        L_0x0098:
            r4 = 0
        L_0x0099:
            android.widget.ImageView r5 = r13.scheduledButton
            r6 = 1119879168(0x42CLASSNAME, float:96.0)
            r7 = 1111490560(0x42400000, float:48.0)
            r8 = 0
            r9 = 8
            if (r5 == 0) goto L_0x0106
            java.lang.Object r5 = r5.getTag()
            if (r5 == 0) goto L_0x00ac
            if (r4 != 0) goto L_0x00b6
        L_0x00ac:
            android.widget.ImageView r5 = r13.scheduledButton
            java.lang.Object r5 = r5.getTag()
            if (r5 != 0) goto L_0x00f9
            if (r4 != 0) goto L_0x00f9
        L_0x00b6:
            android.widget.ImageView r14 = r13.notifyButton
            if (r14 == 0) goto L_0x00f8
            if (r1 != 0) goto L_0x00c7
            if (r3 == 0) goto L_0x00c7
            android.widget.ImageView r14 = r13.scheduledButton
            int r14 = r14.getVisibility()
            if (r14 == 0) goto L_0x00c7
            goto L_0x00c9
        L_0x00c7:
            r2 = 8
        L_0x00c9:
            android.widget.ImageView r14 = r13.notifyButton
            int r14 = r14.getVisibility()
            if (r2 == r14) goto L_0x00f8
            android.widget.ImageView r14 = r13.notifyButton
            r14.setVisibility(r2)
            android.widget.LinearLayout r14 = r13.attachLayout
            if (r14 == 0) goto L_0x00f8
            android.widget.ImageView r0 = r13.botButton
            if (r0 == 0) goto L_0x00e4
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x00f0
        L_0x00e4:
            android.widget.ImageView r0 = r13.notifyButton
            if (r0 == 0) goto L_0x00ee
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x00f0
        L_0x00ee:
            r6 = 1111490560(0x42400000, float:48.0)
        L_0x00f0:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r0 = (float) r0
            r14.setPivotX(r0)
        L_0x00f8:
            return
        L_0x00f9:
            android.widget.ImageView r1 = r13.scheduledButton
            if (r4 == 0) goto L_0x0102
            java.lang.Integer r5 = java.lang.Integer.valueOf(r0)
            goto L_0x0103
        L_0x0102:
            r5 = r8
        L_0x0103:
            r1.setTag(r5)
        L_0x0106:
            android.animation.AnimatorSet r1 = r13.scheduledButtonAnimation
            if (r1 == 0) goto L_0x010f
            r1.cancel()
            r13.scheduledButtonAnimation = r8
        L_0x010f:
            r1 = 0
            r5 = 1036831949(0x3dcccccd, float:0.1)
            r8 = 1065353216(0x3var_, float:1.0)
            if (r14 == 0) goto L_0x018e
            if (r3 == 0) goto L_0x011a
            goto L_0x018e
        L_0x011a:
            if (r4 == 0) goto L_0x0121
            android.widget.ImageView r14 = r13.scheduledButton
            r14.setVisibility(r2)
        L_0x0121:
            android.widget.ImageView r14 = r13.scheduledButton
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r14.setPivotX(r3)
            android.animation.AnimatorSet r14 = new android.animation.AnimatorSet
            r14.<init>()
            r13.scheduledButtonAnimation = r14
            android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
            r3 = 3
            android.animation.Animator[] r3 = new android.animation.Animator[r3]
            android.widget.ImageView r10 = r13.scheduledButton
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r0]
            if (r4 == 0) goto L_0x0143
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0143:
            r12[r2] = r1
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r3[r2] = r1
            android.widget.ImageView r1 = r13.scheduledButton
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r0]
            if (r4 == 0) goto L_0x0156
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0159
        L_0x0156:
            r12 = 1036831949(0x3dcccccd, float:0.1)
        L_0x0159:
            r11[r2] = r12
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r1, r10, r11)
            r3[r0] = r1
            r1 = 2
            android.widget.ImageView r10 = r13.scheduledButton
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r0 = new float[r0]
            if (r4 == 0) goto L_0x016c
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x016c:
            r0[r2] = r5
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r10, r11, r0)
            r3[r1] = r0
            r14.playTogether(r3)
            android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
            r0 = 180(0xb4, double:8.9E-322)
            r14.setDuration(r0)
            android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
            org.telegram.ui.Components.ChatActivityEnterView$34 r0 = new org.telegram.ui.Components.ChatActivityEnterView$34
            r0.<init>(r4)
            r14.addListener(r0)
            android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
            r14.start()
            goto L_0x01ce
        L_0x018e:
            android.widget.ImageView r14 = r13.scheduledButton
            if (r14 == 0) goto L_0x01ba
            if (r4 == 0) goto L_0x0196
            r0 = 0
            goto L_0x0198
        L_0x0196:
            r0 = 8
        L_0x0198:
            r14.setVisibility(r0)
            android.widget.ImageView r14 = r13.scheduledButton
            if (r4 == 0) goto L_0x01a1
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x01a1:
            r14.setAlpha(r1)
            android.widget.ImageView r14 = r13.scheduledButton
            if (r4 == 0) goto L_0x01ab
            r0 = 1065353216(0x3var_, float:1.0)
            goto L_0x01ae
        L_0x01ab:
            r0 = 1036831949(0x3dcccccd, float:0.1)
        L_0x01ae:
            r14.setScaleX(r0)
            android.widget.ImageView r14 = r13.scheduledButton
            if (r4 == 0) goto L_0x01b7
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x01b7:
            r14.setScaleY(r5)
        L_0x01ba:
            android.widget.ImageView r14 = r13.notifyButton
            if (r14 == 0) goto L_0x01ce
            if (r3 == 0) goto L_0x01c9
            android.widget.ImageView r0 = r13.scheduledButton
            int r0 = r0.getVisibility()
            if (r0 == 0) goto L_0x01c9
            goto L_0x01cb
        L_0x01c9:
            r2 = 8
        L_0x01cb:
            r14.setVisibility(r2)
        L_0x01ce:
            android.widget.LinearLayout r14 = r13.attachLayout
            if (r14 == 0) goto L_0x01f0
            android.widget.ImageView r0 = r13.botButton
            if (r0 == 0) goto L_0x01dc
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x01e8
        L_0x01dc:
            android.widget.ImageView r0 = r13.notifyButton
            if (r0 == 0) goto L_0x01e6
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x01e8
        L_0x01e6:
            r6 = 1111490560(0x42400000, float:48.0)
        L_0x01e8:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r0 = (float) r0
            r14.setPivotX(r0)
        L_0x01f0:
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
            r1 = 2131165501(0x7var_d, float:1.794522E38)
            r0.setImageResource(r1)
            android.widget.ImageView r0 = r4.botButton
            r1 = 2131624010(0x7f0e004a, float:1.8875188E38)
            java.lang.String r3 = "AccDescrShowKeyboard"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
            goto L_0x0075
        L_0x0048:
            android.widget.ImageView r0 = r4.botButton
            r1 = 2131165494(0x7var_, float:1.7945207E38)
            r0.setImageResource(r1)
            android.widget.ImageView r0 = r4.botButton
            r1 = 2131623953(0x7f0e0011, float:1.8875072E38)
            java.lang.String r3 = "AccDescrBotKeyboard"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
            goto L_0x0075
        L_0x005f:
            android.widget.ImageView r0 = r4.botButton
            r1 = 2131165493(0x7var_, float:1.7945205E38)
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

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00c9, code lost:
        if (r8.getInt("answered_" + r6.dialog_id, 0) == r7.getId()) goto L_0x00cd;
     */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00e3  */
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
            if (r0 == 0) goto L_0x00f9
            org.telegram.messenger.MessageObject r0 = r6.botButtonsMessageObject
            if (r0 == 0) goto L_0x0017
            if (r0 == r7) goto L_0x00f9
        L_0x0017:
            org.telegram.messenger.MessageObject r0 = r6.botButtonsMessageObject
            if (r0 != 0) goto L_0x001f
            if (r7 != 0) goto L_0x001f
            goto L_0x00f9
        L_0x001f:
            org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x004d
            org.telegram.ui.Components.BotKeyboardView r0 = new org.telegram.ui.Components.BotKeyboardView
            android.app.Activity r3 = r6.parentActivity
            r0.<init>(r3)
            r6.botKeyboardView = r0
            org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
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
        L_0x004d:
            r6.botButtonsMessageObject = r7
            if (r7 == 0) goto L_0x005c
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup
            if (r3 == 0) goto L_0x005c
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = (org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup) r0
            goto L_0x005d
        L_0x005c:
            r0 = 0
        L_0x005d:
            r6.botReplyMarkup = r0
            org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
            int r3 = r3.y
            if (r4 <= r3) goto L_0x006c
            int r3 = r6.keyboardHeightLand
            goto L_0x006e
        L_0x006c:
            int r3 = r6.keyboardHeight
        L_0x006e:
            r0.setPanelHeight(r3)
            org.telegram.ui.Components.BotKeyboardView r0 = r6.botKeyboardView
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r3 = r6.botReplyMarkup
            r0.setButtons(r3)
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = r6.botReplyMarkup
            if (r0 == 0) goto L_0x00e3
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
            if (r0 != r3) goto L_0x00a1
            r0 = 1
            goto L_0x00a2
        L_0x00a1:
            r0 = 0
        L_0x00a2:
            org.telegram.messenger.MessageObject r3 = r6.botButtonsMessageObject
            org.telegram.messenger.MessageObject r4 = r6.replyingMessageObject
            if (r3 == r4) goto L_0x00cc
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r3 = r6.botReplyMarkup
            boolean r3 = r3.single_use
            if (r3 == 0) goto L_0x00cc
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "answered_"
            r3.append(r4)
            long r4 = r6.dialog_id
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            int r8 = r8.getInt(r3, r1)
            int r7 = r7.getId()
            if (r8 != r7) goto L_0x00cc
            goto L_0x00cd
        L_0x00cc:
            r1 = 1
        L_0x00cd:
            if (r1 == 0) goto L_0x00f6
            if (r0 != 0) goto L_0x00f6
            org.telegram.ui.Components.EditTextCaption r7 = r6.messageEditText
            int r7 = r7.length()
            if (r7 != 0) goto L_0x00f6
            boolean r7 = r6.isPopupShowing()
            if (r7 != 0) goto L_0x00f6
            r6.showPopup(r2, r2)
            goto L_0x00f6
        L_0x00e3:
            boolean r7 = r6.isPopupShowing()
            if (r7 == 0) goto L_0x00f6
            int r7 = r6.currentPopupContentType
            if (r7 != r2) goto L_0x00f6
            if (r8 == 0) goto L_0x00f3
            r6.openKeyboardInternal()
            goto L_0x00f6
        L_0x00f3:
            r6.showPopup(r1, r2)
        L_0x00f6:
            r6.updateBotButton()
        L_0x00f9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setButtons(org.telegram.messenger.MessageObject, boolean):void");
    }

    public /* synthetic */ void lambda$setButtons$24$ChatActivityEnterView(TLRPC.KeyboardButton keyboardButton) {
        MessageObject messageObject = this.replyingMessageObject;
        if (messageObject == null) {
            messageObject = ((int) this.dialog_id) < 0 ? this.botButtonsMessageObject : null;
        }
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 == null) {
            messageObject2 = this.botButtonsMessageObject;
        }
        boolean didPressedBotButton = didPressedBotButton(keyboardButton, messageObject, messageObject2);
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

    public boolean didPressedBotButton(TLRPC.KeyboardButton keyboardButton, MessageObject messageObject, MessageObject messageObject2) {
        TLRPC.KeyboardButton keyboardButton2 = keyboardButton;
        MessageObject messageObject3 = messageObject2;
        if (keyboardButton2 == null || messageObject3 == null) {
            return false;
        }
        if (keyboardButton2 instanceof TLRPC.TL_keyboardButton) {
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(keyboardButton2.text, this.dialog_id, messageObject, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        } else if (keyboardButton2 instanceof TLRPC.TL_keyboardButtonUrl) {
            this.parentFragment.showOpenUrlAlert(keyboardButton2.url, true);
        } else if (keyboardButton2 instanceof TLRPC.TL_keyboardButtonRequestPhone) {
            this.parentFragment.shareMyContact(2, messageObject3);
        } else {
            Boolean bool = null;
            if (keyboardButton2 instanceof TLRPC.TL_keyboardButtonRequestPoll) {
                ChatActivity chatActivity = this.parentFragment;
                if ((keyboardButton2.flags & 1) != 0) {
                    bool = Boolean.valueOf(keyboardButton2.quiz);
                }
                chatActivity.openPollCreate(bool);
                return false;
            } else if (keyboardButton2 instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(messageObject3, keyboardButton2) {
                    private final /* synthetic */ MessageObject f$1;
                    private final /* synthetic */ TLRPC.KeyboardButton f$2;

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
            } else if ((keyboardButton2 instanceof TLRPC.TL_keyboardButtonCallback) || (keyboardButton2 instanceof TLRPC.TL_keyboardButtonGame) || (keyboardButton2 instanceof TLRPC.TL_keyboardButtonBuy) || (keyboardButton2 instanceof TLRPC.TL_keyboardButtonUrlAuth)) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCallback(true, messageObject3, keyboardButton2, this.parentFragment);
            } else if (!(keyboardButton2 instanceof TLRPC.TL_keyboardButtonSwitchInline) || this.parentFragment.processSwitchButton((TLRPC.TL_keyboardButtonSwitchInline) keyboardButton2)) {
                return true;
            } else {
                if (keyboardButton2.same_peer) {
                    TLRPC.Message message = messageObject3.messageOwner;
                    int i = message.from_id;
                    int i2 = message.via_bot_id;
                    if (i2 == 0) {
                        i2 = i;
                    }
                    TLRPC.User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i2));
                    if (user == null) {
                        return true;
                    }
                    setFieldText("@" + user.username + " " + keyboardButton2.query);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlySelect", true);
                    bundle.putInt("dialogsType", 1);
                    DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                    dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate(messageObject3, keyboardButton2) {
                        private final /* synthetic */ MessageObject f$1;
                        private final /* synthetic */ TLRPC.KeyboardButton f$2;

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

    public /* synthetic */ void lambda$didPressedBotButton$25$ChatActivityEnterView(MessageObject messageObject, TLRPC.KeyboardButton keyboardButton, DialogInterface dialogInterface, int i) {
        if (Build.VERSION.SDK_INT < 23 || this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(messageObject, keyboardButton);
            return;
        }
        this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
        this.pendingMessageObject = messageObject;
        this.pendingLocationButton = keyboardButton;
    }

    public /* synthetic */ void lambda$didPressedBotButton$26$ChatActivityEnterView(MessageObject messageObject, TLRPC.KeyboardButton keyboardButton, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        TLRPC.Message message = messageObject.messageOwner;
        int i = message.from_id;
        int i2 = message.via_bot_id;
        if (i2 == 0) {
            i2 = i;
        }
        TLRPC.User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i2));
        if (user == null) {
            dialogsActivity.finishFragment();
            return;
        }
        long longValue = ((Long) arrayList.get(0)).longValue();
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        instance.saveDraft(longValue, "@" + user.username + " " + keyboardButton.query, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.Message) null, true);
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
            this.emojiView = new EmojiView(this.allowStickers, this.allowGifs, this.parentActivity, true, this.info);
            this.emojiView.setVisibility(8);
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

                public void onStickerSelected(View view, TLRPC.Document document, Object obj, boolean z, int i) {
                    if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                int unused = ChatActivityEnterView.this.searchingType = 0;
                                ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(document));
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        ChatActivityEnterView.this.lambda$onStickerSelected$27$ChatActivityEnterView(document, obj, false, z, i);
                        if (((int) ChatActivityEnterView.this.dialog_id) == 0 && MessageObject.isGifDocument(document)) {
                            ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj, document);
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
                public void lambda$onGifSelected$0$ChatActivityEnterView$35(View view, Object obj, Object obj2, boolean z, int i) {
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
                                ChatActivityEnterView.AnonymousClass35.this.lambda$onGifSelected$0$ChatActivityEnterView$35(this.f$1, this.f$2, this.f$3, z, i);
                            }
                        });
                    } else if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        if (obj3 instanceof TLRPC.Document) {
                            TLRPC.Document document = (TLRPC.Document) obj3;
                            SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(document, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, obj2, z, i);
                            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
                            if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                                ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj4, document);
                            }
                        } else if (obj3 instanceof TLRPC.BotInlineResult) {
                            TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) obj3;
                            if (botInlineResult.document != null) {
                                MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(botInlineResult.document, (int) (System.currentTimeMillis() / 1000));
                                if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                                    ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj4, botInlineResult.document);
                                }
                            }
                            TLRPC.User user = (TLRPC.User) obj4;
                            HashMap hashMap = new HashMap();
                            hashMap.put("id", botInlineResult.id);
                            hashMap.put("query_id", "" + botInlineResult.query_id);
                            SendMessagesHelper.prepareSendingBotContextResult(ChatActivityEnterView.this.accountInstance, botInlineResult, hashMap, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, z, i);
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
                                ChatActivityEnterView.AnonymousClass35.this.lambda$onClearEmojiRecent$1$ChatActivityEnterView$35(dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                    }
                }

                public /* synthetic */ void lambda$onClearEmojiRecent$1$ChatActivityEnterView$35(DialogInterface dialogInterface, int i) {
                    ChatActivityEnterView.this.emojiView.clearRecentEmoji();
                }

                public void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
                    if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        if (stickerSet != null) {
                            inputStickerSet = new TLRPC.TL_inputStickerSetID();
                            inputStickerSet.access_hash = stickerSet.access_hash;
                            inputStickerSet.id = stickerSet.id;
                        }
                        ChatActivityEnterView.this.parentFragment.showDialog(new StickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, inputStickerSet, (TLRPC.TL_messages_stickerSet) null, ChatActivityEnterView.this));
                    }
                }

                public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered) {
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).removeStickersSet(ChatActivityEnterView.this.parentActivity, stickerSetCovered.set, 2, ChatActivityEnterView.this.parentFragment, false);
                }

                public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered) {
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).removeStickersSet(ChatActivityEnterView.this.parentActivity, stickerSetCovered.set, 0, ChatActivityEnterView.this.parentFragment, false);
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
                            int access$9600 = chatActivityEnterView2.stickersExpandedHeight;
                            int dp = AndroidUtilities.dp(120.0f);
                            Point point = AndroidUtilities.displaySize;
                            int unused4 = chatActivityEnterView2.stickersExpandedHeight = Math.min(access$9600, dp + (point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight));
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
                        int access$9800 = point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                        float max = (float) Math.max(Math.min(i + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - access$9800));
                        ChatActivityEnterView.this.emojiView.setTranslationY(max);
                        ChatActivityEnterView.this.setTranslationY(max);
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        float unused = chatActivityEnterView.stickersExpansionProgress = max / ((float) (-(chatActivityEnterView.stickersExpandedHeight - access$9800)));
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
    public void lambda$onStickerSelected$27$ChatActivityEnterView(TLRPC.Document document, Object obj, boolean z, boolean z2, int i) {
        if (isInScheduleMode() && i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(document, obj, z) {
                private final /* synthetic */ TLRPC.Document f$1;
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
            SendMessagesHelper.getInstance(this.currentAccount).sendSticker(document, this.dialog_id, this.replyingMessageObject, obj, z2, i);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, true, i);
            }
            if (z) {
                setFieldText("");
            }
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, obj, document, (int) (System.currentTimeMillis() / 1000), false);
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

    public void addStickerToRecent(TLRPC.Document document) {
        createEmojiView();
        this.emojiView.addRecentSticker(document);
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
    public void showPopup(int i, int i2) {
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
                if (!(botKeyboardView2 == null || botKeyboardView2.getVisibility() == 8)) {
                    this.botKeyboardView.setVisibility(8);
                }
                view = this.emojiView;
            } else if (i2 == 1) {
                this.botKeyboardViewVisible = true;
                EmojiView emojiView2 = this.emojiView;
                if (!(emojiView2 == null || emojiView2.getVisibility() == 8)) {
                    this.sizeNotifierLayout.removeView(this.emojiView);
                    this.emojiView.setVisibility(8);
                    this.emojiViewVisible = false;
                }
                this.botKeyboardView.setVisibility(0);
                view = this.botKeyboardView;
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
            }
        } else {
            if (this.emojiButton != null) {
                setEmojiButtonImage(false, true);
            }
            this.currentPopupContentType = -1;
            if (this.emojiView != null) {
                this.emojiViewVisible = false;
                if (i != 2 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    this.sizeNotifierLayout.removeView(this.emojiView);
                    this.emojiView.setVisibility(8);
                }
            }
            if (this.botKeyboardView != null) {
                this.botKeyboardViewVisible = false;
                if (AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    this.botKeyboardView.setVisibility(8);
                }
            }
            if (this.sizeNotifierLayout != null) {
                if (i == 0) {
                    this.emojiPadding = 0;
                }
                this.sizeNotifierLayout.requestLayout();
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

    private void setEmojiButtonImage(boolean z, boolean z2) {
        int i;
        int i2;
        char c = z2;
        if (z2) {
            c = z2;
            if (this.currentEmojiIcon == -1) {
                c = 0;
            }
        }
        if (!z || this.currentPopupContentType != 0) {
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 == null) {
                i2 = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
            } else {
                i2 = emojiView2.getCurrentPage();
            }
            i = (i2 == 0 || (!this.allowStickers && !this.allowGifs)) ? 1 : i2 == 1 ? 2 : 3;
        } else {
            i = 0;
        }
        if (this.currentEmojiIcon != i) {
            AnimatorSet animatorSet = this.emojiButtonAnimation;
            int i3 = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiButtonAnimation = null;
            }
            if (i == 0) {
                this.emojiButton[c].setImageResource(NUM);
            } else if (i == 1) {
                this.emojiButton[c].setImageResource(NUM);
            } else if (i == 2) {
                this.emojiButton[c].setImageResource(NUM);
            } else if (i == 3) {
                this.emojiButton[c].setImageResource(NUM);
            }
            ImageView imageView = this.emojiButton[c];
            if (i == 2) {
                i3 = 1;
            }
            imageView.setTag(i3);
            this.currentEmojiIcon = i;
            if (c != 0) {
                this.emojiButton[1].setVisibility(0);
                this.emojiButtonAnimation = new AnimatorSet();
                this.emojiButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f})});
                this.emojiButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.emojiButtonAnimation)) {
                            AnimatorSet unused = ChatActivityEnterView.this.emojiButtonAnimation = null;
                            ImageView imageView = ChatActivityEnterView.this.emojiButton[1];
                            ChatActivityEnterView.this.emojiButton[1] = ChatActivityEnterView.this.emojiButton[0];
                            ChatActivityEnterView.this.emojiButton[0] = imageView;
                            ChatActivityEnterView.this.emojiButton[1].setVisibility(4);
                            ChatActivityEnterView.this.emojiButton[1].setAlpha(0.0f);
                            ChatActivityEnterView.this.emojiButton[1].setScaleX(0.1f);
                            ChatActivityEnterView.this.emojiButton[1].setScaleY(0.1f);
                        }
                    }
                });
                this.emojiButtonAnimation.setDuration(150);
                this.emojiButtonAnimation.start();
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

    public void addRecentGif(TLRPC.Document document) {
        MediaDataController.getInstance(this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.addRecentGif(document);
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
                if (this.sizeNotifierLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    this.sizeNotifierLayout.requestLayout();
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
        if (this.keyboardVisible && isPopupShowing()) {
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
        TLRPC.ChatFull chatFull;
        TLRPC.Chat chat;
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
                long longValue = objArr[1].longValue();
                long j = longValue / 1000;
                String format = String.format("%02d:%02d.%02d", new Object[]{Long.valueOf(j / 60), Long.valueOf(j % 60), Integer.valueOf(((int) (longValue % 1000)) / 10)});
                String str = this.lastTimeString;
                if (str == null || !str.equals(format)) {
                    if (this.lastTypingSendTime != j && j % 5 == 0 && !isInScheduleMode()) {
                        this.lastTypingSendTime = j;
                        MessagesController messagesController = this.accountInstance.getMessagesController();
                        long j2 = this.dialog_id;
                        ImageView imageView = this.videoSendButton;
                        messagesController.sendTyping(j2, (imageView == null || imageView.getTag() == null) ? 1 : 7, 0);
                    }
                    TextView textView = this.recordTimeText;
                    if (textView != null) {
                        textView.setText(format);
                    }
                }
                RecordCircle recordCircle2 = this.recordCircle;
                if (recordCircle2 != null) {
                    recordCircle2.setAmplitude(objArr[2].doubleValue());
                }
                ImageView imageView2 = this.videoSendButton;
                if (imageView2 != null && imageView2.getTag() != null && longValue >= 59500) {
                    this.startedDraggingX = -1.0f;
                    this.delegate.needStartRecordVideo(3, true, 0);
                }
            }
        } else if (i == NotificationCenter.closeChats) {
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null && editTextCaption.isFocused()) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
        } else if (i == NotificationCenter.recordStartError || i == NotificationCenter.recordStopped) {
            if (objArr[0].intValue() == this.recordingGuid) {
                if (this.recordingAudioVideo) {
                    this.accountInstance.getMessagesController().sendTyping(this.dialog_id, 2, 0);
                    this.recordingAudioVideo = false;
                    updateRecordIntefrace();
                }
                if (i == NotificationCenter.recordStopped) {
                    Integer num = objArr[1];
                    if (num.intValue() == 2) {
                        this.videoTimelineView.setVisibility(0);
                        this.recordedAudioBackground.setVisibility(8);
                        this.recordedAudioTimeTextView.setVisibility(8);
                        this.recordedAudioPlayButton.setVisibility(8);
                        this.recordedAudioSeekBar.setVisibility(8);
                        this.recordedAudioPanel.setAlpha(1.0f);
                        this.recordedAudioPanel.setVisibility(0);
                        return;
                    }
                    num.intValue();
                }
            }
        } else if (i == NotificationCenter.recordStarted) {
            if (objArr[0].intValue() == this.recordingGuid && !this.recordingAudioVideo) {
                this.recordingAudioVideo = true;
                updateRecordIntefrace();
            }
        } else if (i == NotificationCenter.audioDidSent) {
            if (objArr[0].intValue() == this.recordingGuid) {
                VideoEditedInfo videoEditedInfo = objArr[1];
                if (videoEditedInfo instanceof VideoEditedInfo) {
                    this.videoToSendMessageObject = videoEditedInfo;
                    this.audioToSendPath = objArr[2];
                    this.videoTimelineView.setVideoPath(this.audioToSendPath);
                    this.videoTimelineView.setVisibility(0);
                    this.videoTimelineView.setMinProgressDiff(1000.0f / ((float) this.videoToSendMessageObject.estimatedDuration));
                    this.recordedAudioBackground.setVisibility(8);
                    this.recordedAudioTimeTextView.setVisibility(8);
                    this.recordedAudioPlayButton.setVisibility(8);
                    this.recordedAudioSeekBar.setVisibility(8);
                    this.recordedAudioPanel.setAlpha(1.0f);
                    this.recordedAudioPanel.setVisibility(0);
                    closeKeyboard();
                    hidePopup(false);
                    checkSendButton(false);
                    return;
                }
                this.audioToSend = objArr[1];
                this.audioToSendPath = objArr[2];
                if (this.audioToSend == null) {
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
                    TLRPC.TL_message tL_message = new TLRPC.TL_message();
                    tL_message.out = true;
                    tL_message.id = 0;
                    tL_message.to_id = new TLRPC.TL_peerUser();
                    TLRPC.Peer peer = tL_message.to_id;
                    int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    tL_message.from_id = clientUserId;
                    peer.user_id = clientUserId;
                    tL_message.date = (int) (System.currentTimeMillis() / 1000);
                    tL_message.message = "";
                    tL_message.attachPath = this.audioToSendPath;
                    tL_message.media = new TLRPC.TL_messageMediaDocument();
                    TLRPC.MessageMedia messageMedia = tL_message.media;
                    messageMedia.flags |= 3;
                    messageMedia.document = this.audioToSend;
                    tL_message.flags |= 768;
                    this.audioToSendMessageObject = new MessageObject(UserConfig.selectedAccount, tL_message, false);
                    this.recordedAudioPanel.setAlpha(1.0f);
                    this.recordedAudioPanel.setVisibility(0);
                    int i5 = 0;
                    while (true) {
                        if (i5 >= this.audioToSend.attributes.size()) {
                            i3 = 0;
                            break;
                        }
                        TLRPC.DocumentAttribute documentAttribute = this.audioToSend.attributes.get(i5);
                        if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                            i3 = documentAttribute.duration;
                            break;
                        }
                        i5++;
                    }
                    int i6 = 0;
                    while (true) {
                        if (i6 >= this.audioToSend.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute documentAttribute2 = this.audioToSend.attributes.get(i6);
                        if (documentAttribute2 instanceof TLRPC.TL_documentAttributeAudio) {
                            byte[] bArr = documentAttribute2.waveform;
                            if (bArr == null || bArr.length == 0) {
                                documentAttribute2.waveform = MediaController.getInstance().getWaveform(this.audioToSendPath);
                            }
                            this.recordedAudioSeekBar.setWaveform(documentAttribute2.waveform);
                        } else {
                            i6++;
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
                this.recordedAudioPlayButton.setImageDrawable(this.playDrawable);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
                this.recordedAudioSeekBar.setProgress(0.0f);
            }
        } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer num2 = objArr[0];
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
            if (!objArr[6].booleanValue() && objArr[3].longValue() == this.dialog_id && (chatFull = this.info) != null && chatFull.slowmode_seconds != 0 && (chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(this.info.id))) != null && !ChatObject.hasAdminRights(chat)) {
                TLRPC.ChatFull chatFull2 = this.info;
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                TLRPC.ChatFull chatFull3 = this.info;
                chatFull2.slowmode_next_send_date = currentTime + chatFull3.slowmode_seconds;
                chatFull3.flags |= 262144;
                setSlowModeTimer(chatFull3.slowmode_next_send_date);
            }
        } else if (i == NotificationCenter.sendingMessagesChanged && this.info != null) {
            updateSlowModeText();
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
                this.originalViewHeight = this.sizeNotifierLayout.getHeight();
                this.stickersExpandedHeight = (((this.originalViewHeight - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                if (this.searchingType == 2) {
                    this.stickersExpandedHeight = Math.min(this.stickersExpandedHeight, AndroidUtilities.dp(120.0f) + i);
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

    private class ScrimDrawable extends Drawable {
        private Paint paint = new Paint();

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public ScrimDrawable() {
            this.paint.setColor(0);
        }

        public void draw(Canvas canvas) {
            if (ChatActivityEnterView.this.emojiView != null) {
                this.paint.setAlpha(Math.round(ChatActivityEnterView.this.stickersExpansionProgress * 102.0f));
                canvas.drawRect(0.0f, 0.0f, (float) ChatActivityEnterView.this.getWidth(), (ChatActivityEnterView.this.emojiView.getY() - ((float) ChatActivityEnterView.this.getHeight())) + ((float) Theme.chat_composeShadowDrawable.getIntrinsicHeight()), this.paint);
            }
        }
    }
}
