package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.Layout.Alignment;
import android.text.Layout.Directions;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewStructure;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessagePosition;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.messenger.MessageObject.TextLayoutBlock;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.PhoneCallDiscardReason;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC.TL_poll;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RoundVideoPlayingDrawable;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.SecretMediaViewer;

public class ChatMessageCell extends BaseCell implements FileDownloadProgressListener, ImageReceiverDelegate, SeekBarDelegate {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_ROUND = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static final int DOCUMENT_ATTACH_TYPE_WALLPAPER = 8;
    private int TAG;
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
    private boolean attachedToWindow;
    private StaticLayout authorLayout;
    private int authorX;
    private boolean autoPlayingVideo;
    private int availableTimeWidth;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage = new ImageReceiver();
    private boolean avatarPressed;
    private int backgroundDrawableLeft;
    private int backgroundDrawableRight;
    private int backgroundWidth = 100;
    private ArrayList<BotButton> botButtons = new ArrayList();
    private HashMap<String, BotButton> botButtonsByData = new HashMap();
    private HashMap<String, BotButton> botButtonsByPosition = new HashMap();
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
    private boolean checkOnlyButtonPressed;
    private AvatarDrawable contactAvatarDrawable;
    private float controlsAlpha = 1.0f;
    private int currentAccount = UserConfig.selectedAccount;
    private Drawable currentBackgroundDrawable;
    private CharSequence currentCaption;
    private Chat currentChat;
    private Chat currentForwardChannel;
    private String currentForwardNameString;
    private User currentForwardUser;
    private int currentMapProvider;
    private MessageObject currentMessageObject;
    private GroupedMessages currentMessagesGroup;
    private String currentNameString;
    private FileLocation currentPhoto;
    private String currentPhotoFilter;
    private String currentPhotoFilterThumb;
    private PhotoSize currentPhotoObject;
    private PhotoSize currentPhotoObjectThumb;
    private GroupedMessagePosition currentPosition;
    private PhotoSize currentReplyPhoto;
    private String currentTimeString;
    private String currentUrl;
    private User currentUser;
    private User currentViaBotUser;
    private String currentViewsString;
    private WebFile currentWebFile;
    private ChatMessageCellDelegate delegate;
    private RectF deleteProgressRect = new RectF();
    private StaticLayout descriptionLayout;
    private int descriptionX;
    private int descriptionY;
    private boolean disallowLongPress;
    private StaticLayout docTitleLayout;
    private int docTitleOffsetX;
    private int docTitleWidth;
    private Document documentAttach;
    private int documentAttachType;
    private boolean drawBackground = true;
    private boolean drawForwardedName;
    private boolean drawImageButton;
    private boolean drawInstantView;
    private int drawInstantViewType;
    private boolean drawJoinChannelView;
    private boolean drawJoinGroupView;
    private boolean drawName;
    private boolean drawNameLayout;
    private boolean drawPhotoImage;
    private boolean drawPinnedBottom;
    private boolean drawPinnedTop;
    private boolean drawRadialCheckBackground;
    private boolean drawShareButton;
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
    private float[] forwardNameOffsetX = new float[2];
    private boolean forwardNamePressed;
    private int forwardNameX;
    private int forwardNameY;
    private StaticLayout[] forwardedNameLayout = new StaticLayout[2];
    private int forwardedNameWidth;
    private boolean fullyDraw;
    private boolean gamePreviewPressed;
    private boolean groupPhotoInvisible;
    private GroupedMessages groupedMessagesToSet;
    private boolean hasEmbed;
    private boolean hasGamePreview;
    private boolean hasInvoicePreview;
    private boolean hasLinkPreview;
    private int hasMiniProgress;
    private boolean hasNewLineForTime;
    private boolean hasOldCaptionPreview;
    private int highlightProgress;
    private int imageBackgroundColor;
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
    private StaticLayout instantViewLayout;
    private int instantWidth;
    private Runnable invalidateRunnable = new Runnable() {
        public void run() {
            ChatMessageCell.this.checkLocationExpired();
            if (ChatMessageCell.this.locationExpired) {
                ChatMessageCell.this.invalidate();
                ChatMessageCell.this.scheduledInvalidate = false;
                return;
            }
            ChatMessageCell.this.invalidate(((int) ChatMessageCell.this.rect.left) - 5, ((int) ChatMessageCell.this.rect.top) - 5, ((int) ChatMessageCell.this.rect.right) + 5, ((int) ChatMessageCell.this.rect.bottom) + 5);
            if (ChatMessageCell.this.scheduledInvalidate) {
                AndroidUtilities.runOnUIThread(ChatMessageCell.this.invalidateRunnable, 1000);
            }
        }
    };
    private boolean isAvatarVisible;
    public boolean isChat;
    private boolean isCheckPressed = true;
    private boolean isHighlighted;
    private boolean isHighlightedAnimated;
    private boolean isPressed;
    private boolean isSmallImage;
    private int keyboardHeight;
    private long lastAnimationTime;
    private long lastControlsAlphaChangeTime;
    private int lastDeleteDate;
    private int lastHeight;
    private long lastHighlightProgressTime;
    private TL_poll lastPoll;
    private ArrayList<TL_pollAnswerVoters> lastPollResults;
    private int lastPollResultsVoters;
    private int lastSendState;
    private int lastTime;
    private int lastViewsCount;
    private int lastVisibleBlockNum;
    private int layoutHeight;
    private int layoutWidth;
    private int linkBlockNum;
    private int linkPreviewHeight;
    private boolean linkPreviewPressed;
    private int linkSelectionBlockNum;
    private boolean locationExpired;
    private ImageReceiver locationImageReceiver;
    private boolean mediaBackground;
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
    private StaticLayout performerLayout;
    private int performerX;
    private ImageReceiver photoImage;
    private boolean photoNotSet;
    private StaticLayout photosCountLayout;
    private int photosCountWidth;
    private boolean pinnedBottom;
    private boolean pinnedTop;
    private float pollAnimationProgress;
    private float pollAnimationProgressTime;
    private ArrayList<PollButton> pollButtons = new ArrayList();
    private boolean pollClosed;
    private boolean pollUnvoteInProgress;
    private boolean pollVoteInProgress;
    private int pollVoteInProgressNum;
    private boolean pollVoted;
    private int pressedBotButton;
    private CharacterStyle pressedLink;
    private int pressedLinkType;
    private int[] pressedState = new int[]{16842910, 16842919};
    private int pressedVoteButton;
    private RadialProgress2 radialProgress;
    private RectF rect = new RectF();
    private ImageReceiver replyImageReceiver;
    private StaticLayout replyNameLayout;
    private float replyNameOffset;
    private int replyNameWidth;
    private boolean replyPressed;
    private int replyStartX;
    private int replyStartY;
    private StaticLayout replyTextLayout;
    private float replyTextOffset;
    private int replyTextWidth;
    private RoundVideoPlayingDrawable roundVideoPlayingDrawable;
    private boolean scheduledInvalidate;
    private Rect scrollRect = new Rect();
    private SeekBar seekBar;
    private SeekBarWaveform seekBarWaveform;
    private int seekBarX;
    private int seekBarY;
    private Drawable selectorDrawable;
    private boolean sharePressed;
    private int shareStartX;
    private int shareStartY;
    private StaticLayout siteNameLayout;
    private boolean siteNameRtl;
    private int siteNameWidth;
    private StaticLayout songLayout;
    private int songX;
    private int substractBackgroundHeight;
    private int textX;
    private int textY;
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
    private ArrayList<LinkPath> urlPath = new ArrayList();
    private ArrayList<LinkPath> urlPathCache = new ArrayList();
    private ArrayList<LinkPath> urlPathSelection = new ArrayList();
    private boolean useSeekBarWaweform;
    private int viaNameWidth;
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
    private int widthBeforeNewTimeLine;
    private int widthForButtons;

    private class BotButton {
        private int angle;
        private KeyboardButton button;
        private int height;
        private long lastUpdateTime;
        private float progressAlpha;
        private StaticLayout title;
        private int width;
        private int x;
        private int y;

        private BotButton() {
        }

        /* synthetic */ BotButton(ChatMessageCell x0, AnonymousClass1 x1) {
            this();
        }
    }

    public interface ChatMessageCellDelegate {
        boolean canPerformActions();

        void didLongPress(ChatMessageCell chatMessageCell);

        void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton);

        void didPressCancelSendButton(ChatMessageCell chatMessageCell);

        void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i);

        void didPressImage(ChatMessageCell chatMessageCell);

        void didPressInstantButton(ChatMessageCell chatMessageCell, int i);

        void didPressOther(ChatMessageCell chatMessageCell);

        void didPressReplyMessage(ChatMessageCell chatMessageCell, int i);

        void didPressShare(ChatMessageCell chatMessageCell);

        void didPressUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z);

        void didPressUserAvatar(ChatMessageCell chatMessageCell, User user);

        void didPressViaBot(ChatMessageCell chatMessageCell, String str);

        void didPressVoteButton(ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer);

        boolean isChatAdminCell(int i);

        void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2);

        boolean needPlayMessage(MessageObject messageObject);

        void videoTimerReached();
    }

    private class PollButton {
        private TL_pollAnswer answer;
        private float decimal;
        private int height;
        private int percent;
        private float percentProgress;
        private int prevPercent;
        private float prevPercentProgress;
        private StaticLayout title;
        private int x;
        private int y;

        private PollButton() {
        }

        /* synthetic */ PollButton(ChatMessageCell x0, AnonymousClass1 x1) {
            this();
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

    private void resetPressedLink(int type) {
        if (this.pressedLink == null) {
            return;
        }
        if (this.pressedLinkType == type || type == -1) {
            resetUrlPaths(false);
            this.pressedLink = null;
            this.pressedLinkType = -1;
            invalidate();
        }
    }

    private void resetUrlPaths(boolean text) {
        if (text) {
            if (!this.urlPathSelection.isEmpty()) {
                this.urlPathCache.addAll(this.urlPathSelection);
                this.urlPathSelection.clear();
            }
        } else if (!this.urlPath.isEmpty()) {
            this.urlPathCache.addAll(this.urlPath);
            this.urlPath.clear();
        }
    }

    private LinkPath obtainNewUrlPath(boolean text) {
        LinkPath linkPath;
        if (this.urlPathCache.isEmpty()) {
            linkPath = new LinkPath();
        } else {
            linkPath = (LinkPath) this.urlPathCache.get(0);
            this.urlPathCache.remove(0);
        }
        linkPath.reset();
        if (text) {
            this.urlPathSelection.add(linkPath);
        } else {
            this.urlPath.add(linkPath);
        }
        return linkPath;
    }

    private boolean checkTextBlockMotionEvent(MotionEvent event) {
        if (this.currentMessageObject.type != 0 || this.currentMessageObject.textLayoutBlocks == null || this.currentMessageObject.textLayoutBlocks.isEmpty() || !(this.currentMessageObject.messageText instanceof Spannable)) {
            return false;
        }
        if (event.getAction() == 0 || (event.getAction() == 1 && this.pressedLinkType == 1)) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x < this.textX || y < this.textY || x > this.textX + this.currentMessageObject.textWidth || y > this.textY + this.currentMessageObject.textHeight) {
                resetPressedLink(1);
            } else {
                y -= this.textY;
                int blockNum = 0;
                int a = 0;
                while (a < this.currentMessageObject.textLayoutBlocks.size() && ((TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a)).textYOffset <= ((float) y)) {
                    blockNum = a;
                    a++;
                }
                try {
                    TextLayoutBlock block = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(blockNum);
                    x = (int) (((float) x) - (((float) this.textX) - (block.isRtl() ? this.currentMessageObject.textXOffset : 0.0f)));
                    int line = block.textLayout.getLineForVertical((int) (((float) y) - block.textYOffset));
                    int off = block.textLayout.getOffsetForHorizontal(line, (float) x);
                    float left = block.textLayout.getLineLeft(line);
                    if (left <= ((float) x) && block.textLayout.getLineWidth(line) + left >= ((float) x)) {
                        Spannable buffer = this.currentMessageObject.messageText;
                        CharacterStyle[] link = (CharacterStyle[]) buffer.getSpans(off, off, ClickableSpan.class);
                        boolean isMono = false;
                        if (link == null || link.length == 0) {
                            link = (CharacterStyle[]) buffer.getSpans(off, off, URLSpanMono.class);
                            isMono = true;
                        }
                        boolean ignore = false;
                        if (link.length == 0 || !(link.length == 0 || !(link[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled)) {
                            ignore = true;
                        }
                        if (!ignore) {
                            if (event.getAction() == 0) {
                                this.pressedLink = link[0];
                                this.linkBlockNum = blockNum;
                                this.pressedLinkType = 1;
                                resetUrlPaths(false);
                                try {
                                    TextLayoutBlock nextBlock;
                                    CharacterStyle[] nextLink;
                                    Path path = obtainNewUrlPath(false);
                                    int start = buffer.getSpanStart(this.pressedLink);
                                    int end = buffer.getSpanEnd(this.pressedLink);
                                    path.setCurrentLayout(block.textLayout, start, 0.0f);
                                    block.textLayout.getSelectionPath(start, end, path);
                                    if (end >= block.charactersEnd) {
                                        a = blockNum + 1;
                                        while (a < this.currentMessageObject.textLayoutBlocks.size()) {
                                            nextBlock = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a);
                                            if (isMono) {
                                                nextLink = (CharacterStyle[]) buffer.getSpans(nextBlock.charactersOffset, nextBlock.charactersOffset, URLSpanMono.class);
                                            } else {
                                                nextLink = (CharacterStyle[]) buffer.getSpans(nextBlock.charactersOffset, nextBlock.charactersOffset, ClickableSpan.class);
                                            }
                                            if (nextLink != null && nextLink.length != 0 && nextLink[0] == this.pressedLink) {
                                                path = obtainNewUrlPath(false);
                                                path.setCurrentLayout(nextBlock.textLayout, 0, nextBlock.textYOffset - block.textYOffset);
                                                nextBlock.textLayout.getSelectionPath(0, end, path);
                                                if (end < nextBlock.charactersEnd - 1) {
                                                    break;
                                                }
                                                a++;
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                    if (start <= block.charactersOffset) {
                                        int offsetY = 0;
                                        a = blockNum - 1;
                                        while (a >= 0) {
                                            nextBlock = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a);
                                            if (isMono) {
                                                nextLink = (CharacterStyle[]) buffer.getSpans(nextBlock.charactersEnd - 1, nextBlock.charactersEnd - 1, URLSpanMono.class);
                                            } else {
                                                nextLink = (CharacterStyle[]) buffer.getSpans(nextBlock.charactersEnd - 1, nextBlock.charactersEnd - 1, ClickableSpan.class);
                                            }
                                            if (nextLink != null && nextLink.length != 0) {
                                                if (nextLink[0] == this.pressedLink) {
                                                    path = obtainNewUrlPath(false);
                                                    start = buffer.getSpanStart(this.pressedLink);
                                                    offsetY -= nextBlock.height;
                                                    path.setCurrentLayout(nextBlock.textLayout, start, (float) offsetY);
                                                    nextBlock.textLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), path);
                                                    if (start <= nextBlock.charactersOffset) {
                                                        a--;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                                invalidate();
                                return true;
                            }
                            if (link[0] == this.pressedLink) {
                                this.delegate.didPressUrl(this.currentMessageObject, this.pressedLink, false);
                                resetPressedLink(1);
                                return true;
                            }
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
        }
        return false;
    }

    private boolean checkCaptionMotionEvent(MotionEvent event) {
        if (!(this.currentCaption instanceof Spannable) || this.captionLayout == null) {
            return false;
        }
        if (event.getAction() == 0 || ((this.linkPreviewPressed || this.pressedLink != null) && event.getAction() == 1)) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x < this.captionX || x > this.captionX + this.captionWidth || y < this.captionY || y > this.captionY + this.captionHeight) {
                resetPressedLink(3);
            } else if (event.getAction() == 0) {
                try {
                    x -= this.captionX;
                    int line = this.captionLayout.getLineForVertical(y - this.captionY);
                    int off = this.captionLayout.getOffsetForHorizontal(line, (float) x);
                    float left = this.captionLayout.getLineLeft(line);
                    if (left <= ((float) x) && this.captionLayout.getLineWidth(line) + left >= ((float) x)) {
                        Spannable buffer = this.currentCaption;
                        CharacterStyle[] link = (CharacterStyle[]) buffer.getSpans(off, off, ClickableSpan.class);
                        if (link == null || link.length == 0) {
                            link = (CharacterStyle[]) buffer.getSpans(off, off, URLSpanMono.class);
                        }
                        boolean ignore = false;
                        if (link.length == 0 || !(link.length == 0 || !(link[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled)) {
                            ignore = true;
                        }
                        if (!ignore) {
                            this.pressedLink = link[0];
                            this.pressedLinkType = 3;
                            resetUrlPaths(false);
                            try {
                                LinkPath path = obtainNewUrlPath(false);
                                int start = buffer.getSpanStart(this.pressedLink);
                                path.setCurrentLayout(this.captionLayout, start, 0.0f);
                                this.captionLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), path);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                            if (!(this.currentMessagesGroup == null || getParent() == null)) {
                                ((ViewGroup) getParent()).invalidate();
                            }
                            invalidate();
                            return true;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            } else if (this.pressedLinkType == 3) {
                this.delegate.didPressUrl(this.currentMessageObject, this.pressedLink, false);
                resetPressedLink(3);
                return true;
            }
        }
        return false;
    }

    private boolean checkGameMotionEvent(MotionEvent event) {
        if (!this.hasGamePreview) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            if (this.drawPhotoImage && this.drawImageButton && this.buttonState != -1 && x >= this.buttonX && x <= this.buttonX + AndroidUtilities.dp(48.0f) && y >= this.buttonY && y <= this.buttonY + AndroidUtilities.dp(48.0f) && this.radialProgress.getIcon() != 4) {
                this.buttonPressed = 1;
                invalidate();
                return true;
            } else if (this.drawPhotoImage && this.photoImage.isInsideImage((float) x, (float) y)) {
                this.gamePreviewPressed = true;
                return true;
            } else if (this.descriptionLayout != null && y >= this.descriptionY) {
                try {
                    x -= (this.unmovedTextX + AndroidUtilities.dp(10.0f)) + this.descriptionX;
                    int line = this.descriptionLayout.getLineForVertical(y - this.descriptionY);
                    int off = this.descriptionLayout.getOffsetForHorizontal(line, (float) x);
                    float left = this.descriptionLayout.getLineLeft(line);
                    if (left <= ((float) x) && this.descriptionLayout.getLineWidth(line) + left >= ((float) x)) {
                        Spannable buffer = this.currentMessageObject.linkDescription;
                        ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                        boolean ignore = false;
                        if (link.length == 0 || !(link.length == 0 || !(link[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled)) {
                            ignore = true;
                        }
                        if (!ignore) {
                            this.pressedLink = link[0];
                            this.linkBlockNum = -10;
                            this.pressedLinkType = 2;
                            resetUrlPaths(false);
                            try {
                                LinkPath path = obtainNewUrlPath(false);
                                int start = buffer.getSpanStart(this.pressedLink);
                                path.setCurrentLayout(this.descriptionLayout, start, 0.0f);
                                this.descriptionLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), path);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                            invalidate();
                            return true;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
        } else if (event.getAction() == 1) {
            if (this.pressedLinkType != 2 && !this.gamePreviewPressed && this.buttonPressed == 0) {
                resetPressedLink(2);
            } else if (this.buttonPressed != 0) {
                this.buttonPressed = 0;
                playSoundEffect(0);
                didPressButton(true, false);
                invalidate();
            } else if (this.pressedLink != null) {
                if (this.pressedLink instanceof URLSpan) {
                    Browser.openUrl(getContext(), ((URLSpan) this.pressedLink).getURL());
                } else if (this.pressedLink instanceof ClickableSpan) {
                    ((ClickableSpan) this.pressedLink).onClick(this);
                }
                resetPressedLink(2);
            } else {
                this.gamePreviewPressed = false;
                for (int a = 0; a < this.botButtons.size(); a++) {
                    BotButton button = (BotButton) this.botButtons.get(a);
                    if (button.button instanceof TL_keyboardButtonGame) {
                        playSoundEffect(0);
                        this.delegate.didPressBotButton(this, button.button);
                        invalidate();
                        break;
                    }
                }
                resetPressedLink(2);
                return true;
            }
        }
        return false;
    }

    private boolean checkLinkPreviewMotionEvent(MotionEvent event) {
        if (this.currentMessageObject.type != 0 || !this.hasLinkPreview) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (x >= this.unmovedTextX && x <= this.unmovedTextX + this.backgroundWidth && y >= this.textY + this.currentMessageObject.textHeight) {
            if (y <= AndroidUtilities.dp((float) ((this.drawInstantView ? 46 : 0) + 8)) + (this.linkPreviewHeight + (this.textY + this.currentMessageObject.textHeight))) {
                WebPage webPage;
                if (event.getAction() == 0) {
                    if (this.descriptionLayout != null && y >= this.descriptionY) {
                        try {
                            int checkX = x - ((this.unmovedTextX + AndroidUtilities.dp(10.0f)) + this.descriptionX);
                            int checkY = y - this.descriptionY;
                            if (checkY <= this.descriptionLayout.getHeight()) {
                                int line = this.descriptionLayout.getLineForVertical(checkY);
                                int off = this.descriptionLayout.getOffsetForHorizontal(line, (float) checkX);
                                float left = this.descriptionLayout.getLineLeft(line);
                                if (left <= ((float) checkX) && this.descriptionLayout.getLineWidth(line) + left >= ((float) checkX)) {
                                    Spannable buffer = this.currentMessageObject.linkDescription;
                                    ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                                    boolean ignore = false;
                                    if (link.length == 0 || !(link.length == 0 || !(link[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled)) {
                                        ignore = true;
                                    }
                                    if (!ignore) {
                                        this.pressedLink = link[0];
                                        this.linkBlockNum = -10;
                                        this.pressedLinkType = 2;
                                        resetUrlPaths(false);
                                        try {
                                            Path path = obtainNewUrlPath(false);
                                            int start = buffer.getSpanStart(this.pressedLink);
                                            path.setCurrentLayout(this.descriptionLayout, start, 0.0f);
                                            this.descriptionLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), path);
                                        } catch (Throwable e) {
                                            FileLog.e(e);
                                        }
                                        invalidate();
                                        return true;
                                    }
                                }
                            }
                        } catch (Throwable e2) {
                            FileLog.e(e2);
                        }
                    }
                    if (this.pressedLink == null) {
                        int side = AndroidUtilities.dp(48.0f);
                        boolean area2 = false;
                        if (this.miniButtonState >= 0) {
                            int offset = AndroidUtilities.dp(27.0f);
                            area2 = x >= this.buttonX + offset && x <= (this.buttonX + offset) + side && y >= this.buttonY + offset && y <= (this.buttonY + offset) + side;
                        }
                        if (area2) {
                            this.miniButtonPressed = 1;
                            invalidate();
                            return true;
                        } else if (this.drawVideoImageButton && this.buttonState != -1 && x >= this.videoButtonX && x <= (this.videoButtonX + AndroidUtilities.dp(34.0f)) + Math.max(this.infoWidth, this.docTitleWidth) && y >= this.videoButtonY && y <= this.videoButtonY + AndroidUtilities.dp(30.0f)) {
                            this.videoButtonPressed = 1;
                            invalidate();
                            return true;
                        } else if (this.drawPhotoImage && this.drawImageButton && this.buttonState != -1 && ((!this.checkOnlyButtonPressed && this.photoImage.isInsideImage((float) x, (float) y)) || (x >= this.buttonX && x <= this.buttonX + AndroidUtilities.dp(48.0f) && y >= this.buttonY && y <= this.buttonY + AndroidUtilities.dp(48.0f) && this.radialProgress.getIcon() != 4))) {
                            this.buttonPressed = 1;
                            invalidate();
                            return true;
                        } else if (this.drawInstantView) {
                            this.instantPressed = true;
                            if (VERSION.SDK_INT >= 21 && this.selectorDrawable != null && this.selectorDrawable.getBounds().contains(x, y)) {
                                this.selectorDrawable.setState(this.pressedState);
                                this.selectorDrawable.setHotspot((float) x, (float) y);
                                this.instantButtonPressed = true;
                            }
                            invalidate();
                            return true;
                        } else if (this.documentAttachType != 1 && this.drawPhotoImage && this.photoImage.isInsideImage((float) x, (float) y)) {
                            this.linkPreviewPressed = true;
                            webPage = this.currentMessageObject.messageOwner.media.webpage;
                            if (this.documentAttachType != 2 || this.buttonState != -1 || !SharedConfig.autoplayGifs || (this.photoImage.getAnimation() != null && TextUtils.isEmpty(webPage.embed_url))) {
                                return true;
                            }
                            this.linkPreviewPressed = false;
                            return false;
                        }
                    }
                } else if (event.getAction() == 1) {
                    if (this.instantPressed) {
                        if (this.delegate != null) {
                            this.delegate.didPressInstantButton(this, this.drawInstantViewType);
                        }
                        playSoundEffect(0);
                        if (VERSION.SDK_INT >= 21 && this.selectorDrawable != null) {
                            this.selectorDrawable.setState(StateSet.NOTHING);
                        }
                        this.instantButtonPressed = false;
                        this.instantPressed = false;
                        invalidate();
                    } else if (this.pressedLinkType != 2 && this.buttonPressed == 0 && this.miniButtonPressed == 0 && this.videoButtonPressed == 0 && !this.linkPreviewPressed) {
                        resetPressedLink(2);
                    } else if (this.videoButtonPressed == 1) {
                        this.videoButtonPressed = 0;
                        playSoundEffect(0);
                        didPressButton(true, true);
                        invalidate();
                    } else if (this.buttonPressed != 0) {
                        this.buttonPressed = 0;
                        playSoundEffect(0);
                        if (this.drawVideoImageButton) {
                            didClickedImage();
                        } else {
                            didPressButton(true, false);
                        }
                        invalidate();
                    } else if (this.miniButtonPressed != 0) {
                        this.miniButtonPressed = 0;
                        playSoundEffect(0);
                        didPressMiniButton(true);
                        invalidate();
                    } else if (this.pressedLink != null) {
                        if (this.pressedLink instanceof URLSpan) {
                            Browser.openUrl(getContext(), ((URLSpan) this.pressedLink).getURL());
                        } else if (this.pressedLink instanceof ClickableSpan) {
                            ((ClickableSpan) this.pressedLink).onClick(this);
                        }
                        resetPressedLink(2);
                    } else {
                        if (this.documentAttachType == 7) {
                            if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isMessagePaused()) {
                                this.delegate.needPlayMessage(this.currentMessageObject);
                            } else {
                                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject);
                            }
                        } else if (this.documentAttachType != 2 || !this.drawImageButton) {
                            webPage = this.currentMessageObject.messageOwner.media.webpage;
                            if (webPage != null && !TextUtils.isEmpty(webPage.embed_url)) {
                                this.delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.title, webPage.url, webPage.embed_width, webPage.embed_height);
                            } else if (this.buttonState == -1 || this.buttonState == 3) {
                                this.delegate.didPressImage(this);
                                playSoundEffect(0);
                            } else if (webPage != null) {
                                Browser.openUrl(getContext(), webPage.url);
                            }
                        } else if (this.buttonState == -1) {
                            if (SharedConfig.autoplayGifs) {
                                this.delegate.didPressImage(this);
                            } else {
                                this.buttonState = 2;
                                this.currentMessageObject.gifState = 1.0f;
                                this.photoImage.setAllowStartAnimation(false);
                                this.photoImage.stopAnimation();
                                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                                invalidate();
                                playSoundEffect(0);
                            }
                        } else if (this.buttonState == 2 || this.buttonState == 0) {
                            didPressButton(true, false);
                            playSoundEffect(0);
                        }
                        resetPressedLink(2);
                        return true;
                    }
                } else if (event.getAction() == 2 && this.instantButtonPressed && VERSION.SDK_INT >= 21 && this.selectorDrawable != null) {
                    this.selectorDrawable.setHotspot((float) x, (float) y);
                }
            }
        }
        return false;
    }

    private boolean checkPollButtonMotionEvent(MotionEvent event) {
        if (this.currentMessageObject.eventId != 0 || this.pollVoted || this.pollClosed || this.pollVoteInProgress || this.pollUnvoteInProgress || this.pollButtons.isEmpty() || this.currentMessageObject.type != 17 || !this.currentMessageObject.isSent()) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            this.pressedVoteButton = -1;
            int a = 0;
            while (a < this.pollButtons.size()) {
                PollButton button = (PollButton) this.pollButtons.get(a);
                int y2 = (button.y + this.namesOffset) - AndroidUtilities.dp(13.0f);
                if (x < button.x || x > (button.x + this.backgroundWidth) - AndroidUtilities.dp(31.0f) || y < y2 || y > (button.height + y2) + AndroidUtilities.dp(26.0f)) {
                    a++;
                } else {
                    this.pressedVoteButton = a;
                    if (VERSION.SDK_INT >= 21 && this.selectorDrawable != null) {
                        this.selectorDrawable.setBounds(button.x - AndroidUtilities.dp(9.0f), y2, (button.x + this.backgroundWidth) - AndroidUtilities.dp(22.0f), (button.height + y2) + AndroidUtilities.dp(26.0f));
                        this.selectorDrawable.setState(this.pressedState);
                        this.selectorDrawable.setHotspot((float) x, (float) y);
                    }
                    invalidate();
                    return true;
                }
            }
            return false;
        } else if (event.getAction() == 1) {
            if (this.pressedVoteButton == -1) {
                return false;
            }
            playSoundEffect(0);
            if (VERSION.SDK_INT >= 21 && this.selectorDrawable != null) {
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
            this.pollVoteInProgressNum = this.pressedVoteButton;
            this.pollVoteInProgress = true;
            this.voteCurrentProgressTime = 0.0f;
            this.firstCircleLength = true;
            this.voteCurrentCircleLength = 360.0f;
            this.voteRisingCircleLength = false;
            this.delegate.didPressVoteButton(this, ((PollButton) this.pollButtons.get(this.pressedVoteButton)).answer);
            this.pressedVoteButton = -1;
            invalidate();
            return false;
        } else if (event.getAction() != 2 || this.pressedVoteButton == -1 || VERSION.SDK_INT < 21 || this.selectorDrawable == null) {
            return false;
        } else {
            this.selectorDrawable.setHotspot((float) x, (float) y);
            return false;
        }
    }

    private boolean checkInstantButtonMotionEvent(MotionEvent event) {
        if (!this.drawInstantView || this.currentMessageObject.type == 0) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            if (this.drawInstantView && this.instantButtonRect.contains((float) x, (float) y)) {
                this.instantPressed = true;
                if (VERSION.SDK_INT >= 21 && this.selectorDrawable != null && this.selectorDrawable.getBounds().contains(x, y)) {
                    this.selectorDrawable.setState(this.pressedState);
                    this.selectorDrawable.setHotspot((float) x, (float) y);
                    this.instantButtonPressed = true;
                }
                invalidate();
                return true;
            }
        } else if (event.getAction() == 1) {
            if (this.instantPressed) {
                if (this.delegate != null) {
                    this.delegate.didPressInstantButton(this, this.drawInstantViewType);
                }
                playSoundEffect(0);
                if (VERSION.SDK_INT >= 21 && this.selectorDrawable != null) {
                    this.selectorDrawable.setState(StateSet.NOTHING);
                }
                this.instantButtonPressed = false;
                this.instantPressed = false;
                invalidate();
            }
        } else if (event.getAction() == 2 && this.instantButtonPressed && VERSION.SDK_INT >= 21 && this.selectorDrawable != null) {
            this.selectorDrawable.setHotspot((float) x, (float) y);
        }
        return false;
    }

    private boolean checkOtherButtonMotionEvent(MotionEvent event) {
        boolean allow;
        if (this.currentMessageObject.type == 16) {
            allow = true;
        } else {
            allow = false;
        }
        if (!allow) {
            if ((this.documentAttachType != 1 && this.currentMessageObject.type != 12 && this.documentAttachType != 5 && this.documentAttachType != 4 && this.documentAttachType != 2 && this.currentMessageObject.type != 8) || this.hasGamePreview || this.hasInvoicePreview) {
                allow = false;
            } else {
                allow = true;
            }
        }
        if (!allow) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        if (event.getAction() == 0) {
            if (this.currentMessageObject.type == 16) {
                if (x >= this.otherX && x <= this.otherX + AndroidUtilities.dp(235.0f) && y >= this.otherY - AndroidUtilities.dp(14.0f) && y <= this.otherY + AndroidUtilities.dp(50.0f)) {
                    this.otherPressed = true;
                    result = true;
                    invalidate();
                }
            } else if (x >= this.otherX - AndroidUtilities.dp(20.0f) && x <= this.otherX + AndroidUtilities.dp(20.0f) && y >= this.otherY - AndroidUtilities.dp(4.0f) && y <= this.otherY + AndroidUtilities.dp(30.0f)) {
                this.otherPressed = true;
                result = true;
                invalidate();
            }
        } else if (event.getAction() == 1 && this.otherPressed) {
            this.otherPressed = false;
            playSoundEffect(0);
            this.delegate.didPressOther(this);
            invalidate();
            result = true;
        }
        return result;
    }

    private boolean checkPhotoImageMotionEvent(MotionEvent event) {
        if (!this.drawPhotoImage && this.documentAttachType != 1) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        if (event.getAction() == 0) {
            boolean area2 = false;
            int side = AndroidUtilities.dp(48.0f);
            if (this.miniButtonState >= 0) {
                int offset = AndroidUtilities.dp(27.0f);
                if (x < this.buttonX + offset || x > (this.buttonX + offset) + side || y < this.buttonY + offset || y > (this.buttonY + offset) + side) {
                    area2 = false;
                } else {
                    area2 = true;
                }
            }
            if (area2) {
                this.miniButtonPressed = 1;
                invalidate();
                result = true;
            } else if (this.buttonState != -1 && this.radialProgress.getIcon() != 4 && x >= this.buttonX && x <= this.buttonX + side && y >= this.buttonY && y <= this.buttonY + side) {
                this.buttonPressed = 1;
                invalidate();
                result = true;
            } else if (this.drawVideoImageButton && this.buttonState != -1 && x >= this.videoButtonX && x <= (this.videoButtonX + AndroidUtilities.dp(34.0f)) + Math.max(this.infoWidth, this.docTitleWidth) && y >= this.videoButtonY && y <= this.videoButtonY + AndroidUtilities.dp(30.0f)) {
                this.videoButtonPressed = 1;
                invalidate();
                result = true;
            } else if (this.documentAttachType == 1) {
                if (x >= this.photoImage.getImageX() && x <= (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(50.0f) && y >= this.photoImage.getImageY() && y <= this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                    this.imagePressed = true;
                    result = true;
                }
            } else if (!(this.currentMessageObject.type == 13 && this.currentMessageObject.getInputStickerSet() == null)) {
                if (x >= this.photoImage.getImageX() && x <= this.photoImage.getImageX() + this.photoImage.getImageWidth() && y >= this.photoImage.getImageY() && y <= this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                    this.imagePressed = true;
                    result = true;
                }
                if (this.currentMessageObject.type == 12 && MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)) == null) {
                    this.imagePressed = false;
                    result = false;
                }
            }
            if (this.imagePressed) {
                if (this.currentMessageObject.isSendError()) {
                    this.imagePressed = false;
                    result = false;
                } else if (this.currentMessageObject.type == 8 && this.buttonState == -1 && SharedConfig.autoplayGifs && this.photoImage.getAnimation() == null) {
                    this.imagePressed = false;
                    result = false;
                } else if (this.currentMessageObject.type == 5 && this.buttonState != -1) {
                    this.imagePressed = false;
                    result = false;
                }
            }
        } else if (event.getAction() == 1) {
            if (this.videoButtonPressed == 1) {
                this.videoButtonPressed = 0;
                playSoundEffect(0);
                didPressButton(true, true);
                invalidate();
            } else if (this.buttonPressed == 1) {
                this.buttonPressed = 0;
                playSoundEffect(0);
                if (this.drawVideoImageButton) {
                    didClickedImage();
                } else {
                    didPressButton(true, false);
                }
                invalidate();
            } else if (this.miniButtonPressed == 1) {
                this.miniButtonPressed = 0;
                playSoundEffect(0);
                didPressMiniButton(true);
                invalidate();
            } else if (this.imagePressed) {
                this.imagePressed = false;
                if (this.buttonState == -1 || this.buttonState == 2 || this.buttonState == 3 || this.drawVideoImageButton) {
                    playSoundEffect(0);
                    didClickedImage();
                } else if (this.buttonState == 0 && this.documentAttachType == 1) {
                    playSoundEffect(0);
                    didPressButton(true, false);
                }
                invalidate();
            }
        }
        return result;
    }

    private boolean checkAudioMotionEvent(MotionEvent event) {
        if (this.documentAttachType != 3 && this.documentAttachType != 5) {
            return false;
        }
        boolean result;
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (this.useSeekBarWaweform) {
            result = this.seekBarWaveform.onTouch(event.getAction(), (event.getX() - ((float) this.seekBarX)) - ((float) AndroidUtilities.dp(13.0f)), event.getY() - ((float) this.seekBarY));
        } else {
            result = this.seekBar.onTouch(event.getAction(), event.getX() - ((float) this.seekBarX), event.getY() - ((float) this.seekBarY));
        }
        if (result) {
            if (!this.useSeekBarWaweform && event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (this.useSeekBarWaweform && !this.seekBarWaveform.isStartDraging() && event.getAction() == 1) {
                didPressButton(true, false);
            }
            this.disallowLongPress = true;
            invalidate();
            return result;
        }
        int side = AndroidUtilities.dp(36.0f);
        boolean area = false;
        boolean area2 = false;
        if (this.miniButtonState >= 0) {
            int offset = AndroidUtilities.dp(27.0f);
            area2 = x >= this.buttonX + offset && x <= (this.buttonX + offset) + side && y >= this.buttonY + offset && y <= (this.buttonY + offset) + side;
        }
        if (!area2) {
            if (this.buttonState == 0 || this.buttonState == 1 || this.buttonState == 2) {
                if (x >= this.buttonX - AndroidUtilities.dp(12.0f) && x <= (this.buttonX - AndroidUtilities.dp(12.0f)) + this.backgroundWidth) {
                    if (y >= (this.drawInstantView ? this.buttonY : this.namesOffset + this.mediaOffsetY)) {
                        if (y <= (this.drawInstantView ? this.buttonY + side : (this.namesOffset + this.mediaOffsetY) + AndroidUtilities.dp(82.0f))) {
                            area = true;
                        }
                    }
                }
                area = false;
            } else {
                area = x >= this.buttonX && x <= this.buttonX + side && y >= this.buttonY && y <= this.buttonY + side;
            }
        }
        if (event.getAction() == 0) {
            if (!area && !area2) {
                return result;
            }
            if (area) {
                this.buttonPressed = 1;
            } else {
                this.miniButtonPressed = 1;
            }
            invalidate();
            return true;
        } else if (this.buttonPressed != 0) {
            if (event.getAction() == 1) {
                this.buttonPressed = 0;
                playSoundEffect(0);
                didPressButton(true, false);
                invalidate();
                return result;
            } else if (event.getAction() == 3) {
                this.buttonPressed = 0;
                invalidate();
                return result;
            } else if (event.getAction() != 2 || area) {
                return result;
            } else {
                this.buttonPressed = 0;
                invalidate();
                return result;
            }
        } else if (this.miniButtonPressed == 0) {
            return result;
        } else {
            if (event.getAction() == 1) {
                this.miniButtonPressed = 0;
                playSoundEffect(0);
                didPressMiniButton(true);
                invalidate();
                return result;
            } else if (event.getAction() == 3) {
                this.miniButtonPressed = 0;
                invalidate();
                return result;
            } else if (event.getAction() != 2 || area2) {
                return result;
            } else {
                this.miniButtonPressed = 0;
                invalidate();
                return result;
            }
        }
    }

    private boolean checkBotButtonMotionEvent(MotionEvent event) {
        if (this.botButtons.isEmpty() || this.currentMessageObject.eventId != 0) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            int addX;
            if (this.currentMessageObject.isOutOwner()) {
                addX = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                addX = this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
            }
            int a = 0;
            while (a < this.botButtons.size()) {
                BotButton button = (BotButton) this.botButtons.get(a);
                int y2 = (button.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                if (x < button.x + addX || x > (button.x + addX) + button.width || y < y2 || y > button.height + y2) {
                    a++;
                } else {
                    this.pressedBotButton = a;
                    invalidate();
                    return true;
                }
            }
            return false;
        } else if (event.getAction() != 1 || this.pressedBotButton == -1) {
            return false;
        } else {
            playSoundEffect(0);
            this.delegate.didPressBotButton(this, ((BotButton) this.botButtons.get(this.pressedBotButton)).button);
            this.pressedBotButton = -1;
            invalidate();
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.currentMessageObject == null || !this.delegate.canPerformActions()) {
            return super.onTouchEvent(event);
        }
        this.disallowLongPress = false;
        boolean result = checkTextBlockMotionEvent(event);
        if (!result) {
            result = checkOtherButtonMotionEvent(event);
        }
        if (!result) {
            result = checkCaptionMotionEvent(event);
        }
        if (!result) {
            result = checkAudioMotionEvent(event);
        }
        if (!result) {
            result = checkLinkPreviewMotionEvent(event);
        }
        if (!result) {
            result = checkInstantButtonMotionEvent(event);
        }
        if (!result) {
            result = checkGameMotionEvent(event);
        }
        if (!result) {
            result = checkPhotoImageMotionEvent(event);
        }
        if (!result) {
            result = checkBotButtonMotionEvent(event);
        }
        if (!result) {
            result = checkPollButtonMotionEvent(event);
        }
        if (event.getAction() == 3) {
            this.buttonPressed = 0;
            this.miniButtonPressed = 0;
            this.pressedBotButton = -1;
            this.pressedVoteButton = -1;
            this.linkPreviewPressed = false;
            this.otherPressed = false;
            this.imagePressed = false;
            this.gamePreviewPressed = false;
            this.instantButtonPressed = false;
            this.instantPressed = false;
            if (VERSION.SDK_INT >= 21 && this.selectorDrawable != null) {
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
            result = false;
            resetPressedLink(-1);
        }
        updateRadialProgressBackground();
        if (!this.disallowLongPress && result && event.getAction() == 0) {
            startCheckLongPress();
        }
        if (!(event.getAction() == 0 || event.getAction() == 2)) {
            cancelCheckLongPress();
        }
        if (result) {
            return result;
        }
        float x = event.getX();
        float y = event.getY();
        int replyEnd;
        if (event.getAction() != 0) {
            if (event.getAction() != 2) {
                cancelCheckLongPress();
            }
            if (this.avatarPressed) {
                if (event.getAction() == 1) {
                    this.avatarPressed = false;
                    playSoundEffect(0);
                    if (this.delegate == null) {
                        return result;
                    }
                    if (this.currentUser != null) {
                        this.delegate.didPressUserAvatar(this, this.currentUser);
                        return result;
                    } else if (this.currentChat == null) {
                        return result;
                    } else {
                        this.delegate.didPressChannelAvatar(this, this.currentChat, 0);
                        return result;
                    }
                } else if (event.getAction() == 3) {
                    this.avatarPressed = false;
                    return result;
                } else if (event.getAction() != 2 || !this.isAvatarVisible || this.avatarImage.isInsideImage(x, ((float) getTop()) + y)) {
                    return result;
                } else {
                    this.avatarPressed = false;
                    return result;
                }
            } else if (this.forwardNamePressed) {
                if (event.getAction() == 1) {
                    this.forwardNamePressed = false;
                    playSoundEffect(0);
                    if (this.delegate == null) {
                        return result;
                    }
                    if (this.currentForwardChannel != null) {
                        this.delegate.didPressChannelAvatar(this, this.currentForwardChannel, this.currentMessageObject.messageOwner.fwd_from.channel_post);
                        return result;
                    } else if (this.currentForwardUser == null) {
                        return result;
                    } else {
                        this.delegate.didPressUserAvatar(this, this.currentForwardUser);
                        return result;
                    }
                } else if (event.getAction() == 3) {
                    this.forwardNamePressed = false;
                    return result;
                } else if (event.getAction() != 2) {
                    return result;
                } else {
                    if (x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                        return result;
                    }
                    this.forwardNamePressed = false;
                    return result;
                }
            } else if (this.forwardBotPressed) {
                if (event.getAction() == 1) {
                    this.forwardBotPressed = false;
                    playSoundEffect(0);
                    if (this.delegate == null) {
                        return result;
                    }
                    this.delegate.didPressViaBot(this, this.currentViaBotUser != null ? this.currentViaBotUser.username : this.currentMessageObject.messageOwner.via_bot_name);
                    return result;
                } else if (event.getAction() == 3) {
                    this.forwardBotPressed = false;
                    return result;
                } else if (event.getAction() != 2) {
                    return result;
                } else {
                    if (!this.drawForwardedName || this.forwardedNameLayout[0] == null) {
                        if (x >= this.nameX + ((float) this.viaNameWidth) && x <= (this.nameX + ((float) this.viaNameWidth)) + ((float) this.viaWidth) && y >= this.nameY - ((float) AndroidUtilities.dp(4.0f)) && y <= this.nameY + ((float) AndroidUtilities.dp(20.0f))) {
                            return result;
                        }
                        this.forwardBotPressed = false;
                        return result;
                    } else if (x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                        return result;
                    } else {
                        this.forwardBotPressed = false;
                        return result;
                    }
                }
            } else if (this.replyPressed) {
                if (event.getAction() == 1) {
                    this.replyPressed = false;
                    playSoundEffect(0);
                    if (this.delegate == null) {
                        return result;
                    }
                    this.delegate.didPressReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
                    return result;
                } else if (event.getAction() == 3) {
                    this.replyPressed = false;
                    return result;
                } else if (event.getAction() != 2) {
                    return result;
                } else {
                    if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                        replyEnd = this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth);
                    } else {
                        replyEnd = this.replyStartX + this.backgroundDrawableRight;
                    }
                    if (x >= ((float) this.replyStartX) && x <= ((float) replyEnd) && y >= ((float) this.replyStartY) && y <= ((float) (this.replyStartY + AndroidUtilities.dp(35.0f)))) {
                        return result;
                    }
                    this.replyPressed = false;
                    return result;
                }
            } else if (!this.sharePressed) {
                return result;
            } else {
                if (event.getAction() == 1) {
                    this.sharePressed = false;
                    playSoundEffect(0);
                    if (this.delegate != null) {
                        this.delegate.didPressShare(this);
                    }
                } else if (event.getAction() == 3) {
                    this.sharePressed = false;
                } else if (event.getAction() == 2 && (x < ((float) this.shareStartX) || x > ((float) (this.shareStartX + AndroidUtilities.dp(40.0f))) || y < ((float) this.shareStartY) || y > ((float) (this.shareStartY + AndroidUtilities.dp(32.0f))))) {
                    this.sharePressed = false;
                }
                invalidate();
                return result;
            }
        } else if (this.delegate != null && !this.delegate.canPerformActions()) {
            return result;
        } else {
            if (this.isAvatarVisible && this.avatarImage.isInsideImage(x, ((float) getTop()) + y)) {
                this.avatarPressed = true;
                result = true;
            } else if (this.drawForwardedName && this.forwardedNameLayout[0] != null && x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                if (this.viaWidth == 0 || x < ((float) ((this.forwardNameX + this.viaNameWidth) + AndroidUtilities.dp(4.0f)))) {
                    this.forwardNamePressed = true;
                } else {
                    this.forwardBotPressed = true;
                }
                result = true;
            } else if (this.drawNameLayout && this.nameLayout != null && this.viaWidth != 0 && x >= this.nameX + ((float) this.viaNameWidth) && x <= (this.nameX + ((float) this.viaNameWidth)) + ((float) this.viaWidth) && y >= this.nameY - ((float) AndroidUtilities.dp(4.0f)) && y <= this.nameY + ((float) AndroidUtilities.dp(20.0f))) {
                this.forwardBotPressed = true;
                result = true;
            } else if (this.drawShareButton && x >= ((float) this.shareStartX) && x <= ((float) (this.shareStartX + AndroidUtilities.dp(40.0f))) && y >= ((float) this.shareStartY) && y <= ((float) (this.shareStartY + AndroidUtilities.dp(32.0f)))) {
                this.sharePressed = true;
                result = true;
                invalidate();
            } else if (this.replyNameLayout != null) {
                if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                    replyEnd = this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth);
                } else {
                    replyEnd = this.replyStartX + this.backgroundDrawableRight;
                }
                if (x >= ((float) this.replyStartX) && x <= ((float) replyEnd) && y >= ((float) this.replyStartY) && y <= ((float) (this.replyStartY + AndroidUtilities.dp(35.0f)))) {
                    this.replyPressed = true;
                    result = true;
                }
            }
            if (!result) {
                return result;
            }
            startCheckLongPress();
            return result;
        }
    }

    public void updatePlayingMessageProgress() {
        if (this.currentMessageObject != null) {
            int duration;
            int a;
            DocumentAttribute attribute;
            String timeString;
            if (this.documentAttachType == 4) {
                if (!PhotoViewer.isPlayingMessage(this.currentMessageObject) && !MediaController.getInstance().isGoingToShowMessageObject(this.currentMessageObject)) {
                    duration = this.currentMessageObject.getDuration();
                    if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                        duration = (int) (((float) duration) - (((float) duration) * this.currentMessageObject.audioProgress));
                    } else {
                        AnimatedFileDrawable animation = this.photoImage.getAnimation();
                        if (animation != null) {
                            int videoDuration = animation.getDurationMs() / 1000;
                            if (videoDuration != 0) {
                                duration = videoDuration;
                            }
                            if (duration != 0) {
                                duration -= animation.getCurrentProgressMs() / 1000;
                            }
                            if (this.delegate != null && animation.getCurrentProgressMs() >= 3000) {
                                this.delegate.videoTimerReached();
                            }
                        }
                    }
                    int seconds = duration - ((duration / 60) * 60);
                    if (this.lastTime != duration) {
                        String str = String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)});
                        this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(str));
                        this.infoLayout = new StaticLayout(str, Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        this.lastTime = duration;
                    }
                }
            } else if (this.currentMessageObject.isRoundVideo()) {
                duration = 0;
                Document document = this.currentMessageObject.getDocument();
                for (a = 0; a < document.attributes.size(); a++) {
                    attribute = (DocumentAttribute) document.attributes.get(a);
                    if (attribute instanceof TL_documentAttributeVideo) {
                        duration = attribute.duration;
                        break;
                    }
                }
                if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    duration = Math.max(0, duration - this.currentMessageObject.audioProgressSec);
                    if (!(this.currentMessageObject.mediaExists || this.currentMessageObject.attachPathExists)) {
                        this.currentMessageObject.mediaExists = true;
                        updateButtonState(true, true, false);
                    }
                }
                if (this.lastTime != duration) {
                    this.lastTime = duration;
                    timeString = String.format("%02d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)});
                    this.timeWidthAudio = (int) Math.ceil((double) Theme.chat_timePaint.measureText(timeString));
                    this.durationLayout = new StaticLayout(timeString, Theme.chat_timePaint, this.timeWidthAudio, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
                duration = 0;
                if (this.documentAttachType == 3) {
                    if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                        duration = this.currentMessageObject.audioProgressSec;
                    } else {
                        for (a = 0; a < this.documentAttach.attributes.size(); a++) {
                            attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                            if (attribute instanceof TL_documentAttributeAudio) {
                                duration = attribute.duration;
                                break;
                            }
                        }
                    }
                    if (this.lastTime != duration) {
                        this.lastTime = duration;
                        timeString = String.format("%02d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)});
                        this.timeWidthAudio = (int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(timeString));
                        this.durationLayout = new StaticLayout(timeString, Theme.chat_audioTimePaint, this.timeWidthAudio, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }
                } else {
                    int currentProgress = 0;
                    duration = this.currentMessageObject.getDuration();
                    if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                        currentProgress = this.currentMessageObject.audioProgressSec;
                    }
                    if (this.lastTime != currentProgress) {
                        this.lastTime = currentProgress;
                        if (duration == 0) {
                            timeString = String.format("%d:%02d / -:--", new Object[]{Integer.valueOf(currentProgress / 60), Integer.valueOf(currentProgress % 60)});
                        } else {
                            timeString = String.format("%d:%02d / %d:%02d", new Object[]{Integer.valueOf(currentProgress / 60), Integer.valueOf(currentProgress % 60), Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)});
                        }
                        this.durationLayout = new StaticLayout(timeString, Theme.chat_audioTimePaint, (int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(timeString)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }
                }
                invalidate();
            }
        }
    }

    public void setFullyDraw(boolean draw) {
        this.fullyDraw = draw;
    }

    public void setVisiblePart(int position, int height) {
        if (this.currentMessageObject != null && this.currentMessageObject.textLayoutBlocks != null) {
            position -= this.textY;
            int newFirst = -1;
            int newLast = -1;
            int newCount = 0;
            int startBlock = 0;
            int a = 0;
            while (a < this.currentMessageObject.textLayoutBlocks.size() && ((TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a)).textYOffset <= ((float) position)) {
                startBlock = a;
                a++;
            }
            for (a = startBlock; a < this.currentMessageObject.textLayoutBlocks.size(); a++) {
                TextLayoutBlock block = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a);
                float y = block.textYOffset;
                if (intersect(y, ((float) block.height) + y, (float) position, (float) (position + height))) {
                    if (newFirst == -1) {
                        newFirst = a;
                    }
                    newLast = a;
                    newCount++;
                } else if (y > ((float) position)) {
                    break;
                }
            }
            if (this.lastVisibleBlockNum != newLast || this.firstVisibleBlockNum != newFirst || this.totalVisibleBlocksCount != newCount) {
                this.lastVisibleBlockNum = newLast;
                this.firstVisibleBlockNum = newFirst;
                this.totalVisibleBlocksCount = newCount;
                invalidate();
            }
        }
    }

    private boolean intersect(float left1, float right1, float left2, float right2) {
        if (left1 <= left2) {
            if (right1 >= left2) {
                return true;
            }
            return false;
        } else if (left1 > right2) {
            return false;
        } else {
            return true;
        }
    }

    public static StaticLayout generateStaticLayout(CharSequence text, TextPaint paint, int maxWidth, int smallWidth, int linesCount, int maxLines) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        int addedChars = 0;
        StaticLayout layout = new StaticLayout(text, paint, smallWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int a = 0;
        while (a < linesCount) {
            Directions directions = layout.getLineDirections(a);
            if (layout.getLineLeft(a) != 0.0f || layout.isRtlCharAt(layout.getLineStart(a)) || layout.isRtlCharAt(layout.getLineEnd(a))) {
                maxWidth = smallWidth;
            }
            int pos = layout.getLineEnd(a);
            if (pos != text.length()) {
                pos--;
                if (stringBuilder.charAt(pos + addedChars) == ' ') {
                    stringBuilder.replace(pos + addedChars, (pos + addedChars) + 1, "\n");
                } else {
                    if (stringBuilder.charAt(pos + addedChars) != 10) {
                        stringBuilder.insert(pos + addedChars, "\n");
                        addedChars++;
                    }
                }
                if (a == layout.getLineCount() - 1 || a == maxLines - 1) {
                    break;
                }
                a++;
            } else {
                break;
            }
        }
        return StaticLayoutEx.createStaticLayout(stringBuilder, paint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.dp(1.0f), false, TruncateAt.END, maxWidth, maxLines, true);
    }

    private void didClickedImage() {
        if (this.currentMessageObject.type == 1 || this.currentMessageObject.type == 13) {
            if (this.buttonState == -1) {
                this.delegate.didPressImage(this);
            } else if (this.buttonState == 0) {
                didPressButton(true, false);
            }
        } else if (this.currentMessageObject.type == 12) {
            this.delegate.didPressUserAvatar(this, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)));
        } else if (this.currentMessageObject.type == 5) {
            if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isMessagePaused()) {
                this.delegate.needPlayMessage(this.currentMessageObject);
            } else {
                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject);
            }
        } else if (this.currentMessageObject.type == 8) {
            if (this.buttonState == -1) {
                if (SharedConfig.autoplayGifs) {
                    this.delegate.didPressImage(this);
                    return;
                }
                this.buttonState = 2;
                this.currentMessageObject.gifState = 1.0f;
                this.photoImage.setAllowStartAnimation(false);
                this.photoImage.stopAnimation();
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            } else if (this.buttonState == 2 || this.buttonState == 0) {
                didPressButton(true, false);
            }
        } else if (this.documentAttachType == 4) {
            if (this.buttonState == -1 || this.drawVideoImageButton) {
                this.delegate.didPressImage(this);
            } else if (this.buttonState == 0 || this.buttonState == 3) {
                didPressButton(true, false);
            }
        } else if (this.currentMessageObject.type == 4) {
            this.delegate.didPressImage(this);
        } else if (this.documentAttachType == 1) {
            if (this.buttonState == -1) {
                this.delegate.didPressImage(this);
            }
        } else if (this.documentAttachType == 2) {
            if (this.buttonState == -1) {
                WebPage webPage = this.currentMessageObject.messageOwner.media.webpage;
                if (webPage == null) {
                    return;
                }
                if (webPage.embed_url == null || webPage.embed_url.length() == 0) {
                    Browser.openUrl(getContext(), webPage.url);
                } else {
                    this.delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.description, webPage.url, webPage.embed_width, webPage.embed_height);
                }
            }
        } else if (this.hasInvoicePreview && this.buttonState == -1) {
            this.delegate.didPressImage(this);
        }
    }

    private void updateSecretTimeText(MessageObject messageObject) {
        if (messageObject != null && messageObject.needDrawBluredPreview()) {
            String str = messageObject.getSecretTimeString();
            if (str != null) {
                this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(str));
                this.infoLayout = new StaticLayout(TextUtils.ellipsize(str, Theme.chat_infoPaint, (float) this.infoWidth, TruncateAt.END), Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                invalidate();
            }
        }
    }

    private boolean isPhotoDataChanged(MessageObject object) {
        if (object.type == 0 || object.type == 14) {
            return false;
        }
        if (object.type == 4) {
            if (this.currentUrl == null) {
                return true;
            }
            String url;
            double lat = object.messageOwner.media.geo.lat;
            double lon = object.messageOwner.media.geo._long;
            if (object.messageOwner.media instanceof TL_messageMediaGeoLive) {
                double rad = ((double) NUM) / 3.141592653589793d;
                url = AndroidUtilities.formapMapUrl(this.currentAccount, ((1.5707963267948966d - (2.0d * Math.atan(Math.exp((((double) (Math.round(((double) NUM) - ((Math.log((1.0d + Math.sin((3.141592653589793d * lat) / 180.0d)) / (1.0d - Math.sin((3.141592653589793d * lat) / 180.0d))) * rad) / 2.0d)) - ((long) (AndroidUtilities.dp(10.3f) << 6)))) - ((double) NUM)) / rad)))) * 180.0d) / 3.141592653589793d, lon, (int) (((float) (this.backgroundWidth - AndroidUtilities.dp(21.0f))) / AndroidUtilities.density), (int) (((float) AndroidUtilities.dp(195.0f)) / AndroidUtilities.density), false, 15);
            } else if (TextUtils.isEmpty(object.messageOwner.media.title)) {
                url = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, (int) (((float) (this.backgroundWidth - AndroidUtilities.dp(12.0f))) / AndroidUtilities.density), (int) (((float) AndroidUtilities.dp(195.0f)) / AndroidUtilities.density), true, 15);
            } else {
                url = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, (int) (((float) (this.backgroundWidth - AndroidUtilities.dp(21.0f))) / AndroidUtilities.density), (int) (((float) AndroidUtilities.dp(195.0f)) / AndroidUtilities.density), true, 15);
            }
            if (url.equals(this.currentUrl)) {
                return false;
            }
            return true;
        } else if (this.currentPhotoObject == null || (this.currentPhotoObject.location instanceof TL_fileLocationUnavailable)) {
            return object.type == 1 || object.type == 5 || object.type == 3 || object.type == 8 || object.type == 13;
        } else {
            if (this.currentMessageObject == null || !this.photoNotSet) {
                return false;
            }
            return FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists();
        }
    }

    private boolean isUserDataChanged() {
        boolean z = false;
        if (this.currentMessageObject != null && !this.hasLinkPreview && this.currentMessageObject.messageOwner.media != null && (this.currentMessageObject.messageOwner.media.webpage instanceof TL_webPage)) {
            return true;
        }
        if (this.currentMessageObject == null || (this.currentUser == null && this.currentChat == null)) {
            return false;
        }
        if (this.lastSendState != this.currentMessageObject.messageOwner.send_state || this.lastDeleteDate != this.currentMessageObject.messageOwner.destroyTime || this.lastViewsCount != this.currentMessageObject.messageOwner.views) {
            return true;
        }
        updateCurrentUserAndChat();
        FileLocation newPhoto = null;
        if (this.isAvatarVisible) {
            if (this.currentUser != null && this.currentUser.photo != null) {
                newPhoto = this.currentUser.photo.photo_small;
            } else if (!(this.currentChat == null || this.currentChat.photo == null)) {
                newPhoto = this.currentChat.photo.photo_small;
            }
        }
        if (this.replyTextLayout == null && this.currentMessageObject.replyMessageObject != null) {
            return true;
        }
        if (this.currentPhoto == null && newPhoto != null) {
            return true;
        }
        if (this.currentPhoto != null && newPhoto == null) {
            return true;
        }
        if (this.currentPhoto != null && newPhoto != null && (this.currentPhoto.local_id != newPhoto.local_id || this.currentPhoto.volume_id != newPhoto.volume_id)) {
            return true;
        }
        PhotoSize newReplyPhoto = null;
        if (this.replyNameLayout != null) {
            PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 40);
            if (!(photoSize == null || this.currentMessageObject.replyMessageObject.type == 13)) {
                newReplyPhoto = photoSize;
            }
        }
        if (this.currentReplyPhoto == null && newReplyPhoto != null) {
            return true;
        }
        String newNameString = null;
        if (this.drawName && this.isChat && !this.currentMessageObject.isOutOwner()) {
            if (this.currentUser != null) {
                newNameString = UserObject.getUserName(this.currentUser);
            } else if (this.currentChat != null) {
                newNameString = this.currentChat.title;
            }
        }
        if (this.currentNameString == null && newNameString != null) {
            return true;
        }
        if (this.currentNameString != null && newNameString == null) {
            return true;
        }
        if (this.currentNameString != null && newNameString != null && !this.currentNameString.equals(newNameString)) {
            return true;
        }
        if (!this.drawForwardedName || !this.currentMessageObject.needDrawForwarded()) {
            return false;
        }
        newNameString = this.currentMessageObject.getForwardedName();
        if ((this.currentForwardNameString == null && newNameString != null) || ((this.currentForwardNameString != null && newNameString == null) || !(this.currentForwardNameString == null || newNameString == null || this.currentForwardNameString.equals(newNameString)))) {
            z = true;
        }
        return z;
    }

    public ImageReceiver getPhotoImage() {
        return this.photoImage;
    }

    public int getNoSoundIconCenterX() {
        return this.noSoundCenterX;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
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
    }

    protected void onAttachedToWindow() {
        float f = 1.0f;
        super.onAttachedToWindow();
        if (this.messageObjectToSet != null) {
            setMessageContent(this.messageObjectToSet, this.groupedMessagesToSet, this.bottomNearToSet, this.topNearToSet);
            this.messageObjectToSet = null;
            this.groupedMessagesToSet = null;
        }
        this.attachedToWindow = true;
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
        if (this.currentMessageObject != null && (this.currentMessageObject.isRoundVideo() || this.currentMessageObject.isVideo())) {
            checkVideoPlayback(true);
        }
        if (this.documentAttachType == 4 && this.autoPlayingVideo) {
            float f2;
            this.animatingNoSoundPlaying = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            this.animatingNoSound = 0;
            if (this.animatingNoSoundPlaying) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            this.animatingNoSoundProgress = f2;
            return;
        }
        this.animatingNoSoundPlaying = false;
        this.animatingNoSoundProgress = 0.0f;
        if (!(this.documentAttachType == 4 && this.drawVideoSize)) {
            f = 0.0f;
        }
        this.animatingDrawVideoImageButtonProgress = f;
    }

    /* JADX WARNING: Removed duplicated region for block: B:269:0x07b6  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x07d4  */
    /* JADX WARNING: Removed duplicated region for block: B:894:0x1ce9  */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x1cb2  */
    /* JADX WARNING: Removed duplicated region for block: B:895:0x1ceb  */
    /* JADX WARNING: Removed duplicated region for block: B:887:0x1cbd  */
    /* JADX WARNING: Removed duplicated region for block: B:890:0x1cda  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x1053  */
    /* JADX WARNING: Removed duplicated region for block: B:902:0x1d07  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x1077  */
    /* JADX WARNING: Removed duplicated region for block: B:918:0x1d63  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x1083  */
    /* JADX WARNING: Removed duplicated region for block: B:919:0x1d66  */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x1099  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x10b3  */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x1e25  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x10d2  */
    /* JADX WARNING: Removed duplicated region for block: B:943:0x1e51  */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x117d  */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x11a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1008:0x216e  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0e5d  */
    /* JADX WARNING: Removed duplicated region for block: B:717:0x1685  */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0fc5  */
    /* JADX WARNING: Removed duplicated region for block: B:874:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0fcd  */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x1011  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x1053  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x1077  */
    /* JADX WARNING: Removed duplicated region for block: B:902:0x1d07  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x1083  */
    /* JADX WARNING: Removed duplicated region for block: B:918:0x1d63  */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x1099  */
    /* JADX WARNING: Removed duplicated region for block: B:919:0x1d66  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x10b3  */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x10c1  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x10d2  */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x1e25  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x1120  */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x117d  */
    /* JADX WARNING: Removed duplicated region for block: B:943:0x1e51  */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x11a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1008:0x216e  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x1200  */
    /* JADX WARNING: Removed duplicated region for block: B:2160:0x48fb A:{SYNTHETIC, Splitter: B:2160:0x48fb} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x136f A:{Catch:{ Exception -> 0x491a }} */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x13a2 A:{SYNTHETIC, Splitter: B:641:0x13a2} */
    /* JADX WARNING: Removed duplicated region for block: B:2167:0x4929  */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x150f A:{Catch:{ Exception -> 0x492c }} */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x1545 A:{Catch:{ Exception -> 0x4943 }} */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x15af A:{Catch:{ Exception -> 0x4943 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2176:0x4958  */
    /* JADX WARNING: Removed duplicated region for block: B:2181:0x4984  */
    /* JADX WARNING: Removed duplicated region for block: B:2277:0x4d34  */
    /* JADX WARNING: Removed duplicated region for block: B:2276:0x4d23  */
    /* JADX WARNING: Removed duplicated region for block: B:2255:0x4CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:2290:0x4d82  */
    /* JADX WARNING: Removed duplicated region for block: B:2258:0x4CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0e5d  */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0fc5  */
    /* JADX WARNING: Removed duplicated region for block: B:717:0x1685  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0fcd  */
    /* JADX WARNING: Removed duplicated region for block: B:874:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x1009 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x1011  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x1053  */
    /* JADX WARNING: Removed duplicated region for block: B:902:0x1d07  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x1077  */
    /* JADX WARNING: Removed duplicated region for block: B:918:0x1d63  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x1083  */
    /* JADX WARNING: Removed duplicated region for block: B:919:0x1d66  */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x1099  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x10b3  */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x10c1  */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x1e25  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x10d2  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x1120  */
    /* JADX WARNING: Removed duplicated region for block: B:943:0x1e51  */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x117d  */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x11a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1008:0x216e  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x1200  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x136f A:{Catch:{ Exception -> 0x491a }} */
    /* JADX WARNING: Removed duplicated region for block: B:2160:0x48fb A:{SYNTHETIC, Splitter: B:2160:0x48fb} */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x13a2 A:{SYNTHETIC, Splitter: B:641:0x13a2} */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x150f A:{Catch:{ Exception -> 0x492c }} */
    /* JADX WARNING: Removed duplicated region for block: B:2167:0x4929  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x1545 A:{Catch:{ Exception -> 0x4943 }} */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x15af A:{Catch:{ Exception -> 0x4943 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2176:0x4958  */
    /* JADX WARNING: Removed duplicated region for block: B:2181:0x4984  */
    /* JADX WARNING: Removed duplicated region for block: B:2184:0x499d  */
    /* JADX WARNING: Removed duplicated region for block: B:2245:0x4c4d  */
    /* JADX WARNING: Removed duplicated region for block: B:2276:0x4d23  */
    /* JADX WARNING: Removed duplicated region for block: B:2277:0x4d34  */
    /* JADX WARNING: Removed duplicated region for block: B:2255:0x4CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:2258:0x4CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:2290:0x4d82  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x4cbe  */
    /* JADX WARNING: Removed duplicated region for block: B:2385:? A:{SYNTHETIC, RETURN, SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:2268:0x4cd2  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0e5d  */
    /* JADX WARNING: Removed duplicated region for block: B:717:0x1685  */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0fc5  */
    /* JADX WARNING: Removed duplicated region for block: B:874:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0fcd  */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x1009 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x1011  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x1053  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x1077  */
    /* JADX WARNING: Removed duplicated region for block: B:902:0x1d07  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x1083  */
    /* JADX WARNING: Removed duplicated region for block: B:918:0x1d63  */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x1099  */
    /* JADX WARNING: Removed duplicated region for block: B:919:0x1d66  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x10b3  */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x10c1  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x10d2  */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x1e25  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x1120  */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x117d  */
    /* JADX WARNING: Removed duplicated region for block: B:943:0x1e51  */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x11a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1008:0x216e  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x1200  */
    /* JADX WARNING: Removed duplicated region for block: B:2160:0x48fb A:{SYNTHETIC, Splitter: B:2160:0x48fb} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x136f A:{Catch:{ Exception -> 0x491a }} */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x13a2 A:{SYNTHETIC, Splitter: B:641:0x13a2} */
    /* JADX WARNING: Removed duplicated region for block: B:2167:0x4929  */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x150f A:{Catch:{ Exception -> 0x492c }} */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x1545 A:{Catch:{ Exception -> 0x4943 }} */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x15af A:{Catch:{ Exception -> 0x4943 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2176:0x4958  */
    /* JADX WARNING: Removed duplicated region for block: B:2181:0x4984  */
    /* JADX WARNING: Removed duplicated region for block: B:2184:0x499d  */
    /* JADX WARNING: Removed duplicated region for block: B:2245:0x4c4d  */
    /* JADX WARNING: Removed duplicated region for block: B:2277:0x4d34  */
    /* JADX WARNING: Removed duplicated region for block: B:2276:0x4d23  */
    /* JADX WARNING: Removed duplicated region for block: B:2255:0x4CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:2290:0x4d82  */
    /* JADX WARNING: Removed duplicated region for block: B:2258:0x4CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x4cbe  */
    /* JADX WARNING: Removed duplicated region for block: B:2268:0x4cd2  */
    /* JADX WARNING: Removed duplicated region for block: B:2385:? A:{SYNTHETIC, RETURN, SKIP} */
    /* JADX WARNING: Missing block: B:259:0x0799, code:
            if (r181.equals("article") != false) goto L_0x079b;
     */
    /* JADX WARNING: Missing block: B:272:0x07ca, code:
            if (r181.equals("article") != false) goto L_0x07cc;
     */
    /* JADX WARNING: Missing block: B:388:0x0b6f, code:
            if ("telegram_album".equals(r189) == false) goto L_0x05b0;
     */
    /* JADX WARNING: Missing block: B:562:0x1046, code:
            if (r194.documentAttachType != 8) goto L_0x1d04;
     */
    private void setMessageContent(org.telegram.messenger.MessageObject r195, org.telegram.messenger.MessageObject.GroupedMessages r196, boolean r197, boolean r198) {
        /*
        r194 = this;
        r4 = r195.checkLayout();
        if (r4 != 0) goto L_0x0016;
    L_0x0006:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x001b;
    L_0x000c:
        r0 = r194;
        r4 = r0.lastHeight;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        if (r4 == r6) goto L_0x001b;
    L_0x0016:
        r4 = 0;
        r0 = r194;
        r0.currentMessageObject = r4;
    L_0x001b:
        r0 = r194;
        r4 = r0.currentMessageObject;
        if (r4 == 0) goto L_0x002f;
    L_0x0021:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.getId();
        r6 = r195.getId();
        if (r4 == r6) goto L_0x0978;
    L_0x002f:
        r129 = 1;
    L_0x0031:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r0 = r195;
        if (r4 != r0) goto L_0x003f;
    L_0x0039:
        r0 = r195;
        r4 = r0.forceUpdate;
        if (r4 == 0) goto L_0x097c;
    L_0x003f:
        r128 = 1;
    L_0x0041:
        r0 = r194;
        r4 = r0.currentMessageObject;
        if (r4 == 0) goto L_0x0062;
    L_0x0047:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.getId();
        r6 = r195.getId();
        if (r4 != r6) goto L_0x0062;
    L_0x0055:
        r0 = r194;
        r4 = r0.lastSendState;
        r6 = 3;
        if (r4 != r6) goto L_0x0062;
    L_0x005c:
        r4 = r195.isSent();
        if (r4 != 0) goto L_0x0076;
    L_0x0062:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r0 = r195;
        if (r4 != r0) goto L_0x0980;
    L_0x006a:
        r4 = r194.isUserDataChanged();
        if (r4 != 0) goto L_0x0076;
    L_0x0070:
        r0 = r194;
        r4 = r0.photoNotSet;
        if (r4 == 0) goto L_0x0980;
    L_0x0076:
        r86 = 1;
    L_0x0078:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r0 = r196;
        if (r0 == r4) goto L_0x0984;
    L_0x0080:
        r98 = 1;
    L_0x0082:
        r149 = 0;
        if (r128 != 0) goto L_0x0112;
    L_0x0086:
        r0 = r195;
        r4 = r0.type;
        r6 = 17;
        if (r4 != r6) goto L_0x0112;
    L_0x008e:
        r135 = 0;
        r133 = 0;
        r136 = 0;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r4 == 0) goto L_0x00be;
    L_0x009e:
        r0 = r195;
        r4 = r0.messageOwner;
        r0 = r4.media;
        r127 = r0;
        r127 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r127;
        r0 = r127;
        r4 = r0.results;
        r0 = r4.results;
        r135 = r0;
        r0 = r127;
        r0 = r0.poll;
        r133 = r0;
        r0 = r127;
        r4 = r0.results;
        r0 = r4.total_voters;
        r136 = r0;
    L_0x00be:
        if (r135 == 0) goto L_0x00d0;
    L_0x00c0:
        r0 = r194;
        r4 = r0.lastPollResults;
        if (r4 == 0) goto L_0x00d0;
    L_0x00c6:
        r0 = r194;
        r4 = r0.lastPollResultsVoters;
        r0 = r136;
        if (r0 == r4) goto L_0x00d0;
    L_0x00ce:
        r149 = 1;
    L_0x00d0:
        if (r149 != 0) goto L_0x00dc;
    L_0x00d2:
        r0 = r194;
        r4 = r0.lastPollResults;
        r0 = r135;
        if (r0 == r4) goto L_0x00dc;
    L_0x00da:
        r149 = 1;
    L_0x00dc:
        if (r149 != 0) goto L_0x00f4;
    L_0x00de:
        r0 = r194;
        r4 = r0.lastPoll;
        r0 = r133;
        if (r4 == r0) goto L_0x00f4;
    L_0x00e6:
        r0 = r194;
        r4 = r0.lastPoll;
        r4 = r4.closed;
        r0 = r133;
        r6 = r0.closed;
        if (r4 == r6) goto L_0x00f4;
    L_0x00f2:
        r149 = 1;
    L_0x00f4:
        if (r149 == 0) goto L_0x0112;
    L_0x00f6:
        r0 = r194;
        r4 = r0.attachedToWindow;
        if (r4 == 0) goto L_0x0112;
    L_0x00fc:
        r4 = 0;
        r0 = r194;
        r0.pollAnimationProgressTime = r4;
        r0 = r194;
        r4 = r0.pollVoted;
        if (r4 == 0) goto L_0x0112;
    L_0x0107:
        r4 = r195.isVoted();
        if (r4 != 0) goto L_0x0112;
    L_0x010d:
        r4 = 1;
        r0 = r194;
        r0.pollUnvoteInProgress = r4;
    L_0x0112:
        if (r98 != 0) goto L_0x013b;
    L_0x0114:
        if (r196 == 0) goto L_0x013b;
    L_0x0116:
        r0 = r196;
        r4 = r0.messages;
        r4 = r4.size();
        r6 = 1;
        if (r4 <= r6) goto L_0x0988;
    L_0x0121:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.positions;
        r0 = r194;
        r6 = r0.currentMessageObject;
        r134 = r4.get(r6);
        r134 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r134;
    L_0x0131:
        r0 = r194;
        r4 = r0.currentPosition;
        r0 = r134;
        if (r0 == r4) goto L_0x098c;
    L_0x0139:
        r98 = 1;
    L_0x013b:
        if (r128 != 0) goto L_0x0159;
    L_0x013d:
        if (r86 != 0) goto L_0x0159;
    L_0x013f:
        if (r98 != 0) goto L_0x0159;
    L_0x0141:
        if (r149 != 0) goto L_0x0159;
    L_0x0143:
        r4 = r194.isPhotoDataChanged(r195);
        if (r4 != 0) goto L_0x0159;
    L_0x0149:
        r0 = r194;
        r4 = r0.pinnedBottom;
        r0 = r197;
        if (r4 != r0) goto L_0x0159;
    L_0x0151:
        r0 = r194;
        r4 = r0.pinnedTop;
        r0 = r198;
        if (r4 == r0) goto L_0x4cb8;
    L_0x0159:
        r0 = r197;
        r1 = r194;
        r1.pinnedBottom = r0;
        r0 = r198;
        r1 = r194;
        r1.pinnedTop = r0;
        r0 = r195;
        r1 = r194;
        r1.currentMessageObject = r0;
        r0 = r196;
        r1 = r194;
        r1.currentMessagesGroup = r0;
        r4 = -2;
        r0 = r194;
        r0.lastTime = r4;
        r4 = 0;
        r0 = r194;
        r0.isHighlightedAnimated = r4;
        r4 = -1;
        r0 = r194;
        r0.widthBeforeNewTimeLine = r4;
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        if (r4 == 0) goto L_0x0990;
    L_0x0186:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r6 = 1;
        if (r4 <= r6) goto L_0x0990;
    L_0x0193:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.positions;
        r0 = r194;
        r6 = r0.currentMessageObject;
        r4 = r4.get(r6);
        r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4;
        r0 = r194;
        r0.currentPosition = r4;
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 != 0) goto L_0x01b2;
    L_0x01ad:
        r4 = 0;
        r0 = r194;
        r0.currentMessagesGroup = r4;
    L_0x01b2:
        r0 = r194;
        r4 = r0.pinnedTop;
        if (r4 == 0) goto L_0x099c;
    L_0x01b8:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x01c8;
    L_0x01be:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x099c;
    L_0x01c8:
        r4 = 1;
    L_0x01c9:
        r0 = r194;
        r0.drawPinnedTop = r4;
        r0 = r194;
        r4 = r0.pinnedBottom;
        if (r4 == 0) goto L_0x099f;
    L_0x01d3:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x01e3;
    L_0x01d9:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 == 0) goto L_0x099f;
    L_0x01e3:
        r4 = 1;
    L_0x01e4:
        r0 = r194;
        r0.drawPinnedBottom = r4;
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setCrossfadeWithOldImage(r6);
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.send_state;
        r0 = r194;
        r0.lastSendState = r4;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.destroyTime;
        r0 = r194;
        r0.lastDeleteDate = r4;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.views;
        r0 = r194;
        r0.lastViewsCount = r4;
        r4 = 0;
        r0 = r194;
        r0.isPressed = r4;
        r4 = 0;
        r0 = r194;
        r0.gamePreviewPressed = r4;
        r4 = 1;
        r0 = r194;
        r0.isCheckPressed = r4;
        r4 = 0;
        r0 = r194;
        r0.hasNewLineForTime = r4;
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x09a2;
    L_0x0228:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x09a2;
    L_0x022e:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x09a2;
    L_0x0234:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x0242;
    L_0x023a:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.edge;
        if (r4 == 0) goto L_0x09a2;
    L_0x0242:
        r4 = 1;
    L_0x0243:
        r0 = r194;
        r0.isAvatarVisible = r4;
        r4 = 0;
        r0 = r194;
        r0.wasLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.drwaShareGoIcon = r4;
        r4 = 0;
        r0 = r194;
        r0.groupPhotoInvisible = r4;
        r4 = 0;
        r0 = r194;
        r0.animatingDrawVideoImageButton = r4;
        r4 = 0;
        r0 = r194;
        r0.drawVideoSize = r4;
        r4 = 0;
        r0 = r194;
        r0.canStreamVideo = r4;
        r4 = 0;
        r0 = r194;
        r0.animatingNoSound = r4;
        r4 = r194.checkNeedDrawShareButton(r195);
        r0 = r194;
        r0.drawShareButton = r4;
        r4 = 0;
        r0 = r194;
        r0.replyNameLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.adminLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.checkOnlyButtonPressed = r4;
        r4 = 0;
        r0 = r194;
        r0.replyTextLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.hasEmbed = r4;
        r4 = 0;
        r0 = r194;
        r0.autoPlayingVideo = r4;
        r4 = 0;
        r0 = r194;
        r0.replyNameWidth = r4;
        r4 = 0;
        r0 = r194;
        r0.replyTextWidth = r4;
        r4 = 0;
        r0 = r194;
        r0.viaWidth = r4;
        r4 = 0;
        r0 = r194;
        r0.viaNameWidth = r4;
        r4 = 0;
        r0 = r194;
        r0.addedCaptionHeight = r4;
        r4 = 0;
        r0 = r194;
        r0.currentReplyPhoto = r4;
        r4 = 0;
        r0 = r194;
        r0.currentUser = r4;
        r4 = 0;
        r0 = r194;
        r0.currentChat = r4;
        r4 = 0;
        r0 = r194;
        r0.currentViaBotUser = r4;
        r4 = 0;
        r0 = r194;
        r0.instantViewLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.drawNameLayout = r4;
        r0 = r194;
        r4 = r0.scheduledInvalidate;
        if (r4 == 0) goto L_0x02d9;
    L_0x02cd:
        r0 = r194;
        r4 = r0.invalidateRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4);
        r4 = 0;
        r0 = r194;
        r0.scheduledInvalidate = r4;
    L_0x02d9:
        r4 = -1;
        r0 = r194;
        r0.resetPressedLink(r4);
        r4 = 0;
        r0 = r195;
        r0.forceUpdate = r4;
        r4 = 0;
        r0 = r194;
        r0.drawPhotoImage = r4;
        r4 = 0;
        r0 = r194;
        r0.hasLinkPreview = r4;
        r4 = 0;
        r0 = r194;
        r0.hasOldCaptionPreview = r4;
        r4 = 0;
        r0 = r194;
        r0.hasGamePreview = r4;
        r4 = 0;
        r0 = r194;
        r0.hasInvoicePreview = r4;
        r4 = 0;
        r0 = r194;
        r0.instantButtonPressed = r4;
        r0 = r194;
        r0.instantPressed = r4;
        if (r149 != 0) goto L_0x0326;
    L_0x0308:
        r4 = android.os.Build.VERSION.SDK_INT;
        r6 = 21;
        if (r4 < r6) goto L_0x0326;
    L_0x030e:
        r0 = r194;
        r4 = r0.selectorDrawable;
        if (r4 == 0) goto L_0x0326;
    L_0x0314:
        r0 = r194;
        r4 = r0.selectorDrawable;
        r6 = 0;
        r8 = 0;
        r4.setVisible(r6, r8);
        r0 = r194;
        r4 = r0.selectorDrawable;
        r6 = android.util.StateSet.NOTHING;
        r4.setState(r6);
    L_0x0326:
        r4 = 0;
        r0 = r194;
        r0.linkPreviewPressed = r4;
        r4 = 0;
        r0 = r194;
        r0.buttonPressed = r4;
        r4 = 0;
        r0 = r194;
        r0.miniButtonPressed = r4;
        r4 = -1;
        r0 = r194;
        r0.pressedBotButton = r4;
        r4 = -1;
        r0 = r194;
        r0.pressedVoteButton = r4;
        r4 = 0;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        r4 = 0;
        r0 = r194;
        r0.mediaOffsetY = r4;
        r4 = 0;
        r0 = r194;
        r0.documentAttachType = r4;
        r4 = 0;
        r0 = r194;
        r0.documentAttach = r4;
        r4 = 0;
        r0 = r194;
        r0.descriptionLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.titleLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.videoInfoLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.photosCountLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.siteNameLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.authorLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.captionLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.captionOffsetX = r4;
        r4 = 0;
        r0 = r194;
        r0.currentCaption = r4;
        r4 = 0;
        r0 = r194;
        r0.docTitleLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.drawImageButton = r4;
        r4 = 0;
        r0 = r194;
        r0.drawVideoImageButton = r4;
        r4 = 0;
        r0 = r194;
        r0.currentPhotoObject = r4;
        r4 = 0;
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
        r4 = 0;
        r0 = r194;
        r0.currentPhotoFilter = r4;
        r4 = 0;
        r0 = r194;
        r0.infoLayout = r4;
        r4 = 0;
        r0 = r194;
        r0.cancelLoading = r4;
        r4 = -1;
        r0 = r194;
        r0.buttonState = r4;
        r4 = -1;
        r0 = r194;
        r0.miniButtonState = r4;
        r4 = 0;
        r0 = r194;
        r0.hasMiniProgress = r4;
        r0 = r194;
        r4 = r0.addedForTest;
        if (r4 == 0) goto L_0x03d4;
    L_0x03bd:
        r0 = r194;
        r4 = r0.currentUrl;
        if (r4 == 0) goto L_0x03d4;
    L_0x03c3:
        r0 = r194;
        r4 = r0.currentWebFile;
        if (r4 == 0) goto L_0x03d4;
    L_0x03c9:
        r4 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r194;
        r6 = r0.currentUrl;
        r4.removeTestWebFile(r6);
    L_0x03d4:
        r4 = 0;
        r0 = r194;
        r0.addedForTest = r4;
        r4 = 0;
        r0 = r194;
        r0.currentUrl = r4;
        r4 = 0;
        r0 = r194;
        r0.currentWebFile = r4;
        r4 = 0;
        r0 = r194;
        r0.photoNotSet = r4;
        r4 = 1;
        r0 = r194;
        r0.drawBackground = r4;
        r4 = 0;
        r0 = r194;
        r0.drawName = r4;
        r4 = 0;
        r0 = r194;
        r0.useSeekBarWaweform = r4;
        r4 = 0;
        r0 = r194;
        r0.drawInstantView = r4;
        r4 = 0;
        r0 = r194;
        r0.drawInstantViewType = r4;
        r4 = 0;
        r0 = r194;
        r0.drawForwardedName = r4;
        r4 = 0;
        r0 = r194;
        r0.imageBackgroundColor = r4;
        r4 = 0;
        r0 = r194;
        r0.imageBackgroundSideColor = r4;
        r4 = 0;
        r0 = r194;
        r0.mediaBackground = r4;
        r0 = r194;
        r4 = r0.photoImage;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4.setAlpha(r6);
        if (r128 != 0) goto L_0x0422;
    L_0x0420:
        if (r86 == 0) goto L_0x0429;
    L_0x0422:
        r0 = r194;
        r4 = r0.pollButtons;
        r4.clear();
    L_0x0429:
        r79 = 0;
        r4 = 0;
        r0 = r194;
        r0.availableTimeWidth = r4;
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setForceLoading(r6);
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setNeedsQualityThumb(r6);
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setShouldGenerateQualityThumb(r6);
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setAllowDecodeSingleFrame(r6);
        r0 = r194;
        r4 = r0.photoImage;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setRoundRadius(r6);
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setColorFilter(r6);
        if (r128 == 0) goto L_0x0476;
    L_0x0467:
        r4 = 0;
        r0 = r194;
        r0.firstVisibleBlockNum = r4;
        r4 = 0;
        r0 = r194;
        r0.lastVisibleBlockNum = r4;
        r4 = 1;
        r0 = r194;
        r0.needNewVisiblePart = r4;
    L_0x0476:
        r0 = r195;
        r4 = r0.type;
        if (r4 != 0) goto L_0x2227;
    L_0x047c:
        r4 = 1;
        r0 = r194;
        r0.drawForwardedName = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x09ca;
    L_0x0487:
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x09a5;
    L_0x048d:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x09a5;
    L_0x0493:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x09a5;
    L_0x0499:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x42var_ float:122.0 double:5.54977537E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r4 = 1;
        r0 = r194;
        r0.drawName = r4;
    L_0x04aa:
        r0 = r43;
        r1 = r194;
        r1.availableTimeWidth = r0;
        r4 = r195.isRoundVideo();
        if (r4 == 0) goto L_0x04de;
    L_0x04b6:
        r0 = r194;
        r4 = r0.availableTimeWidth;
        r8 = (double) r4;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r6 = "00:00";
        r4 = r4.measureText(r6);
        r0 = (double) r4;
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x0a24;
    L_0x04d1:
        r4 = 0;
    L_0x04d2:
        r0 = (double) r4;
        r24 = r0;
        r20 = r20 + r24;
        r8 = r8 - r20;
        r4 = (int) r8;
        r0 = r194;
        r0.availableTimeWidth = r4;
    L_0x04de:
        r194.measureTime(r195);
        r0 = r194;
        r4 = r0.timeWidth;
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r176 = r4 + r6;
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x04fb;
    L_0x04f3:
        r4 = NUM; // 0x41a40000 float:20.5 double:5.44098164E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r176 = r176 + r4;
    L_0x04fb:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r4 == 0) goto L_0x0a2c;
    L_0x0505:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.game;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_game;
        if (r4 == 0) goto L_0x0a2c;
    L_0x0511:
        r4 = 1;
    L_0x0512:
        r0 = r194;
        r0.hasGamePreview = r4;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
        r0 = r194;
        r0.hasInvoicePreview = r4;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r4 == 0) goto L_0x0a2f;
    L_0x052c:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r4 == 0) goto L_0x0a2f;
    L_0x0538:
        r4 = 1;
    L_0x0539:
        r0 = r194;
        r0.hasLinkPreview = r4;
        r0 = r194;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0a32;
    L_0x0543:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.cached_page;
        if (r4 == 0) goto L_0x0a32;
    L_0x054f:
        r4 = 1;
    L_0x0550:
        r0 = r194;
        r0.drawInstantView = r4;
        r0 = r194;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0a35;
    L_0x055a:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.embed_url;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x0a35;
    L_0x056a:
        r4 = 1;
    L_0x056b:
        r0 = r194;
        r0.hasEmbed = r4;
        r168 = 0;
        r0 = r194;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0a38;
    L_0x0577:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r0 = r4.site_name;
        r166 = r0;
    L_0x0583:
        r0 = r194;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0a3c;
    L_0x0589:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r0 = r4.type;
        r189 = r0;
    L_0x0595:
        r0 = r194;
        r4 = r0.drawInstantView;
        if (r4 != 0) goto L_0x0b4a;
    L_0x059b:
        r4 = "telegram_channel";
        r0 = r189;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x0a40;
    L_0x05a6:
        r4 = 1;
        r0 = r194;
        r0.drawInstantView = r4;
        r4 = 1;
        r0 = r194;
        r0.drawInstantViewType = r4;
    L_0x05b0:
        r0 = r43;
        r1 = r194;
        r1.backgroundWidth = r0;
        r0 = r194;
        r4 = r0.hasLinkPreview;
        if (r4 != 0) goto L_0x05d2;
    L_0x05bc:
        r0 = r194;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x05d2;
    L_0x05c2:
        r0 = r194;
        r4 = r0.hasInvoicePreview;
        if (r4 != 0) goto L_0x05d2;
    L_0x05c8:
        r0 = r195;
        r4 = r0.lastLineWidth;
        r4 = r43 - r4;
        r0 = r176;
        if (r4 >= r0) goto L_0x0CLASSNAME;
    L_0x05d2:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r0 = r195;
        r6 = r0.lastLineWidth;
        r4 = java.lang.Math.max(r4, r6);
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.backgroundWidth = r4;
        r0 = r194;
        r4 = r0.backgroundWidth;
        r0 = r194;
        r6 = r0.timeWidth;
        r8 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 + r8;
        r4 = java.lang.Math.max(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x0600:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.availableTimeWidth = r4;
        r4 = r195.isRoundVideo();
        if (r4 == 0) goto L_0x063d;
    L_0x0615:
        r0 = r194;
        r4 = r0.availableTimeWidth;
        r8 = (double) r4;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r6 = "00:00";
        r4 = r4.measureText(r6);
        r0 = (double) r4;
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0630:
        r4 = 0;
    L_0x0631:
        r0 = (double) r4;
        r24 = r0;
        r20 = r20 + r24;
        r8 = r8 - r20;
        r4 = (int) r8;
        r0 = r194;
        r0.availableTimeWidth = r4;
    L_0x063d:
        r194.setMessageObjectInternal(r195);
        r0 = r195;
        r6 = r0.textWidth;
        r0 = r194;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x0650;
    L_0x064a:
        r0 = r194;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x0c7d;
    L_0x0650:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0656:
        r4 = r4 + r6;
        r0 = r194;
        r0.backgroundWidth = r4;
        r0 = r195;
        r4 = r0.textHeight;
        r6 = NUM; // 0x419CLASSNAME float:19.5 double:5.43839131E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x0684;
    L_0x0675:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.namesOffset = r4;
    L_0x0684:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r0 = r194;
        r6 = r0.nameWidth;
        r120 = java.lang.Math.max(r4, r6);
        r0 = r194;
        r4 = r0.forwardedNameWidth;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);
        r0 = r194;
        r4 = r0.replyNameWidth;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);
        r0 = r194;
        r4 = r0.replyTextWidth;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);
        r125 = 0;
        r0 = r194;
        r4 = r0.hasLinkPreview;
        if (r4 != 0) goto L_0x06c2;
    L_0x06b6:
        r0 = r194;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x06c2;
    L_0x06bc:
        r0 = r194;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x2210;
    L_0x06c2:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x0c8e;
    L_0x06c8:
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x06ce:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x06d4:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 != 0) goto L_0x0CLASSNAME;
    L_0x06de:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r4 - r6;
    L_0x06ea:
        r0 = r194;
        r4 = r0.drawShareButton;
        if (r4 == 0) goto L_0x06f7;
    L_0x06f0:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r11 = r11 - r4;
    L_0x06f7:
        r0 = r194;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0cd3;
    L_0x06fd:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.webpage;
        r188 = r0;
        r188 = (org.telegram.tgnet.TLRPC.TL_webPage) r188;
        r0 = r188;
        r7 = r0.site_name;
        r0 = r194;
        r4 = r0.drawInstantViewType;
        r6 = 6;
        if (r4 == r6) goto L_0x0cc0;
    L_0x0714:
        r0 = r188;
        r0 = r0.title;
        r178 = r0;
    L_0x071a:
        r0 = r194;
        r4 = r0.drawInstantViewType;
        r6 = 6;
        if (r4 == r6) goto L_0x0cc4;
    L_0x0721:
        r0 = r188;
        r0 = r0.author;
        r67 = r0;
    L_0x0727:
        r0 = r194;
        r4 = r0.drawInstantViewType;
        r6 = 6;
        if (r4 == r6) goto L_0x0cc8;
    L_0x072e:
        r0 = r188;
        r0 = r0.description;
        r87 = r0;
    L_0x0734:
        r0 = r188;
        r0 = r0.photo;
        r143 = r0;
        r15 = 0;
        r0 = r188;
        r0 = r0.document;
        r89 = r0;
        r0 = r188;
        r0 = r0.type;
        r181 = r0;
        r0 = r188;
        r0 = r0.duration;
        r90 = r0;
        if (r7 == 0) goto L_0x076e;
    L_0x074f:
        if (r143 == 0) goto L_0x076e;
    L_0x0751:
        r4 = r7.toLowerCase();
        r6 = "instagram";
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x076e;
    L_0x075e:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r4 = r4 / 3;
        r0 = r194;
        r6 = r0.currentMessageObject;
        r6 = r6.textWidth;
        r11 = java.lang.Math.max(r4, r6);
    L_0x076e:
        if (r168 != 0) goto L_0x0ccc;
    L_0x0770:
        r0 = r194;
        r4 = r0.drawInstantView;
        if (r4 != 0) goto L_0x0ccc;
    L_0x0776:
        if (r89 != 0) goto L_0x0ccc;
    L_0x0778:
        if (r181 == 0) goto L_0x0ccc;
    L_0x077a:
        r4 = "app";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x079b;
    L_0x0785:
        r4 = "profile";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x079b;
    L_0x0790:
        r4 = "article";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0ccc;
    L_0x079b:
        r169 = 1;
    L_0x079d:
        if (r168 != 0) goto L_0x0cd0;
    L_0x079f:
        r0 = r194;
        r4 = r0.drawInstantView;
        if (r4 != 0) goto L_0x0cd0;
    L_0x07a5:
        if (r89 != 0) goto L_0x0cd0;
    L_0x07a7:
        if (r87 == 0) goto L_0x0cd0;
    L_0x07a9:
        if (r181 == 0) goto L_0x0cd0;
    L_0x07ab:
        r4 = "app";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x07cc;
    L_0x07b6:
        r4 = "profile";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x07cc;
    L_0x07c1:
        r4 = "article";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0cd0;
    L_0x07cc:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.photoThumbs;
        if (r4 == 0) goto L_0x0cd0;
    L_0x07d4:
        r4 = 1;
    L_0x07d5:
        r0 = r194;
        r0.isSmallImage = r4;
        r187 = r15;
    L_0x07db:
        r0 = r194;
        r4 = r0.drawInstantViewType;
        r6 = 6;
        if (r4 != r6) goto L_0x07ec;
    L_0x07e2:
        r4 = "ChatBackground";
        r6 = NUM; // 0x7f0CLASSNAMEee float:1.8610194E38 double:1.0530976425E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x07ec:
        r0 = r194;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x0d59;
    L_0x07f2:
        r59 = 0;
    L_0x07f4:
        r158 = 3;
        r61 = 0;
        r115 = r11 - r59;
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.photoThumbs;
        if (r4 != 0) goto L_0x080c;
    L_0x0802:
        if (r143 == 0) goto L_0x080c;
    L_0x0804:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r6 = 1;
        r4.generateThumbs(r6);
    L_0x080c:
        if (r7 == 0) goto L_0x0891;
    L_0x080e:
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0d64 }
        r4 = r4.measureText(r7);	 Catch:{ Exception -> 0x0d64 }
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = r4 + r6;
        r8 = (double) r4;	 Catch:{ Exception -> 0x0d64 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0d64 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0d64 }
        r190 = r0;
        r6 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0d64 }
        r8 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0d64 }
        r0 = r190;
        r1 = r115;
        r9 = java.lang.Math.min(r0, r1);	 Catch:{ Exception -> 0x0d64 }
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0d64 }
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = 0;
        r13 = 0;
        r6.<init>(r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0d64 }
        r0 = r194;
        r0.siteNameLayout = r6;	 Catch:{ Exception -> 0x0d64 }
        r0 = r194;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0d64 }
        r6 = 0;
        r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x0d64 }
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x0d61;
    L_0x0846:
        r4 = 1;
    L_0x0847:
        r0 = r194;
        r0.siteNameRtl = r4;	 Catch:{ Exception -> 0x0d64 }
        r0 = r194;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0d64 }
        r0 = r194;
        r6 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0d64 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0d64 }
        r6 = r6 + -1;
        r103 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x0d64 }
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0d64 }
        r4 = r4 + r103;
        r0 = r194;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0d64 }
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0d64 }
        r4 = r4 + r103;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0d64 }
        r61 = r61 + r103;
        r0 = r194;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0d64 }
        r190 = r4.getWidth();	 Catch:{ Exception -> 0x0d64 }
        r0 = r190;
        r1 = r194;
        r1.siteNameWidth = r0;	 Catch:{ Exception -> 0x0d64 }
        r4 = r190 + r59;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0d64 }
        r4 = r190 + r59;
        r0 = r125;
        r125 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0d64 }
    L_0x0891:
        r179 = 0;
        if (r178 == 0) goto L_0x4daa;
    L_0x0895:
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0 = r194;
        r0.titleX = r4;	 Catch:{ Exception -> 0x4d93 }
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x4d93 }
        if (r4 == 0) goto L_0x08c0;
    L_0x08a2:
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x4d93 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4d93 }
        r4 = r4 + r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x4d93 }
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4d93 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4d93 }
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4d93 }
    L_0x08c0:
        r155 = 0;
        r0 = r194;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x4d93 }
        if (r4 == 0) goto L_0x08ca;
    L_0x08c8:
        if (r87 != 0) goto L_0x0d6a;
    L_0x08ca:
        r9 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x4d93 }
        r11 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x4d93 }
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4d93 }
        r13 = (float) r4;	 Catch:{ Exception -> 0x4d93 }
        r14 = 0;
        r15 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x4d93 }
        r17 = 4;
        r8 = r178;
        r10 = r115;
        r16 = r115;
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17);	 Catch:{ Exception -> 0x4d93 }
        r0 = r194;
        r0.titleLayout = r4;	 Catch:{ Exception -> 0x4d93 }
        r13 = r158;
    L_0x08ec:
        r0 = r194;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0da2 }
        r0 = r194;
        r6 = r0.titleLayout;	 Catch:{ Exception -> 0x0da2 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0da2 }
        r6 = r6 + -1;
        r103 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x0da2 }
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0da2 }
        r4 = r4 + r103;
        r0 = r194;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0da2 }
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0da2 }
        r4 = r4 + r103;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0da2 }
        r80 = 1;
        r58 = 0;
    L_0x0916:
        r0 = r194;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0da2 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0da2 }
        r0 = r58;
        if (r0 >= r4) goto L_0x0da6;
    L_0x0922:
        r0 = r194;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0da2 }
        r0 = r58;
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x0da2 }
        r0 = (int) r4;	 Catch:{ Exception -> 0x0da2 }
        r114 = r0;
        if (r114 == 0) goto L_0x0933;
    L_0x0931:
        r179 = 1;
    L_0x0933:
        r0 = r194;
        r4 = r0.titleX;	 Catch:{ Exception -> 0x0da2 }
        r6 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r4 != r6) goto L_0x0d91;
    L_0x093c:
        r0 = r114;
        r4 = -r0;
        r0 = r194;
        r0.titleX = r4;	 Catch:{ Exception -> 0x0da2 }
    L_0x0943:
        if (r114 == 0) goto L_0x0var_;
    L_0x0945:
        r0 = r194;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0da2 }
        r4 = r4.getWidth();	 Catch:{ Exception -> 0x0da2 }
        r190 = r4 - r114;
    L_0x094f:
        r0 = r58;
        r1 = r155;
        if (r0 < r1) goto L_0x095d;
    L_0x0955:
        if (r114 == 0) goto L_0x0965;
    L_0x0957:
        r0 = r194;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0da2 }
        if (r4 == 0) goto L_0x0965;
    L_0x095d:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0da2 }
        r190 = r190 + r4;
    L_0x0965:
        r4 = r190 + r59;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0da2 }
        r4 = r190 + r59;
        r0 = r125;
        r125 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0da2 }
        r58 = r58 + 1;
        goto L_0x0916;
    L_0x0978:
        r129 = 0;
        goto L_0x0031;
    L_0x097c:
        r128 = 0;
        goto L_0x0041;
    L_0x0980:
        r86 = 0;
        goto L_0x0078;
    L_0x0984:
        r98 = 0;
        goto L_0x0082;
    L_0x0988:
        r134 = 0;
        goto L_0x0131;
    L_0x098c:
        r98 = 0;
        goto L_0x013b;
    L_0x0990:
        r4 = 0;
        r0 = r194;
        r0.currentMessagesGroup = r4;
        r4 = 0;
        r0 = r194;
        r0.currentPosition = r4;
        goto L_0x01b2;
    L_0x099c:
        r4 = 0;
        goto L_0x01c9;
    L_0x099f:
        r4 = 0;
        goto L_0x01e4;
    L_0x09a2:
        r4 = 0;
        goto L_0x0243;
    L_0x09a5:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.to_id;
        r4 = r4.channel_id;
        if (r4 == 0) goto L_0x09c8;
    L_0x09af:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x09c8;
    L_0x09b5:
        r4 = 1;
    L_0x09b6:
        r0 = r194;
        r0.drawName = r4;
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        goto L_0x04aa;
    L_0x09c8:
        r4 = 0;
        goto L_0x09b6;
    L_0x09ca:
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x09f7;
    L_0x09d0:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x09f7;
    L_0x09d6:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x09f7;
    L_0x09dc:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x42var_ float:122.0 double:5.54977537E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r4 = 1;
        r0 = r194;
        r0.drawName = r4;
        goto L_0x04aa;
    L_0x09f7:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.to_id;
        r4 = r4.channel_id;
        if (r4 == 0) goto L_0x0a22;
    L_0x0a15:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x0a22;
    L_0x0a1b:
        r4 = 1;
    L_0x0a1c:
        r0 = r194;
        r0.drawName = r4;
        goto L_0x04aa;
    L_0x0a22:
        r4 = 0;
        goto L_0x0a1c;
    L_0x0a24:
        r4 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x04d2;
    L_0x0a2c:
        r4 = 0;
        goto L_0x0512;
    L_0x0a2f:
        r4 = 0;
        goto L_0x0539;
    L_0x0a32:
        r4 = 0;
        goto L_0x0550;
    L_0x0a35:
        r4 = 0;
        goto L_0x056b;
    L_0x0a38:
        r166 = 0;
        goto L_0x0583;
    L_0x0a3c:
        r189 = 0;
        goto L_0x0595;
    L_0x0a40:
        r4 = "telegram_megagroup";
        r0 = r189;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x0a57;
    L_0x0a4b:
        r4 = 1;
        r0 = r194;
        r0.drawInstantView = r4;
        r4 = 2;
        r0 = r194;
        r0.drawInstantViewType = r4;
        goto L_0x05b0;
    L_0x0a57:
        r4 = "telegram_message";
        r0 = r189;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x0a6e;
    L_0x0a62:
        r4 = 1;
        r0 = r194;
        r0.drawInstantView = r4;
        r4 = 3;
        r0 = r194;
        r0.drawInstantViewType = r4;
        goto L_0x05b0;
    L_0x0a6e:
        r4 = "telegram_background";
        r0 = r189;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x05b0;
    L_0x0a79:
        r4 = 1;
        r0 = r194;
        r0.drawInstantView = r4;
        r4 = 6;
        r0 = r194;
        r0.drawInstantViewType = r4;
        r0 = r195;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0af4 }
        r4 = r4.media;	 Catch:{ Exception -> 0x0af4 }
        r4 = r4.webpage;	 Catch:{ Exception -> 0x0af4 }
        r4 = r4.url;	 Catch:{ Exception -> 0x0af4 }
        r183 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0af4 }
        r4 = "intensity";
        r0 = r183;
        r4 = r0.getQueryParameter(r4);	 Catch:{ Exception -> 0x0af4 }
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ Exception -> 0x0af4 }
        r107 = r4.intValue();	 Catch:{ Exception -> 0x0af4 }
        r4 = "bg_color";
        r0 = r183;
        r71 = r0.getQueryParameter(r4);	 Catch:{ Exception -> 0x0af4 }
        if (r71 == 0) goto L_0x0af7;
    L_0x0aad:
        r4 = "bg_color";
        r0 = r183;
        r4 = r0.getQueryParameter(r4);	 Catch:{ Exception -> 0x0af4 }
        r6 = 16;
        r4 = java.lang.Integer.parseInt(r4, r6);	 Catch:{ Exception -> 0x0af4 }
        r6 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r4 = r4 | r6;
        r0 = r194;
        r0.imageBackgroundColor = r4;	 Catch:{ Exception -> 0x0af4 }
        r0 = r194;
        r4 = r0.imageBackgroundColor;	 Catch:{ Exception -> 0x0af4 }
        r4 = org.telegram.messenger.AndroidUtilities.getPatternSideColor(r4);	 Catch:{ Exception -> 0x0af4 }
        r0 = r194;
        r0.imageBackgroundSideColor = r4;	 Catch:{ Exception -> 0x0af4 }
        r0 = r194;
        r4 = r0.photoImage;	 Catch:{ Exception -> 0x0af4 }
        r6 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Exception -> 0x0af4 }
        r0 = r194;
        r8 = r0.imageBackgroundColor;	 Catch:{ Exception -> 0x0af4 }
        r8 = org.telegram.messenger.AndroidUtilities.getPatternColor(r8);	 Catch:{ Exception -> 0x0af4 }
        r9 = android.graphics.PorterDuff.Mode.SRC_IN;	 Catch:{ Exception -> 0x0af4 }
        r6.<init>(r8, r9);	 Catch:{ Exception -> 0x0af4 }
        r4.setColorFilter(r6);	 Catch:{ Exception -> 0x0af4 }
        r0 = r194;
        r4 = r0.photoImage;	 Catch:{ Exception -> 0x0af4 }
        r0 = r107;
        r6 = (float) r0;	 Catch:{ Exception -> 0x0af4 }
        r8 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r6 = r6 / r8;
        r4.setAlpha(r6);	 Catch:{ Exception -> 0x0af4 }
        goto L_0x05b0;
    L_0x0af4:
        r4 = move-exception;
        goto L_0x05b0;
    L_0x0af7:
        r81 = r183.getLastPathSegment();	 Catch:{ Exception -> 0x0af4 }
        if (r81 == 0) goto L_0x05b0;
    L_0x0afd:
        r4 = r81.length();	 Catch:{ Exception -> 0x0af4 }
        r6 = 6;
        if (r4 != r6) goto L_0x05b0;
    L_0x0b04:
        r4 = 16;
        r0 = r81;
        r4 = java.lang.Integer.parseInt(r0, r4);	 Catch:{ Exception -> 0x0af4 }
        r6 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r4 = r4 | r6;
        r0 = r194;
        r0.imageBackgroundColor = r4;	 Catch:{ Exception -> 0x0af4 }
        r4 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;	 Catch:{ Exception -> 0x0af4 }
        r4.<init>();	 Catch:{ Exception -> 0x0af4 }
        r0 = r194;
        r0.currentPhotoObject = r4;	 Catch:{ Exception -> 0x0af4 }
        r0 = r194;
        r4 = r0.currentPhotoObject;	 Catch:{ Exception -> 0x0af4 }
        r6 = "s";
        r4.type = r6;	 Catch:{ Exception -> 0x0af4 }
        r0 = r194;
        r4 = r0.currentPhotoObject;	 Catch:{ Exception -> 0x0af4 }
        r6 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0af4 }
        r4.w = r6;	 Catch:{ Exception -> 0x0af4 }
        r0 = r194;
        r4 = r0.currentPhotoObject;	 Catch:{ Exception -> 0x0af4 }
        r6 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0af4 }
        r4.h = r6;	 Catch:{ Exception -> 0x0af4 }
        r0 = r194;
        r4 = r0.currentPhotoObject;	 Catch:{ Exception -> 0x0af4 }
        r6 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;	 Catch:{ Exception -> 0x0af4 }
        r6.<init>();	 Catch:{ Exception -> 0x0af4 }
        r4.location = r6;	 Catch:{ Exception -> 0x0af4 }
        goto L_0x05b0;
    L_0x0b4a:
        if (r166 == 0) goto L_0x05b0;
    L_0x0b4c:
        r166 = r166.toLowerCase();
        r4 = "instagram";
        r0 = r166;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0b71;
    L_0x0b5b:
        r4 = "twitter";
        r0 = r166;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0b71;
    L_0x0b66:
        r4 = "telegram_album";
        r0 = r189;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x05b0;
    L_0x0b71:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.cached_page;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_page;
        if (r4 == 0) goto L_0x05b0;
    L_0x0b7f:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.photo;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r4 != 0) goto L_0x0b9d;
    L_0x0b8d:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.document;
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r4);
        if (r4 == 0) goto L_0x05b0;
    L_0x0b9d:
        r4 = 0;
        r0 = r194;
        r0.drawInstantView = r4;
        r168 = 1;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.cached_page;
        r0 = r4.blocks;
        r73 = r0;
        r82 = 1;
        r58 = 0;
    L_0x0bb6:
        r4 = r73.size();
        r0 = r58;
        if (r0 >= r4) goto L_0x0bf0;
    L_0x0bbe:
        r0 = r73;
        r1 = r58;
        r72 = r0.get(r1);
        r72 = (org.telegram.tgnet.TLRPC.PageBlock) r72;
        r0 = r72;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
        if (r4 == 0) goto L_0x0bdd;
    L_0x0bce:
        r70 = r72;
        r70 = (org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow) r70;
        r0 = r70;
        r4 = r0.items;
        r82 = r4.size();
    L_0x0bda:
        r58 = r58 + 1;
        goto L_0x0bb6;
    L_0x0bdd:
        r0 = r72;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
        if (r4 == 0) goto L_0x0bda;
    L_0x0be3:
        r70 = r72;
        r70 = (org.telegram.tgnet.TLRPC.TL_pageBlockCollage) r70;
        r0 = r70;
        r4 = r0.items;
        r82 = r4.size();
        goto L_0x0bda;
    L_0x0bf0:
        r4 = "Of";
        r6 = NUM; // 0x7f0CLASSNAMEbf float:1.8612175E38 double:1.053098125E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = 1;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r82);
        r8[r9] = r10;
        r5 = org.telegram.messenger.LocaleController.formatString(r4, r6, r8);
        r4 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r194;
        r0.photosCountWidth = r4;
        r4 = new android.text.StaticLayout;
        r6 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r0 = r194;
        r7 = r0.photosCountWidth;
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10 = 0;
        r11 = 0;
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);
        r0 = r194;
        r0.photosCountLayout = r4;
        goto L_0x05b0;
    L_0x0CLASSNAME:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r0 = r195;
        r6 = r0.lastLineWidth;
        r88 = r4 - r6;
        if (r88 < 0) goto L_0x0c5a;
    L_0x0c3f:
        r0 = r88;
        r1 = r176;
        if (r0 > r1) goto L_0x0c5a;
    L_0x0CLASSNAME:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r4 = r4 + r176;
        r4 = r4 - r88;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x0600;
    L_0x0c5a:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r0 = r195;
        r6 = r0.lastLineWidth;
        r6 = r6 + r176;
        r4 = java.lang.Math.max(r4, r6);
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x0600;
    L_0x0CLASSNAME:
        r4 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x0631;
    L_0x0c7d:
        r4 = 0;
        goto L_0x0656;
    L_0x0CLASSNAME:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r4 - r6;
        goto L_0x06ea;
    L_0x0c8e:
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x0cb2;
    L_0x0CLASSNAME:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x0cb2;
    L_0x0c9a:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 != 0) goto L_0x0cb2;
    L_0x0ca4:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r4 - r6;
        goto L_0x06ea;
    L_0x0cb2:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r4 - r6;
        goto L_0x06ea;
    L_0x0cc0:
        r178 = 0;
        goto L_0x071a;
    L_0x0cc4:
        r67 = 0;
        goto L_0x0727;
    L_0x0cc8:
        r87 = 0;
        goto L_0x0734;
    L_0x0ccc:
        r169 = 0;
        goto L_0x079d;
    L_0x0cd0:
        r4 = 0;
        goto L_0x07d5;
    L_0x0cd3:
        r0 = r194;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x0d17;
    L_0x0cd9:
        r0 = r195;
        r4 = r0.messageOwner;
        r0 = r4.media;
        r108 = r0;
        r108 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r108;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r7 = r4.title;
        r178 = 0;
        r87 = 0;
        r143 = 0;
        r67 = 0;
        r89 = 0;
        r0 = r108;
        r4 = r0.photo;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r4 == 0) goto L_0x0d15;
    L_0x0cfd:
        r0 = r108;
        r4 = r0.photo;
        r15 = org.telegram.messenger.WebFile.createWithWebDocument(r4);
    L_0x0d05:
        r90 = 0;
        r181 = "invoice";
        r4 = 0;
        r0 = r194;
        r0.isSmallImage = r4;
        r169 = 0;
        r187 = r15;
        goto L_0x07db;
    L_0x0d15:
        r15 = 0;
        goto L_0x0d05;
    L_0x0d17:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.game;
        r97 = r0;
        r0 = r97;
        r7 = r0.title;
        r178 = 0;
        r15 = 0;
        r0 = r195;
        r4 = r0.messageText;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x0d56;
    L_0x0d32:
        r0 = r97;
        r0 = r0.description;
        r87 = r0;
    L_0x0d38:
        r0 = r97;
        r0 = r0.photo;
        r143 = r0;
        r67 = 0;
        r0 = r97;
        r0 = r0.document;
        r89 = r0;
        r90 = 0;
        r181 = "game";
        r4 = 0;
        r0 = r194;
        r0.isSmallImage = r4;
        r169 = 0;
        r187 = r15;
        goto L_0x07db;
    L_0x0d56:
        r87 = 0;
        goto L_0x0d38;
    L_0x0d59:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r59 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x07f4;
    L_0x0d61:
        r4 = 0;
        goto L_0x0847;
    L_0x0d64:
        r92 = move-exception;
        org.telegram.messenger.FileLog.e(r92);
        goto L_0x0891;
    L_0x0d6a:
        r155 = r158;
        r9 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x4d93 }
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4d93 }
        r11 = r115 - r4;
        r13 = 4;
        r8 = r178;
        r10 = r115;
        r12 = r158;
        r4 = generateStaticLayout(r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x4d93 }
        r0 = r194;
        r0.titleLayout = r4;	 Catch:{ Exception -> 0x4d93 }
        r0 = r194;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x4d93 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x4d93 }
        r13 = r158 - r4;
        goto L_0x08ec;
    L_0x0d91:
        r0 = r194;
        r4 = r0.titleX;	 Catch:{ Exception -> 0x0da2 }
        r0 = r114;
        r6 = -r0;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x0da2 }
        r0 = r194;
        r0.titleX = r4;	 Catch:{ Exception -> 0x0da2 }
        goto L_0x0943;
    L_0x0da2:
        r92 = move-exception;
    L_0x0da3:
        org.telegram.messenger.FileLog.e(r92);
    L_0x0da6:
        if (r179 == 0) goto L_0x4da4;
    L_0x0da8:
        r0 = r194;
        r4 = r0.isSmallImage;
        if (r4 == 0) goto L_0x4da4;
    L_0x0dae:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r11 = r115 - r4;
        r158 = r13;
    L_0x0db8:
        r68 = 0;
        if (r67 == 0) goto L_0x4da0;
    L_0x0dbc:
        if (r178 != 0) goto L_0x4da0;
    L_0x0dbe:
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0var_ }
        if (r4 == 0) goto L_0x0de2;
    L_0x0dc4:
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0var_ }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0var_ }
        r4 = r4 + r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0var_ }
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0var_ }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0var_ }
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0var_ }
    L_0x0de2:
        r4 = 3;
        r0 = r158;
        if (r0 != r4) goto L_0x0f3b;
    L_0x0de7:
        r0 = r194;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0var_ }
        if (r4 == 0) goto L_0x0def;
    L_0x0ded:
        if (r87 != 0) goto L_0x0f3b;
    L_0x0def:
        r8 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0var_ }
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0var_ }
        r12 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0var_ }
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r14 = 0;
        r15 = 0;
        r9 = r67;
        r8.<init>(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ Exception -> 0x0var_ }
        r0 = r194;
        r0.authorLayout = r8;	 Catch:{ Exception -> 0x0var_ }
        r13 = r158;
    L_0x0e04:
        r0 = r194;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x4d90 }
        r0 = r194;
        r6 = r0.authorLayout;	 Catch:{ Exception -> 0x4d90 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x4d90 }
        r6 = r6 + -1;
        r103 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x4d90 }
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x4d90 }
        r4 = r4 + r103;
        r0 = r194;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x4d90 }
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4d90 }
        r4 = r4 + r103;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4d90 }
        r0 = r194;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x4d90 }
        r6 = 0;
        r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x4d90 }
        r0 = (int) r4;	 Catch:{ Exception -> 0x4d90 }
        r114 = r0;
        r0 = r114;
        r4 = -r0;
        r0 = r194;
        r0.authorX = r4;	 Catch:{ Exception -> 0x4d90 }
        if (r114 == 0) goto L_0x0f5e;
    L_0x0e3f:
        r0 = r194;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x4d90 }
        r4 = r4.getWidth();	 Catch:{ Exception -> 0x4d90 }
        r190 = r4 - r114;
        r68 = 1;
    L_0x0e4b:
        r4 = r190 + r59;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x4d90 }
        r4 = r190 + r59;
        r0 = r125;
        r125 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x4d90 }
    L_0x0e5b:
        if (r87 == 0) goto L_0x0fa3;
    L_0x0e5d:
        r4 = 0;
        r0 = r194;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0f9f }
        r0 = r194;
        r4 = r0.currentMessageObject;	 Catch:{ Exception -> 0x0f9f }
        r4.generateLinkDescription();	 Catch:{ Exception -> 0x0f9f }
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0f9f }
        if (r4 == 0) goto L_0x0e8d;
    L_0x0e6f:
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0f9f }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0f9f }
        r4 = r4 + r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0f9f }
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0f9f }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0f9f }
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0f9f }
    L_0x0e8d:
        r155 = 0;
        if (r7 == 0) goto L_0x0var_;
    L_0x0e91:
        r4 = r7.toLowerCase();	 Catch:{ Exception -> 0x0f9f }
        r6 = "twitter";
        r4 = r4.equals(r6);	 Catch:{ Exception -> 0x0f9f }
        if (r4 == 0) goto L_0x0var_;
    L_0x0e9e:
        r63 = 1;
    L_0x0ea0:
        r4 = 3;
        if (r13 != r4) goto L_0x0var_;
    L_0x0ea3:
        r0 = r194;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0f9f }
        if (r4 != 0) goto L_0x0var_;
    L_0x0ea9:
        r0 = r195;
        r9 = r0.linkDescription;	 Catch:{ Exception -> 0x0f9f }
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0f9f }
        r12 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0f9f }
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0f9f }
        r14 = (float) r4;	 Catch:{ Exception -> 0x0f9f }
        r15 = 0;
        r16 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0f9f }
        if (r63 == 0) goto L_0x0f7d;
    L_0x0ebf:
        r18 = 100;
    L_0x0ec1:
        r17 = r11;
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r9, r10, r11, r12, r13, r14, r15, r16, r17, r18);	 Catch:{ Exception -> 0x0f9f }
        r0 = r194;
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x0f9f }
    L_0x0ecb:
        r0 = r194;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f9f }
        r0 = r194;
        r6 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f9f }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0f9f }
        r6 = r6 + -1;
        r103 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x0f9f }
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0f9f }
        r4 = r4 + r103;
        r0 = r194;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0f9f }
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0f9f }
        r4 = r4 + r103;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0f9f }
        r102 = 0;
        r58 = 0;
    L_0x0ef5:
        r0 = r194;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f9f }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0f9f }
        r0 = r58;
        if (r0 >= r4) goto L_0x15e7;
    L_0x0var_:
        r0 = r194;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f9f }
        r0 = r58;
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x0f9f }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0f9f }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0f9f }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0f9f }
        r114 = r0;
        if (r114 == 0) goto L_0x0var_;
    L_0x0var_:
        r102 = 1;
        r0 = r194;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x0f9f }
        if (r4 != 0) goto L_0x15d6;
    L_0x0f1d:
        r0 = r114;
        r4 = -r0;
        r0 = r194;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0f9f }
    L_0x0var_:
        r58 = r58 + 1;
        goto L_0x0ef5;
    L_0x0var_:
        r0 = r194;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0da2 }
        r0 = r58;
        r4 = r4.getLineWidth(r0);	 Catch:{ Exception -> 0x0da2 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0da2 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0da2 }
        r0 = (int) r8;
        r190 = r0;
        goto L_0x094f;
    L_0x0f3b:
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0var_ }
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0var_ }
        r12 = r11 - r4;
        r14 = 1;
        r9 = r67;
        r13 = r158;
        r4 = generateStaticLayout(r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x0var_ }
        r0 = r194;
        r0.authorLayout = r4;	 Catch:{ Exception -> 0x0var_ }
        r0 = r194;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x0var_ }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0var_ }
        r13 = r158 - r4;
        goto L_0x0e04;
    L_0x0f5e:
        r0 = r194;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x4d90 }
        r6 = 0;
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x4d90 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x4d90 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x4d90 }
        r0 = (int) r8;
        r190 = r0;
        goto L_0x0e4b;
    L_0x0var_:
        r92 = move-exception;
        r13 = r158;
    L_0x0var_:
        org.telegram.messenger.FileLog.e(r92);
        goto L_0x0e5b;
    L_0x0var_:
        r63 = 0;
        goto L_0x0ea0;
    L_0x0f7d:
        r18 = 6;
        goto L_0x0ec1;
    L_0x0var_:
        r155 = r13;
        r0 = r195;
        r9 = r0.linkDescription;	 Catch:{ Exception -> 0x0f9f }
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0f9f }
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0f9f }
        r12 = r11 - r4;
        if (r63 == 0) goto L_0x15d3;
    L_0x0var_:
        r14 = 100;
    L_0x0var_:
        r4 = generateStaticLayout(r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x0f9f }
        r0 = r194;
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x0f9f }
        goto L_0x0ecb;
    L_0x0f9f:
        r92 = move-exception;
        org.telegram.messenger.FileLog.e(r92);
    L_0x0fa3:
        if (r169 == 0) goto L_0x0fc3;
    L_0x0fa5:
        r0 = r194;
        r4 = r0.descriptionLayout;
        if (r4 == 0) goto L_0x0fbc;
    L_0x0fab:
        r0 = r194;
        r4 = r0.descriptionLayout;
        if (r4 == 0) goto L_0x0fc3;
    L_0x0fb1:
        r0 = r194;
        r4 = r0.descriptionLayout;
        r4 = r4.getLineCount();
        r6 = 1;
        if (r4 != r6) goto L_0x0fc3;
    L_0x0fbc:
        r169 = 0;
        r4 = 0;
        r0 = r194;
        r0.isSmallImage = r4;
    L_0x0fc3:
        if (r169 == 0) goto L_0x1685;
    L_0x0fc5:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r122 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0fcb:
        if (r89 == 0) goto L_0x1CLASSNAME;
    L_0x0fcd:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r89);
        if (r4 == 0) goto L_0x1689;
    L_0x0fd3:
        r0 = r89;
        r4 = r0.thumbs;
        r6 = 90;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObject = r4;
        r0 = r89;
        r1 = r194;
        r1.documentAttach = r0;
        r4 = 7;
        r0 = r194;
        r0.documentAttachType = r4;
        r15 = r187;
    L_0x0fee:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 5;
        if (r4 == r6) goto L_0x132a;
    L_0x0ff5:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 3;
        if (r4 == r6) goto L_0x132a;
    L_0x0ffc:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 1;
        if (r4 == r6) goto L_0x132a;
    L_0x1003:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x100b;
    L_0x1009:
        if (r15 == 0) goto L_0x21ab;
    L_0x100b:
        if (r143 == 0) goto L_0x100f;
    L_0x100d:
        if (r169 == 0) goto L_0x1048;
    L_0x100f:
        if (r181 == 0) goto L_0x1d04;
    L_0x1011:
        r4 = "photo";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x1048;
    L_0x101c:
        r4 = "document";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x102e;
    L_0x1027:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 6;
        if (r4 != r6) goto L_0x1048;
    L_0x102e:
        r4 = "gif";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x1048;
    L_0x1039:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 4;
        if (r4 == r6) goto L_0x1048;
    L_0x1040:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 8;
        if (r4 != r6) goto L_0x1d04;
    L_0x1048:
        r4 = 1;
    L_0x1049:
        r0 = r194;
        r0.drawImageButton = r4;
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        if (r4 == 0) goto L_0x1071;
    L_0x1053:
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
    L_0x1071:
        r0 = r194;
        r4 = r0.imageBackgroundSideColor;
        if (r4 == 0) goto L_0x1d07;
    L_0x1077:
        r4 = NUM; // 0x43500000 float:208.0 double:5.57956413E-315;
        r122 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x107d:
        r0 = r194;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x1d63;
    L_0x1083:
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x1089:
        r4 = r122 - r4;
        r4 = r4 + r59;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x1d66;
    L_0x1099:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r6 = -1;
        r4.size = r6;
        r0 = r194;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x10ad;
    L_0x10a6:
        r0 = r194;
        r4 = r0.currentPhotoObjectThumb;
        r6 = -1;
        r4.size = r6;
    L_0x10ad:
        r0 = r194;
        r4 = r0.imageBackgroundSideColor;
        if (r4 == 0) goto L_0x10bf;
    L_0x10b3:
        r4 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r120 - r4;
        r0 = r194;
        r0.imageBackgroundSideWidth = r4;
    L_0x10bf:
        if (r169 != 0) goto L_0x10c8;
    L_0x10c1:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 7;
        if (r4 != r6) goto L_0x1d6b;
    L_0x10c8:
        r103 = r122;
        r190 = r122;
    L_0x10cc:
        r0 = r194;
        r4 = r0.isSmallImage;
        if (r4 == 0) goto L_0x1e25;
    L_0x10d2:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r61;
        r0 = r194;
        r6 = r0.linkPreviewHeight;
        if (r4 <= r6) goto L_0x1109;
    L_0x10e0:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r61;
        r0 = r194;
        r8 = r0.linkPreviewHeight;
        r6 = r6 - r8;
        r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 + r8;
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r61;
        r0 = r194;
        r0.linkPreviewHeight = r4;
    L_0x1109:
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;
    L_0x1118:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 8;
        if (r4 != r6) goto L_0x1e42;
    L_0x1120:
        r0 = r194;
        r4 = r0.imageBackgroundSideColor;
        if (r4 != 0) goto L_0x1e42;
    L_0x1126:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r8 = 0;
        r9 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = r120 - r9;
        r0 = r190;
        r9 = java.lang.Math.max(r9, r0);
        r0 = r103;
        r4.setImageCoords(r6, r8, r9, r0);
    L_0x113f:
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r190);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r103);
        r8[r9] = r10;
        r4 = java.lang.String.format(r4, r6, r8);
        r0 = r194;
        r0.currentPhotoFilter = r4;
        r4 = java.util.Locale.US;
        r6 = "%d_%d_b";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r190);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r103);
        r8[r9] = r10;
        r4 = java.lang.String.format(r4, r6, r8);
        r0 = r194;
        r0.currentPhotoFilterThumb = r4;
        if (r15 == 0) goto L_0x1e51;
    L_0x117d:
        r0 = r194;
        r14 = r0.photoImage;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r16 = r0;
        r17 = 0;
        r18 = 0;
        r19 = "b1";
        r0 = r15.size;
        r20 = r0;
        r21 = 0;
        r23 = 1;
        r22 = r195;
        r14.setImage(r15, r16, r17, r18, r19, r20, r21, r22, r23);
    L_0x119b:
        r4 = 1;
        r0 = r194;
        r0.drawPhotoImage = r4;
        if (r181 == 0) goto L_0x2168;
    L_0x11a2:
        r4 = "video";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x2168;
    L_0x11ad:
        if (r90 == 0) goto L_0x2168;
    L_0x11af:
        r131 = r90 / 60;
        r4 = r131 * 60;
        r165 = r90 - r4;
        r4 = "%d:%02d";
        r6 = 2;
        r6 = new java.lang.Object[r6];
        r8 = 0;
        r9 = java.lang.Integer.valueOf(r131);
        r6[r8] = r9;
        r8 = 1;
        r9 = java.lang.Integer.valueOf(r165);
        r6[r8] = r9;
        r5 = java.lang.String.format(r4, r6);
        r4 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r194;
        r0.durationWidth = r4;
        r16 = new android.text.StaticLayout;
        r18 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r0 = r194;
        r0 = r0.durationWidth;
        r19 = r0;
        r20 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r22 = 0;
        r23 = 0;
        r17 = r5;
        r16.<init>(r17, r18, r19, r20, r21, r22, r23);
        r0 = r16;
        r1 = r194;
        r1.videoInfoLayout = r0;
    L_0x11fa:
        r0 = r194;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x12f0;
    L_0x1200:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x21d5;
    L_0x120c:
        r4 = "PaymentReceipt";
        r6 = NUM; // 0x7f0CLASSNAMEdd float:1.8612756E38 double:1.0530982665E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
    L_0x121a:
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r0 = r195;
        r6 = r0.messageOwner;
        r6 = r6.media;
        r8 = r6.total_amount;
        r0 = r195;
        r6 = r0.messageOwner;
        r6 = r6.media;
        r6 = r6.currency;
        r154 = r4.formatCurrencyString(r8, r6);
        r17 = new android.text.SpannableStringBuilder;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r154;
        r4 = r4.append(r0);
        r6 = " ";
        r4 = r4.append(r6);
        r4 = r4.append(r5);
        r4 = r4.toString();
        r0 = r17;
        r0.<init>(r4);
        r4 = new org.telegram.ui.Components.TypefaceSpan;
        r6 = "fonts/rmedium.ttf";
        r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r6);
        r4.<init>(r6);
        r6 = 0;
        r8 = r154.length();
        r9 = 33;
        r0 = r17;
        r0.setSpan(r4, r6, r8, r9);
        r4 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
        r6 = 0;
        r8 = r17.length();
        r0 = r17;
        r4 = r4.measureText(r0, r6, r8);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r194;
        r0.durationWidth = r4;
        r16 = new android.text.StaticLayout;
        r18 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
        r0 = r194;
        r4 = r0.durationWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r19 = r4 + r6;
        r20 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r22 = 0;
        r23 = 0;
        r16.<init>(r17, r18, r19, r20, r21, r22, r23);
        r0 = r16;
        r1 = r194;
        r1.videoInfoLayout = r0;
        r0 = r194;
        r4 = r0.drawPhotoImage;
        if (r4 != 0) goto L_0x12f0;
    L_0x12a9:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r6 = r0.timeWidth;
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x21ff;
    L_0x12c2:
        r4 = 20;
    L_0x12c4:
        r4 = r4 + 14;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r177 = r6 + r4;
        r0 = r194;
        r4 = r0.durationWidth;
        r4 = r4 + r177;
        r0 = r43;
        if (r4 <= r0) goto L_0x2202;
    L_0x12d7:
        r0 = r194;
        r4 = r0.durationWidth;
        r0 = r120;
        r120 = java.lang.Math.max(r4, r0);
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
    L_0x12f0:
        r0 = r194;
        r4 = r0.hasGamePreview;
        if (r4 == 0) goto L_0x131f;
    L_0x12f6:
        r0 = r195;
        r4 = r0.textHeight;
        if (r4 == 0) goto L_0x131f;
    L_0x12fc:
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        r0 = r195;
        r6 = r0.textHeight;
        r8 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 + r8;
        r4 = r4 + r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
    L_0x131f:
        r0 = r194;
        r1 = r43;
        r2 = r176;
        r3 = r120;
        r0.calcBackgroundWidth(r1, r2, r3);
    L_0x132a:
        r194.createInstantViewButton();
    L_0x132d:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 != 0) goto L_0x1456;
    L_0x1333:
        r0 = r195;
        r4 = r0.type;
        r6 = 13;
        if (r4 == r6) goto L_0x1456;
    L_0x133b:
        r0 = r194;
        r4 = r0.addedCaptionHeight;
        if (r4 != 0) goto L_0x1456;
    L_0x1341:
        r0 = r194;
        r4 = r0.captionLayout;
        if (r4 != 0) goto L_0x139c;
    L_0x1347:
        r0 = r195;
        r4 = r0.caption;
        if (r4 == 0) goto L_0x139c;
    L_0x134d:
        r0 = r195;
        r4 = r0.caption;	 Catch:{ Exception -> 0x491a }
        r0 = r194;
        r0.currentCaption = r4;	 Catch:{ Exception -> 0x491a }
        r0 = r194;
        r4 = r0.backgroundWidth;	 Catch:{ Exception -> 0x491a }
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x491a }
        r190 = r4 - r6;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x491a }
        r27 = r190 - r4;
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x491a }
        r6 = 24;
        if (r4 < r6) goto L_0x48fb;
    L_0x136f:
        r0 = r195;
        r4 = r0.caption;	 Catch:{ Exception -> 0x491a }
        r6 = 0;
        r0 = r195;
        r8 = r0.caption;	 Catch:{ Exception -> 0x491a }
        r8 = r8.length();	 Catch:{ Exception -> 0x491a }
        r9 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x491a }
        r0 = r27;
        r4 = android.text.StaticLayout.Builder.obtain(r4, r6, r8, r9, r0);	 Catch:{ Exception -> 0x491a }
        r6 = 1;
        r4 = r4.setBreakStrategy(r6);	 Catch:{ Exception -> 0x491a }
        r6 = 0;
        r4 = r4.setHyphenationFrequency(r6);	 Catch:{ Exception -> 0x491a }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x491a }
        r4 = r4.setAlignment(r6);	 Catch:{ Exception -> 0x491a }
        r4 = r4.build();	 Catch:{ Exception -> 0x491a }
        r0 = r194;
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x491a }
    L_0x139c:
        r0 = r194;
        r4 = r0.captionLayout;
        if (r4 == 0) goto L_0x1456;
    L_0x13a2:
        r0 = r194;
        r4 = r0.backgroundWidth;	 Catch:{ Exception -> 0x4923 }
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4923 }
        r190 = r4 - r6;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4923 }
        r27 = r190 - r4;
        r0 = r194;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4923 }
        if (r4 == 0) goto L_0x1456;
    L_0x13bc:
        r0 = r194;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4923 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x4923 }
        if (r4 <= 0) goto L_0x1456;
    L_0x13c6:
        r0 = r27;
        r1 = r194;
        r1.captionWidth = r0;	 Catch:{ Exception -> 0x4923 }
        r0 = r194;
        r6 = r0.timeWidth;	 Catch:{ Exception -> 0x4923 }
        r4 = r195.isOutOwner();	 Catch:{ Exception -> 0x4923 }
        if (r4 == 0) goto L_0x4920;
    L_0x13d6:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4923 }
    L_0x13dc:
        r177 = r6 + r4;
        r0 = r194;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4923 }
        r4 = r4.getHeight();	 Catch:{ Exception -> 0x4923 }
        r0 = r194;
        r0.captionHeight = r4;	 Catch:{ Exception -> 0x4923 }
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4923 }
        r0 = r194;
        r6 = r0.captionHeight;	 Catch:{ Exception -> 0x4923 }
        r8 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x4923 }
        r6 = r6 + r8;
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4923 }
        r0 = r194;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4923 }
        r0 = r194;
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x4923 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x4923 }
        r6 = r6 + -1;
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x4923 }
        r0 = r194;
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x4923 }
        r0 = r194;
        r8 = r0.captionLayout;	 Catch:{ Exception -> 0x4923 }
        r8 = r8.getLineCount();	 Catch:{ Exception -> 0x4923 }
        r8 = r8 + -1;
        r6 = r6.getLineLeft(r8);	 Catch:{ Exception -> 0x4923 }
        r112 = r4 + r6;
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4923 }
        r4 = r190 - r4;
        r4 = (float) r4;	 Catch:{ Exception -> 0x4923 }
        r4 = r4 - r112;
        r0 = r177;
        r6 = (float) r0;	 Catch:{ Exception -> 0x4923 }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x1456;
    L_0x1436:
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4923 }
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4923 }
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4923 }
        r0 = r194;
        r4 = r0.captionHeight;	 Catch:{ Exception -> 0x4923 }
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4923 }
        r4 = r4 + r6;
        r0 = r194;
        r0.captionHeight = r4;	 Catch:{ Exception -> 0x4923 }
        r79 = 2;
    L_0x1456:
        r0 = r194;
        r4 = r0.captionLayout;
        if (r4 != 0) goto L_0x1481;
    L_0x145c:
        r0 = r194;
        r4 = r0.widthBeforeNewTimeLine;
        r6 = -1;
        if (r4 == r6) goto L_0x1481;
    L_0x1463:
        r0 = r194;
        r4 = r0.availableTimeWidth;
        r0 = r194;
        r6 = r0.widthBeforeNewTimeLine;
        r4 = r4 - r6;
        r0 = r194;
        r6 = r0.timeWidth;
        if (r4 >= r6) goto L_0x1481;
    L_0x1472:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
    L_0x1481:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r8 = r4.eventId;
        r20 = 0;
        r4 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1));
        if (r4 == 0) goto L_0x497b;
    L_0x148d:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.isMediaEmpty();
        if (r4 != 0) goto L_0x497b;
    L_0x1497:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        if (r4 == 0) goto L_0x497b;
    L_0x14a3:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42240000 float:41.0 double:5.48242687E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r4 - r6;
        r4 = 1;
        r0 = r194;
        r0.hasOldCaptionPreview = r4;
        r4 = 0;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.media;
        r0 = r4.webpage;
        r188 = r0;
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x492c }
        r0 = r188;
        r6 = r0.site_name;	 Catch:{ Exception -> 0x492c }
        r4 = r4.measureText(r6);	 Catch:{ Exception -> 0x492c }
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = r4 + r6;
        r8 = (double) r4;	 Catch:{ Exception -> 0x492c }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x492c }
        r0 = (int) r8;	 Catch:{ Exception -> 0x492c }
        r190 = r0;
        r0 = r190;
        r1 = r194;
        r1.siteNameWidth = r0;	 Catch:{ Exception -> 0x492c }
        r44 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x492c }
        r0 = r188;
        r0 = r0.site_name;	 Catch:{ Exception -> 0x492c }
        r45 = r0;
        r46 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x492c }
        r0 = r190;
        r47 = java.lang.Math.min(r0, r11);	 Catch:{ Exception -> 0x492c }
        r48 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x492c }
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r50 = 0;
        r51 = 0;
        r44.<init>(r45, r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x492c }
        r0 = r44;
        r1 = r194;
        r1.siteNameLayout = r0;	 Catch:{ Exception -> 0x492c }
        r0 = r194;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x492c }
        r6 = 0;
        r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x492c }
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x4929;
    L_0x150f:
        r4 = 1;
    L_0x1510:
        r0 = r194;
        r0.siteNameRtl = r4;	 Catch:{ Exception -> 0x492c }
        r0 = r194;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x492c }
        r0 = r194;
        r6 = r0.siteNameLayout;	 Catch:{ Exception -> 0x492c }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x492c }
        r6 = r6 + -1;
        r103 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x492c }
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x492c }
        r4 = r4 + r103;
        r0 = r194;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x492c }
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x492c }
        r4 = r4 + r103;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x492c }
    L_0x153a:
        r4 = 0;
        r0 = r194;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x4943 }
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x4943 }
        if (r4 == 0) goto L_0x1554;
    L_0x1545:
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4943 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4943 }
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4943 }
    L_0x1554:
        r0 = r188;
        r0 = r0.description;	 Catch:{ Exception -> 0x4943 }
        r44 = r0;
        r45 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x4943 }
        r47 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x4943 }
        r48 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4943 }
        r0 = (float) r4;	 Catch:{ Exception -> 0x4943 }
        r49 = r0;
        r50 = 0;
        r51 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x4943 }
        r53 = 6;
        r46 = r11;
        r52 = r11;
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r44, r45, r46, r47, r48, r49, r50, r51, r52, r53);	 Catch:{ Exception -> 0x4943 }
        r0 = r194;
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x4943 }
        r0 = r194;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x4943 }
        r0 = r194;
        r6 = r0.descriptionLayout;	 Catch:{ Exception -> 0x4943 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x4943 }
        r6 = r6 + -1;
        r103 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x4943 }
        r0 = r194;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x4943 }
        r4 = r4 + r103;
        r0 = r194;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x4943 }
        r0 = r194;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4943 }
        r4 = r4 + r103;
        r0 = r194;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4943 }
        r58 = 0;
    L_0x15a3:
        r0 = r194;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x4943 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x4943 }
        r0 = r58;
        if (r0 >= r4) goto L_0x4947;
    L_0x15af:
        r0 = r194;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x4943 }
        r0 = r58;
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x4943 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x4943 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x4943 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x4943 }
        r114 = r0;
        if (r114 == 0) goto L_0x15d0;
    L_0x15c3:
        r0 = r194;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x4943 }
        if (r4 != 0) goto L_0x4932;
    L_0x15c9:
        r0 = r114;
        r4 = -r0;
        r0 = r194;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x4943 }
    L_0x15d0:
        r58 = r58 + 1;
        goto L_0x15a3;
    L_0x15d3:
        r14 = 6;
        goto L_0x0var_;
    L_0x15d6:
        r0 = r194;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x0f9f }
        r0 = r114;
        r6 = -r0;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x0f9f }
        r0 = r194;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0f9f }
        goto L_0x0var_;
    L_0x15e7:
        r0 = r194;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f9f }
        r172 = r4.getWidth();	 Catch:{ Exception -> 0x0f9f }
        r58 = 0;
    L_0x15f1:
        r0 = r194;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f9f }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0f9f }
        r0 = r58;
        if (r0 >= r4) goto L_0x0fa3;
    L_0x15fd:
        r0 = r194;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f9f }
        r0 = r58;
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x0f9f }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0f9f }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0f9f }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0f9f }
        r114 = r0;
        if (r114 != 0) goto L_0x161c;
    L_0x1611:
        r0 = r194;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x0f9f }
        if (r4 == 0) goto L_0x161c;
    L_0x1617:
        r4 = 0;
        r0 = r194;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0f9f }
    L_0x161c:
        if (r114 == 0) goto L_0x1669;
    L_0x161e:
        r190 = r172 - r114;
    L_0x1620:
        r0 = r58;
        r1 = r155;
        if (r0 < r1) goto L_0x1630;
    L_0x1626:
        if (r155 == 0) goto L_0x1638;
    L_0x1628:
        if (r114 == 0) goto L_0x1638;
    L_0x162a:
        r0 = r194;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0f9f }
        if (r4 == 0) goto L_0x1638;
    L_0x1630:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0f9f }
        r190 = r190 + r4;
    L_0x1638:
        r4 = r190 + r59;
        r0 = r125;
        if (r0 >= r4) goto L_0x165e;
    L_0x163e:
        if (r179 == 0) goto L_0x164d;
    L_0x1640:
        r0 = r194;
        r4 = r0.titleX;	 Catch:{ Exception -> 0x0f9f }
        r6 = r190 + r59;
        r6 = r6 - r125;
        r4 = r4 + r6;
        r0 = r194;
        r0.titleX = r4;	 Catch:{ Exception -> 0x0f9f }
    L_0x164d:
        if (r68 == 0) goto L_0x165c;
    L_0x164f:
        r0 = r194;
        r4 = r0.authorX;	 Catch:{ Exception -> 0x0f9f }
        r6 = r190 + r59;
        r6 = r6 - r125;
        r4 = r4 + r6;
        r0 = r194;
        r0.authorX = r4;	 Catch:{ Exception -> 0x0f9f }
    L_0x165c:
        r125 = r190 + r59;
    L_0x165e:
        r4 = r190 + r59;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0f9f }
        r58 = r58 + 1;
        goto L_0x15f1;
    L_0x1669:
        if (r102 == 0) goto L_0x166e;
    L_0x166b:
        r190 = r172;
        goto L_0x1620;
    L_0x166e:
        r0 = r194;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f9f }
        r0 = r58;
        r4 = r4.getLineWidth(r0);	 Catch:{ Exception -> 0x0f9f }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0f9f }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0f9f }
        r4 = (int) r8;	 Catch:{ Exception -> 0x0f9f }
        r0 = r172;
        r190 = java.lang.Math.min(r4, r0);	 Catch:{ Exception -> 0x0f9f }
        goto L_0x1620;
    L_0x1685:
        r122 = r11;
        goto L_0x0fcb;
    L_0x1689:
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r89);
        if (r4 == 0) goto L_0x1740;
    L_0x168f:
        r4 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r4 != 0) goto L_0x1699;
    L_0x1693:
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = r195;
        r0.gifState = r4;
    L_0x1699:
        r0 = r194;
        r6 = r0.photoImage;
        r0 = r195;
        r4 = r0.gifState;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x173a;
    L_0x16a7:
        r4 = 1;
    L_0x16a8:
        r6.setAllowStartAnimation(r4);
        r0 = r89;
        r4 = r0.thumbs;
        r6 = 90;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObject = r4;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x172b;
    L_0x16bf:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x16cf;
    L_0x16c7:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x172b;
    L_0x16cf:
        r58 = 0;
    L_0x16d1:
        r0 = r89;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r58;
        if (r0 >= r4) goto L_0x1709;
    L_0x16dd:
        r0 = r89;
        r4 = r0.attributes;
        r0 = r58;
        r66 = r4.get(r0);
        r66 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r66;
        r0 = r66;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 != 0) goto L_0x16f5;
    L_0x16ef:
        r0 = r66;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x173d;
    L_0x16f5:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r66;
        r6 = r0.w;
        r4.w = r6;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r66;
        r6 = r0.h;
        r4.h = r6;
    L_0x1709:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x1719;
    L_0x1711:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x172b;
    L_0x1719:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r194;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.h = r8;
        r4.w = r8;
    L_0x172b:
        r0 = r89;
        r1 = r194;
        r1.documentAttach = r0;
        r4 = 2;
        r0 = r194;
        r0.documentAttachType = r4;
        r15 = r187;
        goto L_0x0fee;
    L_0x173a:
        r4 = 0;
        goto L_0x16a8;
    L_0x173d:
        r58 = r58 + 1;
        goto L_0x16d1;
    L_0x1740:
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r89);
        if (r4 == 0) goto L_0x1875;
    L_0x1746:
        if (r143 == 0) goto L_0x1767;
    L_0x1748:
        r0 = r143;
        r4 = r0.sizes;
        r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r8 = 1;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6, r8);
        r0 = r194;
        r0.currentPhotoObject = r4;
        r0 = r143;
        r4 = r0.sizes;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
    L_0x1767:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x1789;
    L_0x176d:
        r0 = r89;
        r4 = r0.thumbs;
        r6 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObject = r4;
        r0 = r89;
        r4 = r0.thumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
    L_0x1789:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r194;
        r6 = r0.currentPhotoObjectThumb;
        if (r4 != r6) goto L_0x1798;
    L_0x1793:
        r4 = 0;
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
    L_0x1798:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x17bb;
    L_0x179e:
        r4 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r4.<init>();
        r0 = r194;
        r0.currentPhotoObject = r4;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r6 = "s";
        r4.type = r6;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r6 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r6.<init>();
        r4.location = r6;
    L_0x17bb:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x1850;
    L_0x17c1:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x17d9;
    L_0x17c9:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 == 0) goto L_0x17d9;
    L_0x17d1:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r4 == 0) goto L_0x1850;
    L_0x17d9:
        r58 = 0;
    L_0x17db:
        r0 = r89;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r58;
        if (r0 >= r4) goto L_0x182e;
    L_0x17e7:
        r0 = r89;
        r4 = r0.attributes;
        r0 = r58;
        r66 = r4.get(r0);
        r66 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r66;
        r0 = r66;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x1871;
    L_0x17f9:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r4 == 0) goto L_0x185c;
    L_0x1801:
        r0 = r66;
        r4 = r0.w;
        r0 = r66;
        r6 = r0.w;
        r4 = java.lang.Math.max(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r163 = r4 / r6;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r66;
        r6 = r0.w;
        r6 = (float) r6;
        r6 = r6 / r163;
        r6 = (int) r6;
        r4.w = r6;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r66;
        r6 = r0.h;
        r6 = (float) r6;
        r6 = r6 / r163;
        r6 = (int) r6;
        r4.h = r6;
    L_0x182e:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x183e;
    L_0x1836:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x1850;
    L_0x183e:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r194;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.h = r8;
        r4.w = r8;
    L_0x1850:
        r4 = 0;
        r0 = r194;
        r1 = r195;
        r0.createDocumentLayout(r4, r1);
        r15 = r187;
        goto L_0x0fee;
    L_0x185c:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r66;
        r6 = r0.w;
        r4.w = r6;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r66;
        r6 = r0.h;
        r4.h = r6;
        goto L_0x182e;
    L_0x1871:
        r58 = r58 + 1;
        goto L_0x17db;
    L_0x1875:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r89);
        if (r4 == 0) goto L_0x1907;
    L_0x187b:
        r0 = r89;
        r4 = r0.thumbs;
        r6 = 90;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObject = r4;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x18f5;
    L_0x188f:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x189f;
    L_0x1897:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x18f5;
    L_0x189f:
        r58 = 0;
    L_0x18a1:
        r0 = r89;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r58;
        if (r0 >= r4) goto L_0x18d3;
    L_0x18ad:
        r0 = r89;
        r4 = r0.attributes;
        r0 = r58;
        r66 = r4.get(r0);
        r66 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r66;
        r0 = r66;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 == 0) goto L_0x1904;
    L_0x18bf:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r66;
        r6 = r0.w;
        r4.w = r6;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r66;
        r6 = r0.h;
        r4.h = r6;
    L_0x18d3:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x18e3;
    L_0x18db:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x18f5;
    L_0x18e3:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r194;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.h = r8;
        r4.w = r8;
    L_0x18f5:
        r0 = r89;
        r1 = r194;
        r1.documentAttach = r0;
        r4 = 6;
        r0 = r194;
        r0.documentAttachType = r4;
        r15 = r187;
        goto L_0x0fee;
    L_0x1904:
        r58 = r58 + 1;
        goto L_0x18a1;
    L_0x1907:
        r0 = r194;
        r4 = r0.drawInstantViewType;
        r6 = 6;
        if (r4 != r6) goto L_0x19d1;
    L_0x190e:
        r0 = r89;
        r4 = r0.thumbs;
        r6 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObject = r4;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x1988;
    L_0x1922:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x1932;
    L_0x192a:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x1988;
    L_0x1932:
        r58 = 0;
    L_0x1934:
        r0 = r89;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r58;
        if (r0 >= r4) goto L_0x1966;
    L_0x1940:
        r0 = r89;
        r4 = r0.attributes;
        r0 = r58;
        r66 = r4.get(r0);
        r66 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r66;
        r0 = r66;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 == 0) goto L_0x19cd;
    L_0x1952:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r66;
        r6 = r0.w;
        r4.w = r6;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r66;
        r6 = r0.h;
        r4.h = r6;
    L_0x1966:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x1976;
    L_0x196e:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x1988;
    L_0x1976:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r194;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.h = r8;
        r4.w = r8;
    L_0x1988:
        r0 = r89;
        r1 = r194;
        r1.documentAttach = r0;
        r4 = 8;
        r0 = r194;
        r0.documentAttachType = r4;
        r0 = r194;
        r4 = r0.documentAttach;
        r4 = r4.size;
        r8 = (long) r4;
        r5 = org.telegram.messenger.AndroidUtilities.formatFileSize(r8);
        r4 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r194;
        r0.durationWidth = r4;
        r14 = new android.text.StaticLayout;
        r16 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r0 = r194;
        r0 = r0.durationWidth;
        r17 = r0;
        r18 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r19 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r20 = 0;
        r21 = 0;
        r15 = r5;
        r14.<init>(r15, r16, r17, r18, r19, r20, r21);
        r0 = r194;
        r0.videoInfoLayout = r14;
        r15 = r187;
        goto L_0x0fee;
    L_0x19cd:
        r58 = r58 + 1;
        goto L_0x1934;
    L_0x19d1:
        r0 = r194;
        r1 = r43;
        r2 = r176;
        r3 = r120;
        r0.calcBackgroundWidth(r1, r2, r3);
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r43;
        if (r4 >= r6) goto L_0x19f6;
    L_0x19ea:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r43;
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x19f6:
        r4 = org.telegram.messenger.MessageObject.isVoiceDocument(r89);
        if (r4 == 0) goto L_0x1ad2;
    L_0x19fc:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r1 = r195;
        r0.createDocumentLayout(r4, r1);
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.textHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r6 = r0.linkPreviewHeight;
        r4 = r4 + r6;
        r0 = r194;
        r0.mediaOffsetY = r4;
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        r4 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r43 = r43 - r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1a98;
    L_0x1a50:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x1a96;
    L_0x1a5a:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x1a96;
    L_0x1a60:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x1a96;
    L_0x1a66:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
    L_0x1a68:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x435CLASSNAME float:220.0 double:5.58344962E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r4 = r4 + r59;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);
    L_0x1a87:
        r0 = r194;
        r1 = r43;
        r2 = r176;
        r3 = r120;
        r0.calcBackgroundWidth(r1, r2, r3);
        r15 = r187;
        goto L_0x0fee;
    L_0x1a96:
        r4 = 0;
        goto L_0x1a68;
    L_0x1a98:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x1ad0;
    L_0x1aa2:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x1ad0;
    L_0x1aa8:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x1ad0;
    L_0x1aae:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
    L_0x1ab0:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x435CLASSNAME float:220.0 double:5.58344962E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r4 = r4 + r59;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);
        goto L_0x1a87;
    L_0x1ad0:
        r4 = 0;
        goto L_0x1ab0;
    L_0x1ad2:
        r4 = org.telegram.messenger.MessageObject.isMusicDocument(r89);
        if (r4 == 0) goto L_0x1ba3;
    L_0x1ad8:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r1 = r195;
        r91 = r0.createDocumentLayout(r4, r1);
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.textHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r6 = r0.linkPreviewHeight;
        r4 = r4 + r6;
        r0 = r194;
        r0.mediaOffsetY = r4;
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        r4 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r43 = r43 - r4;
        r4 = r91 + r59;
        r6 = NUM; // 0x42bCLASSNAME float:94.0 double:5.53164308E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);
        r0 = r194;
        r4 = r0.songLayout;
        if (r4 == 0) goto L_0x1b65;
    L_0x1b3c:
        r0 = r194;
        r4 = r0.songLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x1b65;
    L_0x1b46:
        r0 = r120;
        r4 = (float) r0;
        r0 = r194;
        r6 = r0.songLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r0 = r59;
        r8 = (float) r0;
        r6 = r6 + r8;
        r8 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r6 = r6 + r8;
        r4 = java.lang.Math.max(r4, r6);
        r0 = (int) r4;
        r120 = r0;
    L_0x1b65:
        r0 = r194;
        r4 = r0.performerLayout;
        if (r4 == 0) goto L_0x1b94;
    L_0x1b6b:
        r0 = r194;
        r4 = r0.performerLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x1b94;
    L_0x1b75:
        r0 = r120;
        r4 = (float) r0;
        r0 = r194;
        r6 = r0.performerLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r0 = r59;
        r8 = (float) r0;
        r6 = r6 + r8;
        r8 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r6 = r6 + r8;
        r4 = java.lang.Math.max(r4, r6);
        r0 = (int) r4;
        r120 = r0;
    L_0x1b94:
        r0 = r194;
        r1 = r43;
        r2 = r176;
        r3 = r120;
        r0.calcBackgroundWidth(r1, r2, r3);
        r15 = r187;
        goto L_0x0fee;
    L_0x1ba3:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x43280000 float:168.0 double:5.566612494E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r1 = r195;
        r0.createDocumentLayout(r4, r1);
        r4 = 1;
        r0 = r194;
        r0.drawImageButton = r4;
        r0 = r194;
        r4 = r0.drawPhotoImage;
        if (r4 == 0) goto L_0x1bff;
    L_0x1bc0:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r0 = r194;
        r8 = r0.totalHeight;
        r0 = r194;
        r9 = r0.namesOffset;
        r8 = r8 + r9;
        r9 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r4.setImageCoords(r6, r8, r9, r10);
        r15 = r187;
        goto L_0x0fee;
    L_0x1bff:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.textHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r6 = r0.linkPreviewHeight;
        r4 = r4 + r6;
        r0 = r194;
        r0.mediaOffsetY = r4;
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r0 = r194;
        r8 = r0.totalHeight;
        r0 = r194;
        r9 = r0.namesOffset;
        r8 = r8 + r9;
        r9 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r8 = r8 - r9;
        r9 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r4.setImageCoords(r6, r8, r9, r10);
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        r0 = r194;
        r4 = r0.docTitleLayout;
        if (r4 == 0) goto L_0x4d9c;
    L_0x1c5d:
        r0 = r194;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        r6 = 1;
        if (r4 <= r6) goto L_0x4d9c;
    L_0x1CLASSNAME:
        r0 = r194;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        r4 = r4 + -1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r99 = r4 * r6;
        r0 = r194;
        r4 = r0.totalHeight;
        r4 = r4 + r99;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        r4 = r4 + r99;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        r15 = r187;
        goto L_0x0fee;
    L_0x1CLASSNAME:
        if (r143 == 0) goto L_0x1ced;
    L_0x1CLASSNAME:
        if (r181 == 0) goto L_0x1ce3;
    L_0x1CLASSNAME:
        r4 = "photo";
        r0 = r181;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x1ce3;
    L_0x1ca1:
        r110 = 1;
    L_0x1ca3:
        r0 = r195;
        r8 = r0.photoThumbs;
        if (r110 != 0) goto L_0x1cab;
    L_0x1ca9:
        if (r169 != 0) goto L_0x1ce6;
    L_0x1cab:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r6 = r4;
    L_0x1cb0:
        if (r110 != 0) goto L_0x1ce9;
    L_0x1cb2:
        r4 = 1;
    L_0x1cb3:
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r6, r4);
        r0 = r194;
        r0.currentPhotoObject = r4;
        if (r110 != 0) goto L_0x1ceb;
    L_0x1cbd:
        r4 = 1;
    L_0x1cbe:
        r0 = r194;
        r0.checkOnlyButtonPressed = r4;
        r0 = r195;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
        r0 = r194;
        r4 = r0.currentPhotoObjectThumb;
        r0 = r194;
        r6 = r0.currentPhotoObject;
        if (r4 != r6) goto L_0x1cdf;
    L_0x1cda:
        r4 = 0;
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
    L_0x1cdf:
        r15 = r187;
        goto L_0x0fee;
    L_0x1ce3:
        r110 = 0;
        goto L_0x1ca3;
    L_0x1ce6:
        r6 = r122;
        goto L_0x1cb0;
    L_0x1ce9:
        r4 = 0;
        goto L_0x1cb3;
    L_0x1ceb:
        r4 = 0;
        goto L_0x1cbe;
    L_0x1ced:
        if (r187 == 0) goto L_0x4d9c;
    L_0x1cef:
        r0 = r187;
        r4 = r0.mime_type;
        r6 = "image/";
        r4 = r4.startsWith(r6);
        if (r4 != 0) goto L_0x4d98;
    L_0x1cfc:
        r15 = 0;
    L_0x1cfd:
        r4 = 0;
        r0 = r194;
        r0.drawImageButton = r4;
        goto L_0x0fee;
    L_0x1d04:
        r4 = 0;
        goto L_0x1049;
    L_0x1d07:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r4 == 0) goto L_0x1d21;
    L_0x1d0f:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x1d21;
    L_0x1d17:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r4.w;
        r122 = r0;
        goto L_0x107d;
    L_0x1d21:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 6;
        if (r4 == r6) goto L_0x1d30;
    L_0x1d28:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 8;
        if (r4 != r6) goto L_0x1d50;
    L_0x1d30:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1d43;
    L_0x1d36:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r122 = r0;
        goto L_0x107d;
    L_0x1d43:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r122 = r0;
        goto L_0x107d;
    L_0x1d50:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 7;
        if (r4 != r6) goto L_0x107d;
    L_0x1d57:
        r122 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setAllowDecodeSingleFrame(r6);
        goto L_0x107d;
    L_0x1d63:
        r4 = 0;
        goto L_0x1089;
    L_0x1d66:
        r4 = -1;
        r15.size = r4;
        goto L_0x10ad;
    L_0x1d6b:
        r0 = r194;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x1d77;
    L_0x1d71:
        r0 = r194;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x1d9b;
    L_0x1d77:
        r190 = 640; // 0x280 float:8.97E-43 double:3.16E-321;
        r103 = 360; // 0x168 float:5.04E-43 double:1.78E-321;
        r0 = r190;
        r4 = (float) r0;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r122 - r6;
        r6 = (float) r6;
        r163 = r4 / r6;
        r0 = r190;
        r4 = (float) r0;
        r4 = r4 / r163;
        r0 = (int) r4;
        r190 = r0;
        r0 = r103;
        r4 = (float) r0;
        r4 = r4 / r163;
        r0 = (int) r4;
        r103 = r0;
        goto L_0x10cc;
    L_0x1d9b:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r4.w;
        r190 = r0;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r4.h;
        r103 = r0;
        r0 = r190;
        r4 = (float) r0;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r122 - r6;
        r6 = (float) r6;
        r163 = r4 / r6;
        r0 = r190;
        r4 = (float) r0;
        r4 = r4 / r163;
        r0 = (int) r4;
        r190 = r0;
        r0 = r103;
        r4 = (float) r0;
        r4 = r4 / r163;
        r0 = (int) r4;
        r103 = r0;
        if (r7 == 0) goto L_0x1de0;
    L_0x1dcb:
        if (r7 == 0) goto L_0x1e14;
    L_0x1dcd:
        r4 = r7.toLowerCase();
        r6 = "instagram";
        r4 = r4.equals(r6);
        if (r4 != 0) goto L_0x1e14;
    L_0x1dda:
        r0 = r194;
        r4 = r0.documentAttachType;
        if (r4 != 0) goto L_0x1e14;
    L_0x1de0:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r4 = r4 / 3;
        r0 = r103;
        if (r0 <= r4) goto L_0x1df0;
    L_0x1dea:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r103 = r4 / 3;
    L_0x1df0:
        r0 = r194;
        r4 = r0.imageBackgroundSideColor;
        if (r4 == 0) goto L_0x10cc;
    L_0x1df6:
        r0 = r103;
        r4 = (float) r0;
        r6 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r163 = r4 / r6;
        r0 = r190;
        r4 = (float) r0;
        r4 = r4 / r163;
        r0 = (int) r4;
        r190 = r0;
        r0 = r103;
        r4 = (float) r0;
        r4 = r4 / r163;
        r0 = (int) r4;
        r103 = r0;
        goto L_0x10cc;
    L_0x1e14:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r4 = r4 / 2;
        r0 = r103;
        if (r0 <= r4) goto L_0x1df0;
    L_0x1e1e:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r103 = r4 / 2;
        goto L_0x1df0;
    L_0x1e25:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r103;
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        r4 = r4 + r103;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        goto L_0x1118;
    L_0x1e42:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r8 = 0;
        r0 = r190;
        r1 = r103;
        r4.setImageCoords(r6, r8, r0, r1);
        goto L_0x113f;
    L_0x1e51:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 8;
        if (r4 != r6) goto L_0x1eb1;
    L_0x1e59:
        r0 = r195;
        r4 = r0.mediaExists;
        if (r4 == 0) goto L_0x1e8a;
    L_0x1e5f:
        r0 = r194;
        r0 = r0.photoImage;
        r16 = r0;
        r0 = r194;
        r0 = r0.documentAttach;
        r17 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r18 = r0;
        r19 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r20 = r0;
        r21 = "b1";
        r22 = 0;
        r23 = "jpg";
        r25 = 1;
        r24 = r195;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24, r25);
        goto L_0x119b;
    L_0x1e8a:
        r0 = r194;
        r0 = r0.photoImage;
        r16 = r0;
        r17 = 0;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r18 = r0;
        r19 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r20 = r0;
        r21 = "b1";
        r22 = 0;
        r23 = "jpg";
        r25 = 1;
        r24 = r195;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24, r25);
        goto L_0x119b;
    L_0x1eb1:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 6;
        if (r4 != r6) goto L_0x1ee9;
    L_0x1eb8:
        r0 = r194;
        r0 = r0.photoImage;
        r16 = r0;
        r0 = r194;
        r0 = r0.documentAttach;
        r17 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r18 = r0;
        r19 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r20 = r0;
        r21 = "b1";
        r0 = r194;
        r4 = r0.documentAttach;
        r0 = r4.size;
        r22 = r0;
        r23 = "webp";
        r25 = 1;
        r24 = r195;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24, r25);
        goto L_0x119b;
    L_0x1ee9:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 4;
        if (r4 != r6) goto L_0x1fe6;
    L_0x1ef0:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setNeedsQualityThumb(r6);
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setShouldGenerateQualityThumb(r6);
        r4 = org.telegram.messenger.SharedConfig.autoplayVideo;
        if (r4 == 0) goto L_0x1var_;
    L_0x1var_:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.mediaExists;
        if (r4 != 0) goto L_0x1var_;
    L_0x1f0c:
        r4 = r195.canStreamVideo();
        if (r4 == 0) goto L_0x1var_;
    L_0x1var_:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r194;
        r6 = r0.currentMessageObject;
        r4 = r4.canDownloadMedia(r6);
        if (r4 == 0) goto L_0x1var_;
    L_0x1var_:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setAllowStartAnimation(r6);
        r0 = r194;
        r4 = r0.photoImage;
        r4.startAnimation();
        r0 = r194;
        r0 = r0.photoImage;
        r16 = r0;
        r0 = r194;
        r0 = r0.documentAttach;
        r17 = r0;
        r18 = "g";
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r19 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r20 = r0;
        r21 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r22 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r23 = r0;
        r0 = r194;
        r4 = r0.documentAttach;
        r0 = r4.size;
        r24 = r0;
        r25 = 0;
        r27 = 0;
        r26 = r195;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27);
        r4 = 1;
        r0 = r194;
        r0.autoPlayingVideo = r4;
        goto L_0x119b;
    L_0x1var_:
        r0 = r194;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x1fa5;
    L_0x1f7a:
        r0 = r194;
        r0 = r0.photoImage;
        r16 = r0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r17 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r18 = r0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r19 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r20 = r0;
        r21 = 0;
        r22 = 0;
        r24 = 0;
        r23 = r195;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x119b;
    L_0x1fa5:
        r0 = r194;
        r0 = r0.photoImage;
        r16 = r0;
        r17 = 0;
        r18 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r19 = r0;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r4 != 0) goto L_0x1fcc;
    L_0x1fbd:
        r4 = "s";
        r0 = r194;
        r6 = r0.currentPhotoObject;
        r6 = r6.type;
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x1fdf;
    L_0x1fcc:
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r20 = r0;
    L_0x1fd2:
        r21 = 0;
        r22 = 0;
        r24 = 0;
        r23 = r195;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x119b;
    L_0x1fdf:
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r20 = r0;
        goto L_0x1fd2;
    L_0x1fe6:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 2;
        if (r4 == r6) goto L_0x1ff4;
    L_0x1fed:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 7;
        if (r4 != r6) goto L_0x20b2;
    L_0x1ff4:
        r93 = org.telegram.messenger.FileLoader.getAttachFileName(r89);
        r69 = 0;
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r89);
        if (r4 == 0) goto L_0x206f;
    L_0x2000:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r6 = r6 / 2;
        r4.setRoundRadius(r6);
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r194;
        r6 = r0.currentMessageObject;
        r69 = r4.canDownloadMedia(r6);
    L_0x201b:
        r4 = r195.isSending();
        if (r4 != 0) goto L_0x2086;
    L_0x2021:
        r4 = r195.isEditing();
        if (r4 != 0) goto L_0x2086;
    L_0x2027:
        r0 = r195;
        r4 = r0.mediaExists;
        if (r4 != 0) goto L_0x203f;
    L_0x202d:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r93;
        r4 = r4.isLoadingFile(r0);
        if (r4 != 0) goto L_0x203f;
    L_0x203d:
        if (r69 == 0) goto L_0x2086;
    L_0x203f:
        r4 = 0;
        r0 = r194;
        r0.photoNotSet = r4;
        r0 = r194;
        r0 = r0.photoImage;
        r16 = r0;
        r18 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r19 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r20 = r0;
        r0 = r89;
        r0 = r0.size;
        r21 = r0;
        r22 = 0;
        r0 = r194;
        r0 = r0.currentMessageObject;
        r23 = r0;
        r24 = 0;
        r17 = r89;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x119b;
    L_0x206f:
        r4 = org.telegram.messenger.MessageObject.isNewGifDocument(r89);
        if (r4 == 0) goto L_0x201b;
    L_0x2075:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r194;
        r6 = r0.currentMessageObject;
        r69 = r4.canDownloadMedia(r6);
        goto L_0x201b;
    L_0x2086:
        r4 = 1;
        r0 = r194;
        r0.photoNotSet = r4;
        r0 = r194;
        r0 = r0.photoImage;
        r16 = r0;
        r17 = 0;
        r18 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r19 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r20 = r0;
        r21 = 0;
        r22 = 0;
        r0 = r194;
        r0 = r0.currentMessageObject;
        r23 = r0;
        r24 = 0;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x119b;
    L_0x20b2:
        r0 = r195;
        r0 = r0.mediaExists;
        r144 = r0;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r93 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r0 = r194;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x20ea;
    L_0x20c6:
        if (r144 != 0) goto L_0x20ea;
    L_0x20c8:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r194;
        r6 = r0.currentMessageObject;
        r4 = r4.canDownloadMedia(r6);
        if (r4 != 0) goto L_0x20ea;
    L_0x20da:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r93;
        r4 = r4.isLoadingFile(r0);
        if (r4 == 0) goto L_0x211a;
    L_0x20ea:
        r4 = 0;
        r0 = r194;
        r0.photoNotSet = r4;
        r0 = r194;
        r0 = r0.photoImage;
        r16 = r0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r17 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r18 = r0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r19 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r20 = r0;
        r21 = 0;
        r22 = 0;
        r24 = 0;
        r23 = r195;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x119b;
    L_0x211a:
        r4 = 1;
        r0 = r194;
        r0.photoNotSet = r4;
        r0 = r194;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x215c;
    L_0x2125:
        r0 = r194;
        r0 = r0.photoImage;
        r16 = r0;
        r17 = 0;
        r18 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r19 = r0;
        r4 = java.util.Locale.US;
        r6 = "%d_%d_b";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r190);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r103);
        r8[r9] = r10;
        r20 = java.lang.String.format(r4, r6, r8);
        r21 = 0;
        r22 = 0;
        r24 = 0;
        r23 = r195;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x119b;
    L_0x215c:
        r0 = r194;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        goto L_0x119b;
    L_0x2168:
        r0 = r194;
        r4 = r0.hasGamePreview;
        if (r4 == 0) goto L_0x11fa;
    L_0x216e:
        r4 = "AttachGame";
        r6 = NUM; // 0x7f0CLASSNAMEcf float:1.8609612E38 double:1.0530975007E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
        r4 = org.telegram.ui.ActionBar.Theme.chat_gamePaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r194;
        r0.durationWidth = r4;
        r16 = new android.text.StaticLayout;
        r18 = org.telegram.ui.ActionBar.Theme.chat_gamePaint;
        r0 = r194;
        r0 = r0.durationWidth;
        r19 = r0;
        r20 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r22 = 0;
        r23 = 0;
        r17 = r5;
        r16.<init>(r17, r18, r19, r20, r21, r22, r23);
        r0 = r16;
        r1 = r194;
        r1.videoInfoLayout = r0;
        goto L_0x11fa;
    L_0x21ab:
        r0 = r194;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        r0 = r194;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.linkPreviewHeight = r4;
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        goto L_0x11fa;
    L_0x21d5:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.test;
        if (r4 == 0) goto L_0x21ef;
    L_0x21df:
        r4 = "PaymentTestInvoice";
        r6 = NUM; // 0x7f0CLASSNAMEef float:1.8612792E38 double:1.0530982754E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
        goto L_0x121a;
    L_0x21ef:
        r4 = "PaymentInvoice";
        r6 = NUM; // 0x7f0CLASSNAMEd0 float:1.861273E38 double:1.05309826E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
        goto L_0x121a;
    L_0x21ff:
        r4 = 0;
        goto L_0x12c4;
    L_0x2202:
        r0 = r194;
        r4 = r0.durationWidth;
        r4 = r4 + r177;
        r0 = r120;
        r120 = java.lang.Math.max(r4, r0);
        goto L_0x12f0;
    L_0x2210:
        r0 = r194;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        r0 = r194;
        r1 = r43;
        r2 = r176;
        r3 = r120;
        r0.calcBackgroundWidth(r1, r2, r3);
        goto L_0x132d;
    L_0x2227:
        r0 = r195;
        r4 = r0.type;
        r6 = 16;
        if (r4 != r6) goto L_0x23e0;
    L_0x222f:
        r4 = 0;
        r0 = r194;
        r0.drawName = r4;
        r4 = 0;
        r0 = r194;
        r0.drawForwardedName = r4;
        r4 = 0;
        r0 = r194;
        r0.drawPhotoImage = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2375;
    L_0x2244:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2371;
    L_0x224e:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x2371;
    L_0x2254:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x2371;
    L_0x225a:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x225c:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x2270:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.availableTimeWidth = r4;
        r4 = r194.getMaxNameWidth();
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        if (r43 >= 0) goto L_0x2293;
    L_0x228d:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r43 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x2293:
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r4 = r4.formatterDay;
        r0 = r195;
        r6 = r0.messageOwner;
        r6 = r6.date;
        r8 = (long) r6;
        r20 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r8 = r8 * r20;
        r174 = r4.format(r8);
        r0 = r195;
        r4 = r0.messageOwner;
        r0 = r4.action;
        r78 = r0;
        r78 = (org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall) r78;
        r0 = r78;
        r4 = r0.reason;
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        r109 = r0;
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x23b2;
    L_0x22c0:
        if (r109 == 0) goto L_0x23a6;
    L_0x22c2:
        r4 = "CallMessageOutgoingMissed";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8609912E38 double:1.053097574E-314;
        r171 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x22cc:
        r0 = r78;
        r4 = r0.duration;
        if (r4 <= 0) goto L_0x22f4;
    L_0x22d2:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r174;
        r4 = r4.append(r0);
        r6 = ", ";
        r4 = r4.append(r6);
        r0 = r78;
        r6 = r0.duration;
        r6 = org.telegram.messenger.LocaleController.formatCallDuration(r6);
        r4 = r4.append(r6);
        r174 = r4.toString();
    L_0x22f4:
        r18 = new android.text.StaticLayout;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r0 = r43;
        r6 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r171;
        r19 = android.text.TextUtils.ellipsize(r0, r4, r6, r8);
        r20 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r21 = r43 + r4;
        r22 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r23 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r24 = 0;
        r25 = 0;
        r18.<init>(r19, r20, r21, r22, r23, r24, r25);
        r0 = r18;
        r1 = r194;
        r1.titleLayout = r0;
        r18 = new android.text.StaticLayout;
        r4 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r0 = r43;
        r6 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r174;
        r19 = android.text.TextUtils.ellipsize(r0, r4, r6, r8);
        r20 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r21 = r43 + r4;
        r22 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r23 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r24 = 0;
        r25 = 0;
        r18.<init>(r19, r20, r21, r22, r23, r24, r25);
        r0 = r18;
        r1 = r194;
        r1.docTitleLayout = r0;
        r194.setMessageObjectInternal(r195);
        r4 = NUM; // 0x42820000 float:65.0 double:5.51286321E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x132d;
    L_0x2360:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.namesOffset = r4;
        goto L_0x132d;
    L_0x2371:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x225c;
    L_0x2375:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x23a3;
    L_0x237f:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x23a3;
    L_0x2385:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x23a3;
    L_0x238b:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x238d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x2270;
    L_0x23a3:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x238d;
    L_0x23a6:
        r4 = "CallMessageOutgoing";
        r6 = NUM; // 0x7f0CLASSNAME float:1.860991E38 double:1.0530975734E-314;
        r171 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x22cc;
    L_0x23b2:
        if (r109 == 0) goto L_0x23c0;
    L_0x23b4:
        r4 = "CallMessageIncomingMissed";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8609908E38 double:1.053097573E-314;
        r171 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x22cc;
    L_0x23c0:
        r0 = r78;
        r4 = r0.reason;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
        if (r4 == 0) goto L_0x23d4;
    L_0x23c8:
        r4 = "CallMessageIncomingDeclined";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8609906E38 double:1.0530975724E-314;
        r171 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x22cc;
    L_0x23d4:
        r4 = "CallMessageIncoming";
        r6 = NUM; // 0x7f0CLASSNAMEf float:1.8609904E38 double:1.053097572E-314;
        r171 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x22cc;
    L_0x23e0:
        r0 = r195;
        r4 = r0.type;
        r6 = 12;
        if (r4 != r6) goto L_0x267c;
    L_0x23e8:
        r4 = 0;
        r0 = r194;
        r0.drawName = r4;
        r4 = 1;
        r0 = r194;
        r0.drawForwardedName = r4;
        r4 = 1;
        r0 = r194;
        r0.drawPhotoImage = r4;
        r0 = r194;
        r4 = r0.photoImage;
        r6 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setRoundRadius(r6);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x25ab;
    L_0x240a:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x25a7;
    L_0x2414:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x25a7;
    L_0x241a:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x25a7;
    L_0x2420:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x2422:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x2436:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.availableTimeWidth = r4;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.user_id;
        r182 = r0;
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r6 = java.lang.Integer.valueOf(r182);
        r185 = r4.getUser(r6);
        r4 = r194.getMaxNameWidth();
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        if (r43 >= 0) goto L_0x2473;
    L_0x246d:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r43 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x2473:
        r19 = 0;
        if (r185 == 0) goto L_0x248e;
    L_0x2477:
        r0 = r185;
        r4 = r0.photo;
        if (r4 == 0) goto L_0x2485;
    L_0x247d:
        r0 = r185;
        r4 = r0.photo;
        r0 = r4.photo_small;
        r19 = r0;
    L_0x2485:
        r0 = r194;
        r4 = r0.contactAvatarDrawable;
        r0 = r185;
        r4.setInfo(r0);
    L_0x248e:
        r0 = r194;
        r0 = r0.photoImage;
        r18 = r0;
        r20 = "50_50";
        if (r185 == 0) goto L_0x25dc;
    L_0x2499:
        r0 = r194;
        r0 = r0.contactAvatarDrawable;
        r21 = r0;
    L_0x249f:
        r22 = 0;
        r24 = 0;
        r23 = r195;
        r18.setImage(r19, r20, r21, r22, r23, r24);
        r0 = r195;
        r4 = r0.vCardData;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x25eb;
    L_0x24b2:
        r0 = r195;
        r0 = r0.vCardData;
        r142 = r0;
        r4 = 1;
        r0 = r194;
        r0.drawInstantView = r4;
        r4 = 5;
        r0 = r194;
        r0.drawInstantViewType = r4;
    L_0x24c2:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.first_name;
        r0 = r195;
        r6 = r0.messageOwner;
        r6 = r6.media;
        r6 = r6.last_name;
        r4 = org.telegram.messenger.ContactsController.formatName(r4, r6);
        r6 = 10;
        r8 = 32;
        r84 = r4.replace(r6, r8);
        r4 = r84.length();
        if (r4 != 0) goto L_0x24f3;
    L_0x24e4:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.phone_number;
        r84 = r0;
        if (r84 != 0) goto L_0x24f3;
    L_0x24f0:
        r84 = "";
    L_0x24f3:
        r20 = new android.text.StaticLayout;
        r4 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
        r0 = r43;
        r6 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r84;
        r21 = android.text.TextUtils.ellipsize(r0, r4, r6, r8);
        r22 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r43 + r4;
        r24 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r25 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r26 = 0;
        r27 = 0;
        r20.<init>(r21, r22, r23, r24, r25, r26, r27);
        r0 = r20;
        r1 = r194;
        r1.titleLayout = r0;
        r20 = new android.text.StaticLayout;
        r22 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r43 + r4;
        r24 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r25 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = (float) r4;
        r26 = r0;
        r27 = 0;
        r21 = r142;
        r20.<init>(r21, r22, r23, r24, r25, r26, r27);
        r0 = r20;
        r1 = r194;
        r1.docTitleLayout = r0;
        r194.setMessageObjectInternal(r195);
        r0 = r194;
        r4 = r0.drawForwardedName;
        if (r4 == 0) goto L_0x2615;
    L_0x254c:
        r4 = r195.needDrawForwarded();
        if (r4 == 0) goto L_0x2615;
    L_0x2552:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x2560;
    L_0x2558:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.minY;
        if (r4 != 0) goto L_0x2615;
    L_0x2560:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.namesOffset = r4;
    L_0x256f:
        r4 = NUM; // 0x425CLASSNAME float:55.0 double:5.50055916E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r194;
        r6 = r0.docTitleLayout;
        r6 = r6.getHeight();
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x259c;
    L_0x258d:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.namesOffset = r4;
    L_0x259c:
        r0 = r194;
        r4 = r0.drawInstantView;
        if (r4 == 0) goto L_0x2634;
    L_0x25a2:
        r194.createInstantViewButton();
        goto L_0x132d;
    L_0x25a7:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2422;
    L_0x25ab:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x25d9;
    L_0x25b5:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x25d9;
    L_0x25bb:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x25d9;
    L_0x25c1:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x25c3:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x2436;
    L_0x25d9:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x25c3;
    L_0x25dc:
        r6 = org.telegram.ui.ActionBar.Theme.chat_contactDrawable;
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x25e9;
    L_0x25e4:
        r4 = 1;
    L_0x25e5:
        r21 = r6[r4];
        goto L_0x249f;
    L_0x25e9:
        r4 = 0;
        goto L_0x25e5;
    L_0x25eb:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.phone_number;
        r142 = r0;
        r4 = android.text.TextUtils.isEmpty(r142);
        if (r4 != 0) goto L_0x2609;
    L_0x25fb:
        r4 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r142 = (java.lang.String) r142;
        r0 = r142;
        r142 = r4.format(r0);
        goto L_0x24c2;
    L_0x2609:
        r4 = "NumberUnknown";
        r6 = NUM; // 0x7f0CLASSNAMEbc float:1.861217E38 double:1.0530981237E-314;
        r142 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x24c2;
    L_0x2615:
        r0 = r194;
        r4 = r0.drawNameLayout;
        if (r4 == 0) goto L_0x256f;
    L_0x261b:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.reply_to_msg_id;
        if (r4 != 0) goto L_0x256f;
    L_0x2623:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.namesOffset = r4;
        goto L_0x256f;
    L_0x2634:
        r0 = r194;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x132d;
    L_0x263e:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42dCLASSNAME float:110.0 double:5.54200439E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r6 = r0.docTitleLayout;
        r0 = r194;
        r8 = r0.docTitleLayout;
        r8 = r8.getLineCount();
        r8 = r8 + -1;
        r6 = r6.getLineWidth(r8);
        r8 = (double) r6;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r175 = r4 - r6;
        r0 = r194;
        r4 = r0.timeWidth;
        r0 = r175;
        if (r0 >= r4) goto L_0x132d;
    L_0x266b:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        goto L_0x132d;
    L_0x267c:
        r0 = r195;
        r4 = r0.type;
        r6 = 2;
        if (r4 != r6) goto L_0x2721;
    L_0x2683:
        r4 = 1;
        r0 = r194;
        r0.drawForwardedName = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x26f1;
    L_0x268e:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x26ee;
    L_0x2698:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x26ee;
    L_0x269e:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x26ee;
    L_0x26a4:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x26a6:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x26ba:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r0 = r194;
        r1 = r195;
        r0.createDocumentLayout(r4, r1);
        r194.setMessageObjectInternal(r195);
        r4 = NUM; // 0x428CLASSNAME float:70.0 double:5.51610112E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x132d;
    L_0x26dd:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.namesOffset = r4;
        goto L_0x132d;
    L_0x26ee:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x26a6;
    L_0x26f1:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x271e;
    L_0x26fb:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x271e;
    L_0x2701:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x271e;
    L_0x2707:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x2709:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x26ba;
    L_0x271e:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2709;
    L_0x2721:
        r0 = r195;
        r4 = r0.type;
        r6 = 14;
        if (r4 != r6) goto L_0x27c2;
    L_0x2729:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2792;
    L_0x272f:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x278f;
    L_0x2739:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x278f;
    L_0x273f:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x278f;
    L_0x2745:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x2747:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x275b:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r0 = r194;
        r1 = r195;
        r0.createDocumentLayout(r4, r1);
        r194.setMessageObjectInternal(r195);
        r4 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x132d;
    L_0x277e:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.namesOffset = r4;
        goto L_0x132d;
    L_0x278f:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2747;
    L_0x2792:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x27bf;
    L_0x279c:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x27bf;
    L_0x27a2:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x27bf;
    L_0x27a8:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x27aa:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x275b;
    L_0x27bf:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x27aa;
    L_0x27c2:
        r0 = r195;
        r4 = r0.type;
        r6 = 17;
        if (r4 != r6) goto L_0x2d36;
    L_0x27ca:
        r194.createSelectorDrawable();
        r4 = 1;
        r0 = r194;
        r0.drawName = r4;
        r4 = 1;
        r0 = r194;
        r0.drawForwardedName = r4;
        r4 = 0;
        r0 = r194;
        r0.drawPhotoImage = r4;
        r4 = NUM; // 0x43fa0000 float:500.0 double:5.634608575E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = r195.getMaxMessageTextWidth();
        r43 = java.lang.Math.min(r4, r6);
        r0 = r43;
        r1 = r194;
        r1.availableTimeWidth = r0;
        r4 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r43;
        r0 = r194;
        r0.backgroundWidth = r4;
        r4 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r0.availableTimeWidth = r4;
        r194.measureTime(r195);
        r0 = r195;
        r4 = r0.messageOwner;
        r0 = r4.media;
        r126 = r0;
        r126 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r126;
        r0 = r126;
        r4 = r0.poll;
        r4 = r4.closed;
        r0 = r194;
        r0.pollClosed = r4;
        r4 = r195.isVoted();
        r0 = r194;
        r0.pollVoted = r4;
        r20 = new android.text.StaticLayout;
        r0 = r126;
        r4 = r0.poll;
        r4 = r4.question;
        r6 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r6 = r6.getFontMetricsInt();
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r9 = 0;
        r21 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r8, r9);
        r22 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r43 + r4;
        r24 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r25 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r26 = 0;
        r27 = 0;
        r20.<init>(r21, r22, r23, r24, r25, r26, r27);
        r0 = r20;
        r1 = r194;
        r1.titleLayout = r0;
        r180 = 0;
        r0 = r194;
        r4 = r0.titleLayout;
        if (r4 == 0) goto L_0x2882;
    L_0x2861:
        r58 = 0;
        r0 = r194;
        r4 = r0.titleLayout;
        r56 = r4.getLineCount();
    L_0x286b:
        r0 = r58;
        r1 = r56;
        if (r0 >= r1) goto L_0x2882;
    L_0x2871:
        r0 = r194;
        r4 = r0.titleLayout;
        r0 = r58;
        r4 = r4.getLineLeft(r0);
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x2a43;
    L_0x2880:
        r180 = 1;
    L_0x2882:
        r20 = new android.text.StaticLayout;
        r0 = r126;
        r4 = r0.poll;
        r4 = r4.closed;
        if (r4 == 0) goto L_0x2a47;
    L_0x288c:
        r4 = "FinalResults";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8610979E38 double:1.0530978337E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x2896:
        r6 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r0 = r43;
        r8 = (float) r0;
        r9 = android.text.TextUtils.TruncateAt.END;
        r21 = android.text.TextUtils.ellipsize(r4, r6, r8, r9);
        r22 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r43 + r4;
        r24 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r25 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r26 = 0;
        r27 = 0;
        r20.<init>(r21, r22, r23, r24, r25, r26, r27);
        r0 = r20;
        r1 = r194;
        r1.docTitleLayout = r0;
        r0 = r194;
        r4 = r0.docTitleLayout;
        if (r4 == 0) goto L_0x28e9;
    L_0x28c2:
        r0 = r194;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x28e9;
    L_0x28cc:
        if (r180 == 0) goto L_0x2a53;
    L_0x28ce:
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 != 0) goto L_0x2a53;
    L_0x28d2:
        r0 = r43;
        r4 = (float) r0;
        r0 = r194;
        r6 = r0.docTitleLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r4 = r4 - r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r194;
        r0.docTitleOffsetX = r4;
    L_0x28e9:
        r0 = r194;
        r4 = r0.timeWidth;
        r6 = r43 - r4;
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x2a6f;
    L_0x28f5:
        r4 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
    L_0x28f7:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r6 - r4;
        r20 = new android.text.StaticLayout;
        r0 = r126;
        r4 = r0.results;
        r4 = r4.total_voters;
        if (r4 != 0) goto L_0x2a73;
    L_0x2907:
        r4 = "NoVotes";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8611898E38 double:1.0530980575E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x2911:
        r6 = org.telegram.ui.ActionBar.Theme.chat_livePaint;
        r0 = r23;
        r8 = (float) r0;
        r9 = android.text.TextUtils.TruncateAt.END;
        r21 = android.text.TextUtils.ellipsize(r4, r6, r8, r9);
        r22 = org.telegram.ui.ActionBar.Theme.chat_livePaint;
        r24 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r25 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r26 = 0;
        r27 = 0;
        r20.<init>(r21, r22, r23, r24, r25, r26, r27);
        r0 = r20;
        r1 = r194;
        r1.infoLayout = r0;
        r0 = r194;
        r4 = r0.infoLayout;
        if (r4 == 0) goto L_0x2a82;
    L_0x2935:
        r0 = r194;
        r4 = r0.infoLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x2a82;
    L_0x293f:
        r0 = r194;
        r4 = r0.infoLayout;
        r6 = 0;
        r4 = r4.getLineLeft(r6);
        r4 = -r4;
        r8 = (double) r4;
    L_0x294a:
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r194;
        r0.infoX = r4;
        r0 = r126;
        r4 = r0.poll;
        r0 = r194;
        r0.lastPoll = r4;
        r0 = r126;
        r4 = r0.results;
        r4 = r4.results;
        r0 = r194;
        r0.lastPollResults = r4;
        r0 = r126;
        r4 = r0.results;
        r4 = r4.total_voters;
        r0 = r194;
        r0.lastPollResultsVoters = r4;
        r124 = 0;
        r0 = r194;
        r4 = r0.animatePollAnswer;
        if (r4 != 0) goto L_0x2984;
    L_0x2977:
        r0 = r194;
        r4 = r0.pollVoteInProgress;
        if (r4 == 0) goto L_0x2984;
    L_0x297d:
        r4 = 3;
        r6 = 2;
        r0 = r194;
        r0.performHapticFeedback(r4, r6);
    L_0x2984:
        r0 = r194;
        r4 = r0.attachedToWindow;
        if (r4 == 0) goto L_0x2a86;
    L_0x298a:
        r0 = r194;
        r4 = r0.pollVoteInProgress;
        if (r4 != 0) goto L_0x2996;
    L_0x2990:
        r0 = r194;
        r4 = r0.pollUnvoteInProgress;
        if (r4 == 0) goto L_0x2a86;
    L_0x2996:
        r4 = 1;
    L_0x2997:
        r0 = r194;
        r0.animatePollAnswer = r4;
        r0 = r194;
        r0.animatePollAnswerAlpha = r4;
        r153 = 0;
        r170 = new java.util.ArrayList;
        r170.<init>();
        r0 = r194;
        r4 = r0.pollButtons;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x2a8c;
    L_0x29b0:
        r153 = new java.util.ArrayList;
        r0 = r194;
        r4 = r0.pollButtons;
        r0 = r153;
        r0.<init>(r4);
        r0 = r194;
        r4 = r0.pollButtons;
        r4.clear();
        r0 = r194;
        r4 = r0.animatePollAnswer;
        if (r4 != 0) goto L_0x29df;
    L_0x29c8:
        r0 = r194;
        r4 = r0.attachedToWindow;
        if (r4 == 0) goto L_0x2a89;
    L_0x29ce:
        r0 = r194;
        r4 = r0.pollVoted;
        if (r4 != 0) goto L_0x29da;
    L_0x29d4:
        r0 = r194;
        r4 = r0.pollClosed;
        if (r4 == 0) goto L_0x2a89;
    L_0x29da:
        r4 = 1;
    L_0x29db:
        r0 = r194;
        r0.animatePollAnswer = r4;
    L_0x29df:
        r0 = r194;
        r4 = r0.pollAnimationProgress;
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 <= 0) goto L_0x2a8c;
    L_0x29e8:
        r0 = r194;
        r4 = r0.pollAnimationProgress;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x2a8c;
    L_0x29f2:
        r70 = 0;
        r57 = r153.size();
    L_0x29f8:
        r0 = r70;
        r1 = r57;
        if (r0 >= r1) goto L_0x2a8c;
    L_0x29fe:
        r0 = r153;
        r1 = r70;
        r75 = r0.get(r1);
        r75 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r75;
        r4 = r75.prevPercent;
        r4 = (float) r4;
        r6 = r75.percent;
        r8 = r75.prevPercent;
        r6 = r6 - r8;
        r6 = (float) r6;
        r0 = r194;
        r8 = r0.pollAnimationProgress;
        r6 = r6 * r8;
        r4 = r4 + r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r75;
        r0.percent = r4;
        r4 = r75.prevPercentProgress;
        r6 = r75.percentProgress;
        r8 = r75.prevPercentProgress;
        r6 = r6 - r8;
        r0 = r194;
        r8 = r0.pollAnimationProgress;
        r6 = r6 * r8;
        r4 = r4 + r6;
        r0 = r75;
        r0.percentProgress = r4;
        r70 = r70 + 1;
        goto L_0x29f8;
    L_0x2a43:
        r58 = r58 + 1;
        goto L_0x286b;
    L_0x2a47:
        r4 = "AnonymousPoll";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8609488E38 double:1.0530974706E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x2896;
    L_0x2a53:
        if (r180 != 0) goto L_0x28e9;
    L_0x2a55:
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x28e9;
    L_0x2a59:
        r0 = r194;
        r4 = r0.docTitleLayout;
        r6 = 0;
        r4 = r4.getLineLeft(r6);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r4 = -r4;
        r0 = r194;
        r0.docTitleOffsetX = r4;
        goto L_0x28e9;
    L_0x2a6f:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x28f7;
    L_0x2a73:
        r4 = "Vote";
        r0 = r126;
        r6 = r0.results;
        r6 = r6.total_voters;
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r6);
        goto L_0x2911;
    L_0x2a82:
        r8 = 0;
        goto L_0x294a;
    L_0x2a86:
        r4 = 0;
        goto L_0x2997;
    L_0x2a89:
        r4 = 0;
        goto L_0x29db;
    L_0x2a8c:
        r0 = r194;
        r4 = r0.animatePollAnswer;
        if (r4 == 0) goto L_0x2CLASSNAME;
    L_0x2a92:
        r4 = 0;
    L_0x2a93:
        r0 = r194;
        r0.pollAnimationProgress = r4;
        r0 = r194;
        r4 = r0.animatePollAnswerAlpha;
        if (r4 != 0) goto L_0x2c5b;
    L_0x2a9d:
        r4 = 0;
        r0 = r194;
        r0.pollVoteInProgress = r4;
        r4 = -1;
        r0 = r194;
        r0.pollVoteInProgressNum = r4;
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.SendMessagesHelper.getInstance(r4);
        r0 = r194;
        r6 = r0.currentMessageObject;
        r186 = r4.isSendingVote(r6);
    L_0x2ab7:
        r0 = r194;
        r4 = r0.titleLayout;
        if (r4 == 0) goto L_0x2c5f;
    L_0x2abd:
        r0 = r194;
        r4 = r0.titleLayout;
        r103 = r4.getHeight();
    L_0x2ac5:
        r159 = 100;
        r101 = 0;
        r152 = 0;
        r58 = 0;
        r0 = r126;
        r4 = r0.poll;
        r4 = r4.answers;
        r56 = r4.size();
    L_0x2ad7:
        r0 = r58;
        r1 = r56;
        if (r0 >= r1) goto L_0x2c8b;
    L_0x2add:
        r75 = new org.telegram.ui.Cells.ChatMessageCell$PollButton;
        r4 = 0;
        r0 = r75;
        r1 = r194;
        r0.<init>(r1, r4);
        r0 = r126;
        r4 = r0.poll;
        r4 = r4.answers;
        r0 = r58;
        r4 = r4.get(r0);
        r4 = (org.telegram.tgnet.TLRPC.TL_pollAnswer) r4;
        r0 = r75;
        r0.answer = r4;
        r24 = new android.text.StaticLayout;
        r4 = r75.answer;
        r4 = r4.text;
        r6 = org.telegram.ui.ActionBar.Theme.chat_audioPerformerPaint;
        r6 = r6.getFontMetricsInt();
        r8 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r9 = 0;
        r25 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r8, r9);
        r26 = org.telegram.ui.ActionBar.Theme.chat_audioPerformerPaint;
        r4 = NUM; // 0x42040000 float:33.0 double:5.47206556E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r27 = r43 - r4;
        r28 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r29 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r30 = 0;
        r31 = 0;
        r24.<init>(r25, r26, r27, r28, r29, r30, r31);
        r0 = r75;
        r1 = r24;
        r0.title = r1;
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r103;
        r0 = r75;
        r0.y = r4;
        r4 = r75.title;
        r4 = r4.getHeight();
        r0 = r75;
        r0.height = r4;
        r0 = r194;
        r4 = r0.pollButtons;
        r0 = r75;
        r4.add(r0);
        r0 = r170;
        r1 = r75;
        r0.add(r1);
        r4 = r75.height;
        r6 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r103 = r103 + r4;
        r0 = r126;
        r4 = r0.results;
        r4 = r4.results;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x2bfa;
    L_0x2b72:
        r70 = 0;
        r0 = r126;
        r4 = r0.results;
        r4 = r4.results;
        r57 = r4.size();
    L_0x2b7e:
        r0 = r70;
        r1 = r57;
        if (r0 >= r1) goto L_0x2bfa;
    L_0x2b84:
        r0 = r126;
        r4 = r0.results;
        r4 = r4.results;
        r0 = r70;
        r64 = r4.get(r0);
        r64 = (org.telegram.tgnet.TLRPC.TL_pollAnswerVoters) r64;
        r4 = r75.answer;
        r4 = r4.option;
        r0 = r64;
        r6 = r0.option;
        r4 = java.util.Arrays.equals(r4, r6);
        if (r4 == 0) goto L_0x2CLASSNAME;
    L_0x2ba2:
        r0 = r194;
        r4 = r0.pollVoted;
        if (r4 != 0) goto L_0x2bae;
    L_0x2ba8:
        r0 = r194;
        r4 = r0.pollClosed;
        if (r4 == 0) goto L_0x2CLASSNAME;
    L_0x2bae:
        r0 = r126;
        r4 = r0.results;
        r4 = r4.total_voters;
        if (r4 <= 0) goto L_0x2CLASSNAME;
    L_0x2bb6:
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r0 = r64;
        r6 = r0.voters;
        r6 = (float) r6;
        r0 = r126;
        r8 = r0.results;
        r8 = r8.total_voters;
        r8 = (float) r8;
        r6 = r6 / r8;
        r4 = r4 * r6;
        r0 = r75;
        r0.decimal = r4;
        r4 = r75.decimal;
        r4 = (int) r4;
        r0 = r75;
        r0.percent = r4;
        r4 = r75.decimal;
        r6 = r75.percent;
        r6 = (float) r6;
        r4 = r4 - r6;
        r0 = r75;
        r0.decimal = r4;
    L_0x2be4:
        if (r152 != 0) goto L_0x2CLASSNAME;
    L_0x2be6:
        r152 = r75.percent;
    L_0x2bea:
        r4 = r75.percent;
        r159 = r159 - r4;
        r4 = r75.percent;
        r0 = r124;
        r124 = java.lang.Math.max(r4, r0);
    L_0x2bfa:
        if (r153 == 0) goto L_0x2CLASSNAME;
    L_0x2bfc:
        r70 = 0;
        r57 = r153.size();
    L_0x2CLASSNAME:
        r0 = r70;
        r1 = r57;
        if (r0 >= r1) goto L_0x2CLASSNAME;
    L_0x2CLASSNAME:
        r0 = r153;
        r1 = r70;
        r151 = r0.get(r1);
        r151 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r151;
        r4 = r75.answer;
        r4 = r4.option;
        r6 = r151.answer;
        r6 = r6.option;
        r4 = java.util.Arrays.equals(r4, r6);
        if (r4 == 0) goto L_0x2CLASSNAME;
    L_0x2CLASSNAME:
        r4 = r151.percent;
        r0 = r75;
        r0.prevPercent = r4;
        r4 = r151.percentProgress;
        r0 = r75;
        r0.prevPercentProgress = r4;
    L_0x2CLASSNAME:
        if (r186 == 0) goto L_0x2CLASSNAME;
    L_0x2CLASSNAME:
        r4 = r75.answer;
        r4 = r4.option;
        r0 = r186;
        r4 = java.util.Arrays.equals(r4, r0);
        if (r4 == 0) goto L_0x2CLASSNAME;
    L_0x2CLASSNAME:
        r0 = r58;
        r1 = r194;
        r1.pollVoteInProgressNum = r0;
        r4 = 1;
        r0 = r194;
        r0.pollVoteInProgress = r4;
        r186 = 0;
    L_0x2CLASSNAME:
        r58 = r58 + 1;
        goto L_0x2ad7;
    L_0x2CLASSNAME:
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x2a93;
    L_0x2c5b:
        r186 = 0;
        goto L_0x2ab7;
    L_0x2c5f:
        r103 = 0;
        goto L_0x2ac5;
    L_0x2CLASSNAME:
        r4 = 0;
        r0 = r75;
        r0.percent = r4;
        r4 = 0;
        r0 = r75;
        r0.decimal = r4;
        goto L_0x2be4;
    L_0x2CLASSNAME:
        r4 = r75.percent;
        if (r4 == 0) goto L_0x2bea;
    L_0x2CLASSNAME:
        r4 = r75.percent;
        r0 = r152;
        if (r0 == r4) goto L_0x2bea;
    L_0x2c7f:
        r101 = 1;
        goto L_0x2bea;
    L_0x2CLASSNAME:
        r70 = r70 + 1;
        goto L_0x2b7e;
    L_0x2CLASSNAME:
        r70 = r70 + 1;
        goto L_0x2CLASSNAME;
    L_0x2c8b:
        if (r101 == 0) goto L_0x2cbe;
    L_0x2c8d:
        if (r159 == 0) goto L_0x2cbe;
    L_0x2c8f:
        r4 = org.telegram.ui.Cells.ChatMessageCell$$Lambda$0.$instance;
        r0 = r170;
        java.util.Collections.sort(r0, r4);
        r58 = 0;
        r4 = r170.size();
        r0 = r159;
        r56 = java.lang.Math.min(r0, r4);
    L_0x2ca2:
        r0 = r58;
        r1 = r56;
        if (r0 >= r1) goto L_0x2cbe;
    L_0x2ca8:
        r0 = r170;
        r1 = r58;
        r4 = r0.get(r1);
        r4 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r4;
        r6 = r4.percent;
        r6 = r6 + 1;
        r4.percent = r6;
        r58 = r58 + 1;
        goto L_0x2ca2;
    L_0x2cbe:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r190 = r4 - r6;
        r70 = 0;
        r0 = r194;
        r4 = r0.pollButtons;
        r57 = r4.size();
    L_0x2cd4:
        r0 = r70;
        r1 = r57;
        if (r0 >= r1) goto L_0x2d0b;
    L_0x2cda:
        r0 = r194;
        r4 = r0.pollButtons;
        r0 = r70;
        r75 = r4.get(r0);
        r75 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r75;
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r0 = r190;
        r6 = (float) r0;
        r6 = r4 / r6;
        if (r124 == 0) goto L_0x2d09;
    L_0x2cf4:
        r4 = r75.percent;
        r4 = (float) r4;
        r0 = r124;
        r8 = (float) r0;
        r4 = r4 / r8;
    L_0x2cfd:
        r4 = java.lang.Math.max(r6, r4);
        r0 = r75;
        r0.percentProgress = r4;
        r70 = r70 + 1;
        goto L_0x2cd4;
    L_0x2d09:
        r4 = 0;
        goto L_0x2cfd;
    L_0x2d0b:
        r194.setMessageObjectInternal(r195);
        r4 = NUM; // 0x42920000 float:73.0 double:5.518043864E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r4 = r4 + r103;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x132d;
    L_0x2d25:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.namesOffset = r4;
        goto L_0x132d;
    L_0x2d36:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.fwd_from;
        if (r4 == 0) goto L_0x2eb5;
    L_0x2d3e:
        r0 = r195;
        r4 = r0.type;
        r6 = 13;
        if (r4 == r6) goto L_0x2eb5;
    L_0x2d46:
        r4 = 1;
    L_0x2d47:
        r0 = r194;
        r0.drawForwardedName = r4;
        r0 = r195;
        r4 = r0.type;
        r6 = 9;
        if (r4 == r6) goto L_0x2eb8;
    L_0x2d53:
        r4 = 1;
    L_0x2d54:
        r0 = r194;
        r0.mediaBackground = r4;
        r4 = 1;
        r0 = r194;
        r0.drawImageButton = r4;
        r4 = 1;
        r0 = r194;
        r0.drawPhotoImage = r4;
        r146 = 0;
        r145 = 0;
        r60 = 0;
        r0 = r195;
        r4 = r0.gifState;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x2d8b;
    L_0x2d72:
        r4 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r4 != 0) goto L_0x2d8b;
    L_0x2d76:
        r0 = r195;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x2d85;
    L_0x2d7e:
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x2d8b;
    L_0x2d85:
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = r195;
        r0.gifState = r4;
    L_0x2d8b:
        r4 = r195.isVideo();
        if (r4 == 0) goto L_0x2ebb;
    L_0x2d91:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setAllowDecodeSingleFrame(r6);
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setAllowStartAnimation(r6);
    L_0x2da1:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = r195.needDrawBluredPreview();
        r4.setForcePreview(r6);
        r0 = r195;
        r4 = r0.type;
        r6 = 9;
        if (r4 != r6) goto L_0x3173;
    L_0x2db4:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2efe;
    L_0x2dba:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2efa;
    L_0x2dc4:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x2efa;
    L_0x2dca:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x2efa;
    L_0x2dd0:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x2dd2:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x2de6:
        r4 = r194.checkNeedDrawShareButton(r195);
        if (r4 == 0) goto L_0x2dfb;
    L_0x2dec:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x2dfb:
        r123 = 0;
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x430a0000 float:138.0 double:5.55689877E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r0 = r194;
        r1 = r43;
        r2 = r195;
        r0.createDocumentLayout(r1, r2);
        r0 = r195;
        r4 = r0.caption;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x2e6b;
    L_0x2e1c:
        r0 = r195;
        r4 = r0.caption;	 Catch:{ Exception -> 0x2f4c }
        r0 = r194;
        r0.currentCaption = r4;	 Catch:{ Exception -> 0x2f4c }
        r0 = r194;
        r4 = r0.backgroundWidth;	 Catch:{ Exception -> 0x2f4c }
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x2f4c }
        r190 = r4 - r6;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x2f4c }
        r27 = r190 - r4;
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x2f4c }
        r6 = 24;
        if (r4 < r6) goto L_0x2f2f;
    L_0x2e3e:
        r0 = r195;
        r4 = r0.caption;	 Catch:{ Exception -> 0x2f4c }
        r6 = 0;
        r0 = r195;
        r8 = r0.caption;	 Catch:{ Exception -> 0x2f4c }
        r8 = r8.length();	 Catch:{ Exception -> 0x2f4c }
        r9 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x2f4c }
        r0 = r27;
        r4 = android.text.StaticLayout.Builder.obtain(r4, r6, r8, r9, r0);	 Catch:{ Exception -> 0x2f4c }
        r6 = 1;
        r4 = r4.setBreakStrategy(r6);	 Catch:{ Exception -> 0x2f4c }
        r6 = 0;
        r4 = r4.setHyphenationFrequency(r6);	 Catch:{ Exception -> 0x2f4c }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x2f4c }
        r4 = r4.setAlignment(r6);	 Catch:{ Exception -> 0x2f4c }
        r4 = r4.build();	 Catch:{ Exception -> 0x2f4c }
        r0 = r194;
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x2f4c }
    L_0x2e6b:
        r0 = r194;
        r4 = r0.docTitleLayout;
        if (r4 == 0) goto L_0x2var_;
    L_0x2e71:
        r58 = 0;
        r0 = r194;
        r4 = r0.docTitleLayout;
        r56 = r4.getLineCount();
    L_0x2e7b:
        r0 = r58;
        r1 = r56;
        if (r0 >= r1) goto L_0x2var_;
    L_0x2e81:
        r0 = r194;
        r4 = r0.docTitleLayout;
        r0 = r58;
        r4 = r4.getLineWidth(r0);
        r0 = r194;
        r6 = r0.docTitleLayout;
        r0 = r58;
        r6 = r6.getLineLeft(r0);
        r4 = r4 + r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r0 = r194;
        r4 = r0.drawPhotoImage;
        if (r4 == 0) goto L_0x2var_;
    L_0x2ea2:
        r4 = 52;
    L_0x2ea4:
        r4 = r4 + 86;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r6;
        r0 = r123;
        r123 = java.lang.Math.max(r0, r4);
        r58 = r58 + 1;
        goto L_0x2e7b;
    L_0x2eb5:
        r4 = 0;
        goto L_0x2d47;
    L_0x2eb8:
        r4 = 0;
        goto L_0x2d54;
    L_0x2ebb:
        r4 = r195.isRoundVideo();
        if (r4 == 0) goto L_0x2ee5;
    L_0x2ec1:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setAllowDecodeSingleFrame(r6);
        r4 = org.telegram.messenger.MediaController.getInstance();
        r147 = r4.getPlayingMessageObject();
        r0 = r194;
        r6 = r0.photoImage;
        if (r147 == 0) goto L_0x2edd;
    L_0x2ed7:
        r4 = r147.isRoundVideo();
        if (r4 != 0) goto L_0x2ee3;
    L_0x2edd:
        r4 = 1;
    L_0x2ede:
        r6.setAllowStartAnimation(r4);
        goto L_0x2da1;
    L_0x2ee3:
        r4 = 0;
        goto L_0x2ede;
    L_0x2ee5:
        r0 = r194;
        r6 = r0.photoImage;
        r0 = r195;
        r4 = r0.gifState;
        r8 = 0;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x2ef8;
    L_0x2ef2:
        r4 = 1;
    L_0x2ef3:
        r6.setAllowStartAnimation(r4);
        goto L_0x2da1;
    L_0x2ef8:
        r4 = 0;
        goto L_0x2ef3;
    L_0x2efa:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2dd2;
    L_0x2efe:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2f2c;
    L_0x2var_:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x2f2c;
    L_0x2f0e:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x2f2c;
    L_0x2var_:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x2var_:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x2de6;
    L_0x2f2c:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2var_;
    L_0x2f2f:
        r24 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x2f4c }
        r0 = r195;
        r0 = r0.caption;	 Catch:{ Exception -> 0x2f4c }
        r25 = r0;
        r26 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x2f4c }
        r28 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x2f4c }
        r29 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r30 = 0;
        r31 = 0;
        r24.<init>(r25, r26, r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x2f4c }
        r0 = r24;
        r1 = r194;
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x2f4c }
        goto L_0x2e6b;
    L_0x2f4c:
        r92 = move-exception;
        org.telegram.messenger.FileLog.e(r92);
        goto L_0x2e6b;
    L_0x2var_:
        r4 = 22;
        goto L_0x2ea4;
    L_0x2var_:
        r0 = r194;
        r4 = r0.infoLayout;
        if (r4 == 0) goto L_0x2var_;
    L_0x2f5c:
        r58 = 0;
        r0 = r194;
        r4 = r0.infoLayout;
        r56 = r4.getLineCount();
    L_0x2var_:
        r0 = r58;
        r1 = r56;
        if (r0 >= r1) goto L_0x2var_;
    L_0x2f6c:
        r0 = r194;
        r4 = r0.infoLayout;
        r0 = r58;
        r4 = r4.getLineWidth(r0);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r0 = r194;
        r4 = r0.drawPhotoImage;
        if (r4 == 0) goto L_0x2var_;
    L_0x2var_:
        r4 = 52;
    L_0x2var_:
        r4 = r4 + 86;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r6;
        r0 = r123;
        r123 = java.lang.Math.max(r0, r4);
        r58 = r58 + 1;
        goto L_0x2var_;
    L_0x2var_:
        r4 = 22;
        goto L_0x2var_;
    L_0x2var_:
        r0 = r194;
        r4 = r0.captionLayout;
        if (r4 == 0) goto L_0x2fdc;
    L_0x2f9e:
        r58 = 0;
        r0 = r194;
        r4 = r0.captionLayout;
        r56 = r4.getLineCount();
    L_0x2fa8:
        r0 = r58;
        r1 = r56;
        if (r0 >= r1) goto L_0x2fdc;
    L_0x2fae:
        r0 = r194;
        r4 = r0.captionLayout;
        r0 = r58;
        r4 = r4.getLineWidth(r0);
        r0 = r194;
        r6 = r0.captionLayout;
        r0 = r58;
        r6 = r6.getLineLeft(r0);
        r4 = r4 + r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r23 = r4 + r6;
        r0 = r23;
        r1 = r123;
        if (r0 <= r1) goto L_0x2fd9;
    L_0x2fd7:
        r123 = r23;
    L_0x2fd9:
        r58 = r58 + 1;
        goto L_0x2fa8;
    L_0x2fdc:
        if (r123 <= 0) goto L_0x2fec;
    L_0x2fde:
        r0 = r123;
        r1 = r194;
        r1.backgroundWidth = r0;
        r4 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r43 = r123 - r4;
    L_0x2fec:
        r0 = r194;
        r4 = r0.drawPhotoImage;
        if (r4 == 0) goto L_0x3141;
    L_0x2ff2:
        r4 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r146 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r145 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x2ffe:
        r0 = r43;
        r1 = r194;
        r1.availableTimeWidth = r0;
        r0 = r194;
        r4 = r0.drawPhotoImage;
        if (r4 != 0) goto L_0x304d;
    L_0x300a:
        r0 = r195;
        r4 = r0.caption;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x304d;
    L_0x3014:
        r0 = r194;
        r4 = r0.infoLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x304d;
    L_0x301e:
        r194.measureTime(r195);
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42var_ float:122.0 double:5.54977537E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r6 = r0.infoLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r8 = (double) r6;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r175 = r4 - r6;
        r0 = r194;
        r4 = r0.timeWidth;
        r0 = r175;
        if (r0 >= r4) goto L_0x304d;
    L_0x3045:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r145 = r145 + r4;
    L_0x304d:
        r194.setMessageObjectInternal(r195);
        r0 = r194;
        r4 = r0.drawForwardedName;
        if (r4 == 0) goto L_0x4891;
    L_0x3056:
        r4 = r195.needDrawForwarded();
        if (r4 == 0) goto L_0x4891;
    L_0x305c:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x306a;
    L_0x3062:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.minY;
        if (r4 != 0) goto L_0x4891;
    L_0x306a:
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 == r6) goto L_0x3080;
    L_0x3071:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.namesOffset = r4;
    L_0x3080:
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r145;
        r0 = r194;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r4 = r4 + r60;
        r0 = r194;
        r0.totalHeight = r4;
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x30b2;
    L_0x3099:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 != 0) goto L_0x30b2;
    L_0x30a3:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.totalHeight = r4;
    L_0x30b2:
        r62 = 0;
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x30f2;
    L_0x30ba:
        r0 = r194;
        r4 = r0.currentPosition;
        r0 = r194;
        r4 = r0.getAdditionalWidthForPosition(r4);
        r146 = r146 + r4;
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 != 0) goto L_0x30e0;
    L_0x30d0:
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r145 = r145 + r4;
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r62 = r62 - r4;
    L_0x30e0:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 != 0) goto L_0x30f2;
    L_0x30ea:
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r145 = r145 + r4;
    L_0x30f2:
        r0 = r194;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x3107;
    L_0x30f8:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.namesOffset = r4;
    L_0x3107:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x48c7;
    L_0x310d:
        r0 = r194;
        r4 = r0.namesOffset;
        if (r4 <= 0) goto L_0x48b0;
    L_0x3113:
        r4 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r192 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.totalHeight = r4;
    L_0x3128:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 0;
        r0 = r194;
        r8 = r0.namesOffset;
        r8 = r8 + r192;
        r8 = r8 + r62;
        r0 = r146;
        r1 = r145;
        r4.setImageCoords(r6, r8, r0, r1);
        r194.invalidate();
        goto L_0x132d;
    L_0x3141:
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r146 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r145 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r4 = r0.docTitleLayout;
        if (r4 == 0) goto L_0x2ffe;
    L_0x3153:
        r0 = r194;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        r6 = 1;
        if (r4 <= r6) goto L_0x2ffe;
    L_0x315e:
        r0 = r194;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        r4 = r4 + -1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 * r6;
        r145 = r145 + r4;
        goto L_0x2ffe;
    L_0x3173:
        r0 = r195;
        r4 = r0.type;
        r6 = 4;
        if (r4 != r6) goto L_0x37d4;
    L_0x317a:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.geo;
        r148 = r0;
        r0 = r148;
        r0 = r0.lat;
        r30 = r0;
        r0 = r148;
        r0 = r0._long;
        r32 = r0;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r4 == 0) goto L_0x3471;
    L_0x319a:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x33fa;
    L_0x31a0:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x33f6;
    L_0x31aa:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x33f6;
    L_0x31b0:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x33f6;
    L_0x31b6:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x31b8:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x31cd:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.backgroundWidth = r4;
        r4 = r194.checkNeedDrawShareButton(r195);
        if (r4 == 0) goto L_0x31f1;
    L_0x31e2:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x31f1:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42140000 float:37.0 double:5.477246216E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r0 = r43;
        r1 = r194;
        r1.availableTimeWidth = r0;
        r4 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r43 = r43 - r4;
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r146 = r4 - r6;
        r4 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
        r145 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r138 = NUM; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r0 = r138;
        r8 = (double) r0;
        r20 = NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.NUM;
        r156 = r8 / r20;
        r0 = r138;
        r8 = (double) r0;
        r20 = NUM; // 0x3ffNUM float:0.0 double:1.0;
        r24 = NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.NUM;
        r24 = r24 * r30;
        r28 = NUM; // 0xNUM float:0.0 double:180.0;
        r24 = r24 / r28;
        r24 = java.lang.Math.sin(r24);
        r20 = r20 + r24;
        r24 = NUM; // 0x3ffNUM float:0.0 double:1.0;
        r28 = NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.NUM;
        r28 = r28 * r30;
        r34 = NUM; // 0xNUM float:0.0 double:180.0;
        r28 = r28 / r34;
        r28 = java.lang.Math.sin(r28);
        r24 = r24 - r28;
        r20 = r20 / r24;
        r20 = java.lang.Math.log(r20);
        r20 = r20 * r156;
        r24 = NUM; // 0xNUM float:0.0 double:2.0;
        r20 = r20 / r24;
        r8 = r8 - r20;
        r8 = java.lang.Math.round(r8);
        r4 = NUM; // 0x4124cccd float:10.3 double:5.399795443E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 << 6;
        r0 = (long) r4;
        r20 = r0;
        r8 = r8 - r20;
        r0 = (double) r8;
        r192 = r0;
        r8 = NUM; // 0x3fvar_fb54442d18 float:3.37028055E12 double:1.NUM;
        r20 = NUM; // 0xNUM float:0.0 double:2.0;
        r0 = r138;
        r0 = (double) r0;
        r24 = r0;
        r24 = r192 - r24;
        r24 = r24 / r156;
        r24 = java.lang.Math.exp(r24);
        r24 = java.lang.Math.atan(r24);
        r20 = r20 * r24;
        r8 = r8 - r20;
        r20 = NUM; // 0xNUM float:0.0 double:180.0;
        r8 = r8 * r20;
        r20 = NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.NUM;
        r30 = r8 / r20;
        r0 = r194;
        r0 = r0.currentAccount;
        r29 = r0;
        r0 = r146;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r34 = r0;
        r0 = r145;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r35 = r0;
        r36 = 0;
        r37 = 15;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r29, r30, r32, r34, r35, r36, r37);
        r0 = r194;
        r0.currentUrl = r4;
        r0 = r148;
        r0 = r0.access_hash;
        r34 = r0;
        r0 = r146;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r36 = r0;
        r0 = r145;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r37 = r0;
        r38 = 15;
        r4 = 2;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r8 = (double) r6;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r39 = java.lang.Math.min(r4, r6);
        r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r30, r32, r34, r36, r37, r38, r39);
        r0 = r194;
        r0.currentWebFile = r4;
        r4 = r194.isCurrentLocationTimeExpired(r195);
        r0 = r194;
        r0.locationExpired = r4;
        if (r4 != 0) goto L_0x342c;
    L_0x3302:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setCrossfadeWithOldImage(r6);
        r4 = 0;
        r0 = r194;
        r0.mediaBackground = r4;
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r60 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r4 = r0.invalidateRunnable;
        r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4, r8);
        r4 = 1;
        r0 = r194;
        r0.scheduledInvalidate = r4;
    L_0x3323:
        r34 = new android.text.StaticLayout;
        r4 = "AttachLiveLocation";
        r6 = NUM; // 0x7f0CLASSNAMEd3 float:1.860962E38 double:1.0530975027E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r6 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
        r0 = r43;
        r8 = (float) r0;
        r9 = android.text.TextUtils.TruncateAt.END;
        r35 = android.text.TextUtils.ellipsize(r4, r6, r8, r9);
        r36 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
        r38 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r39 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r40 = 0;
        r41 = 0;
        r37 = r43;
        r34.<init>(r35, r36, r37, r38, r39, r40, r41);
        r0 = r34;
        r1 = r194;
        r1.docTitleLayout = r0;
        r19 = 0;
        r194.updateCurrentUserAndChat();
        r39 = 0;
        r0 = r194;
        r4 = r0.currentUser;
        if (r4 == 0) goto L_0x343d;
    L_0x335c:
        r0 = r194;
        r4 = r0.currentUser;
        r4 = r4.photo;
        if (r4 == 0) goto L_0x336e;
    L_0x3364:
        r0 = r194;
        r4 = r0.currentUser;
        r4 = r4.photo;
        r0 = r4.photo_small;
        r19 = r0;
    L_0x336e:
        r0 = r194;
        r4 = r0.contactAvatarDrawable;
        r0 = r194;
        r6 = r0.currentUser;
        r4.setInfo(r6);
        r0 = r194;
        r0 = r0.currentUser;
        r39 = r0;
    L_0x337f:
        r0 = r194;
        r0 = r0.locationImageReceiver;
        r34 = r0;
        r36 = "50_50";
        r0 = r194;
        r0 = r0.contactAvatarDrawable;
        r37 = r0;
        r38 = 0;
        r40 = 0;
        r35 = r19;
        r34.setImage(r35, r36, r37, r38, r39, r40);
        r40 = new android.text.StaticLayout;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.edit_date;
        if (r4 == 0) goto L_0x3468;
    L_0x33a1:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.edit_date;
        r8 = (long) r4;
    L_0x33a8:
        r41 = org.telegram.messenger.LocaleController.formatLocationUpdateDate(r8);
        r42 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
        r44 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r45 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r46 = 0;
        r47 = 0;
        r40.<init>(r41, r42, r43, r44, r45, r46, r47);
        r0 = r40;
        r1 = r194;
        r1.infoLayout = r0;
    L_0x33bf:
        r8 = r195.getDialogId();
        r4 = (int) r8;
        if (r4 != 0) goto L_0x3742;
    L_0x33c6:
        r4 = org.telegram.messenger.SharedConfig.mapPreviewType;
        if (r4 != 0) goto L_0x372f;
    L_0x33ca:
        r4 = 2;
        r0 = r194;
        r0.currentMapProvider = r4;
    L_0x33cf:
        r0 = r194;
        r4 = r0.currentMapProvider;
        r6 = -1;
        if (r4 != r6) goto L_0x3755;
    L_0x33d6:
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r45 = 0;
        r46 = 0;
        r6 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x3752;
    L_0x33e8:
        r4 = 1;
    L_0x33e9:
        r47 = r6[r4];
        r48 = 0;
        r50 = 0;
        r49 = r195;
        r44.setImage(r45, r46, r47, r48, r49, r50);
        goto L_0x304d;
    L_0x33f6:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x31b8;
    L_0x33fa:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x3429;
    L_0x3404:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x3429;
    L_0x340a:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x3429;
    L_0x3410:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x3412:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x31cd;
    L_0x3429:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x3412;
    L_0x342c:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x3323;
    L_0x343d:
        r0 = r194;
        r4 = r0.currentChat;
        if (r4 == 0) goto L_0x337f;
    L_0x3443:
        r0 = r194;
        r4 = r0.currentChat;
        r4 = r4.photo;
        if (r4 == 0) goto L_0x3455;
    L_0x344b:
        r0 = r194;
        r4 = r0.currentChat;
        r4 = r4.photo;
        r0 = r4.photo_small;
        r19 = r0;
    L_0x3455:
        r0 = r194;
        r4 = r0.contactAvatarDrawable;
        r0 = r194;
        r6 = r0.currentChat;
        r4.setInfo(r6);
        r0 = r194;
        r0 = r0.currentChat;
        r39 = r0;
        goto L_0x337f;
    L_0x3468:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.date;
        r8 = (long) r4;
        goto L_0x33a8;
    L_0x3471:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.title;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x3630;
    L_0x347f:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x35f5;
    L_0x3485:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x35f1;
    L_0x348f:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x35f1;
    L_0x3495:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x35f1;
    L_0x349b:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x349d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x34b2:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.backgroundWidth = r4;
        r4 = r194.checkNeedDrawShareButton(r195);
        if (r4 == 0) goto L_0x34d6;
    L_0x34c7:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x34d6:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r0 = r43;
        r1 = r194;
        r1.availableTimeWidth = r0;
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r146 = r4 - r6;
        r4 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
        r145 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = 0;
        r0 = r194;
        r0.mediaBackground = r4;
        r0 = r194;
        r0 = r0.currentAccount;
        r29 = r0;
        r0 = r146;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r34 = r0;
        r0 = r145;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r35 = r0;
        r36 = 1;
        r37 = 15;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r29, r30, r32, r34, r35, r36, r37);
        r0 = r194;
        r0.currentUrl = r4;
        r0 = r146;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r4 = (int) r4;
        r0 = r145;
        r6 = (float) r0;
        r8 = org.telegram.messenger.AndroidUtilities.density;
        r6 = r6 / r8;
        r6 = (int) r6;
        r8 = 15;
        r9 = 2;
        r10 = org.telegram.messenger.AndroidUtilities.density;
        r0 = (double) r10;
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);
        r0 = r20;
        r10 = (int) r0;
        r9 = java.lang.Math.min(r9, r10);
        r0 = r148;
        r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r4, r6, r8, r9);
        r0 = r194;
        r0.currentWebFile = r4;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.title;
        r41 = r0;
        r42 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
        r44 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r45 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r46 = 0;
        r47 = 0;
        r48 = android.text.TextUtils.TruncateAt.END;
        r50 = 1;
        r49 = r43;
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r41, r42, r43, r44, r45, r46, r47, r48, r49, r50);
        r0 = r194;
        r0.docTitleLayout = r4;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r60 = r60 + r4;
        r0 = r194;
        r4 = r0.docTitleLayout;
        r113 = r4.getLineCount();
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.address;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x3629;
    L_0x358e:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.address;
        r41 = r0;
        r42 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
        r44 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r45 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r46 = 0;
        r47 = 0;
        r48 = android.text.TextUtils.TruncateAt.END;
        r50 = 1;
        r49 = r43;
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r41, r42, r43, r44, r45, r46, r47, r48, r49, r50);
        r0 = r194;
        r0.infoLayout = r4;
        r194.measureTime(r195);
        r0 = r194;
        r4 = r0.backgroundWidth;
        r0 = r194;
        r6 = r0.infoLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r8 = (double) r6;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r4 = r4 - r6;
        r6 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r175 = r4 - r6;
        r0 = r194;
        r6 = r0.timeWidth;
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x3627;
    L_0x35d9:
        r4 = 20;
    L_0x35db:
        r4 = r4 + 20;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r6;
        r0 = r175;
        if (r0 >= r4) goto L_0x33bf;
    L_0x35e7:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r60 = r60 + r4;
        goto L_0x33bf;
    L_0x35f1:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x349d;
    L_0x35f5:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x3624;
    L_0x35ff:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x3624;
    L_0x3605:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x3624;
    L_0x360b:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x360d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x34b2;
    L_0x3624:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x360d;
    L_0x3627:
        r4 = 0;
        goto L_0x35db;
    L_0x3629:
        r4 = 0;
        r0 = r194;
        r0.infoLayout = r4;
        goto L_0x33bf;
    L_0x3630:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x36fd;
    L_0x3636:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x36f9;
    L_0x3640:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x36f9;
    L_0x3646:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x36f9;
    L_0x364c:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x364e:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x3663:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.backgroundWidth = r4;
        r4 = r194.checkNeedDrawShareButton(r195);
        if (r4 == 0) goto L_0x3687;
    L_0x3678:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x3687:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.availableTimeWidth = r4;
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r146 = r4 - r6;
        r4 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
        r145 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r0 = r0.currentAccount;
        r29 = r0;
        r0 = r146;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r34 = r0;
        r0 = r145;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r35 = r0;
        r36 = 1;
        r37 = 15;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r29, r30, r32, r34, r35, r36, r37);
        r0 = r194;
        r0.currentUrl = r4;
        r0 = r146;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r4 = (int) r4;
        r0 = r145;
        r6 = (float) r0;
        r8 = org.telegram.messenger.AndroidUtilities.density;
        r6 = r6 / r8;
        r6 = (int) r6;
        r8 = 15;
        r9 = 2;
        r10 = org.telegram.messenger.AndroidUtilities.density;
        r0 = (double) r10;
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);
        r0 = r20;
        r10 = (int) r0;
        r9 = java.lang.Math.min(r9, r10);
        r0 = r148;
        r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r4, r6, r8, r9);
        r0 = r194;
        r0.currentWebFile = r4;
        goto L_0x33bf;
    L_0x36f9:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x364e;
    L_0x36fd:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x372c;
    L_0x3707:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x372c;
    L_0x370d:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x372c;
    L_0x3713:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x3715:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x3663;
    L_0x372c:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x3715;
    L_0x372f:
        r4 = org.telegram.messenger.SharedConfig.mapPreviewType;
        r6 = 1;
        if (r4 != r6) goto L_0x373b;
    L_0x3734:
        r4 = 1;
        r0 = r194;
        r0.currentMapProvider = r4;
        goto L_0x33cf;
    L_0x373b:
        r4 = -1;
        r0 = r194;
        r0.currentMapProvider = r4;
        goto L_0x33cf;
    L_0x3742:
        r0 = r195;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = r4.mapProvider;
        r0 = r194;
        r0.currentMapProvider = r4;
        goto L_0x33cf;
    L_0x3752:
        r4 = 0;
        goto L_0x33e9;
    L_0x3755:
        r0 = r194;
        r4 = r0.currentMapProvider;
        r6 = 2;
        if (r4 != r6) goto L_0x3788;
    L_0x375c:
        r0 = r194;
        r4 = r0.currentWebFile;
        if (r4 == 0) goto L_0x304d;
    L_0x3762:
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r194;
        r0 = r0.currentWebFile;
        r45 = r0;
        r46 = 0;
        r6 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x3786;
    L_0x3778:
        r4 = 1;
    L_0x3779:
        r47 = r6[r4];
        r48 = 0;
        r50 = 0;
        r49 = r195;
        r44.setImage(r45, r46, r47, r48, r49, r50);
        goto L_0x304d;
    L_0x3786:
        r4 = 0;
        goto L_0x3779;
    L_0x3788:
        r0 = r194;
        r4 = r0.currentMapProvider;
        r6 = 3;
        if (r4 == r6) goto L_0x3796;
    L_0x378f:
        r0 = r194;
        r4 = r0.currentMapProvider;
        r6 = 4;
        if (r4 != r6) goto L_0x37aa;
    L_0x3796:
        r4 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r194;
        r6 = r0.currentUrl;
        r0 = r194;
        r8 = r0.currentWebFile;
        r4.addTestWebFile(r6, r8);
        r4 = 1;
        r0 = r194;
        r0.addedForTest = r4;
    L_0x37aa:
        r0 = r194;
        r4 = r0.currentUrl;
        if (r4 == 0) goto L_0x304d;
    L_0x37b0:
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r194;
        r0 = r0.currentUrl;
        r45 = r0;
        r46 = 0;
        r6 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x37d2;
    L_0x37c6:
        r4 = 1;
    L_0x37c7:
        r47 = r6[r4];
        r48 = 0;
        r49 = 0;
        r44.setImage(r45, r46, r47, r48, r49);
        goto L_0x304d;
    L_0x37d2:
        r4 = 0;
        goto L_0x37c7;
    L_0x37d4:
        r0 = r195;
        r4 = r0.type;
        r6 = 13;
        if (r4 != r6) goto L_0x3955;
    L_0x37dc:
        r4 = 0;
        r0 = r194;
        r0.drawBackground = r4;
        r58 = 0;
    L_0x37e3:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r4 = r4.size();
        r0 = r58;
        if (r0 >= r4) goto L_0x3819;
    L_0x37f5:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r0 = r58;
        r66 = r4.get(r0);
        r66 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r66;
        r0 = r66;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 == 0) goto L_0x38df;
    L_0x380d:
        r0 = r66;
        r0 = r0.w;
        r146 = r0;
        r0 = r66;
        r0 = r0.h;
        r145 = r0;
    L_0x3819:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x38e3;
    L_0x381f:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
        r43 = r4 * r6;
        r121 = r43;
    L_0x382b:
        if (r146 != 0) goto L_0x383a;
    L_0x382d:
        r0 = r121;
        r0 = (int) r0;
        r145 = r0;
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r146 = r145 + r4;
    L_0x383a:
        r0 = r145;
        r4 = (float) r0;
        r0 = r146;
        r6 = (float) r0;
        r6 = r43 / r6;
        r4 = r4 * r6;
        r0 = (int) r4;
        r145 = r0;
        r0 = r43;
        r0 = (int) r0;
        r146 = r0;
        r0 = r145;
        r4 = (float) r0;
        r4 = (r4 > r121 ? 1 : (r4 == r121 ? 0 : -1));
        if (r4 <= 0) goto L_0x3863;
    L_0x3852:
        r0 = r146;
        r4 = (float) r0;
        r0 = r145;
        r6 = (float) r0;
        r6 = r121 / r6;
        r4 = r4 * r6;
        r0 = (int) r4;
        r146 = r0;
        r0 = r121;
        r0 = (int) r0;
        r145 = r0;
    L_0x3863:
        r4 = 6;
        r0 = r194;
        r0.documentAttachType = r4;
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r146 - r4;
        r0 = r194;
        r0.availableTimeWidth = r4;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r146;
        r0 = r194;
        r0.backgroundWidth = r4;
        r0 = r195;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
        r0 = r195;
        r4 = r0.attachPathExists;
        if (r4 == 0) goto L_0x38f8;
    L_0x3894:
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r195;
        r4 = r0.messageOwner;
        r0 = r4.attachPath;
        r45 = r0;
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r146);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r145);
        r8[r9] = r10;
        r46 = java.lang.String.format(r4, r6, r8);
        r47 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r48 = r0;
        r49 = "b1";
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r0 = r4.size;
        r50 = r0;
        r51 = "webp";
        r53 = 1;
        r52 = r195;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52, r53);
        goto L_0x304d;
    L_0x38df:
        r58 = r58 + 1;
        goto L_0x37e3;
    L_0x38e3:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r43 = r4 * r6;
        r121 = r43;
        goto L_0x382b;
    L_0x38f8:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r8 = r4.id;
        r20 = 0;
        r4 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1));
        if (r4 == 0) goto L_0x304d;
    L_0x3908:
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.document;
        r45 = r0;
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r146);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r145);
        r8[r9] = r10;
        r46 = java.lang.String.format(r4, r6, r8);
        r47 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r48 = r0;
        r49 = "b1";
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r0 = r4.size;
        r50 = r0;
        r51 = "webp";
        r53 = 1;
        r52 = r195;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52, r53);
        goto L_0x304d;
    L_0x3955:
        r0 = r195;
        r4 = r0.photoThumbs;
        r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObject = r4;
        r184 = 0;
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x3b51;
    L_0x396e:
        r146 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r122 = r146;
    L_0x3972:
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r145 = r146 + r4;
        if (r184 != 0) goto L_0x3bc3;
    L_0x397c:
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 == r6) goto L_0x3999;
    L_0x3983:
        r4 = r194.checkNeedDrawShareButton(r195);
        if (r4 == 0) goto L_0x3999;
    L_0x3989:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r122 = r122 - r4;
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r146 = r146 - r4;
    L_0x3999:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r0 = r146;
        if (r0 <= r4) goto L_0x39a5;
    L_0x39a1:
        r146 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x39a5:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r0 = r145;
        if (r0 <= r4) goto L_0x39b1;
    L_0x39ad:
        r145 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x39b1:
        r132 = 0;
        r0 = r195;
        r4 = r0.type;
        r6 = 1;
        if (r4 != r6) goto L_0x3bdf;
    L_0x39ba:
        r194.updateSecretTimeText(r195);
        r0 = r195;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
    L_0x39cb:
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x3CLASSNAME;
    L_0x39d2:
        r99 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r23 = r99;
    L_0x39d6:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x39f0;
    L_0x39dc:
        r4 = "s";
        r0 = r194;
        r6 = r0.currentPhotoObject;
        r6 = r6.type;
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x39f0;
    L_0x39eb:
        r4 = 0;
        r0 = r194;
        r0.currentPhotoObject = r4;
    L_0x39f0:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x3a0c;
    L_0x39f6:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r194;
        r6 = r0.currentPhotoObjectThumb;
        if (r4 != r6) goto L_0x3a0c;
    L_0x3a00:
        r0 = r195;
        r4 = r0.type;
        r6 = 1;
        if (r4 != r6) goto L_0x3d44;
    L_0x3a07:
        r4 = 0;
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
    L_0x3a0c:
        if (r132 == 0) goto L_0x3a49;
    L_0x3a0e:
        r4 = r195.needDrawBluredPreview();
        if (r4 != 0) goto L_0x3a49;
    L_0x3a14:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x3a24;
    L_0x3a1a:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r194;
        r6 = r0.currentPhotoObjectThumb;
        if (r4 != r6) goto L_0x3a49;
    L_0x3a24:
        r0 = r194;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x3a39;
    L_0x3a2a:
        r4 = "m";
        r0 = r194;
        r6 = r0.currentPhotoObjectThumb;
        r6 = r6.type;
        r4 = r4.equals(r6);
        if (r4 != 0) goto L_0x3a49;
    L_0x3a39:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setNeedsQualityThumb(r6);
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setShouldGenerateQualityThumb(r6);
    L_0x3a49:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        if (r4 != 0) goto L_0x3a5a;
    L_0x3a4f:
        r0 = r195;
        r4 = r0.caption;
        if (r4 == 0) goto L_0x3a5a;
    L_0x3a55:
        r4 = 0;
        r0 = r194;
        r0.mediaBackground = r4;
    L_0x3a5a:
        if (r23 == 0) goto L_0x3a5e;
    L_0x3a5c:
        if (r99 != 0) goto L_0x3ad0;
    L_0x3a5e:
        r0 = r195;
        r4 = r0.type;
        r6 = 8;
        if (r4 != r6) goto L_0x3ad0;
    L_0x3a66:
        r58 = 0;
    L_0x3a68:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r4 = r4.size();
        r0 = r58;
        if (r0 >= r4) goto L_0x3ad0;
    L_0x3a7a:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r0 = r58;
        r66 = r4.get(r0);
        r66 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r66;
        r0 = r66;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 != 0) goto L_0x3a98;
    L_0x3a92:
        r0 = r66;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x3d7f;
    L_0x3a98:
        r0 = r66;
        r4 = r0.w;
        r4 = (float) r4;
        r0 = r146;
        r6 = (float) r0;
        r163 = r4 / r6;
        r0 = r66;
        r4 = r0.w;
        r4 = (float) r4;
        r4 = r4 / r163;
        r0 = (int) r4;
        r23 = r0;
        r0 = r66;
        r4 = r0.h;
        r4 = (float) r4;
        r4 = r4 / r163;
        r0 = (int) r4;
        r99 = r0;
        r0 = r99;
        r1 = r145;
        if (r0 <= r1) goto L_0x3d4b;
    L_0x3abc:
        r0 = r99;
        r0 = (float) r0;
        r164 = r0;
        r99 = r145;
        r0 = r99;
        r4 = (float) r0;
        r164 = r164 / r4;
        r0 = r23;
        r4 = (float) r0;
        r4 = r4 / r164;
        r0 = (int) r4;
        r23 = r0;
    L_0x3ad0:
        if (r23 == 0) goto L_0x3ad4;
    L_0x3ad2:
        if (r99 != 0) goto L_0x3adc;
    L_0x3ad4:
        r4 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r99 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r99;
    L_0x3adc:
        r0 = r195;
        r4 = r0.type;
        r6 = 3;
        if (r4 != r6) goto L_0x3afe;
    L_0x3ae3:
        r0 = r194;
        r4 = r0.infoWidth;
        r6 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r23;
        if (r0 >= r4) goto L_0x3afe;
    L_0x3af2:
        r0 = r194;
        r4 = r0.infoWidth;
        r6 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r23 = r4 + r6;
    L_0x3afe:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        if (r4 == 0) goto L_0x3eb7;
    L_0x3b04:
        r94 = 0;
        r85 = r194.getGroupPhotosWidth();
        r58 = 0;
    L_0x3b0c:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r0 = r58;
        if (r0 >= r4) goto L_0x3d83;
    L_0x3b1a:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r58;
        r150 = r4.get(r0);
        r150 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r150;
        r0 = r150;
        r4 = r0.minY;
        if (r4 != 0) goto L_0x3d83;
    L_0x3b2e:
        r0 = r94;
        r8 = (double) r0;
        r0 = r150;
        r4 = r0.pw;
        r0 = r150;
        r6 = r0.leftSpanOffset;
        r4 = r4 + r6;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r0 = (double) r4;
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);
        r8 = r8 + r20;
        r0 = (int) r8;
        r94 = r0;
        r58 = r58 + 1;
        goto L_0x3b0c;
    L_0x3b51:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x3b67;
    L_0x3b57:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.7 double:5.23867711E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r146 = r0;
        r122 = r146;
        goto L_0x3972;
    L_0x3b67:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x3bab;
    L_0x3b6d:
        r0 = r195;
        r4 = r0.type;
        r6 = 1;
        if (r4 == r6) goto L_0x3b83;
    L_0x3b74:
        r0 = r195;
        r4 = r0.type;
        r6 = 3;
        if (r4 == r6) goto L_0x3b83;
    L_0x3b7b:
        r0 = r195;
        r4 = r0.type;
        r6 = 8;
        if (r4 != r6) goto L_0x3bab;
    L_0x3b83:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        r0 = r194;
        r6 = r0.currentPhotoObject;
        r6 = r6.h;
        if (r4 < r6) goto L_0x3bab;
    L_0x3b91:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r146 = r4 - r6;
        r122 = r146;
        r184 = 1;
        goto L_0x3972;
    L_0x3bab:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.7 double:5.23867711E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r146 = r0;
        r122 = r146;
        goto L_0x3972;
    L_0x3bc3:
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x39b1;
    L_0x3bc9:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x39b1;
    L_0x3bcf:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x39b1;
    L_0x3bd5:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r146 = r146 - r4;
        goto L_0x39b1;
    L_0x3bdf:
        r0 = r195;
        r4 = r0.type;
        r6 = 3;
        if (r4 != r6) goto L_0x3CLASSNAME;
    L_0x3be6:
        r4 = 0;
        r0 = r194;
        r1 = r195;
        r0.createDocumentLayout(r4, r1);
        r0 = r195;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
        r194.updateSecretTimeText(r195);
        r132 = 1;
        goto L_0x39cb;
    L_0x3CLASSNAME:
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x3c1c;
    L_0x3c0a:
        r0 = r195;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
        r132 = 1;
        goto L_0x39cb;
    L_0x3c1c:
        r0 = r195;
        r4 = r0.type;
        r6 = 8;
        if (r4 != r6) goto L_0x39cb;
    L_0x3CLASSNAME:
        r0 = r195;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r0.currentPhotoObjectThumb = r4;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.size;
        r8 = (long) r4;
        r5 = org.telegram.messenger.AndroidUtilities.formatFileSize(r8);
        r4 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r194;
        r0.infoWidth = r4;
        r44 = new android.text.StaticLayout;
        r46 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
        r0 = r194;
        r0 = r0.infoWidth;
        r47 = r0;
        r48 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r50 = 0;
        r51 = 0;
        r45 = r5;
        r44.<init>(r45, r46, r47, r48, r49, r50, r51);
        r0 = r44;
        r1 = r194;
        r1.infoLayout = r0;
        r132 = 1;
        goto L_0x39cb;
    L_0x3CLASSNAME:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x3cd4;
    L_0x3CLASSNAME:
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r167 = r0;
    L_0x3c7e:
        r106 = 0;
        r105 = 0;
        if (r167 == 0) goto L_0x3cdb;
    L_0x3CLASSNAME:
        r0 = r167;
        r0 = r0.w;
        r106 = r0;
        r0 = r167;
        r0 = r0.h;
        r105 = r0;
    L_0x3CLASSNAME:
        r0 = r106;
        r4 = (float) r0;
        r0 = r146;
        r6 = (float) r0;
        r163 = r4 / r6;
        r0 = r106;
        r4 = (float) r0;
        r4 = r4 / r163;
        r0 = (int) r4;
        r23 = r0;
        r0 = r105;
        r4 = (float) r0;
        r4 = r4 / r163;
        r0 = (int) r4;
        r99 = r0;
        if (r23 != 0) goto L_0x3cb0;
    L_0x3caa:
        r4 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r23 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x3cb0:
        if (r99 != 0) goto L_0x3cb8;
    L_0x3cb2:
        r4 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r99 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x3cb8:
        r0 = r99;
        r1 = r145;
        if (r0 <= r1) goto L_0x3d16;
    L_0x3cbe:
        r0 = r99;
        r0 = (float) r0;
        r164 = r0;
        r99 = r145;
        r0 = r99;
        r4 = (float) r0;
        r164 = r164 / r4;
        r0 = r23;
        r4 = (float) r0;
        r4 = r4 / r164;
        r0 = (int) r4;
        r23 = r0;
        goto L_0x39d6;
    L_0x3cd4:
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r167 = r0;
        goto L_0x3c7e;
    L_0x3cdb:
        r0 = r194;
        r4 = r0.documentAttach;
        if (r4 == 0) goto L_0x3CLASSNAME;
    L_0x3ce1:
        r58 = 0;
        r0 = r194;
        r4 = r0.documentAttach;
        r4 = r4.attributes;
        r56 = r4.size();
    L_0x3ced:
        r0 = r58;
        r1 = r56;
        if (r0 >= r1) goto L_0x3CLASSNAME;
    L_0x3cf3:
        r0 = r194;
        r4 = r0.documentAttach;
        r4 = r4.attributes;
        r0 = r58;
        r66 = r4.get(r0);
        r66 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r66;
        r0 = r66;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x3d13;
    L_0x3d07:
        r0 = r66;
        r0 = r0.w;
        r106 = r0;
        r0 = r66;
        r0 = r0.h;
        r105 = r0;
    L_0x3d13:
        r58 = r58 + 1;
        goto L_0x3ced;
    L_0x3d16:
        r4 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r99;
        if (r0 >= r4) goto L_0x39d6;
    L_0x3d20:
        r4 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r99 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r105;
        r4 = (float) r0;
        r0 = r99;
        r6 = (float) r0;
        r100 = r4 / r6;
        r0 = r106;
        r4 = (float) r0;
        r4 = r4 / r100;
        r0 = r146;
        r6 = (float) r0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x39d6;
    L_0x3d3a:
        r0 = r106;
        r4 = (float) r0;
        r4 = r4 / r100;
        r0 = (int) r4;
        r23 = r0;
        goto L_0x39d6;
    L_0x3d44:
        r4 = 0;
        r0 = r194;
        r0.currentPhotoObject = r4;
        goto L_0x3a0c;
    L_0x3d4b:
        r4 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r99;
        if (r0 >= r4) goto L_0x3ad0;
    L_0x3d55:
        r4 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r99 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r66;
        r4 = r0.h;
        r4 = (float) r4;
        r0 = r99;
        r6 = (float) r0;
        r100 = r4 / r6;
        r0 = r66;
        r4 = r0.w;
        r4 = (float) r4;
        r4 = r4 / r100;
        r0 = r146;
        r6 = (float) r0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x3ad0;
    L_0x3d73:
        r0 = r66;
        r4 = r0.w;
        r4 = (float) r4;
        r4 = r4 / r100;
        r0 = (int) r4;
        r23 = r0;
        goto L_0x3ad0;
    L_0x3d7f:
        r58 = r58 + 1;
        goto L_0x3a68;
    L_0x3d83:
        r4 = NUM; // 0x420CLASSNAME float:35.0 double:5.47465589E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r94 - r4;
        r0 = r194;
        r0.availableTimeWidth = r4;
    L_0x3d8f:
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x3dbd;
    L_0x3d96:
        r0 = r194;
        r4 = r0.availableTimeWidth;
        r8 = (double) r4;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r6 = "00:00";
        r4 = r4.measureText(r6);
        r0 = (double) r4;
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);
        r4 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = (double) r4;
        r24 = r0;
        r20 = r20 + r24;
        r8 = r8 - r20;
        r4 = (int) r8;
        r0 = r194;
        r0.availableTimeWidth = r4;
    L_0x3dbd:
        r194.measureTime(r195);
        r0 = r194;
        r6 = r0.timeWidth;
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x3ec5;
    L_0x3dca:
        r4 = 20;
    L_0x3dcc:
        r4 = r4 + 14;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r177 = r6 + r4;
        r0 = r23;
        r1 = r177;
        if (r0 >= r1) goto L_0x3ddd;
    L_0x3ddb:
        r23 = r177;
    L_0x3ddd:
        r4 = r195.isRoundVideo();
        if (r4 == 0) goto L_0x3ec8;
    L_0x3de3:
        r0 = r23;
        r1 = r99;
        r99 = java.lang.Math.min(r0, r1);
        r23 = r99;
        r4 = 0;
        r0 = r194;
        r0.drawBackground = r4;
        r0 = r194;
        r4 = r0.photoImage;
        r6 = r23 / 2;
        r4.setRoundRadius(r6);
    L_0x3dfb:
        r27 = 0;
        r95 = 0;
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        if (r4 == 0) goto L_0x454a;
    L_0x3e05:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.max(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r121 = r4 * r6;
        r85 = r194.getGroupPhotosWidth();
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r23 = r0;
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.minY;
        if (r4 == 0) goto L_0x3var_;
    L_0x3e38:
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x3e48;
    L_0x3e3e:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 1;
        if (r4 != 0) goto L_0x3e58;
    L_0x3e48:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x3var_;
    L_0x3e4e:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x3var_;
    L_0x3e58:
        r94 = 0;
        r83 = 0;
        r58 = 0;
    L_0x3e5e:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r0 = r58;
        if (r0 >= r4) goto L_0x3f4e;
    L_0x3e6c:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r58;
        r150 = r4.get(r0);
        r150 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r150;
        r0 = r150;
        r4 = r0.minY;
        if (r4 != 0) goto L_0x3efd;
    L_0x3e80:
        r0 = r94;
        r0 = (double) r0;
        r20 = r0;
        r0 = r150;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r150;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x3efa;
    L_0x3e9c:
        r0 = r150;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x3ead:
        r8 = r8 + r24;
        r8 = r8 + r20;
        r0 = (int) r8;
        r94 = r0;
    L_0x3eb4:
        r58 = r58 + 1;
        goto L_0x3e5e;
    L_0x3eb7:
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r122 - r4;
        r0 = r194;
        r0.availableTimeWidth = r4;
        goto L_0x3d8f;
    L_0x3ec5:
        r4 = 0;
        goto L_0x3dcc;
    L_0x3ec8:
        r4 = r195.needDrawBluredPreview();
        if (r4 == 0) goto L_0x3dfb;
    L_0x3ece:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x3ee3;
    L_0x3ed4:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r99 = r0;
        r23 = r99;
        goto L_0x3dfb;
    L_0x3ee3:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r99 = r0;
        r23 = r99;
        goto L_0x3dfb;
    L_0x3efa:
        r8 = 0;
        goto L_0x3ead;
    L_0x3efd:
        r0 = r150;
        r4 = r0.minY;
        r0 = r194;
        r6 = r0.currentPosition;
        r6 = r6.minY;
        if (r4 != r6) goto L_0x3var_;
    L_0x3var_:
        r0 = r83;
        r0 = (double) r0;
        r20 = r0;
        r0 = r150;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r150;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x3f3f;
    L_0x3var_:
        r0 = r150;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x3var_:
        r8 = r8 + r24;
        r8 = r8 + r20;
        r0 = (int) r8;
        r83 = r0;
        goto L_0x3eb4;
    L_0x3f3f:
        r8 = 0;
        goto L_0x3var_;
    L_0x3var_:
        r0 = r150;
        r4 = r0.minY;
        r0 = r194;
        r6 = r0.currentPosition;
        r6 = r6.minY;
        if (r4 <= r6) goto L_0x3eb4;
    L_0x3f4e:
        r4 = r94 - r83;
        r23 = r23 + r4;
    L_0x3var_:
        r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        r0 = r194;
        r4 = r0.isAvatarVisible;
        if (r4 == 0) goto L_0x3var_;
    L_0x3var_:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
    L_0x3var_:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        if (r4 == 0) goto L_0x40e5;
    L_0x3var_:
        r99 = 0;
        r58 = 0;
    L_0x3var_:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        r4 = r4.length;
        r0 = r58;
        if (r0 >= r4) goto L_0x3var_;
    L_0x3f7f:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        r4 = r4[r58];
        r4 = r4 * r121;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r99 = r99 + r4;
        r58 = r58 + 1;
        goto L_0x3var_;
    L_0x3var_:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.maxY;
        r0 = r194;
        r6 = r0.currentPosition;
        r6 = r6.minY;
        r4 = r4 - r6;
        r6 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 * r6;
        r99 = r99 + r4;
    L_0x3faa:
        r0 = r23;
        r1 = r194;
        r1.backgroundWidth = r0;
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x40f7;
    L_0x3fba:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 1;
        if (r4 == 0) goto L_0x40f7;
    L_0x3fc4:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
    L_0x3fcc:
        r146 = r23;
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.edge;
        if (r4 != 0) goto L_0x3fde;
    L_0x3fd6:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r146 = r146 + r4;
    L_0x3fde:
        r145 = r99;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r146 - r4;
        r27 = r27 + r4;
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 != 0) goto L_0x4006;
    L_0x3ff4:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.hasSibling;
        if (r4 == 0) goto L_0x421c;
    L_0x3ffc:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 != 0) goto L_0x421c;
    L_0x4006:
        r0 = r194;
        r4 = r0.currentPosition;
        r0 = r194;
        r4 = r0.getAdditionalWidthForPosition(r4);
        r27 = r27 + r4;
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.messages;
        r82 = r4.size();
        r104 = 0;
    L_0x401e:
        r0 = r104;
        r1 = r82;
        if (r0 >= r1) goto L_0x421c;
    L_0x4024:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.messages;
        r0 = r104;
        r117 = r4.get(r0);
        r117 = (org.telegram.messenger.MessageObject) r117;
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r104;
        r161 = r4.get(r0);
        r161 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r161;
        r0 = r194;
        r4 = r0.currentPosition;
        r0 = r161;
        if (r0 == r4) goto L_0x420b;
    L_0x4048:
        r0 = r161;
        r4 = r0.flags;
        r4 = r4 & 8;
        if (r4 == 0) goto L_0x420b;
    L_0x4050:
        r0 = r161;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r23 = r0;
        r0 = r161;
        r4 = r0.minY;
        if (r4 == 0) goto L_0x4187;
    L_0x406a:
        r4 = r195.isOutOwner();
        if (r4 == 0) goto L_0x4078;
    L_0x4070:
        r0 = r161;
        r4 = r0.flags;
        r4 = r4 & 1;
        if (r4 != 0) goto L_0x4086;
    L_0x4078:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x4187;
    L_0x407e:
        r0 = r161;
        r4 = r0.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x4187;
    L_0x4086:
        r94 = 0;
        r83 = 0;
        r58 = 0;
    L_0x408c:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r0 = r58;
        if (r0 >= r4) goto L_0x4183;
    L_0x409a:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r58;
        r150 = r4.get(r0);
        r150 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r150;
        r0 = r150;
        r4 = r0.minY;
        if (r4 != 0) goto L_0x4136;
    L_0x40ae:
        r0 = r94;
        r0 = (double) r0;
        r20 = r0;
        r0 = r150;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r150;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x4133;
    L_0x40ca:
        r0 = r150;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x40db:
        r8 = r8 + r24;
        r8 = r8 + r20;
        r0 = (int) r8;
        r94 = r0;
    L_0x40e2:
        r58 = r58 + 1;
        goto L_0x408c;
    L_0x40e5:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.ph;
        r4 = r4 * r121;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r99 = r0;
        goto L_0x3faa;
    L_0x40f7:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 2;
        if (r4 != 0) goto L_0x4115;
    L_0x4101:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 1;
        if (r4 != 0) goto L_0x4115;
    L_0x410b:
        r4 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        goto L_0x3fcc;
    L_0x4115:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x4129;
    L_0x411f:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        goto L_0x3fcc;
    L_0x4129:
        r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        goto L_0x3fcc;
    L_0x4133:
        r8 = 0;
        goto L_0x40db;
    L_0x4136:
        r0 = r150;
        r4 = r0.minY;
        r0 = r161;
        r6 = r0.minY;
        if (r4 != r6) goto L_0x4179;
    L_0x4140:
        r0 = r83;
        r0 = (double) r0;
        r20 = r0;
        r0 = r150;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r150;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x4176;
    L_0x415c:
        r0 = r150;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r85;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x416d:
        r8 = r8 + r24;
        r8 = r8 + r20;
        r0 = (int) r8;
        r83 = r0;
        goto L_0x40e2;
    L_0x4176:
        r8 = 0;
        goto L_0x416d;
    L_0x4179:
        r0 = r150;
        r4 = r0.minY;
        r0 = r161;
        r6 = r0.minY;
        if (r4 <= r6) goto L_0x40e2;
    L_0x4183:
        r4 = r94 - r83;
        r23 = r23 + r4;
    L_0x4187:
        r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        r0 = r161;
        r4 = r0.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x4508;
    L_0x4197:
        r0 = r161;
        r4 = r0.flags;
        r4 = r4 & 1;
        if (r4 == 0) goto L_0x4508;
    L_0x419f:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
    L_0x41a7:
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x41c9;
    L_0x41ad:
        r4 = r117.isOutOwner();
        if (r4 != 0) goto L_0x41c9;
    L_0x41b3:
        r4 = r117.needDrawAvatar();
        if (r4 == 0) goto L_0x41c9;
    L_0x41b9:
        if (r161 == 0) goto L_0x41c1;
    L_0x41bb:
        r0 = r161;
        r4 = r0.edge;
        if (r4 == 0) goto L_0x41c9;
    L_0x41c1:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
    L_0x41c9:
        r0 = r194;
        r1 = r161;
        r4 = r0.getAdditionalWidthForPosition(r1);
        r23 = r23 + r4;
        r0 = r161;
        r4 = r0.edge;
        if (r4 != 0) goto L_0x41e1;
    L_0x41d9:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 + r4;
    L_0x41e1:
        r27 = r27 + r23;
        r0 = r161;
        r4 = r0.minX;
        r0 = r194;
        r6 = r0.currentPosition;
        r6 = r6.minX;
        if (r4 < r6) goto L_0x4201;
    L_0x41ef:
        r0 = r194;
        r4 = r0.currentMessagesGroup;
        r4 = r4.hasSibling;
        if (r4 == 0) goto L_0x420b;
    L_0x41f7:
        r0 = r161;
        r4 = r0.minY;
        r0 = r161;
        r6 = r0.maxY;
        if (r4 == r6) goto L_0x420b;
    L_0x4201:
        r0 = r194;
        r4 = r0.captionOffsetX;
        r4 = r4 - r23;
        r0 = r194;
        r0.captionOffsetX = r4;
    L_0x420b:
        r0 = r117;
        r4 = r0.caption;
        if (r4 == 0) goto L_0x4546;
    L_0x4211:
        r0 = r194;
        r4 = r0.currentCaption;
        if (r4 == 0) goto L_0x453e;
    L_0x4217:
        r4 = 0;
        r0 = r194;
        r0.currentCaption = r4;
    L_0x421c:
        r0 = r194;
        r4 = r0.currentCaption;
        if (r4 == 0) goto L_0x4344;
    L_0x4222:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x45de }
        r6 = 24;
        if (r4 < r6) goto L_0x45bf;
    L_0x4228:
        r0 = r194;
        r4 = r0.currentCaption;	 Catch:{ Exception -> 0x45de }
        r6 = 0;
        r0 = r194;
        r8 = r0.currentCaption;	 Catch:{ Exception -> 0x45de }
        r8 = r8.length();	 Catch:{ Exception -> 0x45de }
        r9 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x45de }
        r0 = r27;
        r4 = android.text.StaticLayout.Builder.obtain(r4, r6, r8, r9, r0);	 Catch:{ Exception -> 0x45de }
        r6 = 1;
        r4 = r4.setBreakStrategy(r6);	 Catch:{ Exception -> 0x45de }
        r6 = 0;
        r4 = r4.setHyphenationFrequency(r6);	 Catch:{ Exception -> 0x45de }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x45de }
        r4 = r4.setAlignment(r6);	 Catch:{ Exception -> 0x45de }
        r4 = r4.build();	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x45de }
    L_0x4255:
        r0 = r194;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x45de }
        r113 = r4.getLineCount();	 Catch:{ Exception -> 0x45de }
        if (r113 <= 0) goto L_0x4344;
    L_0x425f:
        if (r95 == 0) goto L_0x45e8;
    L_0x4261:
        r4 = 0;
        r0 = r194;
        r0.captionWidth = r4;	 Catch:{ Exception -> 0x45de }
        r58 = 0;
    L_0x4268:
        r0 = r58;
        r1 = r113;
        if (r0 >= r1) goto L_0x42a4;
    L_0x426e:
        r0 = r194;
        r4 = r0.captionWidth;	 Catch:{ Exception -> 0x45de }
        r8 = (double) r4;	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x45de }
        r0 = r58;
        r4 = r4.getLineWidth(r0);	 Catch:{ Exception -> 0x45de }
        r0 = (double) r4;	 Catch:{ Exception -> 0x45de }
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);	 Catch:{ Exception -> 0x45de }
        r0 = r20;
        r8 = java.lang.Math.max(r8, r0);	 Catch:{ Exception -> 0x45de }
        r4 = (int) r8;	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r0.captionWidth = r4;	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x45de }
        r0 = r58;
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x45de }
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x45e4;
    L_0x429e:
        r0 = r27;
        r1 = r194;
        r1.captionWidth = r0;	 Catch:{ Exception -> 0x45de }
    L_0x42a4:
        r0 = r194;
        r4 = r0.captionWidth;	 Catch:{ Exception -> 0x45de }
        r0 = r27;
        if (r4 <= r0) goto L_0x42b2;
    L_0x42ac:
        r0 = r27;
        r1 = r194;
        r1.captionWidth = r0;	 Catch:{ Exception -> 0x45de }
    L_0x42b2:
        r0 = r194;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x45de }
        r4 = r4.getHeight();	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r0.captionHeight = r4;	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r4 = r0.captionHeight;	 Catch:{ Exception -> 0x45de }
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x45de }
        r4 = r4 + r6;
        r0 = r194;
        r0.addedCaptionHeight = r4;	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r4 = r0.currentPosition;	 Catch:{ Exception -> 0x45de }
        if (r4 == 0) goto L_0x42dd;
    L_0x42d3:
        r0 = r194;
        r4 = r0.currentPosition;	 Catch:{ Exception -> 0x45de }
        r4 = r4.flags;	 Catch:{ Exception -> 0x45de }
        r4 = r4 & 8;
        if (r4 == 0) goto L_0x45f0;
    L_0x42dd:
        r0 = r194;
        r4 = r0.addedCaptionHeight;	 Catch:{ Exception -> 0x45de }
        r60 = r60 + r4;
        r0 = r194;
        r4 = r0.captionWidth;	 Catch:{ Exception -> 0x45de }
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x45de }
        r6 = r146 - r6;
        r191 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x45de }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x45de }
        r6 = r6 + -1;
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r8 = r0.captionLayout;	 Catch:{ Exception -> 0x45de }
        r8 = r8.getLineCount();	 Catch:{ Exception -> 0x45de }
        r8 = r8 + -1;
        r6 = r6.getLineLeft(r8);	 Catch:{ Exception -> 0x45de }
        r112 = r4 + r6;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x45de }
        r4 = r4 + r191;
        r4 = (float) r4;	 Catch:{ Exception -> 0x45de }
        r4 = r4 - r112;
        r0 = r177;
        r6 = (float) r0;	 Catch:{ Exception -> 0x45de }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x4344;
    L_0x432b:
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x45de }
        r60 = r60 + r4;
        r0 = r194;
        r4 = r0.addedCaptionHeight;	 Catch:{ Exception -> 0x45de }
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x45de }
        r4 = r4 + r6;
        r0 = r194;
        r0.addedCaptionHeight = r4;	 Catch:{ Exception -> 0x45de }
        r79 = 1;
    L_0x4344:
        if (r95 == 0) goto L_0x4382;
    L_0x4346:
        r0 = r194;
        r4 = r0.captionWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        if (r0 >= r4) goto L_0x4382;
    L_0x4355:
        r0 = r194;
        r4 = r0.captionWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r146 = r4 + r6;
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r146;
        r0 = r194;
        r0.backgroundWidth = r4;
        r0 = r194;
        r4 = r0.mediaBackground;
        if (r4 != 0) goto L_0x4382;
    L_0x4373:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.backgroundWidth = r4;
    L_0x4382:
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r0 = r23;
        r10 = (float) r0;
        r12 = org.telegram.messenger.AndroidUtilities.density;
        r10 = r10 / r12;
        r10 = (int) r10;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r9 = 1;
        r0 = r99;
        r10 = (float) r0;
        r12 = org.telegram.messenger.AndroidUtilities.density;
        r10 = r10 / r12;
        r10 = (int) r10;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r4 = java.lang.String.format(r4, r6, r8);
        r0 = r194;
        r0.currentPhotoFilterThumb = r4;
        r0 = r194;
        r0.currentPhotoFilter = r4;
        r0 = r195;
        r4 = r0.photoThumbs;
        if (r4 == 0) goto L_0x43c3;
    L_0x43b8:
        r0 = r195;
        r4 = r0.photoThumbs;
        r4 = r4.size();
        r6 = 1;
        if (r4 > r6) goto L_0x43d9;
    L_0x43c3:
        r0 = r195;
        r4 = r0.type;
        r6 = 3;
        if (r4 == r6) goto L_0x43d9;
    L_0x43ca:
        r0 = r195;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x43d9;
    L_0x43d2:
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x4417;
    L_0x43d9:
        r4 = r195.needDrawBluredPreview();
        if (r4 == 0) goto L_0x45f7;
    L_0x43df:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r194;
        r6 = r0.currentPhotoFilter;
        r4 = r4.append(r6);
        r6 = "_b2";
        r4 = r4.append(r6);
        r4 = r4.toString();
        r0 = r194;
        r0.currentPhotoFilter = r4;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r194;
        r6 = r0.currentPhotoFilterThumb;
        r4 = r4.append(r6);
        r6 = "_b2";
        r4 = r4.append(r6);
        r4 = r4.toString();
        r0 = r194;
        r0.currentPhotoFilterThumb = r4;
    L_0x4417:
        r137 = 0;
        r0 = r195;
        r4 = r0.type;
        r6 = 3;
        if (r4 == r6) goto L_0x442f;
    L_0x4420:
        r0 = r195;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x442f;
    L_0x4428:
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x4431;
    L_0x442f:
        r137 = 1;
    L_0x4431:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x4448;
    L_0x4437:
        if (r137 != 0) goto L_0x4448;
    L_0x4439:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r4 = r4.size;
        if (r4 != 0) goto L_0x4448;
    L_0x4441:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r6 = -1;
        r4.size = r6;
    L_0x4448:
        r0 = r194;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x445f;
    L_0x444e:
        if (r137 != 0) goto L_0x445f;
    L_0x4450:
        r0 = r194;
        r4 = r0.currentPhotoObjectThumb;
        r4 = r4.size;
        if (r4 != 0) goto L_0x445f;
    L_0x4458:
        r0 = r194;
        r4 = r0.currentPhotoObjectThumb;
        r6 = -1;
        r4.size = r6;
    L_0x445f:
        r4 = org.telegram.messenger.SharedConfig.autoplayVideo;
        if (r4 == 0) goto L_0x44af;
    L_0x4463:
        r0 = r195;
        r4 = r0.type;
        r6 = 3;
        if (r4 != r6) goto L_0x44af;
    L_0x446a:
        r4 = r195.needDrawBluredPreview();
        if (r4 != 0) goto L_0x44af;
    L_0x4470:
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.mediaExists;
        if (r4 != 0) goto L_0x4490;
    L_0x4478:
        r4 = r195.canStreamVideo();
        if (r4 == 0) goto L_0x44af;
    L_0x447e:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r194;
        r6 = r0.currentMessageObject;
        r4 = r4.canDownloadMedia(r6);
        if (r4 == 0) goto L_0x44af;
    L_0x4490:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x4618;
    L_0x4496:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 1;
        if (r4 == 0) goto L_0x4615;
    L_0x44a0:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x4615;
    L_0x44aa:
        r4 = 1;
    L_0x44ab:
        r0 = r194;
        r0.autoPlayingVideo = r4;
    L_0x44af:
        r0 = r194;
        r4 = r0.autoPlayingVideo;
        if (r4 == 0) goto L_0x461f;
    L_0x44b5:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setAllowStartAnimation(r6);
        r0 = r194;
        r4 = r0.photoImage;
        r4.startAnimation();
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.document;
        r45 = r0;
        r46 = "g";
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r47 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r48 = r0;
        r49 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r50 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r51 = r0;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r0 = r4.size;
        r52 = r0;
        r53 = 0;
        r55 = 0;
        r54 = r195;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52, r53, r54, r55);
        goto L_0x304d;
    L_0x4508:
        r0 = r161;
        r4 = r0.flags;
        r4 = r4 & 2;
        if (r4 != 0) goto L_0x4522;
    L_0x4510:
        r0 = r161;
        r4 = r0.flags;
        r4 = r4 & 1;
        if (r4 != 0) goto L_0x4522;
    L_0x4518:
        r4 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        goto L_0x41a7;
    L_0x4522:
        r0 = r161;
        r4 = r0.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x4534;
    L_0x452a:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        goto L_0x41a7;
    L_0x4534:
        r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        goto L_0x41a7;
    L_0x453e:
        r0 = r117;
        r4 = r0.caption;
        r0 = r194;
        r0.currentCaption = r4;
    L_0x4546:
        r104 = r104 + 1;
        goto L_0x401e;
    L_0x454a:
        r145 = r99;
        r146 = r23;
        r0 = r195;
        r4 = r0.caption;
        r0 = r194;
        r0.currentCaption = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x45a1;
    L_0x455c:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.65 double:5.234532584E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r130 = r0;
    L_0x4568:
        r4 = r195.needDrawBluredPreview();
        if (r4 != 0) goto L_0x45b6;
    L_0x456e:
        r0 = r194;
        r4 = r0.currentCaption;
        if (r4 == 0) goto L_0x45b6;
    L_0x4574:
        r0 = r146;
        r1 = r130;
        if (r0 >= r1) goto L_0x45b6;
    L_0x457a:
        r27 = r130;
        r95 = 1;
    L_0x457e:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r146;
        r0 = r194;
        r0.backgroundWidth = r4;
        r0 = r194;
        r4 = r0.mediaBackground;
        if (r4 != 0) goto L_0x421c;
    L_0x4590:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.backgroundWidth = r4;
        goto L_0x421c;
    L_0x45a1:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.65 double:5.234532584E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r130 = r0;
        goto L_0x4568;
    L_0x45b6:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r27 = r146 - r4;
        goto L_0x457e;
    L_0x45bf:
        r44 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x45de }
        r0 = r194;
        r0 = r0.currentCaption;	 Catch:{ Exception -> 0x45de }
        r45 = r0;
        r46 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x45de }
        r48 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x45de }
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r50 = 0;
        r51 = 0;
        r47 = r27;
        r44.<init>(r45, r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x45de }
        r0 = r44;
        r1 = r194;
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x45de }
        goto L_0x4255;
    L_0x45de:
        r92 = move-exception;
        org.telegram.messenger.FileLog.e(r92);
        goto L_0x4344;
    L_0x45e4:
        r58 = r58 + 1;
        goto L_0x4268;
    L_0x45e8:
        r0 = r27;
        r1 = r194;
        r1.captionWidth = r0;	 Catch:{ Exception -> 0x45de }
        goto L_0x42b2;
    L_0x45f0:
        r4 = 0;
        r0 = r194;
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x45de }
        goto L_0x4344;
    L_0x45f7:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r194;
        r6 = r0.currentPhotoFilterThumb;
        r4 = r4.append(r6);
        r6 = "_b";
        r4 = r4.append(r6);
        r4 = r4.toString();
        r0 = r194;
        r0.currentPhotoFilterThumb = r4;
        goto L_0x4417;
    L_0x4615:
        r4 = 0;
        goto L_0x44ab;
    L_0x4618:
        r4 = 1;
        r0 = r194;
        r0.autoPlayingVideo = r4;
        goto L_0x44af;
    L_0x461f:
        r0 = r195;
        r4 = r0.type;
        r6 = 1;
        if (r4 != r6) goto L_0x4728;
    L_0x4626:
        r0 = r195;
        r4 = r0.useCustomPhoto;
        if (r4 == 0) goto L_0x4640;
    L_0x462c:
        r0 = r194;
        r4 = r0.photoImage;
        r6 = r194.getResources();
        r8 = NUM; // 0x7var_a float:1.794567E38 double:1.052935769E-314;
        r6 = r6.getDrawable(r8);
        r4.setImageBitmap(r6);
        goto L_0x304d;
    L_0x4640:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x471c;
    L_0x4646:
        r144 = 1;
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r93 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r0 = r195;
        r4 = r0.mediaExists;
        if (r4 == 0) goto L_0x46c2;
    L_0x4656:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r194;
        r4.removeLoadingFileObserver(r0);
    L_0x4663:
        if (r144 != 0) goto L_0x4687;
    L_0x4665:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r194;
        r6 = r0.currentMessageObject;
        r4 = r4.canDownloadMedia(r6);
        if (r4 != 0) goto L_0x4687;
    L_0x4677:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r93;
        r4 = r4.isLoadingFile(r0);
        if (r4 == 0) goto L_0x46d1;
    L_0x4687:
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r45 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r46 = r0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r47 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r48 = r0;
        if (r137 == 0) goto L_0x46c5;
    L_0x46a7:
        r49 = 0;
    L_0x46a9:
        r50 = 0;
        r0 = r194;
        r0 = r0.currentMessageObject;
        r51 = r0;
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.shouldEncryptPhotoOrVideo();
        if (r4 == 0) goto L_0x46ce;
    L_0x46bb:
        r52 = 2;
    L_0x46bd:
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52);
        goto L_0x304d;
    L_0x46c2:
        r144 = 0;
        goto L_0x4663;
    L_0x46c5:
        r0 = r194;
        r4 = r0.currentPhotoObject;
        r0 = r4.size;
        r49 = r0;
        goto L_0x46a9;
    L_0x46ce:
        r52 = 0;
        goto L_0x46bd;
    L_0x46d1:
        r4 = 1;
        r0 = r194;
        r0.photoNotSet = r4;
        r0 = r194;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x4710;
    L_0x46dc:
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r45 = 0;
        r46 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r47 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r48 = r0;
        r49 = 0;
        r50 = 0;
        r0 = r194;
        r0 = r0.currentMessageObject;
        r51 = r0;
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.shouldEncryptPhotoOrVideo();
        if (r4 == 0) goto L_0x470d;
    L_0x4706:
        r52 = 2;
    L_0x4708:
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52);
        goto L_0x304d;
    L_0x470d:
        r52 = 0;
        goto L_0x4708;
    L_0x4710:
        r0 = r194;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        goto L_0x304d;
    L_0x471c:
        r0 = r194;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        goto L_0x304d;
    L_0x4728:
        r0 = r195;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x4737;
    L_0x4730:
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x4859;
    L_0x4737:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r93 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r116 = 0;
        r0 = r195;
        r4 = r0.attachPathExists;
        if (r4 == 0) goto L_0x47ca;
    L_0x474b:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r194;
        r4.removeLoadingFileObserver(r0);
        r116 = 1;
    L_0x475a:
        r69 = 0;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = org.telegram.messenger.MessageObject.isNewGifDocument(r4);
        if (r4 == 0) goto L_0x47d3;
    L_0x476a:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r194;
        r6 = r0.currentMessageObject;
        r69 = r4.canDownloadMedia(r6);
    L_0x477a:
        r4 = r195.isSending();
        if (r4 != 0) goto L_0x4829;
    L_0x4780:
        r4 = r195.isEditing();
        if (r4 != 0) goto L_0x4829;
    L_0x4786:
        if (r116 != 0) goto L_0x479a;
    L_0x4788:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r93;
        r4 = r4.isLoadingFile(r0);
        if (r4 != 0) goto L_0x479a;
    L_0x4798:
        if (r69 == 0) goto L_0x4829;
    L_0x479a:
        r4 = 1;
        r0 = r116;
        if (r0 != r4) goto L_0x47f4;
    L_0x479f:
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r4 = r195.isSendError();
        if (r4 == 0) goto L_0x47eb;
    L_0x47ab:
        r45 = 0;
    L_0x47ad:
        r46 = 0;
        r47 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r48 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r49 = r0;
        r50 = 0;
        r51 = 0;
        r53 = 0;
        r52 = r195;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52, r53);
        goto L_0x304d;
    L_0x47ca:
        r0 = r195;
        r4 = r0.mediaExists;
        if (r4 == 0) goto L_0x475a;
    L_0x47d0:
        r116 = 2;
        goto L_0x475a;
    L_0x47d3:
        r0 = r195;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x477a;
    L_0x47da:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r194;
        r6 = r0.currentMessageObject;
        r69 = r4.canDownloadMedia(r6);
        goto L_0x477a;
    L_0x47eb:
        r0 = r195;
        r4 = r0.messageOwner;
        r0 = r4.attachPath;
        r45 = r0;
        goto L_0x47ad;
    L_0x47f4:
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.document;
        r45 = r0;
        r46 = 0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r47 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r48 = r0;
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r0 = r4.size;
        r49 = r0;
        r50 = 0;
        r52 = 0;
        r51 = r195;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52);
        goto L_0x304d;
    L_0x4829:
        r4 = 1;
        r0 = r194;
        r0.photoNotSet = r4;
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r45 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r46 = r0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r47 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r48 = r0;
        r49 = 0;
        r50 = 0;
        r52 = 0;
        r51 = r195;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52);
        goto L_0x304d;
    L_0x4859:
        r0 = r194;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r194;
        r0 = r0.currentPhotoObject;
        r45 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilter;
        r46 = r0;
        r0 = r194;
        r0 = r0.currentPhotoObjectThumb;
        r47 = r0;
        r0 = r194;
        r0 = r0.currentPhotoFilterThumb;
        r48 = r0;
        r49 = 0;
        r50 = 0;
        r0 = r194;
        r4 = r0.currentMessageObject;
        r4 = r4.shouldEncryptPhotoOrVideo();
        if (r4 == 0) goto L_0x488e;
    L_0x4885:
        r52 = 2;
    L_0x4887:
        r51 = r195;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52);
        goto L_0x304d;
    L_0x488e:
        r52 = 0;
        goto L_0x4887;
    L_0x4891:
        r0 = r194;
        r4 = r0.drawNameLayout;
        if (r4 == 0) goto L_0x3080;
    L_0x4897:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.reply_to_msg_id;
        if (r4 != 0) goto L_0x3080;
    L_0x489f:
        r0 = r194;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.namesOffset = r4;
        goto L_0x3080;
    L_0x48b0:
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r192 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.totalHeight = r4;
        goto L_0x3128;
    L_0x48c7:
        r0 = r194;
        r4 = r0.namesOffset;
        if (r4 <= 0) goto L_0x48e4;
    L_0x48cd:
        r4 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r192 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.totalHeight = r4;
        goto L_0x3128;
    L_0x48e4:
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r192 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.totalHeight = r4;
        goto L_0x3128;
    L_0x48fb:
        r44 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x491a }
        r0 = r195;
        r0 = r0.caption;	 Catch:{ Exception -> 0x491a }
        r45 = r0;
        r46 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x491a }
        r48 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x491a }
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r50 = 0;
        r51 = 0;
        r47 = r27;
        r44.<init>(r45, r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x491a }
        r0 = r44;
        r1 = r194;
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x491a }
        goto L_0x139c;
    L_0x491a:
        r92 = move-exception;
        org.telegram.messenger.FileLog.e(r92);
        goto L_0x139c;
    L_0x4920:
        r4 = 0;
        goto L_0x13dc;
    L_0x4923:
        r92 = move-exception;
        org.telegram.messenger.FileLog.e(r92);
        goto L_0x1456;
    L_0x4929:
        r4 = 0;
        goto L_0x1510;
    L_0x492c:
        r92 = move-exception;
        org.telegram.messenger.FileLog.e(r92);
        goto L_0x153a;
    L_0x4932:
        r0 = r194;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x4943 }
        r0 = r114;
        r6 = -r0;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x4943 }
        r0 = r194;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x4943 }
        goto L_0x15d0;
    L_0x4943:
        r92 = move-exception;
        org.telegram.messenger.FileLog.e(r92);
    L_0x4947:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.totalHeight = r4;
        if (r79 == 0) goto L_0x497b;
    L_0x4958:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.totalHeight = r4;
        r4 = 2;
        r0 = r79;
        if (r0 != r4) goto L_0x497b;
    L_0x496c:
        r0 = r194;
        r4 = r0.captionHeight;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.captionHeight = r4;
    L_0x497b:
        r0 = r194;
        r4 = r0.botButtons;
        r4.clear();
        if (r129 == 0) goto L_0x4997;
    L_0x4984:
        r0 = r194;
        r4 = r0.botButtonsByData;
        r4.clear();
        r0 = r194;
        r4 = r0.botButtonsByPosition;
        r4.clear();
        r4 = 0;
        r0 = r194;
        r0.botButtonsLayout = r4;
    L_0x4997:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 != 0) goto L_0x4d11;
    L_0x499d:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.reply_markup;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
        if (r4 == 0) goto L_0x4d11;
    L_0x49a7:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.reply_markup;
        r4 = r4.rows;
        r162 = r4.size();
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 * r162;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r194;
        r0.keyboardHeight = r4;
        r0 = r194;
        r0.substractBackgroundHeight = r4;
        r0 = r194;
        r6 = r0.backgroundWidth;
        r0 = r194;
        r4 = r0.mediaBackground;
        if (r4 == 0) goto L_0x4a8a;
    L_0x49d4:
        r4 = 0;
    L_0x49d5:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r0 = r194;
        r0.widthForButtons = r4;
        r96 = 0;
        r0 = r195;
        r4 = r0.wantedBotKeyboardWidth;
        r0 = r194;
        r6 = r0.widthForButtons;
        if (r4 <= r6) goto L_0x4a28;
    L_0x49eb:
        r0 = r194;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x4a8e;
    L_0x49f1:
        r4 = r195.needDrawAvatar();
        if (r4 == 0) goto L_0x4a8e;
    L_0x49f7:
        r4 = r195.isOutOwner();
        if (r4 != 0) goto L_0x4a8e;
    L_0x49fd:
        r4 = NUM; // 0x42780000 float:62.0 double:5.5096253E-315;
    L_0x49ff:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = -r4;
        r118 = r0;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x4a92;
    L_0x4a0c:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r118 = r118 + r4;
    L_0x4a12:
        r0 = r194;
        r4 = r0.backgroundWidth;
        r0 = r195;
        r6 = r0.wantedBotKeyboardWidth;
        r0 = r118;
        r6 = java.lang.Math.min(r6, r0);
        r4 = java.lang.Math.max(r4, r6);
        r0 = r194;
        r0.widthForButtons = r4;
    L_0x4a28:
        r119 = 0;
        r140 = new java.util.HashMap;
        r0 = r194;
        r4 = r0.botButtonsByData;
        r0 = r140;
        r0.<init>(r4);
        r0 = r195;
        r4 = r0.botButtonsLayout;
        if (r4 == 0) goto L_0x4aa9;
    L_0x4a3b:
        r0 = r194;
        r4 = r0.botButtonsLayout;
        if (r4 == 0) goto L_0x4aa9;
    L_0x4a41:
        r0 = r194;
        r4 = r0.botButtonsLayout;
        r0 = r195;
        r6 = r0.botButtonsLayout;
        r6 = r6.toString();
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x4aa9;
    L_0x4a53:
        r141 = new java.util.HashMap;
        r0 = r194;
        r4 = r0.botButtonsByPosition;
        r0 = r141;
        r0.<init>(r4);
    L_0x4a5e:
        r0 = r194;
        r4 = r0.botButtonsByData;
        r4.clear();
        r58 = 0;
    L_0x4a67:
        r0 = r58;
        r1 = r162;
        if (r0 >= r1) goto L_0x4CLASSNAME;
    L_0x4a6d:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.reply_markup;
        r4 = r4.rows;
        r0 = r58;
        r160 = r4.get(r0);
        r160 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r160;
        r0 = r160;
        r4 = r0.buttons;
        r77 = r4.size();
        if (r77 != 0) goto L_0x4abe;
    L_0x4a87:
        r58 = r58 + 1;
        goto L_0x4a67;
    L_0x4a8a:
        r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        goto L_0x49d5;
    L_0x4a8e:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        goto L_0x49ff;
    L_0x4a92:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r118 = r118 + r4;
        goto L_0x4a12;
    L_0x4aa9:
        r0 = r195;
        r4 = r0.botButtonsLayout;
        if (r4 == 0) goto L_0x4abb;
    L_0x4aaf:
        r0 = r195;
        r4 = r0.botButtonsLayout;
        r4 = r4.toString();
        r0 = r194;
        r0.botButtonsLayout = r4;
    L_0x4abb:
        r141 = 0;
        goto L_0x4a5e;
    L_0x4abe:
        r0 = r194;
        r4 = r0.widthForButtons;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r8 = r77 + -1;
        r6 = r6 * r8;
        r4 = r4 - r6;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r76 = r4 / r77;
        r70 = 0;
    L_0x4ad7:
        r0 = r160;
        r4 = r0.buttons;
        r4 = r4.size();
        r0 = r70;
        if (r0 >= r4) goto L_0x4a87;
    L_0x4ae3:
        r74 = new org.telegram.ui.Cells.ChatMessageCell$BotButton;
        r4 = 0;
        r0 = r74;
        r1 = r194;
        r0.<init>(r1, r4);
        r0 = r160;
        r4 = r0.buttons;
        r0 = r70;
        r4 = r4.get(r0);
        r4 = (org.telegram.tgnet.TLRPC.KeyboardButton) r4;
        r0 = r74;
        r0.button = r4;
        r4 = r74.button;
        r4 = r4.data;
        r111 = org.telegram.messenger.Utilities.bytesToHex(r4);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r58;
        r4 = r4.append(r0);
        r6 = "";
        r4 = r4.append(r6);
        r0 = r70;
        r4 = r4.append(r0);
        r150 = r4.toString();
        if (r141 == 0) goto L_0x4bfe;
    L_0x4b26:
        r0 = r141;
        r1 = r150;
        r139 = r0.get(r1);
        r139 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r139;
    L_0x4b30:
        if (r139 == 0) goto L_0x4c0a;
    L_0x4b32:
        r4 = r139.progressAlpha;
        r0 = r74;
        r0.progressAlpha = r4;
        r4 = r139.angle;
        r0 = r74;
        r0.angle = r4;
        r8 = r139.lastUpdateTime;
        r0 = r74;
        r0.lastUpdateTime = r8;
    L_0x4b4d:
        r0 = r194;
        r4 = r0.botButtonsByData;
        r0 = r111;
        r1 = r74;
        r4.put(r0, r1);
        r0 = r194;
        r4 = r0.botButtonsByPosition;
        r0 = r150;
        r1 = r74;
        r4.put(r0, r1);
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r76;
        r4 = r4 * r70;
        r0 = r74;
        r0.x = r4;
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 * r58;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r74;
        r0.y = r4;
        r0 = r74;
        r1 = r76;
        r0.width = r1;
        r4 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r74;
        r0.height = r4;
        r4 = r74.button;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r4 == 0) goto L_0x4CLASSNAME;
    L_0x4ba0:
        r0 = r195;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x4CLASSNAME;
    L_0x4bac:
        r4 = "PaymentReceipt";
        r6 = NUM; // 0x7f0CLASSNAMEdd float:1.8612756E38 double:1.0530982665E-314;
        r45 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x4bb6:
        r44 = new android.text.StaticLayout;
        r46 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r47 = r76 - r4;
        r48 = android.text.Layout.Alignment.ALIGN_CENTER;
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r50 = 0;
        r51 = 0;
        r44.<init>(r45, r46, r47, r48, r49, r50, r51);
        r0 = r74;
        r1 = r44;
        r0.title = r1;
        r0 = r194;
        r4 = r0.botButtons;
        r0 = r74;
        r4.add(r0);
        r0 = r160;
        r4 = r0.buttons;
        r4 = r4.size();
        r4 = r4 + -1;
        r0 = r70;
        if (r0 != r4) goto L_0x4bfa;
    L_0x4beb:
        r4 = r74.x;
        r6 = r74.width;
        r4 = r4 + r6;
        r0 = r119;
        r119 = java.lang.Math.max(r0, r4);
    L_0x4bfa:
        r70 = r70 + 1;
        goto L_0x4ad7;
    L_0x4bfe:
        r0 = r140;
        r1 = r111;
        r139 = r0.get(r1);
        r139 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r139;
        goto L_0x4b30;
    L_0x4c0a:
        r8 = java.lang.System.currentTimeMillis();
        r0 = r74;
        r0.lastUpdateTime = r8;
        goto L_0x4b4d;
    L_0x4CLASSNAME:
        r4 = r74.button;
        r4 = r4.text;
        r6 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
        r6 = r6.getFontMetricsInt();
        r8 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r9 = 0;
        r45 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r8, r9);
        r4 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r76 - r6;
        r6 = (float) r6;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r45;
        r45 = android.text.TextUtils.ellipsize(r0, r4, r6, r8);
        goto L_0x4bb6;
    L_0x4CLASSNAME:
        r0 = r119;
        r1 = r194;
        r1.widthForButtons = r0;
    L_0x4CLASSNAME:
        r0 = r194;
        r4 = r0.drawPinnedBottom;
        if (r4 == 0) goto L_0x4d1d;
    L_0x4c4d:
        r0 = r194;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x4d1d;
    L_0x4CLASSNAME:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.totalHeight = r4;
    L_0x4CLASSNAME:
        r0 = r195;
        r4 = r0.type;
        r6 = 13;
        if (r4 != r6) goto L_0x4CLASSNAME;
    L_0x4c6a:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x428CLASSNAME float:70.0 double:5.51610112E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        if (r4 >= r6) goto L_0x4CLASSNAME;
    L_0x4CLASSNAME:
        r4 = NUM; // 0x428CLASSNAME float:70.0 double:5.51610112E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r194;
        r0.totalHeight = r4;
    L_0x4CLASSNAME:
        r0 = r194;
        r4 = r0.drawPhotoImage;
        if (r4 != 0) goto L_0x4CLASSNAME;
    L_0x4CLASSNAME:
        r0 = r194;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
    L_0x4CLASSNAME:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 5;
        if (r4 != r6) goto L_0x4d82;
    L_0x4CLASSNAME:
        r0 = r194;
        r4 = r0.documentAttach;
        r4 = org.telegram.messenger.MessageObject.isDocumentHasThumb(r4);
        if (r4 == 0) goto L_0x4d5f;
    L_0x4ca1:
        r0 = r194;
        r4 = r0.documentAttach;
        r4 = r4.thumbs;
        r6 = 90;
        r173 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r194;
        r4 = r0.radialProgress;
        r0 = r173;
        r1 = r195;
        r4.setImageOverlay(r0, r1);
    L_0x4cb8:
        r194.updateWaveform();
        r6 = 0;
        if (r86 == 0) goto L_0x4d8d;
    L_0x4cbe:
        r0 = r195;
        r4 = r0.cancelEditing;
        if (r4 != 0) goto L_0x4d8d;
    L_0x4cc4:
        r4 = 1;
    L_0x4cc5:
        r8 = 1;
        r0 = r194;
        r0.updateButtonState(r6, r4, r8);
        r0 = r194;
        r4 = r0.buttonState;
        r6 = 2;
        if (r4 != r6) goto L_0x4d10;
    L_0x4cd2:
        r0 = r194;
        r4 = r0.documentAttachType;
        r6 = 3;
        if (r4 != r6) goto L_0x4d10;
    L_0x4cd9:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r195;
        r4 = r4.canDownloadMedia(r0);
        if (r4 == 0) goto L_0x4d10;
    L_0x4ce9:
        r0 = r194;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r194;
        r6 = r0.documentAttach;
        r0 = r194;
        r8 = r0.currentMessageObject;
        r9 = 1;
        r10 = 0;
        r4.loadFile(r6, r8, r9, r10);
        r4 = 4;
        r0 = r194;
        r0.buttonState = r4;
        r0 = r194;
        r4 = r0.radialProgress;
        r6 = r194.getIconForCurrentState();
        r8 = 0;
        r9 = 0;
        r4.setIcon(r6, r8, r9);
    L_0x4d10:
        return;
    L_0x4d11:
        r4 = 0;
        r0 = r194;
        r0.substractBackgroundHeight = r4;
        r4 = 0;
        r0 = r194;
        r0.keyboardHeight = r4;
        goto L_0x4CLASSNAME;
    L_0x4d1d:
        r0 = r194;
        r4 = r0.drawPinnedBottom;
        if (r4 == 0) goto L_0x4d34;
    L_0x4d23:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.totalHeight = r4;
        goto L_0x4CLASSNAME;
    L_0x4d34:
        r0 = r194;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x4CLASSNAME;
    L_0x4d3a:
        r0 = r194;
        r4 = r0.pinnedBottom;
        if (r4 == 0) goto L_0x4CLASSNAME;
    L_0x4d40:
        r0 = r194;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x4CLASSNAME;
    L_0x4d46:
        r0 = r194;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        if (r4 != 0) goto L_0x4CLASSNAME;
    L_0x4d4e:
        r0 = r194;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r194;
        r0.totalHeight = r4;
        goto L_0x4CLASSNAME;
    L_0x4d5f:
        r4 = 1;
        r0 = r195;
        r65 = r0.getArtworkUrl(r4);
        r4 = android.text.TextUtils.isEmpty(r65);
        if (r4 != 0) goto L_0x4d77;
    L_0x4d6c:
        r0 = r194;
        r4 = r0.radialProgress;
        r0 = r65;
        r4.setImageOverlay(r0);
        goto L_0x4cb8;
    L_0x4d77:
        r0 = r194;
        r4 = r0.radialProgress;
        r6 = 0;
        r8 = 0;
        r4.setImageOverlay(r6, r8);
        goto L_0x4cb8;
    L_0x4d82:
        r0 = r194;
        r4 = r0.radialProgress;
        r6 = 0;
        r8 = 0;
        r4.setImageOverlay(r6, r8);
        goto L_0x4cb8;
    L_0x4d8d:
        r4 = 0;
        goto L_0x4cc5;
    L_0x4d90:
        r92 = move-exception;
        goto L_0x0var_;
    L_0x4d93:
        r92 = move-exception;
        r13 = r158;
        goto L_0x0da3;
    L_0x4d98:
        r15 = r187;
        goto L_0x1cfd;
    L_0x4d9c:
        r15 = r187;
        goto L_0x0fee;
    L_0x4da0:
        r13 = r158;
        goto L_0x0e5b;
    L_0x4da4:
        r158 = r13;
        r11 = r115;
        goto L_0x0db8;
    L_0x4daa:
        r11 = r115;
        goto L_0x0db8;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageContent(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, boolean, boolean):void");
    }

    static final /* synthetic */ int lambda$setMessageContent$0$ChatMessageCell(PollButton o1, PollButton o2) {
        if (o1.decimal > o2.decimal) {
            return -1;
        }
        if (o1.decimal < o2.decimal) {
            return 1;
        }
        return 0;
    }

    public void checkVideoPlayback(boolean allowStart) {
        if (!this.currentMessageObject.isVideo()) {
            if (allowStart) {
                MessageObject playingMessage = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessage == null || !playingMessage.isRoundVideo()) {
                    allowStart = true;
                } else {
                    allowStart = false;
                }
            }
            this.photoImage.setAllowStartAnimation(allowStart);
            if (allowStart) {
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

    protected void onLongPress() {
        if (this.pressedLink instanceof URLSpanMono) {
            this.delegate.didPressUrl(this.currentMessageObject, this.pressedLink, true);
            return;
        }
        if (this.pressedLink instanceof URLSpanNoUnderline) {
            if (this.pressedLink.getURL().startsWith("/")) {
                this.delegate.didPressUrl(this.currentMessageObject, this.pressedLink, true);
                return;
            }
        } else if (this.pressedLink instanceof URLSpan) {
            this.delegate.didPressUrl(this.currentMessageObject, this.pressedLink, true);
            return;
        }
        resetPressedLink(-1);
        if (!(this.buttonPressed == 0 && this.miniButtonPressed == 0 && this.videoButtonPressed == 0 && this.pressedBotButton == -1)) {
            this.buttonPressed = 0;
            this.miniButtonState = 0;
            this.videoButtonPressed = 0;
            this.pressedBotButton = -1;
            invalidate();
        }
        if (this.instantPressed) {
            this.instantButtonPressed = false;
            this.instantPressed = false;
            if (VERSION.SDK_INT >= 21 && this.selectorDrawable != null) {
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
            invalidate();
        }
        if (this.pressedVoteButton != -1) {
            this.pressedVoteButton = -1;
            if (VERSION.SDK_INT >= 21 && this.selectorDrawable != null) {
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
            invalidate();
        }
        if (this.delegate != null) {
            this.delegate.didLongPress(this);
        }
    }

    public void setCheckPressed(boolean value, boolean pressed) {
        this.isCheckPressed = value;
        this.isPressed = pressed;
        updateRadialProgressBackground();
        if (this.useSeekBarWaweform) {
            this.seekBarWaveform.setSelected(isDrawSelectedBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectedBackground());
        }
        invalidate();
    }

    public void setHighlightedAnimated() {
        this.isHighlightedAnimated = true;
        this.highlightProgress = 1000;
        this.lastHighlightProgressTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean isHighlighted() {
        return this.isHighlighted;
    }

    public void setHighlighted(boolean value) {
        if (this.isHighlighted != value) {
            this.isHighlighted = value;
            if (this.isHighlighted) {
                this.isHighlightedAnimated = false;
                this.highlightProgress = 0;
            } else {
                this.lastHighlightProgressTime = System.currentTimeMillis();
                this.isHighlightedAnimated = true;
                this.highlightProgress = 300;
            }
            updateRadialProgressBackground();
            if (this.useSeekBarWaweform) {
                this.seekBarWaveform.setSelected(isDrawSelectedBackground());
            } else {
                this.seekBar.setSelected(isDrawSelectedBackground());
            }
            invalidate();
        }
    }

    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        updateRadialProgressBackground();
        if (this.useSeekBarWaweform) {
            this.seekBarWaveform.setSelected(isDrawSelectedBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectedBackground());
        }
        invalidate();
    }

    private void updateRadialProgressBackground() {
        boolean z = true;
        if (!this.drawRadialCheckBackground) {
            boolean z2;
            RadialProgress2 radialProgress2 = this.radialProgress;
            if (this.isHighlighted || this.isPressed || this.buttonPressed != 0 || isPressed()) {
                z2 = true;
            } else {
                z2 = false;
            }
            radialProgress2.setPressed(z2, false);
            if (this.hasMiniProgress != 0) {
                radialProgress2 = this.radialProgress;
                if (this.isHighlighted || this.isPressed || this.miniButtonPressed != 0 || isPressed()) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                radialProgress2.setPressed(z2, true);
            }
            RadialProgress2 radialProgress22 = this.videoRadialProgress;
            if (!(this.isHighlighted || this.isPressed || this.videoButtonPressed != 0 || isPressed())) {
                z = false;
            }
            radialProgress22.setPressed(z, false);
        }
    }

    public void onSeekBarDrag(float progress) {
        if (this.currentMessageObject != null) {
            this.currentMessageObject.audioProgress = progress;
            MediaController.getInstance().seekToProgress(this.currentMessageObject, progress);
        }
    }

    private void updateWaveform() {
        if (this.currentMessageObject != null && this.documentAttachType == 3) {
            for (int a = 0; a < this.documentAttach.attributes.size(); a++) {
                DocumentAttribute attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                if (attribute instanceof TL_documentAttributeAudio) {
                    if (attribute.waveform == null || attribute.waveform.length == 0) {
                        MediaController.getInstance().generateWaveform(this.currentMessageObject);
                    }
                    this.useSeekBarWaweform = attribute.waveform != null;
                    this.seekBarWaveform.setWaveform(attribute.waveform);
                    return;
                }
            }
        }
    }

    private int createDocumentLayout(int maxWidth, MessageObject messageObject) {
        if (messageObject.type == 0) {
            this.documentAttach = messageObject.messageOwner.media.webpage.document;
        } else {
            this.documentAttach = messageObject.messageOwner.media.document;
        }
        if (this.documentAttach == null) {
            return 0;
        }
        int duration;
        int a;
        DocumentAttribute attribute;
        String str;
        if (MessageObject.isVoiceDocument(this.documentAttach)) {
            this.documentAttachType = 3;
            duration = 0;
            for (a = 0; a < this.documentAttach.attributes.size(); a++) {
                attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                if (attribute instanceof TL_documentAttributeAudio) {
                    duration = attribute.duration;
                    break;
                }
            }
            this.widthBeforeNewTimeLine = (maxWidth - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil((double) Theme.chat_audioTimePaint.measureText("00:00")));
            this.availableTimeWidth = maxWidth - AndroidUtilities.dp(18.0f);
            measureTime(messageObject);
            int minSize = AndroidUtilities.dp(174.0f) + this.timeWidth;
            if (!this.hasLinkPreview) {
                this.backgroundWidth = Math.min(maxWidth, (AndroidUtilities.dp(10.0f) * duration) + minSize);
            }
            this.seekBarWaveform.setMessageObject(messageObject);
            return 0;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
            maxWidth -= AndroidUtilities.dp(86.0f);
            if (maxWidth < 0) {
                maxWidth = AndroidUtilities.dp(100.0f);
            }
            this.songLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicTitle().replace(10, ' '), Theme.chat_audioTitlePaint, (float) (maxWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), Theme.chat_audioTitlePaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.songLayout.getLineCount() > 0) {
                this.songX = -((int) Math.ceil((double) this.songLayout.getLineLeft(0)));
            }
            this.performerLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicAuthor().replace(10, ' '), Theme.chat_audioPerformerPaint, (float) maxWidth, TruncateAt.END), Theme.chat_audioPerformerPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.performerLayout.getLineCount() > 0) {
                this.performerX = -((int) Math.ceil((double) this.performerLayout.getLineLeft(0)));
            }
            duration = 0;
            for (a = 0; a < this.documentAttach.attributes.size(); a++) {
                attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                if (attribute instanceof TL_documentAttributeAudio) {
                    duration = attribute.duration;
                    break;
                }
            }
            int durationWidth = (int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(String.format("%d:%02d / %d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60), Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)})));
            this.widthBeforeNewTimeLine = (this.backgroundWidth - AndroidUtilities.dp(86.0f)) - durationWidth;
            this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.dp(28.0f);
            return durationWidth;
        } else if (MessageObject.isVideoDocument(this.documentAttach)) {
            this.documentAttachType = 4;
            if (!messageObject.needDrawBluredPreview()) {
                updatePlayingMessageProgress();
                str = String.format("%s", new Object[]{AndroidUtilities.formatFileSize((long) this.documentAttach.size)});
                this.docTitleWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(str));
                this.docTitleLayout = new StaticLayout(str, Theme.chat_infoPaint, this.docTitleWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            return 0;
        } else {
            int width;
            boolean z = (this.documentAttach.mime_type != null && this.documentAttach.mime_type.toLowerCase().startsWith("image/")) || MessageObject.isDocumentHasThumb(this.documentAttach);
            this.drawPhotoImage = z;
            if (!this.drawPhotoImage) {
                maxWidth += AndroidUtilities.dp(30.0f);
            }
            this.documentAttachType = 1;
            String name = FileLoader.getDocumentFileName(this.documentAttach);
            if (name == null || name.length() == 0) {
                name = LocaleController.getString("AttachDocument", R.string.AttachDocument);
            }
            this.docTitleLayout = StaticLayoutEx.createStaticLayout(name, Theme.chat_docNamePaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TruncateAt.MIDDLE, maxWidth, 2, false);
            this.docTitleOffsetX = Integer.MIN_VALUE;
            if (this.docTitleLayout == null || this.docTitleLayout.getLineCount() <= 0) {
                width = maxWidth;
                this.docTitleOffsetX = 0;
            } else {
                int maxLineWidth = 0;
                for (a = 0; a < this.docTitleLayout.getLineCount(); a++) {
                    maxLineWidth = Math.max(maxLineWidth, (int) Math.ceil((double) this.docTitleLayout.getLineWidth(a)));
                    this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int) Math.ceil((double) (-this.docTitleLayout.getLineLeft(a))));
                }
                width = Math.min(maxWidth, maxLineWidth);
            }
            str = AndroidUtilities.formatFileSize((long) this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach);
            this.infoWidth = Math.min(maxWidth - AndroidUtilities.dp(30.0f), (int) Math.ceil((double) Theme.chat_infoPaint.measureText(str)));
            CharSequence str2 = TextUtils.ellipsize(str, Theme.chat_infoPaint, (float) this.infoWidth, TruncateAt.END);
            try {
                if (this.infoWidth < 0) {
                    this.infoWidth = AndroidUtilities.dp(10.0f);
                }
                this.infoLayout = new StaticLayout(str2, Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (this.drawPhotoImage) {
                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 320);
                this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 40);
                if ((DownloadController.getInstance(this.currentAccount).getAutodownloadMask() & 1) == 0) {
                    this.currentPhotoObject = null;
                }
                if (this.currentPhotoObject == null || this.currentPhotoObject == this.currentPhotoObjectThumb) {
                    this.currentPhotoObject = null;
                    this.photoImage.setNeedsQualityThumb(true);
                    this.photoImage.setShouldGenerateQualityThumb(true);
                }
                this.currentPhotoFilter = "86_86_b";
                this.photoImage.setImage(this.currentPhotoObject, "86_86", null, this.currentPhotoObjectThumb, this.currentPhotoFilter, 0, null, messageObject, 1);
            }
            return width;
        }
    }

    private void calcBackgroundWidth(int maxWidth, int timeMore, int maxChildWidth) {
        if (this.hasLinkPreview || this.hasOldCaptionPreview || this.hasGamePreview || this.hasInvoicePreview || maxWidth - this.currentMessageObject.lastLineWidth < timeMore || this.currentMessageObject.hasRtl) {
            this.totalHeight += AndroidUtilities.dp(14.0f);
            this.hasNewLineForTime = true;
            this.backgroundWidth = Math.max(maxChildWidth, this.currentMessageObject.lastLineWidth) + AndroidUtilities.dp(31.0f);
            this.backgroundWidth = Math.max(this.backgroundWidth, (this.currentMessageObject.isOutOwner() ? this.timeWidth + AndroidUtilities.dp(17.0f) : this.timeWidth) + AndroidUtilities.dp(31.0f));
            return;
        }
        int diff = maxChildWidth - this.currentMessageObject.lastLineWidth;
        if (diff < 0 || diff > timeMore) {
            this.backgroundWidth = Math.max(maxChildWidth, this.currentMessageObject.lastLineWidth + timeMore) + AndroidUtilities.dp(31.0f);
        } else {
            this.backgroundWidth = ((maxChildWidth + timeMore) - diff) + AndroidUtilities.dp(31.0f);
        }
    }

    public void setHighlightedText(String text) {
        MessageObject messageObject = this.messageObjectToSet != null ? this.messageObjectToSet : this.currentMessageObject;
        if (messageObject != null && messageObject.messageOwner.message != null && messageObject.type == 0 && !TextUtils.isEmpty(messageObject.messageText) && text != null) {
            int start = TextUtils.indexOf(messageObject.messageOwner.message.toLowerCase(), text.toLowerCase());
            if (start != -1) {
                int end = start + text.length();
                int c = 0;
                while (c < messageObject.textLayoutBlocks.size()) {
                    TextLayoutBlock block = (TextLayoutBlock) messageObject.textLayoutBlocks.get(c);
                    if (start < block.charactersOffset || start >= block.charactersOffset + block.textLayout.getText().length()) {
                        c++;
                    } else {
                        this.linkSelectionBlockNum = c;
                        resetUrlPaths(true);
                        try {
                            LinkPath path = obtainNewUrlPath(true);
                            int length = block.textLayout.getText().length();
                            path.setCurrentLayout(block.textLayout, start, 0.0f);
                            block.textLayout.getSelectionPath(start, end - block.charactersOffset, path);
                            if (end >= block.charactersOffset + length) {
                                for (int a = c + 1; a < messageObject.textLayoutBlocks.size(); a++) {
                                    TextLayoutBlock nextBlock = (TextLayoutBlock) messageObject.textLayoutBlocks.get(a);
                                    length = nextBlock.textLayout.getText().length();
                                    path = obtainNewUrlPath(true);
                                    path.setCurrentLayout(nextBlock.textLayout, 0, (float) nextBlock.height);
                                    nextBlock.textLayout.getSelectionPath(0, end - nextBlock.charactersOffset, path);
                                    if (end < (block.charactersOffset + length) - 1) {
                                        break;
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                        invalidate();
                        return;
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

    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.selectorDrawable;
    }

    private boolean isCurrentLocationTimeExpired(MessageObject messageObject) {
        if (this.currentMessageObject.messageOwner.media.period % 60 == 0) {
            if (Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) > messageObject.messageOwner.media.period) {
                return true;
            }
            return false;
        } else if (Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) <= messageObject.messageOwner.media.period - 5) {
            return false;
        } else {
            return true;
        }
    }

    private void checkLocationExpired() {
        if (this.currentMessageObject != null) {
            boolean newExpired = isCurrentLocationTimeExpired(this.currentMessageObject);
            if (newExpired != this.locationExpired) {
                this.locationExpired = newExpired;
                if (this.locationExpired) {
                    MessageObject messageObject = this.currentMessageObject;
                    this.currentMessageObject = null;
                    setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
                    return;
                }
                AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
                this.scheduledInvalidate = true;
                int maxWidth = this.backgroundWidth - AndroidUtilities.dp(91.0f);
                this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation), Theme.chat_locationTitlePaint, (float) maxWidth, TruncateAt.END), Theme.chat_locationTitlePaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
    }

    public void setMessageObject(MessageObject messageObject, GroupedMessages groupedMessages, boolean bottomNear, boolean topNear) {
        if (this.attachedToWindow) {
            setMessageContent(messageObject, groupedMessages, bottomNear, topNear);
            return;
        }
        this.messageObjectToSet = messageObject;
        this.groupedMessagesToSet = groupedMessages;
        this.bottomNearToSet = bottomNear;
        this.topNearToSet = topNear;
    }

    private int getAdditionalWidthForPosition(GroupedMessagePosition position) {
        int w = 0;
        if (position == null) {
            return 0;
        }
        if ((position.flags & 2) == 0) {
            w = 0 + AndroidUtilities.dp(4.0f);
        }
        if ((position.flags & 1) == 0) {
            return w + AndroidUtilities.dp(4.0f);
        }
        return w;
    }

    private void createSelectorDrawable() {
        if (VERSION.SDK_INT >= 21) {
            if (this.selectorDrawable == null) {
                final Paint maskPaint = new Paint(1);
                maskPaint.setColor(-1);
                Drawable maskDrawable = new Drawable() {
                    RectF rect = new RectF();

                    public void draw(Canvas canvas) {
                        Rect bounds = getBounds();
                        this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), maskPaint);
                    }

                    public void setAlpha(int alpha) {
                    }

                    public void setColorFilter(ColorFilter colorFilter) {
                    }

                    public int getOpacity() {
                        return -1;
                    }
                };
                int[][] iArr = new int[][]{StateSet.WILD_CARD};
                int[] iArr2 = new int[1];
                iArr2[0] = Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outPreviewInstantText" : "chat_inPreviewInstantText") & NUM;
                this.selectorDrawable = new RippleDrawable(new ColorStateList(iArr, iArr2), null, maskDrawable);
                this.selectorDrawable.setCallback(this);
            } else {
                Theme.setSelectorDrawableColor(this.selectorDrawable, Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outPreviewInstantText" : "chat_inPreviewInstantText") & NUM, true);
            }
            this.selectorDrawable.setVisible(true, false);
        }
    }

    private void createInstantViewButton() {
        if (VERSION.SDK_INT >= 21 && this.drawInstantView) {
            createSelectorDrawable();
        }
        if (this.drawInstantView && this.instantViewLayout == null) {
            String str;
            this.instantWidth = AndroidUtilities.dp(33.0f);
            if (this.drawInstantViewType == 1) {
                str = LocaleController.getString("OpenChannel", R.string.OpenChannel);
            } else if (this.drawInstantViewType == 2) {
                str = LocaleController.getString("OpenGroup", R.string.OpenGroup);
            } else if (this.drawInstantViewType == 3) {
                str = LocaleController.getString("OpenMessage", R.string.OpenMessage);
            } else if (this.drawInstantViewType == 5) {
                str = LocaleController.getString("ViewContact", R.string.ViewContact);
            } else if (this.drawInstantViewType == 6) {
                str = LocaleController.getString("OpenBackground", R.string.OpenBackground);
            } else {
                str = LocaleController.getString("InstantView", R.string.InstantView);
            }
            int mWidth = this.backgroundWidth - AndroidUtilities.dp(75.0f);
            this.instantViewLayout = new StaticLayout(TextUtils.ellipsize(str, Theme.chat_instantViewPaint, (float) mWidth, TruncateAt.END), Theme.chat_instantViewPaint, AndroidUtilities.dp(2.0f) + mWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.instantWidth = this.backgroundWidth - AndroidUtilities.dp(34.0f);
            this.totalHeight += AndroidUtilities.dp(46.0f);
            if (this.currentMessageObject.type == 12) {
                this.totalHeight += AndroidUtilities.dp(14.0f);
            }
            if (this.instantViewLayout != null && this.instantViewLayout.getLineCount() > 0) {
                int dp;
                int ceil = ((int) (((double) this.instantWidth) - Math.ceil((double) this.instantViewLayout.getLineWidth(0)))) / 2;
                if (this.drawInstantViewType == 0) {
                    dp = AndroidUtilities.dp(8.0f);
                } else {
                    dp = 0;
                }
                this.instantTextX = dp + ceil;
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

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.currentMessageObject != null && (this.currentMessageObject.checkLayout() || ((this.currentMessageObject.type == 17 || this.currentPosition != null) && this.lastHeight != AndroidUtilities.displaySize.y))) {
            this.inLayout = true;
            MessageObject messageObject = this.currentMessageObject;
            this.currentMessageObject = null;
            setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
            this.inLayout = false;
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), this.totalHeight + this.keyboardHeight);
        this.lastHeight = AndroidUtilities.displaySize.y;
    }

    public void forceResetMessageObject() {
        MessageObject messageObject = this.currentMessageObject;
        this.currentMessageObject = null;
        setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
    }

    private int getGroupPhotosWidth() {
        if (AndroidUtilities.isInMultiwindow || !AndroidUtilities.isTablet() || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
            return AndroidUtilities.displaySize.x;
        }
        int leftWidth = (AndroidUtilities.displaySize.x / 100) * 35;
        if (leftWidth < AndroidUtilities.dp(320.0f)) {
            leftWidth = AndroidUtilities.dp(320.0f);
        }
        return AndroidUtilities.displaySize.x - leftWidth;
    }

    @SuppressLint({"DrawAllocation"})
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.currentMessageObject != null) {
            if (changed || !this.wasLayout) {
                this.layoutWidth = getMeasuredWidth();
                this.layoutHeight = getMeasuredHeight() - this.substractBackgroundHeight;
                if (this.timeTextWidth < 0) {
                    this.timeTextWidth = AndroidUtilities.dp(10.0f);
                }
                this.timeLayout = new StaticLayout(this.currentTimeString, Theme.chat_timePaint, this.timeTextWidth + AndroidUtilities.dp(100.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.mediaBackground) {
                    if (this.currentMessageObject.isOutOwner()) {
                        this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(42.0f);
                    } else {
                        this.timeX = (this.isAvatarVisible ? AndroidUtilities.dp(48.0f) : 0) + ((this.backgroundWidth - AndroidUtilities.dp(4.0f)) - this.timeWidth);
                        if (!(this.currentPosition == null || this.currentPosition.leftSpanOffset == 0)) {
                            this.timeX += (int) Math.ceil((double) ((((float) this.currentPosition.leftSpanOffset) / 1000.0f) * ((float) getGroupPhotosWidth())));
                        }
                    }
                } else if (this.currentMessageObject.isOutOwner()) {
                    this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(38.5f);
                } else {
                    int dp;
                    int dp2 = (this.backgroundWidth - AndroidUtilities.dp(9.0f)) - this.timeWidth;
                    if (this.isAvatarVisible) {
                        dp = AndroidUtilities.dp(48.0f);
                    } else {
                        dp = 0;
                    }
                    this.timeX = dp + dp2;
                }
                if ((this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                    this.viewsLayout = new StaticLayout(this.currentViewsString, Theme.chat_timePaint, this.viewsTextWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                } else {
                    this.viewsLayout = null;
                }
                if (this.isAvatarVisible) {
                    this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0f), this.avatarImage.getImageY(), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
                }
                this.wasLayout = true;
            }
            if (this.currentMessageObject.type == 0) {
                this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
            }
            if (this.currentMessageObject.isRoundVideo()) {
                updatePlayingMessageProgress();
            }
            int x;
            if (this.documentAttachType == 3) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(57.0f);
                    this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                    this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
                } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
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
                this.seekBarWaveform.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : 0) + 92)), AndroidUtilities.dp(30.0f));
                this.seekBar.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : 0) + 72)), AndroidUtilities.dp(30.0f));
                this.seekBarY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
                this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
                updatePlayingMessageProgress();
            } else if (this.documentAttachType == 5) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(56.0f);
                    this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                    this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
                } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
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
                this.seekBar.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : 0) + 65)), AndroidUtilities.dp(30.0f));
                this.seekBarY = (AndroidUtilities.dp(29.0f) + this.namesOffset) + this.mediaOffsetY;
                this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
                updatePlayingMessageProgress();
            } else if (this.documentAttachType == 1 && !this.drawPhotoImage) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
                    this.buttonX = AndroidUtilities.dp(71.0f);
                } else {
                    this.buttonX = AndroidUtilities.dp(23.0f);
                }
                if (this.hasLinkPreview) {
                    this.buttonX += AndroidUtilities.dp(10.0f);
                }
                this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
                this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0f), this.buttonY - AndroidUtilities.dp(10.0f), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
            } else if (this.currentMessageObject.type == 12) {
                if (this.currentMessageObject.isOutOwner()) {
                    x = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
                    x = AndroidUtilities.dp(72.0f);
                } else {
                    x = AndroidUtilities.dp(23.0f);
                }
                this.photoImage.setImageCoords(x, AndroidUtilities.dp(13.0f) + this.namesOffset, AndroidUtilities.dp(44.0f), AndroidUtilities.dp(44.0f));
            } else {
                if (this.currentMessageObject.type == 0 && (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview)) {
                    int linkX;
                    if (this.hasGamePreview) {
                        linkX = this.unmovedTextX - AndroidUtilities.dp(10.0f);
                    } else if (this.hasInvoicePreview) {
                        linkX = this.unmovedTextX + AndroidUtilities.dp(1.0f);
                    } else {
                        linkX = this.unmovedTextX + AndroidUtilities.dp(1.0f);
                    }
                    if (this.isSmallImage) {
                        x = (this.backgroundWidth + linkX) - AndroidUtilities.dp(81.0f);
                    } else {
                        x = linkX + (this.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f));
                    }
                } else if (!this.currentMessageObject.isOutOwner()) {
                    if (this.isChat && this.isAvatarVisible) {
                        x = AndroidUtilities.dp(63.0f);
                    } else {
                        x = AndroidUtilities.dp(15.0f);
                    }
                    if (!(this.currentPosition == null || this.currentPosition.edge)) {
                        x -= AndroidUtilities.dp(10.0f);
                    }
                } else if (this.mediaBackground) {
                    x = (this.layoutWidth - this.backgroundWidth) - AndroidUtilities.dp(3.0f);
                } else {
                    x = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(6.0f);
                }
                if (this.currentPosition != null) {
                    if ((this.currentPosition.flags & 1) == 0) {
                        x -= AndroidUtilities.dp(2.0f);
                    }
                    if (this.currentPosition.leftSpanOffset != 0) {
                        x += (int) Math.ceil((double) ((((float) this.currentPosition.leftSpanOffset) / 1000.0f) * ((float) getGroupPhotosWidth())));
                    }
                }
                if (this.currentMessageObject.type != 0) {
                    x -= AndroidUtilities.dp(2.0f);
                }
                this.photoImage.setImageCoords(x, this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                this.buttonX = (int) (((float) x) + (((float) (this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f))) / 2.0f));
                this.buttonY = this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f)) / 2);
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0f), this.buttonY + AndroidUtilities.dp(48.0f));
                this.deleteProgressRect.set((float) (this.buttonX + AndroidUtilities.dp(5.0f)), (float) (this.buttonY + AndroidUtilities.dp(5.0f)), (float) (this.buttonX + AndroidUtilities.dp(43.0f)), (float) (this.buttonY + AndroidUtilities.dp(43.0f)));
                if (this.documentAttachType == 4) {
                    this.videoButtonX = this.photoImage.getImageX() + AndroidUtilities.dp(8.0f);
                    this.videoButtonY = this.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                    this.videoRadialProgress.setProgressRect(this.videoButtonX, this.videoButtonY, this.videoButtonX + AndroidUtilities.dp(24.0f), this.videoButtonY + AndroidUtilities.dp(24.0f));
                }
            }
        }
    }

    public boolean needDelayRoundProgressDraw() {
        return (this.documentAttachType == 7 || this.documentAttachType == 4) && this.currentMessageObject.type != 5 && MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
    }

    public void drawRoundProgress(Canvas canvas) {
        this.rect.set(((float) this.photoImage.getImageX()) + AndroidUtilities.dpf2(1.5f), ((float) this.photoImage.getImageY()) + AndroidUtilities.dpf2(1.5f), ((float) this.photoImage.getImageX2()) - AndroidUtilities.dpf2(1.5f), ((float) this.photoImage.getImageY2()) - AndroidUtilities.dpf2(1.5f));
        canvas.drawArc(this.rect, -90.0f, this.currentMessageObject.audioProgress * 360.0f, false, Theme.chat_radialProgressPaint);
    }

    private void updatePollAnimations() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.voteLastUpdateTime;
        if (dt > 17) {
            dt = 17;
        }
        this.voteLastUpdateTime = newTime;
        if (this.pollVoteInProgress) {
            this.voteRadOffset += ((float) (360 * dt)) / 2000.0f;
            this.voteRadOffset -= (float) (((int) (this.voteRadOffset / 360.0f)) * 360);
            this.voteCurrentProgressTime += (float) dt;
            if (this.voteCurrentProgressTime >= 500.0f) {
                this.voteCurrentProgressTime = 500.0f;
            }
            if (this.voteRisingCircleLength) {
                this.voteCurrentCircleLength = (266.0f * AndroidUtilities.accelerateInterpolator.getInterpolation(this.voteCurrentProgressTime / 500.0f)) + 4.0f;
            } else {
                this.voteCurrentCircleLength = 4.0f - (((float) (this.firstCircleLength ? 360 : 270)) * (1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(this.voteCurrentProgressTime / 500.0f)));
            }
            if (this.voteCurrentProgressTime == 500.0f) {
                boolean z;
                if (this.voteRisingCircleLength) {
                    this.voteRadOffset += 270.0f;
                    this.voteCurrentCircleLength = -266.0f;
                }
                if (this.voteRisingCircleLength) {
                    z = false;
                } else {
                    z = true;
                }
                this.voteRisingCircleLength = z;
                if (this.firstCircleLength) {
                    this.firstCircleLength = false;
                }
                this.voteCurrentProgressTime = 0.0f;
            }
            invalidate();
        }
        if (this.animatePollAnswer) {
            this.pollAnimationProgressTime += (float) dt;
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
        int i;
        float f;
        int a;
        int startY;
        int linkX;
        int linkPreviewY;
        int x;
        int y;
        int oldAlpha;
        RadialProgress2 radialProgress2;
        String str;
        Drawable menuDrawable;
        if (this.needNewVisiblePart && this.currentMessageObject.type == 0) {
            getLocalVisibleRect(this.scrollRect);
            setVisiblePart(this.scrollRect.top, this.scrollRect.bottom - this.scrollRect.top);
            this.needNewVisiblePart = false;
        }
        this.forceNotDrawTime = this.currentMessagesGroup != null;
        ImageReceiver imageReceiver = this.photoImage;
        int i2 = isDrawSelectedBackground() ? this.currentPosition != null ? 2 : 1 : 0;
        imageReceiver.setPressed(i2);
        imageReceiver = this.photoImage;
        boolean z = (PhotoViewer.isShowingImage(this.currentMessageObject) || SecretMediaViewer.getInstance().isShowingImage(this.currentMessageObject)) ? false : true;
        imageReceiver.setVisible(z, false);
        if (!this.photoImage.getVisible()) {
            this.mediaWasInvisible = true;
            this.timeWasInvisible = true;
            if (this.animatingNoSound == 1) {
                this.animatingNoSoundProgress = 0.0f;
                this.animatingNoSound = 0;
            } else if (this.animatingNoSound == 2) {
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
        boolean imageDrawn = false;
        if (this.currentMessageObject.type == 0) {
            int b;
            if (this.currentMessageObject.isOutOwner()) {
                this.textX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
            } else {
                i = this.currentBackgroundDrawable.getBounds().left;
                f = (this.mediaBackground || !this.drawPinnedBottom) ? 17.0f : 11.0f;
                this.textX = AndroidUtilities.dp(f) + i;
            }
            if (this.hasGamePreview) {
                this.textX += AndroidUtilities.dp(11.0f);
                this.textY = AndroidUtilities.dp(14.0f) + this.namesOffset;
                if (this.siteNameLayout != null) {
                    this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                }
            } else if (this.hasInvoicePreview) {
                this.textY = AndroidUtilities.dp(14.0f) + this.namesOffset;
                if (this.siteNameLayout != null) {
                    this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                }
            } else {
                this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
            }
            this.unmovedTextX = this.textX;
            if (!(this.currentMessageObject.textXOffset == 0.0f || this.replyNameLayout == null)) {
                int diff = (this.backgroundWidth - AndroidUtilities.dp(31.0f)) - this.currentMessageObject.textWidth;
                if (!this.hasNewLineForTime) {
                    diff -= AndroidUtilities.dp((float) ((this.currentMessageObject.isOutOwner() ? 20 : 0) + 4)) + this.timeWidth;
                }
                if (diff > 0) {
                    this.textX += diff;
                }
            }
            if (!(this.currentMessageObject.textLayoutBlocks == null || this.currentMessageObject.textLayoutBlocks.isEmpty())) {
                if (this.fullyDraw) {
                    this.firstVisibleBlockNum = 0;
                    this.lastVisibleBlockNum = this.currentMessageObject.textLayoutBlocks.size();
                }
                if (this.firstVisibleBlockNum >= 0) {
                    a = this.firstVisibleBlockNum;
                    while (a <= this.lastVisibleBlockNum && a < this.currentMessageObject.textLayoutBlocks.size()) {
                        TextLayoutBlock block = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a);
                        canvas.save();
                        canvas.translate((float) (this.textX - (block.isRtl() ? (int) Math.ceil((double) this.currentMessageObject.textXOffset) : 0)), ((float) this.textY) + block.textYOffset);
                        if (this.pressedLink != null && a == this.linkBlockNum) {
                            for (b = 0; b < this.urlPath.size(); b++) {
                                canvas.drawPath((Path) this.urlPath.get(b), Theme.chat_urlPaint);
                            }
                        }
                        if (a == this.linkSelectionBlockNum && !this.urlPathSelection.isEmpty()) {
                            for (b = 0; b < this.urlPathSelection.size(); b++) {
                                canvas.drawPath((Path) this.urlPathSelection.get(b), Theme.chat_textSearchSelectionPaint);
                            }
                        }
                        try {
                            block.textLayout.draw(canvas);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                        canvas.restore();
                        a++;
                    }
                }
            }
            if (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview) {
                int size;
                if (this.hasGamePreview) {
                    startY = AndroidUtilities.dp(14.0f) + this.namesOffset;
                    linkX = this.unmovedTextX - AndroidUtilities.dp(10.0f);
                } else if (this.hasInvoicePreview) {
                    startY = AndroidUtilities.dp(14.0f) + this.namesOffset;
                    linkX = this.unmovedTextX + AndroidUtilities.dp(1.0f);
                } else {
                    startY = (this.textY + this.currentMessageObject.textHeight) + AndroidUtilities.dp(8.0f);
                    linkX = this.unmovedTextX + AndroidUtilities.dp(1.0f);
                }
                linkPreviewY = startY;
                int smallImageStartY = 0;
                if (!this.hasInvoicePreview) {
                    Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outPreviewLine" : "chat_inPreviewLine"));
                    canvas.drawRect((float) linkX, (float) (linkPreviewY - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + linkX), (float) ((this.linkPreviewHeight + linkPreviewY) + AndroidUtilities.dp(3.0f)), Theme.chat_replyLinePaint);
                }
                if (this.siteNameLayout != null) {
                    Theme.chat_replyNamePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outSiteNameText" : "chat_inSiteNameText"));
                    canvas.save();
                    if (this.siteNameRtl) {
                        x = (this.backgroundWidth - this.siteNameWidth) - AndroidUtilities.dp(32.0f);
                    } else if (this.hasInvoicePreview) {
                        x = 0;
                    } else {
                        x = AndroidUtilities.dp(10.0f);
                    }
                    canvas.translate((float) (linkX + x), (float) (linkPreviewY - AndroidUtilities.dp(3.0f)));
                    this.siteNameLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                }
                if ((this.hasGamePreview || this.hasInvoicePreview) && this.currentMessageObject.textHeight != 0) {
                    startY += this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
                    linkPreviewY += this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
                }
                if ((this.drawPhotoImage && this.drawInstantView) || (this.drawInstantViewType == 6 && this.imageBackgroundColor != 0)) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.dp(2.0f);
                    }
                    if (this.imageBackgroundSideColor != 0) {
                        x = linkX + AndroidUtilities.dp(10.0f);
                        this.photoImage.setImageCoords(((this.imageBackgroundSideWidth - this.photoImage.getImageWidth()) / 2) + x, linkPreviewY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                        this.rect.set((float) x, (float) this.photoImage.getImageY(), (float) (this.imageBackgroundSideWidth + x), (float) this.photoImage.getImageY2());
                        Theme.chat_instantViewPaint.setColor(this.imageBackgroundSideColor);
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_instantViewPaint);
                    } else {
                        this.photoImage.setImageCoords(AndroidUtilities.dp(10.0f) + linkX, linkPreviewY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    }
                    if (this.imageBackgroundColor != 0) {
                        Theme.chat_instantViewPaint.setColor(this.imageBackgroundColor);
                        this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) this.photoImage.getImageX2(), (float) this.photoImage.getImageY2());
                        if (this.imageBackgroundSideColor != 0) {
                            canvas.drawRect((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) this.photoImage.getImageX2(), (float) this.photoImage.getImageY2(), Theme.chat_instantViewPaint);
                        } else {
                            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_instantViewPaint);
                        }
                    }
                    if (this.drawPhotoImage && this.drawInstantView) {
                        if (this.drawImageButton) {
                            size = AndroidUtilities.dp(48.0f);
                            this.buttonX = (int) (((float) this.photoImage.getImageX()) + (((float) (this.photoImage.getImageWidth() - size)) / 2.0f));
                            this.buttonY = (int) (((float) this.photoImage.getImageY()) + (((float) (this.photoImage.getImageHeight() - size)) / 2.0f));
                            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                        }
                        imageDrawn = this.photoImage.draw(canvas);
                    }
                    linkPreviewY += this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0f);
                }
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_messageTextOut"));
                    Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
                } else {
                    Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_messageTextIn"));
                    Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
                }
                if (this.titleLayout != null) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.dp(2.0f);
                    }
                    smallImageStartY = linkPreviewY - AndroidUtilities.dp(1.0f);
                    canvas.save();
                    canvas.translate((float) ((AndroidUtilities.dp(10.0f) + linkX) + this.titleX), (float) (linkPreviewY - AndroidUtilities.dp(3.0f)));
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
                }
                if (this.authorLayout != null) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.dp(2.0f);
                    }
                    if (smallImageStartY == 0) {
                        smallImageStartY = linkPreviewY - AndroidUtilities.dp(1.0f);
                    }
                    canvas.save();
                    canvas.translate((float) ((AndroidUtilities.dp(10.0f) + linkX) + this.authorX), (float) (linkPreviewY - AndroidUtilities.dp(3.0f)));
                    this.authorLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
                }
                if (this.descriptionLayout != null) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.dp(2.0f);
                    }
                    if (smallImageStartY == 0) {
                        smallImageStartY = linkPreviewY - AndroidUtilities.dp(1.0f);
                    }
                    this.descriptionY = linkPreviewY - AndroidUtilities.dp(3.0f);
                    canvas.save();
                    canvas.translate((float) (((this.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f)) + linkX) + this.descriptionX), (float) this.descriptionY);
                    if (this.pressedLink != null && this.linkBlockNum == -10) {
                        for (b = 0; b < this.urlPath.size(); b++) {
                            canvas.drawPath((Path) this.urlPath.get(b), Theme.chat_urlPaint);
                        }
                    }
                    this.descriptionLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                }
                if (this.drawPhotoImage && !this.drawInstantView) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.dp(2.0f);
                    }
                    if (this.isSmallImage) {
                        this.photoImage.setImageCoords((this.backgroundWidth + linkX) - AndroidUtilities.dp(81.0f), smallImageStartY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    } else {
                        imageReceiver = this.photoImage;
                        if (this.hasInvoicePreview) {
                            i2 = -AndroidUtilities.dp(6.3f);
                        } else {
                            i2 = AndroidUtilities.dp(10.0f);
                        }
                        imageReceiver.setImageCoords(i2 + linkX, linkPreviewY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                        if (this.drawImageButton) {
                            size = AndroidUtilities.dp(48.0f);
                            this.buttonX = (int) (((float) this.photoImage.getImageX()) + (((float) (this.photoImage.getImageWidth() - size)) / 2.0f));
                            this.buttonY = (int) (((float) this.photoImage.getImageY()) + (((float) (this.photoImage.getImageHeight() - size)) / 2.0f));
                            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                        }
                    }
                    if (this.currentMessageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && MediaController.getInstance().isVideoDrawingReady()) {
                        imageDrawn = true;
                        this.drawTime = true;
                    } else {
                        imageDrawn = this.photoImage.draw(canvas);
                    }
                }
                if (this.documentAttachType == 4) {
                    this.videoButtonX = this.photoImage.getImageX() + AndroidUtilities.dp(8.0f);
                    this.videoButtonY = this.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                    this.videoRadialProgress.setProgressRect(this.videoButtonX, this.videoButtonY, this.videoButtonX + AndroidUtilities.dp(24.0f), this.videoButtonY + AndroidUtilities.dp(24.0f));
                }
                if (this.photosCountLayout != null && this.photoImage.getVisible()) {
                    x = ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.photosCountWidth;
                    y = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f);
                    this.rect.set((float) (x - AndroidUtilities.dp(4.0f)), (float) (y - AndroidUtilities.dp(1.5f)), (float) ((this.photosCountWidth + x) + AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(14.5f) + y));
                    oldAlpha = Theme.chat_timeBackgroundPaint.getAlpha();
                    Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                    Theme.chat_durationPaint.setAlpha((int) (255.0f * this.controlsAlpha));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                    Theme.chat_timeBackgroundPaint.setAlpha(oldAlpha);
                    canvas.save();
                    canvas.translate((float) x, (float) y);
                    this.photosCountLayout.draw(canvas);
                    canvas.restore();
                    Theme.chat_durationPaint.setAlpha(255);
                }
                if (this.videoInfoLayout != null && ((!this.drawPhotoImage || this.photoImage.getVisible()) && this.imageBackgroundSideColor == 0)) {
                    if (!this.hasGamePreview && !this.hasInvoicePreview && this.documentAttachType != 8) {
                        x = ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.durationWidth;
                        y = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f);
                        this.rect.set((float) (x - AndroidUtilities.dp(4.0f)), (float) (y - AndroidUtilities.dp(1.5f)), (float) ((this.durationWidth + x) + AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(14.5f) + y));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                    } else if (this.drawPhotoImage) {
                        x = this.photoImage.getImageX() + AndroidUtilities.dp(8.5f);
                        y = this.photoImage.getImageY() + AndroidUtilities.dp(6.0f);
                        this.rect.set((float) (x - AndroidUtilities.dp(4.0f)), (float) (y - AndroidUtilities.dp(1.5f)), (float) ((this.durationWidth + x) + AndroidUtilities.dp(4.0f)), (float) (y + AndroidUtilities.dp(this.documentAttachType == 8 ? 14.5f : 16.5f)));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                    } else {
                        x = linkX;
                        y = linkPreviewY;
                    }
                    canvas.save();
                    canvas.translate((float) x, (float) y);
                    if (this.hasInvoicePreview) {
                        if (this.drawPhotoImage) {
                            Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_previewGameText"));
                        } else if (this.currentMessageObject.isOutOwner()) {
                            Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_messageTextOut"));
                        } else {
                            Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_messageTextIn"));
                        }
                    }
                    this.videoInfoLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.drawInstantView) {
                    Drawable instantDrawable;
                    int instantY = (this.linkPreviewHeight + startY) + AndroidUtilities.dp(10.0f);
                    Paint backPaint = Theme.chat_instantViewRectPaint;
                    if (this.currentMessageObject.isOutOwner()) {
                        instantDrawable = Theme.chat_msgOutInstantDrawable;
                        Theme.chat_instantViewPaint.setColor(Theme.getColor("chat_outPreviewInstantText"));
                        backPaint.setColor(Theme.getColor("chat_outPreviewInstantText"));
                    } else {
                        instantDrawable = Theme.chat_msgInInstantDrawable;
                        Theme.chat_instantViewPaint.setColor(Theme.getColor("chat_inPreviewInstantText"));
                        backPaint.setColor(Theme.getColor("chat_inPreviewInstantText"));
                    }
                    if (VERSION.SDK_INT >= 21) {
                        this.selectorDrawable.setBounds(linkX, instantY, this.instantWidth + linkX, AndroidUtilities.dp(36.0f) + instantY);
                        this.selectorDrawable.draw(canvas);
                    }
                    this.rect.set((float) linkX, (float) instantY, (float) (this.instantWidth + linkX), (float) (AndroidUtilities.dp(36.0f) + instantY));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), backPaint);
                    if (this.drawInstantViewType == 0) {
                        BaseCell.setDrawableBounds(instantDrawable, ((this.instantTextLeftX + this.instantTextX) + linkX) - AndroidUtilities.dp(15.0f), AndroidUtilities.dp(11.5f) + instantY, AndroidUtilities.dp(9.0f), AndroidUtilities.dp(13.0f));
                        instantDrawable.draw(canvas);
                    }
                    if (this.instantViewLayout != null) {
                        canvas.save();
                        canvas.translate((float) (this.instantTextX + linkX), (float) (AndroidUtilities.dp(10.5f) + instantY));
                        this.instantViewLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            }
            this.drawTime = true;
        } else if (this.drawPhotoImage) {
            if (this.currentMessageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && MediaController.getInstance().isVideoDrawingReady()) {
                imageDrawn = true;
                this.drawTime = true;
            } else {
                if (this.currentMessageObject.type == 5 && Theme.chat_roundVideoShadow != null) {
                    x = this.photoImage.getImageX() - AndroidUtilities.dp(3.0f);
                    y = this.photoImage.getImageY() - AndroidUtilities.dp(2.0f);
                    Theme.chat_roundVideoShadow.setAlpha(255);
                    Theme.chat_roundVideoShadow.setBounds(x, y, (AndroidUtilities.roundMessageSize + x) + AndroidUtilities.dp(6.0f), (AndroidUtilities.roundMessageSize + y) + AndroidUtilities.dp(6.0f));
                    Theme.chat_roundVideoShadow.draw(canvas);
                    if (!(this.photoImage.hasBitmapImage() && this.photoImage.getCurrentAlpha() == 1.0f)) {
                        Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outBubble" : "chat_inBubble"));
                        canvas.drawCircle(this.photoImage.getCenterX(), this.photoImage.getCenterY(), (float) (this.photoImage.getImageWidth() / 2), Theme.chat_docBackPaint);
                    }
                }
                imageDrawn = this.photoImage.draw(canvas);
                boolean drawTimeOld = this.drawTime;
                this.drawTime = this.photoImage.getVisible();
                if (!(this.currentPosition == null || drawTimeOld == this.drawTime)) {
                    ViewGroup viewGroup = (ViewGroup) getParent();
                    if (viewGroup != null) {
                        if (this.currentPosition.last) {
                            viewGroup.invalidate();
                        } else {
                            int count = viewGroup.getChildCount();
                            for (a = 0; a < count; a++) {
                                View child = viewGroup.getChildAt(a);
                                if (child != this && (child instanceof ChatMessageCell)) {
                                    ChatMessageCell cell = (ChatMessageCell) child;
                                    if (cell.getCurrentMessagesGroup() == this.currentMessagesGroup) {
                                        GroupedMessagePosition position = cell.getCurrentPosition();
                                        if (position.last && position.maxY == this.currentPosition.maxY && (cell.timeX - AndroidUtilities.dp(4.0f)) + cell.getLeft() < getRight()) {
                                            cell.groupPhotoInvisible = !this.drawTime;
                                            cell.invalidate();
                                            viewGroup.invalidate();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.documentAttachType == 2 || this.currentMessageObject.type == 8) {
            if (!(!this.photoImage.getVisible() || this.hasGamePreview || this.currentMessageObject.needDrawBluredPreview())) {
                oldAlpha = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                Drawable drawable = Theme.chat_msgMediaMenuDrawable;
                i = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                this.otherX = i;
                int imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                this.otherY = imageY;
                BaseCell.setDrawableBounds(drawable, i, imageY);
                Theme.chat_msgMediaMenuDrawable.draw(canvas);
                Theme.chat_msgMediaMenuDrawable.setAlpha(oldAlpha);
            }
        } else if (this.documentAttachType == 7 || this.currentMessageObject.type == 5) {
            if (this.durationLayout != null) {
                int x1;
                int y1;
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (playing && this.currentMessageObject.type == 5) {
                    drawRoundProgress(canvas);
                }
                if (this.documentAttachType == 7) {
                    i = this.backgroundDrawableLeft;
                    f = (this.currentMessageObject.isOutOwner() || this.drawPinnedBottom) ? 12.0f : 18.0f;
                    x1 = i + AndroidUtilities.dp(f);
                    i = this.layoutHeight;
                    if (this.drawPinnedBottom) {
                        i2 = 2;
                    } else {
                        i2 = 0;
                    }
                    y1 = (i - AndroidUtilities.dp(6.3f - ((float) i2))) - this.timeLayout.getHeight();
                } else {
                    x1 = this.backgroundDrawableLeft + AndroidUtilities.dp(8.0f);
                    y1 = this.layoutHeight - AndroidUtilities.dp((float) (28 - (this.drawPinnedBottom ? 2 : 0)));
                    this.rect.set((float) x1, (float) y1, (float) ((this.timeWidthAudio + x1) + AndroidUtilities.dp(22.0f)), (float) (AndroidUtilities.dp(17.0f) + y1));
                    oldAlpha = Theme.chat_actionBackgroundPaint.getAlpha();
                    Theme.chat_actionBackgroundPaint.setAlpha((int) (((float) oldAlpha) * this.timeAlpha));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_actionBackgroundPaint);
                    Theme.chat_actionBackgroundPaint.setAlpha(oldAlpha);
                    if (playing || !this.currentMessageObject.isContentUnread()) {
                        if (!playing || MediaController.getInstance().isMessagePaused()) {
                            this.roundVideoPlayingDrawable.stop();
                        } else {
                            this.roundVideoPlayingDrawable.start();
                        }
                        BaseCell.setDrawableBounds(this.roundVideoPlayingDrawable, (this.timeWidthAudio + x1) + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(2.3f) + y1);
                        this.roundVideoPlayingDrawable.draw(canvas);
                    } else {
                        Theme.chat_docBackPaint.setColor(Theme.getColor("chat_mediaTimeText"));
                        Theme.chat_docBackPaint.setAlpha((int) (255.0f * this.timeAlpha));
                        canvas.drawCircle((float) ((this.timeWidthAudio + x1) + AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(8.3f) + y1), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                    }
                    x1 += AndroidUtilities.dp(4.0f);
                    y1 += AndroidUtilities.dp(1.7f);
                }
                Theme.chat_timePaint.setAlpha((int) (255.0f * this.timeAlpha));
                canvas.save();
                canvas.translate((float) x1, (float) y1);
                this.durationLayout.draw(canvas);
                canvas.restore();
                Theme.chat_timePaint.setAlpha(255);
            }
        } else if (this.documentAttachType == 5) {
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_outAudioTitleText"));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outAudioPerfomerSelectedText" : "chat_outAudioPerfomerText"));
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outAudioDurationSelectedText" : "chat_outAudioDurationText"));
                radialProgress2 = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? "chat_outAudioSelectedProgress" : "chat_outAudioProgress";
                radialProgress2.setProgressColor(Theme.getColor(str));
            } else {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_inAudioTitleText"));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inAudioPerfomerSelectedText" : "chat_inAudioPerfomerText"));
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inAudioDurationSelectedText" : "chat_inAudioDurationText"));
                radialProgress2 = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress";
                radialProgress2.setProgressColor(Theme.getColor(str));
            }
            this.radialProgress.draw(canvas);
            canvas.save();
            canvas.translate((float) (this.timeAudioX + this.songX), (float) ((AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY));
            this.songLayout.draw(canvas);
            canvas.restore();
            canvas.save();
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
            } else {
                canvas.translate((float) (this.timeAudioX + this.performerX), (float) ((AndroidUtilities.dp(35.0f) + this.namesOffset) + this.mediaOffsetY));
                this.performerLayout.draw(canvas);
            }
            canvas.restore();
            canvas.save();
            canvas.translate((float) this.timeAudioX, (float) ((AndroidUtilities.dp(57.0f) + this.namesOffset) + this.mediaOffsetY));
            this.durationLayout.draw(canvas);
            canvas.restore();
            menuDrawable = this.currentMessageObject.isOutOwner() ? isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable : isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            i2 = (this.backgroundWidth + this.buttonX) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
            this.otherX = i2;
            i = this.buttonY - AndroidUtilities.dp(5.0f);
            this.otherY = i;
            BaseCell.setDrawableBounds(menuDrawable, i2, i);
            menuDrawable.draw(canvas);
        } else if (this.documentAttachType == 3) {
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outAudioDurationSelectedText" : "chat_outAudioDurationText"));
                radialProgress2 = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? "chat_outAudioSelectedProgress" : "chat_outAudioProgress";
                radialProgress2.setProgressColor(Theme.getColor(str));
            } else {
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inAudioDurationSelectedText" : "chat_inAudioDurationText"));
                radialProgress2 = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress";
                radialProgress2.setProgressColor(Theme.getColor(str));
            }
            this.radialProgress.draw(canvas);
            canvas.save();
            if (this.useSeekBarWaweform) {
                canvas.translate((float) (this.seekBarX + AndroidUtilities.dp(13.0f)), (float) this.seekBarY);
                this.seekBarWaveform.draw(canvas);
            } else {
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
            }
            canvas.restore();
            canvas.save();
            canvas.translate((float) this.timeAudioX, (float) ((AndroidUtilities.dp(44.0f) + this.namesOffset) + this.mediaOffsetY));
            this.durationLayout.draw(canvas);
            canvas.restore();
            if (this.currentMessageObject.type != 0 && this.currentMessageObject.isContentUnread()) {
                Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outVoiceSeekbarFill" : "chat_inVoiceSeekbarFill"));
                canvas.drawCircle((float) ((this.timeAudioX + this.timeWidthAudio) + AndroidUtilities.dp(6.0f)), (float) ((AndroidUtilities.dp(51.0f) + this.namesOffset) + this.mediaOffsetY), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
            }
        }
        if (this.captionLayout != null) {
            if (this.currentMessageObject.type == 1 || this.documentAttachType == 4 || this.currentMessageObject.type == 8) {
                this.captionX = (this.photoImage.getImageX() + AndroidUtilities.dp(5.0f)) + this.captionOffsetX;
                this.captionY = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) + AndroidUtilities.dp(6.0f);
            } else if (this.hasOldCaptionPreview) {
                this.captionX = (AndroidUtilities.dp(this.currentMessageObject.isOutOwner() ? 11.0f : 17.0f) + this.backgroundDrawableLeft) + this.captionOffsetX;
                this.captionY = (((this.totalHeight - this.captionHeight) - AndroidUtilities.dp(this.drawPinnedTop ? 9.0f : 10.0f)) - this.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
            } else {
                i = this.backgroundDrawableLeft;
                f = (this.currentMessageObject.isOutOwner() || this.mediaBackground || (!this.mediaBackground && this.drawPinnedBottom)) ? 11.0f : 17.0f;
                this.captionX = (AndroidUtilities.dp(f) + i) + this.captionOffsetX;
                this.captionY = (this.totalHeight - this.captionHeight) - AndroidUtilities.dp(this.drawPinnedTop ? 9.0f : 10.0f);
            }
        }
        if (this.currentPosition == null) {
            drawCaptionLayout(canvas, false);
        }
        if (this.hasOldCaptionPreview) {
            if (this.currentMessageObject.type == 1 || this.documentAttachType == 4 || this.currentMessageObject.type == 8) {
                linkX = this.photoImage.getImageX() + AndroidUtilities.dp(5.0f);
            } else {
                linkX = this.backgroundDrawableLeft + AndroidUtilities.dp(this.currentMessageObject.isOutOwner() ? 11.0f : 17.0f);
            }
            startY = ((this.totalHeight - AndroidUtilities.dp(this.drawPinnedTop ? 9.0f : 10.0f)) - this.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
            linkPreviewY = startY;
            Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outPreviewLine" : "chat_inPreviewLine"));
            canvas.drawRect((float) linkX, (float) (linkPreviewY - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + linkX), (float) (this.linkPreviewHeight + linkPreviewY), Theme.chat_replyLinePaint);
            if (this.siteNameLayout != null) {
                Theme.chat_replyNamePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outSiteNameText" : "chat_inSiteNameText"));
                canvas.save();
                if (this.siteNameRtl) {
                    x = (this.backgroundWidth - this.siteNameWidth) - AndroidUtilities.dp(32.0f);
                } else if (this.hasInvoicePreview) {
                    x = 0;
                } else {
                    x = AndroidUtilities.dp(10.0f);
                }
                canvas.translate((float) (linkX + x), (float) (linkPreviewY - AndroidUtilities.dp(3.0f)));
                this.siteNameLayout.draw(canvas);
                canvas.restore();
                linkPreviewY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
            }
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
            } else {
                Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
            }
            if (this.descriptionLayout != null) {
                if (linkPreviewY != startY) {
                    linkPreviewY += AndroidUtilities.dp(2.0f);
                }
                this.descriptionY = linkPreviewY - AndroidUtilities.dp(3.0f);
                canvas.save();
                canvas.translate((float) ((AndroidUtilities.dp(10.0f) + linkX) + this.descriptionX), (float) this.descriptionY);
                this.descriptionLayout.draw(canvas);
                canvas.restore();
            }
            this.drawTime = true;
        }
        if (this.documentAttachType == 1) {
            int titleY;
            int subtitleY;
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_docNamePaint.setColor(Theme.getColor("chat_outFileNameText"));
                Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outFileInfoSelectedText" : "chat_outFileInfoText"));
                Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outFileBackgroundSelected" : "chat_outFileBackground"));
                menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
            } else {
                Theme.chat_docNamePaint.setColor(Theme.getColor("chat_inFileNameText"));
                Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inFileInfoSelectedText" : "chat_inFileInfoText"));
                Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inFileBackgroundSelected" : "chat_inFileBackground"));
                menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            }
            if (this.drawPhotoImage) {
                if (this.currentMessageObject.type == 0) {
                    i2 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(56.0f);
                    this.otherX = i2;
                    i = this.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                    this.otherY = i;
                    BaseCell.setDrawableBounds(menuDrawable, i2, i);
                } else {
                    i2 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(40.0f);
                    this.otherX = i2;
                    i = this.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                    this.otherY = i;
                    BaseCell.setDrawableBounds(menuDrawable, i2, i);
                }
                x = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
                titleY = this.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                subtitleY = this.photoImage.getImageY() + (this.docTitleLayout != null ? this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1) + AndroidUtilities.dp(13.0f) : AndroidUtilities.dp(8.0f));
                if (imageDrawn) {
                    this.radialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
                    this.radialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
                    this.videoRadialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
                    this.videoRadialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
                    if (this.buttonState == -1 && this.radialProgress.getIcon() != 4) {
                        this.radialProgress.setIcon(4, true, true);
                    }
                } else {
                    if (this.currentMessageObject.isOutOwner()) {
                        this.radialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
                        this.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outFileProgressSelected" : "chat_outFileProgress"));
                        this.videoRadialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
                        this.videoRadialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outFileProgressSelected" : "chat_outFileProgress"));
                    } else {
                        this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
                        this.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inFileProgressSelected" : "chat_inFileProgress"));
                        this.videoRadialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
                        this.videoRadialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inFileProgressSelected" : "chat_inFileProgress"));
                    }
                    this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) (this.photoImage.getImageX() + this.photoImage.getImageWidth()), (float) (this.photoImage.getImageY() + this.photoImage.getImageHeight()));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                }
            } else {
                i2 = (this.backgroundWidth + this.buttonX) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
                this.otherX = i2;
                i = this.buttonY - AndroidUtilities.dp(5.0f);
                this.otherY = i;
                BaseCell.setDrawableBounds(menuDrawable, i2, i);
                x = this.buttonX + AndroidUtilities.dp(53.0f);
                titleY = this.buttonY + AndroidUtilities.dp(4.0f);
                subtitleY = this.buttonY + AndroidUtilities.dp(27.0f);
                if (this.docTitleLayout != null && this.docTitleLayout.getLineCount() > 1) {
                    subtitleY += ((this.docTitleLayout.getLineCount() - 1) * AndroidUtilities.dp(16.0f)) + AndroidUtilities.dp(2.0f);
                }
                if (this.currentMessageObject.isOutOwner()) {
                    radialProgress2 = this.radialProgress;
                    str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? "chat_outAudioSelectedProgress" : "chat_outAudioProgress";
                    radialProgress2.setProgressColor(Theme.getColor(str));
                    radialProgress2 = this.videoRadialProgress;
                    if (isDrawSelectedBackground() || this.videoButtonPressed != 0) {
                        str = "chat_outAudioSelectedProgress";
                    } else {
                        str = "chat_outAudioProgress";
                    }
                    radialProgress2.setProgressColor(Theme.getColor(str));
                } else {
                    radialProgress2 = this.radialProgress;
                    str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress";
                    radialProgress2.setProgressColor(Theme.getColor(str));
                    radialProgress2 = this.videoRadialProgress;
                    str = (isDrawSelectedBackground() || this.videoButtonPressed != 0) ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress";
                    radialProgress2.setProgressColor(Theme.getColor(str));
                }
            }
            menuDrawable.draw(canvas);
            try {
                if (this.docTitleLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.docTitleOffsetX + x), (float) titleY);
                    this.docTitleLayout.draw(canvas);
                    canvas.restore();
                }
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
            try {
                if (this.infoLayout != null) {
                    canvas.save();
                    canvas.translate((float) x, (float) subtitleY);
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            } catch (Throwable e22) {
                FileLog.e(e22);
            }
        }
        if (this.buttonState == -1 && this.currentMessageObject.needDrawBluredPreview() && !MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && this.photoImage.getVisible() && this.currentMessageObject.messageOwner.destroyTime != 0) {
            if (!this.currentMessageObject.isOutOwner()) {
                float progress = ((float) Math.max(0, (((long) this.currentMessageObject.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(this.currentAccount).getTimeDifference() * 1000))))) / (((float) this.currentMessageObject.messageOwner.ttl) * 1000.0f);
                Theme.chat_deleteProgressPaint.setAlpha((int) (255.0f * this.controlsAlpha));
                canvas.drawArc(this.deleteProgressRect, -90.0f, -360.0f * progress, true, Theme.chat_deleteProgressPaint);
                if (progress != 0.0f) {
                    int offset = AndroidUtilities.dp(2.0f);
                    invalidate(((int) this.deleteProgressRect.left) - offset, ((int) this.deleteProgressRect.top) - offset, ((int) this.deleteProgressRect.right) + (offset * 2), ((int) this.deleteProgressRect.bottom) + (offset * 2));
                }
            }
            updateSecretTimeText(this.currentMessageObject);
        }
        if (this.currentMessageObject.type == 4 && !(this.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) && this.currentMapProvider == 2 && this.photoImage.hasNotThumb()) {
            int w = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicWidth()) * 0.8f);
            int h = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicHeight()) * 0.8f);
            x = this.photoImage.getImageX() + ((this.photoImage.getImageWidth() - w) / 2);
            y = this.photoImage.getImageY() + ((this.photoImage.getImageHeight() / 2) - h);
            Theme.chat_redLocationIcon.setAlpha((int) (255.0f * this.photoImage.getCurrentAlpha()));
            Theme.chat_redLocationIcon.setBounds(x, y, x + w, y + h);
            Theme.chat_redLocationIcon.draw(canvas);
        }
        if (!this.botButtons.isEmpty()) {
            int addX;
            if (this.currentMessageObject.isOutOwner()) {
                addX = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                addX = this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
            }
            a = 0;
            while (a < this.botButtons.size()) {
                BotButton button = (BotButton) this.botButtons.get(a);
                y = (button.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                Theme.chat_systemDrawable.setColorFilter(a == this.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(button.x + addX, y, (button.x + addX) + button.width, button.height + y);
                Theme.chat_systemDrawable.draw(canvas);
                canvas.save();
                canvas.translate((float) ((button.x + addX) + AndroidUtilities.dp(5.0f)), (float) (((AndroidUtilities.dp(44.0f) - button.title.getLineBottom(button.title.getLineCount() - 1)) / 2) + y));
                button.title.draw(canvas);
                canvas.restore();
                if (button.button instanceof TL_keyboardButtonUrl) {
                    BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((button.x + button.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y);
                    Theme.chat_botLinkDrawalbe.draw(canvas);
                } else if (button.button instanceof TL_keyboardButtonSwitchInline) {
                    BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((button.x + button.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y);
                    Theme.chat_botInlineDrawable.draw(canvas);
                } else if ((button.button instanceof TL_keyboardButtonCallback) || (button.button instanceof TL_keyboardButtonRequestGeoLocation) || (button.button instanceof TL_keyboardButtonGame) || (button.button instanceof TL_keyboardButtonBuy)) {
                    boolean drawProgress = (((button.button instanceof TL_keyboardButtonCallback) || (button.button instanceof TL_keyboardButtonGame) || (button.button instanceof TL_keyboardButtonBuy)) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCallback(this.currentMessageObject, button.button)) || ((button.button instanceof TL_keyboardButtonRequestGeoLocation) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCurrentLocation(this.currentMessageObject, button.button));
                    if (drawProgress || !(drawProgress || button.progressAlpha == 0.0f)) {
                        Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (button.progressAlpha * 255.0f)));
                        x = ((button.x + button.width) - AndroidUtilities.dp(12.0f)) + addX;
                        this.rect.set((float) x, (float) (AndroidUtilities.dp(4.0f) + y), (float) (AndroidUtilities.dp(8.0f) + x), (float) (AndroidUtilities.dp(12.0f) + y));
                        canvas.drawArc(this.rect, (float) button.angle, 220.0f, false, Theme.chat_botProgressPaint);
                        invalidate(((int) this.rect.left) - AndroidUtilities.dp(2.0f), ((int) this.rect.top) - AndroidUtilities.dp(2.0f), ((int) this.rect.right) + AndroidUtilities.dp(2.0f), ((int) this.rect.bottom) + AndroidUtilities.dp(2.0f));
                        long newTime = System.currentTimeMillis();
                        if (Math.abs(button.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                            long delta = newTime - button.lastUpdateTime;
                            button.angle = (int) (((float) button.angle) + (((float) (360 * delta)) / 2000.0f));
                            button.angle = button.angle - ((button.angle / 360) * 360);
                            if (drawProgress) {
                                if (button.progressAlpha < 1.0f) {
                                    button.progressAlpha = button.progressAlpha + (((float) delta) / 200.0f);
                                    if (button.progressAlpha > 1.0f) {
                                        button.progressAlpha = 1.0f;
                                    }
                                }
                            } else if (button.progressAlpha > 0.0f) {
                                button.progressAlpha = button.progressAlpha - (((float) delta) / 200.0f);
                                if (button.progressAlpha < 0.0f) {
                                    button.progressAlpha = 0.0f;
                                }
                            }
                        }
                        button.lastUpdateTime = newTime;
                    }
                }
                a++;
            }
        }
    }

    private int getMiniIconForCurrentState() {
        if (this.miniButtonState < 0) {
            return 4;
        }
        if (this.miniButtonState == 0) {
            return 2;
        }
        return 3;
    }

    private int getIconForCurrentState() {
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            if (this.currentMessageObject.isOutOwner()) {
                this.radialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
            } else {
                this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
            }
            if (this.buttonState == 1) {
                return 1;
            }
            if (this.buttonState == 2) {
                return 2;
            }
            if (this.buttonState == 4) {
                return 3;
            }
            return 0;
        } else if (this.documentAttachType != 1 || this.drawPhotoImage) {
            this.radialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
            this.videoRadialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
            if (this.buttonState < 0 || this.buttonState >= 4) {
                if (this.buttonState != -1) {
                    return 4;
                }
                if (this.documentAttachType == 1) {
                    if (!this.drawPhotoImage || ((this.currentPhotoObject == null && this.currentPhotoObjectThumb == null) || (!this.photoImage.hasBitmapImage() && !this.currentMessageObject.mediaExists && !this.currentMessageObject.attachPathExists))) {
                        return 5;
                    }
                    return 4;
                } else if (this.currentMessageObject.needDrawBluredPreview()) {
                    if (this.currentMessageObject.messageOwner.destroyTime == 0) {
                        return 7;
                    }
                    if (this.currentMessageObject.isOutOwner()) {
                        return 9;
                    }
                    return 11;
                } else if (this.hasEmbed) {
                    return 0;
                } else {
                    return 4;
                }
            } else if (this.buttonState == 0) {
                return 2;
            } else {
                if (this.buttonState == 1) {
                    return 3;
                }
                if (this.buttonState == 2) {
                    return 8;
                }
                if (this.buttonState != 3 || this.autoPlayingVideo) {
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
            if (this.buttonState == -1) {
                return 5;
            }
            if (this.buttonState == 0) {
                return 2;
            }
            if (this.buttonState == 1) {
                return 3;
            }
            return 4;
        }
    }

    private int getMaxNameWidth() {
        if (this.documentAttachType == 6 || this.documentAttachType == 8 || this.currentMessageObject.type == 5) {
            int maxWidth;
            if (AndroidUtilities.isTablet()) {
                if (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.needDrawAvatar()) {
                    maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(42.0f);
                } else {
                    maxWidth = AndroidUtilities.getMinTabletSide();
                }
            } else if (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.needDrawAvatar()) {
                maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(42.0f);
            } else {
                maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            }
            return (maxWidth - this.backgroundWidth) - AndroidUtilities.dp(57.0f);
        } else if (this.currentMessagesGroup != null) {
            int dWidth;
            if (AndroidUtilities.isTablet()) {
                dWidth = AndroidUtilities.getMinTabletSide();
            } else {
                dWidth = AndroidUtilities.displaySize.x;
            }
            int firstLineWidth = 0;
            for (int a = 0; a < this.currentMessagesGroup.posArray.size(); a++) {
                GroupedMessagePosition position = (GroupedMessagePosition) this.currentMessagesGroup.posArray.get(a);
                if (position.minY != (byte) 0) {
                    break;
                }
                firstLineWidth = (int) (((double) firstLineWidth) + Math.ceil((double) ((((float) (position.pw + position.leftSpanOffset)) / 1000.0f) * ((float) dWidth))));
            }
            return firstLineWidth - AndroidUtilities.dp((float) ((this.isAvatarVisible ? 48 : 0) + 31));
        } else {
            return this.backgroundWidth - AndroidUtilities.dp(this.mediaBackground ? 22.0f : 31.0f);
        }
    }

    public void updateButtonState(boolean ifSame, boolean animated, boolean fromSet) {
        if (animated && (PhotoViewer.isShowingImage(this.currentMessageObject) || !this.attachedToWindow)) {
            animated = false;
        }
        this.drawRadialCheckBackground = false;
        String fileName = null;
        boolean fileExists = false;
        if (this.currentMessageObject.type == 1) {
            if (this.currentPhotoObject != null) {
                fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                fileExists = this.currentMessageObject.mediaExists;
            } else {
                return;
            }
        } else if (this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5 || this.documentAttachType == 7 || this.documentAttachType == 4 || this.documentAttachType == 8 || this.currentMessageObject.type == 9 || this.documentAttachType == 3 || this.documentAttachType == 5) {
            if (this.currentMessageObject.useCustomPhoto) {
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                return;
            } else if (this.currentMessageObject.attachPathExists && !TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                fileName = this.currentMessageObject.messageOwner.attachPath;
                fileExists = true;
            } else if (!this.currentMessageObject.isSendError() || this.documentAttachType == 3 || this.documentAttachType == 5) {
                fileName = this.currentMessageObject.getFileName();
                fileExists = this.currentMessageObject.mediaExists;
            }
        } else if (this.documentAttachType != 0) {
            fileName = FileLoader.getAttachFileName(this.documentAttach);
            fileExists = this.currentMessageObject.mediaExists;
        } else if (this.currentPhotoObject != null) {
            fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
            fileExists = this.currentMessageObject.mediaExists;
        }
        boolean z = this.currentMessageObject.isSent() && this.documentAttachType == 4 && this.currentMessageObject.canStreamVideo() && !this.currentMessageObject.needDrawBluredPreview();
        this.canStreamVideo = z;
        if (SharedConfig.streamMedia && ((int) this.currentMessageObject.getDialogId()) != 0 && !this.currentMessageObject.isSecretMedia() && (this.documentAttachType == 5 || (this.canStreamVideo && this.currentPosition != null && ((this.currentPosition.flags & 1) == 0 || (this.currentPosition.flags & 2) == 0)))) {
            this.hasMiniProgress = fileExists ? 1 : 2;
            fileExists = true;
        }
        if (this.currentMessageObject.isSendError() || !(!TextUtils.isEmpty(fileName) || this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) {
            this.radialProgress.setIcon(4, ifSame, false);
            this.radialProgress.setMiniIcon(4, ifSame, false);
            this.videoRadialProgress.setIcon(4, ifSame, false);
            this.videoRadialProgress.setMiniIcon(4, ifSame, false);
            return;
        }
        boolean fromBot = this.currentMessageObject.messageOwner.params != null && this.currentMessageObject.messageOwner.params.containsKey("query_id");
        Float progress;
        RadialProgress2 radialProgress2;
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            boolean playing;
            if ((this.currentMessageObject.isOut() && (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) || (this.currentMessageObject.isSendError() && fromBot)) {
                if (TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                    this.buttonState = -1;
                    getIconForCurrentState();
                    this.radialProgress.setIcon(12, ifSame, false);
                    this.radialProgress.setProgress(0.0f, false);
                } else {
                    DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
                    this.buttonState = 4;
                    this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                    if (fromBot) {
                        this.radialProgress.setProgress(0.0f, false);
                    } else {
                        float floatValue;
                        progress = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
                        if (progress == null && SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId())) {
                            progress = Float.valueOf(1.0f);
                        }
                        radialProgress2 = this.radialProgress;
                        if (progress != null) {
                            floatValue = progress.floatValue();
                        } else {
                            floatValue = 0.0f;
                        }
                        radialProgress2.setProgress(floatValue, false);
                    }
                }
            } else if (this.hasMiniProgress != 0) {
                this.radialProgress.setMiniProgressBackgroundColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outLoader" : "chat_inLoader"));
                playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                if (this.hasMiniProgress == 1) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                    this.miniButtonState = -1;
                } else {
                    DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                    if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                        this.miniButtonState = 1;
                        progress = ImageLoader.getInstance().getFileProgress(fileName);
                        if (progress != null) {
                            this.radialProgress.setProgress(progress.floatValue(), animated);
                        } else {
                            this.radialProgress.setProgress(0.0f, animated);
                        }
                    } else {
                        this.miniButtonState = 0;
                    }
                }
                this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), ifSame, animated);
            } else if (fileExists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 4;
                    progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                } else {
                    this.buttonState = 2;
                    this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                }
            }
            updatePlayingMessageProgress();
        } else if (this.currentMessageObject.type != 0 || this.documentAttachType == 1 || this.documentAttachType == 4 || this.documentAttachType == 8) {
            if (this.currentMessageObject.isOut() && (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) {
                if (TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                    this.buttonState = -1;
                    getIconForCurrentState();
                    radialProgress2 = this.radialProgress;
                    int i = (this.currentMessageObject.isSticker() || this.currentMessageObject.isLocation()) ? 4 : 12;
                    radialProgress2.setIcon(i, ifSame, false);
                    this.radialProgress.setProgress(0.0f, false);
                } else {
                    DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
                    boolean needProgress = this.currentMessageObject.messageOwner.attachPath == null || !this.currentMessageObject.messageOwner.attachPath.startsWith("http");
                    HashMap<String, String> params = this.currentMessageObject.messageOwner.params;
                    if (this.currentMessageObject.messageOwner.message == null || params == null || !(params.containsKey("url") || params.containsKey("bot"))) {
                        this.buttonState = 1;
                    } else {
                        needProgress = false;
                        this.buttonState = -1;
                    }
                    boolean sending = SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId());
                    if (this.currentPosition != null && sending && this.buttonState == 1) {
                        this.drawRadialCheckBackground = true;
                        getIconForCurrentState();
                        this.radialProgress.setIcon(6, ifSame, animated);
                    } else {
                        this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                    }
                    if (needProgress) {
                        progress = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
                        if (progress == null && sending) {
                            progress = Float.valueOf(1.0f);
                        }
                        this.radialProgress.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
                    } else {
                        this.radialProgress.setProgress(0.0f, false);
                    }
                    invalidate();
                }
                this.videoRadialProgress.setIcon(4, ifSame, false);
            } else {
                if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                }
                boolean playingCurrentMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                boolean isLoadingVideo = false;
                if (this.documentAttachType == 4 && this.autoPlayingVideo) {
                    isLoadingVideo = FileLoader.getInstance(this.currentAccount).isLoadingVideo(this.documentAttach, playingCurrentMessage);
                    AnimatedFileDrawable animation = this.photoImage.getAnimation();
                    if (animation != null) {
                        if (!this.currentMessageObject.hadAnimationNotReadyLoading) {
                            MessageObject messageObject = this.currentMessageObject;
                            z = isLoadingVideo && !animation.hasBitmap();
                            messageObject.hadAnimationNotReadyLoading = z;
                        } else if (animation.hasBitmap()) {
                            this.currentMessageObject.hadAnimationNotReadyLoading = false;
                        }
                    }
                }
                if (this.hasMiniProgress != 0) {
                    this.radialProgress.setMiniProgressBackgroundColor(Theme.getColor("chat_inLoaderPhoto"));
                    this.buttonState = 3;
                    this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                    if (this.hasMiniProgress == 1) {
                        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                        this.miniButtonState = -1;
                    } else {
                        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                        if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                            this.miniButtonState = 1;
                            progress = ImageLoader.getInstance().getFileProgress(fileName);
                            if (progress != null) {
                                this.radialProgress.setProgress(progress.floatValue(), animated);
                            } else {
                                this.radialProgress.setProgress(0.0f, animated);
                            }
                        } else {
                            this.miniButtonState = 0;
                        }
                    }
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), ifSame, animated);
                } else if (fileExists || (this.documentAttachType == 4 && this.autoPlayingVideo && !this.currentMessageObject.hadAnimationNotReadyLoading && !isLoadingVideo)) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                    if (this.drawVideoImageButton && animated) {
                        if (this.animatingDrawVideoImageButton != 1 && this.animatingDrawVideoImageButtonProgress > 0.0f) {
                            if (this.animatingDrawVideoImageButton == 0) {
                                this.animatingDrawVideoImageButtonProgress = 1.0f;
                            }
                            this.animatingDrawVideoImageButton = 1;
                        }
                    } else if (this.animatingDrawVideoImageButton == 0) {
                        this.animatingDrawVideoImageButtonProgress = 0.0f;
                    }
                    this.drawVideoImageButton = false;
                    this.drawVideoSize = false;
                    if (this.currentMessageObject.needDrawBluredPreview()) {
                        this.buttonState = -1;
                    } else if (this.currentMessageObject.type == 8 && !this.photoImage.isAllowStartAnimation()) {
                        this.buttonState = 2;
                    } else if (this.documentAttachType == 4) {
                        this.buttonState = 3;
                    } else {
                        this.buttonState = -1;
                    }
                    this.videoRadialProgress.setIcon(4, ifSame, this.animatingDrawVideoImageButton != 0);
                    this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                    if (!fromSet && this.photoNotSet) {
                        setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
                    }
                    invalidate();
                } else {
                    this.drawVideoSize = this.documentAttachType == 4;
                    if (this.documentAttachType == 4 && this.canStreamVideo && !this.drawVideoImageButton && animated) {
                        if (this.animatingDrawVideoImageButton != 2 && this.animatingDrawVideoImageButtonProgress < 1.0f) {
                            if (this.animatingDrawVideoImageButton == 0) {
                                this.animatingDrawVideoImageButtonProgress = 0.0f;
                            }
                            this.animatingDrawVideoImageButton = 2;
                        }
                    } else if (this.animatingDrawVideoImageButton == 0) {
                        this.animatingDrawVideoImageButtonProgress = 1.0f;
                    }
                    DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                    if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                        this.buttonState = 1;
                        progress = ImageLoader.getInstance().getFileProgress(fileName);
                        if (this.documentAttachType == 4 && this.canStreamVideo) {
                            this.drawVideoImageButton = true;
                            getIconForCurrentState();
                            this.radialProgress.setIcon(this.autoPlayingVideo ? 4 : 0, ifSame, animated);
                            this.videoRadialProgress.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
                            this.videoRadialProgress.setIcon(14, ifSame, animated);
                        } else {
                            this.drawVideoImageButton = false;
                            this.radialProgress.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
                            this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                            this.videoRadialProgress.setIcon(4, ifSame, false);
                            if (!this.drawVideoSize && this.animatingDrawVideoImageButton == 0) {
                                this.animatingDrawVideoImageButtonProgress = 0.0f;
                            }
                        }
                    } else {
                        boolean autoDownload = false;
                        if (this.currentMessageObject.type == 1) {
                            autoDownload = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject);
                        } else if (this.currentMessageObject.type == 8 && MessageObject.isNewGifDocument(this.currentMessageObject.messageOwner.media.document)) {
                            autoDownload = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject);
                        } else if (this.currentMessageObject.type == 5) {
                            autoDownload = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject);
                        }
                        if (this.cancelLoading || !autoDownload) {
                            this.buttonState = 0;
                        } else {
                            this.buttonState = 1;
                        }
                        if (this.documentAttachType == 4 && this.canStreamVideo) {
                            this.drawVideoImageButton = true;
                            getIconForCurrentState();
                            this.radialProgress.setIcon(this.autoPlayingVideo ? 4 : 0, ifSame, animated);
                            this.videoRadialProgress.setIcon(2, ifSame, animated);
                        } else {
                            this.drawVideoImageButton = false;
                            this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                            this.videoRadialProgress.setIcon(4, ifSame, false);
                            if (!this.drawVideoSize && this.animatingDrawVideoImageButton == 0) {
                                this.animatingDrawVideoImageButtonProgress = 0.0f;
                            }
                        }
                    }
                    invalidate();
                }
            }
        } else if (this.currentPhotoObject != null && this.drawImageButton) {
            if (fileExists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                if (this.documentAttachType != 2 || this.photoImage.isAllowStartAnimation()) {
                    this.buttonState = -1;
                } else {
                    this.buttonState = 2;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                invalidate();
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                float setProgress = 0.0f;
                if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 1;
                    progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                } else if (this.cancelLoading || !((this.documentAttachType == 0 && DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject)) || (this.documentAttachType == 2 && MessageObject.isNewGifDocument(this.documentAttach) && DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject)))) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(setProgress, false);
                this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                invalidate();
            }
        } else {
            return;
        }
        if (this.hasMiniProgress == 0) {
            this.radialProgress.setMiniIcon(4, false, animated);
        }
    }

    private void didPressMiniButton(boolean animated) {
        if (this.miniButtonState == 0) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            if (this.documentAttachType == 3 || this.documentAttachType == 5) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
            } else if (this.documentAttachType == 4) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
            }
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        } else if (this.miniButtonState == 1) {
            if ((this.documentAttachType == 3 || this.documentAttachType == 5) && MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        }
    }

    private void didPressButton(boolean animated, boolean video) {
        if (this.buttonState != 0 || ((this.drawVideoImageButton || video) && !video)) {
            if (this.buttonState == 1 && ((!this.drawVideoImageButton && !video) || video)) {
                this.photoImage.setForceLoading(false);
                if (this.documentAttachType == 3 || this.documentAttachType == 5) {
                    if (MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject)) {
                        this.buttonState = 0;
                        this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                        invalidate();
                    }
                } else if (!this.currentMessageObject.isOut() || (!this.currentMessageObject.isSending() && !this.currentMessageObject.isEditing())) {
                    this.cancelLoading = true;
                    if (this.documentAttachType == 4 || this.documentAttachType == 1 || this.documentAttachType == 8) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                    } else if (this.currentMessageObject.type == 0 || this.currentMessageObject.type == 1 || this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) {
                        ImageLoader.getInstance().cancelForceLoadingForImageReceiver(this.photoImage);
                        this.photoImage.cancelLoadImage();
                    } else if (this.currentMessageObject.type == 9) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.messageOwner.media.document);
                    }
                    this.buttonState = 0;
                    if (video) {
                        this.videoRadialProgress.setIcon(2, false, animated);
                    } else {
                        this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                    }
                    invalidate();
                } else if (this.radialProgress.getIcon() != 6) {
                    this.delegate.didPressCancelSendButton(this);
                }
            } else if (this.buttonState == 2) {
                if (this.documentAttachType == 3 || this.documentAttachType == 5) {
                    this.radialProgress.setProgress(0.0f, false);
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                    this.buttonState = 4;
                    this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                    invalidate();
                    return;
                }
                if (this.currentMessageObject.isRoundVideo()) {
                    MessageObject playingMessage = MediaController.getInstance().getPlayingMessageObject();
                    if (playingMessage == null || !playingMessage.isRoundVideo()) {
                        this.photoImage.setAllowStartAnimation(true);
                        this.photoImage.startAnimation();
                    }
                } else {
                    this.photoImage.setAllowStartAnimation(true);
                    this.photoImage.startAnimation();
                }
                this.currentMessageObject.gifState = 0.0f;
                this.buttonState = -1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            } else if (this.buttonState == 3 || (this.buttonState == 0 && this.drawVideoImageButton)) {
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, animated);
                }
                this.delegate.didPressImage(this);
            } else if (this.buttonState != 4) {
            } else {
                if (this.documentAttachType != 3 && this.documentAttachType != 5) {
                    return;
                }
                if ((!this.currentMessageObject.isOut() || (!this.currentMessageObject.isSending() && !this.currentMessageObject.isEditing())) && !this.currentMessageObject.isSendError()) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                    this.buttonState = 2;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                    invalidate();
                } else if (this.delegate != null) {
                    this.delegate.didPressCancelSendButton(this);
                }
            }
        } else if (this.documentAttachType == 3 || this.documentAttachType == 5) {
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
            }
        } else {
            PhotoSize thumb;
            String thumbFilter;
            this.cancelLoading = false;
            if (video) {
                this.videoRadialProgress.setProgress(0.0f, false);
            } else {
                this.radialProgress.setProgress(0.0f, false);
            }
            if (this.currentPhotoObject == null || !(this.photoImage.hasNotThumb() || this.currentPhotoObjectThumb == null)) {
                thumb = this.currentPhotoObjectThumb;
                thumbFilter = this.currentPhotoFilterThumb;
            } else {
                thumb = this.currentPhotoObject;
                thumbFilter = ((thumb instanceof TL_photoStrippedSize) || "s".equals(thumb.type)) ? this.currentPhotoFilterThumb : this.currentPhotoFilter;
            }
            if (this.currentMessageObject.type == 1) {
                int i;
                this.photoImage.setForceLoading(true);
                ImageReceiver imageReceiver = this.photoImage;
                PhotoSize photoSize = this.currentPhotoObject;
                String str = this.currentPhotoFilter;
                TLObject tLObject = this.currentPhotoObjectThumb;
                thumbFilter = this.currentPhotoFilterThumb;
                int i2 = this.currentPhotoObject.size;
                MessageObject messageObject = this.currentMessageObject;
                if (this.currentMessageObject.shouldEncryptPhotoOrVideo()) {
                    i = 2;
                } else {
                    i = 0;
                }
                imageReceiver.setImage(photoSize, str, tLObject, thumbFilter, i2, null, messageObject, i);
            } else if (this.currentMessageObject.type == 8) {
                this.currentMessageObject.gifState = 2.0f;
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentMessageObject.messageOwner.media.document, null, thumb, thumbFilter, this.currentMessageObject.messageOwner.media.document.size, null, this.currentMessageObject, 0);
            } else if (this.currentMessageObject.isRoundVideo()) {
                if (this.currentMessageObject.isSecretMedia()) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 1);
                } else {
                    this.currentMessageObject.gifState = 2.0f;
                    Document document = this.currentMessageObject.getDocument();
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(document, null, thumb, thumbFilter, document.size, null, this.currentMessageObject, 0);
                }
            } else if (this.currentMessageObject.type == 9) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 0, 0);
            } else if (this.documentAttachType == 4) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
            } else if (this.currentMessageObject.type != 0 || this.documentAttachType == 0) {
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentPhotoObject, this.currentPhotoFilter, this.currentPhotoObjectThumb, this.currentPhotoFilterThumb, 0, null, this.currentMessageObject, 0);
            } else if (this.documentAttachType == 2) {
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.documentAttach, null, this.currentPhotoObject, this.currentPhotoFilterThumb, this.documentAttach.size, null, this.currentMessageObject, 0);
                this.currentMessageObject.gifState = 2.0f;
            } else if (this.documentAttachType == 1) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 0, 0);
            } else if (this.documentAttachType == 8) {
                this.photoImage.setImage(this.documentAttach, this.currentPhotoFilter, null, this.currentPhotoObject, "b1", 0, "jpg", this.currentMessageObject, 1);
            }
            this.buttonState = 1;
            if (video) {
                this.videoRadialProgress.setIcon(14, false, animated);
            } else {
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            }
            invalidate();
        }
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        boolean z;
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            z = true;
        } else {
            z = false;
        }
        updateButtonState(true, z, false);
    }

    public void onSuccessDownload(String fileName) {
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            updateButtonState(false, true, false);
            updateWaveform();
            return;
        }
        if (this.drawVideoImageButton) {
            this.videoRadialProgress.setProgress(1.0f, true);
        } else {
            this.radialProgress.setProgress(1.0f, true);
        }
        if (SharedConfig.autoplayVideo && !this.currentMessageObject.needDrawBluredPreview() && !this.autoPlayingVideo && this.documentAttach != null && this.documentAttachType == 4 && (this.currentPosition == null || !((this.currentPosition.flags & 1) == 0 || (this.currentPosition.flags & 2) == 0))) {
            this.animatingNoSound = 2;
            ImageReceiver imageReceiver = this.photoImage;
            Document document = this.documentAttach;
            String str = "g";
            PhotoSize photoSize = this.currentPhotoObject;
            String str2 = ((this.currentPhotoObject instanceof TL_photoStrippedSize) || (this.currentPhotoObject != null && "s".equals(this.currentPhotoObject.type))) ? this.currentPhotoFilterThumb : this.currentPhotoFilter;
            imageReceiver.setImage(document, str, photoSize, str2, null, this.currentPhotoObjectThumb, this.currentPhotoFilterThumb, this.documentAttach.size, null, this.currentMessageObject, 0);
            if (PhotoViewer.isPlayingMessage(this.currentMessageObject)) {
                this.photoImage.setAllowStartAnimation(false);
            } else {
                this.photoImage.setAllowStartAnimation(true);
                this.photoImage.startAnimation();
            }
            this.autoPlayingVideo = true;
        }
        if (this.currentMessageObject.type != 0) {
            if (!this.photoNotSet || ((this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) && this.currentMessageObject.gifState != 1.0f)) {
                if ((this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) && this.currentMessageObject.gifState != 1.0f) {
                    this.photoNotSet = false;
                    this.buttonState = 2;
                    didPressButton(true, false);
                } else {
                    updateButtonState(false, true, false);
                }
            }
            if (this.photoNotSet) {
                setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
            }
        } else if (this.documentAttachType == 2 && this.currentMessageObject.gifState != 1.0f) {
            this.buttonState = 2;
            didPressButton(true, false);
        } else if (this.photoNotSet) {
            setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
        } else {
            updateButtonState(false, true, false);
        }
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        boolean animationLoaded;
        if (thumb || imageReceiver.getAnimation() == null) {
            animationLoaded = false;
        } else {
            animationLoaded = true;
        }
        if (this.currentMessageObject != null && set && !thumb && !this.currentMessageObject.mediaExists && !this.currentMessageObject.attachPathExists) {
            if (!((this.currentMessageObject.type == 0 && (this.documentAttachType == 8 || this.documentAttachType == 0 || this.documentAttachType == 6 || (animationLoaded && (this.documentAttachType == 2 || this.documentAttachType == 7)))) || this.currentMessageObject.type == 1)) {
                if (!animationLoaded) {
                    return;
                }
                if (!(this.currentMessageObject.type == 5 || this.currentMessageObject.type == 8)) {
                    return;
                }
            }
            this.currentMessageObject.mediaExists = true;
            updateButtonState(false, true, false);
        }
    }

    public void onProgressDownload(String fileName, float progress) {
        if (this.drawVideoImageButton) {
            this.videoRadialProgress.setProgress(progress, true);
        } else {
            this.radialProgress.setProgress(progress, true);
        }
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
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

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        this.radialProgress.setProgress(progress, true);
        if (progress == 1.0f && this.currentPosition != null && SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId()) && this.buttonState == 1) {
            this.drawRadialCheckBackground = true;
            getIconForCurrentState();
            this.radialProgress.setIcon(6, false, true);
        }
    }

    public void onProvideStructure(ViewStructure structure) {
        super.onProvideStructure(structure);
        if (this.allowAssistant && VERSION.SDK_INT >= 23) {
            if (this.currentMessageObject.messageText != null && this.currentMessageObject.messageText.length() > 0) {
                structure.setText(this.currentMessageObject.messageText);
            } else if (this.currentMessageObject.caption != null && this.currentMessageObject.caption.length() > 0) {
                structure.setText(this.currentMessageObject.caption);
            }
        }
    }

    public void setDelegate(ChatMessageCellDelegate chatMessageCellDelegate) {
        this.delegate = chatMessageCellDelegate;
    }

    public void setAllowAssistant(boolean value) {
        this.allowAssistant = value;
    }

    private void measureTime(MessageObject messageObject) {
        CharSequence signString;
        boolean edited;
        String timeString;
        if (messageObject.messageOwner.post_author != null) {
            signString = messageObject.messageOwner.post_author.replace("\n", "");
        } else if (messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.post_author != null) {
            signString = messageObject.messageOwner.fwd_from.post_author.replace("\n", "");
        } else if (messageObject.isOutOwner() || messageObject.messageOwner.from_id <= 0 || !messageObject.messageOwner.post) {
            signString = null;
        } else {
            User signUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
            if (signUser != null) {
                signString = ContactsController.formatName(signUser.first_name, signUser.last_name).replace(10, ' ');
            } else {
                signString = null;
            }
        }
        User author = null;
        if (this.currentMessageObject.isFromUser()) {
            author = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
        }
        if (messageObject.isLiveLocation() || messageObject.getDialogId() == 777000 || messageObject.messageOwner.via_bot_id != 0 || messageObject.messageOwner.via_bot_name != null || (author != null && author.bot)) {
            edited = false;
        } else if (this.currentPosition == null || this.currentMessagesGroup == null) {
            edited = (messageObject.messageOwner.flags & 32768) != 0 || messageObject.isEditing();
        } else {
            edited = false;
            int size = this.currentMessagesGroup.messages.size();
            for (int a = 0; a < size; a++) {
                MessageObject object = (MessageObject) this.currentMessagesGroup.messages.get(a);
                if ((object.messageOwner.flags & 32768) != 0 || object.isEditing()) {
                    edited = true;
                    break;
                }
            }
        }
        if (edited) {
            timeString = LocaleController.getString("EditedMessage", R.string.EditedMessage) + " " + LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000);
        } else {
            timeString = LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000);
        }
        if (signString != null) {
            this.currentTimeString = ", " + timeString;
        } else {
            this.currentTimeString = timeString;
        }
        int ceil = (int) Math.ceil((double) Theme.chat_timePaint.measureText(this.currentTimeString));
        this.timeWidth = ceil;
        this.timeTextWidth = ceil;
        if ((messageObject.messageOwner.flags & 1024) != 0) {
            this.currentViewsString = String.format("%s", new Object[]{LocaleController.formatShortNumber(Math.max(1, messageObject.messageOwner.views), null)});
            this.viewsTextWidth = (int) Math.ceil((double) Theme.chat_timePaint.measureText(this.currentViewsString));
            this.timeWidth += (this.viewsTextWidth + Theme.chat_msgInViewsDrawable.getIntrinsicWidth()) + AndroidUtilities.dp(10.0f);
        }
        if (signString != null) {
            if (this.availableTimeWidth == 0) {
                this.availableTimeWidth = AndroidUtilities.dp(1000.0f);
            }
            int widthForSign = this.availableTimeWidth - this.timeWidth;
            if (messageObject.isOutOwner()) {
                if (messageObject.type == 5) {
                    widthForSign -= AndroidUtilities.dp(20.0f);
                } else {
                    widthForSign -= AndroidUtilities.dp(96.0f);
                }
            }
            int width = (int) Math.ceil((double) Theme.chat_timePaint.measureText(signString, 0, signString.length()));
            if (width > widthForSign) {
                if (widthForSign <= 0) {
                    signString = "";
                    width = 0;
                } else {
                    signString = TextUtils.ellipsize(signString, Theme.chat_timePaint, (float) widthForSign, TruncateAt.END);
                    width = widthForSign;
                }
            }
            this.currentTimeString = signString + this.currentTimeString;
            this.timeTextWidth += width;
            this.timeWidth += width;
        }
    }

    private boolean isDrawSelectedBackground() {
        return (isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted);
    }

    private boolean isOpenChatByShare(MessageObject messageObject) {
        return (messageObject.messageOwner.fwd_from == null || messageObject.messageOwner.fwd_from.saved_from_peer == null) ? false : true;
    }

    private boolean checkNeedDrawShareButton(MessageObject messageObject) {
        if (this.currentPosition != null && !this.currentPosition.last) {
            return false;
        }
        if (!(messageObject.messageOwner.fwd_from == null || messageObject.isOutOwner() || messageObject.messageOwner.fwd_from.saved_from_peer == null || messageObject.getDialogId() != ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()))) {
            this.drwaShareGoIcon = true;
        }
        return messageObject.needDrawShareButton();
    }

    public boolean isInsideBackground(float x, float y) {
        return this.currentBackgroundDrawable != null && x >= ((float) (getLeft() + this.backgroundDrawableLeft)) && x <= ((float) ((getLeft() + this.backgroundDrawableLeft) + this.backgroundDrawableRight));
    }

    private void updateCurrentUserAndChat() {
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        MessageFwdHeader fwd_from = this.currentMessageObject.messageOwner.fwd_from;
        int currentUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (fwd_from != null && fwd_from.channel_id != 0 && this.currentMessageObject.getDialogId() == ((long) currentUserId)) {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(fwd_from.channel_id));
        } else if (fwd_from == null || fwd_from.saved_from_peer == null) {
            if (fwd_from != null && fwd_from.from_id != 0 && fwd_from.channel_id == 0 && this.currentMessageObject.getDialogId() == ((long) currentUserId)) {
                this.currentUser = messagesController.getUser(Integer.valueOf(fwd_from.from_id));
            } else if (this.currentMessageObject.isFromUser()) {
                this.currentUser = messagesController.getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
            } else if (this.currentMessageObject.messageOwner.from_id < 0) {
                this.currentChat = messagesController.getChat(Integer.valueOf(-this.currentMessageObject.messageOwner.from_id));
            } else if (this.currentMessageObject.messageOwner.post) {
                this.currentChat = messagesController.getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
            }
        } else if (fwd_from.saved_from_peer.user_id != 0) {
            if (fwd_from.from_id != 0) {
                this.currentUser = messagesController.getUser(Integer.valueOf(fwd_from.from_id));
            } else {
                this.currentUser = messagesController.getUser(Integer.valueOf(fwd_from.saved_from_peer.user_id));
            }
        } else if (fwd_from.saved_from_peer.channel_id != 0) {
            if (!this.currentMessageObject.isSavedFromMegagroup() || fwd_from.from_id == 0) {
                this.currentChat = messagesController.getChat(Integer.valueOf(fwd_from.saved_from_peer.channel_id));
            } else {
                this.currentUser = messagesController.getUser(Integer.valueOf(fwd_from.from_id));
            }
        } else if (fwd_from.saved_from_peer.chat_id == 0) {
        } else {
            if (fwd_from.from_id != 0) {
                this.currentUser = messagesController.getUser(Integer.valueOf(fwd_from.from_id));
            } else {
                this.currentChat = messagesController.getChat(Integer.valueOf(fwd_from.saved_from_peer.chat_id));
            }
        }
    }

    private void setMessageObjectInternal(MessageObject messageObject) {
        SpannableStringBuilder spannableStringBuilder;
        String name;
        if (!((messageObject.messageOwner.flags & 1024) == 0 || this.currentMessageObject.viewsReloaded)) {
            MessagesController.getInstance(this.currentAccount).addToViewsQueue(this.currentMessageObject);
            this.currentMessageObject.viewsReloaded = true;
        }
        updateCurrentUserAndChat();
        if (this.isAvatarVisible) {
            Object parentObject = null;
            if (this.currentUser != null) {
                if (this.currentUser.photo != null) {
                    this.currentPhoto = this.currentUser.photo.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(this.currentUser);
                parentObject = this.currentUser;
            } else if (this.currentChat != null) {
                if (this.currentChat.photo != null) {
                    this.currentPhoto = this.currentChat.photo.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(this.currentChat);
                parentObject = this.currentChat;
            } else {
                this.currentPhoto = null;
                this.avatarDrawable.setInfo(messageObject.messageOwner.from_id, null, null, false);
            }
            this.avatarImage.setImage(this.currentPhoto, "50_50", this.avatarDrawable, null, parentObject, 0);
        } else {
            this.currentPhoto = null;
        }
        measureTime(messageObject);
        this.namesOffset = 0;
        String viaUsername = null;
        CharSequence viaString = null;
        if (messageObject.messageOwner.via_bot_id != 0) {
            User botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.via_bot_id));
            if (!(botUser == null || botUser.username == null || botUser.username.length() <= 0)) {
                viaUsername = "@" + botUser.username;
                viaString = AndroidUtilities.replaceTags(String.format(" via <b>%s</b>", new Object[]{viaUsername}));
                this.viaWidth = (int) Math.ceil((double) Theme.chat_replyNamePaint.measureText(viaString, 0, viaString.length()));
                this.currentViaBotUser = botUser;
            }
        } else if (messageObject.messageOwner.via_bot_name != null && messageObject.messageOwner.via_bot_name.length() > 0) {
            viaUsername = "@" + messageObject.messageOwner.via_bot_name;
            viaString = AndroidUtilities.replaceTags(String.format(" via <b>%s</b>", new Object[]{viaUsername}));
            this.viaWidth = (int) Math.ceil((double) Theme.chat_replyNamePaint.measureText(viaString, 0, viaString.length()));
        }
        boolean authorName = this.drawName && this.isChat && !this.currentMessageObject.isOutOwner();
        boolean viaBot = (messageObject.messageOwner.fwd_from == null || messageObject.type == 14) && viaUsername != null;
        if (authorName || viaBot) {
            String adminString;
            int adminWidth;
            this.drawNameLayout = true;
            this.nameWidth = getMaxNameWidth();
            if (this.nameWidth < 0) {
                this.nameWidth = AndroidUtilities.dp(100.0f);
            }
            if (this.currentUser == null || this.currentMessageObject.isOutOwner() || this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5 || !this.delegate.isChatAdminCell(this.currentUser.id)) {
                adminString = null;
                adminWidth = 0;
            } else {
                adminString = LocaleController.getString("ChatAdmin", R.string.ChatAdmin);
                adminWidth = (int) Math.ceil((double) Theme.chat_adminPaint.measureText(adminString));
                this.nameWidth -= adminWidth;
            }
            if (!authorName) {
                this.currentNameString = "";
            } else if (this.currentUser != null) {
                this.currentNameString = UserObject.getUserName(this.currentUser);
            } else if (this.currentChat != null) {
                this.currentNameString = this.currentChat.title;
            } else {
                this.currentNameString = "DELETED";
            }
            CharSequence nameStringFinal = TextUtils.ellipsize(this.currentNameString.replace(10, ' '), Theme.chat_namePaint, (float) (this.nameWidth - (viaBot ? this.viaWidth : 0)), TruncateAt.END);
            if (viaBot) {
                int color;
                this.viaNameWidth = (int) Math.ceil((double) Theme.chat_namePaint.measureText(nameStringFinal, 0, nameStringFinal.length()));
                if (this.viaNameWidth != 0) {
                    this.viaNameWidth += AndroidUtilities.dp(4.0f);
                }
                if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                    color = Theme.getColor("chat_stickerViaBotNameText");
                } else {
                    color = Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outViaBotNameText" : "chat_inViaBotNameText");
                }
                if (this.currentNameString.length() > 0) {
                    spannableStringBuilder = new SpannableStringBuilder(String.format("%s via %s", new Object[]{nameStringFinal, viaUsername}));
                    spannableStringBuilder.setSpan(new TypefaceSpan(Typeface.DEFAULT, 0, color), nameStringFinal.length() + 1, nameStringFinal.length() + 4, 33);
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, color), nameStringFinal.length() + 5, spannableStringBuilder.length(), 33);
                    nameStringFinal = spannableStringBuilder;
                } else {
                    spannableStringBuilder = new SpannableStringBuilder(String.format("via %s", new Object[]{viaUsername}));
                    spannableStringBuilder.setSpan(new TypefaceSpan(Typeface.DEFAULT, 0, color), 0, 4, 33);
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, color), 4, spannableStringBuilder.length(), 33);
                    Object nameStringFinal2 = spannableStringBuilder;
                }
                nameStringFinal = TextUtils.ellipsize(nameStringFinal, Theme.chat_namePaint, (float) this.nameWidth, TruncateAt.END);
            }
            try {
                this.nameLayout = new StaticLayout(nameStringFinal, Theme.chat_namePaint, this.nameWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.nameLayout == null || this.nameLayout.getLineCount() <= 0) {
                    this.nameWidth = 0;
                } else {
                    this.nameWidth = (int) Math.ceil((double) this.nameLayout.getLineWidth(0));
                    if (messageObject.type != 13) {
                        this.namesOffset += AndroidUtilities.dp(19.0f);
                    }
                    this.nameOffsetX = this.nameLayout.getLineLeft(0);
                }
                if (adminString != null) {
                    this.adminLayout = new StaticLayout(adminString, Theme.chat_adminPaint, adminWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.nameWidth = (int) (((float) this.nameWidth) + (this.adminLayout.getLineWidth(0) + ((float) AndroidUtilities.dp(8.0f))));
                } else {
                    this.adminLayout = null;
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (this.currentNameString.length() == 0) {
                this.currentNameString = null;
            }
        } else {
            this.currentNameString = null;
            this.nameLayout = null;
            this.nameWidth = 0;
        }
        this.currentForwardUser = null;
        this.currentForwardNameString = null;
        this.currentForwardChannel = null;
        this.forwardedNameLayout[0] = null;
        this.forwardedNameLayout[1] = null;
        this.forwardedNameWidth = 0;
        if (this.drawForwardedName && messageObject.needDrawForwarded() && (this.currentPosition == null || this.currentPosition.minY == (byte) 0)) {
            if (messageObject.messageOwner.fwd_from.channel_id != 0) {
                this.currentForwardChannel = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(messageObject.messageOwner.fwd_from.channel_id));
            }
            if (messageObject.messageOwner.fwd_from.from_id != 0) {
                this.currentForwardUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.fwd_from.from_id));
            }
            if (!(this.currentForwardUser == null && this.currentForwardChannel == null)) {
                String fromString;
                if (this.currentForwardChannel != null) {
                    if (this.currentForwardUser != null) {
                        this.currentForwardNameString = String.format("%s (%s)", new Object[]{this.currentForwardChannel.title, UserObject.getUserName(this.currentForwardUser)});
                    } else {
                        this.currentForwardNameString = this.currentForwardChannel.title;
                    }
                } else if (this.currentForwardUser != null) {
                    this.currentForwardNameString = UserObject.getUserName(this.currentForwardUser);
                }
                this.forwardedNameWidth = getMaxNameWidth();
                String from = LocaleController.getString("From", R.string.From);
                String fromFormattedString = LocaleController.getString("FromFormatted", R.string.FromFormatted);
                int idx = fromFormattedString.indexOf("%1$s");
                name = TextUtils.ellipsize(this.currentForwardNameString.replace(10, ' '), Theme.chat_replyNamePaint, (float) ((this.forwardedNameWidth - ((int) Math.ceil((double) Theme.chat_forwardNamePaint.measureText(from + " ")))) - this.viaWidth), TruncateAt.END);
                try {
                    fromString = String.format(fromFormattedString, new Object[]{name});
                } catch (Exception e2) {
                    fromString = name.toString();
                }
                if (viaString != null) {
                    spannableStringBuilder = new SpannableStringBuilder(String.format("%s via %s", new Object[]{fromString, viaUsername}));
                    this.viaNameWidth = (int) Math.ceil((double) Theme.chat_forwardNamePaint.measureText(fromString));
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), (spannableStringBuilder.length() - viaUsername.length()) - 1, spannableStringBuilder.length(), 33);
                } else {
                    spannableStringBuilder = new SpannableStringBuilder(String.format(fromFormattedString, new Object[]{name}));
                }
                if (idx >= 0) {
                    stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), idx, name.length() + idx, 33);
                }
                try {
                    this.forwardedNameLayout[1] = new StaticLayout(TextUtils.ellipsize(stringBuilder, Theme.chat_forwardNamePaint, (float) this.forwardedNameWidth, TruncateAt.END), Theme.chat_forwardNamePaint, this.forwardedNameWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.forwardedNameLayout[0] = new StaticLayout(TextUtils.ellipsize(AndroidUtilities.replaceTags(LocaleController.getString("ForwardedMessage", R.string.ForwardedMessage)), Theme.chat_forwardNamePaint, (float) this.forwardedNameWidth, TruncateAt.END), Theme.chat_forwardNamePaint, this.forwardedNameWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.forwardedNameWidth = Math.max((int) Math.ceil((double) this.forwardedNameLayout[0].getLineWidth(0)), (int) Math.ceil((double) this.forwardedNameLayout[1].getLineWidth(0)));
                    this.forwardNameOffsetX[0] = this.forwardedNameLayout[0].getLineLeft(0);
                    this.forwardNameOffsetX[1] = this.forwardedNameLayout[1].getLineLeft(0);
                    if (messageObject.type != 5) {
                        this.namesOffset += AndroidUtilities.dp(36.0f);
                    }
                } catch (Throwable e3) {
                    FileLog.e(e3);
                }
            }
        }
        if (messageObject.hasValidReplyMessageObject() && (this.currentPosition == null || this.currentPosition.minY == (byte) 0)) {
            if (!(messageObject.type == 13 || messageObject.type == 5)) {
                this.namesOffset += AndroidUtilities.dp(42.0f);
                if (messageObject.type != 0) {
                    this.namesOffset += AndroidUtilities.dp(5.0f);
                }
            }
            int maxWidth = getMaxNameWidth();
            if (messageObject.type != 13 && messageObject.type != 5) {
                maxWidth -= AndroidUtilities.dp(10.0f);
            } else if (messageObject.type == 5) {
                maxWidth += AndroidUtilities.dp(13.0f);
            }
            CharSequence stringFinalText = null;
            PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.replyMessageObject.photoThumbs2, 320);
            if (photoSize == null) {
                photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.replyMessageObject.photoThumbs, 320);
            }
            if (photoSize == null || messageObject.replyMessageObject.type == 13 || ((messageObject.type == 13 && !AndroidUtilities.isTablet()) || messageObject.replyMessageObject.isSecretMedia())) {
                this.replyImageReceiver.setImageBitmap((Drawable) null);
                this.needReplyImage = false;
            } else {
                if (messageObject.replyMessageObject.isRoundVideo()) {
                    this.replyImageReceiver.setRoundRadius(AndroidUtilities.dp(22.0f));
                } else {
                    this.replyImageReceiver.setRoundRadius(0);
                }
                this.currentReplyPhoto = photoSize;
                this.replyImageReceiver.setImage(photoSize, "50_50", null, null, messageObject.replyMessageObject, 1);
                this.needReplyImage = true;
                maxWidth -= AndroidUtilities.dp(44.0f);
            }
            name = null;
            Chat chat;
            if (messageObject.customReplyName != null) {
                name = messageObject.customReplyName;
            } else if (messageObject.replyMessageObject.isFromUser()) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.replyMessageObject.messageOwner.from_id));
                if (user != null) {
                    name = UserObject.getUserName(user);
                }
            } else if (messageObject.replyMessageObject.messageOwner.from_id < 0) {
                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-messageObject.replyMessageObject.messageOwner.from_id));
                if (chat != null) {
                    name = chat.title;
                }
            } else {
                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(messageObject.replyMessageObject.messageOwner.to_id.channel_id));
                if (chat != null) {
                    name = chat.title;
                }
            }
            if (name == null) {
                name = LocaleController.getString("Loading", R.string.Loading);
            }
            CharSequence stringFinalName = TextUtils.ellipsize(name.replace(10, ' '), Theme.chat_replyNamePaint, (float) maxWidth, TruncateAt.END);
            if (messageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
                stringFinalText = TextUtils.ellipsize(Emoji.replaceEmoji(messageObject.replyMessageObject.messageOwner.media.game.title, Theme.chat_replyTextPaint.getFontMetricsInt(), AndroidUtilities.dp(14.0f), false), Theme.chat_replyTextPaint, (float) maxWidth, TruncateAt.END);
            } else if (messageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice) {
                stringFinalText = TextUtils.ellipsize(Emoji.replaceEmoji(messageObject.replyMessageObject.messageOwner.media.title, Theme.chat_replyTextPaint.getFontMetricsInt(), AndroidUtilities.dp(14.0f), false), Theme.chat_replyTextPaint, (float) maxWidth, TruncateAt.END);
            } else if (messageObject.replyMessageObject.messageText != null && messageObject.replyMessageObject.messageText.length() > 0) {
                String mess = messageObject.replyMessageObject.messageText.toString();
                if (mess.length() > 150) {
                    mess = mess.substring(0, 150);
                }
                stringFinalText = TextUtils.ellipsize(Emoji.replaceEmoji(mess.replace(10, ' '), Theme.chat_replyTextPaint.getFontMetricsInt(), AndroidUtilities.dp(14.0f), false), Theme.chat_replyTextPaint, (float) maxWidth, TruncateAt.END);
            }
            try {
                this.replyNameWidth = AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : 0) + 4));
                if (stringFinalName != null) {
                    this.replyNameLayout = new StaticLayout(stringFinalName, Theme.chat_replyNamePaint, maxWidth + AndroidUtilities.dp(6.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (this.replyNameLayout.getLineCount() > 0) {
                        this.replyNameWidth += ((int) Math.ceil((double) this.replyNameLayout.getLineWidth(0))) + AndroidUtilities.dp(8.0f);
                        this.replyNameOffset = this.replyNameLayout.getLineLeft(0);
                    }
                }
            } catch (Throwable e32) {
                FileLog.e(e32);
            }
            try {
                this.replyTextWidth = AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : 0) + 4));
                if (stringFinalText != null) {
                    this.replyTextLayout = new StaticLayout(stringFinalText, Theme.chat_replyTextPaint, maxWidth + AndroidUtilities.dp(6.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (this.replyTextLayout.getLineCount() > 0) {
                        this.replyTextWidth += ((int) Math.ceil((double) this.replyTextLayout.getLineWidth(0))) + AndroidUtilities.dp(8.0f);
                        this.replyTextOffset = this.replyTextLayout.getLineLeft(0);
                    }
                }
            } catch (Throwable e322) {
                FileLog.e(e322);
            }
        }
        requestLayout();
    }

    public int getCaptionHeight() {
        return this.addedCaptionHeight;
    }

    public ImageReceiver getAvatarImage() {
        return this.isAvatarVisible ? this.avatarImage : null;
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentMessageObject != null) {
            if (this.wasLayout) {
                Drawable currentBackgroundSelectedDrawable;
                Drawable currentBackgroundShadowDrawable;
                int i;
                int i2;
                long newTime;
                long dt;
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
                    if (this.documentAttachType == 3) {
                        if (this.currentMessageObject.isOutOwner()) {
                            this.seekBarWaveform.setColors(Theme.getColor("chat_outVoiceSeekbar"), Theme.getColor("chat_outVoiceSeekbarFill"), Theme.getColor("chat_outVoiceSeekbarSelected"));
                            this.seekBar.setColors(Theme.getColor("chat_outAudioSeekbar"), Theme.getColor("chat_outAudioCacheSeekbar"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarSelected"));
                        } else {
                            this.seekBarWaveform.setColors(Theme.getColor("chat_inVoiceSeekbar"), Theme.getColor("chat_inVoiceSeekbarFill"), Theme.getColor("chat_inVoiceSeekbarSelected"));
                            this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
                        }
                    } else if (this.documentAttachType == 5) {
                        this.documentAttachType = 5;
                        if (this.currentMessageObject.isOutOwner()) {
                            this.seekBar.setColors(Theme.getColor("chat_outAudioSeekbar"), Theme.getColor("chat_outAudioCacheSeekbar"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarSelected"));
                        } else {
                            this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
                        }
                    }
                }
                if (this.currentMessageObject.type == 5) {
                    Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
                } else if (this.mediaBackground) {
                    if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                        Theme.chat_timePaint.setColor(Theme.getColor("chat_serviceText"));
                    } else {
                        Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
                    }
                } else if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText"));
                } else {
                    Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText"));
                }
                int additionalTop = 0;
                int additionalBottom = 0;
                int offsetBottom;
                int backgroundTop;
                if (this.currentMessageObject.isOutOwner()) {
                    if (this.mediaBackground || this.drawPinnedBottom) {
                        this.currentBackgroundDrawable = Theme.chat_msgOutMediaDrawable;
                        currentBackgroundSelectedDrawable = Theme.chat_msgOutMediaSelectedDrawable;
                        currentBackgroundShadowDrawable = Theme.chat_msgOutMediaShadowDrawable;
                    } else {
                        this.currentBackgroundDrawable = Theme.chat_msgOutDrawable;
                        currentBackgroundSelectedDrawable = Theme.chat_msgOutSelectedDrawable;
                        currentBackgroundShadowDrawable = Theme.chat_msgOutShadowDrawable;
                    }
                    this.backgroundDrawableLeft = (this.layoutWidth - this.backgroundWidth) - (!this.mediaBackground ? 0 : AndroidUtilities.dp(9.0f));
                    i = this.backgroundWidth;
                    if (this.mediaBackground) {
                        i2 = 0;
                    } else {
                        i2 = AndroidUtilities.dp(3.0f);
                    }
                    this.backgroundDrawableRight = i - i2;
                    if (!(this.currentMessagesGroup == null || this.currentPosition.edge)) {
                        this.backgroundDrawableRight += AndroidUtilities.dp(10.0f);
                    }
                    int backgroundLeft = this.backgroundDrawableLeft;
                    if (!this.mediaBackground && this.drawPinnedBottom) {
                        this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                    }
                    if (this.currentPosition != null) {
                        if ((this.currentPosition.flags & 2) == 0) {
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 1) == 0) {
                            backgroundLeft -= AndroidUtilities.dp(8.0f);
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 4) == 0) {
                            additionalTop = 0 - AndroidUtilities.dp(9.0f);
                            additionalBottom = 0 + AndroidUtilities.dp(9.0f);
                        }
                        if ((this.currentPosition.flags & 8) == 0) {
                            additionalBottom += AndroidUtilities.dp(9.0f);
                        }
                    }
                    if (this.drawPinnedBottom && this.drawPinnedTop) {
                        offsetBottom = 0;
                    } else if (this.drawPinnedBottom) {
                        offsetBottom = AndroidUtilities.dp(1.0f);
                    } else {
                        offsetBottom = AndroidUtilities.dp(2.0f);
                    }
                    i2 = (this.drawPinnedTop || (this.drawPinnedTop && this.drawPinnedBottom)) ? 0 : AndroidUtilities.dp(1.0f);
                    backgroundTop = additionalTop + i2;
                    BaseCell.setDrawableBounds(this.currentBackgroundDrawable, backgroundLeft, backgroundTop, this.backgroundDrawableRight, (this.layoutHeight - offsetBottom) + additionalBottom);
                    BaseCell.setDrawableBounds(currentBackgroundSelectedDrawable, backgroundLeft, backgroundTop, this.backgroundDrawableRight, (this.layoutHeight - offsetBottom) + additionalBottom);
                    BaseCell.setDrawableBounds(currentBackgroundShadowDrawable, backgroundLeft, backgroundTop, this.backgroundDrawableRight, (this.layoutHeight - offsetBottom) + additionalBottom);
                } else {
                    if (this.mediaBackground || this.drawPinnedBottom) {
                        this.currentBackgroundDrawable = Theme.chat_msgInMediaDrawable;
                        currentBackgroundSelectedDrawable = Theme.chat_msgInMediaSelectedDrawable;
                        currentBackgroundShadowDrawable = Theme.chat_msgInMediaShadowDrawable;
                    } else {
                        this.currentBackgroundDrawable = Theme.chat_msgInDrawable;
                        currentBackgroundSelectedDrawable = Theme.chat_msgInSelectedDrawable;
                        currentBackgroundShadowDrawable = Theme.chat_msgInShadowDrawable;
                    }
                    i2 = (this.isChat && this.isAvatarVisible) ? 48 : 0;
                    this.backgroundDrawableLeft = AndroidUtilities.dp((float) (i2 + (!this.mediaBackground ? 3 : 9)));
                    i = this.backgroundWidth;
                    if (this.mediaBackground) {
                        i2 = 0;
                    } else {
                        i2 = AndroidUtilities.dp(3.0f);
                    }
                    this.backgroundDrawableRight = i - i2;
                    if (this.currentMessagesGroup != null) {
                        if (!this.currentPosition.edge) {
                            this.backgroundDrawableLeft -= AndroidUtilities.dp(10.0f);
                            this.backgroundDrawableRight += AndroidUtilities.dp(10.0f);
                        }
                        if (this.currentPosition.leftSpanOffset != 0) {
                            this.backgroundDrawableLeft += (int) Math.ceil((double) ((((float) this.currentPosition.leftSpanOffset) / 1000.0f) * ((float) getGroupPhotosWidth())));
                        }
                    }
                    if (!this.mediaBackground && this.drawPinnedBottom) {
                        this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                        this.backgroundDrawableLeft += AndroidUtilities.dp(6.0f);
                    }
                    if (this.currentPosition != null) {
                        if ((this.currentPosition.flags & 2) == 0) {
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 1) == 0) {
                            this.backgroundDrawableLeft -= AndroidUtilities.dp(8.0f);
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 4) == 0) {
                            additionalTop = 0 - AndroidUtilities.dp(9.0f);
                            additionalBottom = 0 + AndroidUtilities.dp(9.0f);
                        }
                        if ((this.currentPosition.flags & 8) == 0) {
                            additionalBottom += AndroidUtilities.dp(10.0f);
                        }
                    }
                    if (this.drawPinnedBottom && this.drawPinnedTop) {
                        offsetBottom = 0;
                    } else if (this.drawPinnedBottom) {
                        offsetBottom = AndroidUtilities.dp(1.0f);
                    } else {
                        offsetBottom = AndroidUtilities.dp(2.0f);
                    }
                    i2 = (this.drawPinnedTop || (this.drawPinnedTop && this.drawPinnedBottom)) ? 0 : AndroidUtilities.dp(1.0f);
                    backgroundTop = additionalTop + i2;
                    BaseCell.setDrawableBounds(this.currentBackgroundDrawable, this.backgroundDrawableLeft, backgroundTop, this.backgroundDrawableRight, (this.layoutHeight - offsetBottom) + additionalBottom);
                    BaseCell.setDrawableBounds(currentBackgroundSelectedDrawable, this.backgroundDrawableLeft, backgroundTop, this.backgroundDrawableRight, (this.layoutHeight - offsetBottom) + additionalBottom);
                    BaseCell.setDrawableBounds(currentBackgroundShadowDrawable, this.backgroundDrawableLeft, backgroundTop, this.backgroundDrawableRight, (this.layoutHeight - offsetBottom) + additionalBottom);
                }
                if (this.drawBackground && this.currentBackgroundDrawable != null) {
                    if (this.isHighlightedAnimated) {
                        float alpha;
                        this.currentBackgroundDrawable.draw(canvas);
                        if (this.highlightProgress >= 300) {
                            alpha = 1.0f;
                        } else {
                            alpha = ((float) this.highlightProgress) / 300.0f;
                        }
                        if (this.currentPosition == null) {
                            currentBackgroundSelectedDrawable.setAlpha((int) (255.0f * alpha));
                            currentBackgroundSelectedDrawable.draw(canvas);
                        }
                        newTime = System.currentTimeMillis();
                        dt = Math.abs(newTime - this.lastHighlightProgressTime);
                        if (dt > 17) {
                            dt = 17;
                        }
                        this.highlightProgress = (int) (((long) this.highlightProgress) - dt);
                        this.lastHighlightProgressTime = newTime;
                        if (this.highlightProgress <= 0) {
                            this.highlightProgress = 0;
                            this.isHighlightedAnimated = false;
                        }
                        invalidate();
                    } else if (!isDrawSelectedBackground() || (this.currentPosition != null && getBackground() == null)) {
                        this.currentBackgroundDrawable.draw(canvas);
                    } else {
                        currentBackgroundSelectedDrawable.setAlpha(255);
                        currentBackgroundSelectedDrawable.draw(canvas);
                    }
                    if (this.currentPosition == null || this.currentPosition.flags != 0) {
                        currentBackgroundShadowDrawable.draw(canvas);
                    }
                }
                drawContent(canvas);
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
                    Drawable drawable = Theme.chat_shareDrawable;
                    i = this.shareStartX;
                    int dp = this.layoutHeight - AndroidUtilities.dp(41.0f);
                    this.shareStartY = dp;
                    BaseCell.setDrawableBounds(drawable, i, dp);
                    Theme.chat_shareDrawable.draw(canvas);
                    if (this.drwaShareGoIcon) {
                        BaseCell.setDrawableBounds(Theme.chat_goIconDrawable, this.shareStartX + AndroidUtilities.dp(12.0f), this.shareStartY + AndroidUtilities.dp(9.0f));
                        Theme.chat_goIconDrawable.draw(canvas);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_shareIconDrawable, this.shareStartX + AndroidUtilities.dp(8.0f), this.shareStartY + AndroidUtilities.dp(9.0f));
                        Theme.chat_shareIconDrawable.draw(canvas);
                    }
                }
                if (this.replyNameLayout != null) {
                    if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                        if (this.currentMessageObject.isOutOwner()) {
                            this.replyStartX = AndroidUtilities.dp(23.0f);
                        } else if (this.currentMessageObject.type == 5) {
                            this.replyStartX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.dp(4.0f);
                        } else {
                            this.replyStartX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.dp(17.0f);
                        }
                        this.replyStartY = AndroidUtilities.dp(12.0f);
                    } else {
                        if (this.currentMessageObject.isOutOwner()) {
                            this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f);
                        } else if (this.mediaBackground) {
                            this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f);
                        } else {
                            i = this.backgroundDrawableLeft;
                            float f = (this.mediaBackground || !this.drawPinnedBottom) ? 18.0f : 12.0f;
                            this.replyStartX = AndroidUtilities.dp(f) + i;
                        }
                        if (!this.drawForwardedName || this.forwardedNameLayout[0] == null) {
                            i2 = 0;
                        } else {
                            i2 = 36;
                        }
                        i = i2 + 12;
                        if (!this.drawNameLayout || this.nameLayout == null) {
                            i2 = 0;
                        } else {
                            i2 = 20;
                        }
                        this.replyStartY = AndroidUtilities.dp((float) (i2 + i));
                    }
                }
                if (this.currentPosition == null) {
                    drawNamesLayout(canvas);
                }
                if (!(this.autoPlayingVideo && MediaController.getInstance().isPlayingMessageAndReadyToDraw(this.currentMessageObject))) {
                    drawOverlays(canvas);
                }
                if ((this.drawTime || !this.mediaBackground) && !this.forceNotDrawTime) {
                    drawTime(canvas);
                }
                if ((this.controlsAlpha != 1.0f || this.timeAlpha != 1.0f) && this.currentMessageObject.type != 5) {
                    newTime = System.currentTimeMillis();
                    dt = Math.abs(this.lastControlsAlphaChangeTime - newTime);
                    if (dt > 17) {
                        dt = 17;
                    }
                    this.totalChangeTime += dt;
                    if (this.totalChangeTime > 100) {
                        this.totalChangeTime = 100;
                    }
                    this.lastControlsAlphaChangeTime = newTime;
                    if (this.controlsAlpha != 1.0f) {
                        this.controlsAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) this.totalChangeTime) / 100.0f);
                    }
                    if (this.timeAlpha != 1.0f) {
                        this.timeAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) this.totalChangeTime) / 100.0f);
                    }
                    invalidate();
                    if (this.forceNotDrawTime && this.currentPosition != null && this.currentPosition.last && getParent() != null) {
                        ((View) getParent()).invalidate();
                        return;
                    }
                    return;
                }
                return;
            }
            requestLayout();
        }
    }

    public void setTimeAlpha(float value) {
        this.timeAlpha = value;
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
        return (this.drawNameLayout && this.nameLayout != null) || ((this.drawForwardedName && this.forwardedNameLayout[0] != null && this.forwardedNameLayout[1] != null && (this.currentPosition == null || (this.currentPosition.minY == (byte) 0 && this.currentPosition.minX == (byte) 0))) || this.replyNameLayout != null);
    }

    public void drawNamesLayout(Canvas canvas) {
        float f;
        int backWidth;
        int i = 0;
        float f2 = 11.0f;
        if (this.drawNameLayout && this.nameLayout != null) {
            canvas.save();
            if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                Theme.chat_namePaint.setColor(Theme.getColor("chat_stickerNameText"));
                if (this.currentMessageObject.isOutOwner()) {
                    this.nameX = (float) AndroidUtilities.dp(28.0f);
                } else {
                    this.nameX = (float) ((this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.dp(22.0f));
                }
                this.nameY = (float) (this.layoutHeight - AndroidUtilities.dp(38.0f));
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(((int) this.nameX) - AndroidUtilities.dp(12.0f), ((int) this.nameY) - AndroidUtilities.dp(5.0f), (((int) this.nameX) + AndroidUtilities.dp(12.0f)) + this.nameWidth, ((int) this.nameY) + AndroidUtilities.dp(22.0f));
                Theme.chat_systemDrawable.draw(canvas);
            } else {
                if (this.mediaBackground || this.currentMessageObject.isOutOwner()) {
                    this.nameX = ((float) (this.backgroundDrawableLeft + AndroidUtilities.dp(11.0f))) - this.nameOffsetX;
                } else {
                    int i2 = this.backgroundDrawableLeft;
                    f = (this.mediaBackground || !this.drawPinnedBottom) ? 17.0f : 11.0f;
                    this.nameX = ((float) (AndroidUtilities.dp(f) + i2)) - this.nameOffsetX;
                }
                if (this.currentUser != null) {
                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentUser.id));
                } else if (this.currentChat == null) {
                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(0));
                } else if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentChat.id));
                } else {
                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(5));
                }
                if (this.drawPinnedTop) {
                    f = 9.0f;
                } else {
                    f = 10.0f;
                }
                this.nameY = (float) AndroidUtilities.dp(f);
            }
            canvas.translate(this.nameX, this.nameY);
            this.nameLayout.draw(canvas);
            canvas.restore();
            if (this.adminLayout != null) {
                Theme.chat_adminPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_adminSelectedText" : "chat_adminText"));
                canvas.save();
                canvas.translate(((float) ((this.backgroundDrawableLeft + this.backgroundDrawableRight) - AndroidUtilities.dp(11.0f))) - this.adminLayout.getLineWidth(0), this.nameY + ((float) AndroidUtilities.dp(0.5f)));
                this.adminLayout.draw(canvas);
                canvas.restore();
            }
        }
        if (this.drawForwardedName && this.forwardedNameLayout[0] != null && this.forwardedNameLayout[1] != null && (this.currentPosition == null || (this.currentPosition.minY == (byte) 0 && this.currentPosition.minX == (byte) 0))) {
            if (this.currentMessageObject.type == 5) {
                Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_stickerReplyNameText"));
                if (this.currentMessageObject.isOutOwner()) {
                    this.forwardNameX = AndroidUtilities.dp(23.0f);
                } else {
                    this.forwardNameX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.dp(17.0f);
                }
                this.forwardNameY = AndroidUtilities.dp(12.0f);
                backWidth = this.forwardedNameWidth + AndroidUtilities.dp(14.0f);
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(this.forwardNameX - AndroidUtilities.dp(7.0f), this.forwardNameY - AndroidUtilities.dp(6.0f), (this.forwardNameX - AndroidUtilities.dp(7.0f)) + backWidth, this.forwardNameY + AndroidUtilities.dp(38.0f));
                Theme.chat_systemDrawable.draw(canvas);
            } else {
                int i3;
                if (this.drawNameLayout) {
                    i3 = 19;
                } else {
                    i3 = 0;
                }
                this.forwardNameY = AndroidUtilities.dp((float) (i3 + 10));
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_outForwardedNameText"));
                    this.forwardNameX = this.backgroundDrawableLeft + AndroidUtilities.dp(11.0f);
                } else {
                    Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_inForwardedNameText"));
                    if (this.mediaBackground) {
                        this.forwardNameX = this.backgroundDrawableLeft + AndroidUtilities.dp(11.0f);
                    } else {
                        i3 = this.backgroundDrawableLeft;
                        if (this.mediaBackground || !this.drawPinnedBottom) {
                            f2 = 17.0f;
                        }
                        this.forwardNameX = i3 + AndroidUtilities.dp(f2);
                    }
                }
            }
            for (int a = 0; a < 2; a++) {
                canvas.save();
                canvas.translate(((float) this.forwardNameX) - this.forwardNameOffsetX[a], (float) (this.forwardNameY + (AndroidUtilities.dp(16.0f) * a)));
                this.forwardedNameLayout[a].draw(canvas);
                canvas.restore();
            }
        }
        if (this.replyNameLayout != null) {
            if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_stickerReplyLine"));
                Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_stickerReplyNameText"));
                Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_stickerReplyMessageText"));
                backWidth = Math.max(this.replyNameWidth, this.replyTextWidth) + AndroidUtilities.dp(14.0f);
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(this.replyStartX - AndroidUtilities.dp(7.0f), this.replyStartY - AndroidUtilities.dp(6.0f), (this.replyStartX - AndroidUtilities.dp(7.0f)) + backWidth, this.replyStartY + AndroidUtilities.dp(41.0f));
                Theme.chat_systemDrawable.draw(canvas);
            } else if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_outReplyLine"));
                Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_outReplyNameText"));
                if (!this.currentMessageObject.hasValidReplyMessageObject() || this.currentMessageObject.replyMessageObject.type != 0 || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                    Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outReplyMediaMessageSelectedText" : "chat_outReplyMediaMessageText"));
                } else {
                    Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_outReplyMessageText"));
                }
            } else {
                Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_inReplyLine"));
                Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_inReplyNameText"));
                if (!this.currentMessageObject.hasValidReplyMessageObject() || this.currentMessageObject.replyMessageObject.type != 0 || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                    Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inReplyMediaMessageSelectedText" : "chat_inReplyMediaMessageText"));
                } else {
                    Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_inReplyMessageText"));
                }
            }
            if (this.currentPosition == null || (this.currentPosition.minY == (byte) 0 && this.currentPosition.minX == (byte) 0)) {
                canvas.drawRect((float) this.replyStartX, (float) this.replyStartY, (float) (this.replyStartX + AndroidUtilities.dp(2.0f)), (float) (this.replyStartY + AndroidUtilities.dp(35.0f)), Theme.chat_replyLinePaint);
                if (this.needReplyImage) {
                    this.replyImageReceiver.setImageCoords(this.replyStartX + AndroidUtilities.dp(10.0f), this.replyStartY, AndroidUtilities.dp(35.0f), AndroidUtilities.dp(35.0f));
                    this.replyImageReceiver.draw(canvas);
                }
                if (this.replyNameLayout != null) {
                    canvas.save();
                    canvas.translate(((float) AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : 0) + 10))) + (((float) this.replyStartX) - this.replyNameOffset), (float) this.replyStartY);
                    this.replyNameLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.replyTextLayout != null) {
                    canvas.save();
                    f = ((float) this.replyStartX) - this.replyTextOffset;
                    if (this.needReplyImage) {
                        i = 44;
                    }
                    canvas.translate(f + ((float) AndroidUtilities.dp((float) (i + 10))), (float) (this.replyStartY + AndroidUtilities.dp(19.0f)));
                    this.replyTextLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    public boolean hasCaptionLayout() {
        return this.captionLayout != null;
    }

    public void drawCaptionLayout(Canvas canvas, boolean selectionOnly) {
        if (this.captionLayout == null) {
            return;
        }
        if (!selectionOnly || this.pressedLink != null) {
            canvas.save();
            canvas.translate((float) this.captionX, (float) this.captionY);
            if (this.pressedLink != null) {
                for (int b = 0; b < this.urlPath.size(); b++) {
                    canvas.drawPath((Path) this.urlPath.get(b), Theme.chat_urlPaint);
                }
            }
            if (!selectionOnly) {
                try {
                    this.captionLayout.draw(canvas);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            canvas.restore();
        }
    }

    public boolean needDrawTime() {
        return !this.forceNotDrawTime;
    }

    public void drawTime(Canvas canvas) {
        if (((this.drawTime && !this.groupPhotoInvisible) || !this.mediaBackground || this.captionLayout != null) && this.timeLayout != null) {
            int x;
            int y;
            if (this.currentMessageObject.type == 5) {
                Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
            } else if (this.mediaBackground && this.captionLayout == null) {
                if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                    Theme.chat_timePaint.setColor(Theme.getColor("chat_serviceText"));
                } else {
                    Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
                }
            } else if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText"));
            } else {
                Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText"));
            }
            if (this.drawPinnedBottom) {
                canvas.translate(0.0f, (float) AndroidUtilities.dp(2.0f));
            }
            int additionalX;
            Drawable viewsDrawable;
            if (this.mediaBackground && this.captionLayout == null) {
                Paint paint;
                if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                    paint = Theme.chat_actionBackgroundPaint;
                } else {
                    paint = Theme.chat_timeBackgroundPaint;
                }
                int oldAlpha = paint.getAlpha();
                paint.setAlpha((int) (((float) oldAlpha) * this.timeAlpha));
                Theme.chat_timePaint.setAlpha((int) (255.0f * this.timeAlpha));
                int x1 = this.timeX - AndroidUtilities.dp(4.0f);
                int y1 = this.layoutHeight - AndroidUtilities.dp(28.0f);
                this.rect.set((float) x1, (float) y1, (float) (AndroidUtilities.dp((float) ((this.currentMessageObject.isOutOwner() ? 20 : 0) + 8)) + (x1 + this.timeWidth)), (float) (AndroidUtilities.dp(17.0f) + y1));
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint);
                paint.setAlpha(oldAlpha);
                additionalX = (int) (-this.timeLayout.getLineLeft(0));
                if ((this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                    additionalX += (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(0));
                    if (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing()) {
                        if (!this.currentMessageObject.isOutOwner()) {
                            BaseCell.setDrawableBounds(Theme.chat_msgMediaClockDrawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(14.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                            Theme.chat_msgMediaClockDrawable.draw(canvas);
                        }
                    } else if (!this.currentMessageObject.isSendError()) {
                        if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                            viewsDrawable = Theme.chat_msgStickerViewsDrawable;
                        } else {
                            viewsDrawable = Theme.chat_msgMediaViewsDrawable;
                        }
                        oldAlpha = ((BitmapDrawable) viewsDrawable).getPaint().getAlpha();
                        viewsDrawable.setAlpha((int) (this.timeAlpha * ((float) oldAlpha)));
                        BaseCell.setDrawableBounds(viewsDrawable, this.timeX, (this.layoutHeight - AndroidUtilities.dp(10.5f)) - this.timeLayout.getHeight());
                        viewsDrawable.draw(canvas);
                        viewsDrawable.setAlpha(oldAlpha);
                        if (this.viewsLayout != null) {
                            canvas.save();
                            canvas.translate((float) ((this.timeX + viewsDrawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.dp(12.3f)) - this.timeLayout.getHeight()));
                            this.viewsLayout.draw(canvas);
                            canvas.restore();
                        }
                    } else if (!this.currentMessageObject.isOutOwner()) {
                        x = this.timeX + AndroidUtilities.dp(11.0f);
                        y = this.layoutHeight - AndroidUtilities.dp(27.5f);
                        this.rect.set((float) x, (float) y, (float) (AndroidUtilities.dp(14.0f) + x), (float) (AndroidUtilities.dp(14.0f) + y));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                        BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, AndroidUtilities.dp(6.0f) + x, AndroidUtilities.dp(2.0f) + y);
                        Theme.chat_msgErrorDrawable.draw(canvas);
                    }
                }
                canvas.save();
                canvas.translate((float) (this.timeX + additionalX), (float) ((this.layoutHeight - AndroidUtilities.dp(12.3f)) - this.timeLayout.getHeight()));
                this.timeLayout.draw(canvas);
                canvas.restore();
                Theme.chat_timePaint.setAlpha(255);
            } else {
                additionalX = (int) (-this.timeLayout.getLineLeft(0));
                if ((this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                    additionalX += (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(0));
                    if (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing()) {
                        if (!this.currentMessageObject.isOutOwner()) {
                            Drawable clockDrawable = isDrawSelectedBackground() ? Theme.chat_msgInSelectedClockDrawable : Theme.chat_msgInClockDrawable;
                            BaseCell.setDrawableBounds(clockDrawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - clockDrawable.getIntrinsicHeight());
                            clockDrawable.draw(canvas);
                        }
                    } else if (!this.currentMessageObject.isSendError()) {
                        if (this.currentMessageObject.isOutOwner()) {
                            viewsDrawable = isDrawSelectedBackground() ? Theme.chat_msgOutViewsSelectedDrawable : Theme.chat_msgOutViewsDrawable;
                            BaseCell.setDrawableBounds(viewsDrawable, this.timeX, (this.layoutHeight - AndroidUtilities.dp(4.5f)) - this.timeLayout.getHeight());
                            viewsDrawable.draw(canvas);
                        } else {
                            viewsDrawable = isDrawSelectedBackground() ? Theme.chat_msgInViewsSelectedDrawable : Theme.chat_msgInViewsDrawable;
                            BaseCell.setDrawableBounds(viewsDrawable, this.timeX, (this.layoutHeight - AndroidUtilities.dp(4.5f)) - this.timeLayout.getHeight());
                            viewsDrawable.draw(canvas);
                        }
                        if (this.viewsLayout != null) {
                            canvas.save();
                            canvas.translate((float) ((this.timeX + Theme.chat_msgInViewsDrawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.dp(6.5f)) - this.timeLayout.getHeight()));
                            this.viewsLayout.draw(canvas);
                            canvas.restore();
                        }
                    } else if (!this.currentMessageObject.isOutOwner()) {
                        x = this.timeX + AndroidUtilities.dp(11.0f);
                        y = this.layoutHeight - AndroidUtilities.dp(20.5f);
                        this.rect.set((float) x, (float) y, (float) (AndroidUtilities.dp(14.0f) + x), (float) (AndroidUtilities.dp(14.0f) + y));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                        BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, AndroidUtilities.dp(6.0f) + x, AndroidUtilities.dp(2.0f) + y);
                        Theme.chat_msgErrorDrawable.draw(canvas);
                    }
                }
                canvas.save();
                canvas.translate((float) (this.timeX + additionalX), (float) ((this.layoutHeight - AndroidUtilities.dp(6.5f)) - this.timeLayout.getHeight()));
                this.timeLayout.draw(canvas);
                canvas.restore();
            }
            if (this.currentMessageObject.isOutOwner()) {
                boolean drawCheck1 = false;
                boolean drawCheck2 = false;
                boolean drawClock = false;
                boolean drawError = false;
                boolean isBroadcast = ((int) (this.currentMessageObject.getDialogId() >> 32)) == 1;
                if (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing()) {
                    drawCheck1 = false;
                    drawCheck2 = false;
                    drawClock = true;
                    drawError = false;
                } else if (this.currentMessageObject.isSendError()) {
                    drawCheck1 = false;
                    drawCheck2 = false;
                    drawClock = false;
                    drawError = true;
                } else if (this.currentMessageObject.isSent()) {
                    if (this.currentMessageObject.isUnread()) {
                        drawCheck1 = false;
                        drawCheck2 = true;
                    } else {
                        drawCheck1 = true;
                        drawCheck2 = true;
                    }
                    drawClock = false;
                    drawError = false;
                }
                if (drawClock) {
                    if (!this.mediaBackground || this.captionLayout != null) {
                        BaseCell.setDrawableBounds(Theme.chat_msgOutClockDrawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicHeight());
                        Theme.chat_msgOutClockDrawable.draw(canvas);
                    } else if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                        Theme.chat_msgStickerClockDrawable.setAlpha((int) (255.0f * this.timeAlpha));
                        BaseCell.setDrawableBounds(Theme.chat_msgStickerClockDrawable, (this.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicHeight());
                        Theme.chat_msgStickerClockDrawable.draw(canvas);
                        Theme.chat_msgStickerClockDrawable.setAlpha(255);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_msgMediaClockDrawable, (this.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                        Theme.chat_msgMediaClockDrawable.draw(canvas);
                    }
                }
                if (!isBroadcast) {
                    Drawable drawable;
                    if (drawCheck2) {
                        if (!this.mediaBackground || this.captionLayout != null) {
                            drawable = isDrawSelectedBackground() ? Theme.chat_msgOutCheckSelectedDrawable : Theme.chat_msgOutCheckDrawable;
                            if (drawCheck1) {
                                BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(22.5f)) - drawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - drawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable.getIntrinsicHeight());
                            }
                            drawable.draw(canvas);
                        } else if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                            if (drawCheck1) {
                                BaseCell.setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgStickerCheckDrawable.draw(canvas);
                        } else {
                            if (drawCheck1) {
                                BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgMediaCheckDrawable.setAlpha((int) (255.0f * this.timeAlpha));
                            Theme.chat_msgMediaCheckDrawable.draw(canvas);
                            Theme.chat_msgMediaCheckDrawable.setAlpha(255);
                        }
                    }
                    if (drawCheck1) {
                        if (!this.mediaBackground || this.captionLayout != null) {
                            drawable = isDrawSelectedBackground() ? Theme.chat_msgOutHalfCheckSelectedDrawable : Theme.chat_msgOutHalfCheckDrawable;
                            BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(18.0f)) - drawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable.getIntrinsicHeight());
                            drawable.draw(canvas);
                        } else if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                            BaseCell.setDrawableBounds(Theme.chat_msgStickerHalfCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicHeight());
                            Theme.chat_msgStickerHalfCheckDrawable.draw(canvas);
                        } else {
                            BaseCell.setDrawableBounds(Theme.chat_msgMediaHalfCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicHeight());
                            Theme.chat_msgMediaHalfCheckDrawable.setAlpha((int) (255.0f * this.timeAlpha));
                            Theme.chat_msgMediaHalfCheckDrawable.draw(canvas);
                            Theme.chat_msgMediaHalfCheckDrawable.setAlpha(255);
                        }
                    }
                } else if (drawCheck1 || drawCheck2) {
                    if (this.mediaBackground && this.captionLayout == null) {
                        BaseCell.setDrawableBounds(Theme.chat_msgBroadcastMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(24.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(14.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicHeight());
                        Theme.chat_msgBroadcastMediaDrawable.draw(canvas);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_msgBroadcastDrawable, (this.layoutWidth - AndroidUtilities.dp(20.5f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicHeight());
                        Theme.chat_msgBroadcastDrawable.draw(canvas);
                    }
                }
                if (drawError) {
                    if (this.mediaBackground && this.captionLayout == null) {
                        x = this.layoutWidth - AndroidUtilities.dp(34.5f);
                        y = this.layoutHeight - AndroidUtilities.dp(26.5f);
                    } else {
                        x = this.layoutWidth - AndroidUtilities.dp(32.0f);
                        y = this.layoutHeight - AndroidUtilities.dp(21.0f);
                    }
                    this.rect.set((float) x, (float) y, (float) (AndroidUtilities.dp(14.0f) + x), (float) (AndroidUtilities.dp(14.0f) + y));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                    BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, AndroidUtilities.dp(6.0f) + x, AndroidUtilities.dp(2.0f) + y);
                    Theme.chat_msgErrorDrawable.draw(canvas);
                }
            }
        }
    }

    public void drawOverlays(Canvas canvas) {
        long newAnimationTime = SystemClock.uptimeMillis();
        long animationDt = newAnimationTime - this.lastAnimationTime;
        if (animationDt > 17) {
            animationDt = 17;
        }
        this.lastAnimationTime = newAnimationTime;
        int imageX;
        int dp;
        String text;
        int x;
        if (this.currentMessageObject.type == 1 || this.documentAttachType == 4) {
            if (this.photoImage.getVisible()) {
                int oldAlpha;
                if (!this.currentMessageObject.needDrawBluredPreview() && this.documentAttachType == 4) {
                    if (this.currentMessageObject.hadAnimationNotReadyLoading) {
                        AnimatedFileDrawable animation = this.photoImage.getAnimation();
                        if (animation != null && animation.hasBitmap()) {
                            this.currentMessageObject.hadAnimationNotReadyLoading = false;
                            updateButtonState(false, true, false);
                        }
                    }
                    oldAlpha = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                    Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                    Drawable drawable = Theme.chat_msgMediaMenuDrawable;
                    imageX = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                    this.otherX = imageX;
                    int imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                    this.otherY = imageY;
                    BaseCell.setDrawableBounds(drawable, imageX, imageY);
                    Theme.chat_msgMediaMenuDrawable.draw(canvas);
                    Theme.chat_msgMediaMenuDrawable.setAlpha(oldAlpha);
                }
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (this.animatingNoSoundPlaying != playing) {
                    this.animatingNoSoundPlaying = playing;
                    this.animatingNoSound = playing ? 1 : 2;
                    this.animatingNoSoundProgress = playing ? 1.0f : 0.0f;
                }
                if (this.buttonState == 1 || this.buttonState == 0 || this.buttonState == 3 || this.currentMessageObject.needDrawBluredPreview()) {
                    if (this.autoPlayingVideo) {
                        updatePlayingMessageProgress();
                    }
                    if (this.infoLayout != null && (!this.forceNotDrawTime || this.autoPlayingVideo || this.drawVideoImageButton)) {
                        int imageW;
                        float alpha = (this.currentMessageObject.needDrawBluredPreview() && this.docTitleLayout == null) ? 0.0f : this.animatingDrawVideoImageButtonProgress;
                        Theme.chat_infoPaint.setColor(Theme.getColor("chat_mediaInfoText"));
                        int x1 = this.photoImage.getImageX() + AndroidUtilities.dp(4.0f);
                        int y1 = this.photoImage.getImageY() + AndroidUtilities.dp(4.0f);
                        if (!this.autoPlayingVideo || (playing && this.animatingNoSound == 0)) {
                            imageW = 0;
                        } else {
                            imageW = (int) (((float) (Theme.chat_msgNoSoundDrawable.getIntrinsicWidth() + AndroidUtilities.dp(4.0f))) * this.animatingNoSoundProgress);
                        }
                        int w = (int) Math.ceil((double) ((((float) ((((this.canStreamVideo ? AndroidUtilities.dp(32.0f) : 0) + Math.max(this.infoWidth + imageW, this.docTitleWidth)) - this.infoWidth) - imageW)) * alpha) + ((float) ((this.infoWidth + AndroidUtilities.dp(8.0f)) + imageW))));
                        if (alpha != 0.0f && this.docTitleLayout == null) {
                            alpha = 0.0f;
                        }
                        this.rect.set((float) x1, (float) y1, (float) (x1 + w), (float) (AndroidUtilities.dp(16.5f + (15.5f * alpha)) + y1));
                        oldAlpha = Theme.chat_timeBackgroundPaint.getAlpha();
                        Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                        Theme.chat_timeBackgroundPaint.setAlpha(oldAlpha);
                        Theme.chat_infoPaint.setAlpha((int) (255.0f * this.controlsAlpha));
                        canvas.save();
                        dp = AndroidUtilities.dp((this.canStreamVideo ? 30.0f * alpha : 0.0f) + 8.0f) + this.photoImage.getImageX();
                        this.noSoundCenterX = dp;
                        canvas.translate((float) dp, (float) (this.photoImage.getImageY() + AndroidUtilities.dp(5.5f + (0.2f * alpha))));
                        if (this.infoLayout != null) {
                            this.infoLayout.draw(canvas);
                        }
                        if (alpha > 0.0f && this.docTitleLayout != null) {
                            canvas.save();
                            Theme.chat_infoPaint.setAlpha((int) ((255.0f * this.controlsAlpha) * alpha));
                            canvas.translate(0.0f, (float) AndroidUtilities.dp(14.3f * alpha));
                            this.docTitleLayout.draw(canvas);
                            canvas.restore();
                        }
                        if (imageW != 0) {
                            Theme.chat_msgNoSoundDrawable.setAlpha((int) (((255.0f * this.animatingNoSoundProgress) * this.animatingNoSoundProgress) * this.controlsAlpha));
                            canvas.translate((float) (this.infoWidth + AndroidUtilities.dp(4.0f)), 0.0f);
                            int size = AndroidUtilities.dp(14.0f * this.animatingNoSoundProgress);
                            int y = (AndroidUtilities.dp(14.0f) - size) / 2;
                            Theme.chat_msgNoSoundDrawable.setBounds(0, y, size, y + size);
                            Theme.chat_msgNoSoundDrawable.draw(canvas);
                            this.noSoundCenterX += (this.infoWidth + AndroidUtilities.dp(4.0f)) + (size / 2);
                        }
                        canvas.restore();
                        Theme.chat_infoPaint.setAlpha(255);
                    }
                }
                if (this.animatingDrawVideoImageButton == 1) {
                    this.animatingDrawVideoImageButtonProgress -= ((float) animationDt) / 160.0f;
                    if (this.animatingDrawVideoImageButtonProgress <= 0.0f) {
                        this.animatingDrawVideoImageButtonProgress = 0.0f;
                        this.animatingDrawVideoImageButton = 0;
                    }
                    invalidate();
                } else if (this.animatingDrawVideoImageButton == 2) {
                    this.animatingDrawVideoImageButtonProgress += ((float) animationDt) / 160.0f;
                    if (this.animatingDrawVideoImageButtonProgress >= 1.0f) {
                        this.animatingDrawVideoImageButtonProgress = 1.0f;
                        this.animatingDrawVideoImageButton = 0;
                    }
                    invalidate();
                }
                if (this.animatingNoSound == 1) {
                    this.animatingNoSoundProgress -= ((float) animationDt) / 180.0f;
                    if (this.animatingNoSoundProgress <= 0.0f) {
                        this.animatingNoSoundProgress = 0.0f;
                        this.animatingNoSound = 0;
                    }
                    invalidate();
                } else if (this.animatingNoSound == 2) {
                    this.animatingNoSoundProgress += ((float) animationDt) / 180.0f;
                    if (this.animatingNoSoundProgress >= 1.0f) {
                        this.animatingNoSoundProgress = 1.0f;
                        this.animatingNoSound = 0;
                    }
                    invalidate();
                }
            }
        } else if (this.currentMessageObject.type == 4) {
            if (this.docTitleLayout != null) {
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_locationTitlePaint.setColor(Theme.getColor("chat_messageTextOut"));
                    Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outVenueInfoSelectedText" : "chat_outVenueInfoText"));
                } else {
                    Theme.chat_locationTitlePaint.setColor(Theme.getColor("chat_messageTextIn"));
                    Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inVenueInfoSelectedText" : "chat_inVenueInfoText"));
                }
                if (this.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                    int cy = this.photoImage.getImageY2() + AndroidUtilities.dp(30.0f);
                    if (!this.locationExpired) {
                        this.forceNotDrawTime = true;
                        float progress = 1.0f - (((float) Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date)) / ((float) this.currentMessageObject.messageOwner.media.period));
                        this.rect.set((float) (this.photoImage.getImageX2() - AndroidUtilities.dp(43.0f)), (float) (cy - AndroidUtilities.dp(15.0f)), (float) (this.photoImage.getImageX2() - AndroidUtilities.dp(13.0f)), (float) (AndroidUtilities.dp(15.0f) + cy));
                        if (this.currentMessageObject.isOutOwner()) {
                            Theme.chat_radialProgress2Paint.setColor(Theme.getColor("chat_outInstant"));
                            Theme.chat_livePaint.setColor(Theme.getColor("chat_outInstant"));
                        } else {
                            Theme.chat_radialProgress2Paint.setColor(Theme.getColor("chat_inInstant"));
                            Theme.chat_livePaint.setColor(Theme.getColor("chat_inInstant"));
                        }
                        Theme.chat_radialProgress2Paint.setAlpha(50);
                        canvas.drawCircle(this.rect.centerX(), this.rect.centerY(), (float) AndroidUtilities.dp(15.0f), Theme.chat_radialProgress2Paint);
                        Theme.chat_radialProgress2Paint.setAlpha(255);
                        canvas.drawArc(this.rect, -90.0f, -360.0f * progress, false, Theme.chat_radialProgress2Paint);
                        text = LocaleController.formatLocationLeftTime(Math.abs(this.currentMessageObject.messageOwner.media.period - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date)));
                        canvas.drawText(text, this.rect.centerX() - (Theme.chat_livePaint.measureText(text) / 2.0f), (float) (AndroidUtilities.dp(4.0f) + cy), Theme.chat_livePaint);
                        canvas.save();
                        canvas.translate((float) (this.photoImage.getImageX() + AndroidUtilities.dp(10.0f)), (float) (this.photoImage.getImageY2() + AndroidUtilities.dp(10.0f)));
                        this.docTitleLayout.draw(canvas);
                        canvas.translate(0.0f, (float) AndroidUtilities.dp(23.0f));
                        this.infoLayout.draw(canvas);
                        canvas.restore();
                    }
                    int cx = (this.photoImage.getImageX() + (this.photoImage.getImageWidth() / 2)) - AndroidUtilities.dp(31.0f);
                    cy = (this.photoImage.getImageY() + (this.photoImage.getImageHeight() / 2)) - AndroidUtilities.dp(38.0f);
                    BaseCell.setDrawableBounds(Theme.chat_msgAvatarLiveLocationDrawable, cx, cy);
                    Theme.chat_msgAvatarLiveLocationDrawable.draw(canvas);
                    this.locationImageReceiver.setImageCoords(AndroidUtilities.dp(5.0f) + cx, AndroidUtilities.dp(5.0f) + cy, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
                    this.locationImageReceiver.draw(canvas);
                } else {
                    canvas.save();
                    canvas.translate((float) (this.photoImage.getImageX() + AndroidUtilities.dp(6.0f)), (float) (this.photoImage.getImageY2() + AndroidUtilities.dp(8.0f)));
                    this.docTitleLayout.draw(canvas);
                    if (this.infoLayout != null) {
                        canvas.translate(0.0f, (float) AndroidUtilities.dp(21.0f));
                        this.infoLayout.draw(canvas);
                    }
                    canvas.restore();
                }
            }
        } else if (this.currentMessageObject.type == 16) {
            Drawable icon;
            Drawable phone;
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_messageTextOut"));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText"));
            } else {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_messageTextIn"));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText"));
            }
            this.forceNotDrawTime = true;
            if (this.currentMessageObject.isOutOwner()) {
                x = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(16.0f);
            } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
                x = AndroidUtilities.dp(74.0f);
            } else {
                x = AndroidUtilities.dp(25.0f);
            }
            this.otherX = x;
            if (this.titleLayout != null) {
                canvas.save();
                canvas.translate((float) x, (float) (AndroidUtilities.dp(12.0f) + this.namesOffset));
                this.titleLayout.draw(canvas);
                canvas.restore();
            }
            if (this.docTitleLayout != null) {
                canvas.save();
                canvas.translate((float) (AndroidUtilities.dp(19.0f) + x), (float) (AndroidUtilities.dp(37.0f) + this.namesOffset));
                this.docTitleLayout.draw(canvas);
                canvas.restore();
            }
            if (this.currentMessageObject.isOutOwner()) {
                icon = Theme.chat_msgCallUpGreenDrawable;
                phone = (isDrawSelectedBackground() || this.otherPressed) ? Theme.chat_msgOutCallSelectedDrawable : Theme.chat_msgOutCallDrawable;
            } else {
                PhoneCallDiscardReason reason = this.currentMessageObject.messageOwner.action.reason;
                if ((reason instanceof TL_phoneCallDiscardReasonMissed) || (reason instanceof TL_phoneCallDiscardReasonBusy)) {
                    icon = Theme.chat_msgCallDownRedDrawable;
                } else {
                    icon = Theme.chat_msgCallDownGreenDrawable;
                }
                phone = (isDrawSelectedBackground() || this.otherPressed) ? Theme.chat_msgInCallSelectedDrawable : Theme.chat_msgInCallDrawable;
            }
            BaseCell.setDrawableBounds(icon, x - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(36.0f) + this.namesOffset);
            icon.draw(canvas);
            dp = AndroidUtilities.dp(205.0f) + x;
            imageX = AndroidUtilities.dp(22.0f);
            this.otherY = imageX;
            BaseCell.setDrawableBounds(phone, dp, imageX);
            phone.draw(canvas);
        } else if (this.currentMessageObject.type == 17) {
            int color;
            if (this.currentMessageObject.isOutOwner()) {
                color = Theme.getColor("chat_messageTextOut");
                Theme.chat_audioTitlePaint.setColor(color);
                Theme.chat_audioPerformerPaint.setColor(color);
                Theme.chat_instantViewPaint.setColor(color);
                color = Theme.getColor(isDrawSelectedBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText");
                Theme.chat_timePaint.setColor(color);
                Theme.chat_livePaint.setColor(color);
            } else {
                color = Theme.getColor("chat_messageTextIn");
                Theme.chat_audioTitlePaint.setColor(color);
                Theme.chat_audioPerformerPaint.setColor(color);
                Theme.chat_instantViewPaint.setColor(color);
                color = Theme.getColor(isDrawSelectedBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText");
                Theme.chat_timePaint.setColor(color);
                Theme.chat_livePaint.setColor(color);
            }
            if (this.currentMessageObject.isOutOwner()) {
                x = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(11.0f);
            } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
                x = AndroidUtilities.dp(68.0f);
            } else {
                x = AndroidUtilities.dp(20.0f);
            }
            if (this.titleLayout != null) {
                canvas.save();
                canvas.translate((float) x, (float) (AndroidUtilities.dp(15.0f) + this.namesOffset));
                this.titleLayout.draw(canvas);
                canvas.restore();
            }
            if (this.docTitleLayout != null) {
                canvas.save();
                canvas.translate((float) (this.docTitleOffsetX + x), (float) (((this.titleLayout != null ? this.titleLayout.getHeight() : 0) + AndroidUtilities.dp(20.0f)) + this.namesOffset));
                this.docTitleLayout.draw(canvas);
                canvas.restore();
            }
            if (VERSION.SDK_INT >= 21 && this.selectorDrawable != null) {
                this.selectorDrawable.draw(canvas);
            }
            int lastVoteY = 0;
            int a = 0;
            int N = this.pollButtons.size();
            while (a < N) {
                float min;
                PollButton button = (PollButton) this.pollButtons.get(a);
                button.x = x;
                canvas.save();
                canvas.translate((float) (AndroidUtilities.dp(34.0f) + x), (float) (button.y + this.namesOffset));
                button.title.draw(canvas);
                if (this.animatePollAnswerAlpha) {
                    min = Math.min((this.pollUnvoteInProgress ? 1.0f - this.pollAnimationProgress : this.pollAnimationProgress) / 0.3f, 1.0f) * 255.0f;
                } else {
                    min = 255.0f;
                }
                int alpha2 = (int) min;
                if (this.pollVoted || this.pollClosed || this.animatePollAnswerAlpha) {
                    Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outAudioSeekbarFill" : "chat_inAudioSeekbarFill"));
                    if (this.animatePollAnswerAlpha) {
                        Theme.chat_instantViewPaint.setAlpha((int) (((float) alpha2) * (((float) Theme.chat_instantViewPaint.getAlpha()) / 255.0f)));
                        Theme.chat_docBackPaint.setAlpha((int) (((float) alpha2) * (((float) Theme.chat_docBackPaint.getAlpha()) / 255.0f)));
                    }
                    text = String.format("%d%%", new Object[]{Integer.valueOf((int) Math.ceil((double) (((float) button.prevPercent) + (((float) (button.percent - button.prevPercent)) * this.pollAnimationProgress))))});
                    canvas.drawText(text, (float) ((-AndroidUtilities.dp(7.0f)) - ((int) Math.ceil((double) Theme.chat_instantViewPaint.measureText(text)))), (float) AndroidUtilities.dp(14.0f), Theme.chat_instantViewPaint);
                    this.instantButtonRect.set(0.0f, (float) (button.height + AndroidUtilities.dp(6.0f)), ((float) (this.backgroundWidth - AndroidUtilities.dp(76.0f))) * (button.prevPercentProgress + ((button.percentProgress - button.prevPercentProgress) * this.pollAnimationProgress)), (float) (button.height + AndroidUtilities.dp(11.0f)));
                    canvas.drawRoundRect(this.instantButtonRect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.chat_docBackPaint);
                }
                if (!(this.pollVoted || this.pollClosed) || this.animatePollAnswerAlpha) {
                    if (isDrawSelectedBackground()) {
                        Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outVoiceSeekbarSelected" : "chat_inVoiceSeekbarSelected"));
                    } else {
                        Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outVoiceSeekbar" : "chat_inVoiceSeekbar"));
                    }
                    if (this.animatePollAnswerAlpha) {
                        Theme.chat_replyLinePaint.setAlpha((int) (((float) (255 - alpha2)) * (((float) Theme.chat_replyLinePaint.getAlpha()) / 255.0f)));
                    }
                    canvas.drawLine((float) (-AndroidUtilities.dp(2.0f)), (float) (button.height + AndroidUtilities.dp(13.0f)), (float) (this.backgroundWidth - AndroidUtilities.dp(56.0f)), (float) (button.height + AndroidUtilities.dp(13.0f)), Theme.chat_replyLinePaint);
                    if (this.pollVoteInProgress && a == this.pollVoteInProgressNum) {
                        Theme.chat_instantViewRectPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outAudioSeekbarFill" : "chat_inAudioSeekbarFill"));
                        if (this.animatePollAnswerAlpha) {
                            Theme.chat_instantViewRectPaint.setAlpha((int) (((float) (255 - alpha2)) * (((float) Theme.chat_instantViewRectPaint.getAlpha()) / 255.0f)));
                        }
                        this.instantButtonRect.set((float) ((-AndroidUtilities.dp(23.0f)) - AndroidUtilities.dp(8.5f)), (float) (AndroidUtilities.dp(9.0f) - AndroidUtilities.dp(8.5f)), (float) ((-AndroidUtilities.dp(23.0f)) + AndroidUtilities.dp(8.5f)), (float) (AndroidUtilities.dp(9.0f) + AndroidUtilities.dp(8.5f)));
                        canvas.drawArc(this.instantButtonRect, this.voteRadOffset, this.voteCurrentCircleLength, false, Theme.chat_instantViewRectPaint);
                    } else {
                        if (this.currentMessageObject.isOutOwner()) {
                            Theme.chat_instantViewRectPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outMenuSelected" : "chat_outMenu"));
                        } else {
                            Theme.chat_instantViewRectPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inMenuSelected" : "chat_inMenu"));
                        }
                        if (this.animatePollAnswerAlpha) {
                            Theme.chat_instantViewRectPaint.setAlpha((int) (((float) (255 - alpha2)) * (((float) Theme.chat_instantViewRectPaint.getAlpha()) / 255.0f)));
                        }
                        canvas.drawCircle((float) (-AndroidUtilities.dp(23.0f)), (float) AndroidUtilities.dp(9.0f), (float) AndroidUtilities.dp(8.5f), Theme.chat_instantViewRectPaint);
                    }
                }
                canvas.restore();
                if (a == N - 1) {
                    lastVoteY = (button.y + this.namesOffset) + button.height;
                }
                a++;
            }
            if (this.infoLayout != null) {
                canvas.save();
                canvas.translate((float) (this.infoX + x), (float) (AndroidUtilities.dp(22.0f) + lastVoteY));
                this.infoLayout.draw(canvas);
                canvas.restore();
            }
            updatePollAnimations();
        } else if (this.currentMessageObject.type == 12) {
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_contactNamePaint.setColor(Theme.getColor("chat_outContactNameText"));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_outContactPhoneSelectedText" : "chat_outContactPhoneText"));
            } else {
                Theme.chat_contactNamePaint.setColor(Theme.getColor("chat_inContactNameText"));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inContactPhoneSelectedText" : "chat_inContactPhoneText"));
            }
            if (this.titleLayout != null) {
                canvas.save();
                canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(16.0f) + this.namesOffset));
                this.titleLayout.draw(canvas);
                canvas.restore();
            }
            if (this.docTitleLayout != null) {
                canvas.save();
                canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(39.0f) + this.namesOffset));
                this.docTitleLayout.draw(canvas);
                canvas.restore();
            }
            Drawable menuDrawable = this.currentMessageObject.isOutOwner() ? isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable : isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            dp = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(48.0f);
            this.otherX = dp;
            imageX = this.photoImage.getImageY() - AndroidUtilities.dp(5.0f);
            this.otherY = imageX;
            BaseCell.setDrawableBounds(menuDrawable, dp, imageX);
            menuDrawable.draw(canvas);
            if (this.drawInstantView) {
                int textX = this.photoImage.getImageX() - AndroidUtilities.dp(2.0f);
                int instantY = getMeasuredHeight() - AndroidUtilities.dp(64.0f);
                Paint backPaint = Theme.chat_instantViewRectPaint;
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_instantViewPaint.setColor(Theme.getColor("chat_outPreviewInstantText"));
                    backPaint.setColor(Theme.getColor("chat_outPreviewInstantText"));
                } else {
                    Theme.chat_instantViewPaint.setColor(Theme.getColor("chat_inPreviewInstantText"));
                    backPaint.setColor(Theme.getColor("chat_inPreviewInstantText"));
                }
                if (VERSION.SDK_INT >= 21) {
                    this.selectorDrawable.setBounds(textX, instantY, this.instantWidth + textX, AndroidUtilities.dp(36.0f) + instantY);
                    this.selectorDrawable.draw(canvas);
                }
                this.instantButtonRect.set((float) textX, (float) instantY, (float) (this.instantWidth + textX), (float) (AndroidUtilities.dp(36.0f) + instantY));
                canvas.drawRoundRect(this.instantButtonRect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), backPaint);
                if (this.instantViewLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.instantTextX + textX), (float) (AndroidUtilities.dp(10.5f) + instantY));
                    this.instantViewLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
        if (this.drawImageButton && this.photoImage.getVisible()) {
            if (this.controlsAlpha != 1.0f) {
                this.radialProgress.setOverrideAlpha(this.controlsAlpha);
            }
            this.radialProgress.draw(canvas);
        }
        if ((this.drawVideoImageButton || this.animatingDrawVideoImageButton != 0) && this.photoImage.getVisible()) {
            if (this.controlsAlpha != 1.0f) {
                this.videoRadialProgress.setOverrideAlpha(this.controlsAlpha);
            }
            this.videoRadialProgress.draw(canvas);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public Document getStreamingVideo() {
        return this.documentAttachType == 4 ? this.documentAttach : null;
    }

    public boolean isPinnedBottom() {
        return this.pinnedBottom;
    }

    public boolean isPinnedTop() {
        return this.pinnedTop;
    }

    public GroupedMessages getCurrentMessagesGroup() {
        return this.currentMessagesGroup;
    }

    public GroupedMessagePosition getCurrentPosition() {
        return this.currentPosition;
    }

    public int getLayoutHeight() {
        return this.layoutHeight;
    }
}
