package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.Keep;
import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat.OnCommitContentListener;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v4.os.BuildCompat;
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
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
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
import java.io.File;
import java.util.ArrayList;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
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
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotKeyboardView.BotKeyboardViewDelegate;
import org.telegram.ui.Components.EmojiView.DragListener;
import org.telegram.ui.Components.EmojiView.Listener;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate;
import org.telegram.ui.Components.StickersAlert.StickersAlertDelegate;
import org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.GroupStickersActivity;
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
    private int currentPopupContentType = -1;
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
    private ImageView emojiButton;
    private int emojiPadding;
    private EmojiView emojiView;
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
    private EditTextCaption messageEditText;
    private WebPage messageWebPage;
    private boolean messageWebPageSearch = true;
    private Drawable micDrawable;
    private boolean needShowTopView;
    private ImageView notifyButton;
    private Runnable openKeyboardRunnable = new C10941();
    private Paint paint = new Paint(1);
    private Paint paintRecord = new Paint(1);
    private Activity parentActivity;
    private ChatActivity parentFragment;
    private Drawable pauseDrawable;
    private KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    private Drawable playDrawable;
    private CloseProgressDrawable2 progressDrawable;
    private Runnable recordAudioVideoRunnable = new C10984();
    private boolean recordAudioVideoRunnableStarted;
    private ImageView recordCancelImage;
    private TextView recordCancelText;
    private RecordCircle recordCircle;
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
        public Integer get(View object) {
            return Integer.valueOf(Math.round(object.getTranslationY()));
        }

        public void set(View object, Integer value) {
            object.setTranslationY((float) value.intValue());
        }
    };
    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private AnimatorSet runningAnimationAudio;
    private int runningAnimationType;
    private boolean searchingStickers;
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
    private View topView;
    private boolean topViewShowed;
    private Runnable updateExpandabilityRunnable = new C10952();
    private ImageView videoSendButton;
    private VideoTimelineView videoTimelineView;
    private VideoEditedInfo videoToSendMessageObject;
    private boolean waitingForKeyboardOpen;
    private WakeLock wakeLock;

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$1 */
    class C10941 implements Runnable {
        C10941() {
        }

        public void run() {
            if (!ChatActivityEnterView.this.destroyed && ChatActivityEnterView.this.messageEditText != null && ChatActivityEnterView.this.waitingForKeyboardOpen && !ChatActivityEnterView.this.keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow) {
                ChatActivityEnterView.this.messageEditText.requestFocus();
                AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
                AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$2 */
    class C10952 implements Runnable {
        private int lastKnownPage = -1;

        C10952() {
        }

        public void run() {
            if (ChatActivityEnterView.this.emojiView != null) {
                int curPage = ChatActivityEnterView.this.emojiView.getCurrentPage();
                if (curPage != this.lastKnownPage) {
                    boolean z;
                    this.lastKnownPage = curPage;
                    boolean prevOpen = ChatActivityEnterView.this.stickersTabOpen;
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    if (curPage != 1) {
                        if (curPage != 2) {
                            z = false;
                            chatActivityEnterView.stickersTabOpen = z;
                            if (prevOpen != ChatActivityEnterView.this.stickersTabOpen) {
                                ChatActivityEnterView.this.checkSendButton(true);
                            }
                            if (!ChatActivityEnterView.this.stickersTabOpen && ChatActivityEnterView.this.stickersExpanded) {
                                if (ChatActivityEnterView.this.searchingStickers) {
                                    ChatActivityEnterView.this.searchingStickers = false;
                                    ChatActivityEnterView.this.emojiView.closeSearch(true);
                                    ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                                }
                                ChatActivityEnterView.this.setStickersExpanded(false, true);
                                return;
                            }
                        }
                    }
                    z = true;
                    chatActivityEnterView.stickersTabOpen = z;
                    if (prevOpen != ChatActivityEnterView.this.stickersTabOpen) {
                        ChatActivityEnterView.this.checkSendButton(true);
                    }
                    if (!ChatActivityEnterView.this.stickersTabOpen) {
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$4 */
    class C10984 implements Runnable {
        C10984() {
        }

        public void run() {
            if (ChatActivityEnterView.this.delegate != null) {
                if (ChatActivityEnterView.this.parentActivity != null) {
                    ChatActivityEnterView.this.delegate.onPreAudioVideoRecord();
                    ChatActivityEnterView.this.calledRecordRunnable = true;
                    ChatActivityEnterView.this.recordAudioVideoRunnableStarted = false;
                    ChatActivityEnterView.this.recordCircle.setLockTranslation(10000.0f);
                    ChatActivityEnterView.this.recordSendText.setAlpha(0.0f);
                    ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                    ChatActivityEnterView.this.slideText.setTranslationY(0.0f);
                    if (ChatActivityEnterView.this.videoSendButton != null && ChatActivityEnterView.this.videoSendButton.getTag() != null) {
                        if (VERSION.SDK_INT >= 23) {
                            boolean hasAudio = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0;
                            boolean hasVideo = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.CAMERA") == 0;
                            if (!(hasAudio && hasVideo)) {
                                String[] permissions = (hasAudio || hasVideo) ? 1 : 2;
                                permissions = new String[permissions];
                                if (!hasAudio && !hasVideo) {
                                    permissions[0] = "android.permission.RECORD_AUDIO";
                                    permissions[1] = "android.permission.CAMERA";
                                } else if (hasAudio) {
                                    permissions[0] = "android.permission.CAMERA";
                                } else {
                                    permissions[0] = "android.permission.RECORD_AUDIO";
                                }
                                ChatActivityEnterView.this.parentActivity.requestPermissions(permissions, 3);
                                return;
                            }
                        }
                        ChatActivityEnterView.this.delegate.needStartRecordVideo(0);
                    } else if (ChatActivityEnterView.this.parentFragment == null || VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                        ChatActivityEnterView.this.delegate.needStartRecordAudio(1);
                        ChatActivityEnterView.this.startedDraggingX = -1.0f;
                        MediaController.getInstance().startRecording(ChatActivityEnterView.this.currentAccount, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
                        ChatActivityEnterView.this.updateRecordIntefrace();
                        ChatActivityEnterView.this.audioVideoButtonContainer.getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$6 */
    class C11006 implements OnClickListener {
        C11006() {
        }

        public void onClick(View view) {
            boolean z = false;
            if (ChatActivityEnterView.this.isPopupShowing()) {
                if (ChatActivityEnterView.this.currentPopupContentType == 0) {
                    if (ChatActivityEnterView.this.searchingStickers) {
                        ChatActivityEnterView.this.searchingStickers = false;
                        ChatActivityEnterView.this.emojiView.closeSearch(false);
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                    }
                    ChatActivityEnterView.this.openKeyboardInternal();
                    ChatActivityEnterView.this.removeGifFromInputField();
                    return;
                }
            }
            ChatActivityEnterView.this.showPopup(1, 0);
            EmojiView access$700 = ChatActivityEnterView.this.emojiView;
            if (ChatActivityEnterView.this.messageEditText.length() > 0 && !ChatActivityEnterView.this.messageEditText.getText().toString().startsWith("@gif")) {
                z = true;
            }
            access$700.onOpen(z);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$8 */
    class C11018 implements OnKeyListener {
        boolean ctrlPressed = false;

        C11018() {
        }

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
                    if (ChatActivityEnterView.this.searchingStickers) {
                        ChatActivityEnterView.this.searchingStickers = false;
                        ChatActivityEnterView.this.emojiView.closeSearch(true);
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                    } else {
                        ChatActivityEnterView.this.showPopup(0, 0);
                        ChatActivityEnterView.this.removeGifFromInputField();
                    }
                }
                return true;
            } else if (i == 66 && ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0 && ChatActivityEnterView.this.editingMessageObject == null)) {
                ChatActivityEnterView.this.sendMessage();
                return true;
            } else {
                if (i != 113) {
                    if (i != 114) {
                        return false;
                    }
                }
                if (keyEvent.getAction() == 0) {
                    z = true;
                }
                this.ctrlPressed = z;
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$9 */
    class C11029 implements OnEditorActionListener {
        boolean ctrlPressed = false;

        C11029() {
        }

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
    }

    private class AnimatedArrowDrawable extends Drawable {
        private float animProgress = null;
        private Paint paint = new Paint(1);
        private Path path = new Path();

        public AnimatedArrowDrawable(int color) {
            this.paint.setStyle(Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.paint.setColor(color);
            updatePath();
        }

        public void draw(Canvas c) {
            c.drawPath(this.path, this.paint);
        }

        private void updatePath() {
            this.path.reset();
            float p = (this.animProgress * 2.0f) - 1.0f;
            this.path.moveTo((float) AndroidUtilities.dp(3.0f), ((float) AndroidUtilities.dp(12.0f)) - (((float) AndroidUtilities.dp(4.0f)) * p));
            this.path.lineTo((float) AndroidUtilities.dp(13.0f), ((float) AndroidUtilities.dp(12.0f)) + (((float) AndroidUtilities.dp(4.0f)) * p));
            this.path.lineTo((float) AndroidUtilities.dp(23.0f), ((float) AndroidUtilities.dp(12.0f)) - (((float) AndroidUtilities.dp(4.0f)) * p));
        }

        @Keep
        public void setAnimationProgress(float progress) {
            this.animProgress = progress;
            updatePath();
            invalidateSelf();
        }

        public float getAnimationProgress() {
            return this.animProgress;
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(26.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(26.0f);
        }
    }

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

        public RecordCircle(Context context) {
            super(context);
            ChatActivityEnterView.this.paint.setColor(Theme.getColor(Theme.key_chat_messagePanelVoiceBackground));
            ChatActivityEnterView.this.paintRecord.setColor(Theme.getColor(Theme.key_chat_messagePanelVoiceShadow));
            ChatActivityEnterView.this.lockDrawable = getResources().getDrawable(R.drawable.lock_middle);
            ChatActivityEnterView.this.lockDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLock), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockTopDrawable = getResources().getDrawable(R.drawable.lock_top);
            ChatActivityEnterView.this.lockTopDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLock), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockArrowDrawable = getResources().getDrawable(R.drawable.lock_arrow);
            ChatActivityEnterView.this.lockArrowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLock), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockBackgroundDrawable = getResources().getDrawable(R.drawable.lock_round);
            ChatActivityEnterView.this.lockBackgroundDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLockBackground), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockShadowDrawable = getResources().getDrawable(R.drawable.lock_round_shadow);
            ChatActivityEnterView.this.lockShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLockShadow), Mode.MULTIPLY));
            ChatActivityEnterView.this.micDrawable = getResources().getDrawable(R.drawable.mic).mutate();
            ChatActivityEnterView.this.micDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), Mode.MULTIPLY));
            ChatActivityEnterView.this.cameraDrawable = getResources().getDrawable(R.drawable.ic_msg_panel_video).mutate();
            ChatActivityEnterView.this.cameraDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), Mode.MULTIPLY));
            ChatActivityEnterView.this.sendDrawable = getResources().getDrawable(R.drawable.ic_send).mutate();
            ChatActivityEnterView.this.sendDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), Mode.MULTIPLY));
        }

        public void setAmplitude(double value) {
            this.animateToAmplitude = ((float) Math.min(100.0d, value)) / 100.0f;
            this.animateAmplitudeDiff = (this.animateToAmplitude - this.amplitude) / 150.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
        }

        public float getScale() {
            return this.scale;
        }

        @Keep
        public void setScale(float value) {
            this.scale = value;
            invalidate();
        }

        @Keep
        public void setLockAnimatedTranslation(float value) {
            this.lockAnimatedTranslation = value;
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
                return 0;
            } else if (this.sendButtonVisible) {
                return 2;
            } else {
                if (this.lockAnimatedTranslation == -1.0f) {
                    this.startTranslation = value;
                }
                this.lockAnimatedTranslation = value;
                invalidate();
                if (this.startTranslation - this.lockAnimatedTranslation < ((float) AndroidUtilities.dp(57.0f))) {
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
                    boolean contains = ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains(x, y);
                    this.pressed = contains;
                    if (contains) {
                        return true;
                    }
                } else if (this.pressed) {
                    if (event.getAction() == 1 && ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains(x, y)) {
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

        protected void onDraw(Canvas canvas) {
            float alpha;
            float sc;
            long dt;
            Drawable drawable;
            float moveProgress;
            float moveProgress2;
            int intAlpha;
            int lockSize;
            int lockY;
            int lockTopY;
            int lockMiddleY;
            int lockArrowY;
            Canvas canvas2 = canvas;
            int cx = getMeasuredWidth() / 2;
            int cy = AndroidUtilities.dp(NUM);
            float yAdd = 0.0f;
            if (this.lockAnimatedTranslation != 10000.0f) {
                yAdd = (float) Math.max(0, (int) (r0.startTranslation - r0.lockAnimatedTranslation));
                if (yAdd > ((float) AndroidUtilities.dp(57.0f))) {
                    yAdd = (float) AndroidUtilities.dp(57.0f);
                }
            }
            cy = (int) (((float) cy) - yAdd);
            if (r0.scale <= 0.5f) {
                alpha = r0.scale / 0.5f;
                sc = alpha;
            } else if (r0.scale <= 0.75f) {
                sc = 1.0f - (((r0.scale - 0.5f) / 0.25f) * 0.1f);
                alpha = 1.0f;
            } else {
                sc = (((r0.scale - 0.75f) / 0.25f) * 0.1f) + 0.9f;
                alpha = 1.0f;
                dt = System.currentTimeMillis() - r0.lastUpdateTime;
                if (r0.animateToAmplitude != r0.amplitude) {
                    r0.amplitude += r0.animateAmplitudeDiff * ((float) dt);
                    if (r0.animateAmplitudeDiff <= 0.0f) {
                        if (r0.amplitude > r0.animateToAmplitude) {
                            r0.amplitude = r0.animateToAmplitude;
                        }
                    } else if (r0.amplitude < r0.animateToAmplitude) {
                        r0.amplitude = r0.animateToAmplitude;
                    }
                    invalidate();
                }
                r0.lastUpdateTime = System.currentTimeMillis();
                if (r0.amplitude != 0.0f) {
                    canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) cy, (((float) AndroidUtilities.dp(42.0f)) + (((float) AndroidUtilities.dp(20.0f)) * r0.amplitude)) * r0.scale, ChatActivityEnterView.this.paintRecord);
                }
                canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) cy, ((float) AndroidUtilities.dp(42.0f)) * sc, ChatActivityEnterView.this.paint);
                drawable = isSendButtonVisible() ? ChatActivityEnterView.this.sendDrawable : (ChatActivityEnterView.this.videoSendButton != null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
                drawable.setBounds(cx - (drawable.getIntrinsicWidth() / 2), cy - (drawable.getIntrinsicHeight() / 2), (drawable.getIntrinsicWidth() / 2) + cx, (drawable.getIntrinsicHeight() / 2) + cy);
                drawable.setAlpha((int) (255.0f * alpha));
                drawable.draw(canvas2);
                moveProgress = 1.0f - (yAdd / ((float) AndroidUtilities.dp(57.0f)));
                moveProgress2 = Math.max(0.0f, 1.0f - ((yAdd / ((float) AndroidUtilities.dp(57.0f))) * 2.0f));
                intAlpha = (int) (NUM * alpha);
                if (isSendButtonVisible()) {
                    float f = alpha;
                    lockSize = AndroidUtilities.dp(31.0f) + ((int) (((float) AndroidUtilities.dp(29.0f)) * moveProgress));
                    lockY = (AndroidUtilities.dp(57.0f) + ((int) (((float) AndroidUtilities.dp(30.0f)) * (1.0f - sc)))) - ((int) yAdd);
                    lockTopY = (lockY + AndroidUtilities.dp(5.0f)) + ((int) (((float) AndroidUtilities.dp(4.0f)) * moveProgress));
                    lockMiddleY = (lockY + AndroidUtilities.dp(11.0f)) + ((int) (((float) AndroidUtilities.dp(10.0f)) * moveProgress));
                    cy = (lockY + AndroidUtilities.dp(25.0f)) + ((int) (((float) AndroidUtilities.dp(16.0f)) * moveProgress));
                    ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(intAlpha);
                    ChatActivityEnterView.this.lockShadowDrawable.setAlpha(intAlpha);
                    ChatActivityEnterView.this.lockTopDrawable.setAlpha(intAlpha);
                    ChatActivityEnterView.this.lockDrawable.setAlpha(intAlpha);
                    ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) intAlpha) * moveProgress2));
                    lockArrowY = cy;
                    cy = lockY;
                } else {
                    lockSize = AndroidUtilities.dp(31.0f);
                    lockY = AndroidUtilities.dp(57.0f) + ((int) (((((float) AndroidUtilities.dp(30.0f)) * (1.0f - sc)) - yAdd) + (((float) AndroidUtilities.dp(20.0f)) * moveProgress)));
                    lockTopY = lockY + AndroidUtilities.dp(5.0f);
                    lockMiddleY = lockY + AndroidUtilities.dp(11.0f);
                    int lockArrowY2 = lockY + AndroidUtilities.dp(NUM);
                    intAlpha = (int) (((float) intAlpha) * (yAdd / ((float) AndroidUtilities.dp(57.0f))));
                    ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(255);
                    ChatActivityEnterView.this.lockShadowDrawable.setAlpha(255);
                    ChatActivityEnterView.this.lockTopDrawable.setAlpha(intAlpha);
                    ChatActivityEnterView.this.lockDrawable.setAlpha(intAlpha);
                    ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) intAlpha) * moveProgress2));
                    float f2 = yAdd;
                    cy = lockY;
                    lockArrowY = lockArrowY2;
                }
                ChatActivityEnterView.this.lockBackgroundDrawable.setBounds(cx - AndroidUtilities.dp(15.0f), cy, AndroidUtilities.dp(15.0f) + cx, cy + lockSize);
                ChatActivityEnterView.this.lockBackgroundDrawable.draw(canvas2);
                ChatActivityEnterView.this.lockShadowDrawable.setBounds(cx - AndroidUtilities.dp(16.0f), cy - AndroidUtilities.dp(1.0f), AndroidUtilities.dp(16.0f) + cx, (cy + lockSize) + AndroidUtilities.dp(1.0f));
                ChatActivityEnterView.this.lockShadowDrawable.draw(canvas2);
                ChatActivityEnterView.this.lockTopDrawable.setBounds(cx - AndroidUtilities.dp(6.0f), lockTopY, AndroidUtilities.dp(6.0f) + cx, AndroidUtilities.dp(14.0f) + lockTopY);
                ChatActivityEnterView.this.lockTopDrawable.draw(canvas2);
                ChatActivityEnterView.this.lockDrawable.setBounds(cx - AndroidUtilities.dp(7.0f), lockMiddleY, AndroidUtilities.dp(7.0f) + cx, AndroidUtilities.dp(12.0f) + lockMiddleY);
                ChatActivityEnterView.this.lockDrawable.draw(canvas2);
                ChatActivityEnterView.this.lockArrowDrawable.setBounds(cx - AndroidUtilities.dp(7.5f), lockArrowY, AndroidUtilities.dp(7.5f) + cx, AndroidUtilities.dp(9.0f) + lockArrowY);
                ChatActivityEnterView.this.lockArrowDrawable.draw(canvas2);
                if (isSendButtonVisible()) {
                    ChatActivityEnterView.this.redDotPaint.setAlpha(255);
                    ChatActivityEnterView.this.rect.set((float) (cx - AndroidUtilities.dp2(6.5f)), (float) (AndroidUtilities.dp(9.0f) + cy), (float) (AndroidUtilities.dp(6.5f) + cx), (float) (AndroidUtilities.dp(22.0f) + cy));
                    canvas2.drawRoundRect(ChatActivityEnterView.this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ChatActivityEnterView.this.redDotPaint);
                }
            }
            dt = System.currentTimeMillis() - r0.lastUpdateTime;
            if (r0.animateToAmplitude != r0.amplitude) {
                r0.amplitude += r0.animateAmplitudeDiff * ((float) dt);
                if (r0.animateAmplitudeDiff <= 0.0f) {
                    if (r0.amplitude < r0.animateToAmplitude) {
                        r0.amplitude = r0.animateToAmplitude;
                    }
                } else if (r0.amplitude > r0.animateToAmplitude) {
                    r0.amplitude = r0.animateToAmplitude;
                }
                invalidate();
            }
            r0.lastUpdateTime = System.currentTimeMillis();
            if (r0.amplitude != 0.0f) {
                canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) cy, (((float) AndroidUtilities.dp(42.0f)) + (((float) AndroidUtilities.dp(20.0f)) * r0.amplitude)) * r0.scale, ChatActivityEnterView.this.paintRecord);
            }
            canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) cy, ((float) AndroidUtilities.dp(42.0f)) * sc, ChatActivityEnterView.this.paint);
            if (isSendButtonVisible()) {
            }
            drawable.setBounds(cx - (drawable.getIntrinsicWidth() / 2), cy - (drawable.getIntrinsicHeight() / 2), (drawable.getIntrinsicWidth() / 2) + cx, (drawable.getIntrinsicHeight() / 2) + cy);
            drawable.setAlpha((int) (255.0f * alpha));
            drawable.draw(canvas2);
            moveProgress = 1.0f - (yAdd / ((float) AndroidUtilities.dp(57.0f)));
            moveProgress2 = Math.max(0.0f, 1.0f - ((yAdd / ((float) AndroidUtilities.dp(57.0f))) * 2.0f));
            intAlpha = (int) (NUM * alpha);
            if (isSendButtonVisible()) {
                float f3 = alpha;
                lockSize = AndroidUtilities.dp(31.0f) + ((int) (((float) AndroidUtilities.dp(29.0f)) * moveProgress));
                lockY = (AndroidUtilities.dp(57.0f) + ((int) (((float) AndroidUtilities.dp(30.0f)) * (1.0f - sc)))) - ((int) yAdd);
                lockTopY = (lockY + AndroidUtilities.dp(5.0f)) + ((int) (((float) AndroidUtilities.dp(4.0f)) * moveProgress));
                lockMiddleY = (lockY + AndroidUtilities.dp(11.0f)) + ((int) (((float) AndroidUtilities.dp(10.0f)) * moveProgress));
                cy = (lockY + AndroidUtilities.dp(25.0f)) + ((int) (((float) AndroidUtilities.dp(16.0f)) * moveProgress));
                ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockShadowDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockTopDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) intAlpha) * moveProgress2));
                lockArrowY = cy;
                cy = lockY;
            } else {
                lockSize = AndroidUtilities.dp(31.0f);
                lockY = AndroidUtilities.dp(57.0f) + ((int) (((((float) AndroidUtilities.dp(30.0f)) * (1.0f - sc)) - yAdd) + (((float) AndroidUtilities.dp(20.0f)) * moveProgress)));
                lockTopY = lockY + AndroidUtilities.dp(5.0f);
                lockMiddleY = lockY + AndroidUtilities.dp(11.0f);
                int lockArrowY22 = lockY + AndroidUtilities.dp(NUM);
                intAlpha = (int) (((float) intAlpha) * (yAdd / ((float) AndroidUtilities.dp(57.0f))));
                ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(255);
                ChatActivityEnterView.this.lockShadowDrawable.setAlpha(255);
                ChatActivityEnterView.this.lockTopDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) intAlpha) * moveProgress2));
                float f22 = yAdd;
                cy = lockY;
                lockArrowY = lockArrowY22;
            }
            ChatActivityEnterView.this.lockBackgroundDrawable.setBounds(cx - AndroidUtilities.dp(15.0f), cy, AndroidUtilities.dp(15.0f) + cx, cy + lockSize);
            ChatActivityEnterView.this.lockBackgroundDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockShadowDrawable.setBounds(cx - AndroidUtilities.dp(16.0f), cy - AndroidUtilities.dp(1.0f), AndroidUtilities.dp(16.0f) + cx, (cy + lockSize) + AndroidUtilities.dp(1.0f));
            ChatActivityEnterView.this.lockShadowDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockTopDrawable.setBounds(cx - AndroidUtilities.dp(6.0f), lockTopY, AndroidUtilities.dp(6.0f) + cx, AndroidUtilities.dp(14.0f) + lockTopY);
            ChatActivityEnterView.this.lockTopDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockDrawable.setBounds(cx - AndroidUtilities.dp(7.0f), lockMiddleY, AndroidUtilities.dp(7.0f) + cx, AndroidUtilities.dp(12.0f) + lockMiddleY);
            ChatActivityEnterView.this.lockDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockArrowDrawable.setBounds(cx - AndroidUtilities.dp(7.5f), lockArrowY, AndroidUtilities.dp(7.5f) + cx, AndroidUtilities.dp(9.0f) + lockArrowY);
            ChatActivityEnterView.this.lockArrowDrawable.draw(canvas2);
            if (isSendButtonVisible()) {
                ChatActivityEnterView.this.redDotPaint.setAlpha(255);
                ChatActivityEnterView.this.rect.set((float) (cx - AndroidUtilities.dp2(6.5f)), (float) (AndroidUtilities.dp(9.0f) + cy), (float) (AndroidUtilities.dp(6.5f) + cx), (float) (AndroidUtilities.dp(22.0f) + cy));
                canvas2.drawRoundRect(ChatActivityEnterView.this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ChatActivityEnterView.this.redDotPaint);
            }
        }
    }

    private class RecordDot extends View {
        private float alpha;
        private boolean isIncr;
        private long lastUpdateTime;

        public RecordDot(Context context) {
            super(context);
            ChatActivityEnterView.this.redDotPaint.setColor(Theme.getColor(Theme.key_chat_recordedVoiceDot));
        }

        public void resetAlpha() {
            this.alpha = 1.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            this.isIncr = false;
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            ChatActivityEnterView.this.redDotPaint.setAlpha((int) (255.0f * this.alpha));
            long dt = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.isIncr) {
                this.alpha += ((float) dt) / 400.0f;
                if (this.alpha >= 1.0f) {
                    this.alpha = 1.0f;
                    this.isIncr = false;
                }
            } else {
                this.alpha -= ((float) dt) / 400.0f;
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

        public ScrimDrawable() {
            this.paint.setColor(0);
        }

        public void draw(Canvas canvas) {
            this.paint.setAlpha(Math.round(102.0f * ChatActivityEnterView.this.stickersExpansionProgress));
            canvas.drawRect(0.0f, 0.0f, (float) ChatActivityEnterView.this.getWidth(), (ChatActivityEnterView.this.emojiView.getY() - ((float) ChatActivityEnterView.this.getHeight())) + ((float) Theme.chat_composeShadowDrawable.getIntrinsicHeight()), this.paint);
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
        }
    }

    private class SeekBarWaveformView extends View {
        public SeekBarWaveformView(Context context) {
            super(context);
            ChatActivityEnterView.this.seekBarWaveform = new SeekBarWaveform(context);
            ChatActivityEnterView.this.seekBarWaveform.setDelegate(new SeekBarDelegate(ChatActivityEnterView.this) {
                public void onSeekBarDrag(float progress) {
                    if (ChatActivityEnterView.this.audioToSendMessageObject != null) {
                        ChatActivityEnterView.this.audioToSendMessageObject.audioProgress = progress;
                        MediaController.getInstance().seekToProgress(ChatActivityEnterView.this.audioToSendMessageObject, progress);
                    }
                }
            });
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
            if (result) {
                return true;
            }
            if (super.onTouchEvent(event)) {
                return true;
            }
            return false;
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            ChatActivityEnterView.this.seekBarWaveform.setSize(right - left, bottom - top);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            ChatActivityEnterView.this.seekBarWaveform.setColors(Theme.getColor(Theme.key_chat_recordedVoiceProgress), Theme.getColor(Theme.key_chat_recordedVoiceProgressInner), Theme.getColor(Theme.key_chat_recordedVoiceProgress));
            ChatActivityEnterView.this.seekBarWaveform.draw(canvas);
        }
    }

    public ChatActivityEnterView(Activity context, SizeNotifierFrameLayout parent, ChatActivity fragment, boolean isChat) {
        Context context2 = context;
        super(context);
        this.dotPaint.setColor(Theme.getColor(Theme.key_chat_emojiPanelNewTrending));
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        this.parentActivity = context2;
        this.parentFragment = fragment;
        this.sizeNotifierLayout = parent;
        this.sizeNotifierLayout.setDelegate(this);
        this.sendByEnter = MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false);
        this.textFieldContainer = new LinearLayout(context2);
        this.textFieldContainer.setOrientation(0);
        addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 2.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        this.emojiButton = new ImageView(context2) {
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (ChatActivityEnterView.this.attachLayout == null) {
                    return;
                }
                if ((ChatActivityEnterView.this.emojiView == null || ChatActivityEnterView.this.emojiView.getVisibility() != 0) && !DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).getUnreadStickerSets().isEmpty() && ChatActivityEnterView.this.dotPaint != null) {
                    canvas.drawCircle((float) ((canvas.getWidth() / 2) + AndroidUtilities.dp(9.0f)), (float) ((canvas.getHeight() / 2) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.dotPaint);
                }
            }
        };
        this.emojiButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
        this.emojiButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.emojiButton.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
        setEmojiButtonImage();
        frameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48.0f, 83, 3.0f, 0.0f, 0.0f, 0.0f));
        this.emojiButton.setOnClickListener(new C11006());
        this.messageEditText = new EditTextCaption(context2) {

            /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$7$1 */
            class C20431 implements OnCommitContentListener {
                C20431() {
                }

                public boolean onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
                    C20431 c20431 = this;
                    if (BuildCompat.isAtLeastNMR1() && (flags & InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                        try {
                            inputContentInfo.requestPermission();
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    if (inputContentInfo.getDescription().hasMimeType("image/gif")) {
                        SendMessagesHelper.prepareSendingDocument(null, null, inputContentInfo.getContentUri(), "image/gif", ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, inputContentInfo);
                    } else {
                        SendMessagesHelper.prepareSendingPhoto(null, inputContentInfo.getContentUri(), ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, null, null, null, inputContentInfo, 0);
                    }
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onMessageSend(null);
                    }
                    return true;
                }
            }

            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                InputConnection ic = super.onCreateInputConnection(editorInfo);
                EditorInfoCompat.setContentMimeTypes(editorInfo, new String[]{"image/gif", "image/*", "image/jpg", "image/png"});
                return InputConnectionCompat.createWrapper(ic, editorInfo, new C20431());
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (ChatActivityEnterView.this.isPopupShowing() && event.getAction() == 0) {
                    if (ChatActivityEnterView.this.searchingStickers) {
                        ChatActivityEnterView.this.searchingStickers = false;
                        ChatActivityEnterView.this.emojiView.closeSearch(false);
                    }
                    ChatActivityEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 0);
                    ChatActivityEnterView.this.openKeyboardInternal();
                }
                try {
                    return super.onTouchEvent(event);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return false;
                }
            }
        };
        updateFieldHint();
        int flags = 268435456;
        if (!(this.parentFragment == null || r0.parentFragment.getCurrentEncryptedChat() == null)) {
            flags = 268435456 | 16777216;
        }
        r0.messageEditText.setImeOptions(flags);
        r0.messageEditText.setInputType((r0.messageEditText.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS) | 131072);
        r0.messageEditText.setSingleLine(false);
        r0.messageEditText.setMaxLines(4);
        r0.messageEditText.setTextSize(1, 18.0f);
        r0.messageEditText.setGravity(80);
        r0.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        r0.messageEditText.setBackgroundDrawable(null);
        r0.messageEditText.setTextColor(Theme.getColor(Theme.key_chat_messagePanelText));
        r0.messageEditText.setHintColor(Theme.getColor(Theme.key_chat_messagePanelHint));
        r0.messageEditText.setHintTextColor(Theme.getColor(Theme.key_chat_messagePanelHint));
        frameLayout.addView(r0.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 80, 52.0f, 0.0f, isChat ? 50.0f : 2.0f, 0.0f));
        r0.messageEditText.setOnKeyListener(new C11018());
        r0.messageEditText.setOnEditorActionListener(new C11029());
        r0.messageEditText.addTextChangedListener(new TextWatcher() {
            boolean processChange = false;

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (ChatActivityEnterView.this.innerTextChange != 1) {
                    ChatActivityEnterView.this.checkSendButton(true);
                    CharSequence message = AndroidUtilities.getTrimmedString(charSequence.toString());
                    if (!(ChatActivityEnterView.this.delegate == null || ChatActivityEnterView.this.ignoreTextChange)) {
                        boolean z;
                        if (count > 2 || charSequence == null || charSequence.length() == 0) {
                            ChatActivityEnterView.this.messageWebPageSearch = true;
                        }
                        ChatActivityEnterViewDelegate access$1300 = ChatActivityEnterView.this.delegate;
                        if (before <= count + 1) {
                            if (count - before <= 2) {
                                z = false;
                                access$1300.onTextChanged(charSequence, z);
                            }
                        }
                        z = true;
                        access$1300.onTextChanged(charSequence, z);
                    }
                    if (!(ChatActivityEnterView.this.innerTextChange == 2 || before == count || count - before <= 1)) {
                        this.processChange = true;
                    }
                    if (!(ChatActivityEnterView.this.editingMessageObject != null || ChatActivityEnterView.this.canWriteToChannel || message.length() == 0 || ChatActivityEnterView.this.lastTypingTimeSend >= System.currentTimeMillis() - DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS || ChatActivityEnterView.this.ignoreTextChange)) {
                        int currentTime = ConnectionsManager.getInstance(ChatActivityEnterView.this.currentAccount).getCurrentTime();
                        User currentUser = null;
                        if (((int) ChatActivityEnterView.this.dialog_id) > 0) {
                            currentUser = MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).getUser(Integer.valueOf((int) ChatActivityEnterView.this.dialog_id));
                        }
                        if (currentUser == null || (currentUser.id != UserConfig.getInstance(ChatActivityEnterView.this.currentAccount).getClientUserId() && (currentUser.status == null || currentUser.status.expires >= currentTime || MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(currentUser.id))))) {
                            ChatActivityEnterView.this.lastTypingTimeSend = System.currentTimeMillis();
                            if (ChatActivityEnterView.this.delegate != null) {
                                ChatActivityEnterView.this.delegate.needSendTyping();
                            }
                        }
                    }
                }
            }

            public void afterTextChanged(Editable editable) {
                if (ChatActivityEnterView.this.innerTextChange == 0) {
                    if (ChatActivityEnterView.this.sendByEnter && editable.length() > 0 && editable.charAt(editable.length() - 1) == '\n' && ChatActivityEnterView.this.editingMessageObject == null) {
                        ChatActivityEnterView.this.sendMessage();
                    }
                    if (this.processChange) {
                        ImageSpan[] spans = (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class);
                        for (Object removeSpan : spans) {
                            editable.removeSpan(removeSpan);
                        }
                        Emoji.replaceEmoji(editable, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        this.processChange = false;
                    }
                }
            }
        });
        if (isChat) {
            r0.attachLayout = new LinearLayout(context2);
            r0.attachLayout.setOrientation(0);
            r0.attachLayout.setEnabled(false);
            r0.attachLayout.setPivotX((float) AndroidUtilities.dp(48.0f));
            frameLayout.addView(r0.attachLayout, LayoutHelper.createFrame(-2, 48, 85));
            r0.botButton = new ImageView(context2);
            r0.botButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
            r0.botButton.setImageResource(R.drawable.bot_keyboard2);
            r0.botButton.setScaleType(ScaleType.CENTER);
            r0.botButton.setVisibility(8);
            r0.attachLayout.addView(r0.botButton, LayoutHelper.createLinear(48, 48));
            r0.botButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ChatActivityEnterView.this.searchingStickers) {
                        ChatActivityEnterView.this.searchingStickers = false;
                        ChatActivityEnterView.this.emojiView.closeSearch(false);
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                    }
                    if (ChatActivityEnterView.this.botReplyMarkup != null) {
                        Editor edit;
                        StringBuilder stringBuilder;
                        if (ChatActivityEnterView.this.isPopupShowing()) {
                            if (ChatActivityEnterView.this.currentPopupContentType == 1) {
                                if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                                    edit = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit();
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("hidekeyboard_");
                                    stringBuilder.append(ChatActivityEnterView.this.dialog_id);
                                    edit.putInt(stringBuilder.toString(), ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                                }
                                ChatActivityEnterView.this.openKeyboardInternal();
                            }
                        }
                        ChatActivityEnterView.this.showPopup(1, 1);
                        edit = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("hidekeyboard_");
                        stringBuilder.append(ChatActivityEnterView.this.dialog_id);
                        edit.remove(stringBuilder.toString()).commit();
                    } else if (ChatActivityEnterView.this.hasBotCommands) {
                        ChatActivityEnterView.this.setFieldText("/");
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                        ChatActivityEnterView.this.openKeyboard();
                    }
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        ChatActivityEnterView.this.setStickersExpanded(false, false);
                    }
                }
            });
            r0.notifyButton = new ImageView(context2);
            r0.notifyButton.setImageResource(r0.silent ? R.drawable.notify_members_off : R.drawable.notify_members_on);
            r0.notifyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
            r0.notifyButton.setScaleType(ScaleType.CENTER);
            r0.notifyButton.setVisibility(r0.canWriteToChannel ? 0 : 8);
            r0.attachLayout.addView(r0.notifyButton, LayoutHelper.createLinear(48, 48));
            r0.notifyButton.setOnClickListener(new OnClickListener() {
                private Toast visibleToast;

                public void onClick(View v) {
                    ChatActivityEnterView.this.silent = ChatActivityEnterView.this.silent ^ 1;
                    ChatActivityEnterView.this.notifyButton.setImageResource(ChatActivityEnterView.this.silent ? R.drawable.notify_members_off : R.drawable.notify_members_on);
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
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    if (ChatActivityEnterView.this.silent) {
                        this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOff", R.string.ChannelNotifyMembersInfoOff), 0);
                        this.visibleToast.show();
                    } else {
                        this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOn", R.string.ChannelNotifyMembersInfoOn), 0);
                        this.visibleToast.show();
                    }
                    ChatActivityEnterView.this.updateFieldHint();
                }
            });
            r0.attachButton = new ImageView(context2);
            r0.attachButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
            r0.attachButton.setImageResource(R.drawable.ic_ab_attach);
            r0.attachButton.setScaleType(ScaleType.CENTER);
            r0.attachLayout.addView(r0.attachButton, LayoutHelper.createLinear(48, 48));
            r0.attachButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ChatActivityEnterView.this.delegate.didPressedAttachButton();
                }
            });
        }
        r0.recordedAudioPanel = new FrameLayout(context2);
        r0.recordedAudioPanel.setVisibility(r0.audioToSend == null ? 8 : 0);
        r0.recordedAudioPanel.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        r0.recordedAudioPanel.setFocusable(true);
        r0.recordedAudioPanel.setFocusableInTouchMode(true);
        r0.recordedAudioPanel.setClickable(true);
        frameLayout.addView(r0.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        r0.recordDeleteImageView = new ImageView(context2);
        r0.recordDeleteImageView.setScaleType(ScaleType.CENTER);
        r0.recordDeleteImageView.setImageResource(R.drawable.ic_ab_delete);
        r0.recordDeleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceDelete), Mode.MULTIPLY));
        r0.recordedAudioPanel.addView(r0.recordDeleteImageView, LayoutHelper.createFrame(48, 48.0f));
        r0.recordDeleteImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ChatActivityEnterView.this.videoToSendMessageObject != null) {
                    ChatActivityEnterView.this.delegate.needStartRecordVideo(2);
                } else {
                    MessageObject playing = MediaController.getInstance().getPlayingMessageObject();
                    if (playing != null && playing == ChatActivityEnterView.this.audioToSendMessageObject) {
                        MediaController.getInstance().cleanupPlayer(true, true);
                    }
                }
                if (ChatActivityEnterView.this.audioToSendPath != null) {
                    new File(ChatActivityEnterView.this.audioToSendPath).delete();
                }
                ChatActivityEnterView.this.hideRecordedAudioPanel();
                ChatActivityEnterView.this.checkSendButton(true);
            }
        });
        r0.videoTimelineView = new VideoTimelineView(context2);
        r0.videoTimelineView.setColor(-11817481);
        r0.videoTimelineView.setRoundFrames(true);
        r0.videoTimelineView.setDelegate(new VideoTimelineViewDelegate() {
            public void onLeftProgressChanged(float progress) {
                if (ChatActivityEnterView.this.videoToSendMessageObject != null) {
                    ChatActivityEnterView.this.videoToSendMessageObject.startTime = (long) (((float) ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration) * progress);
                    ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, progress);
                }
            }

            public void onRightProgressChanged(float progress) {
                if (ChatActivityEnterView.this.videoToSendMessageObject != null) {
                    ChatActivityEnterView.this.videoToSendMessageObject.endTime = (long) (((float) ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration) * progress);
                    ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, progress);
                }
            }

            public void didStartDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(1, 0.0f);
            }

            public void didStopDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(0, 0.0f);
            }
        });
        r0.recordedAudioPanel.addView(r0.videoTimelineView, LayoutHelper.createFrame(-1, 32.0f, 19, 40.0f, 0.0f, 0.0f, 0.0f));
        r0.recordedAudioBackground = new View(context2);
        r0.recordedAudioBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(16.0f), Theme.getColor(Theme.key_chat_recordedVoiceBackground)));
        r0.recordedAudioPanel.addView(r0.recordedAudioBackground, LayoutHelper.createFrame(-1, 36.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        r0.recordedAudioSeekBar = new SeekBarWaveformView(context2);
        r0.recordedAudioPanel.addView(r0.recordedAudioSeekBar, LayoutHelper.createFrame(-1, 32.0f, 19, 92.0f, 0.0f, 52.0f, 0.0f));
        r0.playDrawable = Theme.createSimpleSelectorDrawable(context2, R.drawable.s_play, Theme.getColor(Theme.key_chat_recordedVoicePlayPause), Theme.getColor(Theme.key_chat_recordedVoicePlayPausePressed));
        r0.pauseDrawable = Theme.createSimpleSelectorDrawable(context2, R.drawable.s_pause, Theme.getColor(Theme.key_chat_recordedVoicePlayPause), Theme.getColor(Theme.key_chat_recordedVoicePlayPausePressed));
        r0.recordedAudioPlayButton = new ImageView(context2);
        r0.recordedAudioPlayButton.setImageDrawable(r0.playDrawable);
        r0.recordedAudioPlayButton.setScaleType(ScaleType.CENTER);
        r0.recordedAudioPanel.addView(r0.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0f, 83, 48.0f, 0.0f, 0.0f, 0.0f));
        r0.recordedAudioPlayButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ChatActivityEnterView.this.audioToSend != null) {
                    if (!MediaController.getInstance().isPlayingMessage(ChatActivityEnterView.this.audioToSendMessageObject) || MediaController.getInstance().isMessagePaused()) {
                        ChatActivityEnterView.this.recordedAudioPlayButton.setImageDrawable(ChatActivityEnterView.this.pauseDrawable);
                        MediaController.getInstance().playMessage(ChatActivityEnterView.this.audioToSendMessageObject);
                    } else {
                        MediaController.getInstance().pauseMessage(ChatActivityEnterView.this.audioToSendMessageObject);
                        ChatActivityEnterView.this.recordedAudioPlayButton.setImageDrawable(ChatActivityEnterView.this.playDrawable);
                    }
                }
            }
        });
        r0.recordedAudioTimeTextView = new TextView(context2);
        r0.recordedAudioTimeTextView.setTextColor(Theme.getColor(Theme.key_chat_messagePanelVoiceDuration));
        r0.recordedAudioTimeTextView.setTextSize(1, 13.0f);
        r0.recordedAudioPanel.addView(r0.recordedAudioTimeTextView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 13.0f, 0.0f));
        r0.recordPanel = new FrameLayout(context2);
        r0.recordPanel.setVisibility(8);
        r0.recordPanel.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        frameLayout.addView(r0.recordPanel, LayoutHelper.createFrame(-1, 48, 80));
        r0.recordPanel.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        r0.slideText = new LinearLayout(context2);
        r0.slideText.setOrientation(0);
        r0.recordPanel.addView(r0.slideText, LayoutHelper.createFrame(-2, -2.0f, 17, 30.0f, 0.0f, 0.0f, 0.0f));
        r0.recordCancelImage = new ImageView(context2);
        r0.recordCancelImage.setImageResource(R.drawable.slidearrow);
        r0.recordCancelImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_recordVoiceCancel), Mode.MULTIPLY));
        r0.slideText.addView(r0.recordCancelImage, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 0, 0));
        r0.recordCancelText = new TextView(context2);
        r0.recordCancelText.setText(LocaleController.getString("SlideToCancel", R.string.SlideToCancel));
        r0.recordCancelText.setTextColor(Theme.getColor(Theme.key_chat_recordVoiceCancel));
        r0.recordCancelText.setTextSize(1, 12.0f);
        r0.slideText.addView(r0.recordCancelText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        r0.recordSendText = new TextView(context2);
        r0.recordSendText.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        r0.recordSendText.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        r0.recordSendText.setTextSize(1, 16.0f);
        r0.recordSendText.setGravity(17);
        r0.recordSendText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.recordSendText.setAlpha(0.0f);
        r0.recordSendText.setPadding(AndroidUtilities.dp(36.0f), 0, 0, 0);
        r0.recordSendText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!ChatActivityEnterView.this.hasRecordVideo || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                    ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                    MediaController.getInstance().stopRecording(0);
                } else {
                    ChatActivityEnterView.this.delegate.needStartRecordVideo(2);
                }
                ChatActivityEnterView.this.recordingAudioVideo = false;
                ChatActivityEnterView.this.updateRecordIntefrace();
            }
        });
        r0.recordPanel.addView(r0.recordSendText, LayoutHelper.createFrame(-2, -1.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
        r0.recordTimeContainer = new LinearLayout(context2);
        r0.recordTimeContainer.setOrientation(0);
        r0.recordTimeContainer.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        r0.recordTimeContainer.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        r0.recordPanel.addView(r0.recordTimeContainer, LayoutHelper.createFrame(-2, -2, 16));
        r0.recordDot = new RecordDot(context2);
        r0.recordTimeContainer.addView(r0.recordDot, LayoutHelper.createLinear(11, 11, 16, 0, 1, 0, 0));
        r0.recordTimeText = new TextView(context2);
        r0.recordTimeText.setTextColor(Theme.getColor(Theme.key_chat_recordTime));
        r0.recordTimeText.setTextSize(1, 16.0f);
        r0.recordTimeContainer.addView(r0.recordTimeText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        r0.sendButtonContainer = new FrameLayout(context2);
        r0.textFieldContainer.addView(r0.sendButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        r0.audioVideoButtonContainer = new FrameLayout(context2);
        r0.audioVideoButtonContainer.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        r0.audioVideoButtonContainer.setSoundEffectsEnabled(false);
        r0.sendButtonContainer.addView(r0.audioVideoButtonContainer, LayoutHelper.createFrame(48, 48.0f));
        r0.audioVideoButtonContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                AnonymousClass19 anonymousClass19 = this;
                boolean z = false;
                if (motionEvent.getAction() != 0) {
                    if (motionEvent.getAction() != 1) {
                        if (motionEvent.getAction() != 3) {
                            if (motionEvent.getAction() == 2 && ChatActivityEnterView.this.recordingAudioVideo) {
                                float x = motionEvent.getX();
                                float y = motionEvent.getY();
                                if (ChatActivityEnterView.this.recordCircle.isSendButtonVisible()) {
                                    return false;
                                }
                                if (ChatActivityEnterView.this.recordCircle.setLockTranslation(y) == 2) {
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    r10 = new Animator[5];
                                    r10[0] = ObjectAnimator.ofFloat(ChatActivityEnterView.this.recordCircle, "lockAnimatedTranslation", new float[]{ChatActivityEnterView.this.recordCircle.startTranslation});
                                    r10[1] = ObjectAnimator.ofFloat(ChatActivityEnterView.this.slideText, "alpha", new float[]{0.0f});
                                    r10[2] = ObjectAnimator.ofFloat(ChatActivityEnterView.this.slideText, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f)});
                                    r10[3] = ObjectAnimator.ofFloat(ChatActivityEnterView.this.recordSendText, "alpha", new float[]{1.0f});
                                    r10[4] = ObjectAnimator.ofFloat(ChatActivityEnterView.this.recordSendText, "translationY", new float[]{(float) (-AndroidUtilities.dp(20.0f)), 0.0f});
                                    animatorSet.playTogether(r10);
                                    animatorSet.setInterpolator(new DecelerateInterpolator());
                                    animatorSet.setDuration(150);
                                    animatorSet.start();
                                    return false;
                                }
                                if (x < (-ChatActivityEnterView.this.distCanMove)) {
                                    if (!ChatActivityEnterView.this.hasRecordVideo || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                                        ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                                        MediaController.getInstance().stopRecording(0);
                                    } else {
                                        ChatActivityEnterView.this.delegate.needStartRecordVideo(2);
                                    }
                                    ChatActivityEnterView.this.recordingAudioVideo = false;
                                    ChatActivityEnterView.this.updateRecordIntefrace();
                                }
                                x += ChatActivityEnterView.this.audioVideoButtonContainer.getX();
                                LayoutParams params = (LayoutParams) ChatActivityEnterView.this.slideText.getLayoutParams();
                                if (ChatActivityEnterView.this.startedDraggingX != -1.0f) {
                                    float dist = x - ChatActivityEnterView.this.startedDraggingX;
                                    params.leftMargin = AndroidUtilities.dp(30.0f) + ((int) dist);
                                    ChatActivityEnterView.this.slideText.setLayoutParams(params);
                                    float alpha = (dist / ChatActivityEnterView.this.distCanMove) + 1.0f;
                                    if (alpha > 1.0f) {
                                        alpha = 1.0f;
                                    } else if (alpha < 0.0f) {
                                        alpha = 0.0f;
                                    }
                                    ChatActivityEnterView.this.slideText.setAlpha(alpha);
                                }
                                if (x <= (ChatActivityEnterView.this.slideText.getX() + ((float) ChatActivityEnterView.this.slideText.getWidth())) + ((float) AndroidUtilities.dp(30.0f)) && ChatActivityEnterView.this.startedDraggingX == -1.0f) {
                                    ChatActivityEnterView.this.startedDraggingX = x;
                                    ChatActivityEnterView.this.distCanMove = ((float) ((ChatActivityEnterView.this.recordPanel.getMeasuredWidth() - ChatActivityEnterView.this.slideText.getMeasuredWidth()) - AndroidUtilities.dp(48.0f))) / 2.0f;
                                    if (ChatActivityEnterView.this.distCanMove <= 0.0f) {
                                        ChatActivityEnterView.this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                                    } else if (ChatActivityEnterView.this.distCanMove > ((float) AndroidUtilities.dp(80.0f))) {
                                        ChatActivityEnterView.this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                                    }
                                }
                                if (params.leftMargin > AndroidUtilities.dp(30.0f)) {
                                    params.leftMargin = AndroidUtilities.dp(30.0f);
                                    ChatActivityEnterView.this.slideText.setLayoutParams(params);
                                    ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                                    ChatActivityEnterView.this.startedDraggingX = -1.0f;
                                }
                            }
                        }
                    }
                    if (!ChatActivityEnterView.this.recordCircle.isSendButtonVisible()) {
                        if (ChatActivityEnterView.this.recordedAudioPanel.getVisibility() != 0) {
                            if (ChatActivityEnterView.this.recordAudioVideoRunnableStarted) {
                                AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.recordAudioVideoRunnable);
                                ChatActivityEnterView.this.delegate.onSwitchRecordMode(ChatActivityEnterView.this.videoSendButton.getTag() == null);
                                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                                if (ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                                    z = true;
                                }
                                chatActivityEnterView.setRecordVideoButtonVisible(z, true);
                            } else if (!ChatActivityEnterView.this.hasRecordVideo || ChatActivityEnterView.this.calledRecordRunnable) {
                                ChatActivityEnterView.this.startedDraggingX = -1.0f;
                                if (!ChatActivityEnterView.this.hasRecordVideo || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                                    ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                                    MediaController.getInstance().stopRecording(1);
                                } else {
                                    ChatActivityEnterView.this.delegate.needStartRecordVideo(1);
                                }
                                ChatActivityEnterView.this.recordingAudioVideo = false;
                                ChatActivityEnterView.this.updateRecordIntefrace();
                            }
                        }
                    }
                    return false;
                } else if (ChatActivityEnterView.this.recordCircle.isSendButtonVisible()) {
                    if (!ChatActivityEnterView.this.hasRecordVideo || ChatActivityEnterView.this.calledRecordRunnable) {
                        ChatActivityEnterView.this.startedDraggingX = -1.0f;
                        if (!ChatActivityEnterView.this.hasRecordVideo || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                            ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                            MediaController.getInstance().stopRecording(1);
                        } else {
                            ChatActivityEnterView.this.delegate.needStartRecordVideo(1);
                        }
                        ChatActivityEnterView.this.recordingAudioVideo = false;
                        ChatActivityEnterView.this.updateRecordIntefrace();
                    }
                    return false;
                } else {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        Chat chat = ChatActivityEnterView.this.parentFragment.getCurrentChat();
                        if (ChatObject.isChannel(chat) && chat.banned_rights != null && chat.banned_rights.send_media) {
                            ChatActivityEnterView.this.delegate.needShowMediaBanHint();
                            return false;
                        }
                    }
                    if (ChatActivityEnterView.this.hasRecordVideo) {
                        ChatActivityEnterView.this.calledRecordRunnable = false;
                        ChatActivityEnterView.this.recordAudioVideoRunnableStarted = true;
                        AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.recordAudioVideoRunnable, 150);
                    } else {
                        ChatActivityEnterView.this.recordAudioVideoRunnable.run();
                    }
                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });
        r0.audioSendButton = new ImageView(context2);
        r0.audioSendButton.setScaleType(ScaleType.CENTER_INSIDE);
        r0.audioSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
        r0.audioSendButton.setImageResource(R.drawable.mic);
        r0.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        r0.audioVideoButtonContainer.addView(r0.audioSendButton, LayoutHelper.createFrame(48, 48.0f));
        if (isChat) {
            r0.videoSendButton = new ImageView(context2);
            r0.videoSendButton.setScaleType(ScaleType.CENTER_INSIDE);
            r0.videoSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
            r0.videoSendButton.setImageResource(R.drawable.ic_msg_panel_video);
            r0.videoSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
            r0.audioVideoButtonContainer.addView(r0.videoSendButton, LayoutHelper.createFrame(48, 48.0f));
        }
        r0.recordCircle = new RecordCircle(context2);
        r0.recordCircle.setVisibility(8);
        r0.sizeNotifierLayout.addView(r0.recordCircle, LayoutHelper.createFrame(124, 194.0f, 85, 0.0f, 0.0f, -36.0f, 0.0f));
        r0.cancelBotButton = new ImageView(context2);
        r0.cancelBotButton.setVisibility(4);
        r0.cancelBotButton.setScaleType(ScaleType.CENTER_INSIDE);
        ImageView imageView = r0.cancelBotButton;
        Drawable closeProgressDrawable2 = new CloseProgressDrawable2();
        r0.progressDrawable = closeProgressDrawable2;
        imageView.setImageDrawable(closeProgressDrawable2);
        r0.progressDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelCancelInlineBot), Mode.MULTIPLY));
        r0.cancelBotButton.setSoundEffectsEnabled(false);
        r0.cancelBotButton.setScaleX(0.1f);
        r0.cancelBotButton.setScaleY(0.1f);
        r0.cancelBotButton.setAlpha(0.0f);
        r0.sendButtonContainer.addView(r0.cancelBotButton, LayoutHelper.createFrame(48, 48.0f));
        r0.cancelBotButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String text = ChatActivityEnterView.this.messageEditText.getText().toString();
                int idx = text.indexOf(32);
                if (idx != -1) {
                    if (idx != text.length() - 1) {
                        ChatActivityEnterView.this.setFieldText(text.substring(0, idx + 1));
                        return;
                    }
                }
                ChatActivityEnterView.this.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
            }
        });
        r0.sendButton = new ImageView(context2);
        r0.sendButton.setVisibility(4);
        r0.sendButton.setScaleType(ScaleType.CENTER_INSIDE);
        r0.sendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelSend), Mode.MULTIPLY));
        r0.sendButton.setImageResource(R.drawable.ic_send);
        r0.sendButton.setSoundEffectsEnabled(false);
        r0.sendButton.setScaleX(0.1f);
        r0.sendButton.setScaleY(0.1f);
        r0.sendButton.setAlpha(0.0f);
        r0.sendButtonContainer.addView(r0.sendButton, LayoutHelper.createFrame(48, 48.0f));
        r0.sendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ChatActivityEnterView.this.sendMessage();
            }
        });
        r0.expandStickersButton = new ImageView(context2);
        r0.expandStickersButton.setScaleType(ScaleType.CENTER);
        imageView = r0.expandStickersButton;
        Drawable animatedArrowDrawable = new AnimatedArrowDrawable(Theme.getColor(Theme.key_chat_messagePanelIcons));
        r0.stickersArrow = animatedArrowDrawable;
        imageView.setImageDrawable(animatedArrowDrawable);
        r0.expandStickersButton.setVisibility(8);
        r0.expandStickersButton.setScaleX(0.1f);
        r0.expandStickersButton.setScaleY(0.1f);
        r0.expandStickersButton.setAlpha(0.0f);
        r0.sendButtonContainer.addView(r0.expandStickersButton, LayoutHelper.createFrame(48, 48.0f));
        r0.expandStickersButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ChatActivityEnterView.this.expandStickersButton.getVisibility() == 0) {
                    if (ChatActivityEnterView.this.expandStickersButton.getAlpha() == 1.0f) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingStickers) {
                                ChatActivityEnterView.this.searchingStickers = false;
                                ChatActivityEnterView.this.emojiView.closeSearch(true);
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            } else if (!(ChatActivityEnterView.this.stickersDragging || ChatActivityEnterView.this.emojiView == null)) {
                                ChatActivityEnterView.this.emojiView.showSearchField(false);
                            }
                        } else if (!ChatActivityEnterView.this.stickersDragging) {
                            ChatActivityEnterView.this.emojiView.showSearchField(true);
                        }
                        if (!ChatActivityEnterView.this.stickersDragging) {
                            ChatActivityEnterView.this.setStickersExpanded(ChatActivityEnterView.this.stickersExpanded ^ true, true);
                        }
                    }
                }
            }
        });
        r0.doneButtonContainer = new FrameLayout(context2);
        r0.doneButtonContainer.setVisibility(8);
        r0.textFieldContainer.addView(r0.doneButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        r0.doneButtonContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ChatActivityEnterView.this.doneEditingMessage();
            }
        });
        r0.doneButtonImage = new ImageView(context2);
        r0.doneButtonImage.setScaleType(ScaleType.CENTER);
        r0.doneButtonImage.setImageResource(R.drawable.edit_done);
        r0.doneButtonImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_editDoneIcon), Mode.MULTIPLY));
        r0.doneButtonContainer.addView(r0.doneButtonImage, LayoutHelper.createFrame(48, 48.0f));
        r0.doneButtonProgress = new ContextProgressView(context2, 0);
        r0.doneButtonProgress.setVisibility(4);
        r0.doneButtonContainer.addView(r0.doneButtonProgress, LayoutHelper.createFrame(-1, -1.0f));
        SharedPreferences sharedPreferences = MessagesController.getGlobalEmojiSettings();
        r0.keyboardHeight = sharedPreferences.getInt("kbd_height", AndroidUtilities.dp(200.0f));
        r0.keyboardHeightLand = sharedPreferences.getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
        setRecordVideoButtonVisible(false, false);
        checkSendButton(false);
        checkChannelRights();
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == this.topView) {
            canvas.save();
            canvas.clipRect(0, 0, getMeasuredWidth(), child.getLayoutParams().height + AndroidUtilities.dp(2.0f));
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.topView) {
            canvas.restore();
        }
        return result;
    }

    protected void onDraw(Canvas canvas) {
        int top = (this.topView == null || this.topView.getVisibility() != 0) ? 0 : (int) this.topView.getTranslationY();
        int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + top;
        Theme.chat_composeShadowDrawable.setBounds(0, top, getMeasuredWidth(), bottom);
        Theme.chat_composeShadowDrawable.draw(canvas);
        canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean isSendButtonVisible() {
        return this.sendButton.getVisibility() == 0;
    }

    private void setRecordVideoButtonVisible(boolean visible, boolean animated) {
        boolean z = visible;
        if (this.videoSendButton != null) {
            r0.videoSendButton.setTag(z ? Integer.valueOf(1) : null);
            if (r0.audioVideoButtonAnimation != null) {
                r0.audioVideoButtonAnimation.cancel();
                r0.audioVideoButtonAnimation = null;
            }
            float f = 0.0f;
            float f2 = 0.1f;
            if (animated) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                boolean isChannel = false;
                if (((int) r0.dialog_id) < 0) {
                    Chat chat = MessagesController.getInstance(r0.currentAccount).getChat(Integer.valueOf(-((int) r0.dialog_id)));
                    boolean z2 = ChatObject.isChannel(chat) && !chat.megagroup;
                    isChannel = z2;
                }
                preferences.edit().putBoolean(isChannel ? "currentModeVideoChannel" : "currentModeVideo", z).commit();
                r0.audioVideoButtonAnimation = new AnimatorSet();
                AnimatorSet animatorSet = r0.audioVideoButtonAnimation;
                Animator[] animatorArr = new Animator[6];
                ImageView imageView = r0.videoSendButton;
                String str = "scaleX";
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.1f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView, str, fArr);
                imageView = r0.videoSendButton;
                str = "scaleY";
                fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView, str, fArr);
                ImageView imageView2 = r0.videoSendButton;
                String str2 = "alpha";
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 1.0f : 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView2, str2, fArr2);
                imageView2 = r0.audioSendButton;
                str2 = "scaleX";
                fArr2 = new float[1];
                fArr2[0] = z ? 0.1f : 1.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(imageView2, str2, fArr2);
                imageView2 = r0.audioSendButton;
                str2 = "scaleY";
                fArr2 = new float[1];
                if (!z) {
                    f2 = 1.0f;
                }
                fArr2[0] = f2;
                animatorArr[4] = ObjectAnimator.ofFloat(imageView2, str2, fArr2);
                imageView = r0.audioSendButton;
                str = "alpha";
                float[] fArr3 = new float[1];
                if (!z) {
                    f = 1.0f;
                }
                fArr3[0] = f;
                animatorArr[5] = ObjectAnimator.ofFloat(imageView, str, fArr3);
                animatorSet.playTogether(animatorArr);
                r0.audioVideoButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(ChatActivityEnterView.this.audioVideoButtonAnimation)) {
                            ChatActivityEnterView.this.audioVideoButtonAnimation = null;
                        }
                    }
                });
                r0.audioVideoButtonAnimation.setInterpolator(new DecelerateInterpolator());
                r0.audioVideoButtonAnimation.setDuration(150);
                r0.audioVideoButtonAnimation.start();
            } else {
                r0.videoSendButton.setScaleX(z ? 1.0f : 0.1f);
                r0.videoSendButton.setScaleY(z ? 1.0f : 0.1f);
                r0.videoSendButton.setAlpha(z ? 1.0f : 0.0f);
                r0.audioSendButton.setScaleX(z ? 0.1f : 1.0f);
                ImageView imageView3 = r0.audioSendButton;
                if (!z) {
                    f2 = 1.0f;
                }
                imageView3.setScaleY(f2);
                ImageView imageView4 = r0.audioSendButton;
                if (!z) {
                    f = 1.0f;
                }
                imageView4.setAlpha(f);
            }
        }
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
            this.delegate.needStartRecordVideo(2);
        }
        this.recordingAudioVideo = false;
        updateRecordIntefrace();
    }

    public void showContextProgress(boolean show) {
        if (this.progressDrawable != null) {
            if (show) {
                this.progressDrawable.startAnimation();
            } else {
                this.progressDrawable.stopAnimation();
            }
        }
    }

    public void setCaption(String caption) {
        if (this.messageEditText != null) {
            this.messageEditText.setCaption(caption);
            checkSendButton(true);
        }
    }

    public void addTopView(View view, int height) {
        if (view != null) {
            this.topView = view;
            this.topView.setVisibility(8);
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
        if (!((this.allowStickers == value && this.allowGifs == value2) || this.emojiView == null)) {
            if (this.emojiView.getVisibility() == 0) {
                hidePopup(false);
            }
            this.sizeNotifierLayout.removeView(this.emojiView);
            this.emojiView = null;
        }
        this.allowStickers = value;
        this.allowGifs = value2;
        setEmojiButtonImage();
    }

    public void addEmojiToRecent(String code) {
        createEmojiView();
        this.emojiView.addEmojiToRecent(code);
    }

    public void setOpenGifsTabFirst() {
        createEmojiView();
        DataQuery.getInstance(this.currentAccount).loadRecents(0, true, true, false);
        this.emojiView.switchToGifRecent();
    }

    public void showTopView(boolean animated, final boolean openKeyboard) {
        if (!(this.topView == null || this.topViewShowed)) {
            if (getVisibility() == 0) {
                this.needShowTopView = true;
                this.topViewShowed = true;
                if (this.allowShowTopView) {
                    this.topView.setVisibility(0);
                    if (this.currentTopViewAnimation != null) {
                        this.currentTopViewAnimation.cancel();
                        this.currentTopViewAnimation = null;
                    }
                    resizeForTopView(true);
                    if (animated) {
                        if (!this.keyboardVisible) {
                            if (!isPopupShowing()) {
                                this.topView.setTranslationY(0.0f);
                                if (this.recordedAudioPanel.getVisibility() != 0 && (!this.forceShowSendButton || openKeyboard)) {
                                    openKeyboard();
                                }
                            }
                        }
                        this.currentTopViewAnimation = new AnimatorSet();
                        AnimatorSet animatorSet = this.currentTopViewAnimation;
                        Animator[] animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.topView, "translationY", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                        this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                    if (ChatActivityEnterView.this.recordedAudioPanel.getVisibility() != 0 && (!ChatActivityEnterView.this.forceShowSendButton || openKeyboard)) {
                                        ChatActivityEnterView.this.openKeyboard();
                                    }
                                    ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                    ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                            }
                        });
                        this.currentTopViewAnimation.setDuration(200);
                        this.currentTopViewAnimation.start();
                    } else {
                        this.topView.setTranslationY(0.0f);
                        if (this.recordedAudioPanel.getVisibility() != 0 && (!this.forceShowSendButton || openKeyboard)) {
                            openKeyboard();
                        }
                    }
                }
                return;
            }
        }
        if (this.recordedAudioPanel.getVisibility() != 0 && (!this.forceShowSendButton || openKeyboard)) {
            openKeyboard();
        }
    }

    public void onEditTimeExpired() {
        this.doneButtonContainer.setVisibility(8);
    }

    public void showEditDoneProgress(boolean show, boolean animated) {
        final boolean z = show;
        if (this.doneButtonAnimation != null) {
            r0.doneButtonAnimation.cancel();
        }
        if (animated) {
            r0.doneButtonAnimation = new AnimatorSet();
            Animator[] animatorArr;
            if (z) {
                r0.doneButtonProgress.setVisibility(0);
                r0.doneButtonContainer.setEnabled(false);
                AnimatorSet animatorSet = r0.doneButtonAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(r0.doneButtonImage, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(r0.doneButtonImage, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(r0.doneButtonImage, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(r0.doneButtonProgress, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(r0.doneButtonProgress, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(r0.doneButtonProgress, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                r0.doneButtonImage.setVisibility(0);
                r0.doneButtonContainer.setEnabled(true);
                AnimatorSet animatorSet2 = r0.doneButtonAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(r0.doneButtonProgress, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(r0.doneButtonProgress, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(r0.doneButtonProgress, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(r0.doneButtonImage, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(r0.doneButtonImage, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(r0.doneButtonImage, "alpha", new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            }
            r0.doneButtonAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animation)) {
                        if (z) {
                            ChatActivityEnterView.this.doneButtonImage.setVisibility(4);
                        } else {
                            ChatActivityEnterView.this.doneButtonProgress.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animation)) {
                        ChatActivityEnterView.this.doneButtonAnimation = null;
                    }
                }
            });
            r0.doneButtonAnimation.setDuration(150);
            r0.doneButtonAnimation.start();
        } else if (z) {
            r0.doneButtonImage.setScaleX(0.1f);
            r0.doneButtonImage.setScaleY(0.1f);
            r0.doneButtonImage.setAlpha(0.0f);
            r0.doneButtonProgress.setScaleX(1.0f);
            r0.doneButtonProgress.setScaleY(1.0f);
            r0.doneButtonProgress.setAlpha(1.0f);
            r0.doneButtonImage.setVisibility(4);
            r0.doneButtonProgress.setVisibility(0);
            r0.doneButtonContainer.setEnabled(false);
        } else {
            r0.doneButtonProgress.setScaleX(0.1f);
            r0.doneButtonProgress.setScaleY(0.1f);
            r0.doneButtonProgress.setAlpha(0.0f);
            r0.doneButtonImage.setScaleX(1.0f);
            r0.doneButtonImage.setScaleY(1.0f);
            r0.doneButtonImage.setAlpha(1.0f);
            r0.doneButtonImage.setVisibility(0);
            r0.doneButtonProgress.setVisibility(4);
            r0.doneButtonContainer.setEnabled(true);
        }
    }

    public void hideTopView(boolean animated) {
        if (this.topView != null) {
            if (this.topViewShowed) {
                this.topViewShowed = false;
                this.needShowTopView = false;
                if (this.allowShowTopView) {
                    if (this.currentTopViewAnimation != null) {
                        this.currentTopViewAnimation.cancel();
                        this.currentTopViewAnimation = null;
                    }
                    if (animated) {
                        this.currentTopViewAnimation = new AnimatorSet();
                        AnimatorSet animatorSet = this.currentTopViewAnimation;
                        Animator[] animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.topView, "translationY", new float[]{(float) this.topView.getLayoutParams().height});
                        animatorSet.playTogether(animatorArr);
                        this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                    ChatActivityEnterView.this.topView.setVisibility(8);
                                    ChatActivityEnterView.this.resizeForTopView(false);
                                    ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                    ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                            }
                        });
                        this.currentTopViewAnimation.setDuration(200);
                        this.currentTopViewAnimation.start();
                    } else {
                        this.topView.setVisibility(8);
                        resizeForTopView(false);
                        this.topView.setTranslationY((float) this.topView.getLayoutParams().height);
                    }
                }
            }
        }
    }

    public boolean isTopViewVisible() {
        return this.topView != null && this.topView.getVisibility() == 0;
    }

    private void onWindowSizeChanged() {
        int size = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            size -= this.emojiPadding;
        }
        if (this.delegate != null) {
            this.delegate.onWindowSizeChanged(size);
        }
        if (this.topView == null) {
            return;
        }
        if (size < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
            if (this.allowShowTopView) {
                this.allowShowTopView = false;
                if (this.needShowTopView) {
                    this.topView.setVisibility(8);
                    resizeForTopView(false);
                    this.topView.setTranslationY((float) this.topView.getLayoutParams().height);
                }
            }
        } else if (!this.allowShowTopView) {
            this.allowShowTopView = true;
            if (this.needShowTopView) {
                this.topView.setVisibility(0);
                resizeForTopView(true);
                this.topView.setTranslationY(0.0f);
            }
        }
    }

    private void resizeForTopView(boolean show) {
        LayoutParams layoutParams = (LayoutParams) this.textFieldContainer.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(2.0f) + (show ? this.topView.getLayoutParams().height : 0);
        this.textFieldContainer.setLayoutParams(layoutParams);
        if (this.stickersExpanded) {
            setStickersExpanded(false, true);
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        if (this.emojiView != null) {
            this.emojiView.onDestroy();
        }
        if (this.wakeLock != null) {
            try {
                this.wakeLock.release();
                this.wakeLock = null;
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        if (this.sizeNotifierLayout != null) {
            this.sizeNotifierLayout.setDelegate(null);
        }
    }

    public void checkChannelRights() {
        if (this.parentFragment != null) {
            Chat chat = this.parentFragment.getCurrentChat();
            if (ChatObject.isChannel(chat)) {
                float f;
                EmojiView emojiView;
                boolean z;
                FrameLayout frameLayout = this.audioVideoButtonContainer;
                if (chat.banned_rights != null) {
                    if (chat.banned_rights.send_media) {
                        f = 0.5f;
                        frameLayout.setAlpha(f);
                        if (this.emojiView != null) {
                            emojiView = this.emojiView;
                            z = chat.banned_rights == null && chat.banned_rights.send_stickers;
                            emojiView.setStickersBanned(z, chat.id);
                        }
                    }
                }
                f = 1.0f;
                frameLayout.setAlpha(f);
                if (this.emojiView != null) {
                    emojiView = this.emojiView;
                    if (chat.banned_rights == null) {
                    }
                    emojiView.setStickersBanned(z, chat.id);
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
            if (!this.searchingStickers) {
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

    public void setDialogId(long id, int account) {
        this.dialog_id = id;
        if (this.currentAccount != account) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStarted);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStartError);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStopped);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioDidSent);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRouteChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
            this.currentAccount = account;
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStarted);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStartError);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStopped);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioDidSent);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioRouteChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        }
        int lower_id = (int) this.dialog_id;
        int high_id = (int) (this.dialog_id >> 32);
        if (((int) this.dialog_id) < 0) {
            Chat currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("silent_");
            stringBuilder.append(this.dialog_id);
            this.silent = notificationsSettings.getBoolean(stringBuilder.toString(), false);
            int i = 1;
            boolean z = ChatObject.isChannel(currentChat) && ((currentChat.creator || (currentChat.admin_rights != null && currentChat.admin_rights.post_messages)) && !currentChat.megagroup);
            this.canWriteToChannel = z;
            if (this.notifyButton != null) {
                float f;
                this.notifyButton.setVisibility(this.canWriteToChannel ? 0 : 8);
                this.notifyButton.setImageResource(this.silent ? R.drawable.notify_members_off : R.drawable.notify_members_on);
                LinearLayout linearLayout = this.attachLayout;
                if (this.botButton == null || this.botButton.getVisibility() == 8) {
                    if (this.notifyButton != null) {
                        if (this.notifyButton.getVisibility() == 8) {
                        }
                    }
                    f = 48.0f;
                    linearLayout.setPivotX((float) AndroidUtilities.dp(f));
                }
                f = 96.0f;
                linearLayout.setPivotX((float) AndroidUtilities.dp(f));
            }
            if (this.attachLayout != null) {
                if (this.attachLayout.getVisibility() != 0) {
                    i = 0;
                }
                updateFieldRight(i);
            }
        }
        checkRoundVideo();
        updateFieldHint();
    }

    public void setChatInfo(ChatFull chatInfo) {
        this.info = chatInfo;
        if (this.emojiView != null) {
            this.emojiView.setChatInfo(this.info);
        }
    }

    public void checkRoundVideo() {
        if (!this.hasRecordVideo) {
            if (this.attachLayout != null) {
                if (VERSION.SDK_INT >= 18) {
                    int high_id = (int) (this.dialog_id >> 32);
                    boolean z = true;
                    if (((int) this.dialog_id) != 0 || high_id == 0) {
                        this.hasRecordVideo = true;
                    } else if (AndroidUtilities.getPeerLayerVersion(MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id)).layer) >= 66) {
                        this.hasRecordVideo = true;
                    }
                    boolean isChannel = false;
                    if (((int) this.dialog_id) < 0) {
                        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
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
                        CameraController.getInstance().initCamera();
                        setRecordVideoButtonVisible(MessagesController.getGlobalMainSettings().getBoolean(isChannel ? "currentModeVideoChannel" : "currentModeVideo", isChannel), false);
                    } else {
                        setRecordVideoButtonVisible(false, false);
                    }
                    return;
                }
            }
            this.hasRecordVideo = false;
            setRecordVideoButtonVisible(false, false);
        }
    }

    public boolean isInVideoMode() {
        return this.videoSendButton.getTag() != null;
    }

    public boolean hasRecordVideo() {
        return this.hasRecordVideo;
    }

    private void updateFieldHint() {
        boolean isChannel = false;
        if (((int) this.dialog_id) < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
            boolean z = ChatObject.isChannel(chat) && !chat.megagroup;
            isChannel = z;
        }
        if (!isChannel) {
            this.messageEditText.setHintText(LocaleController.getString("TypeMessage", R.string.TypeMessage));
        } else if (this.editingMessageObject != null) {
            this.messageEditText.setHintText(this.editingCaption ? LocaleController.getString("Caption", R.string.Caption) : LocaleController.getString("TypeMessage", R.string.TypeMessage));
        } else if (this.silent) {
            this.messageEditText.setHintText(LocaleController.getString("ChannelSilentBroadcast", R.string.ChannelSilentBroadcast));
        } else {
            this.messageEditText.setHintText(LocaleController.getString("ChannelBroadcast", R.string.ChannelBroadcast));
        }
    }

    public void setReplyingMessageObject(MessageObject messageObject) {
        if (messageObject != null) {
            if (this.botMessageObject == null && this.botButtonsMessageObject != this.replyingMessageObject) {
                this.botMessageObject = this.botButtonsMessageObject;
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

    public void setWebPage(WebPage webPage, boolean searchWebPages) {
        this.messageWebPage = webPage;
        this.messageWebPageSearch = searchWebPages;
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
        AnimatorSet AnimatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.recordedAudioPanel, "alpha", new float[]{0.0f});
        AnimatorSet.playTogether(animatorArr);
        AnimatorSet.setDuration(200);
        AnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
            }
        });
        AnimatorSet.start();
    }

    private void sendMessage() {
        if (this.videoToSendMessageObject != null) {
            r0.delegate.needStartRecordVideo(4);
            hideRecordedAudioPanel();
            checkSendButton(true);
        } else if (r0.audioToSend != null) {
            MessageObject playing = MediaController.getInstance().getPlayingMessageObject();
            if (playing != null && playing == r0.audioToSendMessageObject) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            SendMessagesHelper.getInstance(r0.currentAccount).sendMessage(r0.audioToSend, null, r0.audioToSendPath, r0.dialog_id, r0.replyingMessageObject, null, null, null, null, 0);
            if (r0.delegate != null) {
                r0.delegate.onMessageSend(null);
            }
            hideRecordedAudioPanel();
            checkSendButton(true);
        } else {
            CharSequence message = r0.messageEditText.getText();
            if (processSendingText(message)) {
                r0.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                r0.lastTypingTimeSend = 0;
                if (r0.delegate != null) {
                    r0.delegate.onMessageSend(message);
                }
            } else if (r0.forceShowSendButton && r0.delegate != null) {
                r0.delegate.onMessageSend(null);
            }
        }
    }

    public void doneEditingMessage() {
        if (this.editingMessageObject != null) {
            this.delegate.onMessageEditEnd(true);
            showEditDoneProgress(true, true);
            CharSequence[] message = new CharSequence[]{this.messageEditText.getText()};
            this.editingMessageReqId = SendMessagesHelper.getInstance(this.currentAccount).editMessage(this.editingMessageObject, message[0].toString(), this.messageWebPageSearch, this.parentFragment, DataQuery.getInstance(this.currentAccount).getEntities(message), new Runnable() {
                public void run() {
                    ChatActivityEnterView.this.editingMessageReqId = 0;
                    ChatActivityEnterView.this.setEditingMessageObject(null, false);
                }
            });
        }
    }

    public boolean processSendingText(CharSequence text) {
        ChatActivityEnterView chatActivityEnterView = this;
        CharSequence text2 = AndroidUtilities.getTrimmedString(text);
        if (text2.length() == 0) {
            return false;
        }
        int count = (int) Math.ceil((double) (((float) text2.length()) / 4096.0f));
        for (int a = 0; a < count; a++) {
            CharSequence[] message = new CharSequence[]{text2.subSequence(a * 4096, Math.min((a + 1) * 4096, text2.length()))};
            SendMessagesHelper.getInstance(chatActivityEnterView.currentAccount).sendMessage(message[0].toString(), chatActivityEnterView.dialog_id, chatActivityEnterView.replyingMessageObject, chatActivityEnterView.messageWebPage, chatActivityEnterView.messageWebPageSearch, DataQuery.getInstance(chatActivityEnterView.currentAccount).getEntities(message), null, null);
        }
        return true;
    }

    private void checkSendButton(boolean animated) {
        if (this.editingMessageObject == null) {
            boolean animated2;
            Animator[] animatorArr;
            if (r0.isPaused) {
                animated2 = false;
            } else {
                animated2 = animated;
            }
            if (AndroidUtilities.getTrimmedString(r0.messageEditText.getText()).length() <= 0 && !r0.forceShowSendButton && r0.audioToSend == null) {
                if (r0.videoToSendMessageObject == null) {
                    AnimatorSet animatorSet;
                    ArrayList<Animator> animators;
                    if (r0.emojiView == null || r0.emojiView.getVisibility() != 0 || !r0.stickersTabOpen || AndroidUtilities.isInMultiwindow) {
                        if (r0.sendButton.getVisibility() == 0 || r0.cancelBotButton.getVisibility() == 0 || r0.expandStickersButton.getVisibility() == 0) {
                            if (!animated2) {
                                r0.sendButton.setScaleX(0.1f);
                                r0.sendButton.setScaleY(0.1f);
                                r0.sendButton.setAlpha(0.0f);
                                r0.cancelBotButton.setScaleX(0.1f);
                                r0.cancelBotButton.setScaleY(0.1f);
                                r0.cancelBotButton.setAlpha(0.0f);
                                r0.expandStickersButton.setScaleX(0.1f);
                                r0.expandStickersButton.setScaleY(0.1f);
                                r0.expandStickersButton.setAlpha(0.0f);
                                r0.audioVideoButtonContainer.setScaleX(1.0f);
                                r0.audioVideoButtonContainer.setScaleY(1.0f);
                                r0.audioVideoButtonContainer.setAlpha(1.0f);
                                r0.cancelBotButton.setVisibility(8);
                                r0.sendButton.setVisibility(8);
                                r0.expandStickersButton.setVisibility(8);
                                r0.audioVideoButtonContainer.setVisibility(0);
                                if (r0.attachLayout != null) {
                                    if (getVisibility() == 0) {
                                        r0.delegate.onAttachButtonShow();
                                    }
                                    r0.attachLayout.setVisibility(0);
                                    updateFieldRight(1);
                                }
                            } else if (r0.runningAnimationType != 2) {
                                if (r0.runningAnimation != null) {
                                    r0.runningAnimation.cancel();
                                    r0.runningAnimation = null;
                                }
                                if (r0.runningAnimation2 != null) {
                                    r0.runningAnimation2.cancel();
                                    r0.runningAnimation2 = null;
                                }
                                if (r0.attachLayout != null) {
                                    r0.attachLayout.setVisibility(0);
                                    r0.runningAnimation2 = new AnimatorSet();
                                    animatorSet = r0.runningAnimation2;
                                    r4 = new Animator[2];
                                    r4[0] = ObjectAnimator.ofFloat(r0.attachLayout, "alpha", new float[]{1.0f});
                                    r4[1] = ObjectAnimator.ofFloat(r0.attachLayout, "scaleX", new float[]{1.0f});
                                    animatorSet.playTogether(r4);
                                    r0.runningAnimation2.setDuration(100);
                                    r0.runningAnimation2.start();
                                    updateFieldRight(1);
                                    if (getVisibility() == 0) {
                                        r0.delegate.onAttachButtonShow();
                                    }
                                }
                                r0.audioVideoButtonContainer.setVisibility(0);
                                r0.runningAnimation = new AnimatorSet();
                                r0.runningAnimationType = 2;
                                animators = new ArrayList();
                                animators.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleX", new float[]{1.0f}));
                                animators.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleY", new float[]{1.0f}));
                                animators.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "alpha", new float[]{1.0f}));
                                if (r0.cancelBotButton.getVisibility() == 0) {
                                    animators.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleX", new float[]{0.1f}));
                                    animators.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleY", new float[]{0.1f}));
                                    animators.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "alpha", new float[]{0.0f}));
                                } else if (r0.expandStickersButton.getVisibility() == 0) {
                                    animators.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleX", new float[]{0.1f}));
                                    animators.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleY", new float[]{0.1f}));
                                    animators.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "alpha", new float[]{0.0f}));
                                } else {
                                    animators.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleX", new float[]{0.1f}));
                                    animators.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleY", new float[]{0.1f}));
                                    animators.add(ObjectAnimator.ofFloat(r0.sendButton, "alpha", new float[]{0.0f}));
                                }
                                r0.runningAnimation.playTogether(animators);
                                r0.runningAnimation.setDuration(150);
                                r0.runningAnimation.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animation)) {
                                            ChatActivityEnterView.this.sendButton.setVisibility(8);
                                            ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                            ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
                                            ChatActivityEnterView.this.runningAnimation = null;
                                            ChatActivityEnterView.this.runningAnimationType = 0;
                                        }
                                    }

                                    public void onAnimationCancel(Animator animation) {
                                        if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animation)) {
                                            ChatActivityEnterView.this.runningAnimation = null;
                                        }
                                    }
                                });
                                r0.runningAnimation.start();
                            } else {
                                return;
                            }
                        }
                    }
                    if (!animated2) {
                        r0.sendButton.setScaleX(0.1f);
                        r0.sendButton.setScaleY(0.1f);
                        r0.sendButton.setAlpha(0.0f);
                        r0.cancelBotButton.setScaleX(0.1f);
                        r0.cancelBotButton.setScaleY(0.1f);
                        r0.cancelBotButton.setAlpha(0.0f);
                        r0.audioVideoButtonContainer.setScaleX(0.1f);
                        r0.audioVideoButtonContainer.setScaleY(0.1f);
                        r0.audioVideoButtonContainer.setAlpha(0.0f);
                        r0.expandStickersButton.setScaleX(1.0f);
                        r0.expandStickersButton.setScaleY(1.0f);
                        r0.expandStickersButton.setAlpha(1.0f);
                        r0.cancelBotButton.setVisibility(8);
                        r0.sendButton.setVisibility(8);
                        r0.audioVideoButtonContainer.setVisibility(8);
                        r0.expandStickersButton.setVisibility(0);
                        if (r0.attachLayout != null) {
                            if (getVisibility() == 0) {
                                r0.delegate.onAttachButtonShow();
                            }
                            r0.attachLayout.setVisibility(0);
                            updateFieldRight(1);
                        }
                    } else if (r0.runningAnimationType != 4) {
                        if (r0.runningAnimation != null) {
                            r0.runningAnimation.cancel();
                            r0.runningAnimation = null;
                        }
                        if (r0.runningAnimation2 != null) {
                            r0.runningAnimation2.cancel();
                            r0.runningAnimation2 = null;
                        }
                        if (r0.attachLayout != null) {
                            r0.attachLayout.setVisibility(0);
                            r0.runningAnimation2 = new AnimatorSet();
                            animatorSet = r0.runningAnimation2;
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(r0.attachLayout, "alpha", new float[]{1.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(r0.attachLayout, "scaleX", new float[]{1.0f});
                            animatorSet.playTogether(animatorArr);
                            r0.runningAnimation2.setDuration(100);
                            r0.runningAnimation2.start();
                            updateFieldRight(1);
                            if (getVisibility() == 0) {
                                r0.delegate.onAttachButtonShow();
                            }
                        }
                        r0.expandStickersButton.setVisibility(0);
                        r0.runningAnimation = new AnimatorSet();
                        r0.runningAnimationType = 4;
                        animators = new ArrayList();
                        animators.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleX", new float[]{1.0f}));
                        animators.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleY", new float[]{1.0f}));
                        animators.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "alpha", new float[]{1.0f}));
                        if (r0.cancelBotButton.getVisibility() == 0) {
                            animators.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleX", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleY", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "alpha", new float[]{0.0f}));
                        } else if (r0.audioVideoButtonContainer.getVisibility() == 0) {
                            animators.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleX", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleY", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "alpha", new float[]{0.0f}));
                        } else {
                            animators.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleX", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleY", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(r0.sendButton, "alpha", new float[]{0.0f}));
                        }
                        r0.runningAnimation.playTogether(animators);
                        r0.runningAnimation.setDuration(150);
                        r0.runningAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animation)) {
                                    ChatActivityEnterView.this.sendButton.setVisibility(8);
                                    ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                    ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                    ChatActivityEnterView.this.expandStickersButton.setVisibility(0);
                                    ChatActivityEnterView.this.runningAnimation = null;
                                    ChatActivityEnterView.this.runningAnimationType = 0;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animation)) {
                                    ChatActivityEnterView.this.runningAnimation = null;
                                }
                            }
                        });
                        r0.runningAnimation.start();
                    } else {
                        return;
                    }
                }
            }
            final String caption = r0.messageEditText.getCaption();
            boolean showBotButton = caption != null && (r0.sendButton.getVisibility() == 0 || r0.expandStickersButton.getVisibility() == 0);
            boolean showSendButton = caption == null && (r0.cancelBotButton.getVisibility() == 0 || r0.expandStickersButton.getVisibility() == 0);
            if (r0.audioVideoButtonContainer.getVisibility() == 0 || showBotButton || showSendButton) {
                if (!animated2) {
                    int i;
                    r0.audioVideoButtonContainer.setScaleX(0.1f);
                    r0.audioVideoButtonContainer.setScaleY(0.1f);
                    r0.audioVideoButtonContainer.setAlpha(0.0f);
                    if (caption != null) {
                        r0.sendButton.setScaleX(0.1f);
                        r0.sendButton.setScaleY(0.1f);
                        r0.sendButton.setAlpha(0.0f);
                        r0.cancelBotButton.setScaleX(1.0f);
                        r0.cancelBotButton.setScaleY(1.0f);
                        r0.cancelBotButton.setAlpha(1.0f);
                        r0.cancelBotButton.setVisibility(0);
                        i = 8;
                        r0.sendButton.setVisibility(8);
                    } else {
                        r0.cancelBotButton.setScaleX(0.1f);
                        r0.cancelBotButton.setScaleY(0.1f);
                        r0.cancelBotButton.setAlpha(0.0f);
                        r0.sendButton.setScaleX(1.0f);
                        r0.sendButton.setScaleY(1.0f);
                        r0.sendButton.setAlpha(1.0f);
                        r0.sendButton.setVisibility(0);
                        i = 8;
                        r0.cancelBotButton.setVisibility(8);
                    }
                    r0.audioVideoButtonContainer.setVisibility(i);
                    if (r0.attachLayout != null) {
                        r0.attachLayout.setVisibility(i);
                        if (r0.delegate != null && getVisibility() == 0) {
                            r0.delegate.onAttachButtonHidden();
                        }
                        updateFieldRight(0);
                    }
                } else if (!(r0.runningAnimationType == 1 && r0.messageEditText.getCaption() == null) && (r0.runningAnimationType != 3 || caption == null)) {
                    if (r0.runningAnimation != null) {
                        r0.runningAnimation.cancel();
                        r0.runningAnimation = null;
                    }
                    if (r0.runningAnimation2 != null) {
                        r0.runningAnimation2.cancel();
                        r0.runningAnimation2 = null;
                    }
                    if (r0.attachLayout != null) {
                        r0.runningAnimation2 = new AnimatorSet();
                        AnimatorSet animatorSet2 = r0.runningAnimation2;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(r0.attachLayout, "alpha", new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(r0.attachLayout, "scaleX", new float[]{0.0f});
                        animatorSet2.playTogether(animatorArr);
                        r0.runningAnimation2.setDuration(100);
                        r0.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivityEnterView.this.runningAnimation2 != null && ChatActivityEnterView.this.runningAnimation2.equals(animation)) {
                                    ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (ChatActivityEnterView.this.runningAnimation2 != null && ChatActivityEnterView.this.runningAnimation2.equals(animation)) {
                                    ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }
                        });
                        r0.runningAnimation2.start();
                        updateFieldRight(0);
                        if (r0.delegate != null && getVisibility() == 0) {
                            r0.delegate.onAttachButtonHidden();
                        }
                    }
                    r0.runningAnimation = new AnimatorSet();
                    ArrayList<Animator> animators2 = new ArrayList();
                    if (r0.audioVideoButtonContainer.getVisibility() == 0) {
                        animators2.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleX", new float[]{0.1f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleY", new float[]{0.1f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "alpha", new float[]{0.0f}));
                    }
                    if (r0.expandStickersButton.getVisibility() == 0) {
                        animators2.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleX", new float[]{0.1f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleY", new float[]{0.1f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "alpha", new float[]{0.0f}));
                    }
                    if (showBotButton) {
                        animators2.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleX", new float[]{0.1f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleY", new float[]{0.1f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.sendButton, "alpha", new float[]{0.0f}));
                    } else if (showSendButton) {
                        animators2.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleX", new float[]{0.1f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleY", new float[]{0.1f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "alpha", new float[]{0.0f}));
                    }
                    if (caption != null) {
                        r0.runningAnimationType = 3;
                        animators2.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleX", new float[]{1.0f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleY", new float[]{1.0f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "alpha", new float[]{1.0f}));
                        r0.cancelBotButton.setVisibility(0);
                    } else {
                        r0.runningAnimationType = 1;
                        animators2.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleX", new float[]{1.0f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleY", new float[]{1.0f}));
                        animators2.add(ObjectAnimator.ofFloat(r0.sendButton, "alpha", new float[]{1.0f}));
                        r0.sendButton.setVisibility(0);
                    }
                    r0.runningAnimation.playTogether(animators2);
                    r0.runningAnimation.setDuration(150);
                    r0.runningAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animation)) {
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

                        public void onAnimationCancel(Animator animation) {
                            if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animation)) {
                                ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }
                    });
                    r0.runningAnimation.start();
                }
            }
        }
    }

    private void updateFieldRight(int attachVisible) {
        if (this.messageEditText != null) {
            if (this.editingMessageObject == null) {
                LayoutParams layoutParams = (LayoutParams) this.messageEditText.getLayoutParams();
                if (attachVisible == 1) {
                    if ((this.botButton == null || this.botButton.getVisibility() != 0) && (this.notifyButton == null || this.notifyButton.getVisibility() != 0)) {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    } else {
                        layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                    }
                } else if (attachVisible != 2) {
                    layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
                } else if (layoutParams.rightMargin != AndroidUtilities.dp(2.0f)) {
                    if ((this.botButton == null || this.botButton.getVisibility() != 0) && (this.notifyButton == null || this.notifyButton.getVisibility() != 0)) {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    } else {
                        layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                    }
                }
                this.messageEditText.setLayoutParams(layoutParams);
            }
        }
    }

    private void updateRecordIntefrace() {
        if (!this.recordingAudioVideo) {
            if (this.wakeLock != null) {
                try {
                    this.wakeLock.release();
                    this.wakeLock = null;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
            AndroidUtilities.unlockOrientation(this.parentActivity);
            if (this.recordInterfaceState != 0) {
                this.recordInterfaceState = 0;
                if (this.runningAnimationAudio != null) {
                    this.runningAnimationAudio.cancel();
                }
                this.runningAnimationAudio = new AnimatorSet();
                AnimatorSet animatorSet = this.runningAnimationAudio;
                r3 = new Animator[3];
                r3[0] = ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[]{(float) AndroidUtilities.displaySize.x});
                r3[1] = ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[]{0.0f});
                r3[2] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "alpha", new float[]{1.0f});
                animatorSet.playTogether(r3);
                this.runningAnimationAudio.setDuration(300);
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivityEnterView.this.runningAnimationAudio != null && ChatActivityEnterView.this.runningAnimationAudio.equals(animator)) {
                            LayoutParams params = (LayoutParams) ChatActivityEnterView.this.slideText.getLayoutParams();
                            params.leftMargin = AndroidUtilities.dp(30.0f);
                            ChatActivityEnterView.this.slideText.setLayoutParams(params);
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
                    this.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(536870918, "audio record lock");
                    this.wakeLock.acquire();
                }
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
            AndroidUtilities.lockOrientation(this.parentActivity);
            this.recordPanel.setVisibility(0);
            this.recordCircle.setVisibility(0);
            this.recordCircle.setAmplitude(0.0d);
            this.recordTimeText.setText(String.format("%02d:%02d.%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)}));
            this.recordDot.resetAlpha();
            this.lastTimeString = null;
            this.lastTypingSendTime = -1;
            LayoutParams params = (LayoutParams) this.slideText.getLayoutParams();
            params.leftMargin = AndroidUtilities.dp(30.0f);
            this.slideText.setLayoutParams(params);
            this.slideText.setAlpha(1.0f);
            this.recordPanel.setX((float) AndroidUtilities.displaySize.x);
            if (this.runningAnimationAudio != null) {
                this.runningAnimationAudio.cancel();
            }
            this.runningAnimationAudio = new AnimatorSet();
            AnimatorSet animatorSet2 = this.runningAnimationAudio;
            r7 = new Animator[3];
            r7[0] = ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[]{0.0f});
            r7[1] = ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[]{1.0f});
            r7[2] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "alpha", new float[]{0.0f});
            animatorSet2.playTogether(r7);
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
        ChatActivityEnterView chatActivityEnterView = this;
        MessageObject messageObject2 = messageObject;
        String str = command;
        if (str != null) {
            if (getVisibility() == 0) {
                User user = null;
                if (longPress) {
                    String text;
                    String text2 = chatActivityEnterView.messageEditText.getText().toString();
                    if (messageObject2 != null && ((int) chatActivityEnterView.dialog_id) < 0) {
                        user = MessagesController.getInstance(chatActivityEnterView.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                    }
                    if ((chatActivityEnterView.botCount != 1 || username) && user != null && user.bot && !str.contains("@")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(String.format(Locale.US, "%s@%s", new Object[]{str, user.username}));
                        stringBuilder.append(" ");
                        stringBuilder.append(text2.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", TtmlNode.ANONYMOUS_REGION_ID));
                        text = stringBuilder.toString();
                    } else {
                        text = new StringBuilder();
                        text.append(str);
                        text.append(" ");
                        text.append(text2.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", TtmlNode.ANONYMOUS_REGION_ID));
                        text = text.toString();
                    }
                    chatActivityEnterView.ignoreTextChange = true;
                    chatActivityEnterView.messageEditText.setText(text);
                    chatActivityEnterView.messageEditText.setSelection(chatActivityEnterView.messageEditText.getText().length());
                    chatActivityEnterView.ignoreTextChange = false;
                    if (chatActivityEnterView.delegate != null) {
                        chatActivityEnterView.delegate.onTextChanged(chatActivityEnterView.messageEditText.getText(), true);
                    }
                    if (!chatActivityEnterView.keyboardVisible && chatActivityEnterView.currentPopupContentType == -1) {
                        openKeyboard();
                    }
                } else {
                    if (messageObject2 != null && ((int) chatActivityEnterView.dialog_id) < 0) {
                        user = MessagesController.getInstance(chatActivityEnterView.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                    }
                    User user2 = user;
                    if ((chatActivityEnterView.botCount != 1 || username) && user2 != null && user2.bot && !str.contains("@")) {
                        SendMessagesHelper.getInstance(chatActivityEnterView.currentAccount).sendMessage(String.format(Locale.US, "%s@%s", new Object[]{str, user2.username}), chatActivityEnterView.dialog_id, chatActivityEnterView.replyingMessageObject, null, false, null, null, null);
                    } else {
                        SendMessagesHelper.getInstance(chatActivityEnterView.currentAccount).sendMessage(str, chatActivityEnterView.dialog_id, chatActivityEnterView.replyingMessageObject, null, false, null, null, null);
                    }
                }
            }
        }
    }

    public void setEditingMessageObject(MessageObject messageObject, boolean caption) {
        MessageObject messageObject2 = messageObject;
        boolean z = caption;
        if (this.audioToSend == null && r1.videoToSendMessageObject == null) {
            if (r1.editingMessageObject != messageObject2) {
                int i = 1;
                if (r1.editingMessageReqId != 0) {
                    ConnectionsManager.getInstance(r1.currentAccount).cancelRequest(r1.editingMessageReqId, true);
                    r1.editingMessageReqId = 0;
                }
                r1.editingMessageObject = messageObject2;
                r1.editingCaption = z;
                if (r1.editingMessageObject != null) {
                    CharSequence editingText;
                    if (r1.doneButtonAnimation != null) {
                        r1.doneButtonAnimation.cancel();
                        r1.doneButtonAnimation = null;
                    }
                    r1.doneButtonContainer.setVisibility(0);
                    showEditDoneProgress(true, false);
                    InputFilter[] inputFilters = new InputFilter[1];
                    if (z) {
                        inputFilters[0] = new LengthFilter(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                        editingText = r1.editingMessageObject.caption;
                    } else {
                        inputFilters[0] = new LengthFilter(4096);
                        editingText = r1.editingMessageObject.messageText;
                    }
                    if (editingText != null) {
                        int a;
                        ArrayList<MessageEntity> entities = r1.editingMessageObject.messageOwner.entities;
                        DataQuery.sortEntities(entities);
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(editingText);
                        Object[] spansToRemove = stringBuilder.getSpans(0, stringBuilder.length(), Object.class);
                        if (spansToRemove != null && spansToRemove.length > 0) {
                            for (Object removeSpan : spansToRemove) {
                                stringBuilder.removeSpan(removeSpan);
                            }
                        }
                        if (entities != null) {
                            a = 0;
                            int addToOffset = a;
                            while (a < entities.size()) {
                                try {
                                    MessageEntity entity = (MessageEntity) entities.get(a);
                                    if ((entity.offset + entity.length) + addToOffset <= stringBuilder.length()) {
                                        if (entity instanceof TL_inputMessageEntityMentionName) {
                                            if ((entity.offset + entity.length) + addToOffset < stringBuilder.length() && stringBuilder.charAt((entity.offset + entity.length) + addToOffset) == ' ') {
                                                entity.length += i;
                                            }
                                            StringBuilder stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                            stringBuilder2.append(((TL_inputMessageEntityMentionName) entity).user_id.user_id);
                                            stringBuilder.setSpan(new URLSpanUserMention(stringBuilder2.toString(), i), entity.offset + addToOffset, (entity.offset + entity.length) + addToOffset, 33);
                                        } else if (entity instanceof TL_messageEntityMentionName) {
                                            if ((entity.offset + entity.length) + addToOffset < stringBuilder.length() && stringBuilder.charAt((entity.offset + entity.length) + addToOffset) == ' ') {
                                                entity.length++;
                                            }
                                            StringBuilder stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append(TtmlNode.ANONYMOUS_REGION_ID);
                                            stringBuilder3.append(((TL_messageEntityMentionName) entity).user_id);
                                            stringBuilder.setSpan(new URLSpanUserMention(stringBuilder3.toString(), 1), entity.offset + addToOffset, (entity.offset + entity.length) + addToOffset, 33);
                                        } else if (entity instanceof TL_messageEntityCode) {
                                            stringBuilder.insert((entity.offset + entity.length) + addToOffset, "`");
                                            stringBuilder.insert(entity.offset + addToOffset, "`");
                                            addToOffset += 2;
                                        } else if (entity instanceof TL_messageEntityPre) {
                                            stringBuilder.insert((entity.offset + entity.length) + addToOffset, "```");
                                            stringBuilder.insert(entity.offset + addToOffset, "```");
                                            addToOffset += 6;
                                        } else if (entity instanceof TL_messageEntityBold) {
                                            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), entity.offset + addToOffset, (entity.offset + entity.length) + addToOffset, 33);
                                        } else if (entity instanceof TL_messageEntityItalic) {
                                            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), entity.offset + addToOffset, (entity.offset + entity.length) + addToOffset, 33);
                                        }
                                    }
                                    a++;
                                    i = 1;
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        }
                        setFieldText(Emoji.replaceEmoji(new SpannableStringBuilder(stringBuilder), r1.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
                    } else {
                        setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                    r1.messageEditText.setFilters(inputFilters);
                    openKeyboard();
                    LayoutParams layoutParams = (LayoutParams) r1.messageEditText.getLayoutParams();
                    layoutParams.rightMargin = AndroidUtilities.dp(4.0f);
                    r1.messageEditText.setLayoutParams(layoutParams);
                    r1.sendButton.setVisibility(8);
                    r1.cancelBotButton.setVisibility(8);
                    r1.audioVideoButtonContainer.setVisibility(8);
                    r1.attachLayout.setVisibility(8);
                    r1.sendButtonContainer.setVisibility(8);
                } else {
                    r1.doneButtonContainer.setVisibility(8);
                    r1.messageEditText.setFilters(new InputFilter[0]);
                    r1.delegate.onMessageEditEnd(false);
                    r1.audioVideoButtonContainer.setVisibility(0);
                    r1.attachLayout.setVisibility(0);
                    r1.sendButtonContainer.setVisibility(0);
                    r1.attachLayout.setScaleX(1.0f);
                    r1.attachLayout.setAlpha(1.0f);
                    r1.sendButton.setScaleX(0.1f);
                    r1.sendButton.setScaleY(0.1f);
                    r1.sendButton.setAlpha(0.0f);
                    r1.cancelBotButton.setScaleX(0.1f);
                    r1.cancelBotButton.setScaleY(0.1f);
                    r1.cancelBotButton.setAlpha(0.0f);
                    r1.audioVideoButtonContainer.setScaleX(1.0f);
                    r1.audioVideoButtonContainer.setScaleY(1.0f);
                    r1.audioVideoButtonContainer.setAlpha(1.0f);
                    r1.sendButton.setVisibility(8);
                    r1.cancelBotButton.setVisibility(8);
                    r1.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                    if (getVisibility() == 0) {
                        r1.delegate.onAttachButtonShow();
                    }
                    updateFieldRight(1);
                }
                updateFieldHint();
            }
        }
    }

    public ImageView getAttachButton() {
        return this.attachButton;
    }

    public ImageView getBotButton() {
        return this.botButton;
    }

    public ImageView getEmojiButton() {
        return this.emojiButton;
    }

    public ImageView getSendButton() {
        return this.sendButton;
    }

    public EmojiView getEmojiView() {
        return this.emojiView;
    }

    public void setFieldText(CharSequence text) {
        if (this.messageEditText != null) {
            this.ignoreTextChange = true;
            this.messageEditText.setText(text);
            this.messageEditText.setSelection(this.messageEditText.getText().length());
            this.ignoreTextChange = false;
            if (this.delegate != null) {
                this.delegate.onTextChanged(this.messageEditText.getText(), true);
            }
        }
    }

    public void setSelection(int start) {
        if (this.messageEditText != null) {
            this.messageEditText.setSelection(start, this.messageEditText.length());
        }
    }

    public int getCursorPosition() {
        if (this.messageEditText == null) {
            return 0;
        }
        return this.messageEditText.getSelectionStart();
    }

    public int getSelectionLength() {
        if (this.messageEditText == null) {
            return 0;
        }
        try {
            return this.messageEditText.getSelectionEnd() - this.messageEditText.getSelectionStart();
        } catch (Throwable e) {
            FileLog.m3e(e);
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void setFieldFocused() {
        if (this.messageEditText != null) {
            try {
                this.messageEditText.requestFocus();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public void setFieldFocused(boolean focus) {
        if (this.messageEditText != null) {
            if (focus) {
                if (!(this.searchingStickers || this.messageEditText.isFocused())) {
                    this.messageEditText.postDelayed(new Runnable() {
                        public void run() {
                            if (ChatActivityEnterView.this.messageEditText != null) {
                                try {
                                    ChatActivityEnterView.this.messageEditText.requestFocus();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        }
                    }, 600);
                }
            } else if (this.messageEditText.isFocused() && !this.keyboardVisible) {
                this.messageEditText.clearFocus();
            }
        }
    }

    public boolean hasText() {
        return this.messageEditText != null && this.messageEditText.length() > 0;
    }

    public CharSequence getFieldText() {
        if (this.messageEditText == null || this.messageEditText.length() <= 0) {
            return null;
        }
        return this.messageEditText.getText();
    }

    private void updateBotButton() {
        if (this.botButton != null) {
            LinearLayout linearLayout;
            float f;
            if (!this.hasBotCommands) {
                if (this.botReplyMarkup == null) {
                    this.botButton.setVisibility(8);
                    updateFieldRight(2);
                    linearLayout = this.attachLayout;
                    if (this.botButton == null || this.botButton.getVisibility() == 8) {
                        if (this.notifyButton != null) {
                            if (this.notifyButton.getVisibility() == 8) {
                            }
                        }
                        f = 48.0f;
                        linearLayout.setPivotX((float) AndroidUtilities.dp(f));
                    }
                    f = 96.0f;
                    linearLayout.setPivotX((float) AndroidUtilities.dp(f));
                }
            }
            if (this.botButton.getVisibility() != 0) {
                this.botButton.setVisibility(0);
            }
            if (this.botReplyMarkup == null) {
                this.botButton.setImageResource(R.drawable.bot_keyboard);
            } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                this.botButton.setImageResource(R.drawable.ic_msg_panel_kb);
            } else {
                this.botButton.setImageResource(R.drawable.bot_keyboard2);
            }
            updateFieldRight(2);
            linearLayout = this.attachLayout;
            if (this.notifyButton != null) {
                if (this.notifyButton.getVisibility() == 8) {
                }
                f = 96.0f;
                linearLayout.setPivotX((float) AndroidUtilities.dp(f));
            }
            f = 48.0f;
            linearLayout.setPivotX((float) AndroidUtilities.dp(f));
        }
    }

    public void setBotsCount(int count, boolean hasCommands) {
        this.botCount = count;
        if (this.hasBotCommands != hasCommands) {
            this.hasBotCommands = hasCommands;
            updateBotButton();
        }
    }

    public void setButtons(MessageObject messageObject) {
        setButtons(messageObject, true);
    }

    public void setButtons(MessageObject messageObject, boolean openKeyboard) {
        if (this.replyingMessageObject == null || this.replyingMessageObject != this.botButtonsMessageObject || this.replyingMessageObject == messageObject) {
            if (this.botButton != null && (this.botButtonsMessageObject == null || this.botButtonsMessageObject != messageObject)) {
                if (this.botButtonsMessageObject != null || messageObject != null) {
                    if (this.botKeyboardView == null) {
                        this.botKeyboardView = new BotKeyboardView(this.parentActivity);
                        this.botKeyboardView.setVisibility(8);
                        this.botKeyboardView.setDelegate(new BotKeyboardViewDelegate() {
                            public void didPressedButton(KeyboardButton button) {
                                MessageObject object = ChatActivityEnterView.this.replyingMessageObject != null ? ChatActivityEnterView.this.replyingMessageObject : ((int) ChatActivityEnterView.this.dialog_id) < 0 ? ChatActivityEnterView.this.botButtonsMessageObject : null;
                                ChatActivityEnterView.this.didPressedBotButton(button, object, ChatActivityEnterView.this.replyingMessageObject != null ? ChatActivityEnterView.this.replyingMessageObject : ChatActivityEnterView.this.botButtonsMessageObject);
                                if (ChatActivityEnterView.this.replyingMessageObject != null) {
                                    ChatActivityEnterView.this.openKeyboardInternal();
                                    ChatActivityEnterView.this.setButtons(ChatActivityEnterView.this.botMessageObject, false);
                                } else if (ChatActivityEnterView.this.botButtonsMessageObject.messageOwner.reply_markup.single_use) {
                                    ChatActivityEnterView.this.openKeyboardInternal();
                                    Editor edit = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("answered_");
                                    stringBuilder.append(ChatActivityEnterView.this.dialog_id);
                                    edit.putInt(stringBuilder.toString(), ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                                }
                                if (ChatActivityEnterView.this.delegate != null) {
                                    ChatActivityEnterView.this.delegate.onMessageSend(null);
                                }
                            }
                        });
                        this.sizeNotifierLayout.addView(this.botKeyboardView);
                    }
                    this.botButtonsMessageObject = messageObject;
                    TL_replyKeyboardMarkup tL_replyKeyboardMarkup = (messageObject == null || !(messageObject.messageOwner.reply_markup instanceof TL_replyKeyboardMarkup)) ? null : (TL_replyKeyboardMarkup) messageObject.messageOwner.reply_markup;
                    this.botReplyMarkup = tL_replyKeyboardMarkup;
                    this.botKeyboardView.setPanelHeight(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight);
                    this.botKeyboardView.setButtons(this.botReplyMarkup);
                    if (this.botReplyMarkup != null) {
                        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("hidekeyboard_");
                        stringBuilder.append(this.dialog_id);
                        boolean keyboardHidden = preferences.getInt(stringBuilder.toString(), 0) == messageObject.getId();
                        if (this.botButtonsMessageObject != this.replyingMessageObject && this.botReplyMarkup.single_use) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("answered_");
                            stringBuilder2.append(this.dialog_id);
                            if (preferences.getInt(stringBuilder2.toString(), 0) == messageObject.getId()) {
                                return;
                            }
                        }
                        if (!(keyboardHidden || this.messageEditText.length() != 0 || isPopupShowing())) {
                            showPopup(1, 1);
                        }
                    } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                        if (openKeyboard) {
                            openKeyboardInternal();
                        } else {
                            showPopup(0, 1);
                        }
                    }
                    updateBotButton();
                    return;
                }
            }
            return;
        }
        this.botMessageObject = messageObject;
    }

    public void didPressedBotButton(final KeyboardButton button, MessageObject replyMessageObject, final MessageObject messageObject) {
        if (button != null) {
            if (messageObject != null) {
                if (button instanceof TL_keyboardButton) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(button.text, this.dialog_id, replyMessageObject, null, false, null, null, null);
                } else if (button instanceof TL_keyboardButtonUrl) {
                    this.parentFragment.showOpenUrlAlert(button.url, true);
                } else if (button instanceof TL_keyboardButtonRequestPhone) {
                    this.parentFragment.shareMyContact(messageObject);
                } else if (button instanceof TL_keyboardButtonRequestGeoLocation) {
                    Builder builder = new Builder(this.parentActivity);
                    builder.setTitle(LocaleController.getString("ShareYouLocationTitle", R.string.ShareYouLocationTitle));
                    builder.setMessage(LocaleController.getString("ShareYouLocationInfo", R.string.ShareYouLocationInfo));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                                SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendCurrentLocation(messageObject, button);
                                return;
                            }
                            ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                            ChatActivityEnterView.this.pendingMessageObject = messageObject;
                            ChatActivityEnterView.this.pendingLocationButton = button;
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    this.parentFragment.showDialog(builder.create());
                } else {
                    if (!((button instanceof TL_keyboardButtonCallback) || (button instanceof TL_keyboardButtonGame))) {
                        if (!(button instanceof TL_keyboardButtonBuy)) {
                            if ((button instanceof TL_keyboardButtonSwitchInline) && !this.parentFragment.processSwitchButton((TL_keyboardButtonSwitchInline) button)) {
                                if (button.same_peer) {
                                    int uid = messageObject.messageOwner.from_id;
                                    if (messageObject.messageOwner.via_bot_id != 0) {
                                        uid = messageObject.messageOwner.via_bot_id;
                                    }
                                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
                                    if (user != null) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("@");
                                        stringBuilder.append(user.username);
                                        stringBuilder.append(" ");
                                        stringBuilder.append(button.query);
                                        setFieldText(stringBuilder.toString());
                                    } else {
                                        return;
                                    }
                                }
                                Bundle args = new Bundle();
                                args.putBoolean("onlySelect", true);
                                args.putInt("dialogsType", 1);
                                DialogsActivity fragment = new DialogsActivity(args);
                                fragment.setDelegate(new DialogsActivityDelegate() {
                                    public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                                        int uid = messageObject.messageOwner.from_id;
                                        if (messageObject.messageOwner.via_bot_id != 0) {
                                            uid = messageObject.messageOwner.via_bot_id;
                                        }
                                        User user = MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).getUser(Integer.valueOf(uid));
                                        if (user == null) {
                                            fragment.finishFragment();
                                            return;
                                        }
                                        long did = ((Long) dids.get(0)).longValue();
                                        DataQuery instance = DataQuery.getInstance(ChatActivityEnterView.this.currentAccount);
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("@");
                                        stringBuilder.append(user.username);
                                        stringBuilder.append(" ");
                                        stringBuilder.append(button.query);
                                        instance.saveDraft(did, stringBuilder.toString(), null, null, true);
                                        if (did != ChatActivityEnterView.this.dialog_id) {
                                            int lower_part = (int) did;
                                            if (lower_part != 0) {
                                                Bundle args = new Bundle();
                                                if (lower_part > 0) {
                                                    args.putInt("user_id", lower_part);
                                                } else if (lower_part < 0) {
                                                    args.putInt("chat_id", -lower_part);
                                                }
                                                BaseFragment baseFragment = fragment;
                                                if (MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).checkCanOpenChat(args, baseFragment)) {
                                                    if (!ChatActivityEnterView.this.parentFragment.presentFragment(new ChatActivity(args), true)) {
                                                        baseFragment.finishFragment();
                                                    } else if (!AndroidUtilities.isTablet()) {
                                                        ChatActivityEnterView.this.parentFragment.removeSelfFromStack();
                                                    }
                                                } else {
                                                    return;
                                                }
                                            }
                                            fragment.finishFragment();
                                        } else {
                                            fragment.finishFragment();
                                        }
                                    }
                                });
                                this.parentFragment.presentFragment(fragment);
                            } else {
                                return;
                            }
                        }
                    }
                    SendMessagesHelper.getInstance(this.currentAccount).sendCallback(true, messageObject, button, this.parentFragment);
                }
            }
        }
    }

    public boolean isPopupView(View view) {
        if (view != this.botKeyboardView) {
            if (view != this.emojiView) {
                return false;
            }
        }
        return true;
    }

    public boolean isRecordCircle(View view) {
        return view == this.recordCircle;
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            this.emojiView = new EmojiView(this.allowStickers, this.allowGifs, this.parentActivity, this.info);
            this.emojiView.setVisibility(8);
            this.emojiView.setListener(new Listener() {

                /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$40$1 */
                class C10971 implements DialogInterface.OnClickListener {
                    C10971() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChatActivityEnterView.this.emojiView.clearRecentEmoji();
                    }
                }

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
                        ChatActivityEnterView.this.innerTextChange = 2;
                        CharSequence localCharSequence = Emoji.replaceEmoji(symbol, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(i, localCharSequence));
                        int j = localCharSequence.length() + i;
                        ChatActivityEnterView.this.messageEditText.setSelection(j, j);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    } catch (Throwable th) {
                        ChatActivityEnterView.this.innerTextChange = 0;
                    }
                    ChatActivityEnterView.this.innerTextChange = 0;
                }

                public void onStickerSelected(Document sticker) {
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        if (ChatActivityEnterView.this.searchingStickers) {
                            ChatActivityEnterView.this.searchingStickers = false;
                            ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(sticker));
                            ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                        }
                        ChatActivityEnterView.this.setStickersExpanded(false, true);
                    }
                    ChatActivityEnterView.this.onStickerSelected(sticker);
                    DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).addRecentSticker(0, sticker, (int) (System.currentTimeMillis() / 1000), false);
                    if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                        MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).saveGif(sticker);
                    }
                }

                public void onStickersSettingsClick() {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0));
                    }
                }

                public void onGifSelected(Document gif) {
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        ChatActivityEnterView.this.setStickersExpanded(false, true);
                    }
                    SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(gif, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
                    DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(gif, (int) (System.currentTimeMillis() / 1000));
                    if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                        MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).saveGif(gif);
                    }
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onMessageSend(null);
                    }
                }

                public void onGifTab(boolean opened) {
                    ChatActivityEnterView.this.post(ChatActivityEnterView.this.updateExpandabilityRunnable);
                    if (!AndroidUtilities.usingHardwareInput) {
                        if (opened) {
                            if (ChatActivityEnterView.this.messageEditText.length() == 0) {
                                ChatActivityEnterView.this.messageEditText.setText("@gif ");
                                ChatActivityEnterView.this.messageEditText.setSelection(ChatActivityEnterView.this.messageEditText.length());
                            }
                        } else if (ChatActivityEnterView.this.messageEditText.getText().toString().equals("@gif ")) {
                            ChatActivityEnterView.this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        }
                    }
                }

                public void onStickersTab(boolean opened) {
                    ChatActivityEnterView.this.delegate.onStickersTab(opened);
                    ChatActivityEnterView.this.post(ChatActivityEnterView.this.updateExpandabilityRunnable);
                }

                public void onClearEmojiRecent() {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        if (ChatActivityEnterView.this.parentActivity != null) {
                            Builder builder = new Builder(ChatActivityEnterView.this.parentActivity);
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setMessage(LocaleController.getString("ClearRecentEmoji", R.string.ClearRecentEmoji));
                            builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new C10971());
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                        }
                    }
                }

                public void onShowStickerSet(StickerSet stickerSet, InputStickerSet inputStickerSet) {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        if (ChatActivityEnterView.this.parentActivity != null) {
                            if (stickerSet != null) {
                                inputStickerSet = new TL_inputStickerSetID();
                                inputStickerSet.access_hash = stickerSet.access_hash;
                                inputStickerSet.id = stickerSet.id;
                            }
                            ChatActivityEnterView.this.parentFragment.showDialog(new StickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, inputStickerSet, null, ChatActivityEnterView.this));
                        }
                    }
                }

                public void onStickerSetAdd(StickerSetCovered stickerSet) {
                    DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).removeStickersSet(ChatActivityEnterView.this.parentActivity, stickerSet.set, 2, ChatActivityEnterView.this.parentFragment, false);
                }

                public void onStickerSetRemove(StickerSetCovered stickerSet) {
                    DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).removeStickersSet(ChatActivityEnterView.this.parentActivity, stickerSet.set, 0, ChatActivityEnterView.this.parentFragment, false);
                }

                public void onStickersGroupClick(int chatId) {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        if (AndroidUtilities.isTablet()) {
                            ChatActivityEnterView.this.hidePopup(false);
                        }
                        GroupStickersActivity fragment = new GroupStickersActivity(chatId);
                        fragment.setInfo(ChatActivityEnterView.this.info);
                        ChatActivityEnterView.this.parentFragment.presentFragment(fragment);
                    }
                }

                public void onSearchOpenClose(boolean open) {
                    ChatActivityEnterView.this.searchingStickers = open;
                    ChatActivityEnterView.this.setStickersExpanded(open, false);
                }

                public boolean isSearchOpened() {
                    return ChatActivityEnterView.this.searchingStickers;
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
                        ChatActivityEnterView.this.stickersExpandedHeight = (((ChatActivityEnterView.this.sizeNotifierLayout.getHeight() - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - ChatActivityEnterView.this.getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
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

                public void onDragEnd(float velocity) {
                    if (allowDragging()) {
                        ChatActivityEnterView.this.stickersDragging = false;
                        if ((!this.wasExpanded || velocity < ((float) AndroidUtilities.dp(200.0f))) && ((this.wasExpanded || velocity > ((float) AndroidUtilities.dp(-200.0f))) && ((!this.wasExpanded || ChatActivityEnterView.this.stickersExpansionProgress > 0.6f) && (this.wasExpanded || ChatActivityEnterView.this.stickersExpansionProgress < 0.4f)))) {
                            ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true);
                        } else {
                            ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded ^ true, true);
                        }
                    }
                }

                public void onDragCancel() {
                    if (ChatActivityEnterView.this.stickersTabOpen) {
                        ChatActivityEnterView.this.stickersDragging = false;
                        ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true);
                    }
                }

                public void onDrag(int offset) {
                    if (allowDragging()) {
                        int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                        offset = Math.max(Math.min(offset + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - origHeight));
                        ChatActivityEnterView.this.emojiView.setTranslationY((float) offset);
                        ChatActivityEnterView.this.setTranslationY((float) offset);
                        ChatActivityEnterView.this.stickersExpansionProgress = ((float) offset) / ((float) (-(ChatActivityEnterView.this.stickersExpandedHeight - origHeight)));
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

    public void onStickerSelected(Document sticker) {
        if (this.searchingStickers) {
            this.searchingStickers = false;
            this.emojiView.closeSearch(true);
            this.emojiView.hideSearchKeyboard();
        }
        setStickersExpanded(false, true);
        SendMessagesHelper.getInstance(this.currentAccount).sendSticker(sticker, this.dialog_id, this.replyingMessageObject);
        if (this.delegate != null) {
            this.delegate.onMessageSend(null);
        }
    }

    public void addStickerToRecent(Document sticker) {
        createEmojiView();
        this.emojiView.addRecentSticker(sticker);
    }

    private void showPopup(int show, int contentType) {
        if (show == 1) {
            if (contentType == 0 && this.emojiView == null) {
                if (this.parentActivity != null) {
                    createEmojiView();
                } else {
                    return;
                }
            }
            View currentView = null;
            if (contentType == 0) {
                this.emojiView.setVisibility(0);
                if (!(this.botKeyboardView == null || this.botKeyboardView.getVisibility() == 8)) {
                    this.botKeyboardView.setVisibility(8);
                }
                currentView = this.emojiView;
            } else if (contentType == 1) {
                if (!(this.emojiView == null || this.emojiView.getVisibility() == 8)) {
                    this.emojiView.setVisibility(8);
                }
                this.botKeyboardView.setVisibility(0);
                currentView = this.botKeyboardView;
            }
            this.currentPopupContentType = contentType;
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            if (contentType == 1) {
                currentHeight = Math.min(this.botKeyboardView.getKeyboardHeight(), currentHeight);
            }
            if (this.botKeyboardView != null) {
                this.botKeyboardView.setPanelHeight(currentHeight);
            }
            LayoutParams layoutParams = (LayoutParams) currentView.getLayoutParams();
            layoutParams.height = currentHeight;
            currentView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            if (this.sizeNotifierLayout != null) {
                this.emojiPadding = currentHeight;
                this.sizeNotifierLayout.requestLayout();
                if (contentType == 0) {
                    this.emojiButton.setImageResource(R.drawable.ic_msg_panel_kb);
                } else if (contentType == 1) {
                    setEmojiButtonImage();
                }
                updateBotButton();
                onWindowSizeChanged();
            }
        } else {
            if (this.emojiButton != null) {
                setEmojiButtonImage();
            }
            this.currentPopupContentType = -1;
            if (this.emojiView != null) {
                this.emojiView.setVisibility(8);
            }
            if (this.botKeyboardView != null) {
                this.botKeyboardView.setVisibility(8);
            }
            if (this.sizeNotifierLayout != null) {
                if (show == 0) {
                    this.emojiPadding = 0;
                }
                this.sizeNotifierLayout.requestLayout();
                onWindowSizeChanged();
            }
            updateBotButton();
        }
        if (this.stickersTabOpen) {
            checkSendButton(true);
        }
        if (this.stickersExpanded && show != 1) {
            setStickersExpanded(false, false);
        }
    }

    private void setEmojiButtonImage() {
        int currentPage;
        if (this.emojiView == null) {
            currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
        } else {
            currentPage = this.emojiView.getCurrentPage();
        }
        if (currentPage != 0) {
            if (this.allowStickers || this.allowGifs) {
                if (currentPage == 1) {
                    this.emojiButton.setImageResource(R.drawable.ic_msg_panel_stickers);
                    return;
                } else if (currentPage == 2) {
                    this.emojiButton.setImageResource(R.drawable.ic_msg_panel_gif);
                    return;
                } else {
                    return;
                }
            }
        }
        this.emojiButton.setImageResource(R.drawable.ic_msg_panel_smiles);
    }

    public void hidePopup(boolean byBackButton) {
        if (isPopupShowing()) {
            if (this.currentPopupContentType == 1 && byBackButton && this.botButtonsMessageObject != null) {
                Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("hidekeyboard_");
                stringBuilder.append(this.dialog_id);
                edit.putInt(stringBuilder.toString(), this.botButtonsMessageObject.getId()).commit();
            }
            if (byBackButton && this.searchingStickers) {
                this.searchingStickers = false;
                this.emojiView.closeSearch(true);
                this.messageEditText.requestFocus();
                setStickersExpanded(false, true);
                return;
            }
            if (this.searchingStickers) {
                this.searchingStickers = false;
                this.emojiView.closeSearch(false);
                this.messageEditText.requestFocus();
            }
            showPopup(0, 0);
            removeGifFromInputField();
        }
    }

    private void removeGifFromInputField() {
        if (!AndroidUtilities.usingHardwareInput && this.messageEditText.getText().toString().equals("@gif ")) {
            this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
        }
    }

    private void openKeyboardInternal() {
        int i;
        if (!AndroidUtilities.usingHardwareInput) {
            if (!this.isPaused) {
                i = 2;
                showPopup(i, 0);
                this.messageEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.messageEditText);
                if (this.isPaused) {
                    this.showKeyboardOnResume = true;
                } else if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
                    this.waitingForKeyboardOpen = true;
                    AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                    AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
                    return;
                } else {
                    return;
                }
            }
        }
        i = 0;
        showPopup(i, 0);
        this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.messageEditText);
        if (this.isPaused) {
            this.showKeyboardOnResume = true;
        } else if (!AndroidUtilities.usingHardwareInput) {
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
        if (this.audioToSendMessageObject == null) {
            if (this.videoToSendMessageObject == null) {
                return false;
            }
        }
        return true;
    }

    public void openKeyboard() {
        AndroidUtilities.showKeyboard(this.messageEditText);
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
    }

    public boolean isPopupShowing() {
        return (this.emojiView != null && this.emojiView.getVisibility() == 0) || (this.botKeyboardView != null && this.botKeyboardView.getVisibility() == 0);
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    public void addRecentGif(Document searchImage) {
        DataQuery.getInstance(this.currentAccount).addRecentGif(searchImage, (int) (System.currentTimeMillis() / 1000));
        if (this.emojiView != null) {
            this.emojiView.addRecentGif(searchImage);
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw && this.stickersExpanded) {
            this.searchingStickers = false;
            this.emojiView.closeSearch(false);
            setStickersExpanded(false, false);
        }
        this.videoTimelineView.clearFrames();
    }

    public boolean isStickersExpanded() {
        return this.stickersExpanded;
    }

    public void onSizeChanged(int height, boolean isWidthGreater) {
        boolean z = true;
        if (this.searchingStickers) {
            this.lastSizeChangeValue1 = height;
            this.lastSizeChangeValue2 = isWidthGreater;
            if (height <= 0) {
                z = false;
            }
            this.keyboardVisible = z;
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
        if (isPopupShowing()) {
            int newHeight = isWidthGreater ? this.keyboardHeightLand : this.keyboardHeight;
            if (this.currentPopupContentType == 1 && !this.botKeyboardView.isFullSize()) {
                newHeight = Math.min(this.botKeyboardView.getKeyboardHeight(), newHeight);
            }
            View currentView = null;
            if (this.currentPopupContentType == 0) {
                currentView = this.emojiView;
            } else if (this.currentPopupContentType == 1) {
                currentView = this.botKeyboardView;
            }
            if (this.botKeyboardView != null) {
                this.botKeyboardView.setPanelHeight(newHeight);
            }
            LayoutParams layoutParams = (LayoutParams) currentView.getLayoutParams();
            if (!(this.closeAnimationInProgress || ((layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == newHeight) || this.stickersExpanded))) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = newHeight;
                currentView.setLayoutParams(layoutParams);
                if (this.sizeNotifierLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    this.sizeNotifierLayout.requestLayout();
                    onWindowSizeChanged();
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
        if (height <= 0) {
            z = false;
        }
        this.keyboardVisible = z;
        if (this.keyboardVisible && isPopupShowing()) {
            showPopup(0, this.currentPopupContentType);
        }
        if (!(this.emojiPadding == 0 || this.keyboardVisible || this.keyboardVisible == oldValue || isPopupShowing())) {
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

    public void didReceivedNotification(int id, int account, Object... args) {
        ChatActivityEnterView chatActivityEnterView = this;
        int i = id;
        if (i == NotificationCenter.emojiDidLoaded) {
            if (chatActivityEnterView.emojiView != null) {
                chatActivityEnterView.emojiView.invalidateViews();
            }
            if (chatActivityEnterView.botKeyboardView != null) {
                chatActivityEnterView.botKeyboardView.invalidateViews();
            }
        } else {
            int i2 = 0;
            if (i == NotificationCenter.recordProgressChanged) {
                long t = ((Long) args[0]).longValue();
                long time = t / 1000;
                int ms = ((int) (t % 1000)) / 10;
                r5 = new Object[3];
                r5[0] = Long.valueOf(time / 60);
                r5[1] = Long.valueOf(time % 60);
                r5[2] = Integer.valueOf(ms);
                String str = String.format("%02d:%02d.%02d", r5);
                if (chatActivityEnterView.lastTimeString == null || !chatActivityEnterView.lastTimeString.equals(str)) {
                    if (chatActivityEnterView.lastTypingSendTime != time && time % 5 == 0) {
                        chatActivityEnterView.lastTypingSendTime = time;
                        MessagesController instance = MessagesController.getInstance(chatActivityEnterView.currentAccount);
                        long j = chatActivityEnterView.dialog_id;
                        int i3 = (chatActivityEnterView.videoSendButton == null || chatActivityEnterView.videoSendButton.getTag() == null) ? 1 : 7;
                        instance.sendTyping(j, i3, 0);
                    }
                    if (chatActivityEnterView.recordTimeText != null) {
                        chatActivityEnterView.recordTimeText.setText(str);
                    }
                }
                if (chatActivityEnterView.recordCircle != null) {
                    chatActivityEnterView.recordCircle.setAmplitude(((Double) args[1]).doubleValue());
                }
                if (!(chatActivityEnterView.videoSendButton == null || chatActivityEnterView.videoSendButton.getTag() == null || t < 59500)) {
                    chatActivityEnterView.startedDraggingX = -1.0f;
                    chatActivityEnterView.delegate.needStartRecordVideo(3);
                }
            } else if (i != NotificationCenter.closeChats) {
                Integer mid;
                if (i != NotificationCenter.recordStartError) {
                    if (i != NotificationCenter.recordStopped) {
                        if (i == NotificationCenter.recordStarted) {
                            if (!chatActivityEnterView.recordingAudioVideo) {
                                chatActivityEnterView.recordingAudioVideo = true;
                                updateRecordIntefrace();
                            }
                        } else if (i == NotificationCenter.audioDidSent) {
                            Object audio = args[0];
                            if (audio instanceof VideoEditedInfo) {
                                chatActivityEnterView.videoToSendMessageObject = (VideoEditedInfo) audio;
                                chatActivityEnterView.audioToSendPath = (String) args[1];
                                chatActivityEnterView.videoTimelineView.setVideoPath(chatActivityEnterView.audioToSendPath);
                                chatActivityEnterView.videoTimelineView.setVisibility(0);
                                chatActivityEnterView.videoTimelineView.setMinProgressDiff(1000.0f / ((float) chatActivityEnterView.videoToSendMessageObject.estimatedDuration));
                                chatActivityEnterView.recordedAudioBackground.setVisibility(8);
                                chatActivityEnterView.recordedAudioTimeTextView.setVisibility(8);
                                chatActivityEnterView.recordedAudioPlayButton.setVisibility(8);
                                chatActivityEnterView.recordedAudioSeekBar.setVisibility(8);
                                chatActivityEnterView.recordedAudioPanel.setAlpha(1.0f);
                                chatActivityEnterView.recordedAudioPanel.setVisibility(0);
                                closeKeyboard();
                                hidePopup(false);
                                checkSendButton(false);
                            } else {
                                chatActivityEnterView.audioToSend = (TL_document) args[0];
                                chatActivityEnterView.audioToSendPath = (String) args[1];
                                if (chatActivityEnterView.audioToSend != null) {
                                    if (chatActivityEnterView.recordedAudioPanel != null) {
                                        int a;
                                        DocumentAttribute attribute;
                                        chatActivityEnterView.videoTimelineView.setVisibility(8);
                                        chatActivityEnterView.recordedAudioBackground.setVisibility(0);
                                        chatActivityEnterView.recordedAudioTimeTextView.setVisibility(0);
                                        chatActivityEnterView.recordedAudioPlayButton.setVisibility(0);
                                        chatActivityEnterView.recordedAudioSeekBar.setVisibility(0);
                                        TL_message message = new TL_message();
                                        message.out = true;
                                        message.id = 0;
                                        message.to_id = new TL_peerUser();
                                        Peer peer = message.to_id;
                                        int clientUserId = UserConfig.getInstance(chatActivityEnterView.currentAccount).getClientUserId();
                                        message.from_id = clientUserId;
                                        peer.user_id = clientUserId;
                                        message.date = (int) (System.currentTimeMillis() / 1000);
                                        message.message = TtmlNode.ANONYMOUS_REGION_ID;
                                        message.attachPath = chatActivityEnterView.audioToSendPath;
                                        message.media = new TL_messageMediaDocument();
                                        MessageMedia messageMedia = message.media;
                                        messageMedia.flags |= 3;
                                        message.media.document = chatActivityEnterView.audioToSend;
                                        message.flags |= 768;
                                        chatActivityEnterView.audioToSendMessageObject = new MessageObject(UserConfig.selectedAccount, message, false);
                                        chatActivityEnterView.recordedAudioPanel.setAlpha(1.0f);
                                        chatActivityEnterView.recordedAudioPanel.setVisibility(0);
                                        int duration = 0;
                                        for (a = 0; a < chatActivityEnterView.audioToSend.attributes.size(); a++) {
                                            attribute = (DocumentAttribute) chatActivityEnterView.audioToSend.attributes.get(a);
                                            if (attribute instanceof TL_documentAttributeAudio) {
                                                duration = attribute.duration;
                                                break;
                                            }
                                        }
                                        a = 0;
                                        while (a < chatActivityEnterView.audioToSend.attributes.size()) {
                                            attribute = (DocumentAttribute) chatActivityEnterView.audioToSend.attributes.get(a);
                                            if (attribute instanceof TL_documentAttributeAudio) {
                                                if (attribute.waveform == null || attribute.waveform.length == 0) {
                                                    attribute.waveform = MediaController.getInstance().getWaveform(chatActivityEnterView.audioToSendPath);
                                                }
                                                chatActivityEnterView.recordedAudioSeekBar.setWaveform(attribute.waveform);
                                                chatActivityEnterView.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)}));
                                                closeKeyboard();
                                                hidePopup(false);
                                                checkSendButton(false);
                                            } else {
                                                a++;
                                            }
                                        }
                                        chatActivityEnterView.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)}));
                                        closeKeyboard();
                                        hidePopup(false);
                                        checkSendButton(false);
                                    } else {
                                        return;
                                    }
                                } else if (chatActivityEnterView.delegate != null) {
                                    chatActivityEnterView.delegate.onMessageSend(null);
                                }
                            }
                        } else if (i == NotificationCenter.audioRouteChanged) {
                            if (chatActivityEnterView.parentActivity != null) {
                                boolean frontSpeaker = ((Boolean) args[0]).booleanValue();
                                Activity activity = chatActivityEnterView.parentActivity;
                                if (!frontSpeaker) {
                                    i2 = Integer.MIN_VALUE;
                                }
                                activity.setVolumeControlStream(i2);
                            }
                        } else if (i == NotificationCenter.messagePlayingDidReset) {
                            if (!(chatActivityEnterView.audioToSendMessageObject == null || MediaController.getInstance().isPlayingMessage(chatActivityEnterView.audioToSendMessageObject))) {
                                chatActivityEnterView.recordedAudioPlayButton.setImageDrawable(chatActivityEnterView.playDrawable);
                                chatActivityEnterView.recordedAudioSeekBar.setProgress(0.0f);
                            }
                        } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
                            mid = args[0];
                            if (chatActivityEnterView.audioToSendMessageObject != null && MediaController.getInstance().isPlayingMessage(chatActivityEnterView.audioToSendMessageObject)) {
                                MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                                chatActivityEnterView.audioToSendMessageObject.audioProgress = player.audioProgress;
                                chatActivityEnterView.audioToSendMessageObject.audioProgressSec = player.audioProgressSec;
                                if (!chatActivityEnterView.recordedAudioSeekBar.isDragging()) {
                                    chatActivityEnterView.recordedAudioSeekBar.setProgress(chatActivityEnterView.audioToSendMessageObject.audioProgress);
                                }
                            }
                        } else if (i == NotificationCenter.featuredStickersDidLoaded && chatActivityEnterView.emojiButton != null) {
                            chatActivityEnterView.emojiButton.invalidate();
                        }
                    }
                }
                if (chatActivityEnterView.recordingAudioVideo) {
                    MessagesController.getInstance(chatActivityEnterView.currentAccount).sendTyping(chatActivityEnterView.dialog_id, 2, 0);
                    chatActivityEnterView.recordingAudioVideo = false;
                    updateRecordIntefrace();
                }
                if (i == NotificationCenter.recordStopped) {
                    mid = args[0];
                    if (mid.intValue() == 2) {
                        chatActivityEnterView.videoTimelineView.setVisibility(0);
                        chatActivityEnterView.recordedAudioBackground.setVisibility(8);
                        chatActivityEnterView.recordedAudioTimeTextView.setVisibility(8);
                        chatActivityEnterView.recordedAudioPlayButton.setVisibility(8);
                        chatActivityEnterView.recordedAudioSeekBar.setVisibility(8);
                        chatActivityEnterView.recordedAudioPanel.setAlpha(1.0f);
                        chatActivityEnterView.recordedAudioPanel.setVisibility(0);
                    } else {
                        mid.intValue();
                    }
                }
            } else if (chatActivityEnterView.messageEditText != null && chatActivityEnterView.messageEditText.isFocused()) {
                AndroidUtilities.hideKeyboard(chatActivityEnterView.messageEditText);
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

    private void setStickersExpanded(boolean expanded, boolean animated) {
        boolean z = expanded;
        if (this.emojiView != null && (!z || r0.emojiView.areThereAnyStickers())) {
            if (r0.stickersExpanded != z) {
                r0.stickersExpanded = z;
                if (r0.delegate != null) {
                    r0.delegate.onStickersExpandedChange();
                }
                final int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? r0.keyboardHeightLand : r0.keyboardHeight;
                if (r0.stickersExpansionAnim != null) {
                    r0.stickersExpansionAnim.cancel();
                    r0.stickersExpansionAnim = null;
                }
                Animator[] animatorArr;
                if (r0.stickersExpanded) {
                    r0.stickersExpandedHeight = (((r0.sizeNotifierLayout.getHeight() - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    r0.emojiView.getLayoutParams().height = r0.stickersExpandedHeight;
                    r0.sizeNotifierLayout.requestLayout();
                    r0.sizeNotifierLayout.setForeground(new ScrimDrawable());
                    r0.messageEditText.setText(r0.messageEditText.getText());
                    if (animated) {
                        AnimatorSet anims = new AnimatorSet();
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofInt(r0, r0.roundedTranslationYProperty, new int[]{-(r0.stickersExpandedHeight - origHeight)});
                        animatorArr[1] = ObjectAnimator.ofInt(r0.emojiView, r0.roundedTranslationYProperty, new int[]{-(r0.stickersExpandedHeight - origHeight)});
                        animatorArr[2] = ObjectAnimator.ofFloat(r0.stickersArrow, "animationProgress", new float[]{1.0f});
                        anims.playTogether(animatorArr);
                        anims.setDuration(400);
                        anims.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                        ((ObjectAnimator) anims.getChildAnimations().get(0)).addUpdateListener(new AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator animation) {
                                ChatActivityEnterView.this.stickersExpansionProgress = ChatActivityEnterView.this.getTranslationY() / ((float) (-(ChatActivityEnterView.this.stickersExpandedHeight - origHeight)));
                                ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                            }
                        });
                        anims.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                ChatActivityEnterView.this.stickersExpansionAnim = null;
                                ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                            }
                        });
                        r0.stickersExpansionAnim = anims;
                        r0.emojiView.setLayerType(2, null);
                        anims.start();
                    } else {
                        r0.stickersExpansionProgress = 1.0f;
                        setTranslationY((float) (-(r0.stickersExpandedHeight - origHeight)));
                        r0.emojiView.setTranslationY((float) (-(r0.stickersExpandedHeight - origHeight)));
                        r0.stickersArrow.setAnimationProgress(1.0f);
                    }
                } else if (animated) {
                    r0.closeAnimationInProgress = true;
                    AnimatorSet anims2 = new AnimatorSet();
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofInt(r0, r0.roundedTranslationYProperty, new int[]{0});
                    animatorArr[1] = ObjectAnimator.ofInt(r0.emojiView, r0.roundedTranslationYProperty, new int[]{0});
                    animatorArr[2] = ObjectAnimator.ofFloat(r0.stickersArrow, "animationProgress", new float[]{0.0f});
                    anims2.playTogether(animatorArr);
                    anims2.setDuration(400);
                    anims2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    ((ObjectAnimator) anims2.getChildAnimations().get(0)).addUpdateListener(new AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            ChatActivityEnterView.this.stickersExpansionProgress = ChatActivityEnterView.this.getTranslationY() / ((float) (-(ChatActivityEnterView.this.stickersExpandedHeight - origHeight)));
                            ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                        }
                    });
                    anims2.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            ChatActivityEnterView.this.closeAnimationInProgress = false;
                            ChatActivityEnterView.this.stickersExpansionAnim = null;
                            ChatActivityEnterView.this.emojiView.getLayoutParams().height = origHeight;
                            ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                            ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                            ChatActivityEnterView.this.sizeNotifierLayout.setForeground(null);
                            ChatActivityEnterView.this.sizeNotifierLayout.setWillNotDraw(false);
                        }
                    });
                    r0.stickersExpansionAnim = anims2;
                    r0.emojiView.setLayerType(2, null);
                    anims2.start();
                } else {
                    r0.stickersExpansionProgress = 0.0f;
                    setTranslationY(0.0f);
                    r0.emojiView.setTranslationY(0.0f);
                    r0.emojiView.getLayoutParams().height = origHeight;
                    r0.sizeNotifierLayout.requestLayout();
                    r0.sizeNotifierLayout.setForeground(null);
                    r0.sizeNotifierLayout.setWillNotDraw(false);
                    r0.stickersArrow.setAnimationProgress(0.0f);
                }
            }
        }
    }
}
