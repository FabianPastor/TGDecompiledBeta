package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
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
import android.util.SparseArray;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.MessageBackgroundDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RoundVideoPlayingDrawable;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.SecretMediaViewer;

public class ChatMessageCell extends BaseCell implements SeekBar.SeekBarDelegate, ImageReceiver.ImageReceiverDelegate, DownloadController.FileDownloadProgressListener, TextSelectionHelper.SelectableView {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_ROUND = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_THEME = 9;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static final int DOCUMENT_ATTACH_TYPE_WALLPAPER = 8;
    private int TAG;
    /* access modifiers changed from: private */
    public SparseArray<Rect> accessibilityVirtualViewBounds = new SparseArray<>();
    private int addedCaptionHeight;
    private boolean addedForTest;
    private StaticLayout adminLayout;
    private boolean allowAssistant;
    private boolean animatePollAnswer;
    private boolean animatePollAnswerAlpha;
    private int animatingDrawVideoImageButton;
    private float animatingDrawVideoImageButtonProgress;
    private int animatingNoSound;
    private boolean animatingNoSoundPlaying;
    private float animatingNoSoundProgress;
    private boolean animationRunning;
    private boolean attachedToWindow;
    private StaticLayout authorLayout;
    private int authorX;
    private boolean autoPlayingMedia;
    private int availableTimeWidth;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage = new ImageReceiver();
    private boolean avatarPressed;
    private MessageBackgroundDrawable backgroundDrawable = new MessageBackgroundDrawable(this);
    /* access modifiers changed from: private */
    public int backgroundDrawableLeft;
    private int backgroundDrawableRight;
    /* access modifiers changed from: private */
    public int backgroundWidth = 100;
    /* access modifiers changed from: private */
    public ArrayList<BotButton> botButtons = new ArrayList<>();
    private HashMap<String, BotButton> botButtonsByData = new HashMap<>();
    private HashMap<String, BotButton> botButtonsByPosition = new HashMap<>();
    private String botButtonsLayout;
    private boolean bottomNearToSet;
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private boolean canStreamVideo;
    private boolean cancelLoading;
    private int captionHeight;
    private StaticLayout captionLayout;
    private int captionOffsetX;
    private int captionWidth;
    private int captionX;
    private int captionY;
    private CheckBoxBase checkBox;
    private boolean checkBoxAnimationInProgress;
    private float checkBoxAnimationProgress;
    private int checkBoxTranslation;
    private boolean checkBoxVisible;
    private boolean checkOnlyButtonPressed;
    private AvatarDrawable contactAvatarDrawable;
    private float controlsAlpha = 1.0f;
    private int currentAccount = UserConfig.selectedAccount;
    private Drawable currentBackgroundDrawable;
    private Drawable currentBackgroundSelectedDrawable;
    private CharSequence currentCaption;
    private TLRPC.Chat currentChat;
    private int currentFocusedVirtualView = -1;
    private TLRPC.Chat currentForwardChannel;
    private String currentForwardName;
    private String currentForwardNameString;
    private TLRPC.User currentForwardUser;
    private int currentMapProvider;
    /* access modifiers changed from: private */
    public MessageObject currentMessageObject;
    private MessageObject.GroupedMessages currentMessagesGroup;
    private String currentNameString;
    private TLRPC.FileLocation currentPhoto;
    private String currentPhotoFilter;
    private String currentPhotoFilterThumb;
    private TLRPC.PhotoSize currentPhotoObject;
    private TLRPC.PhotoSize currentPhotoObjectThumb;
    private MessageObject.GroupedMessagePosition currentPosition;
    private TLRPC.PhotoSize currentReplyPhoto;
    /* access modifiers changed from: private */
    public String currentTimeString;
    private String currentUrl;
    /* access modifiers changed from: private */
    public TLRPC.User currentUser;
    private TLRPC.User currentViaBotUser;
    private String currentViewsString;
    private WebFile currentWebFile;
    /* access modifiers changed from: private */
    public ChatMessageCellDelegate delegate;
    private RectF deleteProgressRect = new RectF();
    private StaticLayout descriptionLayout;
    private int descriptionX;
    private int descriptionY;
    private boolean disallowLongPress;
    private StaticLayout docTitleLayout;
    private int docTitleOffsetX;
    private int docTitleWidth;
    private TLRPC.Document documentAttach;
    private int documentAttachType;
    private boolean drawBackground = true;
    private boolean drawForwardedName;
    private boolean drawImageButton;
    /* access modifiers changed from: private */
    public boolean drawInstantView;
    /* access modifiers changed from: private */
    public int drawInstantViewType;
    private boolean drawJoinChannelView;
    private boolean drawJoinGroupView;
    private boolean drawName;
    private boolean drawNameLayout;
    private boolean drawPhotoCheckBox;
    private boolean drawPhotoImage;
    private boolean drawPinnedBottom;
    private boolean drawPinnedTop;
    private boolean drawRadialCheckBackground;
    /* access modifiers changed from: private */
    public boolean drawSelectionBackground;
    /* access modifiers changed from: private */
    public boolean drawShareButton;
    private boolean drawTime = true;
    private boolean drawVideoImageButton;
    private boolean drawVideoSize;
    private boolean drwaShareGoIcon;
    private StaticLayout durationLayout;
    private int durationWidth;
    private boolean firstCircleLength;
    private int firstVisibleBlockNum;
    private boolean forceNotDrawTime;
    private boolean forwardBotPressed;
    private boolean forwardName;
    private int forwardNameCenterX;
    private float[] forwardNameOffsetX = new float[2];
    private boolean forwardNamePressed;
    private int forwardNameX;
    private int forwardNameY;
    private StaticLayout[] forwardedNameLayout = new StaticLayout[2];
    private int forwardedNameWidth;
    private boolean fullyDraw;
    private boolean gamePreviewPressed;
    private LinearGradient gradientShader;
    private boolean groupPhotoInvisible;
    private MessageObject.GroupedMessages groupedMessagesToSet;
    private boolean hasEmbed;
    private boolean hasGamePreview;
    private boolean hasInvoicePreview;
    private boolean hasLinkPreview;
    private int hasMiniProgress;
    private boolean hasNewLineForTime;
    private boolean hasOldCaptionPreview;
    private int highlightProgress;
    private int imageBackgroundColor;
    private int imageBackgroundGradientColor;
    private int imageBackgroundGradientRotation = 45;
    private int imageBackgroundSideColor;
    private int imageBackgroundSideWidth;
    private boolean imagePressed;
    private boolean inLayout;
    private StaticLayout infoLayout;
    private int infoWidth;
    private int infoX;
    private boolean instantButtonPressed;
    private RectF instantButtonRect = new RectF();
    private boolean instantPressed;
    private int instantTextLeftX;
    private int instantTextX;
    /* access modifiers changed from: private */
    public StaticLayout instantViewLayout;
    /* access modifiers changed from: private */
    public int instantWidth;
    /* access modifiers changed from: private */
    public Runnable invalidateRunnable = new Runnable() {
        public void run() {
            ChatMessageCell.this.checkLocationExpired();
            if (ChatMessageCell.this.locationExpired) {
                ChatMessageCell.this.invalidate();
                boolean unused = ChatMessageCell.this.scheduledInvalidate = false;
                return;
            }
            ChatMessageCell chatMessageCell = ChatMessageCell.this;
            chatMessageCell.invalidate(((int) chatMessageCell.rect.left) - 5, ((int) ChatMessageCell.this.rect.top) - 5, ((int) ChatMessageCell.this.rect.right) + 5, ((int) ChatMessageCell.this.rect.bottom) + 5);
            if (ChatMessageCell.this.scheduledInvalidate) {
                AndroidUtilities.runOnUIThread(ChatMessageCell.this.invalidateRunnable, 1000);
            }
        }
    };
    private boolean invalidatesParent;
    private boolean isAvatarVisible;
    public boolean isChat;
    private boolean isCheckPressed = true;
    private boolean isHighlighted;
    private boolean isHighlightedAnimated;
    public boolean isMegagroup;
    private boolean isPressed;
    private boolean isSmallImage;
    private int keyboardHeight;
    private long lastAnimationTime;
    private long lastCheckBoxAnimationTime;
    private long lastControlsAlphaChangeTime;
    private int lastDeleteDate;
    private int lastHeight;
    private long lastHighlightProgressTime;
    /* access modifiers changed from: private */
    public TLRPC.TL_poll lastPoll;
    private ArrayList<TLRPC.TL_pollAnswerVoters> lastPollResults;
    private int lastPollResultsVoters;
    private TLRPC.TL_messageReactions lastReactions;
    private int lastSendState;
    private int lastTime;
    private float lastTouchX;
    private float lastTouchY;
    private int lastViewsCount;
    private int lastVisibleBlockNum;
    /* access modifiers changed from: private */
    public int layoutHeight;
    private int layoutWidth;
    private int linkBlockNum;
    private int linkPreviewHeight;
    private boolean linkPreviewPressed;
    private int linkSelectionBlockNum;
    /* access modifiers changed from: private */
    public boolean locationExpired;
    private ImageReceiver locationImageReceiver;
    /* access modifiers changed from: private */
    public boolean mediaBackground;
    private int mediaOffsetY;
    private boolean mediaWasInvisible;
    private MessageObject messageObjectToSet;
    private int miniButtonPressed;
    private int miniButtonState;
    private StaticLayout nameLayout;
    private float nameOffsetX;
    private int nameWidth;
    private float nameX;
    private float nameY;
    private int namesOffset;
    private boolean needNewVisiblePart;
    private boolean needReplyImage;
    private int noSoundCenterX;
    private boolean otherPressed;
    private int otherX;
    private int otherY;
    private int parentHeight;
    private StaticLayout performerLayout;
    private int performerX;
    private CheckBoxBase photoCheckBox;
    /* access modifiers changed from: private */
    public ImageReceiver photoImage;
    private boolean photoNotSet;
    private TLObject photoParentObject;
    private StaticLayout photosCountLayout;
    private int photosCountWidth;
    private boolean pinnedBottom;
    private boolean pinnedTop;
    private float pollAnimationProgress;
    private float pollAnimationProgressTime;
    /* access modifiers changed from: private */
    public ArrayList<PollButton> pollButtons = new ArrayList<>();
    private boolean pollClosed;
    private boolean pollUnvoteInProgress;
    private boolean pollVoteInProgress;
    private int pollVoteInProgressNum;
    /* access modifiers changed from: private */
    public boolean pollVoted;
    private int pressedBotButton;
    private CharacterStyle pressedLink;
    private int pressedLinkType;
    private int[] pressedState = {16842910, 16842919};
    private int pressedVoteButton;
    private RadialProgress2 radialProgress;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    private ImageReceiver replyImageReceiver;
    /* access modifiers changed from: private */
    public StaticLayout replyNameLayout;
    private float replyNameOffset;
    /* access modifiers changed from: private */
    public int replyNameWidth;
    private boolean replyPressed;
    /* access modifiers changed from: private */
    public int replyStartX;
    /* access modifiers changed from: private */
    public int replyStartY;
    /* access modifiers changed from: private */
    public StaticLayout replyTextLayout;
    private float replyTextOffset;
    /* access modifiers changed from: private */
    public int replyTextWidth;
    private RoundVideoPlayingDrawable roundVideoPlayingDrawable;
    /* access modifiers changed from: private */
    public boolean scheduledInvalidate;
    private Rect scrollRect = new Rect();
    private SeekBar seekBar;
    private SeekBarWaveform seekBarWaveform;
    private int seekBarX;
    private int seekBarY;
    private float selectedBackgroundProgress;
    private Drawable selectorDrawable;
    /* access modifiers changed from: private */
    public int selectorDrawableMaskType;
    private boolean sharePressed;
    /* access modifiers changed from: private */
    public int shareStartX;
    /* access modifiers changed from: private */
    public int shareStartY;
    private StaticLayout siteNameLayout;
    private boolean siteNameRtl;
    private int siteNameWidth;
    private StaticLayout songLayout;
    private int songX;
    private int substractBackgroundHeight;
    /* access modifiers changed from: private */
    public int textX;
    /* access modifiers changed from: private */
    public int textY;
    private float timeAlpha = 1.0f;
    private int timeAudioX;
    private StaticLayout timeLayout;
    private int timeTextWidth;
    private boolean timeWasInvisible;
    private int timeWidth;
    private int timeWidthAudio;
    private int timeX;
    private StaticLayout titleLayout;
    private int titleX;
    private boolean topNearToSet;
    private long totalChangeTime;
    private int totalHeight;
    private int totalVisibleBlocksCount;
    private int unmovedTextX;
    private ArrayList<LinkPath> urlPath = new ArrayList<>();
    private ArrayList<LinkPath> urlPathCache = new ArrayList<>();
    private ArrayList<LinkPath> urlPathSelection = new ArrayList<>();
    private boolean useSeekBarWaweform;
    private int viaNameWidth;
    private TypefaceSpan viaSpan1;
    private TypefaceSpan viaSpan2;
    private int viaWidth;
    private int videoButtonPressed;
    private int videoButtonX;
    private int videoButtonY;
    private StaticLayout videoInfoLayout;
    private RadialProgress2 videoRadialProgress;
    private StaticLayout viewsLayout;
    private int viewsTextWidth;
    private float voteCurrentCircleLength;
    private float voteCurrentProgressTime;
    private long voteLastUpdateTime;
    private float voteRadOffset;
    private boolean voteRisingCircleLength;
    private boolean wasLayout;
    private boolean wasSending;
    private int widthBeforeNewTimeLine;
    /* access modifiers changed from: private */
    public int widthForButtons;

    public interface ChatMessageCellDelegate {

        /* renamed from: org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$canPerformActions(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$didLongPress(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressBotButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
            }

            public static void $default$didPressCancelSendButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressChannelAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
            }

            public static void $default$didPressHiddenForward(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressImage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressInstantButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressOther(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressReaction(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount) {
            }

            public static void $default$didPressReplyMessage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressShare(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressUrl(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
            }

            public static void $default$didPressUserAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
            }

            public static void $default$didPressViaBot(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, String str) {
            }

            public static void $default$didPressVoteButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.TL_pollAnswer tL_pollAnswer) {
            }

            public static void $default$didStartVideoStream(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
            }

            public static String $default$getAdminRank(ChatMessageCellDelegate chatMessageCellDelegate, int i) {
                return null;
            }

            public static TextSelectionHelper.ChatListTextSelectionHelper $default$getTextSelectionHelper(ChatMessageCellDelegate chatMessageCellDelegate) {
                return null;
            }

            public static boolean $default$hasSelectedMessages(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$needOpenWebView(ChatMessageCellDelegate chatMessageCellDelegate, String str, String str2, String str3, String str4, int i, int i2) {
            }

            public static boolean $default$needPlayMessage(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
                return false;
            }

            public static void $default$setShouldNotRepeatSticker(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
            }

            public static boolean $default$shouldRepeatSticker(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
                return true;
            }

            public static void $default$videoTimerReached(ChatMessageCellDelegate chatMessageCellDelegate) {
            }
        }

        boolean canPerformActions();

        void didLongPress(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton);

        void didPressCancelSendButton(ChatMessageCell chatMessageCell);

        void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2);

        void didPressHiddenForward(ChatMessageCell chatMessageCell);

        void didPressImage(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressInstantButton(ChatMessageCell chatMessageCell, int i);

        void didPressOther(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount);

        void didPressReplyMessage(ChatMessageCell chatMessageCell, int i);

        void didPressShare(ChatMessageCell chatMessageCell);

        void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z);

        void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2);

        void didPressViaBot(ChatMessageCell chatMessageCell, String str);

        void didPressVoteButton(ChatMessageCell chatMessageCell, TLRPC.TL_pollAnswer tL_pollAnswer);

        void didStartVideoStream(MessageObject messageObject);

        String getAdminRank(int i);

        TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper();

        boolean hasSelectedMessages();

        void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2);

        boolean needPlayMessage(MessageObject messageObject);

        void setShouldNotRepeatSticker(MessageObject messageObject);

        boolean shouldRepeatSticker(MessageObject messageObject);

        void videoTimerReached();
    }

    private boolean intersect(float f, float f2, float f3, float f4) {
        return f <= f3 ? f2 >= f3 : f <= f4;
    }

    public /* synthetic */ void onSeekBarContinuousDrag(float f) {
        SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
    }

    private class BotButton {
        /* access modifiers changed from: private */
        public int angle;
        /* access modifiers changed from: private */
        public TLRPC.KeyboardButton button;
        /* access modifiers changed from: private */
        public int height;
        /* access modifiers changed from: private */
        public long lastUpdateTime;
        /* access modifiers changed from: private */
        public float progressAlpha;
        /* access modifiers changed from: private */
        public TLRPC.TL_reactionCount reaction;
        /* access modifiers changed from: private */
        public StaticLayout title;
        /* access modifiers changed from: private */
        public int width;
        /* access modifiers changed from: private */
        public int x;
        /* access modifiers changed from: private */
        public int y;

        private BotButton() {
        }
    }

    private class PollButton {
        /* access modifiers changed from: private */
        public TLRPC.TL_pollAnswer answer;
        /* access modifiers changed from: private */
        public float decimal;
        /* access modifiers changed from: private */
        public int height;
        /* access modifiers changed from: private */
        public int percent;
        /* access modifiers changed from: private */
        public float percentProgress;
        /* access modifiers changed from: private */
        public int prevPercent;
        /* access modifiers changed from: private */
        public float prevPercentProgress;
        /* access modifiers changed from: private */
        public StaticLayout title;
        /* access modifiers changed from: private */
        public int x;
        /* access modifiers changed from: private */
        public int y;

        private PollButton() {
        }
    }

    public ChatMessageCell(Context context) {
        super(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.replyImageReceiver = new ImageReceiver(this);
        this.locationImageReceiver = new ImageReceiver(this);
        this.locationImageReceiver.setRoundRadius(AndroidUtilities.dp(26.1f));
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        this.contactAvatarDrawable = new AvatarDrawable();
        this.photoImage = new ImageReceiver(this);
        this.photoImage.setDelegate(this);
        this.radialProgress = new RadialProgress2(this);
        this.videoRadialProgress = new RadialProgress2(this);
        this.videoRadialProgress.setDrawBackground(false);
        this.videoRadialProgress.setCircleRadius(AndroidUtilities.dp(15.0f));
        this.seekBar = new SeekBar(context);
        this.seekBar.setDelegate(this);
        this.seekBarWaveform = new SeekBarWaveform(context);
        this.seekBarWaveform.setDelegate(this);
        this.seekBarWaveform.setParentView(this);
        this.roundVideoPlayingDrawable = new RoundVideoPlayingDrawable(this);
    }

    private void resetPressedLink(int i) {
        if (this.pressedLink == null) {
            return;
        }
        if (this.pressedLinkType == i || i == -1) {
            resetUrlPaths(false);
            this.pressedLink = null;
            this.pressedLinkType = -1;
            invalidate();
        }
    }

    private void resetUrlPaths(boolean z) {
        if (z) {
            if (!this.urlPathSelection.isEmpty()) {
                this.urlPathCache.addAll(this.urlPathSelection);
                this.urlPathSelection.clear();
            }
        } else if (!this.urlPath.isEmpty()) {
            this.urlPathCache.addAll(this.urlPath);
            this.urlPath.clear();
        }
    }

    private LinkPath obtainNewUrlPath(boolean z) {
        LinkPath linkPath;
        if (!this.urlPathCache.isEmpty()) {
            linkPath = this.urlPathCache.get(0);
            this.urlPathCache.remove(0);
        } else {
            linkPath = new LinkPath();
        }
        linkPath.reset();
        if (z) {
            this.urlPathSelection.add(linkPath);
        } else {
            this.urlPath.add(linkPath);
        }
        return linkPath;
    }

    /* access modifiers changed from: private */
    public int[] getRealSpanStartAndEnd(Spannable spannable, CharacterStyle characterStyle) {
        int i;
        int i2;
        boolean z;
        TextStyleSpan.TextStyleRun style;
        TLRPC.MessageEntity messageEntity;
        if (!(characterStyle instanceof URLSpanBrowser) || (style = ((URLSpanBrowser) characterStyle).getStyle()) == null || (messageEntity = style.urlEntity) == null) {
            z = false;
            i2 = 0;
            i = 0;
        } else {
            i2 = messageEntity.offset;
            i = messageEntity.length + i2;
            z = true;
        }
        if (!z) {
            i2 = spannable.getSpanStart(characterStyle);
            i = spannable.getSpanEnd(characterStyle);
        }
        return new int[]{i2, i};
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x00d6 A[Catch:{ Exception -> 0x01f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00e9 A[Catch:{ Exception -> 0x01f1 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkTextBlockMotionEvent(android.view.MotionEvent r14) {
        /*
            r13 = this;
            org.telegram.messenger.MessageObject r0 = r13.currentMessageObject
            int r1 = r0.type
            r2 = 0
            if (r1 != 0) goto L_0x01f9
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r0.textLayoutBlocks
            if (r0 == 0) goto L_0x01f9
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01f9
            org.telegram.messenger.MessageObject r0 = r13.currentMessageObject
            java.lang.CharSequence r0 = r0.messageText
            boolean r0 = r0 instanceof android.text.Spannable
            if (r0 != 0) goto L_0x001b
            goto L_0x01f9
        L_0x001b:
            int r0 = r14.getAction()
            r1 = 1
            if (r0 == 0) goto L_0x002c
            int r0 = r14.getAction()
            if (r0 != r1) goto L_0x01f9
            int r0 = r13.pressedLinkType
            if (r0 != r1) goto L_0x01f9
        L_0x002c:
            float r0 = r14.getX()
            int r0 = (int) r0
            float r3 = r14.getY()
            int r3 = (int) r3
            int r4 = r13.textX
            if (r0 < r4) goto L_0x01f6
            int r5 = r13.textY
            if (r3 < r5) goto L_0x01f6
            org.telegram.messenger.MessageObject r6 = r13.currentMessageObject
            int r7 = r6.textWidth
            int r4 = r4 + r7
            if (r0 > r4) goto L_0x01f6
            int r4 = r6.textHeight
            int r4 = r4 + r5
            if (r3 > r4) goto L_0x01f6
            int r3 = r3 - r5
            r4 = 0
            r5 = 0
        L_0x004d:
            org.telegram.messenger.MessageObject r6 = r13.currentMessageObject
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r6 = r6.textLayoutBlocks
            int r6 = r6.size()
            if (r4 >= r6) goto L_0x006f
            org.telegram.messenger.MessageObject r6 = r13.currentMessageObject
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r6 = r6.textLayoutBlocks
            java.lang.Object r6 = r6.get(r4)
            org.telegram.messenger.MessageObject$TextLayoutBlock r6 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r6
            float r6 = r6.textYOffset
            float r7 = (float) r3
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 <= 0) goto L_0x0069
            goto L_0x006f
        L_0x0069:
            int r5 = r4 + 1
            r12 = r5
            r5 = r4
            r4 = r12
            goto L_0x004d
        L_0x006f:
            org.telegram.messenger.MessageObject r4 = r13.currentMessageObject     // Catch:{ Exception -> 0x01f1 }
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r4 = r4.textLayoutBlocks     // Catch:{ Exception -> 0x01f1 }
            java.lang.Object r4 = r4.get(r5)     // Catch:{ Exception -> 0x01f1 }
            org.telegram.messenger.MessageObject$TextLayoutBlock r4 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r4     // Catch:{ Exception -> 0x01f1 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x01f1 }
            int r6 = r13.textX     // Catch:{ Exception -> 0x01f1 }
            float r6 = (float) r6     // Catch:{ Exception -> 0x01f1 }
            boolean r7 = r4.isRtl()     // Catch:{ Exception -> 0x01f1 }
            r8 = 0
            if (r7 == 0) goto L_0x0089
            org.telegram.messenger.MessageObject r7 = r13.currentMessageObject     // Catch:{ Exception -> 0x01f1 }
            float r7 = r7.textXOffset     // Catch:{ Exception -> 0x01f1 }
            goto L_0x008a
        L_0x0089:
            r7 = 0
        L_0x008a:
            float r6 = r6 - r7
            float r0 = r0 - r6
            int r0 = (int) r0     // Catch:{ Exception -> 0x01f1 }
            float r3 = (float) r3     // Catch:{ Exception -> 0x01f1 }
            float r6 = r4.textYOffset     // Catch:{ Exception -> 0x01f1 }
            float r3 = r3 - r6
            int r3 = (int) r3     // Catch:{ Exception -> 0x01f1 }
            android.text.StaticLayout r6 = r4.textLayout     // Catch:{ Exception -> 0x01f1 }
            int r3 = r6.getLineForVertical(r3)     // Catch:{ Exception -> 0x01f1 }
            android.text.StaticLayout r6 = r4.textLayout     // Catch:{ Exception -> 0x01f1 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x01f1 }
            int r6 = r6.getOffsetForHorizontal(r3, r0)     // Catch:{ Exception -> 0x01f1 }
            android.text.StaticLayout r7 = r4.textLayout     // Catch:{ Exception -> 0x01f1 }
            float r7 = r7.getLineLeft(r3)     // Catch:{ Exception -> 0x01f1 }
            int r9 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r9 > 0) goto L_0x01f9
            android.text.StaticLayout r9 = r4.textLayout     // Catch:{ Exception -> 0x01f1 }
            float r3 = r9.getLineWidth(r3)     // Catch:{ Exception -> 0x01f1 }
            float r7 = r7 + r3
            int r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x01f9
            org.telegram.messenger.MessageObject r0 = r13.currentMessageObject     // Catch:{ Exception -> 0x01f1 }
            java.lang.CharSequence r0 = r0.messageText     // Catch:{ Exception -> 0x01f1 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x01f1 }
            java.lang.Class<android.text.style.ClickableSpan> r3 = android.text.style.ClickableSpan.class
            java.lang.Object[] r3 = r0.getSpans(r6, r6, r3)     // Catch:{ Exception -> 0x01f1 }
            android.text.style.CharacterStyle[] r3 = (android.text.style.CharacterStyle[]) r3     // Catch:{ Exception -> 0x01f1 }
            if (r3 == 0) goto L_0x00ca
            int r7 = r3.length     // Catch:{ Exception -> 0x01f1 }
            if (r7 != 0) goto L_0x00c8
            goto L_0x00ca
        L_0x00c8:
            r6 = 0
            goto L_0x00d3
        L_0x00ca:
            java.lang.Class<org.telegram.ui.Components.URLSpanMono> r3 = org.telegram.ui.Components.URLSpanMono.class
            java.lang.Object[] r3 = r0.getSpans(r6, r6, r3)     // Catch:{ Exception -> 0x01f1 }
            android.text.style.CharacterStyle[] r3 = (android.text.style.CharacterStyle[]) r3     // Catch:{ Exception -> 0x01f1 }
            r6 = 1
        L_0x00d3:
            int r7 = r3.length     // Catch:{ Exception -> 0x01f1 }
            if (r7 == 0) goto L_0x00e6
            int r7 = r3.length     // Catch:{ Exception -> 0x01f1 }
            if (r7 == 0) goto L_0x00e4
            r7 = r3[r2]     // Catch:{ Exception -> 0x01f1 }
            boolean r7 = r7 instanceof org.telegram.ui.Components.URLSpanBotCommand     // Catch:{ Exception -> 0x01f1 }
            if (r7 == 0) goto L_0x00e4
            boolean r7 = org.telegram.ui.Components.URLSpanBotCommand.enabled     // Catch:{ Exception -> 0x01f1 }
            if (r7 != 0) goto L_0x00e4
            goto L_0x00e6
        L_0x00e4:
            r7 = 0
            goto L_0x00e7
        L_0x00e6:
            r7 = 1
        L_0x00e7:
            if (r7 != 0) goto L_0x01f9
            int r14 = r14.getAction()     // Catch:{ Exception -> 0x01f1 }
            if (r14 != 0) goto L_0x01e0
            r14 = r3[r2]     // Catch:{ Exception -> 0x01f1 }
            r13.pressedLink = r14     // Catch:{ Exception -> 0x01f1 }
            r13.linkBlockNum = r5     // Catch:{ Exception -> 0x01f1 }
            r13.pressedLinkType = r1     // Catch:{ Exception -> 0x01f1 }
            r13.resetUrlPaths(r2)     // Catch:{ Exception -> 0x01f1 }
            org.telegram.ui.Components.LinkPath r14 = r13.obtainNewUrlPath(r2)     // Catch:{ Exception -> 0x01d8 }
            android.text.style.CharacterStyle r3 = r13.pressedLink     // Catch:{ Exception -> 0x01d8 }
            int[] r3 = r13.getRealSpanStartAndEnd(r0, r3)     // Catch:{ Exception -> 0x01d8 }
            android.text.StaticLayout r7 = r4.textLayout     // Catch:{ Exception -> 0x01d8 }
            r9 = r3[r2]     // Catch:{ Exception -> 0x01d8 }
            r14.setCurrentLayout(r7, r9, r8)     // Catch:{ Exception -> 0x01d8 }
            android.text.StaticLayout r7 = r4.textLayout     // Catch:{ Exception -> 0x01d8 }
            r8 = r3[r2]     // Catch:{ Exception -> 0x01d8 }
            r9 = r3[r1]     // Catch:{ Exception -> 0x01d8 }
            r7.getSelectionPath(r8, r9, r14)     // Catch:{ Exception -> 0x01d8 }
            r14 = r3[r1]     // Catch:{ Exception -> 0x01d8 }
            int r7 = r4.charactersEnd     // Catch:{ Exception -> 0x01d8 }
            if (r14 < r7) goto L_0x0177
            int r14 = r5 + 1
        L_0x011c:
            org.telegram.messenger.MessageObject r7 = r13.currentMessageObject     // Catch:{ Exception -> 0x01d8 }
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r7 = r7.textLayoutBlocks     // Catch:{ Exception -> 0x01d8 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x01d8 }
            if (r14 >= r7) goto L_0x0177
            org.telegram.messenger.MessageObject r7 = r13.currentMessageObject     // Catch:{ Exception -> 0x01d8 }
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r7 = r7.textLayoutBlocks     // Catch:{ Exception -> 0x01d8 }
            java.lang.Object r7 = r7.get(r14)     // Catch:{ Exception -> 0x01d8 }
            org.telegram.messenger.MessageObject$TextLayoutBlock r7 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r7     // Catch:{ Exception -> 0x01d8 }
            if (r6 == 0) goto L_0x013f
            int r8 = r7.charactersOffset     // Catch:{ Exception -> 0x01d8 }
            int r9 = r7.charactersOffset     // Catch:{ Exception -> 0x01d8 }
            java.lang.Class<org.telegram.ui.Components.URLSpanMono> r10 = org.telegram.ui.Components.URLSpanMono.class
            java.lang.Object[] r8 = r0.getSpans(r8, r9, r10)     // Catch:{ Exception -> 0x01d8 }
            android.text.style.CharacterStyle[] r8 = (android.text.style.CharacterStyle[]) r8     // Catch:{ Exception -> 0x01d8 }
            goto L_0x014b
        L_0x013f:
            int r8 = r7.charactersOffset     // Catch:{ Exception -> 0x01d8 }
            int r9 = r7.charactersOffset     // Catch:{ Exception -> 0x01d8 }
            java.lang.Class<android.text.style.ClickableSpan> r10 = android.text.style.ClickableSpan.class
            java.lang.Object[] r8 = r0.getSpans(r8, r9, r10)     // Catch:{ Exception -> 0x01d8 }
            android.text.style.CharacterStyle[] r8 = (android.text.style.CharacterStyle[]) r8     // Catch:{ Exception -> 0x01d8 }
        L_0x014b:
            if (r8 == 0) goto L_0x0177
            int r9 = r8.length     // Catch:{ Exception -> 0x01d8 }
            if (r9 == 0) goto L_0x0177
            r8 = r8[r2]     // Catch:{ Exception -> 0x01d8 }
            android.text.style.CharacterStyle r9 = r13.pressedLink     // Catch:{ Exception -> 0x01d8 }
            if (r8 == r9) goto L_0x0157
            goto L_0x0177
        L_0x0157:
            org.telegram.ui.Components.LinkPath r8 = r13.obtainNewUrlPath(r2)     // Catch:{ Exception -> 0x01d8 }
            android.text.StaticLayout r9 = r7.textLayout     // Catch:{ Exception -> 0x01d8 }
            float r10 = r7.textYOffset     // Catch:{ Exception -> 0x01d8 }
            float r11 = r4.textYOffset     // Catch:{ Exception -> 0x01d8 }
            float r10 = r10 - r11
            r8.setCurrentLayout(r9, r2, r10)     // Catch:{ Exception -> 0x01d8 }
            android.text.StaticLayout r9 = r7.textLayout     // Catch:{ Exception -> 0x01d8 }
            r10 = r3[r1]     // Catch:{ Exception -> 0x01d8 }
            r9.getSelectionPath(r2, r10, r8)     // Catch:{ Exception -> 0x01d8 }
            r8 = r3[r1]     // Catch:{ Exception -> 0x01d8 }
            int r7 = r7.charactersEnd     // Catch:{ Exception -> 0x01d8 }
            int r7 = r7 - r1
            if (r8 >= r7) goto L_0x0174
            goto L_0x0177
        L_0x0174:
            int r14 = r14 + 1
            goto L_0x011c
        L_0x0177:
            r14 = r3[r2]     // Catch:{ Exception -> 0x01d8 }
            int r4 = r4.charactersOffset     // Catch:{ Exception -> 0x01d8 }
            if (r14 > r4) goto L_0x01dc
            int r5 = r5 - r1
            r14 = 0
        L_0x017f:
            if (r5 < 0) goto L_0x01dc
            org.telegram.messenger.MessageObject r4 = r13.currentMessageObject     // Catch:{ Exception -> 0x01d8 }
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r4 = r4.textLayoutBlocks     // Catch:{ Exception -> 0x01d8 }
            java.lang.Object r4 = r4.get(r5)     // Catch:{ Exception -> 0x01d8 }
            org.telegram.messenger.MessageObject$TextLayoutBlock r4 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r4     // Catch:{ Exception -> 0x01d8 }
            if (r6 == 0) goto L_0x019c
            int r7 = r4.charactersEnd     // Catch:{ Exception -> 0x01d8 }
            int r7 = r7 - r1
            int r8 = r4.charactersEnd     // Catch:{ Exception -> 0x01d8 }
            int r8 = r8 - r1
            java.lang.Class<org.telegram.ui.Components.URLSpanMono> r9 = org.telegram.ui.Components.URLSpanMono.class
            java.lang.Object[] r7 = r0.getSpans(r7, r8, r9)     // Catch:{ Exception -> 0x01d8 }
            android.text.style.CharacterStyle[] r7 = (android.text.style.CharacterStyle[]) r7     // Catch:{ Exception -> 0x01d8 }
            goto L_0x01aa
        L_0x019c:
            int r7 = r4.charactersEnd     // Catch:{ Exception -> 0x01d8 }
            int r7 = r7 - r1
            int r8 = r4.charactersEnd     // Catch:{ Exception -> 0x01d8 }
            int r8 = r8 - r1
            java.lang.Class<android.text.style.ClickableSpan> r9 = android.text.style.ClickableSpan.class
            java.lang.Object[] r7 = r0.getSpans(r7, r8, r9)     // Catch:{ Exception -> 0x01d8 }
            android.text.style.CharacterStyle[] r7 = (android.text.style.CharacterStyle[]) r7     // Catch:{ Exception -> 0x01d8 }
        L_0x01aa:
            if (r7 == 0) goto L_0x01dc
            int r8 = r7.length     // Catch:{ Exception -> 0x01d8 }
            if (r8 == 0) goto L_0x01dc
            r7 = r7[r2]     // Catch:{ Exception -> 0x01d8 }
            android.text.style.CharacterStyle r8 = r13.pressedLink     // Catch:{ Exception -> 0x01d8 }
            if (r7 == r8) goto L_0x01b6
            goto L_0x01dc
        L_0x01b6:
            org.telegram.ui.Components.LinkPath r7 = r13.obtainNewUrlPath(r2)     // Catch:{ Exception -> 0x01d8 }
            int r8 = r4.height     // Catch:{ Exception -> 0x01d8 }
            int r14 = r14 - r8
            android.text.StaticLayout r8 = r4.textLayout     // Catch:{ Exception -> 0x01d8 }
            r9 = r3[r2]     // Catch:{ Exception -> 0x01d8 }
            float r10 = (float) r14     // Catch:{ Exception -> 0x01d8 }
            r7.setCurrentLayout(r8, r9, r10)     // Catch:{ Exception -> 0x01d8 }
            android.text.StaticLayout r8 = r4.textLayout     // Catch:{ Exception -> 0x01d8 }
            r9 = r3[r2]     // Catch:{ Exception -> 0x01d8 }
            r10 = r3[r1]     // Catch:{ Exception -> 0x01d8 }
            r8.getSelectionPath(r9, r10, r7)     // Catch:{ Exception -> 0x01d8 }
            r7 = r3[r2]     // Catch:{ Exception -> 0x01d8 }
            int r4 = r4.charactersOffset     // Catch:{ Exception -> 0x01d8 }
            if (r7 <= r4) goto L_0x01d5
            goto L_0x01dc
        L_0x01d5:
            int r5 = r5 + -1
            goto L_0x017f
        L_0x01d8:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)     // Catch:{ Exception -> 0x01f1 }
        L_0x01dc:
            r13.invalidate()     // Catch:{ Exception -> 0x01f1 }
            return r1
        L_0x01e0:
            r14 = r3[r2]     // Catch:{ Exception -> 0x01f1 }
            android.text.style.CharacterStyle r0 = r13.pressedLink     // Catch:{ Exception -> 0x01f1 }
            if (r14 != r0) goto L_0x01f9
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r14 = r13.delegate     // Catch:{ Exception -> 0x01f1 }
            android.text.style.CharacterStyle r0 = r13.pressedLink     // Catch:{ Exception -> 0x01f1 }
            r14.didPressUrl(r13, r0, r2)     // Catch:{ Exception -> 0x01f1 }
            r13.resetPressedLink(r1)     // Catch:{ Exception -> 0x01f1 }
            return r1
        L_0x01f1:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
            goto L_0x01f9
        L_0x01f6:
            r13.resetPressedLink(r1)
        L_0x01f9:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkTextBlockMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x009c A[Catch:{ Exception -> 0x00dc }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkCaptionMotionEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            java.lang.CharSequence r0 = r7.currentCaption
            boolean r0 = r0 instanceof android.text.Spannable
            r1 = 0
            if (r0 == 0) goto L_0x00f3
            android.text.StaticLayout r0 = r7.captionLayout
            if (r0 != 0) goto L_0x000d
            goto L_0x00f3
        L_0x000d:
            int r0 = r8.getAction()
            r2 = 1
            if (r0 == 0) goto L_0x0022
            boolean r0 = r7.linkPreviewPressed
            if (r0 != 0) goto L_0x001c
            android.text.style.CharacterStyle r0 = r7.pressedLink
            if (r0 == 0) goto L_0x00f3
        L_0x001c:
            int r0 = r8.getAction()
            if (r0 != r2) goto L_0x00f3
        L_0x0022:
            float r0 = r8.getX()
            int r0 = (int) r0
            float r3 = r8.getY()
            int r3 = (int) r3
            int r4 = r7.captionX
            r5 = 3
            if (r0 < r4) goto L_0x00f0
            int r6 = r7.captionWidth
            int r4 = r4 + r6
            if (r0 > r4) goto L_0x00f0
            int r4 = r7.captionY
            if (r3 < r4) goto L_0x00f0
            int r6 = r7.captionHeight
            int r4 = r4 + r6
            if (r3 > r4) goto L_0x00f0
            int r8 = r8.getAction()
            if (r8 != 0) goto L_0x00e1
            int r8 = r7.captionX     // Catch:{ Exception -> 0x00dc }
            int r0 = r0 - r8
            int r8 = r7.captionY     // Catch:{ Exception -> 0x00dc }
            int r3 = r3 - r8
            android.text.StaticLayout r8 = r7.captionLayout     // Catch:{ Exception -> 0x00dc }
            int r8 = r8.getLineForVertical(r3)     // Catch:{ Exception -> 0x00dc }
            android.text.StaticLayout r3 = r7.captionLayout     // Catch:{ Exception -> 0x00dc }
            float r0 = (float) r0     // Catch:{ Exception -> 0x00dc }
            int r3 = r3.getOffsetForHorizontal(r8, r0)     // Catch:{ Exception -> 0x00dc }
            android.text.StaticLayout r4 = r7.captionLayout     // Catch:{ Exception -> 0x00dc }
            float r4 = r4.getLineLeft(r8)     // Catch:{ Exception -> 0x00dc }
            int r6 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r6 > 0) goto L_0x00f3
            android.text.StaticLayout r6 = r7.captionLayout     // Catch:{ Exception -> 0x00dc }
            float r8 = r6.getLineWidth(r8)     // Catch:{ Exception -> 0x00dc }
            float r4 = r4 + r8
            int r8 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r8 < 0) goto L_0x00f3
            java.lang.CharSequence r8 = r7.currentCaption     // Catch:{ Exception -> 0x00dc }
            android.text.Spannable r8 = (android.text.Spannable) r8     // Catch:{ Exception -> 0x00dc }
            java.lang.Class<android.text.style.ClickableSpan> r0 = android.text.style.ClickableSpan.class
            java.lang.Object[] r0 = r8.getSpans(r3, r3, r0)     // Catch:{ Exception -> 0x00dc }
            android.text.style.CharacterStyle[] r0 = (android.text.style.CharacterStyle[]) r0     // Catch:{ Exception -> 0x00dc }
            if (r0 == 0) goto L_0x007e
            int r4 = r0.length     // Catch:{ Exception -> 0x00dc }
            if (r4 != 0) goto L_0x0086
        L_0x007e:
            java.lang.Class<org.telegram.ui.Components.URLSpanMono> r0 = org.telegram.ui.Components.URLSpanMono.class
            java.lang.Object[] r0 = r8.getSpans(r3, r3, r0)     // Catch:{ Exception -> 0x00dc }
            android.text.style.CharacterStyle[] r0 = (android.text.style.CharacterStyle[]) r0     // Catch:{ Exception -> 0x00dc }
        L_0x0086:
            int r3 = r0.length     // Catch:{ Exception -> 0x00dc }
            if (r3 == 0) goto L_0x0099
            int r3 = r0.length     // Catch:{ Exception -> 0x00dc }
            if (r3 == 0) goto L_0x0097
            r3 = r0[r1]     // Catch:{ Exception -> 0x00dc }
            boolean r3 = r3 instanceof org.telegram.ui.Components.URLSpanBotCommand     // Catch:{ Exception -> 0x00dc }
            if (r3 == 0) goto L_0x0097
            boolean r3 = org.telegram.ui.Components.URLSpanBotCommand.enabled     // Catch:{ Exception -> 0x00dc }
            if (r3 != 0) goto L_0x0097
            goto L_0x0099
        L_0x0097:
            r3 = 0
            goto L_0x009a
        L_0x0099:
            r3 = 1
        L_0x009a:
            if (r3 != 0) goto L_0x00f3
            r0 = r0[r1]     // Catch:{ Exception -> 0x00dc }
            r7.pressedLink = r0     // Catch:{ Exception -> 0x00dc }
            r7.pressedLinkType = r5     // Catch:{ Exception -> 0x00dc }
            r7.resetUrlPaths(r1)     // Catch:{ Exception -> 0x00dc }
            org.telegram.ui.Components.LinkPath r0 = r7.obtainNewUrlPath(r1)     // Catch:{ Exception -> 0x00c1 }
            android.text.style.CharacterStyle r3 = r7.pressedLink     // Catch:{ Exception -> 0x00c1 }
            int[] r8 = r7.getRealSpanStartAndEnd(r8, r3)     // Catch:{ Exception -> 0x00c1 }
            android.text.StaticLayout r3 = r7.captionLayout     // Catch:{ Exception -> 0x00c1 }
            r4 = r8[r1]     // Catch:{ Exception -> 0x00c1 }
            r5 = 0
            r0.setCurrentLayout(r3, r4, r5)     // Catch:{ Exception -> 0x00c1 }
            android.text.StaticLayout r3 = r7.captionLayout     // Catch:{ Exception -> 0x00c1 }
            r4 = r8[r1]     // Catch:{ Exception -> 0x00c1 }
            r8 = r8[r2]     // Catch:{ Exception -> 0x00c1 }
            r3.getSelectionPath(r4, r8, r0)     // Catch:{ Exception -> 0x00c1 }
            goto L_0x00c5
        L_0x00c1:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)     // Catch:{ Exception -> 0x00dc }
        L_0x00c5:
            org.telegram.messenger.MessageObject$GroupedMessages r8 = r7.currentMessagesGroup     // Catch:{ Exception -> 0x00dc }
            if (r8 == 0) goto L_0x00d8
            android.view.ViewParent r8 = r7.getParent()     // Catch:{ Exception -> 0x00dc }
            if (r8 == 0) goto L_0x00d8
            android.view.ViewParent r8 = r7.getParent()     // Catch:{ Exception -> 0x00dc }
            android.view.ViewGroup r8 = (android.view.ViewGroup) r8     // Catch:{ Exception -> 0x00dc }
            r8.invalidate()     // Catch:{ Exception -> 0x00dc }
        L_0x00d8:
            r7.invalidate()     // Catch:{ Exception -> 0x00dc }
            return r2
        L_0x00dc:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            goto L_0x00f3
        L_0x00e1:
            int r8 = r7.pressedLinkType
            if (r8 != r5) goto L_0x00f3
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r8 = r7.delegate
            android.text.style.CharacterStyle r0 = r7.pressedLink
            r8.didPressUrl(r7, r0, r1)
            r7.resetPressedLink(r5)
            return r2
        L_0x00f0:
            r7.resetPressedLink(r5)
        L_0x00f3:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkCaptionMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:0x00bb A[Catch:{ Exception -> 0x00ec }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkGameMotionEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            boolean r0 = r7.hasGamePreview
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            float r0 = r8.getX()
            int r0 = (int) r0
            float r2 = r8.getY()
            int r2 = (int) r2
            int r3 = r8.getAction()
            r4 = 2
            r5 = 1
            if (r3 != 0) goto L_0x00f2
            boolean r8 = r7.drawPhotoImage
            if (r8 == 0) goto L_0x004c
            boolean r8 = r7.drawImageButton
            if (r8 == 0) goto L_0x004c
            int r8 = r7.buttonState
            r3 = -1
            if (r8 == r3) goto L_0x004c
            int r8 = r7.buttonX
            if (r0 < r8) goto L_0x004c
            r3 = 1111490560(0x42400000, float:48.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r8 = r8 + r6
            if (r0 > r8) goto L_0x004c
            int r8 = r7.buttonY
            if (r2 < r8) goto L_0x004c
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r8 = r8 + r3
            if (r2 > r8) goto L_0x004c
            org.telegram.ui.Components.RadialProgress2 r8 = r7.radialProgress
            int r8 = r8.getIcon()
            r3 = 4
            if (r8 == r3) goto L_0x004c
            r7.buttonPressed = r5
            r7.invalidate()
            return r5
        L_0x004c:
            boolean r8 = r7.drawPhotoImage
            if (r8 == 0) goto L_0x005d
            org.telegram.messenger.ImageReceiver r8 = r7.photoImage
            float r3 = (float) r0
            float r6 = (float) r2
            boolean r8 = r8.isInsideImage(r3, r6)
            if (r8 == 0) goto L_0x005d
            r7.gamePreviewPressed = r5
            return r5
        L_0x005d:
            android.text.StaticLayout r8 = r7.descriptionLayout
            if (r8 == 0) goto L_0x0170
            int r8 = r7.descriptionY
            if (r2 < r8) goto L_0x0170
            int r8 = r7.unmovedTextX     // Catch:{ Exception -> 0x00ec }
            r3 = 1092616192(0x41200000, float:10.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x00ec }
            int r8 = r8 + r3
            int r3 = r7.descriptionX     // Catch:{ Exception -> 0x00ec }
            int r8 = r8 + r3
            int r0 = r0 - r8
            int r8 = r7.descriptionY     // Catch:{ Exception -> 0x00ec }
            int r2 = r2 - r8
            android.text.StaticLayout r8 = r7.descriptionLayout     // Catch:{ Exception -> 0x00ec }
            int r8 = r8.getLineForVertical(r2)     // Catch:{ Exception -> 0x00ec }
            android.text.StaticLayout r2 = r7.descriptionLayout     // Catch:{ Exception -> 0x00ec }
            float r0 = (float) r0     // Catch:{ Exception -> 0x00ec }
            int r2 = r2.getOffsetForHorizontal(r8, r0)     // Catch:{ Exception -> 0x00ec }
            android.text.StaticLayout r3 = r7.descriptionLayout     // Catch:{ Exception -> 0x00ec }
            float r3 = r3.getLineLeft(r8)     // Catch:{ Exception -> 0x00ec }
            int r6 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r6 > 0) goto L_0x0170
            android.text.StaticLayout r6 = r7.descriptionLayout     // Catch:{ Exception -> 0x00ec }
            float r8 = r6.getLineWidth(r8)     // Catch:{ Exception -> 0x00ec }
            float r3 = r3 + r8
            int r8 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r8 < 0) goto L_0x0170
            org.telegram.messenger.MessageObject r8 = r7.currentMessageObject     // Catch:{ Exception -> 0x00ec }
            java.lang.CharSequence r8 = r8.linkDescription     // Catch:{ Exception -> 0x00ec }
            android.text.Spannable r8 = (android.text.Spannable) r8     // Catch:{ Exception -> 0x00ec }
            java.lang.Class<android.text.style.ClickableSpan> r0 = android.text.style.ClickableSpan.class
            java.lang.Object[] r0 = r8.getSpans(r2, r2, r0)     // Catch:{ Exception -> 0x00ec }
            android.text.style.ClickableSpan[] r0 = (android.text.style.ClickableSpan[]) r0     // Catch:{ Exception -> 0x00ec }
            int r2 = r0.length     // Catch:{ Exception -> 0x00ec }
            if (r2 == 0) goto L_0x00b8
            int r2 = r0.length     // Catch:{ Exception -> 0x00ec }
            if (r2 == 0) goto L_0x00b6
            r2 = r0[r1]     // Catch:{ Exception -> 0x00ec }
            boolean r2 = r2 instanceof org.telegram.ui.Components.URLSpanBotCommand     // Catch:{ Exception -> 0x00ec }
            if (r2 == 0) goto L_0x00b6
            boolean r2 = org.telegram.ui.Components.URLSpanBotCommand.enabled     // Catch:{ Exception -> 0x00ec }
            if (r2 != 0) goto L_0x00b6
            goto L_0x00b8
        L_0x00b6:
            r2 = 0
            goto L_0x00b9
        L_0x00b8:
            r2 = 1
        L_0x00b9:
            if (r2 != 0) goto L_0x0170
            r0 = r0[r1]     // Catch:{ Exception -> 0x00ec }
            r7.pressedLink = r0     // Catch:{ Exception -> 0x00ec }
            r0 = -10
            r7.linkBlockNum = r0     // Catch:{ Exception -> 0x00ec }
            r7.pressedLinkType = r4     // Catch:{ Exception -> 0x00ec }
            r7.resetUrlPaths(r1)     // Catch:{ Exception -> 0x00ec }
            org.telegram.ui.Components.LinkPath r0 = r7.obtainNewUrlPath(r1)     // Catch:{ Exception -> 0x00e4 }
            android.text.style.CharacterStyle r2 = r7.pressedLink     // Catch:{ Exception -> 0x00e4 }
            int[] r8 = r7.getRealSpanStartAndEnd(r8, r2)     // Catch:{ Exception -> 0x00e4 }
            android.text.StaticLayout r2 = r7.descriptionLayout     // Catch:{ Exception -> 0x00e4 }
            r3 = r8[r1]     // Catch:{ Exception -> 0x00e4 }
            r4 = 0
            r0.setCurrentLayout(r2, r3, r4)     // Catch:{ Exception -> 0x00e4 }
            android.text.StaticLayout r2 = r7.descriptionLayout     // Catch:{ Exception -> 0x00e4 }
            r3 = r8[r1]     // Catch:{ Exception -> 0x00e4 }
            r8 = r8[r5]     // Catch:{ Exception -> 0x00e4 }
            r2.getSelectionPath(r3, r8, r0)     // Catch:{ Exception -> 0x00e4 }
            goto L_0x00e8
        L_0x00e4:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)     // Catch:{ Exception -> 0x00ec }
        L_0x00e8:
            r7.invalidate()     // Catch:{ Exception -> 0x00ec }
            return r5
        L_0x00ec:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            goto L_0x0170
        L_0x00f2:
            int r8 = r8.getAction()
            if (r8 != r5) goto L_0x0170
            int r8 = r7.pressedLinkType
            if (r8 == r4) goto L_0x0109
            boolean r8 = r7.gamePreviewPressed
            if (r8 != 0) goto L_0x0109
            int r8 = r7.buttonPressed
            if (r8 == 0) goto L_0x0105
            goto L_0x0109
        L_0x0105:
            r7.resetPressedLink(r4)
            goto L_0x0170
        L_0x0109:
            int r8 = r7.buttonPressed
            if (r8 == 0) goto L_0x0119
            r7.buttonPressed = r1
            r7.playSoundEffect(r1)
            r7.didPressButton(r5, r1)
            r7.invalidate()
            goto L_0x0170
        L_0x0119:
            android.text.style.CharacterStyle r8 = r7.pressedLink
            if (r8 == 0) goto L_0x013e
            boolean r0 = r8 instanceof android.text.style.URLSpan
            if (r0 == 0) goto L_0x0131
            android.content.Context r8 = r7.getContext()
            android.text.style.CharacterStyle r0 = r7.pressedLink
            android.text.style.URLSpan r0 = (android.text.style.URLSpan) r0
            java.lang.String r0 = r0.getURL()
            org.telegram.messenger.browser.Browser.openUrl((android.content.Context) r8, (java.lang.String) r0)
            goto L_0x013a
        L_0x0131:
            boolean r0 = r8 instanceof android.text.style.ClickableSpan
            if (r0 == 0) goto L_0x013a
            android.text.style.ClickableSpan r8 = (android.text.style.ClickableSpan) r8
            r8.onClick(r7)
        L_0x013a:
            r7.resetPressedLink(r4)
            goto L_0x0170
        L_0x013e:
            r7.gamePreviewPressed = r1
            r8 = 0
        L_0x0141:
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$BotButton> r0 = r7.botButtons
            int r0 = r0.size()
            if (r8 >= r0) goto L_0x016c
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$BotButton> r0 = r7.botButtons
            java.lang.Object r0 = r0.get(r8)
            org.telegram.ui.Cells.ChatMessageCell$BotButton r0 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r0
            org.telegram.tgnet.TLRPC$KeyboardButton r2 = r0.button
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame
            if (r2 == 0) goto L_0x0169
            r7.playSoundEffect(r1)
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r8 = r7.delegate
            org.telegram.tgnet.TLRPC$KeyboardButton r0 = r0.button
            r8.didPressBotButton(r7, r0)
            r7.invalidate()
            goto L_0x016c
        L_0x0169:
            int r8 = r8 + 1
            goto L_0x0141
        L_0x016c:
            r7.resetPressedLink(r4)
            return r5
        L_0x0170:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkGameMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x00b5 A[Catch:{ Exception -> 0x00e9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0120  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkLinkPreviewMotionEvent(android.view.MotionEvent r17) {
        /*
            r16 = this;
            r1 = r16
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            int r0 = r0.type
            r2 = 0
            if (r0 != 0) goto L_0x0388
            boolean r0 = r1.hasLinkPreview
            if (r0 != 0) goto L_0x000f
            goto L_0x0388
        L_0x000f:
            float r0 = r17.getX()
            int r3 = (int) r0
            float r0 = r17.getY()
            int r4 = (int) r0
            int r0 = r1.unmovedTextX
            if (r3 < r0) goto L_0x0388
            int r5 = r1.backgroundWidth
            int r0 = r0 + r5
            if (r3 > r0) goto L_0x0388
            int r0 = r1.textY
            org.telegram.messenger.MessageObject r5 = r1.currentMessageObject
            int r5 = r5.textHeight
            int r6 = r0 + r5
            if (r4 < r6) goto L_0x0388
            int r0 = r0 + r5
            int r5 = r1.linkPreviewHeight
            int r0 = r0 + r5
            boolean r5 = r1.drawInstantView
            if (r5 == 0) goto L_0x0037
            r5 = 46
            goto L_0x0038
        L_0x0037:
            r5 = 0
        L_0x0038:
            int r5 = r5 + 8
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 + r5
            if (r4 > r0) goto L_0x0388
            int r0 = r17.getAction()
            r5 = 21
            r6 = -1
            r7 = 2
            r8 = 1
            if (r0 != 0) goto L_0x01f8
            android.text.StaticLayout r0 = r1.descriptionLayout
            if (r0 == 0) goto L_0x00ed
            int r0 = r1.descriptionY
            if (r4 < r0) goto L_0x00ed
            int r0 = r1.unmovedTextX     // Catch:{ Exception -> 0x00e9 }
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x00e9 }
            int r0 = r0 + r9
            int r9 = r1.descriptionX     // Catch:{ Exception -> 0x00e9 }
            int r0 = r0 + r9
            int r0 = r3 - r0
            int r9 = r1.descriptionY     // Catch:{ Exception -> 0x00e9 }
            int r9 = r4 - r9
            android.text.StaticLayout r10 = r1.descriptionLayout     // Catch:{ Exception -> 0x00e9 }
            int r10 = r10.getHeight()     // Catch:{ Exception -> 0x00e9 }
            if (r9 > r10) goto L_0x00ed
            android.text.StaticLayout r10 = r1.descriptionLayout     // Catch:{ Exception -> 0x00e9 }
            int r9 = r10.getLineForVertical(r9)     // Catch:{ Exception -> 0x00e9 }
            android.text.StaticLayout r10 = r1.descriptionLayout     // Catch:{ Exception -> 0x00e9 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x00e9 }
            int r10 = r10.getOffsetForHorizontal(r9, r0)     // Catch:{ Exception -> 0x00e9 }
            android.text.StaticLayout r11 = r1.descriptionLayout     // Catch:{ Exception -> 0x00e9 }
            float r11 = r11.getLineLeft(r9)     // Catch:{ Exception -> 0x00e9 }
            int r12 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
            if (r12 > 0) goto L_0x00ed
            android.text.StaticLayout r12 = r1.descriptionLayout     // Catch:{ Exception -> 0x00e9 }
            float r9 = r12.getLineWidth(r9)     // Catch:{ Exception -> 0x00e9 }
            float r11 = r11 + r9
            int r0 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x00ed
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject     // Catch:{ Exception -> 0x00e9 }
            java.lang.CharSequence r0 = r0.linkDescription     // Catch:{ Exception -> 0x00e9 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x00e9 }
            java.lang.Class<android.text.style.ClickableSpan> r9 = android.text.style.ClickableSpan.class
            java.lang.Object[] r9 = r0.getSpans(r10, r10, r9)     // Catch:{ Exception -> 0x00e9 }
            android.text.style.ClickableSpan[] r9 = (android.text.style.ClickableSpan[]) r9     // Catch:{ Exception -> 0x00e9 }
            int r10 = r9.length     // Catch:{ Exception -> 0x00e9 }
            if (r10 == 0) goto L_0x00b2
            int r10 = r9.length     // Catch:{ Exception -> 0x00e9 }
            if (r10 == 0) goto L_0x00b0
            r10 = r9[r2]     // Catch:{ Exception -> 0x00e9 }
            boolean r10 = r10 instanceof org.telegram.ui.Components.URLSpanBotCommand     // Catch:{ Exception -> 0x00e9 }
            if (r10 == 0) goto L_0x00b0
            boolean r10 = org.telegram.ui.Components.URLSpanBotCommand.enabled     // Catch:{ Exception -> 0x00e9 }
            if (r10 != 0) goto L_0x00b0
            goto L_0x00b2
        L_0x00b0:
            r10 = 0
            goto L_0x00b3
        L_0x00b2:
            r10 = 1
        L_0x00b3:
            if (r10 != 0) goto L_0x00ed
            r9 = r9[r2]     // Catch:{ Exception -> 0x00e9 }
            r1.pressedLink = r9     // Catch:{ Exception -> 0x00e9 }
            r9 = -10
            r1.linkBlockNum = r9     // Catch:{ Exception -> 0x00e9 }
            r1.pressedLinkType = r7     // Catch:{ Exception -> 0x00e9 }
            r1.resetUrlPaths(r2)     // Catch:{ Exception -> 0x00e9 }
            r16.startCheckLongPress()     // Catch:{ Exception -> 0x00e9 }
            org.telegram.ui.Components.LinkPath r9 = r1.obtainNewUrlPath(r2)     // Catch:{ Exception -> 0x00e1 }
            android.text.style.CharacterStyle r10 = r1.pressedLink     // Catch:{ Exception -> 0x00e1 }
            int[] r0 = r1.getRealSpanStartAndEnd(r0, r10)     // Catch:{ Exception -> 0x00e1 }
            android.text.StaticLayout r10 = r1.descriptionLayout     // Catch:{ Exception -> 0x00e1 }
            r11 = r0[r2]     // Catch:{ Exception -> 0x00e1 }
            r12 = 0
            r9.setCurrentLayout(r10, r11, r12)     // Catch:{ Exception -> 0x00e1 }
            android.text.StaticLayout r10 = r1.descriptionLayout     // Catch:{ Exception -> 0x00e1 }
            r11 = r0[r2]     // Catch:{ Exception -> 0x00e1 }
            r0 = r0[r8]     // Catch:{ Exception -> 0x00e1 }
            r10.getSelectionPath(r11, r0, r9)     // Catch:{ Exception -> 0x00e1 }
            goto L_0x00e5
        L_0x00e1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x00e9 }
        L_0x00e5:
            r16.invalidate()     // Catch:{ Exception -> 0x00e9 }
            return r8
        L_0x00e9:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00ed:
            android.text.style.CharacterStyle r0 = r1.pressedLink
            if (r0 != 0) goto L_0x0388
            r0 = 1111490560(0x42400000, float:48.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r10 = r1.miniButtonState
            if (r10 < 0) goto L_0x0117
            r10 = 1104674816(0x41d80000, float:27.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r11 = r1.buttonX
            int r12 = r11 + r10
            if (r3 < r12) goto L_0x0117
            int r11 = r11 + r10
            int r11 = r11 + r9
            if (r3 > r11) goto L_0x0117
            int r11 = r1.buttonY
            int r12 = r11 + r10
            if (r4 < r12) goto L_0x0117
            int r11 = r11 + r10
            int r11 = r11 + r9
            if (r4 > r11) goto L_0x0117
            r9 = 1
            goto L_0x0118
        L_0x0117:
            r9 = 0
        L_0x0118:
            if (r9 == 0) goto L_0x0120
            r1.miniButtonPressed = r8
            r16.invalidate()
            return r8
        L_0x0120:
            boolean r9 = r1.drawVideoImageButton
            if (r9 == 0) goto L_0x0151
            int r9 = r1.buttonState
            if (r9 == r6) goto L_0x0151
            int r9 = r1.videoButtonX
            if (r3 < r9) goto L_0x0151
            r10 = 1107820544(0x42080000, float:34.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 + r10
            int r10 = r1.infoWidth
            int r11 = r1.docTitleWidth
            int r10 = java.lang.Math.max(r10, r11)
            int r9 = r9 + r10
            if (r3 > r9) goto L_0x0151
            int r9 = r1.videoButtonY
            if (r4 < r9) goto L_0x0151
            r10 = 1106247680(0x41var_, float:30.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 + r10
            if (r4 > r9) goto L_0x0151
            r1.videoButtonPressed = r8
            r16.invalidate()
            return r8
        L_0x0151:
            boolean r9 = r1.drawPhotoImage
            if (r9 == 0) goto L_0x0190
            boolean r9 = r1.drawImageButton
            if (r9 == 0) goto L_0x0190
            int r9 = r1.buttonState
            if (r9 == r6) goto L_0x0190
            boolean r9 = r1.checkOnlyButtonPressed
            if (r9 != 0) goto L_0x016b
            org.telegram.messenger.ImageReceiver r9 = r1.photoImage
            float r10 = (float) r3
            float r11 = (float) r4
            boolean r9 = r9.isInsideImage(r10, r11)
            if (r9 != 0) goto L_0x018a
        L_0x016b:
            int r9 = r1.buttonX
            if (r3 < r9) goto L_0x0190
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r9 = r9 + r10
            if (r3 > r9) goto L_0x0190
            int r9 = r1.buttonY
            if (r4 < r9) goto L_0x0190
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r9 = r9 + r0
            if (r4 > r9) goto L_0x0190
            org.telegram.ui.Components.RadialProgress2 r0 = r1.radialProgress
            int r0 = r0.getIcon()
            r9 = 4
            if (r0 == r9) goto L_0x0190
        L_0x018a:
            r1.buttonPressed = r8
            r16.invalidate()
            return r8
        L_0x0190:
            boolean r0 = r1.drawInstantView
            if (r0 == 0) goto L_0x01bc
            r1.instantPressed = r8
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r5) goto L_0x01b8
            android.graphics.drawable.Drawable r0 = r1.selectorDrawable
            if (r0 == 0) goto L_0x01b8
            android.graphics.Rect r0 = r0.getBounds()
            boolean r0 = r0.contains(r3, r4)
            if (r0 == 0) goto L_0x01b8
            android.graphics.drawable.Drawable r0 = r1.selectorDrawable
            int[] r2 = r1.pressedState
            r0.setState(r2)
            android.graphics.drawable.Drawable r0 = r1.selectorDrawable
            float r2 = (float) r3
            float r3 = (float) r4
            r0.setHotspot(r2, r3)
            r1.instantButtonPressed = r8
        L_0x01b8:
            r16.invalidate()
            return r8
        L_0x01bc:
            int r0 = r1.documentAttachType
            if (r0 == r8) goto L_0x0388
            boolean r0 = r1.drawPhotoImage
            if (r0 == 0) goto L_0x0388
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            float r3 = (float) r3
            float r4 = (float) r4
            boolean r0 = r0.isInsideImage(r3, r4)
            if (r0 == 0) goto L_0x0388
            r1.linkPreviewPressed = r8
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            int r3 = r1.documentAttachType
            if (r3 != r7) goto L_0x01f7
            int r3 = r1.buttonState
            if (r3 != r6) goto L_0x01f7
            boolean r3 = org.telegram.messenger.SharedConfig.autoplayGifs
            if (r3 == 0) goto L_0x01f7
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            org.telegram.ui.Components.AnimatedFileDrawable r3 = r3.getAnimation()
            if (r3 == 0) goto L_0x01f4
            java.lang.String r0 = r0.embed_url
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x01f7
        L_0x01f4:
            r1.linkPreviewPressed = r2
            return r2
        L_0x01f7:
            return r8
        L_0x01f8:
            int r0 = r17.getAction()
            if (r0 != r8) goto L_0x0371
            boolean r0 = r1.instantPressed
            if (r0 == 0) goto L_0x0224
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r1.delegate
            if (r0 == 0) goto L_0x020b
            int r3 = r1.drawInstantViewType
            r0.didPressInstantButton(r1, r3)
        L_0x020b:
            r1.playSoundEffect(r2)
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r5) goto L_0x021b
            android.graphics.drawable.Drawable r0 = r1.selectorDrawable
            if (r0 == 0) goto L_0x021b
            int[] r3 = android.util.StateSet.NOTHING
            r0.setState(r3)
        L_0x021b:
            r1.instantButtonPressed = r2
            r1.instantPressed = r2
            r16.invalidate()
            goto L_0x0388
        L_0x0224:
            int r0 = r1.pressedLinkType
            if (r0 == r7) goto L_0x023e
            int r0 = r1.buttonPressed
            if (r0 != 0) goto L_0x023e
            int r0 = r1.miniButtonPressed
            if (r0 != 0) goto L_0x023e
            int r0 = r1.videoButtonPressed
            if (r0 != 0) goto L_0x023e
            boolean r0 = r1.linkPreviewPressed
            if (r0 == 0) goto L_0x0239
            goto L_0x023e
        L_0x0239:
            r1.resetPressedLink(r7)
            goto L_0x0388
        L_0x023e:
            int r0 = r1.videoButtonPressed
            if (r0 != r8) goto L_0x024f
            r1.videoButtonPressed = r2
            r1.playSoundEffect(r2)
            r1.didPressButton(r8, r8)
            r16.invalidate()
            goto L_0x0388
        L_0x024f:
            int r0 = r1.buttonPressed
            if (r0 == 0) goto L_0x0268
            r1.buttonPressed = r2
            r1.playSoundEffect(r2)
            boolean r0 = r1.drawVideoImageButton
            if (r0 == 0) goto L_0x0260
            r16.didClickedImage()
            goto L_0x0263
        L_0x0260:
            r1.didPressButton(r8, r2)
        L_0x0263:
            r16.invalidate()
            goto L_0x0388
        L_0x0268:
            int r0 = r1.miniButtonPressed
            if (r0 == 0) goto L_0x0279
            r1.miniButtonPressed = r2
            r1.playSoundEffect(r2)
            r1.didPressMiniButton(r8)
            r16.invalidate()
            goto L_0x0388
        L_0x0279:
            android.text.style.CharacterStyle r0 = r1.pressedLink
            if (r0 == 0) goto L_0x02b3
            boolean r3 = r0 instanceof android.text.style.URLSpan
            if (r3 == 0) goto L_0x02a5
            android.text.style.URLSpan r0 = (android.text.style.URLSpan) r0
            java.lang.String r0 = r0.getURL()
            boolean r0 = org.telegram.ui.ChatActivity.isClickableLink(r0)
            if (r0 == 0) goto L_0x0295
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r1.delegate
            android.text.style.CharacterStyle r3 = r1.pressedLink
            r0.didPressUrl(r1, r3, r2)
            goto L_0x02ae
        L_0x0295:
            android.content.Context r0 = r16.getContext()
            android.text.style.CharacterStyle r3 = r1.pressedLink
            android.text.style.URLSpan r3 = (android.text.style.URLSpan) r3
            java.lang.String r3 = r3.getURL()
            org.telegram.messenger.browser.Browser.openUrl((android.content.Context) r0, (java.lang.String) r3)
            goto L_0x02ae
        L_0x02a5:
            boolean r3 = r0 instanceof android.text.style.ClickableSpan
            if (r3 == 0) goto L_0x02ae
            android.text.style.ClickableSpan r0 = (android.text.style.ClickableSpan) r0
            r0.onClick(r1)
        L_0x02ae:
            r1.resetPressedLink(r7)
            goto L_0x0388
        L_0x02b3:
            int r0 = r1.documentAttachType
            r3 = 7
            if (r0 != r3) goto L_0x02e3
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r2 = r1.currentMessageObject
            boolean r0 = r0.isPlayingMessage(r2)
            if (r0 == 0) goto L_0x02da
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            boolean r0 = r0.isMessagePaused()
            if (r0 == 0) goto L_0x02cf
            goto L_0x02da
        L_0x02cf:
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r2 = r1.currentMessageObject
            r0.lambda$startAudioAgain$6$MediaController(r2)
            goto L_0x036d
        L_0x02da:
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r1.delegate
            org.telegram.messenger.MessageObject r2 = r1.currentMessageObject
            r0.needPlayMessage(r2)
            goto L_0x036d
        L_0x02e3:
            if (r0 != r7) goto L_0x0329
            boolean r0 = r1.drawImageButton
            if (r0 == 0) goto L_0x0329
            int r0 = r1.buttonState
            if (r0 != r6) goto L_0x031e
            boolean r0 = org.telegram.messenger.SharedConfig.autoplayGifs
            if (r0 == 0) goto L_0x02fc
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r1.delegate
            float r2 = r1.lastTouchX
            float r3 = r1.lastTouchY
            r0.didPressImage(r1, r2, r3)
            goto L_0x036d
        L_0x02fc:
            r1.buttonState = r7
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            r3 = 1065353216(0x3var_, float:1.0)
            r0.gifState = r3
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r0.setAllowStartAnimation(r2)
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r0.stopAnimation()
            org.telegram.ui.Components.RadialProgress2 r0 = r1.radialProgress
            int r3 = r16.getIconForCurrentState()
            r0.setIcon(r3, r2, r8)
            r16.invalidate()
            r1.playSoundEffect(r2)
            goto L_0x036d
        L_0x031e:
            if (r0 == r7) goto L_0x0322
            if (r0 != 0) goto L_0x036d
        L_0x0322:
            r1.didPressButton(r8, r2)
            r1.playSoundEffect(r2)
            goto L_0x036d
        L_0x0329:
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x034d
            java.lang.String r3 = r0.embed_url
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x034d
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r9 = r1.delegate
            java.lang.String r10 = r0.embed_url
            java.lang.String r11 = r0.site_name
            java.lang.String r12 = r0.title
            java.lang.String r13 = r0.url
            int r14 = r0.embed_width
            int r15 = r0.embed_height
            r9.needOpenWebView(r10, r11, r12, r13, r14, r15)
            goto L_0x036d
        L_0x034d:
            int r3 = r1.buttonState
            if (r3 == r6) goto L_0x0361
            r4 = 3
            if (r3 != r4) goto L_0x0355
            goto L_0x0361
        L_0x0355:
            if (r0 == 0) goto L_0x036d
            android.content.Context r2 = r16.getContext()
            java.lang.String r0 = r0.url
            org.telegram.messenger.browser.Browser.openUrl((android.content.Context) r2, (java.lang.String) r0)
            goto L_0x036d
        L_0x0361:
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r1.delegate
            float r3 = r1.lastTouchX
            float r4 = r1.lastTouchY
            r0.didPressImage(r1, r3, r4)
            r1.playSoundEffect(r2)
        L_0x036d:
            r1.resetPressedLink(r7)
            return r8
        L_0x0371:
            int r0 = r17.getAction()
            if (r0 != r7) goto L_0x0388
            boolean r0 = r1.instantButtonPressed
            if (r0 == 0) goto L_0x0388
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r5) goto L_0x0388
            android.graphics.drawable.Drawable r0 = r1.selectorDrawable
            if (r0 == 0) goto L_0x0388
            float r3 = (float) r3
            float r4 = (float) r4
            r0.setHotspot(r3, r4)
        L_0x0388:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkLinkPreviewMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkPollButtonMotionEvent(MotionEvent motionEvent) {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        if (this.currentMessageObject.eventId != 0 || this.pollVoted || this.pollClosed || this.pollVoteInProgress || this.pollUnvoteInProgress || this.pollButtons.isEmpty()) {
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
            int i = 0;
            while (i < this.pollButtons.size()) {
                PollButton pollButton = this.pollButtons.get(i);
                int access$600 = (pollButton.y + this.namesOffset) - AndroidUtilities.dp(13.0f);
                if (x < pollButton.x || x > (pollButton.x + this.backgroundWidth) - AndroidUtilities.dp(31.0f) || y < access$600 || y > pollButton.height + access$600 + AndroidUtilities.dp(26.0f)) {
                    i++;
                } else {
                    this.pressedVoteButton = i;
                    if (Build.VERSION.SDK_INT >= 21 && (drawable3 = this.selectorDrawable) != null) {
                        drawable3.setBounds(pollButton.x - AndroidUtilities.dp(9.0f), access$600, (pollButton.x + this.backgroundWidth) - AndroidUtilities.dp(22.0f), pollButton.height + access$600 + AndroidUtilities.dp(26.0f));
                        this.selectorDrawable.setState(this.pressedState);
                        this.selectorDrawable.setHotspot((float) x, (float) y);
                    }
                    invalidate();
                    return true;
                }
            }
            return false;
        } else if (motionEvent.getAction() == 1) {
            if (this.pressedVoteButton == -1) {
                return false;
            }
            playSoundEffect(0);
            if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.selectorDrawable) != null) {
                drawable2.setState(StateSet.NOTHING);
            }
            if (this.currentMessageObject.scheduled) {
                Toast.makeText(getContext(), LocaleController.getString("MessageScheduledVote", NUM), 1).show();
            } else {
                int i2 = this.pressedVoteButton;
                this.pollVoteInProgressNum = i2;
                this.pollVoteInProgress = true;
                this.voteCurrentProgressTime = 0.0f;
                this.firstCircleLength = true;
                this.voteCurrentCircleLength = 360.0f;
                this.voteRisingCircleLength = false;
                this.delegate.didPressVoteButton(this, this.pollButtons.get(i2).answer);
            }
            this.pressedVoteButton = -1;
            invalidate();
            return false;
        } else if (motionEvent.getAction() != 2 || this.pressedVoteButton == -1 || Build.VERSION.SDK_INT < 21 || (drawable = this.selectorDrawable) == null) {
            return false;
        } else {
            drawable.setHotspot((float) x, (float) y);
            return false;
        }
    }

    private boolean checkInstantButtonMotionEvent(MotionEvent motionEvent) {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        if (this.drawInstantView && this.currentMessageObject.type != 0) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (motionEvent.getAction() == 0) {
                if (this.drawInstantView) {
                    float f = (float) x;
                    float f2 = (float) y;
                    if (this.instantButtonRect.contains(f, f2)) {
                        this.instantPressed = true;
                        if (Build.VERSION.SDK_INT >= 21 && (drawable3 = this.selectorDrawable) != null && drawable3.getBounds().contains(x, y)) {
                            this.selectorDrawable.setState(this.pressedState);
                            this.selectorDrawable.setHotspot(f, f2);
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
                        chatMessageCellDelegate.didPressInstantButton(this, this.drawInstantViewType);
                    }
                    playSoundEffect(0);
                    if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.selectorDrawable) != null) {
                        drawable2.setState(StateSet.NOTHING);
                    }
                    this.instantButtonPressed = false;
                    this.instantPressed = false;
                    invalidate();
                }
            } else if (motionEvent.getAction() == 2 && this.instantButtonPressed && Build.VERSION.SDK_INT >= 21 && (drawable = this.selectorDrawable) != null) {
                drawable.setHotspot((float) x, (float) y);
            }
        }
        return false;
    }

    private boolean checkOtherButtonMotionEvent(MotionEvent motionEvent) {
        int i;
        boolean z = this.currentMessageObject.type == 16;
        if (!z) {
            int i2 = this.documentAttachType;
            z = (i2 == 1 || (i = this.currentMessageObject.type) == 12 || i2 == 5 || i2 == 4 || i2 == 2 || i == 8) && !this.hasGamePreview && !this.hasInvoicePreview;
        }
        if (!z) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.currentMessageObject.type == 16) {
                int i3 = this.otherX;
                if (x >= i3 && x <= i3 + AndroidUtilities.dp(235.0f) && y >= this.otherY - AndroidUtilities.dp(14.0f) && y <= this.otherY + AndroidUtilities.dp(50.0f)) {
                    this.otherPressed = true;
                    invalidate();
                    return true;
                }
            } else if (x >= this.otherX - AndroidUtilities.dp(20.0f) && x <= this.otherX + AndroidUtilities.dp(20.0f) && y >= this.otherY - AndroidUtilities.dp(4.0f) && y <= this.otherY + AndroidUtilities.dp(30.0f)) {
                this.otherPressed = true;
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1 && this.otherPressed) {
            this.otherPressed = false;
            playSoundEffect(0);
            this.delegate.didPressOther(this, (float) this.otherX, (float) this.otherY);
            invalidate();
            return true;
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x014d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkPhotoImageMotionEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            boolean r0 = r8.drawPhotoImage
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x000b
            int r0 = r8.documentAttachType
            if (r0 == r2) goto L_0x000b
            return r1
        L_0x000b:
            float r0 = r9.getX()
            int r0 = (int) r0
            float r3 = r9.getY()
            int r3 = (int) r3
            int r4 = r9.getAction()
            r5 = -1
            if (r4 != 0) goto L_0x0177
            r9 = 1111490560(0x42400000, float:48.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r4 = r8.miniButtonState
            if (r4 < 0) goto L_0x0042
            r4 = 1104674816(0x41d80000, float:27.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = r8.buttonX
            int r7 = r6 + r4
            if (r0 < r7) goto L_0x0042
            int r6 = r6 + r4
            int r6 = r6 + r9
            if (r0 > r6) goto L_0x0042
            int r6 = r8.buttonY
            int r7 = r6 + r4
            if (r3 < r7) goto L_0x0042
            int r6 = r6 + r4
            int r6 = r6 + r9
            if (r3 > r6) goto L_0x0042
            r4 = 1
            goto L_0x0043
        L_0x0042:
            r4 = 0
        L_0x0043:
            if (r4 == 0) goto L_0x004c
            r8.miniButtonPressed = r2
            r8.invalidate()
            goto L_0x0149
        L_0x004c:
            int r4 = r8.buttonState
            if (r4 == r5) goto L_0x006e
            org.telegram.ui.Components.RadialProgress2 r4 = r8.radialProgress
            int r4 = r4.getIcon()
            r6 = 4
            if (r4 == r6) goto L_0x006e
            int r4 = r8.buttonX
            if (r0 < r4) goto L_0x006e
            int r4 = r4 + r9
            if (r0 > r4) goto L_0x006e
            int r4 = r8.buttonY
            if (r3 < r4) goto L_0x006e
            int r4 = r4 + r9
            if (r3 > r4) goto L_0x006e
            r8.buttonPressed = r2
            r8.invalidate()
            goto L_0x0149
        L_0x006e:
            boolean r9 = r8.drawVideoImageButton
            if (r9 == 0) goto L_0x00a0
            int r9 = r8.buttonState
            if (r9 == r5) goto L_0x00a0
            int r9 = r8.videoButtonX
            if (r0 < r9) goto L_0x00a0
            r4 = 1107820544(0x42080000, float:34.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r9 = r9 + r4
            int r4 = r8.infoWidth
            int r6 = r8.docTitleWidth
            int r4 = java.lang.Math.max(r4, r6)
            int r9 = r9 + r4
            if (r0 > r9) goto L_0x00a0
            int r9 = r8.videoButtonY
            if (r3 < r9) goto L_0x00a0
            r4 = 1106247680(0x41var_, float:30.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r9 = r9 + r4
            if (r3 > r9) goto L_0x00a0
            r8.videoButtonPressed = r2
            r8.invalidate()
            goto L_0x0149
        L_0x00a0:
            int r9 = r8.documentAttachType
            if (r9 != r2) goto L_0x00d9
            org.telegram.messenger.ImageReceiver r9 = r8.photoImage
            int r9 = r9.getImageX()
            if (r0 < r9) goto L_0x00f2
            org.telegram.messenger.ImageReceiver r9 = r8.photoImage
            int r9 = r9.getImageX()
            int r4 = r8.backgroundWidth
            int r9 = r9 + r4
            r4 = 1112014848(0x42480000, float:50.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r9 = r9 - r4
            if (r0 > r9) goto L_0x00f2
            org.telegram.messenger.ImageReceiver r9 = r8.photoImage
            int r9 = r9.getImageY()
            if (r3 < r9) goto L_0x00f2
            org.telegram.messenger.ImageReceiver r9 = r8.photoImage
            int r9 = r9.getImageY()
            org.telegram.messenger.ImageReceiver r0 = r8.photoImage
            int r0 = r0.getImageHeight()
            int r9 = r9 + r0
            if (r3 > r9) goto L_0x00f2
            r8.imagePressed = r2
            goto L_0x0149
        L_0x00d9:
            org.telegram.messenger.MessageObject r9 = r8.currentMessageObject
            boolean r9 = r9.isAnyKindOfSticker()
            if (r9 == 0) goto L_0x00f4
            org.telegram.messenger.MessageObject r9 = r8.currentMessageObject
            org.telegram.tgnet.TLRPC$InputStickerSet r9 = r9.getInputStickerSet()
            if (r9 != 0) goto L_0x00f4
            org.telegram.messenger.MessageObject r9 = r8.currentMessageObject
            boolean r9 = r9.isAnimatedEmoji()
            if (r9 == 0) goto L_0x00f2
            goto L_0x00f4
        L_0x00f2:
            r2 = 0
            goto L_0x0149
        L_0x00f4:
            org.telegram.messenger.ImageReceiver r9 = r8.photoImage
            int r9 = r9.getImageX()
            if (r0 < r9) goto L_0x0125
            org.telegram.messenger.ImageReceiver r9 = r8.photoImage
            int r9 = r9.getImageX()
            org.telegram.messenger.ImageReceiver r4 = r8.photoImage
            int r4 = r4.getImageWidth()
            int r9 = r9 + r4
            if (r0 > r9) goto L_0x0125
            org.telegram.messenger.ImageReceiver r9 = r8.photoImage
            int r9 = r9.getImageY()
            if (r3 < r9) goto L_0x0125
            org.telegram.messenger.ImageReceiver r9 = r8.photoImage
            int r9 = r9.getImageY()
            org.telegram.messenger.ImageReceiver r0 = r8.photoImage
            int r0 = r0.getImageHeight()
            int r9 = r9 + r0
            if (r3 > r9) goto L_0x0125
            r8.imagePressed = r2
            goto L_0x0126
        L_0x0125:
            r2 = 0
        L_0x0126:
            org.telegram.messenger.MessageObject r9 = r8.currentMessageObject
            int r9 = r9.type
            r0 = 12
            if (r9 != r0) goto L_0x0149
            int r9 = r8.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.messenger.MessageObject r0 = r8.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r0)
            if (r9 != 0) goto L_0x0149
            r8.imagePressed = r1
            goto L_0x00f2
        L_0x0149:
            boolean r9 = r8.imagePressed
            if (r9 == 0) goto L_0x0175
            org.telegram.messenger.MessageObject r9 = r8.currentMessageObject
            boolean r9 = r9.isSendError()
            if (r9 == 0) goto L_0x0159
            r8.imagePressed = r1
            goto L_0x01dc
        L_0x0159:
            org.telegram.messenger.MessageObject r9 = r8.currentMessageObject
            int r9 = r9.type
            r0 = 8
            if (r9 != r0) goto L_0x0175
            int r9 = r8.buttonState
            if (r9 != r5) goto L_0x0175
            boolean r9 = org.telegram.messenger.SharedConfig.autoplayGifs
            if (r9 == 0) goto L_0x0175
            org.telegram.messenger.ImageReceiver r9 = r8.photoImage
            org.telegram.ui.Components.AnimatedFileDrawable r9 = r9.getAnimation()
            if (r9 != 0) goto L_0x0175
            r8.imagePressed = r1
            goto L_0x01dc
        L_0x0175:
            r1 = r2
            goto L_0x01dc
        L_0x0177:
            int r9 = r9.getAction()
            if (r9 != r2) goto L_0x01dc
            int r9 = r8.videoButtonPressed
            if (r9 != r2) goto L_0x018d
            r8.videoButtonPressed = r1
            r8.playSoundEffect(r1)
            r8.didPressButton(r2, r2)
            r8.invalidate()
            goto L_0x01dc
        L_0x018d:
            int r9 = r8.buttonPressed
            if (r9 != r2) goto L_0x01a5
            r8.buttonPressed = r1
            r8.playSoundEffect(r1)
            boolean r9 = r8.drawVideoImageButton
            if (r9 == 0) goto L_0x019e
            r8.didClickedImage()
            goto L_0x01a1
        L_0x019e:
            r8.didPressButton(r2, r1)
        L_0x01a1:
            r8.invalidate()
            goto L_0x01dc
        L_0x01a5:
            int r9 = r8.miniButtonPressed
            if (r9 != r2) goto L_0x01b5
            r8.miniButtonPressed = r1
            r8.playSoundEffect(r1)
            r8.didPressMiniButton(r2)
            r8.invalidate()
            goto L_0x01dc
        L_0x01b5:
            boolean r9 = r8.imagePressed
            if (r9 == 0) goto L_0x01dc
            r8.imagePressed = r1
            int r9 = r8.buttonState
            if (r9 == r5) goto L_0x01d3
            r0 = 2
            if (r9 == r0) goto L_0x01d3
            r0 = 3
            if (r9 == r0) goto L_0x01d3
            boolean r0 = r8.drawVideoImageButton
            if (r0 == 0) goto L_0x01ca
            goto L_0x01d3
        L_0x01ca:
            if (r9 != 0) goto L_0x01d9
            r8.playSoundEffect(r1)
            r8.didPressButton(r2, r1)
            goto L_0x01d9
        L_0x01d3:
            r8.playSoundEffect(r1)
            r8.didClickedImage()
        L_0x01d9:
            r8.invalidate()
        L_0x01dc:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkPhotoImageMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00c5, code lost:
        if (r3 <= (r0 + r6)) goto L_0x00c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0101, code lost:
        if (r3 <= (r0 + r6)) goto L_0x00c7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x011b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkAudioMotionEvent(android.view.MotionEvent r13) {
        /*
            r12 = this;
            int r0 = r12.documentAttachType
            r1 = 3
            r2 = 0
            if (r0 == r1) goto L_0x000a
            r3 = 5
            if (r0 == r3) goto L_0x000a
            return r2
        L_0x000a:
            float r0 = r13.getX()
            int r0 = (int) r0
            float r3 = r13.getY()
            int r3 = (int) r3
            boolean r4 = r12.useSeekBarWaweform
            if (r4 == 0) goto L_0x003b
            org.telegram.ui.Components.SeekBarWaveform r4 = r12.seekBarWaveform
            int r5 = r13.getAction()
            float r6 = r13.getX()
            int r7 = r12.seekBarX
            float r7 = (float) r7
            float r6 = r6 - r7
            r7 = 1095761920(0x41500000, float:13.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r6 = r6 - r7
            float r7 = r13.getY()
            int r8 = r12.seekBarY
            float r8 = (float) r8
            float r7 = r7 - r8
            boolean r4 = r4.onTouch(r5, r6, r7)
            goto L_0x0055
        L_0x003b:
            org.telegram.ui.Components.SeekBar r4 = r12.seekBar
            int r5 = r13.getAction()
            float r6 = r13.getX()
            int r7 = r12.seekBarX
            float r7 = (float) r7
            float r6 = r6 - r7
            float r7 = r13.getY()
            int r8 = r12.seekBarY
            float r8 = (float) r8
            float r7 = r7 - r8
            boolean r4 = r4.onTouch(r5, r6, r7)
        L_0x0055:
            r5 = 1
            if (r4 == 0) goto L_0x0086
            boolean r0 = r12.useSeekBarWaweform
            if (r0 != 0) goto L_0x006a
            int r0 = r13.getAction()
            if (r0 != 0) goto L_0x006a
            android.view.ViewParent r13 = r12.getParent()
            r13.requestDisallowInterceptTouchEvent(r5)
            goto L_0x007f
        L_0x006a:
            boolean r0 = r12.useSeekBarWaweform
            if (r0 == 0) goto L_0x007f
            org.telegram.ui.Components.SeekBarWaveform r0 = r12.seekBarWaveform
            boolean r0 = r0.isStartDraging()
            if (r0 != 0) goto L_0x007f
            int r13 = r13.getAction()
            if (r13 != r5) goto L_0x007f
            r12.didPressButton(r5, r2)
        L_0x007f:
            r12.disallowLongPress = r5
            r12.invalidate()
            goto L_0x017a
        L_0x0086:
            r6 = 1108344832(0x42100000, float:36.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r7 = r12.miniButtonState
            if (r7 < 0) goto L_0x00ac
            r7 = 1104674816(0x41d80000, float:27.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r8 = r12.buttonX
            int r9 = r8 + r7
            if (r0 < r9) goto L_0x00ac
            int r8 = r8 + r7
            int r8 = r8 + r6
            if (r0 > r8) goto L_0x00ac
            int r8 = r12.buttonY
            int r9 = r8 + r7
            if (r3 < r9) goto L_0x00ac
            int r8 = r8 + r7
            int r8 = r8 + r6
            if (r3 > r8) goto L_0x00ac
            r7 = 1
            goto L_0x00ad
        L_0x00ac:
            r7 = 0
        L_0x00ad:
            r8 = 2
            if (r7 != 0) goto L_0x0104
            int r9 = r12.buttonState
            if (r9 == 0) goto L_0x00c9
            if (r9 == r5) goto L_0x00c9
            if (r9 != r8) goto L_0x00b9
            goto L_0x00c9
        L_0x00b9:
            int r9 = r12.buttonX
            if (r0 < r9) goto L_0x0104
            int r9 = r9 + r6
            if (r0 > r9) goto L_0x0104
            int r0 = r12.buttonY
            if (r3 < r0) goto L_0x0104
            int r0 = r0 + r6
            if (r3 > r0) goto L_0x0104
        L_0x00c7:
            r0 = 1
            goto L_0x0105
        L_0x00c9:
            int r9 = r12.buttonX
            r10 = 1094713344(0x41400000, float:12.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r11
            if (r0 < r9) goto L_0x0104
            int r9 = r12.buttonX
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
            int r10 = r12.backgroundWidth
            int r9 = r9 + r10
            if (r0 > r9) goto L_0x0104
            boolean r0 = r12.drawInstantView
            if (r0 == 0) goto L_0x00e7
            int r0 = r12.buttonY
            goto L_0x00ec
        L_0x00e7:
            int r0 = r12.namesOffset
            int r9 = r12.mediaOffsetY
            int r0 = r0 + r9
        L_0x00ec:
            if (r3 < r0) goto L_0x0104
            boolean r0 = r12.drawInstantView
            if (r0 == 0) goto L_0x00f5
            int r0 = r12.buttonY
            goto L_0x0100
        L_0x00f5:
            int r0 = r12.namesOffset
            int r6 = r12.mediaOffsetY
            int r0 = r0 + r6
            r6 = 1118044160(0x42a40000, float:82.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
        L_0x0100:
            int r0 = r0 + r6
            if (r3 > r0) goto L_0x0104
            goto L_0x00c7
        L_0x0104:
            r0 = 0
        L_0x0105:
            int r3 = r13.getAction()
            if (r3 != 0) goto L_0x011b
            if (r0 != 0) goto L_0x010f
            if (r7 == 0) goto L_0x017a
        L_0x010f:
            if (r0 == 0) goto L_0x0114
            r12.buttonPressed = r5
            goto L_0x0116
        L_0x0114:
            r12.miniButtonPressed = r5
        L_0x0116:
            r12.invalidate()
            r4 = 1
            goto L_0x017a
        L_0x011b:
            int r3 = r12.buttonPressed
            if (r3 == 0) goto L_0x014b
            int r3 = r13.getAction()
            if (r3 != r5) goto L_0x0131
            r12.buttonPressed = r2
            r12.playSoundEffect(r2)
            r12.didPressButton(r5, r2)
            r12.invalidate()
            goto L_0x017a
        L_0x0131:
            int r3 = r13.getAction()
            if (r3 != r1) goto L_0x013d
            r12.buttonPressed = r2
            r12.invalidate()
            goto L_0x017a
        L_0x013d:
            int r13 = r13.getAction()
            if (r13 != r8) goto L_0x017a
            if (r0 != 0) goto L_0x017a
            r12.buttonPressed = r2
            r12.invalidate()
            goto L_0x017a
        L_0x014b:
            int r0 = r12.miniButtonPressed
            if (r0 == 0) goto L_0x017a
            int r0 = r13.getAction()
            if (r0 != r5) goto L_0x0161
            r12.miniButtonPressed = r2
            r12.playSoundEffect(r2)
            r12.didPressMiniButton(r5)
            r12.invalidate()
            goto L_0x017a
        L_0x0161:
            int r0 = r13.getAction()
            if (r0 != r1) goto L_0x016d
            r12.miniButtonPressed = r2
            r12.invalidate()
            goto L_0x017a
        L_0x016d:
            int r13 = r13.getAction()
            if (r13 != r8) goto L_0x017a
            if (r7 != 0) goto L_0x017a
            r12.miniButtonPressed = r2
            r12.invalidate()
        L_0x017a:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkAudioMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkBotButtonMotionEvent(MotionEvent motionEvent) {
        int i;
        if (this.botButtons.isEmpty() || this.currentMessageObject.eventId != 0) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.currentMessageObject.isOutOwner()) {
                i = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                i = this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
            }
            int i2 = 0;
            while (i2 < this.botButtons.size()) {
                BotButton botButton = this.botButtons.get(i2);
                int access$1000 = (botButton.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                if (x < botButton.x + i || x > botButton.x + i + botButton.width || y < access$1000 || y > access$1000 + botButton.height) {
                    i2++;
                } else {
                    this.pressedBotButton = i2;
                    invalidate();
                    return true;
                }
            }
            return false;
        } else if (motionEvent.getAction() != 1 || this.pressedBotButton == -1) {
            return false;
        } else {
            playSoundEffect(0);
            if (this.currentMessageObject.scheduled) {
                Toast.makeText(getContext(), LocaleController.getString("MessageScheduledBotAction", NUM), 1).show();
            } else {
                BotButton botButton2 = this.botButtons.get(this.pressedBotButton);
                if (botButton2.button != null) {
                    this.delegate.didPressBotButton(this, botButton2.button);
                } else if (botButton2.reaction != null) {
                    this.delegate.didPressReaction(this, botButton2.reaction);
                }
            }
            this.pressedBotButton = -1;
            invalidate();
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:179:0x02ca, code lost:
        if (r5 <= ((float) (r1 + org.telegram.messenger.AndroidUtilities.dp(32.0f)))) goto L_0x0420;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x032d, code lost:
        if (r5 <= ((float) (r1 + org.telegram.messenger.AndroidUtilities.dp(32.0f)))) goto L_0x0420;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x03ce, code lost:
        if (r5 <= ((float) (r1 + org.telegram.messenger.AndroidUtilities.dp(35.0f)))) goto L_0x0420;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x0419, code lost:
        if (r5 <= ((float) (r1 + org.telegram.messenger.AndroidUtilities.dp(32.0f)))) goto L_0x041d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x01ea  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r14) {
        /*
            r13 = this;
            org.telegram.messenger.MessageObject r0 = r13.currentMessageObject
            if (r0 == 0) goto L_0x0421
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r13.delegate
            boolean r0 = r0.canPerformActions()
            if (r0 == 0) goto L_0x0421
            boolean r0 = r13.animationRunning
            if (r0 == 0) goto L_0x0012
            goto L_0x0421
        L_0x0012:
            boolean r0 = r13.checkTextSelection(r14)
            r1 = 1
            if (r0 == 0) goto L_0x001a
            return r1
        L_0x001a:
            r0 = 0
            r13.disallowLongPress = r0
            float r2 = r14.getX()
            r13.lastTouchX = r2
            float r2 = r14.getY()
            r13.lastTouchY = r2
            org.telegram.ui.Components.MessageBackgroundDrawable r2 = r13.backgroundDrawable
            float r3 = r13.lastTouchX
            float r4 = r13.lastTouchY
            r2.setTouchCoords(r3, r4)
            boolean r2 = r13.checkTextBlockMotionEvent(r14)
            if (r2 != 0) goto L_0x003c
            boolean r2 = r13.checkTextSelection(r14)
        L_0x003c:
            if (r2 != 0) goto L_0x0042
            boolean r2 = r13.checkOtherButtonMotionEvent(r14)
        L_0x0042:
            if (r2 != 0) goto L_0x0048
            boolean r2 = r13.checkCaptionMotionEvent(r14)
        L_0x0048:
            if (r2 != 0) goto L_0x004e
            boolean r2 = r13.checkAudioMotionEvent(r14)
        L_0x004e:
            if (r2 != 0) goto L_0x0054
            boolean r2 = r13.checkLinkPreviewMotionEvent(r14)
        L_0x0054:
            if (r2 != 0) goto L_0x005a
            boolean r2 = r13.checkInstantButtonMotionEvent(r14)
        L_0x005a:
            if (r2 != 0) goto L_0x0060
            boolean r2 = r13.checkGameMotionEvent(r14)
        L_0x0060:
            if (r2 != 0) goto L_0x0066
            boolean r2 = r13.checkPhotoImageMotionEvent(r14)
        L_0x0066:
            if (r2 != 0) goto L_0x006c
            boolean r2 = r13.checkBotButtonMotionEvent(r14)
        L_0x006c:
            if (r2 != 0) goto L_0x0072
            boolean r2 = r13.checkPollButtonMotionEvent(r14)
        L_0x0072:
            int r3 = r14.getAction()
            r4 = 3
            if (r3 != r4) goto L_0x00a4
            r13.buttonPressed = r0
            r13.miniButtonPressed = r0
            r2 = -1
            r13.pressedBotButton = r2
            r13.pressedVoteButton = r2
            r13.linkPreviewPressed = r0
            r13.otherPressed = r0
            r13.sharePressed = r0
            r13.imagePressed = r0
            r13.gamePreviewPressed = r0
            r13.instantButtonPressed = r0
            r13.instantPressed = r0
            int r3 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r3 < r5) goto L_0x009f
            android.graphics.drawable.Drawable r3 = r13.selectorDrawable
            if (r3 == 0) goto L_0x009f
            int[] r5 = android.util.StateSet.NOTHING
            r3.setState(r5)
        L_0x009f:
            r13.resetPressedLink(r2)
            r6 = 0
            goto L_0x00a5
        L_0x00a4:
            r6 = r2
        L_0x00a5:
            r13.updateRadialProgressBackground()
            boolean r2 = r13.disallowLongPress
            if (r2 != 0) goto L_0x00b7
            if (r6 == 0) goto L_0x00b7
            int r2 = r14.getAction()
            if (r2 != 0) goto L_0x00b7
            r13.startCheckLongPress()
        L_0x00b7:
            int r2 = r14.getAction()
            r3 = 2
            if (r2 == 0) goto L_0x00c7
            int r2 = r14.getAction()
            if (r2 == r3) goto L_0x00c7
            r13.cancelCheckLongPress()
        L_0x00c7:
            if (r6 != 0) goto L_0x0420
            float r2 = r14.getX()
            float r5 = r14.getY()
            int r7 = r14.getAction()
            r8 = 1108082688(0x420CLASSNAME, float:35.0)
            r9 = 1101004800(0x41a00000, float:20.0)
            r10 = 1109393408(0x42200000, float:40.0)
            r11 = 1082130432(0x40800000, float:4.0)
            r12 = 1107296256(0x42000000, float:32.0)
            if (r7 != 0) goto L_0x01ef
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r3 = r13.delegate
            if (r3 == 0) goto L_0x00eb
            boolean r3 = r3.canPerformActions()
            if (r3 == 0) goto L_0x0420
        L_0x00eb:
            boolean r3 = r13.isAvatarVisible
            if (r3 == 0) goto L_0x0102
            org.telegram.messenger.ImageReceiver r3 = r13.avatarImage
            int r4 = r13.getTop()
            float r4 = (float) r4
            float r4 = r4 + r5
            boolean r3 = r3.isInsideImage(r2, r4)
            if (r3 == 0) goto L_0x0102
            r13.avatarPressed = r1
        L_0x00ff:
            r6 = 1
            goto L_0x01e8
        L_0x0102:
            boolean r3 = r13.drawForwardedName
            if (r3 == 0) goto L_0x0145
            android.text.StaticLayout[] r3 = r13.forwardedNameLayout
            r0 = r3[r0]
            if (r0 == 0) goto L_0x0145
            int r0 = r13.forwardNameX
            float r3 = (float) r0
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x0145
            int r3 = r13.forwardedNameWidth
            int r0 = r0 + r3
            float r0 = (float) r0
            int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x0145
            int r0 = r13.forwardNameY
            float r3 = (float) r0
            int r3 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x0145
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r0 = r0 + r3
            float r0 = (float) r0
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x0145
            int r0 = r13.viaWidth
            if (r0 == 0) goto L_0x0142
            int r0 = r13.forwardNameX
            int r3 = r13.viaNameWidth
            int r0 = r0 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r0 = r0 + r3
            float r0 = (float) r0
            int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x0142
            r13.forwardBotPressed = r1
            goto L_0x00ff
        L_0x0142:
            r13.forwardNamePressed = r1
            goto L_0x00ff
        L_0x0145:
            boolean r0 = r13.drawNameLayout
            if (r0 == 0) goto L_0x017e
            android.text.StaticLayout r0 = r13.nameLayout
            if (r0 == 0) goto L_0x017e
            int r0 = r13.viaWidth
            if (r0 == 0) goto L_0x017e
            float r3 = r13.nameX
            int r4 = r13.viaNameWidth
            float r7 = (float) r4
            float r7 = r7 + r3
            int r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r7 < 0) goto L_0x017e
            float r4 = (float) r4
            float r3 = r3 + r4
            float r0 = (float) r0
            float r3 = r3 + r0
            int r0 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r0 > 0) goto L_0x017e
            float r0 = r13.nameY
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r3 = (float) r3
            float r0 = r0 - r3
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x017e
            float r0 = r13.nameY
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r3 = (float) r3
            float r0 = r0 + r3
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x017e
            r13.forwardBotPressed = r1
            goto L_0x00ff
        L_0x017e:
            boolean r0 = r13.drawShareButton
            if (r0 == 0) goto L_0x01ab
            int r0 = r13.shareStartX
            float r3 = (float) r0
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x01ab
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r0 = r0 + r3
            float r0 = (float) r0
            int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x01ab
            int r0 = r13.shareStartY
            float r3 = (float) r0
            int r3 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x01ab
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r0 = r0 + r3
            float r0 = (float) r0
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x01ab
            r13.sharePressed = r1
            r13.invalidate()
            goto L_0x00ff
        L_0x01ab:
            android.text.StaticLayout r0 = r13.replyNameLayout
            if (r0 == 0) goto L_0x01e8
            org.telegram.messenger.MessageObject r0 = r13.currentMessageObject
            boolean r0 = r0.shouldDrawWithoutBackground()
            if (r0 == 0) goto L_0x01c2
            int r0 = r13.replyStartX
            int r3 = r13.replyNameWidth
            int r4 = r13.replyTextWidth
            int r3 = java.lang.Math.max(r3, r4)
            goto L_0x01c6
        L_0x01c2:
            int r0 = r13.replyStartX
            int r3 = r13.backgroundDrawableRight
        L_0x01c6:
            int r0 = r0 + r3
            int r3 = r13.replyStartX
            float r3 = (float) r3
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x01e8
            float r0 = (float) r0
            int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x01e8
            int r0 = r13.replyStartY
            float r2 = (float) r0
            int r2 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x01e8
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r0 + r2
            float r0 = (float) r0
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x01e8
            r13.replyPressed = r1
            goto L_0x00ff
        L_0x01e8:
            if (r6 == 0) goto L_0x0420
            r13.startCheckLongPress()
            goto L_0x0420
        L_0x01ef:
            int r7 = r14.getAction()
            if (r7 == r3) goto L_0x01f8
            r13.cancelCheckLongPress()
        L_0x01f8:
            boolean r7 = r13.avatarPressed
            if (r7 == 0) goto L_0x025d
            int r7 = r14.getAction()
            if (r7 != r1) goto L_0x0237
            r13.avatarPressed = r0
            r13.playSoundEffect(r0)
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r13.delegate
            if (r0 == 0) goto L_0x0420
            org.telegram.tgnet.TLRPC$User r1 = r13.currentUser
            if (r1 == 0) goto L_0x0221
            int r2 = r1.id
            if (r2 != 0) goto L_0x0218
            r0.didPressHiddenForward(r13)
            goto L_0x0420
        L_0x0218:
            float r2 = r13.lastTouchX
            float r3 = r13.lastTouchY
            r0.didPressUserAvatar(r13, r1, r2, r3)
            goto L_0x0420
        L_0x0221:
            org.telegram.tgnet.TLRPC$Chat r2 = r13.currentChat
            if (r2 == 0) goto L_0x0420
            org.telegram.messenger.MessageObject r1 = r13.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r1 = r1.fwd_from
            int r3 = r1.channel_post
            float r4 = r13.lastTouchX
            float r5 = r13.lastTouchY
            r1 = r13
            r0.didPressChannelAvatar(r1, r2, r3, r4, r5)
            goto L_0x0420
        L_0x0237:
            int r1 = r14.getAction()
            if (r1 != r4) goto L_0x0241
            r13.avatarPressed = r0
            goto L_0x0420
        L_0x0241:
            int r1 = r14.getAction()
            if (r1 != r3) goto L_0x0420
            boolean r1 = r13.isAvatarVisible
            if (r1 == 0) goto L_0x0420
            org.telegram.messenger.ImageReceiver r1 = r13.avatarImage
            int r3 = r13.getTop()
            float r3 = (float) r3
            float r5 = r5 + r3
            boolean r1 = r1.isInsideImage(r2, r5)
            if (r1 != 0) goto L_0x0420
            r13.avatarPressed = r0
            goto L_0x0420
        L_0x025d:
            boolean r7 = r13.forwardNamePressed
            if (r7 == 0) goto L_0x02d0
            int r7 = r14.getAction()
            if (r7 != r1) goto L_0x029c
            r13.forwardNamePressed = r0
            r13.playSoundEffect(r0)
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r13.delegate
            if (r0 == 0) goto L_0x0420
            org.telegram.tgnet.TLRPC$Chat r2 = r13.currentForwardChannel
            if (r2 == 0) goto L_0x0286
            org.telegram.messenger.MessageObject r1 = r13.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r1 = r1.fwd_from
            int r3 = r1.channel_post
            float r4 = r13.lastTouchX
            float r5 = r13.lastTouchY
            r1 = r13
            r0.didPressChannelAvatar(r1, r2, r3, r4, r5)
            goto L_0x0420
        L_0x0286:
            org.telegram.tgnet.TLRPC$User r1 = r13.currentForwardUser
            if (r1 == 0) goto L_0x0293
            float r2 = r13.lastTouchX
            float r3 = r13.lastTouchY
            r0.didPressUserAvatar(r13, r1, r2, r3)
            goto L_0x0420
        L_0x0293:
            java.lang.String r1 = r13.currentForwardName
            if (r1 == 0) goto L_0x0420
            r0.didPressHiddenForward(r13)
            goto L_0x0420
        L_0x029c:
            int r1 = r14.getAction()
            if (r1 != r4) goto L_0x02a6
            r13.forwardNamePressed = r0
            goto L_0x0420
        L_0x02a6:
            int r1 = r14.getAction()
            if (r1 != r3) goto L_0x0420
            int r1 = r13.forwardNameX
            float r3 = (float) r1
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x02cc
            int r3 = r13.forwardedNameWidth
            int r1 = r1 + r3
            float r1 = (float) r1
            int r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x02cc
            int r1 = r13.forwardNameY
            float r2 = (float) r1
            int r2 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x02cc
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r1 = r1 + r2
            float r1 = (float) r1
            int r1 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x0420
        L_0x02cc:
            r13.forwardNamePressed = r0
            goto L_0x0420
        L_0x02d0:
            boolean r7 = r13.forwardBotPressed
            if (r7 == 0) goto L_0x0363
            int r7 = r14.getAction()
            if (r7 != r1) goto L_0x02f5
            r13.forwardBotPressed = r0
            r13.playSoundEffect(r0)
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r13.delegate
            if (r0 == 0) goto L_0x0420
            org.telegram.tgnet.TLRPC$User r1 = r13.currentViaBotUser
            if (r1 == 0) goto L_0x02ea
            java.lang.String r1 = r1.username
            goto L_0x02f0
        L_0x02ea:
            org.telegram.messenger.MessageObject r1 = r13.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.via_bot_name
        L_0x02f0:
            r0.didPressViaBot(r13, r1)
            goto L_0x0420
        L_0x02f5:
            int r1 = r14.getAction()
            if (r1 != r4) goto L_0x02ff
            r13.forwardBotPressed = r0
            goto L_0x0420
        L_0x02ff:
            int r1 = r14.getAction()
            if (r1 != r3) goto L_0x0420
            boolean r1 = r13.drawForwardedName
            if (r1 == 0) goto L_0x0333
            android.text.StaticLayout[] r1 = r13.forwardedNameLayout
            r1 = r1[r0]
            if (r1 == 0) goto L_0x0333
            int r1 = r13.forwardNameX
            float r3 = (float) r1
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x032f
            int r3 = r13.forwardedNameWidth
            int r1 = r1 + r3
            float r1 = (float) r1
            int r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x032f
            int r1 = r13.forwardNameY
            float r2 = (float) r1
            int r2 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x032f
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r1 = r1 + r2
            float r1 = (float) r1
            int r1 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x0420
        L_0x032f:
            r13.forwardBotPressed = r0
            goto L_0x0420
        L_0x0333:
            float r1 = r13.nameX
            int r3 = r13.viaNameWidth
            float r4 = (float) r3
            float r4 = r4 + r1
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 < 0) goto L_0x035f
            float r3 = (float) r3
            float r1 = r1 + r3
            int r3 = r13.viaWidth
            float r3 = (float) r3
            float r1 = r1 + r3
            int r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x035f
            float r1 = r13.nameY
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r2 = (float) r2
            float r1 = r1 - r2
            int r1 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r1 < 0) goto L_0x035f
            float r1 = r13.nameY
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r2 = (float) r2
            float r1 = r1 + r2
            int r1 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x0420
        L_0x035f:
            r13.forwardBotPressed = r0
            goto L_0x0420
        L_0x0363:
            boolean r7 = r13.replyPressed
            if (r7 == 0) goto L_0x03d3
            int r7 = r14.getAction()
            if (r7 != r1) goto L_0x038b
            r13.replyPressed = r0
            r13.playSoundEffect(r0)
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r13.delegate
            if (r0 == 0) goto L_0x0420
            org.telegram.messenger.MessageObject r0 = r13.currentMessageObject
            boolean r0 = r0.hasValidReplyMessageObject()
            if (r0 == 0) goto L_0x0420
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r13.delegate
            org.telegram.messenger.MessageObject r1 = r13.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.reply_to_msg_id
            r0.didPressReplyMessage(r13, r1)
            goto L_0x0420
        L_0x038b:
            int r1 = r14.getAction()
            if (r1 != r4) goto L_0x0395
            r13.replyPressed = r0
            goto L_0x0420
        L_0x0395:
            int r1 = r14.getAction()
            if (r1 != r3) goto L_0x0420
            org.telegram.messenger.MessageObject r1 = r13.currentMessageObject
            boolean r1 = r1.shouldDrawWithoutBackground()
            if (r1 == 0) goto L_0x03ae
            int r1 = r13.replyStartX
            int r3 = r13.replyNameWidth
            int r4 = r13.replyTextWidth
            int r3 = java.lang.Math.max(r3, r4)
            goto L_0x03b2
        L_0x03ae:
            int r1 = r13.replyStartX
            int r3 = r13.backgroundDrawableRight
        L_0x03b2:
            int r1 = r1 + r3
            int r3 = r13.replyStartX
            float r3 = (float) r3
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x03d0
            float r1 = (float) r1
            int r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x03d0
            int r1 = r13.replyStartY
            float r2 = (float) r1
            int r2 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x03d0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r1 = r1 + r2
            float r1 = (float) r1
            int r1 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x0420
        L_0x03d0:
            r13.replyPressed = r0
            goto L_0x0420
        L_0x03d3:
            boolean r7 = r13.sharePressed
            if (r7 == 0) goto L_0x0420
            int r7 = r14.getAction()
            if (r7 != r1) goto L_0x03ea
            r13.sharePressed = r0
            r13.playSoundEffect(r0)
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r13.delegate
            if (r0 == 0) goto L_0x041d
            r0.didPressShare(r13)
            goto L_0x041d
        L_0x03ea:
            int r1 = r14.getAction()
            if (r1 != r4) goto L_0x03f3
            r13.sharePressed = r0
            goto L_0x041d
        L_0x03f3:
            int r1 = r14.getAction()
            if (r1 != r3) goto L_0x041d
            int r1 = r13.shareStartX
            float r3 = (float) r1
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x041b
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r1 = r1 + r3
            float r1 = (float) r1
            int r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x041b
            int r1 = r13.shareStartY
            float r2 = (float) r1
            int r2 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x041b
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r1 = r1 + r2
            float r1 = (float) r1
            int r1 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x041d
        L_0x041b:
            r13.sharePressed = r0
        L_0x041d:
            r13.invalidate()
        L_0x0420:
            return r6
        L_0x0421:
            r13.checkTextSelection(r14)
            boolean r0 = super.onTouchEvent(r14)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkTextSelection(MotionEvent motionEvent) {
        int i;
        int i2;
        int i3;
        TextSelectionHelper.ChatListTextSelectionHelper textSelectionHelper = this.delegate.getTextSelectionHelper();
        if (textSelectionHelper == null) {
            return false;
        }
        ArrayList<MessageObject.TextLayoutBlock> arrayList = this.currentMessageObject.textLayoutBlocks;
        if (!(arrayList != null && !arrayList.isEmpty()) && !hasCaptionLayout()) {
            return false;
        }
        if ((!this.drawSelectionBackground && this.currentMessagesGroup == null) || (this.currentMessagesGroup != null && !this.delegate.hasSelectedMessages())) {
            return false;
        }
        if (!this.currentMessageObject.hasValidGroupId() || this.currentMessagesGroup == null) {
            if (hasCaptionLayout()) {
                textSelectionHelper.setIsDescription(false);
                textSelectionHelper.setMaybeTextCord(this.captionX, this.captionY);
            } else if (this.descriptionLayout == null || motionEvent.getY() <= ((float) this.descriptionY)) {
                textSelectionHelper.setIsDescription(false);
                textSelectionHelper.setMaybeTextCord(this.textX, this.textY);
            } else {
                textSelectionHelper.setIsDescription(true);
                if (this.hasGamePreview) {
                    i = this.unmovedTextX - AndroidUtilities.dp(10.0f);
                } else {
                    if (this.hasInvoicePreview) {
                        i3 = this.unmovedTextX;
                        i2 = AndroidUtilities.dp(1.0f);
                    } else {
                        i3 = this.unmovedTextX;
                        i2 = AndroidUtilities.dp(1.0f);
                    }
                    i = i3 + i2;
                }
                textSelectionHelper.setMaybeTextCord(i + AndroidUtilities.dp(10.0f) + this.descriptionX, this.descriptionY);
            }
            textSelectionHelper.setMessageObject(this);
            return textSelectionHelper.onTouchEvent(motionEvent);
        }
        ViewGroup viewGroup = (ViewGroup) getParent();
        for (int i4 = 0; i4 < viewGroup.getChildCount(); i4++) {
            View childAt = viewGroup.getChildAt(i4);
            if (childAt instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                MessageObject.GroupedMessages currentMessagesGroup2 = chatMessageCell.getCurrentMessagesGroup();
                MessageObject.GroupedMessagePosition currentPosition2 = chatMessageCell.getCurrentPosition();
                if (currentMessagesGroup2 != null && currentMessagesGroup2.groupId == this.currentMessagesGroup.groupId) {
                    int i5 = currentPosition2.flags;
                    if (!((i5 & 8) == 0 || (i5 & 1) == 0)) {
                        textSelectionHelper.setMaybeTextCord(chatMessageCell.captionX, chatMessageCell.captionY);
                        textSelectionHelper.setMessageObject(chatMessageCell);
                        if (chatMessageCell == this) {
                            return textSelectionHelper.onTouchEvent(motionEvent);
                        }
                        motionEvent.offsetLocation((float) (getLeft() - chatMessageCell.getLeft()), (float) (getTop() - chatMessageCell.getTop()));
                        return textSelectionHelper.onTouchEvent(motionEvent);
                    }
                }
            }
        }
        return false;
    }

    private void updateSelectionTextPosition() {
        int i;
        int i2;
        int i3;
        if (getDelegate().getTextSelectionHelper() != null && getDelegate().getTextSelectionHelper().isSelected(this.currentMessageObject)) {
            int textSelectionType = getDelegate().getTextSelectionHelper().getTextSelectionType(this);
            if (textSelectionType == TextSelectionHelper.ChatListTextSelectionHelper.TYPE_DESCRIPTION) {
                if (this.hasGamePreview) {
                    i = this.unmovedTextX - AndroidUtilities.dp(10.0f);
                } else {
                    if (this.hasInvoicePreview) {
                        i3 = this.unmovedTextX;
                        i2 = AndroidUtilities.dp(1.0f);
                    } else {
                        i3 = this.unmovedTextX;
                        i2 = AndroidUtilities.dp(1.0f);
                    }
                    i = i3 + i2;
                }
                getDelegate().getTextSelectionHelper().updateTextPosition(i + AndroidUtilities.dp(10.0f) + this.descriptionX, this.descriptionY);
            } else if (textSelectionType == TextSelectionHelper.ChatListTextSelectionHelper.TYPE_CAPTION) {
                getDelegate().getTextSelectionHelper().updateTextPosition(this.captionX, this.captionY);
            } else {
                getDelegate().getTextSelectionHelper().updateTextPosition(this.textX, this.textY);
            }
        }
    }

    public void updatePlayingMessageProgress() {
        int i;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            int i2 = 0;
            if (this.documentAttachType == 4) {
                if (this.infoLayout == null || (!PhotoViewer.isPlayingMessage(messageObject) && !MediaController.getInstance().isGoingToShowMessageObject(this.currentMessageObject))) {
                    AnimatedFileDrawable animation = this.photoImage.getAnimation();
                    if (animation != null) {
                        MessageObject messageObject2 = this.currentMessageObject;
                        i2 = animation.getDurationMs() / 1000;
                        messageObject2.audioPlayerDuration = i2;
                        MessageObject messageObject3 = this.currentMessageObject;
                        TLRPC.Message message = messageObject3.messageOwner;
                        if (message.ttl > 0 && message.destroyTime == 0 && !messageObject3.needDrawBluredPreview() && this.currentMessageObject.isVideo() && animation.hasBitmap()) {
                            this.delegate.didStartVideoStream(this.currentMessageObject);
                        }
                    }
                    if (i2 == 0) {
                        i2 = this.currentMessageObject.getDuration();
                    }
                    if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                        float f = (float) i2;
                        i2 = (int) (f - (this.currentMessageObject.audioProgress * f));
                    } else if (animation != null) {
                        if (i2 != 0) {
                            i2 -= animation.getCurrentProgressMs() / 1000;
                        }
                        if (this.delegate != null && animation.getCurrentProgressMs() >= 3000) {
                            this.delegate.videoTimerReached();
                        }
                    }
                    if (this.lastTime != i2) {
                        String formatShortDuration = AndroidUtilities.formatShortDuration(i2);
                        this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(formatShortDuration));
                        this.infoLayout = new StaticLayout(formatShortDuration, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        this.lastTime = i2;
                    }
                }
            } else if (messageObject.isRoundVideo()) {
                TLRPC.Document document = this.currentMessageObject.getDocument();
                int i3 = 0;
                while (true) {
                    if (i3 >= document.attributes.size()) {
                        i = 0;
                        break;
                    }
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i3);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                        i = documentAttribute.duration;
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
                    this.timeWidthAudio = (int) Math.ceil((double) Theme.chat_timePaint.measureText(formatLongDuration));
                    this.durationLayout = new StaticLayout(formatLongDuration, Theme.chat_timePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    invalidate();
                }
            } else if (this.documentAttach != null) {
                if (this.useSeekBarWaweform) {
                    if (!this.seekBarWaveform.isDragging()) {
                        this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress);
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
                            TLRPC.DocumentAttribute documentAttribute2 = this.documentAttach.attributes.get(i4);
                            if (documentAttribute2 instanceof TLRPC.TL_documentAttributeAudio) {
                                i2 = documentAttribute2.duration;
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
                        this.timeWidthAudio = (int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(formatLongDuration2));
                        this.durationLayout = new StaticLayout(formatLongDuration2, Theme.chat_audioTimePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }
                } else {
                    int duration = this.currentMessageObject.getDuration();
                    if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                        i2 = this.currentMessageObject.audioProgressSec;
                    }
                    if (this.lastTime != i2) {
                        this.lastTime = i2;
                        String formatShortDuration2 = AndroidUtilities.formatShortDuration(i2, duration);
                        this.durationLayout = new StaticLayout(formatShortDuration2, Theme.chat_audioTimePaint, (int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(formatShortDuration2)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }
                }
                invalidate();
            }
        }
    }

    public void setFullyDraw(boolean z) {
        this.fullyDraw = z;
    }

    public void setVisiblePart(int i, int i2, int i3) {
        if (i3 != this.parentHeight) {
            this.parentHeight = i3;
            invalidate();
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.textLayoutBlocks != null) {
            int i4 = i - this.textY;
            int i5 = 0;
            int i6 = 0;
            while (i5 < this.currentMessageObject.textLayoutBlocks.size() && this.currentMessageObject.textLayoutBlocks.get(i5).textYOffset <= ((float) i4)) {
                i6 = i5;
                i5++;
            }
            int i7 = -1;
            int i8 = -1;
            int i9 = 0;
            while (i6 < this.currentMessageObject.textLayoutBlocks.size()) {
                MessageObject.TextLayoutBlock textLayoutBlock = this.currentMessageObject.textLayoutBlocks.get(i6);
                float f = textLayoutBlock.textYOffset;
                float f2 = (float) i4;
                if (intersect(f, ((float) textLayoutBlock.height) + f, f2, (float) (i4 + i2))) {
                    if (i7 == -1) {
                        i7 = i6;
                    }
                    i9++;
                    i8 = i6;
                } else if (f > f2) {
                    break;
                }
                i6++;
            }
            if (this.lastVisibleBlockNum != i8 || this.firstVisibleBlockNum != i7 || this.totalVisibleBlocksCount != i9 || (this.currentBackgroundDrawable instanceof Theme.MessageDrawable)) {
                this.lastVisibleBlockNum = i8;
                this.firstVisibleBlockNum = i7;
                this.totalVisibleBlocksCount = i9;
                invalidate();
            }
        } else if (this.currentBackgroundDrawable instanceof Theme.MessageDrawable) {
            invalidate();
        }
    }

    public static StaticLayout generateStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, int i2, int i3, int i4) {
        CharSequence charSequence2 = charSequence;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        StaticLayout staticLayout = new StaticLayout(charSequence, textPaint, i2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int i5 = i;
        int i6 = i3;
        int i7 = 0;
        int i8 = 0;
        while (i7 < i6) {
            staticLayout.getLineDirections(i7);
            if (staticLayout.getLineLeft(i7) != 0.0f || staticLayout.isRtlCharAt(staticLayout.getLineStart(i7)) || staticLayout.isRtlCharAt(staticLayout.getLineEnd(i7))) {
                i5 = i2;
            }
            int lineEnd = staticLayout.getLineEnd(i7);
            if (lineEnd != charSequence.length()) {
                int i9 = (lineEnd - 1) + i8;
                if (spannableStringBuilder.charAt(i9) == ' ') {
                    spannableStringBuilder.replace(i9, i9 + 1, "\n");
                } else if (spannableStringBuilder.charAt(i9) != 10) {
                    spannableStringBuilder.insert(i9, "\n");
                    i8++;
                }
                if (i7 == staticLayout.getLineCount() - 1 || i7 == i4 - 1) {
                    break;
                }
                i7++;
            } else {
                break;
            }
        }
        int i10 = i5;
        return StaticLayoutEx.createStaticLayout(spannableStringBuilder, textPaint, i10, Layout.Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.dp(1.0f), false, TextUtils.TruncateAt.END, i10, i4, true);
    }

    private void didClickedImage() {
        TLRPC.WebPage webPage;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.type == 1 || messageObject.isAnyKindOfSticker()) {
            int i = this.buttonState;
            if (i == -1) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            } else if (i == 0) {
                didPressButton(true, false);
            }
        } else {
            MessageObject messageObject2 = this.currentMessageObject;
            int i2 = messageObject2.type;
            if (i2 == 12) {
                this.delegate.didPressUserAvatar(this, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)), this.lastTouchX, this.lastTouchY);
            } else if (i2 == 5) {
                if (this.buttonState != -1) {
                    didPressButton(true, false);
                } else if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isMessagePaused()) {
                    this.delegate.needPlayMessage(this.currentMessageObject);
                } else {
                    MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.currentMessageObject);
                }
            } else if (i2 == 8) {
                int i3 = this.buttonState;
                if (i3 == -1 || (i3 == 1 && this.canStreamVideo && this.autoPlayingMedia)) {
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                    return;
                }
                int i4 = this.buttonState;
                if (i4 == 2 || i4 == 0) {
                    didPressButton(true, false);
                }
            } else {
                int i5 = this.documentAttachType;
                if (i5 == 4) {
                    if (this.buttonState == -1 || (this.drawVideoImageButton && (this.autoPlayingMedia || (SharedConfig.streamMedia && this.canStreamVideo)))) {
                        this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                    } else if (this.drawVideoImageButton) {
                        didPressButton(true, true);
                    } else {
                        int i6 = this.buttonState;
                        if (i6 == 0 || i6 == 3) {
                            didPressButton(true, false);
                        }
                    }
                } else if (i2 == 4) {
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                } else if (i5 == 1) {
                    if (this.buttonState == -1) {
                        this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                    }
                } else if (i5 == 2) {
                    if (this.buttonState == -1 && (webPage = messageObject2.messageOwner.media.webpage) != null) {
                        String str = webPage.embed_url;
                        if (str == null || str.length() == 0) {
                            Browser.openUrl(getContext(), webPage.url);
                        } else {
                            this.delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.description, webPage.url, webPage.embed_width, webPage.embed_height);
                        }
                    }
                } else if (this.hasInvoicePreview && this.buttonState == -1) {
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                }
            }
        }
    }

    private void updateSecretTimeText(MessageObject messageObject) {
        String secretTimeString;
        if (messageObject != null && messageObject.needDrawBluredPreview() && (secretTimeString = messageObject.getSecretTimeString()) != null) {
            this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(secretTimeString));
            this.infoLayout = new StaticLayout(TextUtils.ellipsize(secretTimeString, Theme.chat_infoPaint, (float) this.infoWidth, TextUtils.TruncateAt.END), Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            invalidate();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00ce  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isPhotoDataChanged(org.telegram.messenger.MessageObject r25) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            int r2 = r1.type
            r3 = 0
            if (r2 == 0) goto L_0x0156
            r4 = 14
            if (r2 != r4) goto L_0x000f
            goto L_0x0156
        L_0x000f:
            r4 = 3
            r5 = 4
            r6 = 1
            if (r2 != r5) goto L_0x0123
            java.lang.String r2 = r0.currentUrl
            if (r2 != 0) goto L_0x0019
            return r6
        L_0x0019:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            org.telegram.tgnet.TLRPC$GeoPoint r2 = r2.geo
            double r8 = r2.lat
            double r13 = r2._long
            long r2 = r25.getDialogId()
            int r3 = (int) r2
            r2 = -1
            if (r3 != 0) goto L_0x003a
            int r3 = org.telegram.messenger.SharedConfig.mapPreviewType
            if (r3 != 0) goto L_0x0030
            goto L_0x003a
        L_0x0030:
            if (r3 != r6) goto L_0x0035
            r19 = 4
            goto L_0x003c
        L_0x0035:
            if (r3 != r4) goto L_0x003a
            r19 = 1
            goto L_0x003c
        L_0x003a:
            r19 = -1
        L_0x003c:
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            r3 = 1101529088(0x41a80000, float:21.0)
            r4 = 1128464384(0x43430000, float:195.0)
            if (r2 == 0) goto L_0x00ce
            int r1 = r0.backgroundWidth
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3 = 268435456(0x10000000, float:2.5243549E-29)
            double r3 = (double) r3
            r10 = 4614256656552045848(0x400921fb54442d18, double:3.NUM)
            java.lang.Double.isNaN(r3)
            double r15 = r3 / r10
            double r8 = r8 * r10
            r17 = 4640537203540230144(0xNUM, double:180.0)
            double r8 = r8 / r17
            double r20 = java.lang.Math.sin(r8)
            r22 = 4607182418800017408(0x3ffNUM, double:1.0)
            double r20 = r20 + r22
            double r7 = java.lang.Math.sin(r8)
            double r22 = r22 - r7
            double r20 = r20 / r22
            double r7 = java.lang.Math.log(r20)
            double r7 = r7 * r15
            r20 = 4611686018427387904(0xNUM, double:2.0)
            double r7 = r7 / r20
            java.lang.Double.isNaN(r3)
            double r7 = r3 - r7
            long r7 = java.lang.Math.round(r7)
            r5 = 1092930765(0x4124cccd, float:10.3)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r5 << 6
            long r10 = (long) r5
            long r7 = r7 - r10
            double r7 = (double) r7
            r9 = 4609753056924675352(0x3fvar_fb54442d18, double:1.NUM)
            java.lang.Double.isNaN(r7)
            java.lang.Double.isNaN(r3)
            double r7 = r7 - r3
            double r7 = r7 / r15
            double r3 = java.lang.Math.exp(r7)
            double r3 = java.lang.Math.atan(r3)
            double r3 = r3 * r20
            double r9 = r9 - r3
            double r9 = r9 * r17
            r3 = 4614256656552045848(0x400921fb54442d18, double:3.NUM)
            double r11 = r9 / r3
            int r10 = r0.currentAccount
            float r1 = (float) r1
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r1 = r1 / r3
            int r15 = (int) r1
            float r1 = (float) r2
            float r1 = r1 / r3
            int r1 = (int) r1
            r17 = 0
            r18 = 15
            r16 = r1
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r10, r11, r13, r15, r16, r17, r18, r19)
            goto L_0x011b
        L_0x00ce:
            java.lang.String r1 = r1.title
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x00f8
            int r1 = r0.backgroundWidth
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = r1 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = r0.currentAccount
            float r1 = (float) r1
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r1 = r1 / r3
            int r12 = (int) r1
            float r1 = (float) r2
            float r1 = r1 / r3
            int r1 = (int) r1
            r2 = 1
            r15 = 15
            r10 = r13
            r13 = r1
            r14 = r2
            r16 = r19
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r7, r8, r10, r12, r13, r14, r15, r16)
            goto L_0x011b
        L_0x00f8:
            int r1 = r0.backgroundWidth
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = r0.currentAccount
            float r1 = (float) r1
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r1 = r1 / r3
            int r12 = (int) r1
            float r1 = (float) r2
            float r1 = r1 / r3
            int r1 = (int) r1
            r2 = 1
            r15 = 15
            r10 = r13
            r13 = r1
            r14 = r2
            r16 = r19
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r7, r8, r10, r12, r13, r14, r15, r16)
        L_0x011b:
            java.lang.String r2 = r0.currentUrl
            boolean r1 = r1.equals(r2)
            r1 = r1 ^ r6
            return r1
        L_0x0123:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r0.currentPhotoObject
            if (r2 == 0) goto L_0x0142
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.location
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable
            if (r2 == 0) goto L_0x012e
            goto L_0x0142
        L_0x012e:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 == 0) goto L_0x0141
            boolean r2 = r0.photoNotSet
            if (r2 == 0) goto L_0x0141
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            boolean r1 = r1.exists()
            return r1
        L_0x0141:
            return r3
        L_0x0142:
            int r2 = r1.type
            if (r2 == r6) goto L_0x0155
            r5 = 5
            if (r2 == r5) goto L_0x0155
            if (r2 == r4) goto L_0x0155
            r4 = 8
            if (r2 == r4) goto L_0x0155
            boolean r1 = r25.isAnyKindOfSticker()
            if (r1 == 0) goto L_0x0156
        L_0x0155:
            r3 = 1
        L_0x0156:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.isPhotoDataChanged(org.telegram.messenger.MessageObject):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x006e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x006f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isUserDataChanged() {
        /*
            r8 = this;
            org.telegram.messenger.MessageObject r0 = r8.currentMessageObject
            r1 = 1
            if (r0 == 0) goto L_0x0016
            boolean r2 = r8.hasLinkPreview
            if (r2 != 0) goto L_0x0016
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0016
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPage
            if (r0 == 0) goto L_0x0016
            return r1
        L_0x0016:
            org.telegram.messenger.MessageObject r0 = r8.currentMessageObject
            r2 = 0
            if (r0 == 0) goto L_0x011c
            org.telegram.tgnet.TLRPC$User r0 = r8.currentUser
            if (r0 != 0) goto L_0x0025
            org.telegram.tgnet.TLRPC$Chat r0 = r8.currentChat
            if (r0 != 0) goto L_0x0025
            goto L_0x011c
        L_0x0025:
            int r0 = r8.lastSendState
            org.telegram.messenger.MessageObject r3 = r8.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            int r4 = r3.send_state
            if (r0 == r4) goto L_0x0030
            return r1
        L_0x0030:
            int r0 = r8.lastDeleteDate
            int r4 = r3.destroyTime
            if (r0 == r4) goto L_0x0037
            return r1
        L_0x0037:
            int r0 = r8.lastViewsCount
            int r4 = r3.views
            if (r0 == r4) goto L_0x003e
            return r1
        L_0x003e:
            org.telegram.tgnet.TLRPC$TL_messageReactions r0 = r8.lastReactions
            org.telegram.tgnet.TLRPC$TL_messageReactions r3 = r3.reactions
            if (r0 == r3) goto L_0x0045
            return r1
        L_0x0045:
            r8.updateCurrentUserAndChat()
            boolean r0 = r8.isAvatarVisible
            r3 = 0
            if (r0 == 0) goto L_0x0063
            org.telegram.tgnet.TLRPC$User r0 = r8.currentUser
            if (r0 == 0) goto L_0x0058
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo
            if (r0 == 0) goto L_0x0058
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small
            goto L_0x0064
        L_0x0058:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.currentChat
            if (r0 == 0) goto L_0x0063
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r0.photo
            if (r0 == 0) goto L_0x0063
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small
            goto L_0x0064
        L_0x0063:
            r0 = r3
        L_0x0064:
            android.text.StaticLayout r4 = r8.replyTextLayout
            if (r4 != 0) goto L_0x006f
            org.telegram.messenger.MessageObject r4 = r8.currentMessageObject
            org.telegram.messenger.MessageObject r4 = r4.replyMessageObject
            if (r4 == 0) goto L_0x006f
            return r1
        L_0x006f:
            org.telegram.tgnet.TLRPC$FileLocation r4 = r8.currentPhoto
            if (r4 != 0) goto L_0x0075
            if (r0 != 0) goto L_0x008f
        L_0x0075:
            org.telegram.tgnet.TLRPC$FileLocation r4 = r8.currentPhoto
            if (r4 == 0) goto L_0x007b
            if (r0 == 0) goto L_0x008f
        L_0x007b:
            org.telegram.tgnet.TLRPC$FileLocation r4 = r8.currentPhoto
            if (r4 == 0) goto L_0x0090
            if (r0 == 0) goto L_0x0090
            int r5 = r4.local_id
            int r6 = r0.local_id
            if (r5 != r6) goto L_0x008f
            long r4 = r4.volume_id
            long r6 = r0.volume_id
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x0090
        L_0x008f:
            return r1
        L_0x0090:
            android.text.StaticLayout r0 = r8.replyNameLayout
            if (r0 == 0) goto L_0x00af
            org.telegram.messenger.MessageObject r0 = r8.currentMessageObject
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 == 0) goto L_0x00af
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.photoThumbs
            r4 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r4)
            if (r0 == 0) goto L_0x00af
            org.telegram.messenger.MessageObject r4 = r8.currentMessageObject
            org.telegram.messenger.MessageObject r4 = r4.replyMessageObject
            boolean r4 = r4.isAnyKindOfSticker()
            if (r4 != 0) goto L_0x00af
            goto L_0x00b0
        L_0x00af:
            r0 = r3
        L_0x00b0:
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r8.currentReplyPhoto
            if (r4 != 0) goto L_0x00b7
            if (r0 == 0) goto L_0x00b7
            return r1
        L_0x00b7:
            boolean r0 = r8.drawName
            if (r0 == 0) goto L_0x00d6
            boolean r0 = r8.isChat
            if (r0 == 0) goto L_0x00d6
            org.telegram.messenger.MessageObject r0 = r8.currentMessageObject
            boolean r0 = r0.isOutOwner()
            if (r0 != 0) goto L_0x00d6
            org.telegram.tgnet.TLRPC$User r0 = r8.currentUser
            if (r0 == 0) goto L_0x00d0
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r0)
            goto L_0x00d6
        L_0x00d0:
            org.telegram.tgnet.TLRPC$Chat r0 = r8.currentChat
            if (r0 == 0) goto L_0x00d6
            java.lang.String r3 = r0.title
        L_0x00d6:
            java.lang.String r0 = r8.currentNameString
            if (r0 != 0) goto L_0x00dc
            if (r3 != 0) goto L_0x00ee
        L_0x00dc:
            java.lang.String r0 = r8.currentNameString
            if (r0 == 0) goto L_0x00e2
            if (r3 == 0) goto L_0x00ee
        L_0x00e2:
            java.lang.String r0 = r8.currentNameString
            if (r0 == 0) goto L_0x00ef
            if (r3 == 0) goto L_0x00ef
            boolean r0 = r0.equals(r3)
            if (r0 != 0) goto L_0x00ef
        L_0x00ee:
            return r1
        L_0x00ef:
            boolean r0 = r8.drawForwardedName
            if (r0 == 0) goto L_0x011c
            org.telegram.messenger.MessageObject r0 = r8.currentMessageObject
            boolean r0 = r0.needDrawForwarded()
            if (r0 == 0) goto L_0x011c
            org.telegram.messenger.MessageObject r0 = r8.currentMessageObject
            java.lang.String r0 = r0.getForwardedName()
            java.lang.String r3 = r8.currentForwardNameString
            if (r3 != 0) goto L_0x0107
            if (r0 != 0) goto L_0x011b
        L_0x0107:
            java.lang.String r3 = r8.currentForwardNameString
            if (r3 == 0) goto L_0x010d
            if (r0 == 0) goto L_0x011b
        L_0x010d:
            java.lang.String r3 = r8.currentForwardNameString
            if (r3 == 0) goto L_0x011a
            if (r0 == 0) goto L_0x011a
            boolean r0 = r3.equals(r0)
            if (r0 != 0) goto L_0x011a
            goto L_0x011b
        L_0x011a:
            r1 = 0
        L_0x011b:
            return r1
        L_0x011c:
            return r2
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
        TLRPC.User user = this.currentUser;
        if (user == null || user.id != 0) {
            return this.forwardNameX + this.forwardNameCenterX;
        }
        return (int) this.avatarImage.getCenterX();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!this.animationRunning) {
            CheckBoxBase checkBoxBase = this.checkBox;
            if (checkBoxBase != null) {
                checkBoxBase.onDetachedFromWindow();
            }
            CheckBoxBase checkBoxBase2 = this.photoCheckBox;
            if (checkBoxBase2 != null) {
                checkBoxBase2.onDetachedFromWindow();
            }
            this.attachedToWindow = false;
            this.radialProgress.onDetachedFromWindow();
            this.videoRadialProgress.onDetachedFromWindow();
            this.avatarImage.onDetachedFromWindow();
            this.replyImageReceiver.onDetachedFromWindow();
            this.locationImageReceiver.onDetachedFromWindow();
            this.photoImage.onDetachedFromWindow();
            if (!(!this.addedForTest || this.currentUrl == null || this.currentWebFile == null)) {
                ImageLoader.getInstance().removeTestWebFile(this.currentUrl);
                this.addedForTest = false;
            }
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            if (getDelegate().getTextSelectionHelper() != null) {
                getDelegate().getTextSelectionHelper().onChatMessageCellDetached(this);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        MessageObject messageObject = this.messageObjectToSet;
        if (messageObject != null) {
            setMessageContent(messageObject, this.groupedMessagesToSet, this.bottomNearToSet, this.topNearToSet);
            this.messageObjectToSet = null;
            this.groupedMessagesToSet = null;
        }
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.onAttachedToWindow();
        }
        CheckBoxBase checkBoxBase2 = this.photoCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.onAttachedToWindow();
        }
        this.attachedToWindow = true;
        float f = 0.0f;
        setTranslationX(0.0f);
        this.radialProgress.onAttachedToWindow();
        this.videoRadialProgress.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        this.avatarImage.setParentView((View) getParent());
        this.replyImageReceiver.onAttachedToWindow();
        this.locationImageReceiver.onAttachedToWindow();
        if (!this.photoImage.onAttachedToWindow()) {
            updateButtonState(false, false, false);
        } else if (this.drawPhotoImage) {
            updateButtonState(false, false, false);
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null && (messageObject2.isRoundVideo() || this.currentMessageObject.isVideo())) {
            checkVideoPlayback(true);
        }
        if (this.documentAttachType != 4 || !this.autoPlayingMedia) {
            this.animatingNoSoundPlaying = false;
            this.animatingNoSoundProgress = 0.0f;
            int i = this.documentAttachType;
            if ((i == 4 || i == 2) && this.drawVideoSize) {
                f = 1.0f;
            }
            this.animatingDrawVideoImageButtonProgress = f;
        } else {
            this.animatingNoSoundPlaying = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            if (!this.animatingNoSoundPlaying) {
                f = 1.0f;
            }
            this.animatingNoSoundProgress = f;
            this.animatingNoSound = 0;
        }
        if (getDelegate().getTextSelectionHelper() != null) {
            getDelegate().getTextSelectionHelper().onChatMessageCellAttached(this);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Cells.ChatMessageCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v188, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v189, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v196, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v154, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v197, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v204, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v210, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v212, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v217, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v191, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v193, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v234, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v235, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v238, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v239, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v70, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v72, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v51, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v107, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v68, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r62v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v118, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v119, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v257, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v69, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v70, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v78, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v77, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v149, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v151, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v274, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v153, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v154, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v157, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v158, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v160, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v154, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v156, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v163, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v171, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v284, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r57v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v285, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v248, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v287, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v249, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v250, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v161, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v251, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v288, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v292, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v252, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v264, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v268, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v270, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v271, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v169, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v312, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v274, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v170, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v317, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v320, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v172, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v173, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v327, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v282, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v283, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v285, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v93, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v87, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v297, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v316, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v97, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v211, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v329, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v212, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v339, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v419, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v213, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v341, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v215, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v216, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v343, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v218, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v85, resolved type: org.telegram.messenger.DocumentObject$ThemeDocument} */
    /* JADX WARNING: type inference failed for: r15v2, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r15v3 */
    /* JADX WARNING: type inference failed for: r15v99 */
    /* JADX WARNING: Code restructure failed: missing block: B:1665:0x28cc, code lost:
        if (r0 < (r1.timeWidth + org.telegram.messenger.AndroidUtilities.dp((float) ((r59.isOutOwner() ? 20 : 0) + 20)))) goto L_0x28ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:2150:?, code lost:
        r1.captionWidth = r12;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1111:0x186d  */
    /* JADX WARNING: Removed duplicated region for block: B:1139:0x19a4  */
    /* JADX WARNING: Removed duplicated region for block: B:1469:0x2293  */
    /* JADX WARNING: Removed duplicated region for block: B:1470:0x2299  */
    /* JADX WARNING: Removed duplicated region for block: B:1486:0x22d7  */
    /* JADX WARNING: Removed duplicated region for block: B:1526:0x23bb  */
    /* JADX WARNING: Removed duplicated region for block: B:1533:0x23ee  */
    /* JADX WARNING: Removed duplicated region for block: B:1536:0x23f3  */
    /* JADX WARNING: Removed duplicated region for block: B:1545:0x2422  */
    /* JADX WARNING: Removed duplicated region for block: B:1552:0x2450  */
    /* JADX WARNING: Removed duplicated region for block: B:1555:0x245e  */
    /* JADX WARNING: Removed duplicated region for block: B:1556:0x246b  */
    /* JADX WARNING: Removed duplicated region for block: B:1569:0x24c6  */
    /* JADX WARNING: Removed duplicated region for block: B:1571:0x24ce  */
    /* JADX WARNING: Removed duplicated region for block: B:1575:0x24e0  */
    /* JADX WARNING: Removed duplicated region for block: B:1590:0x2510  */
    /* JADX WARNING: Removed duplicated region for block: B:1630:0x275b  */
    /* JADX WARNING: Removed duplicated region for block: B:1697:0x29b7  */
    /* JADX WARNING: Removed duplicated region for block: B:1708:0x29cf  */
    /* JADX WARNING: Removed duplicated region for block: B:1711:0x29de  */
    /* JADX WARNING: Removed duplicated region for block: B:1712:0x29f4  */
    /* JADX WARNING: Removed duplicated region for block: B:1817:0x2cab  */
    /* JADX WARNING: Removed duplicated region for block: B:1829:0x2cdc  */
    /* JADX WARNING: Removed duplicated region for block: B:1839:0x2d00  */
    /* JADX WARNING: Removed duplicated region for block: B:1840:0x2d0e  */
    /* JADX WARNING: Removed duplicated region for block: B:1851:0x2d3b  */
    /* JADX WARNING: Removed duplicated region for block: B:1852:0x2d3f  */
    /* JADX WARNING: Removed duplicated region for block: B:1872:0x2d92  */
    /* JADX WARNING: Removed duplicated region for block: B:1873:0x2d96  */
    /* JADX WARNING: Removed duplicated region for block: B:1880:0x2da4  */
    /* JADX WARNING: Removed duplicated region for block: B:1881:0x2da7  */
    /* JADX WARNING: Removed duplicated region for block: B:1906:0x2df2  */
    /* JADX WARNING: Removed duplicated region for block: B:1913:0x2e19  */
    /* JADX WARNING: Removed duplicated region for block: B:1914:0x2e20  */
    /* JADX WARNING: Removed duplicated region for block: B:1932:0x2e6d  */
    /* JADX WARNING: Removed duplicated region for block: B:1939:0x2eaf  */
    /* JADX WARNING: Removed duplicated region for block: B:1942:0x2ebd  */
    /* JADX WARNING: Removed duplicated region for block: B:1945:0x2eea  */
    /* JADX WARNING: Removed duplicated region for block: B:1946:0x2eed  */
    /* JADX WARNING: Removed duplicated region for block: B:1949:0x2ef8  */
    /* JADX WARNING: Removed duplicated region for block: B:1952:0x2eff  */
    /* JADX WARNING: Removed duplicated region for block: B:1954:0x2f0f  */
    /* JADX WARNING: Removed duplicated region for block: B:1963:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:2114:0x3254  */
    /* JADX WARNING: Removed duplicated region for block: B:2131:0x32a9 A[SYNTHETIC, Splitter:B:2131:0x32a9] */
    /* JADX WARNING: Removed duplicated region for block: B:2181:0x33bb  */
    /* JADX WARNING: Removed duplicated region for block: B:2185:0x33ca  */
    /* JADX WARNING: Removed duplicated region for block: B:2189:0x33ec  */
    /* JADX WARNING: Removed duplicated region for block: B:2202:0x3431  */
    /* JADX WARNING: Removed duplicated region for block: B:2203:0x345c  */
    /* JADX WARNING: Removed duplicated region for block: B:2210:0x347e  */
    /* JADX WARNING: Removed duplicated region for block: B:2211:0x3480  */
    /* JADX WARNING: Removed duplicated region for block: B:2214:0x3485 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:2227:0x34a0  */
    /* JADX WARNING: Removed duplicated region for block: B:2247:0x34dd  */
    /* JADX WARNING: Removed duplicated region for block: B:2250:0x34e8  */
    /* JADX WARNING: Removed duplicated region for block: B:2258:0x3538  */
    /* JADX WARNING: Removed duplicated region for block: B:2261:0x353d  */
    /* JADX WARNING: Removed duplicated region for block: B:2262:0x358b  */
    /* JADX WARNING: Removed duplicated region for block: B:2352:0x37a4  */
    /* JADX WARNING: Removed duplicated region for block: B:2355:0x37b5  */
    /* JADX WARNING: Removed duplicated region for block: B:2367:0x37f2  */
    /* JADX WARNING: Removed duplicated region for block: B:2374:0x381f  */
    /* JADX WARNING: Removed duplicated region for block: B:2377:0x3824  */
    /* JADX WARNING: Removed duplicated region for block: B:2380:0x3831  */
    /* JADX WARNING: Removed duplicated region for block: B:2384:0x3857  */
    /* JADX WARNING: Removed duplicated region for block: B:2402:0x38b5 A[Catch:{ Exception -> 0x38f3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:2403:0x38d9 A[Catch:{ Exception -> 0x38f3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:2408:0x38fb A[SYNTHETIC, Splitter:B:2408:0x38fb] */
    /* JADX WARNING: Removed duplicated region for block: B:2440:0x3a03 A[Catch:{ Exception -> 0x3a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:2441:0x3a05 A[Catch:{ Exception -> 0x3a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:2448:0x3a2b A[Catch:{ Exception -> 0x3a98 }] */
    /* JADX WARNING: Removed duplicated region for block: B:2452:0x3a76 A[Catch:{ Exception -> 0x3a98 }] */
    /* JADX WARNING: Removed duplicated region for block: B:2463:0x3aa9  */
    /* JADX WARNING: Removed duplicated region for block: B:2468:0x3ac9  */
    /* JADX WARNING: Removed duplicated region for block: B:2471:0x3ada  */
    /* JADX WARNING: Removed duplicated region for block: B:2559:0x3e05  */
    /* JADX WARNING: Removed duplicated region for block: B:2560:0x3e0f  */
    /* JADX WARNING: Removed duplicated region for block: B:2576:0x3e46  */
    /* JADX WARNING: Removed duplicated region for block: B:2577:0x3e4f  */
    /* JADX WARNING: Removed duplicated region for block: B:2582:0x3e64  */
    /* JADX WARNING: Removed duplicated region for block: B:2585:0x3e6f  */
    /* JADX WARNING: Removed duplicated region for block: B:2592:0x3ea1  */
    /* JADX WARNING: Removed duplicated region for block: B:2594:0x3ea9  */
    /* JADX WARNING: Removed duplicated region for block: B:2599:0x3eb5  */
    /* JADX WARNING: Removed duplicated region for block: B:2600:0x3eb7  */
    /* JADX WARNING: Removed duplicated region for block: B:2672:0x2e43 A[EDGE_INSN: B:2672:0x2e43->B:1921:0x2e43 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x06a8  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0702  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x073c  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x073e  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0757  */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x0787  */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x0796  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x09c9  */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x0b23  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0b32 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x0bdb  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0cfc A[Catch:{ Exception -> 0x0d17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x0d26  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0d3c  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0d45  */
    /* JADX WARNING: Removed duplicated region for block: B:872:0x123e  */
    /* JADX WARNING: Removed duplicated region for block: B:887:0x1288  */
    /* JADX WARNING: Removed duplicated region for block: B:903:0x12b4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setMessageContent(org.telegram.messenger.MessageObject r59, org.telegram.messenger.MessageObject.GroupedMessages r60, boolean r61, boolean r62) {
        /*
            r58 = this;
            r1 = r58
            r14 = r59
            r0 = r60
            r2 = r61
            r3 = r62
            boolean r4 = r59.checkLayout()
            r15 = 0
            if (r4 != 0) goto L_0x001d
            org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = r1.currentPosition
            if (r4 == 0) goto L_0x001f
            int r4 = r1.lastHeight
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r5.y
            if (r4 == r5) goto L_0x001f
        L_0x001d:
            r1.currentMessageObject = r15
        L_0x001f:
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.y
            r1.lastHeight = r4
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            r13 = 1
            r12 = 0
            if (r4 == 0) goto L_0x0039
            int r4 = r4.getId()
            int r5 = r59.getId()
            if (r4 == r5) goto L_0x0036
            goto L_0x0039
        L_0x0036:
            r16 = 0
            goto L_0x003b
        L_0x0039:
            r16 = 1
        L_0x003b:
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            if (r4 != r14) goto L_0x0047
            boolean r4 = r14.forceUpdate
            if (r4 == 0) goto L_0x0044
            goto L_0x0047
        L_0x0044:
            r17 = 0
            goto L_0x0049
        L_0x0047:
            r17 = 1
        L_0x0049:
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            r11 = 3
            if (r4 == 0) goto L_0x0062
            int r4 = r4.getId()
            int r5 = r59.getId()
            if (r4 != r5) goto L_0x0062
            int r4 = r1.lastSendState
            if (r4 != r11) goto L_0x0062
            boolean r4 = r59.isSent()
            if (r4 != 0) goto L_0x0070
        L_0x0062:
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            if (r4 != r14) goto L_0x0073
            boolean r4 = r58.isUserDataChanged()
            if (r4 != 0) goto L_0x0070
            boolean r4 = r1.photoNotSet
            if (r4 == 0) goto L_0x0073
        L_0x0070:
            r18 = 1
            goto L_0x0075
        L_0x0073:
            r18 = 0
        L_0x0075:
            org.telegram.messenger.MessageObject$GroupedMessages r4 = r1.currentMessagesGroup
            if (r0 == r4) goto L_0x007b
            r4 = 1
            goto L_0x007c
        L_0x007b:
            r4 = 0
        L_0x007c:
            r10 = 0
            if (r17 != 0) goto L_0x00d1
            int r5 = r14.type
            r6 = 17
            if (r5 != r6) goto L_0x00d1
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r6 == 0) goto L_0x0098
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$TL_pollResults r6 = r5.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r7 = r6.results
            org.telegram.tgnet.TLRPC$TL_poll r5 = r5.poll
            int r6 = r6.total_voters
            goto L_0x009b
        L_0x0098:
            r5 = r15
            r7 = r5
            r6 = 0
        L_0x009b:
            if (r7 == 0) goto L_0x00a7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r8 = r1.lastPollResults
            if (r8 == 0) goto L_0x00a7
            int r8 = r1.lastPollResultsVoters
            if (r6 == r8) goto L_0x00a7
            r6 = 1
            goto L_0x00a8
        L_0x00a7:
            r6 = 0
        L_0x00a8:
            if (r6 != 0) goto L_0x00af
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r8 = r1.lastPollResults
            if (r7 == r8) goto L_0x00af
            r6 = 1
        L_0x00af:
            if (r6 != 0) goto L_0x00bc
            org.telegram.tgnet.TLRPC$TL_poll r7 = r1.lastPoll
            if (r7 == r5) goto L_0x00bc
            boolean r7 = r7.closed
            boolean r5 = r5.closed
            if (r7 == r5) goto L_0x00bc
            r6 = 1
        L_0x00bc:
            if (r6 == 0) goto L_0x00d2
            boolean r5 = r1.attachedToWindow
            if (r5 == 0) goto L_0x00d2
            r1.pollAnimationProgressTime = r10
            boolean r5 = r1.pollVoted
            if (r5 == 0) goto L_0x00d2
            boolean r5 = r59.isVoted()
            if (r5 != 0) goto L_0x00d2
            r1.pollUnvoteInProgress = r13
            goto L_0x00d2
        L_0x00d1:
            r6 = 0
        L_0x00d2:
            if (r4 != 0) goto L_0x00f3
            if (r0 == 0) goto L_0x00f3
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.messages
            int r4 = r4.size()
            if (r4 <= r13) goto L_0x00eb
            org.telegram.messenger.MessageObject$GroupedMessages r4 = r1.currentMessagesGroup
            java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r4.positions
            org.telegram.messenger.MessageObject r5 = r1.currentMessageObject
            java.lang.Object r4 = r4.get(r5)
            org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4
            goto L_0x00ec
        L_0x00eb:
            r4 = r15
        L_0x00ec:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = r1.currentPosition
            if (r4 == r5) goto L_0x00f2
            r4 = 1
            goto L_0x00f3
        L_0x00f2:
            r4 = 0
        L_0x00f3:
            r9 = 2
            if (r17 != 0) goto L_0x010e
            if (r18 != 0) goto L_0x010e
            if (r4 != 0) goto L_0x010e
            if (r6 != 0) goto L_0x010e
            boolean r4 = r58.isPhotoDataChanged(r59)
            if (r4 != 0) goto L_0x010e
            boolean r4 = r1.pinnedBottom
            if (r4 != r2) goto L_0x010e
            boolean r4 = r1.pinnedTop
            if (r4 == r3) goto L_0x010b
            goto L_0x010e
        L_0x010b:
            r15 = 0
            goto L_0x3ea7
        L_0x010e:
            r1.pinnedBottom = r2
            r1.pinnedTop = r3
            r1.currentMessageObject = r14
            r1.currentMessagesGroup = r0
            r0 = -2
            r1.lastTime = r0
            r1.isHighlightedAnimated = r12
            r8 = -1
            r1.widthBeforeNewTimeLine = r8
            org.telegram.messenger.MessageObject$GroupedMessages r0 = r1.currentMessagesGroup
            if (r0 == 0) goto L_0x013f
            java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r0.posArray
            int r0 = r0.size()
            if (r0 <= r13) goto L_0x013f
            org.telegram.messenger.MessageObject$GroupedMessages r0 = r1.currentMessagesGroup
            java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r0.positions
            org.telegram.messenger.MessageObject r2 = r1.currentMessageObject
            java.lang.Object r0 = r0.get(r2)
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r0
            r1.currentPosition = r0
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r1.currentPosition
            if (r0 != 0) goto L_0x0143
            r1.currentMessagesGroup = r15
            goto L_0x0143
        L_0x013f:
            r1.currentMessagesGroup = r15
            r1.currentPosition = r15
        L_0x0143:
            boolean r0 = r1.pinnedTop
            if (r0 == 0) goto L_0x0153
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r1.currentPosition
            if (r0 == 0) goto L_0x0151
            int r0 = r0.flags
            r0 = r0 & 4
            if (r0 == 0) goto L_0x0153
        L_0x0151:
            r0 = 1
            goto L_0x0154
        L_0x0153:
            r0 = 0
        L_0x0154:
            r1.drawPinnedTop = r0
            boolean r0 = r1.pinnedBottom
            r7 = 8
            if (r0 == 0) goto L_0x0167
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r1.currentPosition
            if (r0 == 0) goto L_0x0165
            int r0 = r0.flags
            r0 = r0 & r7
            if (r0 == 0) goto L_0x0167
        L_0x0165:
            r0 = 1
            goto L_0x0168
        L_0x0167:
            r0 = 0
        L_0x0168:
            r1.drawPinnedBottom = r0
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r0.setCrossfadeWithOldImage(r12)
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            int r2 = r0.send_state
            r1.lastSendState = r2
            int r2 = r0.destroyTime
            r1.lastDeleteDate = r2
            int r0 = r0.views
            r1.lastViewsCount = r0
            r1.isPressed = r12
            r1.gamePreviewPressed = r12
            r1.sharePressed = r12
            r1.isCheckPressed = r13
            r1.hasNewLineForTime = r12
            boolean r0 = r1.isChat
            if (r0 == 0) goto L_0x01a1
            boolean r0 = r59.isOutOwner()
            if (r0 != 0) goto L_0x01a1
            boolean r0 = r59.needDrawAvatar()
            if (r0 == 0) goto L_0x01a1
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r1.currentPosition
            if (r0 == 0) goto L_0x019f
            boolean r0 = r0.edge
            if (r0 == 0) goto L_0x01a1
        L_0x019f:
            r0 = 1
            goto L_0x01a2
        L_0x01a1:
            r0 = 0
        L_0x01a2:
            r1.isAvatarVisible = r0
            r1.wasLayout = r12
            r1.drwaShareGoIcon = r12
            r1.groupPhotoInvisible = r12
            r1.animatingDrawVideoImageButton = r12
            r1.drawVideoSize = r12
            r1.canStreamVideo = r12
            r1.animatingNoSound = r12
            boolean r0 = r58.checkNeedDrawShareButton(r59)
            r1.drawShareButton = r0
            r1.replyNameLayout = r15
            r1.adminLayout = r15
            r1.checkOnlyButtonPressed = r12
            r1.replyTextLayout = r15
            r1.hasEmbed = r12
            r1.autoPlayingMedia = r12
            r1.replyNameWidth = r12
            r1.replyTextWidth = r12
            r1.viaWidth = r12
            r1.viaNameWidth = r12
            r1.addedCaptionHeight = r12
            r1.currentReplyPhoto = r15
            r1.currentUser = r15
            r1.currentChat = r15
            r1.currentViaBotUser = r15
            r1.instantViewLayout = r15
            r1.drawNameLayout = r12
            boolean r0 = r1.scheduledInvalidate
            if (r0 == 0) goto L_0x01e5
            java.lang.Runnable r0 = r1.invalidateRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r1.scheduledInvalidate = r12
        L_0x01e5:
            r1.resetPressedLink(r8)
            r14.forceUpdate = r12
            r1.drawPhotoImage = r12
            r1.drawPhotoCheckBox = r12
            r1.hasLinkPreview = r12
            r1.hasOldCaptionPreview = r12
            r1.hasGamePreview = r12
            r1.hasInvoicePreview = r12
            r1.instantButtonPressed = r12
            r1.instantPressed = r12
            if (r6 != 0) goto L_0x0210
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r0 < r2) goto L_0x0210
            android.graphics.drawable.Drawable r0 = r1.selectorDrawable
            if (r0 == 0) goto L_0x0210
            r0.setVisible(r12, r12)
            android.graphics.drawable.Drawable r0 = r1.selectorDrawable
            int[] r2 = android.util.StateSet.NOTHING
            r0.setState(r2)
        L_0x0210:
            r1.linkPreviewPressed = r12
            r1.buttonPressed = r12
            r1.miniButtonPressed = r12
            r1.pressedBotButton = r8
            r1.pressedVoteButton = r8
            r1.linkPreviewHeight = r12
            r1.mediaOffsetY = r12
            r1.documentAttachType = r12
            r1.documentAttach = r15
            r1.descriptionLayout = r15
            r1.titleLayout = r15
            r1.videoInfoLayout = r15
            r1.photosCountLayout = r15
            r1.siteNameLayout = r15
            r1.authorLayout = r15
            r1.captionLayout = r15
            r1.captionOffsetX = r12
            r1.currentCaption = r15
            r1.docTitleLayout = r15
            r1.drawImageButton = r12
            r1.drawVideoImageButton = r12
            r1.currentPhotoObject = r15
            r1.photoParentObject = r15
            r1.currentPhotoObjectThumb = r15
            r1.currentPhotoFilter = r15
            r1.infoLayout = r15
            r1.cancelLoading = r12
            r1.buttonState = r8
            r1.miniButtonState = r8
            r1.hasMiniProgress = r12
            boolean r0 = r1.addedForTest
            if (r0 == 0) goto L_0x0261
            java.lang.String r0 = r1.currentUrl
            if (r0 == 0) goto L_0x0261
            org.telegram.messenger.WebFile r0 = r1.currentWebFile
            if (r0 == 0) goto L_0x0261
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r2 = r1.currentUrl
            r0.removeTestWebFile(r2)
        L_0x0261:
            r1.addedForTest = r12
            r1.currentUrl = r15
            r1.currentWebFile = r15
            r1.photoNotSet = r12
            r1.drawBackground = r13
            r1.drawName = r12
            r1.useSeekBarWaweform = r12
            r1.drawInstantView = r12
            r1.drawInstantViewType = r12
            r1.drawForwardedName = r12
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r0.setSideClip(r10)
            r1.gradientShader = r15
            r1.imageBackgroundColor = r12
            r1.imageBackgroundGradientColor = r12
            r0 = 45
            r1.imageBackgroundGradientRotation = r0
            r1.imageBackgroundSideColor = r12
            r1.mediaBackground = r12
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r6 = 1065353216(0x3var_, float:1.0)
            r0.setAlpha(r6)
            if (r17 != 0) goto L_0x0293
            if (r18 == 0) goto L_0x0298
        L_0x0293:
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$PollButton> r0 = r1.pollButtons
            r0.clear()
        L_0x0298:
            r1.availableTimeWidth = r12
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r0 = r0.reactions
            r1.lastReactions = r0
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r0.setForceLoading(r12)
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r0.setNeedsQualityThumb(r12)
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r0.setShouldGenerateQualityThumb(r12)
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r0.setAllowDecodeSingleFrame(r12)
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setRoundRadius(r2)
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r0.setColorFilter(r15)
            if (r17 == 0) goto L_0x02cc
            r1.firstVisibleBlockNum = r12
            r1.lastVisibleBlockNum = r12
            r1.needNewVisiblePart = r13
        L_0x02cc:
            int r0 = r14.type
            r19 = 1092616192(0x41200000, float:10.0)
            r20 = 1073741824(0x40000000, float:2.0)
            if (r0 != 0) goto L_0x19ad
            r1.drawForwardedName = r13
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0319
            boolean r0 = r1.isChat
            if (r0 == 0) goto L_0x02fa
            boolean r0 = r59.isOutOwner()
            if (r0 != 0) goto L_0x02fa
            boolean r0 = r59.needDrawAvatar()
            if (r0 == 0) goto L_0x02fa
            int r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            r5 = 1123287040(0x42var_, float:122.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.drawName = r13
            goto L_0x0361
        L_0x02fa:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x030a
            boolean r0 = r59.isOutOwner()
            if (r0 != 0) goto L_0x030a
            r0 = 1
            goto L_0x030b
        L_0x030a:
            r0 = 0
        L_0x030b:
            r1.drawName = r0
            int r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            r5 = 1117782016(0x42a00000, float:80.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            goto L_0x0361
        L_0x0319:
            boolean r0 = r1.isChat
            if (r0 == 0) goto L_0x033d
            boolean r0 = r59.isOutOwner()
            if (r0 != 0) goto L_0x033d
            boolean r0 = r59.needDrawAvatar()
            if (r0 == 0) goto L_0x033d
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r0.x
            int r0 = r0.y
            int r0 = java.lang.Math.min(r5, r0)
            r5 = 1123287040(0x42var_, float:122.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            r1.drawName = r13
            goto L_0x0361
        L_0x033d:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r0.x
            int r0 = r0.y
            int r0 = java.lang.Math.min(r5, r0)
            r5 = 1117782016(0x42a00000, float:80.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.to_id
            int r5 = r5.channel_id
            if (r5 == 0) goto L_0x035e
            boolean r5 = r59.isOutOwner()
            if (r5 != 0) goto L_0x035e
            r5 = 1
            goto L_0x035f
        L_0x035e:
            r5 = 0
        L_0x035f:
            r1.drawName = r5
        L_0x0361:
            r5 = r0
            r1.availableTimeWidth = r5
            boolean r0 = r59.isRoundVideo()
            if (r0 == 0) goto L_0x0394
            int r0 = r1.availableTimeWidth
            double r7 = (double) r0
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint
            java.lang.String r4 = "00:00"
            float r0 = r0.measureText(r4)
            double r3 = (double) r0
            double r3 = java.lang.Math.ceil(r3)
            boolean r0 = r59.isOutOwner()
            if (r0 == 0) goto L_0x0382
            r0 = 0
            goto L_0x0388
        L_0x0382:
            r0 = 1115684864(0x42800000, float:64.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
        L_0x0388:
            double r10 = (double) r0
            java.lang.Double.isNaN(r10)
            double r3 = r3 + r10
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r3
            int r0 = (int) r7
            r1.availableTimeWidth = r0
        L_0x0394:
            r58.measureTime(r59)
            int r0 = r1.timeWidth
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 + r3
            boolean r3 = r59.isOutOwner()
            if (r3 == 0) goto L_0x03ad
            r3 = 1101266944(0x41a40000, float:20.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 + r3
        L_0x03ad:
            r11 = r0
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r3 == 0) goto L_0x03be
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_game
            if (r0 == 0) goto L_0x03be
            r0 = 1
            goto L_0x03bf
        L_0x03be:
            r0 = 0
        L_0x03bf:
            r1.hasGamePreview = r0
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            r1.hasInvoicePreview = r3
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r3 == 0) goto L_0x03d5
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPage
            if (r0 == 0) goto L_0x03d5
            r0 = 1
            goto L_0x03d6
        L_0x03d5:
            r0 = 0
        L_0x03d6:
            r1.hasLinkPreview = r0
            boolean r0 = r1.hasLinkPreview
            if (r0 == 0) goto L_0x03e8
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            org.telegram.tgnet.TLRPC$Page r0 = r0.cached_page
            if (r0 == 0) goto L_0x03e8
            r0 = 1
            goto L_0x03e9
        L_0x03e8:
            r0 = 0
        L_0x03e9:
            r1.drawInstantView = r0
            boolean r0 = r1.hasLinkPreview
            if (r0 == 0) goto L_0x0405
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            java.lang.String r0 = r0.embed_url
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0405
            boolean r0 = r59.isGif()
            if (r0 != 0) goto L_0x0405
            r0 = 1
            goto L_0x0406
        L_0x0405:
            r0 = 0
        L_0x0406:
            r1.hasEmbed = r0
            boolean r0 = r1.hasLinkPreview
            if (r0 == 0) goto L_0x0415
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            java.lang.String r0 = r0.site_name
            goto L_0x0416
        L_0x0415:
            r0 = r15
        L_0x0416:
            boolean r3 = r1.hasLinkPreview
            if (r3 == 0) goto L_0x0423
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$WebPage r3 = r3.webpage
            java.lang.String r3 = r3.type
            goto L_0x0424
        L_0x0423:
            r3 = r15
        L_0x0424:
            boolean r4 = r1.drawInstantView
            if (r4 != 0) goto L_0x05dd
            java.lang.String r0 = "telegram_channel"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0436
            r1.drawInstantView = r13
            r1.drawInstantViewType = r13
            goto L_0x0690
        L_0x0436:
            java.lang.String r0 = "telegram_megagroup"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0444
            r1.drawInstantView = r13
            r1.drawInstantViewType = r9
            goto L_0x0690
        L_0x0444:
            java.lang.String r0 = "telegram_message"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0453
            r1.drawInstantView = r13
            r4 = 3
            r1.drawInstantViewType = r4
            goto L_0x0690
        L_0x0453:
            java.lang.String r0 = "telegram_theme"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x04be
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_webPageAttributeTheme> r0 = r0.attributes
            int r0 = r0.size()
            r7 = r15
            r4 = 0
        L_0x0469:
            if (r4 >= r0) goto L_0x04b9
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_webPageAttributeTheme> r8 = r8.attributes
            java.lang.Object r8 = r8.get(r4)
            org.telegram.tgnet.TLRPC$TL_webPageAttributeTheme r8 = (org.telegram.tgnet.TLRPC.TL_webPageAttributeTheme) r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r8.documents
            int r15 = r10.size()
            r6 = 0
        L_0x0480:
            if (r6 >= r15) goto L_0x04a0
            java.lang.Object r25 = r10.get(r6)
            r9 = r25
            org.telegram.tgnet.TLRPC$Document r9 = (org.telegram.tgnet.TLRPC.Document) r9
            java.lang.String r12 = r9.mime_type
            java.lang.String r2 = "application/x-tgtheme-android"
            boolean r2 = r2.equals(r12)
            if (r2 == 0) goto L_0x049b
            r1.drawInstantView = r13
            r2 = 7
            r1.drawInstantViewType = r2
            r7 = r9
            goto L_0x04a0
        L_0x049b:
            int r6 = r6 + 1
            r9 = 2
            r12 = 0
            goto L_0x0480
        L_0x04a0:
            boolean r2 = r1.drawInstantView
            if (r2 == 0) goto L_0x04a5
            goto L_0x04b9
        L_0x04a5:
            org.telegram.tgnet.TLRPC$TL_themeSettings r15 = r8.settings
            if (r15 == 0) goto L_0x04b1
            r1.drawInstantView = r13
            r2 = 7
            r1.drawInstantViewType = r2
            r0 = r15
            r15 = r7
            goto L_0x04bb
        L_0x04b1:
            int r4 = r4 + 1
            r6 = 1065353216(0x3var_, float:1.0)
            r9 = 2
            r12 = 0
            r15 = 0
            goto L_0x0469
        L_0x04b9:
            r15 = r7
            r0 = 0
        L_0x04bb:
            r2 = 0
            goto L_0x0693
        L_0x04be:
            java.lang.String r0 = "telegram_background"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0690
            r1.drawInstantView = r13
            r2 = 6
            r1.drawInstantViewType = r2
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner     // Catch:{ Exception -> 0x0690 }
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media     // Catch:{ Exception -> 0x0690 }
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage     // Catch:{ Exception -> 0x0690 }
            java.lang.String r0 = r0.url     // Catch:{ Exception -> 0x0690 }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x0690 }
            java.lang.String r2 = "intensity"
            java.lang.String r2 = r0.getQueryParameter(r2)     // Catch:{ Exception -> 0x0690 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)     // Catch:{ Exception -> 0x0690 }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x0690 }
            java.lang.String r4 = "bg_color"
            java.lang.String r4 = r0.getQueryParameter(r4)     // Catch:{ Exception -> 0x0690 }
            java.lang.String r6 = "rotation"
            java.lang.String r6 = r0.getQueryParameter(r6)     // Catch:{ Exception -> 0x0690 }
            if (r6 == 0) goto L_0x04fd
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ Exception -> 0x0690 }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x0690 }
            r1.imageBackgroundGradientRotation = r6     // Catch:{ Exception -> 0x0690 }
        L_0x04fd:
            boolean r6 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0690 }
            if (r6 == 0) goto L_0x0519
            org.telegram.tgnet.TLRPC$Document r6 = r59.getDocument()     // Catch:{ Exception -> 0x0690 }
            if (r6 == 0) goto L_0x0515
            java.lang.String r7 = "image/png"
            java.lang.String r6 = r6.mime_type     // Catch:{ Exception -> 0x0690 }
            boolean r6 = r7.equals(r6)     // Catch:{ Exception -> 0x0690 }
            if (r6 == 0) goto L_0x0515
            java.lang.String r4 = "ffffff"
        L_0x0515:
            if (r2 != 0) goto L_0x0519
            r2 = 50
        L_0x0519:
            if (r4 == 0) goto L_0x056e
            r6 = 6
            r7 = 0
            java.lang.String r0 = r4.substring(r7, r6)     // Catch:{ Exception -> 0x0690 }
            r6 = 16
            int r0 = java.lang.Integer.parseInt(r0, r6)     // Catch:{ Exception -> 0x0690 }
            r6 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0 = r0 | r6
            r1.imageBackgroundColor = r0     // Catch:{ Exception -> 0x0690 }
            int r0 = r1.imageBackgroundColor     // Catch:{ Exception -> 0x0690 }
            int r6 = r4.length()     // Catch:{ Exception -> 0x0690 }
            r7 = 6
            if (r6 <= r7) goto L_0x054d
            r6 = 7
            java.lang.String r0 = r4.substring(r6)     // Catch:{ Exception -> 0x0690 }
            r4 = 16
            int r0 = java.lang.Integer.parseInt(r0, r4)     // Catch:{ Exception -> 0x0690 }
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0 = r0 | r4
            r1.imageBackgroundGradientColor = r0     // Catch:{ Exception -> 0x0690 }
            int r0 = r1.imageBackgroundColor     // Catch:{ Exception -> 0x0690 }
            int r4 = r1.imageBackgroundGradientColor     // Catch:{ Exception -> 0x0690 }
            int r0 = org.telegram.messenger.AndroidUtilities.getAverageColor(r0, r4)     // Catch:{ Exception -> 0x0690 }
        L_0x054d:
            int r4 = org.telegram.messenger.AndroidUtilities.getPatternSideColor(r0)     // Catch:{ Exception -> 0x0690 }
            r1.imageBackgroundSideColor = r4     // Catch:{ Exception -> 0x0690 }
            org.telegram.messenger.ImageReceiver r4 = r1.photoImage     // Catch:{ Exception -> 0x0690 }
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter     // Catch:{ Exception -> 0x0690 }
            int r0 = org.telegram.messenger.AndroidUtilities.getPatternColor(r0)     // Catch:{ Exception -> 0x0690 }
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.SRC_IN     // Catch:{ Exception -> 0x0690 }
            r6.<init>(r0, r7)     // Catch:{ Exception -> 0x0690 }
            r4.setColorFilter(r6)     // Catch:{ Exception -> 0x0690 }
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage     // Catch:{ Exception -> 0x0690 }
            float r2 = (float) r2     // Catch:{ Exception -> 0x0690 }
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            float r2 = r2 / r4
            r0.setAlpha(r2)     // Catch:{ Exception -> 0x0690 }
            goto L_0x0690
        L_0x056e:
            java.lang.String r0 = r0.getLastPathSegment()     // Catch:{ Exception -> 0x0690 }
            if (r0 == 0) goto L_0x0690
            int r2 = r0.length()     // Catch:{ Exception -> 0x0690 }
            r4 = 6
            if (r2 == r4) goto L_0x058b
            int r2 = r0.length()     // Catch:{ Exception -> 0x0690 }
            r6 = 13
            if (r2 != r6) goto L_0x0690
            char r2 = r0.charAt(r4)     // Catch:{ Exception -> 0x0690 }
            r6 = 45
            if (r2 != r6) goto L_0x0690
        L_0x058b:
            r2 = 0
            java.lang.String r6 = r0.substring(r2, r4)     // Catch:{ Exception -> 0x0690 }
            r2 = 16
            int r2 = java.lang.Integer.parseInt(r6, r2)     // Catch:{ Exception -> 0x0690 }
            r6 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r2 = r2 | r6
            r1.imageBackgroundColor = r2     // Catch:{ Exception -> 0x0690 }
            int r2 = r0.length()     // Catch:{ Exception -> 0x0690 }
            if (r2 <= r4) goto L_0x05b1
            r2 = 7
            java.lang.String r0 = r0.substring(r2)     // Catch:{ Exception -> 0x0690 }
            r2 = 16
            int r0 = java.lang.Integer.parseInt(r0, r2)     // Catch:{ Exception -> 0x0690 }
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0 = r0 | r2
            r1.imageBackgroundGradientColor = r0     // Catch:{ Exception -> 0x0690 }
        L_0x05b1:
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r0 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty     // Catch:{ Exception -> 0x0690 }
            r0.<init>()     // Catch:{ Exception -> 0x0690 }
            r1.currentPhotoObject = r0     // Catch:{ Exception -> 0x0690 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject     // Catch:{ Exception -> 0x0690 }
            java.lang.String r2 = "s"
            r0.type = r2     // Catch:{ Exception -> 0x0690 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject     // Catch:{ Exception -> 0x0690 }
            r2 = 1127481344(0x43340000, float:180.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x0690 }
            r0.w = r2     // Catch:{ Exception -> 0x0690 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject     // Catch:{ Exception -> 0x0690 }
            r2 = 1125515264(0x43160000, float:150.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x0690 }
            r0.h = r2     // Catch:{ Exception -> 0x0690 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject     // Catch:{ Exception -> 0x0690 }
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r2 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable     // Catch:{ Exception -> 0x0690 }
            r2.<init>()     // Catch:{ Exception -> 0x0690 }
            r0.location = r2     // Catch:{ Exception -> 0x0690 }
            goto L_0x0690
        L_0x05dd:
            if (r0 == 0) goto L_0x0690
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r2 = "instagram"
            boolean r2 = r0.equals(r2)
            if (r2 != 0) goto L_0x05fc
            java.lang.String r2 = "twitter"
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L_0x05fc
            java.lang.String r0 = "telegram_album"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0690
        L_0x05fc:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            org.telegram.tgnet.TLRPC$Page r2 = r0.cached_page
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_page
            if (r2 == 0) goto L_0x0690
            org.telegram.tgnet.TLRPC$Photo r2 = r0.photo
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photo
            if (r2 != 0) goto L_0x0616
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            boolean r0 = org.telegram.messenger.MessageObject.isVideoDocument(r0)
            if (r0 == 0) goto L_0x0690
        L_0x0616:
            r2 = 0
            r1.drawInstantView = r2
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            org.telegram.tgnet.TLRPC$Page r0 = r0.cached_page
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r0 = r0.blocks
            r2 = 0
            r4 = 1
        L_0x0625:
            int r6 = r0.size()
            if (r2 >= r6) goto L_0x064d
            java.lang.Object r6 = r0.get(r2)
            org.telegram.tgnet.TLRPC$PageBlock r6 = (org.telegram.tgnet.TLRPC.PageBlock) r6
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow
            if (r7 == 0) goto L_0x063e
            org.telegram.tgnet.TLRPC$TL_pageBlockSlideshow r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow) r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r4 = r6.items
            int r4 = r4.size()
            goto L_0x064a
        L_0x063e:
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCollage
            if (r7 == 0) goto L_0x064a
            org.telegram.tgnet.TLRPC$TL_pageBlockCollage r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockCollage) r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r4 = r6.items
            int r4 = r4.size()
        L_0x064a:
            int r2 = r2 + 1
            goto L_0x0625
        L_0x064d:
            r0 = 2131625830(0x7f0e0766, float:1.887888E38)
            r2 = 2
            java.lang.Object[] r6 = new java.lang.Object[r2]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r13)
            r7 = 0
            r6[r7] = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            r6[r13] = r2
            java.lang.String r2 = "Of"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r6)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_durationPaint
            float r2 = r2.measureText(r0)
            double r6 = (double) r2
            double r6 = java.lang.Math.ceil(r6)
            int r2 = (int) r6
            r1.photosCountWidth = r2
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r30 = org.telegram.ui.ActionBar.Theme.chat_durationPaint
            int r4 = r1.photosCountWidth
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_NORMAL
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 0
            r35 = 0
            r28 = r2
            r29 = r0
            r31 = r4
            r28.<init>(r29, r30, r31, r32, r33, r34, r35)
            r1.photosCountLayout = r2
            r0 = 0
            r2 = 1
            goto L_0x0692
        L_0x0690:
            r0 = 0
            r2 = 0
        L_0x0692:
            r15 = 0
        L_0x0693:
            r1.backgroundWidth = r5
            boolean r4 = r1.hasLinkPreview
            if (r4 != 0) goto L_0x06cf
            boolean r4 = r1.hasGamePreview
            if (r4 != 0) goto L_0x06cf
            boolean r4 = r1.hasInvoicePreview
            if (r4 != 0) goto L_0x06cf
            int r4 = r14.lastLineWidth
            int r6 = r5 - r4
            if (r6 >= r11) goto L_0x06a8
            goto L_0x06cf
        L_0x06a8:
            int r6 = r1.backgroundWidth
            int r4 = r6 - r4
            if (r4 < 0) goto L_0x06bc
            if (r4 > r11) goto L_0x06bc
            int r6 = r6 + r11
            int r6 = r6 - r4
            r4 = 1106771968(0x41var_, float:31.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = r6 + r4
            r1.backgroundWidth = r6
            goto L_0x06f1
        L_0x06bc:
            int r4 = r1.backgroundWidth
            int r6 = r14.lastLineWidth
            int r6 = r6 + r11
            int r4 = java.lang.Math.max(r4, r6)
            r6 = 1106771968(0x41var_, float:31.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 + r6
            r1.backgroundWidth = r4
            goto L_0x06f1
        L_0x06cf:
            int r4 = r1.backgroundWidth
            int r6 = r14.lastLineWidth
            int r4 = java.lang.Math.max(r4, r6)
            r6 = 1106771968(0x41var_, float:31.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 + r6
            r1.backgroundWidth = r4
            int r4 = r1.backgroundWidth
            int r6 = r1.timeWidth
            r7 = 1106771968(0x41var_, float:31.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 + r7
            int r4 = java.lang.Math.max(r4, r6)
            r1.backgroundWidth = r4
        L_0x06f1:
            int r4 = r1.backgroundWidth
            r6 = 1106771968(0x41var_, float:31.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            r1.availableTimeWidth = r4
            boolean r4 = r59.isRoundVideo()
            if (r4 == 0) goto L_0x072c
            int r4 = r1.availableTimeWidth
            double r6 = (double) r4
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint
            java.lang.String r8 = "00:00"
            float r4 = r4.measureText(r8)
            double r8 = (double) r4
            double r8 = java.lang.Math.ceil(r8)
            boolean r4 = r59.isOutOwner()
            if (r4 == 0) goto L_0x071a
            r12 = 0
            goto L_0x0720
        L_0x071a:
            r4 = 1115684864(0x42800000, float:64.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r4)
        L_0x0720:
            double r13 = (double) r12
            java.lang.Double.isNaN(r13)
            double r8 = r8 + r13
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r8
            int r4 = (int) r6
            r1.availableTimeWidth = r4
        L_0x072c:
            r58.setMessageObjectInternal(r59)
            r14 = r59
            int r4 = r14.textWidth
            boolean r6 = r1.hasGamePreview
            if (r6 != 0) goto L_0x073e
            boolean r6 = r1.hasInvoicePreview
            if (r6 == 0) goto L_0x073c
            goto L_0x073e
        L_0x073c:
            r12 = 0
            goto L_0x0742
        L_0x073e:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r19)
        L_0x0742:
            int r4 = r4 + r12
            r1.backgroundWidth = r4
            int r4 = r14.textHeight
            r6 = 1100742656(0x419CLASSNAME, float:19.5)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 + r6
            int r6 = r1.namesOffset
            int r4 = r4 + r6
            r1.totalHeight = r4
            boolean r4 = r1.drawPinnedTop
            if (r4 == 0) goto L_0x0760
            r4 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = r6 - r7
            r1.namesOffset = r6
        L_0x0760:
            int r4 = r1.backgroundWidth
            int r6 = r1.nameWidth
            int r4 = java.lang.Math.max(r4, r6)
            int r6 = r1.forwardedNameWidth
            int r4 = java.lang.Math.max(r4, r6)
            int r6 = r1.replyNameWidth
            int r4 = java.lang.Math.max(r4, r6)
            int r6 = r1.replyTextWidth
            int r4 = java.lang.Math.max(r4, r6)
            boolean r6 = r1.hasLinkPreview
            if (r6 != 0) goto L_0x0796
            boolean r6 = r1.hasGamePreview
            if (r6 != 0) goto L_0x0796
            boolean r6 = r1.hasInvoicePreview
            if (r6 == 0) goto L_0x0787
            goto L_0x0796
        L_0x0787:
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r2 = 0
            r0.setImageBitmap((android.graphics.drawable.Drawable) r2)
            r1.calcBackgroundWidth(r5, r11, r4)
            r60 = 1065353216(0x3var_, float:1.0)
            r13 = 0
            r15 = 1
            goto L_0x2242
        L_0x0796:
            boolean r6 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r6 == 0) goto L_0x07c4
            boolean r6 = r1.isChat
            if (r6 == 0) goto L_0x07b9
            boolean r6 = r59.needDrawAvatar()
            if (r6 == 0) goto L_0x07b9
            org.telegram.messenger.MessageObject r6 = r1.currentMessageObject
            boolean r6 = r6.isOutOwner()
            if (r6 != 0) goto L_0x07b9
            int r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            r7 = 1124335616(0x43040000, float:132.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x07eb
        L_0x07b9:
            int r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            r7 = 1117782016(0x42a00000, float:80.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x07eb
        L_0x07c4:
            boolean r6 = r1.isChat
            if (r6 == 0) goto L_0x07e1
            boolean r6 = r59.needDrawAvatar()
            if (r6 == 0) goto L_0x07e1
            org.telegram.messenger.MessageObject r6 = r1.currentMessageObject
            boolean r6 = r6.isOutOwner()
            if (r6 != 0) goto L_0x07e1
            android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.displaySize
            int r6 = r6.x
            r7 = 1124335616(0x43040000, float:132.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x07eb
        L_0x07e1:
            android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.displaySize
            int r6 = r6.x
            r7 = 1117782016(0x42a00000, float:80.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
        L_0x07eb:
            int r6 = r6 - r7
            boolean r7 = r1.drawShareButton
            if (r7 == 0) goto L_0x07f7
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
        L_0x07f7:
            boolean r7 = r1.hasLinkPreview
            if (r7 == 0) goto L_0x08ae
            org.telegram.tgnet.TLRPC$Message r7 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            org.telegram.tgnet.TLRPC$WebPage r7 = r7.webpage
            org.telegram.tgnet.TLRPC$TL_webPage r7 = (org.telegram.tgnet.TLRPC.TL_webPage) r7
            java.lang.String r8 = r7.site_name
            int r9 = r1.drawInstantViewType
            r10 = 6
            r12 = 7
            if (r9 == r10) goto L_0x0810
            if (r9 == r12) goto L_0x0810
            java.lang.String r9 = r7.title
            goto L_0x0811
        L_0x0810:
            r9 = 0
        L_0x0811:
            int r13 = r1.drawInstantViewType
            if (r13 == r10) goto L_0x081c
            if (r13 == r12) goto L_0x081c
            java.lang.String r13 = r7.author
            r29 = r6
            goto L_0x081f
        L_0x081c:
            r29 = r6
            r13 = 0
        L_0x081f:
            int r6 = r1.drawInstantViewType
            if (r6 == r10) goto L_0x0828
            if (r6 == r12) goto L_0x0828
            java.lang.String r6 = r7.description
            goto L_0x0829
        L_0x0828:
            r6 = 0
        L_0x0829:
            org.telegram.tgnet.TLRPC$Photo r10 = r7.photo
            r30 = r9
            int r9 = r1.drawInstantViewType
            if (r9 != r12) goto L_0x083a
            if (r0 == 0) goto L_0x083d
            org.telegram.messenger.DocumentObject$ThemeDocument r9 = new org.telegram.messenger.DocumentObject$ThemeDocument
            r9.<init>(r0)
            r15 = r9
            goto L_0x083d
        L_0x083a:
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            r15 = r0
        L_0x083d:
            java.lang.String r0 = r7.type
            int r12 = r7.duration
            if (r8 == 0) goto L_0x0861
            if (r10 == 0) goto L_0x0861
            java.lang.String r7 = r8.toLowerCase()
            java.lang.String r9 = "instagram"
            boolean r7 = r7.equals(r9)
            if (r7 == 0) goto L_0x0861
            android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
            int r7 = r7.y
            r9 = 3
            int r7 = r7 / r9
            org.telegram.messenger.MessageObject r9 = r1.currentMessageObject
            int r9 = r9.textWidth
            int r7 = java.lang.Math.max(r7, r9)
            r29 = r7
        L_0x0861:
            java.lang.String r7 = "app"
            boolean r7 = r7.equals(r0)
            if (r7 != 0) goto L_0x087c
            java.lang.String r7 = "profile"
            boolean r7 = r7.equals(r0)
            if (r7 != 0) goto L_0x087c
            java.lang.String r7 = "article"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x087a
            goto L_0x087c
        L_0x087a:
            r7 = 0
            goto L_0x087d
        L_0x087c:
            r7 = 1
        L_0x087d:
            if (r2 != 0) goto L_0x0889
            boolean r9 = r1.drawInstantView
            if (r9 != 0) goto L_0x0889
            if (r15 != 0) goto L_0x0889
            if (r7 == 0) goto L_0x0889
            r9 = 1
            goto L_0x088a
        L_0x0889:
            r9 = 0
        L_0x088a:
            if (r2 != 0) goto L_0x08a0
            boolean r2 = r1.drawInstantView
            if (r2 != 0) goto L_0x08a0
            if (r15 != 0) goto L_0x08a0
            if (r6 == 0) goto L_0x08a0
            if (r0 == 0) goto L_0x08a0
            if (r7 == 0) goto L_0x08a0
            org.telegram.messenger.MessageObject r2 = r1.currentMessageObject
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.photoThumbs
            if (r2 == 0) goto L_0x08a0
            r2 = 1
            goto L_0x08a1
        L_0x08a0:
            r2 = 0
        L_0x08a1:
            r1.isSmallImage = r2
            r2 = r10
            r40 = r12
            r7 = r15
            r10 = r0
            r15 = r6
            r12 = r9
            r6 = r30
            r9 = 0
            goto L_0x08ff
        L_0x08ae:
            r29 = r6
            boolean r0 = r1.hasInvoicePreview
            if (r0 == 0) goto L_0x08d9
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            r2 = r0
            org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r2
            java.lang.String r8 = r0.title
            org.telegram.tgnet.TLRPC$WebDocument r0 = r2.photo
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r2 == 0) goto L_0x08ca
            org.telegram.messenger.WebFile r0 = org.telegram.messenger.WebFile.createWithWebDocument(r0)
            r15 = r0
            r2 = 0
            goto L_0x08cc
        L_0x08ca:
            r2 = 0
            r15 = 0
        L_0x08cc:
            r1.isSmallImage = r2
            java.lang.String r0 = "invoice"
            r10 = r0
            r9 = r15
            r2 = 0
            r6 = 0
            r7 = 0
            r12 = 0
            r13 = 0
            r15 = 0
            goto L_0x08fd
        L_0x08d9:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            java.lang.String r8 = r0.title
            java.lang.CharSequence r2 = r14.messageText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x08ed
            java.lang.String r2 = r0.description
            r15 = r2
            goto L_0x08ee
        L_0x08ed:
            r15 = 0
        L_0x08ee:
            org.telegram.tgnet.TLRPC$Photo r2 = r0.photo
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            r6 = 0
            r1.isSmallImage = r6
            java.lang.String r6 = "game"
            r7 = r0
            r10 = r6
            r6 = 0
            r9 = 0
            r12 = 0
            r13 = 0
        L_0x08fd:
            r40 = 0
        L_0x08ff:
            int r0 = r1.drawInstantViewType
            r30 = r8
            r8 = 6
            if (r0 != r8) goto L_0x0910
            r0 = 2131624608(0x7f0e02a0, float:1.88764E38)
            java.lang.String r3 = "ChatBackground"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r3, r0)
            goto L_0x0924
        L_0x0910:
            java.lang.String r0 = "telegram_theme"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0922
            r0 = 2131624723(0x7f0e0313, float:1.8876634E38)
            java.lang.String r3 = "ColorTheme"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r3, r0)
            goto L_0x0924
        L_0x0922:
            r8 = r30
        L_0x0924:
            boolean r0 = r1.hasInvoicePreview
            if (r0 == 0) goto L_0x092c
            r41 = r9
            r3 = 0
            goto L_0x0933
        L_0x092c:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r3 = r0
            r41 = r9
        L_0x0933:
            int r9 = r29 - r3
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            r29 = r10
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r0.photoThumbs
            if (r10 != 0) goto L_0x0943
            if (r2 == 0) goto L_0x0943
            r10 = 1
            r0.generateThumbs(r10)
        L_0x0943:
            if (r8 == 0) goto L_0x09c1
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint     // Catch:{ Exception -> 0x09b6 }
            float r0 = r0.measureText(r8)     // Catch:{ Exception -> 0x09b6 }
            r10 = 1065353216(0x3var_, float:1.0)
            float r0 = r0 + r10
            r42 = r11
            double r10 = (double) r0
            double r10 = java.lang.Math.ceil(r10)     // Catch:{ Exception -> 0x09b4 }
            int r0 = (int) r10     // Catch:{ Exception -> 0x09b4 }
            android.text.StaticLayout r10 = new android.text.StaticLayout     // Catch:{ Exception -> 0x09b4 }
            android.text.TextPaint r32 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint     // Catch:{ Exception -> 0x09b4 }
            int r33 = java.lang.Math.min(r0, r9)     // Catch:{ Exception -> 0x09b4 }
            android.text.Layout$Alignment r34 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x09b4 }
            r35 = 1065353216(0x3var_, float:1.0)
            r36 = 0
            r37 = 0
            r30 = r10
            r31 = r8
            r30.<init>(r31, r32, r33, r34, r35, r36, r37)     // Catch:{ Exception -> 0x09b4 }
            r1.siteNameLayout = r10     // Catch:{ Exception -> 0x09b4 }
            android.text.StaticLayout r0 = r1.siteNameLayout     // Catch:{ Exception -> 0x09b4 }
            r10 = 0
            float r0 = r0.getLineLeft(r10)     // Catch:{ Exception -> 0x09b4 }
            r10 = 0
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x097d
            r0 = 1
            goto L_0x097e
        L_0x097d:
            r0 = 0
        L_0x097e:
            r1.siteNameRtl = r0     // Catch:{ Exception -> 0x09b2 }
            android.text.StaticLayout r0 = r1.siteNameLayout     // Catch:{ Exception -> 0x09b2 }
            android.text.StaticLayout r11 = r1.siteNameLayout     // Catch:{ Exception -> 0x09b2 }
            int r11 = r11.getLineCount()     // Catch:{ Exception -> 0x09b2 }
            r23 = 1
            int r11 = r11 + -1
            int r0 = r0.getLineBottom(r11)     // Catch:{ Exception -> 0x09b2 }
            int r11 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x09b2 }
            int r11 = r11 + r0
            r1.linkPreviewHeight = r11     // Catch:{ Exception -> 0x09b2 }
            int r11 = r1.totalHeight     // Catch:{ Exception -> 0x09b2 }
            int r11 = r11 + r0
            r1.totalHeight = r11     // Catch:{ Exception -> 0x09b2 }
            r11 = 0
            int r23 = r0 + 0
            android.text.StaticLayout r0 = r1.siteNameLayout     // Catch:{ Exception -> 0x09b0 }
            int r0 = r0.getWidth()     // Catch:{ Exception -> 0x09b0 }
            r1.siteNameWidth = r0     // Catch:{ Exception -> 0x09b0 }
            int r0 = r0 + r3
            int r4 = java.lang.Math.max(r4, r0)     // Catch:{ Exception -> 0x09b0 }
            int r0 = java.lang.Math.max(r11, r0)     // Catch:{ Exception -> 0x09b0 }
            r11 = r0
            goto L_0x09c7
        L_0x09b0:
            r0 = move-exception
            goto L_0x09bc
        L_0x09b2:
            r0 = move-exception
            goto L_0x09ba
        L_0x09b4:
            r0 = move-exception
            goto L_0x09b9
        L_0x09b6:
            r0 = move-exception
            r42 = r11
        L_0x09b9:
            r10 = 0
        L_0x09ba:
            r23 = 0
        L_0x09bc:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r11 = 0
            goto L_0x09c7
        L_0x09c1:
            r42 = r11
            r10 = 0
            r11 = 0
            r23 = 0
        L_0x09c7:
            if (r6 == 0) goto L_0x0b23
            r0 = 2147483647(0x7fffffff, float:NaN)
            r1.titleX = r0     // Catch:{ Exception -> 0x0afb }
            int r0 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x0afb }
            if (r0 == 0) goto L_0x09f8
            int r0 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x09e7 }
            int r30 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x09e7 }
            int r0 = r0 + r30
            r1.linkPreviewHeight = r0     // Catch:{ Exception -> 0x09e7 }
            int r0 = r1.totalHeight     // Catch:{ Exception -> 0x09e7 }
            int r30 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x09e7 }
            int r0 = r0 + r30
            r1.totalHeight = r0     // Catch:{ Exception -> 0x09e7 }
            goto L_0x09f8
        L_0x09e7:
            r0 = move-exception
            r43 = r2
            r39 = r5
            r44 = r7
            r45 = r8
            r10 = r11
            r30 = 3
            r31 = 0
            r11 = r4
            goto L_0x0b0d
        L_0x09f8:
            boolean r0 = r1.isSmallImage     // Catch:{ Exception -> 0x0afb }
            if (r0 == 0) goto L_0x0a25
            if (r15 != 0) goto L_0x09ff
            goto L_0x0a25
        L_0x09ff:
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint     // Catch:{ Exception -> 0x09e7 }
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x09e7 }
            int r33 = r9 - r0
            r35 = 4
            r34 = 3
            r30 = r6
            r32 = r9
            android.text.StaticLayout r0 = generateStaticLayout(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x09e7 }
            r1.titleLayout = r0     // Catch:{ Exception -> 0x09e7 }
            android.text.StaticLayout r0 = r1.titleLayout     // Catch:{ Exception -> 0x09e7 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x09e7 }
            r22 = 3
            int r0 = 3 - r0
            r30 = r0
            r0 = 3
            goto L_0x0a49
        L_0x0a25:
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint     // Catch:{ Exception -> 0x0afb }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0afb }
            r34 = 1065353216(0x3var_, float:1.0)
            r24 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)     // Catch:{ Exception -> 0x0afb }
            float r0 = (float) r0     // Catch:{ Exception -> 0x0afb }
            r36 = 0
            android.text.TextUtils$TruncateAt r37 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x0afb }
            r39 = 4
            r30 = r6
            r32 = r9
            r35 = r0
            r38 = r9
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r30, r31, r32, r33, r34, r35, r36, r37, r38, r39)     // Catch:{ Exception -> 0x0afb }
            r1.titleLayout = r0     // Catch:{ Exception -> 0x0afb }
            r0 = 0
            r30 = 3
        L_0x0a49:
            android.text.StaticLayout r10 = r1.titleLayout     // Catch:{ Exception -> 0x0aec }
            r31 = r4
            android.text.StaticLayout r4 = r1.titleLayout     // Catch:{ Exception -> 0x0ae8 }
            int r4 = r4.getLineCount()     // Catch:{ Exception -> 0x0ae8 }
            r28 = 1
            int r4 = r4 + -1
            int r4 = r10.getLineBottom(r4)     // Catch:{ Exception -> 0x0ae8 }
            int r10 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x0ae8 }
            int r10 = r10 + r4
            r1.linkPreviewHeight = r10     // Catch:{ Exception -> 0x0ae8 }
            int r10 = r1.totalHeight     // Catch:{ Exception -> 0x0ae8 }
            int r10 = r10 + r4
            r1.totalHeight = r10     // Catch:{ Exception -> 0x0ae8 }
            r39 = r5
            r10 = r11
            r11 = r31
            r4 = 0
            r31 = 0
        L_0x0a6d:
            android.text.StaticLayout r5 = r1.titleLayout     // Catch:{ Exception -> 0x0ae0 }
            int r5 = r5.getLineCount()     // Catch:{ Exception -> 0x0ae0 }
            if (r4 >= r5) goto L_0x0ad9
            android.text.StaticLayout r5 = r1.titleLayout     // Catch:{ Exception -> 0x0ae0 }
            float r5 = r5.getLineLeft(r4)     // Catch:{ Exception -> 0x0ae0 }
            int r5 = (int) r5
            r43 = r2
            if (r5 == 0) goto L_0x0a82
            r31 = 1
        L_0x0a82:
            int r2 = r1.titleX     // Catch:{ Exception -> 0x0ad7 }
            r44 = r7
            r7 = 2147483647(0x7fffffff, float:NaN)
            if (r2 != r7) goto L_0x0a8f
            int r2 = -r5
            r1.titleX = r2     // Catch:{ Exception -> 0x0ad5 }
            goto L_0x0a98
        L_0x0a8f:
            int r2 = r1.titleX     // Catch:{ Exception -> 0x0ad5 }
            int r7 = -r5
            int r2 = java.lang.Math.max(r2, r7)     // Catch:{ Exception -> 0x0ad5 }
            r1.titleX = r2     // Catch:{ Exception -> 0x0ad5 }
        L_0x0a98:
            if (r5 == 0) goto L_0x0aa4
            android.text.StaticLayout r2 = r1.titleLayout     // Catch:{ Exception -> 0x0ad5 }
            int r2 = r2.getWidth()     // Catch:{ Exception -> 0x0ad5 }
            int r2 = r2 - r5
            r45 = r8
            goto L_0x0ab2
        L_0x0aa4:
            android.text.StaticLayout r2 = r1.titleLayout     // Catch:{ Exception -> 0x0ad5 }
            float r2 = r2.getLineWidth(r4)     // Catch:{ Exception -> 0x0ad5 }
            r45 = r8
            double r7 = (double) r2
            double r7 = java.lang.Math.ceil(r7)     // Catch:{ Exception -> 0x0ad3 }
            int r2 = (int) r7     // Catch:{ Exception -> 0x0ad3 }
        L_0x0ab2:
            if (r4 < r0) goto L_0x0aba
            if (r5 == 0) goto L_0x0ac1
            boolean r5 = r1.isSmallImage     // Catch:{ Exception -> 0x0ad3 }
            if (r5 == 0) goto L_0x0ac1
        L_0x0aba:
            r5 = 1112539136(0x42500000, float:52.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x0ad3 }
            int r2 = r2 + r5
        L_0x0ac1:
            int r2 = r2 + r3
            int r11 = java.lang.Math.max(r11, r2)     // Catch:{ Exception -> 0x0ad3 }
            int r10 = java.lang.Math.max(r10, r2)     // Catch:{ Exception -> 0x0ad3 }
            int r4 = r4 + 1
            r2 = r43
            r7 = r44
            r8 = r45
            goto L_0x0a6d
        L_0x0ad3:
            r0 = move-exception
            goto L_0x0b0d
        L_0x0ad5:
            r0 = move-exception
            goto L_0x0ae5
        L_0x0ad7:
            r0 = move-exception
            goto L_0x0ae3
        L_0x0ad9:
            r43 = r2
            r44 = r7
            r45 = r8
            goto L_0x0b10
        L_0x0ae0:
            r0 = move-exception
            r43 = r2
        L_0x0ae3:
            r44 = r7
        L_0x0ae5:
            r45 = r8
            goto L_0x0b0d
        L_0x0ae8:
            r0 = move-exception
            r43 = r2
            goto L_0x0af1
        L_0x0aec:
            r0 = move-exception
            r43 = r2
            r31 = r4
        L_0x0af1:
            r39 = r5
            r44 = r7
            r45 = r8
            r10 = r11
            r11 = r31
            goto L_0x0b0b
        L_0x0afb:
            r0 = move-exception
            r43 = r2
            r31 = r4
            r39 = r5
            r44 = r7
            r45 = r8
            r10 = r11
            r11 = r31
            r30 = 3
        L_0x0b0b:
            r31 = 0
        L_0x0b0d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0b10:
            r4 = r11
            r11 = r30
            if (r31 == 0) goto L_0x0b20
            boolean r0 = r1.isSmallImage
            if (r0 == 0) goto L_0x0b20
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r9 = r9 - r0
        L_0x0b20:
            r2 = r31
            goto L_0x0b30
        L_0x0b23:
            r43 = r2
            r31 = r4
            r39 = r5
            r44 = r7
            r45 = r8
            r10 = r11
            r2 = 0
            r11 = 3
        L_0x0b30:
            if (r13 == 0) goto L_0x0bd8
            if (r6 != 0) goto L_0x0bd8
            int r0 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x0bd2 }
            if (r0 == 0) goto L_0x0b4a
            int r0 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x0bd2 }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x0bd2 }
            int r0 = r0 + r5
            r1.linkPreviewHeight = r0     // Catch:{ Exception -> 0x0bd2 }
            int r0 = r1.totalHeight     // Catch:{ Exception -> 0x0bd2 }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x0bd2 }
            int r0 = r0 + r5
            r1.totalHeight = r0     // Catch:{ Exception -> 0x0bd2 }
        L_0x0b4a:
            r5 = 3
            if (r11 != r5) goto L_0x0b6b
            boolean r0 = r1.isSmallImage     // Catch:{ Exception -> 0x0bd2 }
            if (r0 == 0) goto L_0x0b53
            if (r15 != 0) goto L_0x0b6b
        L_0x0b53:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0bd2 }
            android.text.TextPaint r32 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint     // Catch:{ Exception -> 0x0bd2 }
            android.text.Layout$Alignment r34 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0bd2 }
            r35 = 1065353216(0x3var_, float:1.0)
            r36 = 0
            r37 = 0
            r30 = r0
            r31 = r13
            r33 = r9
            r30.<init>(r31, r32, r33, r34, r35, r36, r37)     // Catch:{ Exception -> 0x0bd2 }
            r1.authorLayout = r0     // Catch:{ Exception -> 0x0bd2 }
            goto L_0x0b8a
        L_0x0b6b:
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint     // Catch:{ Exception -> 0x0bd2 }
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x0bd2 }
            int r33 = r9 - r0
            r35 = 1
            r30 = r13
            r32 = r9
            r34 = r11
            android.text.StaticLayout r0 = generateStaticLayout(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0bd2 }
            r1.authorLayout = r0     // Catch:{ Exception -> 0x0bd2 }
            android.text.StaticLayout r0 = r1.authorLayout     // Catch:{ Exception -> 0x0bd2 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x0bd2 }
            int r11 = r11 - r0
        L_0x0b8a:
            android.text.StaticLayout r0 = r1.authorLayout     // Catch:{ Exception -> 0x0bd2 }
            android.text.StaticLayout r5 = r1.authorLayout     // Catch:{ Exception -> 0x0bd2 }
            int r5 = r5.getLineCount()     // Catch:{ Exception -> 0x0bd2 }
            r6 = 1
            int r5 = r5 - r6
            int r0 = r0.getLineBottom(r5)     // Catch:{ Exception -> 0x0bd2 }
            int r5 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x0bd2 }
            int r5 = r5 + r0
            r1.linkPreviewHeight = r5     // Catch:{ Exception -> 0x0bd2 }
            int r5 = r1.totalHeight     // Catch:{ Exception -> 0x0bd2 }
            int r5 = r5 + r0
            r1.totalHeight = r5     // Catch:{ Exception -> 0x0bd2 }
            android.text.StaticLayout r0 = r1.authorLayout     // Catch:{ Exception -> 0x0bd2 }
            r5 = 0
            float r0 = r0.getLineLeft(r5)     // Catch:{ Exception -> 0x0bd2 }
            int r0 = (int) r0     // Catch:{ Exception -> 0x0bd2 }
            int r5 = -r0
            r1.authorX = r5     // Catch:{ Exception -> 0x0bd2 }
            if (r0 == 0) goto L_0x0bb8
            android.text.StaticLayout r5 = r1.authorLayout     // Catch:{ Exception -> 0x0bd2 }
            int r5 = r5.getWidth()     // Catch:{ Exception -> 0x0bd2 }
            int r5 = r5 - r0
            r6 = 1
            goto L_0x0bc6
        L_0x0bb8:
            android.text.StaticLayout r0 = r1.authorLayout     // Catch:{ Exception -> 0x0bd2 }
            r5 = 0
            float r0 = r0.getLineWidth(r5)     // Catch:{ Exception -> 0x0bd2 }
            double r5 = (double) r0     // Catch:{ Exception -> 0x0bd2 }
            double r5 = java.lang.Math.ceil(r5)     // Catch:{ Exception -> 0x0bd2 }
            int r5 = (int) r5
            r6 = 0
        L_0x0bc6:
            int r5 = r5 + r3
            int r4 = java.lang.Math.max(r4, r5)     // Catch:{ Exception -> 0x0bd0 }
            int r10 = java.lang.Math.max(r10, r5)     // Catch:{ Exception -> 0x0bd0 }
            goto L_0x0bd9
        L_0x0bd0:
            r0 = move-exception
            goto L_0x0bd4
        L_0x0bd2:
            r0 = move-exception
            r6 = 0
        L_0x0bd4:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0bd9
        L_0x0bd8:
            r6 = 0
        L_0x0bd9:
            if (r15 == 0) goto L_0x0d26
            r5 = 0
            r1.descriptionX = r5     // Catch:{ Exception -> 0x0d20 }
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject     // Catch:{ Exception -> 0x0d20 }
            r0.generateLinkDescription()     // Catch:{ Exception -> 0x0d20 }
            int r0 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x0d20 }
            if (r0 == 0) goto L_0x0bf9
            int r0 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x0d20 }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x0d20 }
            int r0 = r0 + r5
            r1.linkPreviewHeight = r0     // Catch:{ Exception -> 0x0d20 }
            int r0 = r1.totalHeight     // Catch:{ Exception -> 0x0d20 }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x0d20 }
            int r0 = r0 + r5
            r1.totalHeight = r0     // Catch:{ Exception -> 0x0d20 }
        L_0x0bf9:
            if (r45 == 0) goto L_0x0c0a
            java.lang.String r0 = r45.toLowerCase()     // Catch:{ Exception -> 0x0d20 }
            java.lang.String r5 = "twitter"
            boolean r0 = r0.equals(r5)     // Catch:{ Exception -> 0x0d20 }
            if (r0 == 0) goto L_0x0c0a
            r0 = 1
            goto L_0x0c0b
        L_0x0c0a:
            r0 = 0
        L_0x0c0b:
            r5 = 3
            if (r11 != r5) goto L_0x0c3e
            boolean r5 = r1.isSmallImage     // Catch:{ Exception -> 0x0d20 }
            if (r5 != 0) goto L_0x0c3e
            java.lang.CharSequence r5 = r14.linkDescription     // Catch:{ Exception -> 0x0d20 }
            android.text.TextPaint r47 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint     // Catch:{ Exception -> 0x0d20 }
            android.text.Layout$Alignment r49 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0d20 }
            r50 = 1065353216(0x3var_, float:1.0)
            r7 = 1065353216(0x3var_, float:1.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x0d20 }
            float r7 = (float) r8     // Catch:{ Exception -> 0x0d20 }
            r52 = 0
            android.text.TextUtils$TruncateAt r53 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x0d20 }
            if (r0 == 0) goto L_0x0c2c
            r0 = 100
            r55 = 100
            goto L_0x0c2e
        L_0x0c2c:
            r55 = 6
        L_0x0c2e:
            r46 = r5
            r48 = r9
            r51 = r7
            r54 = r9
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r46, r47, r48, r49, r50, r51, r52, r53, r54, r55)     // Catch:{ Exception -> 0x0d20 }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x0d20 }
            r11 = 0
            goto L_0x0c5f
        L_0x0c3e:
            java.lang.CharSequence r5 = r14.linkDescription     // Catch:{ Exception -> 0x0d20 }
            android.text.TextPaint r33 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint     // Catch:{ Exception -> 0x0d20 }
            r7 = 1112539136(0x42500000, float:52.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x0d20 }
            int r35 = r9 - r7
            if (r0 == 0) goto L_0x0CLASSNAME
            r0 = 100
            r37 = 100
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r37 = 6
        L_0x0CLASSNAME:
            r32 = r5
            r34 = r9
            r36 = r11
            android.text.StaticLayout r0 = generateStaticLayout(r32, r33, r34, r35, r36, r37)     // Catch:{ Exception -> 0x0d20 }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x0d20 }
        L_0x0c5f:
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x0d20 }
            android.text.StaticLayout r5 = r1.descriptionLayout     // Catch:{ Exception -> 0x0d20 }
            int r5 = r5.getLineCount()     // Catch:{ Exception -> 0x0d20 }
            r7 = 1
            int r5 = r5 - r7
            int r0 = r0.getLineBottom(r5)     // Catch:{ Exception -> 0x0d20 }
            int r5 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x0d20 }
            int r5 = r5 + r0
            r1.linkPreviewHeight = r5     // Catch:{ Exception -> 0x0d20 }
            int r5 = r1.totalHeight     // Catch:{ Exception -> 0x0d20 }
            int r5 = r5 + r0
            r1.totalHeight = r5     // Catch:{ Exception -> 0x0d20 }
            r0 = 0
            r13 = 0
        L_0x0CLASSNAME:
            android.text.StaticLayout r5 = r1.descriptionLayout     // Catch:{ Exception -> 0x0d20 }
            int r5 = r5.getLineCount()     // Catch:{ Exception -> 0x0d20 }
            if (r0 >= r5) goto L_0x0ca4
            android.text.StaticLayout r5 = r1.descriptionLayout     // Catch:{ Exception -> 0x0d20 }
            float r5 = r5.getLineLeft(r0)     // Catch:{ Exception -> 0x0d20 }
            double r7 = (double) r5     // Catch:{ Exception -> 0x0d20 }
            double r7 = java.lang.Math.ceil(r7)     // Catch:{ Exception -> 0x0d20 }
            int r5 = (int) r7     // Catch:{ Exception -> 0x0d20 }
            if (r5 == 0) goto L_0x0ca1
            int r7 = r1.descriptionX     // Catch:{ Exception -> 0x0d20 }
            if (r7 != 0) goto L_0x0CLASSNAME
            int r5 = -r5
            r1.descriptionX = r5     // Catch:{ Exception -> 0x0d20 }
            goto L_0x0ca0
        L_0x0CLASSNAME:
            int r7 = r1.descriptionX     // Catch:{ Exception -> 0x0d20 }
            int r5 = -r5
            int r5 = java.lang.Math.max(r7, r5)     // Catch:{ Exception -> 0x0d20 }
            r1.descriptionX = r5     // Catch:{ Exception -> 0x0d20 }
        L_0x0ca0:
            r13 = 1
        L_0x0ca1:
            int r0 = r0 + 1
            goto L_0x0CLASSNAME
        L_0x0ca4:
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x0d20 }
            int r0 = r0.getWidth()     // Catch:{ Exception -> 0x0d20 }
            r5 = r4
            r4 = 0
        L_0x0cac:
            android.text.StaticLayout r7 = r1.descriptionLayout     // Catch:{ Exception -> 0x0d1c }
            int r7 = r7.getLineCount()     // Catch:{ Exception -> 0x0d1c }
            if (r4 >= r7) goto L_0x0d19
            android.text.StaticLayout r7 = r1.descriptionLayout     // Catch:{ Exception -> 0x0d1c }
            float r7 = r7.getLineLeft(r4)     // Catch:{ Exception -> 0x0d1c }
            double r7 = (double) r7     // Catch:{ Exception -> 0x0d1c }
            double r7 = java.lang.Math.ceil(r7)     // Catch:{ Exception -> 0x0d1c }
            int r7 = (int) r7
            if (r7 != 0) goto L_0x0ccd
            int r8 = r1.descriptionX     // Catch:{ Exception -> 0x0cca }
            if (r8 == 0) goto L_0x0ccd
            r8 = 0
            r1.descriptionX = r8     // Catch:{ Exception -> 0x0cca }
            goto L_0x0ccd
        L_0x0cca:
            r0 = move-exception
            r4 = r5
            goto L_0x0d21
        L_0x0ccd:
            if (r7 == 0) goto L_0x0cd3
            int r8 = r0 - r7
        L_0x0cd1:
            r15 = r9
            goto L_0x0ce8
        L_0x0cd3:
            if (r13 == 0) goto L_0x0cd7
            r8 = r0
            goto L_0x0cd1
        L_0x0cd7:
            android.text.StaticLayout r8 = r1.descriptionLayout     // Catch:{ Exception -> 0x0d1c }
            float r8 = r8.getLineWidth(r4)     // Catch:{ Exception -> 0x0d1c }
            r15 = r9
            double r8 = (double) r8
            double r8 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x0d17 }
            int r8 = (int) r8     // Catch:{ Exception -> 0x0d17 }
            int r8 = java.lang.Math.min(r8, r0)     // Catch:{ Exception -> 0x0d17 }
        L_0x0ce8:
            if (r4 < r11) goto L_0x0cf2
            if (r11 == 0) goto L_0x0cf9
            if (r7 == 0) goto L_0x0cf9
            boolean r7 = r1.isSmallImage     // Catch:{ Exception -> 0x0d17 }
            if (r7 == 0) goto L_0x0cf9
        L_0x0cf2:
            r7 = 1112539136(0x42500000, float:52.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x0d17 }
            int r8 = r8 + r7
        L_0x0cf9:
            int r8 = r8 + r3
            if (r10 >= r8) goto L_0x0d0f
            if (r2 == 0) goto L_0x0d05
            int r7 = r1.titleX     // Catch:{ Exception -> 0x0d17 }
            int r9 = r8 - r10
            int r7 = r7 + r9
            r1.titleX = r7     // Catch:{ Exception -> 0x0d17 }
        L_0x0d05:
            if (r6 == 0) goto L_0x0d0e
            int r7 = r1.authorX     // Catch:{ Exception -> 0x0d17 }
            int r9 = r8 - r10
            int r7 = r7 + r9
            r1.authorX = r7     // Catch:{ Exception -> 0x0d17 }
        L_0x0d0e:
            r10 = r8
        L_0x0d0f:
            int r5 = java.lang.Math.max(r5, r8)     // Catch:{ Exception -> 0x0d17 }
            int r4 = r4 + 1
            r9 = r15
            goto L_0x0cac
        L_0x0d17:
            r0 = move-exception
            goto L_0x0d1e
        L_0x0d19:
            r15 = r9
            r4 = r5
            goto L_0x0d27
        L_0x0d1c:
            r0 = move-exception
            r15 = r9
        L_0x0d1e:
            r4 = r5
            goto L_0x0d22
        L_0x0d20:
            r0 = move-exception
        L_0x0d21:
            r15 = r9
        L_0x0d22:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0d27
        L_0x0d26:
            r15 = r9
        L_0x0d27:
            if (r12 == 0) goto L_0x0d3a
            android.text.StaticLayout r0 = r1.descriptionLayout
            if (r0 == 0) goto L_0x0d36
            if (r0 == 0) goto L_0x0d3a
            int r0 = r0.getLineCount()
            r2 = 1
            if (r0 != r2) goto L_0x0d3a
        L_0x0d36:
            r2 = 0
            r1.isSmallImage = r2
            r12 = 0
        L_0x0d3a:
            if (r12 == 0) goto L_0x0d43
            r0 = 1111490560(0x42400000, float:48.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r15 = r9
        L_0x0d43:
            if (r44 == 0) goto L_0x123e
            boolean r0 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r44)
            if (r0 == 0) goto L_0x0d6c
            r2 = r44
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r2.thumbs
            r5 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5)
            r1.currentPhotoObject = r0
            r1.photoParentObject = r2
            r1.documentAttach = r2
            r5 = 7
            r1.documentAttachType = r5
            r13 = r29
            r5 = r39
            r7 = r41
            r11 = r42
            r10 = r43
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x12a8
        L_0x0d6c:
            r2 = r44
            boolean r0 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r2)
            if (r0 == 0) goto L_0x0df9
            boolean r0 = r59.isGame()
            if (r0 != 0) goto L_0x0d83
            boolean r0 = org.telegram.messenger.SharedConfig.autoplayGifs
            if (r0 != 0) goto L_0x0d83
            r6 = 1065353216(0x3var_, float:1.0)
            r14.gifState = r6
            goto L_0x0d85
        L_0x0d83:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x0d85:
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            float r5 = r14.gifState
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 == 0) goto L_0x0d8f
            r5 = 1
            goto L_0x0d90
        L_0x0d8f:
            r5 = 0
        L_0x0d90:
            r0.setAllowStartAnimation(r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r2.thumbs
            r5 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5)
            r1.currentPhotoObject = r0
            r1.photoParentObject = r2
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            if (r0 == 0) goto L_0x0de8
            int r5 = r0.w
            if (r5 == 0) goto L_0x0dab
            int r0 = r0.h
            if (r0 != 0) goto L_0x0de8
        L_0x0dab:
            r0 = 0
        L_0x0dac:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r2.attributes
            int r5 = r5.size()
            if (r0 >= r5) goto L_0x0dd2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r2.attributes
            java.lang.Object r5 = r5.get(r0)
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r5
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize
            if (r7 != 0) goto L_0x0dc8
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo
            if (r7 == 0) goto L_0x0dc5
            goto L_0x0dc8
        L_0x0dc5:
            int r0 = r0 + 1
            goto L_0x0dac
        L_0x0dc8:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r7 = r5.w
            r0.w = r7
            int r5 = r5.h
            r0.h = r5
        L_0x0dd2:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r5 = r0.w
            if (r5 == 0) goto L_0x0ddc
            int r0 = r0.h
            if (r0 != 0) goto L_0x0de8
        L_0x0ddc:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            r5 = 1125515264(0x43160000, float:150.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.h = r5
            r0.w = r5
        L_0x0de8:
            r1.documentAttach = r2
            r5 = 2
            r1.documentAttachType = r5
            r13 = r29
            r5 = r39
            r7 = r41
            r11 = r42
            r10 = r43
            goto L_0x12a8
        L_0x0df9:
            r6 = 1065353216(0x3var_, float:1.0)
            boolean r0 = org.telegram.messenger.MessageObject.isVideoDocument(r2)
            if (r0 == 0) goto L_0x0ec7
            r10 = r43
            if (r43 == 0) goto L_0x0e1e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r10.sizes
            int r5 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            r7 = 1
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5, r7)
            r1.currentPhotoObject = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r10.sizes
            r5 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5)
            r1.currentPhotoObjectThumb = r0
            r1.photoParentObject = r10
        L_0x0e1e:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            if (r0 != 0) goto L_0x0e38
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r2.thumbs
            r5 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5)
            r1.currentPhotoObject = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r2.thumbs
            r5 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5)
            r1.currentPhotoObjectThumb = r0
            r1.photoParentObject = r2
        L_0x0e38:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObjectThumb
            if (r0 != r5) goto L_0x0e41
            r5 = 0
            r1.currentPhotoObjectThumb = r5
        L_0x0e41:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            if (r0 != 0) goto L_0x0e59
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            r1.currentPhotoObject = r0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            java.lang.String r5 = "s"
            r0.type = r5
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r5 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r5.<init>()
            r0.location = r5
        L_0x0e59:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            if (r0 == 0) goto L_0x0ec1
            int r5 = r0.w
            if (r5 == 0) goto L_0x0e69
            int r5 = r0.h
            if (r5 == 0) goto L_0x0e69
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r0 == 0) goto L_0x0ec1
        L_0x0e69:
            r0 = 0
        L_0x0e6a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r2.attributes
            int r5 = r5.size()
            if (r0 >= r5) goto L_0x0eab
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r2.attributes
            java.lang.Object r5 = r5.get(r0)
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r5
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo
            if (r7 == 0) goto L_0x0ea8
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r7 == 0) goto L_0x0e9f
            int r0 = r5.w
            int r0 = java.lang.Math.max(r0, r0)
            float r0 = (float) r0
            r7 = 1112014848(0x42480000, float:50.0)
            float r0 = r0 / r7
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObject
            int r8 = r5.w
            float r8 = (float) r8
            float r8 = r8 / r0
            int r8 = (int) r8
            r7.w = r8
            int r5 = r5.h
            float r5 = (float) r5
            float r5 = r5 / r0
            int r0 = (int) r5
            r7.h = r0
            goto L_0x0eab
        L_0x0e9f:
            int r7 = r5.w
            r0.w = r7
            int r5 = r5.h
            r0.h = r5
            goto L_0x0eab
        L_0x0ea8:
            int r0 = r0 + 1
            goto L_0x0e6a
        L_0x0eab:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r5 = r0.w
            if (r5 == 0) goto L_0x0eb5
            int r0 = r0.h
            if (r0 != 0) goto L_0x0ec1
        L_0x0eb5:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            r5 = 1125515264(0x43160000, float:150.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.h = r5
            r0.w = r5
        L_0x0ec1:
            r5 = 0
            r1.createDocumentLayout(r5, r14)
            goto L_0x0var_
        L_0x0ec7:
            r10 = r43
            boolean r0 = org.telegram.messenger.MessageObject.isStickerDocument(r2)
            if (r0 != 0) goto L_0x11e0
            r5 = 1
            boolean r0 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r2, r5)
            if (r0 == 0) goto L_0x0ed8
            goto L_0x11e0
        L_0x0ed8:
            int r0 = r1.drawInstantViewType
            r5 = 6
            if (r0 != r5) goto L_0x0f6e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r2.thumbs
            r5 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5)
            r1.currentPhotoObject = r0
            r1.photoParentObject = r2
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            if (r0 == 0) goto L_0x0f2e
            int r5 = r0.w
            if (r5 == 0) goto L_0x0ef5
            int r0 = r0.h
            if (r0 != 0) goto L_0x0f2e
        L_0x0ef5:
            r0 = 0
        L_0x0ef6:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r2.attributes
            int r5 = r5.size()
            if (r0 >= r5) goto L_0x0var_
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r2.attributes
            java.lang.Object r5 = r5.get(r0)
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r5
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize
            if (r7 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r7 = r5.w
            r0.w = r7
            int r5 = r5.h
            r0.h = r5
            goto L_0x0var_
        L_0x0var_:
            int r0 = r0 + 1
            goto L_0x0ef6
        L_0x0var_:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r5 = r0.w
            if (r5 == 0) goto L_0x0var_
            int r0 = r0.h
            if (r0 != 0) goto L_0x0f2e
        L_0x0var_:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            r5 = 1125515264(0x43160000, float:150.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.h = r5
            r0.w = r5
        L_0x0f2e:
            r1.documentAttach = r2
            r5 = 8
            r1.documentAttachType = r5
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            int r0 = r0.size
            long r7 = (long) r0
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.chat_durationPaint
            float r5 = r5.measureText(r0)
            double r7 = (double) r5
            double r7 = java.lang.Math.ceil(r7)
            int r5 = (int) r7
            r1.durationWidth = r5
            android.text.StaticLayout r5 = new android.text.StaticLayout
            android.text.TextPaint r32 = org.telegram.ui.ActionBar.Theme.chat_durationPaint
            int r7 = r1.durationWidth
            android.text.Layout$Alignment r34 = android.text.Layout.Alignment.ALIGN_NORMAL
            r35 = 1065353216(0x3var_, float:1.0)
            r36 = 0
            r37 = 0
            r30 = r5
            r31 = r0
            r33 = r7
            r30.<init>(r31, r32, r33, r34, r35, r36, r37)
            r1.videoInfoLayout = r5
        L_0x0var_:
            r13 = r29
            r5 = r39
            r7 = r41
            r11 = r42
            goto L_0x12a8
        L_0x0f6e:
            r5 = 7
            if (r0 != r5) goto L_0x0fd3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r2.thumbs
            r5 = 700(0x2bc, float:9.81E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5)
            r1.currentPhotoObject = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r2.thumbs
            r5 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5)
            r1.currentPhotoObjectThumb = r0
            r1.photoParentObject = r2
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            if (r0 == 0) goto L_0x0fcc
            int r5 = r0.w
            if (r5 == 0) goto L_0x0var_
            int r0 = r0.h
            if (r0 != 0) goto L_0x0fcc
        L_0x0var_:
            r0 = 0
        L_0x0var_:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r2.attributes
            int r5 = r5.size()
            if (r0 >= r5) goto L_0x0fb6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r2.attributes
            java.lang.Object r5 = r5.get(r0)
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r5
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize
            if (r7 == 0) goto L_0x0fb3
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r7 = r5.w
            r0.w = r7
            int r5 = r5.h
            r0.h = r5
            goto L_0x0fb6
        L_0x0fb3:
            int r0 = r0 + 1
            goto L_0x0var_
        L_0x0fb6:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r5 = r0.w
            if (r5 == 0) goto L_0x0fc0
            int r0 = r0.h
            if (r0 != 0) goto L_0x0fcc
        L_0x0fc0:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            r5 = 1125515264(0x43160000, float:150.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.h = r5
            r0.w = r5
        L_0x0fcc:
            r1.documentAttach = r2
            r0 = 9
            r1.documentAttachType = r0
            goto L_0x0var_
        L_0x0fd3:
            r5 = r39
            r11 = r42
            r1.calcBackgroundWidth(r5, r11, r4)
            int r0 = r1.backgroundWidth
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r5
            if (r0 >= r7) goto L_0x0fee
            r0 = 1101004800(0x41a00000, float:20.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = r0 + r5
            r1.backgroundWidth = r0
        L_0x0fee:
            boolean r0 = org.telegram.messenger.MessageObject.isVoiceDocument(r2)
            if (r0 == 0) goto L_0x109e
            int r0 = r1.backgroundWidth
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r0 = r0 - r7
            r1.createDocumentLayout(r0, r14)
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            int r0 = r0.textHeight
            r7 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            int r7 = r1.linkPreviewHeight
            int r0 = r0 + r7
            r1.mediaOffsetY = r0
            int r0 = r1.totalHeight
            r7 = 1110441984(0x42300000, float:44.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            r1.totalHeight = r0
            int r0 = r1.linkPreviewHeight
            r7 = 1110441984(0x42300000, float:44.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            r1.linkPreviewHeight = r0
            r0 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r5 = r5 - r0
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1065
            int r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            boolean r7 = r1.isChat
            if (r7 == 0) goto L_0x1048
            boolean r7 = r59.needDrawAvatar()
            if (r7 == 0) goto L_0x1048
            boolean r7 = r59.isOutOwner()
            if (r7 != 0) goto L_0x1048
            r7 = 1112539136(0x42500000, float:52.0)
            goto L_0x1049
        L_0x1048:
            r7 = 0
        L_0x1049:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r7
            r7 = 1130102784(0x435CLASSNAME, float:220.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = java.lang.Math.min(r0, r7)
            r7 = 1106247680(0x41var_, float:30.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r7
            int r0 = r0 + r3
            int r0 = java.lang.Math.max(r4, r0)
            goto L_0x1098
        L_0x1065:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
            boolean r7 = r1.isChat
            if (r7 == 0) goto L_0x107c
            boolean r7 = r59.needDrawAvatar()
            if (r7 == 0) goto L_0x107c
            boolean r7 = r59.isOutOwner()
            if (r7 != 0) goto L_0x107c
            r7 = 1112539136(0x42500000, float:52.0)
            goto L_0x107d
        L_0x107c:
            r7 = 0
        L_0x107d:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r7
            r7 = 1130102784(0x435CLASSNAME, float:220.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = java.lang.Math.min(r0, r7)
            r7 = 1106247680(0x41var_, float:30.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r7
            int r0 = r0 + r3
            int r0 = java.lang.Math.max(r4, r0)
        L_0x1098:
            r4 = r0
            r1.calcBackgroundWidth(r5, r11, r4)
            goto L_0x112e
        L_0x109e:
            boolean r0 = org.telegram.messenger.MessageObject.isMusicDocument(r2)
            if (r0 == 0) goto L_0x1135
            int r0 = r1.backgroundWidth
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r0 = r0 - r7
            int r0 = r1.createDocumentLayout(r0, r14)
            org.telegram.messenger.MessageObject r7 = r1.currentMessageObject
            int r7 = r7.textHeight
            r8 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 + r8
            int r8 = r1.linkPreviewHeight
            int r7 = r7 + r8
            r1.mediaOffsetY = r7
            int r7 = r1.totalHeight
            r8 = 1113587712(0x42600000, float:56.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 + r8
            r1.totalHeight = r7
            int r7 = r1.linkPreviewHeight
            r8 = 1113587712(0x42600000, float:56.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 + r8
            r1.linkPreviewHeight = r7
            r7 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            int r0 = r0 + r3
            r7 = 1119617024(0x42bCLASSNAME, float:94.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            int r0 = java.lang.Math.max(r4, r0)
            android.text.StaticLayout r4 = r1.songLayout
            if (r4 == 0) goto L_0x1109
            int r4 = r4.getLineCount()
            if (r4 <= 0) goto L_0x1109
            float r0 = (float) r0
            android.text.StaticLayout r4 = r1.songLayout
            r7 = 0
            float r4 = r4.getLineWidth(r7)
            float r7 = (float) r3
            float r4 = r4 + r7
            r7 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r4 = r4 + r7
            float r0 = java.lang.Math.max(r0, r4)
            int r0 = (int) r0
        L_0x1109:
            android.text.StaticLayout r4 = r1.performerLayout
            if (r4 == 0) goto L_0x112a
            int r4 = r4.getLineCount()
            if (r4 <= 0) goto L_0x112a
            float r0 = (float) r0
            android.text.StaticLayout r4 = r1.performerLayout
            r7 = 0
            float r4 = r4.getLineWidth(r7)
            float r7 = (float) r3
            float r4 = r4 + r7
            r7 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r4 = r4 + r7
            float r0 = java.lang.Math.max(r0, r4)
            int r0 = (int) r0
        L_0x112a:
            r4 = r0
            r1.calcBackgroundWidth(r5, r11, r4)
        L_0x112e:
            r0 = r5
            r13 = r29
            r7 = r41
            goto L_0x12a9
        L_0x1135:
            int r0 = r1.backgroundWidth
            r7 = 1126694912(0x43280000, float:168.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r7
            r1.createDocumentLayout(r0, r14)
            r7 = 1
            r1.drawImageButton = r7
            boolean r0 = r1.drawPhotoImage
            if (r0 == 0) goto L_0x1177
            int r0 = r1.totalHeight
            r7 = 1120403456(0x42CLASSNAME, float:100.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            r1.totalHeight = r0
            int r0 = r1.linkPreviewHeight
            r7 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            r1.linkPreviewHeight = r0
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            int r7 = r1.totalHeight
            int r8 = r1.namesOffset
            int r7 = r7 + r8
            r8 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r13 = 0
            r0.setImageCoords(r13, r7, r8, r9)
            goto L_0x123a
        L_0x1177:
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            int r0 = r0.textHeight
            r7 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            int r7 = r1.linkPreviewHeight
            int r0 = r0 + r7
            r1.mediaOffsetY = r0
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            int r7 = r1.totalHeight
            int r8 = r1.namesOffset
            int r7 = r7 + r8
            r8 = 1096810496(0x41600000, float:14.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 - r8
            r8 = 1113587712(0x42600000, float:56.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 1113587712(0x42600000, float:56.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r13 = 0
            r0.setImageCoords(r13, r7, r8, r9)
            int r0 = r1.totalHeight
            r7 = 1115684864(0x42800000, float:64.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r7
            r1.totalHeight = r0
            int r0 = r1.linkPreviewHeight
            r7 = 1112014848(0x42480000, float:50.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r8
            r1.linkPreviewHeight = r0
            android.text.StaticLayout r0 = r1.docTitleLayout
            if (r0 == 0) goto L_0x123a
            int r0 = r0.getLineCount()
            r7 = 1
            if (r0 <= r7) goto L_0x123a
            android.text.StaticLayout r0 = r1.docTitleLayout
            int r0 = r0.getLineCount()
            int r0 = r0 - r7
            r7 = 1098907648(0x41800000, float:16.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 * r7
            int r7 = r1.totalHeight
            int r7 = r7 + r0
            r1.totalHeight = r7
            int r7 = r1.linkPreviewHeight
            int r7 = r7 + r0
            r1.linkPreviewHeight = r7
            goto L_0x123a
        L_0x11e0:
            r5 = r39
            r11 = r42
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r2.thumbs
            r7 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r7)
            r1.currentPhotoObject = r0
            r1.photoParentObject = r2
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            if (r0 == 0) goto L_0x1235
            int r7 = r0.w
            if (r7 == 0) goto L_0x11fc
            int r0 = r0.h
            if (r0 != 0) goto L_0x1235
        L_0x11fc:
            r0 = 0
        L_0x11fd:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r2.attributes
            int r7 = r7.size()
            if (r0 >= r7) goto L_0x121f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r2.attributes
            java.lang.Object r7 = r7.get(r0)
            org.telegram.tgnet.TLRPC$DocumentAttribute r7 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r7
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize
            if (r8 == 0) goto L_0x121c
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r8 = r7.w
            r0.w = r8
            int r7 = r7.h
            r0.h = r7
            goto L_0x121f
        L_0x121c:
            int r0 = r0 + 1
            goto L_0x11fd
        L_0x121f:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r7 = r0.w
            if (r7 == 0) goto L_0x1229
            int r0 = r0.h
            if (r0 != 0) goto L_0x1235
        L_0x1229:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            r7 = 1125515264(0x43160000, float:150.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r0.h = r7
            r0.w = r7
        L_0x1235:
            r1.documentAttach = r2
            r7 = 6
            r1.documentAttachType = r7
        L_0x123a:
            r13 = r29
            goto L_0x12a6
        L_0x123e:
            r5 = r39
            r11 = r42
            r10 = r43
            r2 = r44
            r6 = 1065353216(0x3var_, float:1.0)
            if (r10 == 0) goto L_0x128c
            if (r29 == 0) goto L_0x1258
            java.lang.String r0 = "photo"
            r13 = r29
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x125a
            r0 = 1
            goto L_0x125b
        L_0x1258:
            r13 = r29
        L_0x125a:
            r0 = 0
        L_0x125b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r14.photoThumbs
            if (r0 != 0) goto L_0x1264
            if (r12 != 0) goto L_0x1262
            goto L_0x1264
        L_0x1262:
            r8 = r15
            goto L_0x1268
        L_0x1264:
            int r8 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
        L_0x1268:
            r9 = r0 ^ 1
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8, r9)
            r1.currentPhotoObject = r7
            org.telegram.tgnet.TLObject r7 = r14.photoThumbsObject
            r1.photoParentObject = r7
            r7 = 1
            r0 = r0 ^ r7
            r1.checkOnlyButtonPressed = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r14.photoThumbs
            r7 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r7)
            r1.currentPhotoObjectThumb = r0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObject
            if (r0 != r7) goto L_0x12a6
            r7 = 0
            r1.currentPhotoObjectThumb = r7
            goto L_0x12a6
        L_0x128c:
            r13 = r29
            if (r41 == 0) goto L_0x12a6
            r7 = r41
            java.lang.String r0 = r7.mime_type
            java.lang.String r8 = "image/"
            boolean r0 = r0.startsWith(r8)
            if (r0 != 0) goto L_0x129f
            r7 = 0
            r9 = 0
            goto L_0x12a1
        L_0x129f:
            r9 = r7
            r7 = 0
        L_0x12a1:
            r1.drawImageButton = r7
            r0 = r5
            r7 = r9
            goto L_0x12a9
        L_0x12a6:
            r7 = r41
        L_0x12a8:
            r0 = r5
        L_0x12a9:
            int r5 = r1.documentAttachType
            r8 = 5
            if (r5 == r8) goto L_0x19a4
            r8 = 3
            if (r5 == r8) goto L_0x19a4
            r8 = 1
            if (r5 == r8) goto L_0x19a4
            org.telegram.tgnet.TLRPC$PhotoSize r8 = r1.currentPhotoObject
            if (r8 != 0) goto L_0x12e6
            if (r7 != 0) goto L_0x12e6
            r8 = 8
            if (r5 == r8) goto L_0x12e6
            r8 = 9
            if (r5 != r8) goto L_0x12c3
            goto L_0x12e6
        L_0x12c3:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r3 = 0
            r2.setImageBitmap((android.graphics.drawable.Drawable) r3)
            int r2 = r1.linkPreviewHeight
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            r1.linkPreviewHeight = r2
            int r2 = r1.totalHeight
            r3 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r1.totalHeight = r2
            r42 = r11
            r60 = 1065353216(0x3var_, float:1.0)
            r15 = 1
            goto L_0x189c
        L_0x12e6:
            if (r10 == 0) goto L_0x12ea
            if (r12 == 0) goto L_0x1312
        L_0x12ea:
            if (r13 == 0) goto L_0x1314
            java.lang.String r5 = "photo"
            boolean r5 = r13.equals(r5)
            if (r5 != 0) goto L_0x1312
            java.lang.String r5 = "document"
            boolean r5 = r13.equals(r5)
            if (r5 == 0) goto L_0x1301
            int r5 = r1.documentAttachType
            r8 = 6
            if (r5 != r8) goto L_0x1312
        L_0x1301:
            java.lang.String r5 = "gif"
            boolean r5 = r13.equals(r5)
            if (r5 != 0) goto L_0x1312
            int r5 = r1.documentAttachType
            r8 = 4
            if (r5 == r8) goto L_0x1312
            r8 = 8
            if (r5 != r8) goto L_0x1314
        L_0x1312:
            r5 = 1
            goto L_0x1315
        L_0x1314:
            r5 = 0
        L_0x1315:
            r1.drawImageButton = r5
            int r5 = r1.linkPreviewHeight
            if (r5 == 0) goto L_0x132b
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r5 + r8
            r1.linkPreviewHeight = r5
            int r5 = r1.totalHeight
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r5 + r8
            r1.totalHeight = r5
        L_0x132b:
            int r5 = r1.imageBackgroundSideColor
            if (r5 == 0) goto L_0x1336
            r5 = 1129316352(0x43500000, float:208.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x1371
        L_0x1336:
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObject
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty
            if (r8 == 0) goto L_0x1342
            int r5 = r5.w
            if (r5 == 0) goto L_0x1342
            r15 = r5
            goto L_0x1371
        L_0x1342:
            int r5 = r1.documentAttachType
            r8 = 6
            if (r5 == r8) goto L_0x135c
            r8 = 8
            if (r5 == r8) goto L_0x135c
            r8 = 9
            if (r5 != r8) goto L_0x1350
            goto L_0x135c
        L_0x1350:
            r8 = 7
            if (r5 != r8) goto L_0x1371
            int r15 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            org.telegram.messenger.ImageReceiver r5 = r1.photoImage
            r8 = 1
            r5.setAllowDecodeSingleFrame(r8)
            goto L_0x1371
        L_0x135c:
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x1367
            int r5 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            goto L_0x136b
        L_0x1367:
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r5.x
        L_0x136b:
            float r5 = (float) r5
            r8 = 1056964608(0x3var_, float:0.5)
            float r5 = r5 * r8
            int r15 = (int) r5
        L_0x1371:
            boolean r5 = r1.hasInvoicePreview
            if (r5 == 0) goto L_0x137c
            r5 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x137d
        L_0x137c:
            r5 = 0
        L_0x137d:
            int r5 = r15 - r5
            int r5 = r5 + r3
            int r24 = java.lang.Math.max(r4, r5)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            if (r3 == 0) goto L_0x1392
            r8 = -1
            r3.size = r8
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObjectThumb
            if (r3 == 0) goto L_0x1397
            r3.size = r8
            goto L_0x1397
        L_0x1392:
            r8 = -1
            if (r7 == 0) goto L_0x1397
            r7.size = r8
        L_0x1397:
            int r3 = r1.imageBackgroundSideColor
            if (r3 == 0) goto L_0x13a5
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r24 - r3
            r1.imageBackgroundSideWidth = r3
        L_0x13a5:
            if (r12 != 0) goto L_0x1448
            int r3 = r1.documentAttachType
            r4 = 7
            if (r3 != r4) goto L_0x13ae
            goto L_0x1448
        L_0x13ae:
            boolean r3 = r1.hasGamePreview
            if (r3 != 0) goto L_0x1430
            boolean r3 = r1.hasInvoicePreview
            if (r3 == 0) goto L_0x13b8
            goto L_0x1430
        L_0x13b8:
            int r3 = r1.drawInstantViewType
            if (r3 != r4) goto L_0x13c1
            r3 = 560(0x230, float:7.85E-43)
            r4 = 678(0x2a6, float:9.5E-43)
            goto L_0x13d3
        L_0x13c1:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            if (r3 == 0) goto L_0x13cf
            int r4 = r3.w
            int r3 = r3.h
            r57 = r4
            r4 = r3
            r3 = r57
            goto L_0x13d3
        L_0x13cf:
            r3 = 30
            r4 = 50
        L_0x13d3:
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r15 = r15 - r5
            float r5 = (float) r15
            float r5 = r3 / r5
            float r3 = r3 / r5
            int r3 = (int) r3
            float r4 = (float) r4
            float r4 = r4 / r5
            int r4 = (int) r4
            if (r45 == 0) goto L_0x1402
            if (r45 == 0) goto L_0x13f6
            java.lang.String r5 = r45.toLowerCase()
            java.lang.String r9 = "instagram"
            boolean r5 = r5.equals(r9)
            if (r5 != 0) goto L_0x13f6
            int r5 = r1.documentAttachType
            if (r5 != 0) goto L_0x13f6
            goto L_0x1402
        L_0x13f6:
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r5.y
            int r9 = r5 / 2
            if (r4 <= r9) goto L_0x140d
            r9 = 2
            int r4 = r5 / 2
            goto L_0x140d
        L_0x1402:
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r5.y
            int r9 = r5 / 3
            if (r4 <= r9) goto L_0x140d
            r9 = 3
            int r4 = r5 / 3
        L_0x140d:
            int r5 = r1.imageBackgroundSideColor
            if (r5 == 0) goto L_0x1420
            float r4 = (float) r4
            r5 = 1126170624(0x43200000, float:160.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r5 = r4 / r5
            float r3 = (float) r3
            float r3 = r3 / r5
            int r3 = (int) r3
            float r4 = r4 / r5
            int r4 = (int) r4
        L_0x1420:
            r15 = r4
            r4 = 1114636288(0x42700000, float:60.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            if (r15 >= r4) goto L_0x1449
            r4 = 1114636288(0x42700000, float:60.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r4)
            goto L_0x1449
        L_0x1430:
            r3 = 640(0x280, float:8.97E-43)
            r4 = 360(0x168, float:5.04E-43)
            float r3 = (float) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r15 = r15 - r5
            float r5 = (float) r15
            float r5 = r3 / r5
            float r3 = r3 / r5
            int r15 = (int) r3
            float r3 = (float) r4
            float r3 = r3 / r5
            int r3 = (int) r3
            r57 = r15
            r15 = r3
            r3 = r57
            goto L_0x1449
        L_0x1448:
            r3 = r15
        L_0x1449:
            boolean r4 = r1.isSmallImage
            if (r4 == 0) goto L_0x1482
            r4 = 1112014848(0x42480000, float:50.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r5 + r23
            int r9 = r1.linkPreviewHeight
            if (r5 <= r9) goto L_0x1476
            int r5 = r1.totalHeight
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r9 = r9 + r23
            int r10 = r1.linkPreviewHeight
            int r9 = r9 - r10
            r10 = 1090519040(0x41000000, float:8.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 + r10
            int r5 = r5 + r9
            r1.totalHeight = r5
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r23
            r1.linkPreviewHeight = r4
        L_0x1476:
            int r4 = r1.linkPreviewHeight
            r5 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            r1.linkPreviewHeight = r4
            goto L_0x1493
        L_0x1482:
            int r4 = r1.totalHeight
            r5 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r5 + r15
            int r4 = r4 + r5
            r1.totalHeight = r4
            int r4 = r1.linkPreviewHeight
            int r4 = r4 + r15
            r1.linkPreviewHeight = r4
        L_0x1493:
            int r4 = r1.documentAttachType
            r5 = 8
            if (r4 != r5) goto L_0x14b0
            int r4 = r1.imageBackgroundSideColor
            if (r4 != 0) goto L_0x14b0
            org.telegram.messenger.ImageReceiver r4 = r1.photoImage
            r5 = 1095761920(0x41500000, float:13.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r24 - r5
            int r5 = java.lang.Math.max(r5, r3)
            r9 = 0
            r4.setImageCoords(r9, r9, r5, r15)
            goto L_0x14b6
        L_0x14b0:
            r9 = 0
            org.telegram.messenger.ImageReceiver r4 = r1.photoImage
            r4.setImageCoords(r9, r9, r3, r15)
        L_0x14b6:
            float r3 = (float) r3
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 / r4
            int r3 = (int) r3
            float r5 = (float) r15
            float r5 = r5 / r4
            int r4 = (int) r5
            java.util.Locale r5 = java.util.Locale.US
            r9 = 2
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.Integer r12 = java.lang.Integer.valueOf(r3)
            r15 = 0
            r10[r15] = r12
            java.lang.Integer r12 = java.lang.Integer.valueOf(r4)
            r23 = 1
            r10[r23] = r12
            java.lang.String r12 = "%d_%d"
            java.lang.String r5 = java.lang.String.format(r5, r12, r10)
            r1.currentPhotoFilter = r5
            java.util.Locale r5 = java.util.Locale.US
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.Integer r12 = java.lang.Integer.valueOf(r3)
            r10[r15] = r12
            java.lang.Integer r12 = java.lang.Integer.valueOf(r4)
            r10[r23] = r12
            java.lang.String r12 = "%d_%d_b"
            java.lang.String r5 = java.lang.String.format(r5, r12, r10)
            r1.currentPhotoFilterThumb = r5
            if (r7 == 0) goto L_0x1515
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForWebFile(r7)
            java.lang.String r4 = r1.currentPhotoFilter
            r5 = 0
            r10 = 0
            int r7 = r7.size
            r12 = 0
            r15 = 1
            r60 = 1065353216(0x3var_, float:1.0)
            r6 = r10
            r10 = -1
            r8 = r12
            r12 = 2
            r9 = r59
            r10 = r15
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
        L_0x150e:
            r42 = r11
            r29 = r13
            r15 = 1
            goto L_0x1833
        L_0x1515:
            r60 = 1065353216(0x3var_, float:1.0)
            r12 = 2
            int r5 = r1.documentAttachType
            r6 = 8
            if (r5 != r6) goto L_0x1560
            boolean r3 = r14.mediaExists
            if (r3 == 0) goto L_0x1544
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            org.telegram.tgnet.TLRPC$Document r4 = r1.documentAttach
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForDocument(r4)
            java.lang.String r5 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r1.currentPhotoObject
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForDocument(r6, r2)
            r7 = 0
            r10 = 1
            java.lang.String r8 = "b1"
            java.lang.String r9 = "jpg"
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r8
            r8 = r9
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x150e
        L_0x1544:
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            r4 = 0
            r5 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r1.currentPhotoObject
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForDocument(r6, r2)
            r7 = 0
            r10 = 1
            java.lang.String r8 = "b1"
            java.lang.String r9 = "jpg"
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r8
            r8 = r9
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x150e
        L_0x1560:
            r6 = 9
            if (r5 != r6) goto L_0x15a6
            boolean r3 = r2 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r3 == 0) goto L_0x1583
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            java.lang.String r5 = r1.currentPhotoFilter
            r6 = 0
            r7 = 0
            r10 = 1
            java.lang.String r8 = "b1"
            java.lang.String r9 = "jpg"
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r8
            r8 = r9
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x150e
        L_0x1583:
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r1.currentPhotoObject
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForDocument(r4, r2)
            java.lang.String r5 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r1.currentPhotoObjectThumb
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForDocument(r6, r2)
            r7 = 0
            r10 = 1
            java.lang.String r8 = "b1"
            java.lang.String r9 = "jpg"
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r8
            r8 = r9
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x150e
        L_0x15a6:
            r6 = 6
            if (r5 != r6) goto L_0x1613
            boolean r2 = r59.isSticker()
            boolean r5 = org.telegram.messenger.SharedConfig.loopStickers
            if (r5 != 0) goto L_0x15e7
            if (r2 == 0) goto L_0x15b4
            goto L_0x15e7
        L_0x15b4:
            java.util.Locale r2 = java.util.Locale.US
            r15 = 3
            java.lang.Object[] r5 = new java.lang.Object[r15]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r21 = 0
            r5[r21] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            r4 = 1
            r5[r4] = r3
            java.lang.String r3 = r59.toString()
            r5[r12] = r3
            java.lang.String r3 = "%d_%d_nr_%s"
            java.lang.String r2 = java.lang.String.format(r2, r3, r5)
            r1.currentPhotoFilter = r2
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r3 = r1.delegate
            boolean r3 = r3.shouldRepeatSticker(r14)
            if (r3 == 0) goto L_0x15e2
            r3 = 2
            goto L_0x15e3
        L_0x15e2:
            r3 = 3
        L_0x15e3:
            r2.setAutoRepeat(r3)
            goto L_0x15f0
        L_0x15e7:
            r15 = 3
            r21 = 0
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r3 = 1
            r2.setAutoRepeat(r3)
        L_0x15f0:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.tgnet.TLRPC$Document r3 = r1.documentAttach
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            java.lang.String r4 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r6 = r1.documentAttach
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument(r5, r6)
            org.telegram.tgnet.TLRPC$Document r6 = r1.documentAttach
            int r7 = r6.size
            r10 = 1
            java.lang.String r6 = "b1"
            java.lang.String r8 = "webp"
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x150e
        L_0x1613:
            r15 = 3
            r21 = 0
            r6 = 4
            if (r5 != r6) goto L_0x16e9
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r3 = 1
            r2.setNeedsQualityThumb(r3)
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r2.setShouldGenerateQualityThumb(r3)
            boolean r2 = org.telegram.messenger.SharedConfig.autoplayVideo
            if (r2 == 0) goto L_0x1694
            org.telegram.messenger.MessageObject r2 = r1.currentMessageObject
            boolean r2 = r2.mediaExists
            if (r2 != 0) goto L_0x1642
            boolean r2 = r59.canStreamVideo()
            if (r2 == 0) goto L_0x1694
            int r2 = r1.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.MessageObject r3 = r1.currentMessageObject
            boolean r2 = r2.canDownloadMedia((org.telegram.messenger.MessageObject) r3)
            if (r2 == 0) goto L_0x1694
        L_0x1642:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r10 = 1
            r2.setAllowDecodeSingleFrame(r10)
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r2.setAllowStartAnimation(r10)
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r2.startAnimation()
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.tgnet.TLRPC$Document r3 = r1.documentAttach
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r1.currentPhotoObject
            org.telegram.tgnet.TLObject r5 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r4, r5)
            java.lang.String r6 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLRPC$Document r7 = r1.documentAttach
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForDocument(r4, r7)
            java.lang.String r8 = r1.currentPhotoFilterThumb
            r9 = 0
            org.telegram.tgnet.TLRPC$Document r4 = r1.documentAttach
            int r4 = r4.size
            r22 = 0
            r23 = 0
            java.lang.String r25 = "g"
            r26 = r4
            r4 = r25
            r25 = 1
            r10 = r26
            r15 = r11
            r11 = r22
            r12 = r59
            r29 = r13
            r42 = r15
            r15 = 1
            r13 = r23
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r1.autoPlayingMedia = r15
            goto L_0x1833
        L_0x1694:
            r42 = r11
            r29 = r13
            r15 = 1
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r1.currentPhotoObjectThumb
            if (r2 == 0) goto L_0x16bd
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            org.telegram.tgnet.TLObject r4 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForObject(r3, r4)
            java.lang.String r4 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            java.lang.String r6 = r1.currentPhotoFilterThumb
            r7 = 0
            r8 = 0
            r10 = 0
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x1833
        L_0x16bd:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r3 = 0
            r4 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObject
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r1.currentPhotoObject
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r7 != 0) goto L_0x16dd
            java.lang.String r6 = r6.type
            java.lang.String r7 = "s"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x16da
            goto L_0x16dd
        L_0x16da:
            java.lang.String r6 = r1.currentPhotoFilter
            goto L_0x16df
        L_0x16dd:
            java.lang.String r6 = r1.currentPhotoFilterThumb
        L_0x16df:
            r7 = 0
            r8 = 0
            r10 = 0
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x1833
        L_0x16e9:
            r42 = r11
            r29 = r13
            r13 = 2
            r15 = 1
            if (r5 == r13) goto L_0x1786
            r6 = 7
            if (r5 != r6) goto L_0x16f6
            goto L_0x1786
        L_0x16f6:
            boolean r2 = r14.mediaExists
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObject
            java.lang.String r5 = org.telegram.messenger.FileLoader.getAttachFileName(r5)
            boolean r6 = r1.hasGamePreview
            if (r6 != 0) goto L_0x1763
            if (r2 != 0) goto L_0x1763
            int r2 = r1.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.MessageObject r6 = r1.currentMessageObject
            boolean r2 = r2.canDownloadMedia((org.telegram.messenger.MessageObject) r6)
            if (r2 != 0) goto L_0x1763
            int r2 = r1.currentAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            boolean r2 = r2.isLoadingFile(r5)
            if (r2 == 0) goto L_0x171f
            goto L_0x1763
        L_0x171f:
            r1.photoNotSet = r15
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r1.currentPhotoObjectThumb
            if (r2 == 0) goto L_0x175a
            org.telegram.messenger.ImageReceiver r5 = r1.photoImage
            r6 = 0
            r7 = 0
            org.telegram.tgnet.TLObject r8 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForObject(r2, r8)
            java.util.Locale r2 = java.util.Locale.US
            java.lang.Object[] r9 = new java.lang.Object[r13]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r12 = 0
            r9[r12] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            r9[r15] = r3
            java.lang.String r3 = "%d_%d_b"
            java.lang.String r9 = java.lang.String.format(r2, r3, r9)
            r10 = 0
            r11 = 0
            r21 = 0
            r2 = r5
            r3 = r6
            r4 = r7
            r5 = r8
            r6 = r9
            r7 = r10
            r8 = r11
            r9 = r59
            r10 = r21
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x1833
        L_0x175a:
            r12 = 0
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r3 = 0
            r2.setImageBitmap((android.graphics.drawable.Drawable) r3)
            goto L_0x1833
        L_0x1763:
            r12 = 0
            r1.photoNotSet = r12
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            org.telegram.tgnet.TLObject r4 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForObject(r3, r4)
            java.lang.String r4 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            java.lang.String r6 = r1.currentPhotoFilterThumb
            r7 = 0
            r8 = 0
            r10 = 0
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x1833
        L_0x1786:
            r12 = 0
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            r3.setAllowDecodeSingleFrame(r15)
            org.telegram.messenger.FileLoader.getAttachFileName(r2)
            boolean r3 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r2)
            if (r3 == 0) goto L_0x17aa
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            int r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            int r4 = r4 / r13
            r3.setRoundRadius(r4)
            int r3 = r1.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            boolean r3 = r3.canDownloadMedia((org.telegram.messenger.MessageObject) r4)
            goto L_0x17be
        L_0x17aa:
            boolean r3 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r2)
            if (r3 == 0) goto L_0x17bd
            int r3 = r1.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            boolean r3 = r3.canDownloadMedia((org.telegram.messenger.MessageObject) r4)
            goto L_0x17be
        L_0x17bd:
            r3 = 0
        L_0x17be:
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r1.currentPhotoObject
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r5 != 0) goto L_0x17d2
            java.lang.String r4 = r4.type
            java.lang.String r5 = "s"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x17cf
            goto L_0x17d2
        L_0x17cf:
            java.lang.String r4 = r1.currentPhotoFilter
            goto L_0x17d4
        L_0x17d2:
            java.lang.String r4 = r1.currentPhotoFilterThumb
        L_0x17d4:
            r34 = r4
            boolean r4 = r14.mediaExists
            if (r4 != 0) goto L_0x17fb
            if (r3 == 0) goto L_0x17dd
            goto L_0x17fb
        L_0x17dd:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r31 = 0
            r32 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r4 = r1.documentAttach
            org.telegram.messenger.ImageLocation r33 = org.telegram.messenger.ImageLocation.getForDocument(r3, r4)
            r35 = 0
            r36 = 0
            org.telegram.messenger.MessageObject r3 = r1.currentMessageObject
            r38 = 0
            r30 = r2
            r37 = r3
            r30.setImage(r31, r32, r33, r34, r35, r36, r37, r38)
            goto L_0x1833
        L_0x17fb:
            r1.autoPlayingMedia = r15
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            int r5 = r2.size
            r6 = 32768(0x8000, float:4.5918E-41)
            if (r5 >= r6) goto L_0x180c
            r5 = 0
            goto L_0x180e
        L_0x180c:
            java.lang.String r5 = "g"
        L_0x180e:
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r1.currentPhotoObject
            org.telegram.tgnet.TLRPC$Document r7 = r1.documentAttach
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForDocument(r6, r7)
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLRPC$Document r8 = r1.documentAttach
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForDocument(r7, r8)
            java.lang.String r8 = r1.currentPhotoFilterThumb
            r9 = 0
            int r10 = r2.size
            r11 = 0
            r21 = 0
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r34
            r12 = r59
            r13 = r21
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
        L_0x1833:
            r1.drawPhotoImage = r15
            if (r29 == 0) goto L_0x1869
            java.lang.String r2 = "video"
            r6 = r29
            boolean r2 = r6.equals(r2)
            if (r2 == 0) goto L_0x1869
            if (r40 == 0) goto L_0x1869
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.formatShortDuration(r40)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_durationPaint
            float r2 = r2.measureText(r4)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            r1.durationWidth = r2
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.chat_durationPaint
            int r6 = r1.durationWidth
            android.text.Layout$Alignment r7 = android.text.Layout.Alignment.ALIGN_NORMAL
            r8 = 1065353216(0x3var_, float:1.0)
            r9 = 0
            r10 = 0
            r3 = r2
            r3.<init>(r4, r5, r6, r7, r8, r9, r10)
            r1.videoInfoLayout = r2
            goto L_0x189a
        L_0x1869:
            boolean r2 = r1.hasGamePreview
            if (r2 == 0) goto L_0x189a
            r2 = 2131624270(0x7f0e014e, float:1.8875715E38)
            java.lang.String r3 = "AttachGame"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r4 = r2.toUpperCase()
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_gamePaint
            float r2 = r2.measureText(r4)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            r1.durationWidth = r2
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.chat_gamePaint
            int r6 = r1.durationWidth
            android.text.Layout$Alignment r7 = android.text.Layout.Alignment.ALIGN_NORMAL
            r8 = 1065353216(0x3var_, float:1.0)
            r9 = 0
            r10 = 0
            r3 = r2
            r3.<init>(r4, r5, r6, r7, r8, r9, r10)
            r1.videoInfoLayout = r2
        L_0x189a:
            r4 = r24
        L_0x189c:
            boolean r2 = r1.hasInvoicePreview
            if (r2 == 0) goto L_0x197e
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            int r3 = r2.flags
            r3 = r3 & 4
            if (r3 == 0) goto L_0x18b8
            r2 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r3 = "PaymentReceipt"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            goto L_0x18d7
        L_0x18b8:
            boolean r2 = r2.test
            if (r2 == 0) goto L_0x18ca
            r2 = 2131626141(0x7f0e089d, float:1.887951E38)
            java.lang.String r3 = "PaymentTestInvoice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            goto L_0x18d7
        L_0x18ca:
            r2 = 2131626110(0x7f0e087e, float:1.8879447E38)
            java.lang.String r3 = "PaymentInvoice"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
        L_0x18d7:
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            long r6 = r5.total_amount
            java.lang.String r5 = r5.currency
            java.lang.String r3 = r3.formatCurrencyString(r6, r5)
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            java.lang.String r7 = " "
            r5.append(r7)
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            r6.<init>(r2)
            org.telegram.ui.Components.TypefaceSpan r2 = new org.telegram.ui.Components.TypefaceSpan
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r2.<init>(r5)
            int r3 = r3.length()
            r5 = 33
            r13 = 0
            r6.setSpan(r2, r13, r3, r5)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint
            int r3 = r6.length()
            float r2 = r2.measureText(r6, r13, r3)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            r1.durationWidth = r2
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint
            int r3 = r1.durationWidth
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r8 = r3 + r5
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            r12 = 0
            r5 = r2
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)
            r1.videoInfoLayout = r2
            boolean r2 = r1.drawPhotoImage
            if (r2 != 0) goto L_0x197f
            int r2 = r1.totalHeight
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r1.totalHeight = r2
            int r2 = r1.timeWidth
            boolean r3 = r59.isOutOwner()
            if (r3 == 0) goto L_0x1959
            r12 = 20
            goto L_0x195a
        L_0x1959:
            r12 = 0
        L_0x195a:
            int r12 = r12 + 14
            float r3 = (float) r12
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            int r3 = r1.durationWidth
            int r5 = r3 + r2
            if (r5 <= r0) goto L_0x1978
            int r4 = java.lang.Math.max(r3, r4)
            int r2 = r1.totalHeight
            r3 = 1094713344(0x41400000, float:12.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r1.totalHeight = r2
            goto L_0x197f
        L_0x1978:
            int r3 = r3 + r2
            int r4 = java.lang.Math.max(r3, r4)
            goto L_0x197f
        L_0x197e:
            r13 = 0
        L_0x197f:
            boolean r2 = r1.hasGamePreview
            if (r2 == 0) goto L_0x199e
            int r2 = r14.textHeight
            if (r2 == 0) goto L_0x199e
            int r3 = r1.linkPreviewHeight
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 + r5
            int r3 = r3 + r2
            r1.linkPreviewHeight = r3
            int r2 = r1.totalHeight
            r3 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r1.totalHeight = r2
        L_0x199e:
            r2 = r42
            r1.calcBackgroundWidth(r0, r2, r4)
            goto L_0x19a8
        L_0x19a4:
            r60 = 1065353216(0x3var_, float:1.0)
            r13 = 0
            r15 = 1
        L_0x19a8:
            r58.createInstantViewButton()
            goto L_0x2242
        L_0x19ad:
            r60 = 1065353216(0x3var_, float:1.0)
            r13 = 0
            r15 = 1
            r2 = 16
            r4 = 1120665600(0x42cCLASSNAME, float:102.0)
            if (r0 != r2) goto L_0x1b09
            r1.drawName = r13
            r1.drawForwardedName = r13
            r1.drawPhotoImage = r13
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x19ec
            int r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x19d8
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x19d8
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x19d8
            goto L_0x19da
        L_0x19d8:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x19da:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r2
            r2 = 1132920832(0x43870000, float:270.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = java.lang.Math.min(r0, r2)
            r1.backgroundWidth = r0
            goto L_0x1a14
        L_0x19ec:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x1a01
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x1a01
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x1a01
            goto L_0x1a03
        L_0x1a01:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x1a03:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r2
            r2 = 1132920832(0x43870000, float:270.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = java.lang.Math.min(r0, r2)
            r1.backgroundWidth = r0
        L_0x1a14:
            int r0 = r1.backgroundWidth
            r2 = 1106771968(0x41var_, float:31.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r1.availableTimeWidth = r0
            int r0 = r58.getMaxNameWidth()
            r2 = 1112014848(0x42480000, float:50.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            if (r0 >= 0) goto L_0x1a30
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
        L_0x1a30:
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterDay
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            int r3 = r3.date
            long r3 = (long) r3
            r5 = 1000(0x3e8, double:4.94E-321)
            long r3 = r3 * r5
            java.lang.String r2 = r2.format((long) r3)
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall r3 = (org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall) r3
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r4 = r3.reason
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed
            boolean r5 = r59.isOutOwner()
            if (r5 == 0) goto L_0x1a69
            if (r4 == 0) goto L_0x1a5f
            r4 = 2131624452(0x7f0e0204, float:1.8876084E38)
            java.lang.String r5 = "CallMessageOutgoingMissed"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            goto L_0x1a8e
        L_0x1a5f:
            r4 = 2131624451(0x7f0e0203, float:1.8876082E38)
            java.lang.String r5 = "CallMessageOutgoing"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            goto L_0x1a8e
        L_0x1a69:
            if (r4 == 0) goto L_0x1a75
            r4 = 2131624450(0x7f0e0202, float:1.887608E38)
            java.lang.String r5 = "CallMessageIncomingMissed"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            goto L_0x1a8e
        L_0x1a75:
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r4 = r3.reason
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy
            if (r4 == 0) goto L_0x1a85
            r4 = 2131624449(0x7f0e0201, float:1.8876078E38)
            java.lang.String r5 = "CallMessageIncomingDeclined"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            goto L_0x1a8e
        L_0x1a85:
            r4 = 2131624448(0x7f0e0200, float:1.8876076E38)
            java.lang.String r5 = "CallMessageIncoming"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
        L_0x1a8e:
            int r5 = r3.duration
            if (r5 <= 0) goto L_0x1aac
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            java.lang.String r2 = ", "
            r5.append(r2)
            int r2 = r3.duration
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatCallDuration(r2)
            r5.append(r2)
            java.lang.String r2 = r5.toString()
        L_0x1aac:
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint
            float r12 = (float) r0
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r6 = android.text.TextUtils.ellipsize(r4, r5, r12, r6)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r8 = r0 + r4
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            r4 = 0
            r5 = r3
            r15 = r12
            r12 = r4
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)
            r1.titleLayout = r3
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r30 = android.text.TextUtils.ellipsize(r2, r4, r15, r5)
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r32 = r0 + r2
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r3
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            r1.docTitleLayout = r3
            r58.setMessageObjectInternal(r59)
            r0 = 1115815936(0x42820000, float:65.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = r1.namesOffset
            int r0 = r0 + r2
            r1.totalHeight = r0
            boolean r0 = r1.drawPinnedTop
            if (r0 == 0) goto L_0x2242
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r60)
            int r2 = r2 - r0
            r1.namesOffset = r2
            goto L_0x2242
        L_0x1b09:
            r2 = 12
            if (r0 != r2) goto L_0x1d1d
            r1.drawName = r13
            r2 = 1
            r1.drawForwardedName = r2
            r1.drawPhotoImage = r2
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r2 = 1102053376(0x41b00000, float:22.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setRoundRadius(r2)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1b4e
            int r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x1b3a
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x1b3a
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x1b3a
            goto L_0x1b3c
        L_0x1b3a:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x1b3c:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r2
            r2 = 1132920832(0x43870000, float:270.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = java.lang.Math.min(r0, r2)
            r1.backgroundWidth = r0
            goto L_0x1b76
        L_0x1b4e:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x1b63
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x1b63
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x1b63
            goto L_0x1b65
        L_0x1b63:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x1b65:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r2
            r2 = 1132920832(0x43870000, float:270.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = java.lang.Math.min(r0, r2)
            r1.backgroundWidth = r0
        L_0x1b76:
            int r0 = r1.backgroundWidth
            r2 = 1106771968(0x41var_, float:31.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r1.availableTimeWidth = r0
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.user_id
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            int r2 = r58.getMaxNameWidth()
            r3 = 1117782016(0x42a00000, float:80.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            if (r2 >= 0) goto L_0x1ba6
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
        L_0x1ba6:
            r9 = r2
            if (r0 == 0) goto L_0x1bae
            org.telegram.ui.Components.AvatarDrawable r2 = r1.contactAvatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC.User) r0)
        L_0x1bae:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUser(r0, r13)
            if (r0 == 0) goto L_0x1bba
            org.telegram.ui.Components.AvatarDrawable r4 = r1.contactAvatarDrawable
        L_0x1bb8:
            r5 = r4
            goto L_0x1bc3
        L_0x1bba:
            android.graphics.drawable.Drawable[] r4 = org.telegram.ui.ActionBar.Theme.chat_contactDrawable
            boolean r5 = r59.isOutOwner()
            r4 = r4[r5]
            goto L_0x1bb8
        L_0x1bc3:
            r6 = 0
            r8 = 0
            java.lang.String r4 = "50_50"
            r7 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8)
            java.lang.CharSequence r2 = r14.vCardData
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x1bdf
            java.lang.CharSequence r0 = r14.vCardData
            r2 = 1
            r1.drawInstantView = r2
            r2 = 5
            r1.drawInstantViewType = r2
        L_0x1bdc:
            r30 = r0
            goto L_0x1CLASSNAME
        L_0x1bdf:
            if (r0 == 0) goto L_0x1CLASSNAME
            java.lang.String r2 = r0.phone
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x1CLASSNAME
            org.telegram.PhoneFormat.PhoneFormat r2 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "+"
            r3.append(r4)
            java.lang.String r0 = r0.phone
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            java.lang.String r0 = r2.format(r0)
            goto L_0x1bdc
        L_0x1CLASSNAME:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r0 = r0.phone_number
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x1c1a
            org.telegram.PhoneFormat.PhoneFormat r2 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.String r0 = r2.format(r0)
            goto L_0x1bdc
        L_0x1c1a:
            r0 = 2131625827(0x7f0e0763, float:1.8878873E38)
            java.lang.String r2 = "NumberUnknown"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x1bdc
        L_0x1CLASSNAME:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r2 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r2, r0)
            r2 = 10
            r3 = 32
            java.lang.String r0 = r0.replace(r2, r3)
            int r2 = r0.length()
            if (r2 != 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r0 = r0.phone_number
            if (r0 != 0) goto L_0x1CLASSNAME
            java.lang.String r0 = ""
        L_0x1CLASSNAME:
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint
            float r4 = (float) r9
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r32 = android.text.TextUtils.ellipsize(r0, r3, r4, r5)
            android.text.TextPaint r33 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint
            r0 = 1082130432(0x40800000, float:4.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r34 = r9 + r0
            android.text.Layout$Alignment r35 = android.text.Layout.Alignment.ALIGN_NORMAL
            r36 = 1065353216(0x3var_, float:1.0)
            r37 = 0
            r38 = 0
            r31 = r2
            r31.<init>(r32, r33, r34, r35, r36, r37, r38)
            r1.titleLayout = r2
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r32 = r9 + r2
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL
            r34 = 1065353216(0x3var_, float:1.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r60)
            float r2 = (float) r2
            r36 = 0
            r29 = r0
            r35 = r2
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            r1.docTitleLayout = r0
            r58.setMessageObjectInternal(r59)
            boolean r0 = r1.drawForwardedName
            if (r0 == 0) goto L_0x1cab
            boolean r0 = r59.needDrawForwarded()
            if (r0 == 0) goto L_0x1cab
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r1.currentPosition
            if (r0 == 0) goto L_0x1c9f
            byte r0 = r0.minY
            if (r0 != 0) goto L_0x1cab
        L_0x1c9f:
            int r0 = r1.namesOffset
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.namesOffset = r0
            goto L_0x1cc0
        L_0x1cab:
            boolean r0 = r1.drawNameLayout
            if (r0 == 0) goto L_0x1cc0
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            int r0 = r0.reply_to_msg_id
            if (r0 != 0) goto L_0x1cc0
            int r0 = r1.namesOffset
            r2 = 1088421888(0x40e00000, float:7.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.namesOffset = r0
        L_0x1cc0:
            r0 = 1113325568(0x425CLASSNAME, float:55.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = r1.namesOffset
            int r0 = r0 + r2
            android.text.StaticLayout r2 = r1.docTitleLayout
            int r2 = r2.getHeight()
            int r0 = r0 + r2
            r1.totalHeight = r0
            boolean r0 = r1.drawPinnedTop
            if (r0 == 0) goto L_0x1cdf
            int r0 = r1.namesOffset
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r60)
            int r0 = r0 - r2
            r1.namesOffset = r0
        L_0x1cdf:
            boolean r0 = r1.drawInstantView
            if (r0 == 0) goto L_0x1ce8
            r58.createInstantViewButton()
            goto L_0x2242
        L_0x1ce8:
            android.text.StaticLayout r0 = r1.docTitleLayout
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x2242
            int r0 = r1.backgroundWidth
            r2 = 1121714176(0x42dCLASSNAME, float:110.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.text.StaticLayout r2 = r1.docTitleLayout
            int r3 = r2.getLineCount()
            r4 = 1
            int r3 = r3 - r4
            float r2 = r2.getLineWidth(r3)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            int r0 = r0 - r2
            int r2 = r1.timeWidth
            if (r0 >= r2) goto L_0x2242
            int r0 = r1.totalHeight
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.totalHeight = r0
            goto L_0x2242
        L_0x1d1d:
            r15 = 2
            if (r0 != r15) goto L_0x1d9a
            r2 = 1
            r1.drawForwardedName = r2
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1d52
            int r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x1d3e
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x1d3e
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x1d3e
            goto L_0x1d40
        L_0x1d3e:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x1d40:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r2
            r2 = 1132920832(0x43870000, float:270.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = java.lang.Math.min(r0, r2)
            r1.backgroundWidth = r0
            goto L_0x1d7a
        L_0x1d52:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x1d67
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x1d67
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x1d67
            goto L_0x1d69
        L_0x1d67:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x1d69:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r2
            r2 = 1132920832(0x43870000, float:270.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = java.lang.Math.min(r0, r2)
            r1.backgroundWidth = r0
        L_0x1d7a:
            int r0 = r1.backgroundWidth
            r1.createDocumentLayout(r0, r14)
            r58.setMessageObjectInternal(r59)
            r0 = 1116471296(0x428CLASSNAME, float:70.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = r1.namesOffset
            int r0 = r0 + r2
            r1.totalHeight = r0
            boolean r0 = r1.drawPinnedTop
            if (r0 == 0) goto L_0x2242
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r60)
            int r2 = r2 - r0
            r1.namesOffset = r2
            goto L_0x2242
        L_0x1d9a:
            r2 = 14
            if (r0 != r2) goto L_0x1e15
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x1dcd
            int r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x1db9
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x1db9
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x1db9
            goto L_0x1dbb
        L_0x1db9:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x1dbb:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r2
            r2 = 1132920832(0x43870000, float:270.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = java.lang.Math.min(r0, r2)
            r1.backgroundWidth = r0
            goto L_0x1df5
        L_0x1dcd:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x1de2
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x1de2
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x1de2
            goto L_0x1de4
        L_0x1de2:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x1de4:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r2
            r2 = 1132920832(0x43870000, float:270.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = java.lang.Math.min(r0, r2)
            r1.backgroundWidth = r0
        L_0x1df5:
            int r0 = r1.backgroundWidth
            r1.createDocumentLayout(r0, r14)
            r58.setMessageObjectInternal(r59)
            r0 = 1118044160(0x42a40000, float:82.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = r1.namesOffset
            int r0 = r0 + r2
            r1.totalHeight = r0
            boolean r0 = r1.drawPinnedTop
            if (r0 == 0) goto L_0x2242
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r60)
            int r2 = r2 - r0
            r1.namesOffset = r2
            goto L_0x2242
        L_0x1e15:
            r2 = 17
            if (r0 != r2) goto L_0x224a
            r58.createSelectorDrawable()
            r2 = 1
            r1.drawName = r2
            r1.drawForwardedName = r2
            r1.drawPhotoImage = r13
            r0 = 1140457472(0x43fa0000, float:500.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = r59.getMaxMessageTextWidth()
            int r0 = java.lang.Math.min(r0, r2)
            r1.availableTimeWidth = r0
            r2 = 1106771968(0x41var_, float:31.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r2 + r0
            r1.backgroundWidth = r2
            r2 = 1123024896(0x42var_, float:120.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.availableTimeWidth = r2
            r58.measureTime(r59)
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$TL_poll r3 = r2.poll
            boolean r3 = r3.closed
            r1.pollClosed = r3
            boolean r3 = r59.isVoted()
            r1.pollVoted = r3
            android.text.StaticLayout r3 = new android.text.StaticLayout
            org.telegram.tgnet.TLRPC$TL_poll r4 = r2.poll
            java.lang.String r4 = r4.question
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            java.lang.CharSequence r5 = org.telegram.messenger.Emoji.replaceEmoji(r4, r5, r6, r13)
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r7 = r0 + r4
            android.text.Layout$Alignment r8 = android.text.Layout.Alignment.ALIGN_NORMAL
            r9 = 1065353216(0x3var_, float:1.0)
            r10 = 0
            r11 = 0
            r4 = r3
            r4.<init>(r5, r6, r7, r8, r9, r10, r11)
            r1.titleLayout = r3
            android.text.StaticLayout r3 = r1.titleLayout
            if (r3 == 0) goto L_0x1e9e
            int r3 = r3.getLineCount()
            r4 = 0
        L_0x1e8c:
            if (r4 >= r3) goto L_0x1e9e
            android.text.StaticLayout r5 = r1.titleLayout
            float r5 = r5.getLineLeft(r4)
            r12 = 0
            int r5 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r5 == 0) goto L_0x1e9b
            r3 = 1
            goto L_0x1ea0
        L_0x1e9b:
            int r4 = r4 + 1
            goto L_0x1e8c
        L_0x1e9e:
            r12 = 0
            r3 = 0
        L_0x1ea0:
            android.text.StaticLayout r11 = new android.text.StaticLayout
            org.telegram.tgnet.TLRPC$TL_poll r4 = r2.poll
            boolean r4 = r4.closed
            if (r4 == 0) goto L_0x1eae
            r4 = 2131625146(0x7f0e04ba, float:1.8877492E38)
            java.lang.String r5 = "FinalResults"
            goto L_0x1eb3
        L_0x1eae:
            r4 = 2131624181(0x7f0e00f5, float:1.8875534E38)
            java.lang.String r5 = "AnonymousPoll"
        L_0x1eb3:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.chat_timePaint
            float r10 = (float) r0
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r5 = android.text.TextUtils.ellipsize(r4, r5, r10, r6)
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.chat_timePaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r7 = r0 + r4
            android.text.Layout$Alignment r8 = android.text.Layout.Alignment.ALIGN_NORMAL
            r9 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            r4 = r11
            r23 = r10
            r10 = r21
            r12 = r11
            r11 = r22
            r4.<init>(r5, r6, r7, r8, r9, r10, r11)
            r1.docTitleLayout = r12
            android.text.StaticLayout r4 = r1.docTitleLayout
            if (r4 == 0) goto L_0x1var_
            int r4 = r4.getLineCount()
            if (r4 <= 0) goto L_0x1var_
            if (r3 == 0) goto L_0x1efe
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x1efe
            android.text.StaticLayout r3 = r1.docTitleLayout
            float r3 = r3.getLineWidth(r13)
            float r10 = r23 - r3
            double r3 = (double) r10
            double r3 = java.lang.Math.ceil(r3)
            int r3 = (int) r3
            r1.docTitleOffsetX = r3
            goto L_0x1var_
        L_0x1efe:
            if (r3 != 0) goto L_0x1var_
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x1var_
            android.text.StaticLayout r3 = r1.docTitleLayout
            float r3 = r3.getLineLeft(r13)
            double r3 = (double) r3
            double r3 = java.lang.Math.ceil(r3)
            int r3 = (int) r3
            int r3 = -r3
            r1.docTitleOffsetX = r3
        L_0x1var_:
            int r3 = r1.timeWidth
            int r3 = r0 - r3
            boolean r4 = r59.isOutOwner()
            if (r4 == 0) goto L_0x1var_
            r4 = 1105199104(0x41e00000, float:28.0)
            goto L_0x1var_
        L_0x1var_:
            r4 = 1090519040(0x41000000, float:8.0)
        L_0x1var_:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r8 = r3 - r4
            android.text.StaticLayout r3 = new android.text.StaticLayout
            org.telegram.tgnet.TLRPC$TL_pollResults r4 = r2.results
            int r4 = r4.total_voters
            if (r4 != 0) goto L_0x1f3a
            r4 = 2131625677(0x7f0e06cd, float:1.8878569E38)
            java.lang.String r5 = "NoVotes"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            goto L_0x1var_
        L_0x1f3a:
            java.lang.String r5 = "Vote"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r4)
        L_0x1var_:
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.chat_livePaint
            float r6 = (float) r8
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r6 = android.text.TextUtils.ellipsize(r4, r5, r6, r7)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.chat_livePaint
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            r12 = 0
            r5 = r3
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)
            r1.infoLayout = r3
            android.text.StaticLayout r3 = r1.infoLayout
            if (r3 == 0) goto L_0x1f6a
            int r3 = r3.getLineCount()
            if (r3 <= 0) goto L_0x1f6a
            android.text.StaticLayout r3 = r1.infoLayout
            float r3 = r3.getLineLeft(r13)
            float r3 = -r3
            double r3 = (double) r3
            goto L_0x1f6c
        L_0x1f6a:
            r3 = 0
        L_0x1f6c:
            double r3 = java.lang.Math.ceil(r3)
            int r3 = (int) r3
            r1.infoX = r3
            org.telegram.tgnet.TLRPC$TL_poll r3 = r2.poll
            r1.lastPoll = r3
            org.telegram.tgnet.TLRPC$TL_pollResults r3 = r2.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r4 = r3.results
            r1.lastPollResults = r4
            int r3 = r3.total_voters
            r1.lastPollResultsVoters = r3
            boolean r3 = r1.animatePollAnswer
            if (r3 != 0) goto L_0x1f8e
            boolean r3 = r1.pollVoteInProgress
            if (r3 == 0) goto L_0x1f8e
            r12 = 3
            r1.performHapticFeedback(r12, r15)
            goto L_0x1f8f
        L_0x1f8e:
            r12 = 3
        L_0x1f8f:
            boolean r3 = r1.attachedToWindow
            if (r3 == 0) goto L_0x1f9d
            boolean r3 = r1.pollVoteInProgress
            if (r3 != 0) goto L_0x1f9b
            boolean r3 = r1.pollUnvoteInProgress
            if (r3 == 0) goto L_0x1f9d
        L_0x1f9b:
            r3 = 1
            goto L_0x1f9e
        L_0x1f9d:
            r3 = 0
        L_0x1f9e:
            r1.animatePollAnswer = r3
            r1.animatePollAnswerAlpha = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$PollButton> r4 = r1.pollButtons
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x201f
            java.util.ArrayList r4 = new java.util.ArrayList
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$PollButton> r5 = r1.pollButtons
            r4.<init>(r5)
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$PollButton> r5 = r1.pollButtons
            r5.clear()
            boolean r5 = r1.animatePollAnswer
            if (r5 != 0) goto L_0x1fd0
            boolean r5 = r1.attachedToWindow
            if (r5 == 0) goto L_0x1fcd
            boolean r5 = r1.pollVoted
            if (r5 != 0) goto L_0x1fcb
            boolean r5 = r1.pollClosed
            if (r5 == 0) goto L_0x1fcd
        L_0x1fcb:
            r5 = 1
            goto L_0x1fce
        L_0x1fcd:
            r5 = 0
        L_0x1fce:
            r1.animatePollAnswer = r5
        L_0x1fd0:
            float r5 = r1.pollAnimationProgress
            r6 = 0
            int r7 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            r11 = 1065353216(0x3var_, float:1.0)
            if (r7 <= 0) goto L_0x2022
            int r5 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r5 >= 0) goto L_0x2022
            int r5 = r4.size()
            r6 = 0
        L_0x1fe2:
            if (r6 >= r5) goto L_0x2022
            java.lang.Object r7 = r4.get(r6)
            org.telegram.ui.Cells.ChatMessageCell$PollButton r7 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r7
            int r8 = r7.prevPercent
            float r8 = (float) r8
            int r9 = r7.percent
            int r10 = r7.prevPercent
            int r9 = r9 - r10
            float r9 = (float) r9
            float r10 = r1.pollAnimationProgress
            float r9 = r9 * r10
            float r8 = r8 + r9
            double r8 = (double) r8
            double r8 = java.lang.Math.ceil(r8)
            int r8 = (int) r8
            int unused = r7.percent = r8
            float r8 = r7.prevPercentProgress
            float r9 = r7.percentProgress
            float r10 = r7.prevPercentProgress
            float r9 = r9 - r10
            float r10 = r1.pollAnimationProgress
            float r9 = r9 * r10
            float r8 = r8 + r9
            float unused = r7.percentProgress = r8
            int r6 = r6 + 1
            goto L_0x1fe2
        L_0x201f:
            r11 = 1065353216(0x3var_, float:1.0)
            r4 = 0
        L_0x2022:
            boolean r5 = r1.animatePollAnswer
            if (r5 == 0) goto L_0x2028
            r5 = 0
            goto L_0x202a
        L_0x2028:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x202a:
            r1.pollAnimationProgress = r5
            boolean r5 = r1.animatePollAnswerAlpha
            if (r5 != 0) goto L_0x2042
            r1.pollVoteInProgress = r13
            r10 = -1
            r1.pollVoteInProgressNum = r10
            int r5 = r1.currentAccount
            org.telegram.messenger.SendMessagesHelper r5 = org.telegram.messenger.SendMessagesHelper.getInstance(r5)
            org.telegram.messenger.MessageObject r6 = r1.currentMessageObject
            byte[] r5 = r5.isSendingVote(r6)
            goto L_0x2044
        L_0x2042:
            r10 = -1
            r5 = 0
        L_0x2044:
            android.text.StaticLayout r6 = r1.titleLayout
            if (r6 == 0) goto L_0x204d
            int r6 = r6.getHeight()
            goto L_0x204e
        L_0x204d:
            r6 = 0
        L_0x204e:
            r7 = 100
            org.telegram.tgnet.TLRPC$TL_poll r8 = r2.poll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswer> r8 = r8.answers
            int r8 = r8.size()
            r12 = r5
            r7 = r6
            r5 = 0
            r6 = 0
            r9 = 100
            r10 = 0
            r15 = 0
        L_0x2060:
            if (r5 >= r8) goto L_0x21c6
            org.telegram.ui.Cells.ChatMessageCell$PollButton r11 = new org.telegram.ui.Cells.ChatMessageCell$PollButton
            r13 = 0
            r11.<init>()
            org.telegram.tgnet.TLRPC$TL_poll r13 = r2.poll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswer> r13 = r13.answers
            java.lang.Object r13 = r13.get(r5)
            org.telegram.tgnet.TLRPC$TL_pollAnswer r13 = (org.telegram.tgnet.TLRPC.TL_pollAnswer) r13
            org.telegram.tgnet.TLRPC.TL_pollAnswer unused = r11.answer = r13
            android.text.StaticLayout r13 = new android.text.StaticLayout
            r61 = r8
            org.telegram.tgnet.TLRPC$TL_pollAnswer r8 = r11.answer
            java.lang.String r8 = r8.text
            android.text.TextPaint r21 = org.telegram.ui.ActionBar.Theme.chat_audioPerformerPaint
            android.graphics.Paint$FontMetricsInt r14 = r21.getFontMetricsInt()
            r21 = 1097859072(0x41700000, float:15.0)
            r62 = r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r21 = r5
            r5 = 0
            java.lang.CharSequence r30 = org.telegram.messenger.Emoji.replaceEmoji(r8, r14, r6, r5)
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_audioPerformerPaint
            r5 = 1107558400(0x42040000, float:33.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r32 = r0 - r5
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r13
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            android.text.StaticLayout unused = r11.title = r13
            r5 = 1112539136(0x42500000, float:52.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r5 + r7
            int unused = r11.y = r5
            android.text.StaticLayout r5 = r11.title
            int r5 = r5.getHeight()
            int unused = r11.height = r5
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$PollButton> r5 = r1.pollButtons
            r5.add(r11)
            r3.add(r11)
            int r5 = r11.height
            r6 = 1104150528(0x41d00000, float:26.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r6
            int r7 = r7 + r5
            org.telegram.tgnet.TLRPC$TL_pollResults r5 = r2.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r5 = r5.results
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x2165
            org.telegram.tgnet.TLRPC$TL_pollResults r5 = r2.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r5 = r5.results
            int r5 = r5.size()
            r6 = 0
        L_0x20ea:
            if (r6 >= r5) goto L_0x2165
            org.telegram.tgnet.TLRPC$TL_pollResults r8 = r2.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r8 = r8.results
            java.lang.Object r8 = r8.get(r6)
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r8 = (org.telegram.tgnet.TLRPC.TL_pollAnswerVoters) r8
            org.telegram.tgnet.TLRPC$TL_pollAnswer r13 = r11.answer
            byte[] r13 = r13.option
            byte[] r14 = r8.option
            boolean r13 = java.util.Arrays.equals(r13, r14)
            if (r13 == 0) goto L_0x2162
            boolean r5 = r1.pollVoted
            if (r5 != 0) goto L_0x210c
            boolean r5 = r1.pollClosed
            if (r5 == 0) goto L_0x2134
        L_0x210c:
            org.telegram.tgnet.TLRPC$TL_pollResults r5 = r2.results
            int r5 = r5.total_voters
            if (r5 <= 0) goto L_0x2134
            r6 = 1120403456(0x42CLASSNAME, float:100.0)
            int r8 = r8.voters
            float r8 = (float) r8
            float r5 = (float) r5
            float r8 = r8 / r5
            float r8 = r8 * r6
            float unused = r11.decimal = r8
            float r5 = r11.decimal
            int r5 = (int) r5
            int unused = r11.percent = r5
            float r5 = r11.decimal
            int r6 = r11.percent
            float r6 = (float) r6
            float r5 = r5 - r6
            float unused = r11.decimal = r5
            goto L_0x213c
        L_0x2134:
            r5 = 0
            int unused = r11.percent = r5
            r5 = 0
            float unused = r11.decimal = r5
        L_0x213c:
            if (r15 != 0) goto L_0x2143
            int r15 = r11.percent
            goto L_0x2151
        L_0x2143:
            int r5 = r11.percent
            if (r5 == 0) goto L_0x2151
            int r5 = r11.percent
            if (r15 == r5) goto L_0x2151
            r6 = 1
            goto L_0x2153
        L_0x2151:
            r6 = r62
        L_0x2153:
            int r5 = r11.percent
            int r9 = r9 - r5
            int r5 = r11.percent
            int r5 = java.lang.Math.max(r5, r10)
            r10 = r5
            goto L_0x2167
        L_0x2162:
            int r6 = r6 + 1
            goto L_0x20ea
        L_0x2165:
            r6 = r62
        L_0x2167:
            if (r4 == 0) goto L_0x219e
            int r5 = r4.size()
            r8 = 0
        L_0x216e:
            if (r8 >= r5) goto L_0x219e
            java.lang.Object r13 = r4.get(r8)
            org.telegram.ui.Cells.ChatMessageCell$PollButton r13 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r13
            org.telegram.tgnet.TLRPC$TL_pollAnswer r14 = r11.answer
            byte[] r14 = r14.option
            r22 = r0
            org.telegram.tgnet.TLRPC$TL_pollAnswer r0 = r13.answer
            byte[] r0 = r0.option
            boolean r0 = java.util.Arrays.equals(r14, r0)
            if (r0 == 0) goto L_0x2199
            int r0 = r13.percent
            int unused = r11.prevPercent = r0
            float r0 = r13.percentProgress
            float unused = r11.prevPercentProgress = r0
            goto L_0x21a0
        L_0x2199:
            int r8 = r8 + 1
            r0 = r22
            goto L_0x216e
        L_0x219e:
            r22 = r0
        L_0x21a0:
            if (r12 == 0) goto L_0x21b7
            org.telegram.tgnet.TLRPC$TL_pollAnswer r0 = r11.answer
            byte[] r0 = r0.option
            boolean r0 = java.util.Arrays.equals(r0, r12)
            if (r0 == 0) goto L_0x21b7
            r13 = r21
            r1.pollVoteInProgressNum = r13
            r5 = 1
            r1.pollVoteInProgress = r5
            r12 = 0
            goto L_0x21b9
        L_0x21b7:
            r13 = r21
        L_0x21b9:
            int r5 = r13 + 1
            r14 = r59
            r8 = r61
            r0 = r22
            r11 = 1065353216(0x3var_, float:1.0)
            r13 = 0
            goto L_0x2060
        L_0x21c6:
            r62 = r6
            if (r62 == 0) goto L_0x21ee
            if (r9 == 0) goto L_0x21ee
            org.telegram.ui.Cells.-$$Lambda$ChatMessageCell$hzMG4njhE1StYhHOT542pSi6Cf0 r0 = org.telegram.ui.Cells.$$Lambda$ChatMessageCell$hzMG4njhE1StYhHOT542pSi6Cf0.INSTANCE
            java.util.Collections.sort(r3, r0)
            int r0 = r3.size()
            int r0 = java.lang.Math.min(r9, r0)
            r2 = 0
        L_0x21da:
            if (r2 >= r0) goto L_0x21ee
            java.lang.Object r4 = r3.get(r2)
            org.telegram.ui.Cells.ChatMessageCell$PollButton r4 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r4
            int r5 = r4.percent
            r6 = 1
            int r5 = r5 + r6
            int unused = r4.percent = r5
            int r2 = r2 + 1
            goto L_0x21da
        L_0x21ee:
            int r0 = r1.backgroundWidth
            r2 = 1117257728(0x42980000, float:76.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$PollButton> r2 = r1.pollButtons
            int r2 = r2.size()
            r3 = 0
        L_0x21fe:
            if (r3 >= r2) goto L_0x2226
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$PollButton> r4 = r1.pollButtons
            java.lang.Object r4 = r4.get(r3)
            org.telegram.ui.Cells.ChatMessageCell$PollButton r4 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r4
            r5 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = (float) r0
            float r5 = r5 / r6
            if (r10 == 0) goto L_0x221b
            int r6 = r4.percent
            float r6 = (float) r6
            float r8 = (float) r10
            float r6 = r6 / r8
            goto L_0x221c
        L_0x221b:
            r6 = 0
        L_0x221c:
            float r5 = java.lang.Math.max(r5, r6)
            float unused = r4.percentProgress = r5
            int r3 = r3 + 1
            goto L_0x21fe
        L_0x2226:
            r58.setMessageObjectInternal(r59)
            r0 = 1116864512(0x42920000, float:73.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = r1.namesOffset
            int r0 = r0 + r2
            int r0 = r0 + r7
            r1.totalHeight = r0
            boolean r0 = r1.drawPinnedTop
            if (r0 == 0) goto L_0x2242
            r3 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r0
            r1.namesOffset = r2
        L_0x2242:
            r14 = r59
            r9 = 0
            r15 = 0
            r24 = 1065353216(0x3var_, float:1.0)
            goto L_0x3889
        L_0x224a:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x2258
            boolean r0 = r59.isAnyKindOfSticker()
            if (r0 != 0) goto L_0x2258
            r0 = 1
            goto L_0x2259
        L_0x2258:
            r0 = 0
        L_0x2259:
            r1.drawForwardedName = r0
            int r0 = r14.type
            r2 = 9
            if (r0 == r2) goto L_0x2263
            r0 = 1
            goto L_0x2264
        L_0x2263:
            r0 = 0
        L_0x2264:
            r1.mediaBackground = r0
            r2 = 1
            r1.drawImageButton = r2
            r1.drawPhotoImage = r2
            float r0 = r14.gifState
            int r0 = (r0 > r20 ? 1 : (r0 == r20 ? 0 : -1))
            if (r0 == 0) goto L_0x2283
            boolean r0 = org.telegram.messenger.SharedConfig.autoplayGifs
            if (r0 != 0) goto L_0x2283
            int r0 = r14.type
            r7 = 8
            if (r0 == r7) goto L_0x227e
            r2 = 5
            if (r0 != r2) goto L_0x2285
        L_0x227e:
            r11 = 1065353216(0x3var_, float:1.0)
            r14.gifState = r11
            goto L_0x2287
        L_0x2283:
            r7 = 8
        L_0x2285:
            r11 = 1065353216(0x3var_, float:1.0)
        L_0x2287:
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r2 = 1
            r0.setAllowDecodeSingleFrame(r2)
            boolean r0 = r59.isVideo()
            if (r0 == 0) goto L_0x2299
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r0.setAllowStartAnimation(r2)
            goto L_0x22c8
        L_0x2299:
            boolean r0 = r59.isRoundVideo()
            if (r0 == 0) goto L_0x22b9
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r0 = r0.getPlayingMessageObject()
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            if (r0 == 0) goto L_0x22b4
            boolean r0 = r0.isRoundVideo()
            if (r0 != 0) goto L_0x22b2
            goto L_0x22b4
        L_0x22b2:
            r0 = 0
            goto L_0x22b5
        L_0x22b4:
            r0 = 1
        L_0x22b5:
            r2.setAllowStartAnimation(r0)
            goto L_0x22c8
        L_0x22b9:
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            float r2 = r14.gifState
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 != 0) goto L_0x22c4
            r2 = 1
            goto L_0x22c5
        L_0x22c4:
            r2 = 0
        L_0x22c5:
            r0.setAllowStartAnimation(r2)
        L_0x22c8:
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            boolean r2 = r59.needDrawBluredPreview()
            r0.setForcePreview(r2)
            int r0 = r14.type
            r2 = 9
            if (r0 != r2) goto L_0x24e0
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2306
            int r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x22f2
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x22f2
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x22f2
            goto L_0x22f4
        L_0x22f2:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x22f4:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r2
            r2 = 1133903872(0x43960000, float:300.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = java.lang.Math.min(r0, r2)
            r1.backgroundWidth = r0
            goto L_0x232e
        L_0x2306:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x231b
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x231b
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x231b
            goto L_0x231d
        L_0x231b:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x231d:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r2
            r2 = 1133903872(0x43960000, float:300.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = java.lang.Math.min(r0, r2)
            r1.backgroundWidth = r0
        L_0x232e:
            boolean r0 = r58.checkNeedDrawShareButton(r59)
            if (r0 == 0) goto L_0x233f
            int r0 = r1.backgroundWidth
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r1.backgroundWidth = r0
        L_0x233f:
            int r0 = r1.backgroundWidth
            r2 = 1124728832(0x430a0000, float:138.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r0 - r2
            r1.createDocumentLayout(r2, r14)
            java.lang.CharSequence r0 = r14.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x23b6
            java.lang.CharSequence r0 = r14.caption     // Catch:{ Exception -> 0x23b0 }
            r1.currentCaption = r0     // Catch:{ Exception -> 0x23b0 }
            int r0 = r1.backgroundWidth     // Catch:{ Exception -> 0x23b0 }
            r3 = 1106771968(0x41var_, float:31.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x23b0 }
            int r0 = r0 - r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x23b0 }
            int r3 = r0 - r3
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x23ad }
            r4 = 24
            if (r0 < r4) goto L_0x2392
            java.lang.CharSequence r0 = r14.caption     // Catch:{ Exception -> 0x23ad }
            java.lang.CharSequence r4 = r14.caption     // Catch:{ Exception -> 0x23ad }
            int r4 = r4.length()     // Catch:{ Exception -> 0x23ad }
            android.text.TextPaint r5 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint     // Catch:{ Exception -> 0x23ad }
            r6 = 0
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r0, r6, r4, r5, r3)     // Catch:{ Exception -> 0x23ad }
            r4 = 1
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r4)     // Catch:{ Exception -> 0x23ad }
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r6)     // Catch:{ Exception -> 0x23ad }
            android.text.Layout$Alignment r4 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x23ad }
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r4)     // Catch:{ Exception -> 0x23ad }
            android.text.StaticLayout r0 = r0.build()     // Catch:{ Exception -> 0x23ad }
            r1.captionLayout = r0     // Catch:{ Exception -> 0x23ad }
            goto L_0x23ab
        L_0x2392:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x23ad }
            java.lang.CharSequence r4 = r14.caption     // Catch:{ Exception -> 0x23ad }
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint     // Catch:{ Exception -> 0x23ad }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x23ad }
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r30 = r4
            r32 = r3
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x23ad }
            r1.captionLayout = r0     // Catch:{ Exception -> 0x23ad }
        L_0x23ab:
            r12 = r3
            goto L_0x23b7
        L_0x23ad:
            r0 = move-exception
            r12 = r3
            goto L_0x23b2
        L_0x23b0:
            r0 = move-exception
            r12 = 0
        L_0x23b2:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x23b7
        L_0x23b6:
            r12 = 0
        L_0x23b7:
            android.text.StaticLayout r0 = r1.docTitleLayout
            if (r0 == 0) goto L_0x23ee
            int r0 = r0.getLineCount()
            r3 = 0
            r4 = 0
        L_0x23c1:
            if (r3 >= r0) goto L_0x23ef
            android.text.StaticLayout r5 = r1.docTitleLayout
            float r5 = r5.getLineWidth(r3)
            android.text.StaticLayout r6 = r1.docTitleLayout
            float r6 = r6.getLineLeft(r3)
            float r5 = r5 + r6
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            boolean r6 = r1.drawPhotoImage
            if (r6 == 0) goto L_0x23dd
            r6 = 52
            goto L_0x23df
        L_0x23dd:
            r6 = 22
        L_0x23df:
            int r6 = r6 + 86
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r6
            int r4 = java.lang.Math.max(r4, r5)
            int r3 = r3 + 1
            goto L_0x23c1
        L_0x23ee:
            r4 = 0
        L_0x23ef:
            android.text.StaticLayout r0 = r1.infoLayout
            if (r0 == 0) goto L_0x241e
            int r0 = r0.getLineCount()
            r3 = 0
        L_0x23f8:
            if (r3 >= r0) goto L_0x241e
            android.text.StaticLayout r5 = r1.infoLayout
            float r5 = r5.getLineWidth(r3)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            boolean r6 = r1.drawPhotoImage
            if (r6 == 0) goto L_0x240d
            r6 = 52
            goto L_0x240f
        L_0x240d:
            r6 = 22
        L_0x240f:
            int r6 = r6 + 86
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r6
            int r4 = java.lang.Math.max(r4, r5)
            int r3 = r3 + 1
            goto L_0x23f8
        L_0x241e:
            android.text.StaticLayout r0 = r1.captionLayout
            if (r0 == 0) goto L_0x244e
            int r0 = r0.getLineCount()
            r3 = 0
        L_0x2427:
            if (r3 >= r0) goto L_0x244e
            float r5 = (float) r12
            android.text.StaticLayout r6 = r1.captionLayout
            float r6 = r6.getLineWidth(r3)
            android.text.StaticLayout r8 = r1.captionLayout
            float r8 = r8.getLineLeft(r3)
            float r6 = r6 + r8
            float r5 = java.lang.Math.min(r5, r6)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            r6 = 1106771968(0x41var_, float:31.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r6
            if (r5 <= r4) goto L_0x244b
            r4 = r5
        L_0x244b:
            int r3 = r3 + 1
            goto L_0x2427
        L_0x244e:
            if (r4 <= 0) goto L_0x245a
            r1.backgroundWidth = r4
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = r4 - r0
        L_0x245a:
            boolean r0 = r1.drawPhotoImage
            if (r0 == 0) goto L_0x246b
            r0 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r3 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x2492
        L_0x246b:
            r0 = 1113587712(0x42600000, float:56.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r3 = 1113587712(0x42600000, float:56.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.text.StaticLayout r4 = r1.docTitleLayout
            if (r4 == 0) goto L_0x2492
            int r4 = r4.getLineCount()
            r5 = 1
            if (r4 <= r5) goto L_0x2492
            android.text.StaticLayout r4 = r1.docTitleLayout
            int r4 = r4.getLineCount()
            int r4 = r4 - r5
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 * r5
            int r3 = r3 + r4
        L_0x2492:
            r1.availableTimeWidth = r2
            boolean r2 = r1.drawPhotoImage
            if (r2 != 0) goto L_0x24d8
            java.lang.CharSequence r2 = r14.caption
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x24d8
            android.text.StaticLayout r2 = r1.infoLayout
            if (r2 == 0) goto L_0x24d8
            int r2 = r2.getLineCount()
            r58.measureTime(r59)
            int r4 = r1.backgroundWidth
            r5 = 1123287040(0x42var_, float:122.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            android.text.StaticLayout r5 = r1.infoLayout
            r6 = 0
            float r5 = r5.getLineWidth(r6)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r4 = r4 - r5
            int r5 = r1.timeWidth
            if (r4 >= r5) goto L_0x24ce
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
        L_0x24cc:
            int r3 = r3 + r2
            goto L_0x24d8
        L_0x24ce:
            r4 = 1
            if (r2 != r4) goto L_0x24d8
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            goto L_0x24cc
        L_0x24d8:
            r12 = 0
            r15 = 0
            r24 = 1065353216(0x3var_, float:1.0)
            r56 = 0
            goto L_0x378f
        L_0x24e0:
            r2 = 4
            if (r0 != r2) goto L_0x2a4a
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$GeoPoint r0 = r0.geo
            double r2 = r0.lat
            double r5 = r0._long
            long r8 = r59.getDialogId()
            int r9 = (int) r8
            if (r9 != 0) goto L_0x2506
            int r8 = org.telegram.messenger.SharedConfig.mapPreviewType
            if (r8 != 0) goto L_0x24f9
            goto L_0x2506
        L_0x24f9:
            r9 = 1
            if (r8 != r9) goto L_0x2500
            r13 = 4
            r38 = 4
            goto L_0x2508
        L_0x2500:
            r9 = 3
            if (r8 != r9) goto L_0x2506
            r38 = 1
            goto L_0x2508
        L_0x2506:
            r38 = -1
        L_0x2508:
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r9 == 0) goto L_0x275b
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r8 == 0) goto L_0x2540
            int r8 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            boolean r9 = r1.isChat
            if (r9 == 0) goto L_0x252b
            boolean r9 = r59.needDrawAvatar()
            if (r9 == 0) goto L_0x252b
            boolean r9 = r59.isOutOwner()
            if (r9 != 0) goto L_0x252b
            goto L_0x252d
        L_0x252b:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x252d:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r8 = r8 - r4
            r4 = 1133543424(0x43908000, float:289.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = java.lang.Math.min(r8, r4)
            r1.backgroundWidth = r4
            goto L_0x2569
        L_0x2540:
            android.graphics.Point r8 = org.telegram.messenger.AndroidUtilities.displaySize
            int r8 = r8.x
            boolean r9 = r1.isChat
            if (r9 == 0) goto L_0x2555
            boolean r9 = r59.needDrawAvatar()
            if (r9 == 0) goto L_0x2555
            boolean r9 = r59.isOutOwner()
            if (r9 != 0) goto L_0x2555
            goto L_0x2557
        L_0x2555:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x2557:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r8 = r8 - r4
            r4 = 1133543424(0x43908000, float:289.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = java.lang.Math.min(r8, r4)
            r1.backgroundWidth = r4
        L_0x2569:
            int r4 = r1.backgroundWidth
            r8 = 1082130432(0x40800000, float:4.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = r4 - r8
            r1.backgroundWidth = r4
            boolean r4 = r58.checkNeedDrawShareButton(r59)
            if (r4 == 0) goto L_0x2585
            int r4 = r1.backgroundWidth
            r8 = 1101004800(0x41a00000, float:20.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = r4 - r8
            r1.backgroundWidth = r4
        L_0x2585:
            int r4 = r1.backgroundWidth
            r8 = 1108606976(0x42140000, float:37.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = r4 - r8
            r1.availableTimeWidth = r4
            r8 = 1113063424(0x42580000, float:54.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = r4 - r8
            int r8 = r1.backgroundWidth
            r9 = 1099431936(0x41880000, float:17.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            r9 = 1128464384(0x43430000, float:195.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r10 = 268435456(0x10000000, float:2.5243549E-29)
            double r12 = (double) r10
            r22 = 4614256656552045848(0x400921fb54442d18, double:3.NUM)
            java.lang.Double.isNaN(r12)
            double r22 = r12 / r22
            r24 = 4607182418800017408(0x3ffNUM, double:1.0)
            r29 = 4614256656552045848(0x400921fb54442d18, double:3.NUM)
            double r2 = r2 * r29
            r29 = 4640537203540230144(0xNUM, double:180.0)
            double r2 = r2 / r29
            double r29 = java.lang.Math.sin(r2)
            double r29 = r29 + r24
            double r2 = java.lang.Math.sin(r2)
            double r24 = r24 - r2
            double r29 = r29 / r24
            double r2 = java.lang.Math.log(r29)
            double r2 = r2 * r22
            r24 = 4611686018427387904(0xNUM, double:2.0)
            double r2 = r2 / r24
            java.lang.Double.isNaN(r12)
            double r2 = r12 - r2
            long r2 = java.lang.Math.round(r2)
            r10 = 1092930765(0x4124cccd, float:10.3)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r15 = 6
            int r10 = r10 << r15
            r62 = r8
            long r7 = (long) r10
            long r2 = r2 - r7
            double r2 = (double) r2
            r7 = 4609753056924675352(0x3fvar_fb54442d18, double:1.NUM)
            java.lang.Double.isNaN(r2)
            java.lang.Double.isNaN(r12)
            double r2 = r2 - r12
            double r2 = r2 / r22
            double r2 = java.lang.Math.exp(r2)
            double r2 = java.lang.Math.atan(r2)
            double r2 = r2 * r24
            double r7 = r7 - r2
            r2 = 4640537203540230144(0xNUM, double:180.0)
            double r7 = r7 * r2
            r2 = 4614256656552045848(0x400921fb54442d18, double:3.NUM)
            double r2 = r7 / r2
            int r7 = r1.currentAccount
            r8 = r62
            float r10 = (float) r8
            float r12 = org.telegram.messenger.AndroidUtilities.density
            float r13 = r10 / r12
            int r13 = (int) r13
            float r15 = (float) r9
            float r12 = r15 / r12
            int r12 = (int) r12
            r36 = 0
            r37 = 15
            r29 = r7
            r30 = r2
            r32 = r5
            r34 = r13
            r35 = r12
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r29, r30, r32, r34, r35, r36, r37, r38)
            r1.currentUrl = r7
            long r12 = r0.access_hash
            float r0 = org.telegram.messenger.AndroidUtilities.density
            float r10 = r10 / r0
            int r7 = (int) r10
            float r15 = r15 / r0
            int r10 = (int) r15
            r21 = r12
            double r11 = (double) r0
            double r11 = java.lang.Math.ceil(r11)
            int r0 = (int) r11
            r11 = 2
            int r38 = java.lang.Math.min(r11, r0)
            r29 = r2
            r31 = r5
            r33 = r21
            r35 = r7
            r36 = r10
            org.telegram.messenger.WebFile r0 = org.telegram.messenger.WebFile.createWithGeoPoint(r29, r31, r33, r35, r36, r37, r38)
            r1.currentWebFile = r0
            boolean r0 = r58.isCurrentLocationTimeExpired(r59)
            r1.locationExpired = r0
            if (r0 != 0) goto L_0x2681
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r2 = 1
            r0.setCrossfadeWithOldImage(r2)
            r3 = 0
            r1.mediaBackground = r3
            r0 = 1113587712(0x42600000, float:56.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r0)
            java.lang.Runnable r0 = r1.invalidateRunnable
            r5 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r5)
            r1.scheduledInvalidate = r2
            goto L_0x268d
        L_0x2681:
            int r0 = r1.backgroundWidth
            r2 = 1091567616(0x41100000, float:9.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r1.backgroundWidth = r0
            r12 = 0
        L_0x268d:
            android.text.StaticLayout r0 = new android.text.StaticLayout
            r2 = 2131624276(0x7f0e0154, float:1.8875727E38)
            java.lang.String r3 = "AttachLiveLocation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint
            float r5 = (float) r4
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r30 = android.text.TextUtils.ellipsize(r2, r3, r5, r6)
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r32 = r4 + r2
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            r1.docTitleLayout = r0
            r58.updateCurrentUserAndChat()
            org.telegram.tgnet.TLRPC$User r0 = r1.currentUser
            if (r0 == 0) goto L_0x26e1
            org.telegram.ui.Components.AvatarDrawable r2 = r1.contactAvatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC.User) r0)
            org.telegram.messenger.ImageReceiver r0 = r1.locationImageReceiver
            org.telegram.tgnet.TLRPC$User r2 = r1.currentUser
            r3 = 0
            org.telegram.messenger.ImageLocation r30 = org.telegram.messenger.ImageLocation.getForUser(r2, r3)
            org.telegram.ui.Components.AvatarDrawable r2 = r1.contactAvatarDrawable
            r33 = 0
            org.telegram.tgnet.TLRPC$User r3 = r1.currentUser
            r35 = 0
            java.lang.String r31 = "50_50"
            r29 = r0
            r32 = r2
            r34 = r3
            r29.setImage(r30, r31, r32, r33, r34, r35)
            goto L_0x2726
        L_0x26e1:
            org.telegram.tgnet.TLRPC$Chat r0 = r1.currentChat
            if (r0 == 0) goto L_0x2711
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r0.photo
            if (r0 == 0) goto L_0x26ed
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small
            r1.currentPhoto = r0
        L_0x26ed:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.contactAvatarDrawable
            org.telegram.tgnet.TLRPC$Chat r2 = r1.currentChat
            r0.setInfo((org.telegram.tgnet.TLRPC.Chat) r2)
            org.telegram.messenger.ImageReceiver r0 = r1.locationImageReceiver
            org.telegram.tgnet.TLRPC$Chat r2 = r1.currentChat
            r3 = 0
            org.telegram.messenger.ImageLocation r30 = org.telegram.messenger.ImageLocation.getForChat(r2, r3)
            org.telegram.ui.Components.AvatarDrawable r2 = r1.contactAvatarDrawable
            r33 = 0
            org.telegram.tgnet.TLRPC$Chat r3 = r1.currentChat
            r35 = 0
            java.lang.String r31 = "50_50"
            r29 = r0
            r32 = r2
            r34 = r3
            r29.setImage(r30, r31, r32, r33, r34, r35)
            goto L_0x2726
        L_0x2711:
            org.telegram.messenger.ImageReceiver r0 = r1.locationImageReceiver
            r37 = 0
            r38 = 0
            org.telegram.ui.Components.AvatarDrawable r2 = r1.contactAvatarDrawable
            r40 = 0
            r41 = 0
            r42 = 0
            r36 = r0
            r39 = r2
            r36.setImage(r37, r38, r39, r40, r41, r42)
        L_0x2726:
            android.text.StaticLayout r0 = new android.text.StaticLayout
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            int r3 = r2.edit_date
            if (r3 == 0) goto L_0x2730
            long r2 = (long) r3
            goto L_0x2733
        L_0x2730:
            int r2 = r2.date
            long r2 = (long) r2
        L_0x2733:
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatLocationUpdateDate(r2)
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r5 + r4
            float r5 = (float) r5
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r30 = android.text.TextUtils.ellipsize(r2, r3, r5, r6)
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r0
            r32 = r4
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            r1.infoLayout = r0
            r0 = r8
            goto L_0x28e0
        L_0x275b:
            java.lang.String r7 = r8.title
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x28e3
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r7 == 0) goto L_0x2793
            int r7 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            boolean r8 = r1.isChat
            if (r8 == 0) goto L_0x277e
            boolean r8 = r59.needDrawAvatar()
            if (r8 == 0) goto L_0x277e
            boolean r8 = r59.isOutOwner()
            if (r8 != 0) goto L_0x277e
            goto L_0x2780
        L_0x277e:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x2780:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = r7 - r4
            r4 = 1133543424(0x43908000, float:289.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = java.lang.Math.min(r7, r4)
            r1.backgroundWidth = r4
            goto L_0x27bc
        L_0x2793:
            android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
            int r7 = r7.x
            boolean r8 = r1.isChat
            if (r8 == 0) goto L_0x27a8
            boolean r8 = r59.needDrawAvatar()
            if (r8 == 0) goto L_0x27a8
            boolean r8 = r59.isOutOwner()
            if (r8 != 0) goto L_0x27a8
            goto L_0x27aa
        L_0x27a8:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x27aa:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = r7 - r4
            r4 = 1133543424(0x43908000, float:289.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = java.lang.Math.min(r7, r4)
            r1.backgroundWidth = r4
        L_0x27bc:
            int r4 = r1.backgroundWidth
            r7 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r7
            r1.backgroundWidth = r4
            boolean r4 = r58.checkNeedDrawShareButton(r59)
            if (r4 == 0) goto L_0x27d8
            int r4 = r1.backgroundWidth
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r7
            r1.backgroundWidth = r4
        L_0x27d8:
            int r4 = r1.backgroundWidth
            r7 = 1107820544(0x42080000, float:34.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r7
            r1.availableTimeWidth = r4
            int r7 = r1.backgroundWidth
            r8 = 1099431936(0x41880000, float:17.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 - r8
            r8 = 1128464384(0x43430000, float:195.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 0
            r1.mediaBackground = r9
            int r9 = r1.currentAccount
            float r10 = (float) r7
            float r11 = org.telegram.messenger.AndroidUtilities.density
            float r12 = r10 / r11
            int r12 = (int) r12
            float r13 = (float) r8
            float r11 = r13 / r11
            int r11 = (int) r11
            r36 = 1
            r37 = 15
            r29 = r9
            r30 = r2
            r32 = r5
            r34 = r12
            r35 = r11
            java.lang.String r2 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r29, r30, r32, r34, r35, r36, r37, r38)
            r1.currentUrl = r2
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r10 = r10 / r2
            int r3 = (int) r10
            float r13 = r13 / r2
            int r5 = (int) r13
            r6 = 15
            double r9 = (double) r2
            double r9 = java.lang.Math.ceil(r9)
            int r2 = (int) r9
            r9 = 2
            int r2 = java.lang.Math.min(r9, r2)
            org.telegram.messenger.WebFile r0 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r3, r5, r6, r2)
            r1.currentWebFile = r0
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r0 = r0.title
            android.text.TextPaint r40 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r41 = r4 + r2
            android.text.Layout$Alignment r42 = android.text.Layout.Alignment.ALIGN_NORMAL
            r43 = 1065353216(0x3var_, float:1.0)
            r44 = 0
            r45 = 0
            android.text.TextUtils$TruncateAt r46 = android.text.TextUtils.TruncateAt.END
            r48 = 1
            r39 = r0
            r47 = r4
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r39, r40, r41, r42, r43, r44, r45, r46, r47, r48)
            r1.docTitleLayout = r0
            r9 = 1112014848(0x42480000, float:50.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r2 = 0
            int r12 = r2 + r0
            android.text.StaticLayout r0 = r1.docTitleLayout
            r0.getLineCount()
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r0 = r0.address
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x28db
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r0 = r0.address
            android.text.TextPaint r40 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint
            android.text.Layout$Alignment r42 = android.text.Layout.Alignment.ALIGN_NORMAL
            r43 = 1065353216(0x3var_, float:1.0)
            r44 = 0
            r45 = 0
            android.text.TextUtils$TruncateAt r46 = android.text.TextUtils.TruncateAt.END
            r48 = 1
            r39 = r0
            r41 = r4
            r47 = r4
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r39, r40, r41, r42, r43, r44, r45, r46, r47, r48)
            r1.infoLayout = r0
            r58.measureTime(r59)
            int r0 = r1.backgroundWidth
            android.text.StaticLayout r2 = r1.infoLayout
            r3 = 0
            float r2 = r2.getLineWidth(r3)
            double r4 = (double) r2
            double r4 = java.lang.Math.ceil(r4)
            int r2 = (int) r4
            int r0 = r0 - r2
            r2 = 1103101952(0x41CLASSNAME, float:24.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            android.text.StaticLayout r2 = r1.infoLayout
            float r2 = r2.getLineLeft(r3)
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x28b5
            r2 = 1
            goto L_0x28b6
        L_0x28b5:
            r2 = 0
        L_0x28b6:
            if (r2 != 0) goto L_0x28ce
            int r3 = r1.timeWidth
            boolean r4 = r59.isOutOwner()
            if (r4 == 0) goto L_0x28c3
            r4 = 20
            goto L_0x28c4
        L_0x28c3:
            r4 = 0
        L_0x28c4:
            int r4 = r4 + 20
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 + r4
            if (r0 >= r3) goto L_0x28de
        L_0x28ce:
            if (r2 == 0) goto L_0x28d3
            r0 = 1092616192(0x41200000, float:10.0)
            goto L_0x28d5
        L_0x28d3:
            r0 = 1090519040(0x41000000, float:8.0)
        L_0x28d5:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r12 = r12 + r0
            goto L_0x28de
        L_0x28db:
            r2 = 0
            r1.infoLayout = r2
        L_0x28de:
            r0 = r7
            r9 = r8
        L_0x28e0:
            r8 = 2
            goto L_0x29b0
        L_0x28e3:
            r9 = 1112014848(0x42480000, float:50.0)
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r7 == 0) goto L_0x2915
            int r7 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            boolean r8 = r1.isChat
            if (r8 == 0) goto L_0x2900
            boolean r8 = r59.needDrawAvatar()
            if (r8 == 0) goto L_0x2900
            boolean r8 = r59.isOutOwner()
            if (r8 != 0) goto L_0x2900
            goto L_0x2902
        L_0x2900:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x2902:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = r7 - r4
            r4 = 1133543424(0x43908000, float:289.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = java.lang.Math.min(r7, r4)
            r1.backgroundWidth = r4
            goto L_0x293e
        L_0x2915:
            android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
            int r7 = r7.x
            boolean r8 = r1.isChat
            if (r8 == 0) goto L_0x292a
            boolean r8 = r59.needDrawAvatar()
            if (r8 == 0) goto L_0x292a
            boolean r8 = r59.isOutOwner()
            if (r8 != 0) goto L_0x292a
            goto L_0x292c
        L_0x292a:
            r4 = 1112014848(0x42480000, float:50.0)
        L_0x292c:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = r7 - r4
            r4 = 1133543424(0x43908000, float:289.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = java.lang.Math.min(r7, r4)
            r1.backgroundWidth = r4
        L_0x293e:
            int r4 = r1.backgroundWidth
            r7 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r7
            r1.backgroundWidth = r4
            boolean r4 = r58.checkNeedDrawShareButton(r59)
            if (r4 == 0) goto L_0x295a
            int r4 = r1.backgroundWidth
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r7
            r1.backgroundWidth = r4
        L_0x295a:
            int r4 = r1.backgroundWidth
            r7 = 1107820544(0x42080000, float:34.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r7
            r1.availableTimeWidth = r4
            int r4 = r1.backgroundWidth
            r7 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r7
            r7 = 1128464384(0x43430000, float:195.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r8 = r1.currentAccount
            float r9 = (float) r4
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r11 = r9 / r10
            int r11 = (int) r11
            float r12 = (float) r7
            float r10 = r12 / r10
            int r10 = (int) r10
            r36 = 1
            r37 = 15
            r29 = r8
            r30 = r2
            r32 = r5
            r34 = r11
            r35 = r10
            java.lang.String r2 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r29, r30, r32, r34, r35, r36, r37, r38)
            r1.currentUrl = r2
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r9 = r9 / r2
            int r3 = (int) r9
            float r12 = r12 / r2
            int r5 = (int) r12
            r6 = 15
            double r8 = (double) r2
            double r8 = java.lang.Math.ceil(r8)
            int r2 = (int) r8
            r8 = 2
            int r2 = java.lang.Math.min(r8, r2)
            org.telegram.messenger.WebFile r0 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r3, r5, r6, r2)
            r1.currentWebFile = r0
            r0 = r4
            r9 = r7
            r12 = 0
        L_0x29b0:
            long r2 = r59.getDialogId()
            int r3 = (int) r2
            if (r3 != 0) goto L_0x29cf
            int r2 = org.telegram.messenger.SharedConfig.mapPreviewType
            if (r2 != 0) goto L_0x29bf
            r1.currentMapProvider = r8
        L_0x29bd:
            r2 = -1
            goto L_0x29da
        L_0x29bf:
            r3 = 1
            if (r2 != r3) goto L_0x29c5
            r1.currentMapProvider = r3
            goto L_0x29bd
        L_0x29c5:
            r4 = 3
            if (r2 != r4) goto L_0x29cb
            r1.currentMapProvider = r3
            goto L_0x29bd
        L_0x29cb:
            r2 = -1
            r1.currentMapProvider = r2
            goto L_0x29da
        L_0x29cf:
            r2 = -1
            int r3 = r14.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r3 = r3.mapProvider
            r1.currentMapProvider = r3
        L_0x29da:
            int r3 = r1.currentMapProvider
            if (r3 != r2) goto L_0x29f4
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r3 = 0
            r4 = 0
            android.graphics.drawable.Drawable[] r5 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable
            boolean r6 = r59.isOutOwner()
            r5 = r5[r6]
            r6 = 0
            r8 = 0
            r15 = 8
            r7 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8)
            goto L_0x2a41
        L_0x29f4:
            r2 = 2
            r15 = 8
            if (r3 != r2) goto L_0x2a19
            org.telegram.messenger.WebFile r2 = r1.currentWebFile
            if (r2 == 0) goto L_0x2a41
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForWebFile(r2)
            r5 = 0
            android.graphics.drawable.Drawable[] r2 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable
            boolean r6 = r59.isOutOwner()
            r6 = r2[r6]
            r7 = 0
            r8 = 0
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8)
            goto L_0x2a41
        L_0x2a19:
            r2 = 3
            if (r3 == r2) goto L_0x2a1f
            r2 = 4
            if (r3 != r2) goto L_0x2a2d
        L_0x2a1f:
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r3 = r1.currentUrl
            org.telegram.messenger.WebFile r4 = r1.currentWebFile
            r2.addTestWebFile(r3, r4)
            r2 = 1
            r1.addedForTest = r2
        L_0x2a2d:
            java.lang.String r4 = r1.currentUrl
            if (r4 == 0) goto L_0x2a41
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            r5 = 0
            android.graphics.drawable.Drawable[] r2 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable
            boolean r6 = r59.isOutOwner()
            r6 = r2[r6]
            r7 = 0
            r8 = 0
            r3.setImage(r4, r5, r6, r7, r8)
        L_0x2a41:
            r3 = r9
            r56 = r12
            r12 = 0
            r15 = 0
            r24 = 1065353216(0x3var_, float:1.0)
            goto L_0x378f
        L_0x2a4a:
            r15 = 8
            boolean r0 = r59.isAnyKindOfSticker()
            if (r0 == 0) goto L_0x2c3a
            r2 = 0
            r1.drawBackground = r2
            int r0 = r14.type
            r2 = 13
            if (r0 != r2) goto L_0x2a5d
            r0 = 1
            goto L_0x2a5e
        L_0x2a5d:
            r0 = 0
        L_0x2a5e:
            r2 = 0
        L_0x2a5f:
            org.telegram.tgnet.TLRPC$Document r3 = r59.getDocument()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x2a83
            org.telegram.tgnet.TLRPC$Document r3 = r59.getDocument()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize
            if (r4 == 0) goto L_0x2a80
            int r12 = r3.w
            int r2 = r3.h
            goto L_0x2a85
        L_0x2a80:
            int r2 = r2 + 1
            goto L_0x2a5f
        L_0x2a83:
            r2 = 0
            r12 = 0
        L_0x2a85:
            boolean r3 = r59.isAnimatedSticker()
            if (r3 == 0) goto L_0x2a93
            if (r12 != 0) goto L_0x2a93
            if (r2 != 0) goto L_0x2a93
            r12 = 512(0x200, float:7.175E-43)
            r2 = 512(0x200, float:7.175E-43)
        L_0x2a93:
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x2aa2
            int r3 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            float r3 = (float) r3
            r4 = 1053609165(0x3ecccccd, float:0.4)
            goto L_0x2aaf
        L_0x2aa2:
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
            int r3 = r3.y
            int r3 = java.lang.Math.min(r4, r3)
            float r3 = (float) r3
            r4 = 1056964608(0x3var_, float:0.5)
        L_0x2aaf:
            float r3 = r3 * r4
            boolean r4 = r59.isAnimatedEmoji()
            if (r4 == 0) goto L_0x2ad2
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            float r4 = r4.animatedEmojisZoom
            float r5 = (float) r12
            r6 = 1140850688(0x44000000, float:512.0)
            float r5 = r5 / r6
            float r5 = r5 * r3
            float r5 = r5 * r4
            int r5 = (int) r5
            float r2 = (float) r2
            float r2 = r2 / r6
            float r2 = r2 * r3
            float r2 = r2 * r4
            int r2 = (int) r2
            r3 = r2
            r2 = r5
            goto L_0x2af3
        L_0x2ad2:
            if (r12 != 0) goto L_0x2add
            int r2 = (int) r3
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r12 = r2 + r4
        L_0x2add:
            float r2 = (float) r2
            float r4 = (float) r12
            float r4 = r3 / r4
            float r2 = r2 * r4
            int r2 = (int) r2
            int r4 = (int) r3
            float r5 = (float) r2
            int r6 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x2af1
            float r2 = (float) r4
            float r3 = r3 / r5
            float r2 = r2 * r3
            int r2 = (int) r2
            r3 = r4
            goto L_0x2af3
        L_0x2af1:
            r3 = r2
            r2 = r4
        L_0x2af3:
            float r4 = (float) r2
            float r5 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r5
            int r4 = (int) r4
            float r6 = (float) r3
            float r6 = r6 / r5
            int r5 = (int) r6
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r6 = r1.delegate
            if (r6 == 0) goto L_0x2b07
            boolean r6 = r6.shouldRepeatSticker(r14)
            if (r6 == 0) goto L_0x2b07
            r6 = 1
            goto L_0x2b08
        L_0x2b07:
            r6 = 0
        L_0x2b08:
            boolean r7 = r59.isAnimatedEmoji()
            if (r7 == 0) goto L_0x2b54
            java.util.Locale r7 = java.util.Locale.US
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "%d_%d_nr_%s"
            r8.append(r9)
            java.lang.String r9 = r14.emojiAnimatedStickerColor
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r9 = 3
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r9 = 0
            r10[r9] = r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r5 = 1
            r10[r5] = r4
            java.lang.String r4 = r59.toString()
            r5 = 2
            r10[r5] = r4
            java.lang.String r4 = java.lang.String.format(r7, r8, r10)
            org.telegram.messenger.ImageReceiver r5 = r1.photoImage
            if (r6 == 0) goto L_0x2b45
            r6 = 2
            goto L_0x2b46
        L_0x2b45:
            r6 = 3
        L_0x2b46:
            r5.setAutoRepeat(r6)
            org.telegram.tgnet.TLRPC$Document r5 = r14.emojiAnimatedSticker
            org.telegram.tgnet.TLRPC$InputStickerSet r5 = org.telegram.messenger.MessageObject.getInputStickerSet((org.telegram.tgnet.TLRPC.Document) r5)
            r31 = r4
            r36 = r5
            goto L_0x2ba8
        L_0x2b54:
            boolean r7 = org.telegram.messenger.SharedConfig.loopStickers
            if (r7 != 0) goto L_0x2b86
            if (r0 == 0) goto L_0x2b5b
            goto L_0x2b86
        L_0x2b5b:
            java.util.Locale r7 = java.util.Locale.US
            r8 = 3
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r8 = 0
            r9[r8] = r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r5 = 1
            r9[r5] = r4
            java.lang.String r4 = r59.toString()
            r8 = 2
            r9[r8] = r4
            java.lang.String r4 = "%d_%d_nr_%s"
            java.lang.String r4 = java.lang.String.format(r7, r4, r9)
            org.telegram.messenger.ImageReceiver r5 = r1.photoImage
            if (r6 == 0) goto L_0x2b81
            r6 = 2
            goto L_0x2b82
        L_0x2b81:
            r6 = 3
        L_0x2b82:
            r5.setAutoRepeat(r6)
            goto L_0x2ba4
        L_0x2b86:
            r8 = 2
            java.util.Locale r6 = java.util.Locale.US
            java.lang.Object[] r7 = new java.lang.Object[r8]
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r8 = 0
            r7[r8] = r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r5 = 1
            r7[r5] = r4
            java.lang.String r4 = "%d_%d"
            java.lang.String r4 = java.lang.String.format(r6, r4, r7)
            org.telegram.messenger.ImageReceiver r6 = r1.photoImage
            r6.setAutoRepeat(r5)
        L_0x2ba4:
            r31 = r4
            r36 = r14
        L_0x2ba8:
            r4 = 6
            r1.documentAttachType = r4
            r4 = 1096810496(0x41600000, float:14.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r2 - r4
            r1.availableTimeWidth = r4
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r2
            r1.backgroundWidth = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r14.photoThumbs
            r5 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5)
            r1.currentPhotoObjectThumb = r4
            org.telegram.tgnet.TLObject r4 = r14.photoThumbsObject
            r1.photoParentObject = r4
            boolean r4 = r14.attachPathExists
            if (r4 == 0) goto L_0x2bfe
            org.telegram.messenger.ImageReceiver r4 = r1.photoImage
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner
            java.lang.String r5 = r5.attachPath
            org.telegram.messenger.ImageLocation r30 = org.telegram.messenger.ImageLocation.getForPath(r5)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r32 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            org.telegram.tgnet.TLRPC$Document r5 = r59.getDocument()
            int r5 = r5.size
            if (r0 == 0) goto L_0x2bf0
            java.lang.String r0 = "webp"
            r35 = r0
            goto L_0x2bf2
        L_0x2bf0:
            r35 = 0
        L_0x2bf2:
            r37 = 1
            java.lang.String r33 = "b1"
            r29 = r4
            r34 = r5
            r29.setImage(r30, r31, r32, r33, r34, r35, r36, r37)
            goto L_0x2CLASSNAME
        L_0x2bfe:
            org.telegram.tgnet.TLRPC$Document r4 = r59.getDocument()
            long r4 = r4.id
            r6 = 0
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x2CLASSNAME
            org.telegram.messenger.ImageReceiver r4 = r1.photoImage
            org.telegram.tgnet.TLRPC$Document r5 = r59.getDocument()
            org.telegram.messenger.ImageLocation r30 = org.telegram.messenger.ImageLocation.getForDocument(r5)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r32 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            org.telegram.tgnet.TLRPC$Document r5 = r59.getDocument()
            int r5 = r5.size
            if (r0 == 0) goto L_0x2c2a
            java.lang.String r0 = "webp"
            r35 = r0
            goto L_0x2c2c
        L_0x2c2a:
            r35 = 0
        L_0x2c2c:
            r37 = 1
            java.lang.String r33 = "b1"
            r29 = r4
            r34 = r5
            r29.setImage(r30, r31, r32, r33, r34, r35, r36, r37)
        L_0x2CLASSNAME:
            r0 = r2
            goto L_0x24d8
        L_0x2c3a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r14.photoThumbs
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2)
            r1.currentPhotoObject = r0
            org.telegram.tgnet.TLObject r0 = r14.photoThumbsObject
            r1.photoParentObject = r0
            int r0 = r14.type
            r2 = 5
            if (r0 != r2) goto L_0x2c5c
            int r0 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            org.telegram.tgnet.TLRPC$Document r2 = r59.getDocument()
            r1.documentAttach = r2
            r2 = 7
            r1.documentAttachType = r2
        L_0x2c5a:
            r2 = 0
            goto L_0x2ca2
        L_0x2c5c:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x2c6e
            int r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
        L_0x2CLASSNAME:
            float r0 = (float) r0
            r2 = 1060320051(0x3var_, float:0.7)
            float r0 = r0 * r2
            int r0 = (int) r0
            goto L_0x2c5a
        L_0x2c6e:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            if (r0 == 0) goto L_0x2CLASSNAME
            int r0 = r14.type
            r2 = 1
            if (r0 == r2) goto L_0x2c7c
            r2 = 3
            if (r0 == r2) goto L_0x2c7c
            if (r0 != r15) goto L_0x2CLASSNAME
        L_0x2c7c:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = r1.currentPhotoObject
            int r2 = r0.w
            int r0 = r0.h
            if (r2 < r0) goto L_0x2CLASSNAME
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r0.x
            int r0 = r0.y
            int r0 = java.lang.Math.min(r2, r0)
            r2 = 1115684864(0x42800000, float:64.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r2 = 1
            goto L_0x2ca2
        L_0x2CLASSNAME:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r0.x
            int r0 = r0.y
            int r0 = java.lang.Math.min(r2, r0)
            goto L_0x2CLASSNAME
        L_0x2ca2:
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r0
            if (r2 != 0) goto L_0x2cdc
            int r2 = r14.type
            r4 = 5
            if (r2 == r4) goto L_0x2cc6
            boolean r2 = r58.checkNeedDrawShareButton(r59)
            if (r2 == 0) goto L_0x2cc6
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r0 - r2
            r4 = 1101004800(0x41a00000, float:20.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            goto L_0x2cc7
        L_0x2cc6:
            r2 = r0
        L_0x2cc7:
            int r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            if (r0 <= r4) goto L_0x2cd1
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
        L_0x2cd1:
            int r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            if (r3 <= r4) goto L_0x2cfb
            int r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            goto L_0x2cfb
        L_0x2cdc:
            boolean r2 = r1.isChat
            if (r2 == 0) goto L_0x2cfa
            boolean r2 = r59.needDrawAvatar()
            if (r2 == 0) goto L_0x2cfa
            boolean r2 = r59.isOutOwner()
            if (r2 != 0) goto L_0x2cfa
            r2 = 1112539136(0x42500000, float:52.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r0 - r2
            r57 = r2
            r2 = r0
            r0 = r57
            goto L_0x2cfb
        L_0x2cfa:
            r2 = r0
        L_0x2cfb:
            int r4 = r14.type
            r5 = 1
            if (r4 != r5) goto L_0x2d0e
            r58.updateSecretTimeText(r59)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r14.photoThumbs
            r5 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5)
            r1.currentPhotoObjectThumb = r4
            goto L_0x2d22
        L_0x2d0e:
            r5 = 3
            if (r4 == r5) goto L_0x2d24
            if (r4 != r15) goto L_0x2d14
            goto L_0x2d24
        L_0x2d14:
            r5 = 5
            if (r4 != r5) goto L_0x2d22
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r14.photoThumbs
            r5 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5)
            r1.currentPhotoObjectThumb = r4
            goto L_0x2d35
        L_0x2d22:
            r4 = 0
            goto L_0x2d36
        L_0x2d24:
            r4 = 0
            r1.createDocumentLayout(r4, r14)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r14.photoThumbs
            r5 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5)
            r1.currentPhotoObjectThumb = r4
            r58.updateSecretTimeText(r59)
        L_0x2d35:
            r4 = 1
        L_0x2d36:
            int r5 = r14.type
            r6 = 5
            if (r5 != r6) goto L_0x2d3f
            int r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            r6 = r5
            goto L_0x2d84
        L_0x2d3f:
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObject
            if (r5 == 0) goto L_0x2d44
            goto L_0x2d46
        L_0x2d44:
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObjectThumb
        L_0x2d46:
            if (r5 == 0) goto L_0x2d4d
            int r12 = r5.w
            int r5 = r5.h
            goto L_0x2d75
        L_0x2d4d:
            org.telegram.tgnet.TLRPC$Document r5 = r1.documentAttach
            if (r5 == 0) goto L_0x2d73
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes
            int r5 = r5.size()
            r6 = 0
            r7 = 0
            r12 = 0
        L_0x2d5a:
            if (r6 >= r5) goto L_0x2d71
            org.telegram.tgnet.TLRPC$Document r8 = r1.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r8.attributes
            java.lang.Object r8 = r8.get(r6)
            org.telegram.tgnet.TLRPC$DocumentAttribute r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo
            if (r9 == 0) goto L_0x2d6e
            int r12 = r8.w
            int r7 = r8.h
        L_0x2d6e:
            int r6 = r6 + 1
            goto L_0x2d5a
        L_0x2d71:
            r5 = r7
            goto L_0x2d75
        L_0x2d73:
            r5 = 0
            r12 = 0
        L_0x2d75:
            org.telegram.ui.Components.Point r5 = getMessageSize(r12, r5, r0, r3)
            float r6 = r5.x
            int r6 = (int) r6
            float r5 = r5.y
            int r5 = (int) r5
            r57 = r6
            r6 = r5
            r5 = r57
        L_0x2d84:
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObject
            if (r7 == 0) goto L_0x2d96
            java.lang.String r7 = r7.type
            java.lang.String r8 = "s"
            boolean r7 = r8.equals(r7)
            if (r7 == 0) goto L_0x2d96
            r7 = 0
            r1.currentPhotoObject = r7
            goto L_0x2d97
        L_0x2d96:
            r7 = 0
        L_0x2d97:
            org.telegram.tgnet.TLRPC$PhotoSize r8 = r1.currentPhotoObject
            if (r8 == 0) goto L_0x2da9
            org.telegram.tgnet.TLRPC$PhotoSize r9 = r1.currentPhotoObjectThumb
            if (r8 != r9) goto L_0x2da9
            int r8 = r14.type
            r9 = 1
            if (r8 != r9) goto L_0x2da7
            r1.currentPhotoObjectThumb = r7
            goto L_0x2da9
        L_0x2da7:
            r1.currentPhotoObject = r7
        L_0x2da9:
            if (r4 == 0) goto L_0x2dd2
            boolean r4 = r59.needDrawBluredPreview()
            if (r4 != 0) goto L_0x2dd2
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r1.currentPhotoObject
            if (r4 == 0) goto L_0x2db9
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObjectThumb
            if (r4 != r7) goto L_0x2dd2
        L_0x2db9:
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r1.currentPhotoObjectThumb
            if (r4 == 0) goto L_0x2dc7
            java.lang.String r4 = r4.type
            java.lang.String r7 = "m"
            boolean r4 = r7.equals(r4)
            if (r4 != 0) goto L_0x2dd2
        L_0x2dc7:
            org.telegram.messenger.ImageReceiver r4 = r1.photoImage
            r7 = 1
            r4.setNeedsQualityThumb(r7)
            org.telegram.messenger.ImageReceiver r4 = r1.photoImage
            r4.setShouldGenerateQualityThumb(r7)
        L_0x2dd2:
            org.telegram.messenger.MessageObject$GroupedMessages r4 = r1.currentMessagesGroup
            if (r4 != 0) goto L_0x2ddd
            java.lang.CharSequence r4 = r14.caption
            if (r4 == 0) goto L_0x2ddd
            r4 = 0
            r1.mediaBackground = r4
        L_0x2ddd:
            if (r5 == 0) goto L_0x2de1
            if (r6 != 0) goto L_0x2e43
        L_0x2de1:
            int r4 = r14.type
            if (r4 != r15) goto L_0x2e43
            r4 = 0
        L_0x2de6:
            org.telegram.tgnet.TLRPC$Document r7 = r59.getDocument()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r7.attributes
            int r7 = r7.size()
            if (r4 >= r7) goto L_0x2e43
            org.telegram.tgnet.TLRPC$Document r7 = r59.getDocument()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r7.attributes
            java.lang.Object r7 = r7.get(r4)
            org.telegram.tgnet.TLRPC$DocumentAttribute r7 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r7
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize
            if (r8 != 0) goto L_0x2e0a
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo
            if (r8 == 0) goto L_0x2e07
            goto L_0x2e0a
        L_0x2e07:
            int r4 = r4 + 1
            goto L_0x2de6
        L_0x2e0a:
            int r4 = r7.w
            float r5 = (float) r4
            float r0 = (float) r0
            float r5 = r5 / r0
            float r4 = (float) r4
            float r4 = r4 / r5
            int r4 = (int) r4
            int r6 = r7.h
            float r6 = (float) r6
            float r6 = r6 / r5
            int r5 = (int) r6
            if (r5 <= r3) goto L_0x2e20
            float r0 = (float) r5
            float r5 = (float) r3
            float r0 = r0 / r5
            float r4 = (float) r4
            float r4 = r4 / r0
            int r5 = (int) r4
            goto L_0x2e44
        L_0x2e20:
            r3 = 1123024896(0x42var_, float:120.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            if (r5 >= r3) goto L_0x2e40
            r3 = 1123024896(0x42var_, float:120.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r5 = r7.h
            float r5 = (float) r5
            float r6 = (float) r3
            float r5 = r5 / r6
            int r6 = r7.w
            float r7 = (float) r6
            float r7 = r7 / r5
            int r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x2e41
            float r0 = (float) r6
            float r0 = r0 / r5
            int r0 = (int) r0
            r5 = r0
            goto L_0x2e44
        L_0x2e40:
            r3 = r5
        L_0x2e41:
            r5 = r4
            goto L_0x2e44
        L_0x2e43:
            r3 = r6
        L_0x2e44:
            if (r5 == 0) goto L_0x2e48
            if (r3 != 0) goto L_0x2e4f
        L_0x2e48:
            r0 = 1125515264(0x43160000, float:150.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r3 = r5
        L_0x2e4f:
            int r0 = r14.type
            r4 = 3
            if (r0 != r4) goto L_0x2e69
            int r0 = r1.infoWidth
            r4 = 1109393408(0x42200000, float:40.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r4
            if (r5 >= r0) goto L_0x2e69
            int r0 = r1.infoWidth
            r4 = 1109393408(0x42200000, float:40.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r0 + r4
        L_0x2e69:
            org.telegram.messenger.MessageObject$GroupedMessages r0 = r1.currentMessagesGroup
            if (r0 == 0) goto L_0x2eaf
            int r0 = r58.getGroupPhotosWidth()
            r2 = 0
            r4 = 0
        L_0x2e73:
            org.telegram.messenger.MessageObject$GroupedMessages r6 = r1.currentMessagesGroup
            java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r6 = r6.posArray
            int r6 = r6.size()
            if (r2 >= r6) goto L_0x2ea5
            org.telegram.messenger.MessageObject$GroupedMessages r6 = r1.currentMessagesGroup
            java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r6 = r6.posArray
            java.lang.Object r6 = r6.get(r2)
            org.telegram.messenger.MessageObject$GroupedMessagePosition r6 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r6
            byte r7 = r6.minY
            if (r7 != 0) goto L_0x2ea5
            double r7 = (double) r4
            int r4 = r6.pw
            int r6 = r6.leftSpanOffset
            int r4 = r4 + r6
            float r4 = (float) r4
            r6 = 1148846080(0x447a0000, float:1000.0)
            float r4 = r4 / r6
            float r6 = (float) r0
            float r4 = r4 * r6
            double r9 = (double) r4
            double r9 = java.lang.Math.ceil(r9)
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            int r4 = (int) r7
            int r2 = r2 + 1
            goto L_0x2e73
        L_0x2ea5:
            r0 = 1108082688(0x420CLASSNAME, float:35.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r4 = r4 - r0
            r1.availableTimeWidth = r4
            goto L_0x2eb8
        L_0x2eaf:
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = r2 - r0
            r1.availableTimeWidth = r2
        L_0x2eb8:
            int r0 = r14.type
            r2 = 5
            if (r0 != r2) goto L_0x2edf
            int r0 = r1.availableTimeWidth
            double r6 = (double) r0
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint
            java.lang.String r2 = "00:00"
            float r0 = r0.measureText(r2)
            double r8 = (double) r0
            double r8 = java.lang.Math.ceil(r8)
            r0 = 1104150528(0x41d00000, float:26.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            double r10 = (double) r0
            java.lang.Double.isNaN(r10)
            double r8 = r8 + r10
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r8
            int r0 = (int) r6
            r1.availableTimeWidth = r0
        L_0x2edf:
            r58.measureTime(r59)
            int r0 = r1.timeWidth
            boolean r2 = r59.isOutOwner()
            if (r2 == 0) goto L_0x2eed
            r12 = 20
            goto L_0x2eee
        L_0x2eed:
            r12 = 0
        L_0x2eee:
            int r12 = r12 + 14
            float r2 = (float) r12
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            if (r5 >= r0) goto L_0x2ef9
            r5 = r0
        L_0x2ef9:
            boolean r2 = r59.isRoundVideo()
            if (r2 == 0) goto L_0x2f0f
            int r3 = java.lang.Math.min(r5, r3)
            r2 = 0
            r1.drawBackground = r2
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            int r4 = r3 / 2
            r2.setRoundRadius(r4)
        L_0x2f0d:
            r5 = r3
            goto L_0x2var_
        L_0x2f0f:
            boolean r2 = r59.needDrawBluredPreview()
            if (r2 == 0) goto L_0x2var_
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x2var_
            int r2 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            goto L_0x2f2a
        L_0x2var_:
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
            int r2 = r2.y
            int r2 = java.lang.Math.min(r3, r2)
        L_0x2f2a:
            float r2 = (float) r2
            r3 = 1056964608(0x3var_, float:0.5)
            float r2 = r2 * r3
            int r3 = (int) r2
            goto L_0x2f0d
        L_0x2var_:
            org.telegram.messenger.MessageObject$GroupedMessages r2 = r1.currentMessagesGroup
            if (r2 == 0) goto L_0x3254
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
            int r2 = r2.y
            int r2 = java.lang.Math.max(r3, r2)
            float r2 = (float) r2
            r3 = 1056964608(0x3var_, float:0.5)
            float r2 = r2 * r3
            int r3 = r58.getGroupPhotosWidth()
            org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = r1.currentPosition
            int r4 = r4.pw
            float r4 = (float) r4
            r5 = 1148846080(0x447a0000, float:1000.0)
            float r4 = r4 / r5
            float r3 = (float) r3
            float r4 = r4 * r3
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            int r4 = (int) r4
            org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = r1.currentPosition
            byte r5 = r5.minY
            if (r5 == 0) goto L_0x2ff4
            boolean r5 = r59.isOutOwner()
            if (r5 == 0) goto L_0x2f6d
            org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = r1.currentPosition
            int r5 = r5.flags
            r6 = 1
            r5 = r5 & r6
            if (r5 != 0) goto L_0x2f7b
        L_0x2f6d:
            boolean r5 = r59.isOutOwner()
            if (r5 != 0) goto L_0x2ff4
            org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = r1.currentPosition
            int r5 = r5.flags
            r6 = 2
            r5 = r5 & r6
            if (r5 == 0) goto L_0x2ff4
        L_0x2f7b:
            r5 = 0
            r6 = 0
            r7 = 0
        L_0x2f7e:
            org.telegram.messenger.MessageObject$GroupedMessages r8 = r1.currentMessagesGroup
            java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r8.posArray
            int r8 = r8.size()
            if (r5 >= r8) goto L_0x2ff2
            org.telegram.messenger.MessageObject$GroupedMessages r8 = r1.currentMessagesGroup
            java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r8.posArray
            java.lang.Object r8 = r8.get(r5)
            org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
            byte r9 = r8.minY
            if (r9 != 0) goto L_0x2fbd
            double r9 = (double) r6
            int r6 = r8.pw
            float r6 = (float) r6
            r11 = 1148846080(0x447a0000, float:1000.0)
            float r6 = r6 / r11
            float r6 = r6 * r3
            double r11 = (double) r6
            double r11 = java.lang.Math.ceil(r11)
            int r6 = r8.leftSpanOffset
            if (r6 == 0) goto L_0x2fb4
            float r6 = (float) r6
            r8 = 1148846080(0x447a0000, float:1000.0)
            float r6 = r6 / r8
            float r6 = r6 * r3
            double r13 = (double) r6
            double r13 = java.lang.Math.ceil(r13)
            goto L_0x2fb6
        L_0x2fb4:
            r13 = 0
        L_0x2fb6:
            double r11 = r11 + r13
            java.lang.Double.isNaN(r9)
            double r9 = r9 + r11
            int r6 = (int) r9
            goto L_0x2fed
        L_0x2fbd:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r10 = r1.currentPosition
            byte r10 = r10.minY
            if (r9 != r10) goto L_0x2fea
            double r9 = (double) r7
            int r7 = r8.pw
            float r7 = (float) r7
            r11 = 1148846080(0x447a0000, float:1000.0)
            float r7 = r7 / r11
            float r7 = r7 * r3
            double r11 = (double) r7
            double r11 = java.lang.Math.ceil(r11)
            int r7 = r8.leftSpanOffset
            if (r7 == 0) goto L_0x2fe1
            float r7 = (float) r7
            r8 = 1148846080(0x447a0000, float:1000.0)
            float r7 = r7 / r8
            float r7 = r7 * r3
            double r7 = (double) r7
            double r7 = java.lang.Math.ceil(r7)
            goto L_0x2fe3
        L_0x2fe1:
            r7 = 0
        L_0x2fe3:
            double r11 = r11 + r7
            java.lang.Double.isNaN(r9)
            double r9 = r9 + r11
            int r7 = (int) r9
            goto L_0x2fed
        L_0x2fea:
            if (r9 <= r10) goto L_0x2fed
            goto L_0x2ff2
        L_0x2fed:
            int r5 = r5 + 1
            r14 = r59
            goto L_0x2f7e
        L_0x2ff2:
            int r6 = r6 - r7
            int r4 = r4 + r6
        L_0x2ff4:
            r5 = 1091567616(0x41100000, float:9.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            boolean r5 = r1.isAvatarVisible
            if (r5 == 0) goto L_0x3006
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
        L_0x3006:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = r1.currentPosition
            float[] r6 = r5.siblingHeights
            if (r6 == 0) goto L_0x3036
            r5 = 0
            r6 = 0
        L_0x300e:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r7 = r1.currentPosition
            float[] r8 = r7.siblingHeights
            int r9 = r8.length
            if (r5 >= r9) goto L_0x3023
            r7 = r8[r5]
            float r7 = r7 * r2
            double r7 = (double) r7
            double r7 = java.lang.Math.ceil(r7)
            int r7 = (int) r7
            int r6 = r6 + r7
            int r5 = r5 + 1
            goto L_0x300e
        L_0x3023:
            byte r2 = r7.maxY
            byte r5 = r7.minY
            int r2 = r2 - r5
            r5 = 1088421888(0x40e00000, float:7.0)
            float r7 = org.telegram.messenger.AndroidUtilities.density
            float r7 = r7 * r5
            int r5 = java.lang.Math.round(r7)
            int r2 = r2 * r5
            int r6 = r6 + r2
            goto L_0x3040
        L_0x3036:
            float r5 = r5.ph
            float r2 = r2 * r5
            double r5 = (double) r2
            double r5 = java.lang.Math.ceil(r5)
            int r6 = (int) r5
        L_0x3040:
            r1.backgroundWidth = r4
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r1.currentPosition
            int r2 = r2.flags
            r5 = r2 & 2
            if (r5 == 0) goto L_0x3056
            r5 = 1
            r2 = r2 & r5
            if (r2 == 0) goto L_0x3056
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
        L_0x3054:
            int r4 = r4 - r2
            goto L_0x307d
        L_0x3056:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r1.currentPosition
            int r2 = r2.flags
            r5 = r2 & 2
            if (r5 != 0) goto L_0x3069
            r5 = 1
            r2 = r2 & r5
            if (r2 != 0) goto L_0x3069
            r2 = 1093664768(0x41300000, float:11.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            goto L_0x3054
        L_0x3069:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r1.currentPosition
            int r2 = r2.flags
            r5 = 2
            r2 = r2 & r5
            if (r2 == 0) goto L_0x3076
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            goto L_0x3054
        L_0x3076:
            r2 = 1091567616(0x41100000, float:9.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            goto L_0x3054
        L_0x307d:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r1.currentPosition
            boolean r2 = r2.edge
            if (r2 != 0) goto L_0x308a
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r2 = r2 + r4
            r5 = r2
            goto L_0x308b
        L_0x308a:
            r5 = r4
        L_0x308b:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r2 = r5 - r2
            r7 = 0
            int r12 = r7 + r2
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r1.currentPosition
            int r2 = r2.flags
            r7 = r2 & 8
            if (r7 != 0) goto L_0x30ae
            org.telegram.messenger.MessageObject$GroupedMessages r7 = r1.currentMessagesGroup
            boolean r7 = r7.hasSibling
            if (r7 == 0) goto L_0x30a7
            r2 = r2 & 4
            if (r2 != 0) goto L_0x30a7
            goto L_0x30ae
        L_0x30a7:
            r21 = r5
            r62 = r6
            r5 = r4
            goto L_0x324b
        L_0x30ae:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r1.currentPosition
            int r2 = r1.getAdditionalWidthForPosition(r2)
            int r12 = r12 + r2
            org.telegram.messenger.MessageObject$GroupedMessages r2 = r1.currentMessagesGroup
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r2.messages
            int r2 = r2.size()
            r7 = r4
            r4 = 0
        L_0x30bf:
            if (r4 >= r2) goto L_0x3244
            org.telegram.messenger.MessageObject$GroupedMessages r8 = r1.currentMessagesGroup
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r8.messages
            java.lang.Object r8 = r8.get(r4)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            org.telegram.messenger.MessageObject$GroupedMessages r9 = r1.currentMessagesGroup
            java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r9 = r9.posArray
            java.lang.Object r9 = r9.get(r4)
            org.telegram.messenger.MessageObject$GroupedMessagePosition r9 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r9
            org.telegram.messenger.MessageObject$GroupedMessagePosition r10 = r1.currentPosition
            if (r9 == r10) goto L_0x3224
            int r10 = r9.flags
            r10 = r10 & r15
            if (r10 == 0) goto L_0x3224
            int r7 = r9.pw
            float r7 = (float) r7
            r10 = 1148846080(0x447a0000, float:1000.0)
            float r7 = r7 / r10
            float r7 = r7 * r3
            double r10 = (double) r7
            double r10 = java.lang.Math.ceil(r10)
            int r7 = (int) r10
            byte r10 = r9.minY
            if (r10 == 0) goto L_0x319b
            boolean r10 = r59.isOutOwner()
            if (r10 == 0) goto L_0x30fc
            int r10 = r9.flags
            r11 = 1
            r10 = r10 & r11
            if (r10 != 0) goto L_0x3108
        L_0x30fc:
            boolean r10 = r59.isOutOwner()
            if (r10 != 0) goto L_0x319b
            int r10 = r9.flags
            r11 = 2
            r10 = r10 & r11
            if (r10 == 0) goto L_0x319b
        L_0x3108:
            r10 = 0
            r11 = 0
            r13 = 0
        L_0x310b:
            org.telegram.messenger.MessageObject$GroupedMessages r14 = r1.currentMessagesGroup
            java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r14 = r14.posArray
            int r14 = r14.size()
            if (r10 >= r14) goto L_0x3192
            org.telegram.messenger.MessageObject$GroupedMessages r14 = r1.currentMessagesGroup
            java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r14 = r14.posArray
            java.lang.Object r14 = r14.get(r10)
            org.telegram.messenger.MessageObject$GroupedMessagePosition r14 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r14
            byte r15 = r14.minY
            if (r15 != 0) goto L_0x3151
            r21 = r5
            r62 = r6
            double r5 = (double) r11
            int r11 = r14.pw
            float r11 = (float) r11
            r15 = 1148846080(0x447a0000, float:1000.0)
            float r11 = r11 / r15
            float r11 = r11 * r3
            r22 = r12
            double r11 = (double) r11
            double r11 = java.lang.Math.ceil(r11)
            int r14 = r14.leftSpanOffset
            if (r14 == 0) goto L_0x3147
            float r14 = (float) r14
            r15 = 1148846080(0x447a0000, float:1000.0)
            float r14 = r14 / r15
            float r14 = r14 * r3
            double r14 = (double) r14
            double r14 = java.lang.Math.ceil(r14)
            goto L_0x3149
        L_0x3147:
            r14 = 0
        L_0x3149:
            double r11 = r11 + r14
            java.lang.Double.isNaN(r5)
            double r5 = r5 + r11
            int r5 = (int) r5
            r11 = r5
            goto L_0x3186
        L_0x3151:
            r21 = r5
            r62 = r6
            r22 = r12
            byte r5 = r9.minY
            if (r15 != r5) goto L_0x3183
            double r5 = (double) r13
            int r12 = r14.pw
            float r12 = (float) r12
            r13 = 1148846080(0x447a0000, float:1000.0)
            float r12 = r12 / r13
            float r12 = r12 * r3
            double r12 = (double) r12
            double r12 = java.lang.Math.ceil(r12)
            int r14 = r14.leftSpanOffset
            if (r14 == 0) goto L_0x3179
            float r14 = (float) r14
            r15 = 1148846080(0x447a0000, float:1000.0)
            float r14 = r14 / r15
            float r14 = r14 * r3
            double r14 = (double) r14
            double r14 = java.lang.Math.ceil(r14)
            goto L_0x317b
        L_0x3179:
            r14 = 0
        L_0x317b:
            double r12 = r12 + r14
            java.lang.Double.isNaN(r5)
            double r5 = r5 + r12
            int r5 = (int) r5
            r13 = r5
            goto L_0x3186
        L_0x3183:
            if (r15 <= r5) goto L_0x3186
            goto L_0x3198
        L_0x3186:
            int r10 = r10 + 1
            r6 = r62
            r5 = r21
            r12 = r22
            r15 = 8
            goto L_0x310b
        L_0x3192:
            r21 = r5
            r62 = r6
            r22 = r12
        L_0x3198:
            int r11 = r11 - r13
            int r7 = r7 + r11
            goto L_0x31a1
        L_0x319b:
            r21 = r5
            r62 = r6
            r22 = r12
        L_0x31a1:
            r5 = 1091567616(0x41100000, float:9.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r7 = r7 - r5
            int r5 = r9.flags
            r6 = r5 & 2
            if (r6 == 0) goto L_0x31ba
            r5 = r5 & 1
            if (r5 == 0) goto L_0x31ba
            r5 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
        L_0x31b8:
            int r7 = r7 - r5
            goto L_0x31dd
        L_0x31ba:
            int r5 = r9.flags
            r6 = r5 & 2
            if (r6 != 0) goto L_0x31cb
            r5 = r5 & 1
            if (r5 != 0) goto L_0x31cb
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x31b8
        L_0x31cb:
            int r5 = r9.flags
            r6 = 2
            r5 = r5 & r6
            if (r5 == 0) goto L_0x31d6
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            goto L_0x31b8
        L_0x31d6:
            r5 = 1091567616(0x41100000, float:9.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x31b8
        L_0x31dd:
            boolean r5 = r1.isChat
            if (r5 == 0) goto L_0x31fa
            boolean r5 = r8.isOutOwner()
            if (r5 != 0) goto L_0x31fa
            boolean r5 = r8.needDrawAvatar()
            if (r5 == 0) goto L_0x31fa
            if (r9 == 0) goto L_0x31f3
            boolean r5 = r9.edge
            if (r5 == 0) goto L_0x31fa
        L_0x31f3:
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r7 = r7 - r5
        L_0x31fa:
            int r5 = r1.getAdditionalWidthForPosition(r9)
            int r7 = r7 + r5
            boolean r5 = r9.edge
            if (r5 != 0) goto L_0x3208
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r7 = r7 + r5
        L_0x3208:
            int r12 = r22 + r7
            byte r5 = r9.minX
            org.telegram.messenger.MessageObject$GroupedMessagePosition r6 = r1.currentPosition
            byte r6 = r6.minX
            if (r5 < r6) goto L_0x321e
            org.telegram.messenger.MessageObject$GroupedMessages r5 = r1.currentMessagesGroup
            boolean r5 = r5.hasSibling
            if (r5 == 0) goto L_0x322c
            byte r5 = r9.minY
            byte r6 = r9.maxY
            if (r5 == r6) goto L_0x322c
        L_0x321e:
            int r5 = r1.captionOffsetX
            int r5 = r5 - r7
            r1.captionOffsetX = r5
            goto L_0x322c
        L_0x3224:
            r21 = r5
            r62 = r6
            r22 = r12
            r12 = r22
        L_0x322c:
            java.lang.CharSequence r5 = r8.caption
            if (r5 == 0) goto L_0x323a
            java.lang.CharSequence r6 = r1.currentCaption
            if (r6 == 0) goto L_0x3238
            r6 = 0
            r1.currentCaption = r6
            goto L_0x324a
        L_0x3238:
            r1.currentCaption = r5
        L_0x323a:
            int r4 = r4 + 1
            r6 = r62
            r5 = r21
            r15 = 8
            goto L_0x30bf
        L_0x3244:
            r21 = r5
            r62 = r6
            r22 = r12
        L_0x324a:
            r5 = r7
        L_0x324b:
            r14 = r59
            r15 = r62
            r3 = r5
            r5 = r21
            r2 = 0
            goto L_0x32a5
        L_0x3254:
            java.lang.CharSequence r2 = r14.caption
            r1.currentCaption = r2
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x3263
            int r2 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            goto L_0x326d
        L_0x3263:
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r2.x
            int r2 = r2.y
            int r2 = java.lang.Math.min(r4, r2)
        L_0x326d:
            float r2 = (float) r2
            r4 = 1059481190(0x3var_, float:0.65)
            float r2 = r2 * r4
            int r2 = (int) r2
            boolean r4 = r59.needDrawBluredPreview()
            if (r4 != 0) goto L_0x3283
            java.lang.CharSequence r4 = r1.currentCaption
            if (r4 == 0) goto L_0x3283
            if (r5 >= r2) goto L_0x3283
            r12 = r2
            r2 = 1
            goto L_0x328b
        L_0x3283:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r2 = r5 - r2
            r12 = r2
            r2 = 0
        L_0x328b:
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r5
            r1.backgroundWidth = r4
            boolean r4 = r1.mediaBackground
            if (r4 != 0) goto L_0x32a3
            int r4 = r1.backgroundWidth
            r6 = 1091567616(0x41100000, float:9.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 + r6
            r1.backgroundWidth = r4
        L_0x32a3:
            r15 = r3
            r3 = r5
        L_0x32a5:
            java.lang.CharSequence r4 = r1.currentCaption
            if (r4 == 0) goto L_0x33bb
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x33b1 }
            r7 = 24
            if (r6 < r7) goto L_0x32d5
            int r6 = r4.length()     // Catch:{ Exception -> 0x32d0 }
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint     // Catch:{ Exception -> 0x32d0 }
            r8 = 0
            android.text.StaticLayout$Builder r4 = android.text.StaticLayout.Builder.obtain(r4, r8, r6, r7, r12)     // Catch:{ Exception -> 0x32d0 }
            r6 = 1
            android.text.StaticLayout$Builder r4 = r4.setBreakStrategy(r6)     // Catch:{ Exception -> 0x32d0 }
            android.text.StaticLayout$Builder r4 = r4.setHyphenationFrequency(r8)     // Catch:{ Exception -> 0x32d0 }
            android.text.Layout$Alignment r6 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x32d0 }
            android.text.StaticLayout$Builder r4 = r4.setAlignment(r6)     // Catch:{ Exception -> 0x32d0 }
            android.text.StaticLayout r4 = r4.build()     // Catch:{ Exception -> 0x32d0 }
            r1.captionLayout = r4     // Catch:{ Exception -> 0x32d0 }
            goto L_0x32ec
        L_0x32d0:
            r0 = move-exception
            r12 = 0
            r13 = 0
            goto L_0x33b4
        L_0x32d5:
            android.text.StaticLayout r6 = new android.text.StaticLayout     // Catch:{ Exception -> 0x33b1 }
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint     // Catch:{ Exception -> 0x33b1 }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x33b1 }
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r6
            r30 = r4
            r32 = r12
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x33b1 }
            r1.captionLayout = r6     // Catch:{ Exception -> 0x33b1 }
        L_0x32ec:
            android.text.StaticLayout r4 = r1.captionLayout     // Catch:{ Exception -> 0x33b1 }
            int r4 = r4.getLineCount()     // Catch:{ Exception -> 0x33b1 }
            if (r4 <= 0) goto L_0x33aa
            if (r2 == 0) goto L_0x332a
            r6 = 0
            r1.captionWidth = r6     // Catch:{ Exception -> 0x33b1 }
            r6 = 0
        L_0x32fa:
            if (r6 >= r4) goto L_0x3322
            int r7 = r1.captionWidth     // Catch:{ Exception -> 0x33b1 }
            double r7 = (double) r7     // Catch:{ Exception -> 0x33b1 }
            android.text.StaticLayout r9 = r1.captionLayout     // Catch:{ Exception -> 0x33b1 }
            float r9 = r9.getLineWidth(r6)     // Catch:{ Exception -> 0x33b1 }
            double r9 = (double) r9     // Catch:{ Exception -> 0x33b1 }
            double r9 = java.lang.Math.ceil(r9)     // Catch:{ Exception -> 0x33b1 }
            double r7 = java.lang.Math.max(r7, r9)     // Catch:{ Exception -> 0x33b1 }
            int r7 = (int) r7     // Catch:{ Exception -> 0x33b1 }
            r1.captionWidth = r7     // Catch:{ Exception -> 0x33b1 }
            android.text.StaticLayout r7 = r1.captionLayout     // Catch:{ Exception -> 0x33b1 }
            float r7 = r7.getLineLeft(r6)     // Catch:{ Exception -> 0x33b1 }
            r13 = 0
            int r7 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r7 == 0) goto L_0x331f
            r1.captionWidth = r12     // Catch:{ Exception -> 0x33a8 }
            goto L_0x3323
        L_0x331f:
            int r6 = r6 + 1
            goto L_0x32fa
        L_0x3322:
            r13 = 0
        L_0x3323:
            int r4 = r1.captionWidth     // Catch:{ Exception -> 0x33a8 }
            if (r4 <= r12) goto L_0x332d
            r1.captionWidth = r12     // Catch:{ Exception -> 0x33a8 }
            goto L_0x332d
        L_0x332a:
            r13 = 0
            r1.captionWidth = r12     // Catch:{ Exception -> 0x33a8 }
        L_0x332d:
            android.text.StaticLayout r4 = r1.captionLayout     // Catch:{ Exception -> 0x33a8 }
            int r4 = r4.getHeight()     // Catch:{ Exception -> 0x33a8 }
            r1.captionHeight = r4     // Catch:{ Exception -> 0x33a8 }
            int r4 = r1.captionHeight     // Catch:{ Exception -> 0x33a8 }
            r6 = 1091567616(0x41100000, float:9.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x33a8 }
            int r4 = r4 + r6
            r1.addedCaptionHeight = r4     // Catch:{ Exception -> 0x33a8 }
            org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = r1.currentPosition     // Catch:{ Exception -> 0x33a8 }
            if (r4 == 0) goto L_0x3352
            org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = r1.currentPosition     // Catch:{ Exception -> 0x33a8 }
            int r4 = r4.flags     // Catch:{ Exception -> 0x33a8 }
            r6 = 8
            r4 = r4 & r6
            if (r4 == 0) goto L_0x334e
            goto L_0x3352
        L_0x334e:
            r4 = 0
            r1.captionLayout = r4     // Catch:{ Exception -> 0x33a8 }
            goto L_0x33ab
        L_0x3352:
            int r4 = r1.addedCaptionHeight     // Catch:{ Exception -> 0x33a8 }
            r6 = 0
            int r12 = r6 + r4
            int r4 = r1.captionWidth     // Catch:{ Exception -> 0x33a6 }
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x33a6 }
            int r6 = r5 - r6
            int r4 = java.lang.Math.max(r4, r6)     // Catch:{ Exception -> 0x33a6 }
            android.text.StaticLayout r6 = r1.captionLayout     // Catch:{ Exception -> 0x33a6 }
            android.text.StaticLayout r7 = r1.captionLayout     // Catch:{ Exception -> 0x33a6 }
            int r7 = r7.getLineCount()     // Catch:{ Exception -> 0x33a6 }
            r8 = 1
            int r7 = r7 - r8
            float r6 = r6.getLineWidth(r7)     // Catch:{ Exception -> 0x33a6 }
            android.text.StaticLayout r7 = r1.captionLayout     // Catch:{ Exception -> 0x33a6 }
            android.text.StaticLayout r9 = r1.captionLayout     // Catch:{ Exception -> 0x33a6 }
            int r9 = r9.getLineCount()     // Catch:{ Exception -> 0x33a6 }
            int r9 = r9 - r8
            float r7 = r7.getLineLeft(r9)     // Catch:{ Exception -> 0x33a6 }
            float r6 = r6 + r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x33a6 }
            int r4 = r4 + r7
            float r4 = (float) r4     // Catch:{ Exception -> 0x33a6 }
            float r4 = r4 - r6
            float r0 = (float) r0     // Catch:{ Exception -> 0x33a6 }
            int r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x339f
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x33a6 }
            int r12 = r12 + r0
            int r0 = r1.addedCaptionHeight     // Catch:{ Exception -> 0x33a6 }
            r4 = 1096810496(0x41600000, float:14.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x33a6 }
            int r0 = r0 + r4
            r1.addedCaptionHeight = r0     // Catch:{ Exception -> 0x33a6 }
            r0 = 1
            goto L_0x33a0
        L_0x339f:
            r0 = 0
        L_0x33a0:
            r57 = r12
            r12 = r0
            r0 = r57
            goto L_0x33ad
        L_0x33a6:
            r0 = move-exception
            goto L_0x33b4
        L_0x33a8:
            r0 = move-exception
            goto L_0x33b3
        L_0x33aa:
            r13 = 0
        L_0x33ab:
            r0 = 0
            r12 = 0
        L_0x33ad:
            r21 = r0
            r0 = r12
            goto L_0x33bf
        L_0x33b1:
            r0 = move-exception
            r13 = 0
        L_0x33b3:
            r12 = 0
        L_0x33b4:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r21 = r12
            r0 = 0
            goto L_0x33bf
        L_0x33bb:
            r13 = 0
            r0 = 0
            r21 = 0
        L_0x33bf:
            if (r2 == 0) goto L_0x33ec
            int r2 = r1.captionWidth
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r2 = r2 + r4
            if (r5 >= r2) goto L_0x33ec
            int r2 = r1.captionWidth
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r2 = r2 + r4
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r2
            r1.backgroundWidth = r4
            boolean r4 = r1.mediaBackground
            if (r4 != 0) goto L_0x33e9
            int r4 = r1.backgroundWidth
            r5 = 1091567616(0x41100000, float:9.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 + r5
            r1.backgroundWidth = r4
        L_0x33e9:
            r22 = r2
            goto L_0x33ee
        L_0x33ec:
            r22 = r5
        L_0x33ee:
            java.util.Locale r2 = java.util.Locale.US
            r4 = 2
            java.lang.Object[] r5 = new java.lang.Object[r4]
            float r3 = (float) r3
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 / r4
            int r3 = (int) r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 0
            r5[r4] = r3
            float r3 = (float) r15
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 / r4
            int r3 = (int) r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 1
            r5[r4] = r3
            java.lang.String r3 = "%d_%d"
            java.lang.String r2 = java.lang.String.format(r2, r3, r5)
            r1.currentPhotoFilterThumb = r2
            r1.currentPhotoFilter = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r14.photoThumbs
            if (r2 == 0) goto L_0x341f
            int r2 = r2.size()
            if (r2 > r4) goto L_0x342b
        L_0x341f:
            int r2 = r14.type
            r3 = 3
            if (r2 == r3) goto L_0x342b
            r3 = 8
            if (r2 == r3) goto L_0x342b
            r3 = 5
            if (r2 != r3) goto L_0x3471
        L_0x342b:
            boolean r2 = r59.needDrawBluredPreview()
            if (r2 == 0) goto L_0x345c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r1.currentPhotoFilter
            r2.append(r3)
            java.lang.String r3 = "_b2"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.currentPhotoFilter = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r1.currentPhotoFilterThumb
            r2.append(r3)
            java.lang.String r3 = "_b2"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.currentPhotoFilterThumb = r2
            goto L_0x3471
        L_0x345c:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r1.currentPhotoFilterThumb
            r2.append(r3)
            java.lang.String r3 = "_b"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.currentPhotoFilterThumb = r2
        L_0x3471:
            int r2 = r14.type
            r3 = 3
            if (r2 == r3) goto L_0x3480
            r3 = 8
            if (r2 == r3) goto L_0x3480
            r3 = 5
            if (r2 != r3) goto L_0x347e
            goto L_0x3480
        L_0x347e:
            r2 = 0
            goto L_0x3481
        L_0x3480:
            r2 = 1
        L_0x3481:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            if (r3 == 0) goto L_0x348f
            if (r2 != 0) goto L_0x348f
            int r4 = r3.size
            if (r4 != 0) goto L_0x348f
            r10 = -1
            r3.size = r10
            goto L_0x3490
        L_0x348f:
            r10 = -1
        L_0x3490:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObjectThumb
            if (r3 == 0) goto L_0x349c
            if (r2 != 0) goto L_0x349c
            int r4 = r3.size
            if (r4 != 0) goto L_0x349c
            r3.size = r10
        L_0x349c:
            boolean r3 = org.telegram.messenger.SharedConfig.autoplayVideo
            if (r3 == 0) goto L_0x34dd
            int r3 = r14.type
            r12 = 3
            if (r3 != r12) goto L_0x34de
            boolean r3 = r59.needDrawBluredPreview()
            if (r3 != 0) goto L_0x34de
            org.telegram.messenger.MessageObject r3 = r1.currentMessageObject
            boolean r3 = r3.mediaExists
            if (r3 != 0) goto L_0x34c5
            boolean r3 = r59.canStreamVideo()
            if (r3 == 0) goto L_0x34de
            int r3 = r1.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            boolean r3 = r3.canDownloadMedia((org.telegram.messenger.MessageObject) r4)
            if (r3 == 0) goto L_0x34de
        L_0x34c5:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = r1.currentPosition
            if (r3 == 0) goto L_0x34d9
            int r3 = r3.flags
            r4 = r3 & 1
            if (r4 == 0) goto L_0x34d5
            r4 = 2
            r3 = r3 & r4
            if (r3 == 0) goto L_0x34d5
            r3 = 1
            goto L_0x34d6
        L_0x34d5:
            r3 = 0
        L_0x34d6:
            r1.autoPlayingMedia = r3
            goto L_0x34de
        L_0x34d9:
            r3 = 1
            r1.autoPlayingMedia = r3
            goto L_0x34de
        L_0x34dd:
            r12 = 3
        L_0x34de:
            org.telegram.tgnet.TLRPC$Document r3 = r59.getDocument()
            boolean r3 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r3)
            if (r3 == 0) goto L_0x3538
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObjectThumb
            if (r3 == 0) goto L_0x3528
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r3 == 0) goto L_0x3528
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObjectThumb
            java.lang.String r4 = org.telegram.messenger.ImageLocation.getStippedKey(r4, r4, r5)
            r3.append(r4)
            java.lang.String r4 = "@"
            r3.append(r4)
            java.lang.String r4 = r1.currentPhotoFilterThumb
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.getInstance()
            r11 = 0
            boolean r3 = r4.isInMemCache(r3, r11)
            if (r3 != 0) goto L_0x3539
            org.telegram.tgnet.TLRPC$Document r3 = r59.getDocument()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            r4 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4)
            r1.currentPhotoObjectThumb = r3
            goto L_0x3539
        L_0x3528:
            r11 = 0
            org.telegram.tgnet.TLRPC$Document r3 = r59.getDocument()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            r4 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4)
            r1.currentPhotoObjectThumb = r3
            goto L_0x3539
        L_0x3538:
            r11 = 0
        L_0x3539:
            boolean r3 = r1.autoPlayingMedia
            if (r3 == 0) goto L_0x358b
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r3 = 1
            r2.setAllowStartAnimation(r3)
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r2.startAnimation()
            org.telegram.tgnet.TLRPC$Document r2 = r59.getDocument()
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObject
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            java.lang.String r6 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObjectThumb
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForDocument(r7, r2)
            java.lang.String r8 = r1.currentPhotoFilterThumb
            r9 = 0
            org.telegram.tgnet.TLRPC$Document r2 = r59.getDocument()
            int r2 = r2.size
            r23 = 0
            r25 = 0
            java.lang.String r27 = "g"
            r29 = r2
            r2 = r3
            r3 = r4
            r4 = r27
            r10 = r29
            r24 = 1065353216(0x3var_, float:1.0)
            r27 = 0
            r11 = r23
            r12 = r59
            r23 = r15
            r15 = 0
            r13 = r25
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x3788
        L_0x358b:
            r23 = r15
            r15 = 0
            r24 = 1065353216(0x3var_, float:1.0)
            int r3 = r14.type
            r4 = 1
            if (r3 != r4) goto L_0x3645
            boolean r3 = r14.useCustomPhoto
            if (r3 == 0) goto L_0x35ab
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            android.content.res.Resources r3 = r58.getResources()
            r4 = 2131165900(0x7var_cc, float:1.794603E38)
            android.graphics.drawable.Drawable r3 = r3.getDrawable(r4)
            r2.setImageBitmap((android.graphics.drawable.Drawable) r3)
            goto L_0x3788
        L_0x35ab:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            if (r3 == 0) goto L_0x363d
            java.lang.String r3 = org.telegram.messenger.FileLoader.getAttachFileName(r3)
            boolean r4 = r14.mediaExists
            if (r4 == 0) goto L_0x35c2
            int r4 = r1.currentAccount
            org.telegram.messenger.DownloadController r4 = org.telegram.messenger.DownloadController.getInstance(r4)
            r4.removeLoadingFileObserver(r1)
            r4 = 1
            goto L_0x35c3
        L_0x35c2:
            r4 = 0
        L_0x35c3:
            if (r4 != 0) goto L_0x360d
            int r4 = r1.currentAccount
            org.telegram.messenger.DownloadController r4 = org.telegram.messenger.DownloadController.getInstance(r4)
            org.telegram.messenger.MessageObject r5 = r1.currentMessageObject
            boolean r4 = r4.canDownloadMedia((org.telegram.messenger.MessageObject) r5)
            if (r4 != 0) goto L_0x360d
            int r4 = r1.currentAccount
            org.telegram.messenger.FileLoader r4 = org.telegram.messenger.FileLoader.getInstance(r4)
            boolean r3 = r4.isLoadingFile(r3)
            if (r3 == 0) goto L_0x35e0
            goto L_0x360d
        L_0x35e0:
            r3 = 1
            r1.photoNotSet = r3
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r1.currentPhotoObjectThumb
            if (r2 == 0) goto L_0x3605
            org.telegram.messenger.ImageReceiver r3 = r1.photoImage
            r4 = 0
            r5 = 0
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForObject(r2, r6)
            java.lang.String r7 = r1.currentPhotoFilterThumb
            r8 = 0
            r9 = 0
            org.telegram.messenger.MessageObject r10 = r1.currentMessageObject
            boolean r2 = r10.shouldEncryptPhotoOrVideo()
            if (r2 == 0) goto L_0x35ff
            r11 = 2
            goto L_0x3600
        L_0x35ff:
            r11 = 0
        L_0x3600:
            r3.setImage(r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x3788
        L_0x3605:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r3 = 0
            r2.setImageBitmap((android.graphics.drawable.Drawable) r3)
            goto L_0x3788
        L_0x360d:
            org.telegram.messenger.ImageReceiver r4 = r1.photoImage
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            org.telegram.tgnet.TLObject r5 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r3, r5)
            java.lang.String r6 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r7 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForObject(r3, r7)
            java.lang.String r8 = r1.currentPhotoFilterThumb
            if (r2 == 0) goto L_0x3627
            r9 = 0
            goto L_0x362c
        L_0x3627:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r1.currentPhotoObject
            int r12 = r2.size
            r9 = r12
        L_0x362c:
            r10 = 0
            org.telegram.messenger.MessageObject r11 = r1.currentMessageObject
            boolean r2 = r11.shouldEncryptPhotoOrVideo()
            if (r2 == 0) goto L_0x3637
            r12 = 2
            goto L_0x3638
        L_0x3637:
            r12 = 0
        L_0x3638:
            r4.setImage(r5, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x3788
        L_0x363d:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            r3 = 0
            r2.setImageBitmap((android.graphics.drawable.Drawable) r3)
            goto L_0x3788
        L_0x3645:
            r2 = 8
            if (r3 == r2) goto L_0x3677
            r2 = 5
            if (r3 != r2) goto L_0x364d
            goto L_0x3677
        L_0x364d:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            org.telegram.tgnet.TLObject r4 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForObject(r3, r4)
            java.lang.String r4 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            java.lang.String r6 = r1.currentPhotoFilterThumb
            r7 = 0
            r8 = 0
            org.telegram.messenger.MessageObject r9 = r1.currentMessageObject
            boolean r9 = r9.shouldEncryptPhotoOrVideo()
            if (r9 == 0) goto L_0x366f
            r10 = 2
            goto L_0x3670
        L_0x366f:
            r10 = 0
        L_0x3670:
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x3788
        L_0x3677:
            org.telegram.tgnet.TLRPC$Document r2 = r59.getDocument()
            java.lang.String r2 = org.telegram.messenger.FileLoader.getAttachFileName(r2)
            boolean r3 = r14.attachPathExists
            if (r3 == 0) goto L_0x368e
            int r3 = r1.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            r3.removeLoadingFileObserver(r1)
            r3 = 1
            goto L_0x3695
        L_0x368e:
            boolean r3 = r14.mediaExists
            if (r3 == 0) goto L_0x3694
            r3 = 2
            goto L_0x3695
        L_0x3694:
            r3 = 0
        L_0x3695:
            org.telegram.tgnet.TLRPC$Document r4 = r59.getDocument()
            boolean r4 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r4)
            if (r4 != 0) goto L_0x36a7
            int r4 = r14.type
            r5 = 5
            if (r4 != r5) goto L_0x36a5
            goto L_0x36a7
        L_0x36a5:
            r12 = 0
            goto L_0x36b3
        L_0x36a7:
            int r4 = r1.currentAccount
            org.telegram.messenger.DownloadController r4 = org.telegram.messenger.DownloadController.getInstance(r4)
            org.telegram.messenger.MessageObject r5 = r1.currentMessageObject
            boolean r12 = r4.canDownloadMedia((org.telegram.messenger.MessageObject) r5)
        L_0x36b3:
            boolean r4 = r59.isSending()
            if (r4 != 0) goto L_0x376a
            boolean r4 = r59.isEditing()
            if (r4 != 0) goto L_0x376a
            if (r3 != 0) goto L_0x36cf
            int r4 = r1.currentAccount
            org.telegram.messenger.FileLoader r4 = org.telegram.messenger.FileLoader.getInstance(r4)
            boolean r2 = r4.isLoadingFile(r2)
            if (r2 != 0) goto L_0x36cf
            if (r12 == 0) goto L_0x376a
        L_0x36cf:
            r2 = 1
            if (r3 == r2) goto L_0x3714
            boolean r4 = r59.needDrawBluredPreview()
            if (r4 != 0) goto L_0x3714
            if (r3 != 0) goto L_0x36e2
            boolean r4 = r59.canStreamVideo()
            if (r4 == 0) goto L_0x3714
            if (r12 == 0) goto L_0x3714
        L_0x36e2:
            r1.autoPlayingMedia = r2
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.tgnet.TLRPC$Document r3 = r59.getDocument()
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r1.currentPhotoObject
            org.telegram.tgnet.TLObject r5 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r4, r5)
            java.lang.String r6 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r7 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForObject(r4, r7)
            java.lang.String r8 = r1.currentPhotoFilterThumb
            r9 = 0
            org.telegram.tgnet.TLRPC$Document r4 = r59.getDocument()
            int r10 = r4.size
            r11 = 0
            r13 = 0
            java.lang.String r4 = "g"
            r12 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x3788
        L_0x3714:
            if (r3 != r2) goto L_0x373c
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            boolean r3 = r59.isSendError()
            if (r3 == 0) goto L_0x3720
            r3 = 0
            goto L_0x3724
        L_0x3720:
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            java.lang.String r3 = r3.attachPath
        L_0x3724:
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForPath(r3)
            r4 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            java.lang.String r6 = r1.currentPhotoFilterThumb
            r7 = 0
            r8 = 0
            r10 = 0
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x3788
        L_0x373c:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.tgnet.TLRPC$Document r3 = r59.getDocument()
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            r4 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObject
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            java.lang.String r6 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r8 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForObject(r7, r8)
            java.lang.String r8 = r1.currentPhotoFilterThumb
            r9 = 0
            org.telegram.tgnet.TLRPC$Document r10 = r59.getDocument()
            int r10 = r10.size
            r11 = 0
            r13 = 0
            r12 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x3788
        L_0x376a:
            org.telegram.messenger.ImageReceiver r2 = r1.photoImage
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            org.telegram.tgnet.TLObject r4 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForObject(r3, r4)
            java.lang.String r4 = r1.currentPhotoFilter
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r1.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r6 = r1.photoParentObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            java.lang.String r6 = r1.currentPhotoFilterThumb
            r7 = 0
            r8 = 0
            r10 = 0
            r9 = r59
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
        L_0x3788:
            r12 = r0
            r56 = r21
            r0 = r22
            r3 = r23
        L_0x378f:
            r58.setMessageObjectInternal(r59)
            boolean r2 = r1.drawForwardedName
            if (r2 == 0) goto L_0x37b5
            boolean r2 = r59.needDrawForwarded()
            if (r2 == 0) goto L_0x37b5
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r1.currentPosition
            if (r2 == 0) goto L_0x37a4
            byte r2 = r2.minY
            if (r2 != 0) goto L_0x37b5
        L_0x37a4:
            int r2 = r14.type
            r4 = 5
            if (r2 == r4) goto L_0x37ca
            int r2 = r1.namesOffset
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r4
            r1.namesOffset = r2
            goto L_0x37ca
        L_0x37b5:
            boolean r2 = r1.drawNameLayout
            if (r2 == 0) goto L_0x37ca
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            int r2 = r2.reply_to_msg_id
            if (r2 != 0) goto L_0x37ca
            int r2 = r1.namesOffset
            r4 = 1088421888(0x40e00000, float:7.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r4
            r1.namesOffset = r2
        L_0x37ca:
            r2 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r2 + r3
            int r4 = r1.namesOffset
            int r2 = r2 + r4
            int r2 = r2 + r56
            r1.totalHeight = r2
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r1.currentPosition
            if (r2 == 0) goto L_0x37ee
            int r2 = r2.flags
            r4 = 8
            r2 = r2 & r4
            if (r2 != 0) goto L_0x37ee
            int r2 = r1.totalHeight
            r4 = 1077936128(0x40400000, float:3.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 - r4
            r1.totalHeight = r2
        L_0x37ee:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r1.currentPosition
            if (r2 == 0) goto L_0x381f
            int r2 = r1.getAdditionalWidthForPosition(r2)
            int r0 = r0 + r2
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r1.currentPosition
            int r2 = r2.flags
            r2 = r2 & 4
            if (r2 != 0) goto L_0x380f
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 + r2
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = 0 - r2
            goto L_0x3810
        L_0x380f:
            r2 = 0
        L_0x3810:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = r1.currentPosition
            int r4 = r4.flags
            r5 = 8
            r4 = r4 & r5
            if (r4 != 0) goto L_0x3820
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r3 = r3 + r4
            goto L_0x3820
        L_0x381f:
            r2 = 0
        L_0x3820:
            boolean r4 = r1.drawPinnedTop
            if (r4 == 0) goto L_0x382d
            int r4 = r1.namesOffset
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r4 = r4 - r5
            r1.namesOffset = r4
        L_0x382d:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = r1.currentPosition
            if (r4 == 0) goto L_0x3857
            int r4 = r1.namesOffset
            if (r4 <= 0) goto L_0x3845
            r4 = 1088421888(0x40e00000, float:7.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r1.totalHeight
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r5 - r6
            r1.totalHeight = r5
            goto L_0x387c
        L_0x3845:
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r1.totalHeight
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r1.totalHeight = r5
            goto L_0x387c
        L_0x3857:
            int r4 = r1.namesOffset
            if (r4 <= 0) goto L_0x386b
            r4 = 1088421888(0x40e00000, float:7.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r1.totalHeight
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r5 = r5 - r6
            r1.totalHeight = r5
            goto L_0x387c
        L_0x386b:
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r1.totalHeight
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r1.totalHeight = r5
        L_0x387c:
            org.telegram.messenger.ImageReceiver r5 = r1.photoImage
            int r6 = r1.namesOffset
            int r4 = r4 + r6
            int r4 = r4 + r2
            r5.setImageCoords(r15, r4, r0, r3)
            r58.invalidate()
            r9 = r12
        L_0x3889:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r1.currentPosition
            if (r0 != 0) goto L_0x397f
            boolean r0 = r59.isAnyKindOfSticker()
            if (r0 != 0) goto L_0x397f
            int r0 = r1.addedCaptionHeight
            if (r0 != 0) goto L_0x397f
            android.text.StaticLayout r0 = r1.captionLayout
            if (r0 != 0) goto L_0x38f7
            java.lang.CharSequence r0 = r14.caption
            if (r0 == 0) goto L_0x38f7
            r1.currentCaption = r0     // Catch:{ Exception -> 0x38f3 }
            int r0 = r1.backgroundWidth     // Catch:{ Exception -> 0x38f3 }
            r2 = 1106771968(0x41var_, float:31.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x38f3 }
            int r0 = r0 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x38f3 }
            int r0 = r0 - r2
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x38f3 }
            r3 = 24
            if (r2 < r3) goto L_0x38d9
            java.lang.CharSequence r2 = r14.caption     // Catch:{ Exception -> 0x38f3 }
            java.lang.CharSequence r3 = r14.caption     // Catch:{ Exception -> 0x38f3 }
            int r3 = r3.length()     // Catch:{ Exception -> 0x38f3 }
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint     // Catch:{ Exception -> 0x38f3 }
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r2, r15, r3, r4, r0)     // Catch:{ Exception -> 0x38f3 }
            r2 = 1
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r2)     // Catch:{ Exception -> 0x38f3 }
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r15)     // Catch:{ Exception -> 0x38f3 }
            android.text.Layout$Alignment r2 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x38f3 }
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r2)     // Catch:{ Exception -> 0x38f3 }
            android.text.StaticLayout r0 = r0.build()     // Catch:{ Exception -> 0x38f3 }
            r1.captionLayout = r0     // Catch:{ Exception -> 0x38f3 }
            goto L_0x38f7
        L_0x38d9:
            android.text.StaticLayout r2 = new android.text.StaticLayout     // Catch:{ Exception -> 0x38f3 }
            java.lang.CharSequence r3 = r14.caption     // Catch:{ Exception -> 0x38f3 }
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint     // Catch:{ Exception -> 0x38f3 }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x38f3 }
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r2
            r30 = r3
            r32 = r0
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x38f3 }
            r1.captionLayout = r2     // Catch:{ Exception -> 0x38f3 }
            goto L_0x38f7
        L_0x38f3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x38f7:
            android.text.StaticLayout r0 = r1.captionLayout
            if (r0 == 0) goto L_0x397f
            int r0 = r1.backgroundWidth     // Catch:{ Exception -> 0x397b }
            r2 = 1106771968(0x41var_, float:31.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x397b }
            int r0 = r0 - r2
            android.text.StaticLayout r2 = r1.captionLayout     // Catch:{ Exception -> 0x397b }
            if (r2 == 0) goto L_0x397f
            android.text.StaticLayout r2 = r1.captionLayout     // Catch:{ Exception -> 0x397b }
            int r2 = r2.getLineCount()     // Catch:{ Exception -> 0x397b }
            if (r2 <= 0) goto L_0x397f
            r1.captionWidth = r0     // Catch:{ Exception -> 0x397b }
            int r2 = r1.timeWidth     // Catch:{ Exception -> 0x397b }
            boolean r3 = r59.isOutOwner()     // Catch:{ Exception -> 0x397b }
            if (r3 == 0) goto L_0x3921
            r3 = 1101004800(0x41a00000, float:20.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x397b }
            goto L_0x3922
        L_0x3921:
            r12 = 0
        L_0x3922:
            int r2 = r2 + r12
            android.text.StaticLayout r3 = r1.captionLayout     // Catch:{ Exception -> 0x397b }
            int r3 = r3.getHeight()     // Catch:{ Exception -> 0x397b }
            r1.captionHeight = r3     // Catch:{ Exception -> 0x397b }
            int r3 = r1.totalHeight     // Catch:{ Exception -> 0x397b }
            int r4 = r1.captionHeight     // Catch:{ Exception -> 0x397b }
            r5 = 1091567616(0x41100000, float:9.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x397b }
            int r4 = r4 + r5
            int r3 = r3 + r4
            r1.totalHeight = r3     // Catch:{ Exception -> 0x397b }
            android.text.StaticLayout r3 = r1.captionLayout     // Catch:{ Exception -> 0x397b }
            android.text.StaticLayout r4 = r1.captionLayout     // Catch:{ Exception -> 0x397b }
            int r4 = r4.getLineCount()     // Catch:{ Exception -> 0x397b }
            r5 = 1
            int r4 = r4 - r5
            float r3 = r3.getLineWidth(r4)     // Catch:{ Exception -> 0x397b }
            android.text.StaticLayout r4 = r1.captionLayout     // Catch:{ Exception -> 0x397b }
            android.text.StaticLayout r6 = r1.captionLayout     // Catch:{ Exception -> 0x397b }
            int r6 = r6.getLineCount()     // Catch:{ Exception -> 0x397b }
            int r6 = r6 - r5
            float r4 = r4.getLineLeft(r6)     // Catch:{ Exception -> 0x397b }
            float r3 = r3 + r4
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x397b }
            int r0 = r0 - r4
            float r0 = (float) r0     // Catch:{ Exception -> 0x397b }
            float r0 = r0 - r3
            float r2 = (float) r2     // Catch:{ Exception -> 0x397b }
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x397f
            int r0 = r1.totalHeight     // Catch:{ Exception -> 0x397b }
            r2 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x397b }
            int r0 = r0 + r2
            r1.totalHeight = r0     // Catch:{ Exception -> 0x397b }
            int r0 = r1.captionHeight     // Catch:{ Exception -> 0x397b }
            r2 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x397b }
            int r0 = r0 + r2
            r1.captionHeight = r0     // Catch:{ Exception -> 0x397b }
            r9 = 2
            goto L_0x397f
        L_0x397b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x397f:
            android.text.StaticLayout r0 = r1.captionLayout
            if (r0 != 0) goto L_0x399a
            int r0 = r1.widthBeforeNewTimeLine
            r2 = -1
            if (r0 == r2) goto L_0x399a
            int r2 = r1.availableTimeWidth
            int r2 = r2 - r0
            int r0 = r1.timeWidth
            if (r2 >= r0) goto L_0x399a
            int r0 = r1.totalHeight
            r2 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.totalHeight = r0
        L_0x399a:
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            long r2 = r0.eventId
            r4 = 0
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x3ac2
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x3ac2
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x3ac2
            int r0 = r1.backgroundWidth
            r2 = 1109655552(0x42240000, float:41.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r0 - r2
            r3 = 1
            r1.hasOldCaptionPreview = r3
            r1.linkPreviewHeight = r15
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r3 = r0.webpage
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint     // Catch:{ Exception -> 0x3a21 }
            java.lang.String r4 = r3.site_name     // Catch:{ Exception -> 0x3a21 }
            float r0 = r0.measureText(r4)     // Catch:{ Exception -> 0x3a21 }
            float r0 = r0 + r24
            double r4 = (double) r0     // Catch:{ Exception -> 0x3a21 }
            double r4 = java.lang.Math.ceil(r4)     // Catch:{ Exception -> 0x3a21 }
            int r0 = (int) r4     // Catch:{ Exception -> 0x3a21 }
            r1.siteNameWidth = r0     // Catch:{ Exception -> 0x3a21 }
            android.text.StaticLayout r4 = new android.text.StaticLayout     // Catch:{ Exception -> 0x3a21 }
            java.lang.String r5 = r3.site_name     // Catch:{ Exception -> 0x3a21 }
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint     // Catch:{ Exception -> 0x3a21 }
            int r32 = java.lang.Math.min(r0, r2)     // Catch:{ Exception -> 0x3a21 }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x3a21 }
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r4
            r30 = r5
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x3a21 }
            r1.siteNameLayout = r4     // Catch:{ Exception -> 0x3a21 }
            android.text.StaticLayout r0 = r1.siteNameLayout     // Catch:{ Exception -> 0x3a21 }
            float r0 = r0.getLineLeft(r15)     // Catch:{ Exception -> 0x3a21 }
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x3a05
            r0 = 1
            goto L_0x3a06
        L_0x3a05:
            r0 = 0
        L_0x3a06:
            r1.siteNameRtl = r0     // Catch:{ Exception -> 0x3a21 }
            android.text.StaticLayout r0 = r1.siteNameLayout     // Catch:{ Exception -> 0x3a21 }
            android.text.StaticLayout r4 = r1.siteNameLayout     // Catch:{ Exception -> 0x3a21 }
            int r4 = r4.getLineCount()     // Catch:{ Exception -> 0x3a21 }
            r5 = 1
            int r4 = r4 - r5
            int r0 = r0.getLineBottom(r4)     // Catch:{ Exception -> 0x3a21 }
            int r4 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x3a21 }
            int r4 = r4 + r0
            r1.linkPreviewHeight = r4     // Catch:{ Exception -> 0x3a21 }
            int r4 = r1.totalHeight     // Catch:{ Exception -> 0x3a21 }
            int r4 = r4 + r0
            r1.totalHeight = r4     // Catch:{ Exception -> 0x3a21 }
            goto L_0x3a25
        L_0x3a21:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x3a25:
            r1.descriptionX = r15     // Catch:{ Exception -> 0x3a98 }
            int r0 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x3a98 }
            if (r0 == 0) goto L_0x3a34
            int r0 = r1.totalHeight     // Catch:{ Exception -> 0x3a98 }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x3a98 }
            int r0 = r0 + r4
            r1.totalHeight = r0     // Catch:{ Exception -> 0x3a98 }
        L_0x3a34:
            java.lang.String r0 = r3.description     // Catch:{ Exception -> 0x3a98 }
            android.text.TextPaint r30 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint     // Catch:{ Exception -> 0x3a98 }
            android.text.Layout$Alignment r32 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x3a98 }
            r33 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r24)     // Catch:{ Exception -> 0x3a98 }
            float r3 = (float) r3     // Catch:{ Exception -> 0x3a98 }
            r35 = 0
            android.text.TextUtils$TruncateAt r36 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x3a98 }
            r38 = 6
            r29 = r0
            r31 = r2
            r34 = r3
            r37 = r2
            android.text.StaticLayout r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r29, r30, r31, r32, r33, r34, r35, r36, r37, r38)     // Catch:{ Exception -> 0x3a98 }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x3a98 }
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x3a98 }
            android.text.StaticLayout r2 = r1.descriptionLayout     // Catch:{ Exception -> 0x3a98 }
            int r2 = r2.getLineCount()     // Catch:{ Exception -> 0x3a98 }
            r3 = 1
            int r2 = r2 - r3
            int r0 = r0.getLineBottom(r2)     // Catch:{ Exception -> 0x3a98 }
            int r2 = r1.linkPreviewHeight     // Catch:{ Exception -> 0x3a98 }
            int r2 = r2 + r0
            r1.linkPreviewHeight = r2     // Catch:{ Exception -> 0x3a98 }
            int r2 = r1.totalHeight     // Catch:{ Exception -> 0x3a98 }
            int r2 = r2 + r0
            r1.totalHeight = r2     // Catch:{ Exception -> 0x3a98 }
            r0 = 0
        L_0x3a6e:
            android.text.StaticLayout r2 = r1.descriptionLayout     // Catch:{ Exception -> 0x3a98 }
            int r2 = r2.getLineCount()     // Catch:{ Exception -> 0x3a98 }
            if (r0 >= r2) goto L_0x3a9c
            android.text.StaticLayout r2 = r1.descriptionLayout     // Catch:{ Exception -> 0x3a98 }
            float r2 = r2.getLineLeft(r0)     // Catch:{ Exception -> 0x3a98 }
            double r2 = (double) r2     // Catch:{ Exception -> 0x3a98 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x3a98 }
            int r2 = (int) r2     // Catch:{ Exception -> 0x3a98 }
            if (r2 == 0) goto L_0x3a95
            int r3 = r1.descriptionX     // Catch:{ Exception -> 0x3a98 }
            if (r3 != 0) goto L_0x3a8c
            int r2 = -r2
            r1.descriptionX = r2     // Catch:{ Exception -> 0x3a98 }
            goto L_0x3a95
        L_0x3a8c:
            int r3 = r1.descriptionX     // Catch:{ Exception -> 0x3a98 }
            int r2 = -r2
            int r2 = java.lang.Math.max(r3, r2)     // Catch:{ Exception -> 0x3a98 }
            r1.descriptionX = r2     // Catch:{ Exception -> 0x3a98 }
        L_0x3a95:
            int r0 = r0 + 1
            goto L_0x3a6e
        L_0x3a98:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x3a9c:
            int r0 = r1.totalHeight
            r2 = 1099431936(0x41880000, float:17.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.totalHeight = r0
            if (r9 == 0) goto L_0x3ac2
            int r0 = r1.totalHeight
            r2 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r1.totalHeight = r0
            r2 = 2
            if (r9 != r2) goto L_0x3ac2
            int r0 = r1.captionHeight
            r2 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            r1.captionHeight = r0
        L_0x3ac2:
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$BotButton> r0 = r1.botButtons
            r0.clear()
            if (r16 == 0) goto L_0x3ad6
            java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton> r0 = r1.botButtonsByData
            r0.clear()
            java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton> r0 = r1.botButtonsByPosition
            r0.clear()
            r2 = 0
            r1.botButtonsLayout = r2
        L_0x3ad6:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r1.currentPosition
            if (r0 != 0) goto L_0x3df9
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r0.reply_markup
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup
            if (r2 != 0) goto L_0x3aee
            org.telegram.tgnet.TLRPC$TL_messageReactions r0 = r0.reactions
            if (r0 == 0) goto L_0x3df9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_reactionCount> r0 = r0.results
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x3df9
        L_0x3aee:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup
            if (r2 == 0) goto L_0x3afd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r13 = r0.size()
            goto L_0x3afe
        L_0x3afd:
            r13 = 1
        L_0x3afe:
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = r0 * r13
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 + r2
            r1.keyboardHeight = r0
            r1.substractBackgroundHeight = r0
            int r0 = r1.backgroundWidth
            boolean r2 = r1.mediaBackground
            if (r2 == 0) goto L_0x3b17
            r10 = 0
            goto L_0x3b19
        L_0x3b17:
            r10 = 1091567616(0x41100000, float:9.0)
        L_0x3b19:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r0 = r0 - r2
            r1.widthForButtons = r0
            int r0 = r14.wantedBotKeyboardWidth
            int r2 = r1.widthForButtons
            if (r0 <= r2) goto L_0x3b6b
            boolean r0 = r1.isChat
            if (r0 == 0) goto L_0x3b39
            boolean r0 = r59.needDrawAvatar()
            if (r0 == 0) goto L_0x3b39
            boolean r0 = r59.isOutOwner()
            if (r0 != 0) goto L_0x3b39
            r0 = 1115160576(0x42780000, float:62.0)
            goto L_0x3b3b
        L_0x3b39:
            r0 = 1092616192(0x41200000, float:10.0)
        L_0x3b3b:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x3b4b
            int r2 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
            goto L_0x3b5c
        L_0x3b4b:
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
            int r2 = r2.y
            int r2 = java.lang.Math.min(r3, r2)
            r3 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
        L_0x3b5c:
            int r0 = r0 + r2
            int r2 = r1.backgroundWidth
            int r3 = r14.wantedBotKeyboardWidth
            int r0 = java.lang.Math.min(r3, r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.widthForButtons = r0
        L_0x3b6b:
            java.util.HashMap r0 = new java.util.HashMap
            java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton> r2 = r1.botButtonsByData
            r0.<init>(r2)
            java.lang.StringBuilder r2 = r14.botButtonsLayout
            if (r2 == 0) goto L_0x3b8c
            java.lang.String r3 = r1.botButtonsLayout
            if (r3 == 0) goto L_0x3b8c
            java.lang.String r2 = r2.toString()
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x3b8c
            java.util.HashMap r2 = new java.util.HashMap
            java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton> r3 = r1.botButtonsByPosition
            r2.<init>(r3)
            goto L_0x3b97
        L_0x3b8c:
            java.lang.StringBuilder r2 = r14.botButtonsLayout
            if (r2 == 0) goto L_0x3b96
            java.lang.String r2 = r2.toString()
            r1.botButtonsLayout = r2
        L_0x3b96:
            r2 = 0
        L_0x3b97:
            java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton> r3 = r1.botButtonsByData
            r3.clear()
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r4 = r3.reply_markup
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup
            if (r4 == 0) goto L_0x3cee
            r3 = 0
            r4 = 0
        L_0x3ba6:
            if (r3 >= r13) goto L_0x3df6
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r5 = r5.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r5 = r5.rows
            java.lang.Object r5 = r5.get(r3)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r5 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r6 = r5.buttons
            int r6 = r6.size()
            if (r6 != 0) goto L_0x3bbe
            goto L_0x3cea
        L_0x3bbe:
            int r7 = r1.widthForButtons
            r8 = 1084227584(0x40a00000, float:5.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r9 = r6 + -1
            int r8 = r8 * r9
            int r7 = r7 - r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r7 = r7 - r8
            int r7 = r7 / r6
            r6 = r4
            r4 = 0
        L_0x3bd3:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r8 = r5.buttons
            int r8 = r8.size()
            if (r4 >= r8) goto L_0x3ce9
            org.telegram.ui.Cells.ChatMessageCell$BotButton r8 = new org.telegram.ui.Cells.ChatMessageCell$BotButton
            r9 = 0
            r8.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r9 = r5.buttons
            java.lang.Object r9 = r9.get(r4)
            org.telegram.tgnet.TLRPC$KeyboardButton r9 = (org.telegram.tgnet.TLRPC.KeyboardButton) r9
            org.telegram.tgnet.TLRPC.KeyboardButton unused = r8.button = r9
            org.telegram.tgnet.TLRPC$KeyboardButton r9 = r8.button
            byte[] r9 = r9.data
            java.lang.String r9 = org.telegram.messenger.Utilities.bytesToHex(r9)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r3)
            java.lang.String r11 = ""
            r10.append(r11)
            r10.append(r4)
            java.lang.String r10 = r10.toString()
            if (r2 == 0) goto L_0x3CLASSNAME
            java.lang.Object r11 = r2.get(r10)
            org.telegram.ui.Cells.ChatMessageCell$BotButton r11 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r11
            goto L_0x3CLASSNAME
        L_0x3CLASSNAME:
            java.lang.Object r11 = r0.get(r9)
            org.telegram.ui.Cells.ChatMessageCell$BotButton r11 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r11
        L_0x3CLASSNAME:
            if (r11 == 0) goto L_0x3CLASSNAME
            float r12 = r11.progressAlpha
            float unused = r8.progressAlpha = r12
            int r12 = r11.angle
            int unused = r8.angle = r12
            long r11 = r11.lastUpdateTime
            long unused = r8.lastUpdateTime = r11
            goto L_0x3CLASSNAME
        L_0x3CLASSNAME:
            long r11 = java.lang.System.currentTimeMillis()
            long unused = r8.lastUpdateTime = r11
        L_0x3CLASSNAME:
            java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton> r11 = r1.botButtonsByData
            r11.put(r9, r8)
            java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton> r9 = r1.botButtonsByPosition
            r9.put(r10, r8)
            r9 = 1084227584(0x40a00000, float:5.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r7
            int r9 = r9 * r4
            int unused = r8.x = r9
            r9 = 1111490560(0x42400000, float:48.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 * r3
            r10 = 1084227584(0x40a00000, float:5.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 + r10
            int unused = r8.y = r9
            int unused = r8.width = r7
            r9 = 1110441984(0x42300000, float:44.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int unused = r8.height = r9
            org.telegram.tgnet.TLRPC$KeyboardButton r9 = r8.button
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy
            if (r9 == 0) goto L_0x3CLASSNAME
            org.telegram.tgnet.TLRPC$Message r9 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r9.media
            int r9 = r9.flags
            r9 = r9 & 4
            if (r9 == 0) goto L_0x3CLASSNAME
            r9 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r10 = "PaymentReceipt"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            goto L_0x3cad
        L_0x3CLASSNAME:
            org.telegram.tgnet.TLRPC$KeyboardButton r9 = r8.button
            java.lang.String r9 = r9.text
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint
            android.graphics.Paint$FontMetricsInt r10 = r10.getFontMetricsInt()
            r11 = 1097859072(0x41700000, float:15.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            java.lang.CharSequence r9 = org.telegram.messenger.Emoji.replaceEmoji(r9, r10, r11, r15)
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r11 = r7 - r11
            float r11 = (float) r11
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r9 = android.text.TextUtils.ellipsize(r9, r10, r11, r12)
        L_0x3cad:
            r30 = r9
            android.text.StaticLayout r9 = new android.text.StaticLayout
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r32 = r7 - r10
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_CENTER
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r9
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            android.text.StaticLayout unused = r8.title = r9
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$BotButton> r9 = r1.botButtons
            r9.add(r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r9 = r5.buttons
            int r9 = r9.size()
            r10 = 1
            int r9 = r9 - r10
            if (r4 != r9) goto L_0x3ce5
            int r9 = r8.x
            int r8 = r8.width
            int r9 = r9 + r8
            int r6 = java.lang.Math.max(r6, r9)
        L_0x3ce5:
            int r4 = r4 + 1
            goto L_0x3bd3
        L_0x3ce9:
            r4 = r6
        L_0x3cea:
            int r3 = r3 + 1
            goto L_0x3ba6
        L_0x3cee:
            org.telegram.tgnet.TLRPC$TL_messageReactions r3 = r3.reactions
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_reactionCount> r3 = r3.results
            int r3 = r3.size()
            int r4 = r1.widthForButtons
            r5 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r6 = r3 + -1
            int r5 = r5 * r6
            int r4 = r4 - r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r4 = r4 - r5
            int r4 = r4 / r3
            r5 = 0
            r7 = 0
        L_0x3d0b:
            if (r5 >= r3) goto L_0x3df5
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r8 = r8.reactions
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_reactionCount> r8 = r8.results
            java.lang.Object r8 = r8.get(r5)
            org.telegram.tgnet.TLRPC$TL_reactionCount r8 = (org.telegram.tgnet.TLRPC.TL_reactionCount) r8
            org.telegram.ui.Cells.ChatMessageCell$BotButton r9 = new org.telegram.ui.Cells.ChatMessageCell$BotButton
            r10 = 0
            r9.<init>()
            org.telegram.tgnet.TLRPC.TL_reactionCount unused = r9.reaction = r8
            java.lang.String r10 = r8.reaction
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "0"
            r11.append(r12)
            r11.append(r5)
            java.lang.String r11 = r11.toString()
            if (r2 == 0) goto L_0x3d3e
            java.lang.Object r12 = r2.get(r11)
            org.telegram.ui.Cells.ChatMessageCell$BotButton r12 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r12
            goto L_0x3d44
        L_0x3d3e:
            java.lang.Object r12 = r0.get(r10)
            org.telegram.ui.Cells.ChatMessageCell$BotButton r12 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r12
        L_0x3d44:
            if (r12 == 0) goto L_0x3d5c
            float r13 = r12.progressAlpha
            float unused = r9.progressAlpha = r13
            int r13 = r12.angle
            int unused = r9.angle = r13
            long r12 = r12.lastUpdateTime
            long unused = r9.lastUpdateTime = r12
            goto L_0x3d63
        L_0x3d5c:
            long r12 = java.lang.System.currentTimeMillis()
            long unused = r9.lastUpdateTime = r12
        L_0x3d63:
            java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton> r12 = r1.botButtonsByData
            r12.put(r10, r9)
            java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton> r10 = r1.botButtonsByPosition
            r10.put(r11, r9)
            r10 = 1084227584(0x40a00000, float:5.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = r10 + r4
            int r10 = r10 * r5
            int unused = r9.x = r10
            r10 = 1084227584(0x40a00000, float:5.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int unused = r9.y = r10
            int unused = r9.width = r4
            r10 = 1110441984(0x42300000, float:44.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int unused = r9.height = r10
            r10 = 2
            java.lang.Object[] r11 = new java.lang.Object[r10]
            int r10 = r8.count
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r11[r15] = r10
            java.lang.String r8 = r8.reaction
            r10 = 1
            r11[r10] = r8
            java.lang.String r8 = "%d %s"
            java.lang.String r8 = java.lang.String.format(r8, r11)
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint
            android.graphics.Paint$FontMetricsInt r10 = r10.getFontMetricsInt()
            r11 = 1097859072(0x41700000, float:15.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            java.lang.CharSequence r8 = org.telegram.messenger.Emoji.replaceEmoji(r8, r10, r11, r15)
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r11 = r4 - r11
            float r11 = (float) r11
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r30 = android.text.TextUtils.ellipsize(r8, r10, r11, r12)
            android.text.StaticLayout r8 = new android.text.StaticLayout
            android.text.TextPaint r31 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r32 = r4 - r10
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_CENTER
            r34 = 1065353216(0x3var_, float:1.0)
            r35 = 0
            r36 = 0
            r29 = r8
            r29.<init>(r30, r31, r32, r33, r34, r35, r36)
            android.text.StaticLayout unused = r9.title = r8
            java.util.ArrayList<org.telegram.ui.Cells.ChatMessageCell$BotButton> r8 = r1.botButtons
            r8.add(r9)
            if (r5 != r6) goto L_0x3df1
            int r8 = r9.x
            int r9 = r9.width
            int r8 = r8 + r9
            int r7 = java.lang.Math.max(r7, r8)
        L_0x3df1:
            int r5 = r5 + 1
            goto L_0x3d0b
        L_0x3df5:
            r4 = r7
        L_0x3df6:
            r1.widthForButtons = r4
            goto L_0x3dfd
        L_0x3df9:
            r1.substractBackgroundHeight = r15
            r1.keyboardHeight = r15
        L_0x3dfd:
            boolean r0 = r1.drawPinnedBottom
            if (r0 == 0) goto L_0x3e0f
            boolean r0 = r1.drawPinnedTop
            if (r0 == 0) goto L_0x3e0f
            int r0 = r1.totalHeight
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r0 = r0 - r2
            r1.totalHeight = r0
            goto L_0x3e36
        L_0x3e0f:
            boolean r0 = r1.drawPinnedBottom
            if (r0 == 0) goto L_0x3e1d
            int r0 = r1.totalHeight
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 - r2
            r1.totalHeight = r0
            goto L_0x3e36
        L_0x3e1d:
            boolean r0 = r1.drawPinnedTop
            if (r0 == 0) goto L_0x3e36
            boolean r0 = r1.pinnedBottom
            if (r0 == 0) goto L_0x3e36
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r1.currentPosition
            if (r0 == 0) goto L_0x3e36
            float[] r0 = r0.siblingHeights
            if (r0 != 0) goto L_0x3e36
            int r0 = r1.totalHeight
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r0 = r0 - r2
            r1.totalHeight = r0
        L_0x3e36:
            boolean r0 = r59.isAnyKindOfSticker()
            if (r0 == 0) goto L_0x3e4f
            int r0 = r1.totalHeight
            r2 = 1116471296(0x428CLASSNAME, float:70.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            if (r0 >= r2) goto L_0x3e4f
            r0 = 1116471296(0x428CLASSNAME, float:70.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.totalHeight = r0
            goto L_0x3e60
        L_0x3e4f:
            boolean r0 = r59.isAnimatedEmoji()
            if (r0 == 0) goto L_0x3e60
            int r0 = r1.totalHeight
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r1.totalHeight = r0
        L_0x3e60:
            boolean r0 = r1.drawPhotoImage
            if (r0 != 0) goto L_0x3e6a
            org.telegram.messenger.ImageReceiver r0 = r1.photoImage
            r2 = 0
            r0.setImageBitmap((android.graphics.drawable.Drawable) r2)
        L_0x3e6a:
            int r0 = r1.documentAttachType
            r2 = 5
            if (r0 != r2) goto L_0x3ea1
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            boolean r0 = org.telegram.messenger.MessageObject.isDocumentHasThumb(r0)
            if (r0 == 0) goto L_0x3e89
            org.telegram.tgnet.TLRPC$Document r0 = r1.documentAttach
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.thumbs
            r2 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2)
            org.telegram.ui.Components.RadialProgress2 r2 = r1.radialProgress
            org.telegram.tgnet.TLRPC$Document r3 = r1.documentAttach
            r2.setImageOverlay(r0, r3, r14)
            goto L_0x3ea7
        L_0x3e89:
            r2 = 1
            java.lang.String r0 = r14.getArtworkUrl(r2)
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x3e9a
            org.telegram.ui.Components.RadialProgress2 r2 = r1.radialProgress
            r2.setImageOverlay(r0)
            goto L_0x3ea7
        L_0x3e9a:
            org.telegram.ui.Components.RadialProgress2 r0 = r1.radialProgress
            r2 = 0
            r0.setImageOverlay(r2, r2, r2)
            goto L_0x3ea7
        L_0x3ea1:
            r2 = 0
            org.telegram.ui.Components.RadialProgress2 r0 = r1.radialProgress
            r0.setImageOverlay(r2, r2, r2)
        L_0x3ea7:
            if (r16 == 0) goto L_0x3eac
            r2 = 0
            r1.selectedBackgroundProgress = r2
        L_0x3eac:
            r58.updateWaveform()
            if (r18 == 0) goto L_0x3eb7
            boolean r0 = r14.cancelEditing
            if (r0 != 0) goto L_0x3eb7
            r0 = 1
            goto L_0x3eb8
        L_0x3eb7:
            r0 = 0
        L_0x3eb8:
            r2 = 1
            r1.updateButtonState(r15, r0, r2)
            int r0 = r1.buttonState
            r2 = 2
            if (r0 != r2) goto L_0x3eec
            int r0 = r1.documentAttachType
            r2 = 3
            if (r0 != r2) goto L_0x3eec
            int r0 = r1.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            boolean r0 = r0.canDownloadMedia((org.telegram.messenger.MessageObject) r14)
            if (r0 == 0) goto L_0x3eec
            int r0 = r1.currentAccount
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)
            org.telegram.tgnet.TLRPC$Document r2 = r1.documentAttach
            org.telegram.messenger.MessageObject r3 = r1.currentMessageObject
            r4 = 1
            r0.loadFile(r2, r3, r4, r15)
            r0 = 4
            r1.buttonState = r0
            org.telegram.ui.Components.RadialProgress2 r0 = r1.radialProgress
            int r2 = r58.getIconForCurrentState()
            r0.setIcon(r2, r15, r15)
        L_0x3eec:
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r1.delegate
            if (r0 == 0) goto L_0x3var_
            org.telegram.ui.Cells.TextSelectionHelper$ChatListTextSelectionHelper r0 = r0.getTextSelectionHelper()
            if (r0 == 0) goto L_0x3var_
            if (r16 != 0) goto L_0x3var_
            if (r17 == 0) goto L_0x3var_
            if (r14 == 0) goto L_0x3var_
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r0 = r1.delegate
            org.telegram.ui.Cells.TextSelectionHelper$ChatListTextSelectionHelper r0 = r0.getTextSelectionHelper()
            r0.checkDataChanged(r14)
        L_0x3var_:
            android.util.SparseArray<android.graphics.Rect> r0 = r1.accessibilityVirtualViewBounds
            r0.clear()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageContent(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, boolean, boolean):void");
    }

    static /* synthetic */ int lambda$setMessageContent$0(PollButton pollButton, PollButton pollButton2) {
        if (pollButton.decimal > pollButton2.decimal) {
            return -1;
        }
        return pollButton.decimal < pollButton2.decimal ? 1 : 0;
    }

    public void checkVideoPlayback(boolean z) {
        if (!this.currentMessageObject.isVideo()) {
            if (z) {
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                z = playingMessageObject == null || !playingMessageObject.isRoundVideo();
            }
            this.photoImage.setAllowStartAnimation(z);
            if (z) {
                this.photoImage.startAnimation();
            } else {
                this.photoImage.stopAnimation();
            }
        } else if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
            this.photoImage.setAllowStartAnimation(false);
            this.photoImage.stopAnimation();
        } else {
            this.photoImage.setAllowStartAnimation(true);
            this.photoImage.startAnimation();
        }
    }

    /* access modifiers changed from: protected */
    public void onLongPress() {
        Drawable drawable;
        Drawable drawable2;
        CharacterStyle characterStyle = this.pressedLink;
        if (characterStyle instanceof URLSpanMono) {
            this.delegate.didPressUrl(this, characterStyle, true);
            return;
        }
        if (characterStyle instanceof URLSpanNoUnderline) {
            URLSpanNoUnderline uRLSpanNoUnderline = (URLSpanNoUnderline) characterStyle;
            if (ChatActivity.isClickableLink(uRLSpanNoUnderline.getURL()) || uRLSpanNoUnderline.getURL().startsWith("/")) {
                this.delegate.didPressUrl(this, this.pressedLink, true);
                return;
            }
        } else if (characterStyle instanceof URLSpan) {
            this.delegate.didPressUrl(this, characterStyle, true);
            return;
        }
        resetPressedLink(-1);
        if (!(this.buttonPressed == 0 && this.miniButtonPressed == 0 && this.videoButtonPressed == 0 && this.pressedBotButton == -1)) {
            this.buttonPressed = 0;
            this.miniButtonPressed = 0;
            this.videoButtonPressed = 0;
            this.pressedBotButton = -1;
            invalidate();
        }
        this.linkPreviewPressed = false;
        this.otherPressed = false;
        this.sharePressed = false;
        this.imagePressed = false;
        this.gamePreviewPressed = false;
        if (this.instantPressed) {
            this.instantButtonPressed = false;
            this.instantPressed = false;
            if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.selectorDrawable) != null) {
                drawable2.setState(StateSet.NOTHING);
            }
            invalidate();
        }
        if (this.pressedVoteButton != -1) {
            this.pressedVoteButton = -1;
            if (Build.VERSION.SDK_INT >= 21 && (drawable = this.selectorDrawable) != null) {
                drawable.setState(StateSet.NOTHING);
            }
            invalidate();
        }
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate != null) {
            chatMessageCellDelegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
        }
    }

    public void setCheckPressed(boolean z, boolean z2) {
        this.isCheckPressed = z;
        this.isPressed = z2;
        updateRadialProgressBackground();
        if (this.useSeekBarWaweform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
    }

    public void setInvalidatesParent(boolean z) {
        this.invalidatesParent = z;
    }

    public void invalidate() {
        super.invalidate();
        if (this.invalidatesParent && getParent() != null) {
            View view = (View) getParent();
            if (view.getParent() != null) {
                ((View) view.getParent()).invalidate();
            }
        }
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
        if (this.isHighlighted != z) {
            this.isHighlighted = z;
            if (!this.isHighlighted) {
                this.lastHighlightProgressTime = System.currentTimeMillis();
                this.isHighlightedAnimated = true;
                this.highlightProgress = 300;
            } else {
                this.isHighlightedAnimated = false;
                this.highlightProgress = 0;
            }
            updateRadialProgressBackground();
            if (this.useSeekBarWaweform) {
                this.seekBarWaveform.setSelected(isDrawSelectionBackground());
            } else {
                this.seekBar.setSelected(isDrawSelectionBackground());
            }
            invalidate();
            if (getParent() != null) {
                ((View) getParent()).invalidate();
            }
        }
    }

    public void setPressed(boolean z) {
        super.setPressed(z);
        updateRadialProgressBackground();
        if (this.useSeekBarWaweform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
    }

    private void updateRadialProgressBackground() {
        if (!this.drawRadialCheckBackground) {
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
    }

    public void onSeekBarDrag(float f) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            messageObject.audioProgress = f;
            MediaController.getInstance().seekToProgress(this.currentMessageObject, f);
        }
    }

    private void updateWaveform() {
        if (this.currentMessageObject != null && this.documentAttachType == 3) {
            boolean z = false;
            for (int i = 0; i < this.documentAttach.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = this.documentAttach.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                    byte[] bArr = documentAttribute.waveform;
                    if (bArr == null || bArr.length == 0) {
                        MediaController.getInstance().generateWaveform(this.currentMessageObject);
                    }
                    if (documentAttribute.waveform != null) {
                        z = true;
                    }
                    this.useSeekBarWaweform = z;
                    this.seekBarWaveform.setWaveform(documentAttribute.waveform);
                    return;
                }
            }
        }
    }

    private int createDocumentLayout(int i, MessageObject messageObject) {
        int i2;
        int i3;
        int i4 = i;
        MessageObject messageObject2 = messageObject;
        if (messageObject2.type == 0) {
            this.documentAttach = messageObject2.messageOwner.media.webpage.document;
        } else {
            this.documentAttach = messageObject.getDocument();
        }
        TLRPC.Document document = this.documentAttach;
        int i5 = 0;
        if (document == null) {
            return 0;
        }
        if (MessageObject.isVoiceDocument(document)) {
            this.documentAttachType = 3;
            int i6 = 0;
            while (true) {
                if (i6 >= this.documentAttach.attributes.size()) {
                    i3 = 0;
                    break;
                }
                TLRPC.DocumentAttribute documentAttribute = this.documentAttach.attributes.get(i6);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                    i3 = documentAttribute.duration;
                    break;
                }
                i6++;
            }
            this.widthBeforeNewTimeLine = (i4 - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil((double) Theme.chat_audioTimePaint.measureText("00:00")));
            this.availableTimeWidth = i4 - AndroidUtilities.dp(18.0f);
            measureTime(messageObject2);
            int dp = AndroidUtilities.dp(174.0f) + this.timeWidth;
            if (!this.hasLinkPreview) {
                this.backgroundWidth = Math.min(i4, dp + (i3 * AndroidUtilities.dp(10.0f)));
            }
            this.seekBarWaveform.setMessageObject(messageObject2);
            return 0;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
            int dp2 = i4 - AndroidUtilities.dp(86.0f);
            if (dp2 < 0) {
                dp2 = AndroidUtilities.dp(100.0f);
            }
            int i7 = dp2;
            this.songLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicTitle().replace(10, ' '), Theme.chat_audioTitlePaint, (float) (i7 - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), Theme.chat_audioTitlePaint, i7, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.songLayout.getLineCount() > 0) {
                this.songX = -((int) Math.ceil((double) this.songLayout.getLineLeft(0)));
            }
            this.performerLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicAuthor().replace(10, ' '), Theme.chat_audioPerformerPaint, (float) i7, TextUtils.TruncateAt.END), Theme.chat_audioPerformerPaint, i7, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.performerLayout.getLineCount() > 0) {
                this.performerX = -((int) Math.ceil((double) this.performerLayout.getLineLeft(0)));
            }
            int i8 = 0;
            while (true) {
                if (i8 >= this.documentAttach.attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute documentAttribute2 = this.documentAttach.attributes.get(i8);
                if (documentAttribute2 instanceof TLRPC.TL_documentAttributeAudio) {
                    i5 = documentAttribute2.duration;
                    break;
                }
                i8++;
            }
            int ceil = (int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(AndroidUtilities.formatShortDuration(i5, i5)));
            this.widthBeforeNewTimeLine = (this.backgroundWidth - AndroidUtilities.dp(86.0f)) - ceil;
            this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.dp(28.0f);
            return ceil;
        } else if (MessageObject.isVideoDocument(this.documentAttach)) {
            this.documentAttachType = 4;
            if (!messageObject.needDrawBluredPreview()) {
                updatePlayingMessageProgress();
                String format = String.format("%s", new Object[]{AndroidUtilities.formatFileSize((long) this.documentAttach.size)});
                this.docTitleWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(format));
                this.docTitleLayout = new StaticLayout(format, Theme.chat_infoPaint, this.docTitleWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            return 0;
        } else if (MessageObject.isGifDocument(this.documentAttach)) {
            this.documentAttachType = 2;
            if (!messageObject.needDrawBluredPreview()) {
                String string = LocaleController.getString("AttachGif", NUM);
                this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(string));
                this.infoLayout = new StaticLayout(string, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                String format2 = String.format("%s", new Object[]{AndroidUtilities.formatFileSize((long) this.documentAttach.size)});
                this.docTitleWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(format2));
                this.docTitleLayout = new StaticLayout(format2, Theme.chat_infoPaint, this.docTitleWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            return 0;
        } else {
            String str = this.documentAttach.mime_type;
            this.drawPhotoImage = (str != null && str.toLowerCase().startsWith("image/")) || MessageObject.isDocumentHasThumb(this.documentAttach);
            if (!this.drawPhotoImage) {
                i4 += AndroidUtilities.dp(30.0f);
            }
            this.documentAttachType = 1;
            String documentFileName = FileLoader.getDocumentFileName(this.documentAttach);
            if (documentFileName == null || documentFileName.length() == 0) {
                documentFileName = LocaleController.getString("AttachDocument", NUM);
            }
            this.docTitleLayout = StaticLayoutEx.createStaticLayout(documentFileName, Theme.chat_docNamePaint, i4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.MIDDLE, i4, 2, false);
            this.docTitleOffsetX = Integer.MIN_VALUE;
            StaticLayout staticLayout = this.docTitleLayout;
            if (staticLayout == null || staticLayout.getLineCount() <= 0) {
                this.docTitleOffsetX = 0;
                i2 = i4;
            } else {
                int i9 = 0;
                while (i5 < this.docTitleLayout.getLineCount()) {
                    i9 = Math.max(i9, (int) Math.ceil((double) this.docTitleLayout.getLineWidth(i5)));
                    this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int) Math.ceil((double) (-this.docTitleLayout.getLineLeft(i5))));
                    i5++;
                }
                i2 = Math.min(i4, i9);
            }
            String str2 = AndroidUtilities.formatFileSize((long) this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach);
            this.infoWidth = Math.min(i4 - AndroidUtilities.dp(30.0f), (int) Math.ceil((double) Theme.chat_infoPaint.measureText(str2)));
            CharSequence ellipsize = TextUtils.ellipsize(str2, Theme.chat_infoPaint, (float) this.infoWidth, TextUtils.TruncateAt.END);
            try {
                if (this.infoWidth < 0) {
                    this.infoWidth = AndroidUtilities.dp(10.0f);
                }
                this.infoLayout = new StaticLayout(ellipsize, Theme.chat_infoPaint, this.infoWidth + AndroidUtilities.dp(6.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (this.drawPhotoImage) {
                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 320);
                this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 40);
                if ((DownloadController.getInstance(this.currentAccount).getAutodownloadMask() & 1) == 0) {
                    this.currentPhotoObject = null;
                }
                TLRPC.PhotoSize photoSize = this.currentPhotoObject;
                if (photoSize == null || photoSize == this.currentPhotoObjectThumb) {
                    this.currentPhotoObject = null;
                    this.photoImage.setNeedsQualityThumb(true);
                    this.photoImage.setShouldGenerateQualityThumb(true);
                }
                this.currentPhotoFilter = "86_86_b";
                this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, messageObject2.photoThumbsObject), "86_86", ImageLocation.getForObject(this.currentPhotoObjectThumb, messageObject2.photoThumbsObject), this.currentPhotoFilter, 0, (String) null, messageObject, 1);
            }
            return i2;
        }
    }

    private void calcBackgroundWidth(int i, int i2, int i3) {
        if (!this.hasLinkPreview && !this.hasOldCaptionPreview && !this.hasGamePreview && !this.hasInvoicePreview) {
            MessageObject messageObject = this.currentMessageObject;
            int i4 = messageObject.lastLineWidth;
            if (i - i4 >= i2 && !messageObject.hasRtl) {
                int i5 = i3 - i4;
                if (i5 < 0 || i5 > i2) {
                    this.backgroundWidth = Math.max(i3, this.currentMessageObject.lastLineWidth + i2) + AndroidUtilities.dp(31.0f);
                    return;
                } else {
                    this.backgroundWidth = ((i3 + i2) - i5) + AndroidUtilities.dp(31.0f);
                    return;
                }
            }
        }
        this.totalHeight += AndroidUtilities.dp(14.0f);
        this.hasNewLineForTime = true;
        this.backgroundWidth = Math.max(i3, this.currentMessageObject.lastLineWidth) + AndroidUtilities.dp(31.0f);
        this.backgroundWidth = Math.max(this.backgroundWidth, (this.currentMessageObject.isOutOwner() ? this.timeWidth + AndroidUtilities.dp(17.0f) : this.timeWidth) + AndroidUtilities.dp(31.0f));
    }

    public void setHighlightedText(String str) {
        MessageObject messageObject = this.messageObjectToSet;
        if (messageObject == null) {
            messageObject = this.currentMessageObject;
        }
        if (messageObject != null && messageObject.messageOwner.message != null && !TextUtils.isEmpty(str)) {
            String lowerCase = str.toLowerCase();
            String lowerCase2 = messageObject.messageOwner.message.toLowerCase();
            int length = lowerCase2.length();
            int i = -1;
            int i2 = -1;
            for (int i3 = 0; i3 < length; i3++) {
                int min = Math.min(lowerCase.length(), length - i3);
                int i4 = 0;
                int i5 = 0;
                while (true) {
                    if (i4 >= min) {
                        break;
                    }
                    boolean z = lowerCase2.charAt(i3 + i4) == lowerCase.charAt(i4);
                    if (z) {
                        if (i5 != 0 || i3 == 0 || " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n".indexOf(lowerCase2.charAt(i3 - 1)) >= 0) {
                            i5++;
                        } else {
                            z = false;
                        }
                    }
                    if (z && i4 != min - 1) {
                        i4++;
                    } else if (i5 > 0 && i5 > i2) {
                        i = i3;
                        i2 = i5;
                    }
                }
            }
            if (i != -1) {
                int i6 = i + i2;
                int length2 = lowerCase2.length();
                while (i6 < length2 && " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n".indexOf(lowerCase2.charAt(i6)) < 0) {
                    i2++;
                    i6++;
                }
                int i7 = i + i2;
                if (this.captionLayout != null && !TextUtils.isEmpty(messageObject.caption)) {
                    resetUrlPaths(true);
                    try {
                        LinkPath obtainNewUrlPath = obtainNewUrlPath(true);
                        obtainNewUrlPath.setCurrentLayout(this.captionLayout, i, 0.0f);
                        this.captionLayout.getSelectionPath(i, i7, obtainNewUrlPath);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    invalidate();
                } else if (messageObject.textLayoutBlocks != null) {
                    int i8 = 0;
                    while (i8 < messageObject.textLayoutBlocks.size()) {
                        MessageObject.TextLayoutBlock textLayoutBlock = messageObject.textLayoutBlocks.get(i8);
                        int i9 = textLayoutBlock.charactersOffset;
                        if (i < i9 || i >= i9 + textLayoutBlock.textLayout.getText().length()) {
                            i8++;
                        } else {
                            this.linkSelectionBlockNum = i8;
                            resetUrlPaths(true);
                            try {
                                LinkPath obtainNewUrlPath2 = obtainNewUrlPath(true);
                                obtainNewUrlPath2.setCurrentLayout(textLayoutBlock.textLayout, i, 0.0f);
                                textLayoutBlock.textLayout.getSelectionPath(i, i7 - textLayoutBlock.charactersOffset, obtainNewUrlPath2);
                                if (i7 >= textLayoutBlock.charactersOffset + i2) {
                                    for (int i10 = i8 + 1; i10 < messageObject.textLayoutBlocks.size(); i10++) {
                                        MessageObject.TextLayoutBlock textLayoutBlock2 = messageObject.textLayoutBlocks.get(i10);
                                        int length3 = textLayoutBlock2.textLayout.getText().length();
                                        LinkPath obtainNewUrlPath3 = obtainNewUrlPath(true);
                                        obtainNewUrlPath3.setCurrentLayout(textLayoutBlock2.textLayout, 0, (float) textLayoutBlock2.height);
                                        textLayoutBlock2.textLayout.getSelectionPath(0, i7 - textLayoutBlock2.charactersOffset, obtainNewUrlPath3);
                                        if (i7 < (textLayoutBlock.charactersOffset + length3) - 1) {
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e2) {
                                FileLog.e((Throwable) e2);
                            }
                            invalidate();
                            return;
                        }
                    }
                }
            } else if (!this.urlPathSelection.isEmpty()) {
                this.linkSelectionBlockNum = -1;
                resetUrlPaths(true);
                invalidate();
            }
        } else if (!this.urlPathSelection.isEmpty()) {
            this.linkSelectionBlockNum = -1;
            resetUrlPaths(true);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.selectorDrawable;
    }

    private boolean isCurrentLocationTimeExpired(MessageObject messageObject) {
        if (this.currentMessageObject.messageOwner.media.period % 60 == 0) {
            if (Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) > messageObject.messageOwner.media.period) {
                return true;
            }
            return false;
        } else if (Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) > messageObject.messageOwner.media.period - 5) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void checkLocationExpired() {
        boolean isCurrentLocationTimeExpired;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && (isCurrentLocationTimeExpired = isCurrentLocationTimeExpired(messageObject)) != this.locationExpired) {
            this.locationExpired = isCurrentLocationTimeExpired;
            if (!this.locationExpired) {
                AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
                this.scheduledInvalidate = true;
                int dp = this.backgroundWidth - AndroidUtilities.dp(91.0f);
                this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(LocaleController.getString("AttachLiveLocation", NUM), Theme.chat_locationTitlePaint, (float) dp, TextUtils.TruncateAt.END), Theme.chat_locationTitlePaint, dp, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                return;
            }
            MessageObject messageObject2 = this.currentMessageObject;
            this.currentMessageObject = null;
            setMessageObject(messageObject2, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
        }
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
        if (groupedMessagePosition == null) {
            return 0;
        }
        if ((groupedMessagePosition.flags & 2) == 0) {
            i = 0 + AndroidUtilities.dp(4.0f);
        }
        return (groupedMessagePosition.flags & 1) == 0 ? i + AndroidUtilities.dp(4.0f) : i;
    }

    public void createSelectorDrawable() {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable drawable = this.selectorDrawable;
            String str = "chat_outPreviewInstantText";
            if (drawable == null) {
                final Paint paint = new Paint(1);
                paint.setColor(-1);
                AnonymousClass2 r6 = new Drawable() {
                    RectF rect = new RectF();

                    public int getOpacity() {
                        return -2;
                    }

                    public void setAlpha(int i) {
                    }

                    public void setColorFilter(ColorFilter colorFilter) {
                    }

                    public void draw(Canvas canvas) {
                        Rect bounds = getBounds();
                        this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                        RectF rectF = this.rect;
                        float f = 0.0f;
                        float dp = ChatMessageCell.this.selectorDrawableMaskType == 0 ? (float) AndroidUtilities.dp(6.0f) : 0.0f;
                        if (ChatMessageCell.this.selectorDrawableMaskType == 0) {
                            f = (float) AndroidUtilities.dp(6.0f);
                        }
                        canvas.drawRoundRect(rectF, dp, f, paint);
                    }
                };
                int[][] iArr = {StateSet.WILD_CARD};
                int[] iArr2 = new int[1];
                if (!this.currentMessageObject.isOutOwner()) {
                    str = "chat_inPreviewInstantText";
                }
                iArr2[0] = NUM & Theme.getColor(str);
                this.selectorDrawable = new RippleDrawable(new ColorStateList(iArr, iArr2), (Drawable) null, r6);
                this.selectorDrawable.setCallback(this);
            } else {
                if (!this.currentMessageObject.isOutOwner()) {
                    str = "chat_inPreviewInstantText";
                }
                Theme.setSelectorDrawableColor(drawable, NUM & Theme.getColor(str), true);
            }
            this.selectorDrawable.setVisible(true, false);
        }
    }

    private void createInstantViewButton() {
        String str;
        if (Build.VERSION.SDK_INT >= 21 && this.drawInstantView) {
            createSelectorDrawable();
        }
        if (this.drawInstantView && this.instantViewLayout == null) {
            this.instantWidth = AndroidUtilities.dp(33.0f);
            int i = this.drawInstantViewType;
            if (i == 1) {
                str = LocaleController.getString("OpenChannel", NUM);
            } else if (i == 2) {
                str = LocaleController.getString("OpenGroup", NUM);
            } else if (i == 3) {
                str = LocaleController.getString("OpenMessage", NUM);
            } else if (i == 5) {
                str = LocaleController.getString("ViewContact", NUM);
            } else if (i == 6) {
                str = LocaleController.getString("OpenBackground", NUM);
            } else if (i == 7) {
                str = LocaleController.getString("OpenTheme", NUM);
            } else {
                str = LocaleController.getString("InstantView", NUM);
            }
            int dp = this.backgroundWidth - AndroidUtilities.dp(75.0f);
            this.instantViewLayout = new StaticLayout(TextUtils.ellipsize(str, Theme.chat_instantViewPaint, (float) dp, TextUtils.TruncateAt.END), Theme.chat_instantViewPaint, dp + AndroidUtilities.dp(2.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.instantWidth = this.backgroundWidth - AndroidUtilities.dp(34.0f);
            this.totalHeight += AndroidUtilities.dp(46.0f);
            if (this.currentMessageObject.type == 12) {
                this.totalHeight += AndroidUtilities.dp(14.0f);
            }
            StaticLayout staticLayout = this.instantViewLayout;
            if (staticLayout != null && staticLayout.getLineCount() > 0) {
                double d = (double) this.instantWidth;
                double ceil = Math.ceil((double) this.instantViewLayout.getLineWidth(0));
                Double.isNaN(d);
                this.instantTextX = (((int) (d - ceil)) / 2) + (this.drawInstantViewType == 0 ? AndroidUtilities.dp(8.0f) : 0);
                this.instantTextLeftX = (int) this.instantViewLayout.getLineLeft(0);
                this.instantTextX += -this.instantTextLeftX;
            }
        }
    }

    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
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
        if (AndroidUtilities.isInMultiwindow || !AndroidUtilities.isTablet() || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
            return AndroidUtilities.displaySize.x;
        }
        int i = (AndroidUtilities.displaySize.x / 100) * 35;
        if (i < AndroidUtilities.dp(320.0f)) {
            i = AndroidUtilities.dp(320.0f);
        }
        return AndroidUtilities.displaySize.x - i;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0461  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:176:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
        /*
            r10 = this;
            org.telegram.messenger.MessageObject r12 = r10.currentMessageObject
            if (r12 != 0) goto L_0x0005
            return
        L_0x0005:
            r12 = 1
            r13 = 0
            r14 = 1111490560(0x42400000, float:48.0)
            r15 = 1092616192(0x41200000, float:10.0)
            if (r11 != 0) goto L_0x0011
            boolean r11 = r10.wasLayout
            if (r11 != 0) goto L_0x0109
        L_0x0011:
            int r11 = r10.getMeasuredWidth()
            r10.layoutWidth = r11
            int r11 = r10.getMeasuredHeight()
            int r0 = r10.substractBackgroundHeight
            int r11 = r11 - r0
            r10.layoutHeight = r11
            int r11 = r10.timeTextWidth
            if (r11 >= 0) goto L_0x002a
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r10.timeTextWidth = r11
        L_0x002a:
            android.text.StaticLayout r11 = new android.text.StaticLayout
            java.lang.String r1 = r10.currentTimeString
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_timePaint
            int r0 = r10.timeTextWidth
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r0
            android.text.Layout$Alignment r4 = android.text.Layout.Alignment.ALIGN_NORMAL
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            r7 = 0
            r0 = r11
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r10.timeLayout = r11
            boolean r11 = r10.mediaBackground
            r0 = 1109917696(0x42280000, float:42.0)
            if (r11 != 0) goto L_0x007c
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.isOutOwner()
            if (r11 != 0) goto L_0x006d
            int r11 = r10.backgroundWidth
            r1 = 1091567616(0x41100000, float:9.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r11 = r11 - r1
            int r1 = r10.timeWidth
            int r11 = r11 - r1
            boolean r1 = r10.isAvatarVisible
            if (r1 == 0) goto L_0x0068
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            goto L_0x0069
        L_0x0068:
            r1 = 0
        L_0x0069:
            int r11 = r11 + r1
            r10.timeX = r11
            goto L_0x00c8
        L_0x006d:
            int r11 = r10.layoutWidth
            int r1 = r10.timeWidth
            int r11 = r11 - r1
            r1 = 1109000192(0x421a0000, float:38.5)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r11 = r11 - r1
            r10.timeX = r11
            goto L_0x00c8
        L_0x007c:
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.isOutOwner()
            if (r11 != 0) goto L_0x00bc
            int r11 = r10.backgroundWidth
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r11 = r11 - r1
            int r1 = r10.timeWidth
            int r11 = r11 - r1
            boolean r1 = r10.isAvatarVisible
            if (r1 == 0) goto L_0x0099
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            goto L_0x009a
        L_0x0099:
            r1 = 0
        L_0x009a:
            int r11 = r11 + r1
            r10.timeX = r11
            org.telegram.messenger.MessageObject$GroupedMessagePosition r11 = r10.currentPosition
            if (r11 == 0) goto L_0x00c8
            int r11 = r11.leftSpanOffset
            if (r11 == 0) goto L_0x00c8
            int r1 = r10.timeX
            float r11 = (float) r11
            r2 = 1148846080(0x447a0000, float:1000.0)
            float r11 = r11 / r2
            int r2 = r10.getGroupPhotosWidth()
            float r2 = (float) r2
            float r11 = r11 * r2
            double r2 = (double) r11
            double r2 = java.lang.Math.ceil(r2)
            int r11 = (int) r2
            int r1 = r1 + r11
            r10.timeX = r1
            goto L_0x00c8
        L_0x00bc:
            int r11 = r10.layoutWidth
            int r1 = r10.timeWidth
            int r11 = r11 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r11 = r11 - r1
            r10.timeX = r11
        L_0x00c8:
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            int r11 = r11.flags
            r11 = r11 & 1024(0x400, float:1.435E-42)
            if (r11 == 0) goto L_0x00e7
            android.text.StaticLayout r11 = new android.text.StaticLayout
            java.lang.String r2 = r10.currentViewsString
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_timePaint
            int r4 = r10.viewsTextWidth
            android.text.Layout$Alignment r5 = android.text.Layout.Alignment.ALIGN_NORMAL
            r6 = 1065353216(0x3var_, float:1.0)
            r7 = 0
            r8 = 0
            r1 = r11
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            r10.viewsLayout = r11
            goto L_0x00ea
        L_0x00e7:
            r11 = 0
            r10.viewsLayout = r11
        L_0x00ea:
            boolean r11 = r10.isAvatarVisible
            if (r11 == 0) goto L_0x0107
            org.telegram.messenger.ImageReceiver r11 = r10.avatarImage
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            org.telegram.messenger.ImageReceiver r2 = r10.avatarImage
            int r2 = r2.getImageY()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.setImageCoords(r1, r2, r3, r0)
        L_0x0107:
            r10.wasLayout = r12
        L_0x0109:
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            int r11 = r11.type
            if (r11 != 0) goto L_0x0118
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r0 = r10.namesOffset
            int r11 = r11 + r0
            r10.textY = r11
        L_0x0118:
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.isRoundVideo()
            if (r11 == 0) goto L_0x0123
            r10.updatePlayingMessageProgress()
        L_0x0123:
            int r11 = r10.documentAttachType
            r0 = 3
            r1 = 1117257728(0x42980000, float:76.0)
            r2 = 1116078080(0x42860000, float:67.0)
            r3 = 1106247680(0x41var_, float:30.0)
            r4 = 10
            r5 = 1116602368(0x428e0000, float:71.0)
            r6 = 1102577664(0x41b80000, float:23.0)
            r7 = 1096810496(0x41600000, float:14.0)
            r8 = 1095761920(0x41500000, float:13.0)
            r9 = 1110441984(0x42300000, float:44.0)
            if (r11 != r0) goto L_0x0225
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.isOutOwner()
            if (r11 == 0) goto L_0x0169
            int r11 = r10.layoutWidth
            int r12 = r10.backgroundWidth
            int r11 = r11 - r12
            r12 = 1113849856(0x42640000, float:57.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 + r12
            r10.seekBarX = r11
            int r11 = r10.layoutWidth
            int r12 = r10.backgroundWidth
            int r11 = r11 - r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r11 = r11 + r12
            r10.buttonX = r11
            int r11 = r10.layoutWidth
            int r12 = r10.backgroundWidth
            int r11 = r11 - r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r11 = r11 + r12
            r10.timeAudioX = r11
            goto L_0x01a0
        L_0x0169:
            boolean r11 = r10.isChat
            if (r11 == 0) goto L_0x018c
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.needDrawAvatar()
            if (r11 == 0) goto L_0x018c
            r11 = 1122238464(0x42e40000, float:114.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.seekBarX = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r10.buttonX = r11
            r11 = 1123549184(0x42var_, float:124.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.timeAudioX = r11
            goto L_0x01a0
        L_0x018c:
            r11 = 1115947008(0x42840000, float:66.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.seekBarX = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r10.buttonX = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r10.timeAudioX = r11
        L_0x01a0:
            boolean r11 = r10.hasLinkPreview
            if (r11 == 0) goto L_0x01bf
            int r11 = r10.seekBarX
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = r11 + r12
            r10.seekBarX = r11
            int r11 = r10.buttonX
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = r11 + r12
            r10.buttonX = r11
            int r11 = r10.timeAudioX
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = r11 + r12
            r10.timeAudioX = r11
        L_0x01bf:
            org.telegram.ui.Components.SeekBarWaveform r11 = r10.seekBarWaveform
            int r12 = r10.backgroundWidth
            boolean r14 = r10.hasLinkPreview
            if (r14 == 0) goto L_0x01ca
            r14 = 10
            goto L_0x01cb
        L_0x01ca:
            r14 = 0
        L_0x01cb:
            int r14 = r14 + 92
            float r14 = (float) r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r12 = r12 - r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r11.setSize(r12, r14)
            org.telegram.ui.Components.SeekBar r11 = r10.seekBar
            int r12 = r10.backgroundWidth
            boolean r14 = r10.hasLinkPreview
            if (r14 == 0) goto L_0x01e4
            r13 = 10
        L_0x01e4:
            int r13 = r13 + 72
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 - r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r11.setSize(r12, r13)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r12 = r10.namesOffset
            int r11 = r11 + r12
            int r12 = r10.mediaOffsetY
            int r11 = r11 + r12
            r10.seekBarY = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r12 = r10.namesOffset
            int r11 = r11 + r12
            int r12 = r10.mediaOffsetY
            int r11 = r11 + r12
            r10.buttonY = r11
            org.telegram.ui.Components.RadialProgress2 r11 = r10.radialProgress
            int r12 = r10.buttonX
            int r13 = r10.buttonY
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r14 = r14 + r12
            int r15 = r10.buttonY
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r15 = r15 + r0
            r11.setProgressRect(r12, r13, r14, r15)
            r10.updatePlayingMessageProgress()
            goto L_0x054c
        L_0x0225:
            r0 = 5
            if (r11 != r0) goto L_0x02fa
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.isOutOwner()
            if (r11 == 0) goto L_0x0257
            int r11 = r10.layoutWidth
            int r12 = r10.backgroundWidth
            int r11 = r11 - r12
            r12 = 1113587712(0x42600000, float:56.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 + r12
            r10.seekBarX = r11
            int r11 = r10.layoutWidth
            int r12 = r10.backgroundWidth
            int r11 = r11 - r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r11 = r11 + r12
            r10.buttonX = r11
            int r11 = r10.layoutWidth
            int r12 = r10.backgroundWidth
            int r11 = r11 - r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r11 = r11 + r12
            r10.timeAudioX = r11
            goto L_0x028e
        L_0x0257:
            boolean r11 = r10.isChat
            if (r11 == 0) goto L_0x027a
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.needDrawAvatar()
            if (r11 == 0) goto L_0x027a
            r11 = 1122107392(0x42e20000, float:113.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.seekBarX = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r10.buttonX = r11
            r11 = 1123549184(0x42var_, float:124.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.timeAudioX = r11
            goto L_0x028e
        L_0x027a:
            r11 = 1115815936(0x42820000, float:65.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.seekBarX = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r10.buttonX = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r10.timeAudioX = r11
        L_0x028e:
            boolean r11 = r10.hasLinkPreview
            if (r11 == 0) goto L_0x02ad
            int r11 = r10.seekBarX
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = r11 + r12
            r10.seekBarX = r11
            int r11 = r10.buttonX
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = r11 + r12
            r10.buttonX = r11
            int r11 = r10.timeAudioX
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = r11 + r12
            r10.timeAudioX = r11
        L_0x02ad:
            org.telegram.ui.Components.SeekBar r11 = r10.seekBar
            int r12 = r10.backgroundWidth
            boolean r14 = r10.hasLinkPreview
            if (r14 == 0) goto L_0x02b7
            r13 = 10
        L_0x02b7:
            int r13 = r13 + 65
            float r13 = (float) r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 - r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r11.setSize(r12, r13)
            r11 = 1105723392(0x41e80000, float:29.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r12 = r10.namesOffset
            int r11 = r11 + r12
            int r12 = r10.mediaOffsetY
            int r11 = r11 + r12
            r10.seekBarY = r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r12 = r10.namesOffset
            int r11 = r11 + r12
            int r12 = r10.mediaOffsetY
            int r11 = r11 + r12
            r10.buttonY = r11
            org.telegram.ui.Components.RadialProgress2 r11 = r10.radialProgress
            int r12 = r10.buttonX
            int r13 = r10.buttonY
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r14 = r14 + r12
            int r15 = r10.buttonY
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r15 = r15 + r0
            r11.setProgressRect(r12, r13, r14, r15)
            r10.updatePlayingMessageProgress()
            goto L_0x054c
        L_0x02fa:
            if (r11 != r12) goto L_0x037d
            boolean r11 = r10.drawPhotoImage
            if (r11 != 0) goto L_0x037d
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.isOutOwner()
            if (r11 == 0) goto L_0x0315
            int r11 = r10.layoutWidth
            int r12 = r10.backgroundWidth
            int r11 = r11 - r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r11 = r11 + r12
            r10.buttonX = r11
            goto L_0x032e
        L_0x0315:
            boolean r11 = r10.isChat
            if (r11 == 0) goto L_0x0328
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.needDrawAvatar()
            if (r11 == 0) goto L_0x0328
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r10.buttonX = r11
            goto L_0x032e
        L_0x0328:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r10.buttonX = r11
        L_0x032e:
            boolean r11 = r10.hasLinkPreview
            if (r11 == 0) goto L_0x033b
            int r11 = r10.buttonX
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = r11 + r12
            r10.buttonX = r11
        L_0x033b:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r12 = r10.namesOffset
            int r11 = r11 + r12
            int r12 = r10.mediaOffsetY
            int r11 = r11 + r12
            r10.buttonY = r11
            org.telegram.ui.Components.RadialProgress2 r11 = r10.radialProgress
            int r12 = r10.buttonX
            int r13 = r10.buttonY
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r14 = r14 + r12
            int r0 = r10.buttonY
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 + r1
            r11.setProgressRect(r12, r13, r14, r0)
            org.telegram.messenger.ImageReceiver r11 = r10.photoImage
            int r12 = r10.buttonX
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r12 = r12 - r13
            int r13 = r10.buttonY
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r13 = r13 - r14
            org.telegram.messenger.ImageReceiver r14 = r10.photoImage
            int r14 = r14.getImageWidth()
            org.telegram.messenger.ImageReceiver r15 = r10.photoImage
            int r15 = r15.getImageHeight()
            r11.setImageCoords(r12, r13, r14, r15)
            goto L_0x054c
        L_0x037d:
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            int r13 = r11.type
            r0 = 12
            if (r13 != r0) goto L_0x03c3
            boolean r11 = r11.isOutOwner()
            if (r11 == 0) goto L_0x0396
            int r11 = r10.layoutWidth
            int r12 = r10.backgroundWidth
            int r11 = r11 - r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r11 = r11 + r12
            goto L_0x03ad
        L_0x0396:
            boolean r11 = r10.isChat
            if (r11 == 0) goto L_0x03a9
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.needDrawAvatar()
            if (r11 == 0) goto L_0x03a9
            r11 = 1116733440(0x42900000, float:72.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            goto L_0x03ad
        L_0x03a9:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
        L_0x03ad:
            org.telegram.messenger.ImageReceiver r12 = r10.photoImage
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r14 = r10.namesOffset
            int r13 = r13 + r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r12.setImageCoords(r11, r13, r14, r15)
            goto L_0x054c
        L_0x03c3:
            if (r13 != 0) goto L_0x0413
            boolean r11 = r10.hasLinkPreview
            if (r11 != 0) goto L_0x03d1
            boolean r11 = r10.hasGamePreview
            if (r11 != 0) goto L_0x03d1
            boolean r11 = r10.hasInvoicePreview
            if (r11 == 0) goto L_0x0413
        L_0x03d1:
            boolean r11 = r10.hasGamePreview
            if (r11 == 0) goto L_0x03dd
            int r11 = r10.unmovedTextX
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = r11 - r13
            goto L_0x03f3
        L_0x03dd:
            boolean r11 = r10.hasInvoicePreview
            if (r11 == 0) goto L_0x03ea
            int r11 = r10.unmovedTextX
            r13 = 1065353216(0x3var_, float:1.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            goto L_0x03f2
        L_0x03ea:
            int r11 = r10.unmovedTextX
            r13 = 1065353216(0x3var_, float:1.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
        L_0x03f2:
            int r11 = r11 + r13
        L_0x03f3:
            boolean r13 = r10.isSmallImage
            if (r13 == 0) goto L_0x0401
            int r13 = r10.backgroundWidth
            int r11 = r11 + r13
            r13 = 1117913088(0x42a20000, float:81.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            goto L_0x042a
        L_0x0401:
            boolean r13 = r10.hasInvoicePreview
            if (r13 == 0) goto L_0x040e
            r13 = 1086953882(0x40CLASSNAMEa, float:6.3)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = -r13
            goto L_0x0437
        L_0x040e:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r15)
            goto L_0x0437
        L_0x0413:
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.isOutOwner()
            if (r11 == 0) goto L_0x0439
            boolean r11 = r10.mediaBackground
            if (r11 == 0) goto L_0x042c
            int r11 = r10.layoutWidth
            int r13 = r10.backgroundWidth
            int r11 = r11 - r13
            r13 = 1077936128(0x40400000, float:3.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
        L_0x042a:
            int r11 = r11 - r13
            goto L_0x045b
        L_0x042c:
            int r11 = r10.layoutWidth
            int r13 = r10.backgroundWidth
            int r11 = r11 - r13
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
        L_0x0437:
            int r11 = r11 + r13
            goto L_0x045b
        L_0x0439:
            boolean r11 = r10.isChat
            if (r11 == 0) goto L_0x0448
            boolean r11 = r10.isAvatarVisible
            if (r11 == 0) goto L_0x0448
            r11 = 1115422720(0x427CLASSNAME, float:63.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            goto L_0x044e
        L_0x0448:
            r11 = 1097859072(0x41700000, float:15.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x044e:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r13 = r10.currentPosition
            if (r13 == 0) goto L_0x045b
            boolean r13 = r13.edge
            if (r13 != 0) goto L_0x045b
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r15)
            goto L_0x042a
        L_0x045b:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r13 = r10.currentPosition
            r15 = 1073741824(0x40000000, float:2.0)
            if (r13 == 0) goto L_0x0483
            int r13 = r13.flags
            r12 = r12 & r13
            if (r12 != 0) goto L_0x046b
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = r11 - r12
        L_0x046b:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r12 = r10.currentPosition
            int r12 = r12.leftSpanOffset
            if (r12 == 0) goto L_0x0483
            float r12 = (float) r12
            r13 = 1148846080(0x447a0000, float:1000.0)
            float r12 = r12 / r13
            int r13 = r10.getGroupPhotosWidth()
            float r13 = (float) r13
            float r12 = r12 * r13
            double r12 = (double) r12
            double r12 = java.lang.Math.ceil(r12)
            int r12 = (int) r12
            int r11 = r11 + r12
        L_0x0483:
            org.telegram.messenger.MessageObject r12 = r10.currentMessageObject
            int r12 = r12.type
            if (r12 == 0) goto L_0x048e
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = r11 - r12
        L_0x048e:
            org.telegram.messenger.ImageReceiver r12 = r10.photoImage
            int r13 = r12.getImageY()
            org.telegram.messenger.ImageReceiver r0 = r10.photoImage
            int r0 = r0.getImageWidth()
            org.telegram.messenger.ImageReceiver r1 = r10.photoImage
            int r1 = r1.getImageHeight()
            r12.setImageCoords(r11, r13, r0, r1)
            float r11 = (float) r11
            org.telegram.messenger.ImageReceiver r12 = r10.photoImage
            int r12 = r12.getImageWidth()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r12 = r12 - r13
            float r12 = (float) r12
            float r12 = r12 / r15
            float r11 = r11 + r12
            int r11 = (int) r11
            r10.buttonX = r11
            org.telegram.messenger.ImageReceiver r11 = r10.photoImage
            int r11 = r11.getImageY()
            org.telegram.messenger.ImageReceiver r12 = r10.photoImage
            int r12 = r12.getImageHeight()
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r12 = r12 - r13
            int r12 = r12 / 2
            int r11 = r11 + r12
            r10.buttonY = r11
            org.telegram.ui.Components.RadialProgress2 r11 = r10.radialProgress
            int r12 = r10.buttonX
            int r13 = r10.buttonY
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r15 = r15 + r12
            int r0 = r10.buttonY
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r0 = r0 + r14
            r11.setProgressRect(r12, r13, r15, r0)
            android.graphics.RectF r11 = r10.deleteProgressRect
            int r12 = r10.buttonX
            r13 = 1084227584(0x40a00000, float:5.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 + r13
            float r12 = (float) r12
            int r13 = r10.buttonY
            r14 = 1084227584(0x40a00000, float:5.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r13 = r13 + r14
            float r13 = (float) r13
            int r14 = r10.buttonX
            r15 = 1110179840(0x422CLASSNAME, float:43.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r14 = r14 + r15
            float r14 = (float) r14
            int r15 = r10.buttonY
            r0 = 1110179840(0x422CLASSNAME, float:43.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r15 = r15 + r0
            float r15 = (float) r15
            r11.set(r12, r13, r14, r15)
            int r11 = r10.documentAttachType
            r12 = 4
            if (r11 == r12) goto L_0x0515
            r12 = 2
            if (r11 != r12) goto L_0x054c
        L_0x0515:
            org.telegram.messenger.ImageReceiver r11 = r10.photoImage
            int r11 = r11.getImageX()
            r12 = 1090519040(0x41000000, float:8.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 + r12
            r10.videoButtonX = r11
            org.telegram.messenger.ImageReceiver r11 = r10.photoImage
            int r11 = r11.getImageY()
            r12 = 1090519040(0x41000000, float:8.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 + r12
            r10.videoButtonY = r11
            org.telegram.ui.Components.RadialProgress2 r11 = r10.videoRadialProgress
            int r12 = r10.videoButtonX
            int r13 = r10.videoButtonY
            r14 = 1103101952(0x41CLASSNAME, float:24.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = r14 + r12
            int r15 = r10.videoButtonY
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r15 = r15 + r0
            r11.setProgressRect(r12, r13, r14, r15)
        L_0x054c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onLayout(boolean, int, int, int, int):void");
    }

    public boolean needDelayRoundProgressDraw() {
        int i = this.documentAttachType;
        return (i == 7 || i == 4) && this.currentMessageObject.type != 5 && MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
    }

    public void drawRoundProgress(Canvas canvas) {
        this.rect.set(((float) this.photoImage.getImageX()) + AndroidUtilities.dpf2(1.5f), ((float) this.photoImage.getImageY()) + AndroidUtilities.dpf2(1.5f), ((float) this.photoImage.getImageX2()) - AndroidUtilities.dpf2(1.5f), ((float) this.photoImage.getImageY2()) - AndroidUtilities.dpf2(1.5f));
        canvas.drawArc(this.rect, -90.0f, this.currentMessageObject.audioProgress * 360.0f, false, Theme.chat_radialProgressPaint);
    }

    private void updatePollAnimations() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.voteLastUpdateTime;
        if (j > 17) {
            j = 17;
        }
        this.voteLastUpdateTime = currentTimeMillis;
        if (this.pollVoteInProgress) {
            this.voteRadOffset += ((float) (360 * j)) / 2000.0f;
            float f = this.voteRadOffset;
            int i = 360;
            this.voteRadOffset = f - ((float) (((int) (f / 360.0f)) * 360));
            this.voteCurrentProgressTime += (float) j;
            if (this.voteCurrentProgressTime >= 500.0f) {
                this.voteCurrentProgressTime = 500.0f;
            }
            if (this.voteRisingCircleLength) {
                this.voteCurrentCircleLength = (AndroidUtilities.accelerateInterpolator.getInterpolation(this.voteCurrentProgressTime / 500.0f) * 266.0f) + 4.0f;
            } else {
                if (!this.firstCircleLength) {
                    i = 270;
                }
                this.voteCurrentCircleLength = 4.0f - (((float) i) * (1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(this.voteCurrentProgressTime / 500.0f)));
            }
            if (this.voteCurrentProgressTime == 500.0f) {
                if (this.voteRisingCircleLength) {
                    this.voteRadOffset += 270.0f;
                    this.voteCurrentCircleLength = -266.0f;
                }
                this.voteRisingCircleLength = !this.voteRisingCircleLength;
                if (this.firstCircleLength) {
                    this.firstCircleLength = false;
                }
                this.voteCurrentProgressTime = 0.0f;
            }
            invalidate();
        }
        if (this.animatePollAnswer) {
            this.pollAnimationProgressTime += (float) j;
            if (this.pollAnimationProgressTime >= 300.0f) {
                this.pollAnimationProgressTime = 300.0f;
            }
            this.pollAnimationProgress = AndroidUtilities.decelerateInterpolator.getInterpolation(this.pollAnimationProgressTime / 300.0f);
            if (this.pollAnimationProgress >= 1.0f) {
                this.pollAnimationProgress = 1.0f;
                this.animatePollAnswer = false;
                this.animatePollAnswerAlpha = false;
                this.pollVoteInProgress = false;
                this.pollUnvoteInProgress = false;
            }
            invalidate();
        }
    }

    private void drawContent(Canvas canvas) {
        boolean z;
        int i;
        Drawable drawable;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        boolean z2;
        Drawable drawable2;
        int i8;
        int i9;
        ViewGroup viewGroup;
        MessageObject.GroupedMessages groupedMessages;
        boolean z3;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        int i15;
        int i16;
        int i17;
        Drawable drawable3;
        int i18;
        int i19;
        boolean z4;
        int i20;
        int i21;
        Canvas canvas2 = canvas;
        if (this.needNewVisiblePart && this.currentMessageObject.type == 0) {
            getLocalVisibleRect(this.scrollRect);
            Rect rect2 = this.scrollRect;
            int i22 = rect2.top;
            setVisiblePart(i22, rect2.bottom - i22, this.parentHeight);
            this.needNewVisiblePart = false;
        }
        this.forceNotDrawTime = this.currentMessagesGroup != null;
        this.photoImage.setVisible(!PhotoViewer.isShowingImage(this.currentMessageObject) && !SecretMediaViewer.getInstance().isShowingImage(this.currentMessageObject), false);
        if (!this.photoImage.getVisible()) {
            this.mediaWasInvisible = true;
            this.timeWasInvisible = true;
            int i23 = this.animatingNoSound;
            if (i23 == 1) {
                this.animatingNoSoundProgress = 0.0f;
                this.animatingNoSound = 0;
            } else if (i23 == 2) {
                this.animatingNoSoundProgress = 1.0f;
                this.animatingNoSound = 0;
            }
        } else if (this.groupPhotoInvisible) {
            this.timeWasInvisible = true;
        } else if (this.mediaWasInvisible || this.timeWasInvisible) {
            if (this.mediaWasInvisible) {
                this.controlsAlpha = 0.0f;
                this.mediaWasInvisible = false;
            }
            if (this.timeWasInvisible) {
                this.timeAlpha = 0.0f;
                this.timeWasInvisible = false;
            }
            this.lastControlsAlphaChangeTime = System.currentTimeMillis();
            this.totalChangeTime = 0;
        }
        this.radialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
        this.videoRadialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
        MessageObject messageObject = this.currentMessageObject;
        float f = 11.0f;
        if (messageObject.type == 0) {
            if (messageObject.isOutOwner()) {
                this.textX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
            } else {
                this.textX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp((this.mediaBackground || !this.drawPinnedBottom) ? 17.0f : 11.0f);
            }
            if (this.hasGamePreview) {
                this.textX += AndroidUtilities.dp(11.0f);
                this.textY = AndroidUtilities.dp(14.0f) + this.namesOffset;
                StaticLayout staticLayout = this.siteNameLayout;
                if (staticLayout != null) {
                    this.textY += staticLayout.getLineBottom(staticLayout.getLineCount() - 1);
                }
            } else if (this.hasInvoicePreview) {
                this.textY = AndroidUtilities.dp(14.0f) + this.namesOffset;
                StaticLayout staticLayout2 = this.siteNameLayout;
                if (staticLayout2 != null) {
                    this.textY += staticLayout2.getLineBottom(staticLayout2.getLineCount() - 1);
                }
            } else {
                this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
            }
            this.unmovedTextX = this.textX;
            if (!(this.currentMessageObject.textXOffset == 0.0f || this.replyNameLayout == null)) {
                int dp = this.backgroundWidth - AndroidUtilities.dp(31.0f);
                MessageObject messageObject2 = this.currentMessageObject;
                int i24 = dp - messageObject2.textWidth;
                if (!this.hasNewLineForTime) {
                    i24 -= this.timeWidth + AndroidUtilities.dp((float) ((messageObject2.isOutOwner() ? 20 : 0) + 4));
                }
                if (i24 > 0) {
                    this.textX += i24;
                }
            }
            ArrayList<MessageObject.TextLayoutBlock> arrayList = this.currentMessageObject.textLayoutBlocks;
            if (arrayList != null && !arrayList.isEmpty()) {
                if (this.fullyDraw) {
                    this.firstVisibleBlockNum = 0;
                    this.lastVisibleBlockNum = this.currentMessageObject.textLayoutBlocks.size();
                }
                int i25 = this.firstVisibleBlockNum;
                if (i25 >= 0) {
                    int i26 = i25;
                    while (i26 <= this.lastVisibleBlockNum && i26 < this.currentMessageObject.textLayoutBlocks.size()) {
                        MessageObject.TextLayoutBlock textLayoutBlock = this.currentMessageObject.textLayoutBlocks.get(i26);
                        canvas.save();
                        canvas2.translate((float) (this.textX - (textLayoutBlock.isRtl() ? (int) Math.ceil((double) this.currentMessageObject.textXOffset) : 0)), ((float) this.textY) + textLayoutBlock.textYOffset);
                        if (this.pressedLink != null && i26 == this.linkBlockNum) {
                            for (int i27 = 0; i27 < this.urlPath.size(); i27++) {
                                canvas2.drawPath(this.urlPath.get(i27), Theme.chat_urlPaint);
                            }
                        }
                        if (i26 == this.linkSelectionBlockNum && !this.urlPathSelection.isEmpty()) {
                            for (int i28 = 0; i28 < this.urlPathSelection.size(); i28++) {
                                canvas2.drawPath(this.urlPathSelection.get(i28), Theme.chat_textSearchSelectionPaint);
                            }
                        }
                        if (this.delegate.getTextSelectionHelper() != null) {
                            this.delegate.getTextSelectionHelper().draw(this.currentMessageObject, textLayoutBlock, canvas2);
                        }
                        try {
                            textLayoutBlock.textLayout.draw(canvas2);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        canvas.restore();
                        i26++;
                    }
                }
            }
            if (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview) {
                if (this.hasGamePreview) {
                    i12 = AndroidUtilities.dp(14.0f) + this.namesOffset;
                    i13 = this.unmovedTextX - AndroidUtilities.dp(10.0f);
                } else {
                    if (this.hasInvoicePreview) {
                        i12 = AndroidUtilities.dp(14.0f) + this.namesOffset;
                        i11 = this.unmovedTextX;
                        i10 = AndroidUtilities.dp(1.0f);
                    } else {
                        i12 = this.textY + this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f);
                        i11 = this.unmovedTextX;
                        i10 = AndroidUtilities.dp(1.0f);
                    }
                    i13 = i11 + i10;
                }
                int i29 = i13;
                if (!this.hasInvoicePreview) {
                    Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outPreviewLine" : "chat_inPreviewLine"));
                    i14 = i29;
                    i15 = 4;
                    canvas.drawRect((float) i29, (float) (i12 - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i29), (float) (this.linkPreviewHeight + i12 + AndroidUtilities.dp(3.0f)), Theme.chat_replyLinePaint);
                } else {
                    i14 = i29;
                    i15 = 4;
                }
                if (this.siteNameLayout != null) {
                    Theme.chat_replyNamePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outSiteNameText" : "chat_inSiteNameText"));
                    canvas.save();
                    if (this.siteNameRtl) {
                        i21 = (this.backgroundWidth - this.siteNameWidth) - AndroidUtilities.dp(32.0f);
                    } else {
                        i21 = this.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f);
                    }
                    canvas2.translate((float) (i14 + i21), (float) (i12 - AndroidUtilities.dp(3.0f)));
                    this.siteNameLayout.draw(canvas2);
                    canvas.restore();
                    StaticLayout staticLayout3 = this.siteNameLayout;
                    i16 = staticLayout3.getLineBottom(staticLayout3.getLineCount() - 1) + i12;
                } else {
                    i16 = i12;
                }
                if ((this.hasGamePreview || this.hasInvoicePreview) && (i20 = this.currentMessageObject.textHeight) != 0) {
                    i12 += i20 + AndroidUtilities.dp(4.0f);
                    i16 += this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
                }
                if ((!this.drawPhotoImage || !this.drawInstantView) && (this.drawInstantViewType != 6 || this.imageBackgroundColor == 0)) {
                    z3 = false;
                } else {
                    if (i16 != i12) {
                        i16 += AndroidUtilities.dp(2.0f);
                    }
                    int i30 = i16;
                    if (this.imageBackgroundSideColor != 0) {
                        int dp2 = i14 + AndroidUtilities.dp(10.0f);
                        ImageReceiver imageReceiver = this.photoImage;
                        imageReceiver.setImageCoords(((this.imageBackgroundSideWidth - imageReceiver.getImageWidth()) / 2) + dp2, i30, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                        this.rect.set((float) dp2, (float) this.photoImage.getImageY(), (float) (dp2 + this.imageBackgroundSideWidth), (float) this.photoImage.getImageY2());
                        Theme.chat_instantViewPaint.setColor(this.imageBackgroundSideColor);
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_instantViewPaint);
                    } else {
                        this.photoImage.setImageCoords(i14 + AndroidUtilities.dp(10.0f), i30, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    }
                    if (this.imageBackgroundColor != 0) {
                        this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) this.photoImage.getImageX2(), (float) this.photoImage.getImageY2());
                        if (this.imageBackgroundGradientColor != 0) {
                            if (this.gradientShader == null) {
                                Rect gradientPoints = BackgroundGradientDrawable.getGradientPoints(AndroidUtilities.getWallpaperRotation(this.imageBackgroundGradientRotation, false), (int) this.rect.width(), (int) this.rect.height());
                                this.gradientShader = new LinearGradient((float) gradientPoints.left, (float) gradientPoints.top, (float) gradientPoints.right, (float) gradientPoints.bottom, new int[]{this.imageBackgroundColor, this.imageBackgroundGradientColor}, (float[]) null, Shader.TileMode.CLAMP);
                            }
                            Theme.chat_instantViewPaint.setShader(this.gradientShader);
                        } else {
                            Theme.chat_instantViewPaint.setShader((Shader) null);
                            Theme.chat_instantViewPaint.setColor(this.imageBackgroundColor);
                        }
                        if (this.imageBackgroundSideColor != 0) {
                            i19 = i30;
                            canvas.drawRect((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) this.photoImage.getImageX2(), (float) this.photoImage.getImageY2(), Theme.chat_instantViewPaint);
                        } else {
                            i19 = i30;
                            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_instantViewPaint);
                        }
                        Theme.chat_instantViewPaint.setShader((Shader) null);
                    } else {
                        i19 = i30;
                    }
                    if (!this.drawPhotoImage || !this.drawInstantView) {
                        z4 = false;
                    } else {
                        if (this.drawImageButton) {
                            int dp3 = AndroidUtilities.dp(48.0f);
                            this.buttonX = (int) (((float) this.photoImage.getImageX()) + (((float) (this.photoImage.getImageWidth() - dp3)) / 2.0f));
                            this.buttonY = (int) (((float) this.photoImage.getImageY()) + (((float) (this.photoImage.getImageHeight() - dp3)) / 2.0f));
                            RadialProgress2 radialProgress2 = this.radialProgress;
                            int i31 = this.buttonX;
                            int i32 = this.buttonY;
                            radialProgress2.setProgressRect(i31, i32, i31 + dp3, dp3 + i32);
                        }
                        z4 = this.photoImage.draw(canvas2);
                    }
                    z3 = z4;
                    i16 = this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0f) + i19;
                }
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_messageTextOut"));
                    Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
                } else {
                    Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_messageTextIn"));
                    Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
                }
                if (this.titleLayout != null) {
                    if (i16 != i12) {
                        i16 += AndroidUtilities.dp(2.0f);
                    }
                    i17 = i16 - AndroidUtilities.dp(1.0f);
                    canvas.save();
                    canvas2.translate((float) (i14 + AndroidUtilities.dp(10.0f) + this.titleX), (float) (i16 - AndroidUtilities.dp(3.0f)));
                    this.titleLayout.draw(canvas2);
                    canvas.restore();
                    StaticLayout staticLayout4 = this.titleLayout;
                    i16 += staticLayout4.getLineBottom(staticLayout4.getLineCount() - 1);
                } else {
                    i17 = 0;
                }
                if (this.authorLayout != null) {
                    if (i16 != i12) {
                        i16 += AndroidUtilities.dp(2.0f);
                    }
                    if (i17 == 0) {
                        i17 = i16 - AndroidUtilities.dp(1.0f);
                    }
                    canvas.save();
                    canvas2.translate((float) (i14 + AndroidUtilities.dp(10.0f) + this.authorX), (float) (i16 - AndroidUtilities.dp(3.0f)));
                    this.authorLayout.draw(canvas2);
                    canvas.restore();
                    StaticLayout staticLayout5 = this.authorLayout;
                    i16 += staticLayout5.getLineBottom(staticLayout5.getLineCount() - 1);
                }
                if (this.descriptionLayout != null) {
                    if (i16 != i12) {
                        i16 += AndroidUtilities.dp(2.0f);
                    }
                    if (i17 == 0) {
                        i17 = i16 - AndroidUtilities.dp(1.0f);
                    }
                    this.descriptionY = i16 - AndroidUtilities.dp(3.0f);
                    canvas.save();
                    canvas2.translate((float) (i14 + (this.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f)) + this.descriptionX), (float) this.descriptionY);
                    if (this.pressedLink != null && this.linkBlockNum == -10) {
                        for (int i33 = 0; i33 < this.urlPath.size(); i33++) {
                            canvas2.drawPath(this.urlPath.get(i33), Theme.chat_urlPaint);
                        }
                    }
                    if (this.delegate.getTextSelectionHelper() != null && getDelegate().getTextSelectionHelper().isSelected(this.currentMessageObject)) {
                        this.delegate.getTextSelectionHelper().drawDescription(this.currentMessageObject.isOutOwner(), this.descriptionLayout, canvas2);
                    }
                    this.descriptionLayout.draw(canvas2);
                    canvas.restore();
                    StaticLayout staticLayout6 = this.descriptionLayout;
                    i16 += staticLayout6.getLineBottom(staticLayout6.getLineCount() - 1);
                }
                if (this.drawPhotoImage && !this.drawInstantView) {
                    if (i16 != i12) {
                        i16 += AndroidUtilities.dp(2.0f);
                    }
                    if (this.isSmallImage) {
                        this.photoImage.setImageCoords((i14 + this.backgroundWidth) - AndroidUtilities.dp(81.0f), i17, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    } else {
                        this.photoImage.setImageCoords(i14 + (this.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f)), i16, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                        if (this.drawImageButton) {
                            int dp4 = AndroidUtilities.dp(48.0f);
                            this.buttonX = (int) (((float) this.photoImage.getImageX()) + (((float) (this.photoImage.getImageWidth() - dp4)) / 2.0f));
                            this.buttonY = (int) (((float) this.photoImage.getImageY()) + (((float) (this.photoImage.getImageHeight() - dp4)) / 2.0f));
                            RadialProgress2 radialProgress22 = this.radialProgress;
                            int i34 = this.buttonX;
                            int i35 = this.buttonY;
                            radialProgress22.setProgressRect(i34, i35, i34 + dp4, dp4 + i35);
                        }
                    }
                    if (!this.currentMessageObject.isRoundVideo() || !MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || !MediaController.getInstance().isVideoDrawingReady()) {
                        z3 = this.photoImage.draw(canvas2);
                    } else {
                        this.drawTime = true;
                        z3 = true;
                    }
                }
                int i36 = this.documentAttachType;
                if (i36 == i15 || i36 == 2) {
                    this.videoButtonX = this.photoImage.getImageX() + AndroidUtilities.dp(8.0f);
                    this.videoButtonY = this.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                    RadialProgress2 radialProgress23 = this.videoRadialProgress;
                    int i37 = this.videoButtonX;
                    radialProgress23.setProgressRect(i37, this.videoButtonY, AndroidUtilities.dp(24.0f) + i37, this.videoButtonY + AndroidUtilities.dp(24.0f));
                }
                if (this.photosCountLayout != null && this.photoImage.getVisible()) {
                    int imageX = ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.photosCountWidth;
                    int imageY = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f);
                    this.rect.set((float) (imageX - AndroidUtilities.dp(4.0f)), (float) (imageY - AndroidUtilities.dp(1.5f)), (float) (this.photosCountWidth + imageX + AndroidUtilities.dp(4.0f)), (float) (imageY + AndroidUtilities.dp(14.5f)));
                    int alpha = Theme.chat_timeBackgroundPaint.getAlpha();
                    Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) alpha) * this.controlsAlpha));
                    Theme.chat_durationPaint.setAlpha((int) (this.controlsAlpha * 255.0f));
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                    Theme.chat_timeBackgroundPaint.setAlpha(alpha);
                    canvas.save();
                    canvas2.translate((float) imageX, (float) imageY);
                    this.photosCountLayout.draw(canvas2);
                    canvas.restore();
                    Theme.chat_durationPaint.setAlpha(255);
                }
                if (this.videoInfoLayout != null && ((!this.drawPhotoImage || this.photoImage.getVisible()) && this.imageBackgroundSideColor == 0)) {
                    if (!this.hasGamePreview && !this.hasInvoicePreview && this.documentAttachType != 8) {
                        i18 = ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.durationWidth;
                        i16 = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f);
                        this.rect.set((float) (i18 - AndroidUtilities.dp(4.0f)), (float) (i16 - AndroidUtilities.dp(1.5f)), (float) (this.durationWidth + i18 + AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(14.5f) + i16));
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                    } else if (this.drawPhotoImage) {
                        i18 = this.photoImage.getImageX() + AndroidUtilities.dp(8.5f);
                        i16 = this.photoImage.getImageY() + AndroidUtilities.dp(6.0f);
                        this.rect.set((float) (i18 - AndroidUtilities.dp(4.0f)), (float) (i16 - AndroidUtilities.dp(1.5f)), (float) (this.durationWidth + i18 + AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(this.documentAttachType == 8 ? 14.5f : 16.5f) + i16));
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                    } else {
                        i18 = i14;
                    }
                    canvas.save();
                    canvas2.translate((float) i18, (float) i16);
                    if (this.hasInvoicePreview) {
                        if (this.drawPhotoImage) {
                            Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_previewGameText"));
                        } else if (this.currentMessageObject.isOutOwner()) {
                            Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_messageTextOut"));
                        } else {
                            Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_messageTextIn"));
                        }
                    }
                    this.videoInfoLayout.draw(canvas2);
                    canvas.restore();
                }
                if (this.drawInstantView) {
                    int dp5 = i12 + this.linkPreviewHeight + AndroidUtilities.dp(10.0f);
                    Paint paint = Theme.chat_instantViewRectPaint;
                    if (this.currentMessageObject.isOutOwner()) {
                        drawable3 = Theme.chat_msgOutInstantDrawable;
                        Theme.chat_instantViewPaint.setColor(Theme.getColor("chat_outPreviewInstantText"));
                        paint.setColor(Theme.getColor("chat_outPreviewInstantText"));
                    } else {
                        drawable3 = Theme.chat_msgInInstantDrawable;
                        Theme.chat_instantViewPaint.setColor(Theme.getColor("chat_inPreviewInstantText"));
                        paint.setColor(Theme.getColor("chat_inPreviewInstantText"));
                    }
                    if (Build.VERSION.SDK_INT >= 21) {
                        this.selectorDrawableMaskType = 0;
                        this.selectorDrawable.setBounds(i14, dp5, this.instantWidth + i14, AndroidUtilities.dp(36.0f) + dp5);
                        this.selectorDrawable.draw(canvas2);
                    }
                    this.rect.set((float) i14, (float) dp5, (float) (this.instantWidth + i14), (float) (AndroidUtilities.dp(36.0f) + dp5));
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), paint);
                    if (this.drawInstantViewType == 0) {
                        BaseCell.setDrawableBounds(drawable3, ((this.instantTextLeftX + this.instantTextX) + i14) - AndroidUtilities.dp(15.0f), AndroidUtilities.dp(11.5f) + dp5, AndroidUtilities.dp(9.0f), AndroidUtilities.dp(13.0f));
                        drawable3.draw(canvas2);
                    }
                    if (this.instantViewLayout != null) {
                        canvas.save();
                        canvas2.translate((float) (i14 + this.instantTextX), (float) (dp5 + AndroidUtilities.dp(10.5f)));
                        this.instantViewLayout.draw(canvas2);
                        canvas.restore();
                    }
                }
            } else {
                z3 = false;
            }
            this.drawTime = true;
            z = z3;
        } else if (!this.drawPhotoImage) {
            z = false;
        } else if (!messageObject.isRoundVideo() || !MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || !MediaController.getInstance().isVideoDrawingReady()) {
            if (this.currentMessageObject.type == 5 && Theme.chat_roundVideoShadow != null) {
                int imageX2 = this.photoImage.getImageX() - AndroidUtilities.dp(3.0f);
                int imageY2 = this.photoImage.getImageY() - AndroidUtilities.dp(2.0f);
                Theme.chat_roundVideoShadow.setAlpha(255);
                Theme.chat_roundVideoShadow.setBounds(imageX2, imageY2, AndroidUtilities.roundMessageSize + imageX2 + AndroidUtilities.dp(6.0f), AndroidUtilities.roundMessageSize + imageY2 + AndroidUtilities.dp(6.0f));
                Theme.chat_roundVideoShadow.draw(canvas2);
                if (!this.photoImage.hasBitmapImage() || this.photoImage.getCurrentAlpha() != 1.0f) {
                    Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outBubble" : "chat_inBubble"));
                    canvas2.drawCircle(this.photoImage.getCenterX(), this.photoImage.getCenterY(), (float) (this.photoImage.getImageWidth() / 2), Theme.chat_docBackPaint);
                }
            }
            CheckBoxBase checkBoxBase = this.photoCheckBox;
            this.drawPhotoCheckBox = checkBoxBase != null && (this.checkBoxVisible || checkBoxBase.getProgress() != 0.0f || this.checkBoxAnimationInProgress) && (groupedMessages = this.currentMessagesGroup) != null && groupedMessages.messages.size() > 1;
            if (!this.drawPhotoCheckBox || ((!this.photoCheckBox.isChecked() && this.photoCheckBox.getProgress() == 0.0f && !this.checkBoxAnimationInProgress) || textIsSelectionMode())) {
                this.photoImage.setSideClip(0.0f);
            } else {
                Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outBubbleSelected" : "chat_inBubbleSelected"));
                this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) this.photoImage.getImageX2(), (float) this.photoImage.getImageY2());
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_replyLinePaint);
                this.photoImage.setSideClip(((float) AndroidUtilities.dp(14.0f)) * this.photoCheckBox.getProgress());
                if (this.checkBoxAnimationInProgress) {
                    this.photoCheckBox.setBackgroundAlpha(this.checkBoxAnimationProgress);
                } else {
                    CheckBoxBase checkBoxBase2 = this.photoCheckBox;
                    checkBoxBase2.setBackgroundAlpha(this.checkBoxVisible ? 1.0f : checkBoxBase2.getProgress());
                }
            }
            z = this.photoImage.draw(canvas2);
            boolean z5 = this.drawTime;
            this.drawTime = this.photoImage.getVisible();
            if (!(this.currentPosition == null || z5 == this.drawTime || (viewGroup = (ViewGroup) getParent()) == null)) {
                if (!this.currentPosition.last) {
                    int childCount = viewGroup.getChildCount();
                    for (int i38 = 0; i38 < childCount; i38++) {
                        View childAt = viewGroup.getChildAt(i38);
                        if (childAt != this && (childAt instanceof ChatMessageCell)) {
                            ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                            if (chatMessageCell.getCurrentMessagesGroup() == this.currentMessagesGroup) {
                                MessageObject.GroupedMessagePosition currentPosition2 = chatMessageCell.getCurrentPosition();
                                if (currentPosition2.last && currentPosition2.maxY == this.currentPosition.maxY && (chatMessageCell.timeX - AndroidUtilities.dp(4.0f)) + chatMessageCell.getLeft() < getRight()) {
                                    chatMessageCell.groupPhotoInvisible = !this.drawTime;
                                    chatMessageCell.invalidate();
                                    viewGroup.invalidate();
                                }
                            }
                        }
                    }
                } else {
                    viewGroup.invalidate();
                }
            }
        } else {
            this.drawTime = true;
            z = true;
        }
        int i39 = this.documentAttachType;
        if (i39 == 2) {
            if (this.photoImage.getVisible() && !this.hasGamePreview && !this.currentMessageObject.needDrawBluredPreview()) {
                int alpha2 = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) alpha2) * this.controlsAlpha));
                Drawable drawable4 = Theme.chat_msgMediaMenuDrawable;
                int imageX3 = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                this.otherX = imageX3;
                int imageY3 = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                this.otherY = imageY3;
                BaseCell.setDrawableBounds(drawable4, imageX3, imageY3);
                Theme.chat_msgMediaMenuDrawable.draw(canvas2);
                Theme.chat_msgMediaMenuDrawable.setAlpha(alpha2);
            }
        } else if (i39 == 7) {
            if (this.durationLayout != null) {
                boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (isPlayingMessage && this.currentMessageObject.type == 5) {
                    drawRoundProgress(canvas);
                    drawOverlays(canvas);
                }
                MessageObject messageObject3 = this.currentMessageObject;
                if (messageObject3.type == 5) {
                    int dp6 = this.backgroundDrawableLeft + AndroidUtilities.dp(8.0f);
                    int dp7 = this.layoutHeight - AndroidUtilities.dp((float) (28 - (this.drawPinnedBottom ? 2 : 0)));
                    this.rect.set((float) dp6, (float) dp7, (float) (this.timeWidthAudio + dp6 + AndroidUtilities.dp(22.0f)), (float) (AndroidUtilities.dp(17.0f) + dp7));
                    int alpha3 = Theme.chat_actionBackgroundPaint.getAlpha();
                    Theme.chat_actionBackgroundPaint.setAlpha((int) (((float) alpha3) * this.timeAlpha));
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_actionBackgroundPaint);
                    Theme.chat_actionBackgroundPaint.setAlpha(alpha3);
                    if (isPlayingMessage || !this.currentMessageObject.isContentUnread()) {
                        if (!isPlayingMessage || MediaController.getInstance().isMessagePaused()) {
                            this.roundVideoPlayingDrawable.stop();
                        } else {
                            this.roundVideoPlayingDrawable.start();
                        }
                        BaseCell.setDrawableBounds((Drawable) this.roundVideoPlayingDrawable, this.timeWidthAudio + dp6 + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(2.3f) + dp7);
                        this.roundVideoPlayingDrawable.draw(canvas2);
                    } else {
                        Theme.chat_docBackPaint.setColor(Theme.getColor("chat_mediaTimeText"));
                        Theme.chat_docBackPaint.setAlpha((int) (this.timeAlpha * 255.0f));
                        canvas2.drawCircle((float) (this.timeWidthAudio + dp6 + AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(8.3f) + dp7), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                    }
                    i9 = dp6 + AndroidUtilities.dp(4.0f);
                    i8 = dp7 + AndroidUtilities.dp(1.7f);
                } else {
                    i9 = AndroidUtilities.dp((messageObject3.isOutOwner() || this.drawPinnedBottom) ? 12.0f : 18.0f) + this.backgroundDrawableLeft;
                    i8 = (this.layoutHeight - AndroidUtilities.dp(6.3f - ((float) (this.drawPinnedBottom ? 2 : 0)))) - this.timeLayout.getHeight();
                }
                Theme.chat_timePaint.setAlpha((int) (this.timeAlpha * 255.0f));
                canvas.save();
                canvas2.translate((float) i9, (float) i8);
                this.durationLayout.draw(canvas2);
                canvas.restore();
                Theme.chat_timePaint.setAlpha(255);
            }
        } else if (i39 == 5) {
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_outAudioTitleText"));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outAudioPerfomerSelectedText" : "chat_outAudioPerfomerText"));
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outAudioDurationSelectedText" : "chat_outAudioDurationText"));
                this.radialProgress.setProgressColor(Theme.getColor((isDrawSelectionBackground() || this.buttonPressed != 0) ? "chat_outAudioSelectedProgress" : "chat_outAudioProgress"));
            } else {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_inAudioTitleText"));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inAudioPerfomerSelectedText" : "chat_inAudioPerfomerText"));
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inAudioDurationSelectedText" : "chat_inAudioDurationText"));
                this.radialProgress.setProgressColor(Theme.getColor((isDrawSelectionBackground() || this.buttonPressed != 0) ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress"));
            }
            this.radialProgress.setBackgroundDrawable(isDrawSelectionBackground() ? this.currentBackgroundSelectedDrawable : this.currentBackgroundDrawable);
            this.radialProgress.draw(canvas2);
            canvas.save();
            canvas2.translate((float) (this.timeAudioX + this.songX), (float) (AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY));
            this.songLayout.draw(canvas2);
            canvas.restore();
            canvas.save();
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                canvas2.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas2);
            } else {
                canvas2.translate((float) (this.timeAudioX + this.performerX), (float) (AndroidUtilities.dp(35.0f) + this.namesOffset + this.mediaOffsetY));
                this.performerLayout.draw(canvas2);
            }
            canvas.restore();
            canvas.save();
            canvas2.translate((float) this.timeAudioX, (float) (AndroidUtilities.dp(57.0f) + this.namesOffset + this.mediaOffsetY));
            this.durationLayout.draw(canvas2);
            canvas.restore();
            if (this.currentMessageObject.isOutOwner()) {
                drawable2 = isDrawSelectionBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
            } else {
                drawable2 = isDrawSelectionBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            }
            int dp8 = (this.buttonX + this.backgroundWidth) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
            this.otherX = dp8;
            int dp9 = this.buttonY - AndroidUtilities.dp(5.0f);
            this.otherY = dp9;
            BaseCell.setDrawableBounds(drawable2, dp8, dp9);
            drawable2.draw(canvas2);
        } else if (i39 == 3) {
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outAudioDurationSelectedText" : "chat_outAudioDurationText"));
                this.radialProgress.setProgressColor(Theme.getColor((isDrawSelectionBackground() || this.buttonPressed != 0) ? "chat_outAudioSelectedProgress" : "chat_outAudioProgress"));
            } else {
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inAudioDurationSelectedText" : "chat_inAudioDurationText"));
                this.radialProgress.setProgressColor(Theme.getColor((isDrawSelectionBackground() || this.buttonPressed != 0) ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress"));
            }
            this.radialProgress.setBackgroundDrawable(isDrawSelectionBackground() ? this.currentBackgroundSelectedDrawable : this.currentBackgroundDrawable);
            this.radialProgress.draw(canvas2);
            canvas.save();
            if (this.useSeekBarWaweform) {
                canvas2.translate((float) (this.seekBarX + AndroidUtilities.dp(13.0f)), (float) this.seekBarY);
                this.seekBarWaveform.draw(canvas2);
            } else {
                canvas2.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas2);
            }
            canvas.restore();
            canvas.save();
            canvas2.translate((float) this.timeAudioX, (float) (AndroidUtilities.dp(44.0f) + this.namesOffset + this.mediaOffsetY));
            this.durationLayout.draw(canvas2);
            canvas.restore();
            MessageObject messageObject4 = this.currentMessageObject;
            if (messageObject4.type != 0 && messageObject4.isContentUnread()) {
                Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outVoiceSeekbarFill" : "chat_inVoiceSeekbarFill"));
                canvas2.drawCircle((float) (this.timeAudioX + this.timeWidthAudio + AndroidUtilities.dp(6.0f)), (float) (AndroidUtilities.dp(51.0f) + this.namesOffset + this.mediaOffsetY), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
            }
        }
        if (this.captionLayout != null) {
            MessageObject messageObject5 = this.currentMessageObject;
            int i40 = messageObject5.type;
            if (i40 == 1 || this.documentAttachType == 4 || i40 == 8) {
                this.captionX = this.photoImage.getImageX() + AndroidUtilities.dp(5.0f) + this.captionOffsetX;
                this.captionY = this.photoImage.getImageY() + this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0f);
            } else if (this.hasOldCaptionPreview) {
                this.captionX = this.backgroundDrawableLeft + AndroidUtilities.dp(messageObject5.isOutOwner() ? 11.0f : 17.0f) + this.captionOffsetX;
                this.captionY = (((this.totalHeight - this.captionHeight) - AndroidUtilities.dp(this.drawPinnedTop ? 9.0f : 10.0f)) - this.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
            } else {
                this.captionX = this.backgroundDrawableLeft + AndroidUtilities.dp((messageObject5.isOutOwner() || (z2 = this.mediaBackground) || (!z2 && this.drawPinnedBottom)) ? 11.0f : 17.0f) + this.captionOffsetX;
                this.captionY = (this.totalHeight - this.captionHeight) - AndroidUtilities.dp(this.drawPinnedTop ? 9.0f : 10.0f);
            }
        }
        if (this.currentPosition == null) {
            drawCaptionLayout(canvas2, false);
        }
        if (this.hasOldCaptionPreview) {
            MessageObject messageObject6 = this.currentMessageObject;
            int i41 = messageObject6.type;
            if (i41 == 1 || this.documentAttachType == 4 || i41 == 8) {
                i5 = AndroidUtilities.dp(5.0f) + this.photoImage.getImageX();
            } else {
                int i42 = this.backgroundDrawableLeft;
                if (!messageObject6.isOutOwner()) {
                    f = 17.0f;
                }
                i5 = i42 + AndroidUtilities.dp(f);
            }
            int i43 = i5;
            int dp10 = ((this.totalHeight - AndroidUtilities.dp(this.drawPinnedTop ? 9.0f : 10.0f)) - this.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
            Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outPreviewLine" : "chat_inPreviewLine"));
            canvas.drawRect((float) i43, (float) (dp10 - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i43), (float) (this.linkPreviewHeight + dp10), Theme.chat_replyLinePaint);
            if (this.siteNameLayout != null) {
                Theme.chat_replyNamePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outSiteNameText" : "chat_inSiteNameText"));
                canvas.save();
                if (this.siteNameRtl) {
                    i7 = (this.backgroundWidth - this.siteNameWidth) - AndroidUtilities.dp(32.0f);
                } else {
                    i7 = this.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f);
                }
                canvas2.translate((float) (i7 + i43), (float) (dp10 - AndroidUtilities.dp(3.0f)));
                this.siteNameLayout.draw(canvas2);
                canvas.restore();
                StaticLayout staticLayout7 = this.siteNameLayout;
                i6 = staticLayout7.getLineBottom(staticLayout7.getLineCount() - 1) + dp10;
            } else {
                i6 = dp10;
            }
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
            } else {
                Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
            }
            if (this.descriptionLayout != null) {
                if (i6 != dp10) {
                    i6 += AndroidUtilities.dp(2.0f);
                }
                this.descriptionY = i6 - AndroidUtilities.dp(3.0f);
                canvas.save();
                canvas2.translate((float) (i43 + AndroidUtilities.dp(10.0f) + this.descriptionX), (float) this.descriptionY);
                this.descriptionLayout.draw(canvas2);
                canvas.restore();
            }
            this.drawTime = true;
        }
        if (this.documentAttachType == 1) {
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_docNamePaint.setColor(Theme.getColor("chat_outFileNameText"));
                Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outFileInfoSelectedText" : "chat_outFileInfoText"));
                Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outFileBackgroundSelected" : "chat_outFileBackground"));
                drawable = isDrawSelectionBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
            } else {
                Theme.chat_docNamePaint.setColor(Theme.getColor("chat_inFileNameText"));
                Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inFileInfoSelectedText" : "chat_inFileInfoText"));
                Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inFileBackgroundSelected" : "chat_inFileBackground"));
                drawable = isDrawSelectionBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            }
            if (this.drawPhotoImage) {
                if (this.currentMessageObject.type == 0) {
                    int imageX4 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(56.0f);
                    this.otherX = imageX4;
                    int imageY4 = this.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                    this.otherY = imageY4;
                    BaseCell.setDrawableBounds(drawable, imageX4, imageY4);
                } else {
                    int imageX5 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(40.0f);
                    this.otherX = imageX5;
                    int imageY5 = this.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                    this.otherY = imageY5;
                    BaseCell.setDrawableBounds(drawable, imageX5, imageY5);
                }
                i4 = this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0f);
                i3 = this.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                int imageY6 = this.photoImage.getImageY();
                StaticLayout staticLayout8 = this.docTitleLayout;
                i2 = imageY6 + (staticLayout8 != null ? staticLayout8.getLineBottom(staticLayout8.getLineCount() - 1) + AndroidUtilities.dp(13.0f) : AndroidUtilities.dp(8.0f));
                if (!z) {
                    if (this.currentMessageObject.isOutOwner()) {
                        this.radialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
                        this.radialProgress.setProgressColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outFileProgressSelected" : "chat_outFileProgress"));
                        this.videoRadialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
                        this.videoRadialProgress.setProgressColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outFileProgressSelected" : "chat_outFileProgress"));
                    } else {
                        this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
                        this.radialProgress.setProgressColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inFileProgressSelected" : "chat_inFileProgress"));
                        this.videoRadialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
                        this.videoRadialProgress.setProgressColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inFileProgressSelected" : "chat_inFileProgress"));
                    }
                    this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) (this.photoImage.getImageX() + this.photoImage.getImageWidth()), (float) (this.photoImage.getImageY() + this.photoImage.getImageHeight()));
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                } else {
                    this.radialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
                    this.radialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
                    this.videoRadialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
                    this.videoRadialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
                    if (this.buttonState == -1 && this.radialProgress.getIcon() != 4) {
                        this.radialProgress.setIcon(4, true, true);
                    }
                }
            } else {
                int dp11 = (this.buttonX + this.backgroundWidth) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
                this.otherX = dp11;
                int dp12 = this.buttonY - AndroidUtilities.dp(5.0f);
                this.otherY = dp12;
                BaseCell.setDrawableBounds(drawable, dp11, dp12);
                i4 = AndroidUtilities.dp(53.0f) + this.buttonX;
                i3 = AndroidUtilities.dp(4.0f) + this.buttonY;
                int dp13 = this.buttonY + AndroidUtilities.dp(27.0f);
                StaticLayout staticLayout9 = this.docTitleLayout;
                if (staticLayout9 != null && staticLayout9.getLineCount() > 1) {
                    dp13 += ((this.docTitleLayout.getLineCount() - 1) * AndroidUtilities.dp(16.0f)) + AndroidUtilities.dp(2.0f);
                }
                i2 = dp13;
                if (this.currentMessageObject.isOutOwner()) {
                    this.radialProgress.setProgressColor(Theme.getColor((isDrawSelectionBackground() || this.buttonPressed != 0) ? "chat_outAudioSelectedProgress" : "chat_outAudioProgress"));
                    this.videoRadialProgress.setProgressColor(Theme.getColor((isDrawSelectionBackground() || this.videoButtonPressed != 0) ? "chat_outAudioSelectedProgress" : "chat_outAudioProgress"));
                } else {
                    this.radialProgress.setProgressColor(Theme.getColor((isDrawSelectionBackground() || this.buttonPressed != 0) ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress"));
                    this.videoRadialProgress.setProgressColor(Theme.getColor((isDrawSelectionBackground() || this.videoButtonPressed != 0) ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress"));
                }
            }
            drawable.draw(canvas2);
            try {
                if (this.docTitleLayout != null) {
                    canvas.save();
                    canvas2.translate((float) (this.docTitleOffsetX + i4), (float) i3);
                    this.docTitleLayout.draw(canvas2);
                    canvas.restore();
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            try {
                if (this.infoLayout != null) {
                    canvas.save();
                    canvas2.translate((float) i4, (float) i2);
                    this.infoLayout.draw(canvas2);
                    canvas.restore();
                }
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
            }
        }
        if (this.buttonState == -1 && this.currentMessageObject.needDrawBluredPreview() && !MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && this.photoImage.getVisible()) {
            MessageObject messageObject7 = this.currentMessageObject;
            if (messageObject7.messageOwner.destroyTime != 0) {
                if (!messageObject7.isOutOwner()) {
                    float max = ((float) Math.max(0, (((long) this.currentMessageObject.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(this.currentAccount).getTimeDifference() * 1000))))) / (((float) this.currentMessageObject.messageOwner.ttl) * 1000.0f);
                    Theme.chat_deleteProgressPaint.setAlpha((int) (this.controlsAlpha * 255.0f));
                    canvas.drawArc(this.deleteProgressRect, -90.0f, max * -360.0f, true, Theme.chat_deleteProgressPaint);
                    if (max != 0.0f) {
                        int dp14 = AndroidUtilities.dp(2.0f);
                        RectF rectF = this.deleteProgressRect;
                        int i44 = ((int) rectF.left) - dp14;
                        int i45 = ((int) rectF.top) - dp14;
                        int i46 = dp14 * 2;
                        invalidate(i44, i45, ((int) rectF.right) + i46, ((int) rectF.bottom) + i46);
                    }
                }
                updateSecretTimeText(this.currentMessageObject);
            }
        }
        MessageObject messageObject8 = this.currentMessageObject;
        if (messageObject8.type == 4 && !(messageObject8.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive) && this.currentMapProvider == 2 && this.photoImage.hasNotThumb()) {
            int intrinsicWidth = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicWidth()) * 0.8f);
            int intrinsicHeight = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicHeight()) * 0.8f);
            int imageX6 = this.photoImage.getImageX() + ((this.photoImage.getImageWidth() - intrinsicWidth) / 2);
            int imageY7 = this.photoImage.getImageY() + ((this.photoImage.getImageHeight() / 2) - intrinsicHeight);
            Theme.chat_redLocationIcon.setAlpha((int) (this.photoImage.getCurrentAlpha() * 255.0f));
            Theme.chat_redLocationIcon.setBounds(imageX6, imageY7, intrinsicWidth + imageX6, intrinsicHeight + imageY7);
            Theme.chat_redLocationIcon.draw(canvas2);
        }
        if (!this.botButtons.isEmpty()) {
            if (this.currentMessageObject.isOutOwner()) {
                i = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                i = this.backgroundDrawableLeft + AndroidUtilities.dp((this.mediaBackground || this.drawPinnedBottom) ? 1.0f : 7.0f);
            }
            int i47 = 0;
            while (i47 < this.botButtons.size()) {
                BotButton botButton = this.botButtons.get(i47);
                int access$1000 = (botButton.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                Theme.chat_systemDrawable.setColorFilter(i47 == this.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(botButton.x + i, access$1000, botButton.x + i + botButton.width, botButton.height + access$1000);
                Theme.chat_systemDrawable.draw(canvas2);
                canvas.save();
                canvas2.translate((float) (botButton.x + i + AndroidUtilities.dp(5.0f)), (float) (((AndroidUtilities.dp(44.0f) - botButton.title.getLineBottom(botButton.title.getLineCount() - 1)) / 2) + access$1000));
                botButton.title.draw(canvas2);
                canvas.restore();
                if (botButton.button instanceof TLRPC.TL_keyboardButtonUrl) {
                    BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((botButton.x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + i, access$1000 + AndroidUtilities.dp(3.0f));
                    Theme.chat_botLinkDrawalbe.draw(canvas2);
                } else if (botButton.button instanceof TLRPC.TL_keyboardButtonSwitchInline) {
                    BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((botButton.x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + i, access$1000 + AndroidUtilities.dp(3.0f));
                    Theme.chat_botInlineDrawable.draw(canvas2);
                } else if ((botButton.button instanceof TLRPC.TL_keyboardButtonCallback) || (botButton.button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) || (botButton.button instanceof TLRPC.TL_keyboardButtonGame) || (botButton.button instanceof TLRPC.TL_keyboardButtonBuy) || (botButton.button instanceof TLRPC.TL_keyboardButtonUrlAuth)) {
                    boolean z6 = (((botButton.button instanceof TLRPC.TL_keyboardButtonCallback) || (botButton.button instanceof TLRPC.TL_keyboardButtonGame) || (botButton.button instanceof TLRPC.TL_keyboardButtonBuy) || (botButton.button instanceof TLRPC.TL_keyboardButtonUrlAuth)) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCallback(this.currentMessageObject, botButton.button)) || ((botButton.button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCurrentLocation(this.currentMessageObject, botButton.button));
                    if (z6 || (!z6 && botButton.progressAlpha != 0.0f)) {
                        Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (botButton.progressAlpha * 255.0f)));
                        int access$1100 = ((botButton.x + botButton.width) - AndroidUtilities.dp(12.0f)) + i;
                        this.rect.set((float) access$1100, (float) (AndroidUtilities.dp(4.0f) + access$1000), (float) (access$1100 + AndroidUtilities.dp(8.0f)), (float) (access$1000 + AndroidUtilities.dp(12.0f)));
                        canvas.drawArc(this.rect, (float) botButton.angle, 220.0f, false, Theme.chat_botProgressPaint);
                        invalidate();
                        long currentTimeMillis = System.currentTimeMillis();
                        if (Math.abs(botButton.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                            long access$2500 = currentTimeMillis - botButton.lastUpdateTime;
                            int unused = botButton.angle = (int) (((float) botButton.angle) + (((float) (360 * access$2500)) / 2000.0f));
                            int unused2 = botButton.angle = botButton.angle - ((botButton.angle / 360) * 360);
                            if (!z6) {
                                if (botButton.progressAlpha > 0.0f) {
                                    float unused3 = botButton.progressAlpha = botButton.progressAlpha - (((float) access$2500) / 200.0f);
                                    if (botButton.progressAlpha < 0.0f) {
                                        float unused4 = botButton.progressAlpha = 0.0f;
                                    }
                                }
                                long unused5 = botButton.lastUpdateTime = currentTimeMillis;
                                i47++;
                            } else if (botButton.progressAlpha < 1.0f) {
                                float unused6 = botButton.progressAlpha = botButton.progressAlpha + (((float) access$2500) / 200.0f);
                                if (botButton.progressAlpha > 1.0f) {
                                    float unused7 = botButton.progressAlpha = 1.0f;
                                }
                            }
                        }
                        long unused8 = botButton.lastUpdateTime = currentTimeMillis;
                        i47++;
                    }
                }
                i47++;
            }
        }
    }

    private boolean textIsSelectionMode() {
        if (getCurrentMessagesGroup() == null && this.delegate.getTextSelectionHelper() != null && this.delegate.getTextSelectionHelper().isSelected(this.currentMessageObject)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public int getMiniIconForCurrentState() {
        int i = this.miniButtonState;
        if (i < 0) {
            return 4;
        }
        return i == 0 ? 2 : 3;
    }

    /* access modifiers changed from: private */
    public int getIconForCurrentState() {
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
        if (i != 1 || this.drawPhotoImage) {
            this.radialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
            this.videoRadialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
            int i3 = this.buttonState;
            if (i3 < 0 || i3 >= 4) {
                if (this.buttonState == -1) {
                    if (this.documentAttachType == 1) {
                        if (this.drawPhotoImage && !(this.currentPhotoObject == null && this.currentPhotoObjectThumb == null)) {
                            if (this.photoImage.hasBitmapImage()) {
                                return 4;
                            }
                            MessageObject messageObject = this.currentMessageObject;
                            if (messageObject.mediaExists || messageObject.attachPathExists) {
                                return 4;
                            }
                        }
                        return 5;
                    } else if (this.currentMessageObject.needDrawBluredPreview()) {
                        MessageObject messageObject2 = this.currentMessageObject;
                        if (messageObject2.messageOwner.destroyTime != 0) {
                            return messageObject2.isOutOwner() ? 9 : 11;
                        }
                        return 7;
                    } else if (this.hasEmbed) {
                        return 0;
                    }
                }
            } else if (i3 == 0) {
                return 2;
            } else {
                if (i3 == 1) {
                    return 3;
                }
                if (i3 == 2) {
                    return 0;
                }
                if (i3 != 3 || this.autoPlayingMedia) {
                    return 4;
                }
                return 0;
            }
        } else {
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
        }
        return 4;
    }

    private int getMaxNameWidth() {
        int i;
        int i2;
        int dp;
        int i3;
        int i4;
        int i5;
        int i6 = this.documentAttachType;
        if (i6 == 6 || i6 == 8 || this.currentMessageObject.type == 5) {
            if (AndroidUtilities.isTablet()) {
                if (!this.isChat || this.currentMessageObject.isOutOwner() || !this.currentMessageObject.needDrawAvatar()) {
                    i = AndroidUtilities.getMinTabletSide();
                    i2 = i - this.backgroundWidth;
                    dp = AndroidUtilities.dp(57.0f);
                } else {
                    i4 = AndroidUtilities.getMinTabletSide();
                    i3 = AndroidUtilities.dp(42.0f);
                }
            } else if (!this.isChat || this.currentMessageObject.isOutOwner() || !this.currentMessageObject.needDrawAvatar()) {
                Point point = AndroidUtilities.displaySize;
                i = Math.min(point.x, point.y);
                i2 = i - this.backgroundWidth;
                dp = AndroidUtilities.dp(57.0f);
            } else {
                Point point2 = AndroidUtilities.displaySize;
                i4 = Math.min(point2.x, point2.y);
                i3 = AndroidUtilities.dp(42.0f);
            }
            i = i4 - i3;
            i2 = i - this.backgroundWidth;
            dp = AndroidUtilities.dp(57.0f);
        } else if (this.currentMessagesGroup != null) {
            if (AndroidUtilities.isTablet()) {
                i5 = AndroidUtilities.getMinTabletSide();
            } else {
                i5 = AndroidUtilities.displaySize.x;
            }
            int i7 = 0;
            int i8 = 0;
            for (int i9 = 0; i9 < this.currentMessagesGroup.posArray.size(); i9++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentMessagesGroup.posArray.get(i9);
                if (groupedMessagePosition.minY != 0) {
                    break;
                }
                double d = (double) i8;
                double ceil = Math.ceil((double) ((((float) (groupedMessagePosition.pw + groupedMessagePosition.leftSpanOffset)) / 1000.0f) * ((float) i5)));
                Double.isNaN(d);
                i8 = (int) (d + ceil);
            }
            if (this.isAvatarVisible) {
                i7 = 48;
            }
            return i8 - AndroidUtilities.dp((float) (i7 + 31));
        } else {
            i2 = this.backgroundWidth;
            dp = AndroidUtilities.dp(this.mediaBackground ? 22.0f : 31.0f);
        }
        return i2 - dp;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0115, code lost:
        if ((r2 & 2) != 0) goto L_0x0122;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateButtonState(boolean r17, boolean r18, boolean r19) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = 0
            if (r18 == 0) goto L_0x0015
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.messenger.MessageObject) r3)
            if (r3 != 0) goto L_0x0013
            boolean r3 = r0.attachedToWindow
            if (r3 != 0) goto L_0x0015
        L_0x0013:
            r3 = 0
            goto L_0x0017
        L_0x0015:
            r3 = r18
        L_0x0017:
            r0.drawRadialCheckBackground = r2
            r4 = 0
            org.telegram.messenger.MessageObject r5 = r0.currentMessageObject
            int r5 = r5.type
            r6 = 9
            r7 = 8
            r8 = 5
            r9 = 3
            r10 = 7
            r11 = 4
            r12 = 1
            if (r5 != r12) goto L_0x003d
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r0.currentPhotoObject
            if (r4 != 0) goto L_0x0033
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            r2.setIcon(r11, r1, r3)
            return
        L_0x0033:
            java.lang.String r4 = org.telegram.messenger.FileLoader.getAttachFileName(r4)
            org.telegram.messenger.MessageObject r5 = r0.currentMessageObject
            boolean r5 = r5.mediaExists
            goto L_0x00ab
        L_0x003d:
            if (r5 == r7) goto L_0x0068
            int r13 = r0.documentAttachType
            if (r13 == r10) goto L_0x0068
            if (r13 == r11) goto L_0x0068
            if (r13 == r7) goto L_0x0068
            if (r5 == r6) goto L_0x0068
            if (r13 == r9) goto L_0x0068
            if (r13 != r8) goto L_0x004e
            goto L_0x0068
        L_0x004e:
            if (r13 == 0) goto L_0x005b
            org.telegram.tgnet.TLRPC$Document r4 = r0.documentAttach
            java.lang.String r4 = org.telegram.messenger.FileLoader.getAttachFileName(r4)
            org.telegram.messenger.MessageObject r5 = r0.currentMessageObject
            boolean r5 = r5.mediaExists
            goto L_0x00ab
        L_0x005b:
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r0.currentPhotoObject
            if (r5 == 0) goto L_0x009f
            java.lang.String r4 = org.telegram.messenger.FileLoader.getAttachFileName(r5)
            org.telegram.messenger.MessageObject r5 = r0.currentMessageObject
            boolean r5 = r5.mediaExists
            goto L_0x00ab
        L_0x0068:
            org.telegram.messenger.MessageObject r5 = r0.currentMessageObject
            boolean r13 = r5.useCustomPhoto
            if (r13 == 0) goto L_0x007a
            r0.buttonState = r12
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r4 = r16.getIconForCurrentState()
            r2.setIcon(r4, r1, r3)
            return
        L_0x007a:
            boolean r13 = r5.attachPathExists
            if (r13 == 0) goto L_0x0090
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            java.lang.String r5 = r5.attachPath
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0090
            org.telegram.messenger.MessageObject r4 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            java.lang.String r4 = r4.attachPath
            r5 = 1
            goto L_0x00ab
        L_0x0090:
            org.telegram.messenger.MessageObject r5 = r0.currentMessageObject
            boolean r5 = r5.isSendError()
            if (r5 == 0) goto L_0x00a1
            int r5 = r0.documentAttachType
            if (r5 == r9) goto L_0x00a1
            if (r5 != r8) goto L_0x009f
            goto L_0x00a1
        L_0x009f:
            r5 = 0
            goto L_0x00ab
        L_0x00a1:
            org.telegram.messenger.MessageObject r4 = r0.currentMessageObject
            java.lang.String r4 = r4.getFileName()
            org.telegram.messenger.MessageObject r5 = r0.currentMessageObject
            boolean r5 = r5.mediaExists
        L_0x00ab:
            org.telegram.tgnet.TLRPC$Document r13 = r0.documentAttach
            if (r13 == 0) goto L_0x00b7
            int r13 = r13.dc_id
            r14 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r13 != r14) goto L_0x00b7
            r13 = 0
            goto L_0x00c3
        L_0x00b7:
            int r13 = r0.currentAccount
            org.telegram.messenger.DownloadController r13 = org.telegram.messenger.DownloadController.getInstance(r13)
            org.telegram.messenger.MessageObject r14 = r0.currentMessageObject
            boolean r13 = r13.canDownloadMedia((org.telegram.messenger.MessageObject) r14)
        L_0x00c3:
            org.telegram.messenger.MessageObject r14 = r0.currentMessageObject
            boolean r14 = r14.isSent()
            r15 = 2
            if (r14 == 0) goto L_0x00e8
            int r14 = r0.documentAttachType
            if (r14 == r11) goto L_0x00d6
            if (r14 == r10) goto L_0x00d6
            if (r14 != r15) goto L_0x00e8
            if (r13 == 0) goto L_0x00e8
        L_0x00d6:
            org.telegram.messenger.MessageObject r14 = r0.currentMessageObject
            boolean r14 = r14.canStreamVideo()
            if (r14 == 0) goto L_0x00e8
            org.telegram.messenger.MessageObject r14 = r0.currentMessageObject
            boolean r14 = r14.needDrawBluredPreview()
            if (r14 != 0) goto L_0x00e8
            r14 = 1
            goto L_0x00e9
        L_0x00e8:
            r14 = 0
        L_0x00e9:
            r0.canStreamVideo = r14
            boolean r14 = org.telegram.messenger.SharedConfig.streamMedia
            if (r14 == 0) goto L_0x0120
            org.telegram.messenger.MessageObject r14 = r0.currentMessageObject
            r18 = r3
            long r2 = r14.getDialogId()
            int r3 = (int) r2
            if (r3 == 0) goto L_0x0122
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            boolean r2 = r2.isSecretMedia()
            if (r2 != 0) goto L_0x0122
            int r2 = r0.documentAttachType
            if (r2 == r8) goto L_0x0117
            boolean r2 = r0.canStreamVideo
            if (r2 == 0) goto L_0x0122
            org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r0.currentPosition
            if (r2 == 0) goto L_0x0122
            int r2 = r2.flags
            r3 = r2 & 1
            if (r3 == 0) goto L_0x0117
            r2 = r2 & r15
            if (r2 != 0) goto L_0x0122
        L_0x0117:
            if (r5 == 0) goto L_0x011b
            r2 = 1
            goto L_0x011c
        L_0x011b:
            r2 = 2
        L_0x011c:
            r0.hasMiniProgress = r2
            r5 = 1
            goto L_0x0122
        L_0x0120:
            r18 = r3
        L_0x0122:
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            boolean r2 = r2.isSendError()
            if (r2 != 0) goto L_0x0741
            boolean r2 = android.text.TextUtils.isEmpty(r4)
            if (r2 == 0) goto L_0x0142
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            boolean r2 = r2.isSending()
            if (r2 != 0) goto L_0x0142
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            boolean r2 = r2.isEditing()
            if (r2 != 0) goto L_0x0142
            goto L_0x0741
        L_0x0142:
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.util.HashMap<java.lang.String, java.lang.String> r2 = r2.params
            if (r2 == 0) goto L_0x0154
            java.lang.String r3 = "query_id"
            boolean r2 = r2.containsKey(r3)
            if (r2 == 0) goto L_0x0154
            r2 = 1
            goto L_0x0155
        L_0x0154:
            r2 = 0
        L_0x0155:
            int r3 = r0.documentAttachType
            r14 = 0
            if (r3 == r9) goto L_0x0583
            if (r3 != r8) goto L_0x015e
            goto L_0x0583
        L_0x015e:
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            int r2 = r2.type
            if (r2 != 0) goto L_0x0205
            if (r3 == r12) goto L_0x0205
            if (r3 == r15) goto L_0x0205
            if (r3 == r10) goto L_0x0205
            if (r3 == r11) goto L_0x0205
            if (r3 == r7) goto L_0x0205
            if (r3 == r6) goto L_0x0205
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r0.currentPhotoObject
            if (r2 == 0) goto L_0x0204
            boolean r2 = r0.drawImageButton
            if (r2 != 0) goto L_0x017a
            goto L_0x0204
        L_0x017a:
            if (r5 != 0) goto L_0x01d9
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            r2.addLoadingFileObserver(r4, r3, r0)
            int r2 = r0.currentAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            boolean r2 = r2.isLoadingFile(r4)
            if (r2 != 0) goto L_0x01b2
            boolean r2 = r0.cancelLoading
            if (r2 != 0) goto L_0x01ae
            int r2 = r0.documentAttachType
            if (r2 != 0) goto L_0x019d
            if (r13 != 0) goto L_0x01ab
        L_0x019d:
            int r2 = r0.documentAttachType
            if (r2 != r15) goto L_0x01ae
            org.telegram.tgnet.TLRPC$Document r2 = r0.documentAttach
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r2)
            if (r2 == 0) goto L_0x01ae
            if (r13 == 0) goto L_0x01ae
        L_0x01ab:
            r0.buttonState = r12
            goto L_0x01c3
        L_0x01ae:
            r2 = 0
            r0.buttonState = r2
            goto L_0x01c3
        L_0x01b2:
            r0.buttonState = r12
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r2 = r2.getFileProgress(r4)
            if (r2 == 0) goto L_0x01c3
            float r2 = r2.floatValue()
            r14 = r2
        L_0x01c3:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            r3 = 0
            r2.setProgress(r14, r3)
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r3 = r16.getIconForCurrentState()
            r6 = r18
            r2.setIcon(r3, r1, r6)
            r16.invalidate()
            goto L_0x0736
        L_0x01d9:
            r6 = r18
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            r2.removeLoadingFileObserver(r0)
            int r2 = r0.documentAttachType
            if (r2 != r15) goto L_0x01f3
            org.telegram.messenger.ImageReceiver r2 = r0.photoImage
            boolean r2 = r2.isAllowStartAnimation()
            if (r2 != 0) goto L_0x01f3
            r0.buttonState = r15
            goto L_0x01f6
        L_0x01f3:
            r2 = -1
            r0.buttonState = r2
        L_0x01f6:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r3 = r16.getIconForCurrentState()
            r2.setIcon(r3, r1, r6)
            r16.invalidate()
            goto L_0x0736
        L_0x0204:
            return
        L_0x0205:
            r6 = r18
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            boolean r2 = r2.isOut()
            if (r2 == 0) goto L_0x030a
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            boolean r2 = r2.isSending()
            if (r2 != 0) goto L_0x021f
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            boolean r2 = r2.isEditing()
            if (r2 == 0) goto L_0x030a
        L_0x021f:
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.attachPath
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x02d5
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            java.lang.String r4 = r4.attachPath
            r2.addLoadingFileObserver(r4, r3, r0)
            r0.wasSending = r12
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.attachPath
            if (r2 == 0) goto L_0x024f
            java.lang.String r3 = "http"
            boolean r2 = r2.startsWith(r3)
            if (r2 != 0) goto L_0x024d
            goto L_0x024f
        L_0x024d:
            r2 = 0
            goto L_0x0250
        L_0x024f:
            r2 = 1
        L_0x0250:
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r3.params
            java.lang.String r3 = r3.message
            if (r3 == 0) goto L_0x0272
            if (r4 == 0) goto L_0x0272
            java.lang.String r3 = "url"
            boolean r3 = r4.containsKey(r3)
            if (r3 != 0) goto L_0x026d
            java.lang.String r3 = "bot"
            boolean r3 = r4.containsKey(r3)
            if (r3 == 0) goto L_0x0272
        L_0x026d:
            r2 = -1
            r0.buttonState = r2
            r2 = 0
            goto L_0x0274
        L_0x0272:
            r0.buttonState = r12
        L_0x0274:
            int r3 = r0.currentAccount
            org.telegram.messenger.SendMessagesHelper r3 = org.telegram.messenger.SendMessagesHelper.getInstance(r3)
            org.telegram.messenger.MessageObject r4 = r0.currentMessageObject
            int r4 = r4.getId()
            boolean r3 = r3.isSendingMessage(r4)
            org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = r0.currentPosition
            if (r4 == 0) goto L_0x029a
            if (r3 == 0) goto L_0x029a
            int r4 = r0.buttonState
            if (r4 != r12) goto L_0x029a
            r0.drawRadialCheckBackground = r12
            r16.getIconForCurrentState()
            org.telegram.ui.Components.RadialProgress2 r4 = r0.radialProgress
            r5 = 6
            r4.setIcon(r5, r1, r6)
            goto L_0x02a3
        L_0x029a:
            org.telegram.ui.Components.RadialProgress2 r4 = r0.radialProgress
            int r5 = r16.getIconForCurrentState()
            r4.setIcon(r5, r1, r6)
        L_0x02a3:
            if (r2 == 0) goto L_0x02ca
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.messenger.MessageObject r4 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            java.lang.String r4 = r4.attachPath
            java.lang.Float r2 = r2.getFileProgress(r4)
            if (r2 != 0) goto L_0x02bd
            if (r3 == 0) goto L_0x02bd
            r3 = 1065353216(0x3var_, float:1.0)
            java.lang.Float r2 = java.lang.Float.valueOf(r3)
        L_0x02bd:
            org.telegram.ui.Components.RadialProgress2 r3 = r0.radialProgress
            if (r2 == 0) goto L_0x02c5
            float r14 = r2.floatValue()
        L_0x02c5:
            r2 = 0
            r3.setProgress(r14, r2)
            goto L_0x02d0
        L_0x02ca:
            r2 = 0
            org.telegram.ui.Components.RadialProgress2 r3 = r0.radialProgress
            r3.setProgress(r14, r2)
        L_0x02d0:
            r16.invalidate()
            r4 = 0
            goto L_0x0303
        L_0x02d5:
            r2 = -1
            r0.buttonState = r2
            r16.getIconForCurrentState()
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.isSticker()
            if (r3 != 0) goto L_0x02f9
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.isAnimatedSticker()
            if (r3 != 0) goto L_0x02f9
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.isLocation()
            if (r3 == 0) goto L_0x02f6
            goto L_0x02f9
        L_0x02f6:
            r3 = 12
            goto L_0x02fa
        L_0x02f9:
            r3 = 4
        L_0x02fa:
            r4 = 0
            r2.setIcon(r3, r1, r4)
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            r2.setProgress(r14, r4)
        L_0x0303:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.videoRadialProgress
            r2.setIcon(r11, r1, r4)
            goto L_0x0736
        L_0x030a:
            boolean r2 = r0.wasSending
            if (r2 == 0) goto L_0x0323
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.attachPath
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0323
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            r2.removeLoadingFileObserver(r0)
        L_0x0323:
            int r2 = r0.documentAttachType
            if (r2 == r11) goto L_0x032b
            if (r2 == r15) goto L_0x032b
            if (r2 != r10) goto L_0x0378
        L_0x032b:
            boolean r2 = r0.autoPlayingMedia
            if (r2 == 0) goto L_0x0378
            int r2 = r0.currentAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            org.telegram.tgnet.TLRPC$Document r3 = r0.documentAttach
            org.telegram.messenger.MediaController r7 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r8 = r0.currentMessageObject
            boolean r7 = r7.isPlayingMessage(r8)
            boolean r2 = r2.isLoadingVideo(r3, r7)
            org.telegram.messenger.ImageReceiver r3 = r0.photoImage
            org.telegram.ui.Components.AnimatedFileDrawable r3 = r3.getAnimation()
            if (r3 == 0) goto L_0x036d
            org.telegram.messenger.MessageObject r7 = r0.currentMessageObject
            boolean r8 = r7.hadAnimationNotReadyLoading
            if (r8 == 0) goto L_0x035f
            boolean r3 = r3.hasBitmap()
            if (r3 == 0) goto L_0x0379
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            r7 = 0
            r3.hadAnimationNotReadyLoading = r7
            goto L_0x0379
        L_0x035f:
            if (r2 == 0) goto L_0x0369
            boolean r3 = r3.hasBitmap()
            if (r3 != 0) goto L_0x0369
            r3 = 1
            goto L_0x036a
        L_0x0369:
            r3 = 0
        L_0x036a:
            r7.hadAnimationNotReadyLoading = r3
            goto L_0x0379
        L_0x036d:
            int r3 = r0.documentAttachType
            if (r3 != r15) goto L_0x0379
            if (r5 != 0) goto L_0x0379
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            r3.hadAnimationNotReadyLoading = r12
            goto L_0x0379
        L_0x0378:
            r2 = 0
        L_0x0379:
            int r3 = r0.hasMiniProgress
            if (r3 == 0) goto L_0x03e5
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            java.lang.String r3 = "chat_inLoaderPhoto"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setMiniProgressBackgroundColor(r3)
            r0.buttonState = r9
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r3 = r16.getIconForCurrentState()
            r2.setIcon(r3, r1, r6)
            int r2 = r0.hasMiniProgress
            if (r2 != r12) goto L_0x03a4
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            r2.removeLoadingFileObserver(r0)
            r2 = -1
            r0.miniButtonState = r2
            goto L_0x03da
        L_0x03a4:
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            r2.addLoadingFileObserver(r4, r3, r0)
            int r2 = r0.currentAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            boolean r2 = r2.isLoadingFile(r4)
            if (r2 != 0) goto L_0x03bf
            r2 = 0
            r0.miniButtonState = r2
            goto L_0x03da
        L_0x03bf:
            r0.miniButtonState = r12
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r2 = r2.getFileProgress(r4)
            if (r2 == 0) goto L_0x03d5
            org.telegram.ui.Components.RadialProgress2 r3 = r0.radialProgress
            float r2 = r2.floatValue()
            r3.setProgress(r2, r6)
            goto L_0x03da
        L_0x03d5:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            r2.setProgress(r14, r6)
        L_0x03da:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r3 = r16.getMiniIconForCurrentState()
            r2.setMiniIcon(r3, r1, r6)
            goto L_0x0736
        L_0x03e5:
            if (r5 != 0) goto L_0x0505
            int r3 = r0.documentAttachType
            if (r3 == r11) goto L_0x03ef
            if (r3 == r15) goto L_0x03ef
            if (r3 != r10) goto L_0x03fd
        L_0x03ef:
            boolean r3 = r0.autoPlayingMedia
            if (r3 == 0) goto L_0x03fd
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.hadAnimationNotReadyLoading
            if (r3 != 0) goto L_0x03fd
            if (r2 != 0) goto L_0x03fd
            goto L_0x0505
        L_0x03fd:
            int r2 = r0.documentAttachType
            if (r2 == r11) goto L_0x0406
            if (r2 != r15) goto L_0x0404
            goto L_0x0406
        L_0x0404:
            r2 = 0
            goto L_0x0407
        L_0x0406:
            r2 = 1
        L_0x0407:
            r0.drawVideoSize = r2
            int r2 = r0.documentAttachType
            if (r2 == r11) goto L_0x0411
            if (r2 == r15) goto L_0x0411
            if (r2 != r10) goto L_0x042e
        L_0x0411:
            boolean r2 = r0.canStreamVideo
            if (r2 == 0) goto L_0x042e
            boolean r2 = r0.drawVideoImageButton
            if (r2 != 0) goto L_0x042e
            if (r6 == 0) goto L_0x042e
            int r2 = r0.animatingDrawVideoImageButton
            if (r2 == r15) goto L_0x0436
            float r3 = r0.animatingDrawVideoImageButtonProgress
            r5 = 1065353216(0x3var_, float:1.0)
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 >= 0) goto L_0x0436
            if (r2 != 0) goto L_0x042b
            r0.animatingDrawVideoImageButtonProgress = r14
        L_0x042b:
            r0.animatingDrawVideoImageButton = r15
            goto L_0x0436
        L_0x042e:
            int r2 = r0.animatingDrawVideoImageButton
            if (r2 != 0) goto L_0x0436
            r2 = 1065353216(0x3var_, float:1.0)
            r0.animatingDrawVideoImageButtonProgress = r2
        L_0x0436:
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            r2.addLoadingFileObserver(r4, r3, r0)
            int r2 = r0.currentAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            boolean r2 = r2.isLoadingFile(r4)
            if (r2 != 0) goto L_0x0499
            boolean r2 = r0.cancelLoading
            if (r2 != 0) goto L_0x0456
            if (r13 == 0) goto L_0x0456
            r0.buttonState = r12
            goto L_0x0459
        L_0x0456:
            r2 = 0
            r0.buttonState = r2
        L_0x0459:
            int r2 = r0.documentAttachType
            if (r2 == r11) goto L_0x0461
            if (r2 != r15) goto L_0x047d
            if (r13 == 0) goto L_0x047d
        L_0x0461:
            boolean r2 = r0.canStreamVideo
            if (r2 == 0) goto L_0x047d
            r0.drawVideoImageButton = r12
            r16.getIconForCurrentState()
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            boolean r3 = r0.autoPlayingMedia
            if (r3 == 0) goto L_0x0472
            r3 = 4
            goto L_0x0473
        L_0x0472:
            r3 = 0
        L_0x0473:
            r2.setIcon(r3, r1, r6)
            org.telegram.ui.Components.RadialProgress2 r2 = r0.videoRadialProgress
            r2.setIcon(r15, r1, r6)
            goto L_0x0500
        L_0x047d:
            r2 = 0
            r0.drawVideoImageButton = r2
            org.telegram.ui.Components.RadialProgress2 r3 = r0.radialProgress
            int r4 = r16.getIconForCurrentState()
            r3.setIcon(r4, r1, r6)
            org.telegram.ui.Components.RadialProgress2 r3 = r0.videoRadialProgress
            r3.setIcon(r11, r1, r2)
            boolean r1 = r0.drawVideoSize
            if (r1 != 0) goto L_0x0500
            int r1 = r0.animatingDrawVideoImageButton
            if (r1 != 0) goto L_0x0500
            r0.animatingDrawVideoImageButtonProgress = r14
            goto L_0x0500
        L_0x0499:
            r0.buttonState = r12
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r2 = r2.getFileProgress(r4)
            int r3 = r0.documentAttachType
            if (r3 == r11) goto L_0x04ab
            if (r3 != r15) goto L_0x04d8
            if (r13 == 0) goto L_0x04d8
        L_0x04ab:
            boolean r3 = r0.canStreamVideo
            if (r3 == 0) goto L_0x04d8
            r0.drawVideoImageButton = r12
            r16.getIconForCurrentState()
            org.telegram.ui.Components.RadialProgress2 r3 = r0.radialProgress
            boolean r4 = r0.autoPlayingMedia
            if (r4 != 0) goto L_0x04c1
            int r4 = r0.documentAttachType
            if (r4 != r15) goto L_0x04bf
            goto L_0x04c1
        L_0x04bf:
            r4 = 0
            goto L_0x04c2
        L_0x04c1:
            r4 = 4
        L_0x04c2:
            r3.setIcon(r4, r1, r6)
            org.telegram.ui.Components.RadialProgress2 r3 = r0.videoRadialProgress
            if (r2 == 0) goto L_0x04cd
            float r14 = r2.floatValue()
        L_0x04cd:
            r3.setProgress(r14, r6)
            org.telegram.ui.Components.RadialProgress2 r2 = r0.videoRadialProgress
            r3 = 14
            r2.setIcon(r3, r1, r6)
            goto L_0x0500
        L_0x04d8:
            r3 = 0
            r0.drawVideoImageButton = r3
            org.telegram.ui.Components.RadialProgress2 r4 = r0.radialProgress
            if (r2 == 0) goto L_0x04e4
            float r2 = r2.floatValue()
            goto L_0x04e5
        L_0x04e4:
            r2 = 0
        L_0x04e5:
            r4.setProgress(r2, r6)
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r4 = r16.getIconForCurrentState()
            r2.setIcon(r4, r1, r6)
            org.telegram.ui.Components.RadialProgress2 r2 = r0.videoRadialProgress
            r2.setIcon(r11, r1, r3)
            boolean r1 = r0.drawVideoSize
            if (r1 != 0) goto L_0x0500
            int r1 = r0.animatingDrawVideoImageButton
            if (r1 != 0) goto L_0x0500
            r0.animatingDrawVideoImageButtonProgress = r14
        L_0x0500:
            r16.invalidate()
            goto L_0x0736
        L_0x0505:
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            r2.removeLoadingFileObserver(r0)
            boolean r2 = r0.drawVideoImageButton
            if (r2 == 0) goto L_0x0527
            if (r6 == 0) goto L_0x0527
            int r2 = r0.animatingDrawVideoImageButton
            if (r2 == r12) goto L_0x052d
            float r3 = r0.animatingDrawVideoImageButtonProgress
            int r3 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r3 <= 0) goto L_0x052d
            if (r2 != 0) goto L_0x0524
            r2 = 1065353216(0x3var_, float:1.0)
            r0.animatingDrawVideoImageButtonProgress = r2
        L_0x0524:
            r0.animatingDrawVideoImageButton = r12
            goto L_0x052d
        L_0x0527:
            int r2 = r0.animatingDrawVideoImageButton
            if (r2 != 0) goto L_0x052d
            r0.animatingDrawVideoImageButtonProgress = r14
        L_0x052d:
            r2 = 0
            r0.drawVideoImageButton = r2
            r0.drawVideoSize = r2
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            boolean r2 = r2.needDrawBluredPreview()
            if (r2 == 0) goto L_0x053e
            r2 = -1
            r0.buttonState = r2
            goto L_0x0559
        L_0x053e:
            int r2 = r0.documentAttachType
            if (r2 != r15) goto L_0x054f
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            float r2 = r2.gifState
            r3 = 1065353216(0x3var_, float:1.0)
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 != 0) goto L_0x054f
            r0.buttonState = r15
            goto L_0x0559
        L_0x054f:
            int r2 = r0.documentAttachType
            if (r2 != r11) goto L_0x0556
            r0.buttonState = r9
            goto L_0x0559
        L_0x0556:
            r2 = -1
            r0.buttonState = r2
        L_0x0559:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.videoRadialProgress
            int r3 = r0.animatingDrawVideoImageButton
            if (r3 == 0) goto L_0x0560
            goto L_0x0561
        L_0x0560:
            r12 = 0
        L_0x0561:
            r2.setIcon(r11, r1, r12)
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r3 = r16.getIconForCurrentState()
            r2.setIcon(r3, r1, r6)
            if (r19 != 0) goto L_0x057e
            boolean r1 = r0.photoNotSet
            if (r1 == 0) goto L_0x057e
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            org.telegram.messenger.MessageObject$GroupedMessages r2 = r0.currentMessagesGroup
            boolean r3 = r0.pinnedBottom
            boolean r4 = r0.pinnedTop
            r0.setMessageObject(r1, r2, r3, r4)
        L_0x057e:
            r16.invalidate()
            goto L_0x0736
        L_0x0583:
            r6 = r18
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.isOut()
            if (r3 == 0) goto L_0x059d
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.isSending()
            if (r3 != 0) goto L_0x05a7
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.isEditing()
            if (r3 != 0) goto L_0x05a7
        L_0x059d:
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.isSendError()
            if (r3 == 0) goto L_0x0624
            if (r2 == 0) goto L_0x0624
        L_0x05a7:
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.attachPath
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x060f
            int r3 = r0.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            org.telegram.messenger.MessageObject r4 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.attachPath
            r3.addLoadingFileObserver(r5, r4, r0)
            r0.wasSending = r12
            r0.buttonState = r11
            org.telegram.ui.Components.RadialProgress2 r3 = r0.radialProgress
            int r4 = r16.getIconForCurrentState()
            r3.setIcon(r4, r1, r6)
            if (r2 != 0) goto L_0x0607
            org.telegram.messenger.ImageLoader r1 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.attachPath
            java.lang.Float r1 = r1.getFileProgress(r2)
            if (r1 != 0) goto L_0x05f9
            int r2 = r0.currentAccount
            org.telegram.messenger.SendMessagesHelper r2 = org.telegram.messenger.SendMessagesHelper.getInstance(r2)
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            int r3 = r3.getId()
            boolean r2 = r2.isSendingMessage(r3)
            if (r2 == 0) goto L_0x05f9
            r2 = 1065353216(0x3var_, float:1.0)
            java.lang.Float r1 = java.lang.Float.valueOf(r2)
        L_0x05f9:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            if (r1 == 0) goto L_0x0601
            float r14 = r1.floatValue()
        L_0x0601:
            r3 = 0
            r2.setProgress(r14, r3)
            goto L_0x0733
        L_0x0607:
            r3 = 0
            org.telegram.ui.Components.RadialProgress2 r1 = r0.radialProgress
            r1.setProgress(r14, r3)
            goto L_0x0733
        L_0x060f:
            r2 = -1
            r3 = 0
            r0.buttonState = r2
            r16.getIconForCurrentState()
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            r4 = 12
            r2.setIcon(r4, r1, r3)
            org.telegram.ui.Components.RadialProgress2 r1 = r0.radialProgress
            r1.setProgress(r14, r3)
            goto L_0x0733
        L_0x0624:
            int r2 = r0.hasMiniProgress
            if (r2 == 0) goto L_0x06b8
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r3 = r3.isOutOwner()
            if (r3 == 0) goto L_0x0635
            java.lang.String r3 = "chat_outLoader"
            goto L_0x0637
        L_0x0635:
            java.lang.String r3 = "chat_inLoader"
        L_0x0637:
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setMiniProgressBackgroundColor(r3)
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r2 = r2.isPlayingMessage(r3)
            if (r2 == 0) goto L_0x065a
            if (r2 == 0) goto L_0x0657
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()
            boolean r2 = r2.isMessagePaused()
            if (r2 == 0) goto L_0x0657
            goto L_0x065a
        L_0x0657:
            r0.buttonState = r12
            goto L_0x065d
        L_0x065a:
            r2 = 0
            r0.buttonState = r2
        L_0x065d:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r3 = r16.getIconForCurrentState()
            r2.setIcon(r3, r1, r6)
            int r2 = r0.hasMiniProgress
            if (r2 != r12) goto L_0x0677
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            r2.removeLoadingFileObserver(r0)
            r2 = -1
            r0.miniButtonState = r2
            goto L_0x06ad
        L_0x0677:
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            r2.addLoadingFileObserver(r4, r3, r0)
            int r2 = r0.currentAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            boolean r2 = r2.isLoadingFile(r4)
            if (r2 != 0) goto L_0x0692
            r2 = 0
            r0.miniButtonState = r2
            goto L_0x06ad
        L_0x0692:
            r0.miniButtonState = r12
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r2 = r2.getFileProgress(r4)
            if (r2 == 0) goto L_0x06a8
            org.telegram.ui.Components.RadialProgress2 r3 = r0.radialProgress
            float r2 = r2.floatValue()
            r3.setProgress(r2, r6)
            goto L_0x06ad
        L_0x06a8:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            r2.setProgress(r14, r6)
        L_0x06ad:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r3 = r16.getMiniIconForCurrentState()
            r2.setMiniIcon(r3, r1, r6)
            goto L_0x0733
        L_0x06b8:
            if (r5 == 0) goto L_0x06ec
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            r2.removeLoadingFileObserver(r0)
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            boolean r2 = r2.isPlayingMessage(r3)
            if (r2 == 0) goto L_0x06df
            if (r2 == 0) goto L_0x06dc
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()
            boolean r2 = r2.isMessagePaused()
            if (r2 == 0) goto L_0x06dc
            goto L_0x06df
        L_0x06dc:
            r0.buttonState = r12
            goto L_0x06e2
        L_0x06df:
            r2 = 0
            r0.buttonState = r2
        L_0x06e2:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r3 = r16.getIconForCurrentState()
            r2.setIcon(r3, r1, r6)
            goto L_0x0733
        L_0x06ec:
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            r2.addLoadingFileObserver(r4, r3, r0)
            int r2 = r0.currentAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            boolean r2 = r2.isLoadingFile(r4)
            if (r2 != 0) goto L_0x070f
            r0.buttonState = r15
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r3 = r16.getIconForCurrentState()
            r2.setIcon(r3, r1, r6)
            goto L_0x0733
        L_0x070f:
            r0.buttonState = r11
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.Float r2 = r2.getFileProgress(r4)
            if (r2 == 0) goto L_0x0725
            org.telegram.ui.Components.RadialProgress2 r3 = r0.radialProgress
            float r2 = r2.floatValue()
            r3.setProgress(r2, r6)
            goto L_0x072a
        L_0x0725:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            r2.setProgress(r14, r6)
        L_0x072a:
            org.telegram.ui.Components.RadialProgress2 r2 = r0.radialProgress
            int r3 = r16.getIconForCurrentState()
            r2.setIcon(r3, r1, r6)
        L_0x0733:
            r16.updatePlayingMessageProgress()
        L_0x0736:
            int r1 = r0.hasMiniProgress
            if (r1 != 0) goto L_0x0740
            org.telegram.ui.Components.RadialProgress2 r1 = r0.radialProgress
            r2 = 0
            r1.setMiniIcon(r11, r2, r6)
        L_0x0740:
            return
        L_0x0741:
            r2 = 0
            org.telegram.ui.Components.RadialProgress2 r3 = r0.radialProgress
            r3.setIcon(r11, r1, r2)
            org.telegram.ui.Components.RadialProgress2 r3 = r0.radialProgress
            r3.setMiniIcon(r11, r1, r2)
            org.telegram.ui.Components.RadialProgress2 r3 = r0.videoRadialProgress
            r3.setIcon(r11, r1, r2)
            org.telegram.ui.Components.RadialProgress2 r3 = r0.videoRadialProgress
            r3.setMiniIcon(r11, r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateButtonState(boolean, boolean, boolean):void");
    }

    private void didPressMiniButton(boolean z) {
        int i = this.miniButtonState;
        if (i == 0) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            int i2 = this.documentAttachType;
            if (i2 == 3 || i2 == 5) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
            } else if (i2 == 4) {
                FileLoader instance = FileLoader.getInstance(this.currentAccount);
                TLRPC.Document document = this.documentAttach;
                MessageObject messageObject = this.currentMessageObject;
                instance.loadFile(document, messageObject, 1, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
            }
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        } else if (i == 1) {
            int i3 = this.documentAttachType;
            if ((i3 == 3 || i3 == 5) && MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        }
    }

    private void didPressButton(boolean z, boolean z2) {
        String str;
        TLRPC.PhotoSize photoSize;
        boolean z3 = z;
        int i = 2;
        if (this.buttonState == 0 && (!this.drawVideoImageButton || z2)) {
            int i2 = this.documentAttachType;
            if (i2 == 3 || i2 == 5) {
                if (this.miniButtonState == 0) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                }
                if (this.delegate.needPlayMessage(this.currentMessageObject)) {
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
                return;
            }
            this.cancelLoading = false;
            if (z2) {
                this.videoRadialProgress.setProgress(0.0f, false);
            } else {
                this.radialProgress.setProgress(0.0f, false);
            }
            if (this.currentPhotoObject == null || (!this.photoImage.hasNotThumb() && this.currentPhotoObjectThumb != null)) {
                photoSize = this.currentPhotoObjectThumb;
                str = this.currentPhotoFilterThumb;
            } else {
                photoSize = this.currentPhotoObject;
                str = ((photoSize instanceof TLRPC.TL_photoStrippedSize) || "s".equals(photoSize.type)) ? this.currentPhotoFilterThumb : this.currentPhotoFilter;
            }
            String str2 = str;
            MessageObject messageObject = this.currentMessageObject;
            int i3 = messageObject.type;
            if (i3 == 1) {
                this.photoImage.setForceLoading(true);
                ImageReceiver imageReceiver = this.photoImage;
                ImageLocation forObject = ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject);
                String str3 = this.currentPhotoFilter;
                ImageLocation forObject2 = ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject);
                String str4 = this.currentPhotoFilterThumb;
                int i4 = this.currentPhotoObject.size;
                MessageObject messageObject2 = this.currentMessageObject;
                imageReceiver.setImage(forObject, str3, forObject2, str4, i4, (String) null, messageObject2, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 0);
            } else if (i3 == 8) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
            } else if (!messageObject.isRoundVideo()) {
                int i5 = this.currentMessageObject.type;
                if (i5 == 9) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                } else {
                    int i6 = this.documentAttachType;
                    if (i6 == 4) {
                        FileLoader instance = FileLoader.getInstance(this.currentAccount);
                        TLRPC.Document document = this.documentAttach;
                        MessageObject messageObject3 = this.currentMessageObject;
                        if (!messageObject3.shouldEncryptPhotoOrVideo()) {
                            i = 0;
                        }
                        instance.loadFile(document, messageObject3, 1, i);
                    } else if (i5 != 0 || i6 == 0) {
                        this.photoImage.setForceLoading(true);
                        this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject), this.currentPhotoFilter, ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject), this.currentPhotoFilterThumb, 0, (String) null, this.currentMessageObject, 0);
                    } else if (i6 == 2) {
                        this.photoImage.setForceLoading(true);
                        this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), (String) null, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), this.currentPhotoFilterThumb, this.documentAttach.size, (String) null, this.currentMessageObject, 0);
                        this.currentMessageObject.gifState = 2.0f;
                    } else if (i6 == 1) {
                        FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 0, 0);
                    } else if (i6 == 8) {
                        this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), this.currentPhotoFilter, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), "b1", 0, "jpg", this.currentMessageObject, 1);
                    }
                }
            } else if (this.currentMessageObject.isSecretMedia()) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 1);
            } else {
                MessageObject messageObject4 = this.currentMessageObject;
                messageObject4.gifState = 2.0f;
                TLRPC.Document document2 = messageObject4.getDocument();
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(ImageLocation.getForDocument(document2), (String) null, ImageLocation.getForObject(photoSize, document2), str2, document2.size, (String) null, this.currentMessageObject, 0);
            }
            this.buttonState = 1;
            if (z2) {
                this.videoRadialProgress.setIcon(14, false, z3);
            } else {
                this.radialProgress.setIcon(getIconForCurrentState(), false, z3);
            }
            invalidate();
        } else if (this.buttonState != 1 || (this.drawVideoImageButton && !z2)) {
            int i7 = this.buttonState;
            if (i7 == 2) {
                int i8 = this.documentAttachType;
                if (i8 == 3 || i8 == 5) {
                    this.radialProgress.setProgress(0.0f, false);
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                    this.buttonState = 4;
                    this.radialProgress.setIcon(getIconForCurrentState(), true, z3);
                    invalidate();
                    return;
                }
                if (this.currentMessageObject.isRoundVideo()) {
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
                this.radialProgress.setIcon(getIconForCurrentState(), false, z3);
            } else if (i7 == 3 || (i7 == 0 && this.drawVideoImageButton)) {
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, z3);
                }
                this.delegate.didPressImage(this, 0.0f, 0.0f);
            } else if (this.buttonState == 4) {
                int i9 = this.documentAttachType;
                if (i9 != 3 && i9 != 5) {
                    return;
                }
                if ((!this.currentMessageObject.isOut() || (!this.currentMessageObject.isSending() && !this.currentMessageObject.isEditing())) && !this.currentMessageObject.isSendError()) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                    this.buttonState = 2;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z3);
                    invalidate();
                    return;
                }
                ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                if (chatMessageCellDelegate != null) {
                    chatMessageCellDelegate.didPressCancelSendButton(this);
                }
            }
        } else {
            this.photoImage.setForceLoading(false);
            int i10 = this.documentAttachType;
            if (i10 == 3 || i10 == 5) {
                if (MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z3);
                    invalidate();
                }
            } else if (!this.currentMessageObject.isOut() || (!this.currentMessageObject.isSending() && !this.currentMessageObject.isEditing())) {
                this.cancelLoading = true;
                int i11 = this.documentAttachType;
                if (i11 == 2 || i11 == 4 || i11 == 1 || i11 == 8) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else {
                    int i12 = this.currentMessageObject.type;
                    if (i12 == 0 || i12 == 1 || i12 == 8 || i12 == 5) {
                        ImageLoader.getInstance().cancelForceLoadingForImageReceiver(this.photoImage);
                        this.photoImage.cancelLoadImage();
                    } else if (i12 == 9) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
                    }
                }
                this.buttonState = 0;
                if (z2) {
                    this.videoRadialProgress.setIcon(2, false, z3);
                } else {
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z3);
                }
                invalidate();
            } else if (this.radialProgress.getIcon() != 6) {
                this.delegate.didPressCancelSendButton(this);
            }
        }
    }

    public void onFailedDownload(String str, boolean z) {
        int i = this.documentAttachType;
        updateButtonState(true, i == 3 || i == 5, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x009b, code lost:
        if ((r1 & 2) != 0) goto L_0x009d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSuccessDownload(java.lang.String r22) {
        /*
            r21 = this;
            r0 = r21
            int r1 = r0.documentAttachType
            r2 = 0
            r3 = 1
            r4 = 3
            if (r1 == r4) goto L_0x01aa
            r4 = 5
            if (r1 != r4) goto L_0x000e
            goto L_0x01aa
        L_0x000e:
            boolean r1 = r0.drawVideoImageButton
            r4 = 1065353216(0x3var_, float:1.0)
            if (r1 == 0) goto L_0x001a
            org.telegram.ui.Components.RadialProgress2 r1 = r0.videoRadialProgress
            r1.setProgress(r4, r3)
            goto L_0x001f
        L_0x001a:
            org.telegram.ui.Components.RadialProgress2 r1 = r0.radialProgress
            r1.setProgress(r4, r3)
        L_0x001f:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            boolean r1 = r1.needDrawBluredPreview()
            r5 = 2
            if (r1 != 0) goto L_0x0165
            boolean r1 = r0.autoPlayingMedia
            if (r1 != 0) goto L_0x0165
            org.telegram.tgnet.TLRPC$Document r1 = r0.documentAttach
            if (r1 == 0) goto L_0x0165
            int r6 = r0.documentAttachType
            r7 = 7
            java.lang.String r8 = "s"
            if (r6 != r7) goto L_0x0089
            org.telegram.messenger.ImageReceiver r9 = r0.photoImage
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r0.currentPhotoObject
            org.telegram.tgnet.TLObject r6 = r0.photoParentObject
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForObject(r1, r6)
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r0.currentPhotoObject
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r6 != 0) goto L_0x0059
            if (r1 == 0) goto L_0x0056
            java.lang.String r1 = r1.type
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x0056
            goto L_0x0059
        L_0x0056:
            java.lang.String r1 = r0.currentPhotoFilter
            goto L_0x005b
        L_0x0059:
            java.lang.String r1 = r0.currentPhotoFilterThumb
        L_0x005b:
            r13 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r0.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r6 = r0.photoParentObject
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForObject(r1, r6)
            java.lang.String r15 = r0.currentPhotoFilterThumb
            r16 = 0
            org.telegram.tgnet.TLRPC$Document r1 = r0.documentAttach
            int r1 = r1.size
            r18 = 0
            org.telegram.messenger.MessageObject r6 = r0.currentMessageObject
            r20 = 0
            java.lang.String r11 = "g"
            r17 = r1
            r19 = r6
            r9.setImage(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            org.telegram.messenger.ImageReceiver r1 = r0.photoImage
            r1.setAllowStartAnimation(r3)
            org.telegram.messenger.ImageReceiver r1 = r0.photoImage
            r1.startAnimation()
            r0.autoPlayingMedia = r3
            goto L_0x0165
        L_0x0089:
            boolean r1 = org.telegram.messenger.SharedConfig.autoplayVideo
            if (r1 == 0) goto L_0x0100
            r1 = 4
            if (r6 != r1) goto L_0x0100
            org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = r0.currentPosition
            if (r1 == 0) goto L_0x009d
            int r1 = r1.flags
            r6 = r1 & 1
            if (r6 == 0) goto L_0x0100
            r1 = r1 & r5
            if (r1 == 0) goto L_0x0100
        L_0x009d:
            r0.animatingNoSound = r5
            org.telegram.messenger.ImageReceiver r9 = r0.photoImage
            org.telegram.tgnet.TLRPC$Document r1 = r0.documentAttach
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r0.currentPhotoObject
            org.telegram.tgnet.TLObject r6 = r0.photoParentObject
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForObject(r1, r6)
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r0.currentPhotoObject
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r6 != 0) goto L_0x00c3
            if (r1 == 0) goto L_0x00c0
            java.lang.String r1 = r1.type
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x00c0
            goto L_0x00c3
        L_0x00c0:
            java.lang.String r1 = r0.currentPhotoFilter
            goto L_0x00c5
        L_0x00c3:
            java.lang.String r1 = r0.currentPhotoFilterThumb
        L_0x00c5:
            r13 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r0.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r6 = r0.photoParentObject
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForObject(r1, r6)
            java.lang.String r15 = r0.currentPhotoFilterThumb
            r16 = 0
            org.telegram.tgnet.TLRPC$Document r1 = r0.documentAttach
            int r1 = r1.size
            r18 = 0
            org.telegram.messenger.MessageObject r6 = r0.currentMessageObject
            r20 = 0
            java.lang.String r11 = "g"
            r17 = r1
            r19 = r6
            r9.setImage(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            boolean r1 = org.telegram.ui.PhotoViewer.isPlayingMessage(r1)
            if (r1 != 0) goto L_0x00f8
            org.telegram.messenger.ImageReceiver r1 = r0.photoImage
            r1.setAllowStartAnimation(r3)
            org.telegram.messenger.ImageReceiver r1 = r0.photoImage
            r1.startAnimation()
            goto L_0x00fd
        L_0x00f8:
            org.telegram.messenger.ImageReceiver r1 = r0.photoImage
            r1.setAllowStartAnimation(r2)
        L_0x00fd:
            r0.autoPlayingMedia = r3
            goto L_0x0165
        L_0x0100:
            int r1 = r0.documentAttachType
            if (r1 != r5) goto L_0x0165
            org.telegram.messenger.ImageReceiver r9 = r0.photoImage
            org.telegram.tgnet.TLRPC$Document r1 = r0.documentAttach
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r0.currentPhotoObject
            org.telegram.tgnet.TLObject r6 = r0.photoParentObject
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForObject(r1, r6)
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r0.currentPhotoObject
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r6 != 0) goto L_0x0128
            if (r1 == 0) goto L_0x0125
            java.lang.String r1 = r1.type
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x0125
            goto L_0x0128
        L_0x0125:
            java.lang.String r1 = r0.currentPhotoFilter
            goto L_0x012a
        L_0x0128:
            java.lang.String r1 = r0.currentPhotoFilterThumb
        L_0x012a:
            r13 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r0.currentPhotoObjectThumb
            org.telegram.tgnet.TLObject r6 = r0.photoParentObject
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForObject(r1, r6)
            java.lang.String r15 = r0.currentPhotoFilterThumb
            r16 = 0
            org.telegram.tgnet.TLRPC$Document r1 = r0.documentAttach
            int r1 = r1.size
            r18 = 0
            org.telegram.messenger.MessageObject r6 = r0.currentMessageObject
            r20 = 0
            java.lang.String r11 = "g"
            r17 = r1
            r19 = r6
            r9.setImage(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            boolean r1 = org.telegram.messenger.SharedConfig.autoplayGifs
            if (r1 == 0) goto L_0x0159
            org.telegram.messenger.ImageReceiver r1 = r0.photoImage
            r1.setAllowStartAnimation(r3)
            org.telegram.messenger.ImageReceiver r1 = r0.photoImage
            r1.startAnimation()
            goto L_0x0163
        L_0x0159:
            org.telegram.messenger.ImageReceiver r1 = r0.photoImage
            r1.setAllowStartAnimation(r2)
            org.telegram.messenger.ImageReceiver r1 = r0.photoImage
            r1.stopAnimation()
        L_0x0163:
            r0.autoPlayingMedia = r3
        L_0x0165:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            int r6 = r1.type
            if (r6 != 0) goto L_0x0193
            boolean r6 = r0.autoPlayingMedia
            if (r6 != 0) goto L_0x017f
            int r6 = r0.documentAttachType
            if (r6 != r5) goto L_0x017f
            float r1 = r1.gifState
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 == 0) goto L_0x017f
            r0.buttonState = r5
            r0.didPressButton(r3, r2)
            goto L_0x01b0
        L_0x017f:
            boolean r1 = r0.photoNotSet
            if (r1 != 0) goto L_0x0187
            r0.updateButtonState(r2, r3, r2)
            goto L_0x01b0
        L_0x0187:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            org.telegram.messenger.MessageObject$GroupedMessages r2 = r0.currentMessagesGroup
            boolean r3 = r0.pinnedBottom
            boolean r4 = r0.pinnedTop
            r0.setMessageObject(r1, r2, r3, r4)
            goto L_0x01b0
        L_0x0193:
            boolean r1 = r0.photoNotSet
            if (r1 != 0) goto L_0x019a
            r0.updateButtonState(r2, r3, r2)
        L_0x019a:
            boolean r1 = r0.photoNotSet
            if (r1 == 0) goto L_0x01b0
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            org.telegram.messenger.MessageObject$GroupedMessages r2 = r0.currentMessagesGroup
            boolean r3 = r0.pinnedBottom
            boolean r4 = r0.pinnedTop
            r0.setMessageObject(r1, r2, r3, r4)
            goto L_0x01b0
        L_0x01aa:
            r0.updateButtonState(r2, r3, r2)
            r21.updateWaveform()
        L_0x01b0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onSuccessDownload(java.lang.String):void");
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        int i;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && z && !z2 && !messageObject.mediaExists && !messageObject.attachPathExists) {
            if ((messageObject.type == 0 && ((i = this.documentAttachType) == 8 || i == 0 || i == 6)) || this.currentMessageObject.type == 1) {
                this.currentMessageObject.mediaExists = true;
                updateButtonState(false, true, false);
            }
        }
    }

    public void onAnimationReady(ImageReceiver imageReceiver) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && imageReceiver == this.photoImage && messageObject.isAnimatedSticker()) {
            this.delegate.setShouldNotRepeatSticker(this.currentMessageObject);
        }
    }

    public void onProgressDownload(String str, float f) {
        if (this.drawVideoImageButton) {
            this.videoRadialProgress.setProgress(f, true);
        } else {
            this.radialProgress.setProgress(f, true);
        }
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.hasMiniProgress != 0) {
                if (this.miniButtonState != 1) {
                    updateButtonState(false, false, false);
                }
            } else if (this.buttonState != 4) {
                updateButtonState(false, false, false);
            }
        } else if (this.hasMiniProgress != 0) {
            if (this.miniButtonState != 1) {
                updateButtonState(false, false, false);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false, false, false);
        }
    }

    public void onProgressUpload(String str, float f, boolean z) {
        this.radialProgress.setProgress(f, true);
        if (f == 1.0f && this.currentPosition != null && SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId()) && this.buttonState == 1) {
            this.drawRadialCheckBackground = true;
            getIconForCurrentState();
            this.radialProgress.setIcon(6, false, true);
        }
    }

    public void onProvideStructure(ViewStructure viewStructure) {
        super.onProvideStructure(viewStructure);
        if (this.allowAssistant && Build.VERSION.SDK_INT >= 23) {
            CharSequence charSequence = this.currentMessageObject.messageText;
            if (charSequence == null || charSequence.length() <= 0) {
                CharSequence charSequence2 = this.currentMessageObject.caption;
                if (charSequence2 != null && charSequence2.length() > 0) {
                    viewStructure.setText(this.currentMessageObject.caption);
                    return;
                }
                return;
            }
            viewStructure.setText(this.currentMessageObject.messageText);
        }
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: java.lang.CharSequence} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: java.lang.CharSequence} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: java.lang.CharSequence} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: java.lang.CharSequence} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v60, resolved type: java.lang.String} */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00de, code lost:
        if (r14.isEditing() == false) goto L_0x00e1;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:103:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0139  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x014d  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01d2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void measureTime(org.telegram.messenger.MessageObject r14) {
        /*
            r13 = this;
            boolean r0 = r14.scheduled
            java.lang.String r1 = ""
            r2 = 0
            if (r0 == 0) goto L_0x0009
        L_0x0007:
            r0 = r2
            goto L_0x0057
        L_0x0009:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            java.lang.String r3 = r0.post_author
            java.lang.String r4 = "\n"
            if (r3 == 0) goto L_0x0016
            java.lang.String r0 = r3.replace(r4, r1)
            goto L_0x0057
        L_0x0016:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x0023
            java.lang.String r0 = r0.post_author
            if (r0 == 0) goto L_0x0023
            java.lang.String r0 = r0.replace(r4, r1)
            goto L_0x0057
        L_0x0023:
            boolean r0 = r14.isOutOwner()
            if (r0 != 0) goto L_0x0007
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            int r3 = r0.from_id
            if (r3 <= 0) goto L_0x0007
            boolean r0 = r0.post
            if (r0 == 0) goto L_0x0007
            int r0 = r13.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            int r3 = r3.from_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            if (r0 == 0) goto L_0x0007
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r3 = 10
            r4 = 32
            java.lang.String r0 = r0.replace(r3, r4)
        L_0x0057:
            org.telegram.messenger.MessageObject r3 = r13.currentMessageObject
            boolean r3 = r3.isFromUser()
            if (r3 == 0) goto L_0x0072
            int r3 = r13.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner
            int r4 = r4.from_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            goto L_0x0073
        L_0x0072:
            r3 = r2
        L_0x0073:
            boolean r4 = r14.scheduled
            r5 = 1
            r6 = 0
            if (r4 != 0) goto L_0x00e1
            boolean r4 = r14.isLiveLocation()
            if (r4 != 0) goto L_0x00e1
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner
            boolean r4 = r4.edit_hide
            if (r4 != 0) goto L_0x00e1
            long r7 = r14.getDialogId()
            r9 = 777000(0xbdb28, double:3.83889E-318)
            int r4 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r4 == 0) goto L_0x00e1
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner
            int r7 = r4.via_bot_id
            if (r7 != 0) goto L_0x00e1
            java.lang.String r4 = r4.via_bot_name
            if (r4 != 0) goto L_0x00e1
            if (r3 == 0) goto L_0x00a1
            boolean r3 = r3.bot
            if (r3 == 0) goto L_0x00a1
            goto L_0x00e1
        L_0x00a1:
            org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = r13.currentPosition
            r4 = 32768(0x8000, float:4.5918E-41)
            if (r3 == 0) goto L_0x00d3
            org.telegram.messenger.MessageObject$GroupedMessages r3 = r13.currentMessagesGroup
            if (r3 != 0) goto L_0x00ad
            goto L_0x00d3
        L_0x00ad:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r3.messages
            int r3 = r3.size()
            r7 = 0
        L_0x00b4:
            if (r7 >= r3) goto L_0x00e1
            org.telegram.messenger.MessageObject$GroupedMessages r8 = r13.currentMessagesGroup
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r8.messages
            java.lang.Object r8 = r8.get(r7)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            org.telegram.tgnet.TLRPC$Message r9 = r8.messageOwner
            int r9 = r9.flags
            r9 = r9 & r4
            if (r9 != 0) goto L_0x00d1
            boolean r8 = r8.isEditing()
            if (r8 == 0) goto L_0x00ce
            goto L_0x00d1
        L_0x00ce:
            int r7 = r7 + 1
            goto L_0x00b4
        L_0x00d1:
            r3 = 1
            goto L_0x00e2
        L_0x00d3:
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            int r3 = r3.flags
            r3 = r3 & r4
            if (r3 != 0) goto L_0x00d1
            boolean r3 = r14.isEditing()
            if (r3 == 0) goto L_0x00e1
            goto L_0x00d1
        L_0x00e1:
            r3 = 0
        L_0x00e2:
            org.telegram.messenger.MessageObject r4 = r13.currentMessageObject
            boolean r7 = r4.scheduled
            r8 = 2147483646(0x7ffffffe, float:NaN)
            if (r7 == 0) goto L_0x00f3
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            int r4 = r4.date
            if (r4 != r8) goto L_0x00f3
            r3 = r1
            goto L_0x0137
        L_0x00f3:
            r9 = 1000(0x3e8, double:4.94E-321)
            if (r3 == 0) goto L_0x0126
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 2131624974(0x7f0e040e, float:1.8877143E38)
            java.lang.String r7 = "EditedMessage"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.append(r4)
            java.lang.String r4 = " "
            r3.append(r4)
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r4 = r4.formatterDay
            org.telegram.tgnet.TLRPC$Message r7 = r14.messageOwner
            int r7 = r7.date
            long r11 = (long) r7
            long r11 = r11 * r9
            java.lang.String r4 = r4.format((long) r11)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            goto L_0x0137
        L_0x0126:
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r3 = r3.formatterDay
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner
            int r4 = r4.date
            long r11 = (long) r4
            long r11 = r11 * r9
            java.lang.String r3 = r3.format((long) r11)
        L_0x0137:
            if (r0 == 0) goto L_0x014d
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r7 = ", "
            r4.append(r7)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            r13.currentTimeString = r3
            goto L_0x014f
        L_0x014d:
            r13.currentTimeString = r3
        L_0x014f:
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_timePaint
            java.lang.String r4 = r13.currentTimeString
            float r3 = r3.measureText(r4)
            double r3 = (double) r3
            double r3 = java.lang.Math.ceil(r3)
            int r3 = (int) r3
            r13.timeWidth = r3
            r13.timeTextWidth = r3
            org.telegram.messenger.MessageObject r3 = r13.currentMessageObject
            boolean r4 = r3.scheduled
            if (r4 == 0) goto L_0x0178
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            int r3 = r3.date
            if (r3 != r8) goto L_0x0178
            int r3 = r13.timeWidth
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            r13.timeWidth = r3
        L_0x0178:
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            int r4 = r3.flags
            r4 = r4 & 1024(0x400, float:1.435E-42)
            if (r4 == 0) goto L_0x01bb
            java.lang.Object[] r4 = new java.lang.Object[r5]
            int r3 = r3.views
            int r3 = java.lang.Math.max(r5, r3)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatShortNumber(r3, r2)
            r4[r6] = r2
            java.lang.String r2 = "%s"
            java.lang.String r2 = java.lang.String.format(r2, r4)
            r13.currentViewsString = r2
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_timePaint
            java.lang.String r3 = r13.currentViewsString
            float r2 = r2.measureText(r3)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            r13.viewsTextWidth = r2
            int r2 = r13.timeWidth
            int r3 = r13.viewsTextWidth
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.chat_msgInViewsDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 + r4
            int r2 = r2 + r3
            r13.timeWidth = r2
        L_0x01bb:
            boolean r2 = r14.scheduled
            if (r2 == 0) goto L_0x01d0
            boolean r2 = r14.isSendError()
            if (r2 == 0) goto L_0x01d0
            int r2 = r13.timeWidth
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r13.timeWidth = r2
        L_0x01d0:
            if (r0 == 0) goto L_0x023b
            int r2 = r13.availableTimeWidth
            if (r2 != 0) goto L_0x01de
            r2 = 1148846080(0x447a0000, float:1000.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r13.availableTimeWidth = r2
        L_0x01de:
            int r2 = r13.availableTimeWidth
            int r3 = r13.timeWidth
            int r2 = r2 - r3
            boolean r3 = r14.isOutOwner()
            if (r3 == 0) goto L_0x01fc
            int r14 = r14.type
            r3 = 5
            if (r14 != r3) goto L_0x01f5
            r14 = 1101004800(0x41a00000, float:20.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            goto L_0x01fb
        L_0x01f5:
            r14 = 1119879168(0x42CLASSNAME, float:96.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
        L_0x01fb:
            int r2 = r2 - r14
        L_0x01fc:
            android.text.TextPaint r14 = org.telegram.ui.ActionBar.Theme.chat_timePaint
            int r3 = r0.length()
            float r14 = r14.measureText(r0, r6, r3)
            double r3 = (double) r14
            double r3 = java.lang.Math.ceil(r3)
            int r14 = (int) r3
            if (r14 <= r2) goto L_0x021d
            if (r2 > 0) goto L_0x0212
            r14 = 0
            goto L_0x021e
        L_0x0212:
            android.text.TextPaint r14 = org.telegram.ui.ActionBar.Theme.chat_timePaint
            float r1 = (float) r2
            android.text.TextUtils$TruncateAt r3 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r1 = android.text.TextUtils.ellipsize(r0, r14, r1, r3)
            r14 = r2
            goto L_0x021e
        L_0x021d:
            r1 = r0
        L_0x021e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            java.lang.String r1 = r13.currentTimeString
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r13.currentTimeString = r0
            int r0 = r13.timeTextWidth
            int r0 = r0 + r14
            r13.timeTextWidth = r0
            int r0 = r13.timeWidth
            int r0 = r0 + r14
            r13.timeWidth = r0
        L_0x023b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.measureTime(org.telegram.messenger.MessageObject):void");
    }

    private boolean isDrawSelectionBackground() {
        return ((isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted)) && !textIsSelectionMode();
    }

    /* access modifiers changed from: private */
    public boolean isOpenChatByShare(MessageObject messageObject) {
        TLRPC.MessageFwdHeader messageFwdHeader = messageObject.messageOwner.fwd_from;
        return (messageFwdHeader == null || messageFwdHeader.saved_from_peer == null) ? false : true;
    }

    private boolean checkNeedDrawShareButton(MessageObject messageObject) {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null && !groupedMessagePosition.last) {
            return false;
        }
        if (messageObject.messageOwner.fwd_from != null && !messageObject.isOutOwner() && messageObject.messageOwner.fwd_from.saved_from_peer != null && messageObject.getDialogId() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId())) {
            this.drwaShareGoIcon = true;
        }
        return messageObject.needDrawShareButton();
    }

    public boolean isInsideBackground(float f, float f2) {
        if (this.currentBackgroundDrawable != null) {
            int i = this.backgroundDrawableLeft;
            return f >= ((float) i) && f <= ((float) (i + this.backgroundDrawableRight));
        }
    }

    private void updateCurrentUserAndChat() {
        TLRPC.Peer peer;
        int i;
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        TLRPC.MessageFwdHeader messageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (messageFwdHeader != null && messageFwdHeader.channel_id != 0 && this.currentMessageObject.getDialogId() == ((long) clientUserId)) {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(messageFwdHeader.channel_id));
        } else if (messageFwdHeader != null && (peer = messageFwdHeader.saved_from_peer) != null) {
            int i2 = peer.user_id;
            if (i2 != 0) {
                int i3 = messageFwdHeader.from_id;
                if (i3 != 0) {
                    this.currentUser = instance.getUser(Integer.valueOf(i3));
                } else {
                    this.currentUser = instance.getUser(Integer.valueOf(i2));
                }
            } else if (peer.channel_id == 0) {
                int i4 = peer.chat_id;
                if (i4 != 0) {
                    int i5 = messageFwdHeader.from_id;
                    if (i5 != 0) {
                        this.currentUser = instance.getUser(Integer.valueOf(i5));
                    } else {
                        this.currentChat = instance.getChat(Integer.valueOf(i4));
                    }
                }
            } else if (!this.currentMessageObject.isSavedFromMegagroup() || (i = messageFwdHeader.from_id) == 0) {
                this.currentChat = instance.getChat(Integer.valueOf(messageFwdHeader.saved_from_peer.channel_id));
            } else {
                this.currentUser = instance.getUser(Integer.valueOf(i));
            }
        } else if (messageFwdHeader != null && messageFwdHeader.from_id != 0 && messageFwdHeader.channel_id == 0 && this.currentMessageObject.getDialogId() == ((long) clientUserId)) {
            this.currentUser = instance.getUser(Integer.valueOf(messageFwdHeader.from_id));
        } else if (messageFwdHeader != null && !TextUtils.isEmpty(messageFwdHeader.from_name) && this.currentMessageObject.getDialogId() == ((long) clientUserId)) {
            this.currentUser = new TLRPC.TL_user();
            this.currentUser.first_name = messageFwdHeader.from_name;
        } else if (this.currentMessageObject.isFromUser()) {
            this.currentUser = instance.getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
        } else {
            TLRPC.Message message = this.currentMessageObject.messageOwner;
            int i6 = message.from_id;
            if (i6 < 0) {
                this.currentChat = instance.getChat(Integer.valueOf(-i6));
            } else if (message.post) {
                this.currentChat = instance.getChat(Integer.valueOf(message.to_id.channel_id));
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: android.text.StaticLayout[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: java.lang.CharSequence} */
    /* JADX WARNING: type inference failed for: r3v1, types: [org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, java.lang.String] */
    /* JADX WARNING: type inference failed for: r3v49 */
    /* JADX WARNING: type inference failed for: r3v73 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x025c  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x034c  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0379 A[Catch:{ Exception -> 0x03da }] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x03a1 A[Catch:{ Exception -> 0x03da }] */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x03a5 A[Catch:{ Exception -> 0x03da }] */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x03d6 A[Catch:{ Exception -> 0x03da }] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x03e7  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0432  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0450  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x0462  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0482  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04f7  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0540  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0622 A[Catch:{ Exception -> 0x062e }] */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0854 A[Catch:{ Exception -> 0x08a6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0857 A[Catch:{ Exception -> 0x08a6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0862 A[Catch:{ Exception -> 0x08a6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x08af A[Catch:{ Exception -> 0x08fd }] */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x08b2 A[Catch:{ Exception -> 0x08fd }] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x08bd A[Catch:{ Exception -> 0x08fd }] */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x0903  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0162  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0164  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x017f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0197  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01af  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01d0  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0225  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x023e  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x024e  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x0251  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setMessageObjectInternal(org.telegram.messenger.MessageObject r40) {
        /*
            r39 = this;
            r1 = r39
            r2 = r40
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            int r0 = r0.flags
            r0 = r0 & 1024(0x400, float:1.435E-42)
            r3 = 1
            if (r0 == 0) goto L_0x0026
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            boolean r4 = r0.scheduled
            if (r4 != 0) goto L_0x0026
            boolean r0 = r0.viewsReloaded
            if (r0 != 0) goto L_0x0026
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            r0.addToViewsQueue(r4)
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            r0.viewsReloaded = r3
        L_0x0026:
            r39.updateCurrentUserAndChat()
            boolean r0 = r1.isAvatarVisible
            r4 = 0
            r5 = 0
            if (r0 == 0) goto L_0x009b
            org.telegram.tgnet.TLRPC$User r0 = r1.currentUser
            if (r0 == 0) goto L_0x0059
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo
            if (r0 == 0) goto L_0x003c
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small
            r1.currentPhoto = r0
            goto L_0x003e
        L_0x003c:
            r1.currentPhoto = r4
        L_0x003e:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$User r6 = r1.currentUser
            r0.setInfo((org.telegram.tgnet.TLRPC.User) r6)
            org.telegram.messenger.ImageReceiver r7 = r1.avatarImage
            org.telegram.tgnet.TLRPC$User r0 = r1.currentUser
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForUser(r0, r5)
            org.telegram.ui.Components.AvatarDrawable r10 = r1.avatarDrawable
            r11 = 0
            org.telegram.tgnet.TLRPC$User r12 = r1.currentUser
            r13 = 0
            java.lang.String r9 = "50_50"
            r7.setImage(r8, r9, r10, r11, r12, r13)
            goto L_0x009d
        L_0x0059:
            org.telegram.tgnet.TLRPC$Chat r0 = r1.currentChat
            if (r0 == 0) goto L_0x0083
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r0.photo
            if (r0 == 0) goto L_0x0066
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small
            r1.currentPhoto = r0
            goto L_0x0068
        L_0x0066:
            r1.currentPhoto = r4
        L_0x0068:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$Chat r6 = r1.currentChat
            r0.setInfo((org.telegram.tgnet.TLRPC.Chat) r6)
            org.telegram.messenger.ImageReceiver r7 = r1.avatarImage
            org.telegram.tgnet.TLRPC$Chat r0 = r1.currentChat
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForChat(r0, r5)
            org.telegram.ui.Components.AvatarDrawable r10 = r1.avatarDrawable
            r11 = 0
            org.telegram.tgnet.TLRPC$Chat r12 = r1.currentChat
            r13 = 0
            java.lang.String r9 = "50_50"
            r7.setImage(r8, r9, r10, r11, r12, r13)
            goto L_0x009d
        L_0x0083:
            r1.currentPhoto = r4
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            int r6 = r6.from_id
            r0.setInfo(r6, r4, r4)
            org.telegram.messenger.ImageReceiver r7 = r1.avatarImage
            r8 = 0
            r9 = 0
            org.telegram.ui.Components.AvatarDrawable r10 = r1.avatarDrawable
            r11 = 0
            r12 = 0
            r13 = 0
            r7.setImage(r8, r9, r10, r11, r12, r13)
            goto L_0x009d
        L_0x009b:
            r1.currentPhoto = r4
        L_0x009d:
            r39.measureTime(r40)
            r1.namesOffset = r5
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            int r6 = r0.via_bot_id
            r7 = 2131626921(0x7f0e0ba9, float:1.8881092E38)
            java.lang.String r8 = "ViaBot"
            r9 = 2
            if (r6 == 0) goto L_0x0108
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            int r6 = r6.via_bot_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r6)
            if (r0 == 0) goto L_0x0150
            java.lang.String r6 = r0.username
            if (r6 == 0) goto L_0x0150
            int r6 = r6.length()
            if (r6 <= 0) goto L_0x0150
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r10 = "@"
            r6.append(r10)
            java.lang.String r10 = r0.username
            r6.append(r10)
            java.lang.String r6 = r6.toString()
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r10[r5] = r11
            r10[r3] = r6
            java.lang.String r11 = " %s <b>%s</b>"
            java.lang.String r10 = java.lang.String.format(r11, r10)
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.replaceTags(r10)
            android.text.TextPaint r11 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint
            int r12 = r10.length()
            float r11 = r11.measureText(r10, r5, r12)
            double r11 = (double) r11
            double r11 = java.lang.Math.ceil(r11)
            int r11 = (int) r11
            r1.viaWidth = r11
            r1.currentViaBotUser = r0
            goto L_0x0152
        L_0x0108:
            java.lang.String r0 = r0.via_bot_name
            if (r0 == 0) goto L_0x0150
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0150
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "@"
            r0.append(r6)
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            java.lang.String r6 = r6.via_bot_name
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r6[r5] = r10
            r6[r3] = r0
            java.lang.String r10 = " %s <b>%s</b>"
            java.lang.String r6 = java.lang.String.format(r10, r6)
            android.text.SpannableStringBuilder r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6)
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint
            int r11 = r6.length()
            float r10 = r10.measureText(r6, r5, r11)
            double r10 = (double) r10
            double r10 = java.lang.Math.ceil(r10)
            int r10 = (int) r10
            r1.viaWidth = r10
            r10 = r6
            r6 = r0
            goto L_0x0152
        L_0x0150:
            r6 = r4
            r10 = r6
        L_0x0152:
            boolean r0 = r1.drawName
            if (r0 == 0) goto L_0x0164
            boolean r0 = r1.isChat
            if (r0 == 0) goto L_0x0164
            org.telegram.messenger.MessageObject r0 = r1.currentMessageObject
            boolean r0 = r0.isOutOwner()
            if (r0 != 0) goto L_0x0164
            r0 = 1
            goto L_0x0165
        L_0x0164:
            r0 = 0
        L_0x0165:
            org.telegram.tgnet.TLRPC$Message r11 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r11 = r11.fwd_from
            if (r11 == 0) goto L_0x0171
            int r11 = r2.type
            r12 = 14
            if (r11 != r12) goto L_0x0175
        L_0x0171:
            if (r6 == 0) goto L_0x0175
            r11 = 1
            goto L_0x0176
        L_0x0175:
            r11 = 0
        L_0x0176:
            java.lang.String r12 = "fonts/rmedium.ttf"
            r15 = 32
            r14 = 10
            r13 = 5
            if (r0 != 0) goto L_0x018b
            if (r11 == 0) goto L_0x0182
            goto L_0x018b
        L_0x0182:
            r1.currentNameString = r4
            r1.nameLayout = r4
            r1.nameWidth = r5
            r3 = r4
            goto L_0x03e9
        L_0x018b:
            r1.drawNameLayout = r3
            int r4 = r39.getMaxNameWidth()
            r1.nameWidth = r4
            int r4 = r1.nameWidth
            if (r4 >= 0) goto L_0x019f
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameWidth = r4
        L_0x019f:
            boolean r4 = r1.isMegagroup
            if (r4 == 0) goto L_0x01d0
            org.telegram.tgnet.TLRPC$Chat r4 = r1.currentChat
            if (r4 == 0) goto L_0x01d0
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            boolean r4 = r4.isForwardedChannelPost()
            if (r4 == 0) goto L_0x01d0
            r4 = 2131624904(0x7f0e03c8, float:1.8877E38)
            java.lang.String r9 = "DiscussChannel"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_adminPaint
            float r9 = r9.measureText(r4)
            r21 = r4
            double r3 = (double) r9
            double r3 = java.lang.Math.ceil(r3)
            int r3 = (int) r3
            int r4 = r1.nameWidth
            int r4 = r4 - r3
            r1.nameWidth = r4
            r23 = r21
            r21 = r8
            goto L_0x0223
        L_0x01d0:
            org.telegram.tgnet.TLRPC$User r3 = r1.currentUser
            if (r3 == 0) goto L_0x021e
            org.telegram.messenger.MessageObject r3 = r1.currentMessageObject
            boolean r3 = r3.isOutOwner()
            if (r3 != 0) goto L_0x021e
            org.telegram.messenger.MessageObject r3 = r1.currentMessageObject
            boolean r3 = r3.isAnyKindOfSticker()
            if (r3 != 0) goto L_0x021e
            org.telegram.messenger.MessageObject r3 = r1.currentMessageObject
            int r3 = r3.type
            if (r3 == r13) goto L_0x021e
            org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate r3 = r1.delegate
            if (r3 == 0) goto L_0x021e
            org.telegram.tgnet.TLRPC$User r4 = r1.currentUser
            int r4 = r4.id
            java.lang.String r3 = r3.getAdminRank(r4)
            if (r3 == 0) goto L_0x021e
            int r4 = r3.length()
            if (r4 != 0) goto L_0x0207
            r3 = 2131624605(0x7f0e029d, float:1.8876394E38)
            java.lang.String r4 = "ChatAdmin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
        L_0x0207:
            r4 = r3
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_adminPaint
            float r3 = r3.measureText(r4)
            r21 = r8
            double r7 = (double) r3
            double r7 = java.lang.Math.ceil(r7)
            int r3 = (int) r7
            int r7 = r1.nameWidth
            int r7 = r7 - r3
            r1.nameWidth = r7
            r23 = r4
            goto L_0x0223
        L_0x021e:
            r21 = r8
            r3 = 0
            r23 = 0
        L_0x0223:
            if (r0 == 0) goto L_0x023e
            org.telegram.tgnet.TLRPC$User r0 = r1.currentUser
            if (r0 == 0) goto L_0x0230
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1.currentNameString = r0
            goto L_0x0242
        L_0x0230:
            org.telegram.tgnet.TLRPC$Chat r0 = r1.currentChat
            if (r0 == 0) goto L_0x0239
            java.lang.String r0 = r0.title
            r1.currentNameString = r0
            goto L_0x0242
        L_0x0239:
            java.lang.String r0 = "DELETED"
            r1.currentNameString = r0
            goto L_0x0242
        L_0x023e:
            java.lang.String r0 = ""
            r1.currentNameString = r0
        L_0x0242:
            java.lang.String r0 = r1.currentNameString
            java.lang.String r0 = r0.replace(r14, r15)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_namePaint
            int r7 = r1.nameWidth
            if (r11 == 0) goto L_0x0251
            int r8 = r1.viaWidth
            goto L_0x0252
        L_0x0251:
            r8 = 0
        L_0x0252:
            int r7 = r7 - r8
            float r7 = (float) r7
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r4, r7, r8)
            if (r11 == 0) goto L_0x034c
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_namePaint
            int r7 = r0.length()
            float r4 = r4.measureText(r0, r5, r7)
            double r7 = (double) r4
            double r7 = java.lang.Math.ceil(r7)
            int r4 = (int) r7
            r1.viaNameWidth = r4
            int r4 = r1.viaNameWidth
            if (r4 == 0) goto L_0x027b
            r7 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r7
            r1.viaNameWidth = r4
        L_0x027b:
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            boolean r4 = r4.shouldDrawWithoutBackground()
            if (r4 == 0) goto L_0x028f
            java.lang.String r4 = "chat_stickerViaBotNameText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
        L_0x0289:
            r8 = r21
            r7 = 2131626921(0x7f0e0ba9, float:1.8881092E38)
            goto L_0x02a1
        L_0x028f:
            org.telegram.messenger.MessageObject r4 = r1.currentMessageObject
            boolean r4 = r4.isOutOwner()
            if (r4 == 0) goto L_0x029a
            java.lang.String r4 = "chat_outViaBotNameText"
            goto L_0x029c
        L_0x029a:
            java.lang.String r4 = "chat_inViaBotNameText"
        L_0x029c:
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            goto L_0x0289
        L_0x02a1:
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r7 = r1.currentNameString
            int r7 = r7.length()
            if (r7 <= 0) goto L_0x0303
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r9 = 3
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r5] = r0
            r20 = 1
            r9[r20] = r11
            r19 = 2
            r9[r19] = r6
            java.lang.String r13 = "%s %s %s"
            java.lang.String r9 = java.lang.String.format(r13, r9)
            r7.<init>(r9)
            org.telegram.ui.Components.TypefaceSpan r9 = new org.telegram.ui.Components.TypefaceSpan
            android.graphics.Typeface r13 = android.graphics.Typeface.DEFAULT
            r9.<init>(r13, r5, r4)
            r1.viaSpan1 = r9
            int r13 = r0.length()
            int r13 = r13 + 1
            int r22 = r0.length()
            int r22 = r22 + 1
            int r24 = r11.length()
            int r14 = r22 + r24
            r15 = 33
            r7.setSpan(r9, r13, r14, r15)
            org.telegram.ui.Components.TypefaceSpan r9 = new org.telegram.ui.Components.TypefaceSpan
            android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r9.<init>(r13, r5, r4)
            r1.viaSpan2 = r9
            int r0 = r0.length()
            r13 = 2
            int r0 = r0 + r13
            int r4 = r11.length()
            int r0 = r0 + r4
            int r4 = r7.length()
            r7.setSpan(r9, r0, r4, r15)
            goto L_0x0340
        L_0x0303:
            r13 = 2
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            java.lang.Object[] r0 = new java.lang.Object[r13]
            r0[r5] = r11
            r9 = 1
            r0[r9] = r6
            java.lang.String r13 = "%s %s"
            java.lang.String r0 = java.lang.String.format(r13, r0)
            r7.<init>(r0)
            org.telegram.ui.Components.TypefaceSpan r0 = new org.telegram.ui.Components.TypefaceSpan
            android.graphics.Typeface r13 = android.graphics.Typeface.DEFAULT
            r0.<init>(r13, r5, r4)
            r1.viaSpan1 = r0
            int r13 = r11.length()
            int r13 = r13 + r9
            r14 = 33
            r7.setSpan(r0, r5, r13, r14)
            org.telegram.ui.Components.TypefaceSpan r0 = new org.telegram.ui.Components.TypefaceSpan
            android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r0.<init>(r13, r5, r4)
            r1.viaSpan2 = r0
            int r4 = r11.length()
            int r4 = r4 + r9
            int r9 = r7.length()
            r7.setSpan(r0, r4, r9, r14)
        L_0x0340:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_namePaint
            int r4 = r1.nameWidth
            float r4 = (float) r4
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r7, r0, r4, r9)
            goto L_0x034e
        L_0x034c:
            r8 = r21
        L_0x034e:
            r31 = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x03da }
            android.text.TextPaint r32 = org.telegram.ui.ActionBar.Theme.chat_namePaint     // Catch:{ Exception -> 0x03da }
            int r4 = r1.nameWidth     // Catch:{ Exception -> 0x03da }
            r7 = 1073741824(0x40000000, float:2.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x03da }
            int r33 = r4 + r9
            android.text.Layout$Alignment r34 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x03da }
            r35 = 1065353216(0x3var_, float:1.0)
            r36 = 0
            r37 = 0
            r30 = r0
            r30.<init>(r31, r32, r33, r34, r35, r36, r37)     // Catch:{ Exception -> 0x03da }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x03da }
            android.text.StaticLayout r0 = r1.nameLayout     // Catch:{ Exception -> 0x03da }
            if (r0 == 0) goto L_0x03a1
            android.text.StaticLayout r0 = r1.nameLayout     // Catch:{ Exception -> 0x03da }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x03da }
            if (r0 <= 0) goto L_0x03a1
            android.text.StaticLayout r0 = r1.nameLayout     // Catch:{ Exception -> 0x03da }
            float r0 = r0.getLineWidth(r5)     // Catch:{ Exception -> 0x03da }
            double r13 = (double) r0     // Catch:{ Exception -> 0x03da }
            double r13 = java.lang.Math.ceil(r13)     // Catch:{ Exception -> 0x03da }
            int r0 = (int) r13     // Catch:{ Exception -> 0x03da }
            r1.nameWidth = r0     // Catch:{ Exception -> 0x03da }
            boolean r0 = r40.isAnyKindOfSticker()     // Catch:{ Exception -> 0x03da }
            if (r0 != 0) goto L_0x0398
            int r0 = r1.namesOffset     // Catch:{ Exception -> 0x03da }
            r4 = 1100480512(0x41980000, float:19.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x03da }
            int r0 = r0 + r4
            r1.namesOffset = r0     // Catch:{ Exception -> 0x03da }
        L_0x0398:
            android.text.StaticLayout r0 = r1.nameLayout     // Catch:{ Exception -> 0x03da }
            float r0 = r0.getLineLeft(r5)     // Catch:{ Exception -> 0x03da }
            r1.nameOffsetX = r0     // Catch:{ Exception -> 0x03da }
            goto L_0x03a3
        L_0x03a1:
            r1.nameWidth = r5     // Catch:{ Exception -> 0x03da }
        L_0x03a3:
            if (r23 == 0) goto L_0x03d6
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x03da }
            android.text.TextPaint r24 = org.telegram.ui.ActionBar.Theme.chat_adminPaint     // Catch:{ Exception -> 0x03da }
            r4 = 1073741824(0x40000000, float:2.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x03da }
            int r25 = r3 + r7
            android.text.Layout$Alignment r26 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x03da }
            r27 = 1065353216(0x3var_, float:1.0)
            r28 = 0
            r29 = 0
            r22 = r0
            r22.<init>(r23, r24, r25, r26, r27, r28, r29)     // Catch:{ Exception -> 0x03da }
            r1.adminLayout = r0     // Catch:{ Exception -> 0x03da }
            int r0 = r1.nameWidth     // Catch:{ Exception -> 0x03da }
            float r0 = (float) r0     // Catch:{ Exception -> 0x03da }
            android.text.StaticLayout r3 = r1.adminLayout     // Catch:{ Exception -> 0x03da }
            float r3 = r3.getLineWidth(r5)     // Catch:{ Exception -> 0x03da }
            r4 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x03da }
            float r4 = (float) r7     // Catch:{ Exception -> 0x03da }
            float r3 = r3 + r4
            float r0 = r0 + r3
            int r0 = (int) r0     // Catch:{ Exception -> 0x03da }
            r1.nameWidth = r0     // Catch:{ Exception -> 0x03da }
            goto L_0x03de
        L_0x03d6:
            r3 = 0
            r1.adminLayout = r3     // Catch:{ Exception -> 0x03da }
            goto L_0x03de
        L_0x03da:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03de:
            java.lang.String r0 = r1.currentNameString
            int r0 = r0.length()
            r3 = 0
            if (r0 != 0) goto L_0x03e9
            r1.currentNameString = r3
        L_0x03e9:
            r1.currentForwardUser = r3
            r1.currentForwardNameString = r3
            r1.currentForwardChannel = r3
            r1.currentForwardName = r3
            android.text.StaticLayout[] r0 = r1.forwardedNameLayout
            r0[r5] = r3
            r4 = 1
            r0[r4] = r3
            r1.forwardedNameWidth = r5
            boolean r0 = r1.drawForwardedName
            if (r0 == 0) goto L_0x0632
            boolean r0 = r40.needDrawForwarded()
            if (r0 == 0) goto L_0x0632
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r1.currentPosition
            if (r0 == 0) goto L_0x040c
            byte r0 = r0.minY
            if (r0 != 0) goto L_0x0632
        L_0x040c:
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x042a
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from
            int r3 = r3.channel_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            r1.currentForwardChannel = r0
        L_0x042a:
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            int r0 = r0.from_id
            if (r0 == 0) goto L_0x0448
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from
            int r3 = r3.from_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            r1.currentForwardUser = r0
        L_0x0448:
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            java.lang.String r0 = r0.from_name
            if (r0 == 0) goto L_0x0452
            r1.currentForwardName = r0
        L_0x0452:
            org.telegram.tgnet.TLRPC$User r0 = r1.currentForwardUser
            if (r0 != 0) goto L_0x045e
            org.telegram.tgnet.TLRPC$Chat r0 = r1.currentForwardChannel
            if (r0 != 0) goto L_0x045e
            java.lang.String r0 = r1.currentForwardName
            if (r0 == 0) goto L_0x0632
        L_0x045e:
            org.telegram.tgnet.TLRPC$Chat r0 = r1.currentForwardChannel
            if (r0 == 0) goto L_0x0482
            org.telegram.tgnet.TLRPC$User r3 = r1.currentForwardUser
            if (r3 == 0) goto L_0x047d
            r4 = 2
            java.lang.Object[] r7 = new java.lang.Object[r4]
            java.lang.String r0 = r0.title
            r7[r5] = r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r3)
            r3 = 1
            r7[r3] = r0
            java.lang.String r0 = "%s (%s)"
            java.lang.String r0 = java.lang.String.format(r0, r7)
            r1.currentForwardNameString = r0
            goto L_0x0493
        L_0x047d:
            java.lang.String r0 = r0.title
            r1.currentForwardNameString = r0
            goto L_0x0493
        L_0x0482:
            org.telegram.tgnet.TLRPC$User r0 = r1.currentForwardUser
            if (r0 == 0) goto L_0x048d
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1.currentForwardNameString = r0
            goto L_0x0493
        L_0x048d:
            java.lang.String r0 = r1.currentForwardName
            if (r0 == 0) goto L_0x0493
            r1.currentForwardNameString = r0
        L_0x0493:
            int r0 = r39.getMaxNameWidth()
            r1.forwardedNameWidth = r0
            r0 = 2131625232(0x7f0e0510, float:1.8877666E38)
            java.lang.String r3 = "From"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r3 = 2131625240(0x7f0e0518, float:1.8877682E38)
            java.lang.String r4 = "FromFormatted"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r4 = "%1$s"
            int r4 = r3.indexOf(r4)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r0)
            java.lang.String r0 = " "
            r9.append(r0)
            java.lang.String r0 = r9.toString()
            float r0 = r7.measureText(r0)
            double r13 = (double) r0
            double r13 = java.lang.Math.ceil(r13)
            int r0 = (int) r13
            java.lang.String r7 = r1.currentForwardNameString
            r9 = 32
            r11 = 10
            java.lang.String r7 = r7.replace(r11, r9)
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint
            int r11 = r1.forwardedNameWidth
            int r11 = r11 - r0
            int r13 = r1.viaWidth
            int r11 = r11 - r13
            float r11 = (float) r11
            android.text.TextUtils$TruncateAt r13 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r7 = android.text.TextUtils.ellipsize(r7, r9, r11, r13)
            r9 = 1
            java.lang.Object[] r11 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x04f1 }
            r11[r5] = r7     // Catch:{ Exception -> 0x04f1 }
            java.lang.String r9 = java.lang.String.format(r3, r11)     // Catch:{ Exception -> 0x04f1 }
            goto L_0x04f5
        L_0x04f1:
            java.lang.String r9 = r7.toString()
        L_0x04f5:
            if (r10 == 0) goto L_0x0540
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r10 = 3
            java.lang.Object[] r10 = new java.lang.Object[r10]
            r10[r5] = r9
            r11 = 2131626921(0x7f0e0ba9, float:1.8881092E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)
            r11 = 1
            r10[r11] = r8
            r8 = 2
            r10[r8] = r6
            java.lang.String r8 = "%s %s %s"
            java.lang.String r8 = java.lang.String.format(r8, r10)
            r3.<init>(r8)
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint
            float r8 = r8.measureText(r9)
            double r8 = (double) r8
            double r8 = java.lang.Math.ceil(r8)
            int r8 = (int) r8
            r1.viaNameWidth = r8
            org.telegram.ui.Components.TypefaceSpan r8 = new org.telegram.ui.Components.TypefaceSpan
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r8.<init>(r9)
            int r9 = r3.length()
            int r6 = r6.length()
            int r9 = r9 - r6
            r6 = 1
            int r9 = r9 - r6
            int r10 = r3.length()
            r11 = 33
            r3.setSpan(r8, r9, r10, r11)
            goto L_0x054f
        L_0x0540:
            r6 = 1
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            java.lang.Object[] r9 = new java.lang.Object[r6]
            r9[r5] = r7
            java.lang.String r3 = java.lang.String.format(r3, r9)
            r8.<init>(r3)
            r3 = r8
        L_0x054f:
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint
            int r8 = r7.length()
            float r6 = r6.measureText(r7, r5, r8)
            double r8 = (double) r6
            double r8 = java.lang.Math.ceil(r8)
            int r6 = (int) r8
            r8 = 2
            int r6 = r6 / r8
            int r0 = r0 + r6
            r1.forwardNameCenterX = r0
            if (r4 < 0) goto L_0x0585
            java.lang.String r0 = r1.currentForwardName
            if (r0 == 0) goto L_0x0572
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            int r0 = r0.from_id
            if (r0 == 0) goto L_0x0585
        L_0x0572:
            org.telegram.ui.Components.TypefaceSpan r0 = new org.telegram.ui.Components.TypefaceSpan
            android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r0.<init>(r6)
            int r6 = r7.length()
            int r6 = r6 + r4
            r7 = 33
            r3.setSpan(r0, r4, r6, r7)
        L_0x0585:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint
            int r4 = r1.forwardedNameWidth
            float r4 = (float) r4
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r8 = android.text.TextUtils.ellipsize(r3, r0, r4, r6)
            android.text.StaticLayout[] r0 = r1.forwardedNameLayout     // Catch:{ Exception -> 0x062e }
            android.text.StaticLayout r3 = new android.text.StaticLayout     // Catch:{ Exception -> 0x062e }
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint     // Catch:{ Exception -> 0x062e }
            int r4 = r1.forwardedNameWidth     // Catch:{ Exception -> 0x062e }
            r6 = 1073741824(0x40000000, float:2.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x062e }
            int r10 = r4 + r7
            android.text.Layout$Alignment r11 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x062e }
            r12 = 1065353216(0x3var_, float:1.0)
            r13 = 0
            r14 = 0
            r7 = r3
            r7.<init>(r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x062e }
            r4 = 1
            r0[r4] = r3     // Catch:{ Exception -> 0x062e }
            java.lang.String r0 = "ForwardedMessage"
            r3 = 2131625188(0x7f0e04e4, float:1.8877577E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r3)     // Catch:{ Exception -> 0x062e }
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)     // Catch:{ Exception -> 0x062e }
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint     // Catch:{ Exception -> 0x062e }
            int r4 = r1.forwardedNameWidth     // Catch:{ Exception -> 0x062e }
            float r4 = (float) r4     // Catch:{ Exception -> 0x062e }
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x062e }
            java.lang.CharSequence r8 = android.text.TextUtils.ellipsize(r0, r3, r4, r6)     // Catch:{ Exception -> 0x062e }
            android.text.StaticLayout[] r0 = r1.forwardedNameLayout     // Catch:{ Exception -> 0x062e }
            android.text.StaticLayout r3 = new android.text.StaticLayout     // Catch:{ Exception -> 0x062e }
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint     // Catch:{ Exception -> 0x062e }
            int r4 = r1.forwardedNameWidth     // Catch:{ Exception -> 0x062e }
            r6 = 1073741824(0x40000000, float:2.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x062e }
            int r10 = r4 + r6
            android.text.Layout$Alignment r11 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x062e }
            r12 = 1065353216(0x3var_, float:1.0)
            r13 = 0
            r14 = 0
            r7 = r3
            r7.<init>(r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x062e }
            r0[r5] = r3     // Catch:{ Exception -> 0x062e }
            android.text.StaticLayout[] r0 = r1.forwardedNameLayout     // Catch:{ Exception -> 0x062e }
            r0 = r0[r5]     // Catch:{ Exception -> 0x062e }
            float r0 = r0.getLineWidth(r5)     // Catch:{ Exception -> 0x062e }
            double r3 = (double) r0     // Catch:{ Exception -> 0x062e }
            double r3 = java.lang.Math.ceil(r3)     // Catch:{ Exception -> 0x062e }
            int r0 = (int) r3     // Catch:{ Exception -> 0x062e }
            android.text.StaticLayout[] r3 = r1.forwardedNameLayout     // Catch:{ Exception -> 0x062e }
            r4 = 1
            r3 = r3[r4]     // Catch:{ Exception -> 0x062e }
            float r3 = r3.getLineWidth(r5)     // Catch:{ Exception -> 0x062e }
            double r3 = (double) r3     // Catch:{ Exception -> 0x062e }
            double r3 = java.lang.Math.ceil(r3)     // Catch:{ Exception -> 0x062e }
            int r3 = (int) r3     // Catch:{ Exception -> 0x062e }
            int r0 = java.lang.Math.max(r0, r3)     // Catch:{ Exception -> 0x062e }
            r1.forwardedNameWidth = r0     // Catch:{ Exception -> 0x062e }
            float[] r0 = r1.forwardNameOffsetX     // Catch:{ Exception -> 0x062e }
            android.text.StaticLayout[] r3 = r1.forwardedNameLayout     // Catch:{ Exception -> 0x062e }
            r3 = r3[r5]     // Catch:{ Exception -> 0x062e }
            float r3 = r3.getLineLeft(r5)     // Catch:{ Exception -> 0x062e }
            r0[r5] = r3     // Catch:{ Exception -> 0x062e }
            float[] r0 = r1.forwardNameOffsetX     // Catch:{ Exception -> 0x062e }
            android.text.StaticLayout[] r3 = r1.forwardedNameLayout     // Catch:{ Exception -> 0x062e }
            r4 = 1
            r3 = r3[r4]     // Catch:{ Exception -> 0x062e }
            float r3 = r3.getLineLeft(r5)     // Catch:{ Exception -> 0x062e }
            r0[r4] = r3     // Catch:{ Exception -> 0x062e }
            int r0 = r2.type     // Catch:{ Exception -> 0x062e }
            r3 = 5
            if (r0 == r3) goto L_0x0632
            int r0 = r1.namesOffset     // Catch:{ Exception -> 0x062e }
            r3 = 1108344832(0x42100000, float:36.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x062e }
            int r0 = r0 + r3
            r1.namesOffset = r0     // Catch:{ Exception -> 0x062e }
            goto L_0x0632
        L_0x062e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0632:
            boolean r0 = r40.hasValidReplyMessageObject()
            r3 = 1092616192(0x41200000, float:10.0)
            if (r0 == 0) goto L_0x0903
            org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r1.currentPosition
            if (r0 == 0) goto L_0x0642
            byte r0 = r0.minY
            if (r0 != 0) goto L_0x0998
        L_0x0642:
            boolean r0 = r40.isAnyKindOfSticker()
            if (r0 != 0) goto L_0x0667
            int r0 = r2.type
            r4 = 5
            if (r0 == r4) goto L_0x0667
            int r0 = r1.namesOffset
            r4 = 1109917696(0x42280000, float:42.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r4
            r1.namesOffset = r0
            int r0 = r2.type
            if (r0 == 0) goto L_0x0667
            int r0 = r1.namesOffset
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r4
            r1.namesOffset = r0
        L_0x0667:
            int r0 = r39.getMaxNameWidth()
            boolean r4 = r40.shouldDrawWithoutBackground()
            if (r4 != 0) goto L_0x0677
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r4
            goto L_0x0683
        L_0x0677:
            int r4 = r2.type
            r6 = 5
            if (r4 != r6) goto L_0x0683
            r4 = 1095761920(0x41500000, float:13.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r4
        L_0x0683:
            org.telegram.messenger.MessageObject r4 = r2.replyMessageObject
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.photoThumbs2
            r6 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6)
            org.telegram.messenger.MessageObject r6 = r2.replyMessageObject
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.photoThumbs2
            r7 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7)
            org.telegram.messenger.MessageObject r7 = r2.replyMessageObject
            org.telegram.tgnet.TLObject r8 = r7.photoThumbsObject2
            if (r4 != 0) goto L_0x06d3
            boolean r4 = r7.mediaExists
            if (r4 == 0) goto L_0x06b3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r7.photoThumbs
            int r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6)
            if (r4 == 0) goto L_0x06b0
            int r6 = r4.size
            goto L_0x06b1
        L_0x06b0:
            r6 = 0
        L_0x06b1:
            r7 = 0
            goto L_0x06bd
        L_0x06b3:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r7.photoThumbs
            r6 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6)
            r6 = 0
            r7 = 1
        L_0x06bd:
            org.telegram.messenger.MessageObject r8 = r2.replyMessageObject
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            r9 = 40
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            org.telegram.messenger.MessageObject r9 = r2.replyMessageObject
            org.telegram.tgnet.TLObject r9 = r9.photoThumbsObject
            r26 = r6
            r29 = r7
            r6 = r4
            r4 = r8
            r8 = r9
            goto L_0x06dc
        L_0x06d3:
            r26 = 0
            r29 = 1
            r38 = r6
            r6 = r4
            r4 = r38
        L_0x06dc:
            if (r4 != r6) goto L_0x06df
            r4 = 0
        L_0x06df:
            if (r6 == 0) goto L_0x073f
            org.telegram.messenger.MessageObject r7 = r2.replyMessageObject
            boolean r7 = r7.isAnyKindOfSticker()
            if (r7 != 0) goto L_0x073f
            boolean r7 = r40.isAnyKindOfSticker()
            if (r7 == 0) goto L_0x06f5
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r7 == 0) goto L_0x073f
        L_0x06f5:
            org.telegram.messenger.MessageObject r7 = r2.replyMessageObject
            boolean r7 = r7.isSecretMedia()
            if (r7 == 0) goto L_0x06fe
            goto L_0x073f
        L_0x06fe:
            org.telegram.messenger.MessageObject r7 = r2.replyMessageObject
            boolean r7 = r7.isRoundVideo()
            if (r7 == 0) goto L_0x0712
            org.telegram.messenger.ImageReceiver r7 = r1.replyImageReceiver
            r9 = 1102053376(0x41b00000, float:22.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r7.setRoundRadius(r9)
            goto L_0x0717
        L_0x0712:
            org.telegram.messenger.ImageReceiver r7 = r1.replyImageReceiver
            r7.setRoundRadius(r5)
        L_0x0717:
            r1.currentReplyPhoto = r6
            org.telegram.messenger.ImageReceiver r7 = r1.replyImageReceiver
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForObject(r6, r8)
            org.telegram.messenger.ImageLocation r24 = org.telegram.messenger.ImageLocation.getForObject(r4, r8)
            r27 = 0
            org.telegram.messenger.MessageObject r4 = r2.replyMessageObject
            java.lang.String r23 = "50_50"
            java.lang.String r25 = "50_50_b"
            r21 = r7
            r28 = r4
            r21.setImage(r22, r23, r24, r25, r26, r27, r28, r29)
            r4 = 1
            r1.needReplyImage = r4
            r4 = 1110441984(0x42300000, float:44.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r4 = r0
            r6 = 0
            goto L_0x0748
        L_0x073f:
            org.telegram.messenger.ImageReceiver r4 = r1.replyImageReceiver
            r6 = 0
            r4.setImageBitmap((android.graphics.drawable.Drawable) r6)
            r1.needReplyImage = r5
            r4 = r0
        L_0x0748:
            java.lang.String r0 = r2.customReplyName
            if (r0 == 0) goto L_0x074d
            goto L_0x07ae
        L_0x074d:
            org.telegram.messenger.MessageObject r0 = r2.replyMessageObject
            boolean r0 = r0.isFromUser()
            if (r0 == 0) goto L_0x0770
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.messenger.MessageObject r7 = r2.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            int r7 = r7.from_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r7)
            if (r0 == 0) goto L_0x07ad
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            goto L_0x07ae
        L_0x0770:
            org.telegram.messenger.MessageObject r0 = r2.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            int r0 = r0.from_id
            if (r0 >= 0) goto L_0x0792
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.messenger.MessageObject r7 = r2.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            int r7 = r7.from_id
            int r7 = -r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r7)
            if (r0 == 0) goto L_0x07ad
            java.lang.String r0 = r0.title
            goto L_0x07ae
        L_0x0792:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.messenger.MessageObject r7 = r2.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.to_id
            int r7 = r7.channel_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r7)
            if (r0 == 0) goto L_0x07ad
            java.lang.String r0 = r0.title
            goto L_0x07ae
        L_0x07ad:
            r0 = r6
        L_0x07ae:
            if (r0 != 0) goto L_0x07b9
            r0 = 2131625443(0x7f0e05e3, float:1.8878094E38)
            java.lang.String r7 = "Loading"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
        L_0x07b9:
            r7 = 32
            r8 = 10
            java.lang.String r0 = r0.replace(r8, r7)
            android.text.TextPaint r7 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint
            float r8 = (float) r4
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r17 = android.text.TextUtils.ellipsize(r0, r7, r8, r9)
            org.telegram.messenger.MessageObject r0 = r2.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            r10 = 1096810496(0x41600000, float:14.0)
            if (r9 == 0) goto L_0x07f2
            org.telegram.tgnet.TLRPC$TL_game r0 = r7.game
            java.lang.String r0 = r0.title
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r10)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r6, r5)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r2, r8, r6)
        L_0x07f0:
            r7 = r0
            goto L_0x084f
        L_0x07f2:
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r9 == 0) goto L_0x080f
            java.lang.String r0 = r7.title
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r10)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r6, r5)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r2, r8, r6)
            goto L_0x07f0
        L_0x080f:
            java.lang.CharSequence r0 = r0.messageText
            if (r0 == 0) goto L_0x084e
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x084e
            org.telegram.messenger.MessageObject r0 = r2.replyMessageObject
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            int r2 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r2 <= r6) goto L_0x082f
            r2 = 150(0x96, float:2.1E-43)
            java.lang.String r0 = r0.substring(r5, r2)
        L_0x082f:
            r2 = 32
            r6 = 10
            java.lang.String r0 = r0.replace(r6, r2)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r10)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r6, r5)
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r2, r8, r6)
            goto L_0x07f0
        L_0x084e:
            r7 = r6
        L_0x084f:
            r0 = 4
            boolean r2 = r1.needReplyImage     // Catch:{ Exception -> 0x08a6 }
            if (r2 == 0) goto L_0x0857
            r2 = 44
            goto L_0x0858
        L_0x0857:
            r2 = 0
        L_0x0858:
            int r0 = r0 + r2
            float r0 = (float) r0     // Catch:{ Exception -> 0x08a6 }
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x08a6 }
            r1.replyNameWidth = r0     // Catch:{ Exception -> 0x08a6 }
            if (r17 == 0) goto L_0x08aa
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x08a6 }
            android.text.TextPaint r18 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint     // Catch:{ Exception -> 0x08a6 }
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x08a6 }
            int r19 = r4 + r2
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x08a6 }
            r21 = 1065353216(0x3var_, float:1.0)
            r22 = 0
            r23 = 0
            r16 = r0
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)     // Catch:{ Exception -> 0x08a6 }
            r1.replyNameLayout = r0     // Catch:{ Exception -> 0x08a6 }
            android.text.StaticLayout r0 = r1.replyNameLayout     // Catch:{ Exception -> 0x08a6 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x08a6 }
            if (r0 <= 0) goto L_0x08aa
            int r0 = r1.replyNameWidth     // Catch:{ Exception -> 0x08a6 }
            android.text.StaticLayout r2 = r1.replyNameLayout     // Catch:{ Exception -> 0x08a6 }
            float r2 = r2.getLineWidth(r5)     // Catch:{ Exception -> 0x08a6 }
            double r8 = (double) r2     // Catch:{ Exception -> 0x08a6 }
            double r8 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x08a6 }
            int r2 = (int) r8     // Catch:{ Exception -> 0x08a6 }
            r6 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x08a6 }
            int r2 = r2 + r8
            int r0 = r0 + r2
            r1.replyNameWidth = r0     // Catch:{ Exception -> 0x08a6 }
            android.text.StaticLayout r0 = r1.replyNameLayout     // Catch:{ Exception -> 0x08a6 }
            float r0 = r0.getLineLeft(r5)     // Catch:{ Exception -> 0x08a6 }
            r1.replyNameOffset = r0     // Catch:{ Exception -> 0x08a6 }
            goto L_0x08aa
        L_0x08a6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x08aa:
            r0 = 4
            boolean r2 = r1.needReplyImage     // Catch:{ Exception -> 0x08fd }
            if (r2 == 0) goto L_0x08b2
            r2 = 44
            goto L_0x08b3
        L_0x08b2:
            r2 = 0
        L_0x08b3:
            int r0 = r0 + r2
            float r0 = (float) r0     // Catch:{ Exception -> 0x08fd }
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ Exception -> 0x08fd }
            r1.replyTextWidth = r0     // Catch:{ Exception -> 0x08fd }
            if (r7 == 0) goto L_0x0998
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x08fd }
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint     // Catch:{ Exception -> 0x08fd }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x08fd }
            int r9 = r4 + r2
            android.text.Layout$Alignment r10 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x08fd }
            r11 = 1065353216(0x3var_, float:1.0)
            r12 = 0
            r13 = 0
            r6 = r0
            r6.<init>(r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x08fd }
            r1.replyTextLayout = r0     // Catch:{ Exception -> 0x08fd }
            android.text.StaticLayout r0 = r1.replyTextLayout     // Catch:{ Exception -> 0x08fd }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x08fd }
            if (r0 <= 0) goto L_0x0998
            int r0 = r1.replyTextWidth     // Catch:{ Exception -> 0x08fd }
            android.text.StaticLayout r2 = r1.replyTextLayout     // Catch:{ Exception -> 0x08fd }
            float r2 = r2.getLineWidth(r5)     // Catch:{ Exception -> 0x08fd }
            double r2 = (double) r2     // Catch:{ Exception -> 0x08fd }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x08fd }
            int r2 = (int) r2     // Catch:{ Exception -> 0x08fd }
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x08fd }
            int r2 = r2 + r3
            int r0 = r0 + r2
            r1.replyTextWidth = r0     // Catch:{ Exception -> 0x08fd }
            android.text.StaticLayout r0 = r1.replyTextLayout     // Catch:{ Exception -> 0x08fd }
            float r0 = r0.getLineLeft(r5)     // Catch:{ Exception -> 0x08fd }
            r1.replyTextOffset = r0     // Catch:{ Exception -> 0x08fd }
            goto L_0x0998
        L_0x08fd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0998
        L_0x0903:
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            int r0 = r0.reply_to_msg_id
            if (r0 == 0) goto L_0x0998
            org.telegram.messenger.MessageObject r0 = r2.replyMessageObject
            if (r0 == 0) goto L_0x0913
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty
            if (r0 != 0) goto L_0x0998
        L_0x0913:
            boolean r0 = r40.isAnyKindOfSticker()
            if (r0 != 0) goto L_0x0938
            int r0 = r2.type
            r4 = 5
            if (r0 == r4) goto L_0x0938
            int r0 = r1.namesOffset
            r4 = 1109917696(0x42280000, float:42.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r4
            r1.namesOffset = r0
            int r0 = r2.type
            if (r0 == 0) goto L_0x0938
            int r0 = r1.namesOffset
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r4
            r1.namesOffset = r0
        L_0x0938:
            r1.needReplyImage = r5
            int r0 = r39.getMaxNameWidth()
            boolean r4 = r40.shouldDrawWithoutBackground()
            if (r4 != 0) goto L_0x094a
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r2
            goto L_0x0956
        L_0x094a:
            int r2 = r2.type
            r3 = 5
            if (r2 != r3) goto L_0x0956
            r2 = 1095761920(0x41500000, float:13.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
        L_0x0956:
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r8 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r9 = r0 + r3
            android.text.Layout$Alignment r10 = android.text.Layout.Alignment.ALIGN_NORMAL
            r11 = 1065353216(0x3var_, float:1.0)
            r12 = 0
            r13 = 0
            java.lang.String r7 = ""
            r6 = r2
            r6.<init>(r7, r8, r9, r10, r11, r12, r13)
            r1.replyNameLayout = r2
            android.text.StaticLayout r0 = r1.replyNameLayout
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0998
            int r0 = r1.replyNameWidth
            android.text.StaticLayout r2 = r1.replyNameLayout
            float r2 = r2.getLineWidth(r5)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            int r0 = r0 + r2
            r1.replyNameWidth = r0
            android.text.StaticLayout r0 = r1.replyNameLayout
            float r0 = r0.getLineLeft(r5)
            r1.replyNameOffset = r0
        L_0x0998:
            r39.requestLayout()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageObjectInternal(org.telegram.messenger.MessageObject):void");
    }

    public int getCaptionHeight() {
        return this.addedCaptionHeight;
    }

    public ImageReceiver getAvatarImage() {
        if (this.isAvatarVisible) {
            return this.avatarImage;
        }
        return null;
    }

    public float getCheckBoxTranslation() {
        return (float) this.checkBoxTranslation;
    }

    public void drawCheckBox(Canvas canvas) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.isSending() && !this.currentMessageObject.isSendError() && this.checkBox != null) {
            if (this.checkBoxVisible || this.checkBoxAnimationInProgress) {
                MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
                if (groupedMessagePosition != null) {
                    int i = groupedMessagePosition.flags;
                    if ((i & 8) == 0 || (i & 1) == 0) {
                        return;
                    }
                }
                canvas.save();
                canvas.translate(0.0f, (float) getTop());
                this.checkBox.draw(canvas);
                canvas.restore();
            }
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"WrongCall"})
    public void onDraw(Canvas canvas) {
        Drawable drawable;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        int i;
        int i2;
        int i3;
        int i4;
        Drawable drawable2;
        int i5;
        int i6;
        int i7;
        Canvas canvas2 = canvas;
        if (this.currentMessageObject != null) {
            if (this.wasLayout || this.animationRunning) {
                if (!this.wasLayout && this.animationRunning) {
                    onLayout(false, getLeft(), getTop(), getRight(), getBottom());
                }
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
                    Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkOut");
                    Theme.chat_msgGameTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
                    Theme.chat_msgGameTextPaint.linkColor = Theme.getColor("chat_messageLinkOut");
                    Theme.chat_replyTextPaint.linkColor = Theme.getColor("chat_messageLinkOut");
                } else {
                    Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
                    Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
                    Theme.chat_msgGameTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
                    Theme.chat_msgGameTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
                    Theme.chat_replyTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
                }
                if (this.documentAttach != null) {
                    int i8 = this.documentAttachType;
                    if (i8 == 3) {
                        if (this.currentMessageObject.isOutOwner()) {
                            this.seekBarWaveform.setColors(Theme.getColor("chat_outVoiceSeekbar"), Theme.getColor("chat_outVoiceSeekbarFill"), Theme.getColor("chat_outVoiceSeekbarSelected"));
                            this.seekBar.setColors(Theme.getColor("chat_outAudioSeekbar"), Theme.getColor("chat_outAudioCacheSeekbar"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarSelected"));
                        } else {
                            this.seekBarWaveform.setColors(Theme.getColor("chat_inVoiceSeekbar"), Theme.getColor("chat_inVoiceSeekbarFill"), Theme.getColor("chat_inVoiceSeekbarSelected"));
                            this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
                        }
                    } else if (i8 == 5) {
                        this.documentAttachType = 5;
                        if (this.currentMessageObject.isOutOwner()) {
                            this.seekBar.setColors(Theme.getColor("chat_outAudioSeekbar"), Theme.getColor("chat_outAudioCacheSeekbar"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarSelected"));
                        } else {
                            this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
                        }
                    }
                }
                MessageObject messageObject = this.currentMessageObject;
                if (messageObject.type == 5) {
                    Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
                } else if (this.mediaBackground) {
                    if (messageObject.shouldDrawWithoutBackground()) {
                        Theme.chat_timePaint.setColor(Theme.getColor("chat_serviceText"));
                    } else {
                        Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
                    }
                } else if (messageObject.isOutOwner()) {
                    Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText"));
                } else {
                    Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText"));
                }
                int i9 = 0;
                if (this.currentMessageObject.isOutOwner()) {
                    if (this.mediaBackground || this.drawPinnedBottom) {
                        this.currentBackgroundDrawable = Theme.chat_msgOutMediaDrawable;
                        this.currentBackgroundSelectedDrawable = Theme.chat_msgOutMediaSelectedDrawable;
                        drawable = Theme.chat_msgOutMediaShadowDrawable;
                    } else {
                        this.currentBackgroundDrawable = Theme.chat_msgOutDrawable;
                        this.currentBackgroundSelectedDrawable = Theme.chat_msgOutSelectedDrawable;
                        drawable = Theme.chat_msgOutShadowDrawable;
                    }
                    this.backgroundDrawableLeft = (this.layoutWidth - this.backgroundWidth) - (!this.mediaBackground ? 0 : AndroidUtilities.dp(9.0f));
                    this.backgroundDrawableRight = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
                    if (this.currentMessagesGroup != null && !this.currentPosition.edge) {
                        this.backgroundDrawableRight += AndroidUtilities.dp(10.0f);
                    }
                    int i10 = this.backgroundDrawableLeft;
                    if (!this.mediaBackground && this.drawPinnedBottom) {
                        this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                    }
                    MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
                    if (groupedMessagePosition2 != null) {
                        if ((groupedMessagePosition2.flags & 2) == 0) {
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 1) == 0) {
                            i10 -= AndroidUtilities.dp(8.0f);
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 4) == 0) {
                            i6 = 0 - AndroidUtilities.dp(9.0f);
                            i5 = AndroidUtilities.dp(9.0f) + 0;
                        } else {
                            i6 = 0;
                            i5 = 0;
                        }
                        if ((this.currentPosition.flags & 8) == 0) {
                            i5 += AndroidUtilities.dp(9.0f);
                        }
                    } else {
                        i6 = 0;
                        i5 = 0;
                    }
                    if (this.drawPinnedBottom && this.drawPinnedTop) {
                        i7 = 0;
                    } else if (this.drawPinnedBottom) {
                        i7 = AndroidUtilities.dp(1.0f);
                    } else {
                        i7 = AndroidUtilities.dp(2.0f);
                    }
                    boolean z = this.drawPinnedTop;
                    int dp = i6 + ((z || (z && this.drawPinnedBottom)) ? 0 : AndroidUtilities.dp(1.0f));
                    BaseCell.setDrawableBounds(this.currentBackgroundDrawable, i10, dp, this.backgroundDrawableRight, (this.layoutHeight - i7) + i5);
                    BaseCell.setDrawableBounds(this.currentBackgroundSelectedDrawable, i10, dp, this.backgroundDrawableRight, (this.layoutHeight - i7) + i5);
                    BaseCell.setDrawableBounds(drawable, i10, dp, this.backgroundDrawableRight, (this.layoutHeight - i7) + i5);
                } else {
                    if (this.mediaBackground || this.drawPinnedBottom) {
                        this.currentBackgroundDrawable = Theme.chat_msgInMediaDrawable;
                        this.currentBackgroundSelectedDrawable = Theme.chat_msgInMediaSelectedDrawable;
                        drawable2 = Theme.chat_msgInMediaShadowDrawable;
                    } else {
                        this.currentBackgroundDrawable = Theme.chat_msgInDrawable;
                        this.currentBackgroundSelectedDrawable = Theme.chat_msgInSelectedDrawable;
                        drawable2 = Theme.chat_msgInShadowDrawable;
                    }
                    this.backgroundDrawableLeft = AndroidUtilities.dp((float) (((!this.isChat || !this.isAvatarVisible) ? 0 : 48) + (!this.mediaBackground ? 3 : 9)));
                    this.backgroundDrawableRight = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
                    if (this.currentMessagesGroup != null) {
                        if (!this.currentPosition.edge) {
                            this.backgroundDrawableLeft -= AndroidUtilities.dp(10.0f);
                            this.backgroundDrawableRight += AndroidUtilities.dp(10.0f);
                        }
                        int i11 = this.currentPosition.leftSpanOffset;
                        if (i11 != 0) {
                            this.backgroundDrawableLeft += (int) Math.ceil((double) ((((float) i11) / 1000.0f) * ((float) getGroupPhotosWidth())));
                        }
                    }
                    if (!this.mediaBackground && this.drawPinnedBottom) {
                        this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                        this.backgroundDrawableLeft += AndroidUtilities.dp(6.0f);
                    }
                    MessageObject.GroupedMessagePosition groupedMessagePosition3 = this.currentPosition;
                    if (groupedMessagePosition3 != null) {
                        if ((groupedMessagePosition3.flags & 2) == 0) {
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 1) == 0) {
                            this.backgroundDrawableLeft -= AndroidUtilities.dp(8.0f);
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 4) == 0) {
                            i2 = 0 - AndroidUtilities.dp(9.0f);
                            i4 = AndroidUtilities.dp(9.0f) + 0;
                        } else {
                            i2 = 0;
                            i4 = 0;
                        }
                        i = (this.currentPosition.flags & 8) == 0 ? AndroidUtilities.dp(10.0f) + i4 : i4;
                    } else {
                        i2 = 0;
                        i = 0;
                    }
                    if (this.drawPinnedBottom && this.drawPinnedTop) {
                        i3 = 0;
                    } else if (this.drawPinnedBottom) {
                        i3 = AndroidUtilities.dp(1.0f);
                    } else {
                        i3 = AndroidUtilities.dp(2.0f);
                    }
                    boolean z2 = this.drawPinnedTop;
                    int dp2 = i2 + ((z2 || (z2 && this.drawPinnedBottom)) ? 0 : AndroidUtilities.dp(1.0f));
                    BaseCell.setDrawableBounds(this.currentBackgroundDrawable, this.backgroundDrawableLeft, dp2, this.backgroundDrawableRight, (this.layoutHeight - i3) + i);
                    BaseCell.setDrawableBounds(this.currentBackgroundSelectedDrawable, this.backgroundDrawableLeft, dp2, this.backgroundDrawableRight, (this.layoutHeight - i3) + i);
                    BaseCell.setDrawableBounds(drawable, this.backgroundDrawableLeft, dp2, this.backgroundDrawableRight, (this.layoutHeight - i3) + i);
                }
                if (this.checkBoxVisible || this.checkBoxAnimationInProgress) {
                    if ((this.checkBoxVisible && this.checkBoxAnimationProgress == 1.0f) || (!this.checkBoxVisible && this.checkBoxAnimationProgress == 0.0f)) {
                        this.checkBoxAnimationInProgress = false;
                    }
                    this.checkBoxTranslation = (int) Math.ceil((double) ((this.checkBoxVisible ? CubicBezierInterpolator.EASE_OUT : CubicBezierInterpolator.EASE_IN).getInterpolation(this.checkBoxAnimationProgress) * ((float) AndroidUtilities.dp(35.0f))));
                    if (!this.currentMessageObject.isOutOwner()) {
                        setTranslationX((float) this.checkBoxTranslation);
                    }
                    int dp3 = AndroidUtilities.dp(21.0f);
                    this.checkBox.setBounds(AndroidUtilities.dp(-27.0f) + this.checkBoxTranslation, (this.currentBackgroundDrawable.getBounds().bottom - AndroidUtilities.dp(8.0f)) - dp3, dp3, dp3);
                    if (this.checkBoxAnimationInProgress) {
                        long uptimeMillis = SystemClock.uptimeMillis();
                        long j = uptimeMillis - this.lastCheckBoxAnimationTime;
                        this.lastCheckBoxAnimationTime = uptimeMillis;
                        if (this.checkBoxVisible) {
                            this.checkBoxAnimationProgress += ((float) j) / 200.0f;
                            if (this.checkBoxAnimationProgress > 1.0f) {
                                this.checkBoxAnimationProgress = 1.0f;
                            }
                            invalidate();
                            ((View) getParent()).invalidate();
                        } else {
                            this.checkBoxAnimationProgress -= ((float) j) / 200.0f;
                            if (this.checkBoxAnimationProgress <= 0.0f) {
                                this.checkBoxAnimationProgress = 0.0f;
                            }
                            invalidate();
                            ((View) getParent()).invalidate();
                        }
                    }
                }
                if (this.drawBackground && this.currentBackgroundDrawable != null) {
                    int i12 = 0;
                    while (i12 < 2) {
                        Drawable drawable3 = i12 == 0 ? this.currentBackgroundDrawable : this.currentBackgroundSelectedDrawable;
                        if (drawable3 instanceof Theme.MessageDrawable) {
                            Theme.MessageDrawable messageDrawable = (Theme.MessageDrawable) drawable3;
                            if (this.parentHeight == 0) {
                                this.parentHeight = AndroidUtilities.displaySize.y;
                                if (getParent() instanceof View) {
                                    this.parentHeight = ((View) getParent()).getMeasuredHeight();
                                }
                            }
                            messageDrawable.setTop(getTop(), this.parentHeight);
                        }
                        i12++;
                    }
                    if (this.isHighlightedAnimated) {
                        this.currentBackgroundDrawable.draw(canvas2);
                        int i13 = this.highlightProgress;
                        float f = i13 >= 300 ? 1.0f : ((float) i13) / 300.0f;
                        if (this.currentPosition == null) {
                            this.currentBackgroundSelectedDrawable.setAlpha((int) (f * 255.0f));
                            this.currentBackgroundSelectedDrawable.draw(canvas2);
                        }
                    } else if (this.selectedBackgroundProgress != 0.0f) {
                        this.currentBackgroundDrawable.draw(canvas2);
                        this.currentBackgroundSelectedDrawable.setAlpha((int) (this.selectedBackgroundProgress * 255.0f));
                        this.currentBackgroundSelectedDrawable.draw(canvas2);
                    } else if (!isDrawSelectionBackground() || (this.currentPosition != null && getBackground() == null)) {
                        this.currentBackgroundDrawable.draw(canvas2);
                    } else {
                        this.currentBackgroundSelectedDrawable.setAlpha(255);
                        this.currentBackgroundSelectedDrawable.draw(canvas2);
                    }
                    MessageObject.GroupedMessagePosition groupedMessagePosition4 = this.currentPosition;
                    if (groupedMessagePosition4 == null || groupedMessagePosition4.flags != 0) {
                        drawable.draw(canvas2);
                    }
                }
                if (this.isHighlightedAnimated) {
                    long currentTimeMillis = System.currentTimeMillis();
                    long abs = Math.abs(currentTimeMillis - this.lastHighlightProgressTime);
                    if (abs > 17) {
                        abs = 17;
                    }
                    this.highlightProgress = (int) (((long) this.highlightProgress) - abs);
                    this.lastHighlightProgressTime = currentTimeMillis;
                    if (this.highlightProgress <= 0) {
                        this.highlightProgress = 0;
                        this.isHighlightedAnimated = false;
                    }
                    invalidate();
                    if (getParent() != null) {
                        ((View) getParent()).invalidate();
                    }
                }
                drawContent(canvas);
                float f2 = 12.0f;
                if (this.drawShareButton) {
                    if (this.sharePressed) {
                        if (!Theme.isCustomTheme() || Theme.hasThemeKey("chat_shareBackgroundSelected")) {
                            Theme.chat_shareDrawable.setColorFilter(Theme.getShareColorFilter(Theme.getColor("chat_shareBackgroundSelected"), true));
                        } else {
                            Theme.chat_shareDrawable.setColorFilter(Theme.colorPressedFilter2);
                        }
                    } else if (!Theme.isCustomTheme() || Theme.hasThemeKey("chat_shareBackground")) {
                        Theme.chat_shareDrawable.setColorFilter(Theme.getShareColorFilter(Theme.getColor("chat_shareBackground"), false));
                    } else {
                        Theme.chat_shareDrawable.setColorFilter(Theme.colorFilter2);
                    }
                    if (this.currentMessageObject.isOutOwner()) {
                        this.shareStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0f)) - Theme.chat_shareDrawable.getIntrinsicWidth();
                    } else {
                        this.shareStartX = this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0f);
                    }
                    Drawable drawable4 = Theme.chat_shareDrawable;
                    int i14 = this.shareStartX;
                    int dp4 = this.layoutHeight - AndroidUtilities.dp(41.0f);
                    this.shareStartY = dp4;
                    BaseCell.setDrawableBounds(drawable4, i14, dp4);
                    Theme.chat_shareDrawable.draw(canvas2);
                    if (this.drwaShareGoIcon) {
                        BaseCell.setDrawableBounds(Theme.chat_goIconDrawable, this.shareStartX + AndroidUtilities.dp(12.0f), this.shareStartY + AndroidUtilities.dp(9.0f));
                        Theme.chat_goIconDrawable.draw(canvas2);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_shareIconDrawable, this.shareStartX + AndroidUtilities.dp(8.0f), this.shareStartY + AndroidUtilities.dp(9.0f));
                        Theme.chat_shareIconDrawable.draw(canvas2);
                    }
                }
                if (this.replyNameLayout != null) {
                    if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                        if (this.currentMessageObject.isOutOwner()) {
                            this.replyStartX = AndroidUtilities.dp(23.0f);
                        } else if (this.currentMessageObject.type == 5) {
                            this.replyStartX = this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(4.0f);
                        } else {
                            this.replyStartX = this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(17.0f);
                        }
                        this.replyStartY = AndroidUtilities.dp(12.0f);
                    } else {
                        if (this.currentMessageObject.isOutOwner()) {
                            this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f);
                        } else {
                            boolean z3 = this.mediaBackground;
                            if (z3) {
                                this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f);
                            } else {
                                int i15 = this.backgroundDrawableLeft;
                                if (z3 || !this.drawPinnedBottom) {
                                    f2 = 18.0f;
                                }
                                this.replyStartX = i15 + AndroidUtilities.dp(f2);
                            }
                        }
                        int i16 = 12 + ((!this.drawForwardedName || this.forwardedNameLayout[0] == null) ? 0 : 36);
                        if (this.drawNameLayout && this.nameLayout != null) {
                            i9 = 20;
                        }
                        this.replyStartY = AndroidUtilities.dp((float) (i16 + i9));
                    }
                }
                if (this.currentPosition == null) {
                    drawNamesLayout(canvas);
                }
                if (!this.autoPlayingMedia || !MediaController.getInstance().isPlayingMessageAndReadyToDraw(this.currentMessageObject)) {
                    drawOverlays(canvas);
                }
                if ((this.drawTime || !this.mediaBackground) && !this.forceNotDrawTime) {
                    drawTime(canvas);
                }
                if (!((this.controlsAlpha == 1.0f && this.timeAlpha == 1.0f) || this.currentMessageObject.type == 5)) {
                    long currentTimeMillis2 = System.currentTimeMillis();
                    long abs2 = Math.abs(this.lastControlsAlphaChangeTime - currentTimeMillis2);
                    if (abs2 > 17) {
                        abs2 = 17;
                    }
                    this.totalChangeTime += abs2;
                    if (this.totalChangeTime > 100) {
                        this.totalChangeTime = 100;
                    }
                    this.lastControlsAlphaChangeTime = currentTimeMillis2;
                    if (this.controlsAlpha != 1.0f) {
                        this.controlsAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) this.totalChangeTime) / 100.0f);
                    }
                    if (this.timeAlpha != 1.0f) {
                        this.timeAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) this.totalChangeTime) / 100.0f);
                    }
                    invalidate();
                    if (this.forceNotDrawTime && (groupedMessagePosition = this.currentPosition) != null && groupedMessagePosition.last && getParent() != null) {
                        ((View) getParent()).invalidate();
                    }
                }
                updateSelectionTextPosition();
                return;
            }
            requestLayout();
        }
    }

    public void setTimeAlpha(float f) {
        this.timeAlpha = f;
    }

    public float getTimeAlpha() {
        return this.timeAlpha;
    }

    public int getBackgroundDrawableLeft() {
        int i = 0;
        if (this.currentMessageObject.isOutOwner()) {
            int i2 = this.layoutWidth - this.backgroundWidth;
            if (this.mediaBackground) {
                i = AndroidUtilities.dp(9.0f);
            }
            return i2 - i;
        }
        if (this.isChat && this.isAvatarVisible) {
            i = 48;
        }
        return AndroidUtilities.dp((float) (i + (!this.mediaBackground ? 3 : 9)));
    }

    public boolean hasNameLayout() {
        if (this.drawNameLayout && this.nameLayout != null) {
            return true;
        }
        if (this.drawForwardedName) {
            StaticLayout[] staticLayoutArr = this.forwardedNameLayout;
            if (!(staticLayoutArr[0] == null || staticLayoutArr[1] == null)) {
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

    public boolean isDrawNameLayout() {
        return this.drawNameLayout && this.nameLayout != null;
    }

    public void drawNamesLayout(Canvas canvas) {
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        float f = 17.0f;
        int i = 0;
        if (this.drawNameLayout && this.nameLayout != null) {
            canvas.save();
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                Theme.chat_namePaint.setColor(Theme.getColor("chat_stickerNameText"));
                if (this.currentMessageObject.isOutOwner()) {
                    this.nameX = (float) AndroidUtilities.dp(28.0f);
                } else {
                    this.nameX = (float) (this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(22.0f));
                }
                this.nameY = (float) (this.layoutHeight - AndroidUtilities.dp(38.0f));
                float f2 = (!this.currentMessageObject.isOut() || (!this.checkBoxVisible && !this.checkBoxAnimationInProgress)) ? 1.0f : 1.0f - this.checkBoxAnimationProgress;
                Theme.chat_systemDrawable.setAlpha((int) (255.0f * f2));
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(((int) this.nameX) - AndroidUtilities.dp(12.0f), ((int) this.nameY) - AndroidUtilities.dp(5.0f), ((int) this.nameX) + AndroidUtilities.dp(12.0f) + this.nameWidth, ((int) this.nameY) + AndroidUtilities.dp(22.0f));
                Theme.chat_systemDrawable.draw(canvas);
                if (this.checkBoxVisible || this.checkBoxAnimationInProgress) {
                    Theme.chat_systemDrawable.setAlpha(255);
                }
                this.nameX -= this.nameOffsetX;
                int alpha = (((int) (((float) Color.alpha(Theme.getColor("chat_stickerViaBotNameText"))) * f2)) << 24) | (Theme.getColor("chat_stickerViaBotNameText") & 16777215);
                TypefaceSpan typefaceSpan = this.viaSpan1;
                if (typefaceSpan != null) {
                    typefaceSpan.setColor(alpha);
                }
                TypefaceSpan typefaceSpan2 = this.viaSpan2;
                if (typefaceSpan2 != null) {
                    typefaceSpan2.setColor(alpha);
                }
                Theme.chat_systemDrawable.setAlpha(255);
            } else {
                if (this.mediaBackground || this.currentMessageObject.isOutOwner()) {
                    this.nameX = ((float) (this.backgroundDrawableLeft + AndroidUtilities.dp(11.0f))) - this.nameOffsetX;
                } else {
                    this.nameX = ((float) (this.backgroundDrawableLeft + AndroidUtilities.dp((this.mediaBackground || !this.drawPinnedBottom) ? 17.0f : 11.0f))) - this.nameOffsetX;
                }
                TLRPC.User user = this.currentUser;
                if (user != null) {
                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(user.id));
                } else {
                    TLRPC.Chat chat = this.currentChat;
                    if (chat == null) {
                        Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(0));
                    } else if (!ChatObject.isChannel(chat) || this.currentChat.megagroup) {
                        Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentChat.id));
                    } else {
                        Theme.chat_namePaint.setColor(Theme.changeColorAccent(AvatarDrawable.getNameColorForId(5)));
                    }
                }
                this.nameY = (float) AndroidUtilities.dp(this.drawPinnedTop ? 9.0f : 10.0f);
            }
            canvas.translate(this.nameX, this.nameY);
            this.nameLayout.draw(canvas);
            canvas.restore();
            if (this.adminLayout != null) {
                Theme.chat_adminPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_adminSelectedText" : "chat_adminText"));
                canvas.save();
                canvas.translate(((float) ((this.backgroundDrawableLeft + this.backgroundDrawableRight) - AndroidUtilities.dp(11.0f))) - this.adminLayout.getLineWidth(0), this.nameY + ((float) AndroidUtilities.dp(0.5f)));
                this.adminLayout.draw(canvas);
                canvas.restore();
            }
        }
        if (this.drawForwardedName) {
            StaticLayout[] staticLayoutArr = this.forwardedNameLayout;
            if (!(staticLayoutArr[0] == null || staticLayoutArr[1] == null || ((groupedMessagePosition = this.currentPosition) != null && (groupedMessagePosition.minY != 0 || groupedMessagePosition.minX != 0)))) {
                if (this.currentMessageObject.type == 5) {
                    Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_stickerReplyNameText"));
                    if (this.currentMessageObject.isOutOwner()) {
                        this.forwardNameX = AndroidUtilities.dp(23.0f);
                    } else {
                        this.forwardNameX = this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(17.0f);
                    }
                    this.forwardNameY = AndroidUtilities.dp(12.0f);
                    int dp = this.forwardedNameWidth + AndroidUtilities.dp(14.0f);
                    Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                    Theme.chat_systemDrawable.setBounds(this.forwardNameX - AndroidUtilities.dp(7.0f), this.forwardNameY - AndroidUtilities.dp(6.0f), (this.forwardNameX - AndroidUtilities.dp(7.0f)) + dp, this.forwardNameY + AndroidUtilities.dp(38.0f));
                    Theme.chat_systemDrawable.draw(canvas);
                } else {
                    this.forwardNameY = AndroidUtilities.dp((float) ((this.drawNameLayout ? 19 : 0) + 10));
                    if (this.currentMessageObject.isOutOwner()) {
                        Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_outForwardedNameText"));
                        this.forwardNameX = this.backgroundDrawableLeft + AndroidUtilities.dp(11.0f);
                    } else {
                        Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_inForwardedNameText"));
                        boolean z = this.mediaBackground;
                        if (z) {
                            this.forwardNameX = this.backgroundDrawableLeft + AndroidUtilities.dp(11.0f);
                        } else {
                            int i2 = this.backgroundDrawableLeft;
                            if (!z && this.drawPinnedBottom) {
                                f = 11.0f;
                            }
                            this.forwardNameX = i2 + AndroidUtilities.dp(f);
                        }
                    }
                }
                for (int i3 = 0; i3 < 2; i3++) {
                    canvas.save();
                    canvas.translate(((float) this.forwardNameX) - this.forwardNameOffsetX[i3], (float) (this.forwardNameY + (AndroidUtilities.dp(16.0f) * i3)));
                    this.forwardedNameLayout[i3].draw(canvas);
                    canvas.restore();
                }
            }
        }
        if (this.replyNameLayout != null) {
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_stickerReplyLine"));
                Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_stickerReplyNameText"));
                Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_stickerReplyMessageText"));
                int max = Math.max(this.replyNameWidth, this.replyTextWidth) + AndroidUtilities.dp(14.0f);
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(this.replyStartX - AndroidUtilities.dp(7.0f), this.replyStartY - AndroidUtilities.dp(6.0f), (this.replyStartX - AndroidUtilities.dp(7.0f)) + max, this.replyStartY + AndroidUtilities.dp(41.0f));
                Theme.chat_systemDrawable.draw(canvas);
            } else if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_outReplyLine"));
                Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_outReplyNameText"));
                if (this.currentMessageObject.hasValidReplyMessageObject()) {
                    MessageObject messageObject = this.currentMessageObject.replyMessageObject;
                    if (messageObject.type == 0) {
                        TLRPC.MessageMedia messageMedia = messageObject.messageOwner.media;
                        if (!(messageMedia instanceof TLRPC.TL_messageMediaGame) && !(messageMedia instanceof TLRPC.TL_messageMediaInvoice)) {
                            Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_outReplyMessageText"));
                        }
                    }
                }
                Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outReplyMediaMessageSelectedText" : "chat_outReplyMediaMessageText"));
            } else {
                Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_inReplyLine"));
                Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_inReplyNameText"));
                if (this.currentMessageObject.hasValidReplyMessageObject()) {
                    MessageObject messageObject2 = this.currentMessageObject.replyMessageObject;
                    if (messageObject2.type == 0) {
                        TLRPC.MessageMedia messageMedia2 = messageObject2.messageOwner.media;
                        if (!(messageMedia2 instanceof TLRPC.TL_messageMediaGame) && !(messageMedia2 instanceof TLRPC.TL_messageMediaInvoice)) {
                            Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_inReplyMessageText"));
                        }
                    }
                }
                Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inReplyMediaMessageSelectedText" : "chat_inReplyMediaMessageText"));
            }
            MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
            if (groupedMessagePosition2 == null || (groupedMessagePosition2.minY == 0 && groupedMessagePosition2.minX == 0)) {
                int i4 = this.replyStartX;
                canvas.drawRect((float) i4, (float) this.replyStartY, (float) (i4 + AndroidUtilities.dp(2.0f)), (float) (this.replyStartY + AndroidUtilities.dp(35.0f)), Theme.chat_replyLinePaint);
                if (this.needReplyImage) {
                    this.replyImageReceiver.setImageCoords(this.replyStartX + AndroidUtilities.dp(10.0f), this.replyStartY, AndroidUtilities.dp(35.0f), AndroidUtilities.dp(35.0f));
                    this.replyImageReceiver.draw(canvas);
                }
                if (this.replyNameLayout != null) {
                    canvas.save();
                    canvas.translate((((float) this.replyStartX) - this.replyNameOffset) + ((float) AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : 0) + 10))), (float) this.replyStartY);
                    this.replyNameLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.replyTextLayout != null) {
                    canvas.save();
                    float f3 = ((float) this.replyStartX) - this.replyTextOffset;
                    if (this.needReplyImage) {
                        i = 44;
                    }
                    canvas.translate(f3 + ((float) AndroidUtilities.dp((float) (i + 10))), (float) (this.replyStartY + AndroidUtilities.dp(19.0f)));
                    this.replyTextLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    public boolean hasCaptionLayout() {
        return this.captionLayout != null;
    }

    public StaticLayout getCaptionLayout() {
        return this.captionLayout;
    }

    public void setDrawSelectionBackground(boolean z) {
        this.drawSelectionBackground = z;
        invalidate();
    }

    public boolean isDrawingSelectionBackground() {
        return this.drawSelectionBackground || this.isHighlightedAnimated || this.isHighlighted;
    }

    public float getHightlightAlpha() {
        int i;
        if (this.drawSelectionBackground || !this.isHighlightedAnimated || (i = this.highlightProgress) >= 300) {
            return 1.0f;
        }
        return ((float) i) / 300.0f;
    }

    public void setCheckBoxVisible(boolean z, boolean z2) {
        MessageObject.GroupedMessages groupedMessages;
        if (z && this.checkBox == null) {
            this.checkBox = new CheckBoxBase(this, 21);
            if (this.attachedToWindow) {
                this.checkBox.onAttachedToWindow();
            }
        }
        if (z && this.photoCheckBox == null && (groupedMessages = this.currentMessagesGroup) != null && groupedMessages.messages.size() > 1) {
            this.photoCheckBox = new CheckBoxBase(this, 21);
            this.photoCheckBox.setUseDefaultCheck(true);
            if (this.attachedToWindow) {
                this.photoCheckBox.onAttachedToWindow();
            }
        }
        float f = 1.0f;
        if (this.checkBoxVisible != z) {
            this.checkBoxAnimationInProgress = z2;
            this.checkBoxVisible = z;
            if (z2) {
                this.lastCheckBoxAnimationTime = SystemClock.uptimeMillis();
            } else {
                if (!z) {
                    f = 0.0f;
                }
                this.checkBoxAnimationProgress = f;
            }
            invalidate();
        } else if (z2 != this.checkBoxAnimationInProgress && !z2) {
            if (!z) {
                f = 0.0f;
            }
            this.checkBoxAnimationProgress = f;
            invalidate();
        }
    }

    public void setChecked(boolean z, boolean z2, boolean z3) {
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.setChecked(z2, z3);
        }
        CheckBoxBase checkBoxBase2 = this.photoCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.setChecked(z, z3);
        }
        this.backgroundDrawable.setSelected(z2, z3);
    }

    public void setLastTouchCoords(float f, float f2) {
        this.lastTouchX = f;
        this.lastTouchY = f2;
        this.backgroundDrawable.setTouchCoords(this.lastTouchX + getTranslationX(), this.lastTouchY);
    }

    public MessageBackgroundDrawable getBackgroundDrawable() {
        return this.backgroundDrawable;
    }

    public void drawCaptionLayout(Canvas canvas, boolean z) {
        if (this.captionLayout == null) {
            return;
        }
        if (!z || this.pressedLink != null) {
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
                Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkOut");
            } else {
                Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
                Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
            }
            canvas.save();
            canvas.translate((float) this.captionX, (float) this.captionY);
            if (this.pressedLink != null) {
                for (int i = 0; i < this.urlPath.size(); i++) {
                    canvas.drawPath(this.urlPath.get(i), Theme.chat_urlPaint);
                }
            }
            if (!this.urlPathSelection.isEmpty()) {
                for (int i2 = 0; i2 < this.urlPathSelection.size(); i2++) {
                    canvas.drawPath(this.urlPathSelection.get(i2), Theme.chat_textSearchSelectionPaint);
                }
            }
            if (!z) {
                try {
                    if (getDelegate().getTextSelectionHelper() != null && getDelegate().getTextSelectionHelper().isSelected(this.currentMessageObject)) {
                        getDelegate().getTextSelectionHelper().drawCaption(this.currentMessageObject.isOutOwner(), this.captionLayout, canvas);
                    }
                    this.captionLayout.draw(canvas);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            canvas.restore();
        }
    }

    public boolean needDrawTime() {
        return !this.forceNotDrawTime;
    }

    public void drawTime(Canvas canvas) {
        boolean z;
        boolean z2;
        int i;
        int i2;
        int i3;
        Drawable drawable;
        Paint paint;
        Drawable drawable2;
        Canvas canvas2 = canvas;
        if (((this.drawTime && !this.groupPhotoInvisible) || !this.mediaBackground || this.captionLayout != null) && this.timeLayout != null) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject.type == 5) {
                Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
            } else if (!this.mediaBackground || this.captionLayout != null) {
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText"));
                } else {
                    Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText"));
                }
            } else if (messageObject.shouldDrawWithoutBackground()) {
                Theme.chat_timePaint.setColor(Theme.getColor("chat_serviceText"));
            } else {
                Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
            }
            if (this.drawPinnedBottom) {
                canvas2.translate(0.0f, (float) AndroidUtilities.dp(2.0f));
            }
            boolean z3 = false;
            if (!this.mediaBackground || this.captionLayout != null) {
                int i4 = (int) (-this.timeLayout.getLineLeft(0));
                if ((ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) || (this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                    i4 += (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(0));
                    if (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing()) {
                        if (!this.currentMessageObject.isOutOwner()) {
                            Drawable drawable3 = isDrawSelectionBackground() ? Theme.chat_msgInSelectedClockDrawable : Theme.chat_msgInClockDrawable;
                            BaseCell.setDrawableBounds(drawable3, this.timeX + (this.currentMessageObject.scheduled ? 0 : AndroidUtilities.dp(11.0f)), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - drawable3.getIntrinsicHeight());
                            drawable3.draw(canvas2);
                        }
                    } else if (this.currentMessageObject.isSendError()) {
                        if (!this.currentMessageObject.isOutOwner()) {
                            int dp = this.timeX + (this.currentMessageObject.scheduled ? 0 : AndroidUtilities.dp(11.0f));
                            int dp2 = this.layoutHeight - AndroidUtilities.dp(20.5f);
                            this.rect.set((float) dp, (float) dp2, (float) (AndroidUtilities.dp(14.0f) + dp), (float) (AndroidUtilities.dp(14.0f) + dp2));
                            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                            BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, dp + AndroidUtilities.dp(6.0f), dp2 + AndroidUtilities.dp(2.0f));
                            Theme.chat_msgErrorDrawable.draw(canvas2);
                        }
                    } else if (this.viewsLayout != null) {
                        if (!this.currentMessageObject.isOutOwner()) {
                            Drawable drawable4 = isDrawSelectionBackground() ? Theme.chat_msgInViewsSelectedDrawable : Theme.chat_msgInViewsDrawable;
                            BaseCell.setDrawableBounds(drawable4, this.timeX, (this.layoutHeight - AndroidUtilities.dp(4.5f)) - this.timeLayout.getHeight());
                            drawable4.draw(canvas2);
                        } else {
                            Drawable drawable5 = isDrawSelectionBackground() ? Theme.chat_msgOutViewsSelectedDrawable : Theme.chat_msgOutViewsDrawable;
                            BaseCell.setDrawableBounds(drawable5, this.timeX, (this.layoutHeight - AndroidUtilities.dp(4.5f)) - this.timeLayout.getHeight());
                            drawable5.draw(canvas2);
                        }
                        canvas.save();
                        canvas2.translate((float) (this.timeX + Theme.chat_msgInViewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.dp(6.5f)) - this.timeLayout.getHeight()));
                        this.viewsLayout.draw(canvas2);
                        canvas.restore();
                    }
                }
                canvas.save();
                canvas2.translate((float) (this.timeX + i4), (float) ((this.layoutHeight - AndroidUtilities.dp(6.5f)) - this.timeLayout.getHeight()));
                this.timeLayout.draw(canvas2);
                canvas.restore();
            } else {
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    paint = Theme.chat_actionBackgroundPaint;
                } else {
                    paint = Theme.chat_timeBackgroundPaint;
                }
                int alpha = paint.getAlpha();
                paint.setAlpha((int) (((float) alpha) * this.timeAlpha));
                Theme.chat_timePaint.setAlpha((int) (this.timeAlpha * 255.0f));
                int dp3 = this.timeX - AndroidUtilities.dp(4.0f);
                int dp4 = this.layoutHeight - AndroidUtilities.dp(28.0f);
                this.rect.set((float) dp3, (float) dp4, (float) (dp3 + this.timeWidth + AndroidUtilities.dp((float) ((this.currentMessageObject.isOutOwner() ? 20 : 0) + 8))), (float) (dp4 + AndroidUtilities.dp(17.0f)));
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint);
                paint.setAlpha(alpha);
                int i5 = (int) (-this.timeLayout.getLineLeft(0));
                if ((ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) || (this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                    i5 += (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(0));
                    if (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing()) {
                        if (!this.currentMessageObject.isOutOwner()) {
                            BaseCell.setDrawableBounds(Theme.chat_msgMediaClockDrawable, this.timeX + (this.currentMessageObject.scheduled ? 0 : AndroidUtilities.dp(11.0f)), (this.layoutHeight - AndroidUtilities.dp(14.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                            Theme.chat_msgMediaClockDrawable.draw(canvas2);
                        }
                    } else if (this.currentMessageObject.isSendError()) {
                        if (!this.currentMessageObject.isOutOwner()) {
                            int dp5 = this.timeX + (this.currentMessageObject.scheduled ? 0 : AndroidUtilities.dp(11.0f));
                            int dp6 = this.layoutHeight - AndroidUtilities.dp(26.5f);
                            this.rect.set((float) dp5, (float) dp6, (float) (AndroidUtilities.dp(14.0f) + dp5), (float) (AndroidUtilities.dp(14.0f) + dp6));
                            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                            BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, dp5 + AndroidUtilities.dp(6.0f), dp6 + AndroidUtilities.dp(2.0f));
                            Theme.chat_msgErrorDrawable.draw(canvas2);
                        }
                    } else if (this.viewsLayout != null) {
                        if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                            drawable2 = Theme.chat_msgStickerViewsDrawable;
                        } else {
                            drawable2 = Theme.chat_msgMediaViewsDrawable;
                        }
                        int alpha2 = ((BitmapDrawable) drawable2).getPaint().getAlpha();
                        drawable2.setAlpha((int) (this.timeAlpha * ((float) alpha2)));
                        BaseCell.setDrawableBounds(drawable2, this.timeX, (this.layoutHeight - AndroidUtilities.dp(10.5f)) - this.timeLayout.getHeight());
                        drawable2.draw(canvas2);
                        drawable2.setAlpha(alpha2);
                        canvas.save();
                        canvas2.translate((float) (this.timeX + drawable2.getIntrinsicWidth() + AndroidUtilities.dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.dp(12.3f)) - this.timeLayout.getHeight()));
                        this.viewsLayout.draw(canvas2);
                        canvas.restore();
                    }
                }
                canvas.save();
                canvas2.translate((float) (this.timeX + i5), (float) ((this.layoutHeight - AndroidUtilities.dp(12.3f)) - this.timeLayout.getHeight()));
                this.timeLayout.draw(canvas2);
                canvas.restore();
                Theme.chat_timePaint.setAlpha(255);
            }
            if (this.currentMessageObject.isOutOwner()) {
                boolean z4 = true;
                boolean z5 = ((int) (this.currentMessageObject.getDialogId() >> 32)) == 1;
                if (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing()) {
                    z4 = false;
                    z2 = false;
                    z = false;
                    z3 = true;
                } else if (this.currentMessageObject.isSendError()) {
                    z4 = false;
                    z2 = false;
                    z = true;
                } else {
                    if (this.currentMessageObject.isSent()) {
                        MessageObject messageObject2 = this.currentMessageObject;
                        if (!messageObject2.scheduled && !messageObject2.isUnread()) {
                            z2 = true;
                            z = false;
                        }
                    } else {
                        z4 = false;
                    }
                    z2 = false;
                    z = false;
                }
                if (z3) {
                    if (!this.mediaBackground || this.captionLayout != null) {
                        BaseCell.setDrawableBounds(Theme.chat_msgOutClockDrawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicHeight());
                        Theme.chat_msgOutClockDrawable.draw(canvas2);
                    } else if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                        Theme.chat_msgStickerClockDrawable.setAlpha((int) (this.timeAlpha * 255.0f));
                        BaseCell.setDrawableBounds(Theme.chat_msgStickerClockDrawable, (this.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicHeight());
                        Theme.chat_msgStickerClockDrawable.draw(canvas2);
                        Theme.chat_msgStickerClockDrawable.setAlpha(255);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_msgMediaClockDrawable, (this.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                        Theme.chat_msgMediaClockDrawable.draw(canvas2);
                    }
                }
                if (!z5) {
                    if (z4) {
                        if (!this.mediaBackground || this.captionLayout != null) {
                            if (z2) {
                                drawable = isDrawSelectionBackground() ? Theme.chat_msgOutCheckReadSelectedDrawable : Theme.chat_msgOutCheckReadDrawable;
                                BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(22.5f)) - drawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable.getIntrinsicHeight());
                            } else {
                                drawable = isDrawSelectionBackground() ? Theme.chat_msgOutCheckSelectedDrawable : Theme.chat_msgOutCheckDrawable;
                                BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - drawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable.getIntrinsicHeight());
                            }
                            drawable.draw(canvas2);
                        } else if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                            if (z2) {
                                BaseCell.setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgStickerCheckDrawable.draw(canvas2);
                        } else {
                            if (z2) {
                                BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgMediaCheckDrawable.setAlpha((int) (this.timeAlpha * 255.0f));
                            Theme.chat_msgMediaCheckDrawable.draw(canvas2);
                            Theme.chat_msgMediaCheckDrawable.setAlpha(255);
                        }
                    }
                    if (z2) {
                        if (!this.mediaBackground || this.captionLayout != null) {
                            Drawable drawable6 = isDrawSelectionBackground() ? Theme.chat_msgOutHalfCheckSelectedDrawable : Theme.chat_msgOutHalfCheckDrawable;
                            BaseCell.setDrawableBounds(drawable6, (this.layoutWidth - AndroidUtilities.dp(18.0f)) - drawable6.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable6.getIntrinsicHeight());
                            drawable6.draw(canvas2);
                        } else if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                            BaseCell.setDrawableBounds(Theme.chat_msgStickerHalfCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicHeight());
                            Theme.chat_msgStickerHalfCheckDrawable.draw(canvas2);
                        } else {
                            BaseCell.setDrawableBounds(Theme.chat_msgMediaHalfCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicHeight());
                            Theme.chat_msgMediaHalfCheckDrawable.setAlpha((int) (this.timeAlpha * 255.0f));
                            Theme.chat_msgMediaHalfCheckDrawable.draw(canvas2);
                            Theme.chat_msgMediaHalfCheckDrawable.setAlpha(255);
                        }
                    }
                } else if (z2 || z4) {
                    if (!this.mediaBackground || this.captionLayout != null) {
                        BaseCell.setDrawableBounds(Theme.chat_msgBroadcastDrawable, (this.layoutWidth - AndroidUtilities.dp(20.5f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicHeight());
                        Theme.chat_msgBroadcastDrawable.draw(canvas2);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_msgBroadcastMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(24.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(14.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicHeight());
                        Theme.chat_msgBroadcastMediaDrawable.draw(canvas2);
                    }
                }
                if (z) {
                    if (!this.mediaBackground || this.captionLayout != null) {
                        i3 = this.layoutWidth - AndroidUtilities.dp(32.0f);
                        i2 = this.layoutHeight;
                        i = AndroidUtilities.dp(21.0f);
                    } else {
                        i3 = this.layoutWidth - AndroidUtilities.dp(34.5f);
                        i2 = this.layoutHeight;
                        i = AndroidUtilities.dp(26.5f);
                    }
                    int i6 = i2 - i;
                    this.rect.set((float) i3, (float) i6, (float) (AndroidUtilities.dp(14.0f) + i3), (float) (AndroidUtilities.dp(14.0f) + i6));
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                    BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, i3 + AndroidUtilities.dp(6.0f), i6 + AndroidUtilities.dp(2.0f));
                    Theme.chat_msgErrorDrawable.draw(canvas2);
                }
            }
        }
    }

    public void drawOverlays(Canvas canvas) {
        int i;
        Drawable drawable;
        int dp;
        float f;
        PollButton pollButton;
        int i2;
        int i3;
        int i4;
        Drawable drawable2;
        int i5;
        Drawable drawable3;
        Drawable drawable4;
        int i6;
        AnimatedFileDrawable animation;
        Canvas canvas2 = canvas;
        long uptimeMillis = SystemClock.uptimeMillis();
        long j = uptimeMillis - this.lastAnimationTime;
        if (j > 17) {
            j = 17;
        }
        this.lastAnimationTime = uptimeMillis;
        int i7 = 1;
        char c = 0;
        if (this.currentMessageObject.hadAnimationNotReadyLoading && this.photoImage.getVisible() && !this.currentMessageObject.needDrawBluredPreview() && (((i6 = this.documentAttachType) == 7 || i6 == 4 || i6 == 2) && (animation = this.photoImage.getAnimation()) != null && animation.hasBitmap())) {
            this.currentMessageObject.hadAnimationNotReadyLoading = false;
            updateButtonState(false, true, false);
        }
        MessageObject messageObject = this.currentMessageObject;
        int i8 = messageObject.type;
        float f2 = 1.0f;
        if (i8 == 1 || (i = this.documentAttachType) == 4 || i == 2) {
            if (this.photoImage.getVisible()) {
                if (!this.currentMessageObject.needDrawBluredPreview() && this.documentAttachType == 4) {
                    int alpha = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                    if (this.drawPhotoCheckBox) {
                        Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) alpha) * this.controlsAlpha * (1.0f - this.checkBoxAnimationProgress)));
                    } else {
                        Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) alpha) * this.controlsAlpha));
                    }
                    Drawable drawable5 = Theme.chat_msgMediaMenuDrawable;
                    int imageX = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                    this.otherX = imageX;
                    int imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                    this.otherY = imageY;
                    BaseCell.setDrawableBounds(drawable5, imageX, imageY);
                    Theme.chat_msgMediaMenuDrawable.draw(canvas2);
                    Theme.chat_msgMediaMenuDrawable.setAlpha(alpha);
                }
                boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (this.animatingNoSoundPlaying != isPlayingMessage) {
                    this.animatingNoSoundPlaying = isPlayingMessage;
                    this.animatingNoSound = isPlayingMessage ? 1 : 2;
                    this.animatingNoSoundProgress = isPlayingMessage ? 1.0f : 0.0f;
                }
                int i9 = this.buttonState;
                if (i9 == 1 || i9 == 2 || i9 == 0 || i9 == 3 || i9 == -1 || this.currentMessageObject.needDrawBluredPreview()) {
                    if (this.autoPlayingMedia) {
                        updatePlayingMessageProgress();
                    }
                    if (this.infoLayout != null && (!this.forceNotDrawTime || this.autoPlayingMedia || this.drawVideoImageButton)) {
                        float f3 = (!this.currentMessageObject.needDrawBluredPreview() || this.docTitleLayout != null) ? this.animatingDrawVideoImageButtonProgress : 0.0f;
                        Theme.chat_infoPaint.setColor(Theme.getColor("chat_mediaInfoText"));
                        int imageX2 = this.photoImage.getImageX() + AndroidUtilities.dp(4.0f);
                        int imageY2 = this.photoImage.getImageY() + AndroidUtilities.dp(4.0f);
                        int intrinsicWidth = (!this.autoPlayingMedia || (isPlayingMessage && this.animatingNoSound == 0)) ? 0 : (int) (((float) (Theme.chat_msgNoSoundDrawable.getIntrinsicWidth() + AndroidUtilities.dp(4.0f))) * this.animatingNoSoundProgress);
                        int ceil = (int) Math.ceil((double) (((float) (this.infoWidth + AndroidUtilities.dp(8.0f) + intrinsicWidth)) + (((float) (((Math.max(this.infoWidth + intrinsicWidth, this.docTitleWidth) + (this.canStreamVideo ? AndroidUtilities.dp(32.0f) : 0)) - this.infoWidth) - intrinsicWidth)) * f3)));
                        if (f3 != 0.0f && this.docTitleLayout == null) {
                            f3 = 0.0f;
                        }
                        this.rect.set((float) imageX2, (float) imageY2, (float) (imageX2 + ceil), (float) (imageY2 + AndroidUtilities.dp((15.5f * f3) + 16.5f)));
                        int alpha2 = Theme.chat_timeBackgroundPaint.getAlpha();
                        Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) alpha2) * this.controlsAlpha));
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                        Theme.chat_timeBackgroundPaint.setAlpha(alpha2);
                        Theme.chat_infoPaint.setAlpha((int) (this.controlsAlpha * 255.0f));
                        canvas.save();
                        int imageX3 = this.photoImage.getImageX() + AndroidUtilities.dp((this.canStreamVideo ? 30.0f * f3 : 0.0f) + 8.0f);
                        this.noSoundCenterX = imageX3;
                        canvas2.translate((float) imageX3, (float) (this.photoImage.getImageY() + AndroidUtilities.dp((0.2f * f3) + 5.5f)));
                        StaticLayout staticLayout = this.infoLayout;
                        if (staticLayout != null) {
                            staticLayout.draw(canvas2);
                        }
                        if (f3 > 0.0f && this.docTitleLayout != null) {
                            canvas.save();
                            Theme.chat_infoPaint.setAlpha((int) (this.controlsAlpha * 255.0f * f3));
                            canvas2.translate(0.0f, (float) AndroidUtilities.dp(f3 * 14.3f));
                            this.docTitleLayout.draw(canvas2);
                            canvas.restore();
                        }
                        if (intrinsicWidth != 0) {
                            Drawable drawable6 = Theme.chat_msgNoSoundDrawable;
                            float f4 = this.animatingNoSoundProgress;
                            drawable6.setAlpha((int) (255.0f * f4 * f4 * this.controlsAlpha));
                            canvas2.translate((float) (this.infoWidth + AndroidUtilities.dp(4.0f)), 0.0f);
                            int dp2 = AndroidUtilities.dp(this.animatingNoSoundProgress * 14.0f);
                            int dp3 = (AndroidUtilities.dp(14.0f) - dp2) / 2;
                            Theme.chat_msgNoSoundDrawable.setBounds(0, dp3, dp2, dp3 + dp2);
                            Theme.chat_msgNoSoundDrawable.draw(canvas2);
                            this.noSoundCenterX += this.infoWidth + AndroidUtilities.dp(4.0f) + (dp2 / 2);
                        }
                        canvas.restore();
                        Theme.chat_infoPaint.setAlpha(255);
                    }
                }
                int i10 = this.animatingDrawVideoImageButton;
                if (i10 == 1) {
                    this.animatingDrawVideoImageButtonProgress -= ((float) j) / 160.0f;
                    if (this.animatingDrawVideoImageButtonProgress <= 0.0f) {
                        this.animatingDrawVideoImageButtonProgress = 0.0f;
                        this.animatingDrawVideoImageButton = 0;
                    }
                    invalidate();
                } else if (i10 == 2) {
                    this.animatingDrawVideoImageButtonProgress += ((float) j) / 160.0f;
                    if (this.animatingDrawVideoImageButtonProgress >= 1.0f) {
                        this.animatingDrawVideoImageButtonProgress = 1.0f;
                        this.animatingDrawVideoImageButton = 0;
                    }
                    invalidate();
                }
                int i11 = this.animatingNoSound;
                if (i11 == 1) {
                    this.animatingNoSoundProgress -= ((float) j) / 180.0f;
                    if (this.animatingNoSoundProgress <= 0.0f) {
                        this.animatingNoSoundProgress = 0.0f;
                        this.animatingNoSound = 0;
                    }
                    invalidate();
                } else if (i11 == 2) {
                    this.animatingNoSoundProgress += ((float) j) / 180.0f;
                    if (this.animatingNoSoundProgress >= 1.0f) {
                        this.animatingNoSoundProgress = 1.0f;
                        this.animatingNoSound = 0;
                    }
                    invalidate();
                }
            }
        } else if (i8 == 4) {
            if (this.docTitleLayout != null) {
                if (messageObject.isOutOwner()) {
                    Theme.chat_locationTitlePaint.setColor(Theme.getColor("chat_messageTextOut"));
                    Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outVenueInfoSelectedText" : "chat_outVenueInfoText"));
                } else {
                    Theme.chat_locationTitlePaint.setColor(Theme.getColor("chat_messageTextIn"));
                    Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inVenueInfoSelectedText" : "chat_inVenueInfoText"));
                }
                if (this.currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive) {
                    int imageY22 = this.photoImage.getImageY2() + AndroidUtilities.dp(30.0f);
                    if (!this.locationExpired) {
                        this.forceNotDrawTime = true;
                        float abs = 1.0f - (((float) Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date)) / ((float) this.currentMessageObject.messageOwner.media.period));
                        this.rect.set((float) (this.photoImage.getImageX2() - AndroidUtilities.dp(43.0f)), (float) (imageY22 - AndroidUtilities.dp(15.0f)), (float) (this.photoImage.getImageX2() - AndroidUtilities.dp(13.0f)), (float) (AndroidUtilities.dp(15.0f) + imageY22));
                        if (this.currentMessageObject.isOutOwner()) {
                            Theme.chat_radialProgress2Paint.setColor(Theme.getColor("chat_outInstant"));
                            Theme.chat_livePaint.setColor(Theme.getColor("chat_outInstant"));
                        } else {
                            Theme.chat_radialProgress2Paint.setColor(Theme.getColor("chat_inInstant"));
                            Theme.chat_livePaint.setColor(Theme.getColor("chat_inInstant"));
                        }
                        Theme.chat_radialProgress2Paint.setAlpha(50);
                        canvas2.drawCircle(this.rect.centerX(), this.rect.centerY(), (float) AndroidUtilities.dp(15.0f), Theme.chat_radialProgress2Paint);
                        Theme.chat_radialProgress2Paint.setAlpha(255);
                        canvas.drawArc(this.rect, -90.0f, -360.0f * abs, false, Theme.chat_radialProgress2Paint);
                        String formatLocationLeftTime = LocaleController.formatLocationLeftTime(Math.abs(this.currentMessageObject.messageOwner.media.period - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date)));
                        canvas2.drawText(formatLocationLeftTime, this.rect.centerX() - (Theme.chat_livePaint.measureText(formatLocationLeftTime) / 2.0f), (float) (imageY22 + AndroidUtilities.dp(4.0f)), Theme.chat_livePaint);
                        canvas.save();
                        canvas2.translate((float) (this.photoImage.getImageX() + AndroidUtilities.dp(10.0f)), (float) (this.photoImage.getImageY2() + AndroidUtilities.dp(10.0f)));
                        this.docTitleLayout.draw(canvas2);
                        canvas2.translate(0.0f, (float) AndroidUtilities.dp(23.0f));
                        this.infoLayout.draw(canvas2);
                        canvas.restore();
                    }
                    int imageX4 = (this.photoImage.getImageX() + (this.photoImage.getImageWidth() / 2)) - AndroidUtilities.dp(31.0f);
                    int imageY3 = (this.photoImage.getImageY() + (this.photoImage.getImageHeight() / 2)) - AndroidUtilities.dp(38.0f);
                    BaseCell.setDrawableBounds(Theme.chat_msgAvatarLiveLocationDrawable, imageX4, imageY3);
                    Theme.chat_msgAvatarLiveLocationDrawable.draw(canvas2);
                    this.locationImageReceiver.setImageCoords(imageX4 + AndroidUtilities.dp(5.0f), imageY3 + AndroidUtilities.dp(5.0f), AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
                    this.locationImageReceiver.draw(canvas2);
                } else {
                    canvas.save();
                    canvas2.translate((float) (this.photoImage.getImageX() + AndroidUtilities.dp(6.0f)), (float) (this.photoImage.getImageY2() + AndroidUtilities.dp(8.0f)));
                    this.docTitleLayout.draw(canvas2);
                    if (this.infoLayout != null) {
                        canvas2.translate(0.0f, (float) AndroidUtilities.dp(21.0f));
                        this.infoLayout.draw(canvas2);
                    }
                    canvas.restore();
                }
            }
        } else if (i8 == 16) {
            if (messageObject.isOutOwner()) {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_messageTextOut"));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText"));
            } else {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_messageTextIn"));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText"));
            }
            this.forceNotDrawTime = true;
            if (this.currentMessageObject.isOutOwner()) {
                i5 = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(16.0f);
            } else if (!this.isChat || !this.currentMessageObject.needDrawAvatar()) {
                i5 = AndroidUtilities.dp(25.0f);
            } else {
                i5 = AndroidUtilities.dp(74.0f);
            }
            this.otherX = i5;
            if (this.titleLayout != null) {
                canvas.save();
                canvas2.translate((float) i5, (float) (AndroidUtilities.dp(12.0f) + this.namesOffset));
                this.titleLayout.draw(canvas2);
                canvas.restore();
            }
            if (this.docTitleLayout != null) {
                canvas.save();
                canvas2.translate((float) (AndroidUtilities.dp(19.0f) + i5), (float) (AndroidUtilities.dp(37.0f) + this.namesOffset));
                this.docTitleLayout.draw(canvas2);
                canvas.restore();
            }
            if (this.currentMessageObject.isOutOwner()) {
                drawable4 = Theme.chat_msgCallUpGreenDrawable;
                drawable3 = (isDrawSelectionBackground() || this.otherPressed) ? Theme.chat_msgOutCallSelectedDrawable : Theme.chat_msgOutCallDrawable;
            } else {
                TLRPC.PhoneCallDiscardReason phoneCallDiscardReason = this.currentMessageObject.messageOwner.action.reason;
                if ((phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonBusy)) {
                    drawable4 = Theme.chat_msgCallDownRedDrawable;
                } else {
                    drawable4 = Theme.chat_msgCallDownGreenDrawable;
                }
                drawable3 = (isDrawSelectionBackground() || this.otherPressed) ? Theme.chat_msgInCallSelectedDrawable : Theme.chat_msgInCallDrawable;
            }
            BaseCell.setDrawableBounds(drawable4, i5 - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(36.0f) + this.namesOffset);
            drawable4.draw(canvas2);
            int dp4 = i5 + AndroidUtilities.dp(205.0f);
            int dp5 = AndroidUtilities.dp(22.0f);
            this.otherY = dp5;
            BaseCell.setDrawableBounds(drawable3, dp4, dp5);
            drawable3.draw(canvas2);
        } else if (i8 == 17) {
            if (messageObject.isOutOwner()) {
                int color = Theme.getColor("chat_messageTextOut");
                Theme.chat_audioTitlePaint.setColor(color);
                Theme.chat_audioPerformerPaint.setColor(color);
                Theme.chat_instantViewPaint.setColor(color);
                int color2 = Theme.getColor(isDrawSelectionBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText");
                Theme.chat_timePaint.setColor(color2);
                Theme.chat_livePaint.setColor(color2);
            } else {
                int color3 = Theme.getColor("chat_messageTextIn");
                Theme.chat_audioTitlePaint.setColor(color3);
                Theme.chat_audioPerformerPaint.setColor(color3);
                Theme.chat_instantViewPaint.setColor(color3);
                int color4 = Theme.getColor(isDrawSelectionBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText");
                Theme.chat_timePaint.setColor(color4);
                Theme.chat_livePaint.setColor(color4);
            }
            if (this.currentMessageObject.isOutOwner()) {
                dp = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(11.0f);
            } else if (!this.isChat || !this.currentMessageObject.needDrawAvatar()) {
                dp = AndroidUtilities.dp(20.0f);
            } else {
                dp = AndroidUtilities.dp(68.0f);
            }
            int i12 = dp;
            if (this.titleLayout != null) {
                canvas.save();
                canvas2.translate((float) i12, (float) (AndroidUtilities.dp(15.0f) + this.namesOffset));
                this.titleLayout.draw(canvas2);
                canvas.restore();
            }
            if (this.docTitleLayout != null) {
                canvas.save();
                float f5 = (float) (this.docTitleOffsetX + i12);
                StaticLayout staticLayout2 = this.titleLayout;
                canvas2.translate(f5, (float) ((staticLayout2 != null ? staticLayout2.getHeight() : 0) + AndroidUtilities.dp(20.0f) + this.namesOffset));
                this.docTitleLayout.draw(canvas2);
                canvas.restore();
            }
            if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.selectorDrawable) != null) {
                this.selectorDrawableMaskType = 1;
                drawable2.draw(canvas2);
            }
            int size = this.pollButtons.size();
            int i13 = 0;
            int i14 = 0;
            while (i13 < size) {
                PollButton pollButton2 = this.pollButtons.get(i13);
                int unused = pollButton2.x = i12;
                canvas.save();
                canvas2.translate((float) (AndroidUtilities.dp(34.0f) + i12), (float) (pollButton2.y + this.namesOffset));
                pollButton2.title.draw(canvas2);
                if (this.animatePollAnswerAlpha) {
                    f = Math.min((this.pollUnvoteInProgress ? f2 - this.pollAnimationProgress : this.pollAnimationProgress) / 0.3f, f2) * 255.0f;
                } else {
                    f = 255.0f;
                }
                int i15 = (int) f;
                if (this.pollVoted || this.pollClosed || this.animatePollAnswerAlpha) {
                    Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outAudioSeekbarFill" : "chat_inAudioSeekbarFill"));
                    if (this.animatePollAnswerAlpha) {
                        float f6 = (float) i15;
                        Theme.chat_instantViewPaint.setAlpha((int) ((((float) Theme.chat_instantViewPaint.getAlpha()) / 255.0f) * f6));
                        Theme.chat_docBackPaint.setAlpha((int) (f6 * (((float) Theme.chat_docBackPaint.getAlpha()) / 255.0f)));
                    }
                    Object[] objArr = new Object[i7];
                    objArr[c] = Integer.valueOf((int) Math.ceil((double) (((float) pollButton2.prevPercent) + (((float) (pollButton2.percent - pollButton2.prevPercent)) * this.pollAnimationProgress))));
                    String format = String.format("%d%%", objArr);
                    canvas2.drawText(format, (float) ((-AndroidUtilities.dp(7.0f)) - ((int) Math.ceil((double) Theme.chat_instantViewPaint.measureText(format)))), (float) AndroidUtilities.dp(14.0f), Theme.chat_instantViewPaint);
                    this.instantButtonRect.set(0.0f, (float) (pollButton2.height + AndroidUtilities.dp(6.0f)), ((float) (this.backgroundWidth - AndroidUtilities.dp(76.0f))) * (pollButton2.prevPercentProgress + ((pollButton2.percentProgress - pollButton2.prevPercentProgress) * this.pollAnimationProgress)), (float) (pollButton2.height + AndroidUtilities.dp(11.0f)));
                    canvas2.drawRoundRect(this.instantButtonRect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.chat_docBackPaint);
                }
                if ((this.pollVoted || this.pollClosed) && !this.animatePollAnswerAlpha) {
                    pollButton = pollButton2;
                    i4 = i13;
                    i3 = size;
                    i2 = i12;
                } else {
                    if (isDrawSelectionBackground()) {
                        Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outVoiceSeekbarSelected" : "chat_inVoiceSeekbarSelected"));
                    } else {
                        Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outVoiceSeekbar" : "chat_inVoiceSeekbar"));
                    }
                    if (this.animatePollAnswerAlpha) {
                        Theme.chat_replyLinePaint.setAlpha((int) (((float) (255 - i15)) * (((float) Theme.chat_replyLinePaint.getAlpha()) / 255.0f)));
                    }
                    float f7 = (float) (-AndroidUtilities.dp(2.0f));
                    float access$800 = (float) (pollButton2.height + AndroidUtilities.dp(13.0f));
                    float dp6 = (float) (this.backgroundWidth - AndroidUtilities.dp(56.0f));
                    float access$8002 = (float) (pollButton2.height + AndroidUtilities.dp(13.0f));
                    pollButton = pollButton2;
                    float f8 = access$800;
                    i4 = i13;
                    float f9 = dp6;
                    i3 = size;
                    float var_ = access$8002;
                    i2 = i12;
                    canvas.drawLine(f7, f8, f9, var_, Theme.chat_replyLinePaint);
                    if (!this.pollVoteInProgress || i4 != this.pollVoteInProgressNum) {
                        if (this.currentMessageObject.isOutOwner()) {
                            Theme.chat_instantViewRectPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outMenuSelected" : "chat_outMenu"));
                        } else {
                            Theme.chat_instantViewRectPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inMenuSelected" : "chat_inMenu"));
                        }
                        if (this.animatePollAnswerAlpha) {
                            Theme.chat_instantViewRectPaint.setAlpha((int) (((float) (255 - i15)) * (((float) Theme.chat_instantViewRectPaint.getAlpha()) / 255.0f)));
                        }
                        canvas2.drawCircle((float) (-AndroidUtilities.dp(23.0f)), (float) AndroidUtilities.dp(9.0f), (float) AndroidUtilities.dp(8.5f), Theme.chat_instantViewRectPaint);
                    } else {
                        Theme.chat_instantViewRectPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outAudioSeekbarFill" : "chat_inAudioSeekbarFill"));
                        if (this.animatePollAnswerAlpha) {
                            Theme.chat_instantViewRectPaint.setAlpha((int) (((float) (255 - i15)) * (((float) Theme.chat_instantViewRectPaint.getAlpha()) / 255.0f)));
                        }
                        this.instantButtonRect.set((float) ((-AndroidUtilities.dp(23.0f)) - AndroidUtilities.dp(8.5f)), (float) (AndroidUtilities.dp(9.0f) - AndroidUtilities.dp(8.5f)), (float) ((-AndroidUtilities.dp(23.0f)) + AndroidUtilities.dp(8.5f)), (float) (AndroidUtilities.dp(9.0f) + AndroidUtilities.dp(8.5f)));
                        canvas.drawArc(this.instantButtonRect, this.voteRadOffset, this.voteCurrentCircleLength, false, Theme.chat_instantViewRectPaint);
                    }
                }
                canvas.restore();
                if (i4 == i3 - 1) {
                    i14 = pollButton.y + this.namesOffset + pollButton.height;
                }
                i13 = i4 + 1;
                size = i3;
                i12 = i2;
                i7 = 1;
                c = 0;
                f2 = 1.0f;
            }
            int i16 = i12;
            if (this.infoLayout != null) {
                canvas.save();
                canvas2.translate((float) (i16 + this.infoX), (float) (i14 + AndroidUtilities.dp(22.0f)));
                this.infoLayout.draw(canvas2);
                canvas.restore();
            }
            updatePollAnimations();
        } else if (i8 == 12) {
            if (messageObject.isOutOwner()) {
                Theme.chat_contactNamePaint.setColor(Theme.getColor("chat_outContactNameText"));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outContactPhoneSelectedText" : "chat_outContactPhoneText"));
            } else {
                Theme.chat_contactNamePaint.setColor(Theme.getColor("chat_inContactNameText"));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inContactPhoneSelectedText" : "chat_inContactPhoneText"));
            }
            if (this.titleLayout != null) {
                canvas.save();
                canvas2.translate((float) (this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(16.0f) + this.namesOffset));
                this.titleLayout.draw(canvas2);
                canvas.restore();
            }
            if (this.docTitleLayout != null) {
                canvas.save();
                canvas2.translate((float) (this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(39.0f) + this.namesOffset));
                this.docTitleLayout.draw(canvas2);
                canvas.restore();
            }
            if (this.currentMessageObject.isOutOwner()) {
                drawable = isDrawSelectionBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
            } else {
                drawable = isDrawSelectionBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            }
            int imageX5 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(48.0f);
            this.otherX = imageX5;
            int imageY4 = this.photoImage.getImageY() - AndroidUtilities.dp(5.0f);
            this.otherY = imageY4;
            BaseCell.setDrawableBounds(drawable, imageX5, imageY4);
            drawable.draw(canvas2);
            if (this.drawInstantView) {
                int imageX6 = this.photoImage.getImageX() - AndroidUtilities.dp(2.0f);
                int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(64.0f);
                Paint paint = Theme.chat_instantViewRectPaint;
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_instantViewPaint.setColor(Theme.getColor("chat_outPreviewInstantText"));
                    paint.setColor(Theme.getColor("chat_outPreviewInstantText"));
                } else {
                    Theme.chat_instantViewPaint.setColor(Theme.getColor("chat_inPreviewInstantText"));
                    paint.setColor(Theme.getColor("chat_inPreviewInstantText"));
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    this.selectorDrawableMaskType = 0;
                    this.selectorDrawable.setBounds(imageX6, measuredHeight, this.instantWidth + imageX6, AndroidUtilities.dp(36.0f) + measuredHeight);
                    this.selectorDrawable.draw(canvas2);
                }
                this.instantButtonRect.set((float) imageX6, (float) measuredHeight, (float) (this.instantWidth + imageX6), (float) (AndroidUtilities.dp(36.0f) + measuredHeight));
                canvas2.drawRoundRect(this.instantButtonRect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), paint);
                if (this.instantViewLayout != null) {
                    canvas.save();
                    canvas2.translate((float) (imageX6 + this.instantTextX), (float) (measuredHeight + AndroidUtilities.dp(10.5f)));
                    this.instantViewLayout.draw(canvas2);
                    canvas.restore();
                }
            }
        }
        if (this.drawImageButton && this.photoImage.getVisible()) {
            float var_ = this.controlsAlpha;
            if (var_ != 1.0f) {
                this.radialProgress.setOverrideAlpha(var_);
            }
            if (this.photoImage.hasImageSet()) {
                this.radialProgress.setBackgroundDrawable((Drawable) null);
            } else {
                this.radialProgress.setBackgroundDrawable(isDrawSelectionBackground() ? this.currentBackgroundSelectedDrawable : this.currentBackgroundDrawable);
            }
            this.radialProgress.draw(canvas2);
        }
        if ((this.drawVideoImageButton || this.animatingDrawVideoImageButton != 0) && this.photoImage.getVisible()) {
            float var_ = this.controlsAlpha;
            if (var_ != 1.0f) {
                this.videoRadialProgress.setOverrideAlpha(var_);
            }
            this.videoRadialProgress.draw(canvas2);
        }
        if (this.drawPhotoCheckBox) {
            int dp7 = AndroidUtilities.dp(21.0f);
            this.photoCheckBox.setColor((String) null, (String) null, this.currentMessageObject.isOutOwner() ? "chat_outBubbleSelected" : "chat_inBubbleSelected");
            this.photoCheckBox.setBounds(this.photoImage.getImageX2() - AndroidUtilities.dp(25.0f), this.photoImage.getImageY() + AndroidUtilities.dp(4.0f), dp7, dp7);
            this.photoCheckBox.draw(canvas2);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public MessageObject getMessageObject() {
        MessageObject messageObject = this.messageObjectToSet;
        return messageObject != null ? messageObject : this.currentMessageObject;
    }

    public TLRPC.Document getStreamingMedia() {
        int i = this.documentAttachType;
        if (i == 4 || i == 7 || i == 2) {
            return this.documentAttach;
        }
        return null;
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

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (i == 16) {
            if (getIconForCurrentState() != 4) {
                didPressButton(true, false);
            } else if (this.currentMessageObject.type == 16) {
                this.delegate.didPressOther(this, (float) this.otherX, (float) this.otherY);
            } else {
                didClickedImage();
            }
            return true;
        }
        if (i == NUM) {
            didPressMiniButton(true);
        } else if (i == NUM && (chatMessageCellDelegate = this.delegate) != null) {
            if (this.currentMessageObject.type == 16) {
                chatMessageCellDelegate.didLongPress(this, 0.0f, 0.0f);
            } else {
                chatMessageCellDelegate.didPressOther(this, (float) this.otherX, (float) this.otherY);
            }
        }
        return super.performAccessibilityAction(i, bundle);
    }

    public void setAnimationRunning(boolean z) {
        this.animationRunning = z;
    }

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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        return new MessageAccessibilityNodeProvider();
    }

    /* access modifiers changed from: private */
    public void sendAccessibilityEventForVirtualView(int i, int i2) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(i2);
            obtain.setPackageName(getContext().getPackageName());
            obtain.setSource(this, i);
            getParent().requestSendAccessibilityEvent(this, obtain);
        }
    }

    public static org.telegram.ui.Components.Point getMessageSize(int i, int i2) {
        return getMessageSize(i, i2, 0, 0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x004c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.telegram.ui.Components.Point getMessageSize(int r3, int r4, int r5, int r6) {
        /*
            if (r6 == 0) goto L_0x0004
            if (r5 != 0) goto L_0x0050
        L_0x0004:
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            r6 = 1060320051(0x3var_, float:0.7)
            if (r5 == 0) goto L_0x0016
            int r5 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
        L_0x0011:
            float r5 = (float) r5
            float r5 = r5 * r6
            int r5 = (int) r5
            goto L_0x0035
        L_0x0016:
            if (r3 < r4) goto L_0x002a
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r6 = r5.x
            int r5 = r5.y
            int r5 = java.lang.Math.min(r6, r5)
            r6 = 1115684864(0x42800000, float:64.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            goto L_0x0035
        L_0x002a:
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r5.x
            int r5 = r5.y
            int r5 = java.lang.Math.min(r0, r5)
            goto L_0x0011
        L_0x0035:
            r6 = 1120403456(0x42CLASSNAME, float:100.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r5
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            if (r5 <= r0) goto L_0x0046
            int r5 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
        L_0x0046:
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            if (r6 <= r0) goto L_0x0050
            int r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
        L_0x0050:
            float r3 = (float) r3
            float r5 = (float) r5
            float r0 = r3 / r5
            float r1 = r3 / r0
            int r1 = (int) r1
            float r4 = (float) r4
            float r0 = r4 / r0
            int r0 = (int) r0
            r2 = 1125515264(0x43160000, float:150.0)
            if (r1 != 0) goto L_0x0063
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
        L_0x0063:
            if (r0 != 0) goto L_0x0069
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
        L_0x0069:
            if (r0 <= r6) goto L_0x0072
            float r3 = (float) r0
            float r4 = (float) r6
            float r3 = r3 / r4
            float r4 = (float) r1
            float r4 = r4 / r3
            int r1 = (int) r4
            goto L_0x0088
        L_0x0072:
            r6 = 1123024896(0x42var_, float:120.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            if (r0 >= r2) goto L_0x0087
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r0 = (float) r6
            float r4 = r4 / r0
            float r3 = r3 / r4
            int r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x0088
            int r1 = (int) r3
            goto L_0x0088
        L_0x0087:
            r6 = r0
        L_0x0088:
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
        int i = messageObject.type;
        if (i == 2 || i == 12 || i == 9 || i == 4 || i == 14 || i == 10 || i == 11 || i == 5) {
            return messageObject.getApproximateHeight();
        }
        this.photoImage.setIgnoreImageSet(true);
        this.avatarImage.setIgnoreImageSet(true);
        this.replyImageReceiver.setIgnoreImageSet(true);
        this.locationImageReceiver.setIgnoreImageSet(true);
        if (groupedMessages != null) {
            int i2 = 0;
            for (int i3 = 0; i3 < groupedMessages.messages.size(); i3++) {
                MessageObject messageObject2 = groupedMessages.messages.get(i3);
                MessageObject.GroupedMessagePosition groupedMessagePosition = groupedMessages.positions.get(messageObject2);
                if (!(groupedMessagePosition == null || (groupedMessagePosition.flags & 1) == 0)) {
                    setMessageContent(messageObject2, groupedMessages, false, false);
                    i2 += this.totalHeight + this.keyboardHeight;
                }
            }
            return i2;
        }
        setMessageContent(messageObject, groupedMessages, false, false);
        this.photoImage.setIgnoreImageSet(false);
        this.avatarImage.setIgnoreImageSet(false);
        this.replyImageReceiver.setIgnoreImageSet(false);
        this.locationImageReceiver.setIgnoreImageSet(false);
        return this.totalHeight + this.keyboardHeight;
    }

    private class MessageAccessibilityNodeProvider extends AccessibilityNodeProvider {
        private final int BOT_BUTTONS_START;
        private final int INSTANT_VIEW;
        private final int LINK_IDS_START;
        private final int POLL_BUTTONS_START;
        private final int REPLY;
        private final int SHARE;
        private Path linkPath;
        private Rect rect;
        private RectF rectF;

        private MessageAccessibilityNodeProvider() {
            this.LINK_IDS_START = 2000;
            this.BOT_BUTTONS_START = 1000;
            this.POLL_BUTTONS_START = 500;
            this.INSTANT_VIEW = 499;
            this.SHARE = 498;
            this.REPLY = 497;
            this.linkPath = new Path();
            this.rectF = new RectF();
            this.rect = new Rect();
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
            int i2;
            int i3;
            AccessibilityNodeInfo.CollectionItemInfo collectionItemInfo;
            String str;
            int i4;
            int[] iArr = {0, 0};
            ChatMessageCell.this.getLocationOnScreen(iArr);
            String str2 = null;
            int i5 = 0;
            if (i == -1) {
                AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain(ChatMessageCell.this);
                ChatMessageCell.this.onInitializeAccessibilityNodeInfo(obtain);
                StringBuilder sb = new StringBuilder();
                ChatMessageCell chatMessageCell = ChatMessageCell.this;
                if (chatMessageCell.isChat && chatMessageCell.currentUser != null && !ChatMessageCell.this.currentMessageObject.isOut()) {
                    sb.append(UserObject.getUserName(ChatMessageCell.this.currentUser));
                    sb.append(10);
                }
                if (!TextUtils.isEmpty(ChatMessageCell.this.currentMessageObject.messageText)) {
                    sb.append(ChatMessageCell.this.currentMessageObject.messageText);
                }
                if (ChatMessageCell.this.currentMessageObject.isMusic()) {
                    sb.append("\n");
                    sb.append(LocaleController.formatString("AccDescrMusicInfo", NUM, ChatMessageCell.this.currentMessageObject.getMusicAuthor(), ChatMessageCell.this.currentMessageObject.getMusicTitle()));
                } else if (ChatMessageCell.this.currentMessageObject.isVoice() || ChatMessageCell.this.currentMessageObject.isRoundVideo()) {
                    sb.append(", ");
                    sb.append(LocaleController.formatCallDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                    if (ChatMessageCell.this.currentMessageObject.isContentUnread()) {
                        sb.append(", ");
                        sb.append(LocaleController.getString("AccDescrMsgNotPlayed", NUM));
                    }
                }
                if (ChatMessageCell.this.lastPoll != null) {
                    sb.append(", ");
                    sb.append(ChatMessageCell.this.lastPoll.question);
                    sb.append(", ");
                    sb.append(LocaleController.getString("AnonymousPoll", NUM));
                }
                if (ChatMessageCell.this.currentMessageObject.messageOwner.media != null && !TextUtils.isEmpty(ChatMessageCell.this.currentMessageObject.caption)) {
                    sb.append("\n");
                    sb.append(ChatMessageCell.this.currentMessageObject.caption);
                }
                sb.append("\n");
                String str3 = LocaleController.getString("TodayAt", NUM) + " " + ChatMessageCell.this.currentTimeString;
                if (ChatMessageCell.this.currentMessageObject.isOut()) {
                    sb.append(LocaleController.formatString("AccDescrSentDate", NUM, str3));
                    sb.append(", ");
                    if (ChatMessageCell.this.currentMessageObject.isUnread()) {
                        i4 = NUM;
                        str = "AccDescrMsgUnread";
                    } else {
                        i4 = NUM;
                        str = "AccDescrMsgRead";
                    }
                    sb.append(LocaleController.getString(str, i4));
                } else {
                    sb.append(LocaleController.formatString("AccDescrReceivedDate", NUM, str3));
                }
                obtain.setContentDescription(sb.toString());
                obtain.setEnabled(true);
                if (Build.VERSION.SDK_INT >= 19 && (collectionItemInfo = obtain.getCollectionItemInfo()) != null) {
                    obtain.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collectionItemInfo.getRowIndex(), 1, 0, 1, false));
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(NUM, LocaleController.getString("AccActionMessageOptions", NUM)));
                    int access$3300 = ChatMessageCell.this.getIconForCurrentState();
                    if (access$3300 == 0) {
                        str2 = LocaleController.getString("AccActionPlay", NUM);
                    } else if (access$3300 == 1) {
                        str2 = LocaleController.getString("AccActionPause", NUM);
                    } else if (access$3300 == 2) {
                        str2 = LocaleController.getString("AccActionDownload", NUM);
                    } else if (access$3300 == 3) {
                        str2 = LocaleController.getString("AccActionCancelDownload", NUM);
                    } else if (access$3300 == 5) {
                        str2 = LocaleController.getString("AccActionOpenFile", NUM);
                    } else if (ChatMessageCell.this.currentMessageObject.type == 16) {
                        str2 = LocaleController.getString("CallAgain", NUM);
                    }
                    obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, str2));
                    obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccActionEnterSelectionMode", NUM)));
                    if (ChatMessageCell.this.getMiniIconForCurrentState() == 2) {
                        obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(NUM, LocaleController.getString("AccActionDownload", NUM)));
                    }
                } else {
                    obtain.addAction(16);
                    obtain.addAction(32);
                }
                if (ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable) {
                    Spannable spannable = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
                    int i6 = 0;
                    for (CharacterStyle characterStyle : (CharacterStyle[]) spannable.getSpans(0, spannable.length(), ClickableSpan.class)) {
                        obtain.addChild(ChatMessageCell.this, i6 + 2000);
                        i6++;
                    }
                }
                Iterator it = ChatMessageCell.this.botButtons.iterator();
                int i7 = 0;
                while (it.hasNext()) {
                    BotButton botButton = (BotButton) it.next();
                    obtain.addChild(ChatMessageCell.this, i7 + 1000);
                    i7++;
                }
                Iterator it2 = ChatMessageCell.this.pollButtons.iterator();
                while (it2.hasNext()) {
                    PollButton pollButton = (PollButton) it2.next();
                    obtain.addChild(ChatMessageCell.this, i5 + 500);
                    i5++;
                }
                if (ChatMessageCell.this.drawInstantView) {
                    obtain.addChild(ChatMessageCell.this, 499);
                }
                if (ChatMessageCell.this.drawShareButton) {
                    obtain.addChild(ChatMessageCell.this, 498);
                }
                if (ChatMessageCell.this.replyNameLayout != null) {
                    obtain.addChild(ChatMessageCell.this, 497);
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
            if (i >= 2000) {
                Spannable spannable2 = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
                ClickableSpan linkById = getLinkById(i);
                if (linkById == null) {
                    return null;
                }
                int[] access$4100 = ChatMessageCell.this.getRealSpanStartAndEnd(spannable2, linkById);
                obtain2.setText(spannable2.subSequence(access$4100[0], access$4100[1]).toString());
                Iterator<MessageObject.TextLayoutBlock> it3 = ChatMessageCell.this.currentMessageObject.textLayoutBlocks.iterator();
                while (true) {
                    if (!it3.hasNext()) {
                        break;
                    }
                    MessageObject.TextLayoutBlock next = it3.next();
                    int length = next.textLayout.getText().length();
                    int i8 = next.charactersOffset;
                    if (i8 <= access$4100[0] && length + i8 >= access$4100[1]) {
                        next.textLayout.getSelectionPath(access$4100[0] - i8, access$4100[1] - i8, this.linkPath);
                        this.linkPath.computeBounds(this.rectF, true);
                        Rect rect2 = this.rect;
                        RectF rectF2 = this.rectF;
                        rect2.set((int) rectF2.left, (int) rectF2.top, (int) rectF2.right, (int) rectF2.bottom);
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
                int i9 = i - 1000;
                if (i9 >= ChatMessageCell.this.botButtons.size()) {
                    return null;
                }
                BotButton botButton2 = (BotButton) ChatMessageCell.this.botButtons.get(i9);
                obtain2.setText(botButton2.title.getText());
                obtain2.setClassName("android.widget.Button");
                obtain2.setEnabled(true);
                obtain2.setClickable(true);
                obtain2.addAction(16);
                this.rect.set(botButton2.x, botButton2.y, botButton2.x + botButton2.width, botButton2.y + botButton2.height);
                if (ChatMessageCell.this.currentMessageObject.isOutOwner()) {
                    i3 = (ChatMessageCell.this.getMeasuredWidth() - ChatMessageCell.this.widthForButtons) - AndroidUtilities.dp(10.0f);
                } else {
                    i3 = ChatMessageCell.this.backgroundDrawableLeft + AndroidUtilities.dp(ChatMessageCell.this.mediaBackground ? 1.0f : 7.0f);
                }
                this.rect.offset(i3, ChatMessageCell.this.layoutHeight);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
            } else if (i >= 500) {
                int i10 = i - 500;
                if (i10 >= ChatMessageCell.this.pollButtons.size()) {
                    return null;
                }
                PollButton pollButton2 = (PollButton) ChatMessageCell.this.pollButtons.get(i10);
                obtain2.setText(pollButton2.title.getText());
                if (!ChatMessageCell.this.pollVoted) {
                    obtain2.setClassName("android.widget.Button");
                } else {
                    obtain2.setText(obtain2.getText() + ", " + pollButton2.percent + "%");
                }
                obtain2.setEnabled(true);
                obtain2.addAction(16);
                this.rect.set(pollButton2.x, pollButton2.y, pollButton2.x + (ChatMessageCell.this.backgroundWidth - AndroidUtilities.dp(76.0f)), pollButton2.y + pollButton2.height);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
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
                int imageX = ChatMessageCell.this.photoImage.getImageX();
                int measuredHeight = ChatMessageCell.this.getMeasuredHeight() - AndroidUtilities.dp(64.0f);
                if (ChatMessageCell.this.currentMessageObject.isOutOwner()) {
                    i2 = (ChatMessageCell.this.getMeasuredWidth() - ChatMessageCell.this.widthForButtons) - AndroidUtilities.dp(10.0f);
                } else {
                    i2 = AndroidUtilities.dp(ChatMessageCell.this.mediaBackground ? 1.0f : 7.0f) + ChatMessageCell.this.backgroundDrawableLeft;
                }
                this.rect.set(imageX + i2, measuredHeight, imageX + ChatMessageCell.this.instantWidth + i2, AndroidUtilities.dp(38.0f) + measuredHeight);
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
                ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
                if (chatMessageCell2.isOpenChatByShare(chatMessageCell2.currentMessageObject)) {
                    obtain2.setContentDescription(LocaleController.getString("AccDescrOpenChat", NUM));
                } else {
                    obtain2.setContentDescription(LocaleController.getString("ShareFile", NUM));
                }
                obtain2.addAction(16);
                this.rect.set(ChatMessageCell.this.shareStartX, ChatMessageCell.this.shareStartY, ChatMessageCell.this.shareStartX + AndroidUtilities.dp(40.0f), ChatMessageCell.this.shareStartY + AndroidUtilities.dp(32.0f));
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
                sb2.append(LocaleController.getString("Reply", NUM));
                sb2.append(", ");
                if (ChatMessageCell.this.replyNameLayout != null) {
                    sb2.append(ChatMessageCell.this.replyNameLayout.getText());
                    sb2.append(", ");
                }
                if (ChatMessageCell.this.replyTextLayout != null) {
                    sb2.append(ChatMessageCell.this.replyTextLayout.getText());
                }
                obtain2.setContentDescription(sb2.toString());
                obtain2.addAction(16);
                this.rect.set(ChatMessageCell.this.replyStartX, ChatMessageCell.this.replyStartY, ChatMessageCell.this.replyStartX + Math.max(ChatMessageCell.this.replyNameWidth, ChatMessageCell.this.replyTextWidth), ChatMessageCell.this.replyStartY + AndroidUtilities.dp(35.0f));
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            }
            obtain2.setFocusable(true);
            obtain2.setVisibleToUser(true);
            return obtain2;
        }

        public boolean performAction(int i, int i2, Bundle bundle) {
            ClickableSpan linkById;
            if (i == -1) {
                ChatMessageCell.this.performAccessibilityAction(i2, bundle);
            } else if (i2 == 64) {
                ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 32768);
            } else if (i2 == 16) {
                if (i >= 2000) {
                    ClickableSpan linkById2 = getLinkById(i);
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
                    if (ChatMessageCell.this.delegate != null) {
                        if (botButton.button != null) {
                            ChatMessageCell.this.delegate.didPressBotButton(ChatMessageCell.this, botButton.button);
                        } else if (botButton.reaction != null) {
                            ChatMessageCell.this.delegate.didPressReaction(ChatMessageCell.this, botButton.reaction);
                        }
                    }
                    ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                } else if (i >= 500) {
                    int i4 = i - 500;
                    if (i4 >= ChatMessageCell.this.pollButtons.size()) {
                        return false;
                    }
                    PollButton pollButton = (PollButton) ChatMessageCell.this.pollButtons.get(i4);
                    if (ChatMessageCell.this.delegate != null) {
                        ChatMessageCell.this.delegate.didPressVoteButton(ChatMessageCell.this, pollButton.answer);
                    }
                    ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                } else if (i == 499) {
                    if (ChatMessageCell.this.delegate != null) {
                        ChatMessageCellDelegate access$6300 = ChatMessageCell.this.delegate;
                        ChatMessageCell chatMessageCell = ChatMessageCell.this;
                        access$6300.didPressInstantButton(chatMessageCell, chatMessageCell.drawInstantViewType);
                    }
                } else if (i == 498) {
                    if (ChatMessageCell.this.delegate != null) {
                        ChatMessageCell.this.delegate.didPressShare(ChatMessageCell.this);
                    }
                } else if (i == 497 && ChatMessageCell.this.delegate != null && ChatMessageCell.this.currentMessageObject.hasValidReplyMessageObject()) {
                    ChatMessageCellDelegate access$63002 = ChatMessageCell.this.delegate;
                    ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
                    access$63002.didPressReplyMessage(chatMessageCell2, chatMessageCell2.currentMessageObject.messageOwner.reply_to_msg_id);
                }
            } else if (i2 == 32 && (linkById = getLinkById(i)) != null) {
                ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, linkById, true);
                ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 2);
            }
            return true;
        }

        private ClickableSpan getLinkById(int i) {
            int i2 = i - 2000;
            if (!(ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable)) {
                return null;
            }
            Spannable spannable = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
            ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(0, spannable.length(), ClickableSpan.class);
            if (clickableSpanArr.length <= i2) {
                return null;
            }
            return clickableSpanArr[i2];
        }
    }
}
