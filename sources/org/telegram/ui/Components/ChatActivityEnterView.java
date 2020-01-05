package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
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
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
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
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
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
import org.telegram.tgnet.TLRPC.TL_messageEntityStrike;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUnderline;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EmojiView.DragListener;
import org.telegram.ui.Components.EmojiView.EmojiViewDelegate;
import org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate;
import org.telegram.ui.Components.StickersAlert.StickersAlertDelegate;
import org.telegram.ui.Components.TextStyleSpan.TextStyleRun;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupStickersActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.StickersActivity;

public class ChatActivityEnterView extends FrameLayout implements NotificationCenterDelegate, SizeNotifierFrameLayoutDelegate, StickersAlertDelegate {
    private AccountInstance accountInstance;
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
    private BotKeyboardView botKeyboardView;
    private boolean botKeyboardViewVisible;
    private MessageObject botMessageObject;
    private TL_replyKeyboardMarkup botReplyMarkup;
    private boolean calledRecordRunnable;
    private Drawable cameraDrawable;
    private boolean canWriteToChannel;
    private ImageView cancelBotButton;
    private boolean closeAnimationInProgress;
    private int currentAccount;
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
    private ImageView emojiButton1;
    private ImageView emojiButton2;
    private AnimatorSet emojiButtonAnimation;
    private int emojiPadding;
    private boolean emojiTabOpen;
    private EmojiView emojiView;
    private boolean emojiViewVisible;
    private ImageView expandStickersButton;
    private Runnable focusRunnable;
    private boolean forceShowSendButton;
    private boolean gifsTabOpen;
    private boolean hasBotCommands;
    private boolean hasRecordVideo;
    private boolean ignoreTextChange;
    private Drawable inactinveSendButtonDrawable;
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
                ChatActivityEnterView.this.delegate.needStartRecordVideo(0, true, 0);
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
                    MediaController.getInstance().startRecording(ChatActivityEnterView.this.currentAccount, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.recordingGuid);
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
    private int recordingGuid;
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
    private boolean scheduleButtonHidden;
    private ImageView scheduledButton;
    private AnimatorSet scheduledButtonAnimation;
    private int searchingType;
    private SeekBarWaveform seekBarWaveform;
    private View sendButton;
    private FrameLayout sendButtonContainer;
    private Drawable sendButtonDrawable;
    private Drawable sendButtonInverseDrawable;
    private boolean sendByEnter;
    private Drawable sendDrawable;
    private ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private boolean showKeyboardOnResume;
    private boolean silent;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private LinearLayout slideText;
    private SimpleTextView slowModeButton;
    private int slowModeTimer;
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
    private Runnable updateSlowModeRunnable;
    private ImageView videoSendButton;
    private VideoTimelineView videoTimelineView;
    private VideoEditedInfo videoToSendMessageObject;
    private boolean waitingForKeyboardOpen;
    private WakeLock wakeLock;

    public interface ChatActivityEnterViewDelegate {

