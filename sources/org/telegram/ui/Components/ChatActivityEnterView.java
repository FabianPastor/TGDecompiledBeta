package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.os.BuildCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SharedPrefsHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
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
import org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BotCommandsMenuView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SenderSelectPopup;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.VideoTimelineView;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupStickersActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.StickersActivity;
/* loaded from: classes3.dex */
public class ChatActivityEnterView extends BlurredFrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StickersAlert.StickersAlertDelegate {
    private AccountInstance accountInstance;
    private AdjustPanLayoutHelper adjustPanLayoutHelper;
    private boolean allowAnimatedEmoji;
    public boolean allowBlur;
    private boolean allowGifs;
    private boolean allowShowTopView;
    private boolean allowStickers;
    protected int animatedTop;
    private int animatingContentType;
    private HashMap<View, Float> animationParamsX;
    private ImageView attachButton;
    private LinearLayout attachLayout;
    private TLRPC$TL_document audioToSend;
    private MessageObject audioToSendMessageObject;
    private String audioToSendPath;
    private FrameLayout audioVideoButtonContainer;
    private ChatActivityEnterViewAnimatedIconView audioVideoSendButton;
    Paint backgroundPaint;
    private ImageView botButton;
    private ReplaceableIconDrawable botButtonDrawable;
    private MessageObject botButtonsMessageObject;
    int botCommandLastPosition;
    int botCommandLastTop;
    private BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter;
    private BotCommandsMenuView botCommandsMenuButton;
    public BotCommandsMenuContainer botCommandsMenuContainer;
    private int botCount;
    private BotKeyboardView botKeyboardView;
    private boolean botKeyboardViewVisible;
    private BotMenuButtonType botMenuButtonType;
    private String botMenuWebViewTitle;
    private String botMenuWebViewUrl;
    private MessageObject botMessageObject;
    private TLRPC$TL_replyKeyboardMarkup botReplyMarkup;
    private ChatActivityBotWebViewButton botWebViewButton;
    private BotWebViewMenuContainer botWebViewMenuContainer;
    private boolean calledRecordRunnable;
    private Drawable cameraDrawable;
    private Drawable cameraOutline;
    private boolean canWriteToChannel;
    private ImageView cancelBotButton;
    private NumberTextView captionLimitView;
    private float chatSearchExpandOffset;
    private boolean clearBotButtonsOnKeyboardOpen;
    private boolean closeAnimationInProgress;
    private int codePointCount;
    private float composeShadowAlpha;
    private boolean configAnimationsEnabled;
    private int currentAccount;
    private int currentLimit;
    private int currentPopupContentType;
    public ValueAnimator currentTopViewAnimation;
    private ChatActivityEnterViewDelegate delegate;
    private boolean destroyed;
    private long dialog_id;
    private float distCanMove;
    private AnimatorSet doneButtonAnimation;
    private ValueAnimator doneButtonColorAnimator;
    private FrameLayout doneButtonContainer;
    boolean doneButtonEnabled;
    private float doneButtonEnabledProgress;
    private ImageView doneButtonImage;
    private ContextProgressView doneButtonProgress;
    private final Drawable doneCheckDrawable;
    private Paint dotPaint;
    private CharSequence draftMessage;
    private boolean draftSearchWebpage;
    private boolean editingCaption;
    private MessageObject editingMessageObject;
    private ChatActivityEnterViewAnimatedIconView emojiButton;
    private int emojiPadding;
    private boolean emojiTabOpen;
    private EmojiView emojiView;
    private boolean emojiViewVisible;
    private ImageView expandStickersButton;
    private Runnable focusRunnable;
    private boolean forceShowSendButton;
    private boolean hasBotCommands;
    private boolean hasRecordVideo;
    private Runnable hideKeyboardRunnable;
    private boolean ignoreTextChange;
    private Drawable inactinveSendButtonDrawable;
    private TLRPC$ChatFull info;
    private int innerTextChange;
    private boolean isInVideoMode;
    private boolean isInitLineCount;
    private boolean isPaste;
    private boolean isPaused;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private long lastTypingTimeSend;
    private int lineCount;
    private int[] location;
    private Drawable lockShadowDrawable;
    private View.AccessibilityDelegate mediaMessageButtonsDelegate;
    protected EditTextCaption messageEditText;
    boolean messageTransitionIsRunning;
    private TLRPC$WebPage messageWebPage;
    private boolean messageWebPageSearch;
    private Drawable micDrawable;
    private Drawable micOutline;
    private Runnable moveToSendStateRunnable;
    private boolean needShowTopView;
    private int notificationsIndex;
    private ImageView notifyButton;
    private CrossOutDrawable notifySilentDrawable;
    private Runnable onEmojiSearchClosed;
    private Runnable onFinishInitCameraRunnable;
    private Runnable onKeyboardClosed;
    private Runnable openKeyboardRunnable;
    private int originalViewHeight;
    private Paint paint;
    private AnimatorSet panelAnimation;
    private Activity parentActivity;
    private ChatActivity parentFragment;
    private RectF pauseRect;
    private TLRPC$KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    private MediaActionDrawable playPauseDrawable;
    private int popupX;
    private int popupY;
    public boolean preventInput;
    private CloseProgressDrawable2 progressDrawable;
    private Runnable recordAudioVideoRunnable;
    private boolean recordAudioVideoRunnableStarted;
    private RecordCircle recordCircle;
    private Property<RecordCircle, Float> recordCircleScale;
    private RLottieImageView recordDeleteImageView;
    private RecordDot recordDot;
    private int recordInterfaceState;
    private boolean recordIsCanceled;
    private FrameLayout recordPanel;
    private AnimatorSet recordPannelAnimation;
    private LinearLayout recordTimeContainer;
    private TimerView recordTimerView;
    private View recordedAudioBackground;
    private FrameLayout recordedAudioPanel;
    private ImageView recordedAudioPlayButton;
    private SeekBarWaveformView recordedAudioSeekBar;
    private TextView recordedAudioTimeTextView;
    private boolean recordingAudioVideo;
    private int recordingGuid;
    private android.graphics.Rect rect;
    private Paint redDotPaint;
    private boolean removeEmojiViewAfterAnimation;
    private MessageObject replyingMessageObject;
    private final Theme.ResourcesProvider resourcesProvider;
    private Property<View, Integer> roundedTranslationYProperty;
    private Runnable runEmojiPanelAnimation;
    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private AnimatorSet runningAnimationAudio;
    private int runningAnimationType;
    private boolean scheduleButtonHidden;
    private ImageView scheduledButton;
    private AnimatorSet scheduledButtonAnimation;
    private ValueAnimator searchAnimator;
    private float searchToOpenProgress;
    private int searchingType;
    private SeekBarWaveform seekBarWaveform;
    private View sendButton;
    private FrameLayout sendButtonContainer;
    private Drawable sendButtonDrawable;
    private Drawable sendButtonInverseDrawable;
    private boolean sendByEnter;
    private Drawable sendDrawable;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private android.graphics.Rect sendRect;
    private SenderSelectPopup senderSelectPopupWindow;
    private SenderSelectView senderSelectView;
    private Runnable setTextFieldRunnable;
    protected boolean shouldAnimateEditTextWithBounds;
    private boolean showKeyboardOnResume;
    private Runnable showTopViewRunnable;
    private boolean silent;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private SlideTextView slideText;
    private SimpleTextView slowModeButton;
    private int slowModeTimer;
    private boolean smoothKeyboard;
    private float startedDraggingX;
    private AnimatedArrowDrawable stickersArrow;
    private boolean stickersDragging;
    private boolean stickersExpanded;
    private int stickersExpandedHeight;
    private Animator stickersExpansionAnim;
    private float stickersExpansionProgress;
    private boolean stickersTabOpen;
    private FrameLayout textFieldContainer;
    boolean textTransitionIsRunning;
    protected View topLineView;
    protected View topView;
    protected float topViewEnterProgress;
    protected boolean topViewShowed;
    private final ValueAnimator.AnimatorUpdateListener topViewUpdateListener;
    private TrendingStickersAlert trendingStickersAlert;
    private Runnable updateExpandabilityRunnable;
    private Runnable updateSlowModeRunnable;
    private VideoTimelineView videoTimelineView;
    private VideoEditedInfo videoToSendMessageObject;
    private boolean waitingForKeyboardOpen;
    private boolean waitingForKeyboardOpenAfterAnimation;
    private PowerManager.WakeLock wakeLock;
    private boolean wasSendTyping;

    /* loaded from: classes3.dex */
    public enum BotMenuButtonType {
        NO_BUTTON,
        COMMANDS,
        WEB_VIEW
    }

    /* loaded from: classes3.dex */
    public interface ChatActivityEnterViewDelegate {

        /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate$-CC */
        /* loaded from: classes3.dex */
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

            public static void $default$onContextMenuClose(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
            }

            public static void $default$onContextMenuOpen(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
            }

            public static void $default$onEditTextScroll(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
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

        void onContextMenuClose();

        void onContextMenuOpen();

        void onEditTextScroll();

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

    public static /* synthetic */ boolean lambda$new$17(View view, MotionEvent motionEvent) {
        return true;
    }

    public void checkAnimation() {
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    protected void onLineCountChanged(int i, int i2) {
    }

    protected boolean pannelAnimationEnabled() {
        return true;
    }

    /* loaded from: classes3.dex */
    public class SeekBarWaveformView extends View {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SeekBarWaveformView(Context context) {
            super(context);
            ChatActivityEnterView.this = r2;
            r2.seekBarWaveform = new SeekBarWaveform(context);
            r2.seekBarWaveform.setDelegate(new SeekBar.SeekBarDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView$$ExternalSyntheticLambda0
                @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
                public /* synthetic */ void onSeekBarContinuousDrag(float f) {
                    SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
                }

                @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
                public final void onSeekBarDrag(float f) {
                    ChatActivityEnterView.SeekBarWaveformView.this.lambda$new$0(f);
                }

                @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
                public /* synthetic */ void onSeekBarPressed() {
                    SeekBar.SeekBarDelegate.CC.$default$onSeekBarPressed(this);
                }

                @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
                public /* synthetic */ void onSeekBarReleased() {
                    SeekBar.SeekBarDelegate.CC.$default$onSeekBarReleased(this);
                }
            });
        }

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

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean onTouch = ChatActivityEnterView.this.seekBarWaveform.onTouch(motionEvent.getAction(), motionEvent.getX(), motionEvent.getY());
            if (onTouch) {
                if (motionEvent.getAction() == 0) {
                    ChatActivityEnterView.this.requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
            }
            return onTouch || super.onTouchEvent(motionEvent);
        }

        @Override // android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ChatActivityEnterView.this.seekBarWaveform.setSize(i3 - i, i4 - i2);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            ChatActivityEnterView.this.seekBarWaveform.setColors(ChatActivityEnterView.this.getThemedColor("chat_recordedVoiceProgress"), ChatActivityEnterView.this.getThemedColor("chat_recordedVoiceProgressInner"), ChatActivityEnterView.this.getThemedColor("chat_recordedVoiceProgress"));
            ChatActivityEnterView.this.seekBarWaveform.draw(canvas, this);
        }
    }

    /* loaded from: classes3.dex */
    public class RecordDot extends View {
        private float alpha;
        boolean attachedToWindow;
        RLottieDrawable drawable;
        private boolean enterAnimation;
        private boolean isIncr;
        private long lastUpdateTime;
        boolean playing;

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attachedToWindow = true;
            if (this.playing) {
                this.drawable.start();
            }
            this.drawable.setMasterParent(this);
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attachedToWindow = false;
            this.drawable.stop();
            this.drawable.setMasterParent(null);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public RecordDot(Context context) {
            super(context);
            ChatActivityEnterView.this = r8;
            int i = R.raw.chat_audio_record_delete;
            RLottieDrawable rLottieDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), false, null);
            this.drawable = rLottieDrawable;
            rLottieDrawable.setCurrentParentView(this);
            this.drawable.setInvalidateOnProgressSet(true);
            updateColors();
        }

