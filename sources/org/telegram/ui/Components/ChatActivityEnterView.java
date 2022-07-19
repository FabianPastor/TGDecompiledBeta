package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
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
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$BotMenuButton;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_botMenuButton;
import org.telegram.tgnet.TLRPC$TL_channels_sendAsPeers;
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
import org.telegram.tgnet.TLRPC$TL_keyboardButtonSimpleWebView;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUserProfile;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonWebView;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageEntityBold;
import org.telegram.tgnet.TLRPC$TL_messageEntityCode;
import org.telegram.tgnet.TLRPC$TL_messageEntityCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC$TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_messageEntityPre;
import org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler;
import org.telegram.tgnet.TLRPC$TL_messageEntityStrike;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageEntityUnderline;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
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
import org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.SenderSelectPopup;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupStickersActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.StickersActivity;

public class ChatActivityEnterView extends BlurredFrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StickersAlert.StickersAlertDelegate {
    /* access modifiers changed from: private */
    public AccountInstance accountInstance;
    private AdjustPanLayoutHelper adjustPanLayoutHelper;
    private boolean allowAnimatedEmoji;
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
    private TLRPC$TL_document audioToSend;
    /* access modifiers changed from: private */
    public MessageObject audioToSendMessageObject;
    private String audioToSendPath;
    /* access modifiers changed from: private */
    public FrameLayout audioVideoButtonContainer;
    /* access modifiers changed from: private */
    public ChatActivityEnterViewAnimatedIconView audioVideoSendButton;
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
    private TLRPC$TL_replyKeyboardMarkup botReplyMarkup;
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
    /* access modifiers changed from: private */
    public int currentLimit;
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
    private CharSequence draftMessage;
    private boolean draftSearchWebpage;
    private boolean editingCaption;
    /* access modifiers changed from: private */
    public MessageObject editingMessageObject;
    /* access modifiers changed from: private */
    public ChatActivityEnterViewAnimatedIconView emojiButton;
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
    private boolean isInVideoMode;
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
    private TLRPC$KeyboardButton pendingLocationButton;
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

        /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$bottomPanelTranslationYChanged(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate, float f) {
            }

            public static int $default$getContentViewHeight(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
                return 0;
            }

            public static TLRPC$TL_channels_sendAsPeers $default$getSendAsPeers(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
                return null;
            }

            public static boolean $default$hasForwardingMessages(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
                return false;
            }

            public static boolean $default$hasScheduledMessages(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
                return true;
            }

