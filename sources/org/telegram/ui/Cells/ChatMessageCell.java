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
import net.hockeyapp.android.UpdateFragment;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
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
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
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
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.RoundVideoPlayingDrawable;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.StaticLayoutEx;
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
    private Runnable invalidateRunnable = new C09131();
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
    class C09131 implements Runnable {
        C09131() {
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

    private class BotButton {
        private int angle;
        private KeyboardButton button;
        private int height;
        private long lastUpdateTime;
        private float progressAlpha;
        private StaticLayout title;
        private int width;
        /* renamed from: x */
        private int f9x;
        /* renamed from: y */
        private int f10y;

        private BotButton() {
        }
    }

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
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.replyImageReceiver = new ImageReceiver(this);
        this.locationImageReceiver = new ImageReceiver(this);
        this.locationImageReceiver.setRoundRadius(AndroidUtilities.dp(26.1f));
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
                                            nextLink = (CharacterStyle[]) buffer.getSpans(nextBlock.charactersOffset, nextBlock.charactersOffset, isMono ? URLSpanMono.class : ClickableSpan.class);
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
                                            nextLink = (CharacterStyle[]) buffer.getSpans(nextBlock.charactersEnd - 1, nextBlock.charactersEnd - 1, isMono ? URLSpanMono.class : ClickableSpan.class);
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
                                    FileLog.m3e(e);
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
                    FileLog.m3e(e2);
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
                                FileLog.m3e(e);
                            }
                            if (!(this.currentMessagesGroup == null || getParent() == null)) {
                                ((ViewGroup) getParent()).invalidate();
                            }
                            invalidate();
                            return true;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
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
                                FileLog.m3e(e);
                            }
                            invalidate();
                            return true;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
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
                                            FileLog.m3e(e);
                                        }
                                        invalidate();
                                        return true;
                                    }
                                }
                            }
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
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
                        } else if (this.drawPhotoImage && this.drawImageButton && this.buttonState != -1 && x >= this.buttonX && x <= this.buttonX + AndroidUtilities.dp(48.0f) && y >= this.buttonY && y <= this.buttonY + AndroidUtilities.dp(48.0f)) {
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
                                MediaController.getInstance().pauseMessage(this.currentMessageObject);
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
            result = this.seekBarWaveform.onTouch(event.getAction(), (event.getX() - ((float) this.seekBarX)) - ((float) AndroidUtilities.dp(13.0f)), event.getY() - ((float) this.seekBarY));
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
                addX = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                addX = this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
            }
            int a = 0;
            while (a < this.botButtons.size()) {
                BotButton button = (BotButton) this.botButtons.get(a);
                int y2 = (button.f10y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                if (x < button.f9x + addX || x > (button.f9x + addX) + button.width || y < y2 || y > button.height + y2) {
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
                    this.delegate.didPressedViaBot(this, this.currentViaBotUser != null ? this.currentViaBotUser.username : this.currentMessageObject.messageOwner.via_bot_name);
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
                        this.delegate.didPressedShare(this);
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
            FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
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
                    if (stringBuilder.charAt(pos + addedChars) != '\n') {
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
                MediaController.getInstance().pauseMessage(this.currentMessageObject);
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
                double rad = ((double) 268435456) / 3.141592653589793d;
                url = AndroidUtilities.formapMapUrl(this.currentAccount, ((1.5707963267948966d - (2.0d * Math.atan(Math.exp((((double) (Math.round(((double) 268435456) - ((Math.log((1.0d + Math.sin((3.141592653589793d * lat) / 180.0d)) / (1.0d - Math.sin((3.141592653589793d * lat) / 180.0d))) * rad) / 2.0d)) - ((long) (AndroidUtilities.dp(10.3f) << 6)))) - ((double) 268435456)) / rad)))) * 180.0d) / 3.141592653589793d, lon, (int) (((float) (this.backgroundWidth - AndroidUtilities.dp(21.0f))) / AndroidUtilities.density), (int) (((float) AndroidUtilities.dp(195.0f)) / AndroidUtilities.density), false, 15);
            } else if (TextUtils.isEmpty(object.messageOwner.media.title)) {
                url = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, Callback.DEFAULT_DRAG_ANIMATION_DURATION, 100, true, 15);
            } else {
                url = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, 72, 72, true, 15);
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
            this.songLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicTitle().replace('\n', ' '), Theme.chat_audioTitlePaint, (float) (maxWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), Theme.chat_audioTitlePaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.songLayout.getLineCount() > 0) {
                this.songX = -((int) Math.ceil((double) this.songLayout.getLineLeft(0)));
            }
            this.performerLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicAuthor().replace('\n', ' '), Theme.chat_audioPerformerPaint, (float) maxWidth, TruncateAt.END), Theme.chat_audioPerformerPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
            boolean z = (this.documentAttach.mime_type != null && this.documentAttach.mime_type.toLowerCase().startsWith("image/")) || !(this.documentAttach.thumb == null || (this.documentAttach.thumb instanceof TL_photoSizeEmpty) || (this.documentAttach.thumb.location instanceof TL_fileLocationUnavailable));
            this.drawPhotoImage = z;
            if (!this.drawPhotoImage) {
                maxWidth += AndroidUtilities.dp(30.0f);
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
            this.infoWidth = Math.min(maxWidth - AndroidUtilities.dp(30.0f), (int) Math.ceil((double) Theme.chat_infoPaint.measureText(str)));
            CharSequence str2 = TextUtils.ellipsize(str, Theme.chat_infoPaint, (float) this.infoWidth, TruncateAt.END);
            try {
                if (this.infoWidth < 0) {
                    this.infoWidth = AndroidUtilities.dp(10.0f);
                }
                this.infoLayout = new StaticLayout(str2, Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (this.drawPhotoImage) {
                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                this.photoImage.setNeedsQualityThumb(true);
                this.photoImage.setShouldGenerateQualityThumb(true);
                this.photoImage.setParentMessageObject(messageObject);
                if (this.currentPhotoObject != null) {
                    this.currentPhotoFilter = "86_86_b";
                    this.photoImage.setImage(null, null, null, null, this.currentPhotoObject.location, this.currentPhotoFilter, 0, null, 1);
                } else {
                    this.photoImage.setImageBitmap((BitmapDrawable) null);
                }
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
                            FileLog.m3e(e);
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
                this.docTitleLayout = new StaticLayout(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation), Theme.chat_locationTitlePaint, this.backgroundWidth - AndroidUtilities.dp(91.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
    }

    public void setMessageObject(org.telegram.messenger.MessageObject r147, org.telegram.messenger.MessageObject.GroupedMessages r148, boolean r149, boolean r150) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r105_0 'oldByPosition' java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton>) in PHI: PHI: (r105_1 'oldByPosition' java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton>) = (r105_0 'oldByPosition' java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton>), (r105_2 'oldByPosition' java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton>) binds: {(r105_0 'oldByPosition' java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton>)=B:1693:0x3895, (r105_2 'oldByPosition' java.util.HashMap<java.lang.String, org.telegram.ui.Cells.ChatMessageCell$BotButton>)=B:1706:0x38fd}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r146 = this;
        r4 = r147.checkLayout();
        if (r4 != 0) goto L_0x0016;
    L_0x0006:
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x001b;
    L_0x000c:
        r0 = r146;
        r4 = r0.lastHeight;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        if (r4 == r6) goto L_0x001b;
    L_0x0016:
        r4 = 0;
        r0 = r146;
        r0.currentMessageObject = r4;
    L_0x001b:
        r0 = r146;
        r4 = r0.currentMessageObject;
        if (r4 == 0) goto L_0x002f;
    L_0x0021:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.getId();
        r6 = r147.getId();
        if (r4 == r6) goto L_0x0853;
    L_0x002f:
        r98 = 1;
    L_0x0031:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r0 = r147;
        if (r4 != r0) goto L_0x003f;
    L_0x0039:
        r0 = r147;
        r4 = r0.forceUpdate;
        if (r4 == 0) goto L_0x0857;
    L_0x003f:
        r97 = 1;
    L_0x0041:
        r0 = r146;
        r4 = r0.currentMessageObject;
        if (r4 == 0) goto L_0x0062;
    L_0x0047:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.getId();
        r6 = r147.getId();
        if (r4 != r6) goto L_0x0062;
    L_0x0055:
        r0 = r146;
        r4 = r0.lastSendState;
        r6 = 3;
        if (r4 != r6) goto L_0x0062;
    L_0x005c:
        r4 = r147.isSent();
        if (r4 != 0) goto L_0x0076;
    L_0x0062:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r0 = r147;
        if (r4 != r0) goto L_0x085b;
    L_0x006a:
        r4 = r146.isUserDataChanged();
        if (r4 != 0) goto L_0x0076;
    L_0x0070:
        r0 = r146;
        r4 = r0.photoNotSet;
        if (r4 == 0) goto L_0x085b;
    L_0x0076:
        r63 = 1;
    L_0x0078:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r0 = r148;
        if (r0 == r4) goto L_0x085f;
    L_0x0080:
        r74 = 1;
    L_0x0082:
        if (r74 != 0) goto L_0x00ab;
    L_0x0084:
        if (r148 == 0) goto L_0x00ab;
    L_0x0086:
        r0 = r148;
        r4 = r0.messages;
        r4 = r4.size();
        r6 = 1;
        if (r4 <= r6) goto L_0x0863;
    L_0x0091:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.positions;
        r0 = r146;
        r6 = r0.currentMessageObject;
        r100 = r4.get(r6);
        r100 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r100;
    L_0x00a1:
        r0 = r146;
        r4 = r0.currentPosition;
        r0 = r100;
        if (r0 == r4) goto L_0x0867;
    L_0x00a9:
        r74 = 1;
    L_0x00ab:
        if (r97 != 0) goto L_0x00c7;
    L_0x00ad:
        if (r63 != 0) goto L_0x00c7;
    L_0x00af:
        if (r74 != 0) goto L_0x00c7;
    L_0x00b1:
        r4 = r146.isPhotoDataChanged(r147);
        if (r4 != 0) goto L_0x00c7;
    L_0x00b7:
        r0 = r146;
        r4 = r0.pinnedBottom;
        r0 = r149;
        if (r4 != r0) goto L_0x00c7;
    L_0x00bf:
        r0 = r146;
        r4 = r0.pinnedTop;
        r0 = r150;
        if (r4 == r0) goto L_0x3abe;
    L_0x00c7:
        r0 = r149;
        r1 = r146;
        r1.pinnedBottom = r0;
        r0 = r150;
        r1 = r146;
        r1.pinnedTop = r0;
        r4 = -2;
        r0 = r146;
        r0.lastTime = r4;
        r4 = 0;
        r0 = r146;
        r0.isHighlightedAnimated = r4;
        r4 = -1;
        r0 = r146;
        r0.widthBeforeNewTimeLine = r4;
        r0 = r147;
        r1 = r146;
        r1.currentMessageObject = r0;
        r0 = r148;
        r1 = r146;
        r1.currentMessagesGroup = r0;
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        if (r4 == 0) goto L_0x086b;
    L_0x00f4:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r6 = 1;
        if (r4 <= r6) goto L_0x086b;
    L_0x0101:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.positions;
        r0 = r146;
        r6 = r0.currentMessageObject;
        r4 = r4.get(r6);
        r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4;
        r0 = r146;
        r0.currentPosition = r4;
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 != 0) goto L_0x0120;
    L_0x011b:
        r4 = 0;
        r0 = r146;
        r0.currentMessagesGroup = r4;
    L_0x0120:
        r0 = r146;
        r4 = r0.pinnedTop;
        if (r4 == 0) goto L_0x0877;
    L_0x0126:
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x0136;
    L_0x012c:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x0877;
    L_0x0136:
        r4 = 1;
    L_0x0137:
        r0 = r146;
        r0.drawPinnedTop = r4;
        r0 = r146;
        r4 = r0.pinnedBottom;
        if (r4 == 0) goto L_0x087a;
    L_0x0141:
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x0151;
    L_0x0147:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 == 0) goto L_0x087a;
    L_0x0151:
        r4 = 1;
    L_0x0152:
        r0 = r146;
        r0.drawPinnedBottom = r4;
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setCrossfadeWithOldImage(r6);
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.send_state;
        r0 = r146;
        r0.lastSendState = r4;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.destroyTime;
        r0 = r146;
        r0.lastDeleteDate = r4;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.views;
        r0 = r146;
        r0.lastViewsCount = r4;
        r4 = 0;
        r0 = r146;
        r0.isPressed = r4;
        r4 = 0;
        r0 = r146;
        r0.gamePreviewPressed = r4;
        r4 = 1;
        r0 = r146;
        r0.isCheckPressed = r4;
        r4 = 0;
        r0 = r146;
        r0.hasNewLineForTime = r4;
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x087d;
    L_0x0196:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x087d;
    L_0x019c:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x087d;
    L_0x01a2:
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x01b0;
    L_0x01a8:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.edge;
        if (r4 == 0) goto L_0x087d;
    L_0x01b0:
        r4 = 1;
    L_0x01b1:
        r0 = r146;
        r0.isAvatarVisible = r4;
        r4 = 0;
        r0 = r146;
        r0.wasLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.drwaShareGoIcon = r4;
        r4 = 0;
        r0 = r146;
        r0.groupPhotoInvisible = r4;
        r4 = r146.checkNeedDrawShareButton(r147);
        r0 = r146;
        r0.drawShareButton = r4;
        r4 = 0;
        r0 = r146;
        r0.replyNameLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.adminLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.replyTextLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.replyNameWidth = r4;
        r4 = 0;
        r0 = r146;
        r0.replyTextWidth = r4;
        r4 = 0;
        r0 = r146;
        r0.viaWidth = r4;
        r4 = 0;
        r0 = r146;
        r0.viaNameWidth = r4;
        r4 = 0;
        r0 = r146;
        r0.addedCaptionHeight = r4;
        r4 = 0;
        r0 = r146;
        r0.currentReplyPhoto = r4;
        r4 = 0;
        r0 = r146;
        r0.currentUser = r4;
        r4 = 0;
        r0 = r146;
        r0.currentChat = r4;
        r4 = 0;
        r0 = r146;
        r0.currentViaBotUser = r4;
        r4 = 0;
        r0 = r146;
        r0.instantViewLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.drawNameLayout = r4;
        r0 = r146;
        r4 = r0.scheduledInvalidate;
        if (r4 == 0) goto L_0x0224;
    L_0x0218:
        r0 = r146;
        r4 = r0.invalidateRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4);
        r4 = 0;
        r0 = r146;
        r0.scheduledInvalidate = r4;
    L_0x0224:
        r4 = -1;
        r0 = r146;
        r0.resetPressedLink(r4);
        r4 = 0;
        r0 = r147;
        r0.forceUpdate = r4;
        r4 = 0;
        r0 = r146;
        r0.drawPhotoImage = r4;
        r4 = 0;
        r0 = r146;
        r0.hasLinkPreview = r4;
        r4 = 0;
        r0 = r146;
        r0.hasOldCaptionPreview = r4;
        r4 = 0;
        r0 = r146;
        r0.hasGamePreview = r4;
        r4 = 0;
        r0 = r146;
        r0.hasInvoicePreview = r4;
        r4 = 0;
        r0 = r146;
        r0.instantButtonPressed = r4;
        r0 = r146;
        r0.instantPressed = r4;
        r4 = android.os.Build.VERSION.SDK_INT;
        r6 = 21;
        if (r4 < r6) goto L_0x026f;
    L_0x0257:
        r0 = r146;
        r4 = r0.instantViewSelectorDrawable;
        if (r4 == 0) goto L_0x026f;
    L_0x025d:
        r0 = r146;
        r4 = r0.instantViewSelectorDrawable;
        r6 = 0;
        r8 = 0;
        r4.setVisible(r6, r8);
        r0 = r146;
        r4 = r0.instantViewSelectorDrawable;
        r6 = android.util.StateSet.NOTHING;
        r4.setState(r6);
    L_0x026f:
        r4 = 0;
        r0 = r146;
        r0.linkPreviewPressed = r4;
        r4 = 0;
        r0 = r146;
        r0.buttonPressed = r4;
        r4 = 0;
        r0 = r146;
        r0.miniButtonPressed = r4;
        r4 = -1;
        r0 = r146;
        r0.pressedBotButton = r4;
        r4 = 0;
        r0 = r146;
        r0.linkPreviewHeight = r4;
        r4 = 0;
        r0 = r146;
        r0.mediaOffsetY = r4;
        r4 = 0;
        r0 = r146;
        r0.documentAttachType = r4;
        r4 = 0;
        r0 = r146;
        r0.documentAttach = r4;
        r4 = 0;
        r0 = r146;
        r0.descriptionLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.titleLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.videoInfoLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.photosCountLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.siteNameLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.authorLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.captionLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.captionOffsetX = r4;
        r4 = 0;
        r0 = r146;
        r0.currentCaption = r4;
        r4 = 0;
        r0 = r146;
        r0.docTitleLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.drawImageButton = r4;
        r4 = 0;
        r0 = r146;
        r0.currentPhotoObject = r4;
        r4 = 0;
        r0 = r146;
        r0.currentPhotoObjectThumb = r4;
        r4 = 0;
        r0 = r146;
        r0.currentPhotoFilter = r4;
        r4 = 0;
        r0 = r146;
        r0.infoLayout = r4;
        r4 = 0;
        r0 = r146;
        r0.cancelLoading = r4;
        r4 = -1;
        r0 = r146;
        r0.buttonState = r4;
        r4 = -1;
        r0 = r146;
        r0.miniButtonState = r4;
        r4 = 0;
        r0 = r146;
        r0.hasMiniProgress = r4;
        r0 = r146;
        r4 = r0.addedForTest;
        if (r4 == 0) goto L_0x0313;
    L_0x02fc:
        r0 = r146;
        r4 = r0.currentUrl;
        if (r4 == 0) goto L_0x0313;
    L_0x0302:
        r0 = r146;
        r4 = r0.currentWebFile;
        if (r4 == 0) goto L_0x0313;
    L_0x0308:
        r4 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r146;
        r6 = r0.currentUrl;
        r4.removeTestWebFile(r6);
    L_0x0313:
        r4 = 0;
        r0 = r146;
        r0.addedForTest = r4;
        r4 = 0;
        r0 = r146;
        r0.currentUrl = r4;
        r4 = 0;
        r0 = r146;
        r0.currentWebFile = r4;
        r4 = 0;
        r0 = r146;
        r0.photoNotSet = r4;
        r4 = 1;
        r0 = r146;
        r0.drawBackground = r4;
        r4 = 0;
        r0 = r146;
        r0.drawName = r4;
        r4 = 0;
        r0 = r146;
        r0.useSeekBarWaweform = r4;
        r4 = 0;
        r0 = r146;
        r0.drawInstantView = r4;
        r4 = 0;
        r0 = r146;
        r0.drawInstantViewType = r4;
        r4 = 0;
        r0 = r146;
        r0.drawForwardedName = r4;
        r4 = 0;
        r0 = r146;
        r0.mediaBackground = r4;
        r57 = 0;
        r4 = 0;
        r0 = r146;
        r0.availableTimeWidth = r4;
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setForceLoading(r6);
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setNeedsQualityThumb(r6);
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setShouldGenerateQualityThumb(r6);
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setAllowDecodeSingleFrame(r6);
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 0;
        r4.setParentMessageObject(r6);
        r0 = r146;
        r4 = r0.photoImage;
        r6 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setRoundRadius(r6);
        if (r97 == 0) goto L_0x0397;
    L_0x0388:
        r4 = 0;
        r0 = r146;
        r0.firstVisibleBlockNum = r4;
        r4 = 0;
        r0 = r146;
        r0.lastVisibleBlockNum = r4;
        r4 = 1;
        r0 = r146;
        r0.needNewVisiblePart = r4;
    L_0x0397:
        r0 = r147;
        r4 = r0.type;
        if (r4 != 0) goto L_0x1c53;
    L_0x039d:
        r4 = 1;
        r0 = r146;
        r0.drawForwardedName = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x08a5;
    L_0x03a8:
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x0880;
    L_0x03ae:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x0880;
    L_0x03b4:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x0880;
    L_0x03ba:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x42f40000 float:122.0 double:5.54977537E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r26 = r4 - r6;
        r4 = 1;
        r0 = r146;
        r0.drawName = r4;
    L_0x03cb:
        r0 = r26;
        r1 = r146;
        r1.availableTimeWidth = r0;
        r4 = r147.isRoundVideo();
        if (r4 == 0) goto L_0x03fc;
    L_0x03d7:
        r0 = r146;
        r4 = r0.availableTimeWidth;
        r8 = (double) r4;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r6 = "00:00";
        r4 = r4.measureText(r6);
        r10 = (double) r4;
        r10 = java.lang.Math.ceil(r10);
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x08ff;
    L_0x03f0:
        r4 = 0;
    L_0x03f1:
        r0 = (double) r4;
        r24 = r0;
        r10 = r10 + r24;
        r8 = r8 - r10;
        r4 = (int) r8;
        r0 = r146;
        r0.availableTimeWidth = r4;
    L_0x03fc:
        r146.measureTime(r147);
        r0 = r146;
        r4 = r0.timeWidth;
        r6 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r131 = r4 + r6;
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x0419;
    L_0x0411:
        r4 = NUM; // 0x41a40000 float:20.5 double:5.44098164E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r131 = r131 + r4;
    L_0x0419:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r4 == 0) goto L_0x0907;
    L_0x0423:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.game;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_game;
        if (r4 == 0) goto L_0x0907;
    L_0x042f:
        r4 = 1;
    L_0x0430:
        r0 = r146;
        r0.hasGamePreview = r4;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
        r0 = r146;
        r0.hasInvoicePreview = r4;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r4 == 0) goto L_0x090a;
    L_0x044a:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r4 == 0) goto L_0x090a;
    L_0x0456:
        r4 = 1;
    L_0x0457:
        r0 = r146;
        r0.hasLinkPreview = r4;
        r0 = r146;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x090d;
    L_0x0461:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.cached_page;
        if (r4 == 0) goto L_0x090d;
    L_0x046d:
        r4 = 1;
    L_0x046e:
        r0 = r146;
        r0.drawInstantView = r4;
        r125 = 0;
        r0 = r146;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0910;
    L_0x047a:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r0 = r4.site_name;
        r124 = r0;
    L_0x0486:
        r0 = r146;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0914;
    L_0x048c:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r0 = r4.type;
        r141 = r0;
    L_0x0498:
        r0 = r146;
        r4 = r0.drawInstantView;
        if (r4 != 0) goto L_0x0946;
    L_0x049e:
        r4 = "telegram_channel";
        r0 = r141;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x0918;
    L_0x04a9:
        r4 = 1;
        r0 = r146;
        r0.drawInstantView = r4;
        r4 = 1;
        r0 = r146;
        r0.drawInstantViewType = r4;
    L_0x04b3:
        r0 = r26;
        r1 = r146;
        r1.backgroundWidth = r0;
        r0 = r146;
        r4 = r0.hasLinkPreview;
        if (r4 != 0) goto L_0x04d5;
    L_0x04bf:
        r0 = r146;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x04d5;
    L_0x04c5:
        r0 = r146;
        r4 = r0.hasInvoicePreview;
        if (r4 != 0) goto L_0x04d5;
    L_0x04cb:
        r0 = r147;
        r4 = r0.lastLineWidth;
        r4 = r26 - r4;
        r0 = r131;
        if (r4 >= r0) goto L_0x0a2f;
    L_0x04d5:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r0 = r147;
        r6 = r0.lastLineWidth;
        r4 = java.lang.Math.max(r4, r6);
        r6 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.backgroundWidth = r4;
        r0 = r146;
        r4 = r0.backgroundWidth;
        r0 = r146;
        r6 = r0.timeWidth;
        r8 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 + r8;
        r4 = java.lang.Math.max(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x0503:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.availableTimeWidth = r4;
        r4 = r147.isRoundVideo();
        if (r4 == 0) goto L_0x053d;
    L_0x0518:
        r0 = r146;
        r4 = r0.availableTimeWidth;
        r8 = (double) r4;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r6 = "00:00";
        r4 = r4.measureText(r6);
        r10 = (double) r4;
        r10 = java.lang.Math.ceil(r10);
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x0a71;
    L_0x0531:
        r4 = 0;
    L_0x0532:
        r0 = (double) r4;
        r24 = r0;
        r10 = r10 + r24;
        r8 = r8 - r10;
        r4 = (int) r8;
        r0 = r146;
        r0.availableTimeWidth = r4;
    L_0x053d:
        r146.setMessageObjectInternal(r147);
        r0 = r147;
        r6 = r0.textWidth;
        r0 = r146;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x0550;
    L_0x054a:
        r0 = r146;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x0a79;
    L_0x0550:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0556:
        r4 = r4 + r6;
        r0 = r146;
        r0.backgroundWidth = r4;
        r0 = r147;
        r4 = r0.textHeight;
        r6 = NUM; // 0x419c0000 float:19.5 double:5.43839131E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x0584;
    L_0x0575:
        r0 = r146;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.namesOffset = r4;
    L_0x0584:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r0 = r146;
        r6 = r0.nameWidth;
        r92 = java.lang.Math.max(r4, r6);
        r0 = r146;
        r4 = r0.forwardedNameWidth;
        r0 = r92;
        r92 = java.lang.Math.max(r0, r4);
        r0 = r146;
        r4 = r0.replyNameWidth;
        r0 = r92;
        r92 = java.lang.Math.max(r0, r4);
        r0 = r146;
        r4 = r0.replyTextWidth;
        r0 = r92;
        r92 = java.lang.Math.max(r0, r4);
        r95 = 0;
        r0 = r146;
        r4 = r0.hasLinkPreview;
        if (r4 != 0) goto L_0x05c2;
    L_0x05b6:
        r0 = r146;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x05c2;
    L_0x05bc:
        r0 = r146;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x1c3c;
    L_0x05c2:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x0a8a;
    L_0x05c8:
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x0a7c;
    L_0x05ce:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x0a7c;
    L_0x05d4:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.isOut();
        if (r4 != 0) goto L_0x0a7c;
    L_0x05de:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r86 = r4 - r6;
    L_0x05ea:
        r0 = r146;
        r4 = r0.drawShareButton;
        if (r4 == 0) goto L_0x05f8;
    L_0x05f0:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r86 = r86 - r4;
    L_0x05f8:
        r0 = r146;
        r4 = r0.hasLinkPreview;
        if (r4 == 0) goto L_0x0ad3;
    L_0x05fe:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.webpage;
        r140 = r0;
        r140 = (org.telegram.tgnet.TLRPC.TL_webPage) r140;
        r0 = r140;
        r7 = r0.site_name;
        r0 = r140;
        r0 = r0.title;
        r133 = r0;
        r0 = r140;
        r0 = r0.author;
        r47 = r0;
        r0 = r140;
        r0 = r0.description;
        r64 = r0;
        r0 = r140;
        r0 = r0.photo;
        r107 = r0;
        r14 = 0;
        r0 = r140;
        r0 = r0.document;
        r66 = r0;
        r0 = r140;
        r0 = r0.type;
        r135 = r0;
        r0 = r140;
        r0 = r0.duration;
        r67 = r0;
        if (r7 == 0) goto L_0x065a;
    L_0x063b:
        if (r107 == 0) goto L_0x065a;
    L_0x063d:
        r4 = r7.toLowerCase();
        r6 = "instagram";
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x065a;
    L_0x064a:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r4 = r4 / 3;
        r0 = r146;
        r6 = r0.currentMessageObject;
        r6 = r6.textWidth;
        r86 = java.lang.Math.max(r4, r6);
    L_0x065a:
        if (r125 != 0) goto L_0x0acc;
    L_0x065c:
        r0 = r146;
        r4 = r0.drawInstantView;
        if (r4 != 0) goto L_0x0acc;
    L_0x0662:
        if (r66 != 0) goto L_0x0acc;
    L_0x0664:
        if (r135 == 0) goto L_0x0acc;
    L_0x0666:
        r4 = "app";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0687;
    L_0x0671:
        r4 = "profile";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0687;
    L_0x067c:
        r4 = "article";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0acc;
    L_0x0687:
        r126 = 1;
    L_0x0689:
        if (r125 != 0) goto L_0x0ad0;
    L_0x068b:
        r0 = r146;
        r4 = r0.drawInstantView;
        if (r4 != 0) goto L_0x0ad0;
    L_0x0691:
        if (r66 != 0) goto L_0x0ad0;
    L_0x0693:
        if (r64 == 0) goto L_0x0ad0;
    L_0x0695:
        if (r135 == 0) goto L_0x0ad0;
    L_0x0697:
        r4 = "app";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x06b8;
    L_0x06a2:
        r4 = "profile";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x06b8;
    L_0x06ad:
        r4 = "article";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0ad0;
    L_0x06b8:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.photoThumbs;
        if (r4 == 0) goto L_0x0ad0;
    L_0x06c0:
        r4 = 1;
    L_0x06c1:
        r0 = r146;
        r0.isSmallImage = r4;
        r139 = r14;
    L_0x06c7:
        r0 = r146;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x0b59;
    L_0x06cd:
        r42 = 0;
    L_0x06cf:
        r117 = 3;
        r44 = 0;
        r86 = r86 - r42;
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.photoThumbs;
        if (r4 != 0) goto L_0x06e7;
    L_0x06dd:
        if (r107 == 0) goto L_0x06e7;
    L_0x06df:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r6 = 1;
        r4.generateThumbs(r6);
    L_0x06e7:
        if (r7 == 0) goto L_0x076c;
    L_0x06e9:
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0b64 }
        r4 = r4.measureText(r7);	 Catch:{ Exception -> 0x0b64 }
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0b64 }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0b64 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0b64 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0b64 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0b64 }
        r142 = r0;	 Catch:{ Exception -> 0x0b64 }
        r6 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0b64 }
        r8 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0b64 }
        r0 = r142;	 Catch:{ Exception -> 0x0b64 }
        r1 = r86;	 Catch:{ Exception -> 0x0b64 }
        r9 = java.lang.Math.min(r0, r1);	 Catch:{ Exception -> 0x0b64 }
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0b64 }
        r11 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0b64 }
        r12 = 0;	 Catch:{ Exception -> 0x0b64 }
        r13 = 0;	 Catch:{ Exception -> 0x0b64 }
        r6.<init>(r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0b64 }
        r0 = r146;	 Catch:{ Exception -> 0x0b64 }
        r0.siteNameLayout = r6;	 Catch:{ Exception -> 0x0b64 }
        r0 = r146;	 Catch:{ Exception -> 0x0b64 }
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0b64 }
        r6 = 0;	 Catch:{ Exception -> 0x0b64 }
        r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x0b64 }
        r6 = 0;	 Catch:{ Exception -> 0x0b64 }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ Exception -> 0x0b64 }
        if (r4 == 0) goto L_0x0b61;	 Catch:{ Exception -> 0x0b64 }
    L_0x0721:
        r4 = 1;	 Catch:{ Exception -> 0x0b64 }
    L_0x0722:
        r0 = r146;	 Catch:{ Exception -> 0x0b64 }
        r0.siteNameRtl = r4;	 Catch:{ Exception -> 0x0b64 }
        r0 = r146;	 Catch:{ Exception -> 0x0b64 }
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0b64 }
        r0 = r146;	 Catch:{ Exception -> 0x0b64 }
        r6 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0b64 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0b64 }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x0b64 }
        r78 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x0b64 }
        r0 = r146;	 Catch:{ Exception -> 0x0b64 }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0b64 }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x0b64 }
        r0 = r146;	 Catch:{ Exception -> 0x0b64 }
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0b64 }
        r0 = r146;	 Catch:{ Exception -> 0x0b64 }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0b64 }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x0b64 }
        r0 = r146;	 Catch:{ Exception -> 0x0b64 }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0b64 }
        r44 = r44 + r78;	 Catch:{ Exception -> 0x0b64 }
        r0 = r146;	 Catch:{ Exception -> 0x0b64 }
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x0b64 }
        r142 = r4.getWidth();	 Catch:{ Exception -> 0x0b64 }
        r0 = r142;	 Catch:{ Exception -> 0x0b64 }
        r1 = r146;	 Catch:{ Exception -> 0x0b64 }
        r1.siteNameWidth = r0;	 Catch:{ Exception -> 0x0b64 }
        r4 = r142 + r42;	 Catch:{ Exception -> 0x0b64 }
        r0 = r92;	 Catch:{ Exception -> 0x0b64 }
        r92 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0b64 }
        r4 = r142 + r42;	 Catch:{ Exception -> 0x0b64 }
        r0 = r95;	 Catch:{ Exception -> 0x0b64 }
        r95 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0b64 }
    L_0x076c:
        r134 = 0;
        if (r133 == 0) goto L_0x0ba8;
    L_0x0770:
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0 = r146;	 Catch:{ Exception -> 0x3b21 }
        r0.titleX = r4;	 Catch:{ Exception -> 0x3b21 }
        r0 = r146;	 Catch:{ Exception -> 0x3b21 }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x3b21 }
        if (r4 == 0) goto L_0x079b;	 Catch:{ Exception -> 0x3b21 }
    L_0x077d:
        r0 = r146;	 Catch:{ Exception -> 0x3b21 }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x3b21 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x3b21 }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x3b21 }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x3b21 }
        r0 = r146;	 Catch:{ Exception -> 0x3b21 }
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x3b21 }
        r0 = r146;	 Catch:{ Exception -> 0x3b21 }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x3b21 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x3b21 }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x3b21 }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x3b21 }
        r0 = r146;	 Catch:{ Exception -> 0x3b21 }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x3b21 }
    L_0x079b:
        r116 = 0;	 Catch:{ Exception -> 0x3b21 }
        r0 = r146;	 Catch:{ Exception -> 0x3b21 }
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x3b21 }
        if (r4 == 0) goto L_0x07a5;	 Catch:{ Exception -> 0x3b21 }
    L_0x07a3:
        if (r64 != 0) goto L_0x0b6a;	 Catch:{ Exception -> 0x3b21 }
    L_0x07a5:
        r9 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x3b21 }
        r11 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x3b21 }
        r12 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x3b21 }
        r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x3b21 }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x3b21 }
        r13 = (float) r4;	 Catch:{ Exception -> 0x3b21 }
        r14 = 0;	 Catch:{ Exception -> 0x3b21 }
        r15 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x3b21 }
        r17 = 4;	 Catch:{ Exception -> 0x3b21 }
        r8 = r133;	 Catch:{ Exception -> 0x3b21 }
        r10 = r86;	 Catch:{ Exception -> 0x3b21 }
        r16 = r86;	 Catch:{ Exception -> 0x3b21 }
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17);	 Catch:{ Exception -> 0x3b21 }
        r0 = r146;	 Catch:{ Exception -> 0x3b21 }
        r0.titleLayout = r4;	 Catch:{ Exception -> 0x3b21 }
        r12 = r117;
    L_0x07c7:
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0ba2 }
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r6 = r0.titleLayout;	 Catch:{ Exception -> 0x0ba2 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0ba2 }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x0ba2 }
        r78 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x0ba2 }
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x0ba2 }
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0ba2 }
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x0ba2 }
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0ba2 }
        r58 = 1;	 Catch:{ Exception -> 0x0ba2 }
        r41 = 0;	 Catch:{ Exception -> 0x0ba2 }
    L_0x07f1:
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0ba2 }
        r0 = r41;	 Catch:{ Exception -> 0x0ba2 }
        if (r0 >= r4) goto L_0x0d1c;	 Catch:{ Exception -> 0x0ba2 }
    L_0x07fd:
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0ba2 }
        r0 = r41;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x0ba2 }
        r0 = (int) r4;	 Catch:{ Exception -> 0x0ba2 }
        r85 = r0;	 Catch:{ Exception -> 0x0ba2 }
        if (r85 == 0) goto L_0x080e;	 Catch:{ Exception -> 0x0ba2 }
    L_0x080c:
        r134 = 1;	 Catch:{ Exception -> 0x0ba2 }
    L_0x080e:
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r0.titleX;	 Catch:{ Exception -> 0x0ba2 }
        r6 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;	 Catch:{ Exception -> 0x0ba2 }
        if (r4 != r6) goto L_0x0b91;	 Catch:{ Exception -> 0x0ba2 }
    L_0x0817:
        r0 = r85;	 Catch:{ Exception -> 0x0ba2 }
        r4 = -r0;	 Catch:{ Exception -> 0x0ba2 }
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r0.titleX = r4;	 Catch:{ Exception -> 0x0ba2 }
    L_0x081e:
        if (r85 == 0) goto L_0x0d08;	 Catch:{ Exception -> 0x0ba2 }
    L_0x0820:
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r4.getWidth();	 Catch:{ Exception -> 0x0ba2 }
        r142 = r4 - r85;	 Catch:{ Exception -> 0x0ba2 }
    L_0x082a:
        r0 = r41;	 Catch:{ Exception -> 0x0ba2 }
        r1 = r116;	 Catch:{ Exception -> 0x0ba2 }
        if (r0 < r1) goto L_0x0838;	 Catch:{ Exception -> 0x0ba2 }
    L_0x0830:
        if (r85 == 0) goto L_0x0840;	 Catch:{ Exception -> 0x0ba2 }
    L_0x0832:
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0ba2 }
        if (r4 == 0) goto L_0x0840;	 Catch:{ Exception -> 0x0ba2 }
    L_0x0838:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Exception -> 0x0ba2 }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0ba2 }
        r142 = r142 + r4;	 Catch:{ Exception -> 0x0ba2 }
    L_0x0840:
        r4 = r142 + r42;	 Catch:{ Exception -> 0x0ba2 }
        r0 = r92;	 Catch:{ Exception -> 0x0ba2 }
        r92 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0ba2 }
        r4 = r142 + r42;	 Catch:{ Exception -> 0x0ba2 }
        r0 = r95;	 Catch:{ Exception -> 0x0ba2 }
        r95 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0ba2 }
        r41 = r41 + 1;
        goto L_0x07f1;
    L_0x0853:
        r98 = 0;
        goto L_0x0031;
    L_0x0857:
        r97 = 0;
        goto L_0x0041;
    L_0x085b:
        r63 = 0;
        goto L_0x0078;
    L_0x085f:
        r74 = 0;
        goto L_0x0082;
    L_0x0863:
        r100 = 0;
        goto L_0x00a1;
    L_0x0867:
        r74 = 0;
        goto L_0x00ab;
    L_0x086b:
        r4 = 0;
        r0 = r146;
        r0.currentMessagesGroup = r4;
        r4 = 0;
        r0 = r146;
        r0.currentPosition = r4;
        goto L_0x0120;
    L_0x0877:
        r4 = 0;
        goto L_0x0137;
    L_0x087a:
        r4 = 0;
        goto L_0x0152;
    L_0x087d:
        r4 = 0;
        goto L_0x01b1;
    L_0x0880:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.to_id;
        r4 = r4.channel_id;
        if (r4 == 0) goto L_0x08a3;
    L_0x088a:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x08a3;
    L_0x0890:
        r4 = 1;
    L_0x0891:
        r0 = r146;
        r0.drawName = r4;
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r26 = r4 - r6;
        goto L_0x03cb;
    L_0x08a3:
        r4 = 0;
        goto L_0x0891;
    L_0x08a5:
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x08d2;
    L_0x08ab:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x08d2;
    L_0x08b1:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x08d2;
    L_0x08b7:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x42f40000 float:122.0 double:5.54977537E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r26 = r4 - r6;
        r4 = 1;
        r0 = r146;
        r0.drawName = r4;
        goto L_0x03cb;
    L_0x08d2:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r26 = r4 - r6;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.to_id;
        r4 = r4.channel_id;
        if (r4 == 0) goto L_0x08fd;
    L_0x08f0:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x08fd;
    L_0x08f6:
        r4 = 1;
    L_0x08f7:
        r0 = r146;
        r0.drawName = r4;
        goto L_0x03cb;
    L_0x08fd:
        r4 = 0;
        goto L_0x08f7;
    L_0x08ff:
        r4 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x03f1;
    L_0x0907:
        r4 = 0;
        goto L_0x0430;
    L_0x090a:
        r4 = 0;
        goto L_0x0457;
    L_0x090d:
        r4 = 0;
        goto L_0x046e;
    L_0x0910:
        r124 = 0;
        goto L_0x0486;
    L_0x0914:
        r141 = 0;
        goto L_0x0498;
    L_0x0918:
        r4 = "telegram_megagroup";
        r0 = r141;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x092f;
    L_0x0923:
        r4 = 1;
        r0 = r146;
        r0.drawInstantView = r4;
        r4 = 2;
        r0 = r146;
        r0.drawInstantViewType = r4;
        goto L_0x04b3;
    L_0x092f:
        r4 = "telegram_message";
        r0 = r141;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x04b3;
    L_0x093a:
        r4 = 1;
        r0 = r146;
        r0.drawInstantView = r4;
        r4 = 3;
        r0 = r146;
        r0.drawInstantViewType = r4;
        goto L_0x04b3;
    L_0x0946:
        if (r124 == 0) goto L_0x04b3;
    L_0x0948:
        r124 = r124.toLowerCase();
        r4 = "instagram";
        r0 = r124;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x096d;
    L_0x0957:
        r4 = "twitter";
        r0 = r124;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x096d;
    L_0x0962:
        r4 = "telegram_album";
        r0 = r141;
        r4 = r4.equals(r0);
        if (r4 == 0) goto L_0x04b3;
    L_0x096d:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.cached_page;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_pageFull;
        if (r4 == 0) goto L_0x04b3;
    L_0x097b:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.photo;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r4 != 0) goto L_0x0999;
    L_0x0989:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.document;
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r4);
        if (r4 == 0) goto L_0x04b3;
    L_0x0999:
        r4 = 0;
        r0 = r146;
        r0.drawInstantView = r4;
        r125 = 1;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        r4 = r4.cached_page;
        r0 = r4.blocks;
        r52 = r0;
        r59 = 1;
        r41 = 0;
    L_0x09b2:
        r4 = r52.size();
        r0 = r41;
        if (r0 >= r4) goto L_0x09ec;
    L_0x09ba:
        r0 = r52;
        r1 = r41;
        r51 = r0.get(r1);
        r51 = (org.telegram.tgnet.TLRPC.PageBlock) r51;
        r0 = r51;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
        if (r4 == 0) goto L_0x09d9;
    L_0x09ca:
        r50 = r51;
        r50 = (org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow) r50;
        r0 = r50;
        r4 = r0.items;
        r59 = r4.size();
    L_0x09d6:
        r41 = r41 + 1;
        goto L_0x09b2;
    L_0x09d9:
        r0 = r51;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
        if (r4 == 0) goto L_0x09d6;
    L_0x09df:
        r50 = r51;
        r50 = (org.telegram.tgnet.TLRPC.TL_pageBlockCollage) r50;
        r0 = r50;
        r4 = r0.items;
        r59 = r4.size();
        goto L_0x09d6;
    L_0x09ec:
        r4 = "Of";
        r6 = NUM; // 0x7f0c04b9 float:1.8611644E38 double:1.053097996E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = 1;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r59);
        r8[r9] = r10;
        r5 = org.telegram.messenger.LocaleController.formatString(r4, r6, r8);
        r4 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r146;
        r0.photosCountWidth = r4;
        r4 = new android.text.StaticLayout;
        r6 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r0 = r146;
        r7 = r0.photosCountWidth;
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r9 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r10 = 0;
        r11 = 0;
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);
        r0 = r146;
        r0.photosCountLayout = r4;
        goto L_0x04b3;
    L_0x0a2f:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r0 = r147;
        r6 = r0.lastLineWidth;
        r65 = r4 - r6;
        if (r65 < 0) goto L_0x0a56;
    L_0x0a3b:
        r0 = r65;
        r1 = r131;
        if (r0 > r1) goto L_0x0a56;
    L_0x0a41:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r4 = r4 + r131;
        r4 = r4 - r65;
        r6 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.backgroundWidth = r4;
        goto L_0x0503;
    L_0x0a56:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r0 = r147;
        r6 = r0.lastLineWidth;
        r6 = r6 + r131;
        r4 = java.lang.Math.max(r4, r6);
        r6 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.backgroundWidth = r4;
        goto L_0x0503;
    L_0x0a71:
        r4 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x0532;
    L_0x0a79:
        r4 = 0;
        goto L_0x0556;
    L_0x0a7c:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r86 = r4 - r6;
        goto L_0x05ea;
    L_0x0a8a:
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x0ab6;
    L_0x0a90:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x0ab6;
    L_0x0a96:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 != 0) goto L_0x0ab6;
    L_0x0aa0:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r86 = r4 - r6;
        goto L_0x05ea;
    L_0x0ab6:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r86 = r4 - r6;
        goto L_0x05ea;
    L_0x0acc:
        r126 = 0;
        goto L_0x0689;
    L_0x0ad0:
        r4 = 0;
        goto L_0x06c1;
    L_0x0ad3:
        r0 = r146;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x0b17;
    L_0x0ad9:
        r0 = r147;
        r4 = r0.messageOwner;
        r0 = r4.media;
        r80 = r0;
        r80 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r80;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r7 = r4.title;
        r133 = 0;
        r64 = 0;
        r107 = 0;
        r47 = 0;
        r66 = 0;
        r0 = r80;
        r4 = r0.photo;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r4 == 0) goto L_0x0b15;
    L_0x0afd:
        r0 = r80;
        r4 = r0.photo;
        r14 = org.telegram.messenger.WebFile.createWithWebDocument(r4);
    L_0x0b05:
        r67 = 0;
        r135 = "invoice";
        r4 = 0;
        r0 = r146;
        r0.isSmallImage = r4;
        r126 = 0;
        r139 = r14;
        goto L_0x06c7;
    L_0x0b15:
        r14 = 0;
        goto L_0x0b05;
    L_0x0b17:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.game;
        r73 = r0;
        r0 = r73;
        r7 = r0.title;
        r133 = 0;
        r14 = 0;
        r0 = r147;
        r4 = r0.messageText;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x0b56;
    L_0x0b32:
        r0 = r73;
        r0 = r0.description;
        r64 = r0;
    L_0x0b38:
        r0 = r73;
        r0 = r0.photo;
        r107 = r0;
        r47 = 0;
        r0 = r73;
        r0 = r0.document;
        r66 = r0;
        r67 = 0;
        r135 = "game";
        r4 = 0;
        r0 = r146;
        r0.isSmallImage = r4;
        r126 = 0;
        r139 = r14;
        goto L_0x06c7;
    L_0x0b56:
        r64 = 0;
        goto L_0x0b38;
    L_0x0b59:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r42 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x06cf;
    L_0x0b61:
        r4 = 0;
        goto L_0x0722;
    L_0x0b64:
        r69 = move-exception;
        org.telegram.messenger.FileLog.m3e(r69);
        goto L_0x076c;
    L_0x0b6a:
        r116 = r117;
        r9 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x3b21 }
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Exception -> 0x3b21 }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x3b21 }
        r11 = r86 - r4;	 Catch:{ Exception -> 0x3b21 }
        r13 = 4;	 Catch:{ Exception -> 0x3b21 }
        r8 = r133;	 Catch:{ Exception -> 0x3b21 }
        r10 = r86;	 Catch:{ Exception -> 0x3b21 }
        r12 = r117;	 Catch:{ Exception -> 0x3b21 }
        r4 = generateStaticLayout(r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x3b21 }
        r0 = r146;	 Catch:{ Exception -> 0x3b21 }
        r0.titleLayout = r4;	 Catch:{ Exception -> 0x3b21 }
        r0 = r146;	 Catch:{ Exception -> 0x3b21 }
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x3b21 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x3b21 }
        r12 = r117 - r4;
        goto L_0x07c7;
    L_0x0b91:
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r0.titleX;	 Catch:{ Exception -> 0x0ba2 }
        r0 = r85;	 Catch:{ Exception -> 0x0ba2 }
        r6 = -r0;	 Catch:{ Exception -> 0x0ba2 }
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x0ba2 }
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r0.titleX = r4;	 Catch:{ Exception -> 0x0ba2 }
        goto L_0x081e;
    L_0x0ba2:
        r69 = move-exception;
    L_0x0ba3:
        org.telegram.messenger.FileLog.m3e(r69);
        r117 = r12;
    L_0x0ba8:
        r48 = 0;
        if (r47 == 0) goto L_0x3b32;
    L_0x0bac:
        if (r133 != 0) goto L_0x3b32;
    L_0x0bae:
        r0 = r146;	 Catch:{ Exception -> 0x0d58 }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0d58 }
        if (r4 == 0) goto L_0x0bd2;	 Catch:{ Exception -> 0x0d58 }
    L_0x0bb4:
        r0 = r146;	 Catch:{ Exception -> 0x0d58 }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0d58 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0d58 }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0d58 }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0d58 }
        r0 = r146;	 Catch:{ Exception -> 0x0d58 }
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0d58 }
        r0 = r146;	 Catch:{ Exception -> 0x0d58 }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0d58 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0d58 }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0d58 }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0d58 }
        r0 = r146;	 Catch:{ Exception -> 0x0d58 }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0d58 }
    L_0x0bd2:
        r4 = 3;	 Catch:{ Exception -> 0x0d58 }
        r0 = r117;	 Catch:{ Exception -> 0x0d58 }
        if (r0 != r4) goto L_0x0d20;	 Catch:{ Exception -> 0x0d58 }
    L_0x0bd7:
        r0 = r146;	 Catch:{ Exception -> 0x0d58 }
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0d58 }
        if (r4 == 0) goto L_0x0bdf;	 Catch:{ Exception -> 0x0d58 }
    L_0x0bdd:
        if (r64 != 0) goto L_0x0d20;	 Catch:{ Exception -> 0x0d58 }
    L_0x0bdf:
        r8 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0d58 }
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0d58 }
        r12 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0d58 }
        r13 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0d58 }
        r14 = 0;	 Catch:{ Exception -> 0x0d58 }
        r15 = 0;	 Catch:{ Exception -> 0x0d58 }
        r9 = r47;	 Catch:{ Exception -> 0x0d58 }
        r11 = r86;	 Catch:{ Exception -> 0x0d58 }
        r8.<init>(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ Exception -> 0x0d58 }
        r0 = r146;	 Catch:{ Exception -> 0x0d58 }
        r0.authorLayout = r8;	 Catch:{ Exception -> 0x0d58 }
        r12 = r117;
    L_0x0bf6:
        r0 = r146;	 Catch:{ Exception -> 0x3b1e }
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x3b1e }
        r0 = r146;	 Catch:{ Exception -> 0x3b1e }
        r6 = r0.authorLayout;	 Catch:{ Exception -> 0x3b1e }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x3b1e }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x3b1e }
        r78 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x3b1e }
        r0 = r146;	 Catch:{ Exception -> 0x3b1e }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x3b1e }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x3b1e }
        r0 = r146;	 Catch:{ Exception -> 0x3b1e }
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x3b1e }
        r0 = r146;	 Catch:{ Exception -> 0x3b1e }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x3b1e }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x3b1e }
        r0 = r146;	 Catch:{ Exception -> 0x3b1e }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x3b1e }
        r0 = r146;	 Catch:{ Exception -> 0x3b1e }
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x3b1e }
        r6 = 0;	 Catch:{ Exception -> 0x3b1e }
        r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x3b1e }
        r0 = (int) r4;	 Catch:{ Exception -> 0x3b1e }
        r85 = r0;	 Catch:{ Exception -> 0x3b1e }
        r0 = r85;	 Catch:{ Exception -> 0x3b1e }
        r4 = -r0;	 Catch:{ Exception -> 0x3b1e }
        r0 = r146;	 Catch:{ Exception -> 0x3b1e }
        r0.authorX = r4;	 Catch:{ Exception -> 0x3b1e }
        if (r85 == 0) goto L_0x0d45;	 Catch:{ Exception -> 0x3b1e }
    L_0x0c31:
        r0 = r146;	 Catch:{ Exception -> 0x3b1e }
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x3b1e }
        r4 = r4.getWidth();	 Catch:{ Exception -> 0x3b1e }
        r142 = r4 - r85;	 Catch:{ Exception -> 0x3b1e }
        r48 = 1;	 Catch:{ Exception -> 0x3b1e }
    L_0x0c3d:
        r4 = r142 + r42;	 Catch:{ Exception -> 0x3b1e }
        r0 = r92;	 Catch:{ Exception -> 0x3b1e }
        r92 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x3b1e }
        r4 = r142 + r42;	 Catch:{ Exception -> 0x3b1e }
        r0 = r95;	 Catch:{ Exception -> 0x3b1e }
        r95 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x3b1e }
    L_0x0c4d:
        if (r64 == 0) goto L_0x0d81;
    L_0x0c4f:
        r4 = 0;
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.currentMessageObject;	 Catch:{ Exception -> 0x0d7d }
        r4.generateLinkDescription();	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0d7d }
        if (r4 == 0) goto L_0x0c7f;	 Catch:{ Exception -> 0x0d7d }
    L_0x0c61:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0d7d }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0d7d }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0d7d }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0d7d }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0d7d }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0d7d }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0d7d }
    L_0x0c7f:
        r116 = 0;	 Catch:{ Exception -> 0x0d7d }
        r4 = 3;	 Catch:{ Exception -> 0x0d7d }
        if (r12 != r4) goto L_0x0d60;	 Catch:{ Exception -> 0x0d7d }
    L_0x0c84:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0d7d }
        if (r4 != 0) goto L_0x0d60;	 Catch:{ Exception -> 0x0d7d }
    L_0x0c8a:
        r0 = r147;	 Catch:{ Exception -> 0x0d7d }
        r8 = r0.linkDescription;	 Catch:{ Exception -> 0x0d7d }
        r9 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0d7d }
        r11 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0d7d }
        r12 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0d7d }
        r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0d7d }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0d7d }
        r13 = (float) r4;	 Catch:{ Exception -> 0x0d7d }
        r14 = 0;	 Catch:{ Exception -> 0x0d7d }
        r15 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0d7d }
        r17 = 6;	 Catch:{ Exception -> 0x0d7d }
        r10 = r86;	 Catch:{ Exception -> 0x0d7d }
        r16 = r86;	 Catch:{ Exception -> 0x0d7d }
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17);	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x0d7d }
    L_0x0cac:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r6 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0d7d }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0d7d }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x0d7d }
        r78 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0d7d }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0d7d }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x0d7d }
        r77 = 0;	 Catch:{ Exception -> 0x0d7d }
        r41 = 0;	 Catch:{ Exception -> 0x0d7d }
    L_0x0cd6:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0d7d }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0d7d }
        r0 = r41;	 Catch:{ Exception -> 0x0d7d }
        if (r0 >= r4) goto L_0x133c;	 Catch:{ Exception -> 0x0d7d }
    L_0x0ce2:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0d7d }
        r0 = r41;	 Catch:{ Exception -> 0x0d7d }
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x0d7d }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0d7d }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0d7d }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0d7d }
        r85 = r0;	 Catch:{ Exception -> 0x0d7d }
        if (r85 == 0) goto L_0x0d05;	 Catch:{ Exception -> 0x0d7d }
    L_0x0cf6:
        r77 = 1;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x0d7d }
        if (r4 != 0) goto L_0x132b;	 Catch:{ Exception -> 0x0d7d }
    L_0x0cfe:
        r0 = r85;	 Catch:{ Exception -> 0x0d7d }
        r4 = -r0;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0d7d }
    L_0x0d05:
        r41 = r41 + 1;
        goto L_0x0cd6;
    L_0x0d08:
        r0 = r146;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x0ba2 }
        r0 = r41;	 Catch:{ Exception -> 0x0ba2 }
        r4 = r4.getLineWidth(r0);	 Catch:{ Exception -> 0x0ba2 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0ba2 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0ba2 }
        r0 = (int) r8;
        r142 = r0;
        goto L_0x082a;
    L_0x0d1c:
        r117 = r12;
        goto L_0x0ba8;
    L_0x0d20:
        r9 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0d58 }
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Exception -> 0x0d58 }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0d58 }
        r11 = r86 - r4;	 Catch:{ Exception -> 0x0d58 }
        r13 = 1;	 Catch:{ Exception -> 0x0d58 }
        r8 = r47;	 Catch:{ Exception -> 0x0d58 }
        r10 = r86;	 Catch:{ Exception -> 0x0d58 }
        r12 = r117;	 Catch:{ Exception -> 0x0d58 }
        r4 = generateStaticLayout(r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0d58 }
        r0 = r146;	 Catch:{ Exception -> 0x0d58 }
        r0.authorLayout = r4;	 Catch:{ Exception -> 0x0d58 }
        r0 = r146;	 Catch:{ Exception -> 0x0d58 }
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x0d58 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0d58 }
        r12 = r117 - r4;
        goto L_0x0bf6;
    L_0x0d45:
        r0 = r146;	 Catch:{ Exception -> 0x3b1e }
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x3b1e }
        r6 = 0;	 Catch:{ Exception -> 0x3b1e }
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x3b1e }
        r8 = (double) r4;	 Catch:{ Exception -> 0x3b1e }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x3b1e }
        r0 = (int) r8;
        r142 = r0;
        goto L_0x0c3d;
    L_0x0d58:
        r69 = move-exception;
        r12 = r117;
    L_0x0d5b:
        org.telegram.messenger.FileLog.m3e(r69);
        goto L_0x0c4d;
    L_0x0d60:
        r116 = r12;
        r0 = r147;	 Catch:{ Exception -> 0x0d7d }
        r8 = r0.linkDescription;	 Catch:{ Exception -> 0x0d7d }
        r9 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0d7d }
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Exception -> 0x0d7d }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0d7d }
        r11 = r86 - r4;	 Catch:{ Exception -> 0x0d7d }
        r13 = 6;	 Catch:{ Exception -> 0x0d7d }
        r10 = r86;	 Catch:{ Exception -> 0x0d7d }
        r4 = generateStaticLayout(r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x0d7d }
        goto L_0x0cac;
    L_0x0d7d:
        r69 = move-exception;
        org.telegram.messenger.FileLog.m3e(r69);
    L_0x0d81:
        if (r126 == 0) goto L_0x0da1;
    L_0x0d83:
        r0 = r146;
        r4 = r0.descriptionLayout;
        if (r4 == 0) goto L_0x0d9a;
    L_0x0d89:
        r0 = r146;
        r4 = r0.descriptionLayout;
        if (r4 == 0) goto L_0x0da1;
    L_0x0d8f:
        r0 = r146;
        r4 = r0.descriptionLayout;
        r4 = r4.getLineCount();
        r6 = 1;
        if (r4 != r6) goto L_0x0da1;
    L_0x0d9a:
        r126 = 0;
        r4 = 0;
        r0 = r146;
        r0.isSmallImage = r4;
    L_0x0da1:
        if (r126 == 0) goto L_0x13da;
    L_0x0da3:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r94 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0da9:
        if (r66 == 0) goto L_0x1834;
    L_0x0dab:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r66);
        if (r4 == 0) goto L_0x13de;
    L_0x0db1:
        r0 = r66;
        r4 = r0.thumb;
        r0 = r146;
        r0.currentPhotoObject = r4;
        r0 = r66;
        r1 = r146;
        r1.documentAttach = r0;
        r4 = 7;
        r0 = r146;
        r0.documentAttachType = r4;
        r14 = r139;
    L_0x0dc6:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 5;
        if (r4 == r6) goto L_0x10d1;
    L_0x0dcd:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 3;
        if (r4 == r6) goto L_0x10d1;
    L_0x0dd4:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 1;
        if (r4 == r6) goto L_0x10d1;
    L_0x0ddb:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x0de3;
    L_0x0de1:
        if (r14 == 0) goto L_0x1bd7;
    L_0x0de3:
        if (r135 == 0) goto L_0x18a4;
    L_0x0de5:
        r4 = "photo";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0e14;
    L_0x0df0:
        r4 = "document";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0e02;
    L_0x0dfb:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 6;
        if (r4 != r6) goto L_0x0e14;
    L_0x0e02:
        r4 = "gif";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0e14;
    L_0x0e0d:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 4;
        if (r4 != r6) goto L_0x18a4;
    L_0x0e14:
        r4 = 1;
    L_0x0e15:
        r0 = r146;
        r0.drawImageButton = r4;
        r0 = r146;
        r4 = r0.linkPreviewHeight;
        if (r4 == 0) goto L_0x0e3d;
    L_0x0e1f:
        r0 = r146;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.linkPreviewHeight = r4;
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
    L_0x0e3d:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 6;
        if (r4 != r6) goto L_0x18b4;
    L_0x0e44:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x18a7;
    L_0x0e4a:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r94 = r0;
    L_0x0e55:
        r0 = r146;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x18c7;
    L_0x0e5b:
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0e61:
        r4 = r94 - r4;
        r4 = r4 + r42;
        r0 = r92;
        r92 = java.lang.Math.max(r0, r4);
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x18ca;
    L_0x0e71:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r6 = -1;
        r4.size = r6;
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x0e85;
    L_0x0e7e:
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        r6 = -1;
        r4.size = r6;
    L_0x0e85:
        if (r126 != 0) goto L_0x0e8e;
    L_0x0e87:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 7;
        if (r4 != r6) goto L_0x18cf;
    L_0x0e8e:
        r78 = r94;
        r142 = r94;
    L_0x0e92:
        r0 = r146;
        r4 = r0.isSmallImage;
        if (r4 == 0) goto L_0x1968;
    L_0x0e98:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r44;
        r0 = r146;
        r6 = r0.linkPreviewHeight;
        if (r4 <= r6) goto L_0x0ecf;
    L_0x0ea6:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r44;
        r0 = r146;
        r8 = r0.linkPreviewHeight;
        r6 = r6 - r8;
        r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 + r8;
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r44;
        r0 = r146;
        r0.linkPreviewHeight = r4;
    L_0x0ecf:
        r0 = r146;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.linkPreviewHeight = r4;
    L_0x0ede:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 0;
        r8 = 0;
        r0 = r142;
        r1 = r78;
        r4.setImageCoords(r6, r8, r0, r1);
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r142);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r78);
        r8[r9] = r10;
        r4 = java.lang.String.format(r4, r6, r8);
        r0 = r146;
        r0.currentPhotoFilter = r4;
        r4 = java.util.Locale.US;
        r6 = "%d_%d_b";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r142);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r78);
        r8[r9] = r10;
        r4 = java.lang.String.format(r4, r6, r8);
        r0 = r146;
        r0.currentPhotoFilterThumb = r4;
        if (r14 == 0) goto L_0x1985;
    L_0x0f29:
        r0 = r146;
        r13 = r0.photoImage;
        r15 = 0;
        r0 = r146;
        r0 = r0.currentPhotoFilter;
        r16 = r0;
        r17 = 0;
        r18 = 0;
        r19 = "b1";
        r0 = r14.size;
        r20 = r0;
        r21 = 0;
        r22 = 1;
        r13.setImage(r14, r15, r16, r17, r18, r19, r20, r21, r22);
    L_0x0f46:
        r4 = 1;
        r0 = r146;
        r0.drawPhotoImage = r4;
        if (r135 == 0) goto L_0x1b96;
    L_0x0f4d:
        r4 = "video";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x1b96;
    L_0x0f58:
        if (r67 == 0) goto L_0x1b96;
    L_0x0f5a:
        r99 = r67 / 60;
        r4 = r99 * 60;
        r123 = r67 - r4;
        r4 = "%d:%02d";
        r6 = 2;
        r6 = new java.lang.Object[r6];
        r8 = 0;
        r9 = java.lang.Integer.valueOf(r99);
        r6[r8] = r9;
        r8 = 1;
        r9 = java.lang.Integer.valueOf(r123);
        r6[r8] = r9;
        r5 = java.lang.String.format(r4, r6);
        r4 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r146;
        r0.durationWidth = r4;
        r15 = new android.text.StaticLayout;
        r17 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r0 = r146;
        r0 = r0.durationWidth;
        r18 = r0;
        r19 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r20 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r21 = 0;
        r22 = 0;
        r16 = r5;
        r15.<init>(r16, r17, r18, r19, r20, r21, r22);
        r0 = r146;
        r0.videoInfoLayout = r15;
    L_0x0fa3:
        r0 = r146;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x1097;
    L_0x0fa9:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x1c01;
    L_0x0fb5:
        r4 = "PaymentReceipt";
        r6 = NUM; // 0x7f0c0579 float:1.8612034E38 double:1.0530980906E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
    L_0x0fc3:
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r0 = r147;
        r6 = r0.messageOwner;
        r6 = r6.media;
        r8 = r6.total_amount;
        r0 = r147;
        r6 = r0.messageOwner;
        r6 = r6.media;
        r6 = r6.currency;
        r113 = r4.formatCurrencyString(r8, r6);
        r16 = new android.text.SpannableStringBuilder;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r113;
        r4 = r4.append(r0);
        r6 = " ";
        r4 = r4.append(r6);
        r4 = r4.append(r5);
        r4 = r4.toString();
        r0 = r16;
        r0.<init>(r4);
        r4 = new org.telegram.ui.Components.TypefaceSpan;
        r6 = "fonts/rmedium.ttf";
        r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r6);
        r4.<init>(r6);
        r6 = 0;
        r8 = r113.length();
        r9 = 33;
        r0 = r16;
        r0.setSpan(r4, r6, r8, r9);
        r4 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
        r6 = 0;
        r8 = r16.length();
        r0 = r16;
        r4 = r4.measureText(r0, r6, r8);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r146;
        r0.durationWidth = r4;
        r15 = new android.text.StaticLayout;
        r17 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
        r0 = r146;
        r4 = r0.durationWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r18 = r4 + r6;
        r19 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r20 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r21 = 0;
        r22 = 0;
        r15.<init>(r16, r17, r18, r19, r20, r21, r22);
        r0 = r146;
        r0.videoInfoLayout = r15;
        r0 = r146;
        r4 = r0.drawPhotoImage;
        if (r4 != 0) goto L_0x1097;
    L_0x1050:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r6 = r0.timeWidth;
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x1c2b;
    L_0x1069:
        r4 = 20;
    L_0x106b:
        r4 = r4 + 14;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r132 = r6 + r4;
        r0 = r146;
        r4 = r0.durationWidth;
        r4 = r4 + r132;
        r0 = r26;
        if (r4 <= r0) goto L_0x1c2e;
    L_0x107e:
        r0 = r146;
        r4 = r0.durationWidth;
        r0 = r92;
        r92 = java.lang.Math.max(r4, r0);
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
    L_0x1097:
        r0 = r146;
        r4 = r0.hasGamePreview;
        if (r4 == 0) goto L_0x10c6;
    L_0x109d:
        r0 = r147;
        r4 = r0.textHeight;
        if (r4 == 0) goto L_0x10c6;
    L_0x10a3:
        r0 = r146;
        r4 = r0.linkPreviewHeight;
        r0 = r147;
        r6 = r0.textHeight;
        r8 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 + r8;
        r4 = r4 + r6;
        r0 = r146;
        r0.linkPreviewHeight = r4;
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
    L_0x10c6:
        r0 = r146;
        r1 = r26;
        r2 = r131;
        r3 = r92;
        r0.calcBackgroundWidth(r1, r2, r3);
    L_0x10d1:
        r146.createInstantViewButton();
    L_0x10d4:
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 != 0) goto L_0x3744;
    L_0x10da:
        r0 = r146;
        r4 = r0.captionLayout;
        if (r4 != 0) goto L_0x3744;
    L_0x10e0:
        r0 = r147;
        r4 = r0.caption;
        if (r4 == 0) goto L_0x3744;
    L_0x10e6:
        r0 = r147;
        r4 = r0.type;
        r6 = 13;
        if (r4 == r6) goto L_0x3744;
    L_0x10ee:
        r0 = r147;	 Catch:{ Exception -> 0x373b }
        r4 = r0.caption;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r0.currentCaption = r4;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r4 = r0.backgroundWidth;	 Catch:{ Exception -> 0x373b }
        r6 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;	 Catch:{ Exception -> 0x373b }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x373b }
        r142 = r4 - r6;	 Catch:{ Exception -> 0x373b }
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;	 Catch:{ Exception -> 0x373b }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x373b }
        r30 = r142 - r4;	 Catch:{ Exception -> 0x373b }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x373b }
        r6 = 24;	 Catch:{ Exception -> 0x373b }
        if (r4 < r6) goto L_0x371e;	 Catch:{ Exception -> 0x373b }
    L_0x1110:
        r0 = r147;	 Catch:{ Exception -> 0x373b }
        r4 = r0.caption;	 Catch:{ Exception -> 0x373b }
        r6 = 0;	 Catch:{ Exception -> 0x373b }
        r0 = r147;	 Catch:{ Exception -> 0x373b }
        r8 = r0.caption;	 Catch:{ Exception -> 0x373b }
        r8 = r8.length();	 Catch:{ Exception -> 0x373b }
        r9 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x373b }
        r0 = r30;	 Catch:{ Exception -> 0x373b }
        r4 = android.text.StaticLayout.Builder.obtain(r4, r6, r8, r9, r0);	 Catch:{ Exception -> 0x373b }
        r6 = 1;	 Catch:{ Exception -> 0x373b }
        r4 = r4.setBreakStrategy(r6);	 Catch:{ Exception -> 0x373b }
        r6 = 0;	 Catch:{ Exception -> 0x373b }
        r4 = r4.setHyphenationFrequency(r6);	 Catch:{ Exception -> 0x373b }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x373b }
        r4 = r4.setAlignment(r6);	 Catch:{ Exception -> 0x373b }
        r4 = r4.build();	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x373b }
    L_0x113d:
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x373b }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x373b }
        if (r4 <= 0) goto L_0x11d7;	 Catch:{ Exception -> 0x373b }
    L_0x1147:
        r0 = r30;	 Catch:{ Exception -> 0x373b }
        r1 = r146;	 Catch:{ Exception -> 0x373b }
        r1.captionWidth = r0;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r6 = r0.timeWidth;	 Catch:{ Exception -> 0x373b }
        r4 = r147.isOutOwner();	 Catch:{ Exception -> 0x373b }
        if (r4 == 0) goto L_0x3741;	 Catch:{ Exception -> 0x373b }
    L_0x1157:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;	 Catch:{ Exception -> 0x373b }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x373b }
    L_0x115d:
        r132 = r6 + r4;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x373b }
        r4 = r4.getHeight();	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r0.captionHeight = r4;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r6 = r0.captionHeight;	 Catch:{ Exception -> 0x373b }
        r8 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;	 Catch:{ Exception -> 0x373b }
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x373b }
        r6 = r6 + r8;	 Catch:{ Exception -> 0x373b }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x373b }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x373b }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x373b }
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r8 = r0.captionLayout;	 Catch:{ Exception -> 0x373b }
        r8 = r8.getLineCount();	 Catch:{ Exception -> 0x373b }
        r8 = r8 + -1;	 Catch:{ Exception -> 0x373b }
        r6 = r6.getLineLeft(r8);	 Catch:{ Exception -> 0x373b }
        r83 = r4 + r6;	 Catch:{ Exception -> 0x373b }
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;	 Catch:{ Exception -> 0x373b }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x373b }
        r4 = r142 - r4;	 Catch:{ Exception -> 0x373b }
        r4 = (float) r4;	 Catch:{ Exception -> 0x373b }
        r4 = r4 - r83;	 Catch:{ Exception -> 0x373b }
        r0 = r132;	 Catch:{ Exception -> 0x373b }
        r6 = (float) r0;	 Catch:{ Exception -> 0x373b }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ Exception -> 0x373b }
        if (r4 >= 0) goto L_0x11d7;	 Catch:{ Exception -> 0x373b }
    L_0x11b7:
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x373b }
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;	 Catch:{ Exception -> 0x373b }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x373b }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r4 = r0.captionHeight;	 Catch:{ Exception -> 0x373b }
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;	 Catch:{ Exception -> 0x373b }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x373b }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x373b }
        r0 = r146;	 Catch:{ Exception -> 0x373b }
        r0.captionHeight = r4;	 Catch:{ Exception -> 0x373b }
        r57 = 2;
    L_0x11d7:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r8 = r4.eventId;
        r10 = 0;
        r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r4 == 0) goto L_0x37bd;
    L_0x11e3:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.isMediaEmpty();
        if (r4 != 0) goto L_0x37bd;
    L_0x11ed:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.media;
        r4 = r4.webpage;
        if (r4 == 0) goto L_0x37bd;
    L_0x11f9:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42240000 float:41.0 double:5.48242687E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r86 = r4 - r6;
        r4 = 1;
        r0 = r146;
        r0.hasOldCaptionPreview = r4;
        r4 = 0;
        r0 = r146;
        r0.linkPreviewHeight = r4;
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.media;
        r0 = r4.webpage;
        r140 = r0;
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x376e }
        r0 = r140;	 Catch:{ Exception -> 0x376e }
        r6 = r0.site_name;	 Catch:{ Exception -> 0x376e }
        r4 = r4.measureText(r6);	 Catch:{ Exception -> 0x376e }
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x376e }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x376e }
        r8 = (double) r4;	 Catch:{ Exception -> 0x376e }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x376e }
        r0 = (int) r8;	 Catch:{ Exception -> 0x376e }
        r142 = r0;	 Catch:{ Exception -> 0x376e }
        r0 = r142;	 Catch:{ Exception -> 0x376e }
        r1 = r146;	 Catch:{ Exception -> 0x376e }
        r1.siteNameWidth = r0;	 Catch:{ Exception -> 0x376e }
        r31 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x376e }
        r0 = r140;	 Catch:{ Exception -> 0x376e }
        r0 = r0.site_name;	 Catch:{ Exception -> 0x376e }
        r32 = r0;	 Catch:{ Exception -> 0x376e }
        r33 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x376e }
        r0 = r142;	 Catch:{ Exception -> 0x376e }
        r1 = r86;	 Catch:{ Exception -> 0x376e }
        r34 = java.lang.Math.min(r0, r1);	 Catch:{ Exception -> 0x376e }
        r35 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x376e }
        r36 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x376e }
        r37 = 0;	 Catch:{ Exception -> 0x376e }
        r38 = 0;	 Catch:{ Exception -> 0x376e }
        r31.<init>(r32, r33, r34, r35, r36, r37, r38);	 Catch:{ Exception -> 0x376e }
        r0 = r31;	 Catch:{ Exception -> 0x376e }
        r1 = r146;	 Catch:{ Exception -> 0x376e }
        r1.siteNameLayout = r0;	 Catch:{ Exception -> 0x376e }
        r0 = r146;	 Catch:{ Exception -> 0x376e }
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x376e }
        r6 = 0;	 Catch:{ Exception -> 0x376e }
        r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x376e }
        r6 = 0;	 Catch:{ Exception -> 0x376e }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ Exception -> 0x376e }
        if (r4 == 0) goto L_0x376b;	 Catch:{ Exception -> 0x376e }
    L_0x1267:
        r4 = 1;	 Catch:{ Exception -> 0x376e }
    L_0x1268:
        r0 = r146;	 Catch:{ Exception -> 0x376e }
        r0.siteNameRtl = r4;	 Catch:{ Exception -> 0x376e }
        r0 = r146;	 Catch:{ Exception -> 0x376e }
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x376e }
        r0 = r146;	 Catch:{ Exception -> 0x376e }
        r6 = r0.siteNameLayout;	 Catch:{ Exception -> 0x376e }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x376e }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x376e }
        r78 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x376e }
        r0 = r146;	 Catch:{ Exception -> 0x376e }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x376e }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x376e }
        r0 = r146;	 Catch:{ Exception -> 0x376e }
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x376e }
        r0 = r146;	 Catch:{ Exception -> 0x376e }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x376e }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x376e }
        r0 = r146;	 Catch:{ Exception -> 0x376e }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x376e }
    L_0x1292:
        r4 = 0;
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x3785 }
        if (r4 == 0) goto L_0x12ac;	 Catch:{ Exception -> 0x3785 }
    L_0x129d:
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x3785 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x3785 }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x3785 }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x3785 }
    L_0x12ac:
        r0 = r140;	 Catch:{ Exception -> 0x3785 }
        r0 = r0.description;	 Catch:{ Exception -> 0x3785 }
        r31 = r0;	 Catch:{ Exception -> 0x3785 }
        r32 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x3785 }
        r34 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x3785 }
        r35 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x3785 }
        r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x3785 }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x3785 }
        r0 = (float) r4;	 Catch:{ Exception -> 0x3785 }
        r36 = r0;	 Catch:{ Exception -> 0x3785 }
        r37 = 0;	 Catch:{ Exception -> 0x3785 }
        r38 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x3785 }
        r40 = 6;	 Catch:{ Exception -> 0x3785 }
        r33 = r86;	 Catch:{ Exception -> 0x3785 }
        r39 = r86;	 Catch:{ Exception -> 0x3785 }
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r31, r32, r33, r34, r35, r36, r37, r38, r39, r40);	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r6 = r0.descriptionLayout;	 Catch:{ Exception -> 0x3785 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x3785 }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x3785 }
        r78 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x3785 }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x3785 }
        r4 = r4 + r78;	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x3785 }
        r41 = 0;	 Catch:{ Exception -> 0x3785 }
    L_0x12fb:
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x3785 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x3785 }
        r0 = r41;	 Catch:{ Exception -> 0x3785 }
        if (r0 >= r4) goto L_0x3789;	 Catch:{ Exception -> 0x3785 }
    L_0x1307:
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x3785 }
        r0 = r41;	 Catch:{ Exception -> 0x3785 }
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x3785 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x3785 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x3785 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x3785 }
        r85 = r0;	 Catch:{ Exception -> 0x3785 }
        if (r85 == 0) goto L_0x1328;	 Catch:{ Exception -> 0x3785 }
    L_0x131b:
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x3785 }
        if (r4 != 0) goto L_0x3774;	 Catch:{ Exception -> 0x3785 }
    L_0x1321:
        r0 = r85;	 Catch:{ Exception -> 0x3785 }
        r4 = -r0;	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x3785 }
    L_0x1328:
        r41 = r41 + 1;
        goto L_0x12fb;
    L_0x132b:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x0d7d }
        r0 = r85;	 Catch:{ Exception -> 0x0d7d }
        r6 = -r0;	 Catch:{ Exception -> 0x0d7d }
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0d7d }
        goto L_0x0d05;	 Catch:{ Exception -> 0x0d7d }
    L_0x133c:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0d7d }
        r128 = r4.getWidth();	 Catch:{ Exception -> 0x0d7d }
        r41 = 0;	 Catch:{ Exception -> 0x0d7d }
    L_0x1346:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0d7d }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0d7d }
        r0 = r41;	 Catch:{ Exception -> 0x0d7d }
        if (r0 >= r4) goto L_0x0d81;	 Catch:{ Exception -> 0x0d7d }
    L_0x1352:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0d7d }
        r0 = r41;	 Catch:{ Exception -> 0x0d7d }
        r4 = r4.getLineLeft(r0);	 Catch:{ Exception -> 0x0d7d }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0d7d }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0d7d }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0d7d }
        r85 = r0;	 Catch:{ Exception -> 0x0d7d }
        if (r85 != 0) goto L_0x1371;	 Catch:{ Exception -> 0x0d7d }
    L_0x1366:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x0d7d }
        if (r4 == 0) goto L_0x1371;	 Catch:{ Exception -> 0x0d7d }
    L_0x136c:
        r4 = 0;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x0d7d }
    L_0x1371:
        if (r85 == 0) goto L_0x13be;	 Catch:{ Exception -> 0x0d7d }
    L_0x1373:
        r142 = r128 - r85;	 Catch:{ Exception -> 0x0d7d }
    L_0x1375:
        r0 = r41;	 Catch:{ Exception -> 0x0d7d }
        r1 = r116;	 Catch:{ Exception -> 0x0d7d }
        if (r0 < r1) goto L_0x1385;	 Catch:{ Exception -> 0x0d7d }
    L_0x137b:
        if (r116 == 0) goto L_0x138d;	 Catch:{ Exception -> 0x0d7d }
    L_0x137d:
        if (r85 == 0) goto L_0x138d;	 Catch:{ Exception -> 0x0d7d }
    L_0x137f:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0d7d }
        if (r4 == 0) goto L_0x138d;	 Catch:{ Exception -> 0x0d7d }
    L_0x1385:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Exception -> 0x0d7d }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0d7d }
        r142 = r142 + r4;	 Catch:{ Exception -> 0x0d7d }
    L_0x138d:
        r4 = r142 + r42;	 Catch:{ Exception -> 0x0d7d }
        r0 = r95;	 Catch:{ Exception -> 0x0d7d }
        if (r0 >= r4) goto L_0x13b3;	 Catch:{ Exception -> 0x0d7d }
    L_0x1393:
        if (r134 == 0) goto L_0x13a2;	 Catch:{ Exception -> 0x0d7d }
    L_0x1395:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.titleX;	 Catch:{ Exception -> 0x0d7d }
        r6 = r142 + r42;	 Catch:{ Exception -> 0x0d7d }
        r6 = r6 - r95;	 Catch:{ Exception -> 0x0d7d }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.titleX = r4;	 Catch:{ Exception -> 0x0d7d }
    L_0x13a2:
        if (r48 == 0) goto L_0x13b1;	 Catch:{ Exception -> 0x0d7d }
    L_0x13a4:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.authorX;	 Catch:{ Exception -> 0x0d7d }
        r6 = r142 + r42;	 Catch:{ Exception -> 0x0d7d }
        r6 = r6 - r95;	 Catch:{ Exception -> 0x0d7d }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0d7d }
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r0.authorX = r4;	 Catch:{ Exception -> 0x0d7d }
    L_0x13b1:
        r95 = r142 + r42;	 Catch:{ Exception -> 0x0d7d }
    L_0x13b3:
        r4 = r142 + r42;	 Catch:{ Exception -> 0x0d7d }
        r0 = r92;	 Catch:{ Exception -> 0x0d7d }
        r92 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0d7d }
        r41 = r41 + 1;	 Catch:{ Exception -> 0x0d7d }
        goto L_0x1346;	 Catch:{ Exception -> 0x0d7d }
    L_0x13be:
        if (r77 == 0) goto L_0x13c3;	 Catch:{ Exception -> 0x0d7d }
    L_0x13c0:
        r142 = r128;	 Catch:{ Exception -> 0x0d7d }
        goto L_0x1375;	 Catch:{ Exception -> 0x0d7d }
    L_0x13c3:
        r0 = r146;	 Catch:{ Exception -> 0x0d7d }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0d7d }
        r0 = r41;	 Catch:{ Exception -> 0x0d7d }
        r4 = r4.getLineWidth(r0);	 Catch:{ Exception -> 0x0d7d }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0d7d }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0d7d }
        r4 = (int) r8;	 Catch:{ Exception -> 0x0d7d }
        r0 = r128;	 Catch:{ Exception -> 0x0d7d }
        r142 = java.lang.Math.min(r4, r0);	 Catch:{ Exception -> 0x0d7d }
        goto L_0x1375;
    L_0x13da:
        r94 = r86;
        goto L_0x0da9;
    L_0x13de:
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r66);
        if (r4 == 0) goto L_0x148f;
    L_0x13e4:
        r4 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r4 != 0) goto L_0x13ee;
    L_0x13e8:
        r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r147;
        r0.gifState = r4;
    L_0x13ee:
        r0 = r146;
        r6 = r0.photoImage;
        r0 = r147;
        r4 = r0.gifState;
        r8 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x1489;
    L_0x13fc:
        r4 = 1;
    L_0x13fd:
        r6.setAllowStartAnimation(r4);
        r0 = r66;
        r4 = r0.thumb;
        r0 = r146;
        r0.currentPhotoObject = r4;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x147a;
    L_0x140e:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f45w;
        if (r4 == 0) goto L_0x141e;
    L_0x1416:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f44h;
        if (r4 != 0) goto L_0x147a;
    L_0x141e:
        r41 = 0;
    L_0x1420:
        r0 = r66;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r41;
        if (r0 >= r4) goto L_0x1458;
    L_0x142c:
        r0 = r66;
        r4 = r0.attributes;
        r0 = r41;
        r46 = r4.get(r0);
        r46 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r46;
        r0 = r46;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 != 0) goto L_0x1444;
    L_0x143e:
        r0 = r46;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x148c;
    L_0x1444:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r46;
        r6 = r0.f38w;
        r4.f45w = r6;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r46;
        r6 = r0.f37h;
        r4.f44h = r6;
    L_0x1458:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f45w;
        if (r4 == 0) goto L_0x1468;
    L_0x1460:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f44h;
        if (r4 != 0) goto L_0x147a;
    L_0x1468:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r146;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.f44h = r8;
        r4.f45w = r8;
    L_0x147a:
        r0 = r66;
        r1 = r146;
        r1.documentAttach = r0;
        r4 = 2;
        r0 = r146;
        r0.documentAttachType = r4;
        r14 = r139;
        goto L_0x0dc6;
    L_0x1489:
        r4 = 0;
        goto L_0x13fd;
    L_0x148c:
        r41 = r41 + 1;
        goto L_0x1420;
    L_0x148f:
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r66);
        if (r4 == 0) goto L_0x1518;
    L_0x1495:
        r0 = r66;
        r4 = r0.thumb;
        r0 = r146;
        r0.currentPhotoObject = r4;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x1509;
    L_0x14a3:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f45w;
        if (r4 == 0) goto L_0x14b3;
    L_0x14ab:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f44h;
        if (r4 != 0) goto L_0x1509;
    L_0x14b3:
        r41 = 0;
    L_0x14b5:
        r0 = r66;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r41;
        if (r0 >= r4) goto L_0x14e7;
    L_0x14c1:
        r0 = r66;
        r4 = r0.attributes;
        r0 = r41;
        r46 = r4.get(r0);
        r46 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r46;
        r0 = r46;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x1515;
    L_0x14d3:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r46;
        r6 = r0.f38w;
        r4.f45w = r6;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r46;
        r6 = r0.f37h;
        r4.f44h = r6;
    L_0x14e7:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f45w;
        if (r4 == 0) goto L_0x14f7;
    L_0x14ef:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f44h;
        if (r4 != 0) goto L_0x1509;
    L_0x14f7:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r146;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.f44h = r8;
        r4.f45w = r8;
    L_0x1509:
        r4 = 0;
        r0 = r146;
        r1 = r147;
        r0.createDocumentLayout(r4, r1);
        r14 = r139;
        goto L_0x0dc6;
    L_0x1515:
        r41 = r41 + 1;
        goto L_0x14b5;
    L_0x1518:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r66);
        if (r4 == 0) goto L_0x15a4;
    L_0x151e:
        r0 = r66;
        r4 = r0.thumb;
        r0 = r146;
        r0.currentPhotoObject = r4;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x1592;
    L_0x152c:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f45w;
        if (r4 == 0) goto L_0x153c;
    L_0x1534:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f44h;
        if (r4 != 0) goto L_0x1592;
    L_0x153c:
        r41 = 0;
    L_0x153e:
        r0 = r66;
        r4 = r0.attributes;
        r4 = r4.size();
        r0 = r41;
        if (r0 >= r4) goto L_0x1570;
    L_0x154a:
        r0 = r66;
        r4 = r0.attributes;
        r0 = r41;
        r46 = r4.get(r0);
        r46 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r46;
        r0 = r46;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 == 0) goto L_0x15a1;
    L_0x155c:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r46;
        r6 = r0.f38w;
        r4.f45w = r6;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r46;
        r6 = r0.f37h;
        r4.f44h = r6;
    L_0x1570:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f45w;
        if (r4 == 0) goto L_0x1580;
    L_0x1578:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f44h;
        if (r4 != 0) goto L_0x1592;
    L_0x1580:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r146;
        r6 = r0.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.f44h = r8;
        r4.f45w = r8;
    L_0x1592:
        r0 = r66;
        r1 = r146;
        r1.documentAttach = r0;
        r4 = 6;
        r0 = r146;
        r0.documentAttachType = r4;
        r14 = r139;
        goto L_0x0dc6;
    L_0x15a1:
        r41 = r41 + 1;
        goto L_0x153e;
    L_0x15a4:
        r0 = r146;
        r1 = r26;
        r2 = r131;
        r3 = r92;
        r0.calcBackgroundWidth(r1, r2, r3);
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r66);
        if (r4 != 0) goto L_0x3b2e;
    L_0x15b5:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r26;
        if (r4 >= r6) goto L_0x15cf;
    L_0x15c3:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r26;
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x15cf:
        r4 = org.telegram.messenger.MessageObject.isVoiceDocument(r66);
        if (r4 == 0) goto L_0x16ab;
    L_0x15d5:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r1 = r147;
        r0.createDocumentLayout(r4, r1);
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.textHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r6 = r0.linkPreviewHeight;
        r4 = r4 + r6;
        r0 = r146;
        r0.mediaOffsetY = r4;
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.linkPreviewHeight = r4;
        r4 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r26 = r26 - r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1671;
    L_0x1629:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x166f;
    L_0x1633:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x166f;
    L_0x1639:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x166f;
    L_0x163f:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
    L_0x1641:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x435c0000 float:220.0 double:5.58344962E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x41f00000 float:30.0 double:5.465589745E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r4 = r4 + r42;
        r0 = r92;
        r92 = java.lang.Math.max(r0, r4);
    L_0x1660:
        r0 = r146;
        r1 = r26;
        r2 = r131;
        r3 = r92;
        r0.calcBackgroundWidth(r1, r2, r3);
        r14 = r139;
        goto L_0x0dc6;
    L_0x166f:
        r4 = 0;
        goto L_0x1641;
    L_0x1671:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x16a9;
    L_0x167b:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x16a9;
    L_0x1681:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x16a9;
    L_0x1687:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
    L_0x1689:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x435c0000 float:220.0 double:5.58344962E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x41f00000 float:30.0 double:5.465589745E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r4 = r4 + r42;
        r0 = r92;
        r92 = java.lang.Math.max(r0, r4);
        goto L_0x1660;
    L_0x16a9:
        r4 = 0;
        goto L_0x1689;
    L_0x16ab:
        r4 = org.telegram.messenger.MessageObject.isMusicDocument(r66);
        if (r4 == 0) goto L_0x177c;
    L_0x16b1:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r1 = r147;
        r68 = r0.createDocumentLayout(r4, r1);
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.textHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r6 = r0.linkPreviewHeight;
        r4 = r4 + r6;
        r0 = r146;
        r0.mediaOffsetY = r4;
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.linkPreviewHeight = r4;
        r4 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r26 = r26 - r4;
        r4 = r68 + r42;
        r6 = NUM; // 0x42bc0000 float:94.0 double:5.53164308E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r92;
        r92 = java.lang.Math.max(r0, r4);
        r0 = r146;
        r4 = r0.songLayout;
        if (r4 == 0) goto L_0x173e;
    L_0x1715:
        r0 = r146;
        r4 = r0.songLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x173e;
    L_0x171f:
        r0 = r92;
        r4 = (float) r0;
        r0 = r146;
        r6 = r0.songLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r0 = r42;
        r8 = (float) r0;
        r6 = r6 + r8;
        r8 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r6 = r6 + r8;
        r4 = java.lang.Math.max(r4, r6);
        r0 = (int) r4;
        r92 = r0;
    L_0x173e:
        r0 = r146;
        r4 = r0.performerLayout;
        if (r4 == 0) goto L_0x176d;
    L_0x1744:
        r0 = r146;
        r4 = r0.performerLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x176d;
    L_0x174e:
        r0 = r92;
        r4 = (float) r0;
        r0 = r146;
        r6 = r0.performerLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r0 = r42;
        r8 = (float) r0;
        r6 = r6 + r8;
        r8 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r6 = r6 + r8;
        r4 = java.lang.Math.max(r4, r6);
        r0 = (int) r4;
        r92 = r0;
    L_0x176d:
        r0 = r146;
        r1 = r26;
        r2 = r131;
        r3 = r92;
        r0.calcBackgroundWidth(r1, r2, r3);
        r14 = r139;
        goto L_0x0dc6;
    L_0x177c:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x43280000 float:168.0 double:5.566612494E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r1 = r147;
        r0.createDocumentLayout(r4, r1);
        r4 = 1;
        r0 = r146;
        r0.drawImageButton = r4;
        r0 = r146;
        r4 = r0.drawPhotoImage;
        if (r4 == 0) goto L_0x17d8;
    L_0x1799:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.linkPreviewHeight = r4;
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 0;
        r0 = r146;
        r8 = r0.totalHeight;
        r0 = r146;
        r9 = r0.namesOffset;
        r8 = r8 + r9;
        r9 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r4.setImageCoords(r6, r8, r9, r10);
        r14 = r139;
        goto L_0x0dc6;
    L_0x17d8:
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.textHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r6 = r0.linkPreviewHeight;
        r4 = r4 + r6;
        r0 = r146;
        r0.mediaOffsetY = r4;
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 0;
        r0 = r146;
        r8 = r0.totalHeight;
        r0 = r146;
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
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.linkPreviewHeight = r4;
        r14 = r139;
        goto L_0x0dc6;
    L_0x1834:
        if (r107 == 0) goto L_0x188d;
    L_0x1836:
        if (r135 == 0) goto L_0x1886;
    L_0x1838:
        r4 = "photo";
        r0 = r135;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x1886;
    L_0x1843:
        r4 = 1;
    L_0x1844:
        r0 = r146;
        r0.drawImageButton = r4;
        r0 = r147;
        r8 = r0.photoThumbs;
        r0 = r146;
        r4 = r0.drawImageButton;
        if (r4 == 0) goto L_0x1888;
    L_0x1852:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x1856:
        r0 = r146;
        r6 = r0.drawImageButton;
        if (r6 != 0) goto L_0x188b;
    L_0x185c:
        r6 = 1;
    L_0x185d:
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r4, r6);
        r0 = r146;
        r0.currentPhotoObject = r4;
        r0 = r147;
        r4 = r0.photoThumbs;
        r6 = 80;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r146;
        r0.currentPhotoObjectThumb = r4;
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        r0 = r146;
        r6 = r0.currentPhotoObject;
        if (r4 != r6) goto L_0x3b2e;
    L_0x187d:
        r4 = 0;
        r0 = r146;
        r0.currentPhotoObjectThumb = r4;
        r14 = r139;
        goto L_0x0dc6;
    L_0x1886:
        r4 = 0;
        goto L_0x1844;
    L_0x1888:
        r4 = r94;
        goto L_0x1856;
    L_0x188b:
        r6 = 0;
        goto L_0x185d;
    L_0x188d:
        if (r139 == 0) goto L_0x3b2e;
    L_0x188f:
        r0 = r139;
        r4 = r0.mime_type;
        r6 = "image/";
        r4 = r4.startsWith(r6);
        if (r4 != 0) goto L_0x3b2a;
    L_0x189c:
        r14 = 0;
    L_0x189d:
        r4 = 0;
        r0 = r146;
        r0.drawImageButton = r4;
        goto L_0x0dc6;
    L_0x18a4:
        r4 = 0;
        goto L_0x0e15;
    L_0x18a7:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r4 = (float) r4;
        r6 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r94 = r0;
        goto L_0x0e55;
    L_0x18b4:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 7;
        if (r4 != r6) goto L_0x0e55;
    L_0x18bb:
        r94 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setAllowDecodeSingleFrame(r6);
        goto L_0x0e55;
    L_0x18c7:
        r4 = 0;
        goto L_0x0e61;
    L_0x18ca:
        r4 = -1;
        r14.size = r4;
        goto L_0x0e85;
    L_0x18cf:
        r0 = r146;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x18db;
    L_0x18d5:
        r0 = r146;
        r4 = r0.hasInvoicePreview;
        if (r4 == 0) goto L_0x18ff;
    L_0x18db:
        r142 = 640; // 0x280 float:8.97E-43 double:3.16E-321;
        r78 = 360; // 0x168 float:5.04E-43 double:1.78E-321;
        r0 = r142;
        r4 = (float) r0;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r94 - r6;
        r6 = (float) r6;
        r121 = r4 / r6;
        r0 = r142;
        r4 = (float) r0;
        r4 = r4 / r121;
        r0 = (int) r4;
        r142 = r0;
        r0 = r78;
        r4 = (float) r0;
        r4 = r4 / r121;
        r0 = (int) r4;
        r78 = r0;
        goto L_0x0e92;
    L_0x18ff:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.f45w;
        r142 = r0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.f44h;
        r78 = r0;
        r0 = r142;
        r4 = (float) r0;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r94 - r6;
        r6 = (float) r6;
        r121 = r4 / r6;
        r0 = r142;
        r4 = (float) r0;
        r4 = r4 / r121;
        r0 = (int) r4;
        r142 = r0;
        r0 = r78;
        r4 = (float) r0;
        r4 = r4 / r121;
        r0 = (int) r4;
        r78 = r0;
        if (r7 == 0) goto L_0x1944;
    L_0x192f:
        if (r7 == 0) goto L_0x1956;
    L_0x1931:
        r4 = r7.toLowerCase();
        r6 = "instagram";
        r4 = r4.equals(r6);
        if (r4 != 0) goto L_0x1956;
    L_0x193e:
        r0 = r146;
        r4 = r0.documentAttachType;
        if (r4 != 0) goto L_0x1956;
    L_0x1944:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r4 = r4 / 3;
        r0 = r78;
        if (r0 <= r4) goto L_0x0e92;
    L_0x194e:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r78 = r4 / 3;
        goto L_0x0e92;
    L_0x1956:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r4 = r4 / 2;
        r0 = r78;
        if (r0 <= r4) goto L_0x0e92;
    L_0x1960:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r78 = r4 / 2;
        goto L_0x0e92;
    L_0x1968:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r78;
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.linkPreviewHeight;
        r4 = r4 + r78;
        r0 = r146;
        r0.linkPreviewHeight = r4;
        goto L_0x0ede;
    L_0x1985:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 6;
        if (r4 != r6) goto L_0x19c6;
    L_0x198c:
        r0 = r146;
        r15 = r0.photoImage;
        r0 = r146;
        r0 = r0.documentAttach;
        r16 = r0;
        r17 = 0;
        r0 = r146;
        r0 = r0.currentPhotoFilter;
        r18 = r0;
        r19 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x19c3;
    L_0x19a6:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r20 = r0;
    L_0x19ae:
        r21 = "b1";
        r0 = r146;
        r4 = r0.documentAttach;
        r0 = r4.size;
        r22 = r0;
        r23 = "webp";
        r24 = 1;
        r15.setImage(r16, r17, r18, r19, r20, r21, r22, r23, r24);
        goto L_0x0f46;
    L_0x19c3:
        r20 = 0;
        goto L_0x19ae;
    L_0x19c6:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 4;
        if (r4 != r6) goto L_0x1a07;
    L_0x19cd:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setNeedsQualityThumb(r6);
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setShouldGenerateQualityThumb(r6);
        r0 = r146;
        r4 = r0.photoImage;
        r0 = r147;
        r4.setParentMessageObject(r0);
        r0 = r146;
        r15 = r0.photoImage;
        r16 = 0;
        r17 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r18 = r0;
        r0 = r146;
        r0 = r0.currentPhotoFilter;
        r19 = r0;
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r15.setImage(r16, r17, r18, r19, r20, r21, r22);
        goto L_0x0f46;
    L_0x1a07:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 2;
        if (r4 == r6) goto L_0x1a15;
    L_0x1a0e:
        r0 = r146;
        r4 = r0.documentAttachType;
        r6 = 7;
        if (r4 != r6) goto L_0x1ad9;
    L_0x1a15:
        r70 = org.telegram.messenger.FileLoader.getAttachFileName(r66);
        r49 = 0;
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r66);
        if (r4 == 0) goto L_0x1a90;
    L_0x1a21:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r6 = r6 / 2;
        r4.setRoundRadius(r6);
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r146;
        r6 = r0.currentMessageObject;
        r49 = r4.canDownloadMedia(r6);
    L_0x1a3c:
        r4 = r147.isSending();
        if (r4 != 0) goto L_0x1aaa;
    L_0x1a42:
        r4 = r147.isEditing();
        if (r4 != 0) goto L_0x1aaa;
    L_0x1a48:
        r0 = r147;
        r4 = r0.mediaExists;
        if (r4 != 0) goto L_0x1a60;
    L_0x1a4e:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r70;
        r4 = r4.isLoadingFile(r0);
        if (r4 != 0) goto L_0x1a60;
    L_0x1a5e:
        if (r49 == 0) goto L_0x1aaa;
    L_0x1a60:
        r4 = 0;
        r0 = r146;
        r0.photoNotSet = r4;
        r0 = r146;
        r15 = r0.photoImage;
        r17 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x1aa7;
    L_0x1a71:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r18 = r0;
    L_0x1a79:
        r0 = r146;
        r0 = r0.currentPhotoFilterThumb;
        r19 = r0;
        r0 = r66;
        r0 = r0.size;
        r20 = r0;
        r21 = 0;
        r22 = 0;
        r16 = r66;
        r15.setImage(r16, r17, r18, r19, r20, r21, r22);
        goto L_0x0f46;
    L_0x1a90:
        r4 = org.telegram.messenger.MessageObject.isNewGifDocument(r66);
        if (r4 == 0) goto L_0x1a3c;
    L_0x1a96:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r146;
        r6 = r0.currentMessageObject;
        r49 = r4.canDownloadMedia(r6);
        goto L_0x1a3c;
    L_0x1aa7:
        r18 = 0;
        goto L_0x1a79;
    L_0x1aaa:
        r4 = 1;
        r0 = r146;
        r0.photoNotSet = r4;
        r0 = r146;
        r15 = r0.photoImage;
        r16 = 0;
        r17 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x1ad6;
    L_0x1abd:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r18 = r0;
    L_0x1ac5:
        r0 = r146;
        r0 = r0.currentPhotoFilterThumb;
        r19 = r0;
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r15.setImage(r16, r17, r18, r19, r20, r21, r22);
        goto L_0x0f46;
    L_0x1ad6:
        r18 = 0;
        goto L_0x1ac5;
    L_0x1ad9:
        r0 = r147;
        r0 = r0.mediaExists;
        r108 = r0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r70 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r0 = r146;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x1b11;
    L_0x1aed:
        if (r108 != 0) goto L_0x1b11;
    L_0x1aef:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r146;
        r6 = r0.currentMessageObject;
        r4 = r4.canDownloadMedia(r6);
        if (r4 != 0) goto L_0x1b11;
    L_0x1b01:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r70;
        r4 = r4.isLoadingFile(r0);
        if (r4 == 0) goto L_0x1b4a;
    L_0x1b11:
        r4 = 0;
        r0 = r146;
        r0.photoNotSet = r4;
        r0 = r146;
        r15 = r0.photoImage;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r16 = r0;
        r0 = r146;
        r0 = r0.currentPhotoFilter;
        r17 = r0;
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x1b47;
    L_0x1b2e:
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        r0 = r4.location;
        r18 = r0;
    L_0x1b36:
        r0 = r146;
        r0 = r0.currentPhotoFilterThumb;
        r19 = r0;
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r15.setImage(r16, r17, r18, r19, r20, r21, r22);
        goto L_0x0f46;
    L_0x1b47:
        r18 = 0;
        goto L_0x1b36;
    L_0x1b4a:
        r4 = 1;
        r0 = r146;
        r0.photoNotSet = r4;
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x1b8a;
    L_0x1b55:
        r0 = r146;
        r15 = r0.photoImage;
        r16 = 0;
        r17 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        r0 = r4.location;
        r18 = r0;
        r4 = java.util.Locale.US;
        r6 = "%d_%d_b";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r142);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r78);
        r8[r9] = r10;
        r19 = java.lang.String.format(r4, r6, r8);
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r15.setImage(r16, r17, r18, r19, r20, r21, r22);
        goto L_0x0f46;
    L_0x1b8a:
        r0 = r146;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        goto L_0x0f46;
    L_0x1b96:
        r0 = r146;
        r4 = r0.hasGamePreview;
        if (r4 == 0) goto L_0x0fa3;
    L_0x1b9c:
        r4 = "AttachGame";
        r6 = NUM; // 0x7f0c00b7 float:1.8609563E38 double:1.053097489E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
        r4 = org.telegram.ui.ActionBar.Theme.chat_gamePaint;
        r4 = r4.measureText(r5);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r0 = r146;
        r0.durationWidth = r4;
        r15 = new android.text.StaticLayout;
        r17 = org.telegram.ui.ActionBar.Theme.chat_gamePaint;
        r0 = r146;
        r0 = r0.durationWidth;
        r18 = r0;
        r19 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r20 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r21 = 0;
        r22 = 0;
        r16 = r5;
        r15.<init>(r16, r17, r18, r19, r20, r21, r22);
        r0 = r146;
        r0.videoInfoLayout = r15;
        goto L_0x0fa3;
    L_0x1bd7:
        r0 = r146;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        r0 = r146;
        r4 = r0.linkPreviewHeight;
        r6 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.linkPreviewHeight = r4;
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        goto L_0x0fa3;
    L_0x1c01:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.test;
        if (r4 == 0) goto L_0x1c1b;
    L_0x1c0b:
        r4 = "PaymentTestInvoice";
        r6 = NUM; // 0x7f0c058b float:1.861207E38 double:1.0530980995E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
        goto L_0x0fc3;
    L_0x1c1b:
        r4 = "PaymentInvoice";
        r6 = NUM; // 0x7f0c056c float:1.8612007E38 double:1.053098084E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r5 = r4.toUpperCase();
        goto L_0x0fc3;
    L_0x1c2b:
        r4 = 0;
        goto L_0x106b;
    L_0x1c2e:
        r0 = r146;
        r4 = r0.durationWidth;
        r4 = r4 + r132;
        r0 = r92;
        r92 = java.lang.Math.max(r4, r0);
        goto L_0x1097;
    L_0x1c3c:
        r0 = r146;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        r0 = r146;
        r1 = r26;
        r2 = r131;
        r3 = r92;
        r0.calcBackgroundWidth(r1, r2, r3);
        goto L_0x10d4;
    L_0x1c53:
        r0 = r147;
        r4 = r0.type;
        r6 = 16;
        if (r4 != r6) goto L_0x1e0b;
    L_0x1c5b:
        r4 = 0;
        r0 = r146;
        r0.drawName = r4;
        r4 = 0;
        r0 = r146;
        r0.drawForwardedName = r4;
        r4 = 0;
        r0 = r146;
        r0.drawPhotoImage = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1da0;
    L_0x1c70:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x1d9c;
    L_0x1c7a:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x1d9c;
    L_0x1c80:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x1d9c;
    L_0x1c86:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x1c88:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x1c9c:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.availableTimeWidth = r4;
        r4 = r146.getMaxNameWidth();
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r26 = r4 - r6;
        if (r26 >= 0) goto L_0x1cbf;
    L_0x1cb9:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r26 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x1cbf:
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r4 = r4.formatterDay;
        r0 = r147;
        r6 = r0.messageOwner;
        r6 = r6.date;
        r8 = (long) r6;
        r10 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r8 = r8 * r10;
        r129 = r4.format(r8);
        r0 = r147;
        r4 = r0.messageOwner;
        r0 = r4.action;
        r56 = r0;
        r56 = (org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall) r56;
        r0 = r56;
        r4 = r0.reason;
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        r81 = r0;
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x1ddd;
    L_0x1ceb:
        if (r81 == 0) goto L_0x1dd1;
    L_0x1ced:
        r4 = "CallMessageOutgoingMissed";
        r6 = NUM; // 0x7f0c010f float:1.8609742E38 double:1.0530975323E-314;
        r127 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x1cf7:
        r0 = r56;
        r4 = r0.duration;
        if (r4 <= 0) goto L_0x1d1f;
    L_0x1cfd:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r129;
        r4 = r4.append(r0);
        r6 = ", ";
        r4 = r4.append(r6);
        r0 = r56;
        r6 = r0.duration;
        r6 = org.telegram.messenger.LocaleController.formatCallDuration(r6);
        r4 = r4.append(r6);
        r129 = r4.toString();
    L_0x1d1f:
        r17 = new android.text.StaticLayout;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r0 = r26;
        r6 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r127;
        r18 = android.text.TextUtils.ellipsize(r0, r4, r6, r8);
        r19 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r20 = r26 + r4;
        r21 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r22 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r23 = 0;
        r24 = 0;
        r17.<init>(r18, r19, r20, r21, r22, r23, r24);
        r0 = r17;
        r1 = r146;
        r1.titleLayout = r0;
        r17 = new android.text.StaticLayout;
        r4 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r0 = r26;
        r6 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r129;
        r18 = android.text.TextUtils.ellipsize(r0, r4, r6, r8);
        r19 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r20 = r26 + r4;
        r21 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r22 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r23 = 0;
        r24 = 0;
        r17.<init>(r18, r19, r20, r21, r22, r23, r24);
        r0 = r17;
        r1 = r146;
        r1.docTitleLayout = r0;
        r146.setMessageObjectInternal(r147);
        r4 = NUM; // 0x42820000 float:65.0 double:5.51286321E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r146;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x10d4;
    L_0x1d8b:
        r0 = r146;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.namesOffset = r4;
        goto L_0x10d4;
    L_0x1d9c:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x1c88;
    L_0x1da0:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x1dce;
    L_0x1daa:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x1dce;
    L_0x1db0:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x1dce;
    L_0x1db6:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x1db8:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
        goto L_0x1c9c;
    L_0x1dce:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x1db8;
    L_0x1dd1:
        r4 = "CallMessageOutgoing";
        r6 = NUM; // 0x7f0c010e float:1.860974E38 double:1.053097532E-314;
        r127 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x1cf7;
    L_0x1ddd:
        if (r81 == 0) goto L_0x1deb;
    L_0x1ddf:
        r4 = "CallMessageIncomingMissed";
        r6 = NUM; // 0x7f0c010d float:1.8609738E38 double:1.0530975314E-314;
        r127 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x1cf7;
    L_0x1deb:
        r0 = r56;
        r4 = r0.reason;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
        if (r4 == 0) goto L_0x1dff;
    L_0x1df3:
        r4 = "CallMessageIncomingDeclined";
        r6 = NUM; // 0x7f0c010c float:1.8609736E38 double:1.053097531E-314;
        r127 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x1cf7;
    L_0x1dff:
        r4 = "CallMessageIncoming";
        r6 = NUM; // 0x7f0c010b float:1.8609733E38 double:1.0530975304E-314;
        r127 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x1cf7;
    L_0x1e0b:
        r0 = r147;
        r4 = r0.type;
        r6 = 12;
        if (r4 != r6) goto L_0x20a2;
    L_0x1e13:
        r4 = 0;
        r0 = r146;
        r0.drawName = r4;
        r4 = 1;
        r0 = r146;
        r0.drawForwardedName = r4;
        r4 = 1;
        r0 = r146;
        r0.drawPhotoImage = r4;
        r0 = r146;
        r4 = r0.photoImage;
        r6 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setRoundRadius(r6);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1fd1;
    L_0x1e35:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x1fcd;
    L_0x1e3f:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x1fcd;
    L_0x1e45:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x1fcd;
    L_0x1e4b:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x1e4d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x1e61:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.availableTimeWidth = r4;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.user_id;
        r136 = r0;
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r6 = java.lang.Integer.valueOf(r136);
        r137 = r4.getUser(r6);
        r4 = r146.getMaxNameWidth();
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r26 = r4 - r6;
        if (r26 >= 0) goto L_0x3b26;
    L_0x1e98:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r26 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r96 = r26;
    L_0x1ea0:
        r18 = 0;
        if (r137 == 0) goto L_0x1ebb;
    L_0x1ea4:
        r0 = r137;
        r4 = r0.photo;
        if (r4 == 0) goto L_0x1eb2;
    L_0x1eaa:
        r0 = r137;
        r4 = r0.photo;
        r0 = r4.photo_small;
        r18 = r0;
    L_0x1eb2:
        r0 = r146;
        r4 = r0.contactAvatarDrawable;
        r0 = r137;
        r4.setInfo(r0);
    L_0x1ebb:
        r0 = r146;
        r0 = r0.photoImage;
        r17 = r0;
        r19 = "50_50";
        if (r137 == 0) goto L_0x2002;
    L_0x1ec6:
        r0 = r146;
        r0 = r0.contactAvatarDrawable;
        r20 = r0;
    L_0x1ecc:
        r21 = 0;
        r22 = 0;
        r17.setImage(r18, r19, r20, r21, r22);
        r0 = r147;
        r4 = r0.vCardData;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x2011;
    L_0x1edd:
        r0 = r147;
        r0 = r0.vCardData;
        r106 = r0;
        r4 = 1;
        r0 = r146;
        r0.drawInstantView = r4;
        r4 = 5;
        r0 = r146;
        r0.drawInstantViewType = r4;
    L_0x1eed:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.first_name;
        r0 = r147;
        r6 = r0.messageOwner;
        r6 = r6.media;
        r6 = r6.last_name;
        r4 = org.telegram.messenger.ContactsController.formatName(r4, r6);
        r6 = 10;
        r8 = 32;
        r61 = r4.replace(r6, r8);
        r4 = r61.length();
        if (r4 != 0) goto L_0x1f19;
    L_0x1f0f:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.phone_number;
        r61 = r0;
    L_0x1f19:
        r19 = new android.text.StaticLayout;
        r4 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
        r0 = r96;
        r6 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r61;
        r20 = android.text.TextUtils.ellipsize(r0, r4, r6, r8);
        r21 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r22 = r96 + r4;
        r23 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r24 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r25 = 0;
        r26 = 0;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);
        r0 = r19;
        r1 = r146;
        r1.titleLayout = r0;
        r19 = new android.text.StaticLayout;
        r21 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r22 = r96 + r4;
        r23 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r24 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = (float) r4;
        r25 = r0;
        r26 = 0;
        r20 = r106;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);
        r0 = r19;
        r1 = r146;
        r1.docTitleLayout = r0;
        r146.setMessageObjectInternal(r147);
        r0 = r146;
        r4 = r0.drawForwardedName;
        if (r4 == 0) goto L_0x203b;
    L_0x1f72:
        r4 = r147.needDrawForwarded();
        if (r4 == 0) goto L_0x203b;
    L_0x1f78:
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x1f86;
    L_0x1f7e:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.minY;
        if (r4 != 0) goto L_0x203b;
    L_0x1f86:
        r0 = r146;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.namesOffset = r4;
    L_0x1f95:
        r4 = NUM; // 0x425c0000 float:55.0 double:5.50055916E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r146;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r146;
        r6 = r0.docTitleLayout;
        r6 = r6.getHeight();
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x1fc2;
    L_0x1fb3:
        r0 = r146;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.namesOffset = r4;
    L_0x1fc2:
        r0 = r146;
        r4 = r0.drawInstantView;
        if (r4 == 0) goto L_0x205a;
    L_0x1fc8:
        r146.createInstantViewButton();
        goto L_0x10d4;
    L_0x1fcd:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x1e4d;
    L_0x1fd1:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x1fff;
    L_0x1fdb:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x1fff;
    L_0x1fe1:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x1fff;
    L_0x1fe7:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x1fe9:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
        goto L_0x1e61;
    L_0x1fff:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x1fe9;
    L_0x2002:
        r6 = org.telegram.ui.ActionBar.Theme.chat_contactDrawable;
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x200f;
    L_0x200a:
        r4 = 1;
    L_0x200b:
        r20 = r6[r4];
        goto L_0x1ecc;
    L_0x200f:
        r4 = 0;
        goto L_0x200b;
    L_0x2011:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.phone_number;
        r106 = r0;
        r4 = android.text.TextUtils.isEmpty(r106);
        if (r4 != 0) goto L_0x202f;
    L_0x2021:
        r4 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r106 = (java.lang.String) r106;
        r0 = r106;
        r106 = r4.format(r0);
        goto L_0x1eed;
    L_0x202f:
        r4 = "NumberUnknown";
        r6 = NUM; // 0x7f0c04b7 float:1.861164E38 double:1.053097995E-314;
        r106 = org.telegram.messenger.LocaleController.getString(r4, r6);
        goto L_0x1eed;
    L_0x203b:
        r0 = r146;
        r4 = r0.drawNameLayout;
        if (r4 == 0) goto L_0x1f95;
    L_0x2041:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.reply_to_msg_id;
        if (r4 != 0) goto L_0x1f95;
    L_0x2049:
        r0 = r146;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.namesOffset = r4;
        goto L_0x1f95;
    L_0x205a:
        r0 = r146;
        r4 = r0.docTitleLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x10d4;
    L_0x2064:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42dc0000 float:110.0 double:5.54200439E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r6 = r0.docTitleLayout;
        r0 = r146;
        r8 = r0.docTitleLayout;
        r8 = r8.getLineCount();
        r8 = r8 + -1;
        r6 = r6.getLineWidth(r8);
        r8 = (double) r6;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r130 = r4 - r6;
        r0 = r146;
        r4 = r0.timeWidth;
        r0 = r130;
        if (r0 >= r4) goto L_0x10d4;
    L_0x2091:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        goto L_0x10d4;
    L_0x20a2:
        r0 = r147;
        r4 = r0.type;
        r6 = 2;
        if (r4 != r6) goto L_0x2147;
    L_0x20a9:
        r4 = 1;
        r0 = r146;
        r0.drawForwardedName = r4;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2117;
    L_0x20b4:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2114;
    L_0x20be:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x2114;
    L_0x20c4:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x2114;
    L_0x20ca:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x20cc:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x20e0:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r0 = r146;
        r1 = r147;
        r0.createDocumentLayout(r4, r1);
        r146.setMessageObjectInternal(r147);
        r4 = NUM; // 0x428c0000 float:70.0 double:5.51610112E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r146;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x10d4;
    L_0x2103:
        r0 = r146;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.namesOffset = r4;
        goto L_0x10d4;
    L_0x2114:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x20cc;
    L_0x2117:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2144;
    L_0x2121:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x2144;
    L_0x2127:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x2144;
    L_0x212d:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x212f:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
        goto L_0x20e0;
    L_0x2144:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x212f;
    L_0x2147:
        r0 = r147;
        r4 = r0.type;
        r6 = 14;
        if (r4 != r6) goto L_0x21e8;
    L_0x214f:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x21b8;
    L_0x2155:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x21b5;
    L_0x215f:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x21b5;
    L_0x2165:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x21b5;
    L_0x216b:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x216d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x2181:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r0 = r146;
        r1 = r147;
        r0.createDocumentLayout(r4, r1);
        r146.setMessageObjectInternal(r147);
        r4 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r146;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x10d4;
    L_0x21a4:
        r0 = r146;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.namesOffset = r4;
        goto L_0x10d4;
    L_0x21b5:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x216d;
    L_0x21b8:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x21e5;
    L_0x21c2:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x21e5;
    L_0x21c8:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x21e5;
    L_0x21ce:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x21d0:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
        goto L_0x2181;
    L_0x21e5:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x21d0;
    L_0x21e8:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.fwd_from;
        if (r4 == 0) goto L_0x2417;
    L_0x21f0:
        r0 = r147;
        r4 = r0.type;
        r6 = 13;
        if (r4 == r6) goto L_0x2417;
    L_0x21f8:
        r4 = 1;
    L_0x21f9:
        r0 = r146;
        r0.drawForwardedName = r4;
        r0 = r147;
        r4 = r0.type;
        r6 = 9;
        if (r4 == r6) goto L_0x241a;
    L_0x2205:
        r4 = 1;
    L_0x2206:
        r0 = r146;
        r0.mediaBackground = r4;
        r4 = 1;
        r0 = r146;
        r0.drawImageButton = r4;
        r4 = 1;
        r0 = r146;
        r0.drawPhotoImage = r4;
        r110 = 0;
        r109 = 0;
        r43 = 0;
        r0 = r147;
        r4 = r0.gifState;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x223d;
    L_0x2224:
        r4 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r4 != 0) goto L_0x223d;
    L_0x2228:
        r0 = r147;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x2237;
    L_0x2230:
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x223d;
    L_0x2237:
        r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r147;
        r0.gifState = r4;
    L_0x223d:
        r4 = r147.isRoundVideo();
        if (r4 == 0) goto L_0x2420;
    L_0x2243:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setAllowDecodeSingleFrame(r6);
        r0 = r146;
        r6 = r0.photoImage;
        r4 = org.telegram.messenger.MediaController.getInstance();
        r4 = r4.getPlayingMessageObject();
        if (r4 != 0) goto L_0x241d;
    L_0x2259:
        r4 = 1;
    L_0x225a:
        r6.setAllowStartAnimation(r4);
    L_0x225d:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = r147.needDrawBluredPreview();
        r4.setForcePreview(r6);
        r0 = r147;
        r4 = r0.type;
        r6 = 9;
        if (r4 != r6) goto L_0x248d;
    L_0x2270:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2439;
    L_0x2276:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2435;
    L_0x2280:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x2435;
    L_0x2286:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x2435;
    L_0x228c:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x228e:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x22a2:
        r4 = r146.checkNeedDrawShareButton(r147);
        if (r4 == 0) goto L_0x22b7;
    L_0x22a8:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x22b7:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x430a0000 float:138.0 double:5.55689877E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r26 = r4 - r6;
        r0 = r146;
        r1 = r26;
        r2 = r147;
        r0.createDocumentLayout(r1, r2);
        r0 = r147;
        r4 = r0.caption;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x22de;
    L_0x22d6:
        r4 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r26 = r26 + r4;
    L_0x22de:
        r0 = r146;
        r4 = r0.drawPhotoImage;
        if (r4 == 0) goto L_0x246a;
    L_0x22e4:
        r4 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r110 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r109 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x22f0:
        r0 = r26;
        r1 = r146;
        r1.availableTimeWidth = r0;
        r0 = r146;
        r4 = r0.drawPhotoImage;
        if (r4 != 0) goto L_0x233f;
    L_0x22fc:
        r0 = r147;
        r4 = r0.caption;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x233f;
    L_0x2306:
        r0 = r146;
        r4 = r0.infoLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x233f;
    L_0x2310:
        r146.measureTime(r147);
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42f40000 float:122.0 double:5.54977537E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r6 = r0.infoLayout;
        r8 = 0;
        r6 = r6.getLineWidth(r8);
        r8 = (double) r6;
        r8 = java.lang.Math.ceil(r8);
        r6 = (int) r8;
        r130 = r4 - r6;
        r0 = r146;
        r4 = r0.timeWidth;
        r0 = r130;
        if (r0 >= r4) goto L_0x233f;
    L_0x2337:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r109 = r109 + r4;
    L_0x233f:
        r146.setMessageObjectInternal(r147);
        r0 = r146;
        r4 = r0.drawForwardedName;
        if (r4 == 0) goto L_0x36ff;
    L_0x2348:
        r4 = r147.needDrawForwarded();
        if (r4 == 0) goto L_0x36ff;
    L_0x234e:
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x235c;
    L_0x2354:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.minY;
        if (r4 != 0) goto L_0x36ff;
    L_0x235c:
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 == r6) goto L_0x2372;
    L_0x2363:
        r0 = r146;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.namesOffset = r4;
    L_0x2372:
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r109;
        r0 = r146;
        r6 = r0.namesOffset;
        r4 = r4 + r6;
        r4 = r4 + r43;
        r0 = r146;
        r0.totalHeight = r4;
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x23a4;
    L_0x238b:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 != 0) goto L_0x23a4;
    L_0x2395:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.totalHeight = r4;
    L_0x23a4:
        r45 = 0;
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x23e4;
    L_0x23ac:
        r0 = r146;
        r4 = r0.currentPosition;
        r0 = r146;
        r4 = r0.getAdditionalWidthForPosition(r4);
        r110 = r110 + r4;
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 != 0) goto L_0x23d2;
    L_0x23c2:
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r109 = r109 + r4;
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r45 = r45 - r4;
    L_0x23d2:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 != 0) goto L_0x23e4;
    L_0x23dc:
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r109 = r109 + r4;
    L_0x23e4:
        r0 = r146;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x23f9;
    L_0x23ea:
        r0 = r146;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.namesOffset = r4;
    L_0x23f9:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 0;
        r8 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r146;
        r9 = r0.namesOffset;
        r8 = r8 + r9;
        r8 = r8 + r45;
        r0 = r110;
        r1 = r109;
        r4.setImageCoords(r6, r8, r0, r1);
        r146.invalidate();
        goto L_0x10d4;
    L_0x2417:
        r4 = 0;
        goto L_0x21f9;
    L_0x241a:
        r4 = 0;
        goto L_0x2206;
    L_0x241d:
        r4 = 0;
        goto L_0x225a;
    L_0x2420:
        r0 = r146;
        r6 = r0.photoImage;
        r0 = r147;
        r4 = r0.gifState;
        r8 = 0;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x2433;
    L_0x242d:
        r4 = 1;
    L_0x242e:
        r6.setAllowStartAnimation(r4);
        goto L_0x225d;
    L_0x2433:
        r4 = 0;
        goto L_0x242e;
    L_0x2435:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x228e;
    L_0x2439:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2467;
    L_0x2443:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x2467;
    L_0x2449:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x2467;
    L_0x244f:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x2451:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
        goto L_0x22a2;
    L_0x2467:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2451;
    L_0x246a:
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r110 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r109 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r147;
        r4 = r0.caption;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x248a;
    L_0x2480:
        r4 = NUM; // 0x424c0000 float:51.0 double:5.495378504E-315;
    L_0x2482:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r26 = r26 + r4;
        goto L_0x22f0;
    L_0x248a:
        r4 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        goto L_0x2482;
    L_0x248d:
        r0 = r147;
        r4 = r0.type;
        r6 = 4;
        if (r4 != r6) goto L_0x2955;
    L_0x2494:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.geo;
        r111 = r0;
        r0 = r111;
        r0 = r0.lat;
        r20 = r0;
        r0 = r111;
        r0 = r0._long;
        r22 = r0;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r4 == 0) goto L_0x2759;
    L_0x24b4:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x26e8;
    L_0x24ba:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x26e4;
    L_0x24c4:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x26e4;
    L_0x24ca:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x26e4;
    L_0x24d0:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x24d2:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x24e7:
        r4 = r146.checkNeedDrawShareButton(r147);
        if (r4 == 0) goto L_0x24fc;
    L_0x24ed:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x24fc:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42140000 float:37.0 double:5.477246216E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r26 = r4 - r6;
        r0 = r26;
        r1 = r146;
        r1.availableTimeWidth = r0;
        r4 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r96 = r26 - r4;
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r110 = r4 - r6;
        r4 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
        r109 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r102 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r0 = r102;
        r8 = (double) r0;
        r10 = 461425665NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.14159NUM;
        r114 = r8 / r10;
        r0 = r102;
        r8 = (double) r0;
        r10 = 460718241NUM; // 0x3ff000NUM float:0.0 double:1.0;
        r24 = 461425665NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.14159NUM;
        r24 = r24 * r20;
        r28 = 464053720NUM; // 0x406680NUM float:0.0 double:180.0;
        r24 = r24 / r28;
        r24 = java.lang.Math.sin(r24);
        r10 = r10 + r24;
        r24 = 460718241NUM; // 0x3ff000NUM float:0.0 double:1.0;
        r28 = 461425665NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.14159NUM;
        r28 = r28 * r20;
        r34 = 464053720NUM; // 0x406680NUM float:0.0 double:180.0;
        r28 = r28 / r34;
        r28 = java.lang.Math.sin(r28);
        r24 = r24 - r28;
        r10 = r10 / r24;
        r10 = java.lang.Math.log(r10);
        r10 = r10 * r114;
        r24 = 461168601NUM; // 0x400000NUM float:0.0 double:2.0;
        r10 = r10 / r24;
        r8 = r8 - r10;
        r8 = java.lang.Math.round(r8);
        r4 = NUM; // 0x4124cccd float:10.3 double:5.399795443E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 << 6;
        r10 = (long) r4;
        r8 = r8 - r10;
        r0 = (double) r8;
        r144 = r0;
        r8 = 460975305NUM; // 0x3ff921fb54442d18 float:3.37028055E12 double:1.570796NUM;
        r10 = 461168601NUM; // 0x400000NUM float:0.0 double:2.0;
        r0 = r102;
        r0 = (double) r0;
        r24 = r0;
        r24 = r144 - r24;
        r24 = r24 / r114;
        r24 = java.lang.Math.exp(r24);
        r24 = java.lang.Math.atan(r24);
        r10 = r10 * r24;
        r8 = r8 - r10;
        r10 = 464053720NUM; // 0x406680NUM float:0.0 double:180.0;
        r8 = r8 * r10;
        r10 = 461425665NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.14159NUM;
        r20 = r8 / r10;
        r0 = r146;
        r0 = r0.currentAccount;
        r19 = r0;
        r0 = r110;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r24 = r0;
        r0 = r109;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r0 = (int) r4;
        r25 = r0;
        r26 = 0;
        r27 = 15;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r19, r20, r22, r24, r25, r26, r27);
        r0 = r146;
        r0.currentUrl = r4;
        r0 = r110;
        r4 = (float) r0;
        r6 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r6;
        r4 = (int) r4;
        r0 = r109;
        r6 = (float) r0;
        r8 = org.telegram.messenger.AndroidUtilities.density;
        r6 = r6 / r8;
        r6 = (int) r6;
        r8 = 15;
        r9 = 2;
        r10 = org.telegram.messenger.AndroidUtilities.density;
        r10 = (double) r10;
        r10 = java.lang.Math.ceil(r10);
        r10 = (int) r10;
        r9 = java.lang.Math.min(r9, r10);
        r0 = r111;
        r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r4, r6, r8, r9);
        r0 = r146;
        r0.currentWebFile = r4;
        r4 = r146.isCurrentLocationTimeExpired(r147);
        r0 = r146;
        r0.locationExpired = r4;
        if (r4 != 0) goto L_0x271a;
    L_0x25ff:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setCrossfadeWithOldImage(r6);
        r4 = 0;
        r0 = r146;
        r0.mediaBackground = r4;
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r43 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r146;
        r4 = r0.invalidateRunnable;
        r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4, r8);
        r4 = 1;
        r0 = r146;
        r0.scheduledInvalidate = r4;
    L_0x2620:
        r24 = new android.text.StaticLayout;
        r4 = "AttachLiveLocation";
        r6 = NUM; // 0x7f0c00bb float:1.8609571E38 double:1.053097491E-314;
        r25 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r26 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
        r28 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r29 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r30 = 0;
        r31 = 0;
        r27 = r96;
        r24.<init>(r25, r26, r27, r28, r29, r30, r31);
        r0 = r24;
        r1 = r146;
        r1.docTitleLayout = r0;
        r18 = 0;
        r146.updateCurrentUserAndChat();
        r0 = r146;
        r4 = r0.currentUser;
        if (r4 == 0) goto L_0x272b;
    L_0x264c:
        r0 = r146;
        r4 = r0.currentUser;
        r4 = r4.photo;
        if (r4 == 0) goto L_0x265e;
    L_0x2654:
        r0 = r146;
        r4 = r0.currentUser;
        r4 = r4.photo;
        r0 = r4.photo_small;
        r18 = r0;
    L_0x265e:
        r0 = r146;
        r4 = r0.contactAvatarDrawable;
        r0 = r146;
        r6 = r0.currentUser;
        r4.setInfo(r6);
    L_0x2669:
        r0 = r146;
        r0 = r0.locationImageReceiver;
        r24 = r0;
        r26 = "50_50";
        r0 = r146;
        r0 = r0.contactAvatarDrawable;
        r27 = r0;
        r28 = 0;
        r29 = 0;
        r25 = r18;
        r24.setImage(r25, r26, r27, r28, r29);
        r24 = new android.text.StaticLayout;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.edit_date;
        if (r4 == 0) goto L_0x2750;
    L_0x268b:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.edit_date;
        r8 = (long) r4;
    L_0x2692:
        r25 = org.telegram.messenger.LocaleController.formatLocationUpdateDate(r8);
        r26 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
        r28 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r29 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r30 = 0;
        r31 = 0;
        r27 = r96;
        r24.<init>(r25, r26, r27, r28, r29, r30, r31);
        r0 = r24;
        r1 = r146;
        r1.infoLayout = r0;
    L_0x26ab:
        r0 = r147;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r0 = r4.mapProvider;
        r89 = r0;
        r4 = 2;
        r0 = r89;
        if (r0 != r4) goto L_0x290d;
    L_0x26bc:
        r0 = r146;
        r4 = r0.currentWebFile;
        if (r4 == 0) goto L_0x233f;
    L_0x26c2:
        r0 = r146;
        r0 = r0.photoImage;
        r27 = r0;
        r0 = r146;
        r0 = r0.currentWebFile;
        r28 = r0;
        r29 = 0;
        r6 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x290a;
    L_0x26d8:
        r4 = 1;
    L_0x26d9:
        r30 = r6[r4];
        r31 = 0;
        r32 = 0;
        r27.setImage(r28, r29, r30, r31, r32);
        goto L_0x233f;
    L_0x26e4:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x24d2;
    L_0x26e8:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2717;
    L_0x26f2:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x2717;
    L_0x26f8:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x2717;
    L_0x26fe:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x2700:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
        goto L_0x24e7;
    L_0x2717:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2700;
    L_0x271a:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.backgroundWidth = r4;
        goto L_0x2620;
    L_0x272b:
        r0 = r146;
        r4 = r0.currentChat;
        if (r4 == 0) goto L_0x2669;
    L_0x2731:
        r0 = r146;
        r4 = r0.currentChat;
        r4 = r4.photo;
        if (r4 == 0) goto L_0x2743;
    L_0x2739:
        r0 = r146;
        r4 = r0.currentChat;
        r4 = r4.photo;
        r0 = r4.photo_small;
        r18 = r0;
    L_0x2743:
        r0 = r146;
        r4 = r0.contactAvatarDrawable;
        r0 = r146;
        r6 = r0.currentChat;
        r4.setInfo(r6);
        goto L_0x2669;
    L_0x2750:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.date;
        r8 = (long) r4;
        goto L_0x2692;
    L_0x2759:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.title;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x28af;
    L_0x2767:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2877;
    L_0x276d:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x2873;
    L_0x2777:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x2873;
    L_0x277d:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x2873;
    L_0x2783:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x2785:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x2799:
        r4 = r146.checkNeedDrawShareButton(r147);
        if (r4 == 0) goto L_0x27ae;
    L_0x279f:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x27ae:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x42f60000 float:123.0 double:5.55042295E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r26 = r4 - r6;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.title;
        r24 = r0;
        r25 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
        r27 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r29 = 0;
        r30 = 0;
        r31 = android.text.TextUtils.TruncateAt.END;
        r33 = 2;
        r32 = r26;
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r24, r25, r26, r27, r28, r29, r30, r31, r32, r33);
        r0 = r146;
        r0.docTitleLayout = r4;
        r0 = r146;
        r4 = r0.docTitleLayout;
        r84 = r4.getLineCount();
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.address;
        if (r4 == 0) goto L_0x28a8;
    L_0x27ee:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.address;
        r4 = r4.length();
        if (r4 <= 0) goto L_0x28a8;
    L_0x27fc:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.address;
        r24 = r0;
        r25 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
        r27 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r29 = 0;
        r30 = 0;
        r31 = android.text.TextUtils.TruncateAt.END;
        r4 = 3;
        r6 = 3 - r84;
        r33 = java.lang.Math.min(r4, r6);
        r32 = r26;
        r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r24, r25, r26, r27, r28, r29, r30, r31, r32, r33);
        r0 = r146;
        r0.infoLayout = r4;
    L_0x2823:
        r4 = 0;
        r0 = r146;
        r0.mediaBackground = r4;
        r0 = r26;
        r1 = r146;
        r1.availableTimeWidth = r0;
        r4 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r110 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r109 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r146;
        r0 = r0.currentAccount;
        r27 = r0;
        r32 = 72;
        r33 = 72;
        r34 = 1;
        r35 = 15;
        r28 = r20;
        r30 = r22;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r27, r28, r30, r32, r33, r34, r35);
        r0 = r146;
        r0.currentUrl = r4;
        r4 = 72;
        r6 = 72;
        r8 = 15;
        r9 = 2;
        r10 = org.telegram.messenger.AndroidUtilities.density;
        r10 = (double) r10;
        r10 = java.lang.Math.ceil(r10);
        r10 = (int) r10;
        r9 = java.lang.Math.min(r9, r10);
        r0 = r111;
        r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r4, r6, r8, r9);
        r0 = r146;
        r0.currentWebFile = r4;
        goto L_0x26ab;
    L_0x2873:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x2785;
    L_0x2877:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r4.x;
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x28a5;
    L_0x2881:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x28a5;
    L_0x2887:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x28a5;
    L_0x288d:
        r4 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x288f:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r6 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = java.lang.Math.min(r4, r6);
        r0 = r146;
        r0.backgroundWidth = r4;
        goto L_0x2799;
    L_0x28a5:
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x288f;
    L_0x28a8:
        r4 = 0;
        r0 = r146;
        r0.infoLayout = r4;
        goto L_0x2823;
    L_0x28af:
        r4 = NUM; // 0x433a0000 float:186.0 double:5.57244073E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r146;
        r0.availableTimeWidth = r4;
        r4 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r110 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r109 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r110;
        r0 = r146;
        r0.backgroundWidth = r4;
        r0 = r146;
        r0 = r0.currentAccount;
        r27 = r0;
        r32 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r33 = 100;
        r34 = 1;
        r35 = 15;
        r28 = r20;
        r30 = r22;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r27, r28, r30, r32, r33, r34, r35);
        r0 = r146;
        r0.currentUrl = r4;
        r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r6 = 100;
        r8 = 15;
        r9 = 2;
        r10 = org.telegram.messenger.AndroidUtilities.density;
        r10 = (double) r10;
        r10 = java.lang.Math.ceil(r10);
        r10 = (int) r10;
        r9 = java.lang.Math.min(r9, r10);
        r0 = r111;
        r4 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r4, r6, r8, r9);
        r0 = r146;
        r0.currentWebFile = r4;
        goto L_0x26ab;
    L_0x290a:
        r4 = 0;
        goto L_0x26d9;
    L_0x290d:
        r4 = 3;
        r0 = r89;
        if (r0 == r4) goto L_0x2917;
    L_0x2912:
        r4 = 4;
        r0 = r89;
        if (r0 != r4) goto L_0x292b;
    L_0x2917:
        r4 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r146;
        r6 = r0.currentUrl;
        r0 = r146;
        r8 = r0.currentWebFile;
        r4.addTestWebFile(r6, r8);
        r4 = 1;
        r0 = r146;
        r0.addedForTest = r4;
    L_0x292b:
        r0 = r146;
        r4 = r0.currentUrl;
        if (r4 == 0) goto L_0x233f;
    L_0x2931:
        r0 = r146;
        r0 = r0.photoImage;
        r27 = r0;
        r0 = r146;
        r0 = r0.currentUrl;
        r28 = r0;
        r29 = 0;
        r6 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x2953;
    L_0x2947:
        r4 = 1;
    L_0x2948:
        r30 = r6[r4];
        r31 = 0;
        r32 = 0;
        r27.setImage(r28, r29, r30, r31, r32);
        goto L_0x233f;
    L_0x2953:
        r4 = 0;
        goto L_0x2948;
    L_0x2955:
        r0 = r147;
        r4 = r0.type;
        r6 = 13;
        if (r4 != r6) goto L_0x2aec;
    L_0x295d:
        r4 = 0;
        r0 = r146;
        r0.drawBackground = r4;
        r41 = 0;
    L_0x2964:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r4 = r4.size();
        r0 = r41;
        if (r0 >= r4) goto L_0x299a;
    L_0x2976:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r0 = r41;
        r46 = r4.get(r0);
        r46 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r46;
        r0 = r46;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 == 0) goto L_0x2a68;
    L_0x298e:
        r0 = r46;
        r0 = r0.f38w;
        r110 = r0;
        r0 = r46;
        r0 = r0.f37h;
        r109 = r0;
    L_0x299a:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2a6c;
    L_0x29a0:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
        r26 = r4 * r6;
        r93 = r26;
    L_0x29ac:
        if (r110 != 0) goto L_0x29bb;
    L_0x29ae:
        r0 = r93;
        r0 = (int) r0;
        r109 = r0;
        r4 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r110 = r109 + r4;
    L_0x29bb:
        r0 = r109;
        r4 = (float) r0;
        r0 = r110;
        r6 = (float) r0;
        r6 = r26 / r6;
        r4 = r4 * r6;
        r0 = (int) r4;
        r109 = r0;
        r0 = r26;
        r0 = (int) r0;
        r110 = r0;
        r0 = r109;
        r4 = (float) r0;
        r4 = (r4 > r93 ? 1 : (r4 == r93 ? 0 : -1));
        if (r4 <= 0) goto L_0x29e4;
    L_0x29d3:
        r0 = r110;
        r4 = (float) r0;
        r0 = r109;
        r6 = (float) r0;
        r6 = r93 / r6;
        r4 = r4 * r6;
        r0 = (int) r4;
        r110 = r0;
        r0 = r93;
        r0 = (int) r0;
        r109 = r0;
    L_0x29e4:
        r4 = 6;
        r0 = r146;
        r0.documentAttachType = r4;
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r110 - r4;
        r0 = r146;
        r0.availableTimeWidth = r4;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r110;
        r0 = r146;
        r0.backgroundWidth = r4;
        r0 = r147;
        r4 = r0.photoThumbs;
        r6 = 80;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r146;
        r0.currentPhotoObjectThumb = r4;
        r0 = r147;
        r4 = r0.attachPathExists;
        if (r4 == 0) goto L_0x2a84;
    L_0x2a15:
        r0 = r146;
        r0 = r0.photoImage;
        r27 = r0;
        r28 = 0;
        r0 = r147;
        r4 = r0.messageOwner;
        r0 = r4.attachPath;
        r29 = r0;
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r110);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r109);
        r8[r9] = r10;
        r30 = java.lang.String.format(r4, r6, r8);
        r31 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x2a81;
    L_0x2a47:
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        r0 = r4.location;
        r32 = r0;
    L_0x2a4f:
        r33 = "b1";
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r0 = r4.size;
        r34 = r0;
        r35 = "webp";
        r36 = 1;
        r27.setImage(r28, r29, r30, r31, r32, r33, r34, r35, r36);
        goto L_0x233f;
    L_0x2a68:
        r41 = r41 + 1;
        goto L_0x2964;
    L_0x2a6c:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r26 = r4 * r6;
        r93 = r26;
        goto L_0x29ac;
    L_0x2a81:
        r32 = 0;
        goto L_0x2a4f;
    L_0x2a84:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r8 = r4.id;
        r10 = 0;
        r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r4 == 0) goto L_0x233f;
    L_0x2a94:
        r0 = r146;
        r0 = r0.photoImage;
        r27 = r0;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.document;
        r28 = r0;
        r29 = 0;
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r110);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.Integer.valueOf(r109);
        r8[r9] = r10;
        r30 = java.lang.String.format(r4, r6, r8);
        r31 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x2ae9;
    L_0x2ac8:
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        r0 = r4.location;
        r32 = r0;
    L_0x2ad0:
        r33 = "b1";
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r0 = r4.size;
        r34 = r0;
        r35 = "webp";
        r36 = 1;
        r27.setImage(r28, r29, r30, r31, r32, r33, r34, r35, r36);
        goto L_0x233f;
    L_0x2ae9:
        r32 = 0;
        goto L_0x2ad0;
    L_0x2aec:
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x2cd9;
    L_0x2af3:
        r110 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r94 = r110;
    L_0x2af7:
        r4 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r109 = r110 + r4;
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 == r6) goto L_0x2b1c;
    L_0x2b06:
        r4 = r146.checkNeedDrawShareButton(r147);
        if (r4 == 0) goto L_0x2b1c;
    L_0x2b0c:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r94 = r94 - r4;
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r110 = r110 - r4;
    L_0x2b1c:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r0 = r110;
        if (r0 <= r4) goto L_0x2b28;
    L_0x2b24:
        r110 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x2b28:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r0 = r109;
        if (r0 <= r4) goto L_0x2b34;
    L_0x2b30:
        r109 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x2b34:
        r0 = r147;
        r4 = r0.type;
        r6 = 1;
        if (r4 != r6) goto L_0x2d07;
    L_0x2b3b:
        r146.updateSecretTimeText(r147);
        r0 = r147;
        r4 = r0.photoThumbs;
        r6 = 80;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r146;
        r0.currentPhotoObjectThumb = r4;
    L_0x2b4c:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        if (r4 != 0) goto L_0x2b5d;
    L_0x2b52:
        r0 = r147;
        r4 = r0.caption;
        if (r4 == 0) goto L_0x2b5d;
    L_0x2b58:
        r4 = 0;
        r0 = r146;
        r0.mediaBackground = r4;
    L_0x2b5d:
        r0 = r147;
        r4 = r0.photoThumbs;
        r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r0 = r146;
        r0.currentPhotoObject = r4;
        r138 = 0;
        r75 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x2b86;
    L_0x2b77:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r146;
        r6 = r0.currentPhotoObjectThumb;
        if (r4 != r6) goto L_0x2b86;
    L_0x2b81:
        r4 = 0;
        r0 = r146;
        r0.currentPhotoObjectThumb = r4;
    L_0x2b86:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x2bda;
    L_0x2b8c:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f45w;
        r4 = (float) r4;
        r0 = r110;
        r6 = (float) r0;
        r121 = r4 / r6;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f45w;
        r4 = (float) r4;
        r4 = r4 / r121;
        r0 = (int) r4;
        r138 = r0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f44h;
        r4 = (float) r4;
        r4 = r4 / r121;
        r0 = (int) r4;
        r75 = r0;
        if (r138 != 0) goto L_0x2bb8;
    L_0x2bb2:
        r4 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r138 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x2bb8:
        if (r75 != 0) goto L_0x2bc0;
    L_0x2bba:
        r4 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r75 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x2bc0:
        r0 = r75;
        r1 = r109;
        if (r0 <= r1) goto L_0x2dc7;
    L_0x2bc6:
        r0 = r75;
        r0 = (float) r0;
        r122 = r0;
        r75 = r109;
        r0 = r75;
        r4 = (float) r0;
        r122 = r122 / r4;
        r0 = r138;
        r4 = (float) r0;
        r4 = r4 / r122;
        r0 = (int) r4;
        r138 = r0;
    L_0x2bda:
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x2be5;
    L_0x2be1:
        r75 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r138 = r75;
    L_0x2be5:
        if (r138 == 0) goto L_0x2be9;
    L_0x2be7:
        if (r75 != 0) goto L_0x2c5b;
    L_0x2be9:
        r0 = r147;
        r4 = r0.type;
        r6 = 8;
        if (r4 != r6) goto L_0x2c5b;
    L_0x2bf1:
        r41 = 0;
    L_0x2bf3:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r4 = r4.size();
        r0 = r41;
        if (r0 >= r4) goto L_0x2c5b;
    L_0x2c05:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = r4.attributes;
        r0 = r41;
        r46 = r4.get(r0);
        r46 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r46;
        r0 = r46;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 != 0) goto L_0x2c23;
    L_0x2c1d:
        r0 = r46;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x2e35;
    L_0x2c23:
        r0 = r46;
        r4 = r0.f38w;
        r4 = (float) r4;
        r0 = r110;
        r6 = (float) r0;
        r121 = r4 / r6;
        r0 = r46;
        r4 = r0.f38w;
        r4 = (float) r4;
        r4 = r4 / r121;
        r0 = (int) r4;
        r138 = r0;
        r0 = r46;
        r4 = r0.f37h;
        r4 = (float) r4;
        r4 = r4 / r121;
        r0 = (int) r4;
        r75 = r0;
        r0 = r75;
        r1 = r109;
        if (r0 <= r1) goto L_0x2e01;
    L_0x2c47:
        r0 = r75;
        r0 = (float) r0;
        r122 = r0;
        r75 = r109;
        r0 = r75;
        r4 = (float) r0;
        r122 = r122 / r4;
        r0 = r138;
        r4 = (float) r0;
        r4 = r4 / r122;
        r0 = (int) r4;
        r138 = r0;
    L_0x2c5b:
        if (r138 == 0) goto L_0x2c5f;
    L_0x2c5d:
        if (r75 != 0) goto L_0x2c67;
    L_0x2c5f:
        r4 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r75 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r138 = r75;
    L_0x2c67:
        r0 = r147;
        r4 = r0.type;
        r6 = 3;
        if (r4 != r6) goto L_0x2c89;
    L_0x2c6e:
        r0 = r146;
        r4 = r0.infoWidth;
        r6 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r138;
        if (r0 >= r4) goto L_0x2c89;
    L_0x2c7d:
        r0 = r146;
        r4 = r0.infoWidth;
        r6 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r138 = r4 + r6;
    L_0x2c89:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        if (r4 == 0) goto L_0x2f65;
    L_0x2c8f:
        r71 = 0;
        r62 = r146.getGroupPhotosWidth();
        r41 = 0;
    L_0x2c97:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r0 = r41;
        if (r0 >= r4) goto L_0x2e39;
    L_0x2ca5:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r41;
        r112 = r4.get(r0);
        r112 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r112;
        r0 = r112;
        r4 = r0.minY;
        if (r4 != 0) goto L_0x2e39;
    L_0x2cb9:
        r0 = r71;
        r8 = (double) r0;
        r0 = r112;
        r4 = r0.pw;
        r0 = r112;
        r6 = r0.leftSpanOffset;
        r4 = r4 + r6;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r10 = (double) r4;
        r10 = java.lang.Math.ceil(r10);
        r8 = r8 + r10;
        r0 = (int) r8;
        r71 = r0;
        r41 = r41 + 1;
        goto L_0x2c97;
    L_0x2cd9:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2cef;
    L_0x2cdf:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3f333333 float:0.7 double:5.23867711E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r110 = r0;
        r94 = r110;
        goto L_0x2af7;
    L_0x2cef:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3f333333 float:0.7 double:5.23867711E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r110 = r0;
        r94 = r110;
        goto L_0x2af7;
    L_0x2d07:
        r0 = r147;
        r4 = r0.type;
        r6 = 3;
        if (r4 != r6) goto L_0x2d3a;
    L_0x2d0e:
        r4 = 0;
        r0 = r146;
        r1 = r147;
        r0.createDocumentLayout(r4, r1);
        r146.updateSecretTimeText(r147);
        r4 = r147.needDrawBluredPreview();
        if (r4 != 0) goto L_0x2d2f;
    L_0x2d1f:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setNeedsQualityThumb(r6);
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setShouldGenerateQualityThumb(r6);
    L_0x2d2f:
        r0 = r146;
        r4 = r0.photoImage;
        r0 = r147;
        r4.setParentMessageObject(r0);
        goto L_0x2b4c;
    L_0x2d3a:
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x2d62;
    L_0x2d41:
        r4 = r147.needDrawBluredPreview();
        if (r4 != 0) goto L_0x2d57;
    L_0x2d47:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setNeedsQualityThumb(r6);
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setShouldGenerateQualityThumb(r6);
    L_0x2d57:
        r0 = r146;
        r4 = r0.photoImage;
        r0 = r147;
        r4.setParentMessageObject(r0);
        goto L_0x2b4c;
    L_0x2d62:
        r0 = r147;
        r4 = r0.type;
        r6 = 8;
        if (r4 != r6) goto L_0x2b4c;
    L_0x2d6a:
        r0 = r147;
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
        r0 = r146;
        r0.infoWidth = r4;
        r27 = new android.text.StaticLayout;
        r29 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
        r0 = r146;
        r0 = r0.infoWidth;
        r30 = r0;
        r31 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r32 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r33 = 0;
        r34 = 0;
        r28 = r5;
        r27.<init>(r28, r29, r30, r31, r32, r33, r34);
        r0 = r27;
        r1 = r146;
        r1.infoLayout = r0;
        r4 = r147.needDrawBluredPreview();
        if (r4 != 0) goto L_0x2dbc;
    L_0x2dac:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setNeedsQualityThumb(r6);
        r0 = r146;
        r4 = r0.photoImage;
        r6 = 1;
        r4.setShouldGenerateQualityThumb(r6);
    L_0x2dbc:
        r0 = r146;
        r4 = r0.photoImage;
        r0 = r147;
        r4.setParentMessageObject(r0);
        goto L_0x2b4c;
    L_0x2dc7:
        r4 = NUM; // 0x42f00000 float:120.0 double:5.548480205E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r75;
        if (r0 >= r4) goto L_0x2bda;
    L_0x2dd1:
        r4 = NUM; // 0x42f00000 float:120.0 double:5.548480205E-315;
        r75 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f44h;
        r4 = (float) r4;
        r0 = r75;
        r6 = (float) r0;
        r76 = r4 / r6;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f45w;
        r4 = (float) r4;
        r4 = r4 / r76;
        r0 = r110;
        r6 = (float) r0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x2bda;
    L_0x2df3:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.f45w;
        r4 = (float) r4;
        r4 = r4 / r76;
        r0 = (int) r4;
        r138 = r0;
        goto L_0x2bda;
    L_0x2e01:
        r4 = NUM; // 0x42f00000 float:120.0 double:5.548480205E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r75;
        if (r0 >= r4) goto L_0x2c5b;
    L_0x2e0b:
        r4 = NUM; // 0x42f00000 float:120.0 double:5.548480205E-315;
        r75 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r46;
        r4 = r0.f37h;
        r4 = (float) r4;
        r0 = r75;
        r6 = (float) r0;
        r76 = r4 / r6;
        r0 = r46;
        r4 = r0.f38w;
        r4 = (float) r4;
        r4 = r4 / r76;
        r0 = r110;
        r6 = (float) r0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x2c5b;
    L_0x2e29:
        r0 = r46;
        r4 = r0.f38w;
        r4 = (float) r4;
        r4 = r4 / r76;
        r0 = (int) r4;
        r138 = r0;
        goto L_0x2c5b;
    L_0x2e35:
        r41 = r41 + 1;
        goto L_0x2bf3;
    L_0x2e39:
        r4 = NUM; // 0x420c0000 float:35.0 double:5.47465589E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r71 - r4;
        r0 = r146;
        r0.availableTimeWidth = r4;
    L_0x2e45:
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x2e70;
    L_0x2e4c:
        r0 = r146;
        r4 = r0.availableTimeWidth;
        r8 = (double) r4;
        r4 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r6 = "00:00";
        r4 = r4.measureText(r6);
        r10 = (double) r4;
        r10 = java.lang.Math.ceil(r10);
        r4 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = (double) r4;
        r24 = r0;
        r10 = r10 + r24;
        r8 = r8 - r10;
        r4 = (int) r8;
        r0 = r146;
        r0.availableTimeWidth = r4;
    L_0x2e70:
        r146.measureTime(r147);
        r0 = r146;
        r6 = r0.timeWidth;
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x2f73;
    L_0x2e7d:
        r4 = 20;
    L_0x2e7f:
        r4 = r4 + 14;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r132 = r6 + r4;
        r0 = r138;
        r1 = r132;
        if (r0 >= r1) goto L_0x2e90;
    L_0x2e8e:
        r138 = r132;
    L_0x2e90:
        r4 = r147.isRoundVideo();
        if (r4 == 0) goto L_0x2f76;
    L_0x2e96:
        r0 = r138;
        r1 = r75;
        r75 = java.lang.Math.min(r0, r1);
        r138 = r75;
        r4 = 0;
        r0 = r146;
        r0.drawBackground = r4;
        r0 = r146;
        r4 = r0.photoImage;
        r6 = r138 / 2;
        r4.setRoundRadius(r6);
    L_0x2eae:
        r30 = 0;
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        if (r4 == 0) goto L_0x3416;
    L_0x2eb6:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.max(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r93 = r4 * r6;
        r62 = r146.getGroupPhotosWidth();
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r138 = r0;
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.minY;
        if (r4 == 0) goto L_0x2ffd;
    L_0x2ee9:
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x2ef9;
    L_0x2eef:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 1;
        if (r4 != 0) goto L_0x2f09;
    L_0x2ef9:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x2ffd;
    L_0x2eff:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x2ffd;
    L_0x2f09:
        r71 = 0;
        r60 = 0;
        r41 = 0;
    L_0x2f0f:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r0 = r41;
        if (r0 >= r4) goto L_0x2ff9;
    L_0x2f1d:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r41;
        r112 = r4.get(r0);
        r112 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r112;
        r0 = r112;
        r4 = r0.minY;
        if (r4 != 0) goto L_0x2fab;
    L_0x2f31:
        r0 = r71;
        r10 = (double) r0;
        r0 = r112;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r112;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x2fa8;
    L_0x2f4b:
        r0 = r112;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x2f5c:
        r8 = r8 + r24;
        r8 = r8 + r10;
        r0 = (int) r8;
        r71 = r0;
    L_0x2f62:
        r41 = r41 + 1;
        goto L_0x2f0f;
    L_0x2f65:
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r94 - r4;
        r0 = r146;
        r0.availableTimeWidth = r4;
        goto L_0x2e45;
    L_0x2f73:
        r4 = 0;
        goto L_0x2e7f;
    L_0x2f76:
        r4 = r147.needDrawBluredPreview();
        if (r4 == 0) goto L_0x2eae;
    L_0x2f7c:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x2f91;
    L_0x2f82:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r6 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r75 = r0;
        r138 = r75;
        goto L_0x2eae;
    L_0x2f91:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r4 = (float) r4;
        r6 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r4 = r4 * r6;
        r0 = (int) r4;
        r75 = r0;
        r138 = r75;
        goto L_0x2eae;
    L_0x2fa8:
        r8 = 0;
        goto L_0x2f5c;
    L_0x2fab:
        r0 = r112;
        r4 = r0.minY;
        r0 = r146;
        r6 = r0.currentPosition;
        r6 = r6.minY;
        if (r4 != r6) goto L_0x2fed;
    L_0x2fb7:
        r0 = r60;
        r10 = (double) r0;
        r0 = r112;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r112;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x2fea;
    L_0x2fd1:
        r0 = r112;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x2fe2:
        r8 = r8 + r24;
        r8 = r8 + r10;
        r0 = (int) r8;
        r60 = r0;
        goto L_0x2f62;
    L_0x2fea:
        r8 = 0;
        goto L_0x2fe2;
    L_0x2fed:
        r0 = r112;
        r4 = r0.minY;
        r0 = r146;
        r6 = r0.currentPosition;
        r6 = r6.minY;
        if (r4 <= r6) goto L_0x2f62;
    L_0x2ff9:
        r4 = r71 - r60;
        r138 = r138 + r4;
    L_0x2ffd:
        r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r138 = r138 - r4;
        r0 = r146;
        r4 = r0.isAvatarVisible;
        if (r4 == 0) goto L_0x3013;
    L_0x300b:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r138 = r138 - r4;
    L_0x3013:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        if (r4 == 0) goto L_0x3179;
    L_0x301b:
        r75 = 0;
        r41 = 0;
    L_0x301f:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        r4 = r4.length;
        r0 = r41;
        if (r0 >= r4) goto L_0x303f;
    L_0x302a:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        r4 = r4[r41];
        r4 = r4 * r93;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r75 = r75 + r4;
        r41 = r41 + 1;
        goto L_0x301f;
    L_0x303f:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.maxY;
        r0 = r146;
        r6 = r0.currentPosition;
        r6 = r6.minY;
        r4 = r4 - r6;
        r6 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 * r6;
        r75 = r75 + r4;
    L_0x3055:
        r0 = r138;
        r1 = r146;
        r1.backgroundWidth = r0;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r138 = r138 - r4;
        r110 = r138;
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.edge;
        if (r4 != 0) goto L_0x3075;
    L_0x306d:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r110 = r110 + r4;
    L_0x3075:
        r109 = r75;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r110 - r4;
        r30 = r30 + r4;
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 8;
        if (r4 != 0) goto L_0x309d;
    L_0x308b:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.hasSibling;
        if (r4 == 0) goto L_0x3258;
    L_0x3093:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 != 0) goto L_0x3258;
    L_0x309d:
        r0 = r146;
        r4 = r0.currentPosition;
        r0 = r146;
        r4 = r0.getAdditionalWidthForPosition(r4);
        r30 = r30 + r4;
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.messages;
        r59 = r4.size();
        r79 = 0;
    L_0x30b5:
        r0 = r79;
        r1 = r59;
        if (r0 >= r1) goto L_0x3258;
    L_0x30bb:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.messages;
        r0 = r79;
        r88 = r4.get(r0);
        r88 = (org.telegram.messenger.MessageObject) r88;
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r79;
        r119 = r4.get(r0);
        r119 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r119;
        r0 = r146;
        r4 = r0.currentPosition;
        r0 = r119;
        if (r0 == r4) goto L_0x3247;
    L_0x30df:
        r0 = r119;
        r4 = r0.flags;
        r4 = r4 & 8;
        if (r4 == 0) goto L_0x3247;
    L_0x30e7:
        r0 = r119;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r138 = r0;
        r0 = r119;
        r4 = r0.minY;
        if (r4 == 0) goto L_0x31db;
    L_0x3101:
        r4 = r147.isOutOwner();
        if (r4 == 0) goto L_0x310f;
    L_0x3107:
        r0 = r119;
        r4 = r0.flags;
        r4 = r4 & 1;
        if (r4 != 0) goto L_0x311d;
    L_0x310f:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x31db;
    L_0x3115:
        r0 = r119;
        r4 = r0.flags;
        r4 = r4 & 2;
        if (r4 == 0) goto L_0x31db;
    L_0x311d:
        r71 = 0;
        r60 = 0;
        r41 = 0;
    L_0x3123:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r4 = r4.size();
        r0 = r41;
        if (r0 >= r4) goto L_0x31d7;
    L_0x3131:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.posArray;
        r0 = r41;
        r112 = r4.get(r0);
        r112 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r112;
        r0 = r112;
        r4 = r0.minY;
        if (r4 != 0) goto L_0x318e;
    L_0x3145:
        r0 = r71;
        r10 = (double) r0;
        r0 = r112;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r112;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x318b;
    L_0x315f:
        r0 = r112;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x3170:
        r8 = r8 + r24;
        r8 = r8 + r10;
        r0 = (int) r8;
        r71 = r0;
    L_0x3176:
        r41 = r41 + 1;
        goto L_0x3123;
    L_0x3179:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.ph;
        r4 = r4 * r93;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r75 = r0;
        goto L_0x3055;
    L_0x318b:
        r8 = 0;
        goto L_0x3170;
    L_0x318e:
        r0 = r112;
        r4 = r0.minY;
        r0 = r119;
        r6 = r0.minY;
        if (r4 != r6) goto L_0x31cd;
    L_0x3198:
        r0 = r60;
        r10 = (double) r0;
        r0 = r112;
        r4 = r0.pw;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r24 = java.lang.Math.ceil(r8);
        r0 = r112;
        r4 = r0.leftSpanOffset;
        if (r4 == 0) goto L_0x31ca;
    L_0x31b2:
        r0 = r112;
        r4 = r0.leftSpanOffset;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r0 = r62;
        r6 = (float) r0;
        r4 = r4 * r6;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
    L_0x31c3:
        r8 = r8 + r24;
        r8 = r8 + r10;
        r0 = (int) r8;
        r60 = r0;
        goto L_0x3176;
    L_0x31ca:
        r8 = 0;
        goto L_0x31c3;
    L_0x31cd:
        r0 = r112;
        r4 = r0.minY;
        r0 = r119;
        r6 = r0.minY;
        if (r4 <= r6) goto L_0x3176;
    L_0x31d7:
        r4 = r71 - r60;
        r138 = r138 + r4;
    L_0x31db:
        r4 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r138 = r138 - r4;
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x3205;
    L_0x31e9:
        r4 = r88.isOutOwner();
        if (r4 != 0) goto L_0x3205;
    L_0x31ef:
        r4 = r88.needDrawAvatar();
        if (r4 == 0) goto L_0x3205;
    L_0x31f5:
        if (r119 == 0) goto L_0x31fd;
    L_0x31f7:
        r0 = r119;
        r4 = r0.edge;
        if (r4 == 0) goto L_0x3205;
    L_0x31fd:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r138 = r138 - r4;
    L_0x3205:
        r0 = r146;
        r1 = r119;
        r4 = r0.getAdditionalWidthForPosition(r1);
        r138 = r138 + r4;
        r0 = r119;
        r4 = r0.edge;
        if (r4 != 0) goto L_0x321d;
    L_0x3215:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r138 = r138 + r4;
    L_0x321d:
        r30 = r30 + r138;
        r0 = r119;
        r4 = r0.minX;
        r0 = r146;
        r6 = r0.currentPosition;
        r6 = r6.minX;
        if (r4 < r6) goto L_0x323d;
    L_0x322b:
        r0 = r146;
        r4 = r0.currentMessagesGroup;
        r4 = r4.hasSibling;
        if (r4 == 0) goto L_0x3247;
    L_0x3233:
        r0 = r119;
        r4 = r0.minY;
        r0 = r119;
        r6 = r0.maxY;
        if (r4 == r6) goto L_0x3247;
    L_0x323d:
        r0 = r146;
        r4 = r0.captionOffsetX;
        r4 = r4 - r138;
        r0 = r146;
        r0.captionOffsetX = r4;
    L_0x3247:
        r0 = r88;
        r4 = r0.caption;
        if (r4 == 0) goto L_0x3412;
    L_0x324d:
        r0 = r146;
        r4 = r0.currentCaption;
        if (r4 == 0) goto L_0x340a;
    L_0x3253:
        r4 = 0;
        r0 = r146;
        r0.currentCaption = r4;
    L_0x3258:
        r0 = r146;
        r4 = r0.currentCaption;
        if (r4 == 0) goto L_0x3323;
    L_0x325e:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x346a }
        r6 = 24;	 Catch:{ Exception -> 0x346a }
        if (r4 < r6) goto L_0x344d;	 Catch:{ Exception -> 0x346a }
    L_0x3264:
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r4 = r0.currentCaption;	 Catch:{ Exception -> 0x346a }
        r6 = 0;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r8 = r0.currentCaption;	 Catch:{ Exception -> 0x346a }
        r8 = r8.length();	 Catch:{ Exception -> 0x346a }
        r9 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x346a }
        r0 = r30;	 Catch:{ Exception -> 0x346a }
        r4 = android.text.StaticLayout.Builder.obtain(r4, r6, r8, r9, r0);	 Catch:{ Exception -> 0x346a }
        r6 = 1;	 Catch:{ Exception -> 0x346a }
        r4 = r4.setBreakStrategy(r6);	 Catch:{ Exception -> 0x346a }
        r6 = 0;	 Catch:{ Exception -> 0x346a }
        r4 = r4.setHyphenationFrequency(r6);	 Catch:{ Exception -> 0x346a }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x346a }
        r4 = r4.setAlignment(r6);	 Catch:{ Exception -> 0x346a }
        r4 = r4.build();	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x346a }
    L_0x3291:
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x346a }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x346a }
        if (r4 <= 0) goto L_0x3323;	 Catch:{ Exception -> 0x346a }
    L_0x329b:
        r0 = r30;	 Catch:{ Exception -> 0x346a }
        r1 = r146;	 Catch:{ Exception -> 0x346a }
        r1.captionWidth = r0;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x346a }
        r4 = r4.getHeight();	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r0.captionHeight = r4;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r4 = r0.captionHeight;	 Catch:{ Exception -> 0x346a }
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;	 Catch:{ Exception -> 0x346a }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x346a }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r0.addedCaptionHeight = r4;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r4 = r0.currentPosition;	 Catch:{ Exception -> 0x346a }
        if (r4 == 0) goto L_0x32cc;	 Catch:{ Exception -> 0x346a }
    L_0x32c2:
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r4 = r0.currentPosition;	 Catch:{ Exception -> 0x346a }
        r4 = r4.flags;	 Catch:{ Exception -> 0x346a }
        r4 = r4 & 8;	 Catch:{ Exception -> 0x346a }
        if (r4 == 0) goto L_0x3470;	 Catch:{ Exception -> 0x346a }
    L_0x32cc:
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r4 = r0.addedCaptionHeight;	 Catch:{ Exception -> 0x346a }
        r43 = r43 + r4;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x346a }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x346a }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x346a }
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r6 = r0.captionLayout;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r8 = r0.captionLayout;	 Catch:{ Exception -> 0x346a }
        r8 = r8.getLineCount();	 Catch:{ Exception -> 0x346a }
        r8 = r8 + -1;	 Catch:{ Exception -> 0x346a }
        r6 = r6.getLineLeft(r8);	 Catch:{ Exception -> 0x346a }
        r83 = r4 + r6;	 Catch:{ Exception -> 0x346a }
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x346a }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x346a }
        r4 = r4 + r30;	 Catch:{ Exception -> 0x346a }
        r4 = (float) r4;	 Catch:{ Exception -> 0x346a }
        r4 = r4 - r83;	 Catch:{ Exception -> 0x346a }
        r0 = r132;	 Catch:{ Exception -> 0x346a }
        r6 = (float) r0;	 Catch:{ Exception -> 0x346a }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ Exception -> 0x346a }
        if (r4 >= 0) goto L_0x3323;	 Catch:{ Exception -> 0x346a }
    L_0x330a:
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;	 Catch:{ Exception -> 0x346a }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x346a }
        r43 = r43 + r4;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r4 = r0.addedCaptionHeight;	 Catch:{ Exception -> 0x346a }
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;	 Catch:{ Exception -> 0x346a }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x346a }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r0.addedCaptionHeight = r4;	 Catch:{ Exception -> 0x346a }
        r57 = 1;
    L_0x3323:
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r0 = r138;
        r10 = (float) r0;
        r11 = org.telegram.messenger.AndroidUtilities.density;
        r10 = r10 / r11;
        r10 = (int) r10;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r9 = 1;
        r0 = r75;
        r10 = (float) r0;
        r11 = org.telegram.messenger.AndroidUtilities.density;
        r10 = r10 / r11;
        r10 = (int) r10;
        r10 = java.lang.Integer.valueOf(r10);
        r8[r9] = r10;
        r4 = java.lang.String.format(r4, r6, r8);
        r0 = r146;
        r0.currentPhotoFilterThumb = r4;
        r0 = r146;
        r0.currentPhotoFilter = r4;
        r0 = r147;
        r4 = r0.photoThumbs;
        if (r4 == 0) goto L_0x3364;
    L_0x3359:
        r0 = r147;
        r4 = r0.photoThumbs;
        r4 = r4.size();
        r6 = 1;
        if (r4 > r6) goto L_0x337a;
    L_0x3364:
        r0 = r147;
        r4 = r0.type;
        r6 = 3;
        if (r4 == r6) goto L_0x337a;
    L_0x336b:
        r0 = r147;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x337a;
    L_0x3373:
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x33b8;
    L_0x337a:
        r4 = r147.needDrawBluredPreview();
        if (r4 == 0) goto L_0x3477;
    L_0x3380:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r146;
        r6 = r0.currentPhotoFilter;
        r4 = r4.append(r6);
        r6 = "_b2";
        r4 = r4.append(r6);
        r4 = r4.toString();
        r0 = r146;
        r0.currentPhotoFilter = r4;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r146;
        r6 = r0.currentPhotoFilterThumb;
        r4 = r4.append(r6);
        r6 = "_b2";
        r4 = r4.append(r6);
        r4 = r4.toString();
        r0 = r146;
        r0.currentPhotoFilterThumb = r4;
    L_0x33b8:
        r101 = 0;
        r0 = r147;
        r4 = r0.type;
        r6 = 3;
        if (r4 == r6) goto L_0x33d0;
    L_0x33c1:
        r0 = r147;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x33d0;
    L_0x33c9:
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x33d2;
    L_0x33d0:
        r101 = 1;
    L_0x33d2:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x33e9;
    L_0x33d8:
        if (r101 != 0) goto L_0x33e9;
    L_0x33da:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r4 = r4.size;
        if (r4 != 0) goto L_0x33e9;
    L_0x33e2:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r6 = -1;
        r4.size = r6;
    L_0x33e9:
        r0 = r147;
        r4 = r0.type;
        r6 = 1;
        if (r4 != r6) goto L_0x3580;
    L_0x33f0:
        r0 = r147;
        r4 = r0.useCustomPhoto;
        if (r4 == 0) goto L_0x3495;
    L_0x33f6:
        r0 = r146;
        r4 = r0.photoImage;
        r6 = r146.getResources();
        r8 = NUM; // 0x7f0701f1 float:1.7945586E38 double:1.0529357486E-314;
        r6 = r6.getDrawable(r8);
        r4.setImageBitmap(r6);
        goto L_0x233f;
    L_0x340a:
        r0 = r88;
        r4 = r0.caption;
        r0 = r146;
        r0.currentCaption = r4;
    L_0x3412:
        r79 = r79 + 1;
        goto L_0x30b5;
    L_0x3416:
        r110 = r138;
        r109 = r75;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r138;
        r0 = r146;
        r0.backgroundWidth = r4;
        r0 = r146;
        r4 = r0.mediaBackground;
        if (r4 != 0) goto L_0x343b;
    L_0x342c:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.backgroundWidth = r4;
    L_0x343b:
        r0 = r147;
        r4 = r0.caption;
        r0 = r146;
        r0.currentCaption = r4;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r30 = r110 - r4;
        goto L_0x3258;
    L_0x344d:
        r27 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x346a }
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r0 = r0.currentCaption;	 Catch:{ Exception -> 0x346a }
        r28 = r0;	 Catch:{ Exception -> 0x346a }
        r29 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x346a }
        r31 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x346a }
        r32 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x346a }
        r33 = 0;	 Catch:{ Exception -> 0x346a }
        r34 = 0;	 Catch:{ Exception -> 0x346a }
        r27.<init>(r28, r29, r30, r31, r32, r33, r34);	 Catch:{ Exception -> 0x346a }
        r0 = r27;	 Catch:{ Exception -> 0x346a }
        r1 = r146;	 Catch:{ Exception -> 0x346a }
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x346a }
        goto L_0x3291;
    L_0x346a:
        r69 = move-exception;
        org.telegram.messenger.FileLog.m3e(r69);
        goto L_0x3323;
    L_0x3470:
        r4 = 0;
        r0 = r146;	 Catch:{ Exception -> 0x346a }
        r0.captionLayout = r4;	 Catch:{ Exception -> 0x346a }
        goto L_0x3323;
    L_0x3477:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r146;
        r6 = r0.currentPhotoFilterThumb;
        r4 = r4.append(r6);
        r6 = "_b";
        r4 = r4.append(r6);
        r4 = r4.toString();
        r0 = r146;
        r0.currentPhotoFilterThumb = r4;
        goto L_0x33b8;
    L_0x3495:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x3574;
    L_0x349b:
        r108 = 1;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r70 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r0 = r147;
        r4 = r0.mediaExists;
        if (r4 == 0) goto L_0x351b;
    L_0x34ab:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r146;
        r4.removeLoadingFileObserver(r0);
    L_0x34b8:
        if (r108 != 0) goto L_0x34dc;
    L_0x34ba:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r146;
        r6 = r0.currentMessageObject;
        r4 = r4.canDownloadMedia(r6);
        if (r4 != 0) goto L_0x34dc;
    L_0x34cc:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r70;
        r4 = r4.isLoadingFile(r0);
        if (r4 == 0) goto L_0x352d;
    L_0x34dc:
        r0 = r146;
        r0 = r0.photoImage;
        r31 = r0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r32 = r0;
        r0 = r146;
        r0 = r0.currentPhotoFilter;
        r33 = r0;
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x351e;
    L_0x34f6:
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        r0 = r4.location;
        r34 = r0;
    L_0x34fe:
        r0 = r146;
        r0 = r0.currentPhotoFilterThumb;
        r35 = r0;
        if (r101 == 0) goto L_0x3521;
    L_0x3506:
        r36 = 0;
    L_0x3508:
        r37 = 0;
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.shouldEncryptPhotoOrVideo();
        if (r4 == 0) goto L_0x352a;
    L_0x3514:
        r38 = 2;
    L_0x3516:
        r31.setImage(r32, r33, r34, r35, r36, r37, r38);
        goto L_0x233f;
    L_0x351b:
        r108 = 0;
        goto L_0x34b8;
    L_0x351e:
        r34 = 0;
        goto L_0x34fe;
    L_0x3521:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.size;
        r36 = r0;
        goto L_0x3508;
    L_0x352a:
        r38 = 0;
        goto L_0x3516;
    L_0x352d:
        r4 = 1;
        r0 = r146;
        r0.photoNotSet = r4;
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x3568;
    L_0x3538:
        r0 = r146;
        r0 = r0.photoImage;
        r31 = r0;
        r32 = 0;
        r33 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObjectThumb;
        r0 = r4.location;
        r34 = r0;
        r0 = r146;
        r0 = r0.currentPhotoFilterThumb;
        r35 = r0;
        r36 = 0;
        r37 = 0;
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.shouldEncryptPhotoOrVideo();
        if (r4 == 0) goto L_0x3565;
    L_0x355e:
        r38 = 2;
    L_0x3560:
        r31.setImage(r32, r33, r34, r35, r36, r37, r38);
        goto L_0x233f;
    L_0x3565:
        r38 = 0;
        goto L_0x3560;
    L_0x3568:
        r0 = r146;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.Drawable) r4;
        r6.setImageBitmap(r4);
        goto L_0x233f;
    L_0x3574:
        r0 = r146;
        r6 = r0.photoImage;
        r4 = 0;
        r4 = (android.graphics.drawable.BitmapDrawable) r4;
        r6.setImageBitmap(r4);
        goto L_0x233f;
    L_0x3580:
        r0 = r147;
        r4 = r0.type;
        r6 = 8;
        if (r4 == r6) goto L_0x358f;
    L_0x3588:
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x36c6;
    L_0x358f:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r70 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r87 = 0;
        r0 = r147;
        r4 = r0.attachPathExists;
        if (r4 == 0) goto L_0x362a;
    L_0x35a3:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r146;
        r4.removeLoadingFileObserver(r0);
        r87 = 1;
    L_0x35b2:
        r49 = 0;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = org.telegram.messenger.MessageObject.isNewGifDocument(r4);
        if (r4 == 0) goto L_0x3633;
    L_0x35c2:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r146;
        r6 = r0.currentMessageObject;
        r49 = r4.canDownloadMedia(r6);
    L_0x35d2:
        r4 = r147.isSending();
        if (r4 != 0) goto L_0x3695;
    L_0x35d8:
        r4 = r147.isEditing();
        if (r4 != 0) goto L_0x3695;
    L_0x35de:
        if (r87 != 0) goto L_0x35f2;
    L_0x35e0:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r0 = r70;
        r4 = r4.isLoadingFile(r0);
        if (r4 != 0) goto L_0x35f2;
    L_0x35f0:
        if (r49 == 0) goto L_0x3695;
    L_0x35f2:
        r4 = 1;
        r0 = r87;
        if (r0 != r4) goto L_0x3657;
    L_0x35f7:
        r0 = r146;
        r0 = r0.photoImage;
        r31 = r0;
        r32 = 0;
        r4 = r147.isSendError();
        if (r4 == 0) goto L_0x364b;
    L_0x3605:
        r33 = 0;
    L_0x3607:
        r34 = 0;
        r35 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x3654;
    L_0x3611:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r36 = r0;
    L_0x3619:
        r0 = r146;
        r0 = r0.currentPhotoFilterThumb;
        r37 = r0;
        r38 = 0;
        r39 = 0;
        r40 = 0;
        r31.setImage(r32, r33, r34, r35, r36, r37, r38, r39, r40);
        goto L_0x233f;
    L_0x362a:
        r0 = r147;
        r4 = r0.mediaExists;
        if (r4 == 0) goto L_0x35b2;
    L_0x3630:
        r87 = 2;
        goto L_0x35b2;
    L_0x3633:
        r0 = r147;
        r4 = r0.type;
        r6 = 5;
        if (r4 != r6) goto L_0x35d2;
    L_0x363a:
        r0 = r146;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r0 = r146;
        r6 = r0.currentMessageObject;
        r49 = r4.canDownloadMedia(r6);
        goto L_0x35d2;
    L_0x364b:
        r0 = r147;
        r4 = r0.messageOwner;
        r0 = r4.attachPath;
        r33 = r0;
        goto L_0x3607;
    L_0x3654:
        r36 = 0;
        goto L_0x3619;
    L_0x3657:
        r0 = r146;
        r0 = r0.photoImage;
        r31 = r0;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r0 = r4.document;
        r32 = r0;
        r33 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x3692;
    L_0x366f:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r34 = r0;
    L_0x3677:
        r0 = r146;
        r0 = r0.currentPhotoFilterThumb;
        r35 = r0;
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r0 = r4.size;
        r36 = r0;
        r37 = 0;
        r38 = 0;
        r31.setImage(r32, r33, r34, r35, r36, r37, r38);
        goto L_0x233f;
    L_0x3692:
        r34 = 0;
        goto L_0x3677;
    L_0x3695:
        r4 = 1;
        r0 = r146;
        r0.photoNotSet = r4;
        r0 = r146;
        r0 = r0.photoImage;
        r31 = r0;
        r32 = 0;
        r33 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x36c3;
    L_0x36aa:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r34 = r0;
    L_0x36b2:
        r0 = r146;
        r0 = r0.currentPhotoFilterThumb;
        r35 = r0;
        r36 = 0;
        r37 = 0;
        r38 = 0;
        r31.setImage(r32, r33, r34, r35, r36, r37, r38);
        goto L_0x233f;
    L_0x36c3:
        r34 = 0;
        goto L_0x36b2;
    L_0x36c6:
        r0 = r146;
        r0 = r0.photoImage;
        r31 = r0;
        r32 = 0;
        r33 = 0;
        r0 = r146;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x36f9;
    L_0x36d6:
        r0 = r146;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r34 = r0;
    L_0x36de:
        r0 = r146;
        r0 = r0.currentPhotoFilterThumb;
        r35 = r0;
        r36 = 0;
        r37 = 0;
        r0 = r146;
        r4 = r0.currentMessageObject;
        r4 = r4.shouldEncryptPhotoOrVideo();
        if (r4 == 0) goto L_0x36fc;
    L_0x36f2:
        r38 = 2;
    L_0x36f4:
        r31.setImage(r32, r33, r34, r35, r36, r37, r38);
        goto L_0x233f;
    L_0x36f9:
        r34 = 0;
        goto L_0x36de;
    L_0x36fc:
        r38 = 0;
        goto L_0x36f4;
    L_0x36ff:
        r0 = r146;
        r4 = r0.drawNameLayout;
        if (r4 == 0) goto L_0x2372;
    L_0x3705:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.reply_to_msg_id;
        if (r4 != 0) goto L_0x2372;
    L_0x370d:
        r0 = r146;
        r4 = r0.namesOffset;
        r6 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.namesOffset = r4;
        goto L_0x2372;
    L_0x371e:
        r27 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x373b }
        r0 = r147;	 Catch:{ Exception -> 0x373b }
        r0 = r0.caption;	 Catch:{ Exception -> 0x373b }
        r28 = r0;	 Catch:{ Exception -> 0x373b }
        r29 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x373b }
        r31 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x373b }
        r32 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x373b }
        r33 = 0;	 Catch:{ Exception -> 0x373b }
        r34 = 0;	 Catch:{ Exception -> 0x373b }
        r27.<init>(r28, r29, r30, r31, r32, r33, r34);	 Catch:{ Exception -> 0x373b }
        r0 = r27;	 Catch:{ Exception -> 0x373b }
        r1 = r146;	 Catch:{ Exception -> 0x373b }
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x373b }
        goto L_0x113d;
    L_0x373b:
        r69 = move-exception;
        org.telegram.messenger.FileLog.m3e(r69);
        goto L_0x11d7;
    L_0x3741:
        r4 = 0;
        goto L_0x115d;
    L_0x3744:
        r0 = r146;
        r4 = r0.widthBeforeNewTimeLine;
        r6 = -1;
        if (r4 == r6) goto L_0x11d7;
    L_0x374b:
        r0 = r146;
        r4 = r0.availableTimeWidth;
        r0 = r146;
        r6 = r0.widthBeforeNewTimeLine;
        r4 = r4 - r6;
        r0 = r146;
        r6 = r0.timeWidth;
        if (r4 >= r6) goto L_0x11d7;
    L_0x375a:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        goto L_0x11d7;
    L_0x376b:
        r4 = 0;
        goto L_0x1268;
    L_0x376e:
        r69 = move-exception;
        org.telegram.messenger.FileLog.m3e(r69);
        goto L_0x1292;
    L_0x3774:
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x3785 }
        r0 = r85;	 Catch:{ Exception -> 0x3785 }
        r6 = -r0;	 Catch:{ Exception -> 0x3785 }
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x3785 }
        r0 = r146;	 Catch:{ Exception -> 0x3785 }
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x3785 }
        goto L_0x1328;
    L_0x3785:
        r69 = move-exception;
        org.telegram.messenger.FileLog.m3e(r69);
    L_0x3789:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.totalHeight = r4;
        if (r57 == 0) goto L_0x37bd;
    L_0x379a:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.totalHeight = r4;
        r4 = 2;
        r0 = r57;
        if (r0 != r4) goto L_0x37bd;
    L_0x37ae:
        r0 = r146;
        r4 = r0.captionHeight;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.captionHeight = r4;
    L_0x37bd:
        r0 = r146;
        r4 = r0.botButtons;
        r4.clear();
        if (r98 == 0) goto L_0x37d9;
    L_0x37c6:
        r0 = r146;
        r4 = r0.botButtonsByData;
        r4.clear();
        r0 = r146;
        r4 = r0.botButtonsByPosition;
        r4.clear();
        r4 = 0;
        r0 = r146;
        r0.botButtonsLayout = r4;
    L_0x37d9:
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 != 0) goto L_0x3ad1;
    L_0x37df:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.reply_markup;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
        if (r4 == 0) goto L_0x3ad1;
    L_0x37e9:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.reply_markup;
        r4 = r4.rows;
        r120 = r4.size();
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 * r120;
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r146;
        r0.keyboardHeight = r4;
        r0 = r146;
        r0.substractBackgroundHeight = r4;
        r0 = r146;
        r6 = r0.backgroundWidth;
        r0 = r146;
        r4 = r0.mediaBackground;
        if (r4 == 0) goto L_0x38cc;
    L_0x3816:
        r4 = 0;
    L_0x3817:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r6 - r4;
        r0 = r146;
        r0.widthForButtons = r4;
        r72 = 0;
        r0 = r147;
        r4 = r0.wantedBotKeyboardWidth;
        r0 = r146;
        r6 = r0.widthForButtons;
        if (r4 <= r6) goto L_0x386a;
    L_0x382d:
        r0 = r146;
        r4 = r0.isChat;
        if (r4 == 0) goto L_0x38d0;
    L_0x3833:
        r4 = r147.needDrawAvatar();
        if (r4 == 0) goto L_0x38d0;
    L_0x3839:
        r4 = r147.isOutOwner();
        if (r4 != 0) goto L_0x38d0;
    L_0x383f:
        r4 = NUM; // 0x42780000 float:62.0 double:5.5096253E-315;
    L_0x3841:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = -r4;
        r90 = r0;
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x38d4;
    L_0x384e:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r90 = r90 + r4;
    L_0x3854:
        r0 = r146;
        r4 = r0.backgroundWidth;
        r0 = r147;
        r6 = r0.wantedBotKeyboardWidth;
        r0 = r90;
        r6 = java.lang.Math.min(r6, r0);
        r4 = java.lang.Math.max(r4, r6);
        r0 = r146;
        r0.widthForButtons = r4;
    L_0x386a:
        r91 = 0;
        r104 = new java.util.HashMap;
        r0 = r146;
        r4 = r0.botButtonsByData;
        r0 = r104;
        r0.<init>(r4);
        r0 = r147;
        r4 = r0.botButtonsLayout;
        if (r4 == 0) goto L_0x38eb;
    L_0x387d:
        r0 = r146;
        r4 = r0.botButtonsLayout;
        if (r4 == 0) goto L_0x38eb;
    L_0x3883:
        r0 = r146;
        r4 = r0.botButtonsLayout;
        r0 = r147;
        r6 = r0.botButtonsLayout;
        r6 = r6.toString();
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x38eb;
    L_0x3895:
        r105 = new java.util.HashMap;
        r0 = r146;
        r4 = r0.botButtonsByPosition;
        r0 = r105;
        r0.<init>(r4);
    L_0x38a0:
        r0 = r146;
        r4 = r0.botButtonsByData;
        r4.clear();
        r41 = 0;
    L_0x38a9:
        r0 = r41;
        r1 = r120;
        if (r0 >= r1) goto L_0x3a7f;
    L_0x38af:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.reply_markup;
        r4 = r4.rows;
        r0 = r41;
        r118 = r4.get(r0);
        r118 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r118;
        r0 = r118;
        r4 = r0.buttons;
        r55 = r4.size();
        if (r55 != 0) goto L_0x3900;
    L_0x38c9:
        r41 = r41 + 1;
        goto L_0x38a9;
    L_0x38cc:
        r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        goto L_0x3817;
    L_0x38d0:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        goto L_0x3841;
    L_0x38d4:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.y;
        r4 = java.lang.Math.min(r4, r6);
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r90 = r90 + r4;
        goto L_0x3854;
    L_0x38eb:
        r0 = r147;
        r4 = r0.botButtonsLayout;
        if (r4 == 0) goto L_0x38fd;
    L_0x38f1:
        r0 = r147;
        r4 = r0.botButtonsLayout;
        r4 = r4.toString();
        r0 = r146;
        r0.botButtonsLayout = r4;
    L_0x38fd:
        r105 = 0;
        goto L_0x38a0;
    L_0x3900:
        r0 = r146;
        r4 = r0.widthForButtons;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r8 = r55 + -1;
        r6 = r6 * r8;
        r4 = r4 - r6;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r54 = r4 / r55;
        r50 = 0;
    L_0x3919:
        r0 = r118;
        r4 = r0.buttons;
        r4 = r4.size();
        r0 = r50;
        if (r0 >= r4) goto L_0x38c9;
    L_0x3925:
        r53 = new org.telegram.ui.Cells.ChatMessageCell$BotButton;
        r4 = 0;
        r0 = r53;
        r1 = r146;
        r0.<init>();
        r0 = r118;
        r4 = r0.buttons;
        r0 = r50;
        r4 = r4.get(r0);
        r4 = (org.telegram.tgnet.TLRPC.KeyboardButton) r4;
        r0 = r53;
        r0.button = r4;
        r4 = r53.button;
        r4 = r4.data;
        r82 = org.telegram.messenger.Utilities.bytesToHex(r4);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r41;
        r4 = r4.append(r0);
        r6 = "";
        r4 = r4.append(r6);
        r0 = r50;
        r4 = r4.append(r0);
        r112 = r4.toString();
        if (r105 == 0) goto L_0x3a3c;
    L_0x3968:
        r0 = r105;
        r1 = r112;
        r103 = r0.get(r1);
        r103 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r103;
    L_0x3972:
        if (r103 == 0) goto L_0x3a48;
    L_0x3974:
        r4 = r103.progressAlpha;
        r0 = r53;
        r0.progressAlpha = r4;
        r4 = r103.angle;
        r0 = r53;
        r0.angle = r4;
        r8 = r103.lastUpdateTime;
        r0 = r53;
        r0.lastUpdateTime = r8;
    L_0x398f:
        r0 = r146;
        r4 = r0.botButtonsByData;
        r0 = r82;
        r1 = r53;
        r4.put(r0, r1);
        r0 = r146;
        r4 = r0.botButtonsByPosition;
        r0 = r112;
        r1 = r53;
        r4.put(r0, r1);
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r54;
        r4 = r4 * r50;
        r0 = r53;
        r0.f9x = r4;
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 * r41;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r0 = r53;
        r0.f10y = r4;
        r53.width = r54;
        r4 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r53;
        r0.height = r4;
        r4 = r53.button;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r4 == 0) goto L_0x3a53;
    L_0x39de:
        r0 = r147;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x3a53;
    L_0x39ea:
        r4 = "PaymentReceipt";
        r6 = NUM; // 0x7f0c0579 float:1.8612034E38 double:1.0530980906E-314;
        r32 = org.telegram.messenger.LocaleController.getString(r4, r6);
    L_0x39f4:
        r31 = new android.text.StaticLayout;
        r33 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r34 = r54 - r4;
        r35 = android.text.Layout.Alignment.ALIGN_CENTER;
        r36 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r37 = 0;
        r38 = 0;
        r31.<init>(r32, r33, r34, r35, r36, r37, r38);
        r0 = r53;
        r1 = r31;
        r0.title = r1;
        r0 = r146;
        r4 = r0.botButtons;
        r0 = r53;
        r4.add(r0);
        r0 = r118;
        r4 = r0.buttons;
        r4 = r4.size();
        r4 = r4 + -1;
        r0 = r50;
        if (r0 != r4) goto L_0x3a38;
    L_0x3a29:
        r4 = r53.f9x;
        r6 = r53.width;
        r4 = r4 + r6;
        r0 = r91;
        r91 = java.lang.Math.max(r0, r4);
    L_0x3a38:
        r50 = r50 + 1;
        goto L_0x3919;
    L_0x3a3c:
        r0 = r104;
        r1 = r82;
        r103 = r0.get(r1);
        r103 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r103;
        goto L_0x3972;
    L_0x3a48:
        r8 = java.lang.System.currentTimeMillis();
        r0 = r53;
        r0.lastUpdateTime = r8;
        goto L_0x398f;
    L_0x3a53:
        r4 = r53.button;
        r4 = r4.text;
        r6 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
        r6 = r6.getFontMetricsInt();
        r8 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r9 = 0;
        r32 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r8, r9);
        r4 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r54 - r6;
        r6 = (float) r6;
        r8 = android.text.TextUtils.TruncateAt.END;
        r0 = r32;
        r32 = android.text.TextUtils.ellipsize(r0, r4, r6, r8);
        goto L_0x39f4;
    L_0x3a7f:
        r0 = r91;
        r1 = r146;
        r1.widthForButtons = r0;
    L_0x3a85:
        r0 = r146;
        r4 = r0.drawPinnedBottom;
        if (r4 == 0) goto L_0x3adc;
    L_0x3a8b:
        r0 = r146;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x3adc;
    L_0x3a91:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.totalHeight = r4;
    L_0x3aa0:
        r0 = r147;
        r4 = r0.type;
        r6 = 13;
        if (r4 != r6) goto L_0x3abe;
    L_0x3aa8:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x428c0000 float:70.0 double:5.51610112E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        if (r4 >= r6) goto L_0x3abe;
    L_0x3ab4:
        r4 = NUM; // 0x428c0000 float:70.0 double:5.51610112E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r146;
        r0.totalHeight = r4;
    L_0x3abe:
        r146.updateWaveform();
        if (r63 == 0) goto L_0x3b1c;
    L_0x3ac3:
        r0 = r147;
        r4 = r0.cancelEditing;
        if (r4 != 0) goto L_0x3b1c;
    L_0x3ac9:
        r4 = 1;
    L_0x3aca:
        r6 = 1;
        r0 = r146;
        r0.updateButtonState(r4, r6);
        return;
    L_0x3ad1:
        r4 = 0;
        r0 = r146;
        r0.substractBackgroundHeight = r4;
        r4 = 0;
        r0 = r146;
        r0.keyboardHeight = r4;
        goto L_0x3a85;
    L_0x3adc:
        r0 = r146;
        r4 = r0.drawPinnedBottom;
        if (r4 == 0) goto L_0x3af2;
    L_0x3ae2:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.totalHeight = r4;
        goto L_0x3aa0;
    L_0x3af2:
        r0 = r146;
        r4 = r0.drawPinnedTop;
        if (r4 == 0) goto L_0x3aa0;
    L_0x3af8:
        r0 = r146;
        r4 = r0.pinnedBottom;
        if (r4 == 0) goto L_0x3aa0;
    L_0x3afe:
        r0 = r146;
        r4 = r0.currentPosition;
        if (r4 == 0) goto L_0x3aa0;
    L_0x3b04:
        r0 = r146;
        r4 = r0.currentPosition;
        r4 = r4.siblingHeights;
        if (r4 != 0) goto L_0x3aa0;
    L_0x3b0c:
        r0 = r146;
        r4 = r0.totalHeight;
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r0 = r146;
        r0.totalHeight = r4;
        goto L_0x3aa0;
    L_0x3b1c:
        r4 = 0;
        goto L_0x3aca;
    L_0x3b1e:
        r69 = move-exception;
        goto L_0x0d5b;
    L_0x3b21:
        r69 = move-exception;
        r12 = r117;
        goto L_0x0ba3;
    L_0x3b26:
        r96 = r26;
        goto L_0x1ea0;
    L_0x3b2a:
        r14 = r139;
        goto L_0x189d;
    L_0x3b2e:
        r14 = r139;
        goto L_0x0dc6;
    L_0x3b32:
        r12 = r117;
        goto L_0x0c4d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageObject(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, boolean, boolean):void");
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
            this.instantWidth = AndroidUtilities.dp(33.0f);
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
            int mWidth = this.backgroundWidth - AndroidUtilities.dp(75.0f);
            this.instantViewLayout = new StaticLayout(TextUtils.ellipsize(str, Theme.chat_instantViewPaint, (float) mWidth, TruncateAt.END), Theme.chat_instantViewPaint, mWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
                this.deleteProgressRect.set((float) (this.buttonX + AndroidUtilities.dp(3.0f)), (float) (this.buttonY + AndroidUtilities.dp(3.0f)), (float) (this.buttonX + AndroidUtilities.dp(45.0f)), (float) (this.buttonY + AndroidUtilities.dp(45.0f)));
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
        int a;
        int startY;
        int linkX;
        int linkPreviewY;
        int x;
        int y;
        int instantY;
        Paint backPaint;
        int x1;
        int y1;
        RadialProgress radialProgress;
        String str;
        if (this.needNewVisiblePart && this.currentMessageObject.type == 0) {
            getLocalVisibleRect(this.scrollRect);
            setVisiblePart(this.scrollRect.top, this.scrollRect.bottom - this.scrollRect.top);
            this.needNewVisiblePart = false;
        }
        this.forceNotDrawTime = this.currentMessagesGroup != null;
        ImageReceiver imageReceiver = this.photoImage;
        int i = isDrawSelectedBackground() ? this.currentPosition != null ? 2 : 1 : 0;
        imageReceiver.setPressed(i);
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
                this.textX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
            } else {
                int i2 = this.currentBackgroundDrawable.getBounds().left;
                float f = (this.mediaBackground || !this.drawPinnedBottom) ? 17.0f : 11.0f;
                this.textX = AndroidUtilities.dp(f) + i2;
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
                            FileLog.m3e(e);
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
                    Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                    canvas.drawRect((float) linkX, (float) (linkPreviewY - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + linkX), (float) ((this.linkPreviewHeight + linkPreviewY) + AndroidUtilities.dp(3.0f)), Theme.chat_replyLinePaint);
                }
                if (this.siteNameLayout != null) {
                    Theme.chat_replyNamePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
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
                if (this.drawPhotoImage && this.drawInstantView) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.dp(2.0f);
                    }
                    this.photoImage.setImageCoords(AndroidUtilities.dp(10.0f) + linkX, linkPreviewY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    if (this.drawImageButton) {
                        size = AndroidUtilities.dp(48.0f);
                        this.buttonX = (int) (((float) this.photoImage.getImageX()) + (((float) (this.photoImage.getImageWidth() - size)) / 2.0f));
                        this.buttonY = (int) (((float) this.photoImage.getImageY()) + (((float) (this.photoImage.getImageHeight() - size)) / 2.0f));
                        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                    }
                    imageDrawn = this.photoImage.draw(canvas);
                    linkPreviewY += this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0f);
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
                            i = -AndroidUtilities.dp(6.3f);
                        } else {
                            i = AndroidUtilities.dp(10.0f);
                        }
                        imageReceiver.setImageCoords(i + linkX, linkPreviewY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
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
                    int oldAlpha = Theme.chat_timeBackgroundPaint.getAlpha();
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
                if (this.videoInfoLayout != null && (!this.drawPhotoImage || this.photoImage.getVisible())) {
                    if (!this.hasGamePreview && !this.hasInvoicePreview) {
                        x = ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.durationWidth;
                        y = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f);
                        this.rect.set((float) (x - AndroidUtilities.dp(4.0f)), (float) (y - AndroidUtilities.dp(1.5f)), (float) ((this.durationWidth + x) + AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(14.5f) + y));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                    } else if (this.drawPhotoImage) {
                        x = this.photoImage.getImageX() + AndroidUtilities.dp(8.5f);
                        y = this.photoImage.getImageY() + AndroidUtilities.dp(6.0f);
                        this.rect.set((float) (x - AndroidUtilities.dp(4.0f)), (float) (y - AndroidUtilities.dp(1.5f)), (float) ((this.durationWidth + x) + AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(16.5f) + y));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
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
                    instantY = (this.linkPreviewHeight + startY) + AndroidUtilities.dp(10.0f);
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
                        this.instantViewSelectorDrawable.setBounds(linkX, instantY, this.instantWidth + linkX, AndroidUtilities.dp(36.0f) + instantY);
                        this.instantViewSelectorDrawable.draw(canvas);
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
                    Theme.chat_roundVideoShadow.setAlpha((int) (this.photoImage.getCurrentAlpha() * 255.0f));
                    Theme.chat_roundVideoShadow.setBounds(x, y, (AndroidUtilities.roundMessageSize + x) + AndroidUtilities.dp(6.0f), (AndroidUtilities.roundMessageSize + y) + AndroidUtilities.dp(6.0f));
                    Theme.chat_roundVideoShadow.draw(canvas);
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
        if (this.buttonState == -1 && this.currentMessageObject.needDrawBluredPreview() && !MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && this.photoImage.getVisible()) {
            int drawable = 4;
            if (this.currentMessageObject.messageOwner.destroyTime != 0) {
                if (this.currentMessageObject.isOutOwner()) {
                    drawable = 6;
                } else {
                    drawable = 5;
                }
            }
            BaseCell.setDrawableBounds(Theme.chat_photoStatesDrawables[drawable][this.buttonPressed], this.buttonX, this.buttonY);
            Theme.chat_photoStatesDrawables[drawable][this.buttonPressed].setAlpha((int) ((255.0f * (1.0f - this.radialProgress.getAlpha())) * this.controlsAlpha));
            Theme.chat_photoStatesDrawables[drawable][this.buttonPressed].draw(canvas);
            if (this.currentMessageObject.messageOwner.destroyTime != 0) {
                if (!this.currentMessageObject.isOutOwner()) {
                    float progress;
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
        }
        if (this.documentAttachType == 2 || this.currentMessageObject.type == 8) {
            if (!(!this.photoImage.getVisible() || this.hasGamePreview || this.currentMessageObject.needDrawBluredPreview())) {
                oldAlpha = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                Drawable drawable2 = Theme.chat_msgMediaMenuDrawable;
                i2 = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                this.otherX = i2;
                int imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                this.otherY = imageY;
                BaseCell.setDrawableBounds(drawable2, i2, imageY);
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
                    i2 = this.backgroundDrawableLeft;
                    f = (this.currentMessageObject.isOutOwner() || this.drawPinnedBottom) ? 12.0f : 18.0f;
                    x1 = i2 + AndroidUtilities.dp(f);
                    i2 = this.layoutHeight;
                    if (this.drawPinnedBottom) {
                        i = 2;
                    } else {
                        i = 0;
                    }
                    y1 = (i2 - AndroidUtilities.dp(6.3f - ((float) i))) - this.timeLayout.getHeight();
                } else {
                    x1 = this.backgroundDrawableLeft + AndroidUtilities.dp(8.0f);
                    y1 = this.layoutHeight - AndroidUtilities.dp((float) (28 - (this.drawPinnedBottom ? 2 : 0)));
                    this.rect.set((float) x1, (float) y1, (float) ((this.timeWidthAudio + x1) + AndroidUtilities.dp(22.0f)), (float) (AndroidUtilities.dp(17.0f) + y1));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_actionBackgroundPaint);
                    if (playing || !this.currentMessageObject.isContentUnread()) {
                        if (!playing || MediaController.getInstance().isMessagePaused()) {
                            this.roundVideoPlayingDrawable.stop();
                        } else {
                            this.roundVideoPlayingDrawable.start();
                        }
                        BaseCell.setDrawableBounds(this.roundVideoPlayingDrawable, (this.timeWidthAudio + x1) + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(2.3f) + y1);
                        this.roundVideoPlayingDrawable.draw(canvas);
                    } else {
                        Theme.chat_docBackPaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
                        canvas.drawCircle((float) ((this.timeWidthAudio + x1) + AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(8.3f) + y1), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                    }
                    x1 += AndroidUtilities.dp(4.0f);
                    y1 += AndroidUtilities.dp(1.7f);
                }
                canvas.save();
                canvas.translate((float) x1, (float) y1);
                this.durationLayout.draw(canvas);
                canvas.restore();
            }
        } else if (this.documentAttachType == 5) {
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_outAudioTitleText));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor(Theme.key_chat_outAudioPerfomerText));
                Theme.chat_audioTimePaint.setColor(Theme.getColor(Theme.key_chat_outAudioDurationText));
                radialProgress = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_outAudioSelectedProgress : Theme.key_chat_outAudioProgress;
                radialProgress.setProgressColor(Theme.getColor(str));
            } else {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_inAudioTitleText));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor(Theme.key_chat_inAudioPerfomerText));
                Theme.chat_audioTimePaint.setColor(Theme.getColor(Theme.key_chat_inAudioDurationText));
                radialProgress = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress;
                radialProgress.setProgressColor(Theme.getColor(str));
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
            Drawable menuDrawable = this.currentMessageObject.isOutOwner() ? isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable : isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            i = (this.backgroundWidth + this.buttonX) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
            this.otherX = i;
            i2 = this.buttonY - AndroidUtilities.dp(5.0f);
            this.otherY = i2;
            BaseCell.setDrawableBounds(menuDrawable, i, i2);
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
                Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outVoiceSeekbarFill : Theme.key_chat_inVoiceSeekbarFill));
                canvas.drawCircle((float) ((this.timeAudioX + this.timeWidthAudio) + AndroidUtilities.dp(6.0f)), (float) ((AndroidUtilities.dp(51.0f) + this.namesOffset) + this.mediaOffsetY), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
            }
        }
        if (this.currentMessageObject.type == 1 || this.documentAttachType == 4) {
            if (this.photoImage.getVisible()) {
                if (!this.currentMessageObject.needDrawBluredPreview() && this.documentAttachType == 4) {
                    oldAlpha = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                    Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) oldAlpha) * this.controlsAlpha));
                    drawable2 = Theme.chat_msgMediaMenuDrawable;
                    i2 = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                    this.otherX = i2;
                    imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                    this.otherY = imageY;
                    BaseCell.setDrawableBounds(drawable2, i2, imageY);
                    Theme.chat_msgMediaMenuDrawable.draw(canvas);
                    Theme.chat_msgMediaMenuDrawable.setAlpha(oldAlpha);
                }
                if (!(this.forceNotDrawTime || this.infoLayout == null || (this.buttonState != 1 && this.buttonState != 0 && this.buttonState != 3 && !this.currentMessageObject.needDrawBluredPreview()))) {
                    Theme.chat_infoPaint.setColor(Theme.getColor(Theme.key_chat_mediaInfoText));
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
                    if (this.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                        Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                    } else {
                        Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_outVenueNameText));
                    }
                    Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outVenueInfoSelectedText : Theme.key_chat_outVenueInfoText));
                } else {
                    if (this.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                        Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                    } else {
                        Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_inVenueNameText));
                    }
                    Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inVenueInfoSelectedText : Theme.key_chat_inVenueInfoText));
                }
                if (this.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                    int cy = this.photoImage.getImageY2() + AndroidUtilities.dp(30.0f);
                    if (!this.locationExpired) {
                        this.forceNotDrawTime = true;
                        progress = 1.0f - (((float) Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date)) / ((float) this.currentMessageObject.messageOwner.media.period));
                        this.rect.set((float) (this.photoImage.getImageX2() - AndroidUtilities.dp(43.0f)), (float) (cy - AndroidUtilities.dp(15.0f)), (float) (this.photoImage.getImageX2() - AndroidUtilities.dp(13.0f)), (float) (AndroidUtilities.dp(15.0f) + cy));
                        if (this.currentMessageObject.isOutOwner()) {
                            Theme.chat_radialProgress2Paint.setColor(Theme.getColor(Theme.key_chat_outInstant));
                            Theme.chat_livePaint.setColor(Theme.getColor(Theme.key_chat_outInstant));
                        } else {
                            Theme.chat_radialProgress2Paint.setColor(Theme.getColor(Theme.key_chat_inInstant));
                            Theme.chat_livePaint.setColor(Theme.getColor(Theme.key_chat_inInstant));
                        }
                        Theme.chat_radialProgress2Paint.setAlpha(50);
                        canvas.drawCircle(this.rect.centerX(), this.rect.centerY(), (float) AndroidUtilities.dp(15.0f), Theme.chat_radialProgress2Paint);
                        Theme.chat_radialProgress2Paint.setAlpha(255);
                        canvas.drawArc(this.rect, -90.0f, -360.0f * progress, false, Theme.chat_radialProgress2Paint);
                        String text = LocaleController.formatLocationLeftTime(Math.abs(this.currentMessageObject.messageOwner.media.period - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date)));
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
                    canvas.translate((float) (((this.docTitleOffsetX + this.photoImage.getImageX()) + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f)));
                    this.docTitleLayout.draw(canvas);
                    canvas.restore();
                    if (this.infoLayout != null) {
                        canvas.save();
                        canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) ((this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f)));
                        this.infoLayout.draw(canvas);
                        canvas.restore();
                    }
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
            i = AndroidUtilities.dp(205.0f) + x;
            i2 = AndroidUtilities.dp(22.0f);
            this.otherY = i2;
            BaseCell.setDrawableBounds(phone, i, i2);
            phone.draw(canvas);
        } else if (this.currentMessageObject.type == 12) {
            Theme.chat_contactNamePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outContactNameText : Theme.key_chat_inContactNameText));
            Theme.chat_contactPhonePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outContactPhoneText : Theme.key_chat_inContactPhoneText));
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
            i = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(48.0f);
            this.otherX = i;
            i2 = this.photoImage.getImageY() - AndroidUtilities.dp(5.0f);
            this.otherY = i2;
            BaseCell.setDrawableBounds(menuDrawable, i, i2);
            menuDrawable.draw(canvas);
            if (this.drawInstantView) {
                int textX = this.photoImage.getImageX() - AndroidUtilities.dp(2.0f);
                instantY = getMeasuredHeight() - AndroidUtilities.dp(64.0f);
                backPaint = Theme.chat_instantViewRectPaint;
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_instantViewPaint.setColor(Theme.getColor(Theme.key_chat_outPreviewInstantText));
                    backPaint.setColor(Theme.getColor(Theme.key_chat_outPreviewInstantText));
                } else {
                    Theme.chat_instantViewPaint.setColor(Theme.getColor(Theme.key_chat_inPreviewInstantText));
                    backPaint.setColor(Theme.getColor(Theme.key_chat_inPreviewInstantText));
                }
                if (VERSION.SDK_INT >= 21) {
                    this.instantViewSelectorDrawable.setBounds(textX, instantY, this.instantWidth + textX, AndroidUtilities.dp(36.0f) + instantY);
                    this.instantViewSelectorDrawable.draw(canvas);
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
                this.captionX = (AndroidUtilities.dp(this.currentMessageObject.isOutOwner() ? 11.0f : 17.0f) + this.backgroundDrawableLeft) + this.captionOffsetX;
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
            Theme.chat_replyLinePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
            canvas.drawRect((float) linkX, (float) (linkPreviewY - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + linkX), (float) (this.linkPreviewHeight + linkPreviewY), Theme.chat_replyLinePaint);
            if (this.siteNameLayout != null) {
                Theme.chat_replyNamePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
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
                Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
            } else {
                Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
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
                Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_outFileNameText));
                Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileInfoSelectedText : Theme.key_chat_outFileInfoText));
                Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileBackgroundSelected : Theme.key_chat_outFileBackground));
                if (isDrawSelectedBackground()) {
                    menuDrawable = Theme.chat_msgOutMenuSelectedDrawable;
                } else {
                    menuDrawable = Theme.chat_msgOutMenuDrawable;
                }
            } else {
                Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_inFileNameText));
                Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileInfoSelectedText : Theme.key_chat_inFileInfoText));
                Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileBackgroundSelected : Theme.key_chat_inFileBackground));
                menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            }
            if (this.drawPhotoImage) {
                if (this.currentMessageObject.type == 0) {
                    i = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(56.0f);
                    this.otherX = i;
                    i2 = this.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                    this.otherY = i2;
                    BaseCell.setDrawableBounds(menuDrawable, i, i2);
                } else {
                    i = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(40.0f);
                    this.otherX = i;
                    i2 = this.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                    this.otherY = i2;
                    BaseCell.setDrawableBounds(menuDrawable, i, i2);
                }
                x = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
                titleY = this.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                subtitleY = (this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f);
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
                        i = (isDrawSelectedBackground() || this.buttonPressed != 0) ? 1 : 0;
                        radialProgress.swapBackground(drawableArr[i]);
                    }
                }
                if (imageDrawn) {
                    if (this.buttonState == -1) {
                        this.radialProgress.setHideCurrentDrawable(true);
                    }
                    this.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
                } else {
                    this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) (this.photoImage.getImageX() + this.photoImage.getImageWidth()), (float) (this.photoImage.getImageY() + this.photoImage.getImageHeight()));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
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
                i = (this.backgroundWidth + this.buttonX) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
                this.otherX = i;
                i2 = this.buttonY - AndroidUtilities.dp(5.0f);
                this.otherY = i2;
                BaseCell.setDrawableBounds(menuDrawable, i, i2);
                x = this.buttonX + AndroidUtilities.dp(53.0f);
                titleY = this.buttonY + AndroidUtilities.dp(4.0f);
                subtitleY = this.buttonY + AndroidUtilities.dp(27.0f);
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
                FileLog.m3e(e2);
            }
            try {
                if (this.infoLayout != null) {
                    canvas.save();
                    canvas.translate((float) x, (float) subtitleY);
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            } catch (Throwable e22) {
                FileLog.m3e(e22);
            }
        }
        if (this.drawImageButton && this.photoImage.getVisible()) {
            if (this.controlsAlpha != 1.0f) {
                this.radialProgress.setOverrideAlpha(this.controlsAlpha);
            }
            this.radialProgress.draw(canvas);
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
                y = (button.f10y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                Theme.chat_systemDrawable.setColorFilter(a == this.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(button.f9x + addX, y, (button.f9x + addX) + button.width, button.height + y);
                Theme.chat_systemDrawable.draw(canvas);
                canvas.save();
                canvas.translate((float) ((button.f9x + addX) + AndroidUtilities.dp(5.0f)), (float) (((AndroidUtilities.dp(44.0f) - button.title.getLineBottom(button.title.getLineCount() - 1)) / 2) + y));
                button.title.draw(canvas);
                canvas.restore();
                if (button.button instanceof TL_keyboardButtonUrl) {
                    BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((button.f9x + button.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y);
                    Theme.chat_botLinkDrawalbe.draw(canvas);
                } else if (button.button instanceof TL_keyboardButtonSwitchInline) {
                    BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((button.f9x + button.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y);
                    Theme.chat_botInlineDrawable.draw(canvas);
                } else if ((button.button instanceof TL_keyboardButtonCallback) || (button.button instanceof TL_keyboardButtonRequestGeoLocation) || (button.button instanceof TL_keyboardButtonGame) || (button.button instanceof TL_keyboardButtonBuy)) {
                    boolean drawProgress = (((button.button instanceof TL_keyboardButtonCallback) || (button.button instanceof TL_keyboardButtonGame) || (button.button instanceof TL_keyboardButtonBuy)) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCallback(this.currentMessageObject, button.button)) || ((button.button instanceof TL_keyboardButtonRequestGeoLocation) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCurrentLocation(this.currentMessageObject, button.button));
                    if (drawProgress || !(drawProgress || button.progressAlpha == 0.0f)) {
                        Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (button.progressAlpha * 255.0f)));
                        x = ((button.f9x + button.width) - AndroidUtilities.dp(12.0f)) + addX;
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
            } else if (this.currentMessageObject.attachPathExists) {
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
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
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
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
            } else if (this.documentAttachType == 4) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
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
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
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
                if (this.currentMessageObject.shouldEncryptPhotoOrVideo()) {
                    i = 2;
                } else {
                    i = 0;
                }
                imageReceiver.setImage(tLObject, str, fileLocation, str2, i2, null, i);
            } else if (this.currentMessageObject.type == 8) {
                this.currentMessageObject.gifState = 2.0f;
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentMessageObject.messageOwner.media.document, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilterThumb, this.currentMessageObject.messageOwner.media.document.size, null, 0);
            } else if (this.currentMessageObject.isRoundVideo()) {
                if (this.currentMessageObject.isSecretMedia()) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 1);
                } else {
                    this.currentMessageObject.gifState = 2.0f;
                    Document document = this.currentMessageObject.getDocument();
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(document, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilterThumb, document.size, null, 0);
                }
            } else if (this.currentMessageObject.type == 9) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.messageOwner.media.document, false, 0);
            } else if (this.documentAttachType == 4) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
            } else if (this.currentMessageObject.type != 0 || this.documentAttachType == 0) {
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, this.currentPhotoFilterThumb, 0, null, 0);
            } else if (this.documentAttachType == 2) {
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(this.currentMessageObject.messageOwner.media.webpage.document, null, this.currentPhotoObject.location, this.currentPhotoFilterThumb, this.currentMessageObject.messageOwner.media.webpage.document.size, null, 0);
                this.currentMessageObject.gifState = 2.0f;
            } else if (this.documentAttachType == 1) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.messageOwner.media.webpage.document, false, 0);
            }
            this.buttonState = 1;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
            invalidate();
        } else if (this.buttonState == 1) {
            if (this.documentAttachType == 3 || this.documentAttachType == 5) {
                if (MediaController.getInstance().pauseMessage(this.currentMessageObject)) {
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
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
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

    public void onFailedDownload(String fileName) {
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
        if (this.currentMessageObject != null && set && !thumb && !this.currentMessageObject.mediaExists && !this.currentMessageObject.attachPathExists) {
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
                signString = ContactsController.formatName(signUser.first_name, signUser.last_name).replace('\n', ' ');
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
        if (this.currentPosition != null && !this.currentPosition.last) {
            return false;
        }
        if (messageObject.eventId != 0) {
            return false;
        }
        if (messageObject.messageOwner.fwd_from != null && !messageObject.isOutOwner() && messageObject.messageOwner.fwd_from.saved_from_peer != null && messageObject.getDialogId() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId())) {
            this.drwaShareGoIcon = true;
            return true;
        } else if (messageObject.type == 13) {
            return false;
        } else {
            if (messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.channel_id != 0 && !messageObject.isOutOwner()) {
                return true;
            }
            if (messageObject.isFromUser()) {
                if ((messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || messageObject.messageOwner.media == null || ((messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && !(messageObject.messageOwner.media.webpage instanceof TL_webPage))) {
                    return false;
                }
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (user != null && user.bot) {
                    return true;
                }
                if (!messageObject.isOut()) {
                    if ((messageObject.messageOwner.media instanceof TL_messageMediaGame) || (messageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                        return true;
                    }
                    if (messageObject.isMegagroup()) {
                        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(messageObject.messageOwner.to_id.channel_id));
                        if (chat == null || chat.username == null || chat.username.length() <= 0 || (messageObject.messageOwner.media instanceof TL_messageMediaContact) || (messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                            return false;
                        }
                        return true;
                    }
                }
            } else if ((messageObject.messageOwner.from_id < 0 || messageObject.messageOwner.post) && messageObject.messageOwner.to_id.channel_id != 0 && ((messageObject.messageOwner.via_bot_id == 0 && messageObject.messageOwner.reply_to_msg_id == 0) || messageObject.type != 13)) {
                return true;
            }
            return false;
        }
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

    private void setMessageObjectInternal(org.telegram.messenger.MessageObject r39) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r32_2 'stringBuilder' android.text.SpannableStringBuilder) in PHI: PHI: (r32_3 'stringBuilder' android.text.SpannableStringBuilder) = (r32_2 'stringBuilder' android.text.SpannableStringBuilder), (r32_4 'stringBuilder' android.text.SpannableStringBuilder) binds: {(r32_2 'stringBuilder' android.text.SpannableStringBuilder)=B:115:0x0461, (r32_4 'stringBuilder' android.text.SpannableStringBuilder)=B:228:0x0900}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r38 = this;
        r0 = r39;
        r2 = r0.messageOwner;
        r2 = r2.flags;
        r2 = r2 & 1024;
        if (r2 == 0) goto L_0x002a;
    L_0x000a:
        r0 = r38;
        r2 = r0.currentMessageObject;
        r2 = r2.viewsReloaded;
        if (r2 != 0) goto L_0x002a;
    L_0x0012:
        r0 = r38;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r0 = r38;
        r4 = r0.currentMessageObject;
        r4 = r4.messageOwner;
        r2.addToViewsQueue(r4);
        r0 = r38;
        r2 = r0.currentMessageObject;
        r4 = 1;
        r2.viewsReloaded = r4;
    L_0x002a:
        r38.updateCurrentUserAndChat();
        r0 = r38;
        r2 = r0.isAvatarVisible;
        if (r2 == 0) goto L_0x006c;
    L_0x0033:
        r0 = r38;
        r2 = r0.currentUser;
        if (r2 == 0) goto L_0x0786;
    L_0x0039:
        r0 = r38;
        r2 = r0.currentUser;
        r2 = r2.photo;
        if (r2 == 0) goto L_0x077f;
    L_0x0041:
        r0 = r38;
        r2 = r0.currentUser;
        r2 = r2.photo;
        r2 = r2.photo_small;
        r0 = r38;
        r0.currentPhoto = r2;
    L_0x004d:
        r0 = r38;
        r2 = r0.avatarDrawable;
        r0 = r38;
        r4 = r0.currentUser;
        r2.setInfo(r4);
    L_0x0058:
        r0 = r38;
        r2 = r0.avatarImage;
        r0 = r38;
        r3 = r0.currentPhoto;
        r4 = "50_50";
        r0 = r38;
        r5 = r0.avatarDrawable;
        r6 = 0;
        r7 = 0;
        r2.setImage(r3, r4, r5, r6, r7);
    L_0x006c:
        r38.measureTime(r39);
        r2 = 0;
        r0 = r38;
        r0.namesOffset = r2;
        r37 = 0;
        r36 = 0;
        r0 = r39;
        r2 = r0.messageOwner;
        r2 = r2.via_bot_id;
        if (r2 == 0) goto L_0x07ca;
    L_0x0080:
        r0 = r38;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r0 = r39;
        r4 = r0.messageOwner;
        r4 = r4.via_bot_id;
        r4 = java.lang.Integer.valueOf(r4);
        r19 = r2.getUser(r4);
        if (r19 == 0) goto L_0x00ee;
    L_0x0098:
        r0 = r19;
        r2 = r0.username;
        if (r2 == 0) goto L_0x00ee;
    L_0x009e:
        r0 = r19;
        r2 = r0.username;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x00ee;
    L_0x00a8:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "@";
        r2 = r2.append(r4);
        r0 = r19;
        r4 = r0.username;
        r2 = r2.append(r4);
        r37 = r2.toString();
        r2 = " via <b>%s</b>";
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r6 = 0;
        r4[r6] = r37;
        r2 = java.lang.String.format(r2, r4);
        r36 = org.telegram.messenger.AndroidUtilities.replaceTags(r2);
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r4 = 0;
        r6 = r36.length();
        r0 = r36;
        r2 = r2.measureText(r0, r4, r6);
        r8 = (double) r2;
        r8 = java.lang.Math.ceil(r8);
        r2 = (int) r8;
        r0 = r38;
        r0.viaWidth = r2;
        r0 = r19;
        r1 = r38;
        r1.currentViaBotUser = r0;
    L_0x00ee:
        r0 = r38;
        r2 = r0.drawName;
        if (r2 == 0) goto L_0x0822;
    L_0x00f4:
        r0 = r38;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x0822;
    L_0x00fa:
        r0 = r38;
        r2 = r0.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 != 0) goto L_0x0822;
    L_0x0104:
        r18 = 1;
    L_0x0106:
        r0 = r39;
        r2 = r0.messageOwner;
        r2 = r2.fwd_from;
        if (r2 == 0) goto L_0x0116;
    L_0x010e:
        r0 = r39;
        r2 = r0.type;
        r4 = 14;
        if (r2 != r4) goto L_0x0826;
    L_0x0116:
        if (r37 == 0) goto L_0x0826;
    L_0x0118:
        r35 = 1;
    L_0x011a:
        if (r18 != 0) goto L_0x011e;
    L_0x011c:
        if (r35 == 0) goto L_0x08c8;
    L_0x011e:
        r2 = 1;
        r0 = r38;
        r0.drawNameLayout = r2;
        r2 = r38.getMaxNameWidth();
        r0 = r38;
        r0.nameWidth = r2;
        r0 = r38;
        r2 = r0.nameWidth;
        if (r2 >= 0) goto L_0x013b;
    L_0x0131:
        r2 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r38;
        r0.nameWidth = r2;
    L_0x013b:
        r0 = r38;
        r2 = r0.currentUser;
        if (r2 == 0) goto L_0x082a;
    L_0x0141:
        r0 = r38;
        r2 = r0.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 != 0) goto L_0x082a;
    L_0x014b:
        r0 = r38;
        r2 = r0.currentMessageObject;
        r2 = r2.type;
        r4 = 13;
        if (r2 == r4) goto L_0x082a;
    L_0x0155:
        r0 = r38;
        r2 = r0.currentMessageObject;
        r2 = r2.type;
        r4 = 5;
        if (r2 == r4) goto L_0x082a;
    L_0x015e:
        r0 = r38;
        r2 = r0.delegate;
        r0 = r38;
        r4 = r0.currentUser;
        r4 = r4.id;
        r2 = r2.isChatAdminCell(r4);
        if (r2 == 0) goto L_0x082a;
    L_0x016e:
        r2 = "ChatAdmin";
        r4 = NUM; // 0x7f0c017a float:1.8609959E38 double:1.053097585E-314;
        r16 = org.telegram.messenger.LocaleController.getString(r2, r4);
        r2 = org.telegram.ui.ActionBar.Theme.chat_adminPaint;
        r0 = r16;
        r2 = r2.measureText(r0);
        r8 = (double) r2;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r17 = r0;
        r0 = r38;
        r2 = r0.nameWidth;
        r2 = r2 - r17;
        r0 = r38;
        r0.nameWidth = r2;
    L_0x0192:
        if (r18 == 0) goto L_0x084b;
    L_0x0194:
        r0 = r38;
        r2 = r0.currentUser;
        if (r2 == 0) goto L_0x0830;
    L_0x019a:
        r0 = r38;
        r2 = r0.currentUser;
        r2 = org.telegram.messenger.UserObject.getUserName(r2);
        r0 = r38;
        r0.currentNameString = r2;
    L_0x01a6:
        r0 = r38;
        r2 = r0.currentNameString;
        r4 = 10;
        r6 = 32;
        r4 = r2.replace(r4, r6);
        r6 = org.telegram.ui.ActionBar.Theme.chat_namePaint;
        r0 = r38;
        r8 = r0.nameWidth;
        if (r35 == 0) goto L_0x0854;
    L_0x01ba:
        r0 = r38;
        r2 = r0.viaWidth;
    L_0x01be:
        r2 = r8 - r2;
        r2 = (float) r2;
        r8 = android.text.TextUtils.TruncateAt.END;
        r3 = android.text.TextUtils.ellipsize(r4, r6, r2, r8);
        if (r35 == 0) goto L_0x027a;
    L_0x01c9:
        r2 = org.telegram.ui.ActionBar.Theme.chat_namePaint;
        r4 = 0;
        r6 = r3.length();
        r2 = r2.measureText(r3, r4, r6);
        r8 = (double) r2;
        r8 = java.lang.Math.ceil(r8);
        r2 = (int) r8;
        r0 = r38;
        r0.viaNameWidth = r2;
        r0 = r38;
        r2 = r0.viaNameWidth;
        if (r2 == 0) goto L_0x01f3;
    L_0x01e4:
        r0 = r38;
        r2 = r0.viaNameWidth;
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r38;
        r0.viaNameWidth = r2;
    L_0x01f3:
        r0 = r38;
        r2 = r0.currentMessageObject;
        r2 = r2.type;
        r4 = 13;
        if (r2 == r4) goto L_0x0206;
    L_0x01fd:
        r0 = r38;
        r2 = r0.currentMessageObject;
        r2 = r2.type;
        r4 = 5;
        if (r2 != r4) goto L_0x0857;
    L_0x0206:
        r2 = "chat_stickerViaBotNameText";
        r21 = org.telegram.ui.ActionBar.Theme.getColor(r2);
    L_0x020d:
        r0 = r38;
        r2 = r0.currentNameString;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x086e;
    L_0x0217:
        r32 = new android.text.SpannableStringBuilder;
        r2 = "%s via %s";
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r6 = 0;
        r4[r6] = r3;
        r6 = 1;
        r4[r6] = r37;
        r2 = java.lang.String.format(r2, r4);
        r0 = r32;
        r0.<init>(r2);
        r2 = new org.telegram.ui.Components.TypefaceSpan;
        r4 = android.graphics.Typeface.DEFAULT;
        r6 = 0;
        r0 = r21;
        r2.<init>(r4, r6, r0);
        r4 = r3.length();
        r4 = r4 + 1;
        r6 = r3.length();
        r6 = r6 + 4;
        r8 = 33;
        r0 = r32;
        r0.setSpan(r2, r4, r6, r8);
        r2 = new org.telegram.ui.Components.TypefaceSpan;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r6 = 0;
        r0 = r21;
        r2.<init>(r4, r6, r0);
        r4 = r3.length();
        r4 = r4 + 5;
        r6 = r32.length();
        r8 = 33;
        r0 = r32;
        r0.setSpan(r2, r4, r6, r8);
        r3 = r32;
    L_0x026d:
        r2 = org.telegram.ui.ActionBar.Theme.chat_namePaint;
        r0 = r38;
        r4 = r0.nameWidth;
        r4 = (float) r4;
        r6 = android.text.TextUtils.TruncateAt.END;
        r3 = android.text.TextUtils.ellipsize(r3, r2, r4, r6);
    L_0x027a:
        r2 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x08bb }
        r4 = org.telegram.ui.ActionBar.Theme.chat_namePaint;	 Catch:{ Exception -> 0x08bb }
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r6 = r0.nameWidth;	 Catch:{ Exception -> 0x08bb }
        r8 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x08bb }
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x08bb }
        r5 = r6 + r8;	 Catch:{ Exception -> 0x08bb }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x08bb }
        r7 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x08bb }
        r8 = 0;	 Catch:{ Exception -> 0x08bb }
        r9 = 0;	 Catch:{ Exception -> 0x08bb }
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x08bb }
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r0.nameLayout = r2;	 Catch:{ Exception -> 0x08bb }
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r2 = r0.nameLayout;	 Catch:{ Exception -> 0x08bb }
        if (r2 == 0) goto L_0x08b4;	 Catch:{ Exception -> 0x08bb }
    L_0x029d:
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r2 = r0.nameLayout;	 Catch:{ Exception -> 0x08bb }
        r2 = r2.getLineCount();	 Catch:{ Exception -> 0x08bb }
        if (r2 <= 0) goto L_0x08b4;	 Catch:{ Exception -> 0x08bb }
    L_0x02a7:
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r2 = r0.nameLayout;	 Catch:{ Exception -> 0x08bb }
        r4 = 0;	 Catch:{ Exception -> 0x08bb }
        r2 = r2.getLineWidth(r4);	 Catch:{ Exception -> 0x08bb }
        r8 = (double) r2;	 Catch:{ Exception -> 0x08bb }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x08bb }
        r2 = (int) r8;	 Catch:{ Exception -> 0x08bb }
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r0.nameWidth = r2;	 Catch:{ Exception -> 0x08bb }
        r0 = r39;	 Catch:{ Exception -> 0x08bb }
        r2 = r0.type;	 Catch:{ Exception -> 0x08bb }
        r4 = 13;	 Catch:{ Exception -> 0x08bb }
        if (r2 == r4) goto L_0x02d1;	 Catch:{ Exception -> 0x08bb }
    L_0x02c2:
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r2 = r0.namesOffset;	 Catch:{ Exception -> 0x08bb }
        r4 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;	 Catch:{ Exception -> 0x08bb }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x08bb }
        r2 = r2 + r4;	 Catch:{ Exception -> 0x08bb }
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r0.namesOffset = r2;	 Catch:{ Exception -> 0x08bb }
    L_0x02d1:
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r2 = r0.nameLayout;	 Catch:{ Exception -> 0x08bb }
        r4 = 0;	 Catch:{ Exception -> 0x08bb }
        r2 = r2.getLineLeft(r4);	 Catch:{ Exception -> 0x08bb }
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r0.nameOffsetX = r2;	 Catch:{ Exception -> 0x08bb }
    L_0x02de:
        if (r16 == 0) goto L_0x08c1;	 Catch:{ Exception -> 0x08bb }
    L_0x02e0:
        r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x08bb }
        r6 = org.telegram.ui.ActionBar.Theme.chat_adminPaint;	 Catch:{ Exception -> 0x08bb }
        r2 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x08bb }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x08bb }
        r7 = r17 + r2;	 Catch:{ Exception -> 0x08bb }
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x08bb }
        r9 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x08bb }
        r10 = 0;	 Catch:{ Exception -> 0x08bb }
        r11 = 0;	 Catch:{ Exception -> 0x08bb }
        r5 = r16;	 Catch:{ Exception -> 0x08bb }
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x08bb }
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r0.adminLayout = r4;	 Catch:{ Exception -> 0x08bb }
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r2 = r0.nameWidth;	 Catch:{ Exception -> 0x08bb }
        r2 = (float) r2;	 Catch:{ Exception -> 0x08bb }
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r4 = r0.adminLayout;	 Catch:{ Exception -> 0x08bb }
        r6 = 0;	 Catch:{ Exception -> 0x08bb }
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x08bb }
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;	 Catch:{ Exception -> 0x08bb }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x08bb }
        r6 = (float) r6;	 Catch:{ Exception -> 0x08bb }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x08bb }
        r2 = r2 + r4;	 Catch:{ Exception -> 0x08bb }
        r2 = (int) r2;	 Catch:{ Exception -> 0x08bb }
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r0.nameWidth = r2;	 Catch:{ Exception -> 0x08bb }
    L_0x0317:
        r0 = r38;
        r2 = r0.currentNameString;
        r2 = r2.length();
        if (r2 != 0) goto L_0x0326;
    L_0x0321:
        r2 = 0;
        r0 = r38;
        r0.currentNameString = r2;
    L_0x0326:
        r2 = 0;
        r0 = r38;
        r0.currentForwardUser = r2;
        r2 = 0;
        r0 = r38;
        r0.currentForwardNameString = r2;
        r2 = 0;
        r0 = r38;
        r0.currentForwardChannel = r2;
        r0 = r38;
        r2 = r0.forwardedNameLayout;
        r4 = 0;
        r6 = 0;
        r2[r4] = r6;
        r0 = r38;
        r2 = r0.forwardedNameLayout;
        r4 = 1;
        r6 = 0;
        r2[r4] = r6;
        r2 = 0;
        r0 = r38;
        r0.forwardedNameWidth = r2;
        r0 = r38;
        r2 = r0.drawForwardedName;
        if (r2 == 0) goto L_0x059b;
    L_0x0350:
        r2 = r39.needDrawForwarded();
        if (r2 == 0) goto L_0x059b;
    L_0x0356:
        r0 = r38;
        r2 = r0.currentPosition;
        if (r2 == 0) goto L_0x0364;
    L_0x035c:
        r0 = r38;
        r2 = r0.currentPosition;
        r2 = r2.minY;
        if (r2 != 0) goto L_0x059b;
    L_0x0364:
        r0 = r39;
        r2 = r0.messageOwner;
        r2 = r2.fwd_from;
        r2 = r2.channel_id;
        if (r2 == 0) goto L_0x038a;
    L_0x036e:
        r0 = r38;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r0 = r39;
        r4 = r0.messageOwner;
        r4 = r4.fwd_from;
        r4 = r4.channel_id;
        r4 = java.lang.Integer.valueOf(r4);
        r2 = r2.getChat(r4);
        r0 = r38;
        r0.currentForwardChannel = r2;
    L_0x038a:
        r0 = r39;
        r2 = r0.messageOwner;
        r2 = r2.fwd_from;
        r2 = r2.from_id;
        if (r2 == 0) goto L_0x03b0;
    L_0x0394:
        r0 = r38;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r0 = r39;
        r4 = r0.messageOwner;
        r4 = r4.fwd_from;
        r4 = r4.from_id;
        r4 = java.lang.Integer.valueOf(r4);
        r2 = r2.getUser(r4);
        r0 = r38;
        r0.currentForwardUser = r2;
    L_0x03b0:
        r0 = r38;
        r2 = r0.currentForwardUser;
        if (r2 != 0) goto L_0x03bc;
    L_0x03b6:
        r0 = r38;
        r2 = r0.currentForwardChannel;
        if (r2 == 0) goto L_0x059b;
    L_0x03bc:
        r0 = r38;
        r2 = r0.currentForwardChannel;
        if (r2 == 0) goto L_0x08e5;
    L_0x03c2:
        r0 = r38;
        r2 = r0.currentForwardUser;
        if (r2 == 0) goto L_0x08d9;
    L_0x03c8:
        r2 = "%s (%s)";
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r6 = 0;
        r0 = r38;
        r8 = r0.currentForwardChannel;
        r8 = r8.title;
        r4[r6] = r8;
        r6 = 1;
        r0 = r38;
        r8 = r0.currentForwardUser;
        r8 = org.telegram.messenger.UserObject.getUserName(r8);
        r4[r6] = r8;
        r2 = java.lang.String.format(r2, r4);
        r0 = r38;
        r0.currentForwardNameString = r2;
    L_0x03ea:
        r2 = r38.getMaxNameWidth();
        r0 = r38;
        r0.forwardedNameWidth = r2;
        r2 = "From";
        r4 = NUM; // 0x7f0c0315 float:1.8610792E38 double:1.0530977883E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r2, r4);
        r2 = "FromFormatted";
        r4 = NUM; // 0x7f0c031d float:1.8610808E38 double:1.053097792E-314;
        r24 = org.telegram.messenger.LocaleController.getString(r2, r4);
        r2 = "%1$s";
        r0 = r24;
        r27 = r0.indexOf(r2);
        r2 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r23;
        r4 = r4.append(r0);
        r6 = " ";
        r4 = r4.append(r6);
        r4 = r4.toString();
        r2 = r2.measureText(r4);
        r8 = (double) r2;
        r8 = java.lang.Math.ceil(r8);
        r0 = (int) r8;
        r26 = r0;
        r0 = r38;
        r2 = r0.currentForwardNameString;
        r4 = 10;
        r6 = 32;
        r2 = r2.replace(r4, r6);
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r0 = r38;
        r6 = r0.forwardedNameWidth;
        r6 = r6 - r26;
        r0 = r38;
        r8 = r0.viaWidth;
        r6 = r6 - r8;
        r6 = (float) r6;
        r8 = android.text.TextUtils.TruncateAt.END;
        r30 = android.text.TextUtils.ellipsize(r2, r4, r6, r8);
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x08f9 }
        r4 = 0;	 Catch:{ Exception -> 0x08f9 }
        r2[r4] = r30;	 Catch:{ Exception -> 0x08f9 }
        r0 = r24;	 Catch:{ Exception -> 0x08f9 }
        r25 = java.lang.String.format(r0, r2);	 Catch:{ Exception -> 0x08f9 }
    L_0x045f:
        if (r36 == 0) goto L_0x0900;
    L_0x0461:
        r32 = new android.text.SpannableStringBuilder;
        r2 = "%s via %s";
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r6 = 0;
        r4[r6] = r25;
        r6 = 1;
        r4[r6] = r37;
        r2 = java.lang.String.format(r2, r4);
        r0 = r32;
        r0.<init>(r2);
        r2 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;
        r0 = r25;
        r2 = r2.measureText(r0);
        r8 = (double) r2;
        r8 = java.lang.Math.ceil(r8);
        r2 = (int) r8;
        r0 = r38;
        r0.viaNameWidth = r2;
        r2 = new org.telegram.ui.Components.TypefaceSpan;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r2.<init>(r4);
        r4 = r32.length();
        r6 = r37.length();
        r4 = r4 - r6;
        r4 = r4 + -1;
        r6 = r32.length();
        r8 = 33;
        r0 = r32;
        r0.setSpan(r2, r4, r6, r8);
    L_0x04ac:
        if (r27 < 0) goto L_0x04c9;
    L_0x04ae:
        r2 = new org.telegram.ui.Components.TypefaceSpan;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r2.<init>(r4);
        r4 = r30.length();
        r4 = r4 + r27;
        r6 = 33;
        r0 = r32;
        r1 = r27;
        r0.setSpan(r2, r1, r4, r6);
    L_0x04c9:
        r5 = r32;
        r2 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;
        r0 = r38;
        r4 = r0.forwardedNameWidth;
        r4 = (float) r4;
        r6 = android.text.TextUtils.TruncateAt.END;
        r5 = android.text.TextUtils.ellipsize(r5, r2, r4, r6);
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r2 = r0.forwardedNameLayout;	 Catch:{ Exception -> 0x0915 }
        r12 = 1;	 Catch:{ Exception -> 0x0915 }
        r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0915 }
        r6 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r8 = r0.forwardedNameWidth;	 Catch:{ Exception -> 0x0915 }
        r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0915 }
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Exception -> 0x0915 }
        r7 = r8 + r9;	 Catch:{ Exception -> 0x0915 }
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0915 }
        r9 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0915 }
        r10 = 0;	 Catch:{ Exception -> 0x0915 }
        r11 = 0;	 Catch:{ Exception -> 0x0915 }
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0915 }
        r2[r12] = r4;	 Catch:{ Exception -> 0x0915 }
        r2 = "ForwardedMessage";	 Catch:{ Exception -> 0x0915 }
        r4 = NUM; // 0x7f0c02ef float:1.8610715E38 double:1.0530977695E-314;	 Catch:{ Exception -> 0x0915 }
        r2 = org.telegram.messenger.LocaleController.getString(r2, r4);	 Catch:{ Exception -> 0x0915 }
        r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2);	 Catch:{ Exception -> 0x0915 }
        r4 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r6 = r0.forwardedNameWidth;	 Catch:{ Exception -> 0x0915 }
        r6 = (float) r6;	 Catch:{ Exception -> 0x0915 }
        r8 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0915 }
        r5 = android.text.TextUtils.ellipsize(r2, r4, r6, r8);	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r2 = r0.forwardedNameLayout;	 Catch:{ Exception -> 0x0915 }
        r12 = 0;	 Catch:{ Exception -> 0x0915 }
        r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0915 }
        r6 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r8 = r0.forwardedNameWidth;	 Catch:{ Exception -> 0x0915 }
        r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0915 }
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Exception -> 0x0915 }
        r7 = r8 + r9;	 Catch:{ Exception -> 0x0915 }
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0915 }
        r9 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0915 }
        r10 = 0;	 Catch:{ Exception -> 0x0915 }
        r11 = 0;	 Catch:{ Exception -> 0x0915 }
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0915 }
        r2[r12] = r4;	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r2 = r0.forwardedNameLayout;	 Catch:{ Exception -> 0x0915 }
        r4 = 0;	 Catch:{ Exception -> 0x0915 }
        r2 = r2[r4];	 Catch:{ Exception -> 0x0915 }
        r4 = 0;	 Catch:{ Exception -> 0x0915 }
        r2 = r2.getLineWidth(r4);	 Catch:{ Exception -> 0x0915 }
        r8 = (double) r2;	 Catch:{ Exception -> 0x0915 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0915 }
        r2 = (int) r8;	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r4 = r0.forwardedNameLayout;	 Catch:{ Exception -> 0x0915 }
        r6 = 1;	 Catch:{ Exception -> 0x0915 }
        r4 = r4[r6];	 Catch:{ Exception -> 0x0915 }
        r6 = 0;	 Catch:{ Exception -> 0x0915 }
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x0915 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0915 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0915 }
        r4 = (int) r8;	 Catch:{ Exception -> 0x0915 }
        r2 = java.lang.Math.max(r2, r4);	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r0.forwardedNameWidth = r2;	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r2 = r0.forwardNameOffsetX;	 Catch:{ Exception -> 0x0915 }
        r4 = 0;	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r6 = r0.forwardedNameLayout;	 Catch:{ Exception -> 0x0915 }
        r8 = 0;	 Catch:{ Exception -> 0x0915 }
        r6 = r6[r8];	 Catch:{ Exception -> 0x0915 }
        r8 = 0;	 Catch:{ Exception -> 0x0915 }
        r6 = r6.getLineLeft(r8);	 Catch:{ Exception -> 0x0915 }
        r2[r4] = r6;	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r2 = r0.forwardNameOffsetX;	 Catch:{ Exception -> 0x0915 }
        r4 = 1;	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r6 = r0.forwardedNameLayout;	 Catch:{ Exception -> 0x0915 }
        r8 = 1;	 Catch:{ Exception -> 0x0915 }
        r6 = r6[r8];	 Catch:{ Exception -> 0x0915 }
        r8 = 0;	 Catch:{ Exception -> 0x0915 }
        r6 = r6.getLineLeft(r8);	 Catch:{ Exception -> 0x0915 }
        r2[r4] = r6;	 Catch:{ Exception -> 0x0915 }
        r0 = r39;	 Catch:{ Exception -> 0x0915 }
        r2 = r0.type;	 Catch:{ Exception -> 0x0915 }
        r4 = 5;	 Catch:{ Exception -> 0x0915 }
        if (r2 == r4) goto L_0x059b;	 Catch:{ Exception -> 0x0915 }
    L_0x058c:
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r2 = r0.namesOffset;	 Catch:{ Exception -> 0x0915 }
        r4 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;	 Catch:{ Exception -> 0x0915 }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0915 }
        r2 = r2 + r4;	 Catch:{ Exception -> 0x0915 }
        r0 = r38;	 Catch:{ Exception -> 0x0915 }
        r0.namesOffset = r2;	 Catch:{ Exception -> 0x0915 }
    L_0x059b:
        r2 = r39.hasValidReplyMessageObject();
        if (r2 == 0) goto L_0x077b;
    L_0x05a1:
        r0 = r38;
        r2 = r0.currentPosition;
        if (r2 == 0) goto L_0x05af;
    L_0x05a7:
        r0 = r38;
        r2 = r0.currentPosition;
        r2 = r2.minY;
        if (r2 != 0) goto L_0x077b;
    L_0x05af:
        r0 = r39;
        r2 = r0.type;
        r4 = 13;
        if (r2 == r4) goto L_0x05e2;
    L_0x05b7:
        r0 = r39;
        r2 = r0.type;
        r4 = 5;
        if (r2 == r4) goto L_0x05e2;
    L_0x05be:
        r0 = r38;
        r2 = r0.namesOffset;
        r4 = NUM; // 0x42280000 float:42.0 double:5.483722033E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r38;
        r0.namesOffset = r2;
        r0 = r39;
        r2 = r0.type;
        if (r2 == 0) goto L_0x05e2;
    L_0x05d3:
        r0 = r38;
        r2 = r0.namesOffset;
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r38;
        r0.namesOffset = r2;
    L_0x05e2:
        r28 = r38.getMaxNameWidth();
        r0 = r39;
        r2 = r0.type;
        r4 = 13;
        if (r2 == r4) goto L_0x05fd;
    L_0x05ee:
        r0 = r39;
        r2 = r0.type;
        r4 = 5;
        if (r2 == r4) goto L_0x05fd;
    L_0x05f5:
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r28 = r28 - r2;
    L_0x05fd:
        r33 = 0;
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.photoThumbs2;
        r4 = 80;
        r31 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r4);
        if (r31 != 0) goto L_0x0619;
    L_0x060d:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.photoThumbs;
        r4 = 80;
        r31 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r4);
    L_0x0619:
        if (r31 == 0) goto L_0x063d;
    L_0x061b:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.type;
        r4 = 13;
        if (r2 == r4) goto L_0x063d;
    L_0x0625:
        r0 = r39;
        r2 = r0.type;
        r4 = 13;
        if (r2 != r4) goto L_0x0633;
    L_0x062d:
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x063d;
    L_0x0633:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.isSecretMedia();
        if (r2 == 0) goto L_0x091b;
    L_0x063d:
        r0 = r38;
        r4 = r0.replyImageReceiver;
        r2 = 0;
        r2 = (android.graphics.drawable.Drawable) r2;
        r4.setImageBitmap(r2);
        r2 = 0;
        r0 = r38;
        r0.needReplyImage = r2;
    L_0x064c:
        r30 = 0;
        r0 = r39;
        r2 = r0.customReplyName;
        if (r2 == 0) goto L_0x0963;
    L_0x0654:
        r0 = r39;
        r0 = r0.customReplyName;
        r30 = r0;
    L_0x065a:
        if (r30 != 0) goto L_0x0666;
    L_0x065c:
        r2 = "Loading";
        r4 = NUM; // 0x7f0c03ab float:1.8611096E38 double:1.0530978624E-314;
        r30 = org.telegram.messenger.LocaleController.getString(r2, r4);
    L_0x0666:
        r2 = 10;
        r4 = 32;
        r0 = r30;
        r2 = r0.replace(r2, r4);
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r0 = r28;
        r6 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r7 = android.text.TextUtils.ellipsize(r2, r4, r6, r8);
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.media;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r2 == 0) goto L_0x09de;
    L_0x0687:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.media;
        r2 = r2.game;
        r2 = r2.title;
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r4 = r4.getFontMetricsInt();
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r8 = 0;
        r33 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r6, r8);
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r0 = r28;
        r4 = (float) r0;
        r6 = android.text.TextUtils.TruncateAt.END;
        r0 = r33;
        r33 = android.text.TextUtils.ellipsize(r0, r2, r4, r6);
    L_0x06b1:
        r0 = r38;	 Catch:{ Exception -> 0x0a72 }
        r2 = r0.needReplyImage;	 Catch:{ Exception -> 0x0a72 }
        if (r2 == 0) goto L_0x0a6f;	 Catch:{ Exception -> 0x0a72 }
    L_0x06b7:
        r2 = 44;	 Catch:{ Exception -> 0x0a72 }
    L_0x06b9:
        r2 = r2 + 4;	 Catch:{ Exception -> 0x0a72 }
        r2 = (float) r2;	 Catch:{ Exception -> 0x0a72 }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x0a72 }
        r0 = r38;	 Catch:{ Exception -> 0x0a72 }
        r0.replyNameWidth = r2;	 Catch:{ Exception -> 0x0a72 }
        if (r7 == 0) goto L_0x0715;	 Catch:{ Exception -> 0x0a72 }
    L_0x06c6:
        r6 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0a72 }
        r8 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0a72 }
        r2 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;	 Catch:{ Exception -> 0x0a72 }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x0a72 }
        r9 = r28 + r2;	 Catch:{ Exception -> 0x0a72 }
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0a72 }
        r11 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0a72 }
        r12 = 0;	 Catch:{ Exception -> 0x0a72 }
        r13 = 0;	 Catch:{ Exception -> 0x0a72 }
        r6.<init>(r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0a72 }
        r0 = r38;	 Catch:{ Exception -> 0x0a72 }
        r0.replyNameLayout = r6;	 Catch:{ Exception -> 0x0a72 }
        r0 = r38;	 Catch:{ Exception -> 0x0a72 }
        r2 = r0.replyNameLayout;	 Catch:{ Exception -> 0x0a72 }
        r2 = r2.getLineCount();	 Catch:{ Exception -> 0x0a72 }
        if (r2 <= 0) goto L_0x0715;	 Catch:{ Exception -> 0x0a72 }
    L_0x06e9:
        r0 = r38;	 Catch:{ Exception -> 0x0a72 }
        r2 = r0.replyNameWidth;	 Catch:{ Exception -> 0x0a72 }
        r0 = r38;	 Catch:{ Exception -> 0x0a72 }
        r4 = r0.replyNameLayout;	 Catch:{ Exception -> 0x0a72 }
        r6 = 0;	 Catch:{ Exception -> 0x0a72 }
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x0a72 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0a72 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0a72 }
        r4 = (int) r8;	 Catch:{ Exception -> 0x0a72 }
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;	 Catch:{ Exception -> 0x0a72 }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0a72 }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0a72 }
        r2 = r2 + r4;	 Catch:{ Exception -> 0x0a72 }
        r0 = r38;	 Catch:{ Exception -> 0x0a72 }
        r0.replyNameWidth = r2;	 Catch:{ Exception -> 0x0a72 }
        r0 = r38;	 Catch:{ Exception -> 0x0a72 }
        r2 = r0.replyNameLayout;	 Catch:{ Exception -> 0x0a72 }
        r4 = 0;	 Catch:{ Exception -> 0x0a72 }
        r2 = r2.getLineLeft(r4);	 Catch:{ Exception -> 0x0a72 }
        r0 = r38;	 Catch:{ Exception -> 0x0a72 }
        r0.replyNameOffset = r2;	 Catch:{ Exception -> 0x0a72 }
    L_0x0715:
        r0 = r38;	 Catch:{ Exception -> 0x0a7b }
        r2 = r0.needReplyImage;	 Catch:{ Exception -> 0x0a7b }
        if (r2 == 0) goto L_0x0a78;	 Catch:{ Exception -> 0x0a7b }
    L_0x071b:
        r2 = 44;	 Catch:{ Exception -> 0x0a7b }
    L_0x071d:
        r2 = r2 + 4;	 Catch:{ Exception -> 0x0a7b }
        r2 = (float) r2;	 Catch:{ Exception -> 0x0a7b }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x0a7b }
        r0 = r38;	 Catch:{ Exception -> 0x0a7b }
        r0.replyTextWidth = r2;	 Catch:{ Exception -> 0x0a7b }
        if (r33 == 0) goto L_0x077b;	 Catch:{ Exception -> 0x0a7b }
    L_0x072a:
        r8 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0a7b }
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0a7b }
        r2 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;	 Catch:{ Exception -> 0x0a7b }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x0a7b }
        r11 = r28 + r2;	 Catch:{ Exception -> 0x0a7b }
        r12 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0a7b }
        r13 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0a7b }
        r14 = 0;	 Catch:{ Exception -> 0x0a7b }
        r15 = 0;	 Catch:{ Exception -> 0x0a7b }
        r9 = r33;	 Catch:{ Exception -> 0x0a7b }
        r8.<init>(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ Exception -> 0x0a7b }
        r0 = r38;	 Catch:{ Exception -> 0x0a7b }
        r0.replyTextLayout = r8;	 Catch:{ Exception -> 0x0a7b }
        r0 = r38;	 Catch:{ Exception -> 0x0a7b }
        r2 = r0.replyTextLayout;	 Catch:{ Exception -> 0x0a7b }
        r2 = r2.getLineCount();	 Catch:{ Exception -> 0x0a7b }
        if (r2 <= 0) goto L_0x077b;	 Catch:{ Exception -> 0x0a7b }
    L_0x074f:
        r0 = r38;	 Catch:{ Exception -> 0x0a7b }
        r2 = r0.replyTextWidth;	 Catch:{ Exception -> 0x0a7b }
        r0 = r38;	 Catch:{ Exception -> 0x0a7b }
        r4 = r0.replyTextLayout;	 Catch:{ Exception -> 0x0a7b }
        r6 = 0;	 Catch:{ Exception -> 0x0a7b }
        r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x0a7b }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0a7b }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0a7b }
        r4 = (int) r8;	 Catch:{ Exception -> 0x0a7b }
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;	 Catch:{ Exception -> 0x0a7b }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0a7b }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x0a7b }
        r2 = r2 + r4;	 Catch:{ Exception -> 0x0a7b }
        r0 = r38;	 Catch:{ Exception -> 0x0a7b }
        r0.replyTextWidth = r2;	 Catch:{ Exception -> 0x0a7b }
        r0 = r38;	 Catch:{ Exception -> 0x0a7b }
        r2 = r0.replyTextLayout;	 Catch:{ Exception -> 0x0a7b }
        r4 = 0;	 Catch:{ Exception -> 0x0a7b }
        r2 = r2.getLineLeft(r4);	 Catch:{ Exception -> 0x0a7b }
        r0 = r38;	 Catch:{ Exception -> 0x0a7b }
        r0.replyTextOffset = r2;	 Catch:{ Exception -> 0x0a7b }
    L_0x077b:
        r38.requestLayout();
        return;
    L_0x077f:
        r2 = 0;
        r0 = r38;
        r0.currentPhoto = r2;
        goto L_0x004d;
    L_0x0786:
        r0 = r38;
        r2 = r0.currentChat;
        if (r2 == 0) goto L_0x07b3;
    L_0x078c:
        r0 = r38;
        r2 = r0.currentChat;
        r2 = r2.photo;
        if (r2 == 0) goto L_0x07ad;
    L_0x0794:
        r0 = r38;
        r2 = r0.currentChat;
        r2 = r2.photo;
        r2 = r2.photo_small;
        r0 = r38;
        r0.currentPhoto = r2;
    L_0x07a0:
        r0 = r38;
        r2 = r0.avatarDrawable;
        r0 = r38;
        r4 = r0.currentChat;
        r2.setInfo(r4);
        goto L_0x0058;
    L_0x07ad:
        r2 = 0;
        r0 = r38;
        r0.currentPhoto = r2;
        goto L_0x07a0;
    L_0x07b3:
        r2 = 0;
        r0 = r38;
        r0.currentPhoto = r2;
        r0 = r38;
        r2 = r0.avatarDrawable;
        r0 = r39;
        r4 = r0.messageOwner;
        r4 = r4.from_id;
        r6 = 0;
        r8 = 0;
        r9 = 0;
        r2.setInfo(r4, r6, r8, r9);
        goto L_0x0058;
    L_0x07ca:
        r0 = r39;
        r2 = r0.messageOwner;
        r2 = r2.via_bot_name;
        if (r2 == 0) goto L_0x00ee;
    L_0x07d2:
        r0 = r39;
        r2 = r0.messageOwner;
        r2 = r2.via_bot_name;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x00ee;
    L_0x07de:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "@";
        r2 = r2.append(r4);
        r0 = r39;
        r4 = r0.messageOwner;
        r4 = r4.via_bot_name;
        r2 = r2.append(r4);
        r37 = r2.toString();
        r2 = " via <b>%s</b>";
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r6 = 0;
        r4[r6] = r37;
        r2 = java.lang.String.format(r2, r4);
        r36 = org.telegram.messenger.AndroidUtilities.replaceTags(r2);
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r4 = 0;
        r6 = r36.length();
        r0 = r36;
        r2 = r2.measureText(r0, r4, r6);
        r8 = (double) r2;
        r8 = java.lang.Math.ceil(r8);
        r2 = (int) r8;
        r0 = r38;
        r0.viaWidth = r2;
        goto L_0x00ee;
    L_0x0822:
        r18 = 0;
        goto L_0x0106;
    L_0x0826:
        r35 = 0;
        goto L_0x011a;
    L_0x082a:
        r16 = 0;
        r17 = 0;
        goto L_0x0192;
    L_0x0830:
        r0 = r38;
        r2 = r0.currentChat;
        if (r2 == 0) goto L_0x0842;
    L_0x0836:
        r0 = r38;
        r2 = r0.currentChat;
        r2 = r2.title;
        r0 = r38;
        r0.currentNameString = r2;
        goto L_0x01a6;
    L_0x0842:
        r2 = "DELETED";
        r0 = r38;
        r0.currentNameString = r2;
        goto L_0x01a6;
    L_0x084b:
        r2 = "";
        r0 = r38;
        r0.currentNameString = r2;
        goto L_0x01a6;
    L_0x0854:
        r2 = 0;
        goto L_0x01be;
    L_0x0857:
        r0 = r38;
        r2 = r0.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x086a;
    L_0x0861:
        r2 = "chat_outViaBotNameText";
    L_0x0864:
        r21 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        goto L_0x020d;
    L_0x086a:
        r2 = "chat_inViaBotNameText";
        goto L_0x0864;
    L_0x086e:
        r32 = new android.text.SpannableStringBuilder;
        r2 = "via %s";
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r6 = 0;
        r4[r6] = r37;
        r2 = java.lang.String.format(r2, r4);
        r0 = r32;
        r0.<init>(r2);
        r2 = new org.telegram.ui.Components.TypefaceSpan;
        r4 = android.graphics.Typeface.DEFAULT;
        r6 = 0;
        r0 = r21;
        r2.<init>(r4, r6, r0);
        r4 = 0;
        r6 = 4;
        r8 = 33;
        r0 = r32;
        r0.setSpan(r2, r4, r6, r8);
        r2 = new org.telegram.ui.Components.TypefaceSpan;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r6 = 0;
        r0 = r21;
        r2.<init>(r4, r6, r0);
        r4 = 4;
        r6 = r32.length();
        r8 = 33;
        r0 = r32;
        r0.setSpan(r2, r4, r6, r8);
        r3 = r32;
        goto L_0x026d;
    L_0x08b4:
        r2 = 0;
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r0.nameWidth = r2;	 Catch:{ Exception -> 0x08bb }
        goto L_0x02de;
    L_0x08bb:
        r22 = move-exception;
        org.telegram.messenger.FileLog.m3e(r22);
        goto L_0x0317;
    L_0x08c1:
        r2 = 0;
        r0 = r38;	 Catch:{ Exception -> 0x08bb }
        r0.adminLayout = r2;	 Catch:{ Exception -> 0x08bb }
        goto L_0x0317;
    L_0x08c8:
        r2 = 0;
        r0 = r38;
        r0.currentNameString = r2;
        r2 = 0;
        r0 = r38;
        r0.nameLayout = r2;
        r2 = 0;
        r0 = r38;
        r0.nameWidth = r2;
        goto L_0x0326;
    L_0x08d9:
        r0 = r38;
        r2 = r0.currentForwardChannel;
        r2 = r2.title;
        r0 = r38;
        r0.currentForwardNameString = r2;
        goto L_0x03ea;
    L_0x08e5:
        r0 = r38;
        r2 = r0.currentForwardUser;
        if (r2 == 0) goto L_0x03ea;
    L_0x08eb:
        r0 = r38;
        r2 = r0.currentForwardUser;
        r2 = org.telegram.messenger.UserObject.getUserName(r2);
        r0 = r38;
        r0.currentForwardNameString = r2;
        goto L_0x03ea;
    L_0x08f9:
        r22 = move-exception;
        r25 = r30.toString();
        goto L_0x045f;
    L_0x0900:
        r32 = new android.text.SpannableStringBuilder;
        r2 = 1;
        r2 = new java.lang.Object[r2];
        r4 = 0;
        r2[r4] = r30;
        r0 = r24;
        r2 = java.lang.String.format(r0, r2);
        r0 = r32;
        r0.<init>(r2);
        goto L_0x04ac;
    L_0x0915:
        r22 = move-exception;
        org.telegram.messenger.FileLog.m3e(r22);
        goto L_0x059b;
    L_0x091b:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.isRoundVideo();
        if (r2 == 0) goto L_0x095a;
    L_0x0925:
        r0 = r38;
        r2 = r0.replyImageReceiver;
        r4 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2.setRoundRadius(r4);
    L_0x0932:
        r0 = r31;
        r2 = r0.location;
        r0 = r38;
        r0.currentReplyPhoto = r2;
        r0 = r38;
        r6 = r0.replyImageReceiver;
        r0 = r31;
        r7 = r0.location;
        r8 = "50_50";
        r9 = 0;
        r10 = 0;
        r11 = 1;
        r6.setImage(r7, r8, r9, r10, r11);
        r2 = 1;
        r0 = r38;
        r0.needReplyImage = r2;
        r2 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r28 = r28 - r2;
        goto L_0x064c;
    L_0x095a:
        r0 = r38;
        r2 = r0.replyImageReceiver;
        r4 = 0;
        r2.setRoundRadius(r4);
        goto L_0x0932;
    L_0x0963:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.isFromUser();
        if (r2 == 0) goto L_0x098d;
    L_0x096d:
        r0 = r38;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r0 = r39;
        r4 = r0.replyMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.from_id;
        r4 = java.lang.Integer.valueOf(r4);
        r34 = r2.getUser(r4);
        if (r34 == 0) goto L_0x065a;
    L_0x0987:
        r30 = org.telegram.messenger.UserObject.getUserName(r34);
        goto L_0x065a;
    L_0x098d:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.from_id;
        if (r2 >= 0) goto L_0x09ba;
    L_0x0997:
        r0 = r38;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r0 = r39;
        r4 = r0.replyMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.from_id;
        r4 = -r4;
        r4 = java.lang.Integer.valueOf(r4);
        r20 = r2.getChat(r4);
        if (r20 == 0) goto L_0x065a;
    L_0x09b2:
        r0 = r20;
        r0 = r0.title;
        r30 = r0;
        goto L_0x065a;
    L_0x09ba:
        r0 = r38;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r0 = r39;
        r4 = r0.replyMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.to_id;
        r4 = r4.channel_id;
        r4 = java.lang.Integer.valueOf(r4);
        r20 = r2.getChat(r4);
        if (r20 == 0) goto L_0x065a;
    L_0x09d6:
        r0 = r20;
        r0 = r0.title;
        r30 = r0;
        goto L_0x065a;
    L_0x09de:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.media;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
        if (r2 == 0) goto L_0x0a14;
    L_0x09ea:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.media;
        r2 = r2.title;
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r4 = r4.getFontMetricsInt();
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r8 = 0;
        r33 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r6, r8);
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r0 = r28;
        r4 = (float) r0;
        r6 = android.text.TextUtils.TruncateAt.END;
        r0 = r33;
        r33 = android.text.TextUtils.ellipsize(r0, r2, r4, r6);
        goto L_0x06b1;
    L_0x0a14:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.messageText;
        if (r2 == 0) goto L_0x06b1;
    L_0x0a1c:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.messageText;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x06b1;
    L_0x0a28:
        r0 = r39;
        r2 = r0.replyMessageObject;
        r2 = r2.messageText;
        r29 = r2.toString();
        r2 = r29.length();
        r4 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        if (r2 <= r4) goto L_0x0a43;
    L_0x0a3a:
        r2 = 0;
        r4 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r0 = r29;
        r29 = r0.substring(r2, r4);
    L_0x0a43:
        r2 = 10;
        r4 = 32;
        r0 = r29;
        r29 = r0.replace(r2, r4);
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r2 = r2.getFontMetricsInt();
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = 0;
        r0 = r29;
        r33 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r4, r6);
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r0 = r28;
        r4 = (float) r0;
        r6 = android.text.TextUtils.TruncateAt.END;
        r0 = r33;
        r33 = android.text.TextUtils.ellipsize(r0, r2, r4, r6);
        goto L_0x06b1;
    L_0x0a6f:
        r2 = 0;
        goto L_0x06b9;
    L_0x0a72:
        r22 = move-exception;
        org.telegram.messenger.FileLog.m3e(r22);
        goto L_0x0715;
    L_0x0a78:
        r2 = 0;
        goto L_0x071d;
    L_0x0a7b:
        r22 = move-exception;
        org.telegram.messenger.FileLog.m3e(r22);
        goto L_0x077b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageObjectInternal(org.telegram.messenger.MessageObject):void");
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
                    Theme.chat_shareDrawable.setColorFilter(this.sharePressed ? Theme.colorPressedFilter : Theme.colorFilter);
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
                        BaseCell.setDrawableBounds(Theme.chat_shareIconDrawable, this.shareStartX + AndroidUtilities.dp(9.0f), this.shareStartY + AndroidUtilities.dp(9.0f));
                        Theme.chat_shareIconDrawable.draw(canvas);
                    }
                }
                if (this.currentPosition == null) {
                    drawNamesLayout(canvas);
                }
                if ((this.drawTime || !this.mediaBackground) && !this.forceNotDrawTime) {
                    drawTimeLayout(canvas);
                }
                if (this.controlsAlpha != 1.0f || this.timeAlpha != 1.0f) {
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
        int i;
        float f2 = 11.0f;
        int i2 = 0;
        if (this.drawNameLayout && this.nameLayout != null) {
            canvas.save();
            if (this.currentMessageObject.type == 13 || this.currentMessageObject.type == 5) {
                Theme.chat_namePaint.setColor(Theme.getColor(Theme.key_chat_stickerNameText));
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
                    int i3 = this.backgroundDrawableLeft;
                    f = (this.mediaBackground || !this.drawPinnedBottom) ? 17.0f : 11.0f;
                    this.nameX = ((float) (AndroidUtilities.dp(f) + i3)) - this.nameOffsetX;
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
                Theme.chat_adminPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_adminSelectedText : Theme.key_chat_adminText));
                canvas.save();
                canvas.translate(((float) ((this.backgroundDrawableLeft + this.backgroundDrawableRight) - AndroidUtilities.dp(11.0f))) - this.adminLayout.getLineWidth(0), this.nameY + ((float) AndroidUtilities.dp(0.5f)));
                this.adminLayout.draw(canvas);
                canvas.restore();
            }
        }
        if (this.drawForwardedName && this.forwardedNameLayout[0] != null && this.forwardedNameLayout[1] != null && (this.currentPosition == null || (this.currentPosition.minY == (byte) 0 && this.currentPosition.minX == (byte) 0))) {
            if (this.currentMessageObject.type == 5) {
                Theme.chat_forwardNamePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyNameText));
                if (this.currentMessageObject.isOutOwner()) {
                    this.forwardNameX = AndroidUtilities.dp(23.0f);
                } else {
                    this.forwardNameX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.dp(17.0f);
                }
                this.forwardNameY = AndroidUtilities.dp(12.0f);
                int backWidth = this.forwardedNameWidth + AndroidUtilities.dp(14.0f);
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(this.forwardNameX - AndroidUtilities.dp(7.0f), this.forwardNameY - AndroidUtilities.dp(6.0f), (this.forwardNameX - AndroidUtilities.dp(7.0f)) + backWidth, this.forwardNameY + AndroidUtilities.dp(38.0f));
                Theme.chat_systemDrawable.draw(canvas);
            } else {
                if (this.drawNameLayout) {
                    i = 19;
                } else {
                    i = 0;
                }
                this.forwardNameY = AndroidUtilities.dp((float) (i + 10));
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_forwardNamePaint.setColor(Theme.getColor(Theme.key_chat_outForwardedNameText));
                    this.forwardNameX = this.backgroundDrawableLeft + AndroidUtilities.dp(11.0f);
                } else {
                    Theme.chat_forwardNamePaint.setColor(Theme.getColor(Theme.key_chat_inForwardedNameText));
                    if (this.mediaBackground) {
                        this.forwardNameX = this.backgroundDrawableLeft + AndroidUtilities.dp(11.0f);
                    } else {
                        i = this.backgroundDrawableLeft;
                        if (this.mediaBackground || !this.drawPinnedBottom) {
                            f2 = 17.0f;
                        }
                        this.forwardNameX = i + AndroidUtilities.dp(f2);
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
                Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyLine));
                Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyNameText));
                Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyMessageText));
                if (this.currentMessageObject.isOutOwner()) {
                    this.replyStartX = AndroidUtilities.dp(23.0f);
                } else {
                    this.replyStartX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.dp(17.0f);
                }
                this.replyStartY = AndroidUtilities.dp(12.0f);
                backWidth = Math.max(this.replyNameWidth, this.replyTextWidth) + AndroidUtilities.dp(14.0f);
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(this.replyStartX - AndroidUtilities.dp(7.0f), this.replyStartY - AndroidUtilities.dp(6.0f), (this.replyStartX - AndroidUtilities.dp(7.0f)) + backWidth, this.replyStartY + AndroidUtilities.dp(41.0f));
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
                    this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f);
                } else {
                    Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_inReplyLine));
                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_inReplyNameText));
                    if (!this.currentMessageObject.hasValidReplyMessageObject() || this.currentMessageObject.replyMessageObject.type != 0 || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                        Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inReplyMediaMessageSelectedText : Theme.key_chat_inReplyMediaMessageText));
                    } else {
                        Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_inReplyMessageText));
                    }
                    if (this.mediaBackground) {
                        this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f);
                    } else {
                        i4 = this.backgroundDrawableLeft;
                        f = (this.mediaBackground || !this.drawPinnedBottom) ? 18.0f : 12.0f;
                        this.replyStartX = AndroidUtilities.dp(f) + i4;
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
                this.replyStartY = AndroidUtilities.dp((float) (i + i4));
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
                        i2 = 44;
                    }
                    canvas.translate(f + ((float) AndroidUtilities.dp((float) (i2 + 10))), (float) (this.replyStartY + AndroidUtilities.dp(19.0f)));
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
                    FileLog.m3e(e);
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
                        BaseCell.setDrawableBounds(Theme.chat_msgStickerClockDrawable, (this.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicHeight());
                        Theme.chat_msgStickerClockDrawable.draw(canvas);
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