        public void updateColors() {
            int themedColor = ChatActivityEnterView.this.getThemedColor("chat_recordedVoiceDot");
            int themedColor2 = ChatActivityEnterView.this.getThemedColor("chat_messagePanelBackground");
            ChatActivityEnterView.this.redDotPaint.setColor(themedColor);
            this.drawable.beginApplyLayerColors();
            this.drawable.setLayerColor("Cup Red.**", themedColor);
            this.drawable.setLayerColor("Box.**", themedColor);
            this.drawable.setLayerColor("Line 1.**", themedColor2);
            this.drawable.setLayerColor("Line 2.**", themedColor2);
            this.drawable.setLayerColor("Line 3.**", themedColor2);
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

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.playing) {
                this.drawable.setAlpha((int) (this.alpha * 255.0f));
            }
            ChatActivityEnterView.this.redDotPaint.setAlpha((int) (this.alpha * 255.0f));
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.enterAnimation) {
                this.alpha = 1.0f;
            } else if (!this.isIncr && !this.playing) {
                float f = this.alpha - (((float) currentTimeMillis) / 600.0f);
                this.alpha = f;
                if (f <= 0.0f) {
                    this.alpha = 0.0f;
                    this.isIncr = true;
                }
            } else {
                float f2 = this.alpha + (((float) currentTimeMillis) / 600.0f);
                this.alpha = f2;
                if (f2 >= 1.0f) {
                    this.alpha = 1.0f;
                    this.isIncr = false;
                }
            }
            this.lastUpdateTime = System.currentTimeMillis();
            if (this.playing) {
                this.drawable.draw(canvas);
            }
            if (!this.playing || !this.drawable.hasBitmap()) {
                canvas.drawCircle(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.redDotPaint);
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

    /* loaded from: classes3.dex */
    public class RecordCircle extends View {
        private float amplitude;
        private float animateAmplitudeDiff;
        private float animateToAmplitude;
        BlobDrawable bigWaveDrawable;
        private boolean canceledByGesture;
        private float circleRadius;
        private float circleRadiusAmplitude;
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
        Paint lockBackgroundPaint;
        Paint lockOutlinePaint;
        Paint lockPaint;
        private Paint p;
        private int paintAlpha;
        Path path;
        private boolean pressed;
        private float progressToSeekbarStep3;
        private float progressToSendButton;
        RectF rectF;
        private float scale;
        private boolean sendButtonVisible;
        private boolean showTooltip;
        private long showTooltipStartTime;
        private boolean showWaves;
        public boolean skipDraw;
        private int slideDelta;
        private float slideToCancelLockProgress;
        private float slideToCancelProgress;
        private float snapAnimationProgress;
        private float startTranslation;
        BlobDrawable tinyWaveDrawable;
        private float tooltipAlpha;
        private Drawable tooltipBackground;
        private Drawable tooltipBackgroundArrow;
        private StaticLayout tooltipLayout;
        private String tooltipMessage;
        private TextPaint tooltipPaint;
        private float tooltipWidth;
        private float touchSlop;
        private float transformToSeekbar;
        private VirtualViewHelper virtualViewHelper;
        public boolean voiceEnterTransitionInProgress;
        private float wavesEnterAnimation;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public RecordCircle(Context context) {
            super(context);
            ChatActivityEnterView.this = r6;
            this.tinyWaveDrawable = new BlobDrawable(11);
            this.bigWaveDrawable = new BlobDrawable(12);
            this.tooltipPaint = new TextPaint(1);
            this.circleRadius = AndroidUtilities.dpf2(41.0f);
            this.circleRadiusAmplitude = AndroidUtilities.dp(30.0f);
            this.lockBackgroundPaint = new Paint(1);
            this.lockPaint = new Paint(1);
            this.lockOutlinePaint = new Paint(1);
            this.rectF = new RectF();
            this.path = new Path();
            this.wavesEnterAnimation = 0.0f;
            this.showWaves = true;
            this.p = new Paint(1);
            r6.micDrawable = getResources().getDrawable(R.drawable.input_mic_pressed).mutate();
            r6.micDrawable.setColorFilter(new PorterDuffColorFilter(r6.getThemedColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            r6.cameraDrawable = getResources().getDrawable(R.drawable.input_video_pressed).mutate();
            r6.cameraDrawable.setColorFilter(new PorterDuffColorFilter(r6.getThemedColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            r6.sendDrawable = getResources().getDrawable(R.drawable.attach_send).mutate();
            r6.sendDrawable.setColorFilter(new PorterDuffColorFilter(r6.getThemedColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
            r6.micOutline = getResources().getDrawable(R.drawable.input_mic).mutate();
            r6.micOutline.setColorFilter(new PorterDuffColorFilter(r6.getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            r6.cameraOutline = getResources().getDrawable(R.drawable.input_video).mutate();
            r6.cameraOutline.setColorFilter(new PorterDuffColorFilter(r6.getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            VirtualViewHelper virtualViewHelper = new VirtualViewHelper(this);
            this.virtualViewHelper = virtualViewHelper;
            ViewCompat.setAccessibilityDelegate(this, virtualViewHelper);
            this.tinyWaveDrawable.minRadius = AndroidUtilities.dp(47.0f);
            this.tinyWaveDrawable.maxRadius = AndroidUtilities.dp(55.0f);
            this.tinyWaveDrawable.generateBlob();
            this.bigWaveDrawable.minRadius = AndroidUtilities.dp(47.0f);
            this.bigWaveDrawable.maxRadius = AndroidUtilities.dp(55.0f);
            this.bigWaveDrawable.generateBlob();
            this.lockOutlinePaint.setStyle(Paint.Style.STROKE);
            this.lockOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
            this.lockOutlinePaint.setStrokeWidth(AndroidUtilities.dpf2(1.7f));
            r6.lockShadowDrawable = getResources().getDrawable(R.drawable.lock_round_shadow);
            r6.lockShadowDrawable.setColorFilter(new PorterDuffColorFilter(r6.getThemedColor("key_chat_messagePanelVoiceLockShadow"), PorterDuff.Mode.MULTIPLY));
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), r6.getThemedColor("chat_gifSaveHintBackground"));
            this.tooltipPaint.setTextSize(AndroidUtilities.dp(14.0f));
            this.tooltipBackgroundArrow = ContextCompat.getDrawable(context, R.drawable.tooltip_arrow);
            this.tooltipMessage = LocaleController.getString("SlideUpToLock", R.string.SlideUpToLock);
            float scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
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
                if (this.canceledByGesture || this.slideToCancelProgress < 0.7f || this.startTranslation - this.lockAnimatedTranslation < AndroidUtilities.dp(57.0f)) {
                    return 1;
                }
                this.sendButtonVisible = true;
                return 2;
            }
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.sendButtonVisible) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == 0) {
                    boolean contains = ChatActivityEnterView.this.pauseRect.contains(x, y);
                    this.pressed = contains;
                    return contains;
                } else if (this.pressed) {
                    if (motionEvent.getAction() == 1 && ChatActivityEnterView.this.pauseRect.contains(x, y)) {
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

        @Override // android.view.View
        @SuppressLint({"DrawAllocation"})
        protected void onMeasure(int i, int i2) {
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
            float measuredWidth = getMeasuredWidth() * 0.35f;
            if (measuredWidth > AndroidUtilities.dp(140.0f)) {
                measuredWidth = AndroidUtilities.dp(140.0f);
            }
            this.slideDelta = (int) ((-measuredWidth) * (1.0f - this.slideToCancelProgress));
        }

        /* JADX WARN: Removed duplicated region for block: B:402:0x0562  */
        /* JADX WARN: Removed duplicated region for block: B:407:0x05c6  */
        /* JADX WARN: Removed duplicated region for block: B:410:0x063f  */
        /* JADX WARN: Removed duplicated region for block: B:414:0x0652  */
        /* JADX WARN: Removed duplicated region for block: B:419:0x0662  */
        /* JADX WARN: Removed duplicated region for block: B:428:0x067b  */
        /* JADX WARN: Removed duplicated region for block: B:433:0x0699  */
        /* JADX WARN: Removed duplicated region for block: B:438:0x06c6  */
        /* JADX WARN: Removed duplicated region for block: B:439:0x07ed  */
        /* JADX WARN: Removed duplicated region for block: B:443:0x0818  */
        /* JADX WARN: Removed duplicated region for block: B:444:0x081d  */
        /* JADX WARN: Removed duplicated region for block: B:453:0x0831  */
        /* JADX WARN: Removed duplicated region for block: B:463:0x0855  */
        /* JADX WARN: Removed duplicated region for block: B:469:0x0882  */
        /* JADX WARN: Removed duplicated region for block: B:472:0x097d  */
        /* JADX WARN: Removed duplicated region for block: B:475:0x098c  */
        /* JADX WARN: Removed duplicated region for block: B:481:0x0a62  */
        /* JADX WARN: Removed duplicated region for block: B:486:0x0a9e  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onDraw(android.graphics.Canvas r38) {
            /*
                Method dump skipped, instructions count: 2723
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.onDraw(android.graphics.Canvas):void");
        }

        public void drawIcon(Canvas canvas, int i, int i2, float f) {
            Drawable drawable;
            Drawable drawable2 = null;
            if (isSendButtonVisible()) {
                if (this.progressToSendButton != 1.0f) {
                    drawable2 = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.cameraDrawable : ChatActivityEnterView.this.micDrawable;
                }
                drawable = ChatActivityEnterView.this.sendDrawable;
            } else {
                drawable = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.cameraDrawable : ChatActivityEnterView.this.micDrawable;
            }
            Drawable drawable3 = drawable;
            Drawable drawable4 = drawable2;
            ChatActivityEnterView.this.sendRect.set(i - (drawable3.getIntrinsicWidth() / 2), i2 - (drawable3.getIntrinsicHeight() / 2), (drawable3.getIntrinsicWidth() / 2) + i, (drawable3.getIntrinsicHeight() / 2) + i2);
            drawable3.setBounds(ChatActivityEnterView.this.sendRect);
            if (drawable4 != null) {
                drawable4.setBounds(i - (drawable4.getIntrinsicWidth() / 2), i2 - (drawable4.getIntrinsicHeight() / 2), i + (drawable4.getIntrinsicWidth() / 2), i2 + (drawable4.getIntrinsicHeight() / 2));
            }
            drawIconInternal(canvas, drawable3, drawable4, this.progressToSendButton, (int) (f * 255.0f));
        }

        private void drawIconInternal(Canvas canvas, Drawable drawable, Drawable drawable2, float f, int i) {
            float f2 = 0.0f;
            if (f == 0.0f || f == 1.0f || drawable2 == null) {
                boolean z = this.canceledByGesture;
                if (z && this.slideToCancelProgress == 1.0f) {
                    ChatActivityEnterView.this.audioVideoSendButton.setAlpha(1.0f);
                    setVisibility(8);
                    return;
                } else if (!z || this.slideToCancelProgress >= 1.0f) {
                    if (z) {
                        return;
                    }
                    drawable.setAlpha(i);
                    drawable.draw(canvas);
                    return;
                } else {
                    Drawable drawable3 = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.cameraOutline : ChatActivityEnterView.this.micOutline;
                    drawable3.setBounds(drawable.getBounds());
                    float f3 = this.slideToCancelProgress;
                    if (f3 >= 0.93f) {
                        f2 = ((f3 - 0.93f) / 0.07f) * 255.0f;
                    }
                    int i2 = (int) f2;
                    drawable3.setAlpha(i2);
                    drawable3.draw(canvas);
                    drawable3.setAlpha(255);
                    drawable.setAlpha(255 - i2);
                    drawable.draw(canvas);
                    return;
                }
            }
            canvas.save();
            canvas.scale(f, f, drawable.getBounds().centerX(), drawable.getBounds().centerY());
            float f4 = i;
            drawable.setAlpha((int) (f4 * f));
            drawable.draw(canvas);
            canvas.restore();
            canvas.save();
            float f5 = 1.0f - f;
            canvas.scale(f5, f5, drawable.getBounds().centerX(), drawable.getBounds().centerY());
            drawable2.setAlpha((int) (f4 * f5));
            drawable2.draw(canvas);
            canvas.restore();
        }

        @Override // android.view.View
        protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
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
            float measuredWidth = getMeasuredWidth() * 0.35f;
            if (measuredWidth > AndroidUtilities.dp(140.0f)) {
                measuredWidth = AndroidUtilities.dp(140.0f);
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
            if (!this.showTooltip || this.tooltipAlpha != 0.0f || f6 <= this.touchSlop) {
                return;
            }
            this.showTooltipStartTime = System.currentTimeMillis();
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

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class VirtualViewHelper extends ExploreByTouchHelper {
            private int[] coords;

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
                return true;
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public VirtualViewHelper(View view) {
                super(view);
                RecordCircle.this = r1;
                this.coords = new int[2];
            }

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected int getVirtualViewAt(float f, float f2) {
                if (RecordCircle.this.isSendButtonVisible()) {
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
                    rectF.offset(iArr[0], iArr[1]);
                    ChatActivityEnterView.this.recordCircle.getLocationOnScreen(this.coords);
                    int[] iArr2 = this.coords;
                    rectF.offset(-iArr2[0], -iArr2[1]);
                    return rectF.contains(f, f2) ? 3 : -1;
                }
                return -1;
            }

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected void getVisibleVirtualViews(List<Integer> list) {
                if (RecordCircle.this.isSendButtonVisible()) {
                    list.add(1);
                    list.add(2);
                    list.add(3);
                }
            }

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                if (i == 1) {
                    accessibilityNodeInfoCompat.setBoundsInParent(ChatActivityEnterView.this.sendRect);
                    accessibilityNodeInfoCompat.setText(LocaleController.getString("Send", R.string.Send));
                } else if (i == 2) {
                    ChatActivityEnterView.this.rect.set((int) ChatActivityEnterView.this.pauseRect.left, (int) ChatActivityEnterView.this.pauseRect.top, (int) ChatActivityEnterView.this.pauseRect.right, (int) ChatActivityEnterView.this.pauseRect.bottom);
                    accessibilityNodeInfoCompat.setBoundsInParent(ChatActivityEnterView.this.rect);
                    accessibilityNodeInfoCompat.setText(LocaleController.getString("Stop", R.string.Stop));
                } else if (i != 3) {
                } else {
                    if (ChatActivityEnterView.this.slideText != null && ChatActivityEnterView.this.slideText.cancelRect != null) {
                        android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                        rect.set(ChatActivityEnterView.this.slideText.cancelRect);
                        ChatActivityEnterView.this.slideText.getLocationOnScreen(this.coords);
                        int[] iArr = this.coords;
                        rect.offset(iArr[0], iArr[1]);
                        ChatActivityEnterView.this.recordCircle.getLocationOnScreen(this.coords);
                        int[] iArr2 = this.coords;
                        rect.offset(-iArr2[0], -iArr2[1]);
                        accessibilityNodeInfoCompat.setBoundsInParent(rect);
                    }
                    accessibilityNodeInfoCompat.setText(LocaleController.getString("Cancel", R.string.Cancel));
                }
            }
        }
    }

    public ChatActivityEnterView(Activity activity, SizeNotifierFrameLayout sizeNotifierFrameLayout, ChatActivity chatActivity, boolean z) {
        this(activity, sizeNotifierFrameLayout, chatActivity, z, null);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public ChatActivityEnterView(final Activity activity, SizeNotifierFrameLayout sizeNotifierFrameLayout, final ChatActivity chatActivity, boolean z, final Theme.ResourcesProvider resourcesProvider) {
        super(activity, chatActivity == null ? null : chatActivity.contentView);
        String str;
        int i;
        String str2;
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        int i2 = UserConfig.selectedAccount;
        this.currentAccount = i2;
        this.accountInstance = AccountInstance.getInstance(i2);
        this.lineCount = 1;
        this.currentLimit = -1;
        this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
        this.animationParamsX = new HashMap<>();
        this.mediaMessageButtonsDelegate = new View.AccessibilityDelegate(this) { // from class: org.telegram.ui.Components.ChatActivityEnterView.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.setClassName("android.widget.ImageButton");
                accessibilityNodeInfo.setClickable(true);
                accessibilityNodeInfo.setLongClickable(true);
            }
        };
        this.currentPopupContentType = -1;
        this.isPaused = true;
        this.startedDraggingX = -1.0f;
        this.distCanMove = AndroidUtilities.dp(80.0f);
        this.location = new int[2];
        this.messageWebPageSearch = true;
        this.animatingContentType = -1;
        this.doneButtonEnabledProgress = 1.0f;
        this.doneButtonEnabled = true;
        this.openKeyboardRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.2
            {
                ChatActivityEnterView.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                if ((!ChatActivityEnterView.this.hasBotWebView() || !ChatActivityEnterView.this.botCommandsMenuIsShowing()) && !ChatActivityEnterView.this.destroyed) {
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    if (chatActivityEnterView.messageEditText == null || !chatActivityEnterView.waitingForKeyboardOpen || ChatActivityEnterView.this.keyboardVisible || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                        return;
                    }
                    ChatActivityEnterView.this.messageEditText.requestFocus();
                    AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
                    AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
                    AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100L);
                }
            }
        };
        this.updateExpandabilityRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.3
            private int lastKnownPage = -1;

            {
                ChatActivityEnterView.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                int currentPage;
                if (ChatActivityEnterView.this.emojiView == null || (currentPage = ChatActivityEnterView.this.emojiView.getCurrentPage()) == this.lastKnownPage) {
                    return;
                }
                this.lastKnownPage = currentPage;
                boolean z2 = ChatActivityEnterView.this.stickersTabOpen;
                int i3 = 2;
                ChatActivityEnterView.this.stickersTabOpen = currentPage == 1 || currentPage == 2;
                boolean z3 = ChatActivityEnterView.this.emojiTabOpen;
                ChatActivityEnterView.this.emojiTabOpen = currentPage == 0;
                if (ChatActivityEnterView.this.stickersExpanded) {
                    if (ChatActivityEnterView.this.searchingType == 0) {
                        if (!ChatActivityEnterView.this.stickersTabOpen) {
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                    } else {
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        if (currentPage != 0) {
                            i3 = 1;
                        }
                        chatActivityEnterView.setSearchingTypeInternal(i3, true);
                        ChatActivityEnterView.this.checkStickresExpandHeight();
                    }
                }
                if (z2 == ChatActivityEnterView.this.stickersTabOpen && z3 == ChatActivityEnterView.this.emojiTabOpen) {
                    return;
                }
                ChatActivityEnterView.this.checkSendButton(true);
            }
        };
        this.roundedTranslationYProperty = new Property<View, Integer>(this, Integer.class, "translationY") { // from class: org.telegram.ui.Components.ChatActivityEnterView.4
            @Override // android.util.Property
            public Integer get(View view) {
                return Integer.valueOf(Math.round(view.getTranslationY()));
            }

            @Override // android.util.Property
            public void set(View view, Integer num) {
                view.setTranslationY(num.intValue());
            }
        };
        this.recordCircleScale = new Property<RecordCircle, Float>(this, Float.class, "scale") { // from class: org.telegram.ui.Components.ChatActivityEnterView.5
            @Override // android.util.Property
            public Float get(RecordCircle recordCircle) {
                return Float.valueOf(recordCircle.getScale());
            }

            @Override // android.util.Property
            public void set(RecordCircle recordCircle, Float f) {
                recordCircle.setScale(f.floatValue());
            }
        };
        this.redDotPaint = new Paint(1);
        this.onFinishInitCameraRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.6
            {
                ChatActivityEnterView.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.needStartRecordVideo(0, true, 0);
                }
            }
        };
        this.recordAudioVideoRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.7
            {
                ChatActivityEnterView.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (ChatActivityEnterView.this.delegate == null || ChatActivityEnterView.this.parentActivity == null) {
                    return;
                }
                ChatActivityEnterView.this.delegate.onPreAudioVideoRecord();
                ChatActivityEnterView.this.calledRecordRunnable = true;
                ChatActivityEnterView.this.recordAudioVideoRunnableStarted = false;
                ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                ChatActivityEnterView.this.slideText.setTranslationY(0.0f);
                if (!ChatActivityEnterView.this.isInVideoMode()) {
                    if (ChatActivityEnterView.this.parentFragment == null || Build.VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                        ChatActivityEnterView.this.delegate.needStartRecordAudio(1);
                        ChatActivityEnterView.this.startedDraggingX = -1.0f;
                        MediaController.getInstance().startRecording(ChatActivityEnterView.this.currentAccount, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), ChatActivityEnterView.this.recordingGuid);
                        ChatActivityEnterView.this.recordingAudioVideo = true;
                        ChatActivityEnterView.this.updateRecordInterface(0);
                        ChatActivityEnterView.this.recordTimerView.start();
                        ChatActivityEnterView.this.recordDot.enterAnimation = false;
                        ChatActivityEnterView.this.audioVideoButtonContainer.getParent().requestDisallowInterceptTouchEvent(true);
                        ChatActivityEnterView.this.recordCircle.showWaves(true, false);
                        return;
                    }
                    ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
                    return;
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    boolean z2 = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0;
                    boolean z3 = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.CAMERA") == 0;
                    if (!z2 || !z3) {
                        String[] strArr = new String[(z2 || z3) ? 1 : 2];
                        if (!z2 && !z3) {
                            strArr[0] = "android.permission.RECORD_AUDIO";
                            strArr[1] = "android.permission.CAMERA";
                        } else if (!z2) {
                            strArr[0] = "android.permission.RECORD_AUDIO";
                        } else {
                            strArr[0] = "android.permission.CAMERA";
                        }
                        ChatActivityEnterView.this.parentActivity.requestPermissions(strArr, 150);
                        return;
                    }
                }
                if (!CameraController.getInstance().isCameraInitied()) {
                    CameraController.getInstance().initCamera(ChatActivityEnterView.this.onFinishInitCameraRunnable);
                } else {
                    ChatActivityEnterView.this.onFinishInitCameraRunnable.run();
                }
                if (ChatActivityEnterView.this.recordingAudioVideo) {
                    return;
                }
                ChatActivityEnterView.this.recordingAudioVideo = true;
                ChatActivityEnterView.this.updateRecordInterface(0);
                ChatActivityEnterView.this.recordCircle.showWaves(false, false);
                ChatActivityEnterView.this.recordTimerView.reset();
            }
        };
        this.paint = new Paint(1);
        this.pauseRect = new RectF();
        this.sendRect = new android.graphics.Rect();
        this.rect = new android.graphics.Rect();
        this.runEmojiPanelAnimation = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.8
            {
                ChatActivityEnterView.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (ChatActivityEnterView.this.panelAnimation == null || ChatActivityEnterView.this.panelAnimation.isRunning()) {
                    return;
                }
                ChatActivityEnterView.this.panelAnimation.start();
            }
        };
        this.allowBlur = true;
        this.backgroundPaint = new Paint();
        this.composeShadowAlpha = 1.0f;
        this.topViewUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatActivityEnterView.this.lambda$new$37(valueAnimator);
            }
        };
        this.botCommandLastPosition = -1;
        this.resourcesProvider = resourcesProvider;
        this.backgroundColor = getThemedColor("chat_messagePanelBackground");
        this.drawBlur = false;
        this.smoothKeyboard = z && SharedConfig.smoothKeyboard && !AndroidUtilities.isInMultiwindow && (chatActivity == null || !chatActivity.isInBubbleMode());
        Paint paint = new Paint(1);
        this.dotPaint = paint;
        paint.setColor(getThemedColor("chat_emojiPanelNewTrending"));
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateBotMenuButton);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.parentActivity = activity;
        this.parentFragment = chatActivity;
        if (chatActivity != null) {
            this.recordingGuid = chatActivity.getClassGuid();
        }
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setDelegate(this);
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        this.sendByEnter = globalMainSettings.getBoolean("send_by_enter", false);
        this.configAnimationsEnabled = globalMainSettings.getBoolean("view_animations", true);
        FrameLayout frameLayout = new FrameLayout(activity) { // from class: org.telegram.ui.Components.ChatActivityEnterView.9
            {
                ChatActivityEnterView.this = this;
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (ChatActivityEnterView.this.botWebViewButton.getVisibility() == 0) {
                    return ChatActivityEnterView.this.botWebViewButton.dispatchTouchEvent(motionEvent);
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.textFieldContainer = frameLayout;
        frameLayout.setClipChildren(false);
        this.textFieldContainer.setClipToPadding(false);
        this.textFieldContainer.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
        addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 1.0f, 0.0f, 0.0f));
        FrameLayout frameLayout2 = new FrameLayout(activity) { // from class: org.telegram.ui.Components.ChatActivityEnterView.10
            {
                ChatActivityEnterView.this = this;
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean z2, int i3, int i4, int i5, int i6) {
                super.onLayout(z2, i3, i4, i5, i6);
                if (ChatActivityEnterView.this.scheduledButton != null) {
                    int measuredWidth = (getMeasuredWidth() - AndroidUtilities.dp((ChatActivityEnterView.this.botButton == null || ChatActivityEnterView.this.botButton.getVisibility() != 0) ? 48.0f : 96.0f)) - AndroidUtilities.dp(48.0f);
                    ChatActivityEnterView.this.scheduledButton.layout(measuredWidth, ChatActivityEnterView.this.scheduledButton.getTop(), ChatActivityEnterView.this.scheduledButton.getMeasuredWidth() + measuredWidth, ChatActivityEnterView.this.scheduledButton.getBottom());
                }
                if (!ChatActivityEnterView.this.animationParamsX.isEmpty()) {
                    for (int i7 = 0; i7 < getChildCount(); i7++) {
                        View childAt = getChildAt(i7);
                        Float f = (Float) ChatActivityEnterView.this.animationParamsX.get(childAt);
                        if (f != null) {
                            childAt.setTranslationX(f.floatValue() - childAt.getLeft());
                            childAt.animate().translationX(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                        }
                    }
                    ChatActivityEnterView.this.animationParamsX.clear();
                }
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View view, long j) {
                if (view == ChatActivityEnterView.this.messageEditText) {
                    canvas.save();
                    canvas.clipRect(0, ((-getTop()) - ChatActivityEnterView.this.textFieldContainer.getTop()) - ChatActivityEnterView.this.getTop(), getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(6.0f));
                    boolean drawChild = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild;
                }
                return super.drawChild(canvas, view, j);
            }
        };
        frameLayout2.setClipChildren(false);
        this.textFieldContainer.addView(frameLayout2, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 48.0f, 0.0f));
        ChatActivityEnterViewAnimatedIconView chatActivityEnterViewAnimatedIconView = new ChatActivityEnterViewAnimatedIconView(activity) { // from class: org.telegram.ui.Components.ChatActivityEnterView.11
            {
                ChatActivityEnterView.this = this;
            }

            @Override // android.widget.ImageView, android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (getTag() == null || ChatActivityEnterView.this.attachLayout == null || ChatActivityEnterView.this.emojiViewVisible || MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).getUnreadStickerSets().isEmpty() || ChatActivityEnterView.this.dotPaint == null) {
                    return;
                }
                canvas.drawCircle((getWidth() / 2) + AndroidUtilities.dp(9.0f), (getHeight() / 2) - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.dotPaint);
            }
        };
        this.emojiButton = chatActivityEnterViewAnimatedIconView;
        chatActivityEnterViewAnimatedIconView.setContentDescription(LocaleController.getString(R.string.AccDescrEmojiButton));
        this.emojiButton.setFocusable(true);
        int dp = AndroidUtilities.dp(9.5f);
        this.emojiButton.setPadding(dp, dp, dp, dp);
        this.emojiButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.SRC_IN));
        int i3 = Build.VERSION.SDK_INT;
        if (i3 >= 21) {
            this.emojiButton.setBackground(Theme.createSelectorDrawable(getThemedColor("listSelectorSDK21")));
        }
        this.emojiButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda22
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$1(view);
            }
        });
        frameLayout2.addView(this.emojiButton, LayoutHelper.createFrame(48, 48.0f, 83, 3.0f, 0.0f, 0.0f, 0.0f));
        setEmojiButtonImage(false, false);
        NumberTextView numberTextView = new NumberTextView(activity);
        this.captionLimitView = numberTextView;
        numberTextView.setVisibility(8);
        this.captionLimitView.setTextSize(15);
        this.captionLimitView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText"));
        this.captionLimitView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.captionLimitView.setCenterAlign(true);
        addView(this.captionLimitView, LayoutHelper.createFrame(48, 20.0f, 85, 3.0f, 0.0f, 0.0f, 48.0f));
        AnonymousClass12 anonymousClass12 = new AnonymousClass12(activity, resourcesProvider, resourcesProvider, chatActivity, activity);
        this.messageEditText = anonymousClass12;
        if (i3 >= 28) {
            anonymousClass12.setFallbackLineSpacing(false);
        }
        this.messageEditText.setDelegate(new EditTextCaption.EditTextCaptionDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda59
            @Override // org.telegram.ui.Components.EditTextCaption.EditTextCaptionDelegate
            public final void onSpansChanged() {
                ChatActivityEnterView.this.lambda$new$2();
            }
        });
        this.messageEditText.setIncludeFontPadding(false);
        this.messageEditText.setWindowView(this.parentActivity.getWindow().getDecorView());
        ChatActivity chatActivity2 = this.parentFragment;
        TLRPC$EncryptedChat currentEncryptedChat = chatActivity2 != null ? chatActivity2.getCurrentEncryptedChat() : null;
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
        this.messageEditText.setBackgroundDrawable(null);
        this.messageEditText.setTextColor(getThemedColor("chat_messagePanelText"));
        this.messageEditText.setLinkTextColor(getThemedColor("chat_messageLinkOut"));
        this.messageEditText.setHighlightColor(getThemedColor("chat_inTextSelectionHighlight"));
        this.messageEditText.setHintColor(getThemedColor("chat_messagePanelHint"));
        this.messageEditText.setHintTextColor(getThemedColor("chat_messagePanelHint"));
        this.messageEditText.setCursorColor(getThemedColor("chat_messagePanelCursor"));
        this.messageEditText.setHandlesColor(getThemedColor("chat_TextSelectionCursor"));
        frameLayout2.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 80, 52.0f, 0.0f, z ? 50.0f : 2.0f, 0.0f));
        this.messageEditText.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.13
            boolean ctrlPressed = false;

            {
                ChatActivityEnterView.this = this;
            }

            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i4, KeyEvent keyEvent) {
                boolean z2 = false;
                if (i4 == 4 && !ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing() && keyEvent.getAction() == 1) {
                    if (!ContentPreviewViewer.hasInstance() || !ContentPreviewViewer.getInstance().isVisible()) {
                        if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                            return false;
                        }
                        if (keyEvent.getAction() == 1) {
                            if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                                SharedPreferences.Editor edit = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit();
                                edit.putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                            }
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.setSearchingTypeInternal(0, true);
                                if (ChatActivityEnterView.this.emojiView != null) {
                                    ChatActivityEnterView.this.emojiView.closeSearch(true);
                                }
                                ChatActivityEnterView.this.messageEditText.requestFocus();
                            } else if (!ChatActivityEnterView.this.stickersExpanded) {
                                if (ChatActivityEnterView.this.stickersExpansionAnim == null) {
                                    if (ChatActivityEnterView.this.botButtonsMessageObject == null || ChatActivityEnterView.this.currentPopupContentType == 1 || !TextUtils.isEmpty(ChatActivityEnterView.this.messageEditText.getText())) {
                                        ChatActivityEnterView.this.showPopup(0, 0);
                                    } else {
                                        ChatActivityEnterView.this.showPopup(1, 1);
                                    }
                                }
                            } else {
                                ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                            }
                        }
                        return true;
                    }
                    ContentPreviewViewer.getInstance().closeWithMenu();
                    return true;
                } else if (i4 == 66 && ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0 && ChatActivityEnterView.this.editingMessageObject == null)) {
                    ChatActivityEnterView.this.sendMessage();
                    return true;
                } else if (i4 != 113 && i4 != 114) {
                    return false;
                } else {
                    if (keyEvent.getAction() == 0) {
                        z2 = true;
                    }
                    this.ctrlPressed = z2;
                    return true;
                }
            }
        });
        this.messageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.14
            boolean ctrlPressed = false;

            {
                ChatActivityEnterView.this = this;
            }

            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i4, KeyEvent keyEvent) {
                if (i4 == 4) {
                    ChatActivityEnterView.this.sendMessage();
                    return true;
                } else if (keyEvent == null || i4 != 0) {
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
        this.messageEditText.addTextChangedListener(new AnonymousClass15());
        if (z) {
            if (this.parentFragment != null) {
                Drawable mutate = activity.getResources().getDrawable(R.drawable.input_calendar1).mutate();
                Drawable mutate2 = activity.getResources().getDrawable(R.drawable.input_calendar2).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
                mutate2.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_recordedVoiceDot"), PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, mutate2);
                ImageView imageView = new ImageView(activity);
                this.scheduledButton = imageView;
                imageView.setImageDrawable(combinedDrawable);
                this.scheduledButton.setVisibility(8);
                this.scheduledButton.setContentDescription(LocaleController.getString("ScheduledMessages", R.string.ScheduledMessages));
                this.scheduledButton.setScaleType(ImageView.ScaleType.CENTER);
                if (i3 >= 21) {
                    str = "listSelectorSDK21";
                    this.scheduledButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(str)));
                } else {
                    str = "listSelectorSDK21";
                }
                frameLayout2.addView(this.scheduledButton, LayoutHelper.createFrame(48, 48, 85));
                this.scheduledButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda25
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ChatActivityEnterView.this.lambda$new$3(view);
                    }
                });
            } else {
                str = "listSelectorSDK21";
            }
            LinearLayout linearLayout = new LinearLayout(activity);
            this.attachLayout = linearLayout;
            linearLayout.setOrientation(0);
            this.attachLayout.setEnabled(false);
            this.attachLayout.setPivotX(AndroidUtilities.dp(48.0f));
            this.attachLayout.setClipChildren(false);
            frameLayout2.addView(this.attachLayout, LayoutHelper.createFrame(-2, 48, 85));
            BotCommandsMenuView botCommandsMenuView = new BotCommandsMenuView(getContext());
            this.botCommandsMenuButton = botCommandsMenuView;
            botCommandsMenuView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda16
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$4(view);
                }
            });
            frameLayout2.addView(this.botCommandsMenuButton, LayoutHelper.createFrame(-2, 32.0f, 83, 10.0f, 8.0f, 10.0f, 8.0f));
            AndroidUtilities.updateViewVisibilityAnimated(this.botCommandsMenuButton, false, 1.0f, false);
            this.botCommandsMenuButton.setExpanded(true, false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            BotCommandsMenuContainer botCommandsMenuContainer = new BotCommandsMenuContainer(activity) { // from class: org.telegram.ui.Components.ChatActivityEnterView.16
                {
                    ChatActivityEnterView.this = this;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.Components.BotCommandsMenuContainer
                public void onDismiss() {
                    super.onDismiss();
                    ChatActivityEnterView.this.botCommandsMenuButton.setOpened(false);
                }
            };
            this.botCommandsMenuContainer = botCommandsMenuContainer;
            botCommandsMenuContainer.listView.setLayoutManager(linearLayoutManager);
            RecyclerListView recyclerListView = this.botCommandsMenuContainer.listView;
            BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter = new BotCommandsMenuView.BotCommandsAdapter();
            this.botCommandsAdapter = botCommandsAdapter;
            recyclerListView.setAdapter(botCommandsAdapter);
            this.botCommandsMenuContainer.listView.setOnItemClickListener(new AnonymousClass17(resourcesProvider, chatActivity));
            this.botCommandsMenuContainer.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.18
                {
                    ChatActivityEnterView.this = this;
                }

                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                public boolean onItemClick(View view, int i4) {
                    if (view instanceof BotCommandsMenuView.BotCommandView) {
                        String command = ((BotCommandsMenuView.BotCommandView) view).getCommand();
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        chatActivityEnterView.setFieldText(command + " ");
                        ChatActivityEnterView.this.botCommandsMenuContainer.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            this.botCommandsMenuContainer.setClipToPadding(false);
            this.sizeNotifierLayout.addView(this.botCommandsMenuContainer, 14, LayoutHelper.createFrame(-1, -1, 80));
            this.botCommandsMenuContainer.setVisibility(8);
            BotWebViewMenuContainer botWebViewMenuContainer = new BotWebViewMenuContainer(activity, this) { // from class: org.telegram.ui.Components.ChatActivityEnterView.19
                {
                    ChatActivityEnterView.this = this;
                }

                @Override // org.telegram.ui.Components.BotWebViewMenuContainer
                public void onDismiss() {
                    super.onDismiss();
                    ChatActivityEnterView.this.botCommandsMenuButton.setOpened(false);
                }
            };
            this.botWebViewMenuContainer = botWebViewMenuContainer;
            this.sizeNotifierLayout.addView(botWebViewMenuContainer, 15, LayoutHelper.createFrame(-1, -1, 80));
            this.botWebViewMenuContainer.setVisibility(8);
            this.botWebViewMenuContainer.setOnDismissGlobalListener(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda35
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.this.lambda$new$5();
                }
            });
            ImageView imageView2 = new ImageView(activity);
            this.botButton = imageView2;
            ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(activity);
            this.botButtonDrawable = replaceableIconDrawable;
            imageView2.setImageDrawable(replaceableIconDrawable);
            this.botButtonDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            this.botButtonDrawable.setIcon(R.drawable.input_bot2, false);
            this.botButton.setScaleType(ImageView.ScaleType.CENTER);
            if (i3 >= 21) {
                this.botButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(str)));
            }
            this.botButton.setVisibility(8);
            AndroidUtilities.updateViewVisibilityAnimated(this.botButton, false, 0.1f, false);
            this.attachLayout.addView(this.botButton, LayoutHelper.createLinear(48, 48));
            this.botButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda18
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$6(view);
                }
            });
            this.notifyButton = new ImageView(activity);
            CrossOutDrawable crossOutDrawable = new CrossOutDrawable(activity, R.drawable.input_notify_on, "chat_messagePanelIcons");
            this.notifySilentDrawable = crossOutDrawable;
            this.notifyButton.setImageDrawable(crossOutDrawable);
            this.notifySilentDrawable.setCrossOut(this.silent, false);
            ImageView imageView3 = this.notifyButton;
            if (this.silent) {
                i = R.string.AccDescrChanSilentOn;
                str2 = "AccDescrChanSilentOn";
            } else {
                i = R.string.AccDescrChanSilentOff;
                str2 = "AccDescrChanSilentOff";
            }
            imageView3.setContentDescription(LocaleController.getString(str2, i));
            this.notifyButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            this.notifyButton.setScaleType(ImageView.ScaleType.CENTER);
            if (i3 >= 21) {
                this.notifyButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(str)));
            }
            this.notifyButton.setVisibility((!this.canWriteToChannel || ((chatActivityEnterViewDelegate = this.delegate) != null && chatActivityEnterViewDelegate.hasScheduledMessages())) ? 8 : 0);
            this.attachLayout.addView(this.notifyButton, LayoutHelper.createLinear(48, 48));
            this.notifyButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.20
                private Toast visibleToast;

                {
                    ChatActivityEnterView.this = this;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    int i4;
                    String str3;
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.silent = !chatActivityEnterView.silent;
                    if (ChatActivityEnterView.this.notifySilentDrawable == null) {
                        ChatActivityEnterView.this.notifySilentDrawable = new CrossOutDrawable(activity, R.drawable.input_notify_on, "chat_messagePanelIcons");
                    }
                    ChatActivityEnterView.this.notifySilentDrawable.setCrossOut(ChatActivityEnterView.this.silent, true);
                    ChatActivityEnterView.this.notifyButton.setImageDrawable(ChatActivityEnterView.this.notifySilentDrawable);
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ChatActivityEnterView.this.currentAccount).edit();
                    edit.putBoolean("silent_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.silent).commit();
                    NotificationsController notificationsController = NotificationsController.getInstance(ChatActivityEnterView.this.currentAccount);
                    long j = ChatActivityEnterView.this.dialog_id;
                    ChatActivity chatActivity3 = chatActivity;
                    notificationsController.updateServerNotificationsSettings(j, chatActivity3 == null ? 0 : chatActivity3.getTopicId());
                    try {
                        Toast toast = this.visibleToast;
                        if (toast != null) {
                            toast.cancel();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    chatActivity.getUndoView().showWithAction(0L, !ChatActivityEnterView.this.silent ? 54 : 55, (Runnable) null);
                    ImageView imageView4 = ChatActivityEnterView.this.notifyButton;
                    if (ChatActivityEnterView.this.silent) {
                        i4 = R.string.AccDescrChanSilentOn;
                        str3 = "AccDescrChanSilentOn";
                    } else {
                        i4 = R.string.AccDescrChanSilentOff;
                        str3 = "AccDescrChanSilentOff";
                    }
                    imageView4.setContentDescription(LocaleController.getString(str3, i4));
                    ChatActivityEnterView.this.updateFieldHint(true);
                }
            });
            ImageView imageView4 = new ImageView(activity);
            this.attachButton = imageView4;
            imageView4.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            this.attachButton.setImageResource(R.drawable.input_attach);
            this.attachButton.setScaleType(ImageView.ScaleType.CENTER);
            if (i3 >= 21) {
                this.attachButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(str)));
            }
            this.attachLayout.addView(this.attachButton, LayoutHelper.createLinear(48, 48));
            this.attachButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda13
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$7(view);
                }
            });
            this.attachButton.setContentDescription(LocaleController.getString("AccDescrAttachButton", R.string.AccDescrAttachButton));
        } else {
            str = "listSelectorSDK21";
        }
        SenderSelectView senderSelectView = new SenderSelectView(getContext());
        this.senderSelectView = senderSelectView;
        senderSelectView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda26
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$14(activity, view);
            }
        });
        this.senderSelectView.setVisibility(8);
        frameLayout2.addView(this.senderSelectView, LayoutHelper.createFrame(32, 32.0f, 83, 10.0f, 8.0f, 10.0f, 8.0f));
        FrameLayout frameLayout3 = new FrameLayout(activity) { // from class: org.telegram.ui.Components.ChatActivityEnterView.26
            {
                ChatActivityEnterView.this = this;
            }

            @Override // android.view.View
            public void setVisibility(int i4) {
                super.setVisibility(i4);
                ChatActivityEnterView.this.updateSendAsButton();
            }
        };
        this.recordedAudioPanel = frameLayout3;
        frameLayout3.setVisibility(this.audioToSend == null ? 8 : 0);
        this.recordedAudioPanel.setFocusable(true);
        this.recordedAudioPanel.setFocusableInTouchMode(true);
        this.recordedAudioPanel.setClickable(true);
        frameLayout2.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        RLottieImageView rLottieImageView = new RLottieImageView(activity);
        this.recordDeleteImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.recordDeleteImageView.setAnimation(R.raw.chat_audio_record_delete_2, 28, 28);
        this.recordDeleteImageView.getAnimatedDrawable().setInvalidateOnProgressSet(true);
        updateRecordedDeleteIconColors();
        this.recordDeleteImageView.setContentDescription(LocaleController.getString("Delete", R.string.Delete));
        if (i3 >= 21) {
            this.recordDeleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(str)));
        }
        this.recordedAudioPanel.addView(this.recordDeleteImageView, LayoutHelper.createFrame(48, 48.0f));
        this.recordDeleteImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda12
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$15(view);
            }
        });
        VideoTimelineView videoTimelineView = new VideoTimelineView(activity);
        this.videoTimelineView = videoTimelineView;
        videoTimelineView.setColor(getThemedColor("chat_messagePanelVideoFrame"));
        this.videoTimelineView.setRoundFrames(true);
        this.videoTimelineView.setDelegate(new VideoTimelineView.VideoTimelineViewDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView.27
            {
                ChatActivityEnterView.this = this;
            }

            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void onLeftProgressChanged(float f) {
                if (ChatActivityEnterView.this.videoToSendMessageObject == null) {
                    return;
                }
                ChatActivityEnterView.this.videoToSendMessageObject.startTime = ((float) ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration) * f;
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, f);
            }

            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void onRightProgressChanged(float f) {
                if (ChatActivityEnterView.this.videoToSendMessageObject == null) {
                    return;
                }
                ChatActivityEnterView.this.videoToSendMessageObject.endTime = ((float) ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration) * f;
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, f);
            }

            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void didStartDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(1, 0.0f);
            }

            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void didStopDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(0, 0.0f);
            }
        });
        this.recordedAudioPanel.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, -1.0f, 19, 56.0f, 0.0f, 8.0f, 0.0f));
        VideoTimelineView.TimeHintView timeHintView = new VideoTimelineView.TimeHintView(activity);
        this.videoTimelineView.setTimeHintView(timeHintView);
        this.sizeNotifierLayout.addView(timeHintView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 52.0f));
        View view = new View(activity);
        this.recordedAudioBackground = view;
        view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), getThemedColor("chat_recordedVoiceBackground")));
        this.recordedAudioPanel.addView(this.recordedAudioBackground, LayoutHelper.createFrame(-1, 36.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioSeekBar = new SeekBarWaveformView(activity);
        LinearLayout linearLayout2 = new LinearLayout(activity);
        linearLayout2.setOrientation(0);
        this.recordedAudioPanel.addView(linearLayout2, LayoutHelper.createFrame(-1, 32.0f, 19, 92.0f, 0.0f, 13.0f, 0.0f));
        this.playPauseDrawable = new MediaActionDrawable();
        this.recordedAudioPlayButton = new ImageView(activity);
        Matrix matrix = new Matrix();
        matrix.postScale(0.8f, 0.8f, AndroidUtilities.dpf2(24.0f), AndroidUtilities.dpf2(24.0f));
        this.recordedAudioPlayButton.setImageMatrix(matrix);
        this.recordedAudioPlayButton.setImageDrawable(this.playPauseDrawable);
        this.recordedAudioPlayButton.setScaleType(ImageView.ScaleType.MATRIX);
        this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
        this.recordedAudioPanel.addView(this.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0f, 83, 48.0f, 0.0f, 13.0f, 0.0f));
        this.recordedAudioPlayButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda14
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatActivityEnterView.this.lambda$new$16(view2);
            }
        });
        TextView textView = new TextView(activity);
        this.recordedAudioTimeTextView = textView;
        textView.setTextColor(getThemedColor("chat_messagePanelVoiceDuration"));
        this.recordedAudioTimeTextView.setTextSize(1, 13.0f);
        linearLayout2.addView(this.recordedAudioSeekBar, LayoutHelper.createLinear(0, 32, 1.0f, 16, 0, 0, 4, 0));
        linearLayout2.addView(this.recordedAudioTimeTextView, LayoutHelper.createLinear(-2, -2, 0.0f, 16));
        FrameLayout frameLayout4 = new FrameLayout(activity);
        this.recordPanel = frameLayout4;
        frameLayout4.setClipChildren(false);
        this.recordPanel.setVisibility(8);
        frameLayout2.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48.0f));
        this.recordPanel.setOnTouchListener(ChatActivityEnterView$$ExternalSyntheticLambda30.INSTANCE);
        SlideTextView slideTextView = new SlideTextView(activity);
        this.slideText = slideTextView;
        this.recordPanel.addView(slideTextView, LayoutHelper.createFrame(-1, -1.0f, 0, 45.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout3 = new LinearLayout(activity);
        this.recordTimeContainer = linearLayout3;
        linearLayout3.setOrientation(0);
        this.recordTimeContainer.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        this.recordTimeContainer.setFocusable(false);
        this.recordPanel.addView(this.recordTimeContainer, LayoutHelper.createFrame(-1, -1, 16));
        RecordDot recordDot = new RecordDot(activity);
        this.recordDot = recordDot;
        this.recordTimeContainer.addView(recordDot, LayoutHelper.createLinear(28, 28, 16, 0, 0, 0, 0));
        TimerView timerView = new TimerView(activity);
        this.recordTimerView = timerView;
        this.recordTimeContainer.addView(timerView, LayoutHelper.createLinear(-1, -1, 16, 6, 0, 0, 0));
        FrameLayout frameLayout5 = new FrameLayout(activity) { // from class: org.telegram.ui.Components.ChatActivityEnterView.28
            {
                ChatActivityEnterView.this = this;
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View view2, long j) {
                if (view2 != ChatActivityEnterView.this.sendButton || !ChatActivityEnterView.this.textTransitionIsRunning) {
                    return super.drawChild(canvas, view2, j);
                }
                return true;
            }
        };
        this.sendButtonContainer = frameLayout5;
        frameLayout5.setClipChildren(false);
        this.sendButtonContainer.setClipToPadding(false);
        this.textFieldContainer.addView(this.sendButtonContainer, LayoutHelper.createFrame(48, 48, 85));
        FrameLayout frameLayout6 = new FrameLayout(activity);
        this.audioVideoButtonContainer = frameLayout6;
        frameLayout6.setSoundEffectsEnabled(false);
        this.sendButtonContainer.addView(this.audioVideoButtonContainer, LayoutHelper.createFrame(48, 48.0f));
        this.audioVideoButtonContainer.setFocusable(true);
        this.audioVideoButtonContainer.setImportantForAccessibility(1);
        this.audioVideoButtonContainer.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda29
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                boolean lambda$new$24;
                lambda$new$24 = ChatActivityEnterView.this.lambda$new$24(resourcesProvider, view2, motionEvent);
                return lambda$new$24;
            }
        });
        ChatActivityEnterViewAnimatedIconView chatActivityEnterViewAnimatedIconView2 = new ChatActivityEnterViewAnimatedIconView(activity);
        this.audioVideoSendButton = chatActivityEnterViewAnimatedIconView2;
        chatActivityEnterViewAnimatedIconView2.setFocusable(true);
        this.audioVideoSendButton.setImportantForAccessibility(1);
        this.audioVideoSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
        int dp2 = AndroidUtilities.dp(9.5f);
        this.audioVideoSendButton.setPadding(dp2, dp2, dp2, dp2);
        this.audioVideoSendButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.SRC_IN));
        this.audioVideoButtonContainer.addView(this.audioVideoSendButton, LayoutHelper.createFrame(48, 48.0f));
        RecordCircle recordCircle = new RecordCircle(activity);
        this.recordCircle = recordCircle;
        recordCircle.setVisibility(8);
        this.sizeNotifierLayout.addView(this.recordCircle, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
        ImageView imageView5 = new ImageView(activity);
        this.cancelBotButton = imageView5;
        imageView5.setVisibility(4);
        this.cancelBotButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageView imageView6 = this.cancelBotButton;
        CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2(this) { // from class: org.telegram.ui.Components.ChatActivityEnterView.29
            @Override // org.telegram.ui.Components.CloseProgressDrawable2
            protected int getCurrentColor() {
                return Theme.getColor("chat_messagePanelCancelInlineBot");
            }
        };
        this.progressDrawable = closeProgressDrawable2;
        imageView6.setImageDrawable(closeProgressDrawable2);
        this.cancelBotButton.setContentDescription(LocaleController.getString("Cancel", R.string.Cancel));
        this.cancelBotButton.setSoundEffectsEnabled(false);
        this.cancelBotButton.setScaleX(0.1f);
        this.cancelBotButton.setScaleY(0.1f);
        this.cancelBotButton.setAlpha(0.0f);
        if (i3 >= 21) {
            this.cancelBotButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(str)));
        }
        this.sendButtonContainer.addView(this.cancelBotButton, LayoutHelper.createFrame(48, 48.0f));
        this.cancelBotButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda24
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatActivityEnterView.this.lambda$new$25(view2);
            }
        });
        if (isInScheduleMode()) {
            Resources resources = activity.getResources();
            int i4 = R.drawable.input_schedule;
            this.sendButtonDrawable = resources.getDrawable(i4).mutate();
            this.sendButtonInverseDrawable = activity.getResources().getDrawable(i4).mutate();
            this.inactinveSendButtonDrawable = activity.getResources().getDrawable(i4).mutate();
        } else {
            Resources resources2 = activity.getResources();
            int i5 = R.drawable.ic_send;
            this.sendButtonDrawable = resources2.getDrawable(i5).mutate();
            this.sendButtonInverseDrawable = activity.getResources().getDrawable(i5).mutate();
            this.inactinveSendButtonDrawable = activity.getResources().getDrawable(i5).mutate();
        }
        View view2 = new View(activity) { // from class: org.telegram.ui.Components.ChatActivityEnterView.30
            private float animationDuration;
            private float animationProgress;
            private int drawableColor;
            private long lastAnimationTime;
            private int prevColorType;

            {
                ChatActivityEnterView.this = this;
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int themedColor;
                int dp3;
                float f;
                float dp4;
                int measuredWidth = (getMeasuredWidth() - ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth()) / 2;
                int measuredHeight = (getMeasuredHeight() - ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight()) / 2;
                if (ChatActivityEnterView.this.isInScheduleMode()) {
                    measuredHeight -= AndroidUtilities.dp(1.0f);
                } else {
                    measuredWidth += AndroidUtilities.dp(2.0f);
                }
                int i6 = 1;
                boolean z2 = ChatActivityEnterView.this.sendPopupWindow != null && ChatActivityEnterView.this.sendPopupWindow.isShowing();
                if (z2) {
                    themedColor = ChatActivityEnterView.this.getThemedColor("chat_messagePanelVoicePressed");
                } else {
                    themedColor = ChatActivityEnterView.this.getThemedColor("chat_messagePanelSend");
                    i6 = 2;
                }
                if (themedColor != this.drawableColor) {
                    this.lastAnimationTime = SystemClock.elapsedRealtime();
                    int i7 = this.prevColorType;
                    if (i7 != 0 && i7 != i6) {
                        this.animationProgress = 0.0f;
                        if (z2) {
                            this.animationDuration = 200.0f;
                        } else {
                            this.animationDuration = 120.0f;
                        }
                    } else {
                        this.animationProgress = 1.0f;
                    }
                    this.prevColorType = i6;
                    this.drawableColor = themedColor;
                    ChatActivityEnterView.this.sendButtonDrawable.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor("chat_messagePanelSend"), PorterDuff.Mode.MULTIPLY));
                    int themedColor2 = ChatActivityEnterView.this.getThemedColor("chat_messagePanelIcons");
                    ChatActivityEnterView.this.inactinveSendButtonDrawable.setColorFilter(new PorterDuffColorFilter(Color.argb(180, Color.red(themedColor2), Color.green(themedColor2), Color.blue(themedColor2)), PorterDuff.Mode.MULTIPLY));
                    ChatActivityEnterView.this.sendButtonInverseDrawable.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
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
                if (!z2) {
                    if (ChatActivityEnterView.this.slowModeTimer != Integer.MAX_VALUE || ChatActivityEnterView.this.isInScheduleMode()) {
                        ChatActivityEnterView.this.sendButtonDrawable.setBounds(measuredWidth, measuredHeight, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + measuredWidth, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + measuredHeight);
                        ChatActivityEnterView.this.sendButtonDrawable.draw(canvas);
                    } else {
                        ChatActivityEnterView.this.inactinveSendButtonDrawable.setBounds(measuredWidth, measuredHeight, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + measuredWidth, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + measuredHeight);
                        ChatActivityEnterView.this.inactinveSendButtonDrawable.draw(canvas);
                    }
                }
                if (z2 || this.animationProgress != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_messagePanelSend"));
                    int dp5 = AndroidUtilities.dp(20.0f);
                    if (z2) {
                        ChatActivityEnterView.this.sendButtonInverseDrawable.setAlpha(255);
                        float f3 = this.animationProgress;
                        if (f3 <= 0.25f) {
                            f = dp5;
                            dp4 = AndroidUtilities.dp(2.0f) * CubicBezierInterpolator.EASE_IN.getInterpolation(f3 / 0.25f);
                        } else {
                            float f4 = f3 - 0.25f;
                            if (f4 <= 0.5f) {
                                f = dp5;
                                dp4 = AndroidUtilities.dp(2.0f) - (AndroidUtilities.dp(3.0f) * CubicBezierInterpolator.EASE_IN.getInterpolation(f4 / 0.5f));
                            } else {
                                dp3 = (int) (dp5 + (-AndroidUtilities.dp(1.0f)) + (AndroidUtilities.dp(1.0f) * CubicBezierInterpolator.EASE_IN.getInterpolation((f4 - 0.5f) / 0.25f)));
                                dp5 = dp3;
                            }
                        }
                        dp3 = (int) (f + dp4);
                        dp5 = dp3;
                    } else {
                        int i8 = (int) ((1.0f - this.animationProgress) * 255.0f);
                        Theme.dialogs_onlineCirclePaint.setAlpha(i8);
                        ChatActivityEnterView.this.sendButtonInverseDrawable.setAlpha(i8);
                    }
                    canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, dp5, Theme.dialogs_onlineCirclePaint);
                    ChatActivityEnterView.this.sendButtonInverseDrawable.setBounds(measuredWidth, measuredHeight, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + measuredWidth, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + measuredHeight);
                    ChatActivityEnterView.this.sendButtonInverseDrawable.draw(canvas);
                }
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (getAlpha() <= 0.0f) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.sendButton = view2;
        view2.setVisibility(4);
        int themedColor = getThemedColor("chat_messagePanelSend");
        this.sendButton.setContentDescription(LocaleController.getString("Send", R.string.Send));
        this.sendButton.setSoundEffectsEnabled(false);
        this.sendButton.setScaleX(0.1f);
        this.sendButton.setScaleY(0.1f);
        this.sendButton.setAlpha(0.0f);
        if (i3 >= 21) {
            this.sendButton.setBackgroundDrawable(Theme.createSelectorDrawable(Color.argb(24, Color.red(themedColor), Color.green(themedColor), Color.blue(themedColor)), 1));
        }
        this.sendButtonContainer.addView(this.sendButton, LayoutHelper.createFrame(48, 48.0f));
        this.sendButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda23
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                ChatActivityEnterView.this.lambda$new$26(view3);
            }
        });
        this.sendButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda27
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view3) {
                boolean onSendLongClick;
                onSendLongClick = ChatActivityEnterView.this.onSendLongClick(view3);
                return onSendLongClick;
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(activity);
        this.slowModeButton = simpleTextView;
        simpleTextView.setTextSize(18);
        this.slowModeButton.setVisibility(4);
        this.slowModeButton.setSoundEffectsEnabled(false);
        this.slowModeButton.setScaleX(0.1f);
        this.slowModeButton.setScaleY(0.1f);
        this.slowModeButton.setAlpha(0.0f);
        this.slowModeButton.setPadding(0, 0, AndroidUtilities.dp(13.0f), 0);
        this.slowModeButton.setGravity(21);
        this.slowModeButton.setTextColor(getThemedColor("chat_messagePanelIcons"));
        this.sendButtonContainer.addView(this.slowModeButton, LayoutHelper.createFrame(64, 48, 53));
        this.slowModeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda20
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                ChatActivityEnterView.this.lambda$new$27(view3);
            }
        });
        this.slowModeButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda28
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view3) {
                boolean lambda$new$28;
                lambda$new$28 = ChatActivityEnterView.this.lambda$new$28(view3);
                return lambda$new$28;
            }
        });
        ImageView imageView7 = new ImageView(this, activity) { // from class: org.telegram.ui.Components.ChatActivityEnterView.31
            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (getAlpha() <= 0.0f) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.expandStickersButton = imageView7;
        imageView7.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView8 = this.expandStickersButton;
        AnimatedArrowDrawable animatedArrowDrawable = new AnimatedArrowDrawable(getThemedColor("chat_messagePanelIcons"), false);
        this.stickersArrow = animatedArrowDrawable;
        imageView8.setImageDrawable(animatedArrowDrawable);
        this.expandStickersButton.setVisibility(8);
        this.expandStickersButton.setScaleX(0.1f);
        this.expandStickersButton.setScaleY(0.1f);
        this.expandStickersButton.setAlpha(0.0f);
        if (i3 >= 21) {
            this.expandStickersButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(str)));
        }
        this.sendButtonContainer.addView(this.expandStickersButton, LayoutHelper.createFrame(48, 48.0f));
        this.expandStickersButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda19
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                ChatActivityEnterView.this.lambda$new$29(view3);
            }
        });
        this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", R.string.AccDescrExpandPanel));
        FrameLayout frameLayout7 = new FrameLayout(activity);
        this.doneButtonContainer = frameLayout7;
        frameLayout7.setVisibility(8);
        this.textFieldContainer.addView(this.doneButtonContainer, LayoutHelper.createFrame(48, 48, 85));
        this.doneButtonContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda15
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                ChatActivityEnterView.this.lambda$new$30(view3);
            }
        });
        ShapeDrawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(16.0f), getThemedColor("chat_messagePanelSend"));
        Drawable mutate3 = activity.getResources().getDrawable(R.drawable.input_done).mutate();
        this.doneCheckDrawable = mutate3;
        mutate3.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable2 = new CombinedDrawable(createCircleDrawable, mutate3, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable2.setCustomSize(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        ImageView imageView9 = new ImageView(activity);
        this.doneButtonImage = imageView9;
        imageView9.setScaleType(ImageView.ScaleType.CENTER);
        this.doneButtonImage.setImageDrawable(combinedDrawable2);
        this.doneButtonImage.setContentDescription(LocaleController.getString("Done", R.string.Done));
        this.doneButtonContainer.addView(this.doneButtonImage, LayoutHelper.createFrame(48, 48.0f));
        ContextProgressView contextProgressView = new ContextProgressView(activity, 0);
        this.doneButtonProgress = contextProgressView;
        contextProgressView.setVisibility(4);
        this.doneButtonContainer.addView(this.doneButtonProgress, LayoutHelper.createFrame(-1, -1.0f));
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        this.keyboardHeight = globalEmojiSettings.getInt("kbd_height", AndroidUtilities.dp(200.0f));
        this.keyboardHeightLand = globalEmojiSettings.getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
        setRecordVideoButtonVisible(false, false);
        checkSendButton(false);
        checkChannelRights();
        ChatActivityBotWebViewButton chatActivityBotWebViewButton = new ChatActivityBotWebViewButton(activity);
        this.botWebViewButton = chatActivityBotWebViewButton;
        chatActivityBotWebViewButton.setVisibility(8);
        this.botWebViewButton.setBotMenuButton(this.botCommandsMenuButton);
        frameLayout2.addView(this.botWebViewButton, LayoutHelper.createFrame(-1, -1, 80));
    }

    public /* synthetic */ void lambda$new$1(View view) {
        AdjustPanLayoutHelper adjustPanLayoutHelper = this.adjustPanLayoutHelper;
        if (adjustPanLayoutHelper == null || !adjustPanLayoutHelper.animationInProgress()) {
            if (hasBotWebView() && botCommandsMenuIsShowing()) {
                BotWebViewMenuContainer botWebViewMenuContainer = this.botWebViewMenuContainer;
                view.getClass();
                botWebViewMenuContainer.dismiss(new ChatActivityEnterView$$ExternalSyntheticLambda33(view));
                return;
            }
            boolean z = true;
            if (!isPopupShowing() || this.currentPopupContentType != 0) {
                showPopup(1, 0);
                EmojiView emojiView = this.emojiView;
                if (this.messageEditText.length() <= 0) {
                    z = false;
                }
                emojiView.onOpen(z);
                return;
            }
            if (this.searchingType != 0) {
                setSearchingTypeInternal(0, true);
                EmojiView emojiView2 = this.emojiView;
                if (emojiView2 != null) {
                    emojiView2.closeSearch(false);
                }
                this.messageEditText.requestFocus();
            }
            if (this.stickersExpanded) {
                setStickersExpanded(false, true, false);
                this.waitingForKeyboardOpenAfterAnimation = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda40
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatActivityEnterView.this.lambda$new$0();
                    }
                }, 200L);
                return;
            }
            openKeyboardInternal();
        }
    }

    public /* synthetic */ void lambda$new$0() {
        this.waitingForKeyboardOpenAfterAnimation = false;
        openKeyboardInternal();
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$12 */
    /* loaded from: classes3.dex */
    public class AnonymousClass12 extends EditTextCaption {
        final /* synthetic */ Activity val$context;
        final /* synthetic */ ChatActivity val$fragment;
        final /* synthetic */ Theme.ResourcesProvider val$resourcesProvider;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass12(Context context, Theme.ResourcesProvider resourcesProvider, Theme.ResourcesProvider resourcesProvider2, ChatActivity chatActivity, Activity activity) {
            super(context, resourcesProvider);
            ChatActivityEnterView.this = r1;
            this.val$resourcesProvider = resourcesProvider2;
            this.val$fragment = chatActivity;
            this.val$context = activity;
        }

        @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
        public void onScrollChanged(int i, int i2, int i3, int i4) {
            super.onScrollChanged(i, i2, i3, i4);
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.onEditTextScroll();
            }
        }

        @Override // org.telegram.ui.Components.EditTextCaption
        protected void onContextMenuOpen() {
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.onContextMenuOpen();
            }
        }

        @Override // org.telegram.ui.Components.EditTextCaption
        protected void onContextMenuClose() {
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.onContextMenuClose();
            }
        }

        /* renamed from: send */
        public void lambda$onCreateInputConnection$0(InputContentInfoCompat inputContentInfoCompat, boolean z, int i) {
            if (inputContentInfoCompat.getDescription().hasMimeType("image/gif")) {
                SendMessagesHelper.prepareSendingDocument(ChatActivityEnterView.this.accountInstance, null, null, inputContentInfoCompat.getContentUri(), null, "image/gif", ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), inputContentInfoCompat, null, z, 0);
            } else {
                SendMessagesHelper.prepareSendingPhoto(ChatActivityEnterView.this.accountInstance, null, inputContentInfoCompat.getContentUri(), ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), null, null, null, inputContentInfoCompat, 0, null, z, 0);
            }
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.onMessageSend(null, true, i);
            }
        }

        @Override // android.widget.TextView, android.view.View
        public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
            InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
            if (onCreateInputConnection == null) {
                return null;
            }
            try {
                EditorInfoCompat.setContentMimeTypes(editorInfo, new String[]{"image/gif", "image/*", "image/jpg", "image/png", "image/webp"});
                final Theme.ResourcesProvider resourcesProvider = this.val$resourcesProvider;
                return InputConnectionCompat.createWrapper(onCreateInputConnection, editorInfo, new InputConnectionCompat.OnCommitContentListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$12$$ExternalSyntheticLambda0
                    @Override // androidx.core.view.inputmethod.InputConnectionCompat.OnCommitContentListener
                    public final boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
                        boolean lambda$onCreateInputConnection$1;
                        lambda$onCreateInputConnection$1 = ChatActivityEnterView.AnonymousClass12.this.lambda$onCreateInputConnection$1(resourcesProvider, inputContentInfoCompat, i, bundle);
                        return lambda$onCreateInputConnection$1;
                    }
                });
            } catch (Throwable th) {
                FileLog.e(th);
                return onCreateInputConnection;
            }
        }

        public /* synthetic */ boolean lambda$onCreateInputConnection$1(Theme.ResourcesProvider resourcesProvider, final InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
            if (BuildCompat.isAtLeastNMR1() && (i & 1) != 0) {
                try {
                    inputContentInfoCompat.requestPermission();
                } catch (Exception unused) {
                    return false;
                }
            }
            if (inputContentInfoCompat.getDescription().hasMimeType("image/gif") || SendMessagesHelper.shouldSendWebPAsSticker(null, inputContentInfoCompat.getContentUri())) {
                if (ChatActivityEnterView.this.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$12$$ExternalSyntheticLambda4
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i2) {
                            ChatActivityEnterView.AnonymousClass12.this.lambda$onCreateInputConnection$0(inputContentInfoCompat, z, i2);
                        }
                    }, resourcesProvider);
                } else {
                    lambda$onCreateInputConnection$0(inputContentInfoCompat, true, 0);
                }
            } else {
                editPhoto(inputContentInfoCompat.getContentUri(), inputContentInfoCompat.getDescription().getMimeType(0));
            }
            return true;
        }

        @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ChatActivityEnterView.this.stickersDragging && ChatActivityEnterView.this.stickersExpansionAnim == null) {
                if (ChatActivityEnterView.this.isPopupShowing() && motionEvent.getAction() == 0) {
                    if (ChatActivityEnterView.this.searchingType != 0) {
                        ChatActivityEnterView.this.setSearchingTypeInternal(0, false);
                        ChatActivityEnterView.this.emojiView.closeSearch(false);
                        requestFocus();
                    }
                    ChatActivityEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 0);
                    if (!ChatActivityEnterView.this.stickersExpanded) {
                        ChatActivityEnterView.this.openKeyboardInternal();
                    } else {
                        ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        ChatActivityEnterView.this.waitingForKeyboardOpenAfterAnimation = true;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$12$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                ChatActivityEnterView.AnonymousClass12.this.lambda$onTouchEvent$2();
                            }
                        }, 200L);
                    }
                    return true;
                }
                try {
                    return super.onTouchEvent(motionEvent);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            return false;
        }

        public /* synthetic */ void lambda$onTouchEvent$2() {
            ChatActivityEnterView.this.waitingForKeyboardOpenAfterAnimation = false;
            ChatActivityEnterView.this.openKeyboardInternal();
        }

        @Override // android.view.View
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            if (ChatActivityEnterView.this.preventInput) {
                return false;
            }
            return super.dispatchKeyEvent(keyEvent);
        }

        @Override // org.telegram.ui.Components.EditTextEffects, android.widget.TextView
        public void onSelectionChanged(int i, int i2) {
            super.onSelectionChanged(i, i2);
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.onTextSelectionChanged(i, i2);
            }
        }

        @Override // org.telegram.ui.Components.EditTextBoldCursor
        protected void extendActionMode(ActionMode actionMode, Menu menu) {
            if (ChatActivityEnterView.this.parentFragment != null) {
                ChatActivityEnterView.this.parentFragment.extendActionMode(menu);
            }
        }

        @Override // android.view.View
        public boolean requestRectangleOnScreen(android.graphics.Rect rect) {
            rect.bottom += AndroidUtilities.dp(1000.0f);
            return super.requestRectangleOnScreen(rect);
        }

        @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
        public void onMeasure(int i, int i2) {
            ChatActivityEnterView.this.isInitLineCount = getMeasuredWidth() == 0 && getMeasuredHeight() == 0;
            super.onMeasure(i, i2);
            if (ChatActivityEnterView.this.isInitLineCount) {
                ChatActivityEnterView.this.lineCount = getLineCount();
            }
            ChatActivityEnterView.this.isInitLineCount = false;
        }

        @Override // org.telegram.ui.Components.EditTextCaption, android.widget.TextView
        public boolean onTextContextMenuItem(int i) {
            if (i == 16908322) {
                ChatActivityEnterView.this.isPaste = true;
            }
            ClipData primaryClip = ((ClipboardManager) getContext().getSystemService("clipboard")).getPrimaryClip();
            if (primaryClip != null && primaryClip.getItemCount() == 1 && primaryClip.getDescription().hasMimeType("image/*")) {
                editPhoto(primaryClip.getItemAt(0).getUri(), primaryClip.getDescription().getMimeType(0));
            }
            return super.onTextContextMenuItem(i);
        }

        private void editPhoto(final Uri uri, String str) {
            ChatActivity chatActivity = this.val$fragment;
            final File generatePicturePath = AndroidUtilities.generatePicturePath(chatActivity != null && chatActivity.isSecretChat(), MimeTypeMap.getSingleton().getExtensionFromMimeType(str));
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            final Activity activity = this.val$context;
            dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$12$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.AnonymousClass12.this.lambda$editPhoto$4(activity, uri, generatePicturePath);
                }
            });
        }

        public /* synthetic */ void lambda$editPhoto$4(Activity activity, Uri uri, final File file) {
            try {
                InputStream openInputStream = activity.getContentResolver().openInputStream(uri);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = openInputStream.read(bArr);
                    if (read > 0) {
                        fileOutputStream.write(bArr, 0, read);
                        fileOutputStream.flush();
                    } else {
                        openInputStream.close();
                        fileOutputStream.close();
                        MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, -1, 0L, file.getAbsolutePath(), 0, false, 0, 0, 0L);
                        final ArrayList arrayList = new ArrayList();
                        arrayList.add(photoEntry);
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$12$$ExternalSyntheticLambda3
                            @Override // java.lang.Runnable
                            public final void run() {
                                ChatActivityEnterView.AnonymousClass12.this.lambda$editPhoto$3(arrayList, file);
                            }
                        });
                        return;
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

        /* renamed from: openPhotoViewerForEdit */
        public void lambda$editPhoto$3(final ArrayList<Object> arrayList, final File file) {
            final MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(0);
            if (!ChatActivityEnterView.this.keyboardVisible) {
                PhotoViewer.getInstance().setParentActivity(ChatActivityEnterView.this.parentFragment, this.val$resourcesProvider);
                PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 2, false, new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.Components.ChatActivityEnterView.12.2
                    boolean sending;

                    @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                    public boolean canCaptureMorePhotos() {
                        return false;
                    }

                    {
                        AnonymousClass12.this = this;
                    }

                    @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                    public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
                        String str;
                        ArrayList arrayList2 = new ArrayList();
                        SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                        MediaController.PhotoEntry photoEntry2 = photoEntry;
                        boolean z3 = photoEntry2.isVideo;
                        if (!z3 && (str = photoEntry2.imagePath) != null) {
                            sendingMediaInfo.path = str;
                        } else {
                            String str2 = photoEntry2.path;
                            if (str2 != null) {
                                sendingMediaInfo.path = str2;
                            }
                        }
                        sendingMediaInfo.thumbPath = photoEntry2.thumbPath;
                        sendingMediaInfo.isVideo = z3;
                        CharSequence charSequence = photoEntry2.caption;
                        sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                        MediaController.PhotoEntry photoEntry3 = photoEntry;
                        sendingMediaInfo.entities = photoEntry3.entities;
                        sendingMediaInfo.masks = photoEntry3.stickers;
                        sendingMediaInfo.ttl = photoEntry3.ttl;
                        sendingMediaInfo.videoEditedInfo = videoEditedInfo;
                        sendingMediaInfo.canDeleteAfter = true;
                        arrayList2.add(sendingMediaInfo);
                        photoEntry.reset();
                        this.sending = true;
                        SendMessagesHelper.prepareSendingMedia(ChatActivityEnterView.this.accountInstance, arrayList2, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), null, false, false, ChatActivityEnterView.this.editingMessageObject, z, i2, SendMessagesHelper.checkUpdateStickersOrder(sendingMediaInfo.caption));
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onMessageSend(null, true, i2);
                        }
                    }

                    @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                    public void willHidePhotoViewer() {
                        if (!this.sending) {
                            try {
                                file.delete();
                            } catch (Throwable unused) {
                            }
                        }
                    }
                }, ChatActivityEnterView.this.parentFragment);
                return;
            }
            AndroidUtilities.hideKeyboard(ChatActivityEnterView.this.messageEditText);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.12.1
                {
                    AnonymousClass12.this = this;
                }

                @Override // java.lang.Runnable
                public void run() {
                    AnonymousClass12.this.lambda$editPhoto$3(arrayList, file);
                }
            }, 100L);
        }

        @Override // org.telegram.ui.Components.EditTextBoldCursor
        protected Theme.ResourcesProvider getResourcesProvider() {
            return this.val$resourcesProvider;
        }
    }

    public /* synthetic */ void lambda$new$2() {
        this.messageEditText.invalidateEffects();
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onTextSpansChanged(this.messageEditText.getText());
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$15 */
    /* loaded from: classes3.dex */
    public class AnonymousClass15 implements TextWatcher {
        private boolean ignorePrevTextChange;
        private boolean nextChangeIsSend;
        private CharSequence prevText;
        private boolean processChange;

        AnonymousClass15() {
            ChatActivityEnterView.this = r1;
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!this.ignorePrevTextChange && ChatActivityEnterView.this.recordingAudioVideo) {
                this.prevText = charSequence.toString();
            }
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            int currentPage;
            if (this.ignorePrevTextChange) {
                return;
            }
            boolean z = false;
            if (ChatActivityEnterView.this.emojiView != null) {
                currentPage = ChatActivityEnterView.this.emojiView.getCurrentPage();
            } else {
                currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
            }
            boolean z2 = currentPage != 0 && (ChatActivityEnterView.this.allowStickers || ChatActivityEnterView.this.allowGifs);
            if (((i2 == 0 && !TextUtils.isEmpty(charSequence)) || (i2 != 0 && TextUtils.isEmpty(charSequence))) && z2) {
                ChatActivityEnterView.this.setEmojiButtonImage(false, true);
            }
            if (ChatActivityEnterView.this.lineCount != ChatActivityEnterView.this.messageEditText.getLineCount()) {
                if (!ChatActivityEnterView.this.isInitLineCount && ChatActivityEnterView.this.messageEditText.getMeasuredWidth() > 0) {
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.onLineCountChanged(chatActivityEnterView.lineCount, ChatActivityEnterView.this.messageEditText.getLineCount());
                }
                ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                chatActivityEnterView2.lineCount = chatActivityEnterView2.messageEditText.getLineCount();
            }
            if (ChatActivityEnterView.this.innerTextChange == 1) {
                return;
            }
            if (ChatActivityEnterView.this.sendByEnter && !ChatActivityEnterView.this.isPaste && ChatActivityEnterView.this.editingMessageObject == null && i3 > i2 && charSequence.length() > 0 && charSequence.length() == i + i3 && charSequence.charAt(charSequence.length() - 1) == '\n') {
                this.nextChangeIsSend = true;
            }
            ChatActivityEnterView.this.isPaste = false;
            ChatActivityEnterView.this.checkSendButton(true);
            CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence.toString());
            if (ChatActivityEnterView.this.delegate != null && !ChatActivityEnterView.this.ignoreTextChange) {
                int i4 = i3 + 1;
                if (i2 > i4 || i3 - i2 > 2 || TextUtils.isEmpty(charSequence)) {
                    ChatActivityEnterView.this.messageWebPageSearch = true;
                }
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = ChatActivityEnterView.this.delegate;
                if (i2 > i4 || i3 - i2 > 2) {
                    z = true;
                }
                chatActivityEnterViewDelegate.onTextChanged(charSequence, z);
            }
            if (ChatActivityEnterView.this.innerTextChange != 2 && i3 - i2 > 1) {
                this.processChange = true;
            }
            if (ChatActivityEnterView.this.editingMessageObject != null || ChatActivityEnterView.this.canWriteToChannel || trimmedString.length() == 0 || ChatActivityEnterView.this.lastTypingTimeSend >= System.currentTimeMillis() - 5000 || ChatActivityEnterView.this.ignoreTextChange) {
                return;
            }
            ChatActivityEnterView.this.lastTypingTimeSend = System.currentTimeMillis();
            if (ChatActivityEnterView.this.delegate == null) {
                return;
            }
            ChatActivityEnterView.this.delegate.needSendTyping();
        }

        /* JADX WARN: Removed duplicated region for block: B:103:0x015f  */
        /* JADX WARN: Removed duplicated region for block: B:116:0x01af  */
        @Override // android.text.TextWatcher
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void afterTextChanged(android.text.Editable r11) {
            /*
                Method dump skipped, instructions count: 440
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.AnonymousClass15.afterTextChanged(android.text.Editable):void");
        }

        public /* synthetic */ void lambda$afterTextChanged$0(ValueAnimator valueAnimator) {
            int themedColor = ChatActivityEnterView.this.getThemedColor("chat_messagePanelVoicePressed");
            int alpha = Color.alpha(themedColor);
            ChatActivityEnterView.this.doneButtonEnabledProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ChatActivityEnterView.this.doneCheckDrawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(themedColor, (int) (alpha * ((ChatActivityEnterView.this.doneButtonEnabledProgress * 0.42f) + 0.58f))), PorterDuff.Mode.MULTIPLY));
            ChatActivityEnterView.this.doneButtonImage.invalidate();
        }
    }

    public /* synthetic */ void lambda$new$3(View view) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.openScheduledMessages();
        }
    }

    public /* synthetic */ void lambda$new$4(View view) {
        boolean z = !this.botCommandsMenuButton.isOpened();
        this.botCommandsMenuButton.setOpened(z);
        try {
            performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        if (!hasBotWebView()) {
            if (z) {
                this.botCommandsMenuContainer.show();
            } else {
                this.botCommandsMenuContainer.dismiss();
            }
        } else if (z) {
            if (this.emojiViewVisible || this.botKeyboardViewVisible) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda45
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatActivityEnterView.this.openWebViewMenu();
                    }
                }, 275L);
                hidePopup(false);
                return;
            }
            openWebViewMenu();
        } else {
            this.botWebViewMenuContainer.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$17 */
    /* loaded from: classes3.dex */
    public class AnonymousClass17 implements RecyclerListView.OnItemClickListener {
        final /* synthetic */ ChatActivity val$fragment;
        final /* synthetic */ Theme.ResourcesProvider val$resourcesProvider;

        AnonymousClass17(Theme.ResourcesProvider resourcesProvider, ChatActivity chatActivity) {
            ChatActivityEnterView.this = r1;
            this.val$resourcesProvider = resourcesProvider;
            this.val$fragment = chatActivity;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
        public void onItemClick(View view, int i) {
            if (view instanceof BotCommandsMenuView.BotCommandView) {
                final String command = ((BotCommandsMenuView.BotCommandView) view).getCommand();
                if (TextUtils.isEmpty(command)) {
                    return;
                }
                if (ChatActivityEnterView.this.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.dialog_id, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$17$$ExternalSyntheticLambda0
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i2) {
                            ChatActivityEnterView.AnonymousClass17.this.lambda$onItemClick$0(command, z, i2);
                        }
                    }, this.val$resourcesProvider);
                    return;
                }
                ChatActivity chatActivity = this.val$fragment;
                if (chatActivity != null && chatActivity.checkSlowMode(view)) {
                    return;
                }
                SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendMessage(command, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), null, false, null, null, null, true, 0, null, false);
                ChatActivityEnterView.this.setFieldText("");
                ChatActivityEnterView.this.botCommandsMenuContainer.dismiss();
            }
        }

        public /* synthetic */ void lambda$onItemClick$0(String str, boolean z, int i) {
            SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendMessage(str, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), null, false, null, null, null, z, i, null, false);
            ChatActivityEnterView.this.setFieldText("");
            ChatActivityEnterView.this.botCommandsMenuContainer.dismiss();
        }
    }

    public /* synthetic */ void lambda$new$5() {
        if (this.botButtonsMessageObject == null || !TextUtils.isEmpty(this.messageEditText.getText()) || this.botWebViewMenuContainer.hasSavedText()) {
            return;
        }
        showPopup(1, 1);
    }

    public /* synthetic */ void lambda$new$6(View view) {
        if (hasBotWebView() && botCommandsMenuIsShowing()) {
            BotWebViewMenuContainer botWebViewMenuContainer = this.botWebViewMenuContainer;
            view.getClass();
            botWebViewMenuContainer.dismiss(new ChatActivityEnterView$$ExternalSyntheticLambda33(view));
            return;
        }
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
        if (!this.stickersExpanded) {
            return;
        }
        setStickersExpanded(false, false, false);
    }

    public /* synthetic */ void lambda$new$7(View view) {
        AdjustPanLayoutHelper adjustPanLayoutHelper = this.adjustPanLayoutHelper;
        if (adjustPanLayoutHelper == null || !adjustPanLayoutHelper.animationInProgress()) {
            this.delegate.didPressAttachButton();
        }
    }

    public /* synthetic */ void lambda$new$14(Activity activity, View view) {
        int i;
        int i2;
        if (getTranslationY() != 0.0f) {
            this.onEmojiSearchClosed = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda43
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.this.lambda$new$8();
                }
            };
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
                this.onKeyboardClosed = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda44
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatActivityEnterView.this.lambda$new$9();
                    }
                };
                closeKeyboard();
                return;
            }
        }
        if (this.delegate.getSendAsPeers() == null) {
            return;
        }
        try {
            view.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
        int i3 = 0;
        if (senderSelectPopup != null) {
            senderSelectPopup.setPauseNotifications(false);
            this.senderSelectPopupWindow.startDismissAnimation(new SpringAnimation[0]);
            return;
        }
        final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        final TLRPC$ChatFull chatFull = messagesController.getChatFull(-this.dialog_id);
        if (chatFull == null) {
            return;
        }
        final ViewGroup overlayContainerView = this.parentFragment.getParentLayout().getOverlayContainerView();
        SenderSelectPopup senderSelectPopup2 = new SenderSelectPopup(activity, this.parentFragment, messagesController, chatFull, this.delegate.getSendAsPeers(), new SenderSelectPopup.OnSelectCallback() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda60
            @Override // org.telegram.ui.Components.SenderSelectPopup.OnSelectCallback
            public final void onPeerSelected(RecyclerView recyclerView, SenderSelectPopup.SenderView senderView, TLRPC$Peer tLRPC$Peer) {
                ChatActivityEnterView.this.lambda$new$13(chatFull, messagesController, recyclerView, senderView, tLRPC$Peer);
            }
        }) { // from class: org.telegram.ui.Components.ChatActivityEnterView.21
            {
                ChatActivityEnterView.this = this;
            }

            @Override // org.telegram.ui.Components.SenderSelectPopup, org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
            public void dismiss() {
                if (ChatActivityEnterView.this.senderSelectPopupWindow == this) {
                    ChatActivityEnterView.this.senderSelectPopupWindow = null;
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
                    return;
                }
                overlayContainerView.removeView(this.dimView);
                super.dismiss();
            }
        };
        this.senderSelectPopupWindow = senderSelectPopup2;
        senderSelectPopup2.setPauseNotifications(true);
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
            overlayContainerView.addView(this.senderSelectPopupWindow.dimView, new FrameLayout.LayoutParams(-1, i4 + i2 + measuredHeight + dp + AndroidUtilities.dp(2.0f)));
        } else {
            if (!this.parentFragment.isInBubbleMode()) {
                i3 = AndroidUtilities.statusBarHeight;
            }
            int dp2 = AndroidUtilities.dp(14.0f);
            this.senderSelectPopupWindow.recyclerContainer.getLayoutParams().height = ((contentViewHeight2 - i3) - dp2) - getHeightWithTopView();
            overlayContainerView.addView(this.senderSelectPopupWindow.dimView, new FrameLayout.LayoutParams(-1, dp2 + i3 + this.senderSelectPopupWindow.recyclerContainer.getLayoutParams().height + dp));
            i2 = i3;
        }
        this.senderSelectPopupWindow.startShowAnimation();
        SenderSelectPopup senderSelectPopup3 = this.senderSelectPopupWindow;
        this.popupX = i;
        this.popupY = i2;
        senderSelectPopup3.showAtLocation(view, 51, i, i2);
        this.senderSelectView.setProgress(1.0f);
    }

    public /* synthetic */ void lambda$new$8() {
        this.senderSelectView.callOnClick();
    }

    public /* synthetic */ void lambda$new$9() {
        this.senderSelectView.callOnClick();
    }

    public /* synthetic */ void lambda$new$13(TLRPC$ChatFull tLRPC$ChatFull, MessagesController messagesController, RecyclerView recyclerView, final SenderSelectPopup.SenderView senderView, TLRPC$Peer tLRPC$Peer) {
        TLRPC$User user;
        if (this.senderSelectPopupWindow == null) {
            return;
        }
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
        final int[] iArr = new int[2];
        boolean isSelected = senderView.avatar.isSelected();
        senderView.avatar.getLocationInWindow(iArr);
        senderView.avatar.setSelected(true, true);
        final SimpleAvatarView simpleAvatarView = new SimpleAvatarView(getContext());
        long j4 = tLRPC$Peer.channel_id;
        if (j4 != 0) {
            TLRPC$Chat chat = messagesController.getChat(Long.valueOf(j4));
            if (chat != null) {
                simpleAvatarView.setAvatar(chat);
            }
        } else {
            long j5 = tLRPC$Peer.user_id;
            if (j5 != 0 && (user = messagesController.getUser(Long.valueOf(j5))) != null) {
                simpleAvatarView.setAvatar(user);
            }
        }
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View childAt = recyclerView.getChildAt(i);
            if ((childAt instanceof SenderSelectPopup.SenderView) && childAt != senderView) {
                ((SenderSelectPopup.SenderView) childAt).avatar.setSelected(false, true);
            }
        }
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda50
            @Override // java.lang.Runnable
            public final void run() {
                ChatActivityEnterView.this.lambda$new$12(simpleAvatarView, iArr, senderView);
            }
        };
        if (!isSelected) {
            j3 = 200;
        }
        AndroidUtilities.runOnUIThread(runnable, j3);
    }

    public /* synthetic */ void lambda$new$12(final SimpleAvatarView simpleAvatarView, int[] iArr, SenderSelectPopup.SenderView senderView) {
        if (this.senderSelectPopupWindow == null) {
            return;
        }
        final Dialog dialog = new Dialog(getContext(), R.style.TransparentDialogNoAnimation);
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.addView(simpleAvatarView, LayoutHelper.createFrame(40, 40, 3));
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
            AndroidUtilities.setLightStatusBar(dialog.getWindow(), Theme.getColor("actionBarDefault", null, true) == -1);
            if (i >= 26) {
                AndroidUtilities.setLightNavigationBar(dialog.getWindow(), AndroidUtilities.computePerceivedBrightness(Theme.getColor("windowBackgroundGray", null, true)) >= 0.721f);
            }
        }
        if (i >= 23) {
            this.popupX += getRootWindowInsets().getSystemWindowInsetLeft();
        }
        this.senderSelectView.getLocationInWindow(this.location);
        int[] iArr2 = this.location;
        final float f = iArr2[0];
        final float f2 = iArr2[1];
        float dp = AndroidUtilities.dp(5.0f);
        float dp2 = iArr[0] + this.popupX + dp + AndroidUtilities.dp(4.0f) + 0.0f;
        float f3 = iArr[1] + this.popupY + dp + 0.0f;
        simpleAvatarView.setTranslationX(dp2);
        simpleAvatarView.setTranslationY(f3);
        float dp3 = this.senderSelectView.getLayoutParams().width / AndroidUtilities.dp(40.0f);
        simpleAvatarView.setPivotX(0.0f);
        simpleAvatarView.setPivotY(0.0f);
        simpleAvatarView.setScaleX(0.75f);
        simpleAvatarView.setScaleY(0.75f);
        simpleAvatarView.getViewTreeObserver().addOnDrawListener(new AnonymousClass22(this, simpleAvatarView, senderView));
        dialog.show();
        this.senderSelectView.setScaleX(1.0f);
        this.senderSelectView.setScaleY(1.0f);
        this.senderSelectView.setAlpha(1.0f);
        SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
        SenderSelectView senderSelectView = this.senderSelectView;
        DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.SCALE_X;
        SenderSelectView senderSelectView2 = this.senderSelectView;
        DynamicAnimation.ViewProperty viewProperty2 = DynamicAnimation.SCALE_Y;
        senderSelectPopup.startDismissAnimation(new SpringAnimation(senderSelectView, viewProperty).setSpring(new SpringForce(0.5f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(senderSelectView2, viewProperty2).setSpring(new SpringForce(0.5f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.senderSelectView, DynamicAnimation.ALPHA).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda32
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f4, float f5) {
                ChatActivityEnterView.this.lambda$new$10(dialog, simpleAvatarView, f, f2, dynamicAnimation, z, f4, f5);
            }
        }), new SpringAnimation(simpleAvatarView, DynamicAnimation.TRANSLATION_X).setStartValue(MathUtils.clamp(dp2, f - AndroidUtilities.dp(6.0f), dp2)).setSpring(new SpringForce(f).setStiffness(700.0f).setDampingRatio(0.75f)).setMinValue(f - AndroidUtilities.dp(6.0f)), new SpringAnimation(simpleAvatarView, DynamicAnimation.TRANSLATION_Y).setStartValue(MathUtils.clamp(f3, f3, AndroidUtilities.dp(6.0f) + f2)).setSpring(new SpringForce(f2).setStiffness(700.0f).setDampingRatio(0.75f)).setMaxValue(AndroidUtilities.dp(6.0f) + f2).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener(this) { // from class: org.telegram.ui.Components.ChatActivityEnterView.24
            boolean performedHapticFeedback = false;

            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f4, float f5) {
                if (this.performedHapticFeedback || f4 < f2) {
                    return;
                }
                this.performedHapticFeedback = true;
                try {
                    simpleAvatarView.performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
            }
        }).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda31
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f4, float f5) {
                ChatActivityEnterView.this.lambda$new$11(dialog, simpleAvatarView, f, f2, dynamicAnimation, z, f4, f5);
            }
        }), new SpringAnimation(simpleAvatarView, viewProperty).setSpring(new SpringForce(dp3).setStiffness(1000.0f).setDampingRatio(1.0f)), new SpringAnimation(simpleAvatarView, viewProperty2).setSpring(new SpringForce(dp3).setStiffness(1000.0f).setDampingRatio(1.0f)));
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$22 */
    /* loaded from: classes3.dex */
    public class AnonymousClass22 implements ViewTreeObserver.OnDrawListener {
        final /* synthetic */ SimpleAvatarView val$avatar;
        final /* synthetic */ SenderSelectPopup.SenderView val$senderView;

        AnonymousClass22(ChatActivityEnterView chatActivityEnterView, SimpleAvatarView simpleAvatarView, SenderSelectPopup.SenderView senderView) {
            this.val$avatar = simpleAvatarView;
            this.val$senderView = senderView;
        }

        @Override // android.view.ViewTreeObserver.OnDrawListener
        public void onDraw() {
            final SimpleAvatarView simpleAvatarView = this.val$avatar;
            final SenderSelectPopup.SenderView senderView = this.val$senderView;
            simpleAvatarView.post(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$22$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.AnonymousClass22.this.lambda$onDraw$0(simpleAvatarView, senderView);
                }
            });
        }

        public /* synthetic */ void lambda$onDraw$0(SimpleAvatarView simpleAvatarView, SenderSelectPopup.SenderView senderView) {
            simpleAvatarView.getViewTreeObserver().removeOnDrawListener(this);
            senderView.avatar.setHideAvatar(true);
        }
    }

    public /* synthetic */ void lambda$new$10(final Dialog dialog, SimpleAvatarView simpleAvatarView, float f, float f2, DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
        if (dialog.isShowing()) {
            simpleAvatarView.setTranslationX(f);
            simpleAvatarView.setTranslationY(f2);
            this.senderSelectView.setProgress(0.0f, false);
            this.senderSelectView.setScaleX(1.0f);
            this.senderSelectView.setScaleY(1.0f);
            this.senderSelectView.setAlpha(1.0f);
            this.senderSelectView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.23
                {
                    ChatActivityEnterView.this = this;
                }

                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    ChatActivityEnterView.this.senderSelectView.getViewTreeObserver().removeOnPreDrawListener(this);
                    SenderSelectView senderSelectView = ChatActivityEnterView.this.senderSelectView;
                    Dialog dialog2 = dialog;
                    dialog2.getClass();
                    senderSelectView.postDelayed(new ChatActivityEnterView$23$$ExternalSyntheticLambda0(dialog2), 100L);
                    return true;
                }
            });
        }
    }

    public /* synthetic */ void lambda$new$11(final Dialog dialog, SimpleAvatarView simpleAvatarView, float f, float f2, DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
        if (dialog.isShowing()) {
            simpleAvatarView.setTranslationX(f);
            simpleAvatarView.setTranslationY(f2);
            this.senderSelectView.setProgress(0.0f, false);
            this.senderSelectView.setScaleX(1.0f);
            this.senderSelectView.setScaleY(1.0f);
            this.senderSelectView.setAlpha(1.0f);
            this.senderSelectView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.25
                {
                    ChatActivityEnterView.this = this;
                }

                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    ChatActivityEnterView.this.senderSelectView.getViewTreeObserver().removeOnPreDrawListener(this);
                    SenderSelectView senderSelectView = ChatActivityEnterView.this.senderSelectView;
                    Dialog dialog2 = dialog;
                    dialog2.getClass();
                    senderSelectView.postDelayed(new ChatActivityEnterView$23$$ExternalSyntheticLambda0(dialog2), 100L);
                    return true;
                }
            });
        }
    }

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

    public /* synthetic */ void lambda$new$16(View view) {
        if (this.audioToSend == null) {
            return;
        }
        if (MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject) && !MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().lambda$startAudioAgain$7(this.audioToSendMessageObject);
            this.playPauseDrawable.setIcon(0, true);
            this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
            return;
        }
        this.playPauseDrawable.setIcon(1, true);
        MediaController.getInstance().playMessage(this.audioToSendMessageObject);
        this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPause", R.string.AccActionPause));
    }

    public /* synthetic */ boolean lambda$new$24(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        int i = 3;
        if (motionEvent.getAction() == 0) {
            if (this.recordCircle.isSendButtonVisible()) {
                boolean z = this.hasRecordVideo;
                if (!z || this.calledRecordRunnable) {
                    this.startedDraggingX = -1.0f;
                    if (z && isInVideoMode()) {
                        this.delegate.needStartRecordVideo(1, true, 0);
                    } else {
                        if (this.recordingAudioVideo && isInScheduleMode()) {
                            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), ChatActivityEnterView$$ExternalSyntheticLambda56.INSTANCE, ChatActivityEnterView$$ExternalSyntheticLambda52.INSTANCE, resourcesProvider);
                        }
                        MediaController mediaController = MediaController.getInstance();
                        if (!isInScheduleMode()) {
                            i = 1;
                        }
                        mediaController.stopRecording(i, true, 0);
                        this.delegate.needStartRecordAudio(0);
                    }
                    this.recordingAudioVideo = false;
                    this.messageTransitionIsRunning = false;
                    Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda37
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatActivityEnterView.this.lambda$new$20();
                        }
                    };
                    this.moveToSendStateRunnable = runnable;
                    AndroidUtilities.runOnUIThread(runnable, 200L);
                }
                return false;
            }
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
                AndroidUtilities.runOnUIThread(this.recordAudioVideoRunnable, 150L);
            } else {
                this.recordAudioVideoRunnable.run();
            }
            return true;
        }
        float f = 1.0f;
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent.getAction() == 3 && this.recordingAudioVideo) {
                if (this.recordCircle.slideToCancelProgress >= 0.7f) {
                    this.recordCircle.sendButtonVisible = true;
                    startLockTransition();
                } else {
                    if (this.hasRecordVideo && isInVideoMode()) {
                        CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                        this.delegate.needStartRecordVideo(2, true, 0);
                    } else {
                        this.delegate.needStartRecordAudio(0);
                        MediaController.getInstance().stopRecording(0, false, 0);
                    }
                    this.recordingAudioVideo = false;
                    updateRecordInterface(5);
                }
                return false;
            } else if (this.recordCircle.isSendButtonVisible() || this.recordedAudioPanel.getVisibility() == 0) {
                if (this.recordAudioVideoRunnableStarted) {
                    AndroidUtilities.cancelRunOnUIThread(this.recordAudioVideoRunnable);
                }
                return false;
            } else {
                if ((((motionEvent.getX() + this.audioVideoButtonContainer.getX()) - this.startedDraggingX) / this.distCanMove) + 1.0f < 0.45d) {
                    if (this.hasRecordVideo && isInVideoMode()) {
                        CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                        this.delegate.needStartRecordVideo(2, true, 0);
                    } else {
                        this.delegate.needStartRecordAudio(0);
                        MediaController.getInstance().stopRecording(0, false, 0);
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
                    boolean z2 = this.hasRecordVideo;
                    if (!z2 || this.calledRecordRunnable) {
                        this.startedDraggingX = -1.0f;
                        if (z2 && isInVideoMode()) {
                            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                            this.delegate.needStartRecordVideo(1, true, 0);
                        } else {
                            if (this.recordingAudioVideo && isInScheduleMode()) {
                                AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), ChatActivityEnterView$$ExternalSyntheticLambda57.INSTANCE, ChatActivityEnterView$$ExternalSyntheticLambda51.INSTANCE, resourcesProvider);
                            }
                            this.delegate.needStartRecordAudio(0);
                            MediaController mediaController2 = MediaController.getInstance();
                            if (!isInScheduleMode()) {
                                i = 1;
                            }
                            mediaController2.stopRecording(i, true, 0);
                        }
                        this.recordingAudioVideo = false;
                        this.messageTransitionIsRunning = false;
                        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda46
                            @Override // java.lang.Runnable
                            public final void run() {
                                ChatActivityEnterView.this.lambda$new$23();
                            }
                        };
                        this.moveToSendStateRunnable = runnable2;
                        AndroidUtilities.runOnUIThread(runnable2, 500L);
                    }
                }
                return true;
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
                double measuredWidth = this.sizeNotifierLayout.getMeasuredWidth();
                Double.isNaN(measuredWidth);
                float f2 = (float) (measuredWidth * 0.35d);
                this.distCanMove = f2;
                if (f2 > AndroidUtilities.dp(140.0f)) {
                    this.distCanMove = AndroidUtilities.dp(140.0f);
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
                if (this.hasRecordVideo && isInVideoMode()) {
                    CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                    this.delegate.needStartRecordVideo(2, true, 0);
                } else {
                    this.delegate.needStartRecordAudio(0);
                    MediaController.getInstance().stopRecording(0, false, 0);
                }
                this.recordingAudioVideo = false;
                updateRecordInterface(5);
            }
            return true;
        } else {
            view.onTouchEvent(motionEvent);
            return true;
        }
    }

    public static /* synthetic */ void lambda$new$18(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }

    public static /* synthetic */ void lambda$new$19() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }

    public /* synthetic */ void lambda$new$20() {
        this.moveToSendStateRunnable = null;
        updateRecordInterface(1);
    }

    public static /* synthetic */ void lambda$new$21(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }

    public static /* synthetic */ void lambda$new$22() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }

    public /* synthetic */ void lambda$new$23() {
        this.moveToSendStateRunnable = null;
        updateRecordInterface(1);
    }

    public /* synthetic */ void lambda$new$25(View view) {
        String obj = this.messageEditText.getText().toString();
        int indexOf = obj.indexOf(32);
        if (indexOf == -1 || indexOf == obj.length() - 1) {
            setFieldText("");
        } else {
            setFieldText(obj.substring(0, indexOf + 1));
        }
    }

    public /* synthetic */ void lambda$new$26(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            AnimatorSet animatorSet = this.runningAnimationAudio;
            if ((animatorSet != null && animatorSet.isRunning()) || this.moveToSendStateRunnable != null) {
                return;
            }
            sendMessage();
        }
    }

    public /* synthetic */ void lambda$new$27(View view) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            SimpleTextView simpleTextView = this.slowModeButton;
            chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
        }
    }

    public /* synthetic */ boolean lambda$new$28(View view) {
        if (this.messageEditText.length() == 0) {
            return false;
        }
        return onSendLongClick(view);
    }

    public /* synthetic */ void lambda$new$29(View view) {
        EmojiView emojiView;
        if (this.expandStickersButton.getVisibility() == 0 && this.expandStickersButton.getAlpha() == 1.0f && !this.waitingForKeyboardOpen) {
            if (this.keyboardVisible && this.messageEditText.isFocused()) {
                return;
            }
            if (this.stickersExpanded) {
                if (this.searchingType != 0) {
                    setSearchingTypeInternal(0, true);
                    this.emojiView.closeSearch(true);
                    this.emojiView.hideSearchKeyboard();
                    if (this.emojiTabOpen) {
                        checkSendButton(true);
                    }
                } else if (!this.stickersDragging && (emojiView = this.emojiView) != null) {
                    emojiView.showSearchField(false);
                }
            } else if (!this.stickersDragging) {
                this.emojiView.showSearchField(true);
            }
            if (this.stickersDragging) {
                return;
            }
            setStickersExpanded(!this.stickersExpanded, true, false);
        }
    }

    public /* synthetic */ void lambda$new$30(View view) {
        doneEditingMessage();
    }

    public void openWebViewMenu() {
        final Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                ChatActivityEnterView.this.lambda$openWebViewMenu$31();
            }
        };
        if (SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, this.dialog_id)) {
            runnable.run();
        } else {
            new AlertDialog.Builder(this.parentFragment.getParentActivity()).setTitle(LocaleController.getString(R.string.BotOpenPageTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotOpenPageMessage, UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.dialog_id)))))).setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda8
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatActivityEnterView.this.lambda$openWebViewMenu$32(runnable, dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda11
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ChatActivityEnterView.this.lambda$openWebViewMenu$33(dialogInterface);
                }
            }).show();
        }
    }

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

    public /* synthetic */ void lambda$openWebViewMenu$32(Runnable runnable, DialogInterface dialogInterface, int i) {
        runnable.run();
        SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, this.dialog_id, true);
    }

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

    public void checkBotMenu() {
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        if (botCommandsMenuView != null) {
            boolean z = botCommandsMenuView.expanded;
            botCommandsMenuView.setExpanded(TextUtils.isEmpty(this.messageEditText.getText()) && !this.keyboardVisible && !this.waitingForKeyboardOpen && !isPopupShowing(), true);
            if (z == this.botCommandsMenuButton.expanded) {
                return;
            }
            beginDelayedTransition();
        }
    }

    public void forceSmoothKeyboard(boolean z) {
        ChatActivity chatActivity;
        this.smoothKeyboard = z && SharedConfig.smoothKeyboard && !AndroidUtilities.isInMultiwindow && ((chatActivity = this.parentFragment) == null || !chatActivity.isInBubbleMode());
    }

    private void startLockTransition() {
        AnimatorSet animatorSet = new AnimatorSet();
        performHapticFeedback(3, 2);
        RecordCircle recordCircle = this.recordCircle;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(recordCircle, "lockAnimatedTranslation", recordCircle.startTranslation);
        ofFloat.setStartDelay(100L);
        ofFloat.setDuration(350L);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.recordCircle, "snapAnimationProgress", 1.0f);
        ofFloat2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        ofFloat2.setDuration(250L);
        SharedConfig.removeLockRecordAudioVideoHint();
        animatorSet.playTogether(ofFloat2, ofFloat, ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", 1.0f).setDuration(200L), ObjectAnimator.ofFloat(this.slideText, "cancelToProgress", 1.0f));
        animatorSet.start();
    }

    public int getBackgroundTop() {
        int top = getTop();
        View view = this.topView;
        return (view == null || view.getVisibility() != 0) ? top : top + this.topView.getLayoutParams().height;
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        boolean z = view == this.topView || view == this.textFieldContainer;
        if (z) {
            canvas.save();
            if (view == this.textFieldContainer) {
                int dp = (int) (this.animatedTop + AndroidUtilities.dp(2.0f) + this.chatSearchExpandOffset);
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

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int intrinsicHeight = (int) (this.animatedTop + (Theme.chat_composeShadowDrawable.getIntrinsicHeight() * (1.0f - this.composeShadowAlpha)));
        View view = this.topView;
        if (view != null && view.getVisibility() == 0) {
            intrinsicHeight = (int) (intrinsicHeight + ((1.0f - this.topViewEnterProgress) * this.topView.getLayoutParams().height));
        }
        int intrinsicHeight2 = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + intrinsicHeight;
        Theme.chat_composeShadowDrawable.setAlpha((int) (this.composeShadowAlpha * 255.0f));
        Theme.chat_composeShadowDrawable.setBounds(0, intrinsicHeight, getMeasuredWidth(), intrinsicHeight2);
        Theme.chat_composeShadowDrawable.draw(canvas);
        int i = (int) (intrinsicHeight2 + this.chatSearchExpandOffset);
        if (this.allowBlur) {
            this.backgroundPaint.setColor(getThemedColor("chat_messagePanelBackground"));
            if (SharedConfig.chatBlurEnabled() && this.sizeNotifierLayout != null) {
                android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                rect.set(0, i, getWidth(), getHeight());
                this.sizeNotifierLayout.drawBlurRect(canvas, getTop(), rect, this.backgroundPaint, false);
                return;
            }
            canvas.drawRect(0.0f, i, getWidth(), getHeight(), this.backgroundPaint);
            return;
        }
        canvas.drawRect(0.0f, i, getWidth(), getHeight(), getThemedPaint("paintChatComposeBackground"));
    }

    /* JADX WARN: Can't wrap try/catch for region: R(11:5|(1:55)(1:9)|10|(8:12|(1:36)(1:16)|(1:35)(1:22)|23|(3:25|(1:27)(1:29)|28)|(1:31)|32|(1:34))|37|(4:39|(1:53)(1:43)|44|(5:46|47|48|49|50))|54|47|48|49|50) */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onSendLongClick(android.view.View r14) {
        /*
            Method dump skipped, instructions count: 429
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.onSendLongClick(android.view.View):boolean");
    }

    public /* synthetic */ void lambda$onSendLongClick$34(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() != 4 || keyEvent.getRepeatCount() != 0 || (actionBarPopupWindow = this.sendPopupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.sendPopupWindow.dismiss();
    }

    public /* synthetic */ void lambda$onSendLongClick$35(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new ChatActivityEnterView$$ExternalSyntheticLambda54(this), this.resourcesProvider);
    }

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
        if (this.audioVideoSendButton == null) {
            return;
        }
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

    public boolean isRecordingAudioVideo() {
        AnimatorSet animatorSet;
        return this.recordingAudioVideo || ((animatorSet = this.runningAnimationAudio) != null && animatorSet.isRunning());
    }

    public boolean isRecordLocked() {
        return this.recordingAudioVideo && this.recordCircle.isSendButtonVisible();
    }

    public void cancelRecordingAudioVideo() {
        if (this.hasRecordVideo && isInVideoMode()) {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(5, true, 0);
        } else {
            this.delegate.needStartRecordAudio(0);
            MediaController.getInstance().stopRecording(0, false, 0);
        }
        this.recordingAudioVideo = false;
        updateRecordInterface(2);
    }

    public void showContextProgress(boolean z) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 == null) {
            return;
        }
        if (z) {
            closeProgressDrawable2.startAnimation();
        } else {
            closeProgressDrawable2.stopAnimation();
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

    public void updateSlowModeText() {
        int i;
        boolean isUploadingMessageIdDialog;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        AndroidUtilities.cancelRunOnUIThread(this.updateSlowModeRunnable);
        this.updateSlowModeRunnable = null;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        int i2 = NUM;
        if (tLRPC$ChatFull != null && tLRPC$ChatFull.slowmode_seconds != 0 && tLRPC$ChatFull.slowmode_next_send_date <= currentTime && ((isUploadingMessageIdDialog = SendMessagesHelper.getInstance(this.currentAccount).isUploadingMessageIdDialog(this.dialog_id)) || SendMessagesHelper.getInstance(this.currentAccount).isSendingMessageIdDialog(this.dialog_id))) {
            if (!ChatObject.hasAdminRights(this.accountInstance.getMessagesController().getChat(Long.valueOf(this.info.id)))) {
                i = this.info.slowmode_seconds;
                if (isUploadingMessageIdDialog) {
                    i2 = Integer.MAX_VALUE;
                }
                this.slowModeTimer = i2;
            }
            i = 0;
        } else {
            int i3 = this.slowModeTimer;
            if (i3 >= NUM) {
                if (this.info != null) {
                    this.accountInstance.getMessagesController().loadFullChat(this.info.id, 0, true);
                }
                i = 0;
            } else {
                i = i3 - currentTime;
            }
        }
        if (this.slowModeTimer != 0 && i > 0) {
            this.slowModeButton.setText(AndroidUtilities.formatDurationNoHours(Math.max(1, i), false));
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                SimpleTextView simpleTextView = this.slowModeButton;
                chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, false, simpleTextView.getText());
            }
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda41
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.this.updateSlowModeText();
                }
            };
            this.updateSlowModeRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 100L);
        } else {
            this.slowModeTimer = 0;
        }
        if (!isInScheduleMode()) {
            checkSendButton(true);
        }
    }

    public void addTopView(View view, View view2, int i) {
        if (view == null) {
            return;
        }
        this.topLineView = view2;
        view2.setVisibility(8);
        this.topLineView.setAlpha(0.0f);
        addView(this.topLineView, LayoutHelper.createFrame(-1, 1.0f, 51, 0.0f, i + 1, 0.0f, 0.0f));
        this.topView = view;
        view.setVisibility(8);
        this.topViewEnterProgress = 0.0f;
        float f = i;
        this.topView.setTranslationY(f);
        addView(this.topView, 0, LayoutHelper.createFrame(-1, f, 51, 0.0f, 2.0f, 0.0f, 0.0f));
        this.needShowTopView = false;
    }

    public void setForceShowSendButton(boolean z, boolean z2) {
        this.forceShowSendButton = z;
        checkSendButton(z2);
    }

    public void setAllowStickersAndGifs(boolean z, boolean z2, boolean z3) {
        setAllowStickersAndGifs(z, z2, z3, false);
    }

    public void setAllowStickersAndGifs(boolean z, boolean z2, boolean z3, boolean z4) {
        if ((this.allowStickers != z2 || this.allowGifs != z3) && this.emojiView != null) {
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
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.setAllow(z2, z3, true);
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

    public /* synthetic */ void lambda$new$37(ValueAnimator valueAnimator) {
        MentionsContainerView mentionsContainerView;
        if (this.topView != null) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.topViewEnterProgress = floatValue;
            View view = this.topView;
            float f = 1.0f - floatValue;
            view.setTranslationY(this.animatedTop + (view.getLayoutParams().height * f));
            this.topLineView.setAlpha(floatValue);
            this.topLineView.setTranslationY(this.animatedTop);
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || (mentionsContainerView = chatActivity.mentionContainer) == null) {
                return;
            }
            mentionsContainerView.setTranslationY(f * this.topView.getLayoutParams().height);
        }
    }

    public void showTopView(boolean z, boolean z2) {
        showTopView(z, z2, false);
    }

    private void showTopView(boolean z, boolean z2, boolean z3) {
        if (this.topView == null || this.topViewShowed || getVisibility() != 0) {
            if (this.recordedAudioPanel.getVisibility() == 0) {
                return;
            }
            if (this.forceShowSendButton && !z2) {
                return;
            }
            openKeyboard();
            return;
        }
        boolean z4 = this.recordedAudioPanel.getVisibility() != 0 && (!this.forceShowSendButton || z2) && (this.botReplyMarkup == null || this.editingMessageObject != null);
        if (!z3 && z && z4 && !this.keyboardVisible && !isPopupShowing()) {
            openKeyboard();
            Runnable runnable = this.showTopViewRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda42
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.this.lambda$showTopView$38();
                }
            };
            this.showTopViewRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 200L);
            return;
        }
        this.needShowTopView = true;
        this.topViewShowed = true;
        if (!this.allowShowTopView) {
            return;
        }
        this.topView.setVisibility(0);
        this.topLineView.setVisibility(0);
        ValueAnimator valueAnimator = this.currentTopViewAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.currentTopViewAnimation = null;
        }
        resizeForTopView(true);
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.topViewEnterProgress, 1.0f);
            this.currentTopViewAnimation = ofFloat;
            ofFloat.addUpdateListener(this.topViewUpdateListener);
            this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.34
                {
                    ChatActivityEnterView.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ValueAnimator valueAnimator2 = ChatActivityEnterView.this.currentTopViewAnimation;
                    if (valueAnimator2 != null && valueAnimator2.equals(animator)) {
                        ChatActivityEnterView.this.currentTopViewAnimation = null;
                    }
                    NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                    if (ChatActivityEnterView.this.parentFragment == null || ChatActivityEnterView.this.parentFragment.mentionContainer == null) {
                        return;
                    }
                    ChatActivityEnterView.this.parentFragment.mentionContainer.setTranslationY(0.0f);
                }
            });
            this.currentTopViewAnimation.setDuration(270L);
            this.currentTopViewAnimation.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
            this.currentTopViewAnimation.start();
            this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
        } else {
            this.topViewEnterProgress = 1.0f;
            this.topView.setTranslationY(0.0f);
            this.topLineView.setAlpha(1.0f);
        }
        if (!z4) {
            return;
        }
        this.messageEditText.requestFocus();
        openKeyboard();
    }

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
                this.doneButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.doneButtonImage, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.doneButtonProgress, View.ALPHA, 1.0f));
            } else {
                this.doneButtonImage.setVisibility(0);
                this.doneButtonContainer.setEnabled(true);
                this.doneButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.doneButtonProgress, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.doneButtonImage, View.ALPHA, 1.0f));
            }
            this.doneButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.35
                {
                    ChatActivityEnterView.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (ChatActivityEnterView.this.doneButtonAnimation == null || !ChatActivityEnterView.this.doneButtonAnimation.equals(animator)) {
                        return;
                    }
                    if (!z) {
                        ChatActivityEnterView.this.doneButtonProgress.setVisibility(4);
                    } else {
                        ChatActivityEnterView.this.doneButtonImage.setVisibility(4);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    if (ChatActivityEnterView.this.doneButtonAnimation == null || !ChatActivityEnterView.this.doneButtonAnimation.equals(animator)) {
                        return;
                    }
                    ChatActivityEnterView.this.doneButtonAnimation = null;
                }
            });
            this.doneButtonAnimation.setDuration(150L);
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
        if (this.topView == null || !this.topViewShowed) {
            return;
        }
        Runnable runnable = this.showTopViewRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.topViewShowed = false;
        this.needShowTopView = false;
        if (!this.allowShowTopView) {
            return;
        }
        ValueAnimator valueAnimator = this.currentTopViewAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.currentTopViewAnimation = null;
        }
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.topViewEnterProgress, 0.0f);
            this.currentTopViewAnimation = ofFloat;
            ofFloat.addUpdateListener(this.topViewUpdateListener);
            this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.36
                {
                    ChatActivityEnterView.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ValueAnimator valueAnimator2 = ChatActivityEnterView.this.currentTopViewAnimation;
                    if (valueAnimator2 != null && valueAnimator2.equals(animator)) {
                        ChatActivityEnterView.this.topView.setVisibility(8);
                        ChatActivityEnterView.this.topLineView.setVisibility(8);
                        ChatActivityEnterView.this.resizeForTopView(false);
                        ChatActivityEnterView.this.currentTopViewAnimation = null;
                    }
                    if (ChatActivityEnterView.this.parentFragment == null || ChatActivityEnterView.this.parentFragment.mentionContainer == null) {
                        return;
                    }
                    ChatActivityEnterView.this.parentFragment.mentionContainer.setTranslationY(0.0f);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    ValueAnimator valueAnimator2 = ChatActivityEnterView.this.currentTopViewAnimation;
                    if (valueAnimator2 == null || !valueAnimator2.equals(animator)) {
                        return;
                    }
                    ChatActivityEnterView.this.currentTopViewAnimation = null;
                }
            });
            this.currentTopViewAnimation.setDuration(250L);
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
        view.setTranslationY(view.getLayoutParams().height);
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
        if (!z || !this.messageEditText.hasFocus() || !hasBotWebView() || !botCommandsMenuIsShowing()) {
            return;
        }
        this.botWebViewMenuContainer.dismiss();
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
        if (this.topView != null) {
            if (height < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
                if (!this.allowShowTopView) {
                    return;
                }
                this.allowShowTopView = false;
                if (!this.needShowTopView) {
                    return;
                }
                this.topView.setVisibility(8);
                this.topLineView.setVisibility(8);
                this.topLineView.setAlpha(0.0f);
                resizeForTopView(false);
                this.topViewEnterProgress = 0.0f;
                View view = this.topView;
                view.setTranslationY(view.getLayoutParams().height);
            } else if (this.allowShowTopView) {
            } else {
                this.allowShowTopView = true;
                if (!this.needShowTopView) {
                    return;
                }
                this.topView.setVisibility(0);
                this.topLineView.setVisibility(0);
                this.topLineView.setAlpha(1.0f);
                resizeForTopView(true);
                this.topViewEnterProgress = 1.0f;
                this.topView.setTranslationY(0.0f);
            }
        }
    }

    public void resizeForTopView(boolean z) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textFieldContainer.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(2.0f) + (z ? this.topView.getLayoutParams().height : 0);
        this.textFieldContainer.setLayoutParams(layoutParams);
        setMinimumHeight(AndroidUtilities.dp(51.0f) + (z ? this.topView.getLayoutParams().height : 0));
        if (this.stickersExpanded) {
            if (this.searchingType == 0) {
                setStickersExpanded(false, true, false);
            } else {
                checkStickresExpandHeight();
            }
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
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.onDestroy();
        }
        Runnable runnable = this.updateSlowModeRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateSlowModeRunnable = null;
        }
        PowerManager.WakeLock wakeLock = this.wakeLock;
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
        SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
        if (senderSelectPopup != null) {
            senderSelectPopup.setPauseNotifications(false);
            this.senderSelectPopupWindow.dismiss();
        }
    }

    public void checkChannelRights() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity == null) {
            return;
        }
        TLRPC$Chat currentChat = chatActivity.getCurrentChat();
        TLRPC$UserFull currentUserInfo = this.parentFragment.getCurrentUserInfo();
        float f = 1.0f;
        if (currentChat == null) {
            if (currentUserInfo == null) {
                return;
            }
            FrameLayout frameLayout = this.audioVideoButtonContainer;
            if (currentUserInfo.voice_messages_forbidden) {
                f = 0.5f;
            }
            frameLayout.setAlpha(f);
            return;
        }
        FrameLayout frameLayout2 = this.audioVideoButtonContainer;
        if (!ChatObject.canSendMedia(currentChat)) {
            f = 0.5f;
        }
        frameLayout2.setAlpha(f);
        EmojiView emojiView = this.emojiView;
        if (emojiView == null) {
            return;
        }
        emojiView.setStickersBanned(!ChatObject.canSendStickers(currentChat), currentChat.id);
    }

    public void onBeginHide() {
        Runnable runnable = this.focusRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.focusRunnable = null;
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
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
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                ChatActivityEnterView.this.lambda$onPause$39();
            }
        };
        this.hideKeyboardRunnable = runnable;
        AndroidUtilities.runOnUIThread(runnable, 500L);
    }

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
            if (!this.showKeyboardOnResume || !this.parentFragment.isLastFragment()) {
                return;
            }
            this.showKeyboardOnResume = false;
            if (this.searchingType == 0) {
                this.messageEditText.requestFocus();
            }
            AndroidUtilities.showKeyboard(this.messageEditText);
            if (AndroidUtilities.usingHardwareInput || this.keyboardVisible || AndroidUtilities.isInMultiwindow) {
                return;
            }
            this.waitingForKeyboardOpen = true;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
            AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        this.messageEditText.setEnabled(i == 0);
    }

    public void setDialogId(long j, int i) {
        this.dialog_id = j;
        int i2 = this.currentAccount;
        if (i2 != i) {
            NotificationCenter.getInstance(i2).onAnimationFinish(this.notificationsIndex);
            NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
            int i3 = NotificationCenter.recordStarted;
            notificationCenter.removeObserver(this, i3);
            NotificationCenter notificationCenter2 = NotificationCenter.getInstance(this.currentAccount);
            int i4 = NotificationCenter.recordStartError;
            notificationCenter2.removeObserver(this, i4);
            NotificationCenter notificationCenter3 = NotificationCenter.getInstance(this.currentAccount);
            int i5 = NotificationCenter.recordStopped;
            notificationCenter3.removeObserver(this, i5);
            NotificationCenter notificationCenter4 = NotificationCenter.getInstance(this.currentAccount);
            int i6 = NotificationCenter.recordProgressChanged;
            notificationCenter4.removeObserver(this, i6);
            NotificationCenter notificationCenter5 = NotificationCenter.getInstance(this.currentAccount);
            int i7 = NotificationCenter.closeChats;
            notificationCenter5.removeObserver(this, i7);
            NotificationCenter notificationCenter6 = NotificationCenter.getInstance(this.currentAccount);
            int i8 = NotificationCenter.audioDidSent;
            notificationCenter6.removeObserver(this, i8);
            NotificationCenter notificationCenter7 = NotificationCenter.getInstance(this.currentAccount);
            int i9 = NotificationCenter.audioRouteChanged;
            notificationCenter7.removeObserver(this, i9);
            NotificationCenter notificationCenter8 = NotificationCenter.getInstance(this.currentAccount);
            int i10 = NotificationCenter.messagePlayingDidReset;
            notificationCenter8.removeObserver(this, i10);
            NotificationCenter notificationCenter9 = NotificationCenter.getInstance(this.currentAccount);
            int i11 = NotificationCenter.messagePlayingProgressDidChanged;
            notificationCenter9.removeObserver(this, i11);
            NotificationCenter notificationCenter10 = NotificationCenter.getInstance(this.currentAccount);
            int i12 = NotificationCenter.featuredStickersDidLoad;
            notificationCenter10.removeObserver(this, i12);
            NotificationCenter notificationCenter11 = NotificationCenter.getInstance(this.currentAccount);
            int i13 = NotificationCenter.messageReceivedByServer;
            notificationCenter11.removeObserver(this, i13);
            NotificationCenter notificationCenter12 = NotificationCenter.getInstance(this.currentAccount);
            int i14 = NotificationCenter.sendingMessagesChanged;
            notificationCenter12.removeObserver(this, i14);
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
        boolean z = false;
        updateScheduleButton(false);
        checkRoundVideo();
        updateFieldHint(false);
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && chatActivity.getFragmentBeginToShow()) {
            z = true;
        }
        updateSendAsButton(z);
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.setChatInfo(tLRPC$ChatFull);
        }
        setSlowModeTimer(tLRPC$ChatFull.slowmode_next_send_date);
    }

    public void checkRoundVideo() {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        if (this.hasRecordVideo) {
            return;
        }
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
                CameraController.getInstance().initCamera(null);
            }
            setRecordVideoButtonVisible(MessagesController.getGlobalMainSettings().getBoolean(z ? "currentModeVideoChannel" : "currentModeVideo", z), false);
            return;
        }
        setRecordVideoButtonVisible(false, false);
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
        int i;
        TLRPC$ReplyMarkup tLRPC$ReplyMarkup2;
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 != null && (tLRPC$ReplyMarkup2 = messageObject2.messageOwner.reply_markup) != null && !TextUtils.isEmpty(tLRPC$ReplyMarkup2.placeholder)) {
            this.messageEditText.setHintText(this.replyingMessageObject.messageOwner.reply_markup.placeholder, z);
            return;
        }
        String str = "TypeMessage";
        if (this.editingMessageObject != null) {
            EditTextCaption editTextCaption = this.messageEditText;
            if (this.editingCaption) {
                i = R.string.Caption;
                str = "Caption";
            } else {
                i = R.string.TypeMessage;
            }
            editTextCaption.setHintText(LocaleController.getString(str, i));
        } else if (this.botKeyboardViewVisible && (messageObject = this.botButtonsMessageObject) != null && (tLRPC$ReplyMarkup = messageObject.messageOwner.reply_markup) != null && !TextUtils.isEmpty(tLRPC$ReplyMarkup.placeholder)) {
            this.messageEditText.setHintText(this.botButtonsMessageObject.messageOwner.reply_markup.placeholder, z);
        } else {
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
                this.messageEditText.setHintText(LocaleController.getString("SendAnonymously", R.string.SendAnonymously));
                return;
            }
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && chatActivity.isThreadChat()) {
                ChatActivity chatActivity2 = this.parentFragment;
                if (!chatActivity2.isTopic) {
                    if (chatActivity2.isReplyChatComment()) {
                        this.messageEditText.setHintText(LocaleController.getString("Comment", R.string.Comment));
                        return;
                    } else {
                        this.messageEditText.setHintText(LocaleController.getString("Reply", R.string.Reply));
                        return;
                    }
                }
            }
            if (z2) {
                if (this.silent) {
                    this.messageEditText.setHintText(LocaleController.getString("ChannelSilentBroadcast", R.string.ChannelSilentBroadcast), z);
                    return;
                } else {
                    this.messageEditText.setHintText(LocaleController.getString("ChannelBroadcast", R.string.ChannelBroadcast), z);
                    return;
                }
            }
            this.messageEditText.setHintText(LocaleController.getString(str, R.string.TypeMessage));
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
                animatorSet3.playTogether(ObjectAnimator.ofFloat(this.emojiButton, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.emojiButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, 0.0f));
                BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
                if (botCommandsMenuView != null) {
                    botCommandsMenuView.setAlpha(0.0f);
                    this.botCommandsMenuButton.setScaleY(0.0f);
                    this.botCommandsMenuButton.setScaleX(0.0f);
                    this.recordPannelAnimation.playTogether(ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, 1.0f));
                }
                this.recordPannelAnimation.setDuration(150L);
                this.recordPannelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.37
                    {
                        ChatActivityEnterView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                    }
                });
            } else {
                this.recordDeleteImageView.playAnimation();
                AnimatorSet animatorSet4 = new AnimatorSet();
                if (isInVideoMode()) {
                    animatorSet4.playTogether(ObjectAnimator.ofFloat(this.videoTimelineView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.videoTimelineView, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, 0.0f));
                } else {
                    this.messageEditText.setAlpha(1.0f);
                    this.messageEditText.setTranslationX(0.0f);
                    animatorSet4.playTogether(ObjectAnimator.ofFloat(this.recordedAudioSeekBar, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioPlayButton, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioBackground, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioSeekBar, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.recordedAudioPlayButton, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.recordedAudioBackground, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)));
                }
                animatorSet4.setDuration(200L);
                ImageView imageView = this.attachButton;
                if (imageView != null) {
                    imageView.setAlpha(0.0f);
                    this.attachButton.setScaleX(0.0f);
                    this.attachButton.setScaleY(0.0f);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, 1.0f));
                    animatorSet.setDuration(150L);
                } else {
                    animatorSet = null;
                }
                this.emojiButton.setAlpha(0.0f);
                this.emojiButton.setScaleX(0.0f);
                this.emojiButton.setScaleY(0.0f);
                AnimatorSet animatorSet5 = new AnimatorSet();
                animatorSet5.playTogether(ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.emojiButton, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.emojiButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton, View.SCALE_Y, 1.0f));
                BotCommandsMenuView botCommandsMenuView2 = this.botCommandsMenuButton;
                if (botCommandsMenuView2 != null) {
                    botCommandsMenuView2.setAlpha(0.0f);
                    this.botCommandsMenuButton.setScaleY(0.0f);
                    this.botCommandsMenuButton.setScaleX(0.0f);
                    animatorSet5.playTogether(ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, 1.0f));
                }
                animatorSet5.setDuration(150L);
                animatorSet5.setStartDelay(600L);
                AnimatorSet animatorSet6 = new AnimatorSet();
                this.recordPannelAnimation = animatorSet6;
                if (animatorSet != null) {
                    animatorSet6.playTogether(animatorSet4, animatorSet, animatorSet5);
                } else {
                    animatorSet6.playTogether(animatorSet4, animatorSet5);
                }
                this.recordPannelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.38
                    {
                        ChatActivityEnterView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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

    public void sendMessage() {
        if (isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new ChatActivityEnterView$$ExternalSyntheticLambda54(this), this.resourcesProvider);
        } else {
            sendMessageInternal(true, 0);
        }
    }

    public void sendMessageInternal(final boolean z, final int i) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        TLRPC$Chat currentChat;
        EmojiView emojiView;
        if (this.slowModeTimer == Integer.MAX_VALUE && !isInScheduleMode()) {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
            if (chatActivityEnterViewDelegate2 == null) {
                return;
            }
            chatActivityEnterViewDelegate2.scrollToSendingMessage();
            return;
        }
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC$Chat currentChat2 = chatActivity.getCurrentChat();
            if (this.parentFragment.getCurrentUser() != null || ((ChatObject.isChannel(currentChat2) && currentChat2.megagroup) || !ChatObject.isChannel(currentChat2))) {
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                edit.putBoolean("silent_" + this.dialog_id, !z).commit();
            }
        }
        if (this.stickersExpanded) {
            setStickersExpanded(false, true, false);
            if (this.searchingType != 0 && (emojiView = this.emojiView) != null) {
                emojiView.closeSearch(false);
                this.emojiView.hideSearchKeyboard();
            }
        }
        if (this.videoToSendMessageObject != null) {
            this.delegate.needStartRecordVideo(4, z, i);
            hideRecordedAudioPanel(true);
            checkSendButton(true);
        } else if (this.audioToSend != null) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null && playingMessageObject == this.audioToSendMessageObject) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.audioToSend, null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, getThreadMessage(), null, null, null, null, z, i, 0, null, null, false);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
            if (chatActivityEnterViewDelegate3 != null) {
                chatActivityEnterViewDelegate3.onMessageSend(null, z, i);
            }
            hideRecordedAudioPanel(true);
            checkSendButton(true);
        } else {
            final Editable text = this.messageEditText.getText();
            ChatActivity chatActivity2 = this.parentFragment;
            if (chatActivity2 != null && (currentChat = chatActivity2.getCurrentChat()) != null && currentChat.slowmode_enabled && !ChatObject.hasAdminRights(currentChat)) {
                if (text.length() > this.accountInstance.getMessagesController().maxMessageLength) {
                    AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendErrorTooLong", R.string.SlowmodeSendErrorTooLong), this.resourcesProvider);
                    return;
                } else if (this.forceShowSendButton && text.length() > 0) {
                    AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendError", R.string.SlowmodeSendError), this.resourcesProvider);
                    return;
                }
            }
            if (checkPremiumAnimatedEmoji(this.currentAccount, this.dialog_id, this.parentFragment, null, text)) {
                return;
            }
            if (processSendingText(text, z, i)) {
                if (this.delegate.hasForwardingMessages() || ((i != 0 && !isInScheduleMode()) || isInScheduleMode())) {
                    this.messageEditText.setText("");
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate4 = this.delegate;
                    if (chatActivityEnterViewDelegate4 != null) {
                        chatActivityEnterViewDelegate4.onMessageSend(text, z, i);
                    }
                } else {
                    this.messageTransitionIsRunning = false;
                    Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda49
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatActivityEnterView.this.lambda$sendMessageInternal$40(text, z, i);
                        }
                    };
                    this.moveToSendStateRunnable = runnable;
                    AndroidUtilities.runOnUIThread(runnable, 200L);
                }
                this.lastTypingTimeSend = 0L;
            } else if (!this.forceShowSendButton || (chatActivityEnterViewDelegate = this.delegate) == null) {
            } else {
                chatActivityEnterViewDelegate.onMessageSend(null, z, i);
            }
        }
    }

    public /* synthetic */ void lambda$sendMessageInternal$40(CharSequence charSequence, boolean z, int i) {
        this.moveToSendStateRunnable = null;
        hideTopView(true);
        this.messageEditText.setText("");
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend(charSequence, z, i);
        }
    }

    public static boolean checkPremiumAnimatedEmoji(int i, long j, final BaseFragment baseFragment, FrameLayout frameLayout, CharSequence charSequence) {
        AnimatedEmojiSpan[] animatedEmojiSpanArr;
        if (charSequence != null && baseFragment != null) {
            if (frameLayout == null) {
                frameLayout = baseFragment.getLayoutContainer();
            }
            if (!UserConfig.getInstance(i).isPremium() && UserConfig.getInstance(i).getClientUserId() != j && (charSequence instanceof Spanned) && (animatedEmojiSpanArr = (AnimatedEmojiSpan[]) ((Spanned) charSequence).getSpans(0, charSequence.length(), AnimatedEmojiSpan.class)) != null) {
                for (int i2 = 0; i2 < animatedEmojiSpanArr.length; i2++) {
                    if (animatedEmojiSpanArr[i2] != null) {
                        TLRPC$Document tLRPC$Document = animatedEmojiSpanArr[i2].document;
                        if (tLRPC$Document == null) {
                            tLRPC$Document = AnimatedEmojiDrawable.findDocument(i, animatedEmojiSpanArr[i2].getDocumentId());
                        }
                        if (tLRPC$Document != null && !MessageObject.isFreeEmoji(tLRPC$Document)) {
                            BulletinFactory.of(frameLayout, baseFragment.getResourceProvider()).createEmojiBulletin(tLRPC$Document, AndroidUtilities.replaceTags(LocaleController.getString("UnlockPremiumEmojiHint", R.string.UnlockPremiumEmojiHint)), LocaleController.getString("PremiumMore", R.string.PremiumMore), new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda34
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ChatActivityEnterView.lambda$checkPremiumAnimatedEmoji$41(BaseFragment.this);
                                }
                            }).show();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static /* synthetic */ void lambda$checkPremiumAnimatedEmoji$41(BaseFragment baseFragment) {
        if (baseFragment != null) {
            new PremiumFeatureBottomSheet(baseFragment, 11, false).show();
        } else if (!(baseFragment.getContext() instanceof LaunchActivity)) {
        } else {
            ((LaunchActivity) baseFragment.getContext()).lambda$runLinkRequest$66(new PremiumPreviewFragment(null));
        }
    }

    public void doneEditingMessage() {
        if (this.editingMessageObject == null) {
            return;
        }
        if (this.currentLimit - this.codePointCount < 0) {
            AndroidUtilities.shakeView(this.captionLimitView);
            Vibrator vibrator = (Vibrator) this.captionLimitView.getContext().getSystemService("vibrator");
            if (vibrator == null) {
                return;
            }
            vibrator.vibrate(200L);
            return;
        }
        if (this.searchingType != 0) {
            setSearchingTypeInternal(0, true);
            this.emojiView.closeSearch(false);
            if (this.stickersExpanded) {
                setStickersExpanded(false, true, false);
                this.waitingForKeyboardOpenAfterAnimation = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda38
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatActivityEnterView.this.lambda$doneEditingMessage$42();
                    }
                }, 200L);
            }
        }
        CharSequence text = this.messageEditText.getText();
        MessageObject messageObject = this.editingMessageObject;
        if (messageObject == null || messageObject.type != 19) {
            text = AndroidUtilities.getTrimmedString(text);
        }
        CharSequence[] charSequenceArr = {text};
        ArrayList<TLRPC$MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities());
        if (!TextUtils.equals(charSequenceArr[0], this.editingMessageObject.messageText) || ((entities != null && !entities.isEmpty()) || (((entities == null || entities.isEmpty()) && !this.editingMessageObject.messageOwner.entities.isEmpty()) || (this.editingMessageObject.messageOwner.media instanceof TLRPC$TL_messageMediaWebPage)))) {
            MessageObject messageObject2 = this.editingMessageObject;
            messageObject2.editingMessage = charSequenceArr[0];
            messageObject2.editingMessageEntities = entities;
            messageObject2.editingMessageSearchWebPage = this.messageWebPageSearch;
            SendMessagesHelper.getInstance(this.currentAccount).editMessage(this.editingMessageObject, null, null, null, null, null, false, null);
        }
        setEditingMessageObject(null, false);
    }

    public /* synthetic */ void lambda$doneEditingMessage$42() {
        this.waitingForKeyboardOpenAfterAnimation = false;
        openKeyboardInternal();
    }

    public boolean processSendingText(CharSequence charSequence, boolean z, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        ChatActivity chatActivity;
        int[] iArr = new int[1];
        CharSequence charSequence2 = charSequence;
        Emoji.parseEmojis(charSequence2, iArr);
        boolean z2 = iArr[0] > 0;
        if (!z2) {
            charSequence2 = AndroidUtilities.getTrimmedString(charSequence);
        }
        boolean supportsSendingNewEntities = supportsSendingNewEntities();
        int i6 = this.accountInstance.getMessagesController().maxMessageLength;
        if (charSequence2.length() != 0) {
            if (this.delegate != null && (chatActivity = this.parentFragment) != null) {
                if ((i != 0) == chatActivity.isInScheduleMode()) {
                    this.delegate.prepareMessageSending();
                }
            }
            int i7 = 0;
            while (true) {
                int i8 = i7 + i6;
                if (charSequence2.length() > i8) {
                    int i9 = i8 - 1;
                    i3 = -1;
                    i4 = -1;
                    i5 = -1;
                    for (int i10 = 0; i9 > i7 && i10 < 300; i10++) {
                        char charAt = charSequence2.charAt(i9);
                        char charAt2 = i9 > 0 ? charSequence2.charAt(i9 - 1) : ' ';
                        if (charAt == '\n' && charAt2 == '\n') {
                            i2 = i9;
                            break;
                        }
                        if (charAt == '\n') {
                            i5 = i9;
                        } else if (i3 < 0 && Character.isWhitespace(charAt) && charAt2 == '.') {
                            i3 = i9;
                        } else if (i4 < 0 && Character.isWhitespace(charAt)) {
                            i4 = i9;
                        }
                        i9--;
                    }
                    i2 = -1;
                } else {
                    i2 = -1;
                    i3 = -1;
                    i4 = -1;
                    i5 = -1;
                }
                int min = Math.min(i8, charSequence2.length());
                if (i2 > 0) {
                    min = i2;
                } else if (i5 > 0) {
                    min = i5;
                } else if (i3 > 0) {
                    min = i3;
                } else if (i4 > 0) {
                    min = i4;
                }
                CharSequence subSequence = charSequence2.subSequence(i7, min);
                if (!z2) {
                    subSequence = AndroidUtilities.getTrimmedString(subSequence);
                }
                CharSequence[] charSequenceArr = {subSequence};
                ArrayList<TLRPC$MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities);
                MessageObject.SendAnimationData sendAnimationData = null;
                if (!this.delegate.hasForwardingMessages()) {
                    sendAnimationData = new MessageObject.SendAnimationData();
                    float dp = AndroidUtilities.dp(22.0f);
                    sendAnimationData.height = dp;
                    sendAnimationData.width = dp;
                    this.messageEditText.getLocationInWindow(this.location);
                    sendAnimationData.x = this.location[0] + AndroidUtilities.dp(11.0f);
                    sendAnimationData.y = this.location[1] + AndroidUtilities.dp(19.0f);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(charSequenceArr[0].toString(), this.dialog_id, this.replyingMessageObject, getThreadMessage(), this.messageWebPage, this.messageWebPageSearch, entities, null, null, z, i, sendAnimationData, SendMessagesHelper.checkUpdateStickersOrder(charSequence2));
                i7 = min + 1;
                if (min == charSequence2.length()) {
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private boolean supportsSendingNewEntities() {
        ChatActivity chatActivity = this.parentFragment;
        TLRPC$EncryptedChat currentEncryptedChat = chatActivity != null ? chatActivity.getCurrentEncryptedChat() : null;
        return currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(currentEncryptedChat.layer) >= 101;
    }

    public void checkSendButton(boolean z) {
        int themedColor;
        int i;
        if (this.editingMessageObject != null || this.recordingAudioVideo) {
            return;
        }
        boolean z2 = this.isPaused ? false : z;
        CharSequence trimmedString = AndroidUtilities.getTrimmedString(this.messageEditText.getText());
        int i2 = this.slowModeTimer;
        float f = 1.0f;
        if (i2 > 0 && i2 != Integer.MAX_VALUE && !isInScheduleMode()) {
            if (this.slowModeButton.getVisibility() == 0) {
                return;
            }
            if (z2) {
                if (this.runningAnimationType == 5) {
                    return;
                }
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
                    arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 0.0f));
                    arrayList.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, 0.0f));
                    this.scheduleButtonHidden = false;
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                    boolean z3 = chatActivityEnterViewDelegate != null && chatActivityEnterViewDelegate.hasScheduledMessages();
                    ImageView imageView = this.scheduledButton;
                    if (imageView != null) {
                        imageView.setScaleY(1.0f);
                        if (z3) {
                            this.scheduledButton.setVisibility(0);
                            this.scheduledButton.setTag(1);
                            this.scheduledButton.setPivotX(AndroidUtilities.dp(48.0f));
                            ImageView imageView2 = this.scheduledButton;
                            Property property = View.TRANSLATION_X;
                            float[] fArr = new float[1];
                            ImageView imageView3 = this.botButton;
                            fArr[0] = AndroidUtilities.dp((imageView3 == null || imageView3.getVisibility() != 0) ? 48.0f : 96.0f);
                            arrayList.add(ObjectAnimator.ofFloat(imageView2, property, fArr));
                            arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 1.0f));
                            arrayList.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, 1.0f));
                        } else {
                            ImageView imageView4 = this.scheduledButton;
                            ImageView imageView5 = this.botButton;
                            imageView4.setTranslationX(AndroidUtilities.dp((imageView5 == null || imageView5.getVisibility() != 0) ? 48.0f : 96.0f));
                            this.scheduledButton.setAlpha(1.0f);
                            this.scheduledButton.setScaleX(1.0f);
                        }
                    }
                    this.runningAnimation2.playTogether(arrayList);
                    this.runningAnimation2.setDuration(100L);
                    this.runningAnimation2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.39
                        {
                            ChatActivityEnterView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                ChatActivityEnterView.this.runningAnimation2 = null;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
                ArrayList arrayList2 = new ArrayList();
                if (this.audioVideoButtonContainer.getVisibility() == 0) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, 0.1f));
                    arrayList2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, 0.1f));
                    arrayList2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 0.0f));
                }
                if (this.expandStickersButton.getVisibility() == 0) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, 0.1f));
                    arrayList2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, 0.1f));
                    arrayList2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, 0.0f));
                }
                if (this.sendButton.getVisibility() == 0) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, 0.1f));
                    arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, 0.1f));
                    arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, 0.0f));
                }
                if (this.cancelBotButton.getVisibility() == 0) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, 0.1f));
                    arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, 0.1f));
                    arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, 0.0f));
                }
                arrayList2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, 1.0f));
                arrayList2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, 1.0f));
                arrayList2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, 1.0f));
                setSlowModeButtonVisible(true);
                this.runningAnimation.playTogether(arrayList2);
                this.runningAnimation.setDuration(150L);
                this.runningAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.40
                    {
                        ChatActivityEnterView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                            ChatActivityEnterView.this.runningAnimation = null;
                        }
                    }
                });
                this.runningAnimation.start();
                return;
            }
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
            LinearLayout linearLayout = this.attachLayout;
            if (linearLayout != null) {
                linearLayout.setVisibility(8);
                if (this.delegate != null && getVisibility() == 0) {
                    this.delegate.onAttachButtonHidden();
                }
                updateFieldRight(0);
            }
            this.scheduleButtonHidden = false;
            if (this.scheduledButton == null) {
                return;
            }
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
            if (chatActivityEnterViewDelegate2 != null && chatActivityEnterViewDelegate2.hasScheduledMessages()) {
                this.scheduledButton.setVisibility(0);
                this.scheduledButton.setTag(1);
            }
            ImageView imageView6 = this.scheduledButton;
            ImageView imageView7 = this.botButton;
            imageView6.setTranslationX(AndroidUtilities.dp((imageView7 == null || imageView7.getVisibility() != 0) ? 48.0f : 96.0f));
            this.scheduledButton.setAlpha(1.0f);
            this.scheduledButton.setScaleX(1.0f);
            this.scheduledButton.setScaleY(1.0f);
        } else if (trimmedString.length() > 0 || this.forceShowSendButton || this.audioToSend != null || this.videoToSendMessageObject != null || (this.slowModeTimer == Integer.MAX_VALUE && !isInScheduleMode())) {
            final String caption = this.messageEditText.getCaption();
            boolean z4 = caption != null && (this.sendButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
            boolean z5 = caption == null && (this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
            if (this.slowModeTimer == Integer.MAX_VALUE && !isInScheduleMode()) {
                themedColor = getThemedColor("chat_messagePanelIcons");
            } else {
                themedColor = getThemedColor("chat_messagePanelSend");
            }
            Theme.setSelectorDrawableColor(this.sendButton.getBackground(), Color.argb(24, Color.red(themedColor), Color.green(themedColor), Color.blue(themedColor)), true);
            if (this.audioVideoButtonContainer.getVisibility() != 0 && this.slowModeButton.getVisibility() != 0 && !z4 && !z5) {
                return;
            }
            if (z2) {
                if (this.runningAnimationType == 1 && this.messageEditText.getCaption() == null) {
                    return;
                }
                if (this.runningAnimationType == 3 && caption != null) {
                    return;
                }
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
                if (this.attachLayout != null) {
                    this.runningAnimation2 = new AnimatorSet();
                    ArrayList arrayList3 = new ArrayList();
                    arrayList3.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 0.0f));
                    arrayList3.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, 0.0f));
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                    final boolean z6 = chatActivityEnterViewDelegate3 != null && chatActivityEnterViewDelegate3.hasScheduledMessages();
                    this.scheduleButtonHidden = true;
                    ImageView imageView8 = this.scheduledButton;
                    if (imageView8 != null) {
                        imageView8.setScaleY(1.0f);
                        if (z6) {
                            this.scheduledButton.setTag(null);
                            arrayList3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 0.0f));
                            arrayList3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, 0.0f));
                            ImageView imageView9 = this.scheduledButton;
                            Property property2 = View.TRANSLATION_X;
                            float[] fArr2 = new float[1];
                            ImageView imageView10 = this.botButton;
                            fArr2[0] = AndroidUtilities.dp((imageView10 == null || imageView10.getVisibility() == 8) ? 48.0f : 96.0f);
                            arrayList3.add(ObjectAnimator.ofFloat(imageView9, property2, fArr2));
                        } else {
                            this.scheduledButton.setAlpha(0.0f);
                            this.scheduledButton.setScaleX(0.0f);
                            ImageView imageView11 = this.scheduledButton;
                            ImageView imageView12 = this.botButton;
                            imageView11.setTranslationX(AndroidUtilities.dp((imageView12 == null || imageView12.getVisibility() == 8) ? 48.0f : 96.0f));
                        }
                    }
                    this.runningAnimation2.playTogether(arrayList3);
                    this.runningAnimation2.setDuration(100L);
                    this.runningAnimation2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.41
                        {
                            ChatActivityEnterView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                if (z6) {
                                    ChatActivityEnterView.this.scheduledButton.setVisibility(8);
                                }
                                ChatActivityEnterView.this.runningAnimation2 = null;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
                ArrayList arrayList4 = new ArrayList();
                if (this.audioVideoButtonContainer.getVisibility() == 0) {
                    arrayList4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, 0.1f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, 0.1f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 0.0f));
                }
                if (this.expandStickersButton.getVisibility() == 0) {
                    arrayList4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, 0.1f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, 0.1f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, 0.0f));
                }
                if (this.slowModeButton.getVisibility() == 0) {
                    arrayList4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, 0.1f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, 0.1f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, 0.0f));
                }
                if (z4) {
                    arrayList4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, 0.1f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, 0.1f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, 0.0f));
                } else if (z5) {
                    arrayList4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, 0.1f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, 0.1f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, 0.0f));
                }
                if (caption != null) {
                    this.runningAnimationType = 3;
                    arrayList4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, 1.0f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, 1.0f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, 1.0f));
                    this.cancelBotButton.setVisibility(0);
                } else {
                    this.runningAnimationType = 1;
                    arrayList4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, 1.0f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, 1.0f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, 1.0f));
                    this.sendButton.setVisibility(0);
                }
                this.runningAnimation.playTogether(arrayList4);
                this.runningAnimation.setDuration(150L);
                this.runningAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.42
                    {
                        ChatActivityEnterView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                            ChatActivityEnterView.this.runningAnimation = null;
                        }
                    }
                });
                this.runningAnimation.start();
                return;
            }
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
            LinearLayout linearLayout2 = this.attachLayout;
            if (linearLayout2 != null) {
                linearLayout2.setVisibility(i);
                if (this.delegate != null && getVisibility() == 0) {
                    this.delegate.onAttachButtonHidden();
                }
                updateFieldRight(0);
            }
            this.scheduleButtonHidden = true;
            if (this.scheduledButton == null) {
                return;
            }
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate4 = this.delegate;
            if (chatActivityEnterViewDelegate4 != null && chatActivityEnterViewDelegate4.hasScheduledMessages()) {
                this.scheduledButton.setVisibility(8);
                this.scheduledButton.setTag(null);
            }
            this.scheduledButton.setAlpha(0.0f);
            this.scheduledButton.setScaleX(0.0f);
            this.scheduledButton.setScaleY(1.0f);
            ImageView imageView13 = this.scheduledButton;
            ImageView imageView14 = this.botButton;
            imageView13.setTranslationX(AndroidUtilities.dp((imageView14 == null || imageView14.getVisibility() == 8) ? 48.0f : 96.0f));
        } else if (this.emojiView != null && this.emojiViewVisible && ((this.stickersTabOpen || (this.emojiTabOpen && this.searchingType == 2)) && !AndroidUtilities.isInMultiwindow)) {
            if (z2) {
                if (this.runningAnimationType == 4) {
                    return;
                }
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
                    arrayList5.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 1.0f));
                    arrayList5.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, 1.0f));
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate5 = this.delegate;
                    boolean z7 = chatActivityEnterViewDelegate5 != null && chatActivityEnterViewDelegate5.hasScheduledMessages();
                    this.scheduleButtonHidden = false;
                    ImageView imageView15 = this.scheduledButton;
                    if (imageView15 != null) {
                        imageView15.setScaleY(1.0f);
                        if (z7) {
                            this.scheduledButton.setVisibility(0);
                            this.scheduledButton.setTag(1);
                            this.scheduledButton.setPivotX(AndroidUtilities.dp(48.0f));
                            arrayList5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 1.0f));
                            arrayList5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, 1.0f));
                            arrayList5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, 0.0f));
                        } else {
                            this.scheduledButton.setAlpha(1.0f);
                            this.scheduledButton.setScaleX(1.0f);
                            this.scheduledButton.setTranslationX(0.0f);
                        }
                    }
                    this.runningAnimation2.playTogether(arrayList5);
                    this.runningAnimation2.setDuration(100L);
                    this.runningAnimation2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.43
                        {
                            ChatActivityEnterView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                ChatActivityEnterView.this.runningAnimation2 = null;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
                ArrayList arrayList6 = new ArrayList();
                arrayList6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, 1.0f));
                arrayList6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, 1.0f));
                arrayList6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, 1.0f));
                if (this.cancelBotButton.getVisibility() == 0) {
                    arrayList6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, 0.1f));
                    arrayList6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, 0.1f));
                    arrayList6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, 0.0f));
                } else if (this.audioVideoButtonContainer.getVisibility() == 0) {
                    arrayList6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, 0.1f));
                    arrayList6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, 0.1f));
                    arrayList6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 0.0f));
                } else if (this.slowModeButton.getVisibility() == 0) {
                    arrayList6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, 0.1f));
                    arrayList6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, 0.1f));
                    arrayList6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, 0.0f));
                } else {
                    arrayList6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, 0.1f));
                    arrayList6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, 0.1f));
                    arrayList6.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, 0.0f));
                }
                this.runningAnimation.playTogether(arrayList6);
                this.runningAnimation.setDuration(250L);
                this.runningAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.44
                    {
                        ChatActivityEnterView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                            ChatActivityEnterView.this.runningAnimation = null;
                        }
                    }
                });
                this.runningAnimation.start();
                return;
            }
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
            if (this.scheduledButton == null) {
                return;
            }
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate6 = this.delegate;
            if (chatActivityEnterViewDelegate6 != null && chatActivityEnterViewDelegate6.hasScheduledMessages()) {
                this.scheduledButton.setVisibility(0);
                this.scheduledButton.setTag(1);
            }
            this.scheduledButton.setAlpha(1.0f);
            this.scheduledButton.setScaleX(1.0f);
            this.scheduledButton.setScaleY(1.0f);
            this.scheduledButton.setTranslationX(0.0f);
        } else if (this.sendButton.getVisibility() != 0 && this.cancelBotButton.getVisibility() != 0 && this.expandStickersButton.getVisibility() != 0 && this.slowModeButton.getVisibility() != 0) {
        } else {
            if (z2) {
                if (this.runningAnimationType == 2) {
                    return;
                }
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
                LinearLayout linearLayout4 = this.attachLayout;
                if (linearLayout4 != null) {
                    if (linearLayout4.getVisibility() != 0) {
                        this.attachLayout.setVisibility(0);
                        this.attachLayout.setAlpha(0.0f);
                        this.attachLayout.setScaleX(0.0f);
                    }
                    this.runningAnimation2 = new AnimatorSet();
                    ArrayList arrayList7 = new ArrayList();
                    arrayList7.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 1.0f));
                    arrayList7.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, 1.0f));
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate7 = this.delegate;
                    boolean z8 = chatActivityEnterViewDelegate7 != null && chatActivityEnterViewDelegate7.hasScheduledMessages();
                    this.scheduleButtonHidden = false;
                    ImageView imageView16 = this.scheduledButton;
                    if (imageView16 != null) {
                        if (z8) {
                            imageView16.setVisibility(0);
                            this.scheduledButton.setTag(1);
                            this.scheduledButton.setPivotX(AndroidUtilities.dp(48.0f));
                            arrayList7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 1.0f));
                            arrayList7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, 1.0f));
                            arrayList7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, 0.0f));
                        } else {
                            imageView16.setAlpha(1.0f);
                            this.scheduledButton.setScaleX(1.0f);
                            this.scheduledButton.setScaleY(1.0f);
                            this.scheduledButton.setTranslationX(0.0f);
                        }
                    }
                    this.runningAnimation2.playTogether(arrayList7);
                    this.runningAnimation2.setDuration(100L);
                    this.runningAnimation2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.45
                        {
                            ChatActivityEnterView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                ChatActivityEnterView.this.runningAnimation2 = null;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
                ArrayList arrayList8 = new ArrayList();
                arrayList8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, 1.0f));
                arrayList8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, 1.0f));
                TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
                TLRPC$UserFull currentUserInfo = this.parentFragment.getCurrentUserInfo();
                if (currentChat == null ? !(currentUserInfo == null || !currentUserInfo.voice_messages_forbidden) : !ChatObject.canSendMedia(currentChat)) {
                    f = 0.5f;
                }
                arrayList8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, f));
                if (this.cancelBotButton.getVisibility() == 0) {
                    arrayList8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, 0.1f));
                    arrayList8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, 0.1f));
                    arrayList8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, 0.0f));
                } else if (this.expandStickersButton.getVisibility() == 0) {
                    arrayList8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, 0.1f));
                    arrayList8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, 0.1f));
                    arrayList8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, 0.0f));
                } else if (this.slowModeButton.getVisibility() == 0) {
                    arrayList8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, 0.1f));
                    arrayList8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, 0.1f));
                    arrayList8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, 0.0f));
                } else {
                    arrayList8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, 0.1f));
                    arrayList8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, 0.1f));
                    arrayList8.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, 0.0f));
                }
                this.runningAnimation.playTogether(arrayList8);
                this.runningAnimation.setDuration(150L);
                this.runningAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.46
                    {
                        ChatActivityEnterView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                            ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                            ChatActivityEnterView.this.runningAnimation = null;
                            ChatActivityEnterView.this.runningAnimationType = 0;
                            if (ChatActivityEnterView.this.audioVideoButtonContainer == null) {
                                return;
                            }
                            ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimation)) {
                            ChatActivityEnterView.this.runningAnimation = null;
                        }
                    }
                });
                this.runningAnimation.start();
                return;
            }
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
            if (this.scheduledButton == null) {
                return;
            }
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate8 = this.delegate;
            if (chatActivityEnterViewDelegate8 != null && chatActivityEnterViewDelegate8.hasScheduledMessages()) {
                this.scheduledButton.setVisibility(0);
                this.scheduledButton.setTag(1);
            }
            this.scheduledButton.setAlpha(1.0f);
            this.scheduledButton.setScaleX(1.0f);
            this.scheduledButton.setScaleY(1.0f);
            this.scheduledButton.setTranslationX(0.0f);
        }
    }

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
        if (editTextCaption == null || this.editingMessageObject != null) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) editTextCaption.getLayoutParams();
        int i2 = layoutParams.rightMargin;
        if (i == 1) {
            ImageView imageView7 = this.botButton;
            if (imageView7 != null && imageView7.getVisibility() == 0 && (imageView6 = this.scheduledButton) != null && imageView6.getVisibility() == 0 && (linearLayout2 = this.attachLayout) != null && linearLayout2.getVisibility() == 0) {
                layoutParams.rightMargin = AndroidUtilities.dp(146.0f);
            } else {
                ImageView imageView8 = this.botButton;
                if ((imageView8 != null && imageView8.getVisibility() == 0) || (((imageView4 = this.notifyButton) != null && imageView4.getVisibility() == 0) || ((imageView5 = this.scheduledButton) != null && imageView5.getTag() != null))) {
                    layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                }
            }
        } else if (i == 2) {
            if (i2 != AndroidUtilities.dp(2.0f)) {
                ImageView imageView9 = this.botButton;
                if (imageView9 != null && imageView9.getVisibility() == 0 && (imageView3 = this.scheduledButton) != null && imageView3.getVisibility() == 0 && (linearLayout = this.attachLayout) != null && linearLayout.getVisibility() == 0) {
                    layoutParams.rightMargin = AndroidUtilities.dp(146.0f);
                } else {
                    ImageView imageView10 = this.botButton;
                    if ((imageView10 != null && imageView10.getVisibility() == 0) || (((imageView = this.notifyButton) != null && imageView.getVisibility() == 0) || ((imageView2 = this.scheduledButton) != null && imageView2.getTag() != null))) {
                        layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                    } else {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    }
                }
            }
        } else {
            ImageView imageView11 = this.scheduledButton;
            if (imageView11 != null && imageView11.getTag() != null) {
                layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
            } else {
                layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
            }
        }
        if (i2 == layoutParams.rightMargin) {
            return;
        }
        this.messageEditText.setLayoutParams(layoutParams);
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

    /* JADX WARN: Removed duplicated region for block: B:273:0x029b A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:274:0x029c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateRecordInterface(final int r25) {
        /*
            Method dump skipped, instructions count: 3164
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.updateRecordInterface(int):void");
    }

    public /* synthetic */ void lambda$updateRecordInterface$43(ValueAnimator valueAnimator) {
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

    @Override // android.view.ViewGroup
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
        if (str == null || getVisibility() != 0) {
            return;
        }
        TLRPC$User tLRPC$User = null;
        if (z) {
            String obj = this.messageEditText.getText().toString();
            if (messageObject != null && DialogObject.isChatDialog(this.dialog_id)) {
                tLRPC$User = this.accountInstance.getMessagesController().getUser(Long.valueOf(messageObject.messageOwner.from_id.user_id));
            }
            if ((this.botCount != 1 || z2) && tLRPC$User != null && tLRPC$User.bot && !str.contains("@")) {
                str2 = String.format(Locale.US, "%s@%s", str, tLRPC$User.username) + " " + obj.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
            } else {
                str2 = str + " " + obj.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
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
            if (this.keyboardVisible || this.currentPopupContentType != -1) {
                return;
            }
            openKeyboard();
        } else if (this.slowModeTimer > 0 && !isInScheduleMode()) {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
            if (chatActivityEnterViewDelegate2 == null) {
                return;
            }
            SimpleTextView simpleTextView = this.slowModeButton;
            chatActivityEnterViewDelegate2.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
        } else {
            if (messageObject != null && DialogObject.isChatDialog(this.dialog_id)) {
                tLRPC$User = this.accountInstance.getMessagesController().getUser(Long.valueOf(messageObject.messageOwner.from_id.user_id));
            }
            if ((this.botCount != 1 || z2) && tLRPC$User != null && tLRPC$User.bot && !str.contains("@")) {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(String.format(Locale.US, "%s@%s", str, tLRPC$User.username), this.dialog_id, this.replyingMessageObject, getThreadMessage(), null, false, null, null, null, true, 0, null, false);
            } else {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(str, this.dialog_id, this.replyingMessageObject, getThreadMessage(), null, false, null, null, null, true, 0, null, false);
            }
        }
    }

    public void setEditingMessageObject(MessageObject messageObject, boolean z) {
        MessageObject messageObject2;
        boolean z2;
        CharSequence charSequence;
        ArrayList<TLRPC$MessageEntity> arrayList;
        AnimatedEmojiSpan animatedEmojiSpan;
        if (this.audioToSend == null && this.videoToSendMessageObject == null && (messageObject2 = this.editingMessageObject) != messageObject) {
            int i = 1;
            boolean z3 = messageObject2 != null;
            this.editingMessageObject = messageObject;
            this.editingCaption = z;
            if (messageObject != null) {
                AnimatorSet animatorSet = this.doneButtonAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.doneButtonAnimation = null;
                }
                this.doneButtonContainer.setVisibility(0);
                this.doneButtonImage.setScaleX(0.1f);
                this.doneButtonImage.setScaleY(0.1f);
                this.doneButtonImage.setAlpha(0.0f);
                this.doneButtonImage.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                if (z) {
                    this.currentLimit = this.accountInstance.getMessagesController().maxCaptionLength;
                    charSequence = this.editingMessageObject.caption;
                } else {
                    this.currentLimit = this.accountInstance.getMessagesController().maxMessageLength;
                    charSequence = this.editingMessageObject.messageText;
                }
                String str = "";
                final String str2 = str;
                if (charSequence != null) {
                    ArrayList<TLRPC$MessageEntity> arrayList2 = this.editingMessageObject.messageOwner.entities;
                    MediaDataController.sortEntities(arrayList2);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
                    Object[] spans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), Object.class);
                    if (spans != null && spans.length > 0) {
                        for (Object obj : spans) {
                            spannableStringBuilder.removeSpan(obj);
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
                                        if (!(tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCode) && !(tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityPre)) {
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
                                                TLRPC$TL_messageEntityCustomEmoji tLRPC$TL_messageEntityCustomEmoji = (TLRPC$TL_messageEntityCustomEmoji) tLRPC$MessageEntity;
                                                if (tLRPC$TL_messageEntityCustomEmoji.document != null) {
                                                    animatedEmojiSpan = new AnimatedEmojiSpan(tLRPC$TL_messageEntityCustomEmoji.document, this.messageEditText.getPaint().getFontMetricsInt());
                                                } else {
                                                    animatedEmojiSpan = new AnimatedEmojiSpan(tLRPC$TL_messageEntityCustomEmoji.document_id, this.messageEditText.getPaint().getFontMetricsInt());
                                                }
                                                int i11 = tLRPC$MessageEntity.offset;
                                                spannableStringBuilder.setSpan(animatedEmojiSpan, i11, tLRPC$MessageEntity.length + i11, 33);
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
                                FileLog.e(e);
                            }
                        }
                    }
                    str2 = Emoji.replaceEmoji(new SpannableStringBuilder(spannableStringBuilder), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, null, true);
                }
                if (this.draftMessage == null && !z3) {
                    this.draftMessage = this.messageEditText.length() > 0 ? this.messageEditText.getText() : null;
                    this.draftSearchWebpage = this.messageWebPageSearch;
                }
                this.messageWebPageSearch = this.editingMessageObject.messageOwner.media instanceof TLRPC$TL_messageMediaWebPage;
                if (!this.keyboardVisible) {
                    Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda48
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatActivityEnterView.this.lambda$setEditingMessageObject$44(str2);
                        }
                    };
                    this.setTextFieldRunnable = runnable;
                    AndroidUtilities.runOnUIThread(runnable, 200L);
                } else {
                    Runnable runnable2 = this.setTextFieldRunnable;
                    if (runnable2 != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable2);
                        this.setTextFieldRunnable = null;
                    }
                    setFieldText(str2);
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
                Runnable runnable3 = this.setTextFieldRunnable;
                if (runnable3 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable3);
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
                if (this.slowModeTimer > 0 && !isInScheduleMode()) {
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
                } else {
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

    public /* synthetic */ void lambda$setEditingMessageObject$44(CharSequence charSequence) {
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
        if (numberTextView != null && this.messageEditText != null) {
            if (this.codePointCount - this.currentLimit < 0) {
                numberTextView.setTextColor(getThemedColor("windowBackgroundWhiteRedText"));
            } else {
                numberTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText"));
            }
        }
        int themedColor = getThemedColor("chat_messagePanelVoicePressed");
        this.doneCheckDrawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(themedColor, (int) (Color.alpha(themedColor) * ((this.doneButtonEnabledProgress * 0.42f) + 0.58f))), PorterDuff.Mode.MULTIPLY));
        BotCommandsMenuContainer botCommandsMenuContainer = this.botCommandsMenuContainer;
        if (botCommandsMenuContainer != null) {
            botCommandsMenuContainer.updateColors();
        }
        BotKeyboardView botKeyboardView = this.botKeyboardView;
        if (botKeyboardView != null) {
            botKeyboardView.updateColors();
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
        if (editTextCaption == null) {
            return;
        }
        this.ignoreTextChange = z;
        editTextCaption.setText(charSequence);
        EditTextCaption editTextCaption2 = this.messageEditText;
        editTextCaption2.setSelection(editTextCaption2.getText().length());
        this.ignoreTextChange = false;
        if (!z || (chatActivityEnterViewDelegate = this.delegate) == null) {
            return;
        }
        chatActivityEnterViewDelegate.onTextChanged(this.messageEditText.getText(), true);
    }

    public void setSelection(int i) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return;
        }
        editTextCaption.setSelection(i, editTextCaption.length());
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
            FileLog.e(e);
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
            FileLog.e(e);
        }
    }

    public void setFieldFocused() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText == null || accessibilityManager.isTouchExplorationEnabled()) {
            return;
        }
        try {
            this.messageEditText.requestFocus();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setFieldFocused(boolean z) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText == null || accessibilityManager.isTouchExplorationEnabled()) {
            return;
        }
        if (z) {
            if (this.searchingType != 0 || this.messageEditText.isFocused()) {
                return;
            }
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda39
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.this.lambda$setFieldFocused$45();
                }
            };
            this.focusRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 600L);
            return;
        }
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null || !editTextCaption.isFocused()) {
            return;
        }
        if (this.keyboardVisible && !this.isPaused) {
            return;
        }
        this.messageEditText.clearFocus();
    }

    public /* synthetic */ void lambda$setFieldFocused$45() {
        EditTextCaption editTextCaption;
        ViewGroup view;
        this.focusRunnable = null;
        boolean z = true;
        if (AndroidUtilities.isTablet()) {
            Activity activity = this.parentActivity;
            if ((activity instanceof LaunchActivity) && (view = ((LaunchActivity) activity).getLayersActionBarLayout().getView()) != null && view.getVisibility() == 0) {
                z = false;
            }
        }
        if (this.isPaused || !z || (editTextCaption = this.messageEditText) == null) {
            return;
        }
        try {
            editTextCaption.requestFocus();
        } catch (Exception e) {
            FileLog.e(e);
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
            if (!TextUtils.isEmpty(this.draftMessage)) {
                return this.draftMessage;
            }
            return null;
        } else if (!hasText()) {
            return null;
        } else {
            return this.messageEditText.getText();
        }
    }

    public CharSequence getFieldText() {
        if (hasText()) {
            return this.messageEditText.getText();
        }
        return null;
    }

    public void updateScheduleButton(boolean z) {
        boolean z2;
        ImageView imageView;
        ImageView imageView2;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        int i = 0;
        if (DialogObject.isChatDialog(this.dialog_id)) {
            TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            this.silent = notificationsSettings.getBoolean("silent_" + this.dialog_id, false);
            z2 = ChatObject.isChannel(chat) && (chat.creator || ((tLRPC$TL_chatAdminRights = chat.admin_rights) != null && tLRPC$TL_chatAdminRights.post_messages)) && !chat.megagroup;
            this.canWriteToChannel = z2;
            if (this.notifyButton != null) {
                if (this.notifySilentDrawable == null) {
                    this.notifySilentDrawable = new CrossOutDrawable(getContext(), R.drawable.input_notify_on, "chat_messagePanelIcons");
                }
                this.notifySilentDrawable.setCrossOut(this.silent, false);
                this.notifyButton.setImageDrawable(this.notifySilentDrawable);
            } else {
                z2 = false;
            }
            LinearLayout linearLayout = this.attachLayout;
            if (linearLayout != null) {
                updateFieldRight(linearLayout.getVisibility() == 0 ? 1 : 0);
            }
        } else {
            z2 = false;
        }
        boolean z3 = this.delegate != null && !isInScheduleMode() && this.delegate.hasScheduledMessages();
        final boolean z4 = z3 && !this.scheduleButtonHidden && !this.recordingAudioVideo;
        ImageView imageView3 = this.scheduledButton;
        float f = 96.0f;
        if (imageView3 != null) {
            if ((imageView3.getTag() != null && z4) || (this.scheduledButton.getTag() == null && !z4)) {
                if (this.notifyButton == null) {
                    return;
                }
                if (z3 || !z2 || this.scheduledButton.getVisibility() == 0) {
                    i = 8;
                }
                if (i == this.notifyButton.getVisibility()) {
                    return;
                }
                this.notifyButton.setVisibility(i);
                LinearLayout linearLayout2 = this.attachLayout;
                if (linearLayout2 == null) {
                    return;
                }
                ImageView imageView4 = this.botButton;
                if ((imageView4 == null || imageView4.getVisibility() == 8) && ((imageView2 = this.notifyButton) == null || imageView2.getVisibility() == 8)) {
                    f = 48.0f;
                }
                linearLayout2.setPivotX(AndroidUtilities.dp(f));
                return;
            }
            this.scheduledButton.setTag(z4 ? 1 : null);
        }
        AnimatorSet animatorSet = this.scheduledButtonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.scheduledButtonAnimation = null;
        }
        float f2 = 0.0f;
        float f3 = 0.1f;
        if (!z || z2) {
            ImageView imageView5 = this.scheduledButton;
            if (imageView5 != null) {
                imageView5.setVisibility(z4 ? 0 : 8);
                ImageView imageView6 = this.scheduledButton;
                if (z4) {
                    f2 = 1.0f;
                }
                imageView6.setAlpha(f2);
                this.scheduledButton.setScaleX(z4 ? 1.0f : 0.1f);
                ImageView imageView7 = this.scheduledButton;
                if (z4) {
                    f3 = 1.0f;
                }
                imageView7.setScaleY(f3);
                ImageView imageView8 = this.notifyButton;
                if (imageView8 != null) {
                    if (!z2 || this.scheduledButton.getVisibility() == 0) {
                        i = 8;
                    }
                    imageView8.setVisibility(i);
                }
            }
        } else {
            ImageView imageView9 = this.scheduledButton;
            if (imageView9 != null) {
                if (z4) {
                    imageView9.setVisibility(0);
                }
                this.scheduledButton.setPivotX(AndroidUtilities.dp(24.0f));
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.scheduledButtonAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView10 = this.scheduledButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                if (z4) {
                    f2 = 1.0f;
                }
                fArr[0] = f2;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView10, property, fArr);
                ImageView imageView11 = this.scheduledButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = z4 ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView11, property2, fArr2);
                ImageView imageView12 = this.scheduledButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (z4) {
                    f3 = 1.0f;
                }
                fArr3[0] = f3;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView12, property3, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.scheduledButtonAnimation.setDuration(180L);
                this.scheduledButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.52
                    {
                        ChatActivityEnterView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        ChatActivityEnterView.this.scheduledButtonAnimation = null;
                        if (!z4) {
                            ChatActivityEnterView.this.scheduledButton.setVisibility(8);
                        }
                    }
                });
                this.scheduledButtonAnimation.start();
            }
        }
        LinearLayout linearLayout3 = this.attachLayout;
        if (linearLayout3 != null) {
            ImageView imageView13 = this.botButton;
            if ((imageView13 == null || imageView13.getVisibility() == 8) && ((imageView = this.notifyButton) == null || imageView.getVisibility() == 8)) {
                f = 48.0f;
            }
            linearLayout3.setPivotX(AndroidUtilities.dp(f));
        }
    }

    public void updateSendAsButton() {
        updateSendAsButton(true);
    }

    public void updateSendAsButton(boolean z) {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity == null || this.delegate == null) {
            return;
        }
        TLRPC$ChatFull chatFull = chatActivity.getMessagesController().getChatFull(-this.dialog_id);
        TLRPC$Peer tLRPC$Peer = chatFull != null ? chatFull.default_send_as : null;
        int i = 0;
        if (tLRPC$Peer == null && this.delegate.getSendAsPeers() != null && !this.delegate.getSendAsPeers().peers.isEmpty()) {
            tLRPC$Peer = this.delegate.getSendAsPeers().peers.get(0).peer;
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
        float f = 0.0f;
        float f2 = z3 ? 0.0f : 1.0f;
        float f3 = z3 ? ((-this.senderSelectView.getLayoutParams().width) - marginLayoutParams.leftMargin) - dp : 0.0f;
        float f4 = z3 ? 1.0f : 0.0f;
        float f5 = z3 ? 0.0f : ((-this.senderSelectView.getLayoutParams().width) - marginLayoutParams.leftMargin) - dp;
        if (z2 == z3) {
            return;
        }
        ValueAnimator valueAnimator = (ValueAnimator) this.senderSelectView.getTag();
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.senderSelectView.setTag(null);
        }
        if (this.parentFragment.getOtherSameChatsDiff() == 0 && this.parentFragment.fragmentOpened && z) {
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
            this.senderSelectView.setTranslationX(f3);
            this.messageEditText.setTranslationX(this.senderSelectView.getTranslationX());
            final float f6 = f2;
            final float f7 = f4;
            final float f8 = f3;
            final float f9 = f5;
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda5
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChatActivityEnterView.this.lambda$updateSendAsButton$46(f6, f7, f8, f9, valueAnimator2);
                }
            });
            final boolean z4 = z3;
            final float var_ = f2;
            final float var_ = f4;
            final float var_ = f5;
            duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.53
                {
                    ChatActivityEnterView.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    if (z4) {
                        ChatActivityEnterView.this.senderSelectView.setVisibility(0);
                    }
                    ChatActivityEnterView.this.senderSelectView.setAlpha(var_);
                    ChatActivityEnterView.this.senderSelectView.setTranslationX(f8);
                    ChatActivityEnterView.this.emojiButton.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.messageEditText.setTranslationX(chatActivityEnterView.senderSelectView.getTranslationX());
                    if (ChatActivityEnterView.this.botCommandsMenuButton.getTag() == null) {
                        ChatActivityEnterView.this.animationParamsX.clear();
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (!z4) {
                        ChatActivityEnterView.this.senderSelectView.setVisibility(8);
                        ChatActivityEnterView.this.emojiButton.setTranslationX(0.0f);
                        ChatActivityEnterView.this.messageEditText.setTranslationX(0.0f);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    if (z4) {
                        ChatActivityEnterView.this.senderSelectView.setVisibility(0);
                    } else {
                        ChatActivityEnterView.this.senderSelectView.setVisibility(8);
                    }
                    ChatActivityEnterView.this.senderSelectView.setAlpha(var_);
                    ChatActivityEnterView.this.senderSelectView.setTranslationX(var_);
                    ChatActivityEnterView.this.emojiButton.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.messageEditText.setTranslationX(chatActivityEnterView.senderSelectView.getTranslationX());
                    ChatActivityEnterView.this.requestLayout();
                }
            });
            duration.start();
            this.senderSelectView.setTag(duration);
            return;
        }
        SenderSelectView senderSelectView = this.senderSelectView;
        if (!z3) {
            i = 8;
        }
        senderSelectView.setVisibility(i);
        this.senderSelectView.setTranslationX(f5);
        if (z3) {
            f = f5;
        }
        this.emojiButton.setTranslationX(f);
        this.messageEditText.setTranslationX(f);
        this.senderSelectView.setAlpha(f4);
        this.senderSelectView.setTag(null);
    }

    public /* synthetic */ void lambda$updateSendAsButton$46(float f, float f2, float f3, float f4, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.senderSelectView.setAlpha(f + ((f2 - f) * floatValue));
        this.senderSelectView.setTranslationX(f3 + ((f4 - f3) * floatValue));
        this.emojiButton.setTranslationX(this.senderSelectView.getTranslationX());
        this.messageEditText.setTranslationX(this.senderSelectView.getTranslationX());
    }

    public boolean onBotWebViewBackPressed() {
        BotWebViewMenuContainer botWebViewMenuContainer = this.botWebViewMenuContainer;
        return botWebViewMenuContainer != null && botWebViewMenuContainer.onBackPressed();
    }

    public boolean hasBotWebView() {
        return this.botMenuButtonType == BotMenuButtonType.WEB_VIEW;
    }

    private void updateBotButton(boolean z) {
        ImageView imageView;
        if (this.botButton == null) {
            return;
        }
        if (!this.parentFragment.openAnimationEnded) {
            z = false;
        }
        boolean hasBotWebView = hasBotWebView();
        boolean z2 = this.botMenuButtonType != BotMenuButtonType.NO_BUTTON && this.dialog_id > 0;
        boolean z3 = this.botButton.getVisibility() == 0;
        if (hasBotWebView || this.hasBotCommands || this.botReplyMarkup != null) {
            if (this.botReplyMarkup != null) {
                if (isPopupShowing() && this.currentPopupContentType == 1) {
                    if (this.botButton.getVisibility() != 8) {
                        this.botButton.setVisibility(8);
                    }
                } else {
                    if (this.botButton.getVisibility() != 0) {
                        this.botButton.setVisibility(0);
                    }
                    this.botButtonDrawable.setIcon(R.drawable.input_bot2, true);
                    this.botButton.setContentDescription(LocaleController.getString("AccDescrBotKeyboard", R.string.AccDescrBotKeyboard));
                }
            } else if (!z2) {
                this.botButtonDrawable.setIcon(R.drawable.input_bot1, true);
                this.botButton.setContentDescription(LocaleController.getString("AccDescrBotCommands", R.string.AccDescrBotCommands));
                this.botButton.setVisibility(0);
            } else {
                this.botButton.setVisibility(8);
            }
        } else {
            this.botButton.setVisibility(8);
        }
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        boolean z4 = botCommandsMenuView.isWebView;
        botCommandsMenuView.setWebView(this.botMenuButtonType == BotMenuButtonType.WEB_VIEW);
        boolean menuText = this.botCommandsMenuButton.setMenuText(this.botMenuButtonType == BotMenuButtonType.COMMANDS ? LocaleController.getString(R.string.BotsMenuTitle) : this.botMenuWebViewTitle);
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
        linearLayout.setPivotX(AndroidUtilities.dp(((imageView2 == null || imageView2.getVisibility() == 8) && ((imageView = this.notifyButton) == null || imageView.getVisibility() == 8)) ? 48.0f : 96.0f));
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

    /* JADX WARN: Code restructure failed: missing block: B:96:0x00a0, code lost:
        if (r7.getInt("answered_" + r5.dialog_id, 0) == r6.getId()) goto L37;
     */
    /* JADX WARN: Removed duplicated region for block: B:106:0x00bf  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0067  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x006a  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x0073  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setButtons(org.telegram.messenger.MessageObject r6, boolean r7) {
        /*
            r5 = this;
            org.telegram.messenger.MessageObject r0 = r5.replyingMessageObject
            if (r0 == 0) goto Ld
            org.telegram.messenger.MessageObject r1 = r5.botButtonsMessageObject
            if (r0 != r1) goto Ld
            if (r0 == r6) goto Ld
            r5.botMessageObject = r6
            return
        Ld:
            android.widget.ImageView r0 = r5.botButton
            if (r0 == 0) goto Ld7
            org.telegram.messenger.MessageObject r0 = r5.botButtonsMessageObject
            if (r0 == 0) goto L17
            if (r0 == r6) goto Ld7
        L17:
            if (r0 != 0) goto L1d
            if (r6 != 0) goto L1d
            goto Ld7
        L1d:
            org.telegram.ui.Components.BotKeyboardView r0 = r5.botKeyboardView
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L4b
            org.telegram.ui.Components.ChatActivityEnterView$54 r0 = new org.telegram.ui.Components.ChatActivityEnterView$54
            android.app.Activity r3 = r5.parentActivity
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r5.resourcesProvider
            r0.<init>(r3, r4)
            r5.botKeyboardView = r0
            r3 = 8
            r0.setVisibility(r3)
            r5.botKeyboardViewVisible = r1
            org.telegram.ui.Components.BotKeyboardView r0 = r5.botKeyboardView
            org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda58 r3 = new org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda58
            r3.<init>()
            r0.setDelegate(r3)
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r5.sizeNotifierLayout
            org.telegram.ui.Components.BotKeyboardView r3 = r5.botKeyboardView
            int r4 = r0.getChildCount()
            int r4 = r4 - r2
            r0.addView(r3, r4)
        L4b:
            r5.botButtonsMessageObject = r6
            if (r6 == 0) goto L5a
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup
            if (r3 == 0) goto L5a
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = (org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup) r0
            goto L5b
        L5a:
            r0 = 0
        L5b:
            r5.botReplyMarkup = r0
            org.telegram.ui.Components.BotKeyboardView r0 = r5.botKeyboardView
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
            int r3 = r3.y
            if (r4 <= r3) goto L6a
            int r3 = r5.keyboardHeightLand
            goto L6c
        L6a:
            int r3 = r5.keyboardHeight
        L6c:
            r0.setPanelHeight(r3)
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = r5.botReplyMarkup
            if (r0 == 0) goto Lbf
            int r7 = r5.currentAccount
            android.content.SharedPreferences r7 = org.telegram.messenger.MessagesController.getMainSettings(r7)
            org.telegram.messenger.MessageObject r0 = r5.botButtonsMessageObject
            org.telegram.messenger.MessageObject r3 = r5.replyingMessageObject
            if (r0 == r3) goto La3
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = r5.botReplyMarkup
            boolean r0 = r0.single_use
            if (r0 == 0) goto La3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "answered_"
            r0.append(r3)
            long r3 = r5.dialog_id
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            int r7 = r7.getInt(r0, r1)
            int r6 = r6.getId()
            if (r7 != r6) goto La3
            goto La4
        La3:
            r1 = 1
        La4:
            org.telegram.ui.Components.BotKeyboardView r6 = r5.botKeyboardView
            org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup r7 = r5.botReplyMarkup
            r6.setButtons(r7)
            if (r1 == 0) goto Ld4
            org.telegram.ui.Components.EditTextCaption r6 = r5.messageEditText
            int r6 = r6.length()
            if (r6 != 0) goto Ld4
            boolean r6 = r5.isPopupShowing()
            if (r6 != 0) goto Ld4
            r5.showPopup(r2, r2)
            goto Ld4
        Lbf:
            boolean r6 = r5.isPopupShowing()
            if (r6 == 0) goto Ld4
            int r6 = r5.currentPopupContentType
            if (r6 != r2) goto Ld4
            if (r7 == 0) goto Ld1
            r5.clearBotButtonsOnKeyboardOpen = r2
            r5.openKeyboardInternal()
            goto Ld4
        Ld1:
            r5.showPopup(r1, r2)
        Ld4:
            r5.updateBotButton(r2)
        Ld7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setButtons(org.telegram.messenger.MessageObject, boolean):void");
    }

    public /* synthetic */ void lambda$setButtons$47(TLRPC$KeyboardButton tLRPC$KeyboardButton) {
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
            chatActivityEnterViewDelegate.onMessageSend(null, true, 0);
        }
    }

    public boolean didPressedBotButton(final TLRPC$KeyboardButton tLRPC$KeyboardButton, final MessageObject messageObject, final MessageObject messageObject2) {
        if (tLRPC$KeyboardButton == null || messageObject2 == null) {
            return false;
        }
        if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButton) {
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(tLRPC$KeyboardButton.text, this.dialog_id, messageObject, getThreadMessage(), null, false, null, null, null, true, 0, null, false);
        } else if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonUrl) {
            AlertsCreator.showOpenUrlAlert(this.parentFragment, tLRPC$KeyboardButton.url, false, true, this.resourcesProvider);
        } else if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonRequestPhone) {
            this.parentFragment.shareMyContact(2, messageObject2);
        } else {
            Boolean bool = null;
            if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonRequestPoll) {
                ChatActivity chatActivity = this.parentFragment;
                if ((tLRPC$KeyboardButton.flags & 1) != 0) {
                    bool = Boolean.valueOf(tLRPC$KeyboardButton.quiz);
                }
                chatActivity.openPollCreate(bool);
                return false;
            } else if ((tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonWebView) || (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonSimpleWebView)) {
                TLRPC$Message tLRPC$Message = messageObject2.messageOwner;
                final long j = tLRPC$Message.via_bot_id;
                if (j == 0) {
                    j = tLRPC$Message.from_id.user_id;
                }
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
                final long j2 = j;
                final Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.55
                    {
                        ChatActivityEnterView.this = this;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        if (ChatActivityEnterView.this.sizeNotifierLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                            AndroidUtilities.hideKeyboard(ChatActivityEnterView.this);
                            AndroidUtilities.runOnUIThread(this, 150L);
                            return;
                        }
                        BotWebViewSheet botWebViewSheet = new BotWebViewSheet(ChatActivityEnterView.this.getContext(), ChatActivityEnterView.this.resourcesProvider);
                        botWebViewSheet.setParentActivity(ChatActivityEnterView.this.parentActivity);
                        int i = ChatActivityEnterView.this.currentAccount;
                        long j3 = messageObject2.messageOwner.dialog_id;
                        long j4 = j2;
                        TLRPC$KeyboardButton tLRPC$KeyboardButton2 = tLRPC$KeyboardButton;
                        String str = tLRPC$KeyboardButton2.text;
                        String str2 = tLRPC$KeyboardButton2.url;
                        boolean z = tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonSimpleWebView;
                        MessageObject messageObject3 = messageObject;
                        botWebViewSheet.requestWebView(i, j3, j4, str, str2, z ? 1 : 0, messageObject3 != null ? messageObject3.messageOwner.id : 0, false);
                        botWebViewSheet.show();
                    }
                };
                if (SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, j)) {
                    runnable.run();
                } else {
                    new AlertDialog.Builder(this.parentFragment.getParentActivity()).setTitle(LocaleController.getString(R.string.BotOpenPageTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BotOpenPageMessage", R.string.BotOpenPageMessage, UserObject.getUserName(user)))).setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda9
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatActivityEnterView.this.lambda$didPressedBotButton$48(runnable, j, dialogInterface, i);
                        }
                    }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
                }
            } else if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonRequestGeoLocation) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", R.string.ShareYouLocationTitle));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", R.string.ShareYouLocationInfo));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda10
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChatActivityEnterView.this.lambda$didPressedBotButton$49(messageObject2, tLRPC$KeyboardButton, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                this.parentFragment.showDialog(builder.create());
            } else if ((tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonCallback) || (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonGame) || (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonBuy) || (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonUrlAuth)) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCallback(true, messageObject2, tLRPC$KeyboardButton, this.parentFragment);
            } else if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonSwitchInline) {
                if (this.parentFragment.processSwitchButton((TLRPC$TL_keyboardButtonSwitchInline) tLRPC$KeyboardButton)) {
                    return true;
                }
                if (tLRPC$KeyboardButton.same_peer) {
                    TLRPC$Message tLRPC$Message2 = messageObject2.messageOwner;
                    long j3 = tLRPC$Message2.from_id.user_id;
                    long j4 = tLRPC$Message2.via_bot_id;
                    if (j4 != 0) {
                        j3 = j4;
                    }
                    TLRPC$User user2 = this.accountInstance.getMessagesController().getUser(Long.valueOf(j3));
                    if (user2 == null) {
                        return true;
                    }
                    setFieldText("@" + user2.username + " " + tLRPC$KeyboardButton.query);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlySelect", true);
                    bundle.putInt("dialogsType", 1);
                    DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                    dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda61
                        @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                        public final void didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z) {
                            ChatActivityEnterView.this.lambda$didPressedBotButton$50(messageObject2, tLRPC$KeyboardButton, dialogsActivity2, arrayList, charSequence, z);
                        }
                    });
                    this.parentFragment.presentFragment(dialogsActivity);
                }
            } else if ((tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonUserProfile) && MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$KeyboardButton.user_id)) != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("user_id", tLRPC$KeyboardButton.user_id);
                this.parentFragment.presentFragment(new ProfileActivity(bundle2));
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$didPressedBotButton$48(Runnable runnable, long j, DialogInterface dialogInterface, int i) {
        runnable.run();
        SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, j, true);
    }

    public /* synthetic */ void lambda$didPressedBotButton$49(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, DialogInterface dialogInterface, int i) {
        if (Build.VERSION.SDK_INT >= 23 && this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
            this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            this.pendingMessageObject = messageObject;
            this.pendingLocationButton = tLRPC$KeyboardButton;
            return;
        }
        SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(messageObject, tLRPC$KeyboardButton);
    }

    public /* synthetic */ void lambda$didPressedBotButton$50(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
        long j3 = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        mediaDataController.saveDraft(j3, 0, "@" + user.username + " " + tLRPC$KeyboardButton.query, null, null, true);
        if (j3 != this.dialog_id) {
            if (!DialogObject.isEncryptedDialog(j3)) {
                Bundle bundle = new Bundle();
                if (DialogObject.isUserDialog(j3)) {
                    bundle.putLong("user_id", j3);
                } else {
                    bundle.putLong("chat_id", -j3);
                }
                if (!this.accountInstance.getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
                    return;
                }
                if (this.parentFragment.presentFragment(new ChatActivity(bundle), true)) {
                    if (AndroidUtilities.isTablet()) {
                        return;
                    }
                    this.parentFragment.removeSelfFromStack();
                    return;
                }
                dialogsActivity.finishFragment();
                return;
            }
            dialogsActivity.finishFragment();
            return;
        }
        dialogsActivity.finishFragment();
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
        EmojiView emojiView = this.emojiView;
        if (emojiView != null && emojiView.currentAccount != UserConfig.selectedAccount) {
            this.sizeNotifierLayout.removeView(emojiView);
            this.emojiView = null;
        }
        if (this.emojiView != null) {
            return;
        }
        EmojiView emojiView2 = new EmojiView(this.parentFragment, this.allowAnimatedEmoji, true, true, getContext(), true, this.info, this.sizeNotifierLayout, this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatActivityEnterView.56
            {
                ChatActivityEnterView.this = this;
            }

            @Override // org.telegram.ui.Components.EmojiView, android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                if (ChatActivityEnterView.this.panelAnimation == null || ChatActivityEnterView.this.animatingContentType != 0) {
                    return;
                }
                ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(f);
            }
        };
        this.emojiView = emojiView2;
        emojiView2.setAllow(this.allowStickers, this.allowGifs, true);
        this.emojiView.setVisibility(8);
        this.emojiView.setShowing(false);
        this.emojiView.setDelegate(new AnonymousClass57());
        this.emojiView.setDragListener(new EmojiView.DragListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.58
            int initialOffset;
            boolean wasExpanded;

            {
                ChatActivityEnterView.this = this;
            }

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDragStart() {
                if (!allowDragging()) {
                    return;
                }
                if (ChatActivityEnterView.this.stickersExpansionAnim != null) {
                    ChatActivityEnterView.this.stickersExpansionAnim.cancel();
                }
                ChatActivityEnterView.this.stickersDragging = true;
                this.wasExpanded = ChatActivityEnterView.this.stickersExpanded;
                ChatActivityEnterView.this.stickersExpanded = true;
                int i = 0;
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 1);
                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                int height = chatActivityEnterView.sizeNotifierLayout.getHeight();
                if (Build.VERSION.SDK_INT >= 21) {
                    i = AndroidUtilities.statusBarHeight;
                }
                chatActivityEnterView.stickersExpandedHeight = (((height - i) - ActionBar.getCurrentActionBarHeight()) - ChatActivityEnterView.this.getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                if (ChatActivityEnterView.this.searchingType == 2) {
                    ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                    int i2 = chatActivityEnterView2.stickersExpandedHeight;
                    int dp = AndroidUtilities.dp(120.0f);
                    android.graphics.Point point = AndroidUtilities.displaySize;
                    chatActivityEnterView2.stickersExpandedHeight = Math.min(i2, dp + (point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight));
                }
                ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                ChatActivityEnterView.this.emojiView.setLayerType(2, null);
                ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                ChatActivityEnterView.this.sizeNotifierLayout.setForeground(new ScrimDrawable());
                this.initialOffset = (int) ChatActivityEnterView.this.getTranslationY();
                if (ChatActivityEnterView.this.delegate == null) {
                    return;
                }
                ChatActivityEnterView.this.delegate.onStickersExpandedChange();
            }

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDragEnd(float f) {
                if (!allowDragging()) {
                    return;
                }
                ChatActivityEnterView.this.stickersDragging = false;
                if ((this.wasExpanded && f >= AndroidUtilities.dp(200.0f)) || ((!this.wasExpanded && f <= AndroidUtilities.dp(-200.0f)) || ((this.wasExpanded && ChatActivityEnterView.this.stickersExpansionProgress <= 0.6f) || (!this.wasExpanded && ChatActivityEnterView.this.stickersExpansionProgress >= 0.4f)))) {
                    ChatActivityEnterView.this.setStickersExpanded(!this.wasExpanded, true, true);
                } else {
                    ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, true);
                }
            }

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDragCancel() {
                if (!ChatActivityEnterView.this.stickersTabOpen) {
                    return;
                }
                ChatActivityEnterView.this.stickersDragging = false;
                ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, false);
            }

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDrag(int i) {
                int i2;
                ChatActivityEnterView chatActivityEnterView;
                if (!allowDragging()) {
                    return;
                }
                android.graphics.Point point = AndroidUtilities.displaySize;
                float max = Math.max(Math.min(i + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - (point.x > point.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight)));
                ChatActivityEnterView.this.emojiView.setTranslationY(max);
                ChatActivityEnterView.this.setTranslationY(max);
                ChatActivityEnterView.this.stickersExpansionProgress = max / (-(chatActivityEnterView.stickersExpandedHeight - i2));
                ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
            }

            private boolean allowDragging() {
                return ChatActivityEnterView.this.stickersTabOpen && (ChatActivityEnterView.this.stickersExpanded || ChatActivityEnterView.this.messageEditText.length() <= 0) && ChatActivityEnterView.this.emojiView.areThereAnyStickers() && !ChatActivityEnterView.this.waitingForKeyboardOpen;
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 5);
        checkChannelRights();
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$57 */
    /* loaded from: classes3.dex */
    public class AnonymousClass57 implements EmojiView.EmojiViewDelegate {
        AnonymousClass57() {
            ChatActivityEnterView.this = r1;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean isUserSelf() {
            return ChatActivityEnterView.this.dialog_id == UserConfig.getInstance(ChatActivityEnterView.this.currentAccount).getClientUserId();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean onBackspace() {
            if (ChatActivityEnterView.this.messageEditText.length() == 0) {
                return false;
            }
            ChatActivityEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
            return true;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onEmojiSelected(String str) {
            int selectionEnd = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
            if (selectionEnd < 0) {
                selectionEnd = 0;
            }
            try {
                try {
                    ChatActivityEnterView.this.innerTextChange = 2;
                    CharSequence replaceEmoji = Emoji.replaceEmoji(str, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    EditTextCaption editTextCaption = ChatActivityEnterView.this.messageEditText;
                    editTextCaption.setText(editTextCaption.getText().insert(selectionEnd, replaceEmoji));
                    int length = selectionEnd + replaceEmoji.length();
                    ChatActivityEnterView.this.messageEditText.setSelection(length, length);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } finally {
                ChatActivityEnterView.this.innerTextChange = 0;
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onCustomEmojiSelected(final long j, final TLRPC$Document tLRPC$Document, final String str, final boolean z) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$57$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.AnonymousClass57.this.lambda$onCustomEmojiSelected$0(str, tLRPC$Document, j, z);
                }
            });
        }

        public /* synthetic */ void lambda$onCustomEmojiSelected$0(String str, TLRPC$Document tLRPC$Document, long j, boolean z) {
            AnimatedEmojiSpan animatedEmojiSpan;
            int selectionEnd = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
            if (selectionEnd < 0) {
                selectionEnd = 0;
            }
            try {
                try {
                    ChatActivityEnterView.this.innerTextChange = 2;
                    if (str == null) {
                        str = "😀";
                    }
                    SpannableString spannableString = new SpannableString(str);
                    if (tLRPC$Document != null) {
                        animatedEmojiSpan = new AnimatedEmojiSpan(tLRPC$Document, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt());
                    } else {
                        animatedEmojiSpan = new AnimatedEmojiSpan(j, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt());
                    }
                    if (!z) {
                        animatedEmojiSpan.fromEmojiKeyboard = true;
                    }
                    animatedEmojiSpan.cacheType = AnimatedEmojiDrawable.getCacheTypeForEnterView();
                    spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
                    EditTextCaption editTextCaption = ChatActivityEnterView.this.messageEditText;
                    editTextCaption.setText(editTextCaption.getText().insert(selectionEnd, spannableString));
                    ChatActivityEnterView.this.messageEditText.setSelection(spannableString.length() + selectionEnd, selectionEnd + spannableString.length());
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } finally {
                ChatActivityEnterView.this.innerTextChange = 0;
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onAnimatedEmojiUnlockClick() {
            PremiumFeatureBottomSheet premiumFeatureBottomSheet = new PremiumFeatureBottomSheet(ChatActivityEnterView.this.parentFragment, 11, false);
            if (ChatActivityEnterView.this.parentFragment != null) {
                ChatActivityEnterView.this.parentFragment.showDialog(premiumFeatureBottomSheet);
            } else {
                premiumFeatureBottomSheet.show();
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onStickerSelected(View view, TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
            if (ChatActivityEnterView.this.trendingStickersAlert != null) {
                ChatActivityEnterView.this.trendingStickersAlert.dismiss();
                ChatActivityEnterView.this.trendingStickersAlert = null;
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
                ChatActivityEnterView.this.lambda$onStickerSelected$51(tLRPC$Document, str, obj, sendAnimationData, false, z, i);
                if (!DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id) || !MessageObject.isGifDocument(tLRPC$Document)) {
                    return;
                }
                ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj, tLRPC$Document);
            } else if (ChatActivityEnterView.this.delegate == null) {
            } else {
                ChatActivityEnterView.this.delegate.onUpdateSlowModeButton(view != null ? view : ChatActivityEnterView.this.slowModeButton, true, ChatActivityEnterView.this.slowModeButton.getText());
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onStickersSettingsClick() {
            if (ChatActivityEnterView.this.parentFragment != null) {
                ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0, null));
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onEmojiSettingsClick(ArrayList<TLRPC$TL_messages_stickerSet> arrayList) {
            if (ChatActivityEnterView.this.parentFragment != null) {
                ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(5, arrayList));
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        /* renamed from: onGifSelected */
        public void lambda$onGifSelected$1(final View view, final Object obj, final String str, final Object obj2, boolean z, int i) {
            if (!isInScheduleMode() || i != 0) {
                if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                        }
                        ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                    }
                    if (obj instanceof TLRPC$Document) {
                        TLRPC$Document tLRPC$Document = (TLRPC$Document) obj;
                        SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(tLRPC$Document, str, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), obj2, null, z, i, false);
                        MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(tLRPC$Document, (int) (System.currentTimeMillis() / 1000), true);
                        if (DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id)) {
                            ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj2, tLRPC$Document);
                        }
                    } else if (obj instanceof TLRPC$BotInlineResult) {
                        TLRPC$BotInlineResult tLRPC$BotInlineResult = (TLRPC$BotInlineResult) obj;
                        if (tLRPC$BotInlineResult.document != null) {
                            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(tLRPC$BotInlineResult.document, (int) (System.currentTimeMillis() / 1000), false);
                            if (DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id)) {
                                ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj2, tLRPC$BotInlineResult.document);
                            }
                        }
                        TLRPC$User tLRPC$User = (TLRPC$User) obj2;
                        HashMap hashMap = new HashMap();
                        hashMap.put("id", tLRPC$BotInlineResult.id);
                        hashMap.put("query_id", "" + tLRPC$BotInlineResult.query_id);
                        hashMap.put("force_gif", "1");
                        SendMessagesHelper.prepareSendingBotContextResult(ChatActivityEnterView.this.parentFragment, ChatActivityEnterView.this.accountInstance, tLRPC$BotInlineResult, hashMap, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), z, i);
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            ChatActivityEnterView.this.setSearchingTypeInternal(0, true);
                            ChatActivityEnterView.this.emojiView.closeSearch(true);
                            ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                        }
                    }
                    if (ChatActivityEnterView.this.delegate == null) {
                        return;
                    }
                    ChatActivityEnterView.this.delegate.onMessageSend(null, z, i);
                    return;
                } else if (ChatActivityEnterView.this.delegate == null) {
                    return;
                } else {
                    ChatActivityEnterView.this.delegate.onUpdateSlowModeButton(view != null ? view : ChatActivityEnterView.this.slowModeButton, true, ChatActivityEnterView.this.slowModeButton.getText());
                    return;
                }
            }
            AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$57$$ExternalSyntheticLambda2
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public final void didSelectDate(boolean z2, int i2) {
                    ChatActivityEnterView.AnonymousClass57.this.lambda$onGifSelected$1(view, obj, str, obj2, z2, i2);
                }
            }, ChatActivityEnterView.this.resourcesProvider);
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onTabOpened(int i) {
            ChatActivityEnterView.this.delegate.onStickersTab(i == 3);
            ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
            chatActivityEnterView.post(chatActivityEnterView.updateExpandabilityRunnable);
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onClearEmojiRecent() {
            if (ChatActivityEnterView.this.parentFragment == null || ChatActivityEnterView.this.parentActivity == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.resourcesProvider);
            builder.setTitle(LocaleController.getString("ClearRecentEmojiTitle", R.string.ClearRecentEmojiTitle));
            builder.setMessage(LocaleController.getString("ClearRecentEmojiText", R.string.ClearRecentEmojiText));
            builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearForAll), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$57$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatActivityEnterView.AnonymousClass57.this.lambda$onClearEmojiRecent$2(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
        }

        public /* synthetic */ void lambda$onClearEmojiRecent$2(DialogInterface dialogInterface, int i) {
            ChatActivityEnterView.this.emojiView.clearRecentEmoji();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onShowStickerSet(TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet) {
            if (ChatActivityEnterView.this.trendingStickersAlert == null || ChatActivityEnterView.this.trendingStickersAlert.isDismissed()) {
                if (ChatActivityEnterView.this.parentFragment == null || ChatActivityEnterView.this.parentActivity == null) {
                    return;
                }
                if (tLRPC$StickerSet != null) {
                    tLRPC$InputStickerSet = new TLRPC$TL_inputStickerSetID();
                    tLRPC$InputStickerSet.access_hash = tLRPC$StickerSet.access_hash;
                    tLRPC$InputStickerSet.id = tLRPC$StickerSet.id;
                }
                ChatActivity chatActivity = ChatActivityEnterView.this.parentFragment;
                Activity activity = ChatActivityEnterView.this.parentActivity;
                ChatActivity chatActivity2 = ChatActivityEnterView.this.parentFragment;
                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                chatActivity.showDialog(new StickersAlert(activity, chatActivity2, tLRPC$InputStickerSet, null, chatActivityEnterView, chatActivityEnterView.resourcesProvider));
                return;
            }
            ChatActivityEnterView.this.trendingStickersAlert.getLayout().showStickerSet(tLRPC$StickerSet, tLRPC$InputStickerSet);
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).toggleStickerSet(ChatActivityEnterView.this.parentActivity, tLRPC$StickerSetCovered, 2, ChatActivityEnterView.this.parentFragment, false, false);
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).toggleStickerSet(ChatActivityEnterView.this.parentActivity, tLRPC$StickerSetCovered, 0, ChatActivityEnterView.this.parentFragment, false, false);
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
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

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onSearchOpenClose(int i) {
            ChatActivityEnterView.this.setSearchingTypeInternal(i, true);
            if (i != 0) {
                ChatActivityEnterView.this.setStickersExpanded(true, true, false, i == 1);
            }
            if (!ChatActivityEnterView.this.emojiTabOpen || ChatActivityEnterView.this.searchingType != 2) {
                return;
            }
            ChatActivityEnterView.this.checkStickresExpandHeight();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean isSearchOpened() {
            return ChatActivityEnterView.this.searchingType != 0;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean isExpanded() {
            return ChatActivityEnterView.this.stickersExpanded;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean canSchedule() {
            return ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.canScheduleMessage();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean isInScheduleMode() {
            return ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.isInScheduleMode();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public long getDialogId() {
            return ChatActivityEnterView.this.dialog_id;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public int getThreadId() {
            return ChatActivityEnterView.this.getThreadMessageId();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout) {
            if (ChatActivityEnterView.this.parentActivity == null || ChatActivityEnterView.this.parentFragment == null) {
                return;
            }
            ChatActivityEnterView.this.trendingStickersAlert = new TrendingStickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, trendingStickersLayout, ChatActivityEnterView.this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatActivityEnterView.57.1
                {
                    AnonymousClass57.this = this;
                }

                @Override // org.telegram.ui.Components.TrendingStickersAlert, org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
                public void dismiss() {
                    super.dismiss();
                    if (ChatActivityEnterView.this.trendingStickersAlert == this) {
                        ChatActivityEnterView.this.trendingStickersAlert = null;
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

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void invalidateEnterView() {
            ChatActivityEnterView.this.invalidate();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public float getProgressToSearchOpened() {
            return ChatActivityEnterView.this.searchToOpenProgress;
        }
    }

    @Override // org.telegram.ui.Components.StickersAlert.StickersAlertDelegate
    /* renamed from: onStickerSelected */
    public void lambda$onStickerSelected$51(final TLRPC$Document tLRPC$Document, final String str, final Object obj, final MessageObject.SendAnimationData sendAnimationData, final boolean z, boolean z2, int i) {
        if (isInScheduleMode() && i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda55
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public final void didSelectDate(boolean z3, int i2) {
                    ChatActivityEnterView.this.lambda$onStickerSelected$51(tLRPC$Document, str, obj, sendAnimationData, z, z3, i2);
                }
            }, this.resourcesProvider);
        } else if (this.slowModeTimer > 0 && !isInScheduleMode()) {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate == null) {
                return;
            }
            SimpleTextView simpleTextView = this.slowModeButton;
            chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
        } else {
            if (this.searchingType != 0) {
                setSearchingTypeInternal(0, true);
                this.emojiView.closeSearch(true);
                this.emojiView.hideSearchKeyboard();
            }
            setStickersExpanded(false, true, false);
            SendMessagesHelper.getInstance(this.currentAccount).sendSticker(tLRPC$Document, str, this.dialog_id, this.replyingMessageObject, getThreadMessage(), obj, sendAnimationData, z2, i, obj instanceof TLRPC$TL_messages_stickerSet);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
            if (chatActivityEnterViewDelegate2 != null) {
                chatActivityEnterViewDelegate2.onMessageSend(null, true, i);
            }
            if (z) {
                setFieldText("");
            }
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, obj, tLRPC$Document, (int) (System.currentTimeMillis() / 1000), false);
        }
    }

    @Override // org.telegram.ui.Components.StickersAlert.StickersAlertDelegate
    public boolean canSchedule() {
        ChatActivity chatActivity = this.parentFragment;
        return chatActivity != null && chatActivity.canScheduleMessage();
    }

    @Override // org.telegram.ui.Components.StickersAlert.StickersAlertDelegate
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

    public void showPopup(int i, int i2) {
        showPopup(i, i2, true);
    }

    private void showPopup(final int i, int i2, boolean z) {
        BotKeyboardView botKeyboardView;
        EmojiView emojiView;
        int i3;
        View view;
        SizeNotifierFrameLayout sizeNotifierFrameLayout;
        if (i == 2) {
            return;
        }
        if (i == 1) {
            if (i2 == 0) {
                if (this.parentActivity == null && this.emojiView == null) {
                    return;
                }
                createEmojiView();
            }
            if (i2 == 0) {
                if (this.emojiView.getParent() == null) {
                    this.sizeNotifierLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 5);
                }
                if (this.emojiViewVisible) {
                    this.emojiView.getVisibility();
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
            } else if (i2 == 1) {
                if (this.botKeyboardViewVisible) {
                    this.botKeyboardView.getVisibility();
                }
                this.botKeyboardViewVisible = true;
                EmojiView emojiView2 = this.emojiView;
                if (emojiView2 == null || emojiView2.getVisibility() == 8) {
                    i3 = 0;
                } else {
                    this.sizeNotifierLayout.removeView(this.emojiView);
                    this.emojiView.setVisibility(8);
                    this.emojiView.setShowing(false);
                    this.emojiViewVisible = false;
                    i3 = this.emojiView.getMeasuredHeight();
                }
                this.botKeyboardView.setVisibility(0);
                view = this.botKeyboardView;
                this.animatingContentType = 1;
            } else {
                i3 = 0;
                view = null;
            }
            this.currentPopupContentType = i2;
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            android.graphics.Point point = AndroidUtilities.displaySize;
            int i4 = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
            if (i2 == 1) {
                i4 = Math.min(this.botKeyboardView.getKeyboardHeight(), i4);
            }
            BotKeyboardView botKeyboardView3 = this.botKeyboardView;
            if (botKeyboardView3 != null) {
                botKeyboardView3.setPanelHeight(i4);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.height = i4;
            view.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayout2 != null) {
                this.emojiPadding = i4;
                sizeNotifierFrameLayout2.requestLayout();
                setEmojiButtonImage(true, true);
                updateBotButton(true);
                onWindowSizeChanged();
                if (this.smoothKeyboard && !this.keyboardVisible && i4 != i3 && z) {
                    this.panelAnimation = new AnimatorSet();
                    float f = i4 - i3;
                    view.setTranslationY(f);
                    this.panelAnimation.playTogether(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, f, 0.0f));
                    this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    this.panelAnimation.setDuration(250L);
                    this.panelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.59
                        {
                            ChatActivityEnterView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            ChatActivityEnterView.this.panelAnimation = null;
                            if (ChatActivityEnterView.this.delegate != null) {
                                ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                            }
                            NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                            ChatActivityEnterView.this.requestLayout();
                        }
                    });
                    AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50L);
                    this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
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
                if (i != 2 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    if (this.smoothKeyboard && !this.keyboardVisible && !this.stickersExpanded) {
                        this.emojiViewVisible = true;
                        this.animatingContentType = 0;
                        emojiView3.setShowing(false);
                        AnimatorSet animatorSet = new AnimatorSet();
                        this.panelAnimation = animatorSet;
                        animatorSet.playTogether(ObjectAnimator.ofFloat(this.emojiView, View.TRANSLATION_Y, emojiView.getMeasuredHeight()));
                        this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                        this.panelAnimation.setDuration(250L);
                        this.panelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.60
                            {
                                ChatActivityEnterView.this = this;
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                if (i == 0) {
                                    ChatActivityEnterView.this.emojiPadding = 0;
                                }
                                ChatActivityEnterView.this.panelAnimation = null;
                                if (ChatActivityEnterView.this.emojiView != null) {
                                    ChatActivityEnterView.this.emojiView.setTranslationY(0.0f);
                                    ChatActivityEnterView.this.emojiView.setVisibility(8);
                                    ChatActivityEnterView.this.sizeNotifierLayout.removeView(ChatActivityEnterView.this.emojiView);
                                    if (ChatActivityEnterView.this.removeEmojiViewAfterAnimation) {
                                        ChatActivityEnterView.this.removeEmojiViewAfterAnimation = false;
                                        ChatActivityEnterView.this.emojiView = null;
                                    }
                                }
                                if (ChatActivityEnterView.this.delegate != null) {
                                    ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                }
                                NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                ChatActivityEnterView.this.requestLayout();
                            }
                        });
                        this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
                        AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50L);
                        requestLayout();
                    } else {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                        if (chatActivityEnterViewDelegate != null) {
                            chatActivityEnterViewDelegate.bottomPanelTranslationYChanged(0.0f);
                        }
                        this.emojiPadding = 0;
                        this.sizeNotifierLayout.removeView(this.emojiView);
                        this.emojiView.setVisibility(8);
                        this.emojiView.setShowing(false);
                    }
                } else {
                    this.removeEmojiViewAfterAnimation = false;
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                    if (chatActivityEnterViewDelegate2 != null) {
                        chatActivityEnterViewDelegate2.bottomPanelTranslationYChanged(0.0f);
                    }
                    this.sizeNotifierLayout.removeView(this.emojiView);
                    this.emojiView = null;
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
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.botKeyboardView, View.TRANSLATION_Y, botKeyboardView.getMeasuredHeight()));
                        this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                        this.panelAnimation.setDuration(250L);
                        this.panelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.61
                            {
                                ChatActivityEnterView.this = this;
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                if (i == 0) {
                                    ChatActivityEnterView.this.emojiPadding = 0;
                                }
                                ChatActivityEnterView.this.panelAnimation = null;
                                ChatActivityEnterView.this.botKeyboardView.setTranslationY(0.0f);
                                ChatActivityEnterView.this.botKeyboardView.setVisibility(8);
                                NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                if (ChatActivityEnterView.this.delegate != null) {
                                    ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                }
                                ChatActivityEnterView.this.requestLayout();
                            }
                        });
                        this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
                        AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50L);
                        requestLayout();
                    } else if (!this.waitingForKeyboardOpen) {
                        this.botKeyboardView.setVisibility(8);
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

    public void setEmojiButtonImage(boolean z, boolean z2) {
        int currentPage;
        ChatActivityEnterViewAnimatedIconView.State state;
        FrameLayout frameLayout;
        if (this.emojiButton == null) {
            return;
        }
        if (this.recordInterfaceState == 1 || ((frameLayout = this.recordedAudioPanel) != null && frameLayout.getVisibility() == 0)) {
            this.emojiButton.setScaleX(0.0f);
            this.emojiButton.setScaleY(0.0f);
            this.emojiButton.setAlpha(0.0f);
            z2 = false;
        }
        if (z && this.currentPopupContentType == 0) {
            state = ChatActivityEnterViewAnimatedIconView.State.KEYBOARD;
        } else {
            EmojiView emojiView = this.emojiView;
            if (emojiView == null) {
                currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
            } else {
                currentPage = emojiView.getCurrentPage();
            }
            if (currentPage == 0 || (!this.allowStickers && !this.allowGifs)) {
                state = ChatActivityEnterViewAnimatedIconView.State.SMILE;
            } else {
                EditTextCaption editTextCaption = this.messageEditText;
                if (editTextCaption != null && !TextUtils.isEmpty(editTextCaption.getText())) {
                    state = ChatActivityEnterViewAnimatedIconView.State.SMILE;
                } else if (currentPage == 1) {
                    state = ChatActivityEnterViewAnimatedIconView.State.STICKER;
                } else {
                    state = ChatActivityEnterViewAnimatedIconView.State.GIF;
                }
            }
        }
        this.emojiButton.setState(state, z2);
        onEmojiIconChanged(state);
    }

    protected void onEmojiIconChanged(ChatActivityEnterViewAnimatedIconView.State state) {
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
        if (isPopupShowing()) {
            if (this.currentPopupContentType == 1 && z && this.botButtonsMessageObject != null) {
                return false;
            }
            if ((z && this.searchingType != 0) || z2) {
                setSearchingTypeInternal(0, true);
                EmojiView emojiView = this.emojiView;
                if (emojiView != null) {
                    emojiView.closeSearch(true);
                }
                this.messageEditText.requestFocus();
                setStickersExpanded(false, true, false);
                if (this.emojiTabOpen) {
                    checkSendButton(true);
                }
            } else {
                if (this.searchingType != 0) {
                    setSearchingTypeInternal(0, false);
                    this.emojiView.closeSearch(false);
                    this.messageEditText.requestFocus();
                }
                showPopup(0, 0);
            }
            return true;
        }
        return false;
    }

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
                EmojiView emojiView = this.emojiView;
                if (emojiView != null) {
                    emojiView.searchProgressChanged();
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
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda3
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ChatActivityEnterView.this.lambda$setSearchingTypeInternal$52(valueAnimator2);
                    }
                });
                this.searchAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.62
                    {
                        ChatActivityEnterView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        ChatActivityEnterView.this.searchToOpenProgress = z2 ? 1.0f : 0.0f;
                        if (ChatActivityEnterView.this.emojiView != null) {
                            ChatActivityEnterView.this.emojiView.searchProgressChanged();
                        }
                    }
                });
                this.searchAnimator.setDuration(220L);
                this.searchAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.searchAnimator.start();
            }
        }
        this.searchingType = i;
    }

    public /* synthetic */ void lambda$setSearchingTypeInternal$52(ValueAnimator valueAnimator) {
        this.searchToOpenProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.searchProgressChanged();
        }
    }

    public void openKeyboardInternal() {
        ChatActivity chatActivity;
        if (!hasBotWebView() || !botCommandsMenuIsShowing()) {
            showPopup((AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow || ((chatActivity = this.parentFragment) != null && chatActivity.isInBubbleMode()) || this.isPaused) ? 0 : 2, 0);
            this.messageEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.messageEditText);
            if (this.isPaused) {
                this.showKeyboardOnResume = true;
            } else if (AndroidUtilities.usingHardwareInput || this.keyboardVisible || AndroidUtilities.isInMultiwindow) {
            } else {
                ChatActivity chatActivity2 = this.parentFragment;
                if (chatActivity2 != null && chatActivity2.isInBubbleMode()) {
                    return;
                }
                this.waitingForKeyboardOpen = true;
                EmojiView emojiView = this.emojiView;
                if (emojiView != null) {
                    emojiView.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 3, 0.0f, 0.0f, 0));
                }
                AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
            }
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
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.addRecentGif(tLRPC$Document);
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
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

    @Override // org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate
    public void onSizeChanged(int i, boolean z) {
        MessageObject messageObject;
        TLRPC$TL_replyKeyboardMarkup tLRPC$TL_replyKeyboardMarkup;
        boolean z2;
        View view;
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
            int i2 = z ? this.keyboardHeightLand : this.keyboardHeight;
            if (this.currentPopupContentType == 1 && !this.botKeyboardView.isFullSize()) {
                i2 = Math.min(this.botKeyboardView.getKeyboardHeight(), i2);
            }
            int i3 = this.currentPopupContentType;
            if (i3 == 0) {
                view = this.emojiView;
            } else {
                view = i3 == 1 ? this.botKeyboardView : null;
            }
            BotKeyboardView botKeyboardView = this.botKeyboardView;
            if (botKeyboardView != null) {
                botKeyboardView.setPanelHeight(i2);
            }
            if (view != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                if (!this.closeAnimationInProgress) {
                    int i4 = layoutParams.width;
                    int i5 = AndroidUtilities.displaySize.x;
                    if ((i4 != i5 || layoutParams.height != i2) && !this.stickersExpanded) {
                        layoutParams.width = i5;
                        layoutParams.height = i2;
                        view.setLayoutParams(layoutParams);
                        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                        if (sizeNotifierFrameLayout != null) {
                            int i6 = this.emojiPadding;
                            this.emojiPadding = layoutParams.height;
                            sizeNotifierFrameLayout.requestLayout();
                            onWindowSizeChanged();
                            if (this.smoothKeyboard && !this.keyboardVisible && i6 != this.emojiPadding && pannelAnimationEnabled()) {
                                AnimatorSet animatorSet = new AnimatorSet();
                                this.panelAnimation = animatorSet;
                                animatorSet.playTogether(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, this.emojiPadding - i6, 0.0f));
                                this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                                this.panelAnimation.setDuration(250L);
                                this.panelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.63
                                    {
                                        ChatActivityEnterView.this = this;
                                    }

                                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                    public void onAnimationEnd(Animator animator) {
                                        ChatActivityEnterView.this.panelAnimation = null;
                                        if (ChatActivityEnterView.this.delegate != null) {
                                            ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                        }
                                        ChatActivityEnterView.this.requestLayout();
                                        NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                    }
                                });
                                AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50L);
                                this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
                                requestLayout();
                            }
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
        } else if (!this.keyboardVisible && !isPopupShowing() && (messageObject = this.botButtonsMessageObject) != null && this.replyingMessageObject != messageObject && ((!hasBotWebView() || !botCommandsMenuIsShowing()) && TextUtils.isEmpty(this.messageEditText.getText()) && (tLRPC$TL_replyKeyboardMarkup = this.botReplyMarkup) != null && !tLRPC$TL_replyKeyboardMarkup.rows.isEmpty())) {
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

    public MessageObject getThreadMessage() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            return chatActivity.getThreadMessage();
        }
        return null;
    }

    public int getThreadMessageId() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity == null || chatActivity.getThreadMessage() == null) {
            return 0;
        }
        return this.parentFragment.getThreadMessage().getId();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$ChatFull tLRPC$ChatFull;
        TLRPC$Chat chat;
        int i3;
        if (i == NotificationCenter.emojiLoaded) {
            EmojiView emojiView = this.emojiView;
            if (emojiView != null) {
                emojiView.invalidateViews();
            }
            BotKeyboardView botKeyboardView = this.botKeyboardView;
            if (botKeyboardView != null) {
                botKeyboardView.invalidateViews();
            }
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption == null) {
                return;
            }
            editTextCaption.postInvalidate();
            this.messageEditText.invalidateForce();
            return;
        }
        int i4 = 0;
        if (i == NotificationCenter.recordProgressChanged) {
            if (((Integer) objArr[0]).intValue() != this.recordingGuid) {
                return;
            }
            if (this.recordInterfaceState != 0 && !this.wasSendTyping && !isInScheduleMode()) {
                this.wasSendTyping = true;
                this.accountInstance.getMessagesController().sendTyping(this.dialog_id, getThreadMessageId(), isInVideoMode() ? 7 : 1, 0);
            }
            RecordCircle recordCircle = this.recordCircle;
            if (recordCircle == null) {
                return;
            }
            recordCircle.setAmplitude(((Double) objArr[1]).doubleValue());
        } else if (i == NotificationCenter.closeChats) {
            EditTextCaption editTextCaption2 = this.messageEditText;
            if (editTextCaption2 == null || !editTextCaption2.isFocused()) {
                return;
            }
            AndroidUtilities.hideKeyboard(this.messageEditText);
        } else {
            int i5 = 4;
            if (i == NotificationCenter.recordStartError || i == NotificationCenter.recordStopped) {
                if (((Integer) objArr[0]).intValue() != this.recordingGuid) {
                    return;
                }
                if (this.recordingAudioVideo) {
                    this.recordingAudioVideo = false;
                    if (i == NotificationCenter.recordStopped) {
                        Integer num = (Integer) objArr[1];
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
                if (i != NotificationCenter.recordStopped) {
                    return;
                }
                Integer num2 = (Integer) objArr[1];
            } else if (i == NotificationCenter.recordStarted) {
                if (((Integer) objArr[0]).intValue() != this.recordingGuid) {
                    return;
                }
                boolean booleanValue = ((Boolean) objArr[1]).booleanValue();
                this.isInVideoMode = !booleanValue;
                this.audioVideoSendButton.setState(booleanValue ? ChatActivityEnterViewAnimatedIconView.State.VOICE : ChatActivityEnterViewAnimatedIconView.State.VIDEO, true);
                if (!this.recordingAudioVideo) {
                    this.recordingAudioVideo = true;
                    updateRecordInterface(0);
                } else {
                    this.recordCircle.showWaves(true, true);
                }
                this.recordTimerView.start();
                this.recordDot.enterAnimation = false;
            } else if (i == NotificationCenter.audioDidSent) {
                if (((Integer) objArr[0]).intValue() != this.recordingGuid) {
                    return;
                }
                Object obj = objArr[1];
                if (obj instanceof VideoEditedInfo) {
                    this.videoToSendMessageObject = (VideoEditedInfo) obj;
                    String str = (String) objArr[2];
                    this.audioToSendPath = str;
                    this.videoTimelineView.setVideoPath(str);
                    this.videoTimelineView.setKeyframes((ArrayList) objArr[3]);
                    this.videoTimelineView.setVisibility(0);
                    this.videoTimelineView.setMinProgressDiff(1000.0f / ((float) this.videoToSendMessageObject.estimatedDuration));
                    updateRecordInterface(3);
                    checkSendButton(false);
                    return;
                }
                TLRPC$TL_document tLRPC$TL_document = (TLRPC$TL_document) objArr[1];
                this.audioToSend = tLRPC$TL_document;
                this.audioToSendPath = (String) objArr[2];
                if (tLRPC$TL_document != null) {
                    if (this.recordedAudioPanel == null) {
                        return;
                    }
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
                    return;
                }
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate == null) {
                    return;
                }
                chatActivityEnterViewDelegate.onMessageSend(null, true, 0);
            } else if (i == NotificationCenter.audioRouteChanged) {
                if (this.parentActivity == null) {
                    return;
                }
                boolean booleanValue2 = ((Boolean) objArr[0]).booleanValue();
                Activity activity = this.parentActivity;
                if (!booleanValue2) {
                    i4 = Integer.MIN_VALUE;
                }
                activity.setVolumeControlStream(i4);
            } else if (i == NotificationCenter.messagePlayingDidReset) {
                if (this.audioToSendMessageObject == null || MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                    return;
                }
                this.playPauseDrawable.setIcon(0, true);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
                this.recordedAudioSeekBar.setProgress(0.0f);
            } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
                Integer num3 = (Integer) objArr[0];
                if (this.audioToSendMessageObject == null || !MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                    return;
                }
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                MessageObject messageObject = this.audioToSendMessageObject;
                messageObject.audioProgress = playingMessageObject.audioProgress;
                messageObject.audioProgressSec = playingMessageObject.audioProgressSec;
                if (this.recordedAudioSeekBar.isDragging()) {
                    return;
                }
                this.recordedAudioSeekBar.setProgress(this.audioToSendMessageObject.audioProgress);
            } else if (i == NotificationCenter.featuredStickersDidLoad) {
                ChatActivityEnterViewAnimatedIconView chatActivityEnterViewAnimatedIconView = this.emojiButton;
                if (chatActivityEnterViewAnimatedIconView == null) {
                    return;
                }
                chatActivityEnterViewAnimatedIconView.invalidate();
            } else if (i == NotificationCenter.messageReceivedByServer) {
                if (((Boolean) objArr[6]).booleanValue() || ((Long) objArr[3]).longValue() != this.dialog_id || (tLRPC$ChatFull = this.info) == null || tLRPC$ChatFull.slowmode_seconds == 0 || (chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.info.id))) == null || ChatObject.hasAdminRights(chat)) {
                    return;
                }
                TLRPC$ChatFull tLRPC$ChatFull2 = this.info;
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                TLRPC$ChatFull tLRPC$ChatFull3 = this.info;
                tLRPC$ChatFull2.slowmode_next_send_date = currentTime + tLRPC$ChatFull3.slowmode_seconds;
                tLRPC$ChatFull3.flags |= 262144;
                setSlowModeTimer(tLRPC$ChatFull3.slowmode_next_send_date);
            } else if (i == NotificationCenter.sendingMessagesChanged) {
                if (this.info == null) {
                    return;
                }
                updateSlowModeText();
            } else if (i == NotificationCenter.audioRecordTooShort) {
                updateRecordInterface(4);
            } else if (i != NotificationCenter.updateBotMenuButton) {
            } else {
                long longValue = ((Long) objArr[0]).longValue();
                TLRPC$BotMenuButton tLRPC$BotMenuButton = (TLRPC$BotMenuButton) objArr[1];
                if (longValue != this.dialog_id) {
                    return;
                }
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

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i != 2 || this.pendingLocationButton == null) {
            return;
        }
        if (iArr.length > 0 && iArr[0] == 0) {
            SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
        }
        this.pendingLocationButton = null;
        this.pendingMessageObject = null;
    }

    public void checkStickresExpandHeight() {
        if (this.emojiView == null) {
            return;
        }
        android.graphics.Point point = AndroidUtilities.displaySize;
        int i = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
        int currentActionBarHeight = (((this.originalViewHeight - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
        if (this.searchingType == 2) {
            currentActionBarHeight = Math.min(currentActionBarHeight, AndroidUtilities.dp(120.0f) + i);
        }
        int i2 = this.emojiView.getLayoutParams().height;
        if (i2 == currentActionBarHeight) {
            return;
        }
        Animator animator = this.stickersExpansionAnim;
        if (animator != null) {
            animator.cancel();
            this.stickersExpansionAnim = null;
        }
        this.stickersExpandedHeight = currentActionBarHeight;
        if (i2 > currentActionBarHeight) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofInt(this, (Property<ChatActivityEnterView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - i)), ObjectAnimator.ofInt(this.emojiView, (Property<EmojiView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - i)));
            ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatActivityEnterView.this.lambda$checkStickresExpandHeight$53(valueAnimator);
                }
            });
            animatorSet.setDuration(300L);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.64
                {
                    ChatActivityEnterView.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator2) {
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
        animatorSet2.playTogether(ObjectAnimator.ofInt(this, (Property<ChatActivityEnterView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - i)), ObjectAnimator.ofInt(this.emojiView, (Property<EmojiView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - i)));
        ((ObjectAnimator) animatorSet2.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatActivityEnterView.this.lambda$checkStickresExpandHeight$54(valueAnimator);
            }
        });
        animatorSet2.setDuration(300L);
        animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.65
            {
                ChatActivityEnterView.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                ChatActivityEnterView.this.stickersExpansionAnim = null;
                ChatActivityEnterView.this.emojiView.setLayerType(0, null);
            }
        });
        this.stickersExpansionAnim = animatorSet2;
        this.emojiView.setLayerType(2, null);
        animatorSet2.start();
    }

    public /* synthetic */ void lambda$checkStickresExpandHeight$53(ValueAnimator valueAnimator) {
        this.sizeNotifierLayout.invalidate();
    }

    public /* synthetic */ void lambda$checkStickresExpandHeight$54(ValueAnimator valueAnimator) {
        this.sizeNotifierLayout.invalidate();
    }

    public void setStickersExpanded(boolean z, boolean z2, boolean z3) {
        setStickersExpanded(z, z2, z3, true);
    }

    public void setStickersExpanded(boolean z, boolean z2, boolean z3, boolean z4) {
        AdjustPanLayoutHelper adjustPanLayoutHelper = this.adjustPanLayoutHelper;
        if ((adjustPanLayoutHelper == null || !adjustPanLayoutHelper.animationInProgress()) && !this.waitingForKeyboardOpenAfterAnimation && this.emojiView != null) {
            if (!z3 && this.stickersExpanded == z) {
                return;
            }
            this.stickersExpanded = z;
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onStickersExpandedChange();
            }
            android.graphics.Point point = AndroidUtilities.displaySize;
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
                    animatorSet.playTogether(ObjectAnimator.ofInt(this, (Property<ChatActivityEnterView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - i)), ObjectAnimator.ofInt(this.emojiView, (Property<EmojiView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - i)), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", 1.0f));
                    animatorSet.setDuration(300L);
                    animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda7
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.lambda$setStickersExpanded$55(i, valueAnimator);
                        }
                    });
                    animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.66
                        {
                            ChatActivityEnterView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator2) {
                            ChatActivityEnterView.this.stickersExpansionAnim = null;
                            ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                            NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                        }
                    });
                    this.stickersExpansionAnim = animatorSet;
                    this.emojiView.setLayerType(2, null);
                    this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
                    this.stickersExpansionProgress = 0.0f;
                    this.sizeNotifierLayout.invalidate();
                    animatorSet.start();
                } else {
                    this.stickersExpansionProgress = 1.0f;
                    setTranslationY(-(this.stickersExpandedHeight - i));
                    this.emojiView.setTranslationY(-(this.stickersExpandedHeight - i));
                    this.stickersArrow.setAnimationProgress(1.0f);
                }
            } else {
                if (z4) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 1);
                }
                if (z2) {
                    this.closeAnimationInProgress = true;
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    animatorSet2.playTogether(ObjectAnimator.ofInt(this, (Property<ChatActivityEnterView, Integer>) this.roundedTranslationYProperty, 0), ObjectAnimator.ofInt(this.emojiView, (Property<EmojiView, Integer>) this.roundedTranslationYProperty, 0), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", 0.0f));
                    animatorSet2.setDuration(300L);
                    animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ((ObjectAnimator) animatorSet2.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda6
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.lambda$setStickersExpanded$56(i, valueAnimator);
                        }
                    });
                    animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.67
                        {
                            ChatActivityEnterView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator2) {
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
                            if (ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing()) {
                                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                                chatActivityEnterView.showPopup(0, chatActivityEnterView.currentPopupContentType);
                            }
                            if (ChatActivityEnterView.this.onEmojiSearchClosed != null) {
                                ChatActivityEnterView.this.onEmojiSearchClosed.run();
                                ChatActivityEnterView.this.onEmojiSearchClosed = null;
                            }
                            NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                        }
                    });
                    this.stickersExpansionProgress = 1.0f;
                    this.sizeNotifierLayout.invalidate();
                    this.stickersExpansionAnim = animatorSet2;
                    this.emojiView.setLayerType(2, null);
                    this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
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
            if (z) {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrCollapsePanel", R.string.AccDescrCollapsePanel));
            } else {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", R.string.AccDescrExpandPanel));
            }
        }
    }

    public /* synthetic */ void lambda$setStickersExpanded$55(int i, ValueAnimator valueAnimator) {
        this.stickersExpansionProgress = Math.abs(getTranslationY() / (-(this.stickersExpandedHeight - i)));
        this.sizeNotifierLayout.invalidate();
    }

    public /* synthetic */ void lambda$setStickersExpanded$56(int i, ValueAnimator valueAnimator) {
        this.stickersExpansionProgress = getTranslationY() / (-(this.stickersExpandedHeight - i));
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
        return !hasBotWebView() || !this.botCommandsMenuButton.isOpened();
    }

    public int getHeightWithTopView() {
        int measuredHeight = getMeasuredHeight();
        View view = this.topView;
        return (view == null || view.getVisibility() != 0) ? measuredHeight : (int) (measuredHeight - ((1.0f - this.topViewEnterProgress) * this.topView.getLayoutParams().height));
    }

    public void setAdjustPanLayoutHelper(AdjustPanLayoutHelper adjustPanLayoutHelper) {
        this.adjustPanLayoutHelper = adjustPanLayoutHelper;
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

    /* loaded from: classes3.dex */
    public class ScrimDrawable extends Drawable {
        private Paint paint;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public ScrimDrawable() {
            ChatActivityEnterView.this = r2;
            Paint paint = new Paint();
            this.paint = paint;
            paint.setColor(0);
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            if (ChatActivityEnterView.this.emojiView == null) {
                return;
            }
            this.paint.setAlpha(Math.round(ChatActivityEnterView.this.stickersExpansionProgress * 102.0f));
            canvas.drawRect(0.0f, 0.0f, ChatActivityEnterView.this.getWidth(), (ChatActivityEnterView.this.emojiView.getY() - ChatActivityEnterView.this.getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight(), this.paint);
        }
    }

    /* loaded from: classes3.dex */
    public class SlideTextView extends View {
        Paint arrowPaint;
        Path arrowPath;
        TextPaint bluePaint;
        float cancelAlpha;
        int cancelCharOffset;
        StaticLayout cancelLayout;
        public android.graphics.Rect cancelRect;
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
        float xOffset;

        @Override // android.view.View
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
                        this.selectableBackground.setHotspot(x, y);
                    }
                    setPressed(true);
                }
                return this.pressed;
            }
            boolean z = this.pressed;
            if (!z) {
                return z;
            }
            if (motionEvent.getAction() == 2 && !this.cancelRect.contains(x, y)) {
                setPressed(false);
                return false;
            }
            if (motionEvent.getAction() == 1 && this.cancelRect.contains(x, y)) {
                onCancelButtonPressed();
            }
            return true;
        }

        public void onCancelButtonPressed() {
            if (!ChatActivityEnterView.this.hasRecordVideo || !ChatActivityEnterView.this.isInVideoMode()) {
                ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                MediaController.getInstance().stopRecording(0, false, 0);
            } else {
                CameraController.getInstance().cancelOnInitRunnable(ChatActivityEnterView.this.onFinishInitCameraRunnable);
                ChatActivityEnterView.this.delegate.needStartRecordVideo(5, true, 0);
            }
            ChatActivityEnterView.this.recordingAudioVideo = false;
            ChatActivityEnterView.this.updateRecordInterface(2);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SlideTextView(Context context) {
            super(context);
            ChatActivityEnterView.this = r5;
            this.arrowPaint = new Paint(1);
            this.xOffset = 0.0f;
            this.arrowPath = new Path();
            this.cancelRect = new android.graphics.Rect();
            this.smallSize = AndroidUtilities.displaySize.x <= AndroidUtilities.dp(320.0f);
            TextPaint textPaint = new TextPaint(1);
            this.grayPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(this.smallSize ? 13.0f : 15.0f));
            TextPaint textPaint2 = new TextPaint(1);
            this.bluePaint = textPaint2;
            textPaint2.setTextSize(AndroidUtilities.dp(15.0f));
            this.bluePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.arrowPaint.setColor(r5.getThemedColor("chat_messagePanelIcons"));
            this.arrowPaint.setStyle(Paint.Style.STROKE);
            this.arrowPaint.setStrokeWidth(AndroidUtilities.dpf2(this.smallSize ? 1.0f : 1.6f));
            this.arrowPaint.setStrokeCap(Paint.Cap.ROUND);
            this.arrowPaint.setStrokeJoin(Paint.Join.ROUND);
            this.slideToCancelString = LocaleController.getString("SlideToCancel", R.string.SlideToCancel);
            this.slideToCancelString = this.slideToCancelString.charAt(0) + this.slideToCancelString.substring(1).toLowerCase();
            String upperCase = LocaleController.getString("Cancel", R.string.Cancel).toUpperCase();
            this.cancelString = upperCase;
            this.cancelCharOffset = this.slideToCancelString.indexOf(upperCase);
            updateColors();
        }

        public void updateColors() {
            this.grayPaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_recordTime"));
            this.bluePaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_recordVoiceCancel"));
            this.slideToAlpha = this.grayPaint.getAlpha();
            this.cancelAlpha = this.bluePaint.getAlpha();
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(60.0f), 0, ColorUtils.setAlphaComponent(ChatActivityEnterView.this.getThemedColor("chat_recordVoiceCancel"), 26));
            this.selectableBackground = createSimpleSelectorCircleDrawable;
            createSimpleSelectorCircleDrawable.setCallback(this);
        }

        @Override // android.view.View
        protected void drawableStateChanged() {
            super.drawableStateChanged();
            this.selectableBackground.setState(getDrawableState());
        }

        @Override // android.view.View
        public boolean verifyDrawable(Drawable drawable) {
            return this.selectableBackground == drawable || super.verifyDrawable(drawable);
        }

        @Override // android.view.View
        public void jumpDrawablesToCurrentState() {
            super.jumpDrawablesToCurrentState();
            Drawable drawable = this.selectableBackground;
            if (drawable != null) {
                drawable.jumpToCurrentState();
            }
        }

        @Override // android.view.View
        @SuppressLint({"DrawAllocation"})
        protected void onMeasure(int i, int i2) {
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
                    float f = measuredHeight2;
                    this.arrowPath.setLastPoint(AndroidUtilities.dpf2(2.5f), f - AndroidUtilities.dpf2(3.12f));
                    this.arrowPath.lineTo(0.0f, f);
                    this.arrowPath.lineTo(AndroidUtilities.dpf2(2.5f), f + AndroidUtilities.dpf2(3.12f));
                } else {
                    float f2 = measuredHeight2;
                    this.arrowPath.setLastPoint(AndroidUtilities.dpf2(4.0f), f2 - AndroidUtilities.dpf2(5.0f));
                    this.arrowPath.lineTo(0.0f, f2);
                    this.arrowPath.lineTo(AndroidUtilities.dpf2(4.0f), f2 + AndroidUtilities.dpf2(5.0f));
                }
                this.slideToLayout = new StaticLayout(this.slideToCancelString, this.grayPaint, (int) this.slideToCancelWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.cancelLayout = new StaticLayout(this.cancelString, this.bluePaint, (int) this.cancelWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            StaticLayout staticLayout;
            if (this.slideToLayout == null || (staticLayout = this.cancelLayout) == null) {
                return;
            }
            int width = staticLayout.getWidth() + AndroidUtilities.dp(16.0f);
            this.grayPaint.setColor(ChatActivityEnterView.this.getThemedColor("chat_recordTime"));
            this.grayPaint.setAlpha((int) (this.slideToAlpha * (1.0f - this.cancelToProgress) * this.slideProgress));
            this.bluePaint.setAlpha((int) (this.cancelAlpha * this.cancelToProgress));
            this.arrowPaint.setColor(this.grayPaint.getColor());
            boolean z = true;
            if (this.smallSize) {
                this.xOffset = AndroidUtilities.dp(16.0f);
            } else {
                long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
                this.lastUpdateTime = System.currentTimeMillis();
                if (this.cancelToProgress == 0.0f && this.slideProgress > 0.8f) {
                    if (this.moveForward) {
                        float dp = this.xOffset + ((AndroidUtilities.dp(3.0f) / 250.0f) * ((float) currentTimeMillis));
                        this.xOffset = dp;
                        if (dp > AndroidUtilities.dp(6.0f)) {
                            this.xOffset = AndroidUtilities.dp(6.0f);
                            this.moveForward = false;
                        }
                    } else {
                        float dp2 = this.xOffset - ((AndroidUtilities.dp(3.0f) / 250.0f) * ((float) currentTimeMillis));
                        this.xOffset = dp2;
                        if (dp2 < (-AndroidUtilities.dp(6.0f))) {
                            this.xOffset = -AndroidUtilities.dp(6.0f);
                            this.moveForward = true;
                        }
                    }
                }
            }
            if (this.cancelCharOffset < 0) {
                z = false;
            }
            int measuredWidth = ((int) ((getMeasuredWidth() - this.slideToCancelWidth) / 2.0f)) + AndroidUtilities.dp(5.0f);
            int measuredWidth2 = (int) ((getMeasuredWidth() - this.cancelWidth) / 2.0f);
            float primaryHorizontal = z ? this.slideToLayout.getPrimaryHorizontal(this.cancelCharOffset) : 0.0f;
            float f = z ? (measuredWidth + primaryHorizontal) - measuredWidth2 : 0.0f;
            float f2 = this.xOffset;
            float f3 = this.cancelToProgress;
            float dp3 = ((measuredWidth + ((f2 * (1.0f - f3)) * this.slideProgress)) - (f * f3)) + AndroidUtilities.dp(16.0f);
            float dp4 = z ? 0.0f : this.cancelToProgress * AndroidUtilities.dp(12.0f);
            if (this.cancelToProgress != 1.0f) {
                int i = (int) (((-getMeasuredWidth()) / 4) * (1.0f - this.slideProgress));
                canvas.save();
                canvas.clipRect(ChatActivityEnterView.this.recordTimerView.getLeftProperty() + AndroidUtilities.dp(4.0f), 0.0f, getMeasuredWidth(), getMeasuredHeight());
                canvas.save();
                int i2 = (int) dp3;
                canvas.translate((i2 - AndroidUtilities.dp(this.smallSize ? 7.0f : 10.0f)) + i, dp4);
                canvas.drawPath(this.arrowPath, this.arrowPaint);
                canvas.restore();
                canvas.save();
                canvas.translate(i2 + i, ((getMeasuredHeight() - this.slideToLayout.getHeight()) / 2.0f) + dp4);
                this.slideToLayout.draw(canvas);
                canvas.restore();
                canvas.restore();
            }
            float measuredHeight = (getMeasuredHeight() - this.cancelLayout.getHeight()) / 2.0f;
            if (!z) {
                measuredHeight -= AndroidUtilities.dp(12.0f) - dp4;
            }
            float f4 = z ? dp3 + primaryHorizontal : measuredWidth2;
            this.cancelRect.set((int) f4, (int) measuredHeight, (int) (this.cancelLayout.getWidth() + f4), (int) (this.cancelLayout.getHeight() + measuredHeight));
            this.cancelRect.inset(-AndroidUtilities.dp(16.0f), -AndroidUtilities.dp(16.0f));
            if (this.cancelToProgress > 0.0f) {
                this.selectableBackground.setBounds((getMeasuredWidth() / 2) - width, (getMeasuredHeight() / 2) - width, (getMeasuredWidth() / 2) + width, (getMeasuredHeight() / 2) + width);
                this.selectableBackground.draw(canvas);
                canvas.save();
                canvas.translate(f4, measuredHeight);
                this.cancelLayout.draw(canvas);
                canvas.restore();
            } else {
                setPressed(false);
            }
            if (this.cancelToProgress == 1.0f) {
                return;
            }
            invalidate();
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

    /* loaded from: classes3.dex */
    public class TimerView extends View {
        StaticLayout inLayout;
        boolean isRunning;
        long lastSendTypingTime;
        float left;
        String oldString;
        StaticLayout outLayout;
        final float replaceDistance;
        SpannableStringBuilder replaceIn;
        SpannableStringBuilder replaceOut;
        SpannableStringBuilder replaceStable;
        float replaceTransition;
        long startTime;
        long stopTime;
        boolean stoppedInternal;
        final TextPaint textPaint;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TimerView(Context context) {
            super(context);
            ChatActivityEnterView.this = r2;
            this.replaceIn = new SpannableStringBuilder();
            this.replaceOut = new SpannableStringBuilder();
            this.replaceStable = new SpannableStringBuilder();
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            this.replaceDistance = AndroidUtilities.dp(15.0f);
            textPaint.setTextSize(AndroidUtilities.dp(15.0f));
            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
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
            this.lastSendTypingTime = 0L;
        }

        @Override // android.view.View
        @SuppressLint({"DrawAllocation"})
        protected void onDraw(Canvas canvas) {
            String format;
            SpannableStringBuilder spannableStringBuilder;
            String str;
            long currentTimeMillis = System.currentTimeMillis();
            long j = this.isRunning ? currentTimeMillis - this.startTime : this.stopTime - this.startTime;
            long j2 = j / 1000;
            int i = ((int) (j % 1000)) / 10;
            if (ChatActivityEnterView.this.isInVideoMode() && j >= 59500 && !this.stoppedInternal) {
                ChatActivityEnterView.this.startedDraggingX = -1.0f;
                ChatActivityEnterView.this.delegate.needStartRecordVideo(3, true, 0);
                this.stoppedInternal = true;
            }
            if (this.isRunning && currentTimeMillis > this.lastSendTypingTime + 5000) {
                this.lastSendTypingTime = currentTimeMillis;
                MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).sendTyping(ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.getThreadMessageId(), ChatActivityEnterView.this.isInVideoMode() ? 7 : 1, 0);
            }
            long j3 = j2 / 60;
            if (j3 >= 60) {
                format = String.format(Locale.US, "%01d:%02d:%02d,%d", Long.valueOf(j3 / 60), Long.valueOf(j3 % 60), Long.valueOf(j2 % 60), Integer.valueOf(i / 10));
            } else {
                format = String.format(Locale.US, "%01d:%02d,%d", Long.valueOf(j3), Long.valueOf(j2 % 60), Integer.valueOf(i / 10));
            }
            if (format.length() >= 3 && (str = this.oldString) != null && str.length() >= 3 && format.length() == this.oldString.length() && format.charAt(format.length() - 3) != this.oldString.charAt(format.length() - 3)) {
                int length = format.length();
                this.replaceIn.clear();
                this.replaceOut.clear();
                this.replaceStable.clear();
                this.replaceIn.append((CharSequence) format);
                this.replaceOut.append((CharSequence) this.oldString);
                this.replaceStable.append((CharSequence) format);
                int i2 = -1;
                int i3 = -1;
                int i4 = 0;
                int i5 = 0;
                for (int i6 = 0; i6 < length - 1; i6++) {
                    if (this.oldString.charAt(i6) != format.charAt(i6)) {
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
            } else {
                if (this.replaceStable == null) {
                    this.replaceStable = new SpannableStringBuilder(format);
                }
                if (this.replaceStable.length() == 0 || this.replaceStable.length() != format.length()) {
                    this.replaceStable.clear();
                    this.replaceStable.append((CharSequence) format);
                } else {
                    this.replaceStable.replace(spannableStringBuilder.length() - 1, this.replaceStable.length(), (CharSequence) format, (format.length() - 1) - (format.length() - this.replaceStable.length()), format.length());
                }
            }
            float f = this.replaceTransition;
            if (f != 0.0f) {
                float f2 = f - 0.15f;
                this.replaceTransition = f2;
                if (f2 < 0.0f) {
                    this.replaceTransition = 0.0f;
                }
            }
            float measuredHeight = getMeasuredHeight() / 2;
            if (this.replaceTransition == 0.0f) {
                this.replaceStable.clearSpans();
                StaticLayout staticLayout = new StaticLayout(this.replaceStable, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                canvas.save();
                canvas.translate(0.0f, measuredHeight - (staticLayout.getHeight() / 2.0f));
                staticLayout.draw(canvas);
                canvas.restore();
                this.left = staticLayout.getLineWidth(0) + 0.0f;
            } else {
                if (this.inLayout != null) {
                    canvas.save();
                    this.textPaint.setAlpha((int) ((1.0f - this.replaceTransition) * 255.0f));
                    canvas.translate(0.0f, (measuredHeight - (this.inLayout.getHeight() / 2.0f)) - (this.replaceDistance * this.replaceTransition));
                    this.inLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.outLayout != null) {
                    canvas.save();
                    this.textPaint.setAlpha((int) (this.replaceTransition * 255.0f));
                    canvas.translate(0.0f, (measuredHeight - (this.outLayout.getHeight() / 2.0f)) + (this.replaceDistance * (1.0f - this.replaceTransition)));
                    this.outLayout.draw(canvas);
                    canvas.restore();
                }
                canvas.save();
                this.textPaint.setAlpha(255);
                StaticLayout staticLayout2 = new StaticLayout(this.replaceStable, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                canvas.translate(0.0f, measuredHeight - (staticLayout2.getHeight() / 2.0f));
                staticLayout2.draw(canvas);
                canvas.restore();
                this.left = staticLayout2.getLineWidth(0) + 0.0f;
            }
            this.oldString = format;
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
            this.startTime = 0L;
            this.stopTime = 0L;
            this.stoppedInternal = false;
        }
    }

    public RecordCircle getRecordCicle() {
        return this.recordCircle;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int max;
        LinearLayoutManager linearLayoutManager;
        int findFirstVisibleItemPosition;
        View findViewByPosition;
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        if (botCommandsMenuView != null && botCommandsMenuView.getTag() != null) {
            this.botCommandsMenuButton.measure(i, i2);
            ((ViewGroup.MarginLayoutParams) this.emojiButton.getLayoutParams()).leftMargin = AndroidUtilities.dp(10.0f) + this.botCommandsMenuButton.getMeasuredWidth();
            ((ViewGroup.MarginLayoutParams) this.messageEditText.getLayoutParams()).leftMargin = AndroidUtilities.dp(57.0f) + this.botCommandsMenuButton.getMeasuredWidth();
        } else {
            SenderSelectView senderSelectView = this.senderSelectView;
            if (senderSelectView != null && senderSelectView.getVisibility() == 0) {
                int i3 = this.senderSelectView.getLayoutParams().width;
                this.senderSelectView.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(this.senderSelectView.getLayoutParams().height, NUM));
                ((ViewGroup.MarginLayoutParams) this.emojiButton.getLayoutParams()).leftMargin = AndroidUtilities.dp(16.0f) + i3;
                ((ViewGroup.MarginLayoutParams) this.messageEditText.getLayoutParams()).leftMargin = AndroidUtilities.dp(63.0f) + i3;
            } else {
                ((ViewGroup.MarginLayoutParams) this.emojiButton.getLayoutParams()).leftMargin = AndroidUtilities.dp(3.0f);
                ((ViewGroup.MarginLayoutParams) this.messageEditText.getLayoutParams()).leftMargin = AndroidUtilities.dp(50.0f);
            }
        }
        if (this.botCommandsMenuContainer != null) {
            if (this.botCommandsAdapter.getItemCount() > 4) {
                max = Math.max(0, this.sizeNotifierLayout.getMeasuredHeight() - AndroidUtilities.dp(162.8f));
            } else {
                max = Math.max(0, this.sizeNotifierLayout.getMeasuredHeight() - AndroidUtilities.dp((Math.max(1, Math.min(4, this.botCommandsAdapter.getItemCount())) * 36) + 8));
            }
            if (this.botCommandsMenuContainer.listView.getPaddingTop() != max) {
                this.botCommandsMenuContainer.listView.setTopGlowOffset(max);
                if (this.botCommandLastPosition == -1 && this.botCommandsMenuContainer.getVisibility() == 0 && this.botCommandsMenuContainer.listView.getLayoutManager() != null && (findFirstVisibleItemPosition = (linearLayoutManager = (LinearLayoutManager) this.botCommandsMenuContainer.listView.getLayoutManager()).findFirstVisibleItemPosition()) >= 0 && (findViewByPosition = linearLayoutManager.findViewByPosition(findFirstVisibleItemPosition)) != null) {
                    this.botCommandLastPosition = findFirstVisibleItemPosition;
                    this.botCommandLastTop = findViewByPosition.getTop() - this.botCommandsMenuContainer.listView.getPaddingTop();
                }
                this.botCommandsMenuContainer.listView.setPadding(0, max, 0, AndroidUtilities.dp(8.0f));
            }
        }
        super.onMeasure(i, i2);
        ChatActivityBotWebViewButton chatActivityBotWebViewButton = this.botWebViewButton;
        if (chatActivityBotWebViewButton != null) {
            BotCommandsMenuView botCommandsMenuView2 = this.botCommandsMenuButton;
            if (botCommandsMenuView2 != null) {
                chatActivityBotWebViewButton.setMeasuredButtonWidth(botCommandsMenuView2.getMeasuredWidth());
            }
            this.botWebViewButton.getLayoutParams().height = getMeasuredHeight() - AndroidUtilities.dp(2.0f);
            measureChild(this.botWebViewButton, i, i2);
        }
        BotWebViewMenuContainer botWebViewMenuContainer = this.botWebViewMenuContainer;
        if (botWebViewMenuContainer != null) {
            ((ViewGroup.MarginLayoutParams) botWebViewMenuContainer.getLayoutParams()).bottomMargin = this.messageEditText.getMeasuredHeight();
            measureChild(this.botWebViewMenuContainer, i, i2);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
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
        BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter = this.botCommandsAdapter;
        if (botCommandsAdapter != null) {
            botCommandsAdapter.setBotInfo(longSparseArray);
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
        return this.topView.getLayoutParams().height;
    }

    public void runEmojiPanelAnimation() {
        AndroidUtilities.cancelRunOnUIThread(this.runEmojiPanelAnimation);
        this.runEmojiPanelAnimation.run();
    }

    public Drawable getStickersArrowDrawable() {
        return this.stickersArrow;
    }

    @Override // org.telegram.ui.Components.BlurredFrameLayout, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        EmojiView emojiView = this.emojiView;
        if (emojiView == null || emojiView.getVisibility() != 0 || this.emojiView.getStickersExpandOffset() == 0.0f) {
            super.dispatchDraw(canvas);
            return;
        }
        canvas.save();
        canvas.clipRect(0, AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight());
        canvas.translate(0.0f, -this.emojiView.getStickersExpandOffset());
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    public void setChatSearchExpandOffset(float f) {
        this.chatSearchExpandOffset = f;
        invalidate();
    }
}
