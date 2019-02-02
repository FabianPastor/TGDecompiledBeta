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
import org.telegram.tgnet.TLRPC.TL_poll;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
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
    private boolean attachedToWindow;
    private StaticLayout authorLayout;
    private int authorX;
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
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private boolean cancelLoading;
    private int captionHeight;
    private StaticLayout captionLayout;
    private int captionOffsetX;
    private int captionWidth;
    private int captionX;
    private int captionY;
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
    private StaticLayout videoInfoLayout;
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
            if (this.drawPhotoImage && this.photoImage.isInsideImage((float) x, (float) y)) {
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
            if (this.pressedLinkType != 2 && !this.gamePreviewPressed) {
                resetPressedLink(2);
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
                        } else if (this.drawPhotoImage && this.drawImageButton && this.buttonState != -1 && (this.photoImage.isInsideImage((float) x, (float) y) || (x >= this.buttonX && x <= this.buttonX + AndroidUtilities.dp(48.0f) && y >= this.buttonY && y <= this.buttonY + AndroidUtilities.dp(48.0f)))) {
                            this.buttonPressed = 1;
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
                    } else if (this.pressedLinkType != 2 && this.buttonPressed == 0 && this.miniButtonPressed == 0 && !this.linkPreviewPressed) {
                        resetPressedLink(2);
                    } else if (this.buttonPressed != 0) {
                        this.buttonPressed = 0;
                        playSoundEffect(0);
                        didPressButton(true);
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
                            didPressButton(true);
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
            } else if (this.buttonState != -1 && x >= this.buttonX && x <= this.buttonX + side && y >= this.buttonY && y <= this.buttonY + side) {
                this.buttonPressed = 1;
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
            if (this.buttonPressed == 1) {
                this.buttonPressed = 0;
                playSoundEffect(0);
                didPressButton(true);
                invalidate();
            } else if (this.miniButtonPressed == 1) {
                this.miniButtonPressed = 0;
                playSoundEffect(0);
                didPressMiniButton(true);
                invalidate();
            } else if (this.imagePressed) {
                this.imagePressed = false;
                if (this.buttonState == -1 || this.buttonState == 2 || this.buttonState == 3) {
                    playSoundEffect(0);
                    didClickedImage();
                } else if (this.buttonState == 0 && this.documentAttachType == 1) {
                    playSoundEffect(0);
                    didPressButton(true);
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
                didPressButton(true);
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
                didPressButton(true);
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
            if (this.currentMessageObject.isRoundVideo()) {
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

    public void downloadAudioIfNeed() {
        if (this.documentAttachType == 3 && this.buttonState == 2) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
            this.buttonState = 4;
            this.radialProgress.setIcon(getIconForCurrentState(), false, false);
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
        return StaticLayoutEx.createStaticLayout(stringBuilder, paint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.dp(1.0f), false, TruncateAt.END, maxWidth, maxLines);
    }

    private void didClickedImage() {
        if (this.currentMessageObject.type == 1 || this.currentMessageObject.type == 13) {
            if (this.buttonState == -1) {
                this.delegate.didPressImage(this);
            } else if (this.buttonState == 0) {
                didPressButton(true);
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
                didPressButton(true);
            }
        } else if (this.documentAttachType == 4) {
            if (this.buttonState == -1) {
                this.delegate.didPressImage(this);
            } else if (this.buttonState == 0 || this.buttonState == 3) {
                didPressButton(true);
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

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
        this.radialProgress.onDetachedFromWindow();
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
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        setTranslationX(0.0f);
        this.radialProgress.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        this.avatarImage.setParentView((View) getParent());
        this.replyImageReceiver.onAttachedToWindow();
        this.locationImageReceiver.onAttachedToWindow();
        if (!this.photoImage.onAttachedToWindow()) {
            updateButtonState(false, false, false);
        } else if (this.drawPhotoImage) {
            updateButtonState(false, false, false);
        }
        if (this.currentMessageObject != null && this.currentMessageObject.isRoundVideo()) {
            checkRoundVideoPlayback(true);
        }
    }

    public void checkRoundVideoPlayback(boolean allowStart) {
        if (allowStart) {
            allowStart = MediaController.getInstance().getPlayingMessageObject() == null;
        }
        this.photoImage.setAllowStartAnimation(allowStart);
        if (allowStart) {
            this.photoImage.startAnimation();
        } else {
            this.photoImage.stopAnimation();
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
        if (!(this.buttonPressed == 0 && this.miniButtonPressed == 0 && this.pressedBotButton == -1)) {
            this.buttonPressed = 0;
            this.miniButtonState = 0;
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
        boolean z = false;
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
                RadialProgress2 radialProgress22 = this.radialProgress;
                if (this.isHighlighted || this.isPressed || this.miniButtonPressed != 0 || isPressed()) {
                    z = true;
                }
                radialProgress22.setPressed(z, true);
            }
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
                duration = 0;
                for (a = 0; a < this.documentAttach.attributes.size(); a++) {
                    attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                    if (attribute instanceof TL_documentAttributeVideo) {
                        duration = attribute.duration;
                        break;
                    }
                }
                int seconds = duration - ((duration / 60) * 60);
                str = String.format("%d:%02d, %s", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.documentAttach.size)});
                this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(str));
                this.infoLayout = new StaticLayout(str, Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
        if (this.currentMessageObject.messageOwner.message != null && this.currentMessageObject != null && this.currentMessageObject.type == 0 && !TextUtils.isEmpty(this.currentMessageObject.messageText) && text != null) {
            int start = TextUtils.indexOf(this.currentMessageObject.messageOwner.message.toLowerCase(), text.toLowerCase());
            if (start != -1) {
                int end = start + text.length();
                int c = 0;
                while (c < this.currentMessageObject.textLayoutBlocks.size()) {
                    TextLayoutBlock block = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(c);
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
                                for (int a = c + 1; a < this.currentMessageObject.textLayoutBlocks.size(); a++) {
                                    TextLayoutBlock nextBlock = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a);
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

    /* JADX WARNING: Removed duplicated region for block: B:269:0x0793  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x07b1  */
    /* JADX WARNING: Removed duplicated region for block: B:876:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:868:0x1bfc  */
    /* JADX WARNING: Removed duplicated region for block: B:877:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:871:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:874:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x102c  */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x1050  */
    /* JADX WARNING: Removed duplicated region for block: B:900:0x1cad  */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x105c  */
    /* JADX WARNING: Removed duplicated region for block: B:901:0x1cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x1072  */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x108c  */
    /* JADX WARNING: Removed duplicated region for block: B:923:0x1d6f  */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x10ab  */
    /* JADX WARNING: Removed duplicated region for block: B:925:0x1d9b  */
    /* JADX WARNING: Removed duplicated region for block: B:596:0x1156  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x117b  */
    /* JADX WARNING: Removed duplicated region for block: B:972:0x1ff5  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0e3a  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x165e  */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0fa2  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x1bde  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0faa  */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x0fea  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x102c  */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x1050  */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x105c  */
    /* JADX WARNING: Removed duplicated region for block: B:900:0x1cad  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x1072  */
    /* JADX WARNING: Removed duplicated region for block: B:901:0x1cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x108c  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x109a  */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x10ab  */
    /* JADX WARNING: Removed duplicated region for block: B:923:0x1d6f  */
    /* JADX WARNING: Removed duplicated region for block: B:591:0x10f9  */
    /* JADX WARNING: Removed duplicated region for block: B:596:0x1156  */
    /* JADX WARNING: Removed duplicated region for block: B:925:0x1d9b  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x117b  */
    /* JADX WARNING: Removed duplicated region for block: B:972:0x1ff5  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x11d9  */
    /* JADX WARNING: Removed duplicated region for block: B:2023:0x44da A:{SYNTHETIC, Splitter: B:2023:0x44da} */
    /* JADX WARNING: Removed duplicated region for block: B:636:0x1348 A:{Catch:{ Exception -> 0x44f9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x137b A:{SYNTHETIC, Splitter: B:639:0x137b} */
    /* JADX WARNING: Removed duplicated region for block: B:2030:0x4508  */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x14e8 A:{Catch:{ Exception -> 0x450b }} */
    /* JADX WARNING: Removed duplicated region for block: B:674:0x151e A:{Catch:{ Exception -> 0x4522 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x1588 A:{Catch:{ Exception -> 0x4522 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2039:0x4537  */
    /* JADX WARNING: Removed duplicated region for block: B:2044:0x4563  */
    /* JADX WARNING: Removed duplicated region for block: B:2135:0x48ce  */
    /* JADX WARNING: Removed duplicated region for block: B:2134:0x48bd  */
    /* JADX WARNING: Removed duplicated region for block: B:2118:0x4865  */
    /* JADX WARNING: Removed duplicated region for block: B:2148:0x491b  */
    /* JADX WARNING: Removed duplicated region for block: B:2121:0x4876  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0e3a  */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0fa2  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x165e  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0faa  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x1bde  */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x0fe6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x0fea  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x102c  */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x1050  */
    /* JADX WARNING: Removed duplicated region for block: B:900:0x1cad  */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x105c  */
    /* JADX WARNING: Removed duplicated region for block: B:901:0x1cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x1072  */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x108c  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x109a  */
    /* JADX WARNING: Removed duplicated region for block: B:923:0x1d6f  */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x10ab  */
    /* JADX WARNING: Removed duplicated region for block: B:591:0x10f9  */
    /* JADX WARNING: Removed duplicated region for block: B:925:0x1d9b  */
    /* JADX WARNING: Removed duplicated region for block: B:596:0x1156  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x117b  */
    /* JADX WARNING: Removed duplicated region for block: B:972:0x1ff5  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x11d9  */
    /* JADX WARNING: Removed duplicated region for block: B:636:0x1348 A:{Catch:{ Exception -> 0x44f9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2023:0x44da A:{SYNTHETIC, Splitter: B:2023:0x44da} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x137b A:{SYNTHETIC, Splitter: B:639:0x137b} */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x14e8 A:{Catch:{ Exception -> 0x450b }} */
    /* JADX WARNING: Removed duplicated region for block: B:2030:0x4508  */
    /* JADX WARNING: Removed duplicated region for block: B:674:0x151e A:{Catch:{ Exception -> 0x4522 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x1588 A:{Catch:{ Exception -> 0x4522 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2039:0x4537  */
    /* JADX WARNING: Removed duplicated region for block: B:2044:0x4563  */
    /* JADX WARNING: Removed duplicated region for block: B:2047:0x457c  */
    /* JADX WARNING: Removed duplicated region for block: B:2108:0x482c  */
    /* JADX WARNING: Removed duplicated region for block: B:2134:0x48bd  */
    /* JADX WARNING: Removed duplicated region for block: B:2135:0x48ce  */
    /* JADX WARNING: Removed duplicated region for block: B:2118:0x4865  */
    /* JADX WARNING: Removed duplicated region for block: B:2121:0x4876  */
    /* JADX WARNING: Removed duplicated region for block: B:2148:0x491b  */
    /* JADX WARNING: Removed duplicated region for block: B:2126:0x489d  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0e3a  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x165e  */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0fa2  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x1bde  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0faa  */
    /* JADX WARNING: Removed duplicated region for block: B:547:0x0fe6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x0fea  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x102c  */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x1050  */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x105c  */
    /* JADX WARNING: Removed duplicated region for block: B:900:0x1cad  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x1072  */
    /* JADX WARNING: Removed duplicated region for block: B:901:0x1cb0  */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x108c  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x109a  */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x10ab  */
    /* JADX WARNING: Removed duplicated region for block: B:923:0x1d6f  */
    /* JADX WARNING: Removed duplicated region for block: B:591:0x10f9  */
    /* JADX WARNING: Removed duplicated region for block: B:596:0x1156  */
    /* JADX WARNING: Removed duplicated region for block: B:925:0x1d9b  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x117b  */
    /* JADX WARNING: Removed duplicated region for block: B:972:0x1ff5  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x11d9  */
    /* JADX WARNING: Removed duplicated region for block: B:2023:0x44da A:{SYNTHETIC, Splitter: B:2023:0x44da} */
    /* JADX WARNING: Removed duplicated region for block: B:636:0x1348 A:{Catch:{ Exception -> 0x44f9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x137b A:{SYNTHETIC, Splitter: B:639:0x137b} */
    /* JADX WARNING: Removed duplicated region for block: B:2030:0x4508  */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x14e8 A:{Catch:{ Exception -> 0x450b }} */
    /* JADX WARNING: Removed duplicated region for block: B:674:0x151e A:{Catch:{ Exception -> 0x4522 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x1588 A:{Catch:{ Exception -> 0x4522 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2039:0x4537  */
    /* JADX WARNING: Removed duplicated region for block: B:2044:0x4563  */
    /* JADX WARNING: Removed duplicated region for block: B:2047:0x457c  */
    /* JADX WARNING: Removed duplicated region for block: B:2108:0x482c  */
    /* JADX WARNING: Removed duplicated region for block: B:2135:0x48ce  */
    /* JADX WARNING: Removed duplicated region for block: B:2134:0x48bd  */
    /* JADX WARNING: Removed duplicated region for block: B:2118:0x4865  */
    /* JADX WARNING: Removed duplicated region for block: B:2148:0x491b  */
    /* JADX WARNING: Removed duplicated region for block: B:2121:0x4876  */
    /* JADX WARNING: Removed duplicated region for block: B:2126:0x489d  */
    /* JADX WARNING: Missing block: B:259:0x0776, code:
            if (r175.equals("article") != false) goto L_0x0778;
     */
    /* JADX WARNING: Missing block: B:272:0x07a7, code:
            if (r175.equals("article") != false) goto L_0x07a9;
     */
    /* JADX WARNING: Missing block: B:388:0x0b4c, code:
            if ("telegram_album".equals(r182) == false) goto L_0x058d;
     */
    /* JADX WARNING: Missing block: B:560:0x101f, code:
            if (r188.documentAttachType != 8) goto L_0x1c4e;
     */
    public void setMessageObject(org.telegram.messenger.MessageObject r189, org.telegram.messenger.MessageObject.GroupedMessages r190, boolean r191, boolean r192) {
        /*
        r188 = this;
        r4 = r189.checkLayout();
        if (r4 != 0) goto L_0x0016;
    L_0x0006:
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x001b;
    L_0x000c:
        r0 = r188;
        r4 = r0.lastHeight;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        if (r4 == r6) goto L_0x001b;
    L_0x0016:
        r4 = 0;
        r0 = r188;
        r0.currentMessageObject = r4;
    L_0x001b:
        r0 = r188;
        r4 = r0.currentMessageObject;
        if (r4 == 0) goto L_0x002f;
    L_0x0021:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.getId();
        r6 = r189.getId();
        if (r4 == r6) goto L_0x0955;
    L_0x002f:
        r124 = 1;
    L_0x0031:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r0 = r189;
        if (r4 != r0) goto L_0x003f;
    L_0x0039:
        r0 = r189;
        r4 = r0.forceUpdate;
        if (r4 == 0) goto L_0x0959;
    L_0x003f:
        r123 = 1;
    L_0x0041:
        r0 = r188;
        r4 = r0.currentMessageObject;
        if (r4 == 0) goto L_0x0062;
    L_0x0047:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.getId();
        r6 = r189.getId();
        if (r4 != r6) goto L_0x0062;
    L_0x0055:
        r0 = r188;
        r4 = r0.lastSendState;
        r6 = 3;
        if (r4 != r6) goto L_0x0062;
    L_0x005c:
        r4 = r189.isSent();
        if (r4 != 0) goto L_0x0076;
    L_0x0062:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r0 = r189;
        if (r4 != r0) goto L_0x095d;
    L_0x006a:
        r4 = r188.isUserDataChanged();
        if (r4 != 0) goto L_0x0076;
    L_0x0070:
        r0 = r188;
        r4 = r0.photoNotSet;
        if (r4 == 0) goto L_0x095d;
    L_0x0076:
        r84 = 1;
    L_0x0078:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r0 = r190;
        if (r0 == r4) goto L_0x0961;
    L_0x0080:
        r96 = 1;
    L_0x0082:
        r143 = 0;
        if (r123 != 0) goto L_0x0112;
    L_0x0086:
        r0 = r189;
        r4 = r0.type;
        r6 = 17;
        if (r4 != r6) goto L_0x0112;
    L_0x008e:
        r130 = 0;
        r128 = 0;
        r131 = 0;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r4 == 0) goto L_0x00be;
    L_0x009e:
        r0 = r189;
        r4 = r0.messageOwner;
        r0 = r4.media;
        r122 = r0;
        r122 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r122;
        r0 = r122;
        r4 = r0.results;
        r0 = r4.results;
        r130 = r0;
        r0 = r122;
        r0 = r0.poll;
        r128 = r0;
        r0 = r122;
        r4 = r0.results;
        r0 = r4.total_voters;
        r131 = r0;
    L_0x00be:
        if (r130 == 0) goto L_0x00d0;
    L_0x00c0:
        r0 = r188;
        r4 = r0.lastPollResults;
        if (r4 == 0) goto L_0x00d0;
    L_0x00c6:
        r0 = r188;
        r4 = r0.lastPollResultsVoters;
        r0 = r131;
        if (r0 == r4) goto L_0x00d0;
    L_0x00ce:
        r143 = 1;
    L_0x00d0:
        if (r143 != 0) goto L_0x00dc;
    L_0x00d2:
        r0 = r188;
        r4 = r0.lastPollResults;
        r0 = r130;
        if (r0 == r4) goto L_0x00dc;
    L_0x00da:
        r143 = 1;
    L_0x00dc:
        if (r143 != 0) goto L_0x00f4;
    L_0x00de:
        r0 = r188;
        r4 = r0.lastPoll;
        r0 = r128;
        if (r4 == r0) goto L_0x00f4;
    L_0x00e6:
        r0 = r188;
        r4 = r0.lastPoll;
        r4 = r4.closed;
        r0 = r128;
        r6 = r0.closed;
        if (r4 == r6) goto L_0x00f4;
    L_0x00f2:
        r143 = 1;
    L_0x00f4:
        if (r143 == 0) goto L_0x0112;
    L_0x00f6:
        r0 = r188;
        r4 = r0.attachedToWindow;
        if (r4 == 0) goto L_0x0112;
    L_0x00fc:
        r4 = 0;
        r0 = r188;
        r0.pollAnimationProgressTime = r4;
        r0 = r188;
        r4 = r0.pollVoted;
        if (r4 == 0) goto L_0x0112;
    L_0x0107:
        r4 = r189.isVoted();
        if (r4 != 0) goto L_0x0112;
    L_0x010d:
        r4 = 1;
        r0 = r188;
        r0.pollUnvoteInProgress = r4;
    L_0x0112:
        if (r96 != 0) goto L_0x013b;
    L_0x0114:
        if (r190 == 0) goto L_0x013b;
    L_0x0116:
        r0 = r190;
        r4 = r0.messages;
        r4 = r4.size();
        r6 = 1;
        if (r4 <= r6) goto L_0x0965;
    L_0x0121:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.positions;
        r0 = r188;
        r6 = r0.currentMessageObject;
        r129 = r4.get(r6);
        r129 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r129;
    L_0x0131:
        r0 = r188;
        r4 = r0.currentPosition;
        r0 = r129;
        if (r0 == r4) goto L_0x0969;
    L_0x0139:
        r96 = 1;
    L_0x013b:
        if (r123 != 0) goto L_0x0159;
    L_0x013d:
        if (r84 != 0) goto L_0x0159;
    L_0x013f:
        if (r96 != 0) goto L_0x0159;
    L_0x0141:
        if (r143 != 0) goto L_0x0159;
    L_0x0143:
        r4 = r188.isPhotoDataChanged(r189);
        if (r4 != 0) goto L_0x0159;
    L_0x0149:
        r0 = r188;
        r4 = r0.pinnedBottom;
        r0 = r191;
        if (r4 != r0) goto L_0x0159;
    L_0x0151:
        r0 = r188;
        r4 = r0.pinnedTop;
        r0 = r192;
        if (r4 == r0) goto L_0x4897;
    L_0x0159:
        r0 = r191;
        r1 = r188;
        r1.pinnedBottom = r0;
        r0 = r192;
        r1 = r188;
        r1.pinnedTop = r0;
        r4 = -2;
        r0 = r188;
        r0.lastTime = r4;
        r4 = 0;
        r0 = r188;
        r0.isHighlightedAnimated = r4;
        r4 = -1;
        r0 = r188;
        r0.widthBeforeNewTimeLine = r4;
        r0 = r189;
        r1 = r188;
        r1.currentMessageObject = r0;
        r0 = r190;
        r1 = r188;
        r1.currentMessagesGroup = r0;
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        if (r4 == 0) goto L_0x096d;
    L_0x0186:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r6 = 1;
        if (r4 <= r6) goto L_0x096d;
    L_0x0193:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.positions;
        r0 = r188;
        r6 = r0.currentMessageObject;
        r4 = r4.get(r6);
        r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4;
        r0 = r188;
        r0.currentPosition = r4;
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 != 0) goto L_0x01b2;
    L_0x01ad:
        r4 = 0;
        r0 = r188;
        r0.currentMessagesGroup = r4;
    L_0x01b2:
        r0 = r188;
        r4 = r0.pinnedTop;
        if (r4 == 0) goto L_0x0979;
    L_0x01b8:
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x01c8;
    L_0x01be:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x0979;
    L_0x01c8:
        r4 = 1;
    L_0x01c9:
        r0 = r188;
        r0.drawPinnedTop = r4;
        r0 = r188;
        r4 = r0.pinnedBottom;
        if (r4 == 0) goto L_0x097c;
    L_0x01d3:
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x01e3;
    L_0x01d9:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 == 0) goto L_0x097c;
    L_0x01e3:
        r4 = 1;
    L_0x01e4:
        r0 = r188;
        r0.drawPinnedBottom = r4;
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setCrossfadeWithOldImage(r6);
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.send_state;
        r0 = r188;
        r0.lastSendState = r4;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.destroyTime;
        r0 = r188;
        r0.lastDeleteDate = r4;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.views;
        r0 = r188;
        r0.lastViewsCount = r4;
        r4 = 0;
        r0 = r188;
        r0.isPressed = r4;
        r4 = 0;
        r0 = r188;
        r0.gamePreviewPressed = r4;
        r4 = 1;
        r0 = r188;
        r0.isCheckPressed = r4;
        r4 = 0;
        r0 = r188;
        r0.hasNewLineForTime = r4;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x097f;
    L_0x0228:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x097f;
    L_0x022e:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x097f;
    L_0x0234:
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x0242;
    L_0x023a:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.edge;
        if (r4 == 0) goto L_0x097f;
    L_0x0242:
        r4 = 1;
    L_0x0243:
        r0 = r188;
        r0.isAvatarVisible = r4;
        r4 = 0;
        r0 = r188;
        r0.wasLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.drwaShareGoIcon = r4;
        r4 = 0;
        r0 = r188;
        r0.groupPhotoInvisible = r4;
        r4 = r188.checkNeedDrawShareButton(r189);
        r0 = r188;
        r0.drawShareButton = r4;
        r4 = 0;
        r0 = r188;
        r0.replyNameLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.adminLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.replyTextLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.hasEmbed = r4;
        r4 = 0;
        r0 = r188;
        r0.replyNameWidth = r4;
        r4 = 0;
        r0 = r188;
        r0.replyTextWidth = r4;
        r4 = 0;
        r0 = r188;
        r0.viaWidth = r4;
        r4 = 0;
        r0 = r188;
        r0.viaNameWidth = r4;
        r4 = 0;
        r0 = r188;
        r0.addedCaptionHeight = r4;
        r4 = 0;
        r0 = r188;
        r0.currentReplyPhoto = r4;
        r4 = 0;
        r0 = r188;
        r0.currentUser = r4;
        r4 = 0;
        r0 = r188;
        r0.currentChat = r4;
        r4 = 0;
        r0 = r188;
        r0.currentViaBotUser = r4;
        r4 = 0;
        r0 = r188;
        r0.instantViewLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.drawNameLayout = r4;
        r0 = r188;
        r4 = r0.scheduledInvalidate;
        if (r4 == 0) goto L_0x02bb;
    L_0x02af:
        r0 = r188;
        r4 = r0.invalidateRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4);
        r4 = 0;
        r0 = r188;
        r0.scheduledInvalidate = r4;
    L_0x02bb:
        r4 = -1;
        r0 = r188;
        r0.resetPressedLink(r4);
        r4 = 0;
        r0 = r189;
        r0.forceUpdate = r4;
        r4 = 0;
        r0 = r188;
        r0.drawPhotoImage = r4;
        r4 = 0;
        r0 = r188;
        r0.hasLinkPreview = r4;
        r4 = 0;
        r0 = r188;
        r0.hasOldCaptionPreview = r4;
        r4 = 0;
        r0 = r188;
        r0.hasGamePreview = r4;
        r4 = 0;
        r0 = r188;
        r0.hasInvoicePreview = r4;
        r4 = 0;
        r0 = r188;
        r0.instantButtonPressed = r4;
        r0 = r188;
        r0.instantPressed = r4;
        if (r143 != 0) goto L_0x0308;
    L_0x02ea:
        r4 = android.os.Build.VERSION.SDK_INT;
        r6 = 21;
        if (r4 < r6) goto L_0x0308;
    L_0x02f0:
        r0 = r188;
        r4 = r0.selectorDrawable;
        if (r4 == 0) goto L_0x0308;
    L_0x02f6:
        r0 = r188;
        r4 = r0.selectorDrawable;
        r6 = 0;
        r8 = 0;
        r4.setVisible(r6, r8);
        r0 = r188;
        r4 = r0.selectorDrawable;
        r6 = android.util.StateSet.NOTHING;
        r4.setState(r6);
    L_0x0308:
        r4 = 0;
        r0 = r188;
        r0.linkPreviewPressed = r4;
        r4 = 0;
        r0 = r188;
        r0.buttonPressed = r4;
        r4 = 0;
        r0 = r188;
        r0.miniButtonPressed = r4;
        r4 = -1;
        r0 = r188;
        r0.pressedBotButton = r4;
        r4 = -1;
        r0 = r188;
        r0.pressedVoteButton = r4;
        r4 = 0;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        r4 = 0;
        r0 = r188;
        r0.mediaOffsetY = r4;
        r4 = 0;
        r0 = r188;
        r0.documentAttachType = r4;
        r4 = 0;
        r0 = r188;
        r0.documentAttach = r4;
        r4 = 0;
        r0 = r188;
        r0.descriptionLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.titleLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.videoInfoLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.photosCountLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.siteNameLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.authorLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.captionLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.captionOffsetX = r4;
        r4 = 0;
        r0 = r188;
        r0.currentCaption = r4;
        r4 = 0;
        r0 = r188;
        r0.docTitleLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.drawImageButton = r4;
        r4 = 0;
        r0 = r188;
        r0.currentPhotoObject = r4;
        r4 = 0;
        r0 = r188;
        r0.currentPhotoObjectThumb = r4;
        r4 = 0;
        r0 = r188;
        r0.currentPhotoFilter = r4;
        r4 = 0;
        r0 = r188;
        r0.infoLayout = r4;
        r4 = 0;
        r0 = r188;
        r0.cancelLoading = r4;
        r4 = -1;
        r0 = r188;
        r0.buttonState = r4;
        r4 = -1;
        r0 = r188;
        r0.miniButtonState = r4;
        r4 = 0;
        r0 = r188;
        r0.hasMiniProgress = r4;
        r0 = r188;
        r4 = r0.addedForTest;
        if (r4 == 0) goto L_0x03b1;
    L_0x039a:
        r0 = r188;
        r4 = r0.currentUrl;
        if (r4 == 0) goto L_0x03b1;
    L_0x03a0:
        r0 = r188;
        r4 = r0.currentWebFile;
        if (r4 == 0) goto L_0x03b1;
    L_0x03a6:
        r4 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r188;
        r6 = r0.currentUrl;
        r4.removeTestWebFile(r6);
    L_0x03b1:
        r4 = 0;
        r0 = r188;
        r0.addedForTest = r4;
        r4 = 0;
        r0 = r188;
        r0.currentUrl = r4;
        r4 = 0;
        r0 = r188;
        r0.currentWebFile = r4;
        r4 = 0;
        r0 = r188;
        r0.photoNotSet = r4;
        r4 = 1;
        r0 = r188;
        r0.drawBackground = r4;
        r4 = 0;
        r0 = r188;
        r0.drawName = r4;
        r4 = 0;
        r0 = r188;
        r0.useSeekBarWaweform = r4;
        r4 = 0;
        r0 = r188;
        r0.drawInstantView = r4;
        r4 = 0;
        r0 = r188;
        r0.drawInstantViewType = r4;
        r4 = 0;
        r0 = r188;
        r0.drawForwardedName = r4;
        r4 = 0;
        r0 = r188;
        r0.imageBackgroundColor = r4;
        r4 = 0;
        r0 = r188;
        r0.imageBackgroundSideColor = r4;
        r4 = 0;
        r0 = r188;
        r0.mediaBackground = r4;
        r0 = r188;
        r4 = r0.photoImage;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4.setAlpha(r6);
        if (r123 != 0) goto L_0x03ff;
    L_0x03fd:
        if (r84 == 0) goto L_0x0406;
    L_0x03ff:
        r0 = r188;
        r4 = r0.pollButtons;
        r4.clear();
    L_0x0406:
        r77 = 0;
        r4 = 0;
        r0 = r188;
        r0.availableTimeWidth = r4;
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setForceLoading(r6);
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setNeedsQualityThumb(r6);
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setShouldGenerateQualityThumb(r6);
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setAllowDecodeSingleFrame(r6);
        r0 = r188;
        r4 = r0.photoImage;
        r6 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setRoundRadius(r6);
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setColorFilter(r6);
        if (r123 == 0) goto L_0x0453;
    L_0x0444:
        r4 = 0;
        r0 = r188;
        r0.firstVisibleBlockNum = r4;
        r4 = 0;
        r0 = r188;
        r0.lastVisibleBlockNum = r4;
        r4 = 1;
        r0 = r188;
        r0.needNewVisiblePart = r4;
    L_0x0453:
        r0 = r189;
        r4 = r0.type;
        if (r4 != 0) goto L_0x20ae;
    L_0x0459:
        r4 = 1;
        r0 = r188;
        r0.drawForwardedName = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x09a7;
    L_0x0464:
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x0982;
    L_0x046a:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x0982;
    L_0x0470:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x0982;
    L_0x0476:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x42var_ float:122.0 double:5.54977537E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r4 = 1;
        r0 = r188;
        r0.drawName = r4;
    L_0x0487:
        r0 = r43;
        r1 = r188;
        r1.availableTimeWidth = r0;
        r4 = r189.isRoundVideo();
        if (r4 == 0) goto L_0x04bb;
    L_0x0493:
        r0 = r188;
        r4 = r0.availableTimeWidth;
        r8 = (double) r4;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r6 = "00:00";
        r4 = r4.measureText(r6);
        r0 = (double) r4;
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x0a01;
    L_0x04ae:
        r4 = 0;
    L_0x04af:
        r0 = (double) r4;
        r24 = r0;
        r20 = r20 + r24;
        r8 = r8 - r20;
        r4 = (int) r8;
        r0 = r188;
        r0.availableTimeWidth = r4;
    L_0x04bb:
        r188.measureTime(r189);
        r0 = r188;
        r4 = r0.timeWidth;
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r170 = r4 + r6;
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x04d8;
    L_0x04d0:
        r4 = NUM; // 0x41a40000 float:20.5 double:5.44098164E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r170 = r170 + r4;
    L_0x04d8:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r4 == 0) goto L_0x0a09;
    L_0x04e2:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.game;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_game;
        if (r4 == 0) goto L_0x0a09;
    L_0x04ee:
        r4 = 1;
    L_0x04ef:
        r0 = r188;
        r0.hasGamePreview = r4;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
        r0 = r188;
        r0.hasInvoicePreview = r4;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r4 == 0) goto L_0x0a0c;
    L_0x0509:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r4 == 0) goto L_0x0a0c;
    L_0x0515:
        r4 = 1;
    L_0x0516:
        r0 = r188;
        r0.hasLinkPreview = r4;
        r0 = r188;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0a0f;
    L_0x0520:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.cached_page;
        if (r4 == 0) goto L_0x0a0f;
    L_0x052c:
        r4 = 1;
    L_0x052d:
        r0 = r188;
        r0.drawInstantView = r4;
        r0 = r188;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0a12;
    L_0x0537:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.embed_url;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x0a12;
    L_0x0547:
        r4 = 1;
    L_0x0548:
        r0 = r188;
        r0.hasEmbed = r4;
        r162 = 0;
        r0 = r188;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0a15;
    L_0x0554:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r0 = r4.site_name;
        r160 = r0;
    L_0x0560:
        r0 = r188;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0a19;
    L_0x0566:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r0 = r4.type;
        r182 = r0;
    L_0x0572:
        r0 = r188;
        r4 = r0.drawInstantView;
        if (r4 != 0) goto L_0x0b27;
    L_0x0578:
        r4 = "telegram_channel";
        r0 = r182;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x0a1d;
    L_0x0583:
        r4 = 1;
        r0 = r188;
        r0.drawInstantView = r4;
        r4 = 1;
        r0 = r188;
        r0.drawInstantViewType = r4;
    L_0x058d:
        r0 = r43;
        r1 = r188;
        r1.backgroundWidth = r0;
        r0 = r188;
        r4 = r0.hasLinkPreview;
        if (r4 != 0) goto L_0x05af;
    L_0x0599:
        r0 = r188;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x05af;
    L_0x059f:
        r0 = r188;
        r4 = r0.hasInvoicePreview;
        if (r4 != 0) goto L_0x05af;
    L_0x05a5:
        r0 = r189;
        r4 = r0.lastLineWidth;
        r4 = r43 - r4;
        r0 = r170;
        if (r4 >= r0) goto L_0x0CLASSNAME;
    L_0x05af:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r0 = r189;
        r6 = r0.lastLineWidth;
        r4 = java.lang.Math.max(r4, r6);
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.backgroundWidth = r4;
        r0 = r188;
        r4 = r0.backgroundWidth;
        r0 = r188;
        r6 = r0.timeWidth;
        r8 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 + r8;
        r4 = java.lang.Math.max(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x05dd:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.availableTimeWidth = r4;
        r4 = r189.isRoundVideo();
        if (r4 == 0) goto L_0x061a;
    L_0x05f2:
        r0 = r188;
        r4 = r0.availableTimeWidth;
        r8 = (double) r4;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r6 = "00:00";
        r4 = r4.measureText(r6);
        r0 = (double) r4;
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x060d:
        r4 = 0;
    L_0x060e:
        r0 = (double) r4;
        r24 = r0;
        r20 = r20 + r24;
        r8 = r8 - r20;
        r4 = (int) r8;
        r0 = r188;
        r0.availableTimeWidth = r4;
    L_0x061a:
        r188.setMessageObjectInternal(r189);
        r0 = r189;
        r6 = r0.textWidth;
        r0 = r188;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x062d;
    L_0x0627:
        r0 = r188;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x0c5a;
    L_0x062d:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0633:
        r4 = r4 + r6;
        r0 = r188;
        r0.backgroundWidth = r4;
        r0 = r189;
        r4 = r0.textHeight;
        r6 = NUM; // 0x419CLASSNAME float:19.5 double:5.43839131E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x0661;
    L_0x0652:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.namesOffset = r4;
    L_0x0661:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r0 = r188;
        r6 = r0.nameWidth;
        r115 = java.lang.Math.max(r4, r6);
        r0 = r188;
        r4 = r0.forwardedNameWidth;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);
        r0 = r188;
        r4 = r0.replyNameWidth;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);
        r0 = r188;
        r4 = r0.replyTextWidth;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);
        r120 = 0;
        r0 = r188;
        r4 = r0.hasLinkPreview;
        if (r4 != 0) goto L_0x069f;
    L_0x0693:
        r0 = r188;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x069f;
    L_0x0699:
        r0 = r188;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x2097;
    L_0x069f:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x0c6b;
    L_0x06a5:
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x0c5d;
    L_0x06ab:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x0c5d;
    L_0x06b1:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 != 0) goto L_0x0c5d;
    L_0x06bb:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r4 - r6;
    L_0x06c7:
        r0 = r188;
        r4 = r0.drawShareButton;
        if (r4 == 0) goto L_0x06d4;
    L_0x06cd:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r11 = r11 - r4;
    L_0x06d4:
        r0 = r188;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0cb0;
    L_0x06da:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.webpage;
        r181 = r0;
        r181 = (org.telegram.tgnet.TLRPC.TL_webPage) r181;
        r0 = r181;
        r7 = r0.site_name;
        r0 = r188;
        r4 = r0.drawInstantViewType;
        r6 = 6;
        if (r4 == r6) goto L_0x0c9d;
    L_0x06f1:
        r0 = r181;
        r0 = r0.title;
        r172 = r0;
    L_0x06f7:
        r0 = r188;
        r4 = r0.drawInstantViewType;
        r6 = 6;
        if (r4 == r6) goto L_0x0ca1;
    L_0x06fe:
        r0 = r181;
        r0 = r0.author;
        r65 = r0;
    L_0x0704:
        r0 = r188;
        r4 = r0.drawInstantViewType;
        r6 = 6;
        if (r4 == r6) goto L_0x0ca5;
    L_0x070b:
        r0 = r181;
        r0 = r0.description;
        r85 = r0;
    L_0x0711:
        r0 = r181;
        r0 = r0.photo;
        r138 = r0;
        r15 = 0;
        r0 = r181;
        r0 = r0.document;
        r87 = r0;
        r0 = r181;
        r0 = r0.type;
        r175 = r0;
        r0 = r181;
        r0 = r0.duration;
        r88 = r0;
        if (r7 == 0) goto L_0x074b;
    L_0x072c:
        if (r138 == 0) goto L_0x074b;
    L_0x072e:
        r4 = r7.toLowerCase();
        r6 = "instagram";
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x074b;
    L_0x073b:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r4 = r4 / 3;
        r0 = r188;
        r6 = r0.currentMessageObject;
        r6 = r6.textWidth;
        r11 = java.lang.Math.max(r4, r6);
    L_0x074b:
        if (r162 != 0) goto L_0x0ca9;
    L_0x074d:
        r0 = r188;
        r4 = r0.drawInstantView;
        if (r4 != 0) goto L_0x0ca9;
    L_0x0753:
        if (r87 != 0) goto L_0x0ca9;
    L_0x0755:
        if (r175 == 0) goto L_0x0ca9;
    L_0x0757:
        r4 = "app";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0778;
    L_0x0762:
        r4 = "profile";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0778;
    L_0x076d:
        r4 = "article";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0ca9;
    L_0x0778:
        r163 = 1;
    L_0x077a:
        if (r162 != 0) goto L_0x0cad;
    L_0x077c:
        r0 = r188;
        r4 = r0.drawInstantView;
        if (r4 != 0) goto L_0x0cad;
    L_0x0782:
        if (r87 != 0) goto L_0x0cad;
    L_0x0784:
        if (r85 == 0) goto L_0x0cad;
    L_0x0786:
        if (r175 == 0) goto L_0x0cad;
    L_0x0788:
        r4 = "app";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x07a9;
    L_0x0793:
        r4 = "profile";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x07a9;
    L_0x079e:
        r4 = "article";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0cad;
    L_0x07a9:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.photoThumbs;
        if (r4 == 0) goto L_0x0cad;
    L_0x07b1:
        r4 = 1;
    L_0x07b2:
        r0 = r188;
        r0.isSmallImage = r4;
        r180 = r15;
    L_0x07b8:
        r0 = r188;
        r4 = r0.drawInstantViewType;
        r6 = 6;
        if (r4 != r6) goto L_0x07c9;
    L_0x07bf:
        r4 = "ChatBackground";
        r6 = NUM; // 0x7f0CLASSNAMEc4 float:1.8610109E38 double:1.053097622E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x07c9:
        r0 = r188;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x0d36;
    L_0x07cf:
        r57 = 0;
    L_0x07d1:
        r152 = 3;
        r59 = 0;
        r110 = r11 - r57;
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.photoThumbs;
        if (r4 != 0) goto L_0x07e9;
    L_0x07df:
        if (r138 == 0) goto L_0x07e9;
    L_0x07e1:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r6 = 1;
        r4.generateThumbs(r6);
    L_0x07e9:
        if (r7 == 0) goto L_0x086e;
    L_0x07eb:
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0d41 }
        r4 = r4.measureText(r7);	 Catch:{ Exception -> 0x0d41 }
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = r4 + r6;
        r8 = (double) r4;	 Catch:{ Exception -> 0x0d41 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0d41 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0d41 }
        r183 = r0;
        r6 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0d41 }
        r8 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0d41 }
        r0 = r183;
        r1 = r110;
        r9 = java.lang.Math.min(r0, r1);	 Catch:{ Exception -> 0x0d41 }
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0d41 }
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = 0;
        r13 = 0;
        r6.<init>(r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0d41 }
        r0 = r188;
        r0.siteNameLayout = r6;	 Catch:{ Exception -> 0x0d41 }
        r0 = r188;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0d41 }
        r6 = 0;
        r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x0d41 }
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x0d3e;
    L_0x0823:
        r4 = 1;
    L_0x0824:
        r0 = r188;
        r0.siteNameRtl = r4;	 Catch:{ Exception -> 0x0d41 }
        r0 = r188;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0d41 }
        r0 = r188;
        r6 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0d41 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0d41 }
        r6 = r6 + -1;
        r101 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x0d41 }
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0d41 }
        r4 = r4 + r101;
        r0 = r188;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0d41 }
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0d41 }
        r4 = r4 + r101;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0d41 }
        r59 = r59 + r101;
        r0 = r188;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0d41 }
        r183 = r4.getWidth();	 Catch:{ Exception -> 0x0d41 }
        r0 = r183;
        r1 = r188;
        r1.siteNameWidth = r0;	 Catch:{ Exception -> 0x0d41 }
        r4 = r183 + r57;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0d41 }
        r4 = r183 + r57;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0d41 }
    L_0x086e:
        r173 = 0;
        if (r172 == 0) goto L_0x4943;
    L_0x0872:
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0 = r188;
        r0.titleX = r4;	 Catch:{ Exception -> 0x492c }
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x492c }
        if (r4 == 0) goto L_0x089d;
    L_0x087f:
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x492c }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x492c }
        r4 = r4 + r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x492c }
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x492c }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x492c }
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x492c }
    L_0x089d:
        r149 = 0;
        r0 = r188;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x492c }
        if (r4 == 0) goto L_0x08a7;
    L_0x08a5:
        if (r85 != 0) goto L_0x0d47;
    L_0x08a7:
        r9 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x492c }
        r11 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x492c }
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x492c }
        r13 = (float) r4;	 Catch:{ Exception -> 0x492c }
        r14 = 0;
        r15 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x492c }
        r17 = 4;
        r8 = r172;
        r10 = r110;
        r16 = r110;
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17);	 Catch:{ Exception -> 0x492c }
        r0 = r188;
        r0.titleLayout = r4;	 Catch:{ Exception -> 0x492c }
        r13 = r152;
    L_0x08c9:
        r0 = r188;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0d7f }
        r0 = r188;
        r6 = r0.titleLayout;	 Catch:{ Exception -> 0x0d7f }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0d7f }
        r6 = r6 + -1;
        r101 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x0d7f }
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0d7f }
        r4 = r4 + r101;
        r0 = r188;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0d7f }
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0d7f }
        r4 = r4 + r101;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0d7f }
        r78 = 1;
        r56 = 0;
    L_0x08f3:
        r0 = r188;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0d7f }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0d7f }
        r0 = r56;
        if (r0 >= r4) goto L_0x0d83;
    L_0x08ff:
        r0 = r188;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0d7f }
        r0 = r56;
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x0d7f }
        r0 = (int) r4;	 Catch:{ Exception -> 0x0d7f }
        r109 = r0;
        if (r109 == 0) goto L_0x0910;
    L_0x090e:
        r173 = 1;
    L_0x0910:
        r0 = r188;
        r4 = r0.titleX;	 Catch:{ Exception -> 0x0d7f }
        r6 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r4 != r6) goto L_0x0d6e;
    L_0x0919:
        r0 = r109;
        r4 = -r0;
        r0 = r188;
        r0.titleX = r4;	 Catch:{ Exception -> 0x0d7f }
    L_0x0920:
        if (r109 == 0) goto L_0x0var_;
    L_0x0922:
        r0 = r188;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0d7f }
        r4 = r4.getWidth();	 Catch:{ Exception -> 0x0d7f }
        r183 = r4 - r109;
    L_0x092c:
        r0 = r56;
        r1 = r149;
        if (r0 < r1) goto L_0x093a;
    L_0x0932:
        if (r109 == 0) goto L_0x0942;
    L_0x0934:
        r0 = r188;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0d7f }
        if (r4 == 0) goto L_0x0942;
    L_0x093a:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0d7f }
        r183 = r183 + r4;
    L_0x0942:
        r4 = r183 + r57;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0d7f }
        r4 = r183 + r57;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0d7f }
        r56 = r56 + 1;
        goto L_0x08f3;
    L_0x0955:
        r124 = 0;
        goto L_0x0031;
    L_0x0959:
        r123 = 0;
        goto L_0x0041;
    L_0x095d:
        r84 = 0;
        goto L_0x0078;
    L_0x0961:
        r96 = 0;
        goto L_0x0082;
    L_0x0965:
        r129 = 0;
        goto L_0x0131;
    L_0x0969:
        r96 = 0;
        goto L_0x013b;
    L_0x096d:
        r4 = 0;
        r0 = r188;
        r0.currentMessagesGroup = r4;
        r4 = 0;
        r0 = r188;
        r0.currentPosition = r4;
        goto L_0x01b2;
    L_0x0979:
        r4 = 0;
        goto L_0x01c9;
    L_0x097c:
        r4 = 0;
        goto L_0x01e4;
    L_0x097f:
        r4 = 0;
        goto L_0x0243;
    L_0x0982:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.to_id;
        r4 = r4.channel_id;
        if (r4 == 0) goto L_0x09a5;
    L_0x098c:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x09a5;
    L_0x0992:
        r4 = 1;
    L_0x0993:
        r0 = r188;
        r0.drawName = r4;
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        goto L_0x0487;
    L_0x09a5:
        r4 = 0;
        goto L_0x0993;
    L_0x09a7:
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x09d4;
    L_0x09ad:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x09d4;
    L_0x09b3:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x09d4;
    L_0x09b9:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x42var_ float:122.0 double:5.54977537E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r4 = 1;
        r0 = r188;
        r0.drawName = r4;
        goto L_0x0487;
    L_0x09d4:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.to_id;
        r4 = r4.channel_id;
        if (r4 == 0) goto L_0x09ff;
    L_0x09f2:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x09ff;
    L_0x09f8:
        r4 = 1;
    L_0x09f9:
        r0 = r188;
        r0.drawName = r4;
        goto L_0x0487;
    L_0x09ff:
        r4 = 0;
        goto L_0x09f9;
    L_0x0a01:
        r4 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x04af;
    L_0x0a09:
        r4 = 0;
        goto L_0x04ef;
    L_0x0a0c:
        r4 = 0;
        goto L_0x0516;
    L_0x0a0f:
        r4 = 0;
        goto L_0x052d;
    L_0x0a12:
        r4 = 0;
        goto L_0x0548;
    L_0x0a15:
        r160 = 0;
        goto L_0x0560;
    L_0x0a19:
        r182 = 0;
        goto L_0x0572;
    L_0x0a1d:
        r4 = "telegram_megagroup";
        r0 = r182;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x0a34;
    L_0x0a28:
        r4 = 1;
        r0 = r188;
        r0.drawInstantView = r4;
        r4 = 2;
        r0 = r188;
        r0.drawInstantViewType = r4;
        goto L_0x058d;
    L_0x0a34:
        r4 = "telegram_message";
        r0 = r182;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x0a4b;
    L_0x0a3f:
        r4 = 1;
        r0 = r188;
        r0.drawInstantView = r4;
        r4 = 3;
        r0 = r188;
        r0.drawInstantViewType = r4;
        goto L_0x058d;
    L_0x0a4b:
        r4 = "telegram_background";
        r0 = r182;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x058d;
    L_0x0a56:
        r4 = 1;
        r0 = r188;
        r0.drawInstantView = r4;
        r4 = 6;
        r0 = r188;
        r0.drawInstantViewType = r4;
        r0 = r189;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0ad1 }
        r4 = r4.media;	 Catch:{ Exception -> 0x0ad1 }
        r4 = r4.webpage;	 Catch:{ Exception -> 0x0ad1 }
        r4 = r4.url;	 Catch:{ Exception -> 0x0ad1 }
        r177 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0ad1 }
        r4 = "intensity";
        r0 = r177;
        r4 = r0.getQueryParameter(r4);	 Catch:{ Exception -> 0x0ad1 }
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ Exception -> 0x0ad1 }
        r103 = r4.intValue();	 Catch:{ Exception -> 0x0ad1 }
        r4 = "bg_color";
        r0 = r177;
        r69 = r0.getQueryParameter(r4);	 Catch:{ Exception -> 0x0ad1 }
        if (r69 == 0) goto L_0x0ad4;
    L_0x0a8a:
        r4 = "bg_color";
        r0 = r177;
        r4 = r0.getQueryParameter(r4);	 Catch:{ Exception -> 0x0ad1 }
        r6 = 16;
        r4 = java.lang.Integer.parseInt(r4, r6);	 Catch:{ Exception -> 0x0ad1 }
        r6 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r4 = r4 | r6;
        r0 = r188;
        r0.imageBackgroundColor = r4;	 Catch:{ Exception -> 0x0ad1 }
        r0 = r188;
        r4 = r0.imageBackgroundColor;	 Catch:{ Exception -> 0x0ad1 }
        r4 = org.telegram.messenger.AndroidUtilities.getPatternSideColor(r4);	 Catch:{ Exception -> 0x0ad1 }
        r0 = r188;
        r0.imageBackgroundSideColor = r4;	 Catch:{ Exception -> 0x0ad1 }
        r0 = r188;
        r4 = r0.photoImage;	 Catch:{ Exception -> 0x0ad1 }
        r6 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Exception -> 0x0ad1 }
        r0 = r188;
        r8 = r0.imageBackgroundColor;	 Catch:{ Exception -> 0x0ad1 }
        r8 = org.telegram.messenger.AndroidUtilities.getPatternColor(r8);	 Catch:{ Exception -> 0x0ad1 }
        r9 = android.graphics.PorterDuff.Mode.SRC_IN;	 Catch:{ Exception -> 0x0ad1 }
        r6.<init>(r8, r9);	 Catch:{ Exception -> 0x0ad1 }
        r4.setColorFilter(r6);	 Catch:{ Exception -> 0x0ad1 }
        r0 = r188;
        r4 = r0.photoImage;	 Catch:{ Exception -> 0x0ad1 }
        r0 = r103;
        r6 = (float) r0;	 Catch:{ Exception -> 0x0ad1 }
        r8 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r6 = r6 / r8;
        r4.setAlpha(r6);	 Catch:{ Exception -> 0x0ad1 }
        goto L_0x058d;
    L_0x0ad1:
        r4 = move-exception;
        goto L_0x058d;
    L_0x0ad4:
        r79 = r177.getLastPathSegment();	 Catch:{ Exception -> 0x0ad1 }
        if (r79 == 0) goto L_0x058d;
    L_0x0ada:
        r4 = r79.length();	 Catch:{ Exception -> 0x0ad1 }
        r6 = 6;
        if (r4 != r6) goto L_0x058d;
    L_0x0ae1:
        r4 = 16;
        r0 = r79;
        r4 = java.lang.Integer.parseInt(r0, r4);	 Catch:{ Exception -> 0x0ad1 }
        r6 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r4 = r4 | r6;
        r0 = r188;
        r0.imageBackgroundColor = r4;	 Catch:{ Exception -> 0x0ad1 }
        r4 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;	 Catch:{ Exception -> 0x0ad1 }
        r4.<init>();	 Catch:{ Exception -> 0x0ad1 }
        r0 = r188;
        r0.currentPhotoObject = r4;	 Catch:{ Exception -> 0x0ad1 }
        r0 = r188;
        r4 = r0.currentPhotoObject;	 Catch:{ Exception -> 0x0ad1 }
        r6 = "s";
        r4.type = r6;	 Catch:{ Exception -> 0x0ad1 }
        r0 = r188;
        r4 = r0.currentPhotoObject;	 Catch:{ Exception -> 0x0ad1 }
        r6 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0ad1 }
        r4.w = r6;	 Catch:{ Exception -> 0x0ad1 }
        r0 = r188;
        r4 = r0.currentPhotoObject;	 Catch:{ Exception -> 0x0ad1 }
        r6 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0ad1 }
        r4.h = r6;	 Catch:{ Exception -> 0x0ad1 }
        r0 = r188;
        r4 = r0.currentPhotoObject;	 Catch:{ Exception -> 0x0ad1 }
        r6 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;	 Catch:{ Exception -> 0x0ad1 }
        r6.<init>();	 Catch:{ Exception -> 0x0ad1 }
        r4.location = r6;	 Catch:{ Exception -> 0x0ad1 }
        goto L_0x058d;
    L_0x0b27:
        if (r160 == 0) goto L_0x058d;
    L_0x0b29:
        r160 = r160.toLowerCase();
        r4 = "instagram";
        r0 = r160;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0b4e;
    L_0x0b38:
        r4 = "twitter";
        r0 = r160;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0b4e;
    L_0x0b43:
        r4 = "telegram_album";
        r0 = r182;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x058d;
    L_0x0b4e:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.cached_page;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_page;
        if (r4 == 0) goto L_0x058d;
    L_0x0b5c:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.photo;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r4 != 0) goto L_0x0b7a;
    L_0x0b6a:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.document;
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r4);
        if (r4 == 0) goto L_0x058d;
    L_0x0b7a:
        r4 = 0;
        r0 = r188;
        r0.drawInstantView = r4;
        r162 = 1;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.cached_page;
        r0 = r4.blocks;
        r71 = r0;
        r80 = 1;
        r56 = 0;
    L_0x0b93:
        r4 = r71.size();
        r0 = r56;
        if (r0 >= r4) goto L_0x0bcd;
    L_0x0b9b:
        r0 = r71;
        r1 = r56;
        r70 = r0.get(r1);
        r70 = (org.telegram.tgnet.TLRPC.PageBlock) r70;
        r0 = r70;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
        if (r4 == 0) goto L_0x0bba;
    L_0x0bab:
        r68 = r70;
        r68 = (org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow) r68;
        r0 = r68;
        r4 = r0.items;
        r80 = r4.size();
    L_0x0bb7:
        r56 = r56 + 1;
        goto L_0x0b93;
    L_0x0bba:
        r0 = r70;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
        if (r4 == 0) goto L_0x0bb7;
    L_0x0bc0:
        r68 = r70;
        r68 = (org.telegram.tgnet.TLRPC.TL_pageBlockCollage) r68;
        r0 = r68;
        r4 = r0.items;
        r80 = r4.size();
        goto L_0x0bb7;
    L_0x0bcd:
        r4 = "Of";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612066E38 double:1.0530980985E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = 1;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r80);
        r8[r9] = r10;
        r5 = org.telegram.messenger.LocaleController.formatString(r4, r6, r8);
        r4 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r188;
        r0.photosCountWidth = r4;
        r4 = new android.text.StaticLayout;
        r6 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r0 = r188;
        r7 = r0.photosCountWidth;
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10 = 0;
        r11 = 0;
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);
        r0 = r188;
        r0.photosCountLayout = r4;
        goto L_0x058d;
    L_0x0CLASSNAME:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r0 = r189;
        r6 = r0.lastLineWidth;
        r86 = r4 - r6;
        if (r86 < 0) goto L_0x0CLASSNAME;
    L_0x0c1c:
        r0 = r86;
        r1 = r170;
        if (r0 > r1) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r4 = r4 + r170;
        r4 = r4 - r86;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x05dd;
    L_0x0CLASSNAME:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r0 = r189;
        r6 = r0.lastLineWidth;
        r6 = r6 + r170;
        r4 = java.lang.Math.max(r4, r6);
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x05dd;
    L_0x0CLASSNAME:
        r4 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x060e;
    L_0x0c5a:
        r4 = 0;
        goto L_0x0633;
    L_0x0c5d:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r4 - r6;
        goto L_0x06c7;
    L_0x0c6b:
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x0c8f;
    L_0x0CLASSNAME:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x0c8f;
    L_0x0CLASSNAME:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 != 0) goto L_0x0c8f;
    L_0x0CLASSNAME:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r4 - r6;
        goto L_0x06c7;
    L_0x0c8f:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r4 - r6;
        goto L_0x06c7;
    L_0x0c9d:
        r172 = 0;
        goto L_0x06f7;
    L_0x0ca1:
        r65 = 0;
        goto L_0x0704;
    L_0x0ca5:
        r85 = 0;
        goto L_0x0711;
    L_0x0ca9:
        r163 = 0;
        goto L_0x077a;
    L_0x0cad:
        r4 = 0;
        goto L_0x07b2;
    L_0x0cb0:
        r0 = r188;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x0cf4;
    L_0x0cb6:
        r0 = r189;
        r4 = r0.messageOwner;
        r0 = r4.media;
        r104 = r0;
        r104 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r104;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r7 = r4.title;
        r172 = 0;
        r85 = 0;
        r138 = 0;
        r65 = 0;
        r87 = 0;
        r0 = r104;
        r4 = r0.photo;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r4 == 0) goto L_0x0cf2;
    L_0x0cda:
        r0 = r104;
        r4 = r0.photo;
        r15 = org.telegram.messenger.WebFile.createWithWebDocument(r4);
    L_0x0ce2:
        r88 = 0;
        r175 = "invoice";
        r4 = 0;
        r0 = r188;
        r0.isSmallImage = r4;
        r163 = 0;
        r180 = r15;
        goto L_0x07b8;
    L_0x0cf2:
        r15 = 0;
        goto L_0x0ce2;
    L_0x0cf4:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.game;
        r95 = r0;
        r0 = r95;
        r7 = r0.title;
        r172 = 0;
        r15 = 0;
        r0 = r189;
        r4 = r0.messageText;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x0d33;
    L_0x0d0f:
        r0 = r95;
        r0 = r0.description;
        r85 = r0;
    L_0x0d15:
        r0 = r95;
        r0 = r0.photo;
        r138 = r0;
        r65 = 0;
        r0 = r95;
        r0 = r0.document;
        r87 = r0;
        r88 = 0;
        r175 = "game";
        r4 = 0;
        r0 = r188;
        r0.isSmallImage = r4;
        r163 = 0;
        r180 = r15;
        goto L_0x07b8;
    L_0x0d33:
        r85 = 0;
        goto L_0x0d15;
    L_0x0d36:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r57 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x07d1;
    L_0x0d3e:
        r4 = 0;
        goto L_0x0824;
    L_0x0d41:
        r90 = move-exception;
        org.telegram.messenger.FileLog.e(r90);
        goto L_0x086e;
    L_0x0d47:
        r149 = r152;
        r9 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x492c }
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x492c }
        r11 = r110 - r4;
        r13 = 4;
        r8 = r172;
        r10 = r110;
        r12 = r152;
        r4 = generateStaticLayout(r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x492c }
        r0 = r188;
        r0.titleLayout = r4;	 Catch:{ Exception -> 0x492c }
        r0 = r188;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x492c }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x492c }
        r13 = r152 - r4;
        goto L_0x08c9;
    L_0x0d6e:
        r0 = r188;
        r4 = r0.titleX;	 Catch:{ Exception -> 0x0d7f }
        r0 = r109;
        r6 = -r0;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x0d7f }
        r0 = r188;
        r0.titleX = r4;	 Catch:{ Exception -> 0x0d7f }
        goto L_0x0920;
    L_0x0d7f:
        r90 = move-exception;
    L_0x0d80:
        org.telegram.messenger.FileLog.e(r90);
    L_0x0d83:
        if (r173 == 0) goto L_0x493d;
    L_0x0d85:
        r0 = r188;
        r4 = r0.isSmallImage;
        if (r4 == 0) goto L_0x493d;
    L_0x0d8b:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r11 = r110 - r4;
        r152 = r13;
    L_0x0d95:
        r66 = 0;
        if (r65 == 0) goto L_0x4939;
    L_0x0d99:
        if (r172 != 0) goto L_0x4939;
    L_0x0d9b:
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0f4e }
        if (r4 == 0) goto L_0x0dbf;
    L_0x0da1:
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0f4e }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0f4e }
        r4 = r4 + r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0f4e }
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0f4e }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0f4e }
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0f4e }
    L_0x0dbf:
        r4 = 3;
        r0 = r152;
        if (r0 != r4) goto L_0x0var_;
    L_0x0dc4:
        r0 = r188;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0f4e }
        if (r4 == 0) goto L_0x0dcc;
    L_0x0dca:
        if (r85 != 0) goto L_0x0var_;
    L_0x0dcc:
        r8 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0f4e }
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0f4e }
        r12 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0f4e }
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r14 = 0;
        r15 = 0;
        r9 = r65;
        r8.<init>(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ Exception -> 0x0f4e }
        r0 = r188;
        r0.authorLayout = r8;	 Catch:{ Exception -> 0x0f4e }
        r13 = r152;
    L_0x0de1:
        r0 = r188;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x4929 }
        r0 = r188;
        r6 = r0.authorLayout;	 Catch:{ Exception -> 0x4929 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x4929 }
        r6 = r6 + -1;
        r101 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x4929 }
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x4929 }
        r4 = r4 + r101;
        r0 = r188;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x4929 }
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4929 }
        r4 = r4 + r101;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4929 }
        r0 = r188;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x4929 }
        r6 = 0;
        r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x4929 }
        r0 = (int) r4;	 Catch:{ Exception -> 0x4929 }
        r109 = r0;
        r0 = r109;
        r4 = -r0;
        r0 = r188;
        r0.authorX = r4;	 Catch:{ Exception -> 0x4929 }
        if (r109 == 0) goto L_0x0f3b;
    L_0x0e1c:
        r0 = r188;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x4929 }
        r4 = r4.getWidth();	 Catch:{ Exception -> 0x4929 }
        r183 = r4 - r109;
        r66 = 1;
    L_0x0e28:
        r4 = r183 + r57;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x4929 }
        r4 = r183 + r57;
        r0 = r120;
        r120 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x4929 }
    L_0x0e38:
        if (r85 == 0) goto L_0x0var_;
    L_0x0e3a:
        r4 = 0;
        r0 = r188;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0f7c }
        r0 = r188;
        r4 = r0.currentMessageObject;	 Catch:{ Exception -> 0x0f7c }
        r4.generateLinkDescription();	 Catch:{ Exception -> 0x0f7c }
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0f7c }
        if (r4 == 0) goto L_0x0e6a;
    L_0x0e4c:
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0f7c }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0f7c }
        r4 = r4 + r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0f7c }
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0f7c }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0f7c }
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0f7c }
    L_0x0e6a:
        r149 = 0;
        if (r7 == 0) goto L_0x0var_;
    L_0x0e6e:
        r4 = r7.toLowerCase();	 Catch:{ Exception -> 0x0f7c }
        r6 = "twitter";
        r4 = r4.equals(r6);	 Catch:{ Exception -> 0x0f7c }
        if (r4 == 0) goto L_0x0var_;
    L_0x0e7b:
        r61 = 1;
    L_0x0e7d:
        r4 = 3;
        if (r13 != r4) goto L_0x0f5e;
    L_0x0e80:
        r0 = r188;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0f7c }
        if (r4 != 0) goto L_0x0f5e;
    L_0x0e86:
        r0 = r189;
        r9 = r0.linkDescription;	 Catch:{ Exception -> 0x0f7c }
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0f7c }
        r12 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0f7c }
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0f7c }
        r14 = (float) r4;	 Catch:{ Exception -> 0x0f7c }
        r15 = 0;
        r16 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0f7c }
        if (r61 == 0) goto L_0x0f5a;
    L_0x0e9c:
        r18 = 100;
    L_0x0e9e:
        r17 = r11;
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r9, r10, r11, r12, r13, r14, r15, r16, r17, r18);	 Catch:{ Exception -> 0x0f7c }
        r0 = r188;
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x0f7c }
    L_0x0ea8:
        r0 = r188;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f7c }
        r0 = r188;
        r6 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f7c }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0f7c }
        r6 = r6 + -1;
        r101 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x0f7c }
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0f7c }
        r4 = r4 + r101;
        r0 = r188;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0f7c }
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0f7c }
        r4 = r4 + r101;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0f7c }
        r100 = 0;
        r56 = 0;
    L_0x0ed2:
        r0 = r188;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f7c }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0f7c }
        r0 = r56;
        if (r0 >= r4) goto L_0x15c0;
    L_0x0ede:
        r0 = r188;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f7c }
        r0 = r56;
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x0f7c }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0f7c }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0f7c }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0f7c }
        r109 = r0;
        if (r109 == 0) goto L_0x0var_;
    L_0x0ef2:
        r100 = 1;
        r0 = r188;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x0f7c }
        if (r4 != 0) goto L_0x15af;
    L_0x0efa:
        r0 = r109;
        r4 = -r0;
        r0 = r188;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0f7c }
    L_0x0var_:
        r56 = r56 + 1;
        goto L_0x0ed2;
    L_0x0var_:
        r0 = r188;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0d7f }
        r0 = r56;
        r4 = r4.getLineWidth(r0);	 Catch:{ Exception -> 0x0d7f }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0d7f }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0d7f }
        r0 = (int) r8;
        r183 = r0;
        goto L_0x092c;
    L_0x0var_:
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0f4e }
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0f4e }
        r12 = r11 - r4;
        r14 = 1;
        r9 = r65;
        r13 = r152;
        r4 = generateStaticLayout(r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x0f4e }
        r0 = r188;
        r0.authorLayout = r4;	 Catch:{ Exception -> 0x0f4e }
        r0 = r188;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x0f4e }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0f4e }
        r13 = r152 - r4;
        goto L_0x0de1;
    L_0x0f3b:
        r0 = r188;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x4929 }
        r6 = 0;
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x4929 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x4929 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x4929 }
        r0 = (int) r8;
        r183 = r0;
        goto L_0x0e28;
    L_0x0f4e:
        r90 = move-exception;
        r13 = r152;
    L_0x0var_:
        org.telegram.messenger.FileLog.e(r90);
        goto L_0x0e38;
    L_0x0var_:
        r61 = 0;
        goto L_0x0e7d;
    L_0x0f5a:
        r18 = 6;
        goto L_0x0e9e;
    L_0x0f5e:
        r149 = r13;
        r0 = r189;
        r9 = r0.linkDescription;	 Catch:{ Exception -> 0x0f7c }
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0f7c }
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0f7c }
        r12 = r11 - r4;
        if (r61 == 0) goto L_0x15ac;
    L_0x0var_:
        r14 = 100;
    L_0x0var_:
        r4 = generateStaticLayout(r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x0f7c }
        r0 = r188;
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x0f7c }
        goto L_0x0ea8;
    L_0x0f7c:
        r90 = move-exception;
        org.telegram.messenger.FileLog.e(r90);
    L_0x0var_:
        if (r163 == 0) goto L_0x0fa0;
    L_0x0var_:
        r0 = r188;
        r4 = r0.descriptionLayout;
        if (r4 == 0) goto L_0x0var_;
    L_0x0var_:
        r0 = r188;
        r4 = r0.descriptionLayout;
        if (r4 == 0) goto L_0x0fa0;
    L_0x0f8e:
        r0 = r188;
        r4 = r0.descriptionLayout;
        r4 = r4.getLineCount();
        r6 = 1;
        if (r4 != r6) goto L_0x0fa0;
    L_0x0var_:
        r163 = 0;
        r4 = 0;
        r0 = r188;
        r0.isSmallImage = r4;
    L_0x0fa0:
        if (r163 == 0) goto L_0x165e;
    L_0x0fa2:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r117 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0fa8:
        if (r87 == 0) goto L_0x1bde;
    L_0x0faa:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r87);
        if (r4 == 0) goto L_0x1662;
    L_0x0fb0:
        r0 = r87;
        r4 = r0.thumbs;
        r6 = 90;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObject = r4;
        r0 = r87;
        r1 = r188;
        r1.documentAttach = r0;
        r4 = 7;
        r0 = r188;
        r0.documentAttachType = r4;
        r15 = r180;
    L_0x0fcb:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 5;
        if (r4 == r6) goto L_0x1303;
    L_0x0fd2:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 3;
        if (r4 == r6) goto L_0x1303;
    L_0x0fd9:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 1;
        if (r4 == r6) goto L_0x1303;
    L_0x0fe0:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x0fe8;
    L_0x0fe6:
        if (r15 == 0) goto L_0x2032;
    L_0x0fe8:
        if (r175 == 0) goto L_0x1c4e;
    L_0x0fea:
        r4 = "photo";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x1021;
    L_0x0ff5:
        r4 = "document";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x1007;
    L_0x1000:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 6;
        if (r4 != r6) goto L_0x1021;
    L_0x1007:
        r4 = "gif";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x1021;
    L_0x1012:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 4;
        if (r4 == r6) goto L_0x1021;
    L_0x1019:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 8;
        if (r4 != r6) goto L_0x1c4e;
    L_0x1021:
        r4 = 1;
    L_0x1022:
        r0 = r188;
        r0.drawImageButton = r4;
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        if (r4 == 0) goto L_0x104a;
    L_0x102c:
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
    L_0x104a:
        r0 = r188;
        r4 = r0.imageBackgroundSideColor;
        if (r4 == 0) goto L_0x1CLASSNAME;
    L_0x1050:
        r4 = NUM; // 0x43500000 float:208.0 double:5.57956413E-315;
        r117 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x1056:
        r0 = r188;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x1cad;
    L_0x105c:
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x1062:
        r4 = r117 - r4;
        r4 = r4 + r57;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x1cb0;
    L_0x1072:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r6 = -1;
        r4.size = r6;
        r0 = r188;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x1086;
    L_0x107f:
        r0 = r188;
        r4 = r0.currentPhotoObjectThumb;
        r6 = -1;
        r4.size = r6;
    L_0x1086:
        r0 = r188;
        r4 = r0.imageBackgroundSideColor;
        if (r4 == 0) goto L_0x1098;
    L_0x108c:
        r4 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r115 - r4;
        r0 = r188;
        r0.imageBackgroundSideWidth = r4;
    L_0x1098:
        if (r163 != 0) goto L_0x10a1;
    L_0x109a:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 7;
        if (r4 != r6) goto L_0x1cb5;
    L_0x10a1:
        r101 = r117;
        r183 = r117;
    L_0x10a5:
        r0 = r188;
        r4 = r0.isSmallImage;
        if (r4 == 0) goto L_0x1d6f;
    L_0x10ab:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r59;
        r0 = r188;
        r6 = r0.linkPreviewHeight;
        if (r4 <= r6) goto L_0x10e2;
    L_0x10b9:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r59;
        r0 = r188;
        r8 = r0.linkPreviewHeight;
        r6 = r6 - r8;
        r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 + r8;
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r59;
        r0 = r188;
        r0.linkPreviewHeight = r4;
    L_0x10e2:
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;
    L_0x10f1:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 8;
        if (r4 != r6) goto L_0x1d8c;
    L_0x10f9:
        r0 = r188;
        r4 = r0.imageBackgroundSideColor;
        if (r4 != 0) goto L_0x1d8c;
    L_0x10ff:
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r8 = 0;
        r9 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = r115 - r9;
        r0 = r183;
        r9 = java.lang.Math.max(r9, r0);
        r0 = r101;
        r4.setImageCoords(r6, r8, r9, r0);
    L_0x1118:
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r183);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r101);
        r8[r9] = r10;
        r4 = java.lang.String.format(r4, r6, r8);
        r0 = r188;
        r0.currentPhotoFilter = r4;
        r4 = java.util.Locale.US;
        r6 = "%d_%d_b";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r183);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r101);
        r8[r9] = r10;
        r4 = java.lang.String.format(r4, r6, r8);
        r0 = r188;
        r0.currentPhotoFilterThumb = r4;
        if (r15 == 0) goto L_0x1d9b;
    L_0x1156:
        r0 = r188;
        r14 = r0.photoImage;
        r0 = r188;
        r0 = r0.currentPhotoFilter;
        r16 = r0;
        r17 = 0;
        r18 = 0;
        r19 = "b1";
        r0 = r15.size;
        r20 = r0;
        r21 = 0;
        r23 = 1;
        r22 = r189;
        r14.setImage(r15, r16, r17, r18, r19, r20, r21, r22, r23);
    L_0x1174:
        r4 = 1;
        r0 = r188;
        r0.drawPhotoImage = r4;
        if (r175 == 0) goto L_0x1fef;
    L_0x117b:
        r4 = "video";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x1fef;
    L_0x1186:
        if (r88 == 0) goto L_0x1fef;
    L_0x1188:
        r126 = r88 / 60;
        r4 = r126 * 60;
        r159 = r88 - r4;
        r4 = "%d:%02d";
        r6 = 2;
        r6 = new java.lang.Object[r6];
        r8 = 0;
        r9 = java.lang.Integer.valueOf(r126);
        r6[r8] = r9;
        r8 = 1;
        r9 = java.lang.Integer.valueOf(r159);
        r6[r8] = r9;
        r5 = java.lang.String.format(r4, r6);
        r4 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r188;
        r0.durationWidth = r4;
        r16 = new android.text.StaticLayout;
        r18 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r0 = r188;
        r0 = r0.durationWidth;
        r19 = r0;
        r20 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r22 = 0;
        r23 = 0;
        r17 = r5;
        r16.<init>(r17, r18, r19, r20, r21, r22, r23);
        r0 = r16;
        r1 = r188;
        r1.videoInfoLayout = r0;
    L_0x11d3:
        r0 = r188;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x12c9;
    L_0x11d9:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x205c;
    L_0x11e5:
        r4 = "PaymentReceipt";
        r6 = NUM; // 0x7f0CLASSNAMEa7 float:1.8612646E38 double:1.05309824E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
    L_0x11f3:
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r0 = r189;
        r6 = r0.messageOwner;
        r6 = r6.media;
        r8 = r6.total_amount;
        r0 = r189;
        r6 = r0.messageOwner;
        r6 = r6.media;
        r6 = r6.currency;
        r148 = r4.formatCurrencyString(r8, r6);
        r17 = new android.text.SpannableStringBuilder;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r148;
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
        r8 = r148.length();
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
        r0 = r188;
        r0.durationWidth = r4;
        r16 = new android.text.StaticLayout;
        r18 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
        r0 = r188;
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
        r1 = r188;
        r1.videoInfoLayout = r0;
        r0 = r188;
        r4 = r0.drawPhotoImage;
        if (r4 != 0) goto L_0x12c9;
    L_0x1282:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r6 = r0.timeWidth;
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x2086;
    L_0x129b:
        r4 = 20;
    L_0x129d:
        r4 = r4 + 14;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r171 = r6 + r4;
        r0 = r188;
        r4 = r0.durationWidth;
        r4 = r4 + r171;
        r0 = r43;
        if (r4 <= r0) goto L_0x2089;
    L_0x12b0:
        r0 = r188;
        r4 = r0.durationWidth;
        r0 = r115;
        r115 = java.lang.Math.max(r4, r0);
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
    L_0x12c9:
        r0 = r188;
        r4 = r0.hasGamePreview;
        if (r4 == 0) goto L_0x12f8;
    L_0x12cf:
        r0 = r189;
        r4 = r0.textHeight;
        if (r4 == 0) goto L_0x12f8;
    L_0x12d5:
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        r0 = r189;
        r6 = r0.textHeight;
        r8 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 + r8;
        r4 = r4 + r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
    L_0x12f8:
        r0 = r188;
        r1 = r43;
        r2 = r170;
        r3 = r115;
        r0.calcBackgroundWidth(r1, r2, r3);
    L_0x1303:
        r188.createInstantViewButton();
    L_0x1306:
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 != 0) goto L_0x142f;
    L_0x130c:
        r0 = r189;
        r4 = r0.type;
        r6 = 13;
        if (r4 == r6) goto L_0x142f;
    L_0x1314:
        r0 = r188;
        r4 = r0.addedCaptionHeight;
        if (r4 != 0) goto L_0x142f;
    L_0x131a:
        r0 = r188;
        r4 = r0.captionLayout;
        if (r4 != 0) goto L_0x1375;
    L_0x1320:
        r0 = r189;
        r4 = r0.caption;
        if (r4 == 0) goto L_0x1375;
    L_0x1326:
        r0 = r189;
        r4 = r0.caption;	 Catch:{ Exception -> 0x44f9 }
        r0 = r188;
        r0.currentCaption = r4;	 Catch:{ Exception -> 0x44f9 }
        r0 = r188;
        r4 = r0.backgroundWidth;	 Catch:{ Exception -> 0x44f9 }
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x44f9 }
        r183 = r4 - r6;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x44f9 }
        r27 = r183 - r4;
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x44f9 }
        r6 = 24;
        if (r4 < r6) goto L_0x44da;
    L_0x1348:
        r0 = r189;
        r4 = r0.caption;	 Catch:{ Exception -> 0x44f9 }
        r6 = 0;
        r0 = r189;
        r8 = r0.caption;	 Catch:{ Exception -> 0x44f9 }
        r8 = r8.length();	 Catch:{ Exception -> 0x44f9 }
        r9 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x44f9 }
        r0 = r27;
        r4 = android.text.StaticLayout.Builder.obtain(r4, r6, r8, r9, r0);	 Catch:{ Exception -> 0x44f9 }
        r6 = 1;
        r4 = r4.setBreakStrategy(r6);	 Catch:{ Exception -> 0x44f9 }
        r6 = 0;
        r4 = r4.setHyphenationFrequency(r6);	 Catch:{ Exception -> 0x44f9 }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x44f9 }
        r4 = r4.setAlignment(r6);	 Catch:{ Exception -> 0x44f9 }
        r4 = r4.build();	 Catch:{ Exception -> 0x44f9 }
        r0 = r188;
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x44f9 }
    L_0x1375:
        r0 = r188;
        r4 = r0.captionLayout;
        if (r4 == 0) goto L_0x142f;
    L_0x137b:
        r0 = r188;
        r4 = r0.backgroundWidth;	 Catch:{ Exception -> 0x4502 }
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4502 }
        r183 = r4 - r6;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4502 }
        r27 = r183 - r4;
        r0 = r188;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4502 }
        if (r4 == 0) goto L_0x142f;
    L_0x1395:
        r0 = r188;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4502 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x4502 }
        if (r4 <= 0) goto L_0x142f;
    L_0x139f:
        r0 = r27;
        r1 = r188;
        r1.captionWidth = r0;	 Catch:{ Exception -> 0x4502 }
        r0 = r188;
        r6 = r0.timeWidth;	 Catch:{ Exception -> 0x4502 }
        r4 = r189.isOutOwner();	 Catch:{ Exception -> 0x4502 }
        if (r4 == 0) goto L_0x44ff;
    L_0x13af:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4502 }
    L_0x13b5:
        r171 = r6 + r4;
        r0 = r188;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4502 }
        r4 = r4.getHeight();	 Catch:{ Exception -> 0x4502 }
        r0 = r188;
        r0.captionHeight = r4;	 Catch:{ Exception -> 0x4502 }
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4502 }
        r0 = r188;
        r6 = r0.captionHeight;	 Catch:{ Exception -> 0x4502 }
        r8 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x4502 }
        r6 = r6 + r8;
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4502 }
        r0 = r188;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4502 }
        r0 = r188;
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x4502 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x4502 }
        r6 = r6 + -1;
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x4502 }
        r0 = r188;
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x4502 }
        r0 = r188;
        r8 = r0.captionLayout;	 Catch:{ Exception -> 0x4502 }
        r8 = r8.getLineCount();	 Catch:{ Exception -> 0x4502 }
        r8 = r8 + -1;
        r6 = r6.getLineLeft(r8);	 Catch:{ Exception -> 0x4502 }
        r107 = r4 + r6;
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4502 }
        r4 = r183 - r4;
        r4 = (float) r4;	 Catch:{ Exception -> 0x4502 }
        r4 = r4 - r107;
        r0 = r171;
        r6 = (float) r0;	 Catch:{ Exception -> 0x4502 }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x142f;
    L_0x140f:
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4502 }
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4502 }
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4502 }
        r0 = r188;
        r4 = r0.captionHeight;	 Catch:{ Exception -> 0x4502 }
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4502 }
        r4 = r4 + r6;
        r0 = r188;
        r0.captionHeight = r4;	 Catch:{ Exception -> 0x4502 }
        r77 = 2;
    L_0x142f:
        r0 = r188;
        r4 = r0.captionLayout;
        if (r4 != 0) goto L_0x145a;
    L_0x1435:
        r0 = r188;
        r4 = r0.widthBeforeNewTimeLine;
        r6 = -1;
        if (r4 == r6) goto L_0x145a;
    L_0x143c:
        r0 = r188;
        r4 = r0.availableTimeWidth;
        r0 = r188;
        r6 = r0.widthBeforeNewTimeLine;
        r4 = r4 - r6;
        r0 = r188;
        r6 = r0.timeWidth;
        if (r4 >= r6) goto L_0x145a;
    L_0x144b:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
    L_0x145a:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r8 = r4.eventId;
        r20 = 0;
        r4 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1));
        if (r4 == 0) goto L_0x455a;
    L_0x1466:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.isMediaEmpty();
        if (r4 != 0) goto L_0x455a;
    L_0x1470:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        if (r4 == 0) goto L_0x455a;
    L_0x147c:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42240000 float:41.0 double:5.48242687E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r4 - r6;
        r4 = 1;
        r0 = r188;
        r0.hasOldCaptionPreview = r4;
        r4 = 0;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.media;
        r0 = r4.webpage;
        r181 = r0;
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x450b }
        r0 = r181;
        r6 = r0.site_name;	 Catch:{ Exception -> 0x450b }
        r4 = r4.measureText(r6);	 Catch:{ Exception -> 0x450b }
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = r4 + r6;
        r8 = (double) r4;	 Catch:{ Exception -> 0x450b }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x450b }
        r0 = (int) r8;	 Catch:{ Exception -> 0x450b }
        r183 = r0;
        r0 = r183;
        r1 = r188;
        r1.siteNameWidth = r0;	 Catch:{ Exception -> 0x450b }
        r44 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x450b }
        r0 = r181;
        r0 = r0.site_name;	 Catch:{ Exception -> 0x450b }
        r45 = r0;
        r46 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x450b }
        r0 = r183;
        r47 = java.lang.Math.min(r0, r11);	 Catch:{ Exception -> 0x450b }
        r48 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x450b }
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r50 = 0;
        r51 = 0;
        r44.<init>(r45, r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x450b }
        r0 = r44;
        r1 = r188;
        r1.siteNameLayout = r0;	 Catch:{ Exception -> 0x450b }
        r0 = r188;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x450b }
        r6 = 0;
        r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x450b }
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x4508;
    L_0x14e8:
        r4 = 1;
    L_0x14e9:
        r0 = r188;
        r0.siteNameRtl = r4;	 Catch:{ Exception -> 0x450b }
        r0 = r188;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x450b }
        r0 = r188;
        r6 = r0.siteNameLayout;	 Catch:{ Exception -> 0x450b }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x450b }
        r6 = r6 + -1;
        r101 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x450b }
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x450b }
        r4 = r4 + r101;
        r0 = r188;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x450b }
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x450b }
        r4 = r4 + r101;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x450b }
    L_0x1513:
        r4 = 0;
        r0 = r188;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x4522 }
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x4522 }
        if (r4 == 0) goto L_0x152d;
    L_0x151e:
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4522 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4522 }
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4522 }
    L_0x152d:
        r0 = r181;
        r0 = r0.description;	 Catch:{ Exception -> 0x4522 }
        r44 = r0;
        r45 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x4522 }
        r47 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x4522 }
        r48 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4522 }
        r0 = (float) r4;	 Catch:{ Exception -> 0x4522 }
        r49 = r0;
        r50 = 0;
        r51 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x4522 }
        r53 = 6;
        r46 = r11;
        r52 = r11;
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r44, r45, r46, r47, r48, r49, r50, r51, r52, r53);	 Catch:{ Exception -> 0x4522 }
        r0 = r188;
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x4522 }
        r0 = r188;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x4522 }
        r0 = r188;
        r6 = r0.descriptionLayout;	 Catch:{ Exception -> 0x4522 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x4522 }
        r6 = r6 + -1;
        r101 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x4522 }
        r0 = r188;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x4522 }
        r4 = r4 + r101;
        r0 = r188;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x4522 }
        r0 = r188;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x4522 }
        r4 = r4 + r101;
        r0 = r188;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x4522 }
        r56 = 0;
    L_0x157c:
        r0 = r188;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x4522 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x4522 }
        r0 = r56;
        if (r0 >= r4) goto L_0x4526;
    L_0x1588:
        r0 = r188;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x4522 }
        r0 = r56;
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x4522 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x4522 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x4522 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x4522 }
        r109 = r0;
        if (r109 == 0) goto L_0x15a9;
    L_0x159c:
        r0 = r188;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x4522 }
        if (r4 != 0) goto L_0x4511;
    L_0x15a2:
        r0 = r109;
        r4 = -r0;
        r0 = r188;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x4522 }
    L_0x15a9:
        r56 = r56 + 1;
        goto L_0x157c;
    L_0x15ac:
        r14 = 6;
        goto L_0x0var_;
    L_0x15af:
        r0 = r188;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x0f7c }
        r0 = r109;
        r6 = -r0;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x0f7c }
        r0 = r188;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0f7c }
        goto L_0x0var_;
    L_0x15c0:
        r0 = r188;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f7c }
        r166 = r4.getWidth();	 Catch:{ Exception -> 0x0f7c }
        r56 = 0;
    L_0x15ca:
        r0 = r188;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f7c }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0f7c }
        r0 = r56;
        if (r0 >= r4) goto L_0x0var_;
    L_0x15d6:
        r0 = r188;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f7c }
        r0 = r56;
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x0f7c }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0f7c }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0f7c }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0f7c }
        r109 = r0;
        if (r109 != 0) goto L_0x15f5;
    L_0x15ea:
        r0 = r188;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x0f7c }
        if (r4 == 0) goto L_0x15f5;
    L_0x15f0:
        r4 = 0;
        r0 = r188;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0f7c }
    L_0x15f5:
        if (r109 == 0) goto L_0x1642;
    L_0x15f7:
        r183 = r166 - r109;
    L_0x15f9:
        r0 = r56;
        r1 = r149;
        if (r0 < r1) goto L_0x1609;
    L_0x15ff:
        if (r149 == 0) goto L_0x1611;
    L_0x1601:
        if (r109 == 0) goto L_0x1611;
    L_0x1603:
        r0 = r188;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0f7c }
        if (r4 == 0) goto L_0x1611;
    L_0x1609:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0f7c }
        r183 = r183 + r4;
    L_0x1611:
        r4 = r183 + r57;
        r0 = r120;
        if (r0 >= r4) goto L_0x1637;
    L_0x1617:
        if (r173 == 0) goto L_0x1626;
    L_0x1619:
        r0 = r188;
        r4 = r0.titleX;	 Catch:{ Exception -> 0x0f7c }
        r6 = r183 + r57;
        r6 = r6 - r120;
        r4 = r4 + r6;
        r0 = r188;
        r0.titleX = r4;	 Catch:{ Exception -> 0x0f7c }
    L_0x1626:
        if (r66 == 0) goto L_0x1635;
    L_0x1628:
        r0 = r188;
        r4 = r0.authorX;	 Catch:{ Exception -> 0x0f7c }
        r6 = r183 + r57;
        r6 = r6 - r120;
        r4 = r4 + r6;
        r0 = r188;
        r0.authorX = r4;	 Catch:{ Exception -> 0x0f7c }
    L_0x1635:
        r120 = r183 + r57;
    L_0x1637:
        r4 = r183 + r57;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0f7c }
        r56 = r56 + 1;
        goto L_0x15ca;
    L_0x1642:
        if (r100 == 0) goto L_0x1647;
    L_0x1644:
        r183 = r166;
        goto L_0x15f9;
    L_0x1647:
        r0 = r188;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0f7c }
        r0 = r56;
        r4 = r4.getLineWidth(r0);	 Catch:{ Exception -> 0x0f7c }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0f7c }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0f7c }
        r4 = (int) r8;	 Catch:{ Exception -> 0x0f7c }
        r0 = r166;
        r183 = java.lang.Math.min(r4, r0);	 Catch:{ Exception -> 0x0f7c }
        goto L_0x15f9;
    L_0x165e:
        r117 = r11;
        goto L_0x0fa8;
    L_0x1662:
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r87);
        if (r4 == 0) goto L_0x1719;
    L_0x1668:
        r4 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r4 != 0) goto L_0x1672;
    L_0x166c:
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = r189;
        r0.gifState = r4;
    L_0x1672:
        r0 = r188;
        r6 = r0.photoImage;
        r0 = r189;
        r4 = r0.gifState;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x1713;
    L_0x1680:
        r4 = 1;
    L_0x1681:
        r6.setAllowStartAnimation(r4);
        r0 = r87;
        r4 = r0.thumbs;
        r6 = 90;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObject = r4;
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x1704;
    L_0x1698:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x16a8;
    L_0x16a0:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x1704;
    L_0x16a8:
        r56 = 0;
    L_0x16aa:
        r0 = r87;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r56;
        if (r0 >= r4) goto L_0x16e2;
    L_0x16b6:
        r0 = r87;
        r4 = r0.attributes;
        r0 = r56;
        r64 = r4.get(r0);
        r64 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r64;
        r0 = r64;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 != 0) goto L_0x16ce;
    L_0x16c8:
        r0 = r64;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x1716;
    L_0x16ce:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r64;
        r6 = r0.w;
        r4.w = r6;
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r64;
        r6 = r0.h;
        r4.h = r6;
    L_0x16e2:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x16f2;
    L_0x16ea:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x1704;
    L_0x16f2:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r188;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.h = r8;
        r4.w = r8;
    L_0x1704:
        r0 = r87;
        r1 = r188;
        r1.documentAttach = r0;
        r4 = 2;
        r0 = r188;
        r0.documentAttachType = r4;
        r15 = r180;
        goto L_0x0fcb;
    L_0x1713:
        r4 = 0;
        goto L_0x1681;
    L_0x1716:
        r56 = r56 + 1;
        goto L_0x16aa;
    L_0x1719:
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r87);
        if (r4 == 0) goto L_0x17c1;
    L_0x171f:
        if (r138 == 0) goto L_0x1732;
    L_0x1721:
        r0 = r138;
        r4 = r0.sizes;
        r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r8 = 1;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6, r8);
        r0 = r188;
        r0.currentPhotoObject = r4;
    L_0x1732:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x1746;
    L_0x1738:
        r0 = r87;
        r4 = r0.thumbs;
        r6 = 90;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObject = r4;
    L_0x1746:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x17b2;
    L_0x174c:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x175c;
    L_0x1754:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x17b2;
    L_0x175c:
        r56 = 0;
    L_0x175e:
        r0 = r87;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r56;
        if (r0 >= r4) goto L_0x1790;
    L_0x176a:
        r0 = r87;
        r4 = r0.attributes;
        r0 = r56;
        r64 = r4.get(r0);
        r64 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r64;
        r0 = r64;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x17be;
    L_0x177c:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r64;
        r6 = r0.w;
        r4.w = r6;
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r64;
        r6 = r0.h;
        r4.h = r6;
    L_0x1790:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x17a0;
    L_0x1798:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x17b2;
    L_0x17a0:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r188;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.h = r8;
        r4.w = r8;
    L_0x17b2:
        r4 = 0;
        r0 = r188;
        r1 = r189;
        r0.createDocumentLayout(r4, r1);
        r15 = r180;
        goto L_0x0fcb;
    L_0x17be:
        r56 = r56 + 1;
        goto L_0x175e;
    L_0x17c1:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r87);
        if (r4 == 0) goto L_0x1853;
    L_0x17c7:
        r0 = r87;
        r4 = r0.thumbs;
        r6 = 90;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObject = r4;
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x1841;
    L_0x17db:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x17eb;
    L_0x17e3:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x1841;
    L_0x17eb:
        r56 = 0;
    L_0x17ed:
        r0 = r87;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r56;
        if (r0 >= r4) goto L_0x181f;
    L_0x17f9:
        r0 = r87;
        r4 = r0.attributes;
        r0 = r56;
        r64 = r4.get(r0);
        r64 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r64;
        r0 = r64;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 == 0) goto L_0x1850;
    L_0x180b:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r64;
        r6 = r0.w;
        r4.w = r6;
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r64;
        r6 = r0.h;
        r4.h = r6;
    L_0x181f:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x182f;
    L_0x1827:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x1841;
    L_0x182f:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r188;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.h = r8;
        r4.w = r8;
    L_0x1841:
        r0 = r87;
        r1 = r188;
        r1.documentAttach = r0;
        r4 = 6;
        r0 = r188;
        r0.documentAttachType = r4;
        r15 = r180;
        goto L_0x0fcb;
    L_0x1850:
        r56 = r56 + 1;
        goto L_0x17ed;
    L_0x1853:
        r0 = r188;
        r4 = r0.drawInstantViewType;
        r6 = 6;
        if (r4 != r6) goto L_0x191d;
    L_0x185a:
        r0 = r87;
        r4 = r0.thumbs;
        r6 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObject = r4;
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x18d4;
    L_0x186e:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x187e;
    L_0x1876:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x18d4;
    L_0x187e:
        r56 = 0;
    L_0x1880:
        r0 = r87;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r56;
        if (r0 >= r4) goto L_0x18b2;
    L_0x188c:
        r0 = r87;
        r4 = r0.attributes;
        r0 = r56;
        r64 = r4.get(r0);
        r64 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r64;
        r0 = r64;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 == 0) goto L_0x1919;
    L_0x189e:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r64;
        r6 = r0.w;
        r4.w = r6;
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r64;
        r6 = r0.h;
        r4.h = r6;
    L_0x18b2:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x18c2;
    L_0x18ba:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.h;
        if (r4 != 0) goto L_0x18d4;
    L_0x18c2:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r188;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.h = r8;
        r4.w = r8;
    L_0x18d4:
        r0 = r87;
        r1 = r188;
        r1.documentAttach = r0;
        r4 = 8;
        r0 = r188;
        r0.documentAttachType = r4;
        r0 = r188;
        r4 = r0.documentAttach;
        r4 = r4.size;
        r8 = (long) r4;
        r5 = org.telegram.messenger.AndroidUtilities.formatFileSize(r8);
        r4 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r188;
        r0.durationWidth = r4;
        r14 = new android.text.StaticLayout;
        r16 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r0 = r188;
        r0 = r0.durationWidth;
        r17 = r0;
        r18 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r19 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r20 = 0;
        r21 = 0;
        r15 = r5;
        r14.<init>(r15, r16, r17, r18, r19, r20, r21);
        r0 = r188;
        r0.videoInfoLayout = r14;
        r15 = r180;
        goto L_0x0fcb;
    L_0x1919:
        r56 = r56 + 1;
        goto L_0x1880;
    L_0x191d:
        r0 = r188;
        r1 = r43;
        r2 = r170;
        r3 = r115;
        r0.calcBackgroundWidth(r1, r2, r3);
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r43;
        if (r4 >= r6) goto L_0x1942;
    L_0x1936:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r43;
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x1942:
        r4 = org.telegram.messenger.MessageObject.isVoiceDocument(r87);
        if (r4 == 0) goto L_0x1a1e;
    L_0x1948:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r1 = r189;
        r0.createDocumentLayout(r4, r1);
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.textHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r6 = r0.linkPreviewHeight;
        r4 = r4 + r6;
        r0 = r188;
        r0.mediaOffsetY = r4;
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        r4 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r43 = r43 - r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x19e4;
    L_0x199c:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x19e2;
    L_0x19a6:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x19e2;
    L_0x19ac:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x19e2;
    L_0x19b2:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
    L_0x19b4:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x435CLASSNAME float:220.0 double:5.58344962E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r4 = r4 + r57;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);
    L_0x19d3:
        r0 = r188;
        r1 = r43;
        r2 = r170;
        r3 = r115;
        r0.calcBackgroundWidth(r1, r2, r3);
        r15 = r180;
        goto L_0x0fcb;
    L_0x19e2:
        r4 = 0;
        goto L_0x19b4;
    L_0x19e4:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x1a1c;
    L_0x19ee:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x1a1c;
    L_0x19f4:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x1a1c;
    L_0x19fa:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
    L_0x19fc:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x435CLASSNAME float:220.0 double:5.58344962E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r4 = r4 + r57;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);
        goto L_0x19d3;
    L_0x1a1c:
        r4 = 0;
        goto L_0x19fc;
    L_0x1a1e:
        r4 = org.telegram.messenger.MessageObject.isMusicDocument(r87);
        if (r4 == 0) goto L_0x1aef;
    L_0x1a24:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r1 = r189;
        r89 = r0.createDocumentLayout(r4, r1);
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.textHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r6 = r0.linkPreviewHeight;
        r4 = r4 + r6;
        r0 = r188;
        r0.mediaOffsetY = r4;
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        r4 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r43 = r43 - r4;
        r4 = r89 + r57;
        r6 = NUM; // 0x42bCLASSNAME float:94.0 double:5.53164308E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r115;
        r115 = java.lang.Math.max(r0, r4);
        r0 = r188;
        r4 = r0.songLayout;
        if (r4 == 0) goto L_0x1ab1;
    L_0x1a88:
        r0 = r188;
        r4 = r0.songLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x1ab1;
    L_0x1a92:
        r0 = r115;
        r4 = (float) r0;
        r0 = r188;
        r6 = r0.songLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r0 = r57;
        r8 = (float) r0;
        r6 = r6 + r8;
        r8 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r6 = r6 + r8;
        r4 = java.lang.Math.max(r4, r6);
        r0 = (int) r4;
        r115 = r0;
    L_0x1ab1:
        r0 = r188;
        r4 = r0.performerLayout;
        if (r4 == 0) goto L_0x1ae0;
    L_0x1ab7:
        r0 = r188;
        r4 = r0.performerLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x1ae0;
    L_0x1ac1:
        r0 = r115;
        r4 = (float) r0;
        r0 = r188;
        r6 = r0.performerLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r0 = r57;
        r8 = (float) r0;
        r6 = r6 + r8;
        r8 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r6 = r6 + r8;
        r4 = java.lang.Math.max(r4, r6);
        r0 = (int) r4;
        r115 = r0;
    L_0x1ae0:
        r0 = r188;
        r1 = r43;
        r2 = r170;
        r3 = r115;
        r0.calcBackgroundWidth(r1, r2, r3);
        r15 = r180;
        goto L_0x0fcb;
    L_0x1aef:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x43280000 float:168.0 double:5.566612494E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r1 = r189;
        r0.createDocumentLayout(r4, r1);
        r4 = 1;
        r0 = r188;
        r0.drawImageButton = r4;
        r0 = r188;
        r4 = r0.drawPhotoImage;
        if (r4 == 0) goto L_0x1b4b;
    L_0x1b0c:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r0 = r188;
        r8 = r0.totalHeight;
        r0 = r188;
        r9 = r0.namesOffset;
        r8 = r8 + r9;
        r9 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r4.setImageCoords(r6, r8, r9, r10);
        r15 = r180;
        goto L_0x0fcb;
    L_0x1b4b:
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.textHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r6 = r0.linkPreviewHeight;
        r4 = r4 + r6;
        r0 = r188;
        r0.mediaOffsetY = r4;
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r0 = r188;
        r8 = r0.totalHeight;
        r0 = r188;
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
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        r0 = r188;
        r4 = r0.docTitleLayout;
        if (r4 == 0) goto L_0x4935;
    L_0x1ba9:
        r0 = r188;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        r6 = 1;
        if (r4 <= r6) goto L_0x4935;
    L_0x1bb4:
        r0 = r188;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        r4 = r4 + -1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r97 = r4 * r6;
        r0 = r188;
        r4 = r0.totalHeight;
        r4 = r4 + r97;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        r4 = r4 + r97;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        r15 = r180;
        goto L_0x0fcb;
    L_0x1bde:
        if (r138 == 0) goto L_0x1CLASSNAME;
    L_0x1be0:
        if (r175 == 0) goto L_0x1CLASSNAME;
    L_0x1be2:
        r4 = "photo";
        r0 = r175;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x1CLASSNAME;
    L_0x1bed:
        r4 = 1;
    L_0x1bee:
        r0 = r188;
        r0.drawImageButton = r4;
        r0 = r189;
        r8 = r0.photoThumbs;
        r0 = r188;
        r4 = r0.drawImageButton;
        if (r4 == 0) goto L_0x1CLASSNAME;
    L_0x1bfc:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x1CLASSNAME:
        r0 = r188;
        r6 = r0.drawImageButton;
        if (r6 != 0) goto L_0x1CLASSNAME;
    L_0x1CLASSNAME:
        r6 = 1;
    L_0x1CLASSNAME:
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r4, r6);
        r0 = r188;
        r0.currentPhotoObject = r4;
        r0 = r189;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObjectThumb = r4;
        r0 = r188;
        r4 = r0.currentPhotoObjectThumb;
        r0 = r188;
        r6 = r0.currentPhotoObject;
        if (r4 != r6) goto L_0x4935;
    L_0x1CLASSNAME:
        r4 = 0;
        r0 = r188;
        r0.currentPhotoObjectThumb = r4;
        r15 = r180;
        goto L_0x0fcb;
    L_0x1CLASSNAME:
        r4 = 0;
        goto L_0x1bee;
    L_0x1CLASSNAME:
        r4 = r117;
        goto L_0x1CLASSNAME;
    L_0x1CLASSNAME:
        r6 = 0;
        goto L_0x1CLASSNAME;
    L_0x1CLASSNAME:
        if (r180 == 0) goto L_0x4935;
    L_0x1CLASSNAME:
        r0 = r180;
        r4 = r0.mime_type;
        r6 = "image/";
        r4 = r4.startsWith(r6);
        if (r4 != 0) goto L_0x4931;
    L_0x1CLASSNAME:
        r15 = 0;
    L_0x1CLASSNAME:
        r4 = 0;
        r0 = r188;
        r0.drawImageButton = r4;
        goto L_0x0fcb;
    L_0x1c4e:
        r4 = 0;
        goto L_0x1022;
    L_0x1CLASSNAME:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r4 == 0) goto L_0x1c6b;
    L_0x1CLASSNAME:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.w;
        if (r4 == 0) goto L_0x1c6b;
    L_0x1CLASSNAME:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r4.w;
        r117 = r0;
        goto L_0x1056;
    L_0x1c6b:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 6;
        if (r4 == r6) goto L_0x1c7a;
    L_0x1CLASSNAME:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 8;
        if (r4 != r6) goto L_0x1c9a;
    L_0x1c7a:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1c8d;
    L_0x1CLASSNAME:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r117 = r0;
        goto L_0x1056;
    L_0x1c8d:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r117 = r0;
        goto L_0x1056;
    L_0x1c9a:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 7;
        if (r4 != r6) goto L_0x1056;
    L_0x1ca1:
        r117 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setAllowDecodeSingleFrame(r6);
        goto L_0x1056;
    L_0x1cad:
        r4 = 0;
        goto L_0x1062;
    L_0x1cb0:
        r4 = -1;
        r15.size = r4;
        goto L_0x1086;
    L_0x1cb5:
        r0 = r188;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x1cc1;
    L_0x1cbb:
        r0 = r188;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x1ce5;
    L_0x1cc1:
        r183 = 640; // 0x280 float:8.97E-43 double:3.16E-321;
        r101 = 360; // 0x168 float:5.04E-43 double:1.78E-321;
        r0 = r183;
        r4 = (float) r0;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r117 - r6;
        r6 = (float) r6;
        r157 = r4 / r6;
        r0 = r183;
        r4 = (float) r0;
        r4 = r4 / r157;
        r0 = (int) r4;
        r183 = r0;
        r0 = r101;
        r4 = (float) r0;
        r4 = r4 / r157;
        r0 = (int) r4;
        r101 = r0;
        goto L_0x10a5;
    L_0x1ce5:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r4.w;
        r183 = r0;
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r4.h;
        r101 = r0;
        r0 = r183;
        r4 = (float) r0;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r117 - r6;
        r6 = (float) r6;
        r157 = r4 / r6;
        r0 = r183;
        r4 = (float) r0;
        r4 = r4 / r157;
        r0 = (int) r4;
        r183 = r0;
        r0 = r101;
        r4 = (float) r0;
        r4 = r4 / r157;
        r0 = (int) r4;
        r101 = r0;
        if (r7 == 0) goto L_0x1d2a;
    L_0x1d15:
        if (r7 == 0) goto L_0x1d5e;
    L_0x1d17:
        r4 = r7.toLowerCase();
        r6 = "instagram";
        r4 = r4.equals(r6);
        if (r4 != 0) goto L_0x1d5e;
    L_0x1d24:
        r0 = r188;
        r4 = r0.documentAttachType;
        if (r4 != 0) goto L_0x1d5e;
    L_0x1d2a:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r4 = r4 / 3;
        r0 = r101;
        if (r0 <= r4) goto L_0x1d3a;
    L_0x1d34:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r101 = r4 / 3;
    L_0x1d3a:
        r0 = r188;
        r4 = r0.imageBackgroundSideColor;
        if (r4 == 0) goto L_0x10a5;
    L_0x1d40:
        r0 = r101;
        r4 = (float) r0;
        r6 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r157 = r4 / r6;
        r0 = r183;
        r4 = (float) r0;
        r4 = r4 / r157;
        r0 = (int) r4;
        r183 = r0;
        r0 = r101;
        r4 = (float) r0;
        r4 = r4 / r157;
        r0 = (int) r4;
        r101 = r0;
        goto L_0x10a5;
    L_0x1d5e:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r4 = r4 / 2;
        r0 = r101;
        if (r0 <= r4) goto L_0x1d3a;
    L_0x1d68:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r101 = r4 / 2;
        goto L_0x1d3a;
    L_0x1d6f:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r101;
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        r4 = r4 + r101;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        goto L_0x10f1;
    L_0x1d8c:
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r8 = 0;
        r0 = r183;
        r1 = r101;
        r4.setImageCoords(r6, r8, r0, r1);
        goto L_0x1118;
    L_0x1d9b:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 8;
        if (r4 != r6) goto L_0x1dfb;
    L_0x1da3:
        r0 = r189;
        r4 = r0.mediaExists;
        if (r4 == 0) goto L_0x1dd4;
    L_0x1da9:
        r0 = r188;
        r0 = r0.photoImage;
        r16 = r0;
        r0 = r188;
        r0 = r0.documentAttach;
        r17 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilter;
        r18 = r0;
        r19 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r20 = r0;
        r21 = "b1";
        r22 = 0;
        r23 = "jpg";
        r25 = 1;
        r24 = r189;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24, r25);
        goto L_0x1174;
    L_0x1dd4:
        r0 = r188;
        r0 = r0.photoImage;
        r16 = r0;
        r17 = 0;
        r0 = r188;
        r0 = r0.currentPhotoFilter;
        r18 = r0;
        r19 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r20 = r0;
        r21 = "b1";
        r22 = 0;
        r23 = "jpg";
        r25 = 1;
        r24 = r189;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24, r25);
        goto L_0x1174;
    L_0x1dfb:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 6;
        if (r4 != r6) goto L_0x1e33;
    L_0x1e02:
        r0 = r188;
        r0 = r0.photoImage;
        r16 = r0;
        r0 = r188;
        r0 = r0.documentAttach;
        r17 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilter;
        r18 = r0;
        r19 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r20 = r0;
        r21 = "b1";
        r0 = r188;
        r4 = r0.documentAttach;
        r0 = r4.size;
        r22 = r0;
        r23 = "webp";
        r25 = 1;
        r24 = r189;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24, r25);
        goto L_0x1174;
    L_0x1e33:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 4;
        if (r4 != r6) goto L_0x1e6d;
    L_0x1e3a:
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setNeedsQualityThumb(r6);
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setShouldGenerateQualityThumb(r6);
        r0 = r188;
        r0 = r0.photoImage;
        r16 = r0;
        r17 = 0;
        r18 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r19 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilter;
        r20 = r0;
        r21 = 0;
        r22 = 0;
        r24 = 0;
        r23 = r189;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x1174;
    L_0x1e6d:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 2;
        if (r4 == r6) goto L_0x1e7b;
    L_0x1e74:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 7;
        if (r4 != r6) goto L_0x1var_;
    L_0x1e7b:
        r91 = org.telegram.messenger.FileLoader.getAttachFileName(r87);
        r67 = 0;
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r87);
        if (r4 == 0) goto L_0x1ef6;
    L_0x1e87:
        r0 = r188;
        r4 = r0.photoImage;
        r6 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r6 = r6 / 2;
        r4.setRoundRadius(r6);
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r188;
        r6 = r0.currentMessageObject;
        r67 = r4.canDownloadMedia(r6);
    L_0x1ea2:
        r4 = r189.isSending();
        if (r4 != 0) goto L_0x1f0d;
    L_0x1ea8:
        r4 = r189.isEditing();
        if (r4 != 0) goto L_0x1f0d;
    L_0x1eae:
        r0 = r189;
        r4 = r0.mediaExists;
        if (r4 != 0) goto L_0x1ec6;
    L_0x1eb4:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r91;
        r4 = r4.isLoadingFile(r0);
        if (r4 != 0) goto L_0x1ec6;
    L_0x1ec4:
        if (r67 == 0) goto L_0x1f0d;
    L_0x1ec6:
        r4 = 0;
        r0 = r188;
        r0.photoNotSet = r4;
        r0 = r188;
        r0 = r0.photoImage;
        r16 = r0;
        r18 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r19 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilterThumb;
        r20 = r0;
        r0 = r87;
        r0 = r0.size;
        r21 = r0;
        r22 = 0;
        r0 = r188;
        r0 = r0.currentMessageObject;
        r23 = r0;
        r24 = 0;
        r17 = r87;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x1174;
    L_0x1ef6:
        r4 = org.telegram.messenger.MessageObject.isNewGifDocument(r87);
        if (r4 == 0) goto L_0x1ea2;
    L_0x1efc:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r188;
        r6 = r0.currentMessageObject;
        r67 = r4.canDownloadMedia(r6);
        goto L_0x1ea2;
    L_0x1f0d:
        r4 = 1;
        r0 = r188;
        r0.photoNotSet = r4;
        r0 = r188;
        r0 = r0.photoImage;
        r16 = r0;
        r17 = 0;
        r18 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r19 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilterThumb;
        r20 = r0;
        r21 = 0;
        r22 = 0;
        r0 = r188;
        r0 = r0.currentMessageObject;
        r23 = r0;
        r24 = 0;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x1174;
    L_0x1var_:
        r0 = r189;
        r0 = r0.mediaExists;
        r139 = r0;
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r91 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r0 = r188;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x1var_;
    L_0x1f4d:
        if (r139 != 0) goto L_0x1var_;
    L_0x1f4f:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r188;
        r6 = r0.currentMessageObject;
        r4 = r4.canDownloadMedia(r6);
        if (r4 != 0) goto L_0x1var_;
    L_0x1var_:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r91;
        r4 = r4.isLoadingFile(r0);
        if (r4 == 0) goto L_0x1fa1;
    L_0x1var_:
        r4 = 0;
        r0 = r188;
        r0.photoNotSet = r4;
        r0 = r188;
        r0 = r0.photoImage;
        r16 = r0;
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r17 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilter;
        r18 = r0;
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r19 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilterThumb;
        r20 = r0;
        r21 = 0;
        r22 = 0;
        r24 = 0;
        r23 = r189;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x1174;
    L_0x1fa1:
        r4 = 1;
        r0 = r188;
        r0.photoNotSet = r4;
        r0 = r188;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x1fe3;
    L_0x1fac:
        r0 = r188;
        r0 = r0.photoImage;
        r16 = r0;
        r17 = 0;
        r18 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r19 = r0;
        r4 = java.util.Locale.US;
        r6 = "%d_%d_b";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r183);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r101);
        r8[r9] = r10;
        r20 = java.lang.String.format(r4, r6, r8);
        r21 = 0;
        r22 = 0;
        r24 = 0;
        r23 = r189;
        r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x1174;
    L_0x1fe3:
        r0 = r188;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        goto L_0x1174;
    L_0x1fef:
        r0 = r188;
        r4 = r0.hasGamePreview;
        if (r4 == 0) goto L_0x11d3;
    L_0x1ff5:
        r4 = "AttachGame";
        r6 = NUM; // 0x7f0CLASSNAMEcc float:1.8609606E38 double:1.0530974992E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
        r4 = org.telegram.ui.ActionBar.Theme.chat_gamePaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r188;
        r0.durationWidth = r4;
        r16 = new android.text.StaticLayout;
        r18 = org.telegram.ui.ActionBar.Theme.chat_gamePaint;
        r0 = r188;
        r0 = r0.durationWidth;
        r19 = r0;
        r20 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r22 = 0;
        r23 = 0;
        r17 = r5;
        r16.<init>(r17, r18, r19, r20, r21, r22, r23);
        r0 = r16;
        r1 = r188;
        r1.videoInfoLayout = r0;
        goto L_0x11d3;
    L_0x2032:
        r0 = r188;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        r0 = r188;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.linkPreviewHeight = r4;
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        goto L_0x11d3;
    L_0x205c:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.test;
        if (r4 == 0) goto L_0x2076;
    L_0x2066:
        r4 = "PaymentTestInvoice";
        r6 = NUM; // 0x7f0CLASSNAMEb9 float:1.8612683E38 double:1.0530982487E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
        goto L_0x11f3;
    L_0x2076:
        r4 = "PaymentInvoice";
        r6 = NUM; // 0x7f0CLASSNAMEa float:1.861262E38 double:1.0530982334E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
        goto L_0x11f3;
    L_0x2086:
        r4 = 0;
        goto L_0x129d;
    L_0x2089:
        r0 = r188;
        r4 = r0.durationWidth;
        r4 = r4 + r171;
        r0 = r115;
        r115 = java.lang.Math.max(r4, r0);
        goto L_0x12c9;
    L_0x2097:
        r0 = r188;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        r0 = r188;
        r1 = r43;
        r2 = r170;
        r3 = r115;
        r0.calcBackgroundWidth(r1, r2, r3);
        goto L_0x1306;
    L_0x20ae:
        r0 = r189;
        r4 = r0.type;
        r6 = 16;
        if (r4 != r6) goto L_0x2267;
    L_0x20b6:
        r4 = 0;
        r0 = r188;
        r0.drawName = r4;
        r4 = 0;
        r0 = r188;
        r0.drawForwardedName = r4;
        r4 = 0;
        r0 = r188;
        r0.drawPhotoImage = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x21fc;
    L_0x20cb:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x21f8;
    L_0x20d5:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x21f8;
    L_0x20db:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x21f8;
    L_0x20e1:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x20e3:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x20f7:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.availableTimeWidth = r4;
        r4 = r188.getMaxNameWidth();
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        if (r43 >= 0) goto L_0x211a;
    L_0x2114:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r43 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x211a:
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r4 = r4.formatterDay;
        r0 = r189;
        r6 = r0.messageOwner;
        r6 = r6.date;
        r8 = (long) r6;
        r20 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r8 = r8 * r20;
        r168 = r4.format(r8);
        r0 = r189;
        r4 = r0.messageOwner;
        r0 = r4.action;
        r76 = r0;
        r76 = (org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall) r76;
        r0 = r76;
        r4 = r0.reason;
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        r105 = r0;
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x2239;
    L_0x2147:
        if (r105 == 0) goto L_0x222d;
    L_0x2149:
        r4 = "CallMessageOutgoingMissed";
        r6 = NUM; // 0x7f0CLASSNAMEb float:1.860983E38 double:1.053097554E-314;
        r165 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x2153:
        r0 = r76;
        r4 = r0.duration;
        if (r4 <= 0) goto L_0x217b;
    L_0x2159:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r168;
        r4 = r4.append(r0);
        r6 = ", ";
        r4 = r4.append(r6);
        r0 = r76;
        r6 = r0.duration;
        r6 = org.telegram.messenger.LocaleController.formatCallDuration(r6);
        r4 = r4.append(r6);
        r168 = r4.toString();
    L_0x217b:
        r18 = new android.text.StaticLayout;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r0 = r43;
        r6 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r165;
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
        r1 = r188;
        r1.titleLayout = r0;
        r18 = new android.text.StaticLayout;
        r4 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r0 = r43;
        r6 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r168;
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
        r1 = r188;
        r1.docTitleLayout = r0;
        r188.setMessageObjectInternal(r189);
        r4 = NUM; // 0x42820000 float:65.0 double:5.51286321E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r188;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x1306;
    L_0x21e7:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.namesOffset = r4;
        goto L_0x1306;
    L_0x21f8:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x20e3;
    L_0x21fc:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x222a;
    L_0x2206:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x222a;
    L_0x220c:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x222a;
    L_0x2212:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x2214:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x20f7;
    L_0x222a:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2214;
    L_0x222d:
        r4 = "CallMessageOutgoing";
        r6 = NUM; // 0x7f0CLASSNAMEa float:1.8609829E38 double:1.0530975536E-314;
        r165 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x2153;
    L_0x2239:
        if (r105 == 0) goto L_0x2247;
    L_0x223b:
        r4 = "CallMessageIncomingMissed";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8609827E38 double:1.053097553E-314;
        r165 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x2153;
    L_0x2247:
        r0 = r76;
        r4 = r0.reason;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
        if (r4 == 0) goto L_0x225b;
    L_0x224f:
        r4 = "CallMessageIncomingDeclined";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8609825E38 double:1.0530975526E-314;
        r165 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x2153;
    L_0x225b:
        r4 = "CallMessageIncoming";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8609823E38 double:1.053097552E-314;
        r165 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x2153;
    L_0x2267:
        r0 = r189;
        r4 = r0.type;
        r6 = 12;
        if (r4 != r6) goto L_0x2503;
    L_0x226f:
        r4 = 0;
        r0 = r188;
        r0.drawName = r4;
        r4 = 1;
        r0 = r188;
        r0.drawForwardedName = r4;
        r4 = 1;
        r0 = r188;
        r0.drawPhotoImage = r4;
        r0 = r188;
        r4 = r0.photoImage;
        r6 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setRoundRadius(r6);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2432;
    L_0x2291:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x242e;
    L_0x229b:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x242e;
    L_0x22a1:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x242e;
    L_0x22a7:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x22a9:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x22bd:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.availableTimeWidth = r4;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.user_id;
        r176 = r0;
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r6 = java.lang.Integer.valueOf(r176);
        r178 = r4.getUser(r6);
        r4 = r188.getMaxNameWidth();
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        if (r43 >= 0) goto L_0x22fa;
    L_0x22f4:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r43 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x22fa:
        r19 = 0;
        if (r178 == 0) goto L_0x2315;
    L_0x22fe:
        r0 = r178;
        r4 = r0.photo;
        if (r4 == 0) goto L_0x230c;
    L_0x2304:
        r0 = r178;
        r4 = r0.photo;
        r0 = r4.photo_small;
        r19 = r0;
    L_0x230c:
        r0 = r188;
        r4 = r0.contactAvatarDrawable;
        r0 = r178;
        r4.setInfo(r0);
    L_0x2315:
        r0 = r188;
        r0 = r0.photoImage;
        r18 = r0;
        r20 = "50_50";
        if (r178 == 0) goto L_0x2463;
    L_0x2320:
        r0 = r188;
        r0 = r0.contactAvatarDrawable;
        r21 = r0;
    L_0x2326:
        r22 = 0;
        r24 = 0;
        r23 = r189;
        r18.setImage(r19, r20, r21, r22, r23, r24);
        r0 = r189;
        r4 = r0.vCardData;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x2472;
    L_0x2339:
        r0 = r189;
        r0 = r0.vCardData;
        r137 = r0;
        r4 = 1;
        r0 = r188;
        r0.drawInstantView = r4;
        r4 = 5;
        r0 = r188;
        r0.drawInstantViewType = r4;
    L_0x2349:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.first_name;
        r0 = r189;
        r6 = r0.messageOwner;
        r6 = r6.media;
        r6 = r6.last_name;
        r4 = org.telegram.messenger.ContactsController.formatName(r4, r6);
        r6 = 10;
        r8 = 32;
        r82 = r4.replace(r6, r8);
        r4 = r82.length();
        if (r4 != 0) goto L_0x237a;
    L_0x236b:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.phone_number;
        r82 = r0;
        if (r82 != 0) goto L_0x237a;
    L_0x2377:
        r82 = "";
    L_0x237a:
        r20 = new android.text.StaticLayout;
        r4 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
        r0 = r43;
        r6 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r82;
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
        r1 = r188;
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
        r21 = r137;
        r20.<init>(r21, r22, r23, r24, r25, r26, r27);
        r0 = r20;
        r1 = r188;
        r1.docTitleLayout = r0;
        r188.setMessageObjectInternal(r189);
        r0 = r188;
        r4 = r0.drawForwardedName;
        if (r4 == 0) goto L_0x249c;
    L_0x23d3:
        r4 = r189.needDrawForwarded();
        if (r4 == 0) goto L_0x249c;
    L_0x23d9:
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x23e7;
    L_0x23df:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.minY;
        if (r4 != 0) goto L_0x249c;
    L_0x23e7:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.namesOffset = r4;
    L_0x23f6:
        r4 = NUM; // 0x425CLASSNAME float:55.0 double:5.50055916E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r188;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r188;
        r6 = r0.docTitleLayout;
        r6 = r6.getHeight();
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x2423;
    L_0x2414:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.namesOffset = r4;
    L_0x2423:
        r0 = r188;
        r4 = r0.drawInstantView;
        if (r4 == 0) goto L_0x24bb;
    L_0x2429:
        r188.createInstantViewButton();
        goto L_0x1306;
    L_0x242e:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x22a9;
    L_0x2432:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2460;
    L_0x243c:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x2460;
    L_0x2442:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x2460;
    L_0x2448:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x244a:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x22bd;
    L_0x2460:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x244a;
    L_0x2463:
        r6 = org.telegram.ui.ActionBar.Theme.chat_contactDrawable;
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x2470;
    L_0x246b:
        r4 = 1;
    L_0x246c:
        r21 = r6[r4];
        goto L_0x2326;
    L_0x2470:
        r4 = 0;
        goto L_0x246c;
    L_0x2472:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.phone_number;
        r137 = r0;
        r4 = android.text.TextUtils.isEmpty(r137);
        if (r4 != 0) goto L_0x2490;
    L_0x2482:
        r4 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r137 = (java.lang.String) r137;
        r0 = r137;
        r137 = r4.format(r0);
        goto L_0x2349;
    L_0x2490:
        r4 = "NumberUnknown";
        r6 = NUM; // 0x7f0CLASSNAME float:1.861206E38 double:1.053098097E-314;
        r137 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x2349;
    L_0x249c:
        r0 = r188;
        r4 = r0.drawNameLayout;
        if (r4 == 0) goto L_0x23f6;
    L_0x24a2:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.reply_to_msg_id;
        if (r4 != 0) goto L_0x23f6;
    L_0x24aa:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.namesOffset = r4;
        goto L_0x23f6;
    L_0x24bb:
        r0 = r188;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x1306;
    L_0x24c5:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42dCLASSNAME float:110.0 double:5.54200439E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r6 = r0.docTitleLayout;
        r0 = r188;
        r8 = r0.docTitleLayout;
        r8 = r8.getLineCount();
        r8 = r8 + -1;
        r6 = r6.getLineWidth(r8);
        r8 = (double) r6;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r169 = r4 - r6;
        r0 = r188;
        r4 = r0.timeWidth;
        r0 = r169;
        if (r0 >= r4) goto L_0x1306;
    L_0x24f2:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        goto L_0x1306;
    L_0x2503:
        r0 = r189;
        r4 = r0.type;
        r6 = 2;
        if (r4 != r6) goto L_0x25a8;
    L_0x250a:
        r4 = 1;
        r0 = r188;
        r0.drawForwardedName = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2578;
    L_0x2515:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2575;
    L_0x251f:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x2575;
    L_0x2525:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x2575;
    L_0x252b:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x252d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x2541:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r0 = r188;
        r1 = r189;
        r0.createDocumentLayout(r4, r1);
        r188.setMessageObjectInternal(r189);
        r4 = NUM; // 0x428CLASSNAME float:70.0 double:5.51610112E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r188;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x1306;
    L_0x2564:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.namesOffset = r4;
        goto L_0x1306;
    L_0x2575:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x252d;
    L_0x2578:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x25a5;
    L_0x2582:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x25a5;
    L_0x2588:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x25a5;
    L_0x258e:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x2590:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x2541;
    L_0x25a5:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2590;
    L_0x25a8:
        r0 = r189;
        r4 = r0.type;
        r6 = 14;
        if (r4 != r6) goto L_0x2649;
    L_0x25b0:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2619;
    L_0x25b6:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2616;
    L_0x25c0:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x2616;
    L_0x25c6:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x2616;
    L_0x25cc:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x25ce:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x25e2:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r0 = r188;
        r1 = r189;
        r0.createDocumentLayout(r4, r1);
        r188.setMessageObjectInternal(r189);
        r4 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r188;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x1306;
    L_0x2605:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.namesOffset = r4;
        goto L_0x1306;
    L_0x2616:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x25ce;
    L_0x2619:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2646;
    L_0x2623:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x2646;
    L_0x2629:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x2646;
    L_0x262f:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x2631:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x25e2;
    L_0x2646:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2631;
    L_0x2649:
        r0 = r189;
        r4 = r0.type;
        r6 = 17;
        if (r4 != r6) goto L_0x2bbc;
    L_0x2651:
        r188.createSelectorDrawable();
        r4 = 1;
        r0 = r188;
        r0.drawName = r4;
        r4 = 1;
        r0 = r188;
        r0.drawForwardedName = r4;
        r4 = 0;
        r0 = r188;
        r0.drawPhotoImage = r4;
        r4 = NUM; // 0x43fa0000 float:500.0 double:5.634608575E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = r189.getMaxMessageTextWidth();
        r43 = java.lang.Math.min(r4, r6);
        r0 = r43;
        r1 = r188;
        r1.availableTimeWidth = r0;
        r4 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r43;
        r0 = r188;
        r0.backgroundWidth = r4;
        r4 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r188;
        r0.availableTimeWidth = r4;
        r188.measureTime(r189);
        r0 = r189;
        r4 = r0.messageOwner;
        r0 = r4.media;
        r121 = r0;
        r121 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r121;
        r0 = r121;
        r4 = r0.poll;
        r4 = r4.closed;
        r0 = r188;
        r0.pollClosed = r4;
        r4 = r189.isVoted();
        r0 = r188;
        r0.pollVoted = r4;
        r20 = new android.text.StaticLayout;
        r0 = r121;
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
        r1 = r188;
        r1.titleLayout = r0;
        r174 = 0;
        r0 = r188;
        r4 = r0.titleLayout;
        if (r4 == 0) goto L_0x2709;
    L_0x26e8:
        r56 = 0;
        r0 = r188;
        r4 = r0.titleLayout;
        r54 = r4.getLineCount();
    L_0x26f2:
        r0 = r56;
        r1 = r54;
        if (r0 >= r1) goto L_0x2709;
    L_0x26f8:
        r0 = r188;
        r4 = r0.titleLayout;
        r0 = r56;
        r4 = r4.getLineLeft(r0);
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x28c9;
    L_0x2707:
        r174 = 1;
    L_0x2709:
        r20 = new android.text.StaticLayout;
        r0 = r121;
        r4 = r0.poll;
        r4 = r4.closed;
        if (r4 == 0) goto L_0x28cd;
    L_0x2713:
        r4 = "FinalResults";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8610886E38 double:1.053097811E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x271d:
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
        r1 = r188;
        r1.docTitleLayout = r0;
        r0 = r188;
        r4 = r0.docTitleLayout;
        if (r4 == 0) goto L_0x2770;
    L_0x2749:
        r0 = r188;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x2770;
    L_0x2753:
        if (r174 == 0) goto L_0x28d9;
    L_0x2755:
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 != 0) goto L_0x28d9;
    L_0x2759:
        r0 = r43;
        r4 = (float) r0;
        r0 = r188;
        r6 = r0.docTitleLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r4 = r4 - r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r188;
        r0.docTitleOffsetX = r4;
    L_0x2770:
        r0 = r188;
        r4 = r0.timeWidth;
        r6 = r43 - r4;
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x28f5;
    L_0x277c:
        r4 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
    L_0x277e:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r6 - r4;
        r20 = new android.text.StaticLayout;
        r0 = r121;
        r4 = r0.results;
        r4 = r4.total_voters;
        if (r4 != 0) goto L_0x28f9;
    L_0x278e:
        r4 = "NoVotes";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8611792E38 double:1.053098032E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x2798:
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
        r1 = r188;
        r1.infoLayout = r0;
        r0 = r188;
        r4 = r0.infoLayout;
        if (r4 == 0) goto L_0x2908;
    L_0x27bc:
        r0 = r188;
        r4 = r0.infoLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x2908;
    L_0x27c6:
        r0 = r188;
        r4 = r0.infoLayout;
        r6 = 0;
        r4 = r4.getLineLeft(r6);
        r4 = -r4;
        r8 = (double) r4;
    L_0x27d1:
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r188;
        r0.infoX = r4;
        r0 = r121;
        r4 = r0.poll;
        r0 = r188;
        r0.lastPoll = r4;
        r0 = r121;
        r4 = r0.results;
        r4 = r4.results;
        r0 = r188;
        r0.lastPollResults = r4;
        r0 = r121;
        r4 = r0.results;
        r4 = r4.total_voters;
        r0 = r188;
        r0.lastPollResultsVoters = r4;
        r119 = 0;
        r0 = r188;
        r4 = r0.animatePollAnswer;
        if (r4 != 0) goto L_0x280a;
    L_0x27fe:
        r0 = r188;
        r4 = r0.pollVoteInProgress;
        if (r4 == 0) goto L_0x280a;
    L_0x2804:
        r4 = 3;
        r0 = r188;
        r0.performHapticFeedback(r4);
    L_0x280a:
        r0 = r188;
        r4 = r0.attachedToWindow;
        if (r4 == 0) goto L_0x290c;
    L_0x2810:
        r0 = r188;
        r4 = r0.pollVoteInProgress;
        if (r4 != 0) goto L_0x281c;
    L_0x2816:
        r0 = r188;
        r4 = r0.pollUnvoteInProgress;
        if (r4 == 0) goto L_0x290c;
    L_0x281c:
        r4 = 1;
    L_0x281d:
        r0 = r188;
        r0.animatePollAnswer = r4;
        r0 = r188;
        r0.animatePollAnswerAlpha = r4;
        r147 = 0;
        r164 = new java.util.ArrayList;
        r164.<init>();
        r0 = r188;
        r4 = r0.pollButtons;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x2912;
    L_0x2836:
        r147 = new java.util.ArrayList;
        r0 = r188;
        r4 = r0.pollButtons;
        r0 = r147;
        r0.<init>(r4);
        r0 = r188;
        r4 = r0.pollButtons;
        r4.clear();
        r0 = r188;
        r4 = r0.animatePollAnswer;
        if (r4 != 0) goto L_0x2865;
    L_0x284e:
        r0 = r188;
        r4 = r0.attachedToWindow;
        if (r4 == 0) goto L_0x290f;
    L_0x2854:
        r0 = r188;
        r4 = r0.pollVoted;
        if (r4 != 0) goto L_0x2860;
    L_0x285a:
        r0 = r188;
        r4 = r0.pollClosed;
        if (r4 == 0) goto L_0x290f;
    L_0x2860:
        r4 = 1;
    L_0x2861:
        r0 = r188;
        r0.animatePollAnswer = r4;
    L_0x2865:
        r0 = r188;
        r4 = r0.pollAnimationProgress;
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 <= 0) goto L_0x2912;
    L_0x286e:
        r0 = r188;
        r4 = r0.pollAnimationProgress;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x2912;
    L_0x2878:
        r68 = 0;
        r55 = r147.size();
    L_0x287e:
        r0 = r68;
        r1 = r55;
        if (r0 >= r1) goto L_0x2912;
    L_0x2884:
        r0 = r147;
        r1 = r68;
        r73 = r0.get(r1);
        r73 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r73;
        r4 = r73.prevPercent;
        r4 = (float) r4;
        r6 = r73.percent;
        r8 = r73.prevPercent;
        r6 = r6 - r8;
        r6 = (float) r6;
        r0 = r188;
        r8 = r0.pollAnimationProgress;
        r6 = r6 * r8;
        r4 = r4 + r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r73;
        r0.percent = r4;
        r4 = r73.prevPercentProgress;
        r6 = r73.percentProgress;
        r8 = r73.prevPercentProgress;
        r6 = r6 - r8;
        r0 = r188;
        r8 = r0.pollAnimationProgress;
        r6 = r6 * r8;
        r4 = r4 + r6;
        r0 = r73;
        r0.percentProgress = r4;
        r68 = r68 + 1;
        goto L_0x287e;
    L_0x28c9:
        r56 = r56 + 1;
        goto L_0x26f2;
    L_0x28cd:
        r4 = "AnonymousPoll";
        r6 = NUM; // 0x7f0CLASSNAMEf float:1.8609482E38 double:1.053097469E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x271d;
    L_0x28d9:
        if (r174 != 0) goto L_0x2770;
    L_0x28db:
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x2770;
    L_0x28df:
        r0 = r188;
        r4 = r0.docTitleLayout;
        r6 = 0;
        r4 = r4.getLineLeft(r6);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r4 = -r4;
        r0 = r188;
        r0.docTitleOffsetX = r4;
        goto L_0x2770;
    L_0x28f5:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x277e;
    L_0x28f9:
        r4 = "Vote";
        r0 = r121;
        r6 = r0.results;
        r6 = r6.total_voters;
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r6);
        goto L_0x2798;
    L_0x2908:
        r8 = 0;
        goto L_0x27d1;
    L_0x290c:
        r4 = 0;
        goto L_0x281d;
    L_0x290f:
        r4 = 0;
        goto L_0x2861;
    L_0x2912:
        r0 = r188;
        r4 = r0.animatePollAnswer;
        if (r4 == 0) goto L_0x2add;
    L_0x2918:
        r4 = 0;
    L_0x2919:
        r0 = r188;
        r0.pollAnimationProgress = r4;
        r0 = r188;
        r4 = r0.animatePollAnswerAlpha;
        if (r4 != 0) goto L_0x2ae1;
    L_0x2923:
        r4 = 0;
        r0 = r188;
        r0.pollVoteInProgress = r4;
        r4 = -1;
        r0 = r188;
        r0.pollVoteInProgressNum = r4;
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.SendMessagesHelper.getInstance(r4);
        r0 = r188;
        r6 = r0.currentMessageObject;
        r179 = r4.isSendingVote(r6);
    L_0x293d:
        r0 = r188;
        r4 = r0.titleLayout;
        if (r4 == 0) goto L_0x2ae5;
    L_0x2943:
        r0 = r188;
        r4 = r0.titleLayout;
        r101 = r4.getHeight();
    L_0x294b:
        r153 = 100;
        r99 = 0;
        r146 = 0;
        r56 = 0;
        r0 = r121;
        r4 = r0.poll;
        r4 = r4.answers;
        r54 = r4.size();
    L_0x295d:
        r0 = r56;
        r1 = r54;
        if (r0 >= r1) goto L_0x2b11;
    L_0x2963:
        r73 = new org.telegram.ui.Cells.ChatMessageCell$PollButton;
        r4 = 0;
        r0 = r73;
        r1 = r188;
        r0.<init>(r1, r4);
        r0 = r121;
        r4 = r0.poll;
        r4 = r4.answers;
        r0 = r56;
        r4 = r4.get(r0);
        r4 = (org.telegram.tgnet.TLRPC.TL_pollAnswer) r4;
        r0 = r73;
        r0.answer = r4;
        r24 = new android.text.StaticLayout;
        r4 = r73.answer;
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
        r0 = r73;
        r1 = r24;
        r0.title = r1;
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r101;
        r0 = r73;
        r0.y = r4;
        r4 = r73.title;
        r4 = r4.getHeight();
        r0 = r73;
        r0.height = r4;
        r0 = r188;
        r4 = r0.pollButtons;
        r0 = r73;
        r4.add(r0);
        r0 = r164;
        r1 = r73;
        r0.add(r1);
        r4 = r73.height;
        r6 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r101 = r101 + r4;
        r0 = r121;
        r4 = r0.results;
        r4 = r4.results;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x2a80;
    L_0x29f8:
        r68 = 0;
        r0 = r121;
        r4 = r0.results;
        r4 = r4.results;
        r55 = r4.size();
    L_0x2a04:
        r0 = r68;
        r1 = r55;
        if (r0 >= r1) goto L_0x2a80;
    L_0x2a0a:
        r0 = r121;
        r4 = r0.results;
        r4 = r4.results;
        r0 = r68;
        r62 = r4.get(r0);
        r62 = (org.telegram.tgnet.TLRPC.TL_pollAnswerVoters) r62;
        r4 = r73.answer;
        r4 = r4.option;
        r0 = r62;
        r6 = r0.option;
        r4 = java.util.Arrays.equals(r4, r6);
        if (r4 == 0) goto L_0x2b09;
    L_0x2a28:
        r0 = r188;
        r4 = r0.pollVoted;
        if (r4 != 0) goto L_0x2a34;
    L_0x2a2e:
        r0 = r188;
        r4 = r0.pollClosed;
        if (r4 == 0) goto L_0x2ae9;
    L_0x2a34:
        r0 = r121;
        r4 = r0.results;
        r4 = r4.total_voters;
        if (r4 <= 0) goto L_0x2ae9;
    L_0x2a3c:
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r0 = r62;
        r6 = r0.voters;
        r6 = (float) r6;
        r0 = r121;
        r8 = r0.results;
        r8 = r8.total_voters;
        r8 = (float) r8;
        r6 = r6 / r8;
        r4 = r4 * r6;
        r0 = r73;
        r0.decimal = r4;
        r4 = r73.decimal;
        r4 = (int) r4;
        r0 = r73;
        r0.percent = r4;
        r4 = r73.decimal;
        r6 = r73.percent;
        r6 = (float) r6;
        r4 = r4 - r6;
        r0 = r73;
        r0.decimal = r4;
    L_0x2a6a:
        if (r146 != 0) goto L_0x2af7;
    L_0x2a6c:
        r146 = r73.percent;
    L_0x2a70:
        r4 = r73.percent;
        r153 = r153 - r4;
        r4 = r73.percent;
        r0 = r119;
        r119 = java.lang.Math.max(r4, r0);
    L_0x2a80:
        if (r147 == 0) goto L_0x2abc;
    L_0x2a82:
        r68 = 0;
        r55 = r147.size();
    L_0x2a88:
        r0 = r68;
        r1 = r55;
        if (r0 >= r1) goto L_0x2abc;
    L_0x2a8e:
        r0 = r147;
        r1 = r68;
        r145 = r0.get(r1);
        r145 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r145;
        r4 = r73.answer;
        r4 = r4.option;
        r6 = r145.answer;
        r6 = r6.option;
        r4 = java.util.Arrays.equals(r4, r6);
        if (r4 == 0) goto L_0x2b0d;
    L_0x2aaa:
        r4 = r145.percent;
        r0 = r73;
        r0.prevPercent = r4;
        r4 = r145.percentProgress;
        r0 = r73;
        r0.prevPercentProgress = r4;
    L_0x2abc:
        if (r179 == 0) goto L_0x2ad9;
    L_0x2abe:
        r4 = r73.answer;
        r4 = r4.option;
        r0 = r179;
        r4 = java.util.Arrays.equals(r4, r0);
        if (r4 == 0) goto L_0x2ad9;
    L_0x2acc:
        r0 = r56;
        r1 = r188;
        r1.pollVoteInProgressNum = r0;
        r4 = 1;
        r0 = r188;
        r0.pollVoteInProgress = r4;
        r179 = 0;
    L_0x2ad9:
        r56 = r56 + 1;
        goto L_0x295d;
    L_0x2add:
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x2919;
    L_0x2ae1:
        r179 = 0;
        goto L_0x293d;
    L_0x2ae5:
        r101 = 0;
        goto L_0x294b;
    L_0x2ae9:
        r4 = 0;
        r0 = r73;
        r0.percent = r4;
        r4 = 0;
        r0 = r73;
        r0.decimal = r4;
        goto L_0x2a6a;
    L_0x2af7:
        r4 = r73.percent;
        if (r4 == 0) goto L_0x2a70;
    L_0x2afd:
        r4 = r73.percent;
        r0 = r146;
        if (r0 == r4) goto L_0x2a70;
    L_0x2b05:
        r99 = 1;
        goto L_0x2a70;
    L_0x2b09:
        r68 = r68 + 1;
        goto L_0x2a04;
    L_0x2b0d:
        r68 = r68 + 1;
        goto L_0x2a88;
    L_0x2b11:
        if (r99 == 0) goto L_0x2b44;
    L_0x2b13:
        if (r153 == 0) goto L_0x2b44;
    L_0x2b15:
        r4 = org.telegram.ui.Cells.ChatMessageCell$$Lambda$0.$instance;
        r0 = r164;
        java.util.Collections.sort(r0, r4);
        r56 = 0;
        r4 = r164.size();
        r0 = r153;
        r54 = java.lang.Math.min(r0, r4);
    L_0x2b28:
        r0 = r56;
        r1 = r54;
        if (r0 >= r1) goto L_0x2b44;
    L_0x2b2e:
        r0 = r164;
        r1 = r56;
        r4 = r0.get(r1);
        r4 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r4;
        r6 = r4.percent;
        r6 = r6 + 1;
        r4.percent = r6;
        r56 = r56 + 1;
        goto L_0x2b28;
    L_0x2b44:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r183 = r4 - r6;
        r68 = 0;
        r0 = r188;
        r4 = r0.pollButtons;
        r55 = r4.size();
    L_0x2b5a:
        r0 = r68;
        r1 = r55;
        if (r0 >= r1) goto L_0x2b91;
    L_0x2b60:
        r0 = r188;
        r4 = r0.pollButtons;
        r0 = r68;
        r73 = r4.get(r0);
        r73 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r73;
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r0 = r183;
        r6 = (float) r0;
        r6 = r4 / r6;
        if (r119 == 0) goto L_0x2b8f;
    L_0x2b7a:
        r4 = r73.percent;
        r4 = (float) r4;
        r0 = r119;
        r8 = (float) r0;
        r4 = r4 / r8;
    L_0x2b83:
        r4 = java.lang.Math.max(r6, r4);
        r0 = r73;
        r0.percentProgress = r4;
        r68 = r68 + 1;
        goto L_0x2b5a;
    L_0x2b8f:
        r4 = 0;
        goto L_0x2b83;
    L_0x2b91:
        r188.setMessageObjectInternal(r189);
        r4 = NUM; // 0x42920000 float:73.0 double:5.518043864E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r188;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r4 = r4 + r101;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x1306;
    L_0x2bab:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.namesOffset = r4;
        goto L_0x1306;
    L_0x2bbc:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.fwd_from;
        if (r4 == 0) goto L_0x2d45;
    L_0x2bc4:
        r0 = r189;
        r4 = r0.type;
        r6 = 13;
        if (r4 == r6) goto L_0x2d45;
    L_0x2bcc:
        r4 = 1;
    L_0x2bcd:
        r0 = r188;
        r0.drawForwardedName = r4;
        r0 = r189;
        r4 = r0.type;
        r6 = 9;
        if (r4 == r6) goto L_0x2d48;
    L_0x2bd9:
        r4 = 1;
    L_0x2bda:
        r0 = r188;
        r0.mediaBackground = r4;
        r4 = 1;
        r0 = r188;
        r0.drawImageButton = r4;
        r4 = 1;
        r0 = r188;
        r0.drawPhotoImage = r4;
        r141 = 0;
        r140 = 0;
        r58 = 0;
        r0 = r189;
        r4 = r0.gifState;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x2CLASSNAME;
    L_0x2bf8:
        r4 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r4 != 0) goto L_0x2CLASSNAME;
    L_0x2bfc:
        r0 = r189;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x2c0b;
    L_0x2CLASSNAME:
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x2CLASSNAME;
    L_0x2c0b:
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = r189;
        r0.gifState = r4;
    L_0x2CLASSNAME:
        r4 = r189.isRoundVideo();
        if (r4 == 0) goto L_0x2d4e;
    L_0x2CLASSNAME:
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setAllowDecodeSingleFrame(r6);
        r0 = r188;
        r6 = r0.photoImage;
        r4 = org.telegram.messenger.MediaController.getInstance();
        r4 = r4.getPlayingMessageObject();
        if (r4 != 0) goto L_0x2d4b;
    L_0x2c2d:
        r4 = 1;
    L_0x2c2e:
        r6.setAllowStartAnimation(r4);
    L_0x2CLASSNAME:
        r0 = r188;
        r4 = r0.photoImage;
        r6 = r189.needDrawBluredPreview();
        r4.setForcePreview(r6);
        r0 = r189;
        r4 = r0.type;
        r6 = 9;
        if (r4 != r6) goto L_0x2fc0;
    L_0x2CLASSNAME:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2d67;
    L_0x2c4a:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2d63;
    L_0x2CLASSNAME:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x2d63;
    L_0x2c5a:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x2d63;
    L_0x2CLASSNAME:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x2CLASSNAME:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x2CLASSNAME:
        r4 = r188.checkNeedDrawShareButton(r189);
        if (r4 == 0) goto L_0x2c8b;
    L_0x2c7c:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x2c8b:
        r118 = 0;
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x430a0000 float:138.0 double:5.55689877E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r0 = r188;
        r1 = r43;
        r2 = r189;
        r0.createDocumentLayout(r1, r2);
        r0 = r189;
        r4 = r0.caption;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x2cfb;
    L_0x2cac:
        r0 = r189;
        r4 = r0.caption;	 Catch:{ Exception -> 0x2db5 }
        r0 = r188;
        r0.currentCaption = r4;	 Catch:{ Exception -> 0x2db5 }
        r0 = r188;
        r4 = r0.backgroundWidth;	 Catch:{ Exception -> 0x2db5 }
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x2db5 }
        r183 = r4 - r6;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x2db5 }
        r27 = r183 - r4;
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x2db5 }
        r6 = 24;
        if (r4 < r6) goto L_0x2d98;
    L_0x2cce:
        r0 = r189;
        r4 = r0.caption;	 Catch:{ Exception -> 0x2db5 }
        r6 = 0;
        r0 = r189;
        r8 = r0.caption;	 Catch:{ Exception -> 0x2db5 }
        r8 = r8.length();	 Catch:{ Exception -> 0x2db5 }
        r9 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x2db5 }
        r0 = r27;
        r4 = android.text.StaticLayout.Builder.obtain(r4, r6, r8, r9, r0);	 Catch:{ Exception -> 0x2db5 }
        r6 = 1;
        r4 = r4.setBreakStrategy(r6);	 Catch:{ Exception -> 0x2db5 }
        r6 = 0;
        r4 = r4.setHyphenationFrequency(r6);	 Catch:{ Exception -> 0x2db5 }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x2db5 }
        r4 = r4.setAlignment(r6);	 Catch:{ Exception -> 0x2db5 }
        r4 = r4.build();	 Catch:{ Exception -> 0x2db5 }
        r0 = r188;
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x2db5 }
    L_0x2cfb:
        r0 = r188;
        r4 = r0.docTitleLayout;
        if (r4 == 0) goto L_0x2dbf;
    L_0x2d01:
        r56 = 0;
        r0 = r188;
        r4 = r0.docTitleLayout;
        r54 = r4.getLineCount();
    L_0x2d0b:
        r0 = r56;
        r1 = r54;
        if (r0 >= r1) goto L_0x2dbf;
    L_0x2d11:
        r0 = r188;
        r4 = r0.docTitleLayout;
        r0 = r56;
        r4 = r4.getLineWidth(r0);
        r0 = r188;
        r6 = r0.docTitleLayout;
        r0 = r56;
        r6 = r6.getLineLeft(r0);
        r4 = r4 + r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r0 = r188;
        r4 = r0.drawPhotoImage;
        if (r4 == 0) goto L_0x2dbb;
    L_0x2d32:
        r4 = 52;
    L_0x2d34:
        r4 = r4 + 86;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r6;
        r0 = r118;
        r118 = java.lang.Math.max(r0, r4);
        r56 = r56 + 1;
        goto L_0x2d0b;
    L_0x2d45:
        r4 = 0;
        goto L_0x2bcd;
    L_0x2d48:
        r4 = 0;
        goto L_0x2bda;
    L_0x2d4b:
        r4 = 0;
        goto L_0x2c2e;
    L_0x2d4e:
        r0 = r188;
        r6 = r0.photoImage;
        r0 = r189;
        r4 = r0.gifState;
        r8 = 0;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x2d61;
    L_0x2d5b:
        r4 = 1;
    L_0x2d5c:
        r6.setAllowStartAnimation(r4);
        goto L_0x2CLASSNAME;
    L_0x2d61:
        r4 = 0;
        goto L_0x2d5c;
    L_0x2d63:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2CLASSNAME;
    L_0x2d67:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2d95;
    L_0x2d71:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x2d95;
    L_0x2d77:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x2d95;
    L_0x2d7d:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x2d7f:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x2CLASSNAME;
    L_0x2d95:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2d7f;
    L_0x2d98:
        r24 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x2db5 }
        r0 = r189;
        r0 = r0.caption;	 Catch:{ Exception -> 0x2db5 }
        r25 = r0;
        r26 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x2db5 }
        r28 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x2db5 }
        r29 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r30 = 0;
        r31 = 0;
        r24.<init>(r25, r26, r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x2db5 }
        r0 = r24;
        r1 = r188;
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x2db5 }
        goto L_0x2cfb;
    L_0x2db5:
        r90 = move-exception;
        org.telegram.messenger.FileLog.e(r90);
        goto L_0x2cfb;
    L_0x2dbb:
        r4 = 22;
        goto L_0x2d34;
    L_0x2dbf:
        r0 = r188;
        r4 = r0.infoLayout;
        if (r4 == 0) goto L_0x2e01;
    L_0x2dc5:
        r56 = 0;
        r0 = r188;
        r4 = r0.infoLayout;
        r54 = r4.getLineCount();
    L_0x2dcf:
        r0 = r56;
        r1 = r54;
        if (r0 >= r1) goto L_0x2e01;
    L_0x2dd5:
        r0 = r188;
        r4 = r0.infoLayout;
        r0 = r56;
        r4 = r4.getLineWidth(r0);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r0 = r188;
        r4 = r0.drawPhotoImage;
        if (r4 == 0) goto L_0x2dfe;
    L_0x2deb:
        r4 = 52;
    L_0x2ded:
        r4 = r4 + 86;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r6;
        r0 = r118;
        r118 = java.lang.Math.max(r0, r4);
        r56 = r56 + 1;
        goto L_0x2dcf;
    L_0x2dfe:
        r4 = 22;
        goto L_0x2ded;
    L_0x2e01:
        r0 = r188;
        r4 = r0.captionLayout;
        if (r4 == 0) goto L_0x2e45;
    L_0x2e07:
        r56 = 0;
        r0 = r188;
        r4 = r0.captionLayout;
        r54 = r4.getLineCount();
    L_0x2e11:
        r0 = r56;
        r1 = r54;
        if (r0 >= r1) goto L_0x2e45;
    L_0x2e17:
        r0 = r188;
        r4 = r0.captionLayout;
        r0 = r56;
        r4 = r4.getLineWidth(r0);
        r0 = r188;
        r6 = r0.captionLayout;
        r0 = r56;
        r6 = r6.getLineLeft(r0);
        r4 = r4 + r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r23 = r4 + r6;
        r0 = r23;
        r1 = r118;
        if (r0 <= r1) goto L_0x2e42;
    L_0x2e40:
        r118 = r23;
    L_0x2e42:
        r56 = r56 + 1;
        goto L_0x2e11;
    L_0x2e45:
        if (r118 <= 0) goto L_0x2e55;
    L_0x2e47:
        r0 = r118;
        r1 = r188;
        r1.backgroundWidth = r0;
        r4 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r43 = r118 - r4;
    L_0x2e55:
        r0 = r188;
        r4 = r0.drawPhotoImage;
        if (r4 == 0) goto L_0x2f8e;
    L_0x2e5b:
        r4 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r141 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r140 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x2e67:
        r0 = r43;
        r1 = r188;
        r1.availableTimeWidth = r0;
        r0 = r188;
        r4 = r0.drawPhotoImage;
        if (r4 != 0) goto L_0x2eb6;
    L_0x2e73:
        r0 = r189;
        r4 = r0.caption;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x2eb6;
    L_0x2e7d:
        r0 = r188;
        r4 = r0.infoLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x2eb6;
    L_0x2e87:
        r188.measureTime(r189);
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42var_ float:122.0 double:5.54977537E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r6 = r0.infoLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r8 = (double) r6;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r169 = r4 - r6;
        r0 = r188;
        r4 = r0.timeWidth;
        r0 = r169;
        if (r0 >= r4) goto L_0x2eb6;
    L_0x2eae:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r140 = r140 + r4;
    L_0x2eb6:
        r188.setMessageObjectInternal(r189);
        r0 = r188;
        r4 = r0.drawForwardedName;
        if (r4 == 0) goto L_0x44bb;
    L_0x2ebf:
        r4 = r189.needDrawForwarded();
        if (r4 == 0) goto L_0x44bb;
    L_0x2ec5:
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x2ed3;
    L_0x2ecb:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.minY;
        if (r4 != 0) goto L_0x44bb;
    L_0x2ed3:
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 == r6) goto L_0x2ee9;
    L_0x2eda:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.namesOffset = r4;
    L_0x2ee9:
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r140;
        r0 = r188;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r4 = r4 + r58;
        r0 = r188;
        r0.totalHeight = r4;
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x2f1b;
    L_0x2var_:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 != 0) goto L_0x2f1b;
    L_0x2f0c:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.totalHeight = r4;
    L_0x2f1b:
        r60 = 0;
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x2f5b;
    L_0x2var_:
        r0 = r188;
        r4 = r0.currentPosition;
        r0 = r188;
        r4 = r0.getAdditionalWidthForPosition(r4);
        r141 = r141 + r4;
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 != 0) goto L_0x2var_;
    L_0x2var_:
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r140 = r140 + r4;
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r60 = r60 - r4;
    L_0x2var_:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 != 0) goto L_0x2f5b;
    L_0x2var_:
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r140 = r140 + r4;
    L_0x2f5b:
        r0 = r188;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x2var_;
    L_0x2var_:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.namesOffset = r4;
    L_0x2var_:
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 0;
        r8 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r188;
        r9 = r0.namesOffset;
        r8 = r8 + r9;
        r8 = r8 + r60;
        r0 = r141;
        r1 = r140;
        r4.setImageCoords(r6, r8, r0, r1);
        r188.invalidate();
        goto L_0x1306;
    L_0x2f8e:
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r141 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r140 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r188;
        r4 = r0.docTitleLayout;
        if (r4 == 0) goto L_0x2e67;
    L_0x2fa0:
        r0 = r188;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        r6 = 1;
        if (r4 <= r6) goto L_0x2e67;
    L_0x2fab:
        r0 = r188;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        r4 = r4 + -1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 * r6;
        r140 = r140 + r4;
        goto L_0x2e67;
    L_0x2fc0:
        r0 = r189;
        r4 = r0.type;
        r6 = 4;
        if (r4 != r6) goto L_0x35f4;
    L_0x2fc7:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.geo;
        r142 = r0;
        r0 = r142;
        r0 = r0.lat;
        r30 = r0;
        r0 = r142;
        r0 = r0._long;
        r32 = r0;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r4 == 0) goto L_0x32af;
    L_0x2fe7:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x3238;
    L_0x2fed:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x3234;
    L_0x2ff7:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x3234;
    L_0x2ffd:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x3234;
    L_0x3003:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x3005:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x301a:
        r4 = r188.checkNeedDrawShareButton(r189);
        if (r4 == 0) goto L_0x302f;
    L_0x3020:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x302f:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42140000 float:37.0 double:5.477246216E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r0 = r43;
        r1 = r188;
        r1.availableTimeWidth = r0;
        r4 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r43 = r43 - r4;
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r141 = r4 - r6;
        r4 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
        r140 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r133 = NUM; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r0 = r133;
        r8 = (double) r0;
        r20 = NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.NUM;
        r150 = r8 / r20;
        r0 = r133;
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
        r20 = r20 * r150;
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
        r186 = r0;
        r8 = NUM; // 0x3fvar_fb54442d18 float:3.37028055E12 double:1.NUM;
        r20 = NUM; // 0xNUM float:0.0 double:2.0;
        r0 = r133;
        r0 = (double) r0;
        r24 = r0;
        r24 = r186 - r24;
        r24 = r24 / r150;
        r24 = java.lang.Math.exp(r24);
        r24 = java.lang.Math.atan(r24);
        r20 = r20 * r24;
        r8 = r8 - r20;
        r20 = NUM; // 0xNUM float:0.0 double:180.0;
        r8 = r8 * r20;
        r20 = NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.NUM;
        r30 = r8 / r20;
        r0 = r188;
        r0 = r0.currentAccount;
        r29 = r0;
        r0 = r141;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r34 = r0;
        r0 = r140;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r35 = r0;
        r36 = 0;
        r37 = 15;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r29, r30, r32, r34, r35, r36, r37);
        r0 = r188;
        r0.currentUrl = r4;
        r0 = r142;
        r0 = r0.access_hash;
        r34 = r0;
        r0 = r141;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r36 = r0;
        r0 = r140;
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
        r0 = r188;
        r0.currentWebFile = r4;
        r4 = r188.isCurrentLocationTimeExpired(r189);
        r0 = r188;
        r0.locationExpired = r4;
        if (r4 != 0) goto L_0x326a;
    L_0x3140:
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setCrossfadeWithOldImage(r6);
        r4 = 0;
        r0 = r188;
        r0.mediaBackground = r4;
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r58 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r188;
        r4 = r0.invalidateRunnable;
        r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4, r8);
        r4 = 1;
        r0 = r188;
        r0.scheduledInvalidate = r4;
    L_0x3161:
        r34 = new android.text.StaticLayout;
        r4 = "AttachLiveLocation";
        r6 = NUM; // 0x7f0CLASSNAMEd0 float:1.8609614E38 double:1.053097501E-314;
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
        r1 = r188;
        r1.docTitleLayout = r0;
        r19 = 0;
        r188.updateCurrentUserAndChat();
        r39 = 0;
        r0 = r188;
        r4 = r0.currentUser;
        if (r4 == 0) goto L_0x327b;
    L_0x319a:
        r0 = r188;
        r4 = r0.currentUser;
        r4 = r4.photo;
        if (r4 == 0) goto L_0x31ac;
    L_0x31a2:
        r0 = r188;
        r4 = r0.currentUser;
        r4 = r4.photo;
        r0 = r4.photo_small;
        r19 = r0;
    L_0x31ac:
        r0 = r188;
        r4 = r0.contactAvatarDrawable;
        r0 = r188;
        r6 = r0.currentUser;
        r4.setInfo(r6);
        r0 = r188;
        r0 = r0.currentUser;
        r39 = r0;
    L_0x31bd:
        r0 = r188;
        r0 = r0.locationImageReceiver;
        r34 = r0;
        r36 = "50_50";
        r0 = r188;
        r0 = r0.contactAvatarDrawable;
        r37 = r0;
        r38 = 0;
        r40 = 0;
        r35 = r19;
        r34.setImage(r35, r36, r37, r38, r39, r40);
        r40 = new android.text.StaticLayout;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.edit_date;
        if (r4 == 0) goto L_0x32a6;
    L_0x31df:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.edit_date;
        r8 = (long) r4;
    L_0x31e6:
        r41 = org.telegram.messenger.LocaleController.formatLocationUpdateDate(r8);
        r42 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
        r44 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r45 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r46 = 0;
        r47 = 0;
        r40.<init>(r41, r42, r43, r44, r45, r46, r47);
        r0 = r40;
        r1 = r188;
        r1.infoLayout = r0;
    L_0x31fd:
        r8 = r189.getDialogId();
        r4 = (int) r8;
        if (r4 != 0) goto L_0x3562;
    L_0x3204:
        r4 = org.telegram.messenger.SharedConfig.mapPreviewType;
        if (r4 != 0) goto L_0x354f;
    L_0x3208:
        r4 = 2;
        r0 = r188;
        r0.currentMapProvider = r4;
    L_0x320d:
        r0 = r188;
        r4 = r0.currentMapProvider;
        r6 = -1;
        if (r4 != r6) goto L_0x3575;
    L_0x3214:
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r45 = 0;
        r46 = 0;
        r6 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x3572;
    L_0x3226:
        r4 = 1;
    L_0x3227:
        r47 = r6[r4];
        r48 = 0;
        r50 = 0;
        r49 = r189;
        r44.setImage(r45, r46, r47, r48, r49, r50);
        goto L_0x2eb6;
    L_0x3234:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x3005;
    L_0x3238:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x3267;
    L_0x3242:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x3267;
    L_0x3248:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x3267;
    L_0x324e:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x3250:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x301a;
    L_0x3267:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x3250;
    L_0x326a:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x3161;
    L_0x327b:
        r0 = r188;
        r4 = r0.currentChat;
        if (r4 == 0) goto L_0x31bd;
    L_0x3281:
        r0 = r188;
        r4 = r0.currentChat;
        r4 = r4.photo;
        if (r4 == 0) goto L_0x3293;
    L_0x3289:
        r0 = r188;
        r4 = r0.currentChat;
        r4 = r4.photo;
        r0 = r4.photo_small;
        r19 = r0;
    L_0x3293:
        r0 = r188;
        r4 = r0.contactAvatarDrawable;
        r0 = r188;
        r6 = r0.currentChat;
        r4.setInfo(r6);
        r0 = r188;
        r0 = r0.currentChat;
        r39 = r0;
        goto L_0x31bd;
    L_0x32a6:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.date;
        r8 = (long) r4;
        goto L_0x31e6;
    L_0x32af:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.title;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x345f;
    L_0x32bd:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x3424;
    L_0x32c3:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x3420;
    L_0x32cd:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x3420;
    L_0x32d3:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x3420;
    L_0x32d9:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x32db:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x32f0:
        r4 = r188.checkNeedDrawShareButton(r189);
        if (r4 == 0) goto L_0x3305;
    L_0x32f6:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x3305:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r43 = r4 - r6;
        r0 = r43;
        r1 = r188;
        r1.availableTimeWidth = r0;
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r141 = r4 - r6;
        r4 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
        r140 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = 0;
        r0 = r188;
        r0.mediaBackground = r4;
        r0 = r188;
        r0 = r0.currentAccount;
        r29 = r0;
        r0 = r141;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r34 = r0;
        r0 = r140;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r35 = r0;
        r36 = 1;
        r37 = 15;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r29, r30, r32, r34, r35, r36, r37);
        r0 = r188;
        r0.currentUrl = r4;
        r0 = r141;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r4 = (int) r4;
        r0 = r140;
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
        r0 = r142;
        r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r4, r6, r8, r9);
        r0 = r188;
        r0.currentWebFile = r4;
        r0 = r189;
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
        r0 = r188;
        r0.docTitleLayout = r4;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r58 = r58 + r4;
        r0 = r188;
        r4 = r0.docTitleLayout;
        r108 = r4.getLineCount();
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.address;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x3458;
    L_0x33bd:
        r0 = r189;
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
        r0 = r188;
        r0.infoLayout = r4;
        r188.measureTime(r189);
        r0 = r188;
        r4 = r0.backgroundWidth;
        r0 = r188;
        r6 = r0.infoLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r8 = (double) r6;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r4 = r4 - r6;
        r6 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r169 = r4 - r6;
        r0 = r188;
        r6 = r0.timeWidth;
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x3456;
    L_0x3408:
        r4 = 20;
    L_0x340a:
        r4 = r4 + 20;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r6;
        r0 = r169;
        if (r0 >= r4) goto L_0x31fd;
    L_0x3416:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r58 = r58 + r4;
        goto L_0x31fd;
    L_0x3420:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x32db;
    L_0x3424:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x3453;
    L_0x342e:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x3453;
    L_0x3434:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x3453;
    L_0x343a:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x343c:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x32f0;
    L_0x3453:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x343c;
    L_0x3456:
        r4 = 0;
        goto L_0x340a;
    L_0x3458:
        r4 = 0;
        r0 = r188;
        r0.infoLayout = r4;
        goto L_0x31fd;
    L_0x345f:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x351d;
    L_0x3465:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x3519;
    L_0x346f:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x3519;
    L_0x3475:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x3519;
    L_0x347b:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x347d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x3492:
        r4 = r188.checkNeedDrawShareButton(r189);
        if (r4 == 0) goto L_0x34a7;
    L_0x3498:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x34a7:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.availableTimeWidth = r4;
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r141 = r4 - r6;
        r4 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
        r140 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r188;
        r0 = r0.currentAccount;
        r29 = r0;
        r0 = r141;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r34 = r0;
        r0 = r140;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r35 = r0;
        r36 = 1;
        r37 = 15;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r29, r30, r32, r34, r35, r36, r37);
        r0 = r188;
        r0.currentUrl = r4;
        r0 = r141;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r4 = (int) r4;
        r0 = r140;
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
        r0 = r142;
        r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r4, r6, r8, r9);
        r0 = r188;
        r0.currentWebFile = r4;
        goto L_0x31fd;
    L_0x3519:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x347d;
    L_0x351d:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x354c;
    L_0x3527:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x354c;
    L_0x352d:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x354c;
    L_0x3533:
        r4 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
    L_0x3535:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x3492;
    L_0x354c:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x3535;
    L_0x354f:
        r4 = org.telegram.messenger.SharedConfig.mapPreviewType;
        r6 = 1;
        if (r4 != r6) goto L_0x355b;
    L_0x3554:
        r4 = 1;
        r0 = r188;
        r0.currentMapProvider = r4;
        goto L_0x320d;
    L_0x355b:
        r4 = -1;
        r0 = r188;
        r0.currentMapProvider = r4;
        goto L_0x320d;
    L_0x3562:
        r0 = r189;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = r4.mapProvider;
        r0 = r188;
        r0.currentMapProvider = r4;
        goto L_0x320d;
    L_0x3572:
        r4 = 0;
        goto L_0x3227;
    L_0x3575:
        r0 = r188;
        r4 = r0.currentMapProvider;
        r6 = 2;
        if (r4 != r6) goto L_0x35a8;
    L_0x357c:
        r0 = r188;
        r4 = r0.currentWebFile;
        if (r4 == 0) goto L_0x2eb6;
    L_0x3582:
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r188;
        r0 = r0.currentWebFile;
        r45 = r0;
        r46 = 0;
        r6 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x35a6;
    L_0x3598:
        r4 = 1;
    L_0x3599:
        r47 = r6[r4];
        r48 = 0;
        r50 = 0;
        r49 = r189;
        r44.setImage(r45, r46, r47, r48, r49, r50);
        goto L_0x2eb6;
    L_0x35a6:
        r4 = 0;
        goto L_0x3599;
    L_0x35a8:
        r0 = r188;
        r4 = r0.currentMapProvider;
        r6 = 3;
        if (r4 == r6) goto L_0x35b6;
    L_0x35af:
        r0 = r188;
        r4 = r0.currentMapProvider;
        r6 = 4;
        if (r4 != r6) goto L_0x35ca;
    L_0x35b6:
        r4 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r188;
        r6 = r0.currentUrl;
        r0 = r188;
        r8 = r0.currentWebFile;
        r4.addTestWebFile(r6, r8);
        r4 = 1;
        r0 = r188;
        r0.addedForTest = r4;
    L_0x35ca:
        r0 = r188;
        r4 = r0.currentUrl;
        if (r4 == 0) goto L_0x2eb6;
    L_0x35d0:
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r188;
        r0 = r0.currentUrl;
        r45 = r0;
        r46 = 0;
        r6 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x35f2;
    L_0x35e6:
        r4 = 1;
    L_0x35e7:
        r47 = r6[r4];
        r48 = 0;
        r49 = 0;
        r44.setImage(r45, r46, r47, r48, r49);
        goto L_0x2eb6;
    L_0x35f2:
        r4 = 0;
        goto L_0x35e7;
    L_0x35f4:
        r0 = r189;
        r4 = r0.type;
        r6 = 13;
        if (r4 != r6) goto L_0x3775;
    L_0x35fc:
        r4 = 0;
        r0 = r188;
        r0.drawBackground = r4;
        r56 = 0;
    L_0x3603:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r4 = r4.size();
        r0 = r56;
        if (r0 >= r4) goto L_0x3639;
    L_0x3615:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r0 = r56;
        r64 = r4.get(r0);
        r64 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r64;
        r0 = r64;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 == 0) goto L_0x36ff;
    L_0x362d:
        r0 = r64;
        r0 = r0.w;
        r141 = r0;
        r0 = r64;
        r0 = r0.h;
        r140 = r0;
    L_0x3639:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x3703;
    L_0x363f:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
        r43 = r4 * r6;
        r116 = r43;
    L_0x364b:
        if (r141 != 0) goto L_0x365a;
    L_0x364d:
        r0 = r116;
        r0 = (int) r0;
        r140 = r0;
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r141 = r140 + r4;
    L_0x365a:
        r0 = r140;
        r4 = (float) r0;
        r0 = r141;
        r6 = (float) r0;
        r6 = r43 / r6;
        r4 = r4 * r6;
        r0 = (int) r4;
        r140 = r0;
        r0 = r43;
        r0 = (int) r0;
        r141 = r0;
        r0 = r140;
        r4 = (float) r0;
        r4 = (r4 > r116 ? 1 : (r4 == r116 ? 0 : -1));
        if (r4 <= 0) goto L_0x3683;
    L_0x3672:
        r0 = r141;
        r4 = (float) r0;
        r0 = r140;
        r6 = (float) r0;
        r6 = r116 / r6;
        r4 = r4 * r6;
        r0 = (int) r4;
        r141 = r0;
        r0 = r116;
        r0 = (int) r0;
        r140 = r0;
    L_0x3683:
        r4 = 6;
        r0 = r188;
        r0.documentAttachType = r4;
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r141 - r4;
        r0 = r188;
        r0.availableTimeWidth = r4;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r141;
        r0 = r188;
        r0.backgroundWidth = r4;
        r0 = r189;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObjectThumb = r4;
        r0 = r189;
        r4 = r0.attachPathExists;
        if (r4 == 0) goto L_0x3718;
    L_0x36b4:
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r189;
        r4 = r0.messageOwner;
        r0 = r4.attachPath;
        r45 = r0;
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r141);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r140);
        r8[r9] = r10;
        r46 = java.lang.String.format(r4, r6, r8);
        r47 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r48 = r0;
        r49 = "b1";
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r0 = r4.size;
        r50 = r0;
        r51 = "webp";
        r53 = 1;
        r52 = r189;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52, r53);
        goto L_0x2eb6;
    L_0x36ff:
        r56 = r56 + 1;
        goto L_0x3603;
    L_0x3703:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r43 = r4 * r6;
        r116 = r43;
        goto L_0x364b;
    L_0x3718:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r8 = r4.id;
        r20 = 0;
        r4 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1));
        if (r4 == 0) goto L_0x2eb6;
    L_0x3728:
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.document;
        r45 = r0;
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r141);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r140);
        r8[r9] = r10;
        r46 = java.lang.String.format(r4, r6, r8);
        r47 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r48 = r0;
        r49 = "b1";
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r0 = r4.size;
        r50 = r0;
        r51 = "webp";
        r53 = 1;
        r52 = r189;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52, r53);
        goto L_0x2eb6;
    L_0x3775:
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x396c;
    L_0x377c:
        r141 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r117 = r141;
    L_0x3780:
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r140 = r141 + r4;
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 == r6) goto L_0x37a5;
    L_0x378f:
        r4 = r188.checkNeedDrawShareButton(r189);
        if (r4 == 0) goto L_0x37a5;
    L_0x3795:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r117 = r117 - r4;
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r141 = r141 - r4;
    L_0x37a5:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r0 = r141;
        if (r0 <= r4) goto L_0x37b1;
    L_0x37ad:
        r141 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x37b1:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r0 = r140;
        if (r0 <= r4) goto L_0x37bd;
    L_0x37b9:
        r140 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x37bd:
        r127 = 0;
        r0 = r189;
        r4 = r0.photoThumbs;
        r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObject = r4;
        r0 = r189;
        r4 = r0.type;
        r6 = 1;
        if (r4 != r6) goto L_0x399a;
    L_0x37d6:
        r188.updateSecretTimeText(r189);
        r0 = r189;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObjectThumb = r4;
    L_0x37e7:
        r23 = 0;
        r97 = 0;
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x3a2d;
    L_0x37f2:
        r97 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r23 = r97;
    L_0x37f6:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x3812;
    L_0x37fc:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r188;
        r6 = r0.currentPhotoObjectThumb;
        if (r4 != r6) goto L_0x3812;
    L_0x3806:
        r0 = r189;
        r4 = r0.type;
        r6 = 1;
        if (r4 != r6) goto L_0x3ac0;
    L_0x380d:
        r4 = 0;
        r0 = r188;
        r0.currentPhotoObjectThumb = r4;
    L_0x3812:
        if (r127 == 0) goto L_0x3864;
    L_0x3814:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r4 = r4.getAutodownloadMask();
        r4 = r4 & 1;
        if (r4 != 0) goto L_0x3829;
    L_0x3824:
        r4 = 0;
        r0 = r188;
        r0.currentPhotoObject = r4;
    L_0x3829:
        r4 = r189.needDrawBluredPreview();
        if (r4 != 0) goto L_0x3864;
    L_0x382f:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x383f;
    L_0x3835:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r188;
        r6 = r0.currentPhotoObjectThumb;
        if (r4 != r6) goto L_0x3864;
    L_0x383f:
        r0 = r188;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x3854;
    L_0x3845:
        r4 = "m";
        r0 = r188;
        r6 = r0.currentPhotoObjectThumb;
        r6 = r6.type;
        r4 = r4.equals(r6);
        if (r4 != 0) goto L_0x3864;
    L_0x3854:
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setNeedsQualityThumb(r6);
        r0 = r188;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setShouldGenerateQualityThumb(r6);
    L_0x3864:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        if (r4 != 0) goto L_0x3875;
    L_0x386a:
        r0 = r189;
        r4 = r0.caption;
        if (r4 == 0) goto L_0x3875;
    L_0x3870:
        r4 = 0;
        r0 = r188;
        r0.mediaBackground = r4;
    L_0x3875:
        if (r23 == 0) goto L_0x3879;
    L_0x3877:
        if (r97 != 0) goto L_0x38eb;
    L_0x3879:
        r0 = r189;
        r4 = r0.type;
        r6 = 8;
        if (r4 != r6) goto L_0x38eb;
    L_0x3881:
        r56 = 0;
    L_0x3883:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r4 = r4.size();
        r0 = r56;
        if (r0 >= r4) goto L_0x38eb;
    L_0x3895:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r0 = r56;
        r64 = r4.get(r0);
        r64 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r64;
        r0 = r64;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 != 0) goto L_0x38b3;
    L_0x38ad:
        r0 = r64;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x3afb;
    L_0x38b3:
        r0 = r64;
        r4 = r0.w;
        r4 = (float) r4;
        r0 = r141;
        r6 = (float) r0;
        r157 = r4 / r6;
        r0 = r64;
        r4 = r0.w;
        r4 = (float) r4;
        r4 = r4 / r157;
        r0 = (int) r4;
        r23 = r0;
        r0 = r64;
        r4 = r0.h;
        r4 = (float) r4;
        r4 = r4 / r157;
        r0 = (int) r4;
        r97 = r0;
        r0 = r97;
        r1 = r140;
        if (r0 <= r1) goto L_0x3ac7;
    L_0x38d7:
        r0 = r97;
        r0 = (float) r0;
        r158 = r0;
        r97 = r140;
        r0 = r97;
        r4 = (float) r0;
        r158 = r158 / r4;
        r0 = r23;
        r4 = (float) r0;
        r4 = r4 / r158;
        r0 = (int) r4;
        r23 = r0;
    L_0x38eb:
        if (r23 == 0) goto L_0x38ef;
    L_0x38ed:
        if (r97 != 0) goto L_0x38f7;
    L_0x38ef:
        r4 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r97 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r97;
    L_0x38f7:
        r0 = r189;
        r4 = r0.type;
        r6 = 3;
        if (r4 != r6) goto L_0x3919;
    L_0x38fe:
        r0 = r188;
        r4 = r0.infoWidth;
        r6 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r23;
        if (r0 >= r4) goto L_0x3919;
    L_0x390d:
        r0 = r188;
        r4 = r0.infoWidth;
        r6 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r23 = r4 + r6;
    L_0x3919:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        if (r4 == 0) goto L_0x3CLASSNAME;
    L_0x391f:
        r92 = 0;
        r83 = r188.getGroupPhotosWidth();
        r56 = 0;
    L_0x3927:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r0 = r56;
        if (r0 >= r4) goto L_0x3aff;
    L_0x3935:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r56;
        r144 = r4.get(r0);
        r144 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r144;
        r0 = r144;
        r4 = r0.minY;
        if (r4 != 0) goto L_0x3aff;
    L_0x3949:
        r0 = r92;
        r8 = (double) r0;
        r0 = r144;
        r4 = r0.pw;
        r0 = r144;
        r6 = r0.leftSpanOffset;
        r4 = r4 + r6;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r0 = (double) r4;
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);
        r8 = r8 + r20;
        r0 = (int) r8;
        r92 = r0;
        r56 = r56 + 1;
        goto L_0x3927;
    L_0x396c:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x3982;
    L_0x3972:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.7 double:5.23867711E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r141 = r0;
        r117 = r141;
        goto L_0x3780;
    L_0x3982:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.7 double:5.23867711E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r141 = r0;
        r117 = r141;
        goto L_0x3780;
    L_0x399a:
        r0 = r189;
        r4 = r0.type;
        r6 = 3;
        if (r4 != r6) goto L_0x39be;
    L_0x39a1:
        r4 = 0;
        r0 = r188;
        r1 = r189;
        r0.createDocumentLayout(r4, r1);
        r0 = r189;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObjectThumb = r4;
        r188.updateSecretTimeText(r189);
        r127 = 1;
        goto L_0x37e7;
    L_0x39be:
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x39d7;
    L_0x39c5:
        r0 = r189;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObjectThumb = r4;
        r127 = 1;
        goto L_0x37e7;
    L_0x39d7:
        r0 = r189;
        r4 = r0.type;
        r6 = 8;
        if (r4 != r6) goto L_0x37e7;
    L_0x39df:
        r0 = r189;
        r4 = r0.photoThumbs;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r0.currentPhotoObjectThumb = r4;
        r0 = r189;
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
        r0 = r188;
        r0.infoWidth = r4;
        r44 = new android.text.StaticLayout;
        r46 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
        r0 = r188;
        r0 = r0.infoWidth;
        r47 = r0;
        r48 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r50 = 0;
        r51 = 0;
        r45 = r5;
        r44.<init>(r45, r46, r47, r48, r49, r50, r51);
        r0 = r44;
        r1 = r188;
        r1.infoLayout = r0;
        r127 = 1;
        goto L_0x37e7;
    L_0x3a2d:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x3a85;
    L_0x3a33:
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r161 = r0;
    L_0x3a39:
        if (r161 == 0) goto L_0x37f6;
    L_0x3a3b:
        r0 = r161;
        r4 = r0.w;
        r4 = (float) r4;
        r0 = r141;
        r6 = (float) r0;
        r157 = r4 / r6;
        r0 = r161;
        r4 = r0.w;
        r4 = (float) r4;
        r4 = r4 / r157;
        r0 = (int) r4;
        r23 = r0;
        r0 = r161;
        r4 = r0.h;
        r4 = (float) r4;
        r4 = r4 / r157;
        r0 = (int) r4;
        r97 = r0;
        if (r23 != 0) goto L_0x3a61;
    L_0x3a5b:
        r4 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r23 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x3a61:
        if (r97 != 0) goto L_0x3a69;
    L_0x3a63:
        r4 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r97 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x3a69:
        r0 = r97;
        r1 = r140;
        if (r0 <= r1) goto L_0x3a8c;
    L_0x3a6f:
        r0 = r97;
        r0 = (float) r0;
        r158 = r0;
        r97 = r140;
        r0 = r97;
        r4 = (float) r0;
        r158 = r158 / r4;
        r0 = r23;
        r4 = (float) r0;
        r4 = r4 / r158;
        r0 = (int) r4;
        r23 = r0;
        goto L_0x37f6;
    L_0x3a85:
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r161 = r0;
        goto L_0x3a39;
    L_0x3a8c:
        r4 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r97;
        if (r0 >= r4) goto L_0x37f6;
    L_0x3a96:
        r4 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r97 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r161;
        r4 = r0.h;
        r4 = (float) r4;
        r0 = r97;
        r6 = (float) r0;
        r98 = r4 / r6;
        r0 = r161;
        r4 = r0.w;
        r4 = (float) r4;
        r4 = r4 / r98;
        r0 = r141;
        r6 = (float) r0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x37f6;
    L_0x3ab4:
        r0 = r161;
        r4 = r0.w;
        r4 = (float) r4;
        r4 = r4 / r98;
        r0 = (int) r4;
        r23 = r0;
        goto L_0x37f6;
    L_0x3ac0:
        r4 = 0;
        r0 = r188;
        r0.currentPhotoObject = r4;
        goto L_0x3812;
    L_0x3ac7:
        r4 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r97;
        if (r0 >= r4) goto L_0x38eb;
    L_0x3ad1:
        r4 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r97 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r64;
        r4 = r0.h;
        r4 = (float) r4;
        r0 = r97;
        r6 = (float) r0;
        r98 = r4 / r6;
        r0 = r64;
        r4 = r0.w;
        r4 = (float) r4;
        r4 = r4 / r98;
        r0 = r141;
        r6 = (float) r0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x38eb;
    L_0x3aef:
        r0 = r64;
        r4 = r0.w;
        r4 = (float) r4;
        r4 = r4 / r98;
        r0 = (int) r4;
        r23 = r0;
        goto L_0x38eb;
    L_0x3afb:
        r56 = r56 + 1;
        goto L_0x3883;
    L_0x3aff:
        r4 = NUM; // 0x420CLASSNAME float:35.0 double:5.47465589E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r92 - r4;
        r0 = r188;
        r0.availableTimeWidth = r4;
    L_0x3b0b:
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x3b39;
    L_0x3b12:
        r0 = r188;
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
        r0 = r188;
        r0.availableTimeWidth = r4;
    L_0x3b39:
        r188.measureTime(r189);
        r0 = r188;
        r6 = r0.timeWidth;
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x3CLASSNAME;
    L_0x3b46:
        r4 = 20;
    L_0x3b48:
        r4 = r4 + 14;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r171 = r6 + r4;
        r0 = r23;
        r1 = r171;
        if (r0 >= r1) goto L_0x3b59;
    L_0x3b57:
        r23 = r171;
    L_0x3b59:
        r4 = r189.isRoundVideo();
        if (r4 == 0) goto L_0x3CLASSNAME;
    L_0x3b5f:
        r0 = r23;
        r1 = r97;
        r97 = java.lang.Math.min(r0, r1);
        r23 = r97;
        r4 = 0;
        r0 = r188;
        r0.drawBackground = r4;
        r0 = r188;
        r4 = r0.photoImage;
        r6 = r23 / 2;
        r4.setRoundRadius(r6);
    L_0x3b77:
        r27 = 0;
        r93 = 0;
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        if (r4 == 0) goto L_0x419f;
    L_0x3b81:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.max(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r116 = r4 * r6;
        r83 = r188.getGroupPhotosWidth();
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r23 = r0;
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.minY;
        if (r4 == 0) goto L_0x3cce;
    L_0x3bb4:
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x3bc4;
    L_0x3bba:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 1;
        if (r4 != 0) goto L_0x3bd4;
    L_0x3bc4:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x3cce;
    L_0x3bca:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x3cce;
    L_0x3bd4:
        r92 = 0;
        r81 = 0;
        r56 = 0;
    L_0x3bda:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r0 = r56;
        if (r0 >= r4) goto L_0x3cca;
    L_0x3be8:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r56;
        r144 = r4.get(r0);
        r144 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r144;
        r0 = r144;
        r4 = r0.minY;
        if (r4 != 0) goto L_0x3CLASSNAME;
    L_0x3bfc:
        r0 = r92;
        r0 = (double) r0;
        r20 = r0;
        r0 = r144;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r144;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x3CLASSNAME;
    L_0x3CLASSNAME:
        r0 = r144;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x3CLASSNAME:
        r8 = r8 + r24;
        r8 = r8 + r20;
        r0 = (int) r8;
        r92 = r0;
    L_0x3CLASSNAME:
        r56 = r56 + 1;
        goto L_0x3bda;
    L_0x3CLASSNAME:
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r117 - r4;
        r0 = r188;
        r0.availableTimeWidth = r4;
        goto L_0x3b0b;
    L_0x3CLASSNAME:
        r4 = 0;
        goto L_0x3b48;
    L_0x3CLASSNAME:
        r4 = r189.needDrawBluredPreview();
        if (r4 == 0) goto L_0x3b77;
    L_0x3c4a:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x3c5f;
    L_0x3CLASSNAME:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r97 = r0;
        r23 = r97;
        goto L_0x3b77;
    L_0x3c5f:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r97 = r0;
        r23 = r97;
        goto L_0x3b77;
    L_0x3CLASSNAME:
        r8 = 0;
        goto L_0x3CLASSNAME;
    L_0x3CLASSNAME:
        r0 = r144;
        r4 = r0.minY;
        r0 = r188;
        r6 = r0.currentPosition;
        r6 = r6.minY;
        if (r4 != r6) goto L_0x3cbe;
    L_0x3CLASSNAME:
        r0 = r81;
        r0 = (double) r0;
        r20 = r0;
        r0 = r144;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r144;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x3cbb;
    L_0x3ca1:
        r0 = r144;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x3cb2:
        r8 = r8 + r24;
        r8 = r8 + r20;
        r0 = (int) r8;
        r81 = r0;
        goto L_0x3CLASSNAME;
    L_0x3cbb:
        r8 = 0;
        goto L_0x3cb2;
    L_0x3cbe:
        r0 = r144;
        r4 = r0.minY;
        r0 = r188;
        r6 = r0.currentPosition;
        r6 = r6.minY;
        if (r4 <= r6) goto L_0x3CLASSNAME;
    L_0x3cca:
        r4 = r92 - r81;
        r23 = r23 + r4;
    L_0x3cce:
        r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        r0 = r188;
        r4 = r0.isAvatarVisible;
        if (r4 == 0) goto L_0x3ce4;
    L_0x3cdc:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
    L_0x3ce4:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        if (r4 == 0) goto L_0x3e4d;
    L_0x3cec:
        r97 = 0;
        r56 = 0;
    L_0x3cf0:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        r4 = r4.length;
        r0 = r56;
        if (r0 >= r4) goto L_0x3d10;
    L_0x3cfb:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        r4 = r4[r56];
        r4 = r4 * r116;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r97 = r97 + r4;
        r56 = r56 + 1;
        goto L_0x3cf0;
    L_0x3d10:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.maxY;
        r0 = r188;
        r6 = r0.currentPosition;
        r6 = r6.minY;
        r4 = r4 - r6;
        r6 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 * r6;
        r97 = r97 + r4;
    L_0x3d26:
        r0 = r23;
        r1 = r188;
        r1.backgroundWidth = r0;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        r141 = r23;
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.edge;
        if (r4 != 0) goto L_0x3d46;
    L_0x3d3e:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r141 = r141 + r4;
    L_0x3d46:
        r140 = r97;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r141 - r4;
        r27 = r27 + r4;
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 != 0) goto L_0x3d6e;
    L_0x3d5c:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.hasSibling;
        if (r4 == 0) goto L_0x3f2f;
    L_0x3d64:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 != 0) goto L_0x3f2f;
    L_0x3d6e:
        r0 = r188;
        r4 = r0.currentPosition;
        r0 = r188;
        r4 = r0.getAdditionalWidthForPosition(r4);
        r27 = r27 + r4;
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.messages;
        r80 = r4.size();
        r102 = 0;
    L_0x3d86:
        r0 = r102;
        r1 = r80;
        if (r0 >= r1) goto L_0x3f2f;
    L_0x3d8c:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.messages;
        r0 = r102;
        r112 = r4.get(r0);
        r112 = (org.telegram.messenger.MessageObject) r112;
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r102;
        r155 = r4.get(r0);
        r155 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r155;
        r0 = r188;
        r4 = r0.currentPosition;
        r0 = r155;
        if (r0 == r4) goto L_0x3f1e;
    L_0x3db0:
        r0 = r155;
        r4 = r0.flags;
        r4 = r4 & 8;
        if (r4 == 0) goto L_0x3f1e;
    L_0x3db8:
        r0 = r155;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r23 = r0;
        r0 = r155;
        r4 = r0.minY;
        if (r4 == 0) goto L_0x3eb2;
    L_0x3dd2:
        r4 = r189.isOutOwner();
        if (r4 == 0) goto L_0x3de0;
    L_0x3dd8:
        r0 = r155;
        r4 = r0.flags;
        r4 = r4 & 1;
        if (r4 != 0) goto L_0x3dee;
    L_0x3de0:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x3eb2;
    L_0x3de6:
        r0 = r155;
        r4 = r0.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x3eb2;
    L_0x3dee:
        r92 = 0;
        r81 = 0;
        r56 = 0;
    L_0x3df4:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r0 = r56;
        if (r0 >= r4) goto L_0x3eae;
    L_0x3e02:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r56;
        r144 = r4.get(r0);
        r144 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r144;
        r0 = r144;
        r4 = r0.minY;
        if (r4 != 0) goto L_0x3e62;
    L_0x3e16:
        r0 = r92;
        r0 = (double) r0;
        r20 = r0;
        r0 = r144;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r144;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x3e5f;
    L_0x3e32:
        r0 = r144;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x3e43:
        r8 = r8 + r24;
        r8 = r8 + r20;
        r0 = (int) r8;
        r92 = r0;
    L_0x3e4a:
        r56 = r56 + 1;
        goto L_0x3df4;
    L_0x3e4d:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.ph;
        r4 = r4 * r116;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r97 = r0;
        goto L_0x3d26;
    L_0x3e5f:
        r8 = 0;
        goto L_0x3e43;
    L_0x3e62:
        r0 = r144;
        r4 = r0.minY;
        r0 = r155;
        r6 = r0.minY;
        if (r4 != r6) goto L_0x3ea4;
    L_0x3e6c:
        r0 = r81;
        r0 = (double) r0;
        r20 = r0;
        r0 = r144;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r144;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x3ea1;
    L_0x3e88:
        r0 = r144;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r83;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x3e99:
        r8 = r8 + r24;
        r8 = r8 + r20;
        r0 = (int) r8;
        r81 = r0;
        goto L_0x3e4a;
    L_0x3ea1:
        r8 = 0;
        goto L_0x3e99;
    L_0x3ea4:
        r0 = r144;
        r4 = r0.minY;
        r0 = r155;
        r6 = r0.minY;
        if (r4 <= r6) goto L_0x3e4a;
    L_0x3eae:
        r4 = r92 - r81;
        r23 = r23 + r4;
    L_0x3eb2:
        r4 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x3edc;
    L_0x3ec0:
        r4 = r112.isOutOwner();
        if (r4 != 0) goto L_0x3edc;
    L_0x3ec6:
        r4 = r112.needDrawAvatar();
        if (r4 == 0) goto L_0x3edc;
    L_0x3ecc:
        if (r155 == 0) goto L_0x3ed4;
    L_0x3ece:
        r0 = r155;
        r4 = r0.edge;
        if (r4 == 0) goto L_0x3edc;
    L_0x3ed4:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 - r4;
    L_0x3edc:
        r0 = r188;
        r1 = r155;
        r4 = r0.getAdditionalWidthForPosition(r1);
        r23 = r23 + r4;
        r0 = r155;
        r4 = r0.edge;
        if (r4 != 0) goto L_0x3ef4;
    L_0x3eec:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r23 = r23 + r4;
    L_0x3ef4:
        r27 = r27 + r23;
        r0 = r155;
        r4 = r0.minX;
        r0 = r188;
        r6 = r0.currentPosition;
        r6 = r6.minX;
        if (r4 < r6) goto L_0x3var_;
    L_0x3var_:
        r0 = r188;
        r4 = r0.currentMessagesGroup;
        r4 = r4.hasSibling;
        if (r4 == 0) goto L_0x3f1e;
    L_0x3f0a:
        r0 = r155;
        r4 = r0.minY;
        r0 = r155;
        r6 = r0.maxY;
        if (r4 == r6) goto L_0x3f1e;
    L_0x3var_:
        r0 = r188;
        r4 = r0.captionOffsetX;
        r4 = r4 - r23;
        r0 = r188;
        r0.captionOffsetX = r4;
    L_0x3f1e:
        r0 = r112;
        r4 = r0.caption;
        if (r4 == 0) goto L_0x419b;
    L_0x3var_:
        r0 = r188;
        r4 = r0.currentCaption;
        if (r4 == 0) goto L_0x4193;
    L_0x3f2a:
        r4 = 0;
        r0 = r188;
        r0.currentCaption = r4;
    L_0x3f2f:
        r0 = r188;
        r4 = r0.currentCaption;
        if (r4 == 0) goto L_0x4057;
    L_0x3var_:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x4233 }
        r6 = 24;
        if (r4 < r6) goto L_0x4214;
    L_0x3f3b:
        r0 = r188;
        r4 = r0.currentCaption;	 Catch:{ Exception -> 0x4233 }
        r6 = 0;
        r0 = r188;
        r8 = r0.currentCaption;	 Catch:{ Exception -> 0x4233 }
        r8 = r8.length();	 Catch:{ Exception -> 0x4233 }
        r9 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x4233 }
        r0 = r27;
        r4 = android.text.StaticLayout.Builder.obtain(r4, r6, r8, r9, r0);	 Catch:{ Exception -> 0x4233 }
        r6 = 1;
        r4 = r4.setBreakStrategy(r6);	 Catch:{ Exception -> 0x4233 }
        r6 = 0;
        r4 = r4.setHyphenationFrequency(r6);	 Catch:{ Exception -> 0x4233 }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x4233 }
        r4 = r4.setAlignment(r6);	 Catch:{ Exception -> 0x4233 }
        r4 = r4.build();	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x4233 }
    L_0x3var_:
        r0 = r188;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4233 }
        r108 = r4.getLineCount();	 Catch:{ Exception -> 0x4233 }
        if (r108 <= 0) goto L_0x4057;
    L_0x3var_:
        if (r93 == 0) goto L_0x423d;
    L_0x3var_:
        r4 = 0;
        r0 = r188;
        r0.captionWidth = r4;	 Catch:{ Exception -> 0x4233 }
        r56 = 0;
    L_0x3f7b:
        r0 = r56;
        r1 = r108;
        if (r0 >= r1) goto L_0x3fb7;
    L_0x3var_:
        r0 = r188;
        r4 = r0.captionWidth;	 Catch:{ Exception -> 0x4233 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4233 }
        r0 = r56;
        r4 = r4.getLineWidth(r0);	 Catch:{ Exception -> 0x4233 }
        r0 = (double) r4;	 Catch:{ Exception -> 0x4233 }
        r20 = r0;
        r20 = java.lang.Math.ceil(r20);	 Catch:{ Exception -> 0x4233 }
        r0 = r20;
        r8 = java.lang.Math.max(r8, r0);	 Catch:{ Exception -> 0x4233 }
        r4 = (int) r8;	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r0.captionWidth = r4;	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4233 }
        r0 = r56;
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x4233 }
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x4239;
    L_0x3fb1:
        r0 = r27;
        r1 = r188;
        r1.captionWidth = r0;	 Catch:{ Exception -> 0x4233 }
    L_0x3fb7:
        r0 = r188;
        r4 = r0.captionWidth;	 Catch:{ Exception -> 0x4233 }
        r0 = r27;
        if (r4 <= r0) goto L_0x3fc5;
    L_0x3fbf:
        r0 = r27;
        r1 = r188;
        r1.captionWidth = r0;	 Catch:{ Exception -> 0x4233 }
    L_0x3fc5:
        r0 = r188;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4233 }
        r4 = r4.getHeight();	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r0.captionHeight = r4;	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r4 = r0.captionHeight;	 Catch:{ Exception -> 0x4233 }
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4233 }
        r4 = r4 + r6;
        r0 = r188;
        r0.addedCaptionHeight = r4;	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r4 = r0.currentPosition;	 Catch:{ Exception -> 0x4233 }
        if (r4 == 0) goto L_0x3ff0;
    L_0x3fe6:
        r0 = r188;
        r4 = r0.currentPosition;	 Catch:{ Exception -> 0x4233 }
        r4 = r4.flags;	 Catch:{ Exception -> 0x4233 }
        r4 = r4 & 8;
        if (r4 == 0) goto L_0x4245;
    L_0x3ff0:
        r0 = r188;
        r4 = r0.addedCaptionHeight;	 Catch:{ Exception -> 0x4233 }
        r58 = r58 + r4;
        r0 = r188;
        r4 = r0.captionWidth;	 Catch:{ Exception -> 0x4233 }
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4233 }
        r6 = r141 - r6;
        r184 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x4233 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x4233 }
        r6 = r6 + -1;
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r8 = r0.captionLayout;	 Catch:{ Exception -> 0x4233 }
        r8 = r8.getLineCount();	 Catch:{ Exception -> 0x4233 }
        r8 = r8 + -1;
        r6 = r6.getLineLeft(r8);	 Catch:{ Exception -> 0x4233 }
        r107 = r4 + r6;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4233 }
        r4 = r4 + r184;
        r4 = (float) r4;	 Catch:{ Exception -> 0x4233 }
        r4 = r4 - r107;
        r0 = r171;
        r6 = (float) r0;	 Catch:{ Exception -> 0x4233 }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x4057;
    L_0x403e:
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x4233 }
        r58 = r58 + r4;
        r0 = r188;
        r4 = r0.addedCaptionHeight;	 Catch:{ Exception -> 0x4233 }
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x4233 }
        r4 = r4 + r6;
        r0 = r188;
        r0.addedCaptionHeight = r4;	 Catch:{ Exception -> 0x4233 }
        r77 = 1;
    L_0x4057:
        if (r93 == 0) goto L_0x4095;
    L_0x4059:
        r0 = r188;
        r4 = r0.captionWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r141;
        if (r0 >= r4) goto L_0x4095;
    L_0x4068:
        r0 = r188;
        r4 = r0.captionWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r141 = r4 + r6;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r141;
        r0 = r188;
        r0.backgroundWidth = r4;
        r0 = r188;
        r4 = r0.mediaBackground;
        if (r4 != 0) goto L_0x4095;
    L_0x4086:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.backgroundWidth = r4;
    L_0x4095:
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
        r0 = r97;
        r10 = (float) r0;
        r12 = org.telegram.messenger.AndroidUtilities.density;
        r10 = r10 / r12;
        r10 = (int) r10;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r4 = java.lang.String.format(r4, r6, r8);
        r0 = r188;
        r0.currentPhotoFilterThumb = r4;
        r0 = r188;
        r0.currentPhotoFilter = r4;
        r0 = r189;
        r4 = r0.photoThumbs;
        if (r4 == 0) goto L_0x40d6;
    L_0x40cb:
        r0 = r189;
        r4 = r0.photoThumbs;
        r4 = r4.size();
        r6 = 1;
        if (r4 > r6) goto L_0x40ec;
    L_0x40d6:
        r0 = r189;
        r4 = r0.type;
        r6 = 3;
        if (r4 == r6) goto L_0x40ec;
    L_0x40dd:
        r0 = r189;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x40ec;
    L_0x40e5:
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x412a;
    L_0x40ec:
        r4 = r189.needDrawBluredPreview();
        if (r4 == 0) goto L_0x424c;
    L_0x40f2:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r188;
        r6 = r0.currentPhotoFilter;
        r4 = r4.append(r6);
        r6 = "_b2";
        r4 = r4.append(r6);
        r4 = r4.toString();
        r0 = r188;
        r0.currentPhotoFilter = r4;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r188;
        r6 = r0.currentPhotoFilterThumb;
        r4 = r4.append(r6);
        r6 = "_b2";
        r4 = r4.append(r6);
        r4 = r4.toString();
        r0 = r188;
        r0.currentPhotoFilterThumb = r4;
    L_0x412a:
        r132 = 0;
        r0 = r189;
        r4 = r0.type;
        r6 = 3;
        if (r4 == r6) goto L_0x4142;
    L_0x4133:
        r0 = r189;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x4142;
    L_0x413b:
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x4144;
    L_0x4142:
        r132 = 1;
    L_0x4144:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x415b;
    L_0x414a:
        if (r132 != 0) goto L_0x415b;
    L_0x414c:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r4 = r4.size;
        if (r4 != 0) goto L_0x415b;
    L_0x4154:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r6 = -1;
        r4.size = r6;
    L_0x415b:
        r0 = r188;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x4172;
    L_0x4161:
        if (r132 != 0) goto L_0x4172;
    L_0x4163:
        r0 = r188;
        r4 = r0.currentPhotoObjectThumb;
        r4 = r4.size;
        if (r4 != 0) goto L_0x4172;
    L_0x416b:
        r0 = r188;
        r4 = r0.currentPhotoObjectThumb;
        r6 = -1;
        r4.size = r6;
    L_0x4172:
        r0 = r189;
        r4 = r0.type;
        r6 = 1;
        if (r4 != r6) goto L_0x4352;
    L_0x4179:
        r0 = r189;
        r4 = r0.useCustomPhoto;
        if (r4 == 0) goto L_0x426a;
    L_0x417f:
        r0 = r188;
        r4 = r0.photoImage;
        r6 = r188.getResources();
        r8 = NUM; // 0x7var_ float:1.7945661E38 double:1.052935767E-314;
        r6 = r6.getDrawable(r8);
        r4.setImageBitmap(r6);
        goto L_0x2eb6;
    L_0x4193:
        r0 = r112;
        r4 = r0.caption;
        r0 = r188;
        r0.currentCaption = r4;
    L_0x419b:
        r102 = r102 + 1;
        goto L_0x3d86;
    L_0x419f:
        r140 = r97;
        r141 = r23;
        r0 = r189;
        r4 = r0.caption;
        r0 = r188;
        r0.currentCaption = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x41f6;
    L_0x41b1:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.65 double:5.234532584E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r125 = r0;
    L_0x41bd:
        r4 = r189.needDrawBluredPreview();
        if (r4 != 0) goto L_0x420b;
    L_0x41c3:
        r0 = r188;
        r4 = r0.currentCaption;
        if (r4 == 0) goto L_0x420b;
    L_0x41c9:
        r0 = r141;
        r1 = r125;
        if (r0 >= r1) goto L_0x420b;
    L_0x41cf:
        r27 = r125;
        r93 = 1;
    L_0x41d3:
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r141;
        r0 = r188;
        r0.backgroundWidth = r4;
        r0 = r188;
        r4 = r0.mediaBackground;
        if (r4 != 0) goto L_0x3f2f;
    L_0x41e5:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.backgroundWidth = r4;
        goto L_0x3f2f;
    L_0x41f6:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3var_ float:0.65 double:5.234532584E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r125 = r0;
        goto L_0x41bd;
    L_0x420b:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r27 = r141 - r4;
        goto L_0x41d3;
    L_0x4214:
        r44 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x4233 }
        r0 = r188;
        r0 = r0.currentCaption;	 Catch:{ Exception -> 0x4233 }
        r45 = r0;
        r46 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x4233 }
        r48 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x4233 }
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r50 = 0;
        r51 = 0;
        r47 = r27;
        r44.<init>(r45, r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x4233 }
        r0 = r44;
        r1 = r188;
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x4233 }
        goto L_0x3var_;
    L_0x4233:
        r90 = move-exception;
        org.telegram.messenger.FileLog.e(r90);
        goto L_0x4057;
    L_0x4239:
        r56 = r56 + 1;
        goto L_0x3f7b;
    L_0x423d:
        r0 = r27;
        r1 = r188;
        r1.captionWidth = r0;	 Catch:{ Exception -> 0x4233 }
        goto L_0x3fc5;
    L_0x4245:
        r4 = 0;
        r0 = r188;
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x4233 }
        goto L_0x4057;
    L_0x424c:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r188;
        r6 = r0.currentPhotoFilterThumb;
        r4 = r4.append(r6);
        r6 = "_b";
        r4 = r4.append(r6);
        r4 = r4.toString();
        r0 = r188;
        r0.currentPhotoFilterThumb = r4;
        goto L_0x412a;
    L_0x426a:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x4346;
    L_0x4270:
        r139 = 1;
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r91 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r0 = r189;
        r4 = r0.mediaExists;
        if (r4 == 0) goto L_0x42ec;
    L_0x4280:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r188;
        r4.removeLoadingFileObserver(r0);
    L_0x428d:
        if (r139 != 0) goto L_0x42b1;
    L_0x428f:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r188;
        r6 = r0.currentMessageObject;
        r4 = r4.canDownloadMedia(r6);
        if (r4 != 0) goto L_0x42b1;
    L_0x42a1:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r91;
        r4 = r4.isLoadingFile(r0);
        if (r4 == 0) goto L_0x42fb;
    L_0x42b1:
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r45 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilter;
        r46 = r0;
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r47 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilterThumb;
        r48 = r0;
        if (r132 == 0) goto L_0x42ef;
    L_0x42d1:
        r49 = 0;
    L_0x42d3:
        r50 = 0;
        r0 = r188;
        r0 = r0.currentMessageObject;
        r51 = r0;
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.shouldEncryptPhotoOrVideo();
        if (r4 == 0) goto L_0x42f8;
    L_0x42e5:
        r52 = 2;
    L_0x42e7:
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52);
        goto L_0x2eb6;
    L_0x42ec:
        r139 = 0;
        goto L_0x428d;
    L_0x42ef:
        r0 = r188;
        r4 = r0.currentPhotoObject;
        r0 = r4.size;
        r49 = r0;
        goto L_0x42d3;
    L_0x42f8:
        r52 = 0;
        goto L_0x42e7;
    L_0x42fb:
        r4 = 1;
        r0 = r188;
        r0.photoNotSet = r4;
        r0 = r188;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x433a;
    L_0x4306:
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r45 = 0;
        r46 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r47 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilterThumb;
        r48 = r0;
        r49 = 0;
        r50 = 0;
        r0 = r188;
        r0 = r0.currentMessageObject;
        r51 = r0;
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.shouldEncryptPhotoOrVideo();
        if (r4 == 0) goto L_0x4337;
    L_0x4330:
        r52 = 2;
    L_0x4332:
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52);
        goto L_0x2eb6;
    L_0x4337:
        r52 = 0;
        goto L_0x4332;
    L_0x433a:
        r0 = r188;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        goto L_0x2eb6;
    L_0x4346:
        r0 = r188;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        goto L_0x2eb6;
    L_0x4352:
        r0 = r189;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x4361;
    L_0x435a:
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x4483;
    L_0x4361:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r91 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r111 = 0;
        r0 = r189;
        r4 = r0.attachPathExists;
        if (r4 == 0) goto L_0x43f4;
    L_0x4375:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r188;
        r4.removeLoadingFileObserver(r0);
        r111 = 1;
    L_0x4384:
        r67 = 0;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = org.telegram.messenger.MessageObject.isNewGifDocument(r4);
        if (r4 == 0) goto L_0x43fd;
    L_0x4394:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r188;
        r6 = r0.currentMessageObject;
        r67 = r4.canDownloadMedia(r6);
    L_0x43a4:
        r4 = r189.isSending();
        if (r4 != 0) goto L_0x4453;
    L_0x43aa:
        r4 = r189.isEditing();
        if (r4 != 0) goto L_0x4453;
    L_0x43b0:
        if (r111 != 0) goto L_0x43c4;
    L_0x43b2:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r91;
        r4 = r4.isLoadingFile(r0);
        if (r4 != 0) goto L_0x43c4;
    L_0x43c2:
        if (r67 == 0) goto L_0x4453;
    L_0x43c4:
        r4 = 1;
        r0 = r111;
        if (r0 != r4) goto L_0x441e;
    L_0x43c9:
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r4 = r189.isSendError();
        if (r4 == 0) goto L_0x4415;
    L_0x43d5:
        r45 = 0;
    L_0x43d7:
        r46 = 0;
        r47 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r48 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilterThumb;
        r49 = r0;
        r50 = 0;
        r51 = 0;
        r53 = 0;
        r52 = r189;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52, r53);
        goto L_0x2eb6;
    L_0x43f4:
        r0 = r189;
        r4 = r0.mediaExists;
        if (r4 == 0) goto L_0x4384;
    L_0x43fa:
        r111 = 2;
        goto L_0x4384;
    L_0x43fd:
        r0 = r189;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x43a4;
    L_0x4404:
        r0 = r188;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r188;
        r6 = r0.currentMessageObject;
        r67 = r4.canDownloadMedia(r6);
        goto L_0x43a4;
    L_0x4415:
        r0 = r189;
        r4 = r0.messageOwner;
        r0 = r4.attachPath;
        r45 = r0;
        goto L_0x43d7;
    L_0x441e:
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.document;
        r45 = r0;
        r46 = 0;
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r47 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilterThumb;
        r48 = r0;
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r0 = r4.size;
        r49 = r0;
        r50 = 0;
        r52 = 0;
        r51 = r189;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52);
        goto L_0x2eb6;
    L_0x4453:
        r4 = 1;
        r0 = r188;
        r0.photoNotSet = r4;
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r45 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilter;
        r46 = r0;
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r47 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilterThumb;
        r48 = r0;
        r49 = 0;
        r50 = 0;
        r52 = 0;
        r51 = r189;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52);
        goto L_0x2eb6;
    L_0x4483:
        r0 = r188;
        r0 = r0.photoImage;
        r44 = r0;
        r0 = r188;
        r0 = r0.currentPhotoObject;
        r45 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilter;
        r46 = r0;
        r0 = r188;
        r0 = r0.currentPhotoObjectThumb;
        r47 = r0;
        r0 = r188;
        r0 = r0.currentPhotoFilterThumb;
        r48 = r0;
        r49 = 0;
        r50 = 0;
        r0 = r188;
        r4 = r0.currentMessageObject;
        r4 = r4.shouldEncryptPhotoOrVideo();
        if (r4 == 0) goto L_0x44b8;
    L_0x44af:
        r52 = 2;
    L_0x44b1:
        r51 = r189;
        r44.setImage(r45, r46, r47, r48, r49, r50, r51, r52);
        goto L_0x2eb6;
    L_0x44b8:
        r52 = 0;
        goto L_0x44b1;
    L_0x44bb:
        r0 = r188;
        r4 = r0.drawNameLayout;
        if (r4 == 0) goto L_0x2ee9;
    L_0x44c1:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.reply_to_msg_id;
        if (r4 != 0) goto L_0x2ee9;
    L_0x44c9:
        r0 = r188;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.namesOffset = r4;
        goto L_0x2ee9;
    L_0x44da:
        r44 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x44f9 }
        r0 = r189;
        r0 = r0.caption;	 Catch:{ Exception -> 0x44f9 }
        r45 = r0;
        r46 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x44f9 }
        r48 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x44f9 }
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r50 = 0;
        r51 = 0;
        r47 = r27;
        r44.<init>(r45, r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x44f9 }
        r0 = r44;
        r1 = r188;
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x44f9 }
        goto L_0x1375;
    L_0x44f9:
        r90 = move-exception;
        org.telegram.messenger.FileLog.e(r90);
        goto L_0x1375;
    L_0x44ff:
        r4 = 0;
        goto L_0x13b5;
    L_0x4502:
        r90 = move-exception;
        org.telegram.messenger.FileLog.e(r90);
        goto L_0x142f;
    L_0x4508:
        r4 = 0;
        goto L_0x14e9;
    L_0x450b:
        r90 = move-exception;
        org.telegram.messenger.FileLog.e(r90);
        goto L_0x1513;
    L_0x4511:
        r0 = r188;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x4522 }
        r0 = r109;
        r6 = -r0;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x4522 }
        r0 = r188;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x4522 }
        goto L_0x15a9;
    L_0x4522:
        r90 = move-exception;
        org.telegram.messenger.FileLog.e(r90);
    L_0x4526:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.totalHeight = r4;
        if (r77 == 0) goto L_0x455a;
    L_0x4537:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.totalHeight = r4;
        r4 = 2;
        r0 = r77;
        if (r0 != r4) goto L_0x455a;
    L_0x454b:
        r0 = r188;
        r4 = r0.captionHeight;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.captionHeight = r4;
    L_0x455a:
        r0 = r188;
        r4 = r0.botButtons;
        r4.clear();
        if (r124 == 0) goto L_0x4576;
    L_0x4563:
        r0 = r188;
        r4 = r0.botButtonsByData;
        r4.clear();
        r0 = r188;
        r4 = r0.botButtonsByPosition;
        r4.clear();
        r4 = 0;
        r0 = r188;
        r0.botButtonsLayout = r4;
    L_0x4576:
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 != 0) goto L_0x48ab;
    L_0x457c:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.reply_markup;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
        if (r4 == 0) goto L_0x48ab;
    L_0x4586:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.reply_markup;
        r4 = r4.rows;
        r156 = r4.size();
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 * r156;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r188;
        r0.keyboardHeight = r4;
        r0 = r188;
        r0.substractBackgroundHeight = r4;
        r0 = r188;
        r6 = r0.backgroundWidth;
        r0 = r188;
        r4 = r0.mediaBackground;
        if (r4 == 0) goto L_0x4669;
    L_0x45b3:
        r4 = 0;
    L_0x45b4:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r0 = r188;
        r0.widthForButtons = r4;
        r94 = 0;
        r0 = r189;
        r4 = r0.wantedBotKeyboardWidth;
        r0 = r188;
        r6 = r0.widthForButtons;
        if (r4 <= r6) goto L_0x4607;
    L_0x45ca:
        r0 = r188;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x466d;
    L_0x45d0:
        r4 = r189.needDrawAvatar();
        if (r4 == 0) goto L_0x466d;
    L_0x45d6:
        r4 = r189.isOutOwner();
        if (r4 != 0) goto L_0x466d;
    L_0x45dc:
        r4 = NUM; // 0x42780000 float:62.0 double:5.5096253E-315;
    L_0x45de:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = -r4;
        r113 = r0;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x4671;
    L_0x45eb:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r113 = r113 + r4;
    L_0x45f1:
        r0 = r188;
        r4 = r0.backgroundWidth;
        r0 = r189;
        r6 = r0.wantedBotKeyboardWidth;
        r0 = r113;
        r6 = java.lang.Math.min(r6, r0);
        r4 = java.lang.Math.max(r4, r6);
        r0 = r188;
        r0.widthForButtons = r4;
    L_0x4607:
        r114 = 0;
        r135 = new java.util.HashMap;
        r0 = r188;
        r4 = r0.botButtonsByData;
        r0 = r135;
        r0.<init>(r4);
        r0 = r189;
        r4 = r0.botButtonsLayout;
        if (r4 == 0) goto L_0x4688;
    L_0x461a:
        r0 = r188;
        r4 = r0.botButtonsLayout;
        if (r4 == 0) goto L_0x4688;
    L_0x4620:
        r0 = r188;
        r4 = r0.botButtonsLayout;
        r0 = r189;
        r6 = r0.botButtonsLayout;
        r6 = r6.toString();
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x4688;
    L_0x4632:
        r136 = new java.util.HashMap;
        r0 = r188;
        r4 = r0.botButtonsByPosition;
        r0 = r136;
        r0.<init>(r4);
    L_0x463d:
        r0 = r188;
        r4 = r0.botButtonsByData;
        r4.clear();
        r56 = 0;
    L_0x4646:
        r0 = r56;
        r1 = r156;
        if (r0 >= r1) goto L_0x4820;
    L_0x464c:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.reply_markup;
        r4 = r4.rows;
        r0 = r56;
        r154 = r4.get(r0);
        r154 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r154;
        r0 = r154;
        r4 = r0.buttons;
        r75 = r4.size();
        if (r75 != 0) goto L_0x469d;
    L_0x4666:
        r56 = r56 + 1;
        goto L_0x4646;
    L_0x4669:
        r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        goto L_0x45b4;
    L_0x466d:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        goto L_0x45de;
    L_0x4671:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r113 = r113 + r4;
        goto L_0x45f1;
    L_0x4688:
        r0 = r189;
        r4 = r0.botButtonsLayout;
        if (r4 == 0) goto L_0x469a;
    L_0x468e:
        r0 = r189;
        r4 = r0.botButtonsLayout;
        r4 = r4.toString();
        r0 = r188;
        r0.botButtonsLayout = r4;
    L_0x469a:
        r136 = 0;
        goto L_0x463d;
    L_0x469d:
        r0 = r188;
        r4 = r0.widthForButtons;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r8 = r75 + -1;
        r6 = r6 * r8;
        r4 = r4 - r6;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r74 = r4 / r75;
        r68 = 0;
    L_0x46b6:
        r0 = r154;
        r4 = r0.buttons;
        r4 = r4.size();
        r0 = r68;
        if (r0 >= r4) goto L_0x4666;
    L_0x46c2:
        r72 = new org.telegram.ui.Cells.ChatMessageCell$BotButton;
        r4 = 0;
        r0 = r72;
        r1 = r188;
        r0.<init>(r1, r4);
        r0 = r154;
        r4 = r0.buttons;
        r0 = r68;
        r4 = r4.get(r0);
        r4 = (org.telegram.tgnet.TLRPC.KeyboardButton) r4;
        r0 = r72;
        r0.button = r4;
        r4 = r72.button;
        r4 = r4.data;
        r106 = org.telegram.messenger.Utilities.bytesToHex(r4);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r56;
        r4 = r4.append(r0);
        r6 = "";
        r4 = r4.append(r6);
        r0 = r68;
        r4 = r4.append(r0);
        r144 = r4.toString();
        if (r136 == 0) goto L_0x47dd;
    L_0x4705:
        r0 = r136;
        r1 = r144;
        r134 = r0.get(r1);
        r134 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r134;
    L_0x470f:
        if (r134 == 0) goto L_0x47e9;
    L_0x4711:
        r4 = r134.progressAlpha;
        r0 = r72;
        r0.progressAlpha = r4;
        r4 = r134.angle;
        r0 = r72;
        r0.angle = r4;
        r8 = r134.lastUpdateTime;
        r0 = r72;
        r0.lastUpdateTime = r8;
    L_0x472c:
        r0 = r188;
        r4 = r0.botButtonsByData;
        r0 = r106;
        r1 = r72;
        r4.put(r0, r1);
        r0 = r188;
        r4 = r0.botButtonsByPosition;
        r0 = r144;
        r1 = r72;
        r4.put(r0, r1);
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r74;
        r4 = r4 * r68;
        r0 = r72;
        r0.x = r4;
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 * r56;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r72;
        r0.y = r4;
        r0 = r72;
        r1 = r74;
        r0.width = r1;
        r4 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r72;
        r0.height = r4;
        r4 = r72.button;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r4 == 0) goto L_0x47f4;
    L_0x477f:
        r0 = r189;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x47f4;
    L_0x478b:
        r4 = "PaymentReceipt";
        r6 = NUM; // 0x7f0CLASSNAMEa7 float:1.8612646E38 double:1.05309824E-314;
        r45 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x4795:
        r44 = new android.text.StaticLayout;
        r46 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r47 = r74 - r4;
        r48 = android.text.Layout.Alignment.ALIGN_CENTER;
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r50 = 0;
        r51 = 0;
        r44.<init>(r45, r46, r47, r48, r49, r50, r51);
        r0 = r72;
        r1 = r44;
        r0.title = r1;
        r0 = r188;
        r4 = r0.botButtons;
        r0 = r72;
        r4.add(r0);
        r0 = r154;
        r4 = r0.buttons;
        r4 = r4.size();
        r4 = r4 + -1;
        r0 = r68;
        if (r0 != r4) goto L_0x47d9;
    L_0x47ca:
        r4 = r72.x;
        r6 = r72.width;
        r4 = r4 + r6;
        r0 = r114;
        r114 = java.lang.Math.max(r0, r4);
    L_0x47d9:
        r68 = r68 + 1;
        goto L_0x46b6;
    L_0x47dd:
        r0 = r135;
        r1 = r106;
        r134 = r0.get(r1);
        r134 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r134;
        goto L_0x470f;
    L_0x47e9:
        r8 = java.lang.System.currentTimeMillis();
        r0 = r72;
        r0.lastUpdateTime = r8;
        goto L_0x472c;
    L_0x47f4:
        r4 = r72.button;
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
        r6 = r74 - r6;
        r6 = (float) r6;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r45;
        r45 = android.text.TextUtils.ellipsize(r0, r4, r6, r8);
        goto L_0x4795;
    L_0x4820:
        r0 = r114;
        r1 = r188;
        r1.widthForButtons = r0;
    L_0x4826:
        r0 = r188;
        r4 = r0.drawPinnedBottom;
        if (r4 == 0) goto L_0x48b7;
    L_0x482c:
        r0 = r188;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x48b7;
    L_0x4832:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.totalHeight = r4;
    L_0x4841:
        r0 = r189;
        r4 = r0.type;
        r6 = 13;
        if (r4 != r6) goto L_0x485f;
    L_0x4849:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x428CLASSNAME float:70.0 double:5.51610112E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        if (r4 >= r6) goto L_0x485f;
    L_0x4855:
        r4 = NUM; // 0x428CLASSNAME float:70.0 double:5.51610112E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r188;
        r0.totalHeight = r4;
    L_0x485f:
        r0 = r188;
        r4 = r0.drawPhotoImage;
        if (r4 != 0) goto L_0x486f;
    L_0x4865:
        r0 = r188;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
    L_0x486f:
        r0 = r188;
        r4 = r0.documentAttachType;
        r6 = 5;
        if (r4 != r6) goto L_0x491b;
    L_0x4876:
        r0 = r188;
        r4 = r0.documentAttach;
        r4 = org.telegram.messenger.MessageObject.isDocumentHasThumb(r4);
        if (r4 == 0) goto L_0x48f9;
    L_0x4880:
        r0 = r188;
        r4 = r0.documentAttach;
        r4 = r4.thumbs;
        r6 = 90;
        r167 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r188;
        r4 = r0.radialProgress;
        r0 = r167;
        r1 = r189;
        r4.setImageOverlay(r0, r1);
    L_0x4897:
        r188.updateWaveform();
        r6 = 0;
        if (r84 == 0) goto L_0x4926;
    L_0x489d:
        r0 = r189;
        r4 = r0.cancelEditing;
        if (r4 != 0) goto L_0x4926;
    L_0x48a3:
        r4 = 1;
    L_0x48a4:
        r8 = 1;
        r0 = r188;
        r0.updateButtonState(r6, r4, r8);
        return;
    L_0x48ab:
        r4 = 0;
        r0 = r188;
        r0.substractBackgroundHeight = r4;
        r4 = 0;
        r0 = r188;
        r0.keyboardHeight = r4;
        goto L_0x4826;
    L_0x48b7:
        r0 = r188;
        r4 = r0.drawPinnedBottom;
        if (r4 == 0) goto L_0x48ce;
    L_0x48bd:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.totalHeight = r4;
        goto L_0x4841;
    L_0x48ce:
        r0 = r188;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x4841;
    L_0x48d4:
        r0 = r188;
        r4 = r0.pinnedBottom;
        if (r4 == 0) goto L_0x4841;
    L_0x48da:
        r0 = r188;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x4841;
    L_0x48e0:
        r0 = r188;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        if (r4 != 0) goto L_0x4841;
    L_0x48e8:
        r0 = r188;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r188;
        r0.totalHeight = r4;
        goto L_0x4841;
    L_0x48f9:
        r4 = 1;
        r0 = r189;
        r63 = r0.getArtworkUrl(r4);
        r4 = android.text.TextUtils.isEmpty(r63);
        if (r4 != 0) goto L_0x4910;
    L_0x4906:
        r0 = r188;
        r4 = r0.radialProgress;
        r0 = r63;
        r4.setImageOverlay(r0);
        goto L_0x4897;
    L_0x4910:
        r0 = r188;
        r4 = r0.radialProgress;
        r6 = 0;
        r8 = 0;
        r4.setImageOverlay(r6, r8);
        goto L_0x4897;
    L_0x491b:
        r0 = r188;
        r4 = r0.radialProgress;
        r6 = 0;
        r8 = 0;
        r4.setImageOverlay(r6, r8);
        goto L_0x4897;
    L_0x4926:
        r4 = 0;
        goto L_0x48a4;
    L_0x4929:
        r90 = move-exception;
        goto L_0x0var_;
    L_0x492c:
        r90 = move-exception;
        r13 = r152;
        goto L_0x0d80;
    L_0x4931:
        r15 = r180;
        goto L_0x1CLASSNAME;
    L_0x4935:
        r15 = r180;
        goto L_0x0fcb;
    L_0x4939:
        r13 = r152;
        goto L_0x0e38;
    L_0x493d:
        r152 = r13;
        r11 = r110;
        goto L_0x0d95;
    L_0x4943:
        r11 = r110;
        goto L_0x0d95;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageObject(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, boolean, boolean):void");
    }

    static final /* synthetic */ int lambda$setMessageObject$0$ChatMessageCell(PollButton o1, PollButton o2) {
        if (o1.decimal > o2.decimal) {
            return -1;
        }
        if (o1.decimal < o2.decimal) {
            return 1;
        }
        return 0;
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
                        x -= AndroidUtilities.dp(4.0f);
                    }
                    if (this.currentPosition.leftSpanOffset != 0) {
                        x += (int) Math.ceil((double) ((((float) this.currentPosition.leftSpanOffset) / 1000.0f) * ((float) getGroupPhotosWidth())));
                    }
                }
                this.photoImage.setImageCoords(x, this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                this.buttonX = (int) (((float) x) + (((float) (this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f))) / 2.0f));
                this.buttonY = this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f)) / 2);
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0f), this.buttonY + AndroidUtilities.dp(48.0f));
                this.deleteProgressRect.set((float) (this.buttonX + AndroidUtilities.dp(5.0f)), (float) (this.buttonY + AndroidUtilities.dp(5.0f)), (float) (this.buttonX + AndroidUtilities.dp(43.0f)), (float) (this.buttonY + AndroidUtilities.dp(43.0f)));
            }
        }
    }

    public boolean needDelayRoundProgressDraw() {
        return this.documentAttachType == 7 && this.currentMessageObject.type != 5 && MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
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
        int instantY;
        Paint backPaint;
        Drawable drawable;
        int imageY;
        int x1;
        int y1;
        RadialProgress2 radialProgress2;
        String str;
        Drawable menuDrawable;
        float progress;
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
                    if (this.currentMessageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && MediaController.getInstance().isRoundVideoDrawingReady()) {
                        imageDrawn = true;
                        this.drawTime = true;
                    } else {
                        imageDrawn = this.photoImage.draw(canvas);
                    }
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
                    instantY = (this.linkPreviewHeight + startY) + AndroidUtilities.dp(10.0f);
                    backPaint = Theme.chat_instantViewRectPaint;
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
            if (this.currentMessageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && MediaController.getInstance().isRoundVideoDrawingReady()) {
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
                drawable = Theme.chat_msgMediaMenuDrawable;
                i = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                this.otherX = i;
                imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                this.otherY = imageY;
                BaseCell.setDrawableBounds(drawable, i, imageY);
                Theme.chat_msgMediaMenuDrawable.draw(canvas);
                Theme.chat_msgMediaMenuDrawable.setAlpha(oldAlpha);
            }
        } else if (this.documentAttachType == 7 || this.currentMessageObject.type == 5) {
            if (this.durationLayout != null) {
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
        String text;
        if (this.currentMessageObject.type == 1 || this.documentAttachType == 4) {
            if (this.photoImage.getVisible()) {
                if (!this.currentMessageObject.needDrawBluredPreview() && this.documentAttachType == 4) {
                    oldAlpha = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                    Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                    drawable = Theme.chat_msgMediaMenuDrawable;
                    i = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                    this.otherX = i;
                    imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                    this.otherY = imageY;
                    BaseCell.setDrawableBounds(drawable, i, imageY);
                    Theme.chat_msgMediaMenuDrawable.draw(canvas);
                    Theme.chat_msgMediaMenuDrawable.setAlpha(oldAlpha);
                }
                if (!(this.forceNotDrawTime || this.infoLayout == null || (this.buttonState != 1 && this.buttonState != 0 && this.buttonState != 3 && !this.currentMessageObject.needDrawBluredPreview()))) {
                    Theme.chat_infoPaint.setColor(Theme.getColor("chat_mediaInfoText"));
                    x1 = this.photoImage.getImageX() + AndroidUtilities.dp(4.0f);
                    y1 = this.photoImage.getImageY() + AndroidUtilities.dp(4.0f);
                    this.rect.set((float) x1, (float) y1, (float) ((this.infoWidth + x1) + AndroidUtilities.dp(8.0f)), (float) (AndroidUtilities.dp(16.5f) + y1));
                    oldAlpha = Theme.chat_timeBackgroundPaint.getAlpha();
                    Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                    Theme.chat_timeBackgroundPaint.setAlpha(oldAlpha);
                    canvas.save();
                    canvas.translate((float) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f)), (float) (this.photoImage.getImageY() + AndroidUtilities.dp(5.5f)));
                    Theme.chat_infoPaint.setAlpha((int) (255.0f * this.controlsAlpha));
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                    Theme.chat_infoPaint.setAlpha(255);
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
                        progress = 1.0f - (((float) Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date)) / ((float) this.currentMessageObject.messageOwner.media.period));
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
            i2 = AndroidUtilities.dp(205.0f) + x;
            i = AndroidUtilities.dp(22.0f);
            this.otherY = i;
            BaseCell.setDrawableBounds(phone, i2, i);
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
            a = 0;
            int N = this.pollButtons.size();
            while (a < N) {
                PollButton button = (PollButton) this.pollButtons.get(a);
                button.x = x;
                canvas.save();
                canvas.translate((float) (AndroidUtilities.dp(34.0f) + x), (float) (button.y + this.namesOffset));
                button.title.draw(canvas);
                if (this.animatePollAnswerAlpha) {
                    f = Math.min((this.pollUnvoteInProgress ? 1.0f - this.pollAnimationProgress : this.pollAnimationProgress) / 0.3f, 1.0f) * 255.0f;
                } else {
                    f = 255.0f;
                }
                int alpha = (int) f;
                if (this.pollVoted || this.pollClosed || this.animatePollAnswerAlpha) {
                    Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outAudioSeekbarFill" : "chat_inAudioSeekbarFill"));
                    if (this.animatePollAnswerAlpha) {
                        Theme.chat_instantViewPaint.setAlpha((int) (((float) alpha) * (((float) Theme.chat_instantViewPaint.getAlpha()) / 255.0f)));
                        Theme.chat_docBackPaint.setAlpha((int) (((float) alpha) * (((float) Theme.chat_docBackPaint.getAlpha()) / 255.0f)));
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
                        Theme.chat_replyLinePaint.setAlpha((int) (((float) (255 - alpha)) * (((float) Theme.chat_replyLinePaint.getAlpha()) / 255.0f)));
                    }
                    canvas.drawLine((float) (-AndroidUtilities.dp(2.0f)), (float) (button.height + AndroidUtilities.dp(13.0f)), (float) (this.backgroundWidth - AndroidUtilities.dp(56.0f)), (float) (button.height + AndroidUtilities.dp(13.0f)), Theme.chat_replyLinePaint);
                    if (this.pollVoteInProgress && a == this.pollVoteInProgressNum) {
                        Theme.chat_instantViewRectPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? "chat_outAudioSeekbarFill" : "chat_inAudioSeekbarFill"));
                        if (this.animatePollAnswerAlpha) {
                            Theme.chat_instantViewRectPaint.setAlpha((int) (((float) (255 - alpha)) * (((float) Theme.chat_instantViewRectPaint.getAlpha()) / 255.0f)));
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
                            Theme.chat_instantViewRectPaint.setAlpha((int) (((float) (255 - alpha)) * (((float) Theme.chat_instantViewRectPaint.getAlpha()) / 255.0f)));
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
            menuDrawable = this.currentMessageObject.isOutOwner() ? isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable : isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            i2 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(48.0f);
            this.otherX = i2;
            i = this.photoImage.getImageY() - AndroidUtilities.dp(5.0f);
            this.otherY = i;
            BaseCell.setDrawableBounds(menuDrawable, i2, i);
            menuDrawable.draw(canvas);
            if (this.drawInstantView) {
                int textX = this.photoImage.getImageX() - AndroidUtilities.dp(2.0f);
                instantY = getMeasuredHeight() - AndroidUtilities.dp(64.0f);
                backPaint = Theme.chat_instantViewRectPaint;
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
                } else if (this.currentMessageObject.isOutOwner()) {
                    this.radialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
                } else {
                    this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
                }
                if (imageDrawn) {
                    if (this.buttonState == -1 && this.radialProgress.getIcon() != 4) {
                        this.radialProgress.setIcon(4, true, true);
                    }
                    this.radialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
                } else {
                    this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) (this.photoImage.getImageX() + this.photoImage.getImageWidth()), (float) (this.photoImage.getImageY() + this.photoImage.getImageHeight()));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                    if (this.currentMessageObject.isOutOwner()) {
                        radialProgress2 = this.radialProgress;
                        if (isDrawSelectedBackground()) {
                            str = "chat_outFileProgressSelected";
                        } else {
                            str = "chat_outFileProgress";
                        }
                        radialProgress2.setProgressColor(Theme.getColor(str));
                    } else {
                        this.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? "chat_inFileProgressSelected" : "chat_inFileProgress"));
                    }
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
                    if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                        str = "chat_outAudioSelectedProgress";
                    } else {
                        str = "chat_outAudioProgress";
                    }
                    radialProgress2.setProgressColor(Theme.getColor(str));
                } else {
                    radialProgress2 = this.radialProgress;
                    str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress";
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
        if (this.drawImageButton && this.photoImage.getVisible()) {
            if (this.controlsAlpha != 1.0f) {
                this.radialProgress.setOverrideAlpha(this.controlsAlpha);
            }
            this.radialProgress.draw(canvas);
        }
        if (this.buttonState == -1 && this.currentMessageObject.needDrawBluredPreview() && !MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && this.photoImage.getVisible() && this.currentMessageObject.messageOwner.destroyTime != 0) {
            if (!this.currentMessageObject.isOutOwner()) {
                progress = ((float) Math.max(0, (((long) this.currentMessageObject.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(this.currentAccount).getTimeDifference() * 1000))))) / (((float) this.currentMessageObject.messageOwner.ttl) * 1000.0f);
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
                BotButton button2 = (BotButton) this.botButtons.get(a);
                y = (button2.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                Theme.chat_systemDrawable.setColorFilter(a == this.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(button2.x + addX, y, (button2.x + addX) + button2.width, button2.height + y);
                Theme.chat_systemDrawable.draw(canvas);
                canvas.save();
                canvas.translate((float) ((button2.x + addX) + AndroidUtilities.dp(5.0f)), (float) (((AndroidUtilities.dp(44.0f) - button2.title.getLineBottom(button2.title.getLineCount() - 1)) / 2) + y));
                button2.title.draw(canvas);
                canvas.restore();
                if (button2.button instanceof TL_keyboardButtonUrl) {
                    BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((button2.x + button2.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y);
                    Theme.chat_botLinkDrawalbe.draw(canvas);
                } else if (button2.button instanceof TL_keyboardButtonSwitchInline) {
                    BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((button2.x + button2.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y);
                    Theme.chat_botInlineDrawable.draw(canvas);
                } else if ((button2.button instanceof TL_keyboardButtonCallback) || (button2.button instanceof TL_keyboardButtonRequestGeoLocation) || (button2.button instanceof TL_keyboardButtonGame) || (button2.button instanceof TL_keyboardButtonBuy)) {
                    boolean drawProgress = (((button2.button instanceof TL_keyboardButtonCallback) || (button2.button instanceof TL_keyboardButtonGame) || (button2.button instanceof TL_keyboardButtonBuy)) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCallback(this.currentMessageObject, button2.button)) || ((button2.button instanceof TL_keyboardButtonRequestGeoLocation) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCurrentLocation(this.currentMessageObject, button2.button));
                    if (drawProgress || !(drawProgress || button2.progressAlpha == 0.0f)) {
                        Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (button2.progressAlpha * 255.0f)));
                        x = ((button2.x + button2.width) - AndroidUtilities.dp(12.0f)) + addX;
                        this.rect.set((float) x, (float) (AndroidUtilities.dp(4.0f) + y), (float) (AndroidUtilities.dp(8.0f) + x), (float) (AndroidUtilities.dp(12.0f) + y));
                        canvas.drawArc(this.rect, (float) button2.angle, 220.0f, false, Theme.chat_botProgressPaint);
                        invalidate(((int) this.rect.left) - AndroidUtilities.dp(2.0f), ((int) this.rect.top) - AndroidUtilities.dp(2.0f), ((int) this.rect.right) + AndroidUtilities.dp(2.0f), ((int) this.rect.bottom) + AndroidUtilities.dp(2.0f));
                        long newTime = System.currentTimeMillis();
                        if (Math.abs(button2.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                            long delta = newTime - button2.lastUpdateTime;
                            button2.angle = (int) (((float) button2.angle) + (((float) (360 * delta)) / 2000.0f));
                            button2.angle = button2.angle - ((button2.angle / 360) * 360);
                            if (drawProgress) {
                                if (button2.progressAlpha < 1.0f) {
                                    button2.progressAlpha = button2.progressAlpha + (((float) delta) / 200.0f);
                                    if (button2.progressAlpha > 1.0f) {
                                        button2.progressAlpha = 1.0f;
                                    }
                                }
                            } else if (button2.progressAlpha > 0.0f) {
                                button2.progressAlpha = button2.progressAlpha - (((float) delta) / 200.0f);
                                if (button2.progressAlpha < 0.0f) {
                                    button2.progressAlpha = 0.0f;
                                }
                            }
                        }
                        button2.lastUpdateTime = newTime;
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
                if (this.buttonState == 3) {
                    return 0;
                }
                return 4;
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
        if (SharedConfig.streamMedia && ((int) this.currentMessageObject.getDialogId()) != 0 && !this.currentMessageObject.isSecretMedia() && (this.documentAttachType == 5 || (this.documentAttachType == 4 && this.currentMessageObject.canStreamVideo()))) {
            this.hasMiniProgress = fileExists ? 1 : 2;
            fileExists = true;
        }
        if (this.currentMessageObject.isSendError() || !(!TextUtils.isEmpty(fileName) || this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) {
            this.radialProgress.setIcon(4, ifSame, false);
            this.radialProgress.setMiniIcon(4, ifSame, false);
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
            if (!this.currentMessageObject.isOut() || (!this.currentMessageObject.isSending() && !this.currentMessageObject.isEditing())) {
                if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
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
                } else if (fileExists) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                    if (this.currentMessageObject.needDrawBluredPreview()) {
                        this.buttonState = -1;
                    } else if (this.currentMessageObject.type == 8 && !this.photoImage.isAllowStartAnimation()) {
                        this.buttonState = 2;
                    } else if (this.documentAttachType == 4) {
                        this.buttonState = 3;
                    } else {
                        this.buttonState = -1;
                    }
                    this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                    if (!fromSet && this.photoNotSet) {
                        setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
                    }
                    invalidate();
                } else {
                    DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                    if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                        this.buttonState = 1;
                        progress = ImageLoader.getInstance().getFileProgress(fileName);
                        this.radialProgress.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
                        this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
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
                        this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                    }
                    invalidate();
                }
            } else if (TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
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

    private void didPressButton(boolean animated) {
        if (this.buttonState == 0) {
            if (this.documentAttachType == 3 || this.documentAttachType == 5) {
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
            this.radialProgress.setProgress(0.0f, false);
            if (this.currentMessageObject.type == 1) {
                int i;
                this.photoImage.setForceLoading(true);
                ImageReceiver imageReceiver = this.photoImage;
                TLObject tLObject = this.currentPhotoObject;
                String str = this.currentPhotoFilter;
                TLObject tLObject2 = this.currentPhotoObjectThumb;
                String str2 = this.currentPhotoFilterThumb;
                int i2 = this.currentPhotoObject.size;
                MessageObject messageObject = this.currentMessageObject;
                if (this.currentMessageObject.shouldEncryptPhotoOrVideo()) {
                    i = 2;
                } else {
                    i = 0;
                }
                imageReceiver.setImage(tLObject, str, tLObject2, str2, i2, null, messageObject, i);
            } else if (this.currentMessageObject.type == 8) {
                this.currentMessageObject.gifState = 2.0f;
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentMessageObject.messageOwner.media.document, null, this.currentPhotoObjectThumb, this.currentPhotoFilterThumb, this.currentMessageObject.messageOwner.media.document.size, null, this.currentMessageObject, 0);
            } else if (this.currentMessageObject.isRoundVideo()) {
                if (this.currentMessageObject.isSecretMedia()) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 1);
                } else {
                    this.currentMessageObject.gifState = 2.0f;
                    Document document = this.currentMessageObject.getDocument();
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(document, null, this.currentPhotoObjectThumb, this.currentPhotoFilterThumb, document.size, null, this.currentMessageObject, 0);
                }
            } else if (this.currentMessageObject.type == 9) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.messageOwner.media.document, this.currentMessageObject, 0, 0);
            } else if (this.documentAttachType == 4) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
            } else if (this.currentMessageObject.type != 0 || this.documentAttachType == 0) {
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentPhotoObject, this.currentPhotoFilter, this.currentPhotoObjectThumb, this.currentPhotoFilterThumb, 0, null, this.currentMessageObject, 0);
            } else if (this.documentAttachType == 2) {
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentMessageObject.messageOwner.media.webpage.document, null, this.currentPhotoObject, this.currentPhotoFilterThumb, this.currentMessageObject.messageOwner.media.webpage.document.size, null, this.currentMessageObject, 0);
                this.currentMessageObject.gifState = 2.0f;
            } else if (this.documentAttachType == 1) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.messageOwner.media.webpage.document, this.currentMessageObject, 0, 0);
            } else if (this.documentAttachType == 8) {
                this.photoImage.setImage(this.documentAttach, this.currentPhotoFilter, null, this.currentPhotoObject, "b1", 0, "jpg", this.currentMessageObject, 1);
            }
            this.buttonState = 1;
            this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            invalidate();
        } else if (this.buttonState == 1) {
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
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
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
            this.photoImage.setAllowStartAnimation(true);
            this.photoImage.startAnimation();
            this.currentMessageObject.gifState = 0.0f;
            this.buttonState = -1;
            this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
        } else if (this.buttonState == 3) {
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
        this.radialProgress.setProgress(1.0f, true);
        if (this.currentMessageObject.type != 0) {
            if (!this.photoNotSet || ((this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) && this.currentMessageObject.gifState != 1.0f)) {
                if ((this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) && this.currentMessageObject.gifState != 1.0f) {
                    this.photoNotSet = false;
                    this.buttonState = 2;
                    didPressButton(true);
                } else {
                    updateButtonState(false, true, false);
                }
            }
            if (this.photoNotSet) {
                setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
            }
        } else if (this.documentAttachType == 2 && this.currentMessageObject.gifState != 1.0f) {
            this.buttonState = 2;
            didPressButton(true);
        } else if (this.photoNotSet) {
            setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
        } else {
            updateButtonState(false, true, false);
        }
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        if (this.currentMessageObject == null) {
            return;
        }
        if ((this.currentMessageObject.type == 0 || this.currentMessageObject.type == 1 || this.currentMessageObject.type == 5 || this.currentMessageObject.type == 8) && set && !thumb && !this.currentMessageObject.mediaExists && !this.currentMessageObject.attachPathExists) {
            this.currentMessageObject.mediaExists = true;
            updateButtonState(false, true, false);
        }
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, true);
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
                if ((this.drawTime || !this.mediaBackground) && !this.forceNotDrawTime) {
                    drawTimeLayout(canvas);
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

    public void drawTimeLayout(Canvas canvas) {
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

    public int getObserverTag() {
        return this.TAG;
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
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
