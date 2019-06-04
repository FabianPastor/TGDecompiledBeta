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
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
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
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_keyboardButton;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestPhone;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EmojiView.DragListener;
import org.telegram.ui.Components.EmojiView.EmojiViewDelegate;
import org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate;
import org.telegram.ui.Components.StickersAlert.StickersAlertDelegate;
import org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupStickersActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.StickersActivity;

public class ChatActivityEnterView extends FrameLayout implements NotificationCenterDelegate, SizeNotifierFrameLayoutDelegate, StickersAlertDelegate {
    private boolean allowGifs;
    private boolean allowShowTopView;
    private boolean allowStickers;
    private ImageView attachButton;
    private LinearLayout attachLayout;
    private ImageView audioSendButton;
    private TL_document audioToSend;
    private MessageObject audioToSendMessageObject;
    private String audioToSendPath;
    private AnimatorSet audioVideoButtonAnimation;
    private FrameLayout audioVideoButtonContainer;
    private ImageView botButton;
    private MessageObject botButtonsMessageObject;
    private int botCount;
    private PopupWindow botKeyboardPopup;
    private BotKeyboardView botKeyboardView;
    private MessageObject botMessageObject;
    private TL_replyKeyboardMarkup botReplyMarkup;
    private boolean calledRecordRunnable;
    private Drawable cameraDrawable;
    private boolean canWriteToChannel;
    private ImageView cancelBotButton;
    private boolean closeAnimationInProgress;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentEmojiIcon = -1;
    private int currentPopupContentType = -1;
    private Animator currentResizeAnimation;
    private AnimatorSet currentTopViewAnimation;
    private ChatActivityEnterViewDelegate delegate;
    private boolean destroyed;
    private long dialog_id;
    private float distCanMove = ((float) AndroidUtilities.dp(80.0f));
    private AnimatorSet doneButtonAnimation;
    private FrameLayout doneButtonContainer;
    private ImageView doneButtonImage;
    private ContextProgressView doneButtonProgress;
    private Paint dotPaint = new Paint(1);
    private boolean editingCaption;
    private MessageObject editingMessageObject;
    private int editingMessageReqId;
    private ImageView[] emojiButton = new ImageView[2];
    private AnimatorSet emojiButtonAnimation;
    private int emojiPadding;
    private boolean emojiTabOpen;
    private EmojiView emojiView;
    private boolean emojiViewVisible;
    private ImageView expandStickersButton;
    private boolean forceShowSendButton;
    private boolean gifsTabOpen;
    private boolean hasBotCommands;
    private boolean hasRecordVideo;
    private boolean ignoreTextChange;
    private ChatFull info;
    private int innerTextChange;
    private boolean isPaused = true;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private String lastTimeString;
    private long lastTypingSendTime;
    private long lastTypingTimeSend;
    private Drawable lockArrowDrawable;
    private Drawable lockBackgroundDrawable;
    private Drawable lockDrawable;
    private Drawable lockShadowDrawable;
    private Drawable lockTopDrawable;
    private AccessibilityDelegate mediaMessageButtonsDelegate = new AccessibilityDelegate() {
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.widget.ImageButton");
            accessibilityNodeInfo.setClickable(true);
            accessibilityNodeInfo.setLongClickable(true);
        }
    };
    private EditTextCaption messageEditText;
    private WebPage messageWebPage;
    private boolean messageWebPageSearch = true;
    private Drawable micDrawable;
    private boolean needShowTopView;
    private ImageView notifyButton;
    private Runnable onFinishInitCameraRunnable = new Runnable() {
        public void run() {
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.needStartRecordVideo(0);
            }
        }
    };
    private Runnable openKeyboardRunnable = new Runnable() {
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
    private Paint paint = new Paint(1);
    private Paint paintRecord = new Paint(1);
    private Activity parentActivity;
    private ChatActivity parentFragment;
    private Drawable pauseDrawable;
    private KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    private Drawable playDrawable;
    private CloseProgressDrawable2 progressDrawable;
    private Runnable recordAudioVideoRunnable = new Runnable() {
        public void run() {
            if (!(ChatActivityEnterView.this.delegate == null || ChatActivityEnterView.this.parentActivity == null)) {
                ChatActivityEnterView.this.delegate.onPreAudioVideoRecord();
                ChatActivityEnterView.this.calledRecordRunnable = true;
                ChatActivityEnterView.this.recordAudioVideoRunnableStarted = false;
                ChatActivityEnterView.this.recordCircle.setLockTranslation(10000.0f);
                ChatActivityEnterView.this.recordSendText.setAlpha(0.0f);
                ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                ChatActivityEnterView.this.slideText.setTranslationY(0.0f);
                String str = "android.permission.RECORD_AUDIO";
                if (ChatActivityEnterView.this.videoSendButton != null && ChatActivityEnterView.this.videoSendButton.getTag() != null) {
                    if (VERSION.SDK_INT >= 23) {
                        Object obj = ChatActivityEnterView.this.parentActivity.checkSelfPermission(str) == 0 ? 1 : null;
                        String str2 = "android.permission.CAMERA";
                        Object obj2 = ChatActivityEnterView.this.parentActivity.checkSelfPermission(str2) == 0 ? 1 : null;
                        if (obj == null || obj2 == null) {
                            int i = (obj == null && obj2 == null) ? 2 : 1;
                            String[] strArr = new String[i];
                            if (obj == null && obj2 == null) {
                                strArr[0] = str;
                                strArr[1] = str2;
                            } else if (obj == null) {
                                strArr[0] = str;
                            } else {
                                strArr[0] = str2;
                            }
                            ChatActivityEnterView.this.parentActivity.requestPermissions(strArr, 3);
                            return;
                        }
                    }
                    if (CameraController.getInstance().isCameraInitied()) {
                        ChatActivityEnterView.this.onFinishInitCameraRunnable.run();
                    } else {
                        CameraController.getInstance().initCamera(ChatActivityEnterView.this.onFinishInitCameraRunnable);
                    }
                } else if (ChatActivityEnterView.this.parentFragment == null || VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission(str) == 0) {
                    ChatActivityEnterView.this.delegate.needStartRecordAudio(1);
                    ChatActivityEnterView.this.startedDraggingX = -1.0f;
                    MediaController.getInstance().startRecording(ChatActivityEnterView.this.currentAccount, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
                    ChatActivityEnterView.this.updateRecordIntefrace();
                    ChatActivityEnterView.this.audioVideoButtonContainer.getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{str}, 3);
                }
            }
        }
    };
    private boolean recordAudioVideoRunnableStarted;
    private ImageView recordCancelImage;
    private TextView recordCancelText;
    private RecordCircle recordCircle;
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
    private FrameLayout recordPanel;
    private TextView recordSendText;
    private LinearLayout recordTimeContainer;
    private TextView recordTimeText;
    private View recordedAudioBackground;
    private FrameLayout recordedAudioPanel;
    private ImageView recordedAudioPlayButton;
    private SeekBarWaveformView recordedAudioSeekBar;
    private TextView recordedAudioTimeTextView;
    private boolean recordingAudioVideo;
    private RectF rect = new RectF();
    private Paint redDotPaint = new Paint(1);
    private MessageObject replyingMessageObject;
    private Property<View, Integer> roundedTranslationYProperty = new Property<View, Integer>(Integer.class, "translationY") {
        public Integer get(View view) {
            return Integer.valueOf(Math.round(view.getTranslationY()));
        }

        public void set(View view, Integer num) {
            view.setTranslationY((float) num.intValue());
        }
    };
    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private AnimatorSet runningAnimationAudio;
    private int runningAnimationType;
    private int searchingType;
    private SeekBarWaveform seekBarWaveform;
    private ImageView sendButton;
    private FrameLayout sendButtonContainer;
    private boolean sendByEnter;
    private Drawable sendDrawable;
    private boolean showKeyboardOnResume;
    private boolean silent;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private LinearLayout slideText;
    private float startedDraggingX = -1.0f;
    private AnimatedArrowDrawable stickersArrow;
    private boolean stickersDragging;
    private boolean stickersExpanded;
    private int stickersExpandedHeight;
    private Animator stickersExpansionAnim;
    private float stickersExpansionProgress;
    private boolean stickersTabOpen;
    private LinearLayout textFieldContainer;
    private View topLineView;
    private View topView;
    private boolean topViewShowed;
    private Runnable updateExpandabilityRunnable = new Runnable() {
        private int lastKnownPage = -1;

        public void run() {
            if (ChatActivityEnterView.this.emojiView != null) {
                int currentPage = ChatActivityEnterView.this.emojiView.getCurrentPage();
                if (currentPage != this.lastKnownPage) {
                    this.lastKnownPage = currentPage;
                    boolean access$800 = ChatActivityEnterView.this.stickersTabOpen;
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    int i = 2;
                    boolean z = currentPage == 1 || currentPage == 2;
                    chatActivityEnterView.stickersTabOpen = z;
                    boolean access$900 = ChatActivityEnterView.this.emojiTabOpen;
                    ChatActivityEnterView.this.emojiTabOpen = currentPage == 0;
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        if (!ChatActivityEnterView.this.stickersTabOpen && ChatActivityEnterView.this.searchingType == 0) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.searchingType = 0;
                                ChatActivityEnterView.this.emojiView.closeSearch(true);
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        } else if (ChatActivityEnterView.this.searchingType != 0) {
                            ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                            if (currentPage != 0) {
                                i = 1;
                            }
                            chatActivityEnterView2.searchingType = i;
                            ChatActivityEnterView.this.checkStickresExpandHeight();
                        }
                    }
                    if (access$800 != ChatActivityEnterView.this.stickersTabOpen || access$900 != ChatActivityEnterView.this.emojiTabOpen) {
                        ChatActivityEnterView.this.checkSendButton(true);
                    }
                }
            }
        }
    };
    private ImageView videoSendButton;
    private VideoTimelineView videoTimelineView;
    private VideoEditedInfo videoToSendMessageObject;
    private boolean waitingForKeyboardOpen;
    private WakeLock wakeLock;

    public interface ChatActivityEnterViewDelegate {
        void didPressedAttachButton();

        void needChangeVideoPreviewState(int i, float f);

        void needSendTyping();

        void needShowMediaBanHint();

        void needStartRecordAudio(int i);

        void needStartRecordVideo(int i);

        void onAttachButtonHidden();

        void onAttachButtonShow();

        void onMessageEditEnd(boolean z);

        void onMessageSend(CharSequence charSequence);

        void onPreAudioVideoRecord();

        void onStickersExpandedChange();

        void onStickersTab(boolean z);

        void onSwitchRecordMode(boolean z);

        void onTextChanged(CharSequence charSequence, boolean z);

        void onTextSelectionChanged(int i, int i2);

        void onTextSpansChanged(CharSequence charSequence);

        void onWindowSizeChanged(int i);
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
        private float startTranslation;
        private VirtualViewHelper virtualViewHelper = new VirtualViewHelper(this);

        private class VirtualViewHelper extends ExploreByTouchHelper {
            /* Access modifiers changed, original: protected */
            public boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
                return true;
            }

            public VirtualViewHelper(View view) {
                super(view);
            }

            /* Access modifiers changed, original: protected */
            public int getVirtualViewAt(float f, float f2) {
                if (RecordCircle.this.isSendButtonVisible()) {
                    int i = (int) f;
                    int i2 = (int) f2;
                    if (ChatActivityEnterView.this.sendDrawable.getBounds().contains(i, i2)) {
                        return 1;
                    }
                    if (ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains(i, i2)) {
                        return 2;
                    }
                }
                return -1;
            }

            /* Access modifiers changed, original: protected */
            public void getVisibleVirtualViews(List<Integer> list) {
                if (RecordCircle.this.isSendButtonVisible()) {
                    list.add(Integer.valueOf(1));
                    list.add(Integer.valueOf(2));
                }
            }

            /* Access modifiers changed, original: protected */
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

        public RecordCircle(Context context) {
            super(context);
            ChatActivityEnterView.this.paint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
            ChatActivityEnterView.this.paintRecord.setColor(Theme.getColor("chat_messagePanelVoiceShadow"));
            ChatActivityEnterView.this.lockDrawable = getResources().getDrawable(NUM);
            String str = "key_chat_messagePanelVoiceLock";
            ChatActivityEnterView.this.lockDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockTopDrawable = getResources().getDrawable(NUM);
            ChatActivityEnterView.this.lockTopDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockArrowDrawable = getResources().getDrawable(NUM);
            ChatActivityEnterView.this.lockArrowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockBackgroundDrawable = getResources().getDrawable(NUM);
            ChatActivityEnterView.this.lockBackgroundDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("key_chat_messagePanelVoiceLockBackground"), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockShadowDrawable = getResources().getDrawable(NUM);
            ChatActivityEnterView.this.lockShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("key_chat_messagePanelVoiceLockShadow"), Mode.MULTIPLY));
            ChatActivityEnterView.this.micDrawable = getResources().getDrawable(NUM).mutate();
            str = "chat_messagePanelVoicePressed";
            ChatActivityEnterView.this.micDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            ChatActivityEnterView.this.cameraDrawable = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.cameraDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            ChatActivityEnterView.this.sendDrawable = getResources().getDrawable(NUM).mutate();
            ChatActivityEnterView.this.sendDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
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
                            MediaController.getInstance().stopRecording(2);
                            ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                        } else {
                            ChatActivityEnterView.this.delegate.needStartRecordVideo(3);
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            float f;
            int dp;
            int dp2;
            int dp3;
            int dp4;
            int i;
            Canvas canvas2 = canvas;
            int measuredWidth = getMeasuredWidth() / 2;
            int dp5 = AndroidUtilities.dp(170.0f);
            float f2 = this.lockAnimatedTranslation;
            if (f2 != 10000.0f) {
                f2 = (float) Math.max(0, (int) (this.startTranslation - f2));
                if (f2 > ((float) AndroidUtilities.dp(57.0f))) {
                    f2 = (float) AndroidUtilities.dp(57.0f);
                }
            } else {
                f2 = 0.0f;
            }
            dp5 = (int) (((float) dp5) - f2);
            float f3 = this.scale;
            if (f3 <= 0.5f) {
                f3 /= 0.5f;
                f = f3;
            } else {
                f3 = f3 <= 0.75f ? 1.0f - (((f3 - 0.5f) / 0.25f) * 0.1f) : (((f3 - 0.75f) / 0.25f) * 0.1f) + 0.9f;
                f = 1.0f;
            }
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            float f4 = this.animateToAmplitude;
            float f5 = this.amplitude;
            if (f4 != f5) {
                float f6 = this.animateAmplitudeDiff;
                this.amplitude = f5 + (((float) currentTimeMillis) * f6);
                if (f6 > 0.0f) {
                    if (this.amplitude > f4) {
                        this.amplitude = f4;
                    }
                } else if (this.amplitude < f4) {
                    this.amplitude = f4;
                }
                invalidate();
            }
            this.lastUpdateTime = System.currentTimeMillis();
            if (this.amplitude != 0.0f) {
                canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) dp5, (((float) AndroidUtilities.dp(42.0f)) + (((float) AndroidUtilities.dp(20.0f)) * this.amplitude)) * this.scale, ChatActivityEnterView.this.paintRecord);
            }
            canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) dp5, ((float) AndroidUtilities.dp(42.0f)) * f3, ChatActivityEnterView.this.paint);
            Drawable access$4100 = isSendButtonVisible() ? ChatActivityEnterView.this.sendDrawable : (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
            access$4100.setBounds(measuredWidth - (access$4100.getIntrinsicWidth() / 2), dp5 - (access$4100.getIntrinsicHeight() / 2), (access$4100.getIntrinsicWidth() / 2) + measuredWidth, dp5 + (access$4100.getIntrinsicHeight() / 2));
            dp5 = (int) (f * 255.0f);
            access$4100.setAlpha(dp5);
            access$4100.draw(canvas2);
            float dp6 = 1.0f - (f2 / ((float) AndroidUtilities.dp(57.0f)));
            f = Math.max(0.0f, 1.0f - ((f2 / ((float) AndroidUtilities.dp(57.0f))) * 2.0f));
            if (isSendButtonVisible()) {
                dp = AndroidUtilities.dp(31.0f);
                dp2 = AndroidUtilities.dp(57.0f) + ((int) (((((float) AndroidUtilities.dp(30.0f)) * (1.0f - f3)) - f2) + (((float) AndroidUtilities.dp(20.0f)) * dp6)));
                int dp7 = AndroidUtilities.dp(5.0f) + dp2;
                dp3 = AndroidUtilities.dp(11.0f) + dp2;
                dp4 = AndroidUtilities.dp(25.0f) + dp2;
                dp5 = (int) (((float) dp5) * (f2 / ((float) AndroidUtilities.dp(57.0f))));
                ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(255);
                ChatActivityEnterView.this.lockShadowDrawable.setAlpha(255);
                ChatActivityEnterView.this.lockTopDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) dp5) * f));
                i = dp7;
            } else {
                dp = AndroidUtilities.dp(31.0f) + ((int) (((float) AndroidUtilities.dp(29.0f)) * dp6));
                dp2 = (AndroidUtilities.dp(57.0f) + ((int) (((float) AndroidUtilities.dp(30.0f)) * (1.0f - f3)))) - ((int) f2);
                i = (AndroidUtilities.dp(5.0f) + dp2) + ((int) (((float) AndroidUtilities.dp(4.0f)) * dp6));
                dp3 = ((int) (((float) AndroidUtilities.dp(10.0f)) * dp6)) + (AndroidUtilities.dp(11.0f) + dp2);
                dp4 = (AndroidUtilities.dp(25.0f) + dp2) + ((int) (((float) AndroidUtilities.dp(16.0f)) * dp6));
                ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockShadowDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockTopDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) dp5) * f));
            }
            dp += dp2;
            ChatActivityEnterView.this.lockBackgroundDrawable.setBounds(measuredWidth - AndroidUtilities.dp(15.0f), dp2, AndroidUtilities.dp(15.0f) + measuredWidth, dp);
            ChatActivityEnterView.this.lockBackgroundDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockShadowDrawable.setBounds(measuredWidth - AndroidUtilities.dp(16.0f), dp2 - AndroidUtilities.dp(1.0f), AndroidUtilities.dp(16.0f) + measuredWidth, dp + AndroidUtilities.dp(1.0f));
            ChatActivityEnterView.this.lockShadowDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockTopDrawable.setBounds(measuredWidth - AndroidUtilities.dp(6.0f), i, AndroidUtilities.dp(6.0f) + measuredWidth, AndroidUtilities.dp(14.0f) + i);
            ChatActivityEnterView.this.lockTopDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockDrawable.setBounds(measuredWidth - AndroidUtilities.dp(7.0f), dp3, AndroidUtilities.dp(7.0f) + measuredWidth, AndroidUtilities.dp(12.0f) + dp3);
            ChatActivityEnterView.this.lockDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockArrowDrawable.setBounds(measuredWidth - AndroidUtilities.dp(7.5f), dp4, AndroidUtilities.dp(7.5f) + measuredWidth, AndroidUtilities.dp(9.0f) + dp4);
            ChatActivityEnterView.this.lockArrowDrawable.draw(canvas2);
            if (isSendButtonVisible()) {
                ChatActivityEnterView.this.redDotPaint.setAlpha(255);
                ChatActivityEnterView.this.rect.set((float) (measuredWidth - AndroidUtilities.dp2(6.5f)), (float) (AndroidUtilities.dp(9.0f) + dp2), (float) (measuredWidth + AndroidUtilities.dp(6.5f)), (float) (dp2 + AndroidUtilities.dp(22.0f)));
                canvas2.drawRoundRect(ChatActivityEnterView.this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ChatActivityEnterView.this.redDotPaint);
            }
        }

        /* Access modifiers changed, original: protected */
        public boolean dispatchHoverEvent(MotionEvent motionEvent) {
            return super.dispatchHoverEvent(motionEvent) || this.virtualViewHelper.dispatchHoverEvent(motionEvent);
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

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            ChatActivityEnterView.this.redDotPaint.setAlpha((int) (this.alpha * 255.0f));
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.isIncr) {
                this.alpha += ((float) currentTimeMillis) / 400.0f;
                if (this.alpha >= 1.0f) {
                    this.alpha = 1.0f;
                    this.isIncr = false;
                }
            } else {
                this.alpha -= ((float) currentTimeMillis) / 400.0f;
                if (this.alpha <= 0.0f) {
                    this.alpha = 0.0f;
                    this.isIncr = true;
                }
            }
            this.lastUpdateTime = System.currentTimeMillis();
            canvas.drawCircle((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.redDotPaint);
            invalidate();
        }
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

    private class SeekBarWaveformView extends View {
        public SeekBarWaveformView(Context context) {
            super(context);
            ChatActivityEnterView.this.seekBarWaveform = new SeekBarWaveform(context);
            ChatActivityEnterView.this.seekBarWaveform.setDelegate(new -$$Lambda$ChatActivityEnterView$SeekBarWaveformView$3LQzMaku4bFncUmbMthE8hwccgc(this));
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

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ChatActivityEnterView.this.seekBarWaveform.setSize(i3 - i, i4 - i2);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            String str = "chat_recordedVoiceProgress";
            ChatActivityEnterView.this.seekBarWaveform.setColors(Theme.getColor(str), Theme.getColor("chat_recordedVoiceProgressInner"), Theme.getColor(str));
            ChatActivityEnterView.this.seekBarWaveform.draw(canvas);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public ChatActivityEnterView(Activity activity, SizeNotifierFrameLayout sizeNotifierFrameLayout, ChatActivity chatActivity, boolean z) {
        String str;
        Activity activity2 = activity;
        super(activity);
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
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        this.parentActivity = activity2;
        this.parentFragment = chatActivity;
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        this.sizeNotifierLayout.setDelegate(this);
        this.sendByEnter = MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false);
        this.textFieldContainer = new LinearLayout(activity2);
        this.textFieldContainer.setOrientation(0);
        addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 2.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(activity2);
        this.textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f, 80));
        int i = 0;
        while (true) {
            str = "chat_messagePanelIcons";
            if (i >= 2) {
                break;
            }
            this.emojiButton[i] = new ImageView(activity2) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (getTag() != null && ChatActivityEnterView.this.attachLayout != null && !ChatActivityEnterView.this.emojiViewVisible && !DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).getUnreadStickerSets().isEmpty() && ChatActivityEnterView.this.dotPaint != null) {
                        canvas.drawCircle((float) ((getWidth() / 2) + AndroidUtilities.dp(9.0f)), (float) ((getHeight() / 2) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.dotPaint);
                    }
                }
            };
            this.emojiButton[i].setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.emojiButton[i].setScaleType(ScaleType.CENTER_INSIDE);
            frameLayout.addView(this.emojiButton[i], LayoutHelper.createFrame(48, 48.0f, 83, 3.0f, 0.0f, 0.0f, 0.0f));
            this.emojiButton[i].setOnClickListener(new -$$Lambda$ChatActivityEnterView$jlpojNCIsF-c7hlFcVLHdouZye4(this));
            this.emojiButton[i].setContentDescription(LocaleController.getString("AccDescrEmojiButton", NUM));
            if (i == 1) {
                this.emojiButton[i].setVisibility(4);
                this.emojiButton[i].setAlpha(0.0f);
                this.emojiButton[i].setScaleX(0.1f);
                this.emojiButton[i].setScaleY(0.1f);
            }
            i++;
        }
        setEmojiButtonImage(false, false);
        this.messageEditText = new EditTextCaption(activity2) {
            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
                EditorInfoCompat.setContentMimeTypes(editorInfo, new String[]{"image/gif", "image/*", "image/jpg", "image/png"});
                return InputConnectionCompat.createWrapper(onCreateInputConnection, editorInfo, new -$$Lambda$ChatActivityEnterView$9$ETBc4NSNPoua6FyaOMaLXYYn2ak(this));
            }

            public /* synthetic */ boolean lambda$onCreateInputConnection$0$ChatActivityEnterView$9(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
                if (BuildCompat.isAtLeastNMR1() && (i & 1) != 0) {
                    try {
                        inputContentInfoCompat.requestPermission();
                    } catch (Exception unused) {
                        return false;
                    }
                }
                if (inputContentInfoCompat.getDescription().hasMimeType("image/gif")) {
                    SendMessagesHelper.prepareSendingDocument(null, null, inputContentInfoCompat.getContentUri(), null, "image/gif", ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, inputContentInfoCompat, null);
                } else {
                    SendMessagesHelper.prepareSendingPhoto(null, inputContentInfoCompat.getContentUri(), ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, null, null, null, inputContentInfoCompat, 0, null);
                }
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.onMessageSend(null);
                }
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (ChatActivityEnterView.this.isPopupShowing() && motionEvent.getAction() == 0) {
                    if (ChatActivityEnterView.this.searchingType != 0) {
                        ChatActivityEnterView.this.searchingType = 0;
                        ChatActivityEnterView.this.emojiView.closeSearch(false);
                    }
                    ChatActivityEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 0);
                    ChatActivityEnterView.this.openKeyboardInternal();
                }
                try {
                    return super.onTouchEvent(motionEvent);
                } catch (Exception e) {
                    FileLog.e(e);
                    return false;
                }
            }

            /* Access modifiers changed, original: protected */
            public void onSelectionChanged(int i, int i2) {
                super.onSelectionChanged(i, i2);
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.onTextSelectionChanged(i, i2);
                }
            }
        };
        this.messageEditText.setDelegate(new -$$Lambda$ChatActivityEnterView$YXzMuZPPiDr1K7R7cpN8afDWrUQ(this));
        updateFieldHint();
        i = NUM;
        ChatActivity chatActivity2 = this.parentFragment;
        if (!(chatActivity2 == null || chatActivity2.getCurrentEncryptedChat() == null)) {
            i = NUM;
        }
        this.messageEditText.setImeOptions(i);
        EditTextCaption editTextCaption = this.messageEditText;
        editTextCaption.setInputType((editTextCaption.getInputType() | 16384) | 131072);
        this.messageEditText.setSingleLine(false);
        this.messageEditText.setMaxLines(6);
        this.messageEditText.setTextSize(1, 18.0f);
        this.messageEditText.setGravity(80);
        this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        this.messageEditText.setBackgroundDrawable(null);
        this.messageEditText.setTextColor(Theme.getColor("chat_messagePanelText"));
        this.messageEditText.setHintColor(Theme.getColor("chat_messagePanelHint"));
        this.messageEditText.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        frameLayout.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 80, 52.0f, 0.0f, z ? 50.0f : 2.0f, 0.0f));
        this.messageEditText.setOnKeyListener(new OnKeyListener() {
            boolean ctrlPressed = false;

            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean z = false;
                if (i == 4 && !ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing()) {
                    if (keyEvent.getAction() == 1) {
                        if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                            Editor edit = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("hidekeyboard_");
                            stringBuilder.append(ChatActivityEnterView.this.dialog_id);
                            edit.putInt(stringBuilder.toString(), ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                        }
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            ChatActivityEnterView.this.searchingType = 0;
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
        this.messageEditText.setOnEditorActionListener(new OnEditorActionListener() {
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

            /* JADX WARNING: Missing block: B:46:0x00ed, code skipped:
            if (org.telegram.messenger.MessagesController.getInstance(org.telegram.ui.Components.ChatActivityEnterView.access$2600(r4.this$0)).onlinePrivacy.containsKey(java.lang.Integer.valueOf(r6.id)) == false) goto L_0x00ef;
     */
            public void onTextChanged(java.lang.CharSequence r5, int r6, int r7, int r8) {
                /*
                r4 = this;
                r6 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r6 = r6.innerTextChange;
                r0 = 1;
                if (r6 != r0) goto L_0x000a;
            L_0x0009:
                return;
            L_0x000a:
                r6 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r6.checkSendButton(r0);
                r6 = r5.toString();
                r6 = org.telegram.messenger.AndroidUtilities.getTrimmedString(r6);
                r1 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r1 = r1.delegate;
                r2 = 2;
                if (r1 == 0) goto L_0x004c;
            L_0x0020:
                r1 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r1 = r1.ignoreTextChange;
                if (r1 != 0) goto L_0x004c;
            L_0x0028:
                if (r8 > r2) goto L_0x0032;
            L_0x002a:
                if (r5 == 0) goto L_0x0032;
            L_0x002c:
                r1 = r5.length();
                if (r1 != 0) goto L_0x0037;
            L_0x0032:
                r1 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r1.messageWebPageSearch = r0;
            L_0x0037:
                r1 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r1 = r1.delegate;
                r3 = r8 + 1;
                if (r7 > r3) goto L_0x0048;
            L_0x0041:
                r3 = r8 - r7;
                if (r3 <= r2) goto L_0x0046;
            L_0x0045:
                goto L_0x0048;
            L_0x0046:
                r3 = 0;
                goto L_0x0049;
            L_0x0048:
                r3 = 1;
            L_0x0049:
                r1.onTextChanged(r5, r3);
            L_0x004c:
                r5 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r5 = r5.innerTextChange;
                if (r5 == r2) goto L_0x005b;
            L_0x0054:
                if (r7 == r8) goto L_0x005b;
            L_0x0056:
                r8 = r8 - r7;
                if (r8 <= r0) goto L_0x005b;
            L_0x0059:
                r4.processChange = r0;
            L_0x005b:
                r5 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r5 = r5.editingMessageObject;
                if (r5 != 0) goto L_0x010a;
            L_0x0063:
                r5 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r5 = r5.canWriteToChannel;
                if (r5 != 0) goto L_0x010a;
            L_0x006b:
                r5 = r6.length();
                if (r5 == 0) goto L_0x010a;
            L_0x0071:
                r5 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r5 = r5.lastTypingTimeSend;
                r7 = java.lang.System.currentTimeMillis();
                r0 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
                r7 = r7 - r0;
                r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
                if (r0 >= 0) goto L_0x010a;
            L_0x0082:
                r5 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r5 = r5.ignoreTextChange;
                if (r5 != 0) goto L_0x010a;
            L_0x008a:
                r5 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r5 = r5.currentAccount;
                r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5);
                r5 = r5.getCurrentTime();
                r6 = 0;
                r7 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r7 = r7.dialog_id;
                r8 = (int) r7;
                if (r8 <= 0) goto L_0x00bb;
            L_0x00a2:
                r6 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r6 = r6.currentAccount;
                r6 = org.telegram.messenger.MessagesController.getInstance(r6);
                r7 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r7 = r7.dialog_id;
                r8 = (int) r7;
                r7 = java.lang.Integer.valueOf(r8);
                r6 = r6.getUser(r7);
            L_0x00bb:
                if (r6 == 0) goto L_0x00f0;
            L_0x00bd:
                r7 = r6.id;
                r8 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r8 = r8.currentAccount;
                r8 = org.telegram.messenger.UserConfig.getInstance(r8);
                r8 = r8.getClientUserId();
                if (r7 == r8) goto L_0x00ef;
            L_0x00cf:
                r7 = r6.status;
                if (r7 == 0) goto L_0x00f0;
            L_0x00d3:
                r7 = r7.expires;
                if (r7 >= r5) goto L_0x00f0;
            L_0x00d7:
                r5 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r5 = r5.currentAccount;
                r5 = org.telegram.messenger.MessagesController.getInstance(r5);
                r5 = r5.onlinePrivacy;
                r6 = r6.id;
                r6 = java.lang.Integer.valueOf(r6);
                r5 = r5.containsKey(r6);
                if (r5 != 0) goto L_0x00f0;
            L_0x00ef:
                return;
            L_0x00f0:
                r5 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r6 = java.lang.System.currentTimeMillis();
                r5.lastTypingTimeSend = r6;
                r5 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r5 = r5.delegate;
                if (r5 == 0) goto L_0x010a;
            L_0x0101:
                r5 = org.telegram.ui.Components.ChatActivityEnterView.this;
                r5 = r5.delegate;
                r5.needSendTyping();
            L_0x010a:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView$AnonymousClass12.onTextChanged(java.lang.CharSequence, int, int, int):void");
            }

            public void afterTextChanged(Editable editable) {
                if (ChatActivityEnterView.this.innerTextChange == 0) {
                    if (ChatActivityEnterView.this.sendByEnter && editable.length() > 0 && editable.charAt(editable.length() - 1) == 10 && ChatActivityEnterView.this.editingMessageObject == null) {
                        ChatActivityEnterView.this.sendMessage();
                    }
                    if (this.processChange) {
                        ImageSpan[] imageSpanArr = (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class);
                        for (Object removeSpan : imageSpanArr) {
                            editable.removeSpan(removeSpan);
                        }
                        Emoji.replaceEmoji(editable, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        this.processChange = false;
                    }
                }
            }
        });
        if (z) {
            int i2;
            String str2;
            this.attachLayout = new LinearLayout(activity2);
            this.attachLayout.setOrientation(0);
            this.attachLayout.setEnabled(false);
            this.attachLayout.setPivotX((float) AndroidUtilities.dp(48.0f));
            frameLayout.addView(this.attachLayout, LayoutHelper.createFrame(-2, 48, 85));
            this.botButton = new ImageView(activity2);
            this.botButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.botButton.setImageResource(NUM);
            this.botButton.setScaleType(ScaleType.CENTER);
            this.botButton.setVisibility(8);
            this.attachLayout.addView(this.botButton, LayoutHelper.createLinear(48, 48));
            this.botButton.setOnClickListener(new -$$Lambda$ChatActivityEnterView$ePEXGP4V8TnztLq_2EEcKkpts_k(this));
            this.notifyButton = new ImageView(activity2);
            this.notifyButton.setImageResource(this.silent ? NUM : NUM);
            ImageView imageView = this.notifyButton;
            if (this.silent) {
                i2 = NUM;
                str2 = "AccDescrChanSilentOn";
            } else {
                i2 = NUM;
                str2 = "AccDescrChanSilentOff";
            }
            imageView.setContentDescription(LocaleController.getString(str2, i2));
            this.notifyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.notifyButton.setScaleType(ScaleType.CENTER);
            this.notifyButton.setVisibility(this.canWriteToChannel ? 0 : 8);
            this.attachLayout.addView(this.notifyButton, LayoutHelper.createLinear(48, 48));
            this.notifyButton.setOnClickListener(new OnClickListener() {
                private Toast visibleToast;

                public void onClick(View view) {
                    int i;
                    String str;
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.silent = chatActivityEnterView.silent ^ 1;
                    ChatActivityEnterView.this.notifyButton.setImageResource(ChatActivityEnterView.this.silent ? NUM : NUM);
                    Editor edit = MessagesController.getNotificationsSettings(ChatActivityEnterView.this.currentAccount).edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("silent_");
                    stringBuilder.append(ChatActivityEnterView.this.dialog_id);
                    edit.putBoolean(stringBuilder.toString(), ChatActivityEnterView.this.silent).commit();
                    NotificationsController.getInstance(ChatActivityEnterView.this.currentAccount).updateServerNotificationsSettings(ChatActivityEnterView.this.dialog_id);
                    try {
                        if (this.visibleToast != null) {
                            this.visibleToast.cancel();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (ChatActivityEnterView.this.silent) {
                        this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOff", NUM), 0);
                        this.visibleToast.show();
                    } else {
                        this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOn", NUM), 0);
                        this.visibleToast.show();
                    }
                    ImageView access$5900 = ChatActivityEnterView.this.notifyButton;
                    if (ChatActivityEnterView.this.silent) {
                        i = NUM;
                        str = "AccDescrChanSilentOn";
                    } else {
                        i = NUM;
                        str = "AccDescrChanSilentOff";
                    }
                    access$5900.setContentDescription(LocaleController.getString(str, i));
                    ChatActivityEnterView.this.updateFieldHint();
                }
            });
            this.attachButton = new ImageView(activity2);
            this.attachButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.attachButton.setImageResource(NUM);
            this.attachButton.setScaleType(ScaleType.CENTER);
            this.attachLayout.addView(this.attachButton, LayoutHelper.createLinear(48, 48));
            this.attachButton.setOnClickListener(new -$$Lambda$ChatActivityEnterView$sA9S70ZL3Nng90HptN-7RpznwZo(this));
            this.attachButton.setContentDescription(LocaleController.getString("AccDescrAttachButton", NUM));
        }
        this.recordedAudioPanel = new FrameLayout(activity2);
        this.recordedAudioPanel.setVisibility(this.audioToSend == null ? 8 : 0);
        String str3 = "chat_messagePanelBackground";
        this.recordedAudioPanel.setBackgroundColor(Theme.getColor(str3));
        this.recordedAudioPanel.setFocusable(true);
        this.recordedAudioPanel.setFocusableInTouchMode(true);
        this.recordedAudioPanel.setClickable(true);
        frameLayout.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        this.recordDeleteImageView = new ImageView(activity2);
        this.recordDeleteImageView.setScaleType(ScaleType.CENTER);
        this.recordDeleteImageView.setImageResource(NUM);
        this.recordDeleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoiceDelete"), Mode.MULTIPLY));
        this.recordDeleteImageView.setContentDescription(LocaleController.getString("Delete", NUM));
        this.recordedAudioPanel.addView(this.recordDeleteImageView, LayoutHelper.createFrame(48, 48.0f));
        this.recordDeleteImageView.setOnClickListener(new -$$Lambda$ChatActivityEnterView$pQGz59T4TelJWdtUhc7Wjoj7V9g(this));
        this.videoTimelineView = new VideoTimelineView(activity2);
        this.videoTimelineView.setColor(-11817481);
        this.videoTimelineView.setRoundFrames(true);
        this.videoTimelineView.setDelegate(new VideoTimelineViewDelegate() {
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
        this.recordedAudioPlayButton.setScaleType(ScaleType.CENTER);
        this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
        this.recordedAudioPanel.addView(this.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0f, 83, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioPlayButton.setOnClickListener(new -$$Lambda$ChatActivityEnterView$0PqxYA4Sm4DXenEKSdETEwDwen0(this));
        this.recordedAudioTimeTextView = new TextView(activity2);
        this.recordedAudioTimeTextView.setTextColor(Theme.getColor("chat_messagePanelVoiceDuration"));
        this.recordedAudioTimeTextView.setTextSize(1, 13.0f);
        this.recordedAudioPanel.addView(this.recordedAudioTimeTextView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 13.0f, 0.0f));
        this.recordPanel = new FrameLayout(activity2);
        this.recordPanel.setVisibility(8);
        this.recordPanel.setBackgroundColor(Theme.getColor(str3));
        frameLayout.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48, 80));
        this.recordPanel.setOnTouchListener(-$$Lambda$ChatActivityEnterView$NTG78ehgN82Ciqu_59BpKprJO6c.INSTANCE);
        this.slideText = new LinearLayout(activity2);
        this.slideText.setOrientation(0);
        this.recordPanel.addView(this.slideText, LayoutHelper.createFrame(-2, -2.0f, 17, 30.0f, 0.0f, 0.0f, 0.0f));
        this.recordCancelImage = new ImageView(activity2);
        this.recordCancelImage.setImageResource(NUM);
        this.recordCancelImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_recordVoiceCancel"), Mode.MULTIPLY));
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
        this.recordSendText.setOnClickListener(new -$$Lambda$ChatActivityEnterView$yC9IrZCKz9RgoNa9k7GyjXp1EoM(this));
        this.recordPanel.addView(this.recordSendText, LayoutHelper.createFrame(-2, -1.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
        this.recordTimeContainer = new LinearLayout(activity2);
        this.recordTimeContainer.setOrientation(0);
        this.recordTimeContainer.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        this.recordTimeContainer.setBackgroundColor(Theme.getColor(str3));
        this.recordPanel.addView(this.recordTimeContainer, LayoutHelper.createFrame(-2, -2, 16));
        this.recordDot = new RecordDot(activity2);
        this.recordTimeContainer.addView(this.recordDot, LayoutHelper.createLinear(11, 11, 16, 0, 1, 0, 0));
        this.recordTimeText = new TextView(activity2);
        this.recordTimeText.setTextColor(Theme.getColor("chat_recordTime"));
        this.recordTimeText.setTextSize(1, 16.0f);
        this.recordTimeContainer.addView(this.recordTimeText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        this.sendButtonContainer = new FrameLayout(activity2);
        this.textFieldContainer.addView(this.sendButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        this.audioVideoButtonContainer = new FrameLayout(activity2);
        this.audioVideoButtonContainer.setBackgroundColor(Theme.getColor(str3));
        this.audioVideoButtonContainer.setSoundEffectsEnabled(false);
        this.sendButtonContainer.addView(this.audioVideoButtonContainer, LayoutHelper.createFrame(48, 48.0f));
        this.audioVideoButtonContainer.setOnTouchListener(new -$$Lambda$ChatActivityEnterView$ilTU_PI6Smic3T39rzhm2HGqiUo(this));
        this.audioSendButton = new ImageView(activity2);
        this.audioSendButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.audioSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        this.audioSendButton.setImageResource(NUM);
        this.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.audioSendButton.setContentDescription(LocaleController.getString("AccDescrVoiceMessage", NUM));
        this.audioSendButton.setFocusable(true);
        this.audioSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
        this.audioVideoButtonContainer.addView(this.audioSendButton, LayoutHelper.createFrame(48, 48.0f));
        if (z) {
            this.videoSendButton = new ImageView(activity2);
            this.videoSendButton.setScaleType(ScaleType.CENTER_INSIDE);
            this.videoSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
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
        this.cancelBotButton.setScaleType(ScaleType.CENTER_INSIDE);
        ImageView imageView2 = this.cancelBotButton;
        CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
        this.progressDrawable = closeProgressDrawable2;
        imageView2.setImageDrawable(closeProgressDrawable2);
        this.cancelBotButton.setContentDescription(LocaleController.getString("Cancel", NUM));
        this.progressDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelCancelInlineBot"), Mode.MULTIPLY));
        this.cancelBotButton.setSoundEffectsEnabled(false);
        this.cancelBotButton.setScaleX(0.1f);
        this.cancelBotButton.setScaleY(0.1f);
        this.cancelBotButton.setAlpha(0.0f);
        this.sendButtonContainer.addView(this.cancelBotButton, LayoutHelper.createFrame(48, 48.0f));
        this.cancelBotButton.setOnClickListener(new -$$Lambda$ChatActivityEnterView$5SWSfIIIolSi29LVurkXFLuveaY(this));
        this.sendButton = new ImageView(activity2);
        this.sendButton.setVisibility(4);
        this.sendButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.sendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelSend"), Mode.MULTIPLY));
        this.sendButton.setImageResource(NUM);
        this.sendButton.setContentDescription(LocaleController.getString("Send", NUM));
        this.sendButton.setSoundEffectsEnabled(false);
        this.sendButton.setScaleX(0.1f);
        this.sendButton.setScaleY(0.1f);
        this.sendButton.setAlpha(0.0f);
        this.sendButtonContainer.addView(this.sendButton, LayoutHelper.createFrame(48, 48.0f));
        this.sendButton.setOnClickListener(new -$$Lambda$ChatActivityEnterView$LT79Icaff_VcrkpB6XfMaR17p9I(this));
        this.expandStickersButton = new ImageView(activity2);
        this.expandStickersButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.expandStickersButton.setScaleType(ScaleType.CENTER);
        imageView2 = this.expandStickersButton;
        AnimatedArrowDrawable animatedArrowDrawable = new AnimatedArrowDrawable(Theme.getColor(str), false);
        this.stickersArrow = animatedArrowDrawable;
        imageView2.setImageDrawable(animatedArrowDrawable);
        this.expandStickersButton.setVisibility(8);
        this.expandStickersButton.setScaleX(0.1f);
        this.expandStickersButton.setScaleY(0.1f);
        this.expandStickersButton.setAlpha(0.0f);
        this.sendButtonContainer.addView(this.expandStickersButton, LayoutHelper.createFrame(48, 48.0f));
        this.expandStickersButton.setOnClickListener(new -$$Lambda$ChatActivityEnterView$BuL8MDh0vQ9QIU2SBmKOQL4e5dE(this));
        this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", NUM));
        this.doneButtonContainer = new FrameLayout(activity2);
        this.doneButtonContainer.setVisibility(8);
        this.textFieldContainer.addView(this.doneButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        this.doneButtonContainer.setOnClickListener(new -$$Lambda$ChatActivityEnterView$k84iF5Zh9bonq8mHpikPhsUxJO4(this));
        Drawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(16.0f), Theme.getColor("chat_messagePanelSend"));
        Drawable mutate = activity.getResources().getDrawable(NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
        CombinedDrawable combinedDrawable = new CombinedDrawable(createCircleDrawable, mutate, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable.setCustomSize(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        this.doneButtonImage = new ImageView(activity2);
        this.doneButtonImage.setScaleType(ScaleType.CENTER);
        this.doneButtonImage.setImageDrawable(combinedDrawable);
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
        if (isPopupShowing() && this.currentPopupContentType == 0) {
            if (this.searchingType != 0) {
                this.searchingType = 0;
                this.emojiView.closeSearch(false);
                this.messageEditText.requestFocus();
            }
            openKeyboardInternal();
            return;
        }
        boolean z = true;
        showPopup(1, 0);
        EmojiView emojiView = this.emojiView;
        if (this.messageEditText.length() <= 0) {
            z = false;
        }
        emojiView.onOpen(z);
    }

    public /* synthetic */ void lambda$new$1$ChatActivityEnterView() {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onTextSpansChanged(this.messageEditText.getText());
        }
    }

    public /* synthetic */ void lambda$new$2$ChatActivityEnterView(View view) {
        if (this.searchingType != 0) {
            this.searchingType = 0;
            this.emojiView.closeSearch(false);
            this.messageEditText.requestFocus();
        }
        if (this.botReplyMarkup != null) {
            Editor edit;
            StringBuilder stringBuilder;
            String str = "hidekeyboard_";
            if (isPopupShowing()) {
                int i = this.currentPopupContentType;
                if (i == 1) {
                    if (i == 1 && this.botButtonsMessageObject != null) {
                        edit = MessagesController.getMainSettings(this.currentAccount).edit();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str);
                        stringBuilder.append(this.dialog_id);
                        edit.putInt(stringBuilder.toString(), this.botButtonsMessageObject.getId()).commit();
                    }
                    openKeyboardInternal();
                }
            }
            showPopup(1, 1);
            edit = MessagesController.getMainSettings(this.currentAccount).edit();
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(this.dialog_id);
            edit.remove(stringBuilder.toString()).commit();
        } else if (this.hasBotCommands) {
            setFieldText("/");
            this.messageEditText.requestFocus();
            openKeyboard();
        }
        if (this.stickersExpanded) {
            setStickersExpanded(false, false, false);
        }
    }

    public /* synthetic */ void lambda$new$3$ChatActivityEnterView(View view) {
        this.delegate.didPressedAttachButton();
    }

    public /* synthetic */ void lambda$new$4$ChatActivityEnterView(View view) {
        if (this.videoToSendMessageObject != null) {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(2);
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

    public /* synthetic */ void lambda$new$5$ChatActivityEnterView(View view) {
        if (this.audioToSend != null) {
            if (!MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject) || MediaController.getInstance().isMessagePaused()) {
                this.recordedAudioPlayButton.setImageDrawable(this.pauseDrawable);
                MediaController.getInstance().playMessage(this.audioToSendMessageObject);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
            } else {
                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.audioToSendMessageObject);
                this.recordedAudioPlayButton.setImageDrawable(this.playDrawable);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            }
        }
    }

    public /* synthetic */ void lambda$new$7$ChatActivityEnterView(View view) {
        if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
            this.delegate.needStartRecordAudio(0);
            MediaController.getInstance().stopRecording(0);
        } else {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(2);
        }
        this.recordingAudioVideo = false;
        updateRecordIntefrace();
    }

    public /* synthetic */ boolean lambda$new$8$ChatActivityEnterView(View view, MotionEvent motionEvent) {
        boolean z = false;
        if (motionEvent.getAction() == 0) {
            if (this.recordCircle.isSendButtonVisible()) {
                if (!this.hasRecordVideo || this.calledRecordRunnable) {
                    this.startedDraggingX = -1.0f;
                    if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                        this.delegate.needStartRecordAudio(0);
                        MediaController.getInstance().stopRecording(1);
                    } else {
                        this.delegate.needStartRecordVideo(1);
                    }
                    this.recordingAudioVideo = false;
                    updateRecordIntefrace();
                }
                return false;
            }
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null) {
                Chat currentChat = chatActivity.getCurrentChat();
                if (!(currentChat == null || ChatObject.canSendMedia(currentChat))) {
                    this.delegate.needShowMediaBanHint();
                    return false;
                }
            }
            if (this.hasRecordVideo) {
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
                    this.delegate.needStartRecordAudio(0);
                    MediaController.getInstance().stopRecording(1);
                } else {
                    CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                    this.delegate.needStartRecordVideo(1);
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
                Animator[] animatorArr = new Animator[5];
                animatorArr[0] = ObjectAnimator.ofFloat(this.recordCircle, "lockAnimatedTranslation", new float[]{this.recordCircle.startTranslation});
                animatorArr[1] = ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f)});
                animatorArr[3] = ObjectAnimator.ofFloat(this.recordSendText, View.ALPHA, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.recordSendText, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(20.0f)), 0.0f});
                animatorSet.playTogether(animatorArr);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(150);
                animatorSet.start();
                return false;
            }
            if (x < (-this.distCanMove)) {
                if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                    this.delegate.needStartRecordAudio(0);
                    MediaController.getInstance().stopRecording(0);
                } else {
                    CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                    this.delegate.needStartRecordVideo(2);
                }
                this.recordingAudioVideo = false;
                updateRecordIntefrace();
            }
            x += this.audioVideoButtonContainer.getX();
            LayoutParams layoutParams = (LayoutParams) this.slideText.getLayoutParams();
            float f = this.startedDraggingX;
            if (f != -1.0f) {
                f = x - f;
                layoutParams.leftMargin = AndroidUtilities.dp(30.0f) + ((int) f);
                this.slideText.setLayoutParams(layoutParams);
                f = (f / this.distCanMove) + 1.0f;
                if (f > 1.0f) {
                    f = 1.0f;
                } else if (f < 0.0f) {
                    f = 0.0f;
                }
                this.slideText.setAlpha(f);
            }
            if (x <= (this.slideText.getX() + ((float) this.slideText.getWidth())) + ((float) AndroidUtilities.dp(30.0f)) && this.startedDraggingX == -1.0f) {
                this.startedDraggingX = x;
                this.distCanMove = ((float) ((this.recordPanel.getMeasuredWidth() - this.slideText.getMeasuredWidth()) - AndroidUtilities.dp(48.0f))) / 2.0f;
                x = this.distCanMove;
                if (x <= 0.0f) {
                    this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                } else if (x > ((float) AndroidUtilities.dp(80.0f))) {
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

    public /* synthetic */ void lambda$new$9$ChatActivityEnterView(View view) {
        String obj = this.messageEditText.getText().toString();
        int indexOf = obj.indexOf(32);
        if (indexOf == -1 || indexOf == obj.length() - 1) {
            setFieldText("");
        } else {
            setFieldText(obj.substring(0, indexOf + 1));
        }
    }

    public /* synthetic */ void lambda$new$10$ChatActivityEnterView(View view) {
        sendMessage();
    }

    public /* synthetic */ void lambda$new$11$ChatActivityEnterView(View view) {
        if (this.expandStickersButton.getVisibility() == 0 && this.expandStickersButton.getAlpha() == 1.0f) {
            if (this.stickersExpanded) {
                if (this.searchingType != 0) {
                    this.searchingType = 0;
                    this.emojiView.closeSearch(true);
                    this.emojiView.hideSearchKeyboard();
                    if (this.emojiTabOpen) {
                        checkSendButton(true);
                    }
                } else if (!this.stickersDragging) {
                    EmojiView emojiView = this.emojiView;
                    if (emojiView != null) {
                        emojiView.showSearchField(false);
                    }
                }
            } else if (!this.stickersDragging) {
                this.emojiView.showSearchField(true);
            }
            if (!this.stickersDragging) {
                setStickersExpanded(this.stickersExpanded ^ 1, true, false);
            }
        }
    }

    public /* synthetic */ void lambda$new$12$ChatActivityEnterView(View view) {
        doneEditingMessage();
    }

    /* Access modifiers changed, original: protected */
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        View view = this.topView;
        int translationY = (view == null || view.getVisibility() != 0) ? 0 : (int) this.topView.getTranslationY();
        int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + translationY;
        Theme.chat_composeShadowDrawable.setBounds(0, translationY, getMeasuredWidth(), intrinsicHeight);
        Theme.chat_composeShadowDrawable.draw(canvas);
        canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getWidth(), (float) getHeight(), Theme.chat_composeBackgroundPaint);
    }

    public boolean isSendButtonVisible() {
        return this.sendButton.getVisibility() == 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00e0  */
    private void setRecordVideoButtonVisible(boolean r12, boolean r13) {
        /*
        r11 = this;
        r0 = r11.videoSendButton;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r1 = 0;
        r2 = 1;
        if (r12 == 0) goto L_0x000e;
    L_0x0009:
        r3 = java.lang.Integer.valueOf(r2);
        goto L_0x000f;
    L_0x000e:
        r3 = r1;
    L_0x000f:
        r0.setTag(r3);
        r0 = r11.audioVideoButtonAnimation;
        if (r0 == 0) goto L_0x001b;
    L_0x0016:
        r0.cancel();
        r11.audioVideoButtonAnimation = r1;
    L_0x001b:
        r0 = 0;
        r1 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r13 == 0) goto L_0x010e;
    L_0x0023:
        r13 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r4 = r11.dialog_id;
        r5 = (int) r4;
        r4 = 0;
        if (r5 >= 0) goto L_0x004b;
    L_0x002d:
        r5 = r11.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r6 = r11.dialog_id;
        r7 = (int) r6;
        r6 = -r7;
        r6 = java.lang.Integer.valueOf(r6);
        r5 = r5.getChat(r6);
        r6 = org.telegram.messenger.ChatObject.isChannel(r5);
        if (r6 == 0) goto L_0x004b;
    L_0x0045:
        r5 = r5.megagroup;
        if (r5 != 0) goto L_0x004b;
    L_0x0049:
        r5 = 1;
        goto L_0x004c;
    L_0x004b:
        r5 = 0;
    L_0x004c:
        r13 = r13.edit();
        if (r5 == 0) goto L_0x0055;
    L_0x0052:
        r5 = "currentModeVideoChannel";
        goto L_0x0057;
    L_0x0055:
        r5 = "currentModeVideo";
    L_0x0057:
        r13 = r13.putBoolean(r5, r12);
        r13.commit();
        r13 = new android.animation.AnimatorSet;
        r13.<init>();
        r11.audioVideoButtonAnimation = r13;
        r13 = r11.audioVideoButtonAnimation;
        r5 = 6;
        r5 = new android.animation.Animator[r5];
        r6 = r11.videoSendButton;
        r7 = android.view.View.SCALE_X;
        r8 = new float[r2];
        if (r12 == 0) goto L_0x0075;
    L_0x0072:
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0078;
    L_0x0075:
        r9 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
    L_0x0078:
        r8[r4] = r9;
        r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8);
        r5[r4] = r6;
        r6 = r11.videoSendButton;
        r7 = android.view.View.SCALE_Y;
        r8 = new float[r2];
        if (r12 == 0) goto L_0x008b;
    L_0x0088:
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x008e;
    L_0x008b:
        r9 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
    L_0x008e:
        r8[r4] = r9;
        r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8);
        r5[r2] = r6;
        r6 = 2;
        r7 = r11.videoSendButton;
        r8 = android.view.View.ALPHA;
        r9 = new float[r2];
        if (r12 == 0) goto L_0x00a2;
    L_0x009f:
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x00a3;
    L_0x00a2:
        r10 = 0;
    L_0x00a3:
        r9[r4] = r10;
        r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9);
        r5[r6] = r7;
        r6 = 3;
        r7 = r11.audioSendButton;
        r8 = android.view.View.SCALE_X;
        r9 = new float[r2];
        if (r12 == 0) goto L_0x00b8;
    L_0x00b4:
        r10 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        goto L_0x00ba;
    L_0x00b8:
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x00ba:
        r9[r4] = r10;
        r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9);
        r5[r6] = r7;
        r6 = 4;
        r7 = r11.audioSendButton;
        r8 = android.view.View.SCALE_Y;
        r9 = new float[r2];
        if (r12 == 0) goto L_0x00cc;
    L_0x00cb:
        goto L_0x00ce;
    L_0x00cc:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x00ce:
        r9[r4] = r1;
        r1 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9);
        r5[r6] = r1;
        r1 = 5;
        r6 = r11.audioSendButton;
        r7 = android.view.View.ALPHA;
        r2 = new float[r2];
        if (r12 == 0) goto L_0x00e0;
    L_0x00df:
        goto L_0x00e2;
    L_0x00e0:
        r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x00e2:
        r2[r4] = r0;
        r12 = android.animation.ObjectAnimator.ofFloat(r6, r7, r2);
        r5[r1] = r12;
        r13.playTogether(r5);
        r12 = r11.audioVideoButtonAnimation;
        r13 = new org.telegram.ui.Components.ChatActivityEnterView$15;
        r13.<init>();
        r12.addListener(r13);
        r12 = r11.audioVideoButtonAnimation;
        r13 = new android.view.animation.DecelerateInterpolator;
        r13.<init>();
        r12.setInterpolator(r13);
        r12 = r11.audioVideoButtonAnimation;
        r0 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r12.setDuration(r0);
        r12 = r11.audioVideoButtonAnimation;
        r12.start();
        goto L_0x0154;
    L_0x010e:
        r13 = r11.videoSendButton;
        if (r12 == 0) goto L_0x0115;
    L_0x0112:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0118;
    L_0x0115:
        r2 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
    L_0x0118:
        r13.setScaleX(r2);
        r13 = r11.videoSendButton;
        if (r12 == 0) goto L_0x0122;
    L_0x011f:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0125;
    L_0x0122:
        r2 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
    L_0x0125:
        r13.setScaleY(r2);
        r13 = r11.videoSendButton;
        if (r12 == 0) goto L_0x012f;
    L_0x012c:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0130;
    L_0x012f:
        r2 = 0;
    L_0x0130:
        r13.setAlpha(r2);
        r13 = r11.audioSendButton;
        if (r12 == 0) goto L_0x013b;
    L_0x0137:
        r2 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        goto L_0x013d;
    L_0x013b:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x013d:
        r13.setScaleX(r2);
        r13 = r11.audioSendButton;
        if (r12 == 0) goto L_0x0145;
    L_0x0144:
        goto L_0x0147;
    L_0x0145:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0147:
        r13.setScaleY(r1);
        r13 = r11.audioSendButton;
        if (r12 == 0) goto L_0x014f;
    L_0x014e:
        goto L_0x0151;
    L_0x014f:
        r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0151:
        r13.setAlpha(r0);
    L_0x0154:
        return;
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
            MediaController.getInstance().stopRecording(0);
        } else {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(2);
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
        setEmojiButtonImage(false, this.isPaused ^ 1);
    }

    public void addEmojiToRecent(String str) {
        createEmojiView();
        this.emojiView.addEmojiToRecent(str);
    }

    public void setOpenGifsTabFirst() {
        createEmojiView();
        DataQuery.getInstance(this.currentAccount).loadRecents(0, true, true, false);
        this.emojiView.switchToGifRecent();
    }

    public void showTopView(boolean z, boolean z2) {
        if (this.topView == null || this.topViewShowed || getVisibility() != 0) {
            if (this.recordedAudioPanel.getVisibility() != 0 && (!this.forceShowSendButton || z2)) {
                openKeyboard();
            }
            return;
        }
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
                AnimatorSet animatorSet2 = this.currentTopViewAnimation;
                Animator[] animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(this.topView, View.TRANSLATION_Y, new float[]{0.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.topLineView, View.ALPHA, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
                this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator)) {
                            ChatActivityEnterView.this.currentTopViewAnimation = null;
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
            if (this.recordedAudioPanel.getVisibility() != 0 && (!this.forceShowSendButton || z2)) {
                this.messageEditText.requestFocus();
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
            AnimatorSet animatorSet2;
            Animator[] animatorArr;
            if (z) {
                this.doneButtonProgress.setVisibility(0);
                this.doneButtonContainer.setEnabled(false);
                animatorSet2 = this.doneButtonAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_X, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_Y, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneButtonImage, View.ALPHA, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_X, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_Y, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.doneButtonProgress, View.ALPHA, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            } else {
                this.doneButtonImage.setVisibility(0);
                this.doneButtonContainer.setEnabled(true);
                animatorSet2 = this.doneButtonAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_X, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_Y, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneButtonProgress, View.ALPHA, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_X, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_Y, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.doneButtonImage, View.ALPHA, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            }
            this.doneButtonAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animator)) {
                        if (z) {
                            ChatActivityEnterView.this.doneButtonImage.setVisibility(4);
                        } else {
                            ChatActivityEnterView.this.doneButtonProgress.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animator)) {
                        ChatActivityEnterView.this.doneButtonAnimation = null;
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
                    Animator[] animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.topView, View.TRANSLATION_Y, new float[]{(float) r3.getLayoutParams().height});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.topLineView, View.ALPHA, new float[]{0.0f});
                    animatorSet2.playTogether(animatorArr);
                    this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator)) {
                                ChatActivityEnterView.this.topView.setVisibility(8);
                                ChatActivityEnterView.this.topLineView.setVisibility(8);
                                ChatActivityEnterView.this.resizeForTopView(false);
                                ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator)) {
                                ChatActivityEnterView.this.currentTopViewAnimation = null;
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
                View view = this.topView;
                view.setTranslationY((float) view.getLayoutParams().height);
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

    private void resizeForTopView(boolean z) {
        LayoutParams layoutParams = (LayoutParams) this.textFieldContainer.getLayoutParams();
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.onDestroy();
        }
        WakeLock wakeLock = this.wakeLock;
        if (wakeLock != null) {
            try {
                wakeLock.release();
                this.wakeLock = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.setDelegate(null);
        }
    }

    public void checkChannelRights() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            Chat currentChat = chatActivity.getCurrentChat();
            if (currentChat != null) {
                this.audioVideoButtonContainer.setAlpha(ChatObject.canSendMedia(currentChat) ? 1.0f : 0.5f);
                EmojiView emojiView = this.emojiView;
                if (emojiView != null) {
                    emojiView.setStickersBanned(ChatObject.canSendStickers(currentChat) ^ 1, currentChat.id);
                }
            }
        }
    }

    public void onPause() {
        this.isPaused = true;
        closeKeyboard();
    }

    public void onResume() {
        this.isPaused = false;
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

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0139  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x017e  */
    /* JADX WARNING: Missing block: B:12:0x012a, code skipped:
            if (r4.post_messages != false) goto L_0x012c;
     */
    public void setDialogId(long r3, int r5) {
        /*
        r2 = this;
        r2.dialog_id = r3;
        r3 = r2.currentAccount;
        if (r3 == r5) goto L_0x00e2;
    L_0x0006:
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.recordStarted;
        r3.removeObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.recordStartError;
        r3.removeObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.recordStopped;
        r3.removeObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.recordProgressChanged;
        r3.removeObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.closeChats;
        r3.removeObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.audioDidSent;
        r3.removeObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.audioRouteChanged;
        r3.removeObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset;
        r3.removeObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r3.removeObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.featuredStickersDidLoad;
        r3.removeObserver(r2, r4);
        r2.currentAccount = r5;
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.recordStarted;
        r3.addObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.recordStartError;
        r3.addObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.recordStopped;
        r3.addObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.recordProgressChanged;
        r3.addObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.closeChats;
        r3.addObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.audioDidSent;
        r3.addObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.audioRouteChanged;
        r3.addObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset;
        r3.addObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r3.addObserver(r2, r4);
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.featuredStickersDidLoad;
        r3.addObserver(r2, r4);
    L_0x00e2:
        r3 = r2.dialog_id;
        r4 = (int) r3;
        if (r4 >= 0) goto L_0x0189;
    L_0x00e7:
        r3 = r2.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r4 = r2.dialog_id;
        r5 = (int) r4;
        r4 = -r5;
        r4 = java.lang.Integer.valueOf(r4);
        r3 = r3.getChat(r4);
        r4 = r2.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = "silent_";
        r5.append(r0);
        r0 = r2.dialog_id;
        r5.append(r0);
        r5 = r5.toString();
        r0 = 0;
        r4 = r4.getBoolean(r5, r0);
        r2.silent = r4;
        r4 = org.telegram.messenger.ChatObject.isChannel(r3);
        r5 = 1;
        if (r4 == 0) goto L_0x0132;
    L_0x0120:
        r4 = r3.creator;
        if (r4 != 0) goto L_0x012c;
    L_0x0124:
        r4 = r3.admin_rights;
        if (r4 == 0) goto L_0x0132;
    L_0x0128:
        r4 = r4.post_messages;
        if (r4 == 0) goto L_0x0132;
    L_0x012c:
        r3 = r3.megagroup;
        if (r3 != 0) goto L_0x0132;
    L_0x0130:
        r3 = 1;
        goto L_0x0133;
    L_0x0132:
        r3 = 0;
    L_0x0133:
        r2.canWriteToChannel = r3;
        r3 = r2.notifyButton;
        if (r3 == 0) goto L_0x017a;
    L_0x0139:
        r4 = r2.canWriteToChannel;
        r1 = 8;
        if (r4 == 0) goto L_0x0141;
    L_0x013f:
        r4 = 0;
        goto L_0x0143;
    L_0x0141:
        r4 = 8;
    L_0x0143:
        r3.setVisibility(r4);
        r3 = r2.notifyButton;
        r4 = r2.silent;
        if (r4 == 0) goto L_0x0150;
    L_0x014c:
        r4 = NUM; // 0x7var_ float:1.7945227E38 double:1.052935661E-314;
        goto L_0x0153;
    L_0x0150:
        r4 = NUM; // 0x7var_ float:1.794523E38 double:1.0529356616E-314;
    L_0x0153:
        r3.setImageResource(r4);
        r3 = r2.attachLayout;
        r4 = r2.botButton;
        if (r4 == 0) goto L_0x0162;
    L_0x015c:
        r4 = r4.getVisibility();
        if (r4 != r1) goto L_0x016d;
    L_0x0162:
        r4 = r2.notifyButton;
        if (r4 == 0) goto L_0x0170;
    L_0x0166:
        r4 = r4.getVisibility();
        if (r4 != r1) goto L_0x016d;
    L_0x016c:
        goto L_0x0170;
    L_0x016d:
        r4 = NUM; // 0x42CLASSNAME float:96.0 double:5.532938244E-315;
        goto L_0x0172;
    L_0x0170:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
    L_0x0172:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r3.setPivotX(r4);
    L_0x017a:
        r3 = r2.attachLayout;
        if (r3 == 0) goto L_0x0189;
    L_0x017e:
        r3 = r3.getVisibility();
        if (r3 != 0) goto L_0x0185;
    L_0x0184:
        goto L_0x0186;
    L_0x0185:
        r5 = 0;
    L_0x0186:
        r2.updateFieldRight(r5);
    L_0x0189:
        r2.checkRoundVideo();
        r2.updateFieldHint();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setDialogId(long, int):void");
    }

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.setChatInfo(this.info);
        }
    }

    public void checkRoundVideo() {
        if (!this.hasRecordVideo) {
            if (this.attachLayout == null || VERSION.SDK_INT < 18) {
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
            } else if (AndroidUtilities.getPeerLayerVersion(MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i2)).layer) >= 66) {
                this.hasRecordVideo = true;
            }
            if (((int) this.dialog_id) < 0) {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    z = false;
                }
                if (z && !chat.creator) {
                    TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
                    if (tL_chatAdminRights == null || !tL_chatAdminRights.post_messages) {
                        this.hasRecordVideo = false;
                    }
                }
            } else {
                z = false;
            }
            if (!SharedConfig.inappCamera) {
                this.hasRecordVideo = false;
            }
            if (this.hasRecordVideo) {
                if (SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera(null);
                }
                setRecordVideoButtonVisible(MessagesController.getGlobalMainSettings().getBoolean(z ? "currentModeVideoChannel" : "currentModeVideo", z), false);
            } else {
                setRecordVideoButtonVisible(false, false);
            }
        }
    }

    public boolean isInVideoMode() {
        return this.videoSendButton.getTag() != null;
    }

    public boolean hasRecordVideo() {
        return this.hasRecordVideo;
    }

    private void updateFieldHint() {
        Object obj = null;
        if (((int) this.dialog_id) < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                obj = 1;
            }
        }
        String str = "TypeMessage";
        if (this.editingMessageObject != null) {
            this.messageEditText.setHintText(this.editingCaption ? LocaleController.getString("Caption", NUM) : LocaleController.getString(str, NUM));
        } else if (obj == null) {
            this.messageEditText.setHintText(LocaleController.getString(str, NUM));
        } else if (this.silent) {
            this.messageEditText.setHintText(LocaleController.getString("ChannelSilentBroadcast", NUM));
        } else {
            this.messageEditText.setHintText(LocaleController.getString("ChannelBroadcast", NUM));
        }
    }

    public void setReplyingMessageObject(MessageObject messageObject) {
        if (messageObject != null) {
            if (this.botMessageObject == null) {
                MessageObject messageObject2 = this.botButtonsMessageObject;
                if (messageObject2 != this.replyingMessageObject) {
                    this.botMessageObject = messageObject2;
                }
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

    public void setWebPage(WebPage webPage, boolean z) {
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
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
            }
        });
        animatorSet.start();
    }

    private void sendMessage() {
        if (this.stickersExpanded) {
            setStickersExpanded(false, true, false);
            if (this.searchingType != 0) {
                this.emojiView.closeSearch(false);
                this.emojiView.hideSearchKeyboard();
            }
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        if (this.videoToSendMessageObject != null) {
            this.delegate.needStartRecordVideo(4);
            hideRecordedAudioPanel();
            checkSendButton(true);
        } else if (this.audioToSend != null) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null && playingMessageObject == this.audioToSendMessageObject) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.audioToSend, null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, null, null, null, null, 0, null);
            chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onMessageSend(null);
            }
            hideRecordedAudioPanel();
            checkSendButton(true);
        } else {
            Editable text = this.messageEditText.getText();
            if (processSendingText(text)) {
                this.messageEditText.setText("");
                this.lastTypingTimeSend = 0;
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                if (chatActivityEnterViewDelegate2 != null) {
                    chatActivityEnterViewDelegate2.onMessageSend(text);
                }
            } else if (this.forceShowSendButton) {
                chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    chatActivityEnterViewDelegate.onMessageSend(null);
                }
            }
        }
    }

    public void doneEditingMessage() {
        if (this.editingMessageObject != null) {
            this.delegate.onMessageEditEnd(true);
            showEditDoneProgress(true, true);
            CharSequence[] charSequenceArr = new CharSequence[]{this.messageEditText.getText()};
            this.editingMessageReqId = SendMessagesHelper.getInstance(this.currentAccount).editMessage(this.editingMessageObject, charSequenceArr[0].toString(), this.messageWebPageSearch, this.parentFragment, DataQuery.getInstance(this.currentAccount).getEntities(charSequenceArr), new -$$Lambda$ChatActivityEnterView$ORXTEQMKGHUriZg_5Y7AosAFGHI(this));
        }
    }

    public /* synthetic */ void lambda$doneEditingMessage$13$ChatActivityEnterView() {
        this.editingMessageReqId = 0;
        setEditingMessageObject(null, false);
    }

    public boolean processSendingText(CharSequence charSequence) {
        CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence);
        int i = MessagesController.getInstance(this.currentAccount).maxMessageLength;
        if (trimmedString.length() == 0) {
            return false;
        }
        int ceil = (int) Math.ceil((double) (((float) trimmedString.length()) / ((float) i)));
        int i2 = 0;
        while (i2 < ceil) {
            CharSequence[] charSequenceArr = new CharSequence[1];
            int i3 = i2 * i;
            i2++;
            charSequenceArr[0] = trimmedString.subSequence(i3, Math.min(i2 * i, trimmedString.length()));
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(charSequenceArr[0].toString(), this.dialog_id, this.replyingMessageObject, this.messageWebPage, this.messageWebPageSearch, DataQuery.getInstance(this.currentAccount).getEntities(charSequenceArr), null, null);
        }
        return true;
    }

    private void checkSendButton(boolean z) {
        if (this.editingMessageObject == null) {
            boolean z2 = this.isPaused ? false : z;
            LinearLayout linearLayout;
            AnimatorSet animatorSet;
            ArrayList arrayList;
            if (AndroidUtilities.getTrimmedString(this.messageEditText.getText()).length() > 0 || this.forceShowSendButton || this.audioToSend != null || this.videoToSendMessageObject != null) {
                final String caption = this.messageEditText.getCaption();
                Object obj = (caption == null || !(this.sendButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0)) ? null : 1;
                Object obj2 = (caption == null && (this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0)) ? 1 : null;
                if (!(this.audioVideoButtonContainer.getVisibility() != 0 && obj == null && obj2 == null)) {
                    if (!z2) {
                        int i;
                        this.audioVideoButtonContainer.setScaleX(0.1f);
                        this.audioVideoButtonContainer.setScaleY(0.1f);
                        this.audioVideoButtonContainer.setAlpha(0.0f);
                        if (caption != null) {
                            this.sendButton.setScaleX(0.1f);
                            this.sendButton.setScaleY(0.1f);
                            this.sendButton.setAlpha(0.0f);
                            this.cancelBotButton.setScaleX(1.0f);
                            this.cancelBotButton.setScaleY(1.0f);
                            this.cancelBotButton.setAlpha(1.0f);
                            this.cancelBotButton.setVisibility(0);
                            i = 8;
                            this.sendButton.setVisibility(8);
                        } else {
                            this.cancelBotButton.setScaleX(0.1f);
                            this.cancelBotButton.setScaleY(0.1f);
                            this.cancelBotButton.setAlpha(0.0f);
                            this.sendButton.setScaleX(1.0f);
                            this.sendButton.setScaleY(1.0f);
                            this.sendButton.setAlpha(1.0f);
                            this.sendButton.setVisibility(0);
                            i = 8;
                            this.cancelBotButton.setVisibility(8);
                        }
                        this.audioVideoButtonContainer.setVisibility(i);
                        linearLayout = this.attachLayout;
                        if (linearLayout != null) {
                            linearLayout.setVisibility(i);
                            if (this.delegate != null && getVisibility() == 0) {
                                this.delegate.onAttachButtonHidden();
                            }
                            updateFieldRight(0);
                        }
                    } else if (!(this.runningAnimationType == 1 && this.messageEditText.getCaption() == null) && (this.runningAnimationType != 3 || caption == null)) {
                        animatorSet = this.runningAnimation;
                        if (animatorSet != null) {
                            animatorSet.cancel();
                            this.runningAnimation = null;
                        }
                        animatorSet = this.runningAnimation2;
                        if (animatorSet != null) {
                            animatorSet.cancel();
                            this.runningAnimation2 = null;
                        }
                        if (this.attachLayout != null) {
                            this.runningAnimation2 = new AnimatorSet();
                            animatorSet = this.runningAnimation2;
                            Animator[] animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                            this.runningAnimation2.setDuration(100);
                            this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (ChatActivityEnterView.this.runningAnimation2 != null && ChatActivityEnterView.this.runningAnimation2.equals(animator)) {
                                        ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                    }
                                }

                                public void onAnimationCancel(Animator animator) {
                                    if (ChatActivityEnterView.this.runningAnimation2 != null && ChatActivityEnterView.this.runningAnimation2.equals(animator)) {
                                        ChatActivityEnterView.this.runningAnimation2 = null;
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
                        arrayList = new ArrayList();
                        if (this.audioVideoButtonContainer.getVisibility() == 0) {
                            arrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                        }
                        if (this.expandStickersButton.getVisibility() == 0) {
                            arrayList.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                        }
                        if (obj != null) {
                            arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                        } else if (obj2 != null) {
                            arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                        }
                        if (caption != null) {
                            this.runningAnimationType = 3;
                            arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{1.0f}));
                            this.cancelBotButton.setVisibility(0);
                        } else {
                            this.runningAnimationType = 1;
                            arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{1.0f}));
                            this.sendButton.setVisibility(0);
                        }
                        this.runningAnimation.playTogether(arrayList);
                        this.runningAnimation.setDuration(150);
                        this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator)) {
                                    if (caption != null) {
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(0);
                                        ChatActivityEnterView.this.sendButton.setVisibility(8);
                                    } else {
                                        ChatActivityEnterView.this.sendButton.setVisibility(0);
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                    }
                                    ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                    ChatActivityEnterView.this.expandStickersButton.setVisibility(8);
                                    ChatActivityEnterView.this.runningAnimation = null;
                                    ChatActivityEnterView.this.runningAnimationType = 0;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator)) {
                                    ChatActivityEnterView.this.runningAnimation = null;
                                }
                            }
                        });
                        this.runningAnimation.start();
                    }
                }
            } else if (this.emojiView == null || !this.emojiViewVisible || (!(this.stickersTabOpen || (this.emojiTabOpen && this.searchingType == 2)) || AndroidUtilities.isInMultiwindow)) {
                if (this.sendButton.getVisibility() == 0 || this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0) {
                    if (!z2) {
                        this.sendButton.setScaleX(0.1f);
                        this.sendButton.setScaleY(0.1f);
                        this.sendButton.setAlpha(0.0f);
                        this.cancelBotButton.setScaleX(0.1f);
                        this.cancelBotButton.setScaleY(0.1f);
                        this.cancelBotButton.setAlpha(0.0f);
                        this.expandStickersButton.setScaleX(0.1f);
                        this.expandStickersButton.setScaleY(0.1f);
                        this.expandStickersButton.setAlpha(0.0f);
                        this.audioVideoButtonContainer.setScaleX(1.0f);
                        this.audioVideoButtonContainer.setScaleY(1.0f);
                        this.audioVideoButtonContainer.setAlpha(1.0f);
                        this.cancelBotButton.setVisibility(8);
                        this.sendButton.setVisibility(8);
                        this.expandStickersButton.setVisibility(8);
                        this.audioVideoButtonContainer.setVisibility(0);
                        if (this.attachLayout != null) {
                            if (getVisibility() == 0) {
                                this.delegate.onAttachButtonShow();
                            }
                            this.attachLayout.setVisibility(0);
                            updateFieldRight(1);
                        }
                    } else if (this.runningAnimationType != 2) {
                        animatorSet = this.runningAnimation;
                        if (animatorSet != null) {
                            animatorSet.cancel();
                            this.runningAnimation = null;
                        }
                        animatorSet = this.runningAnimation2;
                        if (animatorSet != null) {
                            animatorSet.cancel();
                            this.runningAnimation2 = null;
                        }
                        linearLayout = this.attachLayout;
                        if (linearLayout != null) {
                            linearLayout.setVisibility(0);
                            this.runningAnimation2 = new AnimatorSet();
                            animatorSet = this.runningAnimation2;
                            Animator[] animatorArr2 = new Animator[2];
                            animatorArr2[0] = ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f});
                            animatorArr2[1] = ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{1.0f});
                            animatorSet.playTogether(animatorArr2);
                            this.runningAnimation2.setDuration(100);
                            this.runningAnimation2.start();
                            updateFieldRight(1);
                            if (getVisibility() == 0) {
                                this.delegate.onAttachButtonShow();
                            }
                        }
                        this.audioVideoButtonContainer.setVisibility(0);
                        this.runningAnimation = new AnimatorSet();
                        this.runningAnimationType = 2;
                        arrayList = new ArrayList();
                        arrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{1.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{1.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f}));
                        if (this.cancelBotButton.getVisibility() == 0) {
                            arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                        } else if (this.expandStickersButton.getVisibility() == 0) {
                            arrayList.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                        } else {
                            arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                        }
                        this.runningAnimation.playTogether(arrayList);
                        this.runningAnimation.setDuration(150);
                        this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator)) {
                                    ChatActivityEnterView.this.sendButton.setVisibility(8);
                                    ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                    ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
                                    ChatActivityEnterView.this.runningAnimation = null;
                                    ChatActivityEnterView.this.runningAnimationType = 0;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator)) {
                                    ChatActivityEnterView.this.runningAnimation = null;
                                }
                            }
                        });
                        this.runningAnimation.start();
                    }
                }
            } else if (!z2) {
                this.sendButton.setScaleX(0.1f);
                this.sendButton.setScaleY(0.1f);
                this.sendButton.setAlpha(0.0f);
                this.cancelBotButton.setScaleX(0.1f);
                this.cancelBotButton.setScaleY(0.1f);
                this.cancelBotButton.setAlpha(0.0f);
                this.audioVideoButtonContainer.setScaleX(0.1f);
                this.audioVideoButtonContainer.setScaleY(0.1f);
                this.audioVideoButtonContainer.setAlpha(0.0f);
                this.expandStickersButton.setScaleX(1.0f);
                this.expandStickersButton.setScaleY(1.0f);
                this.expandStickersButton.setAlpha(1.0f);
                this.cancelBotButton.setVisibility(8);
                this.sendButton.setVisibility(8);
                this.audioVideoButtonContainer.setVisibility(8);
                this.expandStickersButton.setVisibility(0);
                if (this.attachLayout != null) {
                    if (getVisibility() == 0) {
                        this.delegate.onAttachButtonShow();
                    }
                    this.attachLayout.setVisibility(0);
                    updateFieldRight(1);
                }
            } else if (this.runningAnimationType != 4) {
                animatorSet = this.runningAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.runningAnimation = null;
                }
                animatorSet = this.runningAnimation2;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.runningAnimation2 = null;
                }
                linearLayout = this.attachLayout;
                if (linearLayout != null) {
                    linearLayout.setVisibility(0);
                    this.runningAnimation2 = new AnimatorSet();
                    animatorSet = this.runningAnimation2;
                    Animator[] animatorArr3 = new Animator[2];
                    animatorArr3[0] = ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f});
                    animatorArr3[1] = ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{1.0f});
                    animatorSet.playTogether(animatorArr3);
                    this.runningAnimation2.setDuration(100);
                    this.runningAnimation2.start();
                    updateFieldRight(1);
                    if (getVisibility() == 0) {
                        this.delegate.onAttachButtonShow();
                    }
                }
                this.expandStickersButton.setVisibility(0);
                this.runningAnimation = new AnimatorSet();
                this.runningAnimationType = 4;
                arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{1.0f}));
                if (this.cancelBotButton.getVisibility() == 0) {
                    arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                } else if (this.audioVideoButtonContainer.getVisibility() == 0) {
                    arrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                } else {
                    arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                }
                this.runningAnimation.playTogether(arrayList);
                this.runningAnimation.setDuration(150);
                this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator)) {
                            ChatActivityEnterView.this.sendButton.setVisibility(8);
                            ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                            ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                            ChatActivityEnterView.this.expandStickersButton.setVisibility(0);
                            ChatActivityEnterView.this.runningAnimation = null;
                            ChatActivityEnterView.this.runningAnimationType = 0;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator)) {
                            ChatActivityEnterView.this.runningAnimation = null;
                        }
                    }
                });
                this.runningAnimation.start();
            }
        }
    }

    private void updateFieldRight(int i) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null && this.editingMessageObject == null) {
            LayoutParams layoutParams = (LayoutParams) editTextCaption.getLayoutParams();
            ImageView imageView;
            if (i == 1) {
                imageView = this.botButton;
                if (imageView == null || imageView.getVisibility() != 0) {
                    imageView = this.notifyButton;
                    if (imageView == null || imageView.getVisibility() != 0) {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    }
                }
                layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
            } else if (i != 2) {
                layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
            } else if (layoutParams.rightMargin != AndroidUtilities.dp(2.0f)) {
                imageView = this.botButton;
                if (imageView == null || imageView.getVisibility() != 0) {
                    imageView = this.notifyButton;
                    if (imageView == null || imageView.getVisibility() != 0) {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    }
                }
                layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
            }
            this.messageEditText.setLayoutParams(layoutParams);
        }
    }

    private void updateRecordIntefrace() {
        boolean z = this.recordingAudioVideo;
        Integer valueOf = Integer.valueOf(0);
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (!z) {
            WakeLock wakeLock = this.wakeLock;
            if (wakeLock != null) {
                try {
                    wakeLock.release();
                    this.wakeLock = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            AndroidUtilities.unlockOrientation(this.parentActivity);
            if (this.recordInterfaceState != 0) {
                this.recordInterfaceState = 0;
                animatorSet = this.runningAnimationAudio;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                this.runningAnimationAudio = new AnimatorSet();
                animatorSet = this.runningAnimationAudio;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.recordPanel, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.displaySize.x});
                animatorArr[1] = ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
                this.runningAnimationAudio.setDuration(300);
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivityEnterView.this.runningAnimationAudio != null && ChatActivityEnterView.this.runningAnimationAudio.equals(animator)) {
                            LayoutParams layoutParams = (LayoutParams) ChatActivityEnterView.this.slideText.getLayoutParams();
                            layoutParams.leftMargin = AndroidUtilities.dp(30.0f);
                            ChatActivityEnterView.this.slideText.setLayoutParams(layoutParams);
                            ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordPanel.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setSendButtonInvisible();
                            ChatActivityEnterView.this.runningAnimationAudio = null;
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
                FileLog.e(e2);
            }
            AndroidUtilities.lockOrientation(this.parentActivity);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.needStartRecordAudio(0);
            }
            this.recordPanel.setVisibility(0);
            this.recordCircle.setVisibility(0);
            this.recordCircle.setAmplitude(0.0d);
            this.recordTimeText.setText(String.format("%02d:%02d.%02d", new Object[]{valueOf, valueOf, valueOf}));
            this.recordDot.resetAlpha();
            this.lastTimeString = null;
            this.lastTypingSendTime = -1;
            LayoutParams layoutParams = (LayoutParams) this.slideText.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(30.0f);
            this.slideText.setLayoutParams(layoutParams);
            this.slideText.setAlpha(1.0f);
            this.recordPanel.setX((float) AndroidUtilities.displaySize.x);
            animatorSet = this.runningAnimationAudio;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.runningAnimationAudio = new AnimatorSet();
            animatorSet = this.runningAnimationAudio;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.recordPanel, View.TRANSLATION_X, new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{1.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.runningAnimationAudio.setDuration(300);
            this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatActivityEnterView.this.runningAnimationAudio != null && ChatActivityEnterView.this.runningAnimationAudio.equals(animator)) {
                        ChatActivityEnterView.this.recordPanel.setX(0.0f);
                        ChatActivityEnterView.this.runningAnimationAudio = null;
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
        MessageObject messageObject2 = messageObject;
        String str2 = str;
        if (str2 != null && getVisibility() == 0) {
            String str3 = "%s@%s";
            String str4 = "@";
            User user = null;
            if (z) {
                CharSequence stringBuilder;
                String obj = this.messageEditText.getText().toString();
                if (messageObject2 != null && ((int) this.dialog_id) < 0) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                }
                String str5 = "";
                String str6 = "^/[a-zA-Z@\\d_]{1,255}(\\s|$)";
                String str7 = " ";
                StringBuilder stringBuilder2;
                if ((this.botCount != 1 || z2) && user != null && user.bot && !str2.contains(str4)) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(String.format(Locale.US, str3, new Object[]{str2, user.username}));
                    stringBuilder2.append(str7);
                    stringBuilder2.append(obj.replaceFirst(str6, str5));
                    stringBuilder = stringBuilder2.toString();
                } else {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str2);
                    stringBuilder2.append(str7);
                    stringBuilder2.append(obj.replaceFirst(str6, str5));
                    stringBuilder = stringBuilder2.toString();
                }
                this.ignoreTextChange = true;
                this.messageEditText.setText(stringBuilder);
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setSelection(editTextCaption.getText().length());
                this.ignoreTextChange = false;
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    chatActivityEnterViewDelegate.onTextChanged(this.messageEditText.getText(), true);
                }
                if (!this.keyboardVisible && this.currentPopupContentType == -1) {
                    openKeyboard();
                    return;
                }
                return;
            }
            if (messageObject2 != null && ((int) this.dialog_id) < 0) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
            }
            if ((this.botCount != 1 || z2) && user != null && user.bot && !str2.contains(str4)) {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(String.format(Locale.US, str3, new Object[]{str2, user.username}), this.dialog_id, this.replyingMessageObject, null, false, null, null, null);
                return;
            }
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(str, this.dialog_id, this.replyingMessageObject, null, false, null, null, null);
        }
    }

    public void setEditingMessageObject(MessageObject messageObject, boolean z) {
        MessageObject messageObject2 = messageObject;
        boolean z2 = z;
        String str = "```";
        String str2 = "`";
        if (this.audioToSend == null && this.videoToSendMessageObject == null && this.editingMessageObject != messageObject2) {
            if (this.editingMessageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.editingMessageReqId, true);
                this.editingMessageReqId = 0;
            }
            this.editingMessageObject = messageObject2;
            this.editingCaption = z2;
            String str3 = "";
            if (this.editingMessageObject != null) {
                CharSequence charSequence;
                AnimatorSet animatorSet = this.doneButtonAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.doneButtonAnimation = null;
                }
                this.doneButtonContainer.setVisibility(0);
                showEditDoneProgress(true, false);
                InputFilter[] inputFilterArr = new InputFilter[1];
                if (z2) {
                    inputFilterArr[0] = new LengthFilter(MessagesController.getInstance(this.currentAccount).maxCaptionLength);
                    charSequence = this.editingMessageObject.caption;
                } else {
                    inputFilterArr[0] = new LengthFilter(MessagesController.getInstance(this.currentAccount).maxMessageLength);
                    charSequence = this.editingMessageObject.messageText;
                }
                if (charSequence != null) {
                    int i;
                    ArrayList arrayList = this.editingMessageObject.messageOwner.entities;
                    DataQuery.sortEntities(arrayList);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
                    Object[] spans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), Object.class);
                    if (spans != null && spans.length > 0) {
                        for (Object removeSpan : spans) {
                            spannableStringBuilder.removeSpan(removeSpan);
                        }
                    }
                    if (arrayList != null) {
                        int i2 = 0;
                        i = 0;
                        while (i2 < arrayList.size()) {
                            try {
                                MessageEntity messageEntity = (MessageEntity) arrayList.get(i2);
                                if ((messageEntity.offset + messageEntity.length) + i <= spannableStringBuilder.length()) {
                                    StringBuilder stringBuilder;
                                    if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                                        if ((messageEntity.offset + messageEntity.length) + i < spannableStringBuilder.length() && spannableStringBuilder.charAt((messageEntity.offset + messageEntity.length) + i) == ' ') {
                                            messageEntity.length++;
                                        }
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(str3);
                                        stringBuilder.append(((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id);
                                        spannableStringBuilder.setSpan(new URLSpanUserMention(stringBuilder.toString(), 1), messageEntity.offset + i, (messageEntity.offset + messageEntity.length) + i, 33);
                                    } else if (messageEntity instanceof TL_messageEntityMentionName) {
                                        if ((messageEntity.offset + messageEntity.length) + i < spannableStringBuilder.length() && spannableStringBuilder.charAt((messageEntity.offset + messageEntity.length) + i) == ' ') {
                                            messageEntity.length++;
                                        }
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(str3);
                                        stringBuilder.append(((TL_messageEntityMentionName) messageEntity).user_id);
                                        spannableStringBuilder.setSpan(new URLSpanUserMention(stringBuilder.toString(), 1), messageEntity.offset + i, (messageEntity.offset + messageEntity.length) + i, 33);
                                    } else if (messageEntity instanceof TL_messageEntityCode) {
                                        spannableStringBuilder.insert((messageEntity.offset + messageEntity.length) + i, str2);
                                        spannableStringBuilder.insert(messageEntity.offset + i, str2);
                                        i += 2;
                                    } else if (messageEntity instanceof TL_messageEntityPre) {
                                        spannableStringBuilder.insert((messageEntity.offset + messageEntity.length) + i, str);
                                        spannableStringBuilder.insert(messageEntity.offset + i, str);
                                        i += 6;
                                    } else if (messageEntity instanceof TL_messageEntityBold) {
                                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), messageEntity.offset + i, (messageEntity.offset + messageEntity.length) + i, 33);
                                    } else if (messageEntity instanceof TL_messageEntityItalic) {
                                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), messageEntity.offset + i, (messageEntity.offset + messageEntity.length) + i, 33);
                                    } else if (messageEntity instanceof TL_messageEntityTextUrl) {
                                        spannableStringBuilder.setSpan(new URLSpanReplacement(messageEntity.url), messageEntity.offset + i, (messageEntity.offset + messageEntity.length) + i, 33);
                                    }
                                }
                                i2++;
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    }
                    setFieldText(Emoji.replaceEmoji(new SpannableStringBuilder(spannableStringBuilder), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
                } else {
                    setFieldText(str3);
                }
                this.messageEditText.setFilters(inputFilterArr);
                openKeyboard();
                LayoutParams layoutParams = (LayoutParams) this.messageEditText.getLayoutParams();
                layoutParams.rightMargin = AndroidUtilities.dp(4.0f);
                this.messageEditText.setLayoutParams(layoutParams);
                this.sendButton.setVisibility(8);
                this.cancelBotButton.setVisibility(8);
                this.audioVideoButtonContainer.setVisibility(8);
                this.attachLayout.setVisibility(8);
                this.sendButtonContainer.setVisibility(8);
            } else {
                this.doneButtonContainer.setVisibility(8);
                this.messageEditText.setFilters(new InputFilter[0]);
                this.delegate.onMessageEditEnd(false);
                this.audioVideoButtonContainer.setVisibility(0);
                this.attachLayout.setVisibility(0);
                this.sendButtonContainer.setVisibility(0);
                this.attachLayout.setScaleX(1.0f);
                this.attachLayout.setAlpha(1.0f);
                this.sendButton.setScaleX(0.1f);
                this.sendButton.setScaleY(0.1f);
                this.sendButton.setAlpha(0.0f);
                this.cancelBotButton.setScaleX(0.1f);
                this.cancelBotButton.setScaleY(0.1f);
                this.cancelBotButton.setAlpha(0.0f);
                this.audioVideoButtonContainer.setScaleX(1.0f);
                this.audioVideoButtonContainer.setScaleY(1.0f);
                this.audioVideoButtonContainer.setAlpha(1.0f);
                this.sendButton.setVisibility(8);
                this.cancelBotButton.setVisibility(8);
                this.messageEditText.setText(str3);
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

    public EmojiView getEmojiView() {
        return this.emojiView;
    }

    public void setFieldText(CharSequence charSequence) {
        setFieldText(charSequence, true);
    }

    public void setFieldText(CharSequence charSequence, boolean z) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            this.ignoreTextChange = z;
            editTextCaption.setText(charSequence);
            EditTextCaption editTextCaption2 = this.messageEditText;
            editTextCaption2.setSelection(editTextCaption2.getText().length());
            this.ignoreTextChange = false;
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
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
        int i = 0;
        if (editTextCaption == null) {
            return 0;
        }
        try {
            int selectionEnd = editTextCaption.getSelectionEnd();
            i = this.messageEditText.getSelectionStart();
            return selectionEnd - i;
        } catch (Exception e) {
            FileLog.e(e);
            return i;
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
            FileLog.e(e);
        }
    }

    public void setFieldFocused() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText != null && !accessibilityManager.isTouchExplorationEnabled()) {
            try {
                this.messageEditText.requestFocus();
            } catch (Exception e) {
                FileLog.e(e);
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
                this.messageEditText.postDelayed(new -$$Lambda$ChatActivityEnterView$w7v9u3TaN-X3t9L7-8faaWf5O3M(this), 600);
            }
        }
    }

    public /* synthetic */ void lambda$setFieldFocused$14$ChatActivityEnterView() {
        Object obj = 1;
        if (AndroidUtilities.isTablet()) {
            Activity activity = this.parentActivity;
            if (activity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) activity;
                if (launchActivity != null) {
                    ActionBarLayout layersActionBarLayout = launchActivity.getLayersActionBarLayout();
                    if (layersActionBarLayout != null && layersActionBarLayout.getVisibility() == 0) {
                        obj = null;
                    }
                }
            }
        }
        if (obj != null) {
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null) {
                try {
                    editTextCaption.requestFocus();
                } catch (Exception e) {
                    FileLog.e(e);
                }
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
        return hasText() ? this.messageEditText.getText() : null;
    }

    private void updateBotButton() {
        ImageView imageView = this.botButton;
        if (imageView != null) {
            float f;
            if (this.hasBotCommands || this.botReplyMarkup != null) {
                if (this.botButton.getVisibility() != 0) {
                    this.botButton.setVisibility(0);
                }
                if (this.botReplyMarkup == null) {
                    this.botButton.setImageResource(NUM);
                    this.botButton.setContentDescription(LocaleController.getString("AccDescrBotCommands", NUM));
                } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                    this.botButton.setImageResource(NUM);
                    this.botButton.setContentDescription(LocaleController.getString("AccDescrShowKeyboard", NUM));
                } else {
                    this.botButton.setImageResource(NUM);
                    this.botButton.setContentDescription(LocaleController.getString("AccDescrBotKeyboard", NUM));
                }
            } else {
                imageView.setVisibility(8);
            }
            updateFieldRight(2);
            LinearLayout linearLayout = this.attachLayout;
            ImageView imageView2 = this.botButton;
            if (imageView2 == null || imageView2.getVisibility() == 8) {
                imageView2 = this.notifyButton;
                if (imageView2 == null || imageView2.getVisibility() == 8) {
                    f = 48.0f;
                    linearLayout.setPivotX((float) AndroidUtilities.dp(f));
                }
            }
            f = 96.0f;
            linearLayout.setPivotX((float) AndroidUtilities.dp(f));
        }
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

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0075  */
    /* JADX WARNING: Missing block: B:39:0x00c2, code skipped:
            if (r8.getInt(r3.toString(), 0) == r7.getId()) goto L_0x00c6;
     */
    public void setButtons(org.telegram.messenger.MessageObject r7, boolean r8) {
        /*
        r6 = this;
        r0 = r6.replyingMessageObject;
        if (r0 == 0) goto L_0x000d;
    L_0x0004:
        r1 = r6.botButtonsMessageObject;
        if (r0 != r1) goto L_0x000d;
    L_0x0008:
        if (r0 == r7) goto L_0x000d;
    L_0x000a:
        r6.botMessageObject = r7;
        return;
    L_0x000d:
        r0 = r6.botButton;
        if (r0 == 0) goto L_0x00f2;
    L_0x0011:
        r0 = r6.botButtonsMessageObject;
        if (r0 == 0) goto L_0x0017;
    L_0x0015:
        if (r0 == r7) goto L_0x00f2;
    L_0x0017:
        r0 = r6.botButtonsMessageObject;
        if (r0 != 0) goto L_0x001f;
    L_0x001b:
        if (r7 != 0) goto L_0x001f;
    L_0x001d:
        goto L_0x00f2;
    L_0x001f:
        r0 = r6.botKeyboardView;
        if (r0 != 0) goto L_0x0044;
    L_0x0023:
        r0 = new org.telegram.ui.Components.BotKeyboardView;
        r1 = r6.parentActivity;
        r0.<init>(r1);
        r6.botKeyboardView = r0;
        r0 = r6.botKeyboardView;
        r1 = 8;
        r0.setVisibility(r1);
        r0 = r6.botKeyboardView;
        r1 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$b-Y8EmOjKWjLQcXw9E16k20A4eo;
        r1.<init>(r6);
        r0.setDelegate(r1);
        r0 = r6.sizeNotifierLayout;
        r1 = r6.botKeyboardView;
        r0.addView(r1);
    L_0x0044:
        r6.botButtonsMessageObject = r7;
        if (r7 == 0) goto L_0x0053;
    L_0x0048:
        r0 = r7.messageOwner;
        r0 = r0.reply_markup;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;
        if (r1 == 0) goto L_0x0053;
    L_0x0050:
        r0 = (org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup) r0;
        goto L_0x0054;
    L_0x0053:
        r0 = 0;
    L_0x0054:
        r6.botReplyMarkup = r0;
        r0 = r6.botKeyboardView;
        r1 = org.telegram.messenger.AndroidUtilities.displaySize;
        r2 = r1.x;
        r1 = r1.y;
        if (r2 <= r1) goto L_0x0063;
    L_0x0060:
        r1 = r6.keyboardHeightLand;
        goto L_0x0065;
    L_0x0063:
        r1 = r6.keyboardHeight;
    L_0x0065:
        r0.setPanelHeight(r1);
        r0 = r6.botKeyboardView;
        r1 = r6.botReplyMarkup;
        r0.setButtons(r1);
        r0 = r6.botReplyMarkup;
        r1 = 0;
        r2 = 1;
        if (r0 == 0) goto L_0x00dc;
    L_0x0075:
        r8 = r6.currentAccount;
        r8 = org.telegram.messenger.MessagesController.getMainSettings(r8);
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = "hidekeyboard_";
        r0.append(r3);
        r3 = r6.dialog_id;
        r0.append(r3);
        r0 = r0.toString();
        r0 = r8.getInt(r0, r1);
        r3 = r7.getId();
        if (r0 != r3) goto L_0x009a;
    L_0x0098:
        r0 = 1;
        goto L_0x009b;
    L_0x009a:
        r0 = 0;
    L_0x009b:
        r3 = r6.botButtonsMessageObject;
        r4 = r6.replyingMessageObject;
        if (r3 == r4) goto L_0x00c5;
    L_0x00a1:
        r3 = r6.botReplyMarkup;
        r3 = r3.single_use;
        if (r3 == 0) goto L_0x00c5;
    L_0x00a7:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "answered_";
        r3.append(r4);
        r4 = r6.dialog_id;
        r3.append(r4);
        r3 = r3.toString();
        r8 = r8.getInt(r3, r1);
        r7 = r7.getId();
        if (r8 != r7) goto L_0x00c5;
    L_0x00c4:
        goto L_0x00c6;
    L_0x00c5:
        r1 = 1;
    L_0x00c6:
        if (r1 == 0) goto L_0x00ef;
    L_0x00c8:
        if (r0 != 0) goto L_0x00ef;
    L_0x00ca:
        r7 = r6.messageEditText;
        r7 = r7.length();
        if (r7 != 0) goto L_0x00ef;
    L_0x00d2:
        r7 = r6.isPopupShowing();
        if (r7 != 0) goto L_0x00ef;
    L_0x00d8:
        r6.showPopup(r2, r2);
        goto L_0x00ef;
    L_0x00dc:
        r7 = r6.isPopupShowing();
        if (r7 == 0) goto L_0x00ef;
    L_0x00e2:
        r7 = r6.currentPopupContentType;
        if (r7 != r2) goto L_0x00ef;
    L_0x00e6:
        if (r8 == 0) goto L_0x00ec;
    L_0x00e8:
        r6.openKeyboardInternal();
        goto L_0x00ef;
    L_0x00ec:
        r6.showPopup(r1, r2);
    L_0x00ef:
        r6.updateBotButton();
    L_0x00f2:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setButtons(org.telegram.messenger.MessageObject, boolean):void");
    }

    public /* synthetic */ void lambda$setButtons$15$ChatActivityEnterView(KeyboardButton keyboardButton) {
        MessageObject messageObject = this.replyingMessageObject;
        if (messageObject == null) {
            messageObject = ((int) this.dialog_id) < 0 ? this.botButtonsMessageObject : null;
        }
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 == null) {
            messageObject2 = this.botButtonsMessageObject;
        }
        didPressedBotButton(keyboardButton, messageObject, messageObject2);
        if (this.replyingMessageObject != null) {
            openKeyboardInternal();
            setButtons(this.botMessageObject, false);
        } else if (this.botButtonsMessageObject.messageOwner.reply_markup.single_use) {
            openKeyboardInternal();
            Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("answered_");
            stringBuilder.append(this.dialog_id);
            edit.putInt(stringBuilder.toString(), this.botButtonsMessageObject.getId()).commit();
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend(null);
        }
    }

    public void didPressedBotButton(KeyboardButton keyboardButton, MessageObject messageObject, MessageObject messageObject2) {
        if (!(keyboardButton == null || messageObject2 == null)) {
            if (keyboardButton instanceof TL_keyboardButton) {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(keyboardButton.text, this.dialog_id, messageObject, null, false, null, null, null);
            } else if (keyboardButton instanceof TL_keyboardButtonUrl) {
                this.parentFragment.showOpenUrlAlert(keyboardButton.url, true);
            } else if (keyboardButton instanceof TL_keyboardButtonRequestPhone) {
                this.parentFragment.shareMyContact(messageObject2);
            } else if (keyboardButton instanceof TL_keyboardButtonRequestGeoLocation) {
                Builder builder = new Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ChatActivityEnterView$fmPI_spkDt8Wbdud0XG8IKtQnwY(this, messageObject2, keyboardButton));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                this.parentFragment.showDialog(builder.create());
            } else if ((keyboardButton instanceof TL_keyboardButtonCallback) || (keyboardButton instanceof TL_keyboardButtonGame) || (keyboardButton instanceof TL_keyboardButtonBuy) || (keyboardButton instanceof TL_keyboardButtonUrlAuth)) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCallback(true, messageObject2, keyboardButton, this.parentFragment);
            } else if ((keyboardButton instanceof TL_keyboardButtonSwitchInline) && !this.parentFragment.processSwitchButton((TL_keyboardButtonSwitchInline) keyboardButton)) {
                if (keyboardButton.same_peer) {
                    Message message = messageObject2.messageOwner;
                    int i = message.from_id;
                    int i2 = message.via_bot_id;
                    if (i2 == 0) {
                        i2 = i;
                    }
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i2));
                    if (user != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("@");
                        stringBuilder.append(user.username);
                        stringBuilder.append(" ");
                        stringBuilder.append(keyboardButton.query);
                        setFieldText(stringBuilder.toString());
                    } else {
                        return;
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putInt("dialogsType", 1);
                DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new -$$Lambda$ChatActivityEnterView$G5pe20Ftkscr87-dZmhZj8HOVnM(this, messageObject2, keyboardButton));
                this.parentFragment.presentFragment(dialogsActivity);
            }
        }
    }

    public /* synthetic */ void lambda$didPressedBotButton$16$ChatActivityEnterView(MessageObject messageObject, KeyboardButton keyboardButton, DialogInterface dialogInterface, int i) {
        if (VERSION.SDK_INT >= 23) {
            if (this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                this.parentActivity.requestPermissions(new String[]{r7, "android.permission.ACCESS_FINE_LOCATION"}, 2);
                this.pendingMessageObject = messageObject;
                this.pendingLocationButton = keyboardButton;
                return;
            }
        }
        SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(messageObject, keyboardButton);
    }

    public /* synthetic */ void lambda$didPressedBotButton$17$ChatActivityEnterView(MessageObject messageObject, KeyboardButton keyboardButton, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        Message message = messageObject.messageOwner;
        int i = message.from_id;
        int i2 = message.via_bot_id;
        if (i2 == 0) {
            i2 = i;
        }
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i2));
        if (user == null) {
            dialogsActivity.finishFragment();
            return;
        }
        long longValue = ((Long) arrayList.get(0)).longValue();
        DataQuery instance = DataQuery.getInstance(this.currentAccount);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@");
        stringBuilder.append(user.username);
        stringBuilder.append(" ");
        stringBuilder.append(keyboardButton.query);
        instance.saveDraft(longValue, stringBuilder.toString(), null, null, true);
        if (longValue != this.dialog_id) {
            i2 = (int) longValue;
            if (i2 != 0) {
                Bundle bundle = new Bundle();
                if (i2 > 0) {
                    bundle.putInt("user_id", i2);
                } else if (i2 < 0) {
                    bundle.putInt("chat_id", -i2);
                }
                if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
                    if (!this.parentFragment.presentFragment(new ChatActivity(bundle), true)) {
                        dialogsActivity.finishFragment();
                    } else if (!AndroidUtilities.isTablet()) {
                        this.parentFragment.removeSelfFromStack();
                    }
                } else {
                    return;
                }
            }
            dialogsActivity.finishFragment();
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
            this.emojiView.setDelegate(new EmojiViewDelegate() {
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
                        ChatActivityEnterView.this.innerTextChange = 2;
                        CharSequence replaceEmoji = Emoji.replaceEmoji(str, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(selectionEnd, replaceEmoji));
                        selectionEnd += replaceEmoji.length();
                        ChatActivityEnterView.this.messageEditText.setSelection(selectionEnd, selectionEnd);
                    } catch (Exception e) {
                        FileLog.e(e);
                    } catch (Throwable th) {
                        ChatActivityEnterView.this.innerTextChange = 0;
                    }
                    ChatActivityEnterView.this.innerTextChange = 0;
                }

                public void onStickerSelected(Document document, Object obj) {
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            ChatActivityEnterView.this.searchingType = 0;
                            ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(document));
                            ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                        }
                        ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                    }
                    ChatActivityEnterView.this.onStickerSelected(document, obj, false);
                    if (((int) ChatActivityEnterView.this.dialog_id) == 0 && MessageObject.isGifDocument(document)) {
                        MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).saveGif(obj, document);
                    }
                }

                public void onStickersSettingsClick() {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0));
                    }
                }

                public void onGifSelected(Object obj, Object obj2) {
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                        }
                        ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                    }
                    if (obj instanceof Document) {
                        Document document = (Document) obj;
                        SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(document, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, obj2);
                        DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
                        if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                            MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).saveGif(obj2, document);
                        }
                    } else if (obj instanceof BotInlineResult) {
                        BotInlineResult botInlineResult = (BotInlineResult) obj;
                        if (botInlineResult.document != null) {
                            DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(botInlineResult.document, (int) (System.currentTimeMillis() / 1000));
                            if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                                MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).saveGif(obj2, botInlineResult.document);
                            }
                        }
                        User user = (User) obj2;
                        HashMap hashMap = new HashMap();
                        hashMap.put("id", botInlineResult.id);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("");
                        stringBuilder.append(botInlineResult.query_id);
                        hashMap.put("query_id", stringBuilder.toString());
                        SendMessagesHelper.prepareSendingBotContextResult(botInlineResult, hashMap, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            ChatActivityEnterView.this.searchingType = 0;
                            ChatActivityEnterView.this.emojiView.closeSearch(true);
                            ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                        }
                    }
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onMessageSend(null);
                    }
                }

                public void onTabOpened(int i) {
                    ChatActivityEnterView.this.delegate.onStickersTab(i == 3);
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.post(chatActivityEnterView.updateExpandabilityRunnable);
                }

                public void onClearEmojiRecent() {
                    if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        Builder builder = new Builder(ChatActivityEnterView.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("ClearRecentEmoji", NUM));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new -$$Lambda$ChatActivityEnterView$26$SN56de-QEsvpP-TQQjotkzhHl_U(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                    }
                }

                public /* synthetic */ void lambda$onClearEmojiRecent$0$ChatActivityEnterView$26(DialogInterface dialogInterface, int i) {
                    ChatActivityEnterView.this.emojiView.clearRecentEmoji();
                }

                public void onShowStickerSet(StickerSet stickerSet, InputStickerSet inputStickerSet) {
                    if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        if (stickerSet != null) {
                            inputStickerSet = new TL_inputStickerSetID();
                            inputStickerSet.access_hash = stickerSet.access_hash;
                            inputStickerSet.id = stickerSet.id;
                        }
                        ChatActivityEnterView.this.parentFragment.showDialog(new StickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, inputStickerSet, null, ChatActivityEnterView.this));
                    }
                }

                public void onStickerSetAdd(StickerSetCovered stickerSetCovered) {
                    DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).removeStickersSet(ChatActivityEnterView.this.parentActivity, stickerSetCovered.set, 2, ChatActivityEnterView.this.parentFragment, false);
                }

                public void onStickerSetRemove(StickerSetCovered stickerSetCovered) {
                    DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).removeStickersSet(ChatActivityEnterView.this.parentActivity, stickerSetCovered.set, 0, ChatActivityEnterView.this.parentFragment, false);
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
                    ChatActivityEnterView.this.searchingType = i;
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
            });
            this.emojiView.setDragListener(new DragListener() {
                int initialOffset;
                boolean wasExpanded;

                public void onDragStart() {
                    if (allowDragging()) {
                        if (ChatActivityEnterView.this.stickersExpansionAnim != null) {
                            ChatActivityEnterView.this.stickersExpansionAnim.cancel();
                        }
                        ChatActivityEnterView.this.stickersDragging = true;
                        this.wasExpanded = ChatActivityEnterView.this.stickersExpanded;
                        ChatActivityEnterView.this.stickersExpanded = true;
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        chatActivityEnterView.stickersExpandedHeight = (((chatActivityEnterView.sizeNotifierLayout.getHeight() - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - ChatActivityEnterView.this.getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                        if (ChatActivityEnterView.this.searchingType == 2) {
                            chatActivityEnterView = ChatActivityEnterView.this;
                            int access$8400 = chatActivityEnterView.stickersExpandedHeight;
                            int dp = AndroidUtilities.dp(120.0f);
                            Point point = AndroidUtilities.displaySize;
                            chatActivityEnterView.stickersExpandedHeight = Math.min(access$8400, dp + (point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight));
                        }
                        ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                        ChatActivityEnterView.this.emojiView.setLayerType(2, null);
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
                        ChatActivityEnterView.this.stickersDragging = false;
                        if ((!this.wasExpanded || f < ((float) AndroidUtilities.dp(200.0f))) && ((this.wasExpanded || f > ((float) AndroidUtilities.dp(-200.0f))) && ((!this.wasExpanded || ChatActivityEnterView.this.stickersExpansionProgress > 0.6f) && (this.wasExpanded || ChatActivityEnterView.this.stickersExpansionProgress < 0.4f)))) {
                            ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, true);
                        } else {
                            ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded ^ 1, true, true);
                        }
                    }
                }

                public void onDragCancel() {
                    if (ChatActivityEnterView.this.stickersTabOpen) {
                        ChatActivityEnterView.this.stickersDragging = false;
                        ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, false);
                    }
                }

                public void onDrag(int i) {
                    if (allowDragging()) {
                        Point point = AndroidUtilities.displaySize;
                        int access$8600 = point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                        float max = (float) Math.max(Math.min(i + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - access$8600));
                        ChatActivityEnterView.this.emojiView.setTranslationY(max);
                        ChatActivityEnterView.this.setTranslationY(max);
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        chatActivityEnterView.stickersExpansionProgress = max / ((float) (-(chatActivityEnterView.stickersExpandedHeight - access$8600)));
                        ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                    }
                }

                private boolean allowDragging() {
                    return ChatActivityEnterView.this.stickersTabOpen && ((ChatActivityEnterView.this.stickersExpanded || ChatActivityEnterView.this.messageEditText.length() <= 0) && ChatActivityEnterView.this.emojiView.areThereAnyStickers());
                }
            });
            this.sizeNotifierLayout.addView(this.emojiView);
            checkChannelRights();
        }
    }

    public void onStickerSelected(Document document, Object obj, boolean z) {
        if (this.searchingType != 0) {
            this.searchingType = 0;
            this.emojiView.closeSearch(true);
            this.emojiView.hideSearchKeyboard();
        }
        setStickersExpanded(false, true, false);
        SendMessagesHelper.getInstance(this.currentAccount).sendSticker(document, this.dialog_id, this.replyingMessageObject, obj);
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend(null);
        }
        if (z) {
            setFieldText("");
        }
        DataQuery.getInstance(this.currentAccount).addRecentSticker(0, obj, document, (int) (System.currentTimeMillis() / 1000), false);
    }

    public void addStickerToRecent(Document document) {
        createEmojiView();
        this.emojiView.addRecentSticker(document);
    }

    public void hideEmojiView() {
        if (!this.emojiViewVisible) {
            EmojiView emojiView = this.emojiView;
            if (emojiView != null && emojiView.getVisibility() != 8) {
                this.emojiView.setVisibility(8);
            }
        }
    }

    private void showPopup(int i, int i2) {
        BotKeyboardView botKeyboardView;
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
                this.emojiView.setVisibility(0);
                this.emojiViewVisible = true;
                BotKeyboardView botKeyboardView2 = this.botKeyboardView;
                if (!(botKeyboardView2 == null || botKeyboardView2.getVisibility() == 8)) {
                    this.botKeyboardView.setVisibility(8);
                }
                view = this.emojiView;
            } else if (i2 == 1) {
                EmojiView emojiView = this.emojiView;
                if (!(emojiView == null || emojiView.getVisibility() == 8)) {
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
            botKeyboardView = this.botKeyboardView;
            if (botKeyboardView != null) {
                botKeyboardView.setPanelHeight(i3);
            }
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.height = i3;
            view.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayout != null) {
                this.emojiPadding = i3;
                sizeNotifierFrameLayout.requestLayout();
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
                if (AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    this.emojiView.setVisibility(8);
                }
            }
            botKeyboardView = this.botKeyboardView;
            if (botKeyboardView != null) {
                botKeyboardView.setVisibility(8);
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
        if (z2 && this.currentEmojiIcon == -1) {
            z2 = false;
        }
        if (z && this.currentPopupContentType == 0) {
            i = 0;
        } else {
            EmojiView emojiView = this.emojiView;
            if (emojiView == null) {
                i = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
            } else {
                i = emojiView.getCurrentPage();
            }
            i = (i == 0 || !(this.allowStickers || this.allowGifs)) ? 1 : i == 1 ? 2 : 3;
        }
        if (this.currentEmojiIcon != i) {
            AnimatorSet animatorSet = this.emojiButtonAnimation;
            Object obj = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiButtonAnimation = null;
            }
            if (i == 0) {
                this.emojiButton[z2 ? 1 : 0].setImageResource(NUM);
            } else if (i == 1) {
                this.emojiButton[z2 ? 1 : 0].setImageResource(NUM);
            } else if (i == 2) {
                this.emojiButton[z2 ? 1 : 0].setImageResource(NUM);
            } else if (i == 3) {
                this.emojiButton[z2 ? 1 : 0].setImageResource(NUM);
            }
            ImageView imageView = this.emojiButton[z2 ? 1 : 0];
            if (i == 2) {
                obj = Integer.valueOf(1);
            }
            imageView.setTag(obj);
            this.currentEmojiIcon = i;
            if (z2) {
                this.emojiButton[1].setVisibility(0);
                this.emojiButtonAnimation = new AnimatorSet();
                AnimatorSet animatorSet2 = this.emojiButtonAnimation;
                Animator[] animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
                this.emojiButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.emojiButtonAnimation)) {
                            ChatActivityEnterView.this.emojiButtonAnimation = null;
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
                Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("hidekeyboard_");
                stringBuilder.append(this.dialog_id);
                edit.putInt(stringBuilder.toString(), this.botButtonsMessageObject.getId()).commit();
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

    private void openKeyboardInternal() {
        int i = (AndroidUtilities.usingHardwareInput || this.isPaused) ? 0 : 2;
        showPopup(i, 0);
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
        if (!this.emojiViewVisible) {
            BotKeyboardView botKeyboardView = this.botKeyboardView;
            if (botKeyboardView == null || botKeyboardView.getVisibility() != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    public void addRecentGif(Document document) {
        DataQuery.getInstance(this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.addRecentGif(document);
        }
    }

    /* Access modifiers changed, original: protected */
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
        boolean z2 = true;
        if (this.searchingType != 0) {
            this.lastSizeChangeValue1 = i;
            this.lastSizeChangeValue2 = z;
            if (i <= 0) {
                z2 = false;
            }
            this.keyboardVisible = z2;
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
            BotKeyboardView botKeyboardView = this.botKeyboardView;
            if (botKeyboardView != null) {
                botKeyboardView.setPanelHeight(i2);
            }
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (!(this.closeAnimationInProgress || ((layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == i2) || this.stickersExpanded))) {
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
        z = this.keyboardVisible;
        if (i <= 0) {
            z2 = false;
        }
        this.keyboardVisible = z2;
        if (this.keyboardVisible && isPopupShowing()) {
            showPopup(0, this.currentPopupContentType);
        }
        if (this.emojiPadding != 0) {
            boolean z3 = this.keyboardVisible;
            if (!(z3 || z3 == z || isPopupShowing())) {
                this.emojiPadding = 0;
                this.sizeNotifierLayout.requestLayout();
            }
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
        if (i == NotificationCenter.emojiDidLoad) {
            EmojiView emojiView = this.emojiView;
            if (emojiView != null) {
                emojiView.invalidateViews();
            }
            BotKeyboardView botKeyboardView = this.botKeyboardView;
            if (botKeyboardView != null) {
                botKeyboardView.invalidateViews();
            }
        } else {
            int i3 = 0;
            Integer num;
            if (i == NotificationCenter.recordProgressChanged) {
                long longValue = ((Long) objArr[0]).longValue();
                long j = longValue / 1000;
                int i4 = ((int) (longValue % 1000)) / 10;
                String format = String.format("%02d:%02d.%02d", new Object[]{Long.valueOf(j / 60), Long.valueOf(j % 60), Integer.valueOf(i4)});
                String str = this.lastTimeString;
                if (str == null || !str.equals(format)) {
                    if (this.lastTypingSendTime != j && j % 5 == 0) {
                        this.lastTypingSendTime = j;
                        MessagesController instance = MessagesController.getInstance(this.currentAccount);
                        j = this.dialog_id;
                        ImageView imageView = this.videoSendButton;
                        int i5 = (imageView == null || imageView.getTag() == null) ? 1 : 7;
                        instance.sendTyping(j, i5, 0);
                    }
                    TextView textView = this.recordTimeText;
                    if (textView != null) {
                        textView.setText(format);
                    }
                }
                RecordCircle recordCircle = this.recordCircle;
                if (recordCircle != null) {
                    recordCircle.setAmplitude(((Double) objArr[1]).doubleValue());
                }
                ImageView imageView2 = this.videoSendButton;
                if (!(imageView2 == null || imageView2.getTag() == null || longValue < 59500)) {
                    this.startedDraggingX = -1.0f;
                    this.delegate.needStartRecordVideo(3);
                }
            } else if (i == NotificationCenter.closeChats) {
                EditTextCaption editTextCaption = this.messageEditText;
                if (editTextCaption != null && editTextCaption.isFocused()) {
                    AndroidUtilities.hideKeyboard(this.messageEditText);
                }
            } else if (i == NotificationCenter.recordStartError || i == NotificationCenter.recordStopped) {
                if (this.recordingAudioVideo) {
                    MessagesController.getInstance(this.currentAccount).sendTyping(this.dialog_id, 2, 0);
                    this.recordingAudioVideo = false;
                    updateRecordIntefrace();
                }
                if (i == NotificationCenter.recordStopped) {
                    num = (Integer) objArr[0];
                    if (num.intValue() == 2) {
                        this.videoTimelineView.setVisibility(0);
                        this.recordedAudioBackground.setVisibility(8);
                        this.recordedAudioTimeTextView.setVisibility(8);
                        this.recordedAudioPlayButton.setVisibility(8);
                        this.recordedAudioSeekBar.setVisibility(8);
                        this.recordedAudioPanel.setAlpha(1.0f);
                        this.recordedAudioPanel.setVisibility(0);
                    } else {
                        num.intValue();
                    }
                }
            } else if (i == NotificationCenter.recordStarted) {
                if (!this.recordingAudioVideo) {
                    this.recordingAudioVideo = true;
                    updateRecordIntefrace();
                }
            } else if (i == NotificationCenter.audioDidSent) {
                Object obj = objArr[0];
                if (obj instanceof VideoEditedInfo) {
                    this.videoToSendMessageObject = (VideoEditedInfo) obj;
                    this.audioToSendPath = (String) objArr[1];
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
                } else {
                    this.audioToSend = (TL_document) objArr[0];
                    this.audioToSendPath = (String) objArr[1];
                    if (this.audioToSend == null) {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                        if (chatActivityEnterViewDelegate != null) {
                            chatActivityEnterViewDelegate.onMessageSend(null);
                        }
                    } else if (this.recordedAudioPanel != null) {
                        this.videoTimelineView.setVisibility(8);
                        this.recordedAudioBackground.setVisibility(0);
                        this.recordedAudioTimeTextView.setVisibility(0);
                        this.recordedAudioPlayButton.setVisibility(0);
                        this.recordedAudioSeekBar.setVisibility(0);
                        TL_message tL_message = new TL_message();
                        tL_message.out = true;
                        tL_message.id = 0;
                        tL_message.to_id = new TL_peerUser();
                        Peer peer = tL_message.to_id;
                        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                        tL_message.from_id = clientUserId;
                        peer.user_id = clientUserId;
                        tL_message.date = (int) (System.currentTimeMillis() / 1000);
                        tL_message.message = "";
                        tL_message.attachPath = this.audioToSendPath;
                        tL_message.media = new TL_messageMediaDocument();
                        MessageMedia messageMedia = tL_message.media;
                        messageMedia.flags |= 3;
                        messageMedia.document = this.audioToSend;
                        tL_message.flags |= 768;
                        this.audioToSendMessageObject = new MessageObject(UserConfig.selectedAccount, tL_message, false);
                        this.recordedAudioPanel.setAlpha(1.0f);
                        this.recordedAudioPanel.setVisibility(0);
                        for (i = 0; i < this.audioToSend.attributes.size(); i++) {
                            DocumentAttribute documentAttribute = (DocumentAttribute) this.audioToSend.attributes.get(i);
                            if (documentAttribute instanceof TL_documentAttributeAudio) {
                                i = documentAttribute.duration;
                                break;
                            }
                        }
                        i = 0;
                        i2 = 0;
                        while (i2 < this.audioToSend.attributes.size()) {
                            DocumentAttribute documentAttribute2 = (DocumentAttribute) this.audioToSend.attributes.get(i2);
                            if (documentAttribute2 instanceof TL_documentAttributeAudio) {
                                byte[] bArr = documentAttribute2.waveform;
                                if (bArr == null || bArr.length == 0) {
                                    documentAttribute2.waveform = MediaController.getInstance().getWaveform(this.audioToSendPath);
                                }
                                this.recordedAudioSeekBar.setWaveform(documentAttribute2.waveform);
                                this.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(i / 60), Integer.valueOf(i % 60)}));
                                closeKeyboard();
                                hidePopup(false);
                                checkSendButton(false);
                            } else {
                                i2++;
                            }
                        }
                        this.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(i / 60), Integer.valueOf(i % 60)}));
                        closeKeyboard();
                        hidePopup(false);
                        checkSendButton(false);
                    }
                }
            } else if (i == NotificationCenter.audioRouteChanged) {
                if (this.parentActivity != null) {
                    boolean booleanValue = ((Boolean) objArr[0]).booleanValue();
                    Activity activity = this.parentActivity;
                    if (!booleanValue) {
                        i3 = Integer.MIN_VALUE;
                    }
                    activity.setVolumeControlStream(i3);
                }
            } else if (i == NotificationCenter.messagePlayingDidReset) {
                if (!(this.audioToSendMessageObject == null || MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject))) {
                    this.recordedAudioPlayButton.setImageDrawable(this.playDrawable);
                    this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
                    this.recordedAudioSeekBar.setProgress(0.0f);
                }
            } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
                num = (Integer) objArr[0];
                if (this.audioToSendMessageObject != null && MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                    MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                    MessageObject messageObject = this.audioToSendMessageObject;
                    messageObject.audioProgress = playingMessageObject.audioProgress;
                    messageObject.audioProgressSec = playingMessageObject.audioProgressSec;
                    if (!this.recordedAudioSeekBar.isDragging()) {
                        this.recordedAudioSeekBar.setProgress(this.audioToSendMessageObject.audioProgress);
                    }
                }
            } else if (i == NotificationCenter.featuredStickersDidLoad && this.emojiButton != null) {
                while (true) {
                    ImageView[] imageViewArr = this.emojiButton;
                    if (i3 >= imageViewArr.length) {
                        break;
                    }
                    imageViewArr[i3].invalidate();
                    i3++;
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

    private void checkStickresExpandHeight() {
        Point point = AndroidUtilities.displaySize;
        int i = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
        int currentActionBarHeight = (((this.originalViewHeight - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
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
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (i2 > currentActionBarHeight) {
                animatorSet = new AnimatorSet();
                animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)});
                animatorArr[1] = ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)});
                animatorSet.playTogether(animatorArr);
                ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new -$$Lambda$ChatActivityEnterView$wjaxKxy8o6FENKkopnAtGTac1QI(this));
                animatorSet.setDuration(400);
                animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ChatActivityEnterView.this.stickersExpansionAnim = null;
                        if (ChatActivityEnterView.this.emojiView != null) {
                            ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                            ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                        }
                    }
                });
                this.stickersExpansionAnim = animatorSet;
                this.emojiView.setLayerType(2, null);
                animatorSet.start();
            } else {
                this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
                this.sizeNotifierLayout.requestLayout();
                currentActionBarHeight = this.messageEditText.getSelectionStart();
                i2 = this.messageEditText.getSelectionEnd();
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setText(editTextCaption.getText());
                this.messageEditText.setSelection(currentActionBarHeight, i2);
                animatorSet = new AnimatorSet();
                animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)});
                animatorArr[1] = ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)});
                animatorSet.playTogether(animatorArr);
                ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new -$$Lambda$ChatActivityEnterView$XEk0ExTurPjgeNRTljJZaBiOh9o(this));
                animatorSet.setDuration(400);
                animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ChatActivityEnterView.this.stickersExpansionAnim = null;
                        ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                    }
                });
                this.stickersExpansionAnim = animatorSet;
                this.emojiView.setLayerType(2, null);
                animatorSet.start();
            }
        }
    }

    public /* synthetic */ void lambda$checkStickresExpandHeight$18$ChatActivityEnterView(ValueAnimator valueAnimator) {
        this.sizeNotifierLayout.invalidate();
    }

    public /* synthetic */ void lambda$checkStickresExpandHeight$19$ChatActivityEnterView(ValueAnimator valueAnimator) {
        this.sizeNotifierLayout.invalidate();
    }

    private void setStickersExpanded(boolean z, boolean z2, boolean z3) {
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
            String str = "animationProgress";
            Animator[] animatorArr;
            if (this.stickersExpanded) {
                this.originalViewHeight = this.sizeNotifierLayout.getHeight();
                this.stickersExpandedHeight = (((this.originalViewHeight - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
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
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)});
                    animatorArr[1] = ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.stickersArrow, str, new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(400);
                    animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new -$$Lambda$ChatActivityEnterView$4bFyOKzZkoegxIkwH3otL-R7SUo(this, i));
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ChatActivityEnterView.this.stickersExpansionAnim = null;
                            ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                        }
                    });
                    this.stickersExpansionAnim = animatorSet;
                    this.emojiView.setLayerType(2, null);
                    animatorSet.start();
                } else {
                    this.stickersExpansionProgress = 1.0f;
                    setTranslationY((float) (-(this.stickersExpandedHeight - i)));
                    this.emojiView.setTranslationY((float) (-(this.stickersExpandedHeight - i)));
                    this.stickersArrow.setAnimationProgress(1.0f);
                }
            } else if (z2) {
                this.closeAnimationInProgress = true;
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{0});
                animatorArr[1] = ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{0});
                animatorArr[2] = ObjectAnimator.ofFloat(this.stickersArrow, str, new float[]{0.0f});
                animatorSet2.playTogether(animatorArr);
                animatorSet2.setDuration(400);
                animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                ((ObjectAnimator) animatorSet2.getChildAnimations().get(0)).addUpdateListener(new -$$Lambda$ChatActivityEnterView$A5TmJlGp3wUlMNBvVwJxDClmnmg(this, i));
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ChatActivityEnterView.this.closeAnimationInProgress = false;
                        ChatActivityEnterView.this.stickersExpansionAnim = null;
                        if (ChatActivityEnterView.this.emojiView != null) {
                            ChatActivityEnterView.this.emojiView.getLayoutParams().height = i;
                            ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                        }
                        if (ChatActivityEnterView.this.sizeNotifierLayout != null) {
                            ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                            ChatActivityEnterView.this.sizeNotifierLayout.setForeground(null);
                            ChatActivityEnterView.this.sizeNotifierLayout.setWillNotDraw(false);
                        }
                    }
                });
                this.stickersExpansionAnim = animatorSet2;
                this.emojiView.setLayerType(2, null);
                animatorSet2.start();
            } else {
                this.stickersExpansionProgress = 0.0f;
                setTranslationY(0.0f);
                this.emojiView.setTranslationY(0.0f);
                this.emojiView.getLayoutParams().height = i;
                this.sizeNotifierLayout.requestLayout();
                this.sizeNotifierLayout.setForeground(null);
                this.sizeNotifierLayout.setWillNotDraw(false);
                this.stickersArrow.setAnimationProgress(0.0f);
            }
            if (z4) {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrCollapsePanel", NUM));
            } else {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", NUM));
            }
        }
    }

    public /* synthetic */ void lambda$setStickersExpanded$20$ChatActivityEnterView(int i, ValueAnimator valueAnimator) {
        this.stickersExpansionProgress = getTranslationY() / ((float) (-(this.stickersExpandedHeight - i)));
        this.sizeNotifierLayout.invalidate();
    }

    public /* synthetic */ void lambda$setStickersExpanded$21$ChatActivityEnterView(int i, ValueAnimator valueAnimator) {
        this.stickersExpansionProgress = getTranslationY() / ((float) (-(this.stickersExpandedHeight - i)));
        this.sizeNotifierLayout.invalidate();
    }
}