        public final /* synthetic */ class -CC {
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
            Drawable access$4200 = isSendButtonVisible() ? ChatActivityEnterView.this.sendDrawable : (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
            access$4200.setBounds(measuredWidth - (access$4200.getIntrinsicWidth() / 2), dp5 - (access$4200.getIntrinsicHeight() / 2), (access$4200.getIntrinsicWidth() / 2) + measuredWidth, dp5 + (access$4200.getIntrinsicHeight() / 2));
            dp5 = (int) (f * 255.0f);
            access$4200.setAlpha(dp5);
            access$4200.draw(canvas2);
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

    /* JADX WARNING: Removed duplicated region for block: B:57:0x04e1  */
    @android.annotation.SuppressLint({"ClickableViewAccessibility"})
    public ChatActivityEnterView(android.app.Activity r27, org.telegram.ui.Components.SizeNotifierFrameLayout r28, org.telegram.ui.ChatActivity r29, boolean r30) {
        /*
        r26 = this;
        r0 = r26;
        r1 = r27;
        r2 = r29;
        r26.<init>(r27);
        r3 = org.telegram.messenger.UserConfig.selectedAccount;
        r0.currentAccount = r3;
        r3 = org.telegram.messenger.AccountInstance.getInstance(r3);
        r0.accountInstance = r3;
        r3 = new org.telegram.ui.Components.ChatActivityEnterView$1;
        r3.<init>();
        r0.mediaMessageButtonsDelegate = r3;
        r3 = 2;
        r3 = new android.widget.ImageView[r3];
        r0.emojiButton = r3;
        r3 = -1;
        r0.currentPopupContentType = r3;
        r0.currentEmojiIcon = r3;
        r4 = 1;
        r0.isPaused = r4;
        r5 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r0.startedDraggingX = r5;
        r5 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r0.distCanMove = r5;
        r0.messageWebPageSearch = r4;
        r5 = new org.telegram.ui.Components.ChatActivityEnterView$2;
        r5.<init>();
        r0.openKeyboardRunnable = r5;
        r5 = new org.telegram.ui.Components.ChatActivityEnterView$3;
        r5.<init>();
        r0.updateExpandabilityRunnable = r5;
        r5 = new org.telegram.ui.Components.ChatActivityEnterView$4;
        r6 = java.lang.Integer.class;
        r7 = "translationY";
        r5.<init>(r6, r7);
        r0.roundedTranslationYProperty = r5;
        r5 = new org.telegram.ui.Components.ChatActivityEnterView$5;
        r6 = java.lang.Float.class;
        r7 = "scale";
        r5.<init>(r6, r7);
        r0.recordCircleScale = r5;
        r5 = new android.graphics.Paint;
        r5.<init>(r4);
        r0.redDotPaint = r5;
        r5 = new org.telegram.ui.Components.ChatActivityEnterView$6;
        r5.<init>();
        r0.onFinishInitCameraRunnable = r5;
        r5 = new org.telegram.ui.Components.ChatActivityEnterView$7;
        r5.<init>();
        r0.recordAudioVideoRunnable = r5;
        r5 = new android.graphics.Paint;
        r5.<init>(r4);
        r0.paint = r5;
        r5 = new android.graphics.Paint;
        r5.<init>(r4);
        r0.paintRecord = r5;
        r5 = new android.graphics.RectF;
        r5.<init>();
        r0.rect = r5;
        r5 = new android.graphics.Paint;
        r5.<init>(r4);
        r0.dotPaint = r5;
        r5 = r0.dotPaint;
        r6 = "chat_emojiPanelNewTrending";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setColor(r6);
        r0.setFocusable(r4);
        r0.setFocusableInTouchMode(r4);
        r5 = 0;
        r0.setWillNotDraw(r5);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.recordStarted;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.recordStartError;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.recordStopped;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.recordProgressChanged;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.closeChats;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.audioDidSent;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.audioRouteChanged;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.featuredStickersDidLoad;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.messageReceivedByServer;
        r6.addObserver(r0, r7);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r7 = org.telegram.messenger.NotificationCenter.sendingMessagesChanged;
        r6.addObserver(r0, r7);
        r6 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r7 = org.telegram.messenger.NotificationCenter.emojiDidLoad;
        r6.addObserver(r0, r7);
        r0.parentActivity = r1;
        r0.parentFragment = r2;
        if (r2 == 0) goto L_0x013c;
    L_0x0134:
        r2 = r0.parentFragment;
        r2 = r2.getClassGuid();
        r0.recordingGuid = r2;
    L_0x013c:
        r2 = r28;
        r0.sizeNotifierLayout = r2;
        r2 = r0.sizeNotifierLayout;
        r2.setDelegate(r0);
        r2 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r6 = "send_by_enter";
        r2 = r2.getBoolean(r6, r5);
        r0.sendByEnter = r2;
        r2 = new android.widget.LinearLayout;
        r2.<init>(r1);
        r0.textFieldContainer = r2;
        r2 = r0.textFieldContainer;
        r2.setOrientation(r5);
        r2 = r0.textFieldContainer;
        r2.setClipChildren(r5);
        r2 = r0.textFieldContainer;
        r2.setClipToPadding(r5);
        r2 = r0.textFieldContainer;
        r6 = -1;
        r7 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r8 = 83;
        r9 = 0;
        r10 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r11 = 0;
        r12 = 0;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12);
        r0.addView(r2, r6);
        r2 = new org.telegram.ui.Components.ChatActivityEnterView$8;
        r2.<init>(r1);
        r6 = r0.textFieldContainer;
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r8 = -2;
        r9 = 80;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r8, r7, r9);
        r6.addView(r2, r7);
        r6 = 0;
    L_0x018e:
        r7 = 2;
        r10 = 4;
        r11 = 0;
        r12 = "listSelectorSDK21";
        r13 = "chat_messagePanelIcons";
        r14 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r15 = 21;
        if (r6 >= r7) goto L_0x0239;
    L_0x019c:
        r7 = r0.emojiButton;
        r3 = new org.telegram.ui.Components.ChatActivityEnterView$9;
        r3.<init>(r1);
        r7[r6] = r3;
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r7 = new android.graphics.PorterDuffColorFilter;
        r13 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r8 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r7.<init>(r13, r8);
        r3.setColorFilter(r7);
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r7 = android.widget.ImageView.ScaleType.CENTER_INSIDE;
        r3.setScaleType(r7);
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 < r15) goto L_0x01d3;
    L_0x01c4:
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r7 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7);
        r3.setBackgroundDrawable(r7);
    L_0x01d3:
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r16 = 48;
        r17 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r18 = 83;
        r19 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r2.addView(r3, r7);
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r7 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$jlpojNCIsF-c7hlFcVLHdouZye4;
        r7.<init>(r0);
        r3.setOnClickListener(r7);
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r7 = NUM; // 0x7f0e0020 float:1.8875102E38 double:1.0531621724E-314;
        r8 = "AccDescrEmojiButton";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r3.setContentDescription(r7);
        if (r6 != r4) goto L_0x022d;
    L_0x020a:
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r3.setVisibility(r10);
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r3.setAlpha(r11);
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r3.setScaleX(r14);
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r3.setScaleY(r14);
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r0.emojiButton2 = r3;
        goto L_0x0233;
    L_0x022d:
        r3 = r0.emojiButton;
        r3 = r3[r6];
        r0.emojiButton1 = r3;
    L_0x0233:
        r6 = r6 + 1;
        r3 = -1;
        r8 = -2;
        goto L_0x018e;
    L_0x0239:
        r0.setEmojiButtonImage(r5, r5);
        r3 = new org.telegram.ui.Components.ChatActivityEnterView$10;
        r3.<init>(r1);
        r0.messageEditText = r3;
        r3 = r0.messageEditText;
        r6 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$YXzMuZPPiDr1K7R7cpN8afDWrUQ;
        r6.<init>(r0);
        r3.setDelegate(r6);
        r3 = r0.messageEditText;
        r6 = r0.parentActivity;
        r6 = r6.getWindow();
        r6 = r6.getDecorView();
        r3.setWindowView(r6);
        r3 = r0.parentFragment;
        if (r3 == 0) goto L_0x0265;
    L_0x0260:
        r3 = r3.getCurrentEncryptedChat();
        goto L_0x0266;
    L_0x0265:
        r3 = 0;
    L_0x0266:
        r6 = r0.messageEditText;
        r7 = r26.supportsSendingNewEntities();
        r6.setAllowTextEntitiesIntersection(r7);
        r26.updateFieldHint();
        r6 = NUM; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        if (r3 == 0) goto L_0x0278;
    L_0x0276:
        r6 = NUM; // 0x11000000 float:1.00974196E-28 double:1.40913783E-315;
    L_0x0278:
        r3 = r0.messageEditText;
        r3.setImeOptions(r6);
        r3 = r0.messageEditText;
        r6 = r3.getInputType();
        r6 = r6 | 16384;
        r7 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r6 = r6 | r7;
        r3.setInputType(r6);
        r3 = r0.messageEditText;
        r3.setSingleLine(r5);
        r3 = r0.messageEditText;
        r6 = 6;
        r3.setMaxLines(r6);
        r3 = r0.messageEditText;
        r6 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r3.setTextSize(r4, r6);
        r3 = r0.messageEditText;
        r3.setGravity(r9);
        r3 = r0.messageEditText;
        r6 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r3.setPadding(r5, r6, r5, r7);
        r3 = r0.messageEditText;
        r6 = 0;
        r3.setBackgroundDrawable(r6);
        r3 = r0.messageEditText;
        r6 = "chat_messagePanelText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r3.setTextColor(r6);
        r3 = r0.messageEditText;
        r6 = "chat_messagePanelHint";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r3.setHintColor(r6);
        r3 = r0.messageEditText;
        r6 = "chat_messagePanelHint";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r3.setHintTextColor(r6);
        r3 = r0.messageEditText;
        r6 = "chat_messagePanelCursor";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r3.setCursorColor(r6);
        r3 = r0.messageEditText;
        r16 = -1;
        r17 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r18 = 80;
        r19 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r20 = 0;
        if (r30 == 0) goto L_0x02f8;
    L_0x02f3:
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r21 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x02fc;
    L_0x02f8:
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r21 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
    L_0x02fc:
        r22 = 0;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r2.addView(r3, r6);
        r3 = r0.messageEditText;
        r6 = new org.telegram.ui.Components.ChatActivityEnterView$11;
        r6.<init>();
        r3.setOnKeyListener(r6);
        r3 = r0.messageEditText;
        r6 = new org.telegram.ui.Components.ChatActivityEnterView$12;
        r6.<init>();
        r3.setOnEditorActionListener(r6);
        r3 = r0.messageEditText;
        r6 = new org.telegram.ui.Components.ChatActivityEnterView$13;
        r6.<init>();
        r3.addTextChangedListener(r6);
        r3 = 8;
        r6 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r7 = 48;
        if (r30 == 0) goto L_0x0511;
    L_0x032b:
        r8 = r0.parentFragment;
        if (r8 == 0) goto L_0x03bc;
    L_0x032f:
        r8 = r27.getResources();
        r14 = NUM; // 0x7var_ float:1.79452E38 double:1.0529356547E-314;
        r8 = r8.getDrawable(r14);
        r8 = r8.mutate();
        r14 = r27.getResources();
        r10 = NUM; // 0x7var_ float:1.7945203E38 double:1.052935655E-314;
        r10 = r14.getDrawable(r10);
        r10 = r10.mutate();
        r14 = new android.graphics.PorterDuffColorFilter;
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r9 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r14.<init>(r11, r9);
        r8.setColorFilter(r14);
        r9 = new android.graphics.PorterDuffColorFilter;
        r11 = "chat_recordedVoiceDot";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r14 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r9.<init>(r11, r14);
        r10.setColorFilter(r9);
        r9 = new org.telegram.ui.Components.CombinedDrawable;
        r9.<init>(r8, r10);
        r8 = new android.widget.ImageView;
        r8.<init>(r1);
        r0.scheduledButton = r8;
        r8 = r0.scheduledButton;
        r8.setImageDrawable(r9);
        r8 = r0.scheduledButton;
        r8.setVisibility(r3);
        r8 = r0.scheduledButton;
        r9 = NUM; // 0x7f0e09ad float:1.8880061E38 double:1.0531633804E-314;
        r10 = "ScheduledMessages";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r8.setContentDescription(r9);
        r8 = r0.scheduledButton;
        r9 = android.widget.ImageView.ScaleType.CENTER;
        r8.setScaleType(r9);
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r15) goto L_0x03a7;
    L_0x039a:
        r8 = r0.scheduledButton;
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9);
        r8.setBackgroundDrawable(r9);
    L_0x03a7:
        r8 = r0.scheduledButton;
        r9 = 85;
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r7, r9);
        r2.addView(r8, r9);
        r8 = r0.scheduledButton;
        r9 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$ePEXGP4V8TnztLq_2EEcKkpts_k;
        r9.<init>(r0);
        r8.setOnClickListener(r9);
    L_0x03bc:
        r8 = new android.widget.LinearLayout;
        r8.<init>(r1);
        r0.attachLayout = r8;
        r8 = r0.attachLayout;
        r8.setOrientation(r5);
        r8 = r0.attachLayout;
        r8.setEnabled(r5);
        r8 = r0.attachLayout;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r9 = (float) r9;
        r8.setPivotX(r9);
        r8 = r0.attachLayout;
        r9 = 85;
        r10 = -2;
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r7, r9);
        r2.addView(r8, r9);
        r8 = new android.widget.ImageView;
        r8.<init>(r1);
        r0.botButton = r8;
        r8 = r0.botButton;
        r9 = new android.graphics.PorterDuffColorFilter;
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r11 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r9.<init>(r10, r11);
        r8.setColorFilter(r9);
        r8 = r0.botButton;
        r9 = NUM; // 0x7var_ float:1.7945199E38 double:1.052935654E-314;
        r8.setImageResource(r9);
        r8 = r0.botButton;
        r9 = android.widget.ImageView.ScaleType.CENTER;
        r8.setScaleType(r9);
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r15) goto L_0x041a;
    L_0x040d:
        r8 = r0.botButton;
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9);
        r8.setBackgroundDrawable(r9);
    L_0x041a:
        r8 = r0.botButton;
        r8.setVisibility(r3);
        r8 = r0.attachLayout;
        r9 = r0.botButton;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r7);
        r8.addView(r9, r10);
        r8 = r0.botButton;
        r9 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$sA9S70ZL3Nng90HptN-7RpznwZo;
        r9.<init>(r0);
        r8.setOnClickListener(r9);
        r8 = new android.widget.ImageView;
        r8.<init>(r1);
        r0.notifyButton = r8;
        r8 = r0.notifyButton;
        r9 = r0.silent;
        if (r9 == 0) goto L_0x0445;
    L_0x0441:
        r9 = NUM; // 0x7var_b float:1.7945217E38 double:1.0529356587E-314;
        goto L_0x0448;
    L_0x0445:
        r9 = NUM; // 0x7var_c float:1.7945219E38 double:1.052935659E-314;
    L_0x0448:
        r8.setImageResource(r9);
        r8 = r0.notifyButton;
        r9 = r0.silent;
        if (r9 == 0) goto L_0x0457;
    L_0x0451:
        r9 = NUM; // 0x7f0e0019 float:1.8875088E38 double:1.053162169E-314;
        r10 = "AccDescrChanSilentOn";
        goto L_0x045c;
    L_0x0457:
        r9 = NUM; // 0x7f0e0018 float:1.8875086E38 double:1.0531621685E-314;
        r10 = "AccDescrChanSilentOff";
    L_0x045c:
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r8.setContentDescription(r9);
        r8 = r0.notifyButton;
        r9 = new android.graphics.PorterDuffColorFilter;
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r11 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r9.<init>(r10, r11);
        r8.setColorFilter(r9);
        r8 = r0.notifyButton;
        r9 = android.widget.ImageView.ScaleType.CENTER;
        r8.setScaleType(r9);
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r15) goto L_0x048b;
    L_0x047e:
        r8 = r0.notifyButton;
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9);
        r8.setBackgroundDrawable(r9);
    L_0x048b:
        r8 = r0.notifyButton;
        r9 = r0.canWriteToChannel;
        if (r9 == 0) goto L_0x049d;
    L_0x0491:
        r9 = r0.delegate;
        if (r9 == 0) goto L_0x049b;
    L_0x0495:
        r9 = r9.hasScheduledMessages();
        if (r9 != 0) goto L_0x049d;
    L_0x049b:
        r9 = 0;
        goto L_0x049f;
    L_0x049d:
        r9 = 8;
    L_0x049f:
        r8.setVisibility(r9);
        r8 = r0.attachLayout;
        r9 = r0.notifyButton;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r7);
        r8.addView(r9, r10);
        r8 = r0.notifyButton;
        r9 = new org.telegram.ui.Components.ChatActivityEnterView$14;
        r9.<init>();
        r8.setOnClickListener(r9);
        r8 = new android.widget.ImageView;
        r8.<init>(r1);
        r0.attachButton = r8;
        r8 = r0.attachButton;
        r9 = new android.graphics.PorterDuffColorFilter;
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r11 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r9.<init>(r10, r11);
        r8.setColorFilter(r9);
        r8 = r0.attachButton;
        r9 = NUM; // 0x7var_ float:1.7945195E38 double:1.052935653E-314;
        r8.setImageResource(r9);
        r8 = r0.attachButton;
        r9 = android.widget.ImageView.ScaleType.CENTER;
        r8.setScaleType(r9);
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r15) goto L_0x04ee;
    L_0x04e1:
        r8 = r0.attachButton;
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9);
        r8.setBackgroundDrawable(r9);
    L_0x04ee:
        r8 = r0.attachLayout;
        r9 = r0.attachButton;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r7);
        r8.addView(r9, r10);
        r8 = r0.attachButton;
        r9 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$pQGz59T4TelJWdtUhc7Wjoj7V9g;
        r9.<init>(r0);
        r8.setOnClickListener(r9);
        r8 = r0.attachButton;
        r9 = NUM; // 0x7f0e000e float:1.8875066E38 double:1.0531621635E-314;
        r10 = "AccDescrAttachButton";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r8.setContentDescription(r9);
    L_0x0511:
        r8 = new android.widget.FrameLayout;
        r8.<init>(r1);
        r0.recordedAudioPanel = r8;
        r8 = r0.recordedAudioPanel;
        r9 = r0.audioToSend;
        if (r9 != 0) goto L_0x0521;
    L_0x051e:
        r9 = 8;
        goto L_0x0522;
    L_0x0521:
        r9 = 0;
    L_0x0522:
        r8.setVisibility(r9);
        r8 = r0.recordedAudioPanel;
        r9 = "chat_messagePanelBackground";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r8.setBackgroundColor(r10);
        r8 = r0.recordedAudioPanel;
        r8.setFocusable(r4);
        r8 = r0.recordedAudioPanel;
        r8.setFocusableInTouchMode(r4);
        r8 = r0.recordedAudioPanel;
        r8.setClickable(r4);
        r8 = r0.recordedAudioPanel;
        r10 = -1;
        r11 = 80;
        r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r7, r11);
        r2.addView(r8, r14);
        r8 = new android.widget.ImageView;
        r8.<init>(r1);
        r0.recordDeleteImageView = r8;
        r8 = r0.recordDeleteImageView;
        r10 = android.widget.ImageView.ScaleType.CENTER;
        r8.setScaleType(r10);
        r8 = r0.recordDeleteImageView;
        r10 = NUM; // 0x7var_ce float:1.7945515E38 double:1.0529357313E-314;
        r8.setImageResource(r10);
        r8 = r0.recordDeleteImageView;
        r10 = new android.graphics.PorterDuffColorFilter;
        r11 = "chat_messagePanelVoiceDelete";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r14 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r10.<init>(r11, r14);
        r8.setColorFilter(r10);
        r8 = r0.recordDeleteImageView;
        r10 = NUM; // 0x7f0e0381 float:1.8876857E38 double:1.0531626E-314;
        r11 = "Delete";
        r10 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r8.setContentDescription(r10);
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r15) goto L_0x0592;
    L_0x0585:
        r8 = r0.recordDeleteImageView;
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r10 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r10);
        r8.setBackgroundDrawable(r10);
    L_0x0592:
        r8 = r0.recordedAudioPanel;
        r10 = r0.recordDeleteImageView;
        r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6);
        r8.addView(r10, r11);
        r8 = r0.recordDeleteImageView;
        r10 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$0PqxYA4Sm4DXenEKSdETEwDwen0;
        r10.<init>(r0);
        r8.setOnClickListener(r10);
        r8 = new org.telegram.ui.Components.VideoTimelineView;
        r8.<init>(r1);
        r0.videoTimelineView = r8;
        r8 = r0.videoTimelineView;
        r10 = "chat_messagePanelVideoFrame";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r8.setColor(r10);
        r8 = r0.videoTimelineView;
        r8.setRoundFrames(r4);
        r8 = r0.videoTimelineView;
        r10 = new org.telegram.ui.Components.ChatActivityEnterView$15;
        r10.<init>();
        r8.setDelegate(r10);
        r8 = r0.recordedAudioPanel;
        r10 = r0.videoTimelineView;
        r19 = -1;
        r20 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r21 = 19;
        r22 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r8.addView(r10, r11);
        r8 = new android.view.View;
        r8.<init>(r1);
        r0.recordedAudioBackground = r8;
        r8 = r0.recordedAudioBackground;
        r10 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = "chat_recordedVoiceBackground";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r10 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r10, r11);
        r8.setBackgroundDrawable(r10);
        r8 = r0.recordedAudioPanel;
        r10 = r0.recordedAudioBackground;
        r20 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r22 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r8.addView(r10, r11);
        r8 = new org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView;
        r8.<init>(r1);
        r0.recordedAudioSeekBar = r8;
        r8 = r0.recordedAudioPanel;
        r10 = r0.recordedAudioSeekBar;
        r20 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r22 = NUM; // 0x42b80000 float:92.0 double:5.530347917E-315;
        r24 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r8.addView(r10, r11);
        r8 = NUM; // 0x7var_ float:1.7945894E38 double:1.0529358237E-314;
        r10 = "chat_recordedVoicePlayPause";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r11 = "chat_recordedVoicePlayPausePressed";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r8 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorDrawable(r1, r8, r10, r11);
        r0.playDrawable = r8;
        r8 = NUM; // 0x7var_ float:1.7945892E38 double:1.052935823E-314;
        r10 = "chat_recordedVoicePlayPause";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r11 = "chat_recordedVoicePlayPausePressed";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r8 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorDrawable(r1, r8, r10, r11);
        r0.pauseDrawable = r8;
        r8 = new android.widget.ImageView;
        r8.<init>(r1);
        r0.recordedAudioPlayButton = r8;
        r8 = r0.recordedAudioPlayButton;
        r10 = r0.playDrawable;
        r8.setImageDrawable(r10);
        r8 = r0.recordedAudioPlayButton;
        r10 = android.widget.ImageView.ScaleType.CENTER;
        r8.setScaleType(r10);
        r8 = r0.recordedAudioPlayButton;
        r10 = NUM; // 0x7f0e000a float:1.8875058E38 double:1.0531621616E-314;
        r11 = "AccActionPlay";
        r10 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r8.setContentDescription(r10);
        r8 = r0.recordedAudioPanel;
        r10 = r0.recordedAudioPlayButton;
        r19 = 48;
        r20 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r21 = 83;
        r22 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r24 = 0;
        r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r8.addView(r10, r11);
        r8 = r0.recordedAudioPlayButton;
        r10 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$H5pZkFIP6ktOW7HudlRmk-xmmkM;
        r10.<init>(r0);
        r8.setOnClickListener(r10);
        r8 = new android.widget.TextView;
        r8.<init>(r1);
        r0.recordedAudioTimeTextView = r8;
        r8 = r0.recordedAudioTimeTextView;
        r10 = "chat_messagePanelVoiceDuration";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r8.setTextColor(r10);
        r8 = r0.recordedAudioTimeTextView;
        r10 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r8.setTextSize(r4, r10);
        r8 = r0.recordedAudioPanel;
        r10 = r0.recordedAudioTimeTextView;
        r19 = -2;
        r20 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r21 = 21;
        r22 = 0;
        r24 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r8.addView(r10, r11);
        r8 = new android.widget.FrameLayout;
        r8.<init>(r1);
        r0.recordPanel = r8;
        r8 = r0.recordPanel;
        r8.setVisibility(r3);
        r8 = r0.recordPanel;
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r8.setBackgroundColor(r10);
        r8 = r0.recordPanel;
        r10 = -1;
        r11 = 80;
        r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r7, r11);
        r2.addView(r8, r14);
        r2 = r0.recordPanel;
        r8 = org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$Uo3sK08hLv7NAPRSM5RagKUbmWg.INSTANCE;
        r2.setOnTouchListener(r8);
        r2 = new android.widget.LinearLayout;
        r2.<init>(r1);
        r0.slideText = r2;
        r2 = r0.slideText;
        r2.setOrientation(r5);
        r2 = r0.recordPanel;
        r8 = r0.slideText;
        r21 = 17;
        r22 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r24 = 0;
        r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r2.addView(r8, r10);
        r2 = new android.widget.ImageView;
        r2.<init>(r1);
        r0.recordCancelImage = r2;
        r2 = r0.recordCancelImage;
        r8 = NUM; // 0x7var_a0 float:1.794594E38 double:1.052935835E-314;
        r2.setImageResource(r8);
        r2 = r0.recordCancelImage;
        r8 = new android.graphics.PorterDuffColorFilter;
        r10 = "chat_recordVoiceCancel";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r11 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r8.<init>(r10, r11);
        r2.setColorFilter(r8);
        r2 = r0.slideText;
        r8 = r0.recordCancelImage;
        r20 = -2;
        r21 = 16;
        r22 = 0;
        r23 = 1;
        r24 = 0;
        r25 = 0;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r19, r20, r21, r22, r23, r24, r25);
        r2.addView(r8, r10);
        r2 = new android.widget.TextView;
        r2.<init>(r1);
        r0.recordCancelText = r2;
        r2 = r0.recordCancelText;
        r8 = NUM; // 0x7f0e0a5c float:1.8880416E38 double:1.053163467E-314;
        r10 = "SlideToCancel";
        r8 = org.telegram.messenger.LocaleController.getString(r10, r8);
        r2.setText(r8);
        r2 = r0.recordCancelText;
        r8 = "chat_recordVoiceCancel";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r2.setTextColor(r8);
        r2 = r0.recordCancelText;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2.setTextSize(r4, r8);
        r2 = r0.slideText;
        r8 = r0.recordCancelText;
        r22 = 6;
        r23 = 0;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r19, r20, r21, r22, r23, r24, r25);
        r2.addView(r8, r10);
        r2 = new android.widget.TextView;
        r2.<init>(r1);
        r0.recordSendText = r2;
        r2 = r0.recordSendText;
        r8 = NUM; // 0x7f0e0213 float:1.8876115E38 double:1.053162419E-314;
        r10 = "Cancel";
        r8 = org.telegram.messenger.LocaleController.getString(r10, r8);
        r8 = r8.toUpperCase();
        r2.setText(r8);
        r2 = r0.recordSendText;
        r8 = "chat_fieldOverlayText";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r2.setTextColor(r8);
        r2 = r0.recordSendText;
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2.setTextSize(r4, r8);
        r2 = r0.recordSendText;
        r8 = 17;
        r2.setGravity(r8);
        r2 = r0.recordSendText;
        r8 = "fonts/rmedium.ttf";
        r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8);
        r2.setTypeface(r8);
        r2 = r0.recordSendText;
        r8 = 0;
        r2.setAlpha(r8);
        r2 = r0.recordSendText;
        r8 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r2.setPadding(r8, r5, r5, r5);
        r2 = r0.recordSendText;
        r8 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$O75UMeYLEo03AAko_H9ah1gZ_GY;
        r8.<init>(r0);
        r2.setOnClickListener(r8);
        r2 = r0.recordPanel;
        r8 = r0.recordSendText;
        r20 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r21 = 49;
        r22 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r2.addView(r8, r10);
        r2 = new android.widget.LinearLayout;
        r2.<init>(r1);
        r0.recordTimeContainer = r2;
        r2 = r0.recordTimeContainer;
        r2.setOrientation(r5);
        r2 = r0.recordTimeContainer;
        r8 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r2.setPadding(r8, r5, r5, r5);
        r2 = r0.recordTimeContainer;
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r2.setBackgroundColor(r8);
        r2 = r0.recordPanel;
        r8 = r0.recordTimeContainer;
        r10 = 16;
        r11 = -2;
        r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r11, r10);
        r2.addView(r8, r10);
        r2 = new org.telegram.ui.Components.ChatActivityEnterView$RecordDot;
        r2.<init>(r1);
        r0.recordDot = r2;
        r2 = r0.recordTimeContainer;
        r8 = r0.recordDot;
        r19 = 11;
        r20 = 11;
        r21 = 16;
        r22 = 0;
        r23 = 1;
        r24 = 0;
        r25 = 0;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r19, r20, r21, r22, r23, r24, r25);
        r2.addView(r8, r10);
        r2 = new android.widget.TextView;
        r2.<init>(r1);
        r0.recordTimeText = r2;
        r2 = r0.recordTimeText;
        r8 = "chat_recordTime";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r2.setTextColor(r8);
        r2 = r0.recordTimeText;
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2.setTextSize(r4, r8);
        r2 = r0.recordTimeContainer;
        r8 = r0.recordTimeText;
        r19 = -2;
        r20 = -2;
        r22 = 6;
        r23 = 0;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r19, r20, r21, r22, r23, r24, r25);
        r2.addView(r8, r10);
        r2 = new android.widget.FrameLayout;
        r2.<init>(r1);
        r0.sendButtonContainer = r2;
        r2 = r0.sendButtonContainer;
        r2.setClipChildren(r5);
        r2 = r0.sendButtonContainer;
        r2.setClipToPadding(r5);
        r2 = r0.textFieldContainer;
        r8 = r0.sendButtonContainer;
        r10 = 80;
        r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r7, r10);
        r2.addView(r8, r11);
        r2 = new android.widget.FrameLayout;
        r2.<init>(r1);
        r0.audioVideoButtonContainer = r2;
        r2 = r0.audioVideoButtonContainer;
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r2.setBackgroundColor(r8);
        r2 = r0.audioVideoButtonContainer;
        r2.setSoundEffectsEnabled(r5);
        r2 = r0.sendButtonContainer;
        r8 = r0.audioVideoButtonContainer;
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6);
        r2.addView(r8, r9);
        r2 = r0.audioVideoButtonContainer;
        r8 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$gEbPm1kOxgvg0eNfia8B92v9dV4;
        r8.<init>(r0);
        r2.setOnTouchListener(r8);
        r2 = new android.widget.ImageView;
        r2.<init>(r1);
        r0.audioSendButton = r2;
        r2 = r0.audioSendButton;
        r8 = android.widget.ImageView.ScaleType.CENTER_INSIDE;
        r2.setScaleType(r8);
        r2 = r0.audioSendButton;
        r8 = new android.graphics.PorterDuffColorFilter;
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r10 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r8.<init>(r9, r10);
        r2.setColorFilter(r8);
        r2 = r0.audioSendButton;
        r8 = NUM; // 0x7var_a float:1.7945215E38 double:1.052935658E-314;
        r2.setImageResource(r8);
        r2 = r0.audioSendButton;
        r8 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r2.setPadding(r5, r5, r8, r5);
        r2 = r0.audioSendButton;
        r8 = NUM; // 0x7f0e0056 float:1.8875212E38 double:1.053162199E-314;
        r9 = "AccDescrVoiceMessage";
        r8 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r2.setContentDescription(r8);
        r2 = r0.audioSendButton;
        r2.setFocusable(r4);
        r2 = r0.audioSendButton;
        r8 = r0.mediaMessageButtonsDelegate;
        r2.setAccessibilityDelegate(r8);
        r2 = r0.audioVideoButtonContainer;
        r8 = r0.audioSendButton;
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6);
        r2.addView(r8, r9);
        if (r30 == 0) goto L_0x0950;
    L_0x08fa:
        r2 = new android.widget.ImageView;
        r2.<init>(r1);
        r0.videoSendButton = r2;
        r2 = r0.videoSendButton;
        r8 = android.widget.ImageView.ScaleType.CENTER_INSIDE;
        r2.setScaleType(r8);
        r2 = r0.videoSendButton;
        r8 = new android.graphics.PorterDuffColorFilter;
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r10 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r8.<init>(r9, r10);
        r2.setColorFilter(r8);
        r2 = r0.videoSendButton;
        r8 = NUM; // 0x7var_ float:1.794523E38 double:1.0529356616E-314;
        r2.setImageResource(r8);
        r2 = r0.videoSendButton;
        r8 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r2.setPadding(r5, r5, r8, r5);
        r2 = r0.videoSendButton;
        r8 = NUM; // 0x7f0e0054 float:1.8875208E38 double:1.053162198E-314;
        r9 = "AccDescrVideoMessage";
        r8 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r2.setContentDescription(r8);
        r2 = r0.videoSendButton;
        r2.setFocusable(r4);
        r2 = r0.videoSendButton;
        r8 = r0.mediaMessageButtonsDelegate;
        r2.setAccessibilityDelegate(r8);
        r2 = r0.audioVideoButtonContainer;
        r8 = r0.videoSendButton;
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6);
        r2.addView(r8, r9);
    L_0x0950:
        r2 = new org.telegram.ui.Components.ChatActivityEnterView$RecordCircle;
        r2.<init>(r1);
        r0.recordCircle = r2;
        r2 = r0.recordCircle;
        r2.setVisibility(r3);
        r2 = r0.sizeNotifierLayout;
        r8 = r0.recordCircle;
        r19 = 124; // 0x7c float:1.74E-43 double:6.13E-322;
        r20 = NUM; // 0x43420000 float:194.0 double:5.575031056E-315;
        r21 = 85;
        r22 = 0;
        r23 = 0;
        r24 = -NUM; // 0xffffffffCLASSNAME float:-36.0 double:NaN;
        r25 = 0;
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r2.addView(r8, r9);
        r2 = new android.widget.ImageView;
        r2.<init>(r1);
        r0.cancelBotButton = r2;
        r2 = r0.cancelBotButton;
        r8 = 4;
        r2.setVisibility(r8);
        r2 = r0.cancelBotButton;
        r8 = android.widget.ImageView.ScaleType.CENTER_INSIDE;
        r2.setScaleType(r8);
        r2 = r0.cancelBotButton;
        r8 = new org.telegram.ui.Components.CloseProgressDrawable2;
        r8.<init>();
        r0.progressDrawable = r8;
        r2.setImageDrawable(r8);
        r2 = r0.cancelBotButton;
        r8 = NUM; // 0x7f0e0213 float:1.8876115E38 double:1.053162419E-314;
        r9 = "Cancel";
        r8 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r2.setContentDescription(r8);
        r2 = r0.progressDrawable;
        r8 = new android.graphics.PorterDuffColorFilter;
        r9 = "chat_messagePanelCancelInlineBot";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r10 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r8.<init>(r9, r10);
        r2.setColorFilter(r8);
        r2 = r0.cancelBotButton;
        r2.setSoundEffectsEnabled(r5);
        r2 = r0.cancelBotButton;
        r8 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r2.setScaleX(r8);
        r2 = r0.cancelBotButton;
        r2.setScaleY(r8);
        r2 = r0.cancelBotButton;
        r8 = 0;
        r2.setAlpha(r8);
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r15) goto L_0x09de;
    L_0x09d1:
        r2 = r0.cancelBotButton;
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8);
        r2.setBackgroundDrawable(r8);
    L_0x09de:
        r2 = r0.sendButtonContainer;
        r8 = r0.cancelBotButton;
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6);
        r2.addView(r8, r9);
        r2 = r0.cancelBotButton;
        r8 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$rXJQBzkQ-6_4ly_28Plp5SExQgU;
        r8.<init>(r0);
        r2.setOnClickListener(r8);
        r2 = r26.isInScheduleMode();
        if (r2 == 0) goto L_0x0a27;
    L_0x09f9:
        r2 = r27.getResources();
        r8 = NUM; // 0x7var_e float:1.7945223E38 double:1.05293566E-314;
        r2 = r2.getDrawable(r8);
        r2 = r2.mutate();
        r0.sendButtonDrawable = r2;
        r2 = r27.getResources();
        r2 = r2.getDrawable(r8);
        r2 = r2.mutate();
        r0.sendButtonInverseDrawable = r2;
        r2 = r27.getResources();
        r2 = r2.getDrawable(r8);
        r2 = r2.mutate();
        r0.inactinveSendButtonDrawable = r2;
        goto L_0x0a54;
    L_0x0a27:
        r2 = r27.getResources();
        r8 = NUM; // 0x7var_ float:1.7945176E38 double:1.052935649E-314;
        r2 = r2.getDrawable(r8);
        r2 = r2.mutate();
        r0.sendButtonDrawable = r2;
        r2 = r27.getResources();
        r2 = r2.getDrawable(r8);
        r2 = r2.mutate();
        r0.sendButtonInverseDrawable = r2;
        r2 = r27.getResources();
        r2 = r2.getDrawable(r8);
        r2 = r2.mutate();
        r0.inactinveSendButtonDrawable = r2;
    L_0x0a54:
        r2 = new org.telegram.ui.Components.ChatActivityEnterView$16;
        r2.<init>(r1);
        r0.sendButton = r2;
        r2 = r0.sendButton;
        r8 = 4;
        r2.setVisibility(r8);
        r2 = "chat_messagePanelSend";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r8 = r0.sendButton;
        r9 = NUM; // 0x7f0e09e0 float:1.8880165E38 double:1.0531634056E-314;
        r10 = "Send";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r8.setContentDescription(r9);
        r8 = r0.sendButton;
        r8.setSoundEffectsEnabled(r5);
        r8 = r0.sendButton;
        r9 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r8.setScaleX(r9);
        r8 = r0.sendButton;
        r8.setScaleY(r9);
        r8 = r0.sendButton;
        r9 = 0;
        r8.setAlpha(r9);
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r15) goto L_0x0aac;
    L_0x0a91:
        r8 = r0.sendButton;
        r9 = 24;
        r10 = android.graphics.Color.red(r2);
        r11 = android.graphics.Color.green(r2);
        r2 = android.graphics.Color.blue(r2);
        r2 = android.graphics.Color.argb(r9, r10, r11, r2);
        r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r4);
        r8.setBackgroundDrawable(r2);
    L_0x0aac:
        r2 = r0.sendButtonContainer;
        r4 = r0.sendButton;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6);
        r2.addView(r4, r8);
        r2 = r0.sendButton;
        r4 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$HKRsckUXlNj7cZuyU8v3eg-vryE;
        r4.<init>(r0);
        r2.setOnClickListener(r4);
        r2 = r0.sendButton;
        r4 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$nEg_Vj2QPwH3tdG2rclMcvQTAgo;
        r4.<init>(r0);
        r2.setOnLongClickListener(r4);
        r2 = new org.telegram.ui.ActionBar.SimpleTextView;
        r2.<init>(r1);
        r0.slowModeButton = r2;
        r2 = r0.slowModeButton;
        r4 = 18;
        r2.setTextSize(r4);
        r2 = r0.slowModeButton;
        r4 = 4;
        r2.setVisibility(r4);
        r2 = r0.slowModeButton;
        r2.setSoundEffectsEnabled(r5);
        r2 = r0.slowModeButton;
        r4 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r2.setScaleX(r4);
        r2 = r0.slowModeButton;
        r2.setScaleY(r4);
        r2 = r0.slowModeButton;
        r4 = 0;
        r2.setAlpha(r4);
        r2 = r0.slowModeButton;
        r4 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2.setPadding(r5, r5, r4, r5);
        r2 = r0.slowModeButton;
        r2.setGravity(r15);
        r2 = r0.slowModeButton;
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r2.setTextColor(r4);
        r2 = r0.sendButtonContainer;
        r4 = r0.slowModeButton;
        r8 = 64;
        r9 = 53;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r7, r9);
        r2.addView(r4, r8);
        r2 = r0.slowModeButton;
        r4 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$7Ap7L1yuDmfLj_OdpsYRD0llSyY;
        r4.<init>(r0);
        r2.setOnClickListener(r4);
        r2 = r0.slowModeButton;
        r4 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$pHTgHSAKpIysTcIE-dI2T8MJcPA;
        r4.<init>(r0);
        r2.setOnLongClickListener(r4);
        r2 = new android.widget.ImageView;
        r2.<init>(r1);
        r0.expandStickersButton = r2;
        r2 = r0.expandStickersButton;
        r4 = android.widget.ImageView.ScaleType.CENTER;
        r2.setScaleType(r4);
        r2 = r0.expandStickersButton;
        r4 = new org.telegram.ui.Components.AnimatedArrowDrawable;
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r4.<init>(r8, r5);
        r0.stickersArrow = r4;
        r2.setImageDrawable(r4);
        r2 = r0.expandStickersButton;
        r2.setVisibility(r3);
        r2 = r0.expandStickersButton;
        r4 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r2.setScaleX(r4);
        r2 = r0.expandStickersButton;
        r2.setScaleY(r4);
        r2 = r0.expandStickersButton;
        r4 = 0;
        r2.setAlpha(r4);
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r15) goto L_0x0b7a;
    L_0x0b6d:
        r2 = r0.expandStickersButton;
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4);
        r2.setBackgroundDrawable(r4);
    L_0x0b7a:
        r2 = r0.sendButtonContainer;
        r4 = r0.expandStickersButton;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6);
        r2.addView(r4, r8);
        r2 = r0.expandStickersButton;
        r4 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$PClI0M3N-bvhUiGON6_IxSsaURQ;
        r4.<init>(r0);
        r2.setOnClickListener(r4);
        r2 = r0.expandStickersButton;
        r4 = NUM; // 0x7f0e0021 float:1.8875104E38 double:1.053162173E-314;
        r8 = "AccDescrExpandPanel";
        r4 = org.telegram.messenger.LocaleController.getString(r8, r4);
        r2.setContentDescription(r4);
        r2 = new android.widget.FrameLayout;
        r2.<init>(r1);
        r0.doneButtonContainer = r2;
        r2 = r0.doneButtonContainer;
        r2.setVisibility(r3);
        r2 = r0.textFieldContainer;
        r3 = r0.doneButtonContainer;
        r4 = 80;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r7, r4);
        r2.addView(r3, r4);
        r2 = r0.doneButtonContainer;
        r3 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$9mFCDdWzowA7_Vky86OkFCUavNc;
        r3.<init>(r0);
        r2.setOnClickListener(r3);
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = "chat_messagePanelSend";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.createCircleDrawable(r2, r3);
        r3 = r27.getResources();
        r4 = NUM; // 0x7var_ float:1.7945207E38 double:1.052935656E-314;
        r3 = r3.getDrawable(r4);
        r3 = r3.mutate();
        r4 = new android.graphics.PorterDuffColorFilter;
        r8 = "chat_messagePanelVoicePressed";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r9 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r4.<init>(r8, r9);
        r3.setColorFilter(r4);
        r4 = new org.telegram.ui.Components.CombinedDrawable;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4.<init>(r2, r3, r5, r8);
        r2 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4.setCustomSize(r2, r3);
        r2 = new android.widget.ImageView;
        r2.<init>(r1);
        r0.doneButtonImage = r2;
        r2 = r0.doneButtonImage;
        r3 = android.widget.ImageView.ScaleType.CENTER;
        r2.setScaleType(r3);
        r2 = r0.doneButtonImage;
        r2.setImageDrawable(r4);
        r2 = r0.doneButtonImage;
        r3 = NUM; // 0x7f0e03e1 float:1.8877052E38 double:1.053162647E-314;
        r4 = "Done";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r2.setContentDescription(r3);
        r2 = r0.doneButtonContainer;
        r3 = r0.doneButtonImage;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6);
        r2.addView(r3, r4);
        r2 = new org.telegram.ui.Components.ContextProgressView;
        r2.<init>(r1, r5);
        r0.doneButtonProgress = r2;
        r1 = r0.doneButtonProgress;
        r2 = 4;
        r1.setVisibility(r2);
        r1 = r0.doneButtonContainer;
        r2 = r0.doneButtonProgress;
        r3 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r4 = -1;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3);
        r1.addView(r2, r3);
        r1 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings();
        r2 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = "kbd_height";
        r2 = r1.getInt(r3, r2);
        r0.keyboardHeight = r2;
        r2 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = "kbd_height_land3";
        r1 = r1.getInt(r3, r2);
        r0.keyboardHeightLand = r1;
        r0.setRecordVideoButtonVisible(r5, r5);
        r0.checkSendButton(r5);
        r26.checkChannelRights();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.<init>(android.app.Activity, org.telegram.ui.Components.SizeNotifierFrameLayout, org.telegram.ui.ChatActivity, boolean):void");
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
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.openScheduledMessages();
        }
    }

    public /* synthetic */ void lambda$new$3$ChatActivityEnterView(View view) {
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
            } else {
                MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.audioToSendMessageObject);
                this.recordedAudioPlayButton.setImageDrawable(this.playDrawable);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            }
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
        int i = 3;
        boolean z = false;
        if (motionEvent.getAction() == 0) {
            if (this.recordCircle.isSendButtonVisible()) {
                if (!this.hasRecordVideo || this.calledRecordRunnable) {
                    this.startedDraggingX = -1.0f;
                    if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                        if (this.recordingAudioVideo && isInScheduleMode()) {
                            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), -$$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY.INSTANCE, -$$Lambda$ChatActivityEnterView$_c9ye99difEKRRO05psDolLw5uU.INSTANCE);
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
                    if (this.recordingAudioVideo && isInScheduleMode()) {
                        AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), -$$Lambda$ChatActivityEnterView$bbN64eZurKUvgp0IAMBkXVy3foY.INSTANCE, -$$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS-0M-hNsaHk.INSTANCE);
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
                    MediaController.getInstance().stopRecording(0, false, 0);
                } else {
                    CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                    this.delegate.needStartRecordVideo(2, true, 0);
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

    public /* synthetic */ void lambda$new$19$ChatActivityEnterView(View view) {
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

    private boolean onSendLongClick(View view) {
        View view2 = view;
        if (!(this.parentFragment == null || isInScheduleMode() || this.parentFragment.getCurrentEncryptedChat() != null)) {
            int i;
            this.parentFragment.getCurrentChat();
            User currentUser = this.parentFragment.getCurrentUser();
            if (this.sendPopupLayout == null) {
                this.sendPopupLayout = new ActionBarPopupWindowLayout(this.parentActivity);
                this.sendPopupLayout.setAnimationEnabled(false);
                this.sendPopupLayout.setOnTouchListener(new OnTouchListener() {
                    private Rect popupRect = new Rect();

                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() == 0 && ChatActivityEnterView.this.sendPopupWindow != null && ChatActivityEnterView.this.sendPopupWindow.isShowing()) {
                            view.getHitRect(this.popupRect);
                            if (!this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                                ChatActivityEnterView.this.sendPopupWindow.dismiss();
                            }
                        }
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new -$$Lambda$ChatActivityEnterView$VeL4MiAWPpYXFOBB_imkzxVg-rM(this));
                this.sendPopupLayout.setShowedFromBotton(false);
                for (i = 0; i < 2; i++) {
                    if (i != 1 || (!UserObject.isUserSelf(currentUser) && (this.slowModeTimer <= 0 || isInScheduleMode()))) {
                        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext());
                        if (i == 0) {
                            if (UserObject.isUserSelf(currentUser)) {
                                actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                            } else {
                                actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                            }
                        } else if (i == 1) {
                            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                        }
                        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
                        this.sendPopupLayout.addView(actionBarMenuSubItem, LayoutHelper.createFrame(-1, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i * 48), 0.0f, 0.0f));
                        actionBarMenuSubItem.setOnClickListener(new -$$Lambda$ChatActivityEnterView$690LP7tIuhU2MMHr9U3lgTPmKXU(this, i));
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
            }
            this.sendPopupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.sendPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            view2.getLocationInWindow(iArr);
            if (this.keyboardVisible) {
                i = getMeasuredHeight();
                View view3 = this.topView;
                float f = (view3 == null || view3.getVisibility() != 0) ? 58.0f : 106.0f;
                if (i > AndroidUtilities.dp(f)) {
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
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$onSendLongClick$21$ChatActivityEnterView(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new -$$Lambda$ChatActivityEnterView$HYedsDwyi9KUQvm0fCLASSNAMEO6pzfJ0(this));
        } else if (i == 1) {
            sendMessageInternal(false, 0);
        }
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
        r5 = r11.accountInstance;
        r5 = r5.getMessagesController();
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
        r13 = new org.telegram.ui.Components.ChatActivityEnterView$19;
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
        return this.slowModeTimer > 0 ? this.slowModeButton.getText() : null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0080 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0080 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b1  */
    private void updateSlowModeText() {
        /*
        r8 = this;
        r0 = r8.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r0 = r0.getCurrentTime();
        r1 = r8.updateSlowModeRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1);
        r1 = 0;
        r8.updateSlowModeRunnable = r1;
        r1 = r8.info;
        r2 = NUM; // 0x7ffffffe float:NaN double:1.0609978945E-314;
        r3 = 1;
        r4 = 0;
        if (r1 == 0) goto L_0x0063;
    L_0x001b:
        r5 = r1.slowmode_seconds;
        if (r5 == 0) goto L_0x0063;
    L_0x001f:
        r1 = r1.slowmode_next_send_date;
        if (r1 > r0) goto L_0x0063;
    L_0x0023:
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.SendMessagesHelper.getInstance(r1);
        r5 = r8.dialog_id;
        r1 = r1.isUploadingMessageIdDialog(r5);
        if (r1 != 0) goto L_0x003f;
    L_0x0031:
        r5 = r8.currentAccount;
        r5 = org.telegram.messenger.SendMessagesHelper.getInstance(r5);
        r6 = r8.dialog_id;
        r5 = r5.isSendingMessageIdDialog(r6);
        if (r5 == 0) goto L_0x0063;
    L_0x003f:
        r0 = r8.accountInstance;
        r0 = r0.getMessagesController();
        r5 = r8.info;
        r5 = r5.id;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r0.getChat(r5);
        r0 = org.telegram.messenger.ChatObject.hasAdminRights(r0);
        if (r0 != 0) goto L_0x0078;
    L_0x0057:
        r0 = r8.info;
        r0 = r0.slowmode_seconds;
        if (r1 == 0) goto L_0x0060;
    L_0x005d:
        r2 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x0060:
        r8.slowModeTimer = r2;
        goto L_0x007c;
    L_0x0063:
        r1 = r8.slowModeTimer;
        if (r1 < r2) goto L_0x007a;
    L_0x0067:
        r0 = r8.info;
        if (r0 == 0) goto L_0x0078;
    L_0x006b:
        r0 = r8.accountInstance;
        r0 = r0.getMessagesController();
        r1 = r8.info;
        r1 = r1.id;
        r0.loadFullChat(r1, r4, r3);
    L_0x0078:
        r0 = 0;
        goto L_0x007c;
    L_0x007a:
        r0 = r1 - r0;
    L_0x007c:
        r1 = r8.slowModeTimer;
        if (r1 == 0) goto L_0x00a9;
    L_0x0080:
        if (r0 <= 0) goto L_0x00a9;
    L_0x0082:
        r1 = r8.slowModeButton;
        r0 = java.lang.Math.max(r3, r0);
        r0 = org.telegram.messenger.AndroidUtilities.formatDurationNoHours(r0, r4);
        r1.setText(r0);
        r0 = r8.delegate;
        if (r0 == 0) goto L_0x009c;
    L_0x0093:
        r1 = r8.slowModeButton;
        r2 = r1.getText();
        r0.onUpdateSlowModeButton(r1, r4, r2);
    L_0x009c:
        r0 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$X9J0gVteYqpfLVW9jaFB4fp2jQc;
        r0.<init>(r8);
        r8.updateSlowModeRunnable = r0;
        r1 = 100;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1);
        goto L_0x00ab;
    L_0x00a9:
        r8.slowModeTimer = r4;
    L_0x00ab:
        r0 = r8.isInScheduleMode();
        if (r0 != 0) goto L_0x00b4;
    L_0x00b1:
        r8.checkSendButton(r3);
    L_0x00b4:
        return;
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
        setEmojiButtonImage(false, this.isPaused ^ 1);
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.sendingMessagesChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.onDestroy();
        }
        Runnable runnable = this.updateSlowModeRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateSlowModeRunnable = null;
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

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.setChatInfo(this.info);
        }
        setSlowModeTimer(chatFull.slowmode_next_send_date);
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
            } else if (AndroidUtilities.getPeerLayerVersion(this.accountInstance.getMessagesController().getEncryptedChat(Integer.valueOf(i2)).layer) >= 66) {
                this.hasRecordVideo = true;
            }
            if (((int) this.dialog_id) < 0) {
                Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
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
            Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
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
        if (isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new -$$Lambda$ChatActivityEnterView$HYedsDwyi9KUQvm0fCLASSNAMEO6pzfJ0(this));
        } else {
            sendMessageInternal(true, 0);
        }
    }

    private void sendMessageInternal(boolean z, int i) {
        boolean z2 = z;
        int i2 = i;
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null) {
                Chat currentChat = chatActivity.getCurrentChat();
                if (this.parentFragment.getCurrentUser() != null || ((ChatObject.isChannel(currentChat) && currentChat.megagroup) || !ChatObject.isChannel(currentChat))) {
                    Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("silent_");
                    stringBuilder.append(this.dialog_id);
                    edit.putBoolean(stringBuilder.toString(), z2 ^ 1).commit();
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
                return;
            } else if (this.audioToSend != null) {
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject != null && playingMessageObject == this.audioToSendMessageObject) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.audioToSend, null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, null, null, null, null, z, i, 0, null);
                chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    chatActivityEnterViewDelegate.onMessageSend(null, z, i);
                }
                hideRecordedAudioPanel();
                checkSendButton(true);
                return;
            } else {
                CharSequence charSequence = null;
                int i3 = i2;
                boolean z3 = z2;
                Editable text = this.messageEditText.getText();
                ChatActivity chatActivity2 = this.parentFragment;
                if (chatActivity2 != null) {
                    Chat currentChat2 = chatActivity2.getCurrentChat();
                    if (!(currentChat2 == null || !currentChat2.slowmode_enabled || ChatObject.hasAdminRights(currentChat2))) {
                        String str = "Slowmode";
                        if (text.length() > this.accountInstance.getMessagesController().maxMessageLength) {
                            AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString(str, NUM), LocaleController.getString("SlowmodeSendErrorTooLong", NUM));
                            return;
                        } else if (this.forceShowSendButton && text.length() > 0) {
                            AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString(str, NUM), LocaleController.getString("SlowmodeSendError", NUM));
                            return;
                        }
                    }
                }
                if (processSendingText(text, z3, i3)) {
                    this.messageEditText.setText("");
                    this.lastTypingTimeSend = 0;
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                    if (chatActivityEnterViewDelegate2 != null) {
                        chatActivityEnterViewDelegate2.onMessageSend(text, z3, i3);
                    }
                } else if (this.forceShowSendButton) {
                    chatActivityEnterViewDelegate = this.delegate;
                    if (chatActivityEnterViewDelegate != null) {
                        chatActivityEnterViewDelegate.onMessageSend(charSequence, z3, i3);
                    }
                }
                return;
            }
        }
        chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.scrollToSendingMessage();
        }
    }

    public void doneEditingMessage() {
        if (this.editingMessageObject != null) {
            this.delegate.onMessageEditEnd(true);
            showEditDoneProgress(true, true);
            CharSequence[] charSequenceArr = new CharSequence[]{this.messageEditText.getText()};
            ArrayList entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities());
            SendMessagesHelper instance = SendMessagesHelper.getInstance(this.currentAccount);
            MessageObject messageObject = this.editingMessageObject;
            String charSequence = charSequenceArr[0].toString();
            boolean z = this.messageWebPageSearch;
            ChatActivity chatActivity = this.parentFragment;
            MessageObject messageObject2 = this.editingMessageObject;
            this.editingMessageReqId = instance.editMessage(messageObject, charSequence, z, chatActivity, entities, messageObject2.scheduled ? messageObject2.messageOwner.date : 0, new -$$Lambda$ChatActivityEnterView$7vFCLASSNAMEJrLb6cmkpoNLbSOkpjG60(this));
        }
    }

    public /* synthetic */ void lambda$doneEditingMessage$22$ChatActivityEnterView() {
        this.editingMessageReqId = 0;
        setEditingMessageObject(null, false);
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
            CharSequence[] charSequenceArr = new CharSequence[1];
            int i4 = i3 * i2;
            i3++;
            charSequenceArr[0] = trimmedString.subSequence(i4, Math.min(i3 * i2, trimmedString.length()));
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(charSequenceArr[0].toString(), this.dialog_id, this.replyingMessageObject, this.messageWebPage, this.messageWebPageSearch, MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities), null, null, z, i);
        }
        return true;
    }

    private boolean supportsSendingNewEntities() {
        ChatActivity chatActivity = this.parentFragment;
        EncryptedChat currentEncryptedChat = chatActivity != null ? chatActivity.getCurrentEncryptedChat() : null;
        return currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(currentEncryptedChat.layer) >= 101;
    }

    private void checkSendButton(boolean z) {
        if (this.editingMessageObject == null) {
            boolean z2 = this.isPaused ? false : z;
            CharSequence trimmedString = AndroidUtilities.getTrimmedString(this.messageEditText.getText());
            int i = this.slowModeTimer;
            Object obj;
            LinearLayout linearLayout;
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
            ImageView imageView;
            ImageView imageView2;
            float f;
            AnimatorSet animatorSet;
            ArrayList arrayList;
            ImageView imageView3;
            ImageView imageView4;
            if (i <= 0 || i == Integer.MAX_VALUE || isInScheduleMode()) {
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2;
                Object obj2;
                if (trimmedString.length() > 0 || this.forceShowSendButton || this.audioToSend != null || this.videoToSendMessageObject != null || (this.slowModeTimer == Integer.MAX_VALUE && !isInScheduleMode())) {
                    int color;
                    final String caption = this.messageEditText.getCaption();
                    obj = (caption == null || !(this.sendButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0)) ? null : 1;
                    Object obj3 = (caption == null && (this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0)) ? 1 : null;
                    if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
                        color = Theme.getColor("chat_messagePanelSend");
                    } else {
                        color = Theme.getColor("chat_messagePanelIcons");
                    }
                    Theme.setSelectorDrawableColor(this.sendButton.getBackground(), Color.argb(24, Color.red(color), Color.green(color), Color.blue(color)), true);
                    if (this.audioVideoButtonContainer.getVisibility() == 0 || this.slowModeButton.getVisibility() == 0 || obj != null || obj3 != null) {
                        if (!z2) {
                            int i2;
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
                            linearLayout = this.attachLayout;
                            if (linearLayout != null) {
                                linearLayout.setVisibility(i2);
                                if (this.delegate != null && getVisibility() == 0) {
                                    this.delegate.onAttachButtonHidden();
                                }
                                updateFieldRight(0);
                            }
                            this.scheduleButtonHidden = true;
                            if (this.scheduledButton != null) {
                                chatActivityEnterViewDelegate = this.delegate;
                                if (chatActivityEnterViewDelegate != null && chatActivityEnterViewDelegate.hasScheduledMessages()) {
                                    this.scheduledButton.setVisibility(8);
                                    this.scheduledButton.setTag(null);
                                }
                                this.scheduledButton.setAlpha(0.0f);
                                this.scheduledButton.setScaleX(0.0f);
                                this.scheduledButton.setScaleY(1.0f);
                                imageView = this.scheduledButton;
                                imageView2 = this.botButton;
                                f = (imageView2 == null || imageView2.getVisibility() == 8) ? 48.0f : 96.0f;
                                imageView.setTranslationX((float) AndroidUtilities.dp(f));
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
                                arrayList = new ArrayList();
                                arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{0.0f}));
                                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                                final boolean z3 = chatActivityEnterViewDelegate3 != null && chatActivityEnterViewDelegate3.hasScheduledMessages();
                                this.scheduleButtonHidden = true;
                                ImageView imageView5 = this.scheduledButton;
                                if (imageView5 != null) {
                                    imageView5.setScaleY(1.0f);
                                    if (z3) {
                                        this.scheduledButton.setTag(null);
                                        arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{0.0f}));
                                        arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{0.0f}));
                                        imageView5 = this.scheduledButton;
                                        Property property = View.TRANSLATION_X;
                                        float[] fArr = new float[1];
                                        ImageView imageView6 = this.botButton;
                                        f = (imageView6 == null || imageView6.getVisibility() == 8) ? 48.0f : 96.0f;
                                        fArr[0] = (float) AndroidUtilities.dp(f);
                                        arrayList.add(ObjectAnimator.ofFloat(imageView5, property, fArr));
                                    } else {
                                        this.scheduledButton.setAlpha(0.0f);
                                        this.scheduledButton.setScaleX(0.0f);
                                        imageView5 = this.scheduledButton;
                                        ImageView imageView7 = this.botButton;
                                        f = (imageView7 == null || imageView7.getVisibility() == 8) ? 48.0f : 96.0f;
                                        imageView5.setTranslationX((float) AndroidUtilities.dp(f));
                                    }
                                }
                                this.runningAnimation2.playTogether(arrayList);
                                this.runningAnimation2.setDuration(100);
                                this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                            ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                            if (z3) {
                                                ChatActivityEnterView.this.scheduledButton.setVisibility(8);
                                            }
                                            ChatActivityEnterView.this.runningAnimation2 = null;
                                        }
                                    }

                                    public void onAnimationCancel(Animator animator) {
                                        if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
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
                            if (this.slowModeButton.getVisibility() == 0) {
                                arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                            }
                            if (obj != null) {
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                            } else if (obj3 != null) {
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
                                        ChatActivityEnterView.this.runningAnimation = null;
                                        ChatActivityEnterView.this.runningAnimationType = 0;
                                    }
                                }

                                public void onAnimationCancel(Animator animator) {
                                    if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                        ChatActivityEnterView.this.runningAnimation = null;
                                    }
                                }
                            });
                            this.runningAnimation.start();
                        }
                    }
                } else if (this.emojiView == null || !this.emojiViewVisible || (!(this.stickersTabOpen || (this.emojiTabOpen && this.searchingType == 2)) || AndroidUtilities.isInMultiwindow)) {
                    if (this.sendButton.getVisibility() == 0 || this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0 || this.slowModeButton.getVisibility() == 0) {
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
                                chatActivityEnterViewDelegate = this.delegate;
                                if (chatActivityEnterViewDelegate != null && chatActivityEnterViewDelegate.hasScheduledMessages()) {
                                    this.scheduledButton.setVisibility(0);
                                    this.scheduledButton.setTag(Integer.valueOf(1));
                                }
                                this.scheduledButton.setAlpha(1.0f);
                                this.scheduledButton.setScaleX(1.0f);
                                this.scheduledButton.setScaleY(1.0f);
                                this.scheduledButton.setTranslationX(0.0f);
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
                                arrayList = new ArrayList();
                                arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{1.0f}));
                                chatActivityEnterViewDelegate2 = this.delegate;
                                obj2 = (chatActivityEnterViewDelegate2 == null || !chatActivityEnterViewDelegate2.hasScheduledMessages()) ? null : 1;
                                this.scheduleButtonHidden = false;
                                imageView3 = this.scheduledButton;
                                if (imageView3 != null) {
                                    if (obj2 != null) {
                                        imageView3.setVisibility(0);
                                        this.scheduledButton.setTag(Integer.valueOf(1));
                                        this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                        arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                        arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                                        arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f}));
                                    } else {
                                        imageView3.setAlpha(1.0f);
                                        this.scheduledButton.setScaleX(1.0f);
                                        this.scheduledButton.setScaleY(1.0f);
                                        this.scheduledButton.setTranslationX(0.0f);
                                    }
                                }
                                this.runningAnimation2.playTogether(arrayList);
                                this.runningAnimation2.setDuration(100);
                                this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                            ChatActivityEnterView.this.runningAnimation2 = null;
                                        }
                                    }

                                    public void onAnimationCancel(Animator animator) {
                                        if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                            ChatActivityEnterView.this.runningAnimation2 = null;
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
                            } else if (this.slowModeButton.getVisibility() == 0) {
                                arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                            } else {
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                            }
                            this.runningAnimation.playTogether(arrayList);
                            this.runningAnimation.setDuration(150);
                            this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                        ChatActivityEnterView.this.sendButton.setVisibility(8);
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                        ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                        ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
                                        ChatActivityEnterView.this.runningAnimation = null;
                                        ChatActivityEnterView.this.runningAnimationType = 0;
                                    }
                                }

                                public void onAnimationCancel(Animator animator) {
                                    if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                        ChatActivityEnterView.this.runningAnimation = null;
                                    }
                                }
                            });
                            this.runningAnimation.start();
                        }
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
                        chatActivityEnterViewDelegate = this.delegate;
                        if (chatActivityEnterViewDelegate != null && chatActivityEnterViewDelegate.hasScheduledMessages()) {
                            this.scheduledButton.setVisibility(0);
                            this.scheduledButton.setTag(Integer.valueOf(1));
                        }
                        this.scheduledButton.setAlpha(1.0f);
                        this.scheduledButton.setScaleX(1.0f);
                        this.scheduledButton.setScaleY(1.0f);
                        this.scheduledButton.setTranslationX(0.0f);
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
                        arrayList = new ArrayList();
                        arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{1.0f}));
                        chatActivityEnterViewDelegate2 = this.delegate;
                        obj2 = (chatActivityEnterViewDelegate2 == null || !chatActivityEnterViewDelegate2.hasScheduledMessages()) ? null : 1;
                        this.scheduleButtonHidden = false;
                        imageView4 = this.scheduledButton;
                        if (imageView4 != null) {
                            imageView4.setScaleY(1.0f);
                            if (obj2 != null) {
                                this.scheduledButton.setVisibility(0);
                                this.scheduledButton.setTag(Integer.valueOf(1));
                                this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f}));
                            } else {
                                this.scheduledButton.setAlpha(1.0f);
                                this.scheduledButton.setScaleX(1.0f);
                                this.scheduledButton.setTranslationX(0.0f);
                            }
                        }
                        this.runningAnimation2.playTogether(arrayList);
                        this.runningAnimation2.setDuration(100);
                        this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    ChatActivityEnterView.this.runningAnimation2 = null;
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
                    } else if (this.slowModeButton.getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                    } else {
                        arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                    }
                    this.runningAnimation.playTogether(arrayList);
                    this.runningAnimation.setDuration(150);
                    this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.sendButton.setVisibility(8);
                                ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                ChatActivityEnterView.this.expandStickersButton.setVisibility(0);
                                ChatActivityEnterView.this.runningAnimation = null;
                                ChatActivityEnterView.this.runningAnimationType = 0;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }
                    });
                    this.runningAnimation.start();
                }
            } else if (this.slowModeButton.getVisibility() != 0) {
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
                    linearLayout = this.attachLayout;
                    if (linearLayout != null) {
                        linearLayout.setVisibility(8);
                        if (this.delegate != null && getVisibility() == 0) {
                            this.delegate.onAttachButtonHidden();
                        }
                        updateFieldRight(0);
                    }
                    this.scheduleButtonHidden = false;
                    if (this.scheduledButton != null) {
                        chatActivityEnterViewDelegate = this.delegate;
                        if (chatActivityEnterViewDelegate != null && chatActivityEnterViewDelegate.hasScheduledMessages()) {
                            this.scheduledButton.setVisibility(0);
                            this.scheduledButton.setTag(Integer.valueOf(1));
                        }
                        imageView = this.scheduledButton;
                        imageView2 = this.botButton;
                        f = (imageView2 == null || imageView2.getVisibility() != 0) ? 48.0f : 96.0f;
                        imageView.setTranslationX((float) AndroidUtilities.dp(f));
                        this.scheduledButton.setAlpha(1.0f);
                        this.scheduledButton.setScaleX(1.0f);
                        this.scheduledButton.setScaleY(1.0f);
                    }
                } else if (this.runningAnimationType != 5) {
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
                        arrayList = new ArrayList();
                        arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{0.0f}));
                        this.scheduleButtonHidden = false;
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate4 = this.delegate;
                        obj = (chatActivityEnterViewDelegate4 == null || !chatActivityEnterViewDelegate4.hasScheduledMessages()) ? null : 1;
                        imageView3 = this.scheduledButton;
                        if (imageView3 != null) {
                            imageView3.setScaleY(1.0f);
                            if (obj != null) {
                                this.scheduledButton.setVisibility(0);
                                this.scheduledButton.setTag(Integer.valueOf(1));
                                this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                imageView4 = this.scheduledButton;
                                Property property2 = View.TRANSLATION_X;
                                float[] fArr2 = new float[1];
                                ImageView imageView8 = this.botButton;
                                f = (imageView8 == null || imageView8.getVisibility() != 0) ? 48.0f : 96.0f;
                                fArr2[0] = (float) AndroidUtilities.dp(f);
                                arrayList.add(ObjectAnimator.ofFloat(imageView4, property2, fArr2));
                                arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                            } else {
                                imageView4 = this.scheduledButton;
                                imageView3 = this.botButton;
                                f = (imageView3 == null || imageView3.getVisibility() != 0) ? 48.0f : 96.0f;
                                imageView4.setTranslationX((float) AndroidUtilities.dp(f));
                                this.scheduledButton.setAlpha(1.0f);
                                this.scheduledButton.setScaleX(1.0f);
                            }
                        }
                        this.runningAnimation2.playTogether(arrayList);
                        this.runningAnimation2.setDuration(100);
                        this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                    ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
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
                    this.runningAnimationType = 5;
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
                    if (this.sendButton.getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                    }
                    if (this.cancelBotButton.getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                    }
                    arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{1.0f}));
                    setSlowModeButtonVisible(true);
                    this.runningAnimation.playTogether(arrayList);
                    this.runningAnimation.setDuration(150);
                    this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.sendButton.setVisibility(8);
                                ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                ChatActivityEnterView.this.expandStickersButton.setVisibility(8);
                                ChatActivityEnterView.this.runningAnimation = null;
                                ChatActivityEnterView.this.runningAnimationType = 0;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }
                    });
                    this.runningAnimation.start();
                }
            }
        }
    }

    private void setSlowModeButtonVisible(boolean z) {
        this.slowModeButton.setVisibility(z ? 0 : 8);
        int dp = z ? AndroidUtilities.dp(16.0f) : 0;
        if (this.messageEditText.getPaddingRight() != dp) {
            this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), dp, AndroidUtilities.dp(12.0f));
        }
    }

    private void updateFieldRight(int i) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null && this.editingMessageObject == null) {
            LayoutParams layoutParams = (LayoutParams) editTextCaption.getLayoutParams();
            int i2 = layoutParams.rightMargin;
            ImageView imageView;
            if (i == 1) {
                imageView = this.botButton;
                if (imageView == null || imageView.getVisibility() != 0) {
                    imageView = this.notifyButton;
                    if (imageView == null || imageView.getVisibility() != 0) {
                        imageView = this.scheduledButton;
                        if (imageView == null || imageView.getTag() == null) {
                            layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                        }
                    }
                }
                layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
            } else if (i != 2) {
                imageView = this.scheduledButton;
                if (imageView == null || imageView.getTag() == null) {
                    layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                }
            } else if (layoutParams.rightMargin != AndroidUtilities.dp(2.0f)) {
                imageView = this.botButton;
                if (imageView == null || imageView.getVisibility() != 0) {
                    imageView = this.notifyButton;
                    if (imageView == null || imageView.getVisibility() != 0) {
                        imageView = this.scheduledButton;
                        if (imageView == null || imageView.getTag() == null) {
                            layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                        }
                    }
                }
                layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
            }
            if (i2 != layoutParams.rightMargin) {
                this.messageEditText.setLayoutParams(layoutParams);
            }
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
                        if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
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
                    if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
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
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
            if (z) {
                CharSequence stringBuilder;
                String obj = this.messageEditText.getText().toString();
                if (messageObject2 != null && ((int) this.dialog_id) < 0) {
                    user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
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
                chatActivityEnterViewDelegate = this.delegate;
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
                if ((this.botCount != 1 || z2) && user != null && user.bot && !str2.contains(str4)) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(String.format(Locale.US, str3, new Object[]{str2, user.username}), this.dialog_id, this.replyingMessageObject, null, false, null, null, null, true, 0);
                } else {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(str, this.dialog_id, this.replyingMessageObject, null, false, null, null, null, true, 0);
                }
            } else {
                chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    SimpleTextView simpleTextView = this.slowModeButton;
                    chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
                }
            }
        }
    }

    public void setEditingMessageObject(MessageObject messageObject, boolean z) {
        if (this.audioToSend == null && this.videoToSendMessageObject == null && this.editingMessageObject != messageObject) {
            if (this.editingMessageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.editingMessageReqId, true);
                this.editingMessageReqId = 0;
            }
            this.editingMessageObject = messageObject;
            this.editingCaption = z;
            String str = "";
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
                if (z) {
                    inputFilterArr[0] = new LengthFilter(this.accountInstance.getMessagesController().maxCaptionLength);
                    charSequence = this.editingMessageObject.caption;
                } else {
                    inputFilterArr[0] = new LengthFilter(this.accountInstance.getMessagesController().maxMessageLength);
                    charSequence = this.editingMessageObject.messageText;
                }
                if (charSequence != null) {
                    ArrayList arrayList = this.editingMessageObject.messageOwner.entities;
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
                                MessageEntity messageEntity = (MessageEntity) arrayList.get(i);
                                if (messageEntity.offset + messageEntity.length <= spannableStringBuilder.length()) {
                                    StringBuilder stringBuilder;
                                    if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                                        if (messageEntity.offset + messageEntity.length < spannableStringBuilder.length() && spannableStringBuilder.charAt(messageEntity.offset + messageEntity.length) == ' ') {
                                            messageEntity.length++;
                                        }
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(str);
                                        stringBuilder.append(((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id);
                                        spannableStringBuilder.setSpan(new URLSpanUserMention(stringBuilder.toString(), 3), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                    } else if (messageEntity instanceof TL_messageEntityMentionName) {
                                        if (messageEntity.offset + messageEntity.length < spannableStringBuilder.length() && spannableStringBuilder.charAt(messageEntity.offset + messageEntity.length) == ' ') {
                                            messageEntity.length++;
                                        }
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(str);
                                        stringBuilder.append(((TL_messageEntityMentionName) messageEntity).user_id);
                                        spannableStringBuilder.setSpan(new URLSpanUserMention(stringBuilder.toString(), 3), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                    } else {
                                        TextStyleRun textStyleRun;
                                        if (!(messageEntity instanceof TL_messageEntityCode)) {
                                            if (!(messageEntity instanceof TL_messageEntityPre)) {
                                                if (messageEntity instanceof TL_messageEntityBold) {
                                                    textStyleRun = new TextStyleRun();
                                                    textStyleRun.flags |= 1;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun), messageEntity.offset, messageEntity.offset + messageEntity.length, spannableStringBuilder, true);
                                                } else if (messageEntity instanceof TL_messageEntityItalic) {
                                                    textStyleRun = new TextStyleRun();
                                                    textStyleRun.flags |= 2;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun), messageEntity.offset, messageEntity.offset + messageEntity.length, spannableStringBuilder, true);
                                                } else if (messageEntity instanceof TL_messageEntityStrike) {
                                                    textStyleRun = new TextStyleRun();
                                                    textStyleRun.flags |= 8;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun), messageEntity.offset, messageEntity.offset + messageEntity.length, spannableStringBuilder, true);
                                                } else if (messageEntity instanceof TL_messageEntityUnderline) {
                                                    textStyleRun = new TextStyleRun();
                                                    textStyleRun.flags |= 16;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun), messageEntity.offset, messageEntity.offset + messageEntity.length, spannableStringBuilder, true);
                                                } else if (messageEntity instanceof TL_messageEntityTextUrl) {
                                                    spannableStringBuilder.setSpan(new URLSpanReplacement(messageEntity.url), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                                }
                                            }
                                        }
                                        textStyleRun = new TextStyleRun();
                                        textStyleRun.flags |= 4;
                                        MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun), messageEntity.offset, messageEntity.offset + messageEntity.length, spannableStringBuilder, true);
                                    }
                                }
                                i++;
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    }
                    setFieldText(Emoji.replaceEmoji(new SpannableStringBuilder(spannableStringBuilder), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
                } else {
                    setFieldText(str);
                }
                this.messageEditText.setFilters(inputFilterArr);
                openKeyboard();
                LayoutParams layoutParams = (LayoutParams) this.messageEditText.getLayoutParams();
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
                this.messageEditText.setText(str);
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
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.updateColors();
        }
        ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.sendPopupLayout;
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
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            this.ignoreTextChange = z;
            editTextCaption.setText(charSequence);
            EditTextCaption editTextCaption2 = this.messageEditText;
            editTextCaption2.setSelection(editTextCaption2.getText().length());
            this.ignoreTextChange = false;
            if (z) {
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    chatActivityEnterViewDelegate.onTextChanged(this.messageEditText.getText(), true);
                }
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
                -$$Lambda$ChatActivityEnterView$05hH6FPF8sAzjObBU2KakU_7gx0 -__lambda_chatactivityenterview_05hh6fpf8sazjobbu2kaku_7gx0 = new -$$Lambda$ChatActivityEnterView$05hH6FPF8sAzjObBU2KakU_7gx0(this);
                this.focusRunnable = -__lambda_chatactivityenterview_05hh6fpf8sazjobbu2kaku_7gx0;
                AndroidUtilities.runOnUIThread(-__lambda_chatactivityenterview_05hh6fpf8sazjobbu2kaku_7gx0, 600);
            }
        }
    }

    public /* synthetic */ void lambda$setFieldFocused$23$ChatActivityEnterView() {
        this.focusRunnable = null;
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
        if (!this.isPaused && obj != null) {
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

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006d  */
    /* JADX WARNING: Missing block: B:9:0x0048, code skipped:
            if (r3.post_messages != false) goto L_0x004a;
     */
    public void updateScheduleButton(boolean r14) {
        /*
        r13 = this;
        r0 = r13.dialog_id;
        r1 = (int) r0;
        r0 = 1;
        r2 = 0;
        if (r1 >= 0) goto L_0x007a;
    L_0x0007:
        r1 = r13.accountInstance;
        r1 = r1.getMessagesController();
        r3 = r13.dialog_id;
        r4 = (int) r3;
        r3 = -r4;
        r3 = java.lang.Integer.valueOf(r3);
        r1 = r1.getChat(r3);
        r3 = r13.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "silent_";
        r4.append(r5);
        r5 = r13.dialog_id;
        r4.append(r5);
        r4 = r4.toString();
        r3 = r3.getBoolean(r4, r2);
        r13.silent = r3;
        r3 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r3 == 0) goto L_0x0050;
    L_0x003e:
        r3 = r1.creator;
        if (r3 != 0) goto L_0x004a;
    L_0x0042:
        r3 = r1.admin_rights;
        if (r3 == 0) goto L_0x0050;
    L_0x0046:
        r3 = r3.post_messages;
        if (r3 == 0) goto L_0x0050;
    L_0x004a:
        r1 = r1.megagroup;
        if (r1 != 0) goto L_0x0050;
    L_0x004e:
        r1 = 1;
        goto L_0x0051;
    L_0x0050:
        r1 = 0;
    L_0x0051:
        r13.canWriteToChannel = r1;
        r1 = r13.notifyButton;
        if (r1 == 0) goto L_0x0068;
    L_0x0057:
        r3 = r13.canWriteToChannel;
        r4 = r13.silent;
        if (r4 == 0) goto L_0x0061;
    L_0x005d:
        r4 = NUM; // 0x7var_b float:1.7945217E38 double:1.0529356587E-314;
        goto L_0x0064;
    L_0x0061:
        r4 = NUM; // 0x7var_c float:1.7945219E38 double:1.052935659E-314;
    L_0x0064:
        r1.setImageResource(r4);
        goto L_0x0069;
    L_0x0068:
        r3 = 0;
    L_0x0069:
        r1 = r13.attachLayout;
        if (r1 == 0) goto L_0x007b;
    L_0x006d:
        r1 = r1.getVisibility();
        if (r1 != 0) goto L_0x0075;
    L_0x0073:
        r1 = 1;
        goto L_0x0076;
    L_0x0075:
        r1 = 0;
    L_0x0076:
        r13.updateFieldRight(r1);
        goto L_0x007b;
    L_0x007a:
        r3 = 0;
    L_0x007b:
        r1 = r13.delegate;
        if (r1 == 0) goto L_0x008f;
    L_0x007f:
        r1 = r13.isInScheduleMode();
        if (r1 != 0) goto L_0x008f;
    L_0x0085:
        r1 = r13.delegate;
        r1 = r1.hasScheduledMessages();
        if (r1 == 0) goto L_0x008f;
    L_0x008d:
        r1 = 1;
        goto L_0x0090;
    L_0x008f:
        r1 = 0;
    L_0x0090:
        if (r1 == 0) goto L_0x0098;
    L_0x0092:
        r4 = r13.scheduleButtonHidden;
        if (r4 != 0) goto L_0x0098;
    L_0x0096:
        r4 = 1;
        goto L_0x0099;
    L_0x0098:
        r4 = 0;
    L_0x0099:
        r5 = r13.scheduledButton;
        r6 = NUM; // 0x42CLASSNAME float:96.0 double:5.532938244E-315;
        r7 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r8 = 0;
        r9 = 8;
        if (r5 == 0) goto L_0x0106;
    L_0x00a4:
        r5 = r5.getTag();
        if (r5 == 0) goto L_0x00ac;
    L_0x00aa:
        if (r4 != 0) goto L_0x00b6;
    L_0x00ac:
        r5 = r13.scheduledButton;
        r5 = r5.getTag();
        if (r5 != 0) goto L_0x00f9;
    L_0x00b4:
        if (r4 != 0) goto L_0x00f9;
    L_0x00b6:
        r14 = r13.notifyButton;
        if (r14 == 0) goto L_0x00f8;
    L_0x00ba:
        if (r1 != 0) goto L_0x00c7;
    L_0x00bc:
        if (r3 == 0) goto L_0x00c7;
    L_0x00be:
        r14 = r13.scheduledButton;
        r14 = r14.getVisibility();
        if (r14 == 0) goto L_0x00c7;
    L_0x00c6:
        goto L_0x00c9;
    L_0x00c7:
        r2 = 8;
    L_0x00c9:
        r14 = r13.notifyButton;
        r14 = r14.getVisibility();
        if (r2 == r14) goto L_0x00f8;
    L_0x00d1:
        r14 = r13.notifyButton;
        r14.setVisibility(r2);
        r14 = r13.attachLayout;
        if (r14 == 0) goto L_0x00f8;
    L_0x00da:
        r0 = r13.botButton;
        if (r0 == 0) goto L_0x00e4;
    L_0x00de:
        r0 = r0.getVisibility();
        if (r0 != r9) goto L_0x00f0;
    L_0x00e4:
        r0 = r13.notifyButton;
        if (r0 == 0) goto L_0x00ee;
    L_0x00e8:
        r0 = r0.getVisibility();
        if (r0 != r9) goto L_0x00f0;
    L_0x00ee:
        r6 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
    L_0x00f0:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = (float) r0;
        r14.setPivotX(r0);
    L_0x00f8:
        return;
    L_0x00f9:
        r1 = r13.scheduledButton;
        if (r4 == 0) goto L_0x0102;
    L_0x00fd:
        r5 = java.lang.Integer.valueOf(r0);
        goto L_0x0103;
    L_0x0102:
        r5 = r8;
    L_0x0103:
        r1.setTag(r5);
    L_0x0106:
        r1 = r13.scheduledButtonAnimation;
        if (r1 == 0) goto L_0x010f;
    L_0x010a:
        r1.cancel();
        r13.scheduledButtonAnimation = r8;
    L_0x010f:
        r1 = 0;
        r5 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r14 == 0) goto L_0x018e;
    L_0x0117:
        if (r3 == 0) goto L_0x011a;
    L_0x0119:
        goto L_0x018e;
    L_0x011a:
        if (r4 == 0) goto L_0x0121;
    L_0x011c:
        r14 = r13.scheduledButton;
        r14.setVisibility(r2);
    L_0x0121:
        r14 = r13.scheduledButton;
        r3 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r14.setPivotX(r3);
        r14 = new android.animation.AnimatorSet;
        r14.<init>();
        r13.scheduledButtonAnimation = r14;
        r14 = r13.scheduledButtonAnimation;
        r3 = 3;
        r3 = new android.animation.Animator[r3];
        r10 = r13.scheduledButton;
        r11 = android.view.View.ALPHA;
        r12 = new float[r0];
        if (r4 == 0) goto L_0x0143;
    L_0x0141:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0143:
        r12[r2] = r1;
        r1 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12);
        r3[r2] = r1;
        r1 = r13.scheduledButton;
        r10 = android.view.View.SCALE_X;
        r11 = new float[r0];
        if (r4 == 0) goto L_0x0156;
    L_0x0153:
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0159;
    L_0x0156:
        r12 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
    L_0x0159:
        r11[r2] = r12;
        r1 = android.animation.ObjectAnimator.ofFloat(r1, r10, r11);
        r3[r0] = r1;
        r1 = 2;
        r10 = r13.scheduledButton;
        r11 = android.view.View.SCALE_Y;
        r0 = new float[r0];
        if (r4 == 0) goto L_0x016c;
    L_0x016a:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x016c:
        r0[r2] = r5;
        r0 = android.animation.ObjectAnimator.ofFloat(r10, r11, r0);
        r3[r1] = r0;
        r14.playTogether(r3);
        r14 = r13.scheduledButtonAnimation;
        r0 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r14.setDuration(r0);
        r14 = r13.scheduledButtonAnimation;
        r0 = new org.telegram.ui.Components.ChatActivityEnterView$34;
        r0.<init>(r4);
        r14.addListener(r0);
        r14 = r13.scheduledButtonAnimation;
        r14.start();
        goto L_0x01ce;
    L_0x018e:
        r14 = r13.scheduledButton;
        if (r14 == 0) goto L_0x01ba;
    L_0x0192:
        if (r4 == 0) goto L_0x0196;
    L_0x0194:
        r0 = 0;
        goto L_0x0198;
    L_0x0196:
        r0 = 8;
    L_0x0198:
        r14.setVisibility(r0);
        r14 = r13.scheduledButton;
        if (r4 == 0) goto L_0x01a1;
    L_0x019f:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x01a1:
        r14.setAlpha(r1);
        r14 = r13.scheduledButton;
        if (r4 == 0) goto L_0x01ab;
    L_0x01a8:
        r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x01ae;
    L_0x01ab:
        r0 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
    L_0x01ae:
        r14.setScaleX(r0);
        r14 = r13.scheduledButton;
        if (r4 == 0) goto L_0x01b7;
    L_0x01b5:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x01b7:
        r14.setScaleY(r5);
    L_0x01ba:
        r14 = r13.notifyButton;
        if (r14 == 0) goto L_0x01ce;
    L_0x01be:
        if (r3 == 0) goto L_0x01c9;
    L_0x01c0:
        r0 = r13.scheduledButton;
        r0 = r0.getVisibility();
        if (r0 == 0) goto L_0x01c9;
    L_0x01c8:
        goto L_0x01cb;
    L_0x01c9:
        r2 = 8;
    L_0x01cb:
        r14.setVisibility(r2);
    L_0x01ce:
        r14 = r13.attachLayout;
        if (r14 == 0) goto L_0x01f0;
    L_0x01d2:
        r0 = r13.botButton;
        if (r0 == 0) goto L_0x01dc;
    L_0x01d6:
        r0 = r0.getVisibility();
        if (r0 != r9) goto L_0x01e8;
    L_0x01dc:
        r0 = r13.notifyButton;
        if (r0 == 0) goto L_0x01e6;
    L_0x01e0:
        r0 = r0.getVisibility();
        if (r0 != r9) goto L_0x01e8;
    L_0x01e6:
        r6 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
    L_0x01e8:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = (float) r0;
        r14.setPivotX(r0);
    L_0x01f0:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.updateScheduleButton(boolean):void");
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

    /* JADX WARNING: Removed duplicated region for block: B:27:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00e3  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x007c  */
    /* JADX WARNING: Missing block: B:39:0x00c9, code skipped:
            if (r8.getInt(r3.toString(), 0) == r7.getId()) goto L_0x00cd;
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
        if (r0 == 0) goto L_0x00f9;
    L_0x0011:
        r0 = r6.botButtonsMessageObject;
        if (r0 == 0) goto L_0x0017;
    L_0x0015:
        if (r0 == r7) goto L_0x00f9;
    L_0x0017:
        r0 = r6.botButtonsMessageObject;
        if (r0 != 0) goto L_0x001f;
    L_0x001b:
        if (r7 != 0) goto L_0x001f;
    L_0x001d:
        goto L_0x00f9;
    L_0x001f:
        r0 = r6.botKeyboardView;
        r1 = 0;
        r2 = 1;
        if (r0 != 0) goto L_0x004d;
    L_0x0025:
        r0 = new org.telegram.ui.Components.BotKeyboardView;
        r3 = r6.parentActivity;
        r0.<init>(r3);
        r6.botKeyboardView = r0;
        r0 = r6.botKeyboardView;
        r3 = 8;
        r0.setVisibility(r3);
        r6.botKeyboardViewVisible = r1;
        r0 = r6.botKeyboardView;
        r3 = new org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$A8Bbipu3sbu9rMKqbtJB0m3cjA0;
        r3.<init>(r6);
        r0.setDelegate(r3);
        r0 = r6.sizeNotifierLayout;
        r3 = r6.botKeyboardView;
        r4 = r0.getChildCount();
        r4 = r4 - r2;
        r0.addView(r3, r4);
    L_0x004d:
        r6.botButtonsMessageObject = r7;
        if (r7 == 0) goto L_0x005c;
    L_0x0051:
        r0 = r7.messageOwner;
        r0 = r0.reply_markup;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;
        if (r3 == 0) goto L_0x005c;
    L_0x0059:
        r0 = (org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup) r0;
        goto L_0x005d;
    L_0x005c:
        r0 = 0;
    L_0x005d:
        r6.botReplyMarkup = r0;
        r0 = r6.botKeyboardView;
        r3 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r3.x;
        r3 = r3.y;
        if (r4 <= r3) goto L_0x006c;
    L_0x0069:
        r3 = r6.keyboardHeightLand;
        goto L_0x006e;
    L_0x006c:
        r3 = r6.keyboardHeight;
    L_0x006e:
        r0.setPanelHeight(r3);
        r0 = r6.botKeyboardView;
        r3 = r6.botReplyMarkup;
        r0.setButtons(r3);
        r0 = r6.botReplyMarkup;
        if (r0 == 0) goto L_0x00e3;
    L_0x007c:
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
        if (r0 != r3) goto L_0x00a1;
    L_0x009f:
        r0 = 1;
        goto L_0x00a2;
    L_0x00a1:
        r0 = 0;
    L_0x00a2:
        r3 = r6.botButtonsMessageObject;
        r4 = r6.replyingMessageObject;
        if (r3 == r4) goto L_0x00cc;
    L_0x00a8:
        r3 = r6.botReplyMarkup;
        r3 = r3.single_use;
        if (r3 == 0) goto L_0x00cc;
    L_0x00ae:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "answered_";
        r3.append(r4);
        r4 = r6.dialog_id;
        r3.append(r4);
        r3 = r3.toString();
        r8 = r8.getInt(r3, r1);
        r7 = r7.getId();
        if (r8 != r7) goto L_0x00cc;
    L_0x00cb:
        goto L_0x00cd;
    L_0x00cc:
        r1 = 1;
    L_0x00cd:
        if (r1 == 0) goto L_0x00f6;
    L_0x00cf:
        if (r0 != 0) goto L_0x00f6;
    L_0x00d1:
        r7 = r6.messageEditText;
        r7 = r7.length();
        if (r7 != 0) goto L_0x00f6;
    L_0x00d9:
        r7 = r6.isPopupShowing();
        if (r7 != 0) goto L_0x00f6;
    L_0x00df:
        r6.showPopup(r2, r2);
        goto L_0x00f6;
    L_0x00e3:
        r7 = r6.isPopupShowing();
        if (r7 == 0) goto L_0x00f6;
    L_0x00e9:
        r7 = r6.currentPopupContentType;
        if (r7 != r2) goto L_0x00f6;
    L_0x00ed:
        if (r8 == 0) goto L_0x00f3;
    L_0x00ef:
        r6.openKeyboardInternal();
        goto L_0x00f6;
    L_0x00f3:
        r6.showPopup(r1, r2);
    L_0x00f6:
        r6.updateBotButton();
    L_0x00f9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setButtons(org.telegram.messenger.MessageObject, boolean):void");
    }

    public /* synthetic */ void lambda$setButtons$24$ChatActivityEnterView(KeyboardButton keyboardButton) {
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
            chatActivityEnterViewDelegate.onMessageSend(null, true, 0);
        }
    }

    public void didPressedBotButton(KeyboardButton keyboardButton, MessageObject messageObject, MessageObject messageObject2) {
        if (!(keyboardButton == null || messageObject2 == null)) {
            if (keyboardButton instanceof TL_keyboardButton) {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(keyboardButton.text, this.dialog_id, messageObject, null, false, null, null, null, true, 0);
            } else if (keyboardButton instanceof TL_keyboardButtonUrl) {
                this.parentFragment.showOpenUrlAlert(keyboardButton.url, true);
            } else if (keyboardButton instanceof TL_keyboardButtonRequestPhone) {
                this.parentFragment.shareMyContact(2, messageObject2);
            } else if (keyboardButton instanceof TL_keyboardButtonRequestGeoLocation) {
                Builder builder = new Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ChatActivityEnterView$JIp58iFY5Jgg-ya08ziH_759Jts(this, messageObject2, keyboardButton));
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
                    User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i2));
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
                dialogsActivity.setDelegate(new -$$Lambda$ChatActivityEnterView$o4Mi39Pyn81ZsI7SIijOXifQPj8(this, messageObject2, keyboardButton));
                this.parentFragment.presentFragment(dialogsActivity);
            }
        }
    }

    public /* synthetic */ void lambda$didPressedBotButton$25$ChatActivityEnterView(MessageObject messageObject, KeyboardButton keyboardButton, DialogInterface dialogInterface, int i) {
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

    public /* synthetic */ void lambda$didPressedBotButton$26$ChatActivityEnterView(MessageObject messageObject, KeyboardButton keyboardButton, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        Message message = messageObject.messageOwner;
        int i = message.from_id;
        int i2 = message.via_bot_id;
        if (i2 == 0) {
            i2 = i;
        }
        User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i2));
        if (user == null) {
            dialogsActivity.finishFragment();
            return;
        }
        long longValue = ((Long) arrayList.get(0)).longValue();
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
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
                if (this.accountInstance.getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
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

                public void onStickerSelected(View view, Document document, Object obj, boolean z, int i) {
                    if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.searchingType = 0;
                                ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(document));
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        ChatActivityEnterView.this.lambda$onStickerSelected$27$ChatActivityEnterView(document, obj, false, z, i);
                        if (((int) ChatActivityEnterView.this.dialog_id) == 0 && MessageObject.isGifDocument(document)) {
                            ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj, document);
                        }
                        return;
                    }
                    if (ChatActivityEnterView.this.delegate != null) {
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
                        AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment.getDialogId(), new -$$Lambda$ChatActivityEnterView$35$qA9baYgZSc5H94fpDOPQhgegFHQ(this, view2, obj3, obj4));
                    } else if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        if (obj3 instanceof Document) {
                            Document document = (Document) obj3;
                            SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(document, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, obj2, z, i);
                            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
                            if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                                ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj4, document);
                            }
                        } else if (obj3 instanceof BotInlineResult) {
                            BotInlineResult botInlineResult = (BotInlineResult) obj3;
                            if (botInlineResult.document != null) {
                                MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(botInlineResult.document, (int) (System.currentTimeMillis() / 1000));
                                if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                                    ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj4, botInlineResult.document);
                                }
                            }
                            User user = (User) obj4;
                            HashMap hashMap = new HashMap();
                            hashMap.put("id", botInlineResult.id);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("");
                            stringBuilder.append(botInlineResult.query_id);
                            hashMap.put("query_id", stringBuilder.toString());
                            SendMessagesHelper.prepareSendingBotContextResult(ChatActivityEnterView.this.accountInstance, botInlineResult, hashMap, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, z, i);
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.searchingType = 0;
                                ChatActivityEnterView.this.emojiView.closeSearch(true);
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                        }
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onMessageSend(null, z, i2);
                        }
                    } else {
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterViewDelegate access$1500 = ChatActivityEnterView.this.delegate;
                            if (view2 == null) {
                                view2 = ChatActivityEnterView.this.slowModeButton;
                            }
                            access$1500.onUpdateSlowModeButton(view2, true, ChatActivityEnterView.this.slowModeButton.getText());
                        }
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
                        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new -$$Lambda$ChatActivityEnterView$35$nM0J6WqYJ0HWGbu9IokP4XIRCXU(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                    }
                }

                public /* synthetic */ void lambda$onClearEmojiRecent$1$ChatActivityEnterView$35(DialogInterface dialogInterface, int i) {
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
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).removeStickersSet(ChatActivityEnterView.this.parentActivity, stickerSetCovered.set, 2, ChatActivityEnterView.this.parentFragment, false);
                }

                public void onStickerSetRemove(StickerSetCovered stickerSetCovered) {
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
                        NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                        int i = NotificationCenter.stopAllHeavyOperations;
                        Object[] objArr = new Object[1];
                        int i2 = 0;
                        objArr[0] = Integer.valueOf(1);
                        globalInstance.postNotificationName(i, objArr);
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        int height = chatActivityEnterView.sizeNotifierLayout.getHeight();
                        if (VERSION.SDK_INT >= 21) {
                            i2 = AndroidUtilities.statusBarHeight;
                        }
                        chatActivityEnterView.stickersExpandedHeight = (((height - i2) - ActionBar.getCurrentActionBarHeight()) - ChatActivityEnterView.this.getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                        if (ChatActivityEnterView.this.searchingType == 2) {
                            chatActivityEnterView = ChatActivityEnterView.this;
                            i = chatActivityEnterView.stickersExpandedHeight;
                            int dp = AndroidUtilities.dp(120.0f);
                            Point point = AndroidUtilities.displaySize;
                            chatActivityEnterView.stickersExpandedHeight = Math.min(i, dp + (point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight));
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
                        int access$9800 = point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                        float max = (float) Math.max(Math.min(i + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - access$9800));
                        ChatActivityEnterView.this.emojiView.setTranslationY(max);
                        ChatActivityEnterView.this.setTranslationY(max);
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        chatActivityEnterView.stickersExpansionProgress = max / ((float) (-(chatActivityEnterView.stickersExpandedHeight - access$9800)));
                        ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                    }
                }

                private boolean allowDragging() {
                    return ChatActivityEnterView.this.stickersTabOpen && ((ChatActivityEnterView.this.stickersExpanded || ChatActivityEnterView.this.messageEditText.length() <= 0) && ChatActivityEnterView.this.emojiView.areThereAnyStickers());
                }
            });
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
            sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 1);
            checkChannelRights();
        }
    }

    /* renamed from: onStickerSelected */
    public void lambda$onStickerSelected$27$ChatActivityEnterView(Document document, Object obj, boolean z, boolean z2, int i) {
        if (isInScheduleMode() && i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new -$$Lambda$ChatActivityEnterView$f0V3X4ZqkkxYVwPpugUT__2f3kU(this, document, obj, z));
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
                chatActivityEnterViewDelegate.onMessageSend(null, true, i);
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

    public void addStickerToRecent(Document document) {
        createEmojiView();
        this.emojiView.addRecentSticker(document);
    }

    public void hideEmojiView() {
        if (!this.emojiViewVisible) {
            EmojiView emojiView = this.emojiView;
            if (emojiView != null && emojiView.getVisibility() != 8) {
                this.sizeNotifierLayout.removeView(this.emojiView);
                this.emojiView.setVisibility(8);
            }
        }
    }

    public void showEmojiView() {
        showPopup(1, 0);
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
                if (this.emojiView.getParent() == null) {
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                    sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 1);
                }
                this.emojiView.setVisibility(0);
                this.emojiViewVisible = true;
                BotKeyboardView botKeyboardView = this.botKeyboardView;
                if (!(botKeyboardView == null || botKeyboardView.getVisibility() == 8)) {
                    this.botKeyboardView.setVisibility(8);
                }
                view = this.emojiView;
            } else if (i2 == 1) {
                this.botKeyboardViewVisible = true;
                EmojiView emojiView = this.emojiView;
                if (!(emojiView == null || emojiView.getVisibility() == 8)) {
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
            BotKeyboardView botKeyboardView2 = this.botKeyboardView;
            if (botKeyboardView2 != null) {
                botKeyboardView2.setPanelHeight(i3);
            }
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
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
        int z22;
        int i;
        if (z22 && this.currentEmojiIcon == -1) {
            z22 = 0;
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
                this.emojiButton[z22].setImageResource(NUM);
            } else if (i == 1) {
                this.emojiButton[z22].setImageResource(NUM);
            } else if (i == 2) {
                this.emojiButton[z22].setImageResource(NUM);
            } else if (i == 3) {
                this.emojiButton[z22].setImageResource(NUM);
            }
            ImageView imageView = this.emojiButton[z22];
            if (i == 2) {
                obj = Integer.valueOf(1);
            }
            imageView.setTag(obj);
            this.currentEmojiIcon = i;
            if (z22 != 0) {
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
        return this.emojiViewVisible || this.botKeyboardViewVisible;
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    public void addRecentGif(Document document) {
        MediaDataController.getInstance(this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
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
                if (((Integer) objArr[0]).intValue() == this.recordingGuid) {
                    long longValue = ((Long) objArr[1]).longValue();
                    long j = longValue / 1000;
                    int i4 = ((int) (longValue % 1000)) / 10;
                    String format = String.format("%02d:%02d.%02d", new Object[]{Long.valueOf(j / 60), Long.valueOf(j % 60), Integer.valueOf(i4)});
                    String str = this.lastTimeString;
                    if (str == null || !str.equals(format)) {
                        if (!(this.lastTypingSendTime == j || j % 5 != 0 || isInScheduleMode())) {
                            this.lastTypingSendTime = j;
                            MessagesController messagesController = this.accountInstance.getMessagesController();
                            j = this.dialog_id;
                            ImageView imageView = this.videoSendButton;
                            int i5 = (imageView == null || imageView.getTag() == null) ? 1 : 7;
                            messagesController.sendTyping(j, i5, 0);
                        }
                        TextView textView = this.recordTimeText;
                        if (textView != null) {
                            textView.setText(format);
                        }
                    }
                    RecordCircle recordCircle = this.recordCircle;
                    if (recordCircle != null) {
                        recordCircle.setAmplitude(((Double) objArr[2]).doubleValue());
                    }
                    ImageView imageView2 = this.videoSendButton;
                    if (!(imageView2 == null || imageView2.getTag() == null || longValue < 59500)) {
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
                if (((Integer) objArr[0]).intValue() == this.recordingGuid) {
                    if (this.recordingAudioVideo) {
                        this.accountInstance.getMessagesController().sendTyping(this.dialog_id, 2, 0);
                        this.recordingAudioVideo = false;
                        updateRecordIntefrace();
                    }
                    if (i == NotificationCenter.recordStopped) {
                        num = (Integer) objArr[1];
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
                }
            } else if (i == NotificationCenter.recordStarted) {
                if (((Integer) objArr[0]).intValue() == this.recordingGuid && !this.recordingAudioVideo) {
                    this.recordingAudioVideo = true;
                    updateRecordIntefrace();
                }
            } else if (i == NotificationCenter.audioDidSent) {
                if (((Integer) objArr[0]).intValue() == this.recordingGuid) {
                    Object obj = objArr[1];
                    if (obj instanceof VideoEditedInfo) {
                        this.videoToSendMessageObject = (VideoEditedInfo) obj;
                        this.audioToSendPath = (String) objArr[2];
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
                        this.audioToSend = (TL_document) objArr[1];
                        this.audioToSendPath = (String) objArr[2];
                        if (this.audioToSend == null) {
                            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                            if (chatActivityEnterViewDelegate != null) {
                                chatActivityEnterViewDelegate.onMessageSend(null, true, 0);
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
                                    this.recordedAudioTimeTextView.setText(AndroidUtilities.formatShortDuration(i));
                                    closeKeyboard();
                                    hidePopup(false);
                                    checkSendButton(false);
                                } else {
                                    i2++;
                                }
                            }
                            this.recordedAudioTimeTextView.setText(AndroidUtilities.formatShortDuration(i));
                            closeKeyboard();
                            hidePopup(false);
                            checkSendButton(false);
                        }
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
            } else if (i == NotificationCenter.featuredStickersDidLoad) {
                if (this.emojiButton != null) {
                    while (true) {
                        ImageView[] imageViewArr = this.emojiButton;
                        if (i3 >= imageViewArr.length) {
                            break;
                        }
                        imageViewArr[i3].invalidate();
                        i3++;
                    }
                }
            } else if (i == NotificationCenter.messageReceivedByServer) {
                if (!((Boolean) objArr[6]).booleanValue() && ((Long) objArr[3]).longValue() == this.dialog_id) {
                    ChatFull chatFull = this.info;
                    if (chatFull != null && chatFull.slowmode_seconds != 0) {
                        Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(this.info.id));
                        if (chat != null && !ChatObject.hasAdminRights(chat)) {
                            chatFull = this.info;
                            i2 = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                            ChatFull chatFull2 = this.info;
                            chatFull.slowmode_next_send_date = i2 + chatFull2.slowmode_seconds;
                            chatFull2.flags |= 262144;
                            setSlowModeTimer(chatFull2.slowmode_next_send_date);
                        }
                    }
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
                ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new -$$Lambda$ChatActivityEnterView$bTokemSYyvMgwjghBZNOWpnTNrY(this));
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
                ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new -$$Lambda$ChatActivityEnterView$_kv_SBl6gVv9CNNuAd7mdWeNYGs(this));
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

    public /* synthetic */ void lambda$checkStickresExpandHeight$28$ChatActivityEnterView(ValueAnimator valueAnimator) {
        this.sizeNotifierLayout.invalidate();
    }

    public /* synthetic */ void lambda$checkStickresExpandHeight$29$ChatActivityEnterView(ValueAnimator valueAnimator) {
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
            if (this.stickersExpanded) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(1));
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
                    r9 = new Animator[3];
                    r9[0] = ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)});
                    r9[1] = ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - i)});
                    r9[2] = ObjectAnimator.ofFloat(this.stickersArrow, str, new float[]{1.0f});
                    animatorSet.playTogether(r9);
                    animatorSet.setDuration(400);
                    animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new -$$Lambda$ChatActivityEnterView$88vDHwO1dYmnzIgrWrCa2FXI2n0(this, i));
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ChatActivityEnterView.this.stickersExpansionAnim = null;
                            ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(512));
                        }
                    });
                    this.stickersExpansionAnim = animatorSet;
                    this.emojiView.setLayerType(2, null);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(512));
                    animatorSet.start();
                } else {
                    this.stickersExpansionProgress = 1.0f;
                    setTranslationY((float) (-(this.stickersExpandedHeight - i)));
                    this.emojiView.setTranslationY((float) (-(this.stickersExpandedHeight - i)));
                    this.stickersArrow.setAnimationProgress(1.0f);
                }
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(1));
                if (z2) {
                    this.closeAnimationInProgress = true;
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    r5 = new Animator[3];
                    r5[0] = ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{0});
                    r5[1] = ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{0});
                    r5[2] = ObjectAnimator.ofFloat(this.stickersArrow, str, new float[]{0.0f});
                    animatorSet2.playTogether(r5);
                    animatorSet2.setDuration(400);
                    animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    ((ObjectAnimator) animatorSet2.getChildAnimations().get(0)).addUpdateListener(new -$$Lambda$ChatActivityEnterView$lJSCRzweiAfnlNbwBsdYjoBikIs(this, i));
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
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(512));
                        }
                    });
                    this.stickersExpansionAnim = animatorSet2;
                    this.emojiView.setLayerType(2, null);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(512));
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
}
