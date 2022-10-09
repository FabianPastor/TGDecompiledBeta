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
import android.graphics.Shader;
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
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedNumberLayout;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
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
    private boolean drawVideoImageButton;
    private boolean drawVideoSize;
    private StaticLayout durationLayout;
    private int durationWidth;
    private boolean edited;
    boolean enterTransitionInProgress;
    private boolean firstCircleLength;
    private int firstVisibleBlockNum;
    private boolean flipImage;
    private boolean forceNotDrawTime;
    private boolean forwardBotPressed;
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
    private WebFile lastWebFile;
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
    public ImageReceiver replyImageReceiver;
    public StaticLayout replyNameLayout;
    private int replyNameOffset;
    private int replyNameWidth;
    private boolean replyPanelIsForward;
    private boolean replyPressed;
    public List<SpoilerEffect> replySpoilers;
    private Stack<SpoilerEffect> replySpoilersPool;
    public int replyStartX;
    public int replyStartY;
    public StaticLayout replyTextLayout;
    private int replyTextOffset;
    private int replyTextWidth;
    private final Theme.ResourcesProvider resourcesProvider;
    private float roundPlayingDrawableProgress;
    private float roundProgressAlpha;
    float roundSeekbarOutAlpha;
    float roundSeekbarOutProgress;
    int roundSeekbarTouched;
    private float roundToPauseProgress;
    private float roundToPauseProgress2;
    private RoundVideoPlayingDrawable roundVideoPlayingDrawable;
    private Path sPath;
    private boolean scheduledInvalidate;
    private Rect scrollRect;
    private SeekBar seekBar;
    private SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;
    private SeekBarWaveform seekBarWaveform;
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
    private int widthBeforeNewTimeLine;
    private int widthForButtons;
    private boolean willRemoved;

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

            public static boolean $default$needPlayMessage(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
                return false;
            }

            public static void $default$needReloadPolls(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static void $default$needShowPremiumFeatures(ChatMessageCellDelegate chatMessageCellDelegate, String str) {
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

        boolean needPlayMessage(MessageObject messageObject);

        void needReloadPolls();

        void needShowPremiumFeatures(String str);

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
        this.ALPHA_PROPERTY_WORKAROUND = Build.VERSION.SDK_INT == 28;
        this.alphaInternal = 1.0f;
        this.transitionParams = new TransitionParams();
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
        this.ANIMATION_OFFSET_X = new Property<ChatMessageCell, Float>(this, Float.class, "animationOffsetX") { // from class: org.telegram.ui.Cells.ChatMessageCell.7
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
            this.pollAvatarDrawables[i].setTextSize(AndroidUtilities.dp(6.0f));
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
            this.commentAvatarDrawables[i].setTextSize(AndroidUtilities.dp(8.0f));
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

    /* JADX WARN: Removed duplicated region for block: B:218:0x00ff A[Catch: Exception -> 0x02b1, TryCatch #1 {Exception -> 0x02b1, blocks: (B:189:0x0077, B:191:0x008c, B:193:0x0092, B:195:0x00b4, B:197:0x00bf, B:199:0x00cf, B:205:0x00e0, B:208:0x00ec, B:210:0x00ef, B:212:0x00f5, B:218:0x00ff, B:220:0x0105, B:222:0x010b, B:224:0x0111, B:226:0x0115, B:278:0x0273, B:228:0x0119, B:229:0x0126, B:231:0x012a, B:233:0x0132, B:237:0x0159, B:277:0x0268, B:276:0x0265, B:280:0x0277, B:282:0x027d, B:284:0x0283, B:286:0x028c, B:288:0x0292, B:289:0x0298, B:291:0x02a2, B:207:0x00e3, B:203:0x00d5, B:238:0x0164, B:240:0x0196, B:241:0x0198, B:243:0x01a2, B:245:0x01ae, B:248:0x01c5, B:250:0x01c8, B:253:0x01d3, B:256:0x01f6, B:246:0x01b9, B:257:0x01f9, B:259:0x01ff, B:261:0x0203, B:263:0x020f, B:266:0x022e, B:268:0x0231, B:271:0x023c, B:264:0x021e), top: B:299:0x0077, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:222:0x010b A[Catch: Exception -> 0x02b1, TryCatch #1 {Exception -> 0x02b1, blocks: (B:189:0x0077, B:191:0x008c, B:193:0x0092, B:195:0x00b4, B:197:0x00bf, B:199:0x00cf, B:205:0x00e0, B:208:0x00ec, B:210:0x00ef, B:212:0x00f5, B:218:0x00ff, B:220:0x0105, B:222:0x010b, B:224:0x0111, B:226:0x0115, B:278:0x0273, B:228:0x0119, B:229:0x0126, B:231:0x012a, B:233:0x0132, B:237:0x0159, B:277:0x0268, B:276:0x0265, B:280:0x0277, B:282:0x027d, B:284:0x0283, B:286:0x028c, B:288:0x0292, B:289:0x0298, B:291:0x02a2, B:207:0x00e3, B:203:0x00d5, B:238:0x0164, B:240:0x0196, B:241:0x0198, B:243:0x01a2, B:245:0x01ae, B:248:0x01c5, B:250:0x01c8, B:253:0x01d3, B:256:0x01f6, B:246:0x01b9, B:257:0x01f9, B:259:0x01ff, B:261:0x0203, B:263:0x020f, B:266:0x022e, B:268:0x0231, B:271:0x023c, B:264:0x021e), top: B:299:0x0077, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:280:0x0277 A[Catch: Exception -> 0x02b1, TryCatch #1 {Exception -> 0x02b1, blocks: (B:189:0x0077, B:191:0x008c, B:193:0x0092, B:195:0x00b4, B:197:0x00bf, B:199:0x00cf, B:205:0x00e0, B:208:0x00ec, B:210:0x00ef, B:212:0x00f5, B:218:0x00ff, B:220:0x0105, B:222:0x010b, B:224:0x0111, B:226:0x0115, B:278:0x0273, B:228:0x0119, B:229:0x0126, B:231:0x012a, B:233:0x0132, B:237:0x0159, B:277:0x0268, B:276:0x0265, B:280:0x0277, B:282:0x027d, B:284:0x0283, B:286:0x028c, B:288:0x0292, B:289:0x0298, B:291:0x02a2, B:207:0x00e3, B:203:0x00d5, B:238:0x0164, B:240:0x0196, B:241:0x0198, B:243:0x01a2, B:245:0x01ae, B:248:0x01c5, B:250:0x01c8, B:253:0x01d3, B:256:0x01f6, B:246:0x01b9, B:257:0x01f9, B:259:0x01ff, B:261:0x0203, B:263:0x020f, B:266:0x022e, B:268:0x0231, B:271:0x023c, B:264:0x021e), top: B:299:0x0077, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkTextBlockMotionEvent(android.view.MotionEvent r19) {
        /*
            Method dump skipped, instructions count: 698
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
        return this.useTranscribeButton && (transcribeButton = this.transcribeButton) != null && transcribeButton.onTouch(motionEvent.getAction(), motionEvent.getX() - this.transcribeX, motionEvent.getY() - this.transcribeY);
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

    /* JADX WARN: Removed duplicated region for block: B:176:0x0051  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:241:0x0199  */
    /* JADX WARN: Removed duplicated region for block: B:248:0x01c0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkPhotoImageMotionEvent(android.view.MotionEvent r9) {
        /*
            Method dump skipped, instructions count: 592
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkPhotoImageMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:180:0x00da, code lost:
        if (r3 <= (r0 + r6)) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x00dc, code lost:
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:195:0x0114, code lost:
        if (r3 <= (r0 + r6)) goto L54;
     */
    /* JADX WARN: Removed duplicated region for block: B:168:0x00c5  */
    /* JADX WARN: Removed duplicated region for block: B:200:0x011e  */
    /* JADX WARN: Removed duplicated region for block: B:206:0x012e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkAudioMotionEvent(android.view.MotionEvent r13) {
        /*
            Method dump skipped, instructions count: 398
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkAudioMotionEvent(android.view.MotionEvent):boolean");
    }

    public boolean checkSpoilersMotionEvent(MotionEvent motionEvent) {
        int i;
        MessageObject.GroupedMessages groupedMessages;
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
                View childAt = viewGroup.getChildAt(i2);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                    if (currentMessagesGroup != null && currentMessagesGroup.groupId == this.currentMessagesGroup.groupId) {
                        int i3 = currentPosition.flags;
                        if ((i3 & 8) != 0 && (i3 & 1) != 0 && chatMessageCell != this) {
                            motionEvent.offsetLocation(getLeft() - chatMessageCell.getLeft(), getTop() - chatMessageCell.getTop());
                            boolean checkSpoilersMotionEvent = chatMessageCell.checkSpoilersMotionEvent(motionEvent);
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
            int i4 = this.textX;
            if (x >= i4 && y >= (i = this.textY)) {
                MessageObject messageObject = this.currentMessageObject;
                if (x <= i4 + messageObject.textWidth && y <= i + messageObject.textHeight) {
                    ArrayList<MessageObject.TextLayoutBlock> arrayList = messageObject.textLayoutBlocks;
                    for (int i5 = 0; i5 < arrayList.size() && arrayList.get(i5).textYOffset <= y; i5++) {
                        MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i5);
                        int i6 = textLayoutBlock.isRtl() ? (int) this.currentMessageObject.textXOffset : 0;
                        for (SpoilerEffect spoilerEffect : textLayoutBlock.spoilers) {
                            if (spoilerEffect.getBounds().contains((x - this.textX) + i6, (int) ((y - this.textY) - textLayoutBlock.textYOffset))) {
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
            this.spoilerPressed.setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda2
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
                        int i7 = next2.isRtl() ? (int) this.currentMessageObject.textXOffset : 0;
                        for (SpoilerEffect spoilerEffect6 : next2.spoilers) {
                            spoilerEffect6.startRipple((x - this.textX) + i7, (y - next2.textYOffset) - this.textY, sqrt);
                        }
                    }
                }
            }
            if (getParent() instanceof RecyclerListView) {
                ViewGroup viewGroup2 = (ViewGroup) getParent();
                for (int i8 = 0; i8 < viewGroup2.getChildCount(); i8++) {
                    View childAt2 = viewGroup2.getChildAt(i8);
                    if (childAt2 instanceof ChatMessageCell) {
                        final ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt2;
                        if (chatMessageCell2.getMessageObject() != null && chatMessageCell2.getMessageObject().getReplyMsgId() == getMessageObject().getId() && !chatMessageCell2.replySpoilers.isEmpty()) {
                            chatMessageCell2.replySpoilers.get(0).setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda5
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
        post(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda3
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
                    postDelayed(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda4
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

    /* JADX WARN: Code restructure failed: missing block: B:831:0x05cc, code lost:
        if (r4 > (r0 + org.telegram.messenger.AndroidUtilities.dp(32 + ((r19.drawSideButton != 3 || r19.commentLayout == null) ? 0 : 18)))) goto L435;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r20) {
        /*
            Method dump skipped, instructions count: 1500
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onTouchEvent(android.view.MotionEvent):boolean");
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
                    this.delegate.needPlayMessage(this.currentMessageObject);
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
        if (runnable == null) {
            return;
        }
        runnable.run();
        this.unregisterFlagSecure = null;
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
        if (messageObject4 == null || messageObject4.type != 20 || this.unlockLayout == null) {
            return;
        }
        invalidate();
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

    /* JADX WARN: Can't wrap try/catch for region: R(167:1|(1:4284)|7|(1:9)(1:4283)|10|(1:12)(1:4282)|13|(1:15)(1:4281)|16|(1:20)(1:4280)|21|(27:25|(2:27|(1:33)(1:35))|36|37|(1:45)(1:4278)|46|(1:48)(1:4265)|(1:52)|53|(1:4264)|(1:77)|78|(9:82|(1:84)(1:4262)|(1:90)(1:4261)|(1:94)|95|(3:99|(1:101)(1:4259)|102)(1:4260)|103|(5:107|(1:111)|112|(1:116)(1:118)|117)|(3:135|(1:(2:147|148)(2:137|(1:139)(1:140)))|(3:142|(1:144)(1:146)|145)))(1:4263)|(4:151|(1:153)(1:158)|154|(1:156)(1:157))|159|(1:173)(70:231|(2:4256|(1:4258))(2:233|234)|235|(5:239|(1:245)(1:4252)|246|(1:252)(1:4251)|253)(1:4253)|254|(1:264)(1:4250)|265|(1:267)(1:4249)|268|(1:274)(1:4248)|275|(1:287)(1:4247)|288|(1:296)(1:4246)|297|(1:299)(1:4245)|300|(5:4222|(1:4230)(1:4244)|4231|(1:4243)|4234)(2:302|303)|304|(1:306)|307|(2:4207|(2:4208|(1:4217)(3:4210|(2:4212|4213)(2:4215|4216)|4214)))(0)|309|310|(1:4204)|314|(1:4203)|316|317|(1:321)(1:4198)|322|(1:324)|325|(1:4197)|328|329|(2:4154|(1:4158)(5:4159|(1:4161)(1:4194)|(3:4169|(6:4172|(2:4174|(2:4176|(1:4179)(1:4181)))(1:4184)|4183|(1:4182)(1:4179)|4181|4170)|4185)(0)|4186|(1:4188)(3:4189|(1:4191)(1:4193)|4192)))|(2:334|(1:340))|341|(1:345)(1:4153)|346|(2:4064|(11:4068|(1:4070)|4071|(4:4073|(1:(1:4076)(1:4133))(1:(1:4135)(1:4136))|4077|(5:4082|(2:4083|(1:4110)(2:(1:(1:4090)(2:4087|4088))(8:4091|(2:4093|(1:4095)(6:4108|(1:(1:4099)(1:4106))(1:4107)|4100|(1:4102)(1:4105)|4103|4104))(1:4109)|4096|(0)(0)|4100|(0)(0)|4103|4104)|4089))|4111|(3:4120|(1:4122)(1:4124)|4123)(3:4113|4114|(1:4116))|4117)(2:4125|(2:4127|(2:4128|(1:4131)(1:4130)))(0)))(1:4137)|4132|4111|(5:4118|4120|(0)(0)|4123|4117)|4113|4114|(0)|4117)(5:4138|(1:4148)(2:4140|4141)|4142|(1:4144)(1:4146)|4145))(2:353|354)|355|(3:357|(2:359|(1:(2:362|(2:364|(30:366|(1:372)(1:2216)|373|(3:377|(1:387)(1:385)|386)|392|(1:394)(1:2215)|395|(1:2214)(1:397)|398|(2:400|(3:402|(1:404)(1:2199)|405)(3:2200|(1:2204)(1:2206)|2205))(1:2207)|406|(15:408|(2:410|(2:412|(78:414|(3:416|(2:418|(77:430|(1:432)(1:1709)|433|434|(1:(1:437))(6:1698|(1:1702)|1703|(1:1705)|1706|(1:1708))|438|(56:442|(2:446|(1:448))(1:1696)|449|450|(1:452)|453|(4:455|(1:457)|458|(2:(1:463)(3:1669|(4:1672|(3:1674|(1:1679)(2:1676|1677)|1678)|1680|1681)|1671)|464)(3:1682|(1:1686)(3:1687|(1:1689)|1671)|464))(3:1690|(1:1692)(1:1694)|1693)|465|(1:469)|470|(2:474|(1:478)(1:479))|(1:491)|492|(1:496)|(5:1646|(3:1649|(1:1653)(3:1655|(2:1657|(2:1659|1660)(3:1662|(1:1664)|1665))(1:1666)|1661)|1647)|1668|1660|1661)|(1:1641)|501|(1:505)|506|(1:512)|513|(1:515)(4:1632|(3:1635|(1:1638)(1:1637)|1633)|1640|1639)|516|(1:518)|519|(1:521)(1:1631)|522|(1:524)(1:1630)|525|(1:527)|528|(3:530|(3:534|(1:536)(1:538)|537)|539)(1:1629)|540|(8:542|(1:544)(1:1459)|545|(3:547|(2:549|(1:551))|(4:554|555|(1:557)|558))|1458|555|(0)|558)(13:1460|(4:1466|(4:1469|(2:1471|(1:(2:1474|1475)(1:1477))(4:1479|(1:1481)(1:1484)|1482|1483))(4:1485|(1:1487)(1:1490)|1488|1489)|1476|1467)|1491|1478)|1496|(1:1498)|1499|(1:1501)(3:1623|(2:1624|(1:1627)(1:1626))|1628)|1502|(1:1506)(2:1614|(1:1618)(2:1619|(1:1621)(1:1622)))|1507|(1:1509)(1:1613)|1510|(4:1517|(7:1519|(11:1535|(4:1574|(5:1610|1582|(4:1585|(2:1587|(1:(2:1590|1591)(1:1593))(4:1595|(1:1597)(1:1600)|1598|1599))(4:1601|(1:1603)(1:1606)|1604|1605)|1592|1583)|1607|1594)|1577|(1:1580)(1:1579))(1:1537)|1538|(1:1542)(2:1565|(1:1569)(2:1570|(1:1572)(1:1573)))|1543|(1:1553)|1554|(1:1556)|1557|(1:1564)|1563)|1523|(2:1525|(1:1527)(3:1528|1529|1530))|1531|1532|1530)|1611|1612)|1516)|559|(1:561)(7:1388|1389|1390|(2:1454|1455)(1:1392)|1393|(1:1395)(11:(2:1398|1399)(5:1432|1433|(1:(2:1452|1453)(6:1435|1436|1437|1438|1439|(1:1441)(1:1442)))|1444|(1:1446)(1:1447))|1400|(1:1425)|1403|1404|1405|(3:1420|1411|1412)|1408|(2:1415|1416)|1411|1412)|1396)|562|(4:1356|(1:1358)(1:1387)|1359|(6:1361|(1:1363)|1364|(1:1366)(1:1386)|1367|(2:1369|(3:1373|(2:1375|(1:1377))|1378)(1:(4:1380|(1:1382)|1383|(1:1385))))))|564|565|(3:1352|(1:1354)|1355)|569|(2:571|(1:573))|574|(3:1336|(2:1344|(1:1346))(1:1338)|1339)(3:576|577|(2:581|(1:583)(1:1327)))|584|(1:590)(1:1326)|591|(1:1325)(1:593)|594|(1:599)|600|(2:1312|(1:1314)(5:1315|(1:1319)(1:1321)|1320|603|(4:605|(2:607|(3:609|(3:613|(3:618|(1:620)(1:1148)|621)(4:(1:1152)|1153|(1:1155)(1:1157)|1156)|622)(7:1158|(2:1160|(1:1162)(1:1249))(1:1250)|1163|(1:1167)(1:1248)|1168|(2:1173|(4:1200|1201|(1:1203)|(5:1210|(1:1212)|1213|(1:1215)(1:1217)|1216)(2:1218|(1:1223)(1:1224)))(2:1175|(1:(5:1182|(1:1184)|1185|(1:1187)(1:1190)|1188)(1:1191))(3:1192|(1:1194)(1:1196)|1195)))(3:1231|(1:1237)(2:(1:1242)|1243)|1238)|1189)|623)(2:1251|(2:1253|(1:1255)(3:1257|(1:1259)(1:1283)|(2:1273|(1:1277)(3:1278|(1:1280)(1:1282)|1281))(4:1261|1262|(1:1264)(1:1266)|1265)))(1:1284)))(3:1285|(1:1287)(1:1289)|1288)|1256|623)(3:1290|(1:1296)(3:1297|(2:(1:1301)|1302)|1256)|623)))|602|603|(0)(0))(1:1697)|1695|450|(0)|453|(0)(0)|465|(2:467|469)|470|(4:472|474|(2:476|478)|479)|(4:481|483|487|491)|492|(2:494|496)|(0)|1642|1644|1646|(1:1647)|1668|1660|1661|(0)|1641|501|(2:503|505)|506|(3:508|510|512)|513|(0)(0)|516|(0)|519|(0)(0)|522|(0)(0)|525|(0)|528|(0)(0)|540|(0)(0)|559|(0)(0)|562|(0)|564|565|(1:567)(4:1347|1352|(0)|1355)|569|(0)|574|(17:1336|(18:1340|1342|1344|(0)|1339|584|(13:586|588|590|591|(9:1323|1325|594|(2:597|599)|600|(4:1304|1306|1312|(0)(0))|602|603|(0)(0))|593|594|(0)|600|(0)|602|603|(0)(0))|1326|591|(0)|593|594|(0)|600|(0)|602|603|(0)(0))|1338|1339|584|(0)|1326|591|(0)|593|594|(0)|600|(0)|602|603|(0)(0))|576|577|(16:579|581|(0)(0)|584|(0)|1326|591|(0)|593|594|(0)|600|(0)|602|603|(0)(0))|1328|581|(0)(0)|584|(0)|1326|591|(0)|593|594|(0)|600|(0)|602|603|(0)(0))(1:1710))(1:1713)|1711)(1:1714)|1712|434|(0)(0)|438|(74:440|442|(73:444|446|(0)|1695|450|(0)|453|(0)(0)|465|(0)|470|(0)|(0)|492|(0)|(0)|1642|1644|1646|(1:1647)|1668|1660|1661|(0)|1641|501|(0)|506|(0)|513|(0)(0)|516|(0)|519|(0)(0)|522|(0)(0)|525|(0)|528|(0)(0)|540|(0)(0)|559|(0)(0)|562|(0)|564|565|(0)(0)|569|(0)|574|(0)|576|577|(0)|1328|581|(0)(0)|584|(0)|1326|591|(0)|593|594|(0)|600|(0)|602|603|(0)(0))|1696|449|450|(0)|453|(0)(0)|465|(0)|470|(0)|(0)|492|(0)|(0)|1642|1644|1646|(1:1647)|1668|1660|1661|(0)|1641|501|(0)|506|(0)|513|(0)(0)|516|(0)|519|(0)(0)|522|(0)(0)|525|(0)|528|(0)(0)|540|(0)(0)|559|(0)(0)|562|(0)|564|565|(0)(0)|569|(0)|574|(0)|576|577|(0)|1328|581|(0)(0)|584|(0)|1326|591|(0)|593|594|(0)|600|(0)|602|603|(0)(0))|1697|1695|450|(0)|453|(0)(0)|465|(0)|470|(0)|(0)|492|(0)|(0)|1642|1644|1646|(1:1647)|1668|1660|1661|(0)|1641|501|(0)|506|(0)|513|(0)(0)|516|(0)|519|(0)(0)|522|(0)(0)|525|(0)|528|(0)(0)|540|(0)(0)|559|(0)(0)|562|(0)|564|565|(0)(0)|569|(0)|574|(0)|576|577|(0)|1328|581|(0)(0)|584|(0)|1326|591|(0)|593|594|(0)|600|(0)|602|603|(0)(0))(26:1715|(1:1717)(1:1881)|1718|(3:1867|(4:1870|(2:1872|(1:1874)(2:1875|1876))(2:1878|1879)|1877|1868)|1880)|1720|1721|(1:1866)|1725|(1:1727)|1728|(1:1730)(1:1863)|1731|(3:(1:1736)|1737|(1:1739)(1:1861))(1:1862)|1740|(1:1744)(1:1860)|1745|(1:1747)(1:1859)|1748|(4:1750|(3:1752|(3:1825|(1:1827)(1:1829)|1828)(2:1754|1755)|1756)(2:1830|(1:1834)(6:1835|(3:1837|(1:1839)(1:1845)|1840)(1:1846)|1841|(1:1843)|1844|1758))|1757|1758)(3:1847|(3:1853|(1:1855)(1:1857)|1856)|1858)|1759|(1:1763)|1764|(1:1766)(2:1782|(2:1784|(2:1786|(2:1788|(1:1790)(5:1791|(1:1793)(1:1799)|1794|(1:1796)(1:1798)|1797))(5:1800|(1:1802)(1:1808)|1803|(1:1805)(1:1807)|1806))(3:1809|(1:1811)(1:1813)|1812))(5:1814|(1:1816)|1817|(1:1819)(1:1821)|1820))|1767|(1:1769)(4:1771|(1:1777)(1:1781)|1778|(1:1780))|1770))(5:1882|(1:1884)(1:1895)|1885|(1:1887)(3:1890|(1:1892)(1:1894)|1893)|1888))(12:1896|(11:1900|(1:(1:1903))(1:2035)|1904|(2:1906|(5:1908|(3:1910|(1:1912)(1:1960)|1913)(3:1961|(1:1963)(1:1965)|1964)|1914|(1:1916)|1917)(7:1966|(3:1968|(1:1970)(1:1995)|1971)(3:1996|(1:1998)(1:2000)|1999)|1972|(1:1974)|1975|(1:1977)(6:1979|(1:1981)(1:1994)|(3:1983|(1:1985)(1:1989)|1986)|(1:1991)(1:1993)|1992|1988)|1978))(11:2001|(3:2003|(1:2005)(1:2029)|2006)(3:2030|(1:2032)(1:2034)|2033)|2007|(1:2009)|2010|(1:2012)(1:2028)|2013|(2:2015|(1:2017)(3:2023|(1:2025)|2026))(1:2027)|2018|(1:2020)(1:2022)|2021)|1918|(1:1920)(2:1949|(3:1951|(2:1953|(1:1955)(1:1957))(1:1958)|1956)(1:1959))|1921|(1:(4:1924|(1:1940)|1928|(1:1930))(2:1941|(3:1943|(1:1945)(1:1947)|1946)))(1:1948)|1931|(3:1935|(1:1937)|1938)|1939)|2036|1904|(0)(0)|1918|(0)(0)|1921|(0)(0)|1931|(4:1933|1935|(0)|1938)|1939)|624|(2:632|(1:634))(3:1138|(1:1147)|1140)|635|(1:643)|644|(1:646)|647|(5:651|(1:653)(1:1136)|654|(1:656)|657)(1:1137)|658|(1:660)(4:1129|(1:1131)|1132|(1:1134)(1:1135))|661|(2:663|(4:667|(7:669|(1:671)(1:681)|672|(1:674)(1:680)|675|(1:677)(1:679)|678)|682|(3:684|(1:686)(1:688)|687)))|689)(20:2037|(3:2039|(1:2041)(1:2193)|2042)(3:2194|(1:2196)(1:2198)|2197)|2043|(1:2045)|2046|(4:2050|2051|(1:2053)(1:2056)|2054)|2059|(1:2061)(4:2182|(3:2184|(2:2186|2187)(2:2189|2190)|2188)|2191|2192)|2062|(4:2064|(3:2066|(2:2068|2069)(2:2071|2072)|2070)|2073|2074)|2075|(4:2077|(3:2079|(1:2084)(2:2081|2082)|2083)|2085|2086)|2087|(2:2089|(1:2093))|(1:2097)|2098|(4:2100|(1:2180)|2103|(2:2107|(3:2111|(2:2113|(1:2115))(1:2117)|2116)))(1:2181)|2118|(11:2122|(1:2124)|2125|(1:2133)(2:2159|(1:2167)(2:2170|(1:2176)))|2134|(1:2138)|2139|(1:2153)|2154|(1:2156)|2157)(1:2177)|2158)|1889|624|(16:626|628|632|(0)|635|(4:637|639|641|643)|644|(0)|647|(11:649|651|(0)(0)|654|(0)|657|658|(0)(0)|661|(0)|689)|1137|658|(0)(0)|661|(0)|689)|1138|(2:1141|1147)|1140|635|(0)|644|(0)|647|(0)|1137|658|(0)(0)|661|(0)|689)(51:2217|(1:2219)|2220|(1:2222)(1:2519)|2223|(1:2225)(1:2518)|2226|(1:2228)|2229|(1:(2:2516|2517)(2:2231|(1:2233)(2:2234|2235)))|2236|(2:2238|(2:2240|(1:2242)(1:2510))(2:2511|(1:2513)(1:2514)))(1:2515)|2243|(1:(1:2248)(1:(1:2252)(1:2253)))|2254|(1:2256)(1:2509)|2257|(6:2259|(1:2263)(1:2286)|2264|(3:2266|(1:2268)(1:2280)|2269)(3:2281|(1:2283)(1:2285)|2284)|2270|(3:2274|(1:2276)(1:2278)|2277)(1:2279))|2287|(2:2293|2294)|2295|(1:2297)|2298|(2:2300|(2:2302|(2:2303|(1:2306)(1:2305)))(0))(2:2478|(2:2479|(1:2497)(3:2481|(4:2484|(1:2486)(1:2490)|2487|2488)(2:2491|(1:2494)(2:2495|2496))|2489)))|2307|(1:2313)|2314|(1:2320)(1:2477)|2321|(1:2323)(5:2457|(3:2459|(1:2465)(1:2467)|2466)|2468|(3:2472|(1:2474)|2475)|2476)|2324|(1:2326)(1:2456)|2327|(1:2329)(1:2455)|2330|(1:2332)(1:2454)|2333|(7:2335|(2:2337|(1:(1:2360)(2:2339|(1:2341)(5:2342|2343|(2:2347|2348)(1:2359)|(2:2350|(1:2354))(1:2356)|2355))))(0)|(2:2362|(1:(1:2369)(2:2364|(1:2366)(2:2367|2368))))(0)|(1:2375)(1:2383)|2376|(2:2378|2379)(2:2381|2382)|2380)|2384|(3:2387|(1:2389)|2390)|2391|(3:2393|(2:2395|2396)(2:2398|2399)|2397)|2400|2401|(1:2405)(2:2449|(1:2453))|2406|(1:2408)|2409|(7:2425|(2:(2:2428|(2:2430|2431)(2:2433|2434))(2:2435|2436)|2432)|2437|2438|(1:2440)(1:2448)|2441|(1:2447))|2413|(4:2417|(1:2419)(1:2423)|2420|(1:2422))))(13:2520|(1:2524)(11:2574|(1:2576)|2529|2530|(3:2532|(1:2534)(1:2567)|2535)(3:2568|(1:2570)(1:2572)|2571)|2536|(4:2542|(1:2544)|2545|(1:2547))|2548|(1:2550)|2551|(2:2553|(7:2555|(1:2557)|2558|(1:2560)|2561|(1:2565)|2566)))|2525|(9:2573|2530|(0)(0)|2536|(6:2538|2540|2542|(0)|2545|(0))|2548|(0)|2551|(0))|2529|2530|(0)(0)|2536|(0)|2548|(0)|2551|(0)))(10:2578|(1:2580)(1:2628)|2581|(1:2585)(1:2627)|2586|(3:2588|(1:2590)(1:2617)|2591)(3:2618|(1:2620)(1:2622)|2621)|2592|(1:2594)|2595|(4:2597|(1:2599)(1:2616)|2600|(9:2602|(1:2604)(1:2615)|2605|(1:2607)|2608|(1:2610)|2611|(1:2613)|2614))))(29:2629|(1:2633)(1:2728)|2634|(1:2636)(1:2723)|2637|(3:2639|(1:2641)(1:2717)|2642)(3:2718|(1:2720)(1:2722)|2721)|2643|(1:2645)(1:2716)|2646|(1:2648)|(2:2650|(16:2654|2655|(3:2657|(1:2659)(1:2711)|2660)(1:2712)|2661|(1:(1:2666)(2:2706|(1:2708)(1:2709)))(1:2710)|2667|(1:2671)|2672|(1:2680)(2:2701|(1:2705))|2681|(1:2683)|2684|(2:2686|(2:2688|(1:2690)))(1:2700)|2691|(3:2695|(1:2697)|2698)|2699)(1:2713))(1:2715)|2714|2655|(0)(0)|2661|(0)(0)|2667|(2:2669|2671)|2672|(10:2674|2676|2680|2681|(0)|2684|(0)(0)|2691|(4:2693|2695|(0)|2698)|2699)|2701|(2:2703|2705)|2681|(0)|2684|(0)(0)|2691|(0)|2699))(10:2729|(3:2731|(1:2733)(1:2770)|2734)(3:2771|(1:2773)(1:2775)|2774)|2735|(1:2737)|2738|(1:(2:2741|(2:2743|(1:2745)(1:2752))(2:2753|(1:2755)(1:2756)))(2:2757|(1:2759)(1:2760)))(1:(2:2762|(1:2764)(1:2765))(2:2766|(1:2768)(1:2769)))|2746|(1:2748)|2749|(1:2751))|2424)(63:2776|(1:2778)(1:4063)|2779|(5:2781|(1:2783)(1:4057)|2784|(1:2796)(1:4056)|2797)(3:4058|(1:4060)(1:4062)|4061)|2798|(3:2800|(1:2802)(1:2804)|2803)|2805|(1:2807)|2808|(1:2812)(1:4055)|2813|(1:2819)(1:4054)|2820|(1:2824)(1:4053)|2825|(1:2827)(1:4052)|2828|(1:2836)(1:4051)|2837|(1:2839)(1:4050)|2840|(1:(2:2843|(43:2855|(6:2858|(2:2860|(2:2866|2865)(1:2862))(1:2867)|2863|2864|2865|2856)|2868|2869|2870|(2:2876|(16:2878|(1:2881)(1:3923)|2882|(3:2884|(1:2886)(1:2888)|2887)|2889|(1:2893)(1:3922)|2894|(2:2896|(3:2898|(1:2900)|2901))|2902|(1:2904)|2905|(1:2909)|2910|2911|(1:2917)(20:2919|(1:(1:2922)(1:3918))(1:(1:3920)(1:3921))|2923|(1:2925)|2926|(2:2928|(3:2930|(1:2932)(1:3839)|2933)(3:3840|(1:3842)(1:3844)|3843))(19:3845|(17:3917|(15:3914|(10:3911|3855|(1:3857)(1:(1:3907)(1:3908))|3858|(1:3863)|3864|(1:3882)(1:3905)|(1:3896)(1:3904)|(1:3901)(1:3903)|3902)|3854|3855|(0)(0)|3858|(2:3861|3863)|3864|(14:3866|3868|3870|3872|3874|3876|3878|3880|3882|(5:3884|3896|(3:3899|3901|3902)|3903|3902)|3904|(0)|3903|3902)|3905|(0)|3904|(0)|3903|3902)|3851|(1:3853)(14:3909|3911|3855|(0)(0)|3858|(0)|3864|(0)|3905|(0)|3904|(0)|3903|3902)|3854|3855|(0)(0)|3858|(0)|3864|(0)|3905|(0)|3904|(0)|3903|3902)|3848|(1:3850)(16:3912|3914|(0)(0)|3854|3855|(0)(0)|3858|(0)|3864|(0)|3905|(0)|3904|(0)|3903|3902)|3851|(0)(0)|3854|3855|(0)(0)|3858|(0)|3864|(0)|3905|(0)|3904|(0)|3903|3902)|2934|(2:2936|(2:2938|(2:2940|(1:2942)(1:3835))(1:3836))(1:3837))(1:3838)|2943|(1:2945)(1:3834)|2946|(1:2949)|(1:2951)(18:3742|3743|3744|3745|3746|(3:3821|3822|3823)(2:3748|3749)|3750|3751|3752|3753|3754|(1:3756)(1:3814)|3757|3758|(11:3762|3763|3764|3765|(8:(8:(1:3794)(1:3796)|3795|3770|3771|3772|3773|3774|3775)(1:3768)|3769|3770|3771|3772|3773|3774|3775)(1:3798)|(3:(1:3782)(2:3783|(1:3785))|3779|3780)(1:3777)|3778|3779|3780|3759|3760)|3802|3803|3804)|(1:2953)(14:3662|3663|3664|(1:3666)(1:3740)|3667|(1:3669)(2:3738|3739)|3670|3671|3672|(12:3676|(1:3678)(1:3728)|3679|3680|(3:3682|3683|3684)(2:3726|3727)|(8:(8:(1:3718)(1:3720)|3719|3689|3690|3691|3692|3693|3694)(1:3687)|3688|3689|3690|3691|3692|3693|3694)(1:3722)|(3:(1:3701)(2:3702|(1:3704))|3698|3699)(1:3696)|3697|3698|3699|3673|3674)|3729|3730|(1:3712)|3713)|(17:2956|2957|(1:2959)(1:3658)|2960|2961|2962|2963|2964|2965|2966|(6:3652|2972|(1:2974)(2:3649|3650)|2975|2976|2977)|2971|2972|(0)(0)|2975|2976|2977)(1:3661)|(18:3555|3556|3557|(1:3559)(1:3643)|(13:3642|3563|(10:3635|(1:3637)(1:3639)|3638|3570|(5:3573|3574|(4:3579|(1:3581)(1:3584)|3582|3583)(2:3576|3577)|3578|3571)|3587|3588|(9:3591|(1:3628)|(1:(3:3596|3597|3598)(1:3623))(1:3624)|(4:(1:3616)(1:(1:3618)(2:3619|(1:3621)))|3602|(4:(1:3608)(1:3614)|(1:3610)(1:3613)|3611|3612)(2:3604|3605)|3606)(1:3600)|3601|3602|(0)(0)|3606|3589)|3629|3630)|3566|(1:3568)(1:3632)|3569|3570|(1:3571)|3587|3588|(1:3589)|3629|3630)|3562|3563|(1:3565)(11:3633|3635|(0)(0)|3638|3570|(1:3571)|3587|3588|(1:3589)|3629|3630)|3566|(0)(0)|3569|3570|(1:3571)|3587|3588|(1:3589)|3629|3630)|2979|(1:2985)(1:3554)|(1:2987)|(8:(1:(1:2991)(6:3316|(1:3318)(1:3320)|3319|2995|(8:3001|(1:3008)(26:(1:3039)(1:3314)|3040|(1:3042)|3043|(2:3045|(1:3049)(2:3286|(2:3292|(1:3294))(3:3295|(1:3297)(1:3299)|3298)))(1:3300)|3050|(1:3054)|3055|(1:3057)(1:3285)|3058|(1:(1:3061)(1:3281))(2:3282|(1:3284))|3062|(1:3064)|(2:3068|(9:3072|(2:3074|(1:3076)(1:3266))(1:3267)|3077|(2:3083|(1:3085))(2:3263|(5:3265|3087|(1:3089)|3090|(1:3092)))|3086|3087|(0)|3090|(0))(4:3268|(3:3272|(2:3274|(1:3276)(2:3277|3278))|3279)|3270|3271))(1:3280)|3093|(1:3095)(3:3259|(1:3261)|3262)|3096|(1:3100)(1:3258)|3101|(2:3103|(2:3105|(2:3107|(3:3109|(2:3111|(4:3115|(2:3154|(2:3156|(1:3160)(2:3162|3119))(1:3163))(1:3117)|3118|3119)(10:3164|(3:3166|(1:3168)(1:3204)|3169)(1:3205)|3170|(1:3174)(1:3203)|3175|(1:3178)(2:3180|(7:3187|(1:3189)(1:3197)|3190|(1:3192)|3193|(1:3195)|3196)(3:3198|(1:3200)(1:3202)|3201))|3179|3121|(1:3126)(2:3128|(7:3130|3131|(1:3133)|3134|(2:3146|(2:3148|(1:3139)))|3137|(0)))|3127))(3:3206|(1:3214)(2:3216|(3:3220|(1:3224)(1:3226)|3225)(1:3227))|3215)|3161)(5:3228|(1:3232)|3233|(4:3239|3240|(1:3244)(1:3246)|3245)(2:3235|3236)|3237))(2:3249|(1:3251)(1:3252)))(2:3253|(1:3255)(1:3256)))(1:3257)|3120|3121|(3:3123|3126|3127)|3128|(0)|3127)|3009|(4:3011|(2:3013|(1:3015)(1:3027))(1:3028)|3016|(5:3018|(1:3020)(1:3026)|3021|(1:3023)(1:3025)|3024))|3029|(1:3036)|3031|3032)(1:3315)|3033))(14:3321|(1:3323)(2:3345|(11:3347|3325|(1:3328)(1:3344)|(1:3330)(1:3343)|3331|(1:3333)(1:3342)|3334|(1:3336)(1:3341)|3337|(1:3339)|3340))|3324|3325|(9:3328|(0)(0)|3331|(0)(0)|3334|(0)(0)|3337|(0)|3340)|3344|(0)(0)|3331|(0)(0)|3334|(0)(0)|3337|(0)|3340)|2992|2993|2994|2995|(41:2997|2999|3001|(10:3004|3006|3008|3009|(0)|3029|(2:3034|3036)|3031|3032|3033)|(35:3039|3040|(0)|3043|(0)(0)|3050|(2:3052|3054)|3055|(0)(0)|3058|(0)(0)|3062|(0)|(27:3066|3068|(32:3070|3072|(0)(0)|3077|(28:3079|3083|(0)|3086|3087|(0)|3090|(0)|3093|(0)(0)|3096|(17:3098|3100|3101|(0)(0)|3120|3121|(0)|3128|(0)|3127|3009|(0)|3029|(0)|3031|3032|3033)|3258|3101|(0)(0)|3120|3121|(0)|3128|(0)|3127|3009|(0)|3029|(0)|3031|3032|3033)|3263|(0)|3086|3087|(0)|3090|(0)|3093|(0)(0)|3096|(0)|3258|3101|(0)(0)|3120|3121|(0)|3128|(0)|3127|3009|(0)|3029|(0)|3031|3032|3033)|3268|(0)|3270|3271|3093|(0)(0)|3096|(0)|3258|3101|(0)(0)|3120|3121|(0)|3128|(0)|3127|3009|(0)|3029|(0)|3031|3032|3033)|3280|3093|(0)(0)|3096|(0)|3258|3101|(0)(0)|3120|3121|(0)|3128|(0)|3127|3009|(0)|3029|(0)|3031|3032|3033)|3302|3039|3040|(0)|3043|(0)(0)|3050|(0)|3055|(0)(0)|3058|(0)(0)|3062|(0)|(0)|3280|3093|(0)(0)|3096|(0)|3258|3101|(0)(0)|3120|3121|(0)|3128|(0)|3127|3009|(0)|3029|(0)|3031|3032|3033)|3315|3033)(7:3348|(2:3350|(3:3352|(3:3354|(2:3358|(2:3360|(9:3362|(1:3364)|3365|(2:3367|(2:3369|(2:3371|(1:3375))(1:3377))(6:3378|(1:3382)|3383|(1:3387)|3388|3389))(3:3391|(3:3393|(1:3395)(1:3398)|3396)(3:3399|(1:3401)(1:3403)|3402)|3397)|3390|2995|(0)|3315|3033)(5:3404|(1:3406)(1:3428)|3407|(4:3413|(2:3414|(1:3426)(2:3416|(1:3418)(2:3419|3420)))|3421|(1:3425))|3427))(3:3429|(4:3435|(2:3436|(1:3448)(2:3438|(1:3440)(2:3441|3442)))|3443|(1:3447))|3449))(3:3450|(4:3456|(2:3457|(1:3469)(2:3459|(1:3461)(2:3462|3463)))|3464|(1:3468))|3470)|3376)(13:3471|(1:3473)(1:3513)|3474|(1:3476)|3477|(1:3479)(1:3512)|3480|(1:3482)|(1:3484)|3485|(4:3495|(2:3496|(1:3511)(2:3498|(1:3500)(3:3501|3502|(1:3504)(1:3510))))|3505|(1:3509))|3487|3488)|2993)(9:3514|(1:3552)(1:3516)|3517|(1:3519)(1:3549)|3520|(1:(1:3523))(1:3548)|3524|(5:3530|(3:3533|(1:3537)(1:3539)|3531)|3546|3540|(1:3544))|3547))(1:3553)|2994|2995|(0)|3315|3033))|2918))|3924|2882|(0)|2889|(33:2891|2893|2894|(0)|2902|(0)|2905|(2:2907|2909)|2910|2911|(4:2913|2915|2917|2918)|2919|(0)(0)|2923|(0)|2926|(0)(0)|2934|(0)(0)|2943|(0)(0)|2946|(1:2949)|(0)(0)|(0)(0)|(23:2956|2957|(0)(0)|2960|2961|2962|2963|2964|2965|2966|(1:2968)|3652|2972|(0)(0)|2975|2976|2977|(0)|2979|(5:2981|2983|2985|(0)|(0)(0))|3554|(0)|(0)(0))|3661|(0)|2979|(0)|3554|(0)|(0)(0))|3922|2894|(0)|2902|(0)|2905|(0)|2910|2911|(0)|2919|(0)(0)|2923|(0)|2926|(0)(0)|2934|(0)(0)|2943|(0)(0)|2946|(0)|(0)(0)|(0)(0)|(0)|3661|(0)|2979|(0)|3554|(0)|(0)(0))))(2:3927|(2:3929|(2:3931|(2:3933|(2:3935|(2:3937|(2:3939|(2:3941|(8:3943|3944|3945|(1:3947)(1:4021)|3948|(5:4006|(1:4020)|4009|4010|(4:4012|4013|4014|(2:3952|(7:3958|(1:3979)|3961|(1:3975)|3964|(1:3971)|3967))(9:3980|(1:4005)|3983|(1:4001)|3986|(1:3997)|3989|(1:3991)(1:3993)|3992)))|3950|(0)(0)))(42:4023|(1:(2:4042|4043)(4:4025|(1:(1:4041)(2:4027|(1:4029)(2:4030|4031)))|4032|(2:4034|(1:4036)(2:4037|4038))(2:4039|4040)))|3926|2870|(4:2872|2874|2876|(0))|3924|2882|(0)|2889|(0)|3922|2894|(0)|2902|(0)|2905|(0)|2910|2911|(0)|2919|(0)(0)|2923|(0)|2926|(0)(0)|2934|(0)(0)|2943|(0)(0)|2946|(0)|(0)(0)|(0)(0)|(0)|3661|(0)|2979|(0)|3554|(0)|(0)(0)))(1:4044))(1:4045))(1:4046))(1:4047))(1:4048))(1:4049))|3925|3926|2870|(0)|3924|2882|(0)|2889|(0)|3922|2894|(0)|2902|(0)|2905|(0)|2910|2911|(0)|2919|(0)(0)|2923|(0)|2926|(0)(0)|2934|(0)(0)|2943|(0)(0)|2946|(0)|(0)(0)|(0)(0)|(0)|3661|(0)|2979|(0)|3554|(0)|(0)(0))|690|(4:1016|(10:1024|(1:1026)(1:1062)|1027|(3:1035|(1:1037)|1038)|1039|1040|(3:1049|(3:1051|(1:1053)|1054)(3:1056|(1:1058)|1059)|1055)(1:1042)|1043|(1:1045)(1:1048)|1046)|1063|(8:1067|1068|(4:1115|(5:1118|1119|(1:1124)(2:1121|1122)|1123|1116)|1125|1126)|1071|(2:1111|(12:1079|(1:1081)(1:1107)|1082|(1:1106)|1085|(1:1103)|1088|(1:1090)(1:1100)|1091|(1:1093)(2:1096|(2:1098|1095))|1094|1095)(1:1078))|1074|(1:1076)(1:1108)|(0)(0)))|696|(1:1011)|700|701|(19:956|957|958|959|(1:961)(1:1005)|962|963|964|965|966|(1:968)(1:999)|969|(4:972|(2:974|975)(2:977|(2:979|980)(2:981|982))|976|970)|983|(1:985)(1:997)|986|(1:996)|991|(2:993|(1:995)))(1:703)|704|(5:706|(1:708)(1:717)|709|(1:715)|716)|718|(1:720)(1:951)|721|(11:846|(1:848)(1:950)|849|(1:851)(1:949)|852|(4:(1:855)(1:861)|856|(1:858)(1:860)|859)|862|(1:868)(3:945|(1:947)|948)|869|(1:871)(4:873|(3:875|(3:877|(14:880|(1:882)(1:939)|(1:884)(1:938)|885|(6:937|892|(1:894)|895|(6:899|900|(7:907|(3:930|920|(5:923|(1:925)(1:926)|913|904|905)(1:922))|910|(2:914|(4:917|(1:919)|920|(0)(0))(1:916))(1:912)|913|904|905)(1:902)|903|904|905)|906)|888|(1:890)(1:934)|891|892|(0)|895|(1:933)(7:897|899|900|(0)(0)|903|904|905)|906|878)|940)(1:942)|941)|943|944)|872)(2:723|724)|725|(3:727|(1:729)(1:731)|730)|732|(1:736)(1:(2:830|(1:838))(1:839))|737|(2:739|(1:743)(2:744|(1:746)))|747|(1:749)|750|(1:752)(2:822|(2:824|(1:826)(1:827))(1:828))|753|(23:755|(1:757)(1:820)|758|(1:760)|761|(1:819)|767|(1:773)(1:818)|774|(1:817)|778|(1:780)(1:816)|781|(8:785|(1:787)|788|(1:790)|791|(1:793)|794|(8:796|797|(2:799|(1:801)(1:802))|803|(2:805|(1:807)(1:808))|809|(1:814)|813))|815|797|(0)|803|(0)|809|(1:811)|814|813)|821)|(6:175|(1:177)|178|(3:180|(1:182)(1:184)|183)|185|(1:187))|188|(6:190|(1:196)(1:205)|(1:198)(1:204)|199|(1:201)(1:203)|202)|206|(1:210)(1:230)|211|(1:219)|220|(1:226)|227|228)|4279|37|(152:39|41|43|45|46|(0)(0)|(2:50|52)|53|(1:55)|4264|(2:75|77)|78|(150:80|82|(0)(0)|(148:86|88|90|(2:92|94)|95|(145:97|99|(0)(0)|102|103|(7:105|107|(2:109|111)|112|(2:116|117)|118|117)|(10:121|123|125|127|129|131|133|135|(2:(0)(0)|139)|(0))|(4:151|(0)(0)|154|(0)(0))|159|(16:167|169|171|173|(0)|188|(0)|206|(8:208|210|211|(4:213|215|217|219)|220|(2:222|226)|227|228)|230|211|(0)|220|(0)|227|228)|231|(134:4254|4256|(0)|235|(136:237|239|(134:241|245|246|(131:248|252|253|254|(130:256|258|260|262|264|265|(0)(0)|268|(123:270|272|274|275|(122:277|279|281|283|287|288|(118:290|292|294|296|297|(0)(0)|300|(116:4218|4222|(115:4224|4226|4230|4231|(4:4235|4237|4239|4241)|4243|4234|304|(0)|307|(3:4205|4207|(3:4208|(0)(0)|4214))(0)|309|310|(0)|4204|314|(3:4199|4201|4203)|316|317|(96:319|321|322|(0)|325|(0)|4195|4197|328|329|(1:331)|4154|(80:4156|4158|(0)|341|(76:343|345|346|(70:349|4064|(72:4066|4068|(0)|4071|(0)(0)|4132|4111|(0)|4113|4114|(0)|4117|355|(0)(0)|690|(1:692)|1012|1014|1016|(12:1018|1020|1024|(0)(0)|1027|(6:1029|1031|1033|1035|(0)|1038)|1039|1040|(0)(0)|1043|(0)(0)|1046)|1063|(1:1065)|1067|1068|(1:1070)(5:1112|1115|(1:1116)|1125|1126)|1071|(1:1073)(45:1109|1111|(0)(0)|696|(1:698)|1009|1011|700|701|(56:952|954|956|957|958|959|(0)(0)|962|963|964|965|966|(0)(0)|969|(1:970)|983|(0)(0)|986|(0)|996|991|(0)|704|(0)|718|(0)(0)|721|(43:840|842|844|846|(0)(0)|849|(0)(0)|852|(0)|862|(32:864|866|868|869|(0)(0)|872|725|(0)|732|(23:734|736|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|945|(0)|948|869|(0)(0)|872|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4138|(64:4148|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4159|(0)(0)|(6:4163|4165|4167|4169|(1:4170)|4185)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4244|4231|(0)|4243|4234|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4248|275|(0)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4250|265|(0)(0)|268|(0)|4248|275|(0)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4251|253|254|(0)|4250|265|(0)(0)|268|(0)|4248|275|(0)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4252|246|(0)|4251|253|254|(0)|4250|265|(0)(0)|268|(0)|4248|275|(0)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4253|254|(0)|4250|265|(0)(0)|268|(0)|4248|275|(0)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|233|234|235|(0)|4253|254|(0)|4250|265|(0)(0)|268|(0)|4248|275|(0)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4260|103|(0)|(0)|(0)|159|(0)|231|(0)|233|234|235|(0)|4253|254|(0)|4250|265|(0)(0)|268|(0)|4248|275|(0)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4261|(0)|95|(0)|4260|103|(0)|(0)|(0)|159|(0)|231|(0)|233|234|235|(0)|4253|254|(0)|4250|265|(0)(0)|268|(0)|4248|275|(0)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4263|(0)|159|(0)|231|(0)|233|234|235|(0)|4253|254|(0)|4250|265|(0)(0)|268|(0)|4248|275|(0)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228)|4266|4268|45|46|(0)(0)|(0)|53|(0)|4264|(0)|78|(0)|4263|(0)|159|(0)|231|(0)|233|234|235|(0)|4253|254|(0)|4250|265|(0)(0)|268|(0)|4248|275|(0)|4247|288|(0)|4246|297|(0)(0)|300|(0)|302|303|304|(0)|307|(0)(0)|309|310|(0)|4204|314|(0)|316|317|(0)|4198|322|(0)|325|(0)|4195|4197|328|329|(0)|4154|(0)|4159|(0)(0)|(0)(0)|4186|(0)(0)|(0)|341|(0)|4153|346|(0)|4149|4151|349|4064|(0)|4138|(0)|4140|4141|4142|(0)(0)|4145|355|(0)(0)|690|(0)|1012|1014|1016|(0)|1063|(0)|1067|1068|(0)(0)|1071|(0)(0)|1074|(0)(0)|(0)(0)|696|(0)|1009|1011|700|701|(0)|703|704|(0)|718|(0)(0)|721|(0)|723|724|725|(0)|732|(0)|(0)(0)|737|(0)|747|(0)|750|(0)(0)|753|(0)|821|(0)|188|(0)|206|(0)|230|211|(0)|220|(0)|227|228|(1:(0))) */
    /* JADX WARN: Can't wrap try/catch for region: R(18:956|957|958|959|(1:961)(1:1005)|(2:962|963)|964|965|966|(1:968)(1:999)|969|(4:972|(2:974|975)(2:977|(2:979|980)(2:981|982))|976|970)|983|(1:985)(1:997)|986|(1:996)|991|(2:993|(1:995))) */
    /* JADX WARN: Can't wrap try/catch for region: R(6:(3:(8:(1:3718)(1:3720)|3719|3689|3690|3691|3692|3693|3694)(1:3687)|3693|3694)|3688|3689|3690|3691|3692) */
    /* JADX WARN: Can't wrap try/catch for region: R(6:(3:(8:(1:3794)(1:3796)|3795|3770|3771|3772|3773|3774|3775)(1:3768)|3774|3775)|3769|3770|3771|3772|3773) */
    /* JADX WARN: Code restructure failed: missing block: B:5105:0x0091, code lost:
        if (r74.isPlayingRound == (org.telegram.messenger.MediaController.getInstance().isPlayingMessage(r74.currentMessageObject) && (r7 = r74.delegate) != null && !r7.keyboardIsOpened())) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6052:0x0d65, code lost:
        if (r2.isSmall != false) goto L1458;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6159:0x0var_, code lost:
        r74.captionWidth = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6590:0x1867, code lost:
        if ((r10.flags & 2) == 0) goto L1538;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6947:0x2163, code lost:
        if (r2 >= (r74.timeWidth + org.telegram.messenger.AndroidUtilities.dp(20 + (!r75.isOutOwner() ? 0 : 20)))) goto L1988;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7682:0x30b0, code lost:
        if (r14.messageOwner.fwd_from.from_id != null) goto L2529;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8464:0x40c9, code lost:
        if (r74.isSmallImage != false) goto L3769;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8471:0x40ed, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8472:0x40ee, code lost:
        r59 = r3;
        r60 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8541:0x4239, code lost:
        if (r74.isSmallImage != false) goto L3688;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8548:0x4257, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8549:0x4258, code lost:
        r37 = r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9494:0x55a8, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9495:0x55a9, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:9547:0x56cf, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9548:0x56d0, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:9913:0x5ca7, code lost:
        if (r4.button.url.startsWith("tg://resolve") != false) goto L913;
     */
    /* JADX WARN: Multi-variable search skipped. Vars limit reached: 7152 (expected less than 5000) */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:10107:0x04a5 A[EDGE_INSN: B:10107:0x04a5->B:5469:0x04a5 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:5141:0x00e1  */
    /* JADX WARN: Removed duplicated region for block: B:5142:0x00e3  */
    /* JADX WARN: Removed duplicated region for block: B:5145:0x00e7  */
    /* JADX WARN: Removed duplicated region for block: B:5152:0x00f9  */
    /* JADX WARN: Removed duplicated region for block: B:5184:0x0133  */
    /* JADX WARN: Removed duplicated region for block: B:5191:0x0143  */
    /* JADX WARN: Removed duplicated region for block: B:5196:0x0156  */
    /* JADX WARN: Removed duplicated region for block: B:5197:0x015a  */
    /* JADX WARN: Removed duplicated region for block: B:5209:0x017c  */
    /* JADX WARN: Removed duplicated region for block: B:5216:0x0187  */
    /* JADX WARN: Removed duplicated region for block: B:5221:0x0194  */
    /* JADX WARN: Removed duplicated region for block: B:5222:0x0196  */
    /* JADX WARN: Removed duplicated region for block: B:5227:0x01a1  */
    /* JADX WARN: Removed duplicated region for block: B:5248:0x01c9 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5274:0x020e  */
    /* JADX WARN: Removed duplicated region for block: B:5279:0x0223  */
    /* JADX WARN: Removed duplicated region for block: B:5286:0x023c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5290:0x0247  */
    /* JADX WARN: Removed duplicated region for block: B:5291:0x0249  */
    /* JADX WARN: Removed duplicated region for block: B:5294:0x0259  */
    /* JADX WARN: Removed duplicated region for block: B:5295:0x025b  */
    /* JADX WARN: Removed duplicated region for block: B:5299:0x0263 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5322:0x02ae  */
    /* JADX WARN: Removed duplicated region for block: B:5329:0x02ce  */
    /* JADX WARN: Removed duplicated region for block: B:5333:0x02d5  */
    /* JADX WARN: Removed duplicated region for block: B:5348:0x02f2  */
    /* JADX WARN: Removed duplicated region for block: B:5360:0x0311  */
    /* JADX WARN: Removed duplicated region for block: B:5375:0x0360  */
    /* JADX WARN: Removed duplicated region for block: B:5376:0x0362  */
    /* JADX WARN: Removed duplicated region for block: B:5380:0x0374  */
    /* JADX WARN: Removed duplicated region for block: B:5390:0x0387  */
    /* JADX WARN: Removed duplicated region for block: B:5409:0x03ad  */
    /* JADX WARN: Removed duplicated region for block: B:5421:0x03c6  */
    /* JADX WARN: Removed duplicated region for block: B:5422:0x03ca  */
    /* JADX WARN: Removed duplicated region for block: B:5426:0x03ee  */
    /* JADX WARN: Removed duplicated region for block: B:5447:0x0416 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5466:0x047c  */
    /* JADX WARN: Removed duplicated region for block: B:5470:0x04a7  */
    /* JADX WARN: Removed duplicated region for block: B:5480:0x04fc A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5488:0x0511  */
    /* JADX WARN: Removed duplicated region for block: B:5498:0x0566  */
    /* JADX WARN: Removed duplicated region for block: B:5505:0x0576  */
    /* JADX WARN: Removed duplicated region for block: B:5509:0x0582 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5518:0x05b9  */
    /* JADX WARN: Removed duplicated region for block: B:5524:0x05c7  */
    /* JADX WARN: Removed duplicated region for block: B:5529:0x05de  */
    /* JADX WARN: Removed duplicated region for block: B:5530:0x05e0  */
    /* JADX WARN: Removed duplicated region for block: B:5533:0x05e4  */
    /* JADX WARN: Removed duplicated region for block: B:5546:0x0603  */
    /* JADX WARN: Removed duplicated region for block: B:5561:0x062c  */
    /* JADX WARN: Removed duplicated region for block: B:5562:0x0634  */
    /* JADX WARN: Removed duplicated region for block: B:5569:0x064b  */
    /* JADX WARN: Removed duplicated region for block: B:5582:0x0668  */
    /* JADX WARN: Removed duplicated region for block: B:5589:0x0691 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5607:0x06c0  */
    /* JADX WARN: Removed duplicated region for block: B:5612:0x06cc A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5618:0x070b  */
    /* JADX WARN: Removed duplicated region for block: B:5619:0x070d  */
    /* JADX WARN: Removed duplicated region for block: B:5624:0x0719  */
    /* JADX WARN: Removed duplicated region for block: B:5627:0x072a  */
    /* JADX WARN: Removed duplicated region for block: B:5667:0x07f1  */
    /* JADX WARN: Removed duplicated region for block: B:5670:0x0812  */
    /* JADX WARN: Removed duplicated region for block: B:5673:0x082b  */
    /* JADX WARN: Removed duplicated region for block: B:5674:0x082e  */
    /* JADX WARN: Removed duplicated region for block: B:5677:0x0839  */
    /* JADX WARN: Removed duplicated region for block: B:5682:0x0873  */
    /* JADX WARN: Removed duplicated region for block: B:5688:0x087f  */
    /* JADX WARN: Removed duplicated region for block: B:5691:0x088b  */
    /* JADX WARN: Removed duplicated region for block: B:5692:0x0892  */
    /* JADX WARN: Removed duplicated region for block: B:5697:0x08d1  */
    /* JADX WARN: Removed duplicated region for block: B:5818:0x0a47  */
    /* JADX WARN: Removed duplicated region for block: B:5821:0x0a52  */
    /* JADX WARN: Removed duplicated region for block: B:5849:0x0a92  */
    /* JADX WARN: Removed duplicated region for block: B:5857:0x0ac5  */
    /* JADX WARN: Removed duplicated region for block: B:5860:0x0acf  */
    /* JADX WARN: Removed duplicated region for block: B:5892:0x0b39  */
    /* JADX WARN: Removed duplicated region for block: B:5900:0x0b48  */
    /* JADX WARN: Removed duplicated region for block: B:5907:0x0b5b  */
    /* JADX WARN: Removed duplicated region for block: B:5919:0x0b76  */
    /* JADX WARN: Removed duplicated region for block: B:5938:0x0ba7  */
    /* JADX WARN: Removed duplicated region for block: B:5944:0x0bb2 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5956:0x0bcf  */
    /* JADX WARN: Removed duplicated region for block: B:5976:0x0c1f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:5982:0x0c2f  */
    /* JADX WARN: Removed duplicated region for block: B:5989:0x0c4a  */
    /* JADX WARN: Removed duplicated region for block: B:5998:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:5999:0x0c6e  */
    /* JADX WARN: Removed duplicated region for block: B:6009:0x0c9c  */
    /* JADX WARN: Removed duplicated region for block: B:6012:0x0cc9  */
    /* JADX WARN: Removed duplicated region for block: B:6013:0x0ccc  */
    /* JADX WARN: Removed duplicated region for block: B:6016:0x0cd4  */
    /* JADX WARN: Removed duplicated region for block: B:6017:0x0cd6  */
    /* JADX WARN: Removed duplicated region for block: B:6021:0x0ce3  */
    /* JADX WARN: Removed duplicated region for block: B:6024:0x0cea  */
    /* JADX WARN: Removed duplicated region for block: B:6035:0x0d19  */
    /* JADX WARN: Removed duplicated region for block: B:6038:0x0d2e  */
    /* JADX WARN: Removed duplicated region for block: B:6060:0x0d7f  */
    /* JADX WARN: Removed duplicated region for block: B:6062:0x0d8e  */
    /* JADX WARN: Removed duplicated region for block: B:6134:0x0ec7  */
    /* JADX WARN: Removed duplicated region for block: B:6201:0x100d  */
    /* JADX WARN: Removed duplicated region for block: B:6244:0x10fe A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6245:0x1100  */
    /* JADX WARN: Removed duplicated region for block: B:6256:0x1119  */
    /* JADX WARN: Removed duplicated region for block: B:6263:0x1132  */
    /* JADX WARN: Removed duplicated region for block: B:6269:0x1155 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6285:0x11c5  */
    /* JADX WARN: Removed duplicated region for block: B:6289:0x11fd  */
    /* JADX WARN: Removed duplicated region for block: B:6303:0x1219  */
    /* JADX WARN: Removed duplicated region for block: B:6304:0x122f  */
    /* JADX WARN: Removed duplicated region for block: B:6308:0x125f  */
    /* JADX WARN: Removed duplicated region for block: B:6318:0x1270 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6327:0x1280 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6336:0x1292 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:6352:0x12bd  */
    /* JADX WARN: Removed duplicated region for block: B:6353:0x12c1  */
    /* JADX WARN: Removed duplicated region for block: B:6363:0x12d7  */
    /* JADX WARN: Removed duplicated region for block: B:6552:0x174a  */
    /* JADX WARN: Removed duplicated region for block: B:6898:0x1f4b  */
    /* JADX WARN: Removed duplicated region for block: B:6955:0x2178  */
    /* JADX WARN: Removed duplicated region for block: B:6993:0x2389  */
    /* JADX WARN: Removed duplicated region for block: B:6994:0x2396  */
    /* JADX WARN: Removed duplicated region for block: B:7007:0x23b5  */
    /* JADX WARN: Removed duplicated region for block: B:7027:0x23ff  */
    /* JADX WARN: Removed duplicated region for block: B:7037:0x2445  */
    /* JADX WARN: Removed duplicated region for block: B:7243:0x2777  */
    /* JADX WARN: Removed duplicated region for block: B:7247:0x2792  */
    /* JADX WARN: Removed duplicated region for block: B:7260:0x27c0  */
    /* JADX WARN: Removed duplicated region for block: B:7264:0x27d8  */
    /* JADX WARN: Removed duplicated region for block: B:7269:0x27f4  */
    /* JADX WARN: Removed duplicated region for block: B:7270:0x27f6  */
    /* JADX WARN: Removed duplicated region for block: B:7274:0x280b  */
    /* JADX WARN: Removed duplicated region for block: B:7278:0x281a  */
    /* JADX WARN: Removed duplicated region for block: B:7279:0x281c  */
    /* JADX WARN: Removed duplicated region for block: B:7290:0x2853  */
    /* JADX WARN: Removed duplicated region for block: B:7693:0x30c9  */
    /* JADX WARN: Removed duplicated region for block: B:7698:0x30e6  */
    /* JADX WARN: Removed duplicated region for block: B:7706:0x311a  */
    /* JADX WARN: Removed duplicated region for block: B:7715:0x3132  */
    /* JADX WARN: Removed duplicated region for block: B:7719:0x3152  */
    /* JADX WARN: Removed duplicated region for block: B:7723:0x3162  */
    /* JADX WARN: Removed duplicated region for block: B:7727:0x3175  */
    /* JADX WARN: Removed duplicated region for block: B:7855:0x3402  */
    /* JADX WARN: Removed duplicated region for block: B:7860:0x3410  */
    /* JADX WARN: Removed duplicated region for block: B:7863:0x341d  */
    /* JADX WARN: Removed duplicated region for block: B:7872:0x3464  */
    /* JADX WARN: Removed duplicated region for block: B:7902:0x352a  */
    /* JADX WARN: Removed duplicated region for block: B:7905:0x3539  */
    /* JADX WARN: Removed duplicated region for block: B:7912:0x356d  */
    /* JADX WARN: Removed duplicated region for block: B:7916:0x3577  */
    /* JADX WARN: Removed duplicated region for block: B:7922:0x35b3  */
    /* JADX WARN: Removed duplicated region for block: B:7976:0x3767  */
    /* JADX WARN: Removed duplicated region for block: B:8142:0x3a71 A[Catch: Exception -> 0x3CLASSNAME, TryCatch #36 {Exception -> 0x3CLASSNAME, blocks: (B:8142:0x3a71, B:8145:0x3a79, B:8148:0x3a81, B:8155:0x3ab6, B:8162:0x3adf, B:8169:0x3b06, B:8165:0x3ae8, B:8168:0x3af5, B:8158:0x3abf, B:8161:0x3acc, B:8151:0x3a9a, B:8154:0x3aa5, B:8170:0x3b2e, B:8177:0x3b69, B:8184:0x3b92, B:8191:0x3bb9, B:8193:0x3bc0, B:8195:0x3bcc, B:8194:0x3bc7, B:8187:0x3b9b, B:8190:0x3ba8, B:8180:0x3b72, B:8183:0x3b7f, B:8173:0x3b47, B:8176:0x3b52, B:8140:0x3a6d), top: B:9998:0x3a6d }] */
    /* JADX WARN: Removed duplicated region for block: B:8170:0x3b2e A[Catch: Exception -> 0x3CLASSNAME, TryCatch #36 {Exception -> 0x3CLASSNAME, blocks: (B:8142:0x3a71, B:8145:0x3a79, B:8148:0x3a81, B:8155:0x3ab6, B:8162:0x3adf, B:8169:0x3b06, B:8165:0x3ae8, B:8168:0x3af5, B:8158:0x3abf, B:8161:0x3acc, B:8151:0x3a9a, B:8154:0x3aa5, B:8170:0x3b2e, B:8177:0x3b69, B:8184:0x3b92, B:8191:0x3bb9, B:8193:0x3bc0, B:8195:0x3bcc, B:8194:0x3bc7, B:8187:0x3b9b, B:8190:0x3ba8, B:8180:0x3b72, B:8183:0x3b7f, B:8173:0x3b47, B:8176:0x3b52, B:8140:0x3a6d), top: B:9998:0x3a6d }] */
    /* JADX WARN: Removed duplicated region for block: B:8226:0x3c8a  */
    /* JADX WARN: Removed duplicated region for block: B:8234:0x3c9a  */
    /* JADX WARN: Removed duplicated region for block: B:8244:0x3ce3  */
    /* JADX WARN: Removed duplicated region for block: B:8252:0x3d1f  */
    /* JADX WARN: Removed duplicated region for block: B:8259:0x3d41  */
    /* JADX WARN: Removed duplicated region for block: B:8270:0x3d6c  */
    /* JADX WARN: Removed duplicated region for block: B:8274:0x3d96  */
    /* JADX WARN: Removed duplicated region for block: B:8281:0x3da9  */
    /* JADX WARN: Removed duplicated region for block: B:8289:0x3dce  */
    /* JADX WARN: Removed duplicated region for block: B:8292:0x3de6  */
    /* JADX WARN: Removed duplicated region for block: B:8298:0x3e03  */
    /* JADX WARN: Removed duplicated region for block: B:8301:0x3e18  */
    /* JADX WARN: Removed duplicated region for block: B:8313:0x3e7d  */
    /* JADX WARN: Removed duplicated region for block: B:8327:0x3eae A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:8328:0x3eb0  */
    /* JADX WARN: Removed duplicated region for block: B:8334:0x3ec2  */
    /* JADX WARN: Removed duplicated region for block: B:8335:0x3ec5  */
    /* JADX WARN: Removed duplicated region for block: B:8341:0x3ed5 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:8350:0x3efe  */
    /* JADX WARN: Removed duplicated region for block: B:8377:0x3f4b  */
    /* JADX WARN: Removed duplicated region for block: B:8399:0x3var_ A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:8408:0x3f9a  */
    /* JADX WARN: Removed duplicated region for block: B:8418:0x3fc7  */
    /* JADX WARN: Removed duplicated region for block: B:8421:0x3fd3  */
    /* JADX WARN: Removed duplicated region for block: B:8422:0x3fd8  */
    /* JADX WARN: Removed duplicated region for block: B:8426:0x3fe4 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:8430:0x3fed  */
    /* JADX WARN: Removed duplicated region for block: B:8505:0x4183  */
    /* JADX WARN: Removed duplicated region for block: B:8506:0x4186  */
    /* JADX WARN: Removed duplicated region for block: B:8573:0x429f  */
    /* JADX WARN: Removed duplicated region for block: B:8579:0x42b1 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:8583:0x42ba  */
    /* JADX WARN: Removed duplicated region for block: B:8584:0x42bb A[Catch: Exception -> 0x4369, TRY_LEAVE, TryCatch #3 {Exception -> 0x4369, blocks: (B:8581:0x42b6, B:8594:0x42e7, B:8599:0x4308, B:8600:0x431d, B:8602:0x4345, B:8603:0x4354, B:8598:0x42ee, B:8584:0x42bb), top: B:9934:0x42b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:8594:0x42e7 A[Catch: Exception -> 0x4369, TRY_ENTER, TryCatch #3 {Exception -> 0x4369, blocks: (B:8581:0x42b6, B:8594:0x42e7, B:8599:0x4308, B:8600:0x431d, B:8602:0x4345, B:8603:0x4354, B:8598:0x42ee, B:8584:0x42bb), top: B:9934:0x42b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:8602:0x4345 A[Catch: Exception -> 0x4369, TryCatch #3 {Exception -> 0x4369, blocks: (B:8581:0x42b6, B:8594:0x42e7, B:8599:0x4308, B:8600:0x431d, B:8602:0x4345, B:8603:0x4354, B:8598:0x42ee, B:8584:0x42bb), top: B:9934:0x42b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:8603:0x4354 A[Catch: Exception -> 0x4369, TRY_LEAVE, TryCatch #3 {Exception -> 0x4369, blocks: (B:8581:0x42b6, B:8594:0x42e7, B:8599:0x4308, B:8600:0x431d, B:8602:0x4345, B:8603:0x4354, B:8598:0x42ee, B:8584:0x42bb), top: B:9934:0x42b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:8615:0x4374  */
    /* JADX WARN: Removed duplicated region for block: B:8633:0x43b6  */
    /* JADX WARN: Removed duplicated region for block: B:8634:0x43b9  */
    /* JADX WARN: Removed duplicated region for block: B:8638:0x43df  */
    /* JADX WARN: Removed duplicated region for block: B:8639:0x43e2  */
    /* JADX WARN: Removed duplicated region for block: B:8648:0x4436 A[Catch: Exception -> 0x44cc, TryCatch #28 {Exception -> 0x44cc, blocks: (B:8616:0x4375, B:8631:0x43ac, B:8635:0x43bd, B:8641:0x43f5, B:8642:0x441d, B:8644:0x4425, B:8645:0x442c, B:8648:0x4436, B:8657:0x4451, B:8651:0x4445, B:8654:0x444a, B:8629:0x43a8, B:8636:0x43ca, B:8640:0x43e6, B:8622:0x4394, B:8619:0x4381), top: B:9982:0x4375 }] */
    /* JADX WARN: Removed duplicated region for block: B:8674:0x4480  */
    /* JADX WARN: Removed duplicated region for block: B:8675:0x4481  */
    /* JADX WARN: Removed duplicated region for block: B:8699:0x44d5  */
    /* JADX WARN: Removed duplicated region for block: B:8707:0x44e7  */
    /* JADX WARN: Removed duplicated region for block: B:8709:0x44eb  */
    /* JADX WARN: Removed duplicated region for block: B:8733:0x454c  */
    /* JADX WARN: Removed duplicated region for block: B:8734:0x454e  */
    /* JADX WARN: Removed duplicated region for block: B:8737:0x455b  */
    /* JADX WARN: Removed duplicated region for block: B:8738:0x455d  */
    /* JADX WARN: Removed duplicated region for block: B:8741:0x4566  */
    /* JADX WARN: Removed duplicated region for block: B:8742:0x4569  */
    /* JADX WARN: Removed duplicated region for block: B:8746:0x457a  */
    /* JADX WARN: Removed duplicated region for block: B:8748:0x4583  */
    /* JADX WARN: Removed duplicated region for block: B:8990:0x4aa4  */
    /* JADX WARN: Removed duplicated region for block: B:9035:0x4b30  */
    /* JADX WARN: Removed duplicated region for block: B:9038:0x4b44  */
    /* JADX WARN: Removed duplicated region for block: B:9061:0x4b83  */
    /* JADX WARN: Removed duplicated region for block: B:9065:0x4b8e  */
    /* JADX WARN: Removed duplicated region for block: B:9071:0x4b9f  */
    /* JADX WARN: Removed duplicated region for block: B:9072:0x4ba1  */
    /* JADX WARN: Removed duplicated region for block: B:9075:0x4bb0  */
    /* JADX WARN: Removed duplicated region for block: B:9078:0x4bb8  */
    /* JADX WARN: Removed duplicated region for block: B:9085:0x4bc7  */
    /* JADX WARN: Removed duplicated region for block: B:9088:0x4bd5  */
    /* JADX WARN: Removed duplicated region for block: B:9097:0x4be8  */
    /* JADX WARN: Removed duplicated region for block: B:9101:0x4bfb  */
    /* JADX WARN: Removed duplicated region for block: B:9113:0x4c2a  */
    /* JADX WARN: Removed duplicated region for block: B:9117:0x4CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:9121:0x4CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:9125:0x4CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:9129:0x4c6a  */
    /* JADX WARN: Removed duplicated region for block: B:9140:0x4ca6  */
    /* JADX WARN: Removed duplicated region for block: B:9141:0x4cb8  */
    /* JADX WARN: Removed duplicated region for block: B:9149:0x4cea  */
    /* JADX WARN: Removed duplicated region for block: B:9155:0x4d48  */
    /* JADX WARN: Removed duplicated region for block: B:9296:0x517a  */
    /* JADX WARN: Removed duplicated region for block: B:9301:0x51a7  */
    /* JADX WARN: Removed duplicated region for block: B:9327:0x51f4  */
    /* JADX WARN: Removed duplicated region for block: B:9333:0x5254  */
    /* JADX WARN: Removed duplicated region for block: B:9355:0x534b  */
    /* JADX WARN: Removed duplicated region for block: B:9365:0x5374  */
    /* JADX WARN: Removed duplicated region for block: B:9380:0x539b  */
    /* JADX WARN: Removed duplicated region for block: B:9391:0x53b4  */
    /* JADX WARN: Removed duplicated region for block: B:9392:0x53b7  */
    /* JADX WARN: Removed duplicated region for block: B:9408:0x53f7  */
    /* JADX WARN: Removed duplicated region for block: B:9412:0x5424  */
    /* JADX WARN: Removed duplicated region for block: B:9413:0x5425 A[Catch: Exception -> 0x5499, TryCatch #20 {Exception -> 0x5499, blocks: (B:9410:0x541c, B:9424:0x5445, B:9426:0x545d, B:9428:0x5492, B:9427:0x5470, B:9413:0x5425, B:9415:0x542b, B:9418:0x5433, B:9423:0x5444, B:9419:0x5438, B:9422:0x5440), top: B:9967:0x541c }] */
    /* JADX WARN: Removed duplicated region for block: B:9426:0x545d A[Catch: Exception -> 0x5499, TryCatch #20 {Exception -> 0x5499, blocks: (B:9410:0x541c, B:9424:0x5445, B:9426:0x545d, B:9428:0x5492, B:9427:0x5470, B:9413:0x5425, B:9415:0x542b, B:9418:0x5433, B:9423:0x5444, B:9419:0x5438, B:9422:0x5440), top: B:9967:0x541c }] */
    /* JADX WARN: Removed duplicated region for block: B:9427:0x5470 A[Catch: Exception -> 0x5499, TryCatch #20 {Exception -> 0x5499, blocks: (B:9410:0x541c, B:9424:0x5445, B:9426:0x545d, B:9428:0x5492, B:9427:0x5470, B:9413:0x5425, B:9415:0x542b, B:9418:0x5433, B:9423:0x5444, B:9419:0x5438, B:9422:0x5440), top: B:9967:0x541c }] */
    /* JADX WARN: Removed duplicated region for block: B:9435:0x54a2  */
    /* JADX WARN: Removed duplicated region for block: B:9440:0x54b0 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:9441:0x54b1 A[Catch: Exception -> 0x55a8, TryCatch #19 {Exception -> 0x55a8, blocks: (B:9438:0x54aa, B:9448:0x54dc, B:9453:0x54ee, B:9460:0x5522, B:9464:0x552f, B:9470:0x554a, B:9476:0x5556, B:9480:0x5566, B:9483:0x557d, B:9486:0x558c, B:9479:0x5560, B:9473:0x554f, B:9467:0x5544, B:9463:0x5527, B:9456:0x54f6, B:9451:0x54e8, B:9457:0x5502, B:9441:0x54b1, B:9444:0x54b6, B:9445:0x54cc, B:9447:0x54d4, B:9488:0x5598), top: B:9965:0x54aa }] */
    /* JADX WARN: Removed duplicated region for block: B:9450:0x54e7 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:9451:0x54e8 A[Catch: Exception -> 0x55a8, TryCatch #19 {Exception -> 0x55a8, blocks: (B:9438:0x54aa, B:9448:0x54dc, B:9453:0x54ee, B:9460:0x5522, B:9464:0x552f, B:9470:0x554a, B:9476:0x5556, B:9480:0x5566, B:9483:0x557d, B:9486:0x558c, B:9479:0x5560, B:9473:0x554f, B:9467:0x5544, B:9463:0x5527, B:9456:0x54f6, B:9451:0x54e8, B:9457:0x5502, B:9441:0x54b1, B:9444:0x54b6, B:9445:0x54cc, B:9447:0x54d4, B:9488:0x5598), top: B:9965:0x54aa }] */
    /* JADX WARN: Removed duplicated region for block: B:9455:0x54f4  */
    /* JADX WARN: Removed duplicated region for block: B:9456:0x54f6 A[Catch: Exception -> 0x55a8, TryCatch #19 {Exception -> 0x55a8, blocks: (B:9438:0x54aa, B:9448:0x54dc, B:9453:0x54ee, B:9460:0x5522, B:9464:0x552f, B:9470:0x554a, B:9476:0x5556, B:9480:0x5566, B:9483:0x557d, B:9486:0x558c, B:9479:0x5560, B:9473:0x554f, B:9467:0x5544, B:9463:0x5527, B:9456:0x54f6, B:9451:0x54e8, B:9457:0x5502, B:9441:0x54b1, B:9444:0x54b6, B:9445:0x54cc, B:9447:0x54d4, B:9488:0x5598), top: B:9965:0x54aa }] */
    /* JADX WARN: Removed duplicated region for block: B:9459:0x5520  */
    /* JADX WARN: Removed duplicated region for block: B:9460:0x5522 A[Catch: Exception -> 0x55a8, TryCatch #19 {Exception -> 0x55a8, blocks: (B:9438:0x54aa, B:9448:0x54dc, B:9453:0x54ee, B:9460:0x5522, B:9464:0x552f, B:9470:0x554a, B:9476:0x5556, B:9480:0x5566, B:9483:0x557d, B:9486:0x558c, B:9479:0x5560, B:9473:0x554f, B:9467:0x5544, B:9463:0x5527, B:9456:0x54f6, B:9451:0x54e8, B:9457:0x5502, B:9441:0x54b1, B:9444:0x54b6, B:9445:0x54cc, B:9447:0x54d4, B:9488:0x5598), top: B:9965:0x54aa }] */
    /* JADX WARN: Removed duplicated region for block: B:9488:0x5598 A[Catch: Exception -> 0x55a8, TRY_LEAVE, TryCatch #19 {Exception -> 0x55a8, blocks: (B:9438:0x54aa, B:9448:0x54dc, B:9453:0x54ee, B:9460:0x5522, B:9464:0x552f, B:9470:0x554a, B:9476:0x5556, B:9480:0x5566, B:9483:0x557d, B:9486:0x558c, B:9479:0x5560, B:9473:0x554f, B:9467:0x5544, B:9463:0x5527, B:9456:0x54f6, B:9451:0x54e8, B:9457:0x5502, B:9441:0x54b1, B:9444:0x54b6, B:9445:0x54cc, B:9447:0x54d4, B:9488:0x5598), top: B:9965:0x54aa }] */
    /* JADX WARN: Removed duplicated region for block: B:9499:0x55b1  */
    /* JADX WARN: Removed duplicated region for block: B:9509:0x55ce  */
    /* JADX WARN: Removed duplicated region for block: B:9519:0x5633  */
    /* JADX WARN: Removed duplicated region for block: B:9520:0x5635  */
    /* JADX WARN: Removed duplicated region for block: B:9531:0x565d  */
    /* JADX WARN: Removed duplicated region for block: B:9532:0x565e A[Catch: Exception -> 0x56cf, TryCatch #31 {Exception -> 0x56cf, blocks: (B:9529:0x5657, B:9533:0x5667, B:9534:0x569e, B:9538:0x56a9, B:9539:0x56ad, B:9542:0x56bd, B:9544:0x56c1, B:9545:0x56c9, B:9532:0x565e), top: B:9988:0x5657 }] */
    /* JADX WARN: Removed duplicated region for block: B:9537:0x56a8  */
    /* JADX WARN: Removed duplicated region for block: B:9538:0x56a9 A[Catch: Exception -> 0x56cf, TryCatch #31 {Exception -> 0x56cf, blocks: (B:9529:0x5657, B:9533:0x5667, B:9534:0x569e, B:9538:0x56a9, B:9539:0x56ad, B:9542:0x56bd, B:9544:0x56c1, B:9545:0x56c9, B:9532:0x565e), top: B:9988:0x5657 }] */
    /* JADX WARN: Removed duplicated region for block: B:9539:0x56ad A[Catch: Exception -> 0x56cf, TryCatch #31 {Exception -> 0x56cf, blocks: (B:9529:0x5657, B:9533:0x5667, B:9534:0x569e, B:9538:0x56a9, B:9539:0x56ad, B:9542:0x56bd, B:9544:0x56c1, B:9545:0x56c9, B:9532:0x565e), top: B:9988:0x5657 }] */
    /* JADX WARN: Removed duplicated region for block: B:9552:0x56d9 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:9561:0x56fa  */
    /* JADX WARN: Removed duplicated region for block: B:9568:0x5714  */
    /* JADX WARN: Removed duplicated region for block: B:9585:0x5755  */
    /* JADX WARN: Removed duplicated region for block: B:9586:0x5757  */
    /* JADX WARN: Removed duplicated region for block: B:9590:0x576a  */
    /* JADX WARN: Removed duplicated region for block: B:9602:0x578d  */
    /* JADX WARN: Removed duplicated region for block: B:9603:0x578f  */
    /* JADX WARN: Removed duplicated region for block: B:9606:0x57ad  */
    /* JADX WARN: Removed duplicated region for block: B:9607:0x57b0  */
    /* JADX WARN: Removed duplicated region for block: B:9611:0x57bd  */
    /* JADX WARN: Removed duplicated region for block: B:9630:0x5816  */
    /* JADX WARN: Removed duplicated region for block: B:9635:0x5833  */
    /* JADX WARN: Removed duplicated region for block: B:9636:0x5835  */
    /* JADX WARN: Removed duplicated region for block: B:9643:0x5841  */
    /* JADX WARN: Removed duplicated region for block: B:9651:0x585f  */
    /* JADX WARN: Removed duplicated region for block: B:9654:0x5865  */
    /* JADX WARN: Removed duplicated region for block: B:9667:0x5883  */
    /* JADX WARN: Removed duplicated region for block: B:9672:0x589d  */
    /* JADX WARN: Removed duplicated region for block: B:9685:0x58dc  */
    /* JADX WARN: Removed duplicated region for block: B:9688:0x58e6  */
    /* JADX WARN: Removed duplicated region for block: B:9689:0x58ec  */
    /* JADX WARN: Removed duplicated region for block: B:9699:0x5923  */
    /* JADX WARN: Removed duplicated region for block: B:9763:0x59ab  */
    /* JADX WARN: Removed duplicated region for block: B:9770:0x59bb  */
    /* JADX WARN: Removed duplicated region for block: B:9784:0x59df  */
    /* JADX WARN: Removed duplicated region for block: B:9803:0x5a2c  */
    /* JADX WARN: Removed duplicated region for block: B:9824:0x5a5d  */
    /* JADX WARN: Removed duplicated region for block: B:9831:0x5a70  */
    /* JADX WARN: Removed duplicated region for block: B:9844:0x5aa7  */
    /* JADX WARN: Removed duplicated region for block: B:9881:0x5CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:9890:0x5CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:9891:0x5c4a A[Catch: Exception -> 0x5caf, TryCatch #11 {Exception -> 0x5caf, blocks: (B:9888:0x5c2f, B:9916:0x5cac, B:9891:0x5c4a, B:9897:0x5CLASSNAME, B:9900:0x5c6d, B:9903:0x5CLASSNAME, B:9906:0x5c7f, B:9909:0x5c8c, B:9912:0x5c9b, B:9894:0x5CLASSNAME), top: B:9949:0x5c2f }] */
    /* JADX WARN: Removed duplicated region for block: B:9908:0x5c8b  */
    /* JADX WARN: Removed duplicated region for block: B:9909:0x5c8c A[Catch: Exception -> 0x5caf, TryCatch #11 {Exception -> 0x5caf, blocks: (B:9888:0x5c2f, B:9916:0x5cac, B:9891:0x5c4a, B:9897:0x5CLASSNAME, B:9900:0x5c6d, B:9903:0x5CLASSNAME, B:9906:0x5c7f, B:9909:0x5c8c, B:9912:0x5c9b, B:9894:0x5CLASSNAME), top: B:9949:0x5c2f }] */
    /* JADX WARN: Removed duplicated region for block: B:9919:0x5cc2  */
    /* JADX WARN: Removed duplicated region for block: B:9941:0x3ffd A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:9953:0x51ba A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:9980:0x44a1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:9994:0x0ece A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:9999:0x020c A[SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r0v390, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r0v648, types: [org.telegram.ui.Components.RadialProgress2] */
    /* JADX WARN: Type inference failed for: r0v656, types: [org.telegram.ui.Components.RadialProgress2] */
    /* JADX WARN: Type inference failed for: r0v657, types: [org.telegram.messenger.ImageReceiver] */
    /* JADX WARN: Type inference failed for: r13v0 */
    /* JADX WARN: Type inference failed for: r13v1 */
    /* JADX WARN: Type inference failed for: r13v2 */
    /* JADX WARN: Type inference failed for: r13v267 */
    /* JADX WARN: Type inference failed for: r13v268 */
    /* JADX WARN: Type inference failed for: r13v3, types: [boolean] */
    /* JADX WARN: Type inference failed for: r21v33 */
    /* JADX WARN: Type inference failed for: r2v1252 */
    /* JADX WARN: Type inference failed for: r2v1253 */
    /* JADX WARN: Type inference failed for: r2v30 */
    /* JADX WARN: Type inference failed for: r2v31, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r2v39 */
    /* JADX WARN: Type inference failed for: r2v52, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r4v584, types: [org.telegram.tgnet.TLRPC$InputStickerSet] */
    /* JADX WARN: Type inference failed for: r6v473, types: [org.telegram.ui.Cells.ChatMessageCell$1] */
    /* JADX WARN: Type inference failed for: r6v489 */
    /* JADX WARN: Type inference failed for: r6v499, types: [org.telegram.tgnet.TLRPC$Document, java.lang.Object, org.telegram.tgnet.TLRPC$PhotoSize, android.graphics.drawable.Drawable] */
    /* JADX WARN: Type inference failed for: r6v774, types: [org.telegram.messenger.WebFile, android.text.StaticLayout, java.lang.String] */
    /* JADX WARN: Type inference failed for: r6v775 */
    /* JADX WARN: Type inference failed for: r6v794 */
    /* JADX WARN: Type inference failed for: r6v795 */
    /* JADX WARN: Type inference failed for: r6v796 */
    /* JADX WARN: Type inference failed for: r6v800 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setMessageContent(org.telegram.messenger.MessageObject r75, org.telegram.messenger.MessageObject.GroupedMessages r76, boolean r77, boolean r78) {
        /*
            Method dump skipped, instructions count: 23786
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageContent(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, boolean, boolean):void");
    }

    public static /* synthetic */ int lambda$setMessageContent$5(PollButton pollButton, PollButton pollButton2) {
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

    @Override // android.view.View, org.telegram.ui.Cells.TextSelectionHelper.SelectableView
    public void invalidate() {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (this.currentMessageObject == null) {
            return;
        }
        super.invalidate();
        if ((this.invalidatesParent || (this.currentMessagesGroup != null && (!this.links.isEmpty() || !this.reactionsLayoutInBubble.isEmpty))) && getParent() != null) {
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
        if (this.currentMessageObject == null || this.documentAttachType != 3) {
            return;
        }
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= this.documentAttach.attributes.size()) {
                break;
            }
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.documentAttach.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                byte[] bArr = tLRPC$DocumentAttribute.waveform;
                if (bArr == null || bArr.length == 0) {
                    MediaController.getInstance().generateWaveform(this.currentMessageObject);
                }
                byte[] bArr2 = tLRPC$DocumentAttribute.waveform;
                this.useSeekBarWaveform = bArr2 != null;
                this.seekBarWaveform.setWaveform(bArr2);
            } else {
                i++;
            }
        }
        if (this.currentMessageObject.isVoice() && this.useSeekBarWaveform && (tLRPC$Message = this.currentMessageObject.messageOwner) != null && !(MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) && UserConfig.getInstance(this.currentAccount).isPremium()) {
            z = true;
        }
        this.useTranscribeButton = z;
        updateSeekBarWaveformWidth();
    }

    private void updateSeekBarWaveformWidth() {
        if (this.seekBarWaveform != null) {
            int dp = (-AndroidUtilities.dp((this.hasLinkPreview ? 10 : 0) + 92)) - AndroidUtilities.dp(this.useTranscribeButton ? 34.0f : 0.0f);
            TransitionParams transitionParams = this.transitionParams;
            if (transitionParams.animateBackgroundBoundsInner && this.documentAttachType == 3) {
                int i = this.backgroundWidth;
                this.seekBarWaveform.setSize(((int) ((i - transitionParams.deltaLeft) + transitionParams.deltaRight)) + dp, AndroidUtilities.dp(30.0f), i + dp, ((int) ((i - transitionParams.toDeltaLeft) + transitionParams.toDeltaRight)) + dp);
                return;
            }
            this.seekBarWaveform.setSize(this.backgroundWidth + dp, AndroidUtilities.dp(30.0f));
        }
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
                            this.path.addRoundRect(this.rect, ChatMessageCell.radii, Path.Direction.CW);
                            this.path.close();
                            canvas.drawPath(this.path, paint);
                            return;
                        }
                        RectF rectF = this.rect;
                        float dp4 = ChatMessageCell.this.selectorDrawableMaskType[i] == 0 ? AndroidUtilities.dp(6.0f) : 0.0f;
                        if (ChatMessageCell.this.selectorDrawableMaskType[i] == 0) {
                            f = AndroidUtilities.dp(6.0f);
                        }
                        canvas.drawRoundRect(rectF, dp4, f, paint);
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
        int dp2;
        int i7;
        int dp3;
        int i8;
        int i9;
        int dp4;
        int dp5;
        if (this.currentMessageObject == null) {
            return;
        }
        int measuredHeight = getMeasuredHeight() + (getMeasuredWidth() << 16);
        int i10 = 10;
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
                int i11 = ((TLRPC$TL_messageExtendedMediaPreview) this.currentMessageObject.messageOwner.media.extended_media).video_duration;
                if (i11 != 0) {
                    String formatDuration = AndroidUtilities.formatDuration(i11, false);
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
        int i12 = this.documentAttachType;
        if (i12 == 3) {
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
            updateSeekBarWaveformWidth();
            SeekBar seekBar = this.seekBar;
            int i13 = this.backgroundWidth;
            if (!this.hasLinkPreview) {
                i10 = 0;
            }
            seekBar.setSize(i13 - AndroidUtilities.dp(i10 + 72), AndroidUtilities.dp(30.0f));
            this.seekBarY = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            int dp6 = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp6;
            RadialProgress2 radialProgress2 = this.radialProgress;
            int i14 = this.buttonX;
            radialProgress2.setProgressRect(i14, dp6, AndroidUtilities.dp(44.0f) + i14, this.buttonY + AndroidUtilities.dp(44.0f));
            updatePlayingMessageProgress();
        } else if (i12 == 5) {
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
            SeekBar seekBar2 = this.seekBar;
            int i15 = this.backgroundWidth;
            if (!this.hasLinkPreview) {
                i10 = 0;
            }
            seekBar2.setSize(i15 - AndroidUtilities.dp(i10 + 65), AndroidUtilities.dp(30.0f));
            this.seekBarY = AndroidUtilities.dp(29.0f) + this.namesOffset + this.mediaOffsetY;
            int dp7 = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp7;
            RadialProgress2 radialProgress22 = this.radialProgress;
            int i16 = this.buttonX;
            radialProgress22.setProgressRect(i16, dp7, AndroidUtilities.dp(44.0f) + i16, this.buttonY + AndroidUtilities.dp(44.0f));
            updatePlayingMessageProgress();
        } else if (i12 == 1 && !this.drawPhotoImage) {
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
            int dp8 = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp8;
            RadialProgress2 radialProgress23 = this.radialProgress;
            int i17 = this.buttonX;
            radialProgress23.setProgressRect(i17, dp8, AndroidUtilities.dp(44.0f) + i17, this.buttonY + AndroidUtilities.dp(44.0f));
            this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0f), this.buttonY - AndroidUtilities.dp(10.0f), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        } else {
            MessageObject messageObject = this.currentMessageObject;
            int i18 = messageObject.type;
            if (i18 == 12) {
                if (messageObject.isOutOwner()) {
                    dp5 = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                } else if (this.isChat && !this.isThreadPost && this.currentMessageObject.needDrawAvatar()) {
                    dp5 = AndroidUtilities.dp(72.0f);
                } else {
                    dp5 = AndroidUtilities.dp(23.0f);
                }
                this.photoImage.setImageCoords(dp5, AndroidUtilities.dp(13.0f) + this.namesOffset, AndroidUtilities.dp(44.0f), AndroidUtilities.dp(44.0f));
                return;
            }
            if (i18 == 0 && (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview)) {
                if (this.hasGamePreview) {
                    i7 = this.unmovedTextX - AndroidUtilities.dp(10.0f);
                } else {
                    if (this.hasInvoicePreview) {
                        i9 = this.unmovedTextX;
                        dp4 = AndroidUtilities.dp(1.0f);
                    } else {
                        i9 = this.unmovedTextX;
                        dp4 = AndroidUtilities.dp(1.0f);
                    }
                    i7 = i9 + dp4;
                }
                if (this.isSmallImage) {
                    dp = i7 + this.backgroundWidth;
                    dp2 = AndroidUtilities.dp(81.0f);
                    dp -= dp2;
                } else {
                    dp3 = this.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f);
                    dp = i7 + dp3;
                }
            } else {
                if (messageObject.isOutOwner()) {
                    if (this.mediaBackground) {
                        dp = this.layoutWidth - this.backgroundWidth;
                        dp2 = AndroidUtilities.dp(3.0f);
                    } else {
                        i7 = this.layoutWidth - this.backgroundWidth;
                        dp3 = AndroidUtilities.dp(6.0f);
                        dp = i7 + dp3;
                    }
                } else {
                    if (this.isChat && this.isAvatarVisible && !this.isPlayingRound) {
                        dp = AndroidUtilities.dp(63.0f);
                    } else {
                        dp = AndroidUtilities.dp(15.0f);
                    }
                    MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
                    if (groupedMessagePosition2 != null && !groupedMessagePosition2.edge) {
                        dp2 = AndroidUtilities.dp(10.0f);
                    }
                }
                dp -= dp2;
            }
            MessageObject.GroupedMessagePosition groupedMessagePosition3 = this.currentPosition;
            if (groupedMessagePosition3 != null) {
                if ((groupedMessagePosition3.flags & 1) == 0) {
                    dp -= AndroidUtilities.dp(2.0f);
                }
                if (this.currentPosition.leftSpanOffset != 0) {
                    dp += (int) Math.ceil((i8 / 1000.0f) * getGroupPhotosWidth());
                }
            }
            if (this.currentMessageObject.type != 0) {
                dp -= AndroidUtilities.dp(2.0f);
            }
            TransitionParams transitionParams = this.transitionParams;
            if (!transitionParams.imageChangeBoundsTransition || transitionParams.updatePhotoImageX) {
                transitionParams.updatePhotoImageX = false;
                ImageReceiver imageReceiver = this.photoImage;
                imageReceiver.setImageCoords(dp, imageReceiver.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
            }
            this.buttonX = (int) (dp + ((this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f)) / 2.0f));
            int imageY = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f)) / 2.0f));
            this.buttonY = imageY;
            RadialProgress2 radialProgress24 = this.radialProgress;
            int i19 = this.buttonX;
            radialProgress24.setProgressRect(i19, imageY, AndroidUtilities.dp(48.0f) + i19, this.buttonY + AndroidUtilities.dp(48.0f));
            this.deleteProgressRect.set(this.buttonX + AndroidUtilities.dp(5.0f), this.buttonY + AndroidUtilities.dp(5.0f), this.buttonX + AndroidUtilities.dp(43.0f), this.buttonY + AndroidUtilities.dp(43.0f));
            int i20 = this.documentAttachType;
            if (i20 != 4 && i20 != 2) {
                return;
            }
            this.videoButtonX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f));
            int imageY2 = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f));
            this.videoButtonY = imageY2;
            RadialProgress2 radialProgress25 = this.videoRadialProgress;
            int i21 = this.videoButtonX;
            radialProgress25.setProgressRect(i21, imageY2, AndroidUtilities.dp(24.0f) + i21, this.videoButtonY + AndroidUtilities.dp(24.0f));
        }
    }

    public boolean needDelayRoundProgressDraw() {
        int i = this.documentAttachType;
        return (i == 7 || i == 4) && this.currentMessageObject.type != 5 && MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
    }

    /* JADX WARN: Removed duplicated region for block: B:125:0x0069  */
    /* JADX WARN: Removed duplicated region for block: B:132:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x009d  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x00a6  */
    /* JADX WARN: Removed duplicated region for block: B:150:0x00cb  */
    /* JADX WARN: Removed duplicated region for block: B:153:0x0128  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x013b  */
    /* JADX WARN: Removed duplicated region for block: B:157:0x0140  */
    /* JADX WARN: Removed duplicated region for block: B:162:0x018c  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x0191  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0197  */
    /* JADX WARN: Removed duplicated region for block: B:167:0x024c  */
    /* JADX WARN: Removed duplicated region for block: B:170:0x0255  */
    /* JADX WARN: Removed duplicated region for block: B:176:0x026d  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x02b4  */
    /* JADX WARN: Removed duplicated region for block: B:183:0x02cc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawRoundProgress(android.graphics.Canvas r20) {
        /*
            Method dump skipped, instructions count: 727
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

    /* JADX WARN: Removed duplicated region for block: B:1166:0x08c3  */
    /* JADX WARN: Removed duplicated region for block: B:1173:0x090d  */
    /* JADX WARN: Removed duplicated region for block: B:1184:0x09ab  */
    /* JADX WARN: Removed duplicated region for block: B:1319:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:1320:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:1323:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:1324:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:1327:0x0cbc  */
    /* JADX WARN: Removed duplicated region for block: B:1340:0x0d14  */
    /* JADX WARN: Removed duplicated region for block: B:1343:0x0d1d  */
    /* JADX WARN: Removed duplicated region for block: B:1344:0x0d29  */
    /* JADX WARN: Removed duplicated region for block: B:1347:0x0d38  */
    /* JADX WARN: Removed duplicated region for block: B:1350:0x0d76  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawContent(android.graphics.Canvas r31) {
        /*
            Method dump skipped, instructions count: 4509
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawContent(android.graphics.Canvas):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:104:0x0017, code lost:
        if ((r1 & 1) != 0) goto L9;
     */
    /* JADX WARN: Removed duplicated region for block: B:177:0x0155  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x0171  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateReactionLayoutPosition() {
        /*
            Method dump skipped, instructions count: 436
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateReactionLayoutPosition():void");
    }

    /* JADX WARN: Type inference failed for: r12v2 */
    /* JADX WARN: Type inference failed for: r12v29 */
    /* JADX WARN: Type inference failed for: r12v3, types: [boolean, int] */
    public void drawLinkPreview(Canvas canvas, float f) {
        int dp;
        int i;
        int dp2;
        int i2;
        int i3;
        int i4;
        int i5;
        ?? r12;
        int i6;
        int i7;
        int i8;
        int i9;
        Drawable drawable;
        int i10;
        int i11;
        int i12;
        boolean z;
        boolean z2;
        int i13;
        int dp3;
        Paint paint;
        if (this.currentMessageObject.isSponsored() || this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview) {
            if (this.hasGamePreview) {
                dp = AndroidUtilities.dp(14.0f) + this.namesOffset;
                i2 = this.unmovedTextX - AndroidUtilities.dp(10.0f);
            } else {
                if (this.hasInvoicePreview) {
                    dp = AndroidUtilities.dp(14.0f) + this.namesOffset;
                    i = this.unmovedTextX;
                    dp2 = AndroidUtilities.dp(1.0f);
                } else if (this.currentMessageObject.isSponsored()) {
                    dp = (this.textY + this.currentMessageObject.textHeight) - AndroidUtilities.dp(2.0f);
                    if (this.hasNewLineForTime) {
                        dp += AndroidUtilities.dp(16.0f);
                    }
                    i = this.unmovedTextX;
                    dp2 = AndroidUtilities.dp(1.0f);
                } else {
                    dp = this.textY + this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f);
                    i = this.unmovedTextX;
                    dp2 = AndroidUtilities.dp(1.0f);
                }
                i2 = i + dp2;
            }
            int i14 = dp;
            int i15 = i2;
            if (!this.hasInvoicePreview && !this.currentMessageObject.isSponsored()) {
                Theme.chat_replyLinePaint.setColor(getThemedColor(this.currentMessageObject.isOutOwner() ? "chat_outPreviewLine" : "chat_inPreviewLine"));
                if (f != 1.0f) {
                    Theme.chat_replyLinePaint.setAlpha((int) (paint.getAlpha() * f));
                }
                canvas.drawRect(i15, i14 - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(2.0f) + i15, this.linkPreviewHeight + i14 + AndroidUtilities.dp(3.0f), Theme.chat_replyLinePaint);
            }
            if (this.siteNameLayout != null) {
                int dp4 = i14 - AndroidUtilities.dp(1.0f);
                Theme.chat_replyNamePaint.setColor(getThemedColor(this.currentMessageObject.isOutOwner() ? "chat_outSiteNameText" : "chat_inSiteNameText"));
                if (f != 1.0f) {
                    Theme.chat_replyNamePaint.setAlpha((int) (Theme.chat_replyLinePaint.getAlpha() * f));
                }
                canvas.save();
                if (this.siteNameRtl) {
                    dp3 = (this.backgroundWidth - this.siteNameWidth) - AndroidUtilities.dp(32.0f);
                    if (this.isSmallImage) {
                        dp3 -= AndroidUtilities.dp(54.0f);
                    }
                } else {
                    dp3 = this.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f);
                }
                canvas.translate(dp3 + i15, i14 - AndroidUtilities.dp(3.0f));
                this.siteNameLayout.draw(canvas);
                canvas.restore();
                StaticLayout staticLayout = this.siteNameLayout;
                i3 = staticLayout.getLineBottom(staticLayout.getLineCount() - 1) + i14;
                i4 = dp4;
            } else {
                i3 = i14;
                i4 = 0;
            }
            if ((this.hasGamePreview || this.hasInvoicePreview) && (i5 = this.currentMessageObject.textHeight) != 0) {
                i14 += i5 + AndroidUtilities.dp(4.0f);
                i3 += this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
            }
            if ((!this.drawPhotoImage || !this.drawInstantView || (i13 = this.drawInstantViewType) == 9 || i13 == 13 || i13 == 11 || i13 == 1) && (this.drawInstantViewType != 6 || this.imageBackgroundColor == 0)) {
                r12 = 1;
                i6 = 0;
            } else {
                if (i3 != i14) {
                    i3 += AndroidUtilities.dp(2.0f);
                }
                int i16 = i3;
                if (this.imageBackgroundSideColor != 0) {
                    int dp5 = AndroidUtilities.dp(10.0f) + i15;
                    ImageReceiver imageReceiver = this.photoImage;
                    float f2 = dp5;
                    imageReceiver.setImageCoords(((this.imageBackgroundSideWidth - imageReceiver.getImageWidth()) / 2.0f) + f2, i16, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    this.rect.set(f2, this.photoImage.getImageY(), dp5 + this.imageBackgroundSideWidth, this.photoImage.getImageY2());
                    Theme.chat_instantViewPaint.setColor(ColorUtils.setAlphaComponent(this.imageBackgroundSideColor, (int) (f * 255.0f)));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Theme.chat_instantViewPaint);
                } else {
                    this.photoImage.setImageCoords(AndroidUtilities.dp(10.0f) + i15, i16, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                }
                if (this.imageBackgroundColor != 0) {
                    this.rect.set(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX2(), this.photoImage.getImageY2());
                    if (this.imageBackgroundGradientColor1 != 0) {
                        if (this.imageBackgroundGradientColor2 != 0) {
                            if (this.motionBackgroundDrawable == null) {
                                MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(this.imageBackgroundColor, this.imageBackgroundGradientColor1, this.imageBackgroundGradientColor2, this.imageBackgroundGradientColor3, true);
                                this.motionBackgroundDrawable = motionBackgroundDrawable;
                                if (this.imageBackgroundIntensity < 0.0f) {
                                    this.photoImage.setGradientBitmap(motionBackgroundDrawable.getBitmap());
                                }
                                if (!this.photoImage.hasImageSet()) {
                                    this.motionBackgroundDrawable.setRoundRadius(AndroidUtilities.dp(4.0f));
                                }
                            }
                        } else {
                            if (this.gradientShader == null) {
                                Rect gradientPoints = BackgroundGradientDrawable.getGradientPoints(AndroidUtilities.getWallpaperRotation(this.imageBackgroundGradientRotation, false), (int) this.rect.width(), (int) this.rect.height());
                                this.gradientShader = new LinearGradient(gradientPoints.left, gradientPoints.top, gradientPoints.right, gradientPoints.bottom, new int[]{this.imageBackgroundColor, this.imageBackgroundGradientColor1}, (float[]) null, Shader.TileMode.CLAMP);
                            }
                            Theme.chat_instantViewPaint.setShader(this.gradientShader);
                            if (f != 1.0f) {
                                Theme.chat_instantViewPaint.setAlpha((int) (f * 255.0f));
                            }
                        }
                    } else {
                        Theme.chat_instantViewPaint.setShader(null);
                        Theme.chat_instantViewPaint.setColor(this.imageBackgroundColor);
                        if (f != 1.0f) {
                            Theme.chat_instantViewPaint.setAlpha((int) (f * 255.0f));
                        }
                    }
                    MotionBackgroundDrawable motionBackgroundDrawable2 = this.motionBackgroundDrawable;
                    if (motionBackgroundDrawable2 != null) {
                        RectF rectF = this.rect;
                        motionBackgroundDrawable2.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        this.motionBackgroundDrawable.draw(canvas);
                        i12 = i16;
                        z2 = true;
                        i6 = 0;
                    } else if (this.imageBackgroundSideColor != 0) {
                        i12 = i16;
                        i6 = 0;
                        z2 = true;
                        canvas.drawRect(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX2(), this.photoImage.getImageY2(), Theme.chat_instantViewPaint);
                    } else {
                        i12 = i16;
                        z2 = true;
                        i6 = 0;
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Theme.chat_instantViewPaint);
                    }
                    Theme.chat_instantViewPaint.setShader(null);
                    Theme.chat_instantViewPaint.setAlpha(255);
                    z = z2;
                } else {
                    i12 = i16;
                    z = true;
                    i6 = 0;
                }
                if (this.drawPhotoImage && this.drawInstantView) {
                    if (this.drawInstantViewType != 9) {
                        if (this.drawImageButton) {
                            int dp6 = AndroidUtilities.dp(48.0f);
                            float f3 = dp6;
                            int imageX = (int) (this.photoImage.getImageX() + ((this.photoImage.getImageWidth() - f3) / 2.0f));
                            this.buttonX = imageX;
                            this.buttonX = imageX;
                            int imageY = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - f3) / 2.0f));
                            this.buttonY = imageY;
                            this.buttonY = imageY;
                            RadialProgress2 radialProgress2 = this.radialProgress;
                            int i17 = this.buttonX;
                            radialProgress2.setProgressRect(i17, imageY, i17 + dp6, dp6 + imageY);
                        }
                        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                        if (chatMessageCellDelegate == null || chatMessageCellDelegate.getPinchToZoomHelper() == null || !this.delegate.getPinchToZoomHelper().isInOverlayModeFor(this)) {
                            if (f != 1.0f) {
                                this.photoImage.setAlpha(f);
                                this.imageDrawn = this.photoImage.draw(canvas);
                                this.photoImage.setAlpha(1.0f);
                            } else {
                                this.imageDrawn = this.photoImage.draw(canvas);
                            }
                        }
                    }
                }
                i3 = (int) (i12 + this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0f));
                r12 = z;
            }
            if (this.currentMessageObject.isOutOwner()) {
                int i18 = (int) (f * 255.0f);
                Theme.chat_replyNamePaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("chat_messageTextOut"), i18));
                Theme.chat_replyTextPaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("chat_messageTextOut"), i18));
            } else {
                int i19 = (int) (f * 255.0f);
                Theme.chat_replyNamePaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("chat_messageTextIn"), i19));
                Theme.chat_replyTextPaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("chat_messageTextIn"), i19));
            }
            if (this.titleLayout != null) {
                if (i3 != i14) {
                    i3 += AndroidUtilities.dp(2.0f);
                }
                if (i4 == 0) {
                    i4 = i3 - AndroidUtilities.dp(1.0f);
                }
                canvas.save();
                canvas.translate(AndroidUtilities.dp(10.0f) + i15 + this.titleX, i3 - AndroidUtilities.dp(3.0f));
                this.titleLayout.draw(canvas);
                canvas.restore();
                StaticLayout staticLayout2 = this.titleLayout;
                i3 += staticLayout2.getLineBottom(staticLayout2.getLineCount() - r12);
            }
            if (this.authorLayout != null) {
                if (i3 != i14) {
                    i3 += AndroidUtilities.dp(2.0f);
                }
                if (i4 == 0) {
                    i4 = i3 - AndroidUtilities.dp(1.0f);
                }
                canvas.save();
                canvas.translate(AndroidUtilities.dp(10.0f) + i15 + this.authorX, i3 - AndroidUtilities.dp(3.0f));
                this.authorLayout.draw(canvas);
                canvas.restore();
                StaticLayout staticLayout3 = this.authorLayout;
                i3 += staticLayout3.getLineBottom(staticLayout3.getLineCount() - r12);
            }
            if (this.descriptionLayout != null) {
                if (i3 != i14) {
                    i3 += AndroidUtilities.dp(2.0f);
                }
                int i20 = i3;
                if (i4 == 0) {
                    i4 = i20 - AndroidUtilities.dp(1.0f);
                }
                this.descriptionY = i20 - AndroidUtilities.dp(3.0f);
                canvas.save();
                canvas.translate((this.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f)) + i15 + this.descriptionX, this.descriptionY);
                if (this.linkBlockNum == -10 && this.links.draw(canvas)) {
                    invalidate();
                }
                ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
                if (chatMessageCellDelegate2 != null && chatMessageCellDelegate2.getTextSelectionHelper() != null && getDelegate().getTextSelectionHelper().isSelected(this.currentMessageObject)) {
                    this.delegate.getTextSelectionHelper().drawDescription(this.currentMessageObject.isOutOwner(), this.descriptionLayout, canvas);
                }
                this.descriptionLayout.draw(canvas);
                i7 = i14;
                i8 = i15;
                AnimatedEmojiSpan.drawAnimatedEmojis(canvas, this.descriptionLayout, this.animatedEmojiDescriptionStack, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f);
                canvas.restore();
                StaticLayout staticLayout4 = this.descriptionLayout;
                int lineCount = staticLayout4.getLineCount();
                int i21 = r12 == true ? 1 : 0;
                int i22 = r12 == true ? 1 : 0;
                int i23 = r12 == true ? 1 : 0;
                int i24 = r12 == true ? 1 : 0;
                i3 = i20 + staticLayout4.getLineBottom(lineCount - i21);
            } else {
                i7 = i14;
                i8 = i15;
            }
            int i25 = i4;
            if (!this.drawPhotoImage || !(!this.drawInstantView || (i11 = this.drawInstantViewType) == 9 || i11 == 11 || i11 == 13 || i11 == r12)) {
                i9 = i8;
            } else {
                if (i3 != i7) {
                    i3 += AndroidUtilities.dp(2.0f);
                }
                if (this.isSmallImage) {
                    i9 = i8;
                    this.photoImage.setImageCoords((i9 + this.backgroundWidth) - AndroidUtilities.dp(81.0f), i25, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                } else {
                    i9 = i8;
                    this.photoImage.setImageCoords(i9 + (this.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f)), i3, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    if (this.drawImageButton) {
                        int dp7 = AndroidUtilities.dp(48.0f);
                        float f4 = dp7;
                        int imageX2 = (int) (this.photoImage.getImageX() + ((this.photoImage.getImageWidth() - f4) / 2.0f));
                        this.buttonX = imageX2;
                        this.buttonX = imageX2;
                        int imageY2 = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - f4) / 2.0f));
                        this.buttonY = imageY2;
                        this.buttonY = imageY2;
                        RadialProgress2 radialProgress22 = this.radialProgress;
                        int i26 = this.buttonX;
                        radialProgress22.setProgressRect(i26, imageY2, i26 + dp7, dp7 + imageY2);
                    }
                }
                if (this.isRoundVideo && MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && MediaController.getInstance().isVideoDrawingReady() && canvas.isHardwareAccelerated()) {
                    this.imageDrawn = r12;
                    this.drawTime = r12;
                } else {
                    ChatMessageCellDelegate chatMessageCellDelegate3 = this.delegate;
                    if (chatMessageCellDelegate3 == null || chatMessageCellDelegate3.getPinchToZoomHelper() == null || !this.delegate.getPinchToZoomHelper().isInOverlayModeFor(this)) {
                        if (f != 1.0f) {
                            this.photoImage.setAlpha(f);
                            this.imageDrawn = this.photoImage.draw(canvas);
                            this.photoImage.setAlpha(1.0f);
                        } else {
                            this.imageDrawn = this.photoImage.draw(canvas);
                        }
                    }
                }
            }
            int i27 = this.documentAttachType;
            if (i27 == 4 || i27 == 2) {
                this.videoButtonX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f));
                int imageY3 = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f));
                this.videoButtonY = imageY3;
                RadialProgress2 radialProgress23 = this.videoRadialProgress;
                int i28 = this.videoButtonX;
                radialProgress23.setProgressRect(i28, imageY3, AndroidUtilities.dp(24.0f) + i28, this.videoButtonY + AndroidUtilities.dp(24.0f));
            }
            Paint themedPaint = getThemedPaint("paintChatTimeBackground");
            if (this.photosCountLayout != null && this.photoImage.getVisible()) {
                int imageX3 = (int) (((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.photosCountWidth);
                int imageY4 = (int) ((this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f));
                this.rect.set(imageX3 - AndroidUtilities.dp(4.0f), imageY4 - AndroidUtilities.dp(1.5f), this.photosCountWidth + imageX3 + AndroidUtilities.dp(4.0f), imageY4 + AndroidUtilities.dp(14.5f));
                int alpha = themedPaint.getAlpha();
                themedPaint.setAlpha((int) (alpha * this.controlsAlpha));
                Theme.chat_durationPaint.setAlpha((int) (this.controlsAlpha * 255.0f));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), themedPaint);
                themedPaint.setAlpha(alpha);
                canvas.save();
                canvas.translate(imageX3, imageY4);
                this.photosCountLayout.draw(canvas);
                canvas.restore();
                Theme.chat_durationPaint.setAlpha(255);
            }
            if (this.videoInfoLayout != null && ((!this.drawPhotoImage || this.photoImage.getVisible()) && this.imageBackgroundSideColor == 0)) {
                if (this.hasGamePreview || this.hasInvoicePreview || this.documentAttachType == 8) {
                    if (this.drawPhotoImage) {
                        i10 = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.5f));
                        i3 = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(6.0f));
                        this.rect.set(i10 - AndroidUtilities.dp(4.0f), i3 - AndroidUtilities.dp(1.5f), this.durationWidth + i10 + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(this.documentAttachType == 8 ? 14.5f : 16.5f) + i3);
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), themedPaint);
                    } else {
                        i10 = i9;
                    }
                } else {
                    i10 = (int) (((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.durationWidth);
                    i3 = (int) ((this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f));
                    this.rect.set(i10 - AndroidUtilities.dp(4.0f), i3 - AndroidUtilities.dp(1.5f), this.durationWidth + i10 + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(14.5f) + i3);
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), getThemedPaint("paintChatTimeBackground"));
                }
                canvas.save();
                canvas.translate(i10, i3);
                if (this.hasInvoicePreview) {
                    if (this.drawPhotoImage) {
                        Theme.chat_shipmentPaint.setColor(getThemedColor("chat_previewGameText"));
                    } else if (this.currentMessageObject.isOutOwner()) {
                        Theme.chat_shipmentPaint.setColor(getThemedColor("chat_messageTextOut"));
                    } else {
                        Theme.chat_shipmentPaint.setColor(getThemedColor("chat_messageTextIn"));
                    }
                }
                this.videoInfoLayout.draw(canvas);
                canvas.restore();
            }
            if (!this.drawInstantView) {
                return;
            }
            int dp8 = i7 + this.linkPreviewHeight + AndroidUtilities.dp(10.0f);
            Paint paint2 = Theme.chat_instantViewRectPaint;
            if (this.currentMessageObject.isOutOwner()) {
                drawable = getThemedDrawable("drawableMsgOutInstant");
                Theme.chat_instantViewPaint.setColor(getThemedColor("chat_outPreviewInstantText"));
                paint2.setColor(getThemedColor("chat_outPreviewInstantText"));
            } else {
                drawable = Theme.chat_msgInInstantDrawable;
                Theme.chat_instantViewPaint.setColor(getThemedColor("chat_inPreviewInstantText"));
                paint2.setColor(getThemedColor("chat_inPreviewInstantText"));
            }
            this.instantButtonRect.set(i9, dp8, i9 + this.instantWidth, AndroidUtilities.dp(36.0f) + dp8);
            if (Build.VERSION.SDK_INT >= 21) {
                this.selectorDrawableMaskType[i6] = i6;
                this.selectorDrawable[i6].setBounds(i9, dp8, i9 + this.instantWidth, AndroidUtilities.dp(36.0f) + dp8);
                this.selectorDrawable[i6].draw(canvas);
            }
            canvas.drawRoundRect(this.instantButtonRect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), paint2);
            if (this.drawInstantViewType == 0) {
                BaseCell.setDrawableBounds(drawable, ((this.instantTextLeftX + this.instantTextX) + i9) - AndroidUtilities.dp(15.0f), AndroidUtilities.dp(11.5f) + dp8, AndroidUtilities.dp(9.0f), AndroidUtilities.dp(13.0f));
                drawable.draw(canvas);
            }
            if (this.instantViewLayout == null) {
                return;
            }
            canvas.save();
            canvas.translate(i9 + this.instantTextX, dp8 + AndroidUtilities.dp(10.5f));
            this.instantViewLayout.draw(canvas);
            canvas.restore();
        }
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
                int i4 = this.backgroundDrawableLeft;
                if (!messageObject.isOutOwner() && !this.mediaBackground && !this.drawPinnedBottom) {
                    f3 = 17.0f;
                }
                this.captionX = i4 + AndroidUtilities.dp(f3) + this.captionOffsetX;
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
        if (messageObject == null || !messageObject.hasExtendedMedia()) {
            int i = this.documentAttachType;
            if (i == 3 || i == 5) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.radialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
                } else {
                    this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
                }
                int i2 = this.buttonState;
                if (i2 == 1) {
                    return 1;
                }
                if (i2 == 2) {
                    return 2;
                }
                return i2 == 4 ? 3 : 0;
            }
            if (i == 1 && !this.drawPhotoImage) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.radialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
                } else {
                    this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
                }
                int i3 = this.buttonState;
                if (i3 == -1) {
                    return 5;
                }
                if (i3 == 0) {
                    return 2;
                }
                if (i3 == 1) {
                    return 3;
                }
            } else {
                this.radialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
                this.videoRadialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
                int i4 = this.buttonState;
                if (i4 >= 0 && i4 < 4) {
                    if (i4 == 0) {
                        return 2;
                    }
                    if (i4 == 1) {
                        return 3;
                    }
                    return (i4 != 2 && this.autoPlayingMedia) ? 4 : 0;
                } else if (i4 == -1) {
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
            return 4;
        }
        return 4;
    }

    /* JADX WARN: Removed duplicated region for block: B:138:0x010d  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x011b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int getMaxNameWidth() {
        /*
            Method dump skipped, instructions count: 292
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getMaxNameWidth():int");
    }

    /* JADX WARN: Code restructure failed: missing block: B:608:0x0121, code lost:
        if ((r9 & 2) != 0) goto L56;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateButtonState(boolean r17, boolean r18, boolean r19) {
        /*
            Method dump skipped, instructions count: 2132
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
            if ((i2 == 3 || i2 == 5) && MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
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
        } else if (i3 == 4) {
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
        TLRPC$PhotoSize tLRPC$PhotoSize;
        String str;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.isAnyKindOfSticker()) {
            this.currentMessageObject.putInDownloadsStore = true;
        }
        int i = this.buttonState;
        int i2 = 2;
        if (i == 0 && (!this.drawVideoImageButton || z2)) {
            int i3 = this.documentAttachType;
            if (i3 == 3 || i3 == 5) {
                if (this.miniButtonState == 0) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 2, 0);
                    this.currentMessageObject.loadingCancelled = false;
                }
                if (!this.delegate.needPlayMessage(this.currentMessageObject)) {
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
            MessageObject messageObject2 = this.currentMessageObject;
            int i4 = messageObject2.type;
            if (i4 == 1 || i4 == 20) {
                this.photoImage.setForceLoading(true);
                ImageReceiver imageReceiver = this.photoImage;
                ImageLocation forObject = ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject);
                String str3 = this.currentPhotoFilter;
                ImageLocation forObject2 = ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject);
                String str4 = this.currentPhotoFilterThumb;
                BitmapDrawable bitmapDrawable = this.currentPhotoObjectThumbStripped;
                long j = this.currentPhotoObject.size;
                MessageObject messageObject3 = this.currentMessageObject;
                imageReceiver.setImage(forObject, str3, forObject2, str4, bitmapDrawable, j, null, messageObject3, messageObject3.shouldEncryptPhotoOrVideo() ? 2 : 0);
            } else if (i4 == 8) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 2, 0);
                if (this.currentMessageObject.loadedFileSize > 0) {
                    createLoadingProgressLayout(this.documentAttach);
                }
            } else if (this.isRoundVideo) {
                if (messageObject2.isSecretMedia()) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 2, 1);
                } else {
                    MessageObject messageObject4 = this.currentMessageObject;
                    messageObject4.gifState = 2.0f;
                    TLRPC$Document document = messageObject4.getDocument();
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(ImageLocation.getForDocument(document), null, ImageLocation.getForObject(tLRPC$PhotoSize, document), str2, document.size, null, this.currentMessageObject, 0);
                }
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
                    MessageObject messageObject5 = this.currentMessageObject;
                    if (!messageObject5.shouldEncryptPhotoOrVideo()) {
                        i2 = 0;
                    }
                    fileLoader.loadFile(tLRPC$Document, messageObject5, 1, i2);
                    MessageObject messageObject6 = this.currentMessageObject;
                    if (messageObject6.loadedFileSize > 0) {
                        createLoadingProgressLayout(messageObject6.getDocument());
                    }
                } else if (i4 != 0 || i5 == 0) {
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject), this.currentPhotoFilter, ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject), this.currentPhotoFilterThumb, this.currentPhotoObjectThumbStripped, 0L, null, this.currentMessageObject, 0);
                } else if (i5 == 2) {
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), null, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), this.currentPhotoFilterThumb, this.documentAttach.size, null, this.currentMessageObject, 0);
                    MessageObject messageObject7 = this.currentMessageObject;
                    messageObject7.gifState = 2.0f;
                    if (messageObject7.loadedFileSize > 0) {
                        createLoadingProgressLayout(messageObject7.getDocument());
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
            if (i6 == 3 || i6 == 5) {
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
                MessageObject messageObject8 = this.currentMessageObject;
                messageObject8.loadingCancelled = true;
                int i7 = this.documentAttachType;
                if (i7 == 2 || i7 == 4 || i7 == 1 || i7 == 8) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else {
                    int i8 = messageObject8.type;
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
        } else if (i == 2) {
            int i9 = this.documentAttachType;
            if (i9 == 3 || i9 == 5) {
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
        } else if (i == 3 || i == 0) {
            if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                this.miniButtonState = 1;
                this.radialProgress.setProgress(0.0f, false);
                this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, z);
            }
            this.delegate.didPressImage(this, 0.0f, 0.0f);
        } else if (i != 4) {
        } else {
            int i10 = this.documentAttachType;
            if (i10 != 3 && i10 != 5) {
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
    /* JADX WARN: Removed duplicated region for block: B:207:0x00b7  */
    /* JADX WARN: Removed duplicated region for block: B:208:0x00c6  */
    /* JADX WARN: Removed duplicated region for block: B:254:0x0173  */
    /* JADX WARN: Removed duplicated region for block: B:260:0x018b  */
    /* JADX WARN: Removed duplicated region for block: B:261:0x0194  */
    /* JADX WARN: Removed duplicated region for block: B:271:0x01e7  */
    /* JADX WARN: Removed duplicated region for block: B:277:0x0217  */
    /* JADX WARN: Removed duplicated region for block: B:280:0x0231  */
    /* JADX WARN: Removed duplicated region for block: B:285:0x024c  */
    /* JADX WARN: Removed duplicated region for block: B:288:0x0287  */
    /* JADX WARN: Removed duplicated region for block: B:294:0x02e0  */
    /* JADX WARN: Removed duplicated region for block: B:303:0x0324  */
    /* JADX WARN: Removed duplicated region for block: B:306:0x033a  */
    /* JADX WARN: Removed duplicated region for block: B:318:0x0373  */
    /* JADX WARN: Removed duplicated region for block: B:320:0x0385  */
    /* JADX WARN: Removed duplicated region for block: B:341:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void measureTime(org.telegram.messenger.MessageObject r18) {
        /*
            Method dump skipped, instructions count: 1009
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
                        return;
                    }
                }
                this.currentChat = messagesController.getChat(Long.valueOf(tLRPC$MessageFwdHeader.saved_from_peer.channel_id));
            } else {
                long j2 = tLRPC$Peer.chat_id;
                if (j2 == 0) {
                    return;
                }
                TLRPC$Peer tLRPC$Peer4 = tLRPC$MessageFwdHeader.from_id;
                if (tLRPC$Peer4 instanceof TLRPC$TL_peerUser) {
                    this.currentUser = messagesController.getUser(Long.valueOf(tLRPC$Peer4.user_id));
                } else {
                    this.currentChat = messagesController.getChat(Long.valueOf(j2));
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
                if (!tLRPC$Message.post) {
                    return;
                }
                this.currentChat = messagesController.getChat(Long.valueOf(tLRPC$Message.peer_id.channel_id));
            }
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(28:21|(1:23)|24|(1:446)(2:30|(23:32|33|34|(1:36)(1:(1:420)(1:421))|37|(1:39)(1:418)|40|(7:42|(1:44)|45|(1:47)(3:53|(1:55)(1:57)|56)|48|(1:50)(1:52)|51)|58|59|60|61|62|(3:64|(1:66)|67)(1:413)|68|(1:70)|(1:72)(1:412)|73|(3:75|(1:77)(1:79)|78)|80|(1:82)(2:406|(1:408)(2:409|(1:411)))|83|(1:85)))|422|(2:429|(22:445|34|(0)(0)|37|(0)(0)|40|(0)|58|59|60|61|62|(0)(0)|68|(0)|(0)(0)|73|(0)|80|(0)(0)|83|(0))(3:441|(1:443)|444))(1:428)|33|34|(0)(0)|37|(0)(0)|40|(0)|58|59|60|61|62|(0)(0)|68|(0)|(0)(0)|73|(0)|80|(0)(0)|83|(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:588:0x039c, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:589:0x039d, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:546:0x01df  */
    /* JADX WARN: Removed duplicated region for block: B:547:0x01e2  */
    /* JADX WARN: Removed duplicated region for block: B:552:0x0205  */
    /* JADX WARN: Removed duplicated region for block: B:553:0x0208  */
    /* JADX WARN: Removed duplicated region for block: B:556:0x0213  */
    /* JADX WARN: Removed duplicated region for block: B:576:0x032c A[Catch: Exception -> 0x039c, TryCatch #1 {Exception -> 0x039c, blocks: (B:574:0x030b, B:576:0x032c, B:578:0x0342, B:579:0x034d, B:581:0x035a, B:583:0x035e, B:585:0x036b, B:586:0x0398, B:580:0x0356), top: B:926:0x030b }] */
    /* JADX WARN: Removed duplicated region for block: B:580:0x0356 A[Catch: Exception -> 0x039c, TryCatch #1 {Exception -> 0x039c, blocks: (B:574:0x030b, B:576:0x032c, B:578:0x0342, B:579:0x034d, B:581:0x035a, B:583:0x035e, B:585:0x036b, B:586:0x0398, B:580:0x0356), top: B:926:0x030b }] */
    /* JADX WARN: Removed duplicated region for block: B:583:0x035e A[Catch: Exception -> 0x039c, TryCatch #1 {Exception -> 0x039c, blocks: (B:574:0x030b, B:576:0x032c, B:578:0x0342, B:579:0x034d, B:581:0x035a, B:583:0x035e, B:585:0x036b, B:586:0x0398, B:580:0x0356), top: B:926:0x030b }] */
    /* JADX WARN: Removed duplicated region for block: B:585:0x036b A[Catch: Exception -> 0x039c, TryCatch #1 {Exception -> 0x039c, blocks: (B:574:0x030b, B:576:0x032c, B:578:0x0342, B:579:0x034d, B:581:0x035a, B:583:0x035e, B:585:0x036b, B:586:0x0398, B:580:0x0356), top: B:926:0x030b }] */
    /* JADX WARN: Removed duplicated region for block: B:586:0x0398 A[Catch: Exception -> 0x039c, TRY_LEAVE, TryCatch #1 {Exception -> 0x039c, blocks: (B:574:0x030b, B:576:0x032c, B:578:0x0342, B:579:0x034d, B:581:0x035a, B:583:0x035e, B:585:0x036b, B:586:0x0398, B:580:0x0356), top: B:926:0x030b }] */
    /* JADX WARN: Removed duplicated region for block: B:592:0x03a4  */
    /* JADX WARN: Removed duplicated region for block: B:599:0x03c5  */
    /* JADX WARN: Removed duplicated region for block: B:600:0x03cc  */
    /* JADX WARN: Removed duplicated region for block: B:608:0x03f0  */
    /* JADX WARN: Type inference failed for: r0v18, types: [android.text.StaticLayout[]] */
    /* JADX WARN: Type inference failed for: r0v38, types: [android.text.SpannableStringBuilder, java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r0v39 */
    /* JADX WARN: Type inference failed for: r0v56, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r15v0, types: [java.lang.CharSequence, java.lang.String] */
    /* JADX WARN: Type inference failed for: r15v1, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r15v3 */
    /* JADX WARN: Type inference failed for: r15v4, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r15v5, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v129 */
    /* JADX WARN: Type inference failed for: r4v174, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v176 */
    /* JADX WARN: Type inference failed for: r4v2, types: [org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, java.lang.String] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setMessageObjectInternal(org.telegram.messenger.MessageObject r43) {
        /*
            Method dump skipped, instructions count: 3324
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageObjectInternal(org.telegram.messenger.MessageObject):void");
    }

    private boolean isNeedAuthorName() {
        return (this.isPinnedChat && this.currentMessageObject.type == 0) || ((!this.pinnedTop || (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup)) && this.drawName && this.isChat && (!this.currentMessageObject.isOutOwner() || (this.currentMessageObject.isSupergroup() && this.currentMessageObject.isFromGroup()))) || (this.currentMessageObject.isImportedForward() && this.currentMessageObject.messageOwner.fwd_from.from_id == null);
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
        int i;
        boolean z;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        Theme.MessageDrawable messageDrawable;
        int i2;
        int i3;
        int i4;
        int i5;
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
            int i6 = this.documentAttachType;
            if (i6 == 3) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBarWaveform.setColors(getThemedColor("chat_outVoiceSeekbar"), getThemedColor("chat_outVoiceSeekbarFill"), getThemedColor("chat_outVoiceSeekbarSelected"));
                    this.seekBar.setColors(getThemedColor("chat_outAudioSeekbar"), getThemedColor("chat_outAudioCacheSeekbar"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarSelected"));
                } else {
                    this.seekBarWaveform.setColors(getThemedColor("chat_inVoiceSeekbar"), getThemedColor("chat_inVoiceSeekbarFill"), getThemedColor("chat_inVoiceSeekbarSelected"));
                    this.seekBar.setColors(getThemedColor("chat_inAudioSeekbar"), getThemedColor("chat_inAudioCacheSeekbar"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarSelected"));
                }
            } else if (i6 == 5) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBar.setColors(getThemedColor("chat_outAudioSeekbar"), getThemedColor("chat_outAudioCacheSeekbar"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarSelected"));
                } else {
                    this.seekBar.setColors(getThemedColor("chat_inAudioSeekbar"), getThemedColor("chat_inAudioCacheSeekbar"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarSelected"));
                }
            }
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.type == 5) {
            Theme.chat_timePaint.setColor(getThemedColor("chat_serviceText"));
        } else if (this.mediaBackground) {
            if (messageObject.shouldDrawWithoutBackground()) {
                Theme.chat_timePaint.setColor(getThemedColor("chat_serviceText"));
            } else {
                Theme.chat_timePaint.setColor(getThemedColor("chat_mediaTimeText"));
            }
        } else if (messageObject.isOutOwner()) {
            Theme.chat_timePaint.setColor(getThemedColor(isDrawSelectionBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText"));
        } else {
            Theme.chat_timePaint.setColor(getThemedColor(isDrawSelectionBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText"));
        }
        drawBackgroundInternal(canvas, false);
        long j = 17;
        if (this.isHighlightedAnimated) {
            long currentTimeMillis = System.currentTimeMillis();
            long abs = Math.abs(currentTimeMillis - this.lastHighlightProgressTime);
            if (abs > 17) {
                abs = 17;
            }
            int i7 = (int) (this.highlightProgress - abs);
            this.highlightProgress = i7;
            this.lastHighlightProgressTime = currentTimeMillis;
            if (i7 <= 0) {
                this.highlightProgress = 0;
                this.isHighlightedAnimated = false;
            }
            invalidate();
            if (getParent() != null) {
                ((View) getParent()).invalidate();
            }
        }
        if (this.alphaInternal != 1.0f) {
            int measuredHeight = getMeasuredHeight();
            int measuredWidth = getMeasuredWidth();
            Theme.MessageDrawable messageDrawable2 = this.currentBackgroundDrawable;
            if (messageDrawable2 != null) {
                i5 = messageDrawable2.getBounds().top;
                i4 = this.currentBackgroundDrawable.getBounds().bottom;
                i3 = this.currentBackgroundDrawable.getBounds().left;
                i2 = this.currentBackgroundDrawable.getBounds().right;
            } else {
                i2 = measuredWidth;
                i3 = 0;
                i4 = measuredHeight;
                i5 = 0;
            }
            if (this.drawSideButton != 0) {
                if (this.currentMessageObject.isOutOwner()) {
                    i3 -= AndroidUtilities.dp(40.0f);
                } else {
                    i2 += AndroidUtilities.dp(40.0f);
                }
            }
            if (getY() < 0.0f) {
                i5 = (int) (-getY());
            }
            float y = getY() + getMeasuredHeight();
            int i8 = this.parentHeight;
            if (y > i8) {
                i4 = (int) (i8 - getY());
            }
            this.rect.set(i3, i5, i2, i4);
            i = canvas.saveLayerAlpha(this.rect, (int) (this.alphaInternal * 255.0f), 31);
        } else {
            i = Integer.MIN_VALUE;
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
            float f = 12.0f;
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
                    this.replyStartY = this.forwardNameY + AndroidUtilities.dp(38.0f);
                } else {
                    this.replyStartY = AndroidUtilities.dp(12.0f);
                }
            } else {
                if (this.currentMessageObject.isOutOwner()) {
                    this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f) + getExtraTextX();
                } else if (this.mediaBackground) {
                    this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f) + getExtraTextX();
                } else {
                    int i9 = this.backgroundDrawableLeft;
                    if (!this.drawPinnedBottom) {
                        f = 18.0f;
                    }
                    this.replyStartX = i9 + AndroidUtilities.dp(f) + getExtraTextX();
                }
                this.replyStartY = AndroidUtilities.dp(12 + ((!this.drawForwardedName || this.forwardedNameLayout[0] == null) ? 0 : 36) + ((!this.drawNameLayout || this.nameLayout == null) ? 0 : 20));
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
        if (this.drawBackground && shouldDrawSelectionOverlay() && this.currentMessagesGroup == null) {
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
        if (i != Integer.MIN_VALUE) {
            canvas.restoreToCount(i);
        }
        updateSelectionTextPosition();
    }

    @SuppressLint({"WrongCall"})
    public void drawBackgroundInternal(Canvas canvas, boolean z) {
        float f;
        Drawable shadowDrawable;
        int i;
        int i2;
        int dp;
        Drawable drawable;
        int i3;
        boolean z2;
        MessageObject.GroupedMessages groupedMessages;
        Theme.MessageDrawable messageDrawable;
        int i4;
        int i5;
        int i6;
        int dp2;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        Drawable shadowDrawable2;
        int i7;
        int i8;
        int i9;
        int dp3;
        if (this.currentMessageObject == null) {
            return;
        }
        boolean z3 = this.wasLayout;
        if (!z3 && !this.animationRunning) {
            forceLayout();
            return;
        }
        if (!z3) {
            onLayout(false, getLeft(), getTop(), getRight(), getBottom());
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
        boolean z4 = groupedMessagePosition2 != null && (groupedMessagePosition2.flags & 8) == 0 && this.currentMessagesGroup.isDocuments && !this.drawPinnedBottom;
        if (this.currentMessageObject.isOutOwner()) {
            if (this.transitionParams.changePinnedBottomProgress >= 1.0f && !this.mediaBackground && !this.drawPinnedBottom && !z4) {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOut");
                this.currentBackgroundSelectedDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutSelected");
                this.transitionParams.drawPinnedBottomBackground = false;
            } else {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutMedia");
                this.currentBackgroundSelectedDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutMediaSelected");
                this.transitionParams.drawPinnedBottomBackground = true;
            }
            setBackgroundTopY(true);
            if (isDrawSelectionBackground() && (this.currentPosition == null || getBackground() != null)) {
                shadowDrawable2 = this.currentBackgroundSelectedDrawable.getShadowDrawable();
            } else {
                shadowDrawable2 = this.currentBackgroundDrawable.getShadowDrawable();
            }
            Drawable drawable2 = shadowDrawable2;
            this.backgroundDrawableLeft = (this.layoutWidth - this.backgroundWidth) - (!this.mediaBackground ? 0 : AndroidUtilities.dp(9.0f));
            int dp4 = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
            this.backgroundDrawableRight = dp4;
            MessageObject.GroupedMessages groupedMessages2 = this.currentMessagesGroup;
            if (groupedMessages2 != null && !groupedMessages2.isDocuments && !this.currentPosition.edge) {
                this.backgroundDrawableRight = dp4 + AndroidUtilities.dp(10.0f);
            }
            int i10 = this.backgroundDrawableLeft;
            if (!z4 && this.transitionParams.changePinnedBottomProgress != 1.0f) {
                if (!this.mediaBackground) {
                    this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                }
            } else if (!this.mediaBackground && this.drawPinnedBottom) {
                this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
            }
            MessageObject.GroupedMessagePosition groupedMessagePosition3 = this.currentPosition;
            if (groupedMessagePosition3 != null) {
                if ((groupedMessagePosition3.flags & 2) == 0) {
                    this.backgroundDrawableRight += AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                }
                if ((this.currentPosition.flags & 1) == 0) {
                    i10 -= AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                    this.backgroundDrawableRight += AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                }
                if ((this.currentPosition.flags & 4) == 0) {
                    i8 = 0 - AndroidUtilities.dp(SharedConfig.bubbleRadius + 3);
                    i9 = AndroidUtilities.dp(SharedConfig.bubbleRadius + 3) + 0;
                } else {
                    i8 = 0;
                    i9 = 0;
                }
                if ((this.currentPosition.flags & 8) == 0) {
                    i9 += AndroidUtilities.dp(SharedConfig.bubbleRadius + 3);
                }
                i7 = i10;
            } else {
                i7 = i10;
                i8 = 0;
                i9 = 0;
            }
            boolean z5 = this.drawPinnedBottom;
            if (z5 && this.drawPinnedTop) {
                dp3 = 0;
            } else if (z5) {
                dp3 = AndroidUtilities.dp(1.0f);
            } else {
                dp3 = AndroidUtilities.dp(2.0f);
            }
            int dp5 = i8 + (this.drawPinnedTop ? 0 : AndroidUtilities.dp(1.0f));
            this.backgroundDrawableTop = dp5;
            int i11 = (this.layoutHeight - dp3) + i9;
            if (z4) {
                setDrawableBoundsInner(this.currentBackgroundDrawable, i7, dp5 - i8, this.backgroundDrawableRight, (i11 - i9) + 10);
                setDrawableBoundsInner(this.currentBackgroundSelectedDrawable, this.backgroundDrawableLeft, this.backgroundDrawableTop, this.backgroundDrawableRight - AndroidUtilities.dp(6.0f), i11);
            } else {
                int i12 = i7;
                setDrawableBoundsInner(this.currentBackgroundDrawable, i12, dp5, this.backgroundDrawableRight, i11);
                setDrawableBoundsInner(this.currentBackgroundSelectedDrawable, i12, this.backgroundDrawableTop, this.backgroundDrawableRight, i11);
            }
            setDrawableBoundsInner(drawable2, i7, this.backgroundDrawableTop, this.backgroundDrawableRight, i11);
            drawable = drawable2;
            f = 1.0f;
        } else {
            f = 1.0f;
            if (this.transitionParams.changePinnedBottomProgress >= 1.0f && !this.mediaBackground && !this.drawPinnedBottom && !z4) {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgIn");
                this.currentBackgroundSelectedDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInSelected");
                this.transitionParams.drawPinnedBottomBackground = false;
            } else {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMedia");
                this.currentBackgroundSelectedDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMediaSelected");
                this.transitionParams.drawPinnedBottomBackground = true;
            }
            setBackgroundTopY(true);
            if (isDrawSelectionBackground() && (this.currentPosition == null || getBackground() != null)) {
                shadowDrawable = this.currentBackgroundSelectedDrawable.getShadowDrawable();
            } else {
                shadowDrawable = this.currentBackgroundDrawable.getShadowDrawable();
            }
            Drawable drawable3 = shadowDrawable;
            this.backgroundDrawableLeft = AndroidUtilities.dp(((!this.isChat || !this.isAvatarVisible) ? 0 : 48) + (!this.mediaBackground ? 3 : 9));
            this.backgroundDrawableRight = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
            MessageObject.GroupedMessages groupedMessages3 = this.currentMessagesGroup;
            if (groupedMessages3 != null && !groupedMessages3.isDocuments) {
                if (!this.currentPosition.edge) {
                    this.backgroundDrawableLeft -= AndroidUtilities.dp(10.0f);
                    this.backgroundDrawableRight += AndroidUtilities.dp(10.0f);
                }
                if (this.currentPosition.leftSpanOffset != 0) {
                    this.backgroundDrawableLeft += (int) Math.ceil((i3 / 1000.0f) * getGroupPhotosWidth());
                }
            }
            boolean z6 = this.mediaBackground;
            if ((!z6 && this.drawPinnedBottom) || (!z4 && this.transitionParams.changePinnedBottomProgress != 1.0f)) {
                if (this.drawPinnedBottom || !z6) {
                    this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                }
                if (!this.mediaBackground) {
                    this.backgroundDrawableLeft += AndroidUtilities.dp(6.0f);
                }
            }
            MessageObject.GroupedMessagePosition groupedMessagePosition4 = this.currentPosition;
            if (groupedMessagePosition4 != null) {
                if ((groupedMessagePosition4.flags & 2) == 0) {
                    this.backgroundDrawableRight += AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                }
                if ((this.currentPosition.flags & 1) == 0) {
                    this.backgroundDrawableLeft -= AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                    this.backgroundDrawableRight += AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                }
                if ((this.currentPosition.flags & 4) == 0) {
                    i = 0 - AndroidUtilities.dp(SharedConfig.bubbleRadius + 3);
                    i2 = AndroidUtilities.dp(SharedConfig.bubbleRadius + 3) + 0;
                } else {
                    i = 0;
                    i2 = 0;
                }
                if ((this.currentPosition.flags & 8) == 0) {
                    i2 += AndroidUtilities.dp(SharedConfig.bubbleRadius + 4);
                }
            } else {
                i = 0;
                i2 = 0;
            }
            boolean z7 = this.drawPinnedBottom;
            if (z7 && this.drawPinnedTop) {
                dp = 0;
            } else if (z7) {
                dp = AndroidUtilities.dp(1.0f);
            } else {
                dp = AndroidUtilities.dp(2.0f);
            }
            int dp6 = (this.drawPinnedTop ? 0 : AndroidUtilities.dp(1.0f)) + i;
            this.backgroundDrawableTop = dp6;
            int i13 = (this.layoutHeight - dp) + i2;
            setDrawableBoundsInner(this.currentBackgroundDrawable, this.backgroundDrawableLeft, dp6, this.backgroundDrawableRight, i13);
            if (z4) {
                setDrawableBoundsInner(this.currentBackgroundSelectedDrawable, AndroidUtilities.dp(6.0f) + this.backgroundDrawableLeft, this.backgroundDrawableTop, this.backgroundDrawableRight - AndroidUtilities.dp(6.0f), i13);
            } else {
                setDrawableBoundsInner(this.currentBackgroundSelectedDrawable, this.backgroundDrawableLeft, this.backgroundDrawableTop, this.backgroundDrawableRight, i13);
            }
            setDrawableBoundsInner(drawable3, this.backgroundDrawableLeft, this.backgroundDrawableTop, this.backgroundDrawableRight, i13);
            drawable = drawable3;
        }
        if (!this.currentMessageObject.isOutOwner() && this.transitionParams.changePinnedBottomProgress != f && !this.mediaBackground && !this.drawPinnedBottom) {
            this.backgroundDrawableLeft -= AndroidUtilities.dp(6.0f);
            this.backgroundDrawableRight += AndroidUtilities.dp(6.0f);
        }
        if (this.hasPsaHint) {
            MessageObject.GroupedMessagePosition groupedMessagePosition5 = this.currentPosition;
            if (groupedMessagePosition5 == null || (groupedMessagePosition5.flags & 2) != 0) {
                i6 = this.currentBackgroundDrawable.getBounds().right;
            } else {
                int groupPhotosWidth = getGroupPhotosWidth();
                i6 = 0;
                for (int i14 = 0; i14 < this.currentMessagesGroup.posArray.size(); i14++) {
                    if (this.currentMessagesGroup.posArray.get(i14).minY != 0) {
                        break;
                    }
                    double d = i6;
                    double ceil = Math.ceil(((groupedMessagePosition.pw + groupedMessagePosition.leftSpanOffset) / 1000.0f) * groupPhotosWidth);
                    Double.isNaN(d);
                    i6 = (int) (d + ceil);
                }
            }
            Drawable drawable4 = Theme.chat_psaHelpDrawable[this.currentMessageObject.isOutOwner() ? 1 : 0];
            if (this.currentMessageObject.type == 5) {
                dp2 = AndroidUtilities.dp(12.0f);
            } else {
                dp2 = AndroidUtilities.dp((this.drawNameLayout ? 19 : 0) + 10);
            }
            this.psaHelpX = (i6 - drawable4.getIntrinsicWidth()) - AndroidUtilities.dp(this.currentMessageObject.isOutOwner() ? 20.0f : 14.0f);
            this.psaHelpY = dp2 + AndroidUtilities.dp(4.0f);
        }
        boolean z8 = this.checkBoxVisible;
        if (z8 || this.checkBoxAnimationInProgress) {
            if ((z8 && this.checkBoxAnimationProgress == f) || (!z8 && this.checkBoxAnimationProgress == 0.0f)) {
                this.checkBoxAnimationInProgress = false;
            }
            this.checkBoxTranslation = (int) Math.ceil((z8 ? CubicBezierInterpolator.EASE_OUT : CubicBezierInterpolator.EASE_IN).getInterpolation(this.checkBoxAnimationProgress) * AndroidUtilities.dp(35.0f));
            if (!this.currentMessageObject.isOutOwner()) {
                updateTranslation();
            }
            int dp7 = AndroidUtilities.dp(21.0f);
            this.checkBox.setBounds(AndroidUtilities.dp(-27.0f) + this.checkBoxTranslation, (this.currentBackgroundDrawable.getBounds().bottom - AndroidUtilities.dp(8.0f)) - dp7, dp7, dp7);
            if (this.checkBoxAnimationInProgress) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j = elapsedRealtime - this.lastCheckBoxAnimationTime;
                this.lastCheckBoxAnimationTime = elapsedRealtime;
                if (this.checkBoxVisible) {
                    float f2 = this.checkBoxAnimationProgress + (((float) j) / 200.0f);
                    this.checkBoxAnimationProgress = f2;
                    if (f2 > f) {
                        this.checkBoxAnimationProgress = f;
                    }
                } else {
                    float f3 = this.checkBoxAnimationProgress - (((float) j) / 200.0f);
                    this.checkBoxAnimationProgress = f3;
                    if (f3 <= 0.0f) {
                        this.checkBoxAnimationProgress = 0.0f;
                    }
                }
                invalidate();
                ((View) getParent()).invalidate();
            }
        }
        if (!z && drawBackgroundInParent()) {
            return;
        }
        if (this.transitionYOffsetForDrawables != 0.0f) {
            canvas.save();
            canvas.translate(0.0f, this.transitionYOffsetForDrawables);
            z2 = true;
        } else {
            z2 = false;
        }
        if (this.drawBackground && this.currentBackgroundDrawable != null && ((this.currentPosition == null || (isDrawSelectionBackground() && (this.currentMessageObject.isMusic() || this.currentMessageObject.isDocument()))) && (!this.enterTransitionInProgress || this.currentMessageObject.isVoice()))) {
            float f4 = this.alphaInternal;
            if (z) {
                f4 *= getAlpha();
            }
            if (hasSelectionOverlay()) {
                this.currentSelectedBackgroundAlpha = 0.0f;
                int i15 = (int) (f4 * 255.0f);
                this.currentBackgroundDrawable.setAlpha(i15);
                this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams);
                if (drawable != null && this.currentPosition == null) {
                    drawable.setAlpha(i15);
                    drawable.draw(canvas);
                }
            } else {
                if (this.isHighlightedAnimated) {
                    this.currentBackgroundDrawable.setAlpha((int) (f4 * 255.0f));
                    this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams);
                    float highlightAlpha = getHighlightAlpha();
                    this.currentSelectedBackgroundAlpha = highlightAlpha;
                    if (this.currentPosition == null) {
                        this.currentBackgroundSelectedDrawable.setAlpha((int) (highlightAlpha * f4 * 255.0f));
                        this.currentBackgroundSelectedDrawable.drawCached(canvas, this.backgroundCacheParams);
                    }
                } else if (this.selectedBackgroundProgress != 0.0f && ((groupedMessages = this.currentMessagesGroup) == null || !groupedMessages.isDocuments)) {
                    this.currentBackgroundDrawable.setAlpha((int) (f4 * 255.0f));
                    this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams);
                    float f5 = this.selectedBackgroundProgress;
                    this.currentSelectedBackgroundAlpha = f5;
                    this.currentBackgroundSelectedDrawable.setAlpha((int) (f5 * f4 * 255.0f));
                    this.currentBackgroundSelectedDrawable.drawCached(canvas, this.backgroundCacheParams);
                    if (this.currentBackgroundDrawable.getGradientShader() == null) {
                        drawable = null;
                    }
                } else if (isDrawSelectionBackground() && (this.currentPosition == null || this.currentMessageObject.isMusic() || this.currentMessageObject.isDocument() || getBackground() != null)) {
                    if (this.currentPosition != null) {
                        canvas.save();
                        canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    }
                    this.currentSelectedBackgroundAlpha = f;
                    this.currentBackgroundSelectedDrawable.setAlpha((int) (f4 * 255.0f));
                    this.currentBackgroundSelectedDrawable.drawCached(canvas, this.backgroundCacheParams);
                    if (this.currentPosition != null) {
                        canvas.restore();
                    }
                } else {
                    this.currentSelectedBackgroundAlpha = 0.0f;
                    this.currentBackgroundDrawable.setAlpha((int) (f4 * 255.0f));
                    this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams);
                }
                if (drawable != null && this.currentPosition == null) {
                    drawable.setAlpha((int) (f4 * 255.0f));
                    drawable.draw(canvas);
                }
                if (this.transitionParams.changePinnedBottomProgress != f && this.currentPosition == null) {
                    if (this.currentMessageObject.isOutOwner()) {
                        Theme.MessageDrawable messageDrawable2 = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOut");
                        Rect bounds = this.currentBackgroundDrawable.getBounds();
                        messageDrawable2.setBounds(bounds.left, bounds.top, bounds.right + AndroidUtilities.dp(6.0f), bounds.bottom);
                        canvas.save();
                        canvas.clipRect(bounds.right - AndroidUtilities.dp(12.0f), bounds.bottom - AndroidUtilities.dp(16.0f), bounds.right + AndroidUtilities.dp(12.0f), bounds.bottom);
                        int i16 = this.parentWidth;
                        int i17 = this.parentHeight;
                        if (i17 == 0) {
                            i16 = getParentWidth();
                            i17 = AndroidUtilities.displaySize.y;
                            if (getParent() instanceof View) {
                                View view = (View) getParent();
                                int measuredWidth = view.getMeasuredWidth();
                                i5 = view.getMeasuredHeight();
                                i4 = measuredWidth;
                                float y = getY();
                                float f6 = this.parentViewTopOffset;
                                messageDrawable2.setTop((int) (y + f6), i4, i5, (int) f6, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, this.pinnedBottom);
                                messageDrawable2.setAlpha((int) (((!this.mediaBackground || this.pinnedBottom) ? f - this.transitionParams.changePinnedBottomProgress : this.transitionParams.changePinnedBottomProgress) * 255.0f));
                                messageDrawable2.draw(canvas);
                                messageDrawable2.setAlpha(255);
                                canvas.restore();
                            }
                        }
                        i4 = i16;
                        i5 = i17;
                        float y2 = getY();
                        float var_ = this.parentViewTopOffset;
                        messageDrawable2.setTop((int) (y2 + var_), i4, i5, (int) var_, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, this.pinnedBottom);
                        messageDrawable2.setAlpha((int) (((!this.mediaBackground || this.pinnedBottom) ? f - this.transitionParams.changePinnedBottomProgress : this.transitionParams.changePinnedBottomProgress) * 255.0f));
                        messageDrawable2.draw(canvas);
                        messageDrawable2.setAlpha(255);
                        canvas.restore();
                    } else {
                        if (this.transitionParams.drawPinnedBottomBackground) {
                            messageDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgIn");
                        } else {
                            messageDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMedia");
                        }
                        messageDrawable.setAlpha((int) (((this.mediaBackground || this.pinnedBottom) ? f - this.transitionParams.changePinnedBottomProgress : this.transitionParams.changePinnedBottomProgress) * 255.0f));
                        Rect bounds2 = this.currentBackgroundDrawable.getBounds();
                        messageDrawable.setBounds(bounds2.left - AndroidUtilities.dp(6.0f), bounds2.top, bounds2.right, bounds2.bottom);
                        canvas.save();
                        canvas.clipRect(bounds2.left - AndroidUtilities.dp(6.0f), bounds2.bottom - AndroidUtilities.dp(16.0f), bounds2.left + AndroidUtilities.dp(6.0f), bounds2.bottom);
                        messageDrawable.draw(canvas);
                        messageDrawable.setAlpha(255);
                        canvas.restore();
                    }
                }
            }
        }
        if (!z2) {
            return;
        }
        canvas.restore();
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
        if (getAlpha() != 1.0f) {
            return false;
        }
        if ((this.transitionParams.transitionBotButtons.isEmpty() || !this.transitionParams.animateBotButtonsChanged) && this.botButtons.isEmpty() && this.drawSideButton == 0 && ((!this.drawNameLayout || this.nameLayout == null || this.currentNameStatus == null) && ((emojiGroupedSpans = this.animatedEmojiStack) == null || emojiGroupedSpans.holders.isEmpty()))) {
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
        int themedColor;
        float f;
        float f2 = 1.0f;
        if (!this.enterTransitionInProgress) {
            drawAnimatedEmojis(canvas, 1.0f);
        }
        if (this.currentNameStatus != null && this.currentNameStatusDrawable != null && this.drawNameLayout && this.nameLayout != null) {
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
                float f3 = 11.0f;
                if (this.mediaBackground || this.currentMessageObject.isOutOwner()) {
                    this.nameX = (((this.backgroundDrawableLeft + this.transitionParams.deltaLeft) + AndroidUtilities.dp(11.0f)) - this.nameOffsetX) + getExtraTextX();
                } else {
                    float f4 = this.backgroundDrawableLeft + this.transitionParams.deltaLeft;
                    if (this.mediaBackground || !this.drawPinnedBottom) {
                        f3 = 17.0f;
                    }
                    this.nameX = ((f4 + AndroidUtilities.dp(f3)) - this.nameOffsetX) + getExtraTextX();
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
            float f5 = this.nameY;
            TransitionParams transitionParams2 = this.transitionParams;
            this.nameY = f5 + transitionParams2.deltaTop;
            if (!transitionParams2.animateSign) {
                f = this.nameX;
            } else {
                f = this.transitionParams.animateNameX + ((this.nameX - this.transitionParams.animateNameX) * this.transitionParams.animateChangeProgress);
            }
            this.currentNameStatusDrawable.setBounds((int) (Math.abs(f) + this.nameLayoutWidth + AndroidUtilities.dp(2.0f)), (((int) this.nameY) + (this.nameLayout.getHeight() / 2)) - AndroidUtilities.dp(10.0f), (int) (Math.abs(f) + this.nameLayoutWidth + AndroidUtilities.dp(22.0f)), (int) (this.nameY + (this.nameLayout.getHeight() / 2) + AndroidUtilities.dp(10.0f)));
            this.currentNameStatusDrawable.setColor(Integer.valueOf(ColorUtils.setAlphaComponent(themedColor, 115)));
            this.currentNameStatusDrawable.draw(canvas);
        }
        if (!this.transitionParams.transitionBotButtons.isEmpty()) {
            TransitionParams transitionParams3 = this.transitionParams;
            if (transitionParams3.animateBotButtonsChanged) {
                drawBotButtons(canvas, transitionParams3.transitionBotButtons, 1.0f - this.transitionParams.animateChangeProgress);
            }
        }
        if (!this.botButtons.isEmpty()) {
            ArrayList<BotButton> arrayList = this.botButtons;
            TransitionParams transitionParams4 = this.transitionParams;
            if (transitionParams4.animateBotButtonsChanged) {
                f2 = transitionParams4.animateChangeProgress;
            }
            drawBotButtons(canvas, arrayList, f2);
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
            boolean r1 = org.telegram.ui.Cells.ChatMessageCell.TransitionParams.access$4900(r1)
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
            float dp3 = (this.layoutHeight - AndroidUtilities.dp(41.0f)) + this.transitionParams.deltaBottom;
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
            if (!reactionsLayoutInBubble.isSmall && reactionsLayoutInBubble.drawServiceShaderBackground) {
                this.sideStartY -= reactionsLayoutInBubble.getCurrentTotalHeight(this.transitionParams.animateChangeProgress);
            }
            if (!this.currentMessageObject.isOutOwner() && this.isRoundVideo && this.isAvatarVisible) {
                float f2 = (AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize) * 0.7f;
                boolean z = this.isPlayingRound;
                float f3 = z ? f2 : 0.0f;
                TransitionParams transitionParams2 = this.transitionParams;
                if (transitionParams2.animatePlayingRound) {
                    f3 = (z ? transitionParams2.animateChangeProgress : 1.0f - transitionParams2.animateChangeProgress) * f2;
                }
                this.sideStartX -= f3;
            }
            if (this.drawSideButton == 3) {
                if (this.enterTransitionInProgress && !this.currentMessageObject.isVoice()) {
                    return;
                }
                drawCommentButton(canvas, 1.0f);
                return;
            }
            RectF rectF = this.rect;
            float f4 = this.sideStartX;
            rectF.set(f4, this.sideStartY, AndroidUtilities.dp(32.0f) + f4, this.sideStartY + AndroidUtilities.dp(32.0f));
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
        int i;
        int i2 = 0;
        if (this.currentMessageObject.isOutOwner()) {
            int i3 = this.layoutWidth - this.backgroundWidth;
            if (this.mediaBackground) {
                i2 = AndroidUtilities.dp(9.0f);
            }
            return i3 - i2;
        }
        if (this.isChat && this.isAvatarVisible) {
            i2 = 48;
        }
        int dp = AndroidUtilities.dp(i2 + (!this.mediaBackground ? 3 : 9));
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.isDocuments && (i = this.currentPosition.leftSpanOffset) != 0) {
            dp += (int) Math.ceil((i / 1000.0f) * getGroupPhotosWidth());
        }
        return (this.mediaBackground || !this.drawPinnedBottom) ? dp : dp + AndroidUtilities.dp(6.0f);
    }

    public int getBackgroundDrawableRight() {
        int dp = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
        if (!this.mediaBackground && this.drawPinnedBottom && this.currentMessageObject.isOutOwner()) {
            dp -= AndroidUtilities.dp(6.0f);
        }
        if (!this.mediaBackground && this.drawPinnedBottom && !this.currentMessageObject.isOutOwner()) {
            dp -= AndroidUtilities.dp(6.0f);
        }
        return getBackgroundDrawableLeft() + dp;
    }

    public int getBackgroundDrawableTop() {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        int i = 0;
        int dp = (groupedMessagePosition == null || (groupedMessagePosition.flags & 4) != 0) ? 0 : 0 - AndroidUtilities.dp(3.0f);
        if (!this.drawPinnedTop) {
            i = AndroidUtilities.dp(1.0f);
        }
        return dp + i;
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
        return ((getBackgroundDrawableTop() + this.layoutHeight) - i2) + i;
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
            return this.replyNameLayout != null;
        }
        return true;
    }

    public boolean isDrawNameLayout() {
        return this.drawNameLayout && this.nameLayout != null;
    }

    public boolean isAdminLayoutChanged() {
        return !TextUtils.equals(this.lastPostAuthor, this.currentMessageObject.messageOwner.post_author);
    }

    /* JADX WARN: Removed duplicated region for block: B:615:0x04aa  */
    /* JADX WARN: Removed duplicated region for block: B:620:0x04cc  */
    /* JADX WARN: Removed duplicated region for block: B:621:0x04ce  */
    /* JADX WARN: Removed duplicated region for block: B:624:0x04d9  */
    /* JADX WARN: Removed duplicated region for block: B:628:0x04f6  */
    /* JADX WARN: Removed duplicated region for block: B:631:0x04fc  */
    /* JADX WARN: Removed duplicated region for block: B:743:0x07bb  */
    /* JADX WARN: Removed duplicated region for block: B:752:0x0813  */
    /* JADX WARN: Removed duplicated region for block: B:755:0x081a  */
    /* JADX WARN: Removed duplicated region for block: B:788:0x08fd  */
    /* JADX WARN: Removed duplicated region for block: B:876:0x0c1c  */
    /* JADX WARN: Removed duplicated region for block: B:883:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawNamesLayout(android.graphics.Canvas r29, float r30) {
        /*
            Method dump skipped, instructions count: 3104
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
        float f2 = 1.0f;
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
            if (reactionsLayoutInBubble.drawServiceShaderBackground) {
                applyServiceShaderMatrix();
            }
            ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
            if (reactionsLayoutInBubble2.drawServiceShaderBackground || !this.transitionParams.animateBackgroundBoundsInner || this.currentPosition != null) {
                TransitionParams transitionParams2 = this.transitionParams;
                if (transitionParams2.animateChange) {
                    f2 = transitionParams2.animateChangeProgress;
                }
                reactionsLayoutInBubble2.draw(canvas, f2, null);
                return;
            }
            canvas.save();
            canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), getBackgroundDrawableBottom() + this.transitionParams.deltaBottom);
            ReactionsLayoutInBubble reactionsLayoutInBubble3 = this.reactionsLayoutInBubble;
            TransitionParams transitionParams3 = this.transitionParams;
            if (transitionParams3.animateChange) {
                f2 = transitionParams3.animateChangeProgress;
            }
            reactionsLayoutInBubble3.draw(canvas, f2, null);
            canvas.restore();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:594:0x04f0  */
    /* JADX WARN: Removed duplicated region for block: B:604:0x053e  */
    /* JADX WARN: Removed duplicated region for block: B:605:0x0558  */
    /* JADX WARN: Removed duplicated region for block: B:608:0x0567  */
    /* JADX WARN: Removed duplicated region for block: B:609:0x056c  */
    /* JADX WARN: Removed duplicated region for block: B:612:0x0586  */
    /* JADX WARN: Removed duplicated region for block: B:614:0x0589  */
    /* JADX WARN: Removed duplicated region for block: B:620:0x05a0  */
    /* JADX WARN: Removed duplicated region for block: B:637:0x0645  */
    /* JADX WARN: Removed duplicated region for block: B:746:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawCaptionLayout(android.graphics.Canvas r27, android.text.StaticLayout r28, boolean r29, float r30) {
        /*
            Method dump skipped, instructions count: 2226
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
        String str;
        int i2;
        int i3;
        boolean z3;
        float f6;
        char c;
        int color;
        float dp;
        boolean z4;
        Paint themedPaint;
        int dp2;
        boolean z5;
        String str2;
        int i4;
        int i5;
        float currentWidth;
        TextPaint textPaint;
        if (((!this.drawTime || this.groupPhotoInvisible) && shouldDrawTimeOnMedia()) || staticLayout == null) {
            return;
        }
        MessageObject messageObject = this.currentMessageObject;
        if ((messageObject.deleted && this.currentPosition != null) || (i = messageObject.type) == 16) {
            return;
        }
        if (i == 5) {
            Theme.chat_timePaint.setColor(getThemedColor("chat_serviceText"));
        } else if (shouldDrawTimeOnMedia()) {
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                Theme.chat_timePaint.setColor(getThemedColor("chat_serviceText"));
            } else {
                Theme.chat_timePaint.setColor(getThemedColor("chat_mediaTimeText"));
            }
        } else if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_timePaint.setColor(getThemedColor(z2 ? "chat_outTimeSelectedText" : "chat_outTimeText"));
        } else {
            Theme.chat_timePaint.setColor(getThemedColor(z2 ? "chat_inTimeSelectedText" : "chat_inTimeText"));
        }
        float f7 = getTransitionParams().animateDrawingTimeAlpha ? getTransitionParams().animateChangeProgress * f : f;
        if (f7 != 1.0f) {
            Theme.chat_timePaint.setAlpha((int) (textPaint.getAlpha() * f7));
        }
        canvas.save();
        float f8 = 2.0f;
        if (this.drawPinnedBottom && !shouldDrawTimeOnMedia()) {
            canvas.translate(0.0f, AndroidUtilities.dp(2.0f));
        }
        TransitionParams transitionParams = this.transitionParams;
        float f9 = this.layoutHeight + transitionParams.deltaBottom;
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
            f9 -= getTranslationY();
            float var_ = this.currentMessagesGroup.transitionParams.offsetRight;
            f5 = f2 + var_;
            f4 += var_;
        }
        if (this.drawPinnedBottom && shouldDrawTimeOnMedia()) {
            f9 += AndroidUtilities.dp(1.0f);
        }
        float var_ = f9;
        TransitionParams transitionParams2 = this.transitionParams;
        boolean z6 = transitionParams2.animateBackgroundBoundsInner;
        if (z6) {
            float var_ = this.animationOffsetX;
            f5 += var_;
            f4 += var_;
        }
        float var_ = f5;
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble.isSmall) {
            if (z6 && transitionParams2.deltaRight != 0.0f) {
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
        boolean z7 = true;
        if (shouldDrawTimeOnMedia()) {
            int i6 = -(this.drawCommentButton ? AndroidUtilities.dp(41.3f) : 0);
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                themedPaint = getThemedPaint("paintChatActionBackground");
            } else {
                themedPaint = getThemedPaint("paintChatTimeBackground");
            }
            int alpha = themedPaint.getAlpha();
            themedPaint.setAlpha((int) (alpha * this.timeAlpha * f7));
            Theme.chat_timePaint.setAlpha((int) (this.timeAlpha * 255.0f * f7));
            int i7 = this.documentAttachType;
            if (i7 != 7 && i7 != 6 && this.currentMessageObject.type != 19) {
                int[] roundRadius = this.photoImage.getRoundRadius();
                dp2 = Math.min(AndroidUtilities.dp(8.0f), Math.max(roundRadius[2], roundRadius[3]));
                z5 = SharedConfig.bubbleRadius >= 10;
            } else {
                dp2 = AndroidUtilities.dp(4.0f);
                z5 = false;
            }
            float dp3 = var_ - AndroidUtilities.dp(z5 ? 6.0f : 4.0f);
            float imageY2 = this.additionalTimeOffsetY + this.photoImage.getImageY2();
            float dp4 = imageY2 - AndroidUtilities.dp(23.0f);
            RectF rectF = this.rect;
            float var_ = dp3 + f3;
            int i8 = z5 ? 12 : 8;
            if (this.currentMessageObject.isOutOwner()) {
                str2 = "paintChatTimeBackground";
                i4 = (this.currentMessageObject.type == 19 ? 4 : 0) + 20;
            } else {
                str2 = "paintChatTimeBackground";
                i4 = 0;
            }
            rectF.set(dp3, dp4, var_ + AndroidUtilities.dp(i8 + i4), AndroidUtilities.dp(17.0f) + dp4);
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
                z3 = z5;
                str = str2;
                i5 = i6;
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
                boolean z8 = this.statusDrawableAnimationInProgress;
                if (z8) {
                    createStatusDrawableParams = this.animateToStatusDrawableParams;
                }
                boolean z9 = (createStatusDrawableParams & 4) != 0;
                boolean z10 = (createStatusDrawableParams & 8) != 0;
                if (z8) {
                    int i10 = this.animateFromStatusDrawableParams;
                    boolean z11 = (i10 & 4) != 0;
                    boolean z12 = (i10 & 8) != 0;
                    float var_ = i6;
                    float var_ = f7;
                    z3 = z5;
                    i5 = i6;
                    str = str2;
                    drawClockOrErrorLayout(canvas, z11, z12, var_, var_, var_, var_, 1.0f - this.statusDrawableProgress, z2);
                    drawClockOrErrorLayout(canvas, z9, z10, var_, var_, var_, var_, this.statusDrawableProgress, z2);
                    if (!this.currentMessageObject.isOutOwner()) {
                        if (!z11 && !z12) {
                            drawViewsAndRepliesLayout(canvas, var_, f7, var_, var_, 1.0f - this.statusDrawableProgress, z2);
                        }
                        if (!z9 && !z10) {
                            drawViewsAndRepliesLayout(canvas, var_, f7, var_, var_, this.statusDrawableProgress, z2);
                        }
                    }
                } else {
                    z3 = z5;
                    str = str2;
                    i5 = i6;
                    if (!this.currentMessageObject.isOutOwner() && !z9 && !z10) {
                        drawViewsAndRepliesLayout(canvas, var_, f7, i5, var_, 1.0f, z2);
                    }
                    drawClockOrErrorLayout(canvas, z9, z10, var_, f7, i5, var_, 1.0f, z2);
                }
                if (this.currentMessageObject.isOutOwner()) {
                    drawViewsAndRepliesLayout(canvas, var_, f7, i5, var_, 1.0f, z2);
                }
                TransitionParams transitionParams3 = this.transitionParams;
                transitionParams3.lastStatusDrawableParams = transitionParams3.createStatusDrawableParams();
                if (z9 && z && getParent() != null) {
                    ((View) getParent()).invalidate();
                }
                var_ = var_;
            }
            canvas.save();
            float var_ = var_ + var_;
            this.drawTimeX = var_;
            float dp5 = (imageY2 - AndroidUtilities.dp(7.3f)) - staticLayout.getHeight();
            this.drawTimeY = dp5;
            canvas.translate(var_, dp5);
            staticLayout.draw(canvas);
            canvas.restore();
            Theme.chat_timePaint.setAlpha(255);
            i3 = i5;
        } else {
            str = "paintChatTimeBackground";
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
                boolean z13 = this.statusDrawableAnimationInProgress;
                if (z13) {
                    createStatusDrawableParams2 = this.animateToStatusDrawableParams;
                }
                boolean z14 = (createStatusDrawableParams2 & 4) != 0;
                boolean z15 = (createStatusDrawableParams2 & 8) != 0;
                if (z13) {
                    int i13 = this.animateFromStatusDrawableParams;
                    boolean z16 = (i13 & 4) != 0;
                    boolean z17 = (i13 & 8) != 0;
                    float var_ = i11;
                    float var_ = f7;
                    drawClockOrErrorLayout(canvas, z16, z17, var_, var_, var_, var_, 1.0f - this.statusDrawableProgress, z2);
                    drawClockOrErrorLayout(canvas, z14, z15, var_, var_, var_, var_, this.statusDrawableProgress, z2);
                    if (!this.currentMessageObject.isOutOwner()) {
                        if (!z16 && !z17) {
                            drawViewsAndRepliesLayout(canvas, var_, f7, var_, var_, 1.0f - this.statusDrawableProgress, z2);
                        }
                        if (!z14 && !z15) {
                            drawViewsAndRepliesLayout(canvas, var_, f7, var_, var_, this.statusDrawableProgress, z2);
                        }
                    }
                } else {
                    if (!this.currentMessageObject.isOutOwner() && !z14 && !z15) {
                        drawViewsAndRepliesLayout(canvas, var_, f7, i11, var_, 1.0f, z2);
                    }
                    drawClockOrErrorLayout(canvas, z14, z15, var_, f7, i11, var_, 1.0f, z2);
                }
                if (this.currentMessageObject.isOutOwner()) {
                    drawViewsAndRepliesLayout(canvas, var_, f7, i11, var_, 1.0f, z2);
                }
                TransitionParams transitionParams4 = this.transitionParams;
                transitionParams4.lastStatusDrawableParams = transitionParams4.createStatusDrawableParams();
                if (z14 && z && getParent() != null) {
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
            boolean z18 = (createStatusDrawableParams3 & 1) != 0;
            boolean z19 = (createStatusDrawableParams3 & 2) != 0;
            boolean z20 = (createStatusDrawableParams3 & 4) != 0;
            boolean z21 = (createStatusDrawableParams3 & 8) != 0;
            if (this.transitionYOffsetForDrawables != 0.0f) {
                canvas.save();
                canvas.translate(0.0f, this.transitionYOffsetForDrawables);
                z4 = true;
            } else {
                z4 = false;
            }
            if (this.statusDrawableAnimationInProgress) {
                int i15 = this.animateFromStatusDrawableParams;
                boolean z22 = (i15 & 1) != 0;
                boolean z23 = (i15 & 2) != 0;
                boolean z24 = (i15 & 4) != 0;
                boolean z25 = (i15 & 8) != 0;
                if (!z24 && z23 && z19 && !z22 && z18) {
                    f6 = 0.0f;
                    c = 5;
                    drawStatusDrawable(canvas, z18, z19, z20, z21, f7, z3, i3, var_, this.statusDrawableProgress, true, z2);
                } else {
                    f6 = 0.0f;
                    c = 5;
                    float var_ = i3;
                    boolean z26 = z25;
                    float var_ = f7;
                    boolean z27 = z3;
                    drawStatusDrawable(canvas, z22, z23, z24, z26, var_, z27, var_, var_, 1.0f - this.statusDrawableProgress, false, z2);
                    drawStatusDrawable(canvas, z18, z19, z20, z21, var_, z27, var_, var_, this.statusDrawableProgress, false, z2);
                }
            } else {
                f6 = 0.0f;
                c = 5;
                drawStatusDrawable(canvas, z18, z19, z20, z21, f7, z3, i3, var_, 1.0f, false, z2);
            }
            if (z4) {
                canvas.restore();
            }
            TransitionParams transitionParams6 = this.transitionParams;
            transitionParams6.lastStatusDrawableParams = transitionParams6.createStatusDrawableParams();
            if (z && z20 && getParent() != null) {
                ((View) getParent()).invalidate();
            }
        } else {
            f6 = 0.0f;
            c = 5;
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
        fArr[c] = var_;
        fArr[4] = var_;
        float var_ = roundRadius2[3];
        fArr[7] = var_;
        fArr[6] = var_;
        this.unlockSpoilerPath.addRoundRect(rectF2, fArr, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(this.unlockSpoilerPath);
        this.unlockSpoilerPath.rewind();
        rectF2.set(this.unlockX - AndroidUtilities.dp(12.0f), this.unlockY - AndroidUtilities.dp(8.0f), this.unlockX + Theme.chat_msgUnlockDrawable.getIntrinsicWidth() + AndroidUtilities.dp(14.0f) + this.unlockLayout.getWidth() + AndroidUtilities.dp(12.0f), this.unlockY + this.unlockLayout.getHeight() + AndroidUtilities.dp(8.0f));
        this.unlockSpoilerPath.addRoundRect(rectF2, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f), Path.Direction.CW);
        canvas.clipPath(this.unlockSpoilerPath, Region.Op.DIFFERENCE);
        this.unlockSpoilerEffect.setColor(ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), (int) (Color.alpha(color) * 0.325f)));
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
        canvas.translate(AndroidUtilities.dp(6.0f) + Theme.chat_msgUnlockDrawable.getIntrinsicWidth(), 0.0f);
        this.unlockLayout.draw(canvas);
        canvas.restore();
        if (this.videoInfoLayout == null || !this.photoImage.getVisible() || this.imageBackgroundSideColor != 0) {
            return;
        }
        int i16 = SharedConfig.bubbleRadius;
        if (i16 > 2) {
            dp = AndroidUtilities.dp(i16 - 2);
            if (SharedConfig.bubbleRadius < 10) {
                z7 = false;
            }
            z3 = z7;
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
            f8 = 0.0f;
        }
        rectF3.set(dp7, dp8, dp9 + AndroidUtilities.dp(f8), this.videoInfoLayout.getHeight() + imageY + AndroidUtilities.dp(1.5f));
        canvas.drawRoundRect(this.rect, dp, dp, getThemedPaint(str));
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
                    ChatMessageCell.this.lambda$createStatusDrawableAnimator$6(z, valueAnimator);
                }
            });
            this.statusDrawableAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.ChatMessageCell.6
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

    public /* synthetic */ void lambda$createStatusDrawableAnimator$6(boolean z, ValueAnimator valueAnimator) {
        this.statusDrawableProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (!z || getParent() == null) {
            return;
        }
        ((View) getParent()).invalidate();
    }

    private void drawClockOrErrorLayout(Canvas canvas, boolean z, boolean z2, float f, float f2, float f3, float f4, float f5, boolean z3) {
        float dp;
        int themedColor;
        float dp2;
        int i = 0;
        boolean z4 = f5 != 1.0f;
        float f6 = (f5 * 0.5f) + 0.5f;
        float f7 = f2 * f5;
        if (z) {
            if (this.currentMessageObject.isOutOwner()) {
                return;
            }
            MsgClockDrawable msgClockDrawable = Theme.chat_msgClockDrawable;
            String str = "chat_mediaSentClock";
            if (shouldDrawTimeOnMedia()) {
                themedColor = getThemedColor(str);
            } else {
                if (z3) {
                    str = "chat_outSentClockSelected";
                }
                themedColor = getThemedColor(str);
            }
            msgClockDrawable.setColor(themedColor);
            if (shouldDrawTimeOnMedia()) {
                dp2 = (this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(9.0f);
            } else {
                dp2 = (f - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 9.5f : 8.5f)) + f3;
            }
            if (!this.currentMessageObject.scheduled) {
                i = AndroidUtilities.dp(11.0f);
            }
            BaseCell.setDrawableBounds(msgClockDrawable, f4 + i, dp2 - msgClockDrawable.getIntrinsicHeight());
            msgClockDrawable.setAlpha((int) (f7 * 255.0f));
            if (z4) {
                canvas.save();
                canvas.scale(f6, f6, msgClockDrawable.getBounds().centerX(), msgClockDrawable.getBounds().centerY());
            }
            msgClockDrawable.draw(canvas);
            msgClockDrawable.setAlpha(255);
            invalidate();
            if (!z4) {
                return;
            }
            canvas.restore();
        } else if (!z2 || this.currentMessageObject.isOutOwner()) {
        } else {
            if (!this.currentMessageObject.scheduled) {
                i = AndroidUtilities.dp(11.0f);
            }
            float f8 = f4 + i;
            float f9 = 21.5f;
            if (shouldDrawTimeOnMedia()) {
                dp = (this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(21.5f);
            } else {
                if (!this.pinnedBottom && !this.pinnedTop) {
                    f9 = 20.5f;
                }
                dp = (f - AndroidUtilities.dp(f9)) + f3;
            }
            this.rect.set(f8, dp, AndroidUtilities.dp(14.0f) + f8, AndroidUtilities.dp(14.0f) + dp);
            int alpha = Theme.chat_msgErrorPaint.getAlpha();
            int i2 = (int) (f7 * 255.0f);
            Theme.chat_msgErrorPaint.setAlpha(i2);
            if (z4) {
                canvas.save();
                canvas.scale(f6, f6, this.rect.centerX(), this.rect.centerY());
            }
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
            Theme.chat_msgErrorPaint.setAlpha(alpha);
            Drawable themedDrawable = getThemedDrawable("drawableMsgError");
            BaseCell.setDrawableBounds(themedDrawable, f8 + AndroidUtilities.dp(6.0f), dp + AndroidUtilities.dp(2.0f));
            themedDrawable.setAlpha(i2);
            themedDrawable.draw(canvas);
            themedDrawable.setAlpha(255);
            if (!z4) {
                return;
            }
            canvas.restore();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:326:0x0152  */
    /* JADX WARN: Removed duplicated region for block: B:329:0x0180  */
    /* JADX WARN: Removed duplicated region for block: B:333:0x01c7  */
    /* JADX WARN: Removed duplicated region for block: B:336:0x01df  */
    /* JADX WARN: Removed duplicated region for block: B:337:0x01e3  */
    /* JADX WARN: Removed duplicated region for block: B:342:0x01fb  */
    /* JADX WARN: Removed duplicated region for block: B:344:0x020d  */
    /* JADX WARN: Removed duplicated region for block: B:347:0x0218  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawViewsAndRepliesLayout(android.graphics.Canvas r24, float r25, float r26, float r27, float r28, float r29, boolean r30) {
        /*
            Method dump skipped, instructions count: 1170
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawViewsAndRepliesLayout(android.graphics.Canvas, float, float, float, float, float, boolean):void");
    }

    private void drawStatusDrawable(Canvas canvas, boolean z, boolean z2, boolean z3, boolean z4, float f, boolean z5, float f2, float f3, float f4, boolean z6, boolean z7) {
        int dp;
        int dp2;
        Drawable themedDrawable;
        Drawable drawable;
        int i;
        boolean z8 = f4 != 1.0f && !z6;
        float f5 = (f4 * 0.5f) + 0.5f;
        float f6 = z8 ? f * f4 : f;
        float imageY2 = (this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(8.5f);
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
                    BaseCell.setDrawableBounds(msgClockDrawable, (i2 - AndroidUtilities.dp(f7)) - msgClockDrawable.getIntrinsicWidth(), (imageY2 - msgClockDrawable.getIntrinsicHeight()) + f2);
                    msgClockDrawable.setAlpha((int) (this.timeAlpha * 255.0f * f6));
                } else {
                    i = getThemedColor("chat_mediaSentClock");
                    int i3 = this.layoutWidth;
                    if (!z5) {
                        f7 = 22.0f;
                    }
                    BaseCell.setDrawableBounds(msgClockDrawable, (i3 - AndroidUtilities.dp(f7)) - msgClockDrawable.getIntrinsicWidth(), (imageY2 - msgClockDrawable.getIntrinsicHeight()) + f2);
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
                float var_ = 28.3f;
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    drawable = getThemedDrawable("drawableMsgStickerCheck");
                    if (z) {
                        if (z6) {
                            canvas.translate(AndroidUtilities.dp(4.8f) * (1.0f - f4), 0.0f);
                        }
                        int i4 = this.layoutWidth;
                        if (!z5) {
                            var_ = 26.3f;
                        }
                        BaseCell.setDrawableBounds(drawable, (i4 - AndroidUtilities.dp(var_)) - drawable.getIntrinsicWidth(), (imageY2 - drawable.getIntrinsicHeight()) + f2);
                    } else {
                        BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(z5 ? 23.5f : 21.5f)) - drawable.getIntrinsicWidth(), (imageY2 - drawable.getIntrinsicHeight()) + f2);
                    }
                    drawable.setAlpha((int) (this.timeAlpha * 255.0f * f6));
                } else {
                    if (z) {
                        if (z6) {
                            canvas.translate(AndroidUtilities.dp(4.8f) * (1.0f - f4), 0.0f);
                        }
                        Drawable drawable2 = Theme.chat_msgMediaCheckDrawable;
                        int i5 = this.layoutWidth;
                        if (!z5) {
                            var_ = 26.3f;
                        }
                        BaseCell.setDrawableBounds(drawable2, (i5 - AndroidUtilities.dp(var_)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (imageY2 - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight()) + f2);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(z5 ? 23.5f : 21.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (imageY2 - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight()) + f2);
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
                int i6 = this.layoutWidth;
                if (!z5) {
                    f8 = 21.5f;
                }
                BaseCell.setDrawableBounds(themedDrawable2, (i6 - AndroidUtilities.dp(f8)) - themedDrawable2.getIntrinsicWidth(), (imageY2 - themedDrawable2.getIntrinsicHeight()) + f2);
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
                float dp3 = (this.layoutWidth - AndroidUtilities.dp(18.0f)) - themedDrawable3.getIntrinsicWidth();
                if (!this.pinnedBottom && !this.pinnedTop) {
                    f9 = 8.0f;
                }
                BaseCell.setDrawableBounds(themedDrawable3, dp3, ((f3 - AndroidUtilities.dp(f9)) - themedDrawable3.getIntrinsicHeight()) + f2);
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

    /* JADX WARN: Code restructure failed: missing block: B:1339:0x09dd, code lost:
        if (r1[0] == 3) goto L323;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1605:0x1136, code lost:
        if (r3 == 2) goto L654;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:1484:0x0dda  */
    /* JADX WARN: Removed duplicated region for block: B:1768:0x1426  */
    /* JADX WARN: Removed duplicated region for block: B:1772:0x143f  */
    /* JADX WARN: Removed duplicated region for block: B:1780:0x1460  */
    /* JADX WARN: Removed duplicated region for block: B:1784:0x1477  */
    /* JADX WARN: Removed duplicated region for block: B:1806:0x14b5  */
    /* JADX WARN: Removed duplicated region for block: B:1810:0x14c6  */
    /* JADX WARN: Removed duplicated region for block: B:1858:0x15db  */
    /* JADX WARN: Removed duplicated region for block: B:1864:0x15ef  */
    /* JADX WARN: Removed duplicated region for block: B:1868:0x15fd  */
    /* JADX WARN: Removed duplicated region for block: B:1903:0x16c2  */
    /* JADX WARN: Removed duplicated region for block: B:1906:0x16c9  */
    /* JADX WARN: Removed duplicated region for block: B:1957:0x1812  */
    /* JADX WARN: Removed duplicated region for block: B:1964:0x1831  */
    /* JADX WARN: Removed duplicated region for block: B:1967:0x1885  */
    /* JADX WARN: Removed duplicated region for block: B:1991:0x0de3 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1992:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r10v1 */
    /* JADX WARN: Type inference failed for: r10v2, types: [org.telegram.ui.ActionBar.Theme$MessageDrawable, java.lang.String] */
    /* JADX WARN: Type inference failed for: r10v45 */
    /* JADX WARN: Type inference failed for: r2v449, types: [boolean] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawOverlays(android.graphics.Canvas r28) {
        /*
            Method dump skipped, instructions count: 6452
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
                rect5.set(i22, chatMessageCell5.replyStartY, Math.max(chatMessageCell5.replyNameWidth, ChatMessageCell.this.replyTextWidth) + i22, ChatMessageCell.this.replyStartY + AndroidUtilities.dp(35.0f));
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
                this.rect.set(min, ChatMessageCell.this.forwardNameY, ChatMessageCell.this.forwardedNameWidth + min, ChatMessageCell.this.forwardNameY + AndroidUtilities.dp(32.0f));
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
                this.rect.set((int) ChatMessageCell.this.transcribeX, (int) ChatMessageCell.this.transcribeY, (int) (ChatMessageCell.this.transcribeX + AndroidUtilities.dp(30.0f)), (int) (ChatMessageCell.this.transcribeY + AndroidUtilities.dp(30.0f)));
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
        boolean z = true;
        boolean z2 = f == 1.0f;
        if (getAlpha() != 1.0f) {
            z = false;
        }
        if (z2 != z) {
            invalidate();
        }
        if (this.ALPHA_PROPERTY_WORKAROUND) {
            this.alphaInternal = f;
            invalidate();
            return;
        }
        super.setAlpha(f);
    }

    public int getCurrentBackgroundLeft() {
        int i = this.currentBackgroundDrawable.getBounds().left;
        return (this.currentMessageObject.isOutOwner() || this.transitionParams.changePinnedBottomProgress == 1.0f || this.mediaBackground || this.drawPinnedBottom) ? i : i - AndroidUtilities.dp(6.0f);
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

        /* JADX WARN: Removed duplicated region for block: B:257:0x00c8  */
        /* JADX WARN: Removed duplicated region for block: B:258:0x00ea  */
        /* JADX WARN: Removed duplicated region for block: B:282:0x01e5  */
        /* JADX WARN: Removed duplicated region for block: B:306:0x026b  */
        /* JADX WARN: Removed duplicated region for block: B:318:0x02a7  */
        /* JADX WARN: Removed duplicated region for block: B:322:0x02c5  */
        /* JADX WARN: Removed duplicated region for block: B:325:0x02cb  */
        /* JADX WARN: Removed duplicated region for block: B:337:0x030a  */
        /* JADX WARN: Removed duplicated region for block: B:340:0x031a  */
        /* JADX WARN: Removed duplicated region for block: B:349:0x034f  */
        /* JADX WARN: Removed duplicated region for block: B:350:0x0354  */
        /* JADX WARN: Removed duplicated region for block: B:353:0x0359  */
        /* JADX WARN: Removed duplicated region for block: B:360:0x0375  */
        /* JADX WARN: Removed duplicated region for block: B:365:0x0391  */
        /* JADX WARN: Removed duplicated region for block: B:376:0x03e3  */
        /* JADX WARN: Removed duplicated region for block: B:379:0x03f8  */
        /* JADX WARN: Removed duplicated region for block: B:381:0x03fe  */
        /* JADX WARN: Removed duplicated region for block: B:390:0x044a  */
        /* JADX WARN: Removed duplicated region for block: B:393:0x0456  */
        /* JADX WARN: Removed duplicated region for block: B:396:0x0462  */
        /* JADX WARN: Removed duplicated region for block: B:399:0x0472  */
        /* JADX WARN: Removed duplicated region for block: B:402:0x0481  */
        /* JADX WARN: Removed duplicated region for block: B:407:0x04b9  */
        /* JADX WARN: Removed duplicated region for block: B:410:0x04c6  */
        /* JADX WARN: Removed duplicated region for block: B:421:0x04f9  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean animateChange() {
            /*
                Method dump skipped, instructions count: 1292
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

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    private boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }
}
