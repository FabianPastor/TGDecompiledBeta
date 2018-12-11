package org.telegram.p005ui.Cells;

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
import android.text.StaticLayout.Builder;
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
import com.google.android.exoplayer2.CLASSNAMEC;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import net.hockeyapp.android.UpdateFragment;
import org.telegram.PhoneFormat.CLASSNAMEPhoneFormat;
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
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.CombinedDrawable;
import org.telegram.p005ui.Components.LinkPath;
import org.telegram.p005ui.Components.RadialProgress;
import org.telegram.p005ui.Components.RoundVideoPlayingDrawable;
import org.telegram.p005ui.Components.SeekBar;
import org.telegram.p005ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.p005ui.Components.SeekBarWaveform;
import org.telegram.p005ui.Components.StaticLayoutEx;
import org.telegram.p005ui.Components.TypefaceSpan;
import org.telegram.p005ui.Components.URLSpanBotCommand;
import org.telegram.p005ui.Components.URLSpanMono;
import org.telegram.p005ui.Components.URLSpanNoUnderline;
import org.telegram.p005ui.PhotoViewer;
import org.telegram.p005ui.SecretMediaViewer;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.PhoneCallDiscardReason;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_page;
import org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
import org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;

/* renamed from: org.telegram.ui.Cells.ChatMessageCell */
public class ChatMessageCell extends BaseCell implements FileDownloadProgressListener, ImageReceiverDelegate, SeekBarDelegate {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_ROUND = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private int TAG;
    private int addedCaptionHeight;
    private boolean addedForTest;
    private StaticLayout adminLayout;
    private boolean allowAssistant;
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
    private FileLocation currentReplyPhoto;
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
    private boolean hasGamePreview;
    private boolean hasInvoicePreview;
    private boolean hasLinkPreview;
    private int hasMiniProgress;
    private boolean hasNewLineForTime;
    private boolean hasOldCaptionPreview;
    private int highlightProgress;
    private boolean imagePressed;
    private boolean inLayout;
    private StaticLayout infoLayout;
    private int infoWidth;
    private boolean instantButtonPressed;
    private RectF instantButtonRect = new RectF();
    private boolean instantPressed;
    private int instantTextLeftX;
    private int instantTextX;
    private StaticLayout instantViewLayout;
    private Drawable instantViewSelectorDrawable;
    private int instantWidth;
    private Runnable invalidateRunnable = new CLASSNAME();
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
    private int pressedBotButton;
    private CharacterStyle pressedLink;
    private int pressedLinkType;
    private int[] pressedState = new int[]{16842910, 16842919};
    private RadialProgress radialProgress;
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
    private boolean wasLayout;
    private int widthBeforeNewTimeLine;
    private int widthForButtons;

    /* renamed from: org.telegram.ui.Cells.ChatMessageCell$1 */
    class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.Cells.ChatMessageCell$BotButton */
    private class BotButton {
        private int angle;
        private KeyboardButton button;
        private int height;
        private long lastUpdateTime;
        private float progressAlpha;
        private StaticLayout title;
        private int width;
        /* renamed from: x */
        private int var_x;
        /* renamed from: y */
        private int var_y;

        private BotButton() {
        }

        /* synthetic */ BotButton(ChatMessageCell x0, CLASSNAME x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate */
    public interface ChatMessageCellDelegate {
        boolean canPerformActions();

        void didLongPressed(ChatMessageCell chatMessageCell);

        void didPressedBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton);

        void didPressedCancelSendButton(ChatMessageCell chatMessageCell);

        void didPressedChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i);

        void didPressedImage(ChatMessageCell chatMessageCell);

        void didPressedInstantButton(ChatMessageCell chatMessageCell, int i);

        void didPressedOther(ChatMessageCell chatMessageCell);

        void didPressedReplyMessage(ChatMessageCell chatMessageCell, int i);

        void didPressedShare(ChatMessageCell chatMessageCell);

        void didPressedUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z);

        void didPressedUserAvatar(ChatMessageCell chatMessageCell, User user);

        void didPressedViaBot(ChatMessageCell chatMessageCell, String str);

        boolean isChatAdminCell(int i);

