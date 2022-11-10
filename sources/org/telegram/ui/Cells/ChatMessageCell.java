package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Property;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.video.VideoPlayerRewinder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatInvite;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageFwdHeader;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$MessagePeerReaction;
import org.telegram.tgnet.TLRPC$MessageReplies;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$Poll;
import org.telegram.tgnet.TLRPC$Reaction;
import org.telegram.tgnet.TLRPC$ReactionCount;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_emojiStatusUntil;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonWebView;
import org.telegram.tgnet.TLRPC$TL_messageExtendedMediaPreview;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC$TL_reactionEmoji;
import org.telegram.tgnet.TLRPC$TL_user;
import org.telegram.tgnet.TLRPC$TL_webPage;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedNumberLayout;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.InfiniteProgress;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.MessageBackgroundDrawable;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.MsgClockDrawable;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RoundVideoPlayingDrawable;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.SlotsDrawable;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.Components.TranscribeButton;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PinchToZoomHelper;
/* loaded from: classes3.dex */
public class ChatMessageCell extends BaseCell implements SeekBar.SeekBarDelegate, ImageReceiver.ImageReceiverDelegate, DownloadController.FileDownloadProgressListener, TextSelectionHelper.SelectableView, NotificationCenter.NotificationCenterDelegate {
    private static float[] radii = new float[8];
    private final boolean ALPHA_PROPERTY_WORKAROUND;
    public Property<ChatMessageCell, Float> ANIMATION_OFFSET_X;
    private int TAG;
    CharSequence accessibilityText;
    private SparseArray<Rect> accessibilityVirtualViewBounds;
    private int addedCaptionHeight;
    private boolean addedForTest;
    private int additionalTimeOffsetY;
    private StaticLayout adminLayout;
    private boolean allowAssistant;
    private float alphaInternal;
    private int animateFromStatusDrawableParams;
    private boolean animatePollAnswer;
    private boolean animatePollAnswerAlpha;
    private boolean animatePollAvatars;
    private int animateToStatusDrawableParams;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiDescriptionStack;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiReplyStack;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiStack;
    private int animatingDrawVideoImageButton;
    private float animatingDrawVideoImageButtonProgress;
    private float animatingLoadingProgressProgress;
    private int animatingNoSound;
    private boolean animatingNoSoundPlaying;
    private float animatingNoSoundProgress;
    private float animationOffsetX;
    private boolean animationRunning;
    private boolean attachedToWindow;
    private StaticLayout authorLayout;
    private int authorX;
    private boolean autoPlayingMedia;
    private int availableTimeWidth;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private boolean avatarPressed;
    private Theme.MessageDrawable.PathDrawParams backgroundCacheParams;
    private MessageBackgroundDrawable backgroundDrawable;
    private int backgroundDrawableBottom;
    private int backgroundDrawableLeft;
    private int backgroundDrawableRight;
    private int backgroundDrawableTop;
    private int backgroundHeight;
    private int backgroundWidth;
    private int blurredViewBottomOffset;
    private int blurredViewTopOffset;
    private ArrayList<BotButton> botButtons;
    private HashMap<String, BotButton> botButtonsByData;
    private HashMap<String, BotButton> botButtonsByPosition;
    private String botButtonsLayout;
    private boolean bottomNearToSet;
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private final boolean canDrawBackgroundInParent;
    private boolean canStreamVideo;
    private int captionHeight;
    private StaticLayout captionLayout;
    private int captionOffsetX;
    private AtomicReference<Layout> captionPatchedSpoilersLayout;
    private List<SpoilerEffect> captionSpoilers;
    private Stack<SpoilerEffect> captionSpoilersPool;
    private int captionWidth;
    private float captionX;
    private float captionY;
    private CheckBoxBase checkBox;
    private boolean checkBoxAnimationInProgress;
    private float checkBoxAnimationProgress;
    private int checkBoxTranslation;
    private boolean checkBoxVisible;
    private boolean checkOnlyButtonPressed;
    private String closeTimeText;
    private int closeTimeWidth;
    private int commentArrowX;
    private AvatarDrawable[] commentAvatarDrawables;
    private ImageReceiver[] commentAvatarImages;
    private boolean[] commentAvatarImagesVisible;
    private boolean commentButtonPressed;
    private Rect commentButtonRect;
    private boolean commentDrawUnread;
    private StaticLayout commentLayout;
    private AnimatedNumberLayout commentNumberLayout;
    private int commentNumberWidth;
    private InfiniteProgress commentProgress;
    private float commentProgressAlpha;
    private long commentProgressLastUpadteTime;
    private int commentUnreadX;
    private int commentWidth;
    private int commentX;
    private AvatarDrawable contactAvatarDrawable;
    private float controlsAlpha;
    private int currentAccount;
    private Theme.MessageDrawable currentBackgroundDrawable;
    private Theme.MessageDrawable currentBackgroundSelectedDrawable;
    private CharSequence currentCaption;
    private TLRPC$Chat currentChat;
    private int currentFocusedVirtualView;
    private TLRPC$Chat currentForwardChannel;
    private String currentForwardName;
    private String currentForwardNameString;
    private TLRPC$User currentForwardUser;
    private int currentMapProvider;
    private MessageObject currentMessageObject;
    private MessageObject.GroupedMessages currentMessagesGroup;
    private Object currentNameStatus;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable currentNameStatusDrawable;
    private String currentNameString;
    private TLRPC$FileLocation currentPhoto;
    private String currentPhotoFilter;
    private String currentPhotoFilterThumb;
    private TLRPC$PhotoSize currentPhotoObject;
    private TLRPC$PhotoSize currentPhotoObjectThumb;
    private BitmapDrawable currentPhotoObjectThumbStripped;
    private MessageObject.GroupedMessagePosition currentPosition;
    private String currentRepliesString;
    private TLRPC$PhotoSize currentReplyPhoto;
    private long currentReplyUserId;
    private float currentSelectedBackgroundAlpha;
    private String currentTimeString;
    private String currentUnlockString;
    private String currentUrl;
    private TLRPC$User currentUser;
    private TLRPC$User currentViaBotUser;
    private String currentViewsString;
    private WebFile currentWebFile;
    private ChatMessageCellDelegate delegate;
    private RectF deleteProgressRect;
    private StaticLayout descriptionLayout;
    private int descriptionX;
    private int descriptionY;
    private Runnable diceFinishCallback;
    private boolean disallowLongPress;
    private StaticLayout docTitleLayout;
    private int docTitleOffsetX;
    private int docTitleWidth;
    private TLRPC$Document documentAttach;
    private int documentAttachType;
    private boolean drawBackground;
    private boolean drawCommentButton;
    private boolean drawCommentNumber;
    public boolean drawForBlur;
    private boolean drawForwardedName;
    public boolean drawFromPinchToZoom;
    private boolean drawImageButton;
    private boolean drawInstantView;
    private int drawInstantViewType;
    private boolean drawMediaCheckBox;
    private boolean drawName;
    private boolean drawNameLayout;
    private boolean drawPhotoImage;
    public boolean drawPinnedBottom;
    private boolean drawPinnedTop;
    private boolean drawRadialCheckBackground;
    private boolean drawSelectionBackground;
    private int drawSideButton;
    private boolean drawTime;
    private float drawTimeX;
    private float drawTimeY;
    private boolean drawTopic;
    private boolean drawVideoImageButton;
    private boolean drawVideoSize;
    private Paint drillHolePaint;
    private Path drillHolePath;
    private StaticLayout durationLayout;
    private int durationWidth;
    private boolean edited;
    boolean enterTransitionInProgress;
    private boolean firstCircleLength;
    private int firstVisibleBlockNum;
    private boolean flipImage;
    private boolean forceNotDrawTime;
    private boolean forwardBotPressed;
    private int forwardHeight;
    private int forwardNameCenterX;
    private float[] forwardNameOffsetX;
    private boolean forwardNamePressed;
    private float forwardNameX;
    private int forwardNameY;
    private StaticLayout[] forwardedNameLayout;
    private int forwardedNameWidth;
    private boolean fullyDraw;
    private boolean gamePreviewPressed;
    private LinearGradient gradientShader;
    private boolean groupPhotoInvisible;
    private MessageObject.GroupedMessages groupedMessagesToSet;
    private boolean hadLongPress;
    public boolean hasDiscussion;
    private boolean hasEmbed;
    private boolean hasGamePreview;
    private boolean hasInvoicePreview;
    private boolean hasLinkPreview;
    private int hasMiniProgress;
    private boolean hasNewLineForTime;
    private boolean hasOldCaptionPreview;
    private boolean hasPsaHint;
    private int highlightProgress;
    private float hintButtonProgress;
    private boolean hintButtonVisible;
    private int imageBackgroundColor;
    private int imageBackgroundGradientColor1;
    private int imageBackgroundGradientColor2;
    private int imageBackgroundGradientColor3;
    private int imageBackgroundGradientRotation;
    private float imageBackgroundIntensity;
    private int imageBackgroundSideColor;
    private int imageBackgroundSideWidth;
    private boolean imageDrawn;
    private boolean imagePressed;
    boolean imageReceiversAttachState;
    private boolean inLayout;
    private StaticLayout infoLayout;
    private int infoWidth;
    private int infoX;
    private boolean instantButtonPressed;
    private RectF instantButtonRect;
    private boolean instantPressed;
    private int instantTextLeftX;
    private boolean instantTextNewLine;
    private int instantTextX;
    private StaticLayout instantViewLayout;
    private int instantWidth;
    private Runnable invalidateRunnable;
    private boolean invalidateSpoilersParent;
    private boolean invalidatesParent;
    private boolean isAvatarVisible;
    public boolean isBlurred;
    public boolean isBot;
    private boolean isCaptionSpoilerPressed;
    public boolean isChat;
    private boolean isCheckPressed;
    private boolean isHighlighted;
    private boolean isHighlightedAnimated;
    public boolean isMegagroup;
    public boolean isPinned;
    public boolean isPinnedChat;
    private boolean isPlayingRound;
    private boolean isPressed;
    public boolean isRepliesChat;
    private boolean isRoundVideo;
    private boolean isSmallImage;
    private boolean isSpoilerRevealing;
    public boolean isThreadChat;
    private boolean isThreadPost;
    private boolean isUpdating;
    private int keyboardHeight;
    private long lastAnimationTime;
    private long lastCheckBoxAnimationTime;
    private long lastControlsAlphaChangeTime;
    private int lastDeleteDate;
    private float lastDrawingAudioProgress;
    private int lastHeight;
    private long lastHighlightProgressTime;
    private long lastLoadingSizeTotal;
    private long lastNamesAnimationTime;
    private TLRPC$Poll lastPoll;
    private long lastPollCloseTime;
    private ArrayList<TLRPC$TL_pollAnswerVoters> lastPollResults;
    private int lastPollResultsVoters;
    private String lastPostAuthor;
    private TLRPC$TL_messageReactions lastReactions;
    private int lastRepliesCount;
    private TLRPC$Message lastReplyMessage;
    private long lastSeekUpdateTime;
    private int lastSendState;
    int lastSize;
    private int lastTime;
    private float lastTouchX;
    private float lastTouchY;
    private int lastViewsCount;
    private int lastVisibleBlockNum;
    private int lastWidth;
    private int layoutHeight;
    private int layoutWidth;
    private int linkBlockNum;
    private int linkPreviewHeight;
    private boolean linkPreviewPressed;
    private int linkSelectionBlockNum;
    public long linkedChatId;
    private LinkSpanDrawable.LinkCollector links;
    private StaticLayout loadingProgressLayout;
    private boolean locationExpired;
    private ImageReceiver locationImageReceiver;
    private Drawable locationLoadingThumb;
    private boolean mediaBackground;
    private CheckBoxBase mediaCheckBox;
    private int mediaOffsetY;
    private boolean mediaWasInvisible;
    private MessageObject messageObjectToSet;
    private int miniButtonPressed;
    private int miniButtonState;
    private MotionBackgroundDrawable motionBackgroundDrawable;
    private StaticLayout nameLayout;
    private int nameLayoutWidth;
    private float nameOffsetX;
    private int nameWidth;
    private float nameX;
    private float nameY;
    private int namesOffset;
    private boolean needNewVisiblePart;
    public boolean needReplyImage;
    private int noSoundCenterX;
    private boolean otherPressed;
    private int otherX;
    private int otherY;
    private int overideShouldDrawTimeOnMedia;
    int parentBoundsBottom;
    float parentBoundsTop;
    private int parentHeight;
    public float parentViewTopOffset;
    private int parentWidth;
    private StaticLayout performerLayout;
    private int performerX;
    private ImageReceiver photoImage;
    private boolean photoImageOutOfBounds;
    private boolean photoNotSet;
    private TLObject photoParentObject;
    private StaticLayout photosCountLayout;
    private int photosCountWidth;
    public boolean pinnedBottom;
    public boolean pinnedTop;
    private float pollAnimationProgress;
    private float pollAnimationProgressTime;
    private AvatarDrawable[] pollAvatarDrawables;
    private ImageReceiver[] pollAvatarImages;
    private boolean[] pollAvatarImagesVisible;
    private ArrayList<PollButton> pollButtons;
    private CheckBoxBase[] pollCheckBox;
    private boolean pollClosed;
    private boolean pollHintPressed;
    private int pollHintX;
    private int pollHintY;
    private boolean pollUnvoteInProgress;
    private boolean pollVoteInProgress;
    private int pollVoteInProgressNum;
    private boolean pollVoted;
    private int pressedBotButton;
    private AnimatedEmojiSpan pressedEmoji;
    private LinkSpanDrawable pressedLink;
    private int pressedLinkType;
    private int[] pressedState;
    private int pressedVoteButton;
    private float psaButtonProgress;
    private boolean psaButtonVisible;
    private int psaHelpX;
    private int psaHelpY;
    private boolean psaHintPressed;
    private RadialProgress2 radialProgress;
    public final ReactionsLayoutInBubble reactionsLayoutInBubble;
    private RectF rect;
    private Path rectPath;
    private StaticLayout repliesLayout;
    private int repliesTextWidth;
    public float replyHeight;
    public ImageReceiver replyImageReceiver;
    public StaticLayout replyNameLayout;
    private int replyNameOffset;
    private int replyNameWidth;
    private boolean replyPanelIsForward;
    private boolean replyPressed;
    public Drawable replySelector;
    private boolean replySelectorCanBePressed;
    public int replySelectorColor;
    private boolean replySelectorPressed;
    public int replySelectorRadLeft;
    public int replySelectorRadRight;
    public Rect replySelectorRect;
    public List<SpoilerEffect> replySpoilers;
    private Stack<SpoilerEffect> replySpoilersPool;
    public int replyStartX;
    public int replyStartY;
    public StaticLayout replyTextLayout;
    private int replyTextOffset;
    private int replyTextWidth;
    private float replyTouchX;
    private float replyTouchY;
    private final Theme.ResourcesProvider resourcesProvider;
    private float roundPlayingDrawableProgress;
    private float roundProgressAlpha;
    float roundSeekbarOutAlpha;
    float roundSeekbarOutProgress;
    int roundSeekbarTouched;
    private float roundToPauseProgress;
    private float roundToPauseProgress2;
    private AnimatedFloat roundVideoPlayPipFloat;
    private RoundVideoPlayingDrawable roundVideoPlayingDrawable;
    private Path sPath;
    private boolean scheduledInvalidate;
    private Rect scrollRect;
    private SeekBar seekBar;
    private SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;
    private SeekBarWaveform seekBarWaveform;
    private int seekBarWaveformTranslateX;
    private int seekBarX;
    private int seekBarY;
    float seekbarRoundX;
    float seekbarRoundY;
    private float selectedBackgroundProgress;
    private Paint selectionOverlayPaint;
    private Drawable[] selectorDrawable;
    private int[] selectorDrawableMaskType;
    private AnimatorSet shakeAnimation;
    public boolean shouldCheckVisibleOnScreen;
    private boolean sideButtonPressed;
    private float sideStartX;
    private float sideStartY;
    private StaticLayout siteNameLayout;
    private boolean siteNameRtl;
    private int siteNameWidth;
    private float slidingOffsetX;
    private StaticLayout songLayout;
    private int songX;
    private SpoilerEffect spoilerPressed;
    private AtomicReference<Layout> spoilersPatchedReplyTextLayout;
    private boolean statusDrawableAnimationInProgress;
    private ValueAnimator statusDrawableAnimator;
    private float statusDrawableProgress;
    private int substractBackgroundHeight;
    private int textX;
    private int textY;
    private float timeAlpha;
    private int timeAudioX;
    private StaticLayout timeLayout;
    private boolean timePressed;
    private int timeTextWidth;
    private boolean timeWasInvisible;
    private int timeWidth;
    private int timeWidthAudio;
    private int timeX;
    private TimerParticles timerParticles;
    private float timerTransitionProgress;
    private StaticLayout titleLayout;
    private int titleX;
    private float toSeekBarProgress;
    private boolean topNearToSet;
    private int topicArrowColor;
    private Drawable topicArrowDrawable;
    private int topicBackgroundColor;
    private AnimatedColor topicBackgroundColorAnimated;
    private float[] topicHSV;
    private int topicHeight;
    private RectF topicHitRect;
    private Drawable topicIconDrawable;
    private Rect topicIconDrawableBounds;
    private boolean topicIconWaiting;
    private int topicNameColor;
    private AnimatedColor topicNameColorAnimated;
    private StaticLayout topicNameLayout;
    private Paint topicPaint;
    private Path topicPath;
    private boolean topicPressed;
    private Drawable topicSelectorDrawable;
    private int topicWidth;
    private long totalChangeTime;
    private int totalCommentWidth;
    private int totalHeight;
    private int totalVisibleBlocksCount;
    private TranscribeButton transcribeButton;
    private float transcribeX;
    private float transcribeY;
    private final TransitionParams transitionParams;
    float transitionYOffsetForDrawables;
    private float unlockAlpha;
    private StaticLayout unlockLayout;
    private SpoilerEffect unlockSpoilerEffect;
    private Path unlockSpoilerPath;
    private float[] unlockSpoilerRadii;
    private int unlockTextWidth;
    private float unlockX;
    private float unlockY;
    private int unmovedTextX;
    private Runnable unregisterFlagSecure;
    private ArrayList<LinkPath> urlPathCache;
    private ArrayList<LinkPath> urlPathSelection;
    private boolean useSeekBarWaveform;
    private boolean useTranscribeButton;
    private int viaNameWidth;
    private TypefaceSpan viaSpan1;
    private TypefaceSpan viaSpan2;
    private int viaWidth;
    private boolean vibrateOnPollVote;
    private int videoButtonPressed;
    private int videoButtonX;
    private int videoButtonY;
    VideoForwardDrawable videoForwardDrawable;
    private StaticLayout videoInfoLayout;
    VideoPlayerRewinder videoPlayerRewinder;
    private RadialProgress2 videoRadialProgress;
    private float viewTop;
    private StaticLayout viewsLayout;
    private int viewsTextWidth;
    private boolean visibleOnScreen;
    private float voteCurrentCircleLength;
    private float voteCurrentProgressTime;
    private long voteLastUpdateTime;
    private float voteRadOffset;
    private boolean voteRisingCircleLength;
    private boolean wasLayout;
    private boolean wasPinned;
    private boolean wasSending;
    private boolean wasTranscriptionOpen;
    private int widthBeforeNewTimeLine;
    private int widthForButtons;
    private boolean willRemoved;
    private boolean wouldBeInPip;

    /* loaded from: classes3.dex */
    public interface ChatMessageCellDelegate {

        /* renamed from: org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static boolean $default$canDrawOutboundsContent(ChatMessageCellDelegate chatMessageCellDelegate) {
                return true;
            }

            public static boolean $default$canPerformActions(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$didLongPress(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didLongPressBotButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
            }

            public static boolean $default$didLongPressChannelAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
                return false;
            }

            public static boolean $default$didLongPressUserAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
                return false;
            }

            public static boolean $default$didPressAnimatedEmoji(ChatMessageCellDelegate chatMessageCellDelegate, AnimatedEmojiSpan animatedEmojiSpan) {
                return false;
            }

            public static void $default$didPressBotButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
            }

            public static void $default$didPressCancelSendButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressChannelAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
            }

            public static void $default$didPressCommentButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressExtendedMediaPreview(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
            }

            public static void $default$didPressHiddenForward(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressHint(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressImage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressInstantButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressOther(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressReaction(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$ReactionCount tLRPC$ReactionCount, boolean z) {
            }

            public static void $default$didPressReplyMessage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressSideButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressTime(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressTopicButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressUrl(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
            }

            public static void $default$didPressUserAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
            }

            public static void $default$didPressViaBot(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, String str) {
            }

            public static void $default$didPressViaBotNotInline(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, long j) {
            }

            public static void $default$didPressVoteButtons(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
            }

            public static void $default$didStartVideoStream(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
            }

            public static String $default$getAdminRank(ChatMessageCellDelegate chatMessageCellDelegate, long j) {
                return null;
            }

            public static PinchToZoomHelper $default$getPinchToZoomHelper(ChatMessageCellDelegate chatMessageCellDelegate) {
                return null;
            }

            public static TextSelectionHelper.ChatListTextSelectionHelper $default$getTextSelectionHelper(ChatMessageCellDelegate chatMessageCellDelegate) {
                return null;
            }

            public static boolean $default$hasSelectedMessages(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$invalidateBlur(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static boolean $default$isLandscape(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static boolean $default$keyboardIsOpened(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$needOpenWebView(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2) {
            }

            public static boolean $default$needPlayMessage(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject, boolean z) {
                return false;
            }

            public static void $default$needReloadPolls(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static void $default$needShowPremiumBulletin(ChatMessageCellDelegate chatMessageCellDelegate, int i) {
            }

            public static boolean $default$onAccessibilityAction(ChatMessageCellDelegate chatMessageCellDelegate, int i, Bundle bundle) {
                return false;
            }

            public static void $default$onDiceFinished(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static void $default$setShouldNotRepeatSticker(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
            }

            public static boolean $default$shouldDrawThreadProgress(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
                return false;
            }

            public static boolean $default$shouldRepeatSticker(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
                return true;
            }

            public static void $default$videoTimerReached(ChatMessageCellDelegate chatMessageCellDelegate) {
            }
        }

        boolean canDrawOutboundsContent();

        boolean canPerformActions();

        void didLongPress(ChatMessageCell chatMessageCell, float f, float f2);

        void didLongPressBotButton(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton);

        boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2);

        boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2);

        boolean didPressAnimatedEmoji(AnimatedEmojiSpan animatedEmojiSpan);

        void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton);

        void didPressCancelSendButton(ChatMessageCell chatMessageCell);

        void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2);

        void didPressCommentButton(ChatMessageCell chatMessageCell);

        void didPressExtendedMediaPreview(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton);

        void didPressHiddenForward(ChatMessageCell chatMessageCell);

        void didPressHint(ChatMessageCell chatMessageCell, int i);

        void didPressImage(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressInstantButton(ChatMessageCell chatMessageCell, int i);

        void didPressOther(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressReaction(ChatMessageCell chatMessageCell, TLRPC$ReactionCount tLRPC$ReactionCount, boolean z);

        void didPressReplyMessage(ChatMessageCell chatMessageCell, int i);

        void didPressSideButton(ChatMessageCell chatMessageCell);

        void didPressTime(ChatMessageCell chatMessageCell);

        void didPressTopicButton(ChatMessageCell chatMessageCell);

        void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z);

        void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2);

        void didPressViaBot(ChatMessageCell chatMessageCell, String str);

        void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j);

        void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList<TLRPC$TL_pollAnswer> arrayList, int i, int i2, int i3);

        void didStartVideoStream(MessageObject messageObject);

        String getAdminRank(long j);

        PinchToZoomHelper getPinchToZoomHelper();

        TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper();

        boolean hasSelectedMessages();

        void invalidateBlur();

        boolean isLandscape();

        boolean keyboardIsOpened();

        void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2);

        boolean needPlayMessage(MessageObject messageObject, boolean z);

        void needReloadPolls();

        void needShowPremiumBulletin(int i);

        boolean onAccessibilityAction(int i, Bundle bundle);

        void onDiceFinished();

        void setShouldNotRepeatSticker(MessageObject messageObject);

        boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell);

        boolean shouldRepeatSticker(MessageObject messageObject);

        void videoTimerReached();
    }

    private boolean intersect(float f, float f2, float f3, float f4) {
        return f <= f3 ? f2 >= f3 : f <= f4;
    }

    public RadialProgress2 getRadialProgress() {
        return this.radialProgress;
    }

    public void setEnterTransitionInProgress(boolean z) {
        this.enterTransitionInProgress = z;
        invalidate();
    }

    public ReactionsLayoutInBubble.ReactionButton getReactionButton(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        return this.reactionsLayoutInBubble.getReactionButton(visibleReaction);
    }

    public MessageObject getPrimaryMessageObject() {
        MessageObject messageObject = this.currentMessageObject;
        MessageObject findPrimaryMessageObject = (messageObject == null || this.currentMessagesGroup == null || !messageObject.hasValidGroupId()) ? null : this.currentMessagesGroup.findPrimaryMessageObject();
        return findPrimaryMessageObject != null ? findPrimaryMessageObject : this.currentMessageObject;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$User tLRPC$User;
        if (i == NotificationCenter.startSpoilers) {
            setSpoilersSuppressed(false);
        } else if (i == NotificationCenter.stopSpoilers) {
            setSpoilersSuppressed(true);
        } else if (i != NotificationCenter.userInfoDidLoad || (tLRPC$User = this.currentUser) == null || tLRPC$User.id != ((Long) objArr[0]).longValue()) {
        } else {
            setAvatar(this.currentMessageObject);
        }
    }

    private void setAvatar(MessageObject messageObject) {
        TLRPC$Chat tLRPC$Chat;
        if (messageObject == null) {
            return;
        }
        if (this.isAvatarVisible) {
            Drawable drawable = messageObject.customAvatarDrawable;
            if (drawable != null) {
                this.avatarImage.setImageBitmap(drawable);
                return;
            }
            TLRPC$User tLRPC$User = this.currentUser;
            if (tLRPC$User != null) {
                TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$User.photo;
                if (tLRPC$UserProfilePhoto != null) {
                    this.currentPhoto = tLRPC$UserProfilePhoto.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(tLRPC$User);
                this.avatarImage.setForUserOrChat(this.currentUser, this.avatarDrawable, null, true);
                return;
            }
            TLRPC$Chat tLRPC$Chat2 = this.currentChat;
            if (tLRPC$Chat2 != null) {
                TLRPC$ChatPhoto tLRPC$ChatPhoto = tLRPC$Chat2.photo;
                if (tLRPC$ChatPhoto != null) {
                    this.currentPhoto = tLRPC$ChatPhoto.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(tLRPC$Chat2);
                this.avatarImage.setForUserOrChat(this.currentChat, this.avatarDrawable);
                return;
            } else if (messageObject.isSponsored()) {
                TLRPC$ChatInvite tLRPC$ChatInvite = messageObject.sponsoredChatInvite;
                if (tLRPC$ChatInvite != null && (tLRPC$Chat = tLRPC$ChatInvite.chat) != null) {
                    this.avatarDrawable.setInfo(tLRPC$Chat);
                    this.avatarImage.setForUserOrChat(messageObject.sponsoredChatInvite.chat, this.avatarDrawable);
                    return;
                }
                this.avatarDrawable.setInfo(tLRPC$ChatInvite);
                TLRPC$Photo tLRPC$Photo = messageObject.sponsoredChatInvite.photo;
                if (tLRPC$Photo == null) {
                    return;
                }
                this.avatarImage.setImage(ImageLocation.getForPhoto(tLRPC$Photo.sizes.get(0), tLRPC$Photo), "50_50", this.avatarDrawable, null, null, 0);
                return;
            } else {
                this.currentPhoto = null;
                this.avatarDrawable.setInfo(messageObject.getFromChatId(), null, null);
                this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
                return;
            }
        }
        this.currentPhoto = null;
    }

    public void setSpoilersSuppressed(boolean z) {
        for (SpoilerEffect spoilerEffect : this.captionSpoilers) {
            spoilerEffect.setSuppressUpdates(z);
        }
        for (SpoilerEffect spoilerEffect2 : this.replySpoilers) {
            spoilerEffect2.setSuppressUpdates(z);
        }
        if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
            return;
        }
        Iterator<MessageObject.TextLayoutBlock> it = getMessageObject().textLayoutBlocks.iterator();
        while (it.hasNext()) {
            for (SpoilerEffect spoilerEffect3 : it.next().spoilers) {
                spoilerEffect3.setSuppressUpdates(z);
            }
        }
    }

    public boolean hasSpoilers() {
        if ((!hasCaptionLayout() || this.captionSpoilers.isEmpty()) && (this.replyTextLayout == null || this.replySpoilers.isEmpty())) {
            if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
                return false;
            }
            Iterator<MessageObject.TextLayoutBlock> it = getMessageObject().textLayoutBlocks.iterator();
            while (it.hasNext()) {
                if (!it.next().spoilers.isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private void updateSpoilersVisiblePart(int i, int i2) {
        if (hasCaptionLayout()) {
            float f = -this.captionY;
            for (SpoilerEffect spoilerEffect : this.captionSpoilers) {
                spoilerEffect.setVisibleBounds(0.0f, i + f, getWidth(), i2 + f);
            }
        }
        StaticLayout staticLayout = this.replyTextLayout;
        if (staticLayout != null) {
            float height = (-this.replyStartY) - staticLayout.getHeight();
            for (SpoilerEffect spoilerEffect2 : this.replySpoilers) {
                spoilerEffect2.setVisibleBounds(0.0f, i + height, getWidth(), i2 + height);
            }
        }
        if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
            return;
        }
        Iterator<MessageObject.TextLayoutBlock> it = getMessageObject().textLayoutBlocks.iterator();
        while (it.hasNext()) {
            MessageObject.TextLayoutBlock next = it.next();
            for (SpoilerEffect spoilerEffect3 : next.spoilers) {
                spoilerEffect3.setVisibleBounds(0.0f, (i - next.textYOffset) - this.textY, getWidth(), (i2 - next.textYOffset) - this.textY);
            }
        }
    }

    public void setScrimReaction(String str) {
        this.reactionsLayoutInBubble.setScrimReaction(str);
    }

    public void drawScrimReaction(Canvas canvas, String str) {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i = groupedMessagePosition.flags;
            if ((i & 8) == 0 || (i & 1) == 0) {
                return;
            }
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (!reactionsLayoutInBubble.isSmall) {
            reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, str);
        }
    }

    public boolean checkUnreadReactions(float f, int i) {
        if (!this.reactionsLayoutInBubble.hasUnreadReactions) {
            return false;
        }
        float y = getY();
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        float f2 = y + reactionsLayoutInBubble.y;
        return f2 > f && (f2 + ((float) reactionsLayoutInBubble.height)) - ((float) AndroidUtilities.dp(16.0f)) < ((float) i);
    }

    public void markReactionsAsRead() {
        this.reactionsLayoutInBubble.hasUnreadReactions = false;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.markReactionsAsRead();
    }

    public void setVisibleOnScreen(boolean z) {
        if (this.visibleOnScreen != z) {
            this.visibleOnScreen = z;
            checkImageReceiversAttachState();
        }
    }

    public void setParentBounds(float f, int i) {
        this.parentBoundsTop = f;
        this.parentBoundsBottom = i;
        if (this.photoImageOutOfBounds) {
            float y = getY() + this.photoImage.getImageY();
            if (this.photoImage.getImageHeight() + y < this.parentBoundsTop || y > this.parentBoundsBottom) {
                return;
            }
            invalidate();
        }
    }

    /* loaded from: classes3.dex */
    public static class BotButton {
        private int angle;
        private TLRPC$KeyboardButton button;
        private int height;
        private boolean isInviteButton;
        private long lastUpdateTime;
        private float progressAlpha;
        private StaticLayout title;
        private int width;
        private int x;
        private int y;

        private BotButton() {
        }

        static /* synthetic */ float access$2716(BotButton botButton, float f) {
            float f2 = botButton.progressAlpha + f;
            botButton.progressAlpha = f2;
            return f2;
        }

        static /* synthetic */ float access$2724(BotButton botButton, float f) {
            float f2 = botButton.progressAlpha - f;
            botButton.progressAlpha = f2;
            return f2;
        }

        static /* synthetic */ int access$2816(BotButton botButton, float f) {
            int i = (int) (botButton.angle + f);
            botButton.angle = i;
            return i;
        }

        static /* synthetic */ int access$2820(BotButton botButton, int i) {
            int i2 = botButton.angle - i;
            botButton.angle = i2;
            return i2;
        }
    }

    /* loaded from: classes3.dex */
    public static class PollButton {
        private TLRPC$TL_pollAnswer answer;
        private boolean chosen;
        private boolean correct;
        private int count;
        private float decimal;
        public int height;
        private int percent;
        private float percentProgress;
        private boolean prevChosen;
        private int prevPercent;
        private float prevPercentProgress;
        private StaticLayout title;
        public int x;
        public int y;

        static /* synthetic */ int access$1712(PollButton pollButton, int i) {
            int i2 = pollButton.percent + i;
            pollButton.percent = i2;
            return i2;
        }

        static /* synthetic */ float access$2424(PollButton pollButton, float f) {
            float f2 = pollButton.decimal - f;
            pollButton.decimal = f2;
            return f2;
        }
    }

    public ChatMessageCell(Context context) {
        this(context, false, null);
    }

    public ChatMessageCell(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.reactionsLayoutInBubble = new ReactionsLayoutInBubble(this);
        this.scrollRect = new Rect();
        this.imageBackgroundGradientRotation = 45;
        this.selectorDrawable = new Drawable[2];
        this.selectorDrawableMaskType = new int[2];
        this.instantButtonRect = new RectF();
        this.pressedState = new int[]{16842910, 16842919};
        this.deleteProgressRect = new RectF();
        this.rect = new RectF();
        this.timeAlpha = 1.0f;
        this.controlsAlpha = 1.0f;
        this.links = new LinkSpanDrawable.LinkCollector(this);
        this.urlPathCache = new ArrayList<>();
        this.urlPathSelection = new ArrayList<>();
        this.rectPath = new Path();
        this.pollButtons = new ArrayList<>();
        this.botButtons = new ArrayList<>();
        this.botButtonsByData = new HashMap<>();
        this.botButtonsByPosition = new HashMap<>();
        this.currentAccount = UserConfig.selectedAccount;
        this.isCheckPressed = true;
        this.drawBackground = true;
        this.backgroundWidth = 100;
        this.commentButtonRect = new Rect();
        this.spoilersPatchedReplyTextLayout = new AtomicReference<>();
        this.forwardedNameLayout = new StaticLayout[2];
        this.forwardNameOffsetX = new float[2];
        this.drawTime = true;
        this.unlockAlpha = 1.0f;
        this.unlockSpoilerEffect = new SpoilerEffect();
        this.unlockSpoilerPath = new Path();
        this.unlockSpoilerRadii = new float[8];
        this.replySelectorRect = new Rect();
        this.ALPHA_PROPERTY_WORKAROUND = Build.VERSION.SDK_INT == 28;
        this.alphaInternal = 1.0f;
        this.transitionParams = new TransitionParams();
        this.roundVideoPlayPipFloat = new AnimatedFloat(this, 200L, CubicBezierInterpolator.EASE_OUT);
        this.diceFinishCallback = new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell.1
            {
                ChatMessageCell.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (ChatMessageCell.this.delegate != null) {
                    ChatMessageCell.this.delegate.onDiceFinished();
                }
            }
        };
        this.invalidateRunnable = new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell.2
            {
                ChatMessageCell.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                ChatMessageCell.this.checkLocationExpired();
                if (ChatMessageCell.this.locationExpired) {
                    ChatMessageCell.this.invalidate();
                    ChatMessageCell.this.scheduledInvalidate = false;
                    return;
                }
                ChatMessageCell chatMessageCell = ChatMessageCell.this;
                chatMessageCell.invalidate(((int) chatMessageCell.rect.left) - 5, ((int) ChatMessageCell.this.rect.top) - 5, ((int) ChatMessageCell.this.rect.right) + 5, ((int) ChatMessageCell.this.rect.bottom) + 5);
                if (!ChatMessageCell.this.scheduledInvalidate) {
                    return;
                }
                AndroidUtilities.runOnUIThread(ChatMessageCell.this.invalidateRunnable, 1000L);
            }
        };
        this.accessibilityVirtualViewBounds = new SparseArray<>();
        this.currentFocusedVirtualView = -1;
        this.backgroundCacheParams = new Theme.MessageDrawable.PathDrawParams();
        this.replySpoilers = new ArrayList();
        this.replySpoilersPool = new Stack<>();
        this.captionSpoilers = new ArrayList();
        this.captionSpoilersPool = new Stack<>();
        this.captionPatchedSpoilersLayout = new AtomicReference<>();
        this.sPath = new Path();
        this.hadLongPress = false;
        this.ANIMATION_OFFSET_X = new Property<ChatMessageCell, Float>(this, Float.class, "animationOffsetX") { // from class: org.telegram.ui.Cells.ChatMessageCell.8
            @Override // android.util.Property
            public Float get(ChatMessageCell chatMessageCell) {
                return Float.valueOf(chatMessageCell.animationOffsetX);
            }

            @Override // android.util.Property
            public void set(ChatMessageCell chatMessageCell, Float f) {
                chatMessageCell.setAnimationOffsetX(f.floatValue());
            }
        };
        this.resourcesProvider = resourcesProvider;
        this.canDrawBackgroundInParent = z;
        this.backgroundDrawable = new MessageBackgroundDrawable(this);
        ImageReceiver imageReceiver = new ImageReceiver();
        this.avatarImage = imageReceiver;
        imageReceiver.setAllowLoadingOnAttachedOnly(true);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        ImageReceiver imageReceiver2 = new ImageReceiver(this);
        this.replyImageReceiver = imageReceiver2;
        imageReceiver2.setAllowLoadingOnAttachedOnly(true);
        this.replyImageReceiver.setRoundRadius(AndroidUtilities.dp(2.0f));
        ImageReceiver imageReceiver3 = new ImageReceiver(this);
        this.locationImageReceiver = imageReceiver3;
        imageReceiver3.setAllowLoadingOnAttachedOnly(true);
        this.locationImageReceiver.setRoundRadius(AndroidUtilities.dp(26.1f));
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        this.contactAvatarDrawable = new AvatarDrawable();
        ImageReceiver imageReceiver4 = new ImageReceiver(this);
        this.photoImage = imageReceiver4;
        imageReceiver4.setAllowLoadingOnAttachedOnly(true);
        this.photoImage.setUseRoundForThumbDrawable(true);
        this.photoImage.setDelegate(this);
        this.radialProgress = new RadialProgress2(this, resourcesProvider);
        RadialProgress2 radialProgress2 = new RadialProgress2(this, resourcesProvider);
        this.videoRadialProgress = radialProgress2;
        radialProgress2.setDrawBackground(false);
        this.videoRadialProgress.setCircleRadius(AndroidUtilities.dp(15.0f));
        SeekBar seekBar = new SeekBar(this);
        this.seekBar = seekBar;
        seekBar.setDelegate(this);
        SeekBarWaveform seekBarWaveform = new SeekBarWaveform(context);
        this.seekBarWaveform = seekBarWaveform;
        seekBarWaveform.setDelegate(this);
        this.seekBarWaveform.setParentView(this);
        this.seekBarAccessibilityDelegate = new FloatSeekBarAccessibilityDelegate() { // from class: org.telegram.ui.Cells.ChatMessageCell.3
            {
                ChatMessageCell.this = this;
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public float getProgress() {
                if (ChatMessageCell.this.currentMessageObject.isMusic()) {
                    return ChatMessageCell.this.seekBar.getProgress();
                }
                if (ChatMessageCell.this.currentMessageObject.isVoice()) {
                    return ChatMessageCell.this.useSeekBarWaveform ? ChatMessageCell.this.seekBarWaveform.getProgress() : ChatMessageCell.this.seekBar.getProgress();
                } else if (!ChatMessageCell.this.currentMessageObject.isRoundVideo()) {
                    return 0.0f;
                } else {
                    return ChatMessageCell.this.currentMessageObject.audioProgress;
                }
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public void setProgress(float f) {
                if (ChatMessageCell.this.currentMessageObject.isMusic()) {
                    ChatMessageCell.this.seekBar.setProgress(f);
                } else if (ChatMessageCell.this.currentMessageObject.isVoice()) {
                    if (ChatMessageCell.this.useSeekBarWaveform) {
                        ChatMessageCell.this.seekBarWaveform.setProgress(f);
                    } else {
                        ChatMessageCell.this.seekBar.setProgress(f);
                    }
                } else if (!ChatMessageCell.this.currentMessageObject.isRoundVideo()) {
                    return;
                } else {
                    if (ChatMessageCell.this.useSeekBarWaveform) {
                        if (ChatMessageCell.this.seekBarWaveform != null) {
                            ChatMessageCell.this.seekBarWaveform.setProgress(f);
                        }
                    } else if (ChatMessageCell.this.seekBar != null) {
                        ChatMessageCell.this.seekBar.setProgress(f);
                    }
                    ChatMessageCell.this.currentMessageObject.audioProgress = f;
                }
                ChatMessageCell.this.onSeekBarDrag(f);
                ChatMessageCell.this.invalidate();
            }
        };
        this.roundVideoPlayingDrawable = new RoundVideoPlayingDrawable(this, resourcesProvider);
        setImportantForAccessibility(1);
    }

    private void createPollUI() {
        if (this.pollAvatarImages != null) {
            return;
        }
        this.pollAvatarImages = new ImageReceiver[3];
        this.pollAvatarDrawables = new AvatarDrawable[3];
        this.pollAvatarImagesVisible = new boolean[3];
        int i = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.pollAvatarImages;
            if (i >= imageReceiverArr.length) {
                break;
            }
            imageReceiverArr[i] = new ImageReceiver(this);
            this.pollAvatarImages[i].setRoundRadius(AndroidUtilities.dp(8.0f));
            this.pollAvatarDrawables[i] = new AvatarDrawable();
            this.pollAvatarDrawables[i].setTextSize(AndroidUtilities.dp(22.0f));
            i++;
        }
        this.pollCheckBox = new CheckBoxBase[10];
        int i2 = 0;
        while (true) {
            CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
            if (i2 >= checkBoxBaseArr.length) {
                return;
            }
            checkBoxBaseArr[i2] = new CheckBoxBase(this, 20, this.resourcesProvider);
            this.pollCheckBox[i2].setDrawUnchecked(false);
            this.pollCheckBox[i2].setBackgroundType(9);
            i2++;
        }
    }

    private void createCommentUI() {
        if (this.commentAvatarImages != null) {
            return;
        }
        this.commentAvatarImages = new ImageReceiver[3];
        this.commentAvatarDrawables = new AvatarDrawable[3];
        this.commentAvatarImagesVisible = new boolean[3];
        int i = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.commentAvatarImages;
            if (i >= imageReceiverArr.length) {
                return;
            }
            imageReceiverArr[i] = new ImageReceiver(this);
            this.commentAvatarImages[i].setRoundRadius(AndroidUtilities.dp(12.0f));
            this.commentAvatarDrawables[i] = new AvatarDrawable();
            this.commentAvatarDrawables[i].setTextSize(AndroidUtilities.dp(18.0f));
            i++;
        }
    }

    public void resetPressedLink(int i) {
        if (i != -1) {
            this.links.removeLinks(Integer.valueOf(i));
        } else {
            this.links.clear();
        }
        this.pressedEmoji = null;
        if (this.pressedLink != null) {
            if (this.pressedLinkType != i && i != -1) {
                return;
            }
            this.pressedLink = null;
            this.pressedLinkType = -1;
            invalidate();
        }
    }

    private void resetUrlPaths() {
        if (this.urlPathSelection.isEmpty()) {
            return;
        }
        this.urlPathCache.addAll(this.urlPathSelection);
        this.urlPathSelection.clear();
    }

    private LinkPath obtainNewUrlPath() {
        LinkPath linkPath;
        if (!this.urlPathCache.isEmpty()) {
            linkPath = this.urlPathCache.get(0);
            this.urlPathCache.remove(0);
        } else {
            linkPath = new LinkPath(true);
        }
        linkPath.reset();
        this.urlPathSelection.add(linkPath);
        return linkPath;
    }

    public int[] getRealSpanStartAndEnd(Spannable spannable, CharacterStyle characterStyle) {
        int i;
        int i2;
        boolean z;
        TextStyleSpan.TextStyleRun style;
        TLRPC$MessageEntity tLRPC$MessageEntity;
        if (!(characterStyle instanceof URLSpanBrowser) || (style = ((URLSpanBrowser) characterStyle).getStyle()) == null || (tLRPC$MessageEntity = style.urlEntity) == null) {
            i = 0;
            i2 = 0;
            z = false;
        } else {
            i2 = tLRPC$MessageEntity.offset;
            i = tLRPC$MessageEntity.length + i2;
            z = true;
        }
        if (!z) {
            i2 = spannable.getSpanStart(characterStyle);
            i = spannable.getSpanEnd(characterStyle);
        }
        return new int[]{i2, i};
    }

    /* JADX WARN: Removed duplicated region for block: B:220:0x00ff A[Catch: Exception -> 0x02b3, TryCatch #1 {Exception -> 0x02b3, blocks: (B:191:0x0077, B:193:0x008c, B:195:0x0092, B:197:0x00b4, B:199:0x00bf, B:201:0x00cf, B:207:0x00e0, B:210:0x00ec, B:212:0x00ef, B:214:0x00f5, B:220:0x00ff, B:222:0x0105, B:224:0x010b, B:226:0x0111, B:228:0x0115, B:280:0x0273, B:230:0x0119, B:231:0x0126, B:233:0x012a, B:235:0x0132, B:239:0x0159, B:279:0x0268, B:278:0x0265, B:282:0x0277, B:284:0x027d, B:286:0x0283, B:288:0x028c, B:290:0x0292, B:291:0x0298, B:293:0x029c, B:295:0x02a4, B:209:0x00e3, B:205:0x00d5, B:240:0x0164, B:242:0x0196, B:243:0x0198, B:245:0x01a2, B:247:0x01ae, B:250:0x01c5, B:252:0x01c8, B:255:0x01d3, B:258:0x01f6, B:248:0x01b9, B:259:0x01f9, B:261:0x01ff, B:263:0x0203, B:265:0x020f, B:268:0x022e, B:270:0x0231, B:273:0x023c, B:266:0x021e), top: B:303:0x0077, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:224:0x010b A[Catch: Exception -> 0x02b3, TryCatch #1 {Exception -> 0x02b3, blocks: (B:191:0x0077, B:193:0x008c, B:195:0x0092, B:197:0x00b4, B:199:0x00bf, B:201:0x00cf, B:207:0x00e0, B:210:0x00ec, B:212:0x00ef, B:214:0x00f5, B:220:0x00ff, B:222:0x0105, B:224:0x010b, B:226:0x0111, B:228:0x0115, B:280:0x0273, B:230:0x0119, B:231:0x0126, B:233:0x012a, B:235:0x0132, B:239:0x0159, B:279:0x0268, B:278:0x0265, B:282:0x0277, B:284:0x027d, B:286:0x0283, B:288:0x028c, B:290:0x0292, B:291:0x0298, B:293:0x029c, B:295:0x02a4, B:209:0x00e3, B:205:0x00d5, B:240:0x0164, B:242:0x0196, B:243:0x0198, B:245:0x01a2, B:247:0x01ae, B:250:0x01c5, B:252:0x01c8, B:255:0x01d3, B:258:0x01f6, B:248:0x01b9, B:259:0x01f9, B:261:0x01ff, B:263:0x0203, B:265:0x020f, B:268:0x022e, B:270:0x0231, B:273:0x023c, B:266:0x021e), top: B:303:0x0077, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:282:0x0277 A[Catch: Exception -> 0x02b3, TryCatch #1 {Exception -> 0x02b3, blocks: (B:191:0x0077, B:193:0x008c, B:195:0x0092, B:197:0x00b4, B:199:0x00bf, B:201:0x00cf, B:207:0x00e0, B:210:0x00ec, B:212:0x00ef, B:214:0x00f5, B:220:0x00ff, B:222:0x0105, B:224:0x010b, B:226:0x0111, B:228:0x0115, B:280:0x0273, B:230:0x0119, B:231:0x0126, B:233:0x012a, B:235:0x0132, B:239:0x0159, B:279:0x0268, B:278:0x0265, B:282:0x0277, B:284:0x027d, B:286:0x0283, B:288:0x028c, B:290:0x0292, B:291:0x0298, B:293:0x029c, B:295:0x02a4, B:209:0x00e3, B:205:0x00d5, B:240:0x0164, B:242:0x0196, B:243:0x0198, B:245:0x01a2, B:247:0x01ae, B:250:0x01c5, B:252:0x01c8, B:255:0x01d3, B:258:0x01f6, B:248:0x01b9, B:259:0x01f9, B:261:0x01ff, B:263:0x0203, B:265:0x020f, B:268:0x022e, B:270:0x0231, B:273:0x023c, B:266:0x021e), top: B:303:0x0077, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkTextBlockMotionEvent(android.view.MotionEvent r19) {
        /*
            Method dump skipped, instructions count: 700
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkTextBlockMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:145:0x00c5 A[Catch: Exception -> 0x0142, TryCatch #1 {Exception -> 0x0142, blocks: (B:118:0x0055, B:120:0x0074, B:122:0x007f, B:124:0x008d, B:128:0x009a, B:131:0x00a6, B:133:0x00a9, B:135:0x00af, B:141:0x00b9, B:143:0x00bf, B:145:0x00c5, B:160:0x013e, B:146:0x00d1, B:148:0x00d5, B:150:0x00dd, B:154:0x0104, B:159:0x0133, B:158:0x0130, B:130:0x009d, B:126:0x0090, B:155:0x010d), top: B:180:0x0055, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:146:0x00d1 A[Catch: Exception -> 0x0142, TryCatch #1 {Exception -> 0x0142, blocks: (B:118:0x0055, B:120:0x0074, B:122:0x007f, B:124:0x008d, B:128:0x009a, B:131:0x00a6, B:133:0x00a9, B:135:0x00af, B:141:0x00b9, B:143:0x00bf, B:145:0x00c5, B:160:0x013e, B:146:0x00d1, B:148:0x00d5, B:150:0x00dd, B:154:0x0104, B:159:0x0133, B:158:0x0130, B:130:0x009d, B:126:0x0090, B:155:0x010d), top: B:180:0x0055, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkCaptionMotionEvent(android.view.MotionEvent r13) {
        /*
            Method dump skipped, instructions count: 375
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkCaptionMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:164:0x00ec  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x00ef  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkGameMotionEvent(android.view.MotionEvent r13) {
        /*
            Method dump skipped, instructions count: 461
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkGameMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkTranscribeButtonMotionEvent(MotionEvent motionEvent) {
        TranscribeButton transcribeButton;
        return this.useTranscribeButton && (!this.isPlayingRound || getVideoTranscriptionProgress() > 0.0f || this.wasTranscriptionOpen) && (transcribeButton = this.transcribeButton) != null && transcribeButton.onTouch(motionEvent.getAction(), motionEvent.getX(), motionEvent.getY());
    }

    /* JADX WARN: Removed duplicated region for block: B:297:0x00e9  */
    /* JADX WARN: Removed duplicated region for block: B:298:0x00ec  */
    /* JADX WARN: Removed duplicated region for block: B:324:0x0164  */
    /* JADX WARN: Removed duplicated region for block: B:326:0x016a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkLinkPreviewMotionEvent(android.view.MotionEvent r19) {
        /*
            Method dump skipped, instructions count: 1025
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkLinkPreviewMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkPollButtonMotionEvent(MotionEvent motionEvent) {
        int i;
        int i2;
        if (this.currentMessageObject.eventId != 0 || this.pollVoteInProgress || this.pollUnvoteInProgress || this.pollButtons.isEmpty()) {
            return false;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.type != 17 || !messageObject.isSent()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            this.pressedVoteButton = -1;
            this.pollHintPressed = false;
            if (this.hintButtonVisible && (i = this.pollHintX) != -1 && x >= i && x <= i + AndroidUtilities.dp(40.0f) && y >= (i2 = this.pollHintY) && y <= i2 + AndroidUtilities.dp(40.0f)) {
                this.pollHintPressed = true;
                this.selectorDrawableMaskType[0] = 3;
                if (Build.VERSION.SDK_INT >= 21) {
                    Drawable[] drawableArr = this.selectorDrawable;
                    if (drawableArr[0] != null) {
                        drawableArr[0].setBounds(this.pollHintX - AndroidUtilities.dp(8.0f), this.pollHintY - AndroidUtilities.dp(8.0f), this.pollHintX + AndroidUtilities.dp(32.0f), this.pollHintY + AndroidUtilities.dp(32.0f));
                        this.selectorDrawable[0].setHotspot(x, y);
                        this.selectorDrawable[0].setState(this.pressedState);
                    }
                }
                invalidate();
            } else {
                for (int i3 = 0; i3 < this.pollButtons.size(); i3++) {
                    PollButton pollButton = this.pollButtons.get(i3);
                    int dp = (pollButton.y + this.namesOffset) - AndroidUtilities.dp(13.0f);
                    int i4 = pollButton.x;
                    if (x >= i4 && x <= (i4 + this.backgroundWidth) - AndroidUtilities.dp(31.0f) && y >= dp && y <= pollButton.height + dp + AndroidUtilities.dp(26.0f)) {
                        this.pressedVoteButton = i3;
                        if (!this.pollVoted && !this.pollClosed) {
                            this.selectorDrawableMaskType[0] = 1;
                            if (Build.VERSION.SDK_INT >= 21) {
                                Drawable[] drawableArr2 = this.selectorDrawable;
                                if (drawableArr2[0] != null) {
                                    drawableArr2[0].setBounds(pollButton.x - AndroidUtilities.dp(9.0f), dp, (pollButton.x + this.backgroundWidth) - AndroidUtilities.dp(22.0f), pollButton.height + dp + AndroidUtilities.dp(26.0f));
                                    this.selectorDrawable[0].setHotspot(x, y);
                                    this.selectorDrawable[0].setState(this.pressedState);
                                }
                            }
                            invalidate();
                        }
                    }
                }
                return false;
            }
            return true;
        } else if (motionEvent.getAction() == 1) {
            if (this.pollHintPressed) {
                playSoundEffect(0);
                this.delegate.didPressHint(this, 0);
                this.pollHintPressed = false;
                if (Build.VERSION.SDK_INT < 21) {
                    return false;
                }
                Drawable[] drawableArr3 = this.selectorDrawable;
                if (drawableArr3[0] == null) {
                    return false;
                }
                drawableArr3[0].setState(StateSet.NOTHING);
                return false;
            } else if (this.pressedVoteButton == -1) {
                return false;
            } else {
                playSoundEffect(0);
                if (Build.VERSION.SDK_INT >= 21) {
                    Drawable[] drawableArr4 = this.selectorDrawable;
                    if (drawableArr4[0] != null) {
                        drawableArr4[0].setState(StateSet.NOTHING);
                    }
                }
                if (this.currentMessageObject.scheduled) {
                    Toast.makeText(getContext(), LocaleController.getString("MessageScheduledVote", R.string.MessageScheduledVote), 1).show();
                } else {
                    PollButton pollButton2 = this.pollButtons.get(this.pressedVoteButton);
                    TLRPC$TL_pollAnswer tLRPC$TL_pollAnswer = pollButton2.answer;
                    if (this.pollVoted || this.pollClosed) {
                        ArrayList<TLRPC$TL_pollAnswer> arrayList = new ArrayList<>();
                        arrayList.add(tLRPC$TL_pollAnswer);
                        this.delegate.didPressVoteButtons(this, arrayList, pollButton2.count, pollButton2.x + AndroidUtilities.dp(50.0f), this.namesOffset + pollButton2.y);
                    } else if (this.lastPoll.multiple_choice) {
                        if (this.currentMessageObject.checkedVotes.contains(tLRPC$TL_pollAnswer)) {
                            this.currentMessageObject.checkedVotes.remove(tLRPC$TL_pollAnswer);
                            this.pollCheckBox[this.pressedVoteButton].setChecked(false, true);
                        } else {
                            this.currentMessageObject.checkedVotes.add(tLRPC$TL_pollAnswer);
                            this.pollCheckBox[this.pressedVoteButton].setChecked(true, true);
                        }
                    } else {
                        this.pollVoteInProgressNum = this.pressedVoteButton;
                        this.pollVoteInProgress = true;
                        this.vibrateOnPollVote = true;
                        this.voteCurrentProgressTime = 0.0f;
                        this.firstCircleLength = true;
                        this.voteCurrentCircleLength = 360.0f;
                        this.voteRisingCircleLength = false;
                        ArrayList<TLRPC$TL_pollAnswer> arrayList2 = new ArrayList<>();
                        arrayList2.add(tLRPC$TL_pollAnswer);
                        this.delegate.didPressVoteButtons(this, arrayList2, -1, 0, 0);
                    }
                }
                this.pressedVoteButton = -1;
                invalidate();
                return false;
            }
        } else if (motionEvent.getAction() != 2) {
            return false;
        } else {
            if ((this.pressedVoteButton == -1 && !this.pollHintPressed) || Build.VERSION.SDK_INT < 21) {
                return false;
            }
            Drawable[] drawableArr5 = this.selectorDrawable;
            if (drawableArr5[0] == null) {
                return false;
            }
            drawableArr5[0].setHotspot(x, y);
            return false;
        }
    }

    private boolean checkInstantButtonMotionEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject.isSponsored() || (this.drawInstantView && this.currentMessageObject.type != 0)) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            int i = 2;
            if (motionEvent.getAction() == 0) {
                if (this.drawInstantView) {
                    float f = x;
                    float f2 = y;
                    if (this.instantButtonRect.contains(f, f2)) {
                        int[] iArr = this.selectorDrawableMaskType;
                        if (this.lastPoll == null) {
                            i = 0;
                        }
                        iArr[0] = i;
                        this.instantPressed = true;
                        if (Build.VERSION.SDK_INT >= 21 && this.selectorDrawable[0] != null && this.instantButtonRect.contains(f, f2)) {
                            this.selectorDrawable[0].setHotspot(f, f2);
                            this.selectorDrawable[0].setState(this.pressedState);
                            this.instantButtonPressed = true;
                        }
                        invalidate();
                        return true;
                    }
                }
            } else if (motionEvent.getAction() == 1) {
                if (this.instantPressed) {
                    ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                    if (chatMessageCellDelegate != null) {
                        if (this.lastPoll != null) {
                            MessageObject messageObject = this.currentMessageObject;
                            if (messageObject.scheduled) {
                                Toast.makeText(getContext(), LocaleController.getString("MessageScheduledVoteResults", R.string.MessageScheduledVoteResults), 1).show();
                            } else if (this.pollVoted || this.pollClosed) {
                                chatMessageCellDelegate.didPressInstantButton(this, this.drawInstantViewType);
                            } else {
                                if (!messageObject.checkedVotes.isEmpty()) {
                                    this.pollVoteInProgressNum = -1;
                                    this.pollVoteInProgress = true;
                                    this.vibrateOnPollVote = true;
                                    this.voteCurrentProgressTime = 0.0f;
                                    this.firstCircleLength = true;
                                    this.voteCurrentCircleLength = 360.0f;
                                    this.voteRisingCircleLength = false;
                                }
                                this.delegate.didPressVoteButtons(this, this.currentMessageObject.checkedVotes, -1, 0, this.namesOffset);
                            }
                        } else {
                            chatMessageCellDelegate.didPressInstantButton(this, this.drawInstantViewType);
                        }
                    }
                    playSoundEffect(0);
                    if (Build.VERSION.SDK_INT >= 21) {
                        Drawable[] drawableArr = this.selectorDrawable;
                        if (drawableArr[0] != null) {
                            drawableArr[0].setState(StateSet.NOTHING);
                        }
                    }
                    this.instantButtonPressed = false;
                    this.instantPressed = false;
                    invalidate();
                }
            } else if (motionEvent.getAction() == 2 && this.instantButtonPressed && Build.VERSION.SDK_INT >= 21) {
                Drawable[] drawableArr2 = this.selectorDrawable;
                if (drawableArr2[0] != null) {
                    drawableArr2[0].setHotspot(x, y);
                }
            }
            return false;
        }
        return false;
    }

    private void invalidateWithParent() {
        if (this.currentMessagesGroup != null && getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
        invalidate();
    }

    private boolean checkCommentButtonMotionEvent(MotionEvent motionEvent) {
        int i = 0;
        if (!this.drawCommentButton) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null && (groupedMessagePosition.flags & 1) == 0 && this.commentButtonRect.contains(x, y)) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            int childCount = viewGroup.getChildCount();
            while (true) {
                if (i >= childCount) {
                    break;
                }
                View childAt = viewGroup.getChildAt(i);
                if (childAt != this && (childAt instanceof ChatMessageCell)) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    if (chatMessageCell.drawCommentButton && chatMessageCell.currentMessagesGroup == this.currentMessagesGroup && (chatMessageCell.currentPosition.flags & 1) != 0) {
                        MotionEvent obtain = MotionEvent.obtain(0L, 0L, motionEvent.getActionMasked(), (motionEvent.getX() + getLeft()) - chatMessageCell.getLeft(), (motionEvent.getY() + getTop()) - chatMessageCell.getTop(), 0);
                        chatMessageCell.checkCommentButtonMotionEvent(obtain);
                        obtain.recycle();
                        break;
                    }
                }
                i++;
            }
            return true;
        }
        if (motionEvent.getAction() == 0) {
            if (this.commentButtonRect.contains(x, y)) {
                if (this.currentMessageObject.isSent()) {
                    this.selectorDrawableMaskType[1] = 2;
                    this.commentButtonPressed = true;
                    if (Build.VERSION.SDK_INT >= 21) {
                        Drawable[] drawableArr = this.selectorDrawable;
                        if (drawableArr[1] != null) {
                            drawableArr[1].setHotspot(x, y);
                            this.selectorDrawable[1].setState(this.pressedState);
                        }
                    }
                    invalidateWithParent();
                }
                return true;
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.commentButtonPressed) {
                ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                if (chatMessageCellDelegate != null) {
                    if (this.isRepliesChat) {
                        chatMessageCellDelegate.didPressSideButton(this);
                    } else {
                        chatMessageCellDelegate.didPressCommentButton(this);
                    }
                }
                playSoundEffect(0);
                if (Build.VERSION.SDK_INT >= 21) {
                    Drawable[] drawableArr2 = this.selectorDrawable;
                    if (drawableArr2[1] != null) {
                        drawableArr2[1].setState(StateSet.NOTHING);
                    }
                }
                this.commentButtonPressed = false;
                invalidateWithParent();
            }
        } else if (motionEvent.getAction() == 2 && this.commentButtonPressed && Build.VERSION.SDK_INT >= 21) {
            Drawable[] drawableArr3 = this.selectorDrawable;
            if (drawableArr3[1] != null) {
                drawableArr3[1].setHotspot(x, y);
            }
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r11v24, types: [boolean] */
    private boolean checkOtherButtonMotionEvent(MotionEvent motionEvent) {
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        int i = this.documentAttachType;
        if ((i == 5 || i == 1) && (groupedMessagePosition = this.currentPosition) != null && (groupedMessagePosition.flags & 4) == 0) {
            return false;
        }
        int i2 = this.currentMessageObject.type;
        boolean z = i2 == 16;
        if (!z) {
            z = (i == 1 || i2 == 12 || i == 5 || i == 4 || i == 2 || i2 == 8) && !this.hasGamePreview && !this.hasInvoicePreview;
        }
        if (!z) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject.type == 16) {
                ?? isVideoCall = messageObject.isVideoCall();
                int i3 = this.otherX;
                if (x >= i3) {
                    if (x <= i3 + AndroidUtilities.dp((isVideoCall == 0 ? 202 : 200) + 30) && y >= this.otherY - AndroidUtilities.dp(14.0f) && y <= this.otherY + AndroidUtilities.dp(50.0f)) {
                        this.otherPressed = true;
                        this.selectorDrawableMaskType[0] = 4;
                        if (Build.VERSION.SDK_INT >= 21 && this.selectorDrawable[0] != null) {
                            int dp = this.otherX + AndroidUtilities.dp(isVideoCall == 0 ? 202.0f : 200.0f) + (Theme.chat_msgInCallDrawable[isVideoCall == true ? 1 : 0].getIntrinsicWidth() / 2);
                            int intrinsicHeight = this.otherY + (Theme.chat_msgInCallDrawable[isVideoCall].getIntrinsicHeight() / 2);
                            this.selectorDrawable[0].setBounds(dp - AndroidUtilities.dp(20.0f), intrinsicHeight - AndroidUtilities.dp(20.0f), dp + AndroidUtilities.dp(20.0f), intrinsicHeight + AndroidUtilities.dp(20.0f));
                            this.selectorDrawable[0].setHotspot(x, y);
                            this.selectorDrawable[0].setState(this.pressedState);
                        }
                        invalidate();
                        return true;
                    }
                }
            } else if (x >= this.otherX - AndroidUtilities.dp(20.0f) && x <= this.otherX + AndroidUtilities.dp(20.0f) && y >= this.otherY - AndroidUtilities.dp(4.0f) && y <= this.otherY + AndroidUtilities.dp(30.0f)) {
                this.otherPressed = true;
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.otherPressed) {
                if (this.currentMessageObject.type == 16 && Build.VERSION.SDK_INT >= 21) {
                    Drawable[] drawableArr = this.selectorDrawable;
                    if (drawableArr[0] != null) {
                        drawableArr[0].setState(StateSet.NOTHING);
                    }
                }
                this.otherPressed = false;
                playSoundEffect(0);
                this.delegate.didPressOther(this, this.otherX, this.otherY);
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 2 && this.currentMessageObject.type == 16 && this.otherPressed && Build.VERSION.SDK_INT >= 21) {
            Drawable[] drawableArr2 = this.selectorDrawable;
            if (drawableArr2[0] != null) {
                drawableArr2[0].setHotspot(x, y);
            }
        }
        return false;
    }

    private boolean checkDateMotionEvent(MotionEvent motionEvent) {
        if (!this.currentMessageObject.isImportedForward()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            float f = x;
            float f2 = this.drawTimeX;
            if (f < f2 || f > f2 + this.timeWidth) {
                return false;
            }
            float f3 = y;
            float f4 = this.drawTimeY;
            if (f3 < f4 || f3 > f4 + AndroidUtilities.dp(20.0f)) {
                return false;
            }
            this.timePressed = true;
            invalidate();
        } else if (motionEvent.getAction() != 1 || !this.timePressed) {
            return false;
        } else {
            this.timePressed = false;
            playSoundEffect(0);
            this.delegate.didPressTime(this);
            invalidate();
        }
        return true;
    }

    private boolean checkRoundSeekbar(MotionEvent motionEvent) {
        if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || !MediaController.getInstance().isMessagePaused()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            float f = x;
            if (f >= this.seekbarRoundX - AndroidUtilities.dp(20.0f) && f <= this.seekbarRoundX + AndroidUtilities.dp(20.0f)) {
                float f2 = y;
                if (f2 >= this.seekbarRoundY - AndroidUtilities.dp(20.0f) && f2 <= this.seekbarRoundY + AndroidUtilities.dp(20.0f)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    cancelCheckLongPress();
                    this.roundSeekbarTouched = 1;
                    invalidate();
                }
            }
            float centerX = f - this.photoImage.getCenterX();
            float centerY = y - this.photoImage.getCenterY();
            float imageWidth = (this.photoImage.getImageWidth() - AndroidUtilities.dp(64.0f)) / 2.0f;
            float f3 = (centerX * centerX) + (centerY * centerY);
            if (f3 < ((this.photoImage.getImageWidth() / 2.0f) * this.photoImage.getImageWidth()) / 2.0f && f3 > imageWidth * imageWidth) {
                getParent().requestDisallowInterceptTouchEvent(true);
                cancelCheckLongPress();
                this.roundSeekbarTouched = 1;
                invalidate();
            }
        } else if (this.roundSeekbarTouched == 1 && motionEvent.getAction() == 2) {
            float degrees = ((float) Math.toDegrees(Math.atan2(y - this.photoImage.getCenterY(), x - this.photoImage.getCenterX()))) + 90.0f;
            if (degrees < 0.0f) {
                degrees += 360.0f;
            }
            float f4 = degrees / 360.0f;
            if (Math.abs(this.currentMessageObject.audioProgress - f4) > 0.9f) {
                if (this.roundSeekbarOutAlpha == 0.0f) {
                    performHapticFeedback(3);
                }
                this.roundSeekbarOutAlpha = 1.0f;
                this.roundSeekbarOutProgress = this.currentMessageObject.audioProgress;
            }
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.lastSeekUpdateTime > 100) {
                MediaController.getInstance().seekToProgress(this.currentMessageObject, f4);
                this.lastSeekUpdateTime = currentTimeMillis;
            }
            this.currentMessageObject.audioProgress = f4;
            updatePlayingMessageProgress();
        }
        if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && this.roundSeekbarTouched != 0) {
            if (motionEvent.getAction() == 1) {
                float degrees2 = ((float) Math.toDegrees(Math.atan2(y - this.photoImage.getCenterY(), x - this.photoImage.getCenterX()))) + 90.0f;
                if (degrees2 < 0.0f) {
                    degrees2 += 360.0f;
                }
                float f5 = degrees2 / 360.0f;
                this.currentMessageObject.audioProgress = f5;
                MediaController.getInstance().seekToProgress(this.currentMessageObject, f5);
                updatePlayingMessageProgress();
            }
            MediaController.getInstance().playMessage(this.currentMessageObject);
            this.roundSeekbarTouched = 0;
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return this.roundSeekbarTouched != 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:179:0x0051  */
    /* JADX WARN: Removed duplicated region for block: B:180:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:244:0x0199  */
    /* JADX WARN: Removed duplicated region for block: B:251:0x01c0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkPhotoImageMotionEvent(android.view.MotionEvent r9) {
        /*
            Method dump skipped, instructions count: 599
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkPhotoImageMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:206:0x00f8, code lost:
        if (r4 <= (r0 + r6)) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x00fa, code lost:
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:221:0x0132, code lost:
        if (r4 <= (r0 + r6)) goto L60;
     */
    /* JADX WARN: Removed duplicated region for block: B:194:0x00e3  */
    /* JADX WARN: Removed duplicated region for block: B:226:0x013c  */
    /* JADX WARN: Removed duplicated region for block: B:232:0x014c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkAudioMotionEvent(android.view.MotionEvent r13) {
        /*
            Method dump skipped, instructions count: 428
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkAudioMotionEvent(android.view.MotionEvent):boolean");
    }

    public boolean checkSpoilersMotionEvent(MotionEvent motionEvent, int i) {
        int i2;
        MessageObject.GroupedMessages groupedMessages;
        if (i <= 15 && getParent() != null) {
            if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
                ViewGroup viewGroup = (ViewGroup) getParent();
                for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                    View childAt = viewGroup.getChildAt(i3);
                    if (childAt instanceof ChatMessageCell) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                        MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
                        MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                        if (currentMessagesGroup != null && currentMessagesGroup.groupId == this.currentMessagesGroup.groupId) {
                            int i4 = currentPosition.flags;
                            if ((i4 & 8) != 0 && (i4 & 1) != 0 && chatMessageCell != this) {
                                motionEvent.offsetLocation(getLeft() - chatMessageCell.getLeft(), getTop() - chatMessageCell.getTop());
                                boolean checkSpoilersMotionEvent = chatMessageCell.checkSpoilersMotionEvent(motionEvent, i + 1);
                                motionEvent.offsetLocation(-(getLeft() - chatMessageCell.getLeft()), -(getTop() - chatMessageCell.getTop()));
                                return checkSpoilersMotionEvent;
                            }
                        }
                    }
                }
            }
            if (this.isSpoilerRevealing) {
                return false;
            }
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                int i5 = this.textX;
                if (x >= i5 && y >= (i2 = this.textY)) {
                    MessageObject messageObject = this.currentMessageObject;
                    if (x <= i5 + messageObject.textWidth && y <= i2 + messageObject.textHeight) {
                        ArrayList<MessageObject.TextLayoutBlock> arrayList = messageObject.textLayoutBlocks;
                        for (int i6 = 0; i6 < arrayList.size() && arrayList.get(i6).textYOffset <= y; i6++) {
                            MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i6);
                            int i7 = textLayoutBlock.isRtl() ? (int) this.currentMessageObject.textXOffset : 0;
                            for (SpoilerEffect spoilerEffect : textLayoutBlock.spoilers) {
                                if (spoilerEffect.getBounds().contains((x - this.textX) + i7, (int) ((y - this.textY) - textLayoutBlock.textYOffset))) {
                                    this.spoilerPressed = spoilerEffect;
                                    this.isCaptionSpoilerPressed = false;
                                    return true;
                                }
                            }
                        }
                    }
                }
                if (hasCaptionLayout()) {
                    float f = x;
                    float f2 = this.captionX;
                    if (f >= f2) {
                        float f3 = y;
                        if (f3 >= this.captionY && f <= f2 + this.captionLayout.getWidth() && f3 <= this.captionY + this.captionLayout.getHeight()) {
                            for (SpoilerEffect spoilerEffect2 : this.captionSpoilers) {
                                if (spoilerEffect2.getBounds().contains((int) (f - this.captionX), (int) (f3 - this.captionY))) {
                                    this.spoilerPressed = spoilerEffect2;
                                    this.isCaptionSpoilerPressed = true;
                                    return true;
                                }
                            }
                        }
                    }
                }
            } else if (actionMasked == 1 && this.spoilerPressed != null) {
                playSoundEffect(0);
                this.sPath.rewind();
                if (this.isCaptionSpoilerPressed) {
                    for (SpoilerEffect spoilerEffect3 : this.captionSpoilers) {
                        Rect bounds = spoilerEffect3.getBounds();
                        this.sPath.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom, Path.Direction.CW);
                    }
                } else {
                    Iterator<MessageObject.TextLayoutBlock> it = this.currentMessageObject.textLayoutBlocks.iterator();
                    while (it.hasNext()) {
                        MessageObject.TextLayoutBlock next = it.next();
                        for (SpoilerEffect spoilerEffect4 : next.spoilers) {
                            Rect bounds2 = spoilerEffect4.getBounds();
                            float f4 = next.textYOffset;
                            this.sPath.addRect(bounds2.left, bounds2.top + f4, bounds2.right, bounds2.bottom + f4, Path.Direction.CW);
                        }
                    }
                }
                this.sPath.computeBounds(this.rect, false);
                float sqrt = (float) Math.sqrt(Math.pow(this.rect.width(), 2.0d) + Math.pow(this.rect.height(), 2.0d));
                this.isSpoilerRevealing = true;
                this.spoilerPressed.setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatMessageCell.this.lambda$checkSpoilersMotionEvent$1();
                    }
                });
                if (this.isCaptionSpoilerPressed) {
                    for (SpoilerEffect spoilerEffect5 : this.captionSpoilers) {
                        spoilerEffect5.startRipple(x - this.captionX, y - this.captionY, sqrt);
                    }
                } else {
                    ArrayList<MessageObject.TextLayoutBlock> arrayList2 = this.currentMessageObject.textLayoutBlocks;
                    if (arrayList2 != null) {
                        Iterator<MessageObject.TextLayoutBlock> it2 = arrayList2.iterator();
                        while (it2.hasNext()) {
                            MessageObject.TextLayoutBlock next2 = it2.next();
                            int i8 = next2.isRtl() ? (int) this.currentMessageObject.textXOffset : 0;
                            for (SpoilerEffect spoilerEffect6 : next2.spoilers) {
                                spoilerEffect6.startRipple((x - this.textX) + i8, (y - next2.textYOffset) - this.textY, sqrt);
                            }
                        }
                    }
                }
                if (getParent() instanceof RecyclerListView) {
                    ViewGroup viewGroup2 = (ViewGroup) getParent();
                    for (int i9 = 0; i9 < viewGroup2.getChildCount(); i9++) {
                        View childAt2 = viewGroup2.getChildAt(i9);
                        if (childAt2 instanceof ChatMessageCell) {
                            final ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt2;
                            if (chatMessageCell2.getMessageObject() != null && chatMessageCell2.getMessageObject().getReplyMsgId() == getMessageObject().getId() && !chatMessageCell2.replySpoilers.isEmpty()) {
                                chatMessageCell2.replySpoilers.get(0).setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda7
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        ChatMessageCell.this.lambda$checkSpoilersMotionEvent$3(chatMessageCell2);
                                    }
                                });
                                for (SpoilerEffect spoilerEffect7 : chatMessageCell2.replySpoilers) {
                                    spoilerEffect7.startRipple(spoilerEffect7.getBounds().centerX(), spoilerEffect7.getBounds().centerY(), sqrt);
                                }
                            }
                        }
                    }
                }
                this.spoilerPressed = null;
                return true;
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$checkSpoilersMotionEvent$1() {
        post(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ChatMessageCell.this.lambda$checkSpoilersMotionEvent$0();
            }
        });
    }

    public /* synthetic */ void lambda$checkSpoilersMotionEvent$0() {
        this.isSpoilerRevealing = false;
        getMessageObject().isSpoilersRevealed = true;
        if (this.isCaptionSpoilerPressed) {
            this.captionSpoilers.clear();
        } else {
            ArrayList<MessageObject.TextLayoutBlock> arrayList = this.currentMessageObject.textLayoutBlocks;
            if (arrayList != null) {
                Iterator<MessageObject.TextLayoutBlock> it = arrayList.iterator();
                while (it.hasNext()) {
                    it.next().spoilers.clear();
                }
            }
        }
        invalidate();
    }

    public /* synthetic */ void lambda$checkSpoilersMotionEvent$3(ChatMessageCell chatMessageCell) {
        post(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ChatMessageCell.lambda$checkSpoilersMotionEvent$2(ChatMessageCell.this);
            }
        });
    }

    public static /* synthetic */ void lambda$checkSpoilersMotionEvent$2(ChatMessageCell chatMessageCell) {
        chatMessageCell.getMessageObject().replyMessageObject.isSpoilersRevealed = true;
        chatMessageCell.replySpoilers.clear();
        chatMessageCell.invalidate();
    }

    private boolean checkBotButtonMotionEvent(MotionEvent motionEvent) {
        int dp;
        if (this.botButtons.isEmpty() || this.currentMessageObject.eventId != 0) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.currentMessageObject.isOutOwner()) {
                dp = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                dp = this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
            }
            for (int i = 0; i < this.botButtons.size(); i++) {
                BotButton botButton = this.botButtons.get(i);
                int dp2 = (botButton.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                if (x >= botButton.x + dp && x <= botButton.x + dp + botButton.width && y >= dp2 && y <= dp2 + botButton.height) {
                    this.pressedBotButton = i;
                    invalidate();
                    final int i2 = this.pressedBotButton;
                    postDelayed(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatMessageCell.this.lambda$checkBotButtonMotionEvent$4(i2);
                        }
                    }, ViewConfiguration.getLongPressTimeout() - 1);
                    return true;
                }
            }
            return false;
        } else if (motionEvent.getAction() != 1 || this.pressedBotButton == -1) {
            return false;
        } else {
            playSoundEffect(0);
            if (this.currentMessageObject.scheduled) {
                Toast.makeText(getContext(), LocaleController.getString("MessageScheduledBotAction", R.string.MessageScheduledBotAction), 1).show();
            } else {
                BotButton botButton2 = this.botButtons.get(this.pressedBotButton);
                if (botButton2.button != null) {
                    this.delegate.didPressBotButton(this, botButton2.button);
                }
            }
            this.pressedBotButton = -1;
            invalidate();
            return false;
        }
    }

    public /* synthetic */ void lambda$checkBotButtonMotionEvent$4(int i) {
        int i2 = this.pressedBotButton;
        if (i == i2) {
            if (!this.currentMessageObject.scheduled) {
                BotButton botButton = this.botButtons.get(i2);
                if (botButton.button != null) {
                    cancelCheckLongPress();
                    this.delegate.didLongPressBotButton(this, botButton.button);
                }
            }
            this.pressedBotButton = -1;
            invalidate();
        }
    }

    private boolean checkTopicButtonMotionEvent(MotionEvent motionEvent) {
        Drawable drawable;
        RectF rectF = this.topicHitRect;
        if (rectF == null || !this.drawTopic) {
            this.topicPressed = false;
            return false;
        }
        boolean contains = rectF.contains(motionEvent.getX(), motionEvent.getY());
        if (motionEvent.getAction() == 0) {
            if (contains) {
                Drawable drawable2 = this.topicSelectorDrawable;
                if (drawable2 != null) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        drawable2.setHotspot(motionEvent.getX() - this.topicHitRect.left, motionEvent.getY() - this.topicHitRect.top);
                    }
                    this.topicSelectorDrawable.setState(new int[]{16842919, 16842910});
                    invalidate();
                }
                this.topicPressed = true;
            } else {
                this.topicPressed = false;
            }
            return this.topicPressed;
        } else if (motionEvent.getAction() == 2) {
            boolean z = this.topicPressed;
            if (z != contains) {
                if (z && (drawable = this.topicSelectorDrawable) != null) {
                    drawable.setState(new int[0]);
                    invalidate();
                }
                this.topicPressed = contains;
            }
            return this.topicPressed;
        } else {
            if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && this.topicPressed) {
                this.topicPressed = false;
                Drawable drawable3 = this.topicSelectorDrawable;
                if (drawable3 != null) {
                    drawable3.setState(new int[0]);
                    invalidate();
                }
                if (motionEvent.getAction() == 1) {
                    ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                    if (chatMessageCellDelegate != null) {
                        chatMessageCellDelegate.didPressTopicButton(this);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:890:0x0656, code lost:
        if (r5 > (r0 + org.telegram.messenger.AndroidUtilities.dp(32 + ((r18.drawSideButton != 3 || r18.commentLayout == null) ? 0 : 18)))) goto L466;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r19) {
        /*
            Method dump skipped, instructions count: 1648
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public /* synthetic */ void lambda$onTouchEvent$5() {
        if (!this.replyPressed || this.replySelectorPressed || !this.replySelectorCanBePressed) {
            return;
        }
        this.replySelectorPressed = true;
        this.replySelector.setState(new int[]{16842919, 16842910});
    }

    public /* synthetic */ void lambda$onTouchEvent$6() {
        this.replySelector.setState(new int[0]);
        invalidate();
    }

    private boolean checkReactionsTouchEvent(MotionEvent motionEvent) {
        MessageObject.GroupedMessages groupedMessages;
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            if (viewGroup == null) {
                return false;
            }
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                    if (currentMessagesGroup != null && currentMessagesGroup.groupId == this.currentMessagesGroup.groupId) {
                        int i2 = currentPosition.flags;
                        if ((i2 & 8) != 0 && (i2 & 1) != 0) {
                            if (chatMessageCell == this) {
                                return this.reactionsLayoutInBubble.chekTouchEvent(motionEvent);
                            }
                            motionEvent.offsetLocation(getLeft() - chatMessageCell.getLeft(), getTop() - chatMessageCell.getTop());
                            boolean chekTouchEvent = chatMessageCell.reactionsLayoutInBubble.chekTouchEvent(motionEvent);
                            motionEvent.offsetLocation(-(getLeft() - chatMessageCell.getLeft()), -(getTop() - chatMessageCell.getTop()));
                            return chekTouchEvent;
                        }
                    }
                }
            }
            return false;
        }
        return this.reactionsLayoutInBubble.chekTouchEvent(motionEvent);
    }

    private boolean checkPinchToZoom(MotionEvent motionEvent) {
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        PinchToZoomHelper pinchToZoomHelper = chatMessageCellDelegate == null ? null : chatMessageCellDelegate.getPinchToZoomHelper();
        if (this.currentMessageObject == null || !this.photoImage.hasNotThumb() || pinchToZoomHelper == null || this.currentMessageObject.isSticker() || this.currentMessageObject.isAnimatedEmoji()) {
            return false;
        }
        if ((this.currentMessageObject.isVideo() && !this.autoPlayingMedia) || this.isRoundVideo || this.currentMessageObject.isAnimatedSticker()) {
            return false;
        }
        if ((this.currentMessageObject.isDocument() && !this.currentMessageObject.isGif()) || this.currentMessageObject.needDrawBluredPreview()) {
            return false;
        }
        return pinchToZoomHelper.checkPinchToZoom(motionEvent, this, this.photoImage, this.currentMessageObject);
    }

    private boolean checkTextSelection(MotionEvent motionEvent) {
        MessageObject messageObject;
        TLRPC$Message tLRPC$Message;
        int i;
        int dp;
        int i2;
        MessageObject.GroupedMessages groupedMessages;
        TextSelectionHelper.ChatListTextSelectionHelper textSelectionHelper = this.delegate.getTextSelectionHelper();
        if (textSelectionHelper == null || MessagesController.getInstance(this.currentAccount).isChatNoForwards(this.currentMessageObject.getChatId()) || ((tLRPC$Message = (messageObject = this.currentMessageObject).messageOwner) != null && tLRPC$Message.noforwards)) {
            return false;
        }
        ArrayList<MessageObject.TextLayoutBlock> arrayList = messageObject.textLayoutBlocks;
        if (!(arrayList != null && !arrayList.isEmpty()) && !hasCaptionLayout()) {
            return false;
        }
        if ((!this.drawSelectionBackground && this.currentMessagesGroup == null) || (this.currentMessagesGroup != null && !this.delegate.hasSelectedMessages())) {
            return false;
        }
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                View childAt = viewGroup.getChildAt(i3);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                    if (currentMessagesGroup != null && currentMessagesGroup.groupId == this.currentMessagesGroup.groupId) {
                        int i4 = currentPosition.flags;
                        if ((i4 & 8) != 0 && (i4 & 1) != 0) {
                            textSelectionHelper.setMaybeTextCord((int) chatMessageCell.captionX, (int) chatMessageCell.captionY);
                            textSelectionHelper.setMessageObject(chatMessageCell);
                            if (chatMessageCell == this) {
                                return textSelectionHelper.onTouchEvent(motionEvent);
                            }
                            motionEvent.offsetLocation(getLeft() - chatMessageCell.getLeft(), getTop() - chatMessageCell.getTop());
                            boolean onTouchEvent = textSelectionHelper.onTouchEvent(motionEvent);
                            motionEvent.offsetLocation(-(getLeft() - chatMessageCell.getLeft()), -(getTop() - chatMessageCell.getTop()));
                            return onTouchEvent;
                        }
                    }
                }
            }
            return false;
        }
        if (hasCaptionLayout()) {
            textSelectionHelper.setIsDescription(false);
            textSelectionHelper.setMaybeTextCord((int) this.captionX, (int) this.captionY);
        } else if (this.descriptionLayout != null && motionEvent.getY() > this.descriptionY) {
            textSelectionHelper.setIsDescription(true);
            if (this.hasGamePreview) {
                i2 = this.unmovedTextX - AndroidUtilities.dp(10.0f);
            } else {
                if (this.hasInvoicePreview) {
                    i = this.unmovedTextX;
                    dp = AndroidUtilities.dp(1.0f);
                } else {
                    i = this.unmovedTextX;
                    dp = AndroidUtilities.dp(1.0f);
                }
                i2 = i + dp;
            }
            textSelectionHelper.setMaybeTextCord(i2 + AndroidUtilities.dp(10.0f) + this.descriptionX, this.descriptionY);
        } else {
            textSelectionHelper.setIsDescription(false);
            textSelectionHelper.setMaybeTextCord(this.textX, this.textY);
        }
        textSelectionHelper.setMessageObject(this);
        return textSelectionHelper.onTouchEvent(motionEvent);
    }

    private void updateSelectionTextPosition() {
        int i;
        int dp;
        int i2;
        if (getDelegate() == null || getDelegate().getTextSelectionHelper() == null || !getDelegate().getTextSelectionHelper().isSelected(this.currentMessageObject)) {
            return;
        }
        int textSelectionType = getDelegate().getTextSelectionHelper().getTextSelectionType(this);
        if (textSelectionType == TextSelectionHelper.ChatListTextSelectionHelper.TYPE_DESCRIPTION) {
            if (this.hasGamePreview) {
                i2 = this.unmovedTextX - AndroidUtilities.dp(10.0f);
            } else {
                if (this.hasInvoicePreview) {
                    i = this.unmovedTextX;
                    dp = AndroidUtilities.dp(1.0f);
                } else {
                    i = this.unmovedTextX;
                    dp = AndroidUtilities.dp(1.0f);
                }
                i2 = i + dp;
            }
            getDelegate().getTextSelectionHelper().updateTextPosition(i2 + AndroidUtilities.dp(10.0f) + this.descriptionX, this.descriptionY);
        } else if (textSelectionType == TextSelectionHelper.ChatListTextSelectionHelper.TYPE_CAPTION) {
            getDelegate().getTextSelectionHelper().updateTextPosition((int) this.captionX, (int) this.captionY);
        } else {
            getDelegate().getTextSelectionHelper().updateTextPosition(this.textX, this.textY);
        }
    }

    public ArrayList<PollButton> getPollButtons() {
        return this.pollButtons;
    }

    public void updatePlayingMessageProgress() {
        String formatShortDuration;
        int i;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        VideoPlayerRewinder videoPlayerRewinder = this.videoPlayerRewinder;
        if (videoPlayerRewinder != null && videoPlayerRewinder.rewindCount != 0 && videoPlayerRewinder.rewindByBackSeek) {
            messageObject.audioProgress = videoPlayerRewinder.getVideoProgress();
        }
        int i2 = 0;
        if (this.documentAttachType == 4) {
            if (this.infoLayout != null && (PhotoViewer.isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isGoingToShowMessageObject(this.currentMessageObject))) {
                return;
            }
            AnimatedFileDrawable animation = this.photoImage.getAnimation();
            if (animation != null) {
                MessageObject messageObject2 = this.currentMessageObject;
                i2 = animation.getDurationMs() / 1000;
                messageObject2.audioPlayerDuration = i2;
                MessageObject messageObject3 = this.currentMessageObject;
                TLRPC$Message tLRPC$Message = messageObject3.messageOwner;
                if (tLRPC$Message.ttl > 0 && tLRPC$Message.destroyTime == 0 && !messageObject3.needDrawBluredPreview() && this.currentMessageObject.isVideo() && animation.hasBitmap()) {
                    this.delegate.didStartVideoStream(this.currentMessageObject);
                }
            }
            if (i2 == 0) {
                i2 = this.currentMessageObject.getDuration();
            }
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                float f = i2;
                i2 = (int) (f - (this.currentMessageObject.audioProgress * f));
            } else if (animation != null) {
                if (i2 != 0) {
                    i2 -= animation.getCurrentProgressMs() / 1000;
                }
                if (this.delegate != null && animation.getCurrentProgressMs() >= 3000) {
                    this.delegate.videoTimerReached();
                }
            }
            if (this.lastTime == i2) {
                return;
            }
            String formatShortDuration2 = AndroidUtilities.formatShortDuration(i2);
            this.infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(formatShortDuration2));
            this.infoLayout = new StaticLayout(formatShortDuration2, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.lastTime = i2;
        } else if (this.isRoundVideo) {
            if (this.useSeekBarWaveform) {
                if (!this.seekBarWaveform.isDragging()) {
                    this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress, true);
                }
            } else if (!this.seekBar.isDragging()) {
                this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                this.seekBar.setBufferedProgress(this.currentMessageObject.bufferedProgress);
            }
            TLRPC$Document document = this.currentMessageObject.getDocument();
            int i3 = 0;
            while (true) {
                if (i3 >= document.attributes.size()) {
                    i = 0;
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = document.attributes.get(i3);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                    i = tLRPC$DocumentAttribute.duration;
                    break;
                }
                i3++;
            }
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                i = Math.max(0, i - this.currentMessageObject.audioProgressSec);
            }
            if (this.lastTime != i) {
                this.lastTime = i;
                String formatLongDuration = AndroidUtilities.formatLongDuration(i);
                this.timeWidthAudio = (int) Math.ceil(Theme.chat_timePaint.measureText(formatLongDuration));
                this.durationLayout = new StaticLayout(formatLongDuration, Theme.chat_timePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            float f2 = this.currentMessageObject.audioProgress;
            if (f2 != 0.0f) {
                this.lastDrawingAudioProgress = f2;
                if (f2 > 0.9f) {
                    this.lastDrawingAudioProgress = 1.0f;
                }
            }
            invalidate();
        } else if (this.documentAttach == null) {
        } else {
            if (this.useSeekBarWaveform) {
                if (!this.seekBarWaveform.isDragging()) {
                    this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress, true);
                }
            } else if (!this.seekBar.isDragging()) {
                this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                this.seekBar.setBufferedProgress(this.currentMessageObject.bufferedProgress);
            }
            if (this.documentAttachType == 3) {
                if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    int i4 = 0;
                    while (true) {
                        if (i4 >= this.documentAttach.attributes.size()) {
                            break;
                        }
                        TLRPC$DocumentAttribute tLRPC$DocumentAttribute2 = this.documentAttach.attributes.get(i4);
                        if (tLRPC$DocumentAttribute2 instanceof TLRPC$TL_documentAttributeAudio) {
                            i2 = tLRPC$DocumentAttribute2.duration;
                            break;
                        }
                        i4++;
                    }
                } else {
                    i2 = this.currentMessageObject.audioProgressSec;
                }
                if (this.lastTime != i2) {
                    this.lastTime = i2;
                    String formatLongDuration2 = AndroidUtilities.formatLongDuration(i2);
                    this.timeWidthAudio = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(formatLongDuration2));
                    this.durationLayout = new StaticLayout(formatLongDuration2, Theme.chat_audioTimePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
            } else {
                int duration = this.currentMessageObject.getDuration();
                if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    i2 = this.currentMessageObject.audioProgressSec;
                }
                if (this.lastTime != i2) {
                    this.lastTime = i2;
                    this.durationLayout = new StaticLayout(AndroidUtilities.formatShortDuration(i2, duration), Theme.chat_audioTimePaint, (int) Math.ceil(Theme.chat_audioTimePaint.measureText(formatShortDuration)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
            }
            invalidate();
        }
    }

    public void setFullyDraw(boolean z) {
        this.fullyDraw = z;
    }

    public void setParentViewSize(int i, int i2) {
        Theme.MessageDrawable messageDrawable;
        this.parentWidth = i;
        this.parentHeight = i2;
        this.backgroundHeight = i2;
        if ((this.currentMessageObject == null || !hasGradientService() || !this.currentMessageObject.shouldDrawWithoutBackground()) && ((messageDrawable = this.currentBackgroundDrawable) == null || messageDrawable.getGradientShader() == null)) {
            return;
        }
        invalidate();
    }

    public void setVisiblePart(int i, int i2, int i3, float f, float f2, int i4, int i5, int i6, int i7) {
        MessageObject.TextLayoutBlock textLayoutBlock;
        this.parentWidth = i4;
        this.parentHeight = i5;
        this.backgroundHeight = i5;
        this.blurredViewTopOffset = i6;
        this.blurredViewBottomOffset = i7;
        this.viewTop = f2;
        if (i3 != i5 || f != this.parentViewTopOffset) {
            this.parentViewTopOffset = f;
            this.parentHeight = i3;
        }
        if (this.currentMessageObject != null && hasGradientService() && this.currentMessageObject.shouldDrawWithoutBackground()) {
            invalidate();
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.textLayoutBlocks == null) {
            return;
        }
        int i8 = i - this.textY;
        int i9 = 0;
        for (int i10 = 0; i10 < this.currentMessageObject.textLayoutBlocks.size() && this.currentMessageObject.textLayoutBlocks.get(i10).textYOffset <= i8; i10++) {
            i9 = i10;
        }
        int i11 = -1;
        int i12 = -1;
        int i13 = 0;
        while (i9 < this.currentMessageObject.textLayoutBlocks.size()) {
            float f3 = this.currentMessageObject.textLayoutBlocks.get(i9).textYOffset;
            float f4 = i8;
            if (intersect(f3, textLayoutBlock.height + f3, f4, i8 + i2)) {
                if (i11 == -1) {
                    i11 = i9;
                }
                i13++;
                i12 = i9;
            } else if (f3 > f4) {
                break;
            }
            i9++;
        }
        if (this.lastVisibleBlockNum != i12 || this.firstVisibleBlockNum != i11 || this.totalVisibleBlocksCount != i13) {
            this.lastVisibleBlockNum = i12;
            this.firstVisibleBlockNum = i11;
            this.totalVisibleBlocksCount = i13;
            invalidate();
        } else if (this.animatedEmojiStack != null) {
            for (int i14 = 0; i14 < this.animatedEmojiStack.holders.size(); i14++) {
                AnimatedEmojiSpan.AnimatedEmojiHolder animatedEmojiHolder = this.animatedEmojiStack.holders.get(i14);
                if (animatedEmojiHolder != null && animatedEmojiHolder.skipDraw && !animatedEmojiHolder.outOfBounds((this.parentBoundsTop - getY()) - animatedEmojiHolder.drawingYOffset, (this.parentBoundsBottom - getY()) - animatedEmojiHolder.drawingYOffset)) {
                    invalidate();
                    return;
                }
            }
        }
    }

    public static StaticLayout generateStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, int i2, int i3, int i4) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        StaticLayout staticLayout = new StaticLayout(charSequence, textPaint, i2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int i5 = i;
        int i6 = 0;
        for (int i7 = 0; i7 < i3; i7++) {
            staticLayout.getLineDirections(i7);
            if (staticLayout.getLineLeft(i7) != 0.0f || staticLayout.isRtlCharAt(staticLayout.getLineStart(i7)) || staticLayout.isRtlCharAt(staticLayout.getLineEnd(i7))) {
                i5 = i2;
            }
            int lineEnd = staticLayout.getLineEnd(i7);
            if (lineEnd == charSequence.length()) {
                break;
            }
            int i8 = (lineEnd - 1) + i6;
            if (spannableStringBuilder.charAt(i8) == ' ') {
                spannableStringBuilder.replace(i8, i8 + 1, (CharSequence) "\n");
            } else if (spannableStringBuilder.charAt(i8) != '\n') {
                spannableStringBuilder.insert(i8, (CharSequence) "\n");
                i6++;
            }
            if (i7 == staticLayout.getLineCount() - 1 || i7 == i4 - 1) {
                break;
            }
        }
        int i9 = i5;
        return StaticLayoutEx.createStaticLayout(spannableStringBuilder, textPaint, i9, Layout.Alignment.ALIGN_NORMAL, 1.0f, AndroidUtilities.dp(1.0f), false, TextUtils.TruncateAt.END, i9, i4, true);
    }

    private void didClickedImage() {
        ChatMessageCellDelegate chatMessageCellDelegate;
        TLRPC$WebPage tLRPC$WebPage;
        boolean z;
        TLRPC$MessageMedia tLRPC$MessageMedia;
        TLRPC$ReplyMarkup tLRPC$ReplyMarkup;
        MessageObject messageObject = this.currentMessageObject;
        int i = messageObject.type;
        if (i == 20) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if (tLRPC$Message == null || (tLRPC$MessageMedia = tLRPC$Message.media) == null || tLRPC$MessageMedia.extended_media == null || (tLRPC$ReplyMarkup = tLRPC$Message.reply_markup) == null) {
                return;
            }
            Iterator<TLRPC$TL_keyboardButtonRow> it = tLRPC$ReplyMarkup.rows.iterator();
            while (it.hasNext()) {
                Iterator<TLRPC$KeyboardButton> it2 = it.next().buttons.iterator();
                if (it2.hasNext()) {
                    this.delegate.didPressExtendedMediaPreview(this, it2.next());
                    return;
                }
            }
        } else if (i == 1 || messageObject.isAnyKindOfSticker()) {
            int i2 = this.buttonState;
            if (i2 == -1) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            } else if (i2 != 0) {
            } else {
                didPressButton(true, false);
            }
        } else {
            MessageObject messageObject2 = this.currentMessageObject;
            int i3 = messageObject2.type;
            if (i3 == 12) {
                long j = MessageObject.getMedia(messageObject2.messageOwner).user_id;
                TLRPC$User tLRPC$User = null;
                if (j != 0) {
                    tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
                }
                this.delegate.didPressUserAvatar(this, tLRPC$User, this.lastTouchX, this.lastTouchY);
            } else if (i3 == 5) {
                if (this.buttonState != -1) {
                    didPressButton(true, false);
                } else if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isMessagePaused()) {
                    this.delegate.needPlayMessage(this.currentMessageObject, false);
                } else {
                    MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject);
                }
            } else if (i3 == 8) {
                int i4 = this.buttonState;
                if (i4 == -1 || (i4 == 1 && this.canStreamVideo && this.autoPlayingMedia)) {
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                } else if (i4 != 2 && i4 != 0) {
                } else {
                    didPressButton(true, false);
                }
            } else {
                int i5 = this.documentAttachType;
                if (i5 == 4) {
                    int i6 = this.buttonState;
                    if (i6 == -1 || ((z = this.drawVideoImageButton) && (this.autoPlayingMedia || (SharedConfig.streamMedia && this.canStreamVideo)))) {
                        this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                    } else if (z) {
                        didPressButton(true, true);
                    } else if (i6 != 0 && i6 != 3) {
                    } else {
                        didPressButton(true, false);
                    }
                } else if (i3 == 4) {
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                } else if (i5 == 1) {
                    if (this.buttonState != -1) {
                        return;
                    }
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                } else if (i5 == 2) {
                    if (this.buttonState != -1 || (tLRPC$WebPage = MessageObject.getMedia(messageObject2.messageOwner).webpage) == null) {
                        return;
                    }
                    String str = tLRPC$WebPage.embed_url;
                    if (str != null && str.length() != 0) {
                        this.delegate.needOpenWebView(this.currentMessageObject, tLRPC$WebPage.embed_url, tLRPC$WebPage.site_name, tLRPC$WebPage.description, tLRPC$WebPage.url, tLRPC$WebPage.embed_width, tLRPC$WebPage.embed_height);
                    } else {
                        Browser.openUrl(getContext(), tLRPC$WebPage.url);
                    }
                } else if (this.hasInvoicePreview) {
                    if (this.buttonState != -1) {
                        return;
                    }
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                } else if (Build.VERSION.SDK_INT < 26 || (chatMessageCellDelegate = this.delegate) == null) {
                } else {
                    if (i3 == 16) {
                        chatMessageCellDelegate.didLongPress(this, 0.0f, 0.0f);
                    } else {
                        chatMessageCellDelegate.didPressOther(this, this.otherX, this.otherY);
                    }
                }
            }
        }
    }

    private void updateSecretTimeText(MessageObject messageObject) {
        String secretTimeString;
        if (messageObject == null || !messageObject.needDrawBluredPreview() || (secretTimeString = messageObject.getSecretTimeString()) == null) {
            return;
        }
        int ceil = (int) Math.ceil(Theme.chat_infoPaint.measureText(secretTimeString));
        this.infoWidth = ceil;
        this.infoLayout = new StaticLayout(TextUtils.ellipsize(secretTimeString, Theme.chat_infoPaint, ceil, TextUtils.TruncateAt.END), Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        invalidate();
    }

    /* JADX WARN: Removed duplicated region for block: B:82:0x0048  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x00ce  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isPhotoDataChanged(org.telegram.messenger.MessageObject r25) {
        /*
            Method dump skipped, instructions count: 353
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.isPhotoDataChanged(org.telegram.messenger.MessageObject):boolean");
    }

    public int getRepliesCount() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.messages.isEmpty()) {
            return this.currentMessagesGroup.messages.get(0).getRepliesCount();
        }
        return this.currentMessageObject.getRepliesCount();
    }

    private ArrayList<TLRPC$Peer> getRecentRepliers() {
        TLRPC$MessageReplies tLRPC$MessageReplies;
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.messages.isEmpty() && (tLRPC$MessageReplies = this.currentMessagesGroup.messages.get(0).messageOwner.replies) != null) {
            return tLRPC$MessageReplies.recent_repliers;
        }
        TLRPC$MessageReplies tLRPC$MessageReplies2 = this.currentMessageObject.messageOwner.replies;
        if (tLRPC$MessageReplies2 == null) {
            return null;
        }
        return tLRPC$MessageReplies2.recent_repliers;
    }

    public void updateAnimatedEmojis() {
        if (!this.imageReceiversAttachState) {
            return;
        }
        boolean z = false;
        int cacheTypeForEnterView = this.currentMessageObject.wasJustSent ? AnimatedEmojiDrawable.getCacheTypeForEnterView() : 0;
        StaticLayout staticLayout = this.captionLayout;
        if (staticLayout != null) {
            this.animatedEmojiStack = AnimatedEmojiSpan.update(cacheTypeForEnterView, (View) this, false, this.animatedEmojiStack, staticLayout);
            return;
        }
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate == null || !chatMessageCellDelegate.canDrawOutboundsContent()) {
            z = true;
        }
        this.animatedEmojiStack = AnimatedEmojiSpan.update(cacheTypeForEnterView, this, z, this.animatedEmojiStack, this.currentMessageObject.textLayoutBlocks);
    }

    private void updateCaptionSpoilers() {
        this.captionSpoilersPool.addAll(this.captionSpoilers);
        this.captionSpoilers.clear();
        if (this.captionLayout == null || getMessageObject().isSpoilersRevealed) {
            return;
        }
        SpoilerEffect.addSpoilers(this, this.captionLayout, this.captionSpoilersPool, this.captionSpoilers);
    }

    /* JADX WARN: Removed duplicated region for block: B:197:0x00da  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isUserDataChanged() {
        /*
            Method dump skipped, instructions count: 281
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.isUserDataChanged():boolean");
    }

    public ImageReceiver getPhotoImage() {
        return this.photoImage;
    }

    public int getNoSoundIconCenterX() {
        return this.noSoundCenterX;
    }

    public int getForwardNameCenterX() {
        float f;
        TLRPC$User tLRPC$User = this.currentUser;
        if (tLRPC$User != null && tLRPC$User.id == 0) {
            f = this.avatarImage.getCenterX();
        } else {
            f = this.forwardNameX + this.forwardNameCenterX;
        }
        return (int) f;
    }

    public int getChecksX() {
        return this.layoutWidth - AndroidUtilities.dp(SharedConfig.bubbleRadius >= 10 ? 27.3f : 25.3f);
    }

    public int getChecksY() {
        float f;
        int intrinsicHeight;
        if (this.currentMessageObject.shouldDrawWithoutBackground()) {
            f = this.drawTimeY;
            intrinsicHeight = getThemedDrawable("drawableMsgStickerCheck").getIntrinsicHeight();
        } else {
            f = this.drawTimeY;
            intrinsicHeight = Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight();
        }
        return (int) (f - intrinsicHeight);
    }

    public TLRPC$User getCurrentUser() {
        return this.currentUser;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startSpoilers);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopSpoilers);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
        cancelShakeAnimation();
        if (this.animationRunning) {
            return;
        }
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.onDetachedFromWindow();
        }
        CheckBoxBase checkBoxBase2 = this.mediaCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.onDetachedFromWindow();
        }
        if (this.pollCheckBox != null) {
            int i = 0;
            while (true) {
                CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
                if (i >= checkBoxBaseArr.length) {
                    break;
                }
                checkBoxBaseArr[i].onDetachedFromWindow();
                i++;
            }
        }
        this.attachedToWindow = false;
        this.avatarImage.onDetachedFromWindow();
        checkImageReceiversAttachState();
        if (this.addedForTest && this.currentUrl != null && this.currentWebFile != null) {
            ImageLoader.getInstance().removeTestWebFile(this.currentUrl);
            this.addedForTest = false;
        }
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        if (getDelegate() != null && getDelegate().getTextSelectionHelper() != null) {
            getDelegate().getTextSelectionHelper().onChatMessageCellDetached(this);
        }
        this.transitionParams.onDetach();
        if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
            Theme.getCurrentAudiVisualizerDrawable().setParentView(null);
        }
        ValueAnimator valueAnimator = this.statusDrawableAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.statusDrawableAnimator.cancel();
        }
        this.reactionsLayoutInBubble.onDetachFromWindow();
        this.statusDrawableAnimationInProgress = false;
        Runnable runnable = this.unregisterFlagSecure;
        if (runnable != null) {
            runnable.run();
            this.unregisterFlagSecure = null;
        }
        Drawable drawable = this.topicIconDrawable;
        if (!(drawable instanceof AnimatedEmojiDrawable)) {
            return;
        }
        ((AnimatedEmojiDrawable) drawable).removeView(new ChatMessageCell$$ExternalSyntheticLambda9(this));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startSpoilers);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopSpoilers);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            messageObject.animateComments = false;
        }
        MessageObject messageObject2 = this.messageObjectToSet;
        if (messageObject2 != null) {
            messageObject2.animateComments = false;
            setMessageContent(messageObject2, this.groupedMessagesToSet, this.bottomNearToSet, this.topNearToSet);
            this.messageObjectToSet = null;
            this.groupedMessagesToSet = null;
        }
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.onAttachedToWindow();
        }
        CheckBoxBase checkBoxBase2 = this.mediaCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.onAttachedToWindow();
        }
        if (this.pollCheckBox != null) {
            int i = 0;
            while (true) {
                CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
                if (i >= checkBoxBaseArr.length) {
                    break;
                }
                checkBoxBaseArr[i].onAttachedToWindow();
                i++;
            }
        }
        this.attachedToWindow = true;
        float f = 0.0f;
        this.animationOffsetX = 0.0f;
        this.slidingOffsetX = 0.0f;
        this.checkBoxTranslation = 0;
        updateTranslation();
        this.avatarImage.setParentView((View) getParent());
        this.avatarImage.onAttachedToWindow();
        checkImageReceiversAttachState();
        MessageObject messageObject3 = this.currentMessageObject;
        if (messageObject3 != null) {
            setAvatar(messageObject3);
        }
        int i2 = this.documentAttachType;
        if (i2 == 4 && this.autoPlayingMedia) {
            boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            this.animatingNoSoundPlaying = isPlayingMessage;
            this.animatingNoSoundProgress = isPlayingMessage ? 0.0f : 1.0f;
            this.animatingNoSound = 0;
        } else {
            this.animatingNoSoundPlaying = false;
            this.animatingNoSoundProgress = 0.0f;
            this.animatingDrawVideoImageButtonProgress = ((i2 == 4 || i2 == 2) && this.drawVideoSize) ? 1.0f : 0.0f;
        }
        if (getDelegate() != null && getDelegate().getTextSelectionHelper() != null) {
            getDelegate().getTextSelectionHelper().onChatMessageCellAttached(this);
        }
        if (this.documentAttachType == 5) {
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                f = 1.0f;
            }
            this.toSeekBarProgress = f;
        }
        this.reactionsLayoutInBubble.onAttachToWindow();
        updateFlagSecure();
        MessageObject messageObject4 = this.currentMessageObject;
        if (messageObject4 != null && messageObject4.type == 20 && this.unlockLayout != null) {
            invalidate();
        }
        Drawable drawable = this.topicIconDrawable;
        if (drawable instanceof AnimatedEmojiDrawable) {
            ((AnimatedEmojiDrawable) drawable).addView(new ChatMessageCell$$ExternalSyntheticLambda9(this));
        }
    }

    private void checkImageReceiversAttachState() {
        boolean z = true;
        boolean z2 = this.attachedToWindow && (this.visibleOnScreen || !this.shouldCheckVisibleOnScreen);
        if (z2 == this.imageReceiversAttachState) {
            return;
        }
        this.imageReceiversAttachState = z2;
        if (z2) {
            this.radialProgress.onAttachedToWindow();
            this.videoRadialProgress.onAttachedToWindow();
            if (this.pollAvatarImages != null) {
                int i = 0;
                while (true) {
                    ImageReceiver[] imageReceiverArr = this.pollAvatarImages;
                    if (i >= imageReceiverArr.length) {
                        break;
                    }
                    imageReceiverArr[i].onAttachedToWindow();
                    i++;
                }
            }
            if (this.commentAvatarImages != null) {
                int i2 = 0;
                while (true) {
                    ImageReceiver[] imageReceiverArr2 = this.commentAvatarImages;
                    if (i2 >= imageReceiverArr2.length) {
                        break;
                    }
                    imageReceiverArr2[i2].onAttachedToWindow();
                    i2++;
                }
            }
            this.replyImageReceiver.onAttachedToWindow();
            this.locationImageReceiver.onAttachedToWindow();
            if (this.photoImage.onAttachedToWindow()) {
                if (this.drawPhotoImage) {
                    updateButtonState(false, false, false);
                }
            } else {
                updateButtonState(false, false, false);
            }
            MessageObject messageObject = this.currentMessageObject;
            TLRPC$PhotoSize tLRPC$PhotoSize = null;
            if (messageObject != null && (this.isRoundVideo || messageObject.isVideo())) {
                checkVideoPlayback(true, null);
            }
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 != null && !messageObject2.mediaExists) {
                int canDownloadMedia = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject.messageOwner);
                TLRPC$Document document = this.currentMessageObject.getDocument();
                if (!(MessageObject.isStickerDocument(document) || MessageObject.isAnimatedStickerDocument(document, true) || MessageObject.isGifDocument(document) || MessageObject.isRoundVideoDocument(document))) {
                    if (document == null) {
                        tLRPC$PhotoSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                    }
                    int i3 = 2;
                    if (canDownloadMedia == 2 || (canDownloadMedia == 1 && this.currentMessageObject.isVideo())) {
                        if (document != null && !this.currentMessageObject.shouldEncryptPhotoOrVideo() && this.currentMessageObject.canStreamVideo()) {
                            FileLoader.getInstance(this.currentAccount).loadFile(document, this.currentMessageObject, 1, 10);
                        }
                    } else if (canDownloadMedia != 0) {
                        if (document != null) {
                            FileLoader fileLoader = FileLoader.getInstance(this.currentAccount);
                            MessageObject messageObject3 = this.currentMessageObject;
                            if (!MessageObject.isVideoDocument(document) || !this.currentMessageObject.shouldEncryptPhotoOrVideo()) {
                                i3 = 0;
                            }
                            fileLoader.loadFile(document, messageObject3, 1, i3);
                        } else if (tLRPC$PhotoSize != null) {
                            FileLoader fileLoader2 = FileLoader.getInstance(this.currentAccount);
                            ImageLocation forObject = ImageLocation.getForObject(tLRPC$PhotoSize, this.currentMessageObject.photoThumbsObject);
                            MessageObject messageObject4 = this.currentMessageObject;
                            fileLoader2.loadFile(forObject, messageObject4, null, 1, messageObject4.shouldEncryptPhotoOrVideo() ? 2 : 0);
                        }
                    }
                    updateButtonState(false, false, false);
                }
            }
            this.animatedEmojiReplyStack = AnimatedEmojiSpan.update(0, (View) this, false, this.animatedEmojiReplyStack, this.replyTextLayout);
            this.animatedEmojiDescriptionStack = AnimatedEmojiSpan.update(0, (View) this, false, this.animatedEmojiDescriptionStack, this.descriptionLayout);
            updateAnimatedEmojis();
            return;
        }
        this.radialProgress.onDetachedFromWindow();
        this.videoRadialProgress.onDetachedFromWindow();
        if (this.pollAvatarImages != null) {
            int i4 = 0;
            while (true) {
                ImageReceiver[] imageReceiverArr3 = this.pollAvatarImages;
                if (i4 >= imageReceiverArr3.length) {
                    break;
                }
                imageReceiverArr3[i4].onDetachedFromWindow();
                i4++;
            }
        }
        if (this.commentAvatarImages != null) {
            int i5 = 0;
            while (true) {
                ImageReceiver[] imageReceiverArr4 = this.commentAvatarImages;
                if (i5 >= imageReceiverArr4.length) {
                    break;
                }
                imageReceiverArr4[i5].onDetachedFromWindow();
                i5++;
            }
        }
        this.replyImageReceiver.onDetachedFromWindow();
        this.locationImageReceiver.onDetachedFromWindow();
        this.photoImage.onDetachedFromWindow();
        MessageObject messageObject5 = this.currentMessageObject;
        if (messageObject5 != null && !messageObject5.mediaExists && !messageObject5.putInDownloadsStore && !DownloadController.getInstance(this.currentAccount).isDownloading(this.currentMessageObject.messageOwner.id)) {
            TLRPC$Document document2 = this.currentMessageObject.getDocument();
            if (!MessageObject.isStickerDocument(document2) && !MessageObject.isAnimatedStickerDocument(document2, true) && !MessageObject.isGifDocument(document2) && !MessageObject.isRoundVideoDocument(document2)) {
                z = false;
            }
            if (!z) {
                if (document2 != null) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(document2);
                } else {
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(closestPhotoSizeWithSize);
                    }
                }
            }
        }
        AnimatedEmojiSpan.release(this, this.animatedEmojiDescriptionStack);
        AnimatedEmojiSpan.release(this, this.animatedEmojiReplyStack);
        AnimatedEmojiSpan.release(this, this.animatedEmojiStack);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(61:(2:1508|1509)(5:1543|1544|(1:(2:1557|1558)(3:1546|1547|(1:1549)(1:1550)))|1552|(1:1554)(1:1555))|1510|(37:1535|1536|579|(1:581)(5:1453|(1:1455)(1:1499)|1456|(8:1460|(2:1462|(1:1464)(1:1497))(1:1498)|1465|(1:1467)(1:1496)|1468|(1:1474)|1475|(3:1477|(3:1481|(2:1483|(1:1485))|1486)(2:(4:1489|(1:1491)|1492|(1:1494))|1495)|1487))(1:1458)|1459)|582|(3:1449|(1:1451)|1452)|586|(2:588|(1:590))|591|(3:1432|(3:1438|(1:1440)(2:1442|(1:1444))|1441)(1:1434)|1435)(3:593|594|(2:598|(1:600)(1:1423)))|601|(1:607)(1:1422)|608|(1:1421)(1:610)|611|(1:616)|617|(2:628|(1:630)(3:631|(1:635)(1:637)|636))|638|(4:640|(2:642|(3:644|(3:648|(3:653|(1:655)(1:1263)|656)(4:(1:1267)|1268|(1:1270)(1:1272)|1271)|657)(7:1273|(2:1275|(1:1277)(1:1364))(1:1365)|1278|(1:1282)(1:1363)|1283|(2:1288|(4:1315|1316|(1:1318)|(5:1325|(1:1327)|1328|(1:1330)(1:1332)|1331)(2:1333|(1:1338)(1:1339)))(2:1290|(1:(5:1297|(1:1299)|1300|(1:1302)(1:1305)|1303)(1:1306))(3:1307|(1:1309)(1:1311)|1310)))(3:1346|(1:1352)(2:(1:1357)|1358)|1353)|1304)|658)(2:1366|(2:1368|(1:1370)(3:1372|(1:1374)(1:1398)|(2:1388|(1:1392)(3:1393|(1:1395)(1:1397)|1396))(4:1376|1377|(1:1379)(1:1381)|1380)))(1:1399)))(3:1400|(1:1402)(1:1404)|1403)|1371|658)(3:1405|(1:1411)(3:1412|(2:(1:1416)|1417)|1371)|658)|660|(2:668|(1:670))(3:1253|(1:1262)|1255)|671|(1:673)|674|(1:682)|683|(1:685)|686|(4:690|(1:692)(1:1240)|693|(1:695))(3:1241|(2:1245|(1:1251))|1252)|696|(1:698)(6:1220|(3:1222|(1:1224)(1:1226)|1225)|1227|(3:1231|(1:1233)(1:1235)|1234)|1236|(1:1238)(1:1239))|699|(2:701|(4:705|(7:707|(1:709)(1:719)|710|(1:712)(1:718)|713|(1:715)(1:717)|716)|720|(3:722|(1:724)(1:726)|725)))|727|(1:731)(1:1219)|732)|1513|1514|1515|(53:1530|1521|1522|579|(0)(0)|582|(1:584)(4:1445|1449|(0)|1452)|586|(0)|591|(40:1432|(41:1436|1438|(0)(0)|1441|1435|601|(36:603|605|607|608|(32:1419|1421|611|(2:614|616)|617|(4:620|622|628|(0)(0))|638|(0)(0)|660|(23:662|664|668|(0)|671|(0)|674|(4:676|678|680|682)|683|(0)|686|(13:688|690|(0)(0)|693|(0)|696|(0)(0)|699|(0)|727|(3:729|731|732)|1219|732)|1241|(3:1243|1245|(3:1247|1249|1251))|1252|696|(0)(0)|699|(0)|727|(0)|1219|732)|1253|(2:1256|1262)|1255|671|(0)|674|(0)|683|(0)|686|(0)|1241|(0)|1252|696|(0)(0)|699|(0)|727|(0)|1219|732)|610|611|(0)|617|(0)|638|(0)(0)|660|(0)|1253|(0)|1255|671|(0)|674|(0)|683|(0)|686|(0)|1241|(0)|1252|696|(0)(0)|699|(0)|727|(0)|1219|732)|1422|608|(0)|610|611|(0)|617|(0)|638|(0)(0)|660|(0)|1253|(0)|1255|671|(0)|674|(0)|683|(0)|686|(0)|1241|(0)|1252|696|(0)(0)|699|(0)|727|(0)|1219|732)|1434|1435|601|(0)|1422|608|(0)|610|611|(0)|617|(0)|638|(0)(0)|660|(0)|1253|(0)|1255|671|(0)|674|(0)|683|(0)|686|(0)|1241|(0)|1252|696|(0)(0)|699|(0)|727|(0)|1219|732)|593|594|(39:596|598|(0)(0)|601|(0)|1422|608|(0)|610|611|(0)|617|(0)|638|(0)(0)|660|(0)|1253|(0)|1255|671|(0)|674|(0)|683|(0)|686|(0)|1241|(0)|1252|696|(0)(0)|699|(0)|727|(0)|1219|732)|1424|598|(0)(0)|601|(0)|1422|608|(0)|610|611|(0)|617|(0)|638|(0)(0)|660|(0)|1253|(0)|1255|671|(0)|674|(0)|683|(0)|686|(0)|1241|(0)|1252|696|(0)(0)|699|(0)|727|(0)|1219|732)|1518|(52:1525|1526|579|(0)(0)|582|(0)(0)|586|(0)|591|(0)|593|594|(0)|1424|598|(0)(0)|601|(0)|1422|608|(0)|610|611|(0)|617|(0)|638|(0)(0)|660|(0)|1253|(0)|1255|671|(0)|674|(0)|683|(0)|686|(0)|1241|(0)|1252|696|(0)(0)|699|(0)|727|(0)|1219|732)|1521|1522|579|(0)(0)|582|(0)(0)|586|(0)|591|(0)|593|594|(0)|1424|598|(0)(0)|601|(0)|1422|608|(0)|610|611|(0)|617|(0)|638|(0)(0)|660|(0)|1253|(0)|1255|671|(0)|674|(0)|683|(0)|686|(0)|1241|(0)|1252|696|(0)(0)|699|(0)|727|(0)|1219|732) */
    /* JADX WARN: Can't wrap try/catch for region: R(6:(3:(8:(1:3851)(1:3853)|3852|3822|3823|3824|3825|3826|3827)(1:3820)|3826|3827)|3821|3822|3823|3824|3825) */
    /* JADX WARN: Can't wrap try/catch for region: R(6:(3:(8:(1:3927)(1:3929)|3928|3903|3904|3905|3906|3907|3908)(1:3901)|3907|3908)|3902|3903|3904|3905|3906) */
    /* JADX WARN: Code restructure failed: missing block: B:10240:0x5e21, code lost:
        if (r10.button.url.startsWith("tg://resolve") != false) goto L974;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5267:0x0091, code lost:
        if (r76.isPlayingRound == (org.telegram.messenger.MediaController.getInstance().isPlayingMessage(r76.currentMessageObject) && (r7 = r76.delegate) != null && !r7.keyboardIsOpened())) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6237:0x0d95, code lost:
        if (r2.isSmall != false) goto L1563;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6342:0x0var_, code lost:
        r76.captionWidth = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6369:0x1012, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6780:0x1895, code lost:
        if ((r13.flags & 1) == 0) goto L1683;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6786:0x18a3, code lost:
        if ((r13.flags & 2) == 0) goto L1632;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6788:0x18a8, code lost:
        r4 = 0;
        r6 = 0;
        r10 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6790:0x18b3, code lost:
        if (r4 < r76.currentMessagesGroup.posArray.size()) goto L1690;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6791:0x18b5, code lost:
        r18 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6792:0x18b8, code lost:
        r14 = r76.currentMessagesGroup.posArray.get(r4);
        r15 = r14.minY;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6793:0x18c4, code lost:
        if (r15 == 0) goto L1706;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6794:0x18c6, code lost:
        r18 = r0;
        r0 = r13.minY;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6795:0x18ca, code lost:
        if (r15 == r0) goto L1700;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6796:0x18cc, code lost:
        if (r15 > r0) goto L1698;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6797:0x18ce, code lost:
        r0 = r2;
        r20 = r3;
        r21 = r7;
        r23 = r8;
        r26 = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6798:0x18da, code lost:
        r3 = r3 + (r6 - r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:6857:0x1984, code lost:
        r0 = r2;
        r20 = r3;
        r2 = r10;
        r21 = r7;
        r23 = r8;
        r7 = java.lang.Math.ceil((r14.pw / 1000.0f) * r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:6858:0x199d, code lost:
        if (r14.leftSpanOffset != 0) goto L1705;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6859:0x199f, code lost:
        r26 = r9;
        r9 = 0.0d;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6860:0x19a8, code lost:
        r26 = r9;
        r9 = java.lang.Math.ceil((r10 / 1000.0f) * r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:6861:0x19b5, code lost:
        java.lang.Double.isNaN(r2);
        r10 = (int) (r2 + (r7 + r9));
     */
    /* JADX WARN: Code restructure failed: missing block: B:6862:0x19bd, code lost:
        r18 = r0;
        r0 = r2;
        r20 = r3;
        r21 = r7;
        r23 = r8;
        r26 = r9;
        r2 = r6;
        r6 = java.lang.Math.ceil((r14.pw / 1000.0f) * r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:6863:0x19d9, code lost:
        if (r14.leftSpanOffset != 0) goto L1711;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6864:0x19db, code lost:
        r8 = 0.0d;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6865:0x19de, code lost:
        r8 = java.lang.Math.ceil((r8 / 1000.0f) * r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:6866:0x19e9, code lost:
        java.lang.Double.isNaN(r2);
        r6 = (int) (r2 + (r6 + r8));
     */
    /* JADX WARN: Code restructure failed: missing block: B:6867:0x19f0, code lost:
        r4 = r4 + 1;
        r2 = r0;
        r0 = r18;
        r3 = r20;
        r7 = r21;
        r8 = r23;
        r9 = r26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7156:0x21a9, code lost:
        if (r2 >= (r76.timeWidth + org.telegram.messenger.AndroidUtilities.dp(20 + (!r77.isOutOwner() ? 0 : 20)))) goto L2130;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7954:0x31fb, code lost:
        if (r15.messageOwner.fwd_from.from_id != null) goto L2668;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8748:0x4212, code lost:
        if (r76.isSmallImage != false) goto L3902;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8755:0x4238, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8756:0x4239, code lost:
        r54 = r3;
        r55 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8825:0x437e, code lost:
        if (r76.isSmallImage != false) goto L3821;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8832:0x439c, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8833:0x439d, code lost:
        r36 = r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9738:0x55d4, code lost:
        if (r0 == r7) goto L1085;
     */
    /* JADX WARN: Multi-variable search skipped. Vars limit reached: 7324 (expected less than 5000) */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:10001:0x5a3e  */
    /* JADX WARN: Removed duplicated region for block: B:10004:0x5a47  */
    /* JADX WARN: Removed duplicated region for block: B:10005:0x5a4d  */
    /* JADX WARN: Removed duplicated region for block: B:10015:0x5a83  */
    /* JADX WARN: Removed duplicated region for block: B:10079:0x5b09  */
    /* JADX WARN: Removed duplicated region for block: B:10086:0x5b19  */
    /* JADX WARN: Removed duplicated region for block: B:10108:0x5b50  */
    /* JADX WARN: Removed duplicated region for block: B:10127:0x5b9a  */
    /* JADX WARN: Removed duplicated region for block: B:10151:0x5bd2  */
    /* JADX WARN: Removed duplicated region for block: B:10158:0x5be5  */
    /* JADX WARN: Removed duplicated region for block: B:10171:0x5c1c  */
    /* JADX WARN: Removed duplicated region for block: B:10208:0x5d8a  */
    /* JADX WARN: Removed duplicated region for block: B:10217:0x5dc3  */
    /* JADX WARN: Removed duplicated region for block: B:10218:0x5dc4 A[Catch: Exception -> 0x5e29, TryCatch #5 {Exception -> 0x5e29, blocks: (B:10215:0x5da9, B:10243:0x5e26, B:10218:0x5dc4, B:10224:0x5dde, B:10227:0x5de7, B:10230:0x5df0, B:10233:0x5df9, B:10236:0x5e06, B:10239:0x5e15, B:10221:0x5dd1), top: B:10265:0x5da9 }] */
    /* JADX WARN: Removed duplicated region for block: B:10235:0x5e05  */
    /* JADX WARN: Removed duplicated region for block: B:10236:0x5e06 A[Catch: Exception -> 0x5e29, TryCatch #5 {Exception -> 0x5e29, blocks: (B:10215:0x5da9, B:10243:0x5e26, B:10218:0x5dc4, B:10224:0x5dde, B:10227:0x5de7, B:10230:0x5df0, B:10233:0x5df9, B:10236:0x5e06, B:10239:0x5e15, B:10221:0x5dd1), top: B:10265:0x5da9 }] */
    /* JADX WARN: Removed duplicated region for block: B:10246:0x5e41  */
    /* JADX WARN: Removed duplicated region for block: B:10255:0x4148 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:10276:0x52b0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:10295:0x0efa A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:10323:0x020c A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:10431:0x04bc A[EDGE_INSN: B:10431:0x04bc->B:5640:0x04bc ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:5303:0x00e1  */
    /* JADX WARN: Removed duplicated region for block: B:5304:0x00e3  */
    /* JADX WARN: Removed duplicated region for block: B:5307:0x00e7  */
    /* JADX WARN: Removed duplicated region for block: B:5314:0x00f9  */
    /* JADX WARN: Removed duplicated region for block: B:5346:0x0133  */
    /* JADX WARN: Removed duplicated region for block: B:5353:0x0143  */
    /* JADX WARN: Removed duplicated region for block: B:5358:0x0156  */
    /* JADX WARN: Removed duplicated region for block: B:5359:0x015a  */
    /* JADX WARN: Removed duplicated region for block: B:5371:0x017c  */
    /* JADX WARN: Removed duplicated region for block: B:5378:0x0187  */
    /* JADX WARN: Removed duplicated region for block: B:5383:0x0194  */
    /* JADX WARN: Removed duplicated region for block: B:5384:0x0196  */
    /* JADX WARN: Removed duplicated region for block: B:5389:0x01a1  */
    /* JADX WARN: Removed duplicated region for block: B:5410:0x01c9 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5436:0x020e  */
    /* JADX WARN: Removed duplicated region for block: B:5441:0x0223  */
    /* JADX WARN: Removed duplicated region for block: B:5448:0x023c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5452:0x0247  */
    /* JADX WARN: Removed duplicated region for block: B:5453:0x0249  */
    /* JADX WARN: Removed duplicated region for block: B:5456:0x0259  */
    /* JADX WARN: Removed duplicated region for block: B:5457:0x025b  */
    /* JADX WARN: Removed duplicated region for block: B:5461:0x0263 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5484:0x02a8 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5490:0x02b6  */
    /* JADX WARN: Removed duplicated region for block: B:5497:0x02d6  */
    /* JADX WARN: Removed duplicated region for block: B:5501:0x02dd  */
    /* JADX WARN: Removed duplicated region for block: B:5516:0x02fa  */
    /* JADX WARN: Removed duplicated region for block: B:5528:0x031b  */
    /* JADX WARN: Removed duplicated region for block: B:5543:0x0370  */
    /* JADX WARN: Removed duplicated region for block: B:5544:0x0372  */
    /* JADX WARN: Removed duplicated region for block: B:5548:0x0384  */
    /* JADX WARN: Removed duplicated region for block: B:5561:0x039c  */
    /* JADX WARN: Removed duplicated region for block: B:5580:0x03c2  */
    /* JADX WARN: Removed duplicated region for block: B:5592:0x03db  */
    /* JADX WARN: Removed duplicated region for block: B:5593:0x03df  */
    /* JADX WARN: Removed duplicated region for block: B:5597:0x0403  */
    /* JADX WARN: Removed duplicated region for block: B:5618:0x042b A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5637:0x0493  */
    /* JADX WARN: Removed duplicated region for block: B:5641:0x04be  */
    /* JADX WARN: Removed duplicated region for block: B:5651:0x0513 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5659:0x0528  */
    /* JADX WARN: Removed duplicated region for block: B:5669:0x057d  */
    /* JADX WARN: Removed duplicated region for block: B:5676:0x058d  */
    /* JADX WARN: Removed duplicated region for block: B:5680:0x0599 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5689:0x05d1  */
    /* JADX WARN: Removed duplicated region for block: B:5698:0x05e3  */
    /* JADX WARN: Removed duplicated region for block: B:5703:0x05fa  */
    /* JADX WARN: Removed duplicated region for block: B:5704:0x05fc  */
    /* JADX WARN: Removed duplicated region for block: B:5707:0x0600  */
    /* JADX WARN: Removed duplicated region for block: B:5720:0x061f  */
    /* JADX WARN: Removed duplicated region for block: B:5735:0x0648  */
    /* JADX WARN: Removed duplicated region for block: B:5736:0x0650  */
    /* JADX WARN: Removed duplicated region for block: B:5743:0x0667  */
    /* JADX WARN: Removed duplicated region for block: B:5756:0x0684  */
    /* JADX WARN: Removed duplicated region for block: B:5763:0x06ad A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5781:0x06dc  */
    /* JADX WARN: Removed duplicated region for block: B:5786:0x06e8 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5792:0x0727  */
    /* JADX WARN: Removed duplicated region for block: B:5793:0x0729  */
    /* JADX WARN: Removed duplicated region for block: B:5798:0x0735  */
    /* JADX WARN: Removed duplicated region for block: B:5801:0x0746  */
    /* JADX WARN: Removed duplicated region for block: B:5841:0x080d  */
    /* JADX WARN: Removed duplicated region for block: B:5844:0x082e  */
    /* JADX WARN: Removed duplicated region for block: B:5847:0x0847  */
    /* JADX WARN: Removed duplicated region for block: B:5848:0x084a  */
    /* JADX WARN: Removed duplicated region for block: B:5851:0x0854  */
    /* JADX WARN: Removed duplicated region for block: B:5856:0x088e  */
    /* JADX WARN: Removed duplicated region for block: B:5862:0x089a  */
    /* JADX WARN: Removed duplicated region for block: B:5865:0x08a6  */
    /* JADX WARN: Removed duplicated region for block: B:5866:0x08ad  */
    /* JADX WARN: Removed duplicated region for block: B:5871:0x08ec  */
    /* JADX WARN: Removed duplicated region for block: B:5992:0x0a60  */
    /* JADX WARN: Removed duplicated region for block: B:5995:0x0a6b  */
    /* JADX WARN: Removed duplicated region for block: B:6023:0x0aab  */
    /* JADX WARN: Removed duplicated region for block: B:6031:0x0ade  */
    /* JADX WARN: Removed duplicated region for block: B:6034:0x0ae8  */
    /* JADX WARN: Removed duplicated region for block: B:6066:0x0b52  */
    /* JADX WARN: Removed duplicated region for block: B:6077:0x0b68  */
    /* JADX WARN: Removed duplicated region for block: B:6084:0x0b7b  */
    /* JADX WARN: Removed duplicated region for block: B:6096:0x0b96  */
    /* JADX WARN: Removed duplicated region for block: B:6115:0x0bc7  */
    /* JADX WARN: Removed duplicated region for block: B:6121:0x0bd2 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6133:0x0bef  */
    /* JADX WARN: Removed duplicated region for block: B:6153:0x0c3f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6159:0x0c4f  */
    /* JADX WARN: Removed duplicated region for block: B:6166:0x0c6a  */
    /* JADX WARN: Removed duplicated region for block: B:6175:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:6176:0x0c8e  */
    /* JADX WARN: Removed duplicated region for block: B:6186:0x0cbc  */
    /* JADX WARN: Removed duplicated region for block: B:6189:0x0ce9  */
    /* JADX WARN: Removed duplicated region for block: B:6190:0x0cec  */
    /* JADX WARN: Removed duplicated region for block: B:6193:0x0cf4  */
    /* JADX WARN: Removed duplicated region for block: B:6194:0x0cf6  */
    /* JADX WARN: Removed duplicated region for block: B:6198:0x0d03  */
    /* JADX WARN: Removed duplicated region for block: B:6201:0x0d0a  */
    /* JADX WARN: Removed duplicated region for block: B:6212:0x0d39  */
    /* JADX WARN: Removed duplicated region for block: B:6223:0x0d5e  */
    /* JADX WARN: Removed duplicated region for block: B:6245:0x0daf  */
    /* JADX WARN: Removed duplicated region for block: B:6247:0x0dbe  */
    /* JADX WARN: Removed duplicated region for block: B:6380:0x1023  */
    /* JADX WARN: Removed duplicated region for block: B:6381:0x1029  */
    /* JADX WARN: Removed duplicated region for block: B:6440:0x1148 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6441:0x114a  */
    /* JADX WARN: Removed duplicated region for block: B:6451:0x1162  */
    /* JADX WARN: Removed duplicated region for block: B:6458:0x117b  */
    /* JADX WARN: Removed duplicated region for block: B:6464:0x119e A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6476:0x11e5  */
    /* JADX WARN: Removed duplicated region for block: B:6477:0x11e7  */
    /* JADX WARN: Removed duplicated region for block: B:6485:0x124b  */
    /* JADX WARN: Removed duplicated region for block: B:6499:0x1267  */
    /* JADX WARN: Removed duplicated region for block: B:6500:0x127d  */
    /* JADX WARN: Removed duplicated region for block: B:6504:0x12ad  */
    /* JADX WARN: Removed duplicated region for block: B:6514:0x12be A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6523:0x12ce A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6532:0x12de A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6548:0x1309  */
    /* JADX WARN: Removed duplicated region for block: B:6549:0x130d  */
    /* JADX WARN: Removed duplicated region for block: B:6559:0x1320  */
    /* JADX WARN: Removed duplicated region for block: B:6748:0x178b  */
    /* JADX WARN: Removed duplicated region for block: B:7107:0x1var_  */
    /* JADX WARN: Removed duplicated region for block: B:7164:0x21bd  */
    /* JADX WARN: Removed duplicated region for block: B:7202:0x23cc  */
    /* JADX WARN: Removed duplicated region for block: B:7203:0x23d8  */
    /* JADX WARN: Removed duplicated region for block: B:7216:0x23f7  */
    /* JADX WARN: Removed duplicated region for block: B:7224:0x2420  */
    /* JADX WARN: Removed duplicated region for block: B:7229:0x243b  */
    /* JADX WARN: Removed duplicated region for block: B:7230:0x243e  */
    /* JADX WARN: Removed duplicated region for block: B:7234:0x2458  */
    /* JADX WARN: Removed duplicated region for block: B:7255:0x24ab  */
    /* JADX WARN: Removed duplicated region for block: B:7259:0x24be  */
    /* JADX WARN: Removed duplicated region for block: B:7265:0x24f3  */
    /* JADX WARN: Removed duplicated region for block: B:7448:0x27e4  */
    /* JADX WARN: Removed duplicated region for block: B:7459:0x27fa  */
    /* JADX WARN: Removed duplicated region for block: B:7472:0x281f  */
    /* JADX WARN: Removed duplicated region for block: B:7476:0x283d  */
    /* JADX WARN: Removed duplicated region for block: B:7480:0x284d  */
    /* JADX WARN: Removed duplicated region for block: B:7493:0x287b  */
    /* JADX WARN: Removed duplicated region for block: B:7497:0x2893  */
    /* JADX WARN: Removed duplicated region for block: B:7502:0x28a0  */
    /* JADX WARN: Removed duplicated region for block: B:7518:0x28d9  */
    /* JADX WARN: Removed duplicated region for block: B:7519:0x28db  */
    /* JADX WARN: Removed duplicated region for block: B:7523:0x28f1  */
    /* JADX WARN: Removed duplicated region for block: B:7526:0x2900  */
    /* JADX WARN: Removed duplicated region for block: B:7527:0x2902  */
    /* JADX WARN: Removed duplicated region for block: B:7553:0x2961  */
    /* JADX WARN: Removed duplicated region for block: B:7586:0x29d0  */
    /* JADX WARN: Removed duplicated region for block: B:7906:0x308d  */
    /* JADX WARN: Removed duplicated region for block: B:7915:0x30cc  */
    /* JADX WARN: Removed duplicated region for block: B:7924:0x3100  */
    /* JADX WARN: Removed duplicated region for block: B:7925:0x3109  */
    /* JADX WARN: Removed duplicated region for block: B:7965:0x3214  */
    /* JADX WARN: Removed duplicated region for block: B:7970:0x3231  */
    /* JADX WARN: Removed duplicated region for block: B:7978:0x3265  */
    /* JADX WARN: Removed duplicated region for block: B:7987:0x327d  */
    /* JADX WARN: Removed duplicated region for block: B:7991:0x329d  */
    /* JADX WARN: Removed duplicated region for block: B:7995:0x32ad  */
    /* JADX WARN: Removed duplicated region for block: B:7999:0x32c0  */
    /* JADX WARN: Removed duplicated region for block: B:8127:0x354b  */
    /* JADX WARN: Removed duplicated region for block: B:8132:0x3559  */
    /* JADX WARN: Removed duplicated region for block: B:8135:0x3566  */
    /* JADX WARN: Removed duplicated region for block: B:8144:0x35ad  */
    /* JADX WARN: Removed duplicated region for block: B:8148:0x35b3  */
    /* JADX WARN: Removed duplicated region for block: B:8178:0x3676  */
    /* JADX WARN: Removed duplicated region for block: B:8181:0x3685  */
    /* JADX WARN: Removed duplicated region for block: B:8188:0x36b9  */
    /* JADX WARN: Removed duplicated region for block: B:8192:0x36c3  */
    /* JADX WARN: Removed duplicated region for block: B:8198:0x36ff  */
    /* JADX WARN: Removed duplicated region for block: B:8252:0x38aa  */
    /* JADX WARN: Removed duplicated region for block: B:8421:0x3bb7 A[Catch: Exception -> 0x3dc7, TryCatch #27 {Exception -> 0x3dc7, blocks: (B:8421:0x3bb7, B:8424:0x3bbf, B:8427:0x3bc8, B:8434:0x3bfe, B:8441:0x3CLASSNAME, B:8448:0x3c4e, B:8444:0x3CLASSNAME, B:8447:0x3c3d, B:8437:0x3CLASSNAME, B:8440:0x3CLASSNAME, B:8430:0x3be1, B:8433:0x3bed, B:8449:0x3CLASSNAME, B:8456:0x3cb3, B:8463:0x3cdc, B:8470:0x3d03, B:8472:0x3d0a, B:8474:0x3d16, B:8473:0x3d11, B:8466:0x3ce5, B:8469:0x3cf2, B:8459:0x3cbc, B:8462:0x3cc9, B:8452:0x3CLASSNAME, B:8455:0x3c9c, B:8419:0x3bb3), top: B:10307:0x3bb3 }] */
    /* JADX WARN: Removed duplicated region for block: B:8449:0x3CLASSNAME A[Catch: Exception -> 0x3dc7, TryCatch #27 {Exception -> 0x3dc7, blocks: (B:8421:0x3bb7, B:8424:0x3bbf, B:8427:0x3bc8, B:8434:0x3bfe, B:8441:0x3CLASSNAME, B:8448:0x3c4e, B:8444:0x3CLASSNAME, B:8447:0x3c3d, B:8437:0x3CLASSNAME, B:8440:0x3CLASSNAME, B:8430:0x3be1, B:8433:0x3bed, B:8449:0x3CLASSNAME, B:8456:0x3cb3, B:8463:0x3cdc, B:8470:0x3d03, B:8472:0x3d0a, B:8474:0x3d16, B:8473:0x3d11, B:8466:0x3ce5, B:8469:0x3cf2, B:8459:0x3cbc, B:8462:0x3cc9, B:8452:0x3CLASSNAME, B:8455:0x3c9c, B:8419:0x3bb3), top: B:10307:0x3bb3 }] */
    /* JADX WARN: Removed duplicated region for block: B:8506:0x3dd1  */
    /* JADX WARN: Removed duplicated region for block: B:8514:0x3de1  */
    /* JADX WARN: Removed duplicated region for block: B:8524:0x3e2a  */
    /* JADX WARN: Removed duplicated region for block: B:8532:0x3e66  */
    /* JADX WARN: Removed duplicated region for block: B:8539:0x3e88  */
    /* JADX WARN: Removed duplicated region for block: B:8550:0x3eb3  */
    /* JADX WARN: Removed duplicated region for block: B:8554:0x3ee3  */
    /* JADX WARN: Removed duplicated region for block: B:8561:0x3ef6  */
    /* JADX WARN: Removed duplicated region for block: B:8569:0x3var_  */
    /* JADX WARN: Removed duplicated region for block: B:8572:0x3f2f  */
    /* JADX WARN: Removed duplicated region for block: B:8578:0x3f4c  */
    /* JADX WARN: Removed duplicated region for block: B:8581:0x3var_  */
    /* JADX WARN: Removed duplicated region for block: B:8594:0x3fc4  */
    /* JADX WARN: Removed duplicated region for block: B:8608:0x3ff5 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:8609:0x3ff7  */
    /* JADX WARN: Removed duplicated region for block: B:8615:0x4009  */
    /* JADX WARN: Removed duplicated region for block: B:8616:0x400c  */
    /* JADX WARN: Removed duplicated region for block: B:8622:0x401c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:8631:0x4045  */
    /* JADX WARN: Removed duplicated region for block: B:8658:0x4092  */
    /* JADX WARN: Removed duplicated region for block: B:8683:0x40bb A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:8692:0x40e5  */
    /* JADX WARN: Removed duplicated region for block: B:8702:0x4112  */
    /* JADX WARN: Removed duplicated region for block: B:8705:0x411e  */
    /* JADX WARN: Removed duplicated region for block: B:8706:0x4123  */
    /* JADX WARN: Removed duplicated region for block: B:8710:0x412f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:8714:0x4138  */
    /* JADX WARN: Removed duplicated region for block: B:8789:0x42c8  */
    /* JADX WARN: Removed duplicated region for block: B:8790:0x42cb  */
    /* JADX WARN: Removed duplicated region for block: B:8857:0x43e5  */
    /* JADX WARN: Removed duplicated region for block: B:8863:0x43f7 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:8867:0x4400  */
    /* JADX WARN: Removed duplicated region for block: B:8868:0x4401 A[Catch: Exception -> 0x44ab, TRY_LEAVE, TryCatch #6 {Exception -> 0x44ab, blocks: (B:8865:0x43fc, B:8873:0x4426, B:8878:0x4449, B:8879:0x445e, B:8881:0x4486, B:8882:0x4495, B:8877:0x442d, B:8868:0x4401), top: B:10267:0x43fc }] */
    /* JADX WARN: Removed duplicated region for block: B:8881:0x4486 A[Catch: Exception -> 0x44ab, TryCatch #6 {Exception -> 0x44ab, blocks: (B:8865:0x43fc, B:8873:0x4426, B:8878:0x4449, B:8879:0x445e, B:8881:0x4486, B:8882:0x4495, B:8877:0x442d, B:8868:0x4401), top: B:10267:0x43fc }] */
    /* JADX WARN: Removed duplicated region for block: B:8882:0x4495 A[Catch: Exception -> 0x44ab, TRY_LEAVE, TryCatch #6 {Exception -> 0x44ab, blocks: (B:8865:0x43fc, B:8873:0x4426, B:8878:0x4449, B:8879:0x445e, B:8881:0x4486, B:8882:0x4495, B:8877:0x442d, B:8868:0x4401), top: B:10267:0x43fc }] */
    /* JADX WARN: Removed duplicated region for block: B:8894:0x44b4  */
    /* JADX WARN: Removed duplicated region for block: B:8912:0x44f6  */
    /* JADX WARN: Removed duplicated region for block: B:8913:0x44f9  */
    /* JADX WARN: Removed duplicated region for block: B:8917:0x451f  */
    /* JADX WARN: Removed duplicated region for block: B:8918:0x4522  */
    /* JADX WARN: Removed duplicated region for block: B:8927:0x4576 A[Catch: Exception -> 0x45fb, TryCatch #29 {Exception -> 0x45fb, blocks: (B:8895:0x44b5, B:8910:0x44ec, B:8914:0x44fd, B:8920:0x4535, B:8921:0x455d, B:8923:0x4565, B:8924:0x456c, B:8927:0x4576, B:8936:0x4591, B:8948:0x45b4, B:8949:0x45b7, B:8959:0x45d1, B:8957:0x45c9, B:8954:0x45bf, B:8945:0x45af, B:8938:0x45a4, B:8930:0x4585, B:8933:0x458a, B:8960:0x45d8, B:8963:0x45e7, B:8965:0x45eb, B:8966:0x45f3, B:8908:0x44e8, B:8915:0x450a, B:8919:0x4526, B:8901:0x44d4, B:8898:0x44c1), top: B:10310:0x44b5 }] */
    /* JADX WARN: Removed duplicated region for block: B:8951:0x45bb  */
    /* JADX WARN: Removed duplicated region for block: B:8952:0x45bc  */
    /* JADX WARN: Removed duplicated region for block: B:8960:0x45d8 A[Catch: Exception -> 0x45fb, TryCatch #29 {Exception -> 0x45fb, blocks: (B:8895:0x44b5, B:8910:0x44ec, B:8914:0x44fd, B:8920:0x4535, B:8921:0x455d, B:8923:0x4565, B:8924:0x456c, B:8927:0x4576, B:8936:0x4591, B:8948:0x45b4, B:8949:0x45b7, B:8959:0x45d1, B:8957:0x45c9, B:8954:0x45bf, B:8945:0x45af, B:8938:0x45a4, B:8930:0x4585, B:8933:0x458a, B:8960:0x45d8, B:8963:0x45e7, B:8965:0x45eb, B:8966:0x45f3, B:8908:0x44e8, B:8915:0x450a, B:8919:0x4526, B:8901:0x44d4, B:8898:0x44c1), top: B:10310:0x44b5 }] */
    /* JADX WARN: Removed duplicated region for block: B:8973:0x4602  */
    /* JADX WARN: Removed duplicated region for block: B:8981:0x4614  */
    /* JADX WARN: Removed duplicated region for block: B:8983:0x4617  */
    /* JADX WARN: Removed duplicated region for block: B:9006:0x4670  */
    /* JADX WARN: Removed duplicated region for block: B:9007:0x4672  */
    /* JADX WARN: Removed duplicated region for block: B:9010:0x467f  */
    /* JADX WARN: Removed duplicated region for block: B:9011:0x4681  */
    /* JADX WARN: Removed duplicated region for block: B:9014:0x468a  */
    /* JADX WARN: Removed duplicated region for block: B:9015:0x468d  */
    /* JADX WARN: Removed duplicated region for block: B:9019:0x469e  */
    /* JADX WARN: Removed duplicated region for block: B:9021:0x46a9  */
    /* JADX WARN: Removed duplicated region for block: B:9263:0x4bd7  */
    /* JADX WARN: Removed duplicated region for block: B:9308:0x4c5a  */
    /* JADX WARN: Removed duplicated region for block: B:9311:0x4c6e  */
    /* JADX WARN: Removed duplicated region for block: B:9334:0x4cad  */
    /* JADX WARN: Removed duplicated region for block: B:9338:0x4cb8  */
    /* JADX WARN: Removed duplicated region for block: B:9344:0x4cc9  */
    /* JADX WARN: Removed duplicated region for block: B:9345:0x4ccb  */
    /* JADX WARN: Removed duplicated region for block: B:9348:0x4cda  */
    /* JADX WARN: Removed duplicated region for block: B:9351:0x4ce2  */
    /* JADX WARN: Removed duplicated region for block: B:9358:0x4cf1  */
    /* JADX WARN: Removed duplicated region for block: B:9361:0x4cff  */
    /* JADX WARN: Removed duplicated region for block: B:9370:0x4d12  */
    /* JADX WARN: Removed duplicated region for block: B:9374:0x4d25  */
    /* JADX WARN: Removed duplicated region for block: B:9386:0x4d54  */
    /* JADX WARN: Removed duplicated region for block: B:9390:0x4d62  */
    /* JADX WARN: Removed duplicated region for block: B:9394:0x4d6a  */
    /* JADX WARN: Removed duplicated region for block: B:9398:0x4d83  */
    /* JADX WARN: Removed duplicated region for block: B:9401:0x4d8f  */
    /* JADX WARN: Removed duplicated region for block: B:9402:0x4d94  */
    /* JADX WARN: Removed duplicated region for block: B:9413:0x4dcf  */
    /* JADX WARN: Removed duplicated region for block: B:9414:0x4de1  */
    /* JADX WARN: Removed duplicated region for block: B:9422:0x4e13  */
    /* JADX WARN: Removed duplicated region for block: B:9428:0x4e71  */
    /* JADX WARN: Removed duplicated region for block: B:9569:0x5278  */
    /* JADX WARN: Removed duplicated region for block: B:9574:0x529f  */
    /* JADX WARN: Removed duplicated region for block: B:9600:0x52ea  */
    /* JADX WARN: Removed duplicated region for block: B:9605:0x534a  */
    /* JADX WARN: Removed duplicated region for block: B:9606:0x534d  */
    /* JADX WARN: Removed duplicated region for block: B:9628:0x5443  */
    /* JADX WARN: Removed duplicated region for block: B:9638:0x546a  */
    /* JADX WARN: Removed duplicated region for block: B:9713:0x5561  */
    /* JADX WARN: Removed duplicated region for block: B:9714:0x5563  */
    /* JADX WARN: Removed duplicated region for block: B:9718:0x557a A[Catch: Exception -> 0x55bf, TryCatch #34 {Exception -> 0x55bf, blocks: (B:9716:0x5567, B:9718:0x557a, B:9720:0x5595), top: B:10320:0x5567 }] */
    /* JADX WARN: Removed duplicated region for block: B:9720:0x5595 A[Catch: Exception -> 0x55bf, TRY_LEAVE, TryCatch #34 {Exception -> 0x55bf, blocks: (B:9716:0x5567, B:9718:0x557a, B:9720:0x5595), top: B:10320:0x5567 }] */
    /* JADX WARN: Removed duplicated region for block: B:9735:0x55cc  */
    /* JADX WARN: Removed duplicated region for block: B:9742:0x55de A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:9743:0x55df A[Catch: Exception -> 0x56d8, TryCatch #18 {Exception -> 0x56d8, blocks: (B:9740:0x55d8, B:9750:0x560b, B:9755:0x561d, B:9762:0x5651, B:9766:0x565e, B:9772:0x5679, B:9778:0x5686, B:9782:0x5696, B:9785:0x56ad, B:9788:0x56bc, B:9781:0x5690, B:9775:0x567e, B:9769:0x5673, B:9765:0x5656, B:9758:0x5626, B:9753:0x5617, B:9759:0x5632, B:9743:0x55df, B:9746:0x55e4, B:9747:0x55fb, B:9749:0x5603, B:9790:0x56c8), top: B:10289:0x55d8 }] */
    /* JADX WARN: Removed duplicated region for block: B:9752:0x5616 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:9753:0x5617 A[Catch: Exception -> 0x56d8, TryCatch #18 {Exception -> 0x56d8, blocks: (B:9740:0x55d8, B:9750:0x560b, B:9755:0x561d, B:9762:0x5651, B:9766:0x565e, B:9772:0x5679, B:9778:0x5686, B:9782:0x5696, B:9785:0x56ad, B:9788:0x56bc, B:9781:0x5690, B:9775:0x567e, B:9769:0x5673, B:9765:0x5656, B:9758:0x5626, B:9753:0x5617, B:9759:0x5632, B:9743:0x55df, B:9746:0x55e4, B:9747:0x55fb, B:9749:0x5603, B:9790:0x56c8), top: B:10289:0x55d8 }] */
    /* JADX WARN: Removed duplicated region for block: B:9757:0x5624  */
    /* JADX WARN: Removed duplicated region for block: B:9758:0x5626 A[Catch: Exception -> 0x56d8, TryCatch #18 {Exception -> 0x56d8, blocks: (B:9740:0x55d8, B:9750:0x560b, B:9755:0x561d, B:9762:0x5651, B:9766:0x565e, B:9772:0x5679, B:9778:0x5686, B:9782:0x5696, B:9785:0x56ad, B:9788:0x56bc, B:9781:0x5690, B:9775:0x567e, B:9769:0x5673, B:9765:0x5656, B:9758:0x5626, B:9753:0x5617, B:9759:0x5632, B:9743:0x55df, B:9746:0x55e4, B:9747:0x55fb, B:9749:0x5603, B:9790:0x56c8), top: B:10289:0x55d8 }] */
    /* JADX WARN: Removed duplicated region for block: B:9761:0x564f  */
    /* JADX WARN: Removed duplicated region for block: B:9762:0x5651 A[Catch: Exception -> 0x56d8, TryCatch #18 {Exception -> 0x56d8, blocks: (B:9740:0x55d8, B:9750:0x560b, B:9755:0x561d, B:9762:0x5651, B:9766:0x565e, B:9772:0x5679, B:9778:0x5686, B:9782:0x5696, B:9785:0x56ad, B:9788:0x56bc, B:9781:0x5690, B:9775:0x567e, B:9769:0x5673, B:9765:0x5656, B:9758:0x5626, B:9753:0x5617, B:9759:0x5632, B:9743:0x55df, B:9746:0x55e4, B:9747:0x55fb, B:9749:0x5603, B:9790:0x56c8), top: B:10289:0x55d8 }] */
    /* JADX WARN: Removed duplicated region for block: B:9790:0x56c8 A[Catch: Exception -> 0x56d8, TRY_LEAVE, TryCatch #18 {Exception -> 0x56d8, blocks: (B:9740:0x55d8, B:9750:0x560b, B:9755:0x561d, B:9762:0x5651, B:9766:0x565e, B:9772:0x5679, B:9778:0x5686, B:9782:0x5696, B:9785:0x56ad, B:9788:0x56bc, B:9781:0x5690, B:9775:0x567e, B:9769:0x5673, B:9765:0x5656, B:9758:0x5626, B:9753:0x5617, B:9759:0x5632, B:9743:0x55df, B:9746:0x55e4, B:9747:0x55fb, B:9749:0x5603, B:9790:0x56c8), top: B:10289:0x55d8 }] */
    /* JADX WARN: Removed duplicated region for block: B:9801:0x56e1  */
    /* JADX WARN: Removed duplicated region for block: B:9811:0x56ff  */
    /* JADX WARN: Removed duplicated region for block: B:9821:0x5762  */
    /* JADX WARN: Removed duplicated region for block: B:9822:0x5764  */
    /* JADX WARN: Removed duplicated region for block: B:9832:0x578a  */
    /* JADX WARN: Removed duplicated region for block: B:9833:0x578b A[Catch: Exception -> 0x57fc, TryCatch #30 {Exception -> 0x57fc, blocks: (B:9830:0x5784, B:9834:0x5794, B:9835:0x57cc, B:9839:0x57d7, B:9840:0x57da, B:9843:0x57ea, B:9845:0x57ee, B:9846:0x57f6, B:9833:0x578b), top: B:10312:0x5784 }] */
    /* JADX WARN: Removed duplicated region for block: B:9838:0x57d6  */
    /* JADX WARN: Removed duplicated region for block: B:9839:0x57d7 A[Catch: Exception -> 0x57fc, TryCatch #30 {Exception -> 0x57fc, blocks: (B:9830:0x5784, B:9834:0x5794, B:9835:0x57cc, B:9839:0x57d7, B:9840:0x57da, B:9843:0x57ea, B:9845:0x57ee, B:9846:0x57f6, B:9833:0x578b), top: B:10312:0x5784 }] */
    /* JADX WARN: Removed duplicated region for block: B:9840:0x57da A[Catch: Exception -> 0x57fc, TryCatch #30 {Exception -> 0x57fc, blocks: (B:9830:0x5784, B:9834:0x5794, B:9835:0x57cc, B:9839:0x57d7, B:9840:0x57da, B:9843:0x57ea, B:9845:0x57ee, B:9846:0x57f6, B:9833:0x578b), top: B:10312:0x5784 }] */
    /* JADX WARN: Removed duplicated region for block: B:9853:0x5805  */
    /* JADX WARN: Removed duplicated region for block: B:9863:0x5827  */
    /* JADX WARN: Removed duplicated region for block: B:9870:0x5842  */
    /* JADX WARN: Removed duplicated region for block: B:9888:0x5883  */
    /* JADX WARN: Removed duplicated region for block: B:9892:0x5894  */
    /* JADX WARN: Removed duplicated region for block: B:9903:0x58b6  */
    /* JADX WARN: Removed duplicated region for block: B:9904:0x58b8  */
    /* JADX WARN: Removed duplicated region for block: B:9907:0x58d8  */
    /* JADX WARN: Removed duplicated region for block: B:9908:0x58db  */
    /* JADX WARN: Removed duplicated region for block: B:9912:0x58e8  */
    /* JADX WARN: Removed duplicated region for block: B:9931:0x5941  */
    /* JADX WARN: Removed duplicated region for block: B:9936:0x595d  */
    /* JADX WARN: Removed duplicated region for block: B:9937:0x595f  */
    /* JADX WARN: Removed duplicated region for block: B:9944:0x596b  */
    /* JADX WARN: Removed duplicated region for block: B:9952:0x5988  */
    /* JADX WARN: Removed duplicated region for block: B:9955:0x598e  */
    /* JADX WARN: Removed duplicated region for block: B:9968:0x59ae  */
    /* JADX WARN: Removed duplicated region for block: B:9972:0x59c7  */
    /* JADX WARN: Removed duplicated region for block: B:9973:0x59ca  */
    /* JADX WARN: Removed duplicated region for block: B:9988:0x59ff  */
    /* JADX WARN: Type inference failed for: r0v614, types: [org.telegram.ui.Components.RadialProgress2] */
    /* JADX WARN: Type inference failed for: r0v622, types: [org.telegram.ui.Components.RadialProgress2] */
    /* JADX WARN: Type inference failed for: r0v623, types: [org.telegram.messenger.ImageReceiver] */
    /* JADX WARN: Type inference failed for: r13v0 */
    /* JADX WARN: Type inference failed for: r13v1 */
    /* JADX WARN: Type inference failed for: r13v102, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r13v151 */
    /* JADX WARN: Type inference failed for: r13v161 */
    /* JADX WARN: Type inference failed for: r13v2 */
    /* JADX WARN: Type inference failed for: r13v223 */
    /* JADX WARN: Type inference failed for: r13v3, types: [boolean] */
    /* JADX WARN: Type inference failed for: r13v314 */
    /* JADX WARN: Type inference failed for: r13v315 */
    /* JADX WARN: Type inference failed for: r13v326 */
    /* JADX WARN: Type inference failed for: r3v1128 */
    /* JADX WARN: Type inference failed for: r3v1129 */
    /* JADX WARN: Type inference failed for: r3v25 */
    /* JADX WARN: Type inference failed for: r3v26, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r3v34 */
    /* JADX WARN: Type inference failed for: r3v47, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r4v804, types: [org.telegram.tgnet.TLRPC$InputStickerSet] */
    /* JADX WARN: Type inference failed for: r5v157 */
    /* JADX WARN: Type inference failed for: r5v161, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r5v170 */
    /* JADX WARN: Type inference failed for: r5v85, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r8v102 */
    /* JADX WARN: Type inference failed for: r8v319, types: [org.telegram.messenger.WebFile, android.text.StaticLayout, java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v320 */
    /* JADX WARN: Type inference failed for: r8v354 */
    /* JADX WARN: Type inference failed for: r8v355 */
    /* JADX WARN: Type inference failed for: r8v356 */
    /* JADX WARN: Type inference failed for: r8v357 */
    /* JADX WARN: Type inference failed for: r8v358 */
    /* JADX WARN: Type inference failed for: r8v359 */
    /* JADX WARN: Type inference failed for: r8v360 */
    /* JADX WARN: Type inference failed for: r8v363 */
    /* JADX WARN: Type inference failed for: r8v364 */
    /* JADX WARN: Type inference failed for: r8v73, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v74, types: [org.telegram.tgnet.TLRPC$Document, java.lang.Object, org.telegram.tgnet.TLRPC$PhotoSize, android.graphics.drawable.Drawable] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setMessageContent(org.telegram.messenger.MessageObject r77, org.telegram.messenger.MessageObject.GroupedMessages r78, boolean r79, boolean r80) {
        /*
            Method dump skipped, instructions count: 24168
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageContent(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, boolean, boolean):void");
    }

    public static /* synthetic */ int lambda$setMessageContent$7(PollButton pollButton, PollButton pollButton2) {
        if (pollButton.decimal > pollButton2.decimal) {
            return -1;
        }
        if (pollButton.decimal < pollButton2.decimal) {
            return 1;
        }
        if (pollButton.decimal != pollButton2.decimal) {
            return 0;
        }
        if (pollButton.percent > pollButton2.percent) {
            return 1;
        }
        return pollButton.percent < pollButton2.percent ? -1 : 0;
    }

    private void calculateUnlockXY() {
        if (this.currentMessageObject.type != 20 || this.unlockLayout == null) {
            return;
        }
        this.unlockX = this.backgroundDrawableLeft + ((this.photoImage.getImageWidth() - this.unlockLayout.getWidth()) / 2.0f);
        this.unlockY = this.backgroundDrawableTop + this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - this.unlockLayout.getHeight()) / 2.0f);
    }

    private void updateFlagSecure() {
        Runnable runnable;
        TLRPC$Message tLRPC$Message;
        MessageObject messageObject = this.currentMessageObject;
        boolean z = (messageObject == null || (tLRPC$Message = messageObject.messageOwner) == null || (!tLRPC$Message.noforwards && !messageObject.hasRevealedExtendedMedia())) ? false : true;
        Activity findActivity = AndroidUtilities.findActivity(getContext());
        if (z && this.unregisterFlagSecure == null && findActivity != null) {
            this.unregisterFlagSecure = AndroidUtilities.registerFlagSecure(findActivity.getWindow());
        } else if (z || (runnable = this.unregisterFlagSecure) == null) {
        } else {
            runnable.run();
            this.unregisterFlagSecure = null;
        }
    }

    public void checkVideoPlayback(boolean z, Bitmap bitmap) {
        if (this.currentMessageObject.isVideo()) {
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                this.photoImage.setAllowStartAnimation(false);
                this.photoImage.stopAnimation();
                return;
            }
            this.photoImage.setAllowStartAnimation(true);
            this.photoImage.startAnimation();
            return;
        }
        if (z) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            z = playingMessageObject == null || !playingMessageObject.isRoundVideo();
        }
        this.photoImage.setAllowStartAnimation(z);
        if (bitmap != null) {
            this.photoImage.startCrossfadeFromStaticThumb(bitmap);
        }
        if (z) {
            this.photoImage.startAnimation();
        } else {
            this.photoImage.stopAnimation();
        }
    }

    private static boolean spanSupportsLongPress(CharacterStyle characterStyle) {
        return (characterStyle instanceof URLSpanMono) || (characterStyle instanceof URLSpan);
    }

    @Override // org.telegram.ui.Cells.BaseCell
    protected boolean onLongPress() {
        int i;
        int i2;
        boolean z = false;
        if (this.isRoundVideo && this.isPlayingRound && MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && ((this.lastTouchX - this.photoImage.getCenterX()) * (this.lastTouchX - this.photoImage.getCenterX())) + ((this.lastTouchY - this.photoImage.getCenterY()) * (this.lastTouchY - this.photoImage.getCenterY())) < (this.photoImage.getImageWidth() / 2.0f) * (this.photoImage.getImageWidth() / 2.0f) && (this.lastTouchX > this.photoImage.getCenterX() + (this.photoImage.getImageWidth() / 4.0f) || this.lastTouchX < this.photoImage.getCenterX() - (this.photoImage.getImageWidth() / 4.0f))) {
            boolean z2 = this.lastTouchX > this.photoImage.getCenterX();
            if (this.videoPlayerRewinder == null) {
                this.videoForwardDrawable = new VideoForwardDrawable(true);
                this.videoPlayerRewinder = new VideoPlayerRewinder() { // from class: org.telegram.ui.Cells.ChatMessageCell.4
                    {
                        ChatMessageCell.this = this;
                    }

                    @Override // org.telegram.messenger.video.VideoPlayerRewinder
                    protected void onRewindCanceled() {
                        ChatMessageCell.this.onTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
                        ChatMessageCell.this.videoForwardDrawable.setShowing(false);
                    }

                    @Override // org.telegram.messenger.video.VideoPlayerRewinder
                    protected void updateRewindProgressUi(long j, float f, boolean z3) {
                        ChatMessageCell.this.videoForwardDrawable.setTime(Math.abs(j));
                        if (z3) {
                            ChatMessageCell.this.currentMessageObject.audioProgress = f;
                            ChatMessageCell.this.updatePlayingMessageProgress();
                        }
                    }

                    @Override // org.telegram.messenger.video.VideoPlayerRewinder
                    protected void onRewindStart(boolean z3) {
                        ChatMessageCell.this.videoForwardDrawable.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() { // from class: org.telegram.ui.Cells.ChatMessageCell.4.1
                            @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
                            public void onAnimationEnd() {
                            }

                            {
                                AnonymousClass4.this = this;
                            }

                            @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
                            public void invalidate() {
                                ChatMessageCell.this.invalidate();
                            }
                        });
                        ChatMessageCell.this.videoForwardDrawable.setOneShootAnimation(false);
                        ChatMessageCell.this.videoForwardDrawable.setLeftSide(!z3);
                        ChatMessageCell.this.videoForwardDrawable.setShowing(true);
                        ChatMessageCell.this.invalidate();
                    }
                };
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            this.videoPlayerRewinder.startRewind(MediaController.getInstance().getVideoPlayer(), z2, MediaController.getInstance().getPlaybackSpeed(false));
            return false;
        }
        Drawable drawable = this.replySelector;
        if (drawable != null) {
            drawable.setState(new int[0]);
        }
        Drawable drawable2 = this.topicSelectorDrawable;
        if (drawable2 != null) {
            drawable2.setState(new int[0]);
        }
        if (this.pressedEmoji != null) {
            this.pressedEmoji = null;
        }
        LinkSpanDrawable linkSpanDrawable = this.pressedLink;
        if (linkSpanDrawable != null) {
            if (linkSpanDrawable.getSpan() instanceof URLSpanMono) {
                this.hadLongPress = true;
                this.delegate.didPressUrl(this, this.pressedLink.getSpan(), true);
                return true;
            } else if (this.pressedLink.getSpan() instanceof URLSpanNoUnderline) {
                URLSpanNoUnderline uRLSpanNoUnderline = (URLSpanNoUnderline) this.pressedLink.getSpan();
                if (ChatActivity.isClickableLink(uRLSpanNoUnderline.getURL()) || uRLSpanNoUnderline.getURL().startsWith("/")) {
                    this.hadLongPress = true;
                    this.delegate.didPressUrl(this, this.pressedLink.getSpan(), true);
                    return true;
                }
            } else if (this.pressedLink.getSpan() instanceof URLSpan) {
                this.hadLongPress = true;
                this.delegate.didPressUrl(this, this.pressedLink.getSpan(), true);
                return true;
            }
        }
        resetPressedLink(-1);
        if (this.buttonPressed != 0 || this.miniButtonPressed != 0 || this.videoButtonPressed != 0 || this.pressedBotButton != -1) {
            this.buttonPressed = 0;
            this.miniButtonPressed = 0;
            this.videoButtonPressed = 0;
            this.pressedBotButton = -1;
            invalidate();
        }
        this.linkPreviewPressed = false;
        this.sideButtonPressed = false;
        this.imagePressed = false;
        this.timePressed = false;
        this.gamePreviewPressed = false;
        if (this.pressedVoteButton != -1 || this.pollHintPressed || this.psaHintPressed || this.instantPressed || this.otherPressed || this.commentButtonPressed) {
            this.commentButtonPressed = false;
            this.instantButtonPressed = false;
            this.instantPressed = false;
            this.pressedVoteButton = -1;
            this.pollHintPressed = false;
            this.psaHintPressed = false;
            this.otherPressed = false;
            if (Build.VERSION.SDK_INT >= 21) {
                int i3 = 0;
                while (true) {
                    Drawable[] drawableArr = this.selectorDrawable;
                    if (i3 >= drawableArr.length) {
                        break;
                    }
                    if (drawableArr[i3] != null) {
                        drawableArr[i3].setState(StateSet.NOTHING);
                    }
                    i3++;
                }
            }
            invalidate();
        }
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate != null) {
            if (this.avatarPressed) {
                TLRPC$User tLRPC$User = this.currentUser;
                if (tLRPC$User != null) {
                    if (tLRPC$User.id != 0) {
                        z = chatMessageCellDelegate.didLongPressUserAvatar(this, tLRPC$User, this.lastTouchX, this.lastTouchY);
                    }
                } else {
                    TLRPC$Chat tLRPC$Chat = this.currentChat;
                    if (tLRPC$Chat != null) {
                        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
                        if (tLRPC$MessageFwdHeader != null) {
                            if ((tLRPC$MessageFwdHeader.flags & 16) != 0) {
                                i2 = tLRPC$MessageFwdHeader.saved_from_msg_id;
                            } else {
                                i2 = tLRPC$MessageFwdHeader.channel_post;
                            }
                            i = i2;
                        } else {
                            i = 0;
                        }
                        z = chatMessageCellDelegate.didLongPressChannelAvatar(this, tLRPC$Chat, i, this.lastTouchX, this.lastTouchY);
                    }
                }
            }
            if (!z) {
                this.delegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
            }
        }
        return true;
    }

    public void showHintButton(boolean z, boolean z2, int i) {
        float f = 1.0f;
        if (i == -1 || i == 0) {
            if (this.hintButtonVisible == z) {
                return;
            }
            this.hintButtonVisible = z;
            if (!z2) {
                this.hintButtonProgress = z ? 1.0f : 0.0f;
            } else {
                invalidate();
            }
        }
        if ((i == -1 || i == 1) && this.psaButtonVisible != z) {
            this.psaButtonVisible = z;
            if (!z2) {
                if (!z) {
                    f = 0.0f;
                }
                this.psaButtonProgress = f;
                return;
            }
            setInvalidatesParent(true);
            invalidate();
        }
    }

    public void setCheckPressed(boolean z, boolean z2) {
        this.isCheckPressed = z;
        this.isPressed = z2;
        updateRadialProgressBackground();
        if (this.useSeekBarWaveform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
    }

    public void setInvalidateSpoilersParent(boolean z) {
        this.invalidateSpoilersParent = z;
    }

    public void setInvalidatesParent(boolean z) {
        this.invalidatesParent = z;
    }

    private boolean invalidateParentForce() {
        return !this.links.isEmpty() || !this.reactionsLayoutInBubble.isEmpty;
    }

    public void invalidateOutbounds() {
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate == null || !chatMessageCellDelegate.canDrawOutboundsContent()) {
            if (!(getParent() instanceof View)) {
                return;
            }
            ((View) getParent()).invalidate();
            return;
        }
        super.invalidate();
    }

    @Override // android.view.View, org.telegram.ui.Cells.TextSelectionHelper.SelectableView
    public void invalidate() {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (this.currentMessageObject == null) {
            return;
        }
        super.invalidate();
        if ((this.invalidatesParent || (this.currentMessagesGroup != null && invalidateParentForce())) && getParent() != null) {
            View view = (View) getParent();
            if (view.getParent() != null) {
                view.invalidate();
                ((View) view.getParent()).invalidate();
            }
        }
        if (!this.isBlurred || (chatMessageCellDelegate = this.delegate) == null) {
            return;
        }
        chatMessageCellDelegate.invalidateBlur();
    }

    @Override // android.view.View
    public void invalidate(int i, int i2, int i3, int i4) {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (this.currentMessageObject == null) {
            return;
        }
        super.invalidate(i, i2, i3, i4);
        if (this.invalidatesParent && getParent() != null) {
            ((View) getParent()).invalidate(((int) getX()) + i, ((int) getY()) + i2, ((int) getX()) + i3, ((int) getY()) + i4);
        }
        if (!this.isBlurred || (chatMessageCellDelegate = this.delegate) == null) {
            return;
        }
        chatMessageCellDelegate.invalidateBlur();
    }

    public boolean isHighlightedAnimated() {
        return this.isHighlightedAnimated;
    }

    public void setHighlightedAnimated() {
        this.isHighlightedAnimated = true;
        this.highlightProgress = 1000;
        this.lastHighlightProgressTime = System.currentTimeMillis();
        invalidate();
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public boolean isHighlighted() {
        return this.isHighlighted;
    }

    public void setHighlighted(boolean z) {
        if (this.isHighlighted == z) {
            return;
        }
        this.isHighlighted = z;
        if (!z) {
            this.lastHighlightProgressTime = System.currentTimeMillis();
            this.isHighlightedAnimated = true;
            this.highlightProgress = 300;
        } else {
            this.isHighlightedAnimated = false;
            this.highlightProgress = 0;
        }
        updateRadialProgressBackground();
        if (this.useSeekBarWaveform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
        if (getParent() == null) {
            return;
        }
        ((View) getParent()).invalidate();
    }

    @Override // android.view.View
    public void setPressed(boolean z) {
        super.setPressed(z);
        updateRadialProgressBackground();
        if (this.useSeekBarWaveform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
    }

    private void updateRadialProgressBackground() {
        if (this.drawRadialCheckBackground) {
            return;
        }
        boolean z = true;
        boolean z2 = (this.isHighlighted || this.isPressed || isPressed()) && (!this.drawPhotoImage || !this.photoImage.hasBitmapImage());
        this.radialProgress.setPressed(z2 || this.buttonPressed != 0, false);
        if (this.hasMiniProgress != 0) {
            this.radialProgress.setPressed(z2 || this.miniButtonPressed != 0, true);
        }
        RadialProgress2 radialProgress2 = this.videoRadialProgress;
        if (!z2 && this.videoButtonPressed == 0) {
            z = false;
        }
        radialProgress2.setPressed(z, false);
    }

    @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
    public void onSeekBarPressed() {
        requestDisallowInterceptTouchEvent(true);
    }

    @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
    public void onSeekBarReleased() {
        requestDisallowInterceptTouchEvent(false);
    }

    @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
    public void onSeekBarDrag(float f) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.audioProgress = f;
        MediaController.getInstance().seekToProgress(this.currentMessageObject, f);
        updatePlayingMessageProgress();
    }

    @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
    public void onSeekBarContinuousDrag(float f) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.audioProgress = f;
        messageObject.audioProgressSec = (int) (messageObject.getDuration() * f);
        updatePlayingMessageProgress();
    }

    public boolean isAnimatingPollAnswer() {
        return this.animatePollAnswerAlpha;
    }

    private void updateWaveform() {
        TLRPC$Message tLRPC$Message;
        MessageObject messageObject;
        TLRPC$Message tLRPC$Message2;
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null) {
            int i = this.documentAttachType;
            if (i != 3 && i != 7) {
                return;
            }
            byte[] waveform = messageObject2.getWaveform();
            boolean z = true;
            this.useSeekBarWaveform = waveform != null;
            SeekBarWaveform seekBarWaveform = this.seekBarWaveform;
            if (seekBarWaveform != null) {
                seekBarWaveform.setWaveform(waveform);
            }
            MessageObject messageObject3 = this.currentMessageObject;
            if (messageObject3 == null || ((messageObject3.isOutOwner() && !this.currentMessageObject.isSent()) || ((!UserConfig.getInstance(this.currentAccount).isPremium() && (MessagesController.getInstance(this.currentAccount).didPressTranscribeButtonEnough() || ((((tLRPC$Message2 = (messageObject = this.currentMessageObject).messageOwner) == null || !tLRPC$Message2.voiceTranscriptionForce) && messageObject.getDuration() < 60) || MessagesController.getInstance(this.currentAccount).premiumLocked))) || (((!this.currentMessageObject.isVoice() || !this.useSeekBarWaveform) && !this.currentMessageObject.isRoundVideo()) || (tLRPC$Message = this.currentMessageObject.messageOwner) == null || (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage))))) {
                z = false;
            }
            this.useTranscribeButton = z;
            updateSeekBarWaveformWidth(null);
        }
    }

    private void updateSeekBarWaveformWidth(Canvas canvas) {
        int i;
        int i2 = 0;
        this.seekBarWaveformTranslateX = 0;
        int i3 = -AndroidUtilities.dp((this.hasLinkPreview ? 10 : 0) + 92);
        TransitionParams transitionParams = this.transitionParams;
        int i4 = 65;
        if (transitionParams.animateBackgroundBoundsInner && ((i = this.documentAttachType) == 3 || i == 7)) {
            int i5 = this.backgroundWidth;
            int i6 = (int) ((i5 - transitionParams.toDeltaLeft) + transitionParams.toDeltaRight);
            int i7 = (int) ((i5 - transitionParams.deltaLeft) + transitionParams.deltaRight);
            if (this.isRoundVideo && !this.drawBackground) {
                i7 = (int) (i7 + (getVideoTranscriptionProgress() * AndroidUtilities.dp(8.0f)));
                i6 += AndroidUtilities.dp(8.0f);
            }
            TransitionParams transitionParams2 = this.transitionParams;
            if (transitionParams2.toDeltaLeft == 0.0f && transitionParams2.toDeltaRight == 0.0f) {
                i6 = i7;
            }
            SeekBarWaveform seekBarWaveform = this.seekBarWaveform;
            if (seekBarWaveform != null) {
                if (transitionParams2.animateUseTranscribeButton) {
                    seekBarWaveform.setSize((i7 + i3) - ((int) (AndroidUtilities.dp(34.0f) * getUseTranscribeButtonProgress())), AndroidUtilities.dp(30.0f), i5 + i3 + (!this.useTranscribeButton ? -AndroidUtilities.dp(34.0f) : 0), i6 + i3 + (this.useTranscribeButton ? -AndroidUtilities.dp(34.0f) : 0));
                } else {
                    seekBarWaveform.setSize((i7 + i3) - ((int) (AndroidUtilities.dp(34.0f) * getUseTranscribeButtonProgress())), AndroidUtilities.dp(30.0f), (i5 + i3) - ((int) (AndroidUtilities.dp(34.0f) * getUseTranscribeButtonProgress())), (i6 + i3) - ((int) (AndroidUtilities.dp(34.0f) * getUseTranscribeButtonProgress())));
                }
            }
            SeekBar seekBar = this.seekBar;
            if (seekBar == null) {
                return;
            }
            int useTranscribeButtonProgress = i7 - ((int) (getUseTranscribeButtonProgress() * AndroidUtilities.dp(34.0f)));
            if (this.documentAttachType != 5) {
                i4 = 72;
            }
            if (this.hasLinkPreview) {
                i2 = 10;
            }
            seekBar.setSize(useTranscribeButtonProgress - AndroidUtilities.dp(i4 + i2), AndroidUtilities.dp(30.0f));
            return;
        }
        SeekBarWaveform seekBarWaveform2 = this.seekBarWaveform;
        if (seekBarWaveform2 != null) {
            if (transitionParams.animateUseTranscribeButton) {
                seekBarWaveform2.setSize((this.backgroundWidth + i3) - ((int) (AndroidUtilities.dp(34.0f) * getUseTranscribeButtonProgress())), AndroidUtilities.dp(30.0f), this.backgroundWidth + i3 + (!this.useTranscribeButton ? -AndroidUtilities.dp(34.0f) : 0), this.backgroundWidth + i3 + (this.useTranscribeButton ? -AndroidUtilities.dp(34.0f) : 0));
            } else {
                seekBarWaveform2.setSize((this.backgroundWidth + i3) - ((int) (AndroidUtilities.dp(34.0f) * getUseTranscribeButtonProgress())), AndroidUtilities.dp(30.0f));
            }
        }
        SeekBar seekBar2 = this.seekBar;
        if (seekBar2 == null) {
            return;
        }
        int useTranscribeButtonProgress2 = this.backgroundWidth - ((int) (getUseTranscribeButtonProgress() * AndroidUtilities.dp(34.0f)));
        if (this.documentAttachType != 5) {
            i4 = 72;
        }
        if (this.hasLinkPreview) {
            i2 = 10;
        }
        seekBar2.setSize(useTranscribeButtonProgress2 - AndroidUtilities.dp(i4 + i2), AndroidUtilities.dp(30.0f));
    }

    private int createDocumentLayout(int i, MessageObject messageObject) {
        int i2;
        int i3;
        int i4 = i;
        if (messageObject.type == 0) {
            this.documentAttach = MessageObject.getMedia(messageObject.messageOwner).webpage.document;
        } else {
            this.documentAttach = messageObject.getDocument();
        }
        TLRPC$Document tLRPC$Document = this.documentAttach;
        int i5 = 0;
        if (tLRPC$Document == null) {
            return 0;
        }
        if (MessageObject.isVoiceDocument(tLRPC$Document)) {
            this.documentAttachType = 3;
            int i6 = 0;
            while (true) {
                if (i6 >= this.documentAttach.attributes.size()) {
                    i3 = 0;
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.documentAttach.attributes.get(i6);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                    i3 = tLRPC$DocumentAttribute.duration;
                    break;
                }
                i6++;
            }
            this.widthBeforeNewTimeLine = (i4 - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil(Theme.chat_audioTimePaint.measureText("00:00")));
            this.availableTimeWidth = i4 - AndroidUtilities.dp(18.0f);
            measureTime(messageObject);
            int dp = AndroidUtilities.dp(174.0f) + this.timeWidth;
            if (!this.hasLinkPreview) {
                this.backgroundWidth = Math.min(i4, dp + ((int) Math.ceil(Theme.chat_audioTimePaint.measureText(AndroidUtilities.formatLongDuration(i3)))));
            }
            this.seekBarWaveform.setMessageObject(messageObject);
            return 0;
        } else if (MessageObject.isVideoDocument(this.documentAttach)) {
            this.documentAttachType = 4;
            if (!messageObject.needDrawBluredPreview()) {
                updatePlayingMessageProgress();
                String format = String.format("%s", AndroidUtilities.formatFileSize(this.documentAttach.size));
                this.docTitleWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(format));
                this.docTitleLayout = new StaticLayout(format, Theme.chat_infoPaint, this.docTitleWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            return 0;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
            int dp2 = i4 - AndroidUtilities.dp(92.0f);
            if (dp2 < 0) {
                dp2 = AndroidUtilities.dp(100.0f);
            }
            int i7 = dp2;
            StaticLayout staticLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicTitle().replace('\n', ' '), Theme.chat_audioTitlePaint, i7 - AndroidUtilities.dp(12.0f), TextUtils.TruncateAt.END), Theme.chat_audioTitlePaint, i7, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.songLayout = staticLayout;
            if (staticLayout.getLineCount() > 0) {
                this.songX = -((int) Math.ceil(this.songLayout.getLineLeft(0)));
            }
            StaticLayout staticLayout2 = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicAuthor().replace('\n', ' '), Theme.chat_audioPerformerPaint, i7, TextUtils.TruncateAt.END), Theme.chat_audioPerformerPaint, i7, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.performerLayout = staticLayout2;
            if (staticLayout2.getLineCount() > 0) {
                this.performerX = -((int) Math.ceil(this.performerLayout.getLineLeft(0)));
            }
            int i8 = 0;
            while (true) {
                if (i8 >= this.documentAttach.attributes.size()) {
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute2 = this.documentAttach.attributes.get(i8);
                if (tLRPC$DocumentAttribute2 instanceof TLRPC$TL_documentAttributeAudio) {
                    i5 = tLRPC$DocumentAttribute2.duration;
                    break;
                }
                i8++;
            }
            int ceil = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(AndroidUtilities.formatShortDuration(i5, i5)));
            this.widthBeforeNewTimeLine = (this.backgroundWidth - AndroidUtilities.dp(86.0f)) - ceil;
            this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.dp(28.0f);
            return ceil;
        } else if (MessageObject.isGifDocument(this.documentAttach, messageObject.hasValidGroupId())) {
            this.documentAttachType = 2;
            if (!messageObject.needDrawBluredPreview()) {
                String string = LocaleController.getString("AttachGif", R.string.AttachGif);
                this.infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(string));
                this.infoLayout = new StaticLayout(string, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                String format2 = String.format("%s", AndroidUtilities.formatFileSize(this.documentAttach.size));
                this.docTitleWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(format2));
                this.docTitleLayout = new StaticLayout(format2, Theme.chat_infoPaint, this.docTitleWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            return 0;
        } else {
            String str = this.documentAttach.mime_type;
            boolean z = (str != null && (str.toLowerCase().startsWith("image/") || this.documentAttach.mime_type.toLowerCase().startsWith("video/mp4"))) || MessageObject.isDocumentHasThumb(this.documentAttach);
            this.drawPhotoImage = z;
            if (!z) {
                i4 += AndroidUtilities.dp(30.0f);
            }
            this.documentAttachType = 1;
            String documentFileName = FileLoader.getDocumentFileName(this.documentAttach);
            if (documentFileName.length() == 0) {
                documentFileName = LocaleController.getString("AttachDocument", R.string.AttachDocument);
            }
            StaticLayout createStaticLayout = StaticLayoutEx.createStaticLayout(documentFileName, Theme.chat_docNamePaint, i4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.MIDDLE, i4, 2, false);
            this.docTitleLayout = createStaticLayout;
            this.docTitleOffsetX = Integer.MIN_VALUE;
            if (createStaticLayout != null && createStaticLayout.getLineCount() > 0) {
                int i9 = 0;
                while (i5 < this.docTitleLayout.getLineCount()) {
                    i9 = Math.max(i9, (int) Math.ceil(this.docTitleLayout.getLineWidth(i5)));
                    this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int) Math.ceil(-this.docTitleLayout.getLineLeft(i5)));
                    i5++;
                }
                i2 = Math.min(i4, i9);
            } else {
                this.docTitleOffsetX = 0;
                i2 = i4;
            }
            int dp3 = i4 - AndroidUtilities.dp(30.0f);
            TextPaint textPaint = Theme.chat_infoPaint;
            int min = Math.min(dp3, (int) Math.ceil(textPaint.measureText("000.0 mm / " + AndroidUtilities.formatFileSize(this.documentAttach.size))));
            this.infoWidth = min;
            CharSequence ellipsize = TextUtils.ellipsize(AndroidUtilities.formatFileSize(this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach), Theme.chat_infoPaint, (float) min, TextUtils.TruncateAt.END);
            try {
                if (this.infoWidth < 0) {
                    this.infoWidth = AndroidUtilities.dp(10.0f);
                }
                this.infoLayout = new StaticLayout(ellipsize, Theme.chat_infoPaint, this.infoWidth + AndroidUtilities.dp(6.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (this.drawPhotoImage) {
                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 320);
                this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 40);
                if ((DownloadController.getInstance(this.currentAccount).getAutodownloadMask() & 1) == 0) {
                    this.currentPhotoObject = null;
                }
                TLRPC$PhotoSize tLRPC$PhotoSize = this.currentPhotoObject;
                if (tLRPC$PhotoSize == null || tLRPC$PhotoSize == this.currentPhotoObjectThumb) {
                    this.currentPhotoObject = null;
                    this.photoImage.setNeedsQualityThumb(true);
                    this.photoImage.setShouldGenerateQualityThumb(true);
                } else {
                    BitmapDrawable bitmapDrawable = this.currentMessageObject.strippedThumb;
                    if (bitmapDrawable != null) {
                        this.currentPhotoObjectThumb = null;
                        this.currentPhotoObjectThumbStripped = bitmapDrawable;
                    }
                }
                this.currentPhotoFilter = "86_86_b";
                this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, messageObject.photoThumbsObject), "86_86", ImageLocation.getForObject(this.currentPhotoObjectThumb, messageObject.photoThumbsObject), this.currentPhotoFilter, this.currentPhotoObjectThumbStripped, 0L, null, messageObject, 1);
            }
            return i2;
        }
    }

    private void calcBackgroundWidth(int i, int i2, int i3) {
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        boolean z = reactionsLayoutInBubble.isEmpty;
        int i4 = (z || reactionsLayoutInBubble.isSmall) ? this.currentMessageObject.lastLineWidth : reactionsLayoutInBubble.lastLineX;
        boolean z2 = false;
        if (!z && !reactionsLayoutInBubble.isSmall) {
            if (i - i4 < i2 || this.currentMessageObject.hasRtl) {
                z2 = true;
            }
            if (this.hasInvoicePreview) {
                this.totalHeight += AndroidUtilities.dp(14.0f);
            }
        } else if (this.hasLinkPreview || this.hasOldCaptionPreview || this.hasGamePreview || this.hasInvoicePreview || i - i4 < i2 || this.currentMessageObject.hasRtl) {
            z2 = true;
        }
        if (z2) {
            this.totalHeight += AndroidUtilities.dp(14.0f);
            this.hasNewLineForTime = true;
            int max = Math.max(i3, i4) + AndroidUtilities.dp(31.0f);
            this.backgroundWidth = max;
            this.backgroundWidth = Math.max(max, (this.currentMessageObject.isOutOwner() ? this.timeWidth + AndroidUtilities.dp(17.0f) : this.timeWidth) + AndroidUtilities.dp(31.0f));
            return;
        }
        int extraTextX = (i3 - getExtraTextX()) - i4;
        if (extraTextX >= 0 && extraTextX <= i2) {
            this.backgroundWidth = ((i3 + i2) - extraTextX) + AndroidUtilities.dp(31.0f);
        } else {
            this.backgroundWidth = Math.max(i3, i4 + i2) + AndroidUtilities.dp(31.0f);
        }
    }

    public void setHighlightedText(String str) {
        MessageObject messageObject = this.messageObjectToSet;
        if (messageObject == null) {
            messageObject = this.currentMessageObject;
        }
        if (messageObject == null || messageObject.messageOwner.message == null || TextUtils.isEmpty(str)) {
            if (this.urlPathSelection.isEmpty()) {
                return;
            }
            this.linkSelectionBlockNum = -1;
            resetUrlPaths();
            invalidate();
            return;
        }
        String lowerCase = str.toLowerCase();
        String lowerCase2 = messageObject.messageOwner.message.toLowerCase();
        int length = lowerCase2.length();
        int i = -1;
        int i2 = -1;
        for (int i3 = 0; i3 < length; i3++) {
            int min = Math.min(lowerCase.length(), length - i3);
            int i4 = 0;
            for (int i5 = 0; i5 < min; i5++) {
                boolean z = lowerCase2.charAt(i3 + i5) == lowerCase.charAt(i5);
                if (z) {
                    if (i4 != 0 || i3 == 0 || " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n".indexOf(lowerCase2.charAt(i3 - 1)) >= 0) {
                        i4++;
                    } else {
                        z = false;
                    }
                }
                if (!z || i5 == min - 1) {
                    if (i4 > 0 && i4 > i2) {
                        i = i3;
                        i2 = i4;
                    }
                }
            }
        }
        if (i == -1) {
            if (this.urlPathSelection.isEmpty()) {
                return;
            }
            this.linkSelectionBlockNum = -1;
            resetUrlPaths();
            invalidate();
            return;
        }
        int length2 = lowerCase2.length();
        for (int i6 = i + i2; i6 < length2 && " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n".indexOf(lowerCase2.charAt(i6)) < 0; i6++) {
            i2++;
        }
        int i7 = i + i2;
        if (this.captionLayout != null && !TextUtils.isEmpty(messageObject.caption)) {
            resetUrlPaths();
            try {
                LinkPath obtainNewUrlPath = obtainNewUrlPath();
                obtainNewUrlPath.setCurrentLayout(this.captionLayout, i, 0.0f);
                this.captionLayout.getSelectionPath(i, i7, obtainNewUrlPath);
            } catch (Exception e) {
                FileLog.e(e);
            }
            invalidate();
        } else if (messageObject.textLayoutBlocks != null) {
            for (int i8 = 0; i8 < messageObject.textLayoutBlocks.size(); i8++) {
                MessageObject.TextLayoutBlock textLayoutBlock = messageObject.textLayoutBlocks.get(i8);
                if (i >= textLayoutBlock.charactersOffset && i < textLayoutBlock.charactersEnd) {
                    this.linkSelectionBlockNum = i8;
                    resetUrlPaths();
                    try {
                        LinkPath obtainNewUrlPath2 = obtainNewUrlPath();
                        obtainNewUrlPath2.setCurrentLayout(textLayoutBlock.textLayout, i, 0.0f);
                        textLayoutBlock.textLayout.getSelectionPath(i, i7, obtainNewUrlPath2);
                        if (i7 >= textLayoutBlock.charactersOffset + i2) {
                            for (int i9 = i8 + 1; i9 < messageObject.textLayoutBlocks.size(); i9++) {
                                MessageObject.TextLayoutBlock textLayoutBlock2 = messageObject.textLayoutBlocks.get(i9);
                                int i10 = textLayoutBlock2.charactersEnd - textLayoutBlock2.charactersOffset;
                                LinkPath obtainNewUrlPath3 = obtainNewUrlPath();
                                obtainNewUrlPath3.setCurrentLayout(textLayoutBlock2.textLayout, 0, textLayoutBlock2.height);
                                textLayoutBlock2.textLayout.getSelectionPath(0, i7 - textLayoutBlock2.charactersOffset, obtainNewUrlPath3);
                                if (i7 < (textLayoutBlock.charactersOffset + i10) - 1) {
                                    break;
                                }
                            }
                        }
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    invalidate();
                    return;
                }
            }
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        if (!super.verifyDrawable(drawable)) {
            Drawable[] drawableArr = this.selectorDrawable;
            if (drawable != drawableArr[0] && drawable != drawableArr[1]) {
                return false;
            }
        }
        return true;
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        super.invalidateDrawable(drawable);
        if (this.currentMessagesGroup != null) {
            invalidateWithParent();
        }
    }

    private boolean isCurrentLocationTimeExpired(MessageObject messageObject) {
        return MessageObject.getMedia(this.currentMessageObject.messageOwner).period % 60 == 0 ? Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) > MessageObject.getMedia(messageObject.messageOwner).period : Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) > MessageObject.getMedia(messageObject.messageOwner).period + (-5);
    }

    public void checkLocationExpired() {
        boolean isCurrentLocationTimeExpired;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || (isCurrentLocationTimeExpired = isCurrentLocationTimeExpired(messageObject)) == this.locationExpired) {
            return;
        }
        this.locationExpired = isCurrentLocationTimeExpired;
        if (!isCurrentLocationTimeExpired) {
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000L);
            this.scheduledInvalidate = true;
            int dp = this.backgroundWidth - AndroidUtilities.dp(91.0f);
            this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation), Theme.chat_locationTitlePaint, dp, TextUtils.TruncateAt.END), Theme.chat_locationTitlePaint, dp, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            return;
        }
        MessageObject messageObject2 = this.currentMessageObject;
        this.currentMessageObject = null;
        setMessageObject(messageObject2, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
    }

    public void setIsUpdating(boolean z) {
        this.isUpdating = true;
    }

    public void setMessageObject(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, boolean z, boolean z2) {
        if (this.attachedToWindow) {
            setMessageContent(messageObject, groupedMessages, z, z2);
            return;
        }
        this.messageObjectToSet = messageObject;
        this.groupedMessagesToSet = groupedMessages;
        this.bottomNearToSet = z;
        this.topNearToSet = z2;
    }

    private int getAdditionalWidthForPosition(MessageObject.GroupedMessagePosition groupedMessagePosition) {
        int i = 0;
        if (groupedMessagePosition != null) {
            if ((groupedMessagePosition.flags & 2) == 0) {
                i = 0 + AndroidUtilities.dp(4.0f);
            }
            return (groupedMessagePosition.flags & 1) == 0 ? i + AndroidUtilities.dp(4.0f) : i;
        }
        return 0;
    }

    public void createSelectorDrawable(final int i) {
        int themedColor;
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        String str = "chat_outPreviewInstantText";
        if (this.psaHintPressed) {
            themedColor = getThemedColor(this.currentMessageObject.isOutOwner() ? "chat_outViews" : "chat_inViews");
        } else {
            themedColor = getThemedColor(this.currentMessageObject.isOutOwner() ? str : "chat_inPreviewInstantText");
        }
        Drawable[] drawableArr = this.selectorDrawable;
        if (drawableArr[i] == null) {
            final Paint paint = new Paint(1);
            paint.setColor(-1);
            Drawable drawable = new Drawable() { // from class: org.telegram.ui.Cells.ChatMessageCell.5
                RectF rect = new RectF();
                Path path = new Path();

                @Override // android.graphics.drawable.Drawable
                public int getOpacity() {
                    return -2;
                }

                @Override // android.graphics.drawable.Drawable
                public void setAlpha(int i2) {
                }

                @Override // android.graphics.drawable.Drawable
                public void setColorFilter(ColorFilter colorFilter) {
                }

                {
                    ChatMessageCell.this = this;
                }

                @Override // android.graphics.drawable.Drawable
                public void draw(Canvas canvas) {
                    Rect bounds = getBounds();
                    this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                    if (ChatMessageCell.this.selectorDrawableMaskType[i] != 3 && ChatMessageCell.this.selectorDrawableMaskType[i] != 4) {
                        float f = 0.0f;
                        if (ChatMessageCell.this.selectorDrawableMaskType[i] == 2) {
                            this.path.reset();
                            boolean z = ChatMessageCell.this.currentMessageObject != null && ChatMessageCell.this.currentMessageObject.isOutOwner();
                            for (int i2 = 0; i2 < 4; i2++) {
                                if (!ChatMessageCell.this.instantTextNewLine) {
                                    if (i2 == 2 && !z) {
                                        float[] fArr = ChatMessageCell.radii;
                                        int i3 = i2 * 2;
                                        float dp = AndroidUtilities.dp(SharedConfig.bubbleRadius);
                                        ChatMessageCell.radii[i3 + 1] = dp;
                                        fArr[i3] = dp;
                                    } else if (i2 != 3 || !z) {
                                        if ((ChatMessageCell.this.mediaBackground || ChatMessageCell.this.pinnedBottom) && (i2 == 2 || i2 == 3)) {
                                            float[] fArr2 = ChatMessageCell.radii;
                                            int i4 = i2 * 2;
                                            float[] fArr3 = ChatMessageCell.radii;
                                            int i5 = i4 + 1;
                                            float dp2 = AndroidUtilities.dp(ChatMessageCell.this.pinnedBottom ? Math.min(5, SharedConfig.bubbleRadius) : SharedConfig.bubbleRadius);
                                            fArr3[i5] = dp2;
                                            fArr2[i4] = dp2;
                                        }
                                    } else {
                                        float[] fArr4 = ChatMessageCell.radii;
                                        int i6 = i2 * 2;
                                        float dp3 = AndroidUtilities.dp(SharedConfig.bubbleRadius);
                                        ChatMessageCell.radii[i6 + 1] = dp3;
                                        fArr4[i6] = dp3;
                                    }
                                }
                                float[] fArr5 = ChatMessageCell.radii;
                                int i7 = i2 * 2;
                                ChatMessageCell.radii[i7 + 1] = 0.0f;
                                fArr5[i7] = 0.0f;
                            }
                            if (!z) {
                                ChatMessageCell chatMessageCell = ChatMessageCell.this;
                                if (!chatMessageCell.drawPinnedBottom && chatMessageCell.currentPosition == null && (ChatMessageCell.this.currentMessageObject == null || ChatMessageCell.this.currentMessageObject.type != 17)) {
                                    this.path.moveTo(this.rect.left + AndroidUtilities.dp(6.0f), this.rect.top);
                                    this.path.lineTo(this.rect.left + AndroidUtilities.dp(6.0f), (this.rect.bottom - AndroidUtilities.dp(6.0f)) - AndroidUtilities.dp(5.0f));
                                    RectF rectF = AndroidUtilities.rectTmp;
                                    rectF.set(this.rect.left + AndroidUtilities.dp(-7.0f), this.rect.bottom - AndroidUtilities.dp(23.0f), this.rect.left + AndroidUtilities.dp(6.0f), this.rect.bottom);
                                    this.path.arcTo(rectF, 0.0f, 83.0f, false);
                                    RectF rectF2 = this.rect;
                                    rectF.set(this.rect.right - (ChatMessageCell.radii[4] * 2.0f), this.rect.bottom - (ChatMessageCell.radii[5] * 2.0f), rectF2.right, rectF2.bottom);
                                    this.path.arcTo(rectF, 90.0f, -90.0f, false);
                                    Path path = this.path;
                                    RectF rectF3 = this.rect;
                                    path.lineTo(rectF3.right, rectF3.top);
                                    this.path.close();
                                    this.path.close();
                                    canvas.drawPath(this.path, paint);
                                    return;
                                }
                            }
                            this.path.addRoundRect(this.rect, ChatMessageCell.radii, Path.Direction.CW);
                            this.path.close();
                            canvas.drawPath(this.path, paint);
                            return;
                        }
                        RectF rectF4 = this.rect;
                        float dp4 = ChatMessageCell.this.selectorDrawableMaskType[i] == 0 ? AndroidUtilities.dp(6.0f) : 0.0f;
                        if (ChatMessageCell.this.selectorDrawableMaskType[i] == 0) {
                            f = AndroidUtilities.dp(6.0f);
                        }
                        canvas.drawRoundRect(rectF4, dp4, f, paint);
                        return;
                    }
                    canvas.drawCircle(this.rect.centerX(), this.rect.centerY(), AndroidUtilities.dp(ChatMessageCell.this.selectorDrawableMaskType[i] == 3 ? 16.0f : 20.0f), paint);
                }
            };
            int[][] iArr = {StateSet.WILD_CARD};
            int[] iArr2 = new int[1];
            if (!this.currentMessageObject.isOutOwner()) {
                str = "chat_inPreviewInstantText";
            }
            iArr2[0] = getThemedColor(str) & NUM;
            this.selectorDrawable[i] = new RippleDrawable(new ColorStateList(iArr, iArr2), null, drawable);
            this.selectorDrawable[i].setCallback(this);
        } else {
            Theme.setSelectorDrawableColor(drawableArr[i], themedColor & NUM, true);
        }
        this.selectorDrawable[i].setVisible(true, false);
    }

    private void createInstantViewButton() {
        String string;
        int measureText;
        if (Build.VERSION.SDK_INT >= 21 && this.drawInstantView) {
            createSelectorDrawable(0);
        }
        if (!this.drawInstantView || this.instantViewLayout != null) {
            return;
        }
        this.instantWidth = AndroidUtilities.dp(33.0f);
        int i = this.drawInstantViewType;
        if (i == 12) {
            string = LocaleController.getString("OpenChannelPost", R.string.OpenChannelPost);
        } else if (i == 1) {
            string = LocaleController.getString("OpenChannel", R.string.OpenChannel);
        } else if (i == 13) {
            string = LocaleController.getString("SendMessage", R.string.SendMessage).toUpperCase();
        } else if (i == 10) {
            string = LocaleController.getString("OpenBot", R.string.OpenBot);
        } else if (i == 2) {
            string = LocaleController.getString("OpenGroup", R.string.OpenGroup);
        } else if (i == 3) {
            string = LocaleController.getString("OpenMessage", R.string.OpenMessage);
        } else if (i == 5) {
            string = LocaleController.getString("ViewContact", R.string.ViewContact);
        } else if (i == 6) {
            string = LocaleController.getString("OpenBackground", R.string.OpenBackground);
        } else if (i == 7) {
            string = LocaleController.getString("OpenTheme", R.string.OpenTheme);
        } else if (i == 8) {
            if (this.pollVoted || this.pollClosed) {
                string = LocaleController.getString("PollViewResults", R.string.PollViewResults);
            } else {
                string = LocaleController.getString("PollSubmitVotes", R.string.PollSubmitVotes);
            }
        } else if (i == 9 || i == 11) {
            TLRPC$TL_webPage tLRPC$TL_webPage = (TLRPC$TL_webPage) MessageObject.getMedia(this.currentMessageObject.messageOwner).webpage;
            if (tLRPC$TL_webPage != null && tLRPC$TL_webPage.url.contains("voicechat=")) {
                string = LocaleController.getString("VoipGroupJoinAsSpeaker", R.string.VoipGroupJoinAsSpeaker);
            } else {
                string = LocaleController.getString("VoipGroupJoinAsLinstener", R.string.VoipGroupJoinAsLinstener);
            }
        } else {
            string = LocaleController.getString("InstantView", R.string.InstantView);
        }
        if (this.currentMessageObject.isSponsored() && this.backgroundWidth < (measureText = (int) (Theme.chat_instantViewPaint.measureText(string) + AndroidUtilities.dp(75.0f)))) {
            this.backgroundWidth = measureText;
        }
        int dp = this.backgroundWidth - AndroidUtilities.dp(75.0f);
        this.instantViewLayout = new StaticLayout(TextUtils.ellipsize(string, Theme.chat_instantViewPaint, dp, TextUtils.TruncateAt.END), Theme.chat_instantViewPaint, dp + AndroidUtilities.dp(2.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        if (this.drawInstantViewType == 8) {
            this.instantWidth = this.backgroundWidth - AndroidUtilities.dp(13.0f);
        } else {
            this.instantWidth = this.backgroundWidth - AndroidUtilities.dp(34.0f);
        }
        int dp2 = this.totalHeight + AndroidUtilities.dp(46.0f);
        this.totalHeight = dp2;
        if (this.currentMessageObject.type == 12) {
            this.totalHeight = dp2 + AndroidUtilities.dp(14.0f);
        }
        if (this.currentMessageObject.isSponsored() && this.hasNewLineForTime) {
            this.totalHeight += AndroidUtilities.dp(16.0f);
        }
        StaticLayout staticLayout = this.instantViewLayout;
        if (staticLayout == null || staticLayout.getLineCount() <= 0) {
            return;
        }
        double d = this.instantWidth;
        double ceil = Math.ceil(this.instantViewLayout.getLineWidth(0));
        Double.isNaN(d);
        this.instantTextX = (((int) (d - ceil)) / 2) + (this.drawInstantViewType == 0 ? AndroidUtilities.dp(8.0f) : 0);
        int lineLeft = (int) this.instantViewLayout.getLineLeft(0);
        this.instantTextLeftX = lineLeft;
        this.instantTextX += -lineLeft;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.inLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && (messageObject.checkLayout() || this.lastHeight != AndroidUtilities.displaySize.y)) {
            this.inLayout = true;
            MessageObject messageObject2 = this.currentMessageObject;
            this.currentMessageObject = null;
            setMessageObject(messageObject2, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
            this.inLayout = false;
        }
        updateSelectionTextPosition();
        setMeasuredDimension(View.MeasureSpec.getSize(i), this.totalHeight + this.keyboardHeight);
    }

    public void forceResetMessageObject() {
        MessageObject messageObject = this.messageObjectToSet;
        if (messageObject == null) {
            messageObject = this.currentMessageObject;
        }
        this.currentMessageObject = null;
        setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
    }

    private int getGroupPhotosWidth() {
        int parentWidth = getParentWidth();
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.preview) {
            parentWidth = this.parentWidth;
        }
        if (AndroidUtilities.isInMultiwindow || !AndroidUtilities.isTablet()) {
            return parentWidth;
        }
        if (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2) {
            return parentWidth;
        }
        int i = (parentWidth / 100) * 35;
        if (i < AndroidUtilities.dp(320.0f)) {
            i = AndroidUtilities.dp(320.0f);
        }
        return parentWidth - i;
    }

    private int getExtraTextX() {
        int i = SharedConfig.bubbleRadius;
        if (i >= 15) {
            return AndroidUtilities.dp(2.0f);
        }
        if (i < 11) {
            return 0;
        }
        return AndroidUtilities.dp(1.0f);
    }

    private int getExtraTimeX() {
        int i;
        if (!this.currentMessageObject.isOutOwner() && ((!this.mediaBackground || this.captionLayout != null) && (i = SharedConfig.bubbleRadius) > 11)) {
            return AndroidUtilities.dp((i - 11) / 1.5f);
        }
        if (!this.currentMessageObject.isOutOwner() && this.isPlayingRound && this.isAvatarVisible && this.currentMessageObject.type == 5) {
            return (int) ((AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize) * 0.7f);
        }
        return 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    @SuppressLint({"DrawAllocation"})
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int dp;
        int i7;
        int dp2;
        int i8;
        int dp3;
        int i9;
        int i10;
        int dp4;
        int dp5;
        int dp6;
        int i11;
        int dp7;
        int i12;
        int i13;
        int dp8;
        int dp9;
        if (this.currentMessageObject == null) {
            return;
        }
        int measuredHeight = getMeasuredHeight() + (getMeasuredWidth() << 16);
        if (this.lastSize != measuredHeight || !this.wasLayout) {
            this.layoutWidth = getMeasuredWidth();
            this.layoutHeight = getMeasuredHeight() - this.substractBackgroundHeight;
            if (this.timeTextWidth < 0) {
                this.timeTextWidth = AndroidUtilities.dp(10.0f);
            }
            this.timeLayout = new StaticLayout(this.currentTimeString, Theme.chat_timePaint, AndroidUtilities.dp(100.0f) + this.timeTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.mediaBackground) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(42.0f);
                } else {
                    this.timeX = (this.backgroundWidth - AndroidUtilities.dp(4.0f)) - this.timeWidth;
                    if (this.currentMessageObject.isAnyKindOfSticker()) {
                        this.timeX = Math.max(AndroidUtilities.dp(26.0f), this.timeX);
                    }
                    if (this.isAvatarVisible) {
                        this.timeX += AndroidUtilities.dp(48.0f);
                    }
                    MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
                    if (groupedMessagePosition != null && (i5 = groupedMessagePosition.leftSpanOffset) != 0) {
                        this.timeX += (int) Math.ceil((i5 / 1000.0f) * getGroupPhotosWidth());
                    }
                    if (this.captionLayout != null && this.currentPosition != null) {
                        this.timeX += AndroidUtilities.dp(4.0f);
                    }
                }
                if (SharedConfig.bubbleRadius >= 10 && this.captionLayout == null && (i6 = this.documentAttachType) != 7 && i6 != 6) {
                    this.timeX -= AndroidUtilities.dp(2.0f);
                }
            } else if (this.currentMessageObject.isOutOwner()) {
                this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(38.5f);
            } else {
                this.timeX = (this.backgroundWidth - AndroidUtilities.dp(9.0f)) - this.timeWidth;
                if (this.currentMessageObject.isAnyKindOfSticker()) {
                    this.timeX = Math.max(0, this.timeX);
                }
                if (this.isAvatarVisible) {
                    this.timeX += AndroidUtilities.dp(48.0f);
                }
                if (shouldDrawTimeOnMedia()) {
                    this.timeX -= AndroidUtilities.dp(7.0f);
                }
            }
            this.timeX -= getExtraTimeX();
            if ((this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                this.viewsLayout = new StaticLayout(this.currentViewsString, Theme.chat_timePaint, this.viewsTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } else {
                this.viewsLayout = null;
            }
            if (this.currentRepliesString != null && !this.currentMessageObject.scheduled) {
                this.repliesLayout = new StaticLayout(this.currentRepliesString, Theme.chat_timePaint, this.repliesTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } else {
                this.repliesLayout = null;
            }
            if (this.isAvatarVisible) {
                this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0f), this.avatarImage.getImageY(), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            }
            if (this.currentMessageObject.type == 20 && this.currentUnlockString != null) {
                this.unlockLayout = new StaticLayout(this.currentUnlockString, Theme.chat_unlockExtendedMediaTextPaint, this.unlockTextWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                int i14 = ((TLRPC$TL_messageExtendedMediaPreview) this.currentMessageObject.messageOwner.media.extended_media).video_duration;
                if (i14 != 0) {
                    String formatDuration = AndroidUtilities.formatDuration(i14, false);
                    this.durationWidth = (int) Math.ceil(Theme.chat_durationPaint.measureText(formatDuration));
                    this.videoInfoLayout = new StaticLayout(formatDuration, Theme.chat_durationPaint, this.durationWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                } else {
                    this.videoInfoLayout = null;
                }
            } else {
                this.unlockLayout = null;
            }
            this.wasLayout = true;
        }
        this.lastSize = measuredHeight;
        if (this.currentMessageObject.type == 0) {
            this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
        }
        if (this.isRoundVideo) {
            updatePlayingMessageProgress();
        }
        int i15 = this.documentAttachType;
        if (i15 == 3 || i15 == 7) {
            if (this.currentMessageObject.isOutOwner()) {
                this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(57.0f);
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
            } else if (this.isChat && !this.isThreadPost && this.currentMessageObject.needDrawAvatar()) {
                this.seekBarX = AndroidUtilities.dp(114.0f);
                this.buttonX = AndroidUtilities.dp(71.0f);
                this.timeAudioX = AndroidUtilities.dp(124.0f);
            } else {
                this.seekBarX = AndroidUtilities.dp(66.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            updateSeekBarWaveformWidth(null);
            this.seekBarY = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            int dp10 = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp10;
            RadialProgress2 radialProgress2 = this.radialProgress;
            int i16 = this.buttonX;
            radialProgress2.setProgressRect(i16, dp10, AndroidUtilities.dp(44.0f) + i16, this.buttonY + AndroidUtilities.dp(44.0f));
            updatePlayingMessageProgress();
            if (this.documentAttachType != 7) {
                return;
            }
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject.type == 0 && (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview)) {
                if (this.hasGamePreview) {
                    i7 = this.unmovedTextX - AndroidUtilities.dp(10.0f);
                } else {
                    if (this.hasInvoicePreview) {
                        i10 = this.unmovedTextX;
                        dp4 = AndroidUtilities.dp(1.0f);
                    } else {
                        i10 = this.unmovedTextX;
                        dp4 = AndroidUtilities.dp(1.0f);
                    }
                    i7 = i10 + dp4;
                }
                if (this.isSmallImage) {
                    i8 = i7 + this.backgroundWidth;
                    dp3 = AndroidUtilities.dp(81.0f);
                    dp = i8 - dp3;
                } else {
                    dp2 = this.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f);
                    dp = i7 + dp2;
                }
            } else if (messageObject.isOutOwner()) {
                if (this.mediaBackground) {
                    i8 = this.layoutWidth - this.backgroundWidth;
                    dp3 = AndroidUtilities.dp(3.0f);
                    dp = i8 - dp3;
                } else {
                    i7 = this.layoutWidth - this.backgroundWidth;
                    dp2 = AndroidUtilities.dp(6.0f);
                    dp = i7 + dp2;
                }
            } else {
                if (this.isChat && this.isAvatarVisible && (!this.isPlayingRound || this.currentMessageObject.isVoiceTranscriptionOpen())) {
                    dp = AndroidUtilities.dp(63.0f);
                } else {
                    dp = AndroidUtilities.dp(15.0f);
                }
                MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
                if (groupedMessagePosition2 != null && !groupedMessagePosition2.edge) {
                    dp -= AndroidUtilities.dp(10.0f);
                }
            }
            MessageObject.GroupedMessagePosition groupedMessagePosition3 = this.currentPosition;
            if (groupedMessagePosition3 != null) {
                if ((groupedMessagePosition3.flags & 1) == 0) {
                    dp -= AndroidUtilities.dp(2.0f);
                }
                if (this.currentPosition.leftSpanOffset != 0) {
                    dp += (int) Math.ceil((i9 / 1000.0f) * getGroupPhotosWidth());
                }
            }
            if (this.currentMessageObject.type != 0) {
                dp -= AndroidUtilities.dp(2.0f);
            }
            if (this.currentMessageObject.isVoiceTranscriptionOpen()) {
                dp += AndroidUtilities.dp(10.0f);
            }
            TransitionParams transitionParams = this.transitionParams;
            if (transitionParams.imageChangeBoundsTransition && !transitionParams.updatePhotoImageX) {
                return;
            }
            transitionParams.updatePhotoImageX = false;
            ImageReceiver imageReceiver = this.photoImage;
            imageReceiver.setImageCoords(dp, imageReceiver.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        } else if (i15 == 5) {
            if (this.currentMessageObject.isOutOwner()) {
                this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(56.0f);
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
            } else if (this.isChat && !this.isThreadPost && this.currentMessageObject.needDrawAvatar()) {
                this.seekBarX = AndroidUtilities.dp(113.0f);
                this.buttonX = AndroidUtilities.dp(71.0f);
                this.timeAudioX = AndroidUtilities.dp(124.0f);
            } else {
                this.seekBarX = AndroidUtilities.dp(65.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            updateSeekBarWaveformWidth(null);
            this.seekBarY = AndroidUtilities.dp(29.0f) + this.namesOffset + this.mediaOffsetY;
            int dp11 = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp11;
            RadialProgress2 radialProgress22 = this.radialProgress;
            int i17 = this.buttonX;
            radialProgress22.setProgressRect(i17, dp11, AndroidUtilities.dp(44.0f) + i17, this.buttonY + AndroidUtilities.dp(44.0f));
            updatePlayingMessageProgress();
        } else if (i15 == 1 && !this.drawPhotoImage) {
            if (this.currentMessageObject.isOutOwner()) {
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
            } else if (this.isChat && !this.isThreadPost && this.currentMessageObject.needDrawAvatar()) {
                this.buttonX = AndroidUtilities.dp(71.0f);
            } else {
                this.buttonX = AndroidUtilities.dp(23.0f);
            }
            if (this.hasLinkPreview) {
                this.buttonX += AndroidUtilities.dp(10.0f);
            }
            int dp12 = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp12;
            RadialProgress2 radialProgress23 = this.radialProgress;
            int i18 = this.buttonX;
            radialProgress23.setProgressRect(i18, dp12, AndroidUtilities.dp(44.0f) + i18, this.buttonY + AndroidUtilities.dp(44.0f));
            this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0f), this.buttonY - AndroidUtilities.dp(10.0f), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        } else {
            MessageObject messageObject2 = this.currentMessageObject;
            int i19 = messageObject2.type;
            if (i19 == 12) {
                if (messageObject2.isOutOwner()) {
                    dp9 = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                } else if (this.isChat && !this.isThreadPost && this.currentMessageObject.needDrawAvatar()) {
                    dp9 = AndroidUtilities.dp(72.0f);
                } else {
                    dp9 = AndroidUtilities.dp(23.0f);
                }
                this.photoImage.setImageCoords(dp9, AndroidUtilities.dp(13.0f) + this.namesOffset, AndroidUtilities.dp(44.0f), AndroidUtilities.dp(44.0f));
                return;
            }
            if (i19 == 0 && (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview)) {
                if (this.hasGamePreview) {
                    i11 = this.unmovedTextX - AndroidUtilities.dp(10.0f);
                } else {
                    if (this.hasInvoicePreview) {
                        i13 = this.unmovedTextX;
                        dp8 = AndroidUtilities.dp(1.0f);
                    } else {
                        i13 = this.unmovedTextX;
                        dp8 = AndroidUtilities.dp(1.0f);
                    }
                    i11 = i13 + dp8;
                }
                if (this.isSmallImage) {
                    dp5 = i11 + this.backgroundWidth;
                    dp6 = AndroidUtilities.dp(81.0f);
                    dp5 -= dp6;
                } else {
                    dp7 = this.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f);
                    dp5 = i11 + dp7;
                }
            } else {
                if (messageObject2.isOutOwner()) {
                    if (this.mediaBackground) {
                        dp5 = this.layoutWidth - this.backgroundWidth;
                        dp6 = AndroidUtilities.dp(3.0f);
                    } else {
                        i11 = this.layoutWidth - this.backgroundWidth;
                        dp7 = AndroidUtilities.dp(6.0f);
                        dp5 = i11 + dp7;
                    }
                } else {
                    if (this.isChat && this.isAvatarVisible && !this.isPlayingRound) {
                        dp5 = AndroidUtilities.dp(63.0f);
                    } else {
                        dp5 = AndroidUtilities.dp(15.0f);
                    }
                    MessageObject.GroupedMessagePosition groupedMessagePosition4 = this.currentPosition;
                    if (groupedMessagePosition4 != null && !groupedMessagePosition4.edge) {
                        dp6 = AndroidUtilities.dp(10.0f);
                    }
                }
                dp5 -= dp6;
            }
            MessageObject.GroupedMessagePosition groupedMessagePosition5 = this.currentPosition;
            if (groupedMessagePosition5 != null) {
                if ((groupedMessagePosition5.flags & 1) == 0) {
                    dp5 -= AndroidUtilities.dp(2.0f);
                }
                if (this.currentPosition.leftSpanOffset != 0) {
                    dp5 += (int) Math.ceil((i12 / 1000.0f) * getGroupPhotosWidth());
                }
            }
            if (this.currentMessageObject.type != 0) {
                dp5 -= AndroidUtilities.dp(2.0f);
            }
            TransitionParams transitionParams2 = this.transitionParams;
            if (!transitionParams2.imageChangeBoundsTransition || transitionParams2.updatePhotoImageX) {
                transitionParams2.updatePhotoImageX = false;
                ImageReceiver imageReceiver2 = this.photoImage;
                imageReceiver2.setImageCoords(dp5, imageReceiver2.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
            }
            this.buttonX = (int) (dp5 + ((this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f)) / 2.0f));
            int imageY = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f)) / 2.0f));
            this.buttonY = imageY;
            RadialProgress2 radialProgress24 = this.radialProgress;
            int i20 = this.buttonX;
            radialProgress24.setProgressRect(i20, imageY, AndroidUtilities.dp(48.0f) + i20, this.buttonY + AndroidUtilities.dp(48.0f));
            this.deleteProgressRect.set(this.buttonX + AndroidUtilities.dp(5.0f), this.buttonY + AndroidUtilities.dp(5.0f), this.buttonX + AndroidUtilities.dp(43.0f), this.buttonY + AndroidUtilities.dp(43.0f));
            int i21 = this.documentAttachType;
            if (i21 != 4 && i21 != 2) {
                return;
            }
            this.videoButtonX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f));
            int imageY2 = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f));
            this.videoButtonY = imageY2;
            RadialProgress2 radialProgress25 = this.videoRadialProgress;
            int i22 = this.videoButtonX;
            radialProgress25.setProgressRect(i22, imageY2, AndroidUtilities.dp(24.0f) + i22, this.videoButtonY + AndroidUtilities.dp(24.0f));
        }
    }

    public boolean needDelayRoundProgressDraw() {
        int i = this.documentAttachType;
        return (i == 7 || i == 4) && this.currentMessageObject.type != 5 && MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
    }

    /* JADX WARN: Removed duplicated region for block: B:138:0x0069  */
    /* JADX WARN: Removed duplicated region for block: B:145:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:152:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:153:0x009d  */
    /* JADX WARN: Removed duplicated region for block: B:156:0x00a6  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x00cb  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0128  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x0145  */
    /* JADX WARN: Removed duplicated region for block: B:175:0x014c  */
    /* JADX WARN: Removed duplicated region for block: B:179:0x015c  */
    /* JADX WARN: Removed duplicated region for block: B:184:0x01a8  */
    /* JADX WARN: Removed duplicated region for block: B:185:0x01ad  */
    /* JADX WARN: Removed duplicated region for block: B:188:0x01b3  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x0277  */
    /* JADX WARN: Removed duplicated region for block: B:196:0x0280  */
    /* JADX WARN: Removed duplicated region for block: B:202:0x0298  */
    /* JADX WARN: Removed duplicated region for block: B:207:0x02df  */
    /* JADX WARN: Removed duplicated region for block: B:209:0x02f7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawRoundProgress(android.graphics.Canvas r20) {
        /*
            Method dump skipped, instructions count: 770
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawRoundProgress(android.graphics.Canvas):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:105:0x00b8  */
    /* JADX WARN: Removed duplicated region for block: B:125:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updatePollAnimations(long r9) {
        /*
            Method dump skipped, instructions count: 277
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updatePollAnimations(long):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:1287:0x08c5  */
    /* JADX WARN: Removed duplicated region for block: B:1292:0x090e  */
    /* JADX WARN: Removed duplicated region for block: B:1295:0x0915  */
    /* JADX WARN: Removed duplicated region for block: B:1305:0x09ac  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawContent(android.graphics.Canvas r36) {
        /*
            Method dump skipped, instructions count: 5163
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawContent(android.graphics.Canvas):void");
    }

    private float getUseTranscribeButtonProgress() {
        TransitionParams transitionParams = this.transitionParams;
        if (!transitionParams.animateUseTranscribeButton) {
            return this.useTranscribeButton ? 1.0f : 0.0f;
        } else if (this.useTranscribeButton) {
            return transitionParams.animateChangeProgress;
        } else {
            return 1.0f - transitionParams.animateChangeProgress;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:111:0x0017, code lost:
        if ((r1 & 1) != 0) goto L9;
     */
    /* JADX WARN: Removed duplicated region for block: B:191:0x0194  */
    /* JADX WARN: Removed duplicated region for block: B:192:0x01b0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateReactionLayoutPosition() {
        /*
            Method dump skipped, instructions count: 499
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateReactionLayoutPosition():void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:486:0x05b3, code lost:
        if (r4 != r3) goto L152;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawLinkPreview(android.graphics.Canvas r34, float r35) {
        /*
            Method dump skipped, instructions count: 2457
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawLinkPreview(android.graphics.Canvas, float):void");
    }

    public boolean shouldDrawMenuDrawable() {
        return this.currentMessagesGroup == null || (this.currentPosition.flags & 4) != 0;
    }

    private void drawBotButtons(Canvas canvas, ArrayList<BotButton> arrayList, float f) {
        int dp;
        BotButton botButton;
        Drawable themedDrawable;
        if (this.currentMessageObject.isOutOwner()) {
            dp = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
        } else {
            dp = this.backgroundDrawableLeft + AndroidUtilities.dp((this.mediaBackground || this.drawPinnedBottom) ? 1.0f : 7.0f);
        }
        int i = dp;
        float f2 = 2.0f;
        float dp2 = (this.layoutHeight - AndroidUtilities.dp(2.0f)) + this.transitionParams.deltaBottom;
        float f3 = 0.0f;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            BotButton botButton2 = arrayList.get(i2);
            float f4 = botButton2.y + botButton2.height;
            if (f4 > f3) {
                f3 = f4;
            }
        }
        this.rect.set(0.0f, dp2, getMeasuredWidth(), f3 + dp2);
        if (f != 1.0f) {
            canvas.saveLayerAlpha(this.rect, (int) (f * 255.0f), 31);
        } else {
            canvas.save();
        }
        int i3 = 0;
        while (i3 < arrayList.size()) {
            BotButton botButton3 = arrayList.get(i3);
            float dp3 = ((botButton3.y + this.layoutHeight) - AndroidUtilities.dp(f2)) + this.transitionParams.deltaBottom;
            this.rect.set(botButton3.x + i, dp3, botButton3.x + i + botButton3.width, botButton3.height + dp3);
            applyServiceShaderMatrix();
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), getThemedPaint(i3 == this.pressedBotButton ? "paintChatActionBackgroundSelected" : "paintChatActionBackground"));
            if (hasGradientService()) {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            canvas.save();
            boolean z = true;
            canvas.translate(botButton3.x + i + AndroidUtilities.dp(5.0f), ((AndroidUtilities.dp(44.0f) - botButton3.title.getLineBottom(botButton3.title.getLineCount() - 1)) / 2) + dp3);
            botButton3.title.draw(canvas);
            canvas.restore();
            if (!(botButton3.button instanceof TLRPC$TL_keyboardButtonWebView)) {
                if (botButton3.button instanceof TLRPC$TL_keyboardButtonUrl) {
                    if (botButton3.isInviteButton) {
                        themedDrawable = getThemedDrawable("drawable_botInvite");
                    } else {
                        themedDrawable = getThemedDrawable("drawableBotLink");
                    }
                    BaseCell.setDrawableBounds(themedDrawable, (((botButton3.x + botButton3.width) - AndroidUtilities.dp(3.0f)) - themedDrawable.getIntrinsicWidth()) + i, dp3 + AndroidUtilities.dp(3.0f));
                    themedDrawable.draw(canvas);
                } else if (!(botButton3.button instanceof TLRPC$TL_keyboardButtonSwitchInline)) {
                    if ((botButton3.button instanceof TLRPC$TL_keyboardButtonCallback) || (botButton3.button instanceof TLRPC$TL_keyboardButtonRequestGeoLocation) || (botButton3.button instanceof TLRPC$TL_keyboardButtonGame) || (botButton3.button instanceof TLRPC$TL_keyboardButtonBuy) || (botButton3.button instanceof TLRPC$TL_keyboardButtonUrlAuth)) {
                        if (botButton3.button instanceof TLRPC$TL_keyboardButtonBuy) {
                            BaseCell.setDrawableBounds(Theme.chat_botCardDrawable, (((botButton3.x + botButton3.width) - AndroidUtilities.dp(5.0f)) - Theme.chat_botCardDrawable.getIntrinsicWidth()) + i, AndroidUtilities.dp(4.0f) + dp3);
                            Theme.chat_botCardDrawable.draw(canvas);
                        }
                        if (((!(botButton3.button instanceof TLRPC$TL_keyboardButtonCallback) && !(botButton3.button instanceof TLRPC$TL_keyboardButtonGame) && !(botButton3.button instanceof TLRPC$TL_keyboardButtonBuy) && !(botButton3.button instanceof TLRPC$TL_keyboardButtonUrlAuth)) || !SendMessagesHelper.getInstance(this.currentAccount).isSendingCallback(this.currentMessageObject, botButton3.button)) && (!(botButton3.button instanceof TLRPC$TL_keyboardButtonRequestGeoLocation) || !SendMessagesHelper.getInstance(this.currentAccount).isSendingCurrentLocation(this.currentMessageObject, botButton3.button))) {
                            z = false;
                        }
                        if (z || botButton3.progressAlpha != 0.0f) {
                            Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (botButton3.progressAlpha * 255.0f)));
                            int dp4 = ((botButton3.x + botButton3.width) - AndroidUtilities.dp(12.0f)) + i;
                            if (botButton3.button instanceof TLRPC$TL_keyboardButtonBuy) {
                                dp3 += AndroidUtilities.dp(26.0f);
                            }
                            this.rect.set(dp4, AndroidUtilities.dp(4.0f) + dp3, dp4 + AndroidUtilities.dp(8.0f), dp3 + AndroidUtilities.dp(12.0f));
                            canvas.drawArc(this.rect, botButton3.angle, 220.0f, false, Theme.chat_botProgressPaint);
                            invalidate();
                            if (getParent() != null) {
                                ((View) getParent()).invalidate();
                            }
                            long currentTimeMillis = System.currentTimeMillis();
                            if (Math.abs(botButton3.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                                long j = currentTimeMillis - botButton3.lastUpdateTime;
                                botButton = botButton3;
                                BotButton.access$2816(botButton, ((float) (360 * j)) / 2000.0f);
                                BotButton.access$2820(botButton, (botButton.angle / 360) * 360);
                                if (z) {
                                    if (botButton.progressAlpha < 1.0f) {
                                        BotButton.access$2716(botButton, ((float) j) / 200.0f);
                                        if (botButton.progressAlpha > 1.0f) {
                                            botButton.progressAlpha = 1.0f;
                                        }
                                    }
                                } else if (botButton.progressAlpha > 0.0f) {
                                    BotButton.access$2724(botButton, ((float) j) / 200.0f);
                                    if (botButton.progressAlpha < 0.0f) {
                                        botButton.progressAlpha = 0.0f;
                                    }
                                }
                            } else {
                                botButton = botButton3;
                            }
                            botButton.lastUpdateTime = currentTimeMillis;
                        }
                    }
                } else {
                    Drawable themedDrawable2 = getThemedDrawable("drawableBotInline");
                    BaseCell.setDrawableBounds(themedDrawable2, (((botButton3.x + botButton3.width) - AndroidUtilities.dp(3.0f)) - themedDrawable2.getIntrinsicWidth()) + i, dp3 + AndroidUtilities.dp(3.0f));
                    themedDrawable2.draw(canvas);
                }
            } else {
                Drawable themedDrawable3 = getThemedDrawable("drawableBotWebView");
                BaseCell.setDrawableBounds(themedDrawable3, (((botButton3.x + botButton3.width) - AndroidUtilities.dp(3.0f)) - themedDrawable3.getIntrinsicWidth()) + i, dp3 + AndroidUtilities.dp(3.0f));
                themedDrawable3.draw(canvas);
            }
            i3++;
            f2 = 2.0f;
        }
        canvas.restore();
    }

    /* JADX WARN: Removed duplicated region for block: B:156:0x0136  */
    /* JADX WARN: Removed duplicated region for block: B:157:0x0141  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x0176 A[LOOP:1: B:170:0x016e->B:172:0x0176, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:191:0x01f4  */
    /* JADX WARN: Removed duplicated region for block: B:194:0x020b  */
    /* JADX WARN: Removed duplicated region for block: B:208:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"Range"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawMessageText(android.graphics.Canvas r27, java.util.ArrayList<org.telegram.messenger.MessageObject.TextLayoutBlock> r28, boolean r29, float r30, boolean r31) {
        /*
            Method dump skipped, instructions count: 527
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawMessageText(android.graphics.Canvas, java.util.ArrayList, boolean, float, boolean):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x0021  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x003a  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0045  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0047  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x004c  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0052  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0060  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0063 A[LOOP:1: B:76:0x0063->B:78:0x0066, LOOP_START, PHI: r2 r5 
      PHI: (r2v1 int) = (r2v0 int), (r2v2 int) binds: [B:75:0x0061, B:78:0x0066] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r5v2 int) = (r5v1 int), (r5v3 int) binds: [B:75:0x0061, B:78:0x0066] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public org.telegram.ui.Components.AnimatedEmojiSpan[] getAnimatedEmojiSpans() {
        /*
            r7 = this;
            org.telegram.messenger.MessageObject r0 = r7.currentMessageObject
            r1 = 0
            r2 = 0
            if (r0 == 0) goto L1c
            java.lang.CharSequence r0 = r0.messageText
            boolean r3 = r0 instanceof android.text.Spanned
            if (r3 == 0) goto L1c
            r3 = r0
            android.text.Spanned r3 = (android.text.Spanned) r3
            int r0 = r0.length()
            java.lang.Class<org.telegram.ui.Components.AnimatedEmojiSpan> r4 = org.telegram.ui.Components.AnimatedEmojiSpan.class
            java.lang.Object[] r0 = r3.getSpans(r2, r0, r4)
            org.telegram.ui.Components.AnimatedEmojiSpan[] r0 = (org.telegram.ui.Components.AnimatedEmojiSpan[]) r0
            goto L1d
        L1c:
            r0 = r1
        L1d:
            org.telegram.messenger.MessageObject r3 = r7.currentMessageObject
            if (r3 == 0) goto L37
            java.lang.CharSequence r3 = r3.caption
            boolean r4 = r3 instanceof android.text.Spanned
            if (r4 == 0) goto L37
            r4 = r3
            android.text.Spanned r4 = (android.text.Spanned) r4
            int r3 = r3.length()
            java.lang.Class<org.telegram.ui.Components.AnimatedEmojiSpan> r5 = org.telegram.ui.Components.AnimatedEmojiSpan.class
            java.lang.Object[] r3 = r4.getSpans(r2, r3, r5)
            org.telegram.ui.Components.AnimatedEmojiSpan[] r3 = (org.telegram.ui.Components.AnimatedEmojiSpan[]) r3
            goto L38
        L37:
            r3 = r1
        L38:
            if (r0 == 0) goto L3d
            int r4 = r0.length
            if (r4 != 0) goto L43
        L3d:
            if (r3 == 0) goto L6f
            int r4 = r3.length
            if (r4 != 0) goto L43
            goto L6f
        L43:
            if (r0 != 0) goto L47
            r1 = 0
            goto L48
        L47:
            int r1 = r0.length
        L48:
            if (r3 != 0) goto L4c
            r4 = 0
            goto L4d
        L4c:
            int r4 = r3.length
        L4d:
            int r1 = r1 + r4
            org.telegram.ui.Components.AnimatedEmojiSpan[] r1 = new org.telegram.ui.Components.AnimatedEmojiSpan[r1]
            if (r0 == 0) goto L60
            r4 = 0
            r5 = 0
        L54:
            int r6 = r0.length
            if (r4 >= r6) goto L61
            r6 = r0[r4]
            r1[r5] = r6
            int r4 = r4 + 1
            int r5 = r5 + 1
            goto L54
        L60:
            r5 = 0
        L61:
            if (r3 == 0) goto L6f
        L63:
            int r0 = r3.length
            if (r2 >= r0) goto L6f
            r0 = r3[r2]
            r1[r5] = r0
            int r2 = r2 + 1
            int r5 = r5 + 1
            goto L63
        L6f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getAnimatedEmojiSpans():org.telegram.ui.Components.AnimatedEmojiSpan[]");
    }

    public void updateCaptionLayout() {
        float imageX;
        float imageY;
        float imageHeight;
        MessageObject messageObject = this.currentMessageObject;
        int i = messageObject.type;
        if (i == 1 || i == 20 || this.documentAttachType == 4 || i == 8) {
            TransitionParams transitionParams = this.transitionParams;
            if (transitionParams.imageChangeBoundsTransition) {
                imageX = transitionParams.animateToImageX;
                imageY = transitionParams.animateToImageY;
                imageHeight = transitionParams.animateToImageH;
            } else {
                imageX = this.photoImage.getImageX();
                imageY = this.photoImage.getImageY();
                imageHeight = this.photoImage.getImageHeight();
            }
            this.captionX = imageX + AndroidUtilities.dp(5.0f) + this.captionOffsetX;
            this.captionY = imageY + imageHeight + AndroidUtilities.dp(6.0f);
        } else {
            float f = 41.3f;
            float f2 = 9.0f;
            float f3 = 11.0f;
            if (this.hasOldCaptionPreview) {
                int i2 = this.backgroundDrawableLeft;
                if (!messageObject.isOutOwner()) {
                    f3 = 17.0f;
                }
                this.captionX = i2 + AndroidUtilities.dp(f3) + this.captionOffsetX;
                int i3 = this.totalHeight - this.captionHeight;
                if (!this.drawPinnedTop) {
                    f2 = 10.0f;
                }
                float dp = ((i3 - AndroidUtilities.dp(f2)) - this.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
                this.captionY = dp;
                if (this.drawCommentButton && this.drawSideButton != 3) {
                    if (!shouldDrawTimeOnMedia()) {
                        f = 43.0f;
                    }
                    this.captionY = dp - AndroidUtilities.dp(f);
                }
            } else {
                if (this.isRoundVideo) {
                    this.captionX = getBackgroundDrawableLeft() + AndroidUtilities.dp((this.currentMessageObject.isOutOwner() ? 0 : 6) + 11);
                } else {
                    int i4 = this.backgroundDrawableLeft;
                    if (!messageObject.isOutOwner() && !this.mediaBackground && !this.drawPinnedBottom) {
                        f3 = 17.0f;
                    }
                    this.captionX = i4 + AndroidUtilities.dp(f3) + this.captionOffsetX;
                }
                int i5 = this.totalHeight - this.captionHeight;
                if (!this.drawPinnedTop) {
                    f2 = 10.0f;
                }
                float dp2 = i5 - AndroidUtilities.dp(f2);
                this.captionY = dp2;
                if (this.drawCommentButton && this.drawSideButton != 3) {
                    if (!shouldDrawTimeOnMedia()) {
                        f = 43.0f;
                    }
                    this.captionY = dp2 - AndroidUtilities.dp(f);
                }
                ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
                if (!reactionsLayoutInBubble.isEmpty && !reactionsLayoutInBubble.isSmall) {
                    this.captionY -= reactionsLayoutInBubble.totalHeight;
                }
            }
        }
        this.captionX += getExtraTextX();
    }

    private boolean textIsSelectionMode() {
        return getCurrentMessagesGroup() == null && this.delegate.getTextSelectionHelper() != null && this.delegate.getTextSelectionHelper().isSelected(this.currentMessageObject);
    }

    public float getViewTop() {
        return this.viewTop;
    }

    public int getBackgroundHeight() {
        return this.backgroundHeight;
    }

    public int getMiniIconForCurrentState() {
        int i = this.miniButtonState;
        if (i < 0) {
            return 4;
        }
        return i == 0 ? 2 : 3;
    }

    public int getIconForCurrentState() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || (messageObject != null && messageObject.hasExtendedMedia())) {
            return 4;
        }
        if (this.documentAttachType == 7 && this.currentMessageObject.isVoiceTranscriptionOpen() && this.canStreamVideo) {
            int i = this.buttonState;
            return (i == 1 || i == 4) ? 1 : 0;
        }
        int i2 = this.documentAttachType;
        if (i2 == 3 || i2 == 5) {
            if (this.currentMessageObject.isOutOwner()) {
                this.radialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
            } else {
                this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
            }
            int i3 = this.buttonState;
            if (i3 == 1) {
                return 1;
            }
            if (i3 == 2) {
                return 2;
            }
            return i3 == 4 ? 3 : 0;
        }
        if (i2 == 1 && !this.drawPhotoImage) {
            if (this.currentMessageObject.isOutOwner()) {
                this.radialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
            } else {
                this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
            }
            int i4 = this.buttonState;
            if (i4 == -1) {
                return 5;
            }
            if (i4 == 0) {
                return 2;
            }
            if (i4 == 1) {
                return 3;
            }
        } else {
            this.radialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
            this.videoRadialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
            int i5 = this.buttonState;
            if (i5 >= 0 && i5 < 4) {
                if (i5 == 0) {
                    return 2;
                }
                if (i5 == 1) {
                    return 3;
                }
                return (i5 != 2 && this.autoPlayingMedia) ? 4 : 0;
            } else if (i5 == -1) {
                if (this.documentAttachType == 1) {
                    if (this.drawPhotoImage && (this.currentPhotoObject != null || this.currentPhotoObjectThumb != null)) {
                        if (this.photoImage.hasBitmapImage()) {
                            return 4;
                        }
                        MessageObject messageObject2 = this.currentMessageObject;
                        if (messageObject2.mediaExists || messageObject2.attachPathExists) {
                            return 4;
                        }
                    }
                    return 5;
                } else if (this.currentMessageObject.needDrawBluredPreview()) {
                    MessageObject messageObject3 = this.currentMessageObject;
                    if (messageObject3.messageOwner.destroyTime == 0) {
                        return 7;
                    }
                    return messageObject3.isOutOwner() ? 9 : 11;
                } else if (this.hasEmbed) {
                    return 0;
                }
            }
        }
        MessageObject messageObject4 = this.currentMessageObject;
        return (messageObject4 == null || !this.isRoundVideo || !messageObject4.isVoiceTranscriptionOpen()) ? 4 : 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:142:0x010d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int getMaxNameWidth() {
        /*
            Method dump skipped, instructions count: 303
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getMaxNameWidth():int");
    }

    /* JADX WARN: Code restructure failed: missing block: B:608:0x011c, code lost:
        if ((r12 & 2) != 0) goto L49;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateButtonState(boolean r17, boolean r18, boolean r19) {
        /*
            Method dump skipped, instructions count: 2184
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateButtonState(boolean, boolean, boolean):void");
    }

    private void didPressMiniButton(boolean z) {
        int i = this.miniButtonState;
        if (i != 0) {
            if (i != 1) {
                return;
            }
            int i2 = this.documentAttachType;
            if ((i2 == 3 || i2 == 5 || i2 == 7) && MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            this.currentMessageObject.loadingCancelled = true;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
            return;
        }
        this.miniButtonState = 1;
        this.radialProgress.setProgress(0.0f, false);
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.isAnyKindOfSticker()) {
            this.currentMessageObject.putInDownloadsStore = true;
        }
        int i3 = this.documentAttachType;
        if (i3 == 3 || i3 == 5) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 2, 0);
            this.currentMessageObject.loadingCancelled = false;
        } else if (i3 == 4 || i3 == 7) {
            createLoadingProgressLayout(this.documentAttach);
            FileLoader fileLoader = FileLoader.getInstance(this.currentAccount);
            TLRPC$Document tLRPC$Document = this.documentAttach;
            MessageObject messageObject2 = this.currentMessageObject;
            fileLoader.loadFile(tLRPC$Document, messageObject2, 2, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 0);
            this.currentMessageObject.loadingCancelled = false;
        }
        this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
        invalidate();
    }

    private void didPressButton(boolean z, boolean z2) {
        MessageObject messageObject;
        MessageObject messageObject2;
        MessageObject messageObject3;
        TLRPC$PhotoSize tLRPC$PhotoSize;
        String str;
        MessageObject messageObject4;
        MessageObject messageObject5 = this.currentMessageObject;
        if (messageObject5 != null && !messageObject5.isAnyKindOfSticker()) {
            this.currentMessageObject.putInDownloadsStore = true;
        }
        int i = this.buttonState;
        int i2 = 2;
        if (i == 0 && (!this.drawVideoImageButton || z2)) {
            int i3 = this.documentAttachType;
            if (i3 == 3 || i3 == 5 || (i3 == 7 && (messageObject4 = this.currentMessageObject) != null && messageObject4.isVoiceTranscriptionOpen() && this.currentMessageObject.mediaExists)) {
                if (this.miniButtonState == 0) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 2, 0);
                    this.currentMessageObject.loadingCancelled = false;
                }
                if (!this.delegate.needPlayMessage(this.currentMessageObject, false)) {
                    return;
                }
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
                }
                updatePlayingMessageProgress();
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
                return;
            }
            if (z2) {
                this.videoRadialProgress.setProgress(0.0f, false);
            } else {
                this.radialProgress.setProgress(0.0f, false);
            }
            if (this.currentPhotoObject != null && (this.photoImage.hasNotThumb() || this.currentPhotoObjectThumb == null)) {
                tLRPC$PhotoSize = this.currentPhotoObject;
                str = ((tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) || "s".equals(tLRPC$PhotoSize.type)) ? this.currentPhotoFilterThumb : this.currentPhotoFilter;
            } else {
                tLRPC$PhotoSize = this.currentPhotoObjectThumb;
                str = this.currentPhotoFilterThumb;
            }
            String str2 = str;
            MessageObject messageObject6 = this.currentMessageObject;
            int i4 = messageObject6.type;
            if (i4 == 1 || i4 == 20) {
                this.photoImage.setForceLoading(true);
                ImageReceiver imageReceiver = this.photoImage;
                ImageLocation forObject = ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject);
                String str3 = this.currentPhotoFilter;
                ImageLocation forObject2 = ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject);
                String str4 = this.currentPhotoFilterThumb;
                BitmapDrawable bitmapDrawable = this.currentPhotoObjectThumbStripped;
                long j = this.currentPhotoObject.size;
                MessageObject messageObject7 = this.currentMessageObject;
                imageReceiver.setImage(forObject, str3, forObject2, str4, bitmapDrawable, j, null, messageObject7, messageObject7.shouldEncryptPhotoOrVideo() ? 2 : 0);
            } else if (i4 == 8) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 2, 0);
                if (this.currentMessageObject.loadedFileSize > 0) {
                    createLoadingProgressLayout(this.documentAttach);
                }
            } else if (this.isRoundVideo) {
                if (messageObject6.isSecretMedia()) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 2, 1);
                } else {
                    MessageObject messageObject8 = this.currentMessageObject;
                    messageObject8.gifState = 2.0f;
                    TLRPC$Document document = messageObject8.getDocument();
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(ImageLocation.getForDocument(document), null, ImageLocation.getForObject(tLRPC$PhotoSize, document), str2, document.size, null, this.currentMessageObject, 0);
                }
                this.wouldBeInPip = true;
                invalidate();
            } else if (i4 == 9) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 2, 0);
                if (this.currentMessageObject.loadedFileSize > 0) {
                    createLoadingProgressLayout(this.documentAttach);
                }
            } else {
                int i5 = this.documentAttachType;
                if (i5 == 4) {
                    FileLoader fileLoader = FileLoader.getInstance(this.currentAccount);
                    TLRPC$Document tLRPC$Document = this.documentAttach;
                    MessageObject messageObject9 = this.currentMessageObject;
                    if (!messageObject9.shouldEncryptPhotoOrVideo()) {
                        i2 = 0;
                    }
                    fileLoader.loadFile(tLRPC$Document, messageObject9, 1, i2);
                    MessageObject messageObject10 = this.currentMessageObject;
                    if (messageObject10.loadedFileSize > 0) {
                        createLoadingProgressLayout(messageObject10.getDocument());
                    }
                } else if (i4 != 0 || i5 == 0) {
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject), this.currentPhotoFilter, ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject), this.currentPhotoFilterThumb, this.currentPhotoObjectThumbStripped, 0L, null, this.currentMessageObject, 0);
                } else if (i5 == 2) {
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), null, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), this.currentPhotoFilterThumb, this.documentAttach.size, null, this.currentMessageObject, 0);
                    MessageObject messageObject11 = this.currentMessageObject;
                    messageObject11.gifState = 2.0f;
                    if (messageObject11.loadedFileSize > 0) {
                        createLoadingProgressLayout(messageObject11.getDocument());
                    }
                } else if (i5 == 1) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 2, 0);
                } else if (i5 == 8) {
                    this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), this.currentPhotoFilter, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), "b1", 0L, "jpg", this.currentMessageObject, 1);
                }
            }
            this.currentMessageObject.loadingCancelled = false;
            this.buttonState = 1;
            if (z2) {
                this.videoRadialProgress.setIcon(14, false, z);
            } else {
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
            }
            invalidate();
        } else if (i == 1 && (!this.drawVideoImageButton || z2)) {
            this.photoImage.setForceLoading(false);
            int i6 = this.documentAttachType;
            if (i6 == 3 || i6 == 5 || (i6 == 7 && (messageObject3 = this.currentMessageObject) != null && messageObject3.isVoiceTranscriptionOpen())) {
                if (!MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject)) {
                    return;
                }
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                invalidate();
            } else if (this.currentMessageObject.isOut() && !this.drawVideoImageButton && (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) {
                if (this.radialProgress.getIcon() == 6) {
                    return;
                }
                this.delegate.didPressCancelSendButton(this);
            } else {
                MessageObject messageObject12 = this.currentMessageObject;
                messageObject12.loadingCancelled = true;
                int i7 = this.documentAttachType;
                if (i7 == 2 || i7 == 4 || i7 == 1 || i7 == 8) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else {
                    int i8 = messageObject12.type;
                    if (i8 == 0 || i8 == 1 || i8 == 20 || i8 == 8 || i8 == 5) {
                        ImageLoader.getInstance().cancelForceLoadingForImageReceiver(this.photoImage);
                        this.photoImage.cancelLoadImage();
                    } else if (i8 == 9) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
                    }
                }
                this.buttonState = 0;
                if (z2) {
                    this.videoRadialProgress.setIcon(2, false, z);
                } else {
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                }
                invalidate();
            }
        } else if (i != 2) {
            if (i == 3 || i == 0) {
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, z);
                }
                this.delegate.didPressImage(this, 0.0f, 0.0f);
            } else if (i != 4) {
            } else {
                int i9 = this.documentAttachType;
                if (i9 != 3 && i9 != 5 && (i9 != 7 || (messageObject = this.currentMessageObject) == null || !messageObject.isVoiceTranscriptionOpen())) {
                    return;
                }
                if ((this.currentMessageObject.isOut() && (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) || this.currentMessageObject.isSendError()) {
                    if (this.delegate == null || this.radialProgress.getIcon() == 6) {
                        return;
                    }
                    this.delegate.didPressCancelSendButton(this);
                    return;
                }
                this.currentMessageObject.loadingCancelled = true;
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                this.buttonState = 2;
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                invalidate();
            }
        } else if (this.documentAttachType == 7 && (messageObject2 = this.currentMessageObject) != null && messageObject2.isVoiceTranscriptionOpen()) {
            if (this.miniButtonState == 0) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 2, 0);
                this.currentMessageObject.loadingCancelled = false;
            }
            if (this.delegate.needPlayMessage(this.currentMessageObject, false)) {
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
                }
                updatePlayingMessageProgress();
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
            if (!this.isRoundVideo) {
                return;
            }
            this.wouldBeInPip = true;
            invalidate();
        } else {
            int i10 = this.documentAttachType;
            if (i10 == 3 || i10 == 5) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 2, 0);
                this.currentMessageObject.loadingCancelled = false;
                this.buttonState = 4;
                this.radialProgress.setIcon(getIconForCurrentState(), true, z);
                invalidate();
                return;
            }
            if (this.isRoundVideo) {
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject == null || !playingMessageObject.isRoundVideo()) {
                    this.photoImage.setAllowStartAnimation(true);
                    this.photoImage.startAnimation();
                }
            } else {
                this.photoImage.setAllowStartAnimation(true);
                this.photoImage.startAnimation();
            }
            this.currentMessageObject.gifState = 0.0f;
            this.buttonState = -1;
            this.radialProgress.setIcon(getIconForCurrentState(), false, z);
        }
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onFailedDownload(String str, boolean z) {
        int i = this.documentAttachType;
        updateButtonState(true, i == 3 || i == 5, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:139:0x00b8, code lost:
        if ((r7 & 2) != 0) goto L41;
     */
    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onSuccessDownload(java.lang.String r23) {
        /*
            Method dump skipped, instructions count: 454
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onSuccessDownload(java.lang.String):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:58:0x0024, code lost:
        if (r3.mediaExists == false) goto L18;
     */
    @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void didSetImage(org.telegram.messenger.ImageReceiver r3, boolean r4, boolean r5, boolean r6) {
        /*
            r2 = this;
            org.telegram.messenger.MessageObject r3 = r2.currentMessageObject
            if (r3 == 0) goto L4a
            if (r4 == 0) goto L4a
            r4 = 0
            r0 = 1
            if (r6 != 0) goto L10
            boolean r3 = r3.wasUnread
            if (r3 != 0) goto L10
            r3 = 1
            goto L11
        L10:
            r3 = 0
        L11:
            boolean r3 = r2.setCurrentDiceValue(r3)
            if (r3 == 0) goto L18
            return
        L18:
            if (r5 == 0) goto L26
            org.telegram.messenger.MessageObject r3 = r2.currentMessageObject
            int r6 = r3.type
            r1 = 20
            if (r6 != r1) goto L26
            boolean r3 = r3.mediaExists
            if (r3 == 0) goto L43
        L26:
            if (r5 != 0) goto L4a
            org.telegram.messenger.MessageObject r3 = r2.currentMessageObject
            boolean r5 = r3.mediaExists
            if (r5 != 0) goto L4a
            boolean r5 = r3.attachPathExists
            if (r5 != 0) goto L4a
            int r3 = r3.type
            if (r3 != 0) goto L41
            int r5 = r2.documentAttachType
            r6 = 8
            if (r5 == r6) goto L43
            if (r5 == 0) goto L43
            r6 = 6
            if (r5 == r6) goto L43
        L41:
            if (r3 != r0) goto L4a
        L43:
            org.telegram.messenger.MessageObject r3 = r2.currentMessageObject
            r3.mediaExists = r0
            r2.updateButtonState(r4, r0, r4)
        L4a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.didSetImage(org.telegram.messenger.ImageReceiver, boolean, boolean, boolean):void");
    }

    public boolean setCurrentDiceValue(boolean z) {
        MessagesController.DiceFrameSuccess diceFrameSuccess;
        if (this.currentMessageObject.isDice()) {
            Drawable drawable = this.photoImage.getDrawable();
            if (drawable instanceof RLottieDrawable) {
                RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                String diceEmoji = this.currentMessageObject.getDiceEmoji();
                TLRPC$TL_messages_stickerSet stickerSetByEmojiOrName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName(diceEmoji);
                if (stickerSetByEmojiOrName != null) {
                    int diceValue = this.currentMessageObject.getDiceValue();
                    if ("🎰".equals(this.currentMessageObject.getDiceEmoji())) {
                        if (diceValue >= 0 && diceValue <= 64) {
                            ((SlotsDrawable) rLottieDrawable).setDiceNumber(this, diceValue, stickerSetByEmojiOrName, z);
                            if (this.currentMessageObject.isOut()) {
                                rLottieDrawable.setOnFinishCallback(this.diceFinishCallback, Integer.MAX_VALUE);
                            }
                            this.currentMessageObject.wasUnread = false;
                        }
                        if (!rLottieDrawable.hasBaseDice() && stickerSetByEmojiOrName.documents.size() > 0) {
                            ((SlotsDrawable) rLottieDrawable).setBaseDice(this, stickerSetByEmojiOrName);
                        }
                    } else {
                        if (!rLottieDrawable.hasBaseDice() && stickerSetByEmojiOrName.documents.size() > 0) {
                            TLRPC$Document tLRPC$Document = stickerSetByEmojiOrName.documents.get(0);
                            if (rLottieDrawable.setBaseDice(FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$Document, true))) {
                                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                            } else {
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(tLRPC$Document), this.currentMessageObject, this);
                                FileLoader.getInstance(this.currentAccount).loadFile(tLRPC$Document, stickerSetByEmojiOrName, 1, 1);
                            }
                        }
                        if (diceValue >= 0 && diceValue < stickerSetByEmojiOrName.documents.size()) {
                            if (!z && this.currentMessageObject.isOut() && (diceFrameSuccess = MessagesController.getInstance(this.currentAccount).diceSuccess.get(diceEmoji)) != null && diceFrameSuccess.num == diceValue) {
                                rLottieDrawable.setOnFinishCallback(this.diceFinishCallback, diceFrameSuccess.frame);
                            }
                            TLRPC$Document tLRPC$Document2 = stickerSetByEmojiOrName.documents.get(Math.max(diceValue, 0));
                            if (rLottieDrawable.setDiceNumber(FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$Document2, true), z)) {
                                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                            } else {
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(tLRPC$Document2), this.currentMessageObject, this);
                                FileLoader.getInstance(this.currentAccount).loadFile(tLRPC$Document2, stickerSetByEmojiOrName, 1, 1);
                            }
                            this.currentMessageObject.wasUnread = false;
                        }
                    }
                } else {
                    MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName(diceEmoji, true, true);
                }
            }
            return true;
        }
        return false;
    }

    @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
    public void onAnimationReady(ImageReceiver imageReceiver) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || imageReceiver != this.photoImage || !messageObject.isAnimatedSticker()) {
            return;
        }
        this.delegate.setShouldNotRepeatSticker(this.currentMessageObject);
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressDownload(String str, long j, long j2) {
        float min = j2 == 0 ? 0.0f : Math.min(1.0f, ((float) j) / ((float) j2));
        this.currentMessageObject.loadedFileSize = j;
        createLoadingProgressLayout(j, j2);
        if (this.drawVideoImageButton) {
            this.videoRadialProgress.setProgress(min, true);
        } else {
            this.radialProgress.setProgress(min, true);
        }
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.hasMiniProgress != 0) {
                if (this.miniButtonState == 1) {
                    return;
                }
                updateButtonState(false, false, false);
            } else if (this.buttonState == 4) {
            } else {
                updateButtonState(false, false, false);
            }
        } else if (this.hasMiniProgress != 0) {
            if (this.miniButtonState == 1) {
                return;
            }
            updateButtonState(false, false, false);
        } else if (this.buttonState == 1) {
        } else {
            updateButtonState(false, false, false);
        }
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressUpload(String str, long j, long j2, boolean z) {
        int i;
        float min = j2 == 0 ? 0.0f : Math.min(1.0f, ((float) j) / ((float) j2));
        this.currentMessageObject.loadedFileSize = j;
        this.radialProgress.setProgress(min, true);
        if (j == j2 && this.currentPosition != null && SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId()) && ((i = this.buttonState) == 1 || (i == 4 && this.documentAttachType == 5))) {
            this.drawRadialCheckBackground = true;
            getIconForCurrentState();
            this.radialProgress.setIcon(6, false, true);
        }
        createLoadingProgressLayout(j, j2);
    }

    private void createLoadingProgressLayout(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return;
        }
        long[] fileProgressSizes = ImageLoader.getInstance().getFileProgressSizes(FileLoader.getDocumentFileName(tLRPC$Document));
        if (fileProgressSizes != null) {
            createLoadingProgressLayout(fileProgressSizes[0], fileProgressSizes[1]);
        } else {
            createLoadingProgressLayout(this.currentMessageObject.loadedFileSize, tLRPC$Document.size);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:66:0x004b  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x006a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void createLoadingProgressLayout(long r17, long r19) {
        /*
            Method dump skipped, instructions count: 263
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.createLoadingProgressLayout(long, long):void");
    }

    @Override // android.view.View
    public void onProvideStructure(ViewStructure viewStructure) {
        super.onProvideStructure(viewStructure);
        if (!this.allowAssistant || Build.VERSION.SDK_INT < 23) {
            return;
        }
        CharSequence charSequence = this.currentMessageObject.messageText;
        if (charSequence != null && charSequence.length() > 0) {
            viewStructure.setText(this.currentMessageObject.messageText);
            return;
        }
        CharSequence charSequence2 = this.currentMessageObject.caption;
        if (charSequence2 == null || charSequence2.length() <= 0) {
            return;
        }
        viewStructure.setText(this.currentMessageObject.caption);
    }

    public void setDelegate(ChatMessageCellDelegate chatMessageCellDelegate) {
        this.delegate = chatMessageCellDelegate;
    }

    public ChatMessageCellDelegate getDelegate() {
        return this.delegate;
    }

    public void setAllowAssistant(boolean z) {
        this.allowAssistant = z;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:204:0x00b7  */
    /* JADX WARN: Removed duplicated region for block: B:205:0x00c6  */
    /* JADX WARN: Removed duplicated region for block: B:251:0x017a  */
    /* JADX WARN: Removed duplicated region for block: B:255:0x0192  */
    /* JADX WARN: Removed duplicated region for block: B:265:0x01e5  */
    /* JADX WARN: Removed duplicated region for block: B:271:0x0215  */
    /* JADX WARN: Removed duplicated region for block: B:274:0x022f  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x024c  */
    /* JADX WARN: Removed duplicated region for block: B:282:0x02a4  */
    /* JADX WARN: Removed duplicated region for block: B:288:0x0301  */
    /* JADX WARN: Removed duplicated region for block: B:297:0x0362  */
    /* JADX WARN: Removed duplicated region for block: B:300:0x0378  */
    /* JADX WARN: Removed duplicated region for block: B:312:0x03b1  */
    /* JADX WARN: Removed duplicated region for block: B:314:0x03c3  */
    /* JADX WARN: Removed duplicated region for block: B:335:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void measureTime(org.telegram.messenger.MessageObject r18) {
        /*
            Method dump skipped, instructions count: 1071
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.measureTime(org.telegram.messenger.MessageObject):void");
    }

    private boolean shouldDrawSelectionOverlay() {
        return hasSelectionOverlay() && ((isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted || this.isHighlightedAnimated)) && !textIsSelectionMode() && ((this.currentMessagesGroup == null || this.drawSelectionBackground) && this.currentBackgroundDrawable != null);
    }

    private Integer getSelectionOverlayColor() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider == null) {
            return null;
        }
        MessageObject messageObject = this.currentMessageObject;
        return resourcesProvider.getColor((messageObject == null || !messageObject.isOut()) ? "chat_inBubbleSelectedOverlay" : "chat_outBubbleSelectedOverlay");
    }

    private boolean hasSelectionOverlay() {
        Integer selectionOverlayColor = getSelectionOverlayColor();
        return (selectionOverlayColor == null || selectionOverlayColor.intValue() == -65536) ? false : true;
    }

    private boolean isDrawSelectionBackground() {
        return ((isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted)) && !textIsSelectionMode() && !hasSelectionOverlay();
    }

    public boolean isOpenChatByShare(MessageObject messageObject) {
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = messageObject.messageOwner.fwd_from;
        return (tLRPC$MessageFwdHeader == null || tLRPC$MessageFwdHeader.saved_from_peer == null) ? false : true;
    }

    private boolean checkNeedDrawShareButton(MessageObject messageObject) {
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2.deleted || messageObject2.isSponsored()) {
            return false;
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null && !this.currentMessagesGroup.isDocuments && !groupedMessagePosition.last) {
            return false;
        }
        return messageObject.needDrawShareButton();
    }

    public boolean isInsideBackground(float f, float f2) {
        if (this.currentBackgroundDrawable != null) {
            int i = this.backgroundDrawableLeft;
            if (f >= i && f <= i + this.backgroundDrawableRight) {
                return true;
            }
        }
        return false;
    }

    private void updateCurrentUserAndChat() {
        TLRPC$Peer tLRPC$Peer;
        MessageObject messageObject;
        if (this.currentMessageObject == null) {
            return;
        }
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (tLRPC$MessageFwdHeader != null && (tLRPC$MessageFwdHeader.from_id instanceof TLRPC$TL_peerChannel) && this.currentMessageObject.getDialogId() == clientUserId) {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(tLRPC$MessageFwdHeader.from_id.channel_id));
        } else if (tLRPC$MessageFwdHeader != null && (tLRPC$Peer = tLRPC$MessageFwdHeader.saved_from_peer) != null) {
            long j = tLRPC$Peer.user_id;
            if (j != 0) {
                TLRPC$Peer tLRPC$Peer2 = tLRPC$MessageFwdHeader.from_id;
                if (tLRPC$Peer2 instanceof TLRPC$TL_peerUser) {
                    this.currentUser = messagesController.getUser(Long.valueOf(tLRPC$Peer2.user_id));
                } else {
                    this.currentUser = messagesController.getUser(Long.valueOf(j));
                }
            } else if (tLRPC$Peer.channel_id != 0) {
                if (this.currentMessageObject.isSavedFromMegagroup()) {
                    TLRPC$Peer tLRPC$Peer3 = tLRPC$MessageFwdHeader.from_id;
                    if (tLRPC$Peer3 instanceof TLRPC$TL_peerUser) {
                        this.currentUser = messagesController.getUser(Long.valueOf(tLRPC$Peer3.user_id));
                    }
                }
                this.currentChat = messagesController.getChat(Long.valueOf(tLRPC$MessageFwdHeader.saved_from_peer.channel_id));
            } else {
                long j2 = tLRPC$Peer.chat_id;
                if (j2 != 0) {
                    TLRPC$Peer tLRPC$Peer4 = tLRPC$MessageFwdHeader.from_id;
                    if (tLRPC$Peer4 instanceof TLRPC$TL_peerUser) {
                        this.currentUser = messagesController.getUser(Long.valueOf(tLRPC$Peer4.user_id));
                    } else {
                        this.currentChat = messagesController.getChat(Long.valueOf(j2));
                    }
                }
            }
        } else if (tLRPC$MessageFwdHeader != null && (tLRPC$MessageFwdHeader.from_id instanceof TLRPC$TL_peerUser) && (tLRPC$MessageFwdHeader.imported || this.currentMessageObject.getDialogId() == clientUserId)) {
            this.currentUser = messagesController.getUser(Long.valueOf(tLRPC$MessageFwdHeader.from_id.user_id));
        } else if (tLRPC$MessageFwdHeader != null && !TextUtils.isEmpty(tLRPC$MessageFwdHeader.from_name) && (tLRPC$MessageFwdHeader.imported || this.currentMessageObject.getDialogId() == clientUserId)) {
            TLRPC$TL_user tLRPC$TL_user = new TLRPC$TL_user();
            this.currentUser = tLRPC$TL_user;
            tLRPC$TL_user.first_name = tLRPC$MessageFwdHeader.from_name;
        } else {
            long fromChatId = this.currentMessageObject.getFromChatId();
            if (DialogObject.isUserDialog(fromChatId) && !this.currentMessageObject.messageOwner.post) {
                this.currentUser = messagesController.getUser(Long.valueOf(fromChatId));
            } else if (DialogObject.isChatDialog(fromChatId)) {
                this.currentChat = messagesController.getChat(Long.valueOf(-fromChatId));
            } else {
                TLRPC$Message tLRPC$Message = this.currentMessageObject.messageOwner;
                if (tLRPC$Message.post) {
                    this.currentChat = messagesController.getChat(Long.valueOf(tLRPC$Message.peer_id.channel_id));
                }
            }
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null && messageObject2.getChatId() != 0) {
            MessageObject messageObject3 = this.currentMessageObject;
            if (messageObject3.messageOwner != null && (messageObject = messageObject3.replyMessageObject) != null && messageObject.isFromUser()) {
                this.currentReplyUserId = this.currentMessageObject.replyMessageObject.messageOwner.from_id.user_id;
                return;
            }
        }
        this.currentReplyUserId = 0L;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(27:21|(1:23)|24|(2:561|(25:568|(4:581|(1:583)(1:590)|584|(3:586|(1:588)|589))|572|34|(1:36)(1:(1:559)(1:560))|37|(1:39)(1:557)|40|(7:42|(1:44)|45|(1:47)(3:53|(1:55)(1:57)|56)|48|(1:50)(1:52)|51)|58|59|60|61|62|(3:64|(1:66)|67)(1:552)|68|(1:70)|(1:72)(1:551)|73|(1:75)|76|(1:78)(2:545|(1:547)(2:548|(1:550)))|79|(1:81)|82)(1:567))(1:32)|33|34|(0)(0)|37|(0)(0)|40|(0)|58|59|60|61|62|(0)(0)|68|(0)|(0)(0)|73|(0)|76|(0)(0)|79|(0)|82) */
    /* JADX WARN: Can't wrap try/catch for region: R(60:1|(2:598|(1:604))|5|(3:7|(1:11)|594)(3:595|(1:597)|594)|12|(1:593)(1:16)|17|(1:591)(27:21|(1:23)|24|(2:561|(25:568|(4:581|(1:583)(1:590)|584|(3:586|(1:588)|589))|572|34|(1:36)(1:(1:559)(1:560))|37|(1:39)(1:557)|40|(7:42|(1:44)|45|(1:47)(3:53|(1:55)(1:57)|56)|48|(1:50)(1:52)|51)|58|59|60|61|62|(3:64|(1:66)|67)(1:552)|68|(1:70)|(1:72)(1:551)|73|(1:75)|76|(1:78)(2:545|(1:547)(2:548|(1:550)))|79|(1:81)|82)(1:567))(1:32)|33|34|(0)(0)|37|(0)(0)|40|(0)|58|59|60|61|62|(0)(0)|68|(0)|(0)(0)|73|(0)|76|(0)(0)|79|(0)|82)|83|(2:85|(1:87)(2:88|(1:90)(2:91|(1:93))))|94|(1:544)(50:98|(4:487|(1:489)|490|(19:496|(1:(1:499)(2:537|(1:539)(1:540)))(1:(1:542)(1:543))|500|(1:502)|503|504|505|(1:507)(1:534)|508|(1:514)|515|516|517|518|519|520|(1:522)|523|(1:527)))|102|103|(3:113|(1:115)|(32:117|(1:119)(3:479|(1:481)(2:483|(1:485))|482)|120|(1:122)|123|(1:125)(1:478)|126|(1:128)(1:477)|129|(1:131)(1:476)|132|(1:134)|135|(5:137|(3:471|(1:473)|474)(1:141)|142|(1:144)(1:470)|(3:146|(1:148)(1:150)|149))(1:475)|151|(4:153|(1:155)|156|(1:158))|159|(1:161)|162|(5:164|(1:166)(1:465)|167|(2:169|(1:171)(1:172))|173)(2:466|(1:468)(1:469))|174|(2:178|(1:180))|181|(1:183)(1:464)|184|(7:186|(1:188)|189|(1:191)(1:197)|192|(1:194)(1:196)|195)|198|(3:200|(1:202)|203)|204|(2:212|(23:219|(2:223|(1:225))|226|(1:230)|231|(1:233)(2:432|(1:434))|234|(10:329|(3:331|(3:333|(1:335)(1:429)|336)(1:430)|337)(1:431)|(1:339)|(1:428)(3:351|(1:353)(1:427)|354)|355|(2:357|(2:359|(2:361|(1:363)(1:407))(1:408))(1:409))(2:410|(2:414|(2:416|(1:418))(2:419|(2:421|(1:423))(2:424|(1:426)))))|(1:365)|366|(1:368)(2:370|(1:372)(2:373|(1:375)(2:376|(6:378|(1:380)(1:389)|381|(1:383)|384|(1:388))(2:390|(1:406)(6:396|(1:398)(1:405)|399|(1:401)|402|(1:404))))))|369)(6:238|(1:240)(2:321|(1:323)(2:324|(1:326)))|241|(1:243)|244|(7:301|(1:(1:304)(1:317))(1:(1:319)(1:320))|305|(1:307)(1:316)|308|(1:314)|315)(1:250))|(1:252)(1:300)|253|254|255|256|257|(1:259)(1:295)|260|(2:262|(1:264))|266|267|268|(1:270)(1:292)|271|(8:273|(3:275|(2:277|278)(1:280)|279)|281|282|(1:284)|285|(1:289)|290)))(2:435|(8:439|(2:441|(1:(1:444)))|446|(2:450|(1:452))|453|(1:455)(2:459|(1:461))|456|(1:458)))|216|217))|486|198|(0)|204|(1:206)|462|212|(1:214)|219|(3:221|223|(0))|226|(2:228|230)|231|(0)(0)|234|(1:236)|327|329|(0)(0)|(0)|(1:341)|428|355|(0)(0)|(0)|366|(0)(0)|369|(0)(0)|253|254|255|256|257|(0)(0)|260|(0)|266|267|268|(0)(0)|271|(0)|216|217)|529|103|(5:105|109|113|(0)|(0))|486|198|(0)|204|(0)|462|212|(0)|219|(0)|226|(0)|231|(0)(0)|234|(0)|327|329|(0)(0)|(0)|(0)|428|355|(0)(0)|(0)|366|(0)(0)|369|(0)(0)|253|254|255|256|257|(0)(0)|260|(0)|266|267|268|(0)(0)|271|(0)|216|217) */
    /* JADX WARN: Code restructure failed: missing block: B:1182:0x1149, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1183:0x114a, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:1207:0x1216, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1208:0x1217, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:738:0x03ba, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:739:0x03bb, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:977:0x0b7e, code lost:
        if ((r0.action instanceof org.telegram.tgnet.TLRPC$TL_messageActionTopicCreate) == false) goto L446;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:1000:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:1004:0x0c4c  */
    /* JADX WARN: Removed duplicated region for block: B:1007:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:1012:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:1013:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:1018:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:1024:0x0cb3  */
    /* JADX WARN: Removed duplicated region for block: B:1033:0x0ce7  */
    /* JADX WARN: Removed duplicated region for block: B:1035:0x0cec  */
    /* JADX WARN: Removed duplicated region for block: B:1037:0x0cef  */
    /* JADX WARN: Removed duplicated region for block: B:1056:0x0d78  */
    /* JADX WARN: Removed duplicated region for block: B:1066:0x0dca  */
    /* JADX WARN: Removed duplicated region for block: B:1085:0x0e2c  */
    /* JADX WARN: Removed duplicated region for block: B:1088:0x0e3a  */
    /* JADX WARN: Removed duplicated region for block: B:1089:0x0e3d  */
    /* JADX WARN: Removed duplicated region for block: B:1168:0x10bc  */
    /* JADX WARN: Removed duplicated region for block: B:1169:0x10bf  */
    /* JADX WARN: Removed duplicated region for block: B:1174:0x10ea A[Catch: Exception -> 0x1149, TryCatch #4 {Exception -> 0x1149, blocks: (B:1172:0x10e2, B:1174:0x10ea, B:1176:0x1101, B:1178:0x1106, B:1180:0x1125), top: B:1221:0x10e2 }] */
    /* JADX WARN: Removed duplicated region for block: B:1175:0x1100  */
    /* JADX WARN: Removed duplicated region for block: B:1178:0x1106 A[Catch: Exception -> 0x1149, TryCatch #4 {Exception -> 0x1149, blocks: (B:1172:0x10e2, B:1174:0x10ea, B:1176:0x1101, B:1178:0x1106, B:1180:0x1125), top: B:1221:0x10e2 }] */
    /* JADX WARN: Removed duplicated region for block: B:1187:0x1157 A[Catch: Exception -> 0x1216, TryCatch #6 {Exception -> 0x1216, blocks: (B:1185:0x114f, B:1187:0x1157, B:1189:0x116e, B:1191:0x1173, B:1193:0x1189, B:1195:0x1195, B:1196:0x1198, B:1197:0x119b, B:1199:0x11c2, B:1200:0x11e5, B:1202:0x11f2, B:1204:0x11fc, B:1205:0x1205), top: B:1225:0x114f }] */
    /* JADX WARN: Removed duplicated region for block: B:1188:0x116d  */
    /* JADX WARN: Removed duplicated region for block: B:1191:0x1173 A[Catch: Exception -> 0x1216, TryCatch #6 {Exception -> 0x1216, blocks: (B:1185:0x114f, B:1187:0x1157, B:1189:0x116e, B:1191:0x1173, B:1193:0x1189, B:1195:0x1195, B:1196:0x1198, B:1197:0x119b, B:1199:0x11c2, B:1200:0x11e5, B:1202:0x11f2, B:1204:0x11fc, B:1205:0x1205), top: B:1225:0x114f }] */
    /* JADX WARN: Removed duplicated region for block: B:696:0x01e7  */
    /* JADX WARN: Removed duplicated region for block: B:697:0x01ea  */
    /* JADX WARN: Removed duplicated region for block: B:702:0x0211  */
    /* JADX WARN: Removed duplicated region for block: B:703:0x0214  */
    /* JADX WARN: Removed duplicated region for block: B:706:0x021f  */
    /* JADX WARN: Removed duplicated region for block: B:726:0x033e A[Catch: Exception -> 0x03ba, TryCatch #3 {Exception -> 0x03ba, blocks: (B:724:0x031d, B:726:0x033e, B:728:0x0355, B:729:0x0368, B:731:0x0377, B:733:0x037b, B:735:0x0388, B:736:0x03b6, B:730:0x0372), top: B:1220:0x031d }] */
    /* JADX WARN: Removed duplicated region for block: B:730:0x0372 A[Catch: Exception -> 0x03ba, TryCatch #3 {Exception -> 0x03ba, blocks: (B:724:0x031d, B:726:0x033e, B:728:0x0355, B:729:0x0368, B:731:0x0377, B:733:0x037b, B:735:0x0388, B:736:0x03b6, B:730:0x0372), top: B:1220:0x031d }] */
    /* JADX WARN: Removed duplicated region for block: B:733:0x037b A[Catch: Exception -> 0x03ba, TryCatch #3 {Exception -> 0x03ba, blocks: (B:724:0x031d, B:726:0x033e, B:728:0x0355, B:729:0x0368, B:731:0x0377, B:733:0x037b, B:735:0x0388, B:736:0x03b6, B:730:0x0372), top: B:1220:0x031d }] */
    /* JADX WARN: Removed duplicated region for block: B:735:0x0388 A[Catch: Exception -> 0x03ba, TryCatch #3 {Exception -> 0x03ba, blocks: (B:724:0x031d, B:726:0x033e, B:728:0x0355, B:729:0x0368, B:731:0x0377, B:733:0x037b, B:735:0x0388, B:736:0x03b6, B:730:0x0372), top: B:1220:0x031d }] */
    /* JADX WARN: Removed duplicated region for block: B:736:0x03b6 A[Catch: Exception -> 0x03ba, TRY_LEAVE, TryCatch #3 {Exception -> 0x03ba, blocks: (B:724:0x031d, B:726:0x033e, B:728:0x0355, B:729:0x0368, B:731:0x0377, B:733:0x037b, B:735:0x0388, B:736:0x03b6, B:730:0x0372), top: B:1220:0x031d }] */
    /* JADX WARN: Removed duplicated region for block: B:742:0x03c2  */
    /* JADX WARN: Removed duplicated region for block: B:745:0x03d4  */
    /* JADX WARN: Removed duplicated region for block: B:746:0x03dc  */
    /* JADX WARN: Removed duplicated region for block: B:754:0x0401  */
    /* JADX WARN: Removed duplicated region for block: B:843:0x070d  */
    /* JADX WARN: Removed duplicated region for block: B:845:0x0722  */
    /* JADX WARN: Removed duplicated region for block: B:952:0x0b2c  */
    /* JADX WARN: Removed duplicated region for block: B:958:0x0b4a  */
    /* JADX WARN: Removed duplicated region for block: B:996:0x0c1f  */
    /* JADX WARN: Type inference failed for: r3v302 */
    /* JADX WARN: Type inference failed for: r3v5 */
    /* JADX WARN: Type inference failed for: r3v6, types: [org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, java.lang.String] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setMessageObjectInternal(org.telegram.messenger.MessageObject r47) {
        /*
            Method dump skipped, instructions count: 4638
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageObjectInternal(org.telegram.messenger.MessageObject):void");
    }

    private void setupTopicColors(int i) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.shouldDrawWithoutBackground()) {
            this.topicNameColor = getThemedColor("chat_stickerReplyNameText");
            return;
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null && messageObject2.isOutOwner()) {
            this.topicNameColor = getThemedColor("chat_outReactionButtonText");
            this.topicBackgroundColor = ColorUtils.setAlphaComponent(getThemedColor("chat_outReactionButtonBackground"), 38);
            return;
        }
        if (this.topicHSV == null) {
            this.topicHSV = new float[3];
        }
        Color.colorToHSV(i, this.topicHSV);
        float[] fArr = this.topicHSV;
        float f = fArr[0];
        float f2 = fArr[1];
        if (f2 <= 0.0f || f2 >= 1.0f) {
            this.topicNameColor = getThemedColor("chat_inReactionButtonText");
            this.topicBackgroundColor = ColorUtils.setAlphaComponent(getThemedColor("chat_inReactionButtonBackground"), 38);
            return;
        }
        Color.colorToHSV(getThemedColor("chat_inReactionButtonText"), this.topicHSV);
        this.topicHSV[0] = f;
        if (!Theme.isCurrentThemeDark() && AndroidUtilities.computePerceivedBrightness(Color.HSVToColor(this.topicHSV)) > 0.7f) {
            float[] fArr2 = this.topicHSV;
            fArr2[2] = MathUtils.clamp(fArr2[2] - 0.25f, 0.0f, 1.0f);
        }
        this.topicNameColor = Color.HSVToColor(Color.alpha(getThemedColor("chat_inReactionButtonText")), this.topicHSV);
        Color.colorToHSV(getThemedColor("chat_inReactionButtonBackground"), this.topicHSV);
        float[] fArr3 = this.topicHSV;
        fArr3[0] = f;
        this.topicBackgroundColor = Color.HSVToColor(38, fArr3);
    }

    private boolean isNeedAuthorName() {
        return (this.isPinnedChat && this.currentMessageObject.type == 0) || (!this.pinnedTop && this.drawName && this.isChat && (!this.currentMessageObject.isOutOwner() || (this.currentMessageObject.isSupergroup() && this.currentMessageObject.isFromGroup()))) || (this.currentMessageObject.isImportedForward() && this.currentMessageObject.messageOwner.fwd_from.from_id == null);
    }

    private String getAuthorName() {
        TLRPC$Chat tLRPC$Chat;
        String str;
        String str2;
        TLRPC$User tLRPC$User = this.currentUser;
        if (tLRPC$User != null) {
            return UserObject.getUserName(tLRPC$User);
        }
        TLRPC$Chat tLRPC$Chat2 = this.currentChat;
        if (tLRPC$Chat2 != null) {
            return tLRPC$Chat2.title;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || !messageObject.isSponsored()) {
            return "DELETED";
        }
        TLRPC$ChatInvite tLRPC$ChatInvite = this.currentMessageObject.sponsoredChatInvite;
        return (tLRPC$ChatInvite == null || (str2 = tLRPC$ChatInvite.title) == null) ? (tLRPC$ChatInvite == null || (tLRPC$Chat = tLRPC$ChatInvite.chat) == null || (str = tLRPC$Chat.title) == null) ? "" : str : str2;
    }

    private Object getAuthorStatus() {
        TLRPC$User tLRPC$User = this.currentUser;
        if (tLRPC$User != null) {
            TLRPC$EmojiStatus tLRPC$EmojiStatus = tLRPC$User.emoji_status;
            if ((tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatusUntil) && ((TLRPC$TL_emojiStatusUntil) tLRPC$EmojiStatus).until > ((int) (System.currentTimeMillis() / 1000))) {
                return Long.valueOf(((TLRPC$TL_emojiStatusUntil) this.currentUser.emoji_status).document_id);
            }
            TLRPC$User tLRPC$User2 = this.currentUser;
            TLRPC$EmojiStatus tLRPC$EmojiStatus2 = tLRPC$User2.emoji_status;
            if (tLRPC$EmojiStatus2 instanceof TLRPC$TL_emojiStatus) {
                return Long.valueOf(((TLRPC$TL_emojiStatus) tLRPC$EmojiStatus2).document_id);
            }
            if (!tLRPC$User2.premium) {
                return null;
            }
            return ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.msg_premium_liststar).mutate();
        }
        return null;
    }

    private String getForwardedMessageText(MessageObject messageObject) {
        if (this.hasPsaHint) {
            String string = LocaleController.getString("PsaMessage_" + messageObject.messageOwner.fwd_from.psa_type);
            return string == null ? LocaleController.getString("PsaMessageDefault", R.string.PsaMessageDefault) : string;
        }
        return LocaleController.getString("ForwardedMessage", R.string.ForwardedMessage);
    }

    public int getExtraInsetHeight() {
        int i = this.addedCaptionHeight;
        if (this.drawCommentButton) {
            i += AndroidUtilities.dp(shouldDrawTimeOnMedia() ? 41.3f : 43.0f);
        }
        return (this.reactionsLayoutInBubble.isEmpty || !this.currentMessageObject.shouldDrawReactionsInLayout()) ? i : i + this.reactionsLayoutInBubble.totalHeight;
    }

    public ImageReceiver getAvatarImage() {
        if (this.isAvatarVisible) {
            return this.avatarImage;
        }
        return null;
    }

    public float getCheckBoxTranslation() {
        return this.checkBoxTranslation;
    }

    public boolean shouldDrawAlphaLayer() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        return (groupedMessages == null || !groupedMessages.transitionParams.backgroundChangeBounds) && getAlpha() != 1.0f;
    }

    public float getCaptionX() {
        return this.captionX;
    }

    public boolean isDrawPinnedBottom() {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        return this.mediaBackground || this.drawPinnedBottom || (groupedMessagePosition != null && (groupedMessagePosition.flags & 8) == 0 && this.currentMessagesGroup.isDocuments);
    }

    public void drawCheckBox(Canvas canvas) {
        float f;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.isSending() || this.currentMessageObject.isSendError() || this.checkBox == null) {
            return;
        }
        if (!this.checkBoxVisible && !this.checkBoxAnimationInProgress) {
            return;
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i = groupedMessagePosition.flags;
            if ((i & 8) == 0 || (i & 1) == 0) {
                return;
            }
        }
        canvas.save();
        float y = getY();
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && groupedMessages.messages.size() > 1) {
            f = (getTop() + this.currentMessagesGroup.transitionParams.offsetTop) - getTranslationY();
        } else {
            f = y + this.transitionParams.deltaTop;
        }
        canvas.translate(0.0f, f + this.transitionYOffsetForDrawables);
        this.checkBox.draw(canvas);
        canvas.restore();
    }

    /* JADX WARN: Removed duplicated region for block: B:60:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0046  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setBackgroundTopY(boolean r14) {
        /*
            r13 = this;
            r0 = 0
            r1 = 0
        L2:
            r2 = 2
            if (r1 >= r2) goto L6f
            r2 = 1
            if (r1 != r2) goto Lb
            if (r14 != 0) goto Lb
            return
        Lb:
            if (r1 != 0) goto L10
            org.telegram.ui.ActionBar.Theme$MessageDrawable r3 = r13.currentBackgroundDrawable
            goto L12
        L10:
            org.telegram.ui.ActionBar.Theme$MessageDrawable r3 = r13.currentBackgroundSelectedDrawable
        L12:
            r4 = r3
            if (r4 != 0) goto L16
            goto L6c
        L16:
            int r3 = r13.parentWidth
            int r5 = r13.parentHeight
            if (r5 != 0) goto L3d
            int r3 = r13.getParentWidth()
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r5.y
            android.view.ViewParent r6 = r13.getParent()
            boolean r6 = r6 instanceof android.view.View
            if (r6 == 0) goto L3d
            android.view.ViewParent r3 = r13.getParent()
            android.view.View r3 = (android.view.View) r3
            int r5 = r3.getMeasuredWidth()
            int r3 = r3.getMeasuredHeight()
            r7 = r3
            r6 = r5
            goto L3f
        L3d:
            r6 = r3
            r7 = r5
        L3f:
            if (r14 == 0) goto L46
            float r3 = r13.getY()
            goto L4b
        L46:
            int r3 = r13.getTop()
            float r3 = (float) r3
        L4b:
            float r5 = r13.parentViewTopOffset
            float r3 = r3 + r5
            int r3 = (int) r3
            int r8 = (int) r5
            int r9 = r13.blurredViewTopOffset
            int r10 = r13.blurredViewBottomOffset
            boolean r11 = r13.pinnedTop
            boolean r5 = r13.pinnedBottom
            if (r5 != 0) goto L67
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r5 = r13.transitionParams
            float r5 = r5.changePinnedBottomProgress
            r12 = 1065353216(0x3var_, float:1.0)
            int r5 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r5 == 0) goto L65
            goto L67
        L65:
            r12 = 0
            goto L68
        L67:
            r12 = 1
        L68:
            r5 = r3
            r4.setTop(r5, r6, r7, r8, r9, r10, r11, r12)
        L6c:
            int r1 = r1 + 1
            goto L2
        L6f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setBackgroundTopY(boolean):void");
    }

    public void setBackgroundTopY(int i) {
        int i2;
        int i3;
        Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
        int i4 = this.parentWidth;
        int i5 = this.parentHeight;
        if (i5 == 0) {
            i4 = getParentWidth();
            i5 = AndroidUtilities.displaySize.y;
            if (getParent() instanceof View) {
                View view = (View) getParent();
                i3 = view.getMeasuredWidth();
                i2 = view.getMeasuredHeight();
                float f = this.parentViewTopOffset;
                messageDrawable.setTop((int) (i + f), i3, i2, (int) f, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, !this.pinnedBottom || this.transitionParams.changePinnedBottomProgress != 1.0f);
            }
        }
        i2 = i5;
        i3 = i4;
        float f2 = this.parentViewTopOffset;
        messageDrawable.setTop((int) (i + f2), i3, i2, (int) f2, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, !this.pinnedBottom || this.transitionParams.changePinnedBottomProgress != 1.0f);
    }

    public void setDrawableBoundsInner(Drawable drawable, int i, int i2, int i3, int i4) {
        if (drawable != null) {
            float f = i4 + i2;
            TransitionParams transitionParams = this.transitionParams;
            float f2 = transitionParams.deltaBottom;
            this.transitionYOffsetForDrawables = (f + f2) - ((int) (f + f2));
            drawable.setBounds((int) (i + transitionParams.deltaLeft), (int) (i2 + transitionParams.deltaTop), (int) (i + i3 + transitionParams.deltaRight), (int) (f + f2));
        }
    }

    @Override // android.view.View
    @SuppressLint({"WrongCall"})
    protected void onDraw(Canvas canvas) {
        boolean z;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        Theme.MessageDrawable messageDrawable;
        int i;
        int i2;
        int i3;
        int i4;
        if (this.currentMessageObject == null) {
            return;
        }
        if (!this.wasLayout) {
            onLayout(false, getLeft(), getTop(), getRight(), getBottom());
        }
        if (this.enterTransitionInProgress && this.currentMessageObject.isAnimatedEmojiStickers()) {
            return;
        }
        if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_msgTextPaint.setColor(getThemedColor("chat_messageTextOut"));
            Theme.chat_msgGameTextPaint.setColor(getThemedColor("chat_messageTextOut"));
            Theme.chat_msgGameTextPaint.linkColor = getThemedColor("chat_messageLinkOut");
            Theme.chat_replyTextPaint.linkColor = getThemedColor("chat_messageLinkOut");
            Theme.chat_msgTextPaint.linkColor = getThemedColor("chat_messageLinkOut");
        } else {
            Theme.chat_msgTextPaint.setColor(getThemedColor("chat_messageTextIn"));
            Theme.chat_msgGameTextPaint.setColor(getThemedColor("chat_messageTextIn"));
            Theme.chat_msgGameTextPaint.linkColor = getThemedColor("chat_messageLinkIn");
            Theme.chat_replyTextPaint.linkColor = getThemedColor("chat_messageLinkIn");
            Theme.chat_msgTextPaint.linkColor = getThemedColor("chat_messageLinkIn");
        }
        if (this.documentAttach != null) {
            int i5 = this.documentAttachType;
            if (i5 == 3 || i5 == 7) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBarWaveform.setColors(getThemedColor("chat_outVoiceSeekbar"), getThemedColor("chat_outVoiceSeekbarFill"), getThemedColor("chat_outVoiceSeekbarSelected"));
                    this.seekBar.setColors(getThemedColor("chat_outAudioSeekbar"), getThemedColor("chat_outAudioCacheSeekbar"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarSelected"));
                } else {
                    this.seekBarWaveform.setColors(getThemedColor("chat_inVoiceSeekbar"), getThemedColor("chat_inVoiceSeekbarFill"), getThemedColor("chat_inVoiceSeekbarSelected"));
                    this.seekBar.setColors(getThemedColor("chat_inAudioSeekbar"), getThemedColor("chat_inAudioCacheSeekbar"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarSelected"));
                }
            } else if (i5 == 5) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBar.setColors(getThemedColor("chat_outAudioSeekbar"), getThemedColor("chat_outAudioCacheSeekbar"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarSelected"));
                } else {
                    this.seekBar.setColors(getThemedColor("chat_inAudioSeekbar"), getThemedColor("chat_inAudioCacheSeekbar"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarSelected"));
                }
            }
        }
        MessageObject messageObject = this.currentMessageObject;
        String str = "chat_inTimeSelectedText";
        String str2 = "chat_outTimeText";
        if (messageObject.type == 5) {
            TextPaint textPaint = Theme.chat_timePaint;
            int themedColor = getThemedColor("chat_serviceText");
            if (isDrawSelectionBackground()) {
                if (this.currentMessageObject.isOutOwner()) {
                    str = "chat_outTimeSelectedText";
                }
            } else {
                str = this.currentMessageObject.isOutOwner() ? str2 : "chat_inTimeText";
            }
            textPaint.setColor(ColorUtils.blendARGB(themedColor, getThemedColor(str), getVideoTranscriptionProgress()));
        } else if (this.mediaBackground) {
            if (messageObject.shouldDrawWithoutBackground()) {
                Theme.chat_timePaint.setColor(getThemedColor("chat_serviceText"));
            } else {
                Theme.chat_timePaint.setColor(getThemedColor("chat_mediaTimeText"));
            }
        } else if (messageObject.isOutOwner()) {
            TextPaint textPaint2 = Theme.chat_timePaint;
            if (isDrawSelectionBackground()) {
                str2 = "chat_outTimeSelectedText";
            }
            textPaint2.setColor(getThemedColor(str2));
        } else {
            TextPaint textPaint3 = Theme.chat_timePaint;
            if (!isDrawSelectionBackground()) {
                str = "chat_inTimeText";
            }
            textPaint3.setColor(getThemedColor(str));
        }
        drawBackgroundInternal(canvas, false);
        long j = 17;
        if (this.isHighlightedAnimated) {
            long currentTimeMillis = System.currentTimeMillis();
            long abs = Math.abs(currentTimeMillis - this.lastHighlightProgressTime);
            if (abs > 17) {
                abs = 17;
            }
            int i6 = (int) (this.highlightProgress - abs);
            this.highlightProgress = i6;
            this.lastHighlightProgressTime = currentTimeMillis;
            if (i6 <= 0) {
                this.highlightProgress = 0;
                this.isHighlightedAnimated = false;
            }
            invalidate();
            if (getParent() != null) {
                ((View) getParent()).invalidate();
            }
        }
        int i7 = Integer.MIN_VALUE;
        if (this.alphaInternal != 1.0f) {
            int measuredHeight = getMeasuredHeight();
            int measuredWidth = getMeasuredWidth();
            Theme.MessageDrawable messageDrawable2 = this.currentBackgroundDrawable;
            if (messageDrawable2 != null) {
                i4 = messageDrawable2.getBounds().top;
                i3 = this.currentBackgroundDrawable.getBounds().bottom;
                i2 = this.currentBackgroundDrawable.getBounds().left;
                i = this.currentBackgroundDrawable.getBounds().right;
            } else {
                i = measuredWidth;
                i2 = 0;
                i3 = measuredHeight;
                i4 = 0;
            }
            if (this.drawSideButton != 0) {
                if (this.currentMessageObject.isOutOwner()) {
                    i2 -= AndroidUtilities.dp(40.0f);
                } else {
                    i += AndroidUtilities.dp(40.0f);
                }
            }
            if (getY() < 0.0f) {
                i4 = (int) (-getY());
            }
            float y = getY() + getMeasuredHeight();
            int i8 = this.parentHeight;
            if (y > i8) {
                i3 = (int) (i8 - getY());
            }
            this.rect.set(i2, i4, i, i3);
            i7 = canvas.saveLayerAlpha(this.rect, (int) (this.alphaInternal * 255.0f), 31);
        }
        if (!this.transitionParams.animateBackgroundBoundsInner || (messageDrawable = this.currentBackgroundDrawable) == null || this.isRoundVideo) {
            z = false;
        } else {
            Rect bounds = messageDrawable.getBounds();
            canvas.save();
            canvas.clipRect(bounds.left + AndroidUtilities.dp(4.0f), bounds.top + AndroidUtilities.dp(4.0f), bounds.right - AndroidUtilities.dp(4.0f), bounds.bottom - AndroidUtilities.dp(4.0f));
            z = true;
        }
        drawContent(canvas);
        if (z) {
            canvas.restore();
        }
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate == null || chatMessageCellDelegate.canDrawOutboundsContent() || this.transitionParams.messageEntering || getAlpha() != 1.0f) {
            drawOutboundsContent(canvas);
        }
        if (this.replyNameLayout != null) {
            this.replyHeight = AndroidUtilities.dp(7.0f) + Theme.chat_replyNamePaint.getTextSize() + Theme.chat_replyTextPaint.getTextSize();
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                if (this.currentMessageObject.isOutOwner()) {
                    int dp = AndroidUtilities.dp(23.0f);
                    this.replyStartX = dp;
                    if (this.isPlayingRound) {
                        this.replyStartX = dp - (AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize);
                    }
                } else if (this.currentMessageObject.type == 5) {
                    this.replyStartX = this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(4.0f);
                } else {
                    this.replyStartX = this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(17.0f);
                }
                if (this.drawForwardedName) {
                    int dp2 = AndroidUtilities.dp(4.0f) + (((int) Theme.chat_forwardNamePaint.getTextSize()) * 2);
                    this.forwardHeight = dp2;
                    this.replyStartY = this.forwardNameY + dp2 + AndroidUtilities.dp(6.0f);
                } else {
                    int dp3 = AndroidUtilities.dp(12.0f);
                    this.replyStartY = dp3;
                    if (this.drawTopic) {
                        this.replyStartY = dp3 + this.topicHeight + AndroidUtilities.dp(10.0f);
                    }
                }
            } else {
                if (this.currentMessageObject.isOutOwner()) {
                    this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f) + getExtraTextX();
                } else if (this.mediaBackground) {
                    this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f) + getExtraTextX();
                } else {
                    this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(this.drawPinnedBottom ? 12.0f : 18.0f) + getExtraTextX();
                }
                this.forwardHeight = AndroidUtilities.dp(4.0f) + (((int) Theme.chat_forwardNamePaint.getTextSize()) * 2);
                int dp4 = AndroidUtilities.dp(12.0f) + ((!this.drawNameLayout || this.nameLayout == null) ? 0 : AndroidUtilities.dp(6.0f) + ((int) Theme.chat_namePaint.getTextSize())) + ((!this.drawForwardedName || this.forwardedNameLayout[0] == null) ? 0 : AndroidUtilities.dp(4.0f) + this.forwardHeight);
                this.replyStartY = dp4;
                if (this.drawTopic) {
                    this.replyStartY = dp4 + this.topicHeight + AndroidUtilities.dp(5.0f);
                }
            }
        }
        if (this.currentPosition == null && !this.transitionParams.animateBackgroundBoundsInner && (!this.enterTransitionInProgress || this.currentMessageObject.isVoice())) {
            drawNamesLayout(canvas, 1.0f);
        }
        if ((!this.autoPlayingMedia || !MediaController.getInstance().isPlayingMessageAndReadyToDraw(this.currentMessageObject) || this.isRoundVideo) && !this.transitionParams.animateBackgroundBoundsInner) {
            drawOverlays(canvas);
        }
        if ((this.drawTime || !this.mediaBackground) && !this.forceNotDrawTime && !this.transitionParams.animateBackgroundBoundsInner && (!this.enterTransitionInProgress || this.currentMessageObject.isVoice())) {
            drawTime(canvas, 1.0f, false);
        }
        if ((this.controlsAlpha != 1.0f || this.timeAlpha != 1.0f) && this.currentMessageObject.type != 5) {
            long currentTimeMillis2 = System.currentTimeMillis();
            long abs2 = Math.abs(this.lastControlsAlphaChangeTime - currentTimeMillis2);
            if (abs2 <= 17) {
                j = abs2;
            }
            long j2 = this.totalChangeTime + j;
            this.totalChangeTime = j2;
            if (j2 > 200) {
                this.totalChangeTime = 200L;
            }
            this.lastControlsAlphaChangeTime = currentTimeMillis2;
            if (this.controlsAlpha != 1.0f) {
                this.controlsAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) this.totalChangeTime) / 200.0f);
            }
            if (this.timeAlpha != 1.0f) {
                this.timeAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) this.totalChangeTime) / 200.0f);
            }
            invalidate();
            if (this.forceNotDrawTime && (groupedMessagePosition = this.currentPosition) != null && groupedMessagePosition.last && getParent() != null) {
                ((View) getParent()).invalidate();
            }
        }
        if ((this.drawBackground || this.transitionParams.animateDrawBackground) && shouldDrawSelectionOverlay() && this.currentMessagesGroup == null) {
            if (this.selectionOverlayPaint == null) {
                this.selectionOverlayPaint = new Paint(1);
            }
            this.selectionOverlayPaint.setColor(getSelectionOverlayColor().intValue());
            int alpha = this.selectionOverlayPaint.getAlpha();
            this.selectionOverlayPaint.setAlpha((int) (alpha * getHighlightAlpha() * getAlpha()));
            if (this.selectionOverlayPaint.getAlpha() > 0) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams, this.selectionOverlayPaint);
                canvas.restore();
            }
            this.selectionOverlayPaint.setAlpha(alpha);
        }
        if (i7 != Integer.MIN_VALUE) {
            canvas.restoreToCount(i7);
        }
        updateSelectionTextPosition();
    }

    /* JADX WARN: Removed duplicated region for block: B:767:0x0789  */
    /* JADX WARN: Removed duplicated region for block: B:780:0x0839  */
    @android.annotation.SuppressLint({"WrongCall"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawBackgroundInternal(android.graphics.Canvas r28, boolean r29) {
        /*
            Method dump skipped, instructions count: 2237
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawBackgroundInternal(android.graphics.Canvas, boolean):void");
    }

    public boolean drawBackgroundInParent() {
        MessageObject messageObject;
        if (!this.canDrawBackgroundInParent || (messageObject = this.currentMessageObject) == null || !messageObject.isOutOwner()) {
            return false;
        }
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.getCurrentColor("chat_outBubbleGradient") != null : Theme.getColorOrNull("chat_outBubbleGradient") != null;
    }

    public void drawCommentButton(Canvas canvas, float f) {
        if (this.drawSideButton != 3) {
            return;
        }
        int dp = AndroidUtilities.dp(32.0f);
        if (this.commentLayout != null) {
            this.sideStartY -= AndroidUtilities.dp(18.0f);
            dp += AndroidUtilities.dp(18.0f);
        }
        RectF rectF = this.rect;
        float f2 = this.sideStartX;
        rectF.set(f2, this.sideStartY, AndroidUtilities.dp(32.0f) + f2, this.sideStartY + dp);
        applyServiceShaderMatrix();
        String str = "paintChatActionBackground";
        if (f != 1.0f) {
            int alpha = getThemedPaint(str).getAlpha();
            getThemedPaint(str).setAlpha((int) (alpha * f));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), getThemedPaint(str));
            getThemedPaint(str).setAlpha(alpha);
        } else {
            RectF rectF2 = this.rect;
            float dp2 = AndroidUtilities.dp(16.0f);
            float dp3 = AndroidUtilities.dp(16.0f);
            if (this.sideButtonPressed) {
                str = "paintChatActionBackgroundSelected";
            }
            canvas.drawRoundRect(rectF2, dp2, dp3, getThemedPaint(str));
        }
        if (hasGradientService()) {
            if (f != 1.0f) {
                int alpha2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (alpha2 * f));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(alpha2);
            } else {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
        }
        Drawable themeDrawable = Theme.getThemeDrawable("drawableCommentSticker");
        BaseCell.setDrawableBounds(themeDrawable, this.sideStartX + AndroidUtilities.dp(4.0f), this.sideStartY + AndroidUtilities.dp(4.0f));
        if (f != 1.0f) {
            themeDrawable.setAlpha((int) (f * 255.0f));
            themeDrawable.draw(canvas);
            themeDrawable.setAlpha(255);
        } else {
            themeDrawable.draw(canvas);
        }
        if (this.commentLayout == null) {
            return;
        }
        Theme.chat_stickerCommentCountPaint.setColor(getThemedColor("chat_stickerReplyNameText"));
        Theme.chat_stickerCommentCountPaint.setAlpha((int) (f * 255.0f));
        if (this.transitionParams.animateComments) {
            if (this.transitionParams.animateCommentsLayout != null) {
                canvas.save();
                TextPaint textPaint = Theme.chat_stickerCommentCountPaint;
                double d = this.transitionParams.animateChangeProgress;
                Double.isNaN(d);
                double d2 = f;
                Double.isNaN(d2);
                textPaint.setAlpha((int) ((1.0d - d) * 255.0d * d2));
                canvas.translate(this.sideStartX + ((AndroidUtilities.dp(32.0f) - this.transitionParams.animateTotalCommentWidth) / 2), this.sideStartY + AndroidUtilities.dp(30.0f));
                this.transitionParams.animateCommentsLayout.draw(canvas);
                canvas.restore();
            }
            Theme.chat_stickerCommentCountPaint.setAlpha((int) (this.transitionParams.animateChangeProgress * 255.0f));
        }
        canvas.save();
        canvas.translate(this.sideStartX + ((AndroidUtilities.dp(32.0f) - this.totalCommentWidth) / 2), this.sideStartY + AndroidUtilities.dp(30.0f));
        this.commentLayout.draw(canvas);
        canvas.restore();
    }

    public void applyServiceShaderMatrix() {
        applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, getX(), this.viewTop);
    }

    private void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(i, i2, f, f2);
        } else {
            Theme.applyServiceShaderMatrix(i, i2, f, f2);
        }
    }

    public boolean hasOutboundsContent() {
        AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
        if (getAlpha() != 1.0f) {
            return false;
        }
        if ((this.transitionParams.transitionBotButtons.isEmpty() || !this.transitionParams.animateBotButtonsChanged) && this.botButtons.isEmpty() && this.drawSideButton == 0 && ((!this.drawNameLayout || this.nameLayout == null || (swapAnimatedEmojiDrawable = this.currentNameStatusDrawable) == null || swapAnimatedEmojiDrawable.getDrawable() == null) && (((emojiGroupedSpans = this.animatedEmojiStack) == null || emojiGroupedSpans.holders.isEmpty()) && (!this.drawTopic || this.topicIconDrawable == null || ((groupedMessagePosition = this.currentPosition) != null && (groupedMessagePosition.minY != 0 || groupedMessagePosition.minX != 0)))))) {
            if (this.currentMessagesGroup != null) {
                return false;
            }
            TransitionParams transitionParams = this.transitionParams;
            if (((!transitionParams.animateReplaceCaptionLayout || transitionParams.animateChangeProgress == 1.0f) && (transitionParams.animateChangeProgress == 1.0f || !transitionParams.animateMessageText)) || transitionParams.animateOutAnimateEmoji == null || this.transitionParams.animateOutAnimateEmoji.holders.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void drawOutboundsContent(Canvas canvas) {
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        float f;
        float f2;
        float f3;
        int themedColor;
        float f4;
        float f5 = 1.0f;
        if (!this.enterTransitionInProgress) {
            drawAnimatedEmojis(canvas, 1.0f);
        }
        if (this.currentNameStatusDrawable != null && this.drawNameLayout && this.nameLayout != null) {
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                themedColor = getThemedColor("chat_stickerNameText");
                if (this.currentMessageObject.isOutOwner()) {
                    this.nameX = AndroidUtilities.dp(28.0f);
                } else {
                    this.nameX = this.backgroundDrawableLeft + this.transitionParams.deltaLeft + this.backgroundDrawableRight + AndroidUtilities.dp(22.0f);
                }
                this.nameY = this.layoutHeight - AndroidUtilities.dp(38.0f);
                this.nameX -= this.nameOffsetX;
            } else {
                float f6 = 11.0f;
                if (this.mediaBackground || this.currentMessageObject.isOutOwner()) {
                    this.nameX = (((this.backgroundDrawableLeft + this.transitionParams.deltaLeft) + AndroidUtilities.dp(11.0f)) - this.nameOffsetX) + getExtraTextX();
                } else {
                    float f7 = this.backgroundDrawableLeft + this.transitionParams.deltaLeft;
                    if (this.mediaBackground || !this.drawPinnedBottom) {
                        f6 = 17.0f;
                    }
                    this.nameX = ((f7 + AndroidUtilities.dp(f6)) - this.nameOffsetX) + getExtraTextX();
                }
                if (this.currentUser != null) {
                    Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
                    if (messageDrawable != null && messageDrawable.hasGradient()) {
                        themedColor = getThemedColor("chat_messageTextOut");
                    } else {
                        themedColor = getThemedColor(AvatarDrawable.getNameColorNameForId(this.currentUser.id));
                    }
                } else if (this.currentChat != null) {
                    if (this.currentMessageObject.isOutOwner() && ChatObject.isChannel(this.currentChat)) {
                        Theme.MessageDrawable messageDrawable2 = this.currentBackgroundDrawable;
                        if (messageDrawable2 != null && messageDrawable2.hasGradient()) {
                            themedColor = getThemedColor("chat_messageTextOut");
                        } else {
                            themedColor = getThemedColor("chat_outForwardedNameText");
                        }
                    } else if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) {
                        themedColor = Theme.changeColorAccent(getThemedColor(AvatarDrawable.getNameColorNameForId(5L)));
                    } else if (this.currentMessageObject.isOutOwner()) {
                        themedColor = getThemedColor("chat_outForwardedNameText");
                    } else {
                        themedColor = getThemedColor(AvatarDrawable.getNameColorNameForId(this.currentChat.id));
                    }
                } else {
                    themedColor = getThemedColor(AvatarDrawable.getNameColorNameForId(0L));
                }
                this.nameY = AndroidUtilities.dp(this.drawPinnedTop ? 9.0f : 10.0f);
            }
            MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
            if (groupedMessages != null) {
                MessageObject.GroupedMessages.TransitionParams transitionParams = groupedMessages.transitionParams;
                if (transitionParams.backgroundChangeBounds) {
                    this.nameX += transitionParams.offsetLeft;
                    this.nameY += transitionParams.offsetTop - getTranslationY();
                }
            }
            this.nameX += this.animationOffsetX;
            float f8 = this.nameY;
            TransitionParams transitionParams2 = this.transitionParams;
            this.nameY = f8 + transitionParams2.deltaTop;
            if (!transitionParams2.animateSign) {
                f4 = this.nameX;
            } else {
                f4 = this.transitionParams.animateNameX + ((this.nameX - this.transitionParams.animateNameX) * this.transitionParams.animateChangeProgress);
            }
            this.currentNameStatusDrawable.setBounds((int) (Math.abs(f4) + this.nameLayoutWidth + AndroidUtilities.dp(2.0f)), (((int) this.nameY) + (this.nameLayout.getHeight() / 2)) - AndroidUtilities.dp(10.0f), (int) (Math.abs(f4) + this.nameLayoutWidth + AndroidUtilities.dp(22.0f)), (int) (this.nameY + (this.nameLayout.getHeight() / 2) + AndroidUtilities.dp(10.0f)));
            this.currentNameStatusDrawable.setColor(Integer.valueOf(ColorUtils.setAlphaComponent(themedColor, 115)));
            this.currentNameStatusDrawable.draw(canvas);
        }
        if (this.drawTopic && this.topicIconDrawable != null && this.topicHitRect != null && ((groupedMessagePosition = this.currentPosition) == null || (groupedMessagePosition.minY == 0 && groupedMessagePosition.minX == 0))) {
            if (!this.isRoundVideo || this.hasLinkPreview) {
                f = 1.0f;
            } else {
                f = (1.0f - getVideoTranscriptionProgress()) * 1.0f;
                TransitionParams transitionParams3 = this.transitionParams;
                if (transitionParams3.animatePlayingRound) {
                    if (this.isPlayingRound) {
                        f3 = 1.0f - transitionParams3.animateChangeProgress;
                    } else {
                        f3 = transitionParams3.animateChangeProgress;
                    }
                    f *= f3;
                } else if (this.isPlayingRound) {
                    f = 0.0f;
                }
            }
            if (!this.transitionParams.animateForwardedLayout) {
                f2 = 1.0f;
            } else if (!this.currentMessageObject.needDrawForwarded()) {
                f2 = 1.0f - this.transitionParams.animateChangeProgress;
            } else {
                f2 = this.transitionParams.animateChangeProgress;
            }
            canvas.save();
            RectF rectF = this.topicHitRect;
            canvas.translate(rectF.left, rectF.top);
            this.topicIconDrawable.setAlpha((int) (f2 * 255.0f * f));
            this.topicIconDrawable.setBounds(this.topicIconDrawableBounds);
            this.topicIconDrawable.draw(canvas);
            canvas.restore();
        }
        if (!this.transitionParams.transitionBotButtons.isEmpty()) {
            TransitionParams transitionParams4 = this.transitionParams;
            if (transitionParams4.animateBotButtonsChanged) {
                drawBotButtons(canvas, transitionParams4.transitionBotButtons, 1.0f - this.transitionParams.animateChangeProgress);
            }
        }
        if (!this.botButtons.isEmpty()) {
            ArrayList<BotButton> arrayList = this.botButtons;
            TransitionParams transitionParams5 = this.transitionParams;
            if (transitionParams5.animateBotButtonsChanged) {
                f5 = transitionParams5.animateChangeProgress;
            }
            drawBotButtons(canvas, arrayList, f5);
        }
        drawSideButton(canvas);
    }

    public void drawAnimatedEmojis(Canvas canvas, float f) {
        drawAnimatedEmojiMessageText(canvas, f);
    }

    private void drawAnimatedEmojiMessageText(Canvas canvas, float f) {
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateChangeProgress != 1.0f && transitionParams.animateMessageText) {
            canvas.save();
            Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
            if (messageDrawable != null) {
                Rect bounds = messageDrawable.getBounds();
                if (this.currentMessageObject.isOutOwner() && !this.mediaBackground && !this.pinnedBottom) {
                    canvas.clipRect(bounds.left + AndroidUtilities.dp(4.0f), bounds.top + AndroidUtilities.dp(4.0f), bounds.right - AndroidUtilities.dp(10.0f), bounds.bottom - AndroidUtilities.dp(4.0f));
                } else {
                    canvas.clipRect(bounds.left + AndroidUtilities.dp(4.0f), bounds.top + AndroidUtilities.dp(4.0f), bounds.right - AndroidUtilities.dp(4.0f), bounds.bottom - AndroidUtilities.dp(4.0f));
                }
            }
            drawAnimatedEmojiMessageText(canvas, this.transitionParams.animateOutTextBlocks, this.transitionParams.animateOutAnimateEmoji, false, f * (1.0f - this.transitionParams.animateChangeProgress));
            drawAnimatedEmojiMessageText(canvas, this.currentMessageObject.textLayoutBlocks, this.animatedEmojiStack, true, f * this.transitionParams.animateChangeProgress);
            canvas.restore();
            return;
        }
        drawAnimatedEmojiMessageText(canvas, this.currentMessageObject.textLayoutBlocks, this.animatedEmojiStack, true, f);
    }

    private void drawAnimatedEmojiMessageText(Canvas canvas, ArrayList<MessageObject.TextLayoutBlock> arrayList, AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans, boolean z, float f) {
        int size;
        int i;
        if (arrayList == null || arrayList.isEmpty() || f == 0.0f) {
            return;
        }
        if (z) {
            if (this.fullyDraw) {
                this.firstVisibleBlockNum = 0;
                this.lastVisibleBlockNum = arrayList.size();
            }
            i = this.firstVisibleBlockNum;
            size = this.lastVisibleBlockNum;
        } else {
            size = arrayList.size();
            i = 0;
        }
        int i2 = this.textY;
        float f2 = i2;
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateText) {
            float f3 = transitionParams.animateFromTextY;
            float f4 = transitionParams.animateChangeProgress;
            f2 = (f3 * (1.0f - f4)) + (i2 * f4);
        }
        float f5 = f2;
        for (int i3 = i; i3 <= size && i3 < arrayList.size(); i3++) {
            if (i3 >= 0) {
                MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i3);
                canvas.save();
                canvas.translate(this.textX - (textLayoutBlock.isRtl() ? (int) Math.ceil(this.currentMessageObject.textXOffset) : 0), textLayoutBlock.textYOffset + f5 + this.transitionYOffsetForDrawables);
                float f6 = textLayoutBlock.textYOffset + f5 + this.transitionYOffsetForDrawables;
                boolean z2 = this.transitionParams.messageEntering;
                AnimatedEmojiSpan.drawAnimatedEmojis(canvas, textLayoutBlock.textLayout, emojiGroupedSpans, 0.0f, textLayoutBlock.spoilers, 0.0f, 0.0f, f6, f);
                canvas.restore();
            }
        }
    }

    public void drawAnimatedEmojiCaption(Canvas canvas, float f) {
        TransitionParams transitionParams = this.transitionParams;
        if (!transitionParams.animateReplaceCaptionLayout || transitionParams.animateChangeProgress == 1.0f) {
            drawAnimatedEmojiCaption(canvas, this.captionLayout, this.animatedEmojiStack, f);
            return;
        }
        drawAnimatedEmojiCaption(canvas, transitionParams.animateOutCaptionLayout, this.transitionParams.animateOutAnimateEmoji, (1.0f - this.transitionParams.animateChangeProgress) * f);
        drawAnimatedEmojiCaption(canvas, this.captionLayout, this.animatedEmojiStack, f * this.transitionParams.animateChangeProgress);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(8:16|(3:18|(1:20)(2:22|(6:24|25|26|27|28|29)(2:33|(1:37)))|21)|38|25|26|27|28|29) */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x008a, code lost:
        r12 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x008b, code lost:
        org.telegram.messenger.FileLog.e(r12);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawAnimatedEmojiCaption(android.graphics.Canvas r11, android.text.Layout r12, org.telegram.ui.Components.AnimatedEmojiSpan.EmojiGroupedSpans r13, float r14) {
        /*
            r10 = this;
            if (r12 == 0) goto L91
            org.telegram.messenger.MessageObject r0 = r10.currentMessageObject
            boolean r0 = r0.deleted
            if (r0 == 0) goto Lc
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r10.currentPosition
            if (r0 != 0) goto L91
        Lc:
            r0 = 0
            int r1 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
            if (r1 > 0) goto L13
            goto L91
        L13:
            r11.save()
            org.telegram.messenger.MessageObject$GroupedMessages r1 = r10.currentMessagesGroup
            if (r1 == 0) goto L20
            org.telegram.messenger.MessageObject$GroupedMessages$TransitionParams r1 = r1.transitionParams
            float r1 = r1.captionEnterProgress
            float r14 = r14 * r1
        L20:
            r9 = r14
            int r14 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r14 != 0) goto L26
            return
        L26:
            float r14 = r10.captionY
            float r0 = r10.captionX
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r1 = r10.transitionParams
            boolean r2 = r1.animateBackgroundBoundsInner
            if (r2 == 0) goto L7a
            boolean r2 = r1.transformGroupToSingleMessage
            if (r2 == 0) goto L3f
            float r1 = r10.getTranslationY()
            float r14 = r14 - r1
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r1 = r10.transitionParams
            float r1 = r1.deltaLeft
        L3d:
            float r0 = r0 + r1
            goto L7a
        L3f:
            boolean r1 = org.telegram.ui.Cells.ChatMessageCell.TransitionParams.access$5300(r1)
            if (r1 == 0) goto L63
            float r14 = r10.captionX
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r0 = r10.transitionParams
            float r1 = r0.animateChangeProgress
            float r14 = r14 * r1
            float r2 = r0.captionFromX
            r3 = 1065353216(0x3var_, float:1.0)
            float r4 = r3 - r1
            float r2 = r2 * r4
            float r14 = r14 + r2
            float r2 = r10.captionY
            float r2 = r2 * r1
            float r0 = r0.captionFromY
            float r3 = r3 - r1
            float r0 = r0 * r3
            float r2 = r2 + r0
            r0 = r14
            r8 = r2
            goto L7b
        L63:
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            boolean r1 = r1.isVoice()
            if (r1 == 0) goto L75
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            java.lang.CharSequence r1 = r1.caption
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L7a
        L75:
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r1 = r10.transitionParams
            float r1 = r1.deltaLeft
            goto L3d
        L7a:
            r8 = r14
        L7b:
            r11.translate(r0, r8)
            r4 = 0
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r5 = r10.captionSpoilers     // Catch: java.lang.Exception -> L8a
            r6 = 0
            r7 = 0
            r1 = r11
            r2 = r12
            r3 = r13
            org.telegram.ui.Components.AnimatedEmojiSpan.drawAnimatedEmojis(r1, r2, r3, r4, r5, r6, r7, r8, r9)     // Catch: java.lang.Exception -> L8a
            goto L8e
        L8a:
            r12 = move-exception
            org.telegram.messenger.FileLog.e(r12)
        L8e:
            r11.restore()
        L91:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawAnimatedEmojiCaption(android.graphics.Canvas, android.text.Layout, org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans, float):void");
    }

    private void drawSideButton(Canvas canvas) {
        if (this.drawSideButton != 0) {
            if (this.currentMessageObject.isOutOwner()) {
                float dp = this.transitionParams.lastBackgroundLeft - AndroidUtilities.dp(40.0f);
                this.sideStartX = dp;
                MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
                if (groupedMessages != null) {
                    this.sideStartX = dp + (groupedMessages.transitionParams.offsetLeft - this.animationOffsetX);
                }
            } else {
                float dp2 = this.transitionParams.lastBackgroundRight + AndroidUtilities.dp(8.0f);
                this.sideStartX = dp2;
                MessageObject.GroupedMessages groupedMessages2 = this.currentMessagesGroup;
                if (groupedMessages2 != null) {
                    this.sideStartX = dp2 + (groupedMessages2.transitionParams.offsetRight - this.animationOffsetX);
                }
            }
            float dp3 = (this.layoutHeight + this.transitionParams.deltaBottom) - AndroidUtilities.dp(41.0f);
            this.sideStartY = dp3;
            MessageObject.GroupedMessages groupedMessages3 = this.currentMessagesGroup;
            if (groupedMessages3 != null) {
                MessageObject.GroupedMessages.TransitionParams transitionParams = groupedMessages3.transitionParams;
                float f = dp3 + transitionParams.offsetBottom;
                this.sideStartY = f;
                if (transitionParams.backgroundChangeBounds) {
                    this.sideStartY = f - getTranslationY();
                }
            }
            ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
            float f2 = 0.0f;
            if (!reactionsLayoutInBubble.isSmall) {
                if (this.isRoundVideo) {
                    this.sideStartY -= reactionsLayoutInBubble.getCurrentTotalHeight(this.transitionParams.animateChangeProgress) * (1.0f - getVideoTranscriptionProgress());
                } else if (reactionsLayoutInBubble.drawServiceShaderBackground > 0.0f) {
                    this.sideStartY -= reactionsLayoutInBubble.getCurrentTotalHeight(this.transitionParams.animateChangeProgress);
                }
            }
            if (!this.currentMessageObject.isOutOwner() && this.isRoundVideo && !this.hasLinkPreview) {
                float dp4 = this.isAvatarVisible ? (AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize) * 0.7f : AndroidUtilities.dp(50.0f);
                float videoTranscriptionProgress = this.isPlayingRound ? (1.0f - getVideoTranscriptionProgress()) * dp4 : 0.0f;
                if (this.isPlayingRound) {
                    f2 = AndroidUtilities.dp(28.0f) * (1.0f - getVideoTranscriptionProgress());
                }
                TransitionParams transitionParams2 = this.transitionParams;
                if (transitionParams2.animatePlayingRound) {
                    videoTranscriptionProgress = (this.isPlayingRound ? transitionParams2.animateChangeProgress : 1.0f - transitionParams2.animateChangeProgress) * (1.0f - getVideoTranscriptionProgress()) * dp4;
                    f2 = AndroidUtilities.dp(28.0f) * (this.isPlayingRound ? this.transitionParams.animateChangeProgress : 1.0f - this.transitionParams.animateChangeProgress) * (1.0f - getVideoTranscriptionProgress());
                }
                this.sideStartX -= videoTranscriptionProgress;
                this.sideStartY -= f2;
            }
            if (this.drawSideButton == 3) {
                if (this.enterTransitionInProgress && !this.currentMessageObject.isVoice()) {
                    return;
                }
                drawCommentButton(canvas, 1.0f);
                return;
            }
            RectF rectF = this.rect;
            float f3 = this.sideStartX;
            rectF.set(f3, this.sideStartY, AndroidUtilities.dp(32.0f) + f3, this.sideStartY + AndroidUtilities.dp(32.0f));
            applyServiceShaderMatrix();
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), getThemedPaint(this.sideButtonPressed ? "paintChatActionBackgroundSelected" : "paintChatActionBackground"));
            if (hasGradientService()) {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            if (this.drawSideButton == 2) {
                Drawable themedDrawable = getThemedDrawable("drawableGoIcon");
                if (this.currentMessageObject.isOutOwner()) {
                    BaseCell.setDrawableBounds(themedDrawable, this.sideStartX + AndroidUtilities.dp(10.0f), this.sideStartY + AndroidUtilities.dp(9.0f));
                    canvas.save();
                    canvas.scale(-1.0f, 1.0f, themedDrawable.getBounds().centerX(), themedDrawable.getBounds().centerY());
                } else {
                    BaseCell.setDrawableBounds(themedDrawable, this.sideStartX + AndroidUtilities.dp(12.0f), this.sideStartY + AndroidUtilities.dp(9.0f));
                }
                themedDrawable.draw(canvas);
                if (!this.currentMessageObject.isOutOwner()) {
                    return;
                }
                canvas.restore();
                return;
            }
            Drawable themedDrawable2 = getThemedDrawable("drawableShareIcon");
            BaseCell.setDrawableBounds(themedDrawable2, this.sideStartX + AndroidUtilities.dp(8.0f), this.sideStartY + AndroidUtilities.dp(9.0f));
            themedDrawable2.draw(canvas);
        }
    }

    public void setTimeAlpha(float f) {
        this.timeAlpha = f;
    }

    public float getTimeAlpha() {
        return this.timeAlpha;
    }

    public int getBackgroundDrawableLeft() {
        int dp;
        int dp2;
        int i;
        int i2 = 0;
        if (this.currentMessageObject.isOutOwner()) {
            if (this.isRoundVideo) {
                return (this.layoutWidth - this.backgroundWidth) - ((int) ((1.0f - getVideoTranscriptionProgress()) * AndroidUtilities.dp(9.0f)));
            }
            int i3 = this.layoutWidth - this.backgroundWidth;
            if (this.mediaBackground) {
                i2 = AndroidUtilities.dp(9.0f);
            }
            return i3 - i2;
        }
        int i4 = 3;
        if (this.isRoundVideo) {
            if (this.isChat && this.isAvatarVisible) {
                i2 = 48;
            }
            dp = AndroidUtilities.dp(i2 + 3) + ((int) (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress())));
        } else {
            if (this.isChat && this.isAvatarVisible) {
                i2 = 48;
            }
            if (this.mediaBackground) {
                i4 = 9;
            }
            dp = AndroidUtilities.dp(i2 + i4);
        }
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.isDocuments && (i = this.currentPosition.leftSpanOffset) != 0) {
            dp += (int) Math.ceil((i / 1000.0f) * getGroupPhotosWidth());
        }
        if (this.isRoundVideo) {
            if (!this.drawPinnedBottom) {
                return dp;
            }
            dp2 = (int) (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress()));
        } else if (this.mediaBackground || !this.drawPinnedBottom) {
            return dp;
        } else {
            dp2 = AndroidUtilities.dp(6.0f);
        }
        return dp + dp2;
    }

    public int getBackgroundDrawableRight() {
        int dp;
        int backgroundDrawableLeft;
        int i = this.backgroundWidth;
        if (this.isRoundVideo) {
            dp = i - ((int) (getVideoTranscriptionProgress() * AndroidUtilities.dp(3.0f)));
            if (this.drawPinnedBottom && this.currentMessageObject.isOutOwner()) {
                dp = (int) (dp - (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress())));
            }
            if (this.drawPinnedBottom && !this.currentMessageObject.isOutOwner()) {
                dp = (int) (dp - (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress())));
            }
            backgroundDrawableLeft = getBackgroundDrawableLeft();
        } else {
            dp = i - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
            if (!this.mediaBackground && this.drawPinnedBottom) {
                dp -= AndroidUtilities.dp(6.0f);
            }
            backgroundDrawableLeft = getBackgroundDrawableLeft();
        }
        return backgroundDrawableLeft + dp;
    }

    public int getBackgroundDrawableTop() {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        int i = 0;
        int dp = (groupedMessagePosition == null || (groupedMessagePosition.flags & 4) != 0) ? 0 : 0 - AndroidUtilities.dp(3.0f);
        if (!this.drawPinnedTop) {
            i = AndroidUtilities.dp(1.0f);
        }
        int i2 = dp + i;
        return (this.mediaBackground || !this.drawPinnedTop) ? i2 : i2 - AndroidUtilities.dp(1.0f);
    }

    public int getBackgroundDrawableBottom() {
        int i;
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        int i2 = 0;
        if (groupedMessagePosition != null) {
            int i3 = 4;
            i = (groupedMessagePosition.flags & 4) == 0 ? AndroidUtilities.dp(3.0f) + 0 : 0;
            if ((this.currentPosition.flags & 8) == 0) {
                if (this.currentMessageObject.isOutOwner()) {
                    i3 = 3;
                }
                i += AndroidUtilities.dp(i3);
            }
        } else {
            i = 0;
        }
        boolean z = this.drawPinnedBottom;
        if (!z || !this.drawPinnedTop) {
            if (z) {
                i2 = AndroidUtilities.dp(1.0f);
            } else {
                i2 = AndroidUtilities.dp(2.0f);
            }
        }
        int backgroundDrawableTop = ((getBackgroundDrawableTop() + this.layoutHeight) - i2) + i;
        if (!this.mediaBackground) {
            if (this.drawPinnedTop) {
                backgroundDrawableTop += AndroidUtilities.dp(1.0f);
            }
            return this.drawPinnedBottom ? backgroundDrawableTop + AndroidUtilities.dp(1.0f) : backgroundDrawableTop;
        }
        return backgroundDrawableTop;
    }

    /* JADX WARN: Removed duplicated region for block: B:74:0x008b  */
    /* JADX WARN: Removed duplicated region for block: B:79:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawBackground(android.graphics.Canvas r16, int r17, int r18, int r19, int r20, boolean r21, boolean r22, boolean r23, int r24) {
        /*
            r15 = this;
            r0 = r15
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            r5 = r20
            org.telegram.messenger.MessageObject r6 = r0.currentMessageObject
            boolean r6 = r6.isOutOwner()
            if (r6 == 0) goto L39
            boolean r6 = r0.mediaBackground
            if (r6 != 0) goto L29
            if (r22 != 0) goto L29
            if (r23 == 0) goto L1e
            java.lang.String r6 = "drawableMsgOutSelected"
            goto L20
        L1e:
            java.lang.String r6 = "drawableMsgOut"
        L20:
            android.graphics.drawable.Drawable r6 = r15.getThemedDrawable(r6)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = (org.telegram.ui.ActionBar.Theme.MessageDrawable) r6
            r0.currentBackgroundDrawable = r6
            goto L5e
        L29:
            if (r23 == 0) goto L2e
            java.lang.String r6 = "drawableMsgOutMediaSelected"
            goto L30
        L2e:
            java.lang.String r6 = "drawableMsgOutMedia"
        L30:
            android.graphics.drawable.Drawable r6 = r15.getThemedDrawable(r6)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = (org.telegram.ui.ActionBar.Theme.MessageDrawable) r6
            r0.currentBackgroundDrawable = r6
            goto L5e
        L39:
            boolean r6 = r0.mediaBackground
            if (r6 != 0) goto L4f
            if (r22 != 0) goto L4f
            if (r23 == 0) goto L44
            java.lang.String r6 = "drawableMsgInSelected"
            goto L46
        L44:
            java.lang.String r6 = "drawableMsgIn"
        L46:
            android.graphics.drawable.Drawable r6 = r15.getThemedDrawable(r6)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = (org.telegram.ui.ActionBar.Theme.MessageDrawable) r6
            r0.currentBackgroundDrawable = r6
            goto L5e
        L4f:
            if (r23 == 0) goto L54
            java.lang.String r6 = "drawableMsgInMediaSelected"
            goto L56
        L54:
            java.lang.String r6 = "drawableMsgInMedia"
        L56:
            android.graphics.drawable.Drawable r6 = r15.getThemedDrawable(r6)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = (org.telegram.ui.ActionBar.Theme.MessageDrawable) r6
            r0.currentBackgroundDrawable = r6
        L5e:
            int r6 = r0.parentWidth
            int r7 = r0.parentHeight
            if (r7 != 0) goto L85
            int r6 = r15.getParentWidth()
            android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
            int r7 = r7.y
            android.view.ViewParent r8 = r15.getParent()
            boolean r8 = r8 instanceof android.view.View
            if (r8 == 0) goto L85
            android.view.ViewParent r6 = r15.getParent()
            android.view.View r6 = (android.view.View) r6
            int r7 = r6.getMeasuredWidth()
            int r6 = r6.getMeasuredHeight()
            r9 = r6
            r8 = r7
            goto L87
        L85:
            r8 = r6
            r9 = r7
        L87:
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = r0.currentBackgroundDrawable
            if (r6 == 0) goto Ld7
            float r7 = r0.parentViewTopOffset
            int r10 = (int) r7
            int r11 = r0.blurredViewTopOffset
            int r12 = r0.blurredViewBottomOffset
            r7 = r24
            r13 = r21
            r14 = r22
            r6.setTop(r7, r8, r9, r10, r11, r12, r13, r14)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = r0.currentBackgroundDrawable
            android.graphics.drawable.Drawable r6 = r6.getShadowDrawable()
            r7 = 255(0xff, float:3.57E-43)
            r8 = 1132396544(0x437var_, float:255.0)
            if (r6 == 0) goto Lba
            float r9 = r15.getAlpha()
            float r9 = r9 * r8
            int r9 = (int) r9
            r6.setAlpha(r9)
            r6.setBounds(r2, r3, r4, r5)
            r6.draw(r1)
            r6.setAlpha(r7)
        Lba:
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = r0.currentBackgroundDrawable
            float r9 = r15.getAlpha()
            float r9 = r9 * r8
            int r8 = (int) r9
            r6.setAlpha(r8)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = r0.currentBackgroundDrawable
            r6.setBounds(r2, r3, r4, r5)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r2 = r0.currentBackgroundDrawable
            org.telegram.ui.ActionBar.Theme$MessageDrawable$PathDrawParams r3 = r0.backgroundCacheParams
            r2.drawCached(r1, r3)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.currentBackgroundDrawable
            r1.setAlpha(r7)
        Ld7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawBackground(android.graphics.Canvas, int, int, int, int, boolean, boolean, boolean, int):void");
    }

    public boolean hasNameLayout() {
        if (!this.drawNameLayout || this.nameLayout == null) {
            if (this.drawForwardedName) {
                StaticLayout[] staticLayoutArr = this.forwardedNameLayout;
                if (staticLayoutArr[0] != null && staticLayoutArr[1] != null) {
                    MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
                    if (groupedMessagePosition == null) {
                        return true;
                    }
                    if (groupedMessagePosition.minY == 0 && groupedMessagePosition.minX == 0) {
                        return true;
                    }
                }
            }
            return this.replyNameLayout != null || this.drawTopic;
        }
        return true;
    }

    public boolean isDrawNameLayout() {
        return this.drawNameLayout && this.nameLayout != null;
    }

    public boolean isDrawTopic() {
        return this.drawTopic;
    }

    public float getDrawTopicHeight() {
        return this.topicHeight;
    }

    public boolean isAdminLayoutChanged() {
        return !TextUtils.equals(this.lastPostAuthor, this.currentMessageObject.messageOwner.post_author);
    }

    /* JADX WARN: Removed duplicated region for block: B:1001:0x0834  */
    /* JADX WARN: Removed duplicated region for block: B:1010:0x0892  */
    /* JADX WARN: Removed duplicated region for block: B:1013:0x0899  */
    /* JADX WARN: Removed duplicated region for block: B:857:0x04b6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawNamesLayout(android.graphics.Canvas r33, float r34) {
        /*
            Method dump skipped, instructions count: 4440
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawNamesLayout(android.graphics.Canvas, float):void");
    }

    public boolean hasCaptionLayout() {
        return this.captionLayout != null;
    }

    public boolean hasCommentLayout() {
        return this.drawCommentButton;
    }

    public StaticLayout getCaptionLayout() {
        return this.captionLayout;
    }

    public void setDrawSelectionBackground(boolean z) {
        if (this.drawSelectionBackground != z) {
            this.drawSelectionBackground = z;
            invalidate();
        }
    }

    public boolean isDrawingSelectionBackground() {
        return this.drawSelectionBackground || this.isHighlightedAnimated || this.isHighlighted;
    }

    public float getHighlightAlpha() {
        int i;
        if (this.drawSelectionBackground || !this.isHighlightedAnimated || (i = this.highlightProgress) >= 300) {
            return 1.0f;
        }
        return i / 300.0f;
    }

    public void setCheckBoxVisible(boolean z, boolean z2) {
        MessageObject.GroupedMessages groupedMessages;
        MessageObject.GroupedMessages groupedMessages2;
        if (z && this.checkBox == null) {
            CheckBoxBase checkBoxBase = new CheckBoxBase(this, 21, this.resourcesProvider);
            this.checkBox = checkBoxBase;
            if (this.attachedToWindow) {
                checkBoxBase.onAttachedToWindow();
            }
        }
        if (z && this.mediaCheckBox == null && (((groupedMessages = this.currentMessagesGroup) != null && groupedMessages.messages.size() > 1) || ((groupedMessages2 = this.groupedMessagesToSet) != null && groupedMessages2.messages.size() > 1))) {
            CheckBoxBase checkBoxBase2 = new CheckBoxBase(this, 21, this.resourcesProvider);
            this.mediaCheckBox = checkBoxBase2;
            checkBoxBase2.setUseDefaultCheck(true);
            if (this.attachedToWindow) {
                this.mediaCheckBox.onAttachedToWindow();
            }
        }
        float f = 1.0f;
        if (this.checkBoxVisible == z) {
            if (z2 == this.checkBoxAnimationInProgress || z2) {
                return;
            }
            if (!z) {
                f = 0.0f;
            }
            this.checkBoxAnimationProgress = f;
            invalidate();
            return;
        }
        this.checkBoxAnimationInProgress = z2;
        this.checkBoxVisible = z;
        if (z2) {
            this.lastCheckBoxAnimationTime = SystemClock.elapsedRealtime();
        } else {
            if (!z) {
                f = 0.0f;
            }
            this.checkBoxAnimationProgress = f;
        }
        invalidate();
    }

    public void setChecked(boolean z, boolean z2, boolean z3) {
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.setChecked(z2, z3);
        }
        CheckBoxBase checkBoxBase2 = this.mediaCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.setChecked(z, z3);
        }
        this.backgroundDrawable.setSelected(z2, z3);
    }

    public void setLastTouchCoords(float f, float f2) {
        this.lastTouchX = f;
        this.lastTouchY = f2;
        this.backgroundDrawable.setTouchCoords(f + getTranslationX(), this.lastTouchY);
    }

    public MessageBackgroundDrawable getBackgroundDrawable() {
        return this.backgroundDrawable;
    }

    public Theme.MessageDrawable getCurrentBackgroundDrawable(boolean z) {
        if (z) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            boolean z2 = groupedMessagePosition != null && (groupedMessagePosition.flags & 8) == 0 && this.currentMessagesGroup.isDocuments && !this.drawPinnedBottom;
            if (this.currentMessageObject.isOutOwner()) {
                if (!this.mediaBackground && !this.drawPinnedBottom && !z2) {
                    this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOut");
                } else {
                    this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutMedia");
                }
            } else if (!this.mediaBackground && !this.drawPinnedBottom && !z2) {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgIn");
            } else {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMedia");
            }
        }
        this.currentBackgroundDrawable.getBackgroundDrawable();
        return this.currentBackgroundDrawable;
    }

    private boolean shouldDrawCaptionLayout() {
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        MessageObject.GroupedMessages groupedMessages;
        MessageObject messageObject = this.currentMessageObject;
        return !messageObject.preview && ((groupedMessagePosition = this.currentPosition) == null || ((groupedMessages = this.currentMessagesGroup) != null && groupedMessages.isDocuments && (groupedMessagePosition.flags & 8) == 0)) && !this.transitionParams.animateBackgroundBoundsInner && (!this.enterTransitionInProgress || !messageObject.isVoice());
    }

    public void drawCaptionLayout(Canvas canvas, boolean z, float f) {
        if (this.animatedEmojiStack != null && (this.captionLayout != null || this.transitionParams.animateOutCaptionLayout != null)) {
            this.animatedEmojiStack.clearPositions();
        }
        TransitionParams transitionParams = this.transitionParams;
        if (!transitionParams.animateReplaceCaptionLayout || transitionParams.animateChangeProgress == 1.0f) {
            drawCaptionLayout(canvas, this.captionLayout, z, f);
        } else {
            drawCaptionLayout(canvas, transitionParams.animateOutCaptionLayout, z, (1.0f - this.transitionParams.animateChangeProgress) * f);
            drawCaptionLayout(canvas, this.captionLayout, z, this.transitionParams.animateChangeProgress * f);
        }
        if (!z) {
            drawAnimatedEmojiCaption(canvas, f);
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.messageOwner != null && messageObject.isVoiceTranscriptionOpen()) {
            MessageObject messageObject2 = this.currentMessageObject;
            if (!messageObject2.messageOwner.voiceTranscriptionFinal && TranscribeButton.isTranscribing(messageObject2)) {
                invalidate();
            }
        }
        if (this.isRoundVideo) {
            this.reactionsLayoutInBubble.drawServiceShaderBackground = 1.0f - getVideoTranscriptionProgress();
        }
        if (!z) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition != null) {
                int i = groupedMessagePosition.flags;
                if ((i & 8) == 0 || (i & 1) == 0) {
                    return;
                }
            }
            ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
            if (reactionsLayoutInBubble.isSmall) {
                return;
            }
            if (reactionsLayoutInBubble.drawServiceShaderBackground > 0.0f) {
                applyServiceShaderMatrix();
            }
            if (getAlpha() != 1.0f) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                canvas.saveLayerAlpha(rectF, (int) (getAlpha() * 255.0f), 31);
            }
            ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
            if (reactionsLayoutInBubble2.drawServiceShaderBackground > 0.0f || !this.transitionParams.animateBackgroundBoundsInner || this.currentPosition != null || this.isRoundVideo) {
                TransitionParams transitionParams2 = this.transitionParams;
                reactionsLayoutInBubble2.draw(canvas, transitionParams2.animateChange ? transitionParams2.animateChangeProgress : 1.0f, null);
            } else {
                canvas.save();
                canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), getBackgroundDrawableBottom() + this.transitionParams.deltaBottom);
                ReactionsLayoutInBubble reactionsLayoutInBubble3 = this.reactionsLayoutInBubble;
                TransitionParams transitionParams3 = this.transitionParams;
                reactionsLayoutInBubble3.draw(canvas, transitionParams3.animateChange ? transitionParams3.animateChangeProgress : 1.0f, null);
                canvas.restore();
            }
            if (getAlpha() == 1.0f) {
                return;
            }
            canvas.restore();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:660:0x057e  */
    /* JADX WARN: Removed duplicated region for block: B:670:0x05ca  */
    /* JADX WARN: Removed duplicated region for block: B:671:0x05e4  */
    /* JADX WARN: Removed duplicated region for block: B:674:0x05f3  */
    /* JADX WARN: Removed duplicated region for block: B:675:0x05f8  */
    /* JADX WARN: Removed duplicated region for block: B:678:0x0612  */
    /* JADX WARN: Removed duplicated region for block: B:680:0x0616  */
    /* JADX WARN: Removed duplicated region for block: B:686:0x062d  */
    /* JADX WARN: Removed duplicated region for block: B:703:0x06d2  */
    /* JADX WARN: Removed duplicated region for block: B:844:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawCaptionLayout(android.graphics.Canvas r27, android.text.StaticLayout r28, boolean r29, float r30) {
        /*
            Method dump skipped, instructions count: 2504
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawCaptionLayout(android.graphics.Canvas, android.text.StaticLayout, boolean, float):void");
    }

    public boolean needDrawTime() {
        return !this.forceNotDrawTime;
    }

    public boolean shouldDrawTimeOnMedia() {
        int i = this.overideShouldDrawTimeOnMedia;
        if (i != 0) {
            return i == 1;
        } else if (!this.mediaBackground || this.captionLayout != null) {
            return false;
        } else {
            ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
            return reactionsLayoutInBubble.isEmpty || reactionsLayoutInBubble.isSmall || this.currentMessageObject.isAnyKindOfSticker() || this.currentMessageObject.isRoundVideo();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:125:0x0102  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x0126  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawTime(android.graphics.Canvas r17, float r18, boolean r19) {
        /*
            Method dump skipped, instructions count: 332
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawTime(android.graphics.Canvas, float, boolean):void");
    }

    private void drawTimeInternal(Canvas canvas, float f, boolean z, float f2, StaticLayout staticLayout, float f3, boolean z2) {
        int i;
        float f4;
        float f5;
        char c;
        char c2;
        int i2;
        int i3;
        boolean z3;
        float f6;
        char c3;
        float dp;
        boolean z4;
        Paint themedPaint;
        float f7;
        int dp2;
        float imageY2;
        int i4;
        int i5;
        float f8;
        float currentWidth;
        TextPaint textPaint;
        if (((!this.drawTime || this.groupPhotoInvisible) && shouldDrawTimeOnMedia()) || staticLayout == null) {
            return;
        }
        MessageObject messageObject = this.currentMessageObject;
        if ((messageObject.deleted && this.currentPosition != null) || (i = messageObject.type) == 16) {
            return;
        }
        String str = "chat_outTimeSelectedText";
        String str2 = "chat_inTimeSelectedText";
        if (i == 5) {
            TextPaint textPaint2 = Theme.chat_timePaint;
            int themedColor = getThemedColor("chat_serviceText");
            if (isDrawSelectionBackground()) {
                if (!this.currentMessageObject.isOutOwner()) {
                    str = str2;
                }
            } else {
                str = this.currentMessageObject.isOutOwner() ? "chat_outTimeText" : "chat_inTimeText";
            }
            textPaint2.setColor(ColorUtils.blendARGB(themedColor, getThemedColor(str), getVideoTranscriptionProgress()));
        } else if (shouldDrawTimeOnMedia()) {
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                Theme.chat_timePaint.setColor(getThemedColor("chat_serviceText"));
            } else {
                Theme.chat_timePaint.setColor(getThemedColor("chat_mediaTimeText"));
            }
        } else if (this.currentMessageObject.isOutOwner()) {
            TextPaint textPaint3 = Theme.chat_timePaint;
            if (!z2) {
                str = "chat_outTimeText";
            }
            textPaint3.setColor(getThemedColor(str));
        } else {
            TextPaint textPaint4 = Theme.chat_timePaint;
            if (!z2) {
                str2 = "chat_inTimeText";
            }
            textPaint4.setColor(getThemedColor(str2));
        }
        float f9 = getTransitionParams().animateDrawingTimeAlpha ? getTransitionParams().animateChangeProgress * f : f;
        if (f9 != 1.0f) {
            Theme.chat_timePaint.setAlpha((int) (textPaint.getAlpha() * f9));
        }
        canvas.save();
        float var_ = 2.0f;
        if (this.drawPinnedBottom && !shouldDrawTimeOnMedia()) {
            canvas.translate(0.0f, AndroidUtilities.dp(2.0f));
        }
        float var_ = this.layoutHeight;
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateBackgroundBoundsInner) {
            var_ += transitionParams.deltaBottom;
        }
        if (transitionParams.shouldAnimateTimeX) {
            float var_ = transitionParams.animateChangeProgress;
            f4 = (transitionParams.animateFromTimeX * (1.0f - var_)) + (this.timeX * var_);
        } else {
            f4 = f2;
        }
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages == null || !groupedMessages.transitionParams.backgroundChangeBounds) {
            f5 = f2;
        } else {
            var_ -= getTranslationY();
            float var_ = this.currentMessagesGroup.transitionParams.offsetRight;
            f5 = f2 + var_;
            f4 += var_;
        }
        if (this.drawPinnedBottom && shouldDrawTimeOnMedia()) {
            var_ += AndroidUtilities.dp(1.0f);
        }
        float var_ = var_;
        TransitionParams transitionParams2 = this.transitionParams;
        boolean z5 = transitionParams2.animateBackgroundBoundsInner;
        if (z5) {
            float var_ = this.animationOffsetX;
            f5 += var_;
            f4 += var_;
        }
        float var_ = f5;
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble.isSmall) {
            if (z5 && transitionParams2.deltaRight != 0.0f) {
                currentWidth = reactionsLayoutInBubble.getCurrentWidth(1.0f);
            } else {
                currentWidth = reactionsLayoutInBubble.getCurrentWidth(transitionParams2.animateChangeProgress);
            }
            f4 += currentWidth;
        }
        if (this.transitionParams.animateEditedEnter) {
            f4 -= this.transitionParams.animateEditedWidthDiff * (1.0f - this.transitionParams.animateChangeProgress);
        }
        float var_ = f4;
        boolean z6 = true;
        if (shouldDrawTimeOnMedia()) {
            int i6 = -(this.drawCommentButton ? AndroidUtilities.dp(41.3f) : 0);
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                themedPaint = getThemedPaint("paintChatActionBackground");
            } else {
                themedPaint = getThemedPaint("paintChatTimeBackground");
            }
            int alpha = themedPaint.getAlpha();
            Theme.chat_timePaint.setAlpha((int) (this.timeAlpha * 255.0f * f9));
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 == null || messageObject2.type != 4) {
                f7 = f9;
            } else {
                float currentAlpha = this.photoImage.isCrossfadingWithOldImage() ? 1.0f : this.photoImage.getCurrentAlpha();
                if (!this.photoImage.hasNotThumb()) {
                    currentAlpha = 0.0f;
                }
                f7 = AndroidUtilities.lerp(0.35f, 1.0f, currentAlpha);
            }
            themedPaint.setAlpha((int) (alpha * this.timeAlpha * f7));
            int i7 = this.documentAttachType;
            if (i7 != 7 && i7 != 6 && this.currentMessageObject.type != 19) {
                int[] roundRadius = this.photoImage.getRoundRadius();
                dp2 = Math.min(AndroidUtilities.dp(8.0f), Math.max(roundRadius[2], roundRadius[3]));
                z3 = SharedConfig.bubbleRadius >= 10;
            } else {
                dp2 = AndroidUtilities.dp(4.0f);
                z3 = false;
            }
            float dp3 = var_ - AndroidUtilities.dp(z3 ? 6.0f : 4.0f);
            if (this.documentAttachType == 7) {
                imageY2 = var_ - ((AndroidUtilities.dp(this.drawPinnedBottom ? 4.0f : 5.0f) + this.reactionsLayoutInBubble.getCurrentTotalHeight(this.transitionParams.animateChangeProgress)) * (1.0f - getVideoTranscriptionProgress()));
            } else {
                imageY2 = this.photoImage.getImageY2() + this.additionalTimeOffsetY;
            }
            float var_ = imageY2;
            float dp4 = var_ - AndroidUtilities.dp(23.0f);
            float max = Math.max(AndroidUtilities.dp(17.0f), Theme.chat_timePaint.getTextSize() + AndroidUtilities.dp(5.0f));
            RectF rectF = this.rect;
            float var_ = dp3 + f3;
            int i8 = z3 ? 12 : 8;
            if (this.currentMessageObject.isOutOwner()) {
                i4 = (this.currentMessageObject.type == 19 ? 4 : 0) + 20;
            } else {
                i4 = 0;
            }
            rectF.set(dp3, dp4, var_ + AndroidUtilities.dp(i8 + i4), max + dp4);
            applyServiceShaderMatrix();
            float var_ = dp2;
            canvas.drawRoundRect(this.rect, var_, var_, themedPaint);
            if (themedPaint == getThemedPaint("paintChatActionBackground") && hasGradientService()) {
                int alpha2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (alpha2 * this.timeAlpha * f7));
                canvas.drawRoundRect(this.rect, var_, var_, Theme.chat_actionBackgroundGradientDarkenPaint);
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(alpha2);
            }
            themedPaint.setAlpha(alpha);
            float var_ = -staticLayout.getLineLeft(0);
            if (this.reactionsLayoutInBubble.isSmall) {
                updateReactionLayoutPosition();
                this.reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, null);
            }
            if ((!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) && (this.currentMessageObject.messageOwner.flags & 1024) == 0 && this.repliesLayout == null && !this.isPinned) {
                i5 = i6;
                c = 7;
                c2 = 4;
            } else {
                float lineWidth = var_ + (this.timeWidth - staticLayout.getLineWidth(0));
                ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
                if (reactionsLayoutInBubble2.isSmall && !reactionsLayoutInBubble2.isEmpty) {
                    lineWidth -= reactionsLayoutInBubble2.width;
                }
                float var_ = lineWidth;
                int createStatusDrawableParams = this.transitionParams.createStatusDrawableParams();
                int i9 = this.transitionParams.lastStatusDrawableParams;
                if (i9 >= 0 && i9 != createStatusDrawableParams && !this.statusDrawableAnimationInProgress) {
                    createStatusDrawableAnimator(i9, createStatusDrawableParams, z);
                }
                boolean z7 = this.statusDrawableAnimationInProgress;
                if (z7) {
                    createStatusDrawableParams = this.animateToStatusDrawableParams;
                }
                boolean z8 = (createStatusDrawableParams & 4) != 0;
                boolean z9 = (createStatusDrawableParams & 8) != 0;
                if (z7) {
                    int i10 = this.animateFromStatusDrawableParams;
                    boolean z10 = (i10 & 4) != 0;
                    boolean z11 = (i10 & 8) != 0;
                    float var_ = i6;
                    float var_ = f9;
                    f8 = var_;
                    c2 = 4;
                    i5 = i6;
                    c = 7;
                    drawClockOrErrorLayout(canvas, z10, z11, var_, var_, var_, var_, 1.0f - this.statusDrawableProgress, z2);
                    drawClockOrErrorLayout(canvas, z8, z9, var_, var_, var_, var_, this.statusDrawableProgress, z2);
                    if (!this.currentMessageObject.isOutOwner()) {
                        if (!z10 && !z11) {
                            drawViewsAndRepliesLayout(canvas, var_, f9, var_, var_, 1.0f - this.statusDrawableProgress, z2);
                        }
                        if (!z8 && !z9) {
                            drawViewsAndRepliesLayout(canvas, var_, f9, var_, var_, this.statusDrawableProgress, z2);
                        }
                    }
                } else {
                    f8 = var_;
                    c = 7;
                    c2 = 4;
                    i5 = i6;
                    if (!this.currentMessageObject.isOutOwner() && !z8 && !z9) {
                        drawViewsAndRepliesLayout(canvas, var_, f9, i5, var_, 1.0f, z2);
                    }
                    drawClockOrErrorLayout(canvas, z8, z9, var_, f9, i5, var_, 1.0f, z2);
                }
                if (this.currentMessageObject.isOutOwner()) {
                    drawViewsAndRepliesLayout(canvas, var_, f9, i5, var_, 1.0f, z2);
                }
                TransitionParams transitionParams3 = this.transitionParams;
                transitionParams3.lastStatusDrawableParams = transitionParams3.createStatusDrawableParams();
                if (z8 && z && getParent() != null) {
                    ((View) getParent()).invalidate();
                }
                var_ = f8;
            }
            canvas.save();
            float var_ = var_ + var_;
            this.drawTimeX = var_;
            float dp5 = (var_ - AndroidUtilities.dp(7.3f)) - staticLayout.getHeight();
            this.drawTimeY = dp5;
            canvas.translate(var_, dp5);
            staticLayout.draw(canvas);
            canvas.restore();
            Theme.chat_timePaint.setAlpha(255);
            i3 = i5;
        } else {
            c = 7;
            c2 = 4;
            if (this.currentMessageObject.isSponsored()) {
                i2 = -AndroidUtilities.dp(48.0f);
                if (this.hasNewLineForTime) {
                    i2 -= AndroidUtilities.dp(16.0f);
                }
            } else {
                i2 = -(this.drawCommentButton ? AndroidUtilities.dp(43.0f) : 0);
            }
            int i11 = i2;
            float var_ = -staticLayout.getLineLeft(0);
            if (this.reactionsLayoutInBubble.isSmall) {
                updateReactionLayoutPosition();
                this.reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, null);
            }
            if ((ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) || (this.currentMessageObject.messageOwner.flags & 1024) != 0 || this.repliesLayout != null || this.transitionParams.animateReplies || this.isPinned || this.transitionParams.animatePinned) {
                float lineWidth2 = var_ + (this.timeWidth - staticLayout.getLineWidth(0));
                ReactionsLayoutInBubble reactionsLayoutInBubble3 = this.reactionsLayoutInBubble;
                if (reactionsLayoutInBubble3.isSmall && !reactionsLayoutInBubble3.isEmpty) {
                    lineWidth2 -= reactionsLayoutInBubble3.width;
                }
                float var_ = lineWidth2;
                int createStatusDrawableParams2 = this.transitionParams.createStatusDrawableParams();
                int i12 = this.transitionParams.lastStatusDrawableParams;
                if (i12 >= 0 && i12 != createStatusDrawableParams2 && !this.statusDrawableAnimationInProgress) {
                    createStatusDrawableAnimator(i12, createStatusDrawableParams2, z);
                }
                boolean z12 = this.statusDrawableAnimationInProgress;
                if (z12) {
                    createStatusDrawableParams2 = this.animateToStatusDrawableParams;
                }
                boolean z13 = (createStatusDrawableParams2 & 4) != 0;
                boolean z14 = (createStatusDrawableParams2 & 8) != 0;
                if (z12) {
                    int i13 = this.animateFromStatusDrawableParams;
                    boolean z15 = (i13 & 4) != 0;
                    boolean z16 = (i13 & 8) != 0;
                    float var_ = i11;
                    float var_ = f9;
                    drawClockOrErrorLayout(canvas, z15, z16, var_, var_, var_, var_, 1.0f - this.statusDrawableProgress, z2);
                    drawClockOrErrorLayout(canvas, z13, z14, var_, var_, var_, var_, this.statusDrawableProgress, z2);
                    if (!this.currentMessageObject.isOutOwner()) {
                        if (!z15 && !z16) {
                            drawViewsAndRepliesLayout(canvas, var_, f9, var_, var_, 1.0f - this.statusDrawableProgress, z2);
                        }
                        if (!z13 && !z14) {
                            drawViewsAndRepliesLayout(canvas, var_, f9, var_, var_, this.statusDrawableProgress, z2);
                        }
                    }
                } else {
                    if (!this.currentMessageObject.isOutOwner() && !z13 && !z14) {
                        drawViewsAndRepliesLayout(canvas, var_, f9, i11, var_, 1.0f, z2);
                    }
                    drawClockOrErrorLayout(canvas, z13, z14, var_, f9, i11, var_, 1.0f, z2);
                }
                if (this.currentMessageObject.isOutOwner()) {
                    drawViewsAndRepliesLayout(canvas, var_, f9, i11, var_, 1.0f, z2);
                }
                TransitionParams transitionParams4 = this.transitionParams;
                transitionParams4.lastStatusDrawableParams = transitionParams4.createStatusDrawableParams();
                if (z13 && z && getParent() != null) {
                    ((View) getParent()).invalidate();
                }
                var_ = var_;
            }
            canvas.save();
            float var_ = 6.5f;
            if (this.transitionParams.animateEditedEnter) {
                TransitionParams transitionParams5 = this.transitionParams;
                if (transitionParams5.animateChangeProgress != 1.0f) {
                    if (transitionParams5.animateEditedLayout != null) {
                        float var_ = var_ + var_;
                        if (this.pinnedBottom || this.pinnedTop) {
                            var_ = 7.5f;
                        }
                        canvas.translate(var_, ((var_ - AndroidUtilities.dp(var_)) - staticLayout.getHeight()) + i11);
                        int alpha3 = Theme.chat_timePaint.getAlpha();
                        Theme.chat_timePaint.setAlpha((int) (alpha3 * this.transitionParams.animateChangeProgress));
                        this.transitionParams.animateEditedLayout.draw(canvas);
                        Theme.chat_timePaint.setAlpha(alpha3);
                        this.transitionParams.animateTimeLayout.draw(canvas);
                    } else {
                        int alpha4 = Theme.chat_timePaint.getAlpha();
                        canvas.save();
                        float var_ = i11;
                        canvas.translate(this.transitionParams.animateFromTimeX + var_, ((var_ - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 7.5f : 6.5f)) - staticLayout.getHeight()) + var_);
                        float var_ = alpha4;
                        Theme.chat_timePaint.setAlpha((int) ((1.0f - this.transitionParams.animateChangeProgress) * var_));
                        this.transitionParams.animateTimeLayout.draw(canvas);
                        canvas.restore();
                        float var_ = var_ + var_;
                        if (this.pinnedBottom || this.pinnedTop) {
                            var_ = 7.5f;
                        }
                        canvas.translate(var_, ((var_ - AndroidUtilities.dp(var_)) - staticLayout.getHeight()) + var_);
                        Theme.chat_timePaint.setAlpha((int) (var_ * this.transitionParams.animateChangeProgress));
                        staticLayout.draw(canvas);
                        Theme.chat_timePaint.setAlpha(alpha4);
                    }
                    canvas.restore();
                    i3 = i11;
                    z3 = false;
                }
            }
            float var_ = var_ + var_;
            this.drawTimeX = var_;
            if (this.pinnedBottom || this.pinnedTop) {
                var_ = 7.5f;
            }
            float dp6 = ((var_ - AndroidUtilities.dp(var_)) - staticLayout.getHeight()) + i11;
            this.drawTimeY = dp6;
            canvas.translate(var_, dp6);
            staticLayout.draw(canvas);
            canvas.restore();
            i3 = i11;
            z3 = false;
        }
        if (this.currentMessageObject.isOutOwner()) {
            int createStatusDrawableParams3 = this.transitionParams.createStatusDrawableParams();
            int i14 = this.transitionParams.lastStatusDrawableParams;
            if (i14 >= 0 && i14 != createStatusDrawableParams3 && !this.statusDrawableAnimationInProgress) {
                createStatusDrawableAnimator(i14, createStatusDrawableParams3, z);
            }
            if (this.statusDrawableAnimationInProgress) {
                createStatusDrawableParams3 = this.animateToStatusDrawableParams;
            }
            boolean z17 = (createStatusDrawableParams3 & 1) != 0;
            boolean z18 = (createStatusDrawableParams3 & 2) != 0;
            boolean z19 = (createStatusDrawableParams3 & 4) != 0;
            boolean z20 = (createStatusDrawableParams3 & 8) != 0;
            if (this.transitionYOffsetForDrawables != 0.0f) {
                canvas.save();
                canvas.translate(0.0f, this.transitionYOffsetForDrawables);
                z4 = true;
            } else {
                z4 = false;
            }
            if (this.statusDrawableAnimationInProgress) {
                int i15 = this.animateFromStatusDrawableParams;
                boolean z21 = (i15 & 1) != 0;
                boolean z22 = (i15 & 2) != 0;
                boolean z23 = (i15 & 4) != 0;
                boolean z24 = (i15 & 8) != 0;
                if (!z23 && z22 && z18 && !z21 && z17) {
                    f6 = 0.0f;
                    c3 = 5;
                    drawStatusDrawable(canvas, z17, z18, z19, z20, f9, z3, i3, var_, this.statusDrawableProgress, true, z2);
                } else {
                    f6 = 0.0f;
                    c3 = 5;
                    float var_ = i3;
                    boolean z25 = z23;
                    boolean z26 = z24;
                    float var_ = f9;
                    boolean z27 = z3;
                    drawStatusDrawable(canvas, z21, z22, z25, z26, var_, z27, var_, var_, 1.0f - this.statusDrawableProgress, false, z2);
                    drawStatusDrawable(canvas, z17, z18, z19, z20, var_, z27, var_, var_, this.statusDrawableProgress, false, z2);
                }
            } else {
                f6 = 0.0f;
                c3 = 5;
                drawStatusDrawable(canvas, z17, z18, z19, z20, f9, z3, i3, var_, 1.0f, false, z2);
            }
            if (z4) {
                canvas.restore();
            }
            TransitionParams transitionParams6 = this.transitionParams;
            transitionParams6.lastStatusDrawableParams = transitionParams6.createStatusDrawableParams();
            if (z && z19 && getParent() != null) {
                ((View) getParent()).invalidate();
            }
        } else {
            f6 = 0.0f;
            c3 = 5;
        }
        canvas.restore();
        if (this.unlockLayout == null) {
            return;
        }
        if (this.unlockX == f6 || this.unlockY == f6) {
            calculateUnlockXY();
        }
        this.unlockSpoilerPath.rewind();
        RectF rectF2 = AndroidUtilities.rectTmp;
        rectF2.set(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX2(), this.photoImage.getImageY2());
        int[] roundRadius2 = this.photoImage.getRoundRadius();
        float[] fArr = this.unlockSpoilerRadii;
        float var_ = roundRadius2[0];
        fArr[1] = var_;
        fArr[0] = var_;
        float var_ = roundRadius2[1];
        fArr[3] = var_;
        fArr[2] = var_;
        float var_ = roundRadius2[2];
        fArr[c3] = var_;
        fArr[c2] = var_;
        float var_ = roundRadius2[3];
        fArr[c] = var_;
        fArr[6] = var_;
        this.unlockSpoilerPath.addRoundRect(rectF2, fArr, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(this.unlockSpoilerPath);
        this.unlockSpoilerPath.rewind();
        rectF2.set(this.unlockX - AndroidUtilities.dp(12.0f), this.unlockY - AndroidUtilities.dp(8.0f), this.unlockX + Theme.chat_msgUnlockDrawable.getIntrinsicWidth() + AndroidUtilities.dp(14.0f) + this.unlockLayout.getWidth() + AndroidUtilities.dp(12.0f), this.unlockY + this.unlockLayout.getHeight() + AndroidUtilities.dp(8.0f));
        this.unlockSpoilerPath.addRoundRect(rectF2, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f), Path.Direction.CW);
        canvas.clipPath(this.unlockSpoilerPath, Region.Op.DIFFERENCE);
        this.unlockSpoilerEffect.setColor(ColorUtils.setAlphaComponent(-1, (int) (Color.alpha(-1) * 0.325f)));
        this.unlockSpoilerEffect.setBounds((int) this.photoImage.getImageX(), (int) this.photoImage.getImageY(), (int) this.photoImage.getImageX2(), (int) this.photoImage.getImageY2());
        this.unlockSpoilerEffect.draw(canvas);
        invalidate();
        canvas.restore();
        canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (this.unlockAlpha * 255.0f), 31);
        int alpha5 = Theme.chat_timeBackgroundPaint.getAlpha();
        Theme.chat_timeBackgroundPaint.setAlpha((int) (alpha5 * 0.7f));
        canvas.drawRoundRect(rectF2, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f), Theme.chat_timeBackgroundPaint);
        Theme.chat_timeBackgroundPaint.setAlpha(alpha5);
        canvas.translate(this.unlockX + AndroidUtilities.dp(4.0f), this.unlockY);
        Drawable drawable = Theme.chat_msgUnlockDrawable;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), Theme.chat_msgUnlockDrawable.getIntrinsicHeight());
        Theme.chat_msgUnlockDrawable.draw(canvas);
        canvas.translate(AndroidUtilities.dp(6.0f) + Theme.chat_msgUnlockDrawable.getIntrinsicWidth(), f6);
        this.unlockLayout.draw(canvas);
        canvas.restore();
        if (this.videoInfoLayout == null || !this.photoImage.getVisible() || this.imageBackgroundSideColor != 0) {
            return;
        }
        int i16 = SharedConfig.bubbleRadius;
        if (i16 > 2) {
            dp = AndroidUtilities.dp(i16 - 2);
            if (SharedConfig.bubbleRadius < 10) {
                z6 = false;
            }
            z3 = z6;
        } else {
            dp = AndroidUtilities.dp(i16);
        }
        int imageX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(9.0f));
        int imageY = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(6.0f));
        RectF rectF3 = this.rect;
        float dp7 = imageX - AndroidUtilities.dp(4.0f);
        float dp8 = imageY - AndroidUtilities.dp(1.5f);
        int dp9 = this.durationWidth + imageX + AndroidUtilities.dp(4.0f);
        if (!z3) {
            var_ = 0.0f;
        }
        rectF3.set(dp7, dp8, dp9 + AndroidUtilities.dp(var_), this.videoInfoLayout.getHeight() + imageY + AndroidUtilities.dp(1.5f));
        canvas.drawRoundRect(this.rect, dp, dp, getThemedPaint("paintChatTimeBackground"));
        canvas.save();
        canvas.translate(imageX + (z3 ? 2 : 0), imageY);
        this.videoInfoLayout.draw(canvas);
        canvas.restore();
    }

    public void createStatusDrawableAnimator(int i, int i2, final boolean z) {
        boolean z2 = false;
        boolean z3 = (i2 & 1) != 0;
        boolean z4 = (i2 & 2) != 0;
        boolean z5 = (i & 1) != 0;
        boolean z6 = (i & 2) != 0;
        if (!((i & 4) != 0) && z6 && z4 && !z5 && z3) {
            z2 = true;
        }
        if (!this.transitionParams.messageEntering || z2) {
            this.statusDrawableProgress = 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.statusDrawableAnimator = ofFloat;
            if (z2) {
                ofFloat.setDuration(220L);
            } else {
                ofFloat.setDuration(150L);
            }
            this.statusDrawableAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animateFromStatusDrawableParams = i;
            this.animateToStatusDrawableParams = i2;
            this.statusDrawableAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatMessageCell.this.lambda$createStatusDrawableAnimator$8(z, valueAnimator);
                }
            });
            this.statusDrawableAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.ChatMessageCell.7
                {
                    ChatMessageCell.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    int createStatusDrawableParams = ChatMessageCell.this.transitionParams.createStatusDrawableParams();
                    if (ChatMessageCell.this.animateToStatusDrawableParams == createStatusDrawableParams) {
                        ChatMessageCell.this.statusDrawableAnimationInProgress = false;
                        ChatMessageCell.this.transitionParams.lastStatusDrawableParams = ChatMessageCell.this.animateToStatusDrawableParams;
                        return;
                    }
                    ChatMessageCell chatMessageCell = ChatMessageCell.this;
                    chatMessageCell.createStatusDrawableAnimator(chatMessageCell.animateToStatusDrawableParams, createStatusDrawableParams, z);
                }
            });
            this.statusDrawableAnimationInProgress = true;
            this.statusDrawableAnimator.start();
        }
    }

    public /* synthetic */ void lambda$createStatusDrawableAnimator$8(boolean z, ValueAnimator valueAnimator) {
        this.statusDrawableProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (!z || getParent() == null) {
            return;
        }
        ((View) getParent()).invalidate();
    }

    /* JADX WARN: Removed duplicated region for block: B:143:0x015c  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x01a8  */
    /* JADX WARN: Removed duplicated region for block: B:153:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawClockOrErrorLayout(android.graphics.Canvas r16, boolean r17, boolean r18, float r19, float r20, float r21, float r22, float r23, boolean r24) {
        /*
            Method dump skipped, instructions count: 428
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawClockOrErrorLayout(android.graphics.Canvas, boolean, boolean, float, float, float, float, float, boolean):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:283:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:284:0x00b7  */
    /* JADX WARN: Removed duplicated region for block: B:322:0x012d  */
    /* JADX WARN: Removed duplicated region for block: B:327:0x013c  */
    /* JADX WARN: Removed duplicated region for block: B:330:0x0145  */
    /* JADX WARN: Removed duplicated region for block: B:334:0x0157  */
    /* JADX WARN: Removed duplicated region for block: B:342:0x0179  */
    /* JADX WARN: Removed duplicated region for block: B:344:0x0180  */
    /* JADX WARN: Removed duplicated region for block: B:349:0x0192  */
    /* JADX WARN: Removed duplicated region for block: B:352:0x01bc  */
    /* JADX WARN: Removed duplicated region for block: B:356:0x0203  */
    /* JADX WARN: Removed duplicated region for block: B:359:0x0219  */
    /* JADX WARN: Removed duplicated region for block: B:360:0x021d  */
    /* JADX WARN: Removed duplicated region for block: B:365:0x0235  */
    /* JADX WARN: Removed duplicated region for block: B:367:0x0244  */
    /* JADX WARN: Removed duplicated region for block: B:370:0x024f  */
    /* JADX WARN: Removed duplicated region for block: B:374:0x025d  */
    /* JADX WARN: Removed duplicated region for block: B:422:0x039e  */
    /* JADX WARN: Removed duplicated region for block: B:429:0x03b2  */
    /* JADX WARN: Removed duplicated region for block: B:430:0x03b6  */
    /* JADX WARN: Removed duplicated region for block: B:445:0x03db  */
    /* JADX WARN: Removed duplicated region for block: B:454:0x03fd  */
    /* JADX WARN: Removed duplicated region for block: B:459:0x040c  */
    /* JADX WARN: Removed duplicated region for block: B:462:0x0415  */
    /* JADX WARN: Removed duplicated region for block: B:466:0x0427  */
    /* JADX WARN: Removed duplicated region for block: B:477:0x044a  */
    /* JADX WARN: Removed duplicated region for block: B:481:0x048e  */
    /* JADX WARN: Removed duplicated region for block: B:483:0x04a8  */
    /* JADX WARN: Removed duplicated region for block: B:486:0x04c2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawViewsAndRepliesLayout(android.graphics.Canvas r25, float r26, float r27, float r28, float r29, float r30, boolean r31) {
        /*
            Method dump skipped, instructions count: 1226
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawViewsAndRepliesLayout(android.graphics.Canvas, float, float, float, float, float, boolean):void");
    }

    private void drawStatusDrawable(Canvas canvas, boolean z, boolean z2, boolean z3, boolean z4, float f, boolean z5, float f2, float f3, float f4, boolean z6, boolean z7) {
        float imageY2;
        int dp;
        int dp2;
        Drawable themedDrawable;
        Drawable drawable;
        int i;
        boolean z8 = f4 != 1.0f && !z6;
        float f5 = (f4 * 0.5f) + 0.5f;
        float f6 = z8 ? f * f4 : f;
        if (this.documentAttachType == 7) {
            imageY2 = f3 - ((AndroidUtilities.dp(this.drawPinnedBottom ? 4.0f : 5.0f) + this.reactionsLayoutInBubble.getCurrentTotalHeight(this.transitionParams.animateChangeProgress)) * (1.0f - getVideoTranscriptionProgress()));
        } else {
            imageY2 = this.photoImage.getImageY2() + this.additionalTimeOffsetY;
        }
        float dp3 = imageY2 - AndroidUtilities.dp(8.5f);
        if (z3) {
            MsgClockDrawable msgClockDrawable = Theme.chat_msgClockDrawable;
            if (shouldDrawTimeOnMedia()) {
                float f7 = 24.0f;
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    i = getThemedColor("chat_serviceText");
                    int i2 = this.layoutWidth;
                    if (!z5) {
                        f7 = 22.0f;
                    }
                    BaseCell.setDrawableBounds(msgClockDrawable, (i2 - AndroidUtilities.dp(f7)) - msgClockDrawable.getIntrinsicWidth(), (dp3 - msgClockDrawable.getIntrinsicHeight()) + f2);
                    msgClockDrawable.setAlpha((int) (this.timeAlpha * 255.0f * f6));
                } else {
                    i = getThemedColor("chat_mediaSentClock");
                    int i3 = this.layoutWidth;
                    if (!z5) {
                        f7 = 22.0f;
                    }
                    BaseCell.setDrawableBounds(msgClockDrawable, (i3 - AndroidUtilities.dp(f7)) - msgClockDrawable.getIntrinsicWidth(), (dp3 - msgClockDrawable.getIntrinsicHeight()) + f2);
                    msgClockDrawable.setAlpha((int) (f6 * 255.0f));
                }
            } else {
                int themedColor = getThemedColor("chat_outSentClock");
                BaseCell.setDrawableBounds(msgClockDrawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - msgClockDrawable.getIntrinsicWidth(), ((f3 - AndroidUtilities.dp(8.5f)) - msgClockDrawable.getIntrinsicHeight()) + f2);
                msgClockDrawable.setAlpha((int) (f6 * 255.0f));
                i = themedColor;
            }
            msgClockDrawable.setColor(i);
            if (z8) {
                canvas.save();
                canvas.scale(f5, f5, msgClockDrawable.getBounds().centerX(), msgClockDrawable.getBounds().centerY());
            }
            msgClockDrawable.draw(canvas);
            msgClockDrawable.setAlpha(255);
            if (z8) {
                canvas.restore();
            }
            invalidate();
        }
        float f8 = 23.5f;
        float f9 = 9.0f;
        if (z2) {
            if (shouldDrawTimeOnMedia()) {
                if (z6) {
                    canvas.save();
                }
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    drawable = getThemedDrawable("drawableMsgStickerCheck");
                    if (z) {
                        if (z6) {
                            canvas.translate(AndroidUtilities.dp(4.8f) * (1.0f - f4), 0.0f);
                        }
                        BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(z5 ? 28.3f : 26.3f)) - drawable.getIntrinsicWidth(), (dp3 - drawable.getIntrinsicHeight()) + f2);
                    } else {
                        BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(z5 ? 23.5f : 21.5f)) - drawable.getIntrinsicWidth(), (dp3 - drawable.getIntrinsicHeight()) + f2);
                    }
                    drawable.setAlpha((int) (this.timeAlpha * 255.0f * f6));
                } else {
                    if (z) {
                        if (z6) {
                            canvas.translate(AndroidUtilities.dp(4.8f) * (1.0f - f4), 0.0f);
                        }
                        BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(z5 ? 28.3f : 26.3f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (dp3 - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight()) + f2);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(z5 ? 23.5f : 21.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (dp3 - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight()) + f2);
                    }
                    Theme.chat_msgMediaCheckDrawable.setAlpha((int) (this.timeAlpha * 255.0f * f6));
                    drawable = Theme.chat_msgMediaCheckDrawable;
                }
                if (z8) {
                    canvas.save();
                    canvas.scale(f5, f5, drawable.getBounds().centerX(), drawable.getBounds().centerY());
                }
                drawable.draw(canvas);
                if (z8) {
                    canvas.restore();
                }
                if (z6) {
                    canvas.restore();
                }
                drawable.setAlpha(255);
            } else {
                if (z6) {
                    canvas.save();
                }
                if (z) {
                    if (z6) {
                        canvas.translate(AndroidUtilities.dp(4.0f) * (1.0f - f4), 0.0f);
                    }
                    themedDrawable = getThemedDrawable(z7 ? "drawableMsgOutCheckReadSelected" : "drawableMsgOutCheckRead");
                    BaseCell.setDrawableBounds(themedDrawable, (this.layoutWidth - AndroidUtilities.dp(22.5f)) - themedDrawable.getIntrinsicWidth(), ((f3 - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 9.0f : 8.0f)) - themedDrawable.getIntrinsicHeight()) + f2);
                } else {
                    themedDrawable = getThemedDrawable(z7 ? "drawableMsgOutCheckSelected" : "drawableMsgOutCheck");
                    BaseCell.setDrawableBounds(themedDrawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - themedDrawable.getIntrinsicWidth(), ((f3 - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 9.0f : 8.0f)) - themedDrawable.getIntrinsicHeight()) + f2);
                }
                themedDrawable.setAlpha((int) (f6 * 255.0f));
                if (z8) {
                    canvas.save();
                    canvas.scale(f5, f5, themedDrawable.getBounds().centerX(), themedDrawable.getBounds().centerY());
                }
                themedDrawable.draw(canvas);
                if (z8) {
                    canvas.restore();
                }
                if (z6) {
                    canvas.restore();
                }
                themedDrawable.setAlpha(255);
            }
        }
        if (z) {
            if (shouldDrawTimeOnMedia()) {
                Drawable themedDrawable2 = this.currentMessageObject.shouldDrawWithoutBackground() ? getThemedDrawable("drawableMsgStickerHalfCheck") : Theme.chat_msgMediaHalfCheckDrawable;
                int i4 = this.layoutWidth;
                if (!z5) {
                    f8 = 21.5f;
                }
                BaseCell.setDrawableBounds(themedDrawable2, (i4 - AndroidUtilities.dp(f8)) - themedDrawable2.getIntrinsicWidth(), (dp3 - themedDrawable2.getIntrinsicHeight()) + f2);
                themedDrawable2.setAlpha((int) (this.timeAlpha * 255.0f * f6));
                if (z8 || z6) {
                    canvas.save();
                    canvas.scale(f5, f5, themedDrawable2.getBounds().centerX(), themedDrawable2.getBounds().centerY());
                }
                themedDrawable2.draw(canvas);
                if (z8 || z6) {
                    canvas.restore();
                }
                themedDrawable2.setAlpha(255);
            } else {
                Drawable themedDrawable3 = getThemedDrawable(z7 ? "drawableMsgOutHalfCheckSelected" : "drawableMsgOutHalfCheck");
                float dp4 = (this.layoutWidth - AndroidUtilities.dp(18.0f)) - themedDrawable3.getIntrinsicWidth();
                if (!this.pinnedBottom && !this.pinnedTop) {
                    f9 = 8.0f;
                }
                BaseCell.setDrawableBounds(themedDrawable3, dp4, ((f3 - AndroidUtilities.dp(f9)) - themedDrawable3.getIntrinsicHeight()) + f2);
                themedDrawable3.setAlpha((int) (f6 * 255.0f));
                if (z8 || z6) {
                    canvas.save();
                    canvas.scale(f5, f5, themedDrawable3.getBounds().centerX(), themedDrawable3.getBounds().centerY());
                }
                themedDrawable3.draw(canvas);
                if (z8 || z6) {
                    canvas.restore();
                }
                themedDrawable3.setAlpha(255);
            }
        }
        if (z4) {
            if (shouldDrawTimeOnMedia()) {
                dp = this.layoutWidth - AndroidUtilities.dp(34.5f);
                dp2 = AndroidUtilities.dp(26.5f);
            } else {
                dp = this.layoutWidth - AndroidUtilities.dp(32.0f);
                dp2 = AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 22.0f : 21.0f);
            }
            float var_ = (f3 - dp2) + f2;
            this.rect.set(dp, var_, AndroidUtilities.dp(14.0f) + dp, AndroidUtilities.dp(14.0f) + var_);
            int alpha = Theme.chat_msgErrorPaint.getAlpha();
            Theme.chat_msgErrorPaint.setAlpha((int) (alpha * f6));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
            Theme.chat_msgErrorPaint.setAlpha(alpha);
            BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, dp + AndroidUtilities.dp(6.0f), var_ + AndroidUtilities.dp(2.0f));
            Theme.chat_msgErrorDrawable.setAlpha((int) (f6 * 255.0f));
            if (z8) {
                canvas.save();
                canvas.scale(f5, f5, Theme.chat_msgErrorDrawable.getBounds().centerX(), Theme.chat_msgErrorDrawable.getBounds().centerY());
            }
            Theme.chat_msgErrorDrawable.draw(canvas);
            Theme.chat_msgErrorDrawable.setAlpha(255);
            if (!z8) {
                return;
            }
            canvas.restore();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:1409:0x0a31, code lost:
        if (r1[0] == 3) goto L333;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1675:0x118a, code lost:
        if (r3 == 2) goto L664;
     */
    /* JADX WARN: Removed duplicated region for block: B:1143:0x02e8  */
    /* JADX WARN: Removed duplicated region for block: B:1144:0x02eb  */
    /* JADX WARN: Removed duplicated region for block: B:1554:0x0e2e  */
    /* JADX WARN: Removed duplicated region for block: B:1838:0x147a  */
    /* JADX WARN: Removed duplicated region for block: B:1842:0x1493  */
    /* JADX WARN: Removed duplicated region for block: B:1850:0x14b4  */
    /* JADX WARN: Removed duplicated region for block: B:1854:0x14cb  */
    /* JADX WARN: Removed duplicated region for block: B:1876:0x1509  */
    /* JADX WARN: Removed duplicated region for block: B:1880:0x151a  */
    /* JADX WARN: Removed duplicated region for block: B:1924:0x166a  */
    /* JADX WARN: Removed duplicated region for block: B:1927:0x1686  */
    /* JADX WARN: Removed duplicated region for block: B:1929:0x168e  */
    /* JADX WARN: Removed duplicated region for block: B:1950:0x173a  */
    /* JADX WARN: Removed duplicated region for block: B:1956:0x174e  */
    /* JADX WARN: Removed duplicated region for block: B:1960:0x175c  */
    /* JADX WARN: Removed duplicated region for block: B:1995:0x1822  */
    /* JADX WARN: Removed duplicated region for block: B:1998:0x1829  */
    /* JADX WARN: Removed duplicated region for block: B:2073:0x1a07  */
    /* JADX WARN: Removed duplicated region for block: B:2080:0x1a26  */
    /* JADX WARN: Removed duplicated region for block: B:2083:0x1a7a  */
    /* JADX WARN: Removed duplicated region for block: B:2111:0x0e37 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:2112:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r2v480, types: [boolean] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawOverlays(android.graphics.Canvas r28) {
        /*
            Method dump skipped, instructions count: 6991
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawOverlays(android.graphics.Canvas):void");
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public int getObserverTag() {
        return this.TAG;
    }

    public MessageObject getMessageObject() {
        MessageObject messageObject = this.messageObjectToSet;
        return messageObject != null ? messageObject : this.currentMessageObject;
    }

    public TLRPC$Document getStreamingMedia() {
        int i = this.documentAttachType;
        if (i == 4 || i == 7 || i == 2) {
            return this.documentAttach;
        }
        return null;
    }

    public boolean drawPinnedBottom() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && groupedMessages.isDocuments) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition != null && (groupedMessagePosition.flags & 8) != 0) {
                return this.pinnedBottom;
            }
            return true;
        }
        return this.pinnedBottom;
    }

    public float getVideoTranscriptionProgress() {
        MessageObject messageObject;
        if (this.transitionParams == null || (messageObject = this.currentMessageObject) == null || !messageObject.isRoundVideo()) {
            return 1.0f;
        }
        TransitionParams transitionParams = this.transitionParams;
        if (!transitionParams.animateDrawBackground) {
            return this.drawBackground ? 1.0f : 0.0f;
        } else if (this.drawBackground) {
            return transitionParams.animateChangeProgress;
        } else {
            return 1.0f - transitionParams.animateChangeProgress;
        }
    }

    public boolean drawPinnedTop() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && groupedMessages.isDocuments) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition != null && (groupedMessagePosition.flags & 4) != 0) {
                return this.pinnedTop;
            }
            return true;
        }
        return this.pinnedTop;
    }

    public boolean isPinnedBottom() {
        return this.pinnedBottom;
    }

    public boolean isPinnedTop() {
        return this.pinnedTop;
    }

    public MessageObject.GroupedMessages getCurrentMessagesGroup() {
        return this.currentMessagesGroup;
    }

    public MessageObject.GroupedMessagePosition getCurrentPosition() {
        return this.currentPosition;
    }

    public int getLayoutHeight() {
        return this.layoutHeight;
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        ChatMessageCellDelegate chatMessageCellDelegate;
        ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
        if (chatMessageCellDelegate2 == null || !chatMessageCellDelegate2.onAccessibilityAction(i, bundle)) {
            if (i == 16) {
                int iconForCurrentState = getIconForCurrentState();
                if (iconForCurrentState != 4 && iconForCurrentState != 5) {
                    didPressButton(true, false);
                } else if (this.currentMessageObject.type == 16) {
                    this.delegate.didPressOther(this, this.otherX, this.otherY);
                } else {
                    didClickedImage();
                }
                return true;
            }
            if (i == R.id.acc_action_small_button) {
                didPressMiniButton(true);
            } else if (i == R.id.acc_action_msg_options) {
                ChatMessageCellDelegate chatMessageCellDelegate3 = this.delegate;
                if (chatMessageCellDelegate3 != null) {
                    if (this.currentMessageObject.type == 16) {
                        chatMessageCellDelegate3.didLongPress(this, 0.0f, 0.0f);
                    } else {
                        chatMessageCellDelegate3.didPressOther(this, this.otherX, this.otherY);
                    }
                }
            } else if (i == R.id.acc_action_open_forwarded_origin && (chatMessageCellDelegate = this.delegate) != null) {
                TLRPC$Chat tLRPC$Chat = this.currentForwardChannel;
                if (tLRPC$Chat != null) {
                    chatMessageCellDelegate.didPressChannelAvatar(this, tLRPC$Chat, this.currentMessageObject.messageOwner.fwd_from.channel_post, this.lastTouchX, this.lastTouchY);
                } else {
                    TLRPC$User tLRPC$User = this.currentForwardUser;
                    if (tLRPC$User != null) {
                        chatMessageCellDelegate.didPressUserAvatar(this, tLRPC$User, this.lastTouchX, this.lastTouchY);
                    } else if (this.currentForwardName != null) {
                        chatMessageCellDelegate.didPressHiddenForward(this);
                    }
                }
            }
            if ((!this.currentMessageObject.isVoice() && !this.currentMessageObject.isRoundVideo() && (!this.currentMessageObject.isMusic() || !MediaController.getInstance().isPlayingMessage(this.currentMessageObject))) || !this.seekBarAccessibilityDelegate.performAccessibilityActionInternal(i, bundle)) {
                return super.performAccessibilityAction(i, bundle);
            }
            return true;
        }
        return false;
    }

    public void setAnimationRunning(boolean z, boolean z2) {
        this.animationRunning = z;
        if (z) {
            this.willRemoved = z2;
        } else {
            this.willRemoved = false;
        }
        if (getParent() != null || !this.attachedToWindow) {
            return;
        }
        onDetachedFromWindow();
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 9 || motionEvent.getAction() == 7) {
            for (int i = 0; i < this.accessibilityVirtualViewBounds.size(); i++) {
                if (this.accessibilityVirtualViewBounds.valueAt(i).contains(x, y)) {
                    int keyAt = this.accessibilityVirtualViewBounds.keyAt(i);
                    if (keyAt == this.currentFocusedVirtualView) {
                        return true;
                    }
                    this.currentFocusedVirtualView = keyAt;
                    sendAccessibilityEventForVirtualView(keyAt, 32768);
                    return true;
                }
            }
        } else if (motionEvent.getAction() == 10) {
            this.currentFocusedVirtualView = 0;
        }
        return super.onHoverEvent(motionEvent);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        return new MessageAccessibilityNodeProvider();
    }

    public void sendAccessibilityEventForVirtualView(int i, int i2) {
        sendAccessibilityEventForVirtualView(i, i2, null);
    }

    private void sendAccessibilityEventForVirtualView(int i, int i2, String str) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(i2);
            obtain.setPackageName(getContext().getPackageName());
            obtain.setSource(this, i);
            if (str != null) {
                obtain.getText().add(str);
            }
            if (getParent() == null) {
                return;
            }
            getParent().requestSendAccessibilityEvent(this, obtain);
        }
    }

    public static Point getMessageSize(int i, int i2) {
        return getMessageSize(i, i2, 0, 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x0042  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x004c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static org.telegram.ui.Components.Point getMessageSize(int r3, int r4, int r5, int r6) {
        /*
            if (r6 == 0) goto L4
            if (r5 != 0) goto L50
        L4:
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            r6 = 1060320051(0x3var_, float:0.7)
            if (r5 == 0) goto L16
            int r5 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
        L11:
            float r5 = (float) r5
            float r5 = r5 * r6
            int r5 = (int) r5
            goto L35
        L16:
            if (r3 < r4) goto L2a
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r6 = r5.x
            int r5 = r5.y
            int r5 = java.lang.Math.min(r6, r5)
            r6 = 1115684864(0x42800000, float:64.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            goto L35
        L2a:
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r5.x
            int r5 = r5.y
            int r5 = java.lang.Math.min(r0, r5)
            goto L11
        L35:
            r6 = 1120403456(0x42CLASSNAME, float:100.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r5
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            if (r5 <= r0) goto L46
            int r5 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
        L46:
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            if (r6 <= r0) goto L50
            int r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
        L50:
            float r3 = (float) r3
            float r5 = (float) r5
            float r0 = r3 / r5
            float r1 = r3 / r0
            int r1 = (int) r1
            float r4 = (float) r4
            float r0 = r4 / r0
            int r0 = (int) r0
            r2 = 1125515264(0x43160000, float:150.0)
            if (r1 != 0) goto L63
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
        L63:
            if (r0 != 0) goto L69
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
        L69:
            if (r0 <= r6) goto L72
            float r3 = (float) r0
            float r4 = (float) r6
            float r3 = r3 / r4
            float r4 = (float) r1
            float r4 = r4 / r3
            int r1 = (int) r4
            goto L88
        L72:
            r6 = 1123024896(0x42var_, float:120.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            if (r0 >= r2) goto L87
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r0 = (float) r6
            float r4 = r4 / r0
            float r3 = r3 / r4
            int r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r4 >= 0) goto L88
            int r1 = (int) r3
            goto L88
        L87:
            r6 = r0
        L88:
            org.telegram.ui.Components.Point r3 = new org.telegram.ui.Components.Point
            float r4 = (float) r1
            float r5 = (float) r6
            r3.<init>(r4, r5)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getMessageSize(int, int, int, int):org.telegram.ui.Components.Point");
    }

    public StaticLayout getDescriptionlayout() {
        return this.descriptionLayout;
    }

    public void setSelectedBackgroundProgress(float f) {
        this.selectedBackgroundProgress = f;
        invalidate();
    }

    public int computeHeight(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages) {
        this.photoImage.setIgnoreImageSet(true);
        this.avatarImage.setIgnoreImageSet(true);
        this.replyImageReceiver.setIgnoreImageSet(true);
        this.locationImageReceiver.setIgnoreImageSet(true);
        if (groupedMessages != null && groupedMessages.messages.size() != 1) {
            int i = 0;
            for (int i2 = 0; i2 < groupedMessages.messages.size(); i2++) {
                MessageObject messageObject2 = groupedMessages.messages.get(i2);
                MessageObject.GroupedMessagePosition groupedMessagePosition = groupedMessages.positions.get(messageObject2);
                if (groupedMessagePosition != null && (groupedMessagePosition.flags & 1) != 0) {
                    setMessageContent(messageObject2, groupedMessages, false, false);
                    i += this.totalHeight + this.keyboardHeight;
                }
            }
            return i;
        }
        setMessageContent(messageObject, groupedMessages, false, false);
        this.photoImage.setIgnoreImageSet(false);
        this.avatarImage.setIgnoreImageSet(false);
        this.replyImageReceiver.setIgnoreImageSet(false);
        this.locationImageReceiver.setIgnoreImageSet(false);
        return this.totalHeight + this.keyboardHeight;
    }

    public void shakeView() {
        PropertyValuesHolder ofKeyframe = PropertyValuesHolder.ofKeyframe(View.ROTATION, Keyframe.ofFloat(0.0f, 0.0f), Keyframe.ofFloat(0.2f, 3.0f), Keyframe.ofFloat(0.4f, -3.0f), Keyframe.ofFloat(0.6f, 3.0f), Keyframe.ofFloat(0.8f, -3.0f), Keyframe.ofFloat(1.0f, 0.0f));
        Keyframe ofFloat = Keyframe.ofFloat(0.0f, 1.0f);
        Keyframe ofFloat2 = Keyframe.ofFloat(0.5f, 0.97f);
        Keyframe ofFloat3 = Keyframe.ofFloat(1.0f, 1.0f);
        PropertyValuesHolder ofKeyframe2 = PropertyValuesHolder.ofKeyframe(View.SCALE_X, ofFloat, ofFloat2, ofFloat3);
        PropertyValuesHolder ofKeyframe3 = PropertyValuesHolder.ofKeyframe(View.SCALE_Y, ofFloat, ofFloat2, ofFloat3);
        AnimatorSet animatorSet = new AnimatorSet();
        this.shakeAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofPropertyValuesHolder(this, ofKeyframe), ObjectAnimator.ofPropertyValuesHolder(this, ofKeyframe2), ObjectAnimator.ofPropertyValuesHolder(this, ofKeyframe3));
        this.shakeAnimation.setDuration(500L);
        this.shakeAnimation.start();
    }

    private void cancelShakeAnimation() {
        AnimatorSet animatorSet = this.shakeAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.shakeAnimation = null;
            setScaleX(1.0f);
            setScaleY(1.0f);
            setRotation(0.0f);
        }
    }

    public void setSlidingOffset(float f) {
        if (this.slidingOffsetX != f) {
            this.slidingOffsetX = f;
            updateTranslation();
        }
    }

    public void setAnimationOffsetX(float f) {
        if (this.animationOffsetX != f) {
            this.animationOffsetX = f;
            updateTranslation();
        }
    }

    private void updateTranslation() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        setTranslationX(this.slidingOffsetX + this.animationOffsetX + (!messageObject.isOutOwner() ? this.checkBoxTranslation : 0));
    }

    public float getNonAnimationTranslationX(boolean z) {
        boolean z2;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.isOutOwner()) {
            if (z && ((z2 = this.checkBoxVisible) || this.checkBoxAnimationInProgress)) {
                this.checkBoxTranslation = (int) Math.ceil((z2 ? CubicBezierInterpolator.EASE_OUT : CubicBezierInterpolator.EASE_IN).getInterpolation(this.checkBoxAnimationProgress) * AndroidUtilities.dp(35.0f));
            }
            return this.slidingOffsetX + this.checkBoxTranslation;
        }
        return this.slidingOffsetX;
    }

    public float getSlidingOffsetX() {
        return this.slidingOffsetX;
    }

    public boolean willRemovedAfterAnimation() {
        return this.willRemoved;
    }

    public float getAnimationOffsetX() {
        return this.animationOffsetX;
    }

    @Override // android.view.View
    public void setTranslationX(float f) {
        super.setTranslationX(f);
    }

    public SeekBar getSeekBar() {
        return this.seekBar;
    }

    public SeekBarWaveform getSeekBarWaveform() {
        return this.seekBarWaveform;
    }

    /* loaded from: classes3.dex */
    public class MessageAccessibilityNodeProvider extends AccessibilityNodeProvider {
        private Path linkPath;
        private Rect rect;
        private RectF rectF;

        private MessageAccessibilityNodeProvider() {
            ChatMessageCell.this = r1;
            this.linkPath = new Path();
            this.rectF = new RectF();
            this.rect = new Rect();
        }

        /* loaded from: classes3.dex */
        private class ProfileSpan extends ClickableSpan {
            private TLRPC$User user;

            public ProfileSpan(TLRPC$User tLRPC$User) {
                MessageAccessibilityNodeProvider.this = r1;
                this.user = tLRPC$User;
            }

            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                if (ChatMessageCell.this.delegate != null) {
                    ChatMessageCell.this.delegate.didPressUserAvatar(ChatMessageCell.this, this.user, 0.0f, 0.0f);
                }
            }
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
            int i2;
            String str;
            String formatShortNumber;
            int i3;
            String str2;
            CharacterStyle[] characterStyleArr;
            CharacterStyle[] characterStyleArr2;
            String string;
            AccessibilityNodeInfo.CollectionItemInfo collectionItemInfo;
            CharSequence charSequence;
            CharSequence charSequence2;
            CharacterStyle[] characterStyleArr3;
            TLRPC$ReactionCount tLRPC$ReactionCount;
            boolean z;
            TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction;
            int i4;
            String str3;
            String string2;
            int[] iArr = {0, 0};
            ChatMessageCell.this.getLocationOnScreen(iArr);
            int i5 = 10;
            if (i == -1) {
                AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain(ChatMessageCell.this);
                ChatMessageCell.this.onInitializeAccessibilityNodeInfo(obtain);
                if (ChatMessageCell.this.accessibilityText == null) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    ChatMessageCell chatMessageCell = ChatMessageCell.this;
                    if (chatMessageCell.isChat && chatMessageCell.currentUser != null && !ChatMessageCell.this.currentMessageObject.isOut()) {
                        spannableStringBuilder.append((CharSequence) UserObject.getUserName(ChatMessageCell.this.currentUser));
                        spannableStringBuilder.setSpan(new ProfileSpan(ChatMessageCell.this.currentUser), 0, spannableStringBuilder.length(), 33);
                        spannableStringBuilder.append('\n');
                    }
                    if (ChatMessageCell.this.drawForwardedName) {
                        int i6 = 0;
                        while (i6 < 2) {
                            if (ChatMessageCell.this.forwardedNameLayout[i6] != null) {
                                spannableStringBuilder.append(ChatMessageCell.this.forwardedNameLayout[i6].getText());
                                spannableStringBuilder.append(i6 == 0 ? " " : "\n");
                            }
                            i6++;
                        }
                    }
                    if (!TextUtils.isEmpty(ChatMessageCell.this.currentMessageObject.messageText)) {
                        spannableStringBuilder.append(ChatMessageCell.this.currentMessageObject.messageText);
                    }
                    if (ChatMessageCell.this.documentAttach == null || !((ChatMessageCell.this.documentAttachType == 1 || ChatMessageCell.this.documentAttachType == 2 || ChatMessageCell.this.documentAttachType == 4) && ChatMessageCell.this.buttonState == 1 && ChatMessageCell.this.loadingProgressLayout != null)) {
                        charSequence = "\n";
                    } else {
                        spannableStringBuilder.append((CharSequence) "\n");
                        boolean isSending = ChatMessageCell.this.currentMessageObject.isSending();
                        charSequence = "\n";
                        spannableStringBuilder.append((CharSequence) LocaleController.formatString(isSending ? "AccDescrUploadProgress" : "AccDescrDownloadProgress", isSending ? R.string.AccDescrUploadProgress : R.string.AccDescrDownloadProgress, AndroidUtilities.formatFileSize(ChatMessageCell.this.currentMessageObject.loadedFileSize), AndroidUtilities.formatFileSize(ChatMessageCell.this.lastLoadingSizeTotal)));
                    }
                    if (!ChatMessageCell.this.currentMessageObject.isMusic()) {
                        charSequence2 = charSequence;
                        if (ChatMessageCell.this.currentMessageObject.isVoice() || ChatMessageCell.this.isRoundVideo) {
                            spannableStringBuilder.append((CharSequence) ", ");
                            spannableStringBuilder.append((CharSequence) LocaleController.formatDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                            spannableStringBuilder.append((CharSequence) ", ");
                            if (ChatMessageCell.this.currentMessageObject.isContentUnread()) {
                                spannableStringBuilder.append((CharSequence) LocaleController.getString("AccDescrMsgNotPlayed", R.string.AccDescrMsgNotPlayed));
                            } else {
                                spannableStringBuilder.append((CharSequence) LocaleController.getString("AccDescrMsgPlayed", R.string.AccDescrMsgPlayed));
                            }
                        }
                    } else {
                        charSequence2 = charSequence;
                        spannableStringBuilder.append(charSequence2);
                        spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrMusicInfo", R.string.AccDescrMusicInfo, ChatMessageCell.this.currentMessageObject.getMusicAuthor(), ChatMessageCell.this.currentMessageObject.getMusicTitle()));
                        spannableStringBuilder.append((CharSequence) ", ");
                        spannableStringBuilder.append((CharSequence) LocaleController.formatDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                    }
                    if (ChatMessageCell.this.lastPoll != null) {
                        spannableStringBuilder.append((CharSequence) ", ");
                        spannableStringBuilder.append((CharSequence) ChatMessageCell.this.lastPoll.question);
                        spannableStringBuilder.append((CharSequence) ", ");
                        if (!ChatMessageCell.this.pollClosed) {
                            if (ChatMessageCell.this.lastPoll.quiz) {
                                if (ChatMessageCell.this.lastPoll.public_voters) {
                                    string2 = LocaleController.getString("QuizPoll", R.string.QuizPoll);
                                } else {
                                    string2 = LocaleController.getString("AnonymousQuizPoll", R.string.AnonymousQuizPoll);
                                }
                            } else if (ChatMessageCell.this.lastPoll.public_voters) {
                                string2 = LocaleController.getString("PublicPoll", R.string.PublicPoll);
                            } else {
                                string2 = LocaleController.getString("AnonymousPoll", R.string.AnonymousPoll);
                            }
                        } else {
                            string2 = LocaleController.getString("FinalResults", R.string.FinalResults);
                        }
                        spannableStringBuilder.append((CharSequence) string2);
                    }
                    if (!ChatMessageCell.this.currentMessageObject.isVoiceTranscriptionOpen()) {
                        if (MessageObject.getMedia(ChatMessageCell.this.currentMessageObject.messageOwner) != null && !TextUtils.isEmpty(ChatMessageCell.this.currentMessageObject.caption)) {
                            spannableStringBuilder.append(charSequence2);
                            spannableStringBuilder.append(ChatMessageCell.this.currentMessageObject.caption);
                        }
                    } else {
                        spannableStringBuilder.append(charSequence2);
                        spannableStringBuilder.append(ChatMessageCell.this.currentMessageObject.getVoiceTranscription());
                    }
                    if (ChatMessageCell.this.documentAttach != null) {
                        if (ChatMessageCell.this.documentAttachType == 4) {
                            spannableStringBuilder.append((CharSequence) ", ");
                            spannableStringBuilder.append((CharSequence) LocaleController.formatDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                        }
                        if (ChatMessageCell.this.buttonState == 0 || ChatMessageCell.this.documentAttachType == 1) {
                            spannableStringBuilder.append((CharSequence) ", ");
                            spannableStringBuilder.append((CharSequence) AndroidUtilities.formatFileSize(ChatMessageCell.this.documentAttach.size));
                        }
                    }
                    if (ChatMessageCell.this.currentMessageObject.isOut()) {
                        if (!ChatMessageCell.this.currentMessageObject.isSent()) {
                            if (!ChatMessageCell.this.currentMessageObject.isSending()) {
                                if (ChatMessageCell.this.currentMessageObject.isSendError()) {
                                    spannableStringBuilder.append(charSequence2);
                                    spannableStringBuilder.append((CharSequence) LocaleController.getString("AccDescrMsgSendingError", R.string.AccDescrMsgSendingError));
                                }
                            } else {
                                spannableStringBuilder.append(charSequence2);
                                spannableStringBuilder.append((CharSequence) LocaleController.getString("AccDescrMsgSending", R.string.AccDescrMsgSending));
                                float progress = ChatMessageCell.this.radialProgress.getProgress();
                                if (progress > 0.0f) {
                                    spannableStringBuilder.append((CharSequence) ", ").append((CharSequence) Integer.toString(Math.round(progress * 100.0f))).append((CharSequence) "%");
                                }
                            }
                        } else {
                            spannableStringBuilder.append(charSequence2);
                            if (ChatMessageCell.this.currentMessageObject.scheduled) {
                                spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrScheduledDate", R.string.AccDescrScheduledDate, ChatMessageCell.this.currentTimeString));
                            } else {
                                spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrSentDate", R.string.AccDescrSentDate, LocaleController.getString("TodayAt", R.string.TodayAt) + " " + ChatMessageCell.this.currentTimeString));
                                spannableStringBuilder.append((CharSequence) ", ");
                                if (ChatMessageCell.this.currentMessageObject.isUnread()) {
                                    i4 = R.string.AccDescrMsgUnread;
                                    str3 = "AccDescrMsgUnread";
                                } else {
                                    i4 = R.string.AccDescrMsgRead;
                                    str3 = "AccDescrMsgRead";
                                }
                                spannableStringBuilder.append((CharSequence) LocaleController.getString(str3, i4));
                            }
                        }
                    } else {
                        spannableStringBuilder.append(charSequence2);
                        spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrReceivedDate", R.string.AccDescrReceivedDate, LocaleController.getString("TodayAt", R.string.TodayAt) + " " + ChatMessageCell.this.currentTimeString));
                    }
                    if (ChatMessageCell.this.getRepliesCount() > 0 && !ChatMessageCell.this.hasCommentLayout()) {
                        spannableStringBuilder.append(charSequence2);
                        spannableStringBuilder.append((CharSequence) LocaleController.formatPluralString("AccDescrNumberOfReplies", ChatMessageCell.this.getRepliesCount(), new Object[0]));
                    }
                    if (ChatMessageCell.this.currentMessageObject.messageOwner.reactions != null && ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results != null) {
                        String str4 = "";
                        if (ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.size() == 1) {
                            TLRPC$ReactionCount tLRPC$ReactionCount2 = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.get(0);
                            TLRPC$Reaction tLRPC$Reaction = tLRPC$ReactionCount2.reaction;
                            String str5 = tLRPC$Reaction instanceof TLRPC$TL_reactionEmoji ? ((TLRPC$TL_reactionEmoji) tLRPC$Reaction).emoticon : str4;
                            int i7 = tLRPC$ReactionCount2.count;
                            if (i7 == 1) {
                                spannableStringBuilder.append(charSequence2);
                                if (ChatMessageCell.this.currentMessageObject.messageOwner.reactions.recent_reactions == null || ChatMessageCell.this.currentMessageObject.messageOwner.reactions.recent_reactions.size() != 1 || (tLRPC$MessagePeerReaction = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.recent_reactions.get(0)) == null) {
                                    z = false;
                                } else {
                                    TLRPC$User user = MessagesController.getInstance(ChatMessageCell.this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(tLRPC$MessagePeerReaction.peer_id)));
                                    z = UserObject.isUserSelf(user);
                                    if (user != null) {
                                        str4 = UserObject.getFirstName(user);
                                    }
                                }
                                if (z) {
                                    spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrYouReactedWith", R.string.AccDescrYouReactedWith, str5));
                                } else {
                                    spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrReactedWith", R.string.AccDescrReactedWith, str4, str5));
                                }
                            } else if (i7 > 1) {
                                spannableStringBuilder.append(charSequence2);
                                spannableStringBuilder.append((CharSequence) LocaleController.formatPluralString("AccDescrNumberOfPeopleReactions", tLRPC$ReactionCount2.count, str5));
                            }
                        } else {
                            spannableStringBuilder.append((CharSequence) LocaleController.getString("Reactions", R.string.Reactions)).append((CharSequence) ": ");
                            int size = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.size();
                            for (int i8 = 0; i8 < size; i8++) {
                                TLRPC$Reaction tLRPC$Reaction2 = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.get(i8).reaction;
                                spannableStringBuilder.append((CharSequence) (tLRPC$Reaction2 instanceof TLRPC$TL_reactionEmoji ? ((TLRPC$TL_reactionEmoji) tLRPC$Reaction2).emoticon : str4)).append((CharSequence) " ").append((CharSequence) (tLRPC$ReactionCount.count + str4));
                                if (i8 + 1 < size) {
                                    spannableStringBuilder.append((CharSequence) ", ");
                                }
                            }
                            spannableStringBuilder.append(charSequence2);
                        }
                    }
                    if ((ChatMessageCell.this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                        spannableStringBuilder.append(charSequence2);
                        spannableStringBuilder.append((CharSequence) LocaleController.formatPluralString("AccDescrNumberOfViews", ChatMessageCell.this.currentMessageObject.messageOwner.views, new Object[0]));
                    }
                    spannableStringBuilder.append(charSequence2);
                    for (final CharacterStyle characterStyle : (CharacterStyle[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ClickableSpan.class)) {
                        int spanStart = spannableStringBuilder.getSpanStart(characterStyle);
                        int spanEnd = spannableStringBuilder.getSpanEnd(characterStyle);
                        spannableStringBuilder.removeSpan(characterStyle);
                        spannableStringBuilder.setSpan(new ClickableSpan() { // from class: org.telegram.ui.Cells.ChatMessageCell.MessageAccessibilityNodeProvider.1
                            {
                                MessageAccessibilityNodeProvider.this = this;
                            }

                            @Override // android.text.style.ClickableSpan
                            public void onClick(View view) {
                                CharacterStyle characterStyle2 = characterStyle;
                                if (!(characterStyle2 instanceof ProfileSpan)) {
                                    if (ChatMessageCell.this.delegate == null) {
                                        return;
                                    }
                                    ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, characterStyle, false);
                                    return;
                                }
                                ((ProfileSpan) characterStyle2).onClick(view);
                            }
                        }, spanStart, spanEnd, 33);
                    }
                    ChatMessageCell.this.accessibilityText = spannableStringBuilder;
                }
                int i9 = Build.VERSION.SDK_INT;
                if (i9 < 24) {
                    obtain.setContentDescription(ChatMessageCell.this.accessibilityText.toString());
                } else {
                    obtain.setText(ChatMessageCell.this.accessibilityText);
                }
                obtain.setEnabled(true);
                if (i9 >= 19 && (collectionItemInfo = obtain.getCollectionItemInfo()) != null) {
                    obtain.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collectionItemInfo.getRowIndex(), 1, 0, 1, false));
                }
                if (i9 >= 21) {
                    obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_msg_options, LocaleController.getString("AccActionMessageOptions", R.string.AccActionMessageOptions)));
                    int iconForCurrentState = ChatMessageCell.this.getIconForCurrentState();
                    if (iconForCurrentState == 0) {
                        string = LocaleController.getString("AccActionPlay", R.string.AccActionPlay);
                    } else if (iconForCurrentState == 1) {
                        string = LocaleController.getString("AccActionPause", R.string.AccActionPause);
                    } else if (iconForCurrentState == 2) {
                        string = LocaleController.getString("AccActionDownload", R.string.AccActionDownload);
                    } else if (iconForCurrentState == 3) {
                        string = LocaleController.getString("AccActionCancelDownload", R.string.AccActionCancelDownload);
                    } else if (iconForCurrentState == 5) {
                        string = LocaleController.getString("AccActionOpenFile", R.string.AccActionOpenFile);
                    } else {
                        string = ChatMessageCell.this.currentMessageObject.type == 16 ? LocaleController.getString("CallAgain", R.string.CallAgain) : null;
                    }
                    obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, string));
                    obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccActionEnterSelectionMode", R.string.AccActionEnterSelectionMode)));
                    if (ChatMessageCell.this.getMiniIconForCurrentState() == 2) {
                        obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_small_button, LocaleController.getString("AccActionDownload", R.string.AccActionDownload)));
                    }
                } else {
                    obtain.addAction(16);
                    obtain.addAction(32);
                }
                if ((ChatMessageCell.this.currentMessageObject.isVoice() || ChatMessageCell.this.currentMessageObject.isRoundVideo() || ChatMessageCell.this.currentMessageObject.isMusic()) && MediaController.getInstance().isPlayingMessage(ChatMessageCell.this.currentMessageObject)) {
                    ChatMessageCell.this.seekBarAccessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(obtain);
                }
                if (ChatMessageCell.this.useTranscribeButton && ChatMessageCell.this.transcribeButton != null) {
                    obtain.addChild(ChatMessageCell.this, 493);
                }
                if (i9 < 24) {
                    ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
                    if (chatMessageCell2.isChat && chatMessageCell2.currentUser != null && !ChatMessageCell.this.currentMessageObject.isOut()) {
                        obtain.addChild(ChatMessageCell.this, 5000);
                    }
                    if (ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable) {
                        Spannable spannable = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
                        int i10 = 0;
                        for (CharacterStyle characterStyle2 : (CharacterStyle[]) spannable.getSpans(0, spannable.length(), ClickableSpan.class)) {
                            obtain.addChild(ChatMessageCell.this, i10 + 2000);
                            i10++;
                        }
                    }
                    if ((ChatMessageCell.this.currentMessageObject.caption instanceof Spannable) && ChatMessageCell.this.captionLayout != null) {
                        Spannable spannable2 = (Spannable) ChatMessageCell.this.currentMessageObject.caption;
                        int i11 = 0;
                        for (CharacterStyle characterStyle3 : (CharacterStyle[]) spannable2.getSpans(0, spannable2.length(), ClickableSpan.class)) {
                            obtain.addChild(ChatMessageCell.this, i11 + 3000);
                            i11++;
                        }
                    }
                }
                Iterator it = ChatMessageCell.this.botButtons.iterator();
                int i12 = 0;
                while (it.hasNext()) {
                    BotButton botButton = (BotButton) it.next();
                    obtain.addChild(ChatMessageCell.this, i12 + 1000);
                    i12++;
                }
                if (ChatMessageCell.this.hintButtonVisible && ChatMessageCell.this.pollHintX != -1 && ChatMessageCell.this.currentMessageObject.isPoll()) {
                    obtain.addChild(ChatMessageCell.this, 495);
                }
                Iterator it2 = ChatMessageCell.this.pollButtons.iterator();
                int i13 = 0;
                while (it2.hasNext()) {
                    PollButton pollButton = (PollButton) it2.next();
                    obtain.addChild(ChatMessageCell.this, i13 + 500);
                    i13++;
                }
                if (ChatMessageCell.this.drawInstantView && !ChatMessageCell.this.instantButtonRect.isEmpty()) {
                    obtain.addChild(ChatMessageCell.this, 499);
                }
                if (ChatMessageCell.this.commentLayout != null) {
                    obtain.addChild(ChatMessageCell.this, 496);
                }
                if (ChatMessageCell.this.drawSideButton == 1) {
                    obtain.addChild(ChatMessageCell.this, 498);
                }
                ChatMessageCell chatMessageCell3 = ChatMessageCell.this;
                if (chatMessageCell3.replyNameLayout != null) {
                    obtain.addChild(chatMessageCell3, 497);
                }
                if (ChatMessageCell.this.forwardedNameLayout[0] != null && ChatMessageCell.this.forwardedNameLayout[1] != null) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_open_forwarded_origin, LocaleController.getString("AccActionOpenForwardedOrigin", R.string.AccActionOpenForwardedOrigin)));
                    } else {
                        obtain.addChild(ChatMessageCell.this, 494);
                    }
                }
                if (ChatMessageCell.this.drawSelectionBackground || ChatMessageCell.this.getBackground() != null) {
                    obtain.setSelected(true);
                }
                return obtain;
            }
            AccessibilityNodeInfo obtain2 = AccessibilityNodeInfo.obtain();
            obtain2.setSource(ChatMessageCell.this, i);
            obtain2.setParent(ChatMessageCell.this);
            obtain2.setPackageName(ChatMessageCell.this.getContext().getPackageName());
            if (i == 5000) {
                if (ChatMessageCell.this.currentUser == null) {
                    return null;
                }
                obtain2.setText(UserObject.getUserName(ChatMessageCell.this.currentUser));
                Rect rect = this.rect;
                int i14 = (int) ChatMessageCell.this.nameX;
                int i15 = (int) ChatMessageCell.this.nameY;
                int i16 = (int) (ChatMessageCell.this.nameX + ChatMessageCell.this.nameWidth);
                float f = ChatMessageCell.this.nameY;
                if (ChatMessageCell.this.nameLayout != null) {
                    i5 = ChatMessageCell.this.nameLayout.getHeight();
                }
                rect.set(i14, i15, i16, (int) (f + i5));
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClassName("android.widget.TextView");
                obtain2.setEnabled(true);
                obtain2.setClickable(true);
                obtain2.setLongClickable(true);
                obtain2.addAction(16);
                obtain2.addAction(32);
            } else if (i >= 3000) {
                if (!(ChatMessageCell.this.currentMessageObject.caption instanceof Spannable) || ChatMessageCell.this.captionLayout == null) {
                    return null;
                }
                Spannable spannable3 = (Spannable) ChatMessageCell.this.currentMessageObject.caption;
                ClickableSpan linkById = getLinkById(i, true);
                if (linkById == null) {
                    return null;
                }
                int[] realSpanStartAndEnd = ChatMessageCell.this.getRealSpanStartAndEnd(spannable3, linkById);
                obtain2.setText(spannable3.subSequence(realSpanStartAndEnd[0], realSpanStartAndEnd[1]).toString());
                ChatMessageCell.this.captionLayout.getText().length();
                ChatMessageCell.this.captionLayout.getSelectionPath(realSpanStartAndEnd[0], realSpanStartAndEnd[1], this.linkPath);
                this.linkPath.computeBounds(this.rectF, true);
                Rect rect2 = this.rect;
                RectF rectF = this.rectF;
                rect2.set((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                this.rect.offset((int) ChatMessageCell.this.captionX, (int) ChatMessageCell.this.captionY);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClassName("android.widget.TextView");
                obtain2.setEnabled(true);
                obtain2.setClickable(true);
                obtain2.setLongClickable(true);
                obtain2.addAction(16);
                obtain2.addAction(32);
            } else if (i >= 2000) {
                if (!(ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable)) {
                    return null;
                }
                Spannable spannable4 = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
                ClickableSpan linkById2 = getLinkById(i, false);
                if (linkById2 == null) {
                    return null;
                }
                int[] realSpanStartAndEnd2 = ChatMessageCell.this.getRealSpanStartAndEnd(spannable4, linkById2);
                obtain2.setText(spannable4.subSequence(realSpanStartAndEnd2[0], realSpanStartAndEnd2[1]).toString());
                Iterator<MessageObject.TextLayoutBlock> it3 = ChatMessageCell.this.currentMessageObject.textLayoutBlocks.iterator();
                while (true) {
                    if (!it3.hasNext()) {
                        break;
                    }
                    MessageObject.TextLayoutBlock next = it3.next();
                    int length = next.textLayout.getText().length();
                    int i17 = next.charactersOffset;
                    if (i17 <= realSpanStartAndEnd2[0] && length + i17 >= realSpanStartAndEnd2[1]) {
                        next.textLayout.getSelectionPath(realSpanStartAndEnd2[0] - i17, realSpanStartAndEnd2[1] - i17, this.linkPath);
                        this.linkPath.computeBounds(this.rectF, true);
                        Rect rect3 = this.rect;
                        RectF rectF2 = this.rectF;
                        rect3.set((int) rectF2.left, (int) rectF2.top, (int) rectF2.right, (int) rectF2.bottom);
                        this.rect.offset(0, (int) next.textYOffset);
                        this.rect.offset(ChatMessageCell.this.textX, ChatMessageCell.this.textY);
                        obtain2.setBoundsInParent(this.rect);
                        if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                            ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                        }
                        this.rect.offset(iArr[0], iArr[1]);
                        obtain2.setBoundsInScreen(this.rect);
                    }
                }
                obtain2.setClassName("android.widget.TextView");
                obtain2.setEnabled(true);
                obtain2.setClickable(true);
                obtain2.setLongClickable(true);
                obtain2.addAction(16);
                obtain2.addAction(32);
            } else if (i >= 1000) {
                int i18 = i - 1000;
                if (i18 >= ChatMessageCell.this.botButtons.size()) {
                    return null;
                }
                BotButton botButton2 = (BotButton) ChatMessageCell.this.botButtons.get(i18);
                obtain2.setText(botButton2.title.getText());
                obtain2.setClassName("android.widget.Button");
                obtain2.setEnabled(true);
                obtain2.setClickable(true);
                obtain2.addAction(16);
                this.rect.set(botButton2.x, botButton2.y, botButton2.x + botButton2.width, botButton2.y + botButton2.height);
                this.rect.offset(ChatMessageCell.this.currentMessageObject.isOutOwner() ? (ChatMessageCell.this.getMeasuredWidth() - ChatMessageCell.this.widthForButtons) - AndroidUtilities.dp(10.0f) : ChatMessageCell.this.backgroundDrawableLeft + AndroidUtilities.dp(ChatMessageCell.this.mediaBackground ? 1.0f : 7.0f), ChatMessageCell.this.layoutHeight);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
            } else if (i >= 500) {
                int i19 = i - 500;
                if (i19 >= ChatMessageCell.this.pollButtons.size()) {
                    return null;
                }
                PollButton pollButton2 = (PollButton) ChatMessageCell.this.pollButtons.get(i19);
                StringBuilder sb = new StringBuilder(pollButton2.title.getText());
                if (ChatMessageCell.this.pollVoted) {
                    obtain2.setSelected(pollButton2.chosen);
                    sb.append(", ");
                    sb.append(pollButton2.percent);
                    sb.append("%");
                    if (ChatMessageCell.this.lastPoll != null && ChatMessageCell.this.lastPoll.quiz && (pollButton2.chosen || pollButton2.correct)) {
                        sb.append(", ");
                        if (pollButton2.correct) {
                            i3 = R.string.AccDescrQuizCorrectAnswer;
                            str2 = "AccDescrQuizCorrectAnswer";
                        } else {
                            i3 = R.string.AccDescrQuizIncorrectAnswer;
                            str2 = "AccDescrQuizIncorrectAnswer";
                        }
                        sb.append(LocaleController.getString(str2, i3));
                    }
                } else {
                    obtain2.setClassName("android.widget.Button");
                }
                obtain2.setText(sb);
                obtain2.setEnabled(true);
                obtain2.addAction(16);
                int i20 = pollButton2.y + ChatMessageCell.this.namesOffset;
                int dp = ChatMessageCell.this.backgroundWidth - AndroidUtilities.dp(76.0f);
                Rect rect4 = this.rect;
                int i21 = pollButton2.x;
                rect4.set(i21, i20, dp + i21, pollButton2.height + i20);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 495) {
                obtain2.setClassName("android.widget.Button");
                obtain2.setEnabled(true);
                obtain2.setText(LocaleController.getString("AccDescrQuizExplanation", R.string.AccDescrQuizExplanation));
                obtain2.addAction(16);
                this.rect.set(ChatMessageCell.this.pollHintX - AndroidUtilities.dp(8.0f), ChatMessageCell.this.pollHintY - AndroidUtilities.dp(8.0f), ChatMessageCell.this.pollHintX + AndroidUtilities.dp(32.0f), ChatMessageCell.this.pollHintY + AndroidUtilities.dp(32.0f));
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 499) {
                obtain2.setClassName("android.widget.Button");
                obtain2.setEnabled(true);
                if (ChatMessageCell.this.instantViewLayout != null) {
                    obtain2.setText(ChatMessageCell.this.instantViewLayout.getText());
                }
                obtain2.addAction(16);
                ChatMessageCell.this.instantButtonRect.round(this.rect);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 498) {
                obtain2.setClassName("android.widget.ImageButton");
                obtain2.setEnabled(true);
                ChatMessageCell chatMessageCell4 = ChatMessageCell.this;
                if (chatMessageCell4.isOpenChatByShare(chatMessageCell4.currentMessageObject)) {
                    obtain2.setContentDescription(LocaleController.getString("AccDescrOpenChat", R.string.AccDescrOpenChat));
                } else {
                    obtain2.setContentDescription(LocaleController.getString("ShareFile", R.string.ShareFile));
                }
                obtain2.addAction(16);
                this.rect.set((int) ChatMessageCell.this.sideStartX, (int) ChatMessageCell.this.sideStartY, ((int) ChatMessageCell.this.sideStartX) + AndroidUtilities.dp(40.0f), ((int) ChatMessageCell.this.sideStartY) + AndroidUtilities.dp(32.0f));
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 497) {
                obtain2.setEnabled(true);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(LocaleController.getString("Reply", R.string.Reply));
                sb2.append(", ");
                StaticLayout staticLayout = ChatMessageCell.this.replyNameLayout;
                if (staticLayout != null) {
                    sb2.append(staticLayout.getText());
                    sb2.append(", ");
                }
                StaticLayout staticLayout2 = ChatMessageCell.this.replyTextLayout;
                if (staticLayout2 != null) {
                    sb2.append(staticLayout2.getText());
                }
                obtain2.setContentDescription(sb2.toString());
                obtain2.addAction(16);
                Rect rect5 = this.rect;
                ChatMessageCell chatMessageCell5 = ChatMessageCell.this;
                int i22 = chatMessageCell5.replyStartX;
                ChatMessageCell chatMessageCell6 = ChatMessageCell.this;
                rect5.set(i22, chatMessageCell5.replyStartY, Math.max(chatMessageCell5.replyNameWidth, ChatMessageCell.this.replyTextWidth) + i22, chatMessageCell6.replyStartY + ((int) chatMessageCell6.replyHeight));
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 494) {
                obtain2.setEnabled(true);
                StringBuilder sb3 = new StringBuilder();
                if (ChatMessageCell.this.forwardedNameLayout[0] != null && ChatMessageCell.this.forwardedNameLayout[1] != null) {
                    int i23 = 0;
                    while (i23 < 2) {
                        sb3.append(ChatMessageCell.this.forwardedNameLayout[i23].getText());
                        sb3.append(i23 == 0 ? " " : "\n");
                        i23++;
                    }
                }
                obtain2.setContentDescription(sb3.toString());
                obtain2.addAction(16);
                int min = (int) Math.min(ChatMessageCell.this.forwardNameX - ChatMessageCell.this.forwardNameOffsetX[0], ChatMessageCell.this.forwardNameX - ChatMessageCell.this.forwardNameOffsetX[1]);
                this.rect.set(min, ChatMessageCell.this.forwardNameY, ChatMessageCell.this.forwardedNameWidth + min, ChatMessageCell.this.forwardNameY + ChatMessageCell.this.forwardHeight);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 496) {
                obtain2.setClassName("android.widget.Button");
                obtain2.setEnabled(true);
                int repliesCount = ChatMessageCell.this.getRepliesCount();
                if (ChatMessageCell.this.currentMessageObject != null && !ChatMessageCell.this.currentMessageObject.shouldDrawWithoutBackground() && !ChatMessageCell.this.currentMessageObject.isAnimatedEmoji()) {
                    if (ChatMessageCell.this.isRepliesChat) {
                        formatShortNumber = LocaleController.getString("ViewInChat", R.string.ViewInChat);
                    } else {
                        formatShortNumber = repliesCount == 0 ? LocaleController.getString("LeaveAComment", R.string.LeaveAComment) : LocaleController.formatPluralString("CommentsCount", repliesCount, new Object[0]);
                    }
                } else {
                    formatShortNumber = (ChatMessageCell.this.isRepliesChat || repliesCount <= 0) ? null : LocaleController.formatShortNumber(repliesCount, null);
                }
                if (formatShortNumber != null) {
                    obtain2.setText(formatShortNumber);
                }
                obtain2.addAction(16);
                this.rect.set(ChatMessageCell.this.commentButtonRect);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 493) {
                obtain2.setClassName("android.widget.Button");
                obtain2.setEnabled(true);
                if (ChatMessageCell.this.currentMessageObject.isVoiceTranscriptionOpen()) {
                    i2 = R.string.AccActionCloseTranscription;
                    str = "AccActionCloseTranscription";
                } else {
                    i2 = R.string.AccActionOpenTranscription;
                    str = "AccActionOpenTranscription";
                }
                obtain2.setText(LocaleController.getString(str, i2));
                obtain2.addAction(16);
                if (ChatMessageCell.this.transcribeButton != null) {
                    this.rect.set((int) ChatMessageCell.this.transcribeX, (int) ChatMessageCell.this.transcribeY, (int) (ChatMessageCell.this.transcribeX + ChatMessageCell.this.transcribeButton.width()), (int) (ChatMessageCell.this.transcribeY + ChatMessageCell.this.transcribeButton.height()));
                }
                obtain2.setBoundsInParent(this.rect);
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            }
            obtain2.setFocusable(true);
            obtain2.setVisibleToUser(true);
            return obtain2;
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public boolean performAction(int i, int i2, Bundle bundle) {
            if (i == -1) {
                ChatMessageCell.this.performAccessibilityAction(i2, bundle);
            } else if (i2 == 64) {
                ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 32768);
            } else {
                boolean z = false;
                if (i2 == 16) {
                    if (i == 5000) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCellDelegate chatMessageCellDelegate = ChatMessageCell.this.delegate;
                            ChatMessageCell chatMessageCell = ChatMessageCell.this;
                            chatMessageCellDelegate.didPressUserAvatar(chatMessageCell, chatMessageCell.currentUser, 0.0f, 0.0f);
                        }
                    } else if (i >= 3000) {
                        ClickableSpan linkById = getLinkById(i, true);
                        if (linkById != null) {
                            ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, linkById, false);
                            ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                        }
                    } else if (i >= 2000) {
                        ClickableSpan linkById2 = getLinkById(i, false);
                        if (linkById2 != null) {
                            ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, linkById2, false);
                            ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                        }
                    } else if (i >= 1000) {
                        int i3 = i - 1000;
                        if (i3 >= ChatMessageCell.this.botButtons.size()) {
                            return false;
                        }
                        BotButton botButton = (BotButton) ChatMessageCell.this.botButtons.get(i3);
                        if (ChatMessageCell.this.delegate != null && botButton.button != null) {
                            ChatMessageCell.this.delegate.didPressBotButton(ChatMessageCell.this, botButton.button);
                        }
                        ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                    } else if (i >= 500) {
                        int i4 = i - 500;
                        if (i4 >= ChatMessageCell.this.pollButtons.size()) {
                            return false;
                        }
                        PollButton pollButton = (PollButton) ChatMessageCell.this.pollButtons.get(i4);
                        if (ChatMessageCell.this.delegate != null) {
                            ArrayList<TLRPC$TL_pollAnswer> arrayList = new ArrayList<>();
                            arrayList.add(pollButton.answer);
                            ChatMessageCell.this.delegate.didPressVoteButtons(ChatMessageCell.this, arrayList, -1, 0, 0);
                        }
                        ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                    } else if (i == 495) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCell.this.delegate.didPressHint(ChatMessageCell.this, 0);
                        }
                    } else if (i == 499) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCellDelegate chatMessageCellDelegate2 = ChatMessageCell.this.delegate;
                            ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
                            chatMessageCellDelegate2.didPressInstantButton(chatMessageCell2, chatMessageCell2.drawInstantViewType);
                        }
                    } else if (i == 498) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCell.this.delegate.didPressSideButton(ChatMessageCell.this);
                        }
                    } else if (i == 497) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCell chatMessageCell3 = ChatMessageCell.this;
                            if ((!chatMessageCell3.isThreadChat || chatMessageCell3.currentMessageObject.getReplyTopMsgId() != 0) && ChatMessageCell.this.currentMessageObject.hasValidReplyMessageObject()) {
                                ChatMessageCellDelegate chatMessageCellDelegate3 = ChatMessageCell.this.delegate;
                                ChatMessageCell chatMessageCell4 = ChatMessageCell.this;
                                chatMessageCellDelegate3.didPressReplyMessage(chatMessageCell4, chatMessageCell4.currentMessageObject.getReplyMsgId());
                            }
                        }
                    } else if (i == 494) {
                        if (ChatMessageCell.this.delegate != null) {
                            if (ChatMessageCell.this.currentForwardChannel != null) {
                                ChatMessageCellDelegate chatMessageCellDelegate4 = ChatMessageCell.this.delegate;
                                ChatMessageCell chatMessageCell5 = ChatMessageCell.this;
                                chatMessageCellDelegate4.didPressChannelAvatar(chatMessageCell5, chatMessageCell5.currentForwardChannel, ChatMessageCell.this.currentMessageObject.messageOwner.fwd_from.channel_post, ChatMessageCell.this.lastTouchX, ChatMessageCell.this.lastTouchY);
                            } else if (ChatMessageCell.this.currentForwardUser != null) {
                                ChatMessageCellDelegate chatMessageCellDelegate5 = ChatMessageCell.this.delegate;
                                ChatMessageCell chatMessageCell6 = ChatMessageCell.this;
                                chatMessageCellDelegate5.didPressUserAvatar(chatMessageCell6, chatMessageCell6.currentForwardUser, ChatMessageCell.this.lastTouchX, ChatMessageCell.this.lastTouchY);
                            } else if (ChatMessageCell.this.currentForwardName != null) {
                                ChatMessageCell.this.delegate.didPressHiddenForward(ChatMessageCell.this);
                            }
                        }
                    } else if (i == 496) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCell chatMessageCell7 = ChatMessageCell.this;
                            if (chatMessageCell7.isRepliesChat) {
                                chatMessageCell7.delegate.didPressSideButton(ChatMessageCell.this);
                            } else {
                                chatMessageCell7.delegate.didPressCommentButton(ChatMessageCell.this);
                            }
                        }
                    } else if (i == 493 && ChatMessageCell.this.transcribeButton != null) {
                        ChatMessageCell.this.transcribeButton.onTap();
                    }
                } else if (i2 == 32) {
                    if (i >= 3000) {
                        z = true;
                    }
                    ClickableSpan linkById3 = getLinkById(i, z);
                    if (linkById3 != null) {
                        ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, linkById3, true);
                        ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 2);
                    }
                }
            }
            return true;
        }

        private ClickableSpan getLinkById(int i, boolean z) {
            if (i == 5000) {
                return null;
            }
            if (z) {
                int i2 = i - 3000;
                if (!(ChatMessageCell.this.currentMessageObject.caption instanceof Spannable) || i2 < 0) {
                    return null;
                }
                Spannable spannable = (Spannable) ChatMessageCell.this.currentMessageObject.caption;
                ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(0, spannable.length(), ClickableSpan.class);
                if (clickableSpanArr.length > i2) {
                    return clickableSpanArr[i2];
                }
                return null;
            }
            int i3 = i - 2000;
            if (!(ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable) || i3 < 0) {
                return null;
            }
            Spannable spannable2 = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
            ClickableSpan[] clickableSpanArr2 = (ClickableSpan[]) spannable2.getSpans(0, spannable2.length(), ClickableSpan.class);
            if (clickableSpanArr2.length > i3) {
                return clickableSpanArr2[i3];
            }
            return null;
        }
    }

    public void setImageCoords(float f, float f2, float f3, float f4) {
        this.photoImage.setImageCoords(f, f2, f3, f4);
        int i = this.documentAttachType;
        if (i == 4 || i == 2) {
            this.videoButtonX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f));
            int imageY = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f));
            this.videoButtonY = imageY;
            RadialProgress2 radialProgress2 = this.videoRadialProgress;
            int i2 = this.videoButtonX;
            radialProgress2.setProgressRect(i2, imageY, AndroidUtilities.dp(24.0f) + i2, this.videoButtonY + AndroidUtilities.dp(24.0f));
            this.buttonX = (int) (f + ((this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f)) / 2.0f));
            int imageY2 = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f)) / 2.0f));
            this.buttonY = imageY2;
            RadialProgress2 radialProgress22 = this.radialProgress;
            int i3 = this.buttonX;
            radialProgress22.setProgressRect(i3, imageY2, AndroidUtilities.dp(48.0f) + i3, this.buttonY + AndroidUtilities.dp(48.0f));
        }
    }

    @Override // android.view.View
    public float getAlpha() {
        if (this.ALPHA_PROPERTY_WORKAROUND) {
            return this.alphaInternal;
        }
        return super.getAlpha();
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        boolean z = false;
        boolean z2 = f == 1.0f;
        if (getAlpha() == 1.0f) {
            z = true;
        }
        if (z2 != z) {
            invalidate();
        }
        if (this.ALPHA_PROPERTY_WORKAROUND) {
            this.alphaInternal = f;
            invalidate();
        } else {
            super.setAlpha(f);
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if ((groupedMessagePosition != null && (groupedMessagePosition.minY != 0 || groupedMessagePosition.minX != 0)) || ((this.enterTransitionInProgress && !this.currentMessageObject.isVoice()) || this.replyNameLayout == null || this.replyTextLayout == null)) {
            MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
            if (groupedMessagePosition2 != null) {
                int i = groupedMessagePosition2.flags;
                if ((i & 8) == 0 || (i & 1) == 0) {
                    return;
                }
            }
            if (this.reactionsLayoutInBubble.isSmall) {
                return;
            }
        }
        invalidate();
    }

    public int getCurrentBackgroundLeft() {
        Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
        if (messageDrawable == null) {
            return 0;
        }
        int i = messageDrawable.getBounds().left;
        if (this.currentMessageObject.isOutOwner() || this.transitionParams.changePinnedBottomProgress == 1.0f) {
            return i;
        }
        boolean z = this.isRoundVideo;
        if ((!z && this.mediaBackground) || this.drawPinnedBottom) {
            return i;
        }
        if (z) {
            return (int) (i - (AndroidUtilities.dp(6.0f) * getVideoTranscriptionProgress()));
        }
        return i - AndroidUtilities.dp(6.0f);
    }

    public int getCurrentBackgroundRight() {
        Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
        if (messageDrawable == null) {
            return getWidth();
        }
        int i = messageDrawable.getBounds().right;
        if (!this.currentMessageObject.isOutOwner() || this.transitionParams.changePinnedBottomProgress == 1.0f) {
            return i;
        }
        boolean z = this.isRoundVideo;
        if ((!z && this.mediaBackground) || this.drawPinnedBottom) {
            return i;
        }
        if (z) {
            return (int) (i + (AndroidUtilities.dp(6.0f) * getVideoTranscriptionProgress()));
        }
        return i + AndroidUtilities.dp(6.0f);
    }

    public TransitionParams getTransitionParams() {
        return this.transitionParams;
    }

    public int getTopMediaOffset() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.type != 14) {
            return 0;
        }
        return this.mediaOffsetY + this.namesOffset;
    }

    public int getTextX() {
        return this.textX;
    }

    public int getTextY() {
        return this.textY;
    }

    public boolean isPlayingRound() {
        return this.isRoundVideo && this.isPlayingRound;
    }

    public int getParentWidth() {
        int i;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            messageObject = this.messageObjectToSet;
        }
        return (messageObject == null || !messageObject.preview || (i = this.parentWidth) <= 0) ? AndroidUtilities.displaySize.x : i;
    }

    /* loaded from: classes3.dex */
    public class TransitionParams {
        public boolean animateBackgroundBoundsInner;
        boolean animateBotButtonsChanged;
        private boolean animateButton;
        public boolean animateChange;
        private int animateCommentArrowX;
        private boolean animateCommentDrawUnread;
        private int animateCommentUnreadX;
        private float animateCommentX;
        private boolean animateComments;
        private StaticLayout animateCommentsLayout;
        public boolean animateDrawBackground;
        private boolean animateDrawCommentNumber;
        public boolean animateDrawingTimeAlpha;
        private boolean animateEditedEnter;
        private StaticLayout animateEditedLayout;
        private int animateEditedWidthDiff;
        int animateForwardNameWidth;
        float animateForwardNameX;
        public boolean animateForwardedLayout;
        public int animateForwardedNamesOffset;
        private float animateFromButtonX;
        private float animateFromButtonY;
        public float animateFromReplyY;
        public float animateFromRoundVideoDotY;
        public float animateFromTextY;
        public int animateFromTimeX;
        public float animateFromTimeXPinned;
        private float animateFromTimeXReplies;
        private float animateFromTimeXViews;
        public boolean animateLocationIsExpired;
        boolean animateMessageText;
        private float animateNameX;
        private AnimatedEmojiSpan.EmojiGroupedSpans animateOutAnimateEmoji;
        private StaticLayout animateOutCaptionLayout;
        private ArrayList<MessageObject.TextLayoutBlock> animateOutTextBlocks;
        private boolean animatePinned;
        public boolean animatePlayingRound;
        public boolean animateRadius;
        boolean animateReplaceCaptionLayout;
        private boolean animateReplies;
        private StaticLayout animateRepliesLayout;
        public boolean animateRoundVideoDotY;
        private boolean animateShouldDrawMenuDrawable;
        private boolean animateShouldDrawTimeOnMedia;
        private boolean animateSign;
        public boolean animateText;
        private StaticLayout animateTimeLayout;
        private int animateTimeWidth;
        public float animateToImageH;
        public float animateToImageW;
        public float animateToImageX;
        public float animateToImageY;
        public int[] animateToRadius;
        private int animateTotalCommentWidth;
        public boolean animateUseTranscribeButton;
        private StaticLayout animateViewsLayout;
        public float captionFromX;
        public float captionFromY;
        public float deltaBottom;
        public float deltaLeft;
        public float deltaRight;
        public float deltaTop;
        public boolean drawPinnedBottomBackground;
        public boolean ignoreAlpha;
        public boolean imageChangeBoundsTransition;
        public int lastBackgroundLeft;
        public int lastBackgroundRight;
        private float lastButtonX;
        private float lastButtonY;
        private int lastCommentArrowX;
        private boolean lastCommentDrawUnread;
        private StaticLayout lastCommentLayout;
        private int lastCommentUnreadX;
        private float lastCommentX;
        private int lastCommentsCount;
        public boolean lastDrawBackground;
        private boolean lastDrawCommentNumber;
        public StaticLayout lastDrawDocTitleLayout;
        public StaticLayout lastDrawInfoLayout;
        public float lastDrawLocationExpireProgress;
        public String lastDrawLocationExpireText;
        public float lastDrawReplyY;
        public float lastDrawRoundVideoDotY;
        public boolean lastDrawTime;
        private StaticLayout lastDrawingCaptionLayout;
        public float lastDrawingCaptionX;
        public float lastDrawingCaptionY;
        private boolean lastDrawingEdited;
        public float lastDrawingImageH;
        public float lastDrawingImageW;
        public float lastDrawingImageX;
        public float lastDrawingImageY;
        private ArrayList<MessageObject.TextLayoutBlock> lastDrawingTextBlocks;
        public float lastDrawingTextY;
        public boolean lastDrawnForwardedName;
        int lastForwardNameWidth;
        float lastForwardNameX;
        public int lastForwardedNamesOffset;
        private boolean lastIsPinned;
        private boolean lastIsPlayingRound;
        public boolean lastLocatinIsExpired;
        private int lastRepliesCount;
        private StaticLayout lastRepliesLayout;
        private boolean lastShouldDrawMenuDrawable;
        private boolean lastShouldDrawTimeOnMedia;
        private String lastSignMessage;
        private StaticLayout lastTimeLayout;
        private int lastTimeWidth;
        public int lastTimeX;
        public float lastTimeXPinned;
        private float lastTimeXReplies;
        private float lastTimeXViews;
        public int lastTopOffset;
        private int lastTotalCommentWidth;
        public boolean lastUseTranscribeButton;
        private int lastViewsCount;
        private StaticLayout lastViewsLayout;
        public boolean messageEntering;
        private boolean moveCaption;
        public boolean shouldAnimateTimeX;
        public float toDeltaLeft;
        public float toDeltaRight;
        public boolean transformGroupToSingleMessage;
        public boolean updatePhotoImageX;
        public boolean wasDraw;
        public int[] imageRoundRadius = new int[4];
        public float captionEnterProgress = 1.0f;
        public float changePinnedBottomProgress = 1.0f;
        public Rect lastDrawingBackgroundRect = new Rect();
        public float animateChangeProgress = 1.0f;
        private ArrayList<BotButton> lastDrawBotButtons = new ArrayList<>();
        private ArrayList<BotButton> transitionBotButtons = new ArrayList<>();
        public int lastStatusDrawableParams = -1;
        public StaticLayout[] lastDrawnForwardedNameLayout = new StaticLayout[2];
        public StaticLayout[] animatingForwardedNameLayout = new StaticLayout[2];

        public boolean supportChangeAnimation() {
            return true;
        }

        public TransitionParams() {
            ChatMessageCell.this = r2;
        }

        public void recordDrawingState() {
            this.wasDraw = true;
            this.lastDrawingImageX = ChatMessageCell.this.photoImage.getImageX();
            this.lastDrawingImageY = ChatMessageCell.this.photoImage.getImageY();
            this.lastDrawingImageW = ChatMessageCell.this.photoImage.getImageWidth();
            this.lastDrawingImageH = ChatMessageCell.this.photoImage.getImageHeight();
            System.arraycopy(ChatMessageCell.this.photoImage.getRoundRadius(), 0, this.imageRoundRadius, 0, 4);
            if (ChatMessageCell.this.currentBackgroundDrawable != null) {
                this.lastDrawingBackgroundRect.set(ChatMessageCell.this.currentBackgroundDrawable.getBounds());
            }
            this.lastDrawingTextBlocks = ChatMessageCell.this.currentMessageObject.textLayoutBlocks;
            this.lastDrawingEdited = ChatMessageCell.this.edited;
            this.lastDrawingCaptionX = ChatMessageCell.this.captionX;
            this.lastDrawingCaptionY = ChatMessageCell.this.captionY;
            this.lastDrawingCaptionLayout = ChatMessageCell.this.captionLayout;
            this.lastDrawBotButtons.clear();
            if (!ChatMessageCell.this.botButtons.isEmpty()) {
                this.lastDrawBotButtons.addAll(ChatMessageCell.this.botButtons);
            }
            if (ChatMessageCell.this.commentLayout != null) {
                this.lastCommentsCount = ChatMessageCell.this.getRepliesCount();
                this.lastTotalCommentWidth = ChatMessageCell.this.totalCommentWidth;
                this.lastCommentLayout = ChatMessageCell.this.commentLayout;
                this.lastCommentArrowX = ChatMessageCell.this.commentArrowX;
                this.lastCommentUnreadX = ChatMessageCell.this.commentUnreadX;
                this.lastCommentDrawUnread = ChatMessageCell.this.commentDrawUnread;
                this.lastCommentX = ChatMessageCell.this.commentX;
                this.lastDrawCommentNumber = ChatMessageCell.this.drawCommentNumber;
            }
            this.lastRepliesCount = ChatMessageCell.this.getRepliesCount();
            this.lastViewsCount = ChatMessageCell.this.getMessageObject().messageOwner.views;
            this.lastRepliesLayout = ChatMessageCell.this.repliesLayout;
            this.lastViewsLayout = ChatMessageCell.this.viewsLayout;
            ChatMessageCell chatMessageCell = ChatMessageCell.this;
            this.lastIsPinned = chatMessageCell.isPinned;
            this.lastSignMessage = chatMessageCell.lastPostAuthor;
            this.lastDrawBackground = ChatMessageCell.this.drawBackground;
            this.lastUseTranscribeButton = ChatMessageCell.this.useTranscribeButton;
            this.lastButtonX = ChatMessageCell.this.buttonX;
            this.lastButtonY = ChatMessageCell.this.buttonY;
            this.lastDrawTime = !ChatMessageCell.this.forceNotDrawTime;
            this.lastTimeX = ChatMessageCell.this.timeX;
            this.lastTimeLayout = ChatMessageCell.this.timeLayout;
            this.lastTimeWidth = ChatMessageCell.this.timeWidth;
            this.lastShouldDrawTimeOnMedia = ChatMessageCell.this.shouldDrawTimeOnMedia();
            this.lastTopOffset = ChatMessageCell.this.getTopMediaOffset();
            this.lastShouldDrawMenuDrawable = ChatMessageCell.this.shouldDrawMenuDrawable();
            this.lastLocatinIsExpired = ChatMessageCell.this.locationExpired;
            this.lastIsPlayingRound = ChatMessageCell.this.isPlayingRound;
            this.lastDrawingTextY = ChatMessageCell.this.textY;
            int unused = ChatMessageCell.this.textX;
            this.lastDrawnForwardedNameLayout[0] = ChatMessageCell.this.forwardedNameLayout[0];
            this.lastDrawnForwardedNameLayout[1] = ChatMessageCell.this.forwardedNameLayout[1];
            this.lastDrawnForwardedName = ChatMessageCell.this.currentMessageObject.needDrawForwarded();
            this.lastForwardNameX = ChatMessageCell.this.forwardNameX;
            this.lastForwardedNamesOffset = ChatMessageCell.this.namesOffset;
            this.lastForwardNameWidth = ChatMessageCell.this.forwardedNameWidth;
            this.lastBackgroundLeft = ChatMessageCell.this.getCurrentBackgroundLeft();
            this.lastBackgroundRight = ChatMessageCell.this.currentBackgroundDrawable.getBounds().right;
            ChatMessageCell.this.reactionsLayoutInBubble.recordDrawingState();
            ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
            if (chatMessageCell2.replyNameLayout != null) {
                this.lastDrawReplyY = chatMessageCell2.replyStartY;
            } else {
                this.lastDrawReplyY = 0.0f;
            }
        }

        public void recordDrawingStatePreview() {
            this.lastDrawnForwardedNameLayout[0] = ChatMessageCell.this.forwardedNameLayout[0];
            this.lastDrawnForwardedNameLayout[1] = ChatMessageCell.this.forwardedNameLayout[1];
            this.lastDrawnForwardedName = ChatMessageCell.this.currentMessageObject.needDrawForwarded();
            this.lastForwardNameX = ChatMessageCell.this.forwardNameX;
            this.lastForwardedNamesOffset = ChatMessageCell.this.namesOffset;
            this.lastForwardNameWidth = ChatMessageCell.this.forwardedNameWidth;
        }

        /* JADX WARN: Removed duplicated region for block: B:263:0x00c8  */
        /* JADX WARN: Removed duplicated region for block: B:264:0x00ea  */
        /* JADX WARN: Removed duplicated region for block: B:288:0x01e7  */
        /* JADX WARN: Removed duplicated region for block: B:291:0x01f6  */
        /* JADX WARN: Removed duplicated region for block: B:294:0x0203  */
        /* JADX WARN: Removed duplicated region for block: B:318:0x0289  */
        /* JADX WARN: Removed duplicated region for block: B:330:0x02c5  */
        /* JADX WARN: Removed duplicated region for block: B:334:0x02e3  */
        /* JADX WARN: Removed duplicated region for block: B:337:0x02e9  */
        /* JADX WARN: Removed duplicated region for block: B:349:0x0328  */
        /* JADX WARN: Removed duplicated region for block: B:352:0x0338  */
        /* JADX WARN: Removed duplicated region for block: B:361:0x036d  */
        /* JADX WARN: Removed duplicated region for block: B:362:0x0372  */
        /* JADX WARN: Removed duplicated region for block: B:365:0x0377  */
        /* JADX WARN: Removed duplicated region for block: B:372:0x0393  */
        /* JADX WARN: Removed duplicated region for block: B:377:0x03af  */
        /* JADX WARN: Removed duplicated region for block: B:388:0x0401  */
        /* JADX WARN: Removed duplicated region for block: B:391:0x0416  */
        /* JADX WARN: Removed duplicated region for block: B:393:0x041c  */
        /* JADX WARN: Removed duplicated region for block: B:402:0x0468  */
        /* JADX WARN: Removed duplicated region for block: B:405:0x0474  */
        /* JADX WARN: Removed duplicated region for block: B:408:0x0480  */
        /* JADX WARN: Removed duplicated region for block: B:411:0x0490  */
        /* JADX WARN: Removed duplicated region for block: B:414:0x049f  */
        /* JADX WARN: Removed duplicated region for block: B:419:0x04d7  */
        /* JADX WARN: Removed duplicated region for block: B:422:0x04e4  */
        /* JADX WARN: Removed duplicated region for block: B:433:0x0517  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean animateChange() {
            /*
                Method dump skipped, instructions count: 1322
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.TransitionParams.animateChange():boolean");
        }

        public void onDetach() {
            this.wasDraw = false;
        }

        public void resetAnimation() {
            this.animateChange = false;
            this.animatePinned = false;
            this.animateBackgroundBoundsInner = false;
            this.deltaLeft = 0.0f;
            this.deltaRight = 0.0f;
            this.deltaBottom = 0.0f;
            this.deltaTop = 0.0f;
            this.toDeltaLeft = 0.0f;
            this.toDeltaRight = 0.0f;
            if (this.imageChangeBoundsTransition && this.animateToImageW != 0.0f && this.animateToImageH != 0.0f) {
                ChatMessageCell.this.photoImage.setImageCoords(this.animateToImageX, this.animateToImageY, this.animateToImageW, this.animateToImageH);
            }
            if (this.animateRadius) {
                ChatMessageCell.this.photoImage.setRoundRadius(this.animateToRadius);
            }
            this.animateToImageX = 0.0f;
            this.animateToImageY = 0.0f;
            this.animateToImageW = 0.0f;
            this.animateToImageH = 0.0f;
            this.imageChangeBoundsTransition = false;
            this.changePinnedBottomProgress = 1.0f;
            this.captionEnterProgress = 1.0f;
            this.animateRadius = false;
            this.animateChangeProgress = 1.0f;
            this.animateMessageText = false;
            this.animateOutTextBlocks = null;
            this.animateEditedLayout = null;
            this.animateTimeLayout = null;
            this.animateEditedEnter = false;
            this.animateReplaceCaptionLayout = false;
            this.transformGroupToSingleMessage = false;
            this.animateOutCaptionLayout = null;
            AnimatedEmojiSpan.release(ChatMessageCell.this, this.animateOutAnimateEmoji);
            this.animateOutAnimateEmoji = null;
            this.moveCaption = false;
            this.animateDrawingTimeAlpha = false;
            this.transitionBotButtons.clear();
            this.animateButton = false;
            this.animateReplies = false;
            this.animateRepliesLayout = null;
            this.animateComments = false;
            this.animateCommentsLayout = null;
            this.animateViewsLayout = null;
            this.animateShouldDrawTimeOnMedia = false;
            this.animateShouldDrawMenuDrawable = false;
            this.shouldAnimateTimeX = false;
            this.animateDrawBackground = false;
            this.animateSign = false;
            this.animateDrawingTimeAlpha = false;
            this.animateLocationIsExpired = false;
            this.animatePlayingRound = false;
            this.animateText = false;
            this.animateForwardedLayout = false;
            StaticLayout[] staticLayoutArr = this.animatingForwardedNameLayout;
            staticLayoutArr[0] = null;
            staticLayoutArr[1] = null;
            this.animateRoundVideoDotY = false;
            ChatMessageCell.this.reactionsLayoutInBubble.resetAnimation();
        }

        /* JADX WARN: Removed duplicated region for block: B:77:0x006b  */
        /* JADX WARN: Removed duplicated region for block: B:78:0x006d  */
        /* JADX WARN: Removed duplicated region for block: B:82:0x0072  */
        /* JADX WARN: Removed duplicated region for block: B:86:0x0077  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public int createStatusDrawableParams() {
            /*
                r7 = this;
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isOutOwner()
                r1 = 8
                r2 = 4
                r3 = 1
                r4 = 0
                if (r0 == 0) goto L7a
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isSending()
                if (r0 != 0) goto L65
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isEditing()
                if (r0 == 0) goto L2a
                goto L65
            L2a:
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isSendError()
                if (r0 == 0) goto L3b
                r0 = 0
                r3 = 0
                r5 = 0
                r6 = 1
                goto L69
            L3b:
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isSent()
                if (r0 == 0) goto L61
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.scheduled
                if (r0 != 0) goto L5f
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isUnread()
                if (r0 != 0) goto L5f
                r0 = 1
                goto L63
            L5f:
                r0 = 0
                goto L63
            L61:
                r0 = 0
                r3 = 0
            L63:
                r5 = 0
                goto L68
            L65:
                r0 = 0
                r3 = 0
                r5 = 1
            L68:
                r6 = 0
            L69:
                if (r3 == 0) goto L6d
                r3 = 2
                goto L6e
            L6d:
                r3 = 0
            L6e:
                r0 = r0 | r3
                if (r5 == 0) goto L72
                goto L73
            L72:
                r2 = 0
            L73:
                r0 = r0 | r2
                if (r6 == 0) goto L77
                goto L78
            L77:
                r1 = 0
            L78:
                r0 = r0 | r1
                return r0
            L7a:
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isSending()
                if (r0 != 0) goto L94
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isEditing()
                if (r0 == 0) goto L93
                goto L94
            L93:
                r3 = 0
            L94:
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isSendError()
                if (r3 == 0) goto La1
                goto La2
            La1:
                r2 = 0
            La2:
                if (r0 == 0) goto La5
                goto La6
            La5:
                r1 = 0
            La6:
                r0 = r2 | r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.TransitionParams.createStatusDrawableParams():int");
        }
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private Drawable getThemedDrawable(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(str) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(str);
    }

    public Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    public boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }
}