            public static int $default$measureKeyboardHeight(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
                return 0;
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

        int getContentViewHeight();

        TLRPC$TL_channels_sendAsPeers getSendAsPeers();

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
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$17(View view, MotionEvent motionEvent) {
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
            ChatActivityEnterView.this.seekBarWaveform.setDelegate(new ChatActivityEnterView$SeekBarWaveformView$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(float f) {
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
            this.drawable.setMasterParent(this);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attachedToWindow = false;
            this.drawable.stop();
            this.drawable.setMasterParent((View) null);
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
            int access$100 = ChatActivityEnterView.this.getThemedColor("chat_recordedVoiceDot");
            int access$1002 = ChatActivityEnterView.this.getThemedColor("chat_messagePanelBackground");
            ChatActivityEnterView.this.redDotPaint.setColor(access$100);
            this.drawable.beginApplyLayerColors();
            this.drawable.setLayerColor("Cup Red.**", access$100);
            this.drawable.setLayerColor("Box.**", access$100);
            this.drawable.setLayerColor("Line 1.**", access$1002);
            this.drawable.setLayerColor("Line 2.**", access$1002);
            this.drawable.setLayerColor("Line 3.**", access$1002);
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
                        if (ChatActivityEnterView.this.isInVideoMode()) {
                            ChatActivityEnterView.this.delegate.needStartRecordVideo(3, true, 0);
                        } else {
                            MediaController.getInstance().stopRecording(2, true, 0);
                            ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
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
            int size = View.MeasureSpec.getSize(i);
            int dp = AndroidUtilities.dp(194.0f);
            if (this.lastSize != size) {
                this.lastSize = size;
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
            }
            StaticLayout staticLayout2 = this.tooltipLayout;
            if (staticLayout2 != null && staticLayout2.getLineCount() > 1) {
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
        /* JADX WARNING: Removed duplicated region for block: B:153:0x0562  */
        /* JADX WARNING: Removed duplicated region for block: B:158:0x05c6  */
        /* JADX WARNING: Removed duplicated region for block: B:161:0x063f  */
        /* JADX WARNING: Removed duplicated region for block: B:164:0x0652  */
        /* JADX WARNING: Removed duplicated region for block: B:178:0x067b  */
        /* JADX WARNING: Removed duplicated region for block: B:183:0x0699  */
        /* JADX WARNING: Removed duplicated region for block: B:188:0x06c6  */
        /* JADX WARNING: Removed duplicated region for block: B:189:0x07ed  */
        /* JADX WARNING: Removed duplicated region for block: B:193:0x0818  */
        /* JADX WARNING: Removed duplicated region for block: B:194:0x081d  */
        /* JADX WARNING: Removed duplicated region for block: B:205:0x0836  */
        /* JADX WARNING: Removed duplicated region for block: B:210:0x084b  */
        /* JADX WARNING: Removed duplicated region for block: B:217:0x0882  */
        /* JADX WARNING: Removed duplicated region for block: B:220:0x097d  */
        /* JADX WARNING: Removed duplicated region for block: B:223:0x098c  */
        /* JADX WARNING: Removed duplicated region for block: B:229:0x0a62  */
        /* JADX WARNING: Removed duplicated region for block: B:234:0x0a9e  */
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
                if (r1 <= 0) goto L_0x0207
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r1 = r1.paint
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                java.lang.String r3 = "chat_messagePanelVoiceBackground"
                int r2 = r2.getThemedColor(r3)
                org.telegram.ui.Components.ChatActivityEnterView r3 = org.telegram.ui.Components.ChatActivityEnterView.this
                java.lang.String r4 = "chat_recordedVoiceBackground"
                int r3 = r3.getThemedColor(r4)
                float r4 = r6.progressToSeekbarStep3
                int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r3, r4)
                r1.setColor(r2)
                goto L_0x0218
            L_0x0207:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r1 = r1.paint
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                java.lang.String r3 = "chat_messagePanelVoiceBackground"
                int r2 = r2.getThemedColor(r3)
                r1.setColor(r2)
            L_0x0218:
                r1 = 0
                boolean r2 = r37.isSendButtonVisible()
                r27 = 1125515264(0x43160000, float:150.0)
                if (r2 == 0) goto L_0x0253
                float r2 = r6.progressToSendButton
                r3 = 1065353216(0x3var_, float:1.0)
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 == 0) goto L_0x024a
                float r1 = (float) r8
                float r1 = r1 / r27
                float r2 = r2 + r1
                r6.progressToSendButton = r2
                int r1 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r1 <= 0) goto L_0x0235
                r6.progressToSendButton = r3
            L_0x0235:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r1 = r1.isInVideoMode()
                if (r1 == 0) goto L_0x0244
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r1 = r1.cameraDrawable
                goto L_0x024a
            L_0x0244:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r1 = r1.micDrawable
            L_0x024a:
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r2 = r2.sendDrawable
            L_0x0250:
                r3 = r1
                r4 = r2
                goto L_0x0269
            L_0x0253:
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r2 = r2.isInVideoMode()
                if (r2 == 0) goto L_0x0262
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r2 = r2.cameraDrawable
                goto L_0x0250
            L_0x0262:
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.drawable.Drawable r2 = r2.micDrawable
                goto L_0x0250
            L_0x0269:
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
                if (r3 == 0) goto L_0x02c4
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
            L_0x02c4:
                r0 = 1113849856(0x42640000, float:57.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                float r0 = r14 / r0
                r1 = 1065353216(0x3var_, float:1.0)
                float r10 = r1 - r0
                boolean r0 = r6.incIdle
                if (r0 == 0) goto L_0x02e7
                float r0 = r6.idleProgress
                r2 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
                float r0 = r0 + r2
                r6.idleProgress = r0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x02f9
                r0 = 0
                r6.incIdle = r0
                r6.idleProgress = r1
                goto L_0x02f9
            L_0x02e7:
                float r0 = r6.idleProgress
                r1 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
                float r0 = r0 - r1
                r6.idleProgress = r0
                r1 = 0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 >= 0) goto L_0x02f9
                r0 = 1
                r6.incIdle = r0
                r6.idleProgress = r1
            L_0x02f9:
                org.telegram.ui.Components.ChatActivityEnterView r0 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r0 = r0.configAnimationsEnabled
                r11 = 1094713344(0x41400000, float:12.0)
                if (r0 == 0) goto L_0x0363
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
            L_0x0363:
                long r0 = java.lang.System.currentTimeMillis()
                r6.lastUpdateTime = r0
                float r0 = r6.slideToCancelProgress
                int r1 = (r0 > r19 ? 1 : (r0 == r19 ? 0 : -1))
                if (r1 <= 0) goto L_0x0372
                r0 = 1065353216(0x3var_, float:1.0)
                goto L_0x0374
            L_0x0372:
                float r0 = r0 / r19
            L_0x0374:
                org.telegram.ui.Components.ChatActivityEnterView r1 = org.telegram.ui.Components.ChatActivityEnterView.this
                boolean r1 = r1.configAnimationsEnabled
                if (r1 == 0) goto L_0x0411
                r1 = 1065353216(0x3var_, float:1.0)
                int r2 = (r15 > r1 ? 1 : (r15 == r1 ? 0 : -1))
                if (r2 == 0) goto L_0x0411
                int r2 = (r25 > r21 ? 1 : (r25 == r21 ? 0 : -1))
                if (r2 >= 0) goto L_0x0411
                r2 = 0
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 <= 0) goto L_0x0411
                boolean r2 = r6.canceledByGesture
                if (r2 != 0) goto L_0x0411
                boolean r2 = r6.showWaves
                if (r2 == 0) goto L_0x03a5
                float r2 = r6.wavesEnterAnimation
                int r5 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
                if (r5 == 0) goto L_0x03a5
                r5 = 1025758986(0x3d23d70a, float:0.04)
                float r2 = r2 + r5
                r6.wavesEnterAnimation = r2
                int r2 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
                if (r2 <= 0) goto L_0x03a5
                r6.wavesEnterAnimation = r1
            L_0x03a5:
                boolean r1 = r6.voiceEnterTransitionInProgress
                if (r1 != 0) goto L_0x0411
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
            L_0x0411:
                boolean r0 = r6.voiceEnterTransitionInProgress
                if (r0 != 0) goto L_0x054e
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
                if (r0 != 0) goto L_0x054e
                float r0 = r6.transformToSeekbar
                r1 = 0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 == 0) goto L_0x050c
                float r0 = r6.progressToSeekbarStep3
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x04f4
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
            L_0x045e:
                android.view.ViewParent r8 = r37.getParent()
                if (r3 == r8) goto L_0x0477
                int r8 = r3.getTop()
                int r28 = r28 + r8
                int r8 = r3.getLeft()
                int r31 = r31 + r8
                android.view.ViewParent r3 = r3.getParent()
                android.view.View r3 = (android.view.View) r3
                goto L_0x045e
            L_0x0477:
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
                if (r14 == 0) goto L_0x04b3
                r11 = 0
                goto L_0x04ba
            L_0x04b3:
                int r11 = r11.getMeasuredHeight()
                float r11 = (float) r11
                float r11 = r11 / r22
            L_0x04ba:
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
                goto L_0x0523
            L_0x04f4:
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
                goto L_0x0523
            L_0x050c:
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
            L_0x0523:
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
                goto L_0x0558
            L_0x054e:
                r11 = r4
                r33 = r8
                r35 = r10
                r28 = r14
                r8 = r30
                r10 = r3
            L_0x0558:
                boolean r0 = r37.isSendButtonVisible()
                r1 = 1108344832(0x42100000, float:36.0)
                r2 = 1090519040(0x41000000, float:8.0)
                if (r0 == 0) goto L_0x05c6
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
                if (r9 <= 0) goto L_0x05a7
                r9 = 1065353216(0x3var_, float:1.0)
                goto L_0x05a9
            L_0x05a7:
                float r9 = r35 / r21
            L_0x05a9:
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
                goto L_0x0635
            L_0x05c6:
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
            L_0x0635:
                boolean r1 = r6.showTooltip
                r21 = 1101004800(0x41a00000, float:20.0)
                r28 = 1077936128(0x40400000, float:3.0)
                r29 = 1082130432(0x40800000, float:4.0)
                if (r1 == 0) goto L_0x0652
                long r30 = java.lang.System.currentTimeMillis()
                r32 = r3
                long r2 = r6.showTooltipStartTime
                long r30 = r30 - r2
                r2 = 200(0xc8, double:9.9E-322)
                int r36 = (r30 > r2 ? 1 : (r30 == r2 ? 0 : -1))
                if (r36 > 0) goto L_0x0650
                goto L_0x0654
            L_0x0650:
                r3 = 0
                goto L_0x065b
            L_0x0652:
                r32 = r3
            L_0x0654:
                float r2 = r6.tooltipAlpha
                r3 = 0
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 == 0) goto L_0x07ed
            L_0x065b:
                r2 = 1061997773(0x3f4ccccd, float:0.8)
                int r2 = (r35 > r2 ? 1 : (r35 == r2 ? 0 : -1))
                if (r2 < 0) goto L_0x0674
                boolean r2 = r37.isSendButtonVisible()
                if (r2 != 0) goto L_0x0674
                float r2 = r6.exitTransition
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 != 0) goto L_0x0674
                float r2 = r6.transformToSeekbar
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 == 0) goto L_0x0677
            L_0x0674:
                r2 = 0
                r6.showTooltip = r2
            L_0x0677:
                boolean r2 = r6.showTooltip
                if (r2 == 0) goto L_0x0699
                float r2 = r6.tooltipAlpha
                r3 = 1065353216(0x3var_, float:1.0)
                int r24 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                r30 = r4
                if (r24 == 0) goto L_0x06ac
                r3 = r33
                float r3 = (float) r3
                float r3 = r3 / r27
                float r2 = r2 + r3
                r6.tooltipAlpha = r2
                r3 = 1065353216(0x3var_, float:1.0)
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 < 0) goto L_0x06ac
                r6.tooltipAlpha = r3
                org.telegram.messenger.SharedConfig.increaseLockRecordAudioVideoHintShowed()
                goto L_0x06ac
            L_0x0699:
                r30 = r4
                r3 = r33
                float r2 = r6.tooltipAlpha
                float r3 = (float) r3
                float r3 = r3 / r27
                float r2 = r2 - r3
                r6.tooltipAlpha = r2
                r3 = 0
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 >= 0) goto L_0x06ac
                r6.tooltipAlpha = r3
            L_0x06ac:
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
                if (r3 == 0) goto L_0x07ef
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
                goto L_0x07f3
            L_0x07ed:
                r30 = r4
            L_0x07ef:
                r31 = r10
                r20 = r11
            L_0x07f3:
                r38.save()
                int r1 = r37.getMeasuredWidth()
                int r2 = r37.getMeasuredHeight()
                org.telegram.ui.Components.ChatActivityEnterView r3 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.widget.FrameLayout r3 = r3.textFieldContainer
                int r3 = r3.getMeasuredHeight()
                int r2 = r2 - r3
                r3 = 0
                r7.clipRect(r3, r3, r1, r2)
                float r1 = r6.scale
                r2 = 1065353216(0x3var_, float:1.0)
                float r3 = r2 - r1
                r4 = 0
                int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r3 == 0) goto L_0x081d
                float r23 = r2 - r1
                r1 = r23
                goto L_0x082b
            L_0x081d:
                int r1 = (r15 > r4 ? 1 : (r15 == r4 ? 0 : -1))
                if (r1 == 0) goto L_0x0823
                r1 = r15
                goto L_0x082b
            L_0x0823:
                int r1 = (r25 > r4 ? 1 : (r25 == r4 ? 0 : -1))
                if (r1 == 0) goto L_0x082a
                r1 = r25
                goto L_0x082b
            L_0x082a:
                r1 = 0
            L_0x082b:
                float r2 = r6.slideToCancelProgress
                int r2 = (r2 > r19 ? 1 : (r2 == r19 ? 0 : -1))
                if (r2 < 0) goto L_0x084b
                boolean r2 = r6.canceledByGesture
                if (r2 == 0) goto L_0x0836
                goto L_0x084b
            L_0x0836:
                float r2 = r6.slideToCancelLockProgress
                r3 = 1065353216(0x3var_, float:1.0)
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 == 0) goto L_0x0861
                r4 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
                float r2 = r2 + r4
                r6.slideToCancelLockProgress = r2
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 <= 0) goto L_0x0861
                r6.slideToCancelLockProgress = r3
                goto L_0x0861
            L_0x084b:
                r2 = 0
                r6.showTooltip = r2
                float r2 = r6.slideToCancelLockProgress
                r3 = 0
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 == 0) goto L_0x0861
                r4 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
                float r2 = r2 - r4
                r6.slideToCancelLockProgress = r2
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 >= 0) goto L_0x0861
                r6.slideToCancelLockProgress = r3
            L_0x0861:
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
                if (r1 <= 0) goto L_0x0882
                goto L_0x0883
            L_0x0882:
                r2 = r3
            L_0x0883:
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
                if (r8 == 0) goto L_0x0988
                float r8 = org.telegram.messenger.AndroidUtilities.dpf2(r22)
                float r8 = r8 * r15
                android.graphics.Paint r11 = r6.lockBackgroundPaint
                r7.drawCircle(r0, r1, r8, r11)
            L_0x0988:
                int r0 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
                if (r0 == 0) goto L_0x0a54
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
                if (r2 <= 0) goto L_0x09f6
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r1 = (float) r1
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r2 = (float) r2
                r7.rotate(r9, r1, r2)
            L_0x09f6:
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
            L_0x0a54:
                r38.restore()
                r38.restore()
                float r0 = r6.scale
                r1 = 1065353216(0x3var_, float:1.0)
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 == 0) goto L_0x0a9e
                int r0 = r6.slideDelta
                int r12 = r12 + r0
                float r0 = (float) r12
                org.telegram.ui.Components.ChatActivityEnterView r2 = org.telegram.ui.Components.ChatActivityEnterView.this
                android.graphics.Paint r2 = r2.paint
                r8 = r16
                r3 = r17
                r7.drawCircle(r0, r3, r8, r2)
                boolean r0 = r6.canceledByGesture
                if (r0 == 0) goto L_0x0a7c
                float r0 = r6.slideToCancelProgress
                float r15 = r1 - r0
                goto L_0x0a7e
            L_0x0a7c:
                r15 = 1065353216(0x3var_, float:1.0)
            L_0x0a7e:
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
                goto L_0x0aa0
            L_0x0a9e:
                r8 = r16
            L_0x0aa0:
                r6.drawingCircleRadius = r8
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.onDraw(android.graphics.Canvas):void");
        }

        public void drawIcon(Canvas canvas, int i, int i2, float f) {
            Drawable access$3900;
            Drawable drawable = null;
            if (isSendButtonVisible()) {
                if (this.progressToSendButton != 1.0f) {
                    drawable = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.cameraDrawable : ChatActivityEnterView.this.micDrawable;
                }
                access$3900 = ChatActivityEnterView.this.sendDrawable;
            } else {
                access$3900 = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.cameraDrawable : ChatActivityEnterView.this.micDrawable;
            }
            Drawable drawable2 = access$3900;
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
                    ChatActivityEnterView.this.audioVideoSendButton.setAlpha(1.0f);
                    setVisibility(8);
                } else if (z && this.slideToCancelProgress < 1.0f) {
                    Drawable access$4200 = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.cameraOutline : ChatActivityEnterView.this.micOutline;
                    access$4200.setBounds(drawable.getBounds());
                    float f3 = this.slideToCancelProgress;
                    if (f3 >= 0.93f) {
                        f2 = ((f3 - 0.93f) / 0.07f) * 255.0f;
                    }
                    int i2 = (int) f2;
                    access$4200.setAlpha(i2);
                    access$4200.draw(canvas);
                    access$4200.setAlpha(255);
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
            private int[] coords = new int[2];

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
                if (ChatActivityEnterView.this.pauseRect.contains(f, f2)) {
                    return 2;
                }
                if (ChatActivityEnterView.this.slideText == null || ChatActivityEnterView.this.slideText.cancelRect == null) {
                    return -1;
                }
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(ChatActivityEnterView.this.slideText.cancelRect);
                ChatActivityEnterView.this.slideText.getLocationOnScreen(this.coords);
                int[] iArr = this.coords;
                rectF.offset((float) iArr[0], (float) iArr[1]);
                ChatActivityEnterView.this.recordCircle.getLocationOnScreen(this.coords);
                int[] iArr2 = this.coords;
                rectF.offset((float) (-iArr2[0]), (float) (-iArr2[1]));
                return rectF.contains(f, f2) ? 3 : -1;
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
            public void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                if (i == 1) {
                    accessibilityNodeInfoCompat.setBoundsInParent(ChatActivityEnterView.this.sendRect);
                    accessibilityNodeInfoCompat.setText(LocaleController.getString("Send", NUM));
                } else if (i == 2) {
                    ChatActivityEnterView.this.rect.set((int) ChatActivityEnterView.this.pauseRect.left, (int) ChatActivityEnterView.this.pauseRect.top, (int) ChatActivityEnterView.this.pauseRect.right, (int) ChatActivityEnterView.this.pauseRect.bottom);
                    accessibilityNodeInfoCompat.setBoundsInParent(ChatActivityEnterView.this.rect);
                    accessibilityNodeInfoCompat.setText(LocaleController.getString("Stop", NUM));
                } else if (i == 3) {
                    if (!(ChatActivityEnterView.this.slideText == null || ChatActivityEnterView.this.slideText.cancelRect == null)) {
                        Rect rect = AndroidUtilities.rectTmp2;
                        rect.set(ChatActivityEnterView.this.slideText.cancelRect);
                        ChatActivityEnterView.this.slideText.getLocationOnScreen(this.coords);
                        int[] iArr = this.coords;
                        rect.offset(iArr[0], iArr[1]);
                        ChatActivityEnterView.this.recordCircle.getLocationOnScreen(this.coords);
                        int[] iArr2 = this.coords;
                        rect.offset(-iArr2[0], -iArr2[1]);
                        accessibilityNodeInfoCompat.setBoundsInParent(rect);
                    }
                    accessibilityNodeInfoCompat.setText(LocaleController.getString("Cancel", NUM));
                }
            }
        }
    }

    public ChatActivityEnterView(Activity activity, SizeNotifierFrameLayout sizeNotifierFrameLayout, ChatActivity chatActivity, boolean z) {
        this(activity, sizeNotifierFrameLayout, chatActivity, z, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    @android.annotation.SuppressLint({"ClickableViewAccessibility"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatActivityEnterView(android.app.Activity r37, org.telegram.ui.Components.SizeNotifierFrameLayout r38, org.telegram.ui.ChatActivity r39, boolean r40, org.telegram.ui.ActionBar.Theme.ResourcesProvider r41) {
        /*
            r36 = this;
            r7 = r36
            r8 = r37
            r0 = r38
            r9 = r39
            r10 = r41
            if (r9 != 0) goto L_0x000e
            r1 = 0
            goto L_0x0010
        L_0x000e:
            org.telegram.ui.Components.SizeNotifierFrameLayout r1 = r9.contentView
        L_0x0010:
            r7.<init>(r8, r1)
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r7.currentAccount = r1
            org.telegram.messenger.AccountInstance r1 = org.telegram.messenger.AccountInstance.getInstance(r1)
            r7.accountInstance = r1
            r12 = 1
            r7.lineCount = r12
            r13 = -1
            r7.currentLimit = r13
            org.telegram.ui.Components.ChatActivityEnterView$BotMenuButtonType r1 = org.telegram.ui.Components.ChatActivityEnterView.BotMenuButtonType.NO_BUTTON
            r7.botMenuButtonType = r1
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r7.animationParamsX = r1
            org.telegram.ui.Components.ChatActivityEnterView$1 r1 = new org.telegram.ui.Components.ChatActivityEnterView$1
            r1.<init>(r7)
            r7.mediaMessageButtonsDelegate = r1
            r7.currentPopupContentType = r13
            r7.isPaused = r12
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r7.startedDraggingX = r14
            r1 = 1117782016(0x42a00000, float:80.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r7.distCanMove = r1
            r1 = 2
            int[] r1 = new int[r1]
            r7.location = r1
            r7.messageWebPageSearch = r12
            r7.animatingContentType = r13
            r15 = 1065353216(0x3var_, float:1.0)
            r7.doneButtonEnabledProgress = r15
            r7.doneButtonEnabled = r12
            org.telegram.ui.Components.ChatActivityEnterView$2 r1 = new org.telegram.ui.Components.ChatActivityEnterView$2
            r1.<init>()
            r7.openKeyboardRunnable = r1
            org.telegram.ui.Components.ChatActivityEnterView$3 r1 = new org.telegram.ui.Components.ChatActivityEnterView$3
            r1.<init>()
            r7.updateExpandabilityRunnable = r1
            org.telegram.ui.Components.ChatActivityEnterView$4 r1 = new org.telegram.ui.Components.ChatActivityEnterView$4
            java.lang.Class<java.lang.Integer> r2 = java.lang.Integer.class
            java.lang.String r3 = "translationY"
            r1.<init>(r7, r2, r3)
            r7.roundedTranslationYProperty = r1
            org.telegram.ui.Components.ChatActivityEnterView$5 r1 = new org.telegram.ui.Components.ChatActivityEnterView$5
            java.lang.Class<java.lang.Float> r2 = java.lang.Float.class
            java.lang.String r3 = "scale"
            r1.<init>(r7, r2, r3)
            r7.recordCircleScale = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r12)
            r7.redDotPaint = r1
            org.telegram.ui.Components.ChatActivityEnterView$6 r1 = new org.telegram.ui.Components.ChatActivityEnterView$6
            r1.<init>()
            r7.onFinishInitCameraRunnable = r1
            org.telegram.ui.Components.ChatActivityEnterView$7 r1 = new org.telegram.ui.Components.ChatActivityEnterView$7
            r1.<init>()
            r7.recordAudioVideoRunnable = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r12)
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
            r7.allowBlur = r12
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>()
            r7.backgroundPaint = r1
            r7.composeShadowAlpha = r15
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda1 r1 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda1
            r1.<init>(r7)
            r7.topViewUpdateListener = r1
            r7.botCommandLastPosition = r13
            r7.resourcesProvider = r10
            java.lang.String r1 = "chat_messagePanelBackground"
            int r1 = r7.getThemedColor(r1)
            r7.backgroundColor = r1
            r6 = 0
            r7.drawBlur = r6
            if (r40 == 0) goto L_0x00e6
            boolean r1 = org.telegram.messenger.SharedConfig.smoothKeyboard
            if (r1 == 0) goto L_0x00e6
            boolean r1 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
            if (r1 != 0) goto L_0x00e6
            if (r9 == 0) goto L_0x00e4
            boolean r1 = r39.isInBubbleMode()
            if (r1 != 0) goto L_0x00e6
        L_0x00e4:
            r1 = 1
            goto L_0x00e7
        L_0x00e6:
            r1 = 0
        L_0x00e7:
            r7.smoothKeyboard = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r12)
            r7.dotPaint = r1
            java.lang.String r2 = "chat_emojiPanelNewTrending"
            int r2 = r7.getThemedColor(r2)
            r1.setColor(r2)
            r7.setFocusable(r12)
            r7.setFocusableInTouchMode(r12)
            r7.setWillNotDraw(r6)
            r7.setClipChildren(r6)
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
            r7.parentFragment = r9
            if (r9 == 0) goto L_0x01b4
            int r1 = r39.getClassGuid()
            r7.recordingGuid = r1
        L_0x01b4:
            r7.sizeNotifierLayout = r0
            r0.setDelegate(r7)
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r1 = "send_by_enter"
            boolean r1 = r0.getBoolean(r1, r6)
            r7.sendByEnter = r1
            java.lang.String r1 = "view_animations"
            boolean r0 = r0.getBoolean(r1, r12)
            r7.configAnimationsEnabled = r0
            org.telegram.ui.Components.ChatActivityEnterView$9 r0 = new org.telegram.ui.Components.ChatActivityEnterView$9
            r0.<init>(r8)
            r7.textFieldContainer = r0
            r0.setClipChildren(r6)
            android.widget.FrameLayout r0 = r7.textFieldContainer
            r0.setClipToPadding(r6)
            android.widget.FrameLayout r0 = r7.textFieldContainer
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r0.setPadding(r6, r1, r6, r6)
            android.widget.FrameLayout r0 = r7.textFieldContainer
            r16 = -1
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 83
            r19 = 0
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r7.addView(r0, r1)
            org.telegram.ui.Components.ChatActivityEnterView$10 r5 = new org.telegram.ui.Components.ChatActivityEnterView$10
            r5.<init>(r8)
            r5.setClipChildren(r6)
            android.widget.FrameLayout r0 = r7.textFieldContainer
            r18 = 80
            r20 = 0
            r21 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r0.addView(r5, r1)
            org.telegram.ui.Components.ChatActivityEnterView$11 r0 = new org.telegram.ui.Components.ChatActivityEnterView$11
            r0.<init>(r8)
            r7.emojiButton = r0
            r1 = 2131623982(0x7f0e002e, float:1.887513E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r7.emojiButton
            r0.setFocusable(r12)
            r0 = 1092091904(0x41180000, float:9.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r1 = r7.emojiButton
            r1.setPadding(r0, r0, r0, r0)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r7.emojiButton
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "chat_messagePanelIcons"
            int r2 = r7.getThemedColor(r4)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.SRC_IN
            r1.<init>(r2, r3)
            r0.setColorFilter(r1)
            int r3 = android.os.Build.VERSION.SDK_INT
            java.lang.String r2 = "listSelectorSDK21"
            r1 = 21
            if (r3 < r1) goto L_0x025b
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r7.emojiButton
            int r16 = r7.getThemedColor(r2)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r16)
            r0.setBackground(r1)
        L_0x025b:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r7.emojiButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda22 r1 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda22
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r7.emojiButton
            r16 = 48
            r17 = 1111490560(0x42400000, float:48.0)
            r18 = 83
            r19 = 1077936128(0x40400000, float:3.0)
            r20 = 0
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r5.addView(r0, r1)
            r7.setEmojiButtonImage(r6, r6)
            org.telegram.ui.Components.NumberTextView r0 = new org.telegram.ui.Components.NumberTextView
            r0.<init>(r8)
            r7.captionLimitView = r0
            r1 = 8
            r0.setVisibility(r1)
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
            r0.setCenterAlign(r12)
            org.telegram.ui.Components.NumberTextView r0 = r7.captionLimitView
            r17 = 48
            r18 = 1101004800(0x41a00000, float:20.0)
            r19 = 85
            r20 = 1077936128(0x40400000, float:3.0)
            r23 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r7.addView(r0, r1)
            org.telegram.ui.Components.ChatActivityEnterView$12 r1 = new org.telegram.ui.Components.ChatActivityEnterView$12
            r0 = r1
            r14 = r1
            r13 = 21
            r15 = 8
            r1 = r36
            r24 = r2
            r2 = r37
            r13 = r3
            r3 = r41
            r15 = r4
            r4 = r41
            r25 = r5
            r5 = r39
            r11 = 0
            r6 = r37
            r0.<init>(r2, r3, r4, r5, r6)
            r7.messageEditText = r14
            r0 = 28
            if (r13 < r0) goto L_0x02e7
            r14.setFallbackLineSpacing(r11)
        L_0x02e7:
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda58 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda58
            r2.<init>(r7)
            r1.setDelegate(r2)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            r1.setIncludeFontPadding(r11)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            android.app.Activity r2 = r7.parentActivity
            android.view.Window r2 = r2.getWindow()
            android.view.View r2 = r2.getDecorView()
            r1.setWindowView(r2)
            org.telegram.ui.ChatActivity r1 = r7.parentFragment
            if (r1 == 0) goto L_0x030e
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getCurrentEncryptedChat()
            goto L_0x030f
        L_0x030e:
            r1 = 0
        L_0x030f:
            org.telegram.ui.Components.EditTextCaption r2 = r7.messageEditText
            boolean r3 = r36.supportsSendingNewEntities()
            r2.setAllowTextEntitiesIntersection(r3)
            r7.updateFieldHint(r11)
            r2 = 268435456(0x10000000, float:2.5243549E-29)
            if (r1 == 0) goto L_0x0321
            r2 = 285212672(0x11000000, float:1.00974196E-28)
        L_0x0321:
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            r1.setImeOptions(r2)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            int r2 = r1.getInputType()
            r2 = r2 | 16384(0x4000, float:2.2959E-41)
            r3 = 131072(0x20000, float:1.83671E-40)
            r2 = r2 | r3
            r1.setInputType(r2)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            r1.setSingleLine(r11)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            r2 = 6
            r1.setMaxLines(r2)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            r2 = 1099956224(0x41900000, float:18.0)
            r1.setTextSize(r12, r2)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            r2 = 80
            r1.setGravity(r2)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            r3 = 1093664768(0x41300000, float:11.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setPadding(r11, r3, r11, r4)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            r3 = 0
            r1.setBackgroundDrawable(r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            java.lang.String r3 = "chat_messagePanelText"
            int r3 = r7.getThemedColor(r3)
            r1.setTextColor(r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            java.lang.String r3 = "chat_messageLinkOut"
            int r3 = r7.getThemedColor(r3)
            r1.setLinkTextColor(r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            java.lang.String r3 = "chat_inTextSelectionHighlight"
            int r3 = r7.getThemedColor(r3)
            r1.setHighlightColor(r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            java.lang.String r3 = "chat_messagePanelHint"
            int r3 = r7.getThemedColor(r3)
            r1.setHintColor(r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            java.lang.String r3 = "chat_messagePanelHint"
            int r3 = r7.getThemedColor(r3)
            r1.setHintTextColor(r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            java.lang.String r3 = "chat_messagePanelCursor"
            int r3 = r7.getThemedColor(r3)
            r1.setCursorColor(r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            java.lang.String r3 = "chat_TextSelectionCursor"
            int r3 = r7.getThemedColor(r3)
            r1.setHandlesColor(r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            r26 = -1
            r27 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r28 = 80
            r29 = 1112539136(0x42500000, float:52.0)
            r30 = 0
            if (r40 == 0) goto L_0x03c4
            r3 = 1112014848(0x42480000, float:50.0)
            r31 = 1112014848(0x42480000, float:50.0)
            goto L_0x03c8
        L_0x03c4:
            r3 = 1073741824(0x40000000, float:2.0)
            r31 = 1073741824(0x40000000, float:2.0)
        L_0x03c8:
            r32 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r4 = r25
            r4.addView(r1, r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            org.telegram.ui.Components.ChatActivityEnterView$13 r3 = new org.telegram.ui.Components.ChatActivityEnterView$13
            r3.<init>()
            r1.setOnKeyListener(r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            org.telegram.ui.Components.ChatActivityEnterView$14 r3 = new org.telegram.ui.Components.ChatActivityEnterView$14
            r3.<init>()
            r1.setOnEditorActionListener(r3)
            org.telegram.ui.Components.EditTextCaption r1 = r7.messageEditText
            org.telegram.ui.Components.ChatActivityEnterView$15 r3 = new org.telegram.ui.Components.ChatActivityEnterView$15
            r3.<init>()
            r1.addTextChangedListener(r3)
            r3 = 85
            r6 = 1111490560(0x42400000, float:48.0)
            r14 = 48
            if (r40 == 0) goto L_0x06b0
            org.telegram.ui.ChatActivity r0 = r7.parentFragment
            if (r0 == 0) goto L_0x048e
            android.content.res.Resources r0 = r37.getResources()
            r5 = 2131165530(0x7var_a, float:1.794528E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r5)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.content.res.Resources r5 = r37.getResources()
            r2 = 2131165531(0x7var_b, float:1.7945282E38)
            android.graphics.drawable.Drawable r2 = r5.getDrawable(r2)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            int r12 = r7.getThemedColor(r15)
            android.graphics.PorterDuff$Mode r1 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r12, r1)
            r0.setColorFilter(r5)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r5 = "chat_recordedVoiceDot"
            int r5 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r5, r12)
            r2.setColorFilter(r1)
            org.telegram.ui.Components.CombinedDrawable r1 = new org.telegram.ui.Components.CombinedDrawable
            r1.<init>(r0, r2)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.scheduledButton = r0
            r0.setImageDrawable(r1)
            android.widget.ImageView r0 = r7.scheduledButton
            r1 = 8
            r0.setVisibility(r1)
            android.widget.ImageView r0 = r7.scheduledButton
            r1 = 2131628146(0x7f0e1072, float:1.8883576E38)
            java.lang.String r2 = "ScheduledMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            android.widget.ImageView r0 = r7.scheduledButton
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            r0 = 21
            if (r13 < r0) goto L_0x0478
            android.widget.ImageView r0 = r7.scheduledButton
            r1 = r24
            int r2 = r7.getThemedColor(r1)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2)
            r0.setBackgroundDrawable(r2)
            goto L_0x047a
        L_0x0478:
            r1 = r24
        L_0x047a:
            android.widget.ImageView r0 = r7.scheduledButton
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r14, (int) r3)
            r4.addView(r0, r2)
            android.widget.ImageView r0 = r7.scheduledButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda25 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda25
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            goto L_0x0490
        L_0x048e:
            r1 = r24
        L_0x0490:
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r7.attachLayout = r0
            r0.setOrientation(r11)
            android.widget.LinearLayout r0 = r7.attachLayout
            r0.setEnabled(r11)
            android.widget.LinearLayout r0 = r7.attachLayout
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r2 = (float) r2
            r0.setPivotX(r2)
            android.widget.LinearLayout r0 = r7.attachLayout
            r0.setClipChildren(r11)
            android.widget.LinearLayout r0 = r7.attachLayout
            r2 = -2
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r14, (int) r3)
            r4.addView(r0, r5)
            org.telegram.ui.Components.BotCommandsMenuView r0 = new org.telegram.ui.Components.BotCommandsMenuView
            android.content.Context r2 = r36.getContext()
            r0.<init>(r2)
            r7.botCommandsMenuButton = r0
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda16 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda16
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.BotCommandsMenuView r0 = r7.botCommandsMenuButton
            r26 = -2
            r27 = 1107296256(0x42000000, float:32.0)
            r28 = 83
            r29 = 1092616192(0x41200000, float:10.0)
            r30 = 1090519040(0x41000000, float:8.0)
            r31 = 1092616192(0x41200000, float:10.0)
            r32 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r4.addView(r0, r2)
            org.telegram.ui.Components.BotCommandsMenuView r0 = r7.botCommandsMenuButton
            r2 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r11, r2, r11)
            org.telegram.ui.Components.BotCommandsMenuView r0 = r7.botCommandsMenuButton
            r2 = 1
            r0.setExpanded(r2, r11)
            androidx.recyclerview.widget.LinearLayoutManager r0 = new androidx.recyclerview.widget.LinearLayoutManager
            r0.<init>(r8)
            org.telegram.ui.Components.ChatActivityEnterView$16 r2 = new org.telegram.ui.Components.ChatActivityEnterView$16
            r2.<init>(r8)
            r7.botCommandsMenuContainer = r2
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.setLayoutManager(r0)
            org.telegram.ui.Components.BotCommandsMenuContainer r0 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            org.telegram.ui.Components.BotCommandsMenuView$BotCommandsAdapter r2 = new org.telegram.ui.Components.BotCommandsMenuView$BotCommandsAdapter
            r2.<init>()
            r7.botCommandsAdapter = r2
            r0.setAdapter(r2)
            org.telegram.ui.Components.BotCommandsMenuContainer r0 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            org.telegram.ui.Components.ChatActivityEnterView$17 r2 = new org.telegram.ui.Components.ChatActivityEnterView$17
            r2.<init>(r10, r9)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            org.telegram.ui.Components.BotCommandsMenuContainer r0 = r7.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            org.telegram.ui.Components.ChatActivityEnterView$18 r2 = new org.telegram.ui.Components.ChatActivityEnterView$18
            r2.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r2)
            org.telegram.ui.Components.BotCommandsMenuContainer r0 = r7.botCommandsMenuContainer
            r0.setClipToPadding(r11)
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r7.sizeNotifierLayout
            org.telegram.ui.Components.BotCommandsMenuContainer r2 = r7.botCommandsMenuContainer
            r5 = 14
            r3 = -1
            r12 = 80
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r3, (int) r12)
            r0.addView(r2, r5, r6)
            org.telegram.ui.Components.BotCommandsMenuContainer r0 = r7.botCommandsMenuContainer
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.ChatActivityEnterView$19 r0 = new org.telegram.ui.Components.ChatActivityEnterView$19
            r0.<init>(r8, r7)
            r7.botWebViewMenuContainer = r0
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r7.sizeNotifierLayout
            r6 = 15
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r3, (int) r12)
            r5.addView(r0, r6, r14)
            org.telegram.ui.Components.BotWebViewMenuContainer r0 = r7.botWebViewMenuContainer
            r0.setVisibility(r2)
            org.telegram.ui.Components.BotWebViewMenuContainer r0 = r7.botWebViewMenuContainer
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda34 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda34
            r2.<init>(r7)
            r0.setOnDismissGlobalListener(r2)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.botButton = r0
            org.telegram.ui.Components.ReplaceableIconDrawable r2 = new org.telegram.ui.Components.ReplaceableIconDrawable
            r2.<init>(r8)
            r7.botButtonDrawable = r2
            r0.setImageDrawable(r2)
            org.telegram.ui.Components.ReplaceableIconDrawable r0 = r7.botButtonDrawable
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r3 = r7.getThemedColor(r15)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r5)
            r0.setColorFilter(r2)
            org.telegram.ui.Components.ReplaceableIconDrawable r0 = r7.botButtonDrawable
            r2 = 2131165529(0x7var_, float:1.7945278E38)
            r0.setIcon((int) r2, (boolean) r11)
            android.widget.ImageView r0 = r7.botButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            r0 = 21
            if (r13 < r0) goto L_0x05a4
            android.widget.ImageView r0 = r7.botButton
            int r2 = r7.getThemedColor(r1)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2)
            r0.setBackgroundDrawable(r2)
        L_0x05a4:
            android.widget.ImageView r0 = r7.botButton
            r2 = 8
            r0.setVisibility(r2)
            android.widget.ImageView r0 = r7.botButton
            r2 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r11, r2, r11)
            android.widget.LinearLayout r0 = r7.attachLayout
            android.widget.ImageView r2 = r7.botButton
            r3 = 48
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r3)
            r0.addView(r2, r5)
            android.widget.ImageView r0 = r7.botButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda18 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda18
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.notifyButton = r0
            org.telegram.ui.Components.CrossOutDrawable r0 = new org.telegram.ui.Components.CrossOutDrawable
            r2 = 2131165540(0x7var_, float:1.79453E38)
            r0.<init>(r8, r2, r15)
            r7.notifySilentDrawable = r0
            android.widget.ImageView r2 = r7.notifyButton
            r2.setImageDrawable(r0)
            org.telegram.ui.Components.CrossOutDrawable r0 = r7.notifySilentDrawable
            boolean r2 = r7.silent
            r0.setCrossOut(r2, r11)
            android.widget.ImageView r0 = r7.notifyButton
            boolean r2 = r7.silent
            if (r2 == 0) goto L_0x05f3
            r2 = 2131623973(0x7f0e0025, float:1.8875113E38)
            java.lang.String r3 = "AccDescrChanSilentOn"
            goto L_0x05f8
        L_0x05f3:
            r2 = 2131623972(0x7f0e0024, float:1.887511E38)
            java.lang.String r3 = "AccDescrChanSilentOff"
        L_0x05f8:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            android.widget.ImageView r0 = r7.notifyButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r3 = r7.getThemedColor(r15)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r5)
            r0.setColorFilter(r2)
            android.widget.ImageView r0 = r7.notifyButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            r0 = 21
            if (r13 < r0) goto L_0x0627
            android.widget.ImageView r0 = r7.notifyButton
            int r2 = r7.getThemedColor(r1)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2)
            r0.setBackgroundDrawable(r2)
        L_0x0627:
            android.widget.ImageView r0 = r7.notifyButton
            boolean r2 = r7.canWriteToChannel
            if (r2 == 0) goto L_0x0639
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r2 = r7.delegate
            if (r2 == 0) goto L_0x0637
            boolean r2 = r2.hasScheduledMessages()
            if (r2 != 0) goto L_0x0639
        L_0x0637:
            r6 = 0
            goto L_0x063b
        L_0x0639:
            r6 = 8
        L_0x063b:
            r0.setVisibility(r6)
            android.widget.LinearLayout r0 = r7.attachLayout
            android.widget.ImageView r2 = r7.notifyButton
            r3 = 48
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r3)
            r0.addView(r2, r5)
            android.widget.ImageView r0 = r7.notifyButton
            org.telegram.ui.Components.ChatActivityEnterView$20 r2 = new org.telegram.ui.Components.ChatActivityEnterView$20
            r2.<init>(r8, r9)
            r0.setOnClickListener(r2)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.attachButton = r0
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r3 = r7.getThemedColor(r15)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r5)
            r0.setColorFilter(r2)
            android.widget.ImageView r0 = r7.attachButton
            r2 = 2131165527(0x7var_, float:1.7945274E38)
            r0.setImageResource(r2)
            android.widget.ImageView r0 = r7.attachButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            r0 = 21
            if (r13 < r0) goto L_0x068a
            android.widget.ImageView r0 = r7.attachButton
            int r2 = r7.getThemedColor(r1)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2)
            r0.setBackgroundDrawable(r2)
        L_0x068a:
            android.widget.LinearLayout r0 = r7.attachLayout
            android.widget.ImageView r2 = r7.attachButton
            r3 = 48
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r3)
            r0.addView(r2, r5)
            android.widget.ImageView r0 = r7.attachButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda13 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda13
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            android.widget.ImageView r0 = r7.attachButton
            r2 = 2131623960(0x7f0e0018, float:1.8875086E38)
            java.lang.String r3 = "AccDescrAttachButton"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            goto L_0x06b2
        L_0x06b0:
            r1 = r24
        L_0x06b2:
            org.telegram.ui.Components.SenderSelectView r0 = new org.telegram.ui.Components.SenderSelectView
            android.content.Context r2 = r36.getContext()
            r0.<init>(r2)
            r7.senderSelectView = r0
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda26 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda26
            r2.<init>(r7, r8)
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.SenderSelectView r0 = r7.senderSelectView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.SenderSelectView r0 = r7.senderSelectView
            r28 = 32
            r29 = 1107296256(0x42000000, float:32.0)
            r30 = 83
            r31 = 1092616192(0x41200000, float:10.0)
            r32 = 1090519040(0x41000000, float:8.0)
            r33 = 1092616192(0x41200000, float:10.0)
            r34 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r4.addView(r0, r2)
            org.telegram.ui.Components.ChatActivityEnterView$26 r0 = new org.telegram.ui.Components.ChatActivityEnterView$26
            r0.<init>(r8)
            r7.recordedAudioPanel = r0
            org.telegram.tgnet.TLRPC$TL_document r2 = r7.audioToSend
            if (r2 != 0) goto L_0x06f1
            r6 = 8
            goto L_0x06f2
        L_0x06f1:
            r6 = 0
        L_0x06f2:
            r0.setVisibility(r6)
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
            r5 = 48
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r5, (int) r2)
            r4.addView(r0, r6)
            org.telegram.ui.Components.RLottieImageView r0 = new org.telegram.ui.Components.RLottieImageView
            r0.<init>(r8)
            r7.recordDeleteImageView = r0
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r7.recordDeleteImageView
            r2 = 2131558419(0x7f0d0013, float:1.8742153E38)
            r3 = 28
            r0.setAnimation((int) r2, (int) r3, (int) r3)
            org.telegram.ui.Components.RLottieImageView r0 = r7.recordDeleteImageView
            org.telegram.ui.Components.RLottieDrawable r0 = r0.getAnimatedDrawable()
            r2 = 1
            r0.setInvalidateOnProgressSet(r2)
            r36.updateRecordedDeleteIconColors()
            org.telegram.ui.Components.RLottieImageView r0 = r7.recordDeleteImageView
            r2 = 2131625384(0x7f0e05a8, float:1.8877974E38)
            java.lang.String r3 = "Delete"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            r0 = 21
            if (r13 < r0) goto L_0x0755
            org.telegram.ui.Components.RLottieImageView r0 = r7.recordDeleteImageView
            int r2 = r7.getThemedColor(r1)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2)
            r0.setBackgroundDrawable(r2)
        L_0x0755:
            android.widget.FrameLayout r0 = r7.recordedAudioPanel
            org.telegram.ui.Components.RLottieImageView r2 = r7.recordDeleteImageView
            r3 = 1111490560(0x42400000, float:48.0)
            r5 = 48
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3)
            r0.addView(r2, r6)
            org.telegram.ui.Components.RLottieImageView r0 = r7.recordDeleteImageView
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda12 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda12
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
            r28 = -1
            r29 = -1082130432(0xffffffffbvar_, float:-1.0)
            r30 = 19
            r31 = 1113587712(0x42600000, float:56.0)
            r32 = 0
            r33 = 1090519040(0x41000000, float:8.0)
            r34 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r0.addView(r2, r3)
            org.telegram.ui.Components.VideoTimelineView$TimeHintView r0 = new org.telegram.ui.Components.VideoTimelineView$TimeHintView
            r0.<init>(r8)
            org.telegram.ui.Components.VideoTimelineView r2 = r7.videoTimelineView
            r2.setTimeHintView(r0)
            org.telegram.ui.Components.SizeNotifierFrameLayout r2 = r7.sizeNotifierLayout
            r29 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r30 = 80
            r31 = 0
            r33 = 0
            r34 = 1112539136(0x42500000, float:52.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r2.addView(r0, r3)
            android.view.View r0 = new android.view.View
            r0.<init>(r8)
            r7.recordedAudioBackground = r0
            r2 = 1099956224(0x41900000, float:18.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            java.lang.String r3 = "chat_recordedVoiceBackground"
            int r3 = r7.getThemedColor(r3)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r2, r3)
            r0.setBackgroundDrawable(r2)
            android.widget.FrameLayout r0 = r7.recordedAudioPanel
            android.view.View r2 = r7.recordedAudioBackground
            r29 = 1108344832(0x42100000, float:36.0)
            r30 = 19
            r31 = 1111490560(0x42400000, float:48.0)
            r34 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r0.addView(r2, r3)
            org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView r0 = new org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView
            r0.<init>(r8)
            r7.recordedAudioSeekBar = r0
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r0.setOrientation(r11)
            android.widget.FrameLayout r2 = r7.recordedAudioPanel
            r29 = 1107296256(0x42000000, float:32.0)
            r31 = 1119354880(0x42b80000, float:92.0)
            r33 = 1095761920(0x41500000, float:13.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r2.addView(r0, r3)
            org.telegram.ui.Components.MediaActionDrawable r2 = new org.telegram.ui.Components.MediaActionDrawable
            r2.<init>()
            r7.playPauseDrawable = r2
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r8)
            r7.recordedAudioPlayButton = r2
            android.graphics.Matrix r2 = new android.graphics.Matrix
            r2.<init>()
            r3 = 1061997773(0x3f4ccccd, float:0.8)
            r5 = 1061997773(0x3f4ccccd, float:0.8)
            r6 = 1103101952(0x41CLASSNAME, float:24.0)
            float r6 = org.telegram.messenger.AndroidUtilities.dpf2(r6)
            r9 = 1103101952(0x41CLASSNAME, float:24.0)
            float r9 = org.telegram.messenger.AndroidUtilities.dpf2(r9)
            r2.postScale(r3, r5, r6, r9)
            android.widget.ImageView r3 = r7.recordedAudioPlayButton
            r3.setImageMatrix(r2)
            android.widget.ImageView r2 = r7.recordedAudioPlayButton
            org.telegram.ui.Components.MediaActionDrawable r3 = r7.playPauseDrawable
            r2.setImageDrawable(r3)
            android.widget.ImageView r2 = r7.recordedAudioPlayButton
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.MATRIX
            r2.setScaleType(r3)
            android.widget.ImageView r2 = r7.recordedAudioPlayButton
            r3 = 2131623955(0x7f0e0013, float:1.8875076E38)
            java.lang.String r5 = "AccActionPlay"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r2.setContentDescription(r3)
            android.widget.FrameLayout r2 = r7.recordedAudioPanel
            android.widget.ImageView r3 = r7.recordedAudioPlayButton
            r28 = 48
            r29 = 1111490560(0x42400000, float:48.0)
            r30 = 83
            r31 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r2.addView(r3, r5)
            android.widget.ImageView r2 = r7.recordedAudioPlayButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda14 r3 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda14
            r3.<init>(r7)
            r2.setOnClickListener(r3)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r8)
            r7.recordedAudioTimeTextView = r2
            java.lang.String r3 = "chat_messagePanelVoiceDuration"
            int r3 = r7.getThemedColor(r3)
            r2.setTextColor(r3)
            android.widget.TextView r2 = r7.recordedAudioTimeTextView
            r3 = 1095761920(0x41500000, float:13.0)
            r5 = 1
            r2.setTextSize(r5, r3)
            org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView r2 = r7.recordedAudioSeekBar
            r28 = 0
            r29 = 32
            r30 = 1065353216(0x3var_, float:1.0)
            r31 = 16
            r32 = 0
            r33 = 0
            r34 = 4
            r35 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r28, r29, r30, r31, r32, r33, r34, r35)
            r0.addView(r2, r5)
            android.widget.TextView r2 = r7.recordedAudioTimeTextView
            r5 = 16
            r6 = 0
            r9 = -2
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r9, r6, r5)
            r0.addView(r2, r5)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.recordPanel = r0
            r0.setClipChildren(r11)
            android.widget.FrameLayout r0 = r7.recordPanel
            r2 = 8
            r0.setVisibility(r2)
            android.widget.FrameLayout r0 = r7.recordPanel
            r2 = 1111490560(0x42400000, float:48.0)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r2)
            r4.addView(r0, r9)
            android.widget.FrameLayout r0 = r7.recordPanel
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda30 r2 = org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda30.INSTANCE
            r0.setOnTouchListener(r2)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = new org.telegram.ui.Components.ChatActivityEnterView$SlideTextView
            r0.<init>(r8)
            r7.slideText = r0
            android.widget.FrameLayout r2 = r7.recordPanel
            r28 = -1
            r29 = -1082130432(0xffffffffbvar_, float:-1.0)
            r30 = 0
            r31 = 1110704128(0x42340000, float:45.0)
            r32 = 0
            r33 = 0
            r34 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r2.addView(r0, r5)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r7.recordTimeContainer = r0
            r0.setOrientation(r11)
            android.widget.LinearLayout r0 = r7.recordTimeContainer
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r0.setPadding(r2, r11, r11, r11)
            android.widget.LinearLayout r0 = r7.recordTimeContainer
            r0.setFocusable(r11)
            android.widget.FrameLayout r0 = r7.recordPanel
            android.widget.LinearLayout r2 = r7.recordTimeContainer
            r5 = 16
            r9 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r5)
            r0.addView(r2, r5)
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r0 = new org.telegram.ui.Components.ChatActivityEnterView$RecordDot
            r0.<init>(r8)
            r7.recordDot = r0
            android.widget.LinearLayout r2 = r7.recordTimeContainer
            r28 = 28
            r29 = 28
            r30 = 16
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33, (int) r34)
            r2.addView(r0, r5)
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r0 = new org.telegram.ui.Components.ChatActivityEnterView$TimerView
            r0.<init>(r8)
            r7.recordTimerView = r0
            android.widget.LinearLayout r2 = r7.recordTimeContainer
            r28 = -1
            r29 = -1
            r31 = 6
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33, (int) r34)
            r2.addView(r0, r5)
            org.telegram.ui.Components.ChatActivityEnterView$28 r0 = new org.telegram.ui.Components.ChatActivityEnterView$28
            r0.<init>(r8)
            r7.sendButtonContainer = r0
            r0.setClipChildren(r11)
            android.widget.FrameLayout r0 = r7.sendButtonContainer
            r0.setClipToPadding(r11)
            android.widget.FrameLayout r0 = r7.textFieldContainer
            android.widget.FrameLayout r2 = r7.sendButtonContainer
            r5 = 85
            r9 = 48
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r5)
            r0.addView(r2, r12)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.audioVideoButtonContainer = r0
            r0.setSoundEffectsEnabled(r11)
            android.widget.FrameLayout r0 = r7.sendButtonContainer
            android.widget.FrameLayout r2 = r7.audioVideoButtonContainer
            r5 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r5)
            r0.addView(r2, r12)
            android.widget.FrameLayout r0 = r7.audioVideoButtonContainer
            r2 = 1
            r0.setFocusable(r2)
            android.widget.FrameLayout r0 = r7.audioVideoButtonContainer
            r0.setImportantForAccessibility(r2)
            android.widget.FrameLayout r0 = r7.audioVideoButtonContainer
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda29 r2 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda29
            r2.<init>(r7, r10)
            r0.setOnTouchListener(r2)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = new org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView
            r0.<init>(r8)
            r7.audioVideoSendButton = r0
            r2 = 2131624107(0x7f0e00ab, float:1.8875384E38)
            java.lang.String r5 = "AccDescrVoiceMessage"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r0.setContentDescription(r2)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r7.audioVideoSendButton
            r2 = 1
            r0.setFocusable(r2)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r7.audioVideoSendButton
            r0.setImportantForAccessibility(r2)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r7.audioVideoSendButton
            android.view.View$AccessibilityDelegate r2 = r7.mediaMessageButtonsDelegate
            r0.setAccessibilityDelegate(r2)
            r0 = 1092091904(0x41180000, float:9.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r2 = r7.audioVideoSendButton
            r2.setPadding(r0, r0, r0, r0)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r7.audioVideoSendButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r5 = r7.getThemedColor(r15)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.SRC_IN
            r2.<init>(r5, r9)
            r0.setColorFilter(r2)
            android.widget.FrameLayout r0 = r7.audioVideoButtonContainer
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r2 = r7.audioVideoSendButton
            r5 = 1111490560(0x42400000, float:48.0)
            r9 = 48
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r5)
            r0.addView(r2, r10)
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = new org.telegram.ui.Components.ChatActivityEnterView$RecordCircle
            r0.<init>(r8)
            r7.recordCircle = r0
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r7.sizeNotifierLayout
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r2 = r7.recordCircle
            r29 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r30 = 80
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r0.addView(r2, r5)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.cancelBotButton = r0
            r2 = 4
            r0.setVisibility(r2)
            android.widget.ImageView r0 = r7.cancelBotButton
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER_INSIDE
            r0.setScaleType(r5)
            android.widget.ImageView r0 = r7.cancelBotButton
            org.telegram.ui.Components.ChatActivityEnterView$29 r5 = new org.telegram.ui.Components.ChatActivityEnterView$29
            r5.<init>(r7)
            r7.progressDrawable = r5
            r0.setImageDrawable(r5)
            android.widget.ImageView r0 = r7.cancelBotButton
            r5 = 2131624832(0x7f0e0380, float:1.8876855E38)
            java.lang.String r9 = "Cancel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r0.setContentDescription(r5)
            android.widget.ImageView r0 = r7.cancelBotButton
            r0.setSoundEffectsEnabled(r11)
            android.widget.ImageView r0 = r7.cancelBotButton
            r5 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r5)
            android.widget.ImageView r0 = r7.cancelBotButton
            r0.setScaleY(r5)
            android.widget.ImageView r0 = r7.cancelBotButton
            r0.setAlpha(r6)
            r0 = 21
            if (r13 < r0) goto L_0x0a5f
            android.widget.ImageView r0 = r7.cancelBotButton
            int r5 = r7.getThemedColor(r1)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5)
            r0.setBackgroundDrawable(r5)
        L_0x0a5f:
            android.widget.FrameLayout r0 = r7.sendButtonContainer
            android.widget.ImageView r5 = r7.cancelBotButton
            r9 = 1111490560(0x42400000, float:48.0)
            r10 = 48
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r9)
            r0.addView(r5, r12)
            android.widget.ImageView r0 = r7.cancelBotButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda24 r5 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda24
            r5.<init>(r7)
            r0.setOnClickListener(r5)
            boolean r0 = r36.isInScheduleMode()
            if (r0 == 0) goto L_0x0aac
            android.content.res.Resources r0 = r37.getResources()
            r5 = 2131165542(0x7var_, float:1.7945304E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r5)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.sendButtonDrawable = r0
            android.content.res.Resources r0 = r37.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r5)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.sendButtonInverseDrawable = r0
            android.content.res.Resources r0 = r37.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r5)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.inactinveSendButtonDrawable = r0
            goto L_0x0ad9
        L_0x0aac:
            android.content.res.Resources r0 = r37.getResources()
            r5 = 2131165498(0x7var_a, float:1.7945215E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r5)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.sendButtonDrawable = r0
            android.content.res.Resources r0 = r37.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r5)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.sendButtonInverseDrawable = r0
            android.content.res.Resources r0 = r37.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r5)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.inactinveSendButtonDrawable = r0
        L_0x0ad9:
            org.telegram.ui.Components.ChatActivityEnterView$30 r0 = new org.telegram.ui.Components.ChatActivityEnterView$30
            r0.<init>(r8)
            r7.sendButton = r0
            r0.setVisibility(r2)
            java.lang.String r0 = "chat_messagePanelSend"
            int r0 = r7.getThemedColor(r0)
            android.view.View r5 = r7.sendButton
            r9 = 2131628237(0x7f0e10cd, float:1.888376E38)
            java.lang.String r10 = "Send"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setContentDescription(r9)
            android.view.View r5 = r7.sendButton
            r5.setSoundEffectsEnabled(r11)
            android.view.View r5 = r7.sendButton
            r9 = 1036831949(0x3dcccccd, float:0.1)
            r5.setScaleX(r9)
            android.view.View r5 = r7.sendButton
            r5.setScaleY(r9)
            android.view.View r5 = r7.sendButton
            r5.setAlpha(r6)
            r5 = 21
            if (r13 < r5) goto L_0x0b2e
            android.view.View r5 = r7.sendButton
            r9 = 24
            int r10 = android.graphics.Color.red(r0)
            int r12 = android.graphics.Color.green(r0)
            int r0 = android.graphics.Color.blue(r0)
            int r0 = android.graphics.Color.argb(r9, r10, r12, r0)
            r9 = 1
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r0, r9)
            r5.setBackgroundDrawable(r0)
        L_0x0b2e:
            android.widget.FrameLayout r0 = r7.sendButtonContainer
            android.view.View r5 = r7.sendButton
            r9 = 1111490560(0x42400000, float:48.0)
            r10 = 48
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r9)
            r0.addView(r5, r12)
            android.view.View r0 = r7.sendButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda23 r5 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda23
            r5.<init>(r7)
            r0.setOnClickListener(r5)
            android.view.View r0 = r7.sendButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda27 r5 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda27
            r5.<init>(r7)
            r0.setOnLongClickListener(r5)
            org.telegram.ui.ActionBar.SimpleTextView r0 = new org.telegram.ui.ActionBar.SimpleTextView
            r0.<init>(r8)
            r7.slowModeButton = r0
            r5 = 18
            r0.setTextSize(r5)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r7.slowModeButton
            r0.setVisibility(r2)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r7.slowModeButton
            r0.setSoundEffectsEnabled(r11)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r7.slowModeButton
            r5 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r5)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r7.slowModeButton
            r0.setScaleY(r5)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r7.slowModeButton
            r0.setAlpha(r6)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r7.slowModeButton
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r0.setPadding(r11, r11, r3, r11)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r7.slowModeButton
            r3 = 21
            r0.setGravity(r3)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r7.slowModeButton
            int r3 = r7.getThemedColor(r15)
            r0.setTextColor(r3)
            android.widget.FrameLayout r0 = r7.sendButtonContainer
            org.telegram.ui.ActionBar.SimpleTextView r3 = r7.slowModeButton
            r5 = 64
            r9 = 53
            r10 = 48
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r10, (int) r9)
            r0.addView(r3, r5)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r7.slowModeButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda20 r3 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda20
            r3.<init>(r7)
            r0.setOnClickListener(r3)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r7.slowModeButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda28 r3 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda28
            r3.<init>(r7)
            r0.setOnLongClickListener(r3)
            org.telegram.ui.Components.ChatActivityEnterView$31 r0 = new org.telegram.ui.Components.ChatActivityEnterView$31
            r0.<init>(r7, r8)
            r7.expandStickersButton = r0
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r3)
            android.widget.ImageView r0 = r7.expandStickersButton
            org.telegram.ui.Components.AnimatedArrowDrawable r3 = new org.telegram.ui.Components.AnimatedArrowDrawable
            int r5 = r7.getThemedColor(r15)
            r3.<init>(r5, r11)
            r7.stickersArrow = r3
            r0.setImageDrawable(r3)
            android.widget.ImageView r0 = r7.expandStickersButton
            r3 = 8
            r0.setVisibility(r3)
            android.widget.ImageView r0 = r7.expandStickersButton
            r3 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r3)
            android.widget.ImageView r0 = r7.expandStickersButton
            r0.setScaleY(r3)
            android.widget.ImageView r0 = r7.expandStickersButton
            r0.setAlpha(r6)
            r0 = 21
            if (r13 < r0) goto L_0x0bfd
            android.widget.ImageView r0 = r7.expandStickersButton
            int r1 = r7.getThemedColor(r1)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1)
            r0.setBackgroundDrawable(r1)
        L_0x0bfd:
            android.widget.FrameLayout r0 = r7.sendButtonContainer
            android.widget.ImageView r1 = r7.expandStickersButton
            r3 = 1111490560(0x42400000, float:48.0)
            r5 = 48
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3)
            r0.addView(r1, r6)
            android.widget.ImageView r0 = r7.expandStickersButton
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda19 r1 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda19
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            android.widget.ImageView r0 = r7.expandStickersButton
            r1 = 2131623983(0x7f0e002f, float:1.8875133E38)
            java.lang.String r3 = "AccDescrExpandPanel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.doneButtonContainer = r0
            r1 = 8
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r7.textFieldContainer
            android.widget.FrameLayout r1 = r7.doneButtonContainer
            r3 = 85
            r5 = 48
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r3)
            r0.addView(r1, r3)
            android.widget.FrameLayout r0 = r7.doneButtonContainer
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda15 r1 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda15
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            r0 = 1098907648(0x41800000, float:16.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            java.lang.String r1 = "chat_messagePanelSend"
            int r1 = r7.getThemedColor(r1)
            android.graphics.drawable.ShapeDrawable r0 = org.telegram.ui.ActionBar.Theme.createCircleDrawable(r0, r1)
            android.content.res.Resources r1 = r37.getResources()
            r3 = 2131165533(0x7var_d, float:1.7945286E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r3)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            r7.doneCheckDrawable = r1
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r5 = "chat_messagePanelVoicePressed"
            int r5 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r5, r6)
            r1.setColorFilter(r3)
            org.telegram.ui.Components.CombinedDrawable r3 = new org.telegram.ui.Components.CombinedDrawable
            r5 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r3.<init>(r0, r1, r11, r5)
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1 = 1107296256(0x42000000, float:32.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3.setCustomSize(r0, r1)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.doneButtonImage = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            android.widget.ImageView r0 = r7.doneButtonImage
            r0.setImageDrawable(r3)
            android.widget.ImageView r0 = r7.doneButtonImage
            r1 = 2131625541(0x7f0e0645, float:1.8878293E38)
            java.lang.String r3 = "Done"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
            android.widget.FrameLayout r0 = r7.doneButtonContainer
            android.widget.ImageView r1 = r7.doneButtonImage
            r3 = 1111490560(0x42400000, float:48.0)
            r5 = 48
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3)
            r0.addView(r1, r3)
            org.telegram.ui.Components.ContextProgressView r0 = new org.telegram.ui.Components.ContextProgressView
            r0.<init>(r8, r11)
            r7.doneButtonProgress = r0
            r0.setVisibility(r2)
            android.widget.FrameLayout r0 = r7.doneButtonContainer
            org.telegram.ui.Components.ContextProgressView r1 = r7.doneButtonProgress
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
            r1 = 1128792064(0x43480000, float:200.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            java.lang.String r2 = "kbd_height"
            int r1 = r0.getInt(r2, r1)
            r7.keyboardHeight = r1
            r1 = 1128792064(0x43480000, float:200.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            java.lang.String r2 = "kbd_height_land3"
            int r0 = r0.getInt(r2, r1)
            r7.keyboardHeightLand = r0
            r7.setRecordVideoButtonVisible(r11, r11)
            r7.checkSendButton(r11)
            r36.checkChannelRights()
            org.telegram.ui.Components.ChatActivityBotWebViewButton r0 = new org.telegram.ui.Components.ChatActivityBotWebViewButton
            r0.<init>(r8)
            r7.botWebViewButton = r0
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Components.ChatActivityBotWebViewButton r0 = r7.botWebViewButton
            org.telegram.ui.Components.BotCommandsMenuView r1 = r7.botCommandsMenuButton
            r0.setBotMenuButton(r1)
            org.telegram.ui.Components.ChatActivityBotWebViewButton r0 = r7.botWebViewButton
            r1 = 80
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r1)
            r4.addView(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.<init>(android.app.Activity, org.telegram.ui.Components.SizeNotifierFrameLayout, org.telegram.ui.ChatActivity, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
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
                AndroidUtilities.runOnUIThread(new ChatActivityEnterView$$ExternalSyntheticLambda37(this), 200);
                return;
            }
            openKeyboardInternal();
            return;
        }
        BotWebViewMenuContainer botWebViewMenuContainer2 = this.botWebViewMenuContainer;
        view.getClass();
        botWebViewMenuContainer2.dismiss(new ChatActivityEnterView$$ExternalSyntheticLambda33(view));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.waitingForKeyboardOpenAfterAnimation = false;
        openKeyboardInternal();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        this.messageEditText.invalidateEffects();
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onTextSpansChanged(this.messageEditText.getText());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.openScheduledMessages();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        boolean z = !this.botCommandsMenuButton.isOpened();
        this.botCommandsMenuButton.setOpened(z);
        try {
            performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        if (hasBotWebView()) {
            if (!z) {
                this.botWebViewMenuContainer.dismiss();
            } else if (this.emojiViewVisible || this.botKeyboardViewVisible) {
                AndroidUtilities.runOnUIThread(new ChatActivityEnterView$$ExternalSyntheticLambda42(this), 275);
                hidePopup(false);
            } else {
                openWebViewMenu();
            }
        } else if (z) {
            this.botCommandsMenuContainer.show();
        } else {
            this.botCommandsMenuContainer.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5() {
        if (this.botButtonsMessageObject != null && TextUtils.isEmpty(this.messageEditText.getText()) && !this.botWebViewMenuContainer.hasSavedText()) {
            showPopup(1, 1);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(View view) {
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
        view.getClass();
        botWebViewMenuContainer2.dismiss(new ChatActivityEnterView$$ExternalSyntheticLambda33(view));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(View view) {
        AdjustPanLayoutHelper adjustPanLayoutHelper2 = this.adjustPanLayoutHelper;
        if (adjustPanLayoutHelper2 == null || !adjustPanLayoutHelper2.animationInProgress()) {
            this.delegate.didPressAttachButton();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$14(Activity activity, View view) {
        int i;
        int i2;
        View view2 = view;
        if (getTranslationY() != 0.0f) {
            this.onEmojiSearchClosed = new ChatActivityEnterView$$ExternalSyntheticLambda40(this);
            hidePopup(true, true);
            return;
        }
        if (this.delegate.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            int contentViewHeight = this.delegate.getContentViewHeight();
            int measureKeyboardHeight = this.delegate.measureKeyboardHeight();
            if (measureKeyboardHeight <= AndroidUtilities.dp(20.0f)) {
                contentViewHeight += measureKeyboardHeight;
            }
            if (this.emojiViewVisible) {
                contentViewHeight -= getEmojiPadding();
            }
            if (contentViewHeight < AndroidUtilities.dp(200.0f)) {
                this.onKeyboardClosed = new ChatActivityEnterView$$ExternalSyntheticLambda41(this);
                closeKeyboard();
                return;
            }
        }
        if (this.delegate.getSendAsPeers() != null) {
            try {
                view2.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
            int i3 = 0;
            if (senderSelectPopup != null) {
                senderSelectPopup.setPauseNotifications(false);
                this.senderSelectPopupWindow.startDismissAnimation(new SpringAnimation[0]);
                return;
            }
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            TLRPC$ChatFull chatFull = instance.getChatFull(-this.dialog_id);
            if (chatFull != null) {
                ActionBarLayout parentLayout = this.parentFragment.getParentLayout();
                AnonymousClass21 r12 = r0;
                final ActionBarLayout actionBarLayout = parentLayout;
                AnonymousClass21 r0 = new SenderSelectPopup(activity, this.parentFragment, instance, chatFull, this.delegate.getSendAsPeers(), new ChatActivityEnterView$$ExternalSyntheticLambda59(this, chatFull, instance)) {
                    public void dismiss() {
                        if (ChatActivityEnterView.this.senderSelectPopupWindow != this) {
                            actionBarLayout.removeView(this.dimView);
                            super.dismiss();
                            return;
                        }
                        SenderSelectPopup unused = ChatActivityEnterView.this.senderSelectPopupWindow = null;
                        if (!this.runningCustomSprings) {
                            startDismissAnimation(new SpringAnimation[0]);
                            ChatActivityEnterView.this.senderSelectView.setProgress(0.0f, true, true);
                            return;
                        }
                        for (SpringAnimation cancel : this.springAnimations) {
                            cancel.cancel();
                        }
                        this.springAnimations.clear();
                        super.dismiss();
                    }
                };
                this.senderSelectPopupWindow = r12;
                r12.setPauseNotifications(true);
                this.senderSelectPopupWindow.setDismissAnimationDuration(220);
                this.senderSelectPopupWindow.setOutsideTouchable(true);
                this.senderSelectPopupWindow.setClippingEnabled(true);
                this.senderSelectPopupWindow.setFocusable(true);
                this.senderSelectPopupWindow.getContentView().measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                this.senderSelectPopupWindow.setInputMethodMode(2);
                this.senderSelectPopupWindow.setSoftInputMode(0);
                this.senderSelectPopupWindow.getContentView().setFocusableInTouchMode(true);
                this.senderSelectPopupWindow.setAnimationEnabled(false);
                int i4 = -AndroidUtilities.dp(4.0f);
                int[] iArr = new int[2];
                if (AndroidUtilities.isTablet()) {
                    this.parentFragment.getFragmentView().getLocationInWindow(iArr);
                    i = iArr[0] + i4;
                } else {
                    i = i4;
                }
                int contentViewHeight2 = this.delegate.getContentViewHeight();
                int measuredHeight = this.senderSelectPopupWindow.getContentView().getMeasuredHeight();
                int measureKeyboardHeight2 = this.delegate.measureKeyboardHeight();
                if (measureKeyboardHeight2 <= AndroidUtilities.dp(20.0f)) {
                    contentViewHeight2 += measureKeyboardHeight2;
                }
                if (this.emojiViewVisible) {
                    contentViewHeight2 -= getEmojiPadding();
                }
                int dp = AndroidUtilities.dp(1.0f);
                if (measuredHeight < (((i4 * 2) + contentViewHeight2) - (this.parentFragment.isInBubbleMode() ? 0 : AndroidUtilities.statusBarHeight)) - this.senderSelectPopupWindow.headerText.getMeasuredHeight()) {
                    getLocationInWindow(iArr);
                    i2 = ((iArr[1] - measuredHeight) - i4) - AndroidUtilities.dp(2.0f);
                    parentLayout.addView(this.senderSelectPopupWindow.dimView, new FrameLayout.LayoutParams(-1, i4 + i2 + measuredHeight + dp + AndroidUtilities.dp(2.0f)));
                } else {
                    if (!this.parentFragment.isInBubbleMode()) {
                        i3 = AndroidUtilities.statusBarHeight;
                    }
                    int dp2 = AndroidUtilities.dp(14.0f);
                    this.senderSelectPopupWindow.recyclerContainer.getLayoutParams().height = ((contentViewHeight2 - i3) - dp2) - getHeightWithTopView();
                    parentLayout.addView(this.senderSelectPopupWindow.dimView, new FrameLayout.LayoutParams(-1, dp2 + i3 + this.senderSelectPopupWindow.recyclerContainer.getLayoutParams().height + dp));
                    i2 = i3;
                }
                this.senderSelectPopupWindow.startShowAnimation();
                SenderSelectPopup senderSelectPopup2 = this.senderSelectPopupWindow;
                this.popupX = i;
                this.popupY = i2;
                senderSelectPopup2.showAtLocation(view2, 51, i, i2);
                this.senderSelectView.setProgress(1.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8() {
        this.senderSelectView.callOnClick();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9() {
        this.senderSelectView.callOnClick();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$13(TLRPC$ChatFull tLRPC$ChatFull, MessagesController messagesController, RecyclerView recyclerView, SenderSelectPopup.SenderView senderView, TLRPC$Peer tLRPC$Peer) {
        TLRPC$User user;
        if (this.senderSelectPopupWindow != null) {
            if (tLRPC$ChatFull != null) {
                tLRPC$ChatFull.default_send_as = tLRPC$Peer;
                updateSendAsButton();
            }
            MessagesController messagesController2 = this.parentFragment.getMessagesController();
            long j = this.dialog_id;
            long j2 = tLRPC$Peer.user_id;
            long j3 = 0;
            if (j2 == 0) {
                j2 = -tLRPC$Peer.channel_id;
            }
            messagesController2.setDefaultSendAs(j, j2);
            int[] iArr = new int[2];
            boolean isSelected = senderView.avatar.isSelected();
            senderView.avatar.getLocationInWindow(iArr);
            senderView.avatar.setSelected(true, true);
            SimpleAvatarView simpleAvatarView = new SimpleAvatarView(getContext());
            long j4 = tLRPC$Peer.channel_id;
            if (j4 != 0) {
                TLRPC$Chat chat = messagesController.getChat(Long.valueOf(j4));
                if (chat != null) {
                    simpleAvatarView.setAvatar(chat);
                }
            } else {
                long j5 = tLRPC$Peer.user_id;
                if (!(j5 == 0 || (user = messagesController.getUser(Long.valueOf(j5))) == null)) {
                    simpleAvatarView.setAvatar(user);
                }
            }
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View childAt = recyclerView.getChildAt(i);
                if ((childAt instanceof SenderSelectPopup.SenderView) && childAt != senderView) {
                    ((SenderSelectPopup.SenderView) childAt).avatar.setSelected(false, true);
                }
            }
            ChatActivityEnterView$$ExternalSyntheticLambda49 chatActivityEnterView$$ExternalSyntheticLambda49 = new ChatActivityEnterView$$ExternalSyntheticLambda49(this, simpleAvatarView, iArr, senderView);
            if (!isSelected) {
                j3 = 200;
            }
            AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda49, j3);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$12(SimpleAvatarView simpleAvatarView, int[] iArr, SenderSelectPopup.SenderView senderView) {
        final SimpleAvatarView simpleAvatarView2 = simpleAvatarView;
        if (this.senderSelectPopupWindow != null) {
            Dialog dialog = new Dialog(getContext(), NUM);
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.addView(simpleAvatarView2, LayoutHelper.createFrame(40, 40, 3));
            dialog.setContentView(frameLayout);
            dialog.getWindow().setLayout(-1, -1);
            int i = Build.VERSION.SDK_INT;
            if (i >= 21) {
                dialog.getWindow().clearFlags(1024);
                dialog.getWindow().clearFlags(67108864);
                dialog.getWindow().clearFlags(NUM);
                dialog.getWindow().addFlags(Integer.MIN_VALUE);
                dialog.getWindow().addFlags(512);
                dialog.getWindow().addFlags(131072);
                dialog.getWindow().getAttributes().windowAnimations = 0;
                dialog.getWindow().getDecorView().setSystemUiVisibility(1792);
                dialog.getWindow().setStatusBarColor(0);
                dialog.getWindow().setNavigationBarColor(0);
                AndroidUtilities.setLightStatusBar(dialog.getWindow(), Theme.getColor("actionBarDefault", (boolean[]) null, true) == -1);
                if (i >= 26) {
                    AndroidUtilities.setLightNavigationBar(dialog.getWindow(), AndroidUtilities.computePerceivedBrightness(Theme.getColor("windowBackgroundGray", (boolean[]) null, true)) >= 0.721f);
                }
            }
            if (i >= 23) {
                this.popupX += getRootWindowInsets().getSystemWindowInsetLeft();
            }
            this.senderSelectView.getLocationInWindow(this.location);
            int[] iArr2 = this.location;
            float f = (float) iArr2[0];
            final float f2 = (float) iArr2[1];
            float dp = (float) AndroidUtilities.dp(5.0f);
            float dp2 = ((float) (iArr[0] + this.popupX)) + dp + ((float) AndroidUtilities.dp(4.0f)) + 0.0f;
            float f3 = ((float) (iArr[1] + this.popupY)) + dp + 0.0f;
            simpleAvatarView2.setTranslationX(dp2);
            simpleAvatarView2.setTranslationY(f3);
            float dp3 = ((float) this.senderSelectView.getLayoutParams().width) / ((float) AndroidUtilities.dp(40.0f));
            simpleAvatarView2.setPivotX(0.0f);
            simpleAvatarView2.setPivotY(0.0f);
            simpleAvatarView2.setScaleX(0.75f);
            simpleAvatarView2.setScaleY(0.75f);
            final SenderSelectPopup.SenderView senderView2 = senderView;
            simpleAvatarView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener(this) {
                public void onDraw() {
                    SimpleAvatarView simpleAvatarView = simpleAvatarView2;
                    simpleAvatarView.post(new ChatActivityEnterView$22$$ExternalSyntheticLambda0(this, simpleAvatarView, senderView2));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onDraw$0(SimpleAvatarView simpleAvatarView, SenderSelectPopup.SenderView senderView) {
                    simpleAvatarView.getViewTreeObserver().removeOnDrawListener(this);
                    senderView.avatar.setHideAvatar(true);
                }
            });
            dialog.show();
            this.senderSelectView.setScaleX(1.0f);
            this.senderSelectView.setScaleY(1.0f);
            this.senderSelectView.setAlpha(1.0f);
            SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
            SpringAnimation[] springAnimationArr = new SpringAnimation[7];
            SenderSelectView senderSelectView2 = this.senderSelectView;
            DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.SCALE_X;
            springAnimationArr[0] = new SpringAnimation(senderSelectView2, viewProperty).setSpring(new SpringForce(0.5f).setStiffness(750.0f).setDampingRatio(1.0f));
            SenderSelectView senderSelectView3 = this.senderSelectView;
            DynamicAnimation.ViewProperty viewProperty2 = DynamicAnimation.SCALE_Y;
            springAnimationArr[1] = new SpringAnimation(senderSelectView3, viewProperty2).setSpring(new SpringForce(0.5f).setStiffness(750.0f).setDampingRatio(1.0f));
            SpringAnimation spring = new SpringAnimation(this.senderSelectView, DynamicAnimation.ALPHA).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f));
            DynamicAnimation.ViewProperty viewProperty3 = viewProperty2;
            SpringAnimation[] springAnimationArr2 = springAnimationArr;
            float f4 = dp3;
            SenderSelectPopup senderSelectPopup2 = senderSelectPopup;
            ChatActivityEnterView$$ExternalSyntheticLambda32 chatActivityEnterView$$ExternalSyntheticLambda32 = r0;
            float f5 = f;
            float f6 = f2;
            ChatActivityEnterView$$ExternalSyntheticLambda32 chatActivityEnterView$$ExternalSyntheticLambda322 = new ChatActivityEnterView$$ExternalSyntheticLambda32(this, dialog, simpleAvatarView, f5, f6);
            springAnimationArr2[2] = (SpringAnimation) spring.addEndListener(chatActivityEnterView$$ExternalSyntheticLambda32);
            springAnimationArr2[3] = (SpringAnimation) ((SpringAnimation) new SpringAnimation(simpleAvatarView2, DynamicAnimation.TRANSLATION_X).setStartValue(MathUtils.clamp(dp2, f - ((float) AndroidUtilities.dp(6.0f)), dp2))).setSpring(new SpringForce(f).setStiffness(700.0f).setDampingRatio(0.75f)).setMinValue(f - ((float) AndroidUtilities.dp(6.0f)));
            springAnimationArr2[4] = (SpringAnimation) ((SpringAnimation) ((SpringAnimation) ((SpringAnimation) new SpringAnimation(simpleAvatarView2, DynamicAnimation.TRANSLATION_Y).setStartValue(MathUtils.clamp(f3, f3, ((float) AndroidUtilities.dp(6.0f)) + f2))).setSpring(new SpringForce(f2).setStiffness(700.0f).setDampingRatio(0.75f)).setMaxValue(((float) AndroidUtilities.dp(6.0f)) + f2)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener(this) {
                boolean performedHapticFeedback = false;

                public void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    if (!this.performedHapticFeedback && f >= f2) {
                        this.performedHapticFeedback = true;
                        try {
                            simpleAvatarView2.performHapticFeedback(3, 2);
                        } catch (Exception unused) {
                        }
                    }
                }
            })).addEndListener(new ChatActivityEnterView$$ExternalSyntheticLambda31(this, dialog, simpleAvatarView, f5, f6));
            float f7 = f4;
            springAnimationArr2[5] = new SpringAnimation(simpleAvatarView2, viewProperty).setSpring(new SpringForce(f7).setStiffness(1000.0f).setDampingRatio(1.0f));
            springAnimationArr2[6] = new SpringAnimation(simpleAvatarView2, viewProperty3).setSpring(new SpringForce(f7).setStiffness(1000.0f).setDampingRatio(1.0f));
            senderSelectPopup2.startDismissAnimation(springAnimationArr2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$10(final Dialog dialog, SimpleAvatarView simpleAvatarView, float f, float f2, DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
        if (dialog.isShowing()) {
            simpleAvatarView.setTranslationX(f);
            simpleAvatarView.setTranslationY(f2);
            this.senderSelectView.setProgress(0.0f, false);
            this.senderSelectView.setScaleX(1.0f);
            this.senderSelectView.setScaleY(1.0f);
            this.senderSelectView.setAlpha(1.0f);
            this.senderSelectView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    ChatActivityEnterView.this.senderSelectView.getViewTreeObserver().removeOnPreDrawListener(this);
                    SenderSelectView access$12000 = ChatActivityEnterView.this.senderSelectView;
                    Dialog dialog = dialog;
                    dialog.getClass();
                    access$12000.postDelayed(new ChatActivityEnterView$23$$ExternalSyntheticLambda0(dialog), 100);
                    return true;
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$11(final Dialog dialog, SimpleAvatarView simpleAvatarView, float f, float f2, DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
        if (dialog.isShowing()) {
            simpleAvatarView.setTranslationX(f);
            simpleAvatarView.setTranslationY(f2);
            this.senderSelectView.setProgress(0.0f, false);
            this.senderSelectView.setScaleX(1.0f);
            this.senderSelectView.setScaleY(1.0f);
            this.senderSelectView.setAlpha(1.0f);
            this.senderSelectView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    ChatActivityEnterView.this.senderSelectView.getViewTreeObserver().removeOnPreDrawListener(this);
                    SenderSelectView access$12000 = ChatActivityEnterView.this.senderSelectView;
                    Dialog dialog = dialog;
                    dialog.getClass();
                    access$12000.postDelayed(new ChatActivityEnterView$23$$ExternalSyntheticLambda0(dialog), 100);
                    return true;
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$15(View view) {
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
    public /* synthetic */ void lambda$new$16(View view) {
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
    public /* synthetic */ boolean lambda$new$24(Theme.ResourcesProvider resourcesProvider2, View view, MotionEvent motionEvent) {
        int i = 3;
        if (motionEvent.getAction() != 0) {
            float f = 1.0f;
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (motionEvent.getAction() == 3 && this.recordingAudioVideo) {
                    if (this.recordCircle.slideToCancelProgress < 0.7f) {
                        if (!this.hasRecordVideo || !isInVideoMode()) {
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
                        if (!this.hasRecordVideo || !isInVideoMode()) {
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
                        this.delegate.onSwitchRecordMode(!isInVideoMode());
                        setRecordVideoButtonVisible(!isInVideoMode(), true);
                        performHapticFeedback(3);
                        sendAccessibilityEvent(1);
                    } else {
                        boolean z = this.hasRecordVideo;
                        if (!z || this.calledRecordRunnable) {
                            this.startedDraggingX = -1.0f;
                            if (!z || !isInVideoMode()) {
                                if (this.recordingAudioVideo && isInScheduleMode()) {
                                    AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) ChatActivityEnterView$$ExternalSyntheticLambda56.INSTANCE, (Runnable) ChatActivityEnterView$$ExternalSyntheticLambda50.INSTANCE, resourcesProvider2);
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
                            this.messageTransitionIsRunning = false;
                            ChatActivityEnterView$$ExternalSyntheticLambda43 chatActivityEnterView$$ExternalSyntheticLambda43 = new ChatActivityEnterView$$ExternalSyntheticLambda43(this);
                            this.moveToSendStateRunnable = chatActivityEnterView$$ExternalSyntheticLambda43;
                            AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda43, 500);
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
                    if (!this.hasRecordVideo || !isInVideoMode()) {
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
        } else if (this.recordCircle.isSendButtonVisible()) {
            boolean z2 = this.hasRecordVideo;
            if (!z2 || this.calledRecordRunnable) {
                this.startedDraggingX = -1.0f;
                if (!z2 || !isInVideoMode()) {
                    if (this.recordingAudioVideo && isInScheduleMode()) {
                        AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) ChatActivityEnterView$$ExternalSyntheticLambda55.INSTANCE, (Runnable) ChatActivityEnterView$$ExternalSyntheticLambda51.INSTANCE, resourcesProvider2);
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
                this.messageTransitionIsRunning = false;
                ChatActivityEnterView$$ExternalSyntheticLambda36 chatActivityEnterView$$ExternalSyntheticLambda36 = new ChatActivityEnterView$$ExternalSyntheticLambda36(this);
                this.moveToSendStateRunnable = chatActivityEnterView$$ExternalSyntheticLambda36;
                AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda36, 200);
            }
            return false;
        } else {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null) {
                TLRPC$Chat currentChat = chatActivity.getCurrentChat();
                TLRPC$UserFull currentUserInfo = this.parentFragment.getCurrentUserInfo();
                if ((currentChat != null && !ChatObject.canSendMedia(currentChat)) || (currentUserInfo != null && currentUserInfo.voice_messages_forbidden)) {
                    this.delegate.needShowMediaBanHint();
                    return true;
                }
            }
            if (this.hasRecordVideo) {
                this.calledRecordRunnable = false;
                this.recordAudioVideoRunnableStarted = true;
                AndroidUtilities.runOnUIThread(this.recordAudioVideoRunnable, 150);
            } else {
                this.recordAudioVideoRunnable.run();
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$20() {
        this.moveToSendStateRunnable = null;
        updateRecordInterface(1);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$23() {
        this.moveToSendStateRunnable = null;
        updateRecordInterface(1);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$25(View view) {
        String obj = this.messageEditText.getText().toString();
        int indexOf = obj.indexOf(32);
        if (indexOf == -1 || indexOf == obj.length() - 1) {
            setFieldText("");
        } else {
            setFieldText(obj.substring(0, indexOf + 1));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$26(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            AnimatorSet animatorSet = this.runningAnimationAudio;
            if ((animatorSet == null || !animatorSet.isRunning()) && this.moveToSendStateRunnable == null) {
                sendMessage();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$27(View view) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            SimpleTextView simpleTextView = this.slowModeButton;
            chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$28(View view) {
        if (this.messageEditText.length() == 0) {
            return false;
        }
        return onSendLongClick(view);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$29(View view) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$30(View view) {
        doneEditingMessage();
    }

    /* access modifiers changed from: private */
    public void openWebViewMenu() {
        ChatActivityEnterView$$ExternalSyntheticLambda35 chatActivityEnterView$$ExternalSyntheticLambda35 = new ChatActivityEnterView$$ExternalSyntheticLambda35(this);
        if (SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, this.dialog_id)) {
            chatActivityEnterView$$ExternalSyntheticLambda35.run();
            return;
        }
        new AlertDialog.Builder((Context) this.parentFragment.getParentActivity()).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(NUM, UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.dialog_id)))))).setPositiveButton(LocaleController.getString(NUM), new ChatActivityEnterView$$ExternalSyntheticLambda8(this, chatActivityEnterView$$ExternalSyntheticLambda35)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setOnDismissListener(new ChatActivityEnterView$$ExternalSyntheticLambda11(this)).show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openWebViewMenu$31() {
        AndroidUtilities.hideKeyboard(this);
        if (AndroidUtilities.isTablet()) {
            BotWebViewSheet botWebViewSheet = new BotWebViewSheet(getContext(), this.parentFragment.getResourceProvider());
            botWebViewSheet.setParentActivity(this.parentActivity);
            int i = this.currentAccount;
            long j = this.dialog_id;
            botWebViewSheet.requestWebView(i, j, j, this.botMenuWebViewTitle, this.botMenuWebViewUrl, 2, 0, false);
            botWebViewSheet.show();
            this.botCommandsMenuButton.setOpened(false);
            return;
        }
        this.botWebViewMenuContainer.show(this.currentAccount, this.dialog_id, this.botMenuWebViewUrl);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openWebViewMenu$32(Runnable runnable, DialogInterface dialogInterface, int i) {
        runnable.run();
        SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, this.dialog_id, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openWebViewMenu$33(DialogInterface dialogInterface) {
        if (!SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, this.dialog_id)) {
            this.botCommandsMenuButton.setOpened(false);
        }
    }

    public void setBotWebViewButtonOffsetX(float f) {
        this.emojiButton.setTranslationX(f);
        this.messageEditText.setTranslationX(f);
        this.attachButton.setTranslationX(f);
        this.audioVideoSendButton.setTranslationX(f);
        ImageView imageView = this.botButton;
        if (imageView != null) {
            imageView.setTranslationX(f);
        }
    }

    public void setComposeShadowAlpha(float f) {
        this.composeShadowAlpha = f;
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
            boolean z = botCommandsMenuView.expanded;
            botCommandsMenuView.setExpanded(TextUtils.isEmpty(this.messageEditText.getText()) && !this.keyboardVisible && !this.waitingForKeyboardOpen && !isPopupShowing(), true);
            if (z != this.botCommandsMenuButton.expanded) {
                beginDelayedTransition();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000a, code lost:
        r1 = r0.parentFragment;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void forceSmoothKeyboard(boolean r1) {
        /*
            r0 = this;
            if (r1 == 0) goto L_0x0016
            boolean r1 = org.telegram.messenger.SharedConfig.smoothKeyboard
            if (r1 == 0) goto L_0x0016
            boolean r1 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
            if (r1 != 0) goto L_0x0016
            org.telegram.ui.ChatActivity r1 = r0.parentFragment
            if (r1 == 0) goto L_0x0014
            boolean r1 = r1.isInBubbleMode()
            if (r1 != 0) goto L_0x0016
        L_0x0014:
            r1 = 1
            goto L_0x0017
        L_0x0016:
            r1 = 0
        L_0x0017:
            r0.smoothKeyboard = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.forceSmoothKeyboard(boolean):void");
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
                int dp = (int) (((float) (this.animatedTop + AndroidUtilities.dp(2.0f))) + this.chatSearchExpandOffset);
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
        int intrinsicHeight = (int) (((float) this.animatedTop) + (((float) Theme.chat_composeShadowDrawable.getIntrinsicHeight()) * (1.0f - this.composeShadowAlpha)));
        View view = this.topView;
        if (view != null && view.getVisibility() == 0) {
            intrinsicHeight = (int) (((float) intrinsicHeight) + ((1.0f - this.topViewEnterProgress) * ((float) this.topView.getLayoutParams().height)));
        }
        int intrinsicHeight2 = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + intrinsicHeight;
        Theme.chat_composeShadowDrawable.setAlpha((int) (this.composeShadowAlpha * 255.0f));
        Theme.chat_composeShadowDrawable.setBounds(0, intrinsicHeight, getMeasuredWidth(), intrinsicHeight2);
        Theme.chat_composeShadowDrawable.draw(canvas);
        int i = (int) (((float) intrinsicHeight2) + this.chatSearchExpandOffset);
        if (this.allowBlur) {
            this.backgroundPaint.setColor(getThemedColor("chat_messagePanelBackground"));
            if (!SharedConfig.chatBlurEnabled() || this.sizeNotifierLayout == null) {
                canvas.drawRect(0.0f, (float) i, (float) getWidth(), (float) getHeight(), this.backgroundPaint);
                return;
            }
            Rect rect2 = AndroidUtilities.rectTmp2;
            rect2.set(0, i, getWidth(), getHeight());
            this.sizeNotifierLayout.drawBlurRect(canvas, (float) getTop(), rect2, this.backgroundPaint, false);
            return;
        }
        canvas.drawRect(0.0f, (float) i, (float) getWidth(), (float) getHeight(), getThemedPaint("paintChatComposeBackground"));
    }

    /* access modifiers changed from: private */
    public boolean onSendLongClick(View view) {
        int i;
        if (isInScheduleMode()) {
            return false;
        }
        ChatActivity chatActivity = this.parentFragment;
        boolean z = chatActivity != null && UserObject.isUserSelf(chatActivity.getCurrentUser());
        if (this.sendPopupLayout == null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity, this.resourcesProvider);
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
            this.sendPopupLayout.setDispatchKeyEventListener(new ChatActivityEnterView$$ExternalSyntheticLambda52(this));
            this.sendPopupLayout.setShownFromBottom(false);
            ChatActivity chatActivity2 = this.parentFragment;
            boolean z2 = chatActivity2 != null && chatActivity2.canScheduleMessage();
            boolean z3 = !z && (this.slowModeTimer <= 0 || isInScheduleMode());
            if (z2) {
                ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), true, !z3, this.resourcesProvider);
                if (z) {
                    actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                } else {
                    actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                }
                actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
                actionBarMenuSubItem.setOnClickListener(new ChatActivityEnterView$$ExternalSyntheticLambda17(this));
                this.sendPopupLayout.addView(actionBarMenuSubItem, LayoutHelper.createLinear(-1, 48));
            }
            if (z3) {
                ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(getContext(), !z2, true, this.resourcesProvider);
                actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                actionBarMenuSubItem2.setMinimumWidth(AndroidUtilities.dp(196.0f));
                actionBarMenuSubItem2.setOnClickListener(new ChatActivityEnterView$$ExternalSyntheticLambda21(this));
                this.sendPopupLayout.addView(actionBarMenuSubItem2, LayoutHelper.createLinear(-1, 48));
            }
            this.sendPopupLayout.setupRadialSelectors(getThemedColor("dialogButtonSelector"));
            AnonymousClass33 r0 = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2) {
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
                i = this.location[1] + view.getMeasuredHeight();
                this.sendPopupWindow.showAtLocation(view, 51, ((this.location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), i);
                this.sendPopupWindow.dimBehind();
                this.sendButton.invalidate();
                view.performHapticFeedback(3, 2);
                return false;
            }
        }
        i = (this.location[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f);
        this.sendPopupWindow.showAtLocation(view, 51, ((this.location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), i);
        this.sendPopupWindow.dimBehind();
        this.sendButton.invalidate();
        try {
            view.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$34(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$35(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatActivityEnterView$$ExternalSyntheticLambda53(this), this.resourcesProvider);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$36(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendMessageInternal(false, 0);
    }

    public boolean isSendButtonVisible() {
        return this.sendButton.getVisibility() == 0;
    }

    private void setRecordVideoButtonVisible(boolean z, boolean z2) {
        if (this.audioVideoSendButton != null) {
            this.isInVideoMode = z;
            if (z2) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                boolean z3 = false;
                if (DialogObject.isChatDialog(this.dialog_id)) {
                    TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
                    if (ChatObject.isChannel(chat) && !chat.megagroup) {
                        z3 = true;
                    }
                }
                globalMainSettings.edit().putBoolean(z3 ? "currentModeVideoChannel" : "currentModeVideo", z).apply();
            }
            this.audioVideoSendButton.setState(isInVideoMode() ? ChatActivityEnterViewAnimatedIconView.State.VIDEO : ChatActivityEnterViewAnimatedIconView.State.VOICE, z2);
            this.audioVideoSendButton.sendAccessibilityEvent(8);
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
        if (!this.hasRecordVideo || !isInVideoMode()) {
            this.delegate.needStartRecordAudio(0);
            MediaController.getInstance().stopRecording(0, false, 0);
        } else {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(5, true, 0);
        }
        this.recordingAudioVideo = false;
        updateRecordInterface(2);
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
            long r5 = r5.id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
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
            long r1 = r1.id
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
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda38 r0 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda38
            r0.<init>(r8)
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

    public void setAllowStickersAndGifs(boolean z, boolean z2, boolean z3) {
        setAllowStickersAndGifs(z, z2, z3, false);
    }

    public void setAllowStickersAndGifs(boolean z, boolean z2, boolean z3, boolean z4) {
        if (!((this.allowStickers == z2 && this.allowGifs == z3) || this.emojiView == null)) {
            if (this.emojiViewVisible && !z4) {
                this.removeEmojiViewAfterAnimation = true;
                hidePopup(false);
            } else if (z4) {
                openKeyboardInternal();
            }
        }
        this.allowAnimatedEmoji = z;
        this.allowStickers = z2;
        this.allowGifs = z3;
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.setAllow(z2, z3, true);
        }
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
    public /* synthetic */ void lambda$new$37(ValueAnimator valueAnimator) {
        MentionsContainerView mentionsContainerView;
        if (this.topView != null) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.topViewEnterProgress = floatValue;
            View view = this.topView;
            float f = 1.0f - floatValue;
            view.setTranslationY(((float) this.animatedTop) + (((float) view.getLayoutParams().height) * f));
            this.topLineView.setAlpha(floatValue);
            this.topLineView.setTranslationY((float) this.animatedTop);
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && (mentionsContainerView = chatActivity.mentionContainer) != null) {
                mentionsContainerView.setTranslationY(f * ((float) this.topView.getLayoutParams().height));
            }
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
            ChatActivityEnterView$$ExternalSyntheticLambda39 chatActivityEnterView$$ExternalSyntheticLambda39 = new ChatActivityEnterView$$ExternalSyntheticLambda39(this);
            this.showTopViewRunnable = chatActivityEnterView$$ExternalSyntheticLambda39;
            AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda39, 200);
        } else if (this.recordedAudioPanel.getVisibility() == 0) {
        } else {
            if (!this.forceShowSendButton || z2) {
                openKeyboard();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showTopView$38() {
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
                            if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.mentionContainer != null) {
                                ChatActivityEnterView.this.parentFragment.mentionContainer.setTranslationY(0.0f);
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            ValueAnimator valueAnimator = ChatActivityEnterView.this.currentTopViewAnimation;
                            if (valueAnimator != null && valueAnimator.equals(animator)) {
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

    public void onAdjustPanTransitionUpdate(float f, float f2, boolean z) {
        this.botWebViewMenuContainer.setTranslationY(f);
    }

    public void onAdjustPanTransitionEnd() {
        this.botWebViewMenuContainer.onPanTransitionEnd();
        Runnable runnable = this.onKeyboardClosed;
        if (runnable != null) {
            runnable.run();
            this.onKeyboardClosed = null;
        }
    }

    public void onAdjustPanTransitionStart(boolean z, int i) {
        Runnable runnable;
        this.botWebViewMenuContainer.onPanTransitionStart(z, i);
        if (z && (runnable = this.showTopViewRunnable) != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showTopViewRunnable.run();
        }
        Runnable runnable2 = this.setTextFieldRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.setTextFieldRunnable.run();
        }
        if (z && this.messageEditText.hasFocus() && hasBotWebView() && botCommandsMenuIsShowing()) {
            this.botWebViewMenuContainer.dismiss();
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
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC$Chat currentChat = chatActivity.getCurrentChat();
            TLRPC$UserFull currentUserInfo = this.parentFragment.getCurrentUserInfo();
            float f = 1.0f;
            if (currentChat != null) {
                FrameLayout frameLayout = this.audioVideoButtonContainer;
                if (!ChatObject.canSendMedia(currentChat)) {
                    f = 0.5f;
                }
                frameLayout.setAlpha(f);
                EmojiView emojiView2 = this.emojiView;
                if (emojiView2 != null) {
                    emojiView2.setStickersBanned(!ChatObject.canSendStickers(currentChat), currentChat.id);
                }
            } else if (currentUserInfo != null) {
                FrameLayout frameLayout2 = this.audioVideoButtonContainer;
                if (currentUserInfo.voice_messages_forbidden) {
                    f = 0.5f;
                }
                frameLayout2.setAlpha(f);
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
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
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
        ChatActivityEnterView$$ExternalSyntheticLambda45 chatActivityEnterView$$ExternalSyntheticLambda45 = new ChatActivityEnterView$$ExternalSyntheticLambda45(this);
        this.hideKeyboardRunnable = chatActivityEnterView$$ExternalSyntheticLambda45;
        AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda45, 500);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPause$39() {
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
            getVisibility();
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
        updateSendAsButton(false);
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
            boolean z = true;
            this.hasRecordVideo = true;
            if (DialogObject.isChatDialog(this.dialog_id)) {
                TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
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
        return this.isInVideoMode;
    }

    public boolean hasRecordVideo() {
        return this.hasRecordVideo;
    }

    public MessageObject getReplyingMessageObject() {
        return this.replyingMessageObject;
    }

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
            if (DialogObject.isChatDialog(this.dialog_id)) {
                TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
                TLRPC$ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(-this.dialog_id);
                z2 = ChatObject.isChannel(chat) && !chat.megagroup;
                if (ChatObject.getSendAsPeerId(chat, chatFull) == chat.id) {
                    z3 = true;
                }
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
            ChatActivityEnterViewAnimatedIconView chatActivityEnterViewAnimatedIconView = this.audioVideoSendButton;
            if (chatActivityEnterViewAnimatedIconView != null) {
                chatActivityEnterViewAnimatedIconView.setVisibility(0);
            }
            if (z) {
                this.attachButton.setAlpha(0.0f);
                this.emojiButton.setAlpha(0.0f);
                this.attachButton.setScaleX(0.0f);
                this.emojiButton.setScaleX(0.0f);
                this.attachButton.setScaleY(0.0f);
                this.emojiButton.setScaleY(0.0f);
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.recordPannelAnimation = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, new float[]{0.0f})});
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
                ImageView imageView = this.attachButton;
                if (imageView != null) {
                    imageView.setAlpha(0.0f);
                    this.attachButton.setScaleX(0.0f);
                    this.attachButton.setScaleY(0.0f);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, new float[]{1.0f})});
                    animatorSet.setDuration(150);
                } else {
                    animatorSet = null;
                }
                this.emojiButton.setAlpha(0.0f);
                this.emojiButton.setScaleX(0.0f);
                this.emojiButton.setScaleY(0.0f);
                AnimatorSet animatorSet5 = new AnimatorSet();
                animatorSet5.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiButton, View.SCALE_Y, new float[]{1.0f})});
                BotCommandsMenuView botCommandsMenuView2 = this.botCommandsMenuButton;
                if (botCommandsMenuView2 != null) {
                    botCommandsMenuView2.setAlpha(0.0f);
                    this.botCommandsMenuButton.setScaleY(0.0f);
                    this.botCommandsMenuButton.setScaleX(0.0f);
                    animatorSet5.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, new float[]{1.0f})});
                }
                animatorSet5.setDuration(150);
                animatorSet5.setStartDelay(600);
                AnimatorSet animatorSet6 = new AnimatorSet();
                this.recordPannelAnimation = animatorSet6;
                if (animatorSet != null) {
                    animatorSet6.playTogether(new Animator[]{animatorSet4, animatorSet, animatorSet5});
                } else {
                    animatorSet6.playTogether(new Animator[]{animatorSet4, animatorSet5});
                }
                this.recordPannelAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
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
            AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatActivityEnterView$$ExternalSyntheticLambda53(this), this.resourcesProvider);
        } else {
            sendMessageInternal(true, 0);
        }
    }

    /* access modifiers changed from: private */
    public void sendMessageInternal(boolean z, int i) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        TLRPC$Chat currentChat;
        EmojiView emojiView2;
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
                if (!(this.searchingType == 0 || (emojiView2 = this.emojiView) == null)) {
                    emojiView2.closeSearch(false);
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
                        AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendErrorTooLong", NUM), this.resourcesProvider);
                        return;
                    } else if (this.forceShowSendButton && text.length() > 0) {
                        AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM), this.resourcesProvider);
                        return;
                    }
                }
                if (processSendingText(text, z3, i3)) {
                    if (this.delegate.hasForwardingMessages() || ((i3 != 0 && !isInScheduleMode()) || isInScheduleMode())) {
                        this.messageEditText.setText("");
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                        if (chatActivityEnterViewDelegate3 != null) {
                            chatActivityEnterViewDelegate3.onMessageSend(text, z3, i3);
                        }
                    } else {
                        this.messageTransitionIsRunning = false;
                        ChatActivityEnterView$$ExternalSyntheticLambda48 chatActivityEnterView$$ExternalSyntheticLambda48 = new ChatActivityEnterView$$ExternalSyntheticLambda48(this, text, z3, i3);
                        this.moveToSendStateRunnable = chatActivityEnterView$$ExternalSyntheticLambda48;
                        AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda48, 200);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMessageInternal$40(CharSequence charSequence, boolean z, int i) {
        this.moveToSendStateRunnable = null;
        hideTopView(true);
        this.messageEditText.setText("");
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend(charSequence, z, i);
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
            if (this.searchingType != 0) {
                setSearchingTypeInternal(0, true);
                this.emojiView.closeSearch(false);
                if (this.stickersExpanded) {
                    setStickersExpanded(false, true, false);
                    this.waitingForKeyboardOpenAfterAnimation = true;
                    AndroidUtilities.runOnUIThread(new ChatActivityEnterView$$ExternalSyntheticLambda44(this), 200);
                }
            }
            CharSequence[] charSequenceArr = {AndroidUtilities.getTrimmedString(this.messageEditText.getText())};
            ArrayList<TLRPC$MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities());
            if (!TextUtils.equals(charSequenceArr[0], this.editingMessageObject.messageText) || ((entities != null && !entities.isEmpty()) || (((entities == null || entities.isEmpty()) && !this.editingMessageObject.messageOwner.entities.isEmpty()) || (this.editingMessageObject.messageOwner.media instanceof TLRPC$TL_messageMediaWebPage)))) {
                MessageObject messageObject = this.editingMessageObject;
                messageObject.editingMessage = charSequenceArr[0];
                messageObject.editingMessageEntities = entities;
                messageObject.editingMessageSearchWebPage = this.messageWebPageSearch;
                SendMessagesHelper.getInstance(this.currentAccount).editMessage(this.editingMessageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$TL_document) null, (String) null, (HashMap<String, String>) null, false, (Object) null);
            }
            setEditingMessageObject((MessageObject) null, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$doneEditingMessage$41() {
        this.waitingForKeyboardOpenAfterAnimation = false;
        openKeyboardInternal();
    }

    public boolean processSendingText(CharSequence charSequence, boolean z, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
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
            MessageObject.SendAnimationData sendAnimationData = null;
            if (!this.delegate.hasForwardingMessages()) {
                sendAnimationData = new MessageObject.SendAnimationData();
                float dp = (float) AndroidUtilities.dp(22.0f);
                sendAnimationData.height = dp;
                sendAnimationData.width = dp;
                this.messageEditText.getLocationInWindow(this.location);
                sendAnimationData.x = (float) (this.location[c] + AndroidUtilities.dp(11.0f));
                sendAnimationData.y = (float) (this.location[1] + AndroidUtilities.dp(19.0f));
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
            float f = 1.0f;
            if (i3 <= 0 || i3 == Integer.MAX_VALUE || isInScheduleMode()) {
                if (trimmedString.length() > 0 || this.forceShowSendButton || this.audioToSend != null || this.videoToSendMessageObject != null || (this.slowModeTimer == Integer.MAX_VALUE && !isInScheduleMode())) {
                    final String caption = this.messageEditText.getCaption();
                    boolean z3 = caption != null && (this.sendButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
                    boolean z4 = caption == null && (this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
                    if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
                        i = getThemedColor("chat_messagePanelSend");
                    } else {
                        i = getThemedColor("chat_messagePanelIcons");
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
                        TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
                        TLRPC$UserFull currentUserInfo = this.parentFragment.getCurrentUserInfo();
                        if (currentChat == null ? !(currentUserInfo == null || !currentUserInfo.voice_messages_forbidden) : !ChatObject.canSendMedia(currentChat)) {
                            f = 0.5f;
                        }
                        arrayList4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{f}));
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
            this.messageTransitionIsRunning = true;
            this.moveToSendStateRunnable.run();
            this.moveToSendStateRunnable = null;
        }
    }

    public boolean canShowMessageTransition() {
        return this.moveToSendStateRunnable != null;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x029b A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x029c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateRecordInterface(int r25) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
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
            r7 = 9
            r10 = 1101004800(0x41a00000, float:20.0)
            r11 = 5
            r15 = 4
            r3 = 3
            r5 = 2
            r6 = 0
            r13 = 1065353216(0x3var_, float:1.0)
            r14 = 1
            if (r0 == 0) goto L_0x0275
            int r0 = r1.recordInterfaceState
            if (r0 != r14) goto L_0x0028
            return
        L_0x0028:
            r1.recordInterfaceState = r14
            org.telegram.ui.Components.EmojiView r0 = r1.emojiView
            if (r0 == 0) goto L_0x0031
            r0.setEnabled(r4)
        L_0x0031:
            android.os.PowerManager$WakeLock r0 = r1.wakeLock     // Catch:{ Exception -> 0x004e }
            if (r0 != 0) goto L_0x0052
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x004e }
            java.lang.String r2 = "power"
            java.lang.Object r0 = r0.getSystemService(r2)     // Catch:{ Exception -> 0x004e }
            android.os.PowerManager r0 = (android.os.PowerManager) r0     // Catch:{ Exception -> 0x004e }
            r2 = 536870918(0x20000006, float:1.084203E-19)
            java.lang.String r12 = "telegram:audio_record_lock"
            android.os.PowerManager$WakeLock r0 = r0.newWakeLock(r2, r12)     // Catch:{ Exception -> 0x004e }
            r1.wakeLock = r0     // Catch:{ Exception -> 0x004e }
            r0.acquire()     // Catch:{ Exception -> 0x004e }
            goto L_0x0052
        L_0x004e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0052:
            android.app.Activity r0 = r1.parentActivity
            org.telegram.messenger.AndroidUtilities.lockOrientation(r0)
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r0 = r1.delegate
            if (r0 == 0) goto L_0x005e
            r0.needStartRecordAudio(r4)
        L_0x005e:
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            if (r0 == 0) goto L_0x0065
            r0.cancel()
        L_0x0065:
            android.animation.AnimatorSet r0 = r1.recordPannelAnimation
            if (r0 == 0) goto L_0x006c
            r0.cancel()
        L_0x006c:
            android.widget.FrameLayout r0 = r1.recordPanel
            r0.setVisibility(r4)
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            r0.setVisibility(r4)
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            r8 = 0
            r0.setAmplitude(r8)
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
            boolean unused = r0.enterAnimation = r14
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r0 = r1.recordTimerView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r2 = (float) r2
            r0.setTranslationX(r2)
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r0 = r1.recordTimerView
            r0.setAlpha(r6)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r2 = (float) r2
            r0.setTranslationX(r2)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            r0.setAlpha(r6)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            r0.setCancelToProgress(r6)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            r0.setSlideX(r13)
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            r2 = 1176256512(0x461CLASSNAME, float:10000.0)
            r0.setLockTranslation(r2)
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            r0.setEnabled(r14)
            r1.recordIsCanceled = r4
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            android.animation.Animator[] r2 = new android.animation.Animator[r7]
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r7 = r1.emojiButton
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r9 = new float[r14]
            r9[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r2[r4] = r7
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r7 = r1.emojiButton
            android.util.Property r8 = android.view.View.SCALE_X
            float[] r9 = new float[r14]
            r9[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r2[r14] = r7
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r7 = r1.emojiButton
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r14]
            r9[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r2[r5] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r7 = r1.recordDot
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r9 = new float[r14]
            r9[r4] = r13
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r2[r3] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r7 = r1.recordDot
            android.util.Property r8 = android.view.View.SCALE_X
            float[] r9 = new float[r14]
            r9[r4] = r13
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r2[r15] = r7
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r7 = r1.recordTimerView
            android.util.Property r8 = android.view.View.TRANSLATION_X
            float[] r9 = new float[r14]
            r9[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r2[r11] = r7
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r7 = r1.recordTimerView
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r14]
            r9[r4] = r13
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r8 = 6
            r2[r8] = r7
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r7 = r1.slideText
            android.util.Property r8 = android.view.View.TRANSLATION_X
            float[] r9 = new float[r14]
            r9[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r8 = 7
            r2[r8] = r7
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r7 = r1.slideText
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r14]
            r9[r4] = r13
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r8 = 8
            r2[r8] = r7
            r0.playTogether(r2)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r2 = r1.audioVideoSendButton
            if (r2 == 0) goto L_0x0170
            android.animation.Animator[] r7 = new android.animation.Animator[r14]
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r14]
            r9[r4] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r8, r9)
            r7[r4] = r2
            r0.playTogether(r7)
        L_0x0170:
            org.telegram.ui.Components.BotCommandsMenuView r2 = r1.botCommandsMenuButton
            if (r2 == 0) goto L_0x01a1
            android.animation.Animator[] r7 = new android.animation.Animator[r3]
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r9 = new float[r14]
            r9[r4] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r8, r9)
            r7[r4] = r2
            org.telegram.ui.Components.BotCommandsMenuView r2 = r1.botCommandsMenuButton
            android.util.Property r8 = android.view.View.SCALE_X
            float[] r9 = new float[r14]
            r9[r4] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r8, r9)
            r7[r14] = r2
            org.telegram.ui.Components.BotCommandsMenuView r2 = r1.botCommandsMenuButton
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r14]
            r9[r4] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r8, r9)
            r7[r5] = r2
            r0.playTogether(r7)
        L_0x01a1:
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            android.animation.Animator[] r7 = new android.animation.Animator[r3]
            org.telegram.ui.Components.EditTextCaption r8 = r1.messageEditText
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r14]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r11[r4] = r10
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r11)
            r7[r4] = r8
            org.telegram.ui.Components.EditTextCaption r8 = r1.messageEditText
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r14]
            r10[r4] = r6
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r7[r14] = r8
            android.widget.FrameLayout r8 = r1.recordedAudioPanel
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r14]
            r10[r4] = r13
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r7[r5] = r8
            r2.playTogether(r7)
            android.widget.ImageView r7 = r1.scheduledButton
            r8 = 1106247680(0x41var_, float:30.0)
            if (r7 == 0) goto L_0x0204
            android.animation.Animator[] r9 = new android.animation.Animator[r5]
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r14]
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r12 = (float) r12
            r11[r4] = r12
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r9[r4] = r7
            android.widget.ImageView r7 = r1.scheduledButton
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r14]
            r11[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r9[r14] = r7
            r2.playTogether(r9)
        L_0x0204:
            android.widget.LinearLayout r7 = r1.attachLayout
            if (r7 == 0) goto L_0x022c
            android.animation.Animator[] r9 = new android.animation.Animator[r5]
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r14]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r11[r4] = r8
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r9[r4] = r7
            android.widget.LinearLayout r7 = r1.attachLayout
            android.util.Property r8 = android.view.View.ALPHA
            float[] r10 = new float[r14]
            r10[r4] = r6
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r7, r8, r10)
            r9[r14] = r6
            r2.playTogether(r9)
        L_0x022c:
            android.animation.AnimatorSet r6 = r1.runningAnimationAudio
            android.animation.Animator[] r3 = new android.animation.Animator[r3]
            r7 = 150(0x96, double:7.4E-322)
            android.animation.AnimatorSet r0 = r0.setDuration(r7)
            r3[r4] = r0
            android.animation.AnimatorSet r0 = r2.setDuration(r7)
            r3[r14] = r0
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            android.util.Property<org.telegram.ui.Components.ChatActivityEnterView$RecordCircle, java.lang.Float> r2 = r1.recordCircleScale
            float[] r7 = new float[r14]
            r7[r4] = r13
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r2, r7)
            r7 = 300(0x12c, double:1.48E-321)
            android.animation.ObjectAnimator r0 = r0.setDuration(r7)
            r3[r5] = r0
            r6.playTogether(r3)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            org.telegram.ui.Components.ChatActivityEnterView$47 r2 = new org.telegram.ui.Components.ChatActivityEnterView$47
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
            goto L_0x0c4a
        L_0x0275:
            boolean r0 = r1.recordIsCanceled
            if (r0 == 0) goto L_0x027c
            if (r2 != r3) goto L_0x027c
            return
        L_0x027c:
            android.os.PowerManager$WakeLock r0 = r1.wakeLock
            if (r0 == 0) goto L_0x028f
            r0.release()     // Catch:{ Exception -> 0x0289 }
            r8 = 0
            r1.wakeLock = r8     // Catch:{ Exception -> 0x0287 }
            goto L_0x0290
        L_0x0287:
            r0 = move-exception
            goto L_0x028b
        L_0x0289:
            r0 = move-exception
            r8 = 0
        L_0x028b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0290
        L_0x028f:
            r8 = 0
        L_0x0290:
            android.app.Activity r0 = r1.parentActivity
            org.telegram.messenger.AndroidUtilities.unlockOrientation(r0)
            r1.wasSendTyping = r4
            int r0 = r1.recordInterfaceState
            if (r0 != 0) goto L_0x029c
            return
        L_0x029c:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r18 = r0.getMessagesController()
            long r8 = r1.dialog_id
            int r21 = r24.getThreadMessageId()
            r22 = 2
            r23 = 0
            r19 = r8
            r18.sendTyping(r19, r21, r22, r23)
            r1.recordInterfaceState = r4
            org.telegram.ui.Components.EmojiView r0 = r1.emojiView
            if (r0 == 0) goto L_0x02ba
            r0.setEnabled(r14)
        L_0x02ba:
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            if (r0 == 0) goto L_0x02d9
            boolean r0 = r0.isRunning()
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r8 = r1.audioVideoSendButton
            if (r8 == 0) goto L_0x02ce
            r8.setScaleX(r13)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r8 = r1.audioVideoSendButton
            r8.setScaleY(r13)
        L_0x02ce:
            android.animation.AnimatorSet r8 = r1.runningAnimationAudio
            r8.removeAllListeners()
            android.animation.AnimatorSet r8 = r1.runningAnimationAudio
            r8.cancel()
            goto L_0x02da
        L_0x02d9:
            r0 = 0
        L_0x02da:
            android.animation.AnimatorSet r8 = r1.recordPannelAnimation
            if (r8 == 0) goto L_0x02e1
            r8.cancel()
        L_0x02e1:
            org.telegram.ui.Components.EditTextCaption r8 = r1.messageEditText
            r8.setVisibility(r4)
            android.animation.AnimatorSet r8 = new android.animation.AnimatorSet
            r8.<init>()
            r1.runningAnimationAudio = r8
            r8 = 12
            r18 = 10
            java.lang.String r12 = "slideToCancelProgress"
            if (r0 != 0) goto L_0x0a9e
            if (r2 != r15) goto L_0x02f9
            goto L_0x0a9e
        L_0x02f9:
            if (r2 != r3) goto L_0x0596
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r0 = r1.slideText
            r0.setEnabled(r4)
            boolean r0 = r24.isInVideoMode()
            if (r0 == 0) goto L_0x0331
            android.view.View r0 = r1.recordedAudioBackground
            r12 = 8
            r0.setVisibility(r12)
            android.widget.TextView r0 = r1.recordedAudioTimeTextView
            r0.setVisibility(r12)
            android.widget.ImageView r0 = r1.recordedAudioPlayButton
            r0.setVisibility(r12)
            org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView r0 = r1.recordedAudioSeekBar
            r0.setVisibility(r12)
            android.widget.FrameLayout r0 = r1.recordedAudioPanel
            r0.setAlpha(r13)
            android.widget.FrameLayout r0 = r1.recordedAudioPanel
            r0.setVisibility(r4)
            org.telegram.ui.Components.RLottieImageView r0 = r1.recordDeleteImageView
            r0.setProgress(r6)
            org.telegram.ui.Components.RLottieImageView r0 = r1.recordDeleteImageView
            r0.stopAnimation()
            goto L_0x036a
        L_0x0331:
            r12 = 8
            org.telegram.ui.Components.VideoTimelineView r0 = r1.videoTimelineView
            r0.setVisibility(r12)
            android.view.View r0 = r1.recordedAudioBackground
            r0.setVisibility(r4)
            android.widget.TextView r0 = r1.recordedAudioTimeTextView
            r0.setVisibility(r4)
            android.widget.ImageView r0 = r1.recordedAudioPlayButton
            r0.setVisibility(r4)
            org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView r0 = r1.recordedAudioSeekBar
            r0.setVisibility(r4)
            android.widget.FrameLayout r0 = r1.recordedAudioPanel
            r0.setAlpha(r13)
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
        L_0x036a:
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
            float[] r0 = new float[r5]
            r0 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda4 r12 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda4
            r12.<init>(r1)
            r0.addUpdateListener(r12)
            boolean r12 = r24.isInVideoMode()
            if (r12 != 0) goto L_0x03cf
            android.widget.FrameLayout r12 = r1.recordedAudioPanel
            android.view.ViewParent r12 = r12.getParent()
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            android.widget.FrameLayout r9 = r1.recordedAudioPanel
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            android.widget.FrameLayout r7 = r1.recordedAudioPanel
            r12.removeView(r7)
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            int r11 = r12.getMeasuredWidth()
            r16 = 1111490560(0x42400000, float:48.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.<init>(r11, r13)
            r11 = 80
            r7.gravity = r11
            org.telegram.ui.Components.SizeNotifierFrameLayout r11 = r1.sizeNotifierLayout
            android.widget.FrameLayout r13 = r1.recordedAudioPanel
            r11.addView(r13, r7)
            org.telegram.ui.Components.VideoTimelineView r7 = r1.videoTimelineView
            r11 = 8
            r7.setVisibility(r11)
            goto L_0x03d6
        L_0x03cf:
            org.telegram.ui.Components.VideoTimelineView r7 = r1.videoTimelineView
            r7.setVisibility(r4)
            r9 = 0
            r12 = 0
        L_0x03d6:
            org.telegram.ui.Components.RLottieImageView r7 = r1.recordDeleteImageView
            r7.setAlpha(r6)
            org.telegram.ui.Components.RLottieImageView r7 = r1.recordDeleteImageView
            r7.setScaleX(r6)
            org.telegram.ui.Components.RLottieImageView r7 = r1.recordDeleteImageView
            r7.setScaleY(r6)
            android.animation.AnimatorSet r7 = new android.animation.AnimatorSet
            r7.<init>()
            android.animation.Animator[] r8 = new android.animation.Animator[r8]
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r11 = r1.recordDot
            android.util.Property r13 = android.view.View.SCALE_Y
            float[] r15 = new float[r14]
            r15[r4] = r6
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r13, r15)
            r8[r4] = r11
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r11 = r1.recordDot
            android.util.Property r13 = android.view.View.SCALE_X
            float[] r15 = new float[r14]
            r15[r4] = r6
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r13, r15)
            r8[r14] = r11
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r11 = r1.recordTimerView
            android.util.Property r13 = android.view.View.ALPHA
            float[] r15 = new float[r14]
            r15[r4] = r6
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r13, r15)
            r8[r5] = r11
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r11 = r1.recordTimerView
            android.util.Property r13 = android.view.View.TRANSLATION_X
            float[] r15 = new float[r14]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = -r10
            float r10 = (float) r10
            r15[r4] = r10
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r11, r13, r15)
            r8[r3] = r10
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r10 = r1.slideText
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r11 = 4
            r8[r11] = r10
            org.telegram.ui.Components.RLottieImageView r10 = r1.recordDeleteImageView
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r15 = 1065353216(0x3var_, float:1.0)
            r13[r4] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r11 = 5
            r8[r11] = r10
            org.telegram.ui.Components.RLottieImageView r10 = r1.recordDeleteImageView
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r13 = new float[r14]
            r13[r4] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r11 = 6
            r8[r11] = r10
            org.telegram.ui.Components.RLottieImageView r10 = r1.recordDeleteImageView
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r13 = new float[r14]
            r13[r4] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r11 = 7
            r8[r11] = r10
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r10 = r1.emojiButton
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r11 = 8
            r8[r11] = r10
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r10 = r1.emojiButton
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r11 = 9
            r8[r11] = r10
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r10 = r1.emojiButton
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r8[r18] = r10
            org.telegram.ui.Components.EditTextCaption r10 = r1.messageEditText
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r11 = 11
            r8[r11] = r10
            r7.playTogether(r8)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r8 = r1.audioVideoSendButton
            if (r8 == 0) goto L_0x04ec
            android.animation.Animator[] r10 = new android.animation.Animator[r3]
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r15 = 1065353216(0x3var_, float:1.0)
            r13[r4] = r15
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r13)
            r10[r4] = r8
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r8 = r1.audioVideoSendButton
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r13 = new float[r14]
            r13[r4] = r15
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r13)
            r10[r14] = r8
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r8 = r1.audioVideoSendButton
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r13 = new float[r14]
            r13[r4] = r15
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r13)
            r10[r5] = r8
            r7.playTogether(r10)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r8 = r1.audioVideoSendButton
            boolean r10 = r24.isInVideoMode()
            if (r10 == 0) goto L_0x04e7
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r10 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.VIDEO
            goto L_0x04e9
        L_0x04e7:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r10 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.VOICE
        L_0x04e9:
            r8.setState(r10, r14)
        L_0x04ec:
            org.telegram.ui.Components.BotCommandsMenuView r8 = r1.botCommandsMenuButton
            if (r8 == 0) goto L_0x051d
            android.animation.Animator[] r10 = new android.animation.Animator[r3]
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r13)
            r10[r4] = r8
            org.telegram.ui.Components.BotCommandsMenuView r8 = r1.botCommandsMenuButton
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r13)
            r10[r14] = r8
            org.telegram.ui.Components.BotCommandsMenuView r8 = r1.botCommandsMenuButton
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r13)
            r10[r5] = r8
            r7.playTogether(r10)
        L_0x051d:
            org.telegram.ui.Components.ChatActivityEnterView$48 r8 = new org.telegram.ui.Components.ChatActivityEnterView$48
            r8.<init>()
            r7.addListener(r8)
            r10 = 150(0x96, double:7.4E-322)
            r7.setDuration(r10)
            r7.setStartDelay(r10)
            android.animation.AnimatorSet r8 = new android.animation.AnimatorSet
            r8.<init>()
            boolean r10 = r24.isInVideoMode()
            if (r10 == 0) goto L_0x056f
            android.widget.TextView r10 = r1.recordedAudioTimeTextView
            r10.setAlpha(r6)
            org.telegram.ui.Components.VideoTimelineView r10 = r1.videoTimelineView
            r10.setAlpha(r6)
            android.animation.Animator[] r6 = new android.animation.Animator[r5]
            android.widget.TextView r10 = r1.recordedAudioTimeTextView
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r15 = 1065353216(0x3var_, float:1.0)
            r13[r4] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r6[r4] = r10
            org.telegram.ui.Components.VideoTimelineView r10 = r1.videoTimelineView
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r13[r4] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r6[r14] = r10
            r8.playTogether(r6)
            r10 = 150(0x96, double:7.4E-322)
            r8.setDuration(r10)
            r10 = 430(0x1ae, double:2.124E-321)
            r8.setStartDelay(r10)
        L_0x056f:
            boolean r6 = r24.isInVideoMode()
            if (r6 == 0) goto L_0x0578
            r10 = 490(0x1ea, double:2.42E-321)
            goto L_0x057a
        L_0x0578:
            r10 = 580(0x244, double:2.866E-321)
        L_0x057a:
            r0.setDuration(r10)
            android.animation.AnimatorSet r6 = r1.runningAnimationAudio
            android.animation.Animator[] r3 = new android.animation.Animator[r3]
            r3[r4] = r7
            r3[r14] = r0
            r3[r5] = r8
            r6.playTogether(r3)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            org.telegram.ui.Components.ChatActivityEnterView$49 r3 = new org.telegram.ui.Components.ChatActivityEnterView$49
            r3.<init>(r12, r9)
            r0.addListener(r3)
            goto L_0x0CLASSNAME
        L_0x0596:
            r7 = 200(0xc8, double:9.9E-322)
            if (r2 == r5) goto L_0x0748
            r9 = 5
            if (r2 != r9) goto L_0x059f
            goto L_0x0748
        L_0x059f:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r1.audioVideoSendButton
            if (r0 == 0) goto L_0x05a6
            r0.setVisibility(r4)
        L_0x05a6:
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r9 = 6
            android.animation.Animator[] r9 = new android.animation.Animator[r9]
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r10 = r1.emojiButton
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r12 = new float[r14]
            r13 = 1065353216(0x3var_, float:1.0)
            r12[r4] = r13
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r9[r4] = r10
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r10 = r1.emojiButton
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r12 = new float[r14]
            r12[r4] = r13
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r9[r14] = r10
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r10 = r1.emojiButton
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r14]
            r12[r4] = r13
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r9[r5] = r10
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r10 = r1.recordDot
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r12 = new float[r14]
            r12[r4] = r6
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r9[r3] = r10
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r10 = r1.recordDot
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r12 = new float[r14]
            r12[r4] = r6
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r11 = 4
            r9[r11] = r10
            android.widget.FrameLayout r10 = r1.audioVideoButtonContainer
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r14]
            r13 = 1065353216(0x3var_, float:1.0)
            r12[r4] = r13
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r11 = 5
            r9[r11] = r10
            r0.playTogether(r9)
            org.telegram.ui.Components.BotCommandsMenuView r9 = r1.botCommandsMenuButton
            if (r9 == 0) goto L_0x063c
            android.animation.Animator[] r10 = new android.animation.Animator[r3]
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r12 = new float[r14]
            r12[r4] = r13
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r11, r12)
            r10[r4] = r9
            org.telegram.ui.Components.BotCommandsMenuView r9 = r1.botCommandsMenuButton
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r12 = new float[r14]
            r12[r4] = r13
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r11, r12)
            r10[r14] = r9
            org.telegram.ui.Components.BotCommandsMenuView r9 = r1.botCommandsMenuButton
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r14]
            r12[r4] = r13
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r11, r12)
            r10[r5] = r9
            r0.playTogether(r10)
        L_0x063c:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r9 = r1.audioVideoSendButton
            if (r9 == 0) goto L_0x066b
            r9.setScaleX(r13)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r9 = r1.audioVideoSendButton
            r9.setScaleY(r13)
            android.animation.Animator[] r9 = new android.animation.Animator[r14]
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r10 = r1.audioVideoSendButton
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r14]
            r12[r4] = r13
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r9[r4] = r10
            r0.playTogether(r9)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r9 = r1.audioVideoSendButton
            boolean r10 = r24.isInVideoMode()
            if (r10 == 0) goto L_0x0666
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r10 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.VIDEO
            goto L_0x0668
        L_0x0666:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r10 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.VOICE
        L_0x0668:
            r9.setState(r10, r14)
        L_0x066b:
            android.widget.LinearLayout r9 = r1.attachLayout
            if (r9 == 0) goto L_0x0687
            r9.setTranslationX(r6)
            android.animation.Animator[] r9 = new android.animation.Animator[r14]
            android.widget.LinearLayout r10 = r1.attachLayout
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r14]
            r13 = 1065353216(0x3var_, float:1.0)
            r12[r4] = r13
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r9[r4] = r10
            r0.playTogether(r9)
        L_0x0687:
            android.widget.ImageView r9 = r1.scheduledButton
            if (r9 == 0) goto L_0x06a3
            r9.setTranslationX(r6)
            android.animation.Animator[] r9 = new android.animation.Animator[r14]
            android.widget.ImageView r10 = r1.scheduledButton
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r14]
            r13 = 1065353216(0x3var_, float:1.0)
            r12[r4] = r13
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r9[r4] = r10
            r0.playTogether(r9)
        L_0x06a3:
            r9 = 150(0x96, double:7.4E-322)
            r0.setDuration(r9)
            r0.setStartDelay(r7)
            android.animation.AnimatorSet r9 = new android.animation.AnimatorSet
            r9.<init>()
            r10 = 4
            android.animation.Animator[] r11 = new android.animation.Animator[r10]
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r10 = r1.recordTimerView
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r13)
            r11[r4] = r10
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r10 = r1.recordTimerView
            android.util.Property r12 = android.view.View.TRANSLATION_X
            float[] r13 = new float[r14]
            r15 = 1109393408(0x42200000, float:40.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r13[r4] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r13)
            r11[r14] = r10
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r10 = r1.slideText
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r13)
            r11[r5] = r10
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r10 = r1.slideText
            android.util.Property r12 = android.view.View.TRANSLATION_X
            float[] r13 = new float[r14]
            r15 = 1109393408(0x42200000, float:40.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r13[r4] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r13)
            r11[r3] = r10
            r9.playTogether(r11)
            r10 = 150(0x96, double:7.4E-322)
            r9.setDuration(r10)
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r10 = r1.recordCircle
            float[] r11 = new float[r14]
            r12 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r12
            java.lang.String r12 = "exitTransition"
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r11)
            boolean r11 = r1.messageTransitionIsRunning
            if (r11 == 0) goto L_0x0716
            r11 = 220(0xdc, double:1.087E-321)
            goto L_0x0718
        L_0x0716:
            r11 = 360(0x168, double:1.78E-321)
        L_0x0718:
            r10.setDuration(r11)
            org.telegram.ui.Components.EditTextCaption r11 = r1.messageEditText
            r11.setTranslationX(r6)
            org.telegram.ui.Components.EditTextCaption r6 = r1.messageEditText
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r14]
            r13 = 1065353216(0x3var_, float:1.0)
            r12[r4] = r13
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r11, r12)
            r11 = 150(0x96, double:7.4E-322)
            r6.setStartDelay(r11)
            r6.setDuration(r7)
            android.animation.AnimatorSet r7 = r1.runningAnimationAudio
            r8 = 4
            android.animation.Animator[] r8 = new android.animation.Animator[r8]
            r8[r4] = r0
            r8[r14] = r9
            r8[r5] = r6
            r8[r3] = r10
            r7.playTogether(r8)
            goto L_0x0CLASSNAME
        L_0x0748:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r1.audioVideoSendButton
            if (r0 == 0) goto L_0x074f
            r0.setVisibility(r4)
        L_0x074f:
            r1.recordIsCanceled = r14
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r9 = 5
            android.animation.Animator[] r11 = new android.animation.Animator[r9]
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r9 = r1.emojiButton
            android.util.Property r13 = android.view.View.SCALE_Y
            float[] r15 = new float[r14]
            r17 = 1065353216(0x3var_, float:1.0)
            r15[r4] = r17
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r13, r15)
            r11[r4] = r9
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r9 = r1.emojiButton
            android.util.Property r13 = android.view.View.SCALE_X
            float[] r15 = new float[r14]
            r15[r4] = r17
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r13, r15)
            r11[r14] = r9
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r9 = r1.emojiButton
            android.util.Property r13 = android.view.View.ALPHA
            float[] r15 = new float[r14]
            r15[r4] = r17
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r13, r15)
            r11[r5] = r9
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r9 = r1.recordDot
            android.util.Property r13 = android.view.View.SCALE_Y
            float[] r15 = new float[r14]
            r15[r4] = r6
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r13, r15)
            r11[r3] = r9
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r9 = r1.recordDot
            android.util.Property r13 = android.view.View.SCALE_X
            float[] r15 = new float[r14]
            r15[r4] = r6
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r13, r15)
            r13 = 4
            r11[r13] = r9
            r0.playTogether(r11)
            org.telegram.ui.Components.BotCommandsMenuView r9 = r1.botCommandsMenuButton
            if (r9 == 0) goto L_0x07d8
            android.animation.Animator[] r11 = new android.animation.Animator[r3]
            android.util.Property r13 = android.view.View.SCALE_Y
            float[] r15 = new float[r14]
            r17 = 1065353216(0x3var_, float:1.0)
            r15[r4] = r17
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r13, r15)
            r11[r4] = r9
            org.telegram.ui.Components.BotCommandsMenuView r9 = r1.botCommandsMenuButton
            android.util.Property r13 = android.view.View.SCALE_X
            float[] r15 = new float[r14]
            r15[r4] = r17
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r13, r15)
            r11[r14] = r9
            org.telegram.ui.Components.BotCommandsMenuView r9 = r1.botCommandsMenuButton
            android.util.Property r13 = android.view.View.ALPHA
            float[] r15 = new float[r14]
            r15[r4] = r17
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r13, r15)
            r11[r5] = r9
            r0.playTogether(r11)
        L_0x07d8:
            android.animation.AnimatorSet r9 = new android.animation.AnimatorSet
            r9.<init>()
            r11 = 4
            android.animation.Animator[] r13 = new android.animation.Animator[r11]
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r11 = r1.recordTimerView
            android.util.Property r15 = android.view.View.ALPHA
            float[] r7 = new float[r14]
            r7[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r11, r15, r7)
            r13[r4] = r7
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r7 = r1.recordTimerView
            android.util.Property r8 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r14]
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r15 = -r15
            float r15 = (float) r15
            r11[r4] = r15
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r11)
            r13[r14] = r7
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r7 = r1.slideText
            android.util.Property r8 = android.view.View.ALPHA
            float[] r11 = new float[r14]
            r11[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r11)
            r13[r5] = r7
            org.telegram.ui.Components.ChatActivityEnterView$SlideTextView r7 = r1.slideText
            android.util.Property r8 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r14]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = -r10
            float r10 = (float) r10
            r11[r4] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r11)
            r13[r3] = r7
            r9.playTogether(r13)
            r7 = 5
            if (r2 == r7) goto L_0x097c
            android.widget.FrameLayout r7 = r1.audioVideoButtonContainer
            r7.setScaleX(r6)
            android.widget.FrameLayout r7 = r1.audioVideoButtonContainer
            r7.setScaleY(r6)
            android.widget.ImageView r7 = r1.attachButton
            if (r7 == 0) goto L_0x0848
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x0848
            android.widget.ImageView r7 = r1.attachButton
            r7.setScaleX(r6)
            android.widget.ImageView r7 = r1.attachButton
            r7.setScaleY(r6)
        L_0x0848:
            android.widget.ImageView r7 = r1.botButton
            if (r7 == 0) goto L_0x085c
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x085c
            android.widget.ImageView r7 = r1.botButton
            r7.setScaleX(r6)
            android.widget.ImageView r7 = r1.botButton
            r7.setScaleY(r6)
        L_0x085c:
            r7 = 4
            android.animation.Animator[] r8 = new android.animation.Animator[r7]
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r7 = r1.recordCircle
            float[] r10 = new float[r14]
            r11 = 1065353216(0x3var_, float:1.0)
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r12, r10)
            r8[r4] = r7
            android.widget.FrameLayout r7 = r1.audioVideoButtonContainer
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r13 = new float[r14]
            r13[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r13)
            r8[r14] = r7
            android.widget.FrameLayout r7 = r1.audioVideoButtonContainer
            android.util.Property r10 = android.view.View.SCALE_Y
            float[] r13 = new float[r14]
            r13[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r13)
            r8[r5] = r7
            android.widget.FrameLayout r7 = r1.audioVideoButtonContainer
            android.util.Property r10 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r13[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r13)
            r8[r3] = r7
            r0.playTogether(r8)
            android.widget.LinearLayout r7 = r1.attachLayout
            if (r7 == 0) goto L_0x08bd
            android.animation.Animator[] r8 = new android.animation.Animator[r5]
            android.util.Property r10 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r13[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r13)
            r8[r4] = r7
            android.widget.LinearLayout r7 = r1.attachLayout
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r14]
            r11[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r8[r14] = r7
            r0.playTogether(r8)
        L_0x08bd:
            android.widget.ImageView r7 = r1.attachButton
            if (r7 == 0) goto L_0x08e3
            android.animation.Animator[] r8 = new android.animation.Animator[r5]
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r14]
            r13 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r13
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r8[r4] = r7
            android.widget.ImageView r7 = r1.attachButton
            android.util.Property r10 = android.view.View.SCALE_Y
            float[] r11 = new float[r14]
            r11[r4] = r13
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r8[r14] = r7
            r0.playTogether(r8)
            goto L_0x08e5
        L_0x08e3:
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x08e5:
            android.widget.ImageView r7 = r1.botButton
            if (r7 == 0) goto L_0x0908
            android.animation.Animator[] r8 = new android.animation.Animator[r5]
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r14]
            r11[r4] = r13
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r8[r4] = r7
            android.widget.ImageView r7 = r1.botButton
            android.util.Property r10 = android.view.View.SCALE_Y
            float[] r11 = new float[r14]
            r11[r4] = r13
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r8[r14] = r7
            r0.playTogether(r8)
        L_0x0908:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r7 = r1.audioVideoSendButton
            if (r7 == 0) goto L_0x0953
            android.animation.Animator[] r8 = new android.animation.Animator[r14]
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r14]
            r11[r4] = r13
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r8[r4] = r7
            r0.playTogether(r8)
            android.animation.Animator[] r7 = new android.animation.Animator[r14]
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r8 = r1.audioVideoSendButton
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r14]
            r11[r4] = r13
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r10, r11)
            r7[r4] = r8
            r0.playTogether(r7)
            android.animation.Animator[] r7 = new android.animation.Animator[r14]
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r8 = r1.audioVideoSendButton
            android.util.Property r10 = android.view.View.SCALE_Y
            float[] r11 = new float[r14]
            r11[r4] = r13
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r10, r11)
            r7[r4] = r8
            r0.playTogether(r7)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r7 = r1.audioVideoSendButton
            boolean r8 = r24.isInVideoMode()
            if (r8 == 0) goto L_0x094e
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r8 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.VIDEO
            goto L_0x0950
        L_0x094e:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r8 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.VOICE
        L_0x0950:
            r7.setState(r8, r14)
        L_0x0953:
            android.widget.ImageView r7 = r1.scheduledButton
            if (r7 == 0) goto L_0x0978
            android.animation.Animator[] r8 = new android.animation.Animator[r5]
            android.util.Property r10 = android.view.View.ALPHA
            float[] r11 = new float[r14]
            r13 = 1065353216(0x3var_, float:1.0)
            r11[r4] = r13
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r8[r4] = r7
            android.widget.ImageView r7 = r1.scheduledButton
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r14]
            r11[r4] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r11)
            r8[r14] = r7
            r0.playTogether(r8)
        L_0x0978:
            r10 = 150(0x96, double:7.4E-322)
            goto L_0x09fc
        L_0x097c:
            android.animation.AnimatorSet r7 = new android.animation.AnimatorSet
            r7.<init>()
            android.animation.Animator[] r8 = new android.animation.Animator[r14]
            android.widget.FrameLayout r10 = r1.audioVideoButtonContainer
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r15 = 1065353216(0x3var_, float:1.0)
            r13[r4] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r8[r4] = r10
            r7.playTogether(r8)
            android.widget.LinearLayout r8 = r1.attachLayout
            if (r8 == 0) goto L_0x09bc
            android.animation.Animator[] r10 = new android.animation.Animator[r5]
            android.util.Property r11 = android.view.View.TRANSLATION_X
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r13)
            r10[r4] = r8
            android.widget.LinearLayout r8 = r1.attachLayout
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r15 = 1065353216(0x3var_, float:1.0)
            r13[r4] = r15
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r13)
            r10[r14] = r8
            r7.playTogether(r10)
            goto L_0x09be
        L_0x09bc:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x09be:
            android.widget.ImageView r8 = r1.scheduledButton
            if (r8 == 0) goto L_0x09e1
            android.animation.Animator[] r10 = new android.animation.Animator[r5]
            android.util.Property r11 = android.view.View.ALPHA
            float[] r13 = new float[r14]
            r13[r4] = r15
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r13)
            r10[r4] = r8
            android.widget.ImageView r8 = r1.scheduledButton
            android.util.Property r11 = android.view.View.TRANSLATION_X
            float[] r13 = new float[r14]
            r13[r4] = r6
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r13)
            r10[r14] = r8
            r7.playTogether(r10)
        L_0x09e1:
            r10 = 150(0x96, double:7.4E-322)
            r7.setDuration(r10)
            r5 = 110(0x6e, double:5.43E-322)
            r7.setStartDelay(r5)
            org.telegram.ui.Components.ChatActivityEnterView$50 r5 = new org.telegram.ui.Components.ChatActivityEnterView$50
            r5.<init>()
            r7.addListener(r5)
            android.animation.AnimatorSet r5 = r1.runningAnimationAudio
            android.animation.Animator[] r6 = new android.animation.Animator[r14]
            r6[r4] = r7
            r5.playTogether(r6)
        L_0x09fc:
            r0.setDuration(r10)
            r5 = 700(0x2bc, double:3.46E-321)
            r0.setStartDelay(r5)
            r5 = 200(0xc8, double:9.9E-322)
            r9.setDuration(r5)
            r9.setStartDelay(r5)
            org.telegram.ui.Components.EditTextCaption r7 = r1.messageEditText
            r8 = 0
            r7.setTranslationX(r8)
            org.telegram.ui.Components.EditTextCaption r7 = r1.messageEditText
            android.util.Property r8 = android.view.View.ALPHA
            float[] r10 = new float[r14]
            r11 = 1065353216(0x3var_, float:1.0)
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r10)
            r10 = 300(0x12c, double:1.48E-321)
            r7.setStartDelay(r10)
            r7.setDuration(r5)
            android.animation.AnimatorSet r5 = r1.runningAnimationAudio
            r6 = 4
            android.animation.Animator[] r6 = new android.animation.Animator[r6]
            r6[r4] = r0
            r6[r14] = r9
            r8 = 2
            r6[r8] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            float[] r7 = new float[r14]
            float r8 = r0.startTranslation
            r7[r4] = r8
            java.lang.String r8 = "lockAnimatedTranslation"
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r8, r7)
            r7 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r0 = r0.setDuration(r7)
            r6[r3] = r0
            r5.playTogether(r6)
            r3 = 5
            if (r2 != r3) goto L_0x0a76
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            r0.canceledByGesture()
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            float[] r3 = new float[r14]
            r5 = 1065353216(0x3var_, float:1.0)
            r3[r4] = r5
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r12, r3)
            android.animation.ObjectAnimator r0 = r0.setDuration(r7)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r0.setInterpolator(r3)
            android.animation.AnimatorSet r3 = r1.runningAnimationAudio
            android.animation.Animator[] r5 = new android.animation.Animator[r14]
            r5[r4] = r0
            r3.playTogether(r5)
            goto L_0x0a97
        L_0x0a76:
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r0 = r1.recordCircle
            float[] r3 = new float[r14]
            r5 = 1065353216(0x3var_, float:1.0)
            r3[r4] = r5
            java.lang.String r5 = "exitTransition"
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r5, r3)
            r5 = 360(0x168, double:1.78E-321)
            r0.setDuration(r5)
            r5 = 490(0x1ea, double:2.42E-321)
            r0.setStartDelay(r5)
            android.animation.AnimatorSet r3 = r1.runningAnimationAudio
            android.animation.Animator[] r5 = new android.animation.Animator[r14]
            r5[r4] = r0
            r3.playTogether(r5)
        L_0x0a97:
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r0 = r1.recordDot
            r0.playDeleteAnimation()
            goto L_0x0CLASSNAME
        L_0x0a9e:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r1.audioVideoSendButton
            if (r0 == 0) goto L_0x0aa5
            r0.setVisibility(r4)
        L_0x0aa5:
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            r6 = 13
            android.animation.Animator[] r6 = new android.animation.Animator[r6]
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r7 = r1.emojiButton
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r14]
            r11 = 1065353216(0x3var_, float:1.0)
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r6[r4] = r7
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r7 = r1.emojiButton
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r14]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r6[r14] = r7
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r7 = r1.emojiButton
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r14]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r9 = 2
            r6[r9] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r7 = r1.recordDot
            android.util.Property r9 = android.view.View.SCALE_Y
            float[] r10 = new float[r14]
            r11 = 0
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r6[r3] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordDot r7 = r1.recordDot
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r14]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r9 = 4
            r6[r9] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r7 = r1.recordCircle
            android.util.Property<org.telegram.ui.Components.ChatActivityEnterView$RecordCircle, java.lang.Float> r9 = r1.recordCircleScale
            float[] r10 = new float[r14]
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r9 = 5
            r6[r9] = r7
            android.widget.FrameLayout r7 = r1.audioVideoButtonContainer
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r14]
            r11 = 1065353216(0x3var_, float:1.0)
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r9 = 6
            r6[r9] = r7
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r7 = r1.recordTimerView
            android.util.Property r9 = android.view.View.ALPHA
            float[] r10 = new float[r14]
            r11 = 0
            r10[r4] = r11
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r5 = 7
            r6[r5] = r7
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r5 = r1.recordCircle
            android.util.Property<org.telegram.ui.Components.ChatActivityEnterView$RecordCircle, java.lang.Float> r7 = r1.recordCircleScale
            float[] r9 = new float[r14]
            r9[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r9)
            r7 = 8
            r6[r7] = r5
            android.widget.FrameLayout r5 = r1.audioVideoButtonContainer
            android.util.Property r7 = android.view.View.ALPHA
            float[] r9 = new float[r14]
            r10 = 1065353216(0x3var_, float:1.0)
            r9[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r9)
            r7 = 9
            r6[r7] = r5
            org.telegram.ui.Components.EditTextCaption r5 = r1.messageEditText
            android.util.Property r7 = android.view.View.ALPHA
            float[] r9 = new float[r14]
            r9[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r9)
            r6[r18] = r5
            org.telegram.ui.Components.EditTextCaption r5 = r1.messageEditText
            android.util.Property r7 = android.view.View.TRANSLATION_X
            float[] r9 = new float[r14]
            r11 = 0
            r9[r4] = r11
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r9)
            r7 = 11
            r6[r7] = r5
            org.telegram.ui.Components.ChatActivityEnterView$RecordCircle r5 = r1.recordCircle
            float[] r7 = new float[r14]
            r7[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r12, r7)
            r6[r8] = r5
            r0.playTogether(r6)
            org.telegram.ui.Components.BotCommandsMenuView r0 = r1.botCommandsMenuButton
            if (r0 == 0) goto L_0x0baa
            android.animation.AnimatorSet r5 = r1.runningAnimationAudio
            android.animation.Animator[] r3 = new android.animation.Animator[r3]
            android.util.Property r6 = android.view.View.SCALE_Y
            float[] r7 = new float[r14]
            r7[r4] = r10
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r6, r7)
            r3[r4] = r0
            org.telegram.ui.Components.BotCommandsMenuView r0 = r1.botCommandsMenuButton
            android.util.Property r6 = android.view.View.SCALE_X
            float[] r7 = new float[r14]
            r7[r4] = r10
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r6, r7)
            r3[r14] = r0
            org.telegram.ui.Components.BotCommandsMenuView r0 = r1.botCommandsMenuButton
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r14]
            r7[r4] = r10
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r6, r7)
            r6 = 2
            r3[r6] = r0
            r5.playTogether(r3)
        L_0x0baa:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r1.audioVideoSendButton
            if (r0 == 0) goto L_0x0bdb
            r0.setScaleX(r10)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r1.audioVideoSendButton
            r0.setScaleY(r10)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            android.animation.Animator[] r3 = new android.animation.Animator[r14]
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r5 = r1.audioVideoSendButton
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r14]
            r7[r4] = r10
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r3[r4] = r5
            r0.playTogether(r3)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r1.audioVideoSendButton
            boolean r3 = r24.isInVideoMode()
            if (r3 == 0) goto L_0x0bd6
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r3 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.VIDEO
            goto L_0x0bd8
        L_0x0bd6:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r3 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.VOICE
        L_0x0bd8:
            r0.setState(r3, r14)
        L_0x0bdb:
            android.widget.ImageView r0 = r1.scheduledButton
            if (r0 == 0) goto L_0x0CLASSNAME
            android.animation.AnimatorSet r3 = r1.runningAnimationAudio
            r5 = 2
            android.animation.Animator[] r6 = new android.animation.Animator[r5]
            android.util.Property r5 = android.view.View.TRANSLATION_X
            float[] r7 = new float[r14]
            r8 = 0
            r7[r4] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r5, r7)
            r6[r4] = r0
            android.widget.ImageView r0 = r1.scheduledButton
            android.util.Property r5 = android.view.View.ALPHA
            float[] r7 = new float[r14]
            r8 = 1065353216(0x3var_, float:1.0)
            r7[r4] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r5, r7)
            r6[r14] = r0
            r3.playTogether(r6)
        L_0x0CLASSNAME:
            android.widget.LinearLayout r0 = r1.attachLayout
            if (r0 == 0) goto L_0x0c2d
            android.animation.AnimatorSet r3 = r1.runningAnimationAudio
            r5 = 2
            android.animation.Animator[] r5 = new android.animation.Animator[r5]
            android.util.Property r6 = android.view.View.TRANSLATION_X
            float[] r7 = new float[r14]
            r8 = 0
            r7[r4] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r6, r7)
            r5[r4] = r0
            android.widget.LinearLayout r0 = r1.attachLayout
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r14]
            r8 = 1065353216(0x3var_, float:1.0)
            r7[r4] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r6, r7)
            r5[r14] = r0
            r3.playTogether(r5)
        L_0x0c2d:
            r1.recordIsCanceled = r14
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            r3 = 150(0x96, double:7.4E-322)
            r0.setDuration(r3)
        L_0x0CLASSNAME:
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            org.telegram.ui.Components.ChatActivityEnterView$51 r3 = new org.telegram.ui.Components.ChatActivityEnterView$51
            r3.<init>(r2)
            r0.addListener(r3)
            android.animation.AnimatorSet r0 = r1.runningAnimationAudio
            r0.start()
            org.telegram.ui.Components.ChatActivityEnterView$TimerView r0 = r1.recordTimerView
            r0.stop()
        L_0x0c4a:
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r0 = r1.delegate
            r0.onAudioVideoInterfaceUpdated()
            r24.updateSendAsButton()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.updateRecordInterface(int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateRecordInterface$42(ValueAnimator valueAnimator) {
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
                if (messageObject2 != null && DialogObject.isChatDialog(this.dialog_id)) {
                    tLRPC$User = this.accountInstance.getMessagesController().getUser(Long.valueOf(messageObject2.messageOwner.from_id.user_id));
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
                if (messageObject2 != null && DialogObject.isChatDialog(this.dialog_id)) {
                    tLRPC$User = this.accountInstance.getMessagesController().getUser(Long.valueOf(messageObject2.messageOwner.from_id.user_id));
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

    public void setEditingMessageObject(MessageObject messageObject, boolean z) {
        MessageObject messageObject2;
        boolean z2;
        CharSequence charSequence;
        ArrayList<TLRPC$MessageEntity> arrayList;
        MessageObject messageObject3 = messageObject;
        boolean z3 = z;
        if (this.audioToSend == null && this.videoToSendMessageObject == null && (messageObject2 = this.editingMessageObject) != messageObject3) {
            int i = 1;
            boolean z4 = messageObject2 != null;
            this.editingMessageObject = messageObject3;
            this.editingCaption = z3;
            if (messageObject3 != null) {
                AnimatorSet animatorSet = this.doneButtonAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.doneButtonAnimation = null;
                }
                this.doneButtonContainer.setVisibility(0);
                this.doneButtonImage.setScaleX(0.1f);
                this.doneButtonImage.setScaleY(0.1f);
                this.doneButtonImage.setAlpha(0.0f);
                this.doneButtonImage.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                if (z3) {
                    this.currentLimit = this.accountInstance.getMessagesController().maxCaptionLength;
                    charSequence = this.editingMessageObject.caption;
                } else {
                    this.currentLimit = this.accountInstance.getMessagesController().maxMessageLength;
                    charSequence = this.editingMessageObject.messageText;
                }
                String str = "";
                CharSequence charSequence2 = str;
                if (charSequence != null) {
                    ArrayList<TLRPC$MessageEntity> arrayList2 = this.editingMessageObject.messageOwner.entities;
                    MediaDataController.sortEntities(arrayList2);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
                    Object[] spans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), Object.class);
                    if (spans != null && spans.length > 0) {
                        for (Object removeSpan : spans) {
                            spannableStringBuilder.removeSpan(removeSpan);
                        }
                    }
                    if (arrayList2 != null) {
                        int i2 = 0;
                        while (i2 < arrayList2.size()) {
                            try {
                                TLRPC$MessageEntity tLRPC$MessageEntity = arrayList2.get(i2);
                                if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length > spannableStringBuilder.length()) {
                                    arrayList = arrayList2;
                                } else if (tLRPC$MessageEntity instanceof TLRPC$TL_inputMessageEntityMentionName) {
                                    if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length < spannableStringBuilder.length() && spannableStringBuilder.charAt(tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length) == ' ') {
                                        tLRPC$MessageEntity.length += i;
                                    }
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(str);
                                    arrayList = arrayList2;
                                    sb.append(((TLRPC$TL_inputMessageEntityMentionName) tLRPC$MessageEntity).user_id.user_id);
                                    URLSpanUserMention uRLSpanUserMention = new URLSpanUserMention(sb.toString(), 3);
                                    int i3 = tLRPC$MessageEntity.offset;
                                    spannableStringBuilder.setSpan(uRLSpanUserMention, i3, tLRPC$MessageEntity.length + i3, 33);
                                } else {
                                    arrayList = arrayList2;
                                    if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityMentionName) {
                                        if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length < spannableStringBuilder.length() && spannableStringBuilder.charAt(tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length) == ' ') {
                                            tLRPC$MessageEntity.length += i;
                                        }
                                        URLSpanUserMention uRLSpanUserMention2 = new URLSpanUserMention(str + ((TLRPC$TL_messageEntityMentionName) tLRPC$MessageEntity).user_id, 3);
                                        int i4 = tLRPC$MessageEntity.offset;
                                        spannableStringBuilder.setSpan(uRLSpanUserMention2, i4, tLRPC$MessageEntity.length + i4, 33);
                                    } else {
                                        if (!(tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCode)) {
                                            if (!(tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityPre)) {
                                                if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityBold) {
                                                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                                                    textStyleRun.flags |= 1;
                                                    TextStyleSpan textStyleSpan = new TextStyleSpan(textStyleRun);
                                                    int i5 = tLRPC$MessageEntity.offset;
                                                    MediaDataController.addStyleToText(textStyleSpan, i5, tLRPC$MessageEntity.length + i5, spannableStringBuilder, true);
                                                } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityItalic) {
                                                    TextStyleSpan.TextStyleRun textStyleRun2 = new TextStyleSpan.TextStyleRun();
                                                    textStyleRun2.flags |= 2;
                                                    TextStyleSpan textStyleSpan2 = new TextStyleSpan(textStyleRun2);
                                                    int i6 = tLRPC$MessageEntity.offset;
                                                    MediaDataController.addStyleToText(textStyleSpan2, i6, tLRPC$MessageEntity.length + i6, spannableStringBuilder, true);
                                                } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityStrike) {
                                                    TextStyleSpan.TextStyleRun textStyleRun3 = new TextStyleSpan.TextStyleRun();
                                                    textStyleRun3.flags |= 8;
                                                    TextStyleSpan textStyleSpan3 = new TextStyleSpan(textStyleRun3);
                                                    int i7 = tLRPC$MessageEntity.offset;
                                                    MediaDataController.addStyleToText(textStyleSpan3, i7, tLRPC$MessageEntity.length + i7, spannableStringBuilder, true);
                                                } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityUnderline) {
                                                    TextStyleSpan.TextStyleRun textStyleRun4 = new TextStyleSpan.TextStyleRun();
                                                    textStyleRun4.flags |= 16;
                                                    TextStyleSpan textStyleSpan4 = new TextStyleSpan(textStyleRun4);
                                                    int i8 = tLRPC$MessageEntity.offset;
                                                    MediaDataController.addStyleToText(textStyleSpan4, i8, tLRPC$MessageEntity.length + i8, spannableStringBuilder, true);
                                                } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityTextUrl) {
                                                    URLSpanReplacement uRLSpanReplacement = new URLSpanReplacement(tLRPC$MessageEntity.url);
                                                    int i9 = tLRPC$MessageEntity.offset;
                                                    spannableStringBuilder.setSpan(uRLSpanReplacement, i9, tLRPC$MessageEntity.length + i9, 33);
                                                } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntitySpoiler) {
                                                    TextStyleSpan.TextStyleRun textStyleRun5 = new TextStyleSpan.TextStyleRun();
                                                    textStyleRun5.flags |= 256;
                                                    TextStyleSpan textStyleSpan5 = new TextStyleSpan(textStyleRun5);
                                                    int i10 = tLRPC$MessageEntity.offset;
                                                    MediaDataController.addStyleToText(textStyleSpan5, i10, tLRPC$MessageEntity.length + i10, spannableStringBuilder, true);
                                                } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCustomEmoji) {
                                                    AnimatedEmojiSpan animatedEmojiSpan = new AnimatedEmojiSpan(((TLRPC$TL_messageEntityCustomEmoji) tLRPC$MessageEntity).document_id, this.messageEditText.getPaint().getFontMetricsInt());
                                                    int i11 = tLRPC$MessageEntity.offset;
                                                    spannableStringBuilder.setSpan(animatedEmojiSpan, i11, tLRPC$MessageEntity.length + i11, 33);
                                                }
                                            }
                                        }
                                        TextStyleSpan.TextStyleRun textStyleRun6 = new TextStyleSpan.TextStyleRun();
                                        textStyleRun6.flags |= 4;
                                        TextStyleSpan textStyleSpan6 = new TextStyleSpan(textStyleRun6);
                                        int i12 = tLRPC$MessageEntity.offset;
                                        MediaDataController.addStyleToText(textStyleSpan6, i12, tLRPC$MessageEntity.length + i12, spannableStringBuilder, true);
                                    }
                                }
                                i2++;
                                arrayList2 = arrayList;
                                i = 1;
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        }
                    }
                    charSequence2 = Emoji.replaceEmoji(new SpannableStringBuilder(spannableStringBuilder), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                }
                if (this.draftMessage == null && !z4) {
                    this.draftMessage = this.messageEditText.length() > 0 ? this.messageEditText.getText() : null;
                    this.draftSearchWebpage = this.messageWebPageSearch;
                }
                this.messageWebPageSearch = this.editingMessageObject.messageOwner.media instanceof TLRPC$TL_messageMediaWebPage;
                if (!this.keyboardVisible) {
                    ChatActivityEnterView$$ExternalSyntheticLambda47 chatActivityEnterView$$ExternalSyntheticLambda47 = new ChatActivityEnterView$$ExternalSyntheticLambda47(this, charSequence2);
                    this.setTextFieldRunnable = chatActivityEnterView$$ExternalSyntheticLambda47;
                    AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda47, 200);
                } else {
                    Runnable runnable = this.setTextFieldRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                        this.setTextFieldRunnable = null;
                    }
                    setFieldText(charSequence2);
                }
                this.messageEditText.requestFocus();
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
                z2 = true;
            } else {
                Runnable runnable2 = this.setTextFieldRunnable;
                if (runnable2 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable2);
                    this.setTextFieldRunnable = null;
                }
                this.doneButtonContainer.setVisibility(8);
                this.currentLimit = -1;
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
                this.messageEditText.setText(this.draftMessage);
                this.draftMessage = null;
                this.messageWebPageSearch = this.draftSearchWebpage;
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setSelection(editTextCaption.length());
                if (getVisibility() == 0) {
                    this.delegate.onAttachButtonShow();
                }
                z2 = true;
                updateFieldRight(1);
            }
            updateFieldHint(z2);
            updateSendAsButton(z2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setEditingMessageObject$43(CharSequence charSequence) {
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

    public View getEmojiButton() {
        return this.emojiButton;
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
            int childCount = actionBarPopupWindowLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.sendPopupLayout.getChildAt(i);
                if (childAt instanceof ActionBarMenuSubItem) {
                    ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) childAt;
                    actionBarMenuSubItem.setColors(getThemedColor("actionBarDefaultSubmenuItem"), getThemedColor("actionBarDefaultSubmenuItemIcon"));
                    actionBarMenuSubItem.setSelectorColor(getThemedColor("dialogButtonSelector"));
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
        int themedColor = getThemedColor("chat_messagePanelVoicePressed");
        this.doneCheckDrawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(themedColor, (int) (((float) Color.alpha(themedColor)) * ((this.doneButtonEnabledProgress * 0.42f) + 0.58f))), PorterDuff.Mode.MULTIPLY));
        BotCommandsMenuContainer botCommandsMenuContainer2 = this.botCommandsMenuContainer;
        if (botCommandsMenuContainer2 != null) {
            botCommandsMenuContainer2.updateColors();
        }
        BotKeyboardView botKeyboardView2 = this.botKeyboardView;
        if (botKeyboardView2 != null) {
            botKeyboardView2.updateColors();
        }
        this.audioVideoSendButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.SRC_IN));
        this.emojiButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.SRC_IN));
        if (Build.VERSION.SDK_INT >= 21) {
            this.emojiButton.setBackground(Theme.createSelectorDrawable(getThemedColor("listSelectorSDK21")));
        }
    }

    private void updateRecordedDeleteIconColors() {
        int themedColor = getThemedColor("chat_recordedVoiceDot");
        int themedColor2 = getThemedColor("chat_messagePanelBackground");
        int themedColor3 = getThemedColor("chat_messagePanelVoiceDelete");
        this.recordDeleteImageView.setLayerColor("Cup Red.**", themedColor);
        this.recordDeleteImageView.setLayerColor("Box Red.**", themedColor);
        this.recordDeleteImageView.setLayerColor("Cup Grey.**", themedColor3);
        this.recordDeleteImageView.setLayerColor("Box Grey.**", themedColor3);
        this.recordDeleteImageView.setLayerColor("Line 1.**", themedColor2);
        this.recordDeleteImageView.setLayerColor("Line 2.**", themedColor2);
        this.recordDeleteImageView.setLayerColor("Line 3.**", themedColor2);
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
                ChatActivityEnterView$$ExternalSyntheticLambda46 chatActivityEnterView$$ExternalSyntheticLambda46 = new ChatActivityEnterView$$ExternalSyntheticLambda46(this);
                this.focusRunnable = chatActivityEnterView$$ExternalSyntheticLambda46;
                AndroidUtilities.runOnUIThread(chatActivityEnterView$$ExternalSyntheticLambda46, 600);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setFieldFocused$44() {
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

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0044, code lost:
        r3 = r0.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateScheduleButton(boolean r14) {
        /*
            r13 = this;
            long r0 = r13.dialog_id
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r0)
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x008e
            org.telegram.messenger.AccountInstance r0 = r13.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r3 = r13.dialog_id
            long r3 = -r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
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
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r3 == 0) goto L_0x0052
            boolean r3 = r0.creator
            if (r3 != 0) goto L_0x004c
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.admin_rights
            if (r3 == 0) goto L_0x0052
            boolean r3 = r3.post_messages
            if (r3 == 0) goto L_0x0052
        L_0x004c:
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0052
            r0 = 1
            goto L_0x0053
        L_0x0052:
            r0 = 0
        L_0x0053:
            r13.canWriteToChannel = r0
            android.widget.ImageView r3 = r13.notifyButton
            if (r3 == 0) goto L_0x007c
            org.telegram.ui.Components.CrossOutDrawable r3 = r13.notifySilentDrawable
            if (r3 != 0) goto L_0x006d
            org.telegram.ui.Components.CrossOutDrawable r3 = new org.telegram.ui.Components.CrossOutDrawable
            android.content.Context r4 = r13.getContext()
            r5 = 2131165540(0x7var_, float:1.79453E38)
            java.lang.String r6 = "chat_messagePanelIcons"
            r3.<init>(r4, r5, r6)
            r13.notifySilentDrawable = r3
        L_0x006d:
            org.telegram.ui.Components.CrossOutDrawable r3 = r13.notifySilentDrawable
            boolean r4 = r13.silent
            r3.setCrossOut(r4, r2)
            android.widget.ImageView r3 = r13.notifyButton
            org.telegram.ui.Components.CrossOutDrawable r4 = r13.notifySilentDrawable
            r3.setImageDrawable(r4)
            goto L_0x007d
        L_0x007c:
            r0 = 0
        L_0x007d:
            android.widget.LinearLayout r3 = r13.attachLayout
            if (r3 == 0) goto L_0x008f
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0089
            r3 = 1
            goto L_0x008a
        L_0x0089:
            r3 = 0
        L_0x008a:
            r13.updateFieldRight(r3)
            goto L_0x008f
        L_0x008e:
            r0 = 0
        L_0x008f:
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r3 = r13.delegate
            if (r3 == 0) goto L_0x00a3
            boolean r3 = r13.isInScheduleMode()
            if (r3 != 0) goto L_0x00a3
            org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate r3 = r13.delegate
            boolean r3 = r3.hasScheduledMessages()
            if (r3 == 0) goto L_0x00a3
            r3 = 1
            goto L_0x00a4
        L_0x00a3:
            r3 = 0
        L_0x00a4:
            if (r3 == 0) goto L_0x00b0
            boolean r4 = r13.scheduleButtonHidden
            if (r4 != 0) goto L_0x00b0
            boolean r4 = r13.recordingAudioVideo
            if (r4 != 0) goto L_0x00b0
            r4 = 1
            goto L_0x00b1
        L_0x00b0:
            r4 = 0
        L_0x00b1:
            android.widget.ImageView r5 = r13.scheduledButton
            r6 = 1119879168(0x42CLASSNAME, float:96.0)
            r7 = 1111490560(0x42400000, float:48.0)
            r8 = 0
            r9 = 8
            if (r5 == 0) goto L_0x011e
            java.lang.Object r5 = r5.getTag()
            if (r5 == 0) goto L_0x00c4
            if (r4 != 0) goto L_0x00ce
        L_0x00c4:
            android.widget.ImageView r5 = r13.scheduledButton
            java.lang.Object r5 = r5.getTag()
            if (r5 != 0) goto L_0x0111
            if (r4 != 0) goto L_0x0111
        L_0x00ce:
            android.widget.ImageView r14 = r13.notifyButton
            if (r14 == 0) goto L_0x0110
            if (r3 != 0) goto L_0x00df
            if (r0 == 0) goto L_0x00df
            android.widget.ImageView r14 = r13.scheduledButton
            int r14 = r14.getVisibility()
            if (r14 == 0) goto L_0x00df
            goto L_0x00e1
        L_0x00df:
            r2 = 8
        L_0x00e1:
            android.widget.ImageView r14 = r13.notifyButton
            int r14 = r14.getVisibility()
            if (r2 == r14) goto L_0x0110
            android.widget.ImageView r14 = r13.notifyButton
            r14.setVisibility(r2)
            android.widget.LinearLayout r14 = r13.attachLayout
            if (r14 == 0) goto L_0x0110
            android.widget.ImageView r0 = r13.botButton
            if (r0 == 0) goto L_0x00fc
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x0108
        L_0x00fc:
            android.widget.ImageView r0 = r13.notifyButton
            if (r0 == 0) goto L_0x0106
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x0108
        L_0x0106:
            r6 = 1111490560(0x42400000, float:48.0)
        L_0x0108:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r0 = (float) r0
            r14.setPivotX(r0)
        L_0x0110:
            return
        L_0x0111:
            android.widget.ImageView r3 = r13.scheduledButton
            if (r4 == 0) goto L_0x011a
            java.lang.Integer r5 = java.lang.Integer.valueOf(r1)
            goto L_0x011b
        L_0x011a:
            r5 = r8
        L_0x011b:
            r3.setTag(r5)
        L_0x011e:
            android.animation.AnimatorSet r3 = r13.scheduledButtonAnimation
            if (r3 == 0) goto L_0x0127
            r3.cancel()
            r13.scheduledButtonAnimation = r8
        L_0x0127:
            r3 = 0
            r5 = 1036831949(0x3dcccccd, float:0.1)
            r8 = 1065353216(0x3var_, float:1.0)
            if (r14 == 0) goto L_0x01a7
            if (r0 == 0) goto L_0x0133
            goto L_0x01a7
        L_0x0133:
            android.widget.ImageView r14 = r13.scheduledButton
            if (r14 == 0) goto L_0x01e7
            if (r4 == 0) goto L_0x013c
            r14.setVisibility(r2)
        L_0x013c:
            android.widget.ImageView r14 = r13.scheduledButton
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            r14.setPivotX(r0)
            android.animation.AnimatorSet r14 = new android.animation.AnimatorSet
            r14.<init>()
            r13.scheduledButtonAnimation = r14
            r0 = 3
            android.animation.Animator[] r0 = new android.animation.Animator[r0]
            android.widget.ImageView r10 = r13.scheduledButton
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r1]
            if (r4 == 0) goto L_0x015c
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x015c:
            r12[r2] = r3
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r0[r2] = r3
            android.widget.ImageView r3 = r13.scheduledButton
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r1]
            if (r4 == 0) goto L_0x016f
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0172
        L_0x016f:
            r12 = 1036831949(0x3dcccccd, float:0.1)
        L_0x0172:
            r11[r2] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r10, r11)
            r0[r1] = r3
            r3 = 2
            android.widget.ImageView r10 = r13.scheduledButton
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r1 = new float[r1]
            if (r4 == 0) goto L_0x0185
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0185:
            r1[r2] = r5
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r10, r11, r1)
            r0[r3] = r1
            r14.playTogether(r0)
            android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
            r0 = 180(0xb4, double:8.9E-322)
            r14.setDuration(r0)
            android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
            org.telegram.ui.Components.ChatActivityEnterView$52 r0 = new org.telegram.ui.Components.ChatActivityEnterView$52
            r0.<init>(r4)
            r14.addListener(r0)
            android.animation.AnimatorSet r14 = r13.scheduledButtonAnimation
            r14.start()
            goto L_0x01e7
        L_0x01a7:
            android.widget.ImageView r14 = r13.scheduledButton
            if (r14 == 0) goto L_0x01e7
            if (r4 == 0) goto L_0x01af
            r1 = 0
            goto L_0x01b1
        L_0x01af:
            r1 = 8
        L_0x01b1:
            r14.setVisibility(r1)
            android.widget.ImageView r14 = r13.scheduledButton
            if (r4 == 0) goto L_0x01ba
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x01ba:
            r14.setAlpha(r3)
            android.widget.ImageView r14 = r13.scheduledButton
            if (r4 == 0) goto L_0x01c4
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x01c7
        L_0x01c4:
            r1 = 1036831949(0x3dcccccd, float:0.1)
        L_0x01c7:
            r14.setScaleX(r1)
            android.widget.ImageView r14 = r13.scheduledButton
            if (r4 == 0) goto L_0x01d0
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x01d0:
            r14.setScaleY(r5)
            android.widget.ImageView r14 = r13.notifyButton
            if (r14 == 0) goto L_0x01e7
            if (r0 == 0) goto L_0x01e2
            android.widget.ImageView r0 = r13.scheduledButton
            int r0 = r0.getVisibility()
            if (r0 == 0) goto L_0x01e2
            goto L_0x01e4
        L_0x01e2:
            r2 = 8
        L_0x01e4:
            r14.setVisibility(r2)
        L_0x01e7:
            android.widget.LinearLayout r14 = r13.attachLayout
            if (r14 == 0) goto L_0x0209
            android.widget.ImageView r0 = r13.botButton
            if (r0 == 0) goto L_0x01f5
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x0201
        L_0x01f5:
            android.widget.ImageView r0 = r13.notifyButton
            if (r0 == 0) goto L_0x01ff
            int r0 = r0.getVisibility()
            if (r0 != r9) goto L_0x0201
        L_0x01ff:
            r6 = 1111490560(0x42400000, float:48.0)
        L_0x0201:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r0 = (float) r0
            r14.setPivotX(r0)
        L_0x0209:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.updateScheduleButton(boolean):void");
    }

    public void updateSendAsButton() {
        updateSendAsButton(true);
    }

    public void updateSendAsButton(boolean z) {
        float f;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && this.delegate != null) {
            TLRPC$ChatFull chatFull = chatActivity.getMessagesController().getChatFull(-this.dialog_id);
            TLRPC$Peer tLRPC$Peer = chatFull != null ? chatFull.default_send_as : null;
            int i = 0;
            if (tLRPC$Peer == null && this.delegate.getSendAsPeers() != null && !this.delegate.getSendAsPeers().peers.isEmpty()) {
                tLRPC$Peer = this.delegate.getSendAsPeers().peers.get(0);
            }
            if (tLRPC$Peer != null) {
                if (tLRPC$Peer.channel_id != 0) {
                    TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(tLRPC$Peer.channel_id));
                    if (chat != null) {
                        this.senderSelectView.setAvatar(chat);
                    }
                } else {
                    TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$Peer.user_id));
                    if (user != null) {
                        this.senderSelectView.setAvatar(user);
                    }
                }
            }
            boolean z2 = this.senderSelectView.getVisibility() == 0;
            boolean z3 = tLRPC$Peer != null && (this.delegate.getSendAsPeers() == null || this.delegate.getSendAsPeers().peers.size() > 1) && !isEditingMessage() && !isRecordingAudioVideo() && this.recordedAudioPanel.getVisibility() != 0;
            int dp = AndroidUtilities.dp(2.0f);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.senderSelectView.getLayoutParams();
            float f2 = 0.0f;
            float f3 = z3 ? 0.0f : 1.0f;
            float f4 = z3 ? (float) (((-this.senderSelectView.getLayoutParams().width) - marginLayoutParams.leftMargin) - dp) : 0.0f;
            float f5 = z3 ? 1.0f : 0.0f;
            if (z3) {
                f = 0.0f;
            } else {
                f = (float) (((-this.senderSelectView.getLayoutParams().width) - marginLayoutParams.leftMargin) - dp);
            }
            if (z2 != z3) {
                ValueAnimator valueAnimator = (ValueAnimator) this.senderSelectView.getTag();
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.senderSelectView.setTag((Object) null);
                }
                if (this.parentFragment.getOtherSameChatsDiff() != 0 || !this.parentFragment.fragmentOpened || !z) {
                    SenderSelectView senderSelectView2 = this.senderSelectView;
                    if (!z3) {
                        i = 8;
                    }
                    senderSelectView2.setVisibility(i);
                    this.senderSelectView.setTranslationX(f);
                    if (z3) {
                        f2 = f;
                    }
                    this.emojiButton.setTranslationX(f2);
                    this.messageEditText.setTranslationX(f2);
                    this.senderSelectView.setAlpha(f5);
                    this.senderSelectView.setTag((Object) null);
                    return;
                }
                ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(150);
                this.senderSelectView.setTranslationX(f4);
                this.messageEditText.setTranslationX(this.senderSelectView.getTranslationX());
                final float f6 = f4;
                duration.addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda5(this, f3, f5, f6, f));
                final boolean z4 = z3;
                final float f7 = f3;
                final float f8 = f5;
                final float f9 = f;
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        if (z4) {
                            ChatActivityEnterView.this.senderSelectView.setVisibility(0);
                        }
                        ChatActivityEnterView.this.senderSelectView.setAlpha(f7);
                        ChatActivityEnterView.this.senderSelectView.setTranslationX(f6);
                        ChatActivityEnterView.this.emojiButton.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        chatActivityEnterView.messageEditText.setTranslationX(chatActivityEnterView.senderSelectView.getTranslationX());
                        if (ChatActivityEnterView.this.botCommandsMenuButton.getTag() == null) {
                            ChatActivityEnterView.this.animationParamsX.clear();
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (!z4) {
                            ChatActivityEnterView.this.senderSelectView.setVisibility(8);
                            ChatActivityEnterView.this.emojiButton.setTranslationX(0.0f);
                            ChatActivityEnterView.this.messageEditText.setTranslationX(0.0f);
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (z4) {
                            ChatActivityEnterView.this.senderSelectView.setVisibility(0);
                        } else {
                            ChatActivityEnterView.this.senderSelectView.setVisibility(8);
                        }
                        ChatActivityEnterView.this.senderSelectView.setAlpha(f8);
                        ChatActivityEnterView.this.senderSelectView.setTranslationX(f9);
                        ChatActivityEnterView.this.emojiButton.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        chatActivityEnterView.messageEditText.setTranslationX(chatActivityEnterView.senderSelectView.getTranslationX());
                        ChatActivityEnterView.this.requestLayout();
                    }
                });
                duration.start();
                this.senderSelectView.setTag(duration);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSendAsButton$45(float f, float f2, float f3, float f4, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.senderSelectView.setAlpha(f + ((f2 - f) * floatValue));
        this.senderSelectView.setTranslationX(f3 + ((f4 - f3) * floatValue));
        this.emojiButton.setTranslationX(this.senderSelectView.getTranslationX());
        this.messageEditText.setTranslationX(this.senderSelectView.getTranslationX());
    }

    public boolean onBotWebViewBackPressed() {
        BotWebViewMenuContainer botWebViewMenuContainer2 = this.botWebViewMenuContainer;
        return botWebViewMenuContainer2 != null && botWebViewMenuContainer2.onBackPressed();
    }

    public boolean hasBotWebView() {
        return this.botMenuButtonType == BotMenuButtonType.WEB_VIEW;
    }

    private void updateBotButton(boolean z) {
        ImageView imageView;
        if (this.botButton != null) {
            if (!this.parentFragment.openAnimationEnded) {
                z = false;
            }
            boolean hasBotWebView = hasBotWebView();
            boolean z2 = this.botMenuButtonType != BotMenuButtonType.NO_BUTTON && this.dialog_id > 0;
            boolean z3 = this.botButton.getVisibility() == 0;
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
            } else if (!z2) {
                this.botButtonDrawable.setIcon(NUM, true);
                this.botButton.setContentDescription(LocaleController.getString("AccDescrBotCommands", NUM));
                this.botButton.setVisibility(0);
            } else {
                this.botButton.setVisibility(8);
            }
            BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
            boolean z4 = botCommandsMenuView.isWebView;
            botCommandsMenuView.setWebView(this.botMenuButtonType == BotMenuButtonType.WEB_VIEW);
            boolean menuText = this.botCommandsMenuButton.setMenuText(this.botMenuButtonType == BotMenuButtonType.COMMANDS ? LocaleController.getString(NUM) : this.botMenuWebViewTitle);
            AndroidUtilities.updateViewVisibilityAnimated(this.botCommandsMenuButton, z2, 0.5f, z);
            if ((((this.botButton.getVisibility() == 0) == z3 && !menuText && z4 == this.botCommandsMenuButton.isWebView) ? false : true) && z) {
                beginDelayedTransition();
                boolean z5 = this.botButton.getVisibility() == 0;
                if (z5 != z3) {
                    this.botButton.setVisibility(0);
                    if (z5) {
                        this.botButton.setAlpha(0.0f);
                        this.botButton.setScaleX(0.1f);
                        this.botButton.setScaleY(0.1f);
                    } else if (!z5) {
                        this.botButton.setAlpha(1.0f);
                        this.botButton.setScaleX(1.0f);
                        this.botButton.setScaleY(1.0f);
                    }
                    AndroidUtilities.updateViewVisibilityAnimated(this.botButton, z5, 0.1f, true);
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
        } catch (Throwable unused) {
            return false;
        }
    }

    public void updateBotWebView(boolean z) {
        this.botCommandsMenuButton.setWebView(hasBotWebView());
        updateBotButton(z);
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

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00a0, code lost:
        if (r7.getInt("answered_" + r5.dialog_id, 0) == r6.getId()) goto L_0x00a4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x006a  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setButtons(org.telegram.messenger.MessageObject r6, boolean r7) {
        /*
            r5 = this;
            org.telegram.messenger.MessageObject r0 = r5.replyingMessageObject
            if (r0 == 0) goto L_0x000d
            org.telegram.messenger.MessageObject r1 = r5.botButtonsMessageObject
            if (r0 != r1) goto L_0x000d
            if (r0 == r6) goto L_0x000d
            r5.botMessageObject = r6
            return
        L_0x000d:
            android.widget.ImageView r0 = r5.botButton
            if (r0 == 0) goto L_0x00d7
            org.telegram.messenger.MessageObject r0 = r5.botButtonsMessageObject
            if (r0 == 0) goto L_0x0017
            if (r0 == r6) goto L_0x00d7
        L_0x0017:
            if (r0 != 0) goto L_0x001d
            if (r6 != 0) goto L_0x001d
            goto L_0x00d7
        L_0x001d:
            org.telegram.ui.Components.BotKeyboardView r0 = r5.botKeyboardView
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x004b
            org.telegram.ui.Components.ChatActivityEnterView$54 r0 = new org.telegram.ui.Components.ChatActivityEnterView$54
            android.app.Activity r3 = r5.parentActivity
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r5.resourcesProvider
            r0.<init>(r3, r4)
            r5.botKeyboardView = r0
            r3 = 8
            r0.setVisibility(r3)
            r5.botKeyboardViewVisible = r1
            org.telegram.ui.Components.BotKeyboardView r0 = r5.botKeyboardView
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda57 r3 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda57
            r3.<init>(r5)
            r0.setDelegate(r3)
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r5.sizeNotifierLayout
            org.telegram.ui.Components.BotKeyboardView r3 = r5.botKeyboardView
            int r4 = r0.getChildCount()
            int r4 = r4 - r2
            r0.addView(r3, r4)
        L_0x004b:
            r5.botButtonsMessageObject = r6
            if (r6 == 0) goto L_0x005a
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup
            if (r3 == 0) goto L_0x005a
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = (org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup) r0
            goto L_0x005b
        L_0x005a:
            r0 = 0
        L_0x005b:
            r5.botReplyMarkup = r0
            org.telegram.ui.Components.BotKeyboardView r0 = r5.botKeyboardView
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
            int r3 = r3.y
            if (r4 <= r3) goto L_0x006a
            int r3 = r5.keyboardHeightLand
            goto L_0x006c
        L_0x006a:
            int r3 = r5.keyboardHeight
        L_0x006c:
            r0.setPanelHeight(r3)
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = r5.botReplyMarkup
            if (r0 == 0) goto L_0x00bf
            int r7 = r5.currentAccount
            android.content.SharedPreferences r7 = org.telegram.messenger.MessagesController.getMainSettings(r7)
            org.telegram.messenger.MessageObject r0 = r5.botButtonsMessageObject
            org.telegram.messenger.MessageObject r3 = r5.replyingMessageObject
            if (r0 == r3) goto L_0x00a3
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = r5.botReplyMarkup
            boolean r0 = r0.single_use
            if (r0 == 0) goto L_0x00a3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "answered_"
            r0.append(r3)
            long r3 = r5.dialog_id
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            int r7 = r7.getInt(r0, r1)
            int r6 = r6.getId()
            if (r7 != r6) goto L_0x00a3
            goto L_0x00a4
        L_0x00a3:
            r1 = 1
        L_0x00a4:
            if (r1 == 0) goto L_0x00b7
            org.telegram.ui.Components.EditTextCaption r6 = r5.messageEditText
            int r6 = r6.length()
            if (r6 != 0) goto L_0x00b7
            boolean r6 = r5.isPopupShowing()
            if (r6 != 0) goto L_0x00b7
            r5.showPopup(r2, r2)
        L_0x00b7:
            org.telegram.ui.Components.BotKeyboardView r6 = r5.botKeyboardView
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r7 = r5.botReplyMarkup
            r6.setButtons(r7)
            goto L_0x00d4
        L_0x00bf:
            boolean r6 = r5.isPopupShowing()
            if (r6 == 0) goto L_0x00d4
            int r6 = r5.currentPopupContentType
            if (r6 != r2) goto L_0x00d4
            if (r7 == 0) goto L_0x00d1
            r5.clearBotButtonsOnKeyboardOpen = r2
            r5.openKeyboardInternal()
            goto L_0x00d4
        L_0x00d1:
            r5.showPopup(r1, r2)
        L_0x00d4:
            r5.updateBotButton(r2)
        L_0x00d7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setButtons(org.telegram.messenger.MessageObject, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setButtons$46(TLRPC$KeyboardButton tLRPC$KeyboardButton) {
        MessageObject messageObject = this.replyingMessageObject;
        if (messageObject == null) {
            messageObject = DialogObject.isChatDialog(this.dialog_id) ? this.botButtonsMessageObject : null;
        }
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 == null) {
            messageObject2 = this.botButtonsMessageObject;
        }
        boolean didPressedBotButton = didPressedBotButton(tLRPC$KeyboardButton, messageObject, messageObject2);
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
            AlertsCreator.showOpenUrlAlert(this.parentFragment, tLRPC$KeyboardButton2.url, false, true, this.resourcesProvider);
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
            } else if ((tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonWebView) || (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonSimpleWebView)) {
                TLRPC$Message tLRPC$Message = messageObject3.messageOwner;
                long j = tLRPC$Message.via_bot_id;
                if (j == 0) {
                    j = tLRPC$Message.from_id.user_id;
                }
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
                final MessageObject messageObject4 = messageObject2;
                final long j2 = j;
                final TLRPC$KeyboardButton tLRPC$KeyboardButton3 = tLRPC$KeyboardButton;
                AnonymousClass55 r25 = r0;
                final MessageObject messageObject5 = messageObject;
                AnonymousClass55 r0 = new Runnable() {
                    public void run() {
                        if (ChatActivityEnterView.this.sizeNotifierLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                            AndroidUtilities.hideKeyboard(ChatActivityEnterView.this);
                            AndroidUtilities.runOnUIThread(this, 150);
                            return;
                        }
                        BotWebViewSheet botWebViewSheet = new BotWebViewSheet(ChatActivityEnterView.this.getContext(), ChatActivityEnterView.this.resourcesProvider);
                        botWebViewSheet.setParentActivity(ChatActivityEnterView.this.parentActivity);
                        int access$2700 = ChatActivityEnterView.this.currentAccount;
                        long j = messageObject4.messageOwner.dialog_id;
                        long j2 = j2;
                        TLRPC$KeyboardButton tLRPC$KeyboardButton = tLRPC$KeyboardButton3;
                        String str = tLRPC$KeyboardButton.text;
                        String str2 = tLRPC$KeyboardButton.url;
                        boolean z = tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonSimpleWebView;
                        MessageObject messageObject = messageObject5;
                        botWebViewSheet.requestWebView(access$2700, j, j2, str, str2, z ? 1 : 0, messageObject != null ? messageObject.messageOwner.id : 0, false);
                        botWebViewSheet.show();
                    }
                };
                if (SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, j)) {
                    r25.run();
                } else {
                    new AlertDialog.Builder((Context) this.parentFragment.getParentActivity()).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BotOpenPageMessage", NUM, UserObject.getUserName(user)))).setPositiveButton(LocaleController.getString(NUM), new ChatActivityEnterView$$ExternalSyntheticLambda9(this, r25, j)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).show();
                }
            } else if (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonRequestGeoLocation) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new ChatActivityEnterView$$ExternalSyntheticLambda10(this, messageObject3, tLRPC$KeyboardButton2));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                this.parentFragment.showDialog(builder.create());
            } else if ((tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonCallback) || (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonGame) || (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonBuy) || (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonUrlAuth)) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCallback(true, messageObject3, tLRPC$KeyboardButton2, this.parentFragment);
            } else if (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonSwitchInline) {
                if (this.parentFragment.processSwitchButton((TLRPC$TL_keyboardButtonSwitchInline) tLRPC$KeyboardButton2)) {
                    return true;
                }
                if (tLRPC$KeyboardButton2.same_peer) {
                    TLRPC$Message tLRPC$Message2 = messageObject3.messageOwner;
                    long j3 = tLRPC$Message2.from_id.user_id;
                    long j4 = tLRPC$Message2.via_bot_id;
                    if (j4 != 0) {
                        j3 = j4;
                    }
                    TLRPC$User user2 = this.accountInstance.getMessagesController().getUser(Long.valueOf(j3));
                    if (user2 == null) {
                        return true;
                    }
                    setFieldText("@" + user2.username + " " + tLRPC$KeyboardButton2.query);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlySelect", true);
                    bundle.putInt("dialogsType", 1);
                    DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                    dialogsActivity.setDelegate(new ChatActivityEnterView$$ExternalSyntheticLambda60(this, messageObject3, tLRPC$KeyboardButton2));
                    this.parentFragment.presentFragment(dialogsActivity);
                }
            } else if ((tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonUserProfile) && MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$KeyboardButton2.user_id)) != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("user_id", tLRPC$KeyboardButton2.user_id);
                this.parentFragment.presentFragment(new ProfileActivity(bundle2));
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didPressedBotButton$47(Runnable runnable, long j, DialogInterface dialogInterface, int i) {
        runnable.run();
        SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, j, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didPressedBotButton$48(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, DialogInterface dialogInterface, int i) {
        if (Build.VERSION.SDK_INT < 23 || this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(messageObject, tLRPC$KeyboardButton);
            return;
        }
        this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
        this.pendingMessageObject = messageObject;
        this.pendingLocationButton = tLRPC$KeyboardButton;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didPressedBotButton$49(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        long j = tLRPC$Message.from_id.user_id;
        long j2 = tLRPC$Message.via_bot_id;
        if (j2 != 0) {
            j = j2;
        }
        TLRPC$User user = this.accountInstance.getMessagesController().getUser(Long.valueOf(j));
        if (user == null) {
            dialogsActivity.finishFragment();
            return;
        }
        long longValue = ((Long) arrayList.get(0)).longValue();
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        instance.saveDraft(longValue, 0, "@" + user.username + " " + tLRPC$KeyboardButton.query, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$Message) null, true);
        if (longValue == this.dialog_id) {
            dialogsActivity.finishFragment();
        } else if (!DialogObject.isEncryptedDialog(longValue)) {
            Bundle bundle = new Bundle();
            if (DialogObject.isUserDialog(longValue)) {
                bundle.putLong("user_id", longValue);
            } else {
                bundle.putLong("chat_id", -longValue);
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
            AnonymousClass56 r1 = new EmojiView(this.parentFragment, this.allowAnimatedEmoji, true, true, getContext(), true, this.info, this.sizeNotifierLayout, this.resourcesProvider) {
                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    if (ChatActivityEnterView.this.panelAnimation != null && ChatActivityEnterView.this.animatingContentType == 0) {
                        ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(f);
                    }
                }
            };
            this.emojiView = r1;
            r1.setAllow(this.allowStickers, this.allowGifs, true);
            this.emojiView.setVisibility(8);
            this.emojiView.setShowing(false);
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

                public void onCustomEmojiSelected(long j, String str) {
                    AndroidUtilities.runOnUIThread(new ChatActivityEnterView$57$$ExternalSyntheticLambda1(this, str, j));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onCustomEmojiSelected$0(String str, long j) {
                    int selectionEnd = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
                    if (selectionEnd < 0) {
                        selectionEnd = 0;
                    }
                    try {
                        int unused = ChatActivityEnterView.this.innerTextChange = 2;
                        if (str == null) {
                            str = "😀";
                        }
                        SpannableString spannableString = new SpannableString(str);
                        AnimatedEmojiSpan animatedEmojiSpan = new AnimatedEmojiSpan(j, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt());
                        animatedEmojiSpan.cacheType = 1;
                        spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
                        EditTextCaption editTextCaption = ChatActivityEnterView.this.messageEditText;
                        editTextCaption.setText(editTextCaption.getText().insert(selectionEnd, spannableString));
                        ChatActivityEnterView.this.messageEditText.setSelection(spannableString.length() + selectionEnd, selectionEnd + spannableString.length());
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
                                ChatActivityEnterView.this.setSearchingTypeInternal(0, true);
                                ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(tLRPC$Document));
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        ChatActivityEnterView.this.lambda$onStickerSelected$50(tLRPC$Document, str, obj, sendAnimationData, false, z, i);
                        if (DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id) && MessageObject.isGifDocument(tLRPC$Document)) {
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
                public void lambda$onGifSelected$1(View view, Object obj, String str, Object obj2, boolean z, int i) {
                    Object obj3 = obj;
                    Object obj4 = obj2;
                    int i2 = i;
                    if (isInScheduleMode() && i2 == 0) {
                        AlertsCreator.createScheduleDatePickerDialog((Context) ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatActivityEnterView$57$$ExternalSyntheticLambda2(this, view, obj, str, obj2), ChatActivityEnterView.this.resourcesProvider);
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
                            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(tLRPC$Document, (int) (System.currentTimeMillis() / 1000), true);
                            if (DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id)) {
                                ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj4, tLRPC$Document);
                            }
                        } else if (obj3 instanceof TLRPC$BotInlineResult) {
                            TLRPC$BotInlineResult tLRPC$BotInlineResult = (TLRPC$BotInlineResult) obj3;
                            if (tLRPC$BotInlineResult.document != null) {
                                MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(tLRPC$BotInlineResult.document, (int) (System.currentTimeMillis() / 1000), false);
                                if (DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id)) {
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
                                ChatActivityEnterView.this.setSearchingTypeInternal(0, true);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.resourcesProvider);
                        builder.setTitle(LocaleController.getString("ClearRecentEmojiTitle", NUM));
                        builder.setMessage(LocaleController.getString("ClearRecentEmojiText", NUM));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new ChatActivityEnterView$57$$ExternalSyntheticLambda0(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onClearEmojiRecent$2(DialogInterface dialogInterface, int i) {
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
                        ChatActivity access$2500 = ChatActivityEnterView.this.parentFragment;
                        Activity access$1600 = ChatActivityEnterView.this.parentActivity;
                        ChatActivity access$25002 = ChatActivityEnterView.this.parentFragment;
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        access$2500.showDialog(new StickersAlert(access$1600, access$25002, tLRPC$InputStickerSet, (TLRPC$TL_messages_stickerSet) null, chatActivityEnterView, chatActivityEnterView.resourcesProvider));
                    }
                }

                public void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).toggleStickerSet(ChatActivityEnterView.this.parentActivity, tLRPC$StickerSetCovered, 2, ChatActivityEnterView.this.parentFragment, false, false);
                }

                public void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).toggleStickerSet(ChatActivityEnterView.this.parentActivity, tLRPC$StickerSetCovered, 0, ChatActivityEnterView.this.parentFragment, false, false);
                }

                public void onStickersGroupClick(long j) {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        if (AndroidUtilities.isTablet()) {
                            ChatActivityEnterView.this.hidePopup(false);
                        }
                        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(j);
                        groupStickersActivity.setInfo(ChatActivityEnterView.this.info);
                        ChatActivityEnterView.this.parentFragment.presentFragment(groupStickersActivity);
                    }
                }

                public void onSearchOpenClose(int i) {
                    ChatActivityEnterView.this.setSearchingTypeInternal(i, true);
                    if (i != 0) {
                        ChatActivityEnterView.this.setStickersExpanded(true, true, false, i == 1);
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

                public void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout) {
                    if (ChatActivityEnterView.this.parentActivity != null && ChatActivityEnterView.this.parentFragment != null) {
                        TrendingStickersAlert unused = ChatActivityEnterView.this.trendingStickersAlert = new TrendingStickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, trendingStickersLayout, ChatActivityEnterView.this.resourcesProvider) {
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
                            int access$12900 = chatActivityEnterView2.stickersExpandedHeight;
                            int dp = AndroidUtilities.dp(120.0f);
                            Point point = AndroidUtilities.displaySize;
                            int unused4 = chatActivityEnterView2.stickersExpandedHeight = Math.min(access$12900, dp + (point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight));
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
                        int access$13000 = point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                        float max = (float) Math.max(Math.min(i + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - access$13000));
                        ChatActivityEnterView.this.emojiView.setTranslationY(max);
                        ChatActivityEnterView.this.setTranslationY(max);
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        float unused = chatActivityEnterView.stickersExpansionProgress = max / ((float) (-(chatActivityEnterView.stickersExpandedHeight - access$13000)));
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
    public void lambda$onStickerSelected$50(TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, boolean z2, int i) {
        int i2 = i;
        if (isInScheduleMode() && i2 == 0) {
            AlertsCreator.createScheduleDatePickerDialog((Context) this.parentActivity, this.parentFragment.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatActivityEnterView$$ExternalSyntheticLambda54(this, tLRPC$Document, str, obj, sendAnimationData, z), this.resourcesProvider);
        } else if (this.slowModeTimer <= 0 || isInScheduleMode()) {
            if (this.searchingType != 0) {
                setSearchingTypeInternal(0, true);
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
    public void showPopup(int i, int i2) {
        showPopup(i, i2, true);
    }

    private void showPopup(int i, int i2, boolean z) {
        View view;
        int i3;
        int i4;
        final int i5 = i;
        int i6 = i2;
        if (i5 != 2) {
            if (i5 == 1) {
                if (i6 == 0 && this.emojiView == null) {
                    if (this.parentActivity != null) {
                        createEmojiView();
                    } else {
                        return;
                    }
                }
                if (i6 == 0) {
                    if (this.emojiView.getParent() == null) {
                        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                        sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 5);
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
                    this.emojiView.setShowing(true);
                    view = this.emojiView;
                    this.animatingContentType = 0;
                } else if (i6 == 1) {
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
                        this.emojiView.setShowing(false);
                        this.emojiViewVisible = false;
                        i4 = this.emojiView.getMeasuredHeight();
                    }
                    this.botKeyboardView.setVisibility(0);
                    view = this.botKeyboardView;
                    this.animatingContentType = 1;
                } else {
                    i3 = 0;
                    view = null;
                }
                this.currentPopupContentType = i6;
                if (this.keyboardHeight <= 0) {
                    this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
                }
                if (this.keyboardHeightLand <= 0) {
                    this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
                }
                Point point = AndroidUtilities.displaySize;
                int i7 = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
                if (i6 == 1) {
                    i7 = Math.min(this.botKeyboardView.getKeyboardHeight(), i7);
                }
                BotKeyboardView botKeyboardView3 = this.botKeyboardView;
                if (botKeyboardView3 != null) {
                    botKeyboardView3.setPanelHeight(i7);
                }
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams.height = i7;
                view.setLayoutParams(layoutParams);
                if (!AndroidUtilities.isInMultiwindow) {
                    AndroidUtilities.hideKeyboard(this.messageEditText);
                }
                SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
                if (sizeNotifierFrameLayout2 != null) {
                    this.emojiPadding = i7;
                    sizeNotifierFrameLayout2.requestLayout();
                    setEmojiButtonImage(true, true);
                    updateBotButton(true);
                    onWindowSizeChanged();
                    if (this.smoothKeyboard && !this.keyboardVisible && i7 != i3 && z) {
                        this.panelAnimation = new AnimatorSet();
                        float f = (float) (i7 - i3);
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
                EmojiView emojiView3 = this.emojiView;
                if (emojiView3 != null) {
                    if (i5 == 2 && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow) {
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
                        emojiView3.setShowing(false);
                        AnimatorSet animatorSet = new AnimatorSet();
                        this.panelAnimation = animatorSet;
                        EmojiView emojiView4 = this.emojiView;
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(emojiView4, View.TRANSLATION_Y, new float[]{(float) emojiView4.getMeasuredHeight()})});
                        this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                        this.panelAnimation.setDuration(250);
                        this.panelAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (i5 == 0) {
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
                    if (i5 != 2 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
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
                                    if (i5 == 0) {
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
                SizeNotifierFrameLayout sizeNotifierFrameLayout3 = this.sizeNotifierLayout;
                if (sizeNotifierFrameLayout3 != null && !SharedConfig.smoothKeyboard && i5 == 0) {
                    this.emojiPadding = 0;
                    sizeNotifierFrameLayout3.requestLayout();
                    onWindowSizeChanged();
                }
                updateBotButton(true);
            }
            if (this.stickersTabOpen || this.emojiTabOpen) {
                checkSendButton(true);
            }
            if (this.stickersExpanded && i5 != 1) {
                setStickersExpanded(false, false, false);
            }
            updateFieldHint(false);
            checkBotMenu();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000b, code lost:
        r0 = r3.recordedAudioPanel;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setEmojiButtonImage(boolean r4, boolean r5) {
        /*
            r3 = this;
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r3.emojiButton
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            int r0 = r3.recordInterfaceState
            r1 = 0
            r2 = 1
            if (r0 == r2) goto L_0x0018
            android.widget.FrameLayout r0 = r3.recordedAudioPanel
            if (r0 == 0) goto L_0x0016
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0016
            goto L_0x0018
        L_0x0016:
            r0 = 0
            goto L_0x0019
        L_0x0018:
            r0 = 1
        L_0x0019:
            if (r0 == 0) goto L_0x002c
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r5 = r3.emojiButton
            r0 = 0
            r5.setScaleX(r0)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r5 = r3.emojiButton
            r5.setScaleY(r0)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r5 = r3.emojiButton
            r5.setAlpha(r0)
            r5 = 0
        L_0x002c:
            if (r4 == 0) goto L_0x0035
            int r4 = r3.currentPopupContentType
            if (r4 != 0) goto L_0x0035
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r4 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.KEYBOARD
            goto L_0x006e
        L_0x0035:
            org.telegram.ui.Components.EditTextCaption r4 = r3.messageEditText
            if (r4 == 0) goto L_0x0046
            android.text.Editable r4 = r4.getText()
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0046
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r4 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.SMILE
            goto L_0x006e
        L_0x0046:
            org.telegram.ui.Components.EmojiView r4 = r3.emojiView
            if (r4 != 0) goto L_0x0055
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
            java.lang.String r0 = "selected_page"
            int r4 = r4.getInt(r0, r1)
            goto L_0x0059
        L_0x0055:
            int r4 = r4.getCurrentPage()
        L_0x0059:
            if (r4 == 0) goto L_0x006c
            boolean r0 = r3.allowStickers
            if (r0 != 0) goto L_0x0064
            boolean r0 = r3.allowGifs
            if (r0 != 0) goto L_0x0064
            goto L_0x006c
        L_0x0064:
            if (r4 != r2) goto L_0x0069
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r4 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.STICKER
            goto L_0x006e
        L_0x0069:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r4 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.GIF
            goto L_0x006e
        L_0x006c:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r4 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.SMILE
        L_0x006e:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r3.emojiButton
            r0.setState(r4, r5)
            r3.onEmojiIconChanged(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setEmojiButtonImage(boolean, boolean):void");
    }

    /* access modifiers changed from: protected */
    public void onEmojiIconChanged(ChatActivityEnterViewAnimatedIconView.State state) {
        if (state == ChatActivityEnterViewAnimatedIconView.State.GIF && this.emojiView == null) {
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
            ArrayList<String> arrayList = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
            int min = Math.min(10, arrayList.size());
            for (int i = 0; i < min; i++) {
                Emoji.preloadEmoji(arrayList.get(i));
            }
        }
    }

    public boolean hidePopup(boolean z) {
        return hidePopup(z, false);
    }

    public boolean hidePopup(boolean z, boolean z2) {
        if (!isPopupShowing()) {
            return false;
        }
        if (this.currentPopupContentType == 1 && z && this.botButtonsMessageObject != null) {
            return false;
        }
        if ((!z || this.searchingType == 0) && !z2) {
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
    public void setSearchingTypeInternal(int i, boolean z) {
        final boolean z2 = i != 0;
        if (z2 != (this.searchingType != 0)) {
            ValueAnimator valueAnimator = this.searchAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.searchAnimator.cancel();
            }
            float f = 1.0f;
            if (!z) {
                if (!z2) {
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
                if (!z2) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.searchAnimator = ofFloat;
                ofFloat.addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda3(this));
                this.searchAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        float unused = ChatActivityEnterView.this.searchToOpenProgress = z2 ? 1.0f : 0.0f;
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
        this.searchingType = i;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setSearchingTypeInternal$51(ValueAnimator valueAnimator) {
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

    public void addRecentGif(TLRPC$Document tLRPC$Document) {
        MediaDataController.getInstance(this.currentAccount).addRecentGif(tLRPC$Document, (int) (System.currentTimeMillis() / 1000), true);
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.addRecentGif(tLRPC$Document);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 && this.stickersExpanded) {
            setSearchingTypeInternal(0, false);
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
        MessageObject messageObject;
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
                            AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50);
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
        this.keyboardVisible = i > 0;
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
            }
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null) {
                editTextCaption.postInvalidate();
                return;
            }
            return;
        }
        int i4 = 0;
        if (i == NotificationCenter.recordProgressChanged) {
            if (objArr[0].intValue() == this.recordingGuid) {
                if (this.recordInterfaceState != 0 && !this.wasSendTyping && !isInScheduleMode()) {
                    this.wasSendTyping = true;
                    this.accountInstance.getMessagesController().sendTyping(this.dialog_id, getThreadMessageId(), isInVideoMode() ? 7 : 1, 0);
                }
                RecordCircle recordCircle2 = this.recordCircle;
                if (recordCircle2 != null) {
                    recordCircle2.setAmplitude(objArr[1].doubleValue());
                }
            }
        } else if (i == NotificationCenter.closeChats) {
            EditTextCaption editTextCaption2 = this.messageEditText;
            if (editTextCaption2 != null && editTextCaption2.isFocused()) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
        } else {
            int i5 = 4;
            if (i == NotificationCenter.recordStartError || i == NotificationCenter.recordStopped) {
                if (objArr[0].intValue() == this.recordingGuid) {
                    if (this.recordingAudioVideo) {
                        this.recordingAudioVideo = false;
                        if (i == NotificationCenter.recordStopped) {
                            Integer num = objArr[1];
                            if (num.intValue() != 4) {
                                if (isInVideoMode() && num.intValue() == 5) {
                                    i5 = 1;
                                } else if (num.intValue() == 0) {
                                    i5 = 5;
                                } else {
                                    i5 = num.intValue() == 6 ? 2 : 3;
                                }
                            }
                            if (i5 != 3) {
                                updateRecordInterface(i5);
                            }
                        } else {
                            updateRecordInterface(2);
                        }
                    }
                    if (i == NotificationCenter.recordStopped) {
                        Integer num2 = objArr[1];
                    }
                }
            } else if (i == NotificationCenter.recordStarted) {
                if (objArr[0].intValue() == this.recordingGuid) {
                    boolean booleanValue = objArr[1].booleanValue();
                    this.isInVideoMode = !booleanValue;
                    this.audioVideoSendButton.setState(booleanValue ? ChatActivityEnterViewAnimatedIconView.State.VOICE : ChatActivityEnterViewAnimatedIconView.State.VIDEO, true);
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
                        updateRecordInterface(3);
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
                        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
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
                        checkSendButton(false);
                        updateRecordInterface(3);
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
                ChatActivityEnterViewAnimatedIconView chatActivityEnterViewAnimatedIconView = this.emojiButton;
                if (chatActivityEnterViewAnimatedIconView != null) {
                    chatActivityEnterViewAnimatedIconView.invalidate();
                }
            } else if (i == NotificationCenter.messageReceivedByServer) {
                if (!objArr[6].booleanValue() && objArr[3].longValue() == this.dialog_id && (tLRPC$ChatFull = this.info) != null && tLRPC$ChatFull.slowmode_seconds != 0 && (chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.info.id))) != null && !ChatObject.hasAdminRights(chat)) {
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
                updateRecordInterface(4);
            } else if (i == NotificationCenter.updateBotMenuButton) {
                long longValue = objArr[0].longValue();
                TLRPC$BotMenuButton tLRPC$BotMenuButton = objArr[1];
                if (longValue == this.dialog_id) {
                    if (tLRPC$BotMenuButton instanceof TLRPC$TL_botMenuButton) {
                        TLRPC$TL_botMenuButton tLRPC$TL_botMenuButton = (TLRPC$TL_botMenuButton) tLRPC$BotMenuButton;
                        this.botMenuWebViewTitle = tLRPC$TL_botMenuButton.text;
                        this.botMenuWebViewUrl = tLRPC$TL_botMenuButton.url;
                        this.botMenuButtonType = BotMenuButtonType.WEB_VIEW;
                    } else if (this.hasBotCommands) {
                        this.botMenuButtonType = BotMenuButtonType.COMMANDS;
                    } else {
                        this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
                    }
                    updateBotButton(false);
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
                    ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda2(this));
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
                ((ObjectAnimator) animatorSet2.getChildAnimations().get(0)).addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda0(this));
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
    public /* synthetic */ void lambda$checkStickresExpandHeight$52(ValueAnimator valueAnimator) {
        this.sizeNotifierLayout.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkStickresExpandHeight$53(ValueAnimator valueAnimator) {
        this.sizeNotifierLayout.invalidate();
    }

    public void setStickersExpanded(boolean z, boolean z2, boolean z3) {
        setStickersExpanded(z, z2, z3, true);
    }

    public void setStickersExpanded(boolean z, boolean z2, boolean z3, boolean z4) {
        boolean z5 = z;
        AdjustPanLayoutHelper adjustPanLayoutHelper2 = this.adjustPanLayoutHelper;
        if ((adjustPanLayoutHelper2 != null && adjustPanLayoutHelper2.animationInProgress()) || this.waitingForKeyboardOpenAfterAnimation || this.emojiView == null) {
            return;
        }
        if (z3 || this.stickersExpanded != z5) {
            this.stickersExpanded = z5;
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
                if (z4) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 1);
                }
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
                    ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda6(this, i));
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
                if (z4) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 1);
                }
                if (z2) {
                    this.closeAnimationInProgress = true;
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{0}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{0}), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", new float[]{0.0f})});
                    animatorSet2.setDuration(300);
                    animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ((ObjectAnimator) animatorSet2.getChildAnimations().get(0)).addUpdateListener(new ChatActivityEnterView$$ExternalSyntheticLambda7(this, i));
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
                            if (ChatActivityEnterView.this.onEmojiSearchClosed != null) {
                                ChatActivityEnterView.this.onEmojiSearchClosed.run();
                                Runnable unused3 = ChatActivityEnterView.this.onEmojiSearchClosed = null;
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
            if (z5) {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrCollapsePanel", NUM));
            } else {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", NUM));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setStickersExpanded$54(int i, ValueAnimator valueAnimator) {
        this.stickersExpansionProgress = Math.abs(getTranslationY() / ((float) (-(this.stickersExpandedHeight - i))));
        this.sizeNotifierLayout.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setStickersExpanded$55(int i, ValueAnimator valueAnimator) {
        this.stickersExpansionProgress = getTranslationY() / ((float) (-(this.stickersExpandedHeight - i)));
        this.sizeNotifierLayout.invalidate();
    }

    public boolean swipeToBackEnabled() {
        FrameLayout frameLayout;
        if (this.recordingAudioVideo) {
            return false;
        }
        if (isInVideoMode() && (frameLayout = this.recordedAudioPanel) != null && frameLayout.getVisibility() == 0) {
            return false;
        }
        if (!hasBotWebView() || !this.botCommandsMenuButton.isOpened()) {
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
                    onCancelButtonPressed();
                }
                return true;
            }
            setPressed(false);
            return false;
        }

        public void onCancelButtonPressed() {
            if (!ChatActivityEnterView.this.hasRecordVideo || !ChatActivityEnterView.this.isInVideoMode()) {
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
        @SuppressLint({"DrawAllocation"})
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            int measuredHeight = getMeasuredHeight() + (getMeasuredWidth() << 16);
            if (this.lastSize != measuredHeight) {
                this.lastSize = measuredHeight;
                this.slideToCancelWidth = this.grayPaint.measureText(this.slideToCancelString);
                this.cancelWidth = this.bluePaint.measureText(this.cancelString);
                this.lastUpdateTime = System.currentTimeMillis();
                int measuredHeight2 = getMeasuredHeight() >> 1;
                this.arrowPath.reset();
                if (this.smallSize) {
                    float f = (float) measuredHeight2;
                    this.arrowPath.setLastPoint(AndroidUtilities.dpf2(2.5f), f - AndroidUtilities.dpf2(3.12f));
                    this.arrowPath.lineTo(0.0f, f);
                    this.arrowPath.lineTo(AndroidUtilities.dpf2(2.5f), f + AndroidUtilities.dpf2(3.12f));
                } else {
                    float f2 = (float) measuredHeight2;
                    this.arrowPath.setLastPoint(AndroidUtilities.dpf2(4.0f), f2 - AndroidUtilities.dpf2(5.0f));
                    this.arrowPath.lineTo(0.0f, f2);
                    this.arrowPath.lineTo(AndroidUtilities.dpf2(4.0f), f2 + AndroidUtilities.dpf2(5.0f));
                }
                this.slideToLayout = new StaticLayout(this.slideToCancelString, this.grayPaint, (int) this.slideToCancelWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.cancelLayout = new StaticLayout(this.cancelString, this.bluePaint, (int) this.cancelWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            StaticLayout staticLayout;
            float f;
            Canvas canvas2 = canvas;
            if (this.slideToLayout != null && (staticLayout = this.cancelLayout) != null) {
                int width = staticLayout.getWidth() + AndroidUtilities.dp(16.0f);
                this.grayPaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_recordTime"));
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
                float measuredHeight = ((float) (getMeasuredHeight() - this.cancelLayout.getHeight())) / 2.0f;
                if (!z) {
                    measuredHeight -= ((float) AndroidUtilities.dp(12.0f)) - f;
                }
                float f5 = z ? dp3 + primaryHorizontal : (float) measuredWidth2;
                this.cancelRect.set((int) f5, (int) measuredHeight, (int) (((float) this.cancelLayout.getWidth()) + f5), (int) (((float) this.cancelLayout.getHeight()) + measuredHeight));
                this.cancelRect.inset(-AndroidUtilities.dp(16.0f), -AndroidUtilities.dp(16.0f));
                if (this.cancelToProgress > 0.0f) {
                    this.selectableBackground.setBounds((getMeasuredWidth() / 2) - width, (getMeasuredHeight() / 2) - width, (getMeasuredWidth() / 2) + width, (getMeasuredHeight() / 2) + width);
                    this.selectableBackground.draw(canvas2);
                    canvas.save();
                    canvas2.translate(f5, measuredHeight);
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
            if (ChatActivityEnterView.this.isInVideoMode() && j >= 59500 && !this.stoppedInternal) {
                float unused = ChatActivityEnterView.this.startedDraggingX = -1.0f;
                ChatActivityEnterView.this.delegate.needStartRecordVideo(3, true, 0);
                this.stoppedInternal = true;
            }
            if (this.isRunning && currentTimeMillis > this.lastSendTypingTime + 5000) {
                this.lastSendTypingTime = currentTimeMillis;
                MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).sendTyping(ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.getThreadMessageId(), ChatActivityEnterView.this.isInVideoMode() ? 7 : 1, 0);
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

    public RecordCircle getRecordCicle() {
        return this.recordCircle;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0116, code lost:
        r1 = (androidx.recyclerview.widget.LinearLayoutManager) r5.botCommandsMenuContainer.listView.getLayoutManager();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r6, int r7) {
        /*
            r5 = this;
            org.telegram.ui.Components.BotCommandsMenuView r0 = r5.botCommandsMenuButton
            if (r0 == 0) goto L_0x003e
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x003e
            org.telegram.ui.Components.BotCommandsMenuView r0 = r5.botCommandsMenuButton
            r0.measure(r6, r7)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r5.emojiButton
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            org.telegram.ui.Components.BotCommandsMenuView r2 = r5.botCommandsMenuButton
            int r2 = r2.getMeasuredWidth()
            int r1 = r1 + r2
            r0.leftMargin = r1
            org.telegram.ui.Components.EditTextCaption r0 = r5.messageEditText
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r1 = 1113849856(0x42640000, float:57.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            org.telegram.ui.Components.BotCommandsMenuView r2 = r5.botCommandsMenuButton
            int r2 = r2.getMeasuredWidth()
            int r1 = r1 + r2
            r0.leftMargin = r1
            goto L_0x00aa
        L_0x003e:
            org.telegram.ui.Components.SenderSelectView r0 = r5.senderSelectView
            if (r0 == 0) goto L_0x008a
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x008a
            org.telegram.ui.Components.SenderSelectView r0 = r5.senderSelectView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            int r0 = r0.width
            org.telegram.ui.Components.SenderSelectView r1 = r5.senderSelectView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            int r1 = r1.height
            org.telegram.ui.Components.SenderSelectView r2 = r5.senderSelectView
            r3 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r2.measure(r4, r1)
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r1 = r5.emojiButton
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r1 = (android.view.ViewGroup.MarginLayoutParams) r1
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r2 + r0
            r1.leftMargin = r2
            org.telegram.ui.Components.EditTextCaption r1 = r5.messageEditText
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r1 = (android.view.ViewGroup.MarginLayoutParams) r1
            r2 = 1115422720(0x427CLASSNAME, float:63.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r2 + r0
            r1.leftMargin = r2
            goto L_0x00aa
        L_0x008a:
            org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView r0 = r5.emojiButton
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.leftMargin = r1
            org.telegram.ui.Components.EditTextCaption r0 = r5.messageEditText
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r1 = 1112014848(0x42480000, float:50.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.leftMargin = r1
        L_0x00aa:
            org.telegram.ui.Components.BotCommandsMenuContainer r0 = r5.botCommandsMenuContainer
            if (r0 == 0) goto L_0x014a
            org.telegram.ui.Components.BotCommandsMenuView$BotCommandsAdapter r0 = r5.botCommandsAdapter
            int r0 = r0.getItemCount()
            r1 = 4
            r2 = 0
            if (r0 <= r1) goto L_0x00cb
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r5.sizeNotifierLayout
            int r0 = r0.getMeasuredHeight()
            r1 = 1126354125(0x4322cccd, float:162.8)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            int r0 = java.lang.Math.max(r2, r0)
            goto L_0x00ee
        L_0x00cb:
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r5.sizeNotifierLayout
            int r0 = r0.getMeasuredHeight()
            r3 = 1
            org.telegram.ui.Components.BotCommandsMenuView$BotCommandsAdapter r4 = r5.botCommandsAdapter
            int r4 = r4.getItemCount()
            int r1 = java.lang.Math.min(r1, r4)
            int r1 = java.lang.Math.max(r3, r1)
            int r1 = r1 * 36
            int r1 = r1 + 8
            float r1 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            int r0 = java.lang.Math.max(r2, r0)
        L_0x00ee:
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            int r1 = r1.getPaddingTop()
            if (r1 == r0) goto L_0x014a
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            r1.setTopGlowOffset(r0)
            int r1 = r5.botCommandLastPosition
            r3 = -1
            if (r1 != r3) goto L_0x013d
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r5.botCommandsMenuContainer
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x013d
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r1 = r1.getLayoutManager()
            if (r1 == 0) goto L_0x013d
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r1 = r1.getLayoutManager()
            androidx.recyclerview.widget.LinearLayoutManager r1 = (androidx.recyclerview.widget.LinearLayoutManager) r1
            int r3 = r1.findFirstVisibleItemPosition()
            if (r3 < 0) goto L_0x013d
            android.view.View r1 = r1.findViewByPosition(r3)
            if (r1 == 0) goto L_0x013d
            r5.botCommandLastPosition = r3
            int r1 = r1.getTop()
            org.telegram.ui.Components.BotCommandsMenuContainer r3 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            int r3 = r3.getPaddingTop()
            int r1 = r1 - r3
            r5.botCommandLastTop = r1
        L_0x013d:
            org.telegram.ui.Components.BotCommandsMenuContainer r1 = r5.botCommandsMenuContainer
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.setPadding(r2, r0, r2, r3)
        L_0x014a:
            super.onMeasure(r6, r7)
            org.telegram.ui.Components.ChatActivityBotWebViewButton r0 = r5.botWebViewButton
            if (r0 == 0) goto L_0x0174
            org.telegram.ui.Components.BotCommandsMenuView r1 = r5.botCommandsMenuButton
            if (r1 == 0) goto L_0x015c
            int r1 = r1.getMeasuredWidth()
            r0.setMeasuredButtonWidth(r1)
        L_0x015c:
            org.telegram.ui.Components.ChatActivityBotWebViewButton r0 = r5.botWebViewButton
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            int r1 = r5.getMeasuredHeight()
            r2 = 1073741824(0x40000000, float:2.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            r0.height = r1
            org.telegram.ui.Components.ChatActivityBotWebViewButton r0 = r5.botWebViewButton
            r5.measureChild(r0, r6, r7)
        L_0x0174:
            org.telegram.ui.Components.BotWebViewMenuContainer r0 = r5.botWebViewMenuContainer
            if (r0 == 0) goto L_0x018b
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            org.telegram.ui.Components.EditTextCaption r1 = r5.messageEditText
            int r1 = r1.getMeasuredHeight()
            r0.bottomMargin = r1
            org.telegram.ui.Components.BotWebViewMenuContainer r0 = r5.botWebViewMenuContainer
            r5.measureChild(r0, r6, r7)
        L_0x018b:
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
        ChatActivityEnterViewAnimatedIconView chatActivityEnterViewAnimatedIconView = this.emojiButton;
        hashMap.put(chatActivityEnterViewAnimatedIconView, Float.valueOf(chatActivityEnterViewAnimatedIconView.getX()));
        HashMap<View, Float> hashMap2 = this.animationParamsX;
        EditTextCaption editTextCaption = this.messageEditText;
        hashMap2.put(editTextCaption, Float.valueOf(editTextCaption.getX()));
    }

    public void setBotInfo(LongSparseArray<TLRPC$BotInfo> longSparseArray) {
        setBotInfo(longSparseArray, true);
    }

    public void setBotInfo(LongSparseArray<TLRPC$BotInfo> longSparseArray, boolean z) {
        if (longSparseArray.size() == 1 && longSparseArray.valueAt(0).user_id == this.dialog_id) {
            TLRPC$BotInfo valueAt = longSparseArray.valueAt(0);
            TLRPC$BotMenuButton tLRPC$BotMenuButton = valueAt.menu_button;
            if (tLRPC$BotMenuButton instanceof TLRPC$TL_botMenuButton) {
                TLRPC$TL_botMenuButton tLRPC$TL_botMenuButton = (TLRPC$TL_botMenuButton) tLRPC$BotMenuButton;
                this.botMenuWebViewTitle = tLRPC$TL_botMenuButton.text;
                this.botMenuWebViewUrl = tLRPC$TL_botMenuButton.url;
                this.botMenuButtonType = BotMenuButtonType.WEB_VIEW;
            } else if (!valueAt.commands.isEmpty()) {
                this.botMenuButtonType = BotMenuButtonType.COMMANDS;
            } else {
                this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
            }
        } else {
            this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
        }
        BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter2 = this.botCommandsAdapter;
        if (botCommandsAdapter2 != null) {
            botCommandsAdapter2.setBotInfo(longSparseArray);
        }
        updateBotButton(z);
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

    public void setTextTransitionIsRunning(boolean z) {
        this.textTransitionIsRunning = z;
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
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Paint paint2 = resourcesProvider2 != null ? resourcesProvider2.getPaint(str) : null;
        return paint2 != null ? paint2 : Theme.getThemePaint(str);
    }

    public void setChatSearchExpandOffset(float f) {
        this.chatSearchExpandOffset = f;
        invalidate();
    }
}