        void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2);

        boolean needPlayMessage(MessageObject messageObject);
    }

    public ChatMessageCell(Context context) {
        super(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.m9dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.replyImageReceiver = new ImageReceiver(this);
        this.locationImageReceiver = new ImageReceiver(this);
        this.locationImageReceiver.setRoundRadius(AndroidUtilities.m9dp(26.1f));
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        this.contactAvatarDrawable = new AvatarDrawable();
        this.photoImage = new ImageReceiver(this);
        this.photoImage.setDelegate(this);
        this.radialProgress = new RadialProgress(this);
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
                                    FileLog.m13e(e);
                                }
                                invalidate();
                                return true;
                            }
                            if (link[0] == this.pressedLink) {
                                this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
                                resetPressedLink(1);
                                return true;
                            }
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
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
                                FileLog.m13e(e);
                            }
                            if (!(this.currentMessagesGroup == null || getParent() == null)) {
                                ((ViewGroup) getParent()).invalidate();
                            }
                            invalidate();
                            return true;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
                }
            } else if (this.pressedLinkType == 3) {
                this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
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
                    x -= (this.unmovedTextX + AndroidUtilities.m9dp(10.0f)) + this.descriptionX;
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
                                FileLog.m13e(e);
                            }
                            invalidate();
                            return true;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
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
                        this.delegate.didPressedBotButton(this, button.button);
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
            if (y <= AndroidUtilities.m9dp((float) ((this.drawInstantView ? 46 : 0) + 8)) + (this.linkPreviewHeight + (this.textY + this.currentMessageObject.textHeight))) {
                WebPage webPage;
                if (event.getAction() == 0) {
                    if (this.descriptionLayout != null && y >= this.descriptionY) {
                        try {
                            int checkX = x - ((this.unmovedTextX + AndroidUtilities.m9dp(10.0f)) + this.descriptionX);
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
                                            FileLog.m13e(e);
                                        }
                                        invalidate();
                                        return true;
                                    }
                                }
                            }
                        } catch (Throwable e2) {
                            FileLog.m13e(e2);
                        }
                    }
                    if (this.pressedLink == null) {
                        int side = AndroidUtilities.m9dp(48.0f);
                        boolean area2 = false;
                        if (this.miniButtonState >= 0) {
                            int offset = AndroidUtilities.m9dp(27.0f);
                            area2 = x >= this.buttonX + offset && x <= (this.buttonX + offset) + side && y >= this.buttonY + offset && y <= (this.buttonY + offset) + side;
                        }
                        if (area2) {
                            this.miniButtonPressed = 1;
                            invalidate();
                            return true;
                        } else if (this.drawPhotoImage && this.drawImageButton && this.buttonState != -1 && (this.photoImage.isInsideImage((float) x, (float) y) || (x >= this.buttonX && x <= this.buttonX + AndroidUtilities.m9dp(48.0f) && y >= this.buttonY && y <= this.buttonY + AndroidUtilities.m9dp(48.0f)))) {
                            this.buttonPressed = 1;
                            return true;
                        } else if (this.drawInstantView) {
                            this.instantPressed = true;
                            if (VERSION.SDK_INT >= 21 && this.instantViewSelectorDrawable != null && this.instantViewSelectorDrawable.getBounds().contains(x, y)) {
                                this.instantViewSelectorDrawable.setState(this.pressedState);
                                this.instantViewSelectorDrawable.setHotspot((float) x, (float) y);
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
                            this.delegate.didPressedInstantButton(this, this.drawInstantViewType);
                        }
                        playSoundEffect(0);
                        if (VERSION.SDK_INT >= 21 && this.instantViewSelectorDrawable != null) {
                            this.instantViewSelectorDrawable.setState(StateSet.NOTHING);
                        }
                        this.instantButtonPressed = false;
                        this.instantPressed = false;
                        invalidate();
                    } else if (this.pressedLinkType != 2 && this.buttonPressed == 0 && this.miniButtonPressed == 0 && !this.linkPreviewPressed) {
                        resetPressedLink(2);
                    } else if (this.buttonPressed != 0) {
                        this.buttonPressed = 0;
                        playSoundEffect(0);
                        didPressedButton(false);
                        invalidate();
                    } else if (this.miniButtonPressed != 0) {
                        this.miniButtonPressed = 0;
                        playSoundEffect(0);
                        didPressedMiniButton(false);
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
                                MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.currentMessageObject);
                            }
                        } else if (this.documentAttachType != 2 || !this.drawImageButton) {
                            webPage = this.currentMessageObject.messageOwner.media.webpage;
                            if (webPage != null && !TextUtils.isEmpty(webPage.embed_url)) {
                                this.delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.title, webPage.url, webPage.embed_width, webPage.embed_height);
                            } else if (this.buttonState == -1 || this.buttonState == 3) {
                                this.delegate.didPressedImage(this);
                                playSoundEffect(0);
                            } else if (webPage != null) {
                                Browser.openUrl(getContext(), webPage.url);
                            }
                        } else if (this.buttonState == -1) {
                            if (SharedConfig.autoplayGifs) {
                                this.delegate.didPressedImage(this);
                            } else {
                                this.buttonState = 2;
                                this.currentMessageObject.gifState = 1.0f;
                                this.photoImage.setAllowStartAnimation(false);
                                this.photoImage.stopAnimation();
                                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                                invalidate();
                                playSoundEffect(0);
                            }
                        } else if (this.buttonState == 2 || this.buttonState == 0) {
                            didPressedButton(false);
                            playSoundEffect(0);
                        }
                        resetPressedLink(2);
                        return true;
                    }
                } else if (event.getAction() == 2 && this.instantButtonPressed && VERSION.SDK_INT >= 21 && this.instantViewSelectorDrawable != null) {
                    this.instantViewSelectorDrawable.setHotspot((float) x, (float) y);
                }
            }
        }
        return false;
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
                if (VERSION.SDK_INT >= 21 && this.instantViewSelectorDrawable != null && this.instantViewSelectorDrawable.getBounds().contains(x, y)) {
                    this.instantViewSelectorDrawable.setState(this.pressedState);
                    this.instantViewSelectorDrawable.setHotspot((float) x, (float) y);
                    this.instantButtonPressed = true;
                }
                invalidate();
                return true;
            }
        } else if (event.getAction() == 1) {
            if (this.instantPressed) {
                if (this.delegate != null) {
                    this.delegate.didPressedInstantButton(this, this.drawInstantViewType);
                }
                playSoundEffect(0);
                if (VERSION.SDK_INT >= 21 && this.instantViewSelectorDrawable != null) {
                    this.instantViewSelectorDrawable.setState(StateSet.NOTHING);
                }
                this.instantButtonPressed = false;
                this.instantPressed = false;
                invalidate();
            }
        } else if (event.getAction() == 2 && this.instantButtonPressed && VERSION.SDK_INT >= 21 && this.instantViewSelectorDrawable != null) {
            this.instantViewSelectorDrawable.setHotspot((float) x, (float) y);
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
                if (x >= this.otherX && x <= this.otherX + AndroidUtilities.m9dp(235.0f) && y >= this.otherY - AndroidUtilities.m9dp(14.0f) && y <= this.otherY + AndroidUtilities.m9dp(50.0f)) {
                    this.otherPressed = true;
                    result = true;
                    invalidate();
                }
            } else if (x >= this.otherX - AndroidUtilities.m9dp(20.0f) && x <= this.otherX + AndroidUtilities.m9dp(20.0f) && y >= this.otherY - AndroidUtilities.m9dp(4.0f) && y <= this.otherY + AndroidUtilities.m9dp(30.0f)) {
                this.otherPressed = true;
                result = true;
                invalidate();
            }
        } else if (event.getAction() == 1 && this.otherPressed) {
            this.otherPressed = false;
            playSoundEffect(0);
            this.delegate.didPressedOther(this);
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
            int side = AndroidUtilities.m9dp(48.0f);
            if (this.miniButtonState >= 0) {
                int offset = AndroidUtilities.m9dp(27.0f);
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
                if (x >= this.photoImage.getImageX() && x <= (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.m9dp(50.0f) && y >= this.photoImage.getImageY() && y <= this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                    this.imagePressed = true;
                    result = true;
                }
            } else if (!(this.currentMessageObject.type == 13 && this.currentMessageObject.getInputStickerSet() == null)) {
                if (x >= this.photoImage.getImageX() && x <= this.photoImage.getImageX() + this.backgroundWidth && y >= this.photoImage.getImageY() && y <= this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
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
                didPressedButton(false);
                updateRadialProgressBackground();
                invalidate();
            } else if (this.miniButtonPressed == 1) {
                this.miniButtonPressed = 0;
                playSoundEffect(0);
                didPressedMiniButton(false);
                invalidate();
            } else if (this.imagePressed) {
                this.imagePressed = false;
                if (this.buttonState == -1 || this.buttonState == 2 || this.buttonState == 3) {
                    playSoundEffect(0);
                    didClickedImage();
                } else if (this.buttonState == 0 && this.documentAttachType == 1) {
                    playSoundEffect(0);
                    didPressedButton(false);
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
            result = this.seekBarWaveform.onTouch(event.getAction(), (event.getX() - ((float) this.seekBarX)) - ((float) AndroidUtilities.m9dp(13.0f)), event.getY() - ((float) this.seekBarY));
        } else {
            result = this.seekBar.onTouch(event.getAction(), event.getX() - ((float) this.seekBarX), event.getY() - ((float) this.seekBarY));
        }
        if (result) {
            if (!this.useSeekBarWaweform && event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (this.useSeekBarWaweform && !this.seekBarWaveform.isStartDraging() && event.getAction() == 1) {
                didPressedButton(true);
            }
            this.disallowLongPress = true;
            invalidate();
            return result;
        }
        int side = AndroidUtilities.m9dp(36.0f);
        boolean area = false;
        boolean area2 = false;
        if (this.miniButtonState >= 0) {
            int offset = AndroidUtilities.m9dp(27.0f);
            area2 = x >= this.buttonX + offset && x <= (this.buttonX + offset) + side && y >= this.buttonY + offset && y <= (this.buttonY + offset) + side;
        }
        if (!area2) {
            if (this.buttonState == 0 || this.buttonState == 1 || this.buttonState == 2) {
                if (x >= this.buttonX - AndroidUtilities.m9dp(12.0f) && x <= (this.buttonX - AndroidUtilities.m9dp(12.0f)) + this.backgroundWidth) {
                    if (y >= (this.drawInstantView ? this.buttonY : this.namesOffset + this.mediaOffsetY)) {
                        if (y <= (this.drawInstantView ? this.buttonY + side : (this.namesOffset + this.mediaOffsetY) + AndroidUtilities.m9dp(82.0f))) {
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
            updateRadialProgressBackground();
            return true;
        } else if (this.buttonPressed != 0) {
            if (event.getAction() == 1) {
                this.buttonPressed = 0;
                playSoundEffect(0);
                didPressedButton(true);
                invalidate();
            } else if (event.getAction() == 3) {
                this.buttonPressed = 0;
                invalidate();
            } else if (event.getAction() == 2 && !area) {
                this.buttonPressed = 0;
                invalidate();
            }
            updateRadialProgressBackground();
            return result;
        } else if (this.miniButtonPressed == 0) {
            return result;
        } else {
            if (event.getAction() == 1) {
                this.miniButtonPressed = 0;
                playSoundEffect(0);
                didPressedMiniButton(true);
                invalidate();
            } else if (event.getAction() == 3) {
                this.miniButtonPressed = 0;
                invalidate();
            } else if (event.getAction() == 2 && !area2) {
                this.miniButtonPressed = 0;
                invalidate();
            }
            updateRadialProgressBackground();
            return result;
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
                addX = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.m9dp(10.0f);
            } else {
                addX = this.backgroundDrawableLeft + AndroidUtilities.m9dp(this.mediaBackground ? 1.0f : 7.0f);
            }
            int a = 0;
            while (a < this.botButtons.size()) {
                BotButton button = (BotButton) this.botButtons.get(a);
                int y2 = (button.var_y + this.layoutHeight) - AndroidUtilities.m9dp(2.0f);
                if (x < button.var_x + addX || x > (button.var_x + addX) + button.width || y < y2 || y > button.height + y2) {
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
            this.delegate.didPressedBotButton(this, ((BotButton) this.botButtons.get(this.pressedBotButton)).button);
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
        if (event.getAction() == 3) {
            this.buttonPressed = 0;
            this.miniButtonPressed = 0;
            this.pressedBotButton = -1;
            this.linkPreviewPressed = false;
            this.otherPressed = false;
            this.imagePressed = false;
            this.gamePreviewPressed = false;
            this.instantButtonPressed = false;
            this.instantPressed = false;
            if (VERSION.SDK_INT >= 21 && this.instantViewSelectorDrawable != null) {
                this.instantViewSelectorDrawable.setState(StateSet.NOTHING);
            }
            result = false;
            resetPressedLink(-1);
        }
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
                        this.delegate.didPressedUserAvatar(this, this.currentUser);
                        return result;
                    } else if (this.currentChat == null) {
                        return result;
                    } else {
                        this.delegate.didPressedChannelAvatar(this, this.currentChat, 0);
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
                        this.delegate.didPressedChannelAvatar(this, this.currentForwardChannel, this.currentMessageObject.messageOwner.fwd_from.channel_post);
                        return result;
                    } else if (this.currentForwardUser == null) {
                        return result;
                    } else {
                        this.delegate.didPressedUserAvatar(this, this.currentForwardUser);
                        return result;
                    }
                } else if (event.getAction() == 3) {
                    this.forwardNamePressed = false;
                    return result;
                } else if (event.getAction() != 2) {
                    return result;
                } else {
                    if (x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.m9dp(32.0f)))) {
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
                    this.delegate.didPressedViaBot(this, this.currentViaBotUser != null ? this.currentViaBotUser.username : this.currentMessageObject.messageOwner.via_bot_name);
                    return result;
                } else if (event.getAction() == 3) {
                    this.forwardBotPressed = false;
                    return result;
                } else if (event.getAction() != 2) {
                    return result;
                } else {
                    if (!this.drawForwardedName || this.forwardedNameLayout[0] == null) {
                        if (x >= this.nameX + ((float) this.viaNameWidth) && x <= (this.nameX + ((float) this.viaNameWidth)) + ((float) this.viaWidth) && y >= this.nameY - ((float) AndroidUtilities.m9dp(4.0f)) && y <= this.nameY + ((float) AndroidUtilities.m9dp(20.0f))) {
                            return result;
                        }
                        this.forwardBotPressed = false;
                        return result;
                    } else if (x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.m9dp(32.0f)))) {
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
                    this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
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
                    if (x >= ((float) this.replyStartX) && x <= ((float) replyEnd) && y >= ((float) this.replyStartY) && y <= ((float) (this.replyStartY + AndroidUtilities.m9dp(35.0f)))) {
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
                        this.delegate.didPressedShare(this);
                    }
                } else if (event.getAction() == 3) {
                    this.sharePressed = false;
                } else if (event.getAction() == 2 && (x < ((float) this.shareStartX) || x > ((float) (this.shareStartX + AndroidUtilities.m9dp(40.0f))) || y < ((float) this.shareStartY) || y > ((float) (this.shareStartY + AndroidUtilities.m9dp(32.0f))))) {
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
            } else if (this.drawForwardedName && this.forwardedNameLayout[0] != null && x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.m9dp(32.0f)))) {
                if (this.viaWidth == 0 || x < ((float) ((this.forwardNameX + this.viaNameWidth) + AndroidUtilities.m9dp(4.0f)))) {
                    this.forwardNamePressed = true;
                } else {
                    this.forwardBotPressed = true;
                }
                result = true;
            } else if (this.drawNameLayout && this.nameLayout != null && this.viaWidth != 0 && x >= this.nameX + ((float) this.viaNameWidth) && x <= (this.nameX + ((float) this.viaNameWidth)) + ((float) this.viaWidth) && y >= this.nameY - ((float) AndroidUtilities.m9dp(4.0f)) && y <= this.nameY + ((float) AndroidUtilities.m9dp(20.0f))) {
                this.forwardBotPressed = true;
                result = true;
            } else if (this.drawShareButton && x >= ((float) this.shareStartX) && x <= ((float) (this.shareStartX + AndroidUtilities.m9dp(40.0f))) && y >= ((float) this.shareStartY) && y <= ((float) (this.shareStartY + AndroidUtilities.m9dp(32.0f)))) {
                this.sharePressed = true;
                result = true;
                invalidate();
            } else if (this.replyNameLayout != null) {
                if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                    replyEnd = this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth);
                } else {
                    replyEnd = this.replyStartX + this.backgroundDrawableRight;
                }
                if (x >= ((float) this.replyStartX) && x <= ((float) replyEnd) && y >= ((float) this.replyStartY) && y <= ((float) (this.replyStartY + AndroidUtilities.m9dp(35.0f)))) {
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
                        updateButtonState(true, false);
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
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
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
        return StaticLayoutEx.createStaticLayout(stringBuilder, paint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.m9dp(1.0f), false, TruncateAt.END, maxWidth, maxLines);
    }

    private void didClickedImage() {
        if (this.currentMessageObject.type == 1 || this.currentMessageObject.type == 13) {
            if (this.buttonState == -1) {
                this.delegate.didPressedImage(this);
            } else if (this.buttonState == 0) {
                didPressedButton(false);
            }
        } else if (this.currentMessageObject.type == 12) {
            this.delegate.didPressedUserAvatar(this, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)));
        } else if (this.currentMessageObject.type == 5) {
            if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isMessagePaused()) {
                this.delegate.needPlayMessage(this.currentMessageObject);
            } else {
                MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.currentMessageObject);
            }
        } else if (this.currentMessageObject.type == 8) {
            if (this.buttonState == -1) {
                if (SharedConfig.autoplayGifs) {
                    this.delegate.didPressedImage(this);
                    return;
                }
                this.buttonState = 2;
                this.currentMessageObject.gifState = 1.0f;
                this.photoImage.setAllowStartAnimation(false);
                this.photoImage.stopAnimation();
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            } else if (this.buttonState == 2 || this.buttonState == 0) {
                didPressedButton(false);
            }
        } else if (this.documentAttachType == 4) {
            if (this.buttonState == -1) {
                this.delegate.didPressedImage(this);
            } else if (this.buttonState == 0 || this.buttonState == 3) {
                didPressedButton(false);
            }
        } else if (this.currentMessageObject.type == 4) {
            this.delegate.didPressedImage(this);
        } else if (this.documentAttachType == 1) {
            if (this.buttonState == -1) {
                this.delegate.didPressedImage(this);
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
            this.delegate.didPressedImage(this);
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
                url = AndroidUtilities.formapMapUrl(this.currentAccount, ((1.5707963267948966d - (2.0d * Math.atan(Math.exp((((double) (Math.round(((double) NUM) - ((Math.log((1.0d + Math.sin((3.141592653589793d * lat) / 180.0d)) / (1.0d - Math.sin((3.141592653589793d * lat) / 180.0d))) * rad) / 2.0d)) - ((long) (AndroidUtilities.m9dp(10.3f) << 6)))) - ((double) NUM)) / rad)))) * 180.0d) / 3.141592653589793d, lon, (int) (((float) (this.backgroundWidth - AndroidUtilities.m9dp(21.0f))) / AndroidUtilities.density), (int) (((float) AndroidUtilities.m9dp(195.0f)) / AndroidUtilities.density), false, 15);
            } else if (TextUtils.isEmpty(object.messageOwner.media.title)) {
                url = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, (int) (((float) (this.backgroundWidth - AndroidUtilities.m9dp(12.0f))) / AndroidUtilities.density), (int) (((float) AndroidUtilities.m9dp(195.0f)) / AndroidUtilities.density), true, 15);
            } else {
                url = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, (int) (((float) (this.backgroundWidth - AndroidUtilities.m9dp(21.0f))) / AndroidUtilities.density), (int) (((float) AndroidUtilities.m9dp(195.0f)) / AndroidUtilities.density), true, 15);
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
        FileLocation newReplyPhoto = null;
        if (this.replyNameLayout != null) {
            PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
            if (!(photoSize == null || this.currentMessageObject.replyMessageObject.type == 13)) {
                newReplyPhoto = photoSize.location;
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
        if (!this.drawForwardedName) {
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
        setTranslationX(0.0f);
        this.avatarImage.onAttachedToWindow();
        this.avatarImage.setParentView((View) getParent());
        this.replyImageReceiver.onAttachedToWindow();
        this.locationImageReceiver.onAttachedToWindow();
        if (!this.photoImage.onAttachedToWindow()) {
            updateButtonState(false, false);
        } else if (this.drawPhotoImage) {
            updateButtonState(false, false);
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
            this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
            return;
        }
        if (this.pressedLink instanceof URLSpanNoUnderline) {
            if (this.pressedLink.getURL().startsWith("/")) {
                this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
                return;
            }
        } else if (this.pressedLink instanceof URLSpan) {
            this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
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
            if (VERSION.SDK_INT >= 21 && this.instantViewSelectorDrawable != null) {
                this.instantViewSelectorDrawable.setState(StateSet.NOTHING);
            }
            invalidate();
        }
        if (this.delegate != null) {
            this.delegate.didLongPressed(this);
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
        if (!this.drawRadialCheckBackground) {
            this.radialProgress.swapBackground(getDrawableForCurrentState());
            if (this.hasMiniProgress != 0) {
                this.radialProgress.swapMiniBackground(getMiniDrawableForCurrentState());
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
            this.widthBeforeNewTimeLine = (maxWidth - AndroidUtilities.m9dp(94.0f)) - ((int) Math.ceil((double) Theme.chat_audioTimePaint.measureText("00:00")));
            this.availableTimeWidth = maxWidth - AndroidUtilities.m9dp(18.0f);
            measureTime(messageObject);
            int minSize = AndroidUtilities.m9dp(174.0f) + this.timeWidth;
            if (!this.hasLinkPreview) {
                this.backgroundWidth = Math.min(maxWidth, (AndroidUtilities.m9dp(10.0f) * duration) + minSize);
            }
            this.seekBarWaveform.setMessageObject(messageObject);
            return 0;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
            maxWidth -= AndroidUtilities.m9dp(86.0f);
            if (maxWidth < 0) {
                maxWidth = AndroidUtilities.m9dp(100.0f);
            }
            this.songLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicTitle().replace(10, ' '), Theme.chat_audioTitlePaint, (float) (maxWidth - AndroidUtilities.m9dp(12.0f)), TruncateAt.END), Theme.chat_audioTitlePaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
            this.widthBeforeNewTimeLine = (this.backgroundWidth - AndroidUtilities.m9dp(86.0f)) - durationWidth;
            this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.m9dp(28.0f);
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
            boolean z = (this.documentAttach.mime_type != null && this.documentAttach.mime_type.toLowerCase().startsWith("image/")) || !(this.documentAttach.thumb == null || (this.documentAttach.thumb instanceof TL_photoSizeEmpty) || (this.documentAttach.thumb.location instanceof TL_fileLocationUnavailable));
            this.drawPhotoImage = z;
            if (!this.drawPhotoImage) {
                maxWidth += AndroidUtilities.m9dp(30.0f);
            }
            this.documentAttachType = 1;
            String name = FileLoader.getDocumentFileName(this.documentAttach);
            if (name == null || name.length() == 0) {
                name = LocaleController.getString("AttachDocument", R.string.AttachDocument);
            }
            this.docTitleLayout = StaticLayoutEx.createStaticLayout(name, Theme.chat_docNamePaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TruncateAt.MIDDLE, maxWidth, this.drawPhotoImage ? 2 : 1);
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
            this.infoWidth = Math.min(maxWidth - AndroidUtilities.m9dp(30.0f), (int) Math.ceil((double) Theme.chat_infoPaint.measureText(str)));
            CharSequence str2 = TextUtils.ellipsize(str, Theme.chat_infoPaint, (float) this.infoWidth, TruncateAt.END);
            try {
                if (this.infoWidth < 0) {
                    this.infoWidth = AndroidUtilities.m9dp(10.0f);
                }
                this.infoLayout = new StaticLayout(str2, Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
            if (this.drawPhotoImage) {
                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                this.photoImage.setNeedsQualityThumb(true);
                this.photoImage.setShouldGenerateQualityThumb(true);
                if (this.currentPhotoObject != null) {
                    this.currentPhotoFilter = "86_86_b";
                    this.photoImage.setImage(null, null, null, null, this.currentPhotoObject.location, this.currentPhotoFilter, 0, null, messageObject, 1);
                } else {
                    this.photoImage.setImageBitmap((BitmapDrawable) null);
                }
            }
            return width;
        }
    }

    private void calcBackgroundWidth(int maxWidth, int timeMore, int maxChildWidth) {
        if (this.hasLinkPreview || this.hasOldCaptionPreview || this.hasGamePreview || this.hasInvoicePreview || maxWidth - this.currentMessageObject.lastLineWidth < timeMore || this.currentMessageObject.hasRtl) {
            this.totalHeight += AndroidUtilities.m9dp(14.0f);
            this.hasNewLineForTime = true;
            this.backgroundWidth = Math.max(maxChildWidth, this.currentMessageObject.lastLineWidth) + AndroidUtilities.m9dp(31.0f);
            this.backgroundWidth = Math.max(this.backgroundWidth, (this.currentMessageObject.isOutOwner() ? this.timeWidth + AndroidUtilities.m9dp(17.0f) : this.timeWidth) + AndroidUtilities.m9dp(31.0f));
            return;
        }
        int diff = maxChildWidth - this.currentMessageObject.lastLineWidth;
        if (diff < 0 || diff > timeMore) {
            this.backgroundWidth = Math.max(maxChildWidth, this.currentMessageObject.lastLineWidth + timeMore) + AndroidUtilities.m9dp(31.0f);
        } else {
            this.backgroundWidth = ((maxChildWidth + timeMore) - diff) + AndroidUtilities.m9dp(31.0f);
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
                            FileLog.m13e(e);
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
        return super.verifyDrawable(who) || who == this.instantViewSelectorDrawable;
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
                int maxWidth = this.backgroundWidth - AndroidUtilities.m9dp(91.0f);
                this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation), Theme.chat_locationTitlePaint, (float) maxWidth, TruncateAt.END), Theme.chat_locationTitlePaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:217:0x069f  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x06bd  */
    /* JADX WARNING: Removed duplicated region for block: B:759:0x18b8  */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:760:0x18bb  */
    /* JADX WARNING: Removed duplicated region for block: B:754:0x188c  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x18ad  */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0e2e  */
    /* JADX WARNING: Removed duplicated region for block: B:768:0x18e4  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0e53  */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0e6a  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x18fa  */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0e80  */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x1998  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0ea7  */
    /* JADX WARNING: Removed duplicated region for block: B:792:0x19b5  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x0f5f  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1be3  */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x0c4a  */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x13f1  */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x0db2  */
    /* JADX WARNING: Removed duplicated region for block: B:744:0x1864  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0dba  */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0df4  */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0e2e  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0e53  */
    /* JADX WARNING: Removed duplicated region for block: B:768:0x18e4  */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0e6a  */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0e80  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x18fa  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0e96  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0ea7  */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x1998  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:792:0x19b5  */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x0f5f  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1be3  */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0fbd  */
    /* JADX WARNING: Removed duplicated region for block: B:1732:0x3a1a  */
    /* JADX WARNING: Removed duplicated region for block: B:574:0x127b A:{Catch:{ Exception -> 0x3a1d }} */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x12b1 A:{Catch:{ Exception -> 0x3a34 }} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x131b A:{Catch:{ Exception -> 0x3a34 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1741:0x3a49  */
    /* JADX WARNING: Removed duplicated region for block: B:1746:0x3a75  */
    /* JADX WARNING: Removed duplicated region for block: B:1832:0x3db1  */
    /* JADX WARNING: Removed duplicated region for block: B:1831:0x3da1  */
    /* JADX WARNING: Removed duplicated region for block: B:1820:0x3d73  */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x0c4a  */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x0db2  */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x13f1  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0dba  */
    /* JADX WARNING: Removed duplicated region for block: B:744:0x1864  */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0df0 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0df4  */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0e2e  */
    /* JADX WARNING: Removed duplicated region for block: B:768:0x18e4  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0e53  */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0e6a  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x18fa  */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0e80  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0e96  */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x1998  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0ea7  */
    /* JADX WARNING: Removed duplicated region for block: B:792:0x19b5  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x0f5f  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1be3  */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0fbd  */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x10f0  */
    /* JADX WARNING: Removed duplicated region for block: B:574:0x127b A:{Catch:{ Exception -> 0x3a1d }} */
    /* JADX WARNING: Removed duplicated region for block: B:1732:0x3a1a  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x12b1 A:{Catch:{ Exception -> 0x3a34 }} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x131b A:{Catch:{ Exception -> 0x3a34 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1741:0x3a49  */
    /* JADX WARNING: Removed duplicated region for block: B:1746:0x3a75  */
    /* JADX WARNING: Removed duplicated region for block: B:1749:0x3a8e  */
    /* JADX WARNING: Removed duplicated region for block: B:1810:0x3d3a  */
    /* JADX WARNING: Removed duplicated region for block: B:1831:0x3da1  */
    /* JADX WARNING: Removed duplicated region for block: B:1832:0x3db1  */
    /* JADX WARNING: Removed duplicated region for block: B:1820:0x3d73  */
    /* JADX WARNING: Removed duplicated region for block: B:1823:0x3d82  */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x0c4a  */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x13f1  */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x0db2  */
    /* JADX WARNING: Removed duplicated region for block: B:744:0x1864  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0dba  */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0df0 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0df4  */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0e2e  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0e53  */
    /* JADX WARNING: Removed duplicated region for block: B:768:0x18e4  */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0e6a  */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x18f7  */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0e80  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x18fa  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0e96  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0ea7  */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x1998  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:792:0x19b5  */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x0f5f  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x1be3  */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0fbd  */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x10f0  */
    /* JADX WARNING: Removed duplicated region for block: B:1732:0x3a1a  */
    /* JADX WARNING: Removed duplicated region for block: B:574:0x127b A:{Catch:{ Exception -> 0x3a1d }} */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x12b1 A:{Catch:{ Exception -> 0x3a34 }} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x131b A:{Catch:{ Exception -> 0x3a34 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1741:0x3a49  */
    /* JADX WARNING: Removed duplicated region for block: B:1746:0x3a75  */
    /* JADX WARNING: Removed duplicated region for block: B:1749:0x3a8e  */
    /* JADX WARNING: Removed duplicated region for block: B:1810:0x3d3a  */
    /* JADX WARNING: Removed duplicated region for block: B:1832:0x3db1  */
    /* JADX WARNING: Removed duplicated region for block: B:1831:0x3da1  */
    /* JADX WARNING: Removed duplicated region for block: B:1820:0x3d73  */
    /* JADX WARNING: Removed duplicated region for block: B:1823:0x3d82  */
    /* JADX WARNING: Missing block: B:207:0x0682, code:
            if (r146.equals("article") != false) goto L_0x0684;
     */
    /* JADX WARNING: Missing block: B:220:0x06b3, code:
            if (r146.equals("article") != false) goto L_0x06b5;
     */
    /* JADX WARNING: Missing block: B:319:0x0968, code:
            if ("telegram_album".equals(r152) == false) goto L_0x04ae;
     */
    /* JADX WARNING: Missing block: B:486:0x0e21, code:
            if (r158.documentAttachType != 4) goto L_0x18d4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setMessageObject(MessageObject messageObject, GroupedMessages groupedMessages, boolean bottomNear, boolean topNear) {
        boolean z;
        String author;
        String description;
        Document document;
        boolean smallImage;
        Throwable e;
        int restLinesCount;
        int maxPhotoWidth;
        WebPage webPage;
        if (messageObject.checkLayout() || !(this.currentPosition == null || this.lastHeight == AndroidUtilities.displaySize.y)) {
            this.currentMessageObject = null;
        }
        boolean messageIdChanged = this.currentMessageObject == null || this.currentMessageObject.getId() != messageObject.getId();
        boolean messageChanged = this.currentMessageObject != messageObject || messageObject.forceUpdate;
        boolean dataChanged = (this.currentMessageObject != null && this.currentMessageObject.getId() == messageObject.getId() && this.lastSendState == 3 && messageObject.isSent()) || (this.currentMessageObject == messageObject && (isUserDataChanged() || this.photoNotSet));
        boolean groupChanged = groupedMessages != this.currentMessagesGroup;
        if (!(groupChanged || groupedMessages == null)) {
            GroupedMessagePosition newPosition;
            if (groupedMessages.messages.size() > 1) {
                newPosition = (GroupedMessagePosition) this.currentMessagesGroup.positions.get(this.currentMessageObject);
            } else {
                newPosition = null;
            }
            groupChanged = newPosition != this.currentPosition;
        }
        if (messageChanged || dataChanged || groupChanged || isPhotoDataChanged(messageObject) || this.pinnedBottom != bottomNear || this.pinnedTop != topNear) {
            int a;
            int dp;
            int linkPreviewMaxWidth;
            int height;
            int width;
            int lineLeft;
            float f;
            int timeWidthTotal;
            int widthForCaption;
            this.pinnedBottom = bottomNear;
            this.pinnedTop = topNear;
            this.lastTime = -2;
            this.isHighlightedAnimated = false;
            this.widthBeforeNewTimeLine = -1;
            this.currentMessageObject = messageObject;
            this.currentMessagesGroup = groupedMessages;
            if (this.currentMessagesGroup == null || this.currentMessagesGroup.posArray.size() <= 1) {
                this.currentMessagesGroup = null;
                this.currentPosition = null;
            } else {
                this.currentPosition = (GroupedMessagePosition) this.currentMessagesGroup.positions.get(this.currentMessageObject);
                if (this.currentPosition == null) {
                    this.currentMessagesGroup = null;
                }
            }
            z = this.pinnedTop && (this.currentPosition == null || (this.currentPosition.flags & 4) != 0);
            this.drawPinnedTop = z;
            z = this.pinnedBottom && (this.currentPosition == null || (this.currentPosition.flags & 8) != 0);
            this.drawPinnedBottom = z;
            this.photoImage.setCrossfadeWithOldImage(false);
            this.lastSendState = messageObject.messageOwner.send_state;
            this.lastDeleteDate = messageObject.messageOwner.destroyTime;
            this.lastViewsCount = messageObject.messageOwner.views;
            this.isPressed = false;
            this.gamePreviewPressed = false;
            this.isCheckPressed = true;
            this.hasNewLineForTime = false;
            z = this.isChat && !messageObject.isOutOwner() && messageObject.needDrawAvatar() && (this.currentPosition == null || this.currentPosition.edge);
            this.isAvatarVisible = z;
            this.wasLayout = false;
            this.drwaShareGoIcon = false;
            this.groupPhotoInvisible = false;
            this.drawShareButton = checkNeedDrawShareButton(messageObject);
            this.replyNameLayout = null;
            this.adminLayout = null;
            this.replyTextLayout = null;
            this.replyNameWidth = 0;
            this.replyTextWidth = 0;
            this.viaWidth = 0;
            this.viaNameWidth = 0;
            this.addedCaptionHeight = 0;
            this.currentReplyPhoto = null;
            this.currentUser = null;
            this.currentChat = null;
            this.currentViaBotUser = null;
            this.instantViewLayout = null;
            this.drawNameLayout = false;
            if (this.scheduledInvalidate) {
                AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
                this.scheduledInvalidate = false;
            }
            resetPressedLink(-1);
            messageObject.forceUpdate = false;
            this.drawPhotoImage = false;
            this.hasLinkPreview = false;
            this.hasOldCaptionPreview = false;
            this.hasGamePreview = false;
            this.hasInvoicePreview = false;
            this.instantButtonPressed = false;
            this.instantPressed = false;
            if (VERSION.SDK_INT >= 21 && this.instantViewSelectorDrawable != null) {
                this.instantViewSelectorDrawable.setVisible(false, false);
                this.instantViewSelectorDrawable.setState(StateSet.NOTHING);
            }
            this.linkPreviewPressed = false;
            this.buttonPressed = 0;
            this.miniButtonPressed = 0;
            this.pressedBotButton = -1;
            this.linkPreviewHeight = 0;
            this.mediaOffsetY = 0;
            this.documentAttachType = 0;
            this.documentAttach = null;
            this.descriptionLayout = null;
            this.titleLayout = null;
            this.videoInfoLayout = null;
            this.photosCountLayout = null;
            this.siteNameLayout = null;
            this.authorLayout = null;
            this.captionLayout = null;
            this.captionOffsetX = 0;
            this.currentCaption = null;
            this.docTitleLayout = null;
            this.drawImageButton = false;
            this.currentPhotoObject = null;
            this.currentPhotoObjectThumb = null;
            this.currentPhotoFilter = null;
            this.infoLayout = null;
            this.cancelLoading = false;
            this.buttonState = -1;
            this.miniButtonState = -1;
            this.hasMiniProgress = 0;
            if (!(!this.addedForTest || this.currentUrl == null || this.currentWebFile == null)) {
                ImageLoader.getInstance().removeTestWebFile(this.currentUrl);
            }
            this.addedForTest = false;
            this.currentUrl = null;
            this.currentWebFile = null;
            this.photoNotSet = false;
            this.drawBackground = true;
            this.drawName = false;
            this.useSeekBarWaweform = false;
            this.drawInstantView = false;
            this.drawInstantViewType = 0;
            this.drawForwardedName = false;
            this.mediaBackground = false;
            int captionNewLine = 0;
            this.availableTimeWidth = 0;
            this.photoImage.setForceLoading(false);
            this.photoImage.setNeedsQualityThumb(false);
            this.photoImage.setShouldGenerateQualityThumb(false);
            this.photoImage.setAllowDecodeSingleFrame(false);
            this.photoImage.setRoundRadius(AndroidUtilities.m9dp(3.0f));
            if (messageChanged) {
                this.firstVisibleBlockNum = 0;
                this.lastVisibleBlockNum = 0;
                this.needNewVisiblePart = true;
            }
            int maxWidth;
            int count;
            String str;
            int i;
            DocumentAttribute attribute;
            float scale;
            String fileName;
            boolean autoDownload;
            FileLocation fileLocation;
            boolean photoExist;
            if (messageObject.type == 0) {
                this.drawForwardedName = true;
                if (AndroidUtilities.isTablet()) {
                    if (this.isChat && !messageObject.isOutOwner() && messageObject.needDrawAvatar()) {
                        maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.m9dp(122.0f);
                        this.drawName = true;
                    } else {
                        z = (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isOutOwner()) ? false : true;
                        this.drawName = z;
                        maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.m9dp(80.0f);
                    }
                } else if (this.isChat && !messageObject.isOutOwner() && messageObject.needDrawAvatar()) {
                    maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.m9dp(122.0f);
                    this.drawName = true;
                } else {
                    maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.m9dp(80.0f);
                    z = (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isOutOwner()) ? false : true;
                    this.drawName = z;
                }
                this.availableTimeWidth = maxWidth;
                if (messageObject.isRoundVideo()) {
                    this.availableTimeWidth = (int) (((double) this.availableTimeWidth) - (Math.ceil((double) Theme.chat_audioTimePaint.measureText("00:00")) + ((double) (messageObject.isOutOwner() ? 0 : AndroidUtilities.m9dp(64.0f)))));
                }
                measureTime(messageObject);
                int timeMore = this.timeWidth + AndroidUtilities.m9dp(6.0f);
                if (messageObject.isOutOwner()) {
                    timeMore += AndroidUtilities.m9dp(20.5f);
                }
                z = (messageObject.messageOwner.media instanceof TL_messageMediaGame) && (messageObject.messageOwner.media.game instanceof TL_game);
                this.hasGamePreview = z;
                this.hasInvoicePreview = messageObject.messageOwner.media instanceof TL_messageMediaInvoice;
                z = (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && (messageObject.messageOwner.media.webpage instanceof TL_webPage);
                this.hasLinkPreview = z;
                z = this.hasLinkPreview && messageObject.messageOwner.media.webpage.cached_page != null;
                this.drawInstantView = z;
                boolean slideshow = false;
                String siteName = this.hasLinkPreview ? messageObject.messageOwner.media.webpage.site_name : null;
                String webpageType = this.hasLinkPreview ? messageObject.messageOwner.media.webpage.type : null;
                if (this.drawInstantView) {
                    if (siteName != null) {
                        siteName = siteName.toLowerCase();
                        if (!siteName.equals("instagram")) {
                            if (!siteName.equals("twitter")) {
                            }
                        }
                        if ((messageObject.messageOwner.media.webpage.cached_page instanceof TL_page) && ((messageObject.messageOwner.media.webpage.photo instanceof TL_photo) || MessageObject.isVideoDocument(messageObject.messageOwner.media.webpage.document))) {
                            this.drawInstantView = false;
                            slideshow = true;
                            ArrayList<PageBlock> blocks = messageObject.messageOwner.media.webpage.cached_page.blocks;
                            count = 1;
                            for (a = 0; a < blocks.size(); a++) {
                                PageBlock block = (PageBlock) blocks.get(a);
                                if (block instanceof TL_pageBlockSlideshow) {
                                    count = ((TL_pageBlockSlideshow) block).items.size();
                                } else if (block instanceof TL_pageBlockCollage) {
                                    count = ((TL_pageBlockCollage) block).items.size();
                                }
                            }
                            str = LocaleController.formatString("Of", R.string.Of, Integer.valueOf(1), Integer.valueOf(count));
                            this.photosCountWidth = (int) Math.ceil((double) Theme.chat_durationPaint.measureText(str));
                            this.photosCountLayout = new StaticLayout(str, Theme.chat_durationPaint, this.photosCountWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        }
                    }
                } else if ("telegram_channel".equals(webpageType)) {
                    this.drawInstantView = true;
                    this.drawInstantViewType = 1;
                } else if ("telegram_megagroup".equals(webpageType)) {
                    this.drawInstantView = true;
                    this.drawInstantViewType = 2;
                } else if ("telegram_message".equals(webpageType)) {
                    this.drawInstantView = true;
                    this.drawInstantViewType = 3;
                }
                this.backgroundWidth = maxWidth;
                if (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview || maxWidth - messageObject.lastLineWidth < timeMore) {
                    this.backgroundWidth = Math.max(this.backgroundWidth, messageObject.lastLineWidth) + AndroidUtilities.m9dp(31.0f);
                    this.backgroundWidth = Math.max(this.backgroundWidth, this.timeWidth + AndroidUtilities.m9dp(31.0f));
                } else {
                    int diff = this.backgroundWidth - messageObject.lastLineWidth;
                    if (diff < 0 || diff > timeMore) {
                        this.backgroundWidth = Math.max(this.backgroundWidth, messageObject.lastLineWidth + timeMore) + AndroidUtilities.m9dp(31.0f);
                    } else {
                        this.backgroundWidth = ((this.backgroundWidth + timeMore) - diff) + AndroidUtilities.m9dp(31.0f);
                    }
                }
                this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.m9dp(31.0f);
                if (messageObject.isRoundVideo()) {
                    this.availableTimeWidth = (int) (((double) this.availableTimeWidth) - (Math.ceil((double) Theme.chat_audioTimePaint.measureText("00:00")) + ((double) (messageObject.isOutOwner() ? 0 : AndroidUtilities.m9dp(64.0f)))));
                }
                setMessageObjectInternal(messageObject);
                i = messageObject.textWidth;
                dp = (this.hasGamePreview || this.hasInvoicePreview) ? AndroidUtilities.m9dp(10.0f) : 0;
                this.backgroundWidth = dp + i;
                this.totalHeight = (messageObject.textHeight + AndroidUtilities.m9dp(19.5f)) + this.namesOffset;
                if (this.drawPinnedTop) {
                    this.namesOffset -= AndroidUtilities.m9dp(1.0f);
                }
                int maxChildWidth = Math.max(Math.max(Math.max(Math.max(this.backgroundWidth, this.nameWidth), this.forwardedNameWidth), this.replyNameWidth), this.replyTextWidth);
                int maxWebWidth = 0;
                if (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview) {
                    String site_name;
                    String title;
                    Photo photo;
                    String type;
                    int duration;
                    WebFile webDocument;
                    WebFile webDocument2;
                    int additinalWidth;
                    int restLines;
                    boolean authorIsRTL;
                    if (AndroidUtilities.isTablet()) {
                        if (this.isChat && messageObject.needDrawAvatar() && !this.currentMessageObject.isOutOwner()) {
                            linkPreviewMaxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.m9dp(132.0f);
                        } else {
                            linkPreviewMaxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.m9dp(80.0f);
                        }
                    } else if (this.isChat && messageObject.needDrawAvatar() && !this.currentMessageObject.isOutOwner()) {
                        linkPreviewMaxWidth = AndroidUtilities.displaySize.x - AndroidUtilities.m9dp(132.0f);
                    } else {
                        linkPreviewMaxWidth = AndroidUtilities.displaySize.x - AndroidUtilities.m9dp(80.0f);
                    }
                    if (this.drawShareButton) {
                        linkPreviewMaxWidth -= AndroidUtilities.m9dp(20.0f);
                    }
                    if (this.hasLinkPreview) {
                        TL_webPage webPage2 = (TL_webPage) messageObject.messageOwner.media.webpage;
                        site_name = webPage2.site_name;
                        title = webPage2.title;
                        author = webPage2.author;
                        description = webPage2.description;
                        photo = webPage2.photo;
                        document = webPage2.document;
                        type = webPage2.type;
                        duration = webPage2.duration;
                        if (!(site_name == null || photo == null || !site_name.toLowerCase().equals("instagram"))) {
                            linkPreviewMaxWidth = Math.max(AndroidUtilities.displaySize.y / 3, this.currentMessageObject.textWidth);
                        }
                        if (!(slideshow || this.drawInstantView || document != null || type == null)) {
                            if (!type.equals("app")) {
                                if (!type.equals("profile")) {
                                }
                            }
                            smallImage = true;
                            if (!(slideshow || this.drawInstantView || document != null || description == null || type == null)) {
                                if (!type.equals("app")) {
                                    if (!type.equals("profile")) {
                                    }
                                }
                                if (this.currentMessageObject.photoThumbs != null) {
                                    z = true;
                                    this.isSmallImage = z;
                                    webDocument = null;
                                }
                            }
                            z = false;
                            this.isSmallImage = z;
                            webDocument = null;
                        }
                        smallImage = false;
                        if (type.equals("app")) {
                        }
                        if (this.currentMessageObject.photoThumbs != null) {
                        }
                        z = false;
                        this.isSmallImage = z;
                        webDocument = null;
                    } else if (this.hasInvoicePreview) {
                        TL_messageMediaInvoice invoice = (TL_messageMediaInvoice) messageObject.messageOwner.media;
                        site_name = messageObject.messageOwner.media.title;
                        title = null;
                        description = null;
                        photo = null;
                        author = null;
                        document = null;
                        if (invoice.photo instanceof TL_webDocument) {
                            webDocument2 = WebFile.createWithWebDocument(invoice.photo);
                        } else {
                            webDocument2 = null;
                        }
                        duration = 0;
                        type = "invoice";
                        this.isSmallImage = false;
                        smallImage = false;
                        webDocument = webDocument2;
                    } else {
                        TL_game game = messageObject.messageOwner.media.game;
                        site_name = game.title;
                        title = null;
                        description = TextUtils.isEmpty(messageObject.messageText) ? game.description : null;
                        photo = game.photo;
                        author = null;
                        document = game.document;
                        duration = 0;
                        type = "game";
                        this.isSmallImage = false;
                        smallImage = false;
                        webDocument = null;
                    }
                    if (this.hasInvoicePreview) {
                        additinalWidth = 0;
                    } else {
                        additinalWidth = AndroidUtilities.m9dp(10.0f);
                    }
                    int restLinesCount2 = 3;
                    int additionalHeight = 0;
                    int linkPreviewMaxWidth2 = linkPreviewMaxWidth - additinalWidth;
                    if (this.currentMessageObject.photoThumbs == null && photo != null) {
                        this.currentMessageObject.generateThumbs(true);
                    }
                    if (site_name != null) {
                        try {
                            this.siteNameLayout = new StaticLayout(site_name, Theme.chat_replyNamePaint, Math.min((int) Math.ceil((double) (Theme.chat_replyNamePaint.measureText(site_name) + 1.0f)), linkPreviewMaxWidth2), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            this.siteNameRtl = this.siteNameLayout.getLineLeft(0) != 0.0f;
                            height = this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                            this.linkPreviewHeight += height;
                            this.totalHeight += height;
                            additionalHeight = 0 + height;
                            width = this.siteNameLayout.getWidth();
                            this.siteNameWidth = width;
                            maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                            maxWebWidth = Math.max(0, width + additinalWidth);
                        } catch (Throwable e2) {
                            FileLog.m13e(e2);
                        }
                    }
                    boolean titleIsRTL = false;
                    if (title != null) {
                        try {
                            this.titleX = ConnectionsManager.DEFAULT_DATACENTER_ID;
                            if (this.linkPreviewHeight != 0) {
                                this.linkPreviewHeight += AndroidUtilities.m9dp(2.0f);
                                this.totalHeight += AndroidUtilities.m9dp(2.0f);
                            }
                            restLines = 0;
                            if (!this.isSmallImage || description == null) {
                                this.titleLayout = StaticLayoutEx.createStaticLayout(title, Theme.chat_replyNamePaint, linkPreviewMaxWidth2, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.m9dp(1.0f), false, TruncateAt.END, linkPreviewMaxWidth2, 4);
                                restLinesCount = 3;
                            } else {
                                restLines = 3;
                                this.titleLayout = ChatMessageCell.generateStaticLayout(title, Theme.chat_replyNamePaint, linkPreviewMaxWidth2, linkPreviewMaxWidth2 - AndroidUtilities.m9dp(52.0f), 3, 4);
                                restLinesCount = 3 - this.titleLayout.getLineCount();
                            }
                            try {
                                height = this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
                                this.linkPreviewHeight += height;
                                this.totalHeight += height;
                                for (a = 0; a < this.titleLayout.getLineCount(); a++) {
                                    lineLeft = (int) this.titleLayout.getLineLeft(a);
                                    if (lineLeft != 0) {
                                        titleIsRTL = true;
                                    }
                                    if (this.titleX == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                        this.titleX = -lineLeft;
                                    } else {
                                        this.titleX = Math.max(this.titleX, -lineLeft);
                                    }
                                    if (lineLeft != 0) {
                                        width = this.titleLayout.getWidth() - lineLeft;
                                    } else {
                                        width = (int) Math.ceil((double) this.titleLayout.getLineWidth(a));
                                    }
                                    if (a < restLines || (lineLeft != 0 && this.isSmallImage)) {
                                        width += AndroidUtilities.m9dp(52.0f);
                                    }
                                    maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                                    maxWebWidth = Math.max(maxWebWidth, width + additinalWidth);
                                }
                            } catch (Exception e3) {
                                e2 = e3;
                            }
                        } catch (Exception e4) {
                            e2 = e4;
                            restLinesCount = 3;
                            FileLog.m13e(e2);
                            if (titleIsRTL) {
                            }
                            restLinesCount2 = restLinesCount;
                            linkPreviewMaxWidth = linkPreviewMaxWidth2;
                            authorIsRTL = false;
                            if (author == null) {
                            }
                            restLinesCount = restLinesCount2;
                            if (description != null) {
                            }
                            smallImage = false;
                            this.isSmallImage = false;
                            if (smallImage) {
                            }
                            if (document == null) {
                            }
                            if (this.currentPhotoObject == null) {
                            }
                            if (type != null) {
                            }
                            z = false;
                            this.drawImageButton = z;
                            if (this.linkPreviewHeight != 0) {
                            }
                            if (this.documentAttachType == 6) {
                            }
                            if (this.hasInvoicePreview) {
                            }
                            maxChildWidth = Math.max(maxChildWidth, (maxPhotoWidth - (this.hasInvoicePreview ? AndroidUtilities.m9dp(12.0f) : 0)) + additinalWidth);
                            if (this.currentPhotoObject != null) {
                            }
                            if (!smallImage) {
                            }
                            height = maxPhotoWidth;
                            width = maxPhotoWidth;
                            if (this.isSmallImage) {
                            }
                            this.photoImage.setImageCoords(0, 0, width, height);
                            this.currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                            this.currentPhotoFilterThumb = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                            if (webDocument2 != null) {
                            }
                            this.drawPhotoImage = true;
                            if (type != null) {
                            }
                            if (this.hasGamePreview) {
                            }
                            if (this.hasInvoicePreview) {
                            }
                            this.linkPreviewHeight += messageObject.textHeight + AndroidUtilities.m9dp(6.0f);
                            this.totalHeight += AndroidUtilities.m9dp(4.0f);
                            calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                            createInstantViewButton();
                            if (this.currentPosition != null) {
                            }
                            this.totalHeight += AndroidUtilities.m9dp(14.0f);
                            linkPreviewMaxWidth = this.backgroundWidth - AndroidUtilities.m9dp(41.0f);
                            this.hasOldCaptionPreview = true;
                            this.linkPreviewHeight = 0;
                            webPage = this.currentMessageObject.messageOwner.media.webpage;
                            try {
                                width = (int) Math.ceil((double) (Theme.chat_replyNamePaint.measureText(webPage.site_name) + 1.0f));
                                this.siteNameWidth = width;
                                this.siteNameLayout = new StaticLayout(webPage.site_name, Theme.chat_replyNamePaint, Math.min(width, linkPreviewMaxWidth), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                if (this.siteNameLayout.getLineLeft(0) == 0.0f) {
                                }
                                this.siteNameRtl = this.siteNameLayout.getLineLeft(0) == 0.0f;
                                height = this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                                this.linkPreviewHeight += height;
                                this.totalHeight += height;
                            } catch (Throwable e22) {
                                FileLog.m13e(e22);
                            }
                            try {
                                this.descriptionX = 0;
                                if (this.linkPreviewHeight != 0) {
                                }
                                this.descriptionLayout = StaticLayoutEx.createStaticLayout(webPage.description, Theme.chat_replyTextPaint, linkPreviewMaxWidth, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.m9dp(1.0f), false, TruncateAt.END, linkPreviewMaxWidth, 6);
                                height = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                                this.linkPreviewHeight += height;
                                this.totalHeight += height;
                                while (a < this.descriptionLayout.getLineCount()) {
                                }
                            } catch (Throwable e222) {
                                FileLog.m13e(e222);
                            }
                            this.totalHeight += AndroidUtilities.m9dp(17.0f);
                            if (captionNewLine != 0) {
                            }
                            this.botButtons.clear();
                            if (messageIdChanged) {
                            }
                            if (this.currentPosition == null) {
                            }
                            this.substractBackgroundHeight = 0;
                            this.keyboardHeight = 0;
                            if (!this.drawPinnedBottom) {
                            }
                            if (this.drawPinnedBottom) {
                            }
                            this.totalHeight = AndroidUtilities.m9dp(70.0f);
                            if (!this.drawPhotoImage) {
                            }
                            updateWaveform();
                            if (!dataChanged) {
                            }
                            updateButtonState(z, true);
                        }
                        if (titleIsRTL || !this.isSmallImage) {
                            restLinesCount2 = restLinesCount;
                            linkPreviewMaxWidth = linkPreviewMaxWidth2;
                        } else {
                            linkPreviewMaxWidth = linkPreviewMaxWidth2 - AndroidUtilities.m9dp(48.0f);
                            restLinesCount2 = restLinesCount;
                        }
                    } else {
                        linkPreviewMaxWidth = linkPreviewMaxWidth2;
                    }
                    authorIsRTL = false;
                    if (author == null && title == null) {
                        try {
                            if (this.linkPreviewHeight != 0) {
                                this.linkPreviewHeight += AndroidUtilities.m9dp(2.0f);
                                this.totalHeight += AndroidUtilities.m9dp(2.0f);
                            }
                            if (restLinesCount2 != 3 || (this.isSmallImage && description != null)) {
                                this.authorLayout = ChatMessageCell.generateStaticLayout(author, Theme.chat_replyNamePaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.m9dp(52.0f), restLinesCount2, 1);
                                restLinesCount = restLinesCount2 - this.authorLayout.getLineCount();
                            } else {
                                this.authorLayout = new StaticLayout(author, Theme.chat_replyNamePaint, linkPreviewMaxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                restLinesCount = restLinesCount2;
                            }
                            try {
                                height = this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
                                this.linkPreviewHeight += height;
                                this.totalHeight += height;
                                lineLeft = (int) this.authorLayout.getLineLeft(0);
                                this.authorX = -lineLeft;
                                if (lineLeft != 0) {
                                    width = this.authorLayout.getWidth() - lineLeft;
                                    authorIsRTL = true;
                                } else {
                                    width = (int) Math.ceil((double) this.authorLayout.getLineWidth(0));
                                }
                                maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                                maxWebWidth = Math.max(maxWebWidth, width + additinalWidth);
                            } catch (Exception e5) {
                                e222 = e5;
                                FileLog.m13e(e222);
                                if (description != null) {
                                }
                                smallImage = false;
                                this.isSmallImage = false;
                                if (smallImage) {
                                }
                                if (document == null) {
                                }
                                if (this.currentPhotoObject == null) {
                                }
                                if (type != null) {
                                }
                                z = false;
                                this.drawImageButton = z;
                                if (this.linkPreviewHeight != 0) {
                                }
                                if (this.documentAttachType == 6) {
                                }
                                if (this.hasInvoicePreview) {
                                }
                                maxChildWidth = Math.max(maxChildWidth, (maxPhotoWidth - (this.hasInvoicePreview ? AndroidUtilities.m9dp(12.0f) : 0)) + additinalWidth);
                                if (this.currentPhotoObject != null) {
                                }
                                if (smallImage) {
                                }
                                height = maxPhotoWidth;
                                width = maxPhotoWidth;
                                if (this.isSmallImage) {
                                }
                                this.photoImage.setImageCoords(0, 0, width, height);
                                this.currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                                this.currentPhotoFilterThumb = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                                if (webDocument2 != null) {
                                }
                                this.drawPhotoImage = true;
                                if (type != null) {
                                }
                                if (this.hasGamePreview) {
                                }
                                if (this.hasInvoicePreview) {
                                }
                                this.linkPreviewHeight += messageObject.textHeight + AndroidUtilities.m9dp(6.0f);
                                this.totalHeight += AndroidUtilities.m9dp(4.0f);
                                calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                                createInstantViewButton();
                                if (this.currentPosition != null) {
                                }
                                this.totalHeight += AndroidUtilities.m9dp(14.0f);
                                linkPreviewMaxWidth = this.backgroundWidth - AndroidUtilities.m9dp(41.0f);
                                this.hasOldCaptionPreview = true;
                                this.linkPreviewHeight = 0;
                                webPage = this.currentMessageObject.messageOwner.media.webpage;
                                width = (int) Math.ceil((double) (Theme.chat_replyNamePaint.measureText(webPage.site_name) + 1.0f));
                                this.siteNameWidth = width;
                                this.siteNameLayout = new StaticLayout(webPage.site_name, Theme.chat_replyNamePaint, Math.min(width, linkPreviewMaxWidth), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                if (this.siteNameLayout.getLineLeft(0) == 0.0f) {
                                }
                                this.siteNameRtl = this.siteNameLayout.getLineLeft(0) == 0.0f;
                                height = this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                                this.linkPreviewHeight += height;
                                this.totalHeight += height;
                                this.descriptionX = 0;
                                if (this.linkPreviewHeight != 0) {
                                }
                                this.descriptionLayout = StaticLayoutEx.createStaticLayout(webPage.description, Theme.chat_replyTextPaint, linkPreviewMaxWidth, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.m9dp(1.0f), false, TruncateAt.END, linkPreviewMaxWidth, 6);
                                height = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                                this.linkPreviewHeight += height;
                                this.totalHeight += height;
                                while (a < this.descriptionLayout.getLineCount()) {
                                }
                                this.totalHeight += AndroidUtilities.m9dp(17.0f);
                                if (captionNewLine != 0) {
                                }
                                this.botButtons.clear();
                                if (messageIdChanged) {
                                }
                                if (this.currentPosition == null) {
                                }
                                this.substractBackgroundHeight = 0;
                                this.keyboardHeight = 0;
                                if (!this.drawPinnedBottom) {
                                }
                                if (this.drawPinnedBottom) {
                                }
                                this.totalHeight = AndroidUtilities.m9dp(70.0f);
                                if (this.drawPhotoImage) {
                                }
                                updateWaveform();
                                if (dataChanged) {
                                }
                                updateButtonState(z, true);
                            }
                        } catch (Exception e6) {
                            e222 = e6;
                            restLinesCount = restLinesCount2;
                            FileLog.m13e(e222);
                            if (description != null) {
                            }
                            smallImage = false;
                            this.isSmallImage = false;
                            if (smallImage) {
                            }
                            if (document == null) {
                            }
                            if (this.currentPhotoObject == null) {
                            }
                            if (type != null) {
                            }
                            z = false;
                            this.drawImageButton = z;
                            if (this.linkPreviewHeight != 0) {
                            }
                            if (this.documentAttachType == 6) {
                            }
                            if (this.hasInvoicePreview) {
                            }
                            maxChildWidth = Math.max(maxChildWidth, (maxPhotoWidth - (this.hasInvoicePreview ? AndroidUtilities.m9dp(12.0f) : 0)) + additinalWidth);
                            if (this.currentPhotoObject != null) {
                            }
                            if (smallImage) {
                            }
                            height = maxPhotoWidth;
                            width = maxPhotoWidth;
                            if (this.isSmallImage) {
                            }
                            this.photoImage.setImageCoords(0, 0, width, height);
                            this.currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                            this.currentPhotoFilterThumb = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                            if (webDocument2 != null) {
                            }
                            this.drawPhotoImage = true;
                            if (type != null) {
                            }
                            if (this.hasGamePreview) {
                            }
                            if (this.hasInvoicePreview) {
                            }
                            this.linkPreviewHeight += messageObject.textHeight + AndroidUtilities.m9dp(6.0f);
                            this.totalHeight += AndroidUtilities.m9dp(4.0f);
                            calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                            createInstantViewButton();
                            if (this.currentPosition != null) {
                            }
                            this.totalHeight += AndroidUtilities.m9dp(14.0f);
                            linkPreviewMaxWidth = this.backgroundWidth - AndroidUtilities.m9dp(41.0f);
                            this.hasOldCaptionPreview = true;
                            this.linkPreviewHeight = 0;
                            webPage = this.currentMessageObject.messageOwner.media.webpage;
                            width = (int) Math.ceil((double) (Theme.chat_replyNamePaint.measureText(webPage.site_name) + 1.0f));
                            this.siteNameWidth = width;
                            this.siteNameLayout = new StaticLayout(webPage.site_name, Theme.chat_replyNamePaint, Math.min(width, linkPreviewMaxWidth), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            if (this.siteNameLayout.getLineLeft(0) == 0.0f) {
                            }
                            this.siteNameRtl = this.siteNameLayout.getLineLeft(0) == 0.0f;
                            height = this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                            this.linkPreviewHeight += height;
                            this.totalHeight += height;
                            this.descriptionX = 0;
                            if (this.linkPreviewHeight != 0) {
                            }
                            this.descriptionLayout = StaticLayoutEx.createStaticLayout(webPage.description, Theme.chat_replyTextPaint, linkPreviewMaxWidth, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.m9dp(1.0f), false, TruncateAt.END, linkPreviewMaxWidth, 6);
                            height = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                            this.linkPreviewHeight += height;
                            this.totalHeight += height;
                            while (a < this.descriptionLayout.getLineCount()) {
                            }
                            this.totalHeight += AndroidUtilities.m9dp(17.0f);
                            if (captionNewLine != 0) {
                            }
                            this.botButtons.clear();
                            if (messageIdChanged) {
                            }
                            if (this.currentPosition == null) {
                            }
                            this.substractBackgroundHeight = 0;
                            this.keyboardHeight = 0;
                            if (!this.drawPinnedBottom) {
                            }
                            if (this.drawPinnedBottom) {
                            }
                            this.totalHeight = AndroidUtilities.m9dp(70.0f);
                            if (this.drawPhotoImage) {
                            }
                            updateWaveform();
                            if (dataChanged) {
                            }
                            updateButtonState(z, true);
                        }
                    }
                    restLinesCount = restLinesCount2;
                    if (description != null) {
                        try {
                            this.descriptionX = 0;
                            this.currentMessageObject.generateLinkDescription();
                            if (this.linkPreviewHeight != 0) {
                                this.linkPreviewHeight += AndroidUtilities.m9dp(2.0f);
                                this.totalHeight += AndroidUtilities.m9dp(2.0f);
                            }
                            restLines = 0;
                            boolean allowAllLines = site_name != null && site_name.toLowerCase().equals("twitter");
                            if (restLinesCount != 3 || this.isSmallImage) {
                                int i2;
                                restLines = restLinesCount;
                                CharSequence charSequence = messageObject.linkDescription;
                                TextPaint textPaint = Theme.chat_replyTextPaint;
                                int dp2 = linkPreviewMaxWidth - AndroidUtilities.m9dp(52.0f);
                                if (allowAllLines) {
                                    i2 = 100;
                                } else {
                                    i2 = 6;
                                }
                                this.descriptionLayout = ChatMessageCell.generateStaticLayout(charSequence, textPaint, linkPreviewMaxWidth, dp2, restLinesCount, i2);
                            } else {
                                this.descriptionLayout = StaticLayoutEx.createStaticLayout(messageObject.linkDescription, Theme.chat_replyTextPaint, linkPreviewMaxWidth, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.m9dp(1.0f), false, TruncateAt.END, linkPreviewMaxWidth, allowAllLines ? 100 : 6);
                            }
                            height = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                            this.linkPreviewHeight += height;
                            this.totalHeight += height;
                            boolean hasRTL = false;
                            for (a = 0; a < this.descriptionLayout.getLineCount(); a++) {
                                lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                                if (lineLeft != 0) {
                                    hasRTL = true;
                                    if (this.descriptionX == 0) {
                                        this.descriptionX = -lineLeft;
                                    } else {
                                        this.descriptionX = Math.max(this.descriptionX, -lineLeft);
                                    }
                                }
                            }
                            int textWidth = this.descriptionLayout.getWidth();
                            for (a = 0; a < this.descriptionLayout.getLineCount(); a++) {
                                lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                                if (lineLeft == 0 && this.descriptionX != 0) {
                                    this.descriptionX = 0;
                                }
                                if (lineLeft != 0) {
                                    width = textWidth - lineLeft;
                                } else if (hasRTL) {
                                    width = textWidth;
                                } else {
                                    width = Math.min((int) Math.ceil((double) this.descriptionLayout.getLineWidth(a)), textWidth);
                                }
                                if (a < restLines || !(restLines == 0 || lineLeft == 0 || !this.isSmallImage)) {
                                    width += AndroidUtilities.m9dp(52.0f);
                                }
                                if (maxWebWidth < width + additinalWidth) {
                                    if (titleIsRTL) {
                                        this.titleX += (width + additinalWidth) - maxWebWidth;
                                    }
                                    if (authorIsRTL) {
                                        this.authorX += (width + additinalWidth) - maxWebWidth;
                                    }
                                    maxWebWidth = width + additinalWidth;
                                }
                                maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                            }
                        } catch (Throwable e2222) {
                            FileLog.m13e(e2222);
                        }
                    }
                    if (smallImage && (this.descriptionLayout == null || (this.descriptionLayout != null && this.descriptionLayout.getLineCount() == 1))) {
                        smallImage = false;
                        this.isSmallImage = false;
                    }
                    if (smallImage) {
                        maxPhotoWidth = AndroidUtilities.m9dp(48.0f);
                    } else {
                        maxPhotoWidth = linkPreviewMaxWidth;
                    }
                    if (document == null) {
                        PhotoSize photoSize;
                        PhotoSize photoSize2;
                        int dp3;
                        if (MessageObject.isRoundVideoDocument(document)) {
                            this.currentPhotoObject = document.thumb;
                            this.documentAttach = document;
                            this.documentAttachType = 7;
                            webDocument2 = webDocument;
                        } else if (MessageObject.isGifDocument(document)) {
                            if (!SharedConfig.autoplayGifs) {
                                messageObject.gifState = 1.0f;
                            }
                            this.photoImage.setAllowStartAnimation(messageObject.gifState != 1.0f);
                            this.currentPhotoObject = document.thumb;
                            if (this.currentPhotoObject != null && (this.currentPhotoObject.var_w == 0 || this.currentPhotoObject.var_h == 0)) {
                                for (a = 0; a < document.attributes.size(); a++) {
                                    attribute = (DocumentAttribute) document.attributes.get(a);
                                    if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                        this.currentPhotoObject.var_w = attribute.var_w;
                                        this.currentPhotoObject.var_h = attribute.var_h;
                                        break;
                                    }
                                }
                                if (this.currentPhotoObject.var_w == 0 || this.currentPhotoObject.var_h == 0) {
                                    photoSize = this.currentPhotoObject;
                                    photoSize2 = this.currentPhotoObject;
                                    dp3 = AndroidUtilities.m9dp(150.0f);
                                    photoSize2.var_h = dp3;
                                    photoSize.var_w = dp3;
                                }
                            }
                            this.documentAttach = document;
                            this.documentAttachType = 2;
                            webDocument2 = webDocument;
                        } else if (MessageObject.isVideoDocument(document)) {
                            if (photo != null) {
                                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize(), true);
                            }
                            if (this.currentPhotoObject == null) {
                                this.currentPhotoObject = document.thumb;
                            }
                            if (this.currentPhotoObject != null && (this.currentPhotoObject.var_w == 0 || this.currentPhotoObject.var_h == 0)) {
                                for (a = 0; a < document.attributes.size(); a++) {
                                    attribute = (DocumentAttribute) document.attributes.get(a);
                                    if (attribute instanceof TL_documentAttributeVideo) {
                                        this.currentPhotoObject.var_w = attribute.var_w;
                                        this.currentPhotoObject.var_h = attribute.var_h;
                                        break;
                                    }
                                }
                                if (this.currentPhotoObject.var_w == 0 || this.currentPhotoObject.var_h == 0) {
                                    photoSize = this.currentPhotoObject;
                                    photoSize2 = this.currentPhotoObject;
                                    dp3 = AndroidUtilities.m9dp(150.0f);
                                    photoSize2.var_h = dp3;
                                    photoSize.var_w = dp3;
                                }
                            }
                            createDocumentLayout(0, messageObject);
                            webDocument2 = webDocument;
                        } else if (MessageObject.isStickerDocument(document)) {
                            this.currentPhotoObject = document.thumb;
                            if (this.currentPhotoObject != null && (this.currentPhotoObject.var_w == 0 || this.currentPhotoObject.var_h == 0)) {
                                for (a = 0; a < document.attributes.size(); a++) {
                                    attribute = (DocumentAttribute) document.attributes.get(a);
                                    if (attribute instanceof TL_documentAttributeImageSize) {
                                        this.currentPhotoObject.var_w = attribute.var_w;
                                        this.currentPhotoObject.var_h = attribute.var_h;
                                        break;
                                    }
                                }
                                if (this.currentPhotoObject.var_w == 0 || this.currentPhotoObject.var_h == 0) {
                                    photoSize = this.currentPhotoObject;
                                    photoSize2 = this.currentPhotoObject;
                                    dp3 = AndroidUtilities.m9dp(150.0f);
                                    photoSize2.var_h = dp3;
                                    photoSize.var_w = dp3;
                                }
                            }
                            this.documentAttach = document;
                            this.documentAttachType = 6;
                            webDocument2 = webDocument;
                        } else {
                            calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                            if (!MessageObject.isStickerDocument(document)) {
                                if (this.backgroundWidth < AndroidUtilities.m9dp(20.0f) + maxWidth) {
                                    this.backgroundWidth = AndroidUtilities.m9dp(20.0f) + maxWidth;
                                }
                                if (MessageObject.isVoiceDocument(document)) {
                                    createDocumentLayout(this.backgroundWidth - AndroidUtilities.m9dp(10.0f), messageObject);
                                    this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.m9dp(8.0f)) + this.linkPreviewHeight;
                                    this.totalHeight += AndroidUtilities.m9dp(44.0f);
                                    this.linkPreviewHeight += AndroidUtilities.m9dp(44.0f);
                                    maxWidth -= AndroidUtilities.m9dp(86.0f);
                                    if (AndroidUtilities.isTablet()) {
                                        i = AndroidUtilities.getMinTabletSide();
                                        f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 52.0f : 0.0f;
                                        maxChildWidth = Math.max(maxChildWidth, (Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(220.0f)) - AndroidUtilities.m9dp(30.0f)) + additinalWidth);
                                    } else {
                                        i = AndroidUtilities.displaySize.x;
                                        f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 52.0f : 0.0f;
                                        maxChildWidth = Math.max(maxChildWidth, (Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(220.0f)) - AndroidUtilities.m9dp(30.0f)) + additinalWidth);
                                    }
                                    calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                                    webDocument2 = webDocument;
                                } else if (MessageObject.isMusicDocument(document)) {
                                    int durationWidth = createDocumentLayout(this.backgroundWidth - AndroidUtilities.m9dp(10.0f), messageObject);
                                    this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.m9dp(8.0f)) + this.linkPreviewHeight;
                                    this.totalHeight += AndroidUtilities.m9dp(56.0f);
                                    this.linkPreviewHeight += AndroidUtilities.m9dp(56.0f);
                                    maxWidth -= AndroidUtilities.m9dp(86.0f);
                                    maxChildWidth = Math.max(maxChildWidth, (durationWidth + additinalWidth) + AndroidUtilities.m9dp(94.0f));
                                    if (this.songLayout != null && this.songLayout.getLineCount() > 0) {
                                        maxChildWidth = (int) Math.max((float) maxChildWidth, (this.songLayout.getLineWidth(0) + ((float) additinalWidth)) + ((float) AndroidUtilities.m9dp(86.0f)));
                                    }
                                    if (this.performerLayout != null && this.performerLayout.getLineCount() > 0) {
                                        maxChildWidth = (int) Math.max((float) maxChildWidth, (this.performerLayout.getLineWidth(0) + ((float) additinalWidth)) + ((float) AndroidUtilities.m9dp(86.0f)));
                                    }
                                    calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                                    webDocument2 = webDocument;
                                } else {
                                    createDocumentLayout(this.backgroundWidth - AndroidUtilities.m9dp(168.0f), messageObject);
                                    this.drawImageButton = true;
                                    if (this.drawPhotoImage) {
                                        this.totalHeight += AndroidUtilities.m9dp(100.0f);
                                        this.linkPreviewHeight += AndroidUtilities.m9dp(86.0f);
                                        this.photoImage.setImageCoords(0, this.totalHeight + this.namesOffset, AndroidUtilities.m9dp(86.0f), AndroidUtilities.m9dp(86.0f));
                                        webDocument2 = webDocument;
                                    } else {
                                        this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.m9dp(8.0f)) + this.linkPreviewHeight;
                                        this.photoImage.setImageCoords(0, (this.totalHeight + this.namesOffset) - AndroidUtilities.m9dp(14.0f), AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
                                        this.totalHeight += AndroidUtilities.m9dp(64.0f);
                                        this.linkPreviewHeight += AndroidUtilities.m9dp(50.0f);
                                        webDocument2 = webDocument;
                                    }
                                }
                            }
                            webDocument2 = webDocument;
                        }
                    } else if (photo != null) {
                        ArrayList arrayList;
                        if (type != null) {
                            if (type.equals("photo")) {
                                z = true;
                                this.drawImageButton = z;
                                arrayList = messageObject.photoThumbs;
                                if (this.drawImageButton) {
                                    dp = maxPhotoWidth;
                                } else {
                                    dp = AndroidUtilities.getPhotoSize();
                                }
                                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, dp, this.drawImageButton);
                                this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                                if (this.currentPhotoObjectThumb == this.currentPhotoObject) {
                                    this.currentPhotoObjectThumb = null;
                                    webDocument2 = webDocument;
                                }
                                webDocument2 = webDocument;
                            }
                        }
                        z = false;
                        this.drawImageButton = z;
                        arrayList = messageObject.photoThumbs;
                        if (this.drawImageButton) {
                        }
                        if (this.drawImageButton) {
                        }
                        this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, dp, this.drawImageButton);
                        this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                        if (this.currentPhotoObjectThumb == this.currentPhotoObject) {
                        }
                        webDocument2 = webDocument;
                    } else {
                        if (webDocument != null) {
                            if (webDocument.mime_type.startsWith("image/")) {
                                webDocument2 = webDocument;
                            } else {
                                webDocument2 = null;
                            }
                            this.drawImageButton = false;
                        }
                        webDocument2 = webDocument;
                    }
                    if (!(this.documentAttachType == 5 || this.documentAttachType == 3 || this.documentAttachType == 1)) {
                        if (this.currentPhotoObject == null || webDocument2 != null) {
                            if (type != null) {
                                if (!type.equals("photo")) {
                                    if (!type.equals("document") || this.documentAttachType == 6) {
                                        if (!type.equals("gif")) {
                                        }
                                    }
                                }
                                z = true;
                                this.drawImageButton = z;
                                if (this.linkPreviewHeight != 0) {
                                    this.linkPreviewHeight += AndroidUtilities.m9dp(2.0f);
                                    this.totalHeight += AndroidUtilities.m9dp(2.0f);
                                }
                                if (this.documentAttachType == 6) {
                                    if (AndroidUtilities.isTablet()) {
                                        maxPhotoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                                    } else {
                                        maxPhotoWidth = (int) (((float) AndroidUtilities.displaySize.x) * 0.5f);
                                    }
                                } else if (this.documentAttachType == 7) {
                                    maxPhotoWidth = AndroidUtilities.roundMessageSize;
                                    this.photoImage.setAllowDecodeSingleFrame(true);
                                }
                                maxChildWidth = Math.max(maxChildWidth, (maxPhotoWidth - (this.hasInvoicePreview ? AndroidUtilities.m9dp(12.0f) : 0)) + additinalWidth);
                                if (this.currentPhotoObject != null) {
                                    this.currentPhotoObject.size = -1;
                                    if (this.currentPhotoObjectThumb != null) {
                                        this.currentPhotoObjectThumb.size = -1;
                                    }
                                } else {
                                    webDocument2.size = -1;
                                }
                                if (smallImage || this.documentAttachType == 7) {
                                    height = maxPhotoWidth;
                                    width = maxPhotoWidth;
                                } else if (this.hasGamePreview || this.hasInvoicePreview) {
                                    scale = ((float) 640) / ((float) (maxPhotoWidth - AndroidUtilities.m9dp(2.0f)));
                                    width = (int) (((float) 640) / scale);
                                    height = (int) (((float) 360) / scale);
                                } else {
                                    width = this.currentPhotoObject.var_w;
                                    scale = ((float) width) / ((float) (maxPhotoWidth - AndroidUtilities.m9dp(2.0f)));
                                    width = (int) (((float) width) / scale);
                                    height = (int) (((float) this.currentPhotoObject.var_h) / scale);
                                    if (site_name == null || !(site_name == null || site_name.toLowerCase().equals("instagram") || this.documentAttachType != 0)) {
                                        if (height > AndroidUtilities.displaySize.y / 3) {
                                            height = AndroidUtilities.displaySize.y / 3;
                                        }
                                    } else if (height > AndroidUtilities.displaySize.y / 2) {
                                        height = AndroidUtilities.displaySize.y / 2;
                                    }
                                }
                                if (this.isSmallImage) {
                                    if (AndroidUtilities.m9dp(50.0f) + additionalHeight > this.linkPreviewHeight) {
                                        this.totalHeight += ((AndroidUtilities.m9dp(50.0f) + additionalHeight) - this.linkPreviewHeight) + AndroidUtilities.m9dp(8.0f);
                                        this.linkPreviewHeight = AndroidUtilities.m9dp(50.0f) + additionalHeight;
                                    }
                                    this.linkPreviewHeight -= AndroidUtilities.m9dp(8.0f);
                                } else {
                                    this.totalHeight += AndroidUtilities.m9dp(12.0f) + height;
                                    this.linkPreviewHeight += height;
                                }
                                this.photoImage.setImageCoords(0, 0, width, height);
                                this.currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                                this.currentPhotoFilterThumb = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                                if (webDocument2 != null) {
                                    this.photoImage.setImage(webDocument2, null, this.currentPhotoFilter, null, null, "b1", webDocument2.size, null, messageObject, 1);
                                } else if (this.documentAttachType == 6) {
                                    this.photoImage.setImage(this.documentAttach, null, this.currentPhotoFilter, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, "b1", this.documentAttach.size, "webp", messageObject, 1);
                                } else if (this.documentAttachType == 4) {
                                    this.photoImage.setNeedsQualityThumb(true);
                                    this.photoImage.setShouldGenerateQualityThumb(true);
                                    this.photoImage.setImage(null, null, this.currentPhotoObject.location, this.currentPhotoFilter, 0, null, messageObject, 0);
                                } else if (this.documentAttachType == 2 || this.documentAttachType == 7) {
                                    fileName = FileLoader.getAttachFileName(document);
                                    autoDownload = false;
                                    if (MessageObject.isRoundVideoDocument(document)) {
                                        this.photoImage.setRoundRadius(AndroidUtilities.roundMessageSize / 2);
                                        autoDownload = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject);
                                    } else if (MessageObject.isNewGifDocument(document)) {
                                        autoDownload = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject);
                                    }
                                    if (messageObject.isSending() || messageObject.isEditing() || !(messageObject.mediaExists || FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName) || autoDownload)) {
                                        this.photoNotSet = true;
                                        this.photoImage.setImage(null, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilterThumb, 0, null, this.currentMessageObject, 0);
                                    } else {
                                        this.photoNotSet = false;
                                        ImageReceiver imageReceiver = this.photoImage;
                                        if (this.currentPhotoObject != null) {
                                            fileLocation = this.currentPhotoObject.location;
                                        } else {
                                            fileLocation = null;
                                        }
                                        imageReceiver.setImage(document, null, fileLocation, this.currentPhotoFilterThumb, document.size, null, this.currentMessageObject, 0);
                                    }
                                } else {
                                    photoExist = messageObject.mediaExists;
                                    fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                                    if (this.hasGamePreview || photoExist || DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject) || FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                                        this.photoNotSet = false;
                                        this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, this.currentPhotoFilterThumb, 0, null, messageObject, 0);
                                    } else {
                                        this.photoNotSet = true;
                                        if (this.currentPhotoObjectThumb != null) {
                                            this.photoImage.setImage(null, null, this.currentPhotoObjectThumb.location, String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}), 0, null, messageObject, 0);
                                        } else {
                                            this.photoImage.setImageBitmap((Drawable) null);
                                        }
                                    }
                                }
                                this.drawPhotoImage = true;
                                if (type != null) {
                                    if (type.equals(MimeTypes.BASE_TYPE_VIDEO) && duration != 0) {
                                        int seconds = duration - ((duration / 60) * 60);
                                        str = String.format("%d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(seconds)});
                                        this.durationWidth = (int) Math.ceil((double) Theme.chat_durationPaint.measureText(str));
                                        this.videoInfoLayout = new StaticLayout(str, Theme.chat_durationPaint, this.durationWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                    }
                                }
                                if (this.hasGamePreview) {
                                    str = LocaleController.getString("AttachGame", R.string.AttachGame).toUpperCase();
                                    this.durationWidth = (int) Math.ceil((double) Theme.chat_gamePaint.measureText(str));
                                    this.videoInfoLayout = new StaticLayout(str, Theme.chat_gamePaint, this.durationWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                }
                            }
                            z = false;
                            this.drawImageButton = z;
                            if (this.linkPreviewHeight != 0) {
                            }
                            if (this.documentAttachType == 6) {
                            }
                            if (this.hasInvoicePreview) {
                            }
                            maxChildWidth = Math.max(maxChildWidth, (maxPhotoWidth - (this.hasInvoicePreview ? AndroidUtilities.m9dp(12.0f) : 0)) + additinalWidth);
                            if (this.currentPhotoObject != null) {
                            }
                            if (smallImage) {
                            }
                            height = maxPhotoWidth;
                            width = maxPhotoWidth;
                            if (this.isSmallImage) {
                            }
                            this.photoImage.setImageCoords(0, 0, width, height);
                            this.currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                            this.currentPhotoFilterThumb = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                            if (webDocument2 != null) {
                            }
                            this.drawPhotoImage = true;
                            if (type != null) {
                            }
                            if (this.hasGamePreview) {
                            }
                        } else {
                            this.photoImage.setImageBitmap((Drawable) null);
                            this.linkPreviewHeight -= AndroidUtilities.m9dp(6.0f);
                            this.totalHeight += AndroidUtilities.m9dp(4.0f);
                        }
                        if (this.hasInvoicePreview) {
                            CharSequence str2;
                            if ((messageObject.messageOwner.media.flags & 4) != 0) {
                                str2 = LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt).toUpperCase();
                            } else if (messageObject.messageOwner.media.test) {
                                str2 = LocaleController.getString("PaymentTestInvoice", R.string.PaymentTestInvoice).toUpperCase();
                            } else {
                                str2 = LocaleController.getString("PaymentInvoice", R.string.PaymentInvoice).toUpperCase();
                            }
                            String price = LocaleController.getInstance().formatCurrencyString(messageObject.messageOwner.media.total_amount, messageObject.messageOwner.media.currency);
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(price + " " + str2);
                            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, price.length(), 33);
                            this.durationWidth = (int) Math.ceil((double) Theme.chat_shipmentPaint.measureText(spannableStringBuilder, 0, spannableStringBuilder.length()));
                            this.videoInfoLayout = new StaticLayout(spannableStringBuilder, Theme.chat_shipmentPaint, this.durationWidth + AndroidUtilities.m9dp(10.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            if (!this.drawPhotoImage) {
                                this.totalHeight += AndroidUtilities.m9dp(6.0f);
                                timeWidthTotal = this.timeWidth + AndroidUtilities.m9dp((float) ((messageObject.isOutOwner() ? 20 : 0) + 14));
                                if (this.durationWidth + timeWidthTotal > maxWidth) {
                                    maxChildWidth = Math.max(this.durationWidth, maxChildWidth);
                                    this.totalHeight += AndroidUtilities.m9dp(12.0f);
                                } else {
                                    maxChildWidth = Math.max(this.durationWidth + timeWidthTotal, maxChildWidth);
                                }
                            }
                        }
                        if (this.hasGamePreview && messageObject.textHeight != 0) {
                            this.linkPreviewHeight += messageObject.textHeight + AndroidUtilities.m9dp(6.0f);
                            this.totalHeight += AndroidUtilities.m9dp(4.0f);
                        }
                        calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                    }
                    createInstantViewButton();
                } else {
                    this.photoImage.setImageBitmap((Drawable) null);
                    calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                }
            } else if (messageObject.type == 16) {
                String text;
                this.drawName = false;
                this.drawForwardedName = false;
                this.drawPhotoImage = false;
                if (AndroidUtilities.isTablet()) {
                    i = AndroidUtilities.getMinTabletSide();
                    f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(270.0f));
                } else {
                    i = AndroidUtilities.displaySize.x;
                    f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(270.0f));
                }
                this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.m9dp(31.0f);
                maxWidth = getMaxNameWidth() - AndroidUtilities.m9dp(50.0f);
                if (maxWidth < 0) {
                    maxWidth = AndroidUtilities.m9dp(10.0f);
                }
                String time = LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000);
                TL_messageActionPhoneCall call = (TL_messageActionPhoneCall) messageObject.messageOwner.action;
                boolean isMissed = call.reason instanceof TL_phoneCallDiscardReasonMissed;
                if (messageObject.isOutOwner()) {
                    if (isMissed) {
                        text = LocaleController.getString("CallMessageOutgoingMissed", R.string.CallMessageOutgoingMissed);
                    } else {
                        text = LocaleController.getString("CallMessageOutgoing", R.string.CallMessageOutgoing);
                    }
                } else if (isMissed) {
                    text = LocaleController.getString("CallMessageIncomingMissed", R.string.CallMessageIncomingMissed);
                } else if (call.reason instanceof TL_phoneCallDiscardReasonBusy) {
                    text = LocaleController.getString("CallMessageIncomingDeclined", R.string.CallMessageIncomingDeclined);
                } else {
                    text = LocaleController.getString("CallMessageIncoming", R.string.CallMessageIncoming);
                }
                if (call.duration > 0) {
                    time = time + ", " + LocaleController.formatCallDuration(call.duration);
                }
                this.titleLayout = new StaticLayout(TextUtils.ellipsize(text, Theme.chat_audioTitlePaint, (float) maxWidth, TruncateAt.END), Theme.chat_audioTitlePaint, maxWidth + AndroidUtilities.m9dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(time, Theme.chat_contactPhonePaint, (float) maxWidth, TruncateAt.END), Theme.chat_contactPhonePaint, maxWidth + AndroidUtilities.m9dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                setMessageObjectInternal(messageObject);
                this.totalHeight = AndroidUtilities.m9dp(65.0f) + this.namesOffset;
                if (this.drawPinnedTop) {
                    this.namesOffset -= AndroidUtilities.m9dp(1.0f);
                }
            } else if (messageObject.type == 12) {
                Drawable drawable;
                CharSequence phone;
                this.drawName = false;
                this.drawForwardedName = true;
                this.drawPhotoImage = true;
                this.photoImage.setRoundRadius(AndroidUtilities.m9dp(22.0f));
                if (AndroidUtilities.isTablet()) {
                    i = AndroidUtilities.getMinTabletSide();
                    f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(270.0f));
                } else {
                    i = AndroidUtilities.displaySize.x;
                    f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(270.0f));
                }
                this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.m9dp(31.0f);
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.media.user_id));
                maxWidth = getMaxNameWidth() - AndroidUtilities.m9dp(80.0f);
                if (maxWidth < 0) {
                    maxWidth = AndroidUtilities.m9dp(10.0f);
                }
                fileLocation = null;
                if (user != null) {
                    if (user.photo != null) {
                        fileLocation = user.photo.photo_small;
                    }
                    this.contactAvatarDrawable.setInfo(user);
                }
                ImageReceiver imageReceiver2 = this.photoImage;
                String str3 = "50_50";
                if (user != null) {
                    drawable = this.contactAvatarDrawable;
                } else {
                    drawable = Theme.chat_contactDrawable[messageObject.isOutOwner() ? 1 : 0];
                }
                imageReceiver2.setImage(fileLocation, str3, drawable, null, messageObject, 0);
                if (TextUtils.isEmpty(messageObject.vCardData)) {
                    String phone2 = messageObject.messageOwner.media.phone_number;
                    if (TextUtils.isEmpty(phone2)) {
                        phone = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                    } else {
                        phone = CLASSNAMEPhoneFormat.getInstance().format(phone2);
                    }
                } else {
                    phone = messageObject.vCardData;
                    this.drawInstantView = true;
                    this.drawInstantViewType = 5;
                }
                CharSequence currentNameString = ContactsController.formatName(messageObject.messageOwner.media.first_name, messageObject.messageOwner.media.last_name).replace(10, ' ');
                if (currentNameString.length() == 0) {
                    currentNameString = messageObject.messageOwner.media.phone_number;
                    if (currentNameString == null) {
                        currentNameString = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                }
                this.titleLayout = new StaticLayout(TextUtils.ellipsize(currentNameString, Theme.chat_contactNamePaint, (float) maxWidth, TruncateAt.END), Theme.chat_contactNamePaint, maxWidth + AndroidUtilities.m9dp(4.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.docTitleLayout = new StaticLayout(phone, Theme.chat_contactPhonePaint, maxWidth + AndroidUtilities.m9dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.m9dp(1.0f), false);
                setMessageObjectInternal(messageObject);
                if (this.drawForwardedName && messageObject.needDrawForwarded() && (this.currentPosition == null || this.currentPosition.minY == (byte) 0)) {
                    this.namesOffset += AndroidUtilities.m9dp(5.0f);
                } else if (this.drawNameLayout && messageObject.messageOwner.reply_to_msg_id == 0) {
                    this.namesOffset += AndroidUtilities.m9dp(7.0f);
                }
                this.totalHeight = (AndroidUtilities.m9dp(55.0f) + this.namesOffset) + this.docTitleLayout.getHeight();
                if (this.drawPinnedTop) {
                    this.namesOffset -= AndroidUtilities.m9dp(1.0f);
                }
                if (this.drawInstantView) {
                    createInstantViewButton();
                } else if (this.docTitleLayout.getLineCount() > 0 && (this.backgroundWidth - AndroidUtilities.m9dp(110.0f)) - ((int) Math.ceil((double) this.docTitleLayout.getLineWidth(this.docTitleLayout.getLineCount() - 1))) < this.timeWidth) {
                    this.totalHeight += AndroidUtilities.m9dp(8.0f);
                }
            } else if (messageObject.type == 2) {
                this.drawForwardedName = true;
                if (AndroidUtilities.isTablet()) {
                    i = AndroidUtilities.getMinTabletSide();
                    f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(270.0f));
                } else {
                    i = AndroidUtilities.displaySize.x;
                    f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(270.0f));
                }
                createDocumentLayout(this.backgroundWidth, messageObject);
                setMessageObjectInternal(messageObject);
                this.totalHeight = AndroidUtilities.m9dp(70.0f) + this.namesOffset;
                if (this.drawPinnedTop) {
                    this.namesOffset -= AndroidUtilities.m9dp(1.0f);
                }
            } else if (messageObject.type == 14) {
                if (AndroidUtilities.isTablet()) {
                    i = AndroidUtilities.getMinTabletSide();
                    f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(270.0f));
                } else {
                    i = AndroidUtilities.displaySize.x;
                    f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(270.0f));
                }
                createDocumentLayout(this.backgroundWidth, messageObject);
                setMessageObjectInternal(messageObject);
                this.totalHeight = AndroidUtilities.m9dp(82.0f) + this.namesOffset;
                if (this.drawPinnedTop) {
                    this.namesOffset -= AndroidUtilities.m9dp(1.0f);
                }
            } else {
                z = (messageObject.messageOwner.fwd_from == null || messageObject.type == 13) ? false : true;
                this.drawForwardedName = z;
                this.mediaBackground = messageObject.type != 9;
                this.drawImageButton = true;
                this.drawPhotoImage = true;
                int photoWidth = 0;
                int photoHeight = 0;
                int additionHeight = 0;
                if (!(messageObject.gifState == 2.0f || SharedConfig.autoplayGifs || (messageObject.type != 8 && messageObject.type != 5))) {
                    messageObject.gifState = 1.0f;
                }
                if (messageObject.isRoundVideo()) {
                    this.photoImage.setAllowDecodeSingleFrame(true);
                    this.photoImage.setAllowStartAnimation(MediaController.getInstance().getPlayingMessageObject() == null);
                } else {
                    this.photoImage.setAllowStartAnimation(messageObject.gifState == 0.0f);
                }
                this.photoImage.setForcePreview(messageObject.needDrawBluredPreview());
                int lineCount;
                float maxHeight;
                if (messageObject.type == 9) {
                    if (AndroidUtilities.isTablet()) {
                        i = AndroidUtilities.getMinTabletSide();
                        f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                        this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(270.0f));
                    } else {
                        i = AndroidUtilities.displaySize.x;
                        f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                        this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(270.0f));
                    }
                    if (checkNeedDrawShareButton(messageObject)) {
                        this.backgroundWidth -= AndroidUtilities.m9dp(20.0f);
                    }
                    maxWidth = this.backgroundWidth - AndroidUtilities.m9dp(138.0f);
                    createDocumentLayout(maxWidth, messageObject);
                    if (!TextUtils.isEmpty(messageObject.caption)) {
                        maxWidth += AndroidUtilities.m9dp(86.0f);
                    }
                    if (this.drawPhotoImage) {
                        photoWidth = AndroidUtilities.m9dp(86.0f);
                        photoHeight = AndroidUtilities.m9dp(86.0f);
                    } else {
                        photoWidth = AndroidUtilities.m9dp(56.0f);
                        photoHeight = AndroidUtilities.m9dp(56.0f);
                        maxWidth += AndroidUtilities.m9dp(TextUtils.isEmpty(messageObject.caption) ? 51.0f : 21.0f);
                    }
                    this.availableTimeWidth = maxWidth;
                    if (!this.drawPhotoImage && TextUtils.isEmpty(messageObject.caption) && this.infoLayout.getLineCount() > 0) {
                        measureTime(messageObject);
                        if ((this.backgroundWidth - AndroidUtilities.m9dp(122.0f)) - ((int) Math.ceil((double) this.infoLayout.getLineWidth(0))) < this.timeWidth) {
                            photoHeight += AndroidUtilities.m9dp(8.0f);
                        }
                    }
                } else if (messageObject.type == 4) {
                    GeoPoint point = messageObject.messageOwner.media.geo;
                    double lat = point.lat;
                    double lon = point._long;
                    if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                        long j;
                        if (AndroidUtilities.isTablet()) {
                            i = AndroidUtilities.getMinTabletSide();
                            f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                            this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(289.0f));
                        } else {
                            i = AndroidUtilities.displaySize.x;
                            f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                            this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(289.0f));
                        }
                        if (checkNeedDrawShareButton(messageObject)) {
                            this.backgroundWidth -= AndroidUtilities.m9dp(20.0f);
                        }
                        maxWidth = this.backgroundWidth - AndroidUtilities.m9dp(37.0f);
                        this.availableTimeWidth = maxWidth;
                        maxWidth -= AndroidUtilities.m9dp(54.0f);
                        photoWidth = this.backgroundWidth - AndroidUtilities.m9dp(21.0f);
                        photoHeight = AndroidUtilities.m9dp(195.0f);
                        double rad = ((double) CLASSNAMEC.ENCODING_PCM_MU_LAW) / 3.141592653589793d;
                        lat = ((1.5707963267948966d - (2.0d * Math.atan(Math.exp((((double) (Math.round(((double) CLASSNAMEC.ENCODING_PCM_MU_LAW) - ((Math.log((1.0d + Math.sin((3.141592653589793d * lat) / 180.0d)) / (1.0d - Math.sin((3.141592653589793d * lat) / 180.0d))) * rad) / 2.0d)) - ((long) (AndroidUtilities.m9dp(10.3f) << 6)))) - ((double) CLASSNAMEC.ENCODING_PCM_MU_LAW)) / rad)))) * 180.0d) / 3.141592653589793d;
                        this.currentUrl = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, (int) (((float) photoWidth) / AndroidUtilities.density), (int) (((float) photoHeight) / AndroidUtilities.density), false, 15);
                        this.currentWebFile = WebFile.createWithGeoPoint(lat, lon, point.access_hash, (int) (((float) photoWidth) / AndroidUtilities.density), (int) (((float) photoHeight) / AndroidUtilities.density), 15, Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                        z = isCurrentLocationTimeExpired(messageObject);
                        this.locationExpired = z;
                        if (z) {
                            this.backgroundWidth -= AndroidUtilities.m9dp(9.0f);
                        } else {
                            this.photoImage.setCrossfadeWithOldImage(true);
                            this.mediaBackground = false;
                            additionHeight = AndroidUtilities.m9dp(56.0f);
                            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
                            this.scheduledInvalidate = true;
                        }
                        this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation), Theme.chat_locationTitlePaint, (float) maxWidth, TruncateAt.END), Theme.chat_locationTitlePaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        TLObject currentPhoto = null;
                        updateCurrentUserAndChat();
                        Object parentObject = null;
                        if (this.currentUser != null) {
                            if (this.currentUser.photo != null) {
                                currentPhoto = this.currentUser.photo.photo_small;
                            }
                            this.contactAvatarDrawable.setInfo(this.currentUser);
                            parentObject = this.currentUser;
                        } else if (this.currentChat != null) {
                            if (this.currentChat.photo != null) {
                                currentPhoto = this.currentChat.photo.photo_small;
                            }
                            this.contactAvatarDrawable.setInfo(this.currentChat);
                            Chat parentObject2 = this.currentChat;
                        }
                        this.locationImageReceiver.setImage(currentPhoto, "50_50", this.contactAvatarDrawable, null, parentObject2, 0);
                        if (messageObject.messageOwner.edit_date != 0) {
                            j = (long) messageObject.messageOwner.edit_date;
                        } else {
                            j = (long) messageObject.messageOwner.date;
                        }
                        this.infoLayout = new StaticLayout(LocaleController.formatLocationUpdateDate(j), Theme.chat_locationAddressPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    } else if (TextUtils.isEmpty(messageObject.messageOwner.media.title)) {
                        if (AndroidUtilities.isTablet()) {
                            i = AndroidUtilities.getMinTabletSide();
                            f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                            this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(289.0f));
                        } else {
                            i = AndroidUtilities.displaySize.x;
                            f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                            this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(289.0f));
                        }
                        if (checkNeedDrawShareButton(messageObject)) {
                            this.backgroundWidth -= AndroidUtilities.m9dp(20.0f);
                        }
                        this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.m9dp(34.0f);
                        photoWidth = this.backgroundWidth - AndroidUtilities.m9dp(12.0f);
                        photoHeight = AndroidUtilities.m9dp(195.0f);
                        this.currentUrl = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, (int) (((float) photoWidth) / AndroidUtilities.density), (int) (((float) photoHeight) / AndroidUtilities.density), true, 15);
                        this.currentWebFile = WebFile.createWithGeoPoint(point, (int) (((float) photoWidth) / AndroidUtilities.density), (int) (((float) photoHeight) / AndroidUtilities.density), 15, Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                    } else {
                        if (AndroidUtilities.isTablet()) {
                            i = AndroidUtilities.getMinTabletSide();
                            f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                            this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(289.0f));
                        } else {
                            i = AndroidUtilities.displaySize.x;
                            f = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                            this.backgroundWidth = Math.min(i - AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(289.0f));
                        }
                        if (checkNeedDrawShareButton(messageObject)) {
                            this.backgroundWidth -= AndroidUtilities.m9dp(20.0f);
                        }
                        maxWidth = this.backgroundWidth - AndroidUtilities.m9dp(34.0f);
                        this.availableTimeWidth = maxWidth;
                        photoWidth = this.backgroundWidth - AndroidUtilities.m9dp(21.0f);
                        photoHeight = AndroidUtilities.m9dp(195.0f);
                        this.mediaBackground = false;
                        this.currentUrl = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, (int) (((float) photoWidth) / AndroidUtilities.density), (int) (((float) photoHeight) / AndroidUtilities.density), true, 15);
                        this.currentWebFile = WebFile.createWithGeoPoint(point, (int) (((float) photoWidth) / AndroidUtilities.density), (int) (((float) photoHeight) / AndroidUtilities.density), 15, Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                        this.docTitleLayout = StaticLayoutEx.createStaticLayout(messageObject.messageOwner.media.title, Theme.chat_locationTitlePaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TruncateAt.END, maxWidth, 1);
                        additionHeight = 0 + AndroidUtilities.m9dp(50.0f);
                        lineCount = this.docTitleLayout.getLineCount();
                        if (TextUtils.isEmpty(messageObject.messageOwner.media.address)) {
                            this.infoLayout = null;
                        } else {
                            this.infoLayout = StaticLayoutEx.createStaticLayout(messageObject.messageOwner.media.address, Theme.chat_locationAddressPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TruncateAt.END, maxWidth, 1);
                            measureTime(messageObject);
                            if ((this.backgroundWidth - ((int) Math.ceil((double) this.infoLayout.getLineWidth(0)))) - AndroidUtilities.m9dp(24.0f) < AndroidUtilities.m9dp((float) ((messageObject.isOutOwner() ? 20 : 0) + 20)) + this.timeWidth) {
                                additionHeight += AndroidUtilities.m9dp(8.0f);
                            }
                        }
                    }
                    if (((int) messageObject.getDialogId()) != 0) {
                        this.currentMapProvider = MessagesController.getInstance(messageObject.currentAccount).mapProvider;
                    } else if (SharedConfig.mapPreviewType == 0) {
                        this.currentMapProvider = 2;
                    } else if (SharedConfig.mapPreviewType == 1) {
                        this.currentMapProvider = 1;
                    } else {
                        this.currentMapProvider = -1;
                    }
                    if (this.currentMapProvider == -1) {
                        ImageReceiver imageReceiver3 = this.photoImage;
                        Drawable[] drawableArr = Theme.chat_locationDrawable;
                        if (messageObject.isOutOwner()) {
                            dp = 1;
                        } else {
                            dp = 0;
                        }
                        imageReceiver3.setImage(null, null, drawableArr[dp], null, messageObject, 0);
                    } else if (this.currentMapProvider != 2) {
                        if (this.currentMapProvider == 3 || this.currentMapProvider == 4) {
                            ImageLoader.getInstance().addTestWebFile(this.currentUrl, this.currentWebFile);
                            this.addedForTest = true;
                        }
                        if (this.currentUrl != null) {
                            this.photoImage.setImage(this.currentUrl, null, Theme.chat_locationDrawable[messageObject.isOutOwner() ? 1 : 0], null, 0);
                        }
                    } else if (this.currentWebFile != null) {
                        this.photoImage.setImage(this.currentWebFile, null, Theme.chat_locationDrawable[messageObject.isOutOwner() ? 1 : 0], null, messageObject, 0);
                    }
                } else if (messageObject.type == 13) {
                    float maxWidth2;
                    this.drawBackground = false;
                    for (a = 0; a < messageObject.messageOwner.media.document.attributes.size(); a++) {
                        attribute = (DocumentAttribute) messageObject.messageOwner.media.document.attributes.get(a);
                        if (attribute instanceof TL_documentAttributeImageSize) {
                            photoWidth = attribute.var_w;
                            photoHeight = attribute.var_h;
                            break;
                        }
                    }
                    if (AndroidUtilities.isTablet()) {
                        maxWidth2 = ((float) AndroidUtilities.getMinTabletSide()) * 0.4f;
                        maxHeight = maxWidth2;
                    } else {
                        maxWidth2 = ((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f;
                        maxHeight = maxWidth2;
                    }
                    if (photoWidth == 0) {
                        photoHeight = (int) maxHeight;
                        photoWidth = photoHeight + AndroidUtilities.m9dp(100.0f);
                    }
                    photoHeight = (int) (((float) photoHeight) * (maxWidth2 / ((float) photoWidth)));
                    photoWidth = (int) maxWidth2;
                    if (((float) photoHeight) > maxHeight) {
                        photoWidth = (int) (((float) photoWidth) * (maxHeight / ((float) photoHeight)));
                        photoHeight = (int) maxHeight;
                    }
                    this.documentAttachType = 6;
                    this.availableTimeWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.m9dp(14.0f);
                    this.backgroundWidth = AndroidUtilities.m9dp(12.0f) + photoWidth;
                    this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                    if (messageObject.attachPathExists) {
                        this.photoImage.setImage(null, messageObject.messageOwner.attachPath, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoWidth), Integer.valueOf(photoHeight)}), null, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, "b1", messageObject.messageOwner.media.document.size, "webp", messageObject, 1);
                    } else if (messageObject.messageOwner.media.document.var_id != 0) {
                        this.photoImage.setImage(messageObject.messageOwner.media.document, null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoWidth), Integer.valueOf(photoHeight)}), null, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, "b1", messageObject.messageOwner.media.document.size, "webp", messageObject, 1);
                    }
                } else {
                    float hScale;
                    int firstLineWidth;
                    int dWidth;
                    GroupedMessagePosition position;
                    if (messageObject.type == 5) {
                        photoWidth = AndroidUtilities.roundMessageSize;
                        maxPhotoWidth = photoWidth;
                    } else if (AndroidUtilities.isTablet()) {
                        photoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
                        maxPhotoWidth = photoWidth;
                    } else {
                        photoWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
                        maxPhotoWidth = photoWidth;
                    }
                    photoHeight = photoWidth + AndroidUtilities.m9dp(100.0f);
                    if (messageObject.type != 5 && checkNeedDrawShareButton(messageObject)) {
                        maxPhotoWidth -= AndroidUtilities.m9dp(20.0f);
                        photoWidth -= AndroidUtilities.m9dp(20.0f);
                    }
                    if (photoWidth > AndroidUtilities.getPhotoSize()) {
                        photoWidth = AndroidUtilities.getPhotoSize();
                    }
                    if (photoHeight > AndroidUtilities.getPhotoSize()) {
                        photoHeight = AndroidUtilities.getPhotoSize();
                    }
                    if (messageObject.type == 1) {
                        updateSecretTimeText(messageObject);
                        this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                    } else if (messageObject.type == 3) {
                        createDocumentLayout(0, messageObject);
                        updateSecretTimeText(messageObject);
                        if (!messageObject.needDrawBluredPreview()) {
                            this.photoImage.setNeedsQualityThumb(true);
                            this.photoImage.setShouldGenerateQualityThumb(true);
                        }
                    } else if (messageObject.type == 5) {
                        if (!messageObject.needDrawBluredPreview()) {
                            this.photoImage.setNeedsQualityThumb(true);
                            this.photoImage.setShouldGenerateQualityThumb(true);
                        }
                    } else if (messageObject.type == 8) {
                        str = AndroidUtilities.formatFileSize((long) messageObject.messageOwner.media.document.size);
                        this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(str));
                        this.infoLayout = new StaticLayout(str, Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        if (!messageObject.needDrawBluredPreview()) {
                            this.photoImage.setNeedsQualityThumb(true);
                            this.photoImage.setShouldGenerateQualityThumb(true);
                        }
                    }
                    if (this.currentMessagesGroup == null && messageObject.caption != null) {
                        this.mediaBackground = false;
                    }
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                    int w = 0;
                    int h = 0;
                    if (this.currentPhotoObject != null && this.currentPhotoObject == this.currentPhotoObjectThumb) {
                        this.currentPhotoObjectThumb = null;
                    }
                    if (this.currentPhotoObject != null) {
                        scale = ((float) this.currentPhotoObject.var_w) / ((float) photoWidth);
                        w = (int) (((float) this.currentPhotoObject.var_w) / scale);
                        h = (int) (((float) this.currentPhotoObject.var_h) / scale);
                        if (w == 0) {
                            w = AndroidUtilities.m9dp(150.0f);
                        }
                        if (h == 0) {
                            h = AndroidUtilities.m9dp(150.0f);
                        }
                        if (h > photoHeight) {
                            h = photoHeight;
                            w = (int) (((float) w) / (((float) h) / ((float) h)));
                        } else if (h < AndroidUtilities.m9dp(120.0f)) {
                            h = AndroidUtilities.m9dp(120.0f);
                            hScale = ((float) this.currentPhotoObject.var_h) / ((float) h);
                            if (((float) this.currentPhotoObject.var_w) / hScale < ((float) photoWidth)) {
                                w = (int) (((float) this.currentPhotoObject.var_w) / hScale);
                            }
                        }
                    }
                    if (messageObject.type == 5) {
                        h = AndroidUtilities.roundMessageSize;
                        w = h;
                    }
                    if ((w == 0 || h == 0) && messageObject.type == 8) {
                        a = 0;
                        while (a < messageObject.messageOwner.media.document.attributes.size()) {
                            attribute = (DocumentAttribute) messageObject.messageOwner.media.document.attributes.get(a);
                            if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                scale = ((float) attribute.var_w) / ((float) photoWidth);
                                w = (int) (((float) attribute.var_w) / scale);
                                h = (int) (((float) attribute.var_h) / scale);
                                if (h > photoHeight) {
                                    h = photoHeight;
                                    w = (int) (((float) w) / (((float) h) / ((float) h)));
                                } else if (h < AndroidUtilities.m9dp(120.0f)) {
                                    h = AndroidUtilities.m9dp(120.0f);
                                    hScale = ((float) attribute.var_h) / ((float) h);
                                    if (((float) attribute.var_w) / hScale < ((float) photoWidth)) {
                                        w = (int) (((float) attribute.var_w) / hScale);
                                    }
                                }
                            } else {
                                a++;
                            }
                        }
                    }
                    if (w == 0 || h == 0) {
                        h = AndroidUtilities.m9dp(150.0f);
                        w = h;
                    }
                    if (messageObject.type == 3 && w < this.infoWidth + AndroidUtilities.m9dp(40.0f)) {
                        w = this.infoWidth + AndroidUtilities.m9dp(40.0f);
                    }
                    if (this.currentMessagesGroup != null) {
                        firstLineWidth = 0;
                        dWidth = getGroupPhotosWidth();
                        for (a = 0; a < this.currentMessagesGroup.posArray.size(); a++) {
                            position = (GroupedMessagePosition) this.currentMessagesGroup.posArray.get(a);
                            if (position.minY != (byte) 0) {
                                break;
                            }
                            firstLineWidth = (int) (((double) firstLineWidth) + Math.ceil((double) ((((float) (position.var_pw + position.leftSpanOffset)) / 1000.0f) * ((float) dWidth))));
                        }
                        this.availableTimeWidth = firstLineWidth - AndroidUtilities.m9dp(35.0f);
                    } else {
                        this.availableTimeWidth = maxPhotoWidth - AndroidUtilities.m9dp(14.0f);
                    }
                    if (messageObject.type == 5) {
                        this.availableTimeWidth = (int) (((double) this.availableTimeWidth) - (Math.ceil((double) Theme.chat_audioTimePaint.measureText("00:00")) + ((double) AndroidUtilities.m9dp(26.0f))));
                    }
                    measureTime(messageObject);
                    timeWidthTotal = this.timeWidth + AndroidUtilities.m9dp((float) ((messageObject.isOutOwner() ? 20 : 0) + 14));
                    if (w < timeWidthTotal) {
                        w = timeWidthTotal;
                    }
                    if (messageObject.isRoundVideo()) {
                        h = Math.min(w, h);
                        w = h;
                        this.drawBackground = false;
                        this.photoImage.setRoundRadius(w / 2);
                    } else if (messageObject.needDrawBluredPreview()) {
                        if (AndroidUtilities.isTablet()) {
                            h = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                            w = h;
                        } else {
                            h = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f);
                            w = h;
                        }
                    }
                    boolean fixPhotoWidth = false;
                    if (this.currentMessagesGroup != null) {
                        int currentLineWidth;
                        maxHeight = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f;
                        dWidth = getGroupPhotosWidth();
                        w = (int) Math.ceil((double) ((((float) this.currentPosition.var_pw) / 1000.0f) * ((float) dWidth)));
                        if (this.currentPosition.minY != (byte) 0 && ((messageObject.isOutOwner() && (this.currentPosition.flags & 1) != 0) || !(messageObject.isOutOwner() || (this.currentPosition.flags & 2) == 0))) {
                            firstLineWidth = 0;
                            currentLineWidth = 0;
                            for (a = 0; a < this.currentMessagesGroup.posArray.size(); a++) {
                                position = (GroupedMessagePosition) this.currentMessagesGroup.posArray.get(a);
                                if (position.minY == (byte) 0) {
                                    firstLineWidth = (int) (((position.leftSpanOffset != 0 ? Math.ceil((double) ((((float) position.leftSpanOffset) / 1000.0f) * ((float) dWidth))) : 0.0d) + Math.ceil((double) ((((float) position.var_pw) / 1000.0f) * ((float) dWidth)))) + ((double) firstLineWidth));
                                } else if (position.minY == this.currentPosition.minY) {
                                    currentLineWidth = (int) (((position.leftSpanOffset != 0 ? Math.ceil((double) ((((float) position.leftSpanOffset) / 1000.0f) * ((float) dWidth))) : 0.0d) + Math.ceil((double) ((((float) position.var_pw) / 1000.0f) * ((float) dWidth)))) + ((double) currentLineWidth));
                                } else if (position.minY > this.currentPosition.minY) {
                                    break;
                                }
                            }
                            w += firstLineWidth - currentLineWidth;
                        }
                        w -= AndroidUtilities.m9dp(9.0f);
                        if (this.isAvatarVisible) {
                            w -= AndroidUtilities.m9dp(48.0f);
                        }
                        if (this.currentPosition.siblingHeights != null) {
                            h = 0;
                            for (float f2 : this.currentPosition.siblingHeights) {
                                h += (int) Math.ceil((double) (f2 * maxHeight));
                            }
                            h += (this.currentPosition.maxY - this.currentPosition.minY) * AndroidUtilities.m9dp(11.0f);
                        } else {
                            h = (int) Math.ceil((double) (this.currentPosition.var_ph * maxHeight));
                        }
                        this.backgroundWidth = w;
                        w -= AndroidUtilities.m9dp(12.0f);
                        photoWidth = w;
                        if (!this.currentPosition.edge) {
                            photoWidth += AndroidUtilities.m9dp(10.0f);
                        }
                        photoHeight = h;
                        widthForCaption = 0 + (photoWidth - AndroidUtilities.m9dp(10.0f));
                        if ((this.currentPosition.flags & 8) != 0 || (this.currentMessagesGroup.hasSibling && (this.currentPosition.flags & 4) == 0)) {
                            widthForCaption += getAdditionalWidthForPosition(this.currentPosition);
                            count = this.currentMessagesGroup.messages.size();
                            for (int i3 = 0; i3 < count; i3++) {
                                MessageObject m = (MessageObject) this.currentMessagesGroup.messages.get(i3);
                                GroupedMessagePosition rowPosition = (GroupedMessagePosition) this.currentMessagesGroup.posArray.get(i3);
                                if (!(rowPosition == this.currentPosition || (rowPosition.flags & 8) == 0)) {
                                    w = (int) Math.ceil((double) ((((float) rowPosition.var_pw) / 1000.0f) * ((float) dWidth)));
                                    if (rowPosition.minY != (byte) 0 && ((messageObject.isOutOwner() && (rowPosition.flags & 1) != 0) || !(messageObject.isOutOwner() || (rowPosition.flags & 2) == 0))) {
                                        firstLineWidth = 0;
                                        currentLineWidth = 0;
                                        for (a = 0; a < this.currentMessagesGroup.posArray.size(); a++) {
                                            position = (GroupedMessagePosition) this.currentMessagesGroup.posArray.get(a);
                                            if (position.minY == (byte) 0) {
                                                firstLineWidth = (int) (((position.leftSpanOffset != 0 ? Math.ceil((double) ((((float) position.leftSpanOffset) / 1000.0f) * ((float) dWidth))) : 0.0d) + Math.ceil((double) ((((float) position.var_pw) / 1000.0f) * ((float) dWidth)))) + ((double) firstLineWidth));
                                            } else if (position.minY == rowPosition.minY) {
                                                currentLineWidth = (int) (((position.leftSpanOffset != 0 ? Math.ceil((double) ((((float) position.leftSpanOffset) / 1000.0f) * ((float) dWidth))) : 0.0d) + Math.ceil((double) ((((float) position.var_pw) / 1000.0f) * ((float) dWidth)))) + ((double) currentLineWidth));
                                            } else if (position.minY > rowPosition.minY) {
                                                break;
                                            }
                                        }
                                        w += firstLineWidth - currentLineWidth;
                                    }
                                    w -= AndroidUtilities.m9dp(18.0f);
                                    if (this.isChat && !m.isOutOwner() && m.needDrawAvatar() && (rowPosition == null || rowPosition.edge)) {
                                        w -= AndroidUtilities.m9dp(48.0f);
                                    }
                                    w += getAdditionalWidthForPosition(rowPosition);
                                    if (!rowPosition.edge) {
                                        w += AndroidUtilities.m9dp(10.0f);
                                    }
                                    widthForCaption += w;
                                    if (rowPosition.minX < this.currentPosition.minX || (this.currentMessagesGroup.hasSibling && rowPosition.minY != rowPosition.maxY)) {
                                        this.captionOffsetX -= w;
                                    }
                                }
                                if (m.caption != null) {
                                    if (this.currentCaption != null) {
                                        this.currentCaption = null;
                                        break;
                                    }
                                    this.currentCaption = m.caption;
                                }
                            }
                        }
                    } else {
                        int minCaptionWidth;
                        photoHeight = h;
                        photoWidth = w;
                        this.currentCaption = messageObject.caption;
                        if (AndroidUtilities.isTablet()) {
                            minCaptionWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.65f);
                        } else {
                            minCaptionWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.65f);
                        }
                        if (messageObject.needDrawBluredPreview() || this.currentCaption == null || photoWidth >= minCaptionWidth) {
                            widthForCaption = photoWidth - AndroidUtilities.m9dp(10.0f);
                        } else {
                            widthForCaption = minCaptionWidth;
                            fixPhotoWidth = true;
                        }
                        this.backgroundWidth = AndroidUtilities.m9dp(12.0f) + photoWidth;
                        if (!this.mediaBackground) {
                            this.backgroundWidth += AndroidUtilities.m9dp(9.0f);
                        }
                    }
                    if (this.currentCaption != null) {
                        try {
                            if (VERSION.SDK_INT >= 24) {
                                this.captionLayout = Builder.obtain(this.currentCaption, 0, this.currentCaption.length(), Theme.chat_msgTextPaint, widthForCaption).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Alignment.ALIGN_NORMAL).build();
                            } else {
                                this.captionLayout = new StaticLayout(this.currentCaption, Theme.chat_msgTextPaint, widthForCaption, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            }
                            lineCount = this.captionLayout.getLineCount();
                            if (lineCount > 0) {
                                if (fixPhotoWidth) {
                                    this.captionWidth = 0;
                                    for (a = 0; a < lineCount; a++) {
                                        this.captionWidth = (int) Math.max((double) this.captionWidth, Math.ceil((double) this.captionLayout.getLineWidth(a)));
                                        if (this.captionLayout.getLineLeft(a) != 0.0f) {
                                            this.captionWidth = widthForCaption;
                                            break;
                                        }
                                    }
                                } else {
                                    this.captionWidth = widthForCaption;
                                }
                                this.captionHeight = this.captionLayout.getHeight();
                                this.addedCaptionHeight = this.captionHeight + AndroidUtilities.m9dp(9.0f);
                                if (this.currentPosition == null || (this.currentPosition.flags & 8) != 0) {
                                    additionHeight = 0 + this.addedCaptionHeight;
                                    if (((float) (AndroidUtilities.m9dp(2.0f) + Math.max(this.captionWidth, photoWidth - AndroidUtilities.m9dp(10.0f)))) - (this.captionLayout.getLineWidth(this.captionLayout.getLineCount() - 1) + this.captionLayout.getLineLeft(this.captionLayout.getLineCount() - 1)) < ((float) timeWidthTotal)) {
                                        additionHeight += AndroidUtilities.m9dp(14.0f);
                                        this.addedCaptionHeight += AndroidUtilities.m9dp(14.0f);
                                        captionNewLine = 1;
                                    }
                                } else {
                                    this.captionLayout = null;
                                }
                            }
                        } catch (Throwable e22222) {
                            FileLog.m13e(e22222);
                        }
                    }
                    if (fixPhotoWidth && photoWidth < this.captionWidth + AndroidUtilities.m9dp(10.0f)) {
                        photoWidth = this.captionWidth + AndroidUtilities.m9dp(10.0f);
                        this.backgroundWidth = AndroidUtilities.m9dp(12.0f) + photoWidth;
                        if (!this.mediaBackground) {
                            this.backgroundWidth += AndroidUtilities.m9dp(9.0f);
                        }
                    }
                    String format = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) w) / AndroidUtilities.density)), Integer.valueOf((int) (((float) h) / AndroidUtilities.density))});
                    this.currentPhotoFilterThumb = format;
                    this.currentPhotoFilter = format;
                    if ((messageObject.photoThumbs != null && messageObject.photoThumbs.size() > 1) || messageObject.type == 3 || messageObject.type == 8 || messageObject.type == 5) {
                        if (messageObject.needDrawBluredPreview()) {
                            this.currentPhotoFilter += "_b2";
                            this.currentPhotoFilterThumb += "_b2";
                        } else {
                            this.currentPhotoFilterThumb += "_b";
                        }
                    }
                    boolean noSize = false;
                    if (messageObject.type == 3 || messageObject.type == 8 || messageObject.type == 5) {
                        noSize = true;
                    }
                    if (!(this.currentPhotoObject == null || noSize || this.currentPhotoObject.size != 0)) {
                        this.currentPhotoObject.size = -1;
                    }
                    if (messageObject.type == 1) {
                        if (messageObject.useCustomPhoto) {
                            this.photoImage.setImageBitmap(getResources().getDrawable(R.drawable.theme_preview_image));
                        } else if (this.currentPhotoObject != null) {
                            photoExist = true;
                            fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                            if (messageObject.mediaExists) {
                                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                            } else {
                                photoExist = false;
                            }
                            if (photoExist || DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject) || FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                                this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, this.currentPhotoFilterThumb, noSize ? 0 : this.currentPhotoObject.size, null, this.currentMessageObject, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
                            } else {
                                this.photoNotSet = true;
                                if (this.currentPhotoObjectThumb != null) {
                                    this.photoImage.setImage(null, null, this.currentPhotoObjectThumb.location, this.currentPhotoFilterThumb, 0, null, this.currentMessageObject, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
                                } else {
                                    this.photoImage.setImageBitmap((Drawable) null);
                                }
                            }
                        } else {
                            this.photoImage.setImageBitmap((Drawable) null);
                        }
                    } else if (messageObject.type == 8 || messageObject.type == 5) {
                        fileName = FileLoader.getAttachFileName(messageObject.messageOwner.media.document);
                        int localFile = 0;
                        if (messageObject.attachPathExists) {
                            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                            localFile = 1;
                        } else if (messageObject.mediaExists) {
                            localFile = 2;
                        }
                        autoDownload = false;
                        if (MessageObject.isNewGifDocument(messageObject.messageOwner.media.document)) {
                            autoDownload = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject);
                        } else if (messageObject.type == 5) {
                            autoDownload = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject);
                        }
                        if (messageObject.isSending() || messageObject.isEditing() || !(localFile != 0 || FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName) || autoDownload)) {
                            this.photoNotSet = true;
                            this.photoImage.setImage(null, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilterThumb, 0, null, messageObject, 0);
                        } else if (localFile == 1) {
                            String str4;
                            FileLocation fileLocation2;
                            ImageReceiver imageReceiver4 = this.photoImage;
                            if (messageObject.isSendError()) {
                                str4 = null;
                            } else {
                                str4 = messageObject.messageOwner.attachPath;
                            }
                            if (this.currentPhotoObject != null) {
                                fileLocation2 = this.currentPhotoObject.location;
                            } else {
                                fileLocation2 = null;
                            }
                            imageReceiver4.setImage(null, str4, null, null, fileLocation2, this.currentPhotoFilterThumb, 0, null, messageObject, 0);
                        } else {
                            this.photoImage.setImage(messageObject.messageOwner.media.document, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilterThumb, messageObject.messageOwner.media.document.size, null, messageObject, 0);
                        }
                    } else {
                        this.photoImage.setImage(null, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilterThumb, 0, null, messageObject, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
                    }
                }
                setMessageObjectInternal(messageObject);
                if (this.drawForwardedName && messageObject.needDrawForwarded() && (this.currentPosition == null || this.currentPosition.minY == (byte) 0)) {
                    if (messageObject.type != 5) {
                        this.namesOffset += AndroidUtilities.m9dp(5.0f);
                    }
                } else if (this.drawNameLayout && messageObject.messageOwner.reply_to_msg_id == 0) {
                    this.namesOffset += AndroidUtilities.m9dp(7.0f);
                }
                this.totalHeight = ((AndroidUtilities.m9dp(14.0f) + photoHeight) + this.namesOffset) + additionHeight;
                if (this.currentPosition != null && (this.currentPosition.flags & 8) == 0) {
                    this.totalHeight -= AndroidUtilities.m9dp(3.0f);
                }
                int additionalTop = 0;
                if (this.currentPosition != null) {
                    photoWidth += getAdditionalWidthForPosition(this.currentPosition);
                    if ((this.currentPosition.flags & 4) == 0) {
                        photoHeight += AndroidUtilities.m9dp(4.0f);
                        additionalTop = 0 - AndroidUtilities.m9dp(4.0f);
                    }
                    if ((this.currentPosition.flags & 8) == 0) {
                        photoHeight += AndroidUtilities.m9dp(4.0f);
                    }
                }
                if (this.drawPinnedTop) {
                    this.namesOffset -= AndroidUtilities.m9dp(1.0f);
                }
                this.photoImage.setImageCoords(0, (AndroidUtilities.m9dp(7.0f) + this.namesOffset) + additionalTop, photoWidth, photoHeight);
                invalidate();
            }
            if (this.currentPosition != null && this.captionLayout == null && messageObject.caption != null && messageObject.type != 13) {
                try {
                    this.currentCaption = messageObject.caption;
                    width = this.backgroundWidth - AndroidUtilities.m9dp(31.0f);
                    widthForCaption = width - AndroidUtilities.m9dp(10.0f);
                    if (VERSION.SDK_INT >= 24) {
                        this.captionLayout = Builder.obtain(messageObject.caption, 0, messageObject.caption.length(), Theme.chat_msgTextPaint, widthForCaption).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Alignment.ALIGN_NORMAL).build();
                    } else {
                        this.captionLayout = new StaticLayout(messageObject.caption, Theme.chat_msgTextPaint, widthForCaption, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }
                    if (this.captionLayout.getLineCount() > 0) {
                        this.captionWidth = widthForCaption;
                        timeWidthTotal = this.timeWidth + (messageObject.isOutOwner() ? AndroidUtilities.m9dp(20.0f) : 0);
                        this.captionHeight = this.captionLayout.getHeight();
                        this.totalHeight += this.captionHeight + AndroidUtilities.m9dp(9.0f);
                        if (((float) (width - AndroidUtilities.m9dp(8.0f))) - (this.captionLayout.getLineWidth(this.captionLayout.getLineCount() - 1) + this.captionLayout.getLineLeft(this.captionLayout.getLineCount() - 1)) < ((float) timeWidthTotal)) {
                            this.totalHeight += AndroidUtilities.m9dp(14.0f);
                            this.captionHeight += AndroidUtilities.m9dp(14.0f);
                            captionNewLine = 2;
                        }
                    }
                } catch (Throwable e222222) {
                    FileLog.m13e(e222222);
                }
            } else if (this.widthBeforeNewTimeLine != -1 && this.availableTimeWidth - this.widthBeforeNewTimeLine < this.timeWidth) {
                this.totalHeight += AndroidUtilities.m9dp(14.0f);
            }
            if (!(this.currentMessageObject.eventId == 0 || this.currentMessageObject.isMediaEmpty() || this.currentMessageObject.messageOwner.media.webpage == null)) {
                linkPreviewMaxWidth = this.backgroundWidth - AndroidUtilities.m9dp(41.0f);
                this.hasOldCaptionPreview = true;
                this.linkPreviewHeight = 0;
                webPage = this.currentMessageObject.messageOwner.media.webpage;
                width = (int) Math.ceil((double) (Theme.chat_replyNamePaint.measureText(webPage.site_name) + 1.0f));
                this.siteNameWidth = width;
                this.siteNameLayout = new StaticLayout(webPage.site_name, Theme.chat_replyNamePaint, Math.min(width, linkPreviewMaxWidth), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.siteNameRtl = this.siteNameLayout.getLineLeft(0) == 0.0f;
                height = this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                this.linkPreviewHeight += height;
                this.totalHeight += height;
                this.descriptionX = 0;
                if (this.linkPreviewHeight != 0) {
                    this.totalHeight += AndroidUtilities.m9dp(2.0f);
                }
                this.descriptionLayout = StaticLayoutEx.createStaticLayout(webPage.description, Theme.chat_replyTextPaint, linkPreviewMaxWidth, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.m9dp(1.0f), false, TruncateAt.END, linkPreviewMaxWidth, 6);
                height = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                this.linkPreviewHeight += height;
                this.totalHeight += height;
                for (a = 0; a < this.descriptionLayout.getLineCount(); a++) {
                    lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                    if (lineLeft != 0) {
                        if (this.descriptionX == 0) {
                            this.descriptionX = -lineLeft;
                        } else {
                            this.descriptionX = Math.max(this.descriptionX, -lineLeft);
                        }
                    }
                }
                this.totalHeight += AndroidUtilities.m9dp(17.0f);
                if (captionNewLine != 0) {
                    this.totalHeight -= AndroidUtilities.m9dp(14.0f);
                    if (captionNewLine == 2) {
                        this.captionHeight -= AndroidUtilities.m9dp(14.0f);
                    }
                }
            }
            this.botButtons.clear();
            if (messageIdChanged) {
                this.botButtonsByData.clear();
                this.botButtonsByPosition.clear();
                this.botButtonsLayout = null;
            }
            if (this.currentPosition == null || !(messageObject.messageOwner.reply_markup instanceof TL_replyInlineMarkup)) {
                this.substractBackgroundHeight = 0;
                this.keyboardHeight = 0;
            } else {
                HashMap<String, BotButton> oldByPosition;
                int rows = messageObject.messageOwner.reply_markup.rows.size();
                dp = (AndroidUtilities.m9dp(48.0f) * rows) + AndroidUtilities.m9dp(1.0f);
                this.keyboardHeight = dp;
                this.substractBackgroundHeight = dp;
                this.widthForButtons = this.backgroundWidth - AndroidUtilities.m9dp(this.mediaBackground ? 0.0f : 9.0f);
                if (messageObject.wantedBotKeyboardWidth > this.widthForButtons) {
                    f2 = (this.isChat && messageObject.needDrawAvatar() && !messageObject.isOutOwner()) ? 62.0f : 10.0f;
                    int maxButtonWidth = -AndroidUtilities.m9dp(f2);
                    if (AndroidUtilities.isTablet()) {
                        maxButtonWidth += AndroidUtilities.getMinTabletSide();
                    } else {
                        maxButtonWidth += Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.m9dp(5.0f);
                    }
                    this.widthForButtons = Math.max(this.backgroundWidth, Math.min(messageObject.wantedBotKeyboardWidth, maxButtonWidth));
                }
                int maxButtonsWidth = 0;
                HashMap<String, BotButton> hashMap = new HashMap(this.botButtonsByData);
                if (messageObject.botButtonsLayout == null || this.botButtonsLayout == null || !this.botButtonsLayout.equals(messageObject.botButtonsLayout.toString())) {
                    if (messageObject.botButtonsLayout != null) {
                        this.botButtonsLayout = messageObject.botButtonsLayout.toString();
                    }
                    oldByPosition = null;
                } else {
                    hashMap = new HashMap(this.botButtonsByPosition);
                }
                this.botButtonsByData.clear();
                for (a = 0; a < rows; a++) {
                    TL_keyboardButtonRow row = (TL_keyboardButtonRow) messageObject.messageOwner.reply_markup.rows.get(a);
                    int buttonsCount = row.buttons.size();
                    if (buttonsCount != 0) {
                        int buttonWidth = ((this.widthForButtons - (AndroidUtilities.m9dp(5.0f) * (buttonsCount - 1))) - AndroidUtilities.m9dp(2.0f)) / buttonsCount;
                        for (int b = 0; b < row.buttons.size(); b++) {
                            BotButton oldButton;
                            CharSequence buttonText;
                            BotButton botButton = new BotButton(this, null);
                            botButton.button = (KeyboardButton) row.buttons.get(b);
                            String key = Utilities.bytesToHex(botButton.button.data);
                            String position2 = a + TtmlNode.ANONYMOUS_REGION_ID + b;
                            if (oldByPosition != null) {
                                oldButton = (BotButton) oldByPosition.get(position2);
                            } else {
                                oldButton = (BotButton) hashMap.get(key);
                            }
                            if (oldButton != null) {
                                botButton.progressAlpha = oldButton.progressAlpha;
                                botButton.angle = oldButton.angle;
                                botButton.lastUpdateTime = oldButton.lastUpdateTime;
                            } else {
                                botButton.lastUpdateTime = System.currentTimeMillis();
                            }
                            this.botButtonsByData.put(key, botButton);
                            this.botButtonsByPosition.put(position2, botButton);
                            botButton.var_x = (AndroidUtilities.m9dp(5.0f) + buttonWidth) * b;
                            botButton.var_y = (AndroidUtilities.m9dp(48.0f) * a) + AndroidUtilities.m9dp(5.0f);
                            botButton.width = buttonWidth;
                            botButton.height = AndroidUtilities.m9dp(44.0f);
                            if (!(botButton.button instanceof TL_keyboardButtonBuy) || (messageObject.messageOwner.media.flags & 4) == 0) {
                                buttonText = TextUtils.ellipsize(Emoji.replaceEmoji(botButton.button.text, Theme.chat_botButtonPaint.getFontMetricsInt(), AndroidUtilities.m9dp(15.0f), false), Theme.chat_botButtonPaint, (float) (buttonWidth - AndroidUtilities.m9dp(10.0f)), TruncateAt.END);
                            } else {
                                buttonText = LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt);
                            }
                            botButton.title = new StaticLayout(buttonText, Theme.chat_botButtonPaint, buttonWidth - AndroidUtilities.m9dp(10.0f), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            this.botButtons.add(botButton);
                            if (b == row.buttons.size() - 1) {
                                maxButtonsWidth = Math.max(maxButtonsWidth, botButton.var_x + botButton.width);
                            }
                        }
                    }
                }
                this.widthForButtons = maxButtonsWidth;
            }
            if (!this.drawPinnedBottom && this.drawPinnedTop) {
                this.totalHeight -= AndroidUtilities.m9dp(2.0f);
            } else if (this.drawPinnedBottom) {
                this.totalHeight -= AndroidUtilities.m9dp(1.0f);
            } else if (this.drawPinnedTop && this.pinnedBottom && this.currentPosition != null && this.currentPosition.siblingHeights == null) {
                this.totalHeight -= AndroidUtilities.m9dp(1.0f);
            }
            if (messageObject.type == 13 && this.totalHeight < AndroidUtilities.m9dp(70.0f)) {
                this.totalHeight = AndroidUtilities.m9dp(70.0f);
            }
            if (this.drawPhotoImage) {
                this.photoImage.setImageBitmap((Drawable) null);
            }
        }
        updateWaveform();
        z = dataChanged && !messageObject.cancelEditing;
        updateButtonState(z, true);
    }

    private int getAdditionalWidthForPosition(GroupedMessagePosition position) {
        int w = 0;
        if (position == null) {
            return 0;
        }
        if ((position.flags & 2) == 0) {
            w = 0 + AndroidUtilities.m9dp(4.0f);
        }
        if ((position.flags & 1) == 0) {
            return w + AndroidUtilities.m9dp(4.0f);
        }
        return w;
    }

    private void createInstantViewButton() {
        if (VERSION.SDK_INT >= 21 && this.drawInstantView) {
            if (this.instantViewSelectorDrawable == null) {
                final Paint maskPaint = new Paint(1);
                maskPaint.setColor(-1);
                Drawable maskDrawable = new Drawable() {
                    RectF rect = new RectF();

                    public void draw(Canvas canvas) {
                        Rect bounds = getBounds();
                        this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(6.0f), (float) AndroidUtilities.m9dp(6.0f), maskPaint);
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
                iArr2[0] = Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewInstantText : Theme.key_chat_inPreviewInstantText) & NUM;
                this.instantViewSelectorDrawable = new RippleDrawable(new ColorStateList(iArr, iArr2), null, maskDrawable);
                this.instantViewSelectorDrawable.setCallback(this);
            } else {
                Theme.setSelectorDrawableColor(this.instantViewSelectorDrawable, Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewInstantText : Theme.key_chat_inPreviewInstantText) & NUM, true);
            }
            this.instantViewSelectorDrawable.setVisible(true, false);
        }
        if (this.drawInstantView && this.instantViewLayout == null) {
            String str;
            this.instantWidth = AndroidUtilities.m9dp(33.0f);
            if (this.drawInstantViewType == 1) {
                str = LocaleController.getString("OpenChannel", R.string.OpenChannel);
            } else if (this.drawInstantViewType == 2) {
                str = LocaleController.getString("OpenGroup", R.string.OpenGroup);
            } else if (this.drawInstantViewType == 3) {
                str = LocaleController.getString("OpenMessage", R.string.OpenMessage);
            } else if (this.drawInstantViewType == 5) {
                str = LocaleController.getString("ViewContact", R.string.ViewContact);
            } else {
                str = LocaleController.getString("InstantView", R.string.InstantView);
            }
            int mWidth = this.backgroundWidth - AndroidUtilities.m9dp(75.0f);
            this.instantViewLayout = new StaticLayout(TextUtils.ellipsize(str, Theme.chat_instantViewPaint, (float) mWidth, TruncateAt.END), Theme.chat_instantViewPaint, mWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.instantWidth = this.backgroundWidth - AndroidUtilities.m9dp(34.0f);
            this.totalHeight += AndroidUtilities.m9dp(46.0f);
            if (this.currentMessageObject.type == 12) {
                this.totalHeight += AndroidUtilities.m9dp(14.0f);
            }
            if (this.instantViewLayout != null && this.instantViewLayout.getLineCount() > 0) {
                int dp;
                int ceil = ((int) (((double) this.instantWidth) - Math.ceil((double) this.instantViewLayout.getLineWidth(0)))) / 2;
                if (this.drawInstantViewType == 0) {
                    dp = AndroidUtilities.m9dp(8.0f);
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
        if (this.currentMessageObject != null && (this.currentMessageObject.checkLayout() || !(this.currentPosition == null || this.lastHeight == AndroidUtilities.displaySize.y))) {
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
        if (leftWidth < AndroidUtilities.m9dp(320.0f)) {
            leftWidth = AndroidUtilities.m9dp(320.0f);
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
                    this.timeTextWidth = AndroidUtilities.m9dp(10.0f);
                }
                this.timeLayout = new StaticLayout(this.currentTimeString, Theme.chat_timePaint, this.timeTextWidth + AndroidUtilities.m9dp(100.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.mediaBackground) {
                    if (this.currentMessageObject.isOutOwner()) {
                        this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.m9dp(42.0f);
                    } else {
                        this.timeX = (this.isAvatarVisible ? AndroidUtilities.m9dp(48.0f) : 0) + ((this.backgroundWidth - AndroidUtilities.m9dp(4.0f)) - this.timeWidth);
                        if (!(this.currentPosition == null || this.currentPosition.leftSpanOffset == 0)) {
                            this.timeX += (int) Math.ceil((double) ((((float) this.currentPosition.leftSpanOffset) / 1000.0f) * ((float) getGroupPhotosWidth())));
                        }
                    }
                } else if (this.currentMessageObject.isOutOwner()) {
                    this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.m9dp(38.5f);
                } else {
                    int dp;
                    int dp2 = (this.backgroundWidth - AndroidUtilities.m9dp(9.0f)) - this.timeWidth;
                    if (this.isAvatarVisible) {
                        dp = AndroidUtilities.m9dp(48.0f);
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
                    this.avatarImage.setImageCoords(AndroidUtilities.m9dp(6.0f), this.avatarImage.getImageY(), AndroidUtilities.m9dp(42.0f), AndroidUtilities.m9dp(42.0f));
                }
                this.wasLayout = true;
            }
            if (this.currentMessageObject.type == 0) {
                this.textY = AndroidUtilities.m9dp(10.0f) + this.namesOffset;
            }
            if (this.currentMessageObject.isRoundVideo()) {
                updatePlayingMessageProgress();
            }
            int x;
            if (this.documentAttachType == 3) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.m9dp(57.0f);
                    this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.m9dp(14.0f);
                    this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.m9dp(67.0f);
                } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
                    this.seekBarX = AndroidUtilities.m9dp(114.0f);
                    this.buttonX = AndroidUtilities.m9dp(71.0f);
                    this.timeAudioX = AndroidUtilities.m9dp(124.0f);
                } else {
                    this.seekBarX = AndroidUtilities.m9dp(66.0f);
                    this.buttonX = AndroidUtilities.m9dp(23.0f);
                    this.timeAudioX = AndroidUtilities.m9dp(76.0f);
                }
                if (this.hasLinkPreview) {
                    this.seekBarX += AndroidUtilities.m9dp(10.0f);
                    this.buttonX += AndroidUtilities.m9dp(10.0f);
                    this.timeAudioX += AndroidUtilities.m9dp(10.0f);
                }
                this.seekBarWaveform.setSize(this.backgroundWidth - AndroidUtilities.m9dp((float) ((this.hasLinkPreview ? 10 : 0) + 92)), AndroidUtilities.m9dp(30.0f));
                this.seekBar.setSize(this.backgroundWidth - AndroidUtilities.m9dp((float) ((this.hasLinkPreview ? 10 : 0) + 72)), AndroidUtilities.m9dp(30.0f));
                this.seekBarY = (AndroidUtilities.m9dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
                this.buttonY = (AndroidUtilities.m9dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.m9dp(44.0f), this.buttonY + AndroidUtilities.m9dp(44.0f));
                updatePlayingMessageProgress();
            } else if (this.documentAttachType == 5) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.m9dp(56.0f);
                    this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.m9dp(14.0f);
                    this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.m9dp(67.0f);
                } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
                    this.seekBarX = AndroidUtilities.m9dp(113.0f);
                    this.buttonX = AndroidUtilities.m9dp(71.0f);
                    this.timeAudioX = AndroidUtilities.m9dp(124.0f);
                } else {
                    this.seekBarX = AndroidUtilities.m9dp(65.0f);
                    this.buttonX = AndroidUtilities.m9dp(23.0f);
                    this.timeAudioX = AndroidUtilities.m9dp(76.0f);
                }
                if (this.hasLinkPreview) {
                    this.seekBarX += AndroidUtilities.m9dp(10.0f);
                    this.buttonX += AndroidUtilities.m9dp(10.0f);
                    this.timeAudioX += AndroidUtilities.m9dp(10.0f);
                }
                this.seekBar.setSize(this.backgroundWidth - AndroidUtilities.m9dp((float) ((this.hasLinkPreview ? 10 : 0) + 65)), AndroidUtilities.m9dp(30.0f));
                this.seekBarY = (AndroidUtilities.m9dp(29.0f) + this.namesOffset) + this.mediaOffsetY;
                this.buttonY = (AndroidUtilities.m9dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.m9dp(44.0f), this.buttonY + AndroidUtilities.m9dp(44.0f));
                updatePlayingMessageProgress();
            } else if (this.documentAttachType == 1 && !this.drawPhotoImage) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.m9dp(14.0f);
                } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
                    this.buttonX = AndroidUtilities.m9dp(71.0f);
                } else {
                    this.buttonX = AndroidUtilities.m9dp(23.0f);
                }
                if (this.hasLinkPreview) {
                    this.buttonX += AndroidUtilities.m9dp(10.0f);
                }
                this.buttonY = (AndroidUtilities.m9dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.m9dp(44.0f), this.buttonY + AndroidUtilities.m9dp(44.0f));
                this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.m9dp(10.0f), this.buttonY - AndroidUtilities.m9dp(10.0f), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
            } else if (this.currentMessageObject.type == 12) {
                if (this.currentMessageObject.isOutOwner()) {
                    x = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.m9dp(14.0f);
                } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
                    x = AndroidUtilities.m9dp(72.0f);
                } else {
                    x = AndroidUtilities.m9dp(23.0f);
                }
                this.photoImage.setImageCoords(x, AndroidUtilities.m9dp(13.0f) + this.namesOffset, AndroidUtilities.m9dp(44.0f), AndroidUtilities.m9dp(44.0f));
            } else {
                if (this.currentMessageObject.type == 0 && (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview)) {
                    int linkX;
                    if (this.hasGamePreview) {
                        linkX = this.unmovedTextX - AndroidUtilities.m9dp(10.0f);
                    } else if (this.hasInvoicePreview) {
                        linkX = this.unmovedTextX + AndroidUtilities.m9dp(1.0f);
                    } else {
                        linkX = this.unmovedTextX + AndroidUtilities.m9dp(1.0f);
                    }
                    if (this.isSmallImage) {
                        x = (this.backgroundWidth + linkX) - AndroidUtilities.m9dp(81.0f);
                    } else {
                        x = linkX + (this.hasInvoicePreview ? -AndroidUtilities.m9dp(6.3f) : AndroidUtilities.m9dp(10.0f));
                    }
                } else if (!this.currentMessageObject.isOutOwner()) {
                    if (this.isChat && this.isAvatarVisible) {
                        x = AndroidUtilities.m9dp(63.0f);
                    } else {
                        x = AndroidUtilities.m9dp(15.0f);
                    }
                    if (!(this.currentPosition == null || this.currentPosition.edge)) {
                        x -= AndroidUtilities.m9dp(10.0f);
                    }
                } else if (this.mediaBackground) {
                    x = (this.layoutWidth - this.backgroundWidth) - AndroidUtilities.m9dp(3.0f);
                } else {
                    x = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.m9dp(6.0f);
                }
                if (this.currentPosition != null) {
                    if ((this.currentPosition.flags & 1) == 0) {
                        x -= AndroidUtilities.m9dp(4.0f);
                    }
                    if (this.currentPosition.leftSpanOffset != 0) {
                        x += (int) Math.ceil((double) ((((float) this.currentPosition.leftSpanOffset) / 1000.0f) * ((float) getGroupPhotosWidth())));
                    }
                }
                this.photoImage.setImageCoords(x, this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                this.buttonX = (int) (((float) x) + (((float) (this.photoImage.getImageWidth() - AndroidUtilities.m9dp(48.0f))) / 2.0f));
                this.buttonY = this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - AndroidUtilities.m9dp(48.0f)) / 2);
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.m9dp(48.0f), this.buttonY + AndroidUtilities.m9dp(48.0f));
                this.deleteProgressRect.set((float) (this.buttonX + AndroidUtilities.m9dp(3.0f)), (float) (this.buttonY + AndroidUtilities.m9dp(3.0f)), (float) (this.buttonX + AndroidUtilities.m9dp(45.0f)), (float) (this.buttonY + AndroidUtilities.m9dp(45.0f)));
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
        float progress;
        Drawable drawable;
        int imageY;
        int x1;
        int y1;
        RadialProgress radialProgress;
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
        this.radialProgress.setHideCurrentDrawable(false);
        this.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
        boolean imageDrawn = false;
        if (this.currentMessageObject.type == 0) {
            int b;
            if (this.currentMessageObject.isOutOwner()) {
                this.textX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.m9dp(11.0f);
            } else {
                i = this.currentBackgroundDrawable.getBounds().left;
                f = (this.mediaBackground || !this.drawPinnedBottom) ? 17.0f : 11.0f;
                this.textX = AndroidUtilities.m9dp(f) + i;
            }
            if (this.hasGamePreview) {
                this.textX += AndroidUtilities.m9dp(11.0f);
                this.textY = AndroidUtilities.m9dp(14.0f) + this.namesOffset;
                if (this.siteNameLayout != null) {
                    this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                }
            } else if (this.hasInvoicePreview) {
                this.textY = AndroidUtilities.m9dp(14.0f) + this.namesOffset;
                if (this.siteNameLayout != null) {
                    this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                }
            } else {
                this.textY = AndroidUtilities.m9dp(10.0f) + this.namesOffset;
            }
            this.unmovedTextX = this.textX;
            if (!(this.currentMessageObject.textXOffset == 0.0f || this.replyNameLayout == null)) {
                int diff = (this.backgroundWidth - AndroidUtilities.m9dp(31.0f)) - this.currentMessageObject.textWidth;
                if (!this.hasNewLineForTime) {
                    diff -= AndroidUtilities.m9dp((float) ((this.currentMessageObject.isOutOwner() ? 20 : 0) + 4)) + this.timeWidth;
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
                            FileLog.m13e(e);
                        }
                        canvas.restore();
                        a++;
                    }
                }
            }
            if (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview) {
                int size;
                if (this.hasGamePreview) {
                    startY = AndroidUtilities.m9dp(14.0f) + this.namesOffset;
                    linkX = this.unmovedTextX - AndroidUtilities.m9dp(10.0f);
                } else if (this.hasInvoicePreview) {
                    startY = AndroidUtilities.m9dp(14.0f) + this.namesOffset;
                    linkX = this.unmovedTextX + AndroidUtilities.m9dp(1.0f);
                } else {
                    startY = (this.textY + this.currentMessageObject.textHeight) + AndroidUtilities.m9dp(8.0f);
                    linkX = this.unmovedTextX + AndroidUtilities.m9dp(1.0f);
                }
                linkPreviewY = startY;
                int smallImageStartY = 0;
                if (!this.hasInvoicePreview) {
                    Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                    canvas.drawRect((float) linkX, (float) (linkPreviewY - AndroidUtilities.m9dp(3.0f)), (float) (AndroidUtilities.m9dp(2.0f) + linkX), (float) ((this.linkPreviewHeight + linkPreviewY) + AndroidUtilities.m9dp(3.0f)), Theme.chat_replyLinePaint);
                }
                if (this.siteNameLayout != null) {
                    Theme.chat_replyNamePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                    canvas.save();
                    if (this.siteNameRtl) {
                        x = (this.backgroundWidth - this.siteNameWidth) - AndroidUtilities.m9dp(32.0f);
                    } else if (this.hasInvoicePreview) {
                        x = 0;
                    } else {
                        x = AndroidUtilities.m9dp(10.0f);
                    }
                    canvas.translate((float) (linkX + x), (float) (linkPreviewY - AndroidUtilities.m9dp(3.0f)));
                    this.siteNameLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                }
                if ((this.hasGamePreview || this.hasInvoicePreview) && this.currentMessageObject.textHeight != 0) {
                    startY += this.currentMessageObject.textHeight + AndroidUtilities.m9dp(4.0f);
                    linkPreviewY += this.currentMessageObject.textHeight + AndroidUtilities.m9dp(4.0f);
                }
                if (this.drawPhotoImage && this.drawInstantView) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.m9dp(2.0f);
                    }
                    this.photoImage.setImageCoords(AndroidUtilities.m9dp(10.0f) + linkX, linkPreviewY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    if (this.drawImageButton) {
                        size = AndroidUtilities.m9dp(48.0f);
                        this.buttonX = (int) (((float) this.photoImage.getImageX()) + (((float) (this.photoImage.getImageWidth() - size)) / 2.0f));
                        this.buttonY = (int) (((float) this.photoImage.getImageY()) + (((float) (this.photoImage.getImageHeight() - size)) / 2.0f));
                        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                    }
                    imageDrawn = this.photoImage.draw(canvas);
                    linkPreviewY += this.photoImage.getImageHeight() + AndroidUtilities.m9dp(6.0f);
                }
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                } else {
                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                }
                if (this.titleLayout != null) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.m9dp(2.0f);
                    }
                    smallImageStartY = linkPreviewY - AndroidUtilities.m9dp(1.0f);
                    canvas.save();
                    canvas.translate((float) ((AndroidUtilities.m9dp(10.0f) + linkX) + this.titleX), (float) (linkPreviewY - AndroidUtilities.m9dp(3.0f)));
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
                }
                if (this.authorLayout != null) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.m9dp(2.0f);
                    }
                    if (smallImageStartY == 0) {
                        smallImageStartY = linkPreviewY - AndroidUtilities.m9dp(1.0f);
                    }
                    canvas.save();
                    canvas.translate((float) ((AndroidUtilities.m9dp(10.0f) + linkX) + this.authorX), (float) (linkPreviewY - AndroidUtilities.m9dp(3.0f)));
                    this.authorLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
                }
                if (this.descriptionLayout != null) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.m9dp(2.0f);
                    }
                    if (smallImageStartY == 0) {
                        smallImageStartY = linkPreviewY - AndroidUtilities.m9dp(1.0f);
                    }
                    this.descriptionY = linkPreviewY - AndroidUtilities.m9dp(3.0f);
                    canvas.save();
                    canvas.translate((float) (((this.hasInvoicePreview ? 0 : AndroidUtilities.m9dp(10.0f)) + linkX) + this.descriptionX), (float) this.descriptionY);
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
                        linkPreviewY += AndroidUtilities.m9dp(2.0f);
                    }
                    if (this.isSmallImage) {
                        this.photoImage.setImageCoords((this.backgroundWidth + linkX) - AndroidUtilities.m9dp(81.0f), smallImageStartY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    } else {
                        imageReceiver = this.photoImage;
                        if (this.hasInvoicePreview) {
                            i2 = -AndroidUtilities.m9dp(6.3f);
                        } else {
                            i2 = AndroidUtilities.m9dp(10.0f);
                        }
                        imageReceiver.setImageCoords(i2 + linkX, linkPreviewY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                        if (this.drawImageButton) {
                            size = AndroidUtilities.m9dp(48.0f);
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
                    x = ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.m9dp(8.0f)) - this.photosCountWidth;
                    y = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.m9dp(19.0f);
                    this.rect.set((float) (x - AndroidUtilities.m9dp(4.0f)), (float) (y - AndroidUtilities.m9dp(1.5f)), (float) ((this.photosCountWidth + x) + AndroidUtilities.m9dp(4.0f)), (float) (AndroidUtilities.m9dp(14.5f) + y));
                    oldAlpha = Theme.chat_timeBackgroundPaint.getAlpha();
                    Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                    Theme.chat_durationPaint.setAlpha((int) (255.0f * this.controlsAlpha));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(4.0f), Theme.chat_timeBackgroundPaint);
                    Theme.chat_timeBackgroundPaint.setAlpha(oldAlpha);
                    canvas.save();
                    canvas.translate((float) x, (float) y);
                    this.photosCountLayout.draw(canvas);
                    canvas.restore();
                    Theme.chat_durationPaint.setAlpha(255);
                }
                if (this.videoInfoLayout != null && (!this.drawPhotoImage || this.photoImage.getVisible())) {
                    if (!this.hasGamePreview && !this.hasInvoicePreview) {
                        x = ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.m9dp(8.0f)) - this.durationWidth;
                        y = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.m9dp(19.0f);
                        this.rect.set((float) (x - AndroidUtilities.m9dp(4.0f)), (float) (y - AndroidUtilities.m9dp(1.5f)), (float) ((this.durationWidth + x) + AndroidUtilities.m9dp(4.0f)), (float) (AndroidUtilities.m9dp(14.5f) + y));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(4.0f), Theme.chat_timeBackgroundPaint);
                    } else if (this.drawPhotoImage) {
                        x = this.photoImage.getImageX() + AndroidUtilities.m9dp(8.5f);
                        y = this.photoImage.getImageY() + AndroidUtilities.m9dp(6.0f);
                        this.rect.set((float) (x - AndroidUtilities.m9dp(4.0f)), (float) (y - AndroidUtilities.m9dp(1.5f)), (float) ((this.durationWidth + x) + AndroidUtilities.m9dp(4.0f)), (float) (AndroidUtilities.m9dp(16.5f) + y));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(4.0f), Theme.chat_timeBackgroundPaint);
                    } else {
                        x = linkX;
                        y = linkPreviewY;
                    }
                    canvas.save();
                    canvas.translate((float) x, (float) y);
                    if (this.hasInvoicePreview) {
                        if (this.drawPhotoImage) {
                            Theme.chat_shipmentPaint.setColor(Theme.getColor(Theme.key_chat_previewGameText));
                        } else if (this.currentMessageObject.isOutOwner()) {
                            Theme.chat_shipmentPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                        } else {
                            Theme.chat_shipmentPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                        }
                    }
                    this.videoInfoLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.drawInstantView) {
                    Drawable instantDrawable;
                    instantY = (this.linkPreviewHeight + startY) + AndroidUtilities.m9dp(10.0f);
                    backPaint = Theme.chat_instantViewRectPaint;
                    if (this.currentMessageObject.isOutOwner()) {
                        instantDrawable = Theme.chat_msgOutInstantDrawable;
                        Theme.chat_instantViewPaint.setColor(Theme.getColor(Theme.key_chat_outPreviewInstantText));
                        backPaint.setColor(Theme.getColor(Theme.key_chat_outPreviewInstantText));
                    } else {
                        instantDrawable = Theme.chat_msgInInstantDrawable;
                        Theme.chat_instantViewPaint.setColor(Theme.getColor(Theme.key_chat_inPreviewInstantText));
                        backPaint.setColor(Theme.getColor(Theme.key_chat_inPreviewInstantText));
                    }
                    if (VERSION.SDK_INT >= 21) {
                        this.instantViewSelectorDrawable.setBounds(linkX, instantY, this.instantWidth + linkX, AndroidUtilities.m9dp(36.0f) + instantY);
                        this.instantViewSelectorDrawable.draw(canvas);
                    }
                    this.rect.set((float) linkX, (float) instantY, (float) (this.instantWidth + linkX), (float) (AndroidUtilities.m9dp(36.0f) + instantY));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(6.0f), (float) AndroidUtilities.m9dp(6.0f), backPaint);
                    if (this.drawInstantViewType == 0) {
                        BaseCell.setDrawableBounds(instantDrawable, ((this.instantTextLeftX + this.instantTextX) + linkX) - AndroidUtilities.m9dp(15.0f), AndroidUtilities.m9dp(11.5f) + instantY, AndroidUtilities.m9dp(9.0f), AndroidUtilities.m9dp(13.0f));
                        instantDrawable.draw(canvas);
                    }
                    if (this.instantViewLayout != null) {
                        canvas.save();
                        canvas.translate((float) (this.instantTextX + linkX), (float) (AndroidUtilities.m9dp(10.5f) + instantY));
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
                    x = this.photoImage.getImageX() - AndroidUtilities.m9dp(3.0f);
                    y = this.photoImage.getImageY() - AndroidUtilities.m9dp(2.0f);
                    Theme.chat_roundVideoShadow.setAlpha(255);
                    Theme.chat_roundVideoShadow.setBounds(x, y, (AndroidUtilities.roundMessageSize + x) + AndroidUtilities.m9dp(6.0f), (AndroidUtilities.roundMessageSize + y) + AndroidUtilities.m9dp(6.0f));
                    Theme.chat_roundVideoShadow.draw(canvas);
                    if (!(this.photoImage.hasBitmapImage() && this.photoImage.getCurrentAlpha() == 1.0f)) {
                        Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outBubble : Theme.key_chat_inBubble));
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
                                        if (position.last && position.maxY == this.currentPosition.maxY && (cell.timeX - AndroidUtilities.m9dp(4.0f)) + cell.getLeft() < getRight()) {
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
        if (this.buttonState == -1 && this.currentMessageObject.needDrawBluredPreview() && !MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && this.photoImage.getVisible()) {
            int drawable2 = 4;
            if (this.currentMessageObject.messageOwner.destroyTime != 0) {
                if (this.currentMessageObject.isOutOwner()) {
                    drawable2 = 6;
                } else {
                    drawable2 = 5;
                }
            }
            BaseCell.setDrawableBounds(Theme.chat_photoStatesDrawables[drawable2][this.buttonPressed], this.buttonX, this.buttonY);
            Theme.chat_photoStatesDrawables[drawable2][this.buttonPressed].setAlpha((int) ((255.0f * (1.0f - this.radialProgress.getAlpha())) * this.controlsAlpha));
            Theme.chat_photoStatesDrawables[drawable2][this.buttonPressed].draw(canvas);
            if (this.currentMessageObject.messageOwner.destroyTime != 0) {
                if (!this.currentMessageObject.isOutOwner()) {
                    progress = ((float) Math.max(0, (((long) this.currentMessageObject.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(this.currentAccount).getTimeDifference() * 1000))))) / (((float) this.currentMessageObject.messageOwner.ttl) * 1000.0f);
                    Theme.chat_deleteProgressPaint.setAlpha((int) (255.0f * this.controlsAlpha));
                    canvas.drawArc(this.deleteProgressRect, -90.0f, -360.0f * progress, true, Theme.chat_deleteProgressPaint);
                    if (progress != 0.0f) {
                        int offset = AndroidUtilities.m9dp(2.0f);
                        invalidate(((int) this.deleteProgressRect.left) - offset, ((int) this.deleteProgressRect.top) - offset, ((int) this.deleteProgressRect.right) + (offset * 2), ((int) this.deleteProgressRect.bottom) + (offset * 2));
                    }
                }
                updateSecretTimeText(this.currentMessageObject);
            }
        }
        if (this.documentAttachType == 2 || this.currentMessageObject.type == 8) {
            if (!(!this.photoImage.getVisible() || this.hasGamePreview || this.currentMessageObject.needDrawBluredPreview())) {
                oldAlpha = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                drawable = Theme.chat_msgMediaMenuDrawable;
                i = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.m9dp(14.0f);
                this.otherX = i;
                imageY = this.photoImage.getImageY() + AndroidUtilities.m9dp(8.1f);
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
                    x1 = i + AndroidUtilities.m9dp(f);
                    i = this.layoutHeight;
                    if (this.drawPinnedBottom) {
                        i2 = 2;
                    } else {
                        i2 = 0;
                    }
                    y1 = (i - AndroidUtilities.m9dp(6.3f - ((float) i2))) - this.timeLayout.getHeight();
                } else {
                    x1 = this.backgroundDrawableLeft + AndroidUtilities.m9dp(8.0f);
                    y1 = this.layoutHeight - AndroidUtilities.m9dp((float) (28 - (this.drawPinnedBottom ? 2 : 0)));
                    this.rect.set((float) x1, (float) y1, (float) ((this.timeWidthAudio + x1) + AndroidUtilities.m9dp(22.0f)), (float) (AndroidUtilities.m9dp(17.0f) + y1));
                    oldAlpha = Theme.chat_actionBackgroundPaint.getAlpha();
                    Theme.chat_actionBackgroundPaint.setAlpha((int) (((float) oldAlpha) * this.timeAlpha));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(4.0f), Theme.chat_actionBackgroundPaint);
                    Theme.chat_actionBackgroundPaint.setAlpha(oldAlpha);
                    if (playing || !this.currentMessageObject.isContentUnread()) {
                        if (!playing || MediaController.getInstance().isMessagePaused()) {
                            this.roundVideoPlayingDrawable.stop();
                        } else {
                            this.roundVideoPlayingDrawable.start();
                        }
                        BaseCell.setDrawableBounds(this.roundVideoPlayingDrawable, (this.timeWidthAudio + x1) + AndroidUtilities.m9dp(6.0f), AndroidUtilities.m9dp(2.3f) + y1);
                        this.roundVideoPlayingDrawable.draw(canvas);
                    } else {
                        Theme.chat_docBackPaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
                        Theme.chat_docBackPaint.setAlpha((int) (255.0f * this.timeAlpha));
                        canvas.drawCircle((float) ((this.timeWidthAudio + x1) + AndroidUtilities.m9dp(12.0f)), (float) (AndroidUtilities.m9dp(8.3f) + y1), (float) AndroidUtilities.m9dp(3.0f), Theme.chat_docBackPaint);
                    }
                    x1 += AndroidUtilities.m9dp(4.0f);
                    y1 += AndroidUtilities.m9dp(1.7f);
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
                Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_outAudioTitleText));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outAudioPerfomerSelectedText : Theme.key_chat_outAudioPerfomerText));
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outAudioDurationSelectedText : Theme.key_chat_outAudioDurationText));
                radialProgress = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_outAudioSelectedProgress : Theme.key_chat_outAudioProgress;
                radialProgress.setProgressColor(Theme.getColor(str));
            } else {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_inAudioTitleText));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inAudioPerfomerSelectedText : Theme.key_chat_inAudioPerfomerText));
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inAudioDurationSelectedText : Theme.key_chat_inAudioDurationText));
                radialProgress = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress;
                radialProgress.setProgressColor(Theme.getColor(str));
            }
            this.radialProgress.draw(canvas);
            canvas.save();
            canvas.translate((float) (this.timeAudioX + this.songX), (float) ((AndroidUtilities.m9dp(13.0f) + this.namesOffset) + this.mediaOffsetY));
            this.songLayout.draw(canvas);
            canvas.restore();
            canvas.save();
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
            } else {
                canvas.translate((float) (this.timeAudioX + this.performerX), (float) ((AndroidUtilities.m9dp(35.0f) + this.namesOffset) + this.mediaOffsetY));
                this.performerLayout.draw(canvas);
            }
            canvas.restore();
            canvas.save();
            canvas.translate((float) this.timeAudioX, (float) ((AndroidUtilities.m9dp(57.0f) + this.namesOffset) + this.mediaOffsetY));
            this.durationLayout.draw(canvas);
            canvas.restore();
            menuDrawable = this.currentMessageObject.isOutOwner() ? isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable : isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            i2 = (this.backgroundWidth + this.buttonX) - AndroidUtilities.m9dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
            this.otherX = i2;
            i = this.buttonY - AndroidUtilities.m9dp(5.0f);
            this.otherY = i;
            BaseCell.setDrawableBounds(menuDrawable, i2, i);
            menuDrawable.draw(canvas);
        } else if (this.documentAttachType == 3) {
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outAudioDurationSelectedText : Theme.key_chat_outAudioDurationText));
                radialProgress = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_outAudioSelectedProgress : Theme.key_chat_outAudioProgress;
                radialProgress.setProgressColor(Theme.getColor(str));
            } else {
                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inAudioDurationSelectedText : Theme.key_chat_inAudioDurationText));
                radialProgress = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress;
                radialProgress.setProgressColor(Theme.getColor(str));
            }
            this.radialProgress.draw(canvas);
            canvas.save();
            if (this.useSeekBarWaweform) {
                canvas.translate((float) (this.seekBarX + AndroidUtilities.m9dp(13.0f)), (float) this.seekBarY);
                this.seekBarWaveform.draw(canvas);
            } else {
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
            }
            canvas.restore();
            canvas.save();
            canvas.translate((float) this.timeAudioX, (float) ((AndroidUtilities.m9dp(44.0f) + this.namesOffset) + this.mediaOffsetY));
            this.durationLayout.draw(canvas);
            canvas.restore();
            if (this.currentMessageObject.type != 0 && this.currentMessageObject.isContentUnread()) {
                Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outVoiceSeekbarFill : Theme.key_chat_inVoiceSeekbarFill));
                canvas.drawCircle((float) ((this.timeAudioX + this.timeWidthAudio) + AndroidUtilities.m9dp(6.0f)), (float) ((AndroidUtilities.m9dp(51.0f) + this.namesOffset) + this.mediaOffsetY), (float) AndroidUtilities.m9dp(3.0f), Theme.chat_docBackPaint);
            }
        }
        if (this.currentMessageObject.type == 1 || this.documentAttachType == 4) {
            if (this.photoImage.getVisible()) {
                if (!this.currentMessageObject.needDrawBluredPreview() && this.documentAttachType == 4) {
                    oldAlpha = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                    Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                    drawable = Theme.chat_msgMediaMenuDrawable;
                    i = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.m9dp(14.0f);
                    this.otherX = i;
                    imageY = this.photoImage.getImageY() + AndroidUtilities.m9dp(8.1f);
                    this.otherY = imageY;
                    BaseCell.setDrawableBounds(drawable, i, imageY);
                    Theme.chat_msgMediaMenuDrawable.draw(canvas);
                    Theme.chat_msgMediaMenuDrawable.setAlpha(oldAlpha);
                }
                if (!(this.forceNotDrawTime || this.infoLayout == null || (this.buttonState != 1 && this.buttonState != 0 && this.buttonState != 3 && !this.currentMessageObject.needDrawBluredPreview()))) {
                    Theme.chat_infoPaint.setColor(Theme.getColor(Theme.key_chat_mediaInfoText));
                    x1 = this.photoImage.getImageX() + AndroidUtilities.m9dp(4.0f);
                    y1 = this.photoImage.getImageY() + AndroidUtilities.m9dp(4.0f);
                    this.rect.set((float) x1, (float) y1, (float) ((this.infoWidth + x1) + AndroidUtilities.m9dp(8.0f)), (float) (AndroidUtilities.m9dp(16.5f) + y1));
                    oldAlpha = Theme.chat_timeBackgroundPaint.getAlpha();
                    Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(4.0f), Theme.chat_timeBackgroundPaint);
                    Theme.chat_timeBackgroundPaint.setAlpha(oldAlpha);
                    canvas.save();
                    canvas.translate((float) (this.photoImage.getImageX() + AndroidUtilities.m9dp(8.0f)), (float) (this.photoImage.getImageY() + AndroidUtilities.m9dp(5.5f)));
                    Theme.chat_infoPaint.setAlpha((int) (255.0f * this.controlsAlpha));
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                    Theme.chat_infoPaint.setAlpha(255);
                }
            }
        } else if (this.currentMessageObject.type == 4) {
            if (this.docTitleLayout != null) {
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                    Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outVenueInfoSelectedText : Theme.key_chat_outVenueInfoText));
                } else {
                    Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                    Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inVenueInfoSelectedText : Theme.key_chat_inVenueInfoText));
                }
                if (this.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                    int cy = this.photoImage.getImageY2() + AndroidUtilities.m9dp(30.0f);
                    if (!this.locationExpired) {
                        this.forceNotDrawTime = true;
                        progress = 1.0f - (((float) Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date)) / ((float) this.currentMessageObject.messageOwner.media.period));
                        this.rect.set((float) (this.photoImage.getImageX2() - AndroidUtilities.m9dp(43.0f)), (float) (cy - AndroidUtilities.m9dp(15.0f)), (float) (this.photoImage.getImageX2() - AndroidUtilities.m9dp(13.0f)), (float) (AndroidUtilities.m9dp(15.0f) + cy));
                        if (this.currentMessageObject.isOutOwner()) {
                            Theme.chat_radialProgress2Paint.setColor(Theme.getColor(Theme.key_chat_outInstant));
                            Theme.chat_livePaint.setColor(Theme.getColor(Theme.key_chat_outInstant));
                        } else {
                            Theme.chat_radialProgress2Paint.setColor(Theme.getColor(Theme.key_chat_inInstant));
                            Theme.chat_livePaint.setColor(Theme.getColor(Theme.key_chat_inInstant));
                        }
                        Theme.chat_radialProgress2Paint.setAlpha(50);
                        canvas.drawCircle(this.rect.centerX(), this.rect.centerY(), (float) AndroidUtilities.m9dp(15.0f), Theme.chat_radialProgress2Paint);
                        Theme.chat_radialProgress2Paint.setAlpha(255);
                        canvas.drawArc(this.rect, -90.0f, -360.0f * progress, false, Theme.chat_radialProgress2Paint);
                        String text = LocaleController.formatLocationLeftTime(Math.abs(this.currentMessageObject.messageOwner.media.period - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date)));
                        canvas.drawText(text, this.rect.centerX() - (Theme.chat_livePaint.measureText(text) / 2.0f), (float) (AndroidUtilities.m9dp(4.0f) + cy), Theme.chat_livePaint);
                        canvas.save();
                        canvas.translate((float) (this.photoImage.getImageX() + AndroidUtilities.m9dp(10.0f)), (float) (this.photoImage.getImageY2() + AndroidUtilities.m9dp(10.0f)));
                        this.docTitleLayout.draw(canvas);
                        canvas.translate(0.0f, (float) AndroidUtilities.m9dp(23.0f));
                        this.infoLayout.draw(canvas);
                        canvas.restore();
                    }
                    int cx = (this.photoImage.getImageX() + (this.photoImage.getImageWidth() / 2)) - AndroidUtilities.m9dp(31.0f);
                    cy = (this.photoImage.getImageY() + (this.photoImage.getImageHeight() / 2)) - AndroidUtilities.m9dp(38.0f);
                    BaseCell.setDrawableBounds(Theme.chat_msgAvatarLiveLocationDrawable, cx, cy);
                    Theme.chat_msgAvatarLiveLocationDrawable.draw(canvas);
                    this.locationImageReceiver.setImageCoords(AndroidUtilities.m9dp(5.0f) + cx, AndroidUtilities.m9dp(5.0f) + cy, AndroidUtilities.m9dp(52.0f), AndroidUtilities.m9dp(52.0f));
                    this.locationImageReceiver.draw(canvas);
                } else {
                    canvas.save();
                    canvas.translate((float) (this.photoImage.getImageX() + AndroidUtilities.m9dp(6.0f)), (float) (this.photoImage.getImageY2() + AndroidUtilities.m9dp(8.0f)));
                    this.docTitleLayout.draw(canvas);
                    if (this.infoLayout != null) {
                        canvas.translate(0.0f, (float) AndroidUtilities.m9dp(21.0f));
                        this.infoLayout.draw(canvas);
                    }
                    canvas.restore();
                }
            }
        } else if (this.currentMessageObject.type == 16) {
            Drawable icon;
            Drawable phone;
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
            } else {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
            }
            this.forceNotDrawTime = true;
            if (this.currentMessageObject.isOutOwner()) {
                x = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.m9dp(16.0f);
            } else if (this.isChat && this.currentMessageObject.needDrawAvatar()) {
                x = AndroidUtilities.m9dp(74.0f);
            } else {
                x = AndroidUtilities.m9dp(25.0f);
            }
            this.otherX = x;
            if (this.titleLayout != null) {
                canvas.save();
                canvas.translate((float) x, (float) (AndroidUtilities.m9dp(12.0f) + this.namesOffset));
                this.titleLayout.draw(canvas);
                canvas.restore();
            }
            if (this.docTitleLayout != null) {
                canvas.save();
                canvas.translate((float) (AndroidUtilities.m9dp(19.0f) + x), (float) (AndroidUtilities.m9dp(37.0f) + this.namesOffset));
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
            BaseCell.setDrawableBounds(icon, x - AndroidUtilities.m9dp(3.0f), AndroidUtilities.m9dp(36.0f) + this.namesOffset);
            icon.draw(canvas);
            i2 = AndroidUtilities.m9dp(205.0f) + x;
            i = AndroidUtilities.m9dp(22.0f);
            this.otherY = i;
            BaseCell.setDrawableBounds(phone, i2, i);
            phone.draw(canvas);
        } else if (this.currentMessageObject.type == 12) {
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_contactNamePaint.setColor(Theme.getColor(Theme.key_chat_outContactNameText));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outContactPhoneSelectedText : Theme.key_chat_outContactPhoneText));
            } else {
                Theme.chat_contactNamePaint.setColor(Theme.getColor(Theme.key_chat_inContactNameText));
                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inContactPhoneSelectedText : Theme.key_chat_inContactPhoneText));
            }
            if (this.titleLayout != null) {
                canvas.save();
                canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.m9dp(9.0f)), (float) (AndroidUtilities.m9dp(16.0f) + this.namesOffset));
                this.titleLayout.draw(canvas);
                canvas.restore();
            }
            if (this.docTitleLayout != null) {
                canvas.save();
                canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.m9dp(9.0f)), (float) (AndroidUtilities.m9dp(39.0f) + this.namesOffset));
                this.docTitleLayout.draw(canvas);
                canvas.restore();
            }
            menuDrawable = this.currentMessageObject.isOutOwner() ? isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable : isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            i2 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.m9dp(48.0f);
            this.otherX = i2;
            i = this.photoImage.getImageY() - AndroidUtilities.m9dp(5.0f);
            this.otherY = i;
            BaseCell.setDrawableBounds(menuDrawable, i2, i);
            menuDrawable.draw(canvas);
            if (this.drawInstantView) {
                int textX = this.photoImage.getImageX() - AndroidUtilities.m9dp(2.0f);
                instantY = getMeasuredHeight() - AndroidUtilities.m9dp(64.0f);
                backPaint = Theme.chat_instantViewRectPaint;
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_instantViewPaint.setColor(Theme.getColor(Theme.key_chat_outPreviewInstantText));
                    backPaint.setColor(Theme.getColor(Theme.key_chat_outPreviewInstantText));
                } else {
                    Theme.chat_instantViewPaint.setColor(Theme.getColor(Theme.key_chat_inPreviewInstantText));
                    backPaint.setColor(Theme.getColor(Theme.key_chat_inPreviewInstantText));
                }
                if (VERSION.SDK_INT >= 21) {
                    this.instantViewSelectorDrawable.setBounds(textX, instantY, this.instantWidth + textX, AndroidUtilities.m9dp(36.0f) + instantY);
                    this.instantViewSelectorDrawable.draw(canvas);
                }
                this.instantButtonRect.set((float) textX, (float) instantY, (float) (this.instantWidth + textX), (float) (AndroidUtilities.m9dp(36.0f) + instantY));
                canvas.drawRoundRect(this.instantButtonRect, (float) AndroidUtilities.m9dp(6.0f), (float) AndroidUtilities.m9dp(6.0f), backPaint);
                if (this.instantViewLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.instantTextX + textX), (float) (AndroidUtilities.m9dp(10.5f) + instantY));
                    this.instantViewLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
        if (this.captionLayout != null) {
            if (this.currentMessageObject.type == 1 || this.documentAttachType == 4 || this.currentMessageObject.type == 8) {
                this.captionX = (this.photoImage.getImageX() + AndroidUtilities.m9dp(5.0f)) + this.captionOffsetX;
                this.captionY = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) + AndroidUtilities.m9dp(6.0f);
            } else if (this.hasOldCaptionPreview) {
                this.captionX = (AndroidUtilities.m9dp(this.currentMessageObject.isOutOwner() ? 11.0f : 17.0f) + this.backgroundDrawableLeft) + this.captionOffsetX;
                this.captionY = (((this.totalHeight - this.captionHeight) - AndroidUtilities.m9dp(this.drawPinnedTop ? 9.0f : 10.0f)) - this.linkPreviewHeight) - AndroidUtilities.m9dp(17.0f);
            } else {
                this.captionX = (AndroidUtilities.m9dp(this.currentMessageObject.isOutOwner() ? 11.0f : 17.0f) + this.backgroundDrawableLeft) + this.captionOffsetX;
                this.captionY = (this.totalHeight - this.captionHeight) - AndroidUtilities.m9dp(this.drawPinnedTop ? 9.0f : 10.0f);
            }
        }
        if (this.currentPosition == null) {
            drawCaptionLayout(canvas, false);
        }
        if (this.hasOldCaptionPreview) {
            if (this.currentMessageObject.type == 1 || this.documentAttachType == 4 || this.currentMessageObject.type == 8) {
                linkX = this.photoImage.getImageX() + AndroidUtilities.m9dp(5.0f);
            } else {
                linkX = this.backgroundDrawableLeft + AndroidUtilities.m9dp(this.currentMessageObject.isOutOwner() ? 11.0f : 17.0f);
            }
            startY = ((this.totalHeight - AndroidUtilities.m9dp(this.drawPinnedTop ? 9.0f : 10.0f)) - this.linkPreviewHeight) - AndroidUtilities.m9dp(8.0f);
            linkPreviewY = startY;
            Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
            canvas.drawRect((float) linkX, (float) (linkPreviewY - AndroidUtilities.m9dp(3.0f)), (float) (AndroidUtilities.m9dp(2.0f) + linkX), (float) (this.linkPreviewHeight + linkPreviewY), Theme.chat_replyLinePaint);
            if (this.siteNameLayout != null) {
                Theme.chat_replyNamePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                canvas.save();
                if (this.siteNameRtl) {
                    x = (this.backgroundWidth - this.siteNameWidth) - AndroidUtilities.m9dp(32.0f);
                } else if (this.hasInvoicePreview) {
                    x = 0;
                } else {
                    x = AndroidUtilities.m9dp(10.0f);
                }
                canvas.translate((float) (linkX + x), (float) (linkPreviewY - AndroidUtilities.m9dp(3.0f)));
                this.siteNameLayout.draw(canvas);
                canvas.restore();
                linkPreviewY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
            }
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
            } else {
                Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
            }
            if (this.descriptionLayout != null) {
                if (linkPreviewY != startY) {
                    linkPreviewY += AndroidUtilities.m9dp(2.0f);
                }
                this.descriptionY = linkPreviewY - AndroidUtilities.m9dp(3.0f);
                canvas.save();
                canvas.translate((float) ((AndroidUtilities.m9dp(10.0f) + linkX) + this.descriptionX), (float) this.descriptionY);
                this.descriptionLayout.draw(canvas);
                canvas.restore();
            }
            this.drawTime = true;
        }
        if (this.documentAttachType == 1) {
            int titleY;
            int subtitleY;
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_outFileNameText));
                Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileInfoSelectedText : Theme.key_chat_outFileInfoText));
                Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileBackgroundSelected : Theme.key_chat_outFileBackground));
                menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
            } else {
                Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_inFileNameText));
                Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileInfoSelectedText : Theme.key_chat_inFileInfoText));
                Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileBackgroundSelected : Theme.key_chat_inFileBackground));
                menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            }
            if (this.drawPhotoImage) {
                if (this.currentMessageObject.type == 0) {
                    i2 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.m9dp(56.0f);
                    this.otherX = i2;
                    i = this.photoImage.getImageY() + AndroidUtilities.m9dp(1.0f);
                    this.otherY = i;
                    BaseCell.setDrawableBounds(menuDrawable, i2, i);
                } else {
                    i2 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.m9dp(40.0f);
                    this.otherX = i2;
                    i = this.photoImage.getImageY() + AndroidUtilities.m9dp(1.0f);
                    this.otherY = i;
                    BaseCell.setDrawableBounds(menuDrawable, i2, i);
                }
                x = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.m9dp(10.0f);
                titleY = this.photoImage.getImageY() + AndroidUtilities.m9dp(8.0f);
                subtitleY = (this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.m9dp(13.0f);
                if (this.buttonState >= 0 && this.buttonState < 4) {
                    if (imageDrawn) {
                        this.radialProgress.swapBackground(Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed]);
                    } else {
                        int image = this.buttonState;
                        if (this.buttonState == 0) {
                            image = this.currentMessageObject.isOutOwner() ? 7 : 10;
                        } else if (this.buttonState == 1) {
                            image = this.currentMessageObject.isOutOwner() ? 8 : 11;
                        }
                        radialProgress = this.radialProgress;
                        Drawable[] drawableArr = Theme.chat_photoStatesDrawables[image];
                        i2 = (isDrawSelectedBackground() || this.buttonPressed != 0) ? 1 : 0;
                        radialProgress.swapBackground(drawableArr[i2]);
                    }
                }
                if (imageDrawn) {
                    if (this.buttonState == -1) {
                        this.radialProgress.setHideCurrentDrawable(true);
                    }
                    this.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
                } else {
                    this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) (this.photoImage.getImageX() + this.photoImage.getImageWidth()), (float) (this.photoImage.getImageY() + this.photoImage.getImageHeight()));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(3.0f), (float) AndroidUtilities.m9dp(3.0f), Theme.chat_docBackPaint);
                    if (this.currentMessageObject.isOutOwner()) {
                        radialProgress = this.radialProgress;
                        if (isDrawSelectedBackground()) {
                            str = Theme.key_chat_outFileProgressSelected;
                        } else {
                            str = Theme.key_chat_outFileProgress;
                        }
                        radialProgress.setProgressColor(Theme.getColor(str));
                    } else {
                        this.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileProgressSelected : Theme.key_chat_inFileProgress));
                    }
                }
            } else {
                i2 = (this.backgroundWidth + this.buttonX) - AndroidUtilities.m9dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
                this.otherX = i2;
                i = this.buttonY - AndroidUtilities.m9dp(5.0f);
                this.otherY = i;
                BaseCell.setDrawableBounds(menuDrawable, i2, i);
                x = this.buttonX + AndroidUtilities.m9dp(53.0f);
                titleY = this.buttonY + AndroidUtilities.m9dp(4.0f);
                subtitleY = this.buttonY + AndroidUtilities.m9dp(27.0f);
                if (this.currentMessageObject.isOutOwner()) {
                    radialProgress = this.radialProgress;
                    if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                        str = Theme.key_chat_outAudioSelectedProgress;
                    } else {
                        str = Theme.key_chat_outAudioProgress;
                    }
                    radialProgress.setProgressColor(Theme.getColor(str));
                } else {
                    radialProgress = this.radialProgress;
                    str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress;
                    radialProgress.setProgressColor(Theme.getColor(str));
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
                FileLog.m13e(e2);
            }
            try {
                if (this.infoLayout != null) {
                    canvas.save();
                    canvas.translate((float) x, (float) subtitleY);
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            } catch (Throwable e22) {
                FileLog.m13e(e22);
            }
        }
        if (this.drawImageButton && this.photoImage.getVisible()) {
            if (this.controlsAlpha != 1.0f) {
                this.radialProgress.setOverrideAlpha(this.controlsAlpha);
            }
            this.radialProgress.draw(canvas);
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
                addX = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.m9dp(10.0f);
            } else {
                addX = this.backgroundDrawableLeft + AndroidUtilities.m9dp(this.mediaBackground ? 1.0f : 7.0f);
            }
            a = 0;
            while (a < this.botButtons.size()) {
                BotButton button = (BotButton) this.botButtons.get(a);
                y = (button.var_y + this.layoutHeight) - AndroidUtilities.m9dp(2.0f);
                Theme.chat_systemDrawable.setColorFilter(a == this.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(button.var_x + addX, y, (button.var_x + addX) + button.width, button.height + y);
                Theme.chat_systemDrawable.draw(canvas);
                canvas.save();
                canvas.translate((float) ((button.var_x + addX) + AndroidUtilities.m9dp(5.0f)), (float) (((AndroidUtilities.m9dp(44.0f) - button.title.getLineBottom(button.title.getLineCount() - 1)) / 2) + y));
                button.title.draw(canvas);
                canvas.restore();
                if (button.button instanceof TL_keyboardButtonUrl) {
                    BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((button.var_x + button.width) - AndroidUtilities.m9dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + addX, AndroidUtilities.m9dp(3.0f) + y);
                    Theme.chat_botLinkDrawalbe.draw(canvas);
                } else if (button.button instanceof TL_keyboardButtonSwitchInline) {
                    BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((button.var_x + button.width) - AndroidUtilities.m9dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + addX, AndroidUtilities.m9dp(3.0f) + y);
                    Theme.chat_botInlineDrawable.draw(canvas);
                } else if ((button.button instanceof TL_keyboardButtonCallback) || (button.button instanceof TL_keyboardButtonRequestGeoLocation) || (button.button instanceof TL_keyboardButtonGame) || (button.button instanceof TL_keyboardButtonBuy)) {
                    boolean drawProgress = (((button.button instanceof TL_keyboardButtonCallback) || (button.button instanceof TL_keyboardButtonGame) || (button.button instanceof TL_keyboardButtonBuy)) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCallback(this.currentMessageObject, button.button)) || ((button.button instanceof TL_keyboardButtonRequestGeoLocation) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCurrentLocation(this.currentMessageObject, button.button));
                    if (drawProgress || !(drawProgress || button.progressAlpha == 0.0f)) {
                        Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (button.progressAlpha * 255.0f)));
                        x = ((button.var_x + button.width) - AndroidUtilities.m9dp(12.0f)) + addX;
                        this.rect.set((float) x, (float) (AndroidUtilities.m9dp(4.0f) + y), (float) (AndroidUtilities.m9dp(8.0f) + x), (float) (AndroidUtilities.m9dp(12.0f) + y));
                        canvas.drawArc(this.rect, (float) button.angle, 220.0f, false, Theme.chat_botProgressPaint);
                        invalidate(((int) this.rect.left) - AndroidUtilities.m9dp(2.0f), ((int) this.rect.top) - AndroidUtilities.m9dp(2.0f), ((int) this.rect.right) + AndroidUtilities.m9dp(2.0f), ((int) this.rect.bottom) + AndroidUtilities.m9dp(2.0f));
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

    private Drawable getMiniDrawableForCurrentState() {
        int i = 1;
        if (this.miniButtonState < 0) {
            return null;
        }
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            this.radialProgress.setAlphaForPrevious(false);
            CombinedDrawable[] combinedDrawableArr = Theme.chat_fileMiniStatesDrawable[this.currentMessageObject.isOutOwner() ? this.miniButtonState : this.miniButtonState + 2];
            int i2 = (isDrawSelectedBackground() || this.miniButtonPressed != 0) ? 1 : 0;
            return combinedDrawableArr[i2];
        } else if (this.documentAttachType != 4) {
            return null;
        } else {
            CombinedDrawable[] combinedDrawableArr2 = Theme.chat_fileMiniStatesDrawable[this.miniButtonState + 4];
            if (this.miniButtonPressed == 0) {
                i = 0;
            }
            return combinedDrawableArr2[i];
        }
    }

    private Drawable getDrawableForCurrentState() {
        int i = 3;
        int i2 = 0;
        int i3 = 1;
        if (this.documentAttachType != 3 && this.documentAttachType != 5) {
            Drawable[] drawableArr;
            if (this.documentAttachType != 1 || this.drawPhotoImage) {
                this.radialProgress.setAlphaForPrevious(true);
                if (this.buttonState < 0 || this.buttonState >= 4) {
                    if (this.buttonState == -1 && this.documentAttachType == 1) {
                        drawableArr = Theme.chat_photoStatesDrawables[this.currentMessageObject.isOutOwner() ? 9 : 12];
                        if (!isDrawSelectedBackground()) {
                            i3 = 0;
                        }
                        return drawableArr[i3];
                    }
                } else if (this.documentAttachType != 1) {
                    return Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed];
                } else {
                    int image = this.buttonState;
                    if (this.buttonState == 0) {
                        image = this.currentMessageObject.isOutOwner() ? 7 : 10;
                    } else if (this.buttonState == 1) {
                        image = this.currentMessageObject.isOutOwner() ? 8 : 11;
                    }
                    drawableArr = Theme.chat_photoStatesDrawables[image];
                    if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                        i2 = 1;
                    }
                    return drawableArr[i2];
                }
            }
            this.radialProgress.setAlphaForPrevious(false);
            if (this.buttonState == -1) {
                Drawable[][] drawableArr2 = Theme.chat_fileStatesDrawable;
                if (!this.currentMessageObject.isOutOwner()) {
                    i = 8;
                }
                drawableArr = drawableArr2[i];
                if (!isDrawSelectedBackground()) {
                    i3 = 0;
                }
                return drawableArr[i3];
            } else if (this.buttonState == 0) {
                drawableArr = Theme.chat_fileStatesDrawable[this.currentMessageObject.isOutOwner() ? 2 : 7];
                if (!isDrawSelectedBackground()) {
                    i3 = 0;
                }
                return drawableArr[i3];
            } else if (this.buttonState == 1) {
                drawableArr = Theme.chat_fileStatesDrawable[this.currentMessageObject.isOutOwner() ? 4 : 9];
                if (!isDrawSelectedBackground()) {
                    i3 = 0;
                }
                return drawableArr[i3];
            }
            return null;
        } else if (this.buttonState == -1) {
            return null;
        } else {
            this.radialProgress.setAlphaForPrevious(false);
            this.radialProgress.setAlphaForMiniPrevious(true);
            Drawable[] drawableArr3 = Theme.chat_fileStatesDrawable[this.currentMessageObject.isOutOwner() ? this.buttonState : this.buttonState + 5];
            i = (isDrawSelectedBackground() || this.buttonPressed != 0) ? 1 : 0;
            return drawableArr3[i];
        }
    }

    private int getMaxNameWidth() {
        if (this.documentAttachType == 6 || this.currentMessageObject.type == 5) {
            int maxWidth;
            if (AndroidUtilities.isTablet()) {
                if (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.needDrawAvatar()) {
                    maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.m9dp(42.0f);
                } else {
                    maxWidth = AndroidUtilities.getMinTabletSide();
                }
            } else if (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.needDrawAvatar()) {
                maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.m9dp(42.0f);
            } else {
                maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            }
            return (maxWidth - this.backgroundWidth) - AndroidUtilities.m9dp(57.0f);
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
                firstLineWidth = (int) (((double) firstLineWidth) + Math.ceil((double) ((((float) (position.var_pw + position.leftSpanOffset)) / 1000.0f) * ((float) dWidth))));
            }
            return firstLineWidth - AndroidUtilities.m9dp((float) ((this.isAvatarVisible ? 48 : 0) + 31));
        } else {
            return this.backgroundWidth - AndroidUtilities.m9dp(this.mediaBackground ? 22.0f : 31.0f);
        }
    }

    public void updateButtonState(boolean animated, boolean fromSet) {
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
        } else if (this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5 || this.documentAttachType == 7 || this.documentAttachType == 4 || this.currentMessageObject.type == 9 || this.documentAttachType == 3 || this.documentAttachType == 5) {
            if (this.currentMessageObject.useCustomPhoto) {
                this.buttonState = 1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
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
        if (TextUtils.isEmpty(fileName)) {
            this.radialProgress.setBackground(null, false, false);
            this.radialProgress.setMiniBackground(null, false, false);
            return;
        }
        boolean fromBot = this.currentMessageObject.messageOwner.params != null && this.currentMessageObject.messageOwner.params.containsKey("query_id");
        Float progress;
        RadialProgress radialProgress;
        Drawable miniDrawableForCurrentState;
        boolean z;
        float setProgress;
        boolean progressVisible;
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            boolean playing;
            if ((this.currentMessageObject.isOut() && (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) || (this.currentMessageObject.isSendError() && fromBot)) {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
                this.buttonState = 4;
                this.radialProgress.setBackground(getDrawableForCurrentState(), !fromBot, animated);
                if (fromBot) {
                    this.radialProgress.setProgress(0.0f, false);
                } else {
                    float floatValue;
                    progress = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
                    if (progress == null && SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId())) {
                        progress = Float.valueOf(1.0f);
                    }
                    radialProgress = this.radialProgress;
                    if (progress != null) {
                        floatValue = progress.floatValue();
                    } else {
                        floatValue = 0.0f;
                    }
                    radialProgress.setProgress(floatValue, false);
                }
            } else if (this.hasMiniProgress != 0) {
                this.radialProgress.setMiniProgressBackgroundColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outLoader : Theme.key_chat_inLoader));
                playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
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
                        this.radialProgress.setProgress(0.0f, animated);
                        this.miniButtonState = 0;
                    }
                }
                radialProgress = this.radialProgress;
                miniDrawableForCurrentState = getMiniDrawableForCurrentState();
                if (this.miniButtonState == 1) {
                    z = true;
                } else {
                    z = false;
                }
                radialProgress.setMiniBackground(miniDrawableForCurrentState, z, animated);
            } else if (fileExists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
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
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                } else {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                }
            }
            updatePlayingMessageProgress();
        } else if (this.currentMessageObject.type != 0 || this.documentAttachType == 1 || this.documentAttachType == 4) {
            if (!this.currentMessageObject.isOut() || (!this.currentMessageObject.isSending() && !this.currentMessageObject.isEditing())) {
                if (!(this.currentMessageObject.messageOwner.attachPath == null || this.currentMessageObject.messageOwner.attachPath.length() == 0)) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                }
                if (this.hasMiniProgress != 0) {
                    this.radialProgress.setMiniProgressBackgroundColor(Theme.getColor(Theme.key_chat_inLoaderPhoto));
                    this.buttonState = 3;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
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
                            this.radialProgress.setProgress(0.0f, animated);
                            this.miniButtonState = 0;
                        }
                    }
                    radialProgress = this.radialProgress;
                    miniDrawableForCurrentState = getMiniDrawableForCurrentState();
                    if (this.miniButtonState == 1) {
                        z = true;
                    } else {
                        z = false;
                    }
                    radialProgress.setMiniBackground(miniDrawableForCurrentState, z, animated);
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
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                    if (!fromSet && this.photoNotSet) {
                        setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
                    }
                    invalidate();
                } else {
                    DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                    setProgress = 0.0f;
                    progressVisible = false;
                    if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                        progressVisible = true;
                        this.buttonState = 1;
                        progress = ImageLoader.getInstance().getFileProgress(fileName);
                        setProgress = progress != null ? progress.floatValue() : 0.0f;
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
                            progressVisible = true;
                            this.buttonState = 1;
                        }
                    }
                    this.radialProgress.setBackground(getDrawableForCurrentState(), progressVisible, animated);
                    this.radialProgress.setProgress(setProgress, false);
                    invalidate();
                }
            } else if (this.currentMessageObject.messageOwner.attachPath != null && this.currentMessageObject.messageOwner.attachPath.length() > 0) {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
                boolean needProgress = this.currentMessageObject.messageOwner.attachPath == null || !this.currentMessageObject.messageOwner.attachPath.startsWith("http");
                HashMap<String, String> params = this.currentMessageObject.messageOwner.params;
                if (this.currentMessageObject.messageOwner.message == null || params == null || !(params.containsKey(UpdateFragment.FRAGMENT_URL) || params.containsKey("bot"))) {
                    this.buttonState = 1;
                } else {
                    needProgress = false;
                    this.buttonState = -1;
                }
                boolean sending = SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId());
                if (this.currentPosition != null && sending && this.buttonState == 1) {
                    this.drawRadialCheckBackground = true;
                    this.radialProgress.setCheckBackground(false, animated);
                } else {
                    this.radialProgress.setBackground(getDrawableForCurrentState(), needProgress, animated);
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
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                invalidate();
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                setProgress = 0.0f;
                progressVisible = false;
                if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    progressVisible = true;
                    this.buttonState = 1;
                    progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                } else if (this.cancelLoading || !((this.documentAttachType == 0 && DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject)) || (this.documentAttachType == 2 && MessageObject.isNewGifDocument(this.documentAttach) && DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject)))) {
                    this.buttonState = 0;
                } else {
                    progressVisible = true;
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(setProgress, false);
                this.radialProgress.setBackground(getDrawableForCurrentState(), progressVisible, animated);
                invalidate();
            }
        } else {
            return;
        }
        if (this.hasMiniProgress == 0) {
            this.radialProgress.setMiniBackground(null, false, animated);
        }
    }

    private void didPressedMiniButton(boolean animated) {
        if (this.miniButtonState == 0) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            if (this.documentAttachType == 3 || this.documentAttachType == 5) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
            } else if (this.documentAttachType == 4) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
            }
            this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
            invalidate();
        } else if (this.miniButtonState == 1) {
            if ((this.documentAttachType == 3 || this.documentAttachType == 5) && MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
            invalidate();
        }
    }

    private void didPressedButton(boolean animated) {
        if (this.buttonState == 0) {
            if (this.documentAttachType == 3 || this.documentAttachType == 5) {
                if (this.miniButtonState == 0) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                }
                if (this.delegate.needPlayMessage(this.currentMessageObject)) {
                    if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                        this.miniButtonState = 1;
                        this.radialProgress.setProgress(0.0f, false);
                        this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
                    }
                    updatePlayingMessageProgress();
                    this.buttonState = 1;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                    return;
                }
                return;
            }
            this.cancelLoading = false;
            this.radialProgress.setProgress(0.0f, false);
            if (this.currentMessageObject.type == 1) {
                FileLocation fileLocation;
                int i;
                this.photoImage.setForceLoading(true);
                ImageReceiver imageReceiver = this.photoImage;
                TLObject tLObject = this.currentPhotoObject.location;
                String str = this.currentPhotoFilter;
                if (this.currentPhotoObjectThumb != null) {
                    fileLocation = this.currentPhotoObjectThumb.location;
                } else {
                    fileLocation = null;
                }
                String str2 = this.currentPhotoFilterThumb;
                int i2 = this.currentPhotoObject.size;
                MessageObject messageObject = this.currentMessageObject;
                if (this.currentMessageObject.shouldEncryptPhotoOrVideo()) {
                    i = 2;
                } else {
                    i = 0;
                }
                imageReceiver.setImage(tLObject, str, fileLocation, str2, i2, null, messageObject, i);
            } else if (this.currentMessageObject.type == 8) {
                this.currentMessageObject.gifState = 2.0f;
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentMessageObject.messageOwner.media.document, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilterThumb, this.currentMessageObject.messageOwner.media.document.size, null, this.currentMessageObject, 0);
            } else if (this.currentMessageObject.isRoundVideo()) {
                if (this.currentMessageObject.isSecretMedia()) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 1);
                } else {
                    this.currentMessageObject.gifState = 2.0f;
                    Document document = this.currentMessageObject.getDocument();
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(document, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilterThumb, document.size, null, this.currentMessageObject, 0);
                }
            } else if (this.currentMessageObject.type == 9) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.messageOwner.media.document, this.currentMessageObject, 0, 0);
            } else if (this.documentAttachType == 4) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
            } else if (this.currentMessageObject.type != 0 || this.documentAttachType == 0) {
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, this.currentPhotoFilterThumb, 0, null, this.currentMessageObject, 0);
            } else if (this.documentAttachType == 2) {
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentMessageObject.messageOwner.media.webpage.document, null, this.currentPhotoObject.location, this.currentPhotoFilterThumb, this.currentMessageObject.messageOwner.media.webpage.document.size, null, this.currentMessageObject, 0);
                this.currentMessageObject.gifState = 2.0f;
            } else if (this.documentAttachType == 1) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.messageOwner.media.webpage.document, this.currentMessageObject, 0, 0);
            }
            this.buttonState = 1;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
            invalidate();
        } else if (this.buttonState == 1) {
            if (this.documentAttachType == 3 || this.documentAttachType == 5) {
                if (MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            } else if (!this.currentMessageObject.isOut() || (!this.currentMessageObject.isSending() && !this.currentMessageObject.isEditing())) {
                this.cancelLoading = true;
                if (this.documentAttachType == 4 || this.documentAttachType == 1) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else if (this.currentMessageObject.type == 0 || this.currentMessageObject.type == 1 || this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) {
                    ImageLoader.getInstance().cancelForceLoadingForImageReceiver(this.photoImage);
                    this.photoImage.cancelLoadImage();
                } else if (this.currentMessageObject.type == 9) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.messageOwner.media.document);
                }
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                invalidate();
            } else if (!this.radialProgress.isDrawCheckDrawable()) {
                this.delegate.didPressedCancelSendButton(this);
            }
        } else if (this.buttonState == 2) {
            if (this.documentAttachType == 3 || this.documentAttachType == 5) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                this.buttonState = 4;
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
                invalidate();
                return;
            }
            this.photoImage.setAllowStartAnimation(true);
            this.photoImage.startAnimation();
            this.currentMessageObject.gifState = 0.0f;
            this.buttonState = -1;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
        } else if (this.buttonState == 3) {
            if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                this.miniButtonState = 1;
                this.radialProgress.setProgress(0.0f, false);
                this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
            }
            this.delegate.didPressedImage(this);
        } else if (this.buttonState != 4) {
        } else {
            if (this.documentAttachType != 3 && this.documentAttachType != 5) {
                return;
            }
            if ((!this.currentMessageObject.isOut() || (!this.currentMessageObject.isSending() && !this.currentMessageObject.isEditing())) && !this.currentMessageObject.isSendError()) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                this.buttonState = 2;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            } else if (this.delegate != null) {
                this.delegate.didPressedCancelSendButton(this);
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
        updateButtonState(z, false);
    }

    public void onSuccessDownload(String fileName) {
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            updateButtonState(true, false);
            updateWaveform();
            return;
        }
        this.radialProgress.setProgress(1.0f, true);
        if (this.currentMessageObject.type != 0) {
            if (!this.photoNotSet || ((this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) && this.currentMessageObject.gifState != 1.0f)) {
                if ((this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) && this.currentMessageObject.gifState != 1.0f) {
                    this.photoNotSet = false;
                    this.buttonState = 2;
                    didPressedButton(true);
                } else {
                    updateButtonState(true, false);
                }
            }
            if (this.photoNotSet) {
                setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
            }
        } else if (this.documentAttachType == 2 && this.currentMessageObject.gifState != 1.0f) {
            this.buttonState = 2;
            didPressedButton(true);
        } else if (this.photoNotSet) {
            setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
        } else {
            updateButtonState(true, false);
        }
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        if (this.currentMessageObject == null) {
            return;
        }
        if ((this.currentMessageObject.type == 0 || this.currentMessageObject.type == 1 || this.currentMessageObject.type == 5 || this.currentMessageObject.type == 8) && set && !thumb && !this.currentMessageObject.mediaExists && !this.currentMessageObject.attachPathExists) {
            this.currentMessageObject.mediaExists = true;
            updateButtonState(true, false);
        }
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, true);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            if (this.hasMiniProgress != 0) {
                if (this.miniButtonState != 1) {
                    updateButtonState(false, false);
                }
            } else if (this.buttonState != 4) {
                updateButtonState(false, false);
            }
        } else if (this.hasMiniProgress != 0) {
            if (this.miniButtonState != 1) {
                updateButtonState(false, false);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false, false);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        this.radialProgress.setProgress(progress, true);
        if (progress == 1.0f && this.currentPosition != null && SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId()) && this.buttonState == 1) {
            this.drawRadialCheckBackground = true;
            this.radialProgress.setCheckBackground(false, true);
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
            signString = messageObject.messageOwner.post_author.replace("\n", TtmlNode.ANONYMOUS_REGION_ID);
        } else if (messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.post_author != null) {
            signString = messageObject.messageOwner.fwd_from.post_author.replace("\n", TtmlNode.ANONYMOUS_REGION_ID);
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
            this.timeWidth += (this.viewsTextWidth + Theme.chat_msgInViewsDrawable.getIntrinsicWidth()) + AndroidUtilities.m9dp(10.0f);
        }
        if (signString != null) {
            if (this.availableTimeWidth == 0) {
                this.availableTimeWidth = AndroidUtilities.m9dp(1000.0f);
            }
            int widthForSign = this.availableTimeWidth - this.timeWidth;
            if (messageObject.isOutOwner()) {
                if (messageObject.type == 5) {
                    widthForSign -= AndroidUtilities.m9dp(20.0f);
                } else {
                    widthForSign -= AndroidUtilities.m9dp(96.0f);
                }
            }
            int width = (int) Math.ceil((double) Theme.chat_timePaint.measureText(signString, 0, signString.length()));
            if (width > widthForSign) {
                if (widthForSign <= 0) {
                    signString = TtmlNode.ANONYMOUS_REGION_ID;
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
        if (this.currentPosition == null || this.currentPosition.last) {
            return messageObject.needDrawShareButton();
        }
        return false;
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
            MessagesController.getInstance(this.currentAccount).addToViewsQueue(this.currentMessageObject.messageOwner);
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
                this.nameWidth = AndroidUtilities.m9dp(100.0f);
            }
            if (this.currentUser == null || this.currentMessageObject.isOutOwner() || this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5 || !this.delegate.isChatAdminCell(this.currentUser.var_id)) {
                adminString = null;
                adminWidth = 0;
            } else {
                adminString = LocaleController.getString("ChatAdmin", R.string.ChatAdmin);
                adminWidth = (int) Math.ceil((double) Theme.chat_adminPaint.measureText(adminString));
                this.nameWidth -= adminWidth;
            }
            if (!authorName) {
                this.currentNameString = TtmlNode.ANONYMOUS_REGION_ID;
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
                    this.viaNameWidth += AndroidUtilities.m9dp(4.0f);
                }
                if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                    color = Theme.getColor(Theme.key_chat_stickerViaBotNameText);
                } else {
                    color = Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outViaBotNameText : Theme.key_chat_inViaBotNameText);
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
                this.nameLayout = new StaticLayout(nameStringFinal, Theme.chat_namePaint, this.nameWidth + AndroidUtilities.m9dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.nameLayout == null || this.nameLayout.getLineCount() <= 0) {
                    this.nameWidth = 0;
                } else {
                    this.nameWidth = (int) Math.ceil((double) this.nameLayout.getLineWidth(0));
                    if (messageObject.type != 13) {
                        this.namesOffset += AndroidUtilities.m9dp(19.0f);
                    }
                    this.nameOffsetX = this.nameLayout.getLineLeft(0);
                }
                if (adminString != null) {
                    this.adminLayout = new StaticLayout(adminString, Theme.chat_adminPaint, adminWidth + AndroidUtilities.m9dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.nameWidth = (int) (((float) this.nameWidth) + (this.adminLayout.getLineWidth(0) + ((float) AndroidUtilities.m9dp(8.0f))));
                } else {
                    this.adminLayout = null;
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
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
                    this.forwardedNameLayout[1] = new StaticLayout(TextUtils.ellipsize(stringBuilder, Theme.chat_forwardNamePaint, (float) this.forwardedNameWidth, TruncateAt.END), Theme.chat_forwardNamePaint, this.forwardedNameWidth + AndroidUtilities.m9dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.forwardedNameLayout[0] = new StaticLayout(TextUtils.ellipsize(AndroidUtilities.replaceTags(LocaleController.getString("ForwardedMessage", R.string.ForwardedMessage)), Theme.chat_forwardNamePaint, (float) this.forwardedNameWidth, TruncateAt.END), Theme.chat_forwardNamePaint, this.forwardedNameWidth + AndroidUtilities.m9dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.forwardedNameWidth = Math.max((int) Math.ceil((double) this.forwardedNameLayout[0].getLineWidth(0)), (int) Math.ceil((double) this.forwardedNameLayout[1].getLineWidth(0)));
                    this.forwardNameOffsetX[0] = this.forwardedNameLayout[0].getLineLeft(0);
                    this.forwardNameOffsetX[1] = this.forwardedNameLayout[1].getLineLeft(0);
                    if (messageObject.type != 5) {
                        this.namesOffset += AndroidUtilities.m9dp(36.0f);
                    }
                } catch (Throwable e3) {
                    FileLog.m13e(e3);
                }
            }
        }
        if (messageObject.hasValidReplyMessageObject() && (this.currentPosition == null || this.currentPosition.minY == (byte) 0)) {
            if (!(messageObject.type == 13 || messageObject.type == 5)) {
                this.namesOffset += AndroidUtilities.m9dp(42.0f);
                if (messageObject.type != 0) {
                    this.namesOffset += AndroidUtilities.m9dp(5.0f);
                }
            }
            int maxWidth = getMaxNameWidth();
            if (messageObject.type != 13 && messageObject.type != 5) {
                maxWidth -= AndroidUtilities.m9dp(10.0f);
            } else if (messageObject.type == 5) {
                maxWidth += AndroidUtilities.m9dp(13.0f);
            }
            CharSequence stringFinalText = null;
            PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.replyMessageObject.photoThumbs2, 80);
            if (photoSize == null) {
                photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.replyMessageObject.photoThumbs, 80);
            }
            if (photoSize == null || messageObject.replyMessageObject.type == 13 || ((messageObject.type == 13 && !AndroidUtilities.isTablet()) || messageObject.replyMessageObject.isSecretMedia())) {
                this.replyImageReceiver.setImageBitmap((Drawable) null);
                this.needReplyImage = false;
            } else {
                if (messageObject.replyMessageObject.isRoundVideo()) {
                    this.replyImageReceiver.setRoundRadius(AndroidUtilities.m9dp(22.0f));
                } else {
                    this.replyImageReceiver.setRoundRadius(0);
                }
                this.currentReplyPhoto = photoSize.location;
                this.replyImageReceiver.setImage(photoSize.location, "50_50", null, null, messageObject.replyMessageObject, 1);
                this.needReplyImage = true;
                maxWidth -= AndroidUtilities.m9dp(44.0f);
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
                stringFinalText = TextUtils.ellipsize(Emoji.replaceEmoji(messageObject.replyMessageObject.messageOwner.media.game.title, Theme.chat_replyTextPaint.getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false), Theme.chat_replyTextPaint, (float) maxWidth, TruncateAt.END);
            } else if (messageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice) {
                stringFinalText = TextUtils.ellipsize(Emoji.replaceEmoji(messageObject.replyMessageObject.messageOwner.media.title, Theme.chat_replyTextPaint.getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false), Theme.chat_replyTextPaint, (float) maxWidth, TruncateAt.END);
            } else if (messageObject.replyMessageObject.messageText != null && messageObject.replyMessageObject.messageText.length() > 0) {
                String mess = messageObject.replyMessageObject.messageText.toString();
                if (mess.length() > 150) {
                    mess = mess.substring(0, 150);
                }
                stringFinalText = TextUtils.ellipsize(Emoji.replaceEmoji(mess.replace(10, ' '), Theme.chat_replyTextPaint.getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false), Theme.chat_replyTextPaint, (float) maxWidth, TruncateAt.END);
            }
            try {
                this.replyNameWidth = AndroidUtilities.m9dp((float) ((this.needReplyImage ? 44 : 0) + 4));
                if (stringFinalName != null) {
                    this.replyNameLayout = new StaticLayout(stringFinalName, Theme.chat_replyNamePaint, maxWidth + AndroidUtilities.m9dp(6.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (this.replyNameLayout.getLineCount() > 0) {
                        this.replyNameWidth += ((int) Math.ceil((double) this.replyNameLayout.getLineWidth(0))) + AndroidUtilities.m9dp(8.0f);
                        this.replyNameOffset = this.replyNameLayout.getLineLeft(0);
                    }
                }
            } catch (Throwable e32) {
                FileLog.m13e(e32);
            }
            try {
                this.replyTextWidth = AndroidUtilities.m9dp((float) ((this.needReplyImage ? 44 : 0) + 4));
                if (stringFinalText != null) {
                    this.replyTextLayout = new StaticLayout(stringFinalText, Theme.chat_replyTextPaint, maxWidth + AndroidUtilities.m9dp(6.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (this.replyTextLayout.getLineCount() > 0) {
                        this.replyTextWidth += ((int) Math.ceil((double) this.replyTextLayout.getLineWidth(0))) + AndroidUtilities.m9dp(8.0f);
                        this.replyTextOffset = this.replyTextLayout.getLineLeft(0);
                    }
                }
            } catch (Throwable e322) {
                FileLog.m13e(e322);
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
                long newTime;
                long dt;
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_msgTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                    Theme.chat_msgTextPaint.linkColor = Theme.getColor(Theme.key_chat_messageLinkOut);
                    Theme.chat_msgGameTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                    Theme.chat_msgGameTextPaint.linkColor = Theme.getColor(Theme.key_chat_messageLinkOut);
                    Theme.chat_replyTextPaint.linkColor = Theme.getColor(Theme.key_chat_messageLinkOut);
                } else {
                    Theme.chat_msgTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                    Theme.chat_msgTextPaint.linkColor = Theme.getColor(Theme.key_chat_messageLinkIn);
                    Theme.chat_msgGameTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                    Theme.chat_msgGameTextPaint.linkColor = Theme.getColor(Theme.key_chat_messageLinkIn);
                    Theme.chat_replyTextPaint.linkColor = Theme.getColor(Theme.key_chat_messageLinkIn);
                }
                if (this.documentAttach != null) {
                    if (this.documentAttachType == 3) {
                        if (this.currentMessageObject.isOutOwner()) {
                            this.seekBarWaveform.setColors(Theme.getColor(Theme.key_chat_outVoiceSeekbar), Theme.getColor(Theme.key_chat_outVoiceSeekbarFill), Theme.getColor(Theme.key_chat_outVoiceSeekbarSelected));
                            this.seekBar.setColors(Theme.getColor(Theme.key_chat_outAudioSeekbar), Theme.getColor(Theme.key_chat_outAudioCacheSeekbar), Theme.getColor(Theme.key_chat_outAudioSeekbarFill), Theme.getColor(Theme.key_chat_outAudioSeekbarFill), Theme.getColor(Theme.key_chat_outAudioSeekbarSelected));
                        } else {
                            this.seekBarWaveform.setColors(Theme.getColor(Theme.key_chat_inVoiceSeekbar), Theme.getColor(Theme.key_chat_inVoiceSeekbarFill), Theme.getColor(Theme.key_chat_inVoiceSeekbarSelected));
                            this.seekBar.setColors(Theme.getColor(Theme.key_chat_inAudioSeekbar), Theme.getColor(Theme.key_chat_inAudioCacheSeekbar), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarSelected));
                        }
                    } else if (this.documentAttachType == 5) {
                        this.documentAttachType = 5;
                        if (this.currentMessageObject.isOutOwner()) {
                            this.seekBar.setColors(Theme.getColor(Theme.key_chat_outAudioSeekbar), Theme.getColor(Theme.key_chat_outAudioCacheSeekbar), Theme.getColor(Theme.key_chat_outAudioSeekbarFill), Theme.getColor(Theme.key_chat_outAudioSeekbarFill), Theme.getColor(Theme.key_chat_outAudioSeekbarSelected));
                        } else {
                            this.seekBar.setColors(Theme.getColor(Theme.key_chat_inAudioSeekbar), Theme.getColor(Theme.key_chat_inAudioCacheSeekbar), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarSelected));
                        }
                    }
                }
                if (this.currentMessageObject.type == 5) {
                    Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
                } else if (this.mediaBackground) {
                    if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                        Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_serviceText));
                    } else {
                        Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
                    }
                } else if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
                } else {
                    Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
                }
                int additionalTop = 0;
                int additionalBottom = 0;
                int i2;
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
                    this.backgroundDrawableLeft = (this.layoutWidth - this.backgroundWidth) - (!this.mediaBackground ? 0 : AndroidUtilities.m9dp(9.0f));
                    i = this.backgroundWidth;
                    if (this.mediaBackground) {
                        i2 = 0;
                    } else {
                        i2 = AndroidUtilities.m9dp(3.0f);
                    }
                    this.backgroundDrawableRight = i - i2;
                    if (!(this.currentMessagesGroup == null || this.currentPosition.edge)) {
                        this.backgroundDrawableRight += AndroidUtilities.m9dp(10.0f);
                    }
                    int backgroundLeft = this.backgroundDrawableLeft;
                    if (!this.mediaBackground && this.drawPinnedBottom) {
                        this.backgroundDrawableRight -= AndroidUtilities.m9dp(6.0f);
                    }
                    if (this.currentPosition != null) {
                        if ((this.currentPosition.flags & 2) == 0) {
                            this.backgroundDrawableRight += AndroidUtilities.m9dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 1) == 0) {
                            backgroundLeft -= AndroidUtilities.m9dp(8.0f);
                            this.backgroundDrawableRight += AndroidUtilities.m9dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 4) == 0) {
                            additionalTop = 0 - AndroidUtilities.m9dp(9.0f);
                            additionalBottom = 0 + AndroidUtilities.m9dp(9.0f);
                        }
                        if ((this.currentPosition.flags & 8) == 0) {
                            additionalBottom += AndroidUtilities.m9dp(9.0f);
                        }
                    }
                    if (this.drawPinnedBottom && this.drawPinnedTop) {
                        offsetBottom = 0;
                    } else if (this.drawPinnedBottom) {
                        offsetBottom = AndroidUtilities.m9dp(1.0f);
                    } else {
                        offsetBottom = AndroidUtilities.m9dp(2.0f);
                    }
                    i2 = (this.drawPinnedTop || (this.drawPinnedTop && this.drawPinnedBottom)) ? 0 : AndroidUtilities.m9dp(1.0f);
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
                    this.backgroundDrawableLeft = AndroidUtilities.m9dp((float) (i2 + (!this.mediaBackground ? 3 : 9)));
                    i = this.backgroundWidth;
                    if (this.mediaBackground) {
                        i2 = 0;
                    } else {
                        i2 = AndroidUtilities.m9dp(3.0f);
                    }
                    this.backgroundDrawableRight = i - i2;
                    if (this.currentMessagesGroup != null) {
                        if (!this.currentPosition.edge) {
                            this.backgroundDrawableLeft -= AndroidUtilities.m9dp(10.0f);
                            this.backgroundDrawableRight += AndroidUtilities.m9dp(10.0f);
                        }
                        if (this.currentPosition.leftSpanOffset != 0) {
                            this.backgroundDrawableLeft += (int) Math.ceil((double) ((((float) this.currentPosition.leftSpanOffset) / 1000.0f) * ((float) getGroupPhotosWidth())));
                        }
                    }
                    if (!this.mediaBackground && this.drawPinnedBottom) {
                        this.backgroundDrawableRight -= AndroidUtilities.m9dp(6.0f);
                        this.backgroundDrawableLeft += AndroidUtilities.m9dp(6.0f);
                    }
                    if (this.currentPosition != null) {
                        if ((this.currentPosition.flags & 2) == 0) {
                            this.backgroundDrawableRight += AndroidUtilities.m9dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 1) == 0) {
                            this.backgroundDrawableLeft -= AndroidUtilities.m9dp(8.0f);
                            this.backgroundDrawableRight += AndroidUtilities.m9dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 4) == 0) {
                            additionalTop = 0 - AndroidUtilities.m9dp(9.0f);
                            additionalBottom = 0 + AndroidUtilities.m9dp(9.0f);
                        }
                        if ((this.currentPosition.flags & 8) == 0) {
                            additionalBottom += AndroidUtilities.m9dp(10.0f);
                        }
                    }
                    if (this.drawPinnedBottom && this.drawPinnedTop) {
                        offsetBottom = 0;
                    } else if (this.drawPinnedBottom) {
                        offsetBottom = AndroidUtilities.m9dp(1.0f);
                    } else {
                        offsetBottom = AndroidUtilities.m9dp(2.0f);
                    }
                    i2 = (this.drawPinnedTop || (this.drawPinnedTop && this.drawPinnedBottom)) ? 0 : AndroidUtilities.m9dp(1.0f);
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
                        if (!Theme.isCustomTheme() || Theme.hasThemeKey(Theme.key_chat_shareBackgroundSelected)) {
                            Theme.chat_shareDrawable.setColorFilter(Theme.getShareColorFilter(Theme.getColor(Theme.key_chat_shareBackgroundSelected), true));
                        } else {
                            Theme.chat_shareDrawable.setColorFilter(Theme.colorPressedFilter2);
                        }
                    } else if (!Theme.isCustomTheme() || Theme.hasThemeKey(Theme.key_chat_shareBackground)) {
                        Theme.chat_shareDrawable.setColorFilter(Theme.getShareColorFilter(Theme.getColor(Theme.key_chat_shareBackground), false));
                    } else {
                        Theme.chat_shareDrawable.setColorFilter(Theme.colorFilter2);
                    }
                    if (this.currentMessageObject.isOutOwner()) {
                        this.shareStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.m9dp(8.0f)) - Theme.chat_shareDrawable.getIntrinsicWidth();
                    } else {
                        this.shareStartX = this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.m9dp(8.0f);
                    }
                    Drawable drawable = Theme.chat_shareDrawable;
                    i = this.shareStartX;
                    int dp = this.layoutHeight - AndroidUtilities.m9dp(41.0f);
                    this.shareStartY = dp;
                    BaseCell.setDrawableBounds(drawable, i, dp);
                    Theme.chat_shareDrawable.draw(canvas);
                    if (this.drwaShareGoIcon) {
                        BaseCell.setDrawableBounds(Theme.chat_goIconDrawable, this.shareStartX + AndroidUtilities.m9dp(12.0f), this.shareStartY + AndroidUtilities.m9dp(9.0f));
                        Theme.chat_goIconDrawable.draw(canvas);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_shareIconDrawable, this.shareStartX + AndroidUtilities.m9dp(8.0f), this.shareStartY + AndroidUtilities.m9dp(9.0f));
                        Theme.chat_shareIconDrawable.draw(canvas);
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
                i = AndroidUtilities.m9dp(9.0f);
            }
            return i2 - i;
        }
        if (this.isChat && this.isAvatarVisible) {
            i = 48;
        }
        return AndroidUtilities.m9dp((float) (i + (!this.mediaBackground ? 3 : 9)));
    }

    public boolean hasNameLayout() {
        return (this.drawNameLayout && this.nameLayout != null) || ((this.drawForwardedName && this.forwardedNameLayout[0] != null && this.forwardedNameLayout[1] != null && (this.currentPosition == null || (this.currentPosition.minY == (byte) 0 && this.currentPosition.minX == (byte) 0))) || this.replyNameLayout != null);
    }

    public void drawNamesLayout(Canvas canvas) {
        float f;
        int backWidth;
        int i;
        float f2 = 11.0f;
        int i2 = 0;
        if (this.drawNameLayout && this.nameLayout != null) {
            canvas.save();
            if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                Theme.chat_namePaint.setColor(Theme.getColor(Theme.key_chat_stickerNameText));
                if (this.currentMessageObject.isOutOwner()) {
                    this.nameX = (float) AndroidUtilities.m9dp(28.0f);
                } else {
                    this.nameX = (float) ((this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.m9dp(22.0f));
                }
                this.nameY = (float) (this.layoutHeight - AndroidUtilities.m9dp(38.0f));
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(((int) this.nameX) - AndroidUtilities.m9dp(12.0f), ((int) this.nameY) - AndroidUtilities.m9dp(5.0f), (((int) this.nameX) + AndroidUtilities.m9dp(12.0f)) + this.nameWidth, ((int) this.nameY) + AndroidUtilities.m9dp(22.0f));
                Theme.chat_systemDrawable.draw(canvas);
            } else {
                if (this.mediaBackground || this.currentMessageObject.isOutOwner()) {
                    this.nameX = ((float) (this.backgroundDrawableLeft + AndroidUtilities.m9dp(11.0f))) - this.nameOffsetX;
                } else {
                    int i3 = this.backgroundDrawableLeft;
                    f = (this.mediaBackground || !this.drawPinnedBottom) ? 17.0f : 11.0f;
                    this.nameX = ((float) (AndroidUtilities.m9dp(f) + i3)) - this.nameOffsetX;
                }
                if (this.currentUser != null) {
                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentUser.var_id));
                } else if (this.currentChat == null) {
                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(0));
                } else if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentChat.var_id));
                } else {
                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(5));
                }
                if (this.drawPinnedTop) {
                    f = 9.0f;
                } else {
                    f = 10.0f;
                }
                this.nameY = (float) AndroidUtilities.m9dp(f);
            }
            canvas.translate(this.nameX, this.nameY);
            this.nameLayout.draw(canvas);
            canvas.restore();
            if (this.adminLayout != null) {
                Theme.chat_adminPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_adminSelectedText : Theme.key_chat_adminText));
                canvas.save();
                canvas.translate(((float) ((this.backgroundDrawableLeft + this.backgroundDrawableRight) - AndroidUtilities.m9dp(11.0f))) - this.adminLayout.getLineWidth(0), this.nameY + ((float) AndroidUtilities.m9dp(0.5f)));
                this.adminLayout.draw(canvas);
                canvas.restore();
            }
        }
        if (this.drawForwardedName && this.forwardedNameLayout[0] != null && this.forwardedNameLayout[1] != null && (this.currentPosition == null || (this.currentPosition.minY == (byte) 0 && this.currentPosition.minX == (byte) 0))) {
            if (this.currentMessageObject.type == 5) {
                Theme.chat_forwardNamePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyNameText));
                if (this.currentMessageObject.isOutOwner()) {
                    this.forwardNameX = AndroidUtilities.m9dp(23.0f);
                } else {
                    this.forwardNameX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.m9dp(17.0f);
                }
                this.forwardNameY = AndroidUtilities.m9dp(12.0f);
                backWidth = this.forwardedNameWidth + AndroidUtilities.m9dp(14.0f);
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(this.forwardNameX - AndroidUtilities.m9dp(7.0f), this.forwardNameY - AndroidUtilities.m9dp(6.0f), (this.forwardNameX - AndroidUtilities.m9dp(7.0f)) + backWidth, this.forwardNameY + AndroidUtilities.m9dp(38.0f));
                Theme.chat_systemDrawable.draw(canvas);
            } else {
                if (this.drawNameLayout) {
                    i = 19;
                } else {
                    i = 0;
                }
                this.forwardNameY = AndroidUtilities.m9dp((float) (i + 10));
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_forwardNamePaint.setColor(Theme.getColor(Theme.key_chat_outForwardedNameText));
                    this.forwardNameX = this.backgroundDrawableLeft + AndroidUtilities.m9dp(11.0f);
                } else {
                    Theme.chat_forwardNamePaint.setColor(Theme.getColor(Theme.key_chat_inForwardedNameText));
                    if (this.mediaBackground) {
                        this.forwardNameX = this.backgroundDrawableLeft + AndroidUtilities.m9dp(11.0f);
                    } else {
                        i = this.backgroundDrawableLeft;
                        if (this.mediaBackground || !this.drawPinnedBottom) {
                            f2 = 17.0f;
                        }
                        this.forwardNameX = i + AndroidUtilities.m9dp(f2);
                    }
                }
            }
            for (int a = 0; a < 2; a++) {
                canvas.save();
                canvas.translate(((float) this.forwardNameX) - this.forwardNameOffsetX[a], (float) (this.forwardNameY + (AndroidUtilities.m9dp(16.0f) * a)));
                this.forwardedNameLayout[a].draw(canvas);
                canvas.restore();
            }
        }
        if (this.replyNameLayout != null) {
            if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyLine));
                Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyNameText));
                Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyMessageText));
                if (this.currentMessageObject.isOutOwner()) {
                    this.replyStartX = AndroidUtilities.m9dp(23.0f);
                } else if (this.currentMessageObject.type == 5) {
                    this.replyStartX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.m9dp(4.0f);
                } else {
                    this.replyStartX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.m9dp(17.0f);
                }
                this.replyStartY = AndroidUtilities.m9dp(12.0f);
                backWidth = Math.max(this.replyNameWidth, this.replyTextWidth) + AndroidUtilities.m9dp(14.0f);
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(this.replyStartX - AndroidUtilities.m9dp(7.0f), this.replyStartY - AndroidUtilities.m9dp(6.0f), (this.replyStartX - AndroidUtilities.m9dp(7.0f)) + backWidth, this.replyStartY + AndroidUtilities.m9dp(41.0f));
                Theme.chat_systemDrawable.draw(canvas);
            } else {
                int i4;
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_outReplyLine));
                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_outReplyNameText));
                    if (!this.currentMessageObject.hasValidReplyMessageObject() || this.currentMessageObject.replyMessageObject.type != 0 || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                        Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outReplyMediaMessageSelectedText : Theme.key_chat_outReplyMediaMessageText));
                    } else {
                        Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_outReplyMessageText));
                    }
                    this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.m9dp(12.0f);
                } else {
                    Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_inReplyLine));
                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_inReplyNameText));
                    if (!this.currentMessageObject.hasValidReplyMessageObject() || this.currentMessageObject.replyMessageObject.type != 0 || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                        Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inReplyMediaMessageSelectedText : Theme.key_chat_inReplyMediaMessageText));
                    } else {
                        Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_inReplyMessageText));
                    }
                    if (this.mediaBackground) {
                        this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.m9dp(12.0f);
                    } else {
                        i4 = this.backgroundDrawableLeft;
                        f = (this.mediaBackground || !this.drawPinnedBottom) ? 18.0f : 12.0f;
                        this.replyStartX = AndroidUtilities.m9dp(f) + i4;
                    }
                }
                if (!this.drawForwardedName || this.forwardedNameLayout[0] == null) {
                    i = 0;
                } else {
                    i = 36;
                }
                i4 = i + 12;
                if (!this.drawNameLayout || this.nameLayout == null) {
                    i = 0;
                } else {
                    i = 20;
                }
                this.replyStartY = AndroidUtilities.m9dp((float) (i + i4));
            }
            if (this.currentPosition == null || (this.currentPosition.minY == (byte) 0 && this.currentPosition.minX == (byte) 0)) {
                canvas.drawRect((float) this.replyStartX, (float) this.replyStartY, (float) (this.replyStartX + AndroidUtilities.m9dp(2.0f)), (float) (this.replyStartY + AndroidUtilities.m9dp(35.0f)), Theme.chat_replyLinePaint);
                if (this.needReplyImage) {
                    this.replyImageReceiver.setImageCoords(this.replyStartX + AndroidUtilities.m9dp(10.0f), this.replyStartY, AndroidUtilities.m9dp(35.0f), AndroidUtilities.m9dp(35.0f));
                    this.replyImageReceiver.draw(canvas);
                }
                if (this.replyNameLayout != null) {
                    canvas.save();
                    canvas.translate(((float) AndroidUtilities.m9dp((float) ((this.needReplyImage ? 44 : 0) + 10))) + (((float) this.replyStartX) - this.replyNameOffset), (float) this.replyStartY);
                    this.replyNameLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.replyTextLayout != null) {
                    canvas.save();
                    f = ((float) this.replyStartX) - this.replyTextOffset;
                    if (this.needReplyImage) {
                        i2 = 44;
                    }
                    canvas.translate(f + ((float) AndroidUtilities.m9dp((float) (i2 + 10))), (float) (this.replyStartY + AndroidUtilities.m9dp(19.0f)));
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
                    FileLog.m13e(e);
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
                Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
            } else if (this.mediaBackground && this.captionLayout == null) {
                if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                    Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_serviceText));
                } else {
                    Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
                }
            } else if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
            } else {
                Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
            }
            if (this.drawPinnedBottom) {
                canvas.translate(0.0f, (float) AndroidUtilities.m9dp(2.0f));
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
                int x1 = this.timeX - AndroidUtilities.m9dp(4.0f);
                int y1 = this.layoutHeight - AndroidUtilities.m9dp(28.0f);
                this.rect.set((float) x1, (float) y1, (float) (AndroidUtilities.m9dp((float) ((this.currentMessageObject.isOutOwner() ? 20 : 0) + 8)) + (x1 + this.timeWidth)), (float) (AndroidUtilities.m9dp(17.0f) + y1));
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(4.0f), paint);
                paint.setAlpha(oldAlpha);
                additionalX = (int) (-this.timeLayout.getLineLeft(0));
                if ((this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                    additionalX += (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(0));
                    if (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing()) {
                        if (!this.currentMessageObject.isOutOwner()) {
                            BaseCell.setDrawableBounds(Theme.chat_msgMediaClockDrawable, this.timeX + AndroidUtilities.m9dp(11.0f), (this.layoutHeight - AndroidUtilities.m9dp(14.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
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
                        BaseCell.setDrawableBounds(viewsDrawable, this.timeX, (this.layoutHeight - AndroidUtilities.m9dp(10.5f)) - this.timeLayout.getHeight());
                        viewsDrawable.draw(canvas);
                        viewsDrawable.setAlpha(oldAlpha);
                        if (this.viewsLayout != null) {
                            canvas.save();
                            canvas.translate((float) ((this.timeX + viewsDrawable.getIntrinsicWidth()) + AndroidUtilities.m9dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.m9dp(12.3f)) - this.timeLayout.getHeight()));
                            this.viewsLayout.draw(canvas);
                            canvas.restore();
                        }
                    } else if (!this.currentMessageObject.isOutOwner()) {
                        x = this.timeX + AndroidUtilities.m9dp(11.0f);
                        y = this.layoutHeight - AndroidUtilities.m9dp(27.5f);
                        this.rect.set((float) x, (float) y, (float) (AndroidUtilities.m9dp(14.0f) + x), (float) (AndroidUtilities.m9dp(14.0f) + y));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(1.0f), (float) AndroidUtilities.m9dp(1.0f), Theme.chat_msgErrorPaint);
                        BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, AndroidUtilities.m9dp(6.0f) + x, AndroidUtilities.m9dp(2.0f) + y);
                        Theme.chat_msgErrorDrawable.draw(canvas);
                    }
                }
                canvas.save();
                canvas.translate((float) (this.timeX + additionalX), (float) ((this.layoutHeight - AndroidUtilities.m9dp(12.3f)) - this.timeLayout.getHeight()));
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
                            BaseCell.setDrawableBounds(clockDrawable, this.timeX + AndroidUtilities.m9dp(11.0f), (this.layoutHeight - AndroidUtilities.m9dp(8.5f)) - clockDrawable.getIntrinsicHeight());
                            clockDrawable.draw(canvas);
                        }
                    } else if (!this.currentMessageObject.isSendError()) {
                        if (this.currentMessageObject.isOutOwner()) {
                            viewsDrawable = isDrawSelectedBackground() ? Theme.chat_msgOutViewsSelectedDrawable : Theme.chat_msgOutViewsDrawable;
                            BaseCell.setDrawableBounds(viewsDrawable, this.timeX, (this.layoutHeight - AndroidUtilities.m9dp(4.5f)) - this.timeLayout.getHeight());
                            viewsDrawable.draw(canvas);
                        } else {
                            viewsDrawable = isDrawSelectedBackground() ? Theme.chat_msgInViewsSelectedDrawable : Theme.chat_msgInViewsDrawable;
                            BaseCell.setDrawableBounds(viewsDrawable, this.timeX, (this.layoutHeight - AndroidUtilities.m9dp(4.5f)) - this.timeLayout.getHeight());
                            viewsDrawable.draw(canvas);
                        }
                        if (this.viewsLayout != null) {
                            canvas.save();
                            canvas.translate((float) ((this.timeX + Theme.chat_msgInViewsDrawable.getIntrinsicWidth()) + AndroidUtilities.m9dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.m9dp(6.5f)) - this.timeLayout.getHeight()));
                            this.viewsLayout.draw(canvas);
                            canvas.restore();
                        }
                    } else if (!this.currentMessageObject.isOutOwner()) {
                        x = this.timeX + AndroidUtilities.m9dp(11.0f);
                        y = this.layoutHeight - AndroidUtilities.m9dp(20.5f);
                        this.rect.set((float) x, (float) y, (float) (AndroidUtilities.m9dp(14.0f) + x), (float) (AndroidUtilities.m9dp(14.0f) + y));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(1.0f), (float) AndroidUtilities.m9dp(1.0f), Theme.chat_msgErrorPaint);
                        BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, AndroidUtilities.m9dp(6.0f) + x, AndroidUtilities.m9dp(2.0f) + y);
                        Theme.chat_msgErrorDrawable.draw(canvas);
                    }
                }
                canvas.save();
                canvas.translate((float) (this.timeX + additionalX), (float) ((this.layoutHeight - AndroidUtilities.m9dp(6.5f)) - this.timeLayout.getHeight()));
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
                        BaseCell.setDrawableBounds(Theme.chat_msgOutClockDrawable, (this.layoutWidth - AndroidUtilities.m9dp(18.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(8.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicHeight());
                        Theme.chat_msgOutClockDrawable.draw(canvas);
                    } else if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                        Theme.chat_msgStickerClockDrawable.setAlpha((int) (255.0f * this.timeAlpha));
                        BaseCell.setDrawableBounds(Theme.chat_msgStickerClockDrawable, (this.layoutWidth - AndroidUtilities.m9dp(22.0f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(13.5f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicHeight());
                        Theme.chat_msgStickerClockDrawable.draw(canvas);
                        Theme.chat_msgStickerClockDrawable.setAlpha(255);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_msgMediaClockDrawable, (this.layoutWidth - AndroidUtilities.m9dp(22.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(13.5f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                        Theme.chat_msgMediaClockDrawable.draw(canvas);
                    }
                }
                if (!isBroadcast) {
                    Drawable drawable;
                    if (drawCheck2) {
                        if (!this.mediaBackground || this.captionLayout != null) {
                            drawable = isDrawSelectedBackground() ? Theme.chat_msgOutCheckSelectedDrawable : Theme.chat_msgOutCheckDrawable;
                            if (drawCheck1) {
                                BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.m9dp(22.5f)) - drawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(8.0f)) - drawable.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.m9dp(18.5f)) - drawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(8.0f)) - drawable.getIntrinsicHeight());
                            }
                            drawable.draw(canvas);
                        } else if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                            if (drawCheck1) {
                                BaseCell.setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.layoutWidth - AndroidUtilities.m9dp(26.3f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(13.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.layoutWidth - AndroidUtilities.m9dp(21.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(13.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgStickerCheckDrawable.draw(canvas);
                        } else {
                            if (drawCheck1) {
                                BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.m9dp(26.3f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(13.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.m9dp(21.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(13.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgMediaCheckDrawable.setAlpha((int) (255.0f * this.timeAlpha));
                            Theme.chat_msgMediaCheckDrawable.draw(canvas);
                            Theme.chat_msgMediaCheckDrawable.setAlpha(255);
                        }
                    }
                    if (drawCheck1) {
                        if (!this.mediaBackground || this.captionLayout != null) {
                            drawable = isDrawSelectedBackground() ? Theme.chat_msgOutHalfCheckSelectedDrawable : Theme.chat_msgOutHalfCheckDrawable;
                            BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.m9dp(18.0f)) - drawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(8.0f)) - drawable.getIntrinsicHeight());
                            drawable.draw(canvas);
                        } else if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                            BaseCell.setDrawableBounds(Theme.chat_msgStickerHalfCheckDrawable, (this.layoutWidth - AndroidUtilities.m9dp(21.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(13.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicHeight());
                            Theme.chat_msgStickerHalfCheckDrawable.draw(canvas);
                        } else {
                            BaseCell.setDrawableBounds(Theme.chat_msgMediaHalfCheckDrawable, (this.layoutWidth - AndroidUtilities.m9dp(21.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(13.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicHeight());
                            Theme.chat_msgMediaHalfCheckDrawable.setAlpha((int) (255.0f * this.timeAlpha));
                            Theme.chat_msgMediaHalfCheckDrawable.draw(canvas);
                            Theme.chat_msgMediaHalfCheckDrawable.setAlpha(255);
                        }
                    }
                } else if (drawCheck1 || drawCheck2) {
                    if (this.mediaBackground && this.captionLayout == null) {
                        BaseCell.setDrawableBounds(Theme.chat_msgBroadcastMediaDrawable, (this.layoutWidth - AndroidUtilities.m9dp(24.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(14.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicHeight());
                        Theme.chat_msgBroadcastMediaDrawable.draw(canvas);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_msgBroadcastDrawable, (this.layoutWidth - AndroidUtilities.m9dp(20.5f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.m9dp(8.0f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicHeight());
                        Theme.chat_msgBroadcastDrawable.draw(canvas);
                    }
                }
                if (drawError) {
                    if (this.mediaBackground && this.captionLayout == null) {
                        x = this.layoutWidth - AndroidUtilities.m9dp(34.5f);
                        y = this.layoutHeight - AndroidUtilities.m9dp(26.5f);
                    } else {
                        x = this.layoutWidth - AndroidUtilities.m9dp(32.0f);
                        y = this.layoutHeight - AndroidUtilities.m9dp(21.0f);
                    }
                    this.rect.set((float) x, (float) y, (float) (AndroidUtilities.m9dp(14.0f) + x), (float) (AndroidUtilities.m9dp(14.0f) + y));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(1.0f), (float) AndroidUtilities.m9dp(1.0f), Theme.chat_msgErrorPaint);
                    BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, AndroidUtilities.m9dp(6.0f) + x, AndroidUtilities.m9dp(2.0f) + y);
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
