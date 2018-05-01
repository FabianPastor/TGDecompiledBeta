package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
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
import java.util.Locale;
import net.hockeyapp.android.UpdateFragment;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
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
    private boolean instantPressed;
    private int instantTextLeftX;
    private int instantTextX;
    private StaticLayout instantViewLayout;
    private Drawable instantViewSelectorDrawable;
    private int instantWidth;
    private Runnable invalidateRunnable = new C08721();
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
    class C08721 implements Runnable {
        C08721() {
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

    private boolean intersect(float f, float f2, float f3, float f4) {
        boolean z = false;
        if (f <= f3) {
            if (f2 >= f3) {
                z = true;
            }
            return z;
        }
        if (f <= f4) {
            z = true;
        }
        return z;
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

    private void resetPressedLink(int i) {
        if (this.pressedLink != null) {
            if (this.pressedLinkType == i || i == -1) {
                resetUrlPaths(0);
                this.pressedLink = 0;
                this.pressedLinkType = -1;
                invalidate();
            }
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
        if (this.urlPathCache.isEmpty()) {
            linkPath = new LinkPath();
        } else {
            linkPath = (LinkPath) this.urlPathCache.get(0);
            this.urlPathCache.remove(0);
        }
        if (z) {
            this.urlPathSelection.add(linkPath);
        } else {
            this.urlPath.add(linkPath);
        }
        return linkPath;
    }

    private boolean checkTextBlockMotionEvent(MotionEvent motionEvent) {
        if (!(this.currentMessageObject.type != 0 || this.currentMessageObject.textLayoutBlocks == null || this.currentMessageObject.textLayoutBlocks.isEmpty())) {
            if (this.currentMessageObject.messageText instanceof Spannable) {
                if (motionEvent.getAction() == 0 || (motionEvent.getAction() == 1 && this.pressedLinkType == 1)) {
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY();
                    if (x < this.textX || y < this.textY || x > this.textX + this.currentMessageObject.textWidth || y > this.textY + this.currentMessageObject.textHeight) {
                        resetPressedLink(1);
                    } else {
                        y -= this.textY;
                        int i = 0;
                        int i2 = i;
                        while (i < this.currentMessageObject.textLayoutBlocks.size()) {
                            if (((TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i)).textYOffset > ((float) y)) {
                                break;
                            }
                            i2 = i;
                            i++;
                        }
                        try {
                            TextLayoutBlock textLayoutBlock = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i2);
                            x = (int) (((float) x) - (((float) this.textX) - (textLayoutBlock.isRtl() ? this.currentMessageObject.textXOffset : 0.0f)));
                            y = textLayoutBlock.textLayout.getLineForVertical((int) (((float) y) - textLayoutBlock.textYOffset));
                            float f = (float) x;
                            int offsetForHorizontal = textLayoutBlock.textLayout.getOffsetForHorizontal(y, f);
                            float lineLeft = textLayoutBlock.textLayout.getLineLeft(y);
                            if (lineLeft <= f && lineLeft + textLayoutBlock.textLayout.getLineWidth(y) >= f) {
                                boolean z;
                                boolean z2;
                                int spanEnd;
                                TextLayoutBlock textLayoutBlock2;
                                CharacterStyle[] characterStyleArr;
                                Path obtainNewUrlPath;
                                TextLayoutBlock textLayoutBlock3;
                                CharacterStyle[] characterStyleArr2;
                                Path obtainNewUrlPath2;
                                Spannable spannable = (Spannable) this.currentMessageObject.messageText;
                                CharacterStyle[] characterStyleArr3 = (CharacterStyle[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                                if (characterStyleArr3 != null) {
                                    if (characterStyleArr3.length != 0) {
                                        z = false;
                                        if (characterStyleArr3.length != 0) {
                                            if (characterStyleArr3.length != 0 || !(characterStyleArr3[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled) {
                                                z2 = false;
                                                if (!z2) {
                                                    if (motionEvent.getAction() != null) {
                                                        this.pressedLink = characterStyleArr3[0];
                                                        this.linkBlockNum = i2;
                                                        this.pressedLinkType = 1;
                                                        resetUrlPaths(false);
                                                        try {
                                                            motionEvent = obtainNewUrlPath(false);
                                                            y = spannable.getSpanStart(this.pressedLink);
                                                            spanEnd = spannable.getSpanEnd(this.pressedLink);
                                                            motionEvent.setCurrentLayout(textLayoutBlock.textLayout, y, 0.0f);
                                                            textLayoutBlock.textLayout.getSelectionPath(y, spanEnd, motionEvent);
                                                            if (spanEnd >= textLayoutBlock.charactersEnd) {
                                                                while (motionEvent < this.currentMessageObject.textLayoutBlocks.size()) {
                                                                    textLayoutBlock2 = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(motionEvent);
                                                                    characterStyleArr = (CharacterStyle[]) spannable.getSpans(textLayoutBlock2.charactersOffset, textLayoutBlock2.charactersOffset, z ? URLSpanMono.class : ClickableSpan.class);
                                                                    if (characterStyleArr == null || characterStyleArr.length == 0) {
                                                                        break;
                                                                    } else if (characterStyleArr[0] == this.pressedLink) {
                                                                        break;
                                                                    } else {
                                                                        obtainNewUrlPath = obtainNewUrlPath(false);
                                                                        obtainNewUrlPath.setCurrentLayout(textLayoutBlock2.textLayout, 0, textLayoutBlock2.textYOffset - textLayoutBlock.textYOffset);
                                                                        textLayoutBlock2.textLayout.getSelectionPath(0, spanEnd, obtainNewUrlPath);
                                                                        if (spanEnd >= textLayoutBlock2.charactersEnd - 1) {
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            if (y <= textLayoutBlock.charactersOffset) {
                                                                motionEvent = null;
                                                                while (i2 >= 0) {
                                                                    textLayoutBlock3 = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i2);
                                                                    characterStyleArr2 = (CharacterStyle[]) spannable.getSpans(textLayoutBlock3.charactersEnd - 1, textLayoutBlock3.charactersEnd - 1, z ? URLSpanMono.class : ClickableSpan.class);
                                                                    if (characterStyleArr2 == null || characterStyleArr2.length == 0) {
                                                                        break;
                                                                    } else if (characterStyleArr2[0] == this.pressedLink) {
                                                                        break;
                                                                    } else {
                                                                        obtainNewUrlPath2 = obtainNewUrlPath(false);
                                                                        spanEnd = spannable.getSpanStart(this.pressedLink);
                                                                        motionEvent -= textLayoutBlock3.height;
                                                                        obtainNewUrlPath2.setCurrentLayout(textLayoutBlock3.textLayout, spanEnd, (float) motionEvent);
                                                                        textLayoutBlock3.textLayout.getSelectionPath(spanEnd, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath2);
                                                                        if (spanEnd <= textLayoutBlock3.charactersOffset) {
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } catch (Throwable e) {
                                                            FileLog.m3e(e);
                                                        }
                                                        invalidate();
                                                        return true;
                                                    } else if (characterStyleArr3[0] == this.pressedLink) {
                                                        this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
                                                        resetPressedLink(1);
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                        z2 = true;
                                        if (z2) {
                                            if (motionEvent.getAction() != null) {
                                                this.pressedLink = characterStyleArr3[0];
                                                this.linkBlockNum = i2;
                                                this.pressedLinkType = 1;
                                                resetUrlPaths(false);
                                                motionEvent = obtainNewUrlPath(false);
                                                y = spannable.getSpanStart(this.pressedLink);
                                                spanEnd = spannable.getSpanEnd(this.pressedLink);
                                                motionEvent.setCurrentLayout(textLayoutBlock.textLayout, y, 0.0f);
                                                textLayoutBlock.textLayout.getSelectionPath(y, spanEnd, motionEvent);
                                                if (spanEnd >= textLayoutBlock.charactersEnd) {
                                                    for (motionEvent = i2 + 1; motionEvent < this.currentMessageObject.textLayoutBlocks.size(); motionEvent++) {
                                                        textLayoutBlock2 = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(motionEvent);
                                                        if (z) {
                                                        }
                                                        characterStyleArr = (CharacterStyle[]) spannable.getSpans(textLayoutBlock2.charactersOffset, textLayoutBlock2.charactersOffset, z ? URLSpanMono.class : ClickableSpan.class);
                                                        if (characterStyleArr[0] == this.pressedLink) {
                                                            break;
                                                        }
                                                        obtainNewUrlPath = obtainNewUrlPath(false);
                                                        obtainNewUrlPath.setCurrentLayout(textLayoutBlock2.textLayout, 0, textLayoutBlock2.textYOffset - textLayoutBlock.textYOffset);
                                                        textLayoutBlock2.textLayout.getSelectionPath(0, spanEnd, obtainNewUrlPath);
                                                        if (spanEnd >= textLayoutBlock2.charactersEnd - 1) {
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (y <= textLayoutBlock.charactersOffset) {
                                                    motionEvent = null;
                                                    for (i2--; i2 >= 0; i2--) {
                                                        textLayoutBlock3 = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i2);
                                                        if (z) {
                                                        }
                                                        characterStyleArr2 = (CharacterStyle[]) spannable.getSpans(textLayoutBlock3.charactersEnd - 1, textLayoutBlock3.charactersEnd - 1, z ? URLSpanMono.class : ClickableSpan.class);
                                                        if (characterStyleArr2[0] == this.pressedLink) {
                                                            break;
                                                        }
                                                        obtainNewUrlPath2 = obtainNewUrlPath(false);
                                                        spanEnd = spannable.getSpanStart(this.pressedLink);
                                                        motionEvent -= textLayoutBlock3.height;
                                                        obtainNewUrlPath2.setCurrentLayout(textLayoutBlock3.textLayout, spanEnd, (float) motionEvent);
                                                        textLayoutBlock3.textLayout.getSelectionPath(spanEnd, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath2);
                                                        if (spanEnd <= textLayoutBlock3.charactersOffset) {
                                                            break;
                                                        }
                                                    }
                                                }
                                                invalidate();
                                                return true;
                                            } else if (characterStyleArr3[0] == this.pressedLink) {
                                                this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
                                                resetPressedLink(1);
                                                return true;
                                            }
                                        }
                                    }
                                }
                                characterStyleArr3 = (CharacterStyle[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, URLSpanMono.class);
                                z = true;
                                if (characterStyleArr3.length != 0) {
                                    if (characterStyleArr3.length != 0) {
                                    }
                                    z2 = false;
                                    if (z2) {
                                        if (motionEvent.getAction() != null) {
                                            this.pressedLink = characterStyleArr3[0];
                                            this.linkBlockNum = i2;
                                            this.pressedLinkType = 1;
                                            resetUrlPaths(false);
                                            motionEvent = obtainNewUrlPath(false);
                                            y = spannable.getSpanStart(this.pressedLink);
                                            spanEnd = spannable.getSpanEnd(this.pressedLink);
                                            motionEvent.setCurrentLayout(textLayoutBlock.textLayout, y, 0.0f);
                                            textLayoutBlock.textLayout.getSelectionPath(y, spanEnd, motionEvent);
                                            if (spanEnd >= textLayoutBlock.charactersEnd) {
                                                for (motionEvent = i2 + 1; motionEvent < this.currentMessageObject.textLayoutBlocks.size(); motionEvent++) {
                                                    textLayoutBlock2 = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(motionEvent);
                                                    if (z) {
                                                    }
                                                    characterStyleArr = (CharacterStyle[]) spannable.getSpans(textLayoutBlock2.charactersOffset, textLayoutBlock2.charactersOffset, z ? URLSpanMono.class : ClickableSpan.class);
                                                    if (characterStyleArr[0] == this.pressedLink) {
                                                        break;
                                                    }
                                                    obtainNewUrlPath = obtainNewUrlPath(false);
                                                    obtainNewUrlPath.setCurrentLayout(textLayoutBlock2.textLayout, 0, textLayoutBlock2.textYOffset - textLayoutBlock.textYOffset);
                                                    textLayoutBlock2.textLayout.getSelectionPath(0, spanEnd, obtainNewUrlPath);
                                                    if (spanEnd >= textLayoutBlock2.charactersEnd - 1) {
                                                        break;
                                                    }
                                                }
                                            }
                                            if (y <= textLayoutBlock.charactersOffset) {
                                                motionEvent = null;
                                                for (i2--; i2 >= 0; i2--) {
                                                    textLayoutBlock3 = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i2);
                                                    if (z) {
                                                    }
                                                    characterStyleArr2 = (CharacterStyle[]) spannable.getSpans(textLayoutBlock3.charactersEnd - 1, textLayoutBlock3.charactersEnd - 1, z ? URLSpanMono.class : ClickableSpan.class);
                                                    if (characterStyleArr2[0] == this.pressedLink) {
                                                        break;
                                                    }
                                                    obtainNewUrlPath2 = obtainNewUrlPath(false);
                                                    spanEnd = spannable.getSpanStart(this.pressedLink);
                                                    motionEvent -= textLayoutBlock3.height;
                                                    obtainNewUrlPath2.setCurrentLayout(textLayoutBlock3.textLayout, spanEnd, (float) motionEvent);
                                                    textLayoutBlock3.textLayout.getSelectionPath(spanEnd, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath2);
                                                    if (spanEnd <= textLayoutBlock3.charactersOffset) {
                                                        break;
                                                    }
                                                }
                                            }
                                            invalidate();
                                            return true;
                                        } else if (characterStyleArr3[0] == this.pressedLink) {
                                            this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
                                            resetPressedLink(1);
                                            return true;
                                        }
                                    }
                                }
                                z2 = true;
                                if (z2) {
                                    if (motionEvent.getAction() != null) {
                                        this.pressedLink = characterStyleArr3[0];
                                        this.linkBlockNum = i2;
                                        this.pressedLinkType = 1;
                                        resetUrlPaths(false);
                                        motionEvent = obtainNewUrlPath(false);
                                        y = spannable.getSpanStart(this.pressedLink);
                                        spanEnd = spannable.getSpanEnd(this.pressedLink);
                                        motionEvent.setCurrentLayout(textLayoutBlock.textLayout, y, 0.0f);
                                        textLayoutBlock.textLayout.getSelectionPath(y, spanEnd, motionEvent);
                                        if (spanEnd >= textLayoutBlock.charactersEnd) {
                                            for (motionEvent = i2 + 1; motionEvent < this.currentMessageObject.textLayoutBlocks.size(); motionEvent++) {
                                                textLayoutBlock2 = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(motionEvent);
                                                if (z) {
                                                }
                                                characterStyleArr = (CharacterStyle[]) spannable.getSpans(textLayoutBlock2.charactersOffset, textLayoutBlock2.charactersOffset, z ? URLSpanMono.class : ClickableSpan.class);
                                                if (characterStyleArr[0] == this.pressedLink) {
                                                    break;
                                                }
                                                obtainNewUrlPath = obtainNewUrlPath(false);
                                                obtainNewUrlPath.setCurrentLayout(textLayoutBlock2.textLayout, 0, textLayoutBlock2.textYOffset - textLayoutBlock.textYOffset);
                                                textLayoutBlock2.textLayout.getSelectionPath(0, spanEnd, obtainNewUrlPath);
                                                if (spanEnd >= textLayoutBlock2.charactersEnd - 1) {
                                                    break;
                                                }
                                            }
                                        }
                                        if (y <= textLayoutBlock.charactersOffset) {
                                            motionEvent = null;
                                            for (i2--; i2 >= 0; i2--) {
                                                textLayoutBlock3 = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i2);
                                                if (z) {
                                                }
                                                characterStyleArr2 = (CharacterStyle[]) spannable.getSpans(textLayoutBlock3.charactersEnd - 1, textLayoutBlock3.charactersEnd - 1, z ? URLSpanMono.class : ClickableSpan.class);
                                                if (characterStyleArr2[0] == this.pressedLink) {
                                                    break;
                                                }
                                                obtainNewUrlPath2 = obtainNewUrlPath(false);
                                                spanEnd = spannable.getSpanStart(this.pressedLink);
                                                motionEvent -= textLayoutBlock3.height;
                                                obtainNewUrlPath2.setCurrentLayout(textLayoutBlock3.textLayout, spanEnd, (float) motionEvent);
                                                textLayoutBlock3.textLayout.getSelectionPath(spanEnd, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath2);
                                                if (spanEnd <= textLayoutBlock3.charactersOffset) {
                                                    break;
                                                }
                                            }
                                        }
                                        invalidate();
                                        return true;
                                    } else if (characterStyleArr3[0] == this.pressedLink) {
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
        }
        return false;
    }

    private boolean checkCaptionMotionEvent(MotionEvent motionEvent) {
        if (this.currentCaption instanceof Spannable) {
            if (this.captionLayout != null) {
                if (motionEvent.getAction() == 0 || ((this.linkPreviewPressed || this.pressedLink != null) && motionEvent.getAction() == 1)) {
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY();
                    if (x < this.captionX || x > this.captionX + this.captionWidth || y < this.captionY || y > this.captionY + this.captionHeight) {
                        resetPressedLink(3);
                    } else if (motionEvent.getAction() == null) {
                        try {
                            x -= this.captionX;
                            motionEvent = this.captionLayout.getLineForVertical(y - this.captionY);
                            float f = (float) x;
                            y = this.captionLayout.getOffsetForHorizontal(motionEvent, f);
                            float lineLeft = this.captionLayout.getLineLeft(motionEvent);
                            if (lineLeft <= f && lineLeft + this.captionLayout.getLineWidth(motionEvent) >= f) {
                                boolean z;
                                Path obtainNewUrlPath;
                                Spannable spannable = (Spannable) this.currentCaption;
                                CharacterStyle[] characterStyleArr = (CharacterStyle[]) spannable.getSpans(y, y, ClickableSpan.class);
                                if (characterStyleArr == null || characterStyleArr.length == 0) {
                                    characterStyleArr = (CharacterStyle[]) spannable.getSpans(y, y, URLSpanMono.class);
                                }
                                if (characterStyleArr.length != 0) {
                                    if (characterStyleArr.length == 0 || !(characterStyleArr[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled) {
                                        z = false;
                                        if (!z) {
                                            this.pressedLink = characterStyleArr[0];
                                            this.pressedLinkType = 3;
                                            resetUrlPaths(false);
                                            try {
                                                obtainNewUrlPath = obtainNewUrlPath(false);
                                                y = spannable.getSpanStart(this.pressedLink);
                                                obtainNewUrlPath.setCurrentLayout(this.captionLayout, y, 0.0f);
                                                this.captionLayout.getSelectionPath(y, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath);
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
                                }
                                z = true;
                                if (z) {
                                    this.pressedLink = characterStyleArr[0];
                                    this.pressedLinkType = 3;
                                    resetUrlPaths(false);
                                    obtainNewUrlPath = obtainNewUrlPath(false);
                                    y = spannable.getSpanStart(this.pressedLink);
                                    obtainNewUrlPath.setCurrentLayout(this.captionLayout, y, 0.0f);
                                    this.captionLayout.getSelectionPath(y, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath);
                                    ((ViewGroup) getParent()).invalidate();
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
        }
        return false;
    }

    private boolean checkGameMotionEvent(MotionEvent motionEvent) {
        if (!this.hasGamePreview) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.drawPhotoImage != null && this.photoImage.isInsideImage((float) x, (float) y) != null) {
                this.gamePreviewPressed = true;
                return true;
            } else if (this.descriptionLayout != null && y >= this.descriptionY) {
                try {
                    x -= (this.unmovedTextX + AndroidUtilities.dp(10.0f)) + this.descriptionX;
                    motionEvent = this.descriptionLayout.getLineForVertical(y - this.descriptionY);
                    float f = (float) x;
                    y = this.descriptionLayout.getOffsetForHorizontal(motionEvent, f);
                    float lineLeft = this.descriptionLayout.getLineLeft(motionEvent);
                    if (lineLeft <= f && lineLeft + this.descriptionLayout.getLineWidth(motionEvent) >= f) {
                        boolean z;
                        Path obtainNewUrlPath;
                        Spannable spannable = (Spannable) this.currentMessageObject.linkDescription;
                        ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(y, y, ClickableSpan.class);
                        if (clickableSpanArr.length != 0) {
                            if (clickableSpanArr.length == 0 || !(clickableSpanArr[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled) {
                                z = false;
                                if (!z) {
                                    this.pressedLink = clickableSpanArr[0];
                                    this.linkBlockNum = -10;
                                    this.pressedLinkType = 2;
                                    resetUrlPaths(false);
                                    try {
                                        obtainNewUrlPath = obtainNewUrlPath(false);
                                        y = spannable.getSpanStart(this.pressedLink);
                                        obtainNewUrlPath.setCurrentLayout(this.descriptionLayout, y, 0.0f);
                                        this.descriptionLayout.getSelectionPath(y, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath);
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    invalidate();
                                    return true;
                                }
                            }
                        }
                        z = true;
                        if (z) {
                            this.pressedLink = clickableSpanArr[0];
                            this.linkBlockNum = -10;
                            this.pressedLinkType = 2;
                            resetUrlPaths(false);
                            obtainNewUrlPath = obtainNewUrlPath(false);
                            y = spannable.getSpanStart(this.pressedLink);
                            obtainNewUrlPath.setCurrentLayout(this.descriptionLayout, y, 0.0f);
                            this.descriptionLayout.getSelectionPath(y, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath);
                            invalidate();
                            return true;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.pressedLinkType != 2) {
                if (this.gamePreviewPressed == null) {
                    resetPressedLink(2);
                }
            }
            if (this.pressedLink != null) {
                if ((this.pressedLink instanceof URLSpan) != null) {
                    Browser.openUrl(getContext(), ((URLSpan) this.pressedLink).getURL());
                } else if ((this.pressedLink instanceof ClickableSpan) != null) {
                    ((ClickableSpan) this.pressedLink).onClick(this);
                }
                resetPressedLink(2);
            } else {
                this.gamePreviewPressed = false;
                for (motionEvent = null; motionEvent < this.botButtons.size(); motionEvent++) {
                    BotButton botButton = (BotButton) this.botButtons.get(motionEvent);
                    if (botButton.button instanceof TL_keyboardButtonGame) {
                        playSoundEffect(0);
                        this.delegate.didPressedBotButton(this, botButton.button);
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

    private boolean checkLinkPreviewMotionEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject.type == 0) {
            if (r1.hasLinkPreview) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                if (x >= r1.unmovedTextX && x <= r1.unmovedTextX + r1.backgroundWidth && y >= r1.textY + r1.currentMessageObject.textHeight) {
                    if (y <= ((r1.textY + r1.currentMessageObject.textHeight) + r1.linkPreviewHeight) + AndroidUtilities.dp((float) (8 + (r1.drawInstantView ? 46 : 0)))) {
                        WebPage webPage;
                        if (motionEvent.getAction() == 0) {
                            int i;
                            int offsetForHorizontal;
                            if (r1.descriptionLayout != null && y >= r1.descriptionY) {
                                try {
                                    int dp = x - ((r1.unmovedTextX + AndroidUtilities.dp(10.0f)) + r1.descriptionX);
                                    i = y - r1.descriptionY;
                                    if (i <= r1.descriptionLayout.getHeight()) {
                                        i = r1.descriptionLayout.getLineForVertical(i);
                                        float f = (float) dp;
                                        offsetForHorizontal = r1.descriptionLayout.getOffsetForHorizontal(i, f);
                                        float lineLeft = r1.descriptionLayout.getLineLeft(i);
                                        if (lineLeft <= f && lineLeft + r1.descriptionLayout.getLineWidth(i) >= f) {
                                            boolean z;
                                            Path obtainNewUrlPath;
                                            Spannable spannable = (Spannable) r1.currentMessageObject.linkDescription;
                                            ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                                            if (clickableSpanArr.length != 0) {
                                                if (clickableSpanArr.length == 0 || !(clickableSpanArr[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled) {
                                                    z = false;
                                                    if (!z) {
                                                        r1.pressedLink = clickableSpanArr[0];
                                                        r1.linkBlockNum = -10;
                                                        r1.pressedLinkType = 2;
                                                        resetUrlPaths(false);
                                                        try {
                                                            obtainNewUrlPath = obtainNewUrlPath(false);
                                                            offsetForHorizontal = spannable.getSpanStart(r1.pressedLink);
                                                            obtainNewUrlPath.setCurrentLayout(r1.descriptionLayout, offsetForHorizontal, 0.0f);
                                                            r1.descriptionLayout.getSelectionPath(offsetForHorizontal, spannable.getSpanEnd(r1.pressedLink), obtainNewUrlPath);
                                                        } catch (Throwable e) {
                                                            FileLog.m3e(e);
                                                        }
                                                        invalidate();
                                                        return true;
                                                    }
                                                }
                                            }
                                            z = true;
                                            if (z) {
                                                r1.pressedLink = clickableSpanArr[0];
                                                r1.linkBlockNum = -10;
                                                r1.pressedLinkType = 2;
                                                resetUrlPaths(false);
                                                obtainNewUrlPath = obtainNewUrlPath(false);
                                                offsetForHorizontal = spannable.getSpanStart(r1.pressedLink);
                                                obtainNewUrlPath.setCurrentLayout(r1.descriptionLayout, offsetForHorizontal, 0.0f);
                                                r1.descriptionLayout.getSelectionPath(offsetForHorizontal, spannable.getSpanEnd(r1.pressedLink), obtainNewUrlPath);
                                                invalidate();
                                                return true;
                                            }
                                        }
                                    }
                                } catch (Throwable e2) {
                                    FileLog.m3e(e2);
                                }
                            }
                            if (r1.pressedLink == null) {
                                boolean z2;
                                i = AndroidUtilities.dp(48.0f);
                                if (r1.miniButtonState >= 0) {
                                    offsetForHorizontal = AndroidUtilities.dp(27.0f);
                                    if (x >= r1.buttonX + offsetForHorizontal && x <= (r1.buttonX + offsetForHorizontal) + i && y >= r1.buttonY + offsetForHorizontal && y <= (r1.buttonY + offsetForHorizontal) + i) {
                                        z2 = true;
                                        if (z2) {
                                            r1.miniButtonPressed = 1;
                                            invalidate();
                                            return true;
                                        } else if (!r1.drawPhotoImage && r1.drawImageButton && r1.buttonState != -1 && x >= r1.buttonX && x <= r1.buttonX + AndroidUtilities.dp(48.0f) && y >= r1.buttonY && y <= r1.buttonY + AndroidUtilities.dp(48.0f)) {
                                            r1.buttonPressed = 1;
                                            return true;
                                        } else if (r1.drawInstantView) {
                                            r1.instantPressed = true;
                                            if (VERSION.SDK_INT >= 21 && r1.instantViewSelectorDrawable != null && r1.instantViewSelectorDrawable.getBounds().contains(x, y)) {
                                                r1.instantViewSelectorDrawable.setState(r1.pressedState);
                                                r1.instantViewSelectorDrawable.setHotspot((float) x, (float) y);
                                                r1.instantButtonPressed = true;
                                            }
                                            invalidate();
                                            return true;
                                        } else if (r1.documentAttachType != 1 && r1.drawPhotoImage && r1.photoImage.isInsideImage((float) x, (float) y)) {
                                            r1.linkPreviewPressed = true;
                                            webPage = r1.currentMessageObject.messageOwner.media.webpage;
                                            if (r1.documentAttachType == 2 || r1.buttonState != -1 || !SharedConfig.autoplayGifs || (r1.photoImage.getAnimation() != null && TextUtils.isEmpty(webPage.embed_url))) {
                                                return true;
                                            }
                                            r1.linkPreviewPressed = false;
                                            return false;
                                        }
                                    }
                                }
                                z2 = false;
                                if (z2) {
                                    r1.miniButtonPressed = 1;
                                    invalidate();
                                    return true;
                                }
                                if (!r1.drawPhotoImage) {
                                }
                                if (r1.drawInstantView) {
                                    r1.instantPressed = true;
                                    r1.instantViewSelectorDrawable.setState(r1.pressedState);
                                    r1.instantViewSelectorDrawable.setHotspot((float) x, (float) y);
                                    r1.instantButtonPressed = true;
                                    invalidate();
                                    return true;
                                }
                                r1.linkPreviewPressed = true;
                                webPage = r1.currentMessageObject.messageOwner.media.webpage;
                                if (r1.documentAttachType == 2) {
                                }
                                return true;
                            }
                        } else if (motionEvent.getAction() == 1) {
                            if (r1.instantPressed) {
                                if (r1.delegate != null) {
                                    r1.delegate.didPressedInstantButton(r1, r1.drawInstantViewType);
                                }
                                playSoundEffect(0);
                                if (VERSION.SDK_INT >= 21 && r1.instantViewSelectorDrawable != null) {
                                    r1.instantViewSelectorDrawable.setState(StateSet.NOTHING);
                                }
                                r1.instantButtonPressed = false;
                                r1.instantPressed = false;
                                invalidate();
                            } else {
                                if (r1.pressedLinkType != 2 && r1.buttonPressed == 0 && r1.miniButtonPressed == 0) {
                                    if (!r1.linkPreviewPressed) {
                                        resetPressedLink(2);
                                    }
                                }
                                if (r1.buttonPressed != 0) {
                                    r1.buttonPressed = 0;
                                    playSoundEffect(0);
                                    didPressedButton(false);
                                    invalidate();
                                } else if (r1.miniButtonPressed != 0) {
                                    r1.miniButtonPressed = 0;
                                    playSoundEffect(0);
                                    didPressedMiniButton(false);
                                    invalidate();
                                } else if (r1.pressedLink != null) {
                                    if (r1.pressedLink instanceof URLSpan) {
                                        Browser.openUrl(getContext(), ((URLSpan) r1.pressedLink).getURL());
                                    } else if (r1.pressedLink instanceof ClickableSpan) {
                                        ((ClickableSpan) r1.pressedLink).onClick(r1);
                                    }
                                    resetPressedLink(2);
                                } else {
                                    if (r1.documentAttachType == 7) {
                                        if (MediaController.getInstance().isPlayingMessage(r1.currentMessageObject)) {
                                            if (!MediaController.getInstance().isMessagePaused()) {
                                                MediaController.getInstance().pauseMessage(r1.currentMessageObject);
                                            }
                                        }
                                        r1.delegate.needPlayMessage(r1.currentMessageObject);
                                    } else if (r1.documentAttachType != 2 || !r1.drawImageButton) {
                                        webPage = r1.currentMessageObject.messageOwner.media.webpage;
                                        if (webPage == null || TextUtils.isEmpty(webPage.embed_url)) {
                                            if (r1.buttonState != -1) {
                                                if (r1.buttonState != 3) {
                                                    if (webPage != null) {
                                                        Browser.openUrl(getContext(), webPage.url);
                                                    }
                                                }
                                            }
                                            r1.delegate.didPressedImage(r1);
                                            playSoundEffect(0);
                                        } else {
                                            r1.delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.title, webPage.url, webPage.embed_width, webPage.embed_height);
                                        }
                                    } else if (r1.buttonState == -1) {
                                        if (SharedConfig.autoplayGifs) {
                                            r1.delegate.didPressedImage(r1);
                                        } else {
                                            r1.buttonState = 2;
                                            r1.currentMessageObject.gifState = 1.0f;
                                            r1.photoImage.setAllowStartAnimation(false);
                                            r1.photoImage.stopAnimation();
                                            r1.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                                            invalidate();
                                            playSoundEffect(0);
                                        }
                                    } else if (r1.buttonState == 2 || r1.buttonState == 0) {
                                        didPressedButton(false);
                                        playSoundEffect(0);
                                    }
                                    resetPressedLink(2);
                                    return true;
                                }
                            }
                        } else if (motionEvent.getAction() == 2 && r1.instantButtonPressed && VERSION.SDK_INT >= 21 && r1.instantViewSelectorDrawable != null) {
                            r1.instantViewSelectorDrawable.setHotspot((float) x, (float) y);
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

    private boolean checkOtherButtonMotionEvent(MotionEvent motionEvent) {
        boolean z = true;
        boolean z2 = this.currentMessageObject.type == 16;
        if (!z2) {
            z2 = ((this.documentAttachType != 1 && this.currentMessageObject.type != 12 && this.documentAttachType != 5 && this.documentAttachType != 4 && this.documentAttachType != 2 && this.currentMessageObject.type != 8) || this.hasGamePreview || this.hasInvoicePreview) ? false : true;
        }
        if (!z2) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.currentMessageObject.type == 16) {
                if (x >= this.otherX && x <= this.otherX + AndroidUtilities.dp(235.0f) && y >= this.otherY - AndroidUtilities.dp(14.0f) && y <= this.otherY + AndroidUtilities.dp(50.0f)) {
                    this.otherPressed = true;
                    invalidate();
                    return z;
                }
            } else if (x >= this.otherX - AndroidUtilities.dp(20.0f) && x <= this.otherX + AndroidUtilities.dp(20.0f) && y >= this.otherY - AndroidUtilities.dp(4.0f) && y <= this.otherY + AndroidUtilities.dp(30.0f)) {
                this.otherPressed = true;
                invalidate();
                return z;
            }
        } else if (motionEvent.getAction() == 1 && this.otherPressed != null) {
            this.otherPressed = false;
            playSoundEffect(0);
            this.delegate.didPressedOther(this);
            invalidate();
            return z;
        }
        z = false;
        return z;
    }

    private boolean checkPhotoImageMotionEvent(MotionEvent motionEvent) {
        boolean z = true;
        boolean z2 = false;
        if (!this.drawPhotoImage && this.documentAttachType != 1) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            boolean z3;
            motionEvent = AndroidUtilities.dp(48.0f);
            if (this.miniButtonState >= 0) {
                int dp = AndroidUtilities.dp(27.0f);
                if (x >= this.buttonX + dp && x <= (this.buttonX + dp) + motionEvent && y >= this.buttonY + dp && y <= (this.buttonY + dp) + motionEvent) {
                    z3 = true;
                    if (z3) {
                        this.miniButtonPressed = 1;
                        invalidate();
                    } else if (this.buttonState != -1 || x < this.buttonX || x > this.buttonX + motionEvent || y < this.buttonY || y > this.buttonY + motionEvent) {
                        if (this.documentAttachType == 1) {
                            if (this.currentMessageObject.type == 13) {
                                if (this.currentMessageObject.getInputStickerSet() != null) {
                                }
                            }
                            if (x >= this.photoImage.getImageX() || x > this.photoImage.getImageX() + this.backgroundWidth || y < this.photoImage.getImageY() || y > this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                                z = false;
                            } else {
                                this.imagePressed = true;
                            }
                            if (this.currentMessageObject.type == 12 && MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)) == null) {
                                this.imagePressed = false;
                            }
                        } else if (x >= this.photoImage.getImageX() && x <= (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(50.0f) && y >= this.photoImage.getImageY() && y <= this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                            this.imagePressed = true;
                        }
                        z = false;
                    } else {
                        this.buttonPressed = 1;
                        invalidate();
                    }
                    if (this.imagePressed != null) {
                        if (this.currentMessageObject.isSendError() == null) {
                            this.imagePressed = false;
                        } else if (this.currentMessageObject.type != 8 && this.buttonState == -1 && SharedConfig.autoplayGifs != null && this.photoImage.getAnimation() == null) {
                            this.imagePressed = false;
                        } else if (this.currentMessageObject.type == 5 && this.buttonState != -1) {
                            this.imagePressed = false;
                        }
                    }
                    z2 = z;
                }
            }
            z3 = false;
            if (z3) {
                this.miniButtonPressed = 1;
                invalidate();
            } else {
                if (this.buttonState != -1) {
                }
                if (this.documentAttachType == 1) {
                    if (this.currentMessageObject.type == 13) {
                        if (this.currentMessageObject.getInputStickerSet() != null) {
                        }
                    }
                    if (x >= this.photoImage.getImageX()) {
                    }
                    z = false;
                    this.imagePressed = false;
                } else {
                    this.imagePressed = true;
                }
                z = false;
            }
            if (this.imagePressed != null) {
                if (this.currentMessageObject.isSendError() == null) {
                    if (this.currentMessageObject.type != 8) {
                    }
                    this.imagePressed = false;
                } else {
                    this.imagePressed = false;
                }
            }
            z2 = z;
        } else if (motionEvent.getAction() == 1) {
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
            } else if (this.imagePressed != null) {
                this.imagePressed = false;
                if (!(this.buttonState == -1 || this.buttonState == 2)) {
                    if (this.buttonState != 3) {
                        if (this.buttonState == null && this.documentAttachType == 1) {
                            playSoundEffect(0);
                            didPressedButton(false);
                        }
                        invalidate();
                    }
                }
                playSoundEffect(0);
                didClickedImage();
                invalidate();
            }
        }
        return z2;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkAudioMotionEvent(MotionEvent motionEvent) {
        if (this.documentAttachType != 3 && this.documentAttachType != 5) {
            return false;
        }
        boolean onTouch;
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (this.useSeekBarWaweform) {
            onTouch = this.seekBarWaveform.onTouch(motionEvent.getAction(), (motionEvent.getX() - ((float) this.seekBarX)) - ((float) AndroidUtilities.dp(13.0f)), motionEvent.getY() - ((float) this.seekBarY));
        } else {
            onTouch = this.seekBar.onTouch(motionEvent.getAction(), motionEvent.getX() - ((float) this.seekBarX), motionEvent.getY() - ((float) this.seekBarY));
        }
        if (onTouch) {
            if (!this.useSeekBarWaweform && motionEvent.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (this.useSeekBarWaweform && !this.seekBarWaveform.isStartDraging() && motionEvent.getAction() == 1) {
                didPressedButton(true);
            }
            this.disallowLongPress = true;
            invalidate();
        } else {
            boolean z;
            boolean z2;
            int dp = AndroidUtilities.dp(36.0f);
            if (this.miniButtonState >= 0) {
                int dp2 = AndroidUtilities.dp(27.0f);
                if (x >= this.buttonX + dp2 && x <= (this.buttonX + dp2) + dp && y >= this.buttonY + dp2 && y <= (this.buttonY + dp2) + dp) {
                    z = true;
                    if (!z) {
                        if (!(this.buttonState == 0 || this.buttonState == 1)) {
                            if (this.buttonState != 2) {
                                if (x >= this.buttonX) {
                                    if (x <= this.buttonX + dp) {
                                        if (y >= this.buttonY) {
                                        }
                                    }
                                }
                            }
                        }
                        if (x >= this.buttonX - AndroidUtilities.dp(12.0f) && x <= (this.buttonX - AndroidUtilities.dp(12.0f)) + this.backgroundWidth) {
                            if (y >= (this.drawInstantView ? this.buttonY : this.namesOffset + this.mediaOffsetY)) {
                                if (this.drawInstantView) {
                                    x = this.namesOffset + this.mediaOffsetY;
                                    dp = AndroidUtilities.dp(82.0f);
                                } else {
                                    x = this.buttonY;
                                }
                                if (y <= x + dp) {
                                    z2 = true;
                                    if (motionEvent.getAction() != 0) {
                                        if (z2 || z) {
                                            if (z2) {
                                                this.miniButtonPressed = 1;
                                            } else {
                                                this.buttonPressed = 1;
                                            }
                                            invalidate();
                                            updateRadialProgressBackground();
                                            onTouch = true;
                                        }
                                    } else if (this.buttonPressed == 0) {
                                        if (motionEvent.getAction() != 1) {
                                            this.buttonPressed = 0;
                                            playSoundEffect(0);
                                            didPressedButton(true);
                                            invalidate();
                                        } else if (motionEvent.getAction() != 3) {
                                            this.buttonPressed = 0;
                                            invalidate();
                                        } else if (motionEvent.getAction() == 2 && !z2) {
                                            this.buttonPressed = 0;
                                            invalidate();
                                        }
                                        updateRadialProgressBackground();
                                    } else if (this.miniButtonPressed != 0) {
                                        if (motionEvent.getAction() != 1) {
                                            this.miniButtonPressed = 0;
                                            playSoundEffect(0);
                                            didPressedMiniButton(true);
                                            invalidate();
                                        } else if (motionEvent.getAction() != 3) {
                                            this.miniButtonPressed = 0;
                                            invalidate();
                                        } else if (motionEvent.getAction() == 2 && !z) {
                                            this.miniButtonPressed = 0;
                                            invalidate();
                                        }
                                        updateRadialProgressBackground();
                                    }
                                }
                            }
                        }
                    }
                    z2 = false;
                    if (motionEvent.getAction() != 0) {
                        if (z2) {
                            this.miniButtonPressed = 1;
                        } else {
                            this.buttonPressed = 1;
                        }
                        invalidate();
                        updateRadialProgressBackground();
                        onTouch = true;
                    } else if (this.buttonPressed == 0) {
                        if (motionEvent.getAction() != 1) {
                            this.buttonPressed = 0;
                            playSoundEffect(0);
                            didPressedButton(true);
                            invalidate();
                        } else if (motionEvent.getAction() != 3) {
                            this.buttonPressed = 0;
                            invalidate();
                        } else {
                            this.buttonPressed = 0;
                            invalidate();
                        }
                        updateRadialProgressBackground();
                    } else if (this.miniButtonPressed != 0) {
                        if (motionEvent.getAction() != 1) {
                            this.miniButtonPressed = 0;
                            playSoundEffect(0);
                            didPressedMiniButton(true);
                            invalidate();
                        } else if (motionEvent.getAction() != 3) {
                            this.miniButtonPressed = 0;
                            invalidate();
                        } else {
                            this.miniButtonPressed = 0;
                            invalidate();
                        }
                        updateRadialProgressBackground();
                    }
                }
            }
            z = false;
            if (z) {
                if (this.buttonState != 2) {
                    if (this.drawInstantView) {
                    }
                    if (y >= (this.drawInstantView ? this.buttonY : this.namesOffset + this.mediaOffsetY)) {
                        if (this.drawInstantView) {
                            x = this.namesOffset + this.mediaOffsetY;
                            dp = AndroidUtilities.dp(82.0f);
                        } else {
                            x = this.buttonY;
                        }
                        if (y <= x + dp) {
                            z2 = true;
                            if (motionEvent.getAction() != 0) {
                                if (z2) {
                                    this.buttonPressed = 1;
                                } else {
                                    this.miniButtonPressed = 1;
                                }
                                invalidate();
                                updateRadialProgressBackground();
                                onTouch = true;
                            } else if (this.buttonPressed == 0) {
                                if (motionEvent.getAction() != 1) {
                                    this.buttonPressed = 0;
                                    playSoundEffect(0);
                                    didPressedButton(true);
                                    invalidate();
                                } else if (motionEvent.getAction() != 3) {
                                    this.buttonPressed = 0;
                                    invalidate();
                                } else {
                                    this.buttonPressed = 0;
                                    invalidate();
                                }
                                updateRadialProgressBackground();
                            } else if (this.miniButtonPressed != 0) {
                                if (motionEvent.getAction() != 1) {
                                    this.miniButtonPressed = 0;
                                    playSoundEffect(0);
                                    didPressedMiniButton(true);
                                    invalidate();
                                } else if (motionEvent.getAction() != 3) {
                                    this.miniButtonPressed = 0;
                                    invalidate();
                                } else {
                                    this.miniButtonPressed = 0;
                                    invalidate();
                                }
                                updateRadialProgressBackground();
                            }
                        }
                    }
                } else if (x >= this.buttonX) {
                    if (x <= this.buttonX + dp) {
                        if (y >= this.buttonY) {
                        }
                    }
                }
            }
            z2 = false;
            if (motionEvent.getAction() != 0) {
                if (z2) {
                    this.miniButtonPressed = 1;
                } else {
                    this.buttonPressed = 1;
                }
                invalidate();
                updateRadialProgressBackground();
                onTouch = true;
            } else if (this.buttonPressed == 0) {
                if (motionEvent.getAction() != 1) {
                    this.buttonPressed = 0;
                    playSoundEffect(0);
                    didPressedButton(true);
                    invalidate();
                } else if (motionEvent.getAction() != 3) {
                    this.buttonPressed = 0;
                    invalidate();
                } else {
                    this.buttonPressed = 0;
                    invalidate();
                }
                updateRadialProgressBackground();
            } else if (this.miniButtonPressed != 0) {
                if (motionEvent.getAction() != 1) {
                    this.miniButtonPressed = 0;
                    playSoundEffect(0);
                    didPressedMiniButton(true);
                    invalidate();
                } else if (motionEvent.getAction() != 3) {
                    this.miniButtonPressed = 0;
                    invalidate();
                } else {
                    this.miniButtonPressed = 0;
                    invalidate();
                }
                updateRadialProgressBackground();
            }
        }
        return onTouch;
    }

    private boolean checkBotButtonMotionEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!this.botButtons.isEmpty()) {
            if (this.currentMessageObject.eventId == 0) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == 0) {
                    if (this.currentMessageObject.isOutOwner() != null) {
                        motionEvent = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
                    } else {
                        motionEvent = this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
                    }
                    for (int i = 0; i < this.botButtons.size(); i++) {
                        BotButton botButton = (BotButton) this.botButtons.get(i);
                        int access$600 = (botButton.f10y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                        if (x >= botButton.f9x + motionEvent && x <= (botButton.f9x + motionEvent) + botButton.width && y >= access$600 && y <= access$600 + botButton.height) {
                            this.pressedBotButton = i;
                            invalidate();
                            z = true;
                            break;
                        }
                    }
                } else if (motionEvent.getAction() == 1 && this.pressedBotButton != -1) {
                    playSoundEffect(0);
                    this.delegate.didPressedBotButton(this, ((BotButton) this.botButtons.get(this.pressedBotButton)).button);
                    this.pressedBotButton = -1;
                    invalidate();
                }
                return z;
            }
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject != null) {
            if (r0.delegate.canPerformActions()) {
                r0.disallowLongPress = false;
                boolean checkTextBlockMotionEvent = checkTextBlockMotionEvent(motionEvent);
                if (!checkTextBlockMotionEvent) {
                    checkTextBlockMotionEvent = checkOtherButtonMotionEvent(motionEvent);
                }
                if (!checkTextBlockMotionEvent) {
                    checkTextBlockMotionEvent = checkCaptionMotionEvent(motionEvent);
                }
                if (!checkTextBlockMotionEvent) {
                    checkTextBlockMotionEvent = checkAudioMotionEvent(motionEvent);
                }
                if (!checkTextBlockMotionEvent) {
                    checkTextBlockMotionEvent = checkLinkPreviewMotionEvent(motionEvent);
                }
                if (!checkTextBlockMotionEvent) {
                    checkTextBlockMotionEvent = checkGameMotionEvent(motionEvent);
                }
                if (!checkTextBlockMotionEvent) {
                    checkTextBlockMotionEvent = checkPhotoImageMotionEvent(motionEvent);
                }
                if (!checkTextBlockMotionEvent) {
                    checkTextBlockMotionEvent = checkBotButtonMotionEvent(motionEvent);
                }
                if (motionEvent.getAction() == 3) {
                    r0.buttonPressed = 0;
                    r0.miniButtonPressed = 0;
                    r0.pressedBotButton = -1;
                    r0.linkPreviewPressed = false;
                    r0.otherPressed = false;
                    r0.imagePressed = false;
                    r0.gamePreviewPressed = false;
                    r0.instantButtonPressed = false;
                    r0.instantPressed = false;
                    if (VERSION.SDK_INT >= 21 && r0.instantViewSelectorDrawable != null) {
                        r0.instantViewSelectorDrawable.setState(StateSet.NOTHING);
                    }
                    resetPressedLink(-1);
                    checkTextBlockMotionEvent = false;
                }
                if (!r0.disallowLongPress && checkTextBlockMotionEvent && motionEvent.getAction() == 0) {
                    startCheckLongPress();
                }
                if (!(motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
                    cancelCheckLongPress();
                }
                if (!checkTextBlockMotionEvent) {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    if (motionEvent.getAction() != 0) {
                        if (motionEvent.getAction() != 2) {
                            cancelCheckLongPress();
                        }
                        if (r0.avatarPressed) {
                            if (motionEvent.getAction() == 1) {
                                r0.avatarPressed = false;
                                playSoundEffect(0);
                                if (r0.delegate != null) {
                                    if (r0.currentUser != null) {
                                        r0.delegate.didPressedUserAvatar(r0, r0.currentUser);
                                    } else if (r0.currentChat != null) {
                                        r0.delegate.didPressedChannelAvatar(r0, r0.currentChat, 0);
                                    }
                                }
                            } else if (motionEvent.getAction() == 3) {
                                r0.avatarPressed = false;
                            } else if (motionEvent.getAction() == 2 && r0.isAvatarVisible && !r0.avatarImage.isInsideImage(x, y + ((float) getTop()))) {
                                r0.avatarPressed = false;
                            }
                        } else if (r0.forwardNamePressed) {
                            if (motionEvent.getAction() == 1) {
                                r0.forwardNamePressed = false;
                                playSoundEffect(0);
                                if (r0.delegate != null) {
                                    if (r0.currentForwardChannel != null) {
                                        r0.delegate.didPressedChannelAvatar(r0, r0.currentForwardChannel, r0.currentMessageObject.messageOwner.fwd_from.channel_post);
                                    } else if (r0.currentForwardUser != null) {
                                        r0.delegate.didPressedUserAvatar(r0, r0.currentForwardUser);
                                    }
                                }
                            } else if (motionEvent.getAction() == 3) {
                                r0.forwardNamePressed = false;
                            } else if (motionEvent.getAction() == 2 && (x < ((float) r0.forwardNameX) || x > ((float) (r0.forwardNameX + r0.forwardedNameWidth)) || y < ((float) r0.forwardNameY) || y > ((float) (r0.forwardNameY + AndroidUtilities.dp(32.0f))))) {
                                r0.forwardNamePressed = false;
                            }
                        } else if (r0.forwardBotPressed) {
                            if (motionEvent.getAction() == 1) {
                                r0.forwardBotPressed = false;
                                playSoundEffect(0);
                                if (r0.delegate != null) {
                                    r0.delegate.didPressedViaBot(r0, r0.currentViaBotUser != null ? r0.currentViaBotUser.username : r0.currentMessageObject.messageOwner.via_bot_name);
                                }
                            } else if (motionEvent.getAction() == 3) {
                                r0.forwardBotPressed = false;
                            } else if (motionEvent.getAction() == 2) {
                                if (!r0.drawForwardedName || r0.forwardedNameLayout[0] == null) {
                                    if (x < r0.nameX + ((float) r0.viaNameWidth) || x > (r0.nameX + ((float) r0.viaNameWidth)) + ((float) r0.viaWidth) || y < r0.nameY - ((float) AndroidUtilities.dp(4.0f)) || y > r0.nameY + ((float) AndroidUtilities.dp(20.0f))) {
                                        r0.forwardBotPressed = false;
                                    }
                                } else if (x < ((float) r0.forwardNameX) || x > ((float) (r0.forwardNameX + r0.forwardedNameWidth)) || y < ((float) r0.forwardNameY) || y > ((float) (r0.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                                    r0.forwardBotPressed = false;
                                }
                            }
                        } else if (r0.replyPressed) {
                            if (motionEvent.getAction() == 1) {
                                r0.replyPressed = false;
                                playSoundEffect(0);
                                if (r0.delegate != null) {
                                    r0.delegate.didPressedReplyMessage(r0, r0.currentMessageObject.messageOwner.reply_to_msg_id);
                                }
                            } else if (motionEvent.getAction() == 3) {
                                r0.replyPressed = false;
                            } else if (motionEvent.getAction() == 2) {
                                int i;
                                if (r0.currentMessageObject.type != 13) {
                                    if (r0.currentMessageObject.type != 5) {
                                        i = r0.replyStartX + r0.backgroundDrawableRight;
                                        if (x < ((float) r0.replyStartX) || x > ((float) r4) || y < ((float) r0.replyStartY) || y > ((float) (r0.replyStartY + AndroidUtilities.dp(35.0f)))) {
                                            r0.replyPressed = false;
                                        }
                                    }
                                }
                                i = r0.replyStartX + Math.max(r0.replyNameWidth, r0.replyTextWidth);
                                r0.replyPressed = false;
                            }
                        } else if (r0.sharePressed) {
                            if (motionEvent.getAction() == 1) {
                                r0.sharePressed = false;
                                playSoundEffect(0);
                                if (r0.delegate != null) {
                                    r0.delegate.didPressedShare(r0);
                                }
                            } else if (motionEvent.getAction() == 3) {
                                r0.sharePressed = false;
                            } else if (motionEvent.getAction() == 2 && (x < ((float) r0.shareStartX) || x > ((float) (r0.shareStartX + AndroidUtilities.dp(40.0f))) || y < ((float) r0.shareStartY) || y > ((float) (r0.shareStartY + AndroidUtilities.dp(32.0f))))) {
                                r0.sharePressed = false;
                            }
                            invalidate();
                        }
                    } else if (r0.delegate == null || r0.delegate.canPerformActions()) {
                        if (r0.isAvatarVisible && r0.avatarImage.isInsideImage(x, ((float) getTop()) + y)) {
                            r0.avatarPressed = true;
                        } else if (!r0.drawForwardedName || r0.forwardedNameLayout[0] == null || x < ((float) r0.forwardNameX) || x > ((float) (r0.forwardNameX + r0.forwardedNameWidth)) || y < ((float) r0.forwardNameY) || y > ((float) (r0.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                            if (r0.drawNameLayout && r0.nameLayout != null && r0.viaWidth != 0 && x >= r0.nameX + ((float) r0.viaNameWidth) && x <= (r0.nameX + ((float) r0.viaNameWidth)) + ((float) r0.viaWidth) && y >= r0.nameY - ((float) AndroidUtilities.dp(4.0f)) && y <= r0.nameY + ((float) AndroidUtilities.dp(20.0f))) {
                                r0.forwardBotPressed = true;
                            } else if (!r0.drawShareButton || x < ((float) r0.shareStartX) || x > ((float) (r0.shareStartX + AndroidUtilities.dp(40.0f))) || y < ((float) r0.shareStartY) || y > ((float) (r0.shareStartY + AndroidUtilities.dp(32.0f)))) {
                                if (r0.replyNameLayout != null) {
                                    int i2;
                                    if (r0.currentMessageObject.type != 13) {
                                        if (r0.currentMessageObject.type != 5) {
                                            i2 = r0.replyStartX + r0.backgroundDrawableRight;
                                            if (x >= ((float) r0.replyStartX) && x <= ((float) r1) && y >= ((float) r0.replyStartY) && y <= ((float) (r0.replyStartY + AndroidUtilities.dp(35.0f)))) {
                                                r0.replyPressed = true;
                                            }
                                        }
                                    }
                                    i2 = r0.replyStartX + Math.max(r0.replyNameWidth, r0.replyTextWidth);
                                    r0.replyPressed = true;
                                }
                                if (checkTextBlockMotionEvent) {
                                    startCheckLongPress();
                                }
                            } else {
                                r0.sharePressed = true;
                                invalidate();
                            }
                        } else if (r0.viaWidth == 0 || x < ((float) ((r0.forwardNameX + r0.viaNameWidth) + AndroidUtilities.dp(4.0f)))) {
                            r0.forwardNamePressed = true;
                        } else {
                            r0.forwardBotPressed = true;
                        }
                        checkTextBlockMotionEvent = true;
                        if (checkTextBlockMotionEvent) {
                            startCheckLongPress();
                        }
                    }
                }
                return checkTextBlockMotionEvent;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public void updatePlayingMessageProgress() {
        if (this.currentMessageObject != null) {
            int i;
            CharSequence format;
            if (this.currentMessageObject.isRoundVideo()) {
                Document document = this.currentMessageObject.getDocument();
                for (int i2 = 0; i2 < document.attributes.size(); i2++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i2);
                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                        i = documentAttribute.duration;
                        break;
                    }
                }
                i = 0;
                if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    i = Math.max(0, i - this.currentMessageObject.audioProgressSec);
                }
                if (this.lastTime != i) {
                    this.lastTime = i;
                    format = String.format("%02d:%02d", new Object[]{Integer.valueOf(i / 60), Integer.valueOf(i % 60)});
                    this.timeWidthAudio = (int) Math.ceil((double) Theme.chat_timePaint.measureText(format));
                    this.durationLayout = new StaticLayout(format, Theme.chat_timePaint, this.timeWidthAudio, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
                    if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                        i = this.currentMessageObject.audioProgressSec;
                    } else {
                        for (i = 0; i < this.documentAttach.attributes.size(); i++) {
                            DocumentAttribute documentAttribute2 = (DocumentAttribute) this.documentAttach.attributes.get(i);
                            if (documentAttribute2 instanceof TL_documentAttributeAudio) {
                                i = documentAttribute2.duration;
                                break;
                            }
                        }
                        i = 0;
                    }
                    if (this.lastTime != i) {
                        this.lastTime = i;
                        format = String.format("%02d:%02d", new Object[]{Integer.valueOf(i / 60), Integer.valueOf(i % 60)});
                        this.timeWidthAudio = (int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(format));
                        this.durationLayout = new StaticLayout(format, Theme.chat_audioTimePaint, this.timeWidthAudio, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }
                } else {
                    i = this.currentMessageObject.getDuration();
                    int i3 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject) ? this.currentMessageObject.audioProgressSec : 0;
                    if (this.lastTime != i3) {
                        String format2;
                        this.lastTime = i3;
                        if (i == 0) {
                            format2 = String.format("%d:%02d / -:--", new Object[]{Integer.valueOf(i3 / 60), Integer.valueOf(i3 % 60)});
                        } else {
                            format2 = String.format("%d:%02d / %d:%02d", new Object[]{Integer.valueOf(i3 / 60), Integer.valueOf(i3 % 60), Integer.valueOf(i / 60), Integer.valueOf(i % 60)});
                        }
                        String str = format2;
                        this.durationLayout = new StaticLayout(str, Theme.chat_audioTimePaint, (int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(str)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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

    public void setFullyDraw(boolean z) {
        this.fullyDraw = z;
    }

    public void setVisiblePart(int i, int i2) {
        if (this.currentMessageObject != null) {
            if (this.currentMessageObject.textLayoutBlocks != null) {
                i -= this.textY;
                int i3 = 0;
                int i4 = i3;
                while (i3 < this.currentMessageObject.textLayoutBlocks.size()) {
                    if (((TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i3)).textYOffset > ((float) i)) {
                        break;
                    }
                    i4 = i3;
                    i3++;
                }
                int i5 = 0;
                int i6 = -1;
                int i7 = i6;
                for (i4 = 
/*
Method generation error in method: org.telegram.ui.Cells.ChatMessageCell.setVisiblePart(int, int):void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r2_1 'i4' int) = (r2_0 'i4' int), (r2_3 'i4' int) binds: {(r2_0 'i4' int)=B:5:0x000c, (r2_3 'i4' int)=B:11:0x002e} in method: org.telegram.ui.Cells.ChatMessageCell.setVisiblePart(int, int):void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:184)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:118)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:57)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:118)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:57)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:75)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:12)
	at jadx.core.ProcessClass.process(ProcessClass.java:40)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:537)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:509)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 27 more

*/

                public static StaticLayout generateStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, int i2, int i3, int i4) {
                    CharSequence spannableStringBuilder = new SpannableStringBuilder(charSequence);
                    StaticLayout staticLayout = new StaticLayout(charSequence, textPaint, i2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    int i5 = 0;
                    int i6 = i;
                    i = 0;
                    while (i5 < i3) {
                        staticLayout.getLineDirections(i5);
                        if (staticLayout.getLineLeft(i5) != 0.0f || staticLayout.isRtlCharAt(staticLayout.getLineStart(i5)) || staticLayout.isRtlCharAt(staticLayout.getLineEnd(i5))) {
                            i6 = i2;
                        }
                        int lineEnd = staticLayout.getLineEnd(i5);
                        if (lineEnd == charSequence.length()) {
                            break;
                        }
                        lineEnd = (lineEnd - 1) + i;
                        if (spannableStringBuilder.charAt(lineEnd) == ' ') {
                            spannableStringBuilder.replace(lineEnd, lineEnd + 1, "\n");
                        } else if (spannableStringBuilder.charAt(lineEnd) != '\n') {
                            spannableStringBuilder.insert(lineEnd, "\n");
                            i++;
                        }
                        if (i5 == staticLayout.getLineCount() - 1) {
                            break;
                        } else if (i5 == i4 - 1) {
                            break;
                        } else {
                            i5++;
                        }
                    }
                    int i7 = i6;
                    return StaticLayoutEx.createStaticLayout(spannableStringBuilder, textPaint, i7, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.dp(1.0f), false, TruncateAt.END, i7, i4);
                }

                private void didClickedImage() {
                    if (this.currentMessageObject.type != 1) {
                        if (this.currentMessageObject.type != 13) {
                            if (this.currentMessageObject.type == 12) {
                                this.delegate.didPressedUserAvatar(this, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)));
                                return;
                            } else if (this.currentMessageObject.type == 5) {
                                if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                                    if (!MediaController.getInstance().isMessagePaused()) {
                                        MediaController.getInstance().pauseMessage(this.currentMessageObject);
                                        return;
                                    }
                                }
                                this.delegate.needPlayMessage(this.currentMessageObject);
                                return;
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
                                    return;
                                } else if (this.buttonState == 2 || this.buttonState == 0) {
                                    didPressedButton(false);
                                    return;
                                } else {
                                    return;
                                }
                            } else if (this.documentAttachType == 4) {
                                if (this.buttonState == -1) {
                                    this.delegate.didPressedImage(this);
                                    return;
                                } else if (this.buttonState == 0 || this.buttonState == 3) {
                                    didPressedButton(false);
                                    return;
                                } else {
                                    return;
                                }
                            } else if (this.currentMessageObject.type == 4) {
                                this.delegate.didPressedImage(this);
                                return;
                            } else if (this.documentAttachType == 1) {
                                if (this.buttonState == -1) {
                                    this.delegate.didPressedImage(this);
                                    return;
                                }
                                return;
                            } else if (this.documentAttachType == 2) {
                                if (this.buttonState == -1) {
                                    WebPage webPage = this.currentMessageObject.messageOwner.media.webpage;
                                    if (webPage == null) {
                                        return;
                                    }
                                    if (webPage.embed_url == null || webPage.embed_url.length() == 0) {
                                        Browser.openUrl(getContext(), webPage.url);
                                        return;
                                    } else {
                                        this.delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.description, webPage.url, webPage.embed_width, webPage.embed_height);
                                        return;
                                    }
                                }
                                return;
                            } else if (this.hasInvoicePreview && this.buttonState == -1) {
                                this.delegate.didPressedImage(this);
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                    if (this.buttonState == -1) {
                        this.delegate.didPressedImage(this);
                    } else if (this.buttonState == 0) {
                        didPressedButton(false);
                    }
                }

                private void updateSecretTimeText(MessageObject messageObject) {
                    if (messageObject != null) {
                        if (messageObject.needDrawBluredPreview()) {
                            messageObject = messageObject.getSecretTimeString();
                            if (messageObject != null) {
                                this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(messageObject));
                                this.infoLayout = new StaticLayout(TextUtils.ellipsize(messageObject, Theme.chat_infoPaint, (float) this.infoWidth, TruncateAt.END), Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                invalidate();
                            }
                        }
                    }
                }

                private boolean isPhotoDataChanged(MessageObject messageObject) {
                    ChatMessageCell chatMessageCell = this;
                    MessageObject messageObject2 = messageObject;
                    if (messageObject2.type != 0) {
                        if (messageObject2.type != 14) {
                            if (messageObject2.type != 4) {
                                if (chatMessageCell.currentPhotoObject != null) {
                                    if (!(chatMessageCell.currentPhotoObject.location instanceof TL_fileLocationUnavailable)) {
                                        if (chatMessageCell.currentMessageObject != null && chatMessageCell.photoNotSet && FileLoader.getPathToMessage(chatMessageCell.currentMessageObject.messageOwner).exists()) {
                                            return true;
                                        }
                                    }
                                }
                                if (!(messageObject2.type == 1 || messageObject2.type == 5 || messageObject2.type == 3 || messageObject2.type == 8)) {
                                    if (messageObject2.type == 13) {
                                    }
                                }
                                return true;
                            } else if (chatMessageCell.currentUrl == null) {
                                return true;
                            } else {
                                String format;
                                double d = messageObject2.messageOwner.media.geo.lat;
                                double d2 = messageObject2.messageOwner.media.geo._long;
                                if (messageObject2.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                    int dp = chatMessageCell.backgroundWidth - AndroidUtilities.dp(21.0f);
                                    int dp2 = AndroidUtilities.dp(195.0f);
                                    double d3 = (double) 268435456;
                                    double d4 = d3 / 3.141592653589793d;
                                    d = (d * 3.141592653589793d) / 180.0d;
                                    double atan = ((1.5707963267948966d - (2.0d * Math.atan(Math.exp((((double) (Math.round(d3 - ((Math.log((1.0d + Math.sin(d)) / (1.0d - Math.sin(d))) * d4) / 2.0d)) - ((long) (AndroidUtilities.dp(10.3f) << 6)))) - d3) / d4)))) * 180.0d) / 3.141592653589793d;
                                    format = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=%dx%d&maptype=roadmap&scale=%d&sensor=false", new Object[]{Double.valueOf(atan), Double.valueOf(d2), Integer.valueOf((int) (((float) dp) / AndroidUtilities.density)), Integer.valueOf((int) (((float) dp2) / AndroidUtilities.density)), Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)))});
                                } else if (TextUtils.isEmpty(messageObject2.messageOwner.media.title)) {
                                    format = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=200x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density))), Double.valueOf(d), Double.valueOf(d2)});
                                } else {
                                    format = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density))), Double.valueOf(d), Double.valueOf(d2)});
                                }
                                if (!format.equals(chatMessageCell.currentUrl)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    }
                    return false;
                }

                private boolean isUserDataChanged() {
                    boolean z = true;
                    if (this.currentMessageObject != null && !this.hasLinkPreview && this.currentMessageObject.messageOwner.media != null && (this.currentMessageObject.messageOwner.media.webpage instanceof TL_webPage)) {
                        return true;
                    }
                    if (this.currentMessageObject != null) {
                        if (this.currentUser != null || this.currentChat != null) {
                            if (this.lastSendState != this.currentMessageObject.messageOwner.send_state || this.lastDeleteDate != this.currentMessageObject.messageOwner.destroyTime || this.lastViewsCount != this.currentMessageObject.messageOwner.views) {
                                return true;
                            }
                            FileLocation fileLocation;
                            PhotoSize closestPhotoSizeWithSize;
                            String forwardedName;
                            updateCurrentUserAndChat();
                            Object obj = null;
                            if (this.isAvatarVisible) {
                                if (this.currentUser != null && this.currentUser.photo != null) {
                                    fileLocation = this.currentUser.photo.photo_small;
                                    if (this.replyTextLayout != null) {
                                    }
                                    if (this.currentPhoto != null) {
                                    }
                                    if (this.replyNameLayout != null) {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
                                        fileLocation = closestPhotoSizeWithSize.location;
                                        if (this.currentReplyPhoto != null) {
                                        }
                                        if (this.currentUser != null) {
                                            obj = UserObject.getUserName(this.currentUser);
                                        } else if (this.currentChat != null) {
                                            obj = this.currentChat.title;
                                        }
                                        if (this.currentNameString != null) {
                                        }
                                        if (this.drawForwardedName) {
                                            return false;
                                        }
                                        forwardedName = this.currentMessageObject.getForwardedName();
                                        if (this.currentForwardNameString != null) {
                                        }
                                        z = false;
                                        return z;
                                    }
                                    fileLocation = null;
                                    if (this.currentReplyPhoto != null) {
                                    }
                                    if (this.currentUser != null) {
                                        obj = UserObject.getUserName(this.currentUser);
                                    } else if (this.currentChat != null) {
                                        obj = this.currentChat.title;
                                    }
                                    if (this.currentNameString != null) {
                                    }
                                    if (this.drawForwardedName) {
                                        return false;
                                    }
                                    forwardedName = this.currentMessageObject.getForwardedName();
                                    if (this.currentForwardNameString != null) {
                                    }
                                    z = false;
                                    return z;
                                } else if (!(this.currentChat == null || this.currentChat.photo == null)) {
                                    fileLocation = this.currentChat.photo.photo_small;
                                    if (this.replyTextLayout != null && this.currentMessageObject.replyMessageObject != null) {
                                        return true;
                                    }
                                    if ((this.currentPhoto != null && r0 != null) || ((this.currentPhoto != null && r0 == null) || (this.currentPhoto != null && r0 != null && (this.currentPhoto.local_id != r0.local_id || this.currentPhoto.volume_id != r0.volume_id)))) {
                                        return true;
                                    }
                                    if (this.replyNameLayout != null) {
                                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
                                        if (!(closestPhotoSizeWithSize == null || this.currentMessageObject.replyMessageObject.type == 13)) {
                                            fileLocation = closestPhotoSizeWithSize.location;
                                            if (this.currentReplyPhoto != null && r0 != null) {
                                                return true;
                                            }
                                            if (this.drawName && this.isChat && !this.currentMessageObject.isOutOwner()) {
                                                if (this.currentUser != null) {
                                                    obj = UserObject.getUserName(this.currentUser);
                                                } else if (this.currentChat != null) {
                                                    obj = this.currentChat.title;
                                                }
                                            }
                                            if ((this.currentNameString != null && r3 != null) || ((this.currentNameString != null && r3 == null) || (this.currentNameString != null && r3 != null && !this.currentNameString.equals(r3)))) {
                                                return true;
                                            }
                                            if (this.drawForwardedName) {
                                                return false;
                                            }
                                            forwardedName = this.currentMessageObject.getForwardedName();
                                            if ((this.currentForwardNameString != null || forwardedName == null) && (this.currentForwardNameString == null || forwardedName != null)) {
                                                if (this.currentForwardNameString != null || forwardedName == null || this.currentForwardNameString.equals(forwardedName)) {
                                                    z = false;
                                                }
                                            }
                                            return z;
                                        }
                                    }
                                    fileLocation = null;
                                    if (this.currentReplyPhoto != null) {
                                    }
                                    if (this.currentUser != null) {
                                        obj = UserObject.getUserName(this.currentUser);
                                    } else if (this.currentChat != null) {
                                        obj = this.currentChat.title;
                                    }
                                    if (this.currentNameString != null) {
                                    }
                                    if (this.drawForwardedName) {
                                        return false;
                                    }
                                    forwardedName = this.currentMessageObject.getForwardedName();
                                    if (this.currentForwardNameString != null) {
                                    }
                                    z = false;
                                    return z;
                                }
                            }
                            fileLocation = null;
                            if (this.replyTextLayout != null) {
                            }
                            if (this.currentPhoto != null) {
                            }
                            if (this.replyNameLayout != null) {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
                                fileLocation = closestPhotoSizeWithSize.location;
                                if (this.currentReplyPhoto != null) {
                                }
                                if (this.currentUser != null) {
                                    obj = UserObject.getUserName(this.currentUser);
                                } else if (this.currentChat != null) {
                                    obj = this.currentChat.title;
                                }
                                if (this.currentNameString != null) {
                                }
                                if (this.drawForwardedName) {
                                    return false;
                                }
                                forwardedName = this.currentMessageObject.getForwardedName();
                                if (this.currentForwardNameString != null) {
                                }
                                z = false;
                                return z;
                            }
                            fileLocation = null;
                            if (this.currentReplyPhoto != null) {
                            }
                            if (this.currentUser != null) {
                                obj = UserObject.getUserName(this.currentUser);
                            } else if (this.currentChat != null) {
                                obj = this.currentChat.title;
                            }
                            if (this.currentNameString != null) {
                            }
                            if (this.drawForwardedName) {
                                return false;
                            }
                            forwardedName = this.currentMessageObject.getForwardedName();
                            if (this.currentForwardNameString != null) {
                            }
                            z = false;
                            return z;
                        }
                    }
                    return false;
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
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                }

                protected void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    setTranslationX(0.0f);
                    this.avatarImage.onAttachedToWindow();
                    this.avatarImage.setParentView((View) getParent());
                    this.replyImageReceiver.onAttachedToWindow();
                    this.locationImageReceiver.onAttachedToWindow();
                    if (!this.drawPhotoImage) {
                        updateButtonState(false);
                    } else if (this.photoImage.onAttachedToWindow()) {
                        updateButtonState(false);
                    }
                    if (this.currentMessageObject != null && this.currentMessageObject.isRoundVideo()) {
                        checkRoundVideoPlayback(true);
                    }
                }

                public void checkRoundVideoPlayback(boolean z) {
                    if (z) {
                        z = !MediaController.getInstance().getPlayingMessageObject();
                    }
                    this.photoImage.setAllowStartAnimation(z);
                    if (z) {
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
                        if (((URLSpanNoUnderline) this.pressedLink).getURL().startsWith("/")) {
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

                public void setCheckPressed(boolean z, boolean z2) {
                    this.isCheckPressed = z;
                    this.isPressed = z2;
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

                public void setHighlighted(boolean z) {
                    if (this.isHighlighted != z) {
                        this.isHighlighted = z;
                        if (this.isHighlighted) {
                            this.isHighlightedAnimated = false;
                            this.highlightProgress = 0;
                        } else {
                            this.lastHighlightProgressTime = System.currentTimeMillis();
                            this.isHighlightedAnimated = true;
                            this.highlightProgress = true;
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

                public void setPressed(boolean z) {
                    super.setPressed(z);
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

                public void onSeekBarDrag(float f) {
                    if (this.currentMessageObject != null) {
                        this.currentMessageObject.audioProgress = f;
                        MediaController.getInstance().seekToProgress(this.currentMessageObject, f);
                    }
                }

                private void updateWaveform() {
                    if (this.currentMessageObject != null) {
                        if (this.documentAttachType == 3) {
                            boolean z = false;
                            for (int i = 0; i < this.documentAttach.attributes.size(); i++) {
                                DocumentAttribute documentAttribute = (DocumentAttribute) this.documentAttach.attributes.get(i);
                                if (documentAttribute instanceof TL_documentAttributeAudio) {
                                    if (documentAttribute.waveform == null || documentAttribute.waveform.length == 0) {
                                        MediaController.getInstance().generateWaveform(this.currentMessageObject);
                                    }
                                    if (documentAttribute.waveform != null) {
                                        z = true;
                                    }
                                    this.useSeekBarWaweform = z;
                                    this.seekBarWaveform.setWaveform(documentAttribute.waveform);
                                }
                            }
                        }
                    }
                }

                private int createDocumentLayout(int i, MessageObject messageObject) {
                    ChatMessageCell chatMessageCell = this;
                    int i2 = i;
                    MessageObject messageObject2 = messageObject;
                    if (messageObject2.type == 0) {
                        chatMessageCell.documentAttach = messageObject2.messageOwner.media.webpage.document;
                    } else {
                        chatMessageCell.documentAttach = messageObject2.messageOwner.media.document;
                    }
                    int i3 = 0;
                    if (chatMessageCell.documentAttach == null) {
                        return 0;
                    }
                    int i4;
                    int dp;
                    if (MessageObject.isVoiceDocument(chatMessageCell.documentAttach)) {
                        chatMessageCell.documentAttachType = 3;
                        for (i4 = 0; i4 < chatMessageCell.documentAttach.attributes.size(); i4++) {
                            DocumentAttribute documentAttribute = (DocumentAttribute) chatMessageCell.documentAttach.attributes.get(i4);
                            if (documentAttribute instanceof TL_documentAttributeAudio) {
                                i4 = documentAttribute.duration;
                                break;
                            }
                        }
                        i4 = 0;
                        chatMessageCell.widthBeforeNewTimeLine = (i2 - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil((double) Theme.chat_audioTimePaint.measureText("00:00")));
                        chatMessageCell.availableTimeWidth = i2 - AndroidUtilities.dp(18.0f);
                        measureTime(messageObject2);
                        dp = AndroidUtilities.dp(174.0f) + chatMessageCell.timeWidth;
                        if (!chatMessageCell.hasLinkPreview) {
                            chatMessageCell.backgroundWidth = Math.min(i2, dp + (i4 * AndroidUtilities.dp(10.0f)));
                        }
                        chatMessageCell.seekBarWaveform.setMessageObject(messageObject2);
                        return 0;
                    } else if (MessageObject.isMusicDocument(chatMessageCell.documentAttach)) {
                        chatMessageCell.documentAttachType = 5;
                        i2 -= AndroidUtilities.dp(86.0f);
                        char c = '\n';
                        char c2 = ' ';
                        chatMessageCell.songLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicTitle().replace('\n', ' '), Theme.chat_audioTitlePaint, (float) (i2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), Theme.chat_audioTitlePaint, i2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        if (chatMessageCell.songLayout.getLineCount() > 0) {
                            chatMessageCell.songX = -((int) Math.ceil((double) chatMessageCell.songLayout.getLineLeft(0)));
                        }
                        chatMessageCell.performerLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicAuthor().replace(c, c2), Theme.chat_audioPerformerPaint, (float) i2, TruncateAt.END), Theme.chat_audioPerformerPaint, i2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        if (chatMessageCell.performerLayout.getLineCount() > 0) {
                            chatMessageCell.performerX = -((int) Math.ceil((double) chatMessageCell.performerLayout.getLineLeft(0)));
                        }
                        for (i2 = 0; i2 < chatMessageCell.documentAttach.attributes.size(); i2++) {
                            r3 = (DocumentAttribute) chatMessageCell.documentAttach.attributes.get(i2);
                            if (r3 instanceof TL_documentAttributeAudio) {
                                i2 = r3.duration;
                                break;
                            }
                        }
                        i2 = 0;
                        TextPaint textPaint = Theme.chat_audioTimePaint;
                        r6 = new Object[4];
                        dp = i2 / 60;
                        r6[0] = Integer.valueOf(dp);
                        i2 %= 60;
                        r6[1] = Integer.valueOf(i2);
                        r6[2] = Integer.valueOf(dp);
                        r6[3] = Integer.valueOf(i2);
                        i2 = (int) Math.ceil((double) textPaint.measureText(String.format("%d:%02d / %d:%02d", r6)));
                        chatMessageCell.widthBeforeNewTimeLine = (chatMessageCell.backgroundWidth - AndroidUtilities.dp(86.0f)) - i2;
                        chatMessageCell.availableTimeWidth = chatMessageCell.backgroundWidth - AndroidUtilities.dp(28.0f);
                        return i2;
                    } else if (MessageObject.isVideoDocument(chatMessageCell.documentAttach)) {
                        chatMessageCell.documentAttachType = 4;
                        if (!messageObject.needDrawBluredPreview()) {
                            for (i2 = 0; i2 < chatMessageCell.documentAttach.attributes.size(); i2++) {
                                r3 = (DocumentAttribute) chatMessageCell.documentAttach.attributes.get(i2);
                                if (r3 instanceof TL_documentAttributeVideo) {
                                    i2 = r3.duration;
                                    break;
                                }
                            }
                            i2 = 0;
                            i2 -= (i2 / 60) * 60;
                            CharSequence format = String.format("%d:%02d, %s", new Object[]{Integer.valueOf(r3), Integer.valueOf(i2), AndroidUtilities.formatFileSize((long) chatMessageCell.documentAttach.size)});
                            chatMessageCell.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(format));
                            chatMessageCell.infoLayout = new StaticLayout(format, Theme.chat_infoPaint, chatMessageCell.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        }
                        return 0;
                    } else {
                        StringBuilder stringBuilder;
                        CharSequence stringBuilder2;
                        CharSequence ellipsize;
                        boolean z = (chatMessageCell.documentAttach.mime_type != null && chatMessageCell.documentAttach.mime_type.toLowerCase().startsWith("image/")) || !(chatMessageCell.documentAttach.thumb == null || (chatMessageCell.documentAttach.thumb instanceof TL_photoSizeEmpty) || (chatMessageCell.documentAttach.thumb.location instanceof TL_fileLocationUnavailable));
                        chatMessageCell.drawPhotoImage = z;
                        if (!chatMessageCell.drawPhotoImage) {
                            i2 += AndroidUtilities.dp(30.0f);
                        }
                        chatMessageCell.documentAttachType = 1;
                        String documentFileName = FileLoader.getDocumentFileName(chatMessageCell.documentAttach);
                        if (documentFileName != null) {
                            if (documentFileName.length() == 0) {
                            }
                            chatMessageCell.docTitleLayout = StaticLayoutEx.createStaticLayout(documentFileName, Theme.chat_docNamePaint, i2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TruncateAt.MIDDLE, i2, chatMessageCell.drawPhotoImage ? 2 : 1);
                            chatMessageCell.docTitleOffsetX = Integer.MIN_VALUE;
                            if (chatMessageCell.docTitleLayout != null || chatMessageCell.docTitleLayout.getLineCount() <= 0) {
                                chatMessageCell.docTitleOffsetX = 0;
                                i4 = i2;
                            } else {
                                i4 = 0;
                                while (i3 < chatMessageCell.docTitleLayout.getLineCount()) {
                                    i4 = Math.max(i4, (int) Math.ceil((double) chatMessageCell.docTitleLayout.getLineWidth(i3)));
                                    chatMessageCell.docTitleOffsetX = Math.max(chatMessageCell.docTitleOffsetX, (int) Math.ceil((double) (-chatMessageCell.docTitleLayout.getLineLeft(i3))));
                                    i3++;
                                }
                                i4 = Math.min(i2, i4);
                            }
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(AndroidUtilities.formatFileSize((long) chatMessageCell.documentAttach.size));
                            stringBuilder.append(" ");
                            stringBuilder.append(FileLoader.getDocumentExtension(chatMessageCell.documentAttach));
                            stringBuilder2 = stringBuilder.toString();
                            chatMessageCell.infoWidth = Math.min(i2 - AndroidUtilities.dp(30.0f), (int) Math.ceil((double) Theme.chat_infoPaint.measureText(stringBuilder2)));
                            ellipsize = TextUtils.ellipsize(stringBuilder2, Theme.chat_infoPaint, (float) chatMessageCell.infoWidth, TruncateAt.END);
                            if (chatMessageCell.infoWidth < 0) {
                                chatMessageCell.infoWidth = AndroidUtilities.dp(10.0f);
                            }
                            chatMessageCell.infoLayout = new StaticLayout(ellipsize, Theme.chat_infoPaint, chatMessageCell.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            if (chatMessageCell.drawPhotoImage) {
                                chatMessageCell.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, AndroidUtilities.getPhotoSize());
                                chatMessageCell.photoImage.setNeedsQualityThumb(true);
                                chatMessageCell.photoImage.setShouldGenerateQualityThumb(true);
                                chatMessageCell.photoImage.setParentMessageObject(messageObject2);
                                if (chatMessageCell.currentPhotoObject == null) {
                                    chatMessageCell.currentPhotoFilter = "86_86_b";
                                    chatMessageCell.photoImage.setImage(null, null, null, null, chatMessageCell.currentPhotoObject.location, chatMessageCell.currentPhotoFilter, 0, null, 1);
                                } else {
                                    chatMessageCell.photoImage.setImageBitmap((BitmapDrawable) null);
                                }
                            }
                            return i4;
                        }
                        documentFileName = LocaleController.getString("AttachDocument", C0446R.string.AttachDocument);
                        if (chatMessageCell.drawPhotoImage) {
                        }
                        chatMessageCell.docTitleLayout = StaticLayoutEx.createStaticLayout(documentFileName, Theme.chat_docNamePaint, i2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TruncateAt.MIDDLE, i2, chatMessageCell.drawPhotoImage ? 2 : 1);
                        chatMessageCell.docTitleOffsetX = Integer.MIN_VALUE;
                        if (chatMessageCell.docTitleLayout != null) {
                        }
                        chatMessageCell.docTitleOffsetX = 0;
                        i4 = i2;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(AndroidUtilities.formatFileSize((long) chatMessageCell.documentAttach.size));
                        stringBuilder.append(" ");
                        stringBuilder.append(FileLoader.getDocumentExtension(chatMessageCell.documentAttach));
                        stringBuilder2 = stringBuilder.toString();
                        chatMessageCell.infoWidth = Math.min(i2 - AndroidUtilities.dp(30.0f), (int) Math.ceil((double) Theme.chat_infoPaint.measureText(stringBuilder2)));
                        ellipsize = TextUtils.ellipsize(stringBuilder2, Theme.chat_infoPaint, (float) chatMessageCell.infoWidth, TruncateAt.END);
                        try {
                            if (chatMessageCell.infoWidth < 0) {
                                chatMessageCell.infoWidth = AndroidUtilities.dp(10.0f);
                            }
                            chatMessageCell.infoLayout = new StaticLayout(ellipsize, Theme.chat_infoPaint, chatMessageCell.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        if (chatMessageCell.drawPhotoImage) {
                            chatMessageCell.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, AndroidUtilities.getPhotoSize());
                            chatMessageCell.photoImage.setNeedsQualityThumb(true);
                            chatMessageCell.photoImage.setShouldGenerateQualityThumb(true);
                            chatMessageCell.photoImage.setParentMessageObject(messageObject2);
                            if (chatMessageCell.currentPhotoObject == null) {
                                chatMessageCell.photoImage.setImageBitmap((BitmapDrawable) null);
                            } else {
                                chatMessageCell.currentPhotoFilter = "86_86_b";
                                chatMessageCell.photoImage.setImage(null, null, null, null, chatMessageCell.currentPhotoObject.location, chatMessageCell.currentPhotoFilter, 0, null, 1);
                            }
                        }
                        return i4;
                    }
                }

                private void calcBackgroundWidth(int i, int i2, int i3) {
                    if (!(this.hasLinkPreview || this.hasOldCaptionPreview || this.hasGamePreview || this.hasInvoicePreview || i - this.currentMessageObject.lastLineWidth < i2)) {
                        if (this.currentMessageObject.hasRtl == 0) {
                            i = i3 - this.currentMessageObject.lastLineWidth;
                            if (i < 0 || i > i2) {
                                this.backgroundWidth = Math.max(i3, this.currentMessageObject.lastLineWidth + i2) + AndroidUtilities.dp(31.0f);
                                return;
                            } else {
                                this.backgroundWidth = ((i3 + i2) - i) + AndroidUtilities.dp(31.0f);
                                return;
                            }
                        }
                    }
                    this.totalHeight += AndroidUtilities.dp(NUM);
                    this.hasNewLineForTime = true;
                    this.backgroundWidth = Math.max(i3, this.currentMessageObject.lastLineWidth) + AndroidUtilities.dp(31.0f);
                    this.backgroundWidth = Math.max(this.backgroundWidth, (this.currentMessageObject.isOutOwner() != 0 ? this.timeWidth + AndroidUtilities.dp(NUM) : this.timeWidth) + AndroidUtilities.dp(31.0f));
                }

                public void setHighlightedText(String str) {
                    if (!(this.currentMessageObject.messageOwner.message == null || this.currentMessageObject == null || this.currentMessageObject.type != 0 || TextUtils.isEmpty(this.currentMessageObject.messageText))) {
                        if (str != null) {
                            int indexOf = TextUtils.indexOf(this.currentMessageObject.messageOwner.message.toLowerCase(), str.toLowerCase());
                            if (indexOf == -1) {
                                if (this.urlPathSelection.isEmpty() == null) {
                                    this.linkSelectionBlockNum = -1;
                                    resetUrlPaths(true);
                                    invalidate();
                                }
                                return;
                            }
                            str = str.length() + indexOf;
                            int i = 0;
                            while (i < this.currentMessageObject.textLayoutBlocks.size()) {
                                TextLayoutBlock textLayoutBlock = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i);
                                if (indexOf < textLayoutBlock.charactersOffset || indexOf >= textLayoutBlock.charactersOffset + textLayoutBlock.textLayout.getText().length()) {
                                    i++;
                                } else {
                                    this.linkSelectionBlockNum = i;
                                    resetUrlPaths(true);
                                    try {
                                        Path obtainNewUrlPath = obtainNewUrlPath(true);
                                        int length = textLayoutBlock.textLayout.getText().length();
                                        obtainNewUrlPath.setCurrentLayout(textLayoutBlock.textLayout, indexOf, 0.0f);
                                        textLayoutBlock.textLayout.getSelectionPath(indexOf, str - textLayoutBlock.charactersOffset, obtainNewUrlPath);
                                        if (str >= textLayoutBlock.charactersOffset + length) {
                                            for (i++; i < this.currentMessageObject.textLayoutBlocks.size(); i++) {
                                                TextLayoutBlock textLayoutBlock2 = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i);
                                                int length2 = textLayoutBlock2.textLayout.getText().length();
                                                Path obtainNewUrlPath2 = obtainNewUrlPath(true);
                                                obtainNewUrlPath2.setCurrentLayout(textLayoutBlock2.textLayout, 0, (float) textLayoutBlock2.height);
                                                textLayoutBlock2.textLayout.getSelectionPath(0, str - textLayoutBlock2.charactersOffset, obtainNewUrlPath2);
                                                if (str < (textLayoutBlock.charactersOffset + length2) - 1) {
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
                            return;
                        }
                    }
                    if (this.urlPathSelection.isEmpty() == null) {
                        this.linkSelectionBlockNum = -1;
                        resetUrlPaths(true);
                        invalidate();
                    }
                }

                protected boolean verifyDrawable(Drawable drawable) {
                    if (!super.verifyDrawable(drawable)) {
                        if (drawable != this.instantViewSelectorDrawable) {
                            return null;
                        }
                    }
                    return true;
                }

                private boolean isCurrentLocationTimeExpired(MessageObject messageObject) {
                    boolean z = false;
                    if (this.currentMessageObject.messageOwner.media.period % 60 == 0) {
                        if (Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) > messageObject.messageOwner.media.period) {
                            z = true;
                        }
                        return z;
                    }
                    if (Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) > messageObject.messageOwner.media.period - 5) {
                        z = true;
                    }
                    return z;
                }

                private void checkLocationExpired() {
                    if (this.currentMessageObject != null) {
                        boolean isCurrentLocationTimeExpired = isCurrentLocationTimeExpired(this.currentMessageObject);
                        if (isCurrentLocationTimeExpired != this.locationExpired) {
                            this.locationExpired = isCurrentLocationTimeExpired;
                            if (this.locationExpired) {
                                MessageObject messageObject = this.currentMessageObject;
                                this.currentMessageObject = null;
                                setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
                            } else {
                                AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
                                this.scheduledInvalidate = true;
                                this.docTitleLayout = new StaticLayout(LocaleController.getString("AttachLiveLocation", C0446R.string.AttachLiveLocation), Theme.chat_locationTitlePaint, this.backgroundWidth - AndroidUtilities.dp(91.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            }
                        }
                    }
                }

                public void setMessageObject(org.telegram.messenger.MessageObject r69, org.telegram.messenger.MessageObject.GroupedMessages r70, boolean r71, boolean r72) {
                    /* JADX: method processing error */
/*
Error: java.lang.IndexOutOfBoundsException: bitIndex < 0: -1
	at java.util.BitSet.get(BitSet.java:623)
	at jadx.core.dex.visitors.CodeShrinker$ArgsInfo.usedArgAssign(CodeShrinker.java:138)
	at jadx.core.dex.visitors.CodeShrinker$ArgsInfo.access$300(CodeShrinker.java:43)
	at jadx.core.dex.visitors.CodeShrinker.canMoveBetweenBlocks(CodeShrinker.java:282)
	at jadx.core.dex.visitors.CodeShrinker.shrinkBlock(CodeShrinker.java:232)
	at jadx.core.dex.visitors.CodeShrinker.shrinkMethod(CodeShrinker.java:38)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.checkArrayForEach(LoopRegionVisitor.java:196)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.checkForIndexedLoop(LoopRegionVisitor.java:119)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.processLoopRegion(LoopRegionVisitor.java:65)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.enterRegion(LoopRegionVisitor.java:52)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:56)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.visit(LoopRegionVisitor.java:46)
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
                    r68 = this;
                    r1 = r68;
                    r2 = r69;
                    r3 = r70;
                    r4 = r71;
                    r5 = r72;
                    r6 = r69.checkLayout();
                    r7 = 0;
                    if (r6 != 0) goto L_0x001d;
                L_0x0011:
                    r6 = r1.currentPosition;
                    if (r6 == 0) goto L_0x001f;
                L_0x0015:
                    r6 = r1.lastHeight;
                    r8 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r8 = r8.y;
                    if (r6 == r8) goto L_0x001f;
                L_0x001d:
                    r1.currentMessageObject = r7;
                L_0x001f:
                    r6 = r1.currentMessageObject;
                    r8 = 1;
                    r9 = 0;
                    if (r6 == 0) goto L_0x0034;
                L_0x0025:
                    r6 = r1.currentMessageObject;
                    r6 = r6.getId();
                    r10 = r69.getId();
                    if (r6 == r10) goto L_0x0032;
                L_0x0031:
                    goto L_0x0034;
                L_0x0032:
                    r6 = r9;
                    goto L_0x0035;
                L_0x0034:
                    r6 = r8;
                L_0x0035:
                    r10 = r1.currentMessageObject;
                    if (r10 != r2) goto L_0x0040;
                L_0x0039:
                    r10 = r2.forceUpdate;
                    if (r10 == 0) goto L_0x003e;
                L_0x003d:
                    goto L_0x0040;
                L_0x003e:
                    r10 = r9;
                    goto L_0x0041;
                L_0x0040:
                    r10 = r8;
                L_0x0041:
                    r11 = r1.currentMessageObject;
                    if (r11 != r2) goto L_0x0051;
                L_0x0045:
                    r11 = r68.isUserDataChanged();
                    if (r11 != 0) goto L_0x004f;
                L_0x004b:
                    r11 = r1.photoNotSet;
                    if (r11 == 0) goto L_0x0051;
                L_0x004f:
                    r11 = r8;
                    goto L_0x0052;
                L_0x0051:
                    r11 = r9;
                L_0x0052:
                    r12 = r1.currentMessagesGroup;
                    if (r3 == r12) goto L_0x0058;
                L_0x0056:
                    r12 = r8;
                    goto L_0x0059;
                L_0x0058:
                    r12 = r9;
                L_0x0059:
                    if (r12 != 0) goto L_0x007a;
                L_0x005b:
                    if (r3 == 0) goto L_0x007a;
                L_0x005d:
                    r12 = r3.messages;
                    r12 = r12.size();
                    if (r12 <= r8) goto L_0x0072;
                L_0x0065:
                    r12 = r1.currentMessagesGroup;
                    r12 = r12.positions;
                    r13 = r1.currentMessageObject;
                    r12 = r12.get(r13);
                    r12 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r12;
                    goto L_0x0073;
                L_0x0072:
                    r12 = r7;
                L_0x0073:
                    r13 = r1.currentPosition;
                    if (r12 == r13) goto L_0x0079;
                L_0x0077:
                    r12 = r8;
                    goto L_0x007a;
                L_0x0079:
                    r12 = r9;
                L_0x007a:
                    if (r10 != 0) goto L_0x0094;
                L_0x007c:
                    if (r11 != 0) goto L_0x0094;
                L_0x007e:
                    if (r12 != 0) goto L_0x0094;
                L_0x0080:
                    r12 = r68.isPhotoDataChanged(r69);
                    if (r12 != 0) goto L_0x0094;
                L_0x0086:
                    r12 = r1.pinnedBottom;
                    if (r12 != r4) goto L_0x0094;
                L_0x008a:
                    r12 = r1.pinnedTop;
                    if (r12 == r5) goto L_0x008f;
                L_0x008e:
                    goto L_0x0094;
                L_0x008f:
                    r2 = r1;
                    r43 = r11;
                    goto L_0x2e5e;
                L_0x0094:
                    r1.pinnedBottom = r4;
                    r1.pinnedTop = r5;
                    r4 = -2;
                    r1.lastTime = r4;
                    r1.isHighlightedAnimated = r9;
                    r4 = -1;
                    r1.widthBeforeNewTimeLine = r4;
                    r1.currentMessageObject = r2;
                    r1.currentMessagesGroup = r3;
                    r3 = r1.currentMessagesGroup;
                    if (r3 == 0) goto L_0x00c7;
                L_0x00a8:
                    r3 = r1.currentMessagesGroup;
                    r3 = r3.posArray;
                    r3 = r3.size();
                    if (r3 <= r8) goto L_0x00c7;
                L_0x00b2:
                    r3 = r1.currentMessagesGroup;
                    r3 = r3.positions;
                    r5 = r1.currentMessageObject;
                    r3 = r3.get(r5);
                    r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3;
                    r1.currentPosition = r3;
                    r3 = r1.currentPosition;
                    if (r3 != 0) goto L_0x00cb;
                L_0x00c4:
                    r1.currentMessagesGroup = r7;
                    goto L_0x00cb;
                L_0x00c7:
                    r1.currentMessagesGroup = r7;
                    r1.currentPosition = r7;
                L_0x00cb:
                    r3 = r1.pinnedTop;
                    if (r3 == 0) goto L_0x00dd;
                L_0x00cf:
                    r3 = r1.currentPosition;
                    if (r3 == 0) goto L_0x00db;
                L_0x00d3:
                    r3 = r1.currentPosition;
                    r3 = r3.flags;
                    r3 = r3 & 4;
                    if (r3 == 0) goto L_0x00dd;
                L_0x00db:
                    r3 = r8;
                    goto L_0x00de;
                L_0x00dd:
                    r3 = r9;
                L_0x00de:
                    r1.drawPinnedTop = r3;
                    r3 = r1.pinnedBottom;
                    r5 = 8;
                    if (r3 == 0) goto L_0x00f3;
                L_0x00e6:
                    r3 = r1.currentPosition;
                    if (r3 == 0) goto L_0x00f1;
                L_0x00ea:
                    r3 = r1.currentPosition;
                    r3 = r3.flags;
                    r3 = r3 & r5;
                    if (r3 == 0) goto L_0x00f3;
                L_0x00f1:
                    r3 = r8;
                    goto L_0x00f4;
                L_0x00f3:
                    r3 = r9;
                L_0x00f4:
                    r1.drawPinnedBottom = r3;
                    r3 = r1.photoImage;
                    r3.setCrossfadeWithOldImage(r9);
                    r3 = r2.messageOwner;
                    r3 = r3.send_state;
                    r1.lastSendState = r3;
                    r3 = r2.messageOwner;
                    r3 = r3.destroyTime;
                    r1.lastDeleteDate = r3;
                    r3 = r2.messageOwner;
                    r3 = r3.views;
                    r1.lastViewsCount = r3;
                    r1.isPressed = r9;
                    r1.gamePreviewPressed = r9;
                    r1.isCheckPressed = r8;
                    r1.hasNewLineForTime = r9;
                    r3 = r1.isChat;
                    if (r3 == 0) goto L_0x0131;
                L_0x0119:
                    r3 = r69.isOutOwner();
                    if (r3 != 0) goto L_0x0131;
                L_0x011f:
                    r3 = r69.needDrawAvatar();
                    if (r3 == 0) goto L_0x0131;
                L_0x0125:
                    r3 = r1.currentPosition;
                    if (r3 == 0) goto L_0x012f;
                L_0x0129:
                    r3 = r1.currentPosition;
                    r3 = r3.edge;
                    if (r3 == 0) goto L_0x0131;
                L_0x012f:
                    r3 = r8;
                    goto L_0x0132;
                L_0x0131:
                    r3 = r9;
                L_0x0132:
                    r1.isAvatarVisible = r3;
                    r1.wasLayout = r9;
                    r1.drwaShareGoIcon = r9;
                    r1.groupPhotoInvisible = r9;
                    r3 = r68.checkNeedDrawShareButton(r69);
                    r1.drawShareButton = r3;
                    r1.replyNameLayout = r7;
                    r1.adminLayout = r7;
                    r1.replyTextLayout = r7;
                    r1.replyNameWidth = r9;
                    r1.replyTextWidth = r9;
                    r1.viaWidth = r9;
                    r1.viaNameWidth = r9;
                    r1.addedCaptionHeight = r9;
                    r1.currentReplyPhoto = r7;
                    r1.currentUser = r7;
                    r1.currentChat = r7;
                    r1.currentViaBotUser = r7;
                    r1.drawNameLayout = r9;
                    r3 = r1.scheduledInvalidate;
                    if (r3 == 0) goto L_0x0165;
                L_0x015e:
                    r3 = r1.invalidateRunnable;
                    org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r3);
                    r1.scheduledInvalidate = r9;
                L_0x0165:
                    r1.resetPressedLink(r4);
                    r2.forceUpdate = r9;
                    r1.drawPhotoImage = r9;
                    r1.hasLinkPreview = r9;
                    r1.hasOldCaptionPreview = r9;
                    r1.hasGamePreview = r9;
                    r1.hasInvoicePreview = r9;
                    r1.instantButtonPressed = r9;
                    r1.instantPressed = r9;
                    r3 = android.os.Build.VERSION.SDK_INT;
                    r12 = 21;
                    if (r3 < r12) goto L_0x018e;
                L_0x017e:
                    r3 = r1.instantViewSelectorDrawable;
                    if (r3 == 0) goto L_0x018e;
                L_0x0182:
                    r3 = r1.instantViewSelectorDrawable;
                    r3.setVisible(r9, r9);
                    r3 = r1.instantViewSelectorDrawable;
                    r12 = android.util.StateSet.NOTHING;
                    r3.setState(r12);
                L_0x018e:
                    r1.linkPreviewPressed = r9;
                    r1.buttonPressed = r9;
                    r1.miniButtonPressed = r9;
                    r1.pressedBotButton = r4;
                    r1.linkPreviewHeight = r9;
                    r1.mediaOffsetY = r9;
                    r1.documentAttachType = r9;
                    r1.documentAttach = r7;
                    r1.descriptionLayout = r7;
                    r1.titleLayout = r7;
                    r1.videoInfoLayout = r7;
                    r1.photosCountLayout = r7;
                    r1.siteNameLayout = r7;
                    r1.authorLayout = r7;
                    r1.captionLayout = r7;
                    r1.captionOffsetX = r9;
                    r1.currentCaption = r7;
                    r1.docTitleLayout = r7;
                    r1.drawImageButton = r9;
                    r1.currentPhotoObject = r7;
                    r1.currentPhotoObjectThumb = r7;
                    r1.currentPhotoFilter = r7;
                    r1.infoLayout = r7;
                    r1.cancelLoading = r9;
                    r1.buttonState = r4;
                    r1.miniButtonState = r4;
                    r1.hasMiniProgress = r9;
                    r1.currentUrl = r7;
                    r1.photoNotSet = r9;
                    r1.drawBackground = r8;
                    r1.drawName = r9;
                    r1.useSeekBarWaweform = r9;
                    r1.drawInstantView = r9;
                    r1.drawInstantViewType = r9;
                    r1.drawForwardedName = r9;
                    r1.mediaBackground = r9;
                    r1.availableTimeWidth = r9;
                    r3 = r1.photoImage;
                    r3.setForceLoading(r9);
                    r3 = r1.photoImage;
                    r3.setNeedsQualityThumb(r9);
                    r3 = r1.photoImage;
                    r3.setShouldGenerateQualityThumb(r9);
                    r3 = r1.photoImage;
                    r3.setAllowDecodeSingleFrame(r9);
                    r3 = r1.photoImage;
                    r3.setParentMessageObject(r7);
                    r3 = r1.photoImage;
                    r12 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
                    r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
                    r3.setRoundRadius(r12);
                    if (r10 == 0) goto L_0x0204;
                L_0x01fe:
                    r1.firstVisibleBlockNum = r9;
                    r1.lastVisibleBlockNum = r9;
                    r1.needNewVisiblePart = r8;
                L_0x0204:
                    r3 = r2.type;
                    r15 = 3;
                    if (r3 != 0) goto L_0x14d4;
                L_0x0209:
                    r1.drawForwardedName = r8;
                    r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r3 == 0) goto L_0x024f;
                L_0x0211:
                    r3 = r1.isChat;
                    if (r3 == 0) goto L_0x0230;
                L_0x0215:
                    r3 = r69.isOutOwner();
                    if (r3 != 0) goto L_0x0230;
                L_0x021b:
                    r3 = r69.needDrawAvatar();
                    if (r3 == 0) goto L_0x0230;
                L_0x0221:
                    r3 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r10 = NUM; // 0x42f40000 float:122.0 double:5.54977537E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r10;
                    r1.drawName = r8;
                    goto L_0x029b;
                L_0x0230:
                    r3 = r2.messageOwner;
                    r3 = r3.to_id;
                    r3 = r3.channel_id;
                    if (r3 == 0) goto L_0x0240;
                L_0x0238:
                    r3 = r69.isOutOwner();
                    if (r3 != 0) goto L_0x0240;
                L_0x023e:
                    r3 = r8;
                    goto L_0x0241;
                L_0x0240:
                    r3 = r9;
                L_0x0241:
                    r1.drawName = r3;
                    r3 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r10 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r10;
                    goto L_0x029b;
                L_0x024f:
                    r3 = r1.isChat;
                    if (r3 == 0) goto L_0x0275;
                L_0x0253:
                    r3 = r69.isOutOwner();
                    if (r3 != 0) goto L_0x0275;
                L_0x0259:
                    r3 = r69.needDrawAvatar();
                    if (r3 == 0) goto L_0x0275;
                L_0x025f:
                    r3 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r3 = r3.x;
                    r10 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r10 = r10.y;
                    r3 = java.lang.Math.min(r3, r10);
                    r10 = NUM; // 0x42f40000 float:122.0 double:5.54977537E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r10;
                    r1.drawName = r8;
                    goto L_0x029b;
                L_0x0275:
                    r3 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r3 = r3.x;
                    r10 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r10 = r10.y;
                    r3 = java.lang.Math.min(r3, r10);
                    r10 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r10;
                    r10 = r2.messageOwner;
                    r10 = r10.to_id;
                    r10 = r10.channel_id;
                    if (r10 == 0) goto L_0x0298;
                L_0x0290:
                    r10 = r69.isOutOwner();
                    if (r10 != 0) goto L_0x0298;
                L_0x0296:
                    r10 = r8;
                    goto L_0x0299;
                L_0x0298:
                    r10 = r9;
                L_0x0299:
                    r1.drawName = r10;
                L_0x029b:
                    r1.availableTimeWidth = r3;
                    r10 = r69.isRoundVideo();
                    if (r10 == 0) goto L_0x02c7;
                L_0x02a3:
                    r10 = r1.availableTimeWidth;
                    r12 = (double) r10;
                    r10 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
                    r14 = "00:00";
                    r10 = r10.measureText(r14);
                    r9 = (double) r10;
                    r9 = java.lang.Math.ceil(r9);
                    r14 = r69.isOutOwner();
                    if (r14 == 0) goto L_0x02bb;
                L_0x02b9:
                    r14 = 0;
                    goto L_0x02c1;
                L_0x02bb:
                    r14 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
                    r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
                L_0x02c1:
                    r4 = (double) r14;
                    r9 = r9 + r4;
                    r12 = r12 - r9;
                    r4 = (int) r12;
                    r1.availableTimeWidth = r4;
                L_0x02c7:
                    r68.measureTime(r69);
                    r4 = r1.timeWidth;
                    r5 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r4 = r4 + r5;
                    r5 = r69.isOutOwner();
                    if (r5 == 0) goto L_0x02e0;
                L_0x02d9:
                    r5 = NUM; // 0x41a40000 float:20.5 double:5.44098164E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r4 = r4 + r5;
                L_0x02e0:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
                    if (r5 == 0) goto L_0x02f4;
                L_0x02e8:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.game;
                    r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_game;
                    if (r5 == 0) goto L_0x02f4;
                L_0x02f2:
                    r5 = r8;
                    goto L_0x02f5;
                L_0x02f4:
                    r5 = 0;
                L_0x02f5:
                    r1.hasGamePreview = r5;
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
                    r1.hasInvoicePreview = r5;
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
                    if (r5 == 0) goto L_0x0313;
                L_0x0307:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.webpage;
                    r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
                    if (r5 == 0) goto L_0x0313;
                L_0x0311:
                    r5 = r8;
                    goto L_0x0314;
                L_0x0313:
                    r5 = 0;
                L_0x0314:
                    r1.hasLinkPreview = r5;
                    r5 = r1.hasLinkPreview;
                    if (r5 == 0) goto L_0x0326;
                L_0x031a:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.webpage;
                    r5 = r5.cached_page;
                    if (r5 == 0) goto L_0x0326;
                L_0x0324:
                    r5 = r8;
                    goto L_0x0327;
                L_0x0326:
                    r5 = 0;
                L_0x0327:
                    r1.drawInstantView = r5;
                    r5 = r1.hasLinkPreview;
                    if (r5 == 0) goto L_0x0336;
                L_0x032d:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.webpage;
                    r5 = r5.site_name;
                    goto L_0x0337;
                L_0x0336:
                    r5 = r7;
                L_0x0337:
                    r9 = r1.hasLinkPreview;
                    if (r9 == 0) goto L_0x0344;
                L_0x033b:
                    r9 = r2.messageOwner;
                    r9 = r9.media;
                    r9 = r9.webpage;
                    r9 = r9.type;
                    goto L_0x0345;
                L_0x0344:
                    r9 = r7;
                L_0x0345:
                    r10 = r1.drawInstantView;
                    if (r10 != 0) goto L_0x0374;
                L_0x0349:
                    r5 = "telegram_channel";
                    r5 = r5.equals(r9);
                    if (r5 == 0) goto L_0x0357;
                L_0x0351:
                    r1.drawInstantView = r8;
                    r1.drawInstantViewType = r8;
                    goto L_0x0431;
                L_0x0357:
                    r5 = "telegram_megagroup";
                    r5 = r5.equals(r9);
                    if (r5 == 0) goto L_0x0366;
                L_0x035f:
                    r1.drawInstantView = r8;
                    r5 = 2;
                    r1.drawInstantViewType = r5;
                    goto L_0x0431;
                L_0x0366:
                    r5 = "telegram_message";
                    r5 = r5.equals(r9);
                    if (r5 == 0) goto L_0x0431;
                L_0x036e:
                    r1.drawInstantView = r8;
                    r1.drawInstantViewType = r15;
                    goto L_0x0431;
                L_0x0374:
                    if (r5 == 0) goto L_0x0431;
                L_0x0376:
                    r5 = r5.toLowerCase();
                    r10 = "instagram";
                    r10 = r5.equals(r10);
                    if (r10 != 0) goto L_0x0392;
                L_0x0382:
                    r10 = "twitter";
                    r5 = r5.equals(r10);
                    if (r5 != 0) goto L_0x0392;
                L_0x038a:
                    r5 = "telegram_album";
                    r5 = r5.equals(r9);
                    if (r5 == 0) goto L_0x0431;
                L_0x0392:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.webpage;
                    r5 = r5.cached_page;
                    r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_pageFull;
                    if (r5 == 0) goto L_0x0431;
                L_0x039e:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.webpage;
                    r5 = r5.photo;
                    r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_photo;
                    if (r5 != 0) goto L_0x03b8;
                L_0x03aa:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.webpage;
                    r5 = r5.document;
                    r5 = org.telegram.messenger.MessageObject.isVideoDocument(r5);
                    if (r5 == 0) goto L_0x0431;
                L_0x03b8:
                    r5 = 0;
                    r1.drawInstantView = r5;
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.webpage;
                    r5 = r5.cached_page;
                    r5 = r5.blocks;
                    r10 = r8;
                    r9 = 0;
                L_0x03c7:
                    r12 = r5.size();
                    if (r9 >= r12) goto L_0x03ef;
                L_0x03cd:
                    r12 = r5.get(r9);
                    r12 = (org.telegram.tgnet.TLRPC.PageBlock) r12;
                    r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
                    if (r13 == 0) goto L_0x03e0;
                L_0x03d7:
                    r12 = (org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow) r12;
                    r10 = r12.items;
                    r10 = r10.size();
                    goto L_0x03ec;
                L_0x03e0:
                    r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
                    if (r13 == 0) goto L_0x03ec;
                L_0x03e4:
                    r12 = (org.telegram.tgnet.TLRPC.TL_pageBlockCollage) r12;
                    r10 = r12.items;
                    r10 = r10.size();
                L_0x03ec:
                    r9 = r9 + 1;
                    goto L_0x03c7;
                L_0x03ef:
                    r5 = "Of";
                    r9 = NUM; // 0x7f0c048d float:1.8611555E38 double:1.053097974E-314;
                    r12 = 2;
                    r13 = new java.lang.Object[r12];
                    r12 = java.lang.Integer.valueOf(r8);
                    r14 = 0;
                    r13[r14] = r12;
                    r10 = java.lang.Integer.valueOf(r10);
                    r13[r8] = r10;
                    r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r13);
                    r9 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
                    r9 = r9.measureText(r5);
                    r9 = (double) r9;
                    r9 = java.lang.Math.ceil(r9);
                    r9 = (int) r9;
                    r1.photosCountWidth = r9;
                    r9 = new android.text.StaticLayout;
                    r25 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
                    r10 = r1.photosCountWidth;
                    r27 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r29 = 0;
                    r30 = 0;
                    r23 = r9;
                    r24 = r5;
                    r26 = r10;
                    r23.<init>(r24, r25, r26, r27, r28, r29, r30);
                    r1.photosCountLayout = r9;
                    r5 = r8;
                    goto L_0x0432;
                L_0x0431:
                    r5 = 0;
                L_0x0432:
                    r9 = android.os.Build.VERSION.SDK_INT;
                    r10 = 21;
                    if (r9 < r10) goto L_0x04a3;
                L_0x0438:
                    r9 = r1.drawInstantView;
                    if (r9 == 0) goto L_0x04a3;
                L_0x043c:
                    r9 = r1.instantViewSelectorDrawable;
                    if (r9 != 0) goto L_0x0483;
                L_0x0440:
                    r9 = new android.graphics.Paint;
                    r9.<init>(r8);
                    r10 = -1;
                    r9.setColor(r10);
                    r10 = new org.telegram.ui.Cells.ChatMessageCell$2;
                    r10.<init>(r9);
                    r9 = new android.content.res.ColorStateList;
                    r12 = new int[r8][];
                    r13 = android.util.StateSet.WILD_CARD;
                    r14 = 0;
                    r12[r14] = r13;
                    r13 = new int[r8];
                    r14 = r1.currentMessageObject;
                    r14 = r14.isOutOwner();
                    if (r14 == 0) goto L_0x0464;
                L_0x0461:
                    r14 = "chat_outPreviewInstantText";
                    goto L_0x0466;
                L_0x0464:
                    r14 = "chat_inPreviewInstantText";
                L_0x0466:
                    r14 = org.telegram.ui.ActionBar.Theme.getColor(r14);
                    r16 = NUM; // 0x5fffffff float:3.6893486E19 double:7.95748421E-315;
                    r14 = r14 & r16;
                    r16 = 0;
                    r13[r16] = r14;
                    r9.<init>(r12, r13);
                    r12 = new android.graphics.drawable.RippleDrawable;
                    r12.<init>(r9, r7, r10);
                    r1.instantViewSelectorDrawable = r12;
                    r9 = r1.instantViewSelectorDrawable;
                    r9.setCallback(r1);
                    goto L_0x049d;
                L_0x0483:
                    r9 = r1.instantViewSelectorDrawable;
                    r10 = r1.currentMessageObject;
                    r10 = r10.isOutOwner();
                    if (r10 == 0) goto L_0x0490;
                L_0x048d:
                    r10 = "chat_outPreviewInstantText";
                    goto L_0x0492;
                L_0x0490:
                    r10 = "chat_inPreviewInstantText";
                L_0x0492:
                    r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
                    r12 = NUM; // 0x5fffffff float:3.6893486E19 double:7.95748421E-315;
                    r10 = r10 & r12;
                    org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(r9, r10, r8);
                L_0x049d:
                    r9 = r1.instantViewSelectorDrawable;
                    r10 = 0;
                    r9.setVisible(r8, r10);
                L_0x04a3:
                    r1.backgroundWidth = r3;
                    r9 = r1.hasLinkPreview;
                    if (r9 != 0) goto L_0x04e2;
                L_0x04a9:
                    r9 = r1.hasGamePreview;
                    if (r9 != 0) goto L_0x04e2;
                L_0x04ad:
                    r9 = r1.hasInvoicePreview;
                    if (r9 != 0) goto L_0x04e2;
                L_0x04b1:
                    r9 = r2.lastLineWidth;
                    r9 = r3 - r9;
                    if (r9 >= r4) goto L_0x04b8;
                L_0x04b7:
                    goto L_0x04e2;
                L_0x04b8:
                    r9 = r1.backgroundWidth;
                    r10 = r2.lastLineWidth;
                    r9 = r9 - r10;
                    if (r9 < 0) goto L_0x04cf;
                L_0x04bf:
                    if (r9 > r4) goto L_0x04cf;
                L_0x04c1:
                    r10 = r1.backgroundWidth;
                    r10 = r10 + r4;
                    r10 = r10 - r9;
                    r9 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r10 = r10 + r9;
                    r1.backgroundWidth = r10;
                    goto L_0x0504;
                L_0x04cf:
                    r9 = r1.backgroundWidth;
                    r10 = r2.lastLineWidth;
                    r10 = r10 + r4;
                    r9 = java.lang.Math.max(r9, r10);
                    r10 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r10;
                    r1.backgroundWidth = r9;
                    goto L_0x0504;
                L_0x04e2:
                    r9 = r1.backgroundWidth;
                    r10 = r2.lastLineWidth;
                    r9 = java.lang.Math.max(r9, r10);
                    r10 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r10;
                    r1.backgroundWidth = r9;
                    r9 = r1.backgroundWidth;
                    r10 = r1.timeWidth;
                    r12 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
                    r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
                    r10 = r10 + r12;
                    r9 = java.lang.Math.max(r9, r10);
                    r1.backgroundWidth = r9;
                L_0x0504:
                    r9 = r1.backgroundWidth;
                    r10 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 - r10;
                    r1.availableTimeWidth = r9;
                    r9 = r69.isRoundVideo();
                    if (r9 == 0) goto L_0x0539;
                L_0x0515:
                    r9 = r1.availableTimeWidth;
                    r9 = (double) r9;
                    r12 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
                    r13 = "00:00";
                    r12 = r12.measureText(r13);
                    r12 = (double) r12;
                    r12 = java.lang.Math.ceil(r12);
                    r14 = r69.isOutOwner();
                    if (r14 == 0) goto L_0x052d;
                L_0x052b:
                    r14 = 0;
                    goto L_0x0533;
                L_0x052d:
                    r14 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
                    r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
                L_0x0533:
                    r7 = (double) r14;
                    r12 = r12 + r7;
                    r9 = r9 - r12;
                    r7 = (int) r9;
                    r1.availableTimeWidth = r7;
                L_0x0539:
                    r68.setMessageObjectInternal(r69);
                    r7 = r2.textWidth;
                    r8 = r1.hasGamePreview;
                    if (r8 != 0) goto L_0x0549;
                L_0x0542:
                    r8 = r1.hasInvoicePreview;
                    if (r8 == 0) goto L_0x0547;
                L_0x0546:
                    goto L_0x0549;
                L_0x0547:
                    r9 = 0;
                    goto L_0x054f;
                L_0x0549:
                    r8 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
                L_0x054f:
                    r7 = r7 + r9;
                    r1.backgroundWidth = r7;
                    r7 = r2.textHeight;
                    r8 = NUM; // 0x419c0000 float:19.5 double:5.43839131E-315;
                    r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
                    r7 = r7 + r8;
                    r8 = r1.namesOffset;
                    r7 = r7 + r8;
                    r1.totalHeight = r7;
                    r7 = r1.drawPinnedTop;
                    if (r7 == 0) goto L_0x056f;
                L_0x0564:
                    r7 = r1.namesOffset;
                    r8 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
                    r7 = r7 - r9;
                    r1.namesOffset = r7;
                L_0x056f:
                    r7 = r1.backgroundWidth;
                    r8 = r1.nameWidth;
                    r7 = java.lang.Math.max(r7, r8);
                    r8 = r1.forwardedNameWidth;
                    r7 = java.lang.Math.max(r7, r8);
                    r8 = r1.replyNameWidth;
                    r7 = java.lang.Math.max(r7, r8);
                    r8 = r1.replyTextWidth;
                    r7 = java.lang.Math.max(r7, r8);
                    r8 = r1.hasLinkPreview;
                    if (r8 != 0) goto L_0x05a8;
                L_0x058d:
                    r8 = r1.hasGamePreview;
                    if (r8 != 0) goto L_0x05a8;
                L_0x0591:
                    r8 = r1.hasInvoicePreview;
                    if (r8 == 0) goto L_0x0596;
                L_0x0595:
                    goto L_0x05a8;
                L_0x0596:
                    r5 = r1.photoImage;
                    r8 = 0;
                    r9 = r8;
                    r9 = (android.graphics.drawable.Drawable) r9;
                    r5.setImageBitmap(r9);
                    r1.calcBackgroundWidth(r3, r4, r7);
                    r44 = r6;
                    r43 = r11;
                    goto L_0x1900;
                L_0x05a8:
                    r8 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r8 == 0) goto L_0x05d8;
                L_0x05ae:
                    r8 = r1.isChat;
                    if (r8 == 0) goto L_0x05cc;
                L_0x05b2:
                    r8 = r69.needDrawAvatar();
                    if (r8 == 0) goto L_0x05cc;
                L_0x05b8:
                    r8 = r1.currentMessageObject;
                    r8 = r8.isOut();
                    if (r8 != 0) goto L_0x05cc;
                L_0x05c0:
                    r8 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r9 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r8 = r8 - r9;
                    goto L_0x0611;
                L_0x05cc:
                    r8 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r8 = r8 - r9;
                    goto L_0x0611;
                L_0x05d8:
                    r8 = r1.isChat;
                    if (r8 == 0) goto L_0x05fe;
                L_0x05dc:
                    r8 = r69.needDrawAvatar();
                    if (r8 == 0) goto L_0x05fe;
                L_0x05e2:
                    r8 = r1.currentMessageObject;
                    r8 = r8.isOutOwner();
                    if (r8 != 0) goto L_0x05fe;
                L_0x05ea:
                    r8 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r8 = r8.x;
                    r9 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r9 = r9.y;
                    r8 = java.lang.Math.min(r8, r9);
                    r9 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r8 = r8 - r9;
                    goto L_0x0611;
                L_0x05fe:
                    r8 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r8 = r8.x;
                    r9 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r9 = r9.y;
                    r8 = java.lang.Math.min(r8, r9);
                    r9 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r8 = r8 - r9;
                L_0x0611:
                    r9 = r1.drawShareButton;
                    if (r9 == 0) goto L_0x061c;
                L_0x0615:
                    r9 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r8 = r8 - r9;
                L_0x061c:
                    r9 = r1.hasLinkPreview;
                    if (r9 == 0) goto L_0x06cb;
                L_0x0620:
                    r9 = r2.messageOwner;
                    r9 = r9.media;
                    r9 = r9.webpage;
                    r9 = (org.telegram.tgnet.TLRPC.TL_webPage) r9;
                    r10 = r9.site_name;
                    r12 = r9.title;
                    r13 = r9.author;
                    r14 = r9.description;
                    r15 = r9.photo;
                    r32 = r8;
                    r8 = r9.document;
                    r33 = r12;
                    r12 = r9.type;
                    r9 = r9.duration;
                    if (r10 == 0) goto L_0x065f;
                L_0x063e:
                    if (r15 == 0) goto L_0x065f;
                L_0x0640:
                    r34 = r9;
                    r9 = r10.toLowerCase();
                    r35 = r10;
                    r10 = "instagram";
                    r9 = r9.equals(r10);
                    if (r9 == 0) goto L_0x0663;
                L_0x0650:
                    r9 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r9 = r9.y;
                    r10 = 3;
                    r9 = r9 / r10;
                    r10 = r1.currentMessageObject;
                    r10 = r10.textWidth;
                    r9 = java.lang.Math.max(r9, r10);
                    goto L_0x0665;
                L_0x065f:
                    r34 = r9;
                    r35 = r10;
                L_0x0663:
                    r9 = r32;
                L_0x0665:
                    if (r5 != 0) goto L_0x0689;
                L_0x0667:
                    r10 = r1.drawInstantView;
                    if (r10 != 0) goto L_0x0689;
                L_0x066b:
                    if (r8 != 0) goto L_0x0689;
                L_0x066d:
                    if (r12 == 0) goto L_0x0689;
                L_0x066f:
                    r10 = "app";
                    r10 = r12.equals(r10);
                    if (r10 != 0) goto L_0x0687;
                L_0x0677:
                    r10 = "profile";
                    r10 = r12.equals(r10);
                    if (r10 != 0) goto L_0x0687;
                L_0x067f:
                    r10 = "article";
                    r10 = r12.equals(r10);
                    if (r10 == 0) goto L_0x0689;
                L_0x0687:
                    r10 = 1;
                    goto L_0x068a;
                L_0x0689:
                    r10 = 0;
                L_0x068a:
                    if (r5 != 0) goto L_0x06b6;
                L_0x068c:
                    r5 = r1.drawInstantView;
                    if (r5 != 0) goto L_0x06b6;
                L_0x0690:
                    if (r8 != 0) goto L_0x06b6;
                L_0x0692:
                    if (r14 == 0) goto L_0x06b6;
                L_0x0694:
                    if (r12 == 0) goto L_0x06b6;
                L_0x0696:
                    r5 = "app";
                    r5 = r12.equals(r5);
                    if (r5 != 0) goto L_0x06ae;
                L_0x069e:
                    r5 = "profile";
                    r5 = r12.equals(r5);
                    if (r5 != 0) goto L_0x06ae;
                L_0x06a6:
                    r5 = "article";
                    r5 = r12.equals(r5);
                    if (r5 == 0) goto L_0x06b6;
                L_0x06ae:
                    r5 = r1.currentMessageObject;
                    r5 = r5.photoThumbs;
                    if (r5 == 0) goto L_0x06b6;
                L_0x06b4:
                    r5 = 1;
                    goto L_0x06b7;
                L_0x06b6:
                    r5 = 0;
                L_0x06b7:
                    r1.isSmallImage = r5;
                    r44 = r6;
                    r32 = r9;
                    r43 = r11;
                    r11 = r12;
                    r9 = r15;
                    r5 = r33;
                    r16 = r34;
                    r15 = 0;
                    r12 = r8;
                    r8 = r10;
                    r10 = r35;
                    goto L_0x0727;
                L_0x06cb:
                    r32 = r8;
                    r5 = r1.hasInvoicePreview;
                    if (r5 == 0) goto L_0x06fd;
                L_0x06d1:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r5;
                    r8 = r2.messageOwner;
                    r8 = r8.media;
                    r10 = r8.title;
                    r8 = r5.photo;
                    r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
                    if (r8 == 0) goto L_0x06e8;
                L_0x06e3:
                    r5 = r5.photo;
                    r5 = (org.telegram.tgnet.TLRPC.TL_webDocument) r5;
                    goto L_0x06e9;
                L_0x06e8:
                    r5 = 0;
                L_0x06e9:
                    r12 = "invoice";
                    r8 = 0;
                    r1.isSmallImage = r8;
                    r15 = r5;
                    r44 = r6;
                    r43 = r11;
                    r11 = r12;
                    r5 = 0;
                    r8 = 0;
                    r9 = 0;
                    r12 = 0;
                    r13 = 0;
                    r14 = 0;
                    r16 = 0;
                    goto L_0x0727;
                L_0x06fd:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.game;
                    r10 = r5.title;
                    r8 = r2.messageText;
                    r8 = android.text.TextUtils.isEmpty(r8);
                    if (r8 == 0) goto L_0x0710;
                L_0x070d:
                    r8 = r5.description;
                    goto L_0x0711;
                L_0x0710:
                    r8 = 0;
                L_0x0711:
                    r9 = r5.photo;
                    r5 = r5.document;
                    r12 = "game";
                    r13 = 0;
                    r1.isSmallImage = r13;
                    r44 = r6;
                    r14 = r8;
                    r43 = r11;
                    r11 = r12;
                    r8 = 0;
                    r13 = 0;
                    r15 = 0;
                    r16 = 0;
                    r12 = r5;
                    r5 = 0;
                L_0x0727:
                    r6 = r1.hasInvoicePreview;
                    if (r6 == 0) goto L_0x072f;
                L_0x072b:
                    r45 = r15;
                    r6 = 0;
                    goto L_0x0739;
                L_0x072f:
                    r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r23 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r45 = r15;
                    r6 = r23;
                L_0x0739:
                    r15 = r32 - r6;
                    r46 = r11;
                    r11 = r1.currentMessageObject;
                    r11 = r11.photoThumbs;
                    if (r11 != 0) goto L_0x074e;
                L_0x0743:
                    if (r9 == 0) goto L_0x074e;
                L_0x0745:
                    r11 = r1.currentMessageObject;
                    r47 = r9;
                    r9 = 1;
                    r11.generateThumbs(r9);
                    goto L_0x0750;
                L_0x074e:
                    r47 = r9;
                L_0x0750:
                    if (r10 == 0) goto L_0x07d2;
                L_0x0752:
                    r9 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x07c6 }
                    r9 = r9.measureText(r10);	 Catch:{ Exception -> 0x07c6 }
                    r11 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r9 = r9 + r11;
                    r48 = r3;
                    r49 = r4;
                    r3 = (double) r9;
                    r3 = java.lang.Math.ceil(r3);	 Catch:{ Exception -> 0x07c4 }
                    r3 = (int) r3;	 Catch:{ Exception -> 0x07c4 }
                    r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x07c4 }
                    r25 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x07c4 }
                    r26 = java.lang.Math.min(r3, r15);	 Catch:{ Exception -> 0x07c4 }
                    r27 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x07c4 }
                    r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x07c4 }
                    r29 = 0;	 Catch:{ Exception -> 0x07c4 }
                    r30 = 0;	 Catch:{ Exception -> 0x07c4 }
                    r23 = r4;	 Catch:{ Exception -> 0x07c4 }
                    r24 = r10;	 Catch:{ Exception -> 0x07c4 }
                    r23.<init>(r24, r25, r26, r27, r28, r29, r30);	 Catch:{ Exception -> 0x07c4 }
                    r1.siteNameLayout = r4;	 Catch:{ Exception -> 0x07c4 }
                    r3 = r1.siteNameLayout;	 Catch:{ Exception -> 0x07c4 }
                    r4 = 0;	 Catch:{ Exception -> 0x07c4 }
                    r3 = r3.getLineLeft(r4);	 Catch:{ Exception -> 0x07c4 }
                    r4 = 0;	 Catch:{ Exception -> 0x07c4 }
                    r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));	 Catch:{ Exception -> 0x07c4 }
                    if (r3 == 0) goto L_0x078c;	 Catch:{ Exception -> 0x07c4 }
                L_0x078a:
                    r3 = 1;	 Catch:{ Exception -> 0x07c4 }
                    goto L_0x078d;	 Catch:{ Exception -> 0x07c4 }
                L_0x078c:
                    r3 = 0;	 Catch:{ Exception -> 0x07c4 }
                L_0x078d:
                    r1.siteNameRtl = r3;	 Catch:{ Exception -> 0x07c4 }
                    r3 = r1.siteNameLayout;	 Catch:{ Exception -> 0x07c4 }
                    r4 = r1.siteNameLayout;	 Catch:{ Exception -> 0x07c4 }
                    r4 = r4.getLineCount();	 Catch:{ Exception -> 0x07c4 }
                    r9 = 1;	 Catch:{ Exception -> 0x07c4 }
                    r4 = r4 - r9;	 Catch:{ Exception -> 0x07c4 }
                    r3 = r3.getLineBottom(r4);	 Catch:{ Exception -> 0x07c4 }
                    r4 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x07c4 }
                    r4 = r4 + r3;	 Catch:{ Exception -> 0x07c4 }
                    r1.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x07c4 }
                    r4 = r1.totalHeight;	 Catch:{ Exception -> 0x07c4 }
                    r4 = r4 + r3;	 Catch:{ Exception -> 0x07c4 }
                    r1.totalHeight = r4;	 Catch:{ Exception -> 0x07c4 }
                    r4 = 0;
                    r9 = r4 + r3;
                    r3 = r1.siteNameLayout;	 Catch:{ Exception -> 0x07c1 }
                    r3 = r3.getWidth();	 Catch:{ Exception -> 0x07c1 }
                    r1.siteNameWidth = r3;	 Catch:{ Exception -> 0x07c1 }
                    r3 = r3 + r6;	 Catch:{ Exception -> 0x07c1 }
                    r11 = java.lang.Math.max(r7, r3);	 Catch:{ Exception -> 0x07c1 }
                    r3 = java.lang.Math.max(r4, r3);	 Catch:{ Exception -> 0x07bd }
                    r7 = r11;
                    goto L_0x07d8;
                L_0x07bd:
                    r0 = move-exception;
                    r3 = r0;
                    r7 = r11;
                    goto L_0x07cd;
                L_0x07c1:
                    r0 = move-exception;
                    r3 = r0;
                    goto L_0x07cd;
                L_0x07c4:
                    r0 = move-exception;
                    goto L_0x07cb;
                L_0x07c6:
                    r0 = move-exception;
                    r48 = r3;
                    r49 = r4;
                L_0x07cb:
                    r3 = r0;
                    r9 = 0;
                L_0x07cd:
                    org.telegram.messenger.FileLog.m3e(r3);
                    r3 = 0;
                    goto L_0x07d8;
                L_0x07d2:
                    r48 = r3;
                    r49 = r4;
                    r3 = 0;
                    r9 = 0;
                L_0x07d8:
                    if (r5 == 0) goto L_0x094a;
                L_0x07da:
                    r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
                    r1.titleX = r4;	 Catch:{ Exception -> 0x0934 }
                    r4 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0934 }
                    if (r4 == 0) goto L_0x080b;
                L_0x07e3:
                    r4 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x07fa }
                    r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x07fa }
                    r20 = org.telegram.messenger.AndroidUtilities.dp(r11);	 Catch:{ Exception -> 0x07fa }
                    r4 = r4 + r20;	 Catch:{ Exception -> 0x07fa }
                    r1.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x07fa }
                    r4 = r1.totalHeight;	 Catch:{ Exception -> 0x07fa }
                    r23 = org.telegram.messenger.AndroidUtilities.dp(r11);	 Catch:{ Exception -> 0x07fa }
                    r4 = r4 + r23;	 Catch:{ Exception -> 0x07fa }
                    r1.totalHeight = r4;	 Catch:{ Exception -> 0x07fa }
                    goto L_0x080b;
                L_0x07fa:
                    r0 = move-exception;
                    r4 = r3;
                    r52 = r9;
                    r53 = r10;
                    r54 = r12;
                    r55 = r14;
                    r56 = r15;
                    r9 = 0;
                    r15 = 3;
                    r3 = r0;
                    goto L_0x0946;
                L_0x080b:
                    r4 = r1.isSmallImage;	 Catch:{ Exception -> 0x0934 }
                    if (r4 == 0) goto L_0x0837;
                L_0x080f:
                    if (r14 != 0) goto L_0x0812;
                L_0x0811:
                    goto L_0x0837;
                L_0x0812:
                    r24 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x07fa }
                    r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Exception -> 0x07fa }
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x07fa }
                    r26 = r15 - r4;	 Catch:{ Exception -> 0x07fa }
                    r28 = 4;	 Catch:{ Exception -> 0x07fa }
                    r27 = 3;	 Catch:{ Exception -> 0x07fa }
                    r23 = r5;	 Catch:{ Exception -> 0x07fa }
                    r25 = r15;	 Catch:{ Exception -> 0x07fa }
                    r4 = generateStaticLayout(r23, r24, r25, r26, r27, r28);	 Catch:{ Exception -> 0x07fa }
                    r1.titleLayout = r4;	 Catch:{ Exception -> 0x07fa }
                    r4 = r1.titleLayout;	 Catch:{ Exception -> 0x07fa }
                    r4 = r4.getLineCount();	 Catch:{ Exception -> 0x07fa }
                    r11 = 3;
                    r4 = 3 - r4;
                    r50 = r3;
                    r11 = 3;
                    goto L_0x085c;
                L_0x0837:
                    r34 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0934 }
                    r36 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0934 }
                    r37 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0934 }
                    r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0934 }
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0934 }
                    r4 = (float) r11;	 Catch:{ Exception -> 0x0934 }
                    r39 = 0;	 Catch:{ Exception -> 0x0934 }
                    r40 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0934 }
                    r42 = 4;	 Catch:{ Exception -> 0x0934 }
                    r33 = r5;	 Catch:{ Exception -> 0x0934 }
                    r35 = r15;	 Catch:{ Exception -> 0x0934 }
                    r38 = r4;	 Catch:{ Exception -> 0x0934 }
                    r41 = r15;	 Catch:{ Exception -> 0x0934 }
                    r4 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r33, r34, r35, r36, r37, r38, r39, r40, r41, r42);	 Catch:{ Exception -> 0x0934 }
                    r1.titleLayout = r4;	 Catch:{ Exception -> 0x0934 }
                    r50 = r3;
                    r4 = 3;
                    r11 = 0;
                L_0x085c:
                    r3 = r1.titleLayout;	 Catch:{ Exception -> 0x0920 }
                    r51 = r4;
                    r4 = r1.titleLayout;	 Catch:{ Exception -> 0x091e }
                    r4 = r4.getLineCount();	 Catch:{ Exception -> 0x091e }
                    r23 = 1;	 Catch:{ Exception -> 0x091e }
                    r4 = r4 + -1;	 Catch:{ Exception -> 0x091e }
                    r3 = r3.getLineBottom(r4);	 Catch:{ Exception -> 0x091e }
                    r4 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x091e }
                    r4 = r4 + r3;	 Catch:{ Exception -> 0x091e }
                    r1.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x091e }
                    r4 = r1.totalHeight;	 Catch:{ Exception -> 0x091e }
                    r4 = r4 + r3;	 Catch:{ Exception -> 0x091e }
                    r1.totalHeight = r4;	 Catch:{ Exception -> 0x091e }
                    r52 = r9;
                    r4 = r50;
                    r3 = 0;
                    r23 = 0;
                L_0x087f:
                    r9 = r1.titleLayout;	 Catch:{ Exception -> 0x090f }
                    r9 = r9.getLineCount();	 Catch:{ Exception -> 0x090f }
                    if (r3 >= r9) goto L_0x0902;	 Catch:{ Exception -> 0x090f }
                L_0x0887:
                    r9 = r1.titleLayout;	 Catch:{ Exception -> 0x090f }
                    r9 = r9.getLineLeft(r3);	 Catch:{ Exception -> 0x090f }
                    r9 = (int) r9;
                    if (r9 == 0) goto L_0x0895;
                L_0x0890:
                    r53 = r10;
                    r23 = 1;
                    goto L_0x0897;
                L_0x0895:
                    r53 = r10;
                L_0x0897:
                    r10 = r1.titleX;	 Catch:{ Exception -> 0x0900 }
                    r54 = r12;
                    r12 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
                    if (r10 != r12) goto L_0x08ac;
                L_0x08a0:
                    r10 = -r9;
                    r1.titleX = r10;	 Catch:{ Exception -> 0x08a4 }
                    goto L_0x08b5;
                L_0x08a4:
                    r0 = move-exception;
                    r3 = r0;
                    r55 = r14;
                    r56 = r15;
                    goto L_0x0919;
                L_0x08ac:
                    r10 = r1.titleX;	 Catch:{ Exception -> 0x08fe }
                    r12 = -r9;	 Catch:{ Exception -> 0x08fe }
                    r10 = java.lang.Math.max(r10, r12);	 Catch:{ Exception -> 0x08fe }
                    r1.titleX = r10;	 Catch:{ Exception -> 0x08fe }
                L_0x08b5:
                    if (r9 == 0) goto L_0x08c3;
                L_0x08b7:
                    r10 = r1.titleLayout;	 Catch:{ Exception -> 0x08a4 }
                    r10 = r10.getWidth();	 Catch:{ Exception -> 0x08a4 }
                    r10 = r10 - r9;
                    r55 = r14;
                    r56 = r15;
                    goto L_0x08d3;
                L_0x08c3:
                    r10 = r1.titleLayout;	 Catch:{ Exception -> 0x08fe }
                    r10 = r10.getLineWidth(r3);	 Catch:{ Exception -> 0x08fe }
                    r55 = r14;
                    r56 = r15;
                    r14 = (double) r10;
                    r14 = java.lang.Math.ceil(r14);	 Catch:{ Exception -> 0x08fc }
                    r10 = (int) r14;	 Catch:{ Exception -> 0x08fc }
                L_0x08d3:
                    if (r3 < r11) goto L_0x08db;	 Catch:{ Exception -> 0x08fc }
                L_0x08d5:
                    if (r9 == 0) goto L_0x08e2;	 Catch:{ Exception -> 0x08fc }
                L_0x08d7:
                    r9 = r1.isSmallImage;	 Catch:{ Exception -> 0x08fc }
                    if (r9 == 0) goto L_0x08e2;	 Catch:{ Exception -> 0x08fc }
                L_0x08db:
                    r9 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Exception -> 0x08fc }
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Exception -> 0x08fc }
                    r10 = r10 + r9;	 Catch:{ Exception -> 0x08fc }
                L_0x08e2:
                    r10 = r10 + r6;	 Catch:{ Exception -> 0x08fc }
                    r9 = java.lang.Math.max(r7, r10);	 Catch:{ Exception -> 0x08fc }
                    r7 = java.lang.Math.max(r4, r10);	 Catch:{ Exception -> 0x08f8 }
                    r3 = r3 + 1;
                    r4 = r7;
                    r7 = r9;
                    r10 = r53;
                    r12 = r54;
                    r14 = r55;
                    r15 = r56;
                    goto L_0x087f;
                L_0x08f8:
                    r0 = move-exception;
                    r3 = r0;
                    r7 = r9;
                    goto L_0x0919;
                L_0x08fc:
                    r0 = move-exception;
                    goto L_0x0918;
                L_0x08fe:
                    r0 = move-exception;
                    goto L_0x0914;
                L_0x0900:
                    r0 = move-exception;
                    goto L_0x0912;
                L_0x0902:
                    r53 = r10;
                    r54 = r12;
                    r55 = r14;
                    r56 = r15;
                    r9 = r23;
                    r15 = r51;
                    goto L_0x095a;
                L_0x090f:
                    r0 = move-exception;
                    r53 = r10;
                L_0x0912:
                    r54 = r12;
                L_0x0914:
                    r55 = r14;
                    r56 = r15;
                L_0x0918:
                    r3 = r0;
                L_0x0919:
                    r9 = r23;
                    r15 = r51;
                    goto L_0x0946;
                L_0x091e:
                    r0 = move-exception;
                    goto L_0x0923;
                L_0x0920:
                    r0 = move-exception;
                    r51 = r4;
                L_0x0923:
                    r52 = r9;
                    r53 = r10;
                    r54 = r12;
                    r55 = r14;
                    r56 = r15;
                    r3 = r0;
                    r4 = r50;
                    r15 = r51;
                    r9 = 0;
                    goto L_0x0946;
                L_0x0934:
                    r0 = move-exception;
                    r50 = r3;
                    r52 = r9;
                    r53 = r10;
                    r54 = r12;
                    r55 = r14;
                    r56 = r15;
                    r3 = r0;
                    r4 = r50;
                    r9 = 0;
                    r15 = 3;
                L_0x0946:
                    org.telegram.messenger.FileLog.m3e(r3);
                    goto L_0x095a;
                L_0x094a:
                    r50 = r3;
                    r52 = r9;
                    r53 = r10;
                    r54 = r12;
                    r55 = r14;
                    r56 = r15;
                    r4 = r50;
                    r9 = 0;
                    r15 = 3;
                L_0x095a:
                    if (r13 == 0) goto L_0x0a0e;
                L_0x095c:
                    if (r5 != 0) goto L_0x0a0e;
                L_0x095e:
                    r3 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0a06 }
                    if (r3 == 0) goto L_0x0976;	 Catch:{ Exception -> 0x0a06 }
                L_0x0962:
                    r3 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0a06 }
                    r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0a06 }
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x0a06 }
                    r3 = r3 + r10;	 Catch:{ Exception -> 0x0a06 }
                    r1.linkPreviewHeight = r3;	 Catch:{ Exception -> 0x0a06 }
                    r3 = r1.totalHeight;	 Catch:{ Exception -> 0x0a06 }
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x0a06 }
                    r3 = r3 + r10;	 Catch:{ Exception -> 0x0a06 }
                    r1.totalHeight = r3;	 Catch:{ Exception -> 0x0a06 }
                L_0x0976:
                    r3 = 3;	 Catch:{ Exception -> 0x0a06 }
                    if (r15 != r3) goto L_0x0997;	 Catch:{ Exception -> 0x0a06 }
                L_0x0979:
                    r3 = r1.isSmallImage;	 Catch:{ Exception -> 0x0a06 }
                    if (r3 == 0) goto L_0x097f;	 Catch:{ Exception -> 0x0a06 }
                L_0x097d:
                    if (r55 != 0) goto L_0x0997;	 Catch:{ Exception -> 0x0a06 }
                L_0x097f:
                    r3 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0a06 }
                    r25 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0a06 }
                    r27 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0a06 }
                    r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0a06 }
                    r29 = 0;	 Catch:{ Exception -> 0x0a06 }
                    r30 = 0;	 Catch:{ Exception -> 0x0a06 }
                    r23 = r3;	 Catch:{ Exception -> 0x0a06 }
                    r24 = r13;	 Catch:{ Exception -> 0x0a06 }
                    r26 = r56;	 Catch:{ Exception -> 0x0a06 }
                    r23.<init>(r24, r25, r26, r27, r28, r29, r30);	 Catch:{ Exception -> 0x0a06 }
                    r1.authorLayout = r3;	 Catch:{ Exception -> 0x0a06 }
                    goto L_0x09b6;	 Catch:{ Exception -> 0x0a06 }
                L_0x0997:
                    r24 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0a06 }
                    r3 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Exception -> 0x0a06 }
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x0a06 }
                    r26 = r56 - r3;	 Catch:{ Exception -> 0x0a06 }
                    r28 = 1;	 Catch:{ Exception -> 0x0a06 }
                    r23 = r13;	 Catch:{ Exception -> 0x0a06 }
                    r25 = r56;	 Catch:{ Exception -> 0x0a06 }
                    r27 = r15;	 Catch:{ Exception -> 0x0a06 }
                    r3 = generateStaticLayout(r23, r24, r25, r26, r27, r28);	 Catch:{ Exception -> 0x0a06 }
                    r1.authorLayout = r3;	 Catch:{ Exception -> 0x0a06 }
                    r3 = r1.authorLayout;	 Catch:{ Exception -> 0x0a06 }
                    r3 = r3.getLineCount();	 Catch:{ Exception -> 0x0a06 }
                    r15 = r15 - r3;	 Catch:{ Exception -> 0x0a06 }
                L_0x09b6:
                    r3 = r1.authorLayout;	 Catch:{ Exception -> 0x0a06 }
                    r5 = r1.authorLayout;	 Catch:{ Exception -> 0x0a06 }
                    r5 = r5.getLineCount();	 Catch:{ Exception -> 0x0a06 }
                    r10 = 1;	 Catch:{ Exception -> 0x0a06 }
                    r5 = r5 - r10;	 Catch:{ Exception -> 0x0a06 }
                    r3 = r3.getLineBottom(r5);	 Catch:{ Exception -> 0x0a06 }
                    r5 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0a06 }
                    r5 = r5 + r3;	 Catch:{ Exception -> 0x0a06 }
                    r1.linkPreviewHeight = r5;	 Catch:{ Exception -> 0x0a06 }
                    r5 = r1.totalHeight;	 Catch:{ Exception -> 0x0a06 }
                    r5 = r5 + r3;	 Catch:{ Exception -> 0x0a06 }
                    r1.totalHeight = r5;	 Catch:{ Exception -> 0x0a06 }
                    r3 = r1.authorLayout;	 Catch:{ Exception -> 0x0a06 }
                    r5 = 0;	 Catch:{ Exception -> 0x0a06 }
                    r3 = r3.getLineLeft(r5);	 Catch:{ Exception -> 0x0a06 }
                    r3 = (int) r3;	 Catch:{ Exception -> 0x0a06 }
                    r5 = -r3;	 Catch:{ Exception -> 0x0a06 }
                    r1.authorX = r5;	 Catch:{ Exception -> 0x0a06 }
                    if (r3 == 0) goto L_0x09e4;	 Catch:{ Exception -> 0x0a06 }
                L_0x09db:
                    r5 = r1.authorLayout;	 Catch:{ Exception -> 0x0a06 }
                    r5 = r5.getWidth();	 Catch:{ Exception -> 0x0a06 }
                    r5 = r5 - r3;	 Catch:{ Exception -> 0x0a06 }
                    r3 = 1;	 Catch:{ Exception -> 0x0a06 }
                    goto L_0x09f2;	 Catch:{ Exception -> 0x0a06 }
                L_0x09e4:
                    r3 = r1.authorLayout;	 Catch:{ Exception -> 0x0a06 }
                    r5 = 0;	 Catch:{ Exception -> 0x0a06 }
                    r3 = r3.getLineWidth(r5);	 Catch:{ Exception -> 0x0a06 }
                    r10 = (double) r3;	 Catch:{ Exception -> 0x0a06 }
                    r10 = java.lang.Math.ceil(r10);	 Catch:{ Exception -> 0x0a06 }
                    r5 = (int) r10;
                    r3 = 0;
                L_0x09f2:
                    r5 = r5 + r6;
                    r10 = java.lang.Math.max(r7, r5);	 Catch:{ Exception -> 0x0a02 }
                    r5 = java.lang.Math.max(r4, r5);	 Catch:{ Exception -> 0x09fe }
                    r4 = r5;
                    r7 = r10;
                    goto L_0x0a0f;
                L_0x09fe:
                    r0 = move-exception;
                    r5 = r3;
                    r7 = r10;
                    goto L_0x0a04;
                L_0x0a02:
                    r0 = move-exception;
                    r5 = r3;
                L_0x0a04:
                    r3 = r0;
                    goto L_0x0a09;
                L_0x0a06:
                    r0 = move-exception;
                    r3 = r0;
                    r5 = 0;
                L_0x0a09:
                    org.telegram.messenger.FileLog.m3e(r3);
                    r3 = r5;
                    goto L_0x0a0f;
                L_0x0a0e:
                    r3 = 0;
                L_0x0a0f:
                    if (r55 == 0) goto L_0x0b2e;
                L_0x0a11:
                    r5 = 0;
                    r1.descriptionX = r5;	 Catch:{ Exception -> 0x0b29 }
                    r5 = r1.currentMessageObject;	 Catch:{ Exception -> 0x0b29 }
                    r5.generateLinkDescription();	 Catch:{ Exception -> 0x0b29 }
                    r5 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0b29 }
                    if (r5 == 0) goto L_0x0a31;	 Catch:{ Exception -> 0x0b29 }
                L_0x0a1d:
                    r5 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0b29 }
                    r10 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0b29 }
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r10);	 Catch:{ Exception -> 0x0b29 }
                    r5 = r5 + r11;	 Catch:{ Exception -> 0x0b29 }
                    r1.linkPreviewHeight = r5;	 Catch:{ Exception -> 0x0b29 }
                    r5 = r1.totalHeight;	 Catch:{ Exception -> 0x0b29 }
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r10);	 Catch:{ Exception -> 0x0b29 }
                    r5 = r5 + r11;	 Catch:{ Exception -> 0x0b29 }
                    r1.totalHeight = r5;	 Catch:{ Exception -> 0x0b29 }
                L_0x0a31:
                    r5 = 3;	 Catch:{ Exception -> 0x0b29 }
                    if (r15 != r5) goto L_0x0a5d;	 Catch:{ Exception -> 0x0b29 }
                L_0x0a34:
                    r5 = r1.isSmallImage;	 Catch:{ Exception -> 0x0b29 }
                    if (r5 != 0) goto L_0x0a5d;	 Catch:{ Exception -> 0x0b29 }
                L_0x0a38:
                    r5 = r2.linkDescription;	 Catch:{ Exception -> 0x0b29 }
                    r34 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0b29 }
                    r36 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0b29 }
                    r37 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0b29 }
                    r10 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0b29 }
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r10);	 Catch:{ Exception -> 0x0b29 }
                    r10 = (float) r11;	 Catch:{ Exception -> 0x0b29 }
                    r39 = 0;	 Catch:{ Exception -> 0x0b29 }
                    r40 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0b29 }
                    r42 = 6;	 Catch:{ Exception -> 0x0b29 }
                    r33 = r5;	 Catch:{ Exception -> 0x0b29 }
                    r35 = r56;	 Catch:{ Exception -> 0x0b29 }
                    r38 = r10;	 Catch:{ Exception -> 0x0b29 }
                    r41 = r56;	 Catch:{ Exception -> 0x0b29 }
                    r5 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r33, r34, r35, r36, r37, r38, r39, r40, r41, r42);	 Catch:{ Exception -> 0x0b29 }
                    r1.descriptionLayout = r5;	 Catch:{ Exception -> 0x0b29 }
                    r15 = 0;	 Catch:{ Exception -> 0x0b29 }
                    goto L_0x0a77;	 Catch:{ Exception -> 0x0b29 }
                L_0x0a5d:
                    r5 = r2.linkDescription;	 Catch:{ Exception -> 0x0b29 }
                    r24 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0b29 }
                    r10 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Exception -> 0x0b29 }
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);	 Catch:{ Exception -> 0x0b29 }
                    r26 = r56 - r10;	 Catch:{ Exception -> 0x0b29 }
                    r28 = 6;	 Catch:{ Exception -> 0x0b29 }
                    r23 = r5;	 Catch:{ Exception -> 0x0b29 }
                    r25 = r56;	 Catch:{ Exception -> 0x0b29 }
                    r27 = r15;	 Catch:{ Exception -> 0x0b29 }
                    r5 = generateStaticLayout(r23, r24, r25, r26, r27, r28);	 Catch:{ Exception -> 0x0b29 }
                    r1.descriptionLayout = r5;	 Catch:{ Exception -> 0x0b29 }
                L_0x0a77:
                    r5 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0b29 }
                    r10 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0b29 }
                    r10 = r10.getLineCount();	 Catch:{ Exception -> 0x0b29 }
                    r11 = 1;	 Catch:{ Exception -> 0x0b29 }
                    r10 = r10 - r11;	 Catch:{ Exception -> 0x0b29 }
                    r5 = r5.getLineBottom(r10);	 Catch:{ Exception -> 0x0b29 }
                    r10 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0b29 }
                    r10 = r10 + r5;	 Catch:{ Exception -> 0x0b29 }
                    r1.linkPreviewHeight = r10;	 Catch:{ Exception -> 0x0b29 }
                    r10 = r1.totalHeight;	 Catch:{ Exception -> 0x0b29 }
                    r10 = r10 + r5;	 Catch:{ Exception -> 0x0b29 }
                    r1.totalHeight = r10;	 Catch:{ Exception -> 0x0b29 }
                    r5 = 0;	 Catch:{ Exception -> 0x0b29 }
                    r10 = 0;	 Catch:{ Exception -> 0x0b29 }
                L_0x0a91:
                    r11 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0b29 }
                    r11 = r11.getLineCount();	 Catch:{ Exception -> 0x0b29 }
                    if (r5 >= r11) goto L_0x0abc;	 Catch:{ Exception -> 0x0b29 }
                L_0x0a99:
                    r11 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0b29 }
                    r11 = r11.getLineLeft(r5);	 Catch:{ Exception -> 0x0b29 }
                    r11 = (double) r11;	 Catch:{ Exception -> 0x0b29 }
                    r11 = java.lang.Math.ceil(r11);	 Catch:{ Exception -> 0x0b29 }
                    r11 = (int) r11;	 Catch:{ Exception -> 0x0b29 }
                    if (r11 == 0) goto L_0x0ab9;	 Catch:{ Exception -> 0x0b29 }
                L_0x0aa7:
                    r10 = r1.descriptionX;	 Catch:{ Exception -> 0x0b29 }
                    if (r10 != 0) goto L_0x0aaf;	 Catch:{ Exception -> 0x0b29 }
                L_0x0aab:
                    r10 = -r11;	 Catch:{ Exception -> 0x0b29 }
                    r1.descriptionX = r10;	 Catch:{ Exception -> 0x0b29 }
                    goto L_0x0ab8;	 Catch:{ Exception -> 0x0b29 }
                L_0x0aaf:
                    r10 = r1.descriptionX;	 Catch:{ Exception -> 0x0b29 }
                    r11 = -r11;	 Catch:{ Exception -> 0x0b29 }
                    r10 = java.lang.Math.max(r10, r11);	 Catch:{ Exception -> 0x0b29 }
                    r1.descriptionX = r10;	 Catch:{ Exception -> 0x0b29 }
                L_0x0ab8:
                    r10 = 1;	 Catch:{ Exception -> 0x0b29 }
                L_0x0ab9:
                    r5 = r5 + 1;	 Catch:{ Exception -> 0x0b29 }
                    goto L_0x0a91;	 Catch:{ Exception -> 0x0b29 }
                L_0x0abc:
                    r5 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0b29 }
                    r5 = r5.getWidth();	 Catch:{ Exception -> 0x0b29 }
                    r11 = r4;	 Catch:{ Exception -> 0x0b29 }
                    r4 = 0;	 Catch:{ Exception -> 0x0b29 }
                L_0x0ac4:
                    r12 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0b29 }
                    r12 = r12.getLineCount();	 Catch:{ Exception -> 0x0b29 }
                    if (r4 >= r12) goto L_0x0b2e;	 Catch:{ Exception -> 0x0b29 }
                L_0x0acc:
                    r12 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0b29 }
                    r12 = r12.getLineLeft(r4);	 Catch:{ Exception -> 0x0b29 }
                    r12 = (double) r12;	 Catch:{ Exception -> 0x0b29 }
                    r12 = java.lang.Math.ceil(r12);	 Catch:{ Exception -> 0x0b29 }
                    r12 = (int) r12;	 Catch:{ Exception -> 0x0b29 }
                    if (r12 != 0) goto L_0x0ae1;	 Catch:{ Exception -> 0x0b29 }
                L_0x0ada:
                    r13 = r1.descriptionX;	 Catch:{ Exception -> 0x0b29 }
                    if (r13 == 0) goto L_0x0ae1;	 Catch:{ Exception -> 0x0b29 }
                L_0x0ade:
                    r13 = 0;	 Catch:{ Exception -> 0x0b29 }
                    r1.descriptionX = r13;	 Catch:{ Exception -> 0x0b29 }
                L_0x0ae1:
                    if (r12 == 0) goto L_0x0ae6;	 Catch:{ Exception -> 0x0b29 }
                L_0x0ae3:
                    r13 = r5 - r12;	 Catch:{ Exception -> 0x0b29 }
                    goto L_0x0afa;	 Catch:{ Exception -> 0x0b29 }
                L_0x0ae6:
                    if (r10 == 0) goto L_0x0aea;	 Catch:{ Exception -> 0x0b29 }
                L_0x0ae8:
                    r13 = r5;	 Catch:{ Exception -> 0x0b29 }
                    goto L_0x0afa;	 Catch:{ Exception -> 0x0b29 }
                L_0x0aea:
                    r13 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0b29 }
                    r13 = r13.getLineWidth(r4);	 Catch:{ Exception -> 0x0b29 }
                    r13 = (double) r13;	 Catch:{ Exception -> 0x0b29 }
                    r13 = java.lang.Math.ceil(r13);	 Catch:{ Exception -> 0x0b29 }
                    r13 = (int) r13;	 Catch:{ Exception -> 0x0b29 }
                    r13 = java.lang.Math.min(r13, r5);	 Catch:{ Exception -> 0x0b29 }
                L_0x0afa:
                    if (r4 < r15) goto L_0x0b04;	 Catch:{ Exception -> 0x0b29 }
                L_0x0afc:
                    if (r15 == 0) goto L_0x0b0b;	 Catch:{ Exception -> 0x0b29 }
                L_0x0afe:
                    if (r12 == 0) goto L_0x0b0b;	 Catch:{ Exception -> 0x0b29 }
                L_0x0b00:
                    r12 = r1.isSmallImage;	 Catch:{ Exception -> 0x0b29 }
                    if (r12 == 0) goto L_0x0b0b;	 Catch:{ Exception -> 0x0b29 }
                L_0x0b04:
                    r12 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Exception -> 0x0b29 }
                    r12 = org.telegram.messenger.AndroidUtilities.dp(r12);	 Catch:{ Exception -> 0x0b29 }
                    r13 = r13 + r12;	 Catch:{ Exception -> 0x0b29 }
                L_0x0b0b:
                    r13 = r13 + r6;	 Catch:{ Exception -> 0x0b29 }
                    if (r11 >= r13) goto L_0x0b21;	 Catch:{ Exception -> 0x0b29 }
                L_0x0b0e:
                    if (r9 == 0) goto L_0x0b17;	 Catch:{ Exception -> 0x0b29 }
                L_0x0b10:
                    r12 = r1.titleX;	 Catch:{ Exception -> 0x0b29 }
                    r14 = r13 - r11;	 Catch:{ Exception -> 0x0b29 }
                    r12 = r12 + r14;	 Catch:{ Exception -> 0x0b29 }
                    r1.titleX = r12;	 Catch:{ Exception -> 0x0b29 }
                L_0x0b17:
                    if (r3 == 0) goto L_0x0b20;	 Catch:{ Exception -> 0x0b29 }
                L_0x0b19:
                    r12 = r1.authorX;	 Catch:{ Exception -> 0x0b29 }
                    r11 = r13 - r11;	 Catch:{ Exception -> 0x0b29 }
                    r12 = r12 + r11;	 Catch:{ Exception -> 0x0b29 }
                    r1.authorX = r12;	 Catch:{ Exception -> 0x0b29 }
                L_0x0b20:
                    r11 = r13;	 Catch:{ Exception -> 0x0b29 }
                L_0x0b21:
                    r12 = java.lang.Math.max(r7, r13);	 Catch:{ Exception -> 0x0b29 }
                    r4 = r4 + 1;
                    r7 = r12;
                    goto L_0x0ac4;
                L_0x0b29:
                    r0 = move-exception;
                    r3 = r0;
                    org.telegram.messenger.FileLog.m3e(r3);
                L_0x0b2e:
                    if (r8 == 0) goto L_0x0b45;
                L_0x0b30:
                    r3 = r1.descriptionLayout;
                    if (r3 == 0) goto L_0x0b41;
                L_0x0b34:
                    r3 = r1.descriptionLayout;
                    if (r3 == 0) goto L_0x0b45;
                L_0x0b38:
                    r3 = r1.descriptionLayout;
                    r3 = r3.getLineCount();
                    r4 = 1;
                    if (r3 != r4) goto L_0x0b45;
                L_0x0b41:
                    r3 = 0;
                    r1.isSmallImage = r3;
                    r8 = 0;
                L_0x0b45:
                    if (r8 == 0) goto L_0x0b4f;
                L_0x0b47:
                    r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
                    r15 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r56 = r15;
                L_0x0b4f:
                    if (r54 == 0) goto L_0x0e98;
                L_0x0b51:
                    r5 = r54;
                    r3 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r5);
                    if (r3 == 0) goto L_0x0b6c;
                L_0x0b59:
                    r3 = r5.thumb;
                    r1.currentPhotoObject = r3;
                    r1.documentAttach = r5;
                    r3 = 7;
                    r1.documentAttachType = r3;
                L_0x0b62:
                    r9 = r45;
                    r12 = r46;
                    r3 = r48;
                    r4 = r49;
                    goto L_0x0ef9;
                L_0x0b6c:
                    r3 = org.telegram.messenger.MessageObject.isGifDocument(r5);
                    if (r3 == 0) goto L_0x0be9;
                L_0x0b72:
                    r3 = org.telegram.messenger.SharedConfig.autoplayGifs;
                    if (r3 != 0) goto L_0x0b7b;
                L_0x0b76:
                    r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r2.gifState = r3;
                    goto L_0x0b7d;
                L_0x0b7b:
                    r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                L_0x0b7d:
                    r4 = r1.photoImage;
                    r9 = r2.gifState;
                    r9 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1));
                    if (r9 == 0) goto L_0x0b87;
                L_0x0b85:
                    r3 = 1;
                    goto L_0x0b88;
                L_0x0b87:
                    r3 = 0;
                L_0x0b88:
                    r4.setAllowStartAnimation(r3);
                    r3 = r5.thumb;
                    r1.currentPhotoObject = r3;
                    r3 = r1.currentPhotoObject;
                    if (r3 == 0) goto L_0x0be2;
                L_0x0b93:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f43w;
                    if (r3 == 0) goto L_0x0b9f;
                L_0x0b99:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f42h;
                    if (r3 != 0) goto L_0x0be2;
                L_0x0b9f:
                    r3 = 0;
                L_0x0ba0:
                    r4 = r5.attributes;
                    r4 = r4.size();
                    if (r3 >= r4) goto L_0x0bc8;
                L_0x0ba8:
                    r4 = r5.attributes;
                    r4 = r4.get(r3);
                    r4 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r4;
                    r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
                    if (r9 != 0) goto L_0x0bbc;
                L_0x0bb4:
                    r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
                    if (r9 == 0) goto L_0x0bb9;
                L_0x0bb8:
                    goto L_0x0bbc;
                L_0x0bb9:
                    r3 = r3 + 1;
                    goto L_0x0ba0;
                L_0x0bbc:
                    r3 = r1.currentPhotoObject;
                    r9 = r4.f36w;
                    r3.f43w = r9;
                    r3 = r1.currentPhotoObject;
                    r4 = r4.f35h;
                    r3.f42h = r4;
                L_0x0bc8:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f43w;
                    if (r3 == 0) goto L_0x0bd4;
                L_0x0bce:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f42h;
                    if (r3 != 0) goto L_0x0be2;
                L_0x0bd4:
                    r3 = r1.currentPhotoObject;
                    r4 = r1.currentPhotoObject;
                    r9 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r4.f42h = r9;
                    r3.f43w = r9;
                L_0x0be2:
                    r1.documentAttach = r5;
                    r3 = 2;
                    r1.documentAttachType = r3;
                    goto L_0x0b62;
                L_0x0be9:
                    r3 = org.telegram.messenger.MessageObject.isVideoDocument(r5);
                    if (r3 == 0) goto L_0x0c48;
                L_0x0bef:
                    r3 = r5.thumb;
                    r1.currentPhotoObject = r3;
                    r3 = r1.currentPhotoObject;
                    if (r3 == 0) goto L_0x0c42;
                L_0x0bf7:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f43w;
                    if (r3 == 0) goto L_0x0c03;
                L_0x0bfd:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f42h;
                    if (r3 != 0) goto L_0x0c42;
                L_0x0c03:
                    r3 = 0;
                L_0x0c04:
                    r4 = r5.attributes;
                    r4 = r4.size();
                    if (r3 >= r4) goto L_0x0c28;
                L_0x0c0c:
                    r4 = r5.attributes;
                    r4 = r4.get(r3);
                    r4 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r4;
                    r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
                    if (r9 == 0) goto L_0x0c25;
                L_0x0c18:
                    r3 = r1.currentPhotoObject;
                    r9 = r4.f36w;
                    r3.f43w = r9;
                    r3 = r1.currentPhotoObject;
                    r4 = r4.f35h;
                    r3.f42h = r4;
                    goto L_0x0c28;
                L_0x0c25:
                    r3 = r3 + 1;
                    goto L_0x0c04;
                L_0x0c28:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f43w;
                    if (r3 == 0) goto L_0x0c34;
                L_0x0c2e:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f42h;
                    if (r3 != 0) goto L_0x0c42;
                L_0x0c34:
                    r3 = r1.currentPhotoObject;
                    r4 = r1.currentPhotoObject;
                    r9 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r4.f42h = r9;
                    r3.f43w = r9;
                L_0x0c42:
                    r3 = 0;
                    r1.createDocumentLayout(r3, r2);
                    goto L_0x0b62;
                L_0x0c48:
                    r3 = org.telegram.messenger.MessageObject.isStickerDocument(r5);
                    if (r3 == 0) goto L_0x0ca8;
                L_0x0c4e:
                    r3 = r5.thumb;
                    r1.currentPhotoObject = r3;
                    r3 = r1.currentPhotoObject;
                    if (r3 == 0) goto L_0x0ca1;
                L_0x0c56:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f43w;
                    if (r3 == 0) goto L_0x0c62;
                L_0x0c5c:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f42h;
                    if (r3 != 0) goto L_0x0ca1;
                L_0x0c62:
                    r3 = 0;
                L_0x0c63:
                    r4 = r5.attributes;
                    r4 = r4.size();
                    if (r3 >= r4) goto L_0x0c87;
                L_0x0c6b:
                    r4 = r5.attributes;
                    r4 = r4.get(r3);
                    r4 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r4;
                    r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
                    if (r9 == 0) goto L_0x0c84;
                L_0x0c77:
                    r3 = r1.currentPhotoObject;
                    r9 = r4.f36w;
                    r3.f43w = r9;
                    r3 = r1.currentPhotoObject;
                    r4 = r4.f35h;
                    r3.f42h = r4;
                    goto L_0x0c87;
                L_0x0c84:
                    r3 = r3 + 1;
                    goto L_0x0c63;
                L_0x0c87:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f43w;
                    if (r3 == 0) goto L_0x0c93;
                L_0x0c8d:
                    r3 = r1.currentPhotoObject;
                    r3 = r3.f42h;
                    if (r3 != 0) goto L_0x0ca1;
                L_0x0c93:
                    r3 = r1.currentPhotoObject;
                    r4 = r1.currentPhotoObject;
                    r9 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r4.f42h = r9;
                    r3.f43w = r9;
                L_0x0ca1:
                    r1.documentAttach = r5;
                    r3 = 6;
                    r1.documentAttachType = r3;
                    goto L_0x0b62;
                L_0x0ca8:
                    r3 = r48;
                    r4 = r49;
                    r1.calcBackgroundWidth(r3, r4, r7);
                    r9 = org.telegram.messenger.MessageObject.isStickerDocument(r5);
                    if (r9 != 0) goto L_0x0e0f;
                L_0x0cb5:
                    r9 = r1.backgroundWidth;
                    r10 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r10 = r10 + r3;
                    if (r9 >= r10) goto L_0x0cc9;
                L_0x0cc0:
                    r9 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r9 = r9 + r3;
                    r1.backgroundWidth = r9;
                L_0x0cc9:
                    r9 = org.telegram.messenger.MessageObject.isVoiceDocument(r5);
                    if (r9 == 0) goto L_0x0d7a;
                L_0x0ccf:
                    r9 = r1.backgroundWidth;
                    r10 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 - r11;
                    r1.createDocumentLayout(r9, r2);
                    r9 = r1.currentMessageObject;
                    r9 = r9.textHeight;
                    r10 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r10;
                    r10 = r1.linkPreviewHeight;
                    r9 = r9 + r10;
                    r1.mediaOffsetY = r9;
                    r9 = r1.totalHeight;
                    r10 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r10;
                    r1.totalHeight = r9;
                    r9 = r1.linkPreviewHeight;
                    r10 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r10;
                    r1.linkPreviewHeight = r9;
                    r9 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r3 = r3 - r9;
                    r9 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r9 == 0) goto L_0x0d42;
                L_0x0d0e:
                    r9 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r10 = r1.isChat;
                    if (r10 == 0) goto L_0x0d25;
                L_0x0d16:
                    r10 = r69.needDrawAvatar();
                    if (r10 == 0) goto L_0x0d25;
                L_0x0d1c:
                    r10 = r69.isOutOwner();
                    if (r10 != 0) goto L_0x0d25;
                L_0x0d22:
                    r10 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
                    goto L_0x0d26;
                L_0x0d25:
                    r10 = 0;
                L_0x0d26:
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 - r10;
                    r10 = NUM; // 0x435c0000 float:220.0 double:5.58344962E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = java.lang.Math.min(r9, r10);
                    r10 = NUM; // 0x41f00000 float:30.0 double:5.465589745E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 - r10;
                    r9 = r9 + r6;
                    r7 = java.lang.Math.max(r7, r9);
                    goto L_0x0d75;
                L_0x0d42:
                    r9 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r9 = r9.x;
                    r10 = r1.isChat;
                    if (r10 == 0) goto L_0x0d59;
                L_0x0d4a:
                    r10 = r69.needDrawAvatar();
                    if (r10 == 0) goto L_0x0d59;
                L_0x0d50:
                    r10 = r69.isOutOwner();
                    if (r10 != 0) goto L_0x0d59;
                L_0x0d56:
                    r10 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
                    goto L_0x0d5a;
                L_0x0d59:
                    r10 = 0;
                L_0x0d5a:
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 - r10;
                    r10 = NUM; // 0x435c0000 float:220.0 double:5.58344962E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = java.lang.Math.min(r9, r10);
                    r10 = NUM; // 0x41f00000 float:30.0 double:5.465589745E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 - r10;
                    r9 = r9 + r6;
                    r7 = java.lang.Math.max(r7, r9);
                L_0x0d75:
                    r1.calcBackgroundWidth(r3, r4, r7);
                    goto L_0x0e0f;
                L_0x0d7a:
                    r9 = org.telegram.messenger.MessageObject.isMusicDocument(r5);
                    if (r9 == 0) goto L_0x0e15;
                L_0x0d80:
                    r9 = r1.backgroundWidth;
                    r10 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 - r11;
                    r9 = r1.createDocumentLayout(r9, r2);
                    r10 = r1.currentMessageObject;
                    r10 = r10.textHeight;
                    r11 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r10 = r10 + r11;
                    r11 = r1.linkPreviewHeight;
                    r10 = r10 + r11;
                    r1.mediaOffsetY = r10;
                    r10 = r1.totalHeight;
                    r11 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r10 = r10 + r11;
                    r1.totalHeight = r10;
                    r10 = r1.linkPreviewHeight;
                    r11 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r10 = r10 + r11;
                    r1.linkPreviewHeight = r10;
                    r10 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r11;
                    r9 = r9 + r6;
                    r10 = NUM; // 0x42bc0000 float:94.0 double:5.53164308E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r10;
                    r7 = java.lang.Math.max(r7, r9);
                    r9 = r1.songLayout;
                    if (r9 == 0) goto L_0x0de9;
                L_0x0dca:
                    r9 = r1.songLayout;
                    r9 = r9.getLineCount();
                    if (r9 <= 0) goto L_0x0de9;
                L_0x0dd2:
                    r7 = (float) r7;
                    r9 = r1.songLayout;
                    r10 = 0;
                    r9 = r9.getLineWidth(r10);
                    r10 = (float) r6;
                    r9 = r9 + r10;
                    r10 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r10 = (float) r11;
                    r9 = r9 + r10;
                    r7 = java.lang.Math.max(r7, r9);
                    r7 = (int) r7;
                L_0x0de9:
                    r9 = r1.performerLayout;
                    if (r9 == 0) goto L_0x0e0c;
                L_0x0ded:
                    r9 = r1.performerLayout;
                    r9 = r9.getLineCount();
                    if (r9 <= 0) goto L_0x0e0c;
                L_0x0df5:
                    r7 = (float) r7;
                    r9 = r1.performerLayout;
                    r10 = 0;
                    r9 = r9.getLineWidth(r10);
                    r10 = (float) r6;
                    r9 = r9 + r10;
                    r10 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r10 = (float) r10;
                    r9 = r9 + r10;
                    r7 = java.lang.Math.max(r7, r9);
                    r7 = (int) r7;
                L_0x0e0c:
                    r1.calcBackgroundWidth(r3, r4, r7);
                L_0x0e0f:
                    r9 = r45;
                    r12 = r46;
                    goto L_0x0ef9;
                L_0x0e15:
                    r9 = r1.backgroundWidth;
                    r10 = NUM; // 0x43280000 float:168.0 double:5.566612494E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 - r10;
                    r1.createDocumentLayout(r9, r2);
                    r9 = 1;
                    r1.drawImageButton = r9;
                    r9 = r1.drawPhotoImage;
                    if (r9 == 0) goto L_0x0e52;
                L_0x0e28:
                    r9 = r1.totalHeight;
                    r10 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r10;
                    r1.totalHeight = r9;
                    r9 = r1.linkPreviewHeight;
                    r10 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r11;
                    r1.linkPreviewHeight = r9;
                    r9 = r1.photoImage;
                    r11 = r1.totalHeight;
                    r12 = r1.namesOffset;
                    r11 = r11 + r12;
                    r12 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r13 = 0;
                    r9.setImageCoords(r13, r11, r12, r10);
                    goto L_0x0e0f;
                L_0x0e52:
                    r9 = r1.currentMessageObject;
                    r9 = r9.textHeight;
                    r10 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r10;
                    r10 = r1.linkPreviewHeight;
                    r9 = r9 + r10;
                    r1.mediaOffsetY = r9;
                    r9 = r1.photoImage;
                    r10 = r1.totalHeight;
                    r11 = r1.namesOffset;
                    r10 = r10 + r11;
                    r11 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r10 = r10 - r11;
                    r11 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r12 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
                    r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
                    r13 = 0;
                    r9.setImageCoords(r13, r10, r11, r12);
                    r9 = r1.totalHeight;
                    r10 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r10;
                    r1.totalHeight = r9;
                    r9 = r1.linkPreviewHeight;
                    r10 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 + r11;
                    r1.linkPreviewHeight = r9;
                    goto L_0x0e0f;
                L_0x0e98:
                    r3 = r48;
                    r4 = r49;
                    r5 = r54;
                    if (r47 == 0) goto L_0x0ede;
                L_0x0ea0:
                    if (r46 == 0) goto L_0x0eae;
                L_0x0ea2:
                    r9 = "photo";
                    r12 = r46;
                    r9 = r12.equals(r9);
                    if (r9 == 0) goto L_0x0eb0;
                L_0x0eac:
                    r9 = 1;
                    goto L_0x0eb1;
                L_0x0eae:
                    r12 = r46;
                L_0x0eb0:
                    r9 = 0;
                L_0x0eb1:
                    r1.drawImageButton = r9;
                    r9 = r2.photoThumbs;
                    r10 = r1.drawImageButton;
                    if (r10 == 0) goto L_0x0ebe;
                L_0x0eb9:
                    r10 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
                    goto L_0x0ec0;
                L_0x0ebe:
                    r10 = r56;
                L_0x0ec0:
                    r11 = r1.drawImageButton;
                    r13 = 1;
                    r11 = r11 ^ r13;
                    r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r10, r11);
                    r1.currentPhotoObject = r9;
                    r9 = r2.photoThumbs;
                    r10 = 80;
                    r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r10);
                    r1.currentPhotoObjectThumb = r9;
                    r9 = r1.currentPhotoObjectThumb;
                    r10 = r1.currentPhotoObject;
                    if (r9 != r10) goto L_0x0ef7;
                L_0x0eda:
                    r9 = 0;
                    r1.currentPhotoObjectThumb = r9;
                    goto L_0x0ef7;
                L_0x0ede:
                    r12 = r46;
                    if (r45 == 0) goto L_0x0ef7;
                L_0x0ee2:
                    r9 = r45;
                    r10 = r9.mime_type;
                    r11 = "image/";
                    r10 = r10.startsWith(r11);
                    if (r10 != 0) goto L_0x0ef1;
                L_0x0eee:
                    r9 = 0;
                    r15 = 0;
                    goto L_0x0ef3;
                L_0x0ef1:
                    r15 = r9;
                    r9 = 0;
                L_0x0ef3:
                    r1.drawImageButton = r9;
                    r9 = r15;
                    goto L_0x0ef9;
                L_0x0ef7:
                    r9 = r45;
                L_0x0ef9:
                    r10 = r1.documentAttachType;
                    r11 = 5;
                    if (r10 == r11) goto L_0x1417;
                L_0x0efe:
                    r10 = r1.documentAttachType;
                    r11 = 3;
                    if (r10 == r11) goto L_0x1417;
                L_0x0f03:
                    r10 = r1.documentAttachType;
                    r11 = 1;
                    if (r10 == r11) goto L_0x1417;
                L_0x0f08:
                    r10 = r1.currentPhotoObject;
                    if (r10 != 0) goto L_0x0f30;
                L_0x0f0c:
                    if (r9 == 0) goto L_0x0f0f;
                L_0x0f0e:
                    goto L_0x0f30;
                L_0x0f0f:
                    r5 = r1.photoImage;
                    r6 = 0;
                    r8 = r6;
                    r8 = (android.graphics.drawable.Drawable) r8;
                    r5.setImageBitmap(r8);
                    r5 = r1.linkPreviewHeight;
                    r6 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 - r6;
                    r1.linkPreviewHeight = r5;
                    r5 = r1.totalHeight;
                    r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 + r6;
                    r1.totalHeight = r5;
                    goto L_0x1305;
                L_0x0f30:
                    if (r12 == 0) goto L_0x0f56;
                L_0x0f32:
                    r10 = "photo";
                    r10 = r12.equals(r10);
                    if (r10 != 0) goto L_0x0f54;
                L_0x0f3a:
                    r10 = "document";
                    r10 = r12.equals(r10);
                    if (r10 == 0) goto L_0x0f47;
                L_0x0f42:
                    r10 = r1.documentAttachType;
                    r11 = 6;
                    if (r10 != r11) goto L_0x0f54;
                L_0x0f47:
                    r10 = "gif";
                    r10 = r12.equals(r10);
                    if (r10 != 0) goto L_0x0f54;
                L_0x0f4f:
                    r10 = r1.documentAttachType;
                    r11 = 4;
                    if (r10 != r11) goto L_0x0f56;
                L_0x0f54:
                    r10 = 1;
                    goto L_0x0f57;
                L_0x0f56:
                    r10 = 0;
                L_0x0f57:
                    r1.drawImageButton = r10;
                    r10 = r1.linkPreviewHeight;
                    if (r10 == 0) goto L_0x0f71;
                L_0x0f5d:
                    r10 = r1.linkPreviewHeight;
                    r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r13 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r10 = r10 + r13;
                    r1.linkPreviewHeight = r10;
                    r10 = r1.totalHeight;
                    r13 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r10 = r10 + r13;
                    r1.totalHeight = r10;
                L_0x0f71:
                    r10 = r1.documentAttachType;
                    r11 = 6;
                    if (r10 != r11) goto L_0x0f90;
                L_0x0f76:
                    r10 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r10 == 0) goto L_0x0f86;
                L_0x0f7c:
                    r10 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r10 = (float) r10;
                    r11 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
                    r10 = r10 * r11;
                    r10 = (int) r10;
                    goto L_0x0f9f;
                L_0x0f86:
                    r10 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r10 = r10.x;
                    r10 = (float) r10;
                    r11 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
                    r10 = r10 * r11;
                    r10 = (int) r10;
                    goto L_0x0f9f;
                L_0x0f90:
                    r10 = r1.documentAttachType;
                    r11 = 7;
                    if (r10 != r11) goto L_0x0f9d;
                L_0x0f95:
                    r56 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
                    r10 = r1.photoImage;
                    r11 = 1;
                    r10.setAllowDecodeSingleFrame(r11);
                L_0x0f9d:
                    r10 = r56;
                L_0x0f9f:
                    r11 = r1.hasInvoicePreview;
                    if (r11 == 0) goto L_0x0faa;
                L_0x0fa3:
                    r11 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    goto L_0x0fab;
                L_0x0faa:
                    r11 = 0;
                L_0x0fab:
                    r11 = r10 - r11;
                    r11 = r11 + r6;
                    r7 = java.lang.Math.max(r7, r11);
                    r6 = r1.currentPhotoObject;
                    if (r6 == 0) goto L_0x0fc4;
                L_0x0fb6:
                    r6 = r1.currentPhotoObject;
                    r11 = -1;
                    r6.size = r11;
                    r6 = r1.currentPhotoObjectThumb;
                    if (r6 == 0) goto L_0x0fc7;
                L_0x0fbf:
                    r6 = r1.currentPhotoObjectThumb;
                    r6.size = r11;
                    goto L_0x0fc7;
                L_0x0fc4:
                    r11 = -1;
                    r9.size = r11;
                L_0x0fc7:
                    if (r8 != 0) goto L_0x103b;
                L_0x0fc9:
                    r6 = r1.documentAttachType;
                    r8 = 7;
                    if (r6 != r8) goto L_0x0fd0;
                L_0x0fce:
                    goto L_0x103b;
                L_0x0fd0:
                    r6 = r1.hasGamePreview;
                    if (r6 != 0) goto L_0x1026;
                L_0x0fd4:
                    r6 = r1.hasInvoicePreview;
                    if (r6 == 0) goto L_0x0fd9;
                L_0x0fd8:
                    goto L_0x1026;
                L_0x0fd9:
                    r6 = r1.currentPhotoObject;
                    r6 = r6.f43w;
                    r8 = r1.currentPhotoObject;
                    r8 = r8.f42h;
                    r6 = (float) r6;
                    r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r13 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r10 = r10 - r13;
                    r10 = (float) r10;
                    r10 = r6 / r10;
                    r6 = r6 / r10;
                    r6 = (int) r6;
                    r8 = (float) r8;
                    r8 = r8 / r10;
                    r10 = (int) r8;
                    if (r53 == 0) goto L_0x1017;
                L_0x0ff3:
                    if (r53 == 0) goto L_0x1008;
                L_0x0ff5:
                    r8 = r53;
                    r8 = r8.toLowerCase();
                    r11 = "instagram";
                    r8 = r8.equals(r11);
                    if (r8 != 0) goto L_0x1008;
                L_0x1003:
                    r8 = r1.documentAttachType;
                    if (r8 != 0) goto L_0x1008;
                L_0x1007:
                    goto L_0x1017;
                L_0x1008:
                    r8 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r8 = r8.y;
                    r11 = 2;
                    r8 = r8 / r11;
                    if (r10 <= r8) goto L_0x103c;
                L_0x1010:
                    r8 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r8 = r8.y;
                    r10 = r8 / 2;
                    goto L_0x103c;
                L_0x1017:
                    r8 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r8 = r8.y;
                    r11 = 3;
                    r8 = r8 / r11;
                    if (r10 <= r8) goto L_0x103c;
                L_0x101f:
                    r8 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r8 = r8.y;
                    r10 = r8 / 3;
                    goto L_0x103c;
                L_0x1026:
                    r6 = 640; // 0x280 float:8.97E-43 double:3.16E-321;
                    r8 = 360; // 0x168 float:5.04E-43 double:1.78E-321;
                    r6 = (float) r6;
                    r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r13 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r10 = r10 - r13;
                    r10 = (float) r10;
                    r10 = r6 / r10;
                    r6 = r6 / r10;
                    r6 = (int) r6;
                    r8 = (float) r8;
                    r8 = r8 / r10;
                    r10 = (int) r8;
                    goto L_0x103c;
                L_0x103b:
                    r6 = r10;
                L_0x103c:
                    r8 = r1.isSmallImage;
                    if (r8 == 0) goto L_0x1075;
                L_0x1040:
                    r8 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r8);
                    r11 = r11 + r52;
                    r13 = r1.linkPreviewHeight;
                    if (r11 <= r13) goto L_0x1069;
                L_0x104c:
                    r11 = r1.totalHeight;
                    r13 = org.telegram.messenger.AndroidUtilities.dp(r8);
                    r13 = r13 + r52;
                    r14 = r1.linkPreviewHeight;
                    r13 = r13 - r14;
                    r14 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
                    r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
                    r13 = r13 + r14;
                    r11 = r11 + r13;
                    r1.totalHeight = r11;
                    r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
                    r8 = r8 + r52;
                    r1.linkPreviewHeight = r8;
                L_0x1069:
                    r8 = r1.linkPreviewHeight;
                    r11 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r8 = r8 - r11;
                    r1.linkPreviewHeight = r8;
                    goto L_0x1086;
                L_0x1075:
                    r8 = r1.totalHeight;
                    r11 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
                    r11 = r11 + r10;
                    r8 = r8 + r11;
                    r1.totalHeight = r8;
                    r8 = r1.linkPreviewHeight;
                    r8 = r8 + r10;
                    r1.linkPreviewHeight = r8;
                L_0x1086:
                    r8 = r1.photoImage;
                    r11 = 0;
                    r8.setImageCoords(r11, r11, r6, r10);
                    r8 = java.util.Locale.US;
                    r13 = "%d_%d";
                    r14 = 2;
                    r15 = new java.lang.Object[r14];
                    r17 = java.lang.Integer.valueOf(r6);
                    r15[r11] = r17;
                    r17 = java.lang.Integer.valueOf(r10);
                    r18 = 1;
                    r15[r18] = r17;
                    r8 = java.lang.String.format(r8, r13, r15);
                    r1.currentPhotoFilter = r8;
                    r8 = java.util.Locale.US;
                    r13 = "%d_%d_b";
                    r15 = new java.lang.Object[r14];
                    r14 = java.lang.Integer.valueOf(r6);
                    r15[r11] = r14;
                    r11 = java.lang.Integer.valueOf(r10);
                    r15[r18] = r11;
                    r8 = java.lang.String.format(r8, r13, r15);
                    r1.currentPhotoFilterThumb = r8;
                    if (r9 == 0) goto L_0x10e1;
                L_0x10c1:
                    r5 = r1.photoImage;
                    r34 = 0;
                    r6 = r1.currentPhotoFilter;
                    r36 = 0;
                    r37 = 0;
                    r38 = "b1";
                    r8 = r9.size;
                    r40 = 0;
                    r41 = 1;
                    r32 = r5;
                    r33 = r9;
                    r35 = r6;
                    r39 = r8;
                    r32.setImage(r33, r34, r35, r36, r37, r38, r39, r40, r41);
                L_0x10de:
                    r5 = 1;
                    goto L_0x1286;
                L_0x10e1:
                    r8 = r1.documentAttachType;
                    r9 = 6;
                    if (r8 != r9) goto L_0x1113;
                L_0x10e6:
                    r5 = r1.photoImage;
                    r6 = r1.documentAttach;
                    r34 = 0;
                    r8 = r1.currentPhotoFilter;
                    r36 = 0;
                    r9 = r1.currentPhotoObject;
                    if (r9 == 0) goto L_0x10fb;
                L_0x10f4:
                    r9 = r1.currentPhotoObject;
                    r9 = r9.location;
                    r37 = r9;
                    goto L_0x10fd;
                L_0x10fb:
                    r37 = 0;
                L_0x10fd:
                    r38 = "b1";
                    r9 = r1.documentAttach;
                    r9 = r9.size;
                    r40 = "webp";
                    r41 = 1;
                    r32 = r5;
                    r33 = r6;
                    r35 = r8;
                    r39 = r9;
                    r32.setImage(r33, r34, r35, r36, r37, r38, r39, r40, r41);
                    goto L_0x10de;
                L_0x1113:
                    r8 = r1.documentAttachType;
                    r9 = 4;
                    if (r8 != r9) goto L_0x1144;
                L_0x1118:
                    r5 = r1.photoImage;
                    r6 = 1;
                    r5.setNeedsQualityThumb(r6);
                    r5 = r1.photoImage;
                    r5.setShouldGenerateQualityThumb(r6);
                    r5 = r1.photoImage;
                    r5.setParentMessageObject(r2);
                    r5 = r1.photoImage;
                    r24 = 0;
                    r25 = 0;
                    r6 = r1.currentPhotoObject;
                    r6 = r6.location;
                    r8 = r1.currentPhotoFilter;
                    r28 = 0;
                    r29 = 0;
                    r30 = 0;
                    r23 = r5;
                    r26 = r6;
                    r27 = r8;
                    r23.setImage(r24, r25, r26, r27, r28, r29, r30);
                    goto L_0x10de;
                L_0x1144:
                    r8 = r1.documentAttachType;
                    r9 = 2;
                    if (r8 == r9) goto L_0x11ea;
                L_0x1149:
                    r8 = r1.documentAttachType;
                    r9 = 7;
                    if (r8 != r9) goto L_0x1150;
                L_0x114e:
                    goto L_0x11ea;
                L_0x1150:
                    r5 = r2.mediaExists;
                    r8 = r1.currentPhotoObject;
                    r8 = org.telegram.messenger.FileLoader.getAttachFileName(r8);
                    r9 = r1.hasGamePreview;
                    if (r9 != 0) goto L_0x11bd;
                L_0x115c:
                    if (r5 != 0) goto L_0x11bd;
                L_0x115e:
                    r5 = r1.currentAccount;
                    r5 = org.telegram.messenger.DownloadController.getInstance(r5);
                    r9 = r1.currentMessageObject;
                    r5 = r5.canDownloadMedia(r9);
                    if (r5 != 0) goto L_0x11bd;
                L_0x116c:
                    r5 = r1.currentAccount;
                    r5 = org.telegram.messenger.FileLoader.getInstance(r5);
                    r5 = r5.isLoadingFile(r8);
                    if (r5 == 0) goto L_0x1179;
                L_0x1178:
                    goto L_0x11bd;
                L_0x1179:
                    r5 = 1;
                    r1.photoNotSet = r5;
                    r5 = r1.currentPhotoObjectThumb;
                    if (r5 == 0) goto L_0x11b2;
                L_0x1180:
                    r5 = r1.photoImage;
                    r24 = 0;
                    r25 = 0;
                    r8 = r1.currentPhotoObjectThumb;
                    r8 = r8.location;
                    r9 = java.util.Locale.US;
                    r11 = "%d_%d_b";
                    r13 = 2;
                    r14 = new java.lang.Object[r13];
                    r6 = java.lang.Integer.valueOf(r6);
                    r13 = 0;
                    r14[r13] = r6;
                    r6 = java.lang.Integer.valueOf(r10);
                    r10 = 1;
                    r14[r10] = r6;
                    r27 = java.lang.String.format(r9, r11, r14);
                    r28 = 0;
                    r29 = 0;
                    r30 = 0;
                    r23 = r5;
                    r26 = r8;
                    r23.setImage(r24, r25, r26, r27, r28, r29, r30);
                    goto L_0x10de;
                L_0x11b2:
                    r5 = r1.photoImage;
                    r6 = 0;
                    r8 = r6;
                    r8 = (android.graphics.drawable.Drawable) r8;
                    r5.setImageBitmap(r8);
                    goto L_0x10de;
                L_0x11bd:
                    r5 = 0;
                    r1.photoNotSet = r5;
                    r5 = r1.photoImage;
                    r6 = r1.currentPhotoObject;
                    r6 = r6.location;
                    r8 = r1.currentPhotoFilter;
                    r9 = r1.currentPhotoObjectThumb;
                    if (r9 == 0) goto L_0x11d3;
                L_0x11cc:
                    r9 = r1.currentPhotoObjectThumb;
                    r9 = r9.location;
                    r26 = r9;
                    goto L_0x11d5;
                L_0x11d3:
                    r26 = 0;
                L_0x11d5:
                    r9 = r1.currentPhotoFilterThumb;
                    r28 = 0;
                    r29 = 0;
                    r30 = 0;
                    r23 = r5;
                    r24 = r6;
                    r25 = r8;
                    r27 = r9;
                    r23.setImage(r24, r25, r26, r27, r28, r29, r30);
                    goto L_0x10de;
                L_0x11ea:
                    r6 = org.telegram.messenger.FileLoader.getAttachFileName(r5);
                    r8 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r5);
                    if (r8 == 0) goto L_0x120a;
                L_0x11f4:
                    r8 = r1.photoImage;
                    r9 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
                    r10 = 2;
                    r9 = r9 / r10;
                    r8.setRoundRadius(r9);
                    r8 = r1.currentAccount;
                    r8 = org.telegram.messenger.DownloadController.getInstance(r8);
                    r9 = r1.currentMessageObject;
                    r9 = r8.canDownloadMedia(r9);
                    goto L_0x121e;
                L_0x120a:
                    r8 = org.telegram.messenger.MessageObject.isNewGifDocument(r5);
                    if (r8 == 0) goto L_0x121d;
                L_0x1210:
                    r8 = r1.currentAccount;
                    r8 = org.telegram.messenger.DownloadController.getInstance(r8);
                    r9 = r1.currentMessageObject;
                    r9 = r8.canDownloadMedia(r9);
                    goto L_0x121e;
                L_0x121d:
                    r9 = 0;
                L_0x121e:
                    r8 = r69.isSending();
                    if (r8 != 0) goto L_0x125f;
                L_0x1224:
                    r8 = r2.mediaExists;
                    if (r8 != 0) goto L_0x1236;
                L_0x1228:
                    r8 = r1.currentAccount;
                    r8 = org.telegram.messenger.FileLoader.getInstance(r8);
                    r6 = r8.isLoadingFile(r6);
                    if (r6 != 0) goto L_0x1236;
                L_0x1234:
                    if (r9 == 0) goto L_0x125f;
                L_0x1236:
                    r6 = 0;
                    r1.photoNotSet = r6;
                    r6 = r1.photoImage;
                    r25 = 0;
                    r8 = r1.currentPhotoObject;
                    if (r8 == 0) goto L_0x1248;
                L_0x1241:
                    r8 = r1.currentPhotoObject;
                    r8 = r8.location;
                    r26 = r8;
                    goto L_0x124a;
                L_0x1248:
                    r26 = 0;
                L_0x124a:
                    r8 = r1.currentPhotoFilterThumb;
                    r9 = r5.size;
                    r29 = 0;
                    r30 = 0;
                    r23 = r6;
                    r24 = r5;
                    r27 = r8;
                    r28 = r9;
                    r23.setImage(r24, r25, r26, r27, r28, r29, r30);
                    goto L_0x10de;
                L_0x125f:
                    r5 = 1;
                    r1.photoNotSet = r5;
                    r5 = r1.photoImage;
                    r24 = 0;
                    r25 = 0;
                    r6 = r1.currentPhotoObject;
                    if (r6 == 0) goto L_0x1273;
                L_0x126c:
                    r6 = r1.currentPhotoObject;
                    r6 = r6.location;
                    r26 = r6;
                    goto L_0x1275;
                L_0x1273:
                    r26 = 0;
                L_0x1275:
                    r6 = r1.currentPhotoFilterThumb;
                    r28 = 0;
                    r29 = 0;
                    r30 = 0;
                    r23 = r5;
                    r27 = r6;
                    r23.setImage(r24, r25, r26, r27, r28, r29, r30);
                    goto L_0x10de;
                L_0x1286:
                    r1.drawPhotoImage = r5;
                    if (r12 == 0) goto L_0x12d4;
                L_0x128a:
                    r5 = "video";
                    r5 = r12.equals(r5);
                    if (r5 == 0) goto L_0x12d4;
                L_0x1292:
                    if (r16 == 0) goto L_0x12d4;
                L_0x1294:
                    r5 = r16 / 60;
                    r6 = r5 * 60;
                    r6 = r16 - r6;
                    r8 = "%d:%02d";
                    r9 = 2;
                    r10 = new java.lang.Object[r9];
                    r5 = java.lang.Integer.valueOf(r5);
                    r9 = 0;
                    r10[r9] = r5;
                    r5 = java.lang.Integer.valueOf(r6);
                    r6 = 1;
                    r10[r6] = r5;
                    r12 = java.lang.String.format(r8, r10);
                    r5 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
                    r5 = r5.measureText(r12);
                    r5 = (double) r5;
                    r5 = java.lang.Math.ceil(r5);
                    r5 = (int) r5;
                    r1.durationWidth = r5;
                    r5 = new android.text.StaticLayout;
                    r13 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
                    r14 = r1.durationWidth;
                    r15 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r16 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r17 = 0;
                    r18 = 0;
                    r11 = r5;
                    r11.<init>(r12, r13, r14, r15, r16, r17, r18);
                    r1.videoInfoLayout = r5;
                    goto L_0x1305;
                L_0x12d4:
                    r5 = r1.hasGamePreview;
                    if (r5 == 0) goto L_0x1305;
                L_0x12d8:
                    r5 = "AttachGame";
                    r6 = NUM; // 0x7f0c00a3 float:1.8609523E38 double:1.053097479E-314;
                    r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
                    r9 = r5.toUpperCase();
                    r5 = org.telegram.ui.ActionBar.Theme.chat_gamePaint;
                    r5 = r5.measureText(r9);
                    r5 = (double) r5;
                    r5 = java.lang.Math.ceil(r5);
                    r5 = (int) r5;
                    r1.durationWidth = r5;
                    r5 = new android.text.StaticLayout;
                    r10 = org.telegram.ui.ActionBar.Theme.chat_gamePaint;
                    r11 = r1.durationWidth;
                    r12 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r13 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r14 = 0;
                    r15 = 0;
                    r8 = r5;
                    r8.<init>(r9, r10, r11, r12, r13, r14, r15);
                    r1.videoInfoLayout = r5;
                L_0x1305:
                    r5 = r1.hasInvoicePreview;
                    if (r5 == 0) goto L_0x13f3;
                L_0x1309:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.flags;
                    r5 = r5 & 4;
                    if (r5 == 0) goto L_0x1321;
                L_0x1313:
                    r5 = "PaymentReceipt";
                    r6 = NUM; // 0x7f0c04e3 float:1.861173E38 double:1.0530980165E-314;
                    r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
                    r5 = r5.toUpperCase();
                    goto L_0x1344;
                L_0x1321:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.test;
                    if (r5 == 0) goto L_0x1337;
                L_0x1329:
                    r5 = "PaymentTestInvoice";
                    r6 = NUM; // 0x7f0c04f5 float:1.8611766E38 double:1.0530980254E-314;
                    r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
                    r5 = r5.toUpperCase();
                    goto L_0x1344;
                L_0x1337:
                    r5 = "PaymentInvoice";
                    r6 = NUM; // 0x7f0c04d6 float:1.8611703E38 double:1.05309801E-314;
                    r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
                    r5 = r5.toUpperCase();
                L_0x1344:
                    r6 = org.telegram.messenger.LocaleController.getInstance();
                    r8 = r2.messageOwner;
                    r8 = r8.media;
                    r8 = r8.total_amount;
                    r10 = r2.messageOwner;
                    r10 = r10.media;
                    r10 = r10.currency;
                    r6 = r6.formatCurrencyString(r8, r10);
                    r9 = new android.text.SpannableStringBuilder;
                    r8 = new java.lang.StringBuilder;
                    r8.<init>();
                    r8.append(r6);
                    r10 = " ";
                    r8.append(r10);
                    r8.append(r5);
                    r5 = r8.toString();
                    r9.<init>(r5);
                    r5 = new org.telegram.ui.Components.TypefaceSpan;
                    r8 = "fonts/rmedium.ttf";
                    r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8);
                    r5.<init>(r8);
                    r6 = r6.length();
                    r8 = 33;
                    r10 = 0;
                    r9.setSpan(r5, r10, r6, r8);
                    r5 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
                    r6 = r9.length();
                    r5 = r5.measureText(r9, r10, r6);
                    r5 = (double) r5;
                    r5 = java.lang.Math.ceil(r5);
                    r5 = (int) r5;
                    r1.durationWidth = r5;
                    r5 = new android.text.StaticLayout;
                    r10 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
                    r6 = r1.durationWidth;
                    r8 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r8);
                    r11 = r11 + r6;
                    r12 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r13 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r14 = 0;
                    r15 = 0;
                    r8 = r5;
                    r8.<init>(r9, r10, r11, r12, r13, r14, r15);
                    r1.videoInfoLayout = r5;
                    r5 = r1.drawPhotoImage;
                    if (r5 != 0) goto L_0x13f3;
                L_0x13b5:
                    r5 = r1.totalHeight;
                    r6 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 + r6;
                    r1.totalHeight = r5;
                    r5 = r1.timeWidth;
                    r6 = 14;
                    r8 = r69.isOutOwner();
                    if (r8 == 0) goto L_0x13cd;
                L_0x13ca:
                    r9 = 20;
                    goto L_0x13ce;
                L_0x13cd:
                    r9 = 0;
                L_0x13ce:
                    r6 = r6 + r9;
                    r6 = (float) r6;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 + r6;
                    r6 = r1.durationWidth;
                    r6 = r6 + r5;
                    if (r6 <= r3) goto L_0x13ec;
                L_0x13da:
                    r5 = r1.durationWidth;
                    r7 = java.lang.Math.max(r5, r7);
                    r5 = r1.totalHeight;
                    r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 + r6;
                    r1.totalHeight = r5;
                    goto L_0x13f3;
                L_0x13ec:
                    r6 = r1.durationWidth;
                    r6 = r6 + r5;
                    r7 = java.lang.Math.max(r6, r7);
                L_0x13f3:
                    r5 = r1.hasGamePreview;
                    if (r5 == 0) goto L_0x1414;
                L_0x13f7:
                    r5 = r2.textHeight;
                    if (r5 == 0) goto L_0x1414;
                L_0x13fb:
                    r5 = r1.linkPreviewHeight;
                    r6 = r2.textHeight;
                    r8 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
                    r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
                    r6 = r6 + r8;
                    r5 = r5 + r6;
                    r1.linkPreviewHeight = r5;
                    r5 = r1.totalHeight;
                    r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 + r6;
                    r1.totalHeight = r5;
                L_0x1414:
                    r1.calcBackgroundWidth(r3, r4, r7);
                L_0x1417:
                    r3 = r1.drawInstantView;
                    if (r3 == 0) goto L_0x1900;
                L_0x141b:
                    r3 = NUM; // 0x42040000 float:33.0 double:5.47206556E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r1.instantWidth = r3;
                    r3 = r1.drawInstantViewType;
                    r4 = 1;
                    if (r3 != r4) goto L_0x1432;
                L_0x1428:
                    r3 = "OpenChannel";
                    r4 = NUM; // 0x7f0c0499 float:1.861158E38 double:1.05309798E-314;
                    r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
                    goto L_0x1459;
                L_0x1432:
                    r3 = r1.drawInstantViewType;
                    r4 = 2;
                    if (r3 != r4) goto L_0x1441;
                L_0x1437:
                    r3 = "OpenGroup";
                    r4 = NUM; // 0x7f0c049a float:1.8611581E38 double:1.0530979805E-314;
                    r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
                    goto L_0x1459;
                L_0x1441:
                    r3 = r1.drawInstantViewType;
                    r4 = 3;
                    if (r3 != r4) goto L_0x1450;
                L_0x1446:
                    r3 = "OpenMessage";
                    r4 = NUM; // 0x7f0c049d float:1.8611587E38 double:1.053097982E-314;
                    r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
                    goto L_0x1459;
                L_0x1450:
                    r3 = "InstantView";
                    r4 = NUM; // 0x7f0c032c float:1.8610839E38 double:1.0530977996E-314;
                    r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
                L_0x1459:
                    r4 = r1.backgroundWidth;
                    r5 = NUM; // 0x42960000 float:75.0 double:5.51933903E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r9 = r4 - r5;
                    r4 = new android.text.StaticLayout;
                    r5 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
                    r6 = (float) r9;
                    r7 = android.text.TextUtils.TruncateAt.END;
                    r7 = android.text.TextUtils.ellipsize(r3, r5, r6, r7);
                    r8 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
                    r10 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r11 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r12 = 0;
                    r13 = 0;
                    r6 = r4;
                    r6.<init>(r7, r8, r9, r10, r11, r12, r13);
                    r1.instantViewLayout = r4;
                    r3 = r1.backgroundWidth;
                    r4 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r1.instantWidth = r3;
                    r3 = r1.totalHeight;
                    r4 = NUM; // 0x42380000 float:46.0 double:5.488902687E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    r1.totalHeight = r3;
                    r3 = r1.instantViewLayout;
                    if (r3 == 0) goto L_0x1900;
                L_0x1496:
                    r3 = r1.instantViewLayout;
                    r3 = r3.getLineCount();
                    if (r3 <= 0) goto L_0x1900;
                L_0x149e:
                    r3 = r1.instantWidth;
                    r3 = (double) r3;
                    r5 = r1.instantViewLayout;
                    r6 = 0;
                    r5 = r5.getLineWidth(r6);
                    r5 = (double) r5;
                    r5 = java.lang.Math.ceil(r5);
                    r3 = r3 - r5;
                    r3 = (int) r3;
                    r4 = 2;
                    r3 = r3 / r4;
                    r4 = r1.drawInstantViewType;
                    if (r4 != 0) goto L_0x14bc;
                L_0x14b5:
                    r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    goto L_0x14bd;
                L_0x14bc:
                    r9 = 0;
                L_0x14bd:
                    r3 = r3 + r9;
                    r1.instantTextX = r3;
                    r3 = r1.instantViewLayout;
                    r4 = 0;
                    r3 = r3.getLineLeft(r4);
                    r3 = (int) r3;
                    r1.instantTextLeftX = r3;
                    r3 = r1.instantTextX;
                    r4 = r1.instantTextLeftX;
                    r4 = -r4;
                    r3 = r3 + r4;
                    r1.instantTextX = r3;
                    goto L_0x1900;
                L_0x14d4:
                    r44 = r6;
                    r43 = r11;
                    r3 = r2.type;
                    r4 = 16;
                    r5 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
                    r6 = NUM; // 0x42cc0000 float:102.0 double:5.536823734E-315;
                    if (r3 != r4) goto L_0x1638;
                L_0x14e2:
                    r3 = 0;
                    r1.drawName = r3;
                    r1.drawForwardedName = r3;
                    r1.drawPhotoImage = r3;
                    r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r3 == 0) goto L_0x1516;
                L_0x14ef:
                    r3 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r4 = r1.isChat;
                    if (r4 == 0) goto L_0x1504;
                L_0x14f7:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x1504;
                L_0x14fd:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x1504;
                L_0x1503:
                    goto L_0x1506;
                L_0x1504:
                    r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
                L_0x1506:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r3 = r3 - r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = java.lang.Math.min(r3, r4);
                    r1.backgroundWidth = r3;
                    goto L_0x153c;
                L_0x1516:
                    r3 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r3 = r3.x;
                    r4 = r1.isChat;
                    if (r4 == 0) goto L_0x152b;
                L_0x151e:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x152b;
                L_0x1524:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x152b;
                L_0x152a:
                    goto L_0x152d;
                L_0x152b:
                    r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
                L_0x152d:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r3 = r3 - r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = java.lang.Math.min(r3, r4);
                    r1.backgroundWidth = r3;
                L_0x153c:
                    r3 = r1.backgroundWidth;
                    r4 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r1.availableTimeWidth = r3;
                    r3 = r68.getMaxNameWidth();
                    r10 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r4;
                    if (r3 >= 0) goto L_0x155a;
                L_0x1554:
                    r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
                L_0x155a:
                    r4 = org.telegram.messenger.LocaleController.getInstance();
                    r4 = r4.formatterDay;
                    r5 = r2.messageOwner;
                    r5 = r5.date;
                    r5 = (long) r5;
                    r7 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
                    r5 = r5 * r7;
                    r4 = r4.format(r5);
                    r5 = r2.messageOwner;
                    r5 = r5.action;
                    r5 = (org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall) r5;
                    r6 = r5.reason;
                    r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
                    r7 = r69.isOutOwner();
                    if (r7 == 0) goto L_0x1592;
                L_0x157c:
                    if (r6 == 0) goto L_0x1588;
                L_0x157e:
                    r6 = "CallMessageOutgoingMissed";
                    r7 = NUM; // 0x7f0c00f9 float:1.8609697E38 double:1.0530975215E-314;
                    r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
                    goto L_0x15b7;
                L_0x1588:
                    r6 = "CallMessageOutgoing";
                    r7 = NUM; // 0x7f0c00f8 float:1.8609695E38 double:1.053097521E-314;
                    r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
                    goto L_0x15b7;
                L_0x1592:
                    if (r6 == 0) goto L_0x159e;
                L_0x1594:
                    r6 = "CallMessageIncomingMissed";
                    r7 = NUM; // 0x7f0c00f7 float:1.8609693E38 double:1.0530975205E-314;
                    r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
                    goto L_0x15b7;
                L_0x159e:
                    r6 = r5.reason;
                    r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
                    if (r6 == 0) goto L_0x15ae;
                L_0x15a4:
                    r6 = "CallMessageIncomingDeclined";
                    r7 = NUM; // 0x7f0c00f6 float:1.860969E38 double:1.05309752E-314;
                    r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
                    goto L_0x15b7;
                L_0x15ae:
                    r6 = "CallMessageIncoming";
                    r7 = NUM; // 0x7f0c00f5 float:1.8609689E38 double:1.0530975195E-314;
                    r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
                L_0x15b7:
                    r7 = r5.duration;
                    if (r7 <= 0) goto L_0x15d5;
                L_0x15bb:
                    r7 = new java.lang.StringBuilder;
                    r7.<init>();
                    r7.append(r4);
                    r4 = ", ";
                    r7.append(r4);
                    r4 = r5.duration;
                    r4 = org.telegram.messenger.LocaleController.formatCallDuration(r4);
                    r7.append(r4);
                    r4 = r7.toString();
                L_0x15d5:
                    r5 = new android.text.StaticLayout;
                    r7 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
                    r15 = (float) r3;
                    r8 = android.text.TextUtils.TruncateAt.END;
                    r8 = android.text.TextUtils.ellipsize(r6, r7, r15, r8);
                    r9 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
                    r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r10 = r3 + r7;
                    r11 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r12 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r13 = 0;
                    r14 = 0;
                    r7 = r5;
                    r7.<init>(r8, r9, r10, r11, r12, r13, r14);
                    r1.titleLayout = r5;
                    r5 = new android.text.StaticLayout;
                    r6 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
                    r7 = android.text.TextUtils.TruncateAt.END;
                    r24 = android.text.TextUtils.ellipsize(r4, r6, r15, r7);
                    r25 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
                    r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r26 = r3 + r6;
                    r27 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r29 = 0;
                    r30 = 0;
                    r23 = r5;
                    r23.<init>(r24, r25, r26, r27, r28, r29, r30);
                    r1.docTitleLayout = r5;
                    r68.setMessageObjectInternal(r69);
                    r3 = NUM; // 0x42820000 float:65.0 double:5.51286321E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r4 = r1.namesOffset;
                    r3 = r3 + r4;
                    r1.totalHeight = r3;
                    r3 = r1.drawPinnedTop;
                    if (r3 == 0) goto L_0x1900;
                L_0x162b:
                    r3 = r1.namesOffset;
                    r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r5;
                    r1.namesOffset = r3;
                    goto L_0x1900;
                L_0x1638:
                    r10 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
                    r3 = r2.type;
                    r4 = 12;
                    if (r3 != r4) goto L_0x180e;
                L_0x1640:
                    r3 = 0;
                    r1.drawName = r3;
                    r3 = 1;
                    r1.drawForwardedName = r3;
                    r1.drawPhotoImage = r3;
                    r3 = r1.photoImage;
                    r4 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3.setRoundRadius(r4);
                    r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r3 == 0) goto L_0x167e;
                L_0x1659:
                    r3 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r4 = r1.isChat;
                    if (r4 == 0) goto L_0x166e;
                L_0x1661:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x166e;
                L_0x1667:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x166e;
                L_0x166d:
                    r10 = r6;
                L_0x166e:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = java.lang.Math.min(r3, r4);
                    r1.backgroundWidth = r3;
                    goto L_0x16a2;
                L_0x167e:
                    r3 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r3 = r3.x;
                    r4 = r1.isChat;
                    if (r4 == 0) goto L_0x1693;
                L_0x1686:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x1693;
                L_0x168c:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x1693;
                L_0x1692:
                    r10 = r6;
                L_0x1693:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = java.lang.Math.min(r3, r4);
                    r1.backgroundWidth = r3;
                L_0x16a2:
                    r3 = r1.backgroundWidth;
                    r4 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r1.availableTimeWidth = r3;
                    r3 = r2.messageOwner;
                    r3 = r3.media;
                    r3 = r3.user_id;
                    r4 = r1.currentAccount;
                    r4 = org.telegram.messenger.MessagesController.getInstance(r4);
                    r3 = java.lang.Integer.valueOf(r3);
                    r3 = r4.getUser(r3);
                    r4 = r68.getMaxNameWidth();
                    r5 = NUM; // 0x42dc0000 float:110.0 double:5.54200439E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r4 = r4 - r5;
                    if (r4 >= 0) goto L_0x16d4;
                L_0x16ce:
                    r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                L_0x16d4:
                    if (r3 == 0) goto L_0x16e8;
                L_0x16d6:
                    r5 = r3.photo;
                    if (r5 == 0) goto L_0x16e0;
                L_0x16da:
                    r5 = r3.photo;
                    r5 = r5.photo_small;
                    r7 = r5;
                    goto L_0x16e1;
                L_0x16e0:
                    r7 = 0;
                L_0x16e1:
                    r5 = r1.contactAvatarDrawable;
                    r5.setInfo(r3);
                    r9 = r7;
                    goto L_0x16e9;
                L_0x16e8:
                    r9 = 0;
                L_0x16e9:
                    r8 = r1.photoImage;
                    r10 = "50_50";
                    if (r3 == 0) goto L_0x16f3;
                L_0x16ef:
                    r3 = r1.contactAvatarDrawable;
                L_0x16f1:
                    r11 = r3;
                    goto L_0x16fc;
                L_0x16f3:
                    r3 = org.telegram.ui.ActionBar.Theme.chat_contactDrawable;
                    r5 = r69.isOutOwner();
                    r3 = r3[r5];
                    goto L_0x16f1;
                L_0x16fc:
                    r12 = 0;
                    r13 = 0;
                    r8.setImage(r9, r10, r11, r12, r13);
                    r3 = r2.messageOwner;
                    r3 = r3.media;
                    r3 = r3.phone_number;
                    if (r3 == 0) goto L_0x1718;
                L_0x1709:
                    r5 = r3.length();
                    if (r5 == 0) goto L_0x1718;
                L_0x170f:
                    r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
                    r3 = r5.format(r3);
                    goto L_0x1721;
                L_0x1718:
                    r3 = "NumberUnknown";
                    r5 = NUM; // 0x7f0c048b float:1.861155E38 double:1.053097973E-314;
                    r3 = org.telegram.messenger.LocaleController.getString(r3, r5);
                L_0x1721:
                    r5 = r2.messageOwner;
                    r5 = r5.media;
                    r5 = r5.first_name;
                    r6 = r2.messageOwner;
                    r6 = r6.media;
                    r6 = r6.last_name;
                    r5 = org.telegram.messenger.ContactsController.formatName(r5, r6);
                    r6 = 10;
                    r7 = 32;
                    r5 = r5.replace(r6, r7);
                    r6 = r5.length();
                    if (r6 != 0) goto L_0x1740;
                L_0x173f:
                    r5 = r3;
                L_0x1740:
                    r14 = new android.text.StaticLayout;
                    r6 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
                    r15 = (float) r4;
                    r7 = android.text.TextUtils.TruncateAt.END;
                    r7 = android.text.TextUtils.ellipsize(r5, r6, r15, r7);
                    r8 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
                    r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r9 = r4 + r6;
                    r10 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r11 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r12 = 0;
                    r13 = 0;
                    r6 = r14;
                    r6.<init>(r7, r8, r9, r10, r11, r12, r13);
                    r1.titleLayout = r14;
                    r5 = new android.text.StaticLayout;
                    r6 = 10;
                    r7 = 32;
                    r3 = r3.replace(r6, r7);
                    r6 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
                    r7 = android.text.TextUtils.TruncateAt.END;
                    r24 = android.text.TextUtils.ellipsize(r3, r6, r15, r7);
                    r25 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
                    r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r26 = r4 + r6;
                    r27 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r29 = 0;
                    r30 = 0;
                    r23 = r5;
                    r23.<init>(r24, r25, r26, r27, r28, r29, r30);
                    r1.docTitleLayout = r5;
                    r68.setMessageObjectInternal(r69);
                    r3 = r1.drawForwardedName;
                    if (r3 == 0) goto L_0x17af;
                L_0x1793:
                    r3 = r69.needDrawForwarded();
                    if (r3 == 0) goto L_0x17af;
                L_0x1799:
                    r3 = r1.currentPosition;
                    if (r3 == 0) goto L_0x17a3;
                L_0x179d:
                    r3 = r1.currentPosition;
                    r3 = r3.minY;
                    if (r3 != 0) goto L_0x17af;
                L_0x17a3:
                    r3 = r1.namesOffset;
                    r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    r1.namesOffset = r3;
                    goto L_0x17c4;
                L_0x17af:
                    r3 = r1.drawNameLayout;
                    if (r3 == 0) goto L_0x17c4;
                L_0x17b3:
                    r3 = r2.messageOwner;
                    r3 = r3.reply_to_msg_id;
                    if (r3 != 0) goto L_0x17c4;
                L_0x17b9:
                    r3 = r1.namesOffset;
                    r4 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    r1.namesOffset = r3;
                L_0x17c4:
                    r3 = NUM; // 0x428c0000 float:70.0 double:5.51610112E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r4 = r1.namesOffset;
                    r3 = r3 + r4;
                    r1.totalHeight = r3;
                    r3 = r1.drawPinnedTop;
                    if (r3 == 0) goto L_0x17de;
                L_0x17d3:
                    r3 = r1.namesOffset;
                    r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r5;
                    r1.namesOffset = r3;
                L_0x17de:
                    r3 = r1.docTitleLayout;
                    r3 = r3.getLineCount();
                    if (r3 <= 0) goto L_0x1900;
                L_0x17e6:
                    r3 = r1.backgroundWidth;
                    r4 = NUM; // 0x42dc0000 float:110.0 double:5.54200439E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r4 = r1.docTitleLayout;
                    r5 = 0;
                    r4 = r4.getLineWidth(r5);
                    r4 = (double) r4;
                    r4 = java.lang.Math.ceil(r4);
                    r4 = (int) r4;
                    r3 = r3 - r4;
                    r4 = r1.timeWidth;
                    if (r3 >= r4) goto L_0x1900;
                L_0x1801:
                    r3 = r1.totalHeight;
                    r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    r1.totalHeight = r3;
                    goto L_0x1900;
                L_0x180e:
                    r3 = r2.type;
                    r4 = 2;
                    if (r3 != r4) goto L_0x1889;
                L_0x1813:
                    r3 = 1;
                    r1.drawForwardedName = r3;
                    r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r3 == 0) goto L_0x1841;
                L_0x181c:
                    r3 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r4 = r1.isChat;
                    if (r4 == 0) goto L_0x1831;
                L_0x1824:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x1831;
                L_0x182a:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x1831;
                L_0x1830:
                    r10 = r6;
                L_0x1831:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = java.lang.Math.min(r3, r4);
                    r1.backgroundWidth = r3;
                    goto L_0x1865;
                L_0x1841:
                    r3 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r3 = r3.x;
                    r4 = r1.isChat;
                    if (r4 == 0) goto L_0x1856;
                L_0x1849:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x1856;
                L_0x184f:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x1856;
                L_0x1855:
                    r10 = r6;
                L_0x1856:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = java.lang.Math.min(r3, r4);
                    r1.backgroundWidth = r3;
                L_0x1865:
                    r3 = r1.backgroundWidth;
                    r1.createDocumentLayout(r3, r2);
                    r68.setMessageObjectInternal(r69);
                    r3 = NUM; // 0x428c0000 float:70.0 double:5.51610112E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r4 = r1.namesOffset;
                    r3 = r3 + r4;
                    r1.totalHeight = r3;
                    r3 = r1.drawPinnedTop;
                    if (r3 == 0) goto L_0x1900;
                L_0x187c:
                    r3 = r1.namesOffset;
                    r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r5;
                    r1.namesOffset = r3;
                    goto L_0x1900;
                L_0x1889:
                    r3 = r2.type;
                    r4 = 14;
                    if (r3 != r4) goto L_0x1908;
                L_0x188f:
                    r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r3 == 0) goto L_0x18ba;
                L_0x1895:
                    r3 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r4 = r1.isChat;
                    if (r4 == 0) goto L_0x18aa;
                L_0x189d:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x18aa;
                L_0x18a3:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x18aa;
                L_0x18a9:
                    r10 = r6;
                L_0x18aa:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = java.lang.Math.min(r3, r4);
                    r1.backgroundWidth = r3;
                    goto L_0x18de;
                L_0x18ba:
                    r3 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r3 = r3.x;
                    r4 = r1.isChat;
                    if (r4 == 0) goto L_0x18cf;
                L_0x18c2:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x18cf;
                L_0x18c8:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x18cf;
                L_0x18ce:
                    r10 = r6;
                L_0x18cf:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = java.lang.Math.min(r3, r4);
                    r1.backgroundWidth = r3;
                L_0x18de:
                    r3 = r1.backgroundWidth;
                    r1.createDocumentLayout(r3, r2);
                    r68.setMessageObjectInternal(r69);
                    r3 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r4 = r1.namesOffset;
                    r3 = r3 + r4;
                    r1.totalHeight = r3;
                    r3 = r1.drawPinnedTop;
                    if (r3 == 0) goto L_0x1900;
                L_0x18f5:
                    r3 = r1.namesOffset;
                    r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r5;
                    r1.namesOffset = r3;
                L_0x1900:
                    r5 = 0;
                    r67 = r2;
                    r2 = r1;
                    r1 = r67;
                    goto L_0x29a1;
                L_0x1908:
                    r3 = r2.messageOwner;
                    r3 = r3.fwd_from;
                    if (r3 == 0) goto L_0x1916;
                L_0x190e:
                    r3 = r2.type;
                    r4 = 13;
                    if (r3 == r4) goto L_0x1916;
                L_0x1914:
                    r3 = 1;
                    goto L_0x1917;
                L_0x1916:
                    r3 = 0;
                L_0x1917:
                    r1.drawForwardedName = r3;
                    r3 = r2.type;
                    r4 = 9;
                    if (r3 == r4) goto L_0x1921;
                L_0x191f:
                    r3 = 1;
                    goto L_0x1922;
                L_0x1921:
                    r3 = 0;
                L_0x1922:
                    r1.mediaBackground = r3;
                    r3 = 1;
                    r1.drawImageButton = r3;
                    r1.drawPhotoImage = r3;
                    r3 = r2.gifState;
                    r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
                    if (r3 == 0) goto L_0x1944;
                L_0x1931:
                    r3 = org.telegram.messenger.SharedConfig.autoplayGifs;
                    if (r3 != 0) goto L_0x1944;
                L_0x1935:
                    r3 = r2.type;
                    r4 = 8;
                    if (r3 == r4) goto L_0x1940;
                L_0x193b:
                    r3 = r2.type;
                    r4 = 5;
                    if (r3 != r4) goto L_0x1944;
                L_0x1940:
                    r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r2.gifState = r3;
                L_0x1944:
                    r3 = r69.isRoundVideo();
                    if (r3 == 0) goto L_0x1963;
                L_0x194a:
                    r3 = r1.photoImage;
                    r4 = 1;
                    r3.setAllowDecodeSingleFrame(r4);
                    r3 = r1.photoImage;
                    r4 = org.telegram.messenger.MediaController.getInstance();
                    r4 = r4.getPlayingMessageObject();
                    if (r4 != 0) goto L_0x195e;
                L_0x195c:
                    r4 = 1;
                    goto L_0x195f;
                L_0x195e:
                    r4 = 0;
                L_0x195f:
                    r3.setAllowStartAnimation(r4);
                    goto L_0x1972;
                L_0x1963:
                    r3 = r1.photoImage;
                    r4 = r2.gifState;
                    r7 = 0;
                    r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1));
                    if (r4 != 0) goto L_0x196e;
                L_0x196c:
                    r4 = 1;
                    goto L_0x196f;
                L_0x196e:
                    r4 = 0;
                L_0x196f:
                    r3.setAllowStartAnimation(r4);
                L_0x1972:
                    r3 = r1.photoImage;
                    r4 = r69.needDrawBluredPreview();
                    r3.setForcePreview(r4);
                    r3 = r2.type;
                    r4 = 9;
                    if (r3 != r4) goto L_0x1a6c;
                L_0x1981:
                    r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r3 == 0) goto L_0x19ac;
                L_0x1987:
                    r3 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r4 = r1.isChat;
                    if (r4 == 0) goto L_0x199c;
                L_0x198f:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x199c;
                L_0x1995:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x199c;
                L_0x199b:
                    r10 = r6;
                L_0x199c:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = java.lang.Math.min(r3, r4);
                    r1.backgroundWidth = r3;
                    goto L_0x19d0;
                L_0x19ac:
                    r3 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r3 = r3.x;
                    r4 = r1.isChat;
                    if (r4 == 0) goto L_0x19c1;
                L_0x19b4:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x19c1;
                L_0x19ba:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x19c1;
                L_0x19c0:
                    r10 = r6;
                L_0x19c1:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r3 = r3 - r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = java.lang.Math.min(r3, r4);
                    r1.backgroundWidth = r3;
                L_0x19d0:
                    r3 = r68.checkNeedDrawShareButton(r69);
                    if (r3 == 0) goto L_0x19e1;
                L_0x19d6:
                    r3 = r1.backgroundWidth;
                    r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r1.backgroundWidth = r3;
                L_0x19e1:
                    r3 = r1.backgroundWidth;
                    r4 = NUM; // 0x430a0000 float:138.0 double:5.55689877E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r1.createDocumentLayout(r3, r2);
                    r4 = r2.caption;
                    r4 = android.text.TextUtils.isEmpty(r4);
                    if (r4 != 0) goto L_0x19fd;
                L_0x19f5:
                    r4 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r5;
                    goto L_0x19ff;
                L_0x19fd:
                    r4 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
                L_0x19ff:
                    r5 = r1.drawPhotoImage;
                    if (r5 == 0) goto L_0x1a0c;
                L_0x1a03:
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    goto L_0x1a28;
                L_0x1a0c:
                    r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r6 = r2.caption;
                    r6 = android.text.TextUtils.isEmpty(r6);
                    if (r6 == 0) goto L_0x1a21;
                L_0x1a1e:
                    r6 = NUM; // 0x424c0000 float:51.0 double:5.495378504E-315;
                    goto L_0x1a23;
                L_0x1a21:
                    r6 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
                L_0x1a23:
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r3 = r3 + r6;
                L_0x1a28:
                    r1.availableTimeWidth = r3;
                    r3 = r1.drawPhotoImage;
                    if (r3 != 0) goto L_0x1a63;
                L_0x1a2e:
                    r3 = r2.caption;
                    r3 = android.text.TextUtils.isEmpty(r3);
                    if (r3 == 0) goto L_0x1a63;
                L_0x1a36:
                    r3 = r1.infoLayout;
                    r3 = r3.getLineCount();
                    if (r3 <= 0) goto L_0x1a63;
                L_0x1a3e:
                    r68.measureTime(r69);
                    r3 = r1.backgroundWidth;
                    r6 = NUM; // 0x42f40000 float:122.0 double:5.54977537E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r3 = r3 - r6;
                    r6 = r1.infoLayout;
                    r7 = 0;
                    r6 = r6.getLineWidth(r7);
                    r6 = (double) r6;
                    r6 = java.lang.Math.ceil(r6);
                    r6 = (int) r6;
                    r3 = r3 - r6;
                    r6 = r1.timeWidth;
                    if (r3 >= r6) goto L_0x1a63;
                L_0x1a5c:
                    r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r4 = r4 + r3;
                L_0x1a63:
                    r3 = 0;
                    r9 = 0;
                    r67 = r2;
                    r2 = r1;
                    r1 = r67;
                    goto L_0x28e5;
                L_0x1a6c:
                    r3 = r2.type;
                    r4 = 4;
                    if (r3 != r4) goto L_0x1e1d;
                L_0x1a71:
                    r3 = r2.messageOwner;
                    r3 = r3.media;
                    r3 = r3.geo;
                    r3 = r3.lat;
                    r7 = r2.messageOwner;
                    r7 = r7.media;
                    r7 = r7.geo;
                    r7 = r7._long;
                    r9 = r2.messageOwner;
                    r9 = r9.media;
                    r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
                    if (r9 == 0) goto L_0x1c7f;
                L_0x1a89:
                    r5 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r5 == 0) goto L_0x1ab7;
                L_0x1a8f:
                    r5 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r9 = r1.isChat;
                    if (r9 == 0) goto L_0x1aa4;
                L_0x1a97:
                    r9 = r69.needDrawAvatar();
                    if (r9 == 0) goto L_0x1aa4;
                L_0x1a9d:
                    r9 = r69.isOutOwner();
                    if (r9 != 0) goto L_0x1aa4;
                L_0x1aa3:
                    r10 = r6;
                L_0x1aa4:
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r5 = r5 - r6;
                    r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = java.lang.Math.min(r5, r6);
                    r1.backgroundWidth = r5;
                    goto L_0x1ade;
                L_0x1ab7:
                    r5 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r5 = r5.x;
                    r9 = r1.isChat;
                    if (r9 == 0) goto L_0x1acc;
                L_0x1abf:
                    r9 = r69.needDrawAvatar();
                    if (r9 == 0) goto L_0x1acc;
                L_0x1ac5:
                    r9 = r69.isOutOwner();
                    if (r9 != 0) goto L_0x1acc;
                L_0x1acb:
                    r10 = r6;
                L_0x1acc:
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r5 = r5 - r6;
                    r6 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = java.lang.Math.min(r5, r6);
                    r1.backgroundWidth = r5;
                L_0x1ade:
                    r5 = r68.checkNeedDrawShareButton(r69);
                    if (r5 == 0) goto L_0x1aef;
                L_0x1ae4:
                    r5 = r1.backgroundWidth;
                    r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 - r6;
                    r1.backgroundWidth = r5;
                L_0x1aef:
                    r5 = r1.backgroundWidth;
                    r6 = NUM; // 0x42140000 float:37.0 double:5.477246216E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 - r6;
                    r1.availableTimeWidth = r5;
                    r6 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 - r6;
                    r6 = r1.backgroundWidth;
                    r9 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r6 = r6 - r9;
                    r9 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r10 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
                    r10 = (double) r10;
                    r12 = 461425665NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.14159NUM;
                    r12 = r10 / r12;
                    r14 = 460718241NUM; // 0x3ff000NUM float:0.0 double:1.0;
                    r18 = 461425665NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.14159NUM;
                    r3 = r3 * r18;
                    r18 = 464053720NUM; // 0x406680NUM float:0.0 double:180.0;
                    r3 = r3 / r18;
                    r18 = java.lang.Math.sin(r3);
                    r14 = r14 + r18;
                    r18 = 460718241NUM; // 0x3ff000NUM float:0.0 double:1.0;
                    r3 = java.lang.Math.sin(r3);
                    r18 = r18 - r3;
                    r14 = r14 / r18;
                    r3 = java.lang.Math.log(r14);
                    r3 = r3 * r12;
                    r14 = 461168601NUM; // 0x400000NUM float:0.0 double:2.0;
                    r3 = r3 / r14;
                    r3 = r10 - r3;
                    r3 = java.lang.Math.round(r3);
                    r14 = NUM; // 0x4124cccd float:10.3 double:5.399795443E-315;
                    r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
                    r14 = r14 << 6;
                    r14 = (long) r14;
                    r1 = r3 - r14;
                    r1 = (double) r1;
                    r3 = 460975305NUM; // 0x3ff921fb54442d18 float:3.37028055E12 double:1.570796NUM;
                    r14 = 461168601NUM; // 0x400000NUM float:0.0 double:2.0;
                    r1 = r1 - r10;
                    r1 = r1 / r12;
                    r1 = java.lang.Math.exp(r1);
                    r1 = java.lang.Math.atan(r1);
                    r14 = r14 * r1;
                    r3 = r3 - r14;
                    r1 = 464053720NUM; // 0x406680NUM float:0.0 double:180.0;
                    r3 = r3 * r1;
                    r1 = 461425665NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.14159NUM;
                    r3 = r3 / r1;
                    r1 = java.util.Locale.US;
                    r2 = "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=%dx%d&maptype=roadmap&scale=%d&sensor=false";
                    r10 = 5;
                    r11 = new java.lang.Object[r10];
                    r3 = java.lang.Double.valueOf(r3);
                    r4 = 0;
                    r11[r4] = r3;
                    r3 = java.lang.Double.valueOf(r7);
                    r4 = 1;
                    r11[r4] = r3;
                    r3 = (float) r6;
                    r4 = org.telegram.messenger.AndroidUtilities.density;
                    r3 = r3 / r4;
                    r3 = (int) r3;
                    r3 = java.lang.Integer.valueOf(r3);
                    r4 = 2;
                    r11[r4] = r3;
                    r3 = (float) r9;
                    r4 = org.telegram.messenger.AndroidUtilities.density;
                    r3 = r3 / r4;
                    r3 = (int) r3;
                    r3 = java.lang.Integer.valueOf(r3);
                    r4 = 3;
                    r11[r4] = r3;
                    r3 = 4;
                    r4 = org.telegram.messenger.AndroidUtilities.density;
                    r7 = (double) r4;
                    r7 = java.lang.Math.ceil(r7);
                    r4 = (int) r7;
                    r7 = 2;
                    r4 = java.lang.Math.min(r7, r4);
                    r4 = java.lang.Integer.valueOf(r4);
                    r11[r3] = r4;
                    r1 = java.lang.String.format(r1, r2, r11);
                    r2 = r68;
                    r2.currentUrl = r1;
                    r1 = r69;
                    r3 = r68.isCurrentLocationTimeExpired(r69);
                    r2.locationExpired = r3;
                    if (r3 != 0) goto L_0x1be0;
                L_0x1bc7:
                    r3 = r2.photoImage;
                    r4 = 1;
                    r3.setCrossfadeWithOldImage(r4);
                    r3 = 0;
                    r2.mediaBackground = r3;
                    r3 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r7 = r2.invalidateRunnable;
                    r10 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r7, r10);
                    r2.scheduledInvalidate = r4;
                    goto L_0x1bec;
                L_0x1be0:
                    r3 = r2.backgroundWidth;
                    r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r2.backgroundWidth = r3;
                    r3 = 0;
                L_0x1bec:
                    r4 = new android.text.StaticLayout;
                    r7 = "AttachLiveLocation";
                    r8 = NUM; // 0x7f0c00a7 float:1.860953E38 double:1.053097481E-314;
                    r24 = org.telegram.messenger.LocaleController.getString(r7, r8);
                    r25 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
                    r27 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r29 = 0;
                    r30 = 0;
                    r23 = r4;
                    r26 = r5;
                    r23.<init>(r24, r25, r26, r27, r28, r29, r30);
                    r2.docTitleLayout = r4;
                    r68.updateCurrentUserAndChat();
                    r4 = r2.currentUser;
                    if (r4 == 0) goto L_0x1c29;
                L_0x1c11:
                    r4 = r2.currentUser;
                    r4 = r4.photo;
                    if (r4 == 0) goto L_0x1c1f;
                L_0x1c17:
                    r4 = r2.currentUser;
                    r4 = r4.photo;
                    r4 = r4.photo_small;
                    r7 = r4;
                    goto L_0x1c20;
                L_0x1c1f:
                    r7 = 0;
                L_0x1c20:
                    r4 = r2.contactAvatarDrawable;
                    r8 = r2.currentUser;
                    r4.setInfo(r8);
                L_0x1c27:
                    r11 = r7;
                    goto L_0x1c45;
                L_0x1c29:
                    r4 = r2.currentChat;
                    if (r4 == 0) goto L_0x1c44;
                L_0x1c2d:
                    r4 = r2.currentChat;
                    r4 = r4.photo;
                    if (r4 == 0) goto L_0x1c3b;
                L_0x1c33:
                    r4 = r2.currentChat;
                    r4 = r4.photo;
                    r4 = r4.photo_small;
                    r7 = r4;
                    goto L_0x1c3c;
                L_0x1c3b:
                    r7 = 0;
                L_0x1c3c:
                    r4 = r2.contactAvatarDrawable;
                    r8 = r2.currentChat;
                    r4.setInfo(r8);
                    goto L_0x1c27;
                L_0x1c44:
                    r11 = 0;
                L_0x1c45:
                    r10 = r2.locationImageReceiver;
                    r12 = "50_50";
                    r13 = r2.contactAvatarDrawable;
                    r14 = 0;
                    r15 = 0;
                    r10.setImage(r11, r12, r13, r14, r15);
                    r4 = new android.text.StaticLayout;
                    r7 = r1.messageOwner;
                    r7 = r7.edit_date;
                    if (r7 == 0) goto L_0x1c5e;
                L_0x1c58:
                    r7 = r1.messageOwner;
                    r7 = r7.edit_date;
                L_0x1c5c:
                    r7 = (long) r7;
                    goto L_0x1c63;
                L_0x1c5e:
                    r7 = r1.messageOwner;
                    r7 = r7.date;
                    goto L_0x1c5c;
                L_0x1c63:
                    r24 = org.telegram.messenger.LocaleController.formatLocationUpdateDate(r7);
                    r25 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
                    r27 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r29 = 0;
                    r30 = 0;
                    r23 = r4;
                    r26 = r5;
                    r23.<init>(r24, r25, r26, r27, r28, r29, r30);
                    r2.infoLayout = r4;
                    r5 = r6;
                    r4 = r9;
                    r9 = r3;
                    goto L_0x1e04;
                L_0x1c7f:
                    r67 = r2;
                    r2 = r1;
                    r1 = r67;
                    r9 = r1.messageOwner;
                    r9 = r9.media;
                    r9 = r9.title;
                    r9 = android.text.TextUtils.isEmpty(r9);
                    if (r9 != 0) goto L_0x1da9;
                L_0x1c90:
                    r9 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r9 == 0) goto L_0x1cbb;
                L_0x1c96:
                    r9 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r11 = r2.isChat;
                    if (r11 == 0) goto L_0x1cab;
                L_0x1c9e:
                    r11 = r69.needDrawAvatar();
                    if (r11 == 0) goto L_0x1cab;
                L_0x1ca4:
                    r11 = r69.isOutOwner();
                    if (r11 != 0) goto L_0x1cab;
                L_0x1caa:
                    r10 = r6;
                L_0x1cab:
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 - r6;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r5 = java.lang.Math.min(r9, r5);
                    r2.backgroundWidth = r5;
                    goto L_0x1cdf;
                L_0x1cbb:
                    r9 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r9 = r9.x;
                    r11 = r2.isChat;
                    if (r11 == 0) goto L_0x1cd0;
                L_0x1cc3:
                    r11 = r69.needDrawAvatar();
                    if (r11 == 0) goto L_0x1cd0;
                L_0x1cc9:
                    r11 = r69.isOutOwner();
                    if (r11 != 0) goto L_0x1cd0;
                L_0x1ccf:
                    r10 = r6;
                L_0x1cd0:
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r10);
                    r9 = r9 - r6;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r5 = java.lang.Math.min(r9, r5);
                    r2.backgroundWidth = r5;
                L_0x1cdf:
                    r5 = r68.checkNeedDrawShareButton(r69);
                    if (r5 == 0) goto L_0x1cf0;
                L_0x1ce5:
                    r5 = r2.backgroundWidth;
                    r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 - r6;
                    r2.backgroundWidth = r5;
                L_0x1cf0:
                    r5 = r2.backgroundWidth;
                    r6 = NUM; // 0x42f60000 float:123.0 double:5.55042295E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r5 = r5 - r6;
                    r6 = r1.messageOwner;
                    r6 = r6.media;
                    r6 = r6.title;
                    r33 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
                    r35 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r36 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r37 = 0;
                    r38 = 0;
                    r39 = android.text.TextUtils.TruncateAt.END;
                    r41 = 2;
                    r32 = r6;
                    r34 = r5;
                    r40 = r5;
                    r6 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r32, r33, r34, r35, r36, r37, r38, r39, r40, r41);
                    r2.docTitleLayout = r6;
                    r6 = r2.docTitleLayout;
                    r6 = r6.getLineCount();
                    r9 = r1.messageOwner;
                    r9 = r9.media;
                    r9 = r9.address;
                    if (r9 == 0) goto L_0x1d59;
                L_0x1d27:
                    r9 = r1.messageOwner;
                    r9 = r9.media;
                    r9 = r9.address;
                    r9 = r9.length();
                    if (r9 <= 0) goto L_0x1d59;
                L_0x1d33:
                    r9 = r1.messageOwner;
                    r9 = r9.media;
                    r9 = r9.address;
                    r33 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
                    r35 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r36 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r37 = 0;
                    r38 = 0;
                    r39 = android.text.TextUtils.TruncateAt.END;
                    r10 = 3;
                    r15 = 3 - r6;
                    r41 = java.lang.Math.min(r10, r15);
                    r32 = r9;
                    r34 = r5;
                    r40 = r5;
                    r6 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r32, r33, r34, r35, r36, r37, r38, r39, r40, r41);
                    r2.infoLayout = r6;
                    goto L_0x1d5c;
                L_0x1d59:
                    r6 = 0;
                    r2.infoLayout = r6;
                L_0x1d5c:
                    r6 = 0;
                    r2.mediaBackground = r6;
                    r2.availableTimeWidth = r5;
                    r5 = NUM; // 0x42ac0000 float:86.0 double:5.526462427E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r10 = java.util.Locale.US;
                    r11 = "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false";
                    r12 = 5;
                    r13 = new java.lang.Object[r12];
                    r12 = java.lang.Double.valueOf(r3);
                    r13[r6] = r12;
                    r6 = java.lang.Double.valueOf(r7);
                    r12 = 1;
                    r13[r12] = r6;
                    r6 = org.telegram.messenger.AndroidUtilities.density;
                    r14 = (double) r6;
                    r14 = java.lang.Math.ceil(r14);
                    r6 = (int) r14;
                    r12 = 2;
                    r6 = java.lang.Math.min(r12, r6);
                    r6 = java.lang.Integer.valueOf(r6);
                    r13[r12] = r6;
                    r3 = java.lang.Double.valueOf(r3);
                    r4 = 3;
                    r13[r4] = r3;
                    r3 = 4;
                    r4 = java.lang.Double.valueOf(r7);
                    r13[r3] = r4;
                    r3 = java.lang.String.format(r10, r11, r13);
                    r2.currentUrl = r3;
                    r4 = r5;
                    r5 = r9;
                    goto L_0x1e03;
                L_0x1da9:
                    r5 = NUM; // 0x433a0000 float:186.0 double:5.57244073E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r2.availableTimeWidth = r5;
                    r5 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r6 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r9 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r9 = r9 + r5;
                    r2.backgroundWidth = r9;
                    r9 = java.util.Locale.US;
                    r10 = "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=200x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false";
                    r11 = 5;
                    r12 = new java.lang.Object[r11];
                    r11 = java.lang.Double.valueOf(r3);
                    r13 = 0;
                    r12[r13] = r11;
                    r11 = java.lang.Double.valueOf(r7);
                    r13 = 1;
                    r12[r13] = r11;
                    r11 = org.telegram.messenger.AndroidUtilities.density;
                    r13 = (double) r11;
                    r13 = java.lang.Math.ceil(r13);
                    r11 = (int) r13;
                    r13 = 2;
                    r11 = java.lang.Math.min(r13, r11);
                    r11 = java.lang.Integer.valueOf(r11);
                    r12[r13] = r11;
                    r3 = java.lang.Double.valueOf(r3);
                    r4 = 3;
                    r12[r4] = r3;
                    r3 = 4;
                    r4 = java.lang.Double.valueOf(r7);
                    r12[r3] = r4;
                    r3 = java.lang.String.format(r9, r10, r12);
                    r2.currentUrl = r3;
                    r4 = r6;
                L_0x1e03:
                    r9 = 0;
                L_0x1e04:
                    r3 = r2.currentUrl;
                    if (r3 == 0) goto L_0x1e1a;
                L_0x1e08:
                    r10 = r2.photoImage;
                    r11 = r2.currentUrl;
                    r12 = 0;
                    r3 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
                    r6 = r69.isOutOwner();
                    r13 = r3[r6];
                    r14 = 0;
                    r15 = 0;
                    r10.setImage(r11, r12, r13, r14, r15);
                L_0x1e1a:
                    r3 = 0;
                    goto L_0x28e5;
                L_0x1e1d:
                    r67 = r2;
                    r2 = r1;
                    r1 = r67;
                    r3 = r1.type;
                    r4 = 13;
                    if (r3 != r4) goto L_0x1f46;
                L_0x1e28:
                    r3 = 0;
                    r2.drawBackground = r3;
                    r3 = 0;
                L_0x1e2c:
                    r4 = r1.messageOwner;
                    r4 = r4.media;
                    r4 = r4.document;
                    r4 = r4.attributes;
                    r4 = r4.size();
                    if (r3 >= r4) goto L_0x1e54;
                L_0x1e3a:
                    r4 = r1.messageOwner;
                    r4 = r4.media;
                    r4 = r4.document;
                    r4 = r4.attributes;
                    r4 = r4.get(r3);
                    r4 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r4;
                    r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
                    if (r5 == 0) goto L_0x1e51;
                L_0x1e4c:
                    r9 = r4.f36w;
                    r3 = r4.f35h;
                    goto L_0x1e56;
                L_0x1e51:
                    r3 = r3 + 1;
                    goto L_0x1e2c;
                L_0x1e54:
                    r3 = 0;
                    r9 = 0;
                L_0x1e56:
                    r4 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r4 == 0) goto L_0x1e66;
                L_0x1e5c:
                    r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r4 = (float) r4;
                    r5 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
                    r4 = r4 * r5;
                    goto L_0x1e76;
                L_0x1e66:
                    r4 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r4 = r4.x;
                    r5 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r5 = r5.y;
                    r4 = java.lang.Math.min(r4, r5);
                    r4 = (float) r4;
                    r5 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
                    r4 = r4 * r5;
                L_0x1e76:
                    if (r9 != 0) goto L_0x1e81;
                L_0x1e78:
                    r3 = (int) r4;
                    r5 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r9 = r3 + r5;
                L_0x1e81:
                    r3 = (float) r3;
                    r5 = (float) r9;
                    r5 = r4 / r5;
                    r3 = r3 * r5;
                    r3 = (int) r3;
                    r5 = (int) r4;
                    r6 = (float) r3;
                    r7 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
                    if (r7 <= 0) goto L_0x1e94;
                L_0x1e8d:
                    r3 = (float) r5;
                    r4 = r4 / r6;
                    r3 = r3 * r4;
                    r3 = (int) r3;
                    r4 = r5;
                    r5 = r3;
                    goto L_0x1e95;
                L_0x1e94:
                    r4 = r3;
                L_0x1e95:
                    r3 = 6;
                    r2.documentAttachType = r3;
                    r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r3 = r5 - r3;
                    r2.availableTimeWidth = r3;
                    r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r3 = r3 + r5;
                    r2.backgroundWidth = r3;
                    r3 = r1.photoThumbs;
                    r6 = 80;
                    r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r6);
                    r2.currentPhotoObjectThumb = r3;
                    r3 = r1.attachPathExists;
                    if (r3 == 0) goto L_0x1ef6;
                L_0x1eb9:
                    r6 = r2.photoImage;
                    r7 = 0;
                    r3 = r1.messageOwner;
                    r8 = r3.attachPath;
                    r3 = java.util.Locale.US;
                    r9 = "%d_%d";
                    r10 = 2;
                    r11 = new java.lang.Object[r10];
                    r10 = java.lang.Integer.valueOf(r5);
                    r12 = 0;
                    r11[r12] = r10;
                    r10 = java.lang.Integer.valueOf(r4);
                    r12 = 1;
                    r11[r12] = r10;
                    r9 = java.lang.String.format(r3, r9, r11);
                    r10 = 0;
                    r3 = r2.currentPhotoObjectThumb;
                    if (r3 == 0) goto L_0x1ee4;
                L_0x1ede:
                    r3 = r2.currentPhotoObjectThumb;
                    r3 = r3.location;
                    r11 = r3;
                    goto L_0x1ee5;
                L_0x1ee4:
                    r11 = 0;
                L_0x1ee5:
                    r12 = "b1";
                    r3 = r1.messageOwner;
                    r3 = r3.media;
                    r3 = r3.document;
                    r13 = r3.size;
                    r14 = "webp";
                    r15 = 1;
                    r6.setImage(r7, r8, r9, r10, r11, r12, r13, r14, r15);
                    goto L_0x1f42;
                L_0x1ef6:
                    r3 = r1.messageOwner;
                    r3 = r3.media;
                    r3 = r3.document;
                    r6 = r3.id;
                    r8 = 0;
                    r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
                    if (r3 == 0) goto L_0x1f42;
                L_0x1f04:
                    r6 = r2.photoImage;
                    r3 = r1.messageOwner;
                    r3 = r3.media;
                    r7 = r3.document;
                    r8 = 0;
                    r3 = java.util.Locale.US;
                    r9 = "%d_%d";
                    r10 = 2;
                    r11 = new java.lang.Object[r10];
                    r10 = java.lang.Integer.valueOf(r5);
                    r12 = 0;
                    r11[r12] = r10;
                    r10 = java.lang.Integer.valueOf(r4);
                    r12 = 1;
                    r11[r12] = r10;
                    r9 = java.lang.String.format(r3, r9, r11);
                    r10 = 0;
                    r3 = r2.currentPhotoObjectThumb;
                    if (r3 == 0) goto L_0x1f31;
                L_0x1f2b:
                    r3 = r2.currentPhotoObjectThumb;
                    r3 = r3.location;
                    r11 = r3;
                    goto L_0x1f32;
                L_0x1f31:
                    r11 = 0;
                L_0x1f32:
                    r12 = "b1";
                    r3 = r1.messageOwner;
                    r3 = r3.media;
                    r3 = r3.document;
                    r13 = r3.size;
                    r14 = "webp";
                    r15 = 1;
                    r6.setImage(r7, r8, r9, r10, r11, r12, r13, r14, r15);
                L_0x1f42:
                    r3 = 0;
                    r9 = 0;
                    goto L_0x28e5;
                L_0x1f46:
                    r3 = r1.type;
                    r4 = 5;
                    if (r3 != r4) goto L_0x1f4e;
                L_0x1f4b:
                    r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
                    goto L_0x1f71;
                L_0x1f4e:
                    r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r3 == 0) goto L_0x1f5f;
                L_0x1f54:
                    r3 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r3 = (float) r3;
                    r4 = NUM; // 0x3f333333 float:0.7 double:5.23867711E-315;
                    r3 = r3 * r4;
                    r3 = (int) r3;
                    goto L_0x1f71;
                L_0x1f5f:
                    r3 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r3 = r3.x;
                    r4 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r4 = r4.y;
                    r3 = java.lang.Math.min(r3, r4);
                    r3 = (float) r3;
                    r4 = NUM; // 0x3f333333 float:0.7 double:5.23867711E-315;
                    r3 = r3 * r4;
                    r3 = (int) r3;
                L_0x1f71:
                    r4 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r4 = r4 + r3;
                    r5 = r1.type;
                    r6 = 5;
                    if (r5 == r6) goto L_0x1f93;
                L_0x1f7d:
                    r5 = r68.checkNeedDrawShareButton(r69);
                    if (r5 == 0) goto L_0x1f93;
                L_0x1f83:
                    r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r5 = r3 - r5;
                    r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r3 = r3 - r6;
                    goto L_0x1f94;
                L_0x1f93:
                    r5 = r3;
                L_0x1f94:
                    r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
                    if (r3 <= r6) goto L_0x1f9e;
                L_0x1f9a:
                    r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
                L_0x1f9e:
                    r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
                    if (r4 <= r6) goto L_0x1fa8;
                L_0x1fa4:
                    r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
                L_0x1fa8:
                    r9 = r4;
                    r4 = r1.type;
                    r6 = 1;
                    if (r4 != r6) goto L_0x1fbd;
                L_0x1fae:
                    r68.updateSecretTimeText(r69);
                    r4 = r1.photoThumbs;
                    r6 = 80;
                    r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
                    r2.currentPhotoObjectThumb = r4;
                    goto L_0x204e;
                L_0x1fbd:
                    r4 = r1.type;
                    r6 = 3;
                    if (r4 != r6) goto L_0x1fe2;
                L_0x1fc2:
                    r4 = 0;
                    r2.createDocumentLayout(r4, r1);
                    r68.updateSecretTimeText(r69);
                    r4 = r69.needDrawBluredPreview();
                    if (r4 != 0) goto L_0x1fdb;
                L_0x1fcf:
                    r4 = r2.photoImage;
                    r6 = 1;
                    r4.setNeedsQualityThumb(r6);
                    r4 = r2.photoImage;
                    r4.setShouldGenerateQualityThumb(r6);
                    goto L_0x1fdc;
                L_0x1fdb:
                    r6 = 1;
                L_0x1fdc:
                    r4 = r2.photoImage;
                    r4.setParentMessageObject(r1);
                    goto L_0x204e;
                L_0x1fe2:
                    r6 = 1;
                    r4 = r1.type;
                    r7 = 5;
                    if (r4 != r7) goto L_0x1ffe;
                L_0x1fe8:
                    r4 = r69.needDrawBluredPreview();
                    if (r4 != 0) goto L_0x1ff8;
                L_0x1fee:
                    r4 = r2.photoImage;
                    r4.setNeedsQualityThumb(r6);
                    r4 = r2.photoImage;
                    r4.setShouldGenerateQualityThumb(r6);
                L_0x1ff8:
                    r4 = r2.photoImage;
                    r4.setParentMessageObject(r1);
                    goto L_0x204e;
                L_0x1ffe:
                    r4 = r1.type;
                    r6 = 8;
                    if (r4 != r6) goto L_0x204e;
                L_0x2004:
                    r4 = r1.messageOwner;
                    r4 = r4.media;
                    r4 = r4.document;
                    r4 = r4.size;
                    r6 = (long) r4;
                    r4 = org.telegram.messenger.AndroidUtilities.formatFileSize(r6);
                    r6 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
                    r6 = r6.measureText(r4);
                    r6 = (double) r6;
                    r6 = java.lang.Math.ceil(r6);
                    r6 = (int) r6;
                    r2.infoWidth = r6;
                    r6 = new android.text.StaticLayout;
                    r25 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
                    r7 = r2.infoWidth;
                    r27 = android.text.Layout.Alignment.ALIGN_NORMAL;
                    r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r29 = 0;
                    r30 = 0;
                    r23 = r6;
                    r24 = r4;
                    r26 = r7;
                    r23.<init>(r24, r25, r26, r27, r28, r29, r30);
                    r2.infoLayout = r6;
                    r4 = r69.needDrawBluredPreview();
                    if (r4 != 0) goto L_0x2049;
                L_0x203e:
                    r4 = r2.photoImage;
                    r6 = 1;
                    r4.setNeedsQualityThumb(r6);
                    r4 = r2.photoImage;
                    r4.setShouldGenerateQualityThumb(r6);
                L_0x2049:
                    r4 = r2.photoImage;
                    r4.setParentMessageObject(r1);
                L_0x204e:
                    r4 = r2.currentMessagesGroup;
                    if (r4 != 0) goto L_0x2059;
                L_0x2052:
                    r4 = r1.caption;
                    if (r4 == 0) goto L_0x2059;
                L_0x2056:
                    r4 = 0;
                    r2.mediaBackground = r4;
                L_0x2059:
                    r4 = r1.photoThumbs;
                    r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
                    r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
                    r2.currentPhotoObject = r4;
                    r4 = r2.currentPhotoObject;
                    if (r4 == 0) goto L_0x2072;
                L_0x2069:
                    r4 = r2.currentPhotoObject;
                    r6 = r2.currentPhotoObjectThumb;
                    if (r4 != r6) goto L_0x2072;
                L_0x206f:
                    r4 = 0;
                    r2.currentPhotoObjectThumb = r4;
                L_0x2072:
                    r4 = r2.currentPhotoObject;
                    if (r4 == 0) goto L_0x20ce;
                L_0x2076:
                    r4 = r2.currentPhotoObject;
                    r4 = r4.f43w;
                    r4 = (float) r4;
                    r6 = (float) r3;
                    r4 = r4 / r6;
                    r7 = r2.currentPhotoObject;
                    r7 = r7.f43w;
                    r7 = (float) r7;
                    r7 = r7 / r4;
                    r7 = (int) r7;
                    r8 = r2.currentPhotoObject;
                    r8 = r8.f42h;
                    r8 = (float) r8;
                    r8 = r8 / r4;
                    r4 = (int) r8;
                    if (r7 != 0) goto L_0x2093;
                L_0x208d:
                    r7 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                L_0x2093:
                    if (r4 != 0) goto L_0x209b;
                L_0x2095:
                    r4 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                L_0x209b:
                    if (r4 <= r9) goto L_0x20a6;
                L_0x209d:
                    r4 = (float) r4;
                    r6 = (float) r9;
                    r4 = r4 / r6;
                    r6 = (float) r7;
                    r6 = r6 / r4;
                    r4 = (int) r6;
                    r7 = r4;
                    r4 = r9;
                    goto L_0x20d0;
                L_0x20a6:
                    r8 = NUM; // 0x42f00000 float:120.0 double:5.548480205E-315;
                    r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
                    if (r4 >= r8) goto L_0x20d0;
                L_0x20ae:
                    r4 = NUM; // 0x42f00000 float:120.0 double:5.548480205E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r8 = r2.currentPhotoObject;
                    r8 = r8.f42h;
                    r8 = (float) r8;
                    r10 = (float) r4;
                    r8 = r8 / r10;
                    r10 = r2.currentPhotoObject;
                    r10 = r10.f43w;
                    r10 = (float) r10;
                    r10 = r10 / r8;
                    r6 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
                    if (r6 >= 0) goto L_0x20d0;
                L_0x20c5:
                    r6 = r2.currentPhotoObject;
                    r6 = r6.f43w;
                    r6 = (float) r6;
                    r6 = r6 / r8;
                    r6 = (int) r6;
                    r7 = r6;
                    goto L_0x20d0;
                L_0x20ce:
                    r4 = 0;
                    r7 = 0;
                L_0x20d0:
                    r6 = r1.type;
                    r8 = 5;
                    if (r6 != r8) goto L_0x20d8;
                L_0x20d5:
                    r7 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
                    r4 = r7;
                L_0x20d8:
                    if (r7 == 0) goto L_0x20dc;
                L_0x20da:
                    if (r4 != 0) goto L_0x2145;
                L_0x20dc:
                    r6 = r1.type;
                    r8 = 8;
                    if (r6 != r8) goto L_0x2145;
                L_0x20e2:
                    r6 = 0;
                L_0x20e3:
                    r8 = r1.messageOwner;
                    r8 = r8.media;
                    r8 = r8.document;
                    r8 = r8.attributes;
                    r8 = r8.size();
                    if (r6 >= r8) goto L_0x2145;
                L_0x20f1:
                    r8 = r1.messageOwner;
                    r8 = r8.media;
                    r8 = r8.document;
                    r8 = r8.attributes;
                    r8 = r8.get(r6);
                    r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8;
                    r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
                    if (r10 != 0) goto L_0x210b;
                L_0x2103:
                    r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
                    if (r10 == 0) goto L_0x2108;
                L_0x2107:
                    goto L_0x210b;
                L_0x2108:
                    r6 = r6 + 1;
                    goto L_0x20e3;
                L_0x210b:
                    r4 = r8.f36w;
                    r4 = (float) r4;
                    r3 = (float) r3;
                    r4 = r4 / r3;
                    r6 = r8.f36w;
                    r6 = (float) r6;
                    r6 = r6 / r4;
                    r7 = (int) r6;
                    r6 = r8.f35h;
                    r6 = (float) r6;
                    r6 = r6 / r4;
                    r4 = (int) r6;
                    if (r4 <= r9) goto L_0x2123;
                L_0x211c:
                    r3 = (float) r4;
                    r4 = (float) r9;
                    r3 = r3 / r4;
                    r4 = (float) r7;
                    r4 = r4 / r3;
                    r7 = (int) r4;
                    goto L_0x2146;
                L_0x2123:
                    r6 = NUM; // 0x42f00000 float:120.0 double:5.548480205E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    if (r4 >= r6) goto L_0x2145;
                L_0x212b:
                    r4 = NUM; // 0x42f00000 float:120.0 double:5.548480205E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r4 = r8.f35h;
                    r4 = (float) r4;
                    r6 = (float) r9;
                    r4 = r4 / r6;
                    r6 = r8.f36w;
                    r6 = (float) r6;
                    r6 = r6 / r4;
                    r3 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1));
                    if (r3 >= 0) goto L_0x2146;
                L_0x213e:
                    r3 = r8.f36w;
                    r3 = (float) r3;
                    r3 = r3 / r4;
                    r3 = (int) r3;
                    r7 = r3;
                    goto L_0x2146;
                L_0x2145:
                    r9 = r4;
                L_0x2146:
                    if (r7 == 0) goto L_0x214a;
                L_0x2148:
                    if (r9 != 0) goto L_0x2151;
                L_0x214a:
                    r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r9 = r7;
                L_0x2151:
                    r3 = r1.type;
                    r4 = 3;
                    if (r3 != r4) goto L_0x216b;
                L_0x2156:
                    r3 = r2.infoWidth;
                    r4 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    if (r7 >= r3) goto L_0x216b;
                L_0x2161:
                    r3 = r2.infoWidth;
                    r4 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r7 = r3 + r4;
                L_0x216b:
                    r3 = r2.currentMessagesGroup;
                    if (r3 == 0) goto L_0x21ad;
                L_0x216f:
                    r3 = r68.getGroupPhotosWidth();
                    r4 = 0;
                    r5 = 0;
                L_0x2175:
                    r6 = r2.currentMessagesGroup;
                    r6 = r6.posArray;
                    r6 = r6.size();
                    if (r4 >= r6) goto L_0x21a3;
                L_0x217f:
                    r6 = r2.currentMessagesGroup;
                    r6 = r6.posArray;
                    r6 = r6.get(r4);
                    r6 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r6;
                    r8 = r6.minY;
                    if (r8 != 0) goto L_0x21a3;
                L_0x218d:
                    r10 = (double) r5;
                    r5 = r6.pw;
                    r6 = r6.leftSpanOffset;
                    r5 = r5 + r6;
                    r5 = (float) r5;
                    r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r5 = r5 / r6;
                    r6 = (float) r3;
                    r5 = r5 * r6;
                    r5 = (double) r5;
                    r5 = java.lang.Math.ceil(r5);
                    r10 = r10 + r5;
                    r5 = (int) r10;
                    r4 = r4 + 1;
                    goto L_0x2175;
                L_0x21a3:
                    r3 = NUM; // 0x420c0000 float:35.0 double:5.47465589E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r5 = r5 - r3;
                    r2.availableTimeWidth = r5;
                    goto L_0x21b6;
                L_0x21ad:
                    r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r5 = r5 - r3;
                    r2.availableTimeWidth = r5;
                L_0x21b6:
                    r3 = r1.type;
                    r4 = 5;
                    if (r3 != r4) goto L_0x21d7;
                L_0x21bb:
                    r3 = r2.availableTimeWidth;
                    r3 = (double) r3;
                    r5 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
                    r6 = "00:00";
                    r5 = r5.measureText(r6);
                    r5 = (double) r5;
                    r5 = java.lang.Math.ceil(r5);
                    r8 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
                    r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
                    r10 = (double) r8;
                    r5 = r5 + r10;
                    r3 = r3 - r5;
                    r3 = (int) r3;
                    r2.availableTimeWidth = r3;
                L_0x21d7:
                    r68.measureTime(r69);
                    r3 = r2.timeWidth;
                    r4 = 14;
                    r5 = r69.isOutOwner();
                    if (r5 == 0) goto L_0x21e7;
                L_0x21e4:
                    r5 = 20;
                    goto L_0x21e8;
                L_0x21e7:
                    r5 = 0;
                L_0x21e8:
                    r4 = r4 + r5;
                    r4 = (float) r4;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    if (r7 >= r3) goto L_0x21f2;
                L_0x21f1:
                    r7 = r3;
                L_0x21f2:
                    r4 = r69.isRoundVideo();
                    if (r4 == 0) goto L_0x2208;
                L_0x21f8:
                    r9 = java.lang.Math.min(r7, r9);
                    r4 = 0;
                    r2.drawBackground = r4;
                    r4 = r2.photoImage;
                    r5 = r9 / 2;
                    r4.setRoundRadius(r5);
                L_0x2206:
                    r4 = r9;
                    goto L_0x2232;
                L_0x2208:
                    r4 = r69.needDrawBluredPreview();
                    if (r4 == 0) goto L_0x2230;
                L_0x220e:
                    r4 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r4 == 0) goto L_0x221e;
                L_0x2214:
                    r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r4 = (float) r4;
                    r5 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
                    r4 = r4 * r5;
                    r9 = (int) r4;
                    goto L_0x2206;
                L_0x221e:
                    r4 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r4 = r4.x;
                    r5 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r5 = r5.y;
                    r4 = java.lang.Math.min(r4, r5);
                    r4 = (float) r4;
                    r5 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
                    r4 = r4 * r5;
                    r9 = (int) r4;
                    goto L_0x2206;
                L_0x2230:
                    r4 = r9;
                    r9 = r7;
                L_0x2232:
                    r5 = r2.currentMessagesGroup;
                    if (r5 == 0) goto L_0x253d;
                L_0x2236:
                    r4 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r4 = r4.x;
                    r5 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r5 = r5.y;
                    r4 = java.lang.Math.max(r4, r5);
                    r4 = (float) r4;
                    r5 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
                    r4 = r4 * r5;
                    r5 = r68.getGroupPhotosWidth();
                    r6 = r2.currentPosition;
                    r6 = r6.pw;
                    r6 = (float) r6;
                    r7 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r6 = r6 / r7;
                    r5 = (float) r5;
                    r6 = r6 * r5;
                    r6 = (double) r6;
                    r6 = java.lang.Math.ceil(r6);
                    r6 = (int) r6;
                    r7 = r2.currentPosition;
                    r7 = r7.minY;
                    if (r7 == 0) goto L_0x230d;
                L_0x2260:
                    r7 = r69.isOutOwner();
                    if (r7 == 0) goto L_0x226e;
                L_0x2266:
                    r7 = r2.currentPosition;
                    r7 = r7.flags;
                    r8 = 1;
                    r7 = r7 & r8;
                    if (r7 != 0) goto L_0x227c;
                L_0x226e:
                    r7 = r69.isOutOwner();
                    if (r7 != 0) goto L_0x230d;
                L_0x2274:
                    r7 = r2.currentPosition;
                    r7 = r7.flags;
                    r8 = 2;
                    r7 = r7 & r8;
                    if (r7 == 0) goto L_0x230d;
                L_0x227c:
                    r7 = 0;
                    r8 = 0;
                    r9 = 0;
                L_0x227f:
                    r10 = r2.currentMessagesGroup;
                    r10 = r10.posArray;
                    r10 = r10.size();
                    if (r7 >= r10) goto L_0x2306;
                L_0x2289:
                    r10 = r2.currentMessagesGroup;
                    r10 = r10.posArray;
                    r10 = r10.get(r7);
                    r10 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r10;
                    r11 = r10.minY;
                    if (r11 != 0) goto L_0x22c4;
                L_0x2297:
                    r11 = (double) r8;
                    r8 = r10.pw;
                    r8 = (float) r8;
                    r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r8 = r8 / r13;
                    r8 = r8 * r5;
                    r13 = (double) r8;
                    r13 = java.lang.Math.ceil(r13);
                    r8 = r10.leftSpanOffset;
                    if (r8 == 0) goto L_0x22b9;
                L_0x22a8:
                    r8 = r10.leftSpanOffset;
                    r8 = (float) r8;
                    r10 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r8 = r8 / r10;
                    r8 = r8 * r5;
                    r57 = r3;
                    r58 = r4;
                    r3 = (double) r8;
                    r3 = java.lang.Math.ceil(r3);
                    goto L_0x22bf;
                L_0x22b9:
                    r57 = r3;
                    r58 = r4;
                    r3 = 0;
                L_0x22bf:
                    r13 = r13 + r3;
                    r11 = r11 + r13;
                    r3 = (int) r11;
                    r8 = r3;
                    goto L_0x22fe;
                L_0x22c4:
                    r57 = r3;
                    r58 = r4;
                    r3 = r10.minY;
                    r4 = r2.currentPosition;
                    r4 = r4.minY;
                    if (r3 != r4) goto L_0x22f5;
                L_0x22d0:
                    r3 = (double) r9;
                    r9 = r10.pw;
                    r9 = (float) r9;
                    r11 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r9 = r9 / r11;
                    r9 = r9 * r5;
                    r11 = (double) r9;
                    r11 = java.lang.Math.ceil(r11);
                    r9 = r10.leftSpanOffset;
                    if (r9 == 0) goto L_0x22ee;
                L_0x22e1:
                    r9 = r10.leftSpanOffset;
                    r9 = (float) r9;
                    r10 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r9 = r9 / r10;
                    r9 = r9 * r5;
                    r9 = (double) r9;
                    r9 = java.lang.Math.ceil(r9);
                    goto L_0x22f0;
                L_0x22ee:
                    r9 = 0;
                L_0x22f0:
                    r11 = r11 + r9;
                    r3 = r3 + r11;
                    r3 = (int) r3;
                    r9 = r3;
                    goto L_0x22fe;
                L_0x22f5:
                    r3 = r10.minY;
                    r4 = r2.currentPosition;
                    r4 = r4.minY;
                    if (r3 <= r4) goto L_0x22fe;
                L_0x22fd:
                    goto L_0x230a;
                L_0x22fe:
                    r7 = r7 + 1;
                    r3 = r57;
                    r4 = r58;
                    goto L_0x227f;
                L_0x2306:
                    r57 = r3;
                    r58 = r4;
                L_0x230a:
                    r8 = r8 - r9;
                    r6 = r6 + r8;
                    goto L_0x2311;
                L_0x230d:
                    r57 = r3;
                    r58 = r4;
                L_0x2311:
                    r3 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r6 = r6 - r3;
                    r3 = r2.isAvatarVisible;
                    if (r3 == 0) goto L_0x2323;
                L_0x231c:
                    r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r6 = r6 - r3;
                L_0x2323:
                    r3 = r2.currentPosition;
                    r3 = r3.siblingHeights;
                    if (r3 == 0) goto L_0x2356;
                L_0x2329:
                    r3 = 0;
                    r4 = 0;
                L_0x232b:
                    r7 = r2.currentPosition;
                    r7 = r7.siblingHeights;
                    r7 = r7.length;
                    if (r3 >= r7) goto L_0x2344;
                L_0x2332:
                    r7 = r2.currentPosition;
                    r7 = r7.siblingHeights;
                    r7 = r7[r3];
                    r7 = r7 * r58;
                    r7 = (double) r7;
                    r7 = java.lang.Math.ceil(r7);
                    r7 = (int) r7;
                    r4 = r4 + r7;
                    r3 = r3 + 1;
                    goto L_0x232b;
                L_0x2344:
                    r3 = r2.currentPosition;
                    r3 = r3.maxY;
                    r7 = r2.currentPosition;
                    r7 = r7.minY;
                    r3 = r3 - r7;
                    r7 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r3 = r3 * r7;
                    r4 = r4 + r3;
                    goto L_0x2362;
                L_0x2356:
                    r3 = r2.currentPosition;
                    r3 = r3.ph;
                    r4 = r58 * r3;
                    r3 = (double) r4;
                    r3 = java.lang.Math.ceil(r3);
                    r4 = (int) r3;
                L_0x2362:
                    r2.backgroundWidth = r6;
                    r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r6 = r6 - r3;
                    r3 = r2.currentPosition;
                    r3 = r3.edge;
                    if (r3 != 0) goto L_0x2379;
                L_0x2371:
                    r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r7 = r7 + r6;
                    goto L_0x237c;
                L_0x2379:
                    r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r7 = r6;
                L_0x237c:
                    r8 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r3 = r7 - r8;
                    r8 = 0;
                    r9 = r8 + r3;
                    r3 = r2.currentPosition;
                    r3 = r3.flags;
                    r8 = 8;
                    r3 = r3 & r8;
                    if (r3 != 0) goto L_0x23a4;
                L_0x238e:
                    r3 = r2.currentMessagesGroup;
                    r3 = r3.hasSibling;
                    if (r3 == 0) goto L_0x239d;
                L_0x2394:
                    r3 = r2.currentPosition;
                    r3 = r3.flags;
                    r3 = r3 & 4;
                    if (r3 != 0) goto L_0x239d;
                L_0x239c:
                    goto L_0x23a4;
                L_0x239d:
                    r60 = r4;
                    r8 = r6;
                    r61 = r7;
                    goto L_0x2538;
                L_0x23a4:
                    r3 = r2.currentPosition;
                    r3 = r2.getAdditionalWidthForPosition(r3);
                    r9 = r9 + r3;
                    r3 = r2.currentMessagesGroup;
                    r3 = r3.messages;
                    r3 = r3.size();
                    r8 = r6;
                    r6 = 0;
                L_0x23b5:
                    if (r6 >= r3) goto L_0x2532;
                L_0x23b7:
                    r10 = r2.currentMessagesGroup;
                    r10 = r10.messages;
                    r10 = r10.get(r6);
                    r10 = (org.telegram.messenger.MessageObject) r10;
                    r11 = r2.currentMessagesGroup;
                    r11 = r11.posArray;
                    r11 = r11.get(r6);
                    r11 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r11;
                    r12 = r2.currentPosition;
                    if (r11 == r12) goto L_0x250c;
                L_0x23cf:
                    r12 = r11.flags;
                    r13 = 8;
                    r12 = r12 & r13;
                    if (r12 == 0) goto L_0x250c;
                L_0x23d6:
                    r8 = r11.pw;
                    r8 = (float) r8;
                    r12 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r8 = r8 / r12;
                    r8 = r8 * r5;
                    r12 = (double) r8;
                    r12 = java.lang.Math.ceil(r12);
                    r8 = (int) r12;
                    r12 = r11.minY;
                    if (r12 == 0) goto L_0x24ab;
                L_0x23e7:
                    r12 = r69.isOutOwner();
                    if (r12 == 0) goto L_0x23f3;
                L_0x23ed:
                    r12 = r11.flags;
                    r13 = 1;
                    r12 = r12 & r13;
                    if (r12 != 0) goto L_0x23ff;
                L_0x23f3:
                    r12 = r69.isOutOwner();
                    if (r12 != 0) goto L_0x24ab;
                L_0x23f9:
                    r12 = r11.flags;
                    r13 = 2;
                    r12 = r12 & r13;
                    if (r12 == 0) goto L_0x24ab;
                L_0x23ff:
                    r12 = 0;
                    r13 = 0;
                    r14 = 0;
                L_0x2402:
                    r15 = r2.currentMessagesGroup;
                    r15 = r15.posArray;
                    r15 = r15.size();
                    if (r12 >= r15) goto L_0x249c;
                L_0x240c:
                    r15 = r2.currentMessagesGroup;
                    r15 = r15.posArray;
                    r15 = r15.get(r12);
                    r15 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r15;
                    r59 = r3;
                    r3 = r15.minY;
                    if (r3 != 0) goto L_0x2450;
                L_0x241c:
                    r60 = r4;
                    r3 = (double) r13;
                    r13 = r15.pw;
                    r13 = (float) r13;
                    r18 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r13 = r13 / r18;
                    r13 = r13 * r5;
                    r62 = r6;
                    r61 = r7;
                    r6 = (double) r13;
                    r6 = java.lang.Math.ceil(r6);
                    r13 = r15.leftSpanOffset;
                    if (r13 == 0) goto L_0x2445;
                L_0x2434:
                    r13 = r15.leftSpanOffset;
                    r13 = (float) r13;
                    r15 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r13 = r13 / r15;
                    r13 = r13 * r5;
                    r63 = r9;
                    r64 = r10;
                    r9 = (double) r13;
                    r9 = java.lang.Math.ceil(r9);
                    goto L_0x244b;
                L_0x2445:
                    r63 = r9;
                    r64 = r10;
                    r9 = 0;
                L_0x244b:
                    r6 = r6 + r9;
                    r3 = r3 + r6;
                    r3 = (int) r3;
                    r13 = r3;
                    goto L_0x248c;
                L_0x2450:
                    r60 = r4;
                    r62 = r6;
                    r61 = r7;
                    r63 = r9;
                    r64 = r10;
                    r3 = r15.minY;
                    r4 = r11.minY;
                    if (r3 != r4) goto L_0x2485;
                L_0x2460:
                    r3 = (double) r14;
                    r6 = r15.pw;
                    r6 = (float) r6;
                    r7 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r6 = r6 / r7;
                    r6 = r6 * r5;
                    r6 = (double) r6;
                    r6 = java.lang.Math.ceil(r6);
                    r9 = r15.leftSpanOffset;
                    if (r9 == 0) goto L_0x247e;
                L_0x2471:
                    r9 = r15.leftSpanOffset;
                    r9 = (float) r9;
                    r10 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
                    r9 = r9 / r10;
                    r9 = r9 * r5;
                    r9 = (double) r9;
                    r9 = java.lang.Math.ceil(r9);
                    goto L_0x2480;
                L_0x247e:
                    r9 = 0;
                L_0x2480:
                    r6 = r6 + r9;
                    r3 = r3 + r6;
                    r3 = (int) r3;
                    r14 = r3;
                    goto L_0x248c;
                L_0x2485:
                    r3 = r15.minY;
                    r4 = r11.minY;
                    if (r3 <= r4) goto L_0x248c;
                L_0x248b:
                    goto L_0x24a8;
                L_0x248c:
                    r12 = r12 + 1;
                    r3 = r59;
                    r4 = r60;
                    r7 = r61;
                    r6 = r62;
                    r9 = r63;
                    r10 = r64;
                    goto L_0x2402;
                L_0x249c:
                    r59 = r3;
                    r60 = r4;
                    r62 = r6;
                    r61 = r7;
                    r63 = r9;
                    r64 = r10;
                L_0x24a8:
                    r13 = r13 - r14;
                    r8 = r8 + r13;
                    goto L_0x24b7;
                L_0x24ab:
                    r59 = r3;
                    r60 = r4;
                    r62 = r6;
                    r61 = r7;
                    r63 = r9;
                    r64 = r10;
                L_0x24b7:
                    r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r8 = r8 - r3;
                    r3 = r2.isChat;
                    if (r3 == 0) goto L_0x24de;
                L_0x24c2:
                    r10 = r64;
                    r3 = r10.isOutOwner();
                    if (r3 != 0) goto L_0x24e0;
                L_0x24ca:
                    r3 = r10.needDrawAvatar();
                    if (r3 == 0) goto L_0x24e0;
                L_0x24d0:
                    if (r11 == 0) goto L_0x24d6;
                L_0x24d2:
                    r3 = r11.edge;
                    if (r3 == 0) goto L_0x24e0;
                L_0x24d6:
                    r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r8 = r8 - r3;
                    goto L_0x24e0;
                L_0x24de:
                    r10 = r64;
                L_0x24e0:
                    r3 = r2.getAdditionalWidthForPosition(r11);
                    r8 = r8 + r3;
                    r3 = r11.edge;
                    if (r3 != 0) goto L_0x24f0;
                L_0x24e9:
                    r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r8 = r8 + r4;
                L_0x24f0:
                    r9 = r63 + r8;
                    r3 = r11.minX;
                    r4 = r2.currentPosition;
                    r4 = r4.minX;
                    if (r3 < r4) goto L_0x2506;
                L_0x24fa:
                    r3 = r2.currentMessagesGroup;
                    r3 = r3.hasSibling;
                    if (r3 == 0) goto L_0x2518;
                L_0x2500:
                    r3 = r11.minY;
                    r4 = r11.maxY;
                    if (r3 == r4) goto L_0x2518;
                L_0x2506:
                    r3 = r2.captionOffsetX;
                    r3 = r3 - r8;
                    r2.captionOffsetX = r3;
                    goto L_0x2518;
                L_0x250c:
                    r59 = r3;
                    r60 = r4;
                    r62 = r6;
                    r61 = r7;
                    r63 = r9;
                    r9 = r63;
                L_0x2518:
                    r3 = r10.caption;
                    if (r3 == 0) goto L_0x2528;
                L_0x251c:
                    r3 = r2.currentCaption;
                    if (r3 == 0) goto L_0x2524;
                L_0x2520:
                    r3 = 0;
                    r2.currentCaption = r3;
                    goto L_0x2538;
                L_0x2524:
                    r3 = r10.caption;
                    r2.currentCaption = r3;
                L_0x2528:
                    r6 = r62 + 1;
                    r3 = r59;
                    r4 = r60;
                    r7 = r61;
                    goto L_0x23b5;
                L_0x2532:
                    r60 = r4;
                    r61 = r7;
                    r63 = r9;
                L_0x2538:
                    r4 = r60;
                    r5 = r61;
                    goto L_0x2566;
                L_0x253d:
                    r57 = r3;
                    r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r3 = r3 + r9;
                    r2.backgroundWidth = r3;
                    r3 = r2.mediaBackground;
                    if (r3 != 0) goto L_0x2557;
                L_0x254c:
                    r3 = r2.backgroundWidth;
                    r5 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r3 = r3 + r5;
                    r2.backgroundWidth = r3;
                L_0x2557:
                    r3 = r1.caption;
                    r2.currentCaption = r3;
                    r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r3 = r9 - r5;
                    r5 = r9;
                    r8 = r5;
                    r9 = r3;
                L_0x2566:
                    r3 = r2.currentCaption;
                    if (r3 == 0) goto L_0x2631;
                L_0x256a:
                    r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x2629 }
                    r6 = 24;	 Catch:{ Exception -> 0x2629 }
                    if (r3 < r6) goto L_0x2595;	 Catch:{ Exception -> 0x2629 }
                L_0x2570:
                    r3 = r2.currentCaption;	 Catch:{ Exception -> 0x2629 }
                    r6 = r2.currentCaption;	 Catch:{ Exception -> 0x2629 }
                    r6 = r6.length();	 Catch:{ Exception -> 0x2629 }
                    r7 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x2629 }
                    r10 = 0;	 Catch:{ Exception -> 0x2629 }
                    r3 = android.text.StaticLayout.Builder.obtain(r3, r10, r6, r7, r9);	 Catch:{ Exception -> 0x2629 }
                    r6 = 1;	 Catch:{ Exception -> 0x2629 }
                    r3 = r3.setBreakStrategy(r6);	 Catch:{ Exception -> 0x2629 }
                    r3 = r3.setHyphenationFrequency(r10);	 Catch:{ Exception -> 0x2629 }
                    r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x2629 }
                    r3 = r3.setAlignment(r6);	 Catch:{ Exception -> 0x2629 }
                    r3 = r3.build();	 Catch:{ Exception -> 0x2629 }
                    r2.captionLayout = r3;	 Catch:{ Exception -> 0x2629 }
                    goto L_0x25ae;	 Catch:{ Exception -> 0x2629 }
                L_0x2595:
                    r3 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x2629 }
                    r6 = r2.currentCaption;	 Catch:{ Exception -> 0x2629 }
                    r25 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x2629 }
                    r27 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x2629 }
                    r28 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x2629 }
                    r29 = 0;	 Catch:{ Exception -> 0x2629 }
                    r30 = 0;	 Catch:{ Exception -> 0x2629 }
                    r23 = r3;	 Catch:{ Exception -> 0x2629 }
                    r24 = r6;	 Catch:{ Exception -> 0x2629 }
                    r26 = r9;	 Catch:{ Exception -> 0x2629 }
                    r23.<init>(r24, r25, r26, r27, r28, r29, r30);	 Catch:{ Exception -> 0x2629 }
                    r2.captionLayout = r3;	 Catch:{ Exception -> 0x2629 }
                L_0x25ae:
                    r3 = r2.captionLayout;	 Catch:{ Exception -> 0x2629 }
                    r3 = r3.getLineCount();	 Catch:{ Exception -> 0x2629 }
                    if (r3 <= 0) goto L_0x2631;	 Catch:{ Exception -> 0x2629 }
                L_0x25b6:
                    r2.captionWidth = r9;	 Catch:{ Exception -> 0x2629 }
                    r3 = r2.captionLayout;	 Catch:{ Exception -> 0x2629 }
                    r3 = r3.getHeight();	 Catch:{ Exception -> 0x2629 }
                    r2.captionHeight = r3;	 Catch:{ Exception -> 0x2629 }
                    r3 = r2.captionHeight;	 Catch:{ Exception -> 0x2629 }
                    r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;	 Catch:{ Exception -> 0x2629 }
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x2629 }
                    r3 = r3 + r6;	 Catch:{ Exception -> 0x2629 }
                    r2.addedCaptionHeight = r3;	 Catch:{ Exception -> 0x2629 }
                    r3 = r2.currentPosition;	 Catch:{ Exception -> 0x2629 }
                    if (r3 == 0) goto L_0x25dd;	 Catch:{ Exception -> 0x2629 }
                L_0x25cf:
                    r3 = r2.currentPosition;	 Catch:{ Exception -> 0x2629 }
                    r3 = r3.flags;	 Catch:{ Exception -> 0x2629 }
                    r6 = 8;	 Catch:{ Exception -> 0x2629 }
                    r3 = r3 & r6;	 Catch:{ Exception -> 0x2629 }
                    if (r3 == 0) goto L_0x25d9;	 Catch:{ Exception -> 0x2629 }
                L_0x25d8:
                    goto L_0x25dd;	 Catch:{ Exception -> 0x2629 }
                L_0x25d9:
                    r3 = 0;	 Catch:{ Exception -> 0x2629 }
                    r2.captionLayout = r3;	 Catch:{ Exception -> 0x2629 }
                    goto L_0x2631;	 Catch:{ Exception -> 0x2629 }
                L_0x25dd:
                    r3 = r2.addedCaptionHeight;	 Catch:{ Exception -> 0x2629 }
                    r6 = 0;
                    r3 = r3 + r6;
                    r6 = r2.captionLayout;	 Catch:{ Exception -> 0x2625 }
                    r7 = r2.captionLayout;	 Catch:{ Exception -> 0x2625 }
                    r7 = r7.getLineCount();	 Catch:{ Exception -> 0x2625 }
                    r10 = 1;	 Catch:{ Exception -> 0x2625 }
                    r7 = r7 - r10;	 Catch:{ Exception -> 0x2625 }
                    r6 = r6.getLineWidth(r7);	 Catch:{ Exception -> 0x2625 }
                    r7 = r2.captionLayout;	 Catch:{ Exception -> 0x2625 }
                    r11 = r2.captionLayout;	 Catch:{ Exception -> 0x2625 }
                    r11 = r11.getLineCount();	 Catch:{ Exception -> 0x2625 }
                    r11 = r11 - r10;	 Catch:{ Exception -> 0x2625 }
                    r7 = r7.getLineLeft(r11);	 Catch:{ Exception -> 0x2625 }
                    r6 = r6 + r7;	 Catch:{ Exception -> 0x2625 }
                    r7 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x2625 }
                    r10 = org.telegram.messenger.AndroidUtilities.dp(r7);	 Catch:{ Exception -> 0x2625 }
                    r9 = r9 + r10;	 Catch:{ Exception -> 0x2625 }
                    r7 = (float) r9;	 Catch:{ Exception -> 0x2625 }
                    r7 = r7 - r6;	 Catch:{ Exception -> 0x2625 }
                    r6 = r57;	 Catch:{ Exception -> 0x2625 }
                    r6 = (float) r6;	 Catch:{ Exception -> 0x2625 }
                    r6 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1));	 Catch:{ Exception -> 0x2625 }
                    if (r6 >= 0) goto L_0x2632;	 Catch:{ Exception -> 0x2625 }
                L_0x260d:
                    r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;	 Catch:{ Exception -> 0x2625 }
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x2625 }
                    r9 = r3 + r6;
                    r3 = r2.addedCaptionHeight;	 Catch:{ Exception -> 0x2623 }
                    r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;	 Catch:{ Exception -> 0x2623 }
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x2623 }
                    r3 = r3 + r6;	 Catch:{ Exception -> 0x2623 }
                    r2.addedCaptionHeight = r3;	 Catch:{ Exception -> 0x2623 }
                    r3 = r9;
                    r9 = 1;
                    goto L_0x2633;
                L_0x2623:
                    r0 = move-exception;
                    goto L_0x2627;
                L_0x2625:
                    r0 = move-exception;
                    r9 = r3;
                L_0x2627:
                    r3 = r0;
                    goto L_0x262c;
                L_0x2629:
                    r0 = move-exception;
                    r3 = r0;
                    r9 = 0;
                L_0x262c:
                    org.telegram.messenger.FileLog.m3e(r3);
                    r3 = r9;
                    goto L_0x2632;
                L_0x2631:
                    r3 = 0;
                L_0x2632:
                    r9 = 0;
                L_0x2633:
                    r6 = java.util.Locale.US;
                    r7 = "%d_%d";
                    r10 = 2;
                    r11 = new java.lang.Object[r10];
                    r8 = (float) r8;
                    r10 = org.telegram.messenger.AndroidUtilities.density;
                    r8 = r8 / r10;
                    r8 = (int) r8;
                    r8 = java.lang.Integer.valueOf(r8);
                    r10 = 0;
                    r11[r10] = r8;
                    r8 = (float) r4;
                    r10 = org.telegram.messenger.AndroidUtilities.density;
                    r8 = r8 / r10;
                    r8 = (int) r8;
                    r8 = java.lang.Integer.valueOf(r8);
                    r10 = 1;
                    r11[r10] = r8;
                    r6 = java.lang.String.format(r6, r7, r11);
                    r2.currentPhotoFilterThumb = r6;
                    r2.currentPhotoFilter = r6;
                    r6 = r1.photoThumbs;
                    if (r6 == 0) goto L_0x2666;
                L_0x265e:
                    r6 = r1.photoThumbs;
                    r6 = r6.size();
                    if (r6 > r10) goto L_0x2676;
                L_0x2666:
                    r6 = r1.type;
                    r7 = 3;
                    if (r6 == r7) goto L_0x2676;
                L_0x266b:
                    r6 = r1.type;
                    r7 = 8;
                    if (r6 == r7) goto L_0x2676;
                L_0x2671:
                    r6 = r1.type;
                    r7 = 5;
                    if (r6 != r7) goto L_0x26bc;
                L_0x2676:
                    r6 = r69.needDrawBluredPreview();
                    if (r6 == 0) goto L_0x26a7;
                L_0x267c:
                    r6 = new java.lang.StringBuilder;
                    r6.<init>();
                    r7 = r2.currentPhotoFilter;
                    r6.append(r7);
                    r7 = "_b2";
                    r6.append(r7);
                    r6 = r6.toString();
                    r2.currentPhotoFilter = r6;
                    r6 = new java.lang.StringBuilder;
                    r6.<init>();
                    r7 = r2.currentPhotoFilterThumb;
                    r6.append(r7);
                    r7 = "_b2";
                    r6.append(r7);
                    r6 = r6.toString();
                    r2.currentPhotoFilterThumb = r6;
                    goto L_0x26bc;
                L_0x26a7:
                    r6 = new java.lang.StringBuilder;
                    r6.<init>();
                    r7 = r2.currentPhotoFilterThumb;
                    r6.append(r7);
                    r7 = "_b";
                    r6.append(r7);
                    r6 = r6.toString();
                    r2.currentPhotoFilterThumb = r6;
                L_0x26bc:
                    r6 = r1.type;
                    r7 = 3;
                    if (r6 == r7) goto L_0x26cf;
                L_0x26c1:
                    r6 = r1.type;
                    r7 = 8;
                    if (r6 == r7) goto L_0x26cf;
                L_0x26c7:
                    r6 = r1.type;
                    r7 = 5;
                    if (r6 != r7) goto L_0x26cd;
                L_0x26cc:
                    goto L_0x26cf;
                L_0x26cd:
                    r6 = 0;
                    goto L_0x26d0;
                L_0x26cf:
                    r6 = 1;
                L_0x26d0:
                    r7 = r2.currentPhotoObject;
                    if (r7 == 0) goto L_0x26e1;
                L_0x26d4:
                    if (r6 != 0) goto L_0x26e1;
                L_0x26d6:
                    r7 = r2.currentPhotoObject;
                    r7 = r7.size;
                    if (r7 != 0) goto L_0x26e1;
                L_0x26dc:
                    r7 = r2.currentPhotoObject;
                    r8 = -1;
                    r7.size = r8;
                L_0x26e1:
                    r7 = r1.type;
                    r8 = 1;
                    if (r7 != r8) goto L_0x27b6;
                L_0x26e6:
                    r7 = r1.useCustomPhoto;
                    if (r7 == 0) goto L_0x26fc;
                L_0x26ea:
                    r6 = r2.photoImage;
                    r7 = r68.getResources();
                    r8 = NUM; // 0x7f0701e7 float:1.7945566E38 double:1.0529357436E-314;
                    r7 = r7.getDrawable(r8);
                    r6.setImageBitmap(r7);
                    goto L_0x28e0;
                L_0x26fc:
                    r7 = r2.currentPhotoObject;
                    if (r7 == 0) goto L_0x27ab;
                L_0x2700:
                    r7 = r2.currentPhotoObject;
                    r7 = org.telegram.messenger.FileLoader.getAttachFileName(r7);
                    r8 = r1.mediaExists;
                    if (r8 == 0) goto L_0x2715;
                L_0x270a:
                    r8 = r2.currentAccount;
                    r8 = org.telegram.messenger.DownloadController.getInstance(r8);
                    r8.removeLoadingFileObserver(r2);
                    r8 = 1;
                    goto L_0x2716;
                L_0x2715:
                    r8 = 0;
                L_0x2716:
                    if (r8 != 0) goto L_0x276d;
                L_0x2718:
                    r8 = r2.currentAccount;
                    r8 = org.telegram.messenger.DownloadController.getInstance(r8);
                    r10 = r2.currentMessageObject;
                    r8 = r8.canDownloadMedia(r10);
                    if (r8 != 0) goto L_0x276d;
                L_0x2726:
                    r8 = r2.currentAccount;
                    r8 = org.telegram.messenger.FileLoader.getInstance(r8);
                    r7 = r8.isLoadingFile(r7);
                    if (r7 == 0) goto L_0x2733;
                L_0x2732:
                    goto L_0x276d;
                L_0x2733:
                    r7 = 1;
                    r2.photoNotSet = r7;
                    r6 = r2.currentPhotoObjectThumb;
                    if (r6 == 0) goto L_0x2762;
                L_0x273a:
                    r6 = r2.photoImage;
                    r24 = 0;
                    r25 = 0;
                    r7 = r2.currentPhotoObjectThumb;
                    r7 = r7.location;
                    r8 = r2.currentPhotoFilterThumb;
                    r28 = 0;
                    r29 = 0;
                    r10 = r2.currentMessageObject;
                    r10 = r10.shouldEncryptPhotoOrVideo();
                    if (r10 == 0) goto L_0x2755;
                L_0x2752:
                    r30 = 2;
                    goto L_0x2757;
                L_0x2755:
                    r30 = 0;
                L_0x2757:
                    r23 = r6;
                    r26 = r7;
                    r27 = r8;
                    r23.setImage(r24, r25, r26, r27, r28, r29, r30);
                    goto L_0x28e0;
                L_0x2762:
                    r6 = r2.photoImage;
                    r7 = 0;
                    r8 = r7;
                    r8 = (android.graphics.drawable.Drawable) r8;
                    r6.setImageBitmap(r8);
                    goto L_0x28e0;
                L_0x276d:
                    r7 = r2.photoImage;
                    r8 = r2.currentPhotoObject;
                    r8 = r8.location;
                    r10 = r2.currentPhotoFilter;
                    r11 = r2.currentPhotoObjectThumb;
                    if (r11 == 0) goto L_0x2780;
                L_0x2779:
                    r11 = r2.currentPhotoObjectThumb;
                    r11 = r11.location;
                    r26 = r11;
                    goto L_0x2782;
                L_0x2780:
                    r26 = 0;
                L_0x2782:
                    r11 = r2.currentPhotoFilterThumb;
                    if (r6 == 0) goto L_0x2789;
                L_0x2786:
                    r28 = 0;
                    goto L_0x278f;
                L_0x2789:
                    r6 = r2.currentPhotoObject;
                    r6 = r6.size;
                    r28 = r6;
                L_0x278f:
                    r29 = 0;
                    r6 = r2.currentMessageObject;
                    r6 = r6.shouldEncryptPhotoOrVideo();
                    if (r6 == 0) goto L_0x279c;
                L_0x2799:
                    r30 = 2;
                    goto L_0x279e;
                L_0x279c:
                    r30 = 0;
                L_0x279e:
                    r23 = r7;
                    r24 = r8;
                    r25 = r10;
                    r27 = r11;
                    r23.setImage(r24, r25, r26, r27, r28, r29, r30);
                    goto L_0x28e0;
                L_0x27ab:
                    r6 = r2.photoImage;
                    r7 = 0;
                    r8 = r7;
                    r8 = (android.graphics.drawable.BitmapDrawable) r8;
                    r6.setImageBitmap(r8);
                    goto L_0x28e0;
                L_0x27b6:
                    r6 = r1.type;
                    r7 = 8;
                    if (r6 == r7) goto L_0x27f1;
                L_0x27bc:
                    r6 = r1.type;
                    r7 = 5;
                    if (r6 != r7) goto L_0x27c2;
                L_0x27c1:
                    goto L_0x27f1;
                L_0x27c2:
                    r6 = r2.photoImage;
                    r24 = 0;
                    r25 = 0;
                    r7 = r2.currentPhotoObject;
                    if (r7 == 0) goto L_0x27d3;
                L_0x27cc:
                    r7 = r2.currentPhotoObject;
                    r7 = r7.location;
                    r26 = r7;
                    goto L_0x27d5;
                L_0x27d3:
                    r26 = 0;
                L_0x27d5:
                    r7 = r2.currentPhotoFilterThumb;
                    r28 = 0;
                    r29 = 0;
                    r8 = r2.currentMessageObject;
                    r8 = r8.shouldEncryptPhotoOrVideo();
                    if (r8 == 0) goto L_0x27e6;
                L_0x27e3:
                    r30 = 2;
                    goto L_0x27e8;
                L_0x27e6:
                    r30 = 0;
                L_0x27e8:
                    r23 = r6;
                    r27 = r7;
                    r23.setImage(r24, r25, r26, r27, r28, r29, r30);
                    goto L_0x28e0;
                L_0x27f1:
                    r6 = r1.messageOwner;
                    r6 = r6.media;
                    r6 = r6.document;
                    r6 = org.telegram.messenger.FileLoader.getAttachFileName(r6);
                    r7 = r1.attachPathExists;
                    if (r7 == 0) goto L_0x280a;
                L_0x27ff:
                    r7 = r2.currentAccount;
                    r7 = org.telegram.messenger.DownloadController.getInstance(r7);
                    r7.removeLoadingFileObserver(r2);
                    r7 = 1;
                    goto L_0x2811;
                L_0x280a:
                    r7 = r1.mediaExists;
                    if (r7 == 0) goto L_0x2810;
                L_0x280e:
                    r7 = 2;
                    goto L_0x2811;
                L_0x2810:
                    r7 = 0;
                L_0x2811:
                    r8 = r1.messageOwner;
                    r8 = r8.media;
                    r8 = r8.document;
                    r8 = org.telegram.messenger.MessageObject.isNewGifDocument(r8);
                    if (r8 == 0) goto L_0x282a;
                L_0x281d:
                    r8 = r2.currentAccount;
                    r8 = org.telegram.messenger.DownloadController.getInstance(r8);
                    r10 = r2.currentMessageObject;
                    r8 = r8.canDownloadMedia(r10);
                    goto L_0x283d;
                L_0x282a:
                    r8 = r1.type;
                    r10 = 5;
                    if (r8 != r10) goto L_0x283c;
                L_0x282f:
                    r8 = r2.currentAccount;
                    r8 = org.telegram.messenger.DownloadController.getInstance(r8);
                    r10 = r2.currentMessageObject;
                    r8 = r8.canDownloadMedia(r10);
                    goto L_0x283d;
                L_0x283c:
                    r8 = 0;
                L_0x283d:
                    r10 = r69.isSending();
                    if (r10 != 0) goto L_0x28bb;
                L_0x2843:
                    if (r7 != 0) goto L_0x2853;
                L_0x2845:
                    r10 = r2.currentAccount;
                    r10 = org.telegram.messenger.FileLoader.getInstance(r10);
                    r6 = r10.isLoadingFile(r6);
                    if (r6 != 0) goto L_0x2853;
                L_0x2851:
                    if (r8 == 0) goto L_0x28bb;
                L_0x2853:
                    r6 = 1;
                    if (r7 != r6) goto L_0x288a;
                L_0x2856:
                    r6 = r2.photoImage;
                    r32 = 0;
                    r7 = r69.isSendError();
                    if (r7 == 0) goto L_0x2863;
                L_0x2860:
                    r33 = 0;
                    goto L_0x2869;
                L_0x2863:
                    r7 = r1.messageOwner;
                    r7 = r7.attachPath;
                    r33 = r7;
                L_0x2869:
                    r34 = 0;
                    r35 = 0;
                    r7 = r2.currentPhotoObject;
                    if (r7 == 0) goto L_0x2878;
                L_0x2871:
                    r7 = r2.currentPhotoObject;
                    r7 = r7.location;
                    r36 = r7;
                    goto L_0x287a;
                L_0x2878:
                    r36 = 0;
                L_0x287a:
                    r7 = r2.currentPhotoFilterThumb;
                    r38 = 0;
                    r39 = 0;
                    r40 = 0;
                    r31 = r6;
                    r37 = r7;
                    r31.setImage(r32, r33, r34, r35, r36, r37, r38, r39, r40);
                    goto L_0x28e0;
                L_0x288a:
                    r6 = r2.photoImage;
                    r7 = r1.messageOwner;
                    r7 = r7.media;
                    r7 = r7.document;
                    r25 = 0;
                    r8 = r2.currentPhotoObject;
                    if (r8 == 0) goto L_0x289f;
                L_0x2898:
                    r8 = r2.currentPhotoObject;
                    r8 = r8.location;
                    r26 = r8;
                    goto L_0x28a1;
                L_0x289f:
                    r26 = 0;
                L_0x28a1:
                    r8 = r2.currentPhotoFilterThumb;
                    r10 = r1.messageOwner;
                    r10 = r10.media;
                    r10 = r10.document;
                    r10 = r10.size;
                    r29 = 0;
                    r30 = 0;
                    r23 = r6;
                    r24 = r7;
                    r27 = r8;
                    r28 = r10;
                    r23.setImage(r24, r25, r26, r27, r28, r29, r30);
                    goto L_0x28e0;
                L_0x28bb:
                    r6 = 1;
                    r2.photoNotSet = r6;
                    r6 = r2.photoImage;
                    r24 = 0;
                    r25 = 0;
                    r7 = r2.currentPhotoObject;
                    if (r7 == 0) goto L_0x28cf;
                L_0x28c8:
                    r7 = r2.currentPhotoObject;
                    r7 = r7.location;
                    r26 = r7;
                    goto L_0x28d1;
                L_0x28cf:
                    r26 = 0;
                L_0x28d1:
                    r7 = r2.currentPhotoFilterThumb;
                    r28 = 0;
                    r29 = 0;
                    r30 = 0;
                    r23 = r6;
                    r27 = r7;
                    r23.setImage(r24, r25, r26, r27, r28, r29, r30);
                L_0x28e0:
                    r67 = r9;
                    r9 = r3;
                    r3 = r67;
                L_0x28e5:
                    r68.setMessageObjectInternal(r69);
                    r6 = r2.drawForwardedName;
                    if (r6 == 0) goto L_0x290d;
                L_0x28ec:
                    r6 = r69.needDrawForwarded();
                    if (r6 == 0) goto L_0x290d;
                L_0x28f2:
                    r6 = r2.currentPosition;
                    if (r6 == 0) goto L_0x28fc;
                L_0x28f6:
                    r6 = r2.currentPosition;
                    r6 = r6.minY;
                    if (r6 != 0) goto L_0x290d;
                L_0x28fc:
                    r6 = r1.type;
                    r7 = 5;
                    if (r6 == r7) goto L_0x2922;
                L_0x2901:
                    r6 = r2.namesOffset;
                    r7 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r6 = r6 + r7;
                    r2.namesOffset = r6;
                    goto L_0x2922;
                L_0x290d:
                    r6 = r2.drawNameLayout;
                    if (r6 == 0) goto L_0x2922;
                L_0x2911:
                    r6 = r1.messageOwner;
                    r6 = r6.reply_to_msg_id;
                    if (r6 != 0) goto L_0x2922;
                L_0x2917:
                    r6 = r2.namesOffset;
                    r7 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r6 = r6 + r7;
                    r2.namesOffset = r6;
                L_0x2922:
                    r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r6 = r6 + r4;
                    r7 = r2.namesOffset;
                    r6 = r6 + r7;
                    r6 = r6 + r9;
                    r2.totalHeight = r6;
                    r6 = r2.currentPosition;
                    if (r6 == 0) goto L_0x2947;
                L_0x2933:
                    r6 = r2.currentPosition;
                    r6 = r6.flags;
                    r7 = 8;
                    r6 = r6 & r7;
                    if (r6 != 0) goto L_0x2947;
                L_0x293c:
                    r6 = r2.totalHeight;
                    r7 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r6 = r6 - r7;
                    r2.totalHeight = r6;
                L_0x2947:
                    r6 = r2.currentPosition;
                    if (r6 == 0) goto L_0x297d;
                L_0x294b:
                    r6 = r2.currentPosition;
                    r6 = r2.getAdditionalWidthForPosition(r6);
                    r5 = r5 + r6;
                    r6 = r2.currentPosition;
                    r6 = r6.flags;
                    r6 = r6 & 4;
                    if (r6 != 0) goto L_0x296b;
                L_0x295a:
                    r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r4 = r4 + r6;
                    r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r7 = 0;
                    r9 = 0 - r6;
                    goto L_0x296c;
                L_0x296b:
                    r9 = 0;
                L_0x296c:
                    r6 = r2.currentPosition;
                    r6 = r6.flags;
                    r7 = 8;
                    r6 = r6 & r7;
                    if (r6 != 0) goto L_0x297e;
                L_0x2975:
                    r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                    r4 = r4 + r6;
                    goto L_0x297e;
                L_0x297d:
                    r9 = 0;
                L_0x297e:
                    r6 = r2.drawPinnedTop;
                    if (r6 == 0) goto L_0x298d;
                L_0x2982:
                    r6 = r2.namesOffset;
                    r7 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r8 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r6 = r6 - r8;
                    r2.namesOffset = r6;
                L_0x298d:
                    r6 = r2.photoImage;
                    r7 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r8 = r2.namesOffset;
                    r7 = r7 + r8;
                    r7 = r7 + r9;
                    r8 = 0;
                    r6.setImageCoords(r8, r7, r5, r4);
                    r68.invalidate();
                    r5 = r3;
                L_0x29a1:
                    r3 = r2.currentPosition;
                    if (r3 != 0) goto L_0x2a7f;
                L_0x29a5:
                    r3 = r2.captionLayout;
                    if (r3 != 0) goto L_0x2a7f;
                L_0x29a9:
                    r3 = r1.caption;
                    if (r3 == 0) goto L_0x2a7f;
                L_0x29ad:
                    r3 = r1.type;
                    r4 = 13;
                    if (r3 == r4) goto L_0x2a7f;
                L_0x29b3:
                    r3 = r1.caption;	 Catch:{ Exception -> 0x2a79 }
                    r2.currentCaption = r3;	 Catch:{ Exception -> 0x2a79 }
                    r3 = r2.backgroundWidth;	 Catch:{ Exception -> 0x2a79 }
                    r4 = NUM; // 0x41f80000 float:31.0 double:5.46818007E-315;	 Catch:{ Exception -> 0x2a79 }
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x2a79 }
                    r3 = r3 - r4;	 Catch:{ Exception -> 0x2a79 }
                    r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;	 Catch:{ Exception -> 0x2a79 }
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x2a79 }
                    r4 = r3 - r6;	 Catch:{ Exception -> 0x2a79 }
                    r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x2a79 }
                    r7 = 24;	 Catch:{ Exception -> 0x2a79 }
                    if (r6 < r7) goto L_0x29f3;	 Catch:{ Exception -> 0x2a79 }
                L_0x29ce:
                    r6 = r1.caption;	 Catch:{ Exception -> 0x2a79 }
                    r7 = r1.caption;	 Catch:{ Exception -> 0x2a79 }
                    r7 = r7.length();	 Catch:{ Exception -> 0x2a79 }
                    r8 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x2a79 }
                    r9 = 0;	 Catch:{ Exception -> 0x2a79 }
                    r6 = android.text.StaticLayout.Builder.obtain(r6, r9, r7, r8, r4);	 Catch:{ Exception -> 0x2a79 }
                    r7 = 1;	 Catch:{ Exception -> 0x2a79 }
                    r6 = r6.setBreakStrategy(r7);	 Catch:{ Exception -> 0x2a79 }
                    r6 = r6.setHyphenationFrequency(r9);	 Catch:{ Exception -> 0x2a79 }
                    r7 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x2a79 }
                    r6 = r6.setAlignment(r7);	 Catch:{ Exception -> 0x2a79 }
                    r6 = r6.build();	 Catch:{ Exception -> 0x2a79 }
                    r2.captionLayout = r6;	 Catch:{ Exception -> 0x2a79 }
                    goto L_0x2a06;	 Catch:{ Exception -> 0x2a79 }
                L_0x29f3:
                    r6 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x2a79 }
                    r8 = r1.caption;	 Catch:{ Exception -> 0x2a79 }
                    r9 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x2a79 }
                    r11 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x2a79 }
                    r12 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x2a79 }
                    r13 = 0;	 Catch:{ Exception -> 0x2a79 }
                    r14 = 0;	 Catch:{ Exception -> 0x2a79 }
                    r7 = r6;	 Catch:{ Exception -> 0x2a79 }
                    r10 = r4;	 Catch:{ Exception -> 0x2a79 }
                    r7.<init>(r8, r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x2a79 }
                    r2.captionLayout = r6;	 Catch:{ Exception -> 0x2a79 }
                L_0x2a06:
                    r6 = r2.captionLayout;	 Catch:{ Exception -> 0x2a79 }
                    r6 = r6.getLineCount();	 Catch:{ Exception -> 0x2a79 }
                    if (r6 <= 0) goto L_0x2a98;	 Catch:{ Exception -> 0x2a79 }
                L_0x2a0e:
                    r2.captionWidth = r4;	 Catch:{ Exception -> 0x2a79 }
                    r4 = r2.timeWidth;	 Catch:{ Exception -> 0x2a79 }
                    r6 = r69.isOutOwner();	 Catch:{ Exception -> 0x2a79 }
                    if (r6 == 0) goto L_0x2a1f;	 Catch:{ Exception -> 0x2a79 }
                L_0x2a18:
                    r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;	 Catch:{ Exception -> 0x2a79 }
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x2a79 }
                    goto L_0x2a20;	 Catch:{ Exception -> 0x2a79 }
                L_0x2a1f:
                    r9 = 0;	 Catch:{ Exception -> 0x2a79 }
                L_0x2a20:
                    r4 = r4 + r9;	 Catch:{ Exception -> 0x2a79 }
                    r6 = r2.captionLayout;	 Catch:{ Exception -> 0x2a79 }
                    r6 = r6.getHeight();	 Catch:{ Exception -> 0x2a79 }
                    r2.captionHeight = r6;	 Catch:{ Exception -> 0x2a79 }
                    r6 = r2.totalHeight;	 Catch:{ Exception -> 0x2a79 }
                    r7 = r2.captionHeight;	 Catch:{ Exception -> 0x2a79 }
                    r8 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;	 Catch:{ Exception -> 0x2a79 }
                    r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x2a79 }
                    r7 = r7 + r8;	 Catch:{ Exception -> 0x2a79 }
                    r6 = r6 + r7;	 Catch:{ Exception -> 0x2a79 }
                    r2.totalHeight = r6;	 Catch:{ Exception -> 0x2a79 }
                    r6 = r2.captionLayout;	 Catch:{ Exception -> 0x2a79 }
                    r7 = r2.captionLayout;	 Catch:{ Exception -> 0x2a79 }
                    r7 = r7.getLineCount();	 Catch:{ Exception -> 0x2a79 }
                    r8 = 1;	 Catch:{ Exception -> 0x2a79 }
                    r7 = r7 - r8;	 Catch:{ Exception -> 0x2a79 }
                    r6 = r6.getLineWidth(r7);	 Catch:{ Exception -> 0x2a79 }
                    r7 = r2.captionLayout;	 Catch:{ Exception -> 0x2a79 }
                    r9 = r2.captionLayout;	 Catch:{ Exception -> 0x2a79 }
                    r9 = r9.getLineCount();	 Catch:{ Exception -> 0x2a79 }
                    r9 = r9 - r8;	 Catch:{ Exception -> 0x2a79 }
                    r7 = r7.getLineLeft(r9);	 Catch:{ Exception -> 0x2a79 }
                    r6 = r6 + r7;	 Catch:{ Exception -> 0x2a79 }
                    r7 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;	 Catch:{ Exception -> 0x2a79 }
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r7);	 Catch:{ Exception -> 0x2a79 }
                    r3 = r3 - r7;	 Catch:{ Exception -> 0x2a79 }
                    r3 = (float) r3;	 Catch:{ Exception -> 0x2a79 }
                    r3 = r3 - r6;	 Catch:{ Exception -> 0x2a79 }
                    r4 = (float) r4;	 Catch:{ Exception -> 0x2a79 }
                    r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));	 Catch:{ Exception -> 0x2a79 }
                    if (r3 >= 0) goto L_0x2a98;	 Catch:{ Exception -> 0x2a79 }
                L_0x2a61:
                    r3 = r2.totalHeight;	 Catch:{ Exception -> 0x2a79 }
                    r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;	 Catch:{ Exception -> 0x2a79 }
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x2a79 }
                    r3 = r3 + r4;	 Catch:{ Exception -> 0x2a79 }
                    r2.totalHeight = r3;	 Catch:{ Exception -> 0x2a79 }
                    r3 = r2.captionHeight;	 Catch:{ Exception -> 0x2a79 }
                    r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;	 Catch:{ Exception -> 0x2a79 }
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x2a79 }
                    r3 = r3 + r4;	 Catch:{ Exception -> 0x2a79 }
                    r2.captionHeight = r3;	 Catch:{ Exception -> 0x2a79 }
                    r5 = 2;
                    goto L_0x2a98;
                L_0x2a79:
                    r0 = move-exception;
                    r3 = r0;
                    org.telegram.messenger.FileLog.m3e(r3);
                    goto L_0x2a98;
                L_0x2a7f:
                    r3 = r2.widthBeforeNewTimeLine;
                    r4 = -1;
                    if (r3 == r4) goto L_0x2a98;
                L_0x2a84:
                    r3 = r2.availableTimeWidth;
                    r4 = r2.widthBeforeNewTimeLine;
                    r3 = r3 - r4;
                    r4 = r2.timeWidth;
                    if (r3 >= r4) goto L_0x2a98;
                L_0x2a8d:
                    r3 = r2.totalHeight;
                    r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    r2.totalHeight = r3;
                L_0x2a98:
                    r3 = r2.currentMessageObject;
                    r3 = r3.eventId;
                    r6 = 0;
                    r8 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));
                    if (r8 == 0) goto L_0x2bbe;
                L_0x2aa2:
                    r3 = r2.currentMessageObject;
                    r3 = r3.isMediaEmpty();
                    if (r3 != 0) goto L_0x2bbe;
                L_0x2aaa:
                    r3 = r2.currentMessageObject;
                    r3 = r3.messageOwner;
                    r3 = r3.media;
                    r3 = r3.webpage;
                    if (r3 == 0) goto L_0x2bbe;
                L_0x2ab4:
                    r3 = r2.backgroundWidth;
                    r4 = NUM; // 0x42240000 float:41.0 double:5.48242687E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r14 = r3 - r4;
                    r3 = 1;
                    r2.hasOldCaptionPreview = r3;
                    r3 = 0;
                    r2.linkPreviewHeight = r3;
                    r3 = r2.currentMessageObject;
                    r3 = r3.messageOwner;
                    r3 = r3.media;
                    r3 = r3.webpage;
                    r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x2b1f }
                    r6 = r3.site_name;	 Catch:{ Exception -> 0x2b1f }
                    r4 = r4.measureText(r6);	 Catch:{ Exception -> 0x2b1f }
                    r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x2b1f }
                    r4 = r4 + r6;	 Catch:{ Exception -> 0x2b1f }
                    r6 = (double) r4;	 Catch:{ Exception -> 0x2b1f }
                    r6 = java.lang.Math.ceil(r6);	 Catch:{ Exception -> 0x2b1f }
                    r4 = (int) r6;	 Catch:{ Exception -> 0x2b1f }
                    r2.siteNameWidth = r4;	 Catch:{ Exception -> 0x2b1f }
                    r15 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x2b1f }
                    r7 = r3.site_name;	 Catch:{ Exception -> 0x2b1f }
                    r8 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x2b1f }
                    r9 = java.lang.Math.min(r4, r14);	 Catch:{ Exception -> 0x2b1f }
                    r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x2b1f }
                    r11 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x2b1f }
                    r12 = 0;	 Catch:{ Exception -> 0x2b1f }
                    r13 = 0;	 Catch:{ Exception -> 0x2b1f }
                    r6 = r15;	 Catch:{ Exception -> 0x2b1f }
                    r6.<init>(r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x2b1f }
                    r2.siteNameLayout = r15;	 Catch:{ Exception -> 0x2b1f }
                    r4 = r2.siteNameLayout;	 Catch:{ Exception -> 0x2b1f }
                    r6 = 0;	 Catch:{ Exception -> 0x2b1f }
                    r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x2b1f }
                    r6 = 0;	 Catch:{ Exception -> 0x2b1f }
                    r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ Exception -> 0x2b1f }
                    if (r4 == 0) goto L_0x2b03;	 Catch:{ Exception -> 0x2b1f }
                L_0x2b01:
                    r4 = 1;	 Catch:{ Exception -> 0x2b1f }
                    goto L_0x2b04;	 Catch:{ Exception -> 0x2b1f }
                L_0x2b03:
                    r4 = 0;	 Catch:{ Exception -> 0x2b1f }
                L_0x2b04:
                    r2.siteNameRtl = r4;	 Catch:{ Exception -> 0x2b1f }
                    r4 = r2.siteNameLayout;	 Catch:{ Exception -> 0x2b1f }
                    r6 = r2.siteNameLayout;	 Catch:{ Exception -> 0x2b1f }
                    r6 = r6.getLineCount();	 Catch:{ Exception -> 0x2b1f }
                    r7 = 1;	 Catch:{ Exception -> 0x2b1f }
                    r6 = r6 - r7;	 Catch:{ Exception -> 0x2b1f }
                    r4 = r4.getLineBottom(r6);	 Catch:{ Exception -> 0x2b1f }
                    r6 = r2.linkPreviewHeight;	 Catch:{ Exception -> 0x2b1f }
                    r6 = r6 + r4;	 Catch:{ Exception -> 0x2b1f }
                    r2.linkPreviewHeight = r6;	 Catch:{ Exception -> 0x2b1f }
                    r6 = r2.totalHeight;	 Catch:{ Exception -> 0x2b1f }
                    r6 = r6 + r4;	 Catch:{ Exception -> 0x2b1f }
                    r2.totalHeight = r6;	 Catch:{ Exception -> 0x2b1f }
                    goto L_0x2b24;
                L_0x2b1f:
                    r0 = move-exception;
                    r4 = r0;
                    org.telegram.messenger.FileLog.m3e(r4);
                L_0x2b24:
                    r4 = 0;
                    r2.descriptionX = r4;	 Catch:{ Exception -> 0x2b93 }
                    r4 = r2.linkPreviewHeight;	 Catch:{ Exception -> 0x2b93 }
                    if (r4 == 0) goto L_0x2b36;	 Catch:{ Exception -> 0x2b93 }
                L_0x2b2b:
                    r4 = r2.totalHeight;	 Catch:{ Exception -> 0x2b93 }
                    r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x2b93 }
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x2b93 }
                    r4 = r4 + r7;	 Catch:{ Exception -> 0x2b93 }
                    r2.totalHeight = r4;	 Catch:{ Exception -> 0x2b93 }
                L_0x2b36:
                    r6 = r3.description;	 Catch:{ Exception -> 0x2b93 }
                    r7 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x2b93 }
                    r9 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x2b93 }
                    r10 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x2b93 }
                    r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x2b93 }
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x2b93 }
                    r11 = (float) r4;	 Catch:{ Exception -> 0x2b93 }
                    r12 = 0;	 Catch:{ Exception -> 0x2b93 }
                    r13 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x2b93 }
                    r15 = 6;	 Catch:{ Exception -> 0x2b93 }
                    r8 = r14;	 Catch:{ Exception -> 0x2b93 }
                    r3 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r6, r7, r8, r9, r10, r11, r12, r13, r14, r15);	 Catch:{ Exception -> 0x2b93 }
                    r2.descriptionLayout = r3;	 Catch:{ Exception -> 0x2b93 }
                    r3 = r2.descriptionLayout;	 Catch:{ Exception -> 0x2b93 }
                    r4 = r2.descriptionLayout;	 Catch:{ Exception -> 0x2b93 }
                    r4 = r4.getLineCount();	 Catch:{ Exception -> 0x2b93 }
                    r6 = 1;	 Catch:{ Exception -> 0x2b93 }
                    r4 = r4 - r6;	 Catch:{ Exception -> 0x2b93 }
                    r3 = r3.getLineBottom(r4);	 Catch:{ Exception -> 0x2b93 }
                    r4 = r2.linkPreviewHeight;	 Catch:{ Exception -> 0x2b93 }
                    r4 = r4 + r3;	 Catch:{ Exception -> 0x2b93 }
                    r2.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x2b93 }
                    r4 = r2.totalHeight;	 Catch:{ Exception -> 0x2b93 }
                    r4 = r4 + r3;	 Catch:{ Exception -> 0x2b93 }
                    r2.totalHeight = r4;	 Catch:{ Exception -> 0x2b93 }
                    r3 = 0;	 Catch:{ Exception -> 0x2b93 }
                L_0x2b69:
                    r4 = r2.descriptionLayout;	 Catch:{ Exception -> 0x2b93 }
                    r4 = r4.getLineCount();	 Catch:{ Exception -> 0x2b93 }
                    if (r3 >= r4) goto L_0x2b98;	 Catch:{ Exception -> 0x2b93 }
                L_0x2b71:
                    r4 = r2.descriptionLayout;	 Catch:{ Exception -> 0x2b93 }
                    r4 = r4.getLineLeft(r3);	 Catch:{ Exception -> 0x2b93 }
                    r6 = (double) r4;	 Catch:{ Exception -> 0x2b93 }
                    r6 = java.lang.Math.ceil(r6);	 Catch:{ Exception -> 0x2b93 }
                    r4 = (int) r6;	 Catch:{ Exception -> 0x2b93 }
                    if (r4 == 0) goto L_0x2b90;	 Catch:{ Exception -> 0x2b93 }
                L_0x2b7f:
                    r6 = r2.descriptionX;	 Catch:{ Exception -> 0x2b93 }
                    if (r6 != 0) goto L_0x2b87;	 Catch:{ Exception -> 0x2b93 }
                L_0x2b83:
                    r4 = -r4;	 Catch:{ Exception -> 0x2b93 }
                    r2.descriptionX = r4;	 Catch:{ Exception -> 0x2b93 }
                    goto L_0x2b90;	 Catch:{ Exception -> 0x2b93 }
                L_0x2b87:
                    r6 = r2.descriptionX;	 Catch:{ Exception -> 0x2b93 }
                    r4 = -r4;	 Catch:{ Exception -> 0x2b93 }
                    r4 = java.lang.Math.max(r6, r4);	 Catch:{ Exception -> 0x2b93 }
                    r2.descriptionX = r4;	 Catch:{ Exception -> 0x2b93 }
                L_0x2b90:
                    r3 = r3 + 1;
                    goto L_0x2b69;
                L_0x2b93:
                    r0 = move-exception;
                    r3 = r0;
                    org.telegram.messenger.FileLog.m3e(r3);
                L_0x2b98:
                    r3 = r2.totalHeight;
                    r4 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    r2.totalHeight = r3;
                    if (r5 == 0) goto L_0x2bbe;
                L_0x2ba5:
                    r3 = r2.totalHeight;
                    r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r2.totalHeight = r3;
                    r3 = 2;
                    if (r5 != r3) goto L_0x2bbe;
                L_0x2bb3:
                    r3 = r2.captionHeight;
                    r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r2.captionHeight = r3;
                L_0x2bbe:
                    r3 = r2.botButtons;
                    r3.clear();
                    if (r44 == 0) goto L_0x2bd2;
                L_0x2bc5:
                    r3 = r2.botButtonsByData;
                    r3.clear();
                    r3 = r2.botButtonsByPosition;
                    r3.clear();
                    r3 = 0;
                    r2.botButtonsLayout = r3;
                L_0x2bd2:
                    r3 = r2.currentPosition;
                    if (r3 != 0) goto L_0x2e00;
                L_0x2bd6:
                    r3 = r1.messageOwner;
                    r3 = r3.reply_markup;
                    r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
                    if (r3 == 0) goto L_0x2e00;
                L_0x2bde:
                    r3 = r1.messageOwner;
                    r3 = r3.reply_markup;
                    r3 = r3.rows;
                    r3 = r3.size();
                    r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r4 = r4 * r3;
                    r5 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r4 = r4 + r6;
                    r2.keyboardHeight = r4;
                    r2.substractBackgroundHeight = r4;
                    r4 = r2.backgroundWidth;
                    r2.widthForButtons = r4;
                    r4 = r1.wantedBotKeyboardWidth;
                    r5 = r2.widthForButtons;
                    if (r4 <= r5) goto L_0x2c47;
                L_0x2c04:
                    r4 = r2.isChat;
                    if (r4 == 0) goto L_0x2c17;
                L_0x2c08:
                    r4 = r69.needDrawAvatar();
                    if (r4 == 0) goto L_0x2c17;
                L_0x2c0e:
                    r4 = r69.isOutOwner();
                    if (r4 != 0) goto L_0x2c17;
                L_0x2c14:
                    r13 = NUM; // 0x42780000 float:62.0 double:5.5096253E-315;
                    goto L_0x2c19;
                L_0x2c17:
                    r13 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                L_0x2c19:
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r13);
                    r4 = -r4;
                    r5 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r5 == 0) goto L_0x2c2a;
                L_0x2c24:
                    r5 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                    r4 = r4 + r5;
                    goto L_0x2c37;
                L_0x2c2a:
                    r5 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r5 = r5.x;
                    r6 = org.telegram.messenger.AndroidUtilities.displaySize;
                    r6 = r6.y;
                    r5 = java.lang.Math.min(r5, r6);
                    r4 = r4 + r5;
                L_0x2c37:
                    r5 = r2.backgroundWidth;
                    r6 = r1.wantedBotKeyboardWidth;
                    r4 = java.lang.Math.min(r6, r4);
                    r4 = java.lang.Math.max(r5, r4);
                    r2.widthForButtons = r4;
                    r4 = 1;
                    goto L_0x2c48;
                L_0x2c47:
                    r4 = 0;
                L_0x2c48:
                    r5 = new java.util.HashMap;
                    r6 = r2.botButtonsByData;
                    r5.<init>(r6);
                    r6 = r1.botButtonsLayout;
                    if (r6 == 0) goto L_0x2c6d;
                L_0x2c53:
                    r6 = r2.botButtonsLayout;
                    if (r6 == 0) goto L_0x2c6d;
                L_0x2c57:
                    r6 = r2.botButtonsLayout;
                    r7 = r1.botButtonsLayout;
                    r7 = r7.toString();
                    r6 = r6.equals(r7);
                    if (r6 == 0) goto L_0x2c6d;
                L_0x2c65:
                    r7 = new java.util.HashMap;
                    r6 = r2.botButtonsByPosition;
                    r7.<init>(r6);
                    goto L_0x2c7a;
                L_0x2c6d:
                    r6 = r1.botButtonsLayout;
                    if (r6 == 0) goto L_0x2c79;
                L_0x2c71:
                    r6 = r1.botButtonsLayout;
                    r6 = r6.toString();
                    r2.botButtonsLayout = r6;
                L_0x2c79:
                    r7 = 0;
                L_0x2c7a:
                    r6 = r2.botButtonsByData;
                    r6.clear();
                    r6 = 0;
                    r8 = 0;
                L_0x2c81:
                    if (r6 >= r3) goto L_0x2dfd;
                L_0x2c83:
                    r9 = r1.messageOwner;
                    r9 = r9.reply_markup;
                    r9 = r9.rows;
                    r9 = r9.get(r6);
                    r9 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r9;
                    r10 = r9.buttons;
                    r10 = r10.size();
                    if (r10 != 0) goto L_0x2ca0;
                L_0x2c97:
                    r65 = r3;
                    r66 = r4;
                    r4 = 1;
                    r13 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    goto L_0x2df5;
                L_0x2ca0:
                    r11 = r2.widthForButtons;
                    r12 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
                    r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
                    r13 = r10 + -1;
                    r12 = r12 * r13;
                    r11 = r11 - r12;
                    if (r4 != 0) goto L_0x2cb4;
                L_0x2cae:
                    r12 = r2.mediaBackground;
                    if (r12 == 0) goto L_0x2cb4;
                L_0x2cb2:
                    r12 = 0;
                    goto L_0x2cb6;
                L_0x2cb4:
                    r12 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
                L_0x2cb6:
                    r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
                    r11 = r11 - r12;
                    r12 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r13 = org.telegram.messenger.AndroidUtilities.dp(r12);
                    r11 = r11 - r13;
                    r11 = r11 / r10;
                    r10 = r8;
                    r8 = 0;
                L_0x2cc5:
                    r12 = r9.buttons;
                    r12 = r12.size();
                    if (r8 >= r12) goto L_0x2ded;
                L_0x2ccd:
                    r12 = new org.telegram.ui.Cells.ChatMessageCell$BotButton;
                    r13 = 0;
                    r12.<init>();
                    r14 = r9.buttons;
                    r14 = r14.get(r8);
                    r14 = (org.telegram.tgnet.TLRPC.KeyboardButton) r14;
                    r12.button = r14;
                    r14 = r12.button;
                    r14 = r14.data;
                    r14 = org.telegram.messenger.Utilities.bytesToHex(r14);
                    r15 = new java.lang.StringBuilder;
                    r15.<init>();
                    r15.append(r6);
                    r13 = "";
                    r15.append(r13);
                    r15.append(r8);
                    r13 = r15.toString();
                    if (r7 == 0) goto L_0x2d05;
                L_0x2cfe:
                    r15 = r7.get(r13);
                    r15 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r15;
                    goto L_0x2d0b;
                L_0x2d05:
                    r15 = r5.get(r14);
                    r15 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r15;
                L_0x2d0b:
                    if (r15 == 0) goto L_0x2d27;
                L_0x2d0d:
                    r65 = r3;
                    r3 = r15.progressAlpha;
                    r12.progressAlpha = r3;
                    r3 = r15.angle;
                    r12.angle = r3;
                    r66 = r4;
                    r3 = r15.lastUpdateTime;
                    r12.lastUpdateTime = r3;
                    goto L_0x2d32;
                L_0x2d27:
                    r65 = r3;
                    r66 = r4;
                    r3 = java.lang.System.currentTimeMillis();
                    r12.lastUpdateTime = r3;
                L_0x2d32:
                    r3 = r2.botButtonsByData;
                    r3.put(r14, r12);
                    r3 = r2.botButtonsByPosition;
                    r3.put(r13, r12);
                    r3 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r3 = r3 + r11;
                    r3 = r3 * r8;
                    r12.f9x = r3;
                    r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r3 = r3 * r6;
                    r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    r12.f10y = r3;
                    r12.width = r11;
                    r3 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r12.height = r3;
                    r3 = r12.button;
                    r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
                    if (r3 == 0) goto L_0x2d84;
                L_0x2d6c:
                    r3 = r1.messageOwner;
                    r3 = r3.media;
                    r3 = r3.flags;
                    r3 = r3 & 4;
                    if (r3 == 0) goto L_0x2d84;
                L_0x2d76:
                    r3 = "PaymentReceipt";
                    r4 = NUM; // 0x7f0c04e3 float:1.861173E38 double:1.0530980165E-314;
                    r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
                    r22 = r3;
                    r13 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    goto L_0x2dae;
                L_0x2d84:
                    r3 = r12.button;
                    r3 = r3.text;
                    r4 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
                    r4 = r4.getFontMetricsInt();
                    r13 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
                    r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
                    r14 = 0;
                    r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r13, r14);
                    r4 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
                    r13 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r14 = org.telegram.messenger.AndroidUtilities.dp(r13);
                    r14 = r11 - r14;
                    r14 = (float) r14;
                    r15 = android.text.TextUtils.TruncateAt.END;
                    r3 = android.text.TextUtils.ellipsize(r3, r4, r14, r15);
                    r22 = r3;
                L_0x2dae:
                    r3 = new android.text.StaticLayout;
                    r23 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r13);
                    r24 = r11 - r4;
                    r25 = android.text.Layout.Alignment.ALIGN_CENTER;
                    r26 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r27 = 0;
                    r28 = 0;
                    r21 = r3;
                    r21.<init>(r22, r23, r24, r25, r26, r27, r28);
                    r12.title = r3;
                    r3 = r2.botButtons;
                    r3.add(r12);
                    r3 = r9.buttons;
                    r3 = r3.size();
                    r4 = 1;
                    r3 = r3 - r4;
                    if (r8 != r3) goto L_0x2de5;
                L_0x2dd7:
                    r3 = r12.f9x;
                    r12 = r12.width;
                    r3 = r3 + r12;
                    r3 = java.lang.Math.max(r10, r3);
                    r10 = r3;
                L_0x2de5:
                    r8 = r8 + 1;
                    r3 = r65;
                    r4 = r66;
                    goto L_0x2cc5;
                L_0x2ded:
                    r65 = r3;
                    r66 = r4;
                    r4 = 1;
                    r13 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r8 = r10;
                L_0x2df5:
                    r6 = r6 + 1;
                    r3 = r65;
                    r4 = r66;
                    goto L_0x2c81;
                L_0x2dfd:
                    r2.widthForButtons = r8;
                    goto L_0x2e05;
                L_0x2e00:
                    r3 = 0;
                    r2.substractBackgroundHeight = r3;
                    r2.keyboardHeight = r3;
                L_0x2e05:
                    r3 = r2.drawPinnedBottom;
                    if (r3 == 0) goto L_0x2e19;
                L_0x2e09:
                    r3 = r2.drawPinnedTop;
                    if (r3 == 0) goto L_0x2e19;
                L_0x2e0d:
                    r3 = r2.totalHeight;
                    r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r2.totalHeight = r3;
                    goto L_0x2e46;
                L_0x2e19:
                    r3 = r2.drawPinnedBottom;
                    if (r3 == 0) goto L_0x2e29;
                L_0x2e1d:
                    r3 = r2.totalHeight;
                    r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r2.totalHeight = r3;
                    goto L_0x2e46;
                L_0x2e29:
                    r3 = r2.drawPinnedTop;
                    if (r3 == 0) goto L_0x2e46;
                L_0x2e2d:
                    r3 = r2.pinnedBottom;
                    if (r3 == 0) goto L_0x2e46;
                L_0x2e31:
                    r3 = r2.currentPosition;
                    if (r3 == 0) goto L_0x2e46;
                L_0x2e35:
                    r3 = r2.currentPosition;
                    r3 = r3.siblingHeights;
                    if (r3 != 0) goto L_0x2e46;
                L_0x2e3b:
                    r3 = r2.totalHeight;
                    r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r2.totalHeight = r3;
                L_0x2e46:
                    r1 = r1.type;
                    r3 = 13;
                    if (r1 != r3) goto L_0x2e5e;
                L_0x2e4c:
                    r1 = r2.totalHeight;
                    r3 = NUM; // 0x428c0000 float:70.0 double:5.51610112E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    if (r1 >= r3) goto L_0x2e5e;
                L_0x2e56:
                    r1 = NUM; // 0x428c0000 float:70.0 double:5.51610112E-315;
                    r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                    r2.totalHeight = r1;
                L_0x2e5e:
                    r68.updateWaveform();
                    r9 = r43;
                    r2.updateButtonState(r9);
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageObject(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, boolean, boolean):void");
                }

                private int getAdditionalWidthForPosition(GroupedMessagePosition groupedMessagePosition) {
                    int i = 0;
                    if (groupedMessagePosition == null) {
                        return 0;
                    }
                    if ((groupedMessagePosition.flags & 2) == 0) {
                        i = 0 + AndroidUtilities.dp(4.0f);
                    }
                    return (groupedMessagePosition.flags & 1) == null ? i + AndroidUtilities.dp(4.0f) : i;
                }

                public void requestLayout() {
                    if (!this.inLayout) {
                        super.requestLayout();
                    }
                }

                protected void onMeasure(int i, int i2) {
                    if (!(this.currentMessageObject == 0 || (this.currentMessageObject.checkLayout() == 0 && (this.currentPosition == 0 || this.lastHeight == AndroidUtilities.displaySize.y)))) {
                        this.inLayout = true;
                        i2 = this.currentMessageObject;
                        this.currentMessageObject = null;
                        setMessageObject(i2, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
                        this.inLayout = false;
                    }
                    setMeasuredDimension(MeasureSpec.getSize(i), this.totalHeight + this.keyboardHeight);
                    this.lastHeight = AndroidUtilities.displaySize.y;
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

                @SuppressLint({"DrawAllocation"})
                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    if (this.currentMessageObject != null) {
                        if (z || !r0.wasLayout) {
                            r0.layoutWidth = getMeasuredWidth();
                            r0.layoutHeight = getMeasuredHeight() - r0.substractBackgroundHeight;
                            if (r0.timeTextWidth < 0) {
                                r0.timeTextWidth = AndroidUtilities.dp(10.0f);
                            }
                            r0.timeLayout = new StaticLayout(r0.currentTimeString, Theme.chat_timePaint, AndroidUtilities.dp(100.0f) + r0.timeTextWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            if (r0.mediaBackground) {
                                if (r0.currentMessageObject.isOutOwner()) {
                                    r0.timeX = (r0.layoutWidth - r0.timeWidth) - AndroidUtilities.dp(42.0f);
                                } else {
                                    r0.timeX = ((r0.backgroundWidth - AndroidUtilities.dp(4.0f)) - r0.timeWidth) + (r0.isAvatarVisible ? AndroidUtilities.dp(48.0f) : 0);
                                    if (!(r0.currentPosition == null || r0.currentPosition.leftSpanOffset == 0)) {
                                        r0.timeX += (int) Math.ceil((double) ((((float) r0.currentPosition.leftSpanOffset) / 1000.0f) * ((float) getGroupPhotosWidth())));
                                    }
                                }
                            } else if (r0.currentMessageObject.isOutOwner()) {
                                r0.timeX = (r0.layoutWidth - r0.timeWidth) - AndroidUtilities.dp(38.5f);
                            } else {
                                r0.timeX = ((r0.backgroundWidth - AndroidUtilities.dp(9.0f)) - r0.timeWidth) + (r0.isAvatarVisible ? AndroidUtilities.dp(48.0f) : 0);
                            }
                            if ((r0.currentMessageObject.messageOwner.flags & 1024) != 0) {
                                r0.viewsLayout = new StaticLayout(r0.currentViewsString, Theme.chat_timePaint, r0.viewsTextWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            } else {
                                r0.viewsLayout = null;
                            }
                            if (r0.isAvatarVisible) {
                                r0.avatarImage.setImageCoords(AndroidUtilities.dp(6.0f), r0.avatarImage.getImageY(), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
                            }
                            r0.wasLayout = true;
                        }
                        if (r0.currentMessageObject.type == 0) {
                            r0.textY = AndroidUtilities.dp(10.0f) + r0.namesOffset;
                        }
                        if (r0.currentMessageObject.isRoundVideo()) {
                            updatePlayingMessageProgress();
                        }
                        int i5 = 10;
                        SeekBar seekBar;
                        int i6;
                        if (r0.documentAttachType == 3) {
                            if (r0.currentMessageObject.isOutOwner()) {
                                r0.seekBarX = (r0.layoutWidth - r0.backgroundWidth) + AndroidUtilities.dp(57.0f);
                                r0.buttonX = (r0.layoutWidth - r0.backgroundWidth) + AndroidUtilities.dp(14.0f);
                                r0.timeAudioX = (r0.layoutWidth - r0.backgroundWidth) + AndroidUtilities.dp(67.0f);
                            } else if (r0.isChat && r0.currentMessageObject.needDrawAvatar()) {
                                r0.seekBarX = AndroidUtilities.dp(114.0f);
                                r0.buttonX = AndroidUtilities.dp(71.0f);
                                r0.timeAudioX = AndroidUtilities.dp(124.0f);
                            } else {
                                r0.seekBarX = AndroidUtilities.dp(66.0f);
                                r0.buttonX = AndroidUtilities.dp(23.0f);
                                r0.timeAudioX = AndroidUtilities.dp(76.0f);
                            }
                            if (r0.hasLinkPreview) {
                                r0.seekBarX += AndroidUtilities.dp(10.0f);
                                r0.buttonX += AndroidUtilities.dp(10.0f);
                                r0.timeAudioX += AndroidUtilities.dp(10.0f);
                            }
                            r0.seekBarWaveform.setSize(r0.backgroundWidth - AndroidUtilities.dp((float) (92 + (r0.hasLinkPreview ? 10 : 0))), AndroidUtilities.dp(30.0f));
                            seekBar = r0.seekBar;
                            i6 = r0.backgroundWidth;
                            if (!r0.hasLinkPreview) {
                                i5 = 0;
                            }
                            seekBar.setSize(i6 - AndroidUtilities.dp((float) (72 + i5)), AndroidUtilities.dp(30.0f));
                            r0.seekBarY = (AndroidUtilities.dp(13.0f) + r0.namesOffset) + r0.mediaOffsetY;
                            r0.buttonY = (AndroidUtilities.dp(13.0f) + r0.namesOffset) + r0.mediaOffsetY;
                            r0.radialProgress.setProgressRect(r0.buttonX, r0.buttonY, r0.buttonX + AndroidUtilities.dp(44.0f), r0.buttonY + AndroidUtilities.dp(44.0f));
                            updatePlayingMessageProgress();
                        } else if (r0.documentAttachType == 5) {
                            if (r0.currentMessageObject.isOutOwner()) {
                                r0.seekBarX = (r0.layoutWidth - r0.backgroundWidth) + AndroidUtilities.dp(56.0f);
                                r0.buttonX = (r0.layoutWidth - r0.backgroundWidth) + AndroidUtilities.dp(14.0f);
                                r0.timeAudioX = (r0.layoutWidth - r0.backgroundWidth) + AndroidUtilities.dp(67.0f);
                            } else if (r0.isChat && r0.currentMessageObject.needDrawAvatar()) {
                                r0.seekBarX = AndroidUtilities.dp(113.0f);
                                r0.buttonX = AndroidUtilities.dp(71.0f);
                                r0.timeAudioX = AndroidUtilities.dp(124.0f);
                            } else {
                                r0.seekBarX = AndroidUtilities.dp(65.0f);
                                r0.buttonX = AndroidUtilities.dp(23.0f);
                                r0.timeAudioX = AndroidUtilities.dp(76.0f);
                            }
                            if (r0.hasLinkPreview) {
                                r0.seekBarX += AndroidUtilities.dp(10.0f);
                                r0.buttonX += AndroidUtilities.dp(10.0f);
                                r0.timeAudioX += AndroidUtilities.dp(10.0f);
                            }
                            seekBar = r0.seekBar;
                            i6 = r0.backgroundWidth;
                            if (!r0.hasLinkPreview) {
                                i5 = 0;
                            }
                            seekBar.setSize(i6 - AndroidUtilities.dp((float) (65 + i5)), AndroidUtilities.dp(30.0f));
                            r0.seekBarY = (AndroidUtilities.dp(29.0f) + r0.namesOffset) + r0.mediaOffsetY;
                            r0.buttonY = (AndroidUtilities.dp(13.0f) + r0.namesOffset) + r0.mediaOffsetY;
                            r0.radialProgress.setProgressRect(r0.buttonX, r0.buttonY, r0.buttonX + AndroidUtilities.dp(44.0f), r0.buttonY + AndroidUtilities.dp(44.0f));
                            updatePlayingMessageProgress();
                        } else if (r0.documentAttachType == 1 && !r0.drawPhotoImage) {
                            if (r0.currentMessageObject.isOutOwner()) {
                                r0.buttonX = (r0.layoutWidth - r0.backgroundWidth) + AndroidUtilities.dp(14.0f);
                            } else if (r0.isChat && r0.currentMessageObject.needDrawAvatar()) {
                                r0.buttonX = AndroidUtilities.dp(71.0f);
                            } else {
                                r0.buttonX = AndroidUtilities.dp(23.0f);
                            }
                            if (r0.hasLinkPreview) {
                                r0.buttonX += AndroidUtilities.dp(10.0f);
                            }
                            r0.buttonY = (AndroidUtilities.dp(13.0f) + r0.namesOffset) + r0.mediaOffsetY;
                            r0.radialProgress.setProgressRect(r0.buttonX, r0.buttonY, r0.buttonX + AndroidUtilities.dp(44.0f), r0.buttonY + AndroidUtilities.dp(44.0f));
                            r0.photoImage.setImageCoords(r0.buttonX - AndroidUtilities.dp(10.0f), r0.buttonY - AndroidUtilities.dp(10.0f), r0.photoImage.getImageWidth(), r0.photoImage.getImageHeight());
                        } else if (r0.currentMessageObject.type == 12) {
                            int dp;
                            if (r0.currentMessageObject.isOutOwner()) {
                                dp = (r0.layoutWidth - r0.backgroundWidth) + AndroidUtilities.dp(14.0f);
                            } else if (r0.isChat && r0.currentMessageObject.needDrawAvatar()) {
                                dp = AndroidUtilities.dp(72.0f);
                            } else {
                                dp = AndroidUtilities.dp(23.0f);
                            }
                            r0.photoImage.setImageCoords(dp, AndroidUtilities.dp(13.0f) + r0.namesOffset, AndroidUtilities.dp(44.0f), AndroidUtilities.dp(44.0f));
                        } else {
                            int dp2;
                            if (r0.currentMessageObject.type == 0 && (r0.hasLinkPreview || r0.hasGamePreview || r0.hasInvoicePreview)) {
                                if (r0.hasGamePreview) {
                                    dp2 = r0.unmovedTextX - AndroidUtilities.dp(10.0f);
                                } else if (r0.hasInvoicePreview) {
                                    dp2 = r0.unmovedTextX + AndroidUtilities.dp(1.0f);
                                } else {
                                    dp2 = r0.unmovedTextX + AndroidUtilities.dp(1.0f);
                                }
                                if (r0.isSmallImage) {
                                    dp2 = (dp2 + r0.backgroundWidth) - AndroidUtilities.dp(81.0f);
                                } else {
                                    dp2 += r0.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f);
                                }
                            } else if (!r0.currentMessageObject.isOutOwner()) {
                                if (r0.isChat && r0.isAvatarVisible) {
                                    dp2 = AndroidUtilities.dp(63.0f);
                                } else {
                                    dp2 = AndroidUtilities.dp(15.0f);
                                }
                                if (!(r0.currentPosition == null || r0.currentPosition.edge)) {
                                    dp2 -= AndroidUtilities.dp(10.0f);
                                }
                            } else if (r0.mediaBackground) {
                                dp2 = (r0.layoutWidth - r0.backgroundWidth) - AndroidUtilities.dp(3.0f);
                            } else {
                                dp2 = (r0.layoutWidth - r0.backgroundWidth) + AndroidUtilities.dp(6.0f);
                            }
                            if (r0.currentPosition != null) {
                                if ((1 & r0.currentPosition.flags) == 0) {
                                    dp2 -= AndroidUtilities.dp(4.0f);
                                }
                                if (r0.currentPosition.leftSpanOffset != 0) {
                                    dp2 += (int) Math.ceil((double) ((((float) r0.currentPosition.leftSpanOffset) / 1000.0f) * ((float) getGroupPhotosWidth())));
                                }
                            }
                            r0.photoImage.setImageCoords(dp2, r0.photoImage.getImageY(), r0.photoImage.getImageWidth(), r0.photoImage.getImageHeight());
                            r0.buttonX = (int) (((float) dp2) + (((float) (r0.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f))) / 2.0f));
                            r0.buttonY = r0.photoImage.getImageY() + ((r0.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f)) / 2);
                            r0.radialProgress.setProgressRect(r0.buttonX, r0.buttonY, r0.buttonX + AndroidUtilities.dp(48.0f), r0.buttonY + AndroidUtilities.dp(48.0f));
                            r0.deleteProgressRect.set((float) (r0.buttonX + AndroidUtilities.dp(3.0f)), (float) (r0.buttonY + AndroidUtilities.dp(3.0f)), (float) (r0.buttonX + AndroidUtilities.dp(45.0f)), (float) (r0.buttonY + AndroidUtilities.dp(45.0f)));
                        }
                    }
                }

                public boolean needDelayRoundProgressDraw() {
                    return this.documentAttachType == 7 && this.currentMessageObject.type != 5 && MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                }

                public void drawRoundProgress(Canvas canvas) {
                    this.rect.set(((float) this.photoImage.getImageX()) + AndroidUtilities.dpf2(1.5f), ((float) this.photoImage.getImageY()) + AndroidUtilities.dpf2(1.5f), ((float) this.photoImage.getImageX2()) - AndroidUtilities.dpf2(1.5f), ((float) this.photoImage.getImageY2()) - AndroidUtilities.dpf2(1.5f));
                    canvas.drawArc(this.rect, -90.0f, 360.0f * this.currentMessageObject.audioProgress, false, Theme.chat_radialProgressPaint);
                }

                private void drawContent(Canvas canvas) {
                    int i;
                    int i2;
                    boolean z;
                    boolean z2;
                    float f;
                    int i3;
                    int i4;
                    Drawable drawable;
                    float max;
                    Drawable drawable2;
                    float abs;
                    String formatLocationLeftTime;
                    Drawable drawable3;
                    PhoneCallDiscardReason phoneCallDiscardReason;
                    float f2;
                    boolean z3;
                    int i5;
                    int dp;
                    RadialProgress radialProgress;
                    String str;
                    int i6;
                    RadialProgress radialProgress2;
                    Drawable[] drawableArr;
                    BotButton botButton;
                    boolean z4;
                    long currentTimeMillis;
                    long access$1300;
                    float f3;
                    Canvas canvas2 = canvas;
                    if (this.needNewVisiblePart && r1.currentMessageObject.type == 0) {
                        getLocalVisibleRect(r1.scrollRect);
                        setVisiblePart(r1.scrollRect.top, r1.scrollRect.bottom - r1.scrollRect.top);
                        r1.needNewVisiblePart = false;
                    }
                    r1.forceNotDrawTime = r1.currentMessagesGroup != null;
                    ImageReceiver imageReceiver = r1.photoImage;
                    int i7 = isDrawSelectedBackground() ? r1.currentPosition != null ? 2 : 1 : 0;
                    imageReceiver.setPressed(i7);
                    imageReceiver = r1.photoImage;
                    boolean z5 = (PhotoViewer.isShowingImage(r1.currentMessageObject) || SecretMediaViewer.getInstance().isShowingImage(r1.currentMessageObject)) ? false : true;
                    imageReceiver.setVisible(z5, false);
                    if (!r1.photoImage.getVisible()) {
                        r1.mediaWasInvisible = true;
                        r1.timeWasInvisible = true;
                    } else if (r1.groupPhotoInvisible) {
                        r1.timeWasInvisible = true;
                    } else if (r1.mediaWasInvisible || r1.timeWasInvisible) {
                        if (r1.mediaWasInvisible) {
                            r1.controlsAlpha = 0.0f;
                            r1.mediaWasInvisible = false;
                        }
                        if (r1.timeWasInvisible) {
                            r1.timeAlpha = 0.0f;
                            r1.timeWasInvisible = false;
                        }
                        r1.lastControlsAlphaChangeTime = System.currentTimeMillis();
                        r1.totalChangeTime = 0;
                    }
                    r1.radialProgress.setHideCurrentDrawable(false);
                    r1.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
                    if (r1.currentMessageObject.type == 0) {
                        int dp2;
                        float f4;
                        float f5;
                        boolean draw;
                        if (r1.currentMessageObject.isOutOwner()) {
                            r1.textX = r1.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
                        } else {
                            i = r1.currentBackgroundDrawable.getBounds().left;
                            float f6 = (r1.mediaBackground || !r1.drawPinnedBottom) ? 17.0f : 11.0f;
                            r1.textX = i + AndroidUtilities.dp(f6);
                        }
                        if (r1.hasGamePreview) {
                            r1.textX += AndroidUtilities.dp(11.0f);
                            r1.textY = AndroidUtilities.dp(14.0f) + r1.namesOffset;
                            if (r1.siteNameLayout != null) {
                                r1.textY += r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1);
                            }
                        } else if (r1.hasInvoicePreview) {
                            r1.textY = AndroidUtilities.dp(14.0f) + r1.namesOffset;
                            if (r1.siteNameLayout != null) {
                                r1.textY += r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1);
                            }
                        } else {
                            r1.textY = AndroidUtilities.dp(10.0f) + r1.namesOffset;
                        }
                        r1.unmovedTextX = r1.textX;
                        if (!(r1.currentMessageObject.textXOffset == 0.0f || r1.replyNameLayout == null)) {
                            i = (r1.backgroundWidth - AndroidUtilities.dp(31.0f)) - r1.currentMessageObject.textWidth;
                            if (!r1.hasNewLineForTime) {
                                i -= r1.timeWidth + AndroidUtilities.dp((float) ((r1.currentMessageObject.isOutOwner() ? 20 : 0) + 4));
                            }
                            if (i > 0) {
                                r1.textX += i;
                            }
                        }
                        if (r1.currentMessageObject.textLayoutBlocks != null && !r1.currentMessageObject.textLayoutBlocks.isEmpty()) {
                            if (r1.fullyDraw) {
                                r1.firstVisibleBlockNum = 0;
                                r1.lastVisibleBlockNum = r1.currentMessageObject.textLayoutBlocks.size();
                            }
                            if (r1.firstVisibleBlockNum >= 0) {
                                i = r1.firstVisibleBlockNum;
                                while (i <= r1.lastVisibleBlockNum) {
                                    if (i >= r1.currentMessageObject.textLayoutBlocks.size()) {
                                        break;
                                    }
                                    TextLayoutBlock textLayoutBlock = (TextLayoutBlock) r1.currentMessageObject.textLayoutBlocks.get(i);
                                    canvas.save();
                                    canvas2.translate((float) (r1.textX - (textLayoutBlock.isRtl() ? (int) Math.ceil((double) r1.currentMessageObject.textXOffset) : 0)), ((float) r1.textY) + textLayoutBlock.textYOffset);
                                    if (r1.pressedLink != null && i == r1.linkBlockNum) {
                                        for (i2 = 0; i2 < r1.urlPath.size(); i2++) {
                                            canvas2.drawPath((Path) r1.urlPath.get(i2), Theme.chat_urlPaint);
                                        }
                                    }
                                    if (i == r1.linkSelectionBlockNum && !r1.urlPathSelection.isEmpty()) {
                                        for (i2 = 0; i2 < r1.urlPathSelection.size(); i2++) {
                                            canvas2.drawPath((Path) r1.urlPathSelection.get(i2), Theme.chat_textSearchSelectionPaint);
                                        }
                                    }
                                    try {
                                        textLayoutBlock.textLayout.draw(canvas2);
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    canvas.restore();
                                    i++;
                                }
                            }
                        }
                        if (!(r1.hasLinkPreview || r1.hasGamePreview)) {
                            if (!r1.hasInvoicePreview) {
                                z = true;
                                z2 = false;
                                f = 6.0f;
                                r1.drawTime = z;
                            }
                        }
                        if (r1.hasGamePreview) {
                            i = AndroidUtilities.dp(14.0f) + r1.namesOffset;
                            dp2 = r1.unmovedTextX - AndroidUtilities.dp(10.0f);
                        } else if (r1.hasInvoicePreview) {
                            i = AndroidUtilities.dp(14.0f) + r1.namesOffset;
                            dp2 = r1.unmovedTextX + AndroidUtilities.dp(1.0f);
                        } else {
                            i = (r1.textY + r1.currentMessageObject.textHeight) + AndroidUtilities.dp(8.0f);
                            dp2 = r1.unmovedTextX + AndroidUtilities.dp(1.0f);
                        }
                        i2 = i;
                        i3 = dp2;
                        if (r1.hasInvoicePreview) {
                            f4 = 2.0f;
                            f5 = 3.0f;
                            f = 6.0f;
                        } else {
                            Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                            f4 = 2.0f;
                            f = 1.0f;
                            f5 = 8.0f;
                            f5 = 3.0f;
                            f = 6.0f;
                            canvas2.drawRect((float) i3, (float) (i2 - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i3), (float) ((r1.linkPreviewHeight + i2) + AndroidUtilities.dp(3.0f)), Theme.chat_replyLinePaint);
                        }
                        if (r1.siteNameLayout != null) {
                            Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                            canvas.save();
                            i = r1.siteNameRtl ? (r1.backgroundWidth - r1.siteNameWidth) - AndroidUtilities.dp(32.0f) : r1.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f);
                            canvas2.translate((float) (i + i3), (float) (i2 - AndroidUtilities.dp(f5)));
                            r1.siteNameLayout.draw(canvas2);
                            canvas.restore();
                            i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + i2;
                        } else {
                            i = i2;
                        }
                        if ((r1.hasGamePreview || r1.hasInvoicePreview) && r1.currentMessageObject.textHeight != 0) {
                            i2 += r1.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
                            i += r1.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
                        }
                        if (r1.drawPhotoImage && r1.drawInstantView) {
                            if (i != i2) {
                                i += AndroidUtilities.dp(f4);
                            }
                            r1.photoImage.setImageCoords(AndroidUtilities.dp(10.0f) + i3, i, r1.photoImage.getImageWidth(), r1.photoImage.getImageHeight());
                            if (r1.drawImageButton) {
                                i7 = AndroidUtilities.dp(48.0f);
                                r1.buttonX = (int) (((float) r1.photoImage.getImageX()) + (((float) (r1.photoImage.getImageWidth() - i7)) / f4));
                                r1.buttonY = (int) (((float) r1.photoImage.getImageY()) + (((float) (r1.photoImage.getImageHeight() - i7)) / f4));
                                r1.radialProgress.setProgressRect(r1.buttonX, r1.buttonY, r1.buttonX + i7, r1.buttonY + i7);
                            }
                            i += r1.photoImage.getImageHeight() + AndroidUtilities.dp(f);
                            draw = r1.photoImage.draw(canvas2);
                        } else {
                            draw = false;
                        }
                        if (r1.currentMessageObject.isOutOwner()) {
                            Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                        } else {
                            Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                        }
                        if (r1.titleLayout != null) {
                            if (i != i2) {
                                i += AndroidUtilities.dp(f4);
                            }
                            i7 = i - AndroidUtilities.dp(1.0f);
                            canvas.save();
                            canvas2.translate((float) ((AndroidUtilities.dp(10.0f) + i3) + r1.titleX), (float) (i - AndroidUtilities.dp(f5)));
                            r1.titleLayout.draw(canvas2);
                            canvas.restore();
                            i += r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                        } else {
                            i7 = 0;
                        }
                        if (r1.authorLayout != null) {
                            if (i != i2) {
                                i += AndroidUtilities.dp(f4);
                            }
                            if (i7 == 0) {
                                i7 = i - AndroidUtilities.dp(1.0f);
                            }
                            canvas.save();
                            canvas2.translate((float) ((AndroidUtilities.dp(10.0f) + i3) + r1.authorX), (float) (i - AndroidUtilities.dp(f5)));
                            r1.authorLayout.draw(canvas2);
                            canvas.restore();
                            i += r1.authorLayout.getLineBottom(r1.authorLayout.getLineCount() - 1);
                        }
                        if (r1.descriptionLayout != null) {
                            if (i != i2) {
                                i += AndroidUtilities.dp(f4);
                            }
                            if (i7 == 0) {
                                i7 = i - AndroidUtilities.dp(1.0f);
                            }
                            r1.descriptionY = i - AndroidUtilities.dp(f5);
                            canvas.save();
                            canvas2.translate((float) (((r1.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f)) + i3) + r1.descriptionX), (float) r1.descriptionY);
                            if (r1.pressedLink != null && r1.linkBlockNum == -10) {
                                for (i4 = 0; i4 < r1.urlPath.size(); i4++) {
                                    canvas2.drawPath((Path) r1.urlPath.get(i4), Theme.chat_urlPaint);
                                }
                            }
                            r1.descriptionLayout.draw(canvas2);
                            canvas.restore();
                            i += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                        }
                        if (r1.drawPhotoImage && !r1.drawInstantView) {
                            if (i != i2) {
                                i += AndroidUtilities.dp(f4);
                            }
                            if (r1.isSmallImage) {
                                r1.photoImage.setImageCoords((r1.backgroundWidth + i3) - AndroidUtilities.dp(81.0f), i7, r1.photoImage.getImageWidth(), r1.photoImage.getImageHeight());
                            } else {
                                r1.photoImage.setImageCoords((r1.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f)) + i3, i, r1.photoImage.getImageWidth(), r1.photoImage.getImageHeight());
                                if (r1.drawImageButton) {
                                    i7 = AndroidUtilities.dp(48.0f);
                                    r1.buttonX = (int) (((float) r1.photoImage.getImageX()) + (((float) (r1.photoImage.getImageWidth() - i7)) / f4));
                                    r1.buttonY = (int) (((float) r1.photoImage.getImageY()) + (((float) (r1.photoImage.getImageHeight() - i7)) / f4));
                                    r1.radialProgress.setProgressRect(r1.buttonX, r1.buttonY, r1.buttonX + i7, r1.buttonY + i7);
                                }
                            }
                            if (r1.currentMessageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(r1.currentMessageObject) && MediaController.getInstance().isRoundVideoDrawingReady()) {
                                r1.drawTime = true;
                                draw = true;
                            } else {
                                draw = r1.photoImage.draw(canvas2);
                            }
                        }
                        if (r1.photosCountLayout != null && r1.photoImage.getVisible()) {
                            i7 = ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - r1.photosCountWidth;
                            i4 = (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f);
                            r1.rect.set((float) (i7 - AndroidUtilities.dp(4.0f)), (float) (i4 - AndroidUtilities.dp(1.5f)), (float) ((r1.photosCountWidth + i7) + AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(14.5f) + i4));
                            int alpha = Theme.chat_timeBackgroundPaint.getAlpha();
                            Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) alpha) * r1.controlsAlpha));
                            Theme.chat_durationPaint.setAlpha((int) (255.0f * r1.controlsAlpha));
                            canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                            Theme.chat_timeBackgroundPaint.setAlpha(alpha);
                            canvas.save();
                            canvas2.translate((float) i7, (float) i4);
                            r1.photosCountLayout.draw(canvas2);
                            canvas.restore();
                            Theme.chat_durationPaint.setAlpha(255);
                        }
                        if (r1.videoInfoLayout != null && (!r1.drawPhotoImage || r1.photoImage.getVisible())) {
                            if (!r1.hasGamePreview) {
                                if (!r1.hasInvoicePreview) {
                                    i = ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - r1.durationWidth;
                                    i7 = (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f);
                                    r1.rect.set((float) (i - AndroidUtilities.dp(4.0f)), (float) (i7 - AndroidUtilities.dp(1.5f)), (float) ((r1.durationWidth + i) + AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(14.5f) + i7));
                                    canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                                    canvas.save();
                                    canvas2.translate((float) i, (float) i7);
                                    if (r1.hasInvoicePreview) {
                                        if (r1.drawPhotoImage) {
                                            Theme.chat_shipmentPaint.setColor(Theme.getColor(Theme.key_chat_previewGameText));
                                        } else if (r1.currentMessageObject.isOutOwner()) {
                                            Theme.chat_shipmentPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                        } else {
                                            Theme.chat_shipmentPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                        }
                                    }
                                    r1.videoInfoLayout.draw(canvas2);
                                    canvas.restore();
                                }
                            }
                            if (r1.drawPhotoImage) {
                                i = r1.photoImage.getImageX() + AndroidUtilities.dp(8.5f);
                                i7 = r1.photoImage.getImageY() + AndroidUtilities.dp(f);
                                r1.rect.set((float) (i - AndroidUtilities.dp(4.0f)), (float) (i7 - AndroidUtilities.dp(1.5f)), (float) ((r1.durationWidth + i) + AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(16.5f) + i7));
                                canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                            } else {
                                i7 = i;
                                i = i3;
                            }
                            canvas.save();
                            canvas2.translate((float) i, (float) i7);
                            if (r1.hasInvoicePreview) {
                                if (r1.drawPhotoImage) {
                                    Theme.chat_shipmentPaint.setColor(Theme.getColor(Theme.key_chat_previewGameText));
                                } else if (r1.currentMessageObject.isOutOwner()) {
                                    Theme.chat_shipmentPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                } else {
                                    Theme.chat_shipmentPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                }
                            }
                            r1.videoInfoLayout.draw(canvas2);
                            canvas.restore();
                        }
                        if (r1.drawInstantView) {
                            i2 = (i2 + r1.linkPreviewHeight) + AndroidUtilities.dp(10.0f);
                            Paint paint = Theme.chat_instantViewRectPaint;
                            if (r1.currentMessageObject.isOutOwner()) {
                                drawable = Theme.chat_msgOutInstantDrawable;
                                Theme.chat_instantViewPaint.setColor(Theme.getColor(Theme.key_chat_outPreviewInstantText));
                                paint.setColor(Theme.getColor(Theme.key_chat_outPreviewInstantText));
                            } else {
                                drawable = Theme.chat_msgInInstantDrawable;
                                Theme.chat_instantViewPaint.setColor(Theme.getColor(Theme.key_chat_inPreviewInstantText));
                                paint.setColor(Theme.getColor(Theme.key_chat_inPreviewInstantText));
                            }
                            if (VERSION.SDK_INT >= 21) {
                                r1.instantViewSelectorDrawable.setBounds(i3, i2, r1.instantWidth + i3, AndroidUtilities.dp(36.0f) + i2);
                                r1.instantViewSelectorDrawable.draw(canvas2);
                            }
                            r1.rect.set((float) i3, (float) i2, (float) (r1.instantWidth + i3), (float) (AndroidUtilities.dp(36.0f) + i2));
                            canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(f), (float) AndroidUtilities.dp(f), paint);
                            if (r1.drawInstantViewType == 0) {
                                BaseCell.setDrawableBounds(drawable, ((r1.instantTextLeftX + r1.instantTextX) + i3) - AndroidUtilities.dp(15.0f), AndroidUtilities.dp(11.5f) + i2, AndroidUtilities.dp(9.0f), AndroidUtilities.dp(13.0f));
                                drawable.draw(canvas2);
                            }
                            if (r1.instantViewLayout != null) {
                                canvas.save();
                                canvas2.translate((float) (i3 + r1.instantTextX), (float) (i2 + AndroidUtilities.dp(10.5f)));
                                r1.instantViewLayout.draw(canvas2);
                                canvas.restore();
                            }
                        }
                        z2 = draw;
                        z = true;
                        r1.drawTime = z;
                    } else {
                        f = 6.0f;
                        if (!r1.drawPhotoImage) {
                            z2 = false;
                        } else if (r1.currentMessageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(r1.currentMessageObject) && MediaController.getInstance().isRoundVideoDrawingReady()) {
                            r1.drawTime = true;
                            z2 = true;
                        } else {
                            if (r1.currentMessageObject.type == 5 && Theme.chat_roundVideoShadow != null) {
                                i = r1.photoImage.getImageX() - AndroidUtilities.dp(3.0f);
                                i7 = r1.photoImage.getImageY() - AndroidUtilities.dp(2.0f);
                                Theme.chat_roundVideoShadow.setAlpha((int) (r1.photoImage.getCurrentAlpha() * 255.0f));
                                Theme.chat_roundVideoShadow.setBounds(i, i7, (AndroidUtilities.roundMessageSize + i) + AndroidUtilities.dp(6.0f), (AndroidUtilities.roundMessageSize + i7) + AndroidUtilities.dp(6.0f));
                                Theme.chat_roundVideoShadow.draw(canvas2);
                            }
                            z2 = r1.photoImage.draw(canvas2);
                            z = r1.drawTime;
                            r1.drawTime = r1.photoImage.getVisible();
                            if (!(r1.currentPosition == null || z == r1.drawTime)) {
                                ViewGroup viewGroup = (ViewGroup) getParent();
                                if (viewGroup != null) {
                                    if (r1.currentPosition.last) {
                                        viewGroup.invalidate();
                                    } else {
                                        i7 = viewGroup.getChildCount();
                                        for (i4 = 0; i4 < i7; i4++) {
                                            View childAt = viewGroup.getChildAt(i4);
                                            if (childAt != r1) {
                                                if (childAt instanceof ChatMessageCell) {
                                                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                                                    if (chatMessageCell.getCurrentMessagesGroup() == r1.currentMessagesGroup) {
                                                        GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                                                        if (currentPosition.last && currentPosition.maxY == r1.currentPosition.maxY && (chatMessageCell.timeX - AndroidUtilities.dp(4.0f)) + chatMessageCell.getLeft() < getRight()) {
                                                            chatMessageCell.groupPhotoInvisible = r1.drawTime ^ true;
                                                            chatMessageCell.invalidate();
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
                    }
                    if (r1.buttonState == -1 && r1.currentMessageObject.needDrawBluredPreview() && !MediaController.getInstance().isPlayingMessage(r1.currentMessageObject) && r1.photoImage.getVisible()) {
                        int i8 = r1.currentMessageObject.messageOwner.destroyTime != 0 ? r1.currentMessageObject.isOutOwner() ? 6 : 5 : 4;
                        BaseCell.setDrawableBounds(Theme.chat_photoStatesDrawables[i8][r1.buttonPressed], r1.buttonX, r1.buttonY);
                        Theme.chat_photoStatesDrawables[i8][r1.buttonPressed].setAlpha((int) ((255.0f * (1.0f - r1.radialProgress.getAlpha())) * r1.controlsAlpha));
                        Theme.chat_photoStatesDrawables[i8][r1.buttonPressed].draw(canvas2);
                        if (r1.currentMessageObject.messageOwner.destroyTime != 0) {
                            if (!r1.currentMessageObject.isOutOwner()) {
                                max = ((float) Math.max(0, (((long) r1.currentMessageObject.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance(r1.currentAccount).getTimeDifference() * 1000))))) / (((float) r1.currentMessageObject.messageOwner.ttl) * 1000.0f);
                                Theme.chat_deleteProgressPaint.setAlpha((int) (255.0f * r1.controlsAlpha));
                                canvas2.drawArc(r1.deleteProgressRect, -90.0f, -360.0f * max, true, Theme.chat_deleteProgressPaint);
                                if (max != 0.0f) {
                                    i7 = AndroidUtilities.dp(2.0f);
                                    i = ((int) r1.deleteProgressRect.left) - i7;
                                    i4 = ((int) r1.deleteProgressRect.top) - i7;
                                    i7 *= 2;
                                    invalidate(i, i4, ((int) r1.deleteProgressRect.right) + i7, ((int) r1.deleteProgressRect.bottom) + i7);
                                }
                            }
                            updateSecretTimeText(r1.currentMessageObject);
                        }
                    }
                    max = 5.0f;
                    if (r1.documentAttachType != 2) {
                        if (r1.currentMessageObject.type != 8) {
                            if (r1.documentAttachType != 7) {
                                if (r1.currentMessageObject.type != 5) {
                                    RadialProgress radialProgress3;
                                    String str2;
                                    if (r1.documentAttachType == 5) {
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_outAudioTitleText));
                                            Theme.chat_audioPerformerPaint.setColor(Theme.getColor(Theme.key_chat_outAudioPerfomerText));
                                            Theme.chat_audioTimePaint.setColor(Theme.getColor(Theme.key_chat_outAudioDurationText));
                                            radialProgress3 = r1.radialProgress;
                                            if (!isDrawSelectedBackground()) {
                                                if (r1.buttonPressed == 0) {
                                                    str2 = Theme.key_chat_outAudioProgress;
                                                    radialProgress3.setProgressColor(Theme.getColor(str2));
                                                }
                                            }
                                            str2 = Theme.key_chat_outAudioSelectedProgress;
                                            radialProgress3.setProgressColor(Theme.getColor(str2));
                                        } else {
                                            Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_inAudioTitleText));
                                            Theme.chat_audioPerformerPaint.setColor(Theme.getColor(Theme.key_chat_inAudioPerfomerText));
                                            Theme.chat_audioTimePaint.setColor(Theme.getColor(Theme.key_chat_inAudioDurationText));
                                            radialProgress3 = r1.radialProgress;
                                            if (!isDrawSelectedBackground()) {
                                                if (r1.buttonPressed == 0) {
                                                    str2 = Theme.key_chat_inAudioProgress;
                                                    radialProgress3.setProgressColor(Theme.getColor(str2));
                                                }
                                            }
                                            str2 = Theme.key_chat_inAudioSelectedProgress;
                                            radialProgress3.setProgressColor(Theme.getColor(str2));
                                        }
                                        r1.radialProgress.draw(canvas2);
                                        canvas.save();
                                        canvas2.translate((float) (r1.timeAudioX + r1.songX), (float) ((AndroidUtilities.dp(13.0f) + r1.namesOffset) + r1.mediaOffsetY));
                                        r1.songLayout.draw(canvas2);
                                        canvas.restore();
                                        canvas.save();
                                        if (MediaController.getInstance().isPlayingMessage(r1.currentMessageObject)) {
                                            canvas2.translate((float) r1.seekBarX, (float) r1.seekBarY);
                                            r1.seekBar.draw(canvas2);
                                        } else {
                                            canvas2.translate((float) (r1.timeAudioX + r1.performerX), (float) ((AndroidUtilities.dp(35.0f) + r1.namesOffset) + r1.mediaOffsetY));
                                            r1.performerLayout.draw(canvas2);
                                        }
                                        canvas.restore();
                                        canvas.save();
                                        canvas2.translate((float) r1.timeAudioX, (float) ((AndroidUtilities.dp(57.0f) + r1.namesOffset) + r1.mediaOffsetY));
                                        r1.durationLayout.draw(canvas2);
                                        canvas.restore();
                                        drawable2 = r1.currentMessageObject.isOutOwner() ? isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable : isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
                                        i7 = (r1.buttonX + r1.backgroundWidth) - AndroidUtilities.dp(r1.currentMessageObject.type == 0 ? 58.0f : 48.0f);
                                        r1.otherX = i7;
                                        i4 = r1.buttonY - AndroidUtilities.dp(5.0f);
                                        r1.otherY = i4;
                                        BaseCell.setDrawableBounds(drawable2, i7, i4);
                                        drawable2.draw(canvas2);
                                    } else if (r1.documentAttachType == 3) {
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outAudioDurationSelectedText : Theme.key_chat_outAudioDurationText));
                                            radialProgress3 = r1.radialProgress;
                                            if (!isDrawSelectedBackground()) {
                                                if (r1.buttonPressed == 0) {
                                                    str2 = Theme.key_chat_outAudioProgress;
                                                    radialProgress3.setProgressColor(Theme.getColor(str2));
                                                }
                                            }
                                            str2 = Theme.key_chat_outAudioSelectedProgress;
                                            radialProgress3.setProgressColor(Theme.getColor(str2));
                                        } else {
                                            Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inAudioDurationSelectedText : Theme.key_chat_inAudioDurationText));
                                            radialProgress3 = r1.radialProgress;
                                            if (!isDrawSelectedBackground()) {
                                                if (r1.buttonPressed == 0) {
                                                    str2 = Theme.key_chat_inAudioProgress;
                                                    radialProgress3.setProgressColor(Theme.getColor(str2));
                                                }
                                            }
                                            str2 = Theme.key_chat_inAudioSelectedProgress;
                                            radialProgress3.setProgressColor(Theme.getColor(str2));
                                        }
                                        r1.radialProgress.draw(canvas2);
                                        canvas.save();
                                        if (r1.useSeekBarWaweform) {
                                            canvas2.translate((float) (r1.seekBarX + AndroidUtilities.dp(13.0f)), (float) r1.seekBarY);
                                            r1.seekBarWaveform.draw(canvas2);
                                        } else {
                                            canvas2.translate((float) r1.seekBarX, (float) r1.seekBarY);
                                            r1.seekBar.draw(canvas2);
                                        }
                                        canvas.restore();
                                        canvas.save();
                                        canvas2.translate((float) r1.timeAudioX, (float) ((AndroidUtilities.dp(44.0f) + r1.namesOffset) + r1.mediaOffsetY));
                                        r1.durationLayout.draw(canvas2);
                                        canvas.restore();
                                        if (r1.currentMessageObject.type != 0 && r1.currentMessageObject.isContentUnread()) {
                                            Theme.chat_docBackPaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outVoiceSeekbarFill : Theme.key_chat_inVoiceSeekbarFill));
                                            canvas2.drawCircle((float) ((r1.timeAudioX + r1.timeWidthAudio) + AndroidUtilities.dp(f)), (float) ((AndroidUtilities.dp(51.0f) + r1.namesOffset) + r1.mediaOffsetY), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                                        }
                                    }
                                    if (r1.currentMessageObject.type != 1) {
                                        if (r1.documentAttachType == 4) {
                                            if (r1.currentMessageObject.type == 4) {
                                                if (r1.docTitleLayout != null) {
                                                    if (r1.currentMessageObject.isOutOwner()) {
                                                        if (r1.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                                            Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                                        } else {
                                                            Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_outVenueNameText));
                                                        }
                                                        Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outVenueInfoSelectedText : Theme.key_chat_outVenueInfoText));
                                                    } else {
                                                        if (r1.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                                            Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                                        } else {
                                                            Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_inVenueNameText));
                                                        }
                                                        Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inVenueInfoSelectedText : Theme.key_chat_inVenueInfoText));
                                                    }
                                                    if (r1.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                                        i3 = r1.photoImage.getImageY2() + AndroidUtilities.dp(30.0f);
                                                        if (!r1.locationExpired) {
                                                            r1.forceNotDrawTime = true;
                                                            abs = 1.0f - (((float) Math.abs(ConnectionsManager.getInstance(r1.currentAccount).getCurrentTime() - r1.currentMessageObject.messageOwner.date)) / ((float) r1.currentMessageObject.messageOwner.media.period));
                                                            r1.rect.set((float) (r1.photoImage.getImageX2() - AndroidUtilities.dp(43.0f)), (float) (i3 - AndroidUtilities.dp(15.0f)), (float) (r1.photoImage.getImageX2() - AndroidUtilities.dp(13.0f)), (float) (AndroidUtilities.dp(15.0f) + i3));
                                                            if (r1.currentMessageObject.isOutOwner()) {
                                                                Theme.chat_radialProgress2Paint.setColor(Theme.getColor(Theme.key_chat_outInstant));
                                                                Theme.chat_livePaint.setColor(Theme.getColor(Theme.key_chat_outInstant));
                                                            } else {
                                                                Theme.chat_radialProgress2Paint.setColor(Theme.getColor(Theme.key_chat_inInstant));
                                                                Theme.chat_livePaint.setColor(Theme.getColor(Theme.key_chat_inInstant));
                                                            }
                                                            Theme.chat_radialProgress2Paint.setAlpha(50);
                                                            canvas2.drawCircle(r1.rect.centerX(), r1.rect.centerY(), (float) AndroidUtilities.dp(15.0f), Theme.chat_radialProgress2Paint);
                                                            Theme.chat_radialProgress2Paint.setAlpha(255);
                                                            canvas2.drawArc(r1.rect, -90.0f, abs * -360.0f, false, Theme.chat_radialProgress2Paint);
                                                            formatLocationLeftTime = LocaleController.formatLocationLeftTime(Math.abs(r1.currentMessageObject.messageOwner.media.period - (ConnectionsManager.getInstance(r1.currentAccount).getCurrentTime() - r1.currentMessageObject.messageOwner.date)));
                                                            canvas2.drawText(formatLocationLeftTime, r1.rect.centerX() - (Theme.chat_livePaint.measureText(formatLocationLeftTime) / 2.0f), (float) (i3 + AndroidUtilities.dp(4.0f)), Theme.chat_livePaint);
                                                            canvas.save();
                                                            canvas2.translate((float) (r1.photoImage.getImageX() + AndroidUtilities.dp(10.0f)), (float) (r1.photoImage.getImageY2() + AndroidUtilities.dp(10.0f)));
                                                            r1.docTitleLayout.draw(canvas2);
                                                            canvas2.translate(0.0f, (float) AndroidUtilities.dp(23.0f));
                                                            r1.infoLayout.draw(canvas2);
                                                            canvas.restore();
                                                        }
                                                        i = (r1.photoImage.getImageX() + (r1.photoImage.getImageWidth() / 2)) - AndroidUtilities.dp(31.0f);
                                                        i7 = (r1.photoImage.getImageY() + (r1.photoImage.getImageHeight() / 2)) - AndroidUtilities.dp(38.0f);
                                                        BaseCell.setDrawableBounds(Theme.chat_msgAvatarLiveLocationDrawable, i, i7);
                                                        Theme.chat_msgAvatarLiveLocationDrawable.draw(canvas2);
                                                        r1.locationImageReceiver.setImageCoords(i + AndroidUtilities.dp(5.0f), i7 + AndroidUtilities.dp(5.0f), AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
                                                        r1.locationImageReceiver.draw(canvas2);
                                                    } else {
                                                        canvas.save();
                                                        canvas2.translate((float) (((r1.docTitleOffsetX + r1.photoImage.getImageX()) + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) (r1.photoImage.getImageY() + AndroidUtilities.dp(8.0f)));
                                                        r1.docTitleLayout.draw(canvas2);
                                                        canvas.restore();
                                                        if (r1.infoLayout != null) {
                                                            canvas.save();
                                                            canvas2.translate((float) ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) ((r1.photoImage.getImageY() + r1.docTitleLayout.getLineBottom(r1.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f)));
                                                            r1.infoLayout.draw(canvas2);
                                                            canvas.restore();
                                                        }
                                                    }
                                                }
                                            } else if (r1.currentMessageObject.type != 16) {
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                    Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                                    Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
                                                } else {
                                                    Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                                    Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
                                                }
                                                r1.forceNotDrawTime = true;
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                    i = (r1.layoutWidth - r1.backgroundWidth) + AndroidUtilities.dp(16.0f);
                                                } else if (r1.isChat || !r1.currentMessageObject.needDrawAvatar()) {
                                                    i = AndroidUtilities.dp(25.0f);
                                                } else {
                                                    i = AndroidUtilities.dp(74.0f);
                                                }
                                                r1.otherX = i;
                                                if (r1.titleLayout != null) {
                                                    canvas.save();
                                                    canvas2.translate((float) i, (float) (AndroidUtilities.dp(12.0f) + r1.namesOffset));
                                                    r1.titleLayout.draw(canvas2);
                                                    canvas.restore();
                                                }
                                                if (r1.docTitleLayout != null) {
                                                    canvas.save();
                                                    canvas2.translate((float) (AndroidUtilities.dp(19.0f) + i), (float) (AndroidUtilities.dp(37.0f) + r1.namesOffset));
                                                    r1.docTitleLayout.draw(canvas2);
                                                    canvas.restore();
                                                }
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                    drawable = Theme.chat_msgCallUpGreenDrawable;
                                                    if (!isDrawSelectedBackground()) {
                                                        if (r1.otherPressed) {
                                                            drawable3 = Theme.chat_msgOutCallDrawable;
                                                        }
                                                    }
                                                    drawable3 = Theme.chat_msgOutCallSelectedDrawable;
                                                } else {
                                                    phoneCallDiscardReason = r1.currentMessageObject.messageOwner.action.reason;
                                                    if (!(phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonMissed)) {
                                                        if (phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonBusy) {
                                                            drawable = Theme.chat_msgCallDownGreenDrawable;
                                                            if (!isDrawSelectedBackground()) {
                                                                if (r1.otherPressed) {
                                                                    drawable3 = Theme.chat_msgInCallDrawable;
                                                                }
                                                            }
                                                            drawable3 = Theme.chat_msgInCallSelectedDrawable;
                                                        }
                                                    }
                                                    drawable = Theme.chat_msgCallDownRedDrawable;
                                                    if (isDrawSelectedBackground()) {
                                                        if (r1.otherPressed) {
                                                            drawable3 = Theme.chat_msgInCallDrawable;
                                                        }
                                                    }
                                                    drawable3 = Theme.chat_msgInCallSelectedDrawable;
                                                }
                                                BaseCell.setDrawableBounds(drawable, i - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(36.0f) + r1.namesOffset);
                                                drawable.draw(canvas2);
                                                i += AndroidUtilities.dp(205.0f);
                                                i7 = AndroidUtilities.dp(22.0f);
                                                r1.otherY = i7;
                                                BaseCell.setDrawableBounds(drawable3, i, i7);
                                                drawable3.draw(canvas2);
                                            } else if (r1.currentMessageObject.type == 12) {
                                                Theme.chat_contactNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outContactNameText : Theme.key_chat_inContactNameText));
                                                Theme.chat_contactPhonePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outContactPhoneText : Theme.key_chat_inContactPhoneText));
                                                if (r1.titleLayout != null) {
                                                    canvas.save();
                                                    canvas2.translate((float) ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(16.0f) + r1.namesOffset));
                                                    r1.titleLayout.draw(canvas2);
                                                    canvas.restore();
                                                }
                                                if (r1.docTitleLayout != null) {
                                                    canvas.save();
                                                    canvas2.translate((float) ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(39.0f) + r1.namesOffset));
                                                    r1.docTitleLayout.draw(canvas2);
                                                    canvas.restore();
                                                }
                                                drawable2 = r1.currentMessageObject.isOutOwner() ? isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable : isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
                                                i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(48.0f);
                                                r1.otherX = i7;
                                                i4 = r1.photoImage.getImageY() - AndroidUtilities.dp(5.0f);
                                                r1.otherY = i4;
                                                BaseCell.setDrawableBounds(drawable2, i7, i4);
                                                drawable2.draw(canvas2);
                                            }
                                            if (r1.captionLayout == null) {
                                                if (!(r1.currentMessageObject.type == 1 || r1.documentAttachType == 4)) {
                                                    if (r1.currentMessageObject.type != 8) {
                                                        if (r1.hasOldCaptionPreview) {
                                                            f2 = 17.0f;
                                                            r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                                                            r1.captionY = (r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f);
                                                        } else {
                                                            r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                                                            f2 = 17.0f;
                                                            r1.captionY = (((r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
                                                        }
                                                    }
                                                }
                                                f2 = 17.0f;
                                                r1.captionX = (r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f)) + r1.captionOffsetX;
                                                r1.captionY = (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()) + AndroidUtilities.dp(f);
                                            } else {
                                                f2 = 17.0f;
                                            }
                                            if (r1.currentPosition != null) {
                                                z3 = false;
                                                drawCaptionLayout(canvas2, false);
                                            } else {
                                                z3 = false;
                                            }
                                            if (r1.hasOldCaptionPreview) {
                                                z = true;
                                            } else {
                                                if (!(r1.currentMessageObject.type == 1 || r1.documentAttachType == 4)) {
                                                    if (r1.currentMessageObject.type == 8) {
                                                        i = r1.backgroundDrawableLeft;
                                                        if (r1.currentMessageObject.isOutOwner()) {
                                                            f2 = 11.0f;
                                                        }
                                                        i += AndroidUtilities.dp(f2);
                                                        i5 = i;
                                                        dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                                                        Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                                                        canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                                                        if (r1.siteNameLayout == null) {
                                                            Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                                                            canvas.save();
                                                            i = r1.siteNameRtl ? (r1.backgroundWidth - r1.siteNameWidth) - AndroidUtilities.dp(32.0f) : r1.hasInvoicePreview ? z3 : AndroidUtilities.dp(10.0f);
                                                            canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                                                            r1.siteNameLayout.draw(canvas2);
                                                            canvas.restore();
                                                            i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                                                        } else {
                                                            i = dp;
                                                        }
                                                        if (r1.currentMessageObject.isOutOwner()) {
                                                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                                        } else {
                                                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                                        }
                                                        if (r1.descriptionLayout != null) {
                                                            if (i != dp) {
                                                                i += AndroidUtilities.dp(2.0f);
                                                            }
                                                            r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                                                            canvas.save();
                                                            canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                                                            r1.descriptionLayout.draw(canvas2);
                                                            canvas.restore();
                                                        }
                                                        z = true;
                                                        r1.drawTime = true;
                                                    }
                                                }
                                                i = r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f);
                                                i5 = i;
                                                if (r1.drawPinnedTop) {
                                                }
                                                dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                }
                                                Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                                                canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                                                if (r1.siteNameLayout == null) {
                                                    i = dp;
                                                } else {
                                                    if (r1.currentMessageObject.isOutOwner()) {
                                                    }
                                                    Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                                                    canvas.save();
                                                    if (r1.siteNameRtl) {
                                                    }
                                                    canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                                                    r1.siteNameLayout.draw(canvas2);
                                                    canvas.restore();
                                                    i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                                                }
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                                } else {
                                                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                                }
                                                if (r1.descriptionLayout != null) {
                                                    if (i != dp) {
                                                        i += AndroidUtilities.dp(2.0f);
                                                    }
                                                    r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                                                    canvas.save();
                                                    canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                                                    r1.descriptionLayout.draw(canvas2);
                                                    canvas.restore();
                                                }
                                                z = true;
                                                r1.drawTime = true;
                                            }
                                            if (r1.documentAttachType == z) {
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                    Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_inFileNameText));
                                                    Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileInfoSelectedText : Theme.key_chat_inFileInfoText));
                                                    Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileBackgroundSelected : Theme.key_chat_inFileBackground));
                                                    drawable2 = isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
                                                } else {
                                                    Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_outFileNameText));
                                                    Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileInfoSelectedText : Theme.key_chat_outFileInfoText));
                                                    Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileBackgroundSelected : Theme.key_chat_outFileBackground));
                                                    drawable2 = isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
                                                }
                                                if (r1.drawPhotoImage) {
                                                    i7 = (r1.buttonX + r1.backgroundWidth) - AndroidUtilities.dp(r1.currentMessageObject.type != 0 ? 58.0f : 48.0f);
                                                    r1.otherX = i7;
                                                    i4 = r1.buttonY - AndroidUtilities.dp(5.0f);
                                                    r1.otherY = i4;
                                                    BaseCell.setDrawableBounds(drawable2, i7, i4);
                                                    i7 = r1.buttonX + AndroidUtilities.dp(53.0f);
                                                    i4 = r1.buttonY + AndroidUtilities.dp(4.0f);
                                                    alpha = r1.buttonY + AndroidUtilities.dp(27.0f);
                                                    if (r1.currentMessageObject.isOutOwner()) {
                                                        radialProgress = r1.radialProgress;
                                                        if (!isDrawSelectedBackground()) {
                                                            if (r1.buttonPressed != 0) {
                                                                str = Theme.key_chat_inAudioProgress;
                                                                radialProgress.setProgressColor(Theme.getColor(str));
                                                            }
                                                        }
                                                        str = Theme.key_chat_inAudioSelectedProgress;
                                                        radialProgress.setProgressColor(Theme.getColor(str));
                                                    } else {
                                                        radialProgress = r1.radialProgress;
                                                        if (!isDrawSelectedBackground()) {
                                                            if (r1.buttonPressed != 0) {
                                                                str = Theme.key_chat_outAudioProgress;
                                                                radialProgress.setProgressColor(Theme.getColor(str));
                                                            }
                                                        }
                                                        str = Theme.key_chat_outAudioSelectedProgress;
                                                        radialProgress.setProgressColor(Theme.getColor(str));
                                                    }
                                                } else {
                                                    if (r1.currentMessageObject.type != 0) {
                                                        i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(56.0f);
                                                        r1.otherX = i7;
                                                        i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                                        r1.otherY = i4;
                                                        BaseCell.setDrawableBounds(drawable2, i7, i4);
                                                    } else {
                                                        i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(40.0f);
                                                        r1.otherX = i7;
                                                        i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                                        r1.otherY = i4;
                                                        BaseCell.setDrawableBounds(drawable2, i7, i4);
                                                    }
                                                    i7 = (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
                                                    i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                                                    alpha = (r1.photoImage.getImageY() + r1.docTitleLayout.getLineBottom(r1.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f);
                                                    if (r1.buttonState >= 0 && r1.buttonState < 4) {
                                                        if (z2) {
                                                            i6 = r1.buttonState;
                                                            if (r1.buttonState != 0) {
                                                                i6 = r1.currentMessageObject.isOutOwner() ? 7 : 10;
                                                            } else if (r1.buttonState == 1) {
                                                                i6 = r1.currentMessageObject.isOutOwner() ? 8 : 11;
                                                            }
                                                            radialProgress2 = r1.radialProgress;
                                                            drawableArr = Theme.chat_photoStatesDrawables[i6];
                                                            if (!isDrawSelectedBackground()) {
                                                                if (r1.buttonPressed != 0) {
                                                                    i5 = z3;
                                                                    radialProgress2.swapBackground(drawableArr[i5]);
                                                                }
                                                            }
                                                            i5 = 1;
                                                            radialProgress2.swapBackground(drawableArr[i5]);
                                                        } else {
                                                            r1.radialProgress.swapBackground(Theme.chat_photoStatesDrawables[r1.buttonState][r1.buttonPressed]);
                                                        }
                                                    }
                                                    if (z2) {
                                                        r1.rect.set((float) r1.photoImage.getImageX(), (float) r1.photoImage.getImageY(), (float) (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()), (float) (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()));
                                                        canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                                                        if (r1.currentMessageObject.isOutOwner()) {
                                                            r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileProgressSelected : Theme.key_chat_inFileProgress));
                                                        } else {
                                                            r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileProgressSelected : Theme.key_chat_outFileProgress));
                                                        }
                                                    } else {
                                                        if (r1.buttonState == -1) {
                                                            r1.radialProgress.setHideCurrentDrawable(true);
                                                        }
                                                        r1.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
                                                    }
                                                }
                                                drawable2.draw(canvas2);
                                                try {
                                                    if (r1.docTitleLayout != null) {
                                                        canvas.save();
                                                        canvas2.translate((float) (r1.docTitleOffsetX + i7), (float) i4);
                                                        r1.docTitleLayout.draw(canvas2);
                                                        canvas.restore();
                                                    }
                                                } catch (Throwable e2) {
                                                    FileLog.m3e(e2);
                                                }
                                                try {
                                                    if (r1.infoLayout != null) {
                                                        canvas.save();
                                                        canvas2.translate((float) i7, (float) alpha);
                                                        r1.infoLayout.draw(canvas2);
                                                        canvas.restore();
                                                    }
                                                } catch (Throwable e22) {
                                                    FileLog.m3e(e22);
                                                }
                                            }
                                            if (r1.drawImageButton && r1.photoImage.getVisible()) {
                                                if (r1.controlsAlpha != 1.0f) {
                                                    r1.radialProgress.setOverrideAlpha(r1.controlsAlpha);
                                                }
                                                r1.radialProgress.draw(canvas2);
                                            }
                                            if (!r1.botButtons.isEmpty()) {
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                    i = r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.mediaBackground ? 1.0f : 7.0f);
                                                } else {
                                                    i = (getMeasuredWidth() - r1.widthForButtons) - AndroidUtilities.dp(10.0f);
                                                }
                                                i2 = i;
                                                i5 = z3;
                                                while (i5 < r1.botButtons.size()) {
                                                    botButton = (BotButton) r1.botButtons.get(i5);
                                                    i = (botButton.f10y + r1.layoutHeight) - AndroidUtilities.dp(2.0f);
                                                    Theme.chat_systemDrawable.setColorFilter(i5 != r1.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                                                    Theme.chat_systemDrawable.setBounds(botButton.f9x + i2, i, (botButton.f9x + i2) + botButton.width, botButton.height + i);
                                                    Theme.chat_systemDrawable.draw(canvas2);
                                                    canvas.save();
                                                    canvas2.translate((float) ((botButton.f9x + i2) + AndroidUtilities.dp(max)), (float) (((AndroidUtilities.dp(44.0f) - botButton.title.getLineBottom(botButton.title.getLineCount() - 1)) / 2) + i));
                                                    botButton.title.draw(canvas2);
                                                    canvas.restore();
                                                    if (botButton.button instanceof TL_keyboardButtonUrl) {
                                                        if (botButton.button instanceof TL_keyboardButtonSwitchInline) {
                                                            f6 = 3.0f;
                                                            if ((botButton.button instanceof TL_keyboardButtonCallback) || (botButton.button instanceof TL_keyboardButtonRequestGeoLocation) || (botButton.button instanceof TL_keyboardButtonGame) || (botButton.button instanceof TL_keyboardButtonBuy)) {
                                                                if (!(((botButton.button instanceof TL_keyboardButtonCallback) || (botButton.button instanceof TL_keyboardButtonGame) || (botButton.button instanceof TL_keyboardButtonBuy)) && SendMessagesHelper.getInstance(r1.currentAccount).isSendingCallback(r1.currentMessageObject, botButton.button))) {
                                                                    if ((botButton.button instanceof TL_keyboardButtonRequestGeoLocation) || !SendMessagesHelper.getInstance(r1.currentAccount).isSendingCurrentLocation(r1.currentMessageObject, botButton.button)) {
                                                                        z4 = z3;
                                                                        if (z4 || !(z4 || botButton.progressAlpha == 0.0f)) {
                                                                            Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (botButton.progressAlpha * 255.0f)));
                                                                            i7 = ((botButton.f9x + botButton.width) - AndroidUtilities.dp(12.0f)) + i2;
                                                                            r1.rect.set((float) i7, (float) (i + AndroidUtilities.dp(4.0f)), (float) (i7 + AndroidUtilities.dp(8.0f)), (float) (i + AndroidUtilities.dp(12.0f)));
                                                                            canvas2.drawArc(r1.rect, (float) botButton.angle, 220.0f, false, Theme.chat_botProgressPaint);
                                                                            invalidate(((int) r1.rect.left) - AndroidUtilities.dp(2.0f), ((int) r1.rect.top) - AndroidUtilities.dp(2.0f), ((int) r1.rect.right) + AndroidUtilities.dp(2.0f), ((int) r1.rect.bottom) + AndroidUtilities.dp(2.0f));
                                                                            currentTimeMillis = System.currentTimeMillis();
                                                                            if (Math.abs(botButton.lastUpdateTime - System.currentTimeMillis()) >= 1000) {
                                                                                access$1300 = currentTimeMillis - botButton.lastUpdateTime;
                                                                                botButton.angle = (int) (((float) botButton.angle) + (((float) (360 * access$1300)) / 2000.0f));
                                                                                botButton.angle = botButton.angle - (360 * (botButton.angle / 360));
                                                                                if (z4) {
                                                                                    if (botButton.progressAlpha > 0.0f) {
                                                                                        botButton.progressAlpha = botButton.progressAlpha - (((float) access$1300) / 200.0f);
                                                                                        if (botButton.progressAlpha < 0.0f) {
                                                                                            botButton.progressAlpha = 0.0f;
                                                                                        }
                                                                                    }
                                                                                    botButton.lastUpdateTime = currentTimeMillis;
                                                                                } else if (botButton.progressAlpha < 1.0f) {
                                                                                    botButton.progressAlpha = botButton.progressAlpha + (((float) access$1300) / 200.0f);
                                                                                    if (botButton.progressAlpha > 1.0f) {
                                                                                        botButton.progressAlpha = 1.0f;
                                                                                    }
                                                                                }
                                                                            }
                                                                            botButton.lastUpdateTime = currentTimeMillis;
                                                                        }
                                                                    }
                                                                }
                                                                z4 = true;
                                                                Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (botButton.progressAlpha * 255.0f)));
                                                                i7 = ((botButton.f9x + botButton.width) - AndroidUtilities.dp(12.0f)) + i2;
                                                                r1.rect.set((float) i7, (float) (i + AndroidUtilities.dp(4.0f)), (float) (i7 + AndroidUtilities.dp(8.0f)), (float) (i + AndroidUtilities.dp(12.0f)));
                                                                canvas2.drawArc(r1.rect, (float) botButton.angle, 220.0f, false, Theme.chat_botProgressPaint);
                                                                invalidate(((int) r1.rect.left) - AndroidUtilities.dp(2.0f), ((int) r1.rect.top) - AndroidUtilities.dp(2.0f), ((int) r1.rect.right) + AndroidUtilities.dp(2.0f), ((int) r1.rect.bottom) + AndroidUtilities.dp(2.0f));
                                                                currentTimeMillis = System.currentTimeMillis();
                                                                if (Math.abs(botButton.lastUpdateTime - System.currentTimeMillis()) >= 1000) {
                                                                    access$1300 = currentTimeMillis - botButton.lastUpdateTime;
                                                                    botButton.angle = (int) (((float) botButton.angle) + (((float) (360 * access$1300)) / 2000.0f));
                                                                    botButton.angle = botButton.angle - (360 * (botButton.angle / 360));
                                                                    if (z4) {
                                                                        if (botButton.progressAlpha > 0.0f) {
                                                                            botButton.progressAlpha = botButton.progressAlpha - (((float) access$1300) / 200.0f);
                                                                            if (botButton.progressAlpha < 0.0f) {
                                                                                botButton.progressAlpha = 0.0f;
                                                                            }
                                                                        }
                                                                        botButton.lastUpdateTime = currentTimeMillis;
                                                                    } else if (botButton.progressAlpha < 1.0f) {
                                                                        botButton.progressAlpha = botButton.progressAlpha + (((float) access$1300) / 200.0f);
                                                                        if (botButton.progressAlpha > 1.0f) {
                                                                            botButton.progressAlpha = 1.0f;
                                                                        }
                                                                    }
                                                                }
                                                                botButton.lastUpdateTime = currentTimeMillis;
                                                            }
                                                        } else {
                                                            f6 = 3.0f;
                                                            BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                                            Theme.chat_botInlineDrawable.draw(canvas2);
                                                        }
                                                        f3 = f6;
                                                    } else {
                                                        BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                                        Theme.chat_botLinkDrawalbe.draw(canvas2);
                                                    }
                                                    i5++;
                                                    max = 5.0f;
                                                    z3 = false;
                                                }
                                            }
                                        }
                                    }
                                    if (r1.photoImage.getVisible()) {
                                        if (!r1.currentMessageObject.needDrawBluredPreview() && r1.documentAttachType == 4) {
                                            i = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                                            Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) i) * r1.controlsAlpha));
                                            drawable = Theme.chat_msgMediaMenuDrawable;
                                            i4 = (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                                            r1.otherX = i4;
                                            alpha = r1.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                                            r1.otherY = alpha;
                                            BaseCell.setDrawableBounds(drawable, i4, alpha);
                                            Theme.chat_msgMediaMenuDrawable.draw(canvas2);
                                            Theme.chat_msgMediaMenuDrawable.setAlpha(i);
                                        }
                                        if (!(r1.forceNotDrawTime || r1.infoLayout == null || (r1.buttonState != 1 && r1.buttonState != 0 && r1.buttonState != 3 && !r1.currentMessageObject.needDrawBluredPreview()))) {
                                            Theme.chat_infoPaint.setColor(Theme.getColor(Theme.key_chat_mediaInfoText));
                                            i = r1.photoImage.getImageX() + AndroidUtilities.dp(4.0f);
                                            i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(4.0f);
                                            r1.rect.set((float) i, (float) i4, (float) ((i + r1.infoWidth) + AndroidUtilities.dp(8.0f)), (float) (i4 + AndroidUtilities.dp(16.5f)));
                                            i = Theme.chat_timeBackgroundPaint.getAlpha();
                                            Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) i) * r1.controlsAlpha));
                                            canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                                            Theme.chat_timeBackgroundPaint.setAlpha(i);
                                            canvas.save();
                                            canvas2.translate((float) (r1.photoImage.getImageX() + AndroidUtilities.dp(8.0f)), (float) (r1.photoImage.getImageY() + AndroidUtilities.dp(5.5f)));
                                            Theme.chat_infoPaint.setAlpha((int) (255.0f * r1.controlsAlpha));
                                            r1.infoLayout.draw(canvas2);
                                            canvas.restore();
                                            Theme.chat_infoPaint.setAlpha(255);
                                        }
                                    }
                                    if (r1.captionLayout == null) {
                                        f2 = 17.0f;
                                    } else if (r1.currentMessageObject.type != 8) {
                                        f2 = 17.0f;
                                        r1.captionX = (r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f)) + r1.captionOffsetX;
                                        r1.captionY = (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()) + AndroidUtilities.dp(f);
                                    } else if (r1.hasOldCaptionPreview) {
                                        f2 = 17.0f;
                                        if (r1.currentMessageObject.isOutOwner()) {
                                        }
                                        r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                                        if (r1.drawPinnedTop) {
                                        }
                                        r1.captionY = (r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f);
                                    } else {
                                        if (r1.currentMessageObject.isOutOwner()) {
                                        }
                                        r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                                        if (r1.drawPinnedTop) {
                                        }
                                        f2 = 17.0f;
                                        r1.captionY = (((r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
                                    }
                                    if (r1.currentPosition != null) {
                                        z3 = false;
                                    } else {
                                        z3 = false;
                                        drawCaptionLayout(canvas2, false);
                                    }
                                    if (r1.hasOldCaptionPreview) {
                                        z = true;
                                    } else if (r1.currentMessageObject.type == 8) {
                                        i = r1.backgroundDrawableLeft;
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            f2 = 11.0f;
                                        }
                                        i += AndroidUtilities.dp(f2);
                                        i5 = i;
                                        if (r1.drawPinnedTop) {
                                        }
                                        dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                                        if (r1.currentMessageObject.isOutOwner()) {
                                        }
                                        Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                                        canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                                        if (r1.siteNameLayout == null) {
                                            if (r1.currentMessageObject.isOutOwner()) {
                                            }
                                            Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                                            canvas.save();
                                            if (r1.siteNameRtl) {
                                            }
                                            canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                                            r1.siteNameLayout.draw(canvas2);
                                            canvas.restore();
                                            i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                                        } else {
                                            i = dp;
                                        }
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                        } else {
                                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                        }
                                        if (r1.descriptionLayout != null) {
                                            if (i != dp) {
                                                i += AndroidUtilities.dp(2.0f);
                                            }
                                            r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                                            canvas.save();
                                            canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                                            r1.descriptionLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                        z = true;
                                        r1.drawTime = true;
                                    } else {
                                        i = r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f);
                                        i5 = i;
                                        if (r1.drawPinnedTop) {
                                        }
                                        dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                                        if (r1.currentMessageObject.isOutOwner()) {
                                        }
                                        Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                                        canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                                        if (r1.siteNameLayout == null) {
                                            i = dp;
                                        } else {
                                            if (r1.currentMessageObject.isOutOwner()) {
                                            }
                                            Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                                            canvas.save();
                                            if (r1.siteNameRtl) {
                                                if (r1.hasInvoicePreview) {
                                                }
                                            }
                                            canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                                            r1.siteNameLayout.draw(canvas2);
                                            canvas.restore();
                                            i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                                        }
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                        } else {
                                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                        }
                                        if (r1.descriptionLayout != null) {
                                            if (i != dp) {
                                                i += AndroidUtilities.dp(2.0f);
                                            }
                                            r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                                            canvas.save();
                                            canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                                            r1.descriptionLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                        z = true;
                                        r1.drawTime = true;
                                    }
                                    if (r1.documentAttachType == z) {
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_inFileNameText));
                                            if (isDrawSelectedBackground()) {
                                            }
                                            Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileInfoSelectedText : Theme.key_chat_inFileInfoText));
                                            if (isDrawSelectedBackground()) {
                                            }
                                            Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileBackgroundSelected : Theme.key_chat_inFileBackground));
                                            if (isDrawSelectedBackground()) {
                                            }
                                        } else {
                                            Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_outFileNameText));
                                            if (isDrawSelectedBackground()) {
                                            }
                                            Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileInfoSelectedText : Theme.key_chat_outFileInfoText));
                                            if (isDrawSelectedBackground()) {
                                            }
                                            Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileBackgroundSelected : Theme.key_chat_outFileBackground));
                                            if (isDrawSelectedBackground()) {
                                            }
                                        }
                                        if (r1.drawPhotoImage) {
                                            if (r1.currentMessageObject.type != 0) {
                                            }
                                            i7 = (r1.buttonX + r1.backgroundWidth) - AndroidUtilities.dp(r1.currentMessageObject.type != 0 ? 58.0f : 48.0f);
                                            r1.otherX = i7;
                                            i4 = r1.buttonY - AndroidUtilities.dp(5.0f);
                                            r1.otherY = i4;
                                            BaseCell.setDrawableBounds(drawable2, i7, i4);
                                            i7 = r1.buttonX + AndroidUtilities.dp(53.0f);
                                            i4 = r1.buttonY + AndroidUtilities.dp(4.0f);
                                            alpha = r1.buttonY + AndroidUtilities.dp(27.0f);
                                            if (r1.currentMessageObject.isOutOwner()) {
                                                radialProgress = r1.radialProgress;
                                                if (isDrawSelectedBackground()) {
                                                    if (r1.buttonPressed != 0) {
                                                        str = Theme.key_chat_inAudioProgress;
                                                        radialProgress.setProgressColor(Theme.getColor(str));
                                                    }
                                                }
                                                str = Theme.key_chat_inAudioSelectedProgress;
                                                radialProgress.setProgressColor(Theme.getColor(str));
                                            } else {
                                                radialProgress = r1.radialProgress;
                                                if (isDrawSelectedBackground()) {
                                                    if (r1.buttonPressed != 0) {
                                                        str = Theme.key_chat_outAudioProgress;
                                                        radialProgress.setProgressColor(Theme.getColor(str));
                                                    }
                                                }
                                                str = Theme.key_chat_outAudioSelectedProgress;
                                                radialProgress.setProgressColor(Theme.getColor(str));
                                            }
                                        } else {
                                            if (r1.currentMessageObject.type != 0) {
                                                i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(40.0f);
                                                r1.otherX = i7;
                                                i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                                r1.otherY = i4;
                                                BaseCell.setDrawableBounds(drawable2, i7, i4);
                                            } else {
                                                i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(56.0f);
                                                r1.otherX = i7;
                                                i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                                r1.otherY = i4;
                                                BaseCell.setDrawableBounds(drawable2, i7, i4);
                                            }
                                            i7 = (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
                                            i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                                            alpha = (r1.photoImage.getImageY() + r1.docTitleLayout.getLineBottom(r1.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f);
                                            if (z2) {
                                                r1.radialProgress.swapBackground(Theme.chat_photoStatesDrawables[r1.buttonState][r1.buttonPressed]);
                                            } else {
                                                i6 = r1.buttonState;
                                                if (r1.buttonState != 0) {
                                                    if (r1.buttonState == 1) {
                                                        if (r1.currentMessageObject.isOutOwner()) {
                                                        }
                                                    }
                                                } else if (r1.currentMessageObject.isOutOwner()) {
                                                }
                                                radialProgress2 = r1.radialProgress;
                                                drawableArr = Theme.chat_photoStatesDrawables[i6];
                                                if (isDrawSelectedBackground()) {
                                                    if (r1.buttonPressed != 0) {
                                                        i5 = z3;
                                                        radialProgress2.swapBackground(drawableArr[i5]);
                                                    }
                                                }
                                                i5 = 1;
                                                radialProgress2.swapBackground(drawableArr[i5]);
                                            }
                                            if (z2) {
                                                if (r1.buttonState == -1) {
                                                    r1.radialProgress.setHideCurrentDrawable(true);
                                                }
                                                r1.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
                                            } else {
                                                r1.rect.set((float) r1.photoImage.getImageX(), (float) r1.photoImage.getImageY(), (float) (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()), (float) (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()));
                                                canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                    if (isDrawSelectedBackground()) {
                                                    }
                                                    r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileProgressSelected : Theme.key_chat_inFileProgress));
                                                } else {
                                                    if (isDrawSelectedBackground()) {
                                                    }
                                                    r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileProgressSelected : Theme.key_chat_outFileProgress));
                                                }
                                            }
                                        }
                                        drawable2.draw(canvas2);
                                        if (r1.docTitleLayout != null) {
                                            canvas.save();
                                            canvas2.translate((float) (r1.docTitleOffsetX + i7), (float) i4);
                                            r1.docTitleLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                        if (r1.infoLayout != null) {
                                            canvas.save();
                                            canvas2.translate((float) i7, (float) alpha);
                                            r1.infoLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                    }
                                    if (r1.controlsAlpha != 1.0f) {
                                        r1.radialProgress.setOverrideAlpha(r1.controlsAlpha);
                                    }
                                    r1.radialProgress.draw(canvas2);
                                    if (!r1.botButtons.isEmpty()) {
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            if (r1.mediaBackground) {
                                            }
                                            i = r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.mediaBackground ? 1.0f : 7.0f);
                                        } else {
                                            i = (getMeasuredWidth() - r1.widthForButtons) - AndroidUtilities.dp(10.0f);
                                        }
                                        i2 = i;
                                        i5 = z3;
                                        while (i5 < r1.botButtons.size()) {
                                            botButton = (BotButton) r1.botButtons.get(i5);
                                            i = (botButton.f10y + r1.layoutHeight) - AndroidUtilities.dp(2.0f);
                                            if (i5 != r1.pressedBotButton) {
                                            }
                                            Theme.chat_systemDrawable.setColorFilter(i5 != r1.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                                            Theme.chat_systemDrawable.setBounds(botButton.f9x + i2, i, (botButton.f9x + i2) + botButton.width, botButton.height + i);
                                            Theme.chat_systemDrawable.draw(canvas2);
                                            canvas.save();
                                            canvas2.translate((float) ((botButton.f9x + i2) + AndroidUtilities.dp(max)), (float) (((AndroidUtilities.dp(44.0f) - botButton.title.getLineBottom(botButton.title.getLineCount() - 1)) / 2) + i));
                                            botButton.title.draw(canvas2);
                                            canvas.restore();
                                            if (botButton.button instanceof TL_keyboardButtonUrl) {
                                                if (botButton.button instanceof TL_keyboardButtonSwitchInline) {
                                                    f6 = 3.0f;
                                                    if (botButton.button instanceof TL_keyboardButtonRequestGeoLocation) {
                                                    }
                                                    z4 = z3;
                                                    Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (botButton.progressAlpha * 255.0f)));
                                                    i7 = ((botButton.f9x + botButton.width) - AndroidUtilities.dp(12.0f)) + i2;
                                                    r1.rect.set((float) i7, (float) (i + AndroidUtilities.dp(4.0f)), (float) (i7 + AndroidUtilities.dp(8.0f)), (float) (i + AndroidUtilities.dp(12.0f)));
                                                    canvas2.drawArc(r1.rect, (float) botButton.angle, 220.0f, false, Theme.chat_botProgressPaint);
                                                    invalidate(((int) r1.rect.left) - AndroidUtilities.dp(2.0f), ((int) r1.rect.top) - AndroidUtilities.dp(2.0f), ((int) r1.rect.right) + AndroidUtilities.dp(2.0f), ((int) r1.rect.bottom) + AndroidUtilities.dp(2.0f));
                                                    currentTimeMillis = System.currentTimeMillis();
                                                    if (Math.abs(botButton.lastUpdateTime - System.currentTimeMillis()) >= 1000) {
                                                        access$1300 = currentTimeMillis - botButton.lastUpdateTime;
                                                        botButton.angle = (int) (((float) botButton.angle) + (((float) (360 * access$1300)) / 2000.0f));
                                                        botButton.angle = botButton.angle - (360 * (botButton.angle / 360));
                                                        if (z4) {
                                                            if (botButton.progressAlpha > 0.0f) {
                                                                botButton.progressAlpha = botButton.progressAlpha - (((float) access$1300) / 200.0f);
                                                                if (botButton.progressAlpha < 0.0f) {
                                                                    botButton.progressAlpha = 0.0f;
                                                                }
                                                            }
                                                            botButton.lastUpdateTime = currentTimeMillis;
                                                        } else if (botButton.progressAlpha < 1.0f) {
                                                            botButton.progressAlpha = botButton.progressAlpha + (((float) access$1300) / 200.0f);
                                                            if (botButton.progressAlpha > 1.0f) {
                                                                botButton.progressAlpha = 1.0f;
                                                            }
                                                        }
                                                    }
                                                    botButton.lastUpdateTime = currentTimeMillis;
                                                } else {
                                                    f6 = 3.0f;
                                                    BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                                    Theme.chat_botInlineDrawable.draw(canvas2);
                                                }
                                                f3 = f6;
                                            } else {
                                                BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                                Theme.chat_botLinkDrawalbe.draw(canvas2);
                                            }
                                            i5++;
                                            max = 5.0f;
                                            z3 = false;
                                        }
                                    }
                                }
                            }
                            if (r1.durationLayout != null) {
                                z = MediaController.getInstance().isPlayingMessage(r1.currentMessageObject);
                                if (z && r1.currentMessageObject.type == 5) {
                                    drawRoundProgress(canvas);
                                }
                                if (r1.documentAttachType == 7) {
                                    i = r1.backgroundDrawableLeft;
                                    if (!r1.currentMessageObject.isOutOwner()) {
                                        if (!r1.drawPinnedBottom) {
                                            f2 = 18.0f;
                                            i += AndroidUtilities.dp(f2);
                                            i7 = (r1.layoutHeight - AndroidUtilities.dp(6.3f - ((float) (r1.drawPinnedBottom ? 2 : 0)))) - r1.timeLayout.getHeight();
                                        }
                                    }
                                    f2 = 12.0f;
                                    i += AndroidUtilities.dp(f2);
                                    if (r1.drawPinnedBottom) {
                                    }
                                    i7 = (r1.layoutHeight - AndroidUtilities.dp(6.3f - ((float) (r1.drawPinnedBottom ? 2 : 0)))) - r1.timeLayout.getHeight();
                                } else {
                                    i7 = r1.backgroundDrawableLeft + AndroidUtilities.dp(8.0f);
                                    i4 = r1.layoutHeight - AndroidUtilities.dp((float) (28 - (r1.drawPinnedBottom ? 2 : 0)));
                                    r1.rect.set((float) i7, (float) i4, (float) ((r1.timeWidthAudio + i7) + AndroidUtilities.dp(22.0f)), (float) (AndroidUtilities.dp(17.0f) + i4));
                                    canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_actionBackgroundPaint);
                                    if (z || !r1.currentMessageObject.isContentUnread()) {
                                        if (!z || MediaController.getInstance().isMessagePaused()) {
                                            r1.roundVideoPlayingDrawable.stop();
                                        } else {
                                            r1.roundVideoPlayingDrawable.start();
                                        }
                                        BaseCell.setDrawableBounds(r1.roundVideoPlayingDrawable, (r1.timeWidthAudio + i7) + AndroidUtilities.dp(f), AndroidUtilities.dp(2.3f) + i4);
                                        r1.roundVideoPlayingDrawable.draw(canvas2);
                                    } else {
                                        Theme.chat_docBackPaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
                                        canvas2.drawCircle((float) ((r1.timeWidthAudio + i7) + AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(8.3f) + i4), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                                    }
                                    i = i7 + AndroidUtilities.dp(4.0f);
                                    i7 = AndroidUtilities.dp(1.7f) + i4;
                                }
                                canvas.save();
                                canvas2.translate((float) i, (float) i7);
                                r1.durationLayout.draw(canvas2);
                                canvas.restore();
                            }
                            if (r1.currentMessageObject.type != 1) {
                                if (r1.documentAttachType == 4) {
                                    if (r1.currentMessageObject.type == 4) {
                                        if (r1.currentMessageObject.type != 16) {
                                            if (r1.currentMessageObject.isOutOwner()) {
                                                Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                                if (isDrawSelectedBackground()) {
                                                }
                                                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
                                            } else {
                                                Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                                if (isDrawSelectedBackground()) {
                                                }
                                                Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
                                            }
                                            r1.forceNotDrawTime = true;
                                            if (r1.currentMessageObject.isOutOwner()) {
                                                if (r1.isChat) {
                                                }
                                                i = AndroidUtilities.dp(25.0f);
                                            } else {
                                                i = (r1.layoutWidth - r1.backgroundWidth) + AndroidUtilities.dp(16.0f);
                                            }
                                            r1.otherX = i;
                                            if (r1.titleLayout != null) {
                                                canvas.save();
                                                canvas2.translate((float) i, (float) (AndroidUtilities.dp(12.0f) + r1.namesOffset));
                                                r1.titleLayout.draw(canvas2);
                                                canvas.restore();
                                            }
                                            if (r1.docTitleLayout != null) {
                                                canvas.save();
                                                canvas2.translate((float) (AndroidUtilities.dp(19.0f) + i), (float) (AndroidUtilities.dp(37.0f) + r1.namesOffset));
                                                r1.docTitleLayout.draw(canvas2);
                                                canvas.restore();
                                            }
                                            if (r1.currentMessageObject.isOutOwner()) {
                                                phoneCallDiscardReason = r1.currentMessageObject.messageOwner.action.reason;
                                                if (phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonMissed) {
                                                    if (phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonBusy) {
                                                        drawable = Theme.chat_msgCallDownGreenDrawable;
                                                        if (isDrawSelectedBackground()) {
                                                            if (r1.otherPressed) {
                                                                drawable3 = Theme.chat_msgInCallDrawable;
                                                            }
                                                        }
                                                        drawable3 = Theme.chat_msgInCallSelectedDrawable;
                                                    }
                                                }
                                                drawable = Theme.chat_msgCallDownRedDrawable;
                                                if (isDrawSelectedBackground()) {
                                                    if (r1.otherPressed) {
                                                        drawable3 = Theme.chat_msgInCallDrawable;
                                                    }
                                                }
                                                drawable3 = Theme.chat_msgInCallSelectedDrawable;
                                            } else {
                                                drawable = Theme.chat_msgCallUpGreenDrawable;
                                                if (isDrawSelectedBackground()) {
                                                    if (r1.otherPressed) {
                                                        drawable3 = Theme.chat_msgOutCallDrawable;
                                                    }
                                                }
                                                drawable3 = Theme.chat_msgOutCallSelectedDrawable;
                                            }
                                            BaseCell.setDrawableBounds(drawable, i - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(36.0f) + r1.namesOffset);
                                            drawable.draw(canvas2);
                                            i += AndroidUtilities.dp(205.0f);
                                            i7 = AndroidUtilities.dp(22.0f);
                                            r1.otherY = i7;
                                            BaseCell.setDrawableBounds(drawable3, i, i7);
                                            drawable3.draw(canvas2);
                                        } else if (r1.currentMessageObject.type == 12) {
                                            if (r1.currentMessageObject.isOutOwner()) {
                                            }
                                            Theme.chat_contactNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outContactNameText : Theme.key_chat_inContactNameText));
                                            if (r1.currentMessageObject.isOutOwner()) {
                                            }
                                            Theme.chat_contactPhonePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outContactPhoneText : Theme.key_chat_inContactPhoneText));
                                            if (r1.titleLayout != null) {
                                                canvas.save();
                                                canvas2.translate((float) ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(16.0f) + r1.namesOffset));
                                                r1.titleLayout.draw(canvas2);
                                                canvas.restore();
                                            }
                                            if (r1.docTitleLayout != null) {
                                                canvas.save();
                                                canvas2.translate((float) ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(39.0f) + r1.namesOffset));
                                                r1.docTitleLayout.draw(canvas2);
                                                canvas.restore();
                                            }
                                            if (r1.currentMessageObject.isOutOwner()) {
                                                if (isDrawSelectedBackground()) {
                                                }
                                            }
                                            i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(48.0f);
                                            r1.otherX = i7;
                                            i4 = r1.photoImage.getImageY() - AndroidUtilities.dp(5.0f);
                                            r1.otherY = i4;
                                            BaseCell.setDrawableBounds(drawable2, i7, i4);
                                            drawable2.draw(canvas2);
                                        }
                                    } else if (r1.docTitleLayout != null) {
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            if (r1.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                                Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_inVenueNameText));
                                            } else {
                                                Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                            }
                                            if (isDrawSelectedBackground()) {
                                            }
                                            Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inVenueInfoSelectedText : Theme.key_chat_inVenueInfoText));
                                        } else {
                                            if (r1.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                                Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_outVenueNameText));
                                            } else {
                                                Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                            }
                                            if (isDrawSelectedBackground()) {
                                            }
                                            Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outVenueInfoSelectedText : Theme.key_chat_outVenueInfoText));
                                        }
                                        if (r1.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                            canvas.save();
                                            canvas2.translate((float) (((r1.docTitleOffsetX + r1.photoImage.getImageX()) + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) (r1.photoImage.getImageY() + AndroidUtilities.dp(8.0f)));
                                            r1.docTitleLayout.draw(canvas2);
                                            canvas.restore();
                                            if (r1.infoLayout != null) {
                                                canvas.save();
                                                canvas2.translate((float) ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) ((r1.photoImage.getImageY() + r1.docTitleLayout.getLineBottom(r1.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f)));
                                                r1.infoLayout.draw(canvas2);
                                                canvas.restore();
                                            }
                                        } else {
                                            i3 = r1.photoImage.getImageY2() + AndroidUtilities.dp(30.0f);
                                            if (r1.locationExpired) {
                                                r1.forceNotDrawTime = true;
                                                abs = 1.0f - (((float) Math.abs(ConnectionsManager.getInstance(r1.currentAccount).getCurrentTime() - r1.currentMessageObject.messageOwner.date)) / ((float) r1.currentMessageObject.messageOwner.media.period));
                                                r1.rect.set((float) (r1.photoImage.getImageX2() - AndroidUtilities.dp(43.0f)), (float) (i3 - AndroidUtilities.dp(15.0f)), (float) (r1.photoImage.getImageX2() - AndroidUtilities.dp(13.0f)), (float) (AndroidUtilities.dp(15.0f) + i3));
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                    Theme.chat_radialProgress2Paint.setColor(Theme.getColor(Theme.key_chat_inInstant));
                                                    Theme.chat_livePaint.setColor(Theme.getColor(Theme.key_chat_inInstant));
                                                } else {
                                                    Theme.chat_radialProgress2Paint.setColor(Theme.getColor(Theme.key_chat_outInstant));
                                                    Theme.chat_livePaint.setColor(Theme.getColor(Theme.key_chat_outInstant));
                                                }
                                                Theme.chat_radialProgress2Paint.setAlpha(50);
                                                canvas2.drawCircle(r1.rect.centerX(), r1.rect.centerY(), (float) AndroidUtilities.dp(15.0f), Theme.chat_radialProgress2Paint);
                                                Theme.chat_radialProgress2Paint.setAlpha(255);
                                                canvas2.drawArc(r1.rect, -90.0f, abs * -360.0f, false, Theme.chat_radialProgress2Paint);
                                                formatLocationLeftTime = LocaleController.formatLocationLeftTime(Math.abs(r1.currentMessageObject.messageOwner.media.period - (ConnectionsManager.getInstance(r1.currentAccount).getCurrentTime() - r1.currentMessageObject.messageOwner.date)));
                                                canvas2.drawText(formatLocationLeftTime, r1.rect.centerX() - (Theme.chat_livePaint.measureText(formatLocationLeftTime) / 2.0f), (float) (i3 + AndroidUtilities.dp(4.0f)), Theme.chat_livePaint);
                                                canvas.save();
                                                canvas2.translate((float) (r1.photoImage.getImageX() + AndroidUtilities.dp(10.0f)), (float) (r1.photoImage.getImageY2() + AndroidUtilities.dp(10.0f)));
                                                r1.docTitleLayout.draw(canvas2);
                                                canvas2.translate(0.0f, (float) AndroidUtilities.dp(23.0f));
                                                r1.infoLayout.draw(canvas2);
                                                canvas.restore();
                                            }
                                            i = (r1.photoImage.getImageX() + (r1.photoImage.getImageWidth() / 2)) - AndroidUtilities.dp(31.0f);
                                            i7 = (r1.photoImage.getImageY() + (r1.photoImage.getImageHeight() / 2)) - AndroidUtilities.dp(38.0f);
                                            BaseCell.setDrawableBounds(Theme.chat_msgAvatarLiveLocationDrawable, i, i7);
                                            Theme.chat_msgAvatarLiveLocationDrawable.draw(canvas2);
                                            r1.locationImageReceiver.setImageCoords(i + AndroidUtilities.dp(5.0f), i7 + AndroidUtilities.dp(5.0f), AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
                                            r1.locationImageReceiver.draw(canvas2);
                                        }
                                    }
                                    if (r1.captionLayout == null) {
                                        f2 = 17.0f;
                                    } else if (r1.currentMessageObject.type != 8) {
                                        f2 = 17.0f;
                                        r1.captionX = (r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f)) + r1.captionOffsetX;
                                        r1.captionY = (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()) + AndroidUtilities.dp(f);
                                    } else if (r1.hasOldCaptionPreview) {
                                        if (r1.currentMessageObject.isOutOwner()) {
                                        }
                                        r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                                        if (r1.drawPinnedTop) {
                                        }
                                        f2 = 17.0f;
                                        r1.captionY = (((r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
                                    } else {
                                        f2 = 17.0f;
                                        if (r1.currentMessageObject.isOutOwner()) {
                                        }
                                        r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                                        if (r1.drawPinnedTop) {
                                        }
                                        r1.captionY = (r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f);
                                    }
                                    if (r1.currentPosition != null) {
                                        z3 = false;
                                        drawCaptionLayout(canvas2, false);
                                    } else {
                                        z3 = false;
                                    }
                                    if (r1.hasOldCaptionPreview) {
                                        z = true;
                                    } else if (r1.currentMessageObject.type == 8) {
                                        i = r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f);
                                        i5 = i;
                                        if (r1.drawPinnedTop) {
                                        }
                                        dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                                        if (r1.currentMessageObject.isOutOwner()) {
                                        }
                                        Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                                        canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                                        if (r1.siteNameLayout == null) {
                                            if (r1.currentMessageObject.isOutOwner()) {
                                            }
                                            Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                                            canvas.save();
                                            if (r1.siteNameRtl) {
                                            }
                                            canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                                            r1.siteNameLayout.draw(canvas2);
                                            canvas.restore();
                                            i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                                        } else {
                                            i = dp;
                                        }
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                        } else {
                                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                        }
                                        if (r1.descriptionLayout != null) {
                                            if (i != dp) {
                                                i += AndroidUtilities.dp(2.0f);
                                            }
                                            r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                                            canvas.save();
                                            canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                                            r1.descriptionLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                        z = true;
                                        r1.drawTime = true;
                                    } else {
                                        i = r1.backgroundDrawableLeft;
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            f2 = 11.0f;
                                        }
                                        i += AndroidUtilities.dp(f2);
                                        i5 = i;
                                        if (r1.drawPinnedTop) {
                                        }
                                        dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                                        if (r1.currentMessageObject.isOutOwner()) {
                                        }
                                        Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                                        canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                                        if (r1.siteNameLayout == null) {
                                            i = dp;
                                        } else {
                                            if (r1.currentMessageObject.isOutOwner()) {
                                            }
                                            Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                                            canvas.save();
                                            if (r1.siteNameRtl) {
                                                if (r1.hasInvoicePreview) {
                                                }
                                            }
                                            canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                                            r1.siteNameLayout.draw(canvas2);
                                            canvas.restore();
                                            i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                                        }
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                        } else {
                                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                        }
                                        if (r1.descriptionLayout != null) {
                                            if (i != dp) {
                                                i += AndroidUtilities.dp(2.0f);
                                            }
                                            r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                                            canvas.save();
                                            canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                                            r1.descriptionLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                        z = true;
                                        r1.drawTime = true;
                                    }
                                    if (r1.documentAttachType == z) {
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_outFileNameText));
                                            if (isDrawSelectedBackground()) {
                                            }
                                            Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileInfoSelectedText : Theme.key_chat_outFileInfoText));
                                            if (isDrawSelectedBackground()) {
                                            }
                                            Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileBackgroundSelected : Theme.key_chat_outFileBackground));
                                            if (isDrawSelectedBackground()) {
                                            }
                                        } else {
                                            Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_inFileNameText));
                                            if (isDrawSelectedBackground()) {
                                            }
                                            Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileInfoSelectedText : Theme.key_chat_inFileInfoText));
                                            if (isDrawSelectedBackground()) {
                                            }
                                            Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileBackgroundSelected : Theme.key_chat_inFileBackground));
                                            if (isDrawSelectedBackground()) {
                                            }
                                        }
                                        if (r1.drawPhotoImage) {
                                            if (r1.currentMessageObject.type != 0) {
                                                i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(56.0f);
                                                r1.otherX = i7;
                                                i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                                r1.otherY = i4;
                                                BaseCell.setDrawableBounds(drawable2, i7, i4);
                                            } else {
                                                i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(40.0f);
                                                r1.otherX = i7;
                                                i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                                r1.otherY = i4;
                                                BaseCell.setDrawableBounds(drawable2, i7, i4);
                                            }
                                            i7 = (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
                                            i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                                            alpha = (r1.photoImage.getImageY() + r1.docTitleLayout.getLineBottom(r1.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f);
                                            if (z2) {
                                                i6 = r1.buttonState;
                                                if (r1.buttonState != 0) {
                                                    if (r1.currentMessageObject.isOutOwner()) {
                                                    }
                                                } else if (r1.buttonState == 1) {
                                                    if (r1.currentMessageObject.isOutOwner()) {
                                                    }
                                                }
                                                radialProgress2 = r1.radialProgress;
                                                drawableArr = Theme.chat_photoStatesDrawables[i6];
                                                if (isDrawSelectedBackground()) {
                                                    if (r1.buttonPressed != 0) {
                                                        i5 = z3;
                                                        radialProgress2.swapBackground(drawableArr[i5]);
                                                    }
                                                }
                                                i5 = 1;
                                                radialProgress2.swapBackground(drawableArr[i5]);
                                            } else {
                                                r1.radialProgress.swapBackground(Theme.chat_photoStatesDrawables[r1.buttonState][r1.buttonPressed]);
                                            }
                                            if (z2) {
                                                r1.rect.set((float) r1.photoImage.getImageX(), (float) r1.photoImage.getImageY(), (float) (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()), (float) (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()));
                                                canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                    if (isDrawSelectedBackground()) {
                                                    }
                                                    r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileProgressSelected : Theme.key_chat_outFileProgress));
                                                } else {
                                                    if (isDrawSelectedBackground()) {
                                                    }
                                                    r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileProgressSelected : Theme.key_chat_inFileProgress));
                                                }
                                            } else {
                                                if (r1.buttonState == -1) {
                                                    r1.radialProgress.setHideCurrentDrawable(true);
                                                }
                                                r1.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
                                            }
                                        } else {
                                            if (r1.currentMessageObject.type != 0) {
                                            }
                                            i7 = (r1.buttonX + r1.backgroundWidth) - AndroidUtilities.dp(r1.currentMessageObject.type != 0 ? 58.0f : 48.0f);
                                            r1.otherX = i7;
                                            i4 = r1.buttonY - AndroidUtilities.dp(5.0f);
                                            r1.otherY = i4;
                                            BaseCell.setDrawableBounds(drawable2, i7, i4);
                                            i7 = r1.buttonX + AndroidUtilities.dp(53.0f);
                                            i4 = r1.buttonY + AndroidUtilities.dp(4.0f);
                                            alpha = r1.buttonY + AndroidUtilities.dp(27.0f);
                                            if (r1.currentMessageObject.isOutOwner()) {
                                                radialProgress = r1.radialProgress;
                                                if (isDrawSelectedBackground()) {
                                                    if (r1.buttonPressed != 0) {
                                                        str = Theme.key_chat_outAudioProgress;
                                                        radialProgress.setProgressColor(Theme.getColor(str));
                                                    }
                                                }
                                                str = Theme.key_chat_outAudioSelectedProgress;
                                                radialProgress.setProgressColor(Theme.getColor(str));
                                            } else {
                                                radialProgress = r1.radialProgress;
                                                if (isDrawSelectedBackground()) {
                                                    if (r1.buttonPressed != 0) {
                                                        str = Theme.key_chat_inAudioProgress;
                                                        radialProgress.setProgressColor(Theme.getColor(str));
                                                    }
                                                }
                                                str = Theme.key_chat_inAudioSelectedProgress;
                                                radialProgress.setProgressColor(Theme.getColor(str));
                                            }
                                        }
                                        drawable2.draw(canvas2);
                                        if (r1.docTitleLayout != null) {
                                            canvas.save();
                                            canvas2.translate((float) (r1.docTitleOffsetX + i7), (float) i4);
                                            r1.docTitleLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                        if (r1.infoLayout != null) {
                                            canvas.save();
                                            canvas2.translate((float) i7, (float) alpha);
                                            r1.infoLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                    }
                                    if (r1.controlsAlpha != 1.0f) {
                                        r1.radialProgress.setOverrideAlpha(r1.controlsAlpha);
                                    }
                                    r1.radialProgress.draw(canvas2);
                                    if (!r1.botButtons.isEmpty()) {
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            i = (getMeasuredWidth() - r1.widthForButtons) - AndroidUtilities.dp(10.0f);
                                        } else {
                                            if (r1.mediaBackground) {
                                            }
                                            i = r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.mediaBackground ? 1.0f : 7.0f);
                                        }
                                        i2 = i;
                                        i5 = z3;
                                        while (i5 < r1.botButtons.size()) {
                                            botButton = (BotButton) r1.botButtons.get(i5);
                                            i = (botButton.f10y + r1.layoutHeight) - AndroidUtilities.dp(2.0f);
                                            if (i5 != r1.pressedBotButton) {
                                            }
                                            Theme.chat_systemDrawable.setColorFilter(i5 != r1.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                                            Theme.chat_systemDrawable.setBounds(botButton.f9x + i2, i, (botButton.f9x + i2) + botButton.width, botButton.height + i);
                                            Theme.chat_systemDrawable.draw(canvas2);
                                            canvas.save();
                                            canvas2.translate((float) ((botButton.f9x + i2) + AndroidUtilities.dp(max)), (float) (((AndroidUtilities.dp(44.0f) - botButton.title.getLineBottom(botButton.title.getLineCount() - 1)) / 2) + i));
                                            botButton.title.draw(canvas2);
                                            canvas.restore();
                                            if (botButton.button instanceof TL_keyboardButtonUrl) {
                                                BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                                Theme.chat_botLinkDrawalbe.draw(canvas2);
                                            } else {
                                                if (botButton.button instanceof TL_keyboardButtonSwitchInline) {
                                                    f6 = 3.0f;
                                                    BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                                    Theme.chat_botInlineDrawable.draw(canvas2);
                                                } else {
                                                    f6 = 3.0f;
                                                    if (botButton.button instanceof TL_keyboardButtonRequestGeoLocation) {
                                                    }
                                                    z4 = z3;
                                                    Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (botButton.progressAlpha * 255.0f)));
                                                    i7 = ((botButton.f9x + botButton.width) - AndroidUtilities.dp(12.0f)) + i2;
                                                    r1.rect.set((float) i7, (float) (i + AndroidUtilities.dp(4.0f)), (float) (i7 + AndroidUtilities.dp(8.0f)), (float) (i + AndroidUtilities.dp(12.0f)));
                                                    canvas2.drawArc(r1.rect, (float) botButton.angle, 220.0f, false, Theme.chat_botProgressPaint);
                                                    invalidate(((int) r1.rect.left) - AndroidUtilities.dp(2.0f), ((int) r1.rect.top) - AndroidUtilities.dp(2.0f), ((int) r1.rect.right) + AndroidUtilities.dp(2.0f), ((int) r1.rect.bottom) + AndroidUtilities.dp(2.0f));
                                                    currentTimeMillis = System.currentTimeMillis();
                                                    if (Math.abs(botButton.lastUpdateTime - System.currentTimeMillis()) >= 1000) {
                                                        access$1300 = currentTimeMillis - botButton.lastUpdateTime;
                                                        botButton.angle = (int) (((float) botButton.angle) + (((float) (360 * access$1300)) / 2000.0f));
                                                        botButton.angle = botButton.angle - (360 * (botButton.angle / 360));
                                                        if (z4) {
                                                            if (botButton.progressAlpha > 0.0f) {
                                                                botButton.progressAlpha = botButton.progressAlpha - (((float) access$1300) / 200.0f);
                                                                if (botButton.progressAlpha < 0.0f) {
                                                                    botButton.progressAlpha = 0.0f;
                                                                }
                                                            }
                                                            botButton.lastUpdateTime = currentTimeMillis;
                                                        } else if (botButton.progressAlpha < 1.0f) {
                                                            botButton.progressAlpha = botButton.progressAlpha + (((float) access$1300) / 200.0f);
                                                            if (botButton.progressAlpha > 1.0f) {
                                                                botButton.progressAlpha = 1.0f;
                                                            }
                                                        }
                                                    }
                                                    botButton.lastUpdateTime = currentTimeMillis;
                                                }
                                                f3 = f6;
                                            }
                                            i5++;
                                            max = 5.0f;
                                            z3 = false;
                                        }
                                    }
                                }
                            }
                            if (r1.photoImage.getVisible()) {
                                i = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                                Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) i) * r1.controlsAlpha));
                                drawable = Theme.chat_msgMediaMenuDrawable;
                                i4 = (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                                r1.otherX = i4;
                                alpha = r1.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                                r1.otherY = alpha;
                                BaseCell.setDrawableBounds(drawable, i4, alpha);
                                Theme.chat_msgMediaMenuDrawable.draw(canvas2);
                                Theme.chat_msgMediaMenuDrawable.setAlpha(i);
                                Theme.chat_infoPaint.setColor(Theme.getColor(Theme.key_chat_mediaInfoText));
                                i = r1.photoImage.getImageX() + AndroidUtilities.dp(4.0f);
                                i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(4.0f);
                                r1.rect.set((float) i, (float) i4, (float) ((i + r1.infoWidth) + AndroidUtilities.dp(8.0f)), (float) (i4 + AndroidUtilities.dp(16.5f)));
                                i = Theme.chat_timeBackgroundPaint.getAlpha();
                                Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) i) * r1.controlsAlpha));
                                canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                                Theme.chat_timeBackgroundPaint.setAlpha(i);
                                canvas.save();
                                canvas2.translate((float) (r1.photoImage.getImageX() + AndroidUtilities.dp(8.0f)), (float) (r1.photoImage.getImageY() + AndroidUtilities.dp(5.5f)));
                                Theme.chat_infoPaint.setAlpha((int) (255.0f * r1.controlsAlpha));
                                r1.infoLayout.draw(canvas2);
                                canvas.restore();
                                Theme.chat_infoPaint.setAlpha(255);
                            }
                            if (r1.captionLayout == null) {
                                f2 = 17.0f;
                            } else if (r1.currentMessageObject.type != 8) {
                                f2 = 17.0f;
                                r1.captionX = (r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f)) + r1.captionOffsetX;
                                r1.captionY = (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()) + AndroidUtilities.dp(f);
                            } else if (r1.hasOldCaptionPreview) {
                                f2 = 17.0f;
                                if (r1.currentMessageObject.isOutOwner()) {
                                }
                                r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                                if (r1.drawPinnedTop) {
                                }
                                r1.captionY = (r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f);
                            } else {
                                if (r1.currentMessageObject.isOutOwner()) {
                                }
                                r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                                if (r1.drawPinnedTop) {
                                }
                                f2 = 17.0f;
                                r1.captionY = (((r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
                            }
                            if (r1.currentPosition != null) {
                                z3 = false;
                            } else {
                                z3 = false;
                                drawCaptionLayout(canvas2, false);
                            }
                            if (r1.hasOldCaptionPreview) {
                                z = true;
                            } else if (r1.currentMessageObject.type == 8) {
                                i = r1.backgroundDrawableLeft;
                                if (r1.currentMessageObject.isOutOwner()) {
                                    f2 = 11.0f;
                                }
                                i += AndroidUtilities.dp(f2);
                                i5 = i;
                                if (r1.drawPinnedTop) {
                                }
                                dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                                if (r1.currentMessageObject.isOutOwner()) {
                                }
                                Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                                canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                                if (r1.siteNameLayout == null) {
                                    if (r1.currentMessageObject.isOutOwner()) {
                                    }
                                    Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                                    canvas.save();
                                    if (r1.siteNameRtl) {
                                    }
                                    canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                                    r1.siteNameLayout.draw(canvas2);
                                    canvas.restore();
                                    i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                                } else {
                                    i = dp;
                                }
                                if (r1.currentMessageObject.isOutOwner()) {
                                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                } else {
                                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                }
                                if (r1.descriptionLayout != null) {
                                    if (i != dp) {
                                        i += AndroidUtilities.dp(2.0f);
                                    }
                                    r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                                    canvas.save();
                                    canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                                    r1.descriptionLayout.draw(canvas2);
                                    canvas.restore();
                                }
                                z = true;
                                r1.drawTime = true;
                            } else {
                                i = r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f);
                                i5 = i;
                                if (r1.drawPinnedTop) {
                                }
                                dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                                if (r1.currentMessageObject.isOutOwner()) {
                                }
                                Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                                canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                                if (r1.siteNameLayout == null) {
                                    i = dp;
                                } else {
                                    if (r1.currentMessageObject.isOutOwner()) {
                                    }
                                    Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                                    canvas.save();
                                    if (r1.siteNameRtl) {
                                        if (r1.hasInvoicePreview) {
                                        }
                                    }
                                    canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                                    r1.siteNameLayout.draw(canvas2);
                                    canvas.restore();
                                    i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                                }
                                if (r1.currentMessageObject.isOutOwner()) {
                                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                } else {
                                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                }
                                if (r1.descriptionLayout != null) {
                                    if (i != dp) {
                                        i += AndroidUtilities.dp(2.0f);
                                    }
                                    r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                                    canvas.save();
                                    canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                                    r1.descriptionLayout.draw(canvas2);
                                    canvas.restore();
                                }
                                z = true;
                                r1.drawTime = true;
                            }
                            if (r1.documentAttachType == z) {
                                if (r1.currentMessageObject.isOutOwner()) {
                                    Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_inFileNameText));
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileInfoSelectedText : Theme.key_chat_inFileInfoText));
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileBackgroundSelected : Theme.key_chat_inFileBackground));
                                    if (isDrawSelectedBackground()) {
                                    }
                                } else {
                                    Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_outFileNameText));
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileInfoSelectedText : Theme.key_chat_outFileInfoText));
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileBackgroundSelected : Theme.key_chat_outFileBackground));
                                    if (isDrawSelectedBackground()) {
                                    }
                                }
                                if (r1.drawPhotoImage) {
                                    if (r1.currentMessageObject.type != 0) {
                                    }
                                    i7 = (r1.buttonX + r1.backgroundWidth) - AndroidUtilities.dp(r1.currentMessageObject.type != 0 ? 58.0f : 48.0f);
                                    r1.otherX = i7;
                                    i4 = r1.buttonY - AndroidUtilities.dp(5.0f);
                                    r1.otherY = i4;
                                    BaseCell.setDrawableBounds(drawable2, i7, i4);
                                    i7 = r1.buttonX + AndroidUtilities.dp(53.0f);
                                    i4 = r1.buttonY + AndroidUtilities.dp(4.0f);
                                    alpha = r1.buttonY + AndroidUtilities.dp(27.0f);
                                    if (r1.currentMessageObject.isOutOwner()) {
                                        radialProgress = r1.radialProgress;
                                        if (isDrawSelectedBackground()) {
                                            if (r1.buttonPressed != 0) {
                                                str = Theme.key_chat_inAudioProgress;
                                                radialProgress.setProgressColor(Theme.getColor(str));
                                            }
                                        }
                                        str = Theme.key_chat_inAudioSelectedProgress;
                                        radialProgress.setProgressColor(Theme.getColor(str));
                                    } else {
                                        radialProgress = r1.radialProgress;
                                        if (isDrawSelectedBackground()) {
                                            if (r1.buttonPressed != 0) {
                                                str = Theme.key_chat_outAudioProgress;
                                                radialProgress.setProgressColor(Theme.getColor(str));
                                            }
                                        }
                                        str = Theme.key_chat_outAudioSelectedProgress;
                                        radialProgress.setProgressColor(Theme.getColor(str));
                                    }
                                } else {
                                    if (r1.currentMessageObject.type != 0) {
                                        i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(40.0f);
                                        r1.otherX = i7;
                                        i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                        r1.otherY = i4;
                                        BaseCell.setDrawableBounds(drawable2, i7, i4);
                                    } else {
                                        i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(56.0f);
                                        r1.otherX = i7;
                                        i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                        r1.otherY = i4;
                                        BaseCell.setDrawableBounds(drawable2, i7, i4);
                                    }
                                    i7 = (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
                                    i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                                    alpha = (r1.photoImage.getImageY() + r1.docTitleLayout.getLineBottom(r1.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f);
                                    if (z2) {
                                        r1.radialProgress.swapBackground(Theme.chat_photoStatesDrawables[r1.buttonState][r1.buttonPressed]);
                                    } else {
                                        i6 = r1.buttonState;
                                        if (r1.buttonState != 0) {
                                            if (r1.buttonState == 1) {
                                                if (r1.currentMessageObject.isOutOwner()) {
                                                }
                                            }
                                        } else if (r1.currentMessageObject.isOutOwner()) {
                                        }
                                        radialProgress2 = r1.radialProgress;
                                        drawableArr = Theme.chat_photoStatesDrawables[i6];
                                        if (isDrawSelectedBackground()) {
                                            if (r1.buttonPressed != 0) {
                                                i5 = z3;
                                                radialProgress2.swapBackground(drawableArr[i5]);
                                            }
                                        }
                                        i5 = 1;
                                        radialProgress2.swapBackground(drawableArr[i5]);
                                    }
                                    if (z2) {
                                        if (r1.buttonState == -1) {
                                            r1.radialProgress.setHideCurrentDrawable(true);
                                        }
                                        r1.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
                                    } else {
                                        r1.rect.set((float) r1.photoImage.getImageX(), (float) r1.photoImage.getImageY(), (float) (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()), (float) (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()));
                                        canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            if (isDrawSelectedBackground()) {
                                            }
                                            r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileProgressSelected : Theme.key_chat_inFileProgress));
                                        } else {
                                            if (isDrawSelectedBackground()) {
                                            }
                                            r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileProgressSelected : Theme.key_chat_outFileProgress));
                                        }
                                    }
                                }
                                drawable2.draw(canvas2);
                                if (r1.docTitleLayout != null) {
                                    canvas.save();
                                    canvas2.translate((float) (r1.docTitleOffsetX + i7), (float) i4);
                                    r1.docTitleLayout.draw(canvas2);
                                    canvas.restore();
                                }
                                if (r1.infoLayout != null) {
                                    canvas.save();
                                    canvas2.translate((float) i7, (float) alpha);
                                    r1.infoLayout.draw(canvas2);
                                    canvas.restore();
                                }
                            }
                            if (r1.controlsAlpha != 1.0f) {
                                r1.radialProgress.setOverrideAlpha(r1.controlsAlpha);
                            }
                            r1.radialProgress.draw(canvas2);
                            if (!r1.botButtons.isEmpty()) {
                                if (r1.currentMessageObject.isOutOwner()) {
                                    if (r1.mediaBackground) {
                                    }
                                    i = r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.mediaBackground ? 1.0f : 7.0f);
                                } else {
                                    i = (getMeasuredWidth() - r1.widthForButtons) - AndroidUtilities.dp(10.0f);
                                }
                                i2 = i;
                                i5 = z3;
                                while (i5 < r1.botButtons.size()) {
                                    botButton = (BotButton) r1.botButtons.get(i5);
                                    i = (botButton.f10y + r1.layoutHeight) - AndroidUtilities.dp(2.0f);
                                    if (i5 != r1.pressedBotButton) {
                                    }
                                    Theme.chat_systemDrawable.setColorFilter(i5 != r1.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                                    Theme.chat_systemDrawable.setBounds(botButton.f9x + i2, i, (botButton.f9x + i2) + botButton.width, botButton.height + i);
                                    Theme.chat_systemDrawable.draw(canvas2);
                                    canvas.save();
                                    canvas2.translate((float) ((botButton.f9x + i2) + AndroidUtilities.dp(max)), (float) (((AndroidUtilities.dp(44.0f) - botButton.title.getLineBottom(botButton.title.getLineCount() - 1)) / 2) + i));
                                    botButton.title.draw(canvas2);
                                    canvas.restore();
                                    if (botButton.button instanceof TL_keyboardButtonUrl) {
                                        if (botButton.button instanceof TL_keyboardButtonSwitchInline) {
                                            f6 = 3.0f;
                                            if (botButton.button instanceof TL_keyboardButtonRequestGeoLocation) {
                                            }
                                            z4 = z3;
                                            Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (botButton.progressAlpha * 255.0f)));
                                            i7 = ((botButton.f9x + botButton.width) - AndroidUtilities.dp(12.0f)) + i2;
                                            r1.rect.set((float) i7, (float) (i + AndroidUtilities.dp(4.0f)), (float) (i7 + AndroidUtilities.dp(8.0f)), (float) (i + AndroidUtilities.dp(12.0f)));
                                            canvas2.drawArc(r1.rect, (float) botButton.angle, 220.0f, false, Theme.chat_botProgressPaint);
                                            invalidate(((int) r1.rect.left) - AndroidUtilities.dp(2.0f), ((int) r1.rect.top) - AndroidUtilities.dp(2.0f), ((int) r1.rect.right) + AndroidUtilities.dp(2.0f), ((int) r1.rect.bottom) + AndroidUtilities.dp(2.0f));
                                            currentTimeMillis = System.currentTimeMillis();
                                            if (Math.abs(botButton.lastUpdateTime - System.currentTimeMillis()) >= 1000) {
                                                access$1300 = currentTimeMillis - botButton.lastUpdateTime;
                                                botButton.angle = (int) (((float) botButton.angle) + (((float) (360 * access$1300)) / 2000.0f));
                                                botButton.angle = botButton.angle - (360 * (botButton.angle / 360));
                                                if (z4) {
                                                    if (botButton.progressAlpha > 0.0f) {
                                                        botButton.progressAlpha = botButton.progressAlpha - (((float) access$1300) / 200.0f);
                                                        if (botButton.progressAlpha < 0.0f) {
                                                            botButton.progressAlpha = 0.0f;
                                                        }
                                                    }
                                                    botButton.lastUpdateTime = currentTimeMillis;
                                                } else if (botButton.progressAlpha < 1.0f) {
                                                    botButton.progressAlpha = botButton.progressAlpha + (((float) access$1300) / 200.0f);
                                                    if (botButton.progressAlpha > 1.0f) {
                                                        botButton.progressAlpha = 1.0f;
                                                    }
                                                }
                                            }
                                            botButton.lastUpdateTime = currentTimeMillis;
                                        } else {
                                            f6 = 3.0f;
                                            BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                            Theme.chat_botInlineDrawable.draw(canvas2);
                                        }
                                        f3 = f6;
                                    } else {
                                        BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                        Theme.chat_botLinkDrawalbe.draw(canvas2);
                                    }
                                    i5++;
                                    max = 5.0f;
                                    z3 = false;
                                }
                            }
                        }
                    }
                    if (!(!r1.photoImage.getVisible() || r1.hasGamePreview || r1.currentMessageObject.needDrawBluredPreview())) {
                        i = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                        Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) i) * r1.controlsAlpha));
                        drawable = Theme.chat_msgMediaMenuDrawable;
                        i4 = (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                        r1.otherX = i4;
                        alpha = r1.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                        r1.otherY = alpha;
                        BaseCell.setDrawableBounds(drawable, i4, alpha);
                        Theme.chat_msgMediaMenuDrawable.draw(canvas2);
                        Theme.chat_msgMediaMenuDrawable.setAlpha(i);
                    }
                    if (r1.currentMessageObject.type != 1) {
                        if (r1.documentAttachType == 4) {
                            if (r1.currentMessageObject.type == 4) {
                                if (r1.docTitleLayout != null) {
                                    if (r1.currentMessageObject.isOutOwner()) {
                                        if (r1.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                            Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                        } else {
                                            Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_outVenueNameText));
                                        }
                                        if (isDrawSelectedBackground()) {
                                        }
                                        Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outVenueInfoSelectedText : Theme.key_chat_outVenueInfoText));
                                    } else {
                                        if (r1.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                            Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                        } else {
                                            Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_inVenueNameText));
                                        }
                                        if (isDrawSelectedBackground()) {
                                        }
                                        Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inVenueInfoSelectedText : Theme.key_chat_inVenueInfoText));
                                    }
                                    if (r1.currentMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                        i3 = r1.photoImage.getImageY2() + AndroidUtilities.dp(30.0f);
                                        if (r1.locationExpired) {
                                            r1.forceNotDrawTime = true;
                                            abs = 1.0f - (((float) Math.abs(ConnectionsManager.getInstance(r1.currentAccount).getCurrentTime() - r1.currentMessageObject.messageOwner.date)) / ((float) r1.currentMessageObject.messageOwner.media.period));
                                            r1.rect.set((float) (r1.photoImage.getImageX2() - AndroidUtilities.dp(43.0f)), (float) (i3 - AndroidUtilities.dp(15.0f)), (float) (r1.photoImage.getImageX2() - AndroidUtilities.dp(13.0f)), (float) (AndroidUtilities.dp(15.0f) + i3));
                                            if (r1.currentMessageObject.isOutOwner()) {
                                                Theme.chat_radialProgress2Paint.setColor(Theme.getColor(Theme.key_chat_outInstant));
                                                Theme.chat_livePaint.setColor(Theme.getColor(Theme.key_chat_outInstant));
                                            } else {
                                                Theme.chat_radialProgress2Paint.setColor(Theme.getColor(Theme.key_chat_inInstant));
                                                Theme.chat_livePaint.setColor(Theme.getColor(Theme.key_chat_inInstant));
                                            }
                                            Theme.chat_radialProgress2Paint.setAlpha(50);
                                            canvas2.drawCircle(r1.rect.centerX(), r1.rect.centerY(), (float) AndroidUtilities.dp(15.0f), Theme.chat_radialProgress2Paint);
                                            Theme.chat_radialProgress2Paint.setAlpha(255);
                                            canvas2.drawArc(r1.rect, -90.0f, abs * -360.0f, false, Theme.chat_radialProgress2Paint);
                                            formatLocationLeftTime = LocaleController.formatLocationLeftTime(Math.abs(r1.currentMessageObject.messageOwner.media.period - (ConnectionsManager.getInstance(r1.currentAccount).getCurrentTime() - r1.currentMessageObject.messageOwner.date)));
                                            canvas2.drawText(formatLocationLeftTime, r1.rect.centerX() - (Theme.chat_livePaint.measureText(formatLocationLeftTime) / 2.0f), (float) (i3 + AndroidUtilities.dp(4.0f)), Theme.chat_livePaint);
                                            canvas.save();
                                            canvas2.translate((float) (r1.photoImage.getImageX() + AndroidUtilities.dp(10.0f)), (float) (r1.photoImage.getImageY2() + AndroidUtilities.dp(10.0f)));
                                            r1.docTitleLayout.draw(canvas2);
                                            canvas2.translate(0.0f, (float) AndroidUtilities.dp(23.0f));
                                            r1.infoLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                        i = (r1.photoImage.getImageX() + (r1.photoImage.getImageWidth() / 2)) - AndroidUtilities.dp(31.0f);
                                        i7 = (r1.photoImage.getImageY() + (r1.photoImage.getImageHeight() / 2)) - AndroidUtilities.dp(38.0f);
                                        BaseCell.setDrawableBounds(Theme.chat_msgAvatarLiveLocationDrawable, i, i7);
                                        Theme.chat_msgAvatarLiveLocationDrawable.draw(canvas2);
                                        r1.locationImageReceiver.setImageCoords(i + AndroidUtilities.dp(5.0f), i7 + AndroidUtilities.dp(5.0f), AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
                                        r1.locationImageReceiver.draw(canvas2);
                                    } else {
                                        canvas.save();
                                        canvas2.translate((float) (((r1.docTitleOffsetX + r1.photoImage.getImageX()) + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) (r1.photoImage.getImageY() + AndroidUtilities.dp(8.0f)));
                                        r1.docTitleLayout.draw(canvas2);
                                        canvas.restore();
                                        if (r1.infoLayout != null) {
                                            canvas.save();
                                            canvas2.translate((float) ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) ((r1.photoImage.getImageY() + r1.docTitleLayout.getLineBottom(r1.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f)));
                                            r1.infoLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                    }
                                }
                            } else if (r1.currentMessageObject.type != 16) {
                                if (r1.currentMessageObject.isOutOwner()) {
                                    Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
                                } else {
                                    Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
                                }
                                r1.forceNotDrawTime = true;
                                if (r1.currentMessageObject.isOutOwner()) {
                                    i = (r1.layoutWidth - r1.backgroundWidth) + AndroidUtilities.dp(16.0f);
                                } else {
                                    if (r1.isChat) {
                                    }
                                    i = AndroidUtilities.dp(25.0f);
                                }
                                r1.otherX = i;
                                if (r1.titleLayout != null) {
                                    canvas.save();
                                    canvas2.translate((float) i, (float) (AndroidUtilities.dp(12.0f) + r1.namesOffset));
                                    r1.titleLayout.draw(canvas2);
                                    canvas.restore();
                                }
                                if (r1.docTitleLayout != null) {
                                    canvas.save();
                                    canvas2.translate((float) (AndroidUtilities.dp(19.0f) + i), (float) (AndroidUtilities.dp(37.0f) + r1.namesOffset));
                                    r1.docTitleLayout.draw(canvas2);
                                    canvas.restore();
                                }
                                if (r1.currentMessageObject.isOutOwner()) {
                                    drawable = Theme.chat_msgCallUpGreenDrawable;
                                    if (isDrawSelectedBackground()) {
                                        if (r1.otherPressed) {
                                            drawable3 = Theme.chat_msgOutCallDrawable;
                                        }
                                    }
                                    drawable3 = Theme.chat_msgOutCallSelectedDrawable;
                                } else {
                                    phoneCallDiscardReason = r1.currentMessageObject.messageOwner.action.reason;
                                    if (phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonMissed) {
                                        if (phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonBusy) {
                                            drawable = Theme.chat_msgCallDownGreenDrawable;
                                            if (isDrawSelectedBackground()) {
                                                if (r1.otherPressed) {
                                                    drawable3 = Theme.chat_msgInCallDrawable;
                                                }
                                            }
                                            drawable3 = Theme.chat_msgInCallSelectedDrawable;
                                        }
                                    }
                                    drawable = Theme.chat_msgCallDownRedDrawable;
                                    if (isDrawSelectedBackground()) {
                                        if (r1.otherPressed) {
                                            drawable3 = Theme.chat_msgInCallDrawable;
                                        }
                                    }
                                    drawable3 = Theme.chat_msgInCallSelectedDrawable;
                                }
                                BaseCell.setDrawableBounds(drawable, i - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(36.0f) + r1.namesOffset);
                                drawable.draw(canvas2);
                                i += AndroidUtilities.dp(205.0f);
                                i7 = AndroidUtilities.dp(22.0f);
                                r1.otherY = i7;
                                BaseCell.setDrawableBounds(drawable3, i, i7);
                                drawable3.draw(canvas2);
                            } else if (r1.currentMessageObject.type == 12) {
                                if (r1.currentMessageObject.isOutOwner()) {
                                }
                                Theme.chat_contactNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outContactNameText : Theme.key_chat_inContactNameText));
                                if (r1.currentMessageObject.isOutOwner()) {
                                }
                                Theme.chat_contactPhonePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outContactPhoneText : Theme.key_chat_inContactPhoneText));
                                if (r1.titleLayout != null) {
                                    canvas.save();
                                    canvas2.translate((float) ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(16.0f) + r1.namesOffset));
                                    r1.titleLayout.draw(canvas2);
                                    canvas.restore();
                                }
                                if (r1.docTitleLayout != null) {
                                    canvas.save();
                                    canvas2.translate((float) ((r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(39.0f) + r1.namesOffset));
                                    r1.docTitleLayout.draw(canvas2);
                                    canvas.restore();
                                }
                                if (r1.currentMessageObject.isOutOwner()) {
                                    if (isDrawSelectedBackground()) {
                                    }
                                }
                                i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(48.0f);
                                r1.otherX = i7;
                                i4 = r1.photoImage.getImageY() - AndroidUtilities.dp(5.0f);
                                r1.otherY = i4;
                                BaseCell.setDrawableBounds(drawable2, i7, i4);
                                drawable2.draw(canvas2);
                            }
                            if (r1.captionLayout == null) {
                                f2 = 17.0f;
                            } else if (r1.currentMessageObject.type != 8) {
                                f2 = 17.0f;
                                r1.captionX = (r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f)) + r1.captionOffsetX;
                                r1.captionY = (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()) + AndroidUtilities.dp(f);
                            } else if (r1.hasOldCaptionPreview) {
                                if (r1.currentMessageObject.isOutOwner()) {
                                }
                                r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                                if (r1.drawPinnedTop) {
                                }
                                f2 = 17.0f;
                                r1.captionY = (((r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
                            } else {
                                f2 = 17.0f;
                                if (r1.currentMessageObject.isOutOwner()) {
                                }
                                r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                                if (r1.drawPinnedTop) {
                                }
                                r1.captionY = (r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f);
                            }
                            if (r1.currentPosition != null) {
                                z3 = false;
                                drawCaptionLayout(canvas2, false);
                            } else {
                                z3 = false;
                            }
                            if (r1.hasOldCaptionPreview) {
                                z = true;
                            } else if (r1.currentMessageObject.type == 8) {
                                i = r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f);
                                i5 = i;
                                if (r1.drawPinnedTop) {
                                }
                                dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                                if (r1.currentMessageObject.isOutOwner()) {
                                }
                                Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                                canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                                if (r1.siteNameLayout == null) {
                                    if (r1.currentMessageObject.isOutOwner()) {
                                    }
                                    Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                                    canvas.save();
                                    if (r1.siteNameRtl) {
                                    }
                                    canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                                    r1.siteNameLayout.draw(canvas2);
                                    canvas.restore();
                                    i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                                } else {
                                    i = dp;
                                }
                                if (r1.currentMessageObject.isOutOwner()) {
                                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                } else {
                                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                }
                                if (r1.descriptionLayout != null) {
                                    if (i != dp) {
                                        i += AndroidUtilities.dp(2.0f);
                                    }
                                    r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                                    canvas.save();
                                    canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                                    r1.descriptionLayout.draw(canvas2);
                                    canvas.restore();
                                }
                                z = true;
                                r1.drawTime = true;
                            } else {
                                i = r1.backgroundDrawableLeft;
                                if (r1.currentMessageObject.isOutOwner()) {
                                    f2 = 11.0f;
                                }
                                i += AndroidUtilities.dp(f2);
                                i5 = i;
                                if (r1.drawPinnedTop) {
                                }
                                dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                                if (r1.currentMessageObject.isOutOwner()) {
                                }
                                Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                                canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                                if (r1.siteNameLayout == null) {
                                    i = dp;
                                } else {
                                    if (r1.currentMessageObject.isOutOwner()) {
                                    }
                                    Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                                    canvas.save();
                                    if (r1.siteNameRtl) {
                                        if (r1.hasInvoicePreview) {
                                        }
                                    }
                                    canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                                    r1.siteNameLayout.draw(canvas2);
                                    canvas.restore();
                                    i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                                }
                                if (r1.currentMessageObject.isOutOwner()) {
                                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                                } else {
                                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                                }
                                if (r1.descriptionLayout != null) {
                                    if (i != dp) {
                                        i += AndroidUtilities.dp(2.0f);
                                    }
                                    r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                                    canvas.save();
                                    canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                                    r1.descriptionLayout.draw(canvas2);
                                    canvas.restore();
                                }
                                z = true;
                                r1.drawTime = true;
                            }
                            if (r1.documentAttachType == z) {
                                if (r1.currentMessageObject.isOutOwner()) {
                                    Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_outFileNameText));
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileInfoSelectedText : Theme.key_chat_outFileInfoText));
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileBackgroundSelected : Theme.key_chat_outFileBackground));
                                    if (isDrawSelectedBackground()) {
                                    }
                                } else {
                                    Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_inFileNameText));
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileInfoSelectedText : Theme.key_chat_inFileInfoText));
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileBackgroundSelected : Theme.key_chat_inFileBackground));
                                    if (isDrawSelectedBackground()) {
                                    }
                                }
                                if (r1.drawPhotoImage) {
                                    if (r1.currentMessageObject.type != 0) {
                                        i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(56.0f);
                                        r1.otherX = i7;
                                        i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                        r1.otherY = i4;
                                        BaseCell.setDrawableBounds(drawable2, i7, i4);
                                    } else {
                                        i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(40.0f);
                                        r1.otherX = i7;
                                        i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                        r1.otherY = i4;
                                        BaseCell.setDrawableBounds(drawable2, i7, i4);
                                    }
                                    i7 = (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
                                    i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                                    alpha = (r1.photoImage.getImageY() + r1.docTitleLayout.getLineBottom(r1.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f);
                                    if (z2) {
                                        i6 = r1.buttonState;
                                        if (r1.buttonState != 0) {
                                            if (r1.currentMessageObject.isOutOwner()) {
                                            }
                                        } else if (r1.buttonState == 1) {
                                            if (r1.currentMessageObject.isOutOwner()) {
                                            }
                                        }
                                        radialProgress2 = r1.radialProgress;
                                        drawableArr = Theme.chat_photoStatesDrawables[i6];
                                        if (isDrawSelectedBackground()) {
                                            if (r1.buttonPressed != 0) {
                                                i5 = z3;
                                                radialProgress2.swapBackground(drawableArr[i5]);
                                            }
                                        }
                                        i5 = 1;
                                        radialProgress2.swapBackground(drawableArr[i5]);
                                    } else {
                                        r1.radialProgress.swapBackground(Theme.chat_photoStatesDrawables[r1.buttonState][r1.buttonPressed]);
                                    }
                                    if (z2) {
                                        r1.rect.set((float) r1.photoImage.getImageX(), (float) r1.photoImage.getImageY(), (float) (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()), (float) (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()));
                                        canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                                        if (r1.currentMessageObject.isOutOwner()) {
                                            if (isDrawSelectedBackground()) {
                                            }
                                            r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileProgressSelected : Theme.key_chat_outFileProgress));
                                        } else {
                                            if (isDrawSelectedBackground()) {
                                            }
                                            r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileProgressSelected : Theme.key_chat_inFileProgress));
                                        }
                                    } else {
                                        if (r1.buttonState == -1) {
                                            r1.radialProgress.setHideCurrentDrawable(true);
                                        }
                                        r1.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
                                    }
                                } else {
                                    if (r1.currentMessageObject.type != 0) {
                                    }
                                    i7 = (r1.buttonX + r1.backgroundWidth) - AndroidUtilities.dp(r1.currentMessageObject.type != 0 ? 58.0f : 48.0f);
                                    r1.otherX = i7;
                                    i4 = r1.buttonY - AndroidUtilities.dp(5.0f);
                                    r1.otherY = i4;
                                    BaseCell.setDrawableBounds(drawable2, i7, i4);
                                    i7 = r1.buttonX + AndroidUtilities.dp(53.0f);
                                    i4 = r1.buttonY + AndroidUtilities.dp(4.0f);
                                    alpha = r1.buttonY + AndroidUtilities.dp(27.0f);
                                    if (r1.currentMessageObject.isOutOwner()) {
                                        radialProgress = r1.radialProgress;
                                        if (isDrawSelectedBackground()) {
                                            if (r1.buttonPressed != 0) {
                                                str = Theme.key_chat_outAudioProgress;
                                                radialProgress.setProgressColor(Theme.getColor(str));
                                            }
                                        }
                                        str = Theme.key_chat_outAudioSelectedProgress;
                                        radialProgress.setProgressColor(Theme.getColor(str));
                                    } else {
                                        radialProgress = r1.radialProgress;
                                        if (isDrawSelectedBackground()) {
                                            if (r1.buttonPressed != 0) {
                                                str = Theme.key_chat_inAudioProgress;
                                                radialProgress.setProgressColor(Theme.getColor(str));
                                            }
                                        }
                                        str = Theme.key_chat_inAudioSelectedProgress;
                                        radialProgress.setProgressColor(Theme.getColor(str));
                                    }
                                }
                                drawable2.draw(canvas2);
                                if (r1.docTitleLayout != null) {
                                    canvas.save();
                                    canvas2.translate((float) (r1.docTitleOffsetX + i7), (float) i4);
                                    r1.docTitleLayout.draw(canvas2);
                                    canvas.restore();
                                }
                                if (r1.infoLayout != null) {
                                    canvas.save();
                                    canvas2.translate((float) i7, (float) alpha);
                                    r1.infoLayout.draw(canvas2);
                                    canvas.restore();
                                }
                            }
                            if (r1.controlsAlpha != 1.0f) {
                                r1.radialProgress.setOverrideAlpha(r1.controlsAlpha);
                            }
                            r1.radialProgress.draw(canvas2);
                            if (!r1.botButtons.isEmpty()) {
                                if (r1.currentMessageObject.isOutOwner()) {
                                    i = (getMeasuredWidth() - r1.widthForButtons) - AndroidUtilities.dp(10.0f);
                                } else {
                                    if (r1.mediaBackground) {
                                    }
                                    i = r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.mediaBackground ? 1.0f : 7.0f);
                                }
                                i2 = i;
                                i5 = z3;
                                while (i5 < r1.botButtons.size()) {
                                    botButton = (BotButton) r1.botButtons.get(i5);
                                    i = (botButton.f10y + r1.layoutHeight) - AndroidUtilities.dp(2.0f);
                                    if (i5 != r1.pressedBotButton) {
                                    }
                                    Theme.chat_systemDrawable.setColorFilter(i5 != r1.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                                    Theme.chat_systemDrawable.setBounds(botButton.f9x + i2, i, (botButton.f9x + i2) + botButton.width, botButton.height + i);
                                    Theme.chat_systemDrawable.draw(canvas2);
                                    canvas.save();
                                    canvas2.translate((float) ((botButton.f9x + i2) + AndroidUtilities.dp(max)), (float) (((AndroidUtilities.dp(44.0f) - botButton.title.getLineBottom(botButton.title.getLineCount() - 1)) / 2) + i));
                                    botButton.title.draw(canvas2);
                                    canvas.restore();
                                    if (botButton.button instanceof TL_keyboardButtonUrl) {
                                        BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                        Theme.chat_botLinkDrawalbe.draw(canvas2);
                                    } else {
                                        if (botButton.button instanceof TL_keyboardButtonSwitchInline) {
                                            f6 = 3.0f;
                                            BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                            Theme.chat_botInlineDrawable.draw(canvas2);
                                        } else {
                                            f6 = 3.0f;
                                            if (botButton.button instanceof TL_keyboardButtonRequestGeoLocation) {
                                            }
                                            z4 = z3;
                                            Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (botButton.progressAlpha * 255.0f)));
                                            i7 = ((botButton.f9x + botButton.width) - AndroidUtilities.dp(12.0f)) + i2;
                                            r1.rect.set((float) i7, (float) (i + AndroidUtilities.dp(4.0f)), (float) (i7 + AndroidUtilities.dp(8.0f)), (float) (i + AndroidUtilities.dp(12.0f)));
                                            canvas2.drawArc(r1.rect, (float) botButton.angle, 220.0f, false, Theme.chat_botProgressPaint);
                                            invalidate(((int) r1.rect.left) - AndroidUtilities.dp(2.0f), ((int) r1.rect.top) - AndroidUtilities.dp(2.0f), ((int) r1.rect.right) + AndroidUtilities.dp(2.0f), ((int) r1.rect.bottom) + AndroidUtilities.dp(2.0f));
                                            currentTimeMillis = System.currentTimeMillis();
                                            if (Math.abs(botButton.lastUpdateTime - System.currentTimeMillis()) >= 1000) {
                                                access$1300 = currentTimeMillis - botButton.lastUpdateTime;
                                                botButton.angle = (int) (((float) botButton.angle) + (((float) (360 * access$1300)) / 2000.0f));
                                                botButton.angle = botButton.angle - (360 * (botButton.angle / 360));
                                                if (z4) {
                                                    if (botButton.progressAlpha > 0.0f) {
                                                        botButton.progressAlpha = botButton.progressAlpha - (((float) access$1300) / 200.0f);
                                                        if (botButton.progressAlpha < 0.0f) {
                                                            botButton.progressAlpha = 0.0f;
                                                        }
                                                    }
                                                    botButton.lastUpdateTime = currentTimeMillis;
                                                } else if (botButton.progressAlpha < 1.0f) {
                                                    botButton.progressAlpha = botButton.progressAlpha + (((float) access$1300) / 200.0f);
                                                    if (botButton.progressAlpha > 1.0f) {
                                                        botButton.progressAlpha = 1.0f;
                                                    }
                                                }
                                            }
                                            botButton.lastUpdateTime = currentTimeMillis;
                                        }
                                        f3 = f6;
                                    }
                                    i5++;
                                    max = 5.0f;
                                    z3 = false;
                                }
                            }
                        }
                    }
                    if (r1.photoImage.getVisible()) {
                        i = ((BitmapDrawable) Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
                        Theme.chat_msgMediaMenuDrawable.setAlpha((int) (((float) i) * r1.controlsAlpha));
                        drawable = Theme.chat_msgMediaMenuDrawable;
                        i4 = (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                        r1.otherX = i4;
                        alpha = r1.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                        r1.otherY = alpha;
                        BaseCell.setDrawableBounds(drawable, i4, alpha);
                        Theme.chat_msgMediaMenuDrawable.draw(canvas2);
                        Theme.chat_msgMediaMenuDrawable.setAlpha(i);
                        Theme.chat_infoPaint.setColor(Theme.getColor(Theme.key_chat_mediaInfoText));
                        i = r1.photoImage.getImageX() + AndroidUtilities.dp(4.0f);
                        i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(4.0f);
                        r1.rect.set((float) i, (float) i4, (float) ((i + r1.infoWidth) + AndroidUtilities.dp(8.0f)), (float) (i4 + AndroidUtilities.dp(16.5f)));
                        i = Theme.chat_timeBackgroundPaint.getAlpha();
                        Theme.chat_timeBackgroundPaint.setAlpha((int) (((float) i) * r1.controlsAlpha));
                        canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                        Theme.chat_timeBackgroundPaint.setAlpha(i);
                        canvas.save();
                        canvas2.translate((float) (r1.photoImage.getImageX() + AndroidUtilities.dp(8.0f)), (float) (r1.photoImage.getImageY() + AndroidUtilities.dp(5.5f)));
                        Theme.chat_infoPaint.setAlpha((int) (255.0f * r1.controlsAlpha));
                        r1.infoLayout.draw(canvas2);
                        canvas.restore();
                        Theme.chat_infoPaint.setAlpha(255);
                    }
                    if (r1.captionLayout == null) {
                        f2 = 17.0f;
                    } else if (r1.currentMessageObject.type != 8) {
                        f2 = 17.0f;
                        r1.captionX = (r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f)) + r1.captionOffsetX;
                        r1.captionY = (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()) + AndroidUtilities.dp(f);
                    } else if (r1.hasOldCaptionPreview) {
                        f2 = 17.0f;
                        if (r1.currentMessageObject.isOutOwner()) {
                        }
                        r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                        if (r1.drawPinnedTop) {
                        }
                        r1.captionY = (r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f);
                    } else {
                        if (r1.currentMessageObject.isOutOwner()) {
                        }
                        r1.captionX = (r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.currentMessageObject.isOutOwner() ? 11.0f : 17.0f)) + r1.captionOffsetX;
                        if (r1.drawPinnedTop) {
                        }
                        f2 = 17.0f;
                        r1.captionY = (((r1.totalHeight - r1.captionHeight) - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
                    }
                    if (r1.currentPosition != null) {
                        z3 = false;
                    } else {
                        z3 = false;
                        drawCaptionLayout(canvas2, false);
                    }
                    if (r1.hasOldCaptionPreview) {
                        z = true;
                    } else if (r1.currentMessageObject.type == 8) {
                        i = r1.backgroundDrawableLeft;
                        if (r1.currentMessageObject.isOutOwner()) {
                            f2 = 11.0f;
                        }
                        i += AndroidUtilities.dp(f2);
                        i5 = i;
                        if (r1.drawPinnedTop) {
                        }
                        dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                        if (r1.currentMessageObject.isOutOwner()) {
                        }
                        Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                        canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                        if (r1.siteNameLayout == null) {
                            if (r1.currentMessageObject.isOutOwner()) {
                            }
                            Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                            canvas.save();
                            if (r1.siteNameRtl) {
                            }
                            canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                            r1.siteNameLayout.draw(canvas2);
                            canvas.restore();
                            i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                        } else {
                            i = dp;
                        }
                        if (r1.currentMessageObject.isOutOwner()) {
                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                        } else {
                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                        }
                        if (r1.descriptionLayout != null) {
                            if (i != dp) {
                                i += AndroidUtilities.dp(2.0f);
                            }
                            r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                            canvas.save();
                            canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                            r1.descriptionLayout.draw(canvas2);
                            canvas.restore();
                        }
                        z = true;
                        r1.drawTime = true;
                    } else {
                        i = r1.photoImage.getImageX() + AndroidUtilities.dp(5.0f);
                        i5 = i;
                        if (r1.drawPinnedTop) {
                        }
                        dp = ((r1.totalHeight - AndroidUtilities.dp(r1.drawPinnedTop ? 9.0f : 10.0f)) - r1.linkPreviewHeight) - AndroidUtilities.dp(8.0f);
                        if (r1.currentMessageObject.isOutOwner()) {
                        }
                        Theme.chat_replyLinePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
                        canvas2.drawRect((float) i5, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + i5), (float) (r1.linkPreviewHeight + dp), Theme.chat_replyLinePaint);
                        if (r1.siteNameLayout == null) {
                            i = dp;
                        } else {
                            if (r1.currentMessageObject.isOutOwner()) {
                            }
                            Theme.chat_replyNamePaint.setColor(Theme.getColor(r1.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
                            canvas.save();
                            if (r1.siteNameRtl) {
                                if (r1.hasInvoicePreview) {
                                }
                            }
                            canvas2.translate((float) (i + i5), (float) (dp - AndroidUtilities.dp(3.0f)));
                            r1.siteNameLayout.draw(canvas2);
                            canvas.restore();
                            i = r1.siteNameLayout.getLineBottom(r1.siteNameLayout.getLineCount() - 1) + dp;
                        }
                        if (r1.currentMessageObject.isOutOwner()) {
                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
                        } else {
                            Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
                        }
                        if (r1.descriptionLayout != null) {
                            if (i != dp) {
                                i += AndroidUtilities.dp(2.0f);
                            }
                            r1.descriptionY = i - AndroidUtilities.dp(3.0f);
                            canvas.save();
                            canvas2.translate((float) ((i5 + AndroidUtilities.dp(10.0f)) + r1.descriptionX), (float) r1.descriptionY);
                            r1.descriptionLayout.draw(canvas2);
                            canvas.restore();
                        }
                        z = true;
                        r1.drawTime = true;
                    }
                    if (r1.documentAttachType == z) {
                        if (r1.currentMessageObject.isOutOwner()) {
                            Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_inFileNameText));
                            if (isDrawSelectedBackground()) {
                            }
                            Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileInfoSelectedText : Theme.key_chat_inFileInfoText));
                            if (isDrawSelectedBackground()) {
                            }
                            Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileBackgroundSelected : Theme.key_chat_inFileBackground));
                            if (isDrawSelectedBackground()) {
                            }
                        } else {
                            Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_outFileNameText));
                            if (isDrawSelectedBackground()) {
                            }
                            Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileInfoSelectedText : Theme.key_chat_outFileInfoText));
                            if (isDrawSelectedBackground()) {
                            }
                            Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileBackgroundSelected : Theme.key_chat_outFileBackground));
                            if (isDrawSelectedBackground()) {
                            }
                        }
                        if (r1.drawPhotoImage) {
                            if (r1.currentMessageObject.type != 0) {
                            }
                            i7 = (r1.buttonX + r1.backgroundWidth) - AndroidUtilities.dp(r1.currentMessageObject.type != 0 ? 58.0f : 48.0f);
                            r1.otherX = i7;
                            i4 = r1.buttonY - AndroidUtilities.dp(5.0f);
                            r1.otherY = i4;
                            BaseCell.setDrawableBounds(drawable2, i7, i4);
                            i7 = r1.buttonX + AndroidUtilities.dp(53.0f);
                            i4 = r1.buttonY + AndroidUtilities.dp(4.0f);
                            alpha = r1.buttonY + AndroidUtilities.dp(27.0f);
                            if (r1.currentMessageObject.isOutOwner()) {
                                radialProgress = r1.radialProgress;
                                if (isDrawSelectedBackground()) {
                                    if (r1.buttonPressed != 0) {
                                        str = Theme.key_chat_inAudioProgress;
                                        radialProgress.setProgressColor(Theme.getColor(str));
                                    }
                                }
                                str = Theme.key_chat_inAudioSelectedProgress;
                                radialProgress.setProgressColor(Theme.getColor(str));
                            } else {
                                radialProgress = r1.radialProgress;
                                if (isDrawSelectedBackground()) {
                                    if (r1.buttonPressed != 0) {
                                        str = Theme.key_chat_outAudioProgress;
                                        radialProgress.setProgressColor(Theme.getColor(str));
                                    }
                                }
                                str = Theme.key_chat_outAudioSelectedProgress;
                                radialProgress.setProgressColor(Theme.getColor(str));
                            }
                        } else {
                            if (r1.currentMessageObject.type != 0) {
                                i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(40.0f);
                                r1.otherX = i7;
                                i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                r1.otherY = i4;
                                BaseCell.setDrawableBounds(drawable2, i7, i4);
                            } else {
                                i7 = (r1.photoImage.getImageX() + r1.backgroundWidth) - AndroidUtilities.dp(56.0f);
                                r1.otherX = i7;
                                i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                                r1.otherY = i4;
                                BaseCell.setDrawableBounds(drawable2, i7, i4);
                            }
                            i7 = (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
                            i4 = r1.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                            alpha = (r1.photoImage.getImageY() + r1.docTitleLayout.getLineBottom(r1.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f);
                            if (z2) {
                                r1.radialProgress.swapBackground(Theme.chat_photoStatesDrawables[r1.buttonState][r1.buttonPressed]);
                            } else {
                                i6 = r1.buttonState;
                                if (r1.buttonState != 0) {
                                    if (r1.buttonState == 1) {
                                        if (r1.currentMessageObject.isOutOwner()) {
                                        }
                                    }
                                } else if (r1.currentMessageObject.isOutOwner()) {
                                }
                                radialProgress2 = r1.radialProgress;
                                drawableArr = Theme.chat_photoStatesDrawables[i6];
                                if (isDrawSelectedBackground()) {
                                    if (r1.buttonPressed != 0) {
                                        i5 = z3;
                                        radialProgress2.swapBackground(drawableArr[i5]);
                                    }
                                }
                                i5 = 1;
                                radialProgress2.swapBackground(drawableArr[i5]);
                            }
                            if (z2) {
                                if (r1.buttonState == -1) {
                                    r1.radialProgress.setHideCurrentDrawable(true);
                                }
                                r1.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
                            } else {
                                r1.rect.set((float) r1.photoImage.getImageX(), (float) r1.photoImage.getImageY(), (float) (r1.photoImage.getImageX() + r1.photoImage.getImageWidth()), (float) (r1.photoImage.getImageY() + r1.photoImage.getImageHeight()));
                                canvas2.drawRoundRect(r1.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                                if (r1.currentMessageObject.isOutOwner()) {
                                    if (isDrawSelectedBackground()) {
                                    }
                                    r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileProgressSelected : Theme.key_chat_inFileProgress));
                                } else {
                                    if (isDrawSelectedBackground()) {
                                    }
                                    r1.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileProgressSelected : Theme.key_chat_outFileProgress));
                                }
                            }
                        }
                        drawable2.draw(canvas2);
                        if (r1.docTitleLayout != null) {
                            canvas.save();
                            canvas2.translate((float) (r1.docTitleOffsetX + i7), (float) i4);
                            r1.docTitleLayout.draw(canvas2);
                            canvas.restore();
                        }
                        if (r1.infoLayout != null) {
                            canvas.save();
                            canvas2.translate((float) i7, (float) alpha);
                            r1.infoLayout.draw(canvas2);
                            canvas.restore();
                        }
                    }
                    if (r1.controlsAlpha != 1.0f) {
                        r1.radialProgress.setOverrideAlpha(r1.controlsAlpha);
                    }
                    r1.radialProgress.draw(canvas2);
                    if (!r1.botButtons.isEmpty()) {
                        if (r1.currentMessageObject.isOutOwner()) {
                            if (r1.mediaBackground) {
                            }
                            i = r1.backgroundDrawableLeft + AndroidUtilities.dp(r1.mediaBackground ? 1.0f : 7.0f);
                        } else {
                            i = (getMeasuredWidth() - r1.widthForButtons) - AndroidUtilities.dp(10.0f);
                        }
                        i2 = i;
                        i5 = z3;
                        while (i5 < r1.botButtons.size()) {
                            botButton = (BotButton) r1.botButtons.get(i5);
                            i = (botButton.f10y + r1.layoutHeight) - AndroidUtilities.dp(2.0f);
                            if (i5 != r1.pressedBotButton) {
                            }
                            Theme.chat_systemDrawable.setColorFilter(i5 != r1.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                            Theme.chat_systemDrawable.setBounds(botButton.f9x + i2, i, (botButton.f9x + i2) + botButton.width, botButton.height + i);
                            Theme.chat_systemDrawable.draw(canvas2);
                            canvas.save();
                            canvas2.translate((float) ((botButton.f9x + i2) + AndroidUtilities.dp(max)), (float) (((AndroidUtilities.dp(44.0f) - botButton.title.getLineBottom(botButton.title.getLineCount() - 1)) / 2) + i));
                            botButton.title.draw(canvas2);
                            canvas.restore();
                            if (botButton.button instanceof TL_keyboardButtonUrl) {
                                if (botButton.button instanceof TL_keyboardButtonSwitchInline) {
                                    f6 = 3.0f;
                                    if (botButton.button instanceof TL_keyboardButtonRequestGeoLocation) {
                                    }
                                    z4 = z3;
                                    Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (botButton.progressAlpha * 255.0f)));
                                    i7 = ((botButton.f9x + botButton.width) - AndroidUtilities.dp(12.0f)) + i2;
                                    r1.rect.set((float) i7, (float) (i + AndroidUtilities.dp(4.0f)), (float) (i7 + AndroidUtilities.dp(8.0f)), (float) (i + AndroidUtilities.dp(12.0f)));
                                    canvas2.drawArc(r1.rect, (float) botButton.angle, 220.0f, false, Theme.chat_botProgressPaint);
                                    invalidate(((int) r1.rect.left) - AndroidUtilities.dp(2.0f), ((int) r1.rect.top) - AndroidUtilities.dp(2.0f), ((int) r1.rect.right) + AndroidUtilities.dp(2.0f), ((int) r1.rect.bottom) + AndroidUtilities.dp(2.0f));
                                    currentTimeMillis = System.currentTimeMillis();
                                    if (Math.abs(botButton.lastUpdateTime - System.currentTimeMillis()) >= 1000) {
                                        access$1300 = currentTimeMillis - botButton.lastUpdateTime;
                                        botButton.angle = (int) (((float) botButton.angle) + (((float) (360 * access$1300)) / 2000.0f));
                                        botButton.angle = botButton.angle - (360 * (botButton.angle / 360));
                                        if (z4) {
                                            if (botButton.progressAlpha > 0.0f) {
                                                botButton.progressAlpha = botButton.progressAlpha - (((float) access$1300) / 200.0f);
                                                if (botButton.progressAlpha < 0.0f) {
                                                    botButton.progressAlpha = 0.0f;
                                                }
                                            }
                                            botButton.lastUpdateTime = currentTimeMillis;
                                        } else if (botButton.progressAlpha < 1.0f) {
                                            botButton.progressAlpha = botButton.progressAlpha + (((float) access$1300) / 200.0f);
                                            if (botButton.progressAlpha > 1.0f) {
                                                botButton.progressAlpha = 1.0f;
                                            }
                                        }
                                    }
                                    botButton.lastUpdateTime = currentTimeMillis;
                                } else {
                                    f6 = 3.0f;
                                    BaseCell.setDrawableBounds(Theme.chat_botInlineDrawable, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                    Theme.chat_botInlineDrawable.draw(canvas2);
                                }
                                f3 = f6;
                            } else {
                                BaseCell.setDrawableBounds(Theme.chat_botLinkDrawalbe, (((botButton.f9x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + i2, i + AndroidUtilities.dp(3.0f));
                                Theme.chat_botLinkDrawalbe.draw(canvas2);
                            }
                            i5++;
                            max = 5.0f;
                            z3 = false;
                        }
                    }
                }

                private Drawable getMiniDrawableForCurrentState() {
                    if (this.miniButtonState < 0) {
                        return null;
                    }
                    CombinedDrawable[] combinedDrawableArr;
                    int i = 1;
                    if (this.documentAttachType != 3) {
                        if (this.documentAttachType != 5) {
                            if (this.documentAttachType != 4) {
                                return null;
                            }
                            combinedDrawableArr = Theme.chat_fileMiniStatesDrawable[4 + this.miniButtonState];
                            if (this.miniButtonPressed == 0) {
                                i = 0;
                            }
                            return combinedDrawableArr[i];
                        }
                    }
                    this.radialProgress.setAlphaForPrevious(false);
                    combinedDrawableArr = Theme.chat_fileMiniStatesDrawable[this.currentMessageObject.isOutOwner() ? this.miniButtonState : this.miniButtonState + 2];
                    if (!isDrawSelectedBackground()) {
                        if (this.miniButtonPressed == 0) {
                            i = 0;
                        }
                    }
                    return combinedDrawableArr[i];
                }

                private Drawable getDrawableForCurrentState() {
                    int i = 3;
                    int i2 = 0;
                    if (this.documentAttachType != 3) {
                        if (this.documentAttachType != 5) {
                            int i3 = 9;
                            int i4 = 7;
                            int i5 = 8;
                            Drawable[][] drawableArr;
                            if (this.documentAttachType != 1 || this.drawPhotoImage) {
                                this.radialProgress.setAlphaForPrevious(true);
                                if (this.buttonState < 0 || this.buttonState >= 4) {
                                    if (this.buttonState == -1 && this.documentAttachType == 1) {
                                        drawableArr = Theme.chat_photoStatesDrawables;
                                        if (!this.currentMessageObject.isOutOwner()) {
                                            i3 = 12;
                                        }
                                        return drawableArr[i3][isDrawSelectedBackground()];
                                    }
                                } else if (this.documentAttachType != 1) {
                                    return Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed];
                                } else {
                                    int i6 = this.buttonState;
                                    if (this.buttonState == 0) {
                                        if (!this.currentMessageObject.isOutOwner()) {
                                            i4 = 10;
                                        }
                                        i6 = i4;
                                    } else if (this.buttonState == 1) {
                                        if (!this.currentMessageObject.isOutOwner()) {
                                            i5 = 11;
                                        }
                                        i6 = i5;
                                    }
                                    Drawable[] drawableArr2 = Theme.chat_photoStatesDrawables[i6];
                                    if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                                        i2 = 1;
                                    }
                                    return drawableArr2[i2];
                                }
                            }
                            this.radialProgress.setAlphaForPrevious(false);
                            if (this.buttonState == -1) {
                                drawableArr = Theme.chat_fileStatesDrawable;
                                if (!this.currentMessageObject.isOutOwner()) {
                                    i = 8;
                                }
                                return drawableArr[i][isDrawSelectedBackground()];
                            } else if (this.buttonState == 0) {
                                drawableArr = Theme.chat_fileStatesDrawable;
                                if (this.currentMessageObject.isOutOwner()) {
                                    i4 = 2;
                                }
                                return drawableArr[i4][isDrawSelectedBackground()];
                            } else if (this.buttonState == 1) {
                                drawableArr = Theme.chat_fileStatesDrawable;
                                if (this.currentMessageObject.isOutOwner()) {
                                    i3 = 4;
                                }
                                return drawableArr[i3][isDrawSelectedBackground()];
                            }
                            return null;
                        }
                    }
                    if (this.buttonState == -1) {
                        return null;
                    }
                    this.radialProgress.setAlphaForPrevious(false);
                    this.radialProgress.setAlphaForMiniPrevious(true);
                    drawableArr2 = Theme.chat_fileStatesDrawable[this.currentMessageObject.isOutOwner() ? this.buttonState : this.buttonState + 5];
                    if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                        i2 = 1;
                    }
                    return drawableArr2[i2];
                }

                private int getMaxNameWidth() {
                    int minTabletSide;
                    if (this.documentAttachType != 6) {
                        if (this.currentMessageObject.type != 5) {
                            if (this.currentMessagesGroup != null) {
                                if (AndroidUtilities.isTablet()) {
                                    minTabletSide = AndroidUtilities.getMinTabletSide();
                                } else {
                                    minTabletSide = AndroidUtilities.displaySize.x;
                                }
                                int i = 0;
                                int i2 = 0;
                                int i3 = i2;
                                while (i2 < this.currentMessagesGroup.posArray.size()) {
                                    GroupedMessagePosition groupedMessagePosition = (GroupedMessagePosition) this.currentMessagesGroup.posArray.get(i2);
                                    if (groupedMessagePosition.minY != (byte) 0) {
                                        break;
                                    }
                                    i3 = (int) (((double) i3) + Math.ceil((double) ((((float) (groupedMessagePosition.pw + groupedMessagePosition.leftSpanOffset)) / 1000.0f) * ((float) minTabletSide))));
                                    i2++;
                                }
                                if (this.isAvatarVisible) {
                                    i = 48;
                                }
                                return i3 - AndroidUtilities.dp((float) (31 + i));
                            }
                            return this.backgroundWidth - AndroidUtilities.dp(this.mediaBackground ? 22.0f : 31.0f);
                        }
                    }
                    if (AndroidUtilities.isTablet()) {
                        if (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.needDrawAvatar()) {
                            minTabletSide = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(42.0f);
                        } else {
                            minTabletSide = AndroidUtilities.getMinTabletSide();
                        }
                    } else if (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.needDrawAvatar()) {
                        minTabletSide = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(42.0f);
                    } else {
                        minTabletSide = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                    }
                    return (minTabletSide - this.backgroundWidth) - AndroidUtilities.dp(57.0f);
                }

                public void updateButtonState(boolean z) {
                    String attachFileName;
                    boolean z2;
                    boolean z3 = z;
                    this.drawRadialCheckBackground = false;
                    boolean z4 = true;
                    if (this.currentMessageObject.type != 1) {
                        if (!(r0.currentMessageObject.type == 8 || r0.currentMessageObject.type == 5 || r0.documentAttachType == 7 || r0.documentAttachType == 4 || r0.currentMessageObject.type == 9 || r0.documentAttachType == 3)) {
                            if (r0.documentAttachType != 5) {
                                if (r0.documentAttachType != 0) {
                                    attachFileName = FileLoader.getAttachFileName(r0.documentAttach);
                                    z2 = r0.currentMessageObject.mediaExists;
                                } else {
                                    if (r0.currentPhotoObject != null) {
                                        attachFileName = FileLoader.getAttachFileName(r0.currentPhotoObject);
                                        z2 = r0.currentMessageObject.mediaExists;
                                    }
                                    z2 = false;
                                    attachFileName = null;
                                }
                            }
                        }
                        if (r0.currentMessageObject.useCustomPhoto) {
                            r0.buttonState = 1;
                            r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z3);
                            return;
                        } else if (r0.currentMessageObject.attachPathExists) {
                            attachFileName = r0.currentMessageObject.messageOwner.attachPath;
                            z2 = true;
                        } else {
                            if (r0.currentMessageObject.isSendError() && r0.documentAttachType != 3) {
                                if (r0.documentAttachType == 5) {
                                }
                                z2 = false;
                                attachFileName = null;
                            }
                            attachFileName = r0.currentMessageObject.getFileName();
                            z2 = r0.currentMessageObject.mediaExists;
                        }
                    } else if (r0.currentPhotoObject != null) {
                        attachFileName = FileLoader.getAttachFileName(r0.currentPhotoObject);
                        z2 = r0.currentMessageObject.mediaExists;
                    } else {
                        return;
                    }
                    if (SharedConfig.streamMedia && ((int) r0.currentMessageObject.getDialogId()) != 0 && !r0.currentMessageObject.isSecretMedia() && (r0.documentAttachType == 5 || (r0.documentAttachType == 4 && r0.currentMessageObject.canStreamVideo()))) {
                        r0.hasMiniProgress = z2 ? 1 : 2;
                        z2 = true;
                    }
                    if (TextUtils.isEmpty(attachFileName)) {
                        r0.radialProgress.setBackground(null, false, false);
                        r0.radialProgress.setMiniBackground(null, false, false);
                        return;
                    }
                    Float fileProgress;
                    boolean z5;
                    boolean isSendingMessage;
                    RadialProgress radialProgress;
                    int i = (r0.currentMessageObject.messageOwner.params == null || !r0.currentMessageObject.messageOwner.params.containsKey("query_id")) ? 0 : 1;
                    float f = 0.0f;
                    if (r0.documentAttachType != 3) {
                        if (r0.documentAttachType != 5) {
                            if (r0.currentMessageObject.type == 0 && r0.documentAttachType != 1 && r0.documentAttachType != 4) {
                                if (r0.currentPhotoObject != null) {
                                    if (r0.drawImageButton) {
                                        if (z2) {
                                            DownloadController.getInstance(r0.currentAccount).removeLoadingFileObserver(r0);
                                            if (r0.documentAttachType != 2 || r0.photoImage.isAllowStartAnimation()) {
                                                r0.buttonState = -1;
                                            } else {
                                                r0.buttonState = 2;
                                            }
                                            r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z3);
                                            invalidate();
                                        } else {
                                            DownloadController.getInstance(r0.currentAccount).addLoadingFileObserver(attachFileName, r0.currentMessageObject, r0);
                                            if (FileLoader.getInstance(r0.currentAccount).isLoadingFile(attachFileName)) {
                                                r0.buttonState = 1;
                                                fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                                                if (fileProgress != null) {
                                                    f = fileProgress.floatValue();
                                                }
                                            } else if (r0.cancelLoading || !((r0.documentAttachType == 0 && DownloadController.getInstance(r0.currentAccount).canDownloadMedia(r0.currentMessageObject)) || (r0.documentAttachType == 2 && MessageObject.isNewGifDocument(r0.documentAttach) && DownloadController.getInstance(r0.currentAccount).canDownloadMedia(r0.currentMessageObject)))) {
                                                r0.buttonState = 0;
                                                z4 = false;
                                            } else {
                                                r0.buttonState = 1;
                                            }
                                            r0.radialProgress.setProgress(f, false);
                                            r0.radialProgress.setBackground(getDrawableForCurrentState(), z4, z3);
                                            invalidate();
                                        }
                                        if (r0.hasMiniProgress == 0) {
                                            r0.radialProgress.setMiniBackground(null, false, z3);
                                        }
                                    }
                                }
                                return;
                            } else if (r0.currentMessageObject.isOut() && r0.currentMessageObject.isSending()) {
                                if (r0.currentMessageObject.messageOwner.attachPath != null && r0.currentMessageObject.messageOwner.attachPath.length() > 0) {
                                    HashMap hashMap;
                                    DownloadController.getInstance(r0.currentAccount).addLoadingFileObserver(r0.currentMessageObject.messageOwner.attachPath, r0.currentMessageObject, r0);
                                    if (r0.currentMessageObject.messageOwner.attachPath != null) {
                                        if (r0.currentMessageObject.messageOwner.attachPath.startsWith("http")) {
                                            z5 = false;
                                            hashMap = r0.currentMessageObject.messageOwner.params;
                                            if (r0.currentMessageObject.messageOwner.message == null && hashMap != null && (hashMap.containsKey(UpdateFragment.FRAGMENT_URL) || hashMap.containsKey("bot"))) {
                                                r0.buttonState = -1;
                                                z5 = false;
                                            } else {
                                                r0.buttonState = 1;
                                            }
                                            isSendingMessage = SendMessagesHelper.getInstance(r0.currentAccount).isSendingMessage(r0.currentMessageObject.getId());
                                            if (r0.currentPosition == null && isSendingMessage && r0.buttonState == 1) {
                                                r0.drawRadialCheckBackground = true;
                                                r0.radialProgress.setCheckBackground(false, z3);
                                            } else {
                                                r0.radialProgress.setBackground(getDrawableForCurrentState(), z5, z3);
                                            }
                                            if (z5) {
                                                r0.radialProgress.setProgress(0.0f, false);
                                            } else {
                                                fileProgress = ImageLoader.getInstance().getFileProgress(r0.currentMessageObject.messageOwner.attachPath);
                                                if (fileProgress == null && isSendingMessage) {
                                                    fileProgress = Float.valueOf(1.0f);
                                                }
                                                radialProgress = r0.radialProgress;
                                                if (fileProgress != null) {
                                                    f = fileProgress.floatValue();
                                                }
                                                radialProgress.setProgress(f, false);
                                            }
                                            invalidate();
                                        }
                                    }
                                    z5 = true;
                                    hashMap = r0.currentMessageObject.messageOwner.params;
                                    if (r0.currentMessageObject.messageOwner.message == null) {
                                    }
                                    r0.buttonState = 1;
                                    isSendingMessage = SendMessagesHelper.getInstance(r0.currentAccount).isSendingMessage(r0.currentMessageObject.getId());
                                    if (r0.currentPosition == null) {
                                    }
                                    r0.radialProgress.setBackground(getDrawableForCurrentState(), z5, z3);
                                    if (z5) {
                                        r0.radialProgress.setProgress(0.0f, false);
                                    } else {
                                        fileProgress = ImageLoader.getInstance().getFileProgress(r0.currentMessageObject.messageOwner.attachPath);
                                        fileProgress = Float.valueOf(1.0f);
                                        radialProgress = r0.radialProgress;
                                        if (fileProgress != null) {
                                            f = fileProgress.floatValue();
                                        }
                                        radialProgress.setProgress(f, false);
                                    }
                                    invalidate();
                                }
                                if (r0.hasMiniProgress == 0) {
                                    r0.radialProgress.setMiniBackground(null, false, z3);
                                }
                            } else {
                                if (!(r0.currentMessageObject.messageOwner.attachPath == null || r0.currentMessageObject.messageOwner.attachPath.length() == 0)) {
                                    DownloadController.getInstance(r0.currentAccount).removeLoadingFileObserver(r0);
                                }
                                if (r0.hasMiniProgress != 0) {
                                    r0.radialProgress.setMiniProgressBackgroundColor(Theme.getColor(Theme.key_chat_inLoaderPhoto));
                                    r0.buttonState = 3;
                                    r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z3);
                                    if (r0.hasMiniProgress == 1) {
                                        DownloadController.getInstance(r0.currentAccount).removeLoadingFileObserver(r0);
                                        r0.miniButtonState = -1;
                                    } else {
                                        DownloadController.getInstance(r0.currentAccount).addLoadingFileObserver(attachFileName, r0.currentMessageObject, r0);
                                        if (FileLoader.getInstance(r0.currentAccount).isLoadingFile(attachFileName)) {
                                            r0.miniButtonState = 1;
                                            fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                                            if (fileProgress != null) {
                                                r0.radialProgress.setProgress(fileProgress.floatValue(), z3);
                                            } else {
                                                r0.radialProgress.setProgress(0.0f, z3);
                                            }
                                        } else {
                                            r0.radialProgress.setProgress(0.0f, z3);
                                            r0.miniButtonState = 0;
                                        }
                                    }
                                    RadialProgress radialProgress2 = r0.radialProgress;
                                    Drawable miniDrawableForCurrentState = getMiniDrawableForCurrentState();
                                    if (r0.miniButtonState != 1) {
                                        z4 = false;
                                    }
                                    radialProgress2.setMiniBackground(miniDrawableForCurrentState, z4, z3);
                                } else if (z2) {
                                    DownloadController.getInstance(r0.currentAccount).removeLoadingFileObserver(r0);
                                    if (r0.currentMessageObject.needDrawBluredPreview()) {
                                        r0.buttonState = -1;
                                    } else if (r0.currentMessageObject.type == 8 && !r0.photoImage.isAllowStartAnimation()) {
                                        r0.buttonState = 2;
                                    } else if (r0.documentAttachType == 4) {
                                        r0.buttonState = 3;
                                    } else {
                                        r0.buttonState = -1;
                                    }
                                    r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z3);
                                    if (r0.photoNotSet) {
                                        setMessageObject(r0.currentMessageObject, r0.currentMessagesGroup, r0.pinnedBottom, r0.pinnedTop);
                                    }
                                    invalidate();
                                } else {
                                    DownloadController.getInstance(r0.currentAccount).addLoadingFileObserver(attachFileName, r0.currentMessageObject, r0);
                                    if (FileLoader.getInstance(r0.currentAccount).isLoadingFile(attachFileName)) {
                                        r0.buttonState = 1;
                                        fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                                        if (fileProgress != null) {
                                            f = fileProgress.floatValue();
                                        }
                                    } else {
                                        z5 = r0.currentMessageObject.type == 1 ? DownloadController.getInstance(r0.currentAccount).canDownloadMedia(r0.currentMessageObject) : (r0.currentMessageObject.type == 8 && MessageObject.isNewGifDocument(r0.currentMessageObject.messageOwner.media.document)) ? DownloadController.getInstance(r0.currentAccount).canDownloadMedia(r0.currentMessageObject) : r0.currentMessageObject.type == 5 ? DownloadController.getInstance(r0.currentAccount).canDownloadMedia(r0.currentMessageObject) : false;
                                        if (r0.cancelLoading || !r3) {
                                            r0.buttonState = 0;
                                            z4 = false;
                                        } else {
                                            r0.buttonState = 1;
                                        }
                                    }
                                    r0.radialProgress.setBackground(getDrawableForCurrentState(), z4, z3);
                                    r0.radialProgress.setProgress(f, false);
                                    invalidate();
                                }
                                if (r0.hasMiniProgress == 0) {
                                    r0.radialProgress.setMiniBackground(null, false, z3);
                                }
                            }
                        }
                    }
                    if ((r0.currentMessageObject.isOut() && r0.currentMessageObject.isSending()) || (r0.currentMessageObject.isSendError() && i != 0)) {
                        DownloadController.getInstance(r0.currentAccount).addLoadingFileObserver(r0.currentMessageObject.messageOwner.attachPath, r0.currentMessageObject, r0);
                        r0.buttonState = 4;
                        r0.radialProgress.setBackground(getDrawableForCurrentState(), i ^ 1, z3);
                        if (i == 0) {
                            fileProgress = ImageLoader.getInstance().getFileProgress(r0.currentMessageObject.messageOwner.attachPath);
                            if (fileProgress == null && SendMessagesHelper.getInstance(r0.currentAccount).isSendingMessage(r0.currentMessageObject.getId())) {
                                fileProgress = Float.valueOf(1.0f);
                            }
                            radialProgress = r0.radialProgress;
                            if (fileProgress != null) {
                                f = fileProgress.floatValue();
                            }
                            radialProgress.setProgress(f, false);
                        } else {
                            r0.radialProgress.setProgress(0.0f, false);
                        }
                    } else if (r0.hasMiniProgress != 0) {
                        r0.radialProgress.setMiniProgressBackgroundColor(Theme.getColor(r0.currentMessageObject.isOutOwner() ? Theme.key_chat_outLoader : Theme.key_chat_inLoader));
                        isSendingMessage = MediaController.getInstance().isPlayingMessage(r0.currentMessageObject);
                        if (isSendingMessage) {
                            if (!isSendingMessage || !MediaController.getInstance().isMessagePaused()) {
                                r0.buttonState = 1;
                                r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z3);
                                if (r0.hasMiniProgress != 1) {
                                    DownloadController.getInstance(r0.currentAccount).removeLoadingFileObserver(r0);
                                    r0.miniButtonState = -1;
                                } else {
                                    DownloadController.getInstance(r0.currentAccount).addLoadingFileObserver(attachFileName, r0.currentMessageObject, r0);
                                    if (FileLoader.getInstance(r0.currentAccount).isLoadingFile(attachFileName)) {
                                        r0.radialProgress.setProgress(0.0f, z3);
                                        r0.miniButtonState = 0;
                                    } else {
                                        r0.miniButtonState = 1;
                                        fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                                        if (fileProgress == null) {
                                            r0.radialProgress.setProgress(fileProgress.floatValue(), z3);
                                        } else {
                                            r0.radialProgress.setProgress(0.0f, z3);
                                        }
                                    }
                                }
                                radialProgress2 = r0.radialProgress;
                                miniDrawableForCurrentState = getMiniDrawableForCurrentState();
                                if (r0.miniButtonState == 1) {
                                    z4 = false;
                                }
                                radialProgress2.setMiniBackground(miniDrawableForCurrentState, z4, z3);
                            }
                        }
                        r0.buttonState = 0;
                        r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z3);
                        if (r0.hasMiniProgress != 1) {
                            DownloadController.getInstance(r0.currentAccount).addLoadingFileObserver(attachFileName, r0.currentMessageObject, r0);
                            if (FileLoader.getInstance(r0.currentAccount).isLoadingFile(attachFileName)) {
                                r0.miniButtonState = 1;
                                fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                                if (fileProgress == null) {
                                    r0.radialProgress.setProgress(0.0f, z3);
                                } else {
                                    r0.radialProgress.setProgress(fileProgress.floatValue(), z3);
                                }
                            } else {
                                r0.radialProgress.setProgress(0.0f, z3);
                                r0.miniButtonState = 0;
                            }
                        } else {
                            DownloadController.getInstance(r0.currentAccount).removeLoadingFileObserver(r0);
                            r0.miniButtonState = -1;
                        }
                        radialProgress2 = r0.radialProgress;
                        miniDrawableForCurrentState = getMiniDrawableForCurrentState();
                        if (r0.miniButtonState == 1) {
                            z4 = false;
                        }
                        radialProgress2.setMiniBackground(miniDrawableForCurrentState, z4, z3);
                    } else if (z2) {
                        DownloadController.getInstance(r0.currentAccount).removeLoadingFileObserver(r0);
                        z5 = MediaController.getInstance().isPlayingMessage(r0.currentMessageObject);
                        if (z5) {
                            if (!z5 || !MediaController.getInstance().isMessagePaused()) {
                                r0.buttonState = 1;
                                r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z3);
                            }
                        }
                        r0.buttonState = 0;
                        r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z3);
                    } else {
                        DownloadController.getInstance(r0.currentAccount).addLoadingFileObserver(attachFileName, r0.currentMessageObject, r0);
                        if (FileLoader.getInstance(r0.currentAccount).isLoadingFile(attachFileName)) {
                            r0.buttonState = 4;
                            fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                            if (fileProgress != null) {
                                r0.radialProgress.setProgress(fileProgress.floatValue(), z3);
                            } else {
                                r0.radialProgress.setProgress(0.0f, z3);
                            }
                            r0.radialProgress.setBackground(getDrawableForCurrentState(), true, z3);
                        } else {
                            r0.buttonState = 2;
                            r0.radialProgress.setProgress(0.0f, z3);
                            r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z3);
                        }
                    }
                    updatePlayingMessageProgress();
                    if (r0.hasMiniProgress == 0) {
                        r0.radialProgress.setMiniBackground(null, false, z3);
                    }
                }

                private void didPressedMiniButton(boolean z) {
                    if (!this.miniButtonState) {
                        this.miniButtonState = 1;
                        this.radialProgress.setProgress(0.0f, false);
                        if (!this.documentAttachType) {
                            if (!this.documentAttachType) {
                                if (this.documentAttachType) {
                                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, this.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
                                }
                                this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
                                invalidate();
                            }
                        }
                        FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
                        this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
                        invalidate();
                    } else if (this.miniButtonState) {
                        if ((this.documentAttachType || this.documentAttachType) && MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                            MediaController.getInstance().cleanupPlayer(true, true);
                        }
                        this.miniButtonState = 0;
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                        this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
                        invalidate();
                    }
                }

                private void didPressedButton(boolean z) {
                    boolean z2 = z;
                    int i = 2;
                    if (this.buttonState == 0) {
                        if (r0.documentAttachType != 3) {
                            if (r0.documentAttachType != 5) {
                                r0.cancelLoading = false;
                                r0.radialProgress.setProgress(0.0f, false);
                                FileLocation fileLocation = null;
                                ImageReceiver imageReceiver;
                                TLObject tLObject;
                                String str;
                                if (r0.currentMessageObject.type == 1) {
                                    r0.photoImage.setForceLoading(true);
                                    imageReceiver = r0.photoImage;
                                    tLObject = r0.currentPhotoObject.location;
                                    str = r0.currentPhotoFilter;
                                    if (r0.currentPhotoObjectThumb != null) {
                                        fileLocation = r0.currentPhotoObjectThumb.location;
                                    }
                                    imageReceiver.setImage(tLObject, str, fileLocation, r0.currentPhotoFilterThumb, r0.currentPhotoObject.size, null, r0.currentMessageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
                                } else if (r0.currentMessageObject.type == 8) {
                                    r0.currentMessageObject.gifState = 2.0f;
                                    r0.photoImage.setForceLoading(true);
                                    imageReceiver = r0.photoImage;
                                    tLObject = r0.currentMessageObject.messageOwner.media.document;
                                    if (r0.currentPhotoObject != null) {
                                        fileLocation = r0.currentPhotoObject.location;
                                    }
                                    imageReceiver.setImage(tLObject, null, fileLocation, r0.currentPhotoFilterThumb, r0.currentMessageObject.messageOwner.media.document.size, null, 0);
                                } else if (r0.currentMessageObject.isRoundVideo()) {
                                    if (r0.currentMessageObject.isSecretMedia()) {
                                        FileLoader.getInstance(r0.currentAccount).loadFile(r0.currentMessageObject.getDocument(), true, 1);
                                    } else {
                                        r0.currentMessageObject.gifState = 2.0f;
                                        tLObject = r0.currentMessageObject.getDocument();
                                        r0.photoImage.setForceLoading(true);
                                        imageReceiver = r0.photoImage;
                                        if (r0.currentPhotoObject != null) {
                                            fileLocation = r0.currentPhotoObject.location;
                                        }
                                        imageReceiver.setImage(tLObject, null, fileLocation, r0.currentPhotoFilterThumb, tLObject.size, null, 0);
                                    }
                                } else if (r0.currentMessageObject.type == 9) {
                                    FileLoader.getInstance(r0.currentAccount).loadFile(r0.currentMessageObject.messageOwner.media.document, false, 0);
                                } else if (r0.documentAttachType == 4) {
                                    FileLoader instance = FileLoader.getInstance(r0.currentAccount);
                                    Document document = r0.documentAttach;
                                    if (!r0.currentMessageObject.shouldEncryptPhotoOrVideo()) {
                                        i = 0;
                                    }
                                    instance.loadFile(document, true, i);
                                } else if (r0.currentMessageObject.type != 0 || r0.documentAttachType == 0) {
                                    r0.photoImage.setForceLoading(true);
                                    imageReceiver = r0.photoImage;
                                    tLObject = r0.currentPhotoObject.location;
                                    str = r0.currentPhotoFilter;
                                    if (r0.currentPhotoObjectThumb != null) {
                                        fileLocation = r0.currentPhotoObjectThumb.location;
                                    }
                                    imageReceiver.setImage(tLObject, str, fileLocation, r0.currentPhotoFilterThumb, 0, null, 0);
                                } else if (r0.documentAttachType == 2) {
                                    r0.photoImage.setForceLoading(true);
                                    r0.photoImage.setImage(r0.currentMessageObject.messageOwner.media.webpage.document, null, r0.currentPhotoObject.location, r0.currentPhotoFilterThumb, r0.currentMessageObject.messageOwner.media.webpage.document.size, null, 0);
                                    r0.currentMessageObject.gifState = 2.0f;
                                } else if (r0.documentAttachType == 1) {
                                    FileLoader.getInstance(r0.currentAccount).loadFile(r0.currentMessageObject.messageOwner.media.webpage.document, false, 0);
                                }
                                r0.buttonState = 1;
                                r0.radialProgress.setBackground(getDrawableForCurrentState(), true, z2);
                                invalidate();
                                return;
                            }
                        }
                        if (r0.miniButtonState == 0) {
                            FileLoader.getInstance(r0.currentAccount).loadFile(r0.documentAttach, true, 0);
                        }
                        if (r0.delegate.needPlayMessage(r0.currentMessageObject)) {
                            if (r0.hasMiniProgress == 2 && r0.miniButtonState != 1) {
                                r0.miniButtonState = 1;
                                r0.radialProgress.setProgress(0.0f, false);
                                r0.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
                            }
                            updatePlayingMessageProgress();
                            r0.buttonState = 1;
                            r0.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                            invalidate();
                        }
                    } else if (r0.buttonState == 1) {
                        if (r0.documentAttachType != 3) {
                            if (r0.documentAttachType != 5) {
                                if (!r0.currentMessageObject.isOut() || !r0.currentMessageObject.isSending()) {
                                    r0.cancelLoading = true;
                                    if (r0.documentAttachType != 4) {
                                        if (r0.documentAttachType != 1) {
                                            if (!(r0.currentMessageObject.type == 0 || r0.currentMessageObject.type == 1 || r0.currentMessageObject.type == 8)) {
                                                if (r0.currentMessageObject.type != 5) {
                                                    if (r0.currentMessageObject.type == 9) {
                                                        FileLoader.getInstance(r0.currentAccount).cancelLoadFile(r0.currentMessageObject.messageOwner.media.document);
                                                    }
                                                    r0.buttonState = 0;
                                                    r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z2);
                                                    invalidate();
                                                    return;
                                                }
                                            }
                                            ImageLoader.getInstance().cancelForceLoadingForImageReceiver(r0.photoImage);
                                            r0.photoImage.cancelLoadImage();
                                            r0.buttonState = 0;
                                            r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z2);
                                            invalidate();
                                            return;
                                        }
                                    }
                                    FileLoader.getInstance(r0.currentAccount).cancelLoadFile(r0.documentAttach);
                                    r0.buttonState = 0;
                                    r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z2);
                                    invalidate();
                                    return;
                                } else if (!r0.radialProgress.isDrawCheckDrawable()) {
                                    r0.delegate.didPressedCancelSendButton(r0);
                                    return;
                                } else {
                                    return;
                                }
                            }
                        }
                        if (MediaController.getInstance().pauseMessage(r0.currentMessageObject)) {
                            r0.buttonState = 0;
                            r0.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                            invalidate();
                        }
                    } else if (r0.buttonState == 2) {
                        if (r0.documentAttachType != 3) {
                            if (r0.documentAttachType != 5) {
                                r0.photoImage.setAllowStartAnimation(true);
                                r0.photoImage.startAnimation();
                                r0.currentMessageObject.gifState = 0.0f;
                                r0.buttonState = -1;
                                r0.radialProgress.setBackground(getDrawableForCurrentState(), false, z2);
                                return;
                            }
                        }
                        r0.radialProgress.setProgress(0.0f, false);
                        FileLoader.getInstance(r0.currentAccount).loadFile(r0.documentAttach, true, 0);
                        r0.buttonState = 4;
                        r0.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
                        invalidate();
                    } else if (r0.buttonState == 3) {
                        if (r0.hasMiniProgress == 2 && r0.miniButtonState != 1) {
                            r0.miniButtonState = 1;
                            r0.radialProgress.setProgress(0.0f, false);
                            r0.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
                        }
                        r0.delegate.didPressedImage(r0);
                    } else if (r0.buttonState != 4) {
                    } else {
                        if (r0.documentAttachType != 3 && r0.documentAttachType != 5) {
                            return;
                        }
                        if ((!r0.currentMessageObject.isOut() || !r0.currentMessageObject.isSending()) && !r0.currentMessageObject.isSendError()) {
                            FileLoader.getInstance(r0.currentAccount).cancelLoadFile(r0.documentAttach);
                            r0.buttonState = 2;
                            r0.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                            invalidate();
                        } else if (r0.delegate != null) {
                            r0.delegate.didPressedCancelSendButton(r0);
                        }
                    }
                }

                public void onFailedDownload(String str) {
                    if (this.documentAttachType != 3) {
                        if (this.documentAttachType != 5) {
                            str = null;
                            updateButtonState(str);
                        }
                    }
                    str = true;
                    updateButtonState(str);
                }

                public void onSuccessDownload(String str) {
                    if (this.documentAttachType != 3) {
                        if (this.documentAttachType != 5) {
                            this.radialProgress.setProgress(1.0f, true);
                            if (this.currentMessageObject.type != null) {
                                if (this.photoNotSet == null || ((this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) && this.currentMessageObject.gifState != NUM)) {
                                    if ((this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) && this.currentMessageObject.gifState != NUM) {
                                        this.photoNotSet = null;
                                        this.buttonState = 2;
                                        didPressedButton(true);
                                    } else {
                                        updateButtonState(true);
                                    }
                                }
                                if (this.photoNotSet != null) {
                                    setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
                                    return;
                                }
                                return;
                            } else if (this.documentAttachType == 2 && this.currentMessageObject.gifState != NUM) {
                                this.buttonState = 2;
                                didPressedButton(true);
                                return;
                            } else if (this.photoNotSet == null) {
                                updateButtonState(true);
                                return;
                            } else {
                                setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
                                return;
                            }
                        }
                    }
                    updateButtonState(true);
                    updateWaveform();
                }

                public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
                    if (this.currentMessageObject != null && z && !z2 && this.currentMessageObject.mediaExists == null && this.currentMessageObject.attachPathExists == null) {
                        this.currentMessageObject.mediaExists = true;
                        updateButtonState(true);
                    }
                }

                public void onProgressDownload(String str, float f) {
                    this.radialProgress.setProgress(f, true);
                    if (this.documentAttachType != 3) {
                        if (this.documentAttachType != 5) {
                            if (this.hasMiniProgress != null) {
                                if (this.miniButtonState != 1) {
                                    updateButtonState(false);
                                    return;
                                }
                                return;
                            } else if (this.buttonState != 1) {
                                updateButtonState(false);
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                    if (this.hasMiniProgress != null) {
                        if (this.miniButtonState != 1) {
                            updateButtonState(false);
                        }
                    } else if (this.buttonState != 4) {
                        updateButtonState(false);
                    }
                }

                public void onProgressUpload(String str, float f, boolean z) {
                    this.radialProgress.setProgress(f, true);
                    if (f == 1.0f && this.currentPosition != null && SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId()) != null && this.buttonState == 1) {
                        this.drawRadialCheckBackground = true;
                        this.radialProgress.setCheckBackground(0.0f, true);
                    }
                }

                public void onProvideStructure(ViewStructure viewStructure) {
                    super.onProvideStructure(viewStructure);
                    if (this.allowAssistant && VERSION.SDK_INT >= 23) {
                        if (this.currentMessageObject.messageText != null && this.currentMessageObject.messageText.length() > 0) {
                            viewStructure.setText(this.currentMessageObject.messageText);
                        } else if (this.currentMessageObject.caption != null && this.currentMessageObject.caption.length() > 0) {
                            viewStructure.setText(this.currentMessageObject.caption);
                        }
                    }
                }

                public void setDelegate(ChatMessageCellDelegate chatMessageCellDelegate) {
                    this.delegate = chatMessageCellDelegate;
                }

                public void setAllowAssistant(boolean z) {
                    this.allowAssistant = z;
                }

                private void measureTime(MessageObject messageObject) {
                    Object replace;
                    String format;
                    if (messageObject.messageOwner.post_author != null) {
                        replace = messageObject.messageOwner.post_author.replace("\n", TtmlNode.ANONYMOUS_REGION_ID);
                    } else if (messageObject.messageOwner.fwd_from == null || messageObject.messageOwner.fwd_from.post_author == null) {
                        if (!messageObject.isOutOwner() && messageObject.messageOwner.from_id > 0 && messageObject.messageOwner.post) {
                            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                            if (user != null) {
                                replace = ContactsController.formatName(user.first_name, user.last_name).replace('\n', ' ');
                            }
                        }
                        replace = null;
                    } else {
                        replace = messageObject.messageOwner.fwd_from.post_author.replace("\n", TtmlNode.ANONYMOUS_REGION_ID);
                    }
                    User user2 = this.currentMessageObject.isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id)) : null;
                    if (messageObject.isLiveLocation() || messageObject.getDialogId() == 777000 || messageObject.messageOwner.via_bot_id != 0 || messageObject.messageOwner.via_bot_name != null || ((user2 != null && user2.bot) || (messageObject.messageOwner.flags & 32768) == 0 || this.currentPosition != null)) {
                        format = LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000);
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(LocaleController.getString("EditedMessage", C0446R.string.EditedMessage));
                        stringBuilder.append(" ");
                        stringBuilder.append(LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000));
                        format = stringBuilder.toString();
                    }
                    if (replace != null) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(", ");
                        stringBuilder2.append(format);
                        this.currentTimeString = stringBuilder2.toString();
                    } else {
                        this.currentTimeString = format;
                    }
                    int ceil = (int) Math.ceil((double) Theme.chat_timePaint.measureText(this.currentTimeString));
                    this.timeWidth = ceil;
                    this.timeTextWidth = ceil;
                    if ((messageObject.messageOwner.flags & 1024) != 0) {
                        this.currentViewsString = String.format("%s", new Object[]{LocaleController.formatShortNumber(Math.max(1, messageObject.messageOwner.views), null)});
                        this.viewsTextWidth = (int) Math.ceil((double) Theme.chat_timePaint.measureText(this.currentViewsString));
                        this.timeWidth += (this.viewsTextWidth + Theme.chat_msgInViewsDrawable.getIntrinsicWidth()) + AndroidUtilities.dp(10.0f);
                    }
                    if (replace != null) {
                        if (this.availableTimeWidth == 0) {
                            this.availableTimeWidth = AndroidUtilities.dp(1000.0f);
                        }
                        int i = this.availableTimeWidth - this.timeWidth;
                        if (messageObject.isOutOwner()) {
                            if (messageObject.type == 5) {
                                i -= AndroidUtilities.dp(20.0f);
                            } else {
                                i -= AndroidUtilities.dp(96.0f);
                            }
                        }
                        messageObject = (int) Math.ceil((double) Theme.chat_timePaint.measureText(replace, 0, replace.length()));
                        if (messageObject > i) {
                            if (i <= 0) {
                                replace = TtmlNode.ANONYMOUS_REGION_ID;
                                messageObject = null;
                            } else {
                                replace = TextUtils.ellipsize(replace, Theme.chat_timePaint, (float) i, TruncateAt.END);
                                messageObject = i;
                            }
                        }
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(replace);
                        stringBuilder3.append(this.currentTimeString);
                        this.currentTimeString = stringBuilder3.toString();
                        this.timeTextWidth += messageObject;
                        this.timeWidth += messageObject;
                    }
                }

                private boolean isDrawSelectedBackground() {
                    return (isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted);
                }

                private boolean isOpenChatByShare(MessageObject messageObject) {
                    return (messageObject.messageOwner.fwd_from == null || messageObject.messageOwner.fwd_from.saved_from_peer == null) ? null : true;
                }

                private boolean checkNeedDrawShareButton(MessageObject messageObject) {
                    boolean z = false;
                    if ((this.currentPosition != null && !this.currentPosition.last) || messageObject.eventId != 0) {
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
                            if (!((messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || messageObject.messageOwner.media == null)) {
                                if (!(messageObject.messageOwner.media instanceof TL_messageMediaWebPage) || (messageObject.messageOwner.media.webpage instanceof TL_webPage)) {
                                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                                    if (user != null && user.bot) {
                                        return true;
                                    }
                                    if (!messageObject.isOut()) {
                                        if (!(messageObject.messageOwner.media instanceof TL_messageMediaGame)) {
                                            if (!(messageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                                                if (messageObject.isMegagroup()) {
                                                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(messageObject.messageOwner.to_id.channel_id));
                                                    if (!(chat == null || chat.username == null || chat.username.length() <= 0 || (messageObject.messageOwner.media instanceof TL_messageMediaContact) || (messageObject.messageOwner.media instanceof TL_messageMediaGeo) != null)) {
                                                        z = true;
                                                    }
                                                    return z;
                                                }
                                            }
                                        }
                                        return true;
                                    }
                                }
                            }
                            return false;
                        } else if ((messageObject.messageOwner.from_id < 0 || messageObject.messageOwner.post) && messageObject.messageOwner.to_id.channel_id != 0 && ((messageObject.messageOwner.via_bot_id == 0 && messageObject.messageOwner.reply_to_msg_id == 0) || messageObject.type != 13)) {
                            return true;
                        }
                        return false;
                    }
                }

                public boolean isInsideBackground(float f, float f2) {
                    return this.currentBackgroundDrawable != null && f >= ((float) (getLeft() + this.backgroundDrawableLeft)) && f <= ((float) ((getLeft() + this.backgroundDrawableLeft) + this.backgroundDrawableRight));
                }

                private void updateCurrentUserAndChat() {
                    MessagesController instance = MessagesController.getInstance(this.currentAccount);
                    MessageFwdHeader messageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
                    int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    if (messageFwdHeader != null && messageFwdHeader.channel_id != 0 && this.currentMessageObject.getDialogId() == ((long) clientUserId)) {
                        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(messageFwdHeader.channel_id));
                    } else if (messageFwdHeader == null || messageFwdHeader.saved_from_peer == null) {
                        if (messageFwdHeader != null && messageFwdHeader.from_id != 0 && messageFwdHeader.channel_id == 0 && this.currentMessageObject.getDialogId() == ((long) clientUserId)) {
                            this.currentUser = instance.getUser(Integer.valueOf(messageFwdHeader.from_id));
                        } else if (this.currentMessageObject.isFromUser()) {
                            this.currentUser = instance.getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
                        } else if (this.currentMessageObject.messageOwner.from_id < 0) {
                            this.currentChat = instance.getChat(Integer.valueOf(-this.currentMessageObject.messageOwner.from_id));
                        } else if (this.currentMessageObject.messageOwner.post) {
                            this.currentChat = instance.getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
                        }
                    } else if (messageFwdHeader.saved_from_peer.user_id != 0) {
                        if (messageFwdHeader.from_id != 0) {
                            this.currentUser = instance.getUser(Integer.valueOf(messageFwdHeader.from_id));
                        } else {
                            this.currentUser = instance.getUser(Integer.valueOf(messageFwdHeader.saved_from_peer.user_id));
                        }
                    } else if (messageFwdHeader.saved_from_peer.channel_id != 0) {
                        if (!this.currentMessageObject.isSavedFromMegagroup() || messageFwdHeader.from_id == 0) {
                            this.currentChat = instance.getChat(Integer.valueOf(messageFwdHeader.saved_from_peer.channel_id));
                        } else {
                            this.currentUser = instance.getUser(Integer.valueOf(messageFwdHeader.from_id));
                        }
                    } else if (messageFwdHeader.saved_from_peer.chat_id == 0) {
                    } else {
                        if (messageFwdHeader.from_id != 0) {
                            this.currentUser = instance.getUser(Integer.valueOf(messageFwdHeader.from_id));
                        } else {
                            this.currentChat = instance.getChat(Integer.valueOf(messageFwdHeader.saved_from_peer.chat_id));
                        }
                    }
                }

                private void setMessageObjectInternal(org.telegram.messenger.MessageObject r35) {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                    /*
                    r34 = this;
                    r1 = r34;
                    r2 = r35;
                    r3 = r2.messageOwner;
                    r3 = r3.flags;
                    r3 = r3 & 1024;
                    r4 = 1;
                    if (r3 == 0) goto L_0x0024;
                L_0x000d:
                    r3 = r1.currentMessageObject;
                    r3 = r3.viewsReloaded;
                    if (r3 != 0) goto L_0x0024;
                L_0x0013:
                    r3 = r1.currentAccount;
                    r3 = org.telegram.messenger.MessagesController.getInstance(r3);
                    r5 = r1.currentMessageObject;
                    r5 = r5.messageOwner;
                    r3.addToViewsQueue(r5);
                    r3 = r1.currentMessageObject;
                    r3.viewsReloaded = r4;
                L_0x0024:
                    r34.updateCurrentUserAndChat();
                    r3 = r1.isAvatarVisible;
                    r5 = 0;
                    r6 = 0;
                    if (r3 == 0) goto L_0x007f;
                L_0x002d:
                    r3 = r1.currentUser;
                    if (r3 == 0) goto L_0x004a;
                L_0x0031:
                    r3 = r1.currentUser;
                    r3 = r3.photo;
                    if (r3 == 0) goto L_0x0040;
                L_0x0037:
                    r3 = r1.currentUser;
                    r3 = r3.photo;
                    r3 = r3.photo_small;
                    r1.currentPhoto = r3;
                    goto L_0x0042;
                L_0x0040:
                    r1.currentPhoto = r5;
                L_0x0042:
                    r3 = r1.avatarDrawable;
                    r7 = r1.currentUser;
                    r3.setInfo(r7);
                    goto L_0x0072;
                L_0x004a:
                    r3 = r1.currentChat;
                    if (r3 == 0) goto L_0x0067;
                L_0x004e:
                    r3 = r1.currentChat;
                    r3 = r3.photo;
                    if (r3 == 0) goto L_0x005d;
                L_0x0054:
                    r3 = r1.currentChat;
                    r3 = r3.photo;
                    r3 = r3.photo_small;
                    r1.currentPhoto = r3;
                    goto L_0x005f;
                L_0x005d:
                    r1.currentPhoto = r5;
                L_0x005f:
                    r3 = r1.avatarDrawable;
                    r7 = r1.currentChat;
                    r3.setInfo(r7);
                    goto L_0x0072;
                L_0x0067:
                    r1.currentPhoto = r5;
                    r3 = r1.avatarDrawable;
                    r7 = r2.messageOwner;
                    r7 = r7.from_id;
                    r3.setInfo(r7, r5, r5, r6);
                L_0x0072:
                    r8 = r1.avatarImage;
                    r9 = r1.currentPhoto;
                    r10 = "50_50";
                    r11 = r1.avatarDrawable;
                    r12 = 0;
                    r13 = 0;
                    r8.setImage(r9, r10, r11, r12, r13);
                L_0x007f:
                    r34.measureTime(r35);
                    r1.namesOffset = r6;
                    r3 = r2.messageOwner;
                    r3 = r3.via_bot_id;
                    if (r3 == 0) goto L_0x00e5;
                L_0x008a:
                    r3 = r1.currentAccount;
                    r3 = org.telegram.messenger.MessagesController.getInstance(r3);
                    r7 = r2.messageOwner;
                    r7 = r7.via_bot_id;
                    r7 = java.lang.Integer.valueOf(r7);
                    r3 = r3.getUser(r7);
                    if (r3 == 0) goto L_0x00e0;
                L_0x009e:
                    r7 = r3.username;
                    if (r7 == 0) goto L_0x00e0;
                L_0x00a2:
                    r7 = r3.username;
                    r7 = r7.length();
                    if (r7 <= 0) goto L_0x00e0;
                L_0x00aa:
                    r7 = new java.lang.StringBuilder;
                    r7.<init>();
                    r8 = "@";
                    r7.append(r8);
                    r8 = r3.username;
                    r7.append(r8);
                    r7 = r7.toString();
                    r8 = " via <b>%s</b>";
                    r9 = new java.lang.Object[r4];
                    r9[r6] = r7;
                    r8 = java.lang.String.format(r8, r9);
                    r8 = org.telegram.messenger.AndroidUtilities.replaceTags(r8);
                    r9 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
                    r10 = r8.length();
                    r9 = r9.measureText(r8, r6, r10);
                    r9 = (double) r9;
                    r9 = java.lang.Math.ceil(r9);
                    r9 = (int) r9;
                    r1.viaWidth = r9;
                    r1.currentViaBotUser = r3;
                    goto L_0x00e2;
                L_0x00e0:
                    r7 = r5;
                    r8 = r7;
                L_0x00e2:
                    r3 = r7;
                    r7 = r8;
                    goto L_0x012d;
                L_0x00e5:
                    r3 = r2.messageOwner;
                    r3 = r3.via_bot_name;
                    if (r3 == 0) goto L_0x012b;
                L_0x00eb:
                    r3 = r2.messageOwner;
                    r3 = r3.via_bot_name;
                    r3 = r3.length();
                    if (r3 <= 0) goto L_0x012b;
                L_0x00f5:
                    r3 = new java.lang.StringBuilder;
                    r3.<init>();
                    r7 = "@";
                    r3.append(r7);
                    r7 = r2.messageOwner;
                    r7 = r7.via_bot_name;
                    r3.append(r7);
                    r3 = r3.toString();
                    r7 = " via <b>%s</b>";
                    r8 = new java.lang.Object[r4];
                    r8[r6] = r3;
                    r7 = java.lang.String.format(r7, r8);
                    r7 = org.telegram.messenger.AndroidUtilities.replaceTags(r7);
                    r8 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
                    r9 = r7.length();
                    r8 = r8.measureText(r7, r6, r9);
                    r8 = (double) r8;
                    r8 = java.lang.Math.ceil(r8);
                    r8 = (int) r8;
                    r1.viaWidth = r8;
                    goto L_0x012d;
                L_0x012b:
                    r3 = r5;
                    r7 = r3;
                L_0x012d:
                    r8 = r1.drawName;
                    if (r8 == 0) goto L_0x013f;
                L_0x0131:
                    r8 = r1.isChat;
                    if (r8 == 0) goto L_0x013f;
                L_0x0135:
                    r8 = r1.currentMessageObject;
                    r8 = r8.isOutOwner();
                    if (r8 != 0) goto L_0x013f;
                L_0x013d:
                    r8 = r4;
                    goto L_0x0140;
                L_0x013f:
                    r8 = r6;
                L_0x0140:
                    r9 = r2.messageOwner;
                    r9 = r9.fwd_from;
                    if (r9 == 0) goto L_0x014c;
                L_0x0146:
                    r9 = r2.type;
                    r10 = 14;
                    if (r9 != r10) goto L_0x0150;
                L_0x014c:
                    if (r3 == 0) goto L_0x0150;
                L_0x014e:
                    r9 = r4;
                    goto L_0x0151;
                L_0x0150:
                    r9 = r6;
                L_0x0151:
                    r11 = 2;
                    r13 = 32;
                    r14 = 10;
                    r12 = 5;
                    r10 = 13;
                    if (r8 != 0) goto L_0x0167;
                L_0x015b:
                    if (r9 == 0) goto L_0x015e;
                L_0x015d:
                    goto L_0x0167;
                L_0x015e:
                    r1.currentNameString = r5;
                    r1.nameLayout = r5;
                    r1.nameWidth = r6;
                    r4 = r5;
                    goto L_0x0367;
                L_0x0167:
                    r1.drawNameLayout = r4;
                    r5 = r34.getMaxNameWidth();
                    r1.nameWidth = r5;
                    r5 = r1.nameWidth;
                    if (r5 >= 0) goto L_0x017b;
                L_0x0173:
                    r5 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                    r1.nameWidth = r5;
                L_0x017b:
                    r5 = r1.currentUser;
                    if (r5 == 0) goto L_0x01be;
                L_0x017f:
                    r5 = r1.currentMessageObject;
                    r5 = r5.isOutOwner();
                    if (r5 != 0) goto L_0x01be;
                L_0x0187:
                    r5 = r1.currentMessageObject;
                    r5 = r5.type;
                    if (r5 == r10) goto L_0x01be;
                L_0x018d:
                    r5 = r1.currentMessageObject;
                    r5 = r5.type;
                    if (r5 == r12) goto L_0x01be;
                L_0x0193:
                    r5 = r1.delegate;
                    r15 = r1.currentUser;
                    r15 = r15.id;
                    r5 = r5.isChatAdminCell(r15);
                    if (r5 == 0) goto L_0x01be;
                L_0x019f:
                    r5 = "ChatAdmin";
                    r15 = NUM; // 0x7f0c0163 float:1.8609912E38 double:1.053097574E-314;
                    r5 = org.telegram.messenger.LocaleController.getString(r5, r15);
                    r15 = org.telegram.ui.ActionBar.Theme.chat_adminPaint;
                    r15 = r15.measureText(r5);
                    r18 = r5;
                    r4 = (double) r15;
                    r4 = java.lang.Math.ceil(r4);
                    r4 = (int) r4;
                    r5 = r1.nameWidth;
                    r5 = r5 - r4;
                    r1.nameWidth = r5;
                    r19 = r18;
                    goto L_0x01c1;
                L_0x01be:
                    r4 = r6;
                    r19 = 0;
                L_0x01c1:
                    if (r8 == 0) goto L_0x01e0;
                L_0x01c3:
                    r5 = r1.currentUser;
                    if (r5 == 0) goto L_0x01d0;
                L_0x01c7:
                    r5 = r1.currentUser;
                    r5 = org.telegram.messenger.UserObject.getUserName(r5);
                    r1.currentNameString = r5;
                    goto L_0x01e4;
                L_0x01d0:
                    r5 = r1.currentChat;
                    if (r5 == 0) goto L_0x01db;
                L_0x01d4:
                    r5 = r1.currentChat;
                    r5 = r5.title;
                    r1.currentNameString = r5;
                    goto L_0x01e4;
                L_0x01db:
                    r5 = "DELETED";
                    r1.currentNameString = r5;
                    goto L_0x01e4;
                L_0x01e0:
                    r5 = "";
                    r1.currentNameString = r5;
                L_0x01e4:
                    r5 = r1.currentNameString;
                    r5 = r5.replace(r14, r13);
                    r8 = org.telegram.ui.ActionBar.Theme.chat_namePaint;
                    r15 = r1.nameWidth;
                    if (r9 == 0) goto L_0x01f3;
                L_0x01f0:
                    r13 = r1.viaWidth;
                    goto L_0x01f4;
                L_0x01f3:
                    r13 = r6;
                L_0x01f4:
                    r15 = r15 - r13;
                    r13 = (float) r15;
                    r15 = android.text.TextUtils.TruncateAt.END;
                    r5 = android.text.TextUtils.ellipsize(r5, r8, r13, r15);
                    if (r9 == 0) goto L_0x02cb;
                L_0x01fe:
                    r8 = org.telegram.ui.ActionBar.Theme.chat_namePaint;
                    r9 = r5.length();
                    r8 = r8.measureText(r5, r6, r9);
                    r8 = (double) r8;
                    r8 = java.lang.Math.ceil(r8);
                    r8 = (int) r8;
                    r1.viaNameWidth = r8;
                    r8 = r1.viaNameWidth;
                    if (r8 == 0) goto L_0x021f;
                L_0x0214:
                    r8 = r1.viaNameWidth;
                    r9 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                    r8 = r8 + r9;
                    r1.viaNameWidth = r8;
                L_0x021f:
                    r8 = r1.currentMessageObject;
                    r8 = r8.type;
                    if (r8 == r10) goto L_0x023e;
                L_0x0225:
                    r8 = r1.currentMessageObject;
                    r8 = r8.type;
                    if (r8 != r12) goto L_0x022c;
                L_0x022b:
                    goto L_0x023e;
                L_0x022c:
                    r8 = r1.currentMessageObject;
                    r8 = r8.isOutOwner();
                    if (r8 == 0) goto L_0x0237;
                L_0x0234:
                    r8 = "chat_outViaBotNameText";
                    goto L_0x0239;
                L_0x0237:
                    r8 = "chat_inViaBotNameText";
                L_0x0239:
                    r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
                    goto L_0x0244;
                L_0x023e:
                    r8 = "chat_stickerViaBotNameText";
                    r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
                L_0x0244:
                    r9 = r1.currentNameString;
                    r9 = r9.length();
                    if (r9 <= 0) goto L_0x0291;
                L_0x024c:
                    r9 = new android.text.SpannableStringBuilder;
                    r13 = "%s via %s";
                    r15 = new java.lang.Object[r11];
                    r15[r6] = r5;
                    r17 = 1;
                    r15[r17] = r3;
                    r13 = java.lang.String.format(r13, r15);
                    r9.<init>(r13);
                    r13 = new org.telegram.ui.Components.TypefaceSpan;
                    r15 = android.graphics.Typeface.DEFAULT;
                    r13.<init>(r15, r6, r8);
                    r15 = r5.length();
                    r15 = r15 + 1;
                    r18 = r5.length();
                    r16 = 4;
                    r14 = r18 + 4;
                    r11 = 33;
                    r9.setSpan(r13, r15, r14, r11);
                    r13 = new org.telegram.ui.Components.TypefaceSpan;
                    r14 = "fonts/rmedium.ttf";
                    r14 = org.telegram.messenger.AndroidUtilities.getTypeface(r14);
                    r13.<init>(r14, r6, r8);
                    r5 = r5.length();
                    r5 = r5 + r12;
                    r8 = r9.length();
                    r9.setSpan(r13, r5, r8, r11);
                    goto L_0x02c0;
                L_0x0291:
                    r11 = 33;
                    r9 = new android.text.SpannableStringBuilder;
                    r5 = "via %s";
                    r13 = 1;
                    r14 = new java.lang.Object[r13];
                    r14[r6] = r3;
                    r5 = java.lang.String.format(r5, r14);
                    r9.<init>(r5);
                    r5 = new org.telegram.ui.Components.TypefaceSpan;
                    r13 = android.graphics.Typeface.DEFAULT;
                    r5.<init>(r13, r6, r8);
                    r13 = 4;
                    r9.setSpan(r5, r6, r13, r11);
                    r5 = new org.telegram.ui.Components.TypefaceSpan;
                    r14 = "fonts/rmedium.ttf";
                    r14 = org.telegram.messenger.AndroidUtilities.getTypeface(r14);
                    r5.<init>(r14, r6, r8);
                    r8 = r9.length();
                    r9.setSpan(r5, r13, r8, r11);
                L_0x02c0:
                    r5 = org.telegram.ui.ActionBar.Theme.chat_namePaint;
                    r8 = r1.nameWidth;
                    r8 = (float) r8;
                    r11 = android.text.TextUtils.TruncateAt.END;
                    r5 = android.text.TextUtils.ellipsize(r9, r5, r8, r11);
                L_0x02cb:
                    r27 = r5;
                    r5 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0355 }
                    r28 = org.telegram.ui.ActionBar.Theme.chat_namePaint;	 Catch:{ Exception -> 0x0355 }
                    r8 = r1.nameWidth;	 Catch:{ Exception -> 0x0355 }
                    r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0355 }
                    r11 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Exception -> 0x0355 }
                    r29 = r8 + r11;	 Catch:{ Exception -> 0x0355 }
                    r30 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0355 }
                    r31 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0355 }
                    r32 = 0;	 Catch:{ Exception -> 0x0355 }
                    r33 = 0;	 Catch:{ Exception -> 0x0355 }
                    r26 = r5;	 Catch:{ Exception -> 0x0355 }
                    r26.<init>(r27, r28, r29, r30, r31, r32, r33);	 Catch:{ Exception -> 0x0355 }
                    r1.nameLayout = r5;	 Catch:{ Exception -> 0x0355 }
                    r5 = r1.nameLayout;	 Catch:{ Exception -> 0x0355 }
                    if (r5 == 0) goto L_0x031c;	 Catch:{ Exception -> 0x0355 }
                L_0x02ee:
                    r5 = r1.nameLayout;	 Catch:{ Exception -> 0x0355 }
                    r5 = r5.getLineCount();	 Catch:{ Exception -> 0x0355 }
                    if (r5 <= 0) goto L_0x031c;	 Catch:{ Exception -> 0x0355 }
                L_0x02f6:
                    r5 = r1.nameLayout;	 Catch:{ Exception -> 0x0355 }
                    r5 = r5.getLineWidth(r6);	 Catch:{ Exception -> 0x0355 }
                    r8 = (double) r5;	 Catch:{ Exception -> 0x0355 }
                    r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0355 }
                    r5 = (int) r8;	 Catch:{ Exception -> 0x0355 }
                    r1.nameWidth = r5;	 Catch:{ Exception -> 0x0355 }
                    r5 = r2.type;	 Catch:{ Exception -> 0x0355 }
                    if (r5 == r10) goto L_0x0313;	 Catch:{ Exception -> 0x0355 }
                L_0x0308:
                    r5 = r1.namesOffset;	 Catch:{ Exception -> 0x0355 }
                    r8 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;	 Catch:{ Exception -> 0x0355 }
                    r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x0355 }
                    r5 = r5 + r8;	 Catch:{ Exception -> 0x0355 }
                    r1.namesOffset = r5;	 Catch:{ Exception -> 0x0355 }
                L_0x0313:
                    r5 = r1.nameLayout;	 Catch:{ Exception -> 0x0355 }
                    r5 = r5.getLineLeft(r6);	 Catch:{ Exception -> 0x0355 }
                    r1.nameOffsetX = r5;	 Catch:{ Exception -> 0x0355 }
                    goto L_0x031e;	 Catch:{ Exception -> 0x0355 }
                L_0x031c:
                    r1.nameWidth = r6;	 Catch:{ Exception -> 0x0355 }
                L_0x031e:
                    if (r19 == 0) goto L_0x0351;	 Catch:{ Exception -> 0x0355 }
                L_0x0320:
                    r5 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0355 }
                    r20 = org.telegram.ui.ActionBar.Theme.chat_adminPaint;	 Catch:{ Exception -> 0x0355 }
                    r8 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0355 }
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x0355 }
                    r21 = r4 + r9;	 Catch:{ Exception -> 0x0355 }
                    r22 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0355 }
                    r23 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0355 }
                    r24 = 0;	 Catch:{ Exception -> 0x0355 }
                    r25 = 0;	 Catch:{ Exception -> 0x0355 }
                    r18 = r5;	 Catch:{ Exception -> 0x0355 }
                    r18.<init>(r19, r20, r21, r22, r23, r24, r25);	 Catch:{ Exception -> 0x0355 }
                    r1.adminLayout = r5;	 Catch:{ Exception -> 0x0355 }
                    r4 = r1.nameWidth;	 Catch:{ Exception -> 0x0355 }
                    r4 = (float) r4;	 Catch:{ Exception -> 0x0355 }
                    r5 = r1.adminLayout;	 Catch:{ Exception -> 0x0355 }
                    r5 = r5.getLineWidth(r6);	 Catch:{ Exception -> 0x0355 }
                    r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;	 Catch:{ Exception -> 0x0355 }
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x0355 }
                    r8 = (float) r9;	 Catch:{ Exception -> 0x0355 }
                    r5 = r5 + r8;	 Catch:{ Exception -> 0x0355 }
                    r4 = r4 + r5;	 Catch:{ Exception -> 0x0355 }
                    r4 = (int) r4;	 Catch:{ Exception -> 0x0355 }
                    r1.nameWidth = r4;	 Catch:{ Exception -> 0x0355 }
                    goto L_0x035a;	 Catch:{ Exception -> 0x0355 }
                L_0x0351:
                    r4 = 0;	 Catch:{ Exception -> 0x0355 }
                    r1.adminLayout = r4;	 Catch:{ Exception -> 0x0355 }
                    goto L_0x035a;
                L_0x0355:
                    r0 = move-exception;
                    r4 = r0;
                    org.telegram.messenger.FileLog.m3e(r4);
                L_0x035a:
                    r4 = r1.currentNameString;
                    r4 = r4.length();
                    if (r4 != 0) goto L_0x0366;
                L_0x0362:
                    r4 = 0;
                    r1.currentNameString = r4;
                    goto L_0x0367;
                L_0x0366:
                    r4 = 0;
                L_0x0367:
                    r1.currentForwardUser = r4;
                    r1.currentForwardNameString = r4;
                    r1.currentForwardChannel = r4;
                    r5 = r1.forwardedNameLayout;
                    r5[r6] = r4;
                    r5 = r1.forwardedNameLayout;
                    r8 = 1;
                    r5[r8] = r4;
                    r1.forwardedNameWidth = r6;
                    r4 = r1.drawForwardedName;
                    if (r4 == 0) goto L_0x0584;
                L_0x037c:
                    r4 = r35.needDrawForwarded();
                    if (r4 == 0) goto L_0x0584;
                L_0x0382:
                    r4 = r1.currentPosition;
                    if (r4 == 0) goto L_0x038c;
                L_0x0386:
                    r4 = r1.currentPosition;
                    r4 = r4.minY;
                    if (r4 != 0) goto L_0x0584;
                L_0x038c:
                    r4 = r2.messageOwner;
                    r4 = r4.fwd_from;
                    r4 = r4.channel_id;
                    if (r4 == 0) goto L_0x03aa;
                L_0x0394:
                    r4 = r1.currentAccount;
                    r4 = org.telegram.messenger.MessagesController.getInstance(r4);
                    r5 = r2.messageOwner;
                    r5 = r5.fwd_from;
                    r5 = r5.channel_id;
                    r5 = java.lang.Integer.valueOf(r5);
                    r4 = r4.getChat(r5);
                    r1.currentForwardChannel = r4;
                L_0x03aa:
                    r4 = r2.messageOwner;
                    r4 = r4.fwd_from;
                    r4 = r4.from_id;
                    if (r4 == 0) goto L_0x03c8;
                L_0x03b2:
                    r4 = r1.currentAccount;
                    r4 = org.telegram.messenger.MessagesController.getInstance(r4);
                    r5 = r2.messageOwner;
                    r5 = r5.fwd_from;
                    r5 = r5.from_id;
                    r5 = java.lang.Integer.valueOf(r5);
                    r4 = r4.getUser(r5);
                    r1.currentForwardUser = r4;
                L_0x03c8:
                    r4 = r1.currentForwardUser;
                    if (r4 != 0) goto L_0x03d0;
                L_0x03cc:
                    r4 = r1.currentForwardChannel;
                    if (r4 == 0) goto L_0x0584;
                L_0x03d0:
                    r4 = r1.currentForwardChannel;
                    if (r4 == 0) goto L_0x03fa;
                L_0x03d4:
                    r4 = r1.currentForwardUser;
                    if (r4 == 0) goto L_0x03f3;
                L_0x03d8:
                    r4 = "%s (%s)";
                    r5 = 2;
                    r8 = new java.lang.Object[r5];
                    r5 = r1.currentForwardChannel;
                    r5 = r5.title;
                    r8[r6] = r5;
                    r5 = r1.currentForwardUser;
                    r5 = org.telegram.messenger.UserObject.getUserName(r5);
                    r9 = 1;
                    r8[r9] = r5;
                    r4 = java.lang.String.format(r4, r8);
                    r1.currentForwardNameString = r4;
                    goto L_0x0406;
                L_0x03f3:
                    r4 = r1.currentForwardChannel;
                    r4 = r4.title;
                    r1.currentForwardNameString = r4;
                    goto L_0x0406;
                L_0x03fa:
                    r4 = r1.currentForwardUser;
                    if (r4 == 0) goto L_0x0406;
                L_0x03fe:
                    r4 = r1.currentForwardUser;
                    r4 = org.telegram.messenger.UserObject.getUserName(r4);
                    r1.currentForwardNameString = r4;
                L_0x0406:
                    r4 = r34.getMaxNameWidth();
                    r1.forwardedNameWidth = r4;
                    r4 = "From";
                    r5 = NUM; // 0x7f0c02ec float:1.861071E38 double:1.053097768E-314;
                    r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
                    r5 = "FromFormatted";
                    r8 = NUM; // 0x7f0c02f4 float:1.8610725E38 double:1.053097772E-314;
                    r5 = org.telegram.messenger.LocaleController.getString(r5, r8);
                    r8 = "%1$s";
                    r8 = r5.indexOf(r8);
                    r9 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;
                    r11 = new java.lang.StringBuilder;
                    r11.<init>();
                    r11.append(r4);
                    r4 = " ";
                    r11.append(r4);
                    r4 = r11.toString();
                    r4 = r9.measureText(r4);
                    r13 = (double) r4;
                    r13 = java.lang.Math.ceil(r13);
                    r4 = (int) r13;
                    r9 = r1.currentForwardNameString;
                    r11 = 10;
                    r13 = 32;
                    r9 = r9.replace(r11, r13);
                    r11 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
                    r13 = r1.forwardedNameWidth;
                    r13 = r13 - r4;
                    r4 = r1.viaWidth;
                    r13 = r13 - r4;
                    r4 = (float) r13;
                    r13 = android.text.TextUtils.TruncateAt.END;
                    r4 = android.text.TextUtils.ellipsize(r9, r11, r4, r13);
                    r9 = 1;
                    r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0464 }
                    r11[r6] = r4;	 Catch:{ Exception -> 0x0464 }
                    r9 = java.lang.String.format(r5, r11);	 Catch:{ Exception -> 0x0464 }
                    goto L_0x0468;
                L_0x0464:
                    r9 = r4.toString();
                L_0x0468:
                    if (r7 == 0) goto L_0x04ab;
                L_0x046a:
                    r5 = new android.text.SpannableStringBuilder;
                    r7 = "%s via %s";
                    r11 = 2;
                    r11 = new java.lang.Object[r11];
                    r11[r6] = r9;
                    r13 = 1;
                    r11[r13] = r3;
                    r7 = java.lang.String.format(r7, r11);
                    r5.<init>(r7);
                    r7 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;
                    r7 = r7.measureText(r9);
                    r13 = (double) r7;
                    r13 = java.lang.Math.ceil(r13);
                    r7 = (int) r13;
                    r1.viaNameWidth = r7;
                    r7 = new org.telegram.ui.Components.TypefaceSpan;
                    r9 = "fonts/rmedium.ttf";
                    r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9);
                    r7.<init>(r9);
                    r9 = r5.length();
                    r3 = r3.length();
                    r9 = r9 - r3;
                    r3 = 1;
                    r9 = r9 - r3;
                    r11 = r5.length();
                    r13 = 33;
                    r5.setSpan(r7, r9, r11, r13);
                    goto L_0x04ba;
                L_0x04ab:
                    r3 = 1;
                    r7 = new android.text.SpannableStringBuilder;
                    r9 = new java.lang.Object[r3];
                    r9[r6] = r4;
                    r3 = java.lang.String.format(r5, r9);
                    r7.<init>(r3);
                    r5 = r7;
                L_0x04ba:
                    if (r8 < 0) goto L_0x04d1;
                L_0x04bc:
                    r3 = new org.telegram.ui.Components.TypefaceSpan;
                    r7 = "fonts/rmedium.ttf";
                    r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7);
                    r3.<init>(r7);
                    r4 = r4.length();
                    r4 = r4 + r8;
                    r7 = 33;
                    r5.setSpan(r3, r8, r4, r7);
                L_0x04d1:
                    r3 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;
                    r4 = r1.forwardedNameWidth;
                    r4 = (float) r4;
                    r7 = android.text.TextUtils.TruncateAt.END;
                    r19 = android.text.TextUtils.ellipsize(r5, r3, r4, r7);
                    r3 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x057f }
                    r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x057f }
                    r20 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;	 Catch:{ Exception -> 0x057f }
                    r5 = r1.forwardedNameWidth;	 Catch:{ Exception -> 0x057f }
                    r7 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x057f }
                    r8 = org.telegram.messenger.AndroidUtilities.dp(r7);	 Catch:{ Exception -> 0x057f }
                    r21 = r5 + r8;	 Catch:{ Exception -> 0x057f }
                    r22 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x057f }
                    r23 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x057f }
                    r24 = 0;	 Catch:{ Exception -> 0x057f }
                    r25 = 0;	 Catch:{ Exception -> 0x057f }
                    r18 = r4;	 Catch:{ Exception -> 0x057f }
                    r18.<init>(r19, r20, r21, r22, r23, r24, r25);	 Catch:{ Exception -> 0x057f }
                    r5 = 1;	 Catch:{ Exception -> 0x057f }
                    r3[r5] = r4;	 Catch:{ Exception -> 0x057f }
                    r3 = "ForwardedMessage";	 Catch:{ Exception -> 0x057f }
                    r4 = NUM; // 0x7f0c02c6 float:1.8610632E38 double:1.053097749E-314;	 Catch:{ Exception -> 0x057f }
                    r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x057f }
                    r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3);	 Catch:{ Exception -> 0x057f }
                    r4 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;	 Catch:{ Exception -> 0x057f }
                    r5 = r1.forwardedNameWidth;	 Catch:{ Exception -> 0x057f }
                    r5 = (float) r5;	 Catch:{ Exception -> 0x057f }
                    r7 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x057f }
                    r19 = android.text.TextUtils.ellipsize(r3, r4, r5, r7);	 Catch:{ Exception -> 0x057f }
                    r3 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x057f }
                    r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x057f }
                    r20 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;	 Catch:{ Exception -> 0x057f }
                    r5 = r1.forwardedNameWidth;	 Catch:{ Exception -> 0x057f }
                    r7 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x057f }
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r7);	 Catch:{ Exception -> 0x057f }
                    r21 = r5 + r7;	 Catch:{ Exception -> 0x057f }
                    r22 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x057f }
                    r23 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x057f }
                    r24 = 0;	 Catch:{ Exception -> 0x057f }
                    r25 = 0;	 Catch:{ Exception -> 0x057f }
                    r18 = r4;	 Catch:{ Exception -> 0x057f }
                    r18.<init>(r19, r20, r21, r22, r23, r24, r25);	 Catch:{ Exception -> 0x057f }
                    r3[r6] = r4;	 Catch:{ Exception -> 0x057f }
                    r3 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x057f }
                    r3 = r3[r6];	 Catch:{ Exception -> 0x057f }
                    r3 = r3.getLineWidth(r6);	 Catch:{ Exception -> 0x057f }
                    r3 = (double) r3;	 Catch:{ Exception -> 0x057f }
                    r3 = java.lang.Math.ceil(r3);	 Catch:{ Exception -> 0x057f }
                    r3 = (int) r3;	 Catch:{ Exception -> 0x057f }
                    r4 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x057f }
                    r5 = 1;	 Catch:{ Exception -> 0x057f }
                    r4 = r4[r5];	 Catch:{ Exception -> 0x057f }
                    r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x057f }
                    r4 = (double) r4;	 Catch:{ Exception -> 0x057f }
                    r4 = java.lang.Math.ceil(r4);	 Catch:{ Exception -> 0x057f }
                    r4 = (int) r4;	 Catch:{ Exception -> 0x057f }
                    r3 = java.lang.Math.max(r3, r4);	 Catch:{ Exception -> 0x057f }
                    r1.forwardedNameWidth = r3;	 Catch:{ Exception -> 0x057f }
                    r3 = r1.forwardNameOffsetX;	 Catch:{ Exception -> 0x057f }
                    r4 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x057f }
                    r4 = r4[r6];	 Catch:{ Exception -> 0x057f }
                    r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x057f }
                    r3[r6] = r4;	 Catch:{ Exception -> 0x057f }
                    r3 = r1.forwardNameOffsetX;	 Catch:{ Exception -> 0x057f }
                    r4 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x057f }
                    r5 = 1;	 Catch:{ Exception -> 0x057f }
                    r4 = r4[r5];	 Catch:{ Exception -> 0x057f }
                    r4 = r4.getLineLeft(r6);	 Catch:{ Exception -> 0x057f }
                    r3[r5] = r4;	 Catch:{ Exception -> 0x057f }
                    r3 = r2.type;	 Catch:{ Exception -> 0x057f }
                    if (r3 == r12) goto L_0x0584;	 Catch:{ Exception -> 0x057f }
                L_0x0573:
                    r3 = r1.namesOffset;	 Catch:{ Exception -> 0x057f }
                    r4 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;	 Catch:{ Exception -> 0x057f }
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x057f }
                    r3 = r3 + r4;	 Catch:{ Exception -> 0x057f }
                    r1.namesOffset = r3;	 Catch:{ Exception -> 0x057f }
                    goto L_0x0584;
                L_0x057f:
                    r0 = move-exception;
                    r3 = r0;
                    org.telegram.messenger.FileLog.m3e(r3);
                L_0x0584:
                    r3 = r35.hasValidReplyMessageObject();
                    if (r3 == 0) goto L_0x080f;
                L_0x058a:
                    r3 = r1.currentPosition;
                    if (r3 == 0) goto L_0x0594;
                L_0x058e:
                    r3 = r1.currentPosition;
                    r3 = r3.minY;
                    if (r3 != 0) goto L_0x080f;
                L_0x0594:
                    r3 = r2.type;
                    if (r3 == r10) goto L_0x05b6;
                L_0x0598:
                    r3 = r2.type;
                    if (r3 == r12) goto L_0x05b6;
                L_0x059c:
                    r3 = r1.namesOffset;
                    r4 = NUM; // 0x42280000 float:42.0 double:5.483722033E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    r1.namesOffset = r3;
                    r3 = r2.type;
                    if (r3 == 0) goto L_0x05b6;
                L_0x05ab:
                    r3 = r1.namesOffset;
                    r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 + r4;
                    r1.namesOffset = r3;
                L_0x05b6:
                    r3 = r34.getMaxNameWidth();
                    r4 = r2.type;
                    if (r4 == r10) goto L_0x05c9;
                L_0x05be:
                    r4 = r2.type;
                    if (r4 == r12) goto L_0x05c9;
                L_0x05c2:
                    r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                L_0x05c9:
                    r4 = r2.replyMessageObject;
                    r4 = r4.photoThumbs2;
                    r5 = 80;
                    r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5);
                    if (r4 != 0) goto L_0x05dd;
                L_0x05d5:
                    r4 = r2.replyMessageObject;
                    r4 = r4.photoThumbs;
                    r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5);
                L_0x05dd:
                    if (r4 == 0) goto L_0x062d;
                L_0x05df:
                    r5 = r2.replyMessageObject;
                    r5 = r5.type;
                    if (r5 == r10) goto L_0x062d;
                L_0x05e5:
                    r5 = r2.type;
                    if (r5 != r10) goto L_0x05ef;
                L_0x05e9:
                    r5 = org.telegram.messenger.AndroidUtilities.isTablet();
                    if (r5 == 0) goto L_0x062d;
                L_0x05ef:
                    r5 = r2.replyMessageObject;
                    r5 = r5.isSecretMedia();
                    if (r5 == 0) goto L_0x05f8;
                L_0x05f7:
                    goto L_0x062d;
                L_0x05f8:
                    r5 = r2.replyMessageObject;
                    r5 = r5.isRoundVideo();
                    if (r5 == 0) goto L_0x060c;
                L_0x0600:
                    r5 = r1.replyImageReceiver;
                    r7 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r5.setRoundRadius(r7);
                    goto L_0x0611;
                L_0x060c:
                    r5 = r1.replyImageReceiver;
                    r5.setRoundRadius(r6);
                L_0x0611:
                    r5 = r4.location;
                    r1.currentReplyPhoto = r5;
                    r7 = r1.replyImageReceiver;
                    r8 = r4.location;
                    r9 = "50_50";
                    r10 = 0;
                    r11 = 0;
                    r12 = 1;
                    r7.setImage(r8, r9, r10, r11, r12);
                    r4 = 1;
                    r1.needReplyImage = r4;
                    r4 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r3 = r3 - r4;
                    r5 = 0;
                    goto L_0x0638;
                L_0x062d:
                    r4 = r1.replyImageReceiver;
                    r5 = 0;
                    r7 = r5;
                    r7 = (android.graphics.drawable.Drawable) r7;
                    r4.setImageBitmap(r7);
                    r1.needReplyImage = r6;
                L_0x0638:
                    r4 = r2.customReplyName;
                    if (r4 == 0) goto L_0x063f;
                L_0x063c:
                    r4 = r2.customReplyName;
                    goto L_0x06a0;
                L_0x063f:
                    r4 = r2.replyMessageObject;
                    r4 = r4.isFromUser();
                    if (r4 == 0) goto L_0x0662;
                L_0x0647:
                    r4 = r1.currentAccount;
                    r4 = org.telegram.messenger.MessagesController.getInstance(r4);
                    r7 = r2.replyMessageObject;
                    r7 = r7.messageOwner;
                    r7 = r7.from_id;
                    r7 = java.lang.Integer.valueOf(r7);
                    r4 = r4.getUser(r7);
                    if (r4 == 0) goto L_0x069f;
                L_0x065d:
                    r4 = org.telegram.messenger.UserObject.getUserName(r4);
                    goto L_0x06a0;
                L_0x0662:
                    r4 = r2.replyMessageObject;
                    r4 = r4.messageOwner;
                    r4 = r4.from_id;
                    if (r4 >= 0) goto L_0x0684;
                L_0x066a:
                    r4 = r1.currentAccount;
                    r4 = org.telegram.messenger.MessagesController.getInstance(r4);
                    r7 = r2.replyMessageObject;
                    r7 = r7.messageOwner;
                    r7 = r7.from_id;
                    r7 = -r7;
                    r7 = java.lang.Integer.valueOf(r7);
                    r4 = r4.getChat(r7);
                    if (r4 == 0) goto L_0x069f;
                L_0x0681:
                    r4 = r4.title;
                    goto L_0x06a0;
                L_0x0684:
                    r4 = r1.currentAccount;
                    r4 = org.telegram.messenger.MessagesController.getInstance(r4);
                    r7 = r2.replyMessageObject;
                    r7 = r7.messageOwner;
                    r7 = r7.to_id;
                    r7 = r7.channel_id;
                    r7 = java.lang.Integer.valueOf(r7);
                    r4 = r4.getChat(r7);
                    if (r4 == 0) goto L_0x069f;
                L_0x069c:
                    r4 = r4.title;
                    goto L_0x06a0;
                L_0x069f:
                    r4 = r5;
                L_0x06a0:
                    if (r4 != 0) goto L_0x06ab;
                L_0x06a2:
                    r4 = "Loading";
                    r7 = NUM; // 0x7f0c0382 float:1.8611013E38 double:1.053097842E-314;
                    r4 = org.telegram.messenger.LocaleController.getString(r4, r7);
                L_0x06ab:
                    r7 = 10;
                    r8 = 32;
                    r4 = r4.replace(r7, r8);
                    r7 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
                    r8 = (float) r3;
                    r9 = android.text.TextUtils.TruncateAt.END;
                    r18 = android.text.TextUtils.ellipsize(r4, r7, r8, r9);
                    r4 = r2.replyMessageObject;
                    r4 = r4.messageOwner;
                    r4 = r4.media;
                    r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
                    r7 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
                    if (r4 == 0) goto L_0x06ea;
                L_0x06c8:
                    r2 = r2.replyMessageObject;
                    r2 = r2.messageOwner;
                    r2 = r2.media;
                    r2 = r2.game;
                    r2 = r2.title;
                    r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
                    r4 = r4.getFontMetricsInt();
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r5, r6);
                    r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
                    r5 = android.text.TextUtils.TruncateAt.END;
                    r5 = android.text.TextUtils.ellipsize(r2, r4, r8, r5);
                L_0x06e8:
                    r8 = r5;
                    goto L_0x0756;
                L_0x06ea:
                    r4 = r2.replyMessageObject;
                    r4 = r4.messageOwner;
                    r4 = r4.media;
                    r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
                    if (r4 == 0) goto L_0x0713;
                L_0x06f4:
                    r2 = r2.replyMessageObject;
                    r2 = r2.messageOwner;
                    r2 = r2.media;
                    r2 = r2.title;
                    r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
                    r4 = r4.getFontMetricsInt();
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r5, r6);
                    r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
                    r5 = android.text.TextUtils.TruncateAt.END;
                    r5 = android.text.TextUtils.ellipsize(r2, r4, r8, r5);
                    goto L_0x06e8;
                L_0x0713:
                    r4 = r2.replyMessageObject;
                    r4 = r4.messageText;
                    if (r4 == 0) goto L_0x06e8;
                L_0x0719:
                    r4 = r2.replyMessageObject;
                    r4 = r4.messageText;
                    r4 = r4.length();
                    if (r4 <= 0) goto L_0x06e8;
                L_0x0723:
                    r2 = r2.replyMessageObject;
                    r2 = r2.messageText;
                    r2 = r2.toString();
                    r4 = r2.length();
                    r5 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
                    if (r4 <= r5) goto L_0x0737;
                L_0x0733:
                    r2 = r2.substring(r6, r5);
                L_0x0737:
                    r4 = 10;
                    r5 = 32;
                    r2 = r2.replace(r4, r5);
                    r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
                    r4 = r4.getFontMetricsInt();
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r2 = org.telegram.messenger.Emoji.replaceEmoji(r2, r4, r5, r6);
                    r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
                    r5 = android.text.TextUtils.TruncateAt.END;
                    r5 = android.text.TextUtils.ellipsize(r2, r4, r8, r5);
                    goto L_0x06e8;
                L_0x0756:
                    r2 = r1.needReplyImage;	 Catch:{ Exception -> 0x07af }
                    if (r2 == 0) goto L_0x075e;	 Catch:{ Exception -> 0x07af }
                L_0x075a:
                    r2 = 44;	 Catch:{ Exception -> 0x07af }
                L_0x075c:
                    r4 = 4;	 Catch:{ Exception -> 0x07af }
                    goto L_0x0760;	 Catch:{ Exception -> 0x07af }
                L_0x075e:
                    r2 = r6;	 Catch:{ Exception -> 0x07af }
                    goto L_0x075c;	 Catch:{ Exception -> 0x07af }
                L_0x0760:
                    r15 = r4 + r2;	 Catch:{ Exception -> 0x07af }
                    r2 = (float) r15;	 Catch:{ Exception -> 0x07af }
                    r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x07af }
                    r1.replyNameWidth = r2;	 Catch:{ Exception -> 0x07af }
                    if (r18 == 0) goto L_0x07b4;	 Catch:{ Exception -> 0x07af }
                L_0x076b:
                    r2 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x07af }
                    r19 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x07af }
                    r4 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;	 Catch:{ Exception -> 0x07af }
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x07af }
                    r20 = r3 + r4;	 Catch:{ Exception -> 0x07af }
                    r21 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x07af }
                    r22 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x07af }
                    r23 = 0;	 Catch:{ Exception -> 0x07af }
                    r24 = 0;	 Catch:{ Exception -> 0x07af }
                    r17 = r2;	 Catch:{ Exception -> 0x07af }
                    r17.<init>(r18, r19, r20, r21, r22, r23, r24);	 Catch:{ Exception -> 0x07af }
                    r1.replyNameLayout = r2;	 Catch:{ Exception -> 0x07af }
                    r2 = r1.replyNameLayout;	 Catch:{ Exception -> 0x07af }
                    r2 = r2.getLineCount();	 Catch:{ Exception -> 0x07af }
                    if (r2 <= 0) goto L_0x07b4;	 Catch:{ Exception -> 0x07af }
                L_0x078e:
                    r2 = r1.replyNameWidth;	 Catch:{ Exception -> 0x07af }
                    r4 = r1.replyNameLayout;	 Catch:{ Exception -> 0x07af }
                    r4 = r4.getLineWidth(r6);	 Catch:{ Exception -> 0x07af }
                    r4 = (double) r4;	 Catch:{ Exception -> 0x07af }
                    r4 = java.lang.Math.ceil(r4);	 Catch:{ Exception -> 0x07af }
                    r4 = (int) r4;	 Catch:{ Exception -> 0x07af }
                    r5 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;	 Catch:{ Exception -> 0x07af }
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x07af }
                    r4 = r4 + r7;	 Catch:{ Exception -> 0x07af }
                    r2 = r2 + r4;	 Catch:{ Exception -> 0x07af }
                    r1.replyNameWidth = r2;	 Catch:{ Exception -> 0x07af }
                    r2 = r1.replyNameLayout;	 Catch:{ Exception -> 0x07af }
                    r2 = r2.getLineLeft(r6);	 Catch:{ Exception -> 0x07af }
                    r1.replyNameOffset = r2;	 Catch:{ Exception -> 0x07af }
                    goto L_0x07b4;
                L_0x07af:
                    r0 = move-exception;
                    r2 = r0;
                    org.telegram.messenger.FileLog.m3e(r2);
                L_0x07b4:
                    r2 = r1.needReplyImage;	 Catch:{ Exception -> 0x080a }
                    if (r2 == 0) goto L_0x07bc;	 Catch:{ Exception -> 0x080a }
                L_0x07b8:
                    r2 = 44;	 Catch:{ Exception -> 0x080a }
                L_0x07ba:
                    r4 = 4;	 Catch:{ Exception -> 0x080a }
                    goto L_0x07be;	 Catch:{ Exception -> 0x080a }
                L_0x07bc:
                    r2 = r6;	 Catch:{ Exception -> 0x080a }
                    goto L_0x07ba;	 Catch:{ Exception -> 0x080a }
                L_0x07be:
                    r15 = r4 + r2;	 Catch:{ Exception -> 0x080a }
                    r2 = (float) r15;	 Catch:{ Exception -> 0x080a }
                    r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x080a }
                    r1.replyTextWidth = r2;	 Catch:{ Exception -> 0x080a }
                    if (r8 == 0) goto L_0x080f;	 Catch:{ Exception -> 0x080a }
                L_0x07c9:
                    r2 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x080a }
                    r9 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x080a }
                    r4 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;	 Catch:{ Exception -> 0x080a }
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x080a }
                    r10 = r3 + r4;	 Catch:{ Exception -> 0x080a }
                    r11 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x080a }
                    r12 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x080a }
                    r13 = 0;	 Catch:{ Exception -> 0x080a }
                    r14 = 0;	 Catch:{ Exception -> 0x080a }
                    r7 = r2;	 Catch:{ Exception -> 0x080a }
                    r7.<init>(r8, r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x080a }
                    r1.replyTextLayout = r2;	 Catch:{ Exception -> 0x080a }
                    r2 = r1.replyTextLayout;	 Catch:{ Exception -> 0x080a }
                    r2 = r2.getLineCount();	 Catch:{ Exception -> 0x080a }
                    if (r2 <= 0) goto L_0x080f;	 Catch:{ Exception -> 0x080a }
                L_0x07e9:
                    r2 = r1.replyTextWidth;	 Catch:{ Exception -> 0x080a }
                    r3 = r1.replyTextLayout;	 Catch:{ Exception -> 0x080a }
                    r3 = r3.getLineWidth(r6);	 Catch:{ Exception -> 0x080a }
                    r3 = (double) r3;	 Catch:{ Exception -> 0x080a }
                    r3 = java.lang.Math.ceil(r3);	 Catch:{ Exception -> 0x080a }
                    r3 = (int) r3;	 Catch:{ Exception -> 0x080a }
                    r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;	 Catch:{ Exception -> 0x080a }
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x080a }
                    r3 = r3 + r4;	 Catch:{ Exception -> 0x080a }
                    r2 = r2 + r3;	 Catch:{ Exception -> 0x080a }
                    r1.replyTextWidth = r2;	 Catch:{ Exception -> 0x080a }
                    r2 = r1.replyTextLayout;	 Catch:{ Exception -> 0x080a }
                    r2 = r2.getLineLeft(r6);	 Catch:{ Exception -> 0x080a }
                    r1.replyTextOffset = r2;	 Catch:{ Exception -> 0x080a }
                    goto L_0x080f;
                L_0x080a:
                    r0 = move-exception;
                    r2 = r0;
                    org.telegram.messenger.FileLog.m3e(r2);
                L_0x080f:
                    r34.requestLayout();
                    return;
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
                    Canvas canvas2 = canvas;
                    if (this.currentMessageObject != null) {
                        if (r0.wasLayout) {
                            Drawable drawable;
                            int i;
                            Drawable drawable2;
                            if (r0.currentMessageObject.isOutOwner()) {
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
                            int i2 = 3;
                            if (r0.documentAttach != null) {
                                if (r0.documentAttachType == 3) {
                                    if (r0.currentMessageObject.isOutOwner()) {
                                        r0.seekBarWaveform.setColors(Theme.getColor(Theme.key_chat_outVoiceSeekbar), Theme.getColor(Theme.key_chat_outVoiceSeekbarFill), Theme.getColor(Theme.key_chat_outVoiceSeekbarSelected));
                                        r0.seekBar.setColors(Theme.getColor(Theme.key_chat_outAudioSeekbar), Theme.getColor(Theme.key_chat_outAudioCacheSeekbar), Theme.getColor(Theme.key_chat_outAudioSeekbarFill), Theme.getColor(Theme.key_chat_outAudioSeekbarFill), Theme.getColor(Theme.key_chat_outAudioSeekbarSelected));
                                    } else {
                                        r0.seekBarWaveform.setColors(Theme.getColor(Theme.key_chat_inVoiceSeekbar), Theme.getColor(Theme.key_chat_inVoiceSeekbarFill), Theme.getColor(Theme.key_chat_inVoiceSeekbarSelected));
                                        r0.seekBar.setColors(Theme.getColor(Theme.key_chat_inAudioSeekbar), Theme.getColor(Theme.key_chat_inAudioCacheSeekbar), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarSelected));
                                    }
                                } else if (r0.documentAttachType == 5) {
                                    r0.documentAttachType = 5;
                                    if (r0.currentMessageObject.isOutOwner()) {
                                        r0.seekBar.setColors(Theme.getColor(Theme.key_chat_outAudioSeekbar), Theme.getColor(Theme.key_chat_outAudioCacheSeekbar), Theme.getColor(Theme.key_chat_outAudioSeekbarFill), Theme.getColor(Theme.key_chat_outAudioSeekbarFill), Theme.getColor(Theme.key_chat_outAudioSeekbarSelected));
                                    } else {
                                        r0.seekBar.setColors(Theme.getColor(Theme.key_chat_inAudioSeekbar), Theme.getColor(Theme.key_chat_inAudioCacheSeekbar), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarSelected));
                                    }
                                }
                            }
                            if (r0.currentMessageObject.type == 5) {
                                Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
                            } else if (r0.mediaBackground) {
                                if (r0.currentMessageObject.type != 13) {
                                    if (r0.currentMessageObject.type != 5) {
                                        Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
                                    }
                                }
                                Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_serviceText));
                            } else if (r0.currentMessageObject.isOutOwner()) {
                                Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
                            } else {
                                Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
                            }
                            int i3;
                            int dp;
                            if (r0.currentMessageObject.isOutOwner()) {
                                Drawable drawable3;
                                int dp2;
                                int dp3;
                                if (r0.mediaBackground || r0.drawPinnedBottom) {
                                    r0.currentBackgroundDrawable = Theme.chat_msgOutMediaDrawable;
                                    drawable = Theme.chat_msgOutMediaSelectedDrawable;
                                    drawable3 = Theme.chat_msgOutMediaShadowDrawable;
                                } else {
                                    r0.currentBackgroundDrawable = Theme.chat_msgOutDrawable;
                                    drawable = Theme.chat_msgOutSelectedDrawable;
                                    drawable3 = Theme.chat_msgOutShadowDrawable;
                                }
                                r0.backgroundDrawableLeft = (r0.layoutWidth - r0.backgroundWidth) - (!r0.mediaBackground ? 0 : AndroidUtilities.dp(9.0f));
                                r0.backgroundDrawableRight = r0.backgroundWidth - (r0.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
                                if (!(r0.currentMessagesGroup == null || r0.currentPosition.edge)) {
                                    r0.backgroundDrawableRight += AndroidUtilities.dp(10.0f);
                                }
                                i3 = r0.backgroundDrawableLeft;
                                if (!r0.mediaBackground && r0.drawPinnedBottom) {
                                    r0.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                                }
                                if (r0.currentPosition != null) {
                                    if ((r0.currentPosition.flags & 2) == 0) {
                                        r0.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                                    }
                                    if ((r0.currentPosition.flags & 1) == 0) {
                                        i3 -= AndroidUtilities.dp(8.0f);
                                        r0.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                                    }
                                    if ((r0.currentPosition.flags & 4) == 0) {
                                        dp = 0 - AndroidUtilities.dp(9.0f);
                                        dp2 = AndroidUtilities.dp(9.0f) + 0;
                                    } else {
                                        dp = 0;
                                        dp2 = dp;
                                    }
                                    if ((r0.currentPosition.flags & 8) == 0) {
                                        dp2 += AndroidUtilities.dp(9.0f);
                                    }
                                } else {
                                    dp = 0;
                                    dp2 = dp;
                                }
                                if (r0.drawPinnedBottom && r0.drawPinnedTop) {
                                    i = 0;
                                } else if (r0.drawPinnedBottom) {
                                    i = AndroidUtilities.dp(1.0f);
                                } else {
                                    i = AndroidUtilities.dp(2.0f);
                                }
                                if (!r0.drawPinnedTop) {
                                    if (!r0.drawPinnedTop || !r0.drawPinnedBottom) {
                                        dp3 = AndroidUtilities.dp(1.0f);
                                        dp += dp3;
                                        BaseCell.setDrawableBounds(r0.currentBackgroundDrawable, i3, dp, r0.backgroundDrawableRight, (r0.layoutHeight - i) + dp2);
                                        BaseCell.setDrawableBounds(drawable, i3, dp, r0.backgroundDrawableRight, (r0.layoutHeight - i) + dp2);
                                        BaseCell.setDrawableBounds(drawable3, i3, dp, r0.backgroundDrawableRight, (r0.layoutHeight - i) + dp2);
                                        drawable2 = drawable3;
                                    }
                                }
                                dp3 = 0;
                                dp += dp3;
                                BaseCell.setDrawableBounds(r0.currentBackgroundDrawable, i3, dp, r0.backgroundDrawableRight, (r0.layoutHeight - i) + dp2);
                                BaseCell.setDrawableBounds(drawable, i3, dp, r0.backgroundDrawableRight, (r0.layoutHeight - i) + dp2);
                                BaseCell.setDrawableBounds(drawable3, i3, dp, r0.backgroundDrawableRight, (r0.layoutHeight - i) + dp2);
                                drawable2 = drawable3;
                            } else {
                                if (r0.mediaBackground || r0.drawPinnedBottom) {
                                    r0.currentBackgroundDrawable = Theme.chat_msgInMediaDrawable;
                                    drawable = Theme.chat_msgInMediaSelectedDrawable;
                                    drawable2 = Theme.chat_msgInMediaShadowDrawable;
                                } else {
                                    r0.currentBackgroundDrawable = Theme.chat_msgInDrawable;
                                    drawable = Theme.chat_msgInSelectedDrawable;
                                    drawable2 = Theme.chat_msgInShadowDrawable;
                                }
                                int i4 = (r0.isChat && r0.isAvatarVisible) ? 48 : 0;
                                if (r0.mediaBackground) {
                                    i2 = 9;
                                }
                                r0.backgroundDrawableLeft = AndroidUtilities.dp((float) (i4 + i2));
                                r0.backgroundDrawableRight = r0.backgroundWidth - (r0.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
                                if (r0.currentMessagesGroup != null) {
                                    if (!r0.currentPosition.edge) {
                                        r0.backgroundDrawableLeft -= AndroidUtilities.dp(10.0f);
                                        r0.backgroundDrawableRight += AndroidUtilities.dp(10.0f);
                                    }
                                    if (r0.currentPosition.leftSpanOffset != 0) {
                                        r0.backgroundDrawableLeft += (int) Math.ceil((double) ((((float) r0.currentPosition.leftSpanOffset) / 1000.0f) * ((float) getGroupPhotosWidth())));
                                    }
                                }
                                if (!r0.mediaBackground && r0.drawPinnedBottom) {
                                    r0.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                                    r0.backgroundDrawableLeft += AndroidUtilities.dp(6.0f);
                                }
                                if (r0.currentPosition != null) {
                                    if ((r0.currentPosition.flags & 2) == 0) {
                                        r0.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                                    }
                                    if ((r0.currentPosition.flags & 1) == 0) {
                                        r0.backgroundDrawableLeft -= AndroidUtilities.dp(8.0f);
                                        r0.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                                    }
                                    if ((r0.currentPosition.flags & 4) == 0) {
                                        i2 = 0 - AndroidUtilities.dp(9.0f);
                                        i3 = AndroidUtilities.dp(9.0f) + 0;
                                    } else {
                                        i2 = 0;
                                        i3 = i2;
                                    }
                                    if ((r0.currentPosition.flags & 8) == 0) {
                                        i3 += AndroidUtilities.dp(10.0f);
                                    }
                                } else {
                                    i2 = 0;
                                    i3 = i2;
                                }
                                if (r0.drawPinnedBottom && r0.drawPinnedTop) {
                                    i = 0;
                                } else if (r0.drawPinnedBottom) {
                                    i = AndroidUtilities.dp(1.0f);
                                } else {
                                    i = AndroidUtilities.dp(2.0f);
                                }
                                if (!r0.drawPinnedTop) {
                                    if (!r0.drawPinnedTop || !r0.drawPinnedBottom) {
                                        dp = AndroidUtilities.dp(1.0f);
                                        i2 += dp;
                                        BaseCell.setDrawableBounds(r0.currentBackgroundDrawable, r0.backgroundDrawableLeft, i2, r0.backgroundDrawableRight, (r0.layoutHeight - i) + i3);
                                        BaseCell.setDrawableBounds(drawable, r0.backgroundDrawableLeft, i2, r0.backgroundDrawableRight, (r0.layoutHeight - i) + i3);
                                        BaseCell.setDrawableBounds(drawable2, r0.backgroundDrawableLeft, i2, r0.backgroundDrawableRight, (r0.layoutHeight - i) + i3);
                                    }
                                }
                                dp = 0;
                                i2 += dp;
                                BaseCell.setDrawableBounds(r0.currentBackgroundDrawable, r0.backgroundDrawableLeft, i2, r0.backgroundDrawableRight, (r0.layoutHeight - i) + i3);
                                BaseCell.setDrawableBounds(drawable, r0.backgroundDrawableLeft, i2, r0.backgroundDrawableRight, (r0.layoutHeight - i) + i3);
                                BaseCell.setDrawableBounds(drawable2, r0.backgroundDrawableLeft, i2, r0.backgroundDrawableRight, (r0.layoutHeight - i) + i3);
                            }
                            if (r0.drawBackground && r0.currentBackgroundDrawable != null) {
                                if (r0.isHighlightedAnimated) {
                                    r0.currentBackgroundDrawable.draw(canvas2);
                                    float f = r0.highlightProgress >= 300 ? 1.0f : ((float) r0.highlightProgress) / 300.0f;
                                    if (r0.currentPosition == null) {
                                        drawable.setAlpha((int) (f * 255.0f));
                                        drawable.draw(canvas2);
                                    }
                                    long currentTimeMillis = System.currentTimeMillis();
                                    long abs = Math.abs(currentTimeMillis - r0.lastHighlightProgressTime);
                                    if (abs > 17) {
                                        abs = 17;
                                    }
                                    r0.highlightProgress = (int) (((long) r0.highlightProgress) - abs);
                                    r0.lastHighlightProgressTime = currentTimeMillis;
                                    if (r0.highlightProgress <= 0) {
                                        r0.highlightProgress = 0;
                                        r0.isHighlightedAnimated = false;
                                    }
                                    invalidate();
                                } else if (!isDrawSelectedBackground() || (r0.currentPosition != null && getBackground() == null)) {
                                    r0.currentBackgroundDrawable.draw(canvas2);
                                } else {
                                    drawable.setAlpha(255);
                                    drawable.draw(canvas2);
                                }
                                if (r0.currentPosition == null || r0.currentPosition.flags != 0) {
                                    drawable2.draw(canvas2);
                                }
                            }
                            drawContent(canvas);
                            if (r0.drawShareButton) {
                                Theme.chat_shareDrawable.setColorFilter(r0.sharePressed ? Theme.colorPressedFilter : Theme.colorFilter);
                                if (r0.currentMessageObject.isOutOwner()) {
                                    r0.shareStartX = (r0.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0f)) - Theme.chat_shareDrawable.getIntrinsicWidth();
                                } else {
                                    r0.shareStartX = r0.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0f);
                                }
                                drawable = Theme.chat_shareDrawable;
                                i2 = r0.shareStartX;
                                i = r0.layoutHeight - AndroidUtilities.dp(41.0f);
                                r0.shareStartY = i;
                                BaseCell.setDrawableBounds(drawable, i2, i);
                                Theme.chat_shareDrawable.draw(canvas2);
                                if (r0.drwaShareGoIcon) {
                                    BaseCell.setDrawableBounds(Theme.chat_goIconDrawable, r0.shareStartX + AndroidUtilities.dp(12.0f), r0.shareStartY + AndroidUtilities.dp(9.0f));
                                    Theme.chat_goIconDrawable.draw(canvas2);
                                } else {
                                    BaseCell.setDrawableBounds(Theme.chat_shareIconDrawable, r0.shareStartX + AndroidUtilities.dp(9.0f), r0.shareStartY + AndroidUtilities.dp(9.0f));
                                    Theme.chat_shareIconDrawable.draw(canvas2);
                                }
                            }
                            if (r0.currentPosition == null) {
                                drawNamesLayout(canvas);
                            }
                            if ((r0.drawTime || !r0.mediaBackground) && !r0.forceNotDrawTime) {
                                drawTimeLayout(canvas);
                            }
                            if (!(r0.controlsAlpha == 1.0f && r0.timeAlpha == 1.0f)) {
                                long currentTimeMillis2 = System.currentTimeMillis();
                                long abs2 = Math.abs(r0.lastControlsAlphaChangeTime - currentTimeMillis2);
                                if (abs2 > 17) {
                                    abs2 = 17;
                                }
                                r0.totalChangeTime += abs2;
                                if (r0.totalChangeTime > 100) {
                                    r0.totalChangeTime = 100;
                                }
                                r0.lastControlsAlphaChangeTime = currentTimeMillis2;
                                if (r0.controlsAlpha != 1.0f) {
                                    r0.controlsAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) r0.totalChangeTime) / 100.0f);
                                }
                                if (r0.timeAlpha != 1.0f) {
                                    r0.timeAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) r0.totalChangeTime) / 100.0f);
                                }
                                invalidate();
                                if (r0.forceNotDrawTime && r0.currentPosition != null && r0.currentPosition.last && getParent() != null) {
                                    ((View) getParent()).invalidate();
                                }
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
                    if (this.drawNameLayout && this.nameLayout != null) {
                        return true;
                    }
                    if (!(!this.drawForwardedName || this.forwardedNameLayout[0] == null || this.forwardedNameLayout[1] == null)) {
                        if (this.currentPosition == null) {
                            return true;
                        }
                        if (this.currentPosition.minY == (byte) 0 && this.currentPosition.minX == (byte) 0) {
                            return true;
                        }
                    }
                    return this.replyNameLayout != null;
                }

                public void drawNamesLayout(Canvas canvas) {
                    Canvas canvas2 = canvas;
                    float f = 11.0f;
                    float f2 = 12.0f;
                    int i = 0;
                    if (this.drawNameLayout && r0.nameLayout != null) {
                        canvas.save();
                        if (r0.currentMessageObject.type != 13) {
                            if (r0.currentMessageObject.type != 5) {
                                if (!r0.mediaBackground) {
                                    if (!r0.currentMessageObject.isOutOwner()) {
                                        int i2 = r0.backgroundDrawableLeft;
                                        float f3 = (r0.mediaBackground || !r0.drawPinnedBottom) ? 17.0f : 11.0f;
                                        r0.nameX = ((float) (i2 + AndroidUtilities.dp(f3))) - r0.nameOffsetX;
                                        if (r0.currentUser != null) {
                                            Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(r0.currentUser.id));
                                        } else if (r0.currentChat != null) {
                                            Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(0));
                                        } else if (ChatObject.isChannel(r0.currentChat) || r0.currentChat.megagroup) {
                                            Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(r0.currentChat.id));
                                        } else {
                                            Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(5));
                                        }
                                        r0.nameY = (float) AndroidUtilities.dp(r0.drawPinnedTop ? 9.0f : 10.0f);
                                        canvas2.translate(r0.nameX, r0.nameY);
                                        r0.nameLayout.draw(canvas2);
                                        canvas.restore();
                                        if (r0.adminLayout != null) {
                                            Theme.chat_adminPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_adminSelectedText : Theme.key_chat_adminText));
                                            canvas.save();
                                            canvas2.translate(((float) ((r0.backgroundDrawableLeft + r0.backgroundDrawableRight) - AndroidUtilities.dp(11.0f))) - r0.adminLayout.getLineWidth(0), r0.nameY + ((float) AndroidUtilities.dp(0.5f)));
                                            r0.adminLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                    }
                                }
                                r0.nameX = ((float) (r0.backgroundDrawableLeft + AndroidUtilities.dp(11.0f))) - r0.nameOffsetX;
                                if (r0.currentUser != null) {
                                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(r0.currentUser.id));
                                } else if (r0.currentChat != null) {
                                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(0));
                                } else {
                                    if (ChatObject.isChannel(r0.currentChat)) {
                                    }
                                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(r0.currentChat.id));
                                }
                                if (r0.drawPinnedTop) {
                                }
                                r0.nameY = (float) AndroidUtilities.dp(r0.drawPinnedTop ? 9.0f : 10.0f);
                                canvas2.translate(r0.nameX, r0.nameY);
                                r0.nameLayout.draw(canvas2);
                                canvas.restore();
                                if (r0.adminLayout != null) {
                                    if (isDrawSelectedBackground()) {
                                    }
                                    Theme.chat_adminPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_adminSelectedText : Theme.key_chat_adminText));
                                    canvas.save();
                                    canvas2.translate(((float) ((r0.backgroundDrawableLeft + r0.backgroundDrawableRight) - AndroidUtilities.dp(11.0f))) - r0.adminLayout.getLineWidth(0), r0.nameY + ((float) AndroidUtilities.dp(0.5f)));
                                    r0.adminLayout.draw(canvas2);
                                    canvas.restore();
                                }
                            }
                        }
                        Theme.chat_namePaint.setColor(Theme.getColor(Theme.key_chat_stickerNameText));
                        if (r0.currentMessageObject.isOutOwner()) {
                            r0.nameX = (float) AndroidUtilities.dp(28.0f);
                        } else {
                            r0.nameX = (float) ((r0.backgroundDrawableLeft + r0.backgroundDrawableRight) + AndroidUtilities.dp(22.0f));
                        }
                        r0.nameY = (float) (r0.layoutHeight - AndroidUtilities.dp(38.0f));
                        Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                        Theme.chat_systemDrawable.setBounds(((int) r0.nameX) - AndroidUtilities.dp(12.0f), ((int) r0.nameY) - AndroidUtilities.dp(5.0f), (((int) r0.nameX) + AndroidUtilities.dp(12.0f)) + r0.nameWidth, ((int) r0.nameY) + AndroidUtilities.dp(22.0f));
                        Theme.chat_systemDrawable.draw(canvas2);
                        canvas2.translate(r0.nameX, r0.nameY);
                        r0.nameLayout.draw(canvas2);
                        canvas.restore();
                        if (r0.adminLayout != null) {
                            if (isDrawSelectedBackground()) {
                            }
                            Theme.chat_adminPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_adminSelectedText : Theme.key_chat_adminText));
                            canvas.save();
                            canvas2.translate(((float) ((r0.backgroundDrawableLeft + r0.backgroundDrawableRight) - AndroidUtilities.dp(11.0f))) - r0.adminLayout.getLineWidth(0), r0.nameY + ((float) AndroidUtilities.dp(0.5f)));
                            r0.adminLayout.draw(canvas2);
                            canvas.restore();
                        }
                    }
                    if (r0.drawForwardedName && r0.forwardedNameLayout[0] != null && r0.forwardedNameLayout[1] != null && (r0.currentPosition == null || (r0.currentPosition.minY == (byte) 0 && r0.currentPosition.minX == (byte) 0))) {
                        if (r0.currentMessageObject.type == 5) {
                            Theme.chat_forwardNamePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyNameText));
                            if (r0.currentMessageObject.isOutOwner()) {
                                r0.forwardNameX = AndroidUtilities.dp(23.0f);
                            } else {
                                r0.forwardNameX = (r0.backgroundDrawableLeft + r0.backgroundDrawableRight) + AndroidUtilities.dp(17.0f);
                            }
                            r0.forwardNameY = AndroidUtilities.dp(12.0f);
                            i2 = r0.forwardedNameWidth + AndroidUtilities.dp(14.0f);
                            Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                            Theme.chat_systemDrawable.setBounds(r0.forwardNameX - AndroidUtilities.dp(7.0f), r0.forwardNameY - AndroidUtilities.dp(6.0f), (r0.forwardNameX - AndroidUtilities.dp(7.0f)) + i2, r0.forwardNameY + AndroidUtilities.dp(38.0f));
                            Theme.chat_systemDrawable.draw(canvas2);
                        } else {
                            r0.forwardNameY = AndroidUtilities.dp((float) ((r0.drawNameLayout ? 19 : 0) + 10));
                            if (r0.currentMessageObject.isOutOwner()) {
                                Theme.chat_forwardNamePaint.setColor(Theme.getColor(Theme.key_chat_outForwardedNameText));
                                r0.forwardNameX = r0.backgroundDrawableLeft + AndroidUtilities.dp(11.0f);
                            } else {
                                Theme.chat_forwardNamePaint.setColor(Theme.getColor(Theme.key_chat_inForwardedNameText));
                                if (r0.mediaBackground) {
                                    r0.forwardNameX = r0.backgroundDrawableLeft + AndroidUtilities.dp(11.0f);
                                } else {
                                    i2 = r0.backgroundDrawableLeft;
                                    if (r0.mediaBackground || !r0.drawPinnedBottom) {
                                        f = 17.0f;
                                    }
                                    r0.forwardNameX = i2 + AndroidUtilities.dp(f);
                                }
                            }
                        }
                        for (i2 = 0; i2 < 2; i2++) {
                            canvas.save();
                            canvas2.translate(((float) r0.forwardNameX) - r0.forwardNameOffsetX[i2], (float) (r0.forwardNameY + (AndroidUtilities.dp(16.0f) * i2)));
                            r0.forwardedNameLayout[i2].draw(canvas2);
                            canvas.restore();
                        }
                    }
                    if (r0.replyNameLayout != null) {
                        float f4;
                        if (r0.currentMessageObject.type != 13) {
                            if (r0.currentMessageObject.type != 5) {
                                if (r0.currentMessageObject.isOutOwner()) {
                                    Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_outReplyLine));
                                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_outReplyNameText));
                                    if (!r0.currentMessageObject.hasValidReplyMessageObject() || r0.currentMessageObject.replyMessageObject.type != 0 || (r0.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) || (r0.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                                        Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outReplyMediaMessageSelectedText : Theme.key_chat_outReplyMediaMessageText));
                                    } else {
                                        Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_outReplyMessageText));
                                    }
                                    r0.replyStartX = r0.backgroundDrawableLeft + AndroidUtilities.dp(12.0f);
                                } else {
                                    Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_inReplyLine));
                                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_inReplyNameText));
                                    if (!r0.currentMessageObject.hasValidReplyMessageObject() || r0.currentMessageObject.replyMessageObject.type != 0 || (r0.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) || (r0.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                                        Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inReplyMediaMessageSelectedText : Theme.key_chat_inReplyMediaMessageText));
                                    } else {
                                        Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_inReplyMessageText));
                                    }
                                    if (r0.mediaBackground) {
                                        r0.replyStartX = r0.backgroundDrawableLeft + AndroidUtilities.dp(12.0f);
                                    } else {
                                        i2 = r0.backgroundDrawableLeft;
                                        if (r0.mediaBackground || !r0.drawPinnedBottom) {
                                            f2 = 18.0f;
                                        }
                                        r0.replyStartX = i2 + AndroidUtilities.dp(f2);
                                    }
                                }
                                int i3 = (!r0.drawForwardedName || r0.forwardedNameLayout[0] == null) ? 0 : 36;
                                i2 = 12 + i3;
                                i3 = (!r0.drawNameLayout || r0.nameLayout == null) ? 0 : 20;
                                r0.replyStartY = AndroidUtilities.dp((float) (i2 + i3));
                                if (r0.currentPosition != null || (r0.currentPosition.minY == (byte) 0 && r0.currentPosition.minX == (byte) 0)) {
                                    canvas2.drawRect((float) r0.replyStartX, (float) r0.replyStartY, (float) (r0.replyStartX + AndroidUtilities.dp(2.0f)), (float) (r0.replyStartY + AndroidUtilities.dp(35.0f)), Theme.chat_replyLinePaint);
                                    if (r0.needReplyImage) {
                                        r0.replyImageReceiver.setImageCoords(r0.replyStartX + AndroidUtilities.dp(10.0f), r0.replyStartY, AndroidUtilities.dp(35.0f), AndroidUtilities.dp(35.0f));
                                        r0.replyImageReceiver.draw(canvas2);
                                    }
                                    if (r0.replyNameLayout != null) {
                                        canvas.save();
                                        canvas2.translate((((float) r0.replyStartX) - r0.replyNameOffset) + ((float) AndroidUtilities.dp((float) ((r0.needReplyImage ? 44 : 0) + 10))), (float) r0.replyStartY);
                                        r0.replyNameLayout.draw(canvas2);
                                        canvas.restore();
                                    }
                                    if (r0.replyTextLayout != null) {
                                        canvas.save();
                                        f4 = ((float) r0.replyStartX) - r0.replyTextOffset;
                                        if (r0.needReplyImage) {
                                            i = 44;
                                        }
                                        canvas2.translate(f4 + ((float) AndroidUtilities.dp((float) (10 + i))), (float) (r0.replyStartY + AndroidUtilities.dp(19.0f)));
                                        r0.replyTextLayout.draw(canvas2);
                                        canvas.restore();
                                    }
                                }
                                return;
                            }
                        }
                        Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyLine));
                        Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyNameText));
                        Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyMessageText));
                        if (r0.currentMessageObject.isOutOwner()) {
                            r0.replyStartX = AndroidUtilities.dp(23.0f);
                        } else {
                            r0.replyStartX = (r0.backgroundDrawableLeft + r0.backgroundDrawableRight) + AndroidUtilities.dp(17.0f);
                        }
                        r0.replyStartY = AndroidUtilities.dp(12.0f);
                        i2 = Math.max(r0.replyNameWidth, r0.replyTextWidth) + AndroidUtilities.dp(14.0f);
                        Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                        Theme.chat_systemDrawable.setBounds(r0.replyStartX - AndroidUtilities.dp(7.0f), r0.replyStartY - AndroidUtilities.dp(6.0f), (r0.replyStartX - AndroidUtilities.dp(7.0f)) + i2, r0.replyStartY + AndroidUtilities.dp(41.0f));
                        Theme.chat_systemDrawable.draw(canvas2);
                        if (r0.currentPosition != null) {
                        }
                        canvas2.drawRect((float) r0.replyStartX, (float) r0.replyStartY, (float) (r0.replyStartX + AndroidUtilities.dp(2.0f)), (float) (r0.replyStartY + AndroidUtilities.dp(35.0f)), Theme.chat_replyLinePaint);
                        if (r0.needReplyImage) {
                            r0.replyImageReceiver.setImageCoords(r0.replyStartX + AndroidUtilities.dp(10.0f), r0.replyStartY, AndroidUtilities.dp(35.0f), AndroidUtilities.dp(35.0f));
                            r0.replyImageReceiver.draw(canvas2);
                        }
                        if (r0.replyNameLayout != null) {
                            canvas.save();
                            if (r0.needReplyImage) {
                            }
                            canvas2.translate((((float) r0.replyStartX) - r0.replyNameOffset) + ((float) AndroidUtilities.dp((float) ((r0.needReplyImage ? 44 : 0) + 10))), (float) r0.replyStartY);
                            r0.replyNameLayout.draw(canvas2);
                            canvas.restore();
                        }
                        if (r0.replyTextLayout != null) {
                            canvas.save();
                            f4 = ((float) r0.replyStartX) - r0.replyTextOffset;
                            if (r0.needReplyImage) {
                                i = 44;
                            }
                            canvas2.translate(f4 + ((float) AndroidUtilities.dp((float) (10 + i))), (float) (r0.replyStartY + AndroidUtilities.dp(19.0f)));
                            r0.replyTextLayout.draw(canvas2);
                            canvas.restore();
                        }
                    }
                }

                public boolean hasCaptionLayout() {
                    return this.captionLayout != null;
                }

                public void drawCaptionLayout(Canvas canvas, boolean z) {
                    if (this.captionLayout != null) {
                        if (!z || this.pressedLink != null) {
                            canvas.save();
                            canvas.translate((float) this.captionX, (float) this.captionY);
                            if (this.pressedLink != null) {
                                for (int i = 0; i < this.urlPath.size(); i++) {
                                    canvas.drawPath((Path) this.urlPath.get(i), Theme.chat_urlPaint);
                                }
                            }
                            if (!z) {
                                try {
                                    this.captionLayout.draw(canvas);
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                            canvas.restore();
                        }
                    }
                }

                public void drawTimeLayout(Canvas canvas) {
                    Canvas canvas2 = canvas;
                    if (((this.drawTime && !r0.groupPhotoInvisible) || !r0.mediaBackground || r0.captionLayout != null) && r0.timeLayout != null) {
                        int i;
                        Drawable drawable;
                        int alpha;
                        int dp;
                        if (r0.currentMessageObject.type == 5) {
                            Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
                        } else if (r0.mediaBackground && r0.captionLayout == null) {
                            if (r0.currentMessageObject.type != 13) {
                                if (r0.currentMessageObject.type != 5) {
                                    Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeText));
                                }
                            }
                            Theme.chat_timePaint.setColor(Theme.getColor(Theme.key_chat_serviceText));
                        } else if (r0.currentMessageObject.isOutOwner()) {
                            Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
                        } else {
                            Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
                        }
                        if (r0.drawPinnedBottom) {
                            canvas2.translate(0.0f, (float) AndroidUtilities.dp(2.0f));
                        }
                        int i2 = 0;
                        if (r0.mediaBackground && r0.captionLayout == null) {
                            Paint paint;
                            int alpha2;
                            int dp2;
                            int dp3;
                            if (r0.currentMessageObject.type != 13) {
                                if (r0.currentMessageObject.type != 5) {
                                    paint = Theme.chat_timeBackgroundPaint;
                                    alpha2 = paint.getAlpha();
                                    paint.setAlpha((int) (((float) alpha2) * r0.timeAlpha));
                                    Theme.chat_timePaint.setAlpha((int) (r0.timeAlpha * 255.0f));
                                    dp2 = r0.timeX - AndroidUtilities.dp(4.0f);
                                    dp3 = r0.layoutHeight - AndroidUtilities.dp(28.0f);
                                    r0.rect.set((float) dp2, (float) dp3, (float) ((dp2 + r0.timeWidth) + AndroidUtilities.dp((float) (8 + (r0.currentMessageObject.isOutOwner() ? 20 : 0)))), (float) (dp3 + AndroidUtilities.dp(17.0f)));
                                    canvas2.drawRoundRect(r0.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint);
                                    paint.setAlpha(alpha2);
                                    i = (int) (-r0.timeLayout.getLineLeft(0));
                                    if ((r0.currentMessageObject.messageOwner.flags & 1024) != 0) {
                                        i += (int) (((float) r0.timeWidth) - r0.timeLayout.getLineWidth(0));
                                        if (r0.currentMessageObject.isSending()) {
                                            if (r0.currentMessageObject.isSendError()) {
                                                if (r0.currentMessageObject.type != 13) {
                                                    if (r0.currentMessageObject.type == 5) {
                                                        drawable = Theme.chat_msgMediaViewsDrawable;
                                                        alpha = ((BitmapDrawable) drawable).getPaint().getAlpha();
                                                        drawable.setAlpha((int) (r0.timeAlpha * ((float) alpha)));
                                                        BaseCell.setDrawableBounds(drawable, r0.timeX, (r0.layoutHeight - AndroidUtilities.dp(10.5f)) - r0.timeLayout.getHeight());
                                                        drawable.draw(canvas2);
                                                        drawable.setAlpha(alpha);
                                                        if (r0.viewsLayout != null) {
                                                            canvas.save();
                                                            canvas2.translate((float) ((r0.timeX + drawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((r0.layoutHeight - AndroidUtilities.dp(12.3f)) - r0.timeLayout.getHeight()));
                                                            r0.viewsLayout.draw(canvas2);
                                                            canvas.restore();
                                                        }
                                                    }
                                                }
                                                drawable = Theme.chat_msgStickerViewsDrawable;
                                                alpha = ((BitmapDrawable) drawable).getPaint().getAlpha();
                                                drawable.setAlpha((int) (r0.timeAlpha * ((float) alpha)));
                                                BaseCell.setDrawableBounds(drawable, r0.timeX, (r0.layoutHeight - AndroidUtilities.dp(10.5f)) - r0.timeLayout.getHeight());
                                                drawable.draw(canvas2);
                                                drawable.setAlpha(alpha);
                                                if (r0.viewsLayout != null) {
                                                    canvas.save();
                                                    canvas2.translate((float) ((r0.timeX + drawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((r0.layoutHeight - AndroidUtilities.dp(12.3f)) - r0.timeLayout.getHeight()));
                                                    r0.viewsLayout.draw(canvas2);
                                                    canvas.restore();
                                                }
                                            } else if (!r0.currentMessageObject.isOutOwner()) {
                                                dp = r0.timeX + AndroidUtilities.dp(11.0f);
                                                alpha = r0.layoutHeight - AndroidUtilities.dp(27.5f);
                                                r0.rect.set((float) dp, (float) alpha, (float) (AndroidUtilities.dp(14.0f) + dp), (float) (AndroidUtilities.dp(14.0f) + alpha));
                                                canvas2.drawRoundRect(r0.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                                                BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, dp + AndroidUtilities.dp(6.0f), alpha + AndroidUtilities.dp(2.0f));
                                                Theme.chat_msgErrorDrawable.draw(canvas2);
                                            }
                                        } else if (!r0.currentMessageObject.isOutOwner()) {
                                            BaseCell.setDrawableBounds(Theme.chat_msgMediaClockDrawable, r0.timeX + AndroidUtilities.dp(11.0f), (r0.layoutHeight - AndroidUtilities.dp(14.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                                            Theme.chat_msgMediaClockDrawable.draw(canvas2);
                                        }
                                    }
                                    canvas.save();
                                    canvas2.translate((float) (r0.timeX + i), (float) ((r0.layoutHeight - AndroidUtilities.dp(12.3f)) - r0.timeLayout.getHeight()));
                                    r0.timeLayout.draw(canvas2);
                                    canvas.restore();
                                    Theme.chat_timePaint.setAlpha(255);
                                }
                            }
                            paint = Theme.chat_actionBackgroundPaint;
                            alpha2 = paint.getAlpha();
                            paint.setAlpha((int) (((float) alpha2) * r0.timeAlpha));
                            Theme.chat_timePaint.setAlpha((int) (r0.timeAlpha * 255.0f));
                            dp2 = r0.timeX - AndroidUtilities.dp(4.0f);
                            dp3 = r0.layoutHeight - AndroidUtilities.dp(28.0f);
                            if (r0.currentMessageObject.isOutOwner()) {
                            }
                            r0.rect.set((float) dp2, (float) dp3, (float) ((dp2 + r0.timeWidth) + AndroidUtilities.dp((float) (8 + (r0.currentMessageObject.isOutOwner() ? 20 : 0)))), (float) (dp3 + AndroidUtilities.dp(17.0f)));
                            canvas2.drawRoundRect(r0.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint);
                            paint.setAlpha(alpha2);
                            i = (int) (-r0.timeLayout.getLineLeft(0));
                            if ((r0.currentMessageObject.messageOwner.flags & 1024) != 0) {
                                i += (int) (((float) r0.timeWidth) - r0.timeLayout.getLineWidth(0));
                                if (r0.currentMessageObject.isSending()) {
                                    if (r0.currentMessageObject.isSendError()) {
                                        if (r0.currentMessageObject.type != 13) {
                                            if (r0.currentMessageObject.type == 5) {
                                                drawable = Theme.chat_msgMediaViewsDrawable;
                                                alpha = ((BitmapDrawable) drawable).getPaint().getAlpha();
                                                drawable.setAlpha((int) (r0.timeAlpha * ((float) alpha)));
                                                BaseCell.setDrawableBounds(drawable, r0.timeX, (r0.layoutHeight - AndroidUtilities.dp(10.5f)) - r0.timeLayout.getHeight());
                                                drawable.draw(canvas2);
                                                drawable.setAlpha(alpha);
                                                if (r0.viewsLayout != null) {
                                                    canvas.save();
                                                    canvas2.translate((float) ((r0.timeX + drawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((r0.layoutHeight - AndroidUtilities.dp(12.3f)) - r0.timeLayout.getHeight()));
                                                    r0.viewsLayout.draw(canvas2);
                                                    canvas.restore();
                                                }
                                            }
                                        }
                                        drawable = Theme.chat_msgStickerViewsDrawable;
                                        alpha = ((BitmapDrawable) drawable).getPaint().getAlpha();
                                        drawable.setAlpha((int) (r0.timeAlpha * ((float) alpha)));
                                        BaseCell.setDrawableBounds(drawable, r0.timeX, (r0.layoutHeight - AndroidUtilities.dp(10.5f)) - r0.timeLayout.getHeight());
                                        drawable.draw(canvas2);
                                        drawable.setAlpha(alpha);
                                        if (r0.viewsLayout != null) {
                                            canvas.save();
                                            canvas2.translate((float) ((r0.timeX + drawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((r0.layoutHeight - AndroidUtilities.dp(12.3f)) - r0.timeLayout.getHeight()));
                                            r0.viewsLayout.draw(canvas2);
                                            canvas.restore();
                                        }
                                    } else if (r0.currentMessageObject.isOutOwner()) {
                                        dp = r0.timeX + AndroidUtilities.dp(11.0f);
                                        alpha = r0.layoutHeight - AndroidUtilities.dp(27.5f);
                                        r0.rect.set((float) dp, (float) alpha, (float) (AndroidUtilities.dp(14.0f) + dp), (float) (AndroidUtilities.dp(14.0f) + alpha));
                                        canvas2.drawRoundRect(r0.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                                        BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, dp + AndroidUtilities.dp(6.0f), alpha + AndroidUtilities.dp(2.0f));
                                        Theme.chat_msgErrorDrawable.draw(canvas2);
                                    }
                                } else if (r0.currentMessageObject.isOutOwner()) {
                                    BaseCell.setDrawableBounds(Theme.chat_msgMediaClockDrawable, r0.timeX + AndroidUtilities.dp(11.0f), (r0.layoutHeight - AndroidUtilities.dp(14.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                                    Theme.chat_msgMediaClockDrawable.draw(canvas2);
                                }
                            }
                            canvas.save();
                            canvas2.translate((float) (r0.timeX + i), (float) ((r0.layoutHeight - AndroidUtilities.dp(12.3f)) - r0.timeLayout.getHeight()));
                            r0.timeLayout.draw(canvas2);
                            canvas.restore();
                            Theme.chat_timePaint.setAlpha(255);
                        } else {
                            i = (int) (-r0.timeLayout.getLineLeft(0));
                            if ((r0.currentMessageObject.messageOwner.flags & 1024) != 0) {
                                i += (int) (((float) r0.timeWidth) - r0.timeLayout.getLineWidth(0));
                                if (r0.currentMessageObject.isSending()) {
                                    if (!r0.currentMessageObject.isOutOwner()) {
                                        drawable = isDrawSelectedBackground() ? Theme.chat_msgInSelectedClockDrawable : Theme.chat_msgInClockDrawable;
                                        BaseCell.setDrawableBounds(drawable, r0.timeX + AndroidUtilities.dp(11.0f), (r0.layoutHeight - AndroidUtilities.dp(8.5f)) - drawable.getIntrinsicHeight());
                                        drawable.draw(canvas2);
                                    }
                                } else if (!r0.currentMessageObject.isSendError()) {
                                    if (r0.currentMessageObject.isOutOwner()) {
                                        drawable = isDrawSelectedBackground() ? Theme.chat_msgOutViewsSelectedDrawable : Theme.chat_msgOutViewsDrawable;
                                        BaseCell.setDrawableBounds(drawable, r0.timeX, (r0.layoutHeight - AndroidUtilities.dp(4.5f)) - r0.timeLayout.getHeight());
                                        drawable.draw(canvas2);
                                    } else {
                                        drawable = isDrawSelectedBackground() ? Theme.chat_msgInViewsSelectedDrawable : Theme.chat_msgInViewsDrawable;
                                        BaseCell.setDrawableBounds(drawable, r0.timeX, (r0.layoutHeight - AndroidUtilities.dp(4.5f)) - r0.timeLayout.getHeight());
                                        drawable.draw(canvas2);
                                    }
                                    if (r0.viewsLayout != null) {
                                        canvas.save();
                                        canvas2.translate((float) ((r0.timeX + Theme.chat_msgInViewsDrawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((r0.layoutHeight - AndroidUtilities.dp(6.5f)) - r0.timeLayout.getHeight()));
                                        r0.viewsLayout.draw(canvas2);
                                        canvas.restore();
                                    }
                                } else if (!r0.currentMessageObject.isOutOwner()) {
                                    dp = r0.timeX + AndroidUtilities.dp(11.0f);
                                    alpha = r0.layoutHeight - AndroidUtilities.dp(20.5f);
                                    r0.rect.set((float) dp, (float) alpha, (float) (AndroidUtilities.dp(14.0f) + dp), (float) (AndroidUtilities.dp(14.0f) + alpha));
                                    canvas2.drawRoundRect(r0.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                                    BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, dp + AndroidUtilities.dp(6.0f), alpha + AndroidUtilities.dp(2.0f));
                                    Theme.chat_msgErrorDrawable.draw(canvas2);
                                }
                            }
                            canvas.save();
                            canvas2.translate((float) (r0.timeX + i), (float) ((r0.layoutHeight - AndroidUtilities.dp(6.5f)) - r0.timeLayout.getHeight()));
                            r0.timeLayout.draw(canvas2);
                            canvas.restore();
                        }
                        if (r0.currentMessageObject.isOutOwner()) {
                            int i3;
                            dp = 1;
                            i = ((int) (r0.currentMessageObject.getDialogId() >> 32)) == 1 ? 1 : 0;
                            if (r0.currentMessageObject.isSending()) {
                                alpha = 0;
                                i3 = alpha;
                                i2 = 1;
                                dp = i3;
                            } else if (r0.currentMessageObject.isSendError()) {
                                i3 = 1;
                                dp = 0;
                                alpha = dp;
                            } else if (r0.currentMessageObject.isSent()) {
                                alpha = !r0.currentMessageObject.isUnread() ? 1 : 0;
                                i3 = 0;
                            } else {
                                dp = 0;
                                alpha = dp;
                                i3 = alpha;
                            }
                            if (i2 != 0) {
                                if (r0.mediaBackground && r0.captionLayout == null) {
                                    if (r0.currentMessageObject.type != 13) {
                                        if (r0.currentMessageObject.type != 5) {
                                            BaseCell.setDrawableBounds(Theme.chat_msgMediaClockDrawable, (r0.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                                            Theme.chat_msgMediaClockDrawable.draw(canvas2);
                                        }
                                    }
                                    BaseCell.setDrawableBounds(Theme.chat_msgStickerClockDrawable, (r0.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicHeight());
                                    Theme.chat_msgStickerClockDrawable.draw(canvas2);
                                } else {
                                    BaseCell.setDrawableBounds(Theme.chat_msgOutClockDrawable, (r0.layoutWidth - AndroidUtilities.dp(18.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(8.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicHeight());
                                    Theme.chat_msgOutClockDrawable.draw(canvas2);
                                }
                            }
                            if (i == 0) {
                                if (dp != 0) {
                                    if (r0.mediaBackground && r0.captionLayout == null) {
                                        if (r0.currentMessageObject.type != 13) {
                                            if (r0.currentMessageObject.type != 5) {
                                                if (alpha != 0) {
                                                    BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (r0.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                                                } else {
                                                    BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (r0.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                                                }
                                                Theme.chat_msgMediaCheckDrawable.setAlpha((int) (r0.timeAlpha * 255.0f));
                                                Theme.chat_msgMediaCheckDrawable.draw(canvas2);
                                                Theme.chat_msgMediaCheckDrawable.setAlpha(255);
                                            }
                                        }
                                        if (alpha != 0) {
                                            BaseCell.setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (r0.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                                        } else {
                                            BaseCell.setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (r0.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                                        }
                                        Theme.chat_msgStickerCheckDrawable.draw(canvas2);
                                    } else {
                                        drawable = isDrawSelectedBackground() ? Theme.chat_msgOutCheckSelectedDrawable : Theme.chat_msgOutCheckDrawable;
                                        if (alpha != 0) {
                                            BaseCell.setDrawableBounds(drawable, (r0.layoutWidth - AndroidUtilities.dp(22.5f)) - drawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable.getIntrinsicHeight());
                                        } else {
                                            BaseCell.setDrawableBounds(drawable, (r0.layoutWidth - AndroidUtilities.dp(18.5f)) - drawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable.getIntrinsicHeight());
                                        }
                                        drawable.draw(canvas2);
                                    }
                                }
                                if (alpha != 0) {
                                    if (r0.mediaBackground && r0.captionLayout == null) {
                                        if (r0.currentMessageObject.type != 13) {
                                            if (r0.currentMessageObject.type != 5) {
                                                BaseCell.setDrawableBounds(Theme.chat_msgMediaHalfCheckDrawable, (r0.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicHeight());
                                                Theme.chat_msgMediaHalfCheckDrawable.setAlpha((int) (255.0f * r0.timeAlpha));
                                                Theme.chat_msgMediaHalfCheckDrawable.draw(canvas2);
                                                Theme.chat_msgMediaHalfCheckDrawable.setAlpha(255);
                                            }
                                        }
                                        BaseCell.setDrawableBounds(Theme.chat_msgStickerHalfCheckDrawable, (r0.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicHeight());
                                        Theme.chat_msgStickerHalfCheckDrawable.draw(canvas2);
                                    } else {
                                        Drawable drawable2 = isDrawSelectedBackground() ? Theme.chat_msgOutHalfCheckSelectedDrawable : Theme.chat_msgOutHalfCheckDrawable;
                                        BaseCell.setDrawableBounds(drawable2, (r0.layoutWidth - AndroidUtilities.dp(18.0f)) - drawable2.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable2.getIntrinsicHeight());
                                        drawable2.draw(canvas2);
                                    }
                                }
                            } else if (!(alpha == 0 && dp == 0)) {
                                if (r0.mediaBackground && r0.captionLayout == null) {
                                    BaseCell.setDrawableBounds(Theme.chat_msgBroadcastMediaDrawable, (r0.layoutWidth - AndroidUtilities.dp(24.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(14.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicHeight());
                                    Theme.chat_msgBroadcastMediaDrawable.draw(canvas2);
                                } else {
                                    BaseCell.setDrawableBounds(Theme.chat_msgBroadcastDrawable, (r0.layoutWidth - AndroidUtilities.dp(20.5f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicWidth(), (r0.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicHeight());
                                    Theme.chat_msgBroadcastDrawable.draw(canvas2);
                                }
                            }
                            if (i3 != 0) {
                                if (r0.mediaBackground && r0.captionLayout == null) {
                                    i = r0.layoutWidth - AndroidUtilities.dp(34.5f);
                                    dp = r0.layoutHeight - AndroidUtilities.dp(26.5f);
                                } else {
                                    i = r0.layoutWidth - AndroidUtilities.dp(32.0f);
                                    dp = r0.layoutHeight - AndroidUtilities.dp(21.0f);
                                }
                                r0.rect.set((float) i, (float) dp, (float) (AndroidUtilities.dp(14.0f) + i), (float) (AndroidUtilities.dp(14.0f) + dp));
                                canvas2.drawRoundRect(r0.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                                BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, i + AndroidUtilities.dp(6.0f), dp + AndroidUtilities.dp(2.0f));
                                Theme.chat_msgErrorDrawable.draw(canvas2);
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
