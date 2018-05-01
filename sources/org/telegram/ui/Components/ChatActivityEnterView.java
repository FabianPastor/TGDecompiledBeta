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
import java.util.Collection;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
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
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
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
                int currentPage = ChatActivityEnterView.this.emojiView.getCurrentPage();
                if (currentPage != this.lastKnownPage) {
                    boolean z;
                    this.lastKnownPage = currentPage;
                    boolean access$800 = ChatActivityEnterView.this.stickersTabOpen;
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    if (currentPage != 1) {
                        if (currentPage != 2) {
                            z = false;
                            chatActivityEnterView.stickersTabOpen = z;
                            if (access$800 != ChatActivityEnterView.this.stickersTabOpen) {
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
                    if (access$800 != ChatActivityEnterView.this.stickersTabOpen) {
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
                            boolean z = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0;
                            boolean z2 = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.CAMERA") == 0;
                            if (!(z && z2)) {
                                int i = (z || z2) ? 1 : 2;
                                String[] strArr = new String[i];
                                if (!z && !z2) {
                                    strArr[0] = "android.permission.RECORD_AUDIO";
                                    strArr[1] = "android.permission.CAMERA";
                                } else if (z) {
                                    strArr[0] = "android.permission.CAMERA";
                                } else {
                                    strArr[0] = "android.permission.RECORD_AUDIO";
                                }
                                ChatActivityEnterView.this.parentActivity.requestPermissions(strArr, 3);
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
            if (ChatActivityEnterView.this.isPopupShowing() != null) {
                if (ChatActivityEnterView.this.currentPopupContentType == null) {
                    if (ChatActivityEnterView.this.searchingStickers != null) {
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
            view = ChatActivityEnterView.this.emojiView;
            if (ChatActivityEnterView.this.messageEditText.length() > 0 && !ChatActivityEnterView.this.messageEditText.getText().toString().startsWith("@gif")) {
                z = true;
            }
            view.onOpen(z);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$8 */
    class C11018 implements OnKeyListener {
        boolean ctrlPressed = null;

        C11018() {
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            view = null;
            if (i == 4 && !ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing()) {
                if (keyEvent.getAction() == 1) {
                    if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != 0) {
                        i = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit();
                        keyEvent = new StringBuilder();
                        keyEvent.append("hidekeyboard_");
                        keyEvent.append(ChatActivityEnterView.this.dialog_id);
                        i.putInt(keyEvent.toString(), ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                    }
                    if (ChatActivityEnterView.this.searchingStickers != 0) {
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
                    view = 1;
                }
                this.ctrlPressed = view;
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$9 */
    class C11029 implements OnEditorActionListener {
        boolean ctrlPressed = null;

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

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public AnimatedArrowDrawable(int i) {
            this.paint.setStyle(Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.paint.setColor(i);
            updatePath();
        }

        public void draw(Canvas canvas) {
            canvas.drawPath(this.path, this.paint);
        }

        private void updatePath() {
            this.path.reset();
            float f = (this.animProgress * 2.0f) - 1.0f;
            this.path.moveTo((float) AndroidUtilities.dp(3.0f), ((float) AndroidUtilities.dp(12.0f)) - (((float) AndroidUtilities.dp(4.0f)) * f));
            this.path.lineTo((float) AndroidUtilities.dp(13.0f), ((float) AndroidUtilities.dp(12.0f)) + (((float) AndroidUtilities.dp(4.0f)) * f));
            this.path.lineTo((float) AndroidUtilities.dp(23.0f), ((float) AndroidUtilities.dp(12.0f)) - (((float) AndroidUtilities.dp(4.0f)) * f));
        }

        @Keep
        public void setAnimationProgress(float f) {
            this.animProgress = f;
            updatePath();
            invalidateSelf();
        }

        public float getAnimationProgress() {
            return this.animProgress;
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
            ChatActivityEnterView.this.lockDrawable = getResources().getDrawable(C0446R.drawable.lock_middle);
            ChatActivityEnterView.this.lockDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLock), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockTopDrawable = getResources().getDrawable(C0446R.drawable.lock_top);
            ChatActivityEnterView.this.lockTopDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLock), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockArrowDrawable = getResources().getDrawable(C0446R.drawable.lock_arrow);
            ChatActivityEnterView.this.lockArrowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLock), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockBackgroundDrawable = getResources().getDrawable(C0446R.drawable.lock_round);
            ChatActivityEnterView.this.lockBackgroundDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLockBackground), Mode.MULTIPLY));
            ChatActivityEnterView.this.lockShadowDrawable = getResources().getDrawable(C0446R.drawable.lock_round_shadow);
            ChatActivityEnterView.this.lockShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLockShadow), Mode.MULTIPLY));
            ChatActivityEnterView.this.micDrawable = getResources().getDrawable(C0446R.drawable.mic).mutate();
            ChatActivityEnterView.this.micDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), Mode.MULTIPLY));
            ChatActivityEnterView.this.cameraDrawable = getResources().getDrawable(C0446R.drawable.ic_msg_panel_video).mutate();
            ChatActivityEnterView.this.cameraDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), Mode.MULTIPLY));
            ChatActivityEnterView.this.sendDrawable = getResources().getDrawable(C0446R.drawable.ic_send).mutate();
            ChatActivityEnterView.this.sendDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), Mode.MULTIPLY));
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
                    motionEvent = ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains(x, y);
                    this.pressed = motionEvent;
                    if (motionEvent != null) {
                        return true;
                    }
                } else if (this.pressed) {
                    if (motionEvent.getAction() == 1 && ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains(x, y) != null) {
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
            float max;
            float f;
            float f2;
            int dp;
            int dp2;
            int dp3;
            int dp4;
            int i;
            Canvas canvas2 = canvas;
            int measuredWidth = getMeasuredWidth() / 2;
            int dp5 = AndroidUtilities.dp(170.0f);
            if (this.lockAnimatedTranslation != 10000.0f) {
                max = (float) Math.max(0, (int) (r0.startTranslation - r0.lockAnimatedTranslation));
                if (max > ((float) AndroidUtilities.dp(57.0f))) {
                    max = (float) AndroidUtilities.dp(57.0f);
                }
            } else {
                max = 0.0f;
            }
            dp5 = (int) (((float) dp5) - max);
            if (r0.scale <= 0.5f) {
                f = r0.scale / 0.5f;
                f2 = f;
            } else {
                if (r0.scale <= 0.75f) {
                    f = 1.0f - (((r0.scale - 0.5f) / 0.25f) * 0.1f);
                } else {
                    f = 0.9f + (((r0.scale - 0.75f) / 0.25f) * 0.1f);
                }
                f2 = 1.0f;
            }
            long currentTimeMillis = System.currentTimeMillis() - r0.lastUpdateTime;
            if (r0.animateToAmplitude != r0.amplitude) {
                r0.amplitude += r0.animateAmplitudeDiff * ((float) currentTimeMillis);
                if (r0.animateAmplitudeDiff > 0.0f) {
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
                canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) dp5, (((float) AndroidUtilities.dp(42.0f)) + (((float) AndroidUtilities.dp(20.0f)) * r0.amplitude)) * r0.scale, ChatActivityEnterView.this.paintRecord);
            }
            canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) dp5, ((float) AndroidUtilities.dp(42.0f)) * f, ChatActivityEnterView.this.paint);
            Drawable access$3800 = isSendButtonVisible() ? ChatActivityEnterView.this.sendDrawable : (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
            access$3800.setBounds(measuredWidth - (access$3800.getIntrinsicWidth() / 2), dp5 - (access$3800.getIntrinsicHeight() / 2), (access$3800.getIntrinsicWidth() / 2) + measuredWidth, dp5 + (access$3800.getIntrinsicHeight() / 2));
            dp5 = (int) (255.0f * f2);
            access$3800.setAlpha(dp5);
            access$3800.draw(canvas2);
            float dp6 = 1.0f - (max / ((float) AndroidUtilities.dp(57.0f)));
            f2 = Math.max(0.0f, 1.0f - ((max / ((float) AndroidUtilities.dp(57.0f))) * 2.0f));
            if (isSendButtonVisible()) {
                dp = AndroidUtilities.dp(31.0f);
                dp2 = AndroidUtilities.dp(57.0f) + ((int) (((((float) AndroidUtilities.dp(30.0f)) * (1.0f - f)) - max) + (((float) AndroidUtilities.dp(20.0f)) * dp6)));
                int dp7 = AndroidUtilities.dp(5.0f) + dp2;
                dp3 = AndroidUtilities.dp(11.0f) + dp2;
                dp4 = AndroidUtilities.dp(25.0f) + dp2;
                dp5 = (int) (((float) dp5) * (max / ((float) AndroidUtilities.dp(57.0f))));
                ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(255);
                ChatActivityEnterView.this.lockShadowDrawable.setAlpha(255);
                ChatActivityEnterView.this.lockTopDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) dp5) * f2));
                i = dp7;
            } else {
                dp = AndroidUtilities.dp(31.0f) + ((int) (((float) AndroidUtilities.dp(29.0f)) * dp6));
                dp2 = (AndroidUtilities.dp(57.0f) + ((int) (((float) AndroidUtilities.dp(30.0f)) * (1.0f - f)))) - ((int) max);
                i = (AndroidUtilities.dp(5.0f) + dp2) + ((int) (((float) AndroidUtilities.dp(4.0f)) * dp6));
                dp3 = ((int) (((float) AndroidUtilities.dp(10.0f)) * dp6)) + (AndroidUtilities.dp(11.0f) + dp2);
                dp4 = (AndroidUtilities.dp(25.0f) + dp2) + ((int) (((float) AndroidUtilities.dp(16.0f)) * dp6));
                ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockShadowDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockTopDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockDrawable.setAlpha(dp5);
                ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) dp5) * f2));
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
            this.paint.setAlpha(Math.round(102.0f * ChatActivityEnterView.this.stickersExpansionProgress));
            canvas.drawRect(0.0f, 0.0f, (float) ChatActivityEnterView.this.getWidth(), (ChatActivityEnterView.this.emojiView.getY() - ((float) ChatActivityEnterView.this.getHeight())) + ((float) Theme.chat_composeShadowDrawable.getIntrinsicHeight()), this.paint);
        }
    }

    private class SeekBarWaveformView extends View {
        public SeekBarWaveformView(Context context) {
            super(context);
            ChatActivityEnterView.this.seekBarWaveform = new SeekBarWaveform(context);
            ChatActivityEnterView.this.seekBarWaveform.setDelegate(new SeekBarDelegate(ChatActivityEnterView.this) {
                public void onSeekBarDrag(float f) {
                    if (ChatActivityEnterView.this.audioToSendMessageObject != null) {
                        ChatActivityEnterView.this.audioToSendMessageObject.audioProgress = f;
                        MediaController.getInstance().seekToProgress(ChatActivityEnterView.this.audioToSendMessageObject, f);
                    }
                }
            });
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
            if (onTouch) {
                return true;
            }
            if (super.onTouchEvent(motionEvent) != null) {
                return true;
            }
            return false;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ChatActivityEnterView.this.seekBarWaveform.setSize(i3 - i, i4 - i2);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            ChatActivityEnterView.this.seekBarWaveform.setColors(Theme.getColor(Theme.key_chat_recordedVoiceProgress), Theme.getColor(Theme.key_chat_recordedVoiceProgressInner), Theme.getColor(Theme.key_chat_recordedVoiceProgress));
            ChatActivityEnterView.this.seekBarWaveform.draw(canvas);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ChatActivityEnterView(Activity activity, SizeNotifierFrameLayout sizeNotifierFrameLayout, ChatActivity chatActivity, boolean z) {
        Context context = activity;
        super(activity);
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
        this.parentActivity = context;
        this.parentFragment = chatActivity;
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        this.sizeNotifierLayout.setDelegate(this);
        this.sendByEnter = MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false);
        this.textFieldContainer = new LinearLayout(context);
        this.textFieldContainer.setOrientation(0);
        addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 2.0f, 0.0f, 0.0f));
        View frameLayout = new FrameLayout(context);
        this.textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        this.emojiButton = new ImageView(context) {
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
        this.messageEditText = new EditTextCaption(context) {

            /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$7$1 */
            class C20431 implements OnCommitContentListener {
                C20431() {
                }

                public boolean onCommitContent(android.support.v13.view.inputmethod.InputContentInfoCompat r20, int r21, android.os.Bundle r22) {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                    /*
                    r19 = this;
                    r0 = r19;
                    r1 = android.support.v4.os.BuildCompat.isAtLeastNMR1();
                    if (r1 == 0) goto L_0x0014;
                L_0x0008:
                    r1 = android.support.v13.view.inputmethod.InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
                    r1 = r21 & r1;
                    if (r1 == 0) goto L_0x0014;
                L_0x000e:
                    r20.requestPermission();	 Catch:{ Exception -> 0x0012 }
                    goto L_0x0014;
                L_0x0012:
                    r1 = 0;
                    return r1;
                L_0x0014:
                    r1 = r20.getDescription();
                    r2 = "image/gif";
                    r1 = r1.hasMimeType(r2);
                    if (r1 == 0) goto L_0x003e;
                L_0x0020:
                    r2 = 0;
                    r3 = 0;
                    r4 = r20.getContentUri();
                    r5 = "image/gif";
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.C23367.this;
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.this;
                    r6 = r1.dialog_id;
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.C23367.this;
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.this;
                    r8 = r1.replyingMessageObject;
                    r9 = r20;
                    org.telegram.messenger.SendMessagesHelper.prepareSendingDocument(r2, r3, r4, r5, r6, r8, r9);
                    goto L_0x005e;
                L_0x003e:
                    r9 = 0;
                    r10 = r20.getContentUri();
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.C23367.this;
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.this;
                    r11 = r1.dialog_id;
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.C23367.this;
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.this;
                    r13 = r1.replyingMessageObject;
                    r14 = 0;
                    r15 = 0;
                    r16 = 0;
                    r18 = 0;
                    r17 = r20;
                    org.telegram.messenger.SendMessagesHelper.prepareSendingPhoto(r9, r10, r11, r13, r14, r15, r16, r17, r18);
                L_0x005e:
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.C23367.this;
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.this;
                    r1 = r1.delegate;
                    if (r1 == 0) goto L_0x0074;
                L_0x0068:
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.C23367.this;
                    r1 = org.telegram.ui.Components.ChatActivityEnterView.this;
                    r1 = r1.delegate;
                    r2 = 0;
                    r1.onMessageSend(r2);
                L_0x0074:
                    r1 = 1;
                    return r1;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.7.1.onCommitContent(android.support.v13.view.inputmethod.InputContentInfoCompat, int, android.os.Bundle):boolean");
                }
            }

            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
                EditorInfoCompat.setContentMimeTypes(editorInfo, new String[]{"image/gif", "image/*", "image/jpg", "image/png"});
                return InputConnectionCompat.createWrapper(onCreateInputConnection, editorInfo, new C20431());
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (ChatActivityEnterView.this.isPopupShowing() && motionEvent.getAction() == 0) {
                    if (ChatActivityEnterView.this.searchingStickers) {
                        ChatActivityEnterView.this.searchingStickers = false;
                        ChatActivityEnterView.this.emojiView.closeSearch(false);
                    }
                    ChatActivityEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 0);
                    ChatActivityEnterView.this.openKeyboardInternal();
                }
                try {
                    return super.onTouchEvent(motionEvent);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return false;
                }
            }
        };
        updateFieldHint();
        int i = (this.parentFragment == null || r0.parentFragment.getCurrentEncryptedChat() == null) ? 268435456 : 285212672;
        r0.messageEditText.setImeOptions(i);
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
        frameLayout.addView(r0.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 80, 52.0f, 0.0f, z ? 50.0f : 2.0f, 0.0f));
        r0.messageEditText.setOnKeyListener(new C11018());
        r0.messageEditText.setOnEditorActionListener(new C11029());
        r0.messageEditText.addTextChangedListener(new TextWatcher() {
            boolean processChange = null;

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (ChatActivityEnterView.this.innerTextChange != 1) {
                    ChatActivityEnterView.this.checkSendButton(true);
                    i = AndroidUtilities.getTrimmedString(charSequence.toString());
                    if (!(ChatActivityEnterView.this.delegate == null || ChatActivityEnterView.this.ignoreTextChange)) {
                        boolean z;
                        if (i3 > 2 || charSequence == null || charSequence.length() == 0) {
                            ChatActivityEnterView.this.messageWebPageSearch = true;
                        }
                        ChatActivityEnterViewDelegate access$1300 = ChatActivityEnterView.this.delegate;
                        if (i2 <= i3 + 1) {
                            if (i3 - i2 <= 2) {
                                z = false;
                                access$1300.onTextChanged(charSequence, z);
                            }
                        }
                        z = true;
                        access$1300.onTextChanged(charSequence, z);
                    }
                    if (!(ChatActivityEnterView.this.innerTextChange == 2 || i2 == i3 || i3 - i2 <= 1)) {
                        this.processChange = true;
                    }
                    if (ChatActivityEnterView.this.editingMessageObject == null && ChatActivityEnterView.this.canWriteToChannel == null && i.length() != null && ChatActivityEnterView.this.lastTypingTimeSend < System.currentTimeMillis() - DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS && ChatActivityEnterView.this.ignoreTextChange == null) {
                        charSequence = ConnectionsManager.getInstance(ChatActivityEnterView.this.currentAccount).getCurrentTime();
                        i = 0;
                        if (((int) ChatActivityEnterView.this.dialog_id) > 0) {
                            i = MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).getUser(Integer.valueOf((int) ChatActivityEnterView.this.dialog_id));
                        }
                        if (i == 0 || (i.id != UserConfig.getInstance(ChatActivityEnterView.this.currentAccount).getClientUserId() && (i.status == 0 || i.status.expires >= charSequence || MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(i.id)) != null))) {
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
            r0.attachLayout = new LinearLayout(context);
            r0.attachLayout.setOrientation(0);
            r0.attachLayout.setEnabled(false);
            r0.attachLayout.setPivotX((float) AndroidUtilities.dp(48.0f));
            frameLayout.addView(r0.attachLayout, LayoutHelper.createFrame(-2, 48, 85));
            r0.botButton = new ImageView(context);
            r0.botButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
            r0.botButton.setImageResource(C0446R.drawable.bot_keyboard2);
            r0.botButton.setScaleType(ScaleType.CENTER);
            r0.botButton.setVisibility(8);
            r0.attachLayout.addView(r0.botButton, LayoutHelper.createLinear(48, 48));
            r0.botButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (ChatActivityEnterView.this.searchingStickers != null) {
                        ChatActivityEnterView.this.searchingStickers = false;
                        ChatActivityEnterView.this.emojiView.closeSearch(false);
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                    }
                    if (ChatActivityEnterView.this.botReplyMarkup != null) {
                        StringBuilder stringBuilder;
                        if (ChatActivityEnterView.this.isPopupShowing() != null) {
                            if (ChatActivityEnterView.this.currentPopupContentType == 1) {
                                if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                                    view = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit();
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("hidekeyboard_");
                                    stringBuilder.append(ChatActivityEnterView.this.dialog_id);
                                    view.putInt(stringBuilder.toString(), ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                                }
                                ChatActivityEnterView.this.openKeyboardInternal();
                            }
                        }
                        ChatActivityEnterView.this.showPopup(1, 1);
                        view = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("hidekeyboard_");
                        stringBuilder.append(ChatActivityEnterView.this.dialog_id);
                        view.remove(stringBuilder.toString()).commit();
                    } else if (ChatActivityEnterView.this.hasBotCommands != null) {
                        ChatActivityEnterView.this.setFieldText("/");
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                        ChatActivityEnterView.this.openKeyboard();
                    }
                    if (ChatActivityEnterView.this.stickersExpanded != null) {
                        ChatActivityEnterView.this.setStickersExpanded(false, false);
                    }
                }
            });
            r0.notifyButton = new ImageView(context);
            r0.notifyButton.setImageResource(r0.silent ? C0446R.drawable.notify_members_off : C0446R.drawable.notify_members_on);
            r0.notifyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
            r0.notifyButton.setScaleType(ScaleType.CENTER);
            r0.notifyButton.setVisibility(r0.canWriteToChannel ? 0 : 8);
            r0.attachLayout.addView(r0.notifyButton, LayoutHelper.createLinear(48, 48));
            r0.notifyButton.setOnClickListener(new OnClickListener() {
                private Toast visibleToast;

                public void onClick(View view) {
                    ChatActivityEnterView.this.silent = ChatActivityEnterView.this.silent ^ 1;
                    ChatActivityEnterView.this.notifyButton.setImageResource(ChatActivityEnterView.this.silent ? C0446R.drawable.notify_members_off : C0446R.drawable.notify_members_on);
                    view = MessagesController.getNotificationsSettings(ChatActivityEnterView.this.currentAccount).edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("silent_");
                    stringBuilder.append(ChatActivityEnterView.this.dialog_id);
                    view.putBoolean(stringBuilder.toString(), ChatActivityEnterView.this.silent).commit();
                    NotificationsController.getInstance(ChatActivityEnterView.this.currentAccount).updateServerNotificationsSettings(ChatActivityEnterView.this.dialog_id);
                    try {
                        if (this.visibleToast != null) {
                            this.visibleToast.cancel();
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    if (ChatActivityEnterView.this.silent != null) {
                        this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOff", C0446R.string.ChannelNotifyMembersInfoOff), 0);
                        this.visibleToast.show();
                    } else {
                        this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOn", C0446R.string.ChannelNotifyMembersInfoOn), 0);
                        this.visibleToast.show();
                    }
                    ChatActivityEnterView.this.updateFieldHint();
                }
            });
            r0.attachButton = new ImageView(context);
            r0.attachButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
            r0.attachButton.setImageResource(C0446R.drawable.ic_ab_attach);
            r0.attachButton.setScaleType(ScaleType.CENTER);
            r0.attachLayout.addView(r0.attachButton, LayoutHelper.createLinear(48, 48));
            r0.attachButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ChatActivityEnterView.this.delegate.didPressedAttachButton();
                }
            });
        }
        r0.recordedAudioPanel = new FrameLayout(context);
        r0.recordedAudioPanel.setVisibility(r0.audioToSend == null ? 8 : 0);
        r0.recordedAudioPanel.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        r0.recordedAudioPanel.setFocusable(true);
        r0.recordedAudioPanel.setFocusableInTouchMode(true);
        r0.recordedAudioPanel.setClickable(true);
        frameLayout.addView(r0.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        r0.recordDeleteImageView = new ImageView(context);
        r0.recordDeleteImageView.setScaleType(ScaleType.CENTER);
        r0.recordDeleteImageView.setImageResource(C0446R.drawable.ic_ab_delete);
        r0.recordDeleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceDelete), Mode.MULTIPLY));
        r0.recordedAudioPanel.addView(r0.recordDeleteImageView, LayoutHelper.createFrame(48, 48.0f));
        r0.recordDeleteImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ChatActivityEnterView.this.videoToSendMessageObject != null) {
                    ChatActivityEnterView.this.delegate.needStartRecordVideo(2);
                } else {
                    view = MediaController.getInstance().getPlayingMessageObject();
                    if (view != null && view == ChatActivityEnterView.this.audioToSendMessageObject) {
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
        r0.videoTimelineView = new VideoTimelineView(context);
        r0.videoTimelineView.setColor(-11817481);
        r0.videoTimelineView.setRoundFrames(true);
        r0.videoTimelineView.setDelegate(new VideoTimelineViewDelegate() {
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
        r0.recordedAudioPanel.addView(r0.videoTimelineView, LayoutHelper.createFrame(-1, 32.0f, 19, 40.0f, 0.0f, 0.0f, 0.0f));
        r0.recordedAudioBackground = new View(context);
        r0.recordedAudioBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(16.0f), Theme.getColor(Theme.key_chat_recordedVoiceBackground)));
        r0.recordedAudioPanel.addView(r0.recordedAudioBackground, LayoutHelper.createFrame(-1, 36.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        r0.recordedAudioSeekBar = new SeekBarWaveformView(context);
        r0.recordedAudioPanel.addView(r0.recordedAudioSeekBar, LayoutHelper.createFrame(-1, 32.0f, 19, 92.0f, 0.0f, 52.0f, 0.0f));
        r0.playDrawable = Theme.createSimpleSelectorDrawable(context, C0446R.drawable.s_play, Theme.getColor(Theme.key_chat_recordedVoicePlayPause), Theme.getColor(Theme.key_chat_recordedVoicePlayPausePressed));
        r0.pauseDrawable = Theme.createSimpleSelectorDrawable(context, C0446R.drawable.s_pause, Theme.getColor(Theme.key_chat_recordedVoicePlayPause), Theme.getColor(Theme.key_chat_recordedVoicePlayPausePressed));
        r0.recordedAudioPlayButton = new ImageView(context);
        r0.recordedAudioPlayButton.setImageDrawable(r0.playDrawable);
        r0.recordedAudioPlayButton.setScaleType(ScaleType.CENTER);
        r0.recordedAudioPanel.addView(r0.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0f, 83, 48.0f, 0.0f, 0.0f, 0.0f));
        r0.recordedAudioPlayButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ChatActivityEnterView.this.audioToSend != null) {
                    if (MediaController.getInstance().isPlayingMessage(ChatActivityEnterView.this.audioToSendMessageObject) == null || MediaController.getInstance().isMessagePaused() != null) {
                        ChatActivityEnterView.this.recordedAudioPlayButton.setImageDrawable(ChatActivityEnterView.this.pauseDrawable);
                        MediaController.getInstance().playMessage(ChatActivityEnterView.this.audioToSendMessageObject);
                    } else {
                        MediaController.getInstance().pauseMessage(ChatActivityEnterView.this.audioToSendMessageObject);
                        ChatActivityEnterView.this.recordedAudioPlayButton.setImageDrawable(ChatActivityEnterView.this.playDrawable);
                    }
                }
            }
        });
        r0.recordedAudioTimeTextView = new TextView(context);
        r0.recordedAudioTimeTextView.setTextColor(Theme.getColor(Theme.key_chat_messagePanelVoiceDuration));
        r0.recordedAudioTimeTextView.setTextSize(1, 13.0f);
        r0.recordedAudioPanel.addView(r0.recordedAudioTimeTextView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 13.0f, 0.0f));
        r0.recordPanel = new FrameLayout(context);
        r0.recordPanel.setVisibility(8);
        r0.recordPanel.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        frameLayout.addView(r0.recordPanel, LayoutHelper.createFrame(-1, 48, 80));
        r0.recordPanel.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        r0.slideText = new LinearLayout(context);
        r0.slideText.setOrientation(0);
        r0.recordPanel.addView(r0.slideText, LayoutHelper.createFrame(-2, -2.0f, 17, 30.0f, 0.0f, 0.0f, 0.0f));
        r0.recordCancelImage = new ImageView(context);
        r0.recordCancelImage.setImageResource(C0446R.drawable.slidearrow);
        r0.recordCancelImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_recordVoiceCancel), Mode.MULTIPLY));
        r0.slideText.addView(r0.recordCancelImage, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 0, 0));
        r0.recordCancelText = new TextView(context);
        r0.recordCancelText.setText(LocaleController.getString("SlideToCancel", C0446R.string.SlideToCancel));
        r0.recordCancelText.setTextColor(Theme.getColor(Theme.key_chat_recordVoiceCancel));
        r0.recordCancelText.setTextSize(1, 12.0f);
        r0.slideText.addView(r0.recordCancelText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        r0.recordSendText = new TextView(context);
        r0.recordSendText.setText(LocaleController.getString("Cancel", C0446R.string.Cancel).toUpperCase());
        r0.recordSendText.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        r0.recordSendText.setTextSize(1, 16.0f);
        r0.recordSendText.setGravity(17);
        r0.recordSendText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.recordSendText.setAlpha(0.0f);
        r0.recordSendText.setPadding(AndroidUtilities.dp(36.0f), 0, 0, 0);
        r0.recordSendText.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ChatActivityEnterView.this.hasRecordVideo == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
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
        r0.recordTimeContainer = new LinearLayout(context);
        r0.recordTimeContainer.setOrientation(0);
        r0.recordTimeContainer.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        r0.recordTimeContainer.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        r0.recordPanel.addView(r0.recordTimeContainer, LayoutHelper.createFrame(-2, -2, 16));
        r0.recordDot = new RecordDot(context);
        r0.recordTimeContainer.addView(r0.recordDot, LayoutHelper.createLinear(11, 11, 16, 0, 1, 0, 0));
        r0.recordTimeText = new TextView(context);
        r0.recordTimeText.setTextColor(Theme.getColor(Theme.key_chat_recordTime));
        r0.recordTimeText.setTextSize(1, 16.0f);
        r0.recordTimeContainer.addView(r0.recordTimeText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        r0.sendButtonContainer = new FrameLayout(context);
        r0.textFieldContainer.addView(r0.sendButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        r0.audioVideoButtonContainer = new FrameLayout(context);
        r0.audioVideoButtonContainer.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        r0.audioVideoButtonContainer.setSoundEffectsEnabled(false);
        r0.sendButtonContainer.addView(r0.audioVideoButtonContainer, LayoutHelper.createFrame(48, 48.0f));
        r0.audioVideoButtonContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
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
                                    view = new AnimatorSet();
                                    motionEvent = new Animator[5];
                                    motionEvent[0] = ObjectAnimator.ofFloat(ChatActivityEnterView.this.recordCircle, "lockAnimatedTranslation", new float[]{ChatActivityEnterView.this.recordCircle.startTranslation});
                                    motionEvent[1] = ObjectAnimator.ofFloat(ChatActivityEnterView.this.slideText, "alpha", new float[]{0.0f});
                                    motionEvent[2] = ObjectAnimator.ofFloat(ChatActivityEnterView.this.slideText, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f)});
                                    motionEvent[3] = ObjectAnimator.ofFloat(ChatActivityEnterView.this.recordSendText, "alpha", new float[]{1.0f});
                                    motionEvent[4] = ObjectAnimator.ofFloat(ChatActivityEnterView.this.recordSendText, "translationY", new float[]{(float) (-AndroidUtilities.dp(20.0f)), 0.0f});
                                    view.playTogether(motionEvent);
                                    view.setInterpolator(new DecelerateInterpolator());
                                    view.setDuration(150);
                                    view.start();
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
                                LayoutParams layoutParams = (LayoutParams) ChatActivityEnterView.this.slideText.getLayoutParams();
                                if (ChatActivityEnterView.this.startedDraggingX != -1.0f) {
                                    float access$2200 = x - ChatActivityEnterView.this.startedDraggingX;
                                    layoutParams.leftMargin = AndroidUtilities.dp(30.0f) + ((int) access$2200);
                                    ChatActivityEnterView.this.slideText.setLayoutParams(layoutParams);
                                    access$2200 = (access$2200 / ChatActivityEnterView.this.distCanMove) + 1.0f;
                                    if (access$2200 > 1.0f) {
                                        access$2200 = 1.0f;
                                    } else if (access$2200 < 0.0f) {
                                        access$2200 = 0.0f;
                                    }
                                    ChatActivityEnterView.this.slideText.setAlpha(access$2200);
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
                                if (layoutParams.leftMargin > AndroidUtilities.dp(30.0f)) {
                                    layoutParams.leftMargin = AndroidUtilities.dp(30.0f);
                                    ChatActivityEnterView.this.slideText.setLayoutParams(layoutParams);
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
                    if (ChatActivityEnterView.this.hasRecordVideo == null || ChatActivityEnterView.this.calledRecordRunnable != null) {
                        ChatActivityEnterView.this.startedDraggingX = -1.0f;
                        if (ChatActivityEnterView.this.hasRecordVideo == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
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
                        Chat currentChat = ChatActivityEnterView.this.parentFragment.getCurrentChat();
                        if (ChatObject.isChannel(currentChat) && currentChat.banned_rights != null && currentChat.banned_rights.send_media) {
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
        r0.audioSendButton = new ImageView(context);
        r0.audioSendButton.setScaleType(ScaleType.CENTER_INSIDE);
        r0.audioSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
        r0.audioSendButton.setImageResource(C0446R.drawable.mic);
        r0.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        r0.audioVideoButtonContainer.addView(r0.audioSendButton, LayoutHelper.createFrame(48, 48.0f));
        if (z) {
            r0.videoSendButton = new ImageView(context);
            r0.videoSendButton.setScaleType(ScaleType.CENTER_INSIDE);
            r0.videoSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
            r0.videoSendButton.setImageResource(C0446R.drawable.ic_msg_panel_video);
            r0.videoSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
            r0.audioVideoButtonContainer.addView(r0.videoSendButton, LayoutHelper.createFrame(48, 48.0f));
        }
        r0.recordCircle = new RecordCircle(context);
        r0.recordCircle.setVisibility(8);
        r0.sizeNotifierLayout.addView(r0.recordCircle, LayoutHelper.createFrame(124, 194.0f, 85, 0.0f, 0.0f, -36.0f, 0.0f));
        r0.cancelBotButton = new ImageView(context);
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
                view = ChatActivityEnterView.this.messageEditText.getText().toString();
                int indexOf = view.indexOf(32);
                if (indexOf != -1) {
                    if (indexOf != view.length() - 1) {
                        ChatActivityEnterView.this.setFieldText(view.substring(0, indexOf + 1));
                        return;
                    }
                }
                ChatActivityEnterView.this.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
            }
        });
        r0.sendButton = new ImageView(context);
        r0.sendButton.setVisibility(4);
        r0.sendButton.setScaleType(ScaleType.CENTER_INSIDE);
        r0.sendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelSend), Mode.MULTIPLY));
        r0.sendButton.setImageResource(C0446R.drawable.ic_send);
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
        r0.expandStickersButton = new ImageView(context);
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
            public void onClick(View view) {
                if (ChatActivityEnterView.this.expandStickersButton.getVisibility() == null) {
                    if (ChatActivityEnterView.this.expandStickersButton.getAlpha() == 1.0f) {
                        if (ChatActivityEnterView.this.stickersExpanded != null) {
                            if (ChatActivityEnterView.this.searchingStickers != null) {
                                ChatActivityEnterView.this.searchingStickers = false;
                                ChatActivityEnterView.this.emojiView.closeSearch(true);
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            } else if (ChatActivityEnterView.this.stickersDragging == null && ChatActivityEnterView.this.emojiView != null) {
                                ChatActivityEnterView.this.emojiView.showSearchField(false);
                            }
                        } else if (ChatActivityEnterView.this.stickersDragging == null) {
                            ChatActivityEnterView.this.emojiView.showSearchField(true);
                        }
                        if (ChatActivityEnterView.this.stickersDragging == null) {
                            ChatActivityEnterView.this.setStickersExpanded(ChatActivityEnterView.this.stickersExpanded ^ true, true);
                        }
                    }
                }
            }
        });
        r0.doneButtonContainer = new FrameLayout(context);
        r0.doneButtonContainer.setVisibility(8);
        r0.textFieldContainer.addView(r0.doneButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        r0.doneButtonContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ChatActivityEnterView.this.doneEditingMessage();
            }
        });
        r0.doneButtonImage = new ImageView(context);
        r0.doneButtonImage.setScaleType(ScaleType.CENTER);
        r0.doneButtonImage.setImageResource(C0446R.drawable.edit_done);
        r0.doneButtonImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_editDoneIcon), Mode.MULTIPLY));
        r0.doneButtonContainer.addView(r0.doneButtonImage, LayoutHelper.createFrame(48, 48.0f));
        r0.doneButtonProgress = new ContextProgressView(context, 0);
        r0.doneButtonProgress.setVisibility(4);
        r0.doneButtonContainer.addView(r0.doneButtonProgress, LayoutHelper.createFrame(-1, -1.0f));
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        r0.keyboardHeight = globalEmojiSettings.getInt("kbd_height", AndroidUtilities.dp(200.0f));
        r0.keyboardHeightLand = globalEmojiSettings.getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
        setRecordVideoButtonVisible(false, false);
        checkSendButton(false);
        checkChannelRights();
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (view == this.topView) {
            canvas.save();
            canvas.clipRect(0, 0, getMeasuredWidth(), view.getLayoutParams().height + AndroidUtilities.dp(2.0f));
        }
        j = super.drawChild(canvas, view, j);
        if (view == this.topView) {
            canvas.restore();
        }
        return j;
    }

    protected void onDraw(Canvas canvas) {
        int translationY = (this.topView == null || this.topView.getVisibility() != 0) ? 0 : (int) this.topView.getTranslationY();
        int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + translationY;
        Theme.chat_composeShadowDrawable.setBounds(0, translationY, getMeasuredWidth(), intrinsicHeight);
        Theme.chat_composeShadowDrawable.draw(canvas);
        canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
    }

    public boolean isSendButtonVisible() {
        return this.sendButton.getVisibility() == 0;
    }

    private void setRecordVideoButtonVisible(boolean z, boolean z2) {
        if (this.videoSendButton != null) {
            this.videoSendButton.setTag(z ? Integer.valueOf(1) : null);
            if (this.audioVideoButtonAnimation != null) {
                this.audioVideoButtonAnimation.cancel();
                this.audioVideoButtonAnimation = null;
            }
            float f = 0.0f;
            float f2 = 0.1f;
            if (z2) {
                int i;
                Animator[] animatorArr;
                ImageView imageView;
                String str;
                float[] fArr;
                ImageView imageView2;
                String str2;
                float[] fArr2;
                float[] fArr3;
                z2 = MessagesController.getGlobalMainSettings();
                if (((int) this.dialog_id) < 0) {
                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
                    if (ChatObject.isChannel(chat) && !chat.megagroup) {
                        i = 1;
                        z2.edit().putBoolean(i == 0 ? "currentModeVideoChannel" : "currentModeVideo", z).commit();
                        this.audioVideoButtonAnimation = new AnimatorSet();
                        z2 = this.audioVideoButtonAnimation;
                        animatorArr = new Animator[6];
                        imageView = this.videoSendButton;
                        str = "scaleX";
                        fArr = new float[1];
                        fArr[0] = z ? 1.0f : 0.1f;
                        animatorArr[0] = ObjectAnimator.ofFloat(imageView, str, fArr);
                        imageView = this.videoSendButton;
                        str = "scaleY";
                        fArr = new float[1];
                        fArr[0] = z ? 1.0f : 0.1f;
                        animatorArr[1] = ObjectAnimator.ofFloat(imageView, str, fArr);
                        imageView2 = this.videoSendButton;
                        str2 = "alpha";
                        fArr2 = new float[1];
                        fArr2[0] = z ? 1.0f : 0.0f;
                        animatorArr[2] = ObjectAnimator.ofFloat(imageView2, str2, fArr2);
                        imageView2 = this.audioSendButton;
                        str2 = "scaleX";
                        fArr2 = new float[1];
                        fArr2[0] = z ? 0.1f : 1.0f;
                        animatorArr[3] = ObjectAnimator.ofFloat(imageView2, str2, fArr2);
                        imageView2 = this.audioSendButton;
                        str2 = "scaleY";
                        fArr2 = new float[1];
                        if (z) {
                            f2 = 1.0f;
                        }
                        fArr2[0] = f2;
                        animatorArr[4] = ObjectAnimator.ofFloat(imageView2, str2, fArr2);
                        imageView = this.audioSendButton;
                        str = "alpha";
                        fArr3 = new float[1];
                        if (z) {
                            f = 1.0f;
                        }
                        fArr3[0] = f;
                        animatorArr[5] = ObjectAnimator.ofFloat(imageView, str, fArr3);
                        z2.playTogether(animatorArr);
                        this.audioVideoButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.audioVideoButtonAnimation) != null) {
                                    ChatActivityEnterView.this.audioVideoButtonAnimation = null;
                                }
                            }
                        });
                        this.audioVideoButtonAnimation.setInterpolator(new DecelerateInterpolator());
                        this.audioVideoButtonAnimation.setDuration(150);
                        this.audioVideoButtonAnimation.start();
                    }
                }
                i = 0;
                if (i == 0) {
                }
                z2.edit().putBoolean(i == 0 ? "currentModeVideoChannel" : "currentModeVideo", z).commit();
                this.audioVideoButtonAnimation = new AnimatorSet();
                z2 = this.audioVideoButtonAnimation;
                animatorArr = new Animator[6];
                imageView = this.videoSendButton;
                str = "scaleX";
                fArr = new float[1];
                if (z) {
                }
                fArr[0] = z ? 1.0f : 0.1f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView, str, fArr);
                imageView = this.videoSendButton;
                str = "scaleY";
                fArr = new float[1];
                if (z) {
                }
                fArr[0] = z ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView, str, fArr);
                imageView2 = this.videoSendButton;
                str2 = "alpha";
                fArr2 = new float[1];
                if (z) {
                }
                fArr2[0] = z ? 1.0f : 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView2, str2, fArr2);
                imageView2 = this.audioSendButton;
                str2 = "scaleX";
                fArr2 = new float[1];
                if (z) {
                }
                fArr2[0] = z ? 0.1f : 1.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(imageView2, str2, fArr2);
                imageView2 = this.audioSendButton;
                str2 = "scaleY";
                fArr2 = new float[1];
                if (z) {
                    f2 = 1.0f;
                }
                fArr2[0] = f2;
                animatorArr[4] = ObjectAnimator.ofFloat(imageView2, str2, fArr2);
                imageView = this.audioSendButton;
                str = "alpha";
                fArr3 = new float[1];
                if (z) {
                    f = 1.0f;
                }
                fArr3[0] = f;
                animatorArr[5] = ObjectAnimator.ofFloat(imageView, str, fArr3);
                z2.playTogether(animatorArr);
                this.audioVideoButtonAnimation.addListener(/* anonymous class already generated */);
                this.audioVideoButtonAnimation.setInterpolator(new DecelerateInterpolator());
                this.audioVideoButtonAnimation.setDuration(150);
                this.audioVideoButtonAnimation.start();
            } else {
                this.videoSendButton.setScaleX(z ? 1.0f : 0.1f);
                this.videoSendButton.setScaleY(z ? 1.0f : 0.1f);
                this.videoSendButton.setAlpha(z ? 1.0f : 0.0f);
                this.audioSendButton.setScaleX(z ? 0.1f : 1.0f);
                z2 = this.audioSendButton;
                if (!z) {
                    f2 = 1.0f;
                }
                z2.setScaleY(f2);
                z2 = this.audioSendButton;
                if (!z) {
                    f = 1.0f;
                }
                z2.setAlpha(f);
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

    public void showContextProgress(boolean z) {
        if (this.progressDrawable != null) {
            if (z) {
                this.progressDrawable.startAnimation();
            } else {
                this.progressDrawable.stopAnimation();
            }
        }
    }

    public void setCaption(String str) {
        if (this.messageEditText != null) {
            this.messageEditText.setCaption(str);
            checkSendButton(true);
        }
    }

    public void addTopView(View view, int i) {
        if (view != null) {
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
            if (this.emojiView.getVisibility() == 0) {
                hidePopup(false);
            }
            this.sizeNotifierLayout.removeView(this.emojiView);
            this.emojiView = null;
        }
        this.allowStickers = z;
        this.allowGifs = z2;
        setEmojiButtonImage();
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

    public void showTopView(boolean z, final boolean z2) {
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
                    if (z) {
                        if (!this.keyboardVisible) {
                            if (!isPopupShowing()) {
                                this.topView.setTranslationY(0.0f);
                                if (this.recordedAudioPanel.getVisibility() && (!this.forceShowSendButton || z2)) {
                                    openKeyboard();
                                }
                            }
                        }
                        this.currentTopViewAnimation = new AnimatorSet();
                        z = this.currentTopViewAnimation;
                        Animator[] animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.topView, "translationY", new float[]{0.0f});
                        z.playTogether(animatorArr);
                        this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator) != null) {
                                    if (ChatActivityEnterView.this.recordedAudioPanel.getVisibility() != null && (ChatActivityEnterView.this.forceShowSendButton == null || z2 != null)) {
                                        ChatActivityEnterView.this.openKeyboard();
                                    }
                                    ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator) != null) {
                                    ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                            }
                        });
                        this.currentTopViewAnimation.setDuration(200);
                        this.currentTopViewAnimation.start();
                    } else {
                        this.topView.setTranslationY(0.0f);
                        if (this.recordedAudioPanel.getVisibility() && (!this.forceShowSendButton || z2)) {
                            openKeyboard();
                        }
                    }
                }
                return;
            }
        }
        if (this.recordedAudioPanel.getVisibility() && (!this.forceShowSendButton || z2)) {
            openKeyboard();
        }
    }

    public void onEditTimeExpired() {
        this.doneButtonContainer.setVisibility(8);
    }

    public void showEditDoneProgress(final boolean z, boolean z2) {
        if (this.doneButtonAnimation != null) {
            this.doneButtonAnimation.cancel();
        }
        if (z2) {
            this.doneButtonAnimation = new AnimatorSet();
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (z) {
                this.doneButtonProgress.setVisibility(0);
                this.doneButtonContainer.setEnabled(false);
                animatorSet = this.doneButtonAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneButtonImage, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneButtonImage, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneButtonImage, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.doneButtonProgress, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.doneButtonProgress, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.doneButtonProgress, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                this.doneButtonImage.setVisibility(0);
                this.doneButtonContainer.setEnabled(true);
                animatorSet = this.doneButtonAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneButtonProgress, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneButtonProgress, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneButtonProgress, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.doneButtonImage, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.doneButtonImage, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.doneButtonImage, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            this.doneButtonAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animator) != null) {
                        if (z == null) {
                            ChatActivityEnterView.this.doneButtonProgress.setVisibility(4);
                        } else {
                            ChatActivityEnterView.this.doneButtonImage.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animator) != null) {
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
        if (this.topView != null) {
            if (this.topViewShowed) {
                this.topViewShowed = false;
                this.needShowTopView = false;
                if (this.allowShowTopView) {
                    if (this.currentTopViewAnimation != null) {
                        this.currentTopViewAnimation.cancel();
                        this.currentTopViewAnimation = null;
                    }
                    if (z) {
                        this.currentTopViewAnimation = new AnimatorSet();
                        z = this.currentTopViewAnimation;
                        Animator[] animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.topView, "translationY", new float[]{(float) this.topView.getLayoutParams().height});
                        z.playTogether(animatorArr);
                        this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator) != null) {
                                    ChatActivityEnterView.this.topView.setVisibility(8);
                                    ChatActivityEnterView.this.resizeForTopView(false);
                                    ChatActivityEnterView.this.currentTopViewAnimation = null;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator) != null) {
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
        int height = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            height -= this.emojiPadding;
        }
        if (this.delegate != null) {
            this.delegate.onWindowSizeChanged(height);
        }
        if (this.topView == null) {
            return;
        }
        if (height < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
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

    private void resizeForTopView(boolean z) {
        LayoutParams layoutParams = (LayoutParams) this.textFieldContainer.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(2.0f) + (z ? this.topView.getLayoutParams().height : false);
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
            Chat currentChat = this.parentFragment.getCurrentChat();
            if (ChatObject.isChannel(currentChat)) {
                float f;
                EmojiView emojiView;
                boolean z;
                FrameLayout frameLayout = this.audioVideoButtonContainer;
                if (currentChat.banned_rights != null) {
                    if (currentChat.banned_rights.send_media) {
                        f = 0.5f;
                        frameLayout.setAlpha(f);
                        if (this.emojiView != null) {
                            emojiView = this.emojiView;
                            z = currentChat.banned_rights == null && currentChat.banned_rights.send_stickers;
                            emojiView.setStickersBanned(z, currentChat.id);
                        }
                    }
                }
                f = 1.0f;
                frameLayout.setAlpha(f);
                if (this.emojiView != null) {
                    emojiView = this.emojiView;
                    if (currentChat.banned_rights == null) {
                    }
                    emojiView.setStickersBanned(z, currentChat.id);
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

    public void setDialogId(long j, int i) {
        this.dialog_id = j;
        if (this.currentAccount != i) {
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
            this.currentAccount = i;
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
        j = this.dialog_id;
        j = this.dialog_id;
        if (((int) this.dialog_id) < null) {
            j = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            i = new StringBuilder();
            i.append("silent_");
            i.append(this.dialog_id);
            this.silent = notificationsSettings.getBoolean(i.toString(), false);
            i = 1;
            j = (!ChatObject.isChannel(j) || ((!j.creator && (j.admin_rights == null || !j.admin_rights.post_messages)) || j.megagroup != null)) ? 0 : 1;
            this.canWriteToChannel = j;
            if (this.notifyButton != null) {
                float f;
                this.notifyButton.setVisibility(this.canWriteToChannel ? 0 : 8);
                this.notifyButton.setImageResource(this.silent ? C0446R.drawable.notify_members_off : C0446R.drawable.notify_members_on);
                j = this.attachLayout;
                if (this.botButton == null || this.botButton.getVisibility() == 8) {
                    if (this.notifyButton != null) {
                        if (this.notifyButton.getVisibility() == 8) {
                        }
                    }
                    f = 48.0f;
                    j.setPivotX((float) AndroidUtilities.dp(f));
                }
                f = 96.0f;
                j.setPivotX((float) AndroidUtilities.dp(f));
            }
            if (this.attachLayout != null) {
                if (this.attachLayout.getVisibility() != null) {
                    i = 0;
                }
                updateFieldRight(i);
            }
        }
        checkRoundVideo();
        updateFieldHint();
    }

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        if (this.emojiView != null) {
            this.emojiView.setChatInfo(this.info);
        }
    }

    public void checkRoundVideo() {
        if (!this.hasRecordVideo) {
            if (this.attachLayout != null) {
                if (VERSION.SDK_INT >= 18) {
                    int i = (int) (this.dialog_id >> 32);
                    boolean z = true;
                    if (((int) this.dialog_id) != 0 || i == 0) {
                        this.hasRecordVideo = true;
                    } else if (AndroidUtilities.getPeerLayerVersion(MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i)).layer) >= 66) {
                        this.hasRecordVideo = true;
                    }
                    if (((int) this.dialog_id) < 0) {
                        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
                        if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            z = false;
                        }
                        if (z && !chat.creator && (chat.admin_rights == null || !chat.admin_rights.post_messages)) {
                            this.hasRecordVideo = false;
                        }
                    } else {
                        z = false;
                    }
                    if (!SharedConfig.inappCamera) {
                        this.hasRecordVideo = false;
                    }
                    if (this.hasRecordVideo) {
                        CameraController.getInstance().initCamera();
                        setRecordVideoButtonVisible(MessagesController.getGlobalMainSettings().getBoolean(z ? "currentModeVideoChannel" : "currentModeVideo", z), false);
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
        Object obj = null;
        if (((int) this.dialog_id) < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                obj = 1;
            }
        }
        if (obj == null) {
            this.messageEditText.setHintText(LocaleController.getString("TypeMessage", C0446R.string.TypeMessage));
        } else if (this.editingMessageObject != null) {
            this.messageEditText.setHintText(this.editingCaption ? LocaleController.getString("Caption", C0446R.string.Caption) : LocaleController.getString("TypeMessage", C0446R.string.TypeMessage));
        } else if (this.silent) {
            this.messageEditText.setHintText(LocaleController.getString("ChannelSilentBroadcast", C0446R.string.ChannelSilentBroadcast));
        } else {
            this.messageEditText.setHintText(LocaleController.getString("ChannelBroadcast", C0446R.string.ChannelBroadcast));
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
        animatorArr[0] = ObjectAnimator.ofFloat(this.recordedAudioPanel, "alpha", new float[]{0.0f});
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
        if (this.videoToSendMessageObject != null) {
            this.delegate.needStartRecordVideo(4);
            hideRecordedAudioPanel();
            checkSendButton(true);
        } else if (this.audioToSend != null) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null && playingMessageObject == this.audioToSendMessageObject) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.audioToSend, null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, null, null, null, null, 0);
            if (this.delegate != null) {
                this.delegate.onMessageSend(null);
            }
            hideRecordedAudioPanel();
            checkSendButton(true);
        } else {
            CharSequence text = this.messageEditText.getText();
            if (processSendingText(text)) {
                this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.lastTypingTimeSend = 0;
                if (this.delegate != null) {
                    this.delegate.onMessageSend(text);
                }
            } else if (this.forceShowSendButton && this.delegate != null) {
                this.delegate.onMessageSend(null);
            }
        }
    }

    public void doneEditingMessage() {
        if (this.editingMessageObject != null) {
            this.delegate.onMessageEditEnd(true);
            showEditDoneProgress(true, true);
            CharSequence[] charSequenceArr = new CharSequence[]{this.messageEditText.getText()};
            this.editingMessageReqId = SendMessagesHelper.getInstance(this.currentAccount).editMessage(this.editingMessageObject, charSequenceArr[0].toString(), this.messageWebPageSearch, this.parentFragment, DataQuery.getInstance(this.currentAccount).getEntities(charSequenceArr), new Runnable() {
                public void run() {
                    ChatActivityEnterView.this.editingMessageReqId = 0;
                    ChatActivityEnterView.this.setEditingMessageObject(null, false);
                }
            });
        }
    }

    public boolean processSendingText(CharSequence charSequence) {
        ChatActivityEnterView chatActivityEnterView = this;
        CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence);
        if (trimmedString.length() == 0) {
            return false;
        }
        int ceil = (int) Math.ceil((double) (((float) trimmedString.length()) / 4096.0f));
        int i = 0;
        while (i < ceil) {
            CharSequence[] charSequenceArr = new CharSequence[1];
            int i2 = i * 4096;
            i++;
            charSequenceArr[0] = trimmedString.subSequence(i2, Math.min(i * 4096, trimmedString.length()));
            SendMessagesHelper.getInstance(chatActivityEnterView.currentAccount).sendMessage(charSequenceArr[0].toString(), chatActivityEnterView.dialog_id, chatActivityEnterView.replyingMessageObject, chatActivityEnterView.messageWebPage, chatActivityEnterView.messageWebPageSearch, DataQuery.getInstance(chatActivityEnterView.currentAccount).getEntities(charSequenceArr), null, null);
        }
        return true;
    }

    private void checkSendButton(boolean z) {
        if (this.editingMessageObject == null) {
            AnimatorSet animatorSet;
            Collection arrayList;
            int i = r0.isPaused ? 0 : z;
            if (AndroidUtilities.getTrimmedString(r0.messageEditText.getText()).length() <= 0 && !r0.forceShowSendButton && r0.audioToSend == null) {
                if (r0.videoToSendMessageObject == null) {
                    if (r0.emojiView == null || r0.emojiView.getVisibility() != 0 || !r0.stickersTabOpen || AndroidUtilities.isInMultiwindow) {
                        if (r0.sendButton.getVisibility() == 0 || r0.cancelBotButton.getVisibility() == 0 || r0.expandStickersButton.getVisibility() == 0) {
                            if (i == 0) {
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
                                    r3 = new Animator[2];
                                    r3[0] = ObjectAnimator.ofFloat(r0.attachLayout, "alpha", new float[]{1.0f});
                                    r3[1] = ObjectAnimator.ofFloat(r0.attachLayout, "scaleX", new float[]{1.0f});
                                    animatorSet.playTogether(r3);
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
                                arrayList = new ArrayList();
                                arrayList.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleX", new float[]{1.0f}));
                                arrayList.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleY", new float[]{1.0f}));
                                arrayList.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "alpha", new float[]{1.0f}));
                                if (r0.cancelBotButton.getVisibility() == 0) {
                                    arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleX", new float[]{0.1f}));
                                    arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleY", new float[]{0.1f}));
                                    arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "alpha", new float[]{0.0f}));
                                } else if (r0.expandStickersButton.getVisibility() == 0) {
                                    arrayList.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleX", new float[]{0.1f}));
                                    arrayList.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleY", new float[]{0.1f}));
                                    arrayList.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "alpha", new float[]{0.0f}));
                                } else {
                                    arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleX", new float[]{0.1f}));
                                    arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleY", new float[]{0.1f}));
                                    arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "alpha", new float[]{0.0f}));
                                }
                                r0.runningAnimation.playTogether(arrayList);
                                r0.runningAnimation.setDuration(150);
                                r0.runningAnimation.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator) != null) {
                                            ChatActivityEnterView.this.sendButton.setVisibility(8);
                                            ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                            ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
                                            ChatActivityEnterView.this.runningAnimation = null;
                                            ChatActivityEnterView.this.runningAnimationType = 0;
                                        }
                                    }

                                    public void onAnimationCancel(Animator animator) {
                                        if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator) != null) {
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
                    if (i == 0) {
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
                            r8 = new Animator[2];
                            r8[0] = ObjectAnimator.ofFloat(r0.attachLayout, "alpha", new float[]{1.0f});
                            r8[1] = ObjectAnimator.ofFloat(r0.attachLayout, "scaleX", new float[]{1.0f});
                            animatorSet.playTogether(r8);
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
                        arrayList = new ArrayList();
                        arrayList.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleX", new float[]{1.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleY", new float[]{1.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "alpha", new float[]{1.0f}));
                        if (r0.cancelBotButton.getVisibility() == 0) {
                            arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleX", new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleY", new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "alpha", new float[]{0.0f}));
                        } else if (r0.audioVideoButtonContainer.getVisibility() == 0) {
                            arrayList.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleX", new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleY", new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "alpha", new float[]{0.0f}));
                        } else {
                            arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleX", new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleY", new float[]{0.1f}));
                            arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "alpha", new float[]{0.0f}));
                        }
                        r0.runningAnimation.playTogether(arrayList);
                        r0.runningAnimation.setDuration(150);
                        r0.runningAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator) != null) {
                                    ChatActivityEnterView.this.sendButton.setVisibility(8);
                                    ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                    ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                    ChatActivityEnterView.this.expandStickersButton.setVisibility(0);
                                    ChatActivityEnterView.this.runningAnimation = null;
                                    ChatActivityEnterView.this.runningAnimationType = 0;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator) != null) {
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
            int i2 = (caption == null || !(r0.sendButton.getVisibility() == 0 || r0.expandStickersButton.getVisibility() == 0)) ? 0 : 1;
            int i3 = (caption == null && (r0.cancelBotButton.getVisibility() == 0 || r0.expandStickersButton.getVisibility() == 0)) ? 1 : 0;
            if (!(r0.audioVideoButtonContainer.getVisibility() != 0 && i2 == 0 && i3 == 0)) {
                if (i == 0) {
                    int i4;
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
                        i4 = 8;
                        r0.sendButton.setVisibility(8);
                    } else {
                        r0.cancelBotButton.setScaleX(0.1f);
                        r0.cancelBotButton.setScaleY(0.1f);
                        r0.cancelBotButton.setAlpha(0.0f);
                        r0.sendButton.setScaleX(1.0f);
                        r0.sendButton.setScaleY(1.0f);
                        r0.sendButton.setAlpha(1.0f);
                        r0.sendButton.setVisibility(0);
                        i4 = 8;
                        r0.cancelBotButton.setVisibility(8);
                    }
                    r0.audioVideoButtonContainer.setVisibility(i4);
                    if (r0.attachLayout != null) {
                        r0.attachLayout.setVisibility(i4);
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
                        animatorSet = r0.runningAnimation2;
                        r5 = new Animator[2];
                        r5[0] = ObjectAnimator.ofFloat(r0.attachLayout, "alpha", new float[]{0.0f});
                        r5[1] = ObjectAnimator.ofFloat(r0.attachLayout, "scaleX", new float[]{0.0f});
                        animatorSet.playTogether(r5);
                        r0.runningAnimation2.setDuration(100);
                        r0.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (ChatActivityEnterView.this.runningAnimation2 != null && ChatActivityEnterView.this.runningAnimation2.equals(animator) != null) {
                                    ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (ChatActivityEnterView.this.runningAnimation2 != null && ChatActivityEnterView.this.runningAnimation2.equals(animator) != null) {
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
                    arrayList = new ArrayList();
                    if (r0.audioVideoButtonContainer.getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleX", new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "scaleY", new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.audioVideoButtonContainer, "alpha", new float[]{0.0f}));
                    }
                    if (r0.expandStickersButton.getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleX", new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "scaleY", new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.expandStickersButton, "alpha", new float[]{0.0f}));
                    }
                    if (i2 != 0) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleX", new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleY", new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "alpha", new float[]{0.0f}));
                    } else if (i3 != 0) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleX", new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleY", new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "alpha", new float[]{0.0f}));
                    }
                    if (caption != null) {
                        r0.runningAnimationType = 3;
                        arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleX", new float[]{1.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "scaleY", new float[]{1.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.cancelBotButton, "alpha", new float[]{1.0f}));
                        r0.cancelBotButton.setVisibility(0);
                    } else {
                        r0.runningAnimationType = 1;
                        arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleX", new float[]{1.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "scaleY", new float[]{1.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.sendButton, "alpha", new float[]{1.0f}));
                        r0.sendButton.setVisibility(0);
                    }
                    r0.runningAnimation.playTogether(arrayList);
                    r0.runningAnimation.setDuration(150);
                    r0.runningAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator) != null) {
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
                            if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator) != null) {
                                ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }
                    });
                    r0.runningAnimation.start();
                }
            }
        }
    }

    private void updateFieldRight(int i) {
        if (this.messageEditText != null) {
            if (this.editingMessageObject == null) {
                LayoutParams layoutParams = (LayoutParams) this.messageEditText.getLayoutParams();
                if (i == 1) {
                    if ((this.botButton == 0 || this.botButton.getVisibility() != 0) && (this.notifyButton == 0 || this.notifyButton.getVisibility() != 0)) {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    } else {
                        layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                    }
                } else if (i != 2) {
                    layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
                } else if (layoutParams.rightMargin != AndroidUtilities.dp(2.0f)) {
                    if ((this.botButton == 0 || this.botButton.getVisibility() != 0) && (this.notifyButton == 0 || this.notifyButton.getVisibility() != 0)) {
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
        AnimatorSet animatorSet;
        Animator[] animatorArr;
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
                animatorSet = this.runningAnimationAudio;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[]{(float) AndroidUtilities.displaySize.x});
                animatorArr[1] = ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
                this.runningAnimationAudio.setDuration(300);
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivityEnterView.this.runningAnimationAudio != null && ChatActivityEnterView.this.runningAnimationAudio.equals(animator) != null) {
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
            LayoutParams layoutParams = (LayoutParams) this.slideText.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(30.0f);
            this.slideText.setLayoutParams(layoutParams);
            this.slideText.setAlpha(1.0f);
            this.recordPanel.setX((float) AndroidUtilities.displaySize.x);
            if (this.runningAnimationAudio != null) {
                this.runningAnimationAudio.cancel();
            }
            this.runningAnimationAudio = new AnimatorSet();
            animatorSet = this.runningAnimationAudio;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[]{1.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.runningAnimationAudio.setDuration(300);
            this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatActivityEnterView.this.runningAnimationAudio != null && ChatActivityEnterView.this.runningAnimationAudio.equals(animator) != null) {
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
        ChatActivityEnterView chatActivityEnterView = this;
        MessageObject messageObject2 = messageObject;
        String str2 = str;
        if (str2 != null) {
            if (getVisibility() == 0) {
                User user = null;
                if (z) {
                    CharSequence stringBuilder;
                    String obj = chatActivityEnterView.messageEditText.getText().toString();
                    if (messageObject2 != null && ((int) chatActivityEnterView.dialog_id) < 0) {
                        user = MessagesController.getInstance(chatActivityEnterView.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                    }
                    StringBuilder stringBuilder2;
                    if ((chatActivityEnterView.botCount != 1 || z2) && user != null && user.bot && !str2.contains("@")) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(String.format(Locale.US, "%s@%s", new Object[]{str2, user.username}));
                        stringBuilder2.append(" ");
                        stringBuilder2.append(obj.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", TtmlNode.ANONYMOUS_REGION_ID));
                        stringBuilder = stringBuilder2.toString();
                    } else {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(str2);
                        stringBuilder2.append(" ");
                        stringBuilder2.append(obj.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", TtmlNode.ANONYMOUS_REGION_ID));
                        stringBuilder = stringBuilder2.toString();
                    }
                    chatActivityEnterView.ignoreTextChange = true;
                    chatActivityEnterView.messageEditText.setText(stringBuilder);
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
                    if ((chatActivityEnterView.botCount != 1 || z2) && user != null && user.bot && !str2.contains("@")) {
                        SendMessagesHelper.getInstance(chatActivityEnterView.currentAccount).sendMessage(String.format(Locale.US, "%s@%s", new Object[]{str2, user.username}), chatActivityEnterView.dialog_id, chatActivityEnterView.replyingMessageObject, null, false, null, null, null);
                    } else {
                        SendMessagesHelper.getInstance(chatActivityEnterView.currentAccount).sendMessage(str2, chatActivityEnterView.dialog_id, chatActivityEnterView.replyingMessageObject, null, false, null, null, null);
                    }
                }
            }
        }
    }

    public void setEditingMessageObject(MessageObject messageObject, boolean z) {
        if (this.audioToSend == null && this.videoToSendMessageObject == null) {
            if (this.editingMessageObject != messageObject) {
                if (this.editingMessageReqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.editingMessageReqId, true);
                    this.editingMessageReqId = 0;
                }
                this.editingMessageObject = messageObject;
                this.editingCaption = z;
                if (this.editingMessageObject != null) {
                    if (this.doneButtonAnimation != null) {
                        this.doneButtonAnimation.cancel();
                        this.doneButtonAnimation = null;
                    }
                    this.doneButtonContainer.setVisibility(0);
                    showEditDoneProgress(true, false);
                    messageObject = new InputFilter[1];
                    if (z) {
                        messageObject[0] = new LengthFilter(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                        z = this.editingMessageObject.caption;
                    } else {
                        messageObject[0] = new LengthFilter(4096);
                        z = this.editingMessageObject.messageText;
                    }
                    if (z) {
                        int i;
                        ArrayList arrayList = this.editingMessageObject.messageOwner.entities;
                        DataQuery.sortEntities(arrayList);
                        CharSequence spannableStringBuilder = new SpannableStringBuilder(z);
                        z = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), Object.class);
                        if (z && z.length > 0) {
                            for (Object removeSpan : z) {
                                spannableStringBuilder.removeSpan(removeSpan);
                            }
                        }
                        if (arrayList != null) {
                            z = false;
                            i = z;
                            while (z < arrayList.size()) {
                                try {
                                    MessageEntity messageEntity = (MessageEntity) arrayList.get(z);
                                    if ((messageEntity.offset + messageEntity.length) + i <= spannableStringBuilder.length()) {
                                        StringBuilder stringBuilder;
                                        if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                                            if ((messageEntity.offset + messageEntity.length) + i < spannableStringBuilder.length() && spannableStringBuilder.charAt((messageEntity.offset + messageEntity.length) + i) == ' ') {
                                                messageEntity.length++;
                                            }
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                            stringBuilder.append(((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id);
                                            spannableStringBuilder.setSpan(new URLSpanUserMention(stringBuilder.toString(), 1), messageEntity.offset + i, (messageEntity.offset + messageEntity.length) + i, 33);
                                        } else if (messageEntity instanceof TL_messageEntityMentionName) {
                                            if ((messageEntity.offset + messageEntity.length) + i < spannableStringBuilder.length() && spannableStringBuilder.charAt((messageEntity.offset + messageEntity.length) + i) == ' ') {
                                                messageEntity.length++;
                                            }
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                            stringBuilder.append(((TL_messageEntityMentionName) messageEntity).user_id);
                                            spannableStringBuilder.setSpan(new URLSpanUserMention(stringBuilder.toString(), 1), messageEntity.offset + i, (messageEntity.offset + messageEntity.length) + i, 33);
                                        } else if (messageEntity instanceof TL_messageEntityCode) {
                                            spannableStringBuilder.insert((messageEntity.offset + messageEntity.length) + i, "`");
                                            spannableStringBuilder.insert(messageEntity.offset + i, "`");
                                            i += 2;
                                        } else if (messageEntity instanceof TL_messageEntityPre) {
                                            spannableStringBuilder.insert((messageEntity.offset + messageEntity.length) + i, "```");
                                            spannableStringBuilder.insert(messageEntity.offset + i, "```");
                                            i += 6;
                                        } else if (messageEntity instanceof TL_messageEntityBold) {
                                            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), messageEntity.offset + i, (messageEntity.offset + messageEntity.length) + i, 33);
                                        } else if (messageEntity instanceof TL_messageEntityItalic) {
                                            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), messageEntity.offset + i, (messageEntity.offset + messageEntity.length) + i, 33);
                                        }
                                    }
                                    z++;
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        }
                        setFieldText(Emoji.replaceEmoji(new SpannableStringBuilder(spannableStringBuilder), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
                    } else {
                        setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                    this.messageEditText.setFilters(messageObject);
                    openKeyboard();
                    LayoutParams layoutParams = (LayoutParams) this.messageEditText.getLayoutParams();
                    layoutParams.rightMargin = AndroidUtilities.dp(true);
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
                    this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                    if (getVisibility() == null) {
                        this.delegate.onAttachButtonShow();
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

    public void setFieldText(CharSequence charSequence) {
        if (this.messageEditText != null) {
            this.ignoreTextChange = true;
            this.messageEditText.setText(charSequence);
            this.messageEditText.setSelection(this.messageEditText.getText().length());
            this.ignoreTextChange = null;
            if (this.delegate != null) {
                this.delegate.onTextChanged(this.messageEditText.getText(), true);
            }
        }
    }

    public void setSelection(int i) {
        if (this.messageEditText != null) {
            this.messageEditText.setSelection(i, this.messageEditText.length());
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

    public void replaceWithText(int i, int i2, CharSequence charSequence, boolean z) {
        try {
            CharSequence spannableStringBuilder = new SpannableStringBuilder(this.messageEditText.getText());
            spannableStringBuilder.replace(i, i2 + i, charSequence);
            if (z) {
                Emoji.replaceEmoji(spannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(true), false);
            }
            this.messageEditText.setText(spannableStringBuilder);
            this.messageEditText.setSelection(i + charSequence.length());
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

    public void setFieldFocused(boolean z) {
        if (this.messageEditText != null) {
            if (z) {
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
        return (this.messageEditText == null || this.messageEditText.length() <= 0) ? null : this.messageEditText.getText();
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
                this.botButton.setImageResource(C0446R.drawable.bot_keyboard);
            } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                this.botButton.setImageResource(C0446R.drawable.ic_msg_panel_kb);
            } else {
                this.botButton.setImageResource(C0446R.drawable.bot_keyboard2);
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

    public void setButtons(MessageObject messageObject, boolean z) {
        if (this.replyingMessageObject == null || this.replyingMessageObject != this.botButtonsMessageObject || this.replyingMessageObject == messageObject) {
            if (this.botButton != null && (this.botButtonsMessageObject == null || this.botButtonsMessageObject != messageObject)) {
                if (this.botButtonsMessageObject != null || messageObject != null) {
                    if (this.botKeyboardView == null) {
                        this.botKeyboardView = new BotKeyboardView(this.parentActivity);
                        this.botKeyboardView.setVisibility(8);
                        this.botKeyboardView.setDelegate(new BotKeyboardViewDelegate() {
                            public void didPressedButton(KeyboardButton keyboardButton) {
                                MessageObject access$2500 = ChatActivityEnterView.this.replyingMessageObject != null ? ChatActivityEnterView.this.replyingMessageObject : ((int) ChatActivityEnterView.this.dialog_id) < 0 ? ChatActivityEnterView.this.botButtonsMessageObject : null;
                                ChatActivityEnterView.this.didPressedBotButton(keyboardButton, access$2500, ChatActivityEnterView.this.replyingMessageObject != null ? ChatActivityEnterView.this.replyingMessageObject : ChatActivityEnterView.this.botButtonsMessageObject);
                                if (ChatActivityEnterView.this.replyingMessageObject != null) {
                                    ChatActivityEnterView.this.openKeyboardInternal();
                                    ChatActivityEnterView.this.setButtons(ChatActivityEnterView.this.botMessageObject, false);
                                } else if (ChatActivityEnterView.this.botButtonsMessageObject.messageOwner.reply_markup.single_use != null) {
                                    ChatActivityEnterView.this.openKeyboardInternal();
                                    keyboardButton = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("answered_");
                                    stringBuilder.append(ChatActivityEnterView.this.dialog_id);
                                    keyboardButton.putInt(stringBuilder.toString(), ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
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
                        z = MessagesController.getMainSettings(this.currentAccount);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("hidekeyboard_");
                        stringBuilder.append(this.dialog_id);
                        int i = z.getInt(stringBuilder.toString(), 0) == messageObject.getId() ? 1 : 0;
                        if (this.botButtonsMessageObject != this.replyingMessageObject && this.botReplyMarkup.single_use) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("answered_");
                            stringBuilder2.append(this.dialog_id);
                            if (z.getInt(stringBuilder2.toString(), 0) == messageObject.getId()) {
                                return;
                            }
                        }
                        if (i == 0 && this.messageEditText.length() == null && isPopupShowing() == null) {
                            showPopup(1, 1);
                        }
                    } else if (isPopupShowing() != null && this.currentPopupContentType == 1) {
                        if (z) {
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

    public void didPressedBotButton(final KeyboardButton keyboardButton, MessageObject messageObject, final MessageObject messageObject2) {
        if (keyboardButton != null) {
            if (messageObject2 != null) {
                if (keyboardButton instanceof TL_keyboardButton) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(keyboardButton.text, this.dialog_id, messageObject, null, false, null, null, null);
                } else if ((keyboardButton instanceof TL_keyboardButtonUrl) != null) {
                    this.parentFragment.showOpenUrlAlert(keyboardButton.url, true);
                } else if ((keyboardButton instanceof TL_keyboardButtonRequestPhone) != null) {
                    this.parentFragment.shareMyContact(messageObject2);
                } else if ((keyboardButton instanceof TL_keyboardButtonRequestGeoLocation) != null) {
                    messageObject = new Builder(this.parentActivity);
                    messageObject.setTitle(LocaleController.getString("ShareYouLocationTitle", C0446R.string.ShareYouLocationTitle));
                    messageObject.setMessage(LocaleController.getString("ShareYouLocationInfo", C0446R.string.ShareYouLocationInfo));
                    messageObject.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == null) {
                                SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendCurrentLocation(messageObject2, keyboardButton);
                                return;
                            }
                            ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                            ChatActivityEnterView.this.pendingMessageObject = messageObject2;
                            ChatActivityEnterView.this.pendingLocationButton = keyboardButton;
                        }
                    });
                    messageObject.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    this.parentFragment.showDialog(messageObject.create());
                } else {
                    if ((keyboardButton instanceof TL_keyboardButtonCallback) == null && (keyboardButton instanceof TL_keyboardButtonGame) == null) {
                        if ((keyboardButton instanceof TL_keyboardButtonBuy) == null) {
                            if ((keyboardButton instanceof TL_keyboardButtonSwitchInline) != null && this.parentFragment.processSwitchButton((TL_keyboardButtonSwitchInline) keyboardButton) == null) {
                                if (keyboardButton.same_peer != null) {
                                    messageObject = messageObject2.messageOwner.from_id;
                                    if (messageObject2.messageOwner.via_bot_id != 0) {
                                        messageObject = messageObject2.messageOwner.via_bot_id;
                                    }
                                    messageObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject));
                                    if (messageObject != null) {
                                        messageObject2 = new StringBuilder();
                                        messageObject2.append("@");
                                        messageObject2.append(messageObject.username);
                                        messageObject2.append(" ");
                                        messageObject2.append(keyboardButton.query);
                                        setFieldText(messageObject2.toString());
                                    } else {
                                        return;
                                    }
                                }
                                messageObject = new Bundle();
                                messageObject.putBoolean("onlySelect", true);
                                messageObject.putInt("dialogsType", 1);
                                BaseFragment dialogsActivity = new DialogsActivity(messageObject);
                                dialogsActivity.setDelegate(new DialogsActivityDelegate() {
                                    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
                                        charSequence = messageObject2.messageOwner.from_id;
                                        if (messageObject2.messageOwner.via_bot_id) {
                                            charSequence = messageObject2.messageOwner.via_bot_id;
                                        }
                                        charSequence = MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).getUser(Integer.valueOf(charSequence));
                                        if (charSequence == null) {
                                            dialogsActivity.finishFragment();
                                            return;
                                        }
                                        long longValue = ((Long) arrayList.get(false)).longValue();
                                        DataQuery instance = DataQuery.getInstance(ChatActivityEnterView.this.currentAccount);
                                        arrayList = new StringBuilder();
                                        arrayList.append("@");
                                        arrayList.append(charSequence.username);
                                        arrayList.append(" ");
                                        arrayList.append(keyboardButton.query);
                                        instance.saveDraft(longValue, arrayList.toString(), null, null, true);
                                        if (longValue != ChatActivityEnterView.this.dialog_id) {
                                            arrayList = (int) longValue;
                                            if (arrayList != null) {
                                                charSequence = new Bundle();
                                                if (arrayList > null) {
                                                    charSequence.putInt("user_id", arrayList);
                                                } else if (arrayList < null) {
                                                    charSequence.putInt("chat_id", -arrayList);
                                                }
                                                if (MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).checkCanOpenChat(charSequence, dialogsActivity) != null) {
                                                    if (ChatActivityEnterView.this.parentFragment.presentFragment(new ChatActivity(charSequence), true) == null) {
                                                        dialogsActivity.finishFragment();
                                                    } else if (AndroidUtilities.isTablet() == null) {
                                                        ChatActivityEnterView.this.parentFragment.removeSelfFromStack();
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
                                });
                                this.parentFragment.presentFragment(dialogsActivity);
                            } else {
                                return;
                            }
                        }
                    }
                    SendMessagesHelper.getInstance(this.currentAccount).sendCallback(true, messageObject2, keyboardButton, this.parentFragment);
                }
            }
        }
    }

    public boolean isPopupView(View view) {
        if (view != this.botKeyboardView) {
            if (view != this.emojiView) {
                return null;
            }
        }
        return true;
    }

    public boolean isRecordCircle(View view) {
        return view == this.recordCircle ? true : null;
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

                public void onEmojiSelected(String str) {
                    int selectionEnd = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
                    if (selectionEnd < 0) {
                        selectionEnd = 0;
                    }
                    try {
                        ChatActivityEnterView.this.innerTextChange = 2;
                        str = Emoji.replaceEmoji(str, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(selectionEnd, str));
                        selectionEnd += str.length();
                        ChatActivityEnterView.this.messageEditText.setSelection(selectionEnd, selectionEnd);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    } catch (Throwable th) {
                        ChatActivityEnterView.this.innerTextChange = 0;
                    }
                    ChatActivityEnterView.this.innerTextChange = 0;
                }

                public void onStickerSelected(Document document) {
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        if (ChatActivityEnterView.this.searchingStickers) {
                            ChatActivityEnterView.this.searchingStickers = false;
                            ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(document));
                            ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                        }
                        ChatActivityEnterView.this.setStickersExpanded(false, true);
                    }
                    ChatActivityEnterView.this.onStickerSelected(document);
                    DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).addRecentSticker(0, document, (int) (System.currentTimeMillis() / 1000), false);
                    if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                        MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).saveGif(document);
                    }
                }

                public void onStickersSettingsClick() {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0));
                    }
                }

                public void onGifSelected(Document document) {
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        ChatActivityEnterView.this.setStickersExpanded(false, true);
                    }
                    SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(document, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
                    DataQuery.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
                    if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                        MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).saveGif(document);
                    }
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onMessageSend(null);
                    }
                }

                public void onGifTab(boolean z) {
                    ChatActivityEnterView.this.post(ChatActivityEnterView.this.updateExpandabilityRunnable);
                    if (!AndroidUtilities.usingHardwareInput) {
                        if (z) {
                            if (!ChatActivityEnterView.this.messageEditText.length()) {
                                ChatActivityEnterView.this.messageEditText.setText("@gif ");
                                ChatActivityEnterView.this.messageEditText.setSelection(ChatActivityEnterView.this.messageEditText.length());
                            }
                        } else if (ChatActivityEnterView.this.messageEditText.getText().toString().equals("@gif ")) {
                            ChatActivityEnterView.this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        }
                    }
                }

                public void onStickersTab(boolean z) {
                    ChatActivityEnterView.this.delegate.onStickersTab(z);
                    ChatActivityEnterView.this.post(ChatActivityEnterView.this.updateExpandabilityRunnable);
                }

                public void onClearEmojiRecent() {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        if (ChatActivityEnterView.this.parentActivity != null) {
                            Builder builder = new Builder(ChatActivityEnterView.this.parentActivity);
                            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            builder.setMessage(LocaleController.getString("ClearRecentEmoji", C0446R.string.ClearRecentEmoji));
                            builder.setPositiveButton(LocaleController.getString("ClearButton", C0446R.string.ClearButton).toUpperCase(), new C10971());
                            builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
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
                        BaseFragment groupStickersActivity = new GroupStickersActivity(i);
                        groupStickersActivity.setInfo(ChatActivityEnterView.this.info);
                        ChatActivityEnterView.this.parentFragment.presentFragment(groupStickersActivity);
                    }
                }

                public void onSearchOpenClose(boolean z) {
                    ChatActivityEnterView.this.searchingStickers = z;
                    ChatActivityEnterView.this.setStickersExpanded(z, false);
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

                public void onDragEnd(float f) {
                    if (allowDragging()) {
                        ChatActivityEnterView.this.stickersDragging = false;
                        if ((!this.wasExpanded || f < ((float) AndroidUtilities.dp(200.0f))) && ((this.wasExpanded || f > ((float) AndroidUtilities.dp(-200.0f))) && ((this.wasExpanded == null || ChatActivityEnterView.this.stickersExpansionProgress > 0.6f) && (this.wasExpanded != null || ChatActivityEnterView.this.stickersExpansionProgress < 0.4f)))) {
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

                public void onDrag(int i) {
                    if (allowDragging()) {
                        int access$10100 = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                        i = (float) Math.max(Math.min(i + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - access$10100));
                        ChatActivityEnterView.this.emojiView.setTranslationY(i);
                        ChatActivityEnterView.this.setTranslationY(i);
                        ChatActivityEnterView.this.stickersExpansionProgress = i / ((float) (-(ChatActivityEnterView.this.stickersExpandedHeight - access$10100)));
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

    public void onStickerSelected(Document document) {
        if (this.searchingStickers) {
            this.searchingStickers = false;
            this.emojiView.closeSearch(true);
            this.emojiView.hideSearchKeyboard();
        }
        setStickersExpanded(false, true);
        SendMessagesHelper.getInstance(this.currentAccount).sendSticker(document, this.dialog_id, this.replyingMessageObject);
        if (this.delegate != null) {
            this.delegate.onMessageSend(null);
        }
    }

    public void addStickerToRecent(Document document) {
        createEmojiView();
        this.emojiView.addRecentSticker(document);
    }

    private void showPopup(int i, int i2) {
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
                if (!(this.botKeyboardView == null || this.botKeyboardView.getVisibility() == 8)) {
                    this.botKeyboardView.setVisibility(8);
                }
                view = this.emojiView;
            } else if (i2 == 1) {
                if (!(this.emojiView == null || this.emojiView.getVisibility() == 8)) {
                    this.emojiView.setVisibility(8);
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
            int i3 = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            if (i2 == 1) {
                i3 = Math.min(this.botKeyboardView.getKeyboardHeight(), i3);
            }
            if (this.botKeyboardView != null) {
                this.botKeyboardView.setPanelHeight(i3);
            }
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.height = i3;
            view.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            if (this.sizeNotifierLayout != null) {
                this.emojiPadding = i3;
                this.sizeNotifierLayout.requestLayout();
                if (i2 == 0) {
                    this.emojiButton.setImageResource(C0446R.drawable.ic_msg_panel_kb);
                } else if (i2 == 1) {
                    setEmojiButtonImage();
                }
                updateBotButton();
                onWindowSizeChanged();
            }
        } else {
            if (this.emojiButton != 0) {
                setEmojiButtonImage();
            }
            this.currentPopupContentType = -1;
            if (this.emojiView != 0) {
                this.emojiView.setVisibility(8);
            }
            if (this.botKeyboardView != 0) {
                this.botKeyboardView.setVisibility(8);
            }
            if (this.sizeNotifierLayout != 0) {
                if (i == 0) {
                    this.emojiPadding = 0;
                }
                this.sizeNotifierLayout.requestLayout();
                onWindowSizeChanged();
            }
            updateBotButton();
        }
        if (this.stickersTabOpen != 0) {
            checkSendButton(true);
        }
        if (!(this.stickersExpanded == 0 || i == 1)) {
            setStickersExpanded(false, false);
        }
    }

    private void setEmojiButtonImage() {
        int i;
        if (this.emojiView == null) {
            i = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
        } else {
            i = this.emojiView.getCurrentPage();
        }
        if (i != 0) {
            if (this.allowStickers || this.allowGifs) {
                if (i == 1) {
                    this.emojiButton.setImageResource(C0446R.drawable.ic_msg_panel_stickers);
                    return;
                } else if (i == 2) {
                    this.emojiButton.setImageResource(C0446R.drawable.ic_msg_panel_gif);
                    return;
                } else {
                    return;
                }
            }
        }
        this.emojiButton.setImageResource(C0446R.drawable.ic_msg_panel_smiles);
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
            if (z && this.searchingStickers) {
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

    public void addRecentGif(Document document) {
        DataQuery.getInstance(this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
        if (this.emojiView != null) {
            this.emojiView.addRecentGif(document);
        }
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (!(i == i3 || this.stickersExpanded == 0)) {
            this.searchingStickers = false;
            this.emojiView.closeSearch(false);
            setStickersExpanded(false, false);
        }
        this.videoTimelineView.clearFrames();
    }

    public boolean isStickersExpanded() {
        return this.stickersExpanded;
    }

    public void onSizeChanged(int i, boolean z) {
        boolean z2 = true;
        if (this.searchingStickers) {
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
            if (this.currentPopupContentType == 0) {
                view = this.emojiView;
            } else if (this.currentPopupContentType == 1) {
                view = this.botKeyboardView;
            }
            if (this.botKeyboardView != null) {
                this.botKeyboardView.setPanelHeight(i2);
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
        if (!(this.keyboardVisible == 0 || isPopupShowing() == 0)) {
            showPopup(0, this.currentPopupContentType);
        }
        if (this.emojiPadding != 0 && this.keyboardVisible == 0 && this.keyboardVisible != z && isPopupShowing() == 0) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (!(this.keyboardVisible == 0 || this.waitingForKeyboardOpen == 0)) {
            this.waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ChatActivityEnterView chatActivityEnterView = this;
        int i3 = i;
        if (i3 == NotificationCenter.emojiDidLoaded) {
            if (chatActivityEnterView.emojiView != null) {
                chatActivityEnterView.emojiView.invalidateViews();
            }
            if (chatActivityEnterView.botKeyboardView != null) {
                chatActivityEnterView.botKeyboardView.invalidateViews();
            }
        } else {
            int i4 = 0;
            if (i3 == NotificationCenter.recordProgressChanged) {
                long longValue = ((Long) objArr[0]).longValue();
                long j = longValue / 1000;
                i3 = ((int) (longValue % 1000)) / 10;
                CharSequence format = String.format("%02d:%02d.%02d", new Object[]{Long.valueOf(j / 60), Long.valueOf(j % 60), Integer.valueOf(i3)});
                if (chatActivityEnterView.lastTimeString == null || !chatActivityEnterView.lastTimeString.equals(format)) {
                    if (chatActivityEnterView.lastTypingSendTime != j && j % 5 == 0) {
                        chatActivityEnterView.lastTypingSendTime = j;
                        MessagesController instance = MessagesController.getInstance(chatActivityEnterView.currentAccount);
                        long j2 = chatActivityEnterView.dialog_id;
                        int i5 = (chatActivityEnterView.videoSendButton == null || chatActivityEnterView.videoSendButton.getTag() == null) ? 1 : 7;
                        instance.sendTyping(j2, i5, 0);
                    }
                    if (chatActivityEnterView.recordTimeText != null) {
                        chatActivityEnterView.recordTimeText.setText(format);
                    }
                }
                if (chatActivityEnterView.recordCircle != null) {
                    chatActivityEnterView.recordCircle.setAmplitude(((Double) objArr[1]).doubleValue());
                }
                if (!(chatActivityEnterView.videoSendButton == null || chatActivityEnterView.videoSendButton.getTag() == null || longValue < 59500)) {
                    chatActivityEnterView.startedDraggingX = -1.0f;
                    chatActivityEnterView.delegate.needStartRecordVideo(3);
                }
            } else if (i3 != NotificationCenter.closeChats) {
                Integer num;
                int i6;
                if (i3 != NotificationCenter.recordStartError) {
                    if (i3 != NotificationCenter.recordStopped) {
                        if (i3 == NotificationCenter.recordStarted) {
                            if (!chatActivityEnterView.recordingAudioVideo) {
                                chatActivityEnterView.recordingAudioVideo = true;
                                updateRecordIntefrace();
                            }
                        } else if (i3 == NotificationCenter.audioDidSent) {
                            Object obj = objArr[0];
                            if (obj instanceof VideoEditedInfo) {
                                chatActivityEnterView.videoToSendMessageObject = (VideoEditedInfo) obj;
                                chatActivityEnterView.audioToSendPath = (String) objArr[1];
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
                                chatActivityEnterView.audioToSend = (TL_document) objArr[0];
                                chatActivityEnterView.audioToSendPath = (String) objArr[1];
                                if (chatActivityEnterView.audioToSend != null) {
                                    if (chatActivityEnterView.recordedAudioPanel != null) {
                                        chatActivityEnterView.videoTimelineView.setVisibility(8);
                                        chatActivityEnterView.recordedAudioBackground.setVisibility(0);
                                        chatActivityEnterView.recordedAudioTimeTextView.setVisibility(0);
                                        chatActivityEnterView.recordedAudioPlayButton.setVisibility(0);
                                        chatActivityEnterView.recordedAudioSeekBar.setVisibility(0);
                                        Message tL_message = new TL_message();
                                        tL_message.out = true;
                                        tL_message.id = 0;
                                        tL_message.to_id = new TL_peerUser();
                                        Peer peer = tL_message.to_id;
                                        int clientUserId = UserConfig.getInstance(chatActivityEnterView.currentAccount).getClientUserId();
                                        tL_message.from_id = clientUserId;
                                        peer.user_id = clientUserId;
                                        tL_message.date = (int) (System.currentTimeMillis() / 1000);
                                        tL_message.message = TtmlNode.ANONYMOUS_REGION_ID;
                                        tL_message.attachPath = chatActivityEnterView.audioToSendPath;
                                        tL_message.media = new TL_messageMediaDocument();
                                        MessageMedia messageMedia = tL_message.media;
                                        messageMedia.flags |= 3;
                                        tL_message.media.document = chatActivityEnterView.audioToSend;
                                        tL_message.flags |= 768;
                                        chatActivityEnterView.audioToSendMessageObject = new MessageObject(UserConfig.selectedAccount, tL_message, false);
                                        chatActivityEnterView.recordedAudioPanel.setAlpha(1.0f);
                                        chatActivityEnterView.recordedAudioPanel.setVisibility(0);
                                        for (i3 = 0; i3 < chatActivityEnterView.audioToSend.attributes.size(); i3++) {
                                            DocumentAttribute documentAttribute = (DocumentAttribute) chatActivityEnterView.audioToSend.attributes.get(i3);
                                            if (documentAttribute instanceof TL_documentAttributeAudio) {
                                                i3 = documentAttribute.duration;
                                                break;
                                            }
                                        }
                                        i3 = 0;
                                        int i7 = 0;
                                        while (i7 < chatActivityEnterView.audioToSend.attributes.size()) {
                                            DocumentAttribute documentAttribute2 = (DocumentAttribute) chatActivityEnterView.audioToSend.attributes.get(i7);
                                            if (documentAttribute2 instanceof TL_documentAttributeAudio) {
                                                if (documentAttribute2.waveform == null || documentAttribute2.waveform.length == 0) {
                                                    documentAttribute2.waveform = MediaController.getInstance().getWaveform(chatActivityEnterView.audioToSendPath);
                                                }
                                                chatActivityEnterView.recordedAudioSeekBar.setWaveform(documentAttribute2.waveform);
                                                chatActivityEnterView.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(i3 / 60), Integer.valueOf(i3 % 60)}));
                                                closeKeyboard();
                                                hidePopup(false);
                                                checkSendButton(false);
                                            } else {
                                                i7++;
                                            }
                                        }
                                        chatActivityEnterView.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(i3 / 60), Integer.valueOf(i3 % 60)}));
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
                        } else if (i3 == NotificationCenter.audioRouteChanged) {
                            if (chatActivityEnterView.parentActivity != null) {
                                boolean booleanValue = ((Boolean) objArr[0]).booleanValue();
                                Activity activity = chatActivityEnterView.parentActivity;
                                if (!booleanValue) {
                                    i4 = Integer.MIN_VALUE;
                                }
                                activity.setVolumeControlStream(i4);
                            }
                        } else if (i3 == NotificationCenter.messagePlayingDidReset) {
                            if (!(chatActivityEnterView.audioToSendMessageObject == null || MediaController.getInstance().isPlayingMessage(chatActivityEnterView.audioToSendMessageObject))) {
                                chatActivityEnterView.recordedAudioPlayButton.setImageDrawable(chatActivityEnterView.playDrawable);
                                chatActivityEnterView.recordedAudioSeekBar.setProgress(0.0f);
                            }
                        } else if (i3 == NotificationCenter.messagePlayingProgressDidChanged) {
                            num = (Integer) objArr[0];
                            if (chatActivityEnterView.audioToSendMessageObject != null && MediaController.getInstance().isPlayingMessage(chatActivityEnterView.audioToSendMessageObject)) {
                                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                                chatActivityEnterView.audioToSendMessageObject.audioProgress = playingMessageObject.audioProgress;
                                chatActivityEnterView.audioToSendMessageObject.audioProgressSec = playingMessageObject.audioProgressSec;
                                if (!chatActivityEnterView.recordedAudioSeekBar.isDragging()) {
                                    chatActivityEnterView.recordedAudioSeekBar.setProgress(chatActivityEnterView.audioToSendMessageObject.audioProgress);
                                }
                            }
                        } else if (i3 == NotificationCenter.featuredStickersDidLoaded && chatActivityEnterView.emojiButton != null) {
                            chatActivityEnterView.emojiButton.invalidate();
                        }
                    }
                }
                if (chatActivityEnterView.recordingAudioVideo) {
                    i6 = 2;
                    MessagesController.getInstance(chatActivityEnterView.currentAccount).sendTyping(chatActivityEnterView.dialog_id, 2, 0);
                    chatActivityEnterView.recordingAudioVideo = false;
                    updateRecordIntefrace();
                } else {
                    i6 = 2;
                }
                if (i3 == NotificationCenter.recordStopped) {
                    num = (Integer) objArr[0];
                    if (num.intValue() == i6) {
                        chatActivityEnterView.videoTimelineView.setVisibility(0);
                        chatActivityEnterView.recordedAudioBackground.setVisibility(8);
                        chatActivityEnterView.recordedAudioTimeTextView.setVisibility(8);
                        chatActivityEnterView.recordedAudioPlayButton.setVisibility(8);
                        chatActivityEnterView.recordedAudioSeekBar.setVisibility(8);
                        chatActivityEnterView.recordedAudioPanel.setAlpha(1.0f);
                        chatActivityEnterView.recordedAudioPanel.setVisibility(0);
                    } else {
                        num.intValue();
                    }
                }
            } else if (chatActivityEnterView.messageEditText != null && chatActivityEnterView.messageEditText.isFocused()) {
                AndroidUtilities.hideKeyboard(chatActivityEnterView.messageEditText);
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 2 && this.pendingLocationButton != 0) {
            if (iArr.length > 0 && iArr[0] == 0) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
            }
            this.pendingLocationButton = null;
            this.pendingMessageObject = null;
        }
    }

    private void setStickersExpanded(boolean z, boolean z2) {
        if (this.emojiView != null && (!z || this.emojiView.areThereAnyStickers())) {
            if (this.stickersExpanded != z) {
                this.stickersExpanded = z;
                if (this.delegate) {
                    this.delegate.onStickersExpandedChange();
                }
                z = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
                if (this.stickersExpansionAnim != null) {
                    this.stickersExpansionAnim.cancel();
                    this.stickersExpansionAnim = null;
                }
                Animator[] animatorArr;
                if (this.stickersExpanded) {
                    this.stickersExpandedHeight = (((this.sizeNotifierLayout.getHeight() - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
                    this.sizeNotifierLayout.requestLayout();
                    this.sizeNotifierLayout.setForeground(new ScrimDrawable());
                    this.messageEditText.setText(this.messageEditText.getText());
                    if (z2) {
                        z2 = new AnimatorSet();
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - z)});
                        animatorArr[1] = ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - z)});
                        animatorArr[2] = ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", new float[]{1.0f});
                        z2.playTogether(animatorArr);
                        z2.setDuration(400);
                        z2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                        ((ObjectAnimator) z2.getChildAnimations().get(0)).addUpdateListener(new AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ChatActivityEnterView.this.stickersExpansionProgress = ChatActivityEnterView.this.getTranslationY() / ((float) (-(ChatActivityEnterView.this.stickersExpandedHeight - z)));
                                ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                            }
                        });
                        z2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                ChatActivityEnterView.this.stickersExpansionAnim = null;
                                ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                            }
                        });
                        this.stickersExpansionAnim = z2;
                        this.emojiView.setLayerType(2, null);
                        z2.start();
                    } else {
                        this.stickersExpansionProgress = 1.0f;
                        setTranslationY((float) (-(this.stickersExpandedHeight - z)));
                        this.emojiView.setTranslationY((float) (-(this.stickersExpandedHeight - z)));
                        this.stickersArrow.setAnimationProgress(1.0f);
                    }
                } else if (z2) {
                    this.closeAnimationInProgress = true;
                    z2 = new AnimatorSet();
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{0});
                    animatorArr[1] = ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{0});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", new float[]{0.0f});
                    z2.playTogether(animatorArr);
                    z2.setDuration(400);
                    z2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    ((ObjectAnimator) z2.getChildAnimations().get(0)).addUpdateListener(new AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.stickersExpansionProgress = ChatActivityEnterView.this.getTranslationY() / ((float) (-(ChatActivityEnterView.this.stickersExpandedHeight - z)));
                            ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                        }
                    });
                    z2.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ChatActivityEnterView.this.closeAnimationInProgress = false;
                            ChatActivityEnterView.this.stickersExpansionAnim = null;
                            ChatActivityEnterView.this.emojiView.getLayoutParams().height = z;
                            ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                            ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                            ChatActivityEnterView.this.sizeNotifierLayout.setForeground(null);
                            ChatActivityEnterView.this.sizeNotifierLayout.setWillNotDraw(false);
                        }
                    });
                    this.stickersExpansionAnim = z2;
                    this.emojiView.setLayerType(2, null);
                    z2.start();
                } else {
                    this.stickersExpansionProgress = 0.0f;
                    setTranslationY(0.0f);
                    this.emojiView.setTranslationY(0.0f);
                    this.emojiView.getLayoutParams().height = z;
                    this.sizeNotifierLayout.requestLayout();
                    this.sizeNotifierLayout.setForeground(null);
                    this.sizeNotifierLayout.setWillNotDraw(false);
                    this.stickersArrow.setAnimationProgress(0.0f);
                }
            }
        }
    }
}
